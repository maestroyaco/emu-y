package variables;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import estaticos.Emu;
import estaticos.GestorSQL;
import estaticos.GestorSalida;
import estaticos.MundoDofus;
import estaticos.MundoDofus.Duo;
import servidor.Colores;
import variables.Objeto.ObjetoModelo;

public class Mercadillo {
	private int _mapaMercadillo;
	private float _porcMercadillo;
	private short _tiempoVenta;
	private short _maxObjCuenta;
	private String _tipoObjPermitidos;
	private short _nivelMax;
	private Map<Integer, TipoObjetos> _tipoObjetos = new HashMap<>();
	private Map<Integer, Duo<Integer, Integer>> _lineas = new HashMap<>();
	private DecimalFormat _porcentaje = new DecimalFormat("0.0");

	public Mercadillo(int mercadilloID, float tasa, short tiempoVenta, short maxObjCuenta, short nivelMax,
			String tipoObj) {
		_mapaMercadillo = mercadilloID;
		_porcMercadillo = tasa;
		_maxObjCuenta = maxObjCuenta;
		_tipoObjPermitidos = tipoObj;
		_nivelMax = nivelMax;
		for (String tipo : tipoObj.split(",")) {
			if (tipo.equals("")) {
				continue;
			}
			int tipoID = Integer.parseInt(tipo);
			_tipoObjetos.put(tipoID, new TipoObjetos());
		}
	}

	public int getMapaMercadillo() {
		return _mapaMercadillo;
	}

	public float getPorcentaje() {
		return _porcMercadillo;
	}

	public short getTiempoVenta() {
		return _tiempoVenta;
	}

	public short getMaxObjCuenta() {
		return _maxObjCuenta;
	}

	public String getTipoObjPermitidos() {
		return _tipoObjPermitidos;
	}

	public short getNivelMax() {
		return _nivelMax;
	}

	public String analizarParaEHl(int modeloID) {
		int tipo = MundoDofus.getObjModelo(modeloID).getTipo();
		return _tipoObjetos.get(tipo).getModelo(modeloID).strLineasPorObjMod();
	}

	public String stringModelo(int tipoObj) {
		if (_tipoObjetos.get(tipoObj) == null) {
			return "";
		}
		return _tipoObjetos.get(tipoObj).stringModelo();
	}

	public String porcentajeImpuesto() {
		return _porcentaje.format(_porcMercadillo).replace(",", ".");
	}

	public LineaMercadillo getLinea(int lineaID) {
		try {
			int tipoObj = _lineas.get(lineaID)._primero;
			int idModelo = _lineas.get(lineaID)._segundo;
			return _tipoObjetos.get(tipoObj).getModelo(idModelo).getLinea(lineaID);
		} catch (NullPointerException e) {
			e.printStackTrace();
			return null;
		}
	}

	public ArrayList<ObjetoMercadillo> todoListaObjMercaDeUnPuesto() {
		ArrayList<ObjetoMercadillo> listaObjMerca = new ArrayList<>();
		for (TipoObjetos tipo : _tipoObjetos.values()) {
			listaObjMerca.addAll(tipo.todoListaObjMercaDeUnTipo());
		}
		return listaObjMerca;
	}

	public void addObjMercaAlPuesto(ObjetoMercadillo objMerca) {
		if (objMerca.getObjeto() == null) {
			return;
		}
		ObjetoModelo objModelo = objMerca.getObjeto().getModelo();
		int tipoObj = objModelo.getTipo();
		int idModelo = objModelo.getID();
		if (_tipoObjetos.get(tipoObj) == null) {
			return;
		}
		objMerca.setIDPuesto(_mapaMercadillo);
		_tipoObjetos.get(tipoObj).addModeloVerificacion(objMerca);
		_lineas.put(objMerca.getLineaID(), new Duo<>(tipoObj, idModelo));
		MundoDofus.addObjMercadillo(objMerca.getDueÑo(), _mapaMercadillo, objMerca);
	}

	boolean borrarObjMercaDelPuesto(ObjetoMercadillo objMerca, Personaje perso) {
		Objeto objeto = objMerca.getObjeto();
		if (objeto == null) {
			return false;
		}
		boolean borrable = _tipoObjetos.get(objeto.getModelo().getTipo()).borrarObjMercaDeModelo(objMerca, perso, this);
		if (borrable) {
			MundoDofus.borrarObjMercadillo(objMerca.getDueÑo(), objMerca.getIDDelPuesto(), objMerca);
		}
		return borrable;
	}

	public synchronized boolean comprarObjeto(int lineaID, int cant, int precio, Personaje nuevoDueÑo,
			Mercadillo curHdv) {
		boolean posible = true;
		try {
			LineaMercadillo linea = getLinea(lineaID);
			if (linea == null) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(nuevoDueÑo, "El objeto estÁ bugeado.", Colores.ROJO);
				return false;
			}
			ObjetoMercadillo objAComprar = linea.tuTienes(cant, precio);
			Cuenta cuenta = MundoDofus.getCuenta(objAComprar.getDueÑo());
			if (cuenta == null) {
				GestorSQL.CARGAR_CUENTA_POR_ID(objAComprar.getDueÑo());
			}
			if (cuenta == null) {
				return false;
			}
			if (nuevoDueÑo.getCuentaID() == cuenta.getID()) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(nuevoDueÑo, "No puedes comprar tus propios items.", Colores.ROJO);
				return false;
			}
			/*
			 * if (nuevoDueÑo.mercadeando == 547) { int puntosdueo =
			 * GestorSQL.getPuntosCuenta(nuevoDueÑo.getCuentaID()); if (puntosdueo < precio)
			 * { GestorSalida.ENVIAR_cs_CHAT_MENSAJE(nuevoDueÑo,
			 * "No tienes suficientes Puntos VIP para comprar esto.", Colores.ROJO); return
			 * false; } GestorSQL.setPuntoCuenta(puntosdueo - precio,
			 * nuevoDueÑo.getCuentaID()); int darogr = precio;
			 * GestorSQL.setPuntoCuenta(GestorSQL.getPuntosCuenta(cuenta.getID()) + darogr,
			 * cuenta.getID()); } else {
			 */
			if (nuevoDueÑo.getKamas() < precio) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(nuevoDueÑo, "No tienes suficientes Kamas para comprar esto.",
						Colores.ROJO);
				return false;
			}
			nuevoDueÑo.addKamas(precio * -1);
			if (objAComprar.getDueÑo() != -1) {
				if (cuenta != null) {
					cuenta.setKamasBanco(cuenta.getKamasBanco() + objAComprar.getPrecio());
				}
			}
			// }
			GestorSalida.ENVIAR_As_STATS_DEL_PJ(nuevoDueÑo);
			Objeto objeto = objAComprar.getObjeto();
			if (nuevoDueÑo.addObjetoSimilar(objeto, true, -1)) {
				MundoDofus.eliminarObjeto(objeto.getID());
			} else {
				nuevoDueÑo.addObjetoPut(objeto);
				GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(nuevoDueÑo, objeto);
			}
			if (curHdv.getLinea(lineaID) != null) {
				if (!curHdv.getLinea(lineaID).categoriaVacia()) {
					GestorSalida.ENVIAR_EHm_DETALLE_LINEA_CON_PRECIOS(nuevoDueÑo, "+",
							curHdv.getLinea(lineaID).analizarParaEHm());
				}
			}
			objeto.getModelo().nuevoPrecio(objAComprar.getTipoCantidad(true), precio);
			borrarObjMercaDelPuesto(objAComprar, nuevoDueÑo);
			if ((MundoDofus.getCuenta(objAComprar.getDueÑo()) != null)
					&& (MundoDofus.getCuenta(objAComprar.getDueÑo()).getTempPersonaje() != null)) {
				GestorSalida.ENVIAR_Im_INFORMACION(MundoDofus.getCuenta(objAComprar.getDueÑo()).getTempPersonaje(),
						"065;" + precio + "~" + objeto.getIDModelo() + "~" + objeto.getIDModelo() + "~1");
			}
			if (objAComprar.getDueÑo() == -1) {
				GestorSQL.SALVAR_OBJETO(objeto);
			}
			objAComprar = null;
		} catch (Exception e) {
			Emu.creaLogs(e);
			posible = false;
		}
		return posible;
	}

	private class LineaMercadillo {
		private int _lineaID;
		private ArrayList<ArrayList<ObjetoMercadillo>> _categorias = new ArrayList<>(3);
		private String _strStats;
		private int _modeloID;

		private LineaMercadillo(final int lineaID, final ObjetoMercadillo objMerca) {
			_lineaID = lineaID;
			final Objeto objeto = objMerca.getObjeto();
			_strStats = objeto.convertirStatsAString(0);
			_modeloID = objeto.getIDModelo();
			for (int i = 0; i < 3; i++) {
				_categorias.add(new ArrayList<>());
			}
			final int categoria = objMerca.getTipoCantidad(false) - 1;
			_categorias.get(categoria).add(objMerca);
			ordenar((byte) categoria);
			objMerca.setLineaID(_lineaID);
		}

		private boolean tieneIgual(final ObjetoMercadillo objMerca) {
			if (!categoriaVacia() && !tieneMismoStats(objMerca)) {
				return false;
			}
			objMerca.setLineaID(_lineaID);
			final int categoria = objMerca.getTipoCantidad(false) - 1;
			_categorias.get(categoria).add(objMerca);
			ordenar((byte) categoria);
			return true;
		}

		private boolean tieneMismoStats(ObjetoMercadillo objMerca) {
			Objeto objeto = objMerca.getObjeto();
			return (_strStats.equalsIgnoreCase(objeto.convertirStatsAString(0)))
					&& (objeto.getModelo().getTipo() != 85);
		}

		private ObjetoMercadillo tuTienes(final int categoria, final long precio) {
			final int index = categoria - 1;
			for (ObjetoMercadillo element : _categorias.get(index)) {
				if (element.getPrecio() == precio) {
					return element;
				}
			}
			return null;
		}

		public long[] getLos3PreciosPorLinea() {
			final long[] str = new long[3];
			for (int i = 0; i < _categorias.size(); i++) {
				try {
					str[i] = _categorias.get(i).get(0).getPrecio();
				} catch (final IndexOutOfBoundsException e) {
					str[i] = 0;
				}
			}
			return str;
		}

		private ArrayList<ObjetoMercadillo> todosObjMercaDeUnaLinea() {
			int totalEntradas = (_categorias.get(0)).size() + (_categorias.get(1)).size() + (_categorias.get(2)).size();
			ArrayList<ObjetoMercadillo> todosObjMerca = new ArrayList<>(totalEntradas);
			for (int cat = 0; cat < _categorias.size(); cat++) {
				todosObjMerca.addAll(_categorias.get(cat));
			}
			return todosObjMerca;
		}

		private boolean borrarObjMercaDeLinea(final ObjetoMercadillo objMerca) {
			final int categoria = objMerca.getTipoCantidad(false) - 1;// 1, 10 ,100
			final boolean borrable = _categorias.get(categoria).remove(objMerca);
			ordenar((byte) categoria);
			return borrable;
		}

		private String strListaDeLineasDeModelo() {
			String aRetornar = "";
			long[] precio = getLos3PreciosPorLinea();
			aRetornar = aRetornar + _lineaID + ";" + _strStats + ";" + (precio[0] == 0 ? "" : precio[0]) + ";"
					+ (precio[1] == 0 ? "" : precio[1]) + ";" + (precio[2] == 0 ? "" : precio[2]);
			return aRetornar;
		}

		private String analizarParaEHm() {
			long[] precio = getLos3PreciosPorLinea();
			String aRetornar = _lineaID + "|" + _modeloID + "|" + _strStats + "|" + (precio[0] == 0 ? "" : precio[0])
					+ "|" + (precio[1] == 0 ? "" : precio[1]) + "|" + (precio[2] == 0 ? "" : precio[2]);
			return aRetornar;
		}

		private void ordenar(byte categoria) {
			Collections.sort(_categorias.get(categoria));
		}

		private boolean categoriaVacia() {
			for (int i = 0; i < _categorias.size();) {
				try {
					if (_categorias.get(i).get(0) != null) {
						return false;
					}
				} catch (Exception e) {
					i++;
				}
			}
			return true;
		}
	}

	private class Modelo {
		int _modeloID;
		Map<Integer, LineaMercadillo> _lineasDeUnModelo = new HashMap<>();

		public Modelo(int modeloID, ObjetoMercadillo objMercadillo) {
			_modeloID = modeloID;
			addObjMercaConLinea(objMercadillo);
		}

		public void addObjMercaConLinea(ObjetoMercadillo objMerca) {
			for (LineaMercadillo linea : _lineasDeUnModelo.values()) {
				if (linea.tieneIgual(objMerca)) {
					return;
				}
			}
			int lineaID = MundoDofus.sigIDLineaMercadillo();
			_lineasDeUnModelo.put(lineaID, new LineaMercadillo(lineaID, objMerca));
		}

		public LineaMercadillo getLinea(int lineaID) {
			return _lineasDeUnModelo.get(lineaID);
		}

		public boolean borrarObjMercaDeUnaLinea(ObjetoMercadillo objMerca, Personaje perso, Mercadillo puesto) {
			boolean borrable = _lineasDeUnModelo.get(objMerca.getLineaID()).borrarObjMercaDeLinea(objMerca);
			if (_lineasDeUnModelo.get(objMerca.getLineaID()).categoriaVacia()) {
				_lineasDeUnModelo.remove(objMerca.getLineaID());
				puesto.borrarPath(objMerca.getLineaID());
				GestorSalida.ENVIAR_EHm_DETALLE_LINEA_CON_PRECIOS(perso, "-", objMerca.getLineaID() + "");
			} else {
				GestorSalida.ENVIAR_EHm_DETALLE_LINEA_CON_PRECIOS(perso, "+",
						_lineasDeUnModelo.get(objMerca.getLineaID()).analizarParaEHm());
			}
			return borrable;
		}

		public ArrayList<ObjetoMercadillo> todosObjMercaDeUnModelo() {
			ArrayList<ObjetoMercadillo> listaObj = new ArrayList<>();
			for (LineaMercadillo linea : _lineasDeUnModelo.values()) {
				listaObj.addAll(linea.todosObjMercaDeUnaLinea());
			}
			return listaObj;
		}

		public String strLineasPorObjMod() {
			final StringBuilder str = new StringBuilder();
			for (final LineaMercadillo linea : _lineasDeUnModelo.values()) {
				if (str.length() > 0) {
					str.append("|");
				}
				str.append(linea.strListaDeLineasDeModelo());
			}
			return _modeloID + "|" + str.toString();
		}

		public boolean estaVacio() {
			return _lineasDeUnModelo.size() == 0;
		}
	}

	public static class ObjetoMercadillo implements Comparable<ObjetoMercadillo> {
		private int _idDelPuesto;
		private int _precio;
		private int _tipoCantidad;
		private Objeto _objeto;
		private int _lineaID;
		private int _dueÑo;

		public ObjetoMercadillo(int precio, int cant, int dueÑo, Objeto obj) {
			_precio = precio;
			_tipoCantidad = cant;
			_objeto = obj;
			_dueÑo = dueÑo;
		}

		public void setIDPuesto(int id) {
			_idDelPuesto = id;
		}

		public int getIDDelPuesto() {
			return _idDelPuesto;
		}

		public int getPrecio() {
			return _precio;
		}

		public int getTipoCantidad(boolean cantidadReal) {
			if (cantidadReal) {
				return (int) (Math.pow(10.0D, _tipoCantidad) / 10.0D);
			}
			return _tipoCantidad;
		}

		public Objeto getObjeto() {
			return _objeto;
		}

		public int getLineaID() {
			return _lineaID;
		}

		public void setLineaID(int ID) {
			_lineaID = ID;
		}

		public int getDueÑo() {
			return _dueÑo;
		}

		public String analizarParaEL() {
			int cantidad = getTipoCantidad(true);
			return _lineaID + ";" + cantidad + ";" + _objeto.getIDModelo() + ";" + _objeto.convertirStatsAString(0)
					+ ";" + _precio + ";350";
		}

		public String analizarParaEmK() {
			int cantidad = getTipoCantidad(true);
			return _objeto.getID() + "|" + cantidad + "|" + _objeto.getIDModelo() + "|"
					+ _objeto.convertirStatsAString(0) + "|" + _precio + "|350";
		}

		public String analizarObjeto(char separador) {
			int cantidad = getTipoCantidad(true);
			return _lineaID + separador + cantidad + separador + _objeto.getIDModelo() + separador
					+ _objeto.convertirStatsAString(0) + separador + _precio + separador + "350";
		}

		@Override
		public int compareTo(ObjetoMercadillo objMercadillo) {
			int otroPrecio = objMercadillo.getPrecio();
			if (otroPrecio > _precio) {
				return -1;
			}
			if (otroPrecio == _precio) {
				return 0;
			}
			if (otroPrecio < _precio) {
				return 1;
			}
			return 0;
		}
	}

	private class TipoObjetos {
		Map<Integer, Modelo> _modelosDeUnTipo = new HashMap<>();

		public void addModeloVerificacion(ObjetoMercadillo objMerca) {
			int modeloID = objMerca.getObjeto().getIDModelo();
			Modelo modelo = _modelosDeUnTipo.get(modeloID);
			if (modelo == null) {
				_modelosDeUnTipo.put(modeloID, new Modelo(modeloID, objMerca));
			} else {
				modelo.addObjMercaConLinea(objMerca);
			}
		}

		public boolean borrarObjMercaDeModelo(final ObjetoMercadillo objMerca, final Personaje perso,
				final Mercadillo puesto) {
			final int idModelo = objMerca.getObjeto().getIDModelo();
			final boolean borrable = _modelosDeUnTipo.get(idModelo).borrarObjMercaDeUnaLinea(objMerca, perso, puesto);
			if (_modelosDeUnTipo.get(idModelo).estaVacio()) {
				_modelosDeUnTipo.remove(idModelo);
				GestorSalida.GAME_SEND_EHM_PACKET(perso, "-", idModelo + "");
			}
			return borrable;
		}

		public Modelo getModelo(int modeloID) {
			return _modelosDeUnTipo.get(modeloID);
		}

		public ArrayList<ObjetoMercadillo> todoListaObjMercaDeUnTipo() {
			ArrayList<ObjetoMercadillo> listaObjMerca = new ArrayList<>();
			for (Modelo modelo : _modelosDeUnTipo.values()) {
				listaObjMerca.addAll(modelo.todosObjMercaDeUnModelo());
			}
			return listaObjMerca;
		}

		public String stringModelo() {
			boolean primero = true;
			StringBuilder str = new StringBuilder("");
			for (Entry<Integer, Modelo> modelos : _modelosDeUnTipo.entrySet()) {
				int curTemp = modelos.getKey();
				if (!primero) {
					str.append(";");
				}
				str.append(curTemp);
				primero = false;
			}
			return str.toString();
		}
	}

	public boolean esTipoDeEsteMercadillo(final int tipoObj) {
		return _tipoObjetos.get(tipoObj) != null;
	}

	private void borrarPath(final int linea) {
		_lineas.remove(linea);
	}

	public boolean hayModeloEnEsteMercadillo(final int tipo, final int modeloID) {
		return _tipoObjetos.get(tipo).getModelo(modeloID) != null;
	}

	public String strListaLineasPorModelo(final int modeloID) {
		try {
			final int tipo = MundoDofus.getObjModelo(modeloID).getTipo();
			return _tipoObjetos.get(tipo).getModelo(modeloID).strLineasPorObjMod();
		} catch (final Exception e) {
			return "";
		}
	}
}
package variables;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.Timer;

import estaticos.Camino;
import estaticos.CentroInfo;
import estaticos.Emu;
import estaticos.Encriptador;
import estaticos.Formulas;
import estaticos.GestorSQL;
import estaticos.GestorSalida;
import estaticos.MundoDofus;
import variables.Mapa.Cercado;
import variables.Objeto.ObjetoModelo;
import variables.Personaje.Stats;

public class Dragopavo {
	private int _id;
	private int _colorID;
	private int _sexo;
	private int _amor;
	private int _resistencia;
	private int _nivel;
	private long _experiencia;
	private String _nombre;
	private int _fatiga;
	private int _energia;
	private int _reprod;
	private int _madurez;
	private int _serenidad;
	private Stats _stats = new Stats();
	private String _ancestros = "?,?,?,?,?,?,?,?,?,?,?,?,?,?";
	private ArrayList<Objeto> _objetos = new ArrayList<>();
	private List<Integer> _capacidades = new ArrayList<>(2);
	private String _habilidad = ",";
	private int _celdaID;
	private int _dueÑo;
	private int _talla;
	private int _mapaID;
	private int _orientacion;
	private int _fecundadaHace;
	private int _pareja;
	private Timer _aumentarFecundo = new Timer(60000, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			_fecundadaHace++;
			startFecundo();
		}
	});
	private String _vip = "";

	public Dragopavo(int color, int dueÑo) {
		int sexo = Formulas.getRandomValor(0, 1);
		_id = MundoDofus.getSigIDMontura();
		_colorID = color;
		_sexo = sexo;
		_nivel = 5;
		_experiencia = 0;
		_nombre = "(Sin Nombre)";
		_fatiga = 0;
		_energia = getMaxEnergia();
		_reprod = 0;
		_madurez = getMaxMadurez();
		_serenidad = 0;
		_stats = _colorID == 75 ? CentroInfo.getStatsMonturaVIP(_vip, _nivel)
				: CentroInfo.getStatsMontura(_colorID, _nivel);
		_ancestros = "?,?,?,?,?,?,?,?,?,?,?,?,?,?";
		_habilidad = "0";
		_talla = 100;
		_dueÑo = dueÑo;
		_celdaID = -1;
		_mapaID = -1;
		_orientacion = 1;
		_fecundadaHace = -1;
		_pareja = -1;
		_vip = "";
		MundoDofus.addDragopavo(this);
		GestorSQL.CREAR_MONTURA(this);
	}

	public Dragopavo(int id, int color, int sexo, int amor, int resistencia, int nivel, long exp, String nombre,
			int fatiga, int energia, int reprod, int madurez, int serenidad, String objetos, String anc,
			String habilidad, int talla, int celda, short mapa, int dueÑo, int orientacion, int fecundable, int pareja,
			String vip) {
		_id = id;
		_colorID = color;
		_sexo = sexo;
		_amor = amor;
		_resistencia = resistencia;
		_nivel = nivel;
		_experiencia = exp;
		_nombre = nombre;
		_fatiga = fatiga;
		_energia = energia;
		_reprod = reprod;
		_madurez = madurez;
		_serenidad = serenidad;
		_ancestros = anc;
		_vip = vip;
		_stats = _colorID == 75 ? CentroInfo.getStatsMonturaVIP(_vip, _nivel)
				: CentroInfo.getStatsMontura(_colorID, _nivel);
		_habilidad = habilidad;
		_talla = talla;
		_celdaID = celda;
		_mapaID = mapa;
		_dueÑo = dueÑo;
		_orientacion = orientacion;
		_fecundadaHace = fecundable;
		_pareja = pareja;
		for (String s : habilidad.split(",", 2)) {
			if (s != null) {
				int a = Integer.parseInt(s);
				try {
					_capacidades.add(a);
				} catch (Exception e) {
				}
			}
		}
		for (String str : objetos.split(";")) {
			try {
				Objeto obj = MundoDofus.getObjeto(Integer.parseInt(str));
				if (obj != null) {
					_objetos.add(obj);
				}
			} catch (Exception e) {
				continue;
			}
		}
	}

	private void startFecundo() {
		if (_fecundadaHace < 120 && _fecundadaHace > 0) {
			_aumentarFecundo.restart();
		} else {
			_aumentarFecundo.stop();
		}
	}

	public int getID() {
		return _id;
	}

	public int getColor() {
		return _colorID;
	}

	public void setStatsVIP(String vip) {
		_vip = vip;
		_stats = CentroInfo.getStatsMonturaVIP(vip, _nivel);
	}

	public String getVIP() {
		return _vip;
	}

	public int getSexo() {
		return _sexo;
	}

	public int getAmor() {
		return _amor;
	}

	public String getAncestros() {
		return _ancestros;
	}

	public int getResistencia() {
		return _resistencia;
	}

	public int getPodsActuales() {
		int pods = 0;
		ArrayList<Objeto> aBorrar = new ArrayList<>();
		for (Objeto obj : _objetos) {
			if (obj == null) {
				continue;
			}
			if (obj.getModelo() == null) {
				aBorrar.add(obj);
				continue;
			}
			pods += (obj.getModelo().getPeso() * obj.getCantidad());
		}
		for (Objeto borra : aBorrar) {
			if (_objetos.contains(borra)) {
				_objetos.remove(borra);
			}
		}
		return pods;
	}

	public String getListaObjDragopavo() {
		StringBuilder objetos = new StringBuilder("");
		for (Objeto obj : _objetos) {
			objetos.append("O" + obj.stringObjetoConGuiÓn());
		}
		return objetos.toString();
	}

	public void addObjAMochila(int id, int cant, Personaje perso) {
		if (_objetos.size() >= 80) {
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "Llegaste al mÁximo de objetos que puede soportar esta montura",
					Emu.COLOR_MENSAJE);
			return;
		}
		Objeto objetoAgregar = MundoDofus.getObjeto(id);
		if ((objetoAgregar == null) || (objetoAgregar.getPosicion() != -1)) {
			return;
		}
		if (perso.getObjetos().get(id) == null) {
			GestorSalida.ENVIAR_BN_NADA(perso);
			return;
		}
		Objeto objIgualEnMochila = getSimilarObjeto(objetoAgregar);
		int nuevaCant = objetoAgregar.getCantidad() - cant;
		String str = "";
		if (objIgualEnMochila == null) {
			if (nuevaCant <= 0) {
				perso.borrarObjetoRemove(objetoAgregar.getID());
				_objetos.add(objetoAgregar);
				str = "O+" + objetoAgregar.getID() + "|" + objetoAgregar.getCantidad() + "|"
						+ objetoAgregar.getModelo().getID() + "|" + objetoAgregar.convertirStatsAString(0);
				GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(perso, id);
			} else {
				objetoAgregar.setCantidad(nuevaCant);
				objIgualEnMochila = Objeto.clonarObjeto(objetoAgregar, cant);
				MundoDofus.addObjeto(objIgualEnMochila, true);
				_objetos.add(objIgualEnMochila);
				str = "O+" + objIgualEnMochila.getID() + "|" + objIgualEnMochila.getCantidad() + "|"
						+ objIgualEnMochila.getModelo().getID() + "|" + objIgualEnMochila.convertirStatsAString(0);
				GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(perso, objetoAgregar);
			}
		} else {
			if (nuevaCant <= 0) {
				perso.borrarObjetoRemove(objetoAgregar.getID());
				objIgualEnMochila.setCantidad(objIgualEnMochila.getCantidad() + objetoAgregar.getCantidad());
				str = "O+" + objIgualEnMochila.getID() + "|" + objIgualEnMochila.getCantidad() + "|"
						+ objIgualEnMochila.getModelo().getID() + "|" + objIgualEnMochila.convertirStatsAString(0);
				MundoDofus.eliminarObjeto(objetoAgregar.getID());
				GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(perso, id);
			} else {
				objetoAgregar.setCantidad(nuevaCant);
				objIgualEnMochila.setCantidad(objIgualEnMochila.getCantidad() + cant);
				str = "O+" + objIgualEnMochila.getID() + "|" + objIgualEnMochila.getCantidad() + "|"
						+ objIgualEnMochila.getModelo().getID() + "|" + objIgualEnMochila.convertirStatsAString(0);
				GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(perso, objetoAgregar);
			}
		}
		GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(perso, str);
		GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(perso);
		GestorSalida.ENVIAR_Ew_PODS_MONTURA(perso, getPodsActuales());
		GestorSQL.ACTUALIZAR_MONTURA(this, false);
	}

	private Objeto getSimilarObjeto(Objeto obj) {
		for (Objeto value : _objetos) {
			ObjetoModelo objetoMod = value.getModelo();
			if (objetoMod.getTipo() == 85) {
				continue;
			}
			if (objetoMod.getID() == obj.getModelo().getID() && value.getStats().sonStatsIguales(obj.getStats())) {
				return value;
			}
		}
		return null;
	}

	public void removerDeLaMochila(int id, int cant, Personaje perso) {
		Objeto objARetirar = MundoDofus.getObjeto(id);
		if (!_objetos.contains(objARetirar)) {
			return;
		}
		Objeto objIgualInventario = perso.getObjSimilarInventario(objARetirar);
		int nuevaCant = objARetirar.getCantidad() - cant;
		if (objIgualInventario == null) {
			if (nuevaCant <= 0) {
				_objetos.remove(objARetirar);
				if (perso.addObjetoSimilar(objARetirar, true, -1)) {
					MundoDofus.eliminarObjeto(id);
				} else {
					perso.addObjetoPut(objARetirar);
					GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(perso, objARetirar);
				}
				String str = "O-" + id;
				GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(perso, str);
			} else {
				objIgualInventario = Objeto.clonarObjeto(objARetirar, cant);
				MundoDofus.addObjeto(objIgualInventario, true);
				objARetirar.setCantidad(nuevaCant);
				perso.addObjetoPut(objIgualInventario);
				GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(perso, objIgualInventario);
				String str = "O+" + objARetirar.getID() + "|" + objARetirar.getCantidad() + "|"
						+ objARetirar.getModelo().getID() + "|" + objARetirar.convertirStatsAString(0);
				GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(perso, str);
			}
		} else {
			if (nuevaCant <= 0) {
				_objetos.remove(objARetirar);
				objIgualInventario.setCantidad(objIgualInventario.getCantidad() + objARetirar.getCantidad());
				GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(perso, objIgualInventario);
				MundoDofus.eliminarObjeto(objARetirar.getID());
				String str = "O-" + id;
				GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(perso, str);
			} else {
				objARetirar.setCantidad(nuevaCant);
				objIgualInventario.setCantidad(objIgualInventario.getCantidad() + cant);
				GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(perso, objIgualInventario);
				String str = "O+" + objARetirar.getID() + "|" + objARetirar.getCantidad() + "|"
						+ objARetirar.getModelo().getID() + "|" + objARetirar.convertirStatsAString(0);
				GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(perso, str);
			}
		}
		GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(perso);
		GestorSalida.ENVIAR_Ew_PODS_MONTURA(perso, getPodsActuales());
		GestorSQL.ACTUALIZAR_MONTURA(this, false);
	}

	public void setDueÑo(int dueÑo) {
		_dueÑo = dueÑo;
	}

	public int getNivel() {
		return _nivel;
	}

	public long getExp() {
		return _experiencia;
	}

	public String getNombre() {
		return _nombre;
	}

	public int getDueÑo() {
		return _dueÑo;
	}

	public boolean estaCriando() {
		if (_celdaID == -1) {
			return false;
		} else {
			return true;
		}
	}

	private void actCapacidades() {
		_capacidades.clear();
		for (String s : _habilidad.split(",", 2)) {
			if (s != null) {
				int a = Integer.parseInt(s);
				try {
					_capacidades.add(a);
				} catch (Exception e) {
				}
			}
		}
	}

	public int getFecundadaHace() {
		if (_reprod == -1 || _reprod >= 20) {
			_fecundadaHace = -1;
			return -1;
		}
		if (_fecundadaHace >= 1) {
			return _fecundadaHace;
		}
		_fecundadaHace = -1;
		return _fecundadaHace;
	}

	public void setHabilidad(String habilidad) {
		_habilidad = habilidad;
		actCapacidades();
	}

	public int getFecunda() {
		if (_reprod == -1 || _fecundadaHace >= 1) {
			return 0;
		}
		if (_amor >= 7500 && _resistencia >= 7500 && _nivel > 4) {
			return 10;
		}
		return 0;
	}

	public void setMapaCelda(int mapa, int celda) {
		_mapaID = mapa;
		_celdaID = celda;
	}

	public int getFatiga() {
		return _fatiga;
	}

	public int getMapa() {
		return _mapaID;
	}

	public int getCelda() {
		return _celdaID;
	}

	public int getTalla() {
		return _talla;
	}

	public int getEnergia() {
		return _energia;
	}

	public int getReprod() {
		return _reprod;
	}

	public int getMadurez() {
		return _madurez;
	}

	public int getSerenidad() {
		return _serenidad;
	}

	public Stats getStats() {
		return _stats;
	}

	public ArrayList<Objeto> getObjetos() {
		return _objetos;
	}

	public List<Integer> getCapacidades() {
		return _capacidades;
	}

	public String detallesMontura() {
		StringBuilder str = new StringBuilder(_id + ":");
		str.append(_colorID + ":");
		str.append(_ancestros + ":");
		str.append(",," + _habilidad + ":");
		str.append(_nombre + ":");
		str.append(_sexo + ":");
		str.append(parseXpString() + ":");
		str.append(_nivel + ":");
		str.append(esMontable() + ":");
		str.append(getTotalPod() + ":");
		str.append("0" + ":"); // estado salvaje
		str.append(_resistencia + ",10000:");
		str.append(_madurez + "," + getMaxMadurez() + ":");
		str.append(_energia + "," + getMaxEnergia() + ":");
		str.append(_serenidad + ",-10000,10000:");
		str.append(_amor + ",10000:");
		str.append(getFecundadaHace() + ":");
		str.append(getFecunda() + ":");
		str.append(convertirStringAStats() + ":");
		str.append(_fatiga + ",240:");
		str.append(_reprod + ",20:");
		return str.toString();
	}

	public void castrarPavo() {
		_reprod = -1;
	}

	private String convertirStringAStats() {
		StringBuilder stats = new StringBuilder("");
		for (Entry<Integer, Integer> entry : _stats.getStatsComoMap().entrySet()) {
			if (entry.getValue() <= 0) {
				continue;
			}
			if (stats.length() > 0) {
				stats.append(",");
			}
			stats.append(Integer.toHexString(entry.getKey()) + "#" + Integer.toHexString(entry.getValue()) + "#0#0");
		}
		return stats.toString();
	}

	private int getMaxEnergia() {
		return (10 * _nivel) + (150 * CentroInfo.getGeneracion(_colorID));
	}

	private int getMaxMadurez() {
		return 1500 * CentroInfo.getGeneracion(_colorID);
	}

	private int getTotalPod() {
		int habilidad = 0;
		if (_capacidades.contains(2)) {
			habilidad = 20 * _nivel;
		}
		return (10 * _nivel) + (100 * CentroInfo.getGeneracion(_colorID) + habilidad);
	}

	private String parseXpString() {
		if (_nivel <= 0) {
			_nivel = 1;
		}
		return _experiencia + "," + MundoDofus.getExpNivel(_nivel)._montura + ","
				+ MundoDofus.getExpNivel(_nivel + 1)._montura;
	}

	public int esMontable() {
		if (_energia < 10 || _madurez < getMaxMadurez() || _fatiga == 240) {
			return 0;
		}
		return 1;
	}

	private void aumFatiga() {
		_fatiga += 2;
		if (_capacidades.contains(1)) {
			_fatiga -= 1;
		}
		if (_fatiga > 240) {
			_fatiga = 240;
		}
	}

	private void aumResistencia() {
		_resistencia += 100 * Emu.RATE_CRIANZA_PAVOS;
		if (_capacidades.contains(5)) {
			_resistencia += 100 * Emu.RATE_CRIANZA_PAVOS;
		}
		if (_resistencia > 10000) {
			_resistencia = 10000;
		}
	}

	public void setAmor(int amor) {
		_amor = amor;
	}

	public void setResistencia(int resistencia) {
		_resistencia = resistencia;
	}

	public void setMaxMadurez() {
		_madurez = getMaxMadurez();
	}

	public void setMaxEnergia() {
		_energia = getMaxEnergia();
	}

	private void aumMadurez() {
		int maxMadurez = getMaxMadurez();
		if (_madurez < maxMadurez) {
			_madurez += 100 * Emu.RATE_CRIANZA_PAVOS;
			if (_capacidades.contains(7)) {
				_madurez += 100 * Emu.RATE_CRIANZA_PAVOS;
			}
			if (_talla < 100) {
				Mapa mapa = MundoDofus.getMapa(_mapaID);
				if (maxMadurez / _madurez <= 1) {
					_talla = 100;
					GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(mapa, _id);
					GestorSalida.ENVIAR_GM_DRAGOPAVO_A_MAPA(mapa, this);
					return;
				} else if (_talla < 75 && maxMadurez / _madurez == 2) {
					_talla = 75;
					GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(mapa, _id);
					GestorSalida.ENVIAR_GM_DRAGOPAVO_A_MAPA(mapa, this);
					return;
				} else if (_talla < 50 && maxMadurez / _madurez == 3) {
					_talla = 50;
					GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(mapa, _id);
					GestorSalida.ENVIAR_GM_DRAGOPAVO_A_MAPA(mapa, this);
					return;
				}
			}
		}
		if (_madurez > maxMadurez) {
			_madurez = maxMadurez;
		}
	}

	private void aumAmor() {
		_amor += 100 * Emu.RATE_CRIANZA_PAVOS;
		if (_amor > 10000) {
			_amor = 10000;
		}
	}

	private void aumSerenidad() {
		_serenidad += 100 * Emu.RATE_CRIANZA_PAVOS;
		if (_serenidad > 10000) {
			_serenidad = 10000;
		}
	}

	private void resSerenidad() {
		_serenidad -= 100 * Emu.RATE_CRIANZA_PAVOS;
		if (_serenidad < -10000) {
			_serenidad = -10000;
		}
	}

	private void aumEnergia() {
		_energia += 10 * Emu.RATE_CRIANZA_PAVOS;
		int maxEnergia = getMaxEnergia();
		if (_energia > maxEnergia) {
			_energia = maxEnergia;
		}
	}

	public void aumEnergia(int valor, int veces) {
		_energia += (valor * veces);
		int maxEnergia = getMaxEnergia();
		if (_energia > maxEnergia) {
			_energia = maxEnergia;
		}
	}

	private void resFatiga() {
		_fatiga -= 20;
		if (_fatiga < 0) {
			_fatiga = 0;
		}
	}

	private void resAmor(int amor) {
		_amor -= amor;
		if (_amor < 0) {
			_amor = 0;
		}
	}

	private void resResistencia(int resistencia) {
		_resistencia -= resistencia;
		if (_resistencia < 0) {
			_resistencia = 0;
		}
	}

	public void aumReproduccion() {
		if (_reprod == -1) {
			return;
		}
		_reprod += 1;
	}

	public String stringObjetosBD() {
		StringBuilder str = new StringBuilder("");
		for (Objeto obj : _objetos) {
			str.append((str.length() > 0 ? ";" : "") + obj.getID());
		}
		return str.toString();
	}

	public void setNombre(String nombre) {
		_nombre = nombre;
	}

	void addXp(long aumentar) {
		if (_capacidades.contains(4)) {
			aumentar = aumentar * 2;
		}
		_experiencia += aumentar;
		while (_experiencia >= MundoDofus.getExpNivel(_nivel + 1)._montura && _nivel < 200) {
			subirNivel();
		}
	}

	private void subirNivel() {
		_nivel++;
		if (_colorID != 74) {
			_stats = CentroInfo.getStatsMontura(_colorID, _nivel);
		} else {
			_stats = CentroInfo.getStatsMonturaVIP(_vip, _nivel);
		}
	}

	String getStringColor(String colorDueÑoPavo) {
		String b = "";
		if (_capacidades.contains(9)) {
			b = "," + colorDueÑoPavo;
		}
		if (_colorID == 75) {
			int colorRandom = Formulas.getRandomValor(1, 87);
			b = "," + CentroInfo.getStringColorDragopavo(colorRandom);
		}
		return _colorID + b;
	}

	public String getHabilidad() {
		return _habilidad;
	}

	public boolean addCapacidad(String capa) {
		int c = 0;
		for (String s : capa.split(",", 2)) {
			if (_capacidades.size() >= 2) {
				return false;
			}
			try {
				c = Integer.parseInt(s);
			} catch (Exception e) {
			}
			if (c != 0) {
				_capacidades.add(c);
			}
			if (_capacidades.size() == 1) {
				_habilidad = _capacidades.get(0) + ",";
			} else {
				_habilidad = _capacidades.get(0) + "," + _capacidades.get(1);
			}
		}
		return true;
	}

	void energiaPerdida(int energia) {
		_energia = energia;
	}

	public int getPareja() {
		return _pareja;
	}

	public void setPareja(int pareja) {
		_pareja = pareja;
	}

	public int getOrientacion() {
		return _orientacion;
	}

	public void setFecundadaHace(int fecundable) {
		if (_reprod == -1) {
			return;
		}
		_fecundadaHace = fecundable;
	}

	private boolean esCastrado() {
		if (_reprod == -1) {
			return true;
		} else {
			return false;
		}
	}

	public synchronized String getCriarMontura(Cercado cercado) {
		if (cercado == null) {
			return "";
		}
		StringBuilder str = new StringBuilder("GM|+");
		if (_celdaID == -1 && _mapaID == -1) {
			str.append(cercado.getColocarCelda() + ";");
		} else {
			str.append(_celdaID + ";");
		}
		str.append(_orientacion + ";0;" + _id + ";" + _nombre + ";-9;");
		if (_colorID == 88) {
			str.append(7005);
		} else {
			str.append(7002);
		}
		str.append("^" + _talla + ";");
		if (MundoDofus.getPersonaje(_dueÑo) == null) {
			str.append("Sin DueÑo");
		} else {
			str.append(MundoDofus.getPersonaje(_dueÑo).getNombre());
		}
		str.append(";" + _nivel + ";" + _colorID);
		return str.toString();
	}

	public synchronized void moverMontura(Personaje dueÑo, int casillas, boolean alejar) {
		int accion = 0;
		if ((dueÑo == null) || (dueÑo.getCelda().getID() == _celdaID)) {
			return;
		}
		StringBuilder path = new StringBuilder("");
		Mapa mapa = dueÑo.getMapa();
		if (mapa.getCercado() == null) {
			return;
		}
		Cercado cercado = mapa.getCercado();
		if (cercado.getListaCriando().size() <= 0) {
			return;
		}
		char direccion;
		int azar = Formulas.getRandomValor(1, 10);
		direccion = Camino.getDirEntreDosCeldas(mapa, (short) _celdaID, dueÑo.getCelda().getID());
		if (alejar) {
			direccion = Camino.getDireccionOpuesta(direccion);
		}
		int celda = _celdaID;
		int celdaprueba = _celdaID;
		for (int i = 0; i < casillas; i++) {
			celdaprueba = Camino.getSigIDCeldaMismaDir(celdaprueba, direccion, _mapaID);
			if (mapa.getCelda(celdaprueba) == null) {
				return;
			}
			if (cercado.getCeldayObjeto().containsKey(celdaprueba)) {
				int item = cercado.getCeldayObjeto().get(celdaprueba);
				if (item == 7758) {// Aporreadora de olmo
					resSerenidad();
				} else if (item == 7781) {// Fulminadora de olmo
					if (_serenidad < 0) {
						aumResistencia();
					}
				} else if (item == 7613) {// Pesebre de carpe
					resFatiga();
					aumEnergia();
				} else if (item == 7696) {// Dragonalgas de cuero violeta de bwork
					if (_serenidad > 0) {
						aumAmor();
					}
				} else if (item == 7628) {// Acariciador de Pluma del Último Pohoyo
					aumSerenidad();
				} else if (item == 7594) {// Abrevadero de Carpe
					if (_serenidad <= 2000 && _serenidad >= -2000) {
						aumMadurez();
					}
				}
				aumFatiga();
				break;
			}
			if (mapa.getCelda(celdaprueba).esCaminable(false) && cercado.getPuerta() != celdaprueba
					&& !mapa.celdaSalienteLateral(celda, celdaprueba)) {
				celda = celdaprueba;
				path.append(direccion + Encriptador.celdaIDACodigo(celda));
			} else {
				break;
			}
		}
		if (celda == _celdaID) {
			_orientacion = Encriptador.getNumeroPorValorHash(direccion);
			GestorSalida.ENVIAR_eD_CAMBIAR_ORIENTACION(mapa, _id, _orientacion);
			GestorSalida.ENVIAR_GDE_FRAME_OBJECT_EXTERNAL(mapa, celdaprueba + ";4");
			GestorSalida.ENVIAR_eUK_EMOTE_MAPA(mapa, _id, accion, "");
			return;
		}
		if (azar == 5) {
			accion = 8;
		}
		if (cercado.getListaCriando().size() > 1) {
			for (Integer pavo : cercado.getListaCriando()) {
				Dragopavo dragopavo = MundoDofus.getDragopavoPorID(pavo);
				if (dragopavo._sexo != _sexo && dragopavo.getFecunda() != 0 && getFecunda() != 0
						&& dragopavo.getCelda() == celdaprueba) {
					if (dragopavo._reprod < 20 && _reprod < 20 && !dragopavo.esCastrado() && !esCastrado()) {
						int aparearce = Formulas.getRandomValor(2, 4);
						if (dragopavo._capacidades.contains(6) || _capacidades.contains(6)) {
							aparearce = 3;
						}
						if (aparearce == 3) {
							if (dragopavo._sexo == 1) {
								dragopavo._fecundadaHace = 1;
								dragopavo._aumentarFecundo.start();
								dragopavo.setPareja(_id);
								resAmor(7500);
								resResistencia(7500);
							} else if (_sexo == 1) {
								_fecundadaHace = 1;
								_aumentarFecundo.start();
								_pareja = dragopavo.getID();
								dragopavo.resAmor(7500);
								dragopavo.resResistencia(7500);
							}
							accion = 4;
							break;
						}
					}
				}
			}
		}
		GestorSalida.ENVIAR_GA_ACCION_JUEGO_AL_MAPA(dueÑo, mapa, "" + 0, 1, _id + "",
				"a" + Encriptador.celdaIDACodigo(_celdaID) + path);
		_celdaID = celda;
		_orientacion = Encriptador.getNumeroPorValorHash(direccion);
		int ID = _id;
		try {
			Thread.sleep(1250);
		} catch (Exception e) {
		}
		GestorSalida.ENVIAR_GDE_FRAME_OBJECT_EXTERNAL(mapa, celdaprueba + ";4");
		try {
			Thread.sleep(500);
		} catch (Exception e) {
		}
		if (accion != 0) {
			GestorSalida.ENVIAR_eUK_EMOTE_MAPA(mapa, ID, accion, "");
		}
		return;
	}

	synchronized void moverMonturaAuto(char dir, int casillas, boolean alejar) {
		int accion = 0;
		StringBuilder path = new StringBuilder("");
		Mapa mapa = MundoDofus.getMapa(_mapaID);
		if ((mapa == null) || (mapa.getCercado() == null)) {
			return;
		}
		Cercado cercado = mapa.getCercado();
		char direccion = dir;
		int azar = Formulas.getRandomValor(1, 10);
		int celda = _celdaID;
		int celdaprueba = _celdaID;
		for (int i = 0; i < casillas; i++) {
			celdaprueba = Camino.getSigIDCeldaMismaDir((short) celdaprueba, direccion, _mapaID);
			if (mapa.getCelda(celdaprueba) == null) {
				return;
			}
			if (cercado.getCeldayObjeto().containsKey(celdaprueba)) {
				int item = cercado.getCeldayObjeto().get(celdaprueba);
				if (item == 7758) {// Aporreadora de olmo
					resSerenidad();
				} else if (item == 7781) {// Fulminadora de olmo
					if (_serenidad < 0) {
						aumResistencia();
					}
				} else if (item == 7613) {// Pesebre de carpe
					resFatiga();
					aumEnergia();
				} else if (item == 7696) {// Dragonalgas de cuero violeta de bwork
					if (_serenidad > 0) {
						aumAmor();
					}
				} else if (item == 7628) {// Acariciador de Pluma del Último Pohoyo
					aumSerenidad();
				} else if (item == 7594) {// Abrevadero de Carpe
					if (_serenidad <= 2000 && _serenidad >= -2000) {
						aumMadurez();
					}
				}
				aumFatiga();
				break;
			}
			if (mapa.getCelda(celdaprueba).esCaminable(false) && cercado.getPuerta() != celdaprueba
					&& !mapa.celdaSalienteLateral(celda, celdaprueba)) {
				celda = celdaprueba;
				path.append(direccion + Encriptador.celdaIDACodigo(celda));
			} else {
				break;
			}
		}
		if (celda == _celdaID) {
			_orientacion = Encriptador.getNumeroPorValorHash(direccion);
			GestorSalida.ENVIAR_eD_CAMBIAR_ORIENTACION(mapa, _id, _orientacion);
			GestorSalida.ENVIAR_GDE_FRAME_OBJECT_EXTERNAL(mapa, celdaprueba + ";4");
			GestorSalida.ENVIAR_eUK_EMOTE_MAPA(mapa, _id, accion, "");
			return;
		}
		if (azar == 5) {
			accion = 8;
		}
		if (cercado.getListaCriando().size() > 1) {
			for (Integer pavo : cercado.getListaCriando()) {
				Dragopavo dragopavo = MundoDofus.getDragopavoPorID(pavo);
				if (dragopavo == null) {
					continue;
				}
				if (dragopavo._sexo != _sexo && dragopavo.getFecunda() != 0 && getFecunda() != 0
						&& dragopavo.getCelda() == celdaprueba) {
					if (dragopavo._reprod < 20 && _reprod < 20 && !dragopavo.esCastrado() && !esCastrado()) {
						int aparearce = Formulas.getRandomValor(2, 4);
						if (dragopavo._capacidades.contains(6) || _capacidades.contains(6)) {
							aparearce = 3;
						}
						if (aparearce == 3) {
							if (dragopavo._sexo == 1) {
								dragopavo._fecundadaHace = 1;
								dragopavo._aumentarFecundo.start();
								dragopavo.setPareja(_id);
								resAmor(7500);
								resResistencia(7500);
							} else if (_sexo == 1) {
								_fecundadaHace = 1;
								_aumentarFecundo.start();
								_pareja = dragopavo.getID();
								dragopavo.resAmor(7500);
								dragopavo.resResistencia(7500);
							}
							accion = 4;
							break;
						}
					}
				}
			}
		}
		GestorSalida.ENVIAR_GA_ACCION_JUEGO_AL_MAPA(null, mapa, "" + 0, 1, _id + "",
				"a" + Encriptador.celdaIDACodigo(_celdaID) + path);
		_celdaID = celda;
		_orientacion = Encriptador.getNumeroPorValorHash(direccion);
		int ID = _id;
		try {
			Thread.sleep(1250);
		} catch (Exception e) {
		}
		GestorSalida.ENVIAR_GDE_FRAME_OBJECT_EXTERNAL(mapa, celdaprueba + ";4");
		try {
			Thread.sleep(500);
		} catch (Exception e) {
		}
		if (accion != 0) {
			GestorSalida.ENVIAR_eUK_EMOTE_MAPA(mapa, ID, accion, "");
		}
		return;
	}

	public int minutosParir() {
		if (_reprod == 0) {
			return Emu.RATE_TIEMPO_PARIR;
		} else if (_reprod < 5) {
			return 2 * Emu.RATE_TIEMPO_PARIR;
		} else if (_reprod < 11) {
			return 3 * Emu.RATE_TIEMPO_PARIR;
		} else if (_reprod <= 20) {
			return 4 * Emu.RATE_TIEMPO_PARIR;
		}
		return 1;
	}
}
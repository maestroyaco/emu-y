package variables;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;

import estaticos.Camino;
import estaticos.Emu;
import estaticos.Encriptador;
import estaticos.GestorSQL;
import estaticos.GestorSalida;
import estaticos.MundoDofus;
import variables.Mapa.Celda;
import variables.Pelea.Luchador;

public class Recaudador {
	private int _id;
	private int _mapaID;
	private int _celdaID;
	private int _orientacion;
	private int _gremioID = 0;
	private String _nombre_1 = "";
	private String _nombre_2 = "";
	private int _estadoPelea = 0;
	private int _peleaID = -1;
	private Map<Integer, Objeto> _objetos = new TreeMap<>();
	private long _kamas = 0;
	private long _xp = 0;
	private boolean _enRecolecta = false;
	private Pelea _pelea;

	public Recaudador(int ID, int mapa, int celdaID, byte orientacion, int gremioID, String N1, String N2, String items,
			long kamas, long xp) {
		_id = ID;
		_mapaID = mapa;
		_celdaID = celdaID;
		_orientacion = orientacion;
		_gremioID = gremioID;
		_nombre_1 = N1;
		_nombre_2 = N2;
		for (String item : items.split("\\|")) {
			if (item.equals("")) {
				continue;
			}
			String[] infos = item.split(":");
			int id = Integer.parseInt(infos[0]);
			Objeto obj = MundoDofus.getObjeto(id);
			if (obj == null) {
				continue;
			}
			_objetos.put(obj.getID(), obj);
		}
		_xp = xp;
		_kamas = kamas;
		_pelea = null;
	}

	public long getKamas() {
		return _kamas;
	}

	public int getPodsActuales() {
		int pods = 0;
		ArrayList<Integer> objetosNull = new ArrayList<>();
		for (Entry<Integer, Objeto> entry : _objetos.entrySet()) {
			Objeto obj = entry.getValue();
			if (obj == null) {
				objetosNull.add(entry.getKey());
				continue;
			}
			pods += (obj.getModelo().getPeso() * obj.getCantidad());
		}
		for (Integer obj : objetosNull) {
			if (_objetos.containsKey(obj)) {
				_objetos.remove(obj);
			}
		}
		return pods;
	}

	public void setKamas(long kamas) {
		this._kamas = kamas;
	}

	public long getXp() {
		return _xp;
	}

	public void setXp(long xp) {
		this._xp = xp;
	}

	void addXp(long xp) {
		this._xp += xp;
	}

	public Map<Integer, Objeto> getObjetos() {
		return _objetos;
	}

	void borrarObjeto(int id) {
		_objetos.remove(id);
	}

	public boolean tieneObjeto(int id) {
		if (_objetos.get(id) != null) {
			return true;
		} else {
			return false;
		}
	}

	public static String enviarGMDeRecaudador(Mapa mapa) {
		StringBuilder packet = new StringBuilder("GM|");
		boolean primero = true;
		ArrayList<Recaudador> aBorrar = new ArrayList<>();
		Map<Integer, Recaudador> todosRecaudadores = MundoDofus.getTodosRecaudadores();
		for (Entry<Integer, Recaudador> recau : todosRecaudadores.entrySet()) {
			Recaudador recaudador = recau.getValue();
			if (recau.getValue()._estadoPelea > 0) {
				continue;
			}
			if (recaudador._mapaID == mapa.getID()) {
				if (!primero) {
					packet.append("|");
				}
				Gremio G = MundoDofus.getGremio(recaudador._gremioID);
				if (G == null) {
					aBorrar.add(recaudador);
					continue;
				}
				packet.append("+");
				packet.append(recaudador._celdaID + ";");
				packet.append(recaudador._orientacion + ";");
				packet.append("0" + ";");
				packet.append(recaudador._id + ";");
				packet.append((recaudador._nombre_1 + "," + recaudador._nombre_2 + ";"));
				packet.append("-6" + ";");
				packet.append("6000^100;");
				packet.append(G.getNivel() + ";");
				packet.append(G.getNombre() + ";" + G.getEmblema());
				primero = false;
			} else {
				continue;
			}
		}
		for (Recaudador recau : aBorrar) {
			recau.borrarRecaudador(recau.getID());
		}
		return packet.toString();
	}

	public int getGremioID() {
		return _gremioID;
	}

	public void borrarRecaudador(int idRecaudador) {
		for (Objeto obj : _objetos.values()) {
			MundoDofus.eliminarObjeto(obj._id);
		}
		MundoDofus.borrarRecaudador(idRecaudador);
	}

	public void borrarRecauPorRecolecta(int idRecaudador, Personaje perso) {
		Gremio gremio = perso.getGremio();
		Mapa mapa = this.getMapa();
		gremio.addUltRecolectaMapa((short)mapa.getID());
		perso.addKamas(_kamas);
		try {
			for (Objeto obj : _objetos.values()) {
				if (obj == null) {
					continue;
				}
				int id = 0;
				int cant = 0;
				id = obj.getID();
				cant = obj.getCantidad();
				if (id <= 0 || cant <= 0) {
					continue;
				}
				if (perso.addObjetoSimilar(obj, true, -1)) {
					MundoDofus.eliminarObjeto(id);
				} else {
					perso.addObjetoPut(obj);
					GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(perso, obj);
				}
			}
		} catch (NumberFormatException e) {
		}
		MundoDofus.borrarRecaudador(idRecaudador);
	}

	public int getEstadoPelea() {
		return _estadoPelea;
	}

	public void setEstadoPelea(int estado) {
		_estadoPelea = estado;
	}

	public int getID() {
		return _id;
	}

	public int getCeldalID() {
		return _celdaID;
	}

	public void setPeleaID(int ID) {
		_peleaID = ID;
	}

	public void setPelea(Pelea pelea) {
		_pelea = pelea;
	}

	public Pelea getPelea() {
		return _pelea;
	}

	public int getPeleaID() {
		return _peleaID;
	}

	public int getMapaID() {
		return _mapaID;
	}

	public String getN1() {
		return _nombre_1;
	}

	public String getN2() {
		return _nombre_2;
	}

	public static String analizarRecaudadores(int gremioID) {
		StringBuilder str = new StringBuilder("+");
		boolean primero = false;
		for (Entry<Integer, Recaudador> recau : MundoDofus.getTodosRecaudadores().entrySet()) {
			if (recau.getValue().getGremioID() == gremioID) {
				Mapa mapa = MundoDofus.getMapa((short) recau.getValue().getMapaID());
				if (mapa == null) {
					continue;
				}
				if (primero) {
					str.append("|");
				}
				str.append(Integer.toString(recau.getValue().getID(), 36) + ";" + recau.getValue().getN1() + ","
						+ recau.getValue().getN2() + ";");
				str.append(Integer.toString(mapa.getID(), 36) + "," + mapa.getCoordX() + "," + mapa.getCoordY() + ";");
				str.append(recau.getValue().getEstadoPelea() + ";");
				if (recau.getValue().getEstadoPelea() == 1) {
					if (mapa.getPelea(recau.getValue().getPeleaID()) == null) {
						str.append(Pelea.tiempoPRecau + ";");
					} else {
						str.append(Math
								.round(Pelea.tiempoPRecau
										- (System.currentTimeMillis() - recau.getValue().getPelea()._tiempoInicioPelea))
								+ ";");
					}
					str.append("45000;");
					str.append("7;");
					str.append("?,?,");
				} else {
					str.append("0;");
					str.append("45000;");
					str.append("7;");
					str.append("?,?,");
				}
				str.append("1,2,3,4,5");
				primero = true;
			}
		}
		if (str.length() == 1) {
			return null;
		} else {
			return str.toString();
		}
	}

	public static int getIDGremioPorMapaID(int id) {
		for (Entry<Integer, Recaudador> recau : MundoDofus.getTodosRecaudadores().entrySet()) {
			if (recau.getValue().getMapaID() == id) {
				return recau.getValue().getGremioID();
			}
		}
		return 0;
	}

	public static void analizarAtaque(Personaje perso, int gremioID) {
		for (Entry<Integer, Recaudador> recau : MundoDofus.getTodosRecaudadores().entrySet()) {
			Recaudador recaudador = recau.getValue();
			if (recaudador._estadoPelea > 0 && recaudador._gremioID == gremioID) {
				GestorSalida.ENVIAR_gITp_INFO_ATACANTES_RECAUDADOR(perso,
						atacantesAlGremio(recaudador._id, recaudador._mapaID, recaudador._peleaID));
			}
		}
	}

	public static void analizarDefensa(Personaje perso, int gremioID) {
		for (Entry<Integer, Recaudador> perco : MundoDofus.getTodosRecaudadores().entrySet()) {
			Recaudador recaudador = perco.getValue();
			if (recaudador._estadoPelea > 0 && recaudador._gremioID == gremioID) {
				GestorSalida.ENVIAR_gITP_INFO_DEFENSORES_RECAUDADOR(perso,
						defensoresDelGremio(recaudador._id, recaudador._mapaID, recaudador._peleaID));
			}
		}
	}

	private static String atacantesAlGremio(int id, int mapaID, int peleaID) {
		StringBuilder str = new StringBuilder("+");
		str.append(Integer.toString(id, 36));
		for (Entry<Integer, Pelea> pelea : MundoDofus.getMapa(mapaID).getPeleas().entrySet()) {
			if (pelea.getValue().getID() == peleaID) {
				for (Luchador luchador : pelea.getValue().luchadoresDeEquipo(1)) {
					if (luchador.getPersonaje() == null) {
						continue;
					}
					str.append("|");
					str.append(Integer.toString(luchador.getPersonaje().getID(), 36) + ";");
					str.append(luchador.getPersonaje().getNombre() + ";");
					str.append(luchador.getPersonaje().getNivel() + ";");
					str.append("0;");
				}
			}
		}
		return str.toString();
	}

	private static String defensoresDelGremio(int id, int mapaID, int peleaID) {
		StringBuilder str = new StringBuilder("+");
		str.append(Integer.toString(id, 36));
		for (Entry<Integer, Pelea> pelea : MundoDofus.getMapa(mapaID).getPeleas().entrySet()) {
			if (pelea.getValue().getID() == peleaID) {
				for (Luchador luchador : pelea.getValue().luchadoresDeEquipo(2)) {
					if (luchador.getPersonaje() == null) {
						continue;
					}
					str.append("|");
					str.append(Integer.toString(luchador.getPersonaje().getID(), 36) + ";");
					str.append(luchador.getPersonaje().getNombre() + ";");
					str.append(luchador.getPersonaje().getGfxID() + ";");
					str.append(luchador.getPersonaje().getNivel() + ";");
					str.append(Integer.toString(luchador.getPersonaje().getColor1(), 36) + ";");
					str.append(Integer.toString(luchador.getPersonaje().getColor2(), 36) + ";");
					str.append(Integer.toString(luchador.getPersonaje().getColor3(), 36) + ";");
					str.append("0;");
				}
				pelea.getValue().setListaDefensores("-" + str.substring(1));
			}
		}
		return str.toString();
	}

	public String getListaObjRecaudador() {
		StringBuilder objetos = new StringBuilder("");
		for (Objeto obj : _objetos.values()) {
			objetos.append("O" + obj.stringObjetoConGuiÓn());
		}
		if (_kamas != 0) {
			objetos.append("G" + _kamas);
		}
		return objetos.toString();
	}

	public String stringListaObjetosBD() {
		StringBuilder objetos = new StringBuilder("");
		for (Objeto obj : _objetos.values()) {
			objetos.append(obj._id + "|");
		}
		return objetos.toString();
	}

	public void borrarDesdeRecaudador(Personaje P, int idObjeto, int cantidad) {
		Objeto RecauObj = MundoDofus.getObjeto(idObjeto);
		Objeto PersoObj = P.getObjSimilarInventario(RecauObj);
		int nuevaCant = RecauObj.getCantidad() - cantidad;
		if (PersoObj == null) {
			if (nuevaCant <= 0) {
				borrarObjeto(idObjeto);
				P.addObjetoPut(RecauObj);
				GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(P, RecauObj);
				String str = "O-" + idObjeto;
				GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(P, str);
			} else {
				PersoObj = Objeto.clonarObjeto(RecauObj, cantidad);
				MundoDofus.addObjeto(PersoObj, true);
				RecauObj.setCantidad(nuevaCant);
				P.addObjetoPut(PersoObj);
				GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(P, PersoObj);
				String str = "O+" + RecauObj.getID() + "|" + RecauObj.getCantidad() + "|" + RecauObj.getModelo().getID()
						+ "|" + RecauObj.convertirStatsAString(0);
				GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(P, str);
			}
		} else {
			if (nuevaCant <= 0) {
				this.borrarObjeto(idObjeto);
				MundoDofus.eliminarObjeto(RecauObj.getID());
				PersoObj.setCantidad(PersoObj.getCantidad() + RecauObj.getCantidad());
				GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(P, PersoObj);
				String str = "O-" + idObjeto;
				GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(P, str);
			} else {
				RecauObj.setCantidad(nuevaCant);
				PersoObj.setCantidad(PersoObj.getCantidad() + cantidad);
				GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(P, PersoObj);
				String str = "O+" + RecauObj.getID() + "|" + RecauObj.getCantidad() + "|" + RecauObj.getModelo().getID()
						+ "|" + RecauObj.convertirStatsAString(0);
				GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(P, str);
			}
		}
		GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(P);
		GestorSQL.SALVAR_PERSONAJE(P, true);
	}

	public String stringObjetos() {
		StringBuilder str = new StringBuilder("");
		boolean esPrimero = true;
		for (Objeto obj : _objetos.values()) {
			if (!esPrimero) {
				str.append(";");
			}
			str.append(obj.getModelo().getID() + "," + obj.getCantidad());
			esPrimero = false;
		}
		return str.toString();
	}

	void addObjeto(Objeto nuevoObj) {
		_objetos.put(nuevoObj.getID(), nuevoObj);
	}

	public void setEnRecolecta(boolean Exchange) {
		_enRecolecta = Exchange;
	}

	public boolean getEnRecolecta() {
		return _enRecolecta;
	}

	public int getOrientacion() {
		return _orientacion;
	}

	public Mapa getMapa() {
		return MundoDofus.getMapa(_mapaID);
	}

	public void moverPerco() {
		Mapa mapa = MundoDofus.getMapa(_mapaID);
		if (mapa.getPersos().size() == 0) {
			return;
		}
		int celdadestino = Camino.celdaMovPerco(mapa, (short) _celdaID);
		ArrayList<Celda> finalPath = Camino.pathMasCortoEntreDosCeldas(mapa, (short) _celdaID, (short) celdadestino, 0);
		StringBuilder pathstr = new StringBuilder("");
		int tempCeldaID = _celdaID;
		int tempDireccion = 0;
		for (Celda celda : finalPath) {
			char dir = Camino.getDirEntreDosCeldas((short) tempCeldaID, celda.getID(), mapa, true);
			if (dir == 0) {
				return;
			}
			if (dir != 0) {
				if (finalPath.indexOf(celda) != 0) {
					pathstr.append(Encriptador.celdaIDACodigo(tempCeldaID));
				}
				pathstr.append(dir);
				switch (dir) {
				case 97:
					tempDireccion = 0;
					break;
				case 98:
					tempDireccion = 1;
					break;
				case 99:
					tempDireccion = 2;
					break;
				case 100:
					tempDireccion = 3;
					break;
				case 101:
					tempDireccion = 4;
					break;
				case 102:
					tempDireccion = 5;
					break;
				case 103:
					tempDireccion = 6;
					break;
				case 104:
					tempDireccion = 7;
					break;
				}
			}
			tempCeldaID = celda.getID();
		}
		if (tempCeldaID != _celdaID) {
			pathstr.append(Encriptador.celdaIDACodigo(tempCeldaID));
		}
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}
		String path = pathstr.toString();
		if (path.equals("")) {
			if (Emu.MOSTRAR_RECIBIDOS) {
				System.out.println("Fallo de desplazamiento: camino vacio");
			}
			return;
		}
		AtomicReference<String> pathRef = new AtomicReference<>(path);
		int result = Camino.caminoValido(getMapa(), (short) getCeldalID(), pathRef, null, null);
		if (result == 0) {
			return;
		}
		if (result != -1000 && result < 0) {
			result = -result;
		}
		path = pathRef.get();
		if (result == -1000) {
			path = Encriptador.getValorHashPorNumero(getOrientacion()) + Encriptador.celdaIDACodigo(getCeldalID());
		}
		GestorSalida.ENVIAR_GA_ACCION_JUEGO_AL_MAPA(null, getMapa(), "" + 0, 1, _id + "",
				"a" + Encriptador.celdaIDACodigo(_celdaID) + path);
		_celdaID = celdadestino;
		_orientacion = tempDireccion;
		return;
	}
}
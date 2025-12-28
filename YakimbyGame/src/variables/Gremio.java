package variables;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import estaticos.CentroInfo;
import estaticos.Formulas;
import estaticos.GestorSQL;
import estaticos.GestorSalida;
import estaticos.MundoDofus;
import servidor.Colores;
import variables.Hechizo.StatsHechizos;
import variables.Mapa.Cercado;
import variables.Personaje.Stats;

public class Gremio {
	private int _id;
	private String _nombre = "";
	private String _emblema = "";
	private Map<Integer, MiembroGremio> _miembros = new TreeMap<>();
	private int _nvl;
	private long _xp;
	private int _capital = 0;
	private int _nroRecaudadores = 0;
	private Map<Integer, StatsHechizos> _hechizos = new TreeMap<>();
	private Map<Integer, Integer> _stats = new TreeMap<>();
	private Map<Integer, Integer> _statsPelea = new TreeMap<>();
	public String ultimaConex = "";
	public boolean borrado = false;
	public int informaC = 0;
	public int puntosextra = 0;// 464 crocobur,422 posicion defensiva,373 chaferloko
	public int lastidpelea = 0;
	private final Map<Short, Long> _tiempoMapaRecolecta = new TreeMap<>(); // tiempo recaudador

	public Gremio(Personaje dueÑo, String nombre, String emblema) {
		_id = MundoDofus.getSigIdGremio();
		_nombre = nombre;
		_emblema = emblema;
		_nvl = 1;
		_xp = 0;
		decompilarHechizos("462;0|461;0|460;0|459;0|458;0|457;0|456;0|455;0|454;0|453;0|452;0|451;0|");
		decompilarStats("176;100|158;1000|124;100|");
	}

	public Gremio(int id, String nombre, String emblema, int nivel, long xp, int capital, int nroMaxRecau,
			String hechizos, String stats, String tiempo, int alinea, int puntoext) {
		_id = id;
		_nombre = nombre;
		_emblema = emblema;
		_xp = xp;
		_nvl = nivel;
		_capital = capital;
		_nroRecaudadores = nroMaxRecau;
		ultimaConex = tiempo;
		decompilarHechizos(hechizos);
		decompilarStats(stats);
		_statsPelea.clear();
		_statsPelea.put(CentroInfo.STATS_ADD_PA, 9);
		_statsPelea.put(CentroInfo.STATS_ADD_PM, 6);
		_statsPelea.put(CentroInfo.STATS_ADD_FUERZA, _nvl * 15);
		_statsPelea.put(CentroInfo.STATS_ADD_SABIDURIA, getStats(CentroInfo.STATS_ADD_SABIDURIA));
		_statsPelea.put(CentroInfo.STATS_ADD_INTELIGENCIA, _nvl * 15);
		_statsPelea.put(CentroInfo.STATS_ADD_SUERTE, _nvl * 15);
		_statsPelea.put(CentroInfo.STATS_ADD_AGILIDAD, _nvl * 15);
		_statsPelea.put(CentroInfo.STATS_ADD_ResPorc_NEUTRAL, (int) Math.floor(getNivel() / 3));
		_statsPelea.put(CentroInfo.STATS_ADD_ResPorc_FUEGO, (int) Math.floor(getNivel() / 3));
		_statsPelea.put(CentroInfo.STATS_ADD_ResPorc_AGUA, (int) Math.floor(getNivel() / 3));
		_statsPelea.put(CentroInfo.STATS_ADD_ResPorc_AIRE, (int) Math.floor(getNivel() / 3));
		_statsPelea.put(CentroInfo.STATS_ADD_ResPorc_TIERRA, (int) Math.floor(getNivel() / 3));
		_statsPelea.put(CentroInfo.STATS_ADD_ProbPerdida_PA, (int) Math.floor(getNivel() / 3));
		_statsPelea.put(CentroInfo.STATS_ADD_ProbPerdida_PM, (int) Math.floor(getNivel() / 3));
		if (alinea == -1) {
			informaC = Formulas.getRandomValor(0, 1);
			GestorSQL.ACTUALIZAR_ALINEA(informaC, _id);
		}
		informaC = alinea;
		puntosextra = puntoext;
	}

	public MiembroGremio addMiembro(int id, String nombre, int nivel, int gfx, int r, byte pXp, long x, int derechos,
			String ultConeccion) {
		MiembroGremio GM = new MiembroGremio(id, this, nombre, nivel, gfx, r, x, pXp, derechos, ultConeccion);
		_miembros.put(id, GM);
		return GM;
	}

	public MiembroGremio addNuevoMiembro(Personaje perso) {
		MiembroGremio GM = new MiembroGremio(perso.getID(), this, perso.getNombre(), perso.getNivel(), perso.getGfxID(),
				0, 0, (byte) 0, 0, perso.getCuenta().getUltimaConeccion());
		_miembros.put(perso.getID(), GM);
		return GM;
	}

	public int getID() {
		return _id;
	}

	public int getNroRecau() {
		return _nroRecaudadores;
	}

	public String getInfoGremio() {
		String str = _nombre + "," + getStats(158) + "," + getStats(176) + "," + getStats(124) + ","
				+ getRecauColocados();
		return str;
	}

	public String getInfo() {
		String str = _nombre;
		return str;
	}

	public void setNroRecau(int nro) {
		_nroRecaudadores = nro;
	}

	public int getRecauColocados() {
		int numero = 0;
		for (Recaudador perco : MundoDofus.getTodosRecaudadores().values()) {
			if (perco.getGremioID() == _id) {
				numero++;
			}
		}
		return numero;
	}

	//tiempo recaudador colocar
	public void addUltRecolectaMapa(short mapaID) {
		_tiempoMapaRecolecta.put(mapaID, System.currentTimeMillis());
	}

	public boolean puedePonerRecaudadorMapa(short mapaID, Personaje perso) {
		if (CentroInfo.HORAS_VOLVER_A_PONER_RECAUDADOR_MAPA < 1) {
			return true;
		}
		if (_tiempoMapaRecolecta.containsKey(mapaID)) {
			int tiempoM = ((60 * 60 * 1000) *CentroInfo.HORAS_VOLVER_A_PONER_RECAUDADOR_MAPA);


			long tiempoFuturo = _tiempoMapaRecolecta.get(mapaID) + tiempoM;
			long diferencia = tiempoFuturo - System.currentTimeMillis();
			long segundos = diferencia / 1000;
			long horas = segundos / 3600;
			segundos %= 3600;
			long minutos = segundos / 60;
			segundos %= 60;
			String formatoTiempo = String.format("%02d:%02d:%02d", horas, minutos, segundos);
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "Aun Faltan: " +formatoTiempo + " Para Colocar un Recaudador de Nuevo", Colores.ROJO);

			return _tiempoMapaRecolecta.get(mapaID) + tiempoM <= System.currentTimeMillis();
		}
		return true;
	}

	//tiempo recaudador colocar

	public int getCapital() {
		return _capital;
	}

	public void setCapital(int nro) {
		_capital = nro;
	}

	public Map<Integer, StatsHechizos> getHechizos() {
		return _hechizos;
	}

	public Map<Integer, Integer> getStats() {
		return _stats;
	}

	public void addStat(int stat, int cant) {
		int vieja = _stats.get(stat);
		_stats.put(stat, vieja + cant);
	}

	public void boostHechizo(int ID) {
		StatsHechizos SS = _hechizos.get(ID);
		if (SS != null && SS.getNivel() == 5) {
			return;
		}
		_hechizos.put(ID, ((SS == null) ? MundoDofus.getHechizo(ID).getStatsPorNivel(1)
				: MundoDofus.getHechizo(ID).getStatsPorNivel(SS.getNivel() + 1)));
	}

	public Stats getStatsPelea() {
		return new Stats(_statsPelea);
	}

	public String getNombre() {
		return _nombre;
	}

	public String getEmblema() {
		return _emblema;
	}

	public long getXP() {
		return _xp;
	}

	public int getNivel() {
		return _nvl;
	}

	public int getSize() {
		return _miembros.size();
	}

	public String analizarMiembrosGM() {
		StringBuilder str = new StringBuilder("");
		for (MiembroGremio GM : _miembros.values()) {
			String enLinea = "0";
			if (GM.getPerso() != null) {
				if (GM.getPerso().enLinea()) {
					enLinea = "1";
				}
			}
			if (str.length() != 0) {
				str.append("|");
			}
			str.append(GM.getID() + ";");
			str.append(GM.getNombre() + ";");
			str.append(GM.getNivel() + ";");
			str.append(GM.getGfx() + ";");
			str.append(GM.getRango() + ";");
			str.append(GM.getXpDonada() + ";");
			str.append(GM.getPorcXpDonada() + ";");
			str.append(GM.getDerechos() + ";");
			str.append(enLinea + ";");
			if (GM.getPerso() != null) {
				str.append(GM.getPerso().getAlineacion() + ";");
			} else {
				str.append("0;");
			}
			str.append(GM.getHorasDeUltimaConeccion());
		}
		return str.toString();
	}

	public ArrayList<Personaje> getPjMiembros() {
		ArrayList<Personaje> a = new ArrayList<>();
		for (MiembroGremio MG : _miembros.values()) {
			a.add(MG.getPerso());
		}
		return a;
	}

	public ArrayList<MiembroGremio> getTodosMiembros() {
		ArrayList<MiembroGremio> a = new ArrayList<>();
		for (MiembroGremio MG : _miembros.values()) {
			a.add(MG);
		}
		return a;
	}

	public MiembroGremio getMiembro(int idMiembro) {
		return _miembros.get(idMiembro);
	}

	public void expulsarMiembro(Personaje perso) {
		Casa h = Casa.getCasaDePj(perso);
		if (h != null) {
			if (Casa.casaGremio(_id) > 0) {
				GestorSQL.ACTUALIZAR_CASA_GREMIO(h, 0, 0);
			}
		}
		_miembros.remove(Integer.valueOf(perso.getID()));
		GestorSQL.BORRAR_MIEMBRO_GREMIO(perso.getID());
	}

	public void addXp(long xp) {
		this._xp += xp;
		while (_xp >= MundoDofus.getXPMaxGremio(_nvl) && _nvl < 200) {
			subirNivel();
		}
	}

	private void subirNivel() {
		this._nvl++;
		this._capital = this._capital + 5;
	}

	public String analizarInfoCercados() {
		byte enclosMax = (byte) Math.floor(_nvl / 10);
		StringBuilder packet = new StringBuilder("" + enclosMax);
		for (Cercado cercados : MundoDofus.todosCercados()) {
			if (cercados.getGremio() == this) {
				packet.append(
						"|" + cercados.getMapa().getID() + ";" + cercados.getTamaÑo() + ";" + cercados.getCantObjMax());
				if (cercados.getListaCriando().size() > 0) {
					packet.append(";");
					boolean primero = false;
					for (Integer monturas : cercados.getListaCriando()) {
						Dragopavo montura = MundoDofus.getDragopavoPorID(monturas);
						if (montura == null) {
							continue;
						}
						if (primero) {
							packet.append(",");
						}
						primero = true;
						if (MundoDofus.getPersonaje(montura.getDueÑo()) == null) {
							packet.append(montura.getColor() + "," + montura.getNombre() + ",SIN DUE�O");
						} else {
							packet.append(montura.getColor() + "," + montura.getNombre() + ","
									+ MundoDofus.getPersonaje(montura.getDueÑo()).getNombre());
						}
					}
				}
			}
		}
		return packet.toString();
	}

	private void decompilarHechizos(String strHechizo) {
		int id;
		int nivel;
		for (String split : strHechizo.split("\\|")) {
			id = Integer.parseInt(split.split(";")[0]);
			nivel = Integer.parseInt(split.split(";")[1]);
			if (MundoDofus.getHechizo(id) != null) {
				_hechizos.put(id, MundoDofus.getHechizo(id).getStatsPorNivel(nivel));
			}
		}
	}

	private void decompilarStats(String statsStr) {
		int id;
		int value;
		for (String split : statsStr.split("\\|")) {
			id = Integer.parseInt(split.split(";")[0]);
			value = Integer.parseInt(split.split(";")[1]);
			_stats.put(id, value);
		}
	}

	public String compilarHechizo() {
		StringBuilder str = new StringBuilder("");
		boolean primero = true;
		for (Entry<Integer, StatsHechizos> statHechizo : _hechizos.entrySet()) {
			if (!primero) {
				str.append("|");
			}
			str.append(statHechizo.getKey() + ";"
					+ ((statHechizo.getValue() == null) ? 0 : statHechizo.getValue().getNivel()));
			primero = false;
		}
		return str.toString();
	}

	public String compilarStats() {
		StringBuilder str = new StringBuilder("");
		boolean primero = true;
		for (Entry<Integer, Integer> stats : _stats.entrySet()) {
			if (!primero) {
				str.append("|");
			}
			str.append(stats.getKey() + ";" + stats.getValue());
			primero = false;
		}
		return str.toString();
	}

	public void actualizarStats(int statsID, int add) {
		int actual = _stats.get(statsID).intValue();
		_stats.put(statsID, (actual + add));
	}

	public int getStats(int statsID) {
		int valor = 0;
		for (Entry<Integer, Integer> tempStats : _stats.entrySet()) {
			if (tempStats.getKey() == statsID) {
				valor = tempStats.getValue();
			}
		}
		return valor;
	}

	public String analizarRecauAGrmio() {
		String packet = getNroRecau() + "|" + MundoDofus.cantRecauDelGremio(getID()) + "|" + 100 * getNivel() + "|"
				+ getNivel() + "|" + getStats(158) + "|" + getStats(176) + "|" + getStats(124) + "|" + getNroRecau()
				+ "|" + getCapital() + "|" + (1000 + (10 * getNivel())) + "|" + compilarHechizo();
		return packet;
	}

	public static class MiembroGremio {
		private int _id;
		private Gremio _gremio;
		private String _nombre;
		private int _nivel;
		private int _gfx;
		private int _rango = 0;
		private byte _porcXpDonada = 0;
		private long _xpDonada = 0;
		private int _derechos = 0;
		private String _ultConeccion;
		private Map<Integer, Boolean> tieneDerecho = new TreeMap<>();

		private MiembroGremio(int id, Gremio gremio, String nombre, int nivel, int gfx, int r, long x, byte pXp,
				int derechos, String ultConeccion) {
			_id = id;
			_gremio = gremio;
			_nombre = nombre;
			_nivel = nivel;
			_gfx = gfx;
			_rango = r;
			_xpDonada = x;
			_porcXpDonada = pXp;
			_derechos = derechos;
			_ultConeccion = ultConeccion;
			convertirDerechosAInt(_derechos);
		}

		public int getGfx() {
			return _gfx;
		}

		public int getNivel() {
			return _nivel;
		}

		public String getNombre() {
			return _nombre;
		}

		public String getVerdaderoNombre() {
			Personaje perso = MundoDofus.getPersonaje(_id);
			if (perso == null) {
				return _nombre;
			}
			_nombre = perso.getNombre();
			return _nombre;
		}

		public int getID() {
			return _id;
		}

		public int getRango() {
			return _rango;
		}

		public Gremio getGremio() {
			return _gremio;
		}

		public String analizarDerechos() {
			return Integer.toString(_derechos, 36);
		}

		public int getDerechos() {
			return _derechos;
		}

		public long getXpDonada() {
			return _xpDonada;
		}

		public int getPorcXpDonada() {
			return _porcXpDonada;
		}

		public String getUltimaConeccino() {
			return _ultConeccion;
		}

		public int getHorasDeUltimaConeccion() {
			String[] strFecha = _ultConeccion.toString().split("~");
			LocalDate ultConeccion = new LocalDate(Integer.parseInt(strFecha[0]), Integer.parseInt(strFecha[1]),
					Integer.parseInt(strFecha[2]));
			LocalDate ahora = new LocalDate();
			return Days.daysBetween(ultConeccion, ahora).getDays() * 24;
		}

		public Personaje getPerso() {
			return MundoDofus.getPersonaje(_id);
		}

		public boolean puede(int derecho) {
			if (this._derechos == 1) {
				return true;
			}
			return tieneDerecho.get(derecho);
		}

		public void setRango(int i) {
			_rango = i;
		}

		public void setTodosDerechos(int rank, byte xp, int right) {
			if (rank == -1) {
				rank = this._rango;
			}
			if (xp < 0) {
				xp = this._porcXpDonada;
			}
			if (xp > 90) {
				xp = 90;
			}
			if (right == -1) {
				right = this._derechos;
			}
			this._rango = rank;
			this._porcXpDonada = xp;
			if (right != this._derechos && right != 1) {
				convertirDerechosAInt(right);
			}
			this._derechos = right;
			GestorSQL.ACTUALIZAR_MIEMBRO_GREMIO(this);
		}

		public void setNivel(int nivel) {
			this._nivel = nivel;
		}

		void darXpAGremio(long xp) {
			_xpDonada += xp;
			_gremio.addXp(xp);
		}

		private void derechosIniciales() {
			tieneDerecho.put(CentroInfo.G_MODIFBOOST, false);
			tieneDerecho.put(CentroInfo.G_MODIFDERECHOS, false);
			tieneDerecho.put(CentroInfo.G_INVITAR, false);
			tieneDerecho.put(CentroInfo.G_BANEAR, false);
			tieneDerecho.put(CentroInfo.G_TODASXPDONADAS, false);
			tieneDerecho.put(CentroInfo.G_SUXPDONADA, false);
			tieneDerecho.put(CentroInfo.G_MODRANGOS, false);
			tieneDerecho.put(CentroInfo.G_PONERRECAUDADOR, false);
			tieneDerecho.put(CentroInfo.G_RECOLECTARRECAUDADOR, false);
			tieneDerecho.put(CentroInfo.G_USARCERCADOS, false);
			tieneDerecho.put(CentroInfo.G_MEJORARCERCADOS, false);
			tieneDerecho.put(CentroInfo.G_OTRASMONTURAS, false);
		}

		private void convertirDerechosAInt(int total) {
			if (tieneDerecho.isEmpty()) {
				derechosIniciales();
			}
			if (total == 1) {
				return;
			}
			if (tieneDerecho.size() > 0) {
				tieneDerecho.clear();
			}
			derechosIniciales();
			Integer[] mapKey = tieneDerecho.keySet().toArray(new Integer[tieneDerecho.size()]);
			while (total > 0) {
				for (int i = tieneDerecho.size() - 1; i < tieneDerecho.size(); i--) {
					if (mapKey[i].intValue() <= total) {
						total ^= mapKey[i].intValue();
						tieneDerecho.put(mapKey[i], true);
						break;
					}
				}
			}
		}

		public void setUltConeccion(String ultConeccion) {
			_ultConeccion = ultConeccion;
		}
	}
}
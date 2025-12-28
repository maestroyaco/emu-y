package variables;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;

import estaticos.CentroInfo;
import estaticos.Emu;
import estaticos.Formulas;
import estaticos.MundoDofus;
import estaticos.MundoDofus.Drop;
import variables.Hechizo.StatsHechizos;
import variables.Mapa.Celda;
import variables.Pelea.Luchador;
import variables.Personaje.Stats;

public class MobModelo {
	private int _ID;
	private int _gfxID;
	private int _alineacion;
	private String _colores;
	private int _tipoIA = 0;
	private int _minKamas;
	private int _maxKamas;
	private Map<Integer, MobGrado> _grados = new TreeMap<>();
	private ArrayList<Drop> _drops = new ArrayList<>();
	private boolean _esCapturable;
	private int _talla;
	private String _nombre;

	public static class GrupoMobs {
		private int _id;
		private int _celdaID;
		private int _orientacion = 3;
		private int _alin = -1;
		private int _distanciaAgresion = 0;
		private int _estrellas = 0;
		private boolean _esFixeado = false;
		private Map<Integer, MobGrado> _mobs = new TreeMap<>();
		// private Map<Integer, Integer> _almas = new TreeMap<Integer, Integer>();
		private String condicion = "";
		// private Timer _tiempoCondicion;
		private String _strGrupoMob = "";

		GrupoMobs(int Aid, int Alineacion, ArrayList<MobGrado> posiblesMobs, Mapa Mapa, int celda, int maxMobs) {
			_id = Aid;
			_alin = Alineacion;
			int rand = 0;
			int nroMobs = 0;
			switch (maxMobs) {
			case 0:
				nroMobs = 1;
				return;
			case 1:
				nroMobs = 1;
				break;
			case 2:
				nroMobs = Formulas.getRandomValor(1, 2);
				break;
			case 3:
				nroMobs = Formulas.getRandomValor(1, 3);
				break;
			case 4:
				rand = Formulas.getRandomValor(0, 99);
				if (rand < 22) {
					nroMobs = 1;
				} else if (rand < 48) {
					nroMobs = 2;
				} else if (rand < 74) {
					nroMobs = 3;
				} else {
					nroMobs = 4;
				}
				break;
			case 5:
				rand = Formulas.getRandomValor(0, 99);
				if (rand < 15) {
					nroMobs = 1;
				} else if (rand < 35) {
					nroMobs = 2;
				} else if (rand < 60) {
					nroMobs = 3;
				} else if (rand < 85) {
					nroMobs = 4;
				} else {
					nroMobs = 5;
				}
				break;
			case 6:
				rand = Formulas.getRandomValor(0, 99);
				if (rand < 10) {
					nroMobs = 1;
				} else if (rand < 25) {
					nroMobs = 2;
				} else if (rand < 45) {
					nroMobs = 3;
				} else if (rand < 65) {
					nroMobs = 4;
				} else if (rand < 85) {
					nroMobs = 5;
				} else {
					nroMobs = 6;
				}
				break;
			case 7:
				rand = Formulas.getRandomValor(0, 99);
				if (rand < 9) {
					nroMobs = 1;
				} else if (rand < 20) {
					nroMobs = 2;
				} else if (rand < 35) {
					nroMobs = 3;
				} else if (rand < 55) {
					nroMobs = 4;
				} else if (rand < 75) {
					nroMobs = 5;
				} else if (rand < 91) {
					nroMobs = 6;
				} else {
					nroMobs = 7;
				}
				break;
			default:
				rand = Formulas.getRandomValor(0, 99);
				if (rand < 9) {
					nroMobs = 1;
				} else if (rand < 20) {
					nroMobs = 2;
				} else if (rand < 33) {
					nroMobs = 3;
				} else if (rand < 50) {
					nroMobs = 4;
				} else if (rand < 67) {
					nroMobs = 5;
				} else if (rand < 80) {
					nroMobs = 6;
				} else if (rand < 91) {
					nroMobs = 7;
				} else {
					nroMobs = 8;
				}
				break;
			}
			boolean tieneMismaAlineacion = false;
			for (MobGrado mob : posiblesMobs) {
				if (mob.getModelo().getAlineacion() == _alin) {
					tieneMismaAlineacion = true;
				}
			}
			if (!tieneMismaAlineacion) {
				return;
			}
			int idMob = -1;
			int maxNivel = 0;
			for (int a = 0; a < nroMobs; a++) {
				MobGrado mob = null;
				do {
					int random = Formulas.getRandomValor(0, posiblesMobs.size() - 1);
					mob = posiblesMobs.get(random).getCopy();
				} while (mob.getModelo().getAlineacion() != _alin);
				if (mob.getNivel() > maxNivel) {
					maxNivel = mob.getNivel();
				}
				// int idModeloMob = mob.getModelo().getID();
				/*
				 * if (_almas.containsKey(idModeloMob)) { int valor = _almas.get(idModeloMob);
				 * _almas.remove(idModeloMob); _almas.put(idModeloMob, valor + 1); } else {
				 * _almas.put(idModeloMob, 1); }
				 */
				_mobs.put(idMob, mob);
				idMob--;
			}
			// _distanciaAgresion = CentroInfo.agresionPorNivel(maxNivel);
			_distanciaAgresion = 0;
			if (_alin != CentroInfo.ALINEACION_NEUTRAL) {
				_distanciaAgresion = 5;
			}
			_celdaID = (celda == -1 ? Mapa.getRandomCeldaIDLibre() : celda);
			Celda cel = Mapa.getCelda(_celdaID);
			if (cel == null) {
				_celdaID = getCeldaBuena(Mapa);
			} else {
				if (!cel.esCaminable(false)) {
					_celdaID = getCeldaBuena(Mapa);
				}
			}
			if (!cel.esCaminable(false)) {
				_celdaID = getCeldaBuena(Mapa);
			}
			if (_celdaID == 0) {
				return;
			}
			_orientacion = Formulas.getRandomValor(0, 3) * 2;
			_esFixeado = false;
			_estrellas = 0;
		}

		private int getCeldaBuena(Mapa mapa) {
			ArrayList<Short> celdabuena = mapa.getPosicionesAleatorias2();
			if (celdabuena != null && celdabuena.size() > 3) {
				Random rand1 = new Random();
				int randomCelda = celdabuena.get(rand1.nextInt(celdabuena.size()));
				Celda cel = mapa.getCelda(randomCelda);
				if (!cel.esCaminable(false)) {
					return mapa.getRandomCeldaIDLibre();
				} else {
					return randomCelda;
				}
			} else {
				return mapa.getRandomCeldaIDLibre();
			}
		}

		GrupoMobs(int idGrupoMob, int celdaID, String grupo) {
			int maxNivel = 0;
			_id = idGrupoMob;
			_alin = -1;
			_celdaID = celdaID;
			_distanciaAgresion = CentroInfo.agresionPorNivel(maxNivel);
			_esFixeado = true;
			_strGrupoMob = grupo;
			int idMob = -1;
			for (String data : grupo.split(";")) {
				String[] infos = data.split(",");
				try {
					int idMobModelo = Integer.parseInt(infos[0]);
					MobModelo m = MundoDofus.getMobModelo(idMobModelo);
					int min = 1;
					try {
						min = Integer.parseInt(infos[1]);
					} catch (Exception e) {
						min = m.getRandomGrado().getNivel();
					}
					int max = min;
					try {
						max = Integer.parseInt(infos[2]);
					} catch (Exception e) {
						max = min;
					}
					List<MobGrado> mgs = new ArrayList<>();
					for (MobGrado MG : m.getGrados().values()) {
						if (MG._nivel >= min && MG._nivel <= max) {
							mgs.add(MG);
						}
					}
					if (mgs.isEmpty()) {
						continue;
					}
					_mobs.put(idMob, mgs.get(Formulas.getRandomValor(0, mgs.size() - 1)));
					idMob--;
				} catch (Exception e) {
					continue;
				}
			}
			_orientacion = (Formulas.getRandomValor(0, 3) * 2) + 1;// 3 kralamar
		}

		public String getStrGrupoMob() {
			return _strGrupoMob;
		}

		public int getID() {
			return _id;
		}

		public int getCeldaID() {
			return _celdaID;
		}

		public int getOrientacion() {
			return _orientacion;
		}

		public int getDistanciaAgresion() {
			return _distanciaAgresion;
		}

		boolean esFixeado() {
			return _esFixeado;
		}

		public void setOrientacion(int o) {
			_orientacion = 0;
		}

		public void setCeldaID(int id) {
			_celdaID = id;
		}

		public int getAlineamiento() {
			return _alin;
		}

		public MobGrado getMobGradoPorID(int id) {
			return _mobs.get(id);
		}

		public int getTamaÑo() {
			return _mobs.size();
		}

		public String enviarGM(Personaje perso) {
			StringBuilder mobIDs = new StringBuilder();
			StringBuilder mobGFX = new StringBuilder();
			StringBuilder mobNiveles = new StringBuilder();
			StringBuilder colores = new StringBuilder();
			boolean primero = true;
			if (_mobs.isEmpty()) {
				return "";
			}
			long experiencia = 0L;
			for (Entry<Integer, MobGrado> entry : _mobs.entrySet()) {
				if (!primero) {
					mobIDs.append(",");
					mobGFX.append(",");
					mobNiveles.append(",");
				}
				MobModelo mobMod = entry.getValue().getModelo();
				mobIDs.append(mobMod.getID());
				mobGFX.append(mobMod.getGfxID() + "^" + mobMod.getTalla());
				mobNiveles.append(entry.getValue().getNivel());
				colores.append(mobMod.getColores() + ";0,0,0,0;");
				experiencia += entry.getValue()._baseXp;
				primero = false;
			}
			if (!Emu.Maldicion) {
				experiencia *= Emu.RATE_XP_PVM;
			} else {
				experiencia *= Math.round((Emu.RATE_XP_PVM / 2));
			}
			experiencia *= (getEstrellas() + 100) / 100;
			experiencia = Math.round(experiencia * _mobs.size());
			return "+" + _celdaID + ";" + _orientacion + ";" + _estrellas + ";" + _id + ";" + mobIDs.toString() + ";-3;"
					+ mobGFX.toString() + ";" + mobNiveles.toString() + ";" + colores.toString() + ";" + experiencia;
		}

		public int getEstrellas() {
			if (_estrellas == 0) {
				_estrellas = MundoDofus.getRandomEstrellas();
			}
			return _estrellas;
		}

		public void setEstrellas(int i) {
			_estrellas = i;
			if (_estrellas > 600) {
				_estrellas = 0;
			}
		}

		void aumentarEstrellas() {
			if (_estrellas == 0) {
				_estrellas = 1;
				return;
			}
			_estrellas += 25;
			if (_estrellas > 600) {
				_estrellas = 0;
			}
		}

		public Map<Integer, MobGrado> getMobs() {
			return _mobs;
		}

		/*
		 * public Map<Integer, Integer> getAlmasMobs () { return _almas; }
		 */

		public void setCondicion(String cond) {
			condicion = cond;
		}

		public String getCondicion() {
			return condicion;
		}

		public void setEsFixeado(boolean fix) {
			_esFixeado = fix;
		}

		/*
		 * void inicioTiempoCondicion () { _tiempoCondicion = new Timer();
		 * _tiempoCondicion.schedule(new TimerTask() { public void run () { condicion =
		 * ""; } }, Emu.TIEMPO_ARENA); }
		 *
		 * public void stopConditionTimer () { try { _tiempoCondicion.cancel(); } catch
		 * (Exception e) {} }
		 */
	}

	public static class MobGrado {
		private MobModelo _modelo;
		private int _grado;
		private int _nivel;
		private int _PDV;
		private int _idEnPelea;
		private int _PDVMAX;
		private int _iniciativa;
		private int _PA;
		private int _PM;
		private Celda celdaPelea;
		private int _baseXp = 10;
		private Map<Integer, Integer> _stats = new TreeMap<>();
		private Map<Integer, StatsHechizos> _hechizos = new TreeMap<>();

		private MobGrado(MobModelo modelo, int grado, int nivel, int PA, int PM, String Aresist, String Astats,
				String Aspells, int pdvMax, int iniciativa, int xp) {
			_modelo = modelo;
			_grado = grado;
			_nivel = nivel;
			_PDVMAX = pdvMax;
			_PDV = _PDVMAX;
			_PA = PA;
			_PM = PM;
			if (xp > 15000000) {
				if (_PDV > 10000) {
					xp = Formulas.getRandomValor(10000000, 15000000);
				} else {
					xp = Formulas.getRandomValor(8000000, 10000000);
				}
			}
			_baseXp = xp;
			_iniciativa = iniciativa;
			String[] resistencias = Aresist.split(",");
			String[] statsArray = Astats.split(",");
			int RNeutral = 0, RFuego = 0, RAgua = 0, RAire = 0, RTierra = 0, EPA = 0, EPM = 0, fuerza = 0,
					inteligencia = 0, sabiduria = 0, suerte = 0, agilidad = 0, daÑoscc = 0;
			try {
				RNeutral = Integer.parseInt(resistencias[1]);
				RTierra = Integer.parseInt(resistencias[2]);
				RFuego = Integer.parseInt(resistencias[3]);
				RAgua = Integer.parseInt(resistencias[4]);
				RAire = Integer.parseInt(resistencias[5]);
				EPA = Integer.parseInt(resistencias[6]);
				EPM = Integer.parseInt(resistencias[7]);
			} catch (Exception e) {
				e.printStackTrace();
			}
			_stats.clear();
			if (!Astats.contains(":")) {
				try {
					fuerza = Integer.parseInt(statsArray[0]);
					sabiduria = Integer.parseInt(statsArray[1]);
					inteligencia = Integer.parseInt(statsArray[2]);
					suerte = Integer.parseInt(statsArray[3]);
					agilidad = Integer.parseInt(statsArray[4]);
				} catch (Exception e) {
				}
				try {
					daÑoscc = Integer.parseInt(statsArray[5]);
				} catch (Exception e) {
				}
				_stats.put(118, fuerza);
				_stats.put(124, sabiduria);
				_stats.put(126, inteligencia);
				_stats.put(123, suerte);
				_stats.put(119, agilidad);
				_stats.put(112, daÑoscc);
			}
			_stats.put(CentroInfo.STATS_ADD_ResPorc_NEUTRAL, RNeutral);
			_stats.put(CentroInfo.STATS_ADD_ResPorc_FUEGO, RFuego);
			_stats.put(CentroInfo.STATS_ADD_ResPorc_AGUA, RAgua);
			_stats.put(CentroInfo.STATS_ADD_ResPorc_AIRE, RAire);
			_stats.put(CentroInfo.STATS_ADD_ResPorc_TIERRA, RTierra);
			_stats.put(CentroInfo.STATS_ADD_ProbPerdida_PA, EPA);
			_stats.put(CentroInfo.STATS_ADD_ProbPerdida_PM, EPM);
			_stats.put(111, PA);
			_stats.put(128, PM);
			for (String stat : statsArray) {
				int statid = 0;
				int valor = 0;
				try {
					statid = Integer.parseInt(stat.split(":")[0]);
					valor = Integer.parseInt(stat.split(":")[1]);
				} catch (Exception e) {
					continue;
				}
				if (statid == 111 || statid == 128) {
					continue;
				}
				if (_modelo.getID() == 423) {
					if (statid == CentroInfo.STATS_ADD_CRIATURAS_INVO || statid == 105) {
						continue;
					}
				}
				if (!_stats.containsKey(statid)) {
					_stats.put(statid, valor);
				}
			}
			if (_modelo.getID() == 423) {
				_stats.put(CentroInfo.STATS_ADD_CRIATURAS_INVO, 4);
				_stats.put(105, 100);
			} else {
				if (!_stats.containsKey(CentroInfo.STATS_ADD_CRIATURAS_INVO)) {
					_stats.put(CentroInfo.STATS_ADD_CRIATURAS_INVO, 2);
				}
			}
			_hechizos.clear();
			String[] spellsArray = Aspells.split(";");
			for (String str : spellsArray) {
				if (str.equals("")) {
					continue;
				}
				String[] spellInfo = str.split("@");
				int hechizoID = 0;
				int hechizoNivel = 0;
				try {
					hechizoID = Integer.parseInt(spellInfo[0]);
					hechizoNivel = Integer.parseInt(spellInfo[1]);
				} catch (Exception e) {
					continue;
				}
				if (hechizoID == 0 || hechizoNivel == 0) {
					continue;
				}
				Hechizo hechizo = MundoDofus.getHechizo(hechizoID);
				if (hechizo == null) {
					continue;
				}
				StatsHechizos hechizoStats = hechizo.getStatsPorNivel(hechizoNivel);
				if (hechizoStats == null) {
					continue;
				}
				_hechizos.put(hechizoID, hechizoStats);
			}
		}

		private MobGrado(MobModelo modelo, int grado, int nivel, int pdv, int pdvmax, int PA, int PM,
				Map<Integer, Integer> stats, Map<Integer, StatsHechizos> hechizos, int iniciativa, int xp) {
			_modelo = modelo;
			_grado = grado;
			_nivel = nivel;
			_PDV = pdv;
			_PDVMAX = pdvmax;
			_PA = PA;
			_PM = PM;
			_iniciativa = iniciativa;
			_stats = stats;
			_hechizos = hechizos;
			_idEnPelea = -1;
			_baseXp = xp;
		}

		public int getBaseXp() {
			return _baseXp;
		}

		public int getIniciativa() {
			return _iniciativa;
		}

		public MobGrado getCopy() {
			Map<Integer, Integer> nuevoStats = new TreeMap<>();
			nuevoStats.putAll(_stats);
			return new MobGrado(_modelo, _grado, _nivel, _PDV, _PDVMAX, _PA, _PM, nuevoStats, _hechizos, _iniciativa,
					_baseXp);
		}

		public Stats getStats() {
			return new Stats(_stats);
		}

		public int getNivel() {
			return _nivel;
		}

		public Celda getCeldaPelea() {
			return celdaPelea;
		}

		public void setCeldaPelea(Celda celda) {
			celdaPelea = celda;
		}

		public Map<Integer, StatsHechizos> getHechizos() {
			return _hechizos;
		}

		public MobModelo getModelo() {
			return _modelo;
		}

		public int getPDV() {
			return _PDV;
		}

		public void setPDV(int pdv) {
			_PDV = pdv;
		}

		public int getPDVMAX() {
			return _PDVMAX;
		}

		public int getGrado() {
			return _grado;
		}

		public void setIdEnPelea(int i) {
			_idEnPelea = i;
		}

		public int getIdEnPelea() {
			return _idEnPelea;
		}

		public int getPA() {
			return _PA;
		}

		public int getPM() {
			return _PM;
		}

		void modificarStatsPorInvocador(Luchador invocador) {
			float coefStats = 0;
			float coefVita = 0;
			while (invocador.esInvocacion()) {
				invocador = invocador.getInvocador();
				coefStats -= 0.2f;
				coefVita -= 0.2f;
			}
			if (invocador.getPersonaje() != null) {
				coefStats += invocador.getNivel() / 250F;
				coefVita += invocador.getNivel() / 150F;
				if (invocador.getPersonaje().getClase(true) == 2) {
					coefStats += 4.1f;
					coefVita += 4.5f;
				} else {
					coefStats += 2.7f;
					coefVita += 3.5f;
				}
			} else {
				coefStats += invocador.getNivel() / 200F;
				coefVita += invocador.getNivel() / 100F;
			}
			Stats stats = invocador.getTotalStatsSinBuff();
			_PDV = (int) (_PDVMAX * coefVita) + (stats.getEfecto(125) / 30);
			if (_PDV < 1) {
				_PDV = 1;
			}
			_PDVMAX = _PDV;
			if (_stats.get(118) != null) {
				int fuerza = (int) ((_stats.get(118)) + (stats.getEfecto(118) * coefStats));
				_stats.remove(118);
				_stats.put(118, fuerza);
			} else {
				int fuerza = (int) (stats.getEfecto(118) * coefStats);
				_stats.put(118, fuerza);
			}
			if (_stats.get(119) != null) {
				int agilidad = (int) ((_stats.get(119)) + (stats.getEfecto(119) * coefStats));
				_stats.remove(119);
				_stats.put(119, agilidad);
			} else {
				int agilidad = (int) (stats.getEfecto(119) * coefStats);
				_stats.put(119, agilidad);
			}
			if (_stats.get(126) != null) {
				int inteligencia = (int) ((_stats.get(126)) + (stats.getEfecto(126) * coefStats));
				_stats.remove(126);
				_stats.put(126, inteligencia);
			} else {
				int inteligencia = (int) (stats.getEfecto(126) * coefStats);
				_stats.put(126, inteligencia);
			}
			if (_stats.get(124) != null) {
				int sabiduria = (int) ((_stats.get(124)) + (stats.getEfecto(124) * coefStats));
				_stats.remove(124);
				_stats.put(124, sabiduria);
			} else {
				int sabiduria = (int) (stats.getEfecto(124) * coefStats);
				_stats.put(124, sabiduria);
			}
			if (_stats.get(123) != null) {
				int suerte = (int) ((_stats.get(123)) + (stats.getEfecto(123) * coefStats));
				_stats.remove(123);
				_stats.put(123, suerte);
			} else {
				int suerte = (int) (stats.getEfecto(123) * coefStats);
				_stats.put(123, suerte);
			}
			if (_stats.get(112) != null) {
				int suerte = (int) ((_stats.get(112)) + (stats.getEfecto(112) * coefStats));
				_stats.remove(112);
				int newv = Math.round(suerte / 4);
				if (newv > 0) {
					_stats.put(112, newv);
				}
			} else {
				int suerte = (int) (stats.getEfecto(112) * coefStats);
				int newv = Math.round(suerte / 4);
				if (newv > 0) {
					_stats.put(112, newv);
				}
			}
			if (_stats.get(178) != null) {
				int suerte = (int) ((_stats.get(178)) + (stats.getEfecto(178) * coefStats));
				_stats.remove(178);
				_stats.put(178, suerte);
			} else {
				int suerte = (int) (stats.getEfecto(178) * coefStats);
				_stats.put(178, suerte);
			}
		}
	}

	public MobModelo(int id, String nombre, int gfx, int alineacion, String colores, String grados, String hechizos,
			String stats, String pdvs, String puntos, String init, int mK, int MK, String xpstr, int tipoIA,
			boolean capturable, int talla) {
		_ID = id;
		_gfxID = gfx;
		_alineacion = alineacion;
		_colores = colores;
		if (MK > 24000) {
			MK = 24000;
		}
		if (mK > MK) {
			mK = MK;
		}
		_minKamas = mK;
		_maxKamas = MK;
		_tipoIA = tipoIA;
		_esCapturable = capturable;
		_talla = talla;
		_nombre = nombre;
		String[] aGrados = grados.split("\\|");
		String[] aStats = stats.split("\\|");
		String[] aHechizos = hechizos.split("\\|");
		String[] aPuntos = puntos.split("\\|");
		String[] aExp = xpstr.split("\\|");
		String[] aPDV = pdvs.split("\\|");
		String[] aIniciativa = init.split("\\|");
		byte grado = 1, PA = 6, PM = 3;

		int tempPDV = 1, tempIniciativa = 0, tempExp = 0;
		String tempHechizo = "", tempResistNivel = "", tempStats = "";
		for (int n = 0; n < aGrados.length; n++) {
			int nivel = 1;
			try {
				tempResistNivel = aGrados[n].split("@")[1];
				if (tempResistNivel.contains(";")) {
					nivel = Integer.parseInt(aGrados[n].split("@")[0]);
					tempResistNivel = nivel + "," + tempResistNivel.replace(";", ",");
				} else {
					nivel = Integer.parseInt(tempResistNivel.split(",")[0]);
				}
			} catch (Exception e) {
			}
			if (!tempResistNivel.isEmpty()) {
				try {
					tempExp = Integer.parseInt(aExp[n]);
				} catch (Exception exception) {
				}
				try {
					tempStats = aStats[n];
				} catch (Exception exception) {
				}
				try {
					tempHechizo = aHechizos[n];
				} catch (Exception exception) {
				}
				try {
					tempPDV = Integer.parseInt(aPDV[n]);
				} catch (Exception exception) {
				}
				try {
					tempIniciativa = Integer.parseInt(aIniciativa[n]);
				} catch (Exception exception) {
				}
				try {
					PA = Byte.parseByte(aPuntos[n].split(";")[0]);
				} catch (Exception exception) {
				}
				try {
					PM = Byte.parseByte(aPuntos[n].split(";")[1]);
				} catch (Exception exception) {
				}
				_grados.put((int) grado, new MobGrado(this, grado, nivel, PA, PM, tempResistNivel, tempStats,
						tempHechizo, tempPDV, tempIniciativa, tempExp));
				grado = (byte) (grado + 1);
			}
		}
	}

	public int getID() {
		return _ID;
	}

	public void addDrop(Drop D) {
		_drops.add(D);
	}

	public int getTalla() {
		return _talla;
	}

	public ArrayList<Drop> getDrops() {
		return _drops;
	}

	public int getGfxID() {
		return _gfxID;
	}

	public int getMinKamas() {
		return _minKamas;
	}

	public int getMaxKamas() {
		return _maxKamas;
	}

	public int getAlineacion() {
		return _alineacion;
	}

	public String getColores() {
		return _colores;
	}

	public int getTipoInteligencia() {
		return _tipoIA;
	}

	public void setTipoInteligencia(int IA) {
		_tipoIA = IA;
	}

	public Map<Integer, MobGrado> getGrados() {
		return _grados;
	}

	MobGrado getGradoPorNivel(int nivel) {
		for (Entry<Integer, MobGrado> grado : _grados.entrySet()) {
			if (grado.getValue().getNivel() == nivel) {
				return grado.getValue();
			}
		}
		return null;
	}

	public MobGrado getRandomGrado() {
		int cantgrados = _grados.size();
		int randomgrade = (int) (Math.random() * (cantgrados - 1)) + 1;
		int graderandom = 1;
		for (Entry<Integer, MobGrado> grade : _grados.entrySet()) {
			if (graderandom == randomgrade) {
				return grade.getValue();
			} else {
				graderandom++;
			}
		}
		return null;
	}

	public MobGrado getRandomGradoPrimero() {
		for (Entry<Integer, MobGrado> grade : _grados.entrySet()) {
			return grade.getValue();
		}
		return null;
	}

	boolean esCapturable() {
		return _esCapturable;
	}

	public String getNombre() {
		return _nombre;
	}
}
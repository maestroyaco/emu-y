package variables;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import estaticos.Camino;
import estaticos.CentroInfo;
import estaticos.Formulas;
import estaticos.MundoDofus;
import variables.Mapa.Celda;
import variables.Pelea.Luchador;

public class Hechizo {
	private String _nombre;
	private int _ID;
	private int _spriteID;
	private String _spriteInfos;// tipo lanz, anim pj, 1 o 0 (frente al sprite)
	private Map<Integer, StatsHechizos> _statsHechizos = new TreeMap<>();
	private ArrayList<Integer> _afectadosEstandar = new ArrayList<>();
	private ArrayList<Integer> _GCafectadosEstandar = new ArrayList<>();
	public int _duracion = 800;
	public int _tipo = 0;

	public Hechizo(int aHechizoID, String aNombre, int aSpriteID, String aSpriteInfos, String afectados, int duracion,
			int tipo) {
		_ID = aHechizoID;
		_nombre = aNombre;
		_spriteID = aSpriteID;
		_spriteInfos = aSpriteInfos;
		afectados = afectados.replace("\\|", ":").replace(",", ";");
		String nET = afectados.split(":")[0];
		String ccET = "";
		if (afectados.split(":").length > 1) {
			ccET = afectados.split(":")[1];
		}
		for (String num : nET.split(";")) {
			try {
				_afectadosEstandar.add(Integer.parseInt(num));
			} catch (Exception e) {
				_afectadosEstandar.add(0);
				continue;
			}
		}
		for (String num : ccET.split(";")) {
			try {
				_GCafectadosEstandar.add(Integer.parseInt(num));
			} catch (Exception e) {
				_GCafectadosEstandar.add(0);
				continue;
			}
		}
		_duracion = duracion;
		_tipo = tipo;
	}

	public void setAfectados(String afectados) {
		_afectadosEstandar.clear();
		_GCafectadosEstandar.clear();
		afectados = afectados.replace("|", ":").replace(",", ";");
		String nET = afectados.split(":")[0];
		String ccET = "";
		if (afectados.split(":").length > 1) {
			ccET = afectados.split(":")[1];
		}
		for (String num : nET.split(";")) {
			try {
				_afectadosEstandar.add(Integer.parseInt(num));
			} catch (Exception e) {
				_afectadosEstandar.add(0);
				continue;
			}
		}
		for (String num : ccET.split(";")) {
			try {
				_GCafectadosEstandar.add(Integer.parseInt(num));
			} catch (Exception e) {
				_GCafectadosEstandar.add(0);
				continue;
			}
		}
	}

	public ArrayList<Integer> getAfectadosEstandar() {
		return _afectadosEstandar;
	}

	public int getSpriteID() {
		return _spriteID;
	}

	public String getSpriteInfos() {
		return _spriteInfos;
	}

	public String getNombre() {
		return _nombre;
	}

	public int getID() {
		return _ID;
	}

	public StatsHechizos getStatsPorNivel(int lvl) {
		return _statsHechizos.get(lvl);
	}

	public void addStatsHechizos(Integer lvl, StatsHechizos stats) {
		if (_statsHechizos.get(lvl) != null) {
			return;
		}
		_statsHechizos.put(lvl, stats);
	}

	public static class StatsHechizos {
		private int _hechizoID;// id hechizo
		private int _nivel;// nivel
		private int _costePA;// coste de PA
		private int _minAlc;// minimo alcance
		private int _maxAlc;// maximo alcance
		private int _porcGC;// probabilidad de golpe critico
		private int _porcFC;// probabilidad de fallo critico
		private boolean _esLanzLinea;// lanzar en linea
		private boolean _tieneLineaVuelo;// linea de vuelo
		private boolean _esCeldaVacia;// celda vacia
		private boolean _esModifAlc;// alcance modificalble
		private int _maxLanzPorTurno;// cantidad de veces por turno
		private int _maxLanzPorObjetivo;// cantidad de veces por objetivo
		private int _sigLanzamiento;// cantidad de turnos para volver a lanzar el hechizo
		private int _reqLevel;// nivel requerido
		private boolean _esFinTurnoSiFC;// si falla, es final del turno
		private ArrayList<EfectoHechizo> _efectos;// efectos
		private ArrayList<EfectoHechizo> _efectosGC;// efectos critico
		private String _afectados;// genera un estado, tipo portador
		private Hechizo hechizo = null;
		public ArrayList<Integer> efectosID = new ArrayList<>();

		public StatsHechizos(int hechizoID, int nivel, int costePA, int minAlc, int maxAlc, int porcGC, int porcFC,
				boolean esLanzLinea, boolean tieneLineaVuelo, boolean esCeldaVacia, boolean esModifAlc,
				int maxLanzPorTurno, int maxLanzPorObjetivo, int sigLanzamiento, int reqLevel, boolean esFinTurnoSiFC,
				String efectos, String efectosGC, String afectados) {
			_hechizoID = hechizoID;// ID
			_nivel = nivel;// nivel
			_costePA = costePA;// coste de PA
			_minAlc = minAlc;// minimo alcance
			_maxAlc = maxAlc;// maximo alcance
			_porcGC = porcGC;// tasa de golpe critico
			_porcFC = porcFC;// tasa de fallo critico
			_esLanzLinea = esLanzLinea;// lanzado en linea
			_tieneLineaVuelo = tieneLineaVuelo;// linea de vuelo
			_esCeldaVacia = esCeldaVacia;// celda libre
			_esModifAlc = esModifAlc;// alcance modificable
			_maxLanzPorTurno = maxLanzPorTurno;// cantidad de veces por turno
			_maxLanzPorObjetivo = maxLanzPorObjetivo;// cantidad de veces por objetivo
			_sigLanzamiento = sigLanzamiento;// cantidad de turnos para volver a lanzar el hechizo
			_reqLevel = reqLevel;// nivel requerido
			_esFinTurnoSiFC = esFinTurnoSiFC;// si es fallo critico , final de turno
			_efectos = analizarEfectos(efectos);// efectos
			_efectosGC = analizarEfectos(efectosGC);// efectos criticos
			_afectados = afectados;// tipo portador de algun estado
		}

		private ArrayList<EfectoHechizo> analizarEfectos(String e) {
			ArrayList<EfectoHechizo> effets = new ArrayList<>();
			String[] splt = e.split("\\|");
			for (String a : splt) {
				try {
					if (e.equals("-1")) {
						continue;
					}
					int id = 0;
					try {
						id = Integer.parseInt(a.split(";", 2)[0]);
					} catch (Exception f) {
						continue;
					}
					String args = a.split(";", 2)[1];
					if (!efectosID.contains(id)) {
						efectosID.add(id);
					}
					effets.add(new EfectoHechizo(id, args, _hechizoID, _nivel));
				} catch (Exception f) {
					f.printStackTrace();
					System.exit(1);
				}
			}
			return effets;
		}

		public int getHechizoID() {
			return _hechizoID;
		}

		public int getNivel() {
			return _nivel;
		}

		public Hechizo getHechizo() {
			if (hechizo == null) {
				hechizo = MundoDofus.getHechizo(_hechizoID);
				return hechizo;
			} else {
				return hechizo;
			}
		}

		public int getSpriteID() {
			return getHechizo().getSpriteID();
		}

		public String getSpriteInfos() {
			return getHechizo().getSpriteInfos();
		}

		public int getCostePA() {
			return _costePA;
		}

		public int getMinAlc() {
			return _minAlc;
		}

		public int getMaxAlc() {
			return _maxAlc;
		}

		public int getPorcGC() {
			return _porcGC;
		}

		public int getPorcFC() {
			return _porcFC;
		}

		public boolean esLanzLinea() {
			return _esLanzLinea;
		}

		public boolean tieneLineaVuelo() {
			return _tieneLineaVuelo;
		}

		public boolean esCeldaVacia() {
			return _esCeldaVacia;
		}

		public boolean esModifAlc() {
			return _esModifAlc;
		}

		public int getMaxLanzPorTurno() {
			return _maxLanzPorTurno;
		}

		public int getMaxLanzPorJugador() {
			return _maxLanzPorObjetivo;
		}

		public int getSigLanzamiento() {
			return _sigLanzamiento;
		}

		public int getReqNivel() {
			return _reqLevel;
		}

		public boolean esFinTurnoSiFC() {
			return _esFinTurnoSiFC;
		}

		public ArrayList<EfectoHechizo> getEfectos() {
			return _efectos;
		}

		public ArrayList<EfectoHechizo> getEfectosGC() {
			return _efectosGC;
		}

		public String getAfectados() {
			return _afectados;
		}

		void aplicaTrampaAPelea(Pelea pelea, Luchador lanzador, Celda celda, ArrayList<Celda> celdas, boolean esGC) {
			ArrayList<EfectoHechizo> efectosH;
			if (esGC) {
				efectosH = _efectosGC;
			} else {
				efectosH = _efectos;
			}
			int azar = Formulas.getRandomValor(0, 99);
			int suerte = 0;
			for (EfectoHechizo EH : efectosH) {
				if (EH.getSuerte() != 0 && EH.getSuerte() != 100) {
					if (azar <= suerte || azar >= (EH.getSuerte() + suerte)) {
						suerte += EH.getSuerte();
						continue;
					}
					suerte += EH.getSuerte();
				}
				ArrayList<Luchador> ojetivos = EfectoHechizo.getAfectados(pelea, celdas, _hechizoID);
				EH.aplicarHechizoAPelea(pelea, lanzador, celda, ojetivos, celdas, false);
			}
		}

		void aplicaHechizoAPelea(Pelea pelea, Luchador lanzador, Celda celda, boolean esGC, boolean glifo) {
			ArrayList<EfectoHechizo> efectosH;
			if (esGC) {
				efectosH = _efectosGC;
			} else {
				efectosH = _efectos;
			}
			int suerte = 0;
			int num = 0;
			int azar = Formulas.getRandomValor(0, 99);
			for (EfectoHechizo EH : efectosH) {
				if (pelea.getEstado() >= CentroInfo.PELEA_ESTADO_FINALIZADO) {
					return;
				}
				if (EH.getSuerte() != 0 && EH.getSuerte() != 100) {
					if (azar <= suerte || azar > (EH.getSuerte() + suerte)) {
						suerte += EH.getSuerte();// formula desbugeada de ruleta
						num++;// sirve para desbugear la ruleta
						continue;
					}
					suerte += EH.getSuerte();
				}
				int tipoAlcance = num * 2;
				if (esGC) {
					tipoAlcance += _efectos.size() * 2;
				}
				ArrayList<Celda> celdas = Camino.getCeldasAfectadasEnElArea(pelea.getMapaCopia(), celda.getID(),
						lanzador.getCeldaPelea().getID(), _afectados, tipoAlcance, esGC);
				EfectoHechizo.celdasarea = celdas;
				ArrayList<Celda> celdasFinales = new ArrayList<>();
				int afectados = 0;
				if (getHechizo() != null ? getHechizo().getAfectadosEstandar().size() > num : false) {
					afectados = getHechizo().getAfectadosEstandar().get(num);
				}
				if (afectados == 18) {
					afectados = 22;
				}
				for (Celda C : celdas) {
					if (C == null) {
						continue;
					}
					Luchador F = C._mapa.getPrimerLuchador(C.getID());
					// Ne touche pas les alli�s
					if ((F == null) || (((afectados & 1) == 1) && (F.getEquipoBin() == lanzador.getEquipoBin()))) {
						continue;
					}
					// Ne touche pas le lanceur
					if ((((afectados >> 1) & 1) == 1) && (F.getID() == lanzador.getID())) {
						continue;
					}
					// Ne touche pas les ennemies
					if ((((afectados >> 2) & 1) == 1) && (F.getEquipoBin() != lanzador.getEquipoBin())) {
						continue;
					}
					// Ne touche pas les combatants (seulement invocations)
					if ((((afectados >> 3) & 1) == 1) && (!F.esInvocacion())) {
						continue;
					}
					// Ne touche pas les invocations
					if ((((afectados >> 4) & 1) == 1) && (F.esInvocacion())) {
						continue;
					}
					// N'affecte que le lanceur
					if ((((afectados >> 5) & 1) == 1) && (F.getID() != lanzador.getID())) {
						continue;
					}
					// Si pas encore eu de continue, on ajoute la case
					celdasFinales.add(C);
				}
				if (((afectados >> 5) & 1) == 1) {
					if (!celdasFinales.contains(lanzador.getCeldaPelea())) {
						celdasFinales.add(lanzador.getCeldaPelea());
					}
				}
				ArrayList<Luchador> objetivos = EfectoHechizo.getAfectados(pelea, celdasFinales, _hechizoID);
				EH.aplicarHechizoAPelea(pelea, lanzador, celda, objetivos, celdasFinales, glifo);
				num++;
			}
		}
	}
}
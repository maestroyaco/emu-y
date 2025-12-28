package variables;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimerTask;
import java.util.TreeMap;

import estaticos.CentroInfo;
import estaticos.Emu;
import estaticos.Formulas;
import estaticos.GestorSalida;
import estaticos.MundoDofus;
import estaticos.MundoDofus.Duo;
import servidor.Colores;
import servidor.EntradaPersonaje.AccionDeJuego;
import variables.Mapa.Celda;
import variables.Mapa.ObjetoInteractivo;
import variables.Objeto.ObjetoModelo;

public class Oficio {
	private int _id;
	private ArrayList<Integer> _herramientas = new ArrayList<>();
	private Map<Integer, ArrayList<Integer>> _recetas = new TreeMap<>();

	public Oficio(int id, String herramientas, String recetas) {
		_id = id;
		if (!herramientas.equals("")) {
			for (String str : herramientas.split(",")) {
				try {
					int herramienta = Integer.parseInt(str);
					_herramientas.add(herramienta);
				} catch (Exception e) {
					continue;
				}
			}
		}
		if (!recetas.equals("")) {
			for (String str : recetas.split("\\|")) {
				try {
					int trabajoID = Integer.parseInt(str.split(";")[0]);
					ArrayList<Integer> list = new ArrayList<>();
					for (String str2 : str.split(";")[1].split(",")) {
						list.add(Integer.parseInt(str2));
					}
					_recetas.put(trabajoID, list);
				} catch (Exception e) {
					continue;
				}
			}
		}
	}

	private ArrayList<Integer> listaRecetaPorTrabajo(int trabajo) {
		return _recetas.get(trabajo);
	}

	private boolean puedeReceta(int trabajo, int modelo) {
		if (_recetas.get(trabajo) != null) {
			for (int a : _recetas.get(trabajo)) {
				if (a == modelo) {
					return true;
				}
			}
		}
		return false;
	}

	public int getID() {
		return _id;
	}

	public boolean herramientaValida(int t) {
		for (int a : _herramientas) {
			if (t == a) {
				return true;
			}
		}
		return false;
	}

	private static byte statActualObjeto(Objeto obj, String statsObjeto) {// 0 = no tiene, 1 = tiene, 2 = negativo
		if (!obj.convertirStatsAString(0).isEmpty()) {
			for (Entry<Integer, Integer> entry : obj.getStats().getStatsComoMap().entrySet()) {
				String hex = Integer.toHexString(entry.getKey());
				if (hex.compareTo(statsObjeto) > 0) {
					if (hex.compareTo("98") == 0 && statsObjeto.compareTo("7b") == 0) {
						return 2;
					} else if (hex.compareTo("9a") == 0 && statsObjeto.compareTo("77") == 0) {
						return 2;
					} else if (hex.compareTo("9b") == 0 && statsObjeto.compareTo("7e") == 0) {
						return 2;
					} else if (hex.compareTo("9d") == 0 && statsObjeto.compareTo("76") == 0) {
						return 2;
					} else if (hex.compareTo("9c") == 0 && statsObjeto.compareTo("7c") == 0) {
						return 2;
					} else if (hex.compareTo("99") == 0 && statsObjeto.compareTo("7d") == 0) {
						return 2;
					} else {
						continue;
					}
				} else if (hex.compareTo(statsObjeto) == 0) {
					return 1;
				}
			}
			return 0;
		} else {
			return 0;
		}
	}

	private static byte statBaseObjeto(Objeto obj, String stat) {
		String[] splitted = obj.getModelo().getStringStatsObj().split(",");
		for (String s : splitted) {
			String[] stats = s.split("#");
			if (stats[0].compareTo(stat) > 0) {
				if (stats[0].compareTo("98") == 0 && stat.compareTo("7b") == 0) {
					return 2;
				} else if (stats[0].compareTo("9a") == 0 && stat.compareTo("77") == 0) {
					return 2;
				} else if (stats[0].compareTo("9b") == 0 && stat.compareTo("7e") == 0) {
					return 2;
				} else if (stats[0].compareTo("9d") == 0 && stat.compareTo("76") == 0) {
					return 2;
				} else if (stats[0].compareTo("9c") == 0 && stat.compareTo("7c") == 0) {
					return 2;
				} else if (stats[0].compareTo("99") == 0 && stat.compareTo("7d") == 0) {
					return 2;
				} else {
					continue;
				}
			} else if (stats[0].compareTo(stat) == 0) {
				return 1;
			}
		}
		return 0;
	}

	public static int getStatBaseMaxs(ObjetoModelo objMod, String statsModif) {
		final String[] split = objMod.getStringStatsObj().split(",");
		String[] array;
		for (int length = (array = split).length, i = 0; i < length; ++i) {
			final String s = array[i];
			final String[] stats = s.split("#");
			if (stats[0].toLowerCase().compareTo(statsModif.toLowerCase()) <= 0) {
				if (stats[0].toLowerCase().compareTo(statsModif.toLowerCase()) == 0) {
					int max = Integer.parseInt(stats[2], 16);
					if (max == 0) {
						max = Integer.parseInt(stats[1], 16);
					}
					return max;
				}
			}
		}
		return 0;
	}

	static int getStatBaseMax(ObjetoModelo objMod, String statsModif) {
		String[] splitted = objMod.getStringStatsObj().split(",");
		for (String s : splitted) {
			String[] stats = s.split("#");
			if ((stats[0].compareTo(statsModif) > 0) || (stats[0].compareTo(statsModif) != 0)) {
				continue;
			}
			int max = Integer.parseInt(stats[2], 16);
			if (max == 0) {
				max = Integer.parseInt(stats[1], 16);
			}
			return max;
		}
		return 0;
	}

	public static byte viewActualStatsItem(Objeto obj, String stats) {
		if (!obj.convertirStatsAString(0).isEmpty()) {
			for (final Map.Entry<Integer, Integer> entry : obj.getStats().getStatsComoMap().entrySet()) {
				if (Integer.toHexString(entry.getKey()).compareTo(stats) > 0) {
					if ((Integer.toHexString(entry.getKey()).compareTo("98") == 0 && stats.compareTo("7b") == 0) || (Integer.toHexString(entry.getKey()).compareTo("9a") == 0 && stats.compareTo("77") == 0)) {
						return 2;
					}
					if (Integer.toHexString(entry.getKey()).compareTo("9b") == 0 && stats.compareTo("7e") == 0) {
						return 2;
					}
					if (Integer.toHexString(entry.getKey()).compareTo("9d") == 0 && stats.compareTo("76") == 0) {
						return 2;
					}
					if (Integer.toHexString(entry.getKey()).compareTo("74") == 0 && stats.compareTo("75") == 0) {
						return 2;
					}
					if (Integer.toHexString(entry.getKey()).compareTo("99") == 0 && stats.compareTo("7d") == 0) {
						return 2;
					}
					continue;
				} else {
					if (Integer.toHexString(entry.getKey()).compareTo(stats) == 0) {
						return 1;
					}
					continue;
				}
			}
			return 0;
		}
		return 0;
	}

	public static byte tieneStatActualObjeto(Objeto obj, String stat) {
		if (!obj.convertirStatsAString(0).isEmpty()) {
			for (Entry<Integer, Integer> entry : obj.getStats().getStatsComoMap().entrySet()) {
				String statObjActual = Integer.toHexString(entry.getKey());
				if (statObjActual.toLowerCase().compareTo(stat.toLowerCase()) > 0) {
					if (statObjActual.toLowerCase().compareTo("98") == 0 && stat.toLowerCase().compareTo("7b") == 0) {
						return 2;
					} else if (statObjActual.toLowerCase().compareTo("9a") == 0
							&& stat.toLowerCase().compareTo("77") == 0) {
						return 2;
					} else if (statObjActual.toLowerCase().compareTo("9b") == 0
							&& stat.toLowerCase().compareTo("7e") == 0) {
						return 2;
					} else if (statObjActual.toLowerCase().compareTo("9d") == 0
							&& stat.toLowerCase().compareTo("76") == 0) {
						return 2;
					} else if (statObjActual.toLowerCase().compareTo("9c") == 0
							&& stat.toLowerCase().compareTo("7c") == 0) {
						return 2;
					} else if (statObjActual.toLowerCase().compareTo("99") == 0
							&& stat.toLowerCase().compareTo("7d") == 0) {
						return 2;
					}
				}
				if (statObjActual.toLowerCase().compareTo(stat.toLowerCase()) == 0) {
					return 1;
				}
			}
		}
		return 0;
	}

	public static byte tieneStatBaseObjeto(Objeto obj, String stat) { // 1 = si objeto base tiene stat
		String[] split = obj.getModelo().getStringStatsObj().split(",");
		for (String s : split) {
			String[] stats = s.split("#");
			if (stats[0].toLowerCase().compareTo(stat.toLowerCase()) > 0) {
				if (stats[0].toLowerCase().compareTo("98") == 0 && stat.toLowerCase().compareTo("7b") == 0) {
					return 2;
				} else if (stats[0].toLowerCase().compareTo("9a") == 0 && stat.toLowerCase().compareTo("77") == 0) {
					return 2;
				} else if (stats[0].toLowerCase().compareTo("9b") == 0 && stat.toLowerCase().compareTo("7e") == 0) {
					return 2;
				} else if (stats[0].toLowerCase().compareTo("9d") == 0 && stat.toLowerCase().compareTo("76") == 0) {
					return 2;
				} else if (stats[0].toLowerCase().compareTo("9c") == 0 && stat.toLowerCase().compareTo("7c") == 0) {
					return 2;
				} else if (stats[0].toLowerCase().compareTo("99") == 0 && stat.toLowerCase().compareTo("7d") == 0) {
					return 2;
				}
			}
			if (stats[0].toLowerCase().compareTo(stat.toLowerCase()) == 0) {
				return 1;
			}
		}
		return 0;
	}

	public static int getBaseMaxJet(final int templateID, final String statsModif) {
		final ObjetoModelo t = MundoDofus.getObjModelo(templateID);
		final String[] splitted = t.getStringStatsObj().split(",");
		String[] array;
		for (int length = (array = splitted).length, i = 0; i < length; ++i) {
			final String s = array[i];
			final String[] stats = s.split("#");
			if (stats[0].compareTo(statsModif) <= 0) {
				if (stats[0].compareTo(statsModif) == 0) {
					int max = Integer.parseInt(stats[2], 16);
					if (max == 0) {
						max = Integer.parseInt(stats[1], 16);
					}
					return max;
				}
			}
		}
		return 0;
	}

	public static int pesoTotalActualObj(String statsModelo, Objeto obj) {
		if (statsModelo.equalsIgnoreCase("")) {
			return 0;
		}
		int Weigth = 0;
		int Alto = 0;
		final String[] split = statsModelo.split(",");
		String[] array;
		for (int length = (array = split).length, i = 0; i < length; ++i) {
			final String s = array[i];
			final String[] stats = s.split("#");
			final int statID = Integer.parseInt(stats[0], 16);
			if (statID != 985) {
				if (statID != 988) {
					boolean xy = false;
					int[] armes_EFFECT_IDS;
					for (int length2 = (armes_EFFECT_IDS = CentroInfo.ID_EFECTOS_ARMAS).length,
							j = 0; j < length2; ++j) {
						final int a = armes_EFFECT_IDS[j];
						if (a == statID) {
							xy = true;
						}
					}
					if (!xy) {
						String jet = "";
						int qua = 1;
						try {
							jet = stats[4];
							qua = Formulas.getRandomValor(jet);
							try {
								final int min = Integer.parseInt(stats[1], 16);
								final int max = Integer.parseInt(stats[2], 16);
								qua = min;
								if (max != 0) {
									qua = max;
								}
							} catch (Exception e) {
								e.printStackTrace();
								qua = Formulas.getRandomValor(jet);
							}
						} catch (Exception ex) {
						}
						int statX = 1;
						int coef = 1;
						final int statsBase = tieneStatBaseObjeto(obj, stats[0]);
						if (statsBase == 2) {
							coef = 3;
						} else if (statsBase == 0) {
							coef = 2;
						}
						if (statID == 125 || statID == 158 || statID == 174) {
							statX = 1;
						} else if (statID == 118 || statID == 126 || statID == 119 || statID == 123) {
							statX = 2;
						} else if (statID == 138 || statID == 666 || statID == 226 || statID == 220) {
							statX = 3;
						} else if (statID == 124 || statID == 176) {
							statX = 5;
						} else if (statID == 240 || statID == 241 || statID == 242 || statID == 243 || statID == 244) {
							statX = 7;
						} else if (statID == 210 || statID == 211 || statID == 212 || statID == 213 || statID == 214) {
							statX = 8;
						} else if (statID == 225) {
							statX = 15;
						} else if (statID == 178 || statID == 112) {
							statX = 20;
						} else if (statID == 115 || statID == 182) {
							statX = 30;
						} else if (statID == 117) {
							statX = 50;
						} else if (statID == 128) {
							statX = 90;
						} else if (statID == 111) {
							statX = 100;
						}
						Weigth = qua * statX * coef;
						Alto += Weigth;
					}
				}
			}
		}
		return Alto;
	}

	public static int getStatBaseMax(int templateID, String statsModif) {
		ObjetoModelo t = MundoDofus.getObjModelo(templateID);
		String[] splitted = t.getStringStatsObj().split(",");
		for (String s : splitted) {
			String[] stats = s.split("#");
			if ((stats[0].compareTo(statsModif) > 0) || (stats[0].compareTo(statsModif) != 0)) {
				continue;
			}
			int max = Integer.parseInt(stats[2], 16);
			if (max == 0) {
				max = Integer.parseInt(stats[1], 16);
			}
			return max;
		}
		return 0;
	}

	public static int WeithTotalBaseMin(final int objTemplateID) {
		int weight = 0;
		int alt = 0;
		String statsTemplate = "";
		statsTemplate = MundoDofus.getObjModelo(objTemplateID).getStringStatsObj();
		if (statsTemplate == null || statsTemplate.isEmpty()) {
			return 0;
		}
		final String[] split = statsTemplate.split(",");
		String[] array;
		for (int length = (array = split).length, i = 0; i < length; ++i) {
			final String s = array[i];
			final String[] stats = s.split("#");
			final int statID = Integer.parseInt(stats[0], 16);
			boolean sig = true;
			int[] armes_EFFECT_IDS;
			for (int length2 = (armes_EFFECT_IDS = CentroInfo.ID_EFECTOS_ARMAS).length, j = 0; j < length2; ++j) {
				final int a = armes_EFFECT_IDS[j];
				if (a == statID) {
					sig = false;
				}
			}
			if (sig) {
				String jet = "";
				int value = 1;
				try {
					jet = stats[4];
					value = Formulas.getRandomValor(jet);
					try {
						value = Integer.parseInt(stats[1], 16);
					} catch (Exception e) {
						value = Formulas.getRandomValor(jet);
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				int statX = 1;
				if (statID == 125 || statID == 158 || statID == 174) {
					statX = 1;
				} else if (statID == 118 || statID == 126 || statID == 119 || statID == 123) {
					statX = 2;
				} else if (statID == 138 || statID == 666 || statID == 226 || statID == 220) {
					statX = 3;
				} else if (statID == 124 || statID == 176) {
					statX = 5;
				} else if (statID == 240 || statID == 241 || statID == 242 || statID == 243 || statID == 244) {
					statX = 7;
				} else if (statID == 210 || statID == 211 || statID == 212 || statID == 213 || statID == 214) {
					statX = 8;
				} else if (statID == 225) {
					statX = 15;
				} else if (statID == 178 || statID == 112) {
					statX = 20;
				} else if (statID == 115 || statID == 182) {
					statX = 30;
				} else if (statID == 117) {
					statX = 50;
				} else if (statID == 128) {
					statX = 90;
				} else if (statID == 111) {
					statX = 100;
				}
				weight = value * statX;
				alt += weight;
			}
		}
		return alt;
	}

	public static int pesoTotalBaseObj(int objTemplateID) {
		int weight = 0;
		int alt = 0;
		String statsTemplate = "";
		statsTemplate = MundoDofus.getObjModelo(objTemplateID).getStringStatsObj();
		if (statsTemplate == null || statsTemplate.isEmpty()) {
			return 0;
		}
		final String[] split = statsTemplate.split(",");
		String[] array;
		for (int length = (array = split).length, i = 0; i < length; ++i) {
			final String s = array[i];
			final String[] stats = s.split("#");
			final int statID = Integer.parseInt(stats[0], 16);
			boolean sig = true;
			int[] armes_EFFECT_IDS;
			for (int length2 = (armes_EFFECT_IDS = CentroInfo.ID_EFECTOS_ARMAS).length, j = 0; j < length2; ++j) {
				final int a = armes_EFFECT_IDS[j];
				if (a == statID) {
					sig = false;
				}
			}
			if (sig) {
				String jet = "";
				int value = 1;
				try {
					jet = stats[4];
					value = Formulas.getRandomValor(jet);
					try {
						final int min = Integer.parseInt(stats[1], 16);
						final int max = Integer.parseInt(stats[2], 16);
						value = min;
						if (max != 0) {
							value = max;
						}
					} catch (Exception e) {
						e.printStackTrace();
						value = Formulas.getRandomValor(jet);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				int statX = 1;
				if (statID == 125 || statID == 158 || statID == 174) {
					statX = 1;
				} else if (statID == 118 || statID == 126 || statID == 119 || statID == 123) {
					statX = 2;
				} else if (statID == 138 || statID == 666 || statID == 226 || statID == 220) {
					statX = 3;
				} else if (statID == 124 || statID == 176) {
					statX = 5;
				} else if (statID == 240 || statID == 241 || statID == 242 || statID == 243 || statID == 244) {
					statX = 7;
				} else if (statID == 210 || statID == 211 || statID == 212 || statID == 213 || statID == 214) {
					statX = 8;
				} else if (statID == 225) {
					statX = 15;
				} else if (statID == 178 || statID == 112) {
					statX = 20;
				} else if (statID == 115 || statID == 182) {
					statX = 30;
				} else if (statID == 117) {
					statX = 50;
				} else if (statID == 128) {
					statX = 90;
				} else if (statID == 111) {
					statX = 100;
				}
				weight = value * statX;
				alt += weight;
			}
		}
		return alt;
	}

	public static int getStatBaseMins(ObjetoModelo objMod, String statsModif) {
		final String[] split = objMod.getStringStatsObj().split(",");
		String[] array;
		for (int length = (array = split).length, i = 0; i < length; ++i) {
			final String s = array[i];
			final String[] stats = s.split("#");
			if (stats[0].toLowerCase().compareTo(statsModif.toLowerCase()) <= 0) {
				if (stats[0].toLowerCase().compareTo(statsModif.toLowerCase()) == 0) {
					return Integer.parseInt(stats[1], 16);
				}
			}
		}
		return 0;
	}

	public static int currentWeithStats(Objeto obj, String statsModif) {
		for (final Map.Entry<Integer, Integer> entry : obj.getStats().getStatsComoMap().entrySet()) {
			final int statID = entry.getKey();
			if (Integer.toHexString(statID).toLowerCase().compareTo(statsModif.toLowerCase()) > 0) {
				continue;
			}
			if (Integer.toHexString(statID).toLowerCase().compareTo(statsModif.toLowerCase()) == 0) {
				int statX = 1;
				int coef = 1;
				int BaseStats = tieneStatBaseObjeto(obj, Integer.toHexString(statID));
				if (BaseStats == 2) {
					coef = 3;
				} else if (BaseStats == 0) {
					coef = 8;
				}
				if (statID == 125 || statID == 158 || statID == 174) {
					statX = 1;
				} else if (statID == 118 || statID == 126 || statID == 119 || statID == 123) {
					statX = 2;
				} else if (statID == 138 || statID == 666 || statID == 226 || statID == 220) {
					statX = 3;
				} else if (statID == 124 || statID == 176) {
					statX = 5;
				} else if (statID == 240 || statID == 241 || statID == 242 || statID == 243 || statID == 244) {
					statX = 7;
				} else if (statID == 210 || statID == 211 || statID == 212 || statID == 213 || statID == 214) {
					statX = 8;
				} else if (statID == 225) {
					statX = 15;
				} else if (statID == 178 || statID == 112) {
					statX = 20;
				} else if (statID == 115 || statID == 182) {
					statX = 30;
				} else if (statID == 117) {
					statX = 50;
				} else if (statID == 128) {
					statX = 90;
				} else if (statID == 111) {
					statX = 100;
				}
				final int Weight = entry.getValue() * statX * coef;
				return Weight;
			}
		}
		return 0;
	}

	public static int pesoStatActual(Objeto obj, String statsModif) {
		for (Entry<Integer, Integer> entry : obj.getStats().getStatsComoMap().entrySet()) {
			if ((Integer.toHexString(entry.getKey().intValue()).compareTo(statsModif) > 0) || (Integer.toHexString(entry.getKey().intValue()).compareTo(statsModif) != 0)) {
				continue;
			}
			int JetActual = entry.getValue().intValue();
			return JetActual;
		}
		return 0;
	}

	public static class StatsOficio {
		private int _posicion;
		private Oficio _oficio;
		private int _nivel;
		private long _exp;
		private ArrayList<AccionTrabajo> _trabajosPoderRealizar = new ArrayList<>();
		private boolean _esPagable;
		private boolean _gratisSiFalla;
		private boolean _noProporcRecurso;
		private int _slotsPublico;
		private int _adicional = 0;
		private AccionTrabajo _tempTrabajo;

		StatsOficio(int posicion, Oficio oficio, int nivel, long xp) {
			_posicion = posicion;
			_oficio = oficio;
			_nivel = nivel;
			_exp = xp;
			_trabajosPoderRealizar = CentroInfo.getTrabajosPorOficios(oficio.getID(), nivel);
			_slotsPublico = CentroInfo.getIngMaxPorNivel(nivel);
		}

		public int getAdicional() {
			return _adicional;
		}

		public int getNivel() {
			return _nivel;
		}

		public boolean esPagable() {
			return _esPagable;
		}

		public boolean esGratisSiFalla() {
			return _gratisSiFalla;
		}

		public boolean noProveerRecuerso() {
			return _noProporcRecurso;
		}

		public void setSlotsPublico(int slots) {
			_slotsPublico = slots;
		}

		public int getSlotsPublico() {
			return _slotsPublico;
		}

		private void subirNivel(Personaje perso) {
			_nivel++;
			_trabajosPoderRealizar = CentroInfo.getTrabajosPorOficios(_oficio.getID(), _nivel);
			_adicional = (int) Math.sqrt(_nivel * 2) + (_nivel / 20);
		}

		public String analizarTrabajolOficio() {
			StringBuilder str = new StringBuilder("|" + _oficio.getID() + ";");
			boolean primero = true;
			for (AccionTrabajo AT : _trabajosPoderRealizar) {
				if (!primero) {
					str.append(",");
				} else {
					primero = false;
				}
				str.append(AT.getIDTrabajo() + "~" + AT.getCasillasMax() + "~");
				if (AT.esReceta()) {
					str.append("0~0~" + AT.getSuerte());
				} else {
					str.append(AT.getCasillasMin() + "~0~" + AT.getTiempo());
				}
			}
			return str.toString();
		}

		public long getXP() {
			return _exp;
		}

		void iniciarTrabajo(int idTrabajo, Personaje perso, ObjetoInteractivo OI, AccionDeJuego AJ, Celda celda) {
			for (AccionTrabajo AT : _trabajosPoderRealizar) {
				if (AT.getIDTrabajo() == idTrabajo) {
					_tempTrabajo = AT;
					AT.iniciarAccionTrabajo(perso, OI, AJ, celda);
					return;
				}
			}
		}

		void finalizarTrabajo(int idTrabajo, Personaje perso, ObjetoInteractivo OI, Celda celda) {
			if (_tempTrabajo == null) {
				return;
			}
			_tempTrabajo.finalizarAccionTrabajo(perso, OI, celda);
			addXP(perso, (int) (_tempTrabajo.getXpGanada() * Emu.RATE_XP_OFICIO));
		}

		public void addXP(Personaje perso, long xp) {
			if ((_nivel > Emu.MAX_NIVEL_OFICIO - 1) || (_exp >= 581687)) {
				return;
			}
			int exNivel = _nivel;
			_exp += xp;
			while (_exp >= MundoDofus.getExpNivel(_nivel + 1)._oficio && _nivel < Emu.MAX_NIVEL_OFICIO) {
				subirNivel(perso);
			}
			ArrayList<StatsOficio> list = new ArrayList<>();
			list.add(this);
			if (perso.enLinea()) {
				if (_nivel >= Emu.MAX_NIVEL_OFICIO) {
					perso.addTitulos(CentroInfo.getIdTituloOficio(this._oficio._id));
					// GestorSQL.SALVAR_PERSONAJE(perso, false);
					if (perso.getPelea() == null) {
						GestorSalida.ENVIAR_GM_REFRESCAR_PJ_EN_MAPA(perso.getMapa(), perso);
					}
				}
				if (_nivel > exNivel) {
					GestorSalida.ENVIAR_JS_TRABAJO_POR_OFICIO(perso, list);
					GestorSalida.ENVIAR_JN_OFICIO_NIVEL(perso, _oficio.getID(), _nivel);
					GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso);
					GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(perso);
					GestorSalida.ENVIAR_JO_OFICIO_OPCIONES(perso, list);
				}
				GestorSalida.ENVIAR_JX_EXPERINENCIA_OFICIO(perso, list);
			}
		}

		public String getXpString(String s) {
			String str = MundoDofus.getExpNivel(_nivel)._oficio + s;
			str += _exp + s;
			str += MundoDofus.getExpNivel((_nivel < Emu.MAX_NIVEL_OFICIO ? _nivel + 1 : _nivel))._oficio;
			return str;
		}

		public Oficio getOficio() {
			return _oficio;
		}

		boolean esValidoTrabajo(int id) {
			for (AccionTrabajo AT : _trabajosPoderRealizar) {
				if (AT.getIDTrabajo() == id) {
					return true;
				}
			}
			return false;
		}

		public int getOpcionBin() {
			int nro = 0;
			nro += (_noProporcRecurso ? 4 : 0);
			nro += (_gratisSiFalla ? 2 : 0);
			nro += (_esPagable ? 1 : 0);
			return nro;
		}

		public void setOpciones(int bin) {
			_noProporcRecurso = false;
			_gratisSiFalla = false;
			_esPagable = false;
			_noProporcRecurso = (bin & 4) == 4;
			_gratisSiFalla = (bin & 2) == 2;
			_esPagable = (bin & 1) == 1;
		}

		public int getPosicion() {
			return _posicion;
		}
	}

	public static class AccionTrabajo {
		private int _idTrabajoMod;
		private int _casillasMin = 1;
		private int _casillasMax = 1;
		private boolean _esReceta;
		private int _suerte = 100;
		private int _tiempo = 0;
		public Celda _celda = null;
		private int _xpGanada = 0;
		private long _iniciarTiempo;
		Map<Integer, Integer> _ingredientes = new TreeMap<>();
		Map<Integer, Integer> _ultimoTrabajo = new TreeMap<>();
		private int _remainingRunes = -1;
		private Personaje _artesano;
		private Personaje _cliente;
		public static float _tolerNormal = 1.2f;
		public static float _tolerVIP = 1.4f;

		public Celda getCelda() {
			return _celda;
		}

		public AccionTrabajo(int idTrabajo, int min, int max, boolean esReceta, int nSuerteTiempo, int xpGanada) {
			_idTrabajoMod = idTrabajo;
			_casillasMin = min;
			_casillasMax = max;
			_esReceta = esReceta;
			if (esReceta) {
				_suerte = nSuerteTiempo;
			} else {
				_tiempo = nSuerteTiempo;
			}
			_xpGanada = xpGanada;
		}

		private void finalizarAccionTrabajo(Personaje perso, ObjetoInteractivo OI, Celda celda) {
			if (_esReceta) {
				GestorSalida.ENVIAR_GDF_FORZADO_PERSONAJE(perso, celda.getID(), 1, 1);
			} else if (OI != null) {
				perso.setIsOnAction(false);
				if (_iniciarTiempo - System.currentTimeMillis() > 500) {
					return;
				}
				long timeact = System.currentTimeMillis() - tiempoInicio;
				if (timeact < _tiempo) {
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso,
							"Has realizado la acciÓn demasiado rÁpido... quÉ ha pasado???", Colores.ROJO);
					return;
				}
				if (OI != null) {
					OI.iniciarTiempoRefresco();
					GestorSalida.ENVIAR_GDF_ESTADO_OBJETO_INTERACTIVO_TODOS(perso, celda, perso.getMapa());// TODO: PARA
																											// TODOS
																											// _TODOS
					OI.setEstado(CentroInfo.IO_ESTADO_VACIO);
				}
				boolean especial = Formulas.getRandomValor(0, 99) == 0;
				int cant = _casillasMax > _casillasMin ? Formulas.getRandomValor(_casillasMin, _casillasMax)
						: _casillasMin;
				int idObjModelo = CentroInfo.getObjetoPorTrabajo(_idTrabajoMod, especial, perso);
				ObjetoModelo OM = MundoDofus.getObjModelo(idObjModelo);
				if (OM == null) {
					return;
				}
				perso.updateObjetivoRecolecta(idObjModelo, cant, "recolectar", false);
				Objeto objeto = OM.crearObjDesdeModelo(cant, false);
				if (!perso.addObjetoSimilar(objeto, true, -1)) {
					MundoDofus.addObjeto(objeto, true);
					perso.addObjetoPut(objeto);
					GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(perso, objeto);
				}
				GestorSalida.ENVIAR_IQ_NUMERO_ARRIBA_PJ(perso, perso.getID(), cant);
				GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(perso);
			}
		}

		private long tiempoInicio = 0;

		private void iniciarAccionTrabajo(Personaje perso, ObjetoInteractivo OI, AccionDeJuego AJ, Celda celda) {
			if (perso.isOnAction() || perso.enTorneo != 0) {
				GestorSalida.ENVIAR_GA_ACCION_DE_JUEGO(perso.getCuenta().getEntradaPersonaje().getOut(), "", "0", "",
						"");
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "Debe acabar primero la acciÓn actual!", Colores.ROJO);
				perso.borrarGA(AJ);
				return;
			}
			if (OI != null) {
				tiempoInicio = System.currentTimeMillis();
				if (OI.getEstado() == CentroInfo.IO_ESTADO_VACIO) {
					GestorSalida.ENVIAR_GA_ACCION_DE_JUEGO(perso.getCuenta().getEntradaPersonaje().getOut(), "", "0",
							"", "");
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "No puedes realizar esta acciÓn", Colores.ROJO);
					perso.borrarGA(AJ);
					return;
				}
			}
			_celda = celda;
			_artesano = perso;
			_broken = false;
			if (_esReceta) {
				perso.setOcupado(true);
				perso.setHaciendoTrabajo(this);
				GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(_artesano, 3, _casillasMax + ";" + _idTrabajoMod);
				GestorSalida.ENVIAR_GDF_FORZADO_PERSONAJE(_artesano, celda.getID(), 2, 1);
				perso.celdadeparo = celda.getID();
			} else {
				perso.setIsOnAction(true);
				GestorSalida.enviar(perso, "#kj");
				if (OI != null) {
					OI.setInteractivo(false);
					OI.setEstado(CentroInfo.IO_ESTADO_ESPERA);
				}
				GestorSalida.ENVIAR_GA_ACCION_JUEGO_AL_MAPA(perso, perso.getMapa(), "" + AJ._idUnica, 501,
						perso.getID() + "", celda.getID() + "," + _tiempo);
				GestorSalida.ENVIAR_GDF_ESTADO_OBJETO_INTERACTIVO_TODOS(perso, celda, perso.getMapa());
				_iniciarTiempo = System.currentTimeMillis() + _tiempo;
			}
		}

		public int getIDTrabajo() {
			return _idTrabajoMod;
		}

		public int getCasillasMin() {
			return _casillasMin;
		}

		public int getCasillasMax() {
			return _casillasMax;
		}

		public int getXpGanada() {
			return _xpGanada;
		}

		public int getSuerte() {
			return _suerte;
		}

		public int getTiempo() {
			return _tiempo;
		}

		public boolean esReceta() {
			return _esReceta;
		}

		public void modificarIngrediente(PrintWriter out, int id, int cant) {
			int c = _ingredientes.get(id) == null ? 0 : _ingredientes.get(id);
			_ingredientes.remove(id);
			c += cant;
			if (c > 0) {
				_ingredientes.put(id, c);
				GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(out, 'O', "+", id + "|" + c);
			} else {
				GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(out, 'O', "-", id + "");
			}
		}

		private void modificarIngredienteIgual(PrintWriter out, int id, int cant) {
			int c = _ingredientes.get(id) == null ? 0 : _ingredientes.get(id);
			_ingredientes.remove(id);
			c = cant;
			if (c > 0) {
				_ingredientes.put(id, c);
				GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(out, 'O', "+", id + "|" + c);
			} else {
				GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(out, 'O', "-", id + "");
			}
		}

		private boolean _broken = false;
		private String _data = "";

		private void iniciarReceta(boolean repite, int veces) {
			if (!_esReceta) {
				return;
			}
			if (!Emu.tallerAbierto) {
				GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_artesano, "Los talleres estÁn cerrados de momento.");
				return;
			}
			boolean firmado = false;
			try {
				if (_idTrabajoMod == 1 || _idTrabajoMod == 113 || _idTrabajoMod == 115 || _idTrabajoMod == 116
						|| _idTrabajoMod == 117 || _idTrabajoMod == 118 || _idTrabajoMod == 119 || _idTrabajoMod == 120
						|| (_idTrabajoMod >= 163 && _idTrabajoMod <= 169)) {
					recetaForjaMagia(repite, veces);
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				Map<Integer, Integer> ingredPorModelo = new TreeMap<>();
				TreeMap<Integer, Integer> ingred = new TreeMap<>();
				ingred.putAll(_ingredientes);
				for (Entry<Integer, Integer> ingrediente : ingred.entrySet()) {
					int idObjeto = ingrediente.getKey();
					int cantObjeto = ingrediente.getValue();
					if (!_artesano.tieneObjetoID(idObjeto)) {
						GestorSalida.ENVIAR_Ec_INICIAR_RECETA(_artesano, "EI");
						return;
					}
					if (!_artesano.tieneObjNoEquip(idObjeto, cantObjeto)) {
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_artesano,
								"No tienes los recursos necesarios para seguir con la receta.", Colores.ROJO);
						GestorSalida.ENVIAR_Ec_INICIAR_RECETA(_artesano, "EI");
						return;
					}
					Objeto obj = MundoDofus.getObjeto(idObjeto); // IGUAL A ING
					if (obj == null || obj.getCantidad() < cantObjeto) {
						GestorSalida.ENVIAR_Ec_INICIAR_RECETA(_artesano, "EI");
						return;
					}
					int nuevaCant = obj.getCantidad() - cantObjeto;
					if (nuevaCant < 0) {
						return;
					}
					if (veces > 1) {
						_artesano.getHaciendoTrabajo().modificarIngredienteIgual(
								_artesano.getCuenta().getEntradaPersonaje().getOut(), idObjeto, cantObjeto);
					}
					if (nuevaCant == 0) {
						_artesano.borrarObjetoRemove(idObjeto);
						MundoDofus.eliminarObjeto(idObjeto);
						GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_artesano, idObjeto);
					} else {// TODO:Quit� esto porq salia undefined al hacer trabajo
						// GestorSalida.GAME_SEND_Em_PACKET(_artesano, "KO+" +
						// obj.getID()+"|-"+cantObjeto);
						obj.setCantidad(nuevaCant);
						GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_artesano, obj);
					}
					ingredPorModelo.put(obj.getModelo().getID(), cantObjeto);
				}
				if (ingredPorModelo.containsKey(7508)) {
					firmado = true;
					ingredPorModelo.remove(7508);
				}
				GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_artesano);
				StatsOficio SO = _artesano.getOficioPorTrabajo(_idTrabajoMod);
				int idReceta = MundoDofus.getIDRecetaPorIngredientes(
						SO.getOficio().listaRecetaPorTrabajo(_idTrabajoMod), ingredPorModelo);
				if (idReceta == -1 || !SO.getOficio().puedeReceta(_idTrabajoMod, idReceta)) {
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_artesano, "La receta no es correcta.", Colores.ROJO);
					GestorSalida.ENVIAR_IO_ICONO_OBJ_INTERACTIVO(_artesano.getMapa(), _artesano.getID(), "-");
					_ingredientes.clear();
					return;
				}
				// int suerte = CentroInfo.getSuertePorNroCasillaYNivel(SO.getNivel(),
				// _ingredientes.size());
				// int jet = Formulas.getRandomValor(1, 100);
				// boolean exito = suerte >= jet;
				boolean exito = true;
				if (!exito) {
					GestorSalida.ENVIAR_Ec_INICIAR_RECETA(_artesano, "EF");
					GestorSalida.ENVIAR_IO_ICONO_OBJ_INTERACTIVO(_artesano.getMapa(), _artesano.getID(),
							"-" + idReceta);
					GestorSalida.ENVIAR_Im_INFORMACION(_artesano, "0118");
				} else {
					_artesano.updateObjetivoRecolecta(idReceta, 1, "recolectar", false);
					Objeto objCreado = MundoDofus.getObjModelo(idReceta).crearObjDesdeModelo(1, false);
					if (firmado) {
						objCreado.addTextoStat(988, _artesano.getNombre());
					}
					Objeto igual = _artesano.getObjSimilarInventario(objCreado);
					if (igual == null) {
						MundoDofus.addObjeto(objCreado, true);
						_artesano.addObjetoPut(objCreado);
						GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_artesano, objCreado);
					} else {
						igual.setCantidad(igual.getCantidad() + 1);
						GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_artesano, igual);
						objCreado = igual;
					}
					GestorSalida.ENVIAR_Ec_INICIAR_RECETA(_artesano, "K;" + idReceta);
					GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_artesano);
					_data = objCreado.stringObjetoConPalo(1);
					sendObject(_data, _artesano);
					GestorSalida.ENVIAR_IO_ICONO_OBJ_INTERACTIVO(_artesano.getMapa(), _artesano.getID(),
							"+" + idReceta);
				}
				int xpGanada = (int) (CentroInfo.calculXpGanadaEnOficio(SO.getNivel(), _ingredientes.size())
						* Emu.RATE_XP_OFICIO);
				SO.addXP(_artesano, xpGanada);
				_ultimoTrabajo.clear();
				_ultimoTrabajo.putAll(_ingredientes);
				_ingredientes.clear();
				try {
					Thread.sleep(200);
				} catch (Exception e) {
				}
			} catch (Exception e) {
				return;
			}
		}

		private void repeat(int veces, Personaje P) {
			_artesano = P;
			TimerTask temp = new TimerTask() {
				int aveces = veces - 1;
				int actpods = P.getPodUsados();
				int maxpods = P.getMaxPod();

				@Override
				public void run() {
					if (!_artesano.enLinea()) {
						this.cancel();
						return;
					}
					if (actpods >= maxpods) {
						GestorSalida.ENVIAR_Ea_TERMINO_RECETAS(_artesano, "1");
						_broken = false;
						sendObject(_data, P);
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(P, "Te has quedado sin espacio en el inventario.",
								Colores.ROJO);
						this.cancel();
						return;
					}
					if (aveces <= 0) {
						GestorSalida.ENVIAR_Ea_TERMINO_RECETAS(_artesano, "1");
						_broken = false;
						sendObject(_data, P);
						this.cancel();
						return;
					} else {
						aveces -= 1;
						if (_broken) {
							GestorSalida.ENVIAR_Ea_TERMINO_RECETAS(_artesano, _broken ? "2" : "4");
							_broken = false;
							sendObject(_data, P);
							this.cancel();
							return;
						}
						GestorSalida.ENVIAR_EA_TURNO_RECETA(_artesano, aveces + "");
						_ingredientes.clear();
						_ingredientes.putAll(_ultimoTrabajo);
						iniciarReceta(true, aveces + 2);
						try {
							Thread.sleep(300);
						} catch (InterruptedException e) {
						}
					}
				}
			};
			Emu._globalTime.schedule(temp, 800, 800);
		}

		public void breakFM(boolean value) {
			_broken = value;
		}

		private void recetaForjaMagia(boolean isRepeat, int repeat) { // TODO:
			try {
				boolean firmado = false;
				Objeto objAMaguear = null, objRunaFirma = null, objModificador = null;
				int nivelRunaElemento = 0;
				int statAgre = -1;
				int nivelRunaStats = 0;
				int agregar = 0;
				int idABorrar = -1;
				int runa = 0;
				int larunaborra = 0;
				boolean vip = false;
				String statAMaguear = "-1";
				for (int idIngrediente : _ingredientes.keySet()) {
					Objeto ing = MundoDofus.getObjeto(idIngrediente);
					if (ing == null || !_artesano.tieneObjetoID(idIngrediente)) {
						GestorSalida.ENVIAR_Ec_INICIAR_RECETA(_artesano, "EI");
						GestorSalida.ENVIAR_IO_ICONO_OBJ_INTERACTIVO(_artesano.getMapa(), _artesano.getID(), "-");
						_ingredientes.clear();
						return;
					}
					if (ing.getModelo().getTipo() == 78) {
						larunaborra = idIngrediente;
					}
					int idModelo = ing.getIDModelo();
					switch (idModelo) {
					case 1333:// pocion chispa
						statAgre = 99;
						nivelRunaElemento = ing.getModelo().getNivel();
						objModificador = ing;
						break;
					case 1335:// pocion llovisna
						statAgre = 96;
						nivelRunaElemento = ing.getModelo().getNivel();
						objModificador = ing;
						break;
					case 1337:// pocion de corriente de airee
						statAgre = 98;
						nivelRunaElemento = ing.getModelo().getNivel();
						objModificador = ing;
						break;
					case 1338:// pocion de sacudida
						statAgre = 97;
						nivelRunaElemento = ing.getModelo().getNivel();
						objModificador = ing;
						break;
					case 1340:// pocion derrumbamiento
						statAgre = 97;
						nivelRunaElemento = ing.getModelo().getNivel();
						objModificador = ing;
						break;
					case 1341:// pocion chaparron
						statAgre = 96;
						nivelRunaElemento = ing.getModelo().getNivel();
						objModificador = ing;
						break;
					case 1342:// pocion de rafaga
						statAgre = 98;
						nivelRunaElemento = ing.getModelo().getNivel();
						objModificador = ing;
						break;
					case 1343:// pocion de Flameacion
						statAgre = 99;
						nivelRunaElemento = ing.getModelo().getNivel();
						objModificador = ing;
						break;
					case 1345:// pocion Incendio
						statAgre = 99;
						nivelRunaElemento = ing.getModelo().getNivel();
						objModificador = ing;
						break;
					case 1346:// pocion Tsunami
						statAgre = 96;
						nivelRunaElemento = ing.getModelo().getNivel();
						objModificador = ing;
						break;
					case 1347:// pocion huracan
						statAgre = 98;
						nivelRunaElemento = ing.getModelo().getNivel();
						objModificador = ing;
						break;
					case 1348:// pocion de seismo
						statAgre = 97;
						nivelRunaElemento = ing.getModelo().getNivel();
						objModificador = ing;
						break;
					case 1519:// Fuerza
						objModificador = ing;
						statAMaguear = "76";
						agregar = 1;
						runa = 1;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 1521:// Sabiduria
						objModificador = ing;
						statAMaguear = "7c";
						agregar = 1;
						runa = 6;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 1522:// Inteligencia
						objModificador = ing;
						statAMaguear = "7e";
						agregar = 1;
						runa = 1;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 1523:// Vita
						objModificador = ing;
						statAMaguear = "7d";
						agregar = 3;
						runa = 1;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 1524:// Agi
						objModificador = ing;
						statAMaguear = "77";
						agregar = 1;
						runa = 1;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 1525:// suerte
						objModificador = ing;
						statAMaguear = "7b";
						agregar = 1;
						runa = 1;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 1545:// Pa fuerza
						objModificador = ing;
						statAMaguear = "76";
						agregar = 3;
						runa = 3;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 1546:// Pa Sabiduria
						objModificador = ing;
						statAMaguear = "7c";
						agregar = 3;
						runa = 18;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 1547:// Pa Inteligencia
						objModificador = ing;
						statAMaguear = "7e";
						agregar = 3;
						runa = 3;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 1548:// Pa Vitalidad
						objModificador = ing;
						statAMaguear = "7d";
						agregar = 10;
						runa = 10;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 1549:// Pa agilidad
						objModificador = ing;
						statAMaguear = "77";
						agregar = 3;
						runa = 3;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 1550:// Pa suerte
						objModificador = ing;
						statAMaguear = "7b";
						agregar = 3;
						runa = 10;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 1551:// Ra Fuerza
						objModificador = ing;
						statAMaguear = "76";
						agregar = 10;
						runa = 10;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 1552:// Ra Sabiduria
						objModificador = ing;
						statAMaguear = "7c";
						agregar = 10;
						runa = 50;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 1553:// Ra Iniciativa
						objModificador = ing;
						statAMaguear = "7e";
						agregar = 10;
						runa = 10;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 1554:// Ra Vida
						objModificador = ing;
						statAMaguear = "7d";
						agregar = 30;
						runa = 10;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 1555:// Ra Agilidad
						objModificador = ing;
						statAMaguear = "77";
						agregar = 10;
						runa = 10;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 1556:// Ra suerte
						objModificador = ing;
						statAMaguear = "7b";
						agregar = 10;
						runa = 10;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 1557:// Ga PA
						objModificador = ing;
						statAMaguear = "6f";
						agregar = 1;
						runa = 100;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 1558:// Ga PM
						objModificador = ing;
						statAMaguear = "80";
						agregar = 1;
						runa = 90;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7433:// Criticos
						objModificador = ing;
						statAMaguear = "73";
						agregar = 1;
						runa = 30;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7434:// curas
						objModificador = ing;
						statAMaguear = "b2";
						agregar = 1;
						runa = 20;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7435:// daÑos
						objModificador = ing;
						statAMaguear = "70";
						agregar = 1;
						runa = 20;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7436:// daÑos %
						objModificador = ing;
						statAMaguear = "8a";
						agregar = 1;
						runa = 2;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 10618:// daÑos %
						objModificador = ing;
						statAMaguear = "8a";
						agregar = 3;
						runa = 8;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 10619:// daÑos %
						objModificador = ing;
						statAMaguear = "8a";
						agregar = 10;
						runa = 30;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7437:// daÑos reenvio
						objModificador = ing;
						statAMaguear = "dc";
						agregar = 1;
						runa = 2;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7438:// alcance
						objModificador = ing;
						statAMaguear = "75";
						agregar = 1;
						runa = 50;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7442:// invocaciones
						objModificador = ing;
						statAMaguear = "b6";
						agregar = 1;
						runa = 30;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7443:// Pod
						objModificador = ing;
						statAMaguear = "9e";
						agregar = 10;
						runa = 1;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7444:// Pa pod
						objModificador = ing;
						statAMaguear = "9e";
						agregar = 30;
						runa = 1; // ?
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7445:// Ra pod
						objModificador = ing;
						statAMaguear = "9e";
						agregar = 100;
						runa = 1; // ?
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7446:// trampa
						objModificador = ing;
						statAMaguear = "e1";
						agregar = 1;
						runa = 15;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7447:// trampa %
						objModificador = ing;
						statAMaguear = "e2";
						agregar = 1;
						runa = 2;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7448:// iniciativa
						objModificador = ing;
						statAMaguear = "ae";
						agregar = 10;
						runa = 1;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7449:// Pa iniciativa
						objModificador = ing;
						statAMaguear = "ae";
						agregar = 30;
						runa = 3;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7450:// Ra iniciativa
						objModificador = ing;
						statAMaguear = "ae";
						agregar = 100;
						runa = 10;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7451:// Prospeccion
						objModificador = ing;
						statAMaguear = "b0";
						agregar = 1;
						runa = 5;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7452:// resistencia fuego
						objModificador = ing;
						statAMaguear = "f3";
						agregar = 1;
						runa = 4;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7453:// resistencia aire
						objModificador = ing;
						statAMaguear = "f2";
						agregar = 1;
						runa = 4;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7454:// resistencia agua
						objModificador = ing;
						statAMaguear = "f1";
						agregar = 1;
						runa = 4;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7455:// resistencia tierra
						objModificador = ing;
						statAMaguear = "f0";
						agregar = 1;
						runa = 4;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7456:// resistencia neutral
						objModificador = ing;
						statAMaguear = "f4";
						agregar = 1;
						runa = 4;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7457:// resistencia % fuego
						objModificador = ing;
						statAMaguear = "d5";
						agregar = 1;
						runa = 5;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7458:// resistencia % aire
						objModificador = ing;
						statAMaguear = "d4";
						agregar = 1;
						runa = 5;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7459:// resistencia % tierra
						objModificador = ing;
						statAMaguear = "d2";
						agregar = 1;
						runa = 5;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7460:// resistencia % neutral
						objModificador = ing;
						statAMaguear = "d6";
						agregar = 1;
						runa = 5;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7560:// resistencia % agua
						objModificador = ing;
						statAMaguear = "d3";
						agregar = 1;
						runa = 5;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 8379:// runa vida
						objModificador = ing;
						statAMaguear = "7d";
						agregar = 10;
						runa = 10;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 10662:// runa ra prospe
						objModificador = ing;
						statAMaguear = "b0";
						agregar = 3;
						runa = 15;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7508:// Runa de Firma
						firmado = true;
						objRunaFirma = ing;
						break;
					case 11118:// Fuerza
						vip = true;
						objModificador = ing;
						statAMaguear = "76";
						agregar = 15;
						runa = 1;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 11119:// Sabiduria
						vip = true;
						objModificador = ing;
						statAMaguear = "7c";
						agregar = 15;
						runa = 1;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 11120:// Inteligencia
						vip = true;
						objModificador = ing;
						statAMaguear = "7e";
						agregar = 15;
						runa = 1;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 11121:// Vita
						vip = true;
						objModificador = ing;
						statAMaguear = "7d";
						agregar = 45;
						runa = 1;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 11122:// Agi
						vip = true;
						objModificador = ing;
						statAMaguear = "77";
						agregar = 15;
						runa = 1;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 11123:// suerte
						vip = true;
						objModificador = ing;
						statAMaguear = "7b";
						agregar = 15;
						runa = 1;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 11124:// Prospeccion
						vip = true;
						objModificador = ing;
						statAMaguear = "b0";
						agregar = 10;
						runa = 1;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 11125:// Criticos
						vip = true;
						objModificador = ing;
						statAMaguear = "73";
						agregar = 3;
						runa = 1;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 11126:// curas
						vip = true;
						objModificador = ing;
						statAMaguear = "b2";
						agregar = 5;
						runa = 1;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 11127:// daÑos
						vip = true;
						objModificador = ing;
						statAMaguear = "70";
						agregar = 5;
						runa = 1;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 11128:// daÑos %
						vip = true;
						objModificador = ing;
						statAMaguear = "8a";
						agregar = 10;
						runa = 1;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 11129:// daÑos reenvio
						vip = true;
						objModificador = ing;
						statAMaguear = "dc";
						agregar = 5;
						runa = 1;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					default:
						int tipo = ing.getModelo().getTipo();
						if ((tipo >= 1 && tipo <= 11) || (tipo >= 16 && tipo <= 22) || tipo == 81 || tipo == 102
								|| tipo == 114 || ing.getModelo().getCostePA() > 0) {
							objAMaguear = ing;
							idABorrar = idIngrediente;
							GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(
									_artesano.getCuenta().getEntradaPersonaje().getOut(), 'O', "+",
									objAMaguear.getID() + "|" + 1);
							Objeto nuevoObj = Objeto.clonarObjeto(objAMaguear, 1);
							if (objAMaguear.getCantidad() > 1) {
								objAMaguear.setCantidad(objAMaguear.getCantidad() - 1);
								GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_artesano, objAMaguear);
							} else {
								_artesano.borrarObjetoEliminar(objAMaguear.getID(), 1, true); // borra de mundo y de pj
								// GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_artesano, idIngrediente);
							}
							objAMaguear = nuevoObj;
						}
					}
				}
				StatsOficio oficio = _artesano.getOficioPorTrabajo(_idTrabajoMod);
				oficio.addXP(_artesano, (int) (Emu.RATE_XP_OFICIO + 9.0 / 10.0) * 10);
				if (oficio == null || objAMaguear == null || objModificador == null) {
					if (objAMaguear != null) {
						MundoDofus.addObjeto(objAMaguear, false);
						_artesano.addObjetoPut(objAMaguear);
						GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_artesano, objAMaguear);
					}
					GestorSalida.ENVIAR_Ec_INICIAR_RECETA(_artesano, "EI");
					GestorSalida.ENVIAR_IO_ICONO_OBJ_INTERACTIVO(_artesano.getMapa(), _artesano.getID(), "-");
					_ingredientes.clear();
					return;
				}
				if (idABorrar != -1) {
					_ingredientes.remove(idABorrar);
				}
				ObjetoModelo objModelo = objAMaguear.getModelo();
				long suerte = 0;
				int nivelOficio = oficio.getNivel();
				int objModeloID = objModelo.getID();
				String statStringObj = objAMaguear.convertirStatsAString(0).replace(";", "#");
				float cant = 0;
				if (nivelRunaElemento > 0 && nivelRunaStats == 0) {// si cambia de elemento
					suerte = Formulas.calculoPorcCambioElenemto(nivelOficio, objModelo.getNivel(), nivelRunaElemento);
					if (suerte > 100 - (nivelOficio / 20)) {
						suerte = 100 - (nivelOficio / 20);
					}
					if (suerte < (nivelOficio / 20)) {
						suerte = (nivelOficio / 20);
					}
				} else if (nivelRunaStats > 0 && nivelRunaElemento == 0) {// si cambia de stats
					float tolerancia = vip ? _tolerVIP : _tolerNormal;
					float base = Objeto.getStatBase(objAMaguear, statAMaguear);
					float basemax = Objeto.getStatBaseMax(objAMaguear.getModelo(), statAMaguear);
					if ((basemax * tolerancia) >= (base + agregar)) {
						if (nivelOficio == Emu.MAX_NIVEL_OFICIO) {
							suerte = 100;
						} else {
							suerte = Math.round(nivelOficio * 1.2);
						}
						if (vip) {
							suerte = 100;
						}
					} else {
						if (vip && basemax > 0) {
							agregar -= (int) ((base + agregar) - (basemax * tolerancia));
							suerte = 100;
							GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_artesano,
									"No metas mas runas VIP porque perder� la runa", Colores.ROJO);
						} else if (base == 0) {
							if (_artesano.getCuenta().getRango() > 4) {
								suerte = 100;
							} else {
								suerte = Math.round(nivelOficio * 1.2);
							}
						} else {
							suerte = 1;
						}
					}
					if (suerte > 100) {
						suerte = 100;
					}
					cant = (basemax * tolerancia) - (base + agregar);
				}
				int jet = Formulas.getRandomValor(1, 100);
				boolean exito = suerte >= jet;
				if (exito && objModificador != null) {
					if (objModificador.getModelo().getID() == 1557) {
						int rnd = Formulas.getRandomValor(1, 3);
						if (rnd != 1) {
							exito = false;
						}
					}
				}
				// System.out.println("Aleatorio: "+aleatoryChance+" SC "+SC+" successC
				// "+successC+" SN "+SN+" successN "+successN);
				if (exito) { // si sale con exito
					int coef = 0;
					if (nivelRunaElemento == 1) {
						coef = 50;
					} else if (nivelRunaElemento == 25) {
						coef = 65;
					} else if (nivelRunaElemento == 50) {
						coef = 85;
					}
					if (firmado) {
						objAMaguear.addTextoStat(985, _artesano.getNombre());
					}
					if (nivelRunaElemento > 0 && nivelRunaStats == 0) {
						for (EfectoHechizo EH : objAMaguear.getEfectos()) {
							if (EH.getEfectoID() != 100) {
								continue;
							}
							String[] infos = EH.getArgs().split(";");
							try {
								int min = Integer.parseInt(infos[0], 16);
								int max = Integer.parseInt(infos[1], 16);
								int nuevoMin = (min * coef) / 100;
								int nuevoMax = (max * coef) / 100;
								if (nuevoMin == 0) {
									nuevoMin = 1;
								}
								String nuevoRango = "1d" + (nuevoMax - nuevoMin + 1) + "+" + (nuevoMin - 1);
								String nuevosArgs = Integer.toHexString(nuevoMin) + ";" + Integer.toHexString(nuevoMax)
										+ ";-1;-1;0;" + nuevoRango;
								EH.setArgs(nuevosArgs);
								EH.setEfectoID(statAgre);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					} else if (nivelRunaStats > 0 && nivelRunaElemento == 0) {// Si se modifica los stats(runa)
						boolean negativo = false;
						int currentStats2 = tieneStatActualObjeto(objAMaguear, statAMaguear);
						if (currentStats2 == 2) {// los stats existentes actual son negativos
							if (statAMaguear.compareTo("7b") == 0) {
								statAMaguear = "98";
								negativo = true;
							}
							if (statAMaguear.compareTo("77") == 0) {
								statAMaguear = "9a";
								negativo = true;
							}
							if (statAMaguear.compareTo("7e") == 0) {
								statAMaguear = "9b";
								negativo = true;
							}
							if (statAMaguear.compareTo("76") == 0) {
								statAMaguear = "9d";
								negativo = true;
							}
							if (statAMaguear.compareTo("7c") == 0) {
								statAMaguear = "9c";
								negativo = true;
							}
							if (statAMaguear.compareTo("7d") == 0) {
								statAMaguear = "99";
								negativo = true;
							}
						}
						if (currentStats2 == 1 || currentStats2 == 2) {
							String statsStr = objAMaguear.convertirStatsAStringFM(statAMaguear, objAMaguear, agregar,
									negativo);
							objAMaguear.clearTodo();
							objAMaguear.convertirStringAStats(statsStr);
						} else {
							if (statStringObj.isEmpty()) {
								String statsStr = statAMaguear + "#" + Integer.toHexString(agregar) + "#0#0#0d0+"
										+ agregar;
								objAMaguear.clearTodo();
								objAMaguear.convertirStringAStats(statsStr);
							} else {
								String statsStr = objAMaguear.convertirStatsAStringFM(statAMaguear, objAMaguear,
										agregar, negativo) + "," + statAMaguear + "#" + Integer.toHexString(agregar)
										+ "#0#0#0d0+" + agregar;
								objAMaguear.clearTodo();
								objAMaguear.convertirStringAStats(statsStr);
							}
						}
					}
					MundoDofus.addObjeto(objAMaguear, false);
					_artesano.addObjetoPut(objAMaguear);
					GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_artesano);// Pods
					GestorSalida.GAME_SEND_Em_PACKET(_artesano,
							"KO+" + objAMaguear.getID() + "|1|" + objAMaguear.getModelo().getID() + "|"
									+ objAMaguear.convertirStatsAString(0).replace(";", "#"));
					GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_artesano, objAMaguear);
					GestorSalida.ENVIAR_IO_ICONO_OBJ_INTERACTIVO(_artesano.getMapa(), _artesano.getID(),
							"+" + objModeloID);
					GestorSalida.ENVIAR_Ec_INICIAR_RECETA(_artesano, "K;" + objModeloID);// Exito
					if (nivelOficio == Emu.MAX_NIVEL_OFICIO) {
						GestorSalida
								.ENVIAR_cs_CHAT_MENSAJE(_artesano,
										"Su probabilidad de Éxito fu� de <b>" + Math.round(suerte)
												+ "%</b>. A�n puedes meterle <b>" + cant + "</b> sin fallar.",
										Colores.VERDE);
					} else {
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_artesano,
								"Su probabilidad de Éxito fu� de <b>" + Math.round(suerte)
										+ "%</b>. A�n puedes meterle <b>" + cant
										+ "</b> sin fallar. Cuanto mÁs nivel de oficio tengas, mÁs probabilidades.",
								Colores.VERDE);
					}
				} else {
					if (!statStringObj.isEmpty()) {
						String statsStr = objAMaguear.stringStatsFCForja(objAMaguear, runa);
						objAMaguear.clearTodo();
						objAMaguear.convertirStringAStats(statsStr);
					}
					MundoDofus.addObjeto(objAMaguear, false);
					_artesano.addObjetoPut(objAMaguear);
					GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_artesano);// Pods
					GestorSalida.GAME_SEND_Em_PACKET(_artesano,
							"KO+" + objAMaguear.getID() + "|1|" + objAMaguear.getModelo().getID() + "|"
									+ objAMaguear.convertirStatsAString(0).replace(";", "#"));
					GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_artesano, objAMaguear);
					GestorSalida.ENVIAR_IO_ICONO_OBJ_INTERACTIVO(_artesano.getMapa(), _artesano.getID(),
							"-" + objModeloID);
					GestorSalida.ENVIAR_Ec_INICIAR_RECETA(_artesano, "EF");
					GestorSalida.ENVIAR_Im_INFORMACION(_artesano, "0183");
				}
				if (objRunaFirma != null) {
					int nuevaCant = objRunaFirma.getCantidad() - 1;
					if (nuevaCant <= 0) {
						_artesano.borrarObjetoRemove(objRunaFirma.getID());
						MundoDofus.eliminarObjeto(objRunaFirma.getID());
						GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_artesano, objRunaFirma.getID());
					} else {
						objRunaFirma.setCantidad(nuevaCant);
						GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_artesano, objRunaFirma);
					}
				}
				if (objModificador != null) {
					int nuevaCant = objModificador.getCantidad() - 1;
					if (nuevaCant <= 0) {
						_artesano.borrarObjetoRemove(objModificador.getID());
						MundoDofus.eliminarObjeto(objModificador.getID());
						GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_artesano, objModificador.getID());
					} else {
						objModificador.setCantidad(nuevaCant);
						GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_artesano, objModificador);
					}
				}
				_ultimoTrabajo.clear();
				_ultimoTrabajo.putAll(_ingredientes);
				_ultimoTrabajo.put(objAMaguear.getID(), 1);
				int nbRunes = 0;
				if (!_ingredientes.isEmpty() && this._ingredientes.get(larunaborra) != null) {
					if (isRepeat) {
						nbRunes = repeat;
					} else {
						nbRunes = this._ingredientes.get(larunaborra) - 1;
					}
				}
				_ingredientes.clear();
				if (nbRunes > 0) {
					modificarIngrediente(_artesano.getCuenta().getEntradaPersonaje().getOut(), larunaborra, nbRunes);
					modificarIngrediente(_artesano.getCuenta().getEntradaPersonaje().getOut(), objAMaguear.getID(), 1);
				} else if (nbRunes == 0) {
					modificarIngrediente(_artesano.getCuenta().getEntradaPersonaje().getOut(), larunaborra, 0);
					modificarIngrediente(_artesano.getCuenta().getEntradaPersonaje().getOut(), objAMaguear.getID(), 1);
				}
				Map<Integer, Integer> magueados = new TreeMap<>();
				magueados.putAll(_artesano.magueados);
				_artesano.magueados.clear();
				int runaid = objModificador.getModelo().getID();
				if (magueados.containsKey(runaid)) {
					int cant1 = magueados.get(runaid) + 1;
					magueados.remove(runaid);
					magueados.put(runaid, cant1);
				} else {
					magueados.put(runaid, 1);
				}
				_artesano.magueados.putAll(magueados);
			} catch (Exception e) {
				Emu.creaLogs(e);
			}
		}

		public void unaMagueada() {
			iniciarReceta(false, 1);
			_artesano.updateObjetivoRecolecta(0, 0, "maguear", false);
		}

		private void sendObject(String str, Personaje P) {
			if (!str.isEmpty() || !_data.isEmpty()) {
				_artesano.updateObjetivoRecolecta(0, 0, "maguear", false);
				GestorSalida.GAME_SEND_Em_PACKET(P, "KO+" + str);
				_data = "";
			}
		}

		public void magearNormaloFM(int time, Personaje P) {
			_artesano = P;
			if (_idTrabajoMod != 1 && _idTrabajoMod != 113 && _idTrabajoMod != 115 && _idTrabajoMod != 116
					&& _idTrabajoMod != 117 && _idTrabajoMod != 118 && _idTrabajoMod != 119 && _idTrabajoMod != 120
					&& _idTrabajoMod != 163 && _idTrabajoMod != 164 && _idTrabajoMod != 165 && _idTrabajoMod != 166
					&& _idTrabajoMod != 167 && _idTrabajoMod != 168 && _idTrabajoMod != 169) {
				repeat(time, P);
				return;
			}
			_remainingRunes = time;
			TimerTask temp = new TimerTask() {
				@Override
				public void run() {
					if (_remainingRunes <= 0 || !_artesano.enLinea()) {
						GestorSalida.ENVIAR_Ea_TERMINO_RECETAS(_artesano, "1");
						_broken = false;
						this.cancel();
						_artesano.updateObjetivoRecolecta(0, 0, "maguear", false);
						return;
					}
					if (_broken) {
						GestorSalida.ENVIAR_Ea_TERMINO_RECETAS(_artesano, _broken ? "2" : "4");
						_broken = false;
						this.cancel();
						_artesano.updateObjetivoRecolecta(0, 0, "maguear", false);
						return;
					} else {
						_remainingRunes -= 1;
						GestorSalida.ENVIAR_EA_TURNO_RECETA(_artesano, _remainingRunes + "");
						iniciarReceta(true, _remainingRunes);
					}
				}
			};
			Emu._globalTime.schedule(temp, 1000, 1000);
		}

		public void ponerIngredUltRecet() {
			if (_artesano == null || _ultimoTrabajo == null || !_ingredientes.isEmpty()) {
				return;
			}
			_ingredientes.clear();
			_ingredientes.putAll(_ultimoTrabajo);
			for (Entry<Integer, Integer> e : _ingredientes.entrySet()) {
				Objeto objeto = MundoDofus.getObjeto(e.getKey());
				if ((objeto == null) || (objeto.getCantidad() < e.getValue())) {
					continue;
				}
				GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_artesano.getCuenta().getEntradaPersonaje().getOut(), 'O',
						"+", e.getKey() + "|" + e.getValue());
			}
		}

		public void resetReceta() {
			_ingredientes.clear();
			_ultimoTrabajo.clear();
		}

		public boolean iniciarTrabajoPago(Personaje artesano, Personaje cliente,
				ArrayList<Duo<Integer, Integer>> objArtesano, ArrayList<Duo<Integer, Integer>> objCliente) {
			if (!_esReceta) {
				return false;
			}
			_artesano = artesano;
			_cliente = cliente;
			boolean firmado = false;
			for (Duo<Integer, Integer> duo : objArtesano) {
				_ingredientes.put(duo._primero, duo._segundo);
			}
			for (Duo<Integer, Integer> duo : objCliente) {
				_ingredientes.put(duo._primero, duo._segundo);
			}
			if (_idTrabajoMod == 1 || _idTrabajoMod == 113 || _idTrabajoMod == 115 || _idTrabajoMod == 116
					|| _idTrabajoMod == 117 || _idTrabajoMod == 118 || _idTrabajoMod == 119 || _idTrabajoMod == 120
					|| (_idTrabajoMod >= 163 && _idTrabajoMod <= 169)) {
				boolean resultado = trabajoPagoFM();
				return resultado;
			}
			Map<Integer, Integer> ingredPorModelo = new TreeMap<>();
			for (Entry<Integer, Integer> ingrediente : _ingredientes.entrySet()) {
				int idObjeto = ingrediente.getKey();
				int cantObjeto = ingrediente.getValue();
				Objeto obj = MundoDofus.getObjeto(idObjeto);
				if (obj == null || obj.getCantidad() < cantObjeto) {
					GestorSalida.ENVIAR_Ec_INICIAR_RECETA(_artesano, "EI");
					GestorSalida.ENVIAR_Ec_INICIAR_RECETA(_cliente, "EI");
					return false;
				}
				int nuevaCant = obj.getCantidad() - cantObjeto;
				if (nuevaCant == 0) {
					if (_artesano.tieneObjetoID(idObjeto)) {
						_artesano.borrarObjetoRemove(idObjeto);
						GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_artesano, idObjeto);
					} else {
						_cliente.borrarObjetoRemove(idObjeto);
						GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_cliente, idObjeto);
					}
					MundoDofus.eliminarObjeto(idObjeto);
				} else {
					obj.setCantidad(nuevaCant);
					if (_artesano.tieneObjetoID(idObjeto)) {
						GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_artesano, obj);
					} else {
						GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_cliente, obj);
					}
				}
				int idModelo = obj.getIDModelo();
				if (ingredPorModelo.get(idModelo) == null) {
					ingredPorModelo.put(idModelo, cantObjeto);
				} else {
					int nueva = ingredPorModelo.get(idModelo) + cantObjeto;
					ingredPorModelo.remove(idModelo);
					ingredPorModelo.put(idModelo, nueva);
				}
			}
			if (ingredPorModelo.containsKey(7508)) {
				firmado = true;
			}
			ingredPorModelo.remove(7508);
			GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_artesano);
			GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_cliente);
			StatsOficio SO = _artesano.getOficioPorTrabajo(_idTrabajoMod);
			int idReceta = MundoDofus.getIDRecetaPorIngredientes(SO.getOficio().listaRecetaPorTrabajo(_idTrabajoMod),
					ingredPorModelo);
			if (idReceta == -1 || !SO.getOficio().puedeReceta(_idTrabajoMod, idReceta)) {
				GestorSalida.ENVIAR_Ec_INICIAR_RECETA(_artesano, "EI");
				GestorSalida.ENVIAR_IO_ICONO_OBJ_INTERACTIVO(_artesano.getMapa(), _artesano.getID(), "-");
				GestorSalida.ENVIAR_Ec_INICIAR_RECETA(_cliente, "EI");
				_ingredientes.clear();
				return false;
			}
			int suerte = CentroInfo.getSuertePorNroCasillaYNivel(SO.getNivel(), _ingredientes.size());
			int jet = Formulas.getRandomValor(1, 100);
			boolean exito = suerte >= jet;
			if (!exito) {
				GestorSalida.ENVIAR_Ec_INICIAR_RECETA(_artesano, "EF");
				GestorSalida.ENVIAR_IO_ICONO_OBJ_INTERACTIVO(_artesano.getMapa(), _artesano.getID(), "-" + idReceta);
				GestorSalida.ENVIAR_Im_INFORMACION(_artesano, "0118");
				GestorSalida.ENVIAR_Im_INFORMACION(_cliente, "0118");
			} else {
				Objeto nuevoObj = MundoDofus.getObjModelo(idReceta).crearObjDesdeModelo(1, false);
				if (firmado) {
					nuevoObj.addTextoStat(988, _artesano.getNombre());
				}
				Objeto igual = null;
				if ((igual = _cliente.getObjSimilarInventario(nuevoObj)) == null) {
					MundoDofus.addObjeto(nuevoObj, true);
					_cliente.addObjetoPut(nuevoObj);
					GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_cliente, nuevoObj);
				} else {
					igual.setCantidad(igual.getCantidad() + 1);
					GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_cliente, igual);
					nuevoObj = igual;
				}
				GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_cliente);
				String statsNuevoObj = nuevoObj.convertirStatsAString(0);
				String todaInfo = nuevoObj.stringObjetoConPalo(1);
				GestorSalida.ENVIAR_ErK_RESULTADO_TRABAJO(_artesano, "O", "+", todaInfo);
				GestorSalida.ENVIAR_ErK_RESULTADO_TRABAJO(_cliente, "O", "+", todaInfo);
				GestorSalida.ENVIAR_Ec_INICIAR_RECETA(_artesano,
						"K;" + idReceta + ";T" + _cliente.getNombre() + ";" + statsNuevoObj);
				GestorSalida.ENVIAR_Ec_INICIAR_RECETA(_cliente,
						"K;" + idReceta + ";B" + _artesano.getNombre() + ";" + statsNuevoObj);
				GestorSalida.ENVIAR_IO_ICONO_OBJ_INTERACTIVO(_artesano.getMapa(), _artesano.getID(), "+" + idReceta);
			}
			int xpGanada = (int) (CentroInfo.calculXpGanadaEnOficio(SO.getNivel(), _ingredientes.size())
					* Emu.RATE_XP_OFICIO);
			SO.addXP(_artesano, xpGanada);
			ArrayList<StatsOficio> statsOficios = new ArrayList<>();
			statsOficios.add(SO);
			GestorSalida.ENVIAR_JX_EXPERINENCIA_OFICIO(_artesano, statsOficios);
			_ingredientes.clear();
			return exito;
		}

		private boolean trabajoPagoFM() {
			boolean firmado = false;
			Objeto objAMaguear = null, objRunaFirma = null, objModificador = null;
			int esCambioElemento = 0, statAgre = -1, esCambiadoStats = 0, agregar = 0, idABorrar = -1;
			int runa = 0;
			boolean vip = false;
			String statAMaguear = "-1";
			for (int idIngrediente : _ingredientes.keySet()) {
				Objeto ing = MundoDofus.getObjeto(idIngrediente);
				if (ing == null) {
					GestorSalida.ENVIAR_Ec_INICIAR_RECETA(_artesano, "EI");
					GestorSalida.ENVIAR_Ec_INICIAR_RECETA(_cliente, "EI");
					GestorSalida.ENVIAR_IO_ICONO_OBJ_INTERACTIVO(_artesano.getMapa(), _artesano.getID(), "-");
					_ingredientes.clear();
					return false;
				}
				int idModelo = ing.getModelo().getID();
				switch (idModelo) {
				case 1333:// pocion chispa
					statAgre = 99;
					esCambioElemento = ing.getModelo().getNivel();
					objModificador = ing;
					break;
				case 1335:// pocion llovisna
					statAgre = 96;
					esCambioElemento = ing.getModelo().getNivel();
					objModificador = ing;
					break;
				case 1337:// pocion de corriente de airee
					statAgre = 98;
					esCambioElemento = ing.getModelo().getNivel();
					objModificador = ing;
					break;
				case 1338:// pocion de sacudida
					statAgre = 97;
					esCambioElemento = ing.getModelo().getNivel();
					objModificador = ing;
					break;
				case 1340:// pocion derrumbamiento
					statAgre = 97;
					esCambioElemento = ing.getModelo().getNivel();
					objModificador = ing;
					break;
				case 1341:// pocion chaparron
					statAgre = 96;
					esCambioElemento = ing.getModelo().getNivel();
					objModificador = ing;
					break;
				case 1342:// pocion de rafaga
					statAgre = 98;
					esCambioElemento = ing.getModelo().getNivel();
					objModificador = ing;
					break;
				case 1343:// pocion de Flameacion
					statAgre = 99;
					esCambioElemento = ing.getModelo().getNivel();
					objModificador = ing;
					break;
				case 1345:// pocion Incendio
					statAgre = 99;
					esCambioElemento = ing.getModelo().getNivel();
					objModificador = ing;
					break;
				case 1346:// pocion Tsunami
					statAgre = 96;
					esCambioElemento = ing.getModelo().getNivel();
					objModificador = ing;
					break;
				case 1347:// pocion huracan
					statAgre = 98;
					esCambioElemento = ing.getModelo().getNivel();
					objModificador = ing;
					break;
				case 1348:// pocion de seismo
					statAgre = 97;
					esCambioElemento = ing.getModelo().getNivel();
					objModificador = ing;
					break;
				case 1519:// Fuerza
					objModificador = ing;
					statAMaguear = "76";
					agregar = 1;
					runa = 1;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 1521:// Sabiduria
					objModificador = ing;
					statAMaguear = "7c";
					agregar = 1;
					runa = 6;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 1522:// Inteligencia
					objModificador = ing;
					statAMaguear = "7e";
					agregar = 1;
					runa = 1;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 1523:// Vita
					objModificador = ing;
					statAMaguear = "7d";
					agregar = 3;
					runa = 1;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 1524:// Agi
					objModificador = ing;
					statAMaguear = "77";
					agregar = 1;
					runa = 1;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 1525:// suerte
					objModificador = ing;
					statAMaguear = "7b";
					agregar = 1;
					runa = 1;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 1545:// Pa fuerza
					objModificador = ing;
					statAMaguear = "76";
					agregar = 3;
					runa = 3;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 1546:// Pa Sabiduria
					objModificador = ing;
					statAMaguear = "7c";
					agregar = 3;
					runa = 18;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 1547:// Pa Inteligencia
					objModificador = ing;
					statAMaguear = "7e";
					agregar = 3;
					runa = 3;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 1548:// Pa Vitalidad
					objModificador = ing;
					statAMaguear = "7d";
					agregar = 10;
					runa = 10;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 1549:// Pa agilidad
					objModificador = ing;
					statAMaguear = "77";
					agregar = 3;
					runa = 3;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 1550:// Pa suerte
					objModificador = ing;
					statAMaguear = "7b";
					agregar = 3;
					runa = 10;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 1551:// Ra Fuerza
					objModificador = ing;
					statAMaguear = "76";
					agregar = 10;
					runa = 10;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 1552:// Ra Sabiduria
					objModificador = ing;
					statAMaguear = "7c";
					agregar = 10;
					runa = 50;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 1553:// Ra Iniciativa
					objModificador = ing;
					statAMaguear = "7e";
					agregar = 10;
					runa = 10;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 1554:// Ra Vida
					objModificador = ing;
					statAMaguear = "7d";
					agregar = 30;
					runa = 10;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 1555:// Ra Agilidad
					objModificador = ing;
					statAMaguear = "77";
					agregar = 10;
					runa = 10;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 1556:// Ra suerte
					objModificador = ing;
					statAMaguear = "7b";
					agregar = 10;
					runa = 10;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 1557:// Ga PA
					objModificador = ing;
					statAMaguear = "6f";
					agregar = 1;
					runa = 100;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 1558:// Ga PM
					objModificador = ing;
					statAMaguear = "80";
					agregar = 1;
					runa = 90;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 7433:// Criticos
					objModificador = ing;
					statAMaguear = "73";
					agregar = 1;
					runa = 30;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 7434:// curas
					objModificador = ing;
					statAMaguear = "b2";
					agregar = 1;
					runa = 20;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 7435:// daÑos
					objModificador = ing;
					statAMaguear = "70";
					agregar = 1;
					runa = 20;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 7436:// daÑos %
					objModificador = ing;
					statAMaguear = "8a";
					agregar = 1;
					runa = 2;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 10618:// daÑos %
					objModificador = ing;
					statAMaguear = "8a";
					agregar = 3;
					runa = 6;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 10619:// daÑos %
					objModificador = ing;
					statAMaguear = "8a";
					agregar = 10;
					runa = 12;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 7437:// daÑos reenvio
					objModificador = ing;
					statAMaguear = "dc";
					agregar = 1;
					runa = 2;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 7438:// alcance
					objModificador = ing;
					statAMaguear = "75";
					agregar = 1;
					runa = 50;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 7442:// invocaciones
					objModificador = ing;
					statAMaguear = "b6";
					agregar = 1;
					runa = 30;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 7443:// Pod
					objModificador = ing;
					statAMaguear = "9e";
					agregar = 10;
					runa = 1;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 7444:// Pa pod
					objModificador = ing;
					statAMaguear = "9e";
					agregar = 30;
					runa = 1; // ?
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 7445:// Ra pod
					objModificador = ing;
					statAMaguear = "9e";
					agregar = 100;
					runa = 1; // ?
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 7446:// trampa
					objModificador = ing;
					statAMaguear = "e1";
					agregar = 1;
					runa = 15;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 7447:// trampa %
					objModificador = ing;
					statAMaguear = "e2";
					agregar = 1;
					runa = 2;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 7448:// iniciativa
					objModificador = ing;
					statAMaguear = "ae";
					agregar = 10;
					runa = 1;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 7449:// Pa iniciativa
					objModificador = ing;
					statAMaguear = "ae";
					agregar = 30;
					runa = 3;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 7450:// Ra iniciativa
					objModificador = ing;
					statAMaguear = "ae";
					agregar = 100;
					runa = 10;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 7451:// Prospeccion
					objModificador = ing;
					statAMaguear = "b0";
					agregar = 1;
					runa = 5;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 7452:// resistencia fuego
					objModificador = ing;
					statAMaguear = "f3";
					agregar = 1;
					runa = 4;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 7453:// resistencia aire
					objModificador = ing;
					statAMaguear = "f2";
					agregar = 1;
					runa = 4;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 7454:// resistencia agua
					objModificador = ing;
					statAMaguear = "f1";
					agregar = 1;
					runa = 4;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 7455:// resistencia tierra
					objModificador = ing;
					statAMaguear = "f0";
					agregar = 1;
					runa = 4;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 7456:// resistencia neutral
					objModificador = ing;
					statAMaguear = "f4";
					agregar = 1;
					runa = 4;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 7457:// resistencia % fuego
					objModificador = ing;
					statAMaguear = "d5";
					agregar = 1;
					runa = 5;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 7458:// resistencia % aire
					objModificador = ing;
					statAMaguear = "d4";
					agregar = 1;
					runa = 5;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 7459:// resistencia % tierra
					objModificador = ing;
					statAMaguear = "d2";
					agregar = 1;
					runa = 5;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 7460:// resistencia % neutral
					objModificador = ing;
					statAMaguear = "d6";
					agregar = 1;
					runa = 5;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 7560:// resistencia % agua
					objModificador = ing;
					statAMaguear = "d3";
					agregar = 1;
					runa = 5;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 8379:// runa vida
					objModificador = ing;
					statAMaguear = "7d";
					agregar = 10;
					runa = 10;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 10662:// runa ra prospe
					objModificador = ing;
					statAMaguear = "b0";
					agregar = 3;
					runa = 15;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 7508:// Runa de Firma
					firmado = true;
					objRunaFirma = ing;
					break;
				case 11118:// Fuerza
					vip = true;
					objModificador = ing;
					statAMaguear = "76";
					agregar = 15;
					runa = 1;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 11119:// Sabiduria
					vip = true;
					objModificador = ing;
					statAMaguear = "7c";
					agregar = 15;
					runa = 1;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 11120:// Inteligencia
					vip = true;
					objModificador = ing;
					statAMaguear = "7e";
					agregar = 15;
					runa = 1;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 11121:// Vita
					vip = true;
					objModificador = ing;
					statAMaguear = "7d";
					agregar = 45;
					runa = 1;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 11122:// Agi
					vip = true;
					objModificador = ing;
					statAMaguear = "77";
					agregar = 15;
					runa = 1;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 11123:// suerte
					vip = true;
					objModificador = ing;
					statAMaguear = "7b";
					agregar = 15;
					runa = 1;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 11124:// Prospeccion
					vip = true;
					objModificador = ing;
					statAMaguear = "b0";
					agregar = 10;
					runa = 1;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 11125:// Criticos
					vip = true;
					objModificador = ing;
					statAMaguear = "73";
					agregar = 3;
					runa = 1;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 11126:// curas
					vip = true;
					objModificador = ing;
					statAMaguear = "b2";
					agregar = 5;
					runa = 1;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 11127:// daÑos
					vip = true;
					objModificador = ing;
					statAMaguear = "70";
					agregar = 5;
					runa = 1;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 11128:// daÑos %
					vip = true;
					objModificador = ing;
					statAMaguear = "8a";
					agregar = 10;
					runa = 1;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				case 11129:// daÑos reenvio
					vip = true;
					objModificador = ing;
					statAMaguear = "dc";
					agregar = 5;
					runa = 1;
					esCambiadoStats = ing.getModelo().getNivel();
					break;
				default:
					if (ing.getModelo().getCostePA() > 0) {
						objAMaguear = ing;
					}
					if ((ing.getModelo().getTipo() != 1) && (ing.getModelo().getTipo() != 2)
							&& (ing.getModelo().getTipo() != 3) && (ing.getModelo().getTipo() != 4)
							&& (ing.getModelo().getTipo() != 5) && (ing.getModelo().getTipo() != 6)
							&& (ing.getModelo().getTipo() != 7) && (ing.getModelo().getTipo() != 8)
							&& (ing.getModelo().getTipo() != 9) && (ing.getModelo().getTipo() != 10)
							&& (ing.getModelo().getTipo() != 11) && (ing.getModelo().getTipo() != 16)
							&& (ing.getModelo().getTipo() != 17) && (ing.getModelo().getTipo() != 19)
							&& (ing.getModelo().getTipo() != 20) && (ing.getModelo().getTipo() != 21)
							&& (ing.getModelo().getTipo() != 22) && (ing.getModelo().getTipo() != 81)
							&& (ing.getModelo().getTipo() != 102) && (ing.getModelo().getTipo() != 114)) {
						continue;
					}
					objAMaguear = ing;
					idABorrar = idIngrediente;
					Personaje modificado = _artesano.tieneObjetoID(idIngrediente) ? _artesano : _cliente;
					if (objAMaguear.getCantidad() > 1) {
						Objeto nuevoObj = Objeto.clonarObjeto(objAMaguear, 1);
						objAMaguear.setCantidad(objAMaguear.getCantidad() - 1);
						GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(modificado, objAMaguear);
						GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(modificado, nuevoObj);
						objAMaguear = nuevoObj;
					} else if (objAMaguear.getCantidad() == 1) {
						Objeto nuevoObj = Objeto.clonarObjeto(objAMaguear, 1); // COGE EL OBJETO QUE YO PONGO, EL KIM
						modificado.borrarObjetoRemove(objAMaguear.getID()); // borra de mundo y de pj
						MundoDofus.eliminarObjeto(objAMaguear.getID());
						GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(modificado, objAMaguear.getID()); // borra el objeto del
																									// cliente
						GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(modificado, nuevoObj);
						objAMaguear = nuevoObj;
					}
				}
			}
			StatsOficio oficio = _artesano.getOficioPorTrabajo(_idTrabajoMod);
			oficio.addXP(_artesano, (int) (Emu.RATE_XP_OFICIO + 9.0 / 10.0) * 10);
			if (oficio == null || objAMaguear == null || objModificador == null) {
				GestorSalida.ENVIAR_Ec_INICIAR_RECETA(_artesano, "EI");
				GestorSalida.ENVIAR_Ec_INICIAR_RECETA(_cliente, "EI");
				GestorSalida.ENVIAR_IO_ICONO_OBJ_INTERACTIVO(_artesano.getMapa(), _artesano.getID(), "-");
				_ingredientes.clear();
				return false;
			}
			if (idABorrar != -1) {
				_ingredientes.remove(idABorrar);
			}
			ObjetoModelo objModeloMaguear = objAMaguear.getModelo();
			int idObjMaguear = objAMaguear.getID();
			int suerte = 0;
			int nivelOficio = oficio.getNivel();
			int idObjModMaguear = objModeloMaguear.getID();
			String statStringObj = objAMaguear.convertirStatsAString(0);
			if (esCambioElemento > 0 && esCambiadoStats == 0) {// si cambia de elemento
				suerte = Formulas.calculoPorcCambioElenemto(nivelOficio, objModeloMaguear.getNivel(), esCambioElemento);
				if (suerte > 100 - (nivelOficio / 20)) {
					suerte = 100 - (nivelOficio / 20);
				}
				if (suerte < (nivelOficio / 20)) {
					suerte = (nivelOficio / 20);
				}
			} else if (esCambiadoStats > 0 && esCambioElemento == 0) {// si cambia de stats
				int pesoTotalActual = 1;
				int pesoActualStat = 1;
				if (!statStringObj.isEmpty()) {
					pesoTotalActual = pesoTotalActualObj(statStringObj, objAMaguear);
					pesoActualStat = pesoStatActual(objAMaguear, statAMaguear);
				}
				int pesoTotalBase = pesoTotalBaseObj(idObjModMaguear);
				if (pesoTotalBase < 0) {
					pesoTotalBase = 0;
				}
				if (pesoActualStat < 0) {
					pesoActualStat = 0;
				}
				if (pesoTotalActual < 0) {
					pesoTotalActual = 0;
				}
				float coef = 1;
				int tieneStatBase = statBaseObjeto(objAMaguear, statAMaguear);
				int tieneStatActual = statActualObjeto(objAMaguear, statAMaguear);
				if (tieneStatBase == 1 && tieneStatActual == 1 || tieneStatBase == 1 && tieneStatActual == 0) {
					coef = 1.0f;
				} else if (tieneStatBase == 2 && tieneStatActual == 2) {
					coef = 0.50f;
				} else if (tieneStatBase == 0 && tieneStatActual == 0 || tieneStatBase == 0 && tieneStatActual == 1) {
					coef = 0.25f;
				}
				float tolerancia = 1;
				int diferencia = (int) (pesoTotalBase * tolerancia) - pesoTotalActual;
				suerte = (int) Formulas.suerteFM(pesoTotalBase, pesoTotalActual, pesoActualStat, runa, diferencia,
						coef);
				suerte = suerte + oficio.getAdicional();
				if (vip) {
					suerte += 30;
				}
				if (suerte <= 0) {
					suerte = 1;
				} else if (suerte > 100) {
					suerte = 100;
				}
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_artesano, "Tu Éxito fu� : " + suerte + "%", Colores.ROJO);
			}
			int jet = Formulas.getRandomValor(1, 100);
			boolean exito = suerte >= jet;
			if (!exito) { // falla magueo
				if (objRunaFirma != null) {
					Personaje modificado = _artesano.tieneObjetoID(objRunaFirma.getID()) ? _artesano : _cliente;
					int nuevaCant = objRunaFirma.getCantidad() - 1;
					if (nuevaCant <= 0) {
						modificado.borrarObjetoRemove(objRunaFirma.getID());
						MundoDofus.eliminarObjeto(objRunaFirma.getID());
						GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(modificado, objRunaFirma.getID());
					} else {
						objRunaFirma.setCantidad(nuevaCant);
						GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_artesano, objRunaFirma);
					}
				}
				if (objModificador != null) {
					Personaje modificado = _artesano.tieneObjetoID(objModificador.getID()) ? _artesano : _cliente;
					int nuevaCant = objModificador.getCantidad() - 1;
					if (nuevaCant <= 0) {
						modificado.borrarObjetoRemove(objModificador.getID());
						MundoDofus.eliminarObjeto(objModificador.getID());
						GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(modificado, objModificador.getID());
					} else {
						objModificador.setCantidad(nuevaCant);
						GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(modificado, objModificador);
					}
				}
				if (_artesano.tieneObjetoID(idObjMaguear)) {
					_artesano.borrarObjetoRemove(idObjMaguear);
					_cliente.addObjetoPut(objAMaguear);
					GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_artesano, idObjMaguear);
					GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_cliente, objAMaguear);
				}
				if (!statStringObj.isEmpty()) {
					String statsStr = objAMaguear.stringStatsFCForja(objAMaguear, runa);
					objAMaguear.clearTodo();
					objAMaguear.convertirStringAStats(statsStr);
				}
				GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_cliente);
				GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(_cliente, objAMaguear);
				String todaInfo = objAMaguear.stringObjetoConPalo(1);
				GestorSalida.ENVIAR_ErK_RESULTADO_TRABAJO(_artesano, "O", "+", todaInfo);
				GestorSalida.ENVIAR_ErK_RESULTADO_TRABAJO(_cliente, "O", "+", todaInfo);

				GestorSalida.ENVIAR_IO_ICONO_OBJ_INTERACTIVO(_artesano.getMapa(), _artesano.getID(),
						"-" + idObjModMaguear);
				GestorSalida.ENVIAR_Im_INFORMACION(_artesano, "0183");
				GestorSalida.ENVIAR_Im_INFORMACION(_cliente, "0183");

				_artesano.setForjaEcK("EF");
				_cliente.setForjaEcK("EF");
			} else {
				int coef = 0;
				if (esCambioElemento == 1) {
					coef = 50;
				} else if (esCambioElemento == 25) {
					coef = 65;
				} else if (esCambioElemento == 50) {
					coef = 85;
				}
				if (firmado) {
					objAMaguear.addTextoStat(985, _artesano.getNombre());
				}
				if (esCambioElemento > 0 && esCambiadoStats == 0) {
					for (EfectoHechizo EH : objAMaguear.getEfectos()) {
						if (EH.getEfectoID() != 100) {
							continue;
						}
						String[] infos = EH.getArgs().split(";");
						try {
							int min = Integer.parseInt(infos[0], 16);
							// int max = Integer.parseInt(infos[1], 16);
							int nuevoMin = (min * coef) / 100;
							// int nuevoMax = (int) ((max * coef) / 100);
							if (nuevoMin == 0) {
								nuevoMin = 1;
							}
							// String nuevoRango = "1d" + (nuevoMax - nuevoMin + 1) + "+" + (nuevoMin - 1);
							// String nuevosArgs = Integer.toHexString(nuevoMin) + ";" +
							// Integer.toHexString(nuevoMax) + ";-1;-1;0;" + nuevoRango;
							// EH.setArgs(nuevosArgs);
							EH.setEfectoID(statAgre);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} else if (esCambiadoStats > 0 && esCambioElemento == 0) {// Si se modifica los
					// stats(runa)
					boolean negativo = false;
					int actualStat = statActualObjeto(objAMaguear, statAMaguear);
					if (actualStat == 2) {// los stats existentes actual son negativos
						if (statAMaguear.compareTo("7b") == 0) {
							statAMaguear = "98";
							negativo = true;
						}
						if (statAMaguear.compareTo("77") == 0) {
							statAMaguear = "9a";
							negativo = true;
						}
						if (statAMaguear.compareTo("7e") == 0) {
							statAMaguear = "9b";
							negativo = true;
						}
						if (statAMaguear.compareTo("76") == 0) {
							statAMaguear = "9d";
							negativo = true;
						}
						if (statAMaguear.compareTo("7c") == 0) {
							statAMaguear = "9c";
							negativo = true;
						}
						if (statAMaguear.compareTo("7d") == 0) {
							statAMaguear = "99";
							negativo = true;
						}
					}
					if (actualStat == 1 || actualStat == 2) {
						String statsStr = objAMaguear.convertirStatsAStringFM(statAMaguear, objAMaguear, agregar,
								negativo);
						objAMaguear.clearTodo();
						objAMaguear.convertirStringAStats(statsStr);
					} else {
						if (statStringObj.isEmpty()) {
							String statsStr = statAMaguear + "#" + Integer.toHexString(agregar) + "#0#0#0d0+" + agregar;
							objAMaguear.clearTodo();
							objAMaguear.convertirStringAStats(statsStr);
						} else {
							String statsStr = objAMaguear.convertirStatsAStringFM(statAMaguear, objAMaguear, agregar,
									negativo) + "," + statAMaguear + "#" + Integer.toHexString(agregar) + "#0#0#0d0+"
									+ agregar;
							objAMaguear.clearTodo();
							objAMaguear.convertirStringAStats(statsStr);
						}
					}
				}
				if (objRunaFirma != null) {
					Personaje modificado = _artesano.tieneObjetoID(objRunaFirma.getID()) ? _artesano : _cliente;
					int nuevaCant = objRunaFirma.getCantidad() - 1;
					if (nuevaCant <= 0) {
						modificado.borrarObjetoRemove(objRunaFirma.getID());
						MundoDofus.eliminarObjeto(objRunaFirma.getID());
						GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(modificado, objRunaFirma.getID());
					} else {
						objRunaFirma.setCantidad(nuevaCant);
						GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(modificado, objRunaFirma);
					}
				}
				if (objModificador != null) {
					Personaje modificado = _artesano.tieneObjetoID(objModificador.getID()) ? _artesano : _cliente;
					int nuevaCant = objModificador.getCantidad() - 1;
					if (nuevaCant <= 0) {
						modificado.borrarObjetoRemove(objModificador.getID());
						MundoDofus.eliminarObjeto(objModificador.getID());
						GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(modificado, objModificador.getID());
					} else {
						objModificador.setCantidad(nuevaCant);
						GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(modificado, objModificador);
					}
				}
				if (_artesano.tieneObjetoID(idObjMaguear)) {
					_artesano.borrarObjetoRemove(idObjMaguear);
					_cliente.addObjetoPut(objAMaguear);
					GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_artesano, idObjMaguear);
					GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_cliente, objAMaguear);
				}
				GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_cliente);// Pods
				GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(_cliente, objAMaguear);
				String statsNuevoObj = objAMaguear.convertirStatsAString(0);
				String todaInfo = objAMaguear.stringObjetoConPalo(1);
				GestorSalida.ENVIAR_IO_ICONO_OBJ_INTERACTIVO(_artesano.getMapa(), _artesano.getID(),
						"+" + idObjModMaguear);
				GestorSalida.ENVIAR_ErK_RESULTADO_TRABAJO(_artesano, "O", "+", todaInfo);
				GestorSalida.ENVIAR_ErK_RESULTADO_TRABAJO(_cliente, "O", "+", todaInfo);
				_artesano.setForjaEcK("K;" + idObjModMaguear + ";T" + _cliente.getNombre() + ";" + statsNuevoObj);
				_cliente.setForjaEcK("K;" + idObjModMaguear + ";B" + _artesano.getNombre() + ";" + statsNuevoObj);
			}
			_ingredientes.clear();
			return exito;
		}
	}
}
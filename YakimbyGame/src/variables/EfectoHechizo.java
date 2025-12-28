package variables;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.Timer;

import estaticos.Camino;
import estaticos.CentroInfo;
import estaticos.Encriptador;
import estaticos.Formulas;
import estaticos.GestorSalida;
import estaticos.MundoDofus;
import servidor.Colores;
import variables.Hechizo.StatsHechizos;
import variables.Mapa.Celda;
import variables.MobModelo.MobGrado;
import variables.Pelea.Glifo;
import variables.Pelea.Luchador;
import variables.Pelea.Trampa;

public class EfectoHechizo {
	private int _efectoID;
	private int _turnos = 0;
	private String _valores = "0d0+0";
	private byte _suerte = 100;
	private String _args;
	private int _valor = 0;
	private Luchador _lanzador = null;
	private Luchador _lanzador2 = null;
	private int _hechizoID = 0;
	private int _nivelHechizo = 1;
	private boolean _desbuffeable = true;
	private int _duracion = 0;
	private Celda _celdaLanz = null;
	private boolean isBegin = false;
	private boolean poison = false;

	public boolean getPoison() {
		return poison;
	}

	EfectoHechizo(int aID, String aArgs, int aHechizo, int aNivelHechizo) {
		_efectoID = aID;
		_args = aArgs;
		_hechizoID = aHechizo;
		_nivelHechizo = aNivelHechizo;
		try {
			String[] args = _args.split(";");
			_valor = Integer.parseInt(args[0]);
			_turnos = Integer.parseInt(args[3]);
			_suerte = Byte.parseByte(args[4]);
			_valores = args[5];
		} catch (Exception e) {
		}
	}

	EfectoHechizo(int id, int aValor, int aDuracion, int aTurnos, boolean debuffeable, Luchador aLanzador, String args2,
			int aHechizoID, boolean Poison) {
		_efectoID = id;
		_valor = aValor;
		_turnos = aTurnos;
		_desbuffeable = debuffeable;
		_lanzador = aLanzador;
		_duracion = aDuracion;
		_args = args2;
		_hechizoID = aHechizoID;
		poison = Poison;
		try {
			String[] args = _args.split(";");
			_valores = args[5];
		} catch (Exception e) {
		}
	}

	private static int[] efectosReto = { 77, 169, 84, 168, 108, 116, 320, 81, 82, 85, 86, 87, 88, 89, 91, 92, 93, 94,
			95, 96, 97, 98, 99, 100, 101 };

	private static boolean esEfectoReto(int efecto) {
		for (Integer e : efectosReto) {
			if (e == efecto) {
				return true;
			}
		}
		return false;
	}

	public boolean esMismoHechizo(int id) {
		return _hechizoID == id;
	}

	public int getDuracion() {
		return _duracion;
	}

	public int getTurnos() {
		return _turnos;
	}

	public boolean esDesbufeable() {
		return _desbuffeable;
	}

	public void setTurnos(int aturnos) {
		_turnos = aturnos;
	}

	public int getEfectoID() {
		return _efectoID;
	}

	public int getValor() {
		return _valor;
	}

	public String getValores() {
		return _valores;
	}

	public int getSuerte() {
		return _suerte;
	}

	public String getArgs() {
		return _args;
	}

	public void setArgs(String args) {
		_args = args;
	}

	public void setEfectoID(int id) {
		_efectoID = id;
	}

	public void setValor(short v) {
		_valor = v;
	}

	int disminuirDuracion() {
		_duracion -= 1;
		return _duracion;
	}

	void aplicarBuffDeInicioTurno(Pelea pelea, Luchador afectado) {
		ArrayList<Luchador> objetivos = new ArrayList<>();
		objetivos.add(afectado);
		_turnos = -1;
		isBegin = true;
		aplicarAPelea(pelea, _lanzador, objetivos, false, false);
		isBegin = false;
	}

	void aplicarHechizoAPelea(Pelea pelea, Luchador lanzador, Celda casilla, ArrayList<Luchador> objetivos,
			ArrayList<Celda> celdas, boolean glifo) {
		_celdaLanz = casilla;
		aplicarAPelea(pelea, lanzador, objetivos, false, glifo);
	}

	public Luchador getLanzador() {
		return _lanzador;
	}

	public int getHechizoID() {
		return _hechizoID;
	}

	private int getMaxMinHechizo(Luchador objetivo, int valor) {
		int val = valor;
		if (objetivo.tieneBuff(782)) {
			int max = Integer.parseInt(_args.split(";")[1]);
			if (max == -1) {
				max = Integer.parseInt(_args.split(";")[0]);
			}
			if (max > valor) {
				valor = max;
			}
		}
		if (objetivo.tieneBuff(781)) {
			int max = Integer.parseInt(_args.split(";")[1]);
			if (max == -1) {
				max = Integer.parseInt(_args.split(";")[0]);
			}
			if (max < valor) {
				valor = max;
			}
		}
		val = valor;
		return val;
	}

	static ArrayList<Luchador> getAfectados(Pelea pelea, ArrayList<Celda> celdas, int hechizo) {
		ArrayList<Luchador> objetivos = new ArrayList<>();
		for (Celda celda : celdas) {
			if (celda == null) {
				continue;
			}
			Luchador luch = celda._mapa.getPrimerLuchador(celda.getID());
			if (luch == null) {
				continue;
			}
			if (luch.getTransportando() != null) {
				if (luch.getTransportando().getTransportadoPor() != null) {
					objetivos.add(luch.getTransportando().getTransportadoPor());
				} else {
					objetivos.add(luch);
				}
			} else {
				objetivos.add(luch);
			}
		}
		return objetivos;
	}

	private static int aplicarBuffContraGolpe(int daÑoFinal, Luchador objetivo, Luchador lanzador, Pelea pelea,
			boolean isPoison) {
		for (int id : CentroInfo.BUFF_ACCION_RESPUESTA) {
			for (EfectoHechizo buff : objetivo.getBuffsPorEfectoID(id)) {
				if (objetivo.estaMuerto()) {
					return 0;
				}
				switch (id) {
				case 9:// retrocede al ser golpeado
					if (isPoison) {
						continue;
					}
					int d = Camino.distanciaEntreDosCeldas(pelea.getMapaCopia(), objetivo.getCeldaPelea().getID(),
							lanzador.getCeldaPelea().getID());
					if (d > 1) {
						continue;
					}
					int elusion = buff.getValor();
					int azar = Formulas.getRandomValor(1, 100);
					if (azar > elusion) {
						continue;
					}
					int nroCasillas = 0;
					try {
						nroCasillas = Integer.parseInt(buff.getArgs().split(";")[1]);
					} catch (Exception e) {
					}
					if (nroCasillas == 0 || objetivo.tieneEstado(6)) {
						continue;
					}
					Celda aCelda = lanzador.getCeldaPelea();
					Luchador afectado = null;
					Mapa mapaCopia = pelea.getMapaCopia();
					int nuevaCeldaID = Camino.getCeldaDespEmpujon(mapaCopia, aCelda, objetivo.getCeldaPelea(),
							nroCasillas, pelea, objetivo);
					if (nuevaCeldaID == 0) {
						continue;
					}
					daÑoFinal = 0;
					if (nuevaCeldaID < 0) {
						int a = -nuevaCeldaID;
						int coef = Formulas.getRandomValor("1d5+8");
						Luchador empujo = lanzador;
						while (empujo.esInvocacion()) {
							empujo = empujo.getInvocador();
						}
						float b = (empujo.getNivel() / (float) (100.00));
						if (b < 0.1) {
							b = 0.1f;
						}
						float c = b * a;
						daÑoFinal = (int) (coef * c);
						if (daÑoFinal < 1) {
							daÑoFinal = 1;
						}
						if (daÑoFinal > objetivo.getPDVConBuff()) {
							daÑoFinal = objetivo.getPDVConBuff();
						}
						if (daÑoFinal > 0) {
							objetivo.restarPDV(daÑoFinal);
							if (objetivo.getPDVConBuff() <= 0) {
								GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, lanzador.getID() + "",
										objetivo.getID() + ",-" + daÑoFinal);
								pelea.agregarAMuertos(objetivo, lanzador, "retrocede");
								continue;
							}
						}
						a = nroCasillas - a;
						nuevaCeldaID = Camino.getCeldaDespEmpujon(mapaCopia, aCelda, objetivo.getCeldaPelea(), a, pelea,
								objetivo);
						char dir = Camino.getDirEntreDosCeldas(aCelda.getID(), objetivo.getCeldaPelea().getID(),
								mapaCopia, true);
						int celdaSigID = 0;
						if (nuevaCeldaID == 0) {
							celdaSigID = Camino.getSigIDCeldaMismaDir(objetivo.getCeldaPelea().getID(), dir, mapaCopia,
									true);
						} else {
							celdaSigID = Camino.getSigIDCeldaMismaDir(nuevaCeldaID, dir, mapaCopia, true);
						}
						Celda celdaSig = mapaCopia.getCelda(celdaSigID);
						if (celdaSig != null) {
							afectado = mapaCopia.getPrimerLuchador(celdaSigID);
						}
					}
					if (nuevaCeldaID != 0) {
						Celda nueva = mapaCopia.getCelda(nuevaCeldaID);
						if (nueva != null) {
							mapaCopia.removerLuchador(objetivo);
							objetivo.setCeldaPelea(nueva);
							mapaCopia.addLuchador(objetivo);
							GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 5, lanzador.getID() + "",
									objetivo.getID() + "," + nuevaCeldaID);
							try {
								Thread.sleep(500);
							} catch (Exception e) {
							}
						}
					}
					if (daÑoFinal > 0) {
						GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, lanzador.getID() + "",
								objetivo.getID() + ",-" + daÑoFinal);
					}
					if (afectado != null) {
						int daÑoFinal2 = (daÑoFinal / 2);
						if (daÑoFinal2 < 1) {
							daÑoFinal2 = 1;
						}
						if (daÑoFinal2 > afectado.getPDVConBuff()) {
							daÑoFinal2 = afectado.getPDVConBuff();
						}
						if (daÑoFinal2 > 0) {
							afectado.restarPDV(daÑoFinal2);
							GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, lanzador.getID() + "",
									afectado.getID() + ",-" + daÑoFinal2);
							if (afectado.getPDVConBuff() <= 0) {
								pelea.agregarAMuertos(afectado, lanzador, "retro 2");
							}
						}
					}
					if (!Thread.interrupted()) {
						try {
							Thread.sleep(300);
						} catch (Exception e) {
						}
					}
					ArrayList<Trampa> trampas = new ArrayList<>();
					trampas.addAll(pelea.getTrampas());
					for (Trampa trampa : trampas) {
						if (trampa.activada) {
							continue;
						}
						int dist = Camino.distanciaEntreDosCeldas(pelea.getMapaCopia(), trampa.getCelda().getID(),
								nuevaCeldaID);
						if (dist <= trampa.getTamaÑo()) {
							trampa.activaTrampa(objetivo);
						}
					}
					return 0;
				case 79:// Suerte de zurcarak
					if (isPoison) {
						continue;
					}
					try {
						String[] infos = buff.getArgs().split(";");
						int coefDaÑo = Integer.parseInt(infos[0]);
						int coefCura = Integer.parseInt(infos[1]);
						int suerte = Integer.parseInt(infos[2]);
						int rango = Formulas.getRandomValor(0, 99);
						if (rango < suerte)// Cura
						{
							daÑoFinal = -(daÑoFinal * coefCura);
							if (-daÑoFinal > (objetivo.getPDVMaxConBuff() - objetivo.getPDVConBuff())) {
								daÑoFinal = -(objetivo.getPDVMaxConBuff() - objetivo.getPDVConBuff());
							}
						} else { // Cura
							daÑoFinal = daÑoFinal * coefDaÑo;
						}
					} catch (Exception e) {
					}
					break;
				case 776:
					if (objetivo.tieneBuff(776)) {// si posee daÑos incurables
						int pdvMax = objetivo.getPDVMax();
						float pdaÑo = objetivo.getValorBuffPelea(776) / 100.00f;
						pdvMax = pdvMax - (int) (daÑoFinal * pdaÑo);
						if (pdvMax < 0) {
							pdvMax = 0;
						}
						objetivo.setPDVMAX(pdvMax);
					}
					break;
				case 220:
				case 107:// renvoie Dom
					if (isPoison) {
						continue;
					}
					String[] args = buff.getArgs().split(";");
					float coef = 1 + (objetivo.getTotalStatsConBuff().getEfecto(124) / 100);
					int reenvio = 0;
					try {
						if (Integer.parseInt(args[1]) != -1) {
							reenvio = (int) (coef
									* Formulas.getRandomValor(Integer.parseInt(args[0]), Integer.parseInt(args[1])));
						} else {
							reenvio = (int) (coef * Integer.parseInt(args[0]));
						}
					} catch (Exception e) {
						continue;
					}
					if (reenvio > daÑoFinal) {
						reenvio = daÑoFinal;
					}
					daÑoFinal -= reenvio;
					if (reenvio > 0)
					 {
						GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 107, "-1", objetivo.getID() + "," + reenvio);// anuncia
					}
																													// que
																													// ha
																													// reenviado
																													// x
																													// daÑo
					if (reenvio > lanzador.getPDVConBuff()) {
						reenvio = lanzador.getPDVConBuff();
					}
					if (daÑoFinal < 0) {
						daÑoFinal = 0;
					}
					lanzador.restarPDV(reenvio);
					if (reenvio > 0)
					 {
						GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, lanzador.getID() + "",
								lanzador.getID() + ",-" + reenvio); // le resta la vida
					}
					break;
				case 606:// Chatiment (acncien)
					int stat = buff.getValor();
					int jet = Formulas.getRandomValor(buff.getValores());
					objetivo.addBuff(stat, jet, -1, -1, false, buff.getHechizoID(), buff.getArgs(), lanzador, true,
							true);
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, stat, lanzador.getID() + "",
							objetivo.getID() + "," + jet + "," + -1);
					break;
				case 114:
					if (buff._hechizoID == 521) {
						daÑoFinal = daÑoFinal * 2;
					}
					break;
				case 607:// Chatiment (acncien)
					stat = buff.getValor();
					jet = Formulas.getRandomValor(buff.getValores());
					objetivo.addBuff(stat, jet, -1, -1, false, buff.getHechizoID(), buff.getArgs(), lanzador, true,
							true);
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, stat, lanzador.getID() + "",
							objetivo.getID() + "," + jet + "," + -1);
					break;
				case 608:// Chatiment (acncien)
					stat = buff.getValor();
					jet = Formulas.getRandomValor(buff.getValores());
					objetivo.addBuff(stat, jet, -1, -1, false, buff.getHechizoID(), buff.getArgs(), lanzador, true,
							true);
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, stat, lanzador.getID() + "",
							objetivo.getID() + "," + jet + "," + -1);
					break;
				case 609:// Chatiment (acncien)
					stat = buff.getValor();
					jet = Formulas.getRandomValor(buff.getValores());
					objetivo.addBuff(stat, jet, -1, -1, false, buff.getHechizoID(), buff.getArgs(), lanzador, true,
							true);
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, stat, lanzador.getID() + "",
							objetivo.getID() + "," + jet + "," + -1);
					break;
				case 611:// Chatiment (acncien)
					stat = buff.getValor();
					jet = Formulas.getRandomValor(buff.getValores());
					objetivo.addBuff(stat, jet, -1, -1, false, buff.getHechizoID(), buff.getArgs(), lanzador, true,
							true);
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, stat, lanzador.getID() + "",
							objetivo.getID() + "," + jet + "," + -1);
					break;
				case 788:// Castigos
					if (isPoison) {
						continue;
					}
					int porc = (lanzador.getPersonaje() == null ? 1 : 2);
					int gana = (daÑoFinal / porc);
					int stat1 = buff.getValor();
					int max = 0;
					try {
						max = Integer.parseInt(buff.getArgs().split(";")[1]);
					} catch (Exception e) {
					}
					max = max - objetivo.getBonusCastigo(stat1);
					if (max <= 0) {
						continue;
					}
					if (gana > max) {
						gana = max;
					}
					objetivo.setBonusCastigo(objetivo.getBonusCastigo(stat1) + gana, stat1);
					objetivo.addBuff(stat1, gana, 5, 1, false, buff.getHechizoID(), buff.getArgs(), lanzador,
							buff.getPoison(), true);
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, stat1, lanzador.getID() + "",
							objetivo.getID() + "," + gana + "," + 5);
					break;
				default:
					System.out.println("Efecto id " + id + " no definido como EFECTO DE CONTRAGOLPE.");
					break;
				}
			}
		}
		return daÑoFinal;
	}

	void aplicarAPelea(Pelea pelea, Luchador acaster, ArrayList<Luchador> cibles, boolean isCaC, boolean glifo) {
		try {
			if (_turnos != -1) {
				_turnos = Integer.parseInt(_args.split(";")[3]);
			}
		} catch (NumberFormatException e) {
		}
		if (acaster.realLuc != null) {
			_lanzador2 = acaster.realLuc;
		}
		_lanzador = acaster;
		if (_lanzador2 == null) {
			_lanzador2 = _lanzador;
		}
		if (_lanzador.getPersonaje() != null) {
			Personaje perso = _lanzador.getPersonaje();
			if (perso.getHechizosSetClase().containsKey(_hechizoID)) {
				int modi = 0;
				if (_efectoID == 108) {
					modi = perso.getModifSetClase(_hechizoID, 284);
				} else if (_efectoID >= 91 && _efectoID <= 100) {
					modi = perso.getModifSetClase(_hechizoID, 283);
				}
				String jeta = _valores.split("\\+")[0];
				int bonus = Integer.parseInt(_valores.split("\\+")[1]) + modi;
				_valores = jeta + "+" + bonus;
			}
		}
		// pelea.setUltAfec((byte) objetivos.size());
		if (pelea.getTipoPelea() == 4 && _lanzador.getPersonaje() != null && esEfectoReto(_efectoID)) {
			Map<Integer, Integer> copiaRetos = new TreeMap<>();
			copiaRetos.putAll(pelea.getRetos());
			for (Entry<Integer, Integer> entry : copiaRetos.entrySet()) {
				int reto = entry.getKey();
				int exitoReto = entry.getValue();
				if (exitoReto != 0) {
					continue;
				}
				switch (reto) {
				case 21: // circulen
					if (_efectoID == 77 || _efectoID == 169) {
						for (Luchador luch : pelea._inicioLucEquipo2) {
							if (cibles.contains(luch)) {
								GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(pelea, reto);
								exitoReto = 2;
								break;
							}
						}
					}
					break;
				case 22: // el tiempo pasa
					if (_efectoID == 84 || _efectoID == 101 || _efectoID == 168) {
						for (Luchador luch : pelea._inicioLucEquipo2) {
							if (cibles.contains(luch)) {
								GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(pelea, reto);
								exitoReto = 2;
								break;
							}
						}
					}
					break;
				case 23: // perdido de vista
					if (_efectoID == 116 || _efectoID == 320) {
						for (Luchador luch : pelea._inicioLucEquipo2) {
							if (cibles.contains(luch)) {
								GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(pelea, reto);
								exitoReto = 2;
								break;
							}
						}
					}
					break;
				case 31: // focalizacion
					if (pelea.getIDMobReto() != 0) {
						for (Luchador luch : cibles) {
							if (!pelea._inicioLucEquipo2.contains(luch)) {
								continue;
							}
							if (luch.getID() != pelea.getIDMobReto()) {
								GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(pelea, reto);
								exitoReto = 2;
								break;
							}
						}
					}
					break;
				case 32: // elitisa
					if (pelea.getIDMobReto() != 0) {
						for (Luchador luch : cibles) {
							if (!pelea._inicioLucEquipo2.contains(luch)) {
								continue;
							}
							if (luch.getID() != pelea.getIDMobReto()) {
								GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(pelea, reto);
								exitoReto = 2;
								break;
							}
						}
					}
					break;
				case 34: // imprevisible
					if ((_efectoID >= 91 && _efectoID <= 100) || (_efectoID >= 85 && _efectoID <= 89)) {
						if (pelea.getIDMobReto() != 0) {
							for (Luchador luch : cibles) {
								if (!pelea._inicioLucEquipo2.contains(luch)) {
									continue;
								}
								if (luch.getID() != pelea.getIDMobReto()) {
									GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(pelea, reto);
									exitoReto = 2;
									break;
								}
							}
						}
					}
					break;
				case 43: // abnegacion
					if (_efectoID == 108) {
						for (Luchador luch : cibles) {
							if (luch.getID() == _lanzador.getID()) {
								GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(pelea, reto);
								exitoReto = 2;
								break;
							}
						}
					}
					break;
				case 45: // duelo
				case 46: // cada uno con su monstruo
					if ((_efectoID >= 91 && _efectoID <= 100) || (_efectoID >= 85 && _efectoID <= 89)) {
						for (Luchador luch : cibles) {
							if (!pelea._inicioLucEquipo2.contains(luch)) {
								continue;
							}
							if (luch.getPjAtacante() == 0) {
								luch.setPjAtacante(_lanzador.getID());
							} else {
								if (luch.getPjAtacante() != _lanzador.getID()) {
									GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(pelea, reto);
									exitoReto = 2;
									break;
								}
							}
						}
					}
					break;
				}
				if (exitoReto != 0) {
					pelea.getRetos().remove(reto);
					pelea.getRetos().put(reto, exitoReto);
				}
			}
		}
		switch (_efectoID) {
		// FIXME
		case 287:
		case 284:
		case 285:
		case 290:// agregar estos efectos
			break;
		case 4:// Teletransporta a una casilla Huida/Salto felino/ Salto / Teleport
			aplicarEfecto_4(pelea);
			break;
		case 5:// Hace retroceder X casillas
			aplicarEfecto_5(cibles, pelea);
			break;
		case 6:// Hace avanzar X casillas atraccion
			aplicarEfecto_6(cibles, pelea);
			break;
		case 8:// Intercambia la posicion de 2 jugadores
			aplicarEfecto_8(cibles, pelea);
			break;
		case 9:// Esquiva un X% del ataque haciendolo retroceder Y casillas
			aplicarEfecto_9(cibles, pelea);
			break;
		case 50:// Permite levantar un jugador
			aplicarEfecto_50(pelea);
			break;
		case 51:// Lanzar el jugador levantado
			aplicarEfecto_51(pelea);
			break;
		case 77:// Robar PM ok
			aplicarEfecto_77(cibles, pelea);
			break;
		case 78:// + PM
			aplicarEfecto_78(cibles, pelea);
			break;
		case 79:// + X % de posibilidades de que sufras daÑos x X, o de que te cure x Y
			aplicarEfecto_79(cibles, pelea);
			break;
		case 81:// Cura, PDV devueltos
			aplicarEfecto_81(cibles, pelea);
			break;
		case 82:// Robar Vida(fijo)
			aplicarEfecto_82(cibles, pelea, glifo);
			break;
		case 84:// Robar PA
			aplicarEfecto_84(cibles, pelea);
			break;
		case 85:// DaÑos Agua %vida del atacante
			aplicarEfecto_85(cibles, pelea);
			break;
		case 86:// DaÑos Tierra %vida del atacante
			aplicarEfecto_86(cibles, pelea);
			break;
		case 87:// DaÑos Aire %vida del atacante
			aplicarEfecto_87(cibles, pelea);
			break;
		case 88:// DaÑos Fuego %vida del atacante
			aplicarEfecto_88(cibles, pelea);
			break;
		case 89:// DaÑos Neutral %vida del atacante
			aplicarEfecto_89(cibles, pelea);
			break;
		case 90:// Dona % de su vida
			aplicarEfecto_90(cibles, pelea);
			break;
		case 91:// Robar Vida(agua)
			aplicarEfecto_91(cibles, pelea, isCaC, glifo);
			break;
		case 92:// Robar Vida(tierra)
			aplicarEfecto_92(cibles, pelea, isCaC, glifo);
			break;
		case 93:// Robar Vida(aire)
			aplicarEfecto_93(cibles, pelea, isCaC, glifo);
			break;
		case 94:// Robar Vida(fuego)
			aplicarEfecto_94(cibles, pelea, isCaC, glifo);
			break;
		case 95:// Robar Vida(neutral)
			aplicarEfecto_95(cibles, pelea, isCaC, glifo);
			break;
		case 96:// DaÑos Agua
			aplicarEfecto_96(cibles, pelea, isCaC, glifo);
			break;
		case 97:// DaÑos Tierra
			aplicarEfecto_97(cibles, pelea, isCaC, glifo);
			break;
		case 98:// DaÑos Aire
			aplicarEfecto_98(cibles, pelea, isCaC, glifo);
			break;
		case 99:// DaÑos Fuego
			aplicarEfecto_99(cibles, pelea, isCaC, glifo);
			break;
		case 100:// DaÑos Neutral
		case 999:
			aplicarEfecto_100(cibles, pelea, isCaC, glifo);
			break;
		case 101:// - PA ok
			aplicarEfecto_101(cibles, pelea);
			break;
		case 105:// DaÑos reducidos a X
			aplicarEfecto_105(cibles, pelea);
			break;
		case 106:// Reenvia un hechizo de nivel
			aplicarEfecto_106(cibles, pelea);
			break;
		case 107:// Reevnia daÑos, DaÑos devueltos
			aplicarEfecto_107(cibles, pelea);
			break;
		case 108:// Cura, PDV devueltos
			aplicarEfecto_108(cibles, pelea, isCaC, glifo);
			break;
		case 109:// DaÑos para el lanzador
			aplicarEfecto_109(pelea, glifo);
			break;
		case 111:// + X PA OK
			aplicarEfecto_111(cibles, pelea);
			break;
		case 110:// + X vida
		case 118:// + Fuerza
		case 112:// + DaÑos
		case 114:// Multiplica los daÑos por X
		case 115:// + Golpes Criticos
		case 121:// + DaÑos
		case 122:// + Fallos Criticos
		case 123:// + Suerte
		case 124:// + Sabiduria
		case 152:// - Suerte
		case 153:// - Vitalidad
		case 154:// - Agilidad
		case 155:// - Inteligencia
		case 156:// - Sabiduria
		case 178:// + a las curaciones
		case 171:// - Golpes Criticos
		case 182:// + Invocaciones
		case 186:// Disminuye los daÑos
		case 179:// - a las curaciones
		case 157:// - Fuerza
		case 160:// + Esquiva PA
		case 161:// + Esquiva PM
		case 162:// - Esquiva PA
		case 163:// - Esquiva PM
		case 142:// + DaÑos Fisicos
		case 138:// + % daÑos
		case 145:// - DaÑos
			aplicarEfecto_115(cibles, pelea);
			break;
		case 116:// - Alcance
			aplicarEfecto_116(cibles, pelea);
			break;
		case 117:// + Alcance
			aplicarEfecto_117(cibles, pelea);
			break;
		case 119:// + Agilidad
			aplicarEfecto_119(cibles, pelea);
			break;
		case 120:// A�ade X PA ok
			aplicarEfecto_120(pelea);
			break;
		case 125:// + Vitalidad
			aplicarEfecto_125(cibles, pelea);
			break;
		case 126:// + Inteligencia
			aplicarEfecto_126(cibles, pelea);
			break;
		case 127:// Pierde X PM ok
			aplicarEfecto_127(cibles, pelea);
			break;
		case 128:// + PM ok
			aplicarEfecto_128(cibles, pelea);
			break;
		case 130:// robo kamas
			aplicarEfecto_130(cibles, pelea);
			break;
		case 131:// Veneno : X Pdv por PA
			aplicarEfecto_131(cibles, pelea);
			break;
		case 132:// Desechiza
			aplicarEfecto_132(cibles, pelea);
			break;
		case 140:// Pasar el turno
			aplicarEfecto_140(cibles, pelea);
			break;
		case 141:// Mata al blanco
			aplicarEfecto_141(cibles, pelea);
			break;
		case 143:// PDV devueltos para castigo
			aplicarEfecto_143(cibles, pelea);
			break;
		case 144:// - DaÑos (no boosteados)
			aplicarEfecto_144(cibles, pelea);
		case 149:// Cambia la apariencia
			aplicarEfecto_149(cibles, pelea);
			break;
		case 150:// Invisible
			aplicarEfecto_150(cibles, pelea);
			break;
		case 164:// DaÑos reducidos en x%
			aplicarEfecto_164(cibles, pelea);
			break;
		case 165:// Aumenta los daÑos %
			aplicarEfecto_165(cibles, pelea);
			break;
		case 168:// - PA , no esquivable ok
			aplicarEfecto_168(cibles, pelea);
			break;
		case 169:// - PM , no esquivable ok
			aplicarEfecto_169(cibles, pelea);
			break;
		case 176:// + a las prospecciones
			aplicarEfecto_176(cibles, pelea);
			break;
		case 177:// - a las prospecciones
			aplicarEfecto_177(cibles, pelea);
			break;
		case 180:// Doble de sram
			aplicarEfecto_180(pelea);
			break;
		case 181:// Invoca una criatura
			aplicarEfecto_181(pelea);
			break;
		case 183:// Reduccion Magica
			aplicarEfecto_183(cibles, pelea);
			break;
		case 184:// Reduccion Fisica
			aplicarEfecto_184(cibles, pelea);
			break;
		case 185:// Invoca una criatura estatica
			aplicarEfecto_185(pelea);
			break;
		case 202:// Revela todos los objetos invisibles
			aplicarEfecto_202(cibles, pelea);
			break;
		case 210:// Resist % tierra
			aplicarEfecto_210(cibles, pelea);
			break;
		case 211:// Resist % agua
			aplicarEfecto_211(cibles, pelea);
			break;
		case 212:// Resist % aire
			aplicarEfecto_212(cibles, pelea);
			break;
		case 213:// Resist % fuego
			aplicarEfecto_213(cibles, pelea);
			break;
		case 214:// Resist % neutral
			aplicarEfecto_214(cibles, pelea);
			break;
		case 215:// Debilidad % tierra
			aplicarEfecto_215(cibles, pelea);
			break;
		case 216:// Debilidad % agua
			aplicarEfecto_216(cibles, pelea);
			break;
		case 217:// Debilidad % aire
			aplicarEfecto_217(cibles, pelea);
			break;
		case 218:// Debilidad % fuego
			aplicarEfecto_218(cibles, pelea);
			break;
		case 219:// Debilidad % neutral
			aplicarEfecto_219(cibles, pelea);
			break;
		case 405:// mata y reemplaza por una invo
			aplicarEfecto_405(pelea, cibles);
			break;
		case 248:// debilidad fuego
			aplicarEfecto_248(pelea, cibles);
			break;
		case 240:// resistencia tierra
		case 241:// resistencia agua
		case 242:// resistencia aire
		case 243:// resistencia fuego
		case 244:// resistencia neutral
			aplicarEfecto_243(pelea, cibles);
			break;
		case 220:// Reevnia daÑos, DaÑos devueltos
			aplicarEfecto_220(cibles, pelea);
			break;
		case 265:// DaÑos reducidos a X
			aplicarEfecto_265(cibles, pelea);
			break;
		case 266:// Robar Suerte
			aplicarEfecto_266(cibles, pelea);
			break;
		case 267:// Robar Vitalidad
			aplicarEfecto_267(cibles, pelea);
			break;
		case 268:// Robar agilidad
			aplicarEfecto_268(cibles, pelea);
			break;
		case 269:// Robar inteligencia
			aplicarEfecto_269(cibles, pelea);
			break;
		case 270:// Robar sabiduria
			aplicarEfecto_270(cibles, pelea);
			break;
		case 275:// DaÑos Agua %vida del atacante
			aplicarEfecto_275(cibles, pelea, glifo);
			break;
		case 276:// DaÑos Tierra %vida del atacante
			aplicarEfecto_276(cibles, pelea, glifo);
			break;
		case 277:// DaÑos Aire %vida del atacante
			aplicarEfecto_277(cibles, pelea, glifo);
			break;
		case 278:// DaÑos Fuego %vida del atacante
			aplicarEfecto_278(cibles, pelea, glifo);
			break;
		case 279:// DaÑos Neutral %vida del atacante
			aplicarEfecto_279(cibles, pelea, glifo);
			break;
		case 271:// Robar fuerza
			aplicarEfecto_271(cibles, pelea);
			break;
		case 293:// Aumenta los daÑos de base del hechizo X de Y
			aplicarEfecto_293(pelea);
			break;
		case 320:// Robar Alcance
			aplicarEfecto_320(cibles, pelea);
			break;
		case 400:// Crea una trampa
			aplicarEfecto_400(pelea);
			break;
		case 401:// Crea un glifo de nivel
			aplicarEfecto_401(pelea);
			break;
		case 402:// Glifo de blops Crea un glifo de nivel
			aplicarEfecto_402(pelea);
			break;
		case 606:// + sabiduria
			aplicarEfecto_606(cibles, pelea);
			break;
		case 607:// + fuerza
			aplicarEfecto_607(cibles, pelea);
			break;
		case 608:// + suerte
			aplicarEfecto_608(cibles, pelea);
			break;
		case 609:// + agilidad
			aplicarEfecto_609(cibles, pelea);
			break;
		case 610:// + vitalidad
			aplicarEfecto_610(cibles, pelea);
			break;
		case 611:// + inteligencia
			aplicarEfecto_611(cibles, pelea);
			break;
		case 666:// Paso de effecto complementario
			break;
		case 671: // DaÑos : X% de la vida del atacante (neutre)
			aplicarEfecto_671(cibles, pelea);
			break;
		case 672:// DaÑos : X% de la vida del atacante (neutre)
			aplicarEfecto_672(cibles, pelea);
			break;
		case 750:// bonus de captura
			aplicarEfecto_750(cibles, pelea);
			break;
		case 765:// Interacambia la posicion de 2 jugadores aliados (sacrificio)
			aplicarEfecto_765(cibles, pelea);
			break;
		case 776:// +% de los daÑos incurables sufridos
			aplicarEfecto_776(cibles, pelea);
			break;
		case 780:// lazo espiritual (invoca a un aliado muerto en combate)
			aplicarEfecto_780(pelea);
			break;
		case 782:// Maximiza los efectos aleatorios
			aplicarEfecto_782(cibles, pelea);
			break;
		case 781:// Minimiza los efectos aleatorios
			aplicarEfecto_781(cibles, pelea);
			break;
		case 783:// Hace retroceder hasta la casilla objetivo
			aplicarEfecto_783(pelea);
			break;
		case 784:// teleporta al punto de inicio
			aplicarEfecto_784(cibles, pelea);
			break;
		case 786:// Cura durante el ataque
			aplicarEfecto_786(cibles, pelea);
			break;
		case 787:// Cura durante el ataque
			aplicarEfecto_787(cibles, pelea);
			break;
		case 788:// Castigo X durante Y turnos
			aplicarEfecto_788(cibles, pelea);
			break;
		case 950:// Estado X
			aplicarEfecto_950(cibles, pelea);
			break;
		case 951:// Sale del Estado X
			aplicarEfecto_951(cibles, pelea);
			break;
		default:
			System.out.println("efecto no implantado : " + _efectoID + " formula: " + _args);
			break;
		}
	}

	private void aplicarEfecto_4(Pelea pelea) {// teletransporta
		if (_turnos > 1) {
			return;
		}
		if (_lanzador.tieneEstado(6)) {
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_lanzador.getPersonaje(), "1116");
			return;
		}
		if (_celdaLanz.esCaminable(true) && !pelea.celdaOcupada(_celdaLanz.getID())) {
			Mapa map = _lanzador.getPelea().getMapaCopia();
			map.removerLuchador(_lanzador);
			_lanzador.setCeldaPelea(_celdaLanz);
			map.addLuchador(_lanzador);
			ArrayList<Trampa> trampas = (new ArrayList<>());
			trampas.addAll(pelea.getTrampas());
			for (Trampa trampa : trampas) {
				if (trampa.activada) {
					continue;
				}
				int dist = Camino.distanciaEntreDosCeldas(pelea.getMapaCopia(), trampa.getCelda().getID(),
						_lanzador.getCeldaPelea().getID());
				if (dist <= trampa.getTamaÑo()) {
					trampa.activaTrampa(_lanzador);
				}
			}
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 4, _lanzador.getID() + "",
					_lanzador.getID() + "," + _celdaLanz.getID());
			Luchador objetivo = _lanzador.getTransportadoPor();
			if (objetivo != null) {
				map.removerLuchador(_lanzador);
				_lanzador.setCeldaPelea(_celdaLanz);
				map.addLuchador(_lanzador);
				_lanzador.setEstado(CentroInfo.ESTADO_TRANSPORTADO, 0);
				objetivo.setEstado(CentroInfo.ESTADO_PORTADOR, 0);
				_lanzador.setTransportadoPor(null);
				objetivo.setTransportado(null);
				if (_lanzador.esInvisible()) {
					_lanzador.unHide(-1);
				}
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 51, objetivo.getID() + "", _celdaLanz.getID() + "");
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 950, _lanzador.getID() + "",
						_lanzador.getID() + "," + CentroInfo.ESTADO_TRANSPORTADO + ",0");
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 950, objetivo.getID() + "",
						objetivo.getID() + "," + CentroInfo.ESTADO_PORTADOR + ",0");
			}
		}
		if (!Thread.interrupted()) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e1) {
			}
		}
	}

	private void aplicarEfecto_5(ArrayList<Luchador> objetivos, Pelea pelea) {// hace retroceder X
		// casillas
		if (objetivos.size() == 1 && _hechizoID == 120) {// destino de zurcarak
			if (objetivos.get(0).tieneEstado(6)) {
				return;
			}
			if (!objetivos.get(0).estaMuerto()) {
				_lanzador.setObjetivoDestZurca(objetivos.get(0));
			}
		}
		Mapa mapaCopia = pelea.getMapaCopia();
		if (_turnos <= 0) {
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto() || objetivo.tieneEstado(6) || objetivo.statico) {
					continue;
				}
				Celda celdaLanz = _celdaLanz;
				int daÑoFinal = 0;
				Luchador afectado = null;
				if (objetivo.getCeldaPelea().getID() == _celdaLanz.getID()) {
					celdaLanz = _lanzador.getCeldaPelea();
				}
				int nuevaCeldaID = Camino.getCeldaDespEmpujon(mapaCopia, celdaLanz, objetivo.getCeldaPelea(), _valor,
						pelea, objetivo);
				if (nuevaCeldaID == 0) {
					continue;
				}
				if (nuevaCeldaID < 0) {
					int a = -nuevaCeldaID;
					int coef = Formulas.getRandomValor("1d5+8");
					Luchador empujo = _lanzador2;
					while (empujo.esInvocacion()) {
						empujo = empujo.getInvocador();
					}
					float b = (empujo.getNivel() / (float) (100.00));
					if (b < 0.1) {
						b = 0.1f;
					}
					float c = b * a;
					daÑoFinal = (int) (coef * c);
					if (daÑoFinal < 1) {
						daÑoFinal = 1;
					}
					if (daÑoFinal > objetivo.getPDVConBuff()) {
						daÑoFinal = objetivo.getPDVConBuff();
					}
					if (daÑoFinal > 0) {
						objetivo.restarPDV(daÑoFinal);
						// GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, _lanzador.getID() + "",
						// objetivo.getID() + ",-" + daÑoFinal);
						if (objetivo.getPDVConBuff() <= 0) {
							GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, _lanzador.getID() + "",
									objetivo.getID() + ",-" + daÑoFinal);
							pelea.agregarAMuertos(objetivo, _lanzador, "ef5m");
							continue;
						}
					}
					a = _valor - a;
					nuevaCeldaID = Camino.getCeldaDespEmpujon(mapaCopia, celdaLanz, objetivo.getCeldaPelea(), a, pelea,
							objetivo);
					char dir = Camino.getDirEntreDosCeldas(celdaLanz.getID(), objetivo.getCeldaPelea().getID(),
							mapaCopia, true);
					int celdaSigID = 0;
					if (nuevaCeldaID == 0) {
						celdaSigID = Camino.getSigIDCeldaMismaDir(objetivo.getCeldaPelea().getID(), dir, mapaCopia,
								true);
					} else {
						celdaSigID = Camino.getSigIDCeldaMismaDir(nuevaCeldaID, dir, mapaCopia, true);
					}
					Celda celdaSig = mapaCopia.getCelda(celdaSigID);
					if (celdaSig != null) {
						afectado = mapaCopia.getPrimerLuchador(celdaSigID);
					}
				}
				if (daÑoFinal > 0) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, _lanzador.getID() + "",
							objetivo.getID() + ",-" + daÑoFinal);
				}
				if (afectado != null) {
					int daÑoFinal2 = (daÑoFinal / 2);
					if (daÑoFinal2 < 1) {
						daÑoFinal2 = 1;
					}
					if (daÑoFinal2 > afectado.getPDVConBuff()) {
						daÑoFinal2 = afectado.getPDVConBuff();
					}
					if (daÑoFinal2 > 0) {
						afectado.restarPDV(daÑoFinal2);
						GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, _lanzador.getID() + "",
								afectado.getID() + ",-" + daÑoFinal2);
						if (afectado.getPDVConBuff() <= 0) {
							pelea.agregarAMuertos(afectado, _lanzador, "ef5");
						}
					}
				}
				if (!Thread.interrupted()) {
					if (this._hechizoID == 1688) {
						try {
							Thread.sleep(300);
						} catch (InterruptedException e1) {
						}
					}
				}
				int dist = 3;
				if (nuevaCeldaID != 0) {
					dist = Camino.distanciaEntreDosCeldas(pelea.getMapaCopia(), objetivo.getCeldaPelea().getID(),
							nuevaCeldaID);
					Celda nueva = mapaCopia.getCelda(nuevaCeldaID);
					if (nueva != null) {
						mapaCopia.removerLuchador(objetivo);
						objetivo.setCeldaPelea(nueva);
						mapaCopia.addLuchador(objetivo);
						if (objetivo.getTransportando() != null) {
							Luchador transp = objetivo.getTransportando();
							mapaCopia.removerLuchador(transp);
							transp.setCeldaPelea(nueva);
							mapaCopia.addLuchador(transp);
						}
						GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 5, _lanzador.getID() + "",
								objetivo.getID() + "," + nuevaCeldaID);
					}
				} else {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 5, _lanzador.getID() + "",
							objetivo.getID() + "," + objetivo.getCeldaPelea().getID());
				}
				if (!Thread.interrupted()) {
					if (_hechizoID == 1688) {
						try {
							Thread.sleep(dist * 200);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					} else {
						try {
							Thread.sleep(dist * 250);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}
				}
				ArrayList<Trampa> trampas = new ArrayList<>();
				trampas.addAll(pelea.getTrampas());
				if (!trampas.isEmpty()) {
					int lastcel = nuevaCeldaID;
					Timer timer = new Timer(400, new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							((Timer) e.getSource()).stop();
							for (Trampa trampa : trampas) {
								if (trampa.activada) {
									continue;
								}
								int dist = Camino.distanciaEntreDosCeldas(pelea.getMapaCopia(),
										trampa.getCelda().getID(), lastcel);
								if (dist <= trampa.getTamaÑo()) {
									trampa.activaTrampa(objetivo);
								}
							}
						}
					});
					timer.start();
				}
			}
		}
		if (!Thread.interrupted()) {
			try {
				Thread.sleep(400);
			} catch (InterruptedException e1) {
			}
		}
	}

	private void aplicarEfecto_6(ArrayList<Luchador> objetivos, Pelea pelea) { // hace avanzar X casillas
		if (_turnos <= 0) {
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto() || objetivo.tieneEstado(6)) {
					continue;
				}
				Celda eCelda = _celdaLanz;
				if (objetivo.getCeldaPelea().getID() == _celdaLanz.getID()) {
					eCelda = _lanzador.getCeldaPelea();
				}
				Mapa map = pelea.getMapaCopia();
				int nuevaCeldaID = Camino.getNuevaCeldaDespuesGolpe(map, eCelda, objetivo.getCeldaPelea(), -_valor,
						pelea, objetivo);
				if (nuevaCeldaID == 0) {
					continue;
				}
				if (nuevaCeldaID < 0) {
					int a = -(_valor + nuevaCeldaID);
					nuevaCeldaID = Camino.getNuevaCeldaDespuesGolpe(map, _lanzador.getCeldaPelea(),
							objetivo.getCeldaPelea(), a, pelea, objetivo);
					if (nuevaCeldaID == 0 || pelea.getMapaCopia().getCelda(nuevaCeldaID) == null) {
						continue;
					}
				}
				map.removerLuchador(objetivo);
				objetivo.setCeldaPelea(map.getCelda(nuevaCeldaID));
				map.addLuchador(objetivo);
				if (objetivo.getTransportando() != null) {
					Luchador transp = objetivo.getTransportando();
					map.removerLuchador(transp);
					transp.setCeldaPelea(map.getCelda(nuevaCeldaID));
					map.addLuchador(transp);
				}
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 5, _lanzador.getID() + "",
						objetivo.getID() + "," + nuevaCeldaID);
				ArrayList<Trampa> trampas = new ArrayList<>();
				trampas.addAll(pelea.getTrampas());
				int lastcel = nuevaCeldaID;
				Timer timer = new Timer(400, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						((Timer) e.getSource()).stop();
						for (Trampa trampa : trampas) {
							if (trampa.activada) {
								continue;
							}
							int dist = Camino.distanciaEntreDosCeldas(pelea.getMapaCopia(), trampa.getCelda().getID(),
									lastcel);
							if (dist <= trampa.getTamaÑo()) {
								trampa.activaTrampa(objetivo);
							}
						}
					}
				});
				timer.start();
			}
		}
		if (!Thread.interrupted()) {
			try {
				Thread.sleep(300);
			} catch (InterruptedException e1) {
			}
		}
	}

	private void aplicarEfecto_8(ArrayList<Luchador> objetivos, Pelea pelea) {// intercambia la posicion de 2 jugadores
		if (objetivos.isEmpty()) {
			return;
		}
		Luchador objetivo = objetivos.get(0);
		if (objetivo == null || objetivo.estaMuerto() || objetivo.tieneEstado(6)) {
			return;
		}
		switch (_hechizoID) {
		case 438:// Transposicion
			if (objetivo.getEquipoBin() != _lanzador.getEquipoBin()) {
				return;
			}
			break;
		case 445:// Cooperacion
			if (objetivo.getEquipoBin() == _lanzador.getEquipoBin()) {
				return;
			}
			break;
		}
		Celda exCeldaObjetivo = objetivo.getCeldaPelea();
		Celda exCeldaLanzador = _lanzador.getCeldaPelea();
		Mapa map = pelea.getMapaCopia();
		map.removerLuchador(objetivo);
		map.removerLuchador(_lanzador);
		objetivo.setCeldaPelea(exCeldaLanzador);
		_lanzador.setCeldaPelea(exCeldaObjetivo);
		map.addLuchador(objetivo);
		map.addLuchador(_lanzador);
		ArrayList<Trampa> trampas = (new ArrayList<>());
		trampas.addAll(pelea.getTrampas());
		for (Trampa trampa : trampas) {
			if (trampa.activada) {
				continue;
			}
			int dist = Camino.distanciaEntreDosCeldas(pelea.getMapaCopia(), trampa.getCelda().getID(),
					objetivo.getCeldaPelea().getID());
			int dist2 = Camino.distanciaEntreDosCeldas(pelea.getMapaCopia(), trampa.getCelda().getID(),
					_lanzador.getCeldaPelea().getID());
			if (dist <= trampa.getTamaÑo()) {
				trampa.activaTrampa(objetivo);
			} else if (dist2 <= trampa.getTamaÑo()) {
				trampa.activaTrampa(_lanzador);
			}
		}
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 4, _lanzador.getID() + "",
				objetivo.getID() + "," + exCeldaLanzador.getID());
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 4, _lanzador.getID() + "",
				_lanzador.getID() + "," + exCeldaObjetivo.getID());
	}

	private void aplicarEfecto_9(ArrayList<Luchador> objetivos, Pelea pelea) {// esquiva golpes retrocediendo casillas
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto()) {
				continue;
			}
			objetivo.addBuff(_efectoID, _valor, _turnos, 1, true, _hechizoID, _args, _lanzador, poison, true);
		}
	}

	private void aplicarEfecto_50(Pelea pelea) {// permite levantar a un jugador
		Mapa map = pelea.getMapaCopia();
		if (map == null) {
			return;
		}
		Luchador objetivo = map.getPrimerLuchador(_celdaLanz.getID());
		if (objetivo == null) {
			return;
		}
		if (objetivo.getBuff(765) != null) {
			if (_lanzador.getPersonaje() != null) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_lanzador.getPersonaje(),
						"No puedes levantar a alguien que tiene Sacrificio", Colores.VERDE);
			}
			return;
		}
		for (EfectoHechizo obj : _lanzador.getBuffPelea().keySet()) {
			if (obj.getEfectoID() == 149 && obj.getHechizoID() == 686) {
				if (_lanzador.getPersonaje() != null) {
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_lanzador.getPersonaje(),
							"No puedes levantar a alguien mientras estÁs en estado Tranka", Colores.VERDE);
				}
				return;
			}
		}
		if (objetivo.tieneEstado(6) || objetivo.estaMuerto()) {
			return;
		}
		map.removerLuchador(objetivo);
		objetivo.setCeldaPelea(_lanzador.getCeldaPelea());
		objetivo.setEstado(CentroInfo.ESTADO_TRANSPORTADO, -1);
		_lanzador.setEstado(CentroInfo.ESTADO_PORTADOR, -1);
		objetivo.setTransportadoPor(_lanzador);
		_lanzador.setTransportado(objetivo);
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 950, objetivo.getID() + "",
				objetivo.getID() + "," + CentroInfo.ESTADO_TRANSPORTADO + ",1");
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 950, _lanzador.getID() + "",
				_lanzador.getID() + "," + CentroInfo.ESTADO_PORTADOR + ",1");
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 50, _lanzador.getID() + "", "" + objetivo.getID());
	}

	private void aplicarEfecto_51(Pelea pelea) {// lanza a un jugador
		Mapa map = _celdaLanz._mapa;
		if ((map == null) || !_celdaLanz.esCaminable(true) || map.getLuchadores(_celdaLanz.getID()).size() > 0) {
			return;
		}
		Luchador objetivo = _lanzador.getTransportando();
		if (objetivo == null) {
			return;
		}
		if (objetivo.estaMuerto()) {
			return;
		}
		map.removerLuchador(objetivo);
		objetivo.setCeldaPelea(_celdaLanz);
		map.addLuchador(objetivo);
		objetivo.setEstado(CentroInfo.ESTADO_TRANSPORTADO, 0);
		_lanzador.setEstado(CentroInfo.ESTADO_PORTADOR, 0);
		objetivo.setTransportadoPor(null);
		_lanzador.setTransportado(null);
		if (objetivo.esInvisible()) {
			objetivo.unHide(-1);
		}
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 51, _lanzador.getID() + "", _celdaLanz.getID() + "");
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 950, objetivo.getID() + "",
				objetivo.getID() + "," + CentroInfo.ESTADO_TRANSPORTADO + ",0");
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 950, _lanzador.getID() + "",
				_lanzador.getID() + "," + CentroInfo.ESTADO_PORTADOR + ",0");
	}

	private void aplicarEfecto_77(ArrayList<Luchador> cibles, Pelea fight) {// robo de PM
		int value = 1;
		try {
			value = Integer.parseInt(_args.split(";")[0]);
		} catch (Exception e) {
		}
		int num = 0;
		for (Luchador target : cibles) {
			int val = Formulas.getPuntosPerdidos('m', value, _lanzador, target);
			val = getMaxMinHechizo(target, val);
			if (val < value) {
				GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 309, _lanzador.getID() + "",
						target.getID() + "," + (value - val));
			}
			if (val < 1) {
				continue;
			}
			target.addBuff(CentroInfo.STATS_REM_PM, val, _turnos, 0, true, _hechizoID, _args, _lanzador, poison, true);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, CentroInfo.STATS_REM_PM, _lanzador.getID() + "",
					target.getID() + ",-" + val + "," + _turnos);
			num += val;
			if (_lanzador.puedeJugar()) {
				_lanzador.addTempPM(fight, num);
			}
		}
		if (num != 0) {
			GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, CentroInfo.STATS_ADD_PM, _lanzador.getID() + "",
					_lanzador.getID() + "," + num + "," + _turnos);
			_lanzador.addBuff(CentroInfo.STATS_ADD_PM, num, 0, 0, true, _hechizoID, _args, _lanzador, poison, true);
		}
	}

	private void aplicarEfecto_78(ArrayList<Luchador> cibles, Pelea fight) {// + PM
		int minV = Integer.parseInt(_args.split(";")[0]);
		int maxV = Integer.parseInt(_args.split(";")[1]);
		if (maxV <= 0) {
			maxV = minV;
		}
		int val = Formulas.getRandomValor(minV, maxV);
		if (val == -1) {
			return;
		}
		for (Luchador target : cibles) {
			val = getMaxMinHechizo(target, val);
			target.addBuff(_efectoID, val, _turnos, 1, false, _hechizoID, _args, _lanzador, poison, true);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, _efectoID, _lanzador.getID() + "",
					target.getID() + "," + val + "," + _turnos);
		}
	}

	private void aplicarEfecto_79(ArrayList<Luchador> objetivos, Pelea pelea) { // posib recibir daÑos o curas
		if (_turnos < 1) {
			return;
		} else {
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto()) {
					continue;
				}
				objetivo.addBuff(_efectoID, -1, _turnos, 0, true, _hechizoID, _args, _lanzador, poison, true);
			}
		}
		if (!Thread.interrupted()) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e1) {
			}
		}
	}

	private void aplicarEfecto_82(ArrayList<Luchador> cibles, Pelea fight, boolean glifo) {// robo de PDV fijo
		if (_turnos <= 0) {
			for (Luchador target : cibles) {
				if (target.tieneBuff(765)) {
					if (target.getBuff(765) != null && !target.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(fight, target);
						target = target.getBuff(765).getLanzador();
					}
				}
				int daÑo = Formulas.getRandomValor(_args.split(";")[5]);
				daÑo = getMaxMinHechizo(target, daÑo);
				if (target.tieneBuff(106) && target.getBuffValor(106) >= _nivelHechizo && _hechizoID != 0 && !isBegin) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 106, target.getID() + "", target.getID() + ",1");
					target = _lanzador2;
				}
				if (_hechizoID == 450 && daÑo > 1000) {
					daÑo = 1000;
				}
				int finalDaÑo = daÑo;
				if (_hechizoID != 450) {
					finalDaÑo = Formulas.calculFinalDommage(fight, _lanzador, target, CentroInfo.ELEMENT_NULL, daÑo,
							false, false, _hechizoID, isBegin, glifo);
				}
				if (!isBegin && _hechizoID != 450) {
					finalDaÑo = aplicarBuffContraGolpe(finalDaÑo, target, _lanzador, fight, poison);
				}
				if (finalDaÑo > target.getPDVConBuff()) {
					finalDaÑo = target.getPDVConBuff();
				}
				target.restarPDV(finalDaÑo);
				finalDaÑo = -(finalDaÑo);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, _lanzador.getID() + "",
						target.getID() + "," + finalDaÑo);
				int cura = 0;
				if (target.tieneBuff(79)) {
					if (finalDaÑo < 0) {
						cura = (-finalDaÑo) / 2;
					} else {
						cura = 0;
					}
				} else {
					cura = (-finalDaÑo) / 2;
				}
				if ((_lanzador.getPDVConBuff() + cura) > _lanzador.getPDVMaxConBuff()) {
					cura = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
				}
				_lanzador.restarPDV(-cura);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, target.getID() + "", _lanzador.getID() + "," + cura);
				if (target.getPDVConBuff() <= 0) {
					fight.agregarAMuertos(target, _lanzador, "");
				}
			}
		} else {
			for (Luchador target : cibles) {
				target.addBuff(_efectoID, _valor, _turnos, 0, true, _hechizoID, _args, _lanzador, poison, true);
			}
		}
	}

	private void aplicarEfecto_84(ArrayList<Luchador> cibles, Pelea fight) { // robo de PA
		int value = 1;
		try {
			value = Integer.parseInt(_args.split(";")[0]);
		} catch (NumberFormatException e) {
		}
		int num = 0;
		for (Luchador target : cibles) {
			int val = Formulas.getPuntosPerdidos('m', value, _lanzador, target);
			val = getMaxMinHechizo(target, val);
			if (val < value) {
				GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 308, _lanzador.getID() + "",
						target.getID() + "," + (value - val));
			}
			if (val < 1) {
				continue;
			}
			if (_hechizoID == 95) {
				target.addBuff(CentroInfo.STATS_REM_PA, val, 1, 1, true, _hechizoID, _args, _lanzador, poison, true);
			} else {
				target.addBuff(CentroInfo.STATS_REM_PA, val, _turnos, 0, true, _hechizoID, _args, _lanzador, poison,
						true);
			}
			GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, CentroInfo.STATS_REM_PA, _lanzador.getID() + "",
					target.getID() + ",-" + val + "," + _turnos);
			num += val;
		}
		if (num != 0) {
			GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, CentroInfo.STATS_ADD_PA, _lanzador.getID() + "",
					_lanzador.getID() + "," + num + "," + _turnos);
			_lanzador.addBuff(CentroInfo.STATS_ADD_PA, num, 0, 0, true, _hechizoID, _args, _lanzador, poison, true);
			if (_lanzador.puedeJugar()) {
				_lanzador.addTempPA(fight, num);
			}
		}
	}

	private void aplicarEfecto_85(ArrayList<Luchador> cibles, Pelea fight) { // daÑos % vida atacante agua
		if (_turnos <= 0) {
			for (Luchador target : cibles) {
				if (_lanzador.getMob() != null && !_lanzador.esInvocacion() && target.getMob() != null
						&& !target.esInvocacion()) {
					continue;
				}
				if (target.tieneBuff(106) && target.getBuffValor(106) >= _nivelHechizo && _hechizoID != 0 && !isBegin) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 106, target.getID() + "", target.getID() + ",1");
					target = _lanzador2;
				}
				if (target.tieneBuff(765)) {
					if (target.getBuff(765) != null && !target.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(fight, target);
						target = target.getBuff(765).getLanzador();
					}
				}
				int resP = target.getTotalStatsConBuff().getEfecto(CentroInfo.STATS_ADD_ResPorc_AGUA);
				int resF = target.getTotalStatsConBuff().getEfecto(CentroInfo.STATS_ADD_R_EAU);
				if (target.getPersonaje() != null) {
					resP += target.getTotalStatsConBuff().getEfecto(CentroInfo.STATS_ADD_RP_PVP_AGUA);
					resF += target.getTotalStatsConBuff().getEfecto(CentroInfo.STATS_ADD_R_PVP_AGUA);
				}
				int dmg = Formulas.getRandomValor(_args.split(";")[5]);// %age de pdv inflig�
				dmg = getMaxMinHechizo(target, dmg);
				int val = _lanzador.getPDVConBuff() / 100 * dmg;// Valeur des d�gats
				// retrait de la r�sist fixe
				val -= resF;
				int reduc = (int) (((float) val) / (float) 100) * resP;// Reduc %resis
				val -= reduc;
				if (val < 0) {
					val = 0;
				}
				if (!isBegin)
				 {
					val = aplicarBuffContraGolpe(val, target, _lanzador, fight, poison);// S'il y a des buffs sp�ciaux
				}
				if (target.getMob() != null) {
					if (target.getMob().getModelo().getID() == 2750) {
						int heal = (-val) / 2;
						if ((_lanzador.getPDVConBuff() + heal) > _lanzador.getPDVMaxConBuff()) {
							heal = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
						}
						_lanzador.restarPDV(-heal);
						GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, target.getID() + "",
								_lanzador.getID() + "," + heal);
					}
				}
				if (val > target.getPDVConBuff())
				 {
					val = target.getPDVConBuff();// Target va mourrir
				}
				target.restarPDV(val);
				val = -(val);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, _lanzador.getID() + "", target.getID() + "," + val);
				if (target.getPDVConBuff() <= 0) {
					fight.agregarAMuertos(target, _lanzador, "");
				}
			}
		} else {
			for (Luchador target : cibles) {
				target.addBuff(_efectoID, _valor, _turnos, 0, true, _hechizoID, _args, _lanzador, poison, true);// on
																												// applique
																												// un
																												// buff
			}
		}
	}

	private void aplicarEfecto_86(ArrayList<Luchador> cibles, Pelea fight) {// daÑos % de la vida
		// del atacante
		// (tierra)
		if (_turnos <= 0) {
			for (Luchador target : cibles) {
				if (_lanzador.getMob() != null && !_lanzador.esInvocacion() && target.getMob() != null
						&& !target.esInvocacion()) {
					continue;
				}
				// si la cible a le buff renvoie de sort
				if (target.tieneBuff(106) && target.getBuffValor(106) >= _nivelHechizo && _hechizoID != 0 && !isBegin) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 106, target.getID() + "", target.getID() + ",1");
					// le lanceur devient donc la cible
					target = _lanzador2;
				}
				if (target.tieneBuff(765))// sacrifice
				{
					if (target.getBuff(765) != null && !target.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(fight, target);
						target = target.getBuff(765).getLanzador();
					}
				}

				int resP = target.getTotalStatsConBuff().getEfecto(CentroInfo.STATS_ADD_ResPorc_TIERRA);
				int resF = target.getTotalStatsConBuff().getEfecto(CentroInfo.STATS_ADD_R_FUEGO);
				if (target.getPersonaje() != null)// Si c'est un joueur, on ajoute les resists bouclier
				{
					resP += target.getTotalStatsConBuff().getEfecto(CentroInfo.STATS_ADD_RP_PVP_TIERRA);
					resF += target.getTotalStatsConBuff().getEfecto(CentroInfo.STATS_ADD_R_PVP_TIERRA);
				}
				int dmg = Formulas.getRandomValor(_args.split(";")[5]);// %age de pdv inflig�
				dmg = getMaxMinHechizo(target, dmg);
				int val = _lanzador.getPDVConBuff() / 100 * dmg;// Valeur des d�gats
				// retrait de la r�sist fixe
				val -= resF;
				int reduc = (int) (((float) val) / (float) 100) * resP;// Reduc %resis
				val -= reduc;
				if (val < 0) {
					val = 0;
				}

				if (!isBegin)
				 {
					val = aplicarBuffContraGolpe(val, target, _lanzador, fight, poison);// S'il y a des buffs sp�ciaux
				}
				if (target.getMob() != null) {
					if (target.getMob().getModelo().getID() == 2750) {
						int heal = (-val) / 2;
						if ((_lanzador.getPDVConBuff() + heal) > _lanzador.getPDVMaxConBuff()) {
							heal = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
						}
						_lanzador.restarPDV(-heal);
						GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, target.getID() + "",
								_lanzador.getID() + "," + heal);
					}
				}
				if (val > target.getPDVConBuff())
				 {
					val = target.getPDVConBuff();// Target va mourrir
				}
				target.restarPDV(val);
				val = -(val);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, _lanzador.getID() + "", target.getID() + "," + val);
				if (target.getPDVConBuff() <= 0) {
					fight.agregarAMuertos(target, _lanzador, "");
				}
			}
		} else {
			for (Luchador target : cibles) {
				target.addBuff(_efectoID, _valor, _turnos, 0, true, _hechizoID, _args, _lanzador, poison, true);// on
																												// applique
																												// un
																												// buff
			}
		}
	}

	private void aplicarEfecto_87(ArrayList<Luchador> cibles, Pelea fight) {// daÑos % de la vida
		// del atacante aire
		if (_turnos <= 0) {
			for (Luchador target : cibles) {
				if (_lanzador.getMob() != null && !_lanzador.esInvocacion() && target.getMob() != null
						&& !target.esInvocacion()) {
					continue;
				}
				// si la cible a le buff renvoie de sort
				if (target.tieneBuff(106) && target.getBuffValor(106) >= _nivelHechizo && _hechizoID != 0 && !isBegin) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 106, target.getID() + "", target.getID() + ",1");
					// le lanceur devient donc la cible
					target = _lanzador2;
				}
				if (target.tieneBuff(765) && !isBegin)// sacrifice
				{
					if (target.getBuff(765) != null && !target.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(fight, target);
						target = target.getBuff(765).getLanzador();
					}
				}

				int resP = target.getTotalStatsConBuff().getEfecto(CentroInfo.STATS_ADD_ResPorc_AIRE);
				int resF = target.getTotalStatsConBuff().getEfecto(CentroInfo.STATS_ADD_R_AIR);
				if (target.getPersonaje() != null)// Si c'est un joueur, on ajoute les resists bouclier
				{
					resP += target.getTotalStatsConBuff().getEfecto(CentroInfo.STATS_ADD_RP_PVP_AIRE);
					resF += target.getTotalStatsConBuff().getEfecto(CentroInfo.STATS_ADD_R_PVP_AIRE);
				}
				int dmg = Formulas.getRandomValor(_args.split(";")[5]);// %age de pdv inflig�
				dmg = getMaxMinHechizo(target, dmg);
				int val = _lanzador.getPDVConBuff() / 100 * dmg;// Valeur des d�gats
				// retrait de la r�sist fixe
				val -= resF;
				int reduc = (int) (((float) val) / (float) 100) * resP;// Reduc %resis
				val -= reduc;
				if (val < 0) {
					val = 0;
				}

				if (!isBegin)
				 {
					val = aplicarBuffContraGolpe(val, target, _lanzador, fight, poison);// S'il y a des buffs sp�ciaux
				}
				if (target.getMob() != null) {
					if (target.getMob().getModelo().getID() == 2750) {
						int heal = (-val) / 2;
						if ((_lanzador.getPDVConBuff() + heal) > _lanzador.getPDVMaxConBuff()) {
							heal = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
						}
						_lanzador.restarPDV(-heal);
						GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, target.getID() + "",
								_lanzador.getID() + "," + heal);
					}
				}
				if (val > target.getPDVConBuff())
				 {
					val = target.getPDVConBuff();// Target va mourrir
				}
				target.restarPDV(val);
				val = -(val);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, _lanzador.getID() + "", target.getID() + "," + val);
				if (target.getPDVConBuff() <= 0) {
					fight.agregarAMuertos(target, _lanzador, "");
				}
			}
		} else {
			for (Luchador target : cibles) {
				target.addBuff(_efectoID, _valor, _turnos, 0, true, _hechizoID, _args, _lanzador, poison, true);// on
																												// applique
																												// un
																												// buff
			}
		}
	}

	private void aplicarEfecto_88(ArrayList<Luchador> cibles, Pelea fight) {// daÑos % de la vida
		// del atacante fuego
		if (_turnos <= 0) {
			for (Luchador target : cibles) {
				if (_lanzador.getMob() != null && !_lanzador.esInvocacion() && target.getMob() != null
						&& !target.esInvocacion()) {
					continue;
				}
				// si la cible a le buff renvoie de sort
				if (target.tieneBuff(106) && target.getBuffValor(106) >= _nivelHechizo && _hechizoID != 0 && !isBegin) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 106, target.getID() + "", target.getID() + ",1");
					// le lanceur devient donc la cible
					target = _lanzador2;
				}
				if (target.tieneBuff(765))// sacrifice
				{
					if (target.getBuff(765) != null && !target.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(fight, target);
						target = target.getBuff(765).getLanzador();
					}
				}

				int resP = target.getTotalStatsConBuff().getEfecto(CentroInfo.STATS_ADD_ResPorc_FUEGO);
				int resF = target.getTotalStatsConBuff().getEfecto(CentroInfo.STATS_ADD_R_FEU);
				if (target.getPersonaje() != null)// Si c'est un joueur, on ajoute les resists bouclier
				{
					resP += target.getTotalStatsConBuff().getEfecto(CentroInfo.STATS_ADD_RP_PVP_FUEGO);
					resF += target.getTotalStatsConBuff().getEfecto(CentroInfo.STATS_ADD_R_PVP_FUEGO);
				}
				int dmg = Formulas.getRandomValor(_args.split(";")[5]);// %age de pdv inflig�
				dmg = getMaxMinHechizo(target, dmg);
				int val = _lanzador.getPDVConBuff() / 100 * dmg;// Valeur des d�gats
				// retrait de la r�sist fixe
				val -= resF;
				int reduc = (int) (((float) val) / (float) 100) * resP;// Reduc %resis
				val -= reduc;
				if (val < 0) {
					val = 0;
				}

				if (!isBegin)
				 {
					val = aplicarBuffContraGolpe(val, target, _lanzador, fight, poison);// S'il y a des buffs sp�ciaux
				}
				if (target.getMob() != null) {
					if (target.getMob().getModelo().getID() == 2750) {
						int heal = (-val) / 2;
						if ((_lanzador.getPDVConBuff() + heal) > _lanzador.getPDVMaxConBuff()) {
							heal = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
						}
						_lanzador.restarPDV(-heal);
						GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, target.getID() + "",
								_lanzador.getID() + "," + heal);
					}
				}
				if (val > target.getPDVConBuff())
				 {
					val = target.getPDVConBuff();// Target va mourrir
				}
				target.restarPDV(val);
				val = -(val);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, _lanzador.getID() + "", target.getID() + "," + val);
				if (target.getPDVConBuff() <= 0) {
					fight.agregarAMuertos(target, _lanzador, "");
				}
			}
		} else {
			for (Luchador target : cibles) {
				target.addBuff(_efectoID, _valor, _turnos, 0, true, _hechizoID, _args, _lanzador, poison, true);// on
																												// applique
																												// un
																												// buff
			}
		}
	}

	private void aplicarEfecto_89(ArrayList<Luchador> cibles, Pelea fight) {// daÑos % de la vida
		if (_turnos <= 0) {
			for (Luchador target : cibles) {
				if (_lanzador.getMob() != null && !_lanzador.esInvocacion() && target.getMob() != null
						&& !target.esInvocacion()) {
					continue;
				}
				// si la cible a le buff renvoie de sort
				if (target.tieneBuff(106) && target.getBuffValor(106) >= _nivelHechizo && _hechizoID != 0 && !isBegin) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 106, target.getID() + "", target.getID() + ",1");
					// le lanceur devient donc la cible
					target = _lanzador2;
				}
				if (target.tieneBuff(765))// sacrifice
				{
					if (target.getBuff(765) != null && !target.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(fight, target);
						target = target.getBuff(765).getLanzador();
					}
				}
				int resP = target.getTotalStatsConBuff().getEfecto(CentroInfo.STATS_ADD_ResPorc_NEUTRAL);
				int resF = target.getTotalStatsConBuff().getEfecto(CentroInfo.STATS_ADD_R_NEU);
				if (target.getPersonaje() != null)// Si c'est un joueur, on ajoute les resists bouclier
				{
					resP += target.getTotalStatsConBuff().getEfecto(CentroInfo.STATS_ADD_RP_PVP_NEUTRAL);
					resF += target.getTotalStatsConBuff().getEfecto(CentroInfo.STATS_ADD_R_PVP_NEUTRAL);
				}
				int dmg = Formulas.getRandomValor(_args.split(";")[5]);// %age de pdv inflig�
				dmg = getMaxMinHechizo(target, dmg);
				int val = _lanzador.getPDVConBuff() / 100 * dmg;// Valeur des d�gats
				// retrait de la r�sist fixe
				val -= resF;
				int reduc = (int) (((float) val) / (float) 100) * resP;// Reduc %resis
				val -= reduc;
				if (val < 0) {
					val = 0;
				}

				if (!isBegin)
				 {
					val = aplicarBuffContraGolpe(val, target, _lanzador, fight, poison);// S'il y a des buffs sp�ciaux
				}
				if (target.getMob() != null) {
					if (target.getMob().getModelo().getID() == 2750) {
						int heal = (-val) / 2;
						if ((_lanzador.getPDVConBuff() + heal) > _lanzador.getPDVMaxConBuff()) {
							heal = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
						}
						_lanzador.restarPDV(-heal);
						GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, target.getID() + "",
								_lanzador.getID() + "," + heal);
					}
				}
				if (val > target.getPDVConBuff())
				 {
					val = target.getPDVConBuff();// Target va mourrir
				}
				target.restarPDV(val);
				val = -(val);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, _lanzador.getID() + "", target.getID() + "," + val);
				if (target.getPDVConBuff() <= 0) {
					fight.agregarAMuertos(target, _lanzador, "");
				}
			}
		} else {
			for (Luchador target : cibles) {
				target.addBuff(_efectoID, _valor, _turnos, 0, true, _hechizoID, _args, _lanzador, poison, true);// on
																												// applique
																												// un
																												// buff
			}
		}
	}

	private void aplicarEfecto_81(ArrayList<Luchador> objetivos, Pelea pelea) {// curacion
		if (_turnos <= 0) {
			String[] jet = _args.split(";");
			int cura = 0;
			if (jet.length < 6) {
				cura = 1;
			} else {
				cura = Formulas.getRandomValor(jet[5]);
			}
			int cura2 = cura;
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto()) {
					continue;
				}
				cura = getMaxMinHechizo(objetivo, cura);
				int pdvMax = objetivo.getPDVMaxConBuff();
				int curaFinal = Formulas.calculFinalCura(_lanzador, cura, false);
				if ((curaFinal + objetivo.getPDVConBuff()) > pdvMax) {
					curaFinal = pdvMax - objetivo.getPDVConBuff();
				}
				if (curaFinal < 1) {
					curaFinal = 0;
				}
				objetivo.restarPDV(-curaFinal);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 108, _lanzador.getID() + "",
						objetivo.getID() + "," + curaFinal);
				cura = cura2;
			}
		} else {
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto()) {
					continue;
				}
				objetivo.addBuff(_efectoID, _valor, _turnos, 0, true, _hechizoID, _args, _lanzador, poison, true);
			}
		}
		if (!Thread.interrupted()) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e1) {
			}
		}
	}

	private void aplicarEfecto_90(ArrayList<Luchador> cibles, Pelea fight) {// entrega X de su vida
		if (_turnos <= 0) {
			int pAge = Formulas.getRandomValor(_args.split(";")[5]);
			int val = pAge * (_lanzador.getPDVConBuff() / 100);
			// Calcul des Doms recus par le lanceur
			int finalDommage = aplicarBuffContraGolpe(val, _lanzador, _lanzador, fight, poison);
			if (finalDommage > _lanzador.getPDVConBuff())
			 {
				finalDommage = _lanzador.getPDVConBuff();// Caster va mourrir
			}
			_lanzador.restarPDV(finalDommage);
			finalDommage = -(finalDommage);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, _lanzador.getID() + "",
					_lanzador.getID() + "," + finalDommage);

			for (Luchador target : cibles) {
				if ((val + target.getPDVConBuff()) > target.getPDVMaxConBuff())
				 {
					val = target.getPDVMaxConBuff() - target.getPDVConBuff();// Target va mourrir
				}
				target.restarPDV(-val);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, _lanzador.getID() + "", target.getID() + ",+" + val);
			}
			if (_lanzador.getPDVConBuff() <= 0) {
				fight.agregarAMuertos(_lanzador, _lanzador, "");
			}
		} else {
			for (Luchador target : cibles) {
				target.addBuff(_efectoID, _valor, _turnos, 0, true, _hechizoID, _args, _lanzador, poison, true);// on
																												// applique
																												// un
																												// buff
			}
		}
	}

	private void aplicarEfecto_91(ArrayList<Luchador> cibles, Pelea fight, boolean isCaC, boolean glifo) {// roba PDV
																											// agua
		if (isCaC) {
			for (Luchador target : cibles) {
				if (target.tieneBuff(765))// sacrifice
				{
					if (target.getBuff(765) != null && !target.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(fight, target);
						target = target.getBuff(765).getLanzador();
					}
				}

				int dmg = Formulas.getRandomValor(_args.split(";")[5]);
				dmg = getMaxMinHechizo(target, dmg);
				int finalDommage = Formulas.calculFinalDommage(fight, _lanzador, target, CentroInfo.ELEMENT_EAU, dmg,
						false, true, _hechizoID, isBegin, glifo);

				if (!isBegin)
				 {
					finalDommage = aplicarBuffContraGolpe(finalDommage, target, _lanzador, fight, poison);// S'il y a
																											// des buffs
																											// sp�ciaux
				}

				if (finalDommage > target.getPDVConBuff())
				 {
					finalDommage = target.getPDVConBuff();// Target va mourrir
				}
				target.restarPDV(finalDommage);
				finaldaÑo += finalDommage;
				finalDommage = -(finalDommage);
				if (!isCaC) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, _lanzador.getID() + "",
							target.getID() + "," + finalDommage);
				} else {
					if (cibles.size() == 1) {
						targetpeles = target;
					} else {
						GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, _lanzador.getID() + "",
								target.getID() + "," + finalDommage);
					}
				}
				int heal = 0;
				if (target.tieneBuff(79)) {
					if (finalDommage < 0) {
						heal = (-finalDommage) / 2;
					} else {
						heal = 0;
					}
				} else {
					heal = (-finalDommage) / 2;
				}
				if ((_lanzador.getPDVConBuff() + heal) > _lanzador.getPDVMaxConBuff()) {
					heal = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
				}
				_lanzador.restarPDV(-heal);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, target.getID() + "", _lanzador.getID() + "," + heal);
				if (target.getPDVConBuff() <= 0) {
					fight.agregarAMuertos(target, _lanzador, "");
				}
			}
		} else if (_turnos <= 0) {
			if (_lanzador.esInvisible()) {
				_lanzador.unHide(_hechizoID);
			}
			for (Luchador target : cibles) {
				// si la cible a le buff renvoie de sort
				if (target.tieneBuff(106) && target.getBuffValor(106) >= _nivelHechizo && _hechizoID != 0 && !isBegin) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 106, target.getID() + "", target.getID() + ",1");
					// le lanceur devient donc la cible
					target = _lanzador2;
				}
				if (target.tieneBuff(765))// sacrifice
				{
					if (target.getBuff(765) != null && !target.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(fight, target);
						target = target.getBuff(765).getLanzador();
					}
				}

				int dmg = Formulas.getRandomValor(_args.split(";")[5]);
				dmg = getMaxMinHechizo(target, dmg);
				int finalDommage = Formulas.calculFinalDommage(fight, _lanzador, target, CentroInfo.ELEMENT_EAU, dmg,
						false, false, _hechizoID, isBegin, glifo);
				if (!isBegin)
				 {
					finalDommage = aplicarBuffContraGolpe(finalDommage, target, _lanzador, fight, poison);// S'il y a
																											// des buffs
																											// sp�ciaux
				}

				if (finalDommage > target.getPDVConBuff())
				 {
					finalDommage = target.getPDVConBuff();// Target va mourrir
				}
				target.restarPDV(finalDommage);
				finalDommage = -(finalDommage);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, _lanzador.getID() + "",
						target.getID() + "," + finalDommage);
				int heal = 0;
				if (target.tieneBuff(79)) {
					if (finalDommage < 0) {
						heal = (-finalDommage) / 2;
					} else {
						heal = 0;
					}
				} else {
					heal = (-finalDommage) / 2;
				}
				if ((_lanzador.getPDVConBuff() + heal) > _lanzador.getPDVMaxConBuff()) {
					heal = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
				}
				_lanzador.restarPDV(-heal);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, target.getID() + "", _lanzador.getID() + "," + heal);

				if (target.getPDVConBuff() <= 0) {
					fight.agregarAMuertos(target, _lanzador, "");
				}
			}
		} else {
			for (Luchador target : cibles) {
				target.addBuff(_efectoID, _valor, _turnos, 0, true, _hechizoID, _args, _lanzador, poison, true);// on
																												// applique
																												// un
																												// buff
			}
		}
	}

	private void aplicarEfecto_92(ArrayList<Luchador> cibles, Pelea fight, boolean isCaC, boolean glifo) {// roba
		// PDV
		// tierra
		if (_lanzador.esInvisible()) {
			_lanzador.unHide(_hechizoID);
		}
		if (isCaC) {
			for (Luchador target : cibles) {
				if (target.tieneBuff(765))// sacrifice
				{
					if (target.getBuff(765) != null && !target.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(fight, target);
						target = target.getBuff(765).getLanzador();
					}
				}

				int dmg = Formulas.getRandomValor(_args.split(";")[5]);
				dmg = getMaxMinHechizo(target, dmg);
				int finalDommage = Formulas.calculFinalDommage(fight, _lanzador, target, CentroInfo.ELEMENT_TERRE, dmg,
						false, true, _hechizoID, isBegin, glifo);

				if (!isBegin)
				 {
					finalDommage = aplicarBuffContraGolpe(finalDommage, target, _lanzador, fight, poison);// S'il y a
																											// des buffs
																											// sp�ciaux
				}

				if (finalDommage > target.getPDVConBuff())
				 {
					finalDommage = target.getPDVConBuff();// Target va mourrir
				}
				target.restarPDV(finalDommage);
				finaldaÑo += finalDommage;
				finalDommage = -(finalDommage);
				if (!isCaC) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, _lanzador.getID() + "",
							target.getID() + "," + finalDommage);
				} else {
					if (cibles.size() == 1) {
						targetpeles = target;
					} else {
						GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, _lanzador.getID() + "",
								target.getID() + "," + finalDommage);
					}
				}
				int heal = 0;
				if (target.tieneBuff(79)) {
					if (finalDommage < 0) {
						heal = (-finalDommage) / 2;
					} else {
						heal = 0;
					}
				} else {
					heal = (-finalDommage) / 2;
				}
				if ((_lanzador.getPDVConBuff() + heal) > _lanzador.getPDVMaxConBuff()) {
					heal = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
				}
				_lanzador.restarPDV(-heal);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, target.getID() + "", _lanzador.getID() + "," + heal);
				if (target.getPDVConBuff() <= 0) {
					fight.agregarAMuertos(target, _lanzador, "");
				}
			}
		} else if (_turnos <= 0) {
			for (Luchador target : cibles) {
				// si la cible a le buff renvoie de sort
				if (target.tieneBuff(106) && target.getBuffValor(106) >= _nivelHechizo && _hechizoID != 0 && !isBegin) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 106, target.getID() + "", target.getID() + ",1");
					// le lanceur devient donc la cible
					target = _lanzador2;
				}
				if (target.tieneBuff(765))// sacrifice
				{
					if (target.getBuff(765) != null && !target.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(fight, target);
						target = target.getBuff(765).getLanzador();
					}
				}

				int dmg = Formulas.getRandomValor(_args.split(";")[5]);
				dmg = getMaxMinHechizo(target, dmg);
				int finalDommage = Formulas.calculFinalDommage(fight, _lanzador, target, CentroInfo.ELEMENT_TERRE, dmg,
						false, false, _hechizoID, isBegin, glifo);

				if (!isBegin)
				 {
					finalDommage = aplicarBuffContraGolpe(finalDommage, target, _lanzador, fight, poison);// S'il y a
				}
																											// des buffs
																											// sp�ciaux
				if (finalDommage > target.getPDVConBuff())
				 {
					finalDommage = target.getPDVConBuff();// Target va mourrir
				}
				target.restarPDV(finalDommage);
				finalDommage = -(finalDommage);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, _lanzador.getID() + "",
						target.getID() + "," + finalDommage);

				int heal = 0;
				if (target.tieneBuff(79)) {
					if (finalDommage < 0) {
						heal = (-finalDommage) / 2;
					} else {
						heal = 0;
					}
				} else {
					heal = (-finalDommage) / 2;
				}
				if ((_lanzador.getPDVConBuff() + heal) > _lanzador.getPDVMaxConBuff()) {
					heal = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
				}
				_lanzador.restarPDV(-heal);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, target.getID() + "", _lanzador.getID() + "," + heal);

				if (target.getPDVConBuff() <= 0) {
					fight.agregarAMuertos(target, _lanzador, "");
				}
			}
		} else {
			for (Luchador target : cibles) {
				target.addBuff(_efectoID, _valor, _turnos, 0, true, _hechizoID, _args, _lanzador, poison, true);// on
																												// applique
																												// un
																												// buff
			}
		}
	}

	private void aplicarEfecto_93(ArrayList<Luchador> cibles, Pelea fight, boolean isCaC, boolean glifo) {// roba
		// PDV
		// aire
		if (_lanzador.esInvisible()) {
			_lanzador.unHide(_hechizoID);
		}
		if (isCaC) {
			for (Luchador target : cibles) {
				if (target.tieneBuff(765))// sacrifice
				{
					if (target.getBuff(765) != null && !target.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(fight, target);
						target = target.getBuff(765).getLanzador();
					}
				}

				int dmg = Formulas.getRandomValor(_args.split(";")[5]);
				dmg = getMaxMinHechizo(target, dmg);
				int finalDommage = Formulas.calculFinalDommage(fight, _lanzador, target, CentroInfo.ELEMENT_AIR, dmg,
						false, true, _hechizoID, isBegin, glifo);

				if (!isBegin)
				 {
					finalDommage = aplicarBuffContraGolpe(finalDommage, target, _lanzador, fight, poison);// S'il y a
																											// des buffs
																											// sp�ciaux
				}

				if (finalDommage > target.getPDVConBuff())
				 {
					finalDommage = target.getPDVConBuff();// Target va mourrir
				}
				target.restarPDV(finalDommage);
				finaldaÑo += finalDommage;
				finalDommage = -(finalDommage);
				if (!isCaC) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, _lanzador.getID() + "",
							target.getID() + "," + finalDommage);
				} else {
					if (cibles.size() == 1) {
						targetpeles = target;
					} else {
						GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, _lanzador.getID() + "",
								target.getID() + "," + finalDommage);
					}
				}
				int heal = 0;
				if (target.tieneBuff(79)) {
					if (finalDommage < 0) {
						heal = (-finalDommage) / 2;
					} else {
						heal = 0;
					}
				} else {
					heal = (-finalDommage) / 2;
				}
				if ((_lanzador.getPDVConBuff() + heal) > _lanzador.getPDVMaxConBuff()) {
					heal = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
				}
				_lanzador.restarPDV(-heal);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, target.getID() + "", _lanzador.getID() + "," + heal);
				if (target.getPDVConBuff() <= 0) {
					fight.agregarAMuertos(target, _lanzador, "");
				}
			}
		} else if (_turnos <= 0) {
			for (Luchador target : cibles) {
				// si la cible a le buff renvoie de sort
				if (target.tieneBuff(106) && target.getBuffValor(106) >= _nivelHechizo && _hechizoID != 0 && !isBegin) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 106, target.getID() + "", target.getID() + ",1");
					// le lanceur devient donc la cible
					target = _lanzador2;
				}
				if (target.tieneBuff(765))// sacrifice
				{
					if (target.getBuff(765) != null && !target.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(fight, target);
						target = target.getBuff(765).getLanzador();
					}
				}

				int dmg = Formulas.getRandomValor(_args.split(";")[5]);
				dmg = getMaxMinHechizo(target, dmg);
				int finalDommage = Formulas.calculFinalDommage(fight, _lanzador, target, CentroInfo.ELEMENT_AIR, dmg,
						false, false, _hechizoID, isBegin, glifo);

				if (!isBegin)
				 {
					finalDommage = aplicarBuffContraGolpe(finalDommage, target, _lanzador, fight, poison);// S'il y a
				}
																											// des buffs
																											// sp�ciaux
				if (finalDommage > target.getPDVConBuff())
				 {
					finalDommage = target.getPDVConBuff();// Target va mourrir
				}
				target.restarPDV(finalDommage);
				finalDommage = -(finalDommage);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, _lanzador.getID() + "",
						target.getID() + "," + finalDommage);

				int heal = 0;
				if (target.tieneBuff(79)) {
					if (finalDommage < 0) {
						heal = (-finalDommage) / 2;
					} else {
						heal = 0;
					}
				} else {
					heal = (-finalDommage) / 2;
				}
				if ((_lanzador.getPDVConBuff() + heal) > _lanzador.getPDVMaxConBuff()) {
					heal = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
				}
				_lanzador.restarPDV(-heal);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, target.getID() + "", _lanzador.getID() + "," + heal);

				if (target.getPDVConBuff() <= 0) {
					fight.agregarAMuertos(target, _lanzador, "");
				}
			}
		} else {
			for (Luchador target : cibles) {
				target.addBuff(_efectoID, _valor, _turnos, 0, true, _hechizoID, _args, _lanzador, poison, true);// on
																												// applique
																												// un
																												// buff
			}
		}
	}

	private void aplicarEfecto_94(ArrayList<Luchador> cibles, Pelea fight, boolean isCaC, boolean glifo) {// roba
		// PDV
		// fuego
		if (_lanzador.esInvisible()) {
			_lanzador.unHide(_hechizoID);
		}
		if (isCaC)// CaC Eau
		{
			for (Luchador target : cibles) {
				if (target.tieneBuff(765))// sacrifice
				{
					if (target.getBuff(765) != null && !target.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(fight, target);
						target = target.getBuff(765).getLanzador();
					}
				}

				int dmg = Formulas.getRandomValor(_args.split(";")[5]);
				dmg = getMaxMinHechizo(target, dmg);
				int finalDommage = Formulas.calculFinalDommage(fight, _lanzador, target, CentroInfo.ELEMENT_FEU, dmg,
						false, true, _hechizoID, isBegin, glifo);

				if (!isBegin)
				 {
					finalDommage = aplicarBuffContraGolpe(finalDommage, target, _lanzador, fight, poison);// S'il y a
																											// des buffs
																											// sp�ciaux
				}

				if (finalDommage > target.getPDVConBuff())
				 {
					finalDommage = target.getPDVConBuff();// Target va mourrir
				}
				target.restarPDV(finalDommage);
				finaldaÑo += finalDommage;
				finalDommage = -(finalDommage);
				if (!isCaC) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, _lanzador.getID() + "",
							target.getID() + "," + finalDommage);
				} else {
					if (cibles.size() == 1) {
						targetpeles = target;
					} else {
						GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, _lanzador.getID() + "",
								target.getID() + "," + finalDommage);
					}
				}
				int heal = 0;
				if (target.tieneBuff(79)) {
					if (finalDommage < 0) {
						heal = (-finalDommage) / 2;
					} else {
						heal = 0;
					}
				} else {
					heal = (-finalDommage) / 2;
				}
				if ((_lanzador.getPDVConBuff() + heal) > _lanzador.getPDVMaxConBuff()) {
					heal = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
				}
				_lanzador.restarPDV(-heal);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, target.getID() + "", _lanzador.getID() + "," + heal);
				if (target.getPDVConBuff() <= 0) {
					fight.agregarAMuertos(target, _lanzador, "");
				}
			}
		} else if (_turnos <= 0) {
			for (Luchador target : cibles) {
				// si la cible a le buff renvoie de sort
				if (target.tieneBuff(106) && target.getBuffValor(106) >= _nivelHechizo && _hechizoID != 0 && !isBegin) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 106, target.getID() + "", target.getID() + ",1");
					// le lanceur devient donc la cible
					target = _lanzador2;
				}
				if (target.tieneBuff(765))// sacrifice
				{
					if (target.getBuff(765) != null && !target.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(fight, target);
						target = target.getBuff(765).getLanzador();
					}
				}

				int dmg = Formulas.getRandomValor(_args.split(";")[5]);
				dmg = getMaxMinHechizo(target, dmg);
				int finalDommage = Formulas.calculFinalDommage(fight, _lanzador, target, CentroInfo.ELEMENT_FEU, dmg,
						false, false, _hechizoID, isBegin, glifo);

				if (!isBegin)
				 {
					finalDommage = aplicarBuffContraGolpe(finalDommage, target, _lanzador, fight, poison);// S'il y a
				}
																											// des buffs
																											// sp�ciaux
				if (finalDommage > target.getPDVConBuff())
				 {
					finalDommage = target.getPDVConBuff();// Target va mourrir
				}
				target.restarPDV(finalDommage);
				finalDommage = -(finalDommage);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, _lanzador.getID() + "",
						target.getID() + "," + finalDommage);
				int heal = 0;
				if (target.tieneBuff(79)) {
					if (finalDommage < 0) {
						heal = (-finalDommage) / 2;
					} else {
						heal = 0;
					}
				} else {
					heal = (-finalDommage) / 2;
				}
				if ((_lanzador.getPDVConBuff() + heal) > _lanzador.getPDVMaxConBuff()) {
					heal = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
				}
				_lanzador.restarPDV(-heal);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, target.getID() + "", _lanzador.getID() + "," + heal);

				if (target.getPDVConBuff() <= 0) {
					fight.agregarAMuertos(target, _lanzador, "");
				}
			}
		} else {
			for (Luchador target : cibles) {
				target.addBuff(_efectoID, _valor, _turnos, 0, true, _hechizoID, _args, _lanzador, poison, true);// on
																												// applique
																												// un
																												// buff
			}
		}
	}

	private void aplicarEfecto_95(ArrayList<Luchador> cibles, Pelea fight, boolean isCaC, boolean glifo) {// roba
		// PDV
		// neutral
		if (_lanzador.esInvisible()) {
			_lanzador.unHide(_hechizoID);
		}
		if (isCaC)// CaC Eau
		{
			for (Luchador target : cibles) {
				if (target.tieneBuff(765))// sacrifice
				{
					if (target.getBuff(765) != null && !target.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(fight, target);
						target = target.getBuff(765).getLanzador();
					}
				}

				int dmg = Formulas.getRandomValor(_args.split(";")[5]);
				dmg = getMaxMinHechizo(target, dmg);
				int finalDommage = Formulas.calculFinalDommage(fight, _lanzador, target, CentroInfo.ELEMENT_NEUTRE, dmg,
						false, true, _hechizoID, isBegin, glifo);

				if (!isBegin)
				 {
					finalDommage = aplicarBuffContraGolpe(finalDommage, target, _lanzador, fight, poison);// S'il y a
																											// des buffs
																											// sp�ciaux
				}

				if (finalDommage > target.getPDVConBuff())
				 {
					finalDommage = target.getPDVConBuff();// Target va mourrir
				}
				target.restarPDV(finalDommage);
				finaldaÑo += finalDommage;
				finalDommage = -(finalDommage);
				if (!isCaC) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, _lanzador.getID() + "",
							target.getID() + "," + finalDommage);
				} else {
					if (cibles.size() == 1) {
						targetpeles = target;
					} else {
						GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, _lanzador.getID() + "",
								target.getID() + "," + finalDommage);
					}
				}
				int heal = 0;
				if (target.tieneBuff(79)) {
					if (finalDommage < 0) {
						heal = (-finalDommage) / 2;
					} else {
						heal = 0;
					}
				} else {
					heal = (-finalDommage) / 2;
				}
				if ((_lanzador.getPDVConBuff() + heal) > _lanzador.getPDVMaxConBuff()) {
					heal = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
				}
				_lanzador.restarPDV(-heal);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, target.getID() + "", _lanzador.getID() + "," + heal);
				if (target.getPDVConBuff() <= 0) {
					fight.agregarAMuertos(target, _lanzador, "");
				}
			}
		} else if (_turnos <= 0) {
			for (Luchador target : cibles) {
				// si la cible a le buff renvoie de sort
				if (target.tieneBuff(106) && target.getBuffValor(106) >= _nivelHechizo && _hechizoID != 0 && !isBegin) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 106, target.getID() + "", target.getID() + ",1");
					// le lanceur devient donc la cible
					target = _lanzador2;
				}
				if (target.tieneBuff(765))// sacrifice
				{
					if (target.getBuff(765) != null && !target.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(fight, target);
						target = target.getBuff(765).getLanzador();
					}
				}

				int dmg = Formulas.getRandomValor(_args.split(";")[5]);
				dmg = getMaxMinHechizo(target, dmg);
				int finalDommage = Formulas.calculFinalDommage(fight, _lanzador, target, CentroInfo.ELEMENT_NEUTRE, dmg,
						false, false, _hechizoID, isBegin, glifo);

				if (!isBegin)
				 {
					finalDommage = aplicarBuffContraGolpe(finalDommage, target, _lanzador, fight, poison);// S'il y a
				}
																											// des buffs
																											// sp�ciaux
				if (finalDommage > target.getPDVConBuff())
				 {
					finalDommage = target.getPDVConBuff();// Target va mourrir
				}
				target.restarPDV(finalDommage);
				finalDommage = -(finalDommage);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, _lanzador.getID() + "",
						target.getID() + "," + finalDommage);

				int heal = 0;
				if (target.tieneBuff(79)) {
					if (finalDommage < 0) {
						heal = (-finalDommage) / 2;
					} else {
						heal = 0;
					}
				} else {
					heal = (-finalDommage) / 2;
				}
				if ((_lanzador.getPDVConBuff() + heal) > _lanzador.getPDVMaxConBuff()) {
					heal = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
				}
				_lanzador.restarPDV(-heal);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, target.getID() + "", _lanzador.getID() + "," + heal);

				if (target.getPDVConBuff() <= 0) {
					fight.agregarAMuertos(target, _lanzador, "");
				}
			}
		} else {
			for (Luchador target : cibles) {
				target.addBuff(_efectoID, _valor, _turnos, 0, true, _hechizoID, _args, _lanzador, poison, true);// on
																												// applique
																												// un
																												// buff
			}
		}
	}

	int finaldaÑo = 0;
	Luchador targetpeles = null;

	private void aplicarEfecto_96(ArrayList<Luchador> cibles, Pelea fight, boolean isCaC, boolean glifo) { // daÑos
		// agua
		if (isCaC)// CaC Eau
		{
			if (_lanzador.esInvisible()) {
				_lanzador.unHide(_hechizoID);
			}
			for (Luchador target : cibles) {
				if (_lanzador.getMob() != null && !_lanzador.esInvocacion() && target.getMob() != null
						&& !target.esInvocacion()) {
					continue;
				}
				if (target.tieneBuff(765) && !isBegin)// sacrifice
				{
					if (target.getBuff(765) != null && !target.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(fight, target);
						target = target.getBuff(765).getLanzador();
					}
				}

				int dmg = Formulas.getRandomValor(_args.split(";")[5]);
				dmg = getMaxMinHechizo(target, dmg);
				// Si le sort est boost� par un buff sp�cifique
				for (EfectoHechizo SE : _lanzador.getBuffsPorEfectoID(293)) {
					if (SE.getValor() == _hechizoID) {
						int add = -1;
						try {
							add = Integer.parseInt(SE.getArgs().split(";")[2]);
						} catch (Exception e) {
						}
						if (add <= 0) {
							continue;
						}
						dmg += add;
					}
				}
				int finalDommage = Formulas.calculFinalDommage(fight, _lanzador, target, CentroInfo.ELEMENT_EAU, dmg,
						false, true, _hechizoID, isBegin, glifo);

				if (!isBegin)
				 {
					finalDommage = aplicarBuffContraGolpe(finalDommage, target, _lanzador, fight, poison);// S'il y a
				}
																											// des buffs
																											// sp�ciaux
				if (target.getMob() != null) {
					if (target.getMob().getModelo().getID() == 2750) {
						int heal = (finalDommage) / 2;
						if ((_lanzador.getPDVConBuff() + heal) > _lanzador.getPDVMaxConBuff()) {
							heal = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
						}
						_lanzador.restarPDV(-heal);
						GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, target.getID() + "",
								_lanzador.getID() + "," + heal);
					}
				}
				if (finalDommage > target.getPDVConBuff())
				 {
					finalDommage = target.getPDVConBuff();// Target va mourrir
				}
				target.restarPDV(finalDommage);
				finaldaÑo += finalDommage;
				finalDommage = -(finalDommage);
				if (!isCaC) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, _lanzador.getID() + "",
							target.getID() + "," + finalDommage);
				} else {
					if (cibles.size() == 1) {
						targetpeles = target;
					} else {
						GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, _lanzador.getID() + "",
								target.getID() + "," + finalDommage);
					}
				}
				if (target.getPDVConBuff() <= 0) {
					fight.agregarAMuertos(target, _lanzador, "");
				}
			}
		} else if (_turnos <= 0) {
			if (_lanzador.esInvisible()) {
				_lanzador.unHide(_hechizoID);
			}
			for (Luchador target : cibles) {
				if (_lanzador.getMob() != null && !_lanzador.esInvocacion() && target.getMob() != null
						&& !target.esInvocacion()) {
					continue;
				}
				// si la cible a le buff renvoie de sort
				if (target.tieneBuff(106) && target.getBuffValor(106) >= _nivelHechizo && _hechizoID != 0 && !isBegin) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 106, target.getID() + "", target.getID() + ",1");
					// le lanceur devient donc la cible
					target = _lanzador2;
				}
				if (target.tieneBuff(765) && !isBegin)// sacrifice
				{
					if (target.getBuff(765) != null && !target.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(fight, target);
						target = target.getBuff(765).getLanzador();
					}
				}

				int dmg = Formulas.getRandomValor(_args.split(";")[5]);
				dmg = getMaxMinHechizo(target, dmg);
				// Si le sort est boost� par un buff sp�cifique
				for (EfectoHechizo SE : _lanzador.getBuffsPorEfectoID(293)) {
					if (SE.getValor() == _hechizoID) {
						int add = -1;
						try {
							add = Integer.parseInt(SE.getArgs().split(";")[2]);
						} catch (Exception e) {
						}
						if (add <= 0) {
							continue;
						}
						dmg += add;
					}
				}

				int finalDommage = Formulas.calculFinalDommage(fight, _lanzador, target, CentroInfo.ELEMENT_EAU, dmg,
						false, false, _hechizoID, isBegin, glifo);

				if (!isBegin)
				 {
					finalDommage = aplicarBuffContraGolpe(finalDommage, target, _lanzador, fight, poison);// S'il y a
				}
																											// des buffs
																											// sp�ciaux
				if (target.getMob() != null) {
					if (target.getMob().getModelo().getID() == 2750) {
						int heal = (finalDommage) / 2;
						if ((_lanzador.getPDVConBuff() + heal) > _lanzador.getPDVMaxConBuff()) {
							heal = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
						}
						_lanzador.restarPDV(-heal);
						GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, target.getID() + "",
								_lanzador.getID() + "," + heal);
					}
				}
				if (finalDommage > target.getPDVConBuff())
				 {
					finalDommage = target.getPDVConBuff();// Target va mourrir
				}
				target.restarPDV(finalDommage);
				finalDommage = -(finalDommage);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, _lanzador.getID() + "",
						target.getID() + "," + finalDommage);
				if (target.getPDVConBuff() <= 0) {
					fight.agregarAMuertos(target, _lanzador, "");
				}
			}
		} else {
			for (Luchador target : cibles) {
				target.addBuff(_efectoID, _valor, _turnos, 0, true, _hechizoID, _args, _lanzador, poison, true);// on
																												// applique
																												// un
																												// buff
			}
		}
	}

	private void aplicarEfecto_97(ArrayList<Luchador> cibles, Pelea fight, boolean isCaC, boolean glifo) {// daÑos
		// tierra
		if (isCaC)// CaC Terre
		{
			if (_lanzador.esInvisible()) {
				_lanzador.unHide(_hechizoID);
			}
			for (Luchador target : cibles) {
				if (_lanzador.getMob() != null && !_lanzador.esInvocacion() && target.getMob() != null
						&& !target.esInvocacion()) {
					continue;
				}
				if (target.tieneBuff(765) && !isBegin)// sacrifice
				{
					if (target.getBuff(765) != null && !target.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(fight, target);
						target = target.getBuff(765).getLanzador();
					}
				}

				int dmg = Formulas.getRandomValor(_args.split(";")[5]);
				dmg = getMaxMinHechizo(target, dmg);
				// Si le sort est boost� par un buff sp�cifique
				for (EfectoHechizo SE : _lanzador.getBuffsPorEfectoID(293)) {
					if (SE.getValor() == _hechizoID) {
						int add = -1;
						try {
							add = Integer.parseInt(SE.getArgs().split(";")[2]);
						} catch (Exception e) {
						}
						if (add <= 0) {
							continue;
						}
						dmg += add;
					}
				}
				int finalDommage = Formulas.calculFinalDommage(fight, _lanzador, target, CentroInfo.ELEMENT_TERRE, dmg,
						false, true, _hechizoID, isBegin, glifo);
				if (!isBegin)
				 {
					finalDommage = aplicarBuffContraGolpe(finalDommage, target, _lanzador, fight, poison);// S'il y a
				}
																											// des buffs
																											// sp�ciaux
				if (target.getMob() != null) {
					if (target.getMob().getModelo().getID() == 2750) {
						int heal = (finalDommage) / 2;
						if ((_lanzador.getPDVConBuff() + heal) > _lanzador.getPDVMaxConBuff()) {
							heal = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
						}
						_lanzador.restarPDV(-heal);
						GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, target.getID() + "",
								_lanzador.getID() + "," + heal);
					}
				}
				if (finalDommage > target.getPDVConBuff())
				 {
					finalDommage = target.getPDVConBuff();// Target va mourrir
				}
				target.restarPDV(finalDommage);
				finaldaÑo += finalDommage;
				finalDommage = -(finalDommage);
				if (!isCaC) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, _lanzador.getID() + "",
							target.getID() + "," + finalDommage);
				} else {
					if (cibles.size() == 1) {
						targetpeles = target;
					} else {
						GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, _lanzador.getID() + "",
								target.getID() + "," + finalDommage);
					}
				}
				if (target.getPDVConBuff() <= 0) {
					fight.agregarAMuertos(target, _lanzador, "");
				}
			}
		} else if (_turnos <= 0) {
			if (_lanzador.esInvisible()) {
				_lanzador.unHide(_hechizoID);
			}
			for (Luchador target : cibles) {
				if (target.estaMuerto()) {
					continue;
				}
				if (_lanzador.getMob() != null && !_lanzador.esInvocacion() && target.getMob() != null
						&& !target.esInvocacion())
				 {
					continue;
				// si la cible a le buff renvoie de sort
				}

				if (target.tieneBuff(106) && target.getBuffValor(106) >= _nivelHechizo && _hechizoID != 0 && !isBegin) {

					GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 106, target.getID() + "", target.getID() + ",1");
					target = _lanzador2;
				}
				if (target.tieneBuff(765) && !isBegin)// sacrifice
				{
					if (target.getBuff(765) != null && !target.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(fight, target);
						target = target.getBuff(765).getLanzador();
					}
				}

				int dmg = Formulas.getRandomValor(_args.split(";")[5]);
				dmg = getMaxMinHechizo(target, dmg);
				// Si le sort est boost� par un buff sp�cifique
				for (EfectoHechizo SE : _lanzador.getBuffsPorEfectoID(293)) {
					if (SE.getValor() == _hechizoID) {
						int add = -1;
						try {
							add = Integer.parseInt(SE.getArgs().split(";")[2]);
						} catch (Exception e) {
						}
						if (add <= 0) {
							continue;
						}
						dmg += add;

					}
				}
				if (_hechizoID == 160 && target == _lanzador) {
					continue;// Ep�e de Iop ne tape pas le lanceur.
				} else if (_suerte > 0 && _hechizoID == 108 && !isBegin)// Esprit f�lin ?
				{
					int fDommage = Formulas.calculFinalDommage(fight, _lanzador, _lanzador, CentroInfo.ELEMENT_TERRE,
							dmg, false, false, _hechizoID, isBegin, glifo);
					if (!isBegin)
					 {
						fDommage = aplicarBuffContraGolpe(fDommage, _lanzador, _lanzador, fight, poison);// S'il y a des
					}
																											// buffs
																											// sp�ciaux
					if (fDommage > _lanzador.getPDVConBuff())
					 {
						fDommage = _lanzador.getPDVConBuff();// Target va mourrir
					}
					_lanzador.restarPDV(fDommage);
					if (target.tieneBuff(786)) {
						int heal = (fDommage) / 2;
						if ((_lanzador.getPDVConBuff() + heal) > _lanzador.getPDVMaxConBuff()) {
							heal = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
						}
						_lanzador.restarPDV(-heal);
						GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, target.getID() + "",
								_lanzador.getID() + "," + heal);

					}
					fDommage = -(fDommage);
					GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, _lanzador.getID() + "",
							_lanzador.getID() + "," + fDommage);
					if (_lanzador.getPDVConBuff() <= 0) {
						fight.agregarAMuertos(_lanzador, _lanzador, "");
					}
				} else {
					int finalDommage = Formulas.calculFinalDommage(fight, _lanzador, target, CentroInfo.ELEMENT_TERRE,
							dmg, false, false, _hechizoID, isBegin, glifo);
					if (!isBegin)
					 {
						finalDommage = aplicarBuffContraGolpe(finalDommage, target, _lanzador, fight, poison);// S'il y
					}
																												// a des
																												// buffs
																												// sp�ciaux
					if (finalDommage > target.getPDVConBuff())
					 {
						finalDommage = target.getPDVConBuff();// Target va mourrir
					}
					target.restarPDV(finalDommage);
					if (target.tieneBuff(786)) {
						int heal = (-finalDommage) / 2;
						if ((_lanzador.getPDVConBuff() + heal) > _lanzador.getPDVMaxConBuff()) {
							heal = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
						}
						_lanzador.restarPDV(-heal);
						GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, target.getID() + "",
								_lanzador.getID() + "," + heal);
					}
					finalDommage = -(finalDommage);
					GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, _lanzador.getID() + "",
							target.getID() + "," + finalDommage);
					if (target.getPDVConBuff() <= 0) {
						fight.agregarAMuertos(target, _lanzador, "");
					}
				}

			}
		} else {
			for (Luchador target : cibles) {
				target.addBuff(_efectoID, _valor, _turnos, 0, true, _hechizoID, _args, _lanzador, poison, true);// on
																												// applique
																												// un
																												// buff
			}
		}
	}

	private void aplicarEfecto_98(ArrayList<Luchador> cibles, Pelea fight, boolean isCaC, boolean glifo) {// daÑos
		// aire
		if (isCaC)// CaC Air
		{
			if (_lanzador.esInvisible()) {
				_lanzador.unHide(_hechizoID);
			}
			for (Luchador target : cibles) {
				if (_lanzador.getMob() != null && !_lanzador.esInvocacion() && target.getMob() != null
						&& !target.esInvocacion()) {
					continue;
				}
				if (target.tieneBuff(765) && !isBegin)// sacrifice
				{
					if (target.getBuff(765) != null && !target.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(fight, target);
						target = target.getBuff(765).getLanzador();
					}
				}

				int dmg = Formulas.getRandomValor(_args.split(";")[5]);
				dmg = getMaxMinHechizo(target, dmg);
				// Si le sort est boost� par un buff sp�cifique
				for (EfectoHechizo SE : _lanzador.getBuffsPorEfectoID(293)) {
					if (SE.getValor() == _hechizoID) {
						int add = -1;
						try {
							add = Integer.parseInt(SE.getArgs().split(";")[2]);
						} catch (Exception e) {
						}
						if (add <= 0) {
							continue;
						}
						dmg += add;
					}
				}
				int finalDommage = Formulas.calculFinalDommage(fight, _lanzador, target, CentroInfo.ELEMENT_AIR, dmg,
						false, true, _hechizoID, isBegin, glifo);

				if (!isBegin)
				 {
					finalDommage = aplicarBuffContraGolpe(finalDommage, target, _lanzador, fight, poison);// S'il y a
				}
																											// des buffs
																											// sp�ciaux
				if (target.getMob() != null) {
					if (target.getMob().getModelo().getID() == 2750) {
						int heal = (finalDommage) / 2;
						if ((_lanzador.getPDVConBuff() + heal) > _lanzador.getPDVMaxConBuff()) {
							heal = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
						}
						_lanzador.restarPDV(-heal);
						GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, target.getID() + "",
								_lanzador.getID() + "," + heal);
					}
				}
				if (finalDommage > target.getPDVConBuff())
				 {
					finalDommage = target.getPDVConBuff();// Target va mourrir
				}
				target.restarPDV(finalDommage);
				finaldaÑo += finalDommage;
				finalDommage = -(finalDommage);
				if (!isCaC) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, _lanzador.getID() + "",
							target.getID() + "," + finalDommage);
				} else {
					if (cibles.size() == 1) {
						targetpeles = target;
					} else {
						GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, _lanzador.getID() + "",
								target.getID() + "," + finalDommage);
					}
				}
				if (target.getPDVConBuff() <= 0) {
					fight.agregarAMuertos(target, _lanzador, "");
				}
			}
		} else if (_turnos <= 0) {
			if (_lanzador.esInvisible()) {
				_lanzador.unHide(_hechizoID);
			}
			for (Luchador target : cibles) {
				if (_lanzador.getMob() != null && !_lanzador.esInvocacion() && target.getMob() != null
						&& !target.esInvocacion()) {
					continue;
				}
				// si la cible a le buff renvoie de sort
				if (target.tieneBuff(106) && target.getBuffValor(106) >= _nivelHechizo && _hechizoID != 0 && !isBegin) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 106, target.getID() + "", target.getID() + ",1");
					// le lanceur devient donc la cible
					target = _lanzador2;
				}
				if (target.tieneBuff(765) && !isBegin)// sacrifice
				{
					if (target.getBuff(765) != null && !target.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(fight, target);
						target = target.getBuff(765).getLanzador();
					}
				}
				int dmg = Formulas.getRandomValor(_args.split(";")[5]);
				dmg = getMaxMinHechizo(target, dmg);
				// Si le sort est boost� par un buff sp�cifique
				for (EfectoHechizo SE : _lanzador.getBuffsPorEfectoID(293)) {
					if (SE.getValor() == _hechizoID) {
						int add = -1;
						try {
							add = Integer.parseInt(SE.getArgs().split(";")[2]);
						} catch (Exception e) {
						}
						if (add <= 0) {
							continue;
						}
						dmg += add;
					}
				}

				int finalDommage = Formulas.calculFinalDommage(fight, _lanzador, target, CentroInfo.ELEMENT_AIR, dmg,
						false, false, _hechizoID, isBegin, glifo);
				if (!isBegin)
				 {
					finalDommage = aplicarBuffContraGolpe(finalDommage, target, _lanzador, fight, poison);// S'il y a
				}
																											// des buffs
																											// sp�ciaux
				if (target.getMob() != null) {
					if (target.getMob().getModelo().getID() == 2750) {
						int heal = (finalDommage) / 2;
						if ((_lanzador.getPDVConBuff() + heal) > _lanzador.getPDVMaxConBuff()) {
							heal = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
						}
						_lanzador.restarPDV(-heal);
						GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, target.getID() + "",
								_lanzador.getID() + "," + heal);
					}
				}
				if (finalDommage > target.getPDVConBuff())
				 {
					finalDommage = target.getPDVConBuff();// Target va mourrir
				}
				target.restarPDV(finalDommage);
				finalDommage = -(finalDommage);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, _lanzador.getID() + "",
						target.getID() + "," + finalDommage);
				if (target.getPDVConBuff() <= 0) {
					fight.agregarAMuertos(target, _lanzador, "");
				}
			}
		} else {
			for (Luchador target : cibles) {
				target.addBuff(_efectoID, _valor, _turnos, 0, true, _hechizoID, _args, _lanzador, poison, true);// on
																												// applique
																												// un
																												// buff
			}
		}
	}

	private void aplicarEfecto_99(ArrayList<Luchador> cibles, Pelea fight, boolean isCaC, boolean glifo) {// daÑos
		// fuego
		if (_lanzador.esInvisible()) {
			_lanzador.unHide(_hechizoID);
		}

		if (isCaC)// CaC Feu
		{
			for (Luchador target : cibles) {
				if (_lanzador.getMob() != null && !_lanzador.esInvocacion() && target.getMob() != null
						&& !target.esInvocacion()) {
					continue;
				}
				if (target.tieneBuff(765) && !isBegin)// sacrifice
				{
					if (target.getBuff(765) != null && !target.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(fight, target);
						target = target.getBuff(765).getLanzador();
					}
				}

				int dmg = Formulas.getRandomValor(_args.split(";")[5]);
				dmg = getMaxMinHechizo(target, dmg);
				// Si le sort est boost� par un buff sp�cifique
				for (EfectoHechizo SE : _lanzador.getBuffsPorEfectoID(293)) {
					if (SE.getValor() == _hechizoID) {
						int add = -1;
						try {
							add = Integer.parseInt(SE.getArgs().split(";")[2]);
						} catch (Exception e) {
						}
						if (add <= 0) {
							continue;
						}
						dmg += add;
					}
				}
				int finalDommage = Formulas.calculFinalDommage(fight, _lanzador, target, CentroInfo.ELEMENT_FEU, dmg,
						false, true, _hechizoID, isBegin, glifo);

				if (!isBegin)
				 {
					finalDommage = aplicarBuffContraGolpe(finalDommage, target, _lanzador, fight, poison);// S'il y a
				}
																											// des buffs
																											// sp�ciaux
				if (target.getMob() != null) {
					if (target.getMob().getModelo().getID() == 2750) {
						int heal = (finalDommage) / 2;
						if ((_lanzador.getPDVConBuff() + heal) > _lanzador.getPDVMaxConBuff()) {
							heal = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
						}
						_lanzador.restarPDV(-heal);
						GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, target.getID() + "",
								_lanzador.getID() + "," + heal);
					}
				}
				if (finalDommage > target.getPDVConBuff())
				 {
					finalDommage = target.getPDVConBuff();// Target va mourrir
				}
				target.restarPDV(finalDommage);
				finaldaÑo += finalDommage;
				finalDommage = -(finalDommage);
				if (!isCaC) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, _lanzador.getID() + "",
							target.getID() + "," + finalDommage);
				} else {
					if (cibles.size() == 1) {
						targetpeles = target;
					} else {
						GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, _lanzador.getID() + "",
								target.getID() + "," + finalDommage);
					}
				}
				if (target.getPDVConBuff() <= 0) {
					fight.agregarAMuertos(target, _lanzador, "");
				}
			}
		} else if (_turnos <= 0) {
			for (Luchador target : cibles) {
				if (_lanzador.getMob() != null && !_lanzador.esInvocacion() && target.getMob() != null
						&& !target.esInvocacion()) {
					continue;
				}
				if (_hechizoID == 36 && target == _lanzador)// Frappe du Craqueleur ne tape pas l'osa
				{
					continue;
				}
				// si la cible a le buff renvoie de sort
				if (target.tieneBuff(106) && target.getBuffValor(106) >= _nivelHechizo && _hechizoID != 0 && !isBegin) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 106, target.getID() + "", target.getID() + ",1");
					// le lanceur devient donc la cible
					target = _lanzador2;
				}
				if (target.tieneBuff(765) && !isBegin)// sacrifice
				{
					if (target.getBuff(765) != null && !target.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(fight, target);
						target = target.getBuff(765).getLanzador();
					}
				}

				int dmg = Formulas.getRandomValor(_args.split(";")[5]);
				dmg = getMaxMinHechizo(target, dmg);
				// Si le sort est boost� par un buff sp�cifique
				for (EfectoHechizo SE : _lanzador.getBuffsPorEfectoID(293)) {
					if (SE.getValor() == _hechizoID) {
						int add = -1;
						try {
							add = Integer.parseInt(SE.getArgs().split(";")[2]);
						} catch (Exception e) {
						}
						if (add <= 0) {
							continue;
						}
						dmg += add;
					}
				}

				int finalDommage = Formulas.calculFinalDommage(fight, _lanzador, target, CentroInfo.ELEMENT_FEU, dmg,
						false, false, _hechizoID, isBegin, glifo);

				if (!isBegin)
				 {
					finalDommage = aplicarBuffContraGolpe(finalDommage, target, _lanzador, fight, poison);// S'il y a
				}
																											// des buffs
																											// sp�ciaux
				if (target.getMob() != null) {
					if (target.getMob().getModelo().getID() == 2750) {
						int heal = (finalDommage) / 2;
						if ((_lanzador.getPDVConBuff() + heal) > _lanzador.getPDVMaxConBuff()) {
							heal = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
						}
						_lanzador.restarPDV(-heal);
						GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, target.getID() + "",
								_lanzador.getID() + "," + heal);
					}
				}
				if (finalDommage > target.getPDVConBuff())
				 {
					finalDommage = target.getPDVConBuff();// Target va mourrir
				}
				target.restarPDV(finalDommage);
				finalDommage = -(finalDommage);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, _lanzador.getID() + "",
						target.getID() + "," + finalDommage);
				if (target.getPDVConBuff() <= 0) {
					fight.agregarAMuertos(target, _lanzador, "");
				}
			}
		} else {
			for (Luchador target : cibles) {
				target.addBuff(_efectoID, _valor, _turnos, 0, true, _hechizoID, _args, _lanzador, poison, true);// on
																												// applique
																												// un
																												// buff
			}
		}
	}

	private void aplicarEfecto_100(ArrayList<Luchador> cibles, Pelea fight, boolean isCaC, boolean glifo) {// daÑos
		// neutral
		if (_lanzador.esInvisible()) {
			_lanzador.unHide(_hechizoID);
		}
		if (isCaC)// CaC Neutre
		{
			for (Luchador target : cibles) {

				if (_lanzador.getMob() != null && !_lanzador.esInvocacion() && target.getMob() != null
						&& !target.esInvocacion()) {
					continue;
				}
				if (target.tieneBuff(765) && !isBegin)// sacrifice
				{
					if (target.getBuff(765) != null && !target.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(fight, target);
						target = target.getBuff(765).getLanzador();
					}
				}

				int dmg = Formulas.getRandomValor(_args.split(";")[5]);
				dmg = getMaxMinHechizo(target, dmg);
				// Si le sort est boost� par un buff sp�cifique
				for (EfectoHechizo SE : _lanzador.getBuffsPorEfectoID(293)) {
					if (SE.getValor() == _hechizoID) {
						int add = -1;
						try {
							add = Integer.parseInt(SE.getArgs().split(";")[2]);
						} catch (Exception e) {
						}
						if (add <= 0) {
							continue;
						}
						dmg += add;
					}
				}
				int finalDommage = Formulas.calculFinalDommage(fight, _lanzador, target, CentroInfo.ELEMENT_NEUTRE, dmg,
						false, true, _hechizoID, isBegin, glifo);

				if (!isBegin)
				 {
					finalDommage = aplicarBuffContraGolpe(finalDommage, target, _lanzador, fight, poison);// S'il y a
				}
																											// des buffs
																											// sp�ciaux
				if (target.getMob() != null) {
					if (target.getMob().getModelo().getID() == 2750) {
						int heal = (finalDommage) / 2;
						if ((_lanzador.getPDVConBuff() + heal) > _lanzador.getPDVMaxConBuff()) {
							heal = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
						}
						_lanzador.restarPDV(-heal);
						GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, target.getID() + "",
								_lanzador.getID() + "," + heal);
					}
				}
				if (finalDommage > target.getPDVConBuff())
				 {
					finalDommage = target.getPDVConBuff();// Target va mourrir
				}
				target.restarPDV(finalDommage);
				finaldaÑo += finalDommage;
				finalDommage = -(finalDommage);
				if (!isCaC) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, _lanzador.getID() + "",
							target.getID() + "," + finalDommage);
				} else {
					if (cibles.size() == 1) {
						targetpeles = target;
					} else {
						GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, _lanzador.getID() + "",
								target.getID() + "," + finalDommage);
					}
				}
				if (target.getPDVConBuff() <= 0) {
					fight.agregarAMuertos(target, _lanzador, "");
				}
			}
		} else if (_turnos <= 0) {
			for (Luchador target : cibles) {
				if (_lanzador.getMob() != null && !_lanzador.esInvocacion() && target.getMob() != null
						&& !target.esInvocacion()) {
					continue;
				}
				// si la cible a le buff renvoie de sort
				if (target.tieneBuff(106) && target.getBuffValor(106) >= _nivelHechizo && _hechizoID != 0 && !isBegin) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 106, target.getID() + "", target.getID() + ",1");
					// le lanceur devient donc la cible
					target = _lanzador2;
				}
				if (target.tieneBuff(765) && !isBegin)// sacrifice
				{
					if (target.getBuff(765) != null && !target.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(fight, target);
						target = target.getBuff(765).getLanzador();
					}
				}

				int dmg = Formulas.getRandomValor(_args.split(";")[5]);
				dmg = getMaxMinHechizo(target, dmg);
				// Si le sort est boost� par un buff sp�cifique
				for (EfectoHechizo SE : _lanzador.getBuffsPorEfectoID(293)) {
					if (SE.getValor() == _hechizoID) {
						int add = -1;
						try {
							add = Integer.parseInt(SE.getArgs().split(";")[2]);
						} catch (Exception e) {
						}
						if (add <= 0) {
							continue;
						}
						dmg += add;
					}
				}

				int finalDommage = Formulas.calculFinalDommage(fight, _lanzador, target, CentroInfo.ELEMENT_NEUTRE, dmg,
						false, false, _hechizoID, isBegin, glifo);

				if (!isBegin)
				 {
					finalDommage = aplicarBuffContraGolpe(finalDommage, target, _lanzador, fight, poison);// S'il y a
				}
																											// des buffs
																											// sp�ciaux
				if (target.getMob() != null) {
					if (target.getMob().getModelo().getID() == 2750) {
						int heal = (finalDommage) / 2;
						if ((_lanzador.getPDVConBuff() + heal) > _lanzador.getPDVMaxConBuff()) {
							heal = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
						}
						_lanzador.restarPDV(-heal);
						GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, target.getID() + "",
								_lanzador.getID() + "," + heal);
					}
				}
				if (finalDommage > target.getPDVConBuff())
				 {
					finalDommage = target.getPDVConBuff();// Target va mourrir
				}
				target.restarPDV(finalDommage);
				finalDommage = -(finalDommage);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, _lanzador.getID() + "",
						target.getID() + "," + finalDommage);
				if (target.getPDVConBuff() <= 0) {
					fight.agregarAMuertos(target, _lanzador, "");
				}
			}
		} else {
			for (Luchador target : cibles) {
				target.addBuff(_efectoID, _valor, _turnos, 0, true, _hechizoID, _args, _lanzador, poison, true);// on
																												// applique
																												// un
																												// buff
			}
		}
	}

	private void aplicarEfecto_101(ArrayList<Luchador> objetivos, Pelea pelea) { // - PA
		int minV = Integer.parseInt(_args.split(";")[0]);
		int maxV = Integer.parseInt(_args.split(";")[1]);
		if (maxV <= 0) {
			maxV = minV;
		}
		int val = Formulas.getRandomValor(minV, maxV);
		if (val == -1) {
			return;
		}
		if (_turnos <= 0) {
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto()) {
					continue;
				}
				int perdidos = Formulas.getPuntosPerdidos('a', val, _lanzador, objetivo);
				if ((_valor - perdidos) > 0) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 308, _lanzador.getID() + "",
							objetivo.getID() + "," + (_valor - perdidos));
				}
				if (perdidos > 0) {
					objetivo.addBuff(CentroInfo.STATS_REM_PA, perdidos, 1, 1, true, _hechizoID, _args, _lanzador,
							poison, true);
					if (_turnos <= 1 || _duracion <= 1) {
						GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 101, objetivo.getID() + "",
								objetivo.getID() + ",-" + perdidos);
					}
				}
			}
		} else {
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto()) {
					continue;
				}
				int perdidos = Formulas.getPuntosPerdidos('a', val, _lanzador, objetivo);
				if ((_valor - perdidos) > 0) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 308, _lanzador.getID() + "",
							objetivo.getID() + "," + (_valor - perdidos));
				}
				if (perdidos > 0) {
					if (_hechizoID == 89) {
						objetivo.addBuff(_efectoID, perdidos, 0, 1, true, _hechizoID, _args, _lanzador, poison, true);
					} else {
						objetivo.addBuff(_efectoID, perdidos, 1, 1, true, _hechizoID, _args, _lanzador, poison, true);
					}
					if (_turnos <= 1 || _duracion <= 1) {
						GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 101, objetivo.getID() + "",
								objetivo.getID() + ",-" + perdidos);
					}
				}
			}
		}
	}

	private void aplicarEfecto_105(ArrayList<Luchador> cibles, Pelea pelea) { // daÑos
		// reducidos(tregua,
		// inmunidad, sapo)
		int val = Formulas.getRandomValor(_valores);
		if (val == -1) {
			return;
		}
		for (Luchador target : cibles) {
			val = getMaxMinHechizo(target, val);
			target.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, poison, true);
		}
	}

	private void aplicarEfecto_106(ArrayList<Luchador> cibles, Pelea pelea) { // reenvio de hechizo
		int val = -1;
		try {
			val = Integer.parseInt(_args.split(";")[1]);// Niveau de sort max
		} catch (Exception e) {
		}
		if (val == -1) {
			return;
		}
		for (Luchador target : cibles) {
			val = getMaxMinHechizo(target, val);
			target.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, poison, true);
		}
	}

	private void aplicarEfecto_107(ArrayList<Luchador> cibles, Pelea pelea) { // daÑos devueltos
		if (_turnos < 1) {
			return;// Je vois pas comment, vraiment ...
		} else {
			for (Luchador target : cibles) {
				target.addBuff(_efectoID, _valor, _turnos, 0, true, _hechizoID, _args, _lanzador, poison, true);// on
																												// applique
																												// un
																												// buff
			}
		}
	}

	private void aplicarEfecto_108(ArrayList<Luchador> cibles, Pelea fight, boolean isCaC, boolean glifo) {// curacion
		if (_hechizoID == 441) {
			return;
		}
		if (_turnos <= 0) {
			String[] jet = _args.split(";");
			int dmg = 0;
			if (jet.length < 6) {
				dmg = 1;
			} else {
				dmg = Formulas.getRandomValor(jet[5]);
				dmg = getMaxMinHechizo(_lanzador, dmg);
			}
			for (Luchador target : cibles) {
				if ((_hechizoID == 139 || _hechizoID == 1675) && target.getEquipoBin() != _lanzador.getEquipoBin()) {
					continue;
				}
				if (_hechizoID == 59 && Integer.parseInt(_args.split(";")[0]) == 200
						&& _lanzador.getEquipoBin() != target.getEquipoBin()) {
					continue;
				}
				if (_hechizoID == 59 && Integer.parseInt(_args.split(";")[0]) == 100
						&& _lanzador.getEquipoBin() == target.getEquipoBin()) {
					continue;
				}
				int finalSoin = 0;
				if (_hechizoID == 140) {
					finalSoin = 0;
					int valo1 = Integer.parseInt(_args.split(";")[0]);
					switch (valo1) {
					case 101:
						finalSoin = (target.getPDVMaxConBuff() * 20) / 100; // lvl 1
						break;
					case 102:
						finalSoin = (target.getPDVMaxConBuff() * 25) / 100; // lvl 2
						break;
					case 103:
						finalSoin = (target.getPDVMaxConBuff() * 30) / 100; // lvl 3
						break;
					case 104:
						finalSoin = (target.getPDVMaxConBuff() * 40) / 100; // lvl 4
						break;
					case 105:
						finalSoin = (target.getPDVMaxConBuff() * 50) / 100; // lvl 5
						break;
					case 106:
						finalSoin = (target.getPDVMaxConBuff() * 55) / 100; // lvl 6
						break;
					case 110:
						finalSoin = (target.getPDVMaxConBuff() * 25) / 100; // lvl 1 gc
						break;
					case 111:
						finalSoin = (target.getPDVMaxConBuff() * 30) / 100; // lvl 2 gc
						break;
					case 112:
						finalSoin = (target.getPDVMaxConBuff() * 35) / 100; // lvl 3 gc
						break;
					case 113:
						finalSoin = (target.getPDVMaxConBuff() * 45) / 100; // lvl 4 gc
						break;
					case 114:
						finalSoin = (target.getPDVMaxConBuff() * 60) / 100; // lvl 5 gc
						break;
					case 115:
						finalSoin = (target.getPDVMaxConBuff() * 65) / 100; // lvl 5 gc
						break;
					}
				} else {
					finalSoin = Formulas.calculFinalDommage(fight, _lanzador, target, CentroInfo.ELEMENT_FEU, dmg, true,
							false, _hechizoID, isBegin, glifo);
				}
				if ((finalSoin + target.getPDVConBuff()) > target.getPDVMaxConBuff())
				 {
					finalSoin = target.getPDVMaxConBuff() - target.getPDVConBuff();// Target va mourrir
				}
				if (finalSoin < 1) {
					finalSoin = 0;
				}
				target.restarPDV(-finalSoin);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, _lanzador.getID() + "",
						target.getID() + ",+" + finalSoin);
			}
		} else {
			for (Luchador target : cibles) {
				target.addBuff(_efectoID, _valor, _turnos, 0, true, _hechizoID, _args, _lanzador, poison, true);// on
																												// applique
																												// un
																												// buff
			}
		}
	}

	private void aplicarEfecto_109(Pelea fight, boolean glifo) {// daÑos para el lanzador (fixe)
		if (_turnos <= 0) {
			int dmg = Formulas.getRandomValor(_args.split(";")[5]);
			dmg = getMaxMinHechizo(_lanzador, dmg);
			int finalDommage = Formulas.calculFinalDommage(fight, _lanzador, _lanzador, CentroInfo.ELEMENT_NULL, dmg,
					false, false, _hechizoID, isBegin, glifo);
			if (!isBegin)
			 {
				finalDommage = aplicarBuffContraGolpe(finalDommage, _lanzador, _lanzador, fight, poison);// S'il y a des
			}
																											// buffs
																											// sp�ciaux
			if (finalDommage > _lanzador.getPDVConBuff())
			 {
				finalDommage = _lanzador.getPDVConBuff();// Caster va mourrir
			}
			_lanzador.restarPDV(finalDommage);
			finalDommage = -(finalDommage);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, _lanzador.getID() + "",
					_lanzador.getID() + "," + finalDommage);
			if (_lanzador.getPDVConBuff() <= 0) {
				fight.agregarAMuertos(_lanzador, _lanzador, "");
			}
		} else {
			_lanzador.addBuff(_efectoID, _valor, _turnos, 0, true, _hechizoID, _args, _lanzador, poison, true);// on
																												// applique
																												// un
																												// buff
		}
	}

	private void aplicarEfecto_111(ArrayList<Luchador> cibles, Pelea fight) {// + PA
		int minV = Integer.parseInt(_args.split(";")[0]);
		int maxV = Integer.parseInt(_args.split(";")[1]);
		if (maxV <= 0) {
			maxV = minV;
		}
		int val = Formulas.getRandomValor(minV, maxV);
		if (val == -1) {
			return;
		}
		for (Luchador target : cibles) {
			if ((_hechizoID == 101 && target.getEquipoBin() != _lanzador.getEquipoBin()) || (_hechizoID == 89 && target.getEquipoBin() != _lanzador.getEquipoBin())) {
				continue;
			}
			val = getMaxMinHechizo(target, val);
			target.addBuff(_efectoID, val, _turnos, 1, false, _hechizoID, _args, _lanzador, poison, true);
			if (target.puedeJugar() && target == _lanzador) {
				target.addTempPA(fight, val);
			}
			GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, _efectoID, _lanzador.getID() + "",
					target.getID() + "," + val + "," + _turnos);
		}
	}

	private void aplicarEfecto_115(ArrayList<Luchador> cibles, Pelea fight) {// + a los golpes criticos
		int val = Formulas.getRandomValor(_valores);
		if (val == -1) {
			return;
		}
		for (Luchador target : cibles) {
			val = getMaxMinHechizo(target, val);
			target.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, poison, true);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, _efectoID, _lanzador.getID() + "",
					target.getID() + "," + val + "," + _turnos);
		}
	}

	private void aplicarEfecto_116(ArrayList<Luchador> cibles, Pelea fight) {// - alcance
		int val = Formulas.getRandomValor(_valores);
		if (val == -1) {
			return;
		}
		for (Luchador target : cibles) {
			val = getMaxMinHechizo(target, val);
			target.addBuff(_efectoID, val, _turnos, 1, false, _hechizoID, _args, _lanzador, poison, true);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, _efectoID, _lanzador.getID() + "",
					target.getID() + "," + val + "," + _turnos);
		}
	}

	private void aplicarEfecto_117(ArrayList<Luchador> cibles, Pelea fight) {// + alcance
		int val = Formulas.getRandomValor(_valores);
		if (val == -1) {
			return;
		}
		for (Luchador target : cibles) {
			val = getMaxMinHechizo(target, val);
			target.addBuff(_efectoID, val, _turnos, 1, false, _hechizoID, _args, _lanzador, poison, true);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, _efectoID, _lanzador.getID() + "",
					target.getID() + "," + val + "," + _turnos);
			// Gain de PO pendant le tour de jeu
			if (target.puedeJugar() && target == _lanzador) {
				target.getTotalStatsConBuff().addUnStat(CentroInfo.STATS_ADD_ALCANCE, val);
			}
		}
	}

	private void aplicarEfecto_119(ArrayList<Luchador> cibles, Pelea fight) {// + agilidad
		int val = Formulas.getRandomValor(_valores);
		if (val == -1) {
			return;
		}
		for (Luchador target : cibles) {
			val = getMaxMinHechizo(target, val);
			target.addBuff(_efectoID, val, _turnos, 1, false, _hechizoID, _args, _lanzador, poison, true);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, _efectoID, _lanzador.getID() + "",
					target.getID() + "," + val + "," + _turnos);
		}
	}

	private void aplicarEfecto_120(Pelea fight) {// + PA segun el porcenta de 3er arg
		int minV = Integer.parseInt(_args.split(";")[0]);
		int maxV = Integer.parseInt(_args.split(";")[1]);
		if (maxV <= 0) {
			maxV = minV;
		}
		int val = Formulas.getRandomValor(minV, maxV);
		if (val == -1) {
			return;
		}
		_lanzador.addBuff(_efectoID, val, _turnos, 1, false, _hechizoID, _args, _lanzador, poison, true);
		_lanzador.addTempPA(fight, val);
		GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, _efectoID, _lanzador.getID() + "",
				_lanzador.getID() + "," + val + "," + _turnos);
		if (!Thread.interrupted()) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e1) {
			}
		}
	}

	private void aplicarEfecto_125(ArrayList<Luchador> cibles, Pelea fight) { // + vitalidad
		int val = Formulas.getRandomValor(_valores);
		if (val == -1) {
			return;
		}
		for (Luchador target : cibles) {
			val = getMaxMinHechizo(target, val);
			target.addBuff(_efectoID, val, _turnos, 1, false, _hechizoID, _args, _lanzador, poison, true);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, _efectoID, _lanzador.getID() + "",
					target.getID() + "," + val + "," + _turnos);
		}
	}

	private void aplicarEfecto_126(ArrayList<Luchador> cibles, Pelea fight) {// + Inteligencia
		int val = Formulas.getRandomValor(_valores);
		if (val == -1) {
			return;
		}
		for (Luchador target : cibles) {
			val = getMaxMinHechizo(target, val);
			target.addBuff(_efectoID, val, _turnos, 1, false, _hechizoID, _args, _lanzador, poison, true);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, _efectoID, _lanzador.getID() + "",
					target.getID() + "," + val + "," + _turnos);
		}
	}

	private void aplicarEfecto_127(ArrayList<Luchador> cibles, Pelea fight) { // PM perdidos por el blanco
		for (EfectoHechizo obj : _lanzador.getBuffPelea().keySet()) {
			if (obj.getEfectoID() == 149 && obj.getHechizoID() == 686) {
				return;
			}
		}
		int minV = Integer.parseInt(_args.split(";")[0]);
		int maxV = Integer.parseInt(_args.split(";")[1]);
		if (maxV <= 0) {
			maxV = minV;
		}
		int val = Formulas.getRandomValor(minV, maxV);
		if (val == -1) {
			return;
		}
		boolean desbuff = false;
		if (getHechizoID() == 50) {
			desbuff = true;
		}
		if (_turnos <= 0) {
			for (Luchador target : cibles) {
				int retrait = Formulas.getPuntosPerdidos('m', val, _lanzador, target);
				if ((_valor - retrait) > 0) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 309, _lanzador.getID() + "",
							target.getID() + "," + (_valor - retrait));
				}
				if (retrait > 0) {
					target.addBuff(CentroInfo.STATS_REM_PM, retrait, 1, 1, desbuff, _hechizoID, _args, _lanzador,
							poison, true);
					if (_turnos <= 1 || _duracion <= 1) {
						GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 127, target.getID() + "",
								target.getID() + ",-" + retrait);
					}
				}
			}
		} else {
			for (Luchador target : cibles) {
				int retrait = Formulas.getPuntosPerdidos('m', val, _lanzador, target);
				if ((_valor - retrait) > 0) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 309, _lanzador.getID() + "",
							target.getID() + "," + (_valor - retrait));
				}
				if (retrait > 0) {
					if (_hechizoID == 136) {
						target.addBuff(_efectoID, retrait, _turnos, _turnos, desbuff, _hechizoID, _args, _lanzador,
								poison, true);
					} else {
						target.addBuff(_efectoID, retrait, 1, 1, desbuff, _hechizoID, _args, _lanzador, poison, true);
					}
					if (_turnos <= 1 || _duracion <= 1) {
						GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 127, target.getID() + "",
								target.getID() + ",-" + retrait);
					}
				}
			}
		}
	}

	private void aplicarEfecto_128(ArrayList<Luchador> cibles, Pelea fight) {// + PMs
		int minV = Integer.parseInt(_args.split(";")[0]);
		int maxV = Integer.parseInt(_args.split(";")[1]);
		if (maxV <= 0) {
			maxV = minV;
		}
		int val = Formulas.getRandomValor(minV, maxV);
		if (val == -1) {
			return;
		}
		for (Luchador target : cibles) {
			val = getMaxMinHechizo(target, val);
			target.addBuff(_efectoID, val, _turnos, 1, false, _hechizoID, _args, _lanzador, poison, true);
			// Gain de PM pendant le tour de jeu
			if (target.puedeJugar() && target == _lanzador) {
				target.addTempPM(fight, val);
			}
			GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, _efectoID, _lanzador.getID() + "",
					target.getID() + "," + val + "," + _turnos);
		}
	}

	private void aplicarEfecto_130(ArrayList<Luchador> cibles, Pelea fight) {// robar kamas
		if (_turnos <= 0) {
			for (Luchador target : cibles) {
				if (target.esInvocacion() || target.esDoble()) {
					continue;
				}
				int kamas = Formulas.getRandomValor(_args.split(";")[5]);
				kamas = getMaxMinHechizo(target, kamas);
				if (_lanzador.getPersonaje() == null) {
					break;
				}
				if (target.getPersonaje() != null) {
					if (target.getPersonaje().getKamas() >= kamas) {
						target.getPersonaje().setKamas(kamas + target.getPersonaje().getKamas());
						_lanzador.getPersonaje().setKamas(kamas + _lanzador.getPersonaje().getKamas());
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE_A_PELEA(fight, 7,
								_lanzador.getNombreLuchador() + " ha robado " + kamas + " kamas", Colores.NARANJA);
					} else {
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE_A_PELEA(fight, 7, _lanzador.getNombreLuchador()
								+ " no puede robar kamas porque el objetivo no tiene ninguna", Colores.NARANJA);
					}
				}
			}
		} else {
			for (Luchador target : cibles) {
				target.addBuff(_efectoID, _valor, _turnos, 0, true, _hechizoID, _args, _lanzador, poison, true);// on
																												// applique
																												// un
																												// buff
			}
		}

	}

	private void aplicarEfecto_131(ArrayList<Luchador> objetivos, Pelea pelea) {// PA utlizados hacen perder X PDV
		boolean cont = true;
		for (Luchador objetivo : objetivos) {
			cont = true;
			for (EfectoHechizo objetivox : objetivo.getBuffPelea().keySet()) {
				if (objetivox.getHechizoID() == 197) {
					cont = false;
					break;
				}
			}
			if (!cont || objetivo.estaMuerto()) {
				continue;
			}
			objetivo.addBuff(_efectoID, _valor, _turnos, 1, false, _hechizoID, _args, _lanzador, poison, true);
		}
	}

	private void aplicarEfecto_132(ArrayList<Luchador> objetivos, Pelea pelea) {// desechizas
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto() || objetivo.estaRetirado()) {
				continue;
			}
			objetivo.desbuffear(_lanzador);
		}
	}

	private void aplicarEfecto_140(ArrayList<Luchador> objetivos, Pelea pelea) {// pasar turno
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto()) {
				continue;
			}
			objetivo.addBuff(_efectoID, _valor, _turnos, 0, true, _hechizoID, _args, _lanzador, poison, true);
		}
	}

	private void aplicarEfecto_405(Pelea pelea, ArrayList<Luchador> objetivos) {// matar al blanco
		int rand = 100;
		try {
			rand = Integer.parseInt(_args.split(";")[4]);
		} catch (Exception e) {
			return;
		}
		int randomv = Formulas.getRandomValor(1, 100);
		if (randomv > rand) {
			return;
		}
		boolean matado = false;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto() || (objetivo.getTransportadoPor() != null) || (objetivo.getTransportando() != null)) {
				continue;
			}
			if (objetivo.esInvocacion()) {
				if (objetivo.getInvocador() == _lanzador) {
					continue;
				}
			}
			matado = true;
			pelea.agregarAMuertos(objetivo, _lanzador, "");
			break;
		}
		if (matado) {
			int mobID = -1;
			try {
				mobID = _valor;
			} catch (Exception e) {
			}
			MobGrado mob = null;
			try {
				mob = MundoDofus.getMobModelo(mobID).getRandomGradoPrimero().getCopy();
			} catch (Exception e1) {
				return;
			}
			if (mobID == -1 || mob == null) {
				return;
			}
			Mapa map = _celdaLanz._mapa;
			if (map == null) {
				return;
			}
			int idInvocacion = pelea.getSigIDLuchador() - pelea.getNumeroInvos();
			mob.setIdEnPelea(idInvocacion);
			mob.modificarStatsPorInvocador(_lanzador);
			Luchador invocacion = new Luchador(pelea, mob);
			invocacion.setEquipoBin(_lanzador.getEquipoBin());
			invocacion.setInvocador(_lanzador);
			invocacion.setCeldaPelea(_celdaLanz);
			map.addLuchador(invocacion);
			_lanzador.aumentarInvocaciones();
			_lanzador.aumentarInvosTotales();
			pelea.agregarLuchador(_lanzador, invocacion, false);
			pelea.addLuchadorEnEquipo(invocacion, _lanzador.getEquipoBin());
			String gm = "+" + invocacion.stringGM();
			String gtl = pelea.stringOrdenJugadores();
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 181, _lanzador.getID() + "", gm);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 999, _lanzador.getID() + "", gtl);
			ArrayList<Trampa> trampas = (new ArrayList<>());
			trampas.addAll(pelea.getTrampas());
			for (Trampa trampa : trampas) {
				if (trampa.activada) {
					continue;
				}
				int dist = Camino.distanciaEntreDosCeldas(pelea.getMapaCopia(), trampa.getCelda().getID(),
						invocacion.getCeldaPelea().getID());
				if (dist <= trampa.getTamaÑo()) {
					trampa.activaTrampa(invocacion);
				}
			}
		}
	}

	private void aplicarEfecto_141(ArrayList<Luchador> objetivos, Pelea pelea) {// matar al blanco
		if (_hechizoID == 265) {
			pelea.agregarAMuertos(_lanzador, _lanzador, "");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto()) {
				continue;
			}
			if (objetivo.tieneBuff(765)) {
				if (objetivo.getBuff(765) != null && !objetivo.getBuff(765).getLanzador().estaMuerto()) {
					aplicarEfecto_765B(pelea, objetivo);
					objetivo = objetivo.getBuff(765).getLanzador();
				}
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			pelea.agregarAMuertos(objetivo, _lanzador, "");
		}
	}

	private void aplicarEfecto_143(ArrayList<Luchador> objetivos, Pelea pelea) {// pdv devueltos para
		// castigo
		if (_turnos <= 0) {
			String[] jet = _args.split(";");
			int cura = 0;
			if (jet.length < 6) {
				cura = 1;
			} else {
				cura = Formulas.getRandomValor(jet[5]);
			}
			int dmg2 = cura;
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto()) {
					continue;
				}
				cura = getMaxMinHechizo(objetivo, cura);
				int curaFinal = Formulas.calculFinalCura(_lanzador, cura, false, _hechizoID);
				if ((curaFinal + objetivo.getPDVConBuff()) > objetivo.getPDVMaxConBuff()) {
					curaFinal = objetivo.getPDVMaxConBuff() - objetivo.getPDVConBuff();
				}
				if (curaFinal < 1) {
					curaFinal = 0;
				}
				objetivo.restarPDV(-curaFinal);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 108, _lanzador.getID() + "",
						objetivo.getID() + "," + curaFinal);
				cura = dmg2;
			}
		} else {
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto()) {
					continue;
				}
				objetivo.addBuff(_efectoID, _valor, _turnos, 0, true, _hechizoID, _args, _lanzador, poison, true);
			}
		}
	}

	private void aplicarEfecto_144(ArrayList<Luchador> cibles, Pelea fight) { // - a los daÑos (no
		// boosteados)
		if (_turnos <= 0) {
			for (Luchador target : cibles) {
				if (target.tieneBuff(765))// sacrifice
				{
					if (target.getBuff(765) != null && !target.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(fight, target);
						target = target.getBuff(765).getLanzador();
					}
				}

				int dmg = Formulas.getRandomValor(_args.split(";")[5]);
				dmg = getMaxMinHechizo(target, dmg);
				// si la cible a le buff renvoie de sort et que le sort peut etre renvoyer
				if (target.tieneBuff(106) && target.getBuffValor(106) >= _nivelHechizo && _hechizoID != 0 && !isBegin) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 106, target.getID() + "", target.getID() + ",1");
					// le lanceur devient donc la cible
					target = _lanzador2;
				}
				if (!isBegin)
				 {
					dmg = aplicarBuffContraGolpe(dmg, target, _lanzador, fight, poison);// S'il y a des buffs sp�ciaux
				}
				if (target.getMob() != null) {
					if (target.getMob().getModelo().getID() == 2750) {
						int heal = (-dmg) / 2;
						if ((_lanzador.getPDVConBuff() + heal) > _lanzador.getPDVMaxConBuff()) {
							heal = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
						}
						_lanzador.restarPDV(-heal);
						GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, target.getID() + "",
								_lanzador.getID() + "," + heal);
					}
				}
				if (dmg > target.getPDVConBuff())
				 {
					dmg = target.getPDVConBuff();// Target va mourrir
				}
				target.restarPDV(dmg);
				dmg = -(dmg);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, _lanzador.getID() + "", target.getID() + "," + dmg);
				if (target.getPDVConBuff() <= 0) {
					fight.agregarAMuertos(target, _lanzador, "");
				}
			}
		} else {
			for (Luchador target : cibles) {
				target.addBuff(_efectoID, _valor, _turnos, 0, true, _hechizoID, _args, _lanzador, poison, true);// on
																												// applique
																												// un
																												// buff
			}
		}
	}

	private void aplicarEfecto_149(ArrayList<Luchador> objetivos, Pelea pelea) { // cambia la
		// apariencia
		int id = -1;
		try {
			id = Integer.parseInt(_args.split(";")[2]);
		} catch (Exception e) {
		}
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto()) {
				continue;
			}
			if (_hechizoID == 686) {
				if (objetivo.getPersonaje() != null && objetivo.getPersonaje().getSexo() == 1
						|| objetivo.getMob() != null && objetivo.getMob().getModelo().getID() == 547) {
					id = 8011;
				}
			}
			if (id == -1) {
				id = objetivo.getGfxDefecto();
			}
			objetivo.addBuff(_efectoID, id, _turnos, 1, true, _hechizoID, _args, _lanzador, poison, true);
			int defecto = objetivo.getGfxDefecto();
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, _efectoID, _lanzador.getID() + "",
					objetivo.getID() + "," + defecto + "," + id + "," + _turnos);
		}
	}

	private void aplicarEfecto_150(ArrayList<Luchador> objetivos, Pelea pelea) {// vuelve invisible al
		// personaje
		if (_turnos == 0) {
			return;
		}
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto()) {
				continue;
			}
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 150, _lanzador.getID() + "", objetivo.getID() + ",4");
			objetivo.addBuff(_efectoID, _valor, _turnos, 0, true, _hechizoID, _args, _lanzador, poison, true);
		}
	}

	private void aplicarEfecto_164(ArrayList<Luchador> objetivos, Pelea pelea) { // daÑos reducidos x
		// %
		int val = _valor;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto()) {
				continue;
			}
			objetivo.addBuff(_efectoID, val, _turnos, 1, false, _hechizoID, _args, _lanzador, poison, true);
		}
	}

	private void aplicarEfecto_165(ArrayList<Luchador> objetivos, Pelea pelea) {// aumenta los daÑos
		// en un X%
		int valor = -1;
		try {
			valor = Integer.parseInt(_args.split(";")[1]);
		} catch (Exception e) {
		}
		if (valor == -1) {
			return;
		}
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto()) {
				continue;
			}
			valor = getMaxMinHechizo(objetivo, valor);
			objetivo.addBuff(_efectoID, valor, _turnos, 1, true, _hechizoID, _args, _lanzador, poison, true);
		}
	}

	private void aplicarEfecto_168(ArrayList<Luchador> objetivos, Pelea pelea) {// - PA, no esquivables
		int minV = Integer.parseInt(_args.split(";")[0]);
		int maxV = Integer.parseInt(_args.split(";")[1]);
		if (maxV <= 0) {
			maxV = minV;
		}
		int val = Formulas.getRandomValor(minV, maxV);
		if (val == -1) {
			return;
		}
		if (_turnos <= 0) {
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto()) {
					continue;
				}
				objetivo.addBuff(_efectoID, val, 1, 1, true, _hechizoID, _args, _lanzador, poison, true);
				if (_turnos <= 1 || _duracion <= 1) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 168, objetivo.getID() + "",
							objetivo.getID() + ",-" + _valor);
				}
			}
		} else {
			boolean repetibles = false;
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto()) {
					continue;
				}
				if (_hechizoID == 197 || _hechizoID == 112) { // potencia silvestre, garra - ceangal (critico)
					objetivo.addBuff(_efectoID, val, _turnos, _turnos, true, _hechizoID, _args, _lanzador, poison,
							true);
				} else if (_hechizoID == 115) {// Olfato
					if (!repetibles) {
						short perdidosPA = (short) Formulas.getRandomValor(_valores);
						if (perdidosPA == -1) {
							continue;
						}
						_valor = perdidosPA;
					}
					objetivo.addBuff(_efectoID, val, _turnos, _turnos, true, _hechizoID, _args, _lanzador, poison,
							true);
					repetibles = true;
				} else {
					objetivo.addBuff(_efectoID, val, 1, 1, true, _hechizoID, _args, _lanzador, poison, true);
				}
				if (_turnos <= 1 || _duracion <= 1) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 168, objetivo.getID() + "",
							objetivo.getID() + ",-" + _valor);
				}
			}
		}
	}

	private void aplicarEfecto_169(ArrayList<Luchador> cibles, Pelea fight) { // - PM, no esquivables
		int minV = Integer.parseInt(_args.split(";")[0]);
		int maxV = Integer.parseInt(_args.split(";")[1]);
		if (maxV <= 0) {
			maxV = minV;
		}
		int val = Formulas.getRandomValor(minV, maxV);
		if (val == -1) {
			return;
		}
		boolean desbuff = false;
		if (_hechizoID == 686) {
			desbuff = true;
		}
		if (_turnos <= 0) {
			for (Luchador target : cibles) {
				target.addBuff(_efectoID, val, 1, 1, desbuff, _hechizoID, _args, _lanzador, poison, true);
				if (_turnos <= 1 || _duracion <= 1) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 169, target.getID() + "",
							target.getID() + ",-" + _valor);
				}
			}
		} else {
			if (cibles.isEmpty() && _hechizoID == 120 && _lanzador.getObjetivoDestZurca() != null) {
				_lanzador.getObjetivoDestZurca().addBuff(_efectoID, val, _turnos, _turnos, desbuff, _hechizoID, _args,
						_lanzador, poison, true);
				if (_turnos <= 1 || _duracion <= 1) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 169, _lanzador.getObjetivoDestZurca().getID() + "",
							_lanzador.getObjetivoDestZurca().getID() + ",-" + _valor);
				}
			}
			for (Luchador target : cibles) {
				boolean repite = false;
				if (_hechizoID == 197) {
					target.addBuff(_efectoID, val, _turnos, _turnos, desbuff, _hechizoID, _args, _lanzador, poison,
							true);
				} else {
					target.addBuff(_efectoID, val, _turnos, _turnos, desbuff, _hechizoID, _args, _lanzador, poison,
							true);
					if (_turnos <= 1 || _duracion <= 1) {
						GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 169, target.getID() + "",
								target.getID() + ",-" + val);
					}
					repite = true;
				}
				if (!repite) {
					if (_turnos <= 1 || _duracion <= 1) {
						GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 169, target.getID() + "",
								target.getID() + ",-" + val);
					}
				}
			}
		}
	}

	private void aplicarEfecto_176(ArrayList<Luchador> objetivos, Pelea pelea) { // + prospecciones
		int val = Formulas.getRandomValor(_valores);
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto()) {
				continue;
			}
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, poison, true);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, CentroInfo.STATS_ADD_PROSPECCION, _lanzador.getID() + "",
					objetivo.getID() + "," + val + "," + _turnos);
		}
	}

	private void aplicarEfecto_177(ArrayList<Luchador> objetivos, Pelea pelea) { // - prospecciones
		int val = Formulas.getRandomValor(_valores);
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto()) {
				continue;
			}
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, poison, true);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, CentroInfo.STATS_REM_PROSPECCION, _lanzador.getID() + "",
					objetivo.getID() + "," + val + "," + _turnos);
		}
	}

	private void aplicarEfecto_183(ArrayList<Luchador> cibles, Pelea fight) { // + reduccion magica
		int val = Formulas.getRandomValor(_valores);
		if (val == -1) {
			return;
		}
		for (Luchador target : cibles) {
			val = getMaxMinHechizo(target, val);
			target.addBuff(_efectoID, val, _turnos, 1, false, _hechizoID, _args, _lanzador, poison, true);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, _efectoID, _lanzador.getID() + "",
					target.getID() + "," + val + "," + _turnos);
		}
	}

	private void aplicarEfecto_184(ArrayList<Luchador> objetivos, Pelea pelea) { // + reduccion fisica
		int val = Formulas.getRandomValor(_valores);
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto()) {
				continue;
			}
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, false, _hechizoID, _args, _lanzador, poison, true);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, _efectoID, _lanzador.getID() + "",
					objetivo.getID() + "," + val + "," + _turnos);
		}
	}

	private void aplicarEfecto_185(Pelea pelea) { // invoca una criatura estatica
		short celdaID = _celdaLanz.getID();
		int mobID = -1;
		int nivel = -1;
		try {
			mobID = Integer.parseInt(_args.split(";")[0]);
			nivel = Integer.parseInt(_args.split(";")[1]);
		} catch (Exception e) {
		}
		MobGrado MG = null;
		try {
			MG = MundoDofus.getMobModelo(mobID).getGradoPorNivel(nivel).getCopy();
		} catch (Exception e1) {
			System.out.println("El Mob ID esta mal configurado: " + mobID);
			return;
		}
		if (mobID == -1 || nivel == -1 || MG == null) {
			return;
		}
		Mapa map = _celdaLanz._mapa;
		if (map == null) {
			return;
		}
		int idInvocacion = pelea.getSigIDLuchador() - pelea.getNumeroInvos();
		MG.setIdEnPelea(idInvocacion);
		MG.modificarStatsPorInvocador(_lanzador);
		Luchador invocacion = new Luchador(pelea, MG);
		int equipoLanz = _lanzador.getEquipoBin();
		invocacion.statico = true;
		invocacion.setEquipoBin(equipoLanz);
		invocacion.setInvocador(_lanzador);
		Celda nuevaCelda = pelea.getMapaCopia().getCelda(celdaID);
		invocacion.setCeldaPelea(nuevaCelda);
		map.addLuchador(invocacion);
		pelea.addLuchadorEnEquipo(invocacion, equipoLanz);
		String gm = "+" + invocacion.stringGM();
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 181, _lanzador.getID() + "", gm);
		ArrayList<Trampa> trampas = new ArrayList<>();
		trampas.addAll(pelea.getTrampas());
		Timer timer = new Timer(450, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				((Timer) e.getSource()).stop();
				for (Trampa trampa : trampas) {
					if (trampa.activada) {
						continue;
					}
					int dist = Camino.distanciaEntreDosCeldas(pelea.getMapaCopia(), trampa.getCelda().getID(), celdaID);
					if (dist <= trampa.getTamaÑo()) {
						trampa.activaTrampa(invocacion);
					}
				}
			}
		});
		timer.start();
	}

	static ArrayList<Celda> celdasarea;

	private void aplicarEfecto_202(ArrayList<Luchador> objetivos, Pelea pelea) {// revela objetos invisibles
		for (Luchador mostrar : pelea.luchadoresDeEquipo(3)) {
			if (mostrar.estaMuerto() || !mostrar.esInvisible() || !celdasarea.contains(mostrar.getCeldaPelea())) {
				continue;
			}
			if (mostrar.esInvisible()) {
				mostrar.unHide(-1);
			}
		}
		for (Trampa trampa : pelea.getTrampas()) {
			if (trampa.activada || (trampa.getParamEquipoDueÑo() == _lanzador.getParamEquipoAliado()) || !celdasarea.contains(trampa.getCelda())) {
				continue;
			}
			trampa.esVisibleParaEnemigo();
			trampa.aparecer(_lanzador.getParamEquipoAliado());
		}
		celdasarea.clear();
	}

	private void aplicarEfecto_210(ArrayList<Luchador> objetivos, Pelea pelea) {// + resist % tierra
		int val = Formulas.getRandomValor(_valores);

		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto()) {
				continue;
			}
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, poison, true);

		}
	}

	private void aplicarEfecto_211(ArrayList<Luchador> objetivos, Pelea pelea) {// + resist % agua
		int val = Formulas.getRandomValor(_valores);

		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto()) {
				continue;
			}
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, poison, true);

		}
	}

	private void aplicarEfecto_212(ArrayList<Luchador> objetivos, Pelea pelea) {// + resist % aire
		int val = Formulas.getRandomValor(_valores);
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto()) {
				continue;
			}
			// System.out.println("ef "+_efectoID+" y val "+val+" y turno "+_turnos+" y hec
			// "+_hechizoID+" y arg "+_args+" y "+poison);
			val = getMaxMinHechizo(objetivo, val);
			// objetivo.addBuff(212, 20, 25, 1, true, 7, "20;-1;-1;4;0;0d0+20",
			// _lanzador,false);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, poison, true);
			val = val2;
		}
	}

	private void aplicarEfecto_213(ArrayList<Luchador> objetivos, Pelea pelea) {// + resist % fuego
		int val = Formulas.getRandomValor(_valores);
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto()) {
				continue;
			}
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, poison, true);

		}
	}

	private void aplicarEfecto_214(ArrayList<Luchador> objetivos, Pelea pelea) {// + resist % neutral

		int val = Formulas.getRandomValor(_valores);

		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto()) {
				continue;
			}
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, poison, true);

		}
	}

	private void aplicarEfecto_215(ArrayList<Luchador> objetivos, Pelea pelea) {// + debilidad %
		// tierra
		int val = Formulas.getRandomValor(_valores);

		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto()) {
				continue;
			}
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, poison, true);

		}
	}

	private void aplicarEfecto_216(ArrayList<Luchador> objetivos, Pelea pelea) {// + debilidad % agua
		int val = Formulas.getRandomValor(_valores);

		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto()) {
				continue;
			}
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, poison, true);

		}
	}

	private void aplicarEfecto_217(ArrayList<Luchador> objetivos, Pelea pelea) {// + debilidad % aire
		int val = Formulas.getRandomValor(_valores);

		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto()) {
				continue;
			}
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, poison, true);

		}
	}

	private void aplicarEfecto_218(ArrayList<Luchador> objetivos, Pelea pelea) {// + debilidad % fuego
		int val = Formulas.getRandomValor(_valores);

		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto()) {
				continue;
			}
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, poison, true);

		}
	}

	private void aplicarEfecto_219(ArrayList<Luchador> objetivos, Pelea pelea) {// + debilidad %
		// neutral
		int val = Formulas.getRandomValor(_valores);

		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto()) {
				continue;
			}
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, poison, true);

		}
	}

	private void aplicarEfecto_248(Pelea fight, ArrayList<Luchador> cibles) {
		int val = Formulas.getRandomValor(_valores);
		if (val <= 0) {
			return;
		}
		for (Luchador target : cibles) {
			val = getMaxMinHechizo(target, val);
			target.addBuff(_efectoID, val, _turnos, 0, true, _hechizoID, _args, _lanzador, poison, true);
		}
	}

	private void aplicarEfecto_243(Pelea fight, ArrayList<Luchador> cibles) {
		int val = Formulas.getRandomValor(_valores);
		if (val == -1) {
			return;
		}
		for (Luchador target : cibles) {
			val = getMaxMinHechizo(target, val);
			target.addBuff(_efectoID, _valor, _turnos, 0, true, _hechizoID, _args, _lanzador, poison, true);
		}
	}

	private void aplicarEfecto_220(ArrayList<Luchador> objetivos, Pelea pelea) { // daÑos devueltos
		if (_turnos < 1) {
			return;
		} else {
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto()) {
					continue;
				}
				objetivo.addBuff(_efectoID, _valor, _turnos, 0, true, _hechizoID, _args, _lanzador, poison, true);
			}
		}
	}

	private void aplicarEfecto_265(ArrayList<Luchador> objetivos, Pelea pelea) { // daÑos reducidos
		// (armaduras)
		int val = Formulas.getRandomValor(_valores);
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto()) {
				continue;
			}
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, poison, true);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, _efectoID, _lanzador.getID() + "",
					objetivo.getID() + "," + val + "," + _turnos);
		}
	}

	private void aplicarEfecto_266(ArrayList<Luchador> objetivos, Pelea pelea) {// robo de suerte
		int val = Formulas.getRandomValor(_valores);
		int vol = 0;

		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto()) {
				continue;
			}
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(CentroInfo.STATS_REM_SUERTE, val, _turnos, 1, true, _hechizoID, _args, _lanzador, poison,
					true);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, CentroInfo.STATS_REM_SUERTE, _lanzador.getID() + "",
					objetivo.getID() + "," + val + "," + _turnos);
			vol += val;

		}
		if (vol == 0) {
			return;
		}
		_lanzador.addBuff(CentroInfo.STATS_ADD_SUERTE, vol, _turnos, 1, false, _hechizoID, _args, _lanzador, poison,
				true);
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, CentroInfo.STATS_ADD_SUERTE, _lanzador.getID() + "",
				_lanzador.getID() + "," + vol + "," + _turnos);

	}

	private void aplicarEfecto_267(ArrayList<Luchador> objetivos, Pelea pelea) {// robo de vitalidad
		int val = Formulas.getRandomValor(_valores);
		int vol = 0;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto()) {
				continue;
			}
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(CentroInfo.STATS_REM_VITALIDAD, val, _turnos, 1, true, _hechizoID, _args, _lanzador,
					poison, true);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, CentroInfo.STATS_REM_VITALIDAD, _lanzador.getID() + "",
					objetivo.getID() + "," + val + "," + _turnos);
			vol += val;

		}
		if (vol == 0) {
			return;
		}
		_lanzador.addBuff(CentroInfo.STATS_ADD_VITALIDAD, vol, _turnos, 1, false, _hechizoID, _args, _lanzador, poison,
				true);
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, CentroInfo.STATS_ADD_VITALIDAD, _lanzador.getID() + "",
				_lanzador.getID() + "," + vol + "," + _turnos);

	}

	private void aplicarEfecto_268(ArrayList<Luchador> objetivos, Pelea pelea) {// robo de agilidad
		int val = Formulas.getRandomValor(_valores);
		int vol = 0;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto()) {
				continue;
			}
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(CentroInfo.STATS_REM_AGILIDAD, val, _turnos, 1, true, _hechizoID, _args, _lanzador, poison,
					true);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, CentroInfo.STATS_REM_AGILIDAD, _lanzador.getID() + "",
					objetivo.getID() + "," + val + "," + _turnos);
			vol += val;
		}
		if (vol == 0) {
			return;
		}
		_lanzador.addBuff(CentroInfo.STATS_ADD_AGILIDAD, vol, _turnos, 1, false, _hechizoID, _args, _lanzador, poison,
				true);
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, CentroInfo.STATS_ADD_AGILIDAD, _lanzador.getID() + "",
				_lanzador.getID() + "," + vol + "," + _turnos);

	}

	private void aplicarEfecto_269(ArrayList<Luchador> objetivos, Pelea pelea) {// robo de
		// inteligencia
		int val = Formulas.getRandomValor(_valores);
		int vol = 0;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto()) {
				continue;
			}
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(CentroInfo.STATS_REM_INTELIGENCIA, val, _turnos, 1, true, _hechizoID, _args, _lanzador,
					poison, true);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, CentroInfo.STATS_REM_INTELIGENCIA, _lanzador.getID() + "",
					objetivo.getID() + "," + val + "," + _turnos);
			vol += val;
		}
		if (vol == 0) {
			return;
		}
		_lanzador.addBuff(CentroInfo.STATS_ADD_INTELIGENCIA, vol, _turnos, 1, false, _hechizoID, _args, _lanzador,
				poison, true);
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, CentroInfo.STATS_ADD_INTELIGENCIA, _lanzador.getID() + "",
				_lanzador.getID() + "," + vol + "," + _turnos);

	}

	private void aplicarEfecto_270(ArrayList<Luchador> objetivos, Pelea pelea) {// robo de sabiduria
		int val = Formulas.getRandomValor(_valores);
		int vol = 0;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto()) {
				continue;
			}
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(CentroInfo.STATS_REM_SABIDURIA, val, _turnos, 1, true, _hechizoID, _args, _lanzador,
					poison, true);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, CentroInfo.STATS_REM_SABIDURIA, _lanzador.getID() + "",
					objetivo.getID() + "," + val + "," + _turnos);
			vol += val;
		}
		if (vol == 0) {
			return;
		}
		_lanzador.addBuff(124, vol, _turnos, 1, false, _hechizoID, _args, _lanzador, poison, true);
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 124, _lanzador.getID() + "",
				_lanzador.getID() + "," + vol + "," + _turnos);
	}

	private void aplicarEfecto_275(ArrayList<Luchador> objetivos, Pelea pelea, boolean glifo) { // daÑos % vida
		// atacante agua
		if (_turnos <= 0) {
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto()) {
					continue;
				}
				if (objetivo.tieneBuff(106) && objetivo.getValorBuffPelea(106) >= _nivelHechizo && _hechizoID != 0) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 106, objetivo.getID() + "", objetivo.getID() + ",1");
					objetivo = _lanzador2;
				}
				if (objetivo.tieneBuff(765)) {
					if (objetivo.getBuff(765) != null && !objetivo.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(pelea, objetivo);
						objetivo = objetivo.getBuff(765).getLanzador();
					}
				}
				int daÑo = Formulas.getRandomValor(_args);
				daÑo = getMaxMinHechizo(objetivo, daÑo);
				daÑo = (daÑo * _lanzador.getPDVConBuff() / 100);
				int daÑoFinal = Formulas.calculFinalDommage(pelea, _lanzador, objetivo, 10 + CentroInfo.ELEMENTO_AGUA,
						daÑo, false, false, _hechizoID, isBegin, glifo);

				if (daÑo < 0) {
					daÑo = 0;
				}
				daÑoFinal = aplicarBuffContraGolpe(daÑoFinal, objetivo, _lanzador, pelea, poison);
				if (daÑo > objetivo.getPDVConBuff()) {
					daÑo = objetivo.getPDVConBuff();
				}
				objetivo.restarPDV(daÑo);
				int cura = daÑo;
				if (objetivo.tieneBuff(786)) {
					if ((cura + _lanzador.getPDVConBuff()) > _lanzador.getPDVMaxConBuff()) {
						cura = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
					}
					_lanzador.restarPDV(-cura);
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, objetivo.getID() + "",
							_lanzador.getID() + ",+" + cura);
				}
				daÑo = -(daÑo);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, _lanzador.getID() + "",
						objetivo.getID() + "," + daÑo);
				if (objetivo.getPDVConBuff() <= 0) {
					pelea.agregarAMuertos(objetivo, _lanzador, "hech");
				}
			}
		} else {
			poison = true;
			int val = Formulas.getRandomValor(_valores);
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto()) {
					continue;
				}
				objetivo.addBuff(_efectoID, val, _turnos, 0, true, _hechizoID, _args, _lanzador, poison, true);
			}
		}
		try {
			Thread.sleep(200);
		} catch (Exception e1) {
		}
	}

	private void aplicarEfecto_276(ArrayList<Luchador> objetivos, Pelea pelea, boolean glifo) {// daÑos % de la vida
		// del atacante
		// (tierra)
		if (_turnos <= 0) {
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto()) {
					continue;
				}
				if (objetivo.tieneBuff(106) && objetivo.getValorBuffPelea(106) >= _nivelHechizo && _hechizoID != 0) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 106, objetivo.getID() + "", objetivo.getID() + ",1");
					objetivo = _lanzador2;
				}
				if (objetivo.tieneBuff(765)) {
					if (objetivo.getBuff(765) != null && !objetivo.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(pelea, objetivo);
						objetivo = objetivo.getBuff(765).getLanzador();
					}
				}
				int daÑo = Formulas.getRandomValor(_args);
				daÑo = getMaxMinHechizo(objetivo, daÑo);
				daÑo = (daÑo * _lanzador.getPDVConBuff() / 100);
				int daÑoFinal = Formulas.calculFinalDommage(pelea, _lanzador, objetivo, 10 + CentroInfo.ELEMENTO_TIERRA,
						daÑo, false, false, _hechizoID, isBegin, glifo);

				if (daÑo < 0) {
					daÑo = 0;
				}
				daÑoFinal = aplicarBuffContraGolpe(daÑoFinal, objetivo, _lanzador, pelea, poison);
				if (daÑo > objetivo.getPDVConBuff()) {
					daÑo = objetivo.getPDVConBuff();
				}
				objetivo.restarPDV(daÑo);
				int cura = daÑo;
				if (objetivo.tieneBuff(786)) {
					if ((cura + _lanzador.getPDVConBuff()) > _lanzador.getPDVMaxConBuff()) {
						cura = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
					}
					_lanzador.restarPDV(-cura);
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, objetivo.getID() + "",
							_lanzador.getID() + ",+" + cura);
				}
				daÑo = -(daÑo);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, _lanzador.getID() + "",
						objetivo.getID() + "," + daÑo);
				if (objetivo.getPDVConBuff() <= 0) {
					pelea.agregarAMuertos(objetivo, _lanzador, "hech");
				}
			}
		} else {
			poison = true;
			int val = Formulas.getRandomValor(_valores);
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto()) {
					continue;
				}
				objetivo.addBuff(_efectoID, val, _turnos, 0, true, _hechizoID, _args, _lanzador, poison, true);
			}
		}
		if (!Thread.interrupted()) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e1) {
			}
		}
	}

	private void aplicarEfecto_277(ArrayList<Luchador> objetivos, Pelea pelea, boolean glifo) {// daÑos % de la vida
		// del atacante aire
		if (_turnos <= 0) {
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto()) {
					continue;
				}
				if (objetivo.tieneBuff(106) && objetivo.getValorBuffPelea(106) >= _nivelHechizo && _hechizoID != 0) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 106, objetivo.getID() + "", objetivo.getID() + ",1");
					objetivo = _lanzador2;
				}
				if (objetivo.tieneBuff(765)) {
					if (objetivo.getBuff(765) != null && !objetivo.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(pelea, objetivo);
						objetivo = objetivo.getBuff(765).getLanzador();
					}
				}
				int daÑo = Formulas.getRandomValor(_args);
				daÑo = getMaxMinHechizo(objetivo, daÑo);
				daÑo = (daÑo * _lanzador.getPDVConBuff() / 100);
				int daÑoFinal = Formulas.calculFinalDommage(pelea, _lanzador, objetivo, 10 + CentroInfo.ELEMENTO_AIRE,
						daÑo, false, false, _hechizoID, isBegin, glifo);

				if (daÑo < 0) {
					daÑo = 0;
				}
				daÑoFinal = aplicarBuffContraGolpe(daÑoFinal, objetivo, _lanzador, pelea, poison);
				if (daÑo > objetivo.getPDVConBuff()) {
					daÑo = objetivo.getPDVConBuff();
				}
				objetivo.restarPDV(daÑo);
				int cura = daÑo;
				if (objetivo.tieneBuff(786)) {
					if ((cura + _lanzador.getPDVConBuff()) > _lanzador.getPDVMaxConBuff()) {
						cura = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
					}
					_lanzador.restarPDV(-cura);
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, objetivo.getID() + "",
							_lanzador.getID() + ",+" + cura);
				}
				daÑo = -(daÑo);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, _lanzador.getID() + "",
						objetivo.getID() + "," + daÑo);
				if (objetivo.getPDVConBuff() <= 0) {
					pelea.agregarAMuertos(objetivo, _lanzador, "hech");
				}
			}
		} else {
			poison = true;
			int val = Formulas.getRandomValor(_valores);
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto()) {
					continue;
				}
				objetivo.addBuff(_efectoID, val, _turnos, 0, true, _hechizoID, _args, _lanzador, poison, true);
			}
		}
		if (!Thread.interrupted()) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e1) {
			}
		}
	}

	private void aplicarEfecto_278(ArrayList<Luchador> objetivos, Pelea pelea, boolean glifo) {// daÑos % de la vida
		// del atacante fuego
		if (_turnos <= 0) {
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto()) {
					continue;
				}
				if (objetivo.tieneBuff(106) && objetivo.getValorBuffPelea(106) >= _nivelHechizo && _hechizoID != 0) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 106, objetivo.getID() + "", objetivo.getID() + ",1");
					objetivo = _lanzador2;
				}
				if (objetivo.tieneBuff(765)) {
					if (objetivo.getBuff(765) != null && !objetivo.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(pelea, objetivo);
						objetivo = objetivo.getBuff(765).getLanzador();
					}
				}
				int daÑo = Formulas.getRandomValor(_args);
				daÑo = getMaxMinHechizo(objetivo, daÑo);
				daÑo = (daÑo * _lanzador.getPDVConBuff() / 100);
				int daÑoFinal = Formulas.calculFinalDommage(pelea, _lanzador, objetivo, 10 + CentroInfo.ELEMENTO_FUEGO,
						daÑo, false, false, _hechizoID, isBegin, glifo);

				if (daÑo < 0) {
					daÑo = 0;
				}
				daÑoFinal = aplicarBuffContraGolpe(daÑoFinal, objetivo, _lanzador, pelea, poison);
				if (daÑo > objetivo.getPDVConBuff()) {
					daÑo = objetivo.getPDVConBuff();
				}
				objetivo.restarPDV(daÑo);
				int cura = daÑo;
				if (objetivo.tieneBuff(786)) {
					if ((cura + _lanzador.getPDVConBuff()) > _lanzador.getPDVMaxConBuff()) {
						cura = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
					}
					_lanzador.restarPDV(-cura);
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, objetivo.getID() + "",
							_lanzador.getID() + ",+" + cura);
				}
				daÑo = -(daÑo);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, _lanzador.getID() + "",
						objetivo.getID() + "," + daÑo);
				if (objetivo.getPDVConBuff() <= 0) {
					pelea.agregarAMuertos(objetivo, _lanzador, "hech");
				}
			}
		} else {
			poison = true;
			int val = Formulas.getRandomValor(_valores);
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto()) {
					continue;
				}
				objetivo.addBuff(_efectoID, val, _turnos, 0, true, _hechizoID, _args, _lanzador, poison, true);
			}
		}
		if (!Thread.interrupted()) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e1) {
			}
		}
	}

	private void aplicarEfecto_279(ArrayList<Luchador> objetivos, Pelea pelea, boolean glifo) {// daÑos % de la vida
		// del atacante
		// neutral
		if (_turnos <= 0) {
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto()) {
					continue;
				}
				if (objetivo.tieneBuff(106) && objetivo.getValorBuffPelea(106) >= _nivelHechizo && _hechizoID != 0) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 106, objetivo.getID() + "", objetivo.getID() + ",1");
					objetivo = _lanzador2;
				}
				if (objetivo.tieneBuff(765)) {
					if (objetivo.getBuff(765) != null && !objetivo.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(pelea, objetivo);
						objetivo = objetivo.getBuff(765).getLanzador();
					}
				}
				int daÑo = Formulas.getRandomValor(_args);
				daÑo = getMaxMinHechizo(objetivo, daÑo);
				daÑo = (daÑo * _lanzador.getPDVConBuff() / 100);
				int daÑoFinal = Formulas.calculFinalDommage(pelea, _lanzador, objetivo,
						10 + CentroInfo.ELEMENTO_NEUTRAL, daÑo, false, false, _hechizoID, isBegin, glifo);

				if (daÑo < 0) {
					daÑo = 0;
				}
				daÑoFinal = aplicarBuffContraGolpe(daÑoFinal, objetivo, _lanzador, pelea, poison);

				if (daÑo > objetivo.getPDVConBuff()) {
					daÑo = objetivo.getPDVConBuff();
				}
				objetivo.restarPDV(daÑo);
				int cura = daÑo;
				if (objetivo.tieneBuff(786)) {
					if ((cura + _lanzador.getPDVConBuff()) > _lanzador.getPDVMaxConBuff()) {
						cura = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
					}
					_lanzador.restarPDV(-cura);
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, objetivo.getID() + "",
							_lanzador.getID() + ",+" + cura);
				}
				daÑo = -(daÑo);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, _lanzador.getID() + "",
						objetivo.getID() + "," + daÑo);
				if (objetivo.getPDVConBuff() <= 0) {
					pelea.agregarAMuertos(objetivo, _lanzador, "hech");
				}
			}
		} else {
			poison = true;
			int val = Formulas.getRandomValor(_valores);
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto()) {
					continue;
				}
				objetivo.addBuff(_efectoID, val, _turnos, 0, true, _hechizoID, _args, _lanzador, poison, true);
			}
		}
		if (!Thread.interrupted()) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e1) {
			}
		}
	}

	private void aplicarEfecto_271(ArrayList<Luchador> objetivos, Pelea pelea) {// robo de fuerza
		int val = Formulas.getRandomValor(_valores);
		int vol = 0;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto()) {
				continue;
			}
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(CentroInfo.STATS_REM_FUERZA, val, _turnos, 1, true, _hechizoID, _args, _lanzador, poison,
					true);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, CentroInfo.STATS_REM_FUERZA, _lanzador.getID() + "",
					objetivo.getID() + "," + val + "," + _turnos);
			vol += val;
		}
		if (vol == 0) {
			return;
		}
		_lanzador.addBuff(CentroInfo.STATS_ADD_FUERZA, vol, _turnos, 1, false, _hechizoID, _args, _lanzador, poison,
				true);
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, CentroInfo.STATS_ADD_FUERZA, _lanzador.getID() + "",
				_lanzador.getID() + "," + vol + "," + _turnos);

	}

	private void aplicarEfecto_293(Pelea pelea) {// aumenta los daÑos del hechizo X
		_lanzador.addBuff(_efectoID, _valor, _turnos, 1, false, _hechizoID, _args, _lanzador, poison, true);
		_lanzador.setEstado(300, _turnos + 1);
	}

	private void aplicarEfecto_320(ArrayList<Luchador> objetivos, Pelea pelea) { // roba alcance
		int valor = 1;
		try {
			valor = Integer.parseInt(_args.split(";")[0]);
		} catch (NumberFormatException e) {
		}
		short num = 0;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto()) {
				continue;
			}
			objetivo.addBuff(CentroInfo.STATS_REM_ALCANCE, valor, _turnos, 0, true, _hechizoID, _args, _lanzador,
					poison, true);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, CentroInfo.STATS_REM_ALCANCE, _lanzador.getID() + "",
					objetivo.getID() + "," + valor + "," + _turnos);
			num += valor;
		}
		if (num != 0) {
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, CentroInfo.STATS_ADD_ALCANCE, _lanzador.getID() + "",
					_lanzador.getID() + "," + num + "," + _turnos);
			_lanzador.addBuff(CentroInfo.STATS_ADD_ALCANCE, num, 1, 0, true, _hechizoID, _args, _lanzador, poison,
					true);
			if (_lanzador.puedeJugar()) {
				_lanzador.getTotalStatsConBuff().addUnStat(CentroInfo.STATS_ADD_ALCANCE, num);
			}
		}
	}

	private void aplicarEfecto_400(Pelea pelea) {// pone una trampa de nivel X
		if (!_celdaLanz.esCaminable(true) || (_celdaLanz._mapa.getPrimerLuchador(_celdaLanz.getID()) != null)) {
			return;
		}
		for (Trampa trampa : pelea.getTrampas()) {
			if (trampa.activada) {
				continue;
			}
			if (trampa.getCelda().getID() == _celdaLanz.getID()) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_lanzador.getPersonaje(), "Ya hay una trampa en esta celda.",
						Colores.ROJO);
				return;
			}
		}
		String[] infos = _args.split(";");
		int hechizoTrampaID = Short.parseShort(infos[0]);
		int nivel = Byte.parseByte(infos[1]);
		String po = MundoDofus.getHechizo(_hechizoID).getStatsPorNivel(_nivelHechizo).getAfectados();
		byte tamaÑo = (byte) Encriptador.getNumeroPorValorHash(po.charAt(1));
		StatsHechizos ST = MundoDofus.getHechizo(hechizoTrampaID).getStatsPorNivel(nivel);
		Trampa trampa = new Trampa(pelea, _lanzador, _celdaLanz, tamaÑo, ST, _hechizoID);
		pelea.getTrampas().add(trampa);
		int color = trampa.getColor();
		int equipo = _lanzador.getEquipoBin() + 1;
		String str = "GDZ+" + _celdaLanz.getID() + ";" + tamaÑo + ";" + color;
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, equipo, 999, _lanzador.getID() + "", str);
		str = "GDC" + _celdaLanz.getID() + ";Haaaaaaaaz3005;";
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, equipo, 999, _lanzador.getID() + "", str);
	}

	private void aplicarEfecto_401(Pelea pelea) {// pone un glifo nivel X
		if (!_celdaLanz.esCaminable(true) || (_celdaLanz._mapa.getPrimerLuchador(_celdaLanz.getID()) != null))
		 {
			return;// Si la casilla esta posicionada por un jugador
		}
		String[] infos = _args.split(";");
		int hechizoGlifoID = Short.parseShort(infos[0]);
		int nivel = Byte.parseByte(infos[1]);
		byte duracion = Byte.parseByte(infos[3]);
		String po = MundoDofus.getHechizo(_hechizoID).getStatsPorNivel(_nivelHechizo).getAfectados();
		byte tamaÑo = (byte) Encriptador.getNumeroPorValorHash(po.charAt(1));
		StatsHechizos ST = MundoDofus.getHechizo(hechizoGlifoID).getStatsPorNivel(nivel);
		Glifo glifo = new Glifo(pelea, _lanzador, _celdaLanz, tamaÑo, ST, duracion, _hechizoID);
		pelea.getGlifos().add(glifo);
		int color = glifo.getColor();
		String str = "GDZ+" + _celdaLanz.getID() + ";" + tamaÑo + ";" + color;
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 999, _lanzador.getID() + "", str);
		str = "GDC" + _celdaLanz.getID() + ";Haaaaaaaaa3005;";
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 999, _lanzador.getID() + "", str);

	}

	private void aplicarEfecto_402(Pelea pelea) {// pone un glifo nivel X
		if (!_celdaLanz.esCaminable(true)) {
			return;
		}
		String[] infos = _args.split(";");
		int hechizoGlifoID = Short.parseShort(infos[0]);
		int nivel = Byte.parseByte(infos[1]);
		byte duracion = Byte.parseByte(infos[3]);
		String po = MundoDofus.getHechizo(_hechizoID).getStatsPorNivel(_nivelHechizo).getAfectados();
		byte tamaÑo = (byte) Encriptador.getNumeroPorValorHash(po.charAt(1));
		StatsHechizos ST = MundoDofus.getHechizo(hechizoGlifoID).getStatsPorNivel(nivel);
		Glifo glifo = new Glifo(pelea, _lanzador, _celdaLanz, tamaÑo, ST, duracion, _hechizoID);
		pelea.getGlifos().add(glifo);
		int color = glifo.getColor();
		String str = "GDZ+" + _celdaLanz.getID() + ";" + tamaÑo + ";" + color;
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 999, _lanzador.getID() + "", str);
		str = "GDC" + _celdaLanz.getID() + ";Haaaaaaaaa3005;";
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 999, _lanzador.getID() + "", str);

	}

	private void aplicarEfecto_606(ArrayList<Luchador> objetivos, Pelea pelea) {// + sabiduria
		int val = Formulas.getRandomValor(_valores);

		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto()) {
				continue;
			}
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(CentroInfo.STATS_ADD_SABIDURIA, val, _turnos, 1, true, _hechizoID, _args, _lanzador,
					poison, true);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, CentroInfo.STATS_ADD_SABIDURIA, _lanzador.getID() + "",
					objetivo.getID() + "," + val + "," + _turnos);

		}
	}

	private void aplicarEfecto_607(ArrayList<Luchador> objetivos, Pelea pelea) {// + fuerza
		int val = Formulas.getRandomValor(_valores);
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto()) {
				continue;
			}
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(CentroInfo.STATS_ADD_FUERZA, val, _turnos, 1, true, _hechizoID, _args, _lanzador, poison,
					true);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, CentroInfo.STATS_ADD_FUERZA, _lanzador.getID() + "",
					objetivo.getID() + "," + val + "," + _turnos);
		}
	}

	private void aplicarEfecto_608(ArrayList<Luchador> objetivos, Pelea pelea) {// + suerte
		int val = Formulas.getRandomValor(_valores);
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto()) {
				continue;
			}
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(CentroInfo.STATS_ADD_SUERTE, val, _turnos, 1, true, _hechizoID, _args, _lanzador, poison,
					true);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, CentroInfo.STATS_ADD_SUERTE, _lanzador.getID() + "",
					objetivo.getID() + "," + val + "," + _turnos);
		}
	}

	private void aplicarEfecto_609(ArrayList<Luchador> objetivos, Pelea pelea) {// + agilidad
		int val = Formulas.getRandomValor(_valores);
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto()) {
				continue;
			}
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(CentroInfo.STATS_ADD_AGILIDAD, val, _turnos, 1, true, _hechizoID, _args, _lanzador, poison,
					true);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, CentroInfo.STATS_ADD_AGILIDAD, _lanzador.getID() + "",
					objetivo.getID() + "," + val + "," + _turnos);
		}
	}

	private void aplicarEfecto_610(ArrayList<Luchador> objetivos, Pelea pelea) {// + vitalidad
		int val = Formulas.getRandomValor(_valores);
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto()) {
				continue;
			}
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(CentroInfo.STATS_ADD_VITALIDAD, val, _turnos, 1, true, _hechizoID, _args, _lanzador,
					poison, true);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, CentroInfo.STATS_ADD_VITALIDAD, _lanzador.getID() + "",
					objetivo.getID() + "," + val + "," + _turnos);
		}
	}

	private void aplicarEfecto_611(ArrayList<Luchador> objetivos, Pelea pelea) {// + inteligencia
		int val = Formulas.getRandomValor(_valores);
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto()) {
				continue;
			}
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(CentroInfo.STATS_ADD_INTELIGENCIA, val, _turnos, 1, true, _hechizoID, _args, _lanzador,
					poison, true);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, CentroInfo.STATS_ADD_INTELIGENCIA, _lanzador.getID() + "",
					objetivo.getID() + "," + val + "," + _turnos);
		}
	}

	private void aplicarEfecto_671(ArrayList<Luchador> cibles, Pelea fight) {// daÑos % de la vida
		// del atacante
		// neutral
		if (_turnos <= 0) {
			for (Luchador target : cibles) {
				int resP = target.getTotalStatsConBuff().getEfecto(CentroInfo.STATS_ADD_ResPorc_NEUTRAL);
				int resF = target.getTotalStatsConBuff().getEfecto(CentroInfo.STATS_ADD_R_NEU);
				if (target.getPersonaje() != null)// Si c'est un joueur, on ajoute les resists bouclier
				{
					resP += target.getTotalStatsConBuff().getEfecto(CentroInfo.STATS_ADD_RP_PVP_NEUTRAL);
					resF += target.getTotalStatsConBuff().getEfecto(CentroInfo.STATS_ADD_R_PVP_NEUTRAL);
				}
				int dmg = Formulas.getRandomValor(_args.split(";")[5]);// %age de pdv inflig�
				dmg = getMaxMinHechizo(target, dmg);
				int val = _lanzador.getPDVConBuff() / 100 * dmg;// Valeur des d�gats
				// retrait de la r�sist fixe
				val -= resF;
				int reduc = (int) (((float) val) / (float) 100) * resP;// Reduc %resis
				val -= reduc;
				if (val < 0) {
					val = 0;
				}

				if (!isBegin)
				 {
					val = aplicarBuffContraGolpe(val, target, _lanzador, fight, poison);// S'il y a des buffs sp�ciaux
				}
				if (target.getMob() != null) {
					if (target.getMob().getModelo().getID() == 2750) {
						int heal = (val) / 2;
						if ((_lanzador.getPDVConBuff() + heal) > _lanzador.getPDVMaxConBuff()) {
							heal = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
						}
						_lanzador.restarPDV(-heal);
						GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, target.getID() + "",
								_lanzador.getID() + "," + heal);
					}
				}
				if (val > target.getPDVConBuff())
				 {
					val = target.getPDVConBuff();// Target va mourrir
				}
				target.restarPDV(val);
				val = -(val);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, _lanzador.getID() + "", target.getID() + "," + val);
				if (target.getPDVConBuff() <= 0) {
					fight.agregarAMuertos(target, _lanzador, "");
				}
			}
		} else {
			for (Luchador target : cibles) {
				target.addBuff(_efectoID, _valor, _turnos, 0, true, _hechizoID, _args, _lanzador, poison, true);// on
																												// applique
																												// un
																												// buff
			}
		}
	}

	private void aplicarEfecto_672(ArrayList<Luchador> cibles, Pelea fight) {// daÑos % de la vida
		// del atacante
		// (neutral)
		double val = ((double) Formulas.getRandomValor(_valores) / (double) 100);
		int pdvMax = _lanzador.getPdvMaxOutFight();
		double pVie = (double) _lanzador.getPDVConBuff() / (double) _lanzador.getPDVMaxConBuff();
		double rad = 2 * Math.PI * (pVie - 0.5);
		double cos = Math.cos(rad);
		double taux = (Math.pow((cos + 1), 2)) / 4;
		double dgtMax = val * pdvMax;
		int dgt = (int) (taux * dgtMax);

		int resfT = 0;
		int respT = 0;
		for (Luchador target : cibles) {
			resfT = target.getTotalStatsConBuff().getEfecto(CentroInfo.STATS_ADD_R_NEU);
			respT = target.getTotalStatsConBuff().getEfecto(CentroInfo.STATS_ADD_ResPorc_NEUTRAL);
			if (_lanzador.getPersonaje() != null)// Si c'est un joueur
			{
				respT += target.getTotalStatsConBuff().getEfecto(CentroInfo.STATS_ADD_RP_PVP_NEUTRAL);
				resfT += target.getTotalStatsConBuff().getEfecto(CentroInfo.STATS_ADD_R_PVP_NEUTRAL);
			}
			if (respT > 50 && target.getMob() == null) {
				respT = 50;
			}
			dgt = dgt - (respT * dgt / 100) - resfT;
			for (EfectoHechizo SE : target.getBuffsPorEfectoID(105)) {
				dgt -= SE.getValor();
			}
			if (dgt <= 0) {
				dgt = 0;
			}
			// si la cible a le buff renvoie de sort
			if (target.tieneBuff(106) && target.getBuffValor(106) >= _nivelHechizo && !isBegin) {
				GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 106, target.getID() + "", target.getID() + ",1");
				// le lanceur devient donc la cible
				target = _lanzador2;
			}
			if (target.tieneBuff(765))// sacrifice
			{
				if (target.getBuff(765) != null && !target.getBuff(765).getLanzador().estaMuerto()) {
					aplicarEfecto_765B(fight, target);
					target = target.getBuff(765).getLanzador();
				}
			}

			int finalDommage = aplicarBuffContraGolpe(dgt, target, _lanzador, fight, poison);// S'il y a des buffs
																								// sp�ciaux
			if (target.tieneBuff(105)) {
				finalDommage = finalDommage - target.getBuff(105).getValor();// Immu
				GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 105, _lanzador.getID() + "",
						target.getID() + "," + target.getBuff(105).getValor());
			}
			if (target.getMob() != null) {
				if (target.getMob().getModelo().getID() == 2750) {
					int heal = (finalDommage) / 2;
					if ((_lanzador.getPDVConBuff() + heal) > _lanzador.getPDVMaxConBuff()) {
						heal = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
					}
					_lanzador.restarPDV(-heal);
					GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, target.getID() + "",
							_lanzador.getID() + "," + heal);
				}
			}
			if (finalDommage > target.getPDVConBuff())
			 {
				finalDommage = target.getPDVConBuff();// Target va mourrir
			}
			target.restarPDV(finalDommage);
			finalDommage = -(finalDommage);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, _lanzador.getID() + "",
					target.getID() + "," + finalDommage);
			if (target.getPDVConBuff() <= 0) {
				fight.agregarAMuertos(target, _lanzador, "");
			}
		}
	}

	private void aplicarEfecto_750(ArrayList<Luchador> objetivos, Pelea pelea) {
	}

	private void aplicarEfecto_765(ArrayList<Luchador> objetivos, Pelea pelea) { // sacrifio sacrogito
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto()) {
				continue;
			}
			if (objetivo.getTransportadoPor() != null || objetivo.getTransportando() != null) {
				if (objetivo.getPersonaje() != null) {
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(objetivo.getPersonaje(),
							"El sacrificio no ha funcionado porque estÁs en estado Transportado.", Colores.ROJO);
				}
				continue;
			}
			objetivo.addBuff(_efectoID, _valor, _turnos, 1, true, _hechizoID, _args, _lanzador, poison, true);
		}
	}

	private void aplicarEfecto_765B(Pelea pelea, Luchador objetivo) {
		Luchador sacrificado = objetivo.getBuff(765).getLanzador();
		Celda cSacrificado = sacrificado.getCeldaPelea();
		Celda cObjetivo = objetivo.getCeldaPelea();
		Mapa map = pelea.getMapaCopia();
		if (map == null) {
			return;
		}
		map.removerLuchador(sacrificado);
		map.removerLuchador(objetivo);
		sacrificado.setCeldaPelea(cObjetivo);
		map.addLuchador(sacrificado);
		objetivo.setCeldaPelea(cSacrificado);
		map.addLuchador(objetivo);
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 4, objetivo.getID() + "",
				objetivo.getID() + "," + cSacrificado.getID());
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 4, sacrificado.getID() + "",
				sacrificado.getID() + "," + cObjetivo.getID());
	}

	private void aplicarEfecto_776(ArrayList<Luchador> objetivos, Pelea pelea) { // + daÑos incurables
		int val = Formulas.getRandomValor(_valores);
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto()) {
				continue;
			}
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, poison, true);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, _efectoID, _lanzador.getID() + "",
					objetivo.getID() + "," + val + "," + _turnos);
		}
	}

	private void aplicarEfecto_181(Pelea pelea) {// invocar una criatura
		int mobID = -1;
		int mobNivel = -1;
		try {
			mobID = Integer.parseInt(_args.split(";")[0]);
			mobNivel = Integer.parseInt(_args.split(";")[1]);
		} catch (Exception e) {
		}
		MobGrado mob = null;
		try {
			mob = MundoDofus.getMobModelo(mobID).getGradoPorNivel(mobNivel).getCopy();
		} catch (Exception e1) {
			try {
				mob = MundoDofus.getMobModelo(mobID).getRandomGrado().getCopy();
			} catch (Exception e2) {
				try {
					mob = MundoDofus.getMobModelo(mobID).getRandomGradoPrimero().getCopy();
				} catch (Exception e3) {
					e3.printStackTrace();
					System.out.println("El Mob ID esta mal configurado: " + mobID);
					return;
				}
			}
		}
		if (mobID == -1 || mobNivel == -1 || mob == null || _lanzador._estaMuerto) {
			return;
		}
		int idInvocacion = pelea.getSigIDLuchador() - pelea.getNumeroInvos();
		mob.setIdEnPelea(idInvocacion);
		mob.modificarStatsPorInvocador(_lanzador);
		Luchador invocacion = new Luchador(pelea, mob);
		invocacion.setEquipoBin(_lanzador.getEquipoBin());
		invocacion.setInvocador(_lanzador);
		invocacion.setCeldaPelea(_celdaLanz);
		_celdaLanz._mapa.addLuchador(invocacion);
		_lanzador.aumentarInvocaciones();
		_lanzador.aumentarInvosTotales();
		pelea.agregarLuchador(_lanzador, invocacion, false);
		pelea.addLuchadorEnEquipo(invocacion, _lanzador.getEquipoBin());
		String gm = "+" + invocacion.stringGM();
		String gtl = pelea.stringOrdenJugadores();
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 181, _lanzador.getID() + "", gm);
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 999, _lanzador.getID() + "", gtl);
		ArrayList<Trampa> trampas = (new ArrayList<>());
		trampas.addAll(pelea.getTrampas());
		for (Trampa trampa : trampas) {
			if (trampa.activada) {
				continue;
			}
			int dist = Camino.distanciaEntreDosCeldas(pelea.getMapaCopia(), trampa.getCelda().getID(),
					invocacion.getCeldaPelea().getID());
			if (dist <= trampa.getTamaÑo()) {
				trampa.activaTrampa(invocacion);
			}
		}
		pelea.tienePasar = 2500;
		pelea.timeActual = System.currentTimeMillis();
	}

	private void aplicarEfecto_180(Pelea pelea) {// invocacion de un doble
		if (_lanzador._estaMuerto) {
			return;
		}
		int idInvocacion = pelea.getSigIDLuchador();
		Personaje clon = Personaje.personajeClonado(_lanzador.getPersonaje(), idInvocacion);
		Luchador doble = new Luchador(pelea, clon);
		_lanzador.aumentarInvocaciones();
		_lanzador.aumentarInvosTotales();
		doble.setEquipoBin(_lanzador.getEquipoBin());
		doble.setInvocador(_lanzador);
		doble.setCeldaPelea(_celdaLanz);
		_celdaLanz._mapa.addLuchador(doble);
		pelea.agregarLuchador(_lanzador, doble, true);
		pelea.addLuchadorEnEquipo(doble, _lanzador.getEquipoBin());
		String gm = "+" + doble.stringGM();
		String gtl = pelea.stringOrdenJugadores();
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 180, _lanzador.getID() + "", gm);
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 999, _lanzador.getID() + "", gtl);
		ArrayList<Trampa> trampas = (new ArrayList<>());
		trampas.addAll(pelea.getTrampas());
		for (Trampa trampa : trampas) {
			if (trampa.activada) {
				continue;
			}
			int dist = Camino.distanciaEntreDosCeldas(pelea.getMapaCopia(), trampa.getCelda().getID(),
					doble.getCeldaPelea().getID());
			if (dist <= trampa.getTamaÑo()) {
				trampa.activaTrampa(doble);
			}
		}
		pelea.tienePasar = 2500;
		pelea.timeActual = System.currentTimeMillis();
	}

	private void aplicarEfecto_780(Pelea pelea) { // invoca a un aliado muerto en combate //TODO:
		ArrayList<Luchador> muertos = pelea.getListaMuertos();
		Luchador objetivo = null;
		for (Luchador entry : muertos) {
			Luchador muerto = entry;
			if (muerto.estaRetirado()) {
				continue;
			}
			if (muerto.getEquipoBin() == _lanzador.getEquipoBin()) {
				if (muerto.esInvocacion()) {
					if (muerto.getInvocador().estaMuerto() || muerto.statico) {
						continue;
					}
				}
				objetivo = muerto;
			}
		}
		if (objetivo == null) {
			if (_lanzador.getPersonaje() != null) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_lanzador.getPersonaje(), "No hay nadie para traer a la vida.",
						Colores.ROJO);
			}
			return;
		}
		if (objetivo.getPersonaje() == null) {
			_lanzador.aumentarInvocaciones();
			_lanzador.aumentarInvosTotales();
		}
		pelea.borrarUnMuerto(objetivo);
		objetivo.setMuerto(false);
		objetivo.setRetirado(false);
		objetivo.setEquipoBin(_lanzador.getEquipoBin());
		if (objetivo.getPersonaje() == null) {
			objetivo.setInvocador(_lanzador);
		}
		objetivo.setCeldaPelea(_celdaLanz);
		_celdaLanz._mapa.addLuchador(objetivo);
		pelea.agregarLuchador(_lanzador, objetivo, false);
		pelea.addLuchadorEnEquipo(objetivo, _lanzador.getEquipoBin());
		objetivo.getBuffPelea().clear();
		objetivo.setPDV(objetivo.getPDVMax());
		int vida = Math.round(((40 * objetivo.getPDV()) / 100));
		objetivo.restarPDV(vida);
		objetivo.setPDVMAX(objetivo.getPDV());
		if (objetivo.getPersonaje() != null) {
			GestorSalida.ENVIAR_As_STATS_DEL_PJ(objetivo.getPersonaje());
			GestorSalida.ENVIAR_ILF_CANTIDAD_DE_VIDA(objetivo.getPersonaje(), vida);
		}
		String gm = "+" + objetivo.stringGM();
		String gtl = pelea.stringOrdenJugadores();
		if (objetivo.getPersonaje() == null) {
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 181, _lanzador.getID() + "", gm);
		} else {
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 180, _lanzador.getID() + "", gm);
		}
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 999, _lanzador.getID() + "", gtl);
		ArrayList<Trampa> trampas = new ArrayList<>();
		trampas.addAll(pelea.getTrampas());
		for (Trampa trampa : trampas) {
			if (trampa.activada) {
				continue;
			}
			int dist = Camino.distanciaEntreDosCeldas(pelea.getMapaCopia(), trampa.getCelda().getID(),
					objetivo.getCeldaPelea().getID());
			if (dist <= trampa.getTamaÑo()) {
				trampa.activaTrampa(objetivo);
			}
		}
		if (!Thread.interrupted()) {
			try {
				Thread.sleep(500);// un tiempo de pausa de un medio de segundo
			} catch (InterruptedException e1) {
			}
		}
	}

	private void aplicarEfecto_781(ArrayList<Luchador> objetivos, Pelea pelea) {// mala sombra
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto()) {
				continue;
			}
			objetivo.addBuff(_efectoID, _valor, _turnos, 1, true, _hechizoID, _args, _lanzador, poison, true);
		}
	}

	private void aplicarEfecto_782(ArrayList<Luchador> objetivos, Pelea pelea) {// brokle
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto()) {
				continue;
			}
			objetivo.addBuff(_efectoID, _valor, _turnos, 1, true, _hechizoID, _args, _lanzador, poison, true);
		}
	}

	private void aplicarEfecto_783(Pelea pelea) {// hace retroceder hasta la casilla objetivo
		Celda celdaLanzador = _lanzador.getCeldaPelea();
		char d = Camino.getDirEntreDosCeldas(celdaLanzador.getID(), _celdaLanz.getID(), pelea.getMapaCopia(), true);
		int idSigCelda = Camino.getSigIDCeldaMismaDir(celdaLanzador.getID(), d, pelea.getMapaCopia(), true); // ES DONDE
																												// ESTA
																												// INICIALMENTE
		Celda sigCelda = pelea.getMapaCopia().getCelda(idSigCelda);
		Mapa map = pelea.getMapaCopia();
		if ((map == null) || sigCelda == null || map.getLuchadores(sigCelda.getID()).isEmpty()) {
			return;
		}
		Luchador objetivo = map.getPrimerLuchador(sigCelda.getID());
		if (objetivo.tieneEstado(6)) {
			return;
		}
		int c1 = idSigCelda;
		int c2 = 0;
		int limite = 0;
		Celda c1celda = pelea.getMapaCopia().getCelda(c1);
		Celda celda2 = null;

		ArrayList<Trampa> trampas = new ArrayList<>();
		trampas.addAll(pelea.getTrampas());
		ArrayList<Short> celdasTram = new ArrayList<>();
		for (Trampa trampa : trampas) {
			if (trampa.activada) {
				continue;
			}
			if (!celdasTram.contains(trampa.getCelda().getID())) {
				celdasTram.add(trampa.getCelda().getID());
			}
		}
		while (true) {
			if (Camino.getSigIDCeldaMismaDir(c1, d, pelea.getMapaCopia(), true) == -1) {
				return;
			}
			c2 = Camino.getSigIDCeldaMismaDir(c1, d, pelea.getMapaCopia(), true);
			celda2 = pelea.getMapaCopia().getCelda(c2);
			if (!celda2.esCaminable(false) || pelea.celdaOcupada(c2)) {
				break;
			}
			if (Camino.getSigIDCeldaMismaDir(c1, d, pelea.getMapaCopia(), true) == _celdaLanz.getID()) {
				c1 = Camino.getSigIDCeldaMismaDir(c1, d, pelea.getMapaCopia(), true);
				c1celda = pelea.getMapaCopia().getCelda(c1);
				break;
			}
			c1 = Camino.getSigIDCeldaMismaDir(c1, d, pelea.getMapaCopia(), true);
			c1celda = pelea.getMapaCopia().getCelda(c1);
			if (c1celda == null) {
				return;
			}
			if (celdasTram.contains(c1celda.getID())) {
				break;
			}
			limite++;
			if (limite > 50) {
				return;
			}
		}
		map.removerLuchador(objetivo);
		objetivo.setCeldaPelea(c1celda);
		map.addLuchador(objetivo);
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 5, _lanzador.getID() + "",
				objetivo.getID() + "," + c1celda.getID());
		if (!Thread.interrupted()) {
			try {
				Thread.sleep(300);
			} catch (Exception e) {
			}
		}
		int finalcel = c1celda.getID();
		if (limite <= 0) {
			limite = 1;
		}
		int waiteable = limite * 300;
		if (waiteable <= 0) {
			waiteable = 1;
		}
		Timer timer = new Timer(waiteable, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				((Timer) e.getSource()).stop();
				for (Trampa trampa : trampas) {
					if (trampa.activada) {
						continue;
					}
					int dist = Camino.distanciaEntreDosCeldas(pelea.getMapaCopia(), trampa.getCelda().getID(),
							finalcel);
					if (dist <= trampa.getTamaÑo()) {
						trampa.activaTrampa(objetivo);
					}
				}
			}
		});
		timer.start();
		if (!Thread.interrupted()) {
			try {
				Thread.sleep(300);
			} catch (InterruptedException e1) {
			}
		}
	}

	private void aplicarEfecto_784(ArrayList<Luchador> objetivos, Pelea pelea) {// teletransporta
		if (_turnos > 1) {
			return;
		}
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto() || objetivo.esInvocacion()) {
				continue;
			}
			Celda celda1 = null;
			for (Entry<Integer, Celda> entry : pelea.getPosInicial().entrySet()) {
				if (entry.getKey() == objetivo.getID()) {
					celda1 = entry.getValue();
					break;
				}
			}
			if (celda1.esCaminable(true) && !pelea.celdaOcupada(celda1.getID())) {
				Mapa map = pelea.getMapaCopia();
				if (map == null) {
					return;
				}
				map.removerLuchador(objetivo);
				objetivo.setCeldaPelea(celda1);
				map.addLuchador(objetivo);
				ArrayList<Trampa> trampas = (new ArrayList<>());
				trampas.addAll(pelea.getTrampas());
				for (Trampa trampa : trampas) {
					if (trampa.activada) {
						continue;
					}
					int dist = Camino.distanciaEntreDosCeldas(pelea.getMapaCopia(), trampa.getCelda().getID(),
							objetivo.getCeldaPelea().getID());
					if (dist <= trampa.getTamaÑo()) {
						trampa.activaTrampa(objetivo);
					}
				}
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 4, _lanzador.getID() + "",
						objetivo.getID() + "," + celda1.getID());
			}
		}
	}

	private void aplicarEfecto_786(ArrayList<Luchador> objetivos, Pelea pelea) {// curacion silvestre
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto()) {
				continue;
			}
			objetivo.addBuff(_efectoID, _valor, _turnos, 1, true, _hechizoID, _args, _lanzador, poison, true);
		}
	}

	private void aplicarEfecto_787(ArrayList<Luchador> objetivos, Pelea pelea) {
		int hechizoID = -1;
		int hechizoNivel = -1;
		try {
			hechizoID = Integer.parseInt(_args.split(";")[0]);
			hechizoNivel = Integer.parseInt(_args.split(";")[1]);
		} catch (Exception e) {
		}
		Hechizo hechizo = MundoDofus.getHechizo(hechizoID);
		ArrayList<EfectoHechizo> EH = hechizo.getStatsPorNivel(hechizoNivel).getEfectos();
		for (EfectoHechizo eh : EH) {
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto()) {
					continue;
				}
				if (objetivo.tieneBuff(89)) {
					return;
				}
				objetivo.addBuff(eh._efectoID, eh._valor, 1, 1, true, eh._hechizoID, eh._args, _lanzador, poison, true);
			}
		}
	}

	private void aplicarEfecto_788(ArrayList<Luchador> objetivos, Pelea pelea) { // castigos
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto()) {
				continue;
			}
			objetivo.addBuff(_efectoID, _valor, _turnos, 1, true, _hechizoID, _args, objetivo, poison, true);
		}
	}

	private void aplicarEfecto_950(ArrayList<Luchador> objetivos, Pelea pelea) {// estatdo X
		int idEstado = -1;
		try {
			idEstado = Integer.parseInt(_args.split(";")[2]);
		} catch (Exception e) {
		}
		if (idEstado == -1) {
			return;
		}
		if (_hechizoID == 59) {
			Luchador objetivo = _celdaLanz._mapa.getPrimerLuchador(_celdaLanz.getID());
			if (objetivo == null) {
				return;
			}
		}
		if (_hechizoID == 686) {
			if (_lanzador.getGfxDefecto() == 8010) {
				return;
			}
		}
		if (_hechizoID == 1103 || (_hechizoID >= 1107 && _hechizoID <= 1110)) {
			if (_turnos <= 0) {
				if (_lanzador.puedeJugar()) {
					_lanzador.setEstado(idEstado, _turnos + 1);
				} else {
					_lanzador.setEstado(idEstado, _turnos);
				}
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 950, _lanzador.getID() + "",
						_lanzador.getID() + "," + idEstado + ",1");
			} else {
				if (_lanzador.puedeJugar()) {
					_lanzador.setEstado(idEstado, _turnos + 1);
				} else {
					_lanzador.setEstado(idEstado, _turnos);
				}
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 950, _lanzador.getID() + "",
						_lanzador.getID() + "," + idEstado + ",1");
				_lanzador.addBuff(_efectoID, idEstado, _turnos, 1, true, _hechizoID, _args, _lanzador, poison, true);
			}
		} else {
			for (Luchador objetivo : objetivos) {
				if (_hechizoID == 59) {
					if (objetivo.getCeldaPelea() != _celdaLanz) {
						return;
					}
				}
				if (objetivo.estaMuerto()) {
					continue;
				}
				if (_turnos <= 0) {
					if (objetivo.puedeJugar()) {
						objetivo.setEstado(idEstado, _turnos + 1);
					} else {
						objetivo.setEstado(idEstado, _turnos);
					}
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 950, _lanzador.getID() + "",
							objetivo.getID() + "," + idEstado + ",1");
				} else {
					if (objetivo.puedeJugar()) {
						objetivo.setEstado(idEstado, _turnos + 1);
					} else {
						objetivo.setEstado(idEstado, _turnos);
					}
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 950, _lanzador.getID() + "",
							objetivo.getID() + "," + idEstado + ",1");
					objetivo.addBuff(_efectoID, idEstado, _turnos, 1, true, _hechizoID, _args, _lanzador, poison, true);
				}
			}
		}
	}

	private void aplicarEfecto_951(ArrayList<Luchador> objetivos, Pelea pelea) {// saca del estado X
		int id = -1;
		try {
			id = Integer.parseInt(_args.split(";")[2]);
		} catch (Exception e) {
		}
		if (id == -1) {
			return;
		}
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto() || !objetivo.tieneEstado(id)) {
				continue;
			}
			objetivo.setEstado(id, 0);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 950, _lanzador.getID() + "",
					objetivo.getID() + "," + id + ",0");
		}
	}
}
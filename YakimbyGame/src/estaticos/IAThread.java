
package estaticos;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;

import servidor.EntradaPersonaje.AccionDeJuego;
import variables.EfectoHechizo;
import variables.Hechizo;
import variables.Hechizo.StatsHechizos;
import variables.Mapa;
import variables.Mapa.Celda;
import variables.Pelea;
import variables.Pelea.HechizoLanzado;
import variables.Pelea.Luchador;

public class IAThread implements Runnable {
	private Luchador _atacante = null;
	private Pelea _pelea = null;
	private Thread _t;

	public IAThread(Luchador fighter, Pelea fight) {
		_atacante = fighter;
		_pelea = fight;
		_t = new Thread(this);
		_t.setDaemon(true);
		_t.start();
	}

	public Thread getThread() {
		return _t;
	}

	@Override
	public void run() {
		try {
			if (_atacante.getMob() == null) {
				if (_atacante.esDoble()) {
					tipo_5(_atacante, _pelea, 1);
				} else if (_atacante.esRecaudador()) {
					tipo_Recaudador(_atacante, _pelea);
				} else if (_atacante.esPrisma()) {
					tipo_Prisma(_atacante, _pelea);
				} else {
					_pelea.finTurno("ia4");
				}
			} else if (_atacante.getMob().getModelo() == null) {
				_pelea.finTurno("ia5");
			} else {
				switch (_atacante.getMob().getModelo().getTipoInteligencia()) {
				case 0:// no realiza nada
					tipo_0(_atacante, _pelea);
					break;
				case 1:// general
					tipo_1(_atacante, _pelea, 1);
					break;
				case 2:// esfera xelor
					tipo_2(_atacante, _pelea);
					break;
				case 3:// mobs sala de entrenamiento
						// tipo_3(_atacante, _pelea);
					break;
				case 4:// tofu,prespic
					tipo_4(_atacante, _pelea, 1);
					break;
				case 5:// bloqueadora
					tipo_5(_atacante, _pelea, 1);
					break;
				case 6:// hinchable, conejo
					tipo_6(_atacante, _pelea);
					break;
				case 7:// gatake, ataca y solo ataca
					tipo_7(_atacante, _pelea, 1);
					break;
				case 8:// mochila animada
					tipo_8(_atacante, _pelea);
					break;
				case 9:// cofre animado, arbol de la vida
					tipo_9(_atacante, _pelea);
					break;
				case 10:// cascara explosiva
					tipo_10(_atacante, _pelea);
					break;
				case 11:// chaferloko, y lancero
					tipo_11(_atacante, _pelea);
					break;
				case 12:// kralamar gigante
					tipo_12(_atacante, _pelea);
					break;
				case 13:// vasija
					tipo_13(_atacante, _pelea);
					break;
				case 14:// como 1 pero no s
					tipo_14(_atacante, _pelea);
					break;
				case 15:// como 1 pero no buffea
					tipo_15(_atacante, _pelea);
					break;
				case 16:// como 1 pero no cura
					tipo_16(_atacante, _pelea);
					break;
				case 17:// como 1 pero no cura
					tipo_17(_atacante, _pelea);
					break;
				}
			}
			if (_pelea.LucActual == _atacante) {
				if (_pelea.esperaHechizo || _pelea.esperaMove) {
					IAThread.espera(_atacante, _pelea, 1);
				} else {
					_atacante.setPuedeJugar(true);
					_pelea.finTurno("ia6");
				}
			}
		} catch (Exception e) {
			Emu.creaLogs(e);
		}
	}

	private static void espera(Luchador atacante, Pelea pelea, int bucle) {
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
		}
		if ((pelea.esperaHechizo || pelea.esperaMove) && bucle < 5) {
			IAThread.espera(atacante, pelea, bucle + 1);
		} else {
			atacante.setPuedeJugar(true);
			pelea.finTurno("ia6");
		}
	}

	private static void tipo_0(Luchador lanzador, Pelea pelea) {
		lanzador.stop = true;
		return;
	}

	private static void tipo_1(Luchador lanzador, Pelea pelea, int prim) {
		try {
			if (!lanzador.puedeJugar() || lanzador.getTempPA(pelea) <= 0 || Emu.Cerrando) {
				lanzador.stop = true;
				return;
			}
			int primero = prim;
			if (primero >= 10) {
				lanzador.stop = true;
				return;
			}
			Luchador enemigo = enemigoMasCercano(pelea, lanzador);
			int ataque = atacaSiEsPosibleTodos(pelea, lanzador, enemigo); // 10 = puede atacar, 0 = no puede atacar, 5 =
																			// fallo critico, 665 = esta lejos, no puede
																			// atacar
			if (ataque == 5 || ataque == 666) { // FC
				lanzador.stop = true;
				return;
			}
			if (ataque != 10) { // no tiene objetivo o no puede atacar a nadie
				if (enemigo == null) { // NO HAY NINGUN ENEMIGO
					if (primero == 1) {
						Map<Integer, StatsHechizos> hechiMob = lanzador.getMob().getHechizos();
						boolean infravision = false;
						StatsHechizos SHx = null;
						if (!hechiMob.isEmpty()) {
							for (Entry<Integer, StatsHechizos> SH : hechiMob.entrySet()) {
								if (infravision) {
									break;
								}
								if (SH.getValue().efectosID.contains(202)) {
									infravision = true;
									SHx = SH.getValue();
								}
							}
						}
						boolean invis = false;
						for (Luchador lx : pelea.luchadoresDeEquipo(lanzador.getParamEquipoEnemigo())) {
							if (lx.esInvisible()) {
								invis = true;
								break;
							}
						}
						if (infravision && invis) {
							int pa = SHx.getHechizo().getStatsPorNivel(SHx.getNivel()).getMaxAlc();
							ArrayList<Integer> celdasTP = Camino.getCeldas(pelea.getMapaCopia(),
									lanzador.getCeldaPelea().getID(), pa, SHx, pelea, lanzador);
							if (celdasTP.size() > 0) {
								int index = new Random().nextInt(celdasTP.size());
								int randomCelda = celdasTP.get(index);
								pelea.intentarLanzarHechizo(lanzador, SHx, randomCelda);
							}
						}
					}
					int random = Formulas.getRandomValor(1, 2);
					boolean curaciones = false;
					if (random == 2) {
						curaciones = curaSiEsPosible(pelea, lanzador, true);
						if (!curaciones) {
							curaciones = curaSiEsPosible(pelea, lanzador, false);
						}
					} else {
						curaciones = curaSiEsPosible(pelea, lanzador, false);
						if (!curaciones) {
							curaciones = curaSiEsPosible(pelea, lanzador, true);
						}
					}
					boolean buffeado = false;
					Luchador aliado = amigoMasCercano(pelea, lanzador);
					if (random == 1) {
						buffeado = buffeaSiEsPosible1(pelea, lanzador, lanzador);
						if (!buffeado) {
							buffeado = buffeaSiEsPosible1(pelea, lanzador, aliado);
						}
					} else {
						buffeado = buffeaSiEsPosible1(pelea, lanzador, aliado);
						if (!buffeado) {
							buffeado = buffeaSiEsPosible1(pelea, lanzador, lanzador);
						}
					}
					boolean moviouna = false;
					if (tieneHechizo(pelea, lanzador, lanzador.getTempPA(pelea), enemigo)) {
						tipo_1(lanzador, pelea, primero + 1);
					} else {
						if (lanzador.esInvisible()) {
							moviouna = true;
							int randompm = Formulas.getRandomValor(1, 2);
							lanzador.addTempPA(pelea, randompm);
							mueveLoMasLejosPosible(pelea, lanzador);
							lanzador.stop = true;
							return;
						} else {
							boolean invoca = invocarSiEsPosible1(pelea, lanzador);
							if (!buffeado && !curaciones && !invoca) {
								if (lanzador.esInvisible() && !moviouna) {
									int randompm = Formulas.getRandomValor(1, 2);
									lanzador.addTempPA(pelea, randompm);
									mueveLoMasLejosPosible(pelea, lanzador);
								}
								lanzador.stop = true;
								return;
							} else {
								tipo_1(lanzador, pelea, primero + 1);
							}
						}
					}
				} else { // HAY UN ENEMIGO
					if (ataque == 665) {
						enemigo = enemigoMasCercano(pelea, lanzador);
						if (lanzador.getTempPM(pelea) > 0) {
							boolean esp = true;
							char[] dirs = { 'b', 'd', 'f', 'h' };
							for (char dir : dirs) {// que no tire tp si tiene al enemigo al lado
								if (pelea.getMapaCopia() == null) {
									break;
								}
								Celda sigCelda = pelea.getMapaCopia().getCelda(Camino.getSigIDCeldaMismaDir(
										lanzador.getCeldaPelea().getID(), dir, pelea.getMapaCopia(), false));
								if (sigCelda == null) {
									continue;
								}
								Luchador luchador = pelea.getMapaCopia().getPrimerLuchador(sigCelda.getID());
								if (luchador != null) {
									if (luchador == enemigo) {
										esp = false;
										break;
									}
								}
							}
							boolean especial = false;
							if (lanzador.getPorcPDV() < 35) {
								especial = true;
							}
							if (esp) {
								acercarseA(pelea, lanzador, enemigo, esp, especial);
							}
						}
					}
					atacar(lanzador, enemigo, pelea, primero);
				}
			} else {// si puede atacar
				atacar(lanzador, enemigo, pelea, primero);
			}
		} catch (Exception e) {
			Emu.creaLogs(e);
			lanzador.stop = true;
			return;
		}
	}

	private static void atacar(Luchador lanzador, Luchador enemigo, Pelea pelea, int primero) {
		if (pelea == null) {
			lanzador.stop = true;
			return;
		}
		boolean usatp = false;
		if (primero >= 10) {
			lanzador.stop = true;
			return;
		}
		if (primero == 1) {
			usatp = true;
		}
		if (enemigo != null) {
			if (lanzador.getCeldaPelea().getID() == enemigo.getCeldaPelea().getID()) {
				usatp = false;
			}
		}
		if (lanzador.getPorcPDV() > 35) {
			int ataque = atacaSiEsPosibleTodos(pelea, lanzador, enemigo);
			if (ataque == 5 || ataque == 666) { // FC
				lanzador.stop = true;
				return;
			}
			if (ataque != 10) {// no puede atacar asi que intenta buffearse
				if (ataque == 665) {
					boolean hizoata = false;
					if (lanzador.getTempPM(pelea) > 0) {
						boolean esp = true;
						char[] dirs = { 'b', 'd', 'f', 'h' };
						for (char dir : dirs) {// que no tire tp si tiene al enemigo al lado
							if (pelea.getMapaCopia() == null) {
								break;
							}
							Celda sigCelda = pelea.getMapaCopia().getCelda(Camino.getSigIDCeldaMismaDir(
									lanzador.getCeldaPelea().getID(), dir, pelea.getMapaCopia(), false));
							if (sigCelda == null) {
								continue;
							}
							Luchador luchador = pelea.getMapaCopia().getPrimerLuchador(sigCelda.getID());
							if (luchador != null) {
								if (luchador == enemigo) {
									esp = false;
									break;
								}
							}
						}
						if (esp) {
							acercarseA(pelea, lanzador, enemigo, usatp, false);
						}
						hizoata = true;
					}
					enemigo = enemigoMasCercano(pelea, lanzador);
					if (enemigo != null && hizoata) {
						ataque = atacaSiEsPosibleTodos(pelea, lanzador, enemigo);
					}
				}
				int random = Formulas.getRandomValor(1, 2);
				boolean curaciones = false;
				if (random == 2) {
					curaciones = curaSiEsPosible(pelea, lanzador, true);
					if (!curaciones) {
						curaciones = curaSiEsPosible(pelea, lanzador, false);
					}
				} else {
					curaciones = curaSiEsPosible(pelea, lanzador, false);
					if (!curaciones) {
						curaciones = curaSiEsPosible(pelea, lanzador, true);
					}
				}
				boolean buffeado = false;
				Luchador aliado = amigoMasCercano(pelea, lanzador);
				if (random == 1) {
					buffeado = buffeaSiEsPosible1(pelea, lanzador, lanzador);
					if (!buffeado) {
						buffeado = buffeaSiEsPosible1(pelea, lanzador, aliado);
					}
				} else {
					buffeado = buffeaSiEsPosible1(pelea, lanzador, aliado);
					if (!buffeado) {
						buffeado = buffeaSiEsPosible1(pelea, lanzador, lanzador);
					}
				}
				if (tieneHechizo(pelea, lanzador, lanzador.getTempPA(pelea), enemigo)) {
					atacar(lanzador, enemigo, pelea, primero + 1);
				} else {
					if (lanzador.esInvisible()) {
						int randompm = Formulas.getRandomValor(1, 4);
						lanzador.addTempPA(pelea, randompm);
						mueveLoMasLejosPosible(pelea, lanzador);
						lanzador.stop = true;
						return;
					}
					if (lanzador.getTempPM(pelea) > 0) {
						acercarseA(pelea, lanzador, enemigo, usatp, false);
					}
					if (tieneHechizo(pelea, lanzador, lanzador.getTempPA(pelea), enemigo)) {
						atacar(lanzador, enemigo, pelea, primero + 1);
					} else {
						boolean invoca = invocarSiEsPosible1(pelea, lanzador);
						if (!buffeado && !invoca) {
							lanzador.stop = true;
							return;
						} else {
							atacar(lanzador, enemigo, pelea, primero + 1);
						}
					}
				}
			} else {
				if (lanzador.getTempPM(pelea) > 0) {
					boolean esp = true;
					char[] dirs = { 'b', 'd', 'f', 'h' };
					for (char dir : dirs) {// que no tire tp si tiene al enemigo al lado
						if (pelea.getMapaCopia() == null) {
							break;
						}
						Celda sigCelda = pelea.getMapaCopia().getCelda(Camino.getSigIDCeldaMismaDir(
								lanzador.getCeldaPelea().getID(), dir, pelea.getMapaCopia(), false));
						if (sigCelda == null) {
							continue;
						}
						Luchador luchador = pelea.getMapaCopia().getPrimerLuchador(sigCelda.getID());
						if (luchador != null) {
							if (luchador == enemigo) {
								esp = false;
								usatp = false;
							}
						}
					}
					if (esp) {
						acercarseA(pelea, lanzador, enemigo, usatp, false);
					}
				}
				atacar(lanzador, enemigo, pelea, primero + 1);
			}
		} else { // cuando tiene menos del 40% de vida
			boolean curayomismo = curaSiEsPosible(pelea, lanzador, true);
			boolean buffyomismo = buffeaSiEsPosible1(pelea, lanzador, lanzador);
			boolean alejarse = true;
			char[] dirs = { 'b', 'd', 'f', 'h' };
			for (char d : dirs) {
				if ((pelea == null) || lanzador.getCeldaPelea() == null || pelea.getMapaCopia() == null) {
					alejarse = false;
					break;
				}
				int sigCelda = Camino.getSigIDCeldaMismaDir(lanzador.getCeldaPelea().getID(), d, pelea.getMapaCopia(),
						true);
				if (pelea.getMapaCopia() == null || pelea.getMapaCopia().getCelda(sigCelda) != null) {
					break;
				}
				if (pelea.getMapaCopia().getPrimerLuchador(sigCelda) != null) {
					alejarse = false;
					break;
				}
			} // TODO:ESPECIAL
			int ataque = atacaSiEsPosibleTodos(pelea, lanzador, enemigo);
			if (ataque == 5 || ataque == 666) { // FC
				lanzador.stop = true;
				return;
			}
			if (ataque == 10) {
				atacar(lanzador, enemigo, pelea, primero + 1);
				return;
			} else {
				if (lanzador.getTempPM(pelea) > 0 && alejarse) {
					mueveLoMasLejosPosible(pelea, lanzador);
				} else {
					if (lanzador.getTempPM(pelea) > 0) {
						acercarseA(pelea, lanzador, enemigo, usatp, true);
					}
					if (tieneHechizo(pelea, lanzador, lanzador.getTempPA(pelea), enemigo)) {
						atacar(lanzador, enemigo, pelea, primero + 1);
						return;
					}
				}
			}
			if (lanzador.esInvisible()) {
				int randompm = Formulas.getRandomValor(1, 4);
				lanzador.addTempPA(pelea, randompm);
				mueveLoMasLejosPosible(pelea, lanzador);
				lanzador.stop = true;
				return;
			}
			if (!curayomismo && !buffyomismo) {
				ataque = atacaSiEsPosibleTodos(pelea, lanzador, enemigo);
				if (ataque == 5 || ataque == 666) { // FC
					lanzador.stop = true;
					return;
				}
			} else {
				atacar(lanzador, enemigo, pelea, primero + 1);
			}
		}
	}

	private static void tipo_2(Luchador lanzador, Pelea pelea) { // esfera xelor
		int veces = 0;
		while (!lanzador.stop && lanzador.puedeJugar()) {
			if (veces > 10) {
				lanzador.stop = true;
				return;
			}
			veces += 1;
			Luchador enemigo = enemigoMasCercano(pelea, lanzador);
			if (enemigo == null) {
				lanzador.stop = true;
				return;
			}
			int ataque = atacaSiEsPosible2(pelea, lanzador);
			if (ataque == 5 || ataque == 665) {
				lanzador.stop = true;
				return;
			}
		}
		return;
	}

	private static void tipo_4(Luchador lanzador, Pelea pelea, int prim) {
		if (!lanzador.puedeJugar() || lanzador.getTempPA(pelea) <= 0 && lanzador.getTempPM(pelea) <= 0) {
			lanzador.stop = true;
			return;
		}
		int primero = prim;
		if (primero >= 10) {
			lanzador.stop = true;
			return;
		}
		Luchador enemigo = enemigoMasCercano(pelea, lanzador);
		int ataque = atacaSiEsPosibleTodos(pelea, lanzador, enemigo);
		if (ataque == 5 || ataque == 666) { // FC
			lanzador.stop = true;
			return;
		}
		if (ataque == 10) {
			tipo_4(lanzador, pelea, primero + 1);
			return;
		} else {
			if (lanzador.getTempPM(pelea) > 0 && ataque == 665) {
				if (tieneHechizo(pelea, lanzador, lanzador.getTempPA(pelea), enemigo)) {
					ataque = atacaSiEsPosibleTodos(pelea, lanzador, enemigo);
				} else {
					if (prim == 1 && ataque == 665) {
						acercarseA(pelea, lanzador, enemigo, true, true);
					}
				}
			}
			if (tieneHechizo(pelea, lanzador, lanzador.getTempPA(pelea), enemigo)) {
				tipo_4(lanzador, pelea, primero + 1);
				return;
			}
			if (lanzador.getTempPM(pelea) > 0) {
				mueveLoMasLejosPosible(pelea, lanzador);
			}
		}
		int random = Formulas.getRandomValor(1, 2);
		boolean curaciones = false;
		int invocaprim = Formulas.getRandomValor(1, 2);
		boolean invoca = false;
		boolean pasoin = false;
		if (invocaprim == 1) {
			pasoin = true;
			invoca = invocarSiEsPosible1(pelea, lanzador);
		}
		if (random == 2) {
			curaciones = curaSiEsPosible(pelea, lanzador, true);
			if (!curaciones) {
				curaciones = curaSiEsPosible(pelea, lanzador, false);
			}
		} else {
			curaciones = curaSiEsPosible(pelea, lanzador, false);
			if (!curaciones) {
				curaciones = curaSiEsPosible(pelea, lanzador, true);
			}
		}
		boolean buffeado = false;
		Luchador aliado = amigoMasCercano(pelea, lanzador);
		if (random == 1) {
			buffeado = buffeaSiEsPosible1(pelea, lanzador, lanzador);
			if (!buffeado) {
				buffeado = buffeaSiEsPosible1(pelea, lanzador, aliado);
			}
		} else {
			buffeado = buffeaSiEsPosible1(pelea, lanzador, aliado);
			if (!buffeado) {
				buffeado = buffeaSiEsPosible1(pelea, lanzador, lanzador);
			}
		}
		if (lanzador.esInvisible()) {
			int randompm = Formulas.getRandomValor(1, 4);
			lanzador.addTempPA(pelea, randompm);
			mueveLoMasLejosPosible(pelea, lanzador);
			lanzador.stop = true;
			return;
		}
		if (!buffeado && !curaciones && !pasoin) {
			invoca = invocarSiEsPosible1(pelea, lanzador);
		}
		// TODO:ESPECIAL
		if (!buffeado && !curaciones && !invoca) {
			ataque = atacaSiEsPosibleTodos(pelea, lanzador, enemigo);
			if (ataque == 5 || ataque == 666) { // FC
				lanzador.stop = true;
				return;
			}
		} else {
			tipo_4(lanzador, pelea, primero + 1);
		}
	}

	private static void tipo_5(Luchador lanzador, Pelea pelea, int prim) { // IA para bloqueadora
		if (!lanzador.puedeJugar() || lanzador.getTempPA(pelea) <= 0 && lanzador.getTempPM(pelea) <= 0) {
			lanzador.stop = true;
			return;
		}
		int primero = prim;
		if (primero >= 10) {
			lanzador.stop = true;
			return;
		}
		Luchador enemigo = enemigoMasCercano(pelea, lanzador);
		boolean usatp = false;
		if (primero == 1) {
			usatp = true;
		}
		if (acercarseA(pelea, lanzador, enemigo, usatp, false)) {
			tipo_5(lanzador, pelea, prim + 1);
		} else {
			lanzador.stop = true;
			return;
		}
	}

	private static void tipo_6(Luchador lanzador, Pelea pelea) {// la hinchable, conejo
		int veces = 0;
		while (!lanzador.stop && lanzador.puedeJugar()) {
			if (veces > 10) {
				lanzador.stop = true;
				return;
			}
			veces += 1;
			Luchador amigo = amigoMasCercano(pelea, lanzador);
			if (amigo == null) {
				lanzador.stop = true;
				return;
			}
			if (!acercarseA(pelea, lanzador, amigo, false, false)) {
				if (!buffeaSiEsPosible2(pelea, lanzador, amigo)) {
					if (!buffeaSiEsPosible2(pelea, lanzador, lanzador)) {
						lanzador.stop = true;
						return;
					}
				}
			}
		}
		return;
	}

	private static void tipo_7(Luchador lanzador, Pelea pelea, int vecesx) {// gatake, pala animada, jabali
		int veces = vecesx;
		boolean usatp = false;
		Luchador enemigo = enemigoMasCercano(pelea, lanzador);
		if (enemigo == null || veces >= 10) {
			lanzador.stop = true;
			return;
		}
		int ataque = atacaSiEsPosible2(pelea, lanzador); // 10 = puede atacar, 0 = no puede atacar, 5 = fallo critico,
															// 665 = esta lejos, no puede atacar
		if (ataque == 5 || ataque == 666) { // FC
			lanzador.stop = true;
			return;
		}
		if (ataque != 10) { // no tiene objetivo o no puede atacar a nadie
			if (moverYAtacarSiEsPosible(pelea, lanzador)) {
				if (lanzador.getTempPM(pelea) > 0) {
					if (tieneHechizoSinProbar(pelea, lanzador, lanzador.getTempPA(pelea), enemigo)
							&& !tieneHechizo(pelea, lanzador, lanzador.getTempPA(pelea), enemigo)) {
						acercarseA(pelea, lanzador, enemigo, usatp, true);
					}
				}
				ataque = atacaSiEsPosible2(pelea, lanzador);
				if (ataque != 10) {
					lanzador.stop = true;
					return;
				} else {
					tipo_7(lanzador, pelea, veces + 1);
				}
			} else {
				enemigo = enemigoMasCercano(pelea, lanzador);
				if (enemigo == null) {
					lanzador.stop = true;
					return;
				}
				if (lanzador.getTempPM(pelea) > 0) {
					if (tieneHechizoSinProbar(pelea, lanzador, lanzador.getTempPA(pelea), enemigo)
							&& !tieneHechizo(pelea, lanzador, lanzador.getTempPA(pelea), enemigo)) {
						acercarseA(pelea, lanzador, enemigo, usatp, true);
					}
				}
				ataque = atacaSiEsPosible2(pelea, lanzador);
				if (veces == 1) {
					usatp = true;
				}
				if (ataque != 10) {
					if (veces == 1) {
						acercarseA(pelea, lanzador, enemigo, usatp, true);
					}
					ataque = atacaSiEsPosible2(pelea, lanzador);
					if (ataque != 10) { // no tiene objetivo o no puede atacar a nadie
						lanzador.stop = true;
						return;
					} else {
						tipo_7(lanzador, pelea, veces + 1);
					}
				} else {
					tipo_7(lanzador, pelea, veces + 1);
				}
			}
		} else {
			tipo_7(lanzador, pelea, veces + 1);
		}
	}

	private static void tipo_8(Luchador lanzador, Pelea pelea) { // mochila animada
		int veces = 0;
		while (!lanzador.stop && lanzador.puedeJugar()) {
			if (++veces >= 8) {
				lanzador.stop = true;
			}
			if (veces > 15) {
				return;
			}
			Luchador amigo = amigoMasCercano(pelea, lanzador);
			if (amigo == null) {
				return;
			}
			if (!acercarseA(pelea, lanzador, amigo, false, false)) {
				while (buffeaSiEsPosible2(pelea, lanzador, amigo)) {
				}
				lanzador.stop = true;
			}
		}
		return;
	}

	private static void tipo_9(Luchador lanzador, Pelea pelea) { // cofre animado, arbol de vida
		int veces = 0;
		while (!lanzador.stop && lanzador.puedeJugar()) {
			if (++veces >= 8) {
				lanzador.stop = true;
			}
			if (veces > 15) {
				return;
			}
			if (!buffeaSiEsPosible2(pelea, lanzador, lanzador)) {
				lanzador.stop = true;
				return;
			}
		}
	}

	private static void tipo_10(Luchador lanzador, Pelea pelea) {// cascara explosiva
		int veces = 0;
		while (!lanzador.stop && lanzador.puedeJugar()) {
			if (++veces >= 8) {
				lanzador.stop = true;
			}
			if (veces > 15) {
				return;
			}
			int ataque = atacaSiEsPosible2(pelea, lanzador);
			while (ataque == 10 && !lanzador.stop) {
				if (ataque == 5) {
					lanzador.stop = true;
				}
				ataque = atacaSiEsPosible2(pelea, lanzador);
			}
			while (buffeaSiEsPosible2(pelea, lanzador, lanzador)) {
			}
			lanzador.stop = true;
		}
		return;
	}

	private static void tipo_11(Luchador lanzador, Pelea pelea) { // chafer y chaferloko
		int veces = 0;
		while (!lanzador.stop && lanzador.puedeJugar()) {
			if (++veces >= 8) {
				lanzador.stop = true;
			}
			if (veces > 15) {
				lanzador.stop = true;
				return;
			}
			Luchador enemigo = enemigoMasCercano(pelea, lanzador); // Enemigos
			if (enemigo == null) {
				lanzador.stop = true;
				mueveLoMasLejosPosible(pelea, lanzador);
				return;
			}
			int ataque = atacaSiEsPosible3(pelea, lanzador);
			while (ataque == 10 && !lanzador.stop) {
				if (ataque == 5) {
					lanzador.stop = true;
				}
				ataque = atacaSiEsPosible3(pelea, lanzador);
			}
			while (buffeaSiEsPosible1(pelea, lanzador, lanzador)) {
			} // auto-buff
			enemigo = enemigoMasCercano(pelea, lanzador);
			if (enemigo == null) {
				mueveLoMasLejosPosible(pelea, lanzador);
				return;
			}
			if (!acercarseA(pelea, lanzador, enemigo, false, false)) {
				lanzador.stop = true;
			}
		}
		return;
	}

	private static void tipo_12(Luchador lanzador, Pelea pelea) {// kralamar
		int veces = 0;
		while (!lanzador.stop && lanzador.puedeJugar()) {
			if (++veces >= 8) {
				lanzador.stop = true;
			}
			if (veces > 15) {
				return;
			}
			int ataque = 0;
			if (!invocarSiEsPosible2(pelea, lanzador)) {
				if (!buffeaKralamar(pelea, lanzador, lanzador)) {
					ataque = atacaSiEsPosible1(pelea, lanzador);
					while (ataque == 10 && !lanzador.stop) {
						if (ataque == 5) {
							lanzador.stop = true;
						}
						ataque = atacaSiEsPosible1(pelea, lanzador);
					}
					lanzador.stop = true;
				} else {
					ataque = atacaSiEsPosible1(pelea, lanzador);
					while (ataque == 10 && !lanzador.stop) {
						if (ataque == 5) {
							lanzador.stop = true;
						}
						ataque = atacaSiEsPosible1(pelea, lanzador);
					}
					lanzador.stop = true;
				}
			}
		}
		return;
	}

	private static void tipo_13(Luchador lanzador, Pelea pelea) {// vasija
		int veces = 0;
		while (!lanzador.stop && lanzador.puedeJugar()) {
			if (++veces >= 8) {
				lanzador.stop = true;
			}
			if (veces > 15) {
				return;
			}
			if (!buffeaSiEsPosible2(pelea, lanzador, lanzador)) {// auto boost
				int ataque = atacaSiEsPosible2(pelea, lanzador);
				while (ataque == 10 && !lanzador.stop) {
					if (ataque == 5) {
						lanzador.stop = true;
					}
					ataque = atacaSiEsPosible2(pelea, lanzador);
				}
				lanzador.stop = true;
			}
		}
		return;
	}

	private static void tipo_14(Luchador lanzador, Pelea pelea) {
		int veces = 0;
		while (!lanzador.stop && lanzador.puedeJugar()) {
			if (++veces >= 8) {
				lanzador.stop = true;
			}
			if (veces > 15) {
				return;
			}
			int porcPDV = (lanzador.getPDVConBuff() * 100) / lanzador.getPDVMaxConBuff();
			Luchador enemigo = enemigoMasCercano(pelea, lanzador); // Enemigos
			Luchador amigo = amigoMasCercano(pelea, lanzador); // Amigos
			if (enemigo == null) {
				mueveLoMasLejosPosible(pelea, lanzador);
				return;
			}
			if (porcPDV > 15) {
				int ataque = atacaSiEsPosible1(pelea, lanzador);
				while (ataque == 10 && !lanzador.stop) {
					if (ataque == 5) {
						lanzador.stop = true;
					}
					ataque = atacaSiEsPosible1(pelea, lanzador);
				}
				if (!moverYAtacarSiEsPosible(pelea, lanzador)) {// se mueve y trata de atacar
					if (!buffeaSiEsPosible1(pelea, lanzador, lanzador)) {// auto-buff
						if (!curaSiEsPosible(pelea, lanzador, false)) {// cura aliada
							if (!buffeaSiEsPosible1(pelea, lanzador, amigo)) {// boost aliado
								enemigo = enemigoMasCercano(pelea, lanzador);
								if (enemigo == null) {
									mueveLoMasLejosPosible(pelea, lanzador);
									return;
								}
								if (!acercarseA(pelea, lanzador, enemigo, false, false)) {// avanzar
									lanzador.stop = true;
								}
							}
						}
					}
				} else {
					ataque = atacaSiEsPosible1(pelea, lanzador);
					while (ataque == 10 && !lanzador.stop) {
						if (ataque == 5) {
							lanzador.stop = true;
						}
						ataque = atacaSiEsPosible1(pelea, lanzador);
					}
				}
			} else {
				if (!curaSiEsPosible(pelea, lanzador, true)) {// auto-cura
					int ataque = atacaSiEsPosible1(pelea, lanzador);
					while (ataque == 10 && !lanzador.stop) {
						if (ataque == 5) {
							lanzador.stop = true;
						}
						ataque = atacaSiEsPosible1(pelea, lanzador);
					}
					if (!buffeaSiEsPosible1(pelea, lanzador, lanzador)) {// auto-buff
						if (!buffeaSiEsPosible1(pelea, lanzador, amigo)) {// buff aliados
							enemigo = enemigoMasCercano(pelea, lanzador);
							if (enemigo == null) {
								mueveLoMasLejosPosible(pelea, lanzador);
								return;
							}
							if (!acercarseA(pelea, lanzador, enemigo, false, false)) {
								lanzador.stop = true;
							}
						}
					}
				}
			}
		}
		return;
	}

	private static void tipo_15(Luchador lanzador, Pelea pelea) {
		int veces = 0;
		while (!lanzador.stop && lanzador.puedeJugar()) {
			if (++veces >= 8) {
				lanzador.stop = true;
			}
			if (veces > 15) {
				return;
			}
			int porcPDV = (lanzador.getPDVConBuff() * 100) / lanzador.getPDVMaxConBuff();
			Luchador enemigo = enemigoMasCercano(pelea, lanzador); // Enemigos
			if (enemigo == null) {
				mueveLoMasLejosPosible(pelea, lanzador);
				return;
			}
			if (porcPDV > 15) {
				int ataque = atacaSiEsPosible1(pelea, lanzador);
				while (ataque == 10 && !lanzador.stop) {
					if (ataque == 5) {
						lanzador.stop = true;
					}
					ataque = atacaSiEsPosible1(pelea, lanzador);
				}
				if (!moverYAtacarSiEsPosible(pelea, lanzador)) {// se mueve y trata de atacar
					if (!curaSiEsPosible(pelea, lanzador, false)) {// cura aliada
						enemigo = enemigoMasCercano(pelea, lanzador);
						if (enemigo == null) {
							mueveLoMasLejosPosible(pelea, lanzador);
							return;
						}
						if (!acercarseA(pelea, lanzador, enemigo, false, false)) {// avanzar
							if (!invocarSiEsPosible1(pelea, lanzador)) {// invocar
								lanzador.stop = true;
							}
						}
					}
				} else {
					ataque = atacaSiEsPosible1(pelea, lanzador);
					while (ataque == 10 && !lanzador.stop) {
						if (ataque == 5) {
							lanzador.stop = true;
						}
						ataque = atacaSiEsPosible1(pelea, lanzador);
					}
				}
			} else {
				if (!curaSiEsPosible(pelea, lanzador, true)) {// auto-cura
					int ataque = atacaSiEsPosible1(pelea, lanzador);
					while (ataque == 10 && !lanzador.stop) {
						if (ataque == 5) {
							lanzador.stop = true;
						}
						ataque = atacaSiEsPosible1(pelea, lanzador);

					}
					if (!invocarSiEsPosible1(pelea, lanzador)) {
						enemigo = enemigoMasCercano(pelea, lanzador);
						if (enemigo == null) {
							mueveLoMasLejosPosible(pelea, lanzador);
							return;
						}
						if (!acercarseA(pelea, lanzador, enemigo, false, false)) {
							lanzador.stop = true;
						}
					}
				}
			}
		}
		return;
	}

	private static void tipo_17(Luchador lanzador, Pelea pelea) {// alejarse sin parar (para evento)
		boolean esp = false;
		char[] dirs = { 'b', 'd', 'f', 'h' };
		for (char dir : dirs) {
			if (pelea.getMapaCopia() == null) {
				break;
			}
			Celda sigCelda = pelea.getMapaCopia().getCelda(
					Camino.getSigIDCeldaMismaDir(lanzador.getCeldaPelea().getID(), dir, pelea.getMapaCopia(), false));
			if (sigCelda == null) {
				continue;
			}
			Luchador luchador = pelea.getMapaCopia().getPrimerLuchador(sigCelda.getID());
			if (luchador != null) {
				esp = true;
				break;
			}
		}
		if (esp) {
			curaSiEsPosible(pelea, lanzador, true);
		}
		if (!mueveLoMasLejosPosible(pelea, lanzador)) {
			lanzador.stop = true;
			return;
		} else {
			if (!mueveLoMasLejosPosible(pelea, lanzador)) {
				lanzador.stop = true;
				return;
			}
		}
	}

	private static void tipo_16(Luchador lanzador, Pelea pelea) {
		int veces = 0;
		while (!lanzador.stop && lanzador.puedeJugar()) {
			if (++veces >= 8) {
				lanzador.stop = true;
			}
			if (veces > 15) {
				return;
			}
			Luchador enemigo = enemigoMasCercano(pelea, lanzador); // Enemigos
			Luchador amigo = amigoMasCercano(pelea, lanzador); // Amigos
			if (enemigo == null) {
				mueveLoMasLejosPosible(pelea, lanzador);
				return;
			}
			int ataque = atacaSiEsPosible1(pelea, lanzador);
			while (ataque == 10 && !lanzador.stop) {
				if (ataque == 5) {
					lanzador.stop = true;
				}
				ataque = atacaSiEsPosible1(pelea, lanzador);
			}
			if (!moverYAtacarSiEsPosible(pelea, lanzador)) {// se mueve y trata de atacar
				if (!buffeaSiEsPosible1(pelea, lanzador, lanzador)) {// auto-buff
					if (!buffeaSiEsPosible1(pelea, lanzador, amigo)) {// boost aliado
						enemigo = enemigoMasCercano(pelea, lanzador);
						if (enemigo == null) {
							mueveLoMasLejosPosible(pelea, lanzador);
							return;
						}
						if (!acercarseA(pelea, lanzador, enemigo, false, false)) {// avanzar
							if (!invocarSiEsPosible1(pelea, lanzador)) {// invocar
								lanzador.stop = true;
							}
						}
					}
				}
			} else {
				ataque = atacaSiEsPosible1(pelea, lanzador);
				while (ataque == 10 && !lanzador.stop) {
					if (ataque == 5) {
						lanzador.stop = true;
					}
					ataque = atacaSiEsPosible1(pelea, lanzador);
				}
			}
		}
		return;
	}

	private static void tipo_Prisma(Luchador lanzador, Pelea pelea) {
		int veces = 0;
		while (!lanzador.stop && lanzador.puedeJugar()) {
			if (++veces >= 8) {
				lanzador.stop = true;
			}
			if (veces > 15) {
				return;
			}
			Luchador amigo = amigoMasCercano(pelea, lanzador); // Amigos
			if (amigo != null) {
				if (!curaSiEsPosiblePrisma(pelea, lanzador, false)) {// cura aliada
					if (!buffeaSiEsPosiblePrisma(pelea, lanzador, amigo)) {// boost aliado
						if (!buffeaSiEsPosiblePrisma(pelea, lanzador, lanzador)) {// auto boost
							int ataque = atacaSiEsPosiblePrisma(pelea, lanzador);
							while (ataque == 0 && !lanzador.stop) {
								if (ataque == 5) {
									lanzador.stop = true;
								}
								ataque = atacaSiEsPosiblePrisma(pelea, lanzador);
							}
							lanzador.stop = true;
						}
					}
				}
			} else {
				int ataque = atacaSiEsPosiblePrisma(pelea, lanzador);
				while (ataque == 0 && !lanzador.stop) {
					if (ataque == 5) {
						lanzador.stop = true;
					}
					ataque = atacaSiEsPosiblePrisma(pelea, lanzador);
				}
				lanzador.stop = true;
			}
		}
		return;
	}

	private static void tipo_Recaudador(Luchador lanzador, Pelea pelea) { // IA propia del recaudador
		int veces = 0;
		while (!lanzador.stop && lanzador.puedeJugar()) {
			if (++veces >= 8) {
				lanzador.stop = true;
			}
			if (veces > 15) {
				return;
			}
			int porcPDV = (lanzador.getPDVConBuff() * 100) / lanzador.getPDVMaxConBuff();
			Luchador amigo = amigoMasCercano(pelea, lanzador);
			Luchador enemigo = enemigoMasCercano(pelea, lanzador);
			if (porcPDV > 15) {
				int ataque = atacaSiEsPosibleRecau(pelea, lanzador);
				while (ataque == 0 && !lanzador.stop) {
					if (ataque == 5) {
						lanzador.stop = true;
					}
					ataque = atacaSiEsPosibleRecau(pelea, lanzador);
				}
				if (!curaSiEsPosibleRecau(pelea, lanzador, false)) {
					if (!buffeaSiEsPosibleRecau(pelea, lanzador, amigo)) {
						enemigo = enemigoMasCercano(pelea, lanzador);
						if (enemigo == null) {
							mueveLoMasLejosPosible(pelea, lanzador);
							return;
						}
						if (!acercarseA(pelea, lanzador, enemigo, false, false)) {
							lanzador.stop = true;
						}
					}
				}
			} else {
				if (!curaSiEsPosibleRecau(pelea, lanzador, true)) {
					int ataque = atacaSiEsPosibleRecau(pelea, lanzador);
					while (ataque == 0 && !lanzador.stop) {
						if (ataque == 5) {
							lanzador.stop = true;
						}
						ataque = atacaSiEsPosibleRecau(pelea, lanzador);
					}
					if (!mueveLoMasLejosPosible(pelea, lanzador)) {
						lanzador.stop = true;
					}
				}
			}
		}
		return;
	}

	private static boolean mueveLoMasLejosPosible(Pelea pelea, Luchador lanzador) {
		if (lanzador.getTempPM(pelea) <= 0) {
			return false;
		}
		Mapa mapa = pelea.getMapaCopia();
		if (mapa == null) {
			return false;
		}
		ArrayList<Integer> celdasMove = Camino.getCeldas(mapa, lanzador.getCeldaPelea().getID(),
				lanzador.getTempPM(pelea), null, pelea, lanzador);
		int celdaIDLanzador = lanzador.getCeldaPelea().getID();
		ArrayList<Luchador> enemigos = new ArrayList<>();
		for (Luchador blanco : pelea.luchadoresDeEquipo(lanzador.getParamEquipoEnemigo())) {
			if (blanco.estaMuerto() || blanco.estaRetirado() || blanco.esInvisible()) {
				continue;
			}
			enemigos.add(blanco);
		}
		int distEntreTodos = -1;
		int celdaIdeal = -1;
		for (Integer celdaTemp : celdasMove) {
			int distTemp = 0;
			for (Luchador blanco : enemigos) {
				distTemp += Camino.distanciaEntreDosCeldas(mapa, celdaTemp, blanco.getCeldaPelea().getID());
			}
			if (distTemp >= distEntreTodos) {
				distEntreTodos = distTemp;
				celdaIdeal = celdaTemp;
			}
		}
		if (celdaIdeal == -1 || celdaIdeal == celdaIDLanzador) {
			return false;
		}
		boolean resultado = moverseA(pelea, lanzador, celdaIdeal);
		if (lanzador.getMob() != null && !lanzador.usoTP) {
			if (lanzador.getMob().getHechizos().size() > 0) {
				for (Entry<Integer, StatsHechizos> SH : lanzador.getMob().getHechizos().entrySet()) {
					if (mapa == null || lanzador.usoTP) {
						break;
					}
					if (SH == null) {
						continue;
					}
					int pa = SH.getValue().getHechizo().getStatsPorNivel(SH.getValue().getNivel()).getMaxAlc();
					ArrayList<Integer> celdasTP = Camino.getCeldas(mapa, lanzador.getCeldaPelea().getID(), pa,
							SH.getValue(), pelea, lanzador);
					int celdamasC = Camino.celdaLejana(celdasTP, lanzador.getCeldaPelea().getID(), mapa);
					if (SH.getValue().efectosID.contains(4)) {
						if (celdamasC != -1) {
							pelea.intentarLanzarHechizo(lanzador, SH.getValue(), celdamasC);
							lanzador.usoTP = true;
							break;
						}
					}
				}
			}
		}
		return resultado;
	}

	private static boolean moverseA(Pelea pelea, Luchador lanzador, int celdaobj) {
		Mapa mapa = pelea.getMapaCopia();
		if (lanzador.getTempPM(pelea) <= 0) {
			return false;
		}
		int celdaID = celdaobj;
		if (celdaID == -1) {
			ArrayList<Luchador> enemigos = listaEnemigosMenosPDV(pelea, lanzador);
			for (Luchador enemigo : enemigos) {
				int celdaID2 = Camino.getCeldaMasCercanaAlrededor(mapa, enemigo.getCeldaPelea().getID(),
						lanzador.getCeldaPelea().getID(), null);
				if (celdaID2 != -1) {
					celdaID = celdaID2;
					break;
				}
			}
		}
		ArrayList<Celda> path = new AstarPathfinding(mapa, pelea, lanzador.getCeldaPelea().getID(), celdaID)
				.getShortestPath(-1);
		if (path == null) {
			return false;
		}
		ArrayList<Celda> finalPath = new ArrayList<>();
		for (int a = 0; a < lanzador.getTempPM(pelea); a++) {
			if (path.size() == a) {
				break;
			}
			finalPath.add(path.get(a));
		}
		StringBuilder pathstr = new StringBuilder("");
		try {
			int tempCeldaID = lanzador.getCeldaPelea().getID();
			int tempDir = 0;
			for (Celda c : finalPath) {
				char d = Camino.getDirEntreDosCeldas(tempCeldaID, c.getID(), mapa, true);
				if (d == 0) {
					return false;
				}
				if (tempDir != d) {
					if (finalPath.indexOf(c) != 0) {
						pathstr.append(Encriptador.celdaIDACodigo(tempCeldaID));
					}
					pathstr.append(d);
				}
				tempCeldaID = c.getID();
			}
			if (tempCeldaID != lanzador.getCeldaPelea().getID()) {
				pathstr.append(Encriptador.celdaIDACodigo(tempCeldaID));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		AccionDeJuego GA = new AccionDeJuego(0, 1, "");
		GA._args = pathstr.toString();
		boolean resultado = pelea.puedeMoverseLuchador(lanzador, GA, "ia1");
		return resultado;
	}

	private static boolean acercarseA(Pelea pelea, Luchador lanzador, Luchador objetivo, boolean usatp,
			boolean especial) {
		if (pelea == null) {
			return false;
		}
		Mapa mapa = pelea.getMapaCopia();
		if (lanzador.atacoAOtro != null) {
			if (lanzador.atacoAOtro.estaMuerto() || lanzador.atacoAOtro.esInvisible()
					|| lanzador.atacoAOtro.estaRetirado()) {
				lanzador.atacoAOtro = null;
			}
			ArrayList<Luchador> enemigosAlrededor = Camino.getEnemigosAlrededor(lanzador, pelea.getMapaCopia(), pelea);
			if (enemigosAlrededor != null) {
				if (!enemigosAlrededor.contains(lanzador.atacoAOtro)) {
					objetivo = enemigoMasCercano(pelea, lanzador, lanzador.atacoAOtro);
					if (objetivo != null) {
						lanzador.atacoAOtro = null;
					}
				}
			} else {
				objetivo = enemigoMasCercano(pelea, lanzador, lanzador.atacoAOtro);
				if (objetivo != null) {
					lanzador.atacoAOtro = null;
				}
			}
		}
		if (usatp) {
			if (objetivo == null || objetivo.estaMuerto()) {
				objetivo = enemigoMasCercano(pelea, lanzador);
			}
			if (lanzador.getMob() != null && !lanzador.usoTP) {
				if (lanzador.getMob().getHechizos().size() > 0) {
					for (Entry<Integer, StatsHechizos> SH : lanzador.getMob().getHechizos().entrySet()) {
						if (mapa == null || lanzador.usoTP || objetivo == null) {
							break;
						}
						if (SH == null) {
							continue;
						}
						int pa = SH.getValue().getHechizo().getStatsPorNivel(SH.getValue().getNivel()).getMaxAlc();
						ArrayList<Integer> celdasTP = Camino.getCeldas(mapa, lanzador.getCeldaPelea().getID(), pa,
								SH.getValue(), pelea, lanzador);
						int celdamasC = Camino.celdaCercanas(celdasTP, objetivo.getCeldaPelea().getID(), mapa);
						if (SH.getValue().efectosID.contains(4)) {
							if (celdamasC != -1) {
								pelea.intentarLanzarHechizo(lanzador, SH.getValue(), celdamasC);
								lanzador.usoTP = true;
								break;
							}
						}
					}
				}
			}
		}
		if (lanzador.getTempPM(pelea) <= 0) {
			return false;
		}
		if (objetivo == null || objetivo.estaMuerto()) {
			objetivo = enemigoMasCercano(pelea, lanzador);
		}
		if (objetivo == null) {
			return false;
		}
		int celdaID = -1;
		int lostempac = lanzador.getTempPM(pelea);
		int maxdemax = 0;
		if (especial) {
			if (lanzador.getMob() != null) {
				if (lanzador.getMob().getHechizos().size() > 0) {
					for (Entry<Integer, StatsHechizos> SH : lanzador.getMob().getHechizos().entrySet()) {
						if (SH == null) {
							continue;
						}
						int maxalc = SH.getValue().getHechizo().getStatsPorNivel(SH.getValue().getNivel()).getMaxAlc();// tengo
																														// 6
																														// pm
																														// y
																														// maximi
																														// alcance
																														// 5
						if (maxdemax < maxalc) {// 0 9
							maxdemax = maxalc;// 5
						}
					}
				}
			} // 6 pa - 5 dealcance = me tengo que mover 1
			int distanci = Camino.distanciaEntreDosCeldas(mapa, objetivo.getCeldaPelea().getID(),
					lanzador.getCeldaPelea().getID()); // esta a 11 y lanzo desde 9
			if (distanci < maxdemax) {
				distanci = maxdemax;
			} else {
				distanci = distanci - maxdemax;
			}
			if (distanci > lostempac) {
				distanci = lostempac;
			}
			ArrayList<Integer> celdasTP = Camino.getCeldas(mapa, lanzador.getCeldaPelea().getID(), distanci, null,
					pelea, lanzador);
			int celdamasC = Camino.celdaCercanas(celdasTP, objetivo.getCeldaPelea().getID(), mapa);
			celdaID = celdamasC;
		} else {
			try {
				if (Camino.esSiguienteA(lanzador.getCeldaPelea().getID(), objetivo.getCeldaPelea().getID(), mapa)) {
					return false;
				}
				celdaID = Camino.getCeldaMasCercanaAlrededor(mapa, objetivo.getCeldaPelea().getID(),
						lanzador.getCeldaPelea().getID(), null);
			} catch (Exception e) {
				return false;
			}
		}
		if (celdaID == -1) {
			ArrayList<Luchador> enemigos = listaEnemigosMenosPDV(pelea, lanzador);
			for (Luchador enemigo : enemigos) {
				int celdaID2 = Camino.getCeldaMasCercanaAlrededor(mapa, enemigo.getCeldaPelea().getID(),
						lanzador.getCeldaPelea().getID(), null);
				if (celdaID2 != -1) {
					celdaID = celdaID2;
					break;
				}
			}
		}
		ArrayList<Celda> path = new AstarPathfinding(mapa, pelea, lanzador.getCeldaPelea().getID(), celdaID)
				.getShortestPath(-1);
		if (path == null) {
			return false;
		}
		ArrayList<Celda> finalPath = new ArrayList<>();
		for (int a = 0; a < lostempac; a++) {
			if (path.size() == a) {
				break;
			}
			finalPath.add(path.get(a));
		}
		StringBuilder pathstr = new StringBuilder("");
		try {
			int tempCeldaID = lanzador.getCeldaPelea().getID();
			int tempDir = 0;
			for (Celda c : finalPath) {
				char d = Camino.getDirEntreDosCeldas(tempCeldaID, c.getID(), mapa, true);
				if (d == 0) {
					return false;
				}
				if (tempDir != d) {
					if (finalPath.indexOf(c) != 0) {
						pathstr.append(Encriptador.celdaIDACodigo(tempCeldaID));
					}
					pathstr.append(d);
				}
				tempCeldaID = c.getID();
			}
			if (tempCeldaID != lanzador.getCeldaPelea().getID()) {
				pathstr.append(Encriptador.celdaIDACodigo(tempCeldaID));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		AccionDeJuego GA = new AccionDeJuego(0, 1, "");
		GA._args = pathstr.toString();
		boolean resultado = pelea.puedeMoverseLuchador(lanzador, GA, "ia1");
		return resultado;
	}

	private static boolean invocarSiEsPosible1(Pelea pelea, Luchador invocador) {
		if (invocador.getNroInvocaciones() >= invocador.getTotalStatsConBuff().getEfecto(182)) {
			return false;
		}
		Luchador enemigoCercano = enemigoMasCercano(pelea, invocador);
		if (enemigoCercano == null) {
			return false;
		}
		int celdaMasCercana = Camino.getCeldaMasCercanaAlrededor(pelea.getMapaCopia(),
				invocador.getCeldaPelea().getID(), enemigoCercano.getCeldaPelea().getID(), null);
		if (celdaMasCercana == -1) {
			return false;
		}
		StatsHechizos hechizo = hechizoInvocacion(pelea, invocador, celdaMasCercana);
		if (hechizo == null) {
			return false;
		}
		int invoc = pelea.intentarLanzarHechizo(invocador, hechizo, celdaMasCercana);
		if (invoc != 0) {
			return false;
		}
		return true;
	}

	private static boolean invocarSiEsPosible2(Pelea pelea, Luchador invocador) {
		if (invocador.getNroInvocaciones() >= invocador.getTotalStatsConBuff().getEfecto(182)) {
			return false;
		}
		Luchador enemigoCercano = enemigoMasCercano(pelea, invocador);
		if (enemigoCercano == null) {
			return false;
		}
		int invoc = hechizoInvocacion2(pelea, invocador, enemigoCercano);
		if (invoc != 0) {
			return false;
		}
		return true;
	}

	private static StatsHechizos hechizoInvocacion(Pelea pelea, Luchador invocador, int celdaCercana) {
		if (invocador.getMob() == null) {
			return null;
		}
		for (Entry<Integer, StatsHechizos> SH : invocador.getMob().getHechizos().entrySet()) {
			if ((SH.getValue().getHechizo()._tipo != 2) || !pelea.puedeLanzarHechizo(invocador, SH.getValue(), pelea.getMapaCopia().getCelda(celdaCercana), -1,
					true, true)) {
				continue;
			}
			return SH.getValue();
		}
		return null;
	}

	private static int hechizoInvocacion2(Pelea pelea, Luchador invocador, Luchador enemigoCercano) {
		if (invocador.getMob() == null) {
			return 5;
		}
		ArrayList<StatsHechizos> hechizos = new ArrayList<>();
		StatsHechizos SH = null;
		int celdaMasCercana = -1;
		try {
			boolean paso = false;
			for (Entry<Integer, StatsHechizos> SS : invocador.getMob().getHechizos().entrySet()) {
				if (paso) {
					break;
				}
				StatsHechizos hechi = SS.getValue();
				if (hechi.efectosID.contains(181) || hechi.efectosID.contains(185)) {
					celdaMasCercana = Camino.getCeldaMasCercanaAlrededor2(pelea.getMapaCopia(),
							invocador.getCeldaPelea().getID(), enemigoCercano.getCeldaPelea().getID(),
							hechi.getMinAlc(), hechi.getMaxAlc());
					if ((celdaMasCercana == -1) || !pelea.puedeLanzarHechizo(invocador, hechi, pelea.getMapaCopia().getCelda(celdaMasCercana), -1,
							true, true)) {
						continue;
					}
					hechizos.add(hechi);
					paso = true;
				}
			}
		} catch (Exception e) {
			return 5;
		}
		if (hechizos.size() <= 0) {
			return 5;
		}
		if (hechizos.size() == 1) {
			SH = hechizos.get(0);
		} else {
			SH = hechizos.get(Formulas.getRandomValor(0, hechizos.size() - 1));
		}
		int invoca = pelea.intentarLanzarHechizo(invocador, SH, celdaMasCercana);
		return invoca;
	}

	private static boolean curaSiEsPosible(Pelea pelea, Luchador lanzador, boolean autoCura) {
		if (pelea == null) {
			return false;
		}
		Luchador objetivo = null;
		StatsHechizos SH = null;
		if (autoCura) {
			if (lanzador.getPDVConBuff() >= lanzador.getPDVMax()) {
				return false;
			}
			objetivo = lanzador;
			SH = mejorHechizoCuracion(pelea, lanzador, objetivo);
		} else {
			Luchador tempObjetivo = null;
			int porcPDVmin = 100;
			StatsHechizos tempSH = null;
			for (Luchador blanco : pelea.luchadoresDeEquipo(3)) {
				if (blanco.estaMuerto() || blanco == lanzador) {
					continue;
				}
				if (blanco.getPDVConBuff() >= blanco.getPDVMax()) {
					continue;
				}
				if (blanco.getParamEquipoAliado() == lanzador.getParamEquipoAliado()) {
					int porcPDV = 0;
					int PDVMAX = blanco.getPDVMaxConBuff();
					if (PDVMAX == 0) {
						porcPDV = 0;
					} else {
						porcPDV = (blanco.getPDVConBuff() * 100) / PDVMAX;
					}
					if (porcPDV < porcPDVmin && porcPDV < 95) {
						int infl = 0;
						for (Entry<Integer, StatsHechizos> ss : lanzador.getMob().getHechizos().entrySet()) {
							if (ss.getValue().getHechizo()._tipo != 3) {
								continue;
							}
							int infCura = calculaInfluenciaCura(ss.getValue());
							if (infl < infCura && infCura != 0 && pelea.puedeLanzarHechizo(lanzador, ss.getValue(),
									blanco.getCeldaPelea(), -1, true, true)) {
								infl = infCura;
								tempSH = ss.getValue();
							}
						}
						if (tempSH != SH && tempSH != null) {
							tempObjetivo = blanco;
							SH = tempSH;
							porcPDVmin = porcPDV;
						}
					}
				}
			}
			objetivo = tempObjetivo;
		}
		if ((objetivo == null) || (SH == null)) {
			return false;
		}
		int cura = pelea.intentarLanzarHechizo(lanzador, SH, objetivo.getCeldaPelea().getID());
		if (cura != 0) {
			return true;
		}
		return true;
	}

	private static boolean curaSiEsPosiblePrisma(Pelea pelea, Luchador prisma, boolean autoCura) {
		if ((pelea == null) || (autoCura && (prisma.getPDVConBuff() * 100) / prisma.getPDVMaxConBuff() > 95)) {
			return false;
		}
		Luchador objetivo = null;
		Hechizo hechizo = MundoDofus.getHechizo(124);
		StatsHechizos SH = hechizo.getStatsPorNivel(6);
		if (autoCura) {
			objetivo = prisma;
		} else {
			Luchador curado = null;
			int porcPDVmin = 100;
			for (Luchador blanco : pelea.luchadoresDeEquipo(3)) {
				if (blanco.estaMuerto() || blanco == prisma) {
					continue;
				}
				if (blanco.getParamEquipoAliado() == prisma.getParamEquipoAliado()) {
					int porcPDV = (blanco.getPDVConBuff() * 100) / blanco.getPDVMaxConBuff();
					if (porcPDV < porcPDVmin && porcPDV < 95) {
						curado = blanco;
						porcPDVmin = porcPDV;
					}
				}
			}
			objetivo = curado;
		}
		if (objetivo == null) {
			return false;
		}
		if (SH == null) {
			return false;
		}
		int cura = pelea.intentarLanzarHechizo(prisma, SH, objetivo.getCeldaPelea().getID());
		if (cura != 0) {
			return false;
		}
		return true;
	}

	private static boolean curaSiEsPosibleRecau(Pelea pelea, Luchador recaudador, boolean autoCura) {
		if ((pelea == null) || (autoCura && (recaudador.getPDVConBuff() * 100) / recaudador.getPDVMaxConBuff() > 95)) {
			return false;
		}
		Luchador objetivo = null;
		StatsHechizos SH = null;
		if (autoCura) {
			objetivo = recaudador;
			SH = mejorHechizoCuracionRecaudador(pelea, recaudador, objetivo);
		} else {
			Luchador tempObjetivo = null;
			int porcPDVmin = 100;
			StatsHechizos tempSH = null;
			if (pelea.luchadoresDeEquipo(recaudador.getParamEquipoAliado()).size() <= 1) {
				return false;
			}
			for (Luchador blanco : pelea.luchadoresDeEquipo(3)) {
				if (blanco.estaMuerto() || blanco == recaudador) {
					continue;
				}
				if (blanco.getParamEquipoAliado() == recaudador.getParamEquipoAliado()) {
					int elmaxv = blanco.getPDVMaxConBuff();
					if (blanco.getPDVConBuff() > elmaxv) {
						elmaxv = blanco.getPDVConBuff();
					}
					int porcPDV = (blanco.getPDVConBuff() * 100) / elmaxv;
					if (porcPDV < porcPDVmin && porcPDV < 95) {
						int infl = 0;
						for (Entry<Integer, StatsHechizos> sh : MundoDofus
								.getGremio(recaudador.getRecau().getGremioID()).getHechizos().entrySet()) {
							if (sh.getValue() == null) {
								continue;
							}
							int infCura = calculaInfluenciaCura(sh.getValue());
							if (infl < infCura && infCura != 0 && pelea.puedeLanzarHechizo(recaudador, sh.getValue(),
									blanco.getCeldaPelea(), -1, true, true)) {
								infl = infCura;
								tempSH = sh.getValue();
							}
						}
						if (tempSH != SH && tempSH != null) {
							tempObjetivo = blanco;
							SH = tempSH;
							porcPDVmin = porcPDV;
						}
					}
				}
			}
			objetivo = tempObjetivo;
		}
		if (objetivo == null) {
			return false;
		}
		if (SH == null) {
			return false;
		}
		int cura = pelea.intentarLanzarHechizo(recaudador, SH, objetivo.getCeldaPelea().getID());
		if (cura != 0) {
			return false;
		}
		return true;
	}

	private static boolean buffeaSiEsPosible1(Pelea pelea, Luchador lanzador, Luchador objetivo) {
		if ((pelea == null) || (objetivo == null)) {
			return false;
		}
		try {
			StatsHechizos SH = mejorBuff1(pelea, lanzador, objetivo);
			if (SH == null) {
				return false;
			}
			int buff = pelea.intentarLanzarHechizo(lanzador, SH, objetivo.getCeldaPelea().getID());
			if (buff != 0) {
				return true;
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private static boolean buffeaSiEsPosible2(Pelea pelea, Luchador lanzador, Luchador objetivo) {
		if ((pelea == null) || (objetivo == null)) {
			return false;
		}
		try {
			StatsHechizos SH = mejorBuff2(pelea, lanzador, objetivo);
			if (SH == null) {
				return false;
			}
			int buff = pelea.intentarLanzarHechizo(lanzador, SH, objetivo.getCeldaPelea().getID());
			if (buff != 0) {
				return true;
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private static boolean buffeaKralamar(Pelea pelea, Luchador lanzador, Luchador objetivo) {
		if ((pelea == null) || (objetivo == null)) {
			return false;
		}
		Hechizo hechizo = MundoDofus.getHechizo(1106);
		StatsHechizos SH = hechizo.getStatsPorNivel(1);
		if (SH == null) {
			return false;
		}
		int buff = 5;
		try {
			buff = pelea.intentarLanzarHechizo(lanzador, SH, objetivo.getCeldaPelea().getID());
		} catch (Exception e) {
			return false;
		}
		if (buff != 0) {
			return false;
		}
		return true;
	}

	private static boolean buffeaSiEsPosiblePrisma(Pelea pelea, Luchador lanzador, Luchador objetivo) {
		if ((pelea == null) || (objetivo == null)) {
			return false;
		}
		StatsHechizos SH = mejorBuffPrisma(pelea, lanzador);
		if (SH == null) {
			return false;
		}
		int buff = 5;
		try {
			buff = pelea.intentarLanzarHechizo(lanzador, SH, objetivo.getCeldaPelea().getID());
		} catch (Exception e) {
			return false;
		}
		if (buff != 0) {
			return false;
		}
		return true;
	}

	private static boolean buffeaSiEsPosibleRecau(Pelea pelea, Luchador recaudador, Luchador objetivo) {
		if ((pelea == null) || (objetivo == null)) {
			return false;
		}
		try {
			StatsHechizos SH = mejorBuffRecaudador(pelea, recaudador, objetivo);
			if (SH == null) {
				return false;
			}
			int buff = pelea.intentarLanzarHechizo(recaudador, SH, objetivo.getCeldaPelea().getID());
			if (buff != 0) {
				return false;
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private static StatsHechizos mejorBuffPrisma(Pelea pelea, Luchador lanzador) {
		if (pelea == null) {
			return null;
		}
		Hechizo hechizo = MundoDofus.getHechizo(153);
		StatsHechizos hechizoStats = hechizo.getStatsPorNivel(6);
		return hechizoStats;
	}

	private static StatsHechizos mejorBuffRecaudador(Pelea pelea, Luchador lanzador, Luchador objetivo) {
		if (pelea == null) {
			return null;
		}
		int infl = 0;
		StatsHechizos sh = null;
		if (objetivo == null) {
			return null;
		}
		try {
			for (Entry<Integer, StatsHechizos> SH : MundoDofus.getGremio(lanzador.getRecau().getGremioID())
					.getHechizos().entrySet()) {
				if (SH.getValue() == null) {
					continue;
				}
				int infDaÑos = calculaInfluenciaDaÑo(SH.getValue(), lanzador, objetivo);
				if (infl < infDaÑos && infDaÑos > 0 && pelea.puedeLanzarHechizo(lanzador, SH.getValue(),
						objetivo.getCeldaPelea(), -1, true, true)) {
					infl = infDaÑos;
					sh = SH.getValue();
				}
			}
		} catch (Exception e) {
			return null;
		}
		return sh;
	}

	private static StatsHechizos mejorHechizoCuracion(Pelea pelea, Luchador lanzador, Luchador objetivo) {
		if (pelea == null) {
			return null;
		}
		int infl = 0;
		StatsHechizos sh = null;
		if (objetivo == null) {
			return null;
		}
		try {
			for (Entry<Integer, StatsHechizos> SH : lanzador.getMob().getHechizos().entrySet()) {
				if (SH.getValue().getHechizo()._tipo != 1) {
					continue;
				}
				int infCura = calculaInfluenciaCura(SH.getValue());
				if (infl < infCura && infCura != 0 && pelea.puedeLanzarHechizo(lanzador, SH.getValue(),
						objetivo.getCeldaPelea(), -1, true, true)) {
					infl = infCura;
					sh = SH.getValue();
				}
			}
		} catch (Exception e) {
			return null;
		}
		return sh;
	}

	private static StatsHechizos mejorHechizoCuracionRecaudador(Pelea pelea, Luchador lanzador, Luchador objetivo) {
		if (pelea == null) {
			return null;
		}
		int infl = 0;
		StatsHechizos sh = null;
		if (objetivo == null) {
			return null;
		}
		try {
			for (Entry<Integer, StatsHechizos> SH : MundoDofus.getGremio(lanzador.getRecau().getGremioID())
					.getHechizos().entrySet()) {
				if ((SH.getValue().getHechizo()._tipo != 1) || (SH.getValue() == null)) {
					continue;
				}
				int infCura = calculaInfluenciaCura(SH.getValue());
				if (infl < infCura && infCura != 0 && pelea.puedeLanzarHechizo(lanzador, SH.getValue(),
						objetivo.getCeldaPelea(), -1, true, true)) {
					infl = infCura;
					sh = SH.getValue();
				}
			}
		} catch (Exception e) {
			return null;
		}
		return sh;
	}

	private static Luchador amigoMasCercano(Pelea pelea, Luchador lanzador) {
		int dist = 1000;
		Luchador tempObjetivo = null;
		for (Luchador objetivo : pelea.luchadoresDeEquipo(lanzador.getParamEquipoAliado())) {
			if (objetivo == null || lanzador == null || objetivo.estaMuerto() || objetivo == lanzador) {
				continue;
			}
			int d = Camino.distanciaEntreDosCeldas(pelea.getMapaCopia(), lanzador.getCeldaPelea().getID(),
					objetivo.getCeldaPelea().getID());
			if (d < dist) {
				dist = d;
				tempObjetivo = objetivo;
			}
		}
		return tempObjetivo;
	}

	private static Luchador enemigoMasCercano(Pelea pelea, Luchador lanzador) {
		return enemigoMasCercano(pelea, lanzador, null);
	}

	private static Luchador enemigoMasCercano(Pelea pelea, Luchador lanzador, Luchador quenoseaeste) {
		if (Emu.Cerrando || (pelea.getMapaCopia() == null)) {
			return null;
		}
		ArrayList<Luchador> luchadores = new ArrayList<>();
		ArrayList<Luchador> luchadoresProtect = new ArrayList<>();
		for (Luchador objetivo : pelea.luchadoresDeEquipo(lanzador.getParamEquipoEnemigo())) {
			if (objetivo == null || objetivo.estaMuerto() || objetivo.esInvisible() || objetivo.getCeldaPelea() == null
					|| objetivo.estaRetirado()) {
				continue;
			}
			if (objetivo.getBuff(106) != null || objetivo.statico) { // reenvio hechizo
				continue;
			}
			if (objetivo.getTransportadoPor() != null) {
				if (objetivo.getTransportadoPor().getEquipoBin() == lanzador.getEquipoBin()) {
					continue;
				}
			}
			if (quenoseaeste != null) {
				if (objetivo == quenoseaeste) {
					continue;
				}
			}
			if (objetivo.getBuff(105) != null || objetivo.getBuff(107) != null) { // daÑos reducidos y reenvio cac
				luchadoresProtect.add(objetivo);
			} else { // daÑos reducidos y reenvio cac
				luchadores.add(objetivo);
			}
		}
		if (luchadores.isEmpty() && !luchadoresProtect.isEmpty()) {
			luchadores.addAll(luchadoresProtect);
			luchadoresProtect.clear();
		}
		int distPJ = 1000;
		int vidaPJ = 100000000;
		Luchador tempObjetivoPJ = null;
		int dist = 1000;
		int vida = 100000000;
		Luchador tempObjetivo = null;
		for (Luchador objetivo : luchadores) {
			if (objetivo.getEquipoBin() == lanzador.getEquipoBin()) {
				continue;
			}
			if (objetivo.esInvisible()) {
				continue;
			}
			int d = Camino.distanciaEntreDosCeldas(pelea.getMapaCopia(), lanzador.getCeldaPelea().getID(),
					objetivo.getCeldaPelea().getID());// 3 celdas
			int pdv = objetivo.getPDVConBuff();// 1000
			int PMs = lanzador.getPMConBuff();// tengo 5pm
			if (PMs > d) {
				d -= PMs;
			}
			if (d <= 0) {
				d = 1;
			}
			if (pdv <= vidaPJ && d <= distPJ && objetivo.getPersonaje() != null) {// busca al pj con menos vida y el mas
																					// cercano
				distPJ = d;
				tempObjetivoPJ = objetivo;
				vidaPJ = pdv;
			}
			if (pdv <= vida && d <= dist && objetivo.getPersonaje() == null) {// busca al pj con menos vida y el mas
																				// cercano
				dist = d;
				tempObjetivo = objetivo;
				vida = pdv;

			}

		}
		if (tempObjetivoPJ != null && tempObjetivo != null) {
			if (distPJ > dist) { // (7-5)
				tempObjetivoPJ = tempObjetivo;
			}
		}
		if (tempObjetivoPJ == null && tempObjetivo != null) {
			tempObjetivoPJ = tempObjetivo;
		}
		if (lanzador != null && tempObjetivoPJ != null) {
			if (lanzador.esInvocacion()) {
				if (lanzador.getInvocador() != null) {
					if (lanzador.getInvocador().manualObj != null) {
						if (!lanzador.getInvocador().manualObj.esInvisible()) {
							if (lanzador.getInvocador().manualObj.estaMuerto()) {
								lanzador.getInvocador().manualObj = null;
							} else {
								return lanzador.getInvocador().manualObj;
							}
						}
					}
				}
			}
		}
		ArrayList<Luchador> enemigosAlrededor = Camino.getEnemigosAlrededor(lanzador, pelea.getMapaCopia(), pelea);
		if (enemigosAlrededor != null) {
			if (!enemigosAlrededor.contains(tempObjetivoPJ)) {
				int perdida = Pelea.getPorcHuida(lanzador, enemigosAlrededor);
				int pierdePM = 0;
				if (perdida <= 100 && perdida >= 0) {
					pierdePM = lanzador.getTempPM(pelea) * perdida / 100;
				}
				if (pierdePM <= 0) {
					Random randomGenerator = new Random();
					int index = randomGenerator.nextInt(enemigosAlrededor.size());
					Luchador luct = enemigosAlrededor.get(index);
					tempObjetivoPJ = luct;
					lanzador.atacoAOtro = luct;
				} else {
					lanzador.excepcionMover = true;
				}
			}
		}
		return tempObjetivoPJ;
	}

	private static Luchador luchadorMasCercano(Pelea pelea, Luchador lanzador) {
		int dist = 1000;
		Luchador tempObjetivo = null;
		for (Luchador objetivo : pelea.luchadoresDeEquipo(3)) {
			if (objetivo == null || lanzador == null || objetivo.estaMuerto() || objetivo == lanzador) {
				continue;
			}
			int d = Camino.distanciaEntreDosCeldas(pelea.getMapaCopia(), lanzador.getCeldaPelea().getID(),
					objetivo.getCeldaPelea().getID());
			if (d < dist) {
				dist = d;
				tempObjetivo = objetivo;
			}
		}
		return tempObjetivo;
	}

	private static ArrayList<Luchador> listaTodoEnemigos(Pelea pelea, Luchador lanzador) {
		ArrayList<Luchador> listaEnemigos = new ArrayList<>();
		ArrayList<Luchador> enemigosNoInvo = new ArrayList<>();
		ArrayList<Luchador> enemigosInvo = new ArrayList<>();
		for (Luchador objetivo : pelea.luchadoresDeEquipo(lanzador.getParamEquipoEnemigo())) {
			if (objetivo.estaMuerto() || objetivo.esInvisible()) {
				continue;
			}
			if (objetivo.esInvocacion()) {
				enemigosInvo.add(objetivo);
			} else {
				enemigosNoInvo.add(objetivo);
			}
		}
		Random rand = new Random();
		if (rand.nextBoolean()) {
			listaEnemigos.addAll(enemigosInvo);
			listaEnemigos.addAll(enemigosNoInvo);
		} else {
			listaEnemigos.addAll(enemigosNoInvo);
			listaEnemigos.addAll(enemigosInvo);
		}
		return listaEnemigos;
	}

	private static ArrayList<Luchador> listaEnemigosMenosPDV(Pelea pelea, Luchador lanzador) {
		ArrayList<Luchador> listaEnemigos = new ArrayList<>();
		ArrayList<Luchador> enemigosNoInvo = new ArrayList<>();
		ArrayList<Luchador> enemigosInvo = new ArrayList<>();
		for (Luchador objetivo : pelea.luchadoresDeEquipo(lanzador.getParamEquipoEnemigo())) {
			if (objetivo.estaMuerto() || objetivo.esInvisible()) {
				continue;
			}
			if (objetivo.esInvocacion()) {
				enemigosInvo.add(objetivo);
			} else {
				enemigosNoInvo.add(objetivo);
			}
		}
		int i = 0;
		int tempPDV;
		Random rand = new Random();
		if (rand.nextBoolean()) {
			try {
				int i3 = enemigosNoInvo.size(), i2 = enemigosInvo.size();
				while (i < i2) {
					tempPDV = 200000;
					int index = 0;
					for (Luchador invo : enemigosInvo) {
						if (invo.getPDVConBuff() <= tempPDV) {
							tempPDV = invo.getPDVConBuff();
							index = enemigosInvo.indexOf(invo);
						}
					}
					Luchador test = enemigosInvo.get(index);
					if (test != null) {
						listaEnemigos.add(test);
					}
					enemigosInvo.remove(index);
					i++;
				}
				i = 0;
				while (i < i3) {
					tempPDV = 200000;
					int index = 0;
					for (Luchador invo : enemigosNoInvo) {
						if (invo.getPDVConBuff() <= tempPDV) {
							tempPDV = invo.getPDVConBuff();
							index = enemigosNoInvo.indexOf(invo);
						}
					}
					Luchador test = enemigosNoInvo.get(index);
					if (test != null) {
						listaEnemigos.add(test);
					}
					enemigosNoInvo.remove(index);
					i++;
				}
			} catch (Exception e) {
				return listaEnemigos;
			}
		} else {
			try {
				int i2 = enemigosNoInvo.size(), i3 = enemigosInvo.size();
				while (i < i2) {
					tempPDV = 200000;
					int index = 0;
					for (Luchador invo : enemigosNoInvo) {
						if (invo.getPDVConBuff() <= tempPDV) {
							tempPDV = invo.getPDVConBuff();
							index = enemigosNoInvo.indexOf(invo);
						}
					}
					Luchador test = enemigosNoInvo.get(index);
					if (test != null) {
						listaEnemigos.add(test);
					}
					enemigosNoInvo.remove(index);
					i++;
				}
				i = 0;
				while (i < i3) {
					tempPDV = 200000;
					int index = 0;
					for (Luchador invo : enemigosInvo) {
						if (invo.getPDVConBuff() <= tempPDV) {
							tempPDV = invo.getPDVConBuff();
							index = enemigosInvo.indexOf(invo);
						}
					}
					Luchador test = enemigosInvo.get(index);
					if (test != null) {
						listaEnemigos.add(test);
					}
					enemigosInvo.remove(index);
					i++;
				}
			} catch (Exception e) {
				return listaEnemigos;
			}
		}
		if (lanzador != null && listaEnemigos.size() > 0) {
			if (lanzador.esInvocacion()) {
				if (lanzador.getInvocador() != null) {
					if (lanzador.getInvocador().manualObj != null) {
						if (!lanzador.getInvocador().manualObj.esInvisible()) {
							if (lanzador.getInvocador().manualObj.estaMuerto()) {
								lanzador.getInvocador().manualObj = null;
							} else {
								listaEnemigos.add(lanzador.getInvocador().manualObj);
								return listaEnemigos;
							}
						}
					}
				}
			}
		}
		return listaEnemigos;
	}

	private static ArrayList<Luchador> listaTodoLuchadores(Pelea pelea, Luchador lanzador) {
		Luchador enemigoMasCercano = luchadorMasCercano(pelea, lanzador);
		ArrayList<Luchador> listaEnemigos = new ArrayList<>();
		ArrayList<Luchador> enemigosNoInvo = new ArrayList<>();
		ArrayList<Luchador> enemigosInvo = new ArrayList<>();
		for (Luchador objetivo : pelea.luchadoresDeEquipo(3)) {
			if (objetivo.estaMuerto()) {
				continue;
			}
			if (objetivo.esInvocacion()) {
				enemigosInvo.add(objetivo);
			} else {
				enemigosNoInvo.add(objetivo);
			}
		}
		if (enemigoMasCercano != null) {
			listaEnemigos.add(enemigoMasCercano);
		}
		int i = 0;
		int tempPDV;
		Random rand = new Random();
		if (rand.nextBoolean()) {
			try {
				int i3 = enemigosNoInvo.size(), i2 = enemigosInvo.size();
				while (i < i2) {
					tempPDV = 200000;
					int index = 0;
					for (Luchador invo : enemigosInvo) {
						if (invo.getPDVConBuff() <= tempPDV) {
							tempPDV = invo.getPDVConBuff();
							index = enemigosInvo.indexOf(invo);
						}
					}
					Luchador test = enemigosInvo.get(index);
					if (test != null) {
						listaEnemigos.add(test);
					}
					enemigosInvo.remove(index);
					i++;
				}
				i = 0;
				while (i < i3) {
					tempPDV = 200000;
					int index = 0;
					for (Luchador invo : enemigosNoInvo) {
						if (invo.getPDVConBuff() <= tempPDV) {
							tempPDV = invo.getPDVConBuff();
							index = enemigosNoInvo.indexOf(invo);
						}
					}
					Luchador test = enemigosNoInvo.get(index);
					if (test != null) {
						listaEnemigos.add(test);
					}
					enemigosNoInvo.remove(index);
					i++;
				}
			} catch (Exception e) {
				return listaEnemigos;
			}
		} else {
			try {
				int i2 = enemigosNoInvo.size(), i3 = enemigosInvo.size();
				while (i < i2) {
					tempPDV = 200000;
					int index = 0;
					for (Luchador invo : enemigosNoInvo) {
						if (invo.getPDVConBuff() <= tempPDV) {
							tempPDV = invo.getPDVConBuff();
							index = enemigosNoInvo.indexOf(invo);
						}
					}
					Luchador test = enemigosNoInvo.get(index);
					if (test != null) {
						listaEnemigos.add(test);
					}
					enemigosNoInvo.remove(index);
					i++;
				}
				i = 0;
				while (i < i3) {
					tempPDV = 200000;
					int index = 0;
					for (Luchador invo : enemigosInvo) {
						if (invo.getPDVConBuff() <= tempPDV) {
							tempPDV = invo.getPDVConBuff();
							index = enemigosInvo.indexOf(invo);
						}
					}
					Luchador test = enemigosInvo.get(index);
					if (test != null) {
						listaEnemigos.add(test);
					}
					enemigosInvo.remove(index);
					i++;
				}
			} catch (Exception e) {
				return listaEnemigos;
			}
		}
		return listaEnemigos;
	}

	private static int atacaSiEsPosibleRecau(Pelea pelea, Luchador recaudador) {
		ArrayList<Luchador> listaEnemigos = objetivosMasCercanos(pelea, recaudador);
		StatsHechizos SH = null;
		Luchador objetivo = null;
		for (Luchador blanco : listaEnemigos) {
			SH = mejorHechizoRecau(pelea, recaudador, blanco);
			if (SH != null) {
				objetivo = blanco;
				break;
			}
		}
		if (objetivo == null || SH == null) {
			return 665;
		}
		int ataque = 665;
		int alcance = SH.getHechizo().getAfectadosEstandar().size();
		int tipoAlcance = alcance * 2;
		int tamaÑo = 0;
		char linea = 0;
		try {
			tamaÑo = Encriptador.getNumeroPorValorHash(SH.getAfectados().charAt(tipoAlcance + 1));
			linea = 0;
			linea = SH.getAfectados().charAt(tipoAlcance);
		} catch (Exception e) {
			linea = 0;
		}
		if (linea != 'C') {
			tamaÑo = 0;
		}
		ataque = mejoraLanzamientos(pelea, recaudador, objetivo, SH, tamaÑo);
		if (ataque != 0) {
			return ataque;
		}
		return 665;
	}

	private static int atacaSiEsPosiblePrisma(Pelea pelea, Luchador lanzador) {
		ArrayList<Luchador> listaEnemigos = listaEnemigosMenosPDV(pelea, lanzador);
		StatsHechizos SH = null;
		Luchador objetivo = null;
		for (Luchador blanco : listaEnemigos) {
			SH = mejorHechizoPrisma(pelea, lanzador, blanco);
			if (SH != null) {
				objetivo = blanco;
				break;
			}
		}
		if (objetivo == null || SH == null) {
			return 665;
		}
		int ataque = 665;
		int alcance = SH.getHechizo().getAfectadosEstandar().size();
		int tipoAlcance = alcance * 2;
		int tamaÑo = 0;
		char linea = 0;
		try {
			tamaÑo = Encriptador.getNumeroPorValorHash(SH.getAfectados().charAt(tipoAlcance + 1));
			linea = 0;
			linea = SH.getAfectados().charAt(tipoAlcance);
		} catch (Exception e) {
			linea = 0;
		}
		if (linea != 'C') {
			tamaÑo = 0;
		}
		ataque = mejoraLanzamientos(pelea, lanzador, objetivo, SH, tamaÑo);
		if (ataque != 0) {
			return ataque;
		}
		return 665;
	}

	private static int atacaSiEsPosibleTodos(Pelea pelea, Luchador lanzador, Luchador objetivo) {
		if (Emu.Cerrando || (pelea == null)) {
			return 666;
		}
		if (lanzador.getTempPA(pelea) <= 0) {
			return 665;
		}
		if (objetivo == null || objetivo.getCeldaPelea() == null) {
			return 665;
		}
		StatsHechizos SH = mejorHechizo1(pelea, lanzador, objetivo);
		if (SH == null) {
			objetivo = enemigoMasCercano(pelea, lanzador, objetivo);
			if (objetivo == null) {
				return 665;
			}
			return 665;
		}
		if (lanzador.atacoAOtro != null) {
			if (lanzador.atacoAOtro.estaMuerto() || lanzador.atacoAOtro.esInvisible()
					|| lanzador.atacoAOtro.estaRetirado()) {
				lanzador.atacoAOtro = null;
			}
			ArrayList<Luchador> enemigosAlrededor = Camino.getEnemigosAlrededor(lanzador, pelea.getMapaCopia(), pelea);
			if (enemigosAlrededor != null) {
				if (!enemigosAlrededor.contains(lanzador.atacoAOtro)) {
					objetivo = enemigoMasCercano(pelea, lanzador, lanzador.atacoAOtro);
					if (objetivo == null) {
						return 665;
					}
					lanzador.atacoAOtro = null;
				}
			} else {
				objetivo = enemigoMasCercano(pelea, lanzador, lanzador.atacoAOtro);
				if (objetivo == null) {
					return 665;
				}
				lanzador.atacoAOtro = null;
			}
		}
		if (objetivo.estaMuerto()) {
			objetivo = enemigoMasCercano(pelea, lanzador, objetivo);
			if (objetivo == null) {
				return 665;
			}
			SH = mejorHechizo1(pelea, lanzador, objetivo);
			if (SH == null) {
				return 665;
			}
		}
		if (objetivo.getPersonaje() != null) {
			if (!objetivo.getPersonaje().enLinea()) {
				return 665;
			}
		}
		int ataque = 0;
		if (SH.getHechizo()._tipo == 5) {
			int pa = SH.getHechizo().getStatsPorNivel(SH.getNivel()).getMaxAlc();
			ArrayList<Integer> celdasTP = Camino.getCeldas(pelea.getMapaCopia(), lanzador.getCeldaPelea().getID(), pa,
					SH, pelea, lanzador);
			int celdamasC = Camino.celdaCercanas(celdasTP, objetivo.getCeldaPelea().getID(), pelea.getMapaCopia());
			ataque = pelea.intentarLanzarHechizo(lanzador, SH, celdamasC);
			if (ataque != 10) {
				if (objetivo.getBuff(107) == null) {
					if (pelea.puedeLanzarHechizo(lanzador, MundoDofus.getHechizo(0).getStatsPorNivel(6),
							objetivo.getCeldaPelea(), lanzador.getCeldaPelea().getID(), true, true)) {
						ataque = pelea.intentarLanzarHechizo(lanzador, MundoDofus.getHechizo(0).getStatsPorNivel(6),
								objetivo.getCeldaPelea().getID());
					} else {
						return 665;
					}
				} else {
					return 665;
				}
			}
			if (ataque != 0) {
				return ataque;
			}
		} else {
			int alcance = SH.getHechizo().getAfectadosEstandar().size();
			int tipoAlcance = alcance * 2;
			int tamaÑo = 0;
			char linea = 0;
			try {
				tamaÑo = Encriptador.getNumeroPorValorHash(SH.getAfectados().charAt(tipoAlcance + 1));
				linea = 0;
				linea = SH.getAfectados().charAt(tipoAlcance);
			} catch (Exception e) {
				linea = 0;
			}
			if (linea != 'C') {
				tamaÑo = 0;
			}
			ataque = mejoraLanzamientos(pelea, lanzador, objetivo, SH, tamaÑo);
		}
		if (ataque != 0) {
			return ataque;
		}
		return 665;
	}

	private static int mejoraLanzamientos(Pelea pelea, Luchador lanzador, Luchador objetivo, StatsHechizos SH,
			int alcance) {
		int ataque;
		int celdaPreferida = 0;
		if (alcance > 0 && alcance < 3 && SH.getHechizo()._tipo == 0) {
			int pa = SH.getMaxAlc();
			if (pa != 0) {
				ArrayList<Integer> celdasTP = Camino.getCeldas(pelea.getMapaCopia(), lanzador.getCeldaPelea().getID(),
						pa, null, pelea, lanzador);
				if (celdasTP.size() > 0) {
					int masenemigos = 0;
					int menosaliados = 0;
					for (Integer celda : celdasTP) {
						int equipoal = 0;
						int equipoen = 0;
						ArrayList<Luchador> luchadores = Camino.getCeldasEnemigo(pelea.getMapaCopia(), celda, alcance,
								SH, pelea, lanzador);
						if (luchadores.size() > 0) {
							for (Luchador lux : luchadores) {
								if (lux.getEquipoBin() == lanzador.getEquipoBin()) {
									equipoal += 1;
								} else {
									equipoen += 1;
								}
							}
							if (equipoen > 0 && equipoen > masenemigos) {
								if (equipoal <= menosaliados) {
									celdaPreferida = celda;
									masenemigos = equipoen;
									menosaliados = equipoal;
								}
							}
						}
					}
				}
			}
		}
		if (celdaPreferida != 0) {
			ataque = pelea.intentarLanzarHechizo(lanzador, SH, celdaPreferida);
		} else {
			if (objetivo != null) {
				if (pelea.puedeLanzarHechizo(lanzador, SH, objetivo.getCeldaPelea(), -1, true, true)) {
					ataque = pelea.intentarLanzarHechizo(lanzador, SH, objetivo.getCeldaPelea().getID());
				} else {
					return 0;
				}
			} else {
				return 0;
			}
		}
		return ataque;
	}

	private static int atacaSiEsPosible1(Pelea pelea, Luchador lanzador) {
		ArrayList<Luchador> listaEnemigos = objetivosMasCercanos(pelea, lanzador);
		StatsHechizos SH = null;
		Luchador objetivo = null;
		for (Luchador blanco : listaEnemigos) {
			SH = mejorHechizo1(pelea, lanzador, blanco);
			if (SH != null) {
				objetivo = blanco;
				break;
			}
		}
		if (objetivo == null || SH == null) {
			return 665;
		}
		int ataque = 665;
		int alcance = SH.getHechizo().getAfectadosEstandar().size();
		int tipoAlcance = alcance * 2;
		int tamaÑo = 0;
		char linea = 0;
		try {
			tamaÑo = Encriptador.getNumeroPorValorHash(SH.getAfectados().charAt(tipoAlcance + 1));
			linea = 0;
			linea = SH.getAfectados().charAt(tipoAlcance);
		} catch (Exception e) {
			linea = 0;
		}
		if (linea != 'C') {
			tamaÑo = 0;
		}
		ataque = mejoraLanzamientos(pelea, lanzador, objetivo, SH, tamaÑo);
		if (ataque != 0) {
			return ataque;
		}
		return 665;
	}

	private static int atacaSiEsPosible2(Pelea pelea, Luchador lanzador) {
		ArrayList<Luchador> listaEnemigos = objetivosMasCercanos(pelea, lanzador);
		StatsHechizos SH = null;
		Luchador objetivo = null;
		for (Luchador blanco : listaEnemigos) {
			SH = mejorHechizo2(pelea, lanzador, blanco);
			if (SH != null) {
				objetivo = blanco;
				break;
			}
		}
		if (objetivo == null || SH == null) {
			return 665;
		}
		if (lanzador != null && objetivo != null) {
			if (lanzador.esInvocacion()) {
				if (lanzador.getInvocador() != null) {
					if (lanzador.getInvocador().manualObj != null) {
						if (!lanzador.getInvocador().manualObj.esInvisible()) {
							if (lanzador.getInvocador().manualObj.estaMuerto()) {
								lanzador.getInvocador().manualObj = null;
							} else {
								objetivo = lanzador.getInvocador().manualObj;
							}
						}
					}
				}
			}
		}
		int ataque = 665;
		int alcance = SH.getHechizo().getAfectadosEstandar().size();
		int tipoAlcance = alcance * 2;
		int tamaÑo = 0;
		char linea = 0;
		try {
			tamaÑo = Encriptador.getNumeroPorValorHash(SH.getAfectados().charAt(tipoAlcance + 1));
			linea = 0;
			linea = SH.getAfectados().charAt(tipoAlcance);
		} catch (Exception e) {
			linea = 0;
		}
		if (linea != 'C') {
			tamaÑo = 0;
		}
		ataque = mejoraLanzamientos(pelea, lanzador, objetivo, SH, tamaÑo);
		if (ataque != 0) {
			return ataque;
		}
		return 665;
	}

	private static int atacaSiEsPosible3(Pelea pelea, Luchador lanzador) {
		ArrayList<Luchador> listaEnemigos = listaTodoLuchadores(pelea, lanzador);
		StatsHechizos SH = null;
		Luchador objetivo = null;
		for (Luchador blanco : listaEnemigos) {
			SH = mejorHechizo2(pelea, lanzador, blanco);
			if (SH != null) {
				objetivo = blanco;
				break;
			}
		}
		if (objetivo == null || SH == null) {
			return 665;
		}
		int ataque = 665;
		int alcance = SH.getHechizo().getAfectadosEstandar().size();
		int tipoAlcance = alcance * 2;
		int tamaÑo = 0;
		char linea = 0;
		try {
			tamaÑo = Encriptador.getNumeroPorValorHash(SH.getAfectados().charAt(tipoAlcance + 1));
			linea = 0;
			linea = SH.getAfectados().charAt(tipoAlcance);
		} catch (Exception e) {
			linea = 0;
		}
		if (linea != 'C') {
			tamaÑo = 0;
		}
		ataque = mejoraLanzamientos(pelea, lanzador, objetivo, SH, tamaÑo);
		if (ataque != 0) {
			return ataque;
		}
		return 665;
	}

	private static boolean moverYAtacarSiEsPosible(Pelea pelea, Luchador lanzador) {
		ArrayList<Integer> celdas = Camino.listaCeldasDesdeLuchador(pelea, lanzador);
		if (celdas == null) {
			return false;
		}
		Luchador enemigo = enemigoMasCercano(pelea, lanzador);
		if (enemigo == null) {
			return false;
		}
		StatsHechizos hechizo;
		int distMin = Camino.distanciaEntreDosCeldas(pelea.getMapaCopia(), lanzador.getCeldaPelea().getID(),
				enemigo.getCeldaPelea().getID());
		ArrayList<StatsHechizos> hechizos = hechizosLanzables(lanzador, pelea, distMin);
		if (hechizos == null || hechizos.isEmpty()) {
			return false;
		}
		if (hechizos.size() == 1) {
			hechizo = hechizos.get(0);
		} else {
			hechizo = hechizos.get(Formulas.getRandomValor(0, hechizos.size() - 1));
		}
		ArrayList<Luchador> objetivos = objetivosMasCercanosAlHechizo(pelea, lanzador, hechizo);
		if (objetivos == null) {
			return false;
		}
		int celdaDestino = 0;
		Luchador objetivo = null;
		boolean encontrado = false;
		for (int celda : celdas) {
			for (Luchador O : objetivos) {
				if (pelea.puedeLanzarHechizo(lanzador, hechizo, O.getCeldaPelea(), celda, true, true)) {
					celdaDestino = celda;
					objetivo = O;
					encontrado = true;
				}
				if (encontrado) {
					break;
				}
			}
			if (encontrado) {
				break;
			}
		}
		if (celdaDestino == 0) {
			return false;
		}
		ArrayList<Celda> path = new AstarPathfinding(pelea.getMapaCopia(), pelea, lanzador.getCeldaPelea().getID(),
				celdaDestino).getShortestPath(-1);
		if (path == null) {
			return false;
		}
		StringBuilder pathStr = new StringBuilder("");
		try {
			int tempCeldaID = lanzador.getCeldaPelea().getID();
			int tempDir = 0;
			for (Celda c : path) {
				char dir = Camino.getDirEntreDosCeldas(tempCeldaID, c.getID(), pelea.getMapaCopia(), true);
				if (dir == 0) {
					return false;
				}
				if (tempDir != dir) {
					if (path.indexOf(c) != 0) {
						pathStr.append(Encriptador.celdaIDACodigo(tempCeldaID));
					}
					pathStr.append(dir);
				}
				tempCeldaID = c.getID();
			}
			if (tempCeldaID != lanzador.getCeldaPelea().getID()) {
				pathStr.append(Encriptador.celdaIDACodigo(tempCeldaID));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		AccionDeJuego GA = new AccionDeJuego(0, 1, "");
		GA._args = pathStr.toString();
		boolean resultado = false;
		if (objetivo != null && hechizo != null) {
			if (!pelea.puedeLanzarHechizo(lanzador, hechizo, objetivo.getCeldaPelea(), -1, true, true)) {
				resultado = pelea.puedeMoverseLuchador(lanzador, GA, "ia2");
				if (resultado) {
					pelea.intentarLanzarHechizo(lanzador, hechizo, objetivo.getCeldaPelea().getID());
				}
			} else {
				int i = pelea.intentarLanzarHechizo(lanzador, hechizo, objetivo.getCeldaPelea().getID());
				if (i == 10) {
					return true;
				}
			}
		}
		return resultado;
	}

	private static ArrayList<StatsHechizos> hechizosLanzables(Luchador lanzador, Pelea pelea, int distMin) {
		ArrayList<StatsHechizos> hechizos = new ArrayList<>();
		if (lanzador.getMob() == null) {
			return null;
		}
		for (Entry<Integer, StatsHechizos> SH : lanzador.getMob().getHechizos().entrySet()) {
			StatsHechizos hechizo = SH.getValue();
			if ((hechizo.getCostePA() > lanzador.getTempPA(pelea)) || !HechizoLanzado.poderSigLanzamiento(lanzador, hechizo.getHechizoID()) || (hechizo.getMaxLanzPorTurno() - HechizoLanzado.getNroLanzamientos(lanzador, hechizo.getHechizoID()) <= 0
					&& hechizo.getMaxLanzPorTurno() > 0)) {
				continue;
			}
			if (calculaInfluenciaDaÑo(hechizo, lanzador, lanzador) >= 0) {
				continue;
			}
			hechizos.add(hechizo);
		}
		ArrayList<StatsHechizos> hechizosFinales = hechizosMasAMenosDaÑos(lanzador, hechizos);
		return hechizosFinales;
	}

	private static ArrayList<StatsHechizos> hechizosMasAMenosDaÑos(Luchador lanzador,
			ArrayList<StatsHechizos> hechizos) {
		if (hechizos == null) {
			return null;
		}
		ArrayList<StatsHechizos> hechizosFinales = new ArrayList<>();
		Map<Integer, StatsHechizos> copia = new TreeMap<>();
		for (StatsHechizos SH : hechizos) {
			copia.put(SH.getHechizoID(), SH);
		}
		int tempInfluencia = 0;
		int tempID = 0;
		while (copia.size() > 0) {
			tempInfluencia = 0;
			tempID = 0;
			for (Entry<Integer, StatsHechizos> SH : copia.entrySet()) {
				int influencia = -calculaInfluenciaDaÑo(SH.getValue(), lanzador, lanzador);
				if (influencia > tempInfluencia) {
					tempID = SH.getValue().getHechizoID();
					tempInfluencia = influencia;
				}
			}
			if (tempID == 0 || tempInfluencia == 0) {
				break;
			}
			hechizosFinales.add(copia.get(tempID));
			copia.remove(tempID);
		}
		return hechizosFinales;
	}

	private static ArrayList<Luchador> objetivosMasCercanosAlHechizo(Pelea pelea, Luchador lanzador,
			StatsHechizos hechizo) {
		ArrayList<Luchador> objetivos = new ArrayList<>();
		ArrayList<Luchador> objetivos1 = new ArrayList<>();
		int distMax = hechizo.getMaxAlc();
		distMax += lanzador.getTempPM(pelea);
		ArrayList<Luchador> objetivosP = listaTodoEnemigos(pelea, lanzador);
		for (Luchador entry : objetivosP) {
			Luchador objetivo = entry;
			int dist = Camino.distanciaEntreDosCeldas(pelea.getMapaCopia(), lanzador.getCeldaPelea().getID(),
					objetivo.getCeldaPelea().getID());
			if (dist < distMax) {
				objetivos.add(objetivo);
			}
		}
		while (objetivos.size() > 0) {
			int index = 0;
			int dista = 1000;
			for (Luchador objetivo : objetivos) {
				int dist = Camino.distanciaEntreDosCeldas(pelea.getMapaCopia(), lanzador.getCeldaPelea().getID(),
						objetivo.getCeldaPelea().getID());
				if (dist < dista) {
					dista = dist;
					index = objetivos.indexOf(objetivo);
				}
			}
			objetivos1.add(objetivos.get(index));
			objetivos.remove(index);
		}
		return objetivos1;
	}

	private static ArrayList<Luchador> objetivosMasCercanos(Pelea pelea, Luchador lanzador) {
		ArrayList<Luchador> objetivos = new ArrayList<>();
		ArrayList<Luchador> objetivos1 = listaTodoEnemigos(pelea, lanzador);
		while (objetivos.size() > 0) {
			int index = 0;
			int dista = 1000;
			for (Luchador objetivo : objetivos) {
				int dist = Camino.distanciaEntreDosCeldas(pelea.getMapaCopia(), lanzador.getCeldaPelea().getID(),
						objetivo.getCeldaPelea().getID());
				if (dist < dista) {
					dista = dist;
					index = objetivos.indexOf(objetivo);
				}
			}
			objetivos1.add(objetivos.get(index));
			objetivos.remove(index);
		}
		return objetivos1;
	}

	private static StatsHechizos mejorHechizoRecau(Pelea pelea, Luchador recaudador, Luchador objetivo) {
		int influenciaMax = 0;
		StatsHechizos sh = null;
		Map<Integer, StatsHechizos> hechiRecau = MundoDofus.getGremio(recaudador.getRecau().getGremioID())
				.getHechizos();
		if (objetivo == null) {
			return null;
		}
		for (Entry<Integer, StatsHechizos> SH1 : hechiRecau.entrySet()) {
			StatsHechizos hechizo1 = SH1.getValue();
			if (hechizo1 == null) {
				continue;
			}
			int tempInfluencia = 0, influencia1 = 0, influencia2 = 0;
			int PA = 6;
			int costePA[] = { 0, 0 };
			if (!pelea.puedeLanzarHechizo(recaudador, hechizo1, objetivo.getCeldaPelea(), -1, true, true)) {
				continue;
			}
			tempInfluencia = calculaInfluenciaDaÑo(hechizo1, recaudador, objetivo);
			if (tempInfluencia == 0) {
				continue;
			}
			if (tempInfluencia > influenciaMax) {
				sh = hechizo1;
				costePA[0] = sh.getCostePA();
				influencia1 = tempInfluencia;
				influenciaMax = influencia1;
			}
			for (Entry<Integer, StatsHechizos> SH2 : hechiRecau.entrySet()) {
				StatsHechizos hechizo2 = SH2.getValue();
				if ((hechizo2 == null) || ((PA - costePA[0]) < hechizo2.getCostePA()) || !pelea.puedeLanzarHechizo(recaudador, hechizo2, objetivo.getCeldaPelea(), -1, true, true)) {
					continue;
				}
				tempInfluencia = calculaInfluenciaDaÑo(hechizo2, recaudador, objetivo);
				if (tempInfluencia == 0) {
					continue;
				}
				if ((influencia1 + tempInfluencia) > influenciaMax) {
					sh = hechizo2;
					costePA[1] = hechizo2.getCostePA();
					influencia2 = tempInfluencia;
					influenciaMax = influencia1 + influencia2;
				}
				for (Entry<Integer, StatsHechizos> SH3 : hechiRecau.entrySet()) {
					StatsHechizos hechizo3 = SH3.getValue();
					if (hechizo3 == null) {
						continue;
					}
					if ((PA - costePA[0] - costePA[1]) < hechizo3.getCostePA()) {
						continue;
					}
					if (!pelea.puedeLanzarHechizo(recaudador, hechizo3, objetivo.getCeldaPelea(), -1, true, true)) {
						continue;
					}
					tempInfluencia = calculaInfluenciaDaÑo(hechizo3, recaudador, objetivo);
					if (tempInfluencia == 0) {
						continue;
					}
					if ((tempInfluencia + influencia1 + influencia2) > influenciaMax) {
						sh = hechizo3;
						influenciaMax = tempInfluencia + influencia1 + influencia2;
					}
				}
			}
		}
		return sh;
	}

	private static StatsHechizos mejorHechizoPrisma(Pelea pelea, Luchador lanzador, Luchador objetivo) {
		StatsHechizos sh = null;
		ArrayList<StatsHechizos> posibles = new ArrayList<>();
		if (objetivo == null) {
			return null;
		}
		try {
			for (Entry<Integer, StatsHechizos> SH : lanzador.getPrisma().getHechizos().entrySet()) {
				StatsHechizos statsH = SH.getValue();
				if (!pelea.puedeLanzarHechizo(lanzador, statsH, objetivo.getCeldaPelea(), -1, true, true)) {
					continue;
				}
				posibles.add(statsH);
			}
		} catch (Exception e) {
			return null;
		}
		if (posibles.isEmpty()) {
			return sh;
		}
		if (posibles.size() == 1) {
			return posibles.get(0);
		}
		sh = posibles.get(Formulas.getRandomValor(0, posibles.size() - 1));
		return sh;
	}

	private static StatsHechizos mejorBuff2(Pelea pelea, Luchador lanzador, Luchador objetivo) {
		if (pelea == null) {
			return null;
		}
		ArrayList<StatsHechizos> hechizos = new ArrayList<>();
		StatsHechizos sh = null;
		if (objetivo == null) {
			return null;
		}
		try {
			Celda celdaObj = objetivo.getCeldaPelea();
			for (Entry<Integer, StatsHechizos> SH : lanzador.getMob().getHechizos().entrySet()) {
				if (pelea.puedeLanzarHechizo(lanzador, SH.getValue(), celdaObj, -1, true, true)) {
					hechizos.add(SH.getValue());
				}
			}
			if (hechizos.size() <= 0) {
				return null;
			}
			if (hechizos.size() == 1) {
				return hechizos.get(0);
			}
			sh = hechizos.get(Formulas.getRandomValor(0, hechizos.size() - 1));
			if (sh == null) {
				return null;
			} else {
				return sh;
			}
		} catch (Exception e) {
			return null;
		}
	}

	private static StatsHechizos mejorBuff1(Pelea pelea, Luchador lanzador, Luchador objetivo) {
		StatsHechizos sh = null;
		Map<Integer, StatsHechizos> hechiMob = lanzador.getMob().getHechizos();
		if (objetivo == null || hechiMob.isEmpty()) {
			return null;
		}
		Map<Integer, StatsHechizos> hechizosDisp = new TreeMap<>();
		int valor = 0;
		for (Entry<Integer, StatsHechizos> SH : lanzador.getMob().getHechizos().entrySet()) {
			StatsHechizos hechizo1 = SH.getValue();
			if (hechizo1.getHechizo()._tipo != 1) {
				continue;
			}
			boolean pasa = true;
			if (SH.getValue().efectosID.contains(132) && objetivo.getBuffPelea().size() <= 0) {
				pasa = false;
			}
			if (SH.getValue().efectosID.contains(108) && objetivo.getPDVConBuff() == objetivo.getPDVMaxConBuff()) {
				pasa = false;
			}
			if (!pasa || !pelea.puedeLanzarHechizo(lanzador, hechizo1, objetivo.getCeldaPelea(),
					lanzador.getCeldaPelea().getID(), true, true)) {
				continue;
			}
			hechizosDisp.put(valor, SH.getValue());
			valor += 1;
		}
		if (!hechizosDisp.isEmpty()) {
			Random randomGenerator = new Random();
			int index = randomGenerator.nextInt(hechizosDisp.size());
			sh = hechizosDisp.get(index);
			if (sh == null) {
				return null;
			} else {
				return sh;
			}
		} else {
			return null;
		}
	}

	private static boolean tieneHechizoSinProbar(Pelea pelea, Luchador lanzador, int tempPa, Luchador objetivo) {
		Map<Integer, StatsHechizos> hechiMob = lanzador.getMob().getHechizos();
		for (Entry<Integer, StatsHechizos> SH : hechiMob.entrySet()) {
			if (objetivo == null || lanzador == null) {
				continue;
			}
			StatsHechizos hechizo1 = SH.getValue();
			if ((hechizo1 == null) || (hechizo1.getHechizo()._tipo != 0)) {
				continue;
			}
			if (SH.getValue().getCostePA() <= tempPa) {
				return true;
			}
		}
		return false;
	}

	private static boolean tieneHechizo(Pelea pelea, Luchador lanzador, int tempPa, Luchador objetivo) {
		Map<Integer, StatsHechizos> hechiMob = lanzador.getMob().getHechizos();
		for (Entry<Integer, StatsHechizos> SH : hechiMob.entrySet()) {
			if (objetivo == null || lanzador == null) {
				continue;
			}
			StatsHechizos hechizo1 = SH.getValue();
			if ((hechizo1 == null) || (hechizo1.getHechizo()._tipo != 0)) {
				continue;
			}
			if (SH.getValue().getCostePA() <= tempPa) {
				if (pelea.puedeLanzarHechizo(lanzador, hechizo1, objetivo.getCeldaPelea(),
						lanzador.getCeldaPelea().getID(), true, true)) {
					return true;
				}
			}
		}
		return false;
	}

	private static StatsHechizos mejorHechizo2(Pelea pelea, Luchador lanzador, Luchador objetivo) {
		StatsHechizos sh = null;
		int hechizoatracc = 0;
		ArrayList<StatsHechizos> posibles = new ArrayList<>();
		if (objetivo == null) {
			return null;
		}
		try {
			for (Entry<Integer, StatsHechizos> SH : lanzador.getMob().getHechizos().entrySet()) {
				StatsHechizos hechizo = SH.getValue();
				if (!pelea.puedeLanzarHechizo(lanzador, hechizo, objetivo.getCeldaPelea(), -1, true, true)) {
					continue;
				}
				boolean hechizoatrac = false;
				if (SH.getValue().efectosID.contains(6) && !lanzador.usoATR) {
					hechizoatrac = true;
					hechizoatracc = SH.getValue().getHechizoID();
				}
				int d = Camino.distanciaEntreDosCeldas(pelea.getMapaCopia(), lanzador.getCeldaPelea().getID(),
						objetivo.getCeldaPelea().getID());
				if (hechizoatrac && d <= 1) {
					continue;
				}
				posibles.add(hechizo);
			}
		} catch (Exception e) {
			return null;
		}
		if (posibles.isEmpty()) {
			return sh;
		}
		if (posibles.size() == 1) {
			return posibles.get(0);
		}
		sh = posibles.get(Formulas.getRandomValor(0, posibles.size() - 1));
		if (sh == null) {
			return null;
		}
		if (sh.getHechizoID() == hechizoatracc) {
			lanzador.usoATR = true;
		}
		return sh;
	}

	private static StatsHechizos mejorHechizo1(Pelea pelea, Luchador lanzador, Luchador objetivo) {
		StatsHechizos sh = null;
		Map<Integer, StatsHechizos> hechiMob = lanzador.getMob().getHechizos();
		if (objetivo == null) {
			return null;
		}
		boolean glifosyempuje = false;
		if (objetivo.getBuff(106) != null || objetivo.getBuff(105) != null || objetivo.tieneBuffHechizoID(20)) {
			glifosyempuje = true;
		}
		Map<Integer, StatsHechizos> hechizosDisp = new TreeMap<>();
		int valor = 0;
		boolean invis = false;
		for (Luchador lx : pelea.luchadoresDeEquipo(lanzador.getParamEquipoEnemigo())) {// 1 no lanza hechizos para ver
																						// enemigos si no hay ninguno
																						// invisible
			if (lx.esInvisible()) {
				invis = true;
				break;
			}
		}
		for (Entry<Integer, StatsHechizos> SH : hechiMob.entrySet()) {
			StatsHechizos hechizo1 = SH.getValue();
			boolean pasa = false;
			if (!glifosyempuje) {
				switch (hechizo1.getHechizo()._tipo) {
				case 0:
				case 5:
					pasa = true;
					break;
				}
				if (SH.getValue().efectosID.contains(202)) {
					if (!invis) {
						pasa = false;
					}
				}
			} else {
				if (hechizo1.getHechizo()._tipo == 5) {
					pasa = true;
				} else {
					if (SH.getValue().efectosID.contains(5)) {
						pasa = true;
					}
				}
			}
			// solo lanza desechizar cuando tiene buffs el enemigo
			boolean desechiza = false;
			boolean esdesechizo = false;
			if (SH.getValue().efectosID.contains(132)) {
				esdesechizo = true;
				if (objetivo.getBuffPelea().size() > 0) {
					for (EfectoHechizo buffs : objetivo.getBuffPelea().keySet()) {
						boolean tienenegativos = false;
						switch (buffs.getEfectoID()) {
						case 215:
						case 216:
						case 217:
						case 218:
						case 219:
						case 248:
						case 101:
						case 153:
						case 152:
						case 155:
						case 156:
						case 171:
						case 122:
						case 179:
						case 157:
						case 162:
						case 163:
						case 145:
						case 116:
						case 127:
						case 144:
						case 169:
						case 168:
							tienenegativos = true;
							break;
						}
						if (!tienenegativos) {
							if (buffs.esDesbufeable()) {
								desechiza = true;
								break;
							}
						}
					}
				}
			}
			if ((!desechiza && esdesechizo) || !pasa) {
				continue;
			}
			hechizosDisp.put(valor, SH.getValue());
			valor += 1;
		}
		if (hechizosDisp.isEmpty()) {
			return null;
		}
		Random randomGenerator = new Random();
		int index = randomGenerator.nextInt(hechizosDisp.size());
		sh = hechizosDisp.get(index);
		if (sh == null) {
			return null;
		} else {
			boolean hechizoatrac = false;
			for (Entry<Integer, StatsHechizos> SH : hechizosDisp.entrySet()) {
				if (SH.getValue().efectosID.contains(6) && !lanzador.usoATR) {
					hechizoatrac = true;
					break;
				}
			}
			if (glifosyempuje) {
				hechizoatrac = false;
			}
			int d = Camino.distanciaEntreDosCeldas(pelea.getMapaCopia(), lanzador.getCeldaPelea().getID(),
					objetivo.getCeldaPelea().getID());
			if (hechizoatrac && d > 1) {
				Map<Integer, StatsHechizos> hechizosDispn = new TreeMap<>();
				hechizosDispn.putAll(hechizosDisp);
				for (Entry<Integer, StatsHechizos> SH : hechizosDisp.entrySet()) {
					if (SH.getValue().efectosID.contains(6)) {
						sh = SH.getValue();
						break;
					}
				}
				if (!pelea.puedeLanzarHechizo(lanzador, sh, objetivo.getCeldaPelea(), lanzador.getCeldaPelea().getID(),
						true, true)) {
					sh = null;
				} else {
					lanzador.usoATR = true;
				}
				if (sh == null) {
					return null;
				}
			} else {
				Map<Integer, StatsHechizos> hechizosDispn = new TreeMap<>();
				hechizosDispn.putAll(hechizosDisp);
				for (Entry<Integer, StatsHechizos> SH : hechizosDisp.entrySet()) {
					if (SH.getValue().efectosID.contains(6)) {
						hechizosDispn.remove(SH.getKey());
					}
				}
				if (hechizosDispn.size() > 0) {
					Random randomGenerator1 = new Random();
					int index1 = randomGenerator1.nextInt(hechizosDispn.size());
					sh = hechizosDispn.get(index1);
					if (sh == null) {
						return null;
					} else {
						return sh;
					}
				} else {
					return null;
				}
			}
		}
		return sh;
	}

	private static int calculaInfluenciaCura(StatsHechizos SH) {
		int inf = 0;
		for (EfectoHechizo SE : SH.getEfectos()) {
			int efectoID = SE.getEfectoID();
			if (efectoID == 108 || efectoID == 81) {
				inf += 100 * Formulas.getMaxValor(SE.getValores());
			}
		}
		return inf;
	}

	private static int calculaInfluenciaDaÑo(StatsHechizos SH, Luchador lanzador, Luchador objetivo) {
		int influenciaTotal = 0;
		for (EfectoHechizo SE : SH.getEfectos()) {
			int inf = 0;
			switch (SE.getEfectoID()) {
			case 5:// empuja de X casillas
				inf = 500 * Formulas.getMaxValor(SE.getValores());
				break;
			case 77:// robo de PM
				inf = 1500 * Formulas.getMaxValor(SE.getValores());
				break;
			case 84:// robo de PA
				inf = 1500 * Formulas.getMaxValor(SE.getValores());
				break;
			case 89:// DaÑos % vida neutral
				inf = 200 * Formulas.getMaxValor(SE.getValores());
				break;
			case 91:// robo de vida Agua
				inf = 150 * Formulas.getMaxValor(SE.getValores());
				break;
			case 92:// robo de vida Tierra
				inf = 150 * Formulas.getMaxValor(SE.getValores());
				break;
			case 93:// robo de vida Aire
				inf = 150 * Formulas.getMaxValor(SE.getValores());
				break;
			case 94:// robo de vida fuego
				inf = 150 * Formulas.getMaxValor(SE.getValores());
				break;
			case 95:// robo de vida neutral
				inf = 150 * Formulas.getMaxValor(SE.getValores());
				break;
			case 96:// DaÑos Eau
				inf = 100 * Formulas.getMaxValor(SE.getValores());
				break;
			case 97:// DaÑos Tierra
				inf = 100 * Formulas.getMaxValor(SE.getValores());
				break;
			case 98:// DaÑos Aire
				inf = 100 * Formulas.getMaxValor(SE.getValores());
				break;
			case 99:// DaÑos fuego
				inf = 100 * Formulas.getMaxValor(SE.getValores());
				break;
			case 100:// DaÑos neutral
				inf = 100 * Formulas.getMaxValor(SE.getValores());
				break;
			case 101:// menos PA
				inf = 1000 * Formulas.getMaxValor(SE.getValores());
				break;
			case 111:// + PA
				inf = -1000 * Formulas.getMaxValor(SE.getValores());
				break;
			case 117:// + alcance
				inf = -500 * Formulas.getMaxValor(SE.getValores());
				break;
			case 121:// + DaÑos
				inf = -100 * Formulas.getMaxValor(SE.getValores());
				break;
			case 122:// + fallos criticos
				inf = 200 * Formulas.getMaxValor(SE.getValores());
				break;
			case 123:// + suerte
				inf = -200 * Formulas.getMaxValor(SE.getValores());
				break;
			case 124:// + sabiduria
				inf = -200 * Formulas.getMaxValor(SE.getValores());
				break;
			case 125:// + vitalidad
				inf = -200 * Formulas.getMaxValor(SE.getValores());
				break;
			case 126:// + inteligencia
				inf = -200 * Formulas.getMaxValor(SE.getValores());
				break;
			case 127:// menos PM
				inf = 1000 * Formulas.getMaxValor(SE.getValores());
				break;
			case 128:// + PM
				inf = -1000 * Formulas.getMaxValor(SE.getValores());
				break;
			case 131:// veneno X pdv por PA
				inf = 300 * Formulas.getMaxValor(SE.getValores());
				break;
			case 132:// desechiza
				inf = 2000;
				break;
			case 138:// + % DaÑos
				inf = -50 * Formulas.getMaxValor(SE.getValores());
				break;
			case 150:// invisibilidad
				inf = -2000; // amigos
				break;
			case 168:// -PA no esquivable
				inf = 1000 * Formulas.getMaxValor(SE.getValores());
				break;
			case 169:// -PM no esquivable
				inf = 1000 * Formulas.getMaxValor(SE.getValores());
				break;
			case 210:// resistencia
				inf = -300 * Formulas.getMaxValor(SE.getValores());
				break;
			case 211:// resistencia
				inf = -300 * Formulas.getMaxValor(SE.getValores());
				break;
			case 212:// resistencia
				inf = -300 * Formulas.getMaxValor(SE.getValores());
				break;
			case 213:// resistencia
				inf = -300 * Formulas.getMaxValor(SE.getValores());
				break;
			case 214:// resistencia
				inf = -300 * Formulas.getMaxValor(SE.getValores());
				break;
			case 215:// debilidad
				inf = 300 * Formulas.getMaxValor(SE.getValores());
				break;
			case 216:// debilidad
				inf = 300 * Formulas.getMaxValor(SE.getValores());
				break;
			case 217:// debilidad
				inf = 300 * Formulas.getMaxValor(SE.getValores());
				break;
			case 218:// debilidad
				inf = 300 * Formulas.getMaxValor(SE.getValores());
				break;
			case 219:// debilidad
				inf = 300 * Formulas.getMaxValor(SE.getValores());
				break;
			case 265:// Reduccion a los DaÑos
				inf = -250 * Formulas.getMaxValor(SE.getValores());
				break;
			case 765:
				inf = -1000;
				break;
			case 786:
				inf = -1000;
				break;
			case 106:
				inf = -900;
				break;
			}
			if (objetivo == null) {
				continue;
			}
			if (lanzador.getParamEquipoAliado() == objetivo.getParamEquipoAliado()) {
				influenciaTotal -= (inf);
			} else {
				influenciaTotal += (inf);
			}
		}
		return influenciaTotal;
	}
}
package variables;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;

import estaticos.Camino;
import estaticos.CentroInfo;
import estaticos.Emu;
import estaticos.Encriptador;
import estaticos.Formulas;
import estaticos.GestorSQL;
import estaticos.GestorSalida;
import estaticos.IAThread;
import estaticos.MundoDofus;
import estaticos.MundoDofus.Drop;
import estaticos.MundoDofus.SubArea;
import servidor.Colores;
import servidor.EntradaPersonaje.AccionDeJuego;
import servidor.Estaticos;
import variables.Hechizo.StatsHechizos;
import variables.Mapa.Celda;
import variables.MobModelo.GrupoMobs;
import variables.MobModelo.MobGrado;
import variables.Objeto.ObjetoModelo;
import variables.Personaje.Grupo;
import variables.Personaje.Stats;

public class Pelea {
	private int _id;
	public Map<Integer, Luchador> _equipo1 = new HashMap<>();
	public Map<Integer, Luchador> _equipo2 = new HashMap<>();
	public ArrayList<Luchador> _listaMuertos = new ArrayList<>();
	public Map<Integer, Personaje> _espectadores = new TreeMap<>();
	public Mapa _mapaCopia;
	public Mapa _mapaReal;
	public Luchador _luchInit1;
	public Luchador _luchInit2;
	private int _idLuchInit1;
	private int _idLuchInit2;
	public ArrayList<Celda> _celdasPos1 = new ArrayList<>();
	public ArrayList<Celda> _celdasPos2 = new ArrayList<>();
	int _estadoPelea = 0;
	private int _gremioID = -1;
	int _tipo = -1;
	private boolean _cerrado1 = false;
	private boolean _soloGrupo1 = false;
	private boolean _cerrado2 = false;
	private boolean _soloGrupo2 = false;
	private boolean _espectadorOk = true;
	private boolean _ayuda1 = false;
	private boolean _ayuda2 = false;
	private int _celdaColor2;
	private int _celdaColor1;
	private int _tempLuchadorPA;
	private int _tempLuchadorPM;
	private int _tempLuchadorPAusados;
	private int _tempLuchadorPMusados;
	String _tempAccion = "";
	public List<Glifo> _glifos = new ArrayList<>();
	public List<Trampa> _trampas = new ArrayList<>();
	public GrupoMobs _mobGrupo;
	// private int _capturadorGanador = -1;
	// private PiedraDeAlma _piedraAlma;
	Recaudador _Recaudador;
	Prisma _Prisma;
	public ArrayList<Integer> _mobsMuertosReto = new ArrayList<>();
	public ArrayList<Integer> _muertesEquipo1 = new ArrayList<>();
	public ArrayList<Integer> _muertesEquipo2 = new ArrayList<>();
	public List<Luchador> _inicioLucEquipo1 = new ArrayList<>();
	public List<Luchador> _inicioLucEquipo2 = new ArrayList<>();
	public Map<Integer, Celda> _posinicial = new TreeMap<>();
	private String _listadefensores = "";
	private int _numeroInvos = 0;
	public boolean enKoliseo = false;
	public boolean enTorneo = false;
	// retos
	public Map<Integer, String> _stringReto = new TreeMap<>();
	int _idMobReto = 0;
	int _luchMenorNivelReto = 0;
	private int _elementoReto = 0;
	int _cantMobsMuerto = 0;
	public Map<Integer, Integer> _ordenNivelMobs = new TreeMap<>();
	public Map<Integer, Luchador> _ordenLuchMobs = new TreeMap<>();
	public Map<Integer, Integer> _retos = new TreeMap<>();
	private int _estrellas = 0;
	public boolean peleainvasion = false;
	// fin retos
	// timers
	private long _tiempoInicio = 0L;
	public long _tiempoInicioTurno = 0L;
	public long _tiempoInicioPelea = 0L;
	public boolean peleaEmpezada = false;
	static int tiempoPPrisma = 60000;
	static int tiempoPRecau = 50000;
	private static int tiempoPPVM = 45000;
	private static int tiempoPPVP = 45000;
	public Timer TimersPelea = null;
	public TimerTask actTimerTask = null;
	public LinkedList<Luchador> turnosPelea = new LinkedList<>();
	public Luchador LucActual = null;
	public IAThread _IAThreads = null;
	// fintimers
	// private ArrayList<Luchador> _capturadores = new ArrayList<Luchador>(8);
	// private boolean _esCapturable = false;
	// Turnos
	private int purgaTimer = 0;
	public Personaje acabaMaitre = null;

	public int turnoActual = 1;
	public boolean activarPasivaPar;
	public boolean activarPasivaImpar;


	private synchronized void startTimer(int time, final boolean isTurn) {
		if (purgaTimer >= 40 && TimersPelea != null) {
			TimersPelea.cancel();
			TimersPelea.purge();
			TimersPelea = null;
			TimersPelea = new Timer();
			actTimerTask = null;
			purgaTimer = 0;
		}
		purgaTimer += 1;
		Pelea pelea = this;
		try {
			if (TimersPelea == null) {
				TimersPelea = new Timer();
			}
			TimersPelea.schedule(actTimerTask = new TimerTask() {
				@Override
				public void run() {
					try {
						if (_estadoPelea == 4) {
							this.cancel();
							return;
						}
						if (isTurn) {
							this.cancel();
							if (LucActual != null) {
								if (LucActual.getPersonaje() != null && LucActual.getMob() == null) {
									if (!LucActual.realizaaccion) {
										LucActual.penalizado += 1;
										if (LucActual.penalizado >= 5) {
											GestorSalida.ENVIAR_cs_CHAT_MENSAJE_A_PELEA(pelea, 7,
													"El personaje " + LucActual.getNombreLuchador()
															+ " ha sido penalizado y se le retirÓ del combate.",
													Colores.VERDE);
											agregarAMuertos(LucActual, LucActual, "");
											// Luchactual.getPersonaje().resetVariables();
											return;
										} else {
											GestorSalida.ENVIAR_cs_CHAT_MENSAJE_A_PELEA(pelea, 7, "Si el personaje "
													+ LucActual.getNombreLuchador()
													+ " no realiza una acciÓn en los prÓximos turnos serÁ penalizado y se le retirarÁ del combate.",
													Colores.VERDE);
										}
									}
								}
							}
							finTurno("timer2");
						} else {
							this.cancel();
							if (!peleaEmpezada) {
								iniciarPelea();
							}
						}
						return;
					} catch (Exception e) {
						Emu.creaLogs(e);
						this.cancel();
						return;
					}
				}
			}, time);
		} catch (Exception e) {
			Emu.creaLogs(e);
		}
	}

	private void setActTimerTask(TimerTask actTimerTasks) {
		actTimerTask = actTimerTasks;
	}

	private TimerTask getActTimerTask() {
		return actTimerTask;
	}

	public int getTempPA() {
		return _tempLuchadorPA;
	}

	public int getTempPM() {
		return _tempLuchadorPM;
	}

	public void setEmpezoPelea(boolean fightStarted) {
		peleaEmpezada = fightStarted;
	}

	public Map<Integer, Integer> getRetos() {
		return _retos;
	}

	void putStringReto(int id, String reto) {
		_stringReto.put(id, reto);
	}

	public void setListaDefensores(String str) {
		_listadefensores = str;
	}

	public String getListaDefensores() {
		return _listadefensores;
	}

	public Map<Integer, Celda> getPosInicial() {
		return _posinicial;
	}

	public int getNumeroInvos() {
		return _numeroInvos;
	}

	public void setIDMobReto(int mob) {
		_idMobReto = mob;
	}

	public int getIDMobReto() {
		return _idMobReto;
	}

	public Luchador getLuchadorTurno() {
		return LucActual;
	}

	Pelea(int tipo, int id, Mapa mapa, Personaje init1, Personaje init2) {
		try {
			_tipo = tipo;
			_id = id;
			_mapaCopia = mapa.copiarMapa();
			_mapaReal = mapa;
			init1.penaliza = false;
			init2.penaliza = false;
			int id1 = init1.getID();
			int id2 = init2.getID();
			_luchInit1 = new Luchador(this, init1);
			_luchInit2 = new Luchador(this, init2);
			if (init1 != null) {
				GestorSalida.enviar(init1, "#Gc");
			}
			if (init2 != null) {
				GestorSalida.enviar(init2, "#Gc");
			}
			_idLuchInit1 = _luchInit1.getID();
			_idLuchInit2 = _luchInit2.getID();
			_equipo1.put(id1, _luchInit1);
			_equipo2.put(id2, _luchInit2);
			_tiempoInicioPelea = System.currentTimeMillis();
			startTimer(tiempoPPVP, false);
			GestorSalida.ENVIAR_GJK_UNIRSE_PELEA(this, 3, 2, false, true, false, tiempoPPVP, _tipo);
			GestorSalida.ENVIAR_ILF_CANTIDAD_DE_VIDA(init1, 0);
			GestorSalida.ENVIAR_ILF_CANTIDAD_DE_VIDA(init2, 0);
			Random equipos = new Random();
			if (equipos.nextBoolean()) {
				_celdasPos1 = analizarPosiciones(0);
				_celdasPos2 = analizarPosiciones(1);
				GestorSalida.ENVIAR_GP_POSICIONES_PELEA(this, 1, _mapaCopia.getLugaresString(), 0);
				GestorSalida.ENVIAR_GP_POSICIONES_PELEA(this, 2, _mapaCopia.getLugaresString(), 1);
				_celdaColor1 = 0;
				_celdaColor2 = 1;
			} else {
				_celdasPos1 = analizarPosiciones(1);
				_celdasPos2 = analizarPosiciones(0);
				_celdaColor1 = 1;
				_celdaColor2 = 0;
				GestorSalida.ENVIAR_GP_POSICIONES_PELEA(this, 1, _mapaCopia.getLugaresString(), 1);
				GestorSalida.ENVIAR_GP_POSICIONES_PELEA(this, 2, _mapaCopia.getLugaresString(), 0);
			}
			GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 3, 950, id1 + "", id1 + "," + 8 + ",0");
			GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 3, 950, id1 + "", id1 + "," + 3 + ",0");
			GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 3, 950, id2 + "", id2 + "," + 8 + ",0");
			GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 3, 950, id2 + "", id2 + "," + 3 + ",0");
			_luchInit1.setCeldaPelea(getCeldaRandom(_celdasPos1));
			_luchInit2.setCeldaPelea(getCeldaRandom(_celdasPos2));
			_mapaReal.borrarJugador(init1);
			_mapaReal.borrarJugador(init2);
			_mapaCopia.addLuchador(_luchInit1);
			_mapaCopia.addLuchador(_luchInit2);
			init1.setPelea(this);
			_luchInit1.setEquipoBin(0);
			init2.setPelea(this);
			_luchInit2.setEquipoBin(1);
			Mapa mapaTemp = init1.getMapa();
			GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(mapaTemp, id1);
			GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(mapaTemp, id2);
			if (_tipo == 1) {
				GestorSalida.ENVIAR_Gc_MOSTRAR_ESPADA_EN_MAPA(mapaTemp, 0, id1, id2, init1.getCelda().getID(),
						"0;" + init1.getAlineacion(), init2.getCelda().getID(), "0;" + init2.getAlineacion());
			} else {
				GestorSalida.ENVIAR_Gc_MOSTRAR_ESPADA_EN_MAPA(mapaTemp, 0, id1, id2, init1.getCelda().getID(), "0;-1",
						init2.getCelda().getID(), "0;-1");
			}
			GestorSalida.ENVIAR_Gt_AGREGAR_NOMBRE_ESPADA(mapaTemp, id1, _luchInit1);
			GestorSalida.ENVIAR_Gt_AGREGAR_NOMBRE_ESPADA(mapaTemp, id2, _luchInit2);
			GestorSalida.ENVIAR_GM_LUCHADOR_A_TODA_PELEA(this, 7, _mapaCopia);
			setEstado(2);
		} catch (Exception e) {
			Emu.creaLogs(e);
			return;
		}
	}

	Pelea(int id, Mapa mapa, Personaje perso, Prisma prisma) {
		prisma.setEstadoPelea(0);
		prisma.setPeleaID(id);
		prisma.setPelea(this);
		if (perso.enTorneo != 0) {
			Estaticos.perderTorneo(perso);
		}
		_tipo = 2;
		_id = id;
		_mapaCopia = mapa.copiarMapa();
		_mapaReal = mapa;
		_luchInit1 = new Luchador(this, perso);
		if (perso != null) {
			GestorSalida.enviar(perso, "#Gc");
		}
		_idLuchInit1 = _luchInit1.getID();
		_Prisma = prisma;
		_idLuchInit2 = _Prisma.getID();
		int id1 = perso.getID();
		_equipo1.put(id1, _luchInit1);
		Luchador lPrisma = new Luchador(this, prisma);
		_equipo2.put(-1, lPrisma);
		_tiempoInicioPelea = System.currentTimeMillis();
		startTimer(tiempoPPrisma, false);
		GestorSalida.ENVIAR_GJK_UNIRSE_PELEA(this, 1, 2, false, true, false, tiempoPPrisma, _tipo);
		GestorSalida.ENVIAR_ILF_CANTIDAD_DE_VIDA(perso, 0);
		Random equipos = new Random();
		if (equipos.nextBoolean()) {
			_celdasPos1 = analizarPosiciones(0);
			_celdasPos2 = analizarPosiciones(1);
			GestorSalida.ENVIAR_GP_POSICIONES_PELEA(this, 1, _mapaCopia.getLugaresString(), 0);
			_celdaColor1 = 0;
			_celdaColor2 = 1;
		} else {
			_celdasPos1 = analizarPosiciones(1);
			_celdasPos2 = analizarPosiciones(0);
			_celdaColor1 = 1;
			_celdaColor2 = 0;
			GestorSalida.ENVIAR_GP_POSICIONES_PELEA(this, 1, _mapaCopia.getLugaresString(), 1);
		}
		GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 3, 950, id1 + "", id1 + "," + 8 + ",0");
		GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 3, 950, id1 + "", id1 + "," + 3 + ",0");
		List<Entry<Integer, Luchador>> equipo2 = new ArrayList<>();
		equipo2.addAll(_equipo2.entrySet());
		for (Entry<Integer, Luchador> entry : equipo2) {
			Luchador lprisma = entry.getValue();
			Celda celdaRandom = getCeldaRandom(_celdasPos2);
			if (celdaRandom == null) {
				_equipo2.remove(lprisma.getID());
				continue;
			}
			GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 3, 950, lprisma.getID() + "", lprisma.getID() + "," + 8 + ",0");
			GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 3, 950, lprisma.getID() + "", lprisma.getID() + "," + 3 + ",0");
			lprisma.setCeldaPelea(celdaRandom);
			_mapaCopia.addLuchador(lprisma);
			lprisma.setEquipoBin(1);
			lprisma.fullPDV();
		}
		_luchInit1.setCeldaPelea(getCeldaRandom(_celdasPos1));
		_mapaReal.borrarJugador(perso);
		_mapaCopia.addLuchador(_luchInit1);
		_luchInit1.getPersonaje().setPelea(this);
		_luchInit1.setEquipoBin(0);
		Mapa mapaTemp = _luchInit1.getPersonaje().getMapa();
		GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(mapaTemp, id1);
		GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(mapaTemp, prisma.getID());
		GestorSalida.ENVIAR_Gc_MOSTRAR_ESPADA_EN_MAPA(mapaTemp, 0, id1, prisma.getID(), perso.getCelda().getID(),
				"0;" + perso.getAlineacion(), prisma.getCelda(), "0;" + prisma.getAlineacion());
		GestorSalida.ENVIAR_Gt_AGREGAR_NOMBRE_ESPADA(mapaTemp, id1, _luchInit1);
		for (Luchador f : _equipo2.values()) {
			GestorSalida.ENVIAR_Gt_AGREGAR_NOMBRE_ESPADA(mapaTemp, prisma.getID(), f);
		}
		GestorSalida.ENVIAR_GM_LUCHADOR_A_TODA_PELEA(this, 7, _mapaCopia);
		setEstado(2);
		String str = "";
		if (_Prisma != null) {
			str = prisma.getMapa() + "|" + prisma.getX() + "|" + prisma.getY();
		}
		for (Personaje z : MundoDofus.getPJsEnLinea()) {
			if ((z == null) || (z.getAlineacion() != prisma.getAlineacion())) {
				continue;
			}
			GestorSalida.ENVIAR_CA_MENSAJE_ATAQUE_PRISMA(z, str);
		}
	}

	boolean esAngel = false;

	Pelea(int id, Mapa mapa, Personaje perso, GrupoMobs grupoMob, int tipo) {
		_tipo = tipo;
		_id = id;
		_mapaCopia = mapa.copiarMapa();
		_mapaReal = mapa;
		if (perso.enTorneo != 0) {
			Estaticos.perderTorneo(perso);
		}
		if (MundoDofus.antesmap != null) {
			if (MundoDofus.antesmap == mapa) {
				esAngel = true;
				if (MundoDofus.angel) {
					GestorSalida.ENVIAR_MENSAJE_A_TODOS_CHAT_COLOR(
							perso.getNombre() + " ha encontrado y ha desafiado al Ángel de la creaci�n. <b>["
									+ mapa.getCoordX() + "," + mapa.getCoordY() + "]</b>",
							Colores.AZUL);
				} else {
					GestorSalida.ENVIAR_MENSAJE_A_TODOS_CHAT_COLOR(
							perso.getNombre() + " ha encontrado y ha desafiado al demonio. <b>[" + mapa.getCoordX()
									+ "," + mapa.getCoordY() + "]</b>",
							Colores.AZUL);
				}
				MundoDofus.antesmap = null;
			}
		}
		if (this.getMapaCopia().getID() == 27105) {
			GestorSalida.enviar(perso, "#GS14");
		}
		int id1 = perso.getID();
		_luchInit1 = new Luchador(this, perso);
		if (perso != null) {
			GestorSalida.enviar(perso, "#Gc");
		}
		_idLuchInit1 = _luchInit1.getID();
		_mobGrupo = grupoMob;
		_idLuchInit2 = _mobGrupo.getID();
		_equipo1.put(id1, _luchInit1);
		for (Entry<Integer, MobGrado> entry : grupoMob.getMobs().entrySet()) {
			entry.getValue().setIdEnPelea(entry.getKey());
			Luchador mob = new Luchador(this, entry.getValue());
			_equipo2.put(entry.getKey(), mob);
		}
		try {
			if (tipo == 4) {
				_estrellas = grupoMob.getEstrellas();
				grupoMob.setEstrellas(1);
			}
			_tiempoInicioPelea = System.currentTimeMillis();
			startTimer(tiempoPPVM, false);
			GestorSalida.ENVIAR_GJK_UNIRSE_PELEA(this, 1, 2, false, true, false, tiempoPPVM, _tipo);
			GestorSalida.ENVIAR_ILF_CANTIDAD_DE_VIDA(perso, 0);
			if (_equipo2.size() <= 2) {
				_celdasPos1 = analizarPosiciones(0);
				_celdasPos2 = analizarPosiciones(1);
				GestorSalida.ENVIAR_GP_POSICIONES_PELEA(this, 1, _mapaCopia.getLugaresString(), 0);
				_celdaColor1 = 0;
				_celdaColor2 = 1;
			} else {
				Random equipos = new Random();
				if (equipos.nextBoolean()) {
					_celdasPos1 = analizarPosiciones(0);
					_celdasPos2 = analizarPosiciones(1);
					GestorSalida.ENVIAR_GP_POSICIONES_PELEA(this, 1, _mapaCopia.getLugaresString(), 0);
					_celdaColor1 = 0;
					_celdaColor2 = 1;
				} else {
					_celdasPos1 = analizarPosiciones(1);
					_celdasPos2 = analizarPosiciones(0);
					_celdaColor1 = 1;
					_celdaColor2 = 0;
					GestorSalida.ENVIAR_GP_POSICIONES_PELEA(this, 1, _mapaCopia.getLugaresString(), 1);
				}
			}
			StringBuilder packet = new StringBuilder("");
			String s2 = id1 + "," + 8 + ",0";
			packet.append("GA;" + 950 + ";" + id1);
			if (!s2.equals("")) {
				packet.append(";" + s2);
			}
			packet.append((char) 0x00);
			s2 = id1 + "," + 3 + ",0";
			packet.append("GA;" + 950 + ";" + id1);
			if (!s2.equals("")) {
				packet.append(";" + s2);
			}
			packet.append((char) 0x00);
			List<Entry<Integer, Luchador>> equipo2 = new ArrayList<>();
			equipo2.addAll(_equipo2.entrySet());
			for (Entry<Integer, Luchador> entry : equipo2) {
				Luchador mob = entry.getValue();
				Celda celdaRandom = getCeldaRandom(_celdasPos2);
				if (celdaRandom == null) {
					_equipo2.remove(mob.getID());
					continue;
				}
				String s21 = mob.getID() + "," + 8 + ",0";
				packet.append("GA;" + 950 + ";" + mob.getID());
				if (!s21.equals("")) {
					packet.append(";" + s21);
				}
				packet.append((char) 0x00);
				s21 = mob.getID() + "," + 3 + ",0";
				packet.append("GA;" + 950 + ";" + mob.getID());
				if (!s21.equals("")) {
					packet.append(";" + s21);
				}
				mob.setCeldaPelea(celdaRandom);
				_mapaCopia.addLuchador(mob);
				mob.setEquipoBin(1);
				mob.fullPDV();
			}
			Celda celda = getCeldaRandom(_celdasPos1);
			if (celda == null) {
				System.out.println("Celda bug en mapa: " + mapa.getID());
				celda = getCeldaRandom(_celdasPos1);
				if (celda != null) {
					System.out.println("Nueva celda " + celda.getID());
				}
			}
			GestorSalida.ENVIAR_A_EQUIPO(packet.toString(), 3, this);
			_mapaReal.borrarJugador(perso);
			_luchInit1.setCeldaPelea(celda);
			_mapaCopia.addLuchador(_luchInit1);
			perso.setPelea(this);
			_luchInit1.setEquipoBin(0);
			Mapa mapaTemp = perso.getMapa();
			StringBuilder packet2 = new StringBuilder("");
			packet2.append("GM|-" + id1).append((char) 0x00);
			packet2.append("GM|-" + grupoMob.getID());
			GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(mapaTemp, packet2.toString());
			if (tipo == 4) {
				GestorSalida.ENVIAR_Gc_MOSTRAR_ESPADA_EN_MAPA(mapaTemp, 4, id1, grupoMob.getID(),
						perso.getCelda().getID(), "0;-1", grupoMob.getCeldaID() - 1, "1;-1");
				GestorSalida.ENVIAR_Gt_AGREGAR_NOMBRE_ESPADA(mapaTemp, id1, _luchInit1);
				for (Luchador luchador : _equipo2.values()) {
					GestorSalida.ENVIAR_Gt_AGREGAR_NOMBRE_ESPADA(mapaTemp, grupoMob.getID(), luchador);
				}
			}
		} catch (Exception e) {
			Emu.creaLogs(e);
		}
		GestorSalida.ENVIAR_GM_LUCHADOR_A_TODA_PELEA(this, 7, _mapaCopia);
		setEstado(2);
	}

	Pelea(int id, Mapa mapa, Personaje perso, Recaudador recaudador) {
		setGremioID(recaudador.getGremioID());
		recaudador.setEstadoPelea(1);
		recaudador.setPeleaID(id);
		recaudador.setPelea(this);
		_tipo = 5;
		_id = id;
		_mapaCopia = mapa.copiarMapa();
		_mapaReal = mapa;
		_luchInit1 = new Luchador(this, perso);
		if (perso != null) {
			GestorSalida.enviar(perso, "#Gc");
		}
		_idLuchInit1 = _luchInit1.getID();
		_Recaudador = recaudador;
		_idLuchInit2 = _Recaudador.getID();
		int id1 = perso.getID();
		_equipo1.put(id1, _luchInit1);
		Luchador lRecaudador = new Luchador(this, recaudador);
		_equipo2.put(-1, lRecaudador);
		_tiempoInicioPelea = System.currentTimeMillis();
		startTimer(tiempoPRecau, false);
		GestorSalida.ENVIAR_GJK_UNIRSE_PELEA(this, 1, 2, false, true, false, tiempoPRecau, _tipo);
		GestorSalida.ENVIAR_ILF_CANTIDAD_DE_VIDA(perso, 0);
		Random equipos = new Random();
		if (equipos.nextBoolean()) {
			_celdasPos1 = analizarPosiciones(0);
			_celdasPos2 = analizarPosiciones(1);
			GestorSalida.ENVIAR_GP_POSICIONES_PELEA(this, 1, _mapaCopia.getLugaresString(), 0);
			_celdaColor1 = 0;
			_celdaColor2 = 1;
		} else {
			_celdasPos1 = analizarPosiciones(1);
			_celdasPos2 = analizarPosiciones(0);
			_celdaColor1 = 1;
			_celdaColor2 = 0;
			GestorSalida.ENVIAR_GP_POSICIONES_PELEA(this, 1, _mapaCopia.getLugaresString(), 1);
		}
		GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 3, 950, id1 + "", id1 + "," + 8 + ",0");
		GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 3, 950, id1 + "", id1 + "," + 3 + ",0");
		List<Entry<Integer, Luchador>> equipo2 = new ArrayList<>();
		equipo2.addAll(_equipo2.entrySet());
		for (Entry<Integer, Luchador> entry : equipo2) {
			Luchador recau = entry.getValue();
			Celda celdaRandom = getCeldaRandom(_celdasPos2);
			if (celdaRandom == null) {
				_equipo2.remove(recau.getID());
				continue;
			}
			GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 3, 950, recau.getID() + "", recau.getID() + "," + 8 + ",0");
			GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 3, 950, recau.getID() + "", recau.getID() + "," + 3 + ",0");
			recau.setCeldaPelea(celdaRandom);
			_mapaCopia.addLuchador(recau);
			recau.setEquipoBin(1);
			recau.fullPDV();
		}
		_luchInit1.setCeldaPelea(getCeldaRandom(_celdasPos1));
		_mapaReal.borrarJugador(perso);
		_mapaCopia.addLuchador(_luchInit1);
		perso.setPelea(this);
		_luchInit1.setEquipoBin(0);
		Mapa mapaTemp = perso.getMapa();
		GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(mapaTemp, _idLuchInit1);
		GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(mapaTemp, recaudador.getID());
		GestorSalida.ENVIAR_Gc_MOSTRAR_ESPADA_EN_MAPA(mapaTemp, 5, id1, recaudador.getID(), perso.getCelda().getID(),
				"0;-1", recaudador.getCeldalID(), "3;-1");
		GestorSalida.ENVIAR_Gt_AGREGAR_NOMBRE_ESPADA(mapaTemp, id1, _luchInit1);
		for (Luchador luchador : _equipo2.values()) {
			GestorSalida.ENVIAR_Gt_AGREGAR_NOMBRE_ESPADA(mapaTemp, recaudador.getID(), luchador);
		}
		GestorSalida.ENVIAR_GM_LUCHADOR_A_TODA_PELEA(this, 7, _mapaCopia);
		setEstado(2);
		String str = "";
		if (_Recaudador != null) {
			str = "A" + _Recaudador.getN1() + "," + _Recaudador.getN2() + "|.|" + _Recaudador.getMapaID() + "|"
					+ _Recaudador.getCeldalID();
		}
		for (Personaje z : MundoDofus.getGremio(_gremioID).getPjMiembros()) {
			if (z == null) {
				continue;
			}
			if (z.enLinea()) {
				GestorSalida.ENVIAR_gITM_INFO_RECAUDADOR(z, Recaudador.analizarRecaudadores(z.getGremio().getID()));
				Recaudador.analizarAtaque(z, _gremioID);
				Recaudador.analizarDefensa(z, _gremioID);
				GestorSalida.ENVIAR_gA_MENSAJE_SOBRE_RECAUDADOR(z, str);
			}
		}
	}

	public Mapa getMapaCopia() {
		return _mapaCopia;
	}

	public Mapa getMapaReal() {
		return _mapaReal;
	}

	public List<Trampa> getTrampas() {
		List<Trampa> sinRep = new ArrayList<>();
		List<Trampa> conRep = new ArrayList<>();
		List<Trampa> trampas = new ArrayList<>();
		trampas.addAll(_trampas);
		for (Trampa tr : trampas) {
			if (tr == null) {
				continue;
			}
			boolean repulsiva = false;
			if (tr._trampaHechizo.efectosID.contains(5)) {
				repulsiva = true;
			}
			if (repulsiva) {
				conRep.add(tr);
			} else {
				sinRep.add(tr);
			}
		}
		sinRep.addAll(conRep);
		_trampas.clear();
		_trampas.addAll(sinRep);
		return _trampas;
	}

	public List<Trampa> getTrampasT() {
		return _trampas;
	}

	public List<Glifo> getGlifos() {
		return _glifos;
	}

	private Celda getCeldaRandom(List<Celda> cells) {
		Random rand = new Random();
		Celda cell;
		if (cells.isEmpty()) {
			return null;
		}
		int limit = 0;
		do {
			int id = rand.nextInt(cells.size());
			cell = cells.get(id);
			limit++;
		} while ((cell == null || !cell._mapa.getLuchadores(cell.getID()).isEmpty()) && limit < 80);
		if (limit == 80) {
			return null;
		}
		return cell;
	}

	private ArrayList<Celda> analizarPosiciones(int num) {
		return Encriptador.analizarInicioCelda(_mapaCopia, num);
	}

	public int getID() {
		return _id;
	}

	public ArrayList<Luchador> luchadoresDeEquipo(int equipos) {
		ArrayList<Luchador> luchadores = new ArrayList<>(); // 5 = espectador y equipo 1 -6 = espectador y
																	// equipo 2 -7 = todos
		if (equipos - 4 >= 0) {// 4 = espectador
			for (Entry<Integer, Personaje> entry : _espectadores.entrySet()) {
				luchadores.add(new Luchador(this, entry.getValue()));
			}
			equipos -= 4;
		}
		if (equipos - 2 >= 0) {// 3 = ambos equipos, 2 = equipo 2 (atacante, mob, recaudador)
			for (Entry<Integer, Luchador> entry : _equipo2.entrySet()) {
				luchadores.add(entry.getValue());
			}
			equipos -= 2;
		}
		if (equipos - 1 >= 0) {// 1 = equipo 1
			for (Entry<Integer, Luchador> entry : _equipo1.entrySet()) {
				luchadores.add(entry.getValue());
			}
		}
		return luchadores;
	}

	public synchronized void cambiarLugar(Personaje perso, int celda) {
		Luchador luchador = getLuchadorPorPJ(perso);
		if (luchador == null) {
			return;
		}
		int equipo = getParamEquipo(perso.getID()) - 1;
		if (getEstado() != 2 || celdaOcupada(celda) || perso.estaListo()
				|| (equipo == 0 && !grupoCeldasContiene(_celdasPos1, celda))
				|| (equipo == 1 && !grupoCeldasContiene(_celdasPos2, celda))) {
			return;
		}
		if (_mapaCopia.getCelda(celda) == null) {
			return;
		}
		_mapaCopia.removerLuchador(luchador);
		luchador.setCeldaPelea(_mapaCopia.getCelda(celda));
		_mapaCopia.addLuchador(luchador);
		GestorSalida.ENVIAR_GIC_CAMBIAR_POS_PELEA(this, 3, _mapaCopia, perso.getID(), celda);
	}

	public boolean celdaOcupada(int celda) {
		Celda celd = _mapaCopia.getCelda(celda);
		if (celd == null) {
			return true;
		}
		return celd._mapa.getLuchadores(celda).size() > 0;
	}

	private boolean grupoCeldasContiene(ArrayList<Celda> celdas, int celda) {
		for (Celda element : celdas) {
			if (element.getID() == celda) {
				return true;
			}
		}
		return false;
	}

	public void verificaTodosListos() {
		boolean val = true;
		for (int a = 0; a < _equipo1.size(); a++) {
			if (!_equipo1.get(_equipo1.keySet().toArray()[a]).getPersonaje().estaListo()) {
				val = false;
			}
		}
		if (_tipo != 4 && _tipo != 5 && _tipo != 2 && _tipo != 3) {
			for (int a = 0; a < _equipo2.size(); a++) {
				if (!_equipo2.get(_equipo2.keySet().toArray()[a]).getPersonaje().estaListo()) {
					val = false;
				}
			}
		}
		if (_tipo == 5 || _tipo == 2) {
			val = false;
		}
		if (peleainvasion || esAngel) {
			val = false;
		}
		if (_mapaCopia.getID() == 27105) {
			val = false;
		}
		if (val) {
			iniciarPelea();
		}
	}
	// public boolean pelearx = false;

	public void iniciarPelea() {
		try {
			setEmpezoPelea(true);
			if (getActTimerTask() != null) {
				getActTimerTask().cancel();
			}
			setActTimerTask(null);
			if (_estadoPelea >= 3) {
				return;
			}
			_tiempoInicioTurno = 0;
			_tiempoInicio = System.currentTimeMillis();
			_estadoPelea = 3;
			if (_luchInit1.getPersonaje() != null) {
				if (_luchInit1.getPersonaje().liderMaitre) {
					for (Personaje pjx : _luchInit1.getPersonaje().getGrupo().getPersos()) {
						if (pjx == _luchInit1.getPersonaje()) {
							_luchInit1.getPersonaje().liderMaitre = false;
							continue;
						}
						pjx.esMaitre = false;
					}
				}
			}
			_inicioLucEquipo1.addAll(luchadoresDeEquipo(1));
			_inicioLucEquipo2.addAll(luchadoresDeEquipo(2));
			switch (_tipo) {
			case 2: // PRISMAS
				_Prisma.setEstadoPelea(-2);
				for (Personaje z : MundoDofus.getPJsEnLinea()) {
					if (z == null) {
						continue;
					}
					if (z.getAlineacion() == _Prisma.getAlineacion()) {
						Prisma.analizarAtaque(z);
						Prisma.analizarDefensa(z);
					}
				}
				break;
			case 4:
				if (_equipo2.size() > 0) {
					_equipo2.get(_equipo2.keySet().toArray()[0]).getMob().getModelo().getAlineacion();
				}
				break;
			case 5: // RECAUDADORES
				_Recaudador.setEstadoPelea(2);
				for (Personaje z : MundoDofus.getGremio(_gremioID).getPjMiembros()) {
					if (z == null) {
						continue;
					}
					if (z.enLinea()) {
						GestorSalida.ENVIAR_gITM_INFO_RECAUDADOR(z,
								Recaudador.analizarRecaudadores(z.getGremio().getID()));
						Recaudador.analizarAtaque(z, _gremioID);
						Recaudador.analizarDefensa(z, _gremioID);
					}
				}
				break;
			}
			GestorSalida.ENVIAR_Gc_BORRAR_BANDERA_EN_MAPA(_mapaReal, _idLuchInit1);
			GestorSalida.ENVIAR_GIC_UBICACION_LUCHADORES_INICIAR(this, 7);
			GestorSalida.ENVIAR_GS_EMPEZAR_COMBATE_EQUIPOS(this, 7);
			iniciarOrdenLuchadores();
			GestorSalida.ENVIAR_GTL_ORDEN_JUGADORES(this, 7);
			int random = Formulas.getRandomValor(1, 3);
			if ((_tipo == 4 || _tipo == 3) && random < 2) {
				ArrayList<Integer> retosPosibles = new ArrayList<>();
				for (int i = 1; i < 51; i++) {
					if (i == 13 || i == 16 || i == 19 || i == 26 || i == 27 || i == 38 || i == 48 || i == 49) {
						continue;
					}
					if (CentroInfo.esRetoPosible1(i, this)) {
						retosPosibles.add(i);
					}
				}
				int idReto = retosPosibles.get(Formulas.getRandomValor(0, retosPosibles.size() - 1));
				_retos.put(idReto, 0);
				GestorSalida.ENVIAR_Gd_RETO_A_LOS_LUCHADORES(this, MundoDofus.getReto(idReto).getDetalleReto(this));
			}
			/*
			 * boolean boostea = false; if
			 * (MundoDofus.mapasBoost.contains(_mapaReal.getID())) { boostea = true; }
			 */
			// boolean ayudado = false;
			/*
			 * for (Luchador luchador : luchadoresDeEquipo(3)) { if (pelearx) break;
			 * Personaje perso = luchador.getPersonaje(); if(perso == null) continue; if
			 * (perso._resets == 3 && !perso.accionesPJ.contains(1)) { pelearx = true;
			 * break; } } if (pelearx) { if (_tipo == 4)
			 * GestorSalida.ENVIAR_cs_CHAT_MENSAJE_A_PELEA(this,
			 * "Los monstruos de esta pelea se han convertido en Megas!!!", Colores.ROJO);
			 * else pelearx = false; } if (pelearx && _tipo == 4) { for (Luchador luchador :
			 * luchadoresDeEquipo(3)) { if (getMapaCopia() != null && luchador.getMob() !=
			 * null) { buffeaMega(luchador); } } }
			 */




			for (Luchador luchador : luchadoresDeEquipo(3)) {
				if (luchador.getPersonaje() != null) {

                  if (luchador.getPersonaje().buffClase == 1) {
						luchador.addBuff(125, 1000, 1000, 1, false, 1201, "1000;-1;-1;1;0;0d0+1000", luchador, false, true);
						luchador.addBuff(265, 10, 1000, 1, false, 1201, "10;-1;-1;1;0;0d0+10", luchador, false, true);
					}
					if (luchador.getPersonaje().buffClase == 2) {
						//luchador.addBuff(128, 1, 1000, 1, false, 1201, "1;-1;-1;1;0;0d0+1", luchador, false, true);
						//luchador.addBuff(111, 1, 1000, 1, false, 1201, "1;-1;-1;2;0;0d0+1", luchador, false, true);
						luchador.addBuff(178, 40, 1000, 1, false, 1201, "40;-1;-1;2;0;0d0+40", luchador, false, true);
					}
					if (luchador.getPersonaje().buffClase == 3) {
						//luchador.addBuff(128, 1, 1000, 1, false, 1201, "1;-1;-1;1;0;0d0+1", luchador, false, true);
						luchador.addBuff(111, 1, 1000, 1, false, 1201, "1;-1;-1;2;0;0d0+1", luchador, false, true);
					}
					
					//RAID DEBUFFO INICIO PELEA
					if (getMapaCopia().getID() == 31006) {
						luchador.addBuff(131, 1, 3, 1, true, 228, "1;10000;-1;3;0;0d0+10000", luchador, false, true);
						
						
						
						
						
					}
					//RAID DEBVUFFO INICIO PELEA
					
					
				}


				if (getMapaCopia() != null && luchador.getMob() != null) {
					int desaf = _luchInit1.getPersonaje().desafdiario;
					if (getMapaCopia().getID() == 30063) {
						int porc = desaf * 20;
						long da = Math.round(desaf * 1.5);
						long res = Math.round(desaf * 1.3);
						luchador.addBuff(125, porc, 100, 1, false, 155, "81;90;-1;20;0;1d10+80", luchador, false, true);// vida
						luchador.addBuff(138, (int) da, 100, 1, false, 1201, "50;-1;-1;0;0;0d0+50", luchador, false,
								true);// daÑo 45%
						luchador.addBuff(210, (int) res, 100, 1, false, 1201, "20;-1;-1;4;0;0d0+10", luchador, false,
								true);// resis
						luchador.addBuff(211, (int) res, 100, 1, false, 1201, "20;-1;-1;4;0;0d0+10", luchador, false,
								true);
						luchador.addBuff(212, (int) res, 100, 1, false, 1201, "20;-1;-1;4;0;0d0+10", luchador, false,
								true);
						luchador.addBuff(213, (int) res, 100, 1, false, 1201, "20;-1;-1;4;0;0d0+10", luchador, false,
								true);
						luchador.addBuff(214, (int) res, 100, 1, false, 1201, "20;-1;-1;4;0;0d0+10", luchador, false,
								true);
					}
				}
				/*
				 * if (getMapaCopia() != null && luchador.getMob() != null) { int valorextra =
				 * 1; switch (getMapaCopia().getID()) { case 30063: int vecesemp =
				 * _luchInit1.getPersonaje().desafdiario+1; if (vecesemp > 1) {
				 * luchador.addPDV(vecesemp*200); } break; case 30064: valorextra = 1;
				 * luchador.addPDV(valorextra*100); break; case 30065: valorextra = 2;
				 * luchador.addPDV(valorextra*100); break; case 30066: valorextra = 3;
				 * luchador.addPDV(valorextra*100); break; case 30067: valorextra = 4;
				 * luchador.addPDV(valorextra*100); break; } }
				 */
				_posinicial.put(luchador.getID(), luchador.getCeldaPelea());
				Personaje perso = luchador.getPersonaje();
				if (perso == null) {
					continue;
				}
				/*if (perso.estaMontando()) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 3, 950, perso.getID() + "",
							perso.getID() + "," + CentroInfo.ESTADO_CABALGANDO + ",1");
				}*/
				if (perso.getPDV() != luchador.getPDVConBuff()) {
					luchador.setPDV(perso.getPDV());
					luchador.setPDVMAX(perso.getPDVMAX());
				}
				/*
				 * int statpa = luchador.getPAConBuff(); int statpm = luchador.getPMConBuff();
				 * int extrapa = 0; int extrapm = 0; if (statpa > 5) extrapa = statpa-5; if
				 * (statpm > 5) extrapm = statpm-5; if (extrapa > 0 && extrapm > 0) {
				 * //addAyuda(luchador,extrapa,extrapm); } if (luchador.getNivel() < 250 &&
				 * !ayudado && luchador.getPersonaje()._resets == 0 && _tipo == 4) {
				 * GestorSalida.ENVIAR_cs_CHAT_MENSAJE_A_PELEA(this,
				 * "Al ser nivel inferior a 250 recibes un caballero (mÁximo 1 por pelea) que te ayudar� en los combates"
				 * , Colores.AZUL); addAyuda(luchador,12,8); ayudado = true; }
				 */
			}
			if (_tipo == 0 || _tipo == 1) {
				for (Luchador equipo1 : luchadoresDeEquipo(1)) {// aplica maldiciones
					if (equipo1.getPersonaje() == null) {
						continue;
					}
					if (equipo1.getPersonaje().getObjPosicion(203) != null) {
						int objetom = equipo1.getPersonaje().getObjPosicion(203).getIDModelo();
						for (Luchador equipo2 : luchadoresDeEquipo(2)) {
							if (equipo2.getPersonaje() == null) {
								continue;
							}
							aplicaMaldicion(equipo2, objetom);
						}
					}
				}
				for (Luchador equipo2 : luchadoresDeEquipo(2)) {// aplica maldiciones
					if (equipo2.getPersonaje() == null) {
						continue;
					}
					if (equipo2.getPersonaje().getObjPosicion(203) != null) {
						int objetom = equipo2.getPersonaje().getObjPosicion(203).getIDModelo();
						for (Luchador equipo1 : luchadoresDeEquipo(1)) {
							if (equipo1.getPersonaje() == null) {
								continue;
							}
							aplicaMaldicion(equipo1, objetom);
						}
					}
				}
			}
			GestorSalida.ENVIAR_GTM_INFO_STATS_TODO_LUCHADORES(this, 7, true);
			try {
				Thread.sleep(100);
			} catch (Exception e) {
			}
			if ((_tipo == 4 || _tipo == 3) && random < 2) {
				Map<Integer, Integer> copiaRetos = new TreeMap<>();
				copiaRetos.putAll(_retos);
				for (Entry<Integer, Integer> entry : copiaRetos.entrySet()) {
					int reto = entry.getKey();
					int exitoReto = entry.getValue();
					if (exitoReto != 0) {
						continue;
					}
					int idLuch = 0;
					int size2 = _equipo2.size();
					int nivel = 10000;
					Map<Integer, Integer> ordenNivelMobs = new TreeMap<>();
					Map<Integer, Luchador> ordenLuchMobs = new TreeMap<>();

					Map<Integer, Luchador> _equipo2b = new TreeMap<>();
					Map<Integer, Luchador> _equipo1b = new TreeMap<>();
					_equipo1b.putAll(_equipo1);
					_equipo2b.putAll(_equipo2);
					switch (reto) {
					case 30:// los pequeÑos antes
						for (Luchador luch : _equipo1b.values()) {
							if (luch.getNivel() < nivel) {
								_luchMenorNivelReto = luch.getID();
								nivel = luch.getNivel();
							}
						}
						break;
					case 10:// cruel
						while (ordenNivelMobs.size() < size2) {
							nivel = 10000;
							for (Luchador luch : _equipo2b.values()) {
								if (luch.getNivel() < nivel && !ordenNivelMobs.containsKey(luch.getID())) {
									idLuch = luch.getID();
									nivel = luch.getNivel();
								}
							}
							ordenNivelMobs.put(idLuch, nivel);
						}
						_ordenNivelMobs.putAll(ordenNivelMobs);
						break;
					case 17: // intocable
						for (Luchador luch : _equipo1b.values()) {
							luch._intocable = true;
						}
						break;
					case 25:// ordenado
						while (ordenNivelMobs.size() < size2) {
							nivel = 0;
							for (Luchador luch : _equipo2b.values()) {
								if (luch.getNivel() > nivel && !ordenNivelMobs.containsKey(luch.getID())) {
									idLuch = luch.getID();
									nivel = luch.getNivel();
								}
							}
							ordenNivelMobs.put(idLuch, nivel);
						}
						_ordenNivelMobs.putAll(ordenNivelMobs);
						break;
					case 35: // asesino a sueldo
						ArrayList<Luchador> temporal1 = new ArrayList<>();
						temporal1.addAll(_equipo2b.values());
						while (ordenLuchMobs.size() < size2) {
							Luchador l = temporal1.get(Formulas.getRandomValor(0, temporal1.size() - 1));
							temporal1.remove(l);
							ordenLuchMobs.put(l.getID(), l);
						}
						_ordenLuchMobs.putAll(ordenLuchMobs);
						for (Entry<Integer, Luchador> e : _ordenLuchMobs.entrySet()) {
							GestorSalida.ENVIAR_Gf_MOSTRAR_CASILLA_EN_PELEA(this, 5, e.getKey(),
									e.getValue().getCeldaPelea().getID());
							break;
						}
						break;
					case 47:// contaminacion
						for (Luchador luch : _equipo1.values()) {
							luch._contaminacion = true;
						}
						break;
					}
				}
			}
			if (_tipo == 4) {
				if (!_mobGrupo.esFixeado()) {
					_mapaReal.spawnGrupo(-1, 1, true, _mobGrupo.getCeldaID());
				} else {
					_mapaReal.refrescarFix(_mobGrupo.getCeldaID());
				}
			}

			inicioTurno("INICIARPELEA");
		} catch (Exception e) {
			e.printStackTrace();
			Emu.creaLogs(e);
		}
	}

	void buffeaMega(Luchador luchador) {
		int porc = Math.round(((10 * luchador.getPDVMax()) / 100));
		if (porc > 2000) {
			porc = 2000;
		}
		luchador.addBuff(125, porc, 100, 1, false, 155, "81;90;-1;20;0;1d10+80", luchador, false, true);// vida
		luchador.addBuff(138, 50, 100, 1, false, 1201, "50;-1;-1;0;0;0d0+50", luchador, false, true);// daÑo 45%
		luchador.addBuff(210, 10, 100, 1, false, 1201, "20;-1;-1;4;0;0d0+10", luchador, false, true);// resis
		luchador.addBuff(211, 10, 100, 1, false, 1201, "20;-1;-1;4;0;0d0+10", luchador, false, true);
		luchador.addBuff(212, 10, 100, 1, false, 1201, "20;-1;-1;4;0;0d0+10", luchador, false, true);
		luchador.addBuff(213, 10, 100, 1, false, 1201, "20;-1;-1;4;0;0d0+10", luchador, false, true);
		luchador.addBuff(214, 10, 100, 1, false, 1201, "20;-1;-1;4;0;0d0+10", luchador, false, true);
		luchador.addBuff(128, 1, 100, 1, false, 1201, "1;-1;-1;1;0;0d0+1", luchador, false, true);
		luchador.addBuff(111, 1, 100, 1, false, 1201, "1;-1;-1;2;0;0d0+1", luchador, false, true);
	}

	private void aplicaMaldicion(Luchador luchador, int objid) {
		switch (objid) {
		case 36030:// mal estar
			luchador.addBuff(215, 5, 10, 1, false, 3660, "10;-1;-1;-1;0;0d0+10", luchador, false, true);
			luchador.addBuff(216, 5, 10, 1, false, 3660, "10;-1;-1;-1;0;0d0+10", luchador, false, true);
			luchador.addBuff(217, 5, 10, 1, false, 3660, "10;-1;-1;-1;0;0d0+10", luchador, false, true);
			luchador.addBuff(218, 5, 10, 1, false, 3660, "10;-1;-1;-1;0;0d0+10", luchador, false, true);
			luchador.addBuff(219, 5, 10, 1, false, 3660, "10;-1;-1;-1;0;0d0+10", luchador, false, true);
			break;
		case 36031:// encadenamiento
			luchador.addBuff(127, 1, 10, 1, false, 3661, "1;2;-1;2;0;1d2+0", luchador, false, true);
			break;
		case 36032:// Factor Sorpresa
			int random = Formulas.getRandomValor(1, 2);
			if (random == 1) {
				luchador.addBuff(168, 2, 10, 1, false, 3662, "30;-1;-1;1;0;0d0+30", luchador, false, true);
			} else {
				luchador.addBuff(128, 2, 10, 1, false, 3662, "1;-1;-1;1;0;0d0+1", luchador, false, true);
			}
			break;
		}
	}

	@SuppressWarnings("unused")
	private void addAyuda(Luchador luch, int pa, int pm) { // TODO:
		String groupData = "394,200,200";
		Map<Integer, Luchador> equipTemp = new TreeMap<>();
		GrupoMobs mobGrupo = new GrupoMobs(_mapaCopia._sigIDMapaInfo, luch.getPersonaje().getCelda().getID(),
				groupData);
		for (Entry<Integer, MobGrado> entry : mobGrupo.getMobs().entrySet()) {
			equipTemp.put(entry.getKey(), new Luchador(this, entry.getValue()));
		}
		for (Entry<Integer, Luchador> entry : equipTemp.entrySet()) {
			Luchador guardia = entry.getValue();
			Celda celdaRandom = getCeldaRandom(_celdasPos1);
			if (celdaRandom == null) {
				return;
			}
			guardia.setCeldaPelea(celdaRandom);
			_mapaCopia.addLuchador(guardia);
			guardia.setEquipoBin(luch.getEquipoBin());
			guardia.setInvocador(luch);
			guardia.fullPDV();
			agregarLuchador(luch, guardia, false);
			addLuchadorEnEquipo(guardia, luch.getEquipoBin());
			String gtl = this.stringOrdenJugadores();
			String gm = "+" + guardia.stringGM();
			GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 181, luch.getID() + "", gm);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 999, luch.getID() + "", gtl);
			luch.aumentarInvocaciones();
			luch.aumentarInvosTotales();
			guardia.getTotalStatsSinBuff().especificarStat(111, pa);
			guardia.getTotalStatsSinBuff().especificarStat(128, pm);
		}
		GestorSalida.ENVIAR_GM_LUCHADOR_A_TODA_PELEA(this, 7, _mapaCopia);
	}
	// private int pmactual = 0;
	// private int idluchador = 0;

	private void inicioTurno(String destino) {
		try {
			//this._nroOrdenLuc = (byte)(this._nroOrdenLuc + 1);
			if (Emu.SOLOSYS) {
				System.out.println("inicioTurno " + destino);
			}
			if (cuantosQueda() <= 0) {
				acaboPelea("finturnoIncio");
				return;
			}
			if (_estadoPelea >= 4) {
				return;
			}
			esperaMove = false;
			esperaHechizo = false;
			_tempAccion = "";
			_cantMobsMuerto = 0;
			if (LucActual != null) {
				if (LucActual.getPersonaje() != null) {
					LucActual.realizaaccion = false;
				}
			}
			if (this.getMapaCopia().getID() == 27105) {
				for (Luchador luchador : luchadoresDeEquipo(3)) {
					if (luchador.getPersonaje() == null) {
						continue;
					}
					GestorSalida.enviar(luchador.getPersonaje(), "#GSparar");
				}
			}

			Luchador luchador = siguienteLuc();


			if (luchador == null) {
				return;
			}



			//CODIGO PARA MAPEAR PASIVAS JHON
			/*if(luchador._perso != null) {
				try {
					if(activarPasivaPar) {
						if(luchador._perso.tieneEquipado(7754)) {
							luchador._buffsPelea.clear();
							GestorSalida.ENVIAR_MENSAJE_A_TODOS_CHAT_COLOR(luchador.getNombreLuchador()+" Activo Buffo Ocre" , Colores.VERDE);
							luchador.addBuff(120, 1, 1, 1, false, 1201, "1;-1;1;1;0;0d0+1", luchador, false, true);

							luchador.aplicarBuffPasivaInicioTurno(luchador.getPelea());
							activarPasivaPar = false;


						}
					}else if(activarPasivaImpar) {


						activarPasivaImpar = false;
					}


				}catch (Exception e) {
					// TODO: handle exception
				}
			}	*/

			//CODIGO PARA MAPEAR PASIVAS JHON

			// acciones de la IA


			luchador.usoTP = false;
			luchador.usoATR = false;
			luchador.hechizosUsados.clear();
			//
			GestorSalida.ENVIAR_GTR_TURNO_LISTO(this, 7, luchador.getID());
			luchador.ataqueotros = 0;
			luchador.ataquedYb = 0;
			luchador.setPuedeJugar(true);
			ArrayList<Glifo> glifos = new ArrayList<>();
			glifos.addAll(_glifos);
			for (Glifo glifo : glifos) {
				if (_estadoPelea >= 4) {
					return;
				}
				if ((glifo.getLanzador().getID() == luchador.getID()) && (glifo.disminuirDuracion() == 0)) {
					glifo.desaparecer();
					_glifos.remove(glifo);
				}
			}
			if (luchador._estaMuerto) {
				return;// COMPROBAR MUERTE POR GLIFO Y SINO PONER FINTURNO
			}



			GestorSalida.ENVIAR_GTS_INICIO_TURNO_PELEA(this, 7, luchador.getID(), tiempoPPVM);


			luchador.aplicarBuffInicioTurno(luchador.getPelea());
			if (luchador._estaMuerto) {
				return;
			}

			glifos.clear();
			glifos.addAll(_glifos);
			int dist;
			for (Glifo glifo : glifos) {
				if (_estadoPelea >= 4 || glifo._hechizos == 476) {
					continue;
				}
				dist = Camino.distanciaEntreDosCeldas(_mapaCopia, luchador.getCeldaPelea().getID(),
						glifo.getCelda().getID());
				if ((dist <= glifo.getTamaÑo())) {
					glifo.activarGlifo(luchador);
				}

			}
			if (luchador._estaMuerto) {
				return;
			}
			luchador._bonusCastigo.clear();
			_tempLuchadorPA = luchador.getPAConBuff();
			_tempLuchadorPM = luchador.getPMConBuff();
			_tempLuchadorPAusados = 0;
			_tempLuchadorPMusados = 0;
			luchador.actualizaHechizoLanzado();
			if (luchador.getPersonaje() != null) {
				GestorSalida.ENVIAR_As_STATS_DEL_PJ(luchador.getPersonaje());
			}


			try {
				if (luchador.tieneBuff(CentroInfo.EFECTO_PASAR_TURNO) || luchador.getPersonaje().getComandoPasarTurno()) {
					if(luchador.getPersonaje() != null) {
						finTurno("INICIOTURNO3");
						return;
					}



				}
			}catch (Exception e) {
				// TODO: handle exception
			}

			if (luchador.getPDVConBuff() <= 0) {
				agregarAMuertos(luchador, luchador, "");
				return;
			}

			if ((_tipo == 4 || _tipo == 3) && luchador.getPersonaje() != null) {
				Map<Integer, Integer> copiaRetos = new TreeMap<>();
				copiaRetos.putAll(_retos);
				for (Entry<Integer, Integer> entry : copiaRetos.entrySet()) {
					int reto = entry.getKey();
					int exitoReto = entry.getValue();
					if (exitoReto != 0) {
						continue;
					}
					switch (reto) {
					case 2:// estatua
						luchador._idCeldaIniTurnoReto = luchador._celda.getID();
						break;
					case 6:// versatil
						luchador._hechiLanzadosReto.clear();
						break;
					case 34:// imprevisible
						ArrayList<Luchador> mobsVivos = new ArrayList<>();
						for (Luchador luch : _inicioLucEquipo2) {
							if (luch.estaMuerto()) {
								continue;
							}
							mobsVivos.add(luch);
						}
						Luchador x = mobsVivos.get(Formulas.getRandomValor(0, mobsVivos.size() - 1));
						_idMobReto = x.getID();
						GestorSalida.ENVIAR_Gf_MOSTRAR_CASILLA_EN_PELEA(this, 5, _idMobReto, x.getCeldaPelea().getID());
						break;
					case 47:// contaminacion
						if (luchador._contaminado) {
							luchador._turnosParaMorir++;
							if (luchador._turnosParaMorir > 3) {
								GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
								exitoReto = 2;
							}
						}
						break;
					}
					if (exitoReto != 0) {
						_retos.remove(reto);
						_retos.put(reto, exitoReto);
					}
				}
			}
			if (luchador._desconectado) {
				luchador._turnosRestantes -= 1;
				if (luchador._turnosRestantes <= 0) {
					for (Personaje perso : _espectadores.values()) {
						if (perso != null) {
							perso.salirEspectador();
						}
					}
					agregarAMuertos(luchador, luchador, "");
					luchador.getPersonaje().setEnLinea(false);
					luchador.getPersonaje().setReconectado(false);
					luchador.getPersonaje().resetVariables();
					luchador._desconectado = false;
				} else {
					finTurno("finturno de inicioturno por reconexion");
				}
				return;
			}
			_tiempoInicioTurno = System.currentTimeMillis();
			startTimer(tiempoPPVM, true);
			if (luchador.getPersonaje() == null || luchador._doble != null
					|| luchador.esInvocacion() && luchador.getPersonaje() == null || luchador._recaudador != null
					|| luchador._prisma != null) {
				_IAThreads = new IAThread(luchador, this);
			}





		} catch (Exception e) {
			e.printStackTrace();
			Emu.creaLogs(e);
		}
	}

	public void finTurno(String destino) {
		if (_estadoPelea == 4) {
			return;
		}
		intentos = 0;
		try {
			if (getActTimerTask() != null) {
				getActTimerTask().cancel();
			}
			setActTimerTask(null);
			_tiempoInicioTurno = 0L;
			/*
			 * int elbucl = 0; while(elbucl < needtowait) { try { Thread.sleep(500L); }
			 * catch (Exception e) {} elbucl+= 1; } needtowait = 0;
			 */
			if (_estadoPelea >= 4) {
				return;
			}
			Luchador luchador = LucActual;
			if (luchador == null) {
				return;
			}
			long timeact = System.currentTimeMillis() - timeActual;
			try {
				if (timeact < tienePasar && luchador.realizaaccion) {
					long timerest = tienePasar - timeact;
					if (timerest > 100) {
						Thread.sleep(timerest);
					}
				}
			} catch (Exception e) {
			}
			int vecesespera = 0;
			while (!_tempAccion.isEmpty() && vecesespera < 5) {
				try {
					Thread.sleep(500L);
				} catch (Exception e) {
				}
				vecesespera += 1;
			}
			long timeespT = System.currentTimeMillis() - _tiempoInicioTurno;
			if ((System.currentTimeMillis() - _tiempoInicioTurno) < 1000 && luchador.realizaaccion) {
				long timer = 1000 - timeespT;
				try {
					Thread.sleep(timer);
				} catch (Exception e) {
				}
			}
			if (_IAThreads != null) {
				try {
					if (_IAThreads.getThread() != null) {
						_IAThreads.getThread().interrupt();
						_IAThreads = null;
					}
				} catch (Exception e) {
				}
			}
			luchador.stop = false;
			luchador.excepcionMover = false;
			if (!luchador.puedeJugar()) {
				return;
			}
			if (luchador.getPersonaje() != null && luchador.getMob() == null && !luchador.esInvocacion()
					&& !luchador.esDoble()) {
				luchador.getPersonaje().caramelosRes(this);
			}
			try {

				GestorSalida.ENVIAR_GTF_FIN_DE_TURNO(this, 7, luchador.getID());

				luchador.setPuedeJugar(false);
				luchador.actualizarBuffsPelea();
				if (!luchador._estaRetirado && !luchador.esInvocacion()) {
					if (!luchador._estaMuerto) {
						for (EfectoHechizo EH : luchador.getBuffsPorEfectoID(131)) {
							if (_estadoPelea == 4) {
								break;
							}
							int cadaCuantosPA = EH.getValor();
							int val = -1;
							try {
								val = Integer.parseInt(EH.getArgs().split(";")[1]);
							} catch (Exception e) {
							}
							if (val == -1) {
								continue;
							}
							int nroPAusados = (int) Math.floor((double) _tempLuchadorPAusados / (double) cadaCuantosPA);
							int daÑoFinTurno = val * nroPAusados;
							Stats totalLanz = EH.getLanzador().getTotalStatsConBuff();
							int inte = 0;
							if (EH.getHechizoID() == 200) {// Si veneno paralizante
								inte = totalLanz.getEfecto(126);
								if (inte < 0) {
									inte = 0;
								}
								int pDaÑos = totalLanz.getEfecto(CentroInfo.STATS_ADD_PORC_DAÑOS);
								if (pDaÑos < 0) {
									pDaÑos = 0;
								}
								int daÑos = totalLanz.getEfecto(CentroInfo.STATS_ADD_DAÑOS);
								if (daÑos < 0) {
									daÑos = 0;
								}
								daÑoFinTurno = ((100 + inte + pDaÑos) / 100) * daÑoFinTurno + daÑos;
							}
							if (luchador.tieneBuff(105)) {
								GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 105, luchador.getID() + "",
										luchador.getID() + "," + luchador.getBuff(105).getValor());
								daÑoFinTurno = daÑoFinTurno - luchador.getBuff(105).getValor();// Immunidad
							}
							if (luchador.tieneBuff(184)) {
								GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 105, luchador.getID() + "",
										luchador.getID() + "," + luchador.getBuff(184).getValor());
								daÑoFinTurno = daÑoFinTurno - luchador.getBuff(184).getValor();
							}
							if (daÑoFinTurno <= 0) {
								continue;
							}
							if (daÑoFinTurno > luchador.getPDVConBuff()) {
								daÑoFinTurno = luchador.getPDVConBuff();
							}
							luchador.restarPDV(daÑoFinTurno);
							daÑoFinTurno = -(daÑoFinTurno);
							GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 100, EH.getLanzador().getID() + "",
									luchador.getID() + "," + daÑoFinTurno);
						}
						ArrayList<Glifo> glifos = new ArrayList<>();
						glifos.addAll(_glifos);
						for (Glifo glifo : glifos) {
							if (_estadoPelea >= 4) {
								return;
							}
							int dist = Camino.distanciaEntreDosCeldas(_mapaCopia, luchador.getCeldaPelea().getID(),
									glifo.getCelda().getID());
							if (dist <= glifo.getTamaÑo() && glifo._hechizos == 476) {
								glifo.activarGlifo(luchador);
							}
						}
						if (luchador.getPDVConBuff() <= 0)
						 {
							agregarAMuertos(luchador, luchador, ""); // retos
						}
						Personaje perso = luchador.getPersonaje();
						if ((_tipo == 4 || _tipo == 3) && perso != null) {
							Map<Integer, Integer> copiaRetos = new TreeMap<>();
							copiaRetos.putAll(_retos);
							for (Entry<Integer, Integer> entry : copiaRetos.entrySet()) {
								int reto = entry.getKey();
								int exitoReto = entry.getValue();
								if (exitoReto != 0) {
									continue;
								}
								switch (reto) {
								case 1:// zombi
									if (_tempLuchadorPMusados == 0) {
										GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
										exitoReto = 2;
									}
									break;
								case 2:// estatua
									if (luchador._idCeldaIniTurnoReto != luchador._celda.getID()) {
										GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
										exitoReto = 2;
									}
									break;
								case 7: // jardinero
									if (hechizoDisponible(luchador, 367)) {
										GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
										exitoReto = 2;
									}
									break;
								case 8:// nomada
									if (_tempLuchadorPM > 0) {
										GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
										exitoReto = 2;
									}
									break;
								case 12: // sepultero
									if (hechizoDisponible(luchador, 373)) {
										GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
										exitoReto = 2;
									}
									break;
								case 14: // casino real
									if (hechizoDisponible(luchador, 101)) {
										GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
										exitoReto = 2;
									}
									break;
								case 15: // jardinero
									if (hechizoDisponible(luchador, 370)) {
										GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
										exitoReto = 2;
									}
									break;
								case 36:// audaz
									if (!Camino.hayAlrededor(_mapaCopia, this, luchador, false)) {
										GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
										exitoReto = 2;
									}
									break;
								case 37:// Pegajoso
									if (!Camino.hayAlrededor(_mapaCopia, this, luchador, true)) {
										GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
										exitoReto = 2;
									}
									break;
								case 39: // anacorea
									if (Camino.hayAlrededor(_mapaCopia, this, luchador, true)) {
										GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
										exitoReto = 2;
									}
									break;
								case 40: // pusilanime
									if (Camino.hayAlrededor(_mapaCopia, this, luchador, false)) {
										GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
										exitoReto = 2;
									}
									break;
								case 41: // impetuoso
									if (_tempLuchadorPA > 0) {
										GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
										exitoReto = 2;
									}
									break;
								}
								if (exitoReto != 0) {
									_retos.remove(reto);
									_retos.put(reto, exitoReto);
								}
							}
						} // reset de valores
						_tempLuchadorPAusados = 0;
						_tempLuchadorPMusados = 0;
						Stats statsConBuff = luchador.getTotalStatsConBuff();
						_tempLuchadorPA = statsConBuff.getEfecto(CentroInfo.STATS_ADD_PA);
						_tempLuchadorPM = statsConBuff.getEfecto(CentroInfo.STATS_ADD_PM);
						if (perso != null) {
							if (perso.enLinea()) {
								GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso);
							}
						}
						GestorSalida.ENVIAR_GTM_INFO_STATS_TODO_LUCHADORES(this, 7, false);


					}
				}
				_tempAccion = "";
				inicioTurno("FINTURNO");
			} catch (Exception e) {
				e.printStackTrace();
				Emu.creaLogs(e);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Emu.creaLogs(e);
		}
	}

	public Map<Integer, Personaje> getEspectadores() {
		return _espectadores;
	}

	private boolean hechizoDisponible(Luchador luchador, int idHechizo) {
		boolean ver = false;
		if (luchador.getPersonaje().tieneHechizoID(idHechizo)) {
			ver = true;
			for (HechizoLanzado HL : luchador.getHechizosLanzados()) {
				if (HL._hechizoId == idHechizo && HL.getSigLanzamiento() > 0) {
					ver = false;
				}
			}
		}
		return ver;
	}

	private void iniciarOrdenLuchadores() {
		int j = 0;
		int k = 0;
		int start0 = 0;
		int start1 = 0;
		int curMaxIni0 = 0;
		int curMaxIni1 = 0;
		Luchador curMax0 = null;
		Luchador curMax1 = null;
		boolean team1_ready = false;
		boolean team2_ready = false;
		do {
			if (!team1_ready) {
				team1_ready = true;
				Map<Integer, Luchador> _Team0 = _equipo1;
				for (Entry<Integer, Luchador> entry : _Team0.entrySet()) {
					if (turnosPelea.contains(entry.getValue())) {
						continue;
					}
					team1_ready = false;
					if (entry.getValue().getIniciativa() >= curMaxIni0) {
						curMaxIni0 = entry.getValue().getIniciativa();
						curMax0 = entry.getValue();
					}
					if (curMaxIni0 > start0) {
						start0 = curMaxIni0;
					}
				}
			}
			if (!team2_ready) {
				team2_ready = true;
				for (Entry<Integer, Luchador> entry : _equipo2.entrySet()) {
					if (turnosPelea.contains(entry.getValue())) {
						continue;
					}
					team2_ready = false;
					if (entry.getValue().getIniciativa() >= curMaxIni1) {
						curMaxIni1 = entry.getValue().getIniciativa();
						curMax1 = entry.getValue();
					}
					if (curMaxIni1 > start1) {
						start1 = curMaxIni1;
					}
				}
			}
			if ((curMax1 == null) && (curMax0 == null)) {
				return;
			}
			if (start0 > start1) {
				if (luchadoresDeEquipo(1).size() > j) {
					turnosPelea.add(curMax0);
					j++;
				}
				if (luchadoresDeEquipo(2).size() > k) {
					turnosPelea.add(curMax1);
					k++;
				}
			} else {
				if (luchadoresDeEquipo(2).size() > j) {
					turnosPelea.add(curMax1);
					j++;
				}
				if (luchadoresDeEquipo(1).size() > k) {
					turnosPelea.add(curMax0);
					k++;
				}
			}
			curMaxIni0 = 0;
			curMaxIni1 = 0;
			curMax0 = null;
			curMax1 = null;
		} while (turnosPelea.size() != luchadoresDeEquipo(3).size());
	}

	/*
	 * private ArrayList<Luchador> getLuchs() {//ok ArrayList<Luchador> luchadores =
	 * new ArrayList<Luchador>(); for (Luchador luch : turnosPelea) { if
	 * (luch.estaMuerto() || luch.estaRetirado() || luch.getPDVConBuff() <= 0 ||
	 * luch.statico) continue; luchadores.add(luch); } return luchadores; }
	 */

	public int getPosicionLuchador(Luchador luch) {// ok
		try {
			for (int i = 0; i < turnosPelea.size(); i++) {
				if (_estadoPelea == 4) {
					break;
				}
				if (turnosPelea.get(i) == null) {
					continue;
				}
				if (turnosPelea.get(i) == luch) {
					return i;
				}
			}
		} catch (Exception e) {
			return -1;
		}
		return -1;
	}

	private int lastid = 0;



	private Luchador siguienteLuc() {// ok
		if (LucActual == null) {
			lastid = 0;
			LucActual = turnosPelea.get(lastid);
		} else {
			try {
				if (!LucActual._estaMuerto) {
					lastid = getPosicionLuchador(LucActual)+1;
				}
				LucActual = turnosPelea.get(lastid);
			} catch (Exception e) {
				lastid = 0;
				LucActual = turnosPelea.get(lastid);
			}
		}

		if(lastid == 0) {
			if(turnoActual % 2 == 0) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE_A_PELEA(this,7,"Inicia Turno Par " + turnoActual , Colores.ROJO);
				
				if (getMapaCopia().getID() == 31006 && turnoActual == 6) {
					
					for (Luchador luchador : luchadoresDeEquipo(3)) {
						if (luchador.getPersonaje() != null) {
							luchador.addBuff(131, 1, 3, 1, true, 228, "1;10000;-1;3;0;0d0+10000", luchador, false, true);
							
						}
						
					}
					
				
			}
				
				
				
				turnoActual += 1;
				activarPasivaPar = true;
				
				
				
				
				

			}else {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE_A_PELEA(this,7,"Inicia Turno Impar " + turnoActual , Colores.ROJO);
				turnoActual += 1;
				activarPasivaImpar = true;
			}
		}


		return LucActual;



	}

	void agregarLuchador(Luchador siguientea, Luchador nuevo, boolean doble) {// ok
		if (!nuevo.statico && getPosicionLuchador(siguientea) != -1) {
			turnosPelea.add(getPosicionLuchador(siguientea) + ((siguientea.getNroInvocaciones())), nuevo);
		}
		GestorSalida.ENVIAR_GTL_ORDEN_JUGADORES(this, 7);
	}

	/*
	 * void borrarInvocaciones(Luchador invocador) { for (Luchador luchador :
	 * luchadoresDeEquipo(3)) { if (luchador == null || luchador._estaMuerto ||
	 * luchador._estaRetirado) continue; if (luchador.getInvocador() == invocador) {
	 * agregarAMuertos(luchador, ""); } } /*try { for (Luchador lucha :
	 * nuevosTrayectos) { if (!lucha.esInvocacion()) continue; if
	 * (lucha.getInvocador() == invocador) { agregarAMuertos(lucha, ""); } } } catch
	 * (Exception e) { Emu.creaLogs(e); } }
	 */
	public boolean checkUnirsePerco(Personaje perso) {
		int tiempoRecaudador = 0;
		if (_Recaudador != null) {
			tiempoRecaudador = (tiempoPRecau - (int) (System.currentTimeMillis() - _tiempoInicioPelea));
		}
		if (tiempoRecaudador <= 2500) {
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "El combate empezarÁ en unos segundos, no te puedes unir ahora",
					Colores.ROJO);
			return false;
		}
		PrintWriter out = perso.getCuenta().getEntradaPersonaje().getOut();
		if ((_tiempoInicio != 0L) || (_estadoPelea > 2)) {
			GestorSalida.ENVIAR_GA903_ERROR_PELEA(out, 'l');
			return false;
		}
		return true;
	}

	public void unirsePelea(Personaje perso, int idOtroPerso) {
		if (esAngel && perso._resets != 3) {
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, Idiomas.getTexto(perso.getCuenta().idioma, 155), Colores.ROJO);
			return;
		}
		int tiempoRestante = (tiempoPPVP - (int) (System.currentTimeMillis() - _tiempoInicioPelea));
		if (tiempoRestante <= 2500) {
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, Idiomas.getTexto(perso.getCuenta().idioma, 156), Colores.ROJO);
			return;
		}
		if (enKoliseo) {
			if (perso.getKoliseo() == null) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, Idiomas.getTexto(perso.getCuenta().idioma, 157),
						Colores.ROJO);
				return;
			} else {
				if (perso.getKoliseo().randommap != this.getMapaCopia().getID()) {
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, Idiomas.getTexto(perso.getCuenta().idioma, 157),
							Colores.ROJO);
					return;
				}
			}
		}
		if (getMapaCopia().getID() == 7796) {
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, Idiomas.getTexto(perso.getCuenta().idioma, 160), Colores.ROJO);
			return;
		}
		if (enTorneo) {
			if (_equipo1.size() >= 1 && _equipo2.size() >= 1) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, Idiomas.getTexto(perso.getCuenta().idioma, 158),
						Colores.ROJO);
				return;
			}
		} else {
			if (perso.enTorneo != 0) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso,
						"No puedes unirte a una pelea mientras estÁs inscrito en un Torneo", Colores.ROJO);
				return;
			}
		}
		if (_tipo == 1) {
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, Idiomas.getTexto(perso.getCuenta().idioma, 159), Colores.ROJO);
			return;
		}
		if (_tipo == 4) {
			switch (_mapaCopia.getID()) {
			case 30080:
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, Idiomas.getTexto(perso.getCuenta().idioma, 160),
						Colores.ROJO);
				return;
			}
		}
		if (_mapaCopia != null) {
			if (_mapaCopia.getID() == 27105) {
				GestorSalida.enviar(perso, "#GS14");
			}
			if (MundoDofus.mapasBoost.contains(_mapaCopia.getID()) || _mapaCopia.getID() == 27105) {
				int ips = 0;
				for (Luchador dMob : luchadoresDeEquipo(3)) {
					if (dMob.getPersonaje() == null || dMob.getMob() != null) {
						continue;
					}
					if (dMob.getPersonaje().getCuenta().getActualIP().compareTo(perso.getCuenta().getActualIP()) == 0) {
						ips += 1;
					}
				}
				if (_mapaCopia.getID() == 27105) {
					if (ips > 0) {
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, Idiomas.getTexto(perso.getCuenta().idioma, 162),
								Colores.ROJO);
						return;
					}
				} else {
					/*
					 * if (ips > 2) { GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso,
					 * Idiomas.getTexto(perso.getCuenta().idioma, 161), Colores.ROJO); return; }
					 */
				}




			} else {
				if (_tipo == 2) {
					int ips = 0;
					for (Luchador dMob : luchadoresDeEquipo(3)) {
						if (dMob.getPersonaje() == null || dMob.getMob() != null) {
							continue;
						}
						if (dMob.getPersonaje().getCuenta().getActualIP()
								.compareTo(perso.getCuenta().getActualIP()) == 0) {
							ips += 1;
						}
					}
					if (ips > 0) {
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso,
								"En las peleas de Prismas sÓlo se permite 1 cuenta por IP", Colores.ROJO);
						return;
					}
				}
			}
			//RAID SOLO SE PERMITE 1 RAZA PARA EL COMBATE
			if (_mapaCopia.getID() == 31006) {
				//int razasCount = 0;
				for (Luchador rcount : luchadoresDeEquipo(3)) {
					if(rcount.getPelea() == null || rcount.getMob() != null) {
						continue;
					}
					if(rcount.getPersonaje().getClase(true) == perso.getClase(true)) {
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, Idiomas.getTexto(perso.getCuenta().idioma, 223),
								Colores.ROJO);
						return;
					}
				}

				/*int ips = 0;
				Mapa mapa = MundoDofus.getMapa(31006);
				for (final Personaje pjs : mapa.getPersos()) {

						if (pjs.getCuenta().getActualIP().compareTo(perso.getCuenta().getActualIP()) == 0) {
							ips += 1;
						}
				}
				if (ips > 0) {
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, Idiomas.getTexto(perso.getCuenta().idioma, 222),
							Colores.ROJO);
					return;
				}*/

			}
			//RAID SOLO SE PERMITE 1 RAZA PARA EL COMBATE
		}
		if (peleainvasion || esAngel) {
			int ips = 0;
			for (Luchador dMob : luchadoresDeEquipo(3)) {
				if (dMob.getPersonaje() == null || dMob.getMob() != null) {
					continue;
				}
				if (dMob.getPersonaje().getCuenta().getActualIP().compareTo(perso.getCuenta().getActualIP()) == 0) {
					ips += 1;
				}
			}
			if (ips > 0) {
				if (peleainvasion) {
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso,
							"En las peleas de Megas sÓlo se permite 1 cuenta por IP en una misma pelea", Colores.ROJO);
				} else if (esAngel) {
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, Idiomas.getTexto(perso.getCuenta().idioma, 162),
							Colores.ROJO);
				}
				return;
			}
		}
		PrintWriter out = perso.getCuenta().getEntradaPersonaje().getOut();
		if ((_tiempoInicio != 0L) || (_estadoPelea > 2)) {
			GestorSalida.ENVIAR_GA903_ERROR_PELEA(out, 'l');
			return;
		}
		if (getMapaCopia() != null) {
			if (getMapaCopia().getID() == 30063) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, Idiomas.getTexto(perso.getCuenta().idioma, 163),
						Colores.ROJO);
				return;
			}
		}
		Luchador jugadorUnirse = null;
		if (_equipo1.containsKey(idOtroPerso)) {
			Celda celda = getCeldaRandom(_celdasPos1);
			if (celda == null) {
				return;
			}
			int cantn = 8;
			if (_equipo1.size() >= cantn) {
				GestorSalida.ENVIAR_GA903_ERROR_PELEA(out, 't');
				return;
			}
			if (_soloGrupo1) {
				Grupo g = _luchInit1.getPersonaje().getGrupo();
				if (g != null) {
					if (!g.getPersos().contains(perso)) {
						GestorSalida.ENVIAR_GA903_ERROR_PELEA(out, 'f');
						return;
					}
				}
			}
			if (_tipo == 1) {
				if (perso.getAlineacion() == -1) {
					GestorSalida.ENVIAR_GA903_ERROR_PELEA(out, 'a');
					return;
				} else if (_luchInit1.getPersonaje().getAlineacion() != perso.getAlineacion()) {
					GestorSalida.ENVIAR_GA903_ERROR_PELEA(out, 'a');
					return;
				}
				perso.botonActDesacAlas('+');
			}
			if (_tipo == 2) {
				if (perso.getAlineacion() == -1) {
					GestorSalida.ENVIAR_GA903_ERROR_PELEA(out, 'a');
					return;
				} else if (_luchInit1.getPersonaje().getAlineacion() != perso.getAlineacion()) {
					GestorSalida.ENVIAR_GA903_ERROR_PELEA(out, 'a');
					return;
				}
				perso.botonActDesacAlas('+');
			}
			if (_gremioID > -1 && perso.getGremio() != null) {
				if (getGremioID() == perso.getGremio().getID()) {
					GestorSalida.ENVIAR_GA903_ERROR_PELEA(out, 'g');
					return;
				}
			}
			if (_cerrado1) {
				GestorSalida.ENVIAR_GA903_ERROR_PELEA(out, 'f');
				return;
			}
			if (_tipo == 0) {
				GestorSalida.ENVIAR_GJK_UNIRSE_PELEA(perso, 2, true, true, false, 0, _tipo);
			} else {
				GestorSalida.ENVIAR_GJK_UNIRSE_PELEA(perso, 2, false, true, false, tiempoRestante, _tipo);
			}
			GestorSalida.ENVIAR_GP_POSICIONES_PELEA(out, _mapaCopia.getLugaresString(), _celdaColor1);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 3, 950, perso.getID() + "", perso.getID() + "," + 8 + ",0");
			GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 3, 950, perso.getID() + "", perso.getID() + "," + 3 + ",0");
			GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(perso.getMapa(), perso.getID());
			Luchador luchador = new Luchador(this, perso);
			luchador.setEquipoBin(0);
			_equipo1.put(perso.getID(), luchador);
			perso.setPelea(this);
			luchador.setCeldaPelea(celda);
			_mapaCopia.addLuchador(luchador);
			jugadorUnirse = luchador;
		} else if (_equipo2.containsKey(idOtroPerso)) {
			Celda celda = getCeldaRandom(_celdasPos2);
			if (celda == null) {
				return;
			}
			int cantn = 8;
			if (_equipo2.size() >= cantn) {
				GestorSalida.ENVIAR_GA903_ERROR_PELEA(out, 't');
				return;
			}
			if (_soloGrupo2) {
				Grupo g = _luchInit2.getPersonaje().getGrupo();
				if (g != null) {
					if (!g.getPersos().contains(perso)) {
						GestorSalida.ENVIAR_GA903_ERROR_PELEA(out, 'f');
						return;
					}
				}
			}
			if (_tipo == 1) {
				if (perso.getAlineacion() == -1) {
					GestorSalida.ENVIAR_GA903_ERROR_PELEA(out, 'a');
					return;
				}
				if (_luchInit2.getPersonaje().getAlineacion() != perso.getAlineacion()) {
					GestorSalida.ENVIAR_GA903_ERROR_PELEA(out, 'a');
					return;
				}
				perso.botonActDesacAlas('+');
			}
			if (_tipo == 2) {
				if (perso.getAlineacion() == -1) {
					GestorSalida.ENVIAR_GA903_ERROR_PELEA(out, 'a');
					return;
				}
				if (_luchInit2.getPrisma().getAlineacion() != perso.getAlineacion()) {
					GestorSalida.ENVIAR_GA903_ERROR_PELEA(out, 'a');
					return;
				}
				perso.botonActDesacAlas('+');
			}
			if (_gremioID > -1 && perso.getGremio() != null) {
				if (getGremioID() == perso.getGremio().getID()) {
					GestorSalida.ENVIAR_GA903_ERROR_PELEA(out, 'g');
					return;
				}
			}
			if (_cerrado2) {
				GestorSalida.ENVIAR_GA903_ERROR_PELEA(out, 'f');
				return;
			}
			if (_tipo == 0) {
				GestorSalida.ENVIAR_GJK_UNIRSE_PELEA(perso, 2, true, true, false, 0, _tipo);
			} else {
				GestorSalida.ENVIAR_GJK_UNIRSE_PELEA(perso, 2, false, true, false, tiempoRestante, _tipo);
			}
			GestorSalida.ENVIAR_GP_POSICIONES_PELEA(out, _mapaCopia.getLugaresString(), _celdaColor2);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 3, 950, perso.getID() + "", perso.getID() + "," + 8 + ",0");
			GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 3, 950, perso.getID() + "", perso.getID() + "," + 3 + ",0");
			GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(perso.getMapa(), perso.getID());
			Luchador luchador = new Luchador(this, perso);
			luchador.setEquipoBin(1);
			_equipo2.put(perso.getID(), luchador);
			perso.setPelea(this);
			luchador.setCeldaPelea(celda);
			_mapaCopia.addLuchador(luchador);
			jugadorUnirse = luchador;
		}
		GestorSalida.ENVIAR_ILF_CANTIDAD_DE_VIDA(perso, 0);
		_mapaReal.borrarJugador(perso);
		GestorSalida.ENVIAR_Gt_AGREGAR_NOMBRE_ESPADA(perso.getMapa(),
				(jugadorUnirse.getEquipoBin() == 0 ? _luchInit1 : _luchInit2).getID(), jugadorUnirse);
		GestorSalida.ENVIAR_GM_JUGADO_UNIRSE_PELEA(this, 7, jugadorUnirse);
		GestorSalida.ENVIAR_GM_LUCHADORES(this, _mapaCopia, perso);
		if (_Recaudador != null) {
			for (Personaje z : MundoDofus.getGremio(_gremioID).getPjMiembros()) {
				if (z == null) {
					continue;
				}
				if (z.enLinea()) {
					Recaudador.analizarAtaque(z, _gremioID);
					Recaudador.analizarDefensa(z, _gremioID);
				}
			}
		}
		if (_Prisma != null) {
			for (Personaje z : MundoDofus.getPJsEnLinea()) {
				if (z == null || z.getAlineacion() != _Prisma.getAlineacion()) {
					continue;
				}
				Prisma.analizarAtaque(perso);
			}
		}
	}

	public void unirseEspectador(Personaje perso) {
		if ((LucActual == null) || _tiempoInicio == 0L || perso == null) {
			GestorSalida.ENVIAR_BN_NADA(perso);
			return;
		}
		if (perso.enTorneo == 1) {
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso,
					"No puedes unirte de espectador mientras estÁs inscrito en un Torneo", Colores.ROJO);
			return;
		}
		if (perso.getKoliseo() != null) {
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso,
					"No puedes unirte de espectador mientras estÁs inscrito en un Koliseo", Colores.ROJO);
			return;
		}
		if (perso != null && perso.esFantasma()) {
			GestorSalida.ENVIAR_Im_INFORMACION(perso, "1116");
			return;
		}
		if (!_espectadorOk && perso.getCuenta().getRango() == 0 || _estadoPelea != 3) {
			GestorSalida.ENVIAR_Im_INFORMACION(perso, "157");
			return;
		}
		int tiempoRestante = (tiempoPPVP - (int) (System.currentTimeMillis() - _tiempoInicioTurno));
		perso.setPelea(this);
		_mapaReal.borrarJugador(perso);
		GestorSalida.ENVIAR_GJK_UNIRSE_PELEA(perso, _estadoPelea, false, false, true, 0, _tipo);
		GestorSalida.ENVIAR_GS_EMPEZAR_COMBATE(perso);
		GestorSalida.ENVIAR_GTL_ORDEN_JUGADORES(perso, this);
		GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(perso.getMapa(), perso.getID());
		GestorSalida.ENVIAR_GM_LUCHADORES(this, _mapaCopia, perso);
		GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(this, 7, "036;" + perso.getNombre());
		if (_tipo == 4 || _tipo == 3) {
			for (Entry<Integer, Integer> entry : _retos.entrySet()) {
				String str = _stringReto.get(entry.getKey());
				if (!str.isEmpty()) {
					GestorSalida.ENVIAR_Gd_RETO_A_PERSONAJE(perso, str);
					if (entry.getValue() == 1) {
						GestorSalida.ENVIAR_GdaK_RETO_REALIZADO(perso, entry.getKey());
					} else if (entry.getValue() == 2) {
						GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(perso, entry.getKey());
					}
				}
			}
		}
		GestorSalida.ENVIAR_GTS_INICIO_TURNO_PELEA(perso, LucActual.getID(), tiempoRestante);
		_espectadores.put(perso.getID(), perso);
		mostrarBuffsDeTodosAPerso(perso, false);
	}

	private void mostrarBuffsDeTodosAPerso(Personaje perso, boolean esreconectado) {
		StringBuilder packet = new StringBuilder();
		for (Luchador luch : luchadoresDeEquipo(3)) {
			for (EfectoHechizo buff : luch.getBuffPelea().keySet()) {
				if (_estadoPelea == 4) {
					break;
				}
				String strx = luch.addBuff(buff.getEfectoID(), buff.getValor(), buff.getDuracion(), buff.getTurnos(),
						buff.esDesbufeable(), buff.getHechizoID(), buff.getArgs(), luch, buff.getPoison(), false);
				packet.append(strx).append((char) 0x00);
				String stra = buff.getArgs();
				switch (buff.getEfectoID()) {
				case 950:
					int idEstado = -1;
					try {
						idEstado = Integer.parseInt(stra.split(";")[2]);
					} catch (Exception e) {
						continue;
					}
					packet.append("GA;" + buff.getEfectoID() + ";" + luch.getID() + ";" + luch.getID() + "," + idEstado
							+ "," + buff.getDuracion()).append((char) 0x00);
					break;
				case 149:
					int id = -1;
					try {
						id = Integer.parseInt(stra.split(";")[2]);
					} catch (Exception e) {
						continue;
					}
					if (buff.getHechizoID() == 686) {
						if (luch.getPersonaje() != null && luch.getPersonaje().getSexo() == 1
								|| luch.getMob() != null && luch.getMob().getModelo().getID() == 547) {
							id = 8011;
						}
					}
					if (id == -1) {
						id = luch.getGfxDefecto();
					}
					int defecto = luch.getGfxDefecto();
					packet.append("GA;" + buff.getEfectoID() + ";" + luch.getID() + ";" + luch.getID() + "," + defecto
							+ "," + id + "," + buff.getDuracion()).append((char) 0x00);
					break;
				}
			}
		}
		if (!packet.toString().isEmpty()) {
			GestorSalida.enviar(perso, packet.toString());
		}
		if (esreconectado) {
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					StringBuilder packet2 = new StringBuilder();
					Luchador lux = getLuchadorPorPJ(perso);
					if (lux == null) {
						timer.cancel();
						return;
					}
					ArrayList<Integer> hecenv = new ArrayList<>();
					if (lux.getHechizosLanzados() != null) {
						if (lux.getHechizosLanzados().size() > 0) {
							for (HechizoLanzado hec : lux.getHechizosLanzados()) {// enviar GA;518;13;88,255,2,
																					// hechizoid,celda,valorturno
								if (hec.getSigLanzamiento() > 0) {
									hecenv.add(hec._hechizoId);
									packet2.append("GA;" + 518 + ";" + lux.getID() + ";" + hec._hechizoId + ","
											+ lux.getCeldaPelea().getID() + "," + hec.getSigLanzamiento() + ",")
											.append((char) 0x00);
								}
							}
						}
					}
					if (lux._buffsPelea.size() > 0) {
						for (EfectoHechizo luc : lux._buffsPelea.keySet()) {
							if (_estadoPelea == 4) {
								break;
							}
							if (luc.getDuracion() > 0 && luc.getEfectoID() == 950
									&& !hecenv.contains(luc.getHechizoID())) {
								packet2.append("GA;" + 518 + ";" + lux.getID() + ";" + luc.getHechizoID() + ","
										+ lux.getCeldaPelea().getID() + "," + luc.getDuracion() + ",")
										.append((char) 0x00);
							}
						}
					}
					if (lux._estados.size() > 0) {
						for (Entry<Integer, Integer> luc : lux._estados.entrySet()) {
							packet2.append("GA;" + 519 + ";" + lux.getID() + ";" + luc.getKey() + ",")
									.append((char) 0x00);

						}
					}
					if (!packet2.toString().isEmpty()) {
						GestorSalida.enviar(perso, packet2.toString());
					}
					timer.cancel();
				}
			}, 500);
		}
	}

	public boolean unirsePeleaRecaudador(Personaje perso, int recauID, short mapaID, int celdaID) {
		int tiempoRecaudador = 0;
		if (_Recaudador != null) {
			tiempoRecaudador = (tiempoPRecau - (int) (System.currentTimeMillis() - _tiempoInicioPelea));
		}
		if (tiempoRecaudador <= 2000) {
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "El combate empezarÁ en unos segundos, no te puedes unir ahora",
					Colores.ROJO);
			return false;
		}
		PrintWriter out = perso.getCuenta().getEntradaPersonaje().getOut();
		if (_tiempoInicio != 0L) {
			GestorSalida.ENVIAR_GA903_ERROR_PELEA(out, 'l');
			return false;
		}
		if (perso != null && perso.esFantasma()) {
			GestorSalida.ENVIAR_Im_INFORMACION(perso, "1116");
			return false;
		}
		Celda celda = getCeldaRandom(_celdasPos2);
		if (celda == null) {
			return false;
		}
		TpTimerRecau(perso, recauID, out, celda);
		return true;
	}

	public boolean checkUnirsePrisma(Personaje perso) {
		int tiempoPrisma = 0;
		if (_Prisma != null) {
			tiempoPrisma = (tiempoPPrisma - (int) (System.currentTimeMillis() - _tiempoInicioPelea));
		}
		if (tiempoPrisma <= 3500) {
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "El combate empezarÁ en unos segundos, no te puedes unir ahora",
					Colores.ROJO);
			return false;
		}
		PrintWriter out = perso.getCuenta().getEntradaPersonaje().getOut();
		if ((_tiempoInicio != 0L) || (_estadoPelea > 2)) {
			GestorSalida.ENVIAR_GA903_ERROR_PELEA(out, 'l');
			return false;
		}
		return true;
	}

	public boolean unirsePeleaPrisma(Personaje perso, int prismaID, int mapaID, int celdaID) {
		int tiempoPrisma = 0;
		if (_Prisma != null) {
			tiempoPrisma = (tiempoPPrisma - (int) (System.currentTimeMillis() - _tiempoInicioPelea));
		}
		if (tiempoPrisma <= 3500) {
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "El combate empezarÁ en unos segundos, no te puedes unir ahora",
					Colores.ROJO);
			return false;
		}
		PrintWriter out = perso.getCuenta().getEntradaPersonaje().getOut();
		if (_tiempoInicio != 0L) {
			GestorSalida.ENVIAR_GA903_ERROR_PELEA(out, 'l');
			return false;
		}
		Celda celda = getCeldaRandom(_celdasPos2);
		if (celda == null) {
			return false;
		}
		TpTimerPrisma(perso, prismaID, out, celda);
		return true;
	}

	private void TpTimerRecau(Personaje perso, int recauID, PrintWriter out, Celda celda) {
		Luchador jugadorAUnirse = null;
		int idPerso = perso.getID();
		int tiempoRecaudador = 0;
		if (_Recaudador != null) {
			tiempoRecaudador = (tiempoPRecau - (int) (System.currentTimeMillis() - _tiempoInicioPelea));
		}
		if (tiempoRecaudador <= 1500) {
			return;
		}
		GestorSalida.ENVIAR_GJK_UNIRSE_PELEA(perso, 2, false, true, false, tiempoRecaudador, _tipo);
		GestorSalida.ENVIAR_GP_POSICIONES_PELEA(out, _mapaCopia.getLugaresString(), _celdaColor2);
		GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 3, 950, idPerso + "", idPerso + "," + 8 + ",0");
		GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 3, 950, idPerso + "", idPerso + "," + 3 + ",0");
		GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(perso.getMapa(), perso.getID());
		Luchador luchador = new Luchador(this, perso);
		luchador.setEquipoBin(1);
		_equipo2.put(idPerso, luchador);
		perso.setPelea(this);
		luchador.setCeldaPelea(celda);
		_mapaCopia.addLuchador(luchador);
		jugadorAUnirse = luchador;
		GestorSalida.ENVIAR_ILF_CANTIDAD_DE_VIDA(perso, 0);
		_mapaReal.borrarJugador(perso);
		GestorSalida.ENVIAR_Gt_AGREGAR_NOMBRE_ESPADA(perso.getMapa(), recauID, jugadorAUnirse);
		GestorSalida.ENVIAR_GM_JUGADO_UNIRSE_PELEA(this, 7, jugadorAUnirse);
		try {
			Thread.sleep(400L);
		} catch (Exception e2) {
		}
		GestorSalida.ENVIAR_GM_LUCHADORES(this, _mapaCopia, perso);
		try {
			Thread.sleep(300);
		} catch (Exception e) {
		}
	}

	private void TpTimerPrisma(Personaje perso, int prismaID, PrintWriter out, Celda celda) {
		int tiempoPrisma = 0;
		if (_Prisma != null) {
			tiempoPrisma = (tiempoPPrisma - (int) (System.currentTimeMillis() - _tiempoInicioPelea));
		}
		if (tiempoPrisma <= 1500) {
			return;
		}
		Luchador jugadorAUnirse = null;
		int idPerso = perso.getID();
		try {
			Thread.sleep(500);
		} catch (Exception e) {
		}
		GestorSalida.ENVIAR_GJK_UNIRSE_PELEA(perso, 2, false, true, false, tiempoPrisma, _tipo);
		GestorSalida.ENVIAR_GP_POSICIONES_PELEA(out, _mapaCopia.getLugaresString(), _celdaColor2);
		GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 3, 950, idPerso + "", idPerso + "," + 8 + ",0");
		GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 3, 950, idPerso + "", idPerso + "," + 3 + ",0");
		GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(perso.getMapa(), idPerso);
		Luchador luchador = new Luchador(this, perso);
		luchador.setEquipoBin(1);
		_equipo2.put(idPerso, luchador);
		perso.setPelea(this);
		luchador.setCeldaPelea(celda);
		_mapaCopia.addLuchador(luchador);
		jugadorAUnirse = luchador;
		GestorSalida.ENVIAR_ILF_CANTIDAD_DE_VIDA(perso, 0);
		_mapaReal.borrarJugador(perso);
		GestorSalida.ENVIAR_Gt_AGREGAR_NOMBRE_ESPADA(perso.getMapa(), prismaID, jugadorAUnirse);
		GestorSalida.ENVIAR_GM_JUGADO_UNIRSE_PELEA(this, 7, jugadorAUnirse);
		try {
			Thread.sleep(300L);
		} catch (Exception e2) {
		}
		GestorSalida.ENVIAR_GM_LUCHADORES(this, _mapaCopia, perso);
		try {
			Thread.sleep(300);
		} catch (Exception e) {
		}
	}

	public void reconectarLuchador(Personaje perso) {
		if (_estadoPelea != 3 || perso == null) {
			return;
		}
		if (LucActual == null) {
			retirarsePelea(perso, null, false);
			return;
		}
		Luchador luc = getLuchadorPorPJ(perso);
		int tiempoRestante = (int) (tiempoPPVM - (System.currentTimeMillis() - _tiempoInicioTurno));
		_mapaReal.borrarJugador(perso);
		GestorSalida.ENVIAR_GP_POSICIONES_PELEA(perso, _mapaCopia.getLugaresString(), luc.getEquipoBin());
		GestorSalida.ENVIAR_GJK_UNIRSE_PELEA(perso, _estadoPelea, false, true, false, 0, _tipo);
		GestorSalida.ENVIAR_GM_LUCHADORES(this, _mapaCopia, perso);
		GestorSalida.ENVIAR_GS_EMPEZAR_COMBATE(perso);
		GestorSalida.ENVIAR_GTS_INICIO_TURNO_PELEA(perso, LucActual.getID(), tiempoRestante);
		GestorSalida.ENVIAR_GTL_ORDEN_JUGADORES(perso, this);
		GestorSalida.ENVIAR_GTM_INFO_STATS_TODO_LUCHADORES(this, perso);
		try {
			Thread.sleep(200L);
		} catch (Exception e) {
		}
		if ((_tipo == 4) || (_tipo == 3)) {
			for (Entry<Integer, Integer> entry : _retos.entrySet()) {
				String str = _stringReto.get(entry.getKey());
				if (!str.isEmpty()) {
					GestorSalida.ENVIAR_Gd_RETO_A_PERSONAJE(perso, str);
					if ((entry.getValue()) == 1) {
						GestorSalida.ENVIAR_GdaK_RETO_REALIZADO(perso, (entry.getKey()));
					} else if ((entry.getValue()) == 2) {
						GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(perso, (entry.getKey()));
					}
				}
			}
		}
		try {
			luc._desconectado = false;
			luc.penalizado = 0;
			luc.realizaaccion = true;
			GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(this, 7, "1184;" + perso.getNombre());
		} catch (Exception e) {
		}
		mostrarBuffsDeTodosAPerso(perso, true);
	}

	public void desconectarLuchador(Personaje perso) {
		Luchador luchador = getLuchadorPorPJ(perso);
		if (luchador == null) {
			return;
		}
		perso.primerRefresh = true;
		luchador._desconectado = true;
		if (luchador.puedeJugar()) {
			GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(this, 7,
					"1182;" + luchador.getNombreLuchador() + "~" + luchador._turnosRestantes);
			luchador._turnosRestantes -= 1;
			finTurno("desconexion");
		}
	}

	public void botonBloquearMasJug(int id) {
		if (peleainvasion || esAngel || enTorneo || (_mapaCopia.getID() == 27105)) {
			return;
		}
		if (_luchInit1 != null && _idLuchInit1 == id) {
			_cerrado1 = !_cerrado1;
			GestorSalida.ENVIAR_Go_BOTON_ESPEC_AYUDA_CERRADO(_mapaReal, _cerrado1 ? '+' : '-', 'A', id);
			GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(this, 1, _cerrado1 ? "095" : "096");
		} else if (_luchInit2 != null && _idLuchInit2 == id) {
			_cerrado2 = !_cerrado2;
			GestorSalida.ENVIAR_Go_BOTON_ESPEC_AYUDA_CERRADO(_mapaReal, _cerrado2 ? '+' : '-', 'A', id);
			GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(this, 2, _cerrado2 ? "095" : "096");
		}
	}

	public synchronized void botonSoloGrupo(int id) {
		if (peleainvasion || esAngel || (_mapaCopia.getID() == 27105)) {
			return;
		}
		if (_luchInit1 != null && _idLuchInit1 == id) {
			_soloGrupo1 = !_soloGrupo1;
			if (_soloGrupo1) {
				ArrayList<Integer> lista = new ArrayList<>();
				ArrayList<Integer> expulsar = new ArrayList<>();
				lista.addAll(_luchInit1.getPersonaje().getGrupo().getIDsPersos());
				for (Entry<Integer, Luchador> entry : _equipo1.entrySet()) {
					int expulsadoID = entry.getKey();
					Luchador luch = entry.getValue();
					if (!lista.contains(expulsadoID)) {
						expulsar.add(expulsadoID);
						GestorSalida.ENVIAR_GM_BORRAR_LUCHADOR(this, expulsadoID, 3);
						GestorSalida.ENVIAR_GV_RESETEAR_PANTALLA_JUEGO(luch.getPersonaje());
						luch.getPersonaje().retornoMapa();
						_mapaCopia.removerLuchador(luch);
						GestorSalida.ENVIAR_Gt_BORRAR_NOMBRE_ESPADA(_mapaReal, _idLuchInit1, luch);
					}
				}
				for (Integer ID : expulsar) {
					_equipo1.remove(ID);
				}
			}
			GestorSalida.ENVIAR_Go_BOTON_ESPEC_AYUDA_CERRADO(_mapaReal, _soloGrupo1 ? '+' : '-', 'P', id);
			GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(this, 1, _soloGrupo1 ? "093" : "094");
		} else if (_luchInit2 != null && _idLuchInit2 == id) {
			_soloGrupo2 = !_soloGrupo2;
			if (_soloGrupo2) {
				ArrayList<Integer> lista = new ArrayList<>();
				ArrayList<Integer> expulsar = new ArrayList<>();
				lista.addAll(_luchInit2.getPersonaje().getGrupo().getIDsPersos());
				for (Entry<Integer, Luchador> entry : _equipo2.entrySet()) {
					int expulsadoID = entry.getKey();
					Luchador luch = entry.getValue();
					if (!lista.contains(expulsadoID)) {
						expulsar.add(expulsadoID);
						GestorSalida.ENVIAR_GM_BORRAR_LUCHADOR(this, expulsadoID, 3);
						GestorSalida.ENVIAR_GV_RESETEAR_PANTALLA_JUEGO(luch.getPersonaje());
						luch.getPersonaje().retornoMapa();
						_mapaCopia.removerLuchador(luch);
						GestorSalida.ENVIAR_Gt_BORRAR_NOMBRE_ESPADA(_mapaReal, _idLuchInit2, luch);
					}
				}
				for (Integer ID : expulsar) {
					_equipo2.remove(ID);
				}
			}
			GestorSalida.ENVIAR_Go_BOTON_ESPEC_AYUDA_CERRADO(_mapaReal, _soloGrupo2 ? '+' : '-', 'P', id);
			GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(this, 2, _soloGrupo2 ? "095" : "096");
		}
	}

	public synchronized void botonBloquearEspect(int id) {
		if (peleainvasion || esAngel || enTorneo || (_mapaCopia.getID() == 27105)) {
			return;
		}
		if ((_luchInit1 != null && _idLuchInit1 == id) || (_luchInit2 != null && _idLuchInit2 == id)) {
			_espectadorOk = !_espectadorOk;
			if (_idLuchInit1 == id) {
				GestorSalida.ENVIAR_Go_BOTON_ESPEC_AYUDA_CERRADO(_mapaReal, _espectadorOk ? '+' : '-', 'S',
						_idLuchInit1);
			} else {
				GestorSalida.ENVIAR_Go_BOTON_ESPEC_AYUDA_CERRADO(_mapaReal, _espectadorOk ? '+' : '-', 'S',
						_idLuchInit2);
			}
			GestorSalida.ENVIAR_Im_INFORMACION_A_MAPA(_mapaCopia, _espectadorOk ? "039" : "040");
		}
		if (_espectadores.size() > 0) {
			Map<Integer, Personaje> espectadoresN = new TreeMap<>();
			for (Personaje espec : _espectadores.values()) {
				if (espec.getCuenta().getRango() > 0) {
					espectadoresN.put(espec.getID(), espec);
					continue;
				}
				GestorSalida.ENVIAR_GV_RESETEAR_PANTALLA_JUEGO(espec);
				espec.retornoMapa();
			}
			_espectadores.clear();
			_espectadores.putAll(espectadoresN);
		}
	}

	public void botonAyuda(int id) {
		if (_luchInit1 != null && _idLuchInit1 == id) {
			_ayuda1 = !_ayuda1;
			GestorSalida.ENVIAR_Go_BOTON_ESPEC_AYUDA_CERRADO(_mapaReal, _ayuda1 ? '+' : '-', 'H', id);
			GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(this, 1, _ayuda1 ? "0103" : "0104");
		} else if (_luchInit2 != null && _idLuchInit2 == id) {
			_ayuda2 = !_ayuda2;
			GestorSalida.ENVIAR_Go_BOTON_ESPEC_AYUDA_CERRADO(_mapaReal, _ayuda2 ? '+' : '-', 'H', id);
			GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(this, 2, _ayuda2 ? "0103" : "0104");
		}
	}

	private void setEstado(int estado) {
		_estadoPelea = estado;
	}

	private void setGremioID(int gremioID) {
		_gremioID = gremioID;
	}

	public int getEstado() {
		return _estadoPelea;
	}

	public int getGremioID() {
		return _gremioID;
	}

	public int getTipoPelea() {
		return _tipo;
	}

	public static int getPorcHuida(Luchador movedor, ArrayList<Luchador> tacle) {// sabiduria sirve para que no me
																					// quiten pa y pm
		int miAgi = movedor.getTotalStatsConBuff().getEfecto(CentroInfo.STATS_ADD_AGILIDAD);
		int mifuga = Math.round(miAgi / 10);
		int esquivapa = movedor.getTotalStatsConBuff().getEfecto(161);
		int negativos = movedor.getTotalStatsConBuff().getEfecto(163);
		int totalt = esquivapa - negativos;
		int extraadd = Math.round((mifuga * totalt) / 100);
		mifuga += extraadd;
		if (mifuga < 0) { // yo quiero escapar
			mifuga = 0;
		}
		int miplacaje = Math.round(miAgi / 10); // bloquear a mi enemigo
		miplacaje += Math.round((mifuga * totalt) / 100);
		if (miplacaje < 0) {
			miplacaje = 0;
		}
		// enemigo
		@SuppressWarnings("unused")
		int totalfuga = 0;
		int totalPlaca = 0;
		for (Luchador T : tacle) {
			if (T.esInvisible() || T.statico || T._estaMuerto) {
				continue;
			}
			int eneAgi = 0;
			if (T.esDoble()) {
				eneAgi = (T.getTotalStatsConBuff().getEfecto(CentroInfo.STATS_ADD_AGILIDAD) / 2);
			} else {
				if (T.esInvocacion()) {
					eneAgi = T.getTotalStatsConBuff().getEfecto(CentroInfo.STATS_ADD_AGILIDAD) / 100;
				} else {
					eneAgi = T.getTotalStatsConBuff().getEfecto(CentroInfo.STATS_ADD_AGILIDAD);
				}
			}
			int eneesquivapa = T.getTotalStatsConBuff().getEfecto(161);
			int enenegativos = T.getTotalStatsConBuff().getEfecto(163);
			int totaltene = eneesquivapa - enenegativos;

			int enefuga = Math.round(eneAgi / 10);
			int extraf = Math.round((enefuga * totaltene) / 100);
			enefuga += extraf;
			totalfuga += enefuga;

			int placajeenemigo = Math.round(eneAgi / 10);
			int extraplaca = Math.round((placajeenemigo * totaltene) / 100);
			placajeenemigo += extraplaca;
			if (placajeenemigo < 0) {
				placajeenemigo = 0;
			}
			totalPlaca += placajeenemigo;
		}

		// fin enemigo
		double perdida = 100;
		int mispms = movedor.getTempPM(movedor.getPelea());
		if ((mispms * (mifuga + 2) - 2) < totalPlaca) {
			// int misabiduria = movedor.getTotalStatsConBuff().getEfecto(124);
			perdida = 0;
			if (movedor.getPersonaje() != null) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(movedor.getPersonaje(), "El enemigo tiene <b>+" + totalPlaca
						+ "</b> Placaje y t� tienes <b>+" + mifuga + "</b> Fuga. Te ha bloqueado completamente.",
						Colores.ROJO);
			}
		} else {
			if (mifuga > (totalPlaca * 2)) {
				perdida = 100;
				if (movedor.getPersonaje() != null) {
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(movedor.getPersonaje(),
							"Tienes el doble de Fuga que el placaje del enemigo y te has escapado", Colores.ROJO);
				}
			} else {
				if (mifuga == totalPlaca) {
					int random = Formulas.getRandomValor(1, 3);
					if (random <= 2) {
						return 100;
					} else {
						if (movedor.getPersonaje() != null) {
							GestorSalida.ENVIAR_cs_CHAT_MENSAJE(movedor.getPersonaje(),
									"T� y el enemigo teneis el mismo valor de Fuga y Placaje, tenÍas un 50% de probabilidades de esquiva pero has fallado",
									Colores.ROJO);
						}
						return 0;
					}
				}
				double a = mifuga + 2;
				double b = 2 * totalPlaca + 2;
				perdida = (a / b) * 100;
				if (perdida < 0) {
					perdida = 0;
				}
				if (perdida > 100) {
					perdida = 100;
				}
				if (movedor.getPersonaje() != null) {
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(movedor.getPersonaje(),
							"El enemigo tiene <b>+" + totalPlaca + "</b> Placaje y t� tienes <b>+" + mifuga
									+ "</b> Fuga. Te mantienes con el <b>" + (int) perdida + "%</b> de tus PMs",
							Colores.ROJO);
				}
			}
		}
		return (int) perdida;
	}

	public boolean puedeMoverseLuchador(Luchador movedor, AccionDeJuego GA, String destino) {
		String path = GA._args;
		if ((path.equals(""))) {
			return false;
		}
		Luchador luchador = LucActual;
		if ((luchador == null) || (!_tempAccion.isEmpty()) || (luchador.getID() != movedor.getID()) || (_estadoPelea != 3)) {
			return false;
		}
		Personaje perso = movedor.getPersonaje();
		if (perso != null) {
			luchador.penalizado = 0;
			luchador.realizaaccion = true;
		}
		if (perso == null || luchador.esDoble()) {
			esperaMove = true;
		}
		ArrayList<Luchador> enemigosAlrededor = Camino.getEnemigosAlrededor(movedor, _mapaCopia, this);
		if ((enemigosAlrededor != null) && (!movedor.tieneEstado(6))
				&& (!movedor.tieneEstado(8) && !movedor.esInvisible())) {
			ArrayList<Luchador> enemigosAlrededor2 = new ArrayList<>();
			enemigosAlrededor2.addAll(enemigosAlrededor);
			for (Luchador T : enemigosAlrededor) {
				if (T.tieneEstado(6) || T.statico) {
					enemigosAlrededor2.remove(T);
				}
			}
			if (!enemigosAlrededor2.isEmpty()) {
				int perdida = getPorcHuida(movedor, enemigosAlrededor2);
				if (movedor.excepcionMover) {
					perdida = -1;
				}
				if (perdida <= 100 && perdida >= 0) {
					int pierdePM = _tempLuchadorPM * perdida / 100;
					int haperd = _tempLuchadorPM - pierdePM;
					_tempLuchadorPM = pierdePM;
					if (pierdePM < 0) {
						pierdePM = 0;
					}
					if (haperd > 0) {
						GestorSalida.ENVIAR_GA_ACCION_PELEA_CON_RESPUESTA(this, 7, GA._idUnica, "129",
								movedor.getID() + "", movedor.getID() + ",-" + haperd);
					}
					if (_tempLuchadorPM <= 0) {
						GestorSalida.ENVIAR_GA_ACCION_PELEA_CON_RESPUESTA(this, 7, GA._idUnica, "104",
								movedor.getID() + ";", "");
						return false;
					}
				}
				/*
				 * if (rand > chance) { GestorSalida.ENVIAR_GA_ACCION_PELEA_CON_RESPUESTA(this,
				 * 7, GA._idUnica, "104", movedor.getID() + ";", ""); int pierdePA =
				 * _tempLuchadorPA*chance/100; if(pierdePA < 0) pierdePA = -pierdePA;
				 * if(_tempLuchadorPM < 0) _tempLuchadorPM = 0; if (_tempLuchadorPM < 0)
				 * _tempLuchadorPM = 0; if (_tempLuchadorPA < 0) _tempLuchadorPA = 0;
				 * GestorSalida.ENVIAR_GA_ACCION_PELEA_CON_RESPUESTA(this, 7, GA._idUnica,
				 * "129", movedor.getID() + "", movedor.getID() + ",-" + _tempLuchadorPM);
				 * GestorSalida.ENVIAR_GA_ACCION_PELEA_CON_RESPUESTA(this, 7, GA._idUnica,
				 * "102", movedor.getID() + "", movedor.getID() + ",-" + pierdePA);
				 * _tempLuchadorPM = 0; _tempLuchadorPA = _tempLuchadorPA-pierdePA; return
				 * false; }
				 */
			}
		}
		if (movedor.excepcionMover) {
			movedor.excepcionMover = false;
		}
		AtomicReference<String> pathRef = new AtomicReference<>(path);
		int nroCeldasMov = Camino.caminoValido(_mapaCopia, movedor.getCeldaPelea().getID(), pathRef, this, movedor);
		String nuevoPath = pathRef.get();
		if (nroCeldasMov > _tempLuchadorPM) {
			nroCeldasMov = _tempLuchadorPM;
		}
		if ((nroCeldasMov > _tempLuchadorPM) || (nroCeldasMov == -1000)) {
			if (movedor.getPersonaje() != null) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(movedor.getPersonaje(),
						"No puedes moverte en esta celda por que estÁ ocupada.", Colores.ROJO);
				GestorSalida.ENVIAR_GA_ACCION_PELEA_CON_RESPUESTA(this, 7, GA._idUnica, "129", movedor.getID() + "",
						movedor.getID() + ",-" + 0);
			}
			return false;
		}
		_tempLuchadorPM -= nroCeldasMov;
		_tempLuchadorPMusados += nroCeldasMov;
		if (((_tipo == 4) || (_tipo == 3)) && (movedor.getPersonaje() != null)) {
			Map<Integer, Integer> copiaRetos = new TreeMap<>();
			copiaRetos.putAll(_retos);
			for (Entry<Integer, Integer> entry : copiaRetos.entrySet()) {
				int reto = (entry.getKey());
				int exitoReto = (entry.getValue());
				if (exitoReto != 0) {
					continue;
				}
				switch (reto) {
				case 1:
					if (_tempLuchadorPMusados == 1) {
						break;
					}
					GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
					exitoReto = 2;
					break;
				}
				if (exitoReto != 0) {
					_retos.remove(reto);
					_retos.put(reto, exitoReto);
				}
			}
		}
		short sigCeldaID = Encriptador.celdaCodigoAID(nuevoPath.substring(nuevoPath.length() - 2));
		String ultPathMov = nuevoPath.substring(nuevoPath.length() - 3);
		if (_mapaCopia.getCelda(sigCeldaID) == null) {
			return false;
		}
		if (perso != null) {
			GestorSalida.ENVIAR_GAS_INICIO_DE_ACCION(this, 7, movedor.getID());
		}
		if (!movedor.esInvisible()) {// ESTO SE MUEVE DOBLE
			GestorSalida.ENVIAR_GA_ACCION_PELEA_CON_RESPUESTA(this, 7, GA._idUnica, "1", movedor.getID() + "",
					"a" + Encriptador.celdaIDACodigo(movedor.getCeldaPelea().getID()) + nuevoPath);
		} else if (perso != null) {
			PrintWriter out = perso.getCuenta().getEntradaPersonaje().getOut();
			if (out != null) {
				GestorSalida.ENVIAR_GA_ACCION_DE_JUEGO(out, GA._idUnica + "", "1", movedor.getID() + "",
						"a" + Encriptador.celdaIDACodigo(movedor.getCeldaPelea().getID()) + nuevoPath);
			}
		}
		Luchador portador = movedor.getTransportadoPor(); // el que me tiene levantado
		if ((portador != null) && (movedor.tieneEstado(8)) && (portador.tieneEstado(3))) {
			if (sigCeldaID != portador.getCeldaPelea().getID()) {
				_mapaCopia.removerLuchador(movedor);
				portador.setEstado(3, 0);
				movedor.setEstado(8, 0);
				portador.setTransportado(null);
				movedor.setTransportadoPor(null);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 950, portador.getID() + "",
						portador.getID() + "," + 3 + ",0");
				GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 950, movedor.getID() + "",
						movedor.getID() + "," + 8 + ",0");
			} else {
				_mapaCopia.removerLuchador(movedor);
			}
		} else {
			_mapaCopia.removerLuchador(movedor);
		}
		movedor.setDireccion(ultPathMov.charAt(0));
		movedor.setCeldaPelea(_mapaCopia.getCelda(sigCeldaID));
		_mapaCopia.addLuchador(movedor);
		if (nroCeldasMov < 0) {
			nroCeldasMov *= -1;
		}
		_tempAccion = ("GA;129;" + movedor.getID() + ";" + movedor.getID() + ",-" + nroCeldasMov);
		Luchador tranportado = movedor.getTransportando(); // SOY YO QUE ME LLEVAN
		if ((tranportado != null) && (movedor.tieneEstado(3)) && (tranportado.tieneEstado(8))) {
			tranportado.setCeldaPelea(movedor.getCeldaPelea());
			_mapaCopia.addLuchador(tranportado);
		}
		if (perso == null) {
			boolean excepcion = false;
			ArrayList<Trampa> trampas = new ArrayList<>();
			trampas.addAll(getTrampas());
			int distantes = movedor.getCeldaPelea().getID();
			for (Trampa t : trampas) {
				if (t.activada) {
					continue;
				}
				int dist = Camino.distanciaEntreDosCeldas(_mapaCopia, t.getCelda().getID(),
						movedor.getCeldaPelea().getID());
				if (dist <= t.getTamaÑo()) {
					excepcion = true;
					t.activaTrampa(movedor);
				}
			}
			if (excepcion) {
				char dist = Camino.getDirEntreDosCeldas(distantes, movedor.getCeldaPelea().getID(), getMapaCopia(),
						true);
				int c1 = -1;
				int buclex = 0;
				while (c1 != movedor.getCeldaPelea().getID()) {
					if (getMapaCopia() == null) {
						buclex = 0;
						break;
					}
					if (buclex >= 5) {
						break;
					}
					c1 = Camino.getSigIDCeldaMismaDir(distantes, dist, getMapaCopia(), true);
					if (getMapaCopia().getCelda(c1) == null) {
						break;
					} else {
						for (Trampa t : trampas) {
							if (t.activada) {
								continue;
							}
							int dist1 = Camino.distanciaEntreDosCeldas(_mapaCopia, t.getCelda().getID(), c1);
							if (dist1 <= t.getTamaÑo()) {
								buclex += 1;
								break;
							}
						}
					}
					distantes = c1;
					buclex += 1;
				}
				try {
					Thread.sleep(buclex * 350L);
				} catch (Exception e) {
				}
			}
			GestorSalida.ENVIAR_GAMEACTION_A_PELEA(this, 7, _tempAccion);
			int bucle = 0;
			while (esperaMove && bucle < 15) {
				try {
					Thread.sleep(300L);
					bucle += 1;
				} catch (Exception e) {
				}
			}
			_tempAccion = "";
		} else {
			perso.addGA(GA);
		}
		return true;
	}

	public void finalizarMovimiento(Personaje perso, AccionDeJuego AJ) {
		if (_tempAccion.isEmpty()) {
			return;
		}
		int idLuchador = LucActual.getID();
		if (_tempAccion.equals("") || idLuchador != perso.getID() || _estadoPelea != 3) {
			return;
		}
		Luchador luchador = getLuchadorPorPJ(perso);
		if (luchador == null) {
			GestorSalida.ENVIAR_BN_NADA(perso);
			return;
		}
		GestorSalida.ENVIAR_GAMEACTION_A_PELEA(this, 7, _tempAccion);
		GestorSalida.ENVIAR_GAF_FINALIZAR_ACCION(this, 7, 2, idLuchador);
		ArrayList<Trampa> trampas = new ArrayList<>();
		trampas.addAll(getTrampas());
		for (Trampa trampa : trampas) {
			if (_estadoPelea == 4) {
				break;
			}
			if (trampa.activada) {
				continue;
			}
			Luchador F = getLuchadorPorPJ(perso);
			int dist = Camino.distanciaEntreDosCeldas(_mapaCopia, trampa.getCelda().getID(), F.getCeldaPelea().getID());
			if (dist <= trampa.getTamaÑo()) {
				trampa.activaTrampa(F);
			}
		}
		_tempAccion = "";
	}

	public void pasarTurno(Personaje perso) {
		if (!_tempAccion.equals("")) {
			return;
		}
		Luchador luchador = getLuchadorPorPJ(perso);
		if (luchador == null || !luchador.puedeJugar()) {
			return;
		}
		if (LucActual.getPersonaje() == perso) {
			finTurno("PASARTURNO");
		} else {
			if ((System.currentTimeMillis() - _tiempoInicioTurno) >= 45000) {
				finTurno("PASARTURNO");
			}
		}
	}

	// private Map<String, Integer> mismoObj = new TreeMap<String, Integer>();

	public int intentarLanzarHechizo(Luchador lanzador, StatsHechizos sHechizo, int celdaID) {
		if (_estadoPelea != 3) {
			return 0;
		}
		Personaje perso = lanzador.getPersonaje();
		if (!_tempAccion.isEmpty() && perso != null || sHechizo == null || !lanzador.puedeJugar()) {
			if (perso != null) {
				GestorSalida.ENVIAR_Im_INFORMACION(perso, "1175");
				GestorSalida.ENVIAR_GAF_FINALIZAR_ACCION(this, 7, 0, perso.getID());
				return 0;
			}
		}
		if (perso != null) {
			lanzador.penalizado = 0;
			lanzador.realizaaccion = true;
		}
		_tempAccion = "lanzando hechizo";
		Celda celda = _mapaCopia.getCelda(celdaID);
		boolean esFC = sHechizo.getPorcFC() != 0
				&& Formulas.getRandomValor(1, sHechizo.getPorcFC()) == sHechizo.getPorcFC();
		if (puedeLanzarHechizo(lanzador, sHechizo, celda, (short) -1, false, false)) {
			if (perso == null) {
				esperaHechizo = true;
			}
			List<String> list = new ArrayList<>();
			list.add("#0000FF");
			list.add("#FF4000");
			list.add("#8A0829");
			list.add("#0B610B");
			list.add("#8B1EDE");
			list.add("#61210B");
			String random = list.get(new Random().nextInt(list.size()));
			GestorSalida.GAME_SEND_MESSAGE3(this, random);
			if (perso != null) {
				GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso);
			}
			if (lanzador.getTipo() == 1 && perso.getHechizosSetClase().containsKey(sHechizo.getHechizoID())) {
				int modi = perso.getModifSetClase(sHechizo.getHechizoID(), 285);
				_tempLuchadorPA -= sHechizo.getCostePA() - modi;
				_tempLuchadorPAusados += sHechizo.getCostePA() - modi;
			} else {
				_tempLuchadorPA -= sHechizo.getCostePA();
				_tempLuchadorPAusados += sHechizo.getCostePA();
			}
			if (lanzador.esInvisible()) {
				GestorSalida.ENVIAR_Gf_MOSTRAR_CASILLA_EN_PELEA(this, 7, lanzador.getID(),
						lanzador.getCeldaPelea().getID());
			}
			GestorSalida.ENVIAR_GAS_INICIO_DE_ACCION(this, 7, lanzador.getID());
			if (esFC) {
				GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 302, lanzador.getID() + "", sHechizo.getHechizoID() + "");
				GestorSalida.ENVIAR_GAF_FINALIZAR_ACCION(this, 7, 0, lanzador.getID());
			} else {
				if ((_tipo == 4 || _tipo == 3) && lanzador.getPersonaje() != null) {
					Map<Integer, Integer> copiaRetos = new TreeMap<>();
					copiaRetos.putAll(_retos);
					for (Entry<Integer, Integer> entry : copiaRetos.entrySet()) {
						int reto = entry.getKey();
						int exitoReto = entry.getValue();
						if (exitoReto != 0) {
							continue;
						}
						switch (reto) {
						case 5: // Ahorrador
							if (lanzador._hechiLanzadosReto.contains(sHechizo.getHechizoID())) {
								GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
								exitoReto = 2;
							} else {
								lanzador._hechiLanzadosReto.add(sHechizo.getHechizoID());
							}
							break;
						case 6: // versatil
							if (lanzador._hechiLanzadosReto.contains(sHechizo.getHechizoID())) {
								GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
								exitoReto = 2;
							} else {
								lanzador._hechiLanzadosReto.add(sHechizo.getHechizoID());
							}
							break;
						case 9: // barbaro
							GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
							exitoReto = 2;
							break;
						case 18: // incurable
							if (sHechizo.efectosID.contains(108)) {
								GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
								exitoReto = 2;
							}
							break;
						case 20: // elemental
							for (EfectoHechizo efecto : sHechizo.getEfectos()) {
								if (_estadoPelea == 4) {
									break;
								}
								int efectoID = efecto.getEfectoID();
								if (efectoID >= 85 && efectoID <= 100 && efectoID != 90) {
									if (_elementoReto == 0) {
										_elementoReto = CentroInfo.efectoElemento(efectoID);
									} else {
										if (CentroInfo.efectoElemento(efectoID) != _elementoReto) {
											GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
											exitoReto = 2;
										}
									}
								}
							}
							break;
						case 24: // limitado
							int hechizoID = sHechizo.getHechizoID();
							if (lanzador._idHechiLanzReto == -1) {
								lanzador._idHechiLanzReto = hechizoID;
							} else {
								if (lanzador._idHechiLanzReto != hechizoID) {
									GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
									exitoReto = 2;
								}
							}
							break;
						}
						if (exitoReto != 0) {
							_retos.remove(reto);
							_retos.put(reto, exitoReto);
						}
					}
				}
				boolean esGC = lanzador.testSiEsGC(sHechizo.getPorcGC(), sHechizo, lanzador);
				String hechizoStr = sHechizo.getHechizoID() + "," + celdaID + "," + sHechizo.getSpriteID() + ","
						+ sHechizo.getNivel() + "," + sHechizo.getSpriteInfos();
				GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 300, lanzador.getID() + "", hechizoStr);
				if (esGC) {
					if (perso != null) {
						GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 301, lanzador.getID() + "", hechizoStr);
					}
				}
				if (lanzador.esInvisible() && sHechizo.getHechizoID() == 446) {
					lanzador.unHide(446);
				}
				// if(lanzador.esInvisible())//TODO: METER ESTA VARIABLE
				// showCaseToAll(lanzador.getID(), lanzador.getCeldaPelea().getID());
				if (sHechizo.getHechizoID() == 59) {
					esGC = false;
				}
				char dir = Camino.getDirEntreDosCeldas(_mapaCopia, lanzador.getCeldaPelea().getID(), celda.getID());
				if (dir != 0) {
					lanzador.setDireccion(dir);
					GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 11, lanzador.getID() + "",
							lanzador.getID() + "," + Camino.getIndexDireccion(dir));
				}
				sHechizo.aplicaHechizoAPelea(this, lanzador, celda, esGC, false);
			}
			if (lanzador.getTipo() == 1 && perso.getHechizosSetClase().containsKey(sHechizo.getHechizoID())) {
				int modi = perso.getModifSetClase(sHechizo.getHechizoID(), 285);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 102, lanzador.getID() + "",
						lanzador.getID() + ",-" + (sHechizo.getCostePA() - modi));
			} else {
				GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 102, lanzador.getID() + "",
						lanzador.getID() + ",-" + sHechizo.getCostePA());
			}
			if (!esFC) {
				// if (lanzador.getMob() != null) {
				lanzador.addHechizoLanzado(_mapaCopia.getPrimerLuchador(celda.getID()), sHechizo, lanzador);
				// }
			}
			if (lanzador.getMob() != null || lanzador.esRecaudador() || lanzador.esPrisma()) {
				int bucle = 0;
				while (esperaHechizo && bucle < 15) {
					try {
						Thread.sleep(350L);
						bucle += 1;
					} catch (Exception e) {
					}
				}
			}
			switch (sHechizo.getHechizoID()) {
			case 1676:
			case 198:// potencia silvestre
			case 542:// metamurfosis
			case 552:// DiCarnio
			case 578:// Dicarnioto
			case 579:// MetamurfÓsis Fantasma
			case 605:// pipa de la paz
			case 630:// CaparazÓn de Alas
			case 701:// Ira de Zatoishwan
			case 772:
			case 838:
			case 869:
			case 699:// leche de bambu
			case 881:
			case 896:
			case 898:
			case 899:
			case 900:
			case 901:
			case 434:// atraccion
			case 67: // miedo
			case 696:
				tienePasar = 2000;
				timeActual = System.currentTimeMillis();
				break;
			case 99:
				tienePasar = 3000;
				timeActual = System.currentTimeMillis();
				break;
			default:
				tienePasar = 700;
				timeActual = System.currentTimeMillis();
				break;
			}
			if (perso != null) {
				if (perso.buffClase == 2) {
					int cura = (lanzador.getPDVMaxConBuff() * 1) / 100;
					if ((lanzador.getPDVConBuff() + cura) >= lanzador.getPDVMaxConBuff()) {
						cura = lanzador.getPDVMaxConBuff() - lanzador.getPDVConBuff();
					}
					if (cura > 0) {
						lanzador.restarPDV(-cura);
						GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 100, lanzador.getID() + "",
								lanzador.getID() + ",+" + cura);
					}
				}
			}
			if ((esFC && sHechizo.esFinTurnoSiFC()) || sHechizo.getHechizoID() == 1735) {
				_tempAccion = "";
				if (lanzador.getMob() != null || lanzador.esInvocacion()) {
					finTurno("lanzahechizo4");
				} else {
					finTurno("lanzahechizo5");
				}
				return 5;
			}
			GestorSalida.ENVIAR_GAF_FINALIZAR_ACCION(this, 7, 0, lanzador.getID());
			if (!acaboPelea("lanzahechizo 1")) {
				_tempAccion = "";
				return 10;
			}
		} else if (lanzador.getMob() != null || lanzador.esInvocacion()) {
			_tempAccion = "";
			return 0;
		}
		if (lanzador.getPersonaje() != null) {
			if (lanzador.getTransportadoPor() == null && lanzador.getTransportando() == null
					&& sHechizo.getHechizoID() == 696) {
				GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 950, lanzador.getID() + "",
						lanzador.getID() + "," + 3 + ",0");
				GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 950, lanzador.getID() + "",
						lanzador.getID() + "," + 8 + ",0");
				lanzador.setEstado(3, 0);
				lanzador.setEstado(8, 0);
			}
			GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 102, lanzador.getID() + "", lanzador.getID() + ",-0"); // annonce
																												// le
																												// co�t
																												// en PA
			if (lanzador.getPDVConBuff() <= 0) {
				agregarAMuertos(lanzador, lanzador, "");
			}
		}
		_tempAccion = "";
		if (esFC) {
			return 5;
		} else {
			return 10;
		}
	}

	long tienePasar = 0;
	long timeActual = 0;

	public boolean esperaHechizo = false;
	public boolean esperaMove = false;
	public int intentos = 0;

	public void intentarCaC(Personaje perso, int celdaID) { // lanzamientos cuerpo a cuerpo CAC
		if (_estadoPelea != 3) {
			return;
		}
		Luchador lanzador = getLuchadorPorPJ(perso);
		if (lanzador == null || !lanzador.puedeJugar()) {
			GestorSalida.ENVIAR_Im_INFORMACION(perso, "1175");
			return;
		}
		if (LucActual.getID() != lanzador.getID()) { // que no se pegue a si mismo
			return;
		}
		if (perso != null) {
			lanzador.penalizado = 0;
			lanzador.realizaaccion = true;
		}
		// retos
		if (_tipo == 4 || _tipo == 3) {



			Map<Integer, Integer> copiaRetos = new TreeMap<>();
			copiaRetos.putAll(_retos);
			for (Entry<Integer, Integer> entry : copiaRetos.entrySet()) {
				int reto = entry.getKey();
				int exitoReto = entry.getValue();
				if (exitoReto != 0) {
					continue;
				}
				switch (reto) {
				case 5:// Ahorrador
					if (lanzador._hechiLanzadosReto.contains(0)) {
						GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
						exitoReto = 2;
					} else {
						lanzador._hechiLanzadosReto.add(0);
					}
					break;
				case 6:// versatil
					if (lanzador._hechiLanzadosReto.contains(0)) {
						GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
						exitoReto = 2;
					} else {
						lanzador._hechiLanzadosReto.add(0);
					}
					break;
				case 11:// mistico
					GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
					exitoReto = 2;
					break;
				case 24: // limitado
					int hechizoID = 0;
					if (lanzador._idHechiLanzReto == -1) {
						lanzador._idHechiLanzReto = hechizoID;
					} else {
						if (lanzador._idHechiLanzReto != hechizoID) {
							GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
							exitoReto = 2;
						}
					}
					break;
				}
				if (exitoReto != 0) {
					_retos.remove(reto);
					_retos.put(reto, exitoReto);
				}
			}
		}
		Objeto arma = perso.getObjPosicion(CentroInfo.ITEM_POS_ARMA);
		if (lanzador.esInvisible()) {
			lanzador.unHide(-1);
		}
		List<String> list = new ArrayList<>();
		list.add("#0000FF");
		list.add("#FF4000");
		list.add("#8A0829");
		list.add("#0B610B");
		list.add("#8B1EDE");
		list.add("#61210B");
		String random = list.get(new Random().nextInt(list.size()));
		GestorSalida.GAME_SEND_MESSAGE4(this, random);
		if (arma == null) {
			if (lanzador.esInvisible()) {
				lanzador.unHide(-1);
			}
			intentarLanzarHechizo(lanzador, MundoDofus.getHechizo(0).getStatsPorNivel(1), celdaID);
		} else {
			if (arma.getModelo().getTipo() == 83) {
				GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 305, perso.getID() + "", "");
				GestorSalida.ENVIAR_GAF_FINALIZAR_ACCION(this, 7, 0, perso.getID());
				try {
					Thread.sleep(500);
				} catch (Exception e) {
				}
				finTurno("arma1");
				return;
			}
			if (_tipo == 0 || _tipo == 1 || _tipo == 4 || _tipo == 3) {
				if (getMapaCopia() != null) {
					/*
					 * if (getMapaCopia().getID() == 952) {
					 * GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 303, perso.getID() + "", celdaID
					 * + ""); GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso,
					 * "Las peleas llevadas a cabo en el mapa de PvP se convierten en Hardcore y tienen l�mite de Vida (1000), Resistencias (25%), Stats (200) y sin armas."
					 * , Colores.ROJO); return; }
					 */
				}
				if (arma.getModelo().getTipo() == 5 || arma.getModelo().getTipo() == 4) {
					if (lanzador.ataquedYb >= 2) {
						GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 301, perso.getID() + "", "0");
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso,
								"Las Dagas y los Bastones sÓlo pueden atacar 2 veces por turno", Colores.ROJO);
						return;
					}
					lanzador.ataquedYb += 1;
				} else {
					if (lanzador.ataqueotros >= 2) {
						GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 301, perso.getID() + "", "0");
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso,
								"Las Espadas, Hachas, Arcos y Varitas sÓlo pueden atacar 2 veces por turno",
								Colores.ROJO);
						return;
					}
					lanzador.ataqueotros += 1;
				}
			}
			int costePA = arma.getModelo().getCostePA();
			if (_tempLuchadorPA < costePA) {
				GestorSalida.ENVIAR_Im_INFORMACION(perso, "1170;" + _tempLuchadorPA + "~" + costePA);
				return;
			}
			int dist = Camino.distanciaEntreDosCeldas(getMapaCopia(), lanzador.getCeldaPelea().getID(),
					(short) celdaID);
			int MaxPO = arma.getModelo().getAlcanceMax();
			int MinPO = arma.getModelo().getAlcMinimo();
			if (dist < MinPO || dist > MaxPO) {
				GestorSalida.ENVIAR_Im_INFORMACION(perso, "1171;" + MinPO + "~" + MaxPO + "~" + dist);
				return;
			}
			GestorSalida.ENVIAR_GAS_INICIO_DE_ACCION(this, 7, perso.getID());
			boolean esFC = arma.getModelo().getPorcFC() != 0
					&& Formulas.getRandomValor(1, arma.getModelo().getPorcFC()) == arma.getModelo().getPorcFC();
			if (esFC) {
				int rannd = Formulas.getRandomValor(1, 2);
				if (rannd != 1) {
					esFC = false;
				}
			}
			if (esFC) {
				GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 305, perso.getID() + "", "");
				GestorSalida.ENVIAR_GAF_FINALIZAR_ACCION(this, 7, 0, perso.getID());
				finTurno("arma2");
			} else {
				lanzador.isCac = true;
				GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 303, perso.getID() + "", celdaID + "");
				boolean esGC = lanzador.calculaSiGC(arma.getModelo().getPorcGC());
				if (esGC) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 301, perso.getID() + "", "0");
				}
				if (lanzador.esInvisible()) {
					lanzador.unHide(-1);
				}
				ArrayList<EfectoHechizo> efectos = arma.getEfectosNormales();
				if (esGC) {
					efectos = arma.getEfectosCriticos();
				}
				ArrayList<Luchador> objetivos = null;
				for (EfectoHechizo EH : efectos) {
					if (_estadoPelea != 3) {
						break;
					}
					objetivos = Camino.getObjetivosZonaArma(this, arma.getModelo().getTipo(),
							_mapaCopia.getCelda(celdaID), lanzador.getCeldaPelea().getID());
					EH.setTurnos(0);
					EH.aplicarAPelea(this, lanzador, objetivos, true, false);
				}
				if (objetivos != null) {
					if (objetivos.size() == 1) {
						String dañf = "";
						int finaldaÑo = 0;
						Luchador dañado = null;
						for (EfectoHechizo EH : efectos) {
							if (_estadoPelea != 3) {
								break;
							}
							if (EH.finaldaÑo != 0) {
								dañf = dañf + "(" + EH.finaldaÑo + ") ";
							}
							finaldaÑo += EH.finaldaÑo;
							EH.finaldaÑo = 0;
							if (EH.targetpeles != null) {
								dañado = EH.targetpeles;
							}
						}
						// if (lanzador.reenviohec != 0 && lanzador.objetivoreen != null)
						// GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 107, "-1",
						// lanzador.objetivoreen.getID()+","+lanzador.reenviohec);
						_tempLuchadorPA -= costePA;
						if (dañado != null && !dañf.equals("")) {
							GestorSalida.ENVIAR_cs_CHAT_MENSAJE_A_PELEA(this, 7, "<b>" + lanzador.getNombreLuchador()
									+ "</b> ha hecho un daÑo total de <b>" + dañf + "</b>", Colores.VERDE);
							GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 100, lanzador.getID() + "",
									dañado.getID() + "," + (-(finaldaÑo)));
						}
						// Reenvios
						// Reenvios fin
					}
				}
				if (lanzador.getPDVConBuff() <= 0) {
					agregarAMuertos(lanzador, lanzador, "");
				}
				char dir = Camino.getDirEntreDosCeldas(_mapaCopia, lanzador.getCeldaPelea().getID(), celdaID);
				if (dir != 0) {
					lanzador.setDireccion(dir);
					GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 11, lanzador.getID() + "",
							lanzador.getID() + "," + Camino.getIndexDireccion(dir));
				}
				GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 102, perso.getID() + "", perso.getID() + ",-" + costePA);
				GestorSalida.ENVIAR_GAF_FINALIZAR_ACCION(this, 7, 0, perso.getID());
				lanzador.isCac = false;
				acaboPelea("intentacac");
			}
		}
	}

	public boolean puedeLanzarHechizo(Luchador lanzador, StatsHechizos sHechizo, Celda celda, int celdaObjetivo,
			boolean esia, boolean prueba) {
		if (_estadoPelea >= 4) {
			return false;
		}
		lanzador.isCac = false;
		int celdaDondeLanza;
		if (celdaObjetivo <= -1) {
			celdaDondeLanza = lanzador.getCeldaPelea().getID();
		} else {
			celdaDondeLanza = celdaObjetivo;
		}
		Luchador tempLuchador = LucActual;
		Personaje perso = lanzador.getPersonaje();
		if (sHechizo == null) {
			if (perso != null && !esia) {
				GestorSalida.ENVIAR_Im_INFORMACION(perso, "1169");
			}
			return false;
		}
		boolean esinvoc = false;
		if (sHechizo.efectosID.contains(181)) {
			esinvoc = true;
		}
		if (esinvoc) {
			int invocaciones = 0;
			Luchador invocador = lanzador.getInvocador();
			Luchador real = null;
			if (invocador == null) {
				real = lanzador;
			}
			while (invocador != null) {
				if (invocador.getInvocador() != null && invocador.getInvocador().getMob() != null) {
					real = invocador;
				}
				invocador = invocador.getInvocador();
				invocaciones += 1;
			}
			if (real != null) {
				if (invocaciones >= 5) {
					return false;
				}
			}
		}
		Map<Integer, Integer> glifos = new HashMap<>();
		for (Glifo glifo : _glifos) {
			boolean esglifo = false;
			if (sHechizo.efectosID.contains(401)) {
				esglifo = true;
			}
			if (esglifo) {
				int hechiid = sHechizo.getHechizoID();
				if (glifo._hechizos == hechiid && glifo.getLanzador() == lanzador && glifo.getDuracion() > 0) {
					if (!glifos.containsKey(hechiid)) {
						glifos.put(hechiid, 1);
					} else {
						int actual = glifos.get(hechiid);
						if (actual >= 2) {
							if (perso != null) {
								GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso,
										"SÓlo puedes tener 2 glifos de este tipo activo", Colores.ROJO);
							}
							return false;
						} else {
							glifos.remove(hechiid);
							glifos.put(hechiid, actual + 1);
						}
					}
				}
			}
		}
		if (tempLuchador == null || tempLuchador.getID() != lanzador.getID()) {
			if (perso != null && !esia) {
				GestorSalida.ENVIAR_Im_INFORMACION(perso, "1175");
			}
			return false;
		}
		int gastarPA = 0;
		if (lanzador.getTipo() == 1 && perso.getHechizosSetClase().containsKey(sHechizo.getHechizoID())) {
			int modi = perso.getModifSetClase(sHechizo.getHechizoID(), 285);
			gastarPA = sHechizo.getCostePA() - modi;
		} else {
			gastarPA = sHechizo.getCostePA();
		}
		if (_tempLuchadorPA < gastarPA || _tempLuchadorPA <= 0) {
			if (perso != null && !esia) {
				GestorSalida.ENVIAR_Im_INFORMACION(perso, "1170;" + _tempLuchadorPA + "~" + sHechizo.getCostePA());
			}
			return false;
		}
		if (celda == null) {
			if (perso != null && !esia) {
				GestorSalida.ENVIAR_Im_INFORMACION(perso, "1172");
			}
			return false;
		}
		// lanzar en linea
		if (lanzador.getTipo() == 1 && perso.getHechizosSetClase().containsKey(sHechizo.getHechizoID())) {
			int modi = perso.getModifSetClase(sHechizo.getHechizoID(), 288);
			boolean modif = modi == 1;
			if (sHechizo.esLanzLinea() && !modif
					&& !Camino.siCeldasEstanEnMismaLinea(_mapaCopia, celdaDondeLanza, celda.getID(), 'z')) {
				if (perso != null && !esia) {
					GestorSalida.ENVIAR_Im_INFORMACION(perso, "1173");
				}
				return false;
			}
		} else if (sHechizo.esLanzLinea()
				&& !Camino.siCeldasEstanEnMismaLinea(_mapaCopia, celdaDondeLanza, celda.getID(), 'z')) {
			if (perso != null && !esia) {
				GestorSalida.ENVIAR_Im_INFORMACION(perso, "1173");
			}
			return false;
		}
		// linea de vista
		if (lanzador.getTipo() == 1 && perso.getHechizosSetClase().containsKey(sHechizo.getHechizoID())) {
			int modi = perso.getModifSetClase(sHechizo.getHechizoID(), 289);
			boolean modif = modi == 1;
			if (sHechizo.tieneLineaVuelo()
					&& !Camino.checkearLineaDeVista(_mapaCopia, celdaDondeLanza, celda.getID(), lanzador) && !modif) {
				if (perso != null && !esia) {
					GestorSalida.ENVIAR_Im_INFORMACION(perso, "1174");
				}
				return false;
			}
		} else if (sHechizo.tieneLineaVuelo()
				&& !Camino.checkearLineaDeVista(_mapaCopia, celdaDondeLanza, celda.getID(), lanzador)) {
			if (perso != null && !esia) {
				GestorSalida.ENVIAR_Im_INFORMACION(perso, "1174");
			}
			return false;
		}
		int dist = Camino.distanciaEntreDosCeldas(_mapaCopia, celdaDondeLanza, celda.getID());
		int maxAlc = sHechizo.getMaxAlc();
		int minAlc = sHechizo.getMinAlc();
		// + alcance
		if (lanzador.getTipo() == 1 && perso.getHechizosSetClase().containsKey(sHechizo.getHechizoID())) {
			int modi = perso.getModifSetClase(sHechizo.getHechizoID(), 281);
			maxAlc = maxAlc + modi;
		}
		// alcance modificable
		Stats statsConBuff = lanzador.getTotalStatsConBuff();
		if (lanzador.getTipo() == 1 && perso.getHechizosSetClase().containsKey(sHechizo.getHechizoID())) {
			int modi = perso.getModifSetClase(sHechizo.getHechizoID(), 282);
			boolean modif = modi == 1;
			if (sHechizo.esModifAlc() || modif) {
				maxAlc += statsConBuff.getEfecto(117);
				if (maxAlc < minAlc) {
					maxAlc = minAlc;
				}
			}
		} else if (sHechizo.esModifAlc()) {
			maxAlc += statsConBuff.getEfecto(117);
			if (maxAlc < minAlc) {
				maxAlc = minAlc;
			}
		}
		if (dist < minAlc || dist > maxAlc) {
			if (perso != null && !esia) {
				GestorSalida.ENVIAR_Im_INFORMACION(perso, "1171;" + minAlc + "~" + maxAlc + "~" + dist);
			}
			return false;
		}
		if (lanzador.getMob() != null && !prueba) {
			int maxveces = sHechizo.getMaxLanzPorJugador();
			if (maxveces != 0) {
				if (lanzador.hechizosUsados.containsKey(sHechizo.getHechizoID())) {
					if (lanzador.hechizosUsados.get(sHechizo.getHechizoID()) >= maxveces) {
						return false;
					} else {
						int elit = lanzador.hechizosUsados.get(sHechizo.getHechizoID()) + 1;
						lanzador.hechizosUsados.remove(sHechizo.getHechizoID());
						lanzador.hechizosUsados.put(sHechizo.getHechizoID(), elit);
					}
				} else {
					lanzador.hechizosUsados.put(sHechizo.getHechizoID(), 1);
				}
			}
		}
		if (!HechizoLanzado.poderSigLanzamiento(lanzador, sHechizo.getHechizoID())) {
			return false;
		}
		int nroLanzTurno = sHechizo.getMaxLanzPorTurno();
		if (lanzador.getTipo() == 1 && perso.getHechizosSetClase().containsKey(sHechizo.getHechizoID())) {
			int modi = perso.getModifSetClase(sHechizo.getHechizoID(), 290);
			nroLanzTurno = sHechizo.getMaxLanzPorTurno() + modi;
		}
		if (nroLanzTurno - HechizoLanzado.getNroLanzamientos(lanzador, sHechizo.getHechizoID()) <= 0
				&& nroLanzTurno > 0) {
			return false;
		}
		Luchador objetivo = _mapaCopia.getPrimerLuchador(celda.getID());
		int nroLanzObjetivo = sHechizo.getMaxLanzPorJugador();
		if (lanzador.getTipo() == 1 && perso.getHechizosSetClase().containsKey(sHechizo.getHechizoID())) {
			int modi = perso.getModifSetClase(sHechizo.getHechizoID(), 291);
			nroLanzObjetivo = sHechizo.getMaxLanzPorJugador() + modi;
		}
		if (nroLanzObjetivo - HechizoLanzado.getNroLanzPorObjetivo(lanzador, objetivo, sHechizo.getHechizoID()) <= 0
				&& nroLanzObjetivo > 0) {
			return false;
		}
		return true;
	}

	private String getPanelResultados(int equipoGanador) {
		long tiempo = System.currentTimeMillis() - _tiempoInicio;
		int initID = _idLuchInit1;
		int tipoX = 0;
		if (_tipo == 1 || _tipo == 2 || _tipo == 6) {
			tipoX = 1;
		}
		boolean exito = false;
		StringBuilder packet = new StringBuilder("GE");
		try {
			if (_tipo == 4 || _tipo == 3) {
				Map<Integer, Integer> copiaRetos = new TreeMap<>();
				copiaRetos.putAll(_retos);
				if (equipoGanador == 1) {
					for (Entry<Integer, Integer> entry : copiaRetos.entrySet()) {
						int reto = entry.getKey();
						int exitoReto = entry.getValue();
						switch (reto) {
						case 33: // superviviente
							for (Luchador luchador : _inicioLucEquipo1) {
								if (luchador._estaMuerto) {
									exitoReto = 2;
									GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
									break;
								}
							}
							break;
						case 44:// reparto
						case 46: // cada uno con su mousntro
							for (Luchador luchador : _inicioLucEquipo1) {
								if (luchador._mobMatadosReto.size() > 0) {
									exitoReto = 2;
									GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
									break;
								}
							}
							break;
						}
						if (exitoReto == 2) {
							continue;
						}
						if (exitoReto == 0) {
							exitoReto = 1;
							GestorSalida.ENVIAR_GdaK_RETO_REALIZADO(this, reto);
						}
						exito = true;
						_retos.remove(reto);
						_retos.put(reto, exitoReto);
					}
				} else {
					for (Entry<Integer, Integer> entry : copiaRetos.entrySet()) {
						int reto = entry.getKey();
						int exitoReto = entry.getValue();
						if (exitoReto != 0) {
							continue;
						}
						exitoReto = 2;
						GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
						_retos.remove(reto);
						_retos.put(reto, exitoReto);
					}
				}
			}
			if (_tipo == 4) {
				packet.append(tiempo + ";" + _estrellas + "|" + initID + "|" + tipoX + "|");
			} else {
				packet.append(tiempo + "|" + initID + "|" + tipoX + "|");
			}
			ArrayList<Luchador> ganadores = new ArrayList<>();
			ArrayList<Luchador> perdedores = new ArrayList<>();
			if (equipoGanador == 1) {
				ganadores.addAll(_equipo1.values());
				perdedores.addAll(_equipo2.values());
			} else {
				ganadores.addAll(_equipo2.values());
				perdedores.addAll(_equipo1.values());
			}
			Personaje pj1 = null;
			long exp = 0;
			Personaje delachapa = null;
			Map<Integer, Integer> mobs = new TreeMap<>();// Mob matado de misiones
			if (_tipo == 4) {
				for (Luchador perdedorxx : perdedores) {
					if (perdedorxx.esInvocacion() || perdedorxx.getPersonaje() != null || perdedorxx.esDoble()) {
						continue;
					}
					if (perdedorxx.getMob() != null) {
						if (mobs.containsKey(perdedorxx.getMob().getModelo().getID())) {
							int cant = mobs.get(perdedorxx.getMob().getModelo().getID()) + 1;
							mobs.remove(perdedorxx.getMob().getModelo().getID());
							mobs.put(perdedorxx.getMob().getModelo().getID(), cant);
						} else {
							mobs.put(perdedorxx.getMob().getModelo().getID(), 1);
						}
					}
				}
			}
			for (Luchador luchador : ganadores) {
				pj1 = luchador.getPersonaje();
				if (luchador.esInvocacion() || luchador.getMob() != null) {
					continue;
				}
				if (pj1 != null) {
					switch (_tipo) {
					case 1:
						if (!MundoDofus.estaRankingPVP(pj1.getID())) {
							RankingPVP rank = new RankingPVP(pj1.getID(), pj1.getNombre(), 0, 0, pj1.getAlineacion());
							MundoDofus.addRankingPVP(rank);
							GestorSQL.AGREGAR_RANKINGPVP(rank);
							rank.aumentarVictoria();
						} else {
							MundoDofus.getRanking(pj1.getID()).aumentarVictoria();
						}
						break;
					case 4:
						float porc = MundoDofus.getBalanceMundo(pj1.getAlineacion()) / 100;
						float porcN = (float) Math.rint(pj1.getNivelAlineacion() / 2.5);
						luchador._bonusAlineacion = porc * porcN;
						pj1.setMobMatado(mobs);
						for (Integer mobxs : mobs.values()) {
							Misiones.checkMision(pj1, mobxs);
						}
						break;
					}
				}
			}
			for (Luchador perdedorxx : perdedores) {
				if (perdedorxx.esInvocacion() || perdedorxx.esDoble()) {
					continue;
				}
				Personaje pj2 = perdedorxx.getPersonaje();
				if (_tipo == 1) {
					if (pj2 != null) {
						int idmis = pj2.getID();
						if (MundoDofus.misionesPvP.containsKey(idmis)) {
							int ganad = MundoDofus.misionesPvP.get(idmis);
							Personaje delpvc = MundoDofus.getPersonaje(ganad);
							Luchador delpvp = getLuchadorPorPJ(delpvc);
							if (ganadores.contains(delpvp) && delpvp != null) {
								// avisa al perdedor
								GestorSalida.ENVIAR_cs_CHAT_MENSAJE(pj2, "Perdiste la mision.", Colores.ROJO);
								// da premio al que estaba siendo cazado
								GestorSalida.ENVIAR_cs_CHAT_MENSAJE(delpvc,
										"Thomas Sacre : Has recibido la recompensa de tu cazador.", Colores.VERDE);

								Objeto nuevo = MundoDofus.getObjModelo(10275).crearObjDesdeModelo(Emu.CHAPAS_MISION,
										false);
								if (!delpvc.addObjetoSimilar(nuevo, true, -1)) {
									MundoDofus.addObjeto(nuevo, true);
									delpvc.addObjetoPut(nuevo);
									GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(delpvc, nuevo);
								}
								delachapa = delpvc;
								MundoDofus.misionesPvP.remove(idmis);
							}
						} else if (MundoDofus.misionesPvP.containsValue(idmis)) {
							int keydelgan = 0;
							for (Entry<Integer, Integer> entry : MundoDofus.misionesPvP.entrySet()) {
								if (entry.getValue() == idmis) {
									keydelgan = entry.getKey();
									break;
								}
							}
							if (keydelgan != 0) {
								Personaje delpvc = MundoDofus.getPersonaje(keydelgan);
								Luchador delpvp = getLuchadorPorPJ(delpvc);
								if (ganadores.contains(delpvp) && delpvp != null) {
									// has cumplido con tu contrato
									GestorSalida.ENVIAR_cs_CHAT_MENSAJE(delpvc, "Thomas Sacre : Contrato finalizado.",
											Colores.VERDE);
									Objeto nuevo = MundoDofus.getObjModelo(10275).crearObjDesdeModelo(Emu.CHAPAS_MISION,
											false);
									if (!delpvc.addObjetoSimilar(nuevo, true, -1)) {
										MundoDofus.addObjeto(nuevo, true);
										delpvc.addObjetoPut(nuevo);
										GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(delpvc, nuevo);
									}
									delachapa = delpvc;
									// avisa al que estaba siendo cazado que perdi�
									GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perdedorxx.getPersonaje(),
											"<b>" + delpvc.getNombre() + "</b> ha cumplido con su misi�n al matarte.",
											Colores.ROJO);
									MundoDofus.misionesPvP.remove(keydelgan);
								}
							}
						}
					}
				}
				if (_tipo == 1 && pj2 != null) {
					if (!MundoDofus.estaRankingPVP(pj2.getID())) {
						RankingPVP rank = new RankingPVP(pj2.getID(), pj2.getNombre(), 0, 0, pj2.getAlineacion());
						MundoDofus.addRankingPVP(rank);
						GestorSQL.AGREGAR_RANKINGPVP(rank);
						rank.aumentarDerrota();
					} else {
						MundoDofus.getRanking(pj2.getID()).aumentarDerrota();
					}
				}
			}
			int grupoPP = 0;
			int minkamas = 0;
			int maxkamas = 0;
			int grupoPPIntacta = 0;
			int PPreto = 0;
			int XPreto = 0;
			float coefEstrella = 0.0F;
			if (_estrellas != 0) {
				coefEstrella = _estrellas / 100.0F;
			}
			int estrellaPP = 0;
			int estrellaXP = 0;
			long totalXP = 0L;
			ArrayList<Luchador> ordenLuchMasAMenosPP = new ArrayList<>();
			Luchador lucConMaxPP = null;
			ArrayList<Drop> posibleDrops = new ArrayList<>();
			// boolean mobCapturable;
			if (((_tipo == 4) || (_tipo == 5)) && (equipoGanador == 1)) {
				for (Luchador ganador : ganadores) {
					if ((!ganador.esInvocacion())
							|| ((ganador.getMob() != null) && (ganador.getMob().getModelo().getID() == 285))) {
						int prosp = ganador.getTotalStatsConBuff().getEfecto(176);
						ganador._prospeccionTemporal = prosp;
						Personaje pjGanador = ganador.getPersonaje();
						if (pjGanador != null) {
							if ((pjGanador.getAlineacion() == 1) || (pjGanador.getAlineacion() == 2)) {
								if (_mapaReal.getSubArea().getAlineacion() == pjGanador.getAlineacion()) {
									ganador._prospeccionTemporal = (int) (prosp + prosp * ganador._bonusAlineacion);
								} else {
									ganador._prospeccionTemporal = (int) (prosp - prosp * ganador._bonusAlineacion);
								}
							}

						}
						grupoPP += ganador._prospeccionTemporal;
					}
				}
				grupoPPIntacta = grupoPP;
				if (grupoPP < 0) {
					grupoPP = 0;
				}
				if (exito) {
					for (Entry<Integer, Integer> entry : _retos.entrySet()) {
						int reto = (entry.getKey());
						int exitoReto = (entry.getValue());
						if (exitoReto == 2) {
							continue;
						}
						Reto retox = MundoDofus.getReto(reto);
						PPreto += retox.bonusPP();
						XPreto += retox.bonusXP();
					}
				}
				if (coefEstrella > 0.0F) {
					estrellaPP = (int) (grupoPP * coefEstrella);
				}
				grupoPP += grupoPP * PPreto / 100 + estrellaPP;
				MobGrado mob;
				int prospReq;
				for (Luchador perdedor : perdedores) {
					mob = perdedor.getMob();
					if ((perdedor.esInvocacion()) || (mob == null)) {
						continue;
					}
					MobModelo mobModelo = mob.getModelo();
					minkamas += mobModelo.getMinKamas();
					maxkamas += mobModelo.getMaxKamas();
					int veces = 0;
					for (Drop drop : mobModelo.getDrops()) {
						if (veces >= 10) {
							break;
						}
						prospReq = (int) (drop.getProspReq() * Formulas.PROSP_REQ);
						if (prospReq <= grupoPP) {
							int eldrop = Emu.RATE_DROP;
							if (Emu.Maldicion) {
								eldrop = Math.round(Emu.RATE_DROP / 2);
							}
							if (eldrop <= 0) {
								eldrop = 1;
							}
							int nuevaProbabilidad = drop.getProbabilidad() + eldrop;
							int cant = Formulas.getRandomValor(1, drop.getDropMax()); // TODO: drop a 1 poner formula
																						// random
							posibleDrops.add(new Drop(drop.getObjetoID(), prospReq, nuevaProbabilidad, cant));
							veces += 1;
						}
					}
				}
				Map<Integer, Luchador> todosConPP = new TreeMap<>();
				for (Luchador ganador : ganadores) {
					int prosp = ganador.getTotalStatsConBuff().getEfecto(176);
					while (todosConPP.containsKey(prosp)) {
						prosp += 1;
					}
					todosConPP.put(prosp, ganador);
				}
				while (ordenLuchMasAMenosPP.size() < ganadores.size()) {
					int tempPP = -1;
					for (Entry<Integer, Luchador> entry : todosConPP.entrySet()) {
						if (entry.getKey() > tempPP && !ordenLuchMasAMenosPP.contains(entry.getValue())) {
							lucConMaxPP = entry.getValue();
							tempPP = entry.getKey();
						}
					}
					ordenLuchMasAMenosPP.add(lucConMaxPP);
				}
				ganadores.clear();
				ganadores.addAll(ordenLuchMasAMenosPP);
				for (Luchador perdedor : perdedores) {
					if ((perdedor.esInvocacion()) || (perdedor.getMob() == null)) {
						continue;
					}
					totalXP += perdedor.getMob().getBaseXp();
				}
				if (coefEstrella > 0.0F) {
					estrellaXP = (int) (totalXP * coefEstrella);
				}
				totalXP = totalXP + totalXP * XPreto / 100L + estrellaXP;
				/*
				 * mobCapturable = true; for (Luchador perdedor : perdedores) { try {
				 * mobCapturable &= perdedor.getMob().getModelo().esCapturable(); } catch
				 * (Exception e) { mobCapturable = false; break; } } _esCapturable |=
				 * mobCapturable; if (_esCapturable) { boolean primero = false; int maxNivel =
				 * 0; StringBuilder piedraStats = new StringBuilder(""); for (Luchador perdedor
				 * : perdedores) { if (primero) piedraStats.append(",");
				 * piedraStats.append("26f#" + Integer.toHexString(perdedor.getNivel()) + "#0#"
				 * + Integer.toHexString(perdedor.getMob().getModelo().getID())); primero =
				 * true; if (perdedor.getNivel() > maxNivel) maxNivel = perdedor.getNivel(); }
				 * _piedraAlma = new PiedraDeAlma(MundoDofus.getSigIDObjeto(), 1, 7010, -1,
				 * piedraStats.toString()); for (Luchador ganador : ganadores) { if
				 * ((!ganador.esInvocacion()) && (ganador.tieneEstado(2))) {
				 * _capturadores.add(ganador); } } if ((_capturadores.size() > 0) &&
				 * (!_mapaReal.esArena())) for (int i = 0; i < _capturadores.size();) try {
				 * Luchador capt = _capturadores.get(Formulas.getRandomValor(0,
				 * _capturadores.size() - 1)); Personaje capturador = capt.getPersonaje();
				 * Objeto objPos1 = capturador.getObjPosicion(1); if
				 * (objPos1.getModelo().getTipo() != 83) { _capturadores.remove(capt); } else {
				 * Duo<Integer, Integer> piedraJug = Formulas.decompilarPiedraAlma(objPos1); if
				 * ((piedraJug._segundo) < maxNivel) { _capturadores.remove(capt); } else { int
				 * suerteCaptura = Formulas.totalPorcCaptura((piedraJug._primero), capturador);
				 * if (Formulas.getRandomValor(1, 100) <= suerteCaptura) { int piedra =
				 * objPos1.getID(); capturador.borrarObjetoRemove(piedra);
				 * GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(capturador, piedra);
				 * _capturadorGanador = capt._id; } } } } catch (NullPointerException
				 * localNullPointerException) { i++; } }
				 */
			}
			for (Luchador ganador : ganadores) {
				if (ganador._estaRetirado || ganador._doble != null || ganador.esInvocacion()) {
					continue;
				}
				Personaje pjGanador = ganador.getPersonaje();
				StringBuilder drops = new StringBuilder("");
				long kamasGanadas = 0;
				if (tipoX == 0 && _tipo != 6) { // PVM, DESAFIO
					if (ganador.esInvocacion() && pjGanador == null && ganador.getMob().getModelo().getID() != 285) {
						continue;
					}
					long xpGanada = Formulas.getXpGanadaPVM(ganador, ganadores, perdedores, totalXP);
					long xpParaGremio = Formulas.getXPDonadaGremio(ganador, xpGanada);
					xpGanada -= (xpParaGremio * 10);
					long xpParaDragopavo = 0;
					if (pjGanador != null && pjGanador.estaMontando()) {
						xpParaDragopavo = Formulas.getXPDonadaDragopavo(ganador, xpGanada);
						pjGanador.getMontura().addXp(xpParaDragopavo);
						GestorSalida.ENVIAR_Re_DETALLES_MONTURA(pjGanador, "+", pjGanador.getMontura());
					}
					xpGanada -= (xpParaDragopavo * 100);
					kamasGanadas += Formulas.getKamasGanadas(ganador, minkamas, maxkamas);
					if ((pjGanador != null) && ((pjGanador.getAlineacion() == 1) || (pjGanador.getAlineacion() == 2))) {
						if (_mapaReal.getSubArea().getAlineacion() == pjGanador.getAlineacion()) {
							xpGanada += (long) (xpGanada * ganador._bonusAlineacion);
							kamasGanadas += (long) (kamasGanadas * ganador._bonusAlineacion);
						} else {
							xpGanada -= (long) (xpGanada * ganador._bonusAlineacion);
							kamasGanadas -= (long) (kamasGanadas * ganador._bonusAlineacion);
						}
						if (xpGanada < 0L) {
							xpGanada = 0L;
						}
						if (kamasGanadas < 0L) {
							kamasGanadas = 0L;
						}
					}
					ArrayList<Drop> tempDrops = new ArrayList<>();
					tempDrops.addAll(posibleDrops);
					Map<Integer, Integer> objetosGanados = new TreeMap<>();
					int veces = 0;
					int parteCorresponde = 100;
					int maximo = 0;
					int canDrops = 0;
					if (_tipo == 4 || _tipo == 5) {
						if (pjGanador != null) {
							parteCorresponde = grupoPPIntacta
									/ (ganador._prospeccionTemporal <= 0 ? 1 : ganador._prospeccionTemporal);
						}
						if (_tipo == 4) {
							for (Drop dro : tempDrops) {
								canDrops += dro.getDropMax();
							}
							maximo = canDrops / (parteCorresponde <= 0 ? 1000 : parteCorresponde);
						}
						if (pjGanador == null) {
							maximo = 3;
						}
					}
					if (_tipo == 5 && pjGanador != null) {
						if (pjGanador.getGremio() == null
								|| (_Recaudador.getGremioID() != pjGanador.getGremio().getID())) {
							boolean primero = false;
							Collection<Objeto> objRecaudador = null;
							ArrayList<Integer> idObjetos = new ArrayList<>();
							objRecaudador = _Recaudador.getObjetos().values();
							if (objRecaudador.size() > 0) {
								maximo = objRecaudador.size() / parteCorresponde;
								for (Objeto objeto : objRecaudador) {
									if (objeto == null) {
										continue;
									}
									if (primero) {
										drops.append(",");
									}
									drops.append(objeto.getModelo().getID() + "~" + objeto.getCantidad());
									if (pjGanador.addObjetoSimilar(objeto, true, -1)) {
										MundoDofus.eliminarObjeto(objeto.getID());
									} else {
										pjGanador.addObjetoPut(objeto);
										GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(pjGanador, objeto);
									}
									idObjetos.add(objeto.getID());
									primero = true;
									veces++;
									if (veces >= maximo) {
										break;
									}
								}
								for (Integer integer : idObjetos) {
									_Recaudador.borrarObjeto(integer);
								}
							}
						}
					} else {
						for (Drop drop : tempDrops) {
							int porc = drop.getProbabilidad();
							if (porc >= 110) {
								ObjetoModelo OM = MundoDofus.getObjModelo(drop.getObjetoID());
								if (OM == null) {
									continue;
								}
								veces++;
								int id = OM.getID();
								Random random = new Random();
								int cant = random.nextInt(drop.getDropMax()); // TODO: drop a 1 poner formula random
								if (cant <= 0) {
									cant = 1;
								}
								objetosGanados.put(id,
										(objetosGanados.get(id) == null ? 0 : objetosGanados.get(id)) + cant);
								drop.setDropMax(drop.getDropMax() - cant);
								if (drop.getDropMax() <= 0) {
									posibleDrops.remove(drop);
								}
								if (veces >= maximo) {
									break;
								}
							} else {
								Random rand = new Random();
								int jet = rand.nextInt(110);
								if (jet < porc) {
									ObjetoModelo OM = MundoDofus.getObjModelo(drop.getObjetoID());
									if (OM == null) {
										continue;
									}
									veces++;
									int id = OM.getID();
									int dopmax = 1;
									if (drop.getDropMax() <= 1) {
										dopmax = 1;
									} else {
										drop.getDropMax();
									}
									Random random = new Random();
									int cant = random.nextInt(dopmax); // TODO: drop a 1 poner formula random
									if (cant <= 0) {
										cant = 1;
									}
									objetosGanados.put(id,
											(objetosGanados.get(id) == null ? 0 : objetosGanados.get(id)) + cant);
									drop.setDropMax(drop.getDropMax() - cant);
									if (drop.getDropMax() <= 0) {
										posibleDrops.remove(drop);
									}
									if (veces >= maximo) {
										break;
									}
								}
							}
						}
						/*
						 * if(_tipo == 3) { for (Luchador perdedor : perdedores) { MobGrado mob =
						 * perdedor.getMob(); if(perdedor.esInvocacion() || mob == null) continue; int
						 * idMob = mob.getModelo().getID(); if(CentroInfo.getDoplonDopeul(idMob) != -1)
						 * { objetosGanados.put(CentroInfo.getDoplonDopeul(idMob), 1);
						 * objetosGanados.put(CentroInfo.getCertificadoDopeul(idMob), 1); } } } else
						 * if(ganador._id == _capturadorGanador && _piedraAlma != null) {
						 * if(drops.length() > 0) drops.append(","); drops.append(7010 + "~" + 1);
						 * if(!pjGanador.addObjetoSimilar(_piedraAlma, false, -1)) {
						 * MundoDofus.addObjeto(_piedraAlma, true); pjGanador.addObjetoPut(_piedraAlma);
						 * GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(pjGanador, _piedraAlma); } }
						 */
						boolean checkeado = false;
						for (Entry<Integer, Integer> entry : objetosGanados.entrySet()) {
							ObjetoModelo OM = MundoDofus.getObjModelo(entry.getKey());
							if (OM == null) {
								continue;
							}
							if (drops.length() > 0) {
								drops.append(",");
							}
							drops.append(entry.getKey() + "~" + entry.getValue());
							if (pjGanador == null) {
								if (ganador.getInvocador() != null) {
									if (ganador.getInvocador().getPersonaje() != null) {
										Personaje pj1i = ganador.getInvocador().getPersonaje();
										pj1i.actualizainv = true;
										if (!checkeado) {
											checkeado = true;
											pj1i.organizaInvent();
										}
										switch (pj1i.combateactual) {
										case 0:
											if (pj1i.itemsFinal.containsKey(entry.getKey())) {
												int valorac = pj1i.itemsFinal.get(entry.getKey()) + entry.getValue();
												pj1i.itemsFinal.remove(entry.getKey());
												pj1i.itemsFinal.put(entry.getKey(), valorac);
											} else {
												pj1i.itemsFinal.put(entry.getKey(), entry.getValue());
											}
											break;
										case 1:
											if (pj1i.itemsFinaldos.containsKey(entry.getKey())) {
												int valorac = pj1i.itemsFinaldos.get(entry.getKey()) + entry.getValue();
												pj1i.itemsFinaldos.remove(entry.getKey());
												pj1i.itemsFinaldos.put(entry.getKey(), valorac);
											} else {
												pj1i.itemsFinaldos.put(entry.getKey(), entry.getValue());
											}
											break;
										case 2:
											if (pj1i.itemsFinaltres.containsKey(entry.getKey())) {
												int valorac = pj1i.itemsFinaltres.get(entry.getKey())
														+ entry.getValue();
												pj1i.itemsFinaltres.remove(entry.getKey());
												pj1i.itemsFinaltres.put(entry.getKey(), valorac);
											} else {
												pj1i.itemsFinaltres.put(entry.getKey(), entry.getValue());
											}
											break;
										case 3:
											if (pj1i.itemsFinalcua.containsKey(entry.getKey())) {
												int valorac = pj1i.itemsFinalcua.get(entry.getKey()) + entry.getValue();
												pj1i.itemsFinalcua.remove(entry.getKey());
												pj1i.itemsFinalcua.put(entry.getKey(), valorac);
											} else {
												pj1i.itemsFinalcua.put(entry.getKey(), entry.getValue());
											}
											break;
										case 4:
											if (pj1i.itemsFinalcinc.containsKey(entry.getKey())) {
												int valorac = pj1i.itemsFinalcinc.get(entry.getKey())
														+ entry.getValue();
												pj1i.itemsFinalcinc.remove(entry.getKey());
												pj1i.itemsFinalcinc.put(entry.getKey(), valorac);
											} else {
												pj1i.itemsFinalcinc.put(entry.getKey(), entry.getValue());
											}
											break;
										default:
											if (pj1i.itemsFinal.containsKey(entry.getKey())) {
												int valorac = pj1i.itemsFinal.get(entry.getKey()) + entry.getValue();
												pj1i.itemsFinal.remove(entry.getKey());
												pj1i.itemsFinal.put(entry.getKey(), valorac);
											} else {
												pj1i.itemsFinal.put(entry.getKey(), entry.getValue());
											}
											break;
										}
									}
								}
							} else {
								pjGanador.actualizainv = true;
								if (!checkeado) {
									checkeado = true;
									pjGanador.organizaInvent();
								}
								switch (pjGanador.combateactual) {
								case 0:
									if (pjGanador.itemsFinal.containsKey(entry.getKey())) {
										int valorac = pjGanador.itemsFinal.get(entry.getKey()) + entry.getValue();
										pjGanador.itemsFinal.remove(entry.getKey());
										pjGanador.itemsFinal.put(entry.getKey(), valorac);
									} else {
										pjGanador.itemsFinal.put(entry.getKey(), entry.getValue());
									}
									break;
								case 1:
									if (pjGanador.itemsFinaldos.containsKey(entry.getKey())) {
										int valorac = pjGanador.itemsFinaldos.get(entry.getKey()) + entry.getValue();
										pjGanador.itemsFinaldos.remove(entry.getKey());
										pjGanador.itemsFinaldos.put(entry.getKey(), valorac);
									} else {
										pjGanador.itemsFinaldos.put(entry.getKey(), entry.getValue());
									}
									break;
								case 2:
									if (pjGanador.itemsFinaltres.containsKey(entry.getKey())) {
										int valorac = pjGanador.itemsFinaltres.get(entry.getKey()) + entry.getValue();
										pjGanador.itemsFinaltres.remove(entry.getKey());
										pjGanador.itemsFinaltres.put(entry.getKey(), valorac);
									} else {
										pjGanador.itemsFinaltres.put(entry.getKey(), entry.getValue());
									}
									break;
								case 3:
									if (pjGanador.itemsFinalcua.containsKey(entry.getKey())) {
										int valorac = pjGanador.itemsFinalcua.get(entry.getKey()) + entry.getValue();
										pjGanador.itemsFinalcua.remove(entry.getKey());
										pjGanador.itemsFinalcua.put(entry.getKey(), valorac);
									} else {
										pjGanador.itemsFinalcua.put(entry.getKey(), entry.getValue());
									}
									break;
								case 4:
									if (pjGanador.itemsFinalcinc.containsKey(entry.getKey())) {
										int valorac = pjGanador.itemsFinalcinc.get(entry.getKey()) + entry.getValue();
										pjGanador.itemsFinalcinc.remove(entry.getKey());
										pjGanador.itemsFinalcinc.put(entry.getKey(), valorac);
									} else {
										pjGanador.itemsFinalcinc.put(entry.getKey(), entry.getValue());
									}
									break;
								default:
									if (pjGanador.itemsFinal.containsKey(entry.getKey())) {
										int valorac = pjGanador.itemsFinal.get(entry.getKey()) + entry.getValue();
										pjGanador.itemsFinal.remove(entry.getKey());
										pjGanador.itemsFinal.put(entry.getKey(), valorac);
									} else {
										pjGanador.itemsFinal.put(entry.getKey(), entry.getValue());
									}
									break;
								}
							}
						}
					}
					if (xpGanada != 0 && pjGanador != null) {
						long expTotal = (xpGanada);
						if (pjGanador._resets == 2) {
							expTotal = Math.round(((75 * xpGanada) / 100));
							if (expTotal < 0) {
								expTotal = 0;
							}
						} else if (pjGanador._resets == 3) { // (50*6000)/100
							expTotal = Math.round(((60 * xpGanada) / 100));
							if (expTotal < 0) {
								expTotal = 0;
							}
						} else if (pjGanador._resets == 4) { // (50*6000)/100
							expTotal = Math.round(((45 * xpGanada) / 100));
							if (expTotal < 0) {
								expTotal = 0;
							}
						}
						pjGanador.addExp(expTotal, false);
					}
					if (kamasGanadas != 0 && pjGanador != null) {
						pjGanador.addKamas(kamasGanadas);
					}
					if (xpParaGremio > 0 && pjGanador.getMiembroGremio() != null) {
						pjGanador.getMiembroGremio().darXpAGremio(xpParaGremio);
					}
					packet.append("2;" + ganador.getID() + ";" + ganador.getNombreLuchador() + ";" + ganador.getNivel()
							+ ";" + (ganador._estaMuerto ? "1" : "0") + ";");
					packet.append(ganador.xpString(";") + ";");
					packet.append((xpGanada == 0 ? "" : xpGanada) + ";");
					packet.append((xpParaGremio == 0 ? "" : xpParaGremio) + ";");
					packet.append((xpParaDragopavo == 0 ? "" : xpParaDragopavo) + ";");
					packet.append(drops + ";");
					packet.append((kamasGanadas == 0 ? "" : kamasGanadas) + "|");
					if (pjGanador != null) {
						GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(pjGanador);
					}


					//ENVIA MENSAJE XP  KAMAS GANADOS
					DecimalFormat formatter = new DecimalFormat("#,###");
					String numeroFormateado = formatter.format(xpGanada);

					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(pjGanador,
							"<b>" + numeroFormateado + "</b> XP Ganada",
							Colores.ROJO);
					String numeroFormateado1 = formatter.format(kamasGanadas);
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(pjGanador,
							"<b>" + numeroFormateado1 + "</b> Kamas Ganadas",
							Colores.ROJO);


				} else {
					if ((ganador.esInvocacion()) && (pjGanador == null)) {
						continue;
					}
					int honor = 0;
					int deshonor = 0;
					if (pjGanador != null) {
						if (_tipo != 6) {
							packet.append("2;" + ganador.getID() + ";" + ganador.getNombreLuchador() + ";"
									+ ganador.getNivel() + ";" + (ganador._estaMuerto ? "1" : "0") + ";"
									+ (pjGanador.getAlineacion() != -1
											? MundoDofus.getExpNivel(pjGanador.getNivelAlineacion())._pvp
											: 0)
									+ ";" + pjGanador.getHonor() + ";");
						} else {
							packet.append("2;" + ganador.getID() + ";" + ganador.getNombreLuchador() + ";"
									+ ganador.getNivel() + ";" + (ganador._estaMuerto ? "1" : "0") + ";" + 0 + ";"
									+ pjGanador.getHonor() + ";");
						}
						if (_tipo == 1) {// pvp
							if ((_luchInit2.getPersonaje().getAlineacion() != 0)
									&& (_luchInit1.getPersonaje().getAlineacion() != 0)) {
								if (_luchInit2.getPersonaje().getCuenta().getActualIP()
										.compareTo(_luchInit1.getPersonaje().getCuenta().getActualIP()) != 0) {
									honor = Formulas.calcularHonorGanado(ganadores, ganador);
								}
								if ((_luchInit2.getPersonaje().getAlineacion() != 0)
										&& (_luchInit1.getPersonaje().getAlineacion() != 0)) {
									if (pjGanador.getDeshonor() > 0) {
										deshonor = pjGanador.getDeshonor() - 1;
									}
								}
							}
						}
						int maxHonor = 0;
						if (_tipo != 6) {
							if (honor < 0) {
								honor = 0;
							}
							if (maxHonor == -1) {
								maxHonor = MundoDofus.getExpNivel(pjGanador.getNivelAlineacion())._pvp;
							} else {
								maxHonor = MundoDofus.getExpNivel(pjGanador.getNivelAlineacion() + 1)._pvp;
							}
						}
						if (_tipo == 2) {
							honor = 100;
							pjGanador.addHonor(honor);
						} else {
							pjGanador.addHonor(honor);
							pjGanador.setDeshonor(pjGanador.getDeshonor() + deshonor);
						}
						packet.append((pjGanador.getAlineacion() != -1 ? maxHonor : 0) + ";");
						packet.append(honor + ";");
						packet.append(pjGanador.getNivelAlineacion() + ";");
						packet.append(pjGanador.getDeshonor() + ";");
						packet.append(deshonor + ";");
						if (_tipo == 6) {// koliseo
							long kamasKoli = 0;
							long expKoli = 0;
							if (drops.length() > 0) {
								packet.append(drops).append(",");
							}
							packet.append("11158~" + Emu.KOLICHAS + ";");
							packet.append(kamasKoli).append(";");
							packet.append(ganador.xpString(";")).append(";");
							packet.append(expKoli).append("|");
						} else if (_tipo == 1) {// pvp
							if (drops.length() > 0) {
								packet.append(drops);
							}
							if (delachapa != null) {
								if (delachapa == pjGanador) {
									if (drops.length() > 0) {
										packet.append(",");
									}
									packet.append("10275~" + Emu.CHAPAS_MISION);
								}
							}
							packet.append(";" + kamasGanadas + ";");
							packet.append(pjGanador.xpString(";") + ";");
							packet.append(exp + "|");
						} else if (_tipo == 2) {// prisma
							packet.append(";");
							if (drops.length() > 0) {
								packet.append(drops);
							}
							packet.append(";" + kamasGanadas + ";");
							packet.append("0;0;0;0|");
						}
					} else if (ganador.getPrisma() != null) {
						Prisma prisma = ganador.getPrisma();
						packet.append("2;" + ganador.getID() + ";" + ganador.getNombreLuchador() + ";"
								+ ganador.getNivel() + ";" + (ganador._estaMuerto ? "1" : "0") + ";"
								+ MundoDofus.getExpNivel(prisma.getNivel())._pvp + ";" + prisma.getHonor() + ";");
						int maxHonor = MundoDofus.getExpNivel(prisma.getNivel() + 1)._pvp;
						if (maxHonor == -1) {
							maxHonor = MundoDofus.getExpNivel(prisma.getNivel())._pvp;
						}
						packet.append(maxHonor + ";");
						packet.append(0 + ";");
						packet.append(prisma.getNivel() + ";");
						packet.append("0;0;;0;0;0;0;0|");
					}
				}
			}
			for (Luchador perdedor : perdedores) {
				if (perdedor._doble != null) {
					continue;
				}
				Personaje pjPerdedor = perdedor.getPersonaje();
				if ((perdedor.esInvocacion()) && (pjPerdedor == null)) {
					continue;
				}
				if (tipoX == 0 && _tipo != 6) {
					packet.append(
							"0;" + perdedor.getID() + ";" + perdedor.getNombreLuchador() + ";" + perdedor.getNivel());
					if ((perdedor.getPDVConBuff() == 0) || (perdedor._estaRetirado)) {
						packet.append(";1");
					} else {
						packet.append(";0");
					}
					packet.append(";" + perdedor.xpString(";") + ";;;;|");
				} else {
					int honor = 0;
					int deshonor = 0;
					if (pjPerdedor != null) {
						if (_tipo != 6) {
							packet.append("0;" + perdedor.getID() + ";" + perdedor.getNombreLuchador() + ";"
									+ perdedor.getNivel() + ";" + (perdedor._estaMuerto ? "1" : "0") + ";"
									+ (pjPerdedor.getAlineacion() != -1
											? MundoDofus.getExpNivel(pjPerdedor.getNivelAlineacion())._pvp
											: 0)
									+ ";" + pjPerdedor.getHonor() + ";");
						} else {
							packet.append("0;" + perdedor.getID() + ";" + perdedor.getNombreLuchador() + ";"
									+ perdedor.getNivel() + ";" + (perdedor._estaMuerto ? "1" : "0") + ";" + 0 + ";"
									+ pjPerdedor.getHonor() + ";");
						}
						if (_tipo == 1) {// pvp
							if ((_luchInit2.getPersonaje().getAlineacion() != 0)
									&& (_luchInit1.getPersonaje().getAlineacion() != 0)) {
								if (_luchInit2.getPersonaje().getCuenta().getActualIP()
										.compareTo(_luchInit1.getPersonaje().getCuenta().getActualIP()) != 0) {
									honor = Formulas.calcularHonorGanado(perdedores, perdedor);
								}
								if ((_luchInit2.getPersonaje().getAlineacion() != 0)
										&& (_luchInit1.getPersonaje().getAlineacion() != 0)) {
									if (pjPerdedor.getDeshonor() > 0) {
										deshonor = pjPerdedor.getDeshonor() - 1;
									}
								}
							}
						}
						int maxHonor = 0;
						if (honor < 0) {
							honor = 0;
						}
						maxHonor = MundoDofus.getExpNivel(pjPerdedor.getNivelAlineacion() + 1)._pvp;
						if (maxHonor == -1) {
							maxHonor = MundoDofus.getExpNivel(pjPerdedor.getNivelAlineacion())._pvp;
						}
						packet.append((pjPerdedor.getAlineacion() != 0 ? maxHonor : 0) + ";");
						packet.append(-honor + ";");
						packet.append(pjPerdedor.getNivelAlineacion() + ";");
						packet.append(pjPerdedor.getDeshonor() + ";");
						packet.append(deshonor + ";");
						pjPerdedor.restarHonor(honor);
						pjPerdedor.setDeshonor(pjPerdedor.getDeshonor() + deshonor);
						if (_tipo == 6) {
							long kamasKoli = 0;
							long expKoli = 0;
							packet.append(/* "11158~1;" */";");
							packet.append(kamasKoli).append(";");
							packet.append(perdedor.xpString(";")).append(";");
							packet.append(expKoli).append("|");
						} else if (_tipo == 1) {
							packet.append(";0;");
							packet.append(pjPerdedor.xpString(";") + ";");
							packet.append("0|");
						} else if (_tipo == 2) {
							packet.append(";;0;0;0;0;0|");
						}
					} else if (perdedor.getPrisma() != null) {
						Prisma prisma = perdedor.getPrisma();
						packet.append("0;" + perdedor.getID() + ";" + perdedor.getNombreLuchador() + ";"
								+ perdedor.getNivel() + ";" + (perdedor._estaMuerto ? "1" : "0") + ";"
								+ MundoDofus.getExpNivel(prisma.getNivel())._pvp + ";" + prisma.getHonor() + ";");
						int maxHonor = MundoDofus.getExpNivel(prisma.getNivel() + 1)._pvp;
						if (maxHonor == -1) {
							maxHonor = MundoDofus.getExpNivel(prisma.getNivel())._pvp;
						}
						packet.append(maxHonor + ";");
						packet.append(0 + ";");
						packet.append(prisma.getNivel() + ";");
						packet.append("0;0;;0;0;0;0;0|");
					}
				}
			}
			if (_mapaCopia == null) {
				return packet.toString();
			}
			if (MundoDofus.getRecauPorMapaID(_mapaCopia.getID()) != null && _tipo == 4) {
				Recaudador recau = MundoDofus.getRecauPorMapaID(_mapaCopia.getID());
				long xpGanada = Formulas.getXpGanadaRecau(recau, totalXP) / 1000L;
				long winkamas = Formulas.getKamasGanadaRecau(minkamas, maxkamas) / 100;
				recau.addXp(xpGanada);
				recau.setKamas(recau.getKamas() + winkamas);
				packet.append("5;" + recau.getID() + ";" + recau.getN1() + "," + recau.getN2() + ";"
						+ MundoDofus.getGremio(recau.getGremioID()).getNivel() + ";0;");
				Gremio gremio = MundoDofus.getGremio(recau.getGremioID());
				packet.append(gremio.getNivel() + ";");
				packet.append(gremio.getXP() + ";");
				packet.append(MundoDofus.getXPMaxGremio(gremio.getNivel()) + ";");
				packet.append(";" + xpGanada + ";;");
				StringBuilder drops = new StringBuilder("");
				if ((gremio.getStats().get(158)) >= recau.getPodsActuales()) {
					NumberFormat formatter = new DecimalFormat("#0.000");
					ArrayList<Drop> tempDrops = new ArrayList<>();
					tempDrops.addAll(posibleDrops);
					Map<Integer, Integer> objGanados = new TreeMap<>();
					int veces = 0;
					int maximo = gremio.getStats(176) / 25;
					for (Drop drop : tempDrops) {
						final double jet = Double.parseDouble(formatter.format(Math.random() * 100).replace(',', '.')),
								chance = (int) (drop.getProbabilidad()
										* (MundoDofus.getGremio(recau.getGremioID()).getStats(176) / 100.0));
						if (jet < chance) {
							ObjetoModelo OM = MundoDofus.getObjModelo(drop.getObjetoID());
							if (OM == null) {
								continue;
							}
							veces++;
							int id = OM.getID();
							objGanados.put(id, (objGanados.get(id)) == null ? 1 : (objGanados.get(id)) + 1);
							drop.setDropMax(drop.getDropMax() - 1);
							if (drop.getDropMax() == 0) {
								posibleDrops.remove(drop);
							}
							if (veces >= maximo) {
								break;
							}
						}
					}
					for (Entry<Integer, Integer> entry : objGanados.entrySet()) {
						ObjetoModelo OM = MundoDofus.getObjModelo((entry.getKey()));
						if (OM == null) {
							continue;
						}
						if (drops.length() > 0) {
							drops.append(",");
						}
						drops.append(entry.getKey() + "~" + entry.getValue());
						Objeto obj = OM.crearObjDesdeModelo((entry.getValue()), false);
						MundoDofus.addObjeto(obj, true);
						recau.addObjeto(obj);
					}
				}
				packet.append(drops + ";");
				packet.append(winkamas + "|");
			}
		} catch (Exception e) {
			Emu.creaLogs(e);
		}
		return packet.toString();
	}

	public void agregarAMuertos(Luchador victima, Luchador atacante, String ubic) {
		try {
			if (victima._estaMuerto || victima == null || _estadoPelea == 4 || _listaMuertos.contains(victima)) {
				return;
			}
			if (!victima.estaRetirado()) {
				_listaMuertos.add(victima);
			}
			victima._estaMuerto = true;
			AgregaMuertos agregaMuertos = new AgregaMuertos(this, victima, atacante);
			agregaMuertos.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	int cantLuchIniMuertos(int equipo) {
		int i = 0;
		List<Luchador> listaMuertos = new ArrayList<>();
		listaMuertos.addAll(_listaMuertos);
		for (Luchador muerto : listaMuertos) {
			if (equipo == 1) {
				if (_muertesEquipo1.contains(muerto.getID())) {
					i++;
				}
			} else if (equipo == 2) {
				if (_muertesEquipo2.contains(muerto.getID())) {
					i++;
				}
			}
		}
		return i;
	}

	boolean acaboPelea(String donde) {
		// System.out.println("ES "+donde);
		if (_estadoPelea == 4 || Emu.Cerrando) {
			return false;
		}
		boolean equipo1Muerto = true;
		boolean equipo2Muerto = true;
		for (Luchador luch : _equipo1.values()) {
			if (!luch.esInvocacion() && !luch._estaMuerto) {
				equipo1Muerto = false;
				break;
			}
		}
		for (Luchador luch : _equipo2.values()) {
			if (!luch.esInvocacion() && !luch._estaMuerto) {
				equipo2Muerto = false;
				break;
			}
		}
		if ((equipo1Muerto || equipo2Muerto || !verificaSiQuedaUno())) {
			int antesestado = _estadoPelea;
			_estadoPelea = 4;
			if (_IAThreads != null) {
				try {
					if (_IAThreads.getThread() != null) {
						if (!_IAThreads.getThread().isInterrupted()) {
							_IAThreads.getThread().interrupt();
						}
						_IAThreads = null;
					}
				} catch (Exception e) {
				}
			}
			if (antesestado == 2 && donde != "retirarsepelea1c") {// si es pelea pvm empezada y se sale
				if (_tipo == 4) {
					if (!_mobGrupo.esFixeado()) {
						_mapaReal.spawnGrupo(-1, 1, true, _mobGrupo.getCeldaID());
					} else {
						_mapaReal.refrescarFix(_mobGrupo.getCeldaID());
					}
				}
			}
			ArrayList<Luchador> ganadores = new ArrayList<>();
			ArrayList<Luchador> perdedores = new ArrayList<>();
			if (equipo1Muerto) {
				perdedores.addAll(_equipo1.values());
				ganadores.addAll(_equipo2.values());
			} else {
				ganadores.addAll(_equipo1.values());
				perdedores.addAll(_equipo2.values());
			}
			int equipoGanador = equipo1Muerto ? 2 : 1;
			String packet = "";
			if (getMapaCopia().getID() != 7796) {
				try {
					if (donde != "retirarsepelea1c") {
						packet = getPanelResultados(equipoGanador);
					}
				} catch (Exception e) {
					packet = "";
					Emu.creaLogs(e);
				}
			}
			try {
				for (Personaje perso : _espectadores.values()) {
					if (perso != null) {
						perso.salirEspectador();
					}
				}
			} catch (Exception e) {
			}
			ArrayList<Luchador> newperdedores = new ArrayList<>();
			int perdedoresx = 0;
			for (Luchador perdedor : perdedores) {
				if (perdedor._recaudador != null) {
					newperdedores.add(perdedor);
					continue;
				} else if (perdedor._prisma != null) {
					newperdedores.add(perdedor);
					continue;
				}
				if (perdedor.getMob() != null && perdedor._recaudador == null && perdedor._prisma == null) {
					perdedoresx += 1;
				}
				if (perdedor._estaRetirado || perdedor == null || perdedor.esDoble() || perdedor.getMob() != null) {
					continue;
				}
				if (perdedor.getPersonaje() != null) {
					perdedor.getPersonaje().salirDePelea(packet, _tipo, enKoliseo, this, false, false, null, enTorneo,
							esAngel, 0);
				}
			}
			perdedores.clear();
			perdedores.addAll(newperdedores);
			// SACA LUCHADORES GANADORES
			ArrayList<Luchador> newganadores = new ArrayList<>();
			boolean mobganador = false;
			int cantidadpjs = 0;
			for (Luchador ganador : ganadores) {
				if (ganador == null) {
					continue;
				}
				if (ganador.getPersonaje() != null) {
					cantidadpjs += 1;
				}
			}
			for (Luchador ganador : ganadores) {
				if (ganador._recaudador != null) {
					newganadores.add(ganador);
					continue;
				} else if (ganador._prisma != null) {
					newganadores.add(ganador);
					continue;
				} else {
					if (peleainvasion && !mobganador) {
						if (ganador.getMob() != null) {
							if (!ganador.esInvocacion()) {
								mobganador = true;
							}
						}
					}
				}
				if (ganador._estaRetirado || ganador == null || ganador.esDoble() || ganador.getMob() != null) {
					continue;
				}
				if (ganador.getPersonaje() != null) {
					boolean premio = false;
					if (peleainvasion) {
						premio = true;
					}
					ganador.getPersonaje().salirDePelea(packet, _tipo, enKoliseo, this, true, premio, acabaMaitre,
							enTorneo, esAngel, cantidadpjs);
					if (getMapaCopia().getID() == 7796 && MundoDofus.eventoJalato) {
						String strext = "!!!";
						int totalpts = 0;
						if (MundoDofus.personajePuntos.containsKey(ganador.getPersonaje())) {
							int cuantotiene = MundoDofus.personajePuntos.get(ganador.getPersonaje());
							MundoDofus.personajePuntos.remove(ganador.getPersonaje());
							totalpts = cuantotiene + perdedoresx;
							MundoDofus.personajePuntos.put(ganador.getPersonaje(), totalpts);
						} else {
							totalpts = perdedoresx;
							MundoDofus.personajePuntos.put(ganador.getPersonaje(), totalpts);
						}
						for (Personaje _perso : MundoDofus.getPJsEnLinea()) {
							if (_perso.getMapa().getID() == getMapaCopia().getID()) {
								if (_perso == ganador.getPersonaje()) {
									strext = ". Actualmente tienes <b>" + totalpts + "</b> Puntos!!!";
								}
								GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, "<b>" + ganador.getPersonaje().getNombre()
										+ "</b> ha ganado <b>+" + perdedoresx + "</b> Puntos" + strext, Colores.ROSA);
							}
						}
					}
				}
			}
			if (peleainvasion) {
				if (mobganador) {
					Invasion.aplicaMaldicion();
					peleainvasion = false;
				} else {
					Invasion.MurioMega();
					peleainvasion = false;
				}
			}
			ganadores.clear();
			ganadores.addAll(newganadores);
			_mapaReal.quitarPelea(_id);
			GestorSalida.ENVIAR_fC_CANTIDAD_DE_PELEAS(_mapaReal);
			GestorSalida.ENVIAR_Gc_BORRAR_BANDERA_EN_MAPA(_mapaReal, _idLuchInit1);
			if (getMapaCopia() != null) {
				if (getMapaCopia().actualizaactual) {
					getMapaCopia().actualizaactual = false;
					GestorSQL.ACTUALIZAR_MAPA_POSPELEA_NROGRUPO(getMapaCopia());
				}
			}
			String str = "";
			if (_Recaudador != null) {
				str = "D" + _Recaudador.getN1() + "," + _Recaudador.getN2() + "|.|" + _Recaudador.getMapaID() + "|"
						+ _Recaudador.getCeldalID();
			}
			if (_Prisma != null) {
				str = _Prisma.getMapa() + "|" + _Prisma.getX() + "|" + _Prisma.getY();
			}
			for (Luchador perdedor : perdedores) {
				if (perdedor._recaudador != null) {
					Gremio gr = MundoDofus.getGremio(_gremioID);
					if (gr != null) {
						if (gr.lastidpelea != getID()) {
							gr.puntosextra -= 4;
							gr.lastidpelea = getID();
						}
						for (Personaje z : gr.getPjMiembros()) {
							if (z == null) {
								continue;
							}
							if (z.enLinea()) {
								GestorSalida.ENVIAR_gITM_INFO_RECAUDADOR(z,
										Recaudador.analizarRecaudadores(z.getGremio().getID()));
								GestorSalida.ENVIAR_gA_MENSAJE_SOBRE_RECAUDADOR(z, str);
								GestorSalida.ENVIAR_cs_CHAT_MENSAJE(z,
										"El gremio ha perdido <b>-4</b> puntos de poder por perder a un recaudador",
										Colores.ROJO);
							}
						}
						Gremio gremio = gr;
						Mapa mapa = getMapaCopia();
						gremio.addUltRecolectaMapa((short)mapa.getID());
					}
					_mapaReal.removeNPC(perdedor._recaudador.getID());
					GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(_mapaReal, perdedor._recaudador.getID());
					_Recaudador.borrarRecaudador(perdedor._recaudador.getID());
				} else if (perdedor._prisma != null) {
					SubArea subarea = _mapaReal.getSubArea();
					for (Personaje z : MundoDofus.getPJsEnLinea()) {
						if (z == null) {
							continue;
						}
						if (z.getAlineacion() == 0) {
							GestorSalida.ENVIAR_am_MENSAJE_ALINEACION_SUBAREA(z, subarea.getID() + "|0|1");
							continue;
						}
						if (z.getAlineacion() == _Prisma.getAlineacion()) {
							GestorSalida.ENVIAR_CD_MENSAJE_MURIO_PRISMA(z, str);
						}
						GestorSalida.ENVIAR_am_MENSAJE_ALINEACION_SUBAREA(z, subarea.getID() + "|-1|0");
						// GestorSalida.ENVIAR_am_MENSAJE_ALINEACION_SUBAREA(z, subarea.getID() +
						// "|0|1");
						if (_Prisma.getAreaConquistada() != -1) {
							GestorSalida.ENVIAR_aM_MENSAJE_ALINEACION_AREA(z, subarea.getArea().getID() + "|-1");
						}
					}
					int prismaID = perdedor._prisma.getID();
					subarea.setPrismaID(0);
					subarea.getArea().setPrismaID(0);
					subarea.getArea().setAlineacion(0);
					subarea.setAlineacion(0);
					_mapaReal.removeNPC(prismaID);
					GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(_mapaReal, prismaID);
					MundoDofus.borrarPrisma(prismaID);
					GestorSQL.BORRAR_PRISMA(prismaID);
				}
			}
			if (_Recaudador != null) {
				str = "S" + _Recaudador.getN1() + "," + _Recaudador.getN2() + "|.|" + _Recaudador.getMapaID() + "|"
						+ _Recaudador.getCeldalID();
			}
			for (Luchador ganador : ganadores) {
				if (ganador._recaudador != null) {
					Gremio gr = MundoDofus.getGremio(_gremioID);
					if (gr != null) {
						for (Personaje pj : gr.getPjMiembros()) {
							if (pj == null) {
								continue;
							}
							if (pj.enLinea()) {
								GestorSalida.ENVIAR_gITM_INFO_RECAUDADOR(pj,
										Recaudador.analizarRecaudadores(pj.getGremio().getID()));
								GestorSalida.ENVIAR_gA_MENSAJE_SOBRE_RECAUDADOR(pj, str);
							}
						}
					}
					ganador._recaudador.setEstadoPelea(0);
					ganador._recaudador.setPeleaID(-1);
					ganador._recaudador.setPelea(null);
					for (Personaje z : MundoDofus.getMapa((short) ganador._recaudador.getMapaID()).getPersos()) {
						if (z == null || z.getCuenta().getEntradaPersonaje() == null) {
							continue;
						}
						PrintWriter out = z.getCuenta().getEntradaPersonaje().getOut();
						if (out == null) {
							continue;
						}
						GestorSalida.ENVIAR_GM_RECAUDADORES_EN_MAPA(out, z.getMapa());
					}
				} else if (ganador._prisma != null) {
					for (Personaje z : MundoDofus.getPJsEnLinea()) {
						if (z == null || z.getAlineacion() != _Prisma.getAlineacion()) {
							continue;
						}
						GestorSalida.ENVIAR_CS_MENSAJE_SOBREVIVIO_PRISMA(z, str);
					}
					ganador._prisma.setEstadoPelea(-1);
					ganador._prisma.setPeleaID(-1);
					ganador._prisma.setPelea(null);
					for (Personaje z : MundoDofus.getMapa((short) ganador._prisma.getMapa()).getPersos()) {
						if (z == null || z.getCuenta().getEntradaPersonaje() == null) {
							continue;
						}
						PrintWriter out = z.getCuenta().getEntradaPersonaje().getOut();
						if (out == null) {
							continue;
						}
						GestorSalida.ENVIAR_GM_PRISMAS_EN_MAPA(out, z.getMapa());
					}
				}
			}
			if (TimersPelea != null) {
				TimersPelea.cancel();
				TimersPelea.purge();
			}
			if (getActTimerTask() != null) {
				getActTimerTask().cancel();
			}
			LucActual = null;
			_IAThreads = null;
			_ordenNivelMobs.clear();
			_ordenLuchMobs.clear();
			_retos.clear();
			_stringReto.clear();
			_posinicial.clear();
			_inicioLucEquipo2.clear();
			_inicioLucEquipo1.clear();
			_muertesEquipo2.clear();
			_muertesEquipo1.clear();
			_mobsMuertosReto.clear();
			_trampas.clear();
			_glifos.clear();
			_celdasPos2.clear();
			_celdasPos1.clear();
			_espectadores.clear();
			_listaMuertos.clear();
			_equipo2.clear();
			_equipo1.clear();
			_tiempoInicioTurno = 0;
			setEmpezoPelea(false);
			actTimerTask = null;
			TimersPelea = null;
			_luchInit1 = null;
			_luchInit2 = null;
			turnosPelea.clear();
			_mobGrupo = null;
			acabaMaitre = null;
			Runtime.getRuntime().gc();
			return false;
		}
		return true;
	}

	public int getParamEquipo(int id) {
		if (_equipo1.containsKey(id)) {
			return 1;
		}
		if (_equipo2.containsKey(id)) {
			return 2;
		}
		if (_espectadores.containsKey(id)) {
			return 4;
		}
		return -1;
	}

	private int getIDEquipoEnemigo(int id) {
		if (_equipo1.containsKey(id)) {
			return 2;
		}
		if (_equipo2.containsKey(id)) {
			return 1;
		}
		return -1;
	}

	public Luchador getLuchadorPorPJ(Personaje perso) {
		Luchador luchador = null;
		if (_equipo1.get(perso.getID()) != null) {
			luchador = _equipo1.get(perso.getID());
		} else if (_equipo2.get(perso.getID()) != null) {
			luchador = _equipo2.get(perso.getID());
		}
		return luchador;
	}

	public void retirarsePelea(Personaje yomeretiro, Personaje expulsado, boolean noenviar) {// jugador marchado
		if ((_estadoPelea >= 4) || (yomeretiro == null)) {
			return;
		}
		Luchador lucqueseretira = getLuchadorPorPJ(yomeretiro);
		Luchador elexpulsado = null;
		if (expulsado != null) {
			elexpulsado = getLuchadorPorPJ(expulsado);
		}
		if (lucqueseretira != null) {
			if (yomeretiro.liderMaitre) {
				for (Personaje pjx : yomeretiro.getGrupo().getPersos()) {
					if (pjx == yomeretiro) {
						yomeretiro.liderMaitre = false;
						continue;
					}
					pjx.esMaitre = false;
				}
			}
		}
		if (lucqueseretira != null) {
			switch (_tipo) {
			case 0:// Desafio
			case 1:// PVP
			case 2: // prisma
			case 3: // templo dopeul
			case 4:// PVM
			case 5:// Recaudador
			case 6:// koliseo
				switch (_estadoPelea) {
				case 3: // PELEA EMPEZADA
					if (_tipo == 1) {
						if (MundoDofus.misionesPvP.containsKey(yomeretiro.getID())) {
							int pobj = MundoDofus.misionesPvP.get(yomeretiro.getID());
							Personaje perj = MundoDofus.getPersonaje(pobj);
							ArrayList<Luchador> ganadores = new ArrayList<>();
							ganadores.add(getLuchadorPorPJ(perj));
							ArrayList<Luchador> perdedores = new ArrayList<>();
							perdedores.add(lucqueseretira);
							int honorgana = 150;
							if (getLuchadorPorPJ(perj) != null) {
								honorgana = Formulas.calcularHonorGanado(ganadores, getLuchadorPorPJ(perj));
							}
							int honorperdi = 150;
							if (lucqueseretira != null) {
								honorperdi = Formulas.calcularHonorGanado(perdedores, lucqueseretira);
							}
							if (perj != null && perj.enLinea()) {
								GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perj,
										"El personaje <b>" + yomeretiro.getNombre()
												+ "</b> se retirÓ del combate y se le ha penalizado con <b>-"
												+ honorperdi + "</b> puntos de honor y tu has ganado <b>+" + honorgana
												+ "</b> puntos a cambio.",
										Colores.ROJO);
							}
							MundoDofus.misionesPvP.remove(yomeretiro.getID());
							yomeretiro.setHonor(yomeretiro.getHonor() - honorperdi);
							if (yomeretiro.getHonor() < 0) {
								yomeretiro.setHonor(0);
							}
							perj.addHonor(honorgana);
							Objeto nuevo = MundoDofus.getObjModelo(10275).crearObjDesdeModelo(Emu.CHAPAS_MISION, false);
							if (!perj.addObjetoSimilar(nuevo, true, -1)) {
								MundoDofus.addObjeto(nuevo, true);
								perj.addObjetoPut(nuevo);
								GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(perj, nuevo);
							}
						} else if (MundoDofus.misionesPvP.containsValue(yomeretiro.getID())) {
							int keydelgan = 0;
							for (Entry<Integer, Integer> entry : MundoDofus.misionesPvP.entrySet()) {
								if (entry.getValue() == yomeretiro.getID()) {
									keydelgan = entry.getKey();
									break;
								}
							}
							if (keydelgan != 0) {
								Personaje perj = MundoDofus.getPersonaje(keydelgan);
								ArrayList<Luchador> ganadores = new ArrayList<>();
								ganadores.add(getLuchadorPorPJ(perj));
								ArrayList<Luchador> perdedores = new ArrayList<>();
								perdedores.add(lucqueseretira);
								int honorgana = 50;
								if (getLuchadorPorPJ(perj) != null) {
									honorgana = Formulas.calcularHonorGanado(ganadores, getLuchadorPorPJ(perj));
								}
								int honorperdi = 50;
								if (lucqueseretira != null) {
									honorperdi = Formulas.calcularHonorGanado(perdedores, lucqueseretira);
								}
								if (perj != null && perj.enLinea()) {
									GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perj,
											"El personaje <b>" + yomeretiro.getNombre()
													+ "</b> se ha retirado del combate y se le ha penalizado con <b>-"
													+ honorperdi + "</b> puntos de honor y tu has ganado <b>+"
													+ honorgana + "</b> puntos a cambio.",
											Colores.ROJO);
								}
								MundoDofus.misionesPvP.remove(keydelgan);
								yomeretiro.setHonor(yomeretiro.getHonor() - honorperdi);
								if (yomeretiro.getHonor() < 0) {
									yomeretiro.setHonor(0);
								}
								perj.addHonor(honorgana);
								Objeto nuevo = MundoDofus.getObjModelo(10275).crearObjDesdeModelo(Emu.CHAPAS_MISION,
										false);
								if (!perj.addObjetoSimilar(nuevo, true, -1)) {
									MundoDofus.addObjeto(nuevo, true);
									perj.addObjetoPut(nuevo);
									GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(perj, nuevo);
								}
							}
						}
					}
					if (acabaPeleaSiSeVa(yomeretiro.getID())) {
						if (_espectadores.containsKey(yomeretiro.getID())) { // Expulsa al espectador
							_espectadores.remove(yomeretiro.getID());
							yomeretiro.salirEspectador();
						}
						if (enKoliseo) {
							if (yomeretiro.getKoliseo() != null) {
								yomeretiro.getKoliseo().perdedor(yomeretiro);
							}
						}
						if (enTorneo) {
							if (yomeretiro.enTorneo == 1) {
								Estaticos.perderTorneo(yomeretiro);
							}
						}
						agregarAMuertos(lucqueseretira, lucqueseretira, "retirarsepelea1c");
						yomeretiro.panelGE = "NO";
						yomeretiro.setPelea(null);
						yomeretiro.setPDV(1);
						return;
					}
					if (_espectadores.containsKey(yomeretiro.getID())) { // Expulsa al espectador
						_espectadores.remove(yomeretiro.getID());
						yomeretiro.salirEspectador();
						return;
					}
					agregarAMuertos(lucqueseretira, lucqueseretira, "");
					GestorSalida.ENVIAR_Gt_BORRAR_NOMBRE_ESPADA(_mapaReal,
							_equipo1.containsKey(lucqueseretira.getID()) ? _idLuchInit1 : _idLuchInit2, lucqueseretira);
					if (_equipo1.containsKey(lucqueseretira.getID())) {
						_equipo1.remove(lucqueseretira.getID());
					} else if (_equipo2.containsKey(lucqueseretira.getID())) {
						_equipo2.remove(lucqueseretira.getID());
					}
					yomeretiro.panelGE = "";
					yomeretiro.setPelea(null);
					lucqueseretira._estaRetirado = true;
					// yomeretiro.restarVidaMascota(null);
					if (_tipo == 1) {
						yomeretiro.getHonor();
						yomeretiro.setHonor(yomeretiro.getHonor() - 200);
					}
					if (enKoliseo) {
						if (yomeretiro.getKoliseo() != null) {
							yomeretiro.getKoliseo().perdedor(yomeretiro);
						}
					}
					if (enTorneo) {
						if (yomeretiro.enTorneo == 1) {
							Estaticos.perderTorneo(yomeretiro);
						}
					}
					if (_tipo == 2 || _tipo == 5) {
						yomeretiro.retornoPtoSalvadaRecau();
					} else {
						yomeretiro.retornoMapa();
						yomeretiro.setPDV(1);
						if (yomeretiro.enLinea()) {
							if (!noenviar) {
								GestorSalida.ENVIAR_GV_RESETEAR_PANTALLA_JUEGO(yomeretiro);
							}
						}
					}
					break;
				case 2: // ESTA POR EMPEZAR
					boolean ignoracasitodo = false;
					if (acabaPeleaSiSeVa(yomeretiro.getID())) {
						GestorSalida.ENVIAR_fC_CANTIDAD_DE_PELEAS(_mapaReal);
						GestorSalida.ENVIAR_Gc_BORRAR_BANDERA_EN_MAPA(_mapaReal, _idLuchInit1);
						if (_tipo == 4) {
							if (!_mobGrupo.esFixeado()) {
								_mapaReal.spawnGrupo(-1, 1, true, _mobGrupo.getCeldaID());
							} else {
								_mapaReal.refrescarFix(_mobGrupo.getCeldaID());
							}
						}
						ignoracasitodo = true;
					}
					boolean puedeExpulsar = false;
					if (!ignoracasitodo) {
						if (elexpulsado != null && (_tipo == 6 || _tipo == 5 || _tipo == 2 || _tipo == 3)) {
							if (!noenviar) {
								GestorSalida.ENVIAR_BN_NADA(yomeretiro);
							}
							return;
						}
						/*
						 * if(_luchInit1 != null && _luchInit1.getPersonaje() != null) {
						 * if(yomeretiro.getID() != _luchInit1.getPersonaje().getID()) { if (!noenviar)
						 * GestorSalida.ENVIAR_BN_NADA(yomeretiro); return; } }
						 */
						if (_luchInit1 != null && _luchInit1.getPersonaje() != null) {
							if (yomeretiro.getID() == _luchInit1.getPersonaje().getID()) {
								puedeExpulsar = true;
							}
						}
						if (_luchInit2 != null && _luchInit2.getPersonaje() != null) {
							if (yomeretiro.getID() == _luchInit2.getPersonaje().getID()) {
								puedeExpulsar = true;
							}
						}
					}
					if (peleainvasion || esAngel) {
						puedeExpulsar = false;
					}
					if (elexpulsado != null && puedeExpulsar) {// si expulsa a otro jugador
						if (_tipo == 6) {
							GestorSalida.ENVIAR_BN_NADA(expulsado);
							return;
						}
						int idLucExpuls = elexpulsado.getID();
						if (elexpulsado.getEquipoBin() == lucqueseretira.getEquipoBin()) {
							if (idLucExpuls != lucqueseretira.getID()) {
								if (_equipo1.containsKey(elexpulsado.getID())) {
									_mapaCopia.removerLuchador(elexpulsado);
									_equipo1.remove(elexpulsado.getID());
								} else if (_equipo2.containsKey(elexpulsado.getID())) {
									_mapaCopia.removerLuchador(elexpulsado);
									_equipo2.remove(elexpulsado.getID());
								}
								GestorSalida.ENVIAR_GM_BORRAR_LUCHADOR(this, idLucExpuls, 3);
								GestorSalida.ENVIAR_Gt_BORRAR_NOMBRE_ESPADA(_mapaReal,
										_equipo1.containsKey(elexpulsado.getID()) ? _idLuchInit1 : _idLuchInit2,
										elexpulsado);
								elexpulsado._estaRetirado = true;
								expulsado.panelGE = "";
								expulsado.setPelea(null);
								if (expulsado.enLinea()) {
									GestorSalida.ENVIAR_GV_RESETEAR_PANTALLA_JUEGO(expulsado);
								}
								if (_tipo == 2 || _tipo == 5) {
									expulsado.retornoPtoSalvadaRecau();
								} else {
									expulsado.retornoMapa();
								}
							}
						}
					} else if (elexpulsado == null) {// se expulsa a si mismo
						if (_equipo1.containsKey(lucqueseretira.getID())) {
							_mapaCopia.removerLuchador(lucqueseretira);
							_equipo1.remove(lucqueseretira.getID());
						} else if (_equipo2.containsKey(lucqueseretira.getID())) {
							_mapaCopia.removerLuchador(lucqueseretira);
							_equipo2.remove(lucqueseretira.getID());
						}
						GestorSalida.ENVIAR_GM_BORRAR_LUCHADOR(this, lucqueseretira.getID(), 3);
						GestorSalida.ENVIAR_Gt_BORRAR_NOMBRE_ESPADA(_mapaReal,
								_equipo1.containsKey(lucqueseretira.getID()) ? _idLuchInit1 : _idLuchInit2,
								lucqueseretira);
						lucqueseretira._estaRetirado = true;
						yomeretiro.panelGE = "";
						yomeretiro.setPelea(null);
						if (yomeretiro.enLinea()) {
							if (!noenviar) {
								GestorSalida.ENVIAR_GV_RESETEAR_PANTALLA_JUEGO(yomeretiro);
							}
						}
						if (_tipo == 2 || _tipo == 5) {
							yomeretiro.retornoPtoSalvadaRecau();
						} else {
							yomeretiro.retornoMapa();
							yomeretiro.setPDV(1);
							// yomeretiro.restarVidaMascota(null);
						}
					}
					acaboPelea("retirarsepelea1c");
					break;
				case 4: // pelea acabada
					break;
				default:
					System.out.println("ERROR, _estadoPelea: " + _estadoPelea + " tipo de combate:" + _tipo
							+ " LuchadorExp:" + elexpulsado + " LuchadorRet:" + lucqueseretira);
					break;
				}
				break;
			}
		} else {
			if (yomeretiro != null) {
				if (_espectadores.containsKey(yomeretiro.getID())) { // Expulsa al espectador
					_espectadores.remove(yomeretiro.getID());
				}
				yomeretiro.salirEspectador();
				return;
			}
		}
	}

	public String stringOrdenJugadores() {
		StringBuilder packet = new StringBuilder("GTL");
		List<Luchador> _turnosPelea = new ArrayList<>();
		_turnosPelea.addAll(turnosPelea);
		for (Luchador luchador : _turnosPelea) {
			if (luchador == null) {
				continue;
			}
			if (_estadoPelea == 4) {
				break;
			}
			if (luchador.estaMuerto() || luchador.estaRetirado()) {
				continue;
			}
			packet.append("|" + luchador.getID());
		}
		return packet.toString() + (char) 0x00;
	}

	public int getSigIDLuchador() {
		int g = -1;
		for (Luchador luchador : luchadoresDeEquipo(3)) {
			if (luchador.getID() < g) {
				g = luchador.getID();
			}
		}
		_numeroInvos++;
		g--;
		return g;
	}

	void addLuchadorEnEquipo(Luchador luchador, int equipo) {
		if (equipo == 0) {
			_equipo1.put(luchador.getID(), luchador);
		} else if (equipo == 1) {
			_equipo2.put(luchador.getID(), luchador);
		}
	}

	public String infoPeleasEnMapa() {
		if (_estadoPelea >= 4) {
			_mapaReal.quitarPelea(_id);
			return "";
		}
		Date actDate = new Date();
		long tiempo = actDate.getTime() + 3600000L - (System.currentTimeMillis() - _tiempoInicio);
		StringBuilder infos = new StringBuilder(_id + ";" + (_tiempoInicio == 0 ? "-1" : tiempo) + ";");
		int jugEquipo0 = 0;
		int jugEquipo1 = 0;
		for (Luchador l : _equipo1.values()) {
			if ((l == null) || l.esInvocacion()) {
				continue;
			}
			jugEquipo0++;
		}
		for (Luchador l : _equipo2.values()) {
			if ((l == null) || l.esInvocacion()) {
				continue;
			}
			jugEquipo1++;
		}
		infos.append("0,");
		switch (_tipo) {
		case 0:
			infos.append("0,");
			infos.append(jugEquipo0 + ";");
			infos.append("0,");
			infos.append("0,");
			infos.append(jugEquipo1 + ";");
			break;
		case 1:
			infos.append(_luchInit1.getPersonaje().getAlineacion() + ",");
			infos.append(jugEquipo0 + ";");
			infos.append("0,");
			infos.append(_luchInit2.getPersonaje().getAlineacion() + ",");
			infos.append(jugEquipo1 + ";");
			break;
		case 2:
			infos.append(_luchInit1.getPersonaje().getAlineacion() + ",");
			infos.append(jugEquipo0 + ";");
			infos.append("0,");
			infos.append(_Prisma.getAlineacion() + ",");
			infos.append(jugEquipo1 + ";");
			break;
		case 3:
			infos.append("0,");
			infos.append(jugEquipo0 + ";");
			infos.append("1,");
			infos.append(_equipo2.get(_equipo2.keySet().toArray()[0]).getMob().getModelo().getAlineacion() + ",");
			infos.append(jugEquipo1 + ";");
			break;
		case 4:
			infos.append("0,");
			infos.append(jugEquipo0 + ";");
			infos.append("1,");
			infos.append(_equipo2.get(_equipo2.keySet().toArray()[0]).getMob().getModelo().getAlineacion() + ",");
			infos.append(jugEquipo1 + ";");
			break;
		case 5:
			infos.append("0,");
			infos.append(jugEquipo0 + ";");
			infos.append("3,");
			infos.append("0,");
			infos.append(jugEquipo1 + ";");
			break;
		}
		return infos.toString();
	}

	private boolean verificaSiQuedaUno() {
		for (Luchador luchador : _equipo1.values()) {
			if (luchador._estaMuerto || luchador.esInvocacion() || luchador.estaRetirado()) {
				continue;
			}
			return true;
		}
		for (Luchador luchador : _equipo2.values()) {
			if (luchador._estaMuerto || luchador.esInvocacion() || luchador.estaRetirado()) {
				continue;
			}
			return true;
		}
		return false;
	}

	private int cuantosQueda() {
		int num = 0;
		for (Luchador luchador : _equipo1.values()) {
			if (luchador._estaMuerto || luchador.getPersonaje() == null || luchador.esInvocacion()
					|| luchador.getMob() != null || luchador.esRecaudador() || luchador.esPrisma()
					|| luchador.estaRetirado()) {
				continue;
			}
			num++;
		}
		for (Luchador luchador : _equipo2.values()) {
			if (luchador._estaMuerto || luchador.getPersonaje() == null || luchador.esInvocacion()
					|| luchador.getMob() != null || luchador.esRecaudador() || luchador.esPrisma()
					|| luchador.estaRetirado()) {
				continue;
			}
			num++;
		}
		return num;
	}

	private boolean acabaPeleaSiSeVa(int id) {
		boolean acaba = true;
		if (_equipo1.containsKey(id)) {
			for (Luchador luchador : _equipo1.values()) {
				if (luchador._estaMuerto || luchador.esInvocacion() || luchador._id == id) {
					continue;
				}
				acaba = false;
			}
		} else if (_equipo2.containsKey(id)) {
			for (Luchador luchador : _equipo2.values()) {
				if (luchador._estaMuerto || luchador.esInvocacion() || luchador._id == id) {
					continue;
				}
				acaba = false;
			}
		} else if (_espectadores.containsKey(id)) {
			return true;
		}
		return acaba;
	}

	public static void agregarEspadaDePelea(Mapa mapa, Personaje perso) {
		for (Entry<Integer, Pelea> peleas : mapa.getPeleas().entrySet()) {
			Pelea pelea = peleas.getValue();
			if (pelea._estadoPelea == 2) {
				Personaje persoInit1 = pelea._luchInit1.getPersonaje();
				int id1 = pelea._idLuchInit1;
				int id2 = pelea._idLuchInit2;
				StringBuilder enviar1 = new StringBuilder("");
				StringBuilder enviar2 = new StringBuilder("");
				boolean primero1 = true;
				boolean primero2 = true;
				Personaje persoInit2 = null;
				switch (pelea._tipo) {
				case 0:
					persoInit2 = pelea._luchInit2.getPersonaje();
					if (persoInit2 == null) {
						continue;
					}
					GestorSalida.ENVIAR_Gc_MOSTRAR_ESPADA_A_JUGADOR(perso, 0, id1, id2, persoInit1.getCelda().getID(),
							"0;-1", persoInit2.getCelda().getID(), "0;-1");
					break;
				case 1:
					persoInit2 = pelea._luchInit2.getPersonaje();
					if (persoInit2 == null) {
						continue;
					}
					GestorSalida.ENVIAR_Gc_MOSTRAR_ESPADA_A_JUGADOR(perso, 0, id1, id2, persoInit1.getCelda().getID(),
							"0;" + persoInit1.getAlineacion(), persoInit2.getCelda().getID(),
							"0;" + persoInit2.getAlineacion());
					break;
				case 2:
					GestorSalida.ENVIAR_Gc_MOSTRAR_ESPADA_A_JUGADOR(perso, 0, id1, pelea._Prisma.getID(),
							persoInit1.getCelda().getID(), "0;" + persoInit1.getAlineacion(), pelea._Prisma.getCelda(),
							"0;" + pelea._Prisma.getAlineacion());
					break;
				case 4:
					GestorSalida.ENVIAR_Gc_MOSTRAR_ESPADA_A_JUGADOR(perso, 4, id1, pelea._mobGrupo.getID(),
							persoInit1.getCelda().getID(), "0;-1", pelea._mobGrupo.getCeldaID() - 1, "1;-1");
					break;
				case 5:
					GestorSalida.ENVIAR_Gc_MOSTRAR_ESPADA_A_JUGADOR(perso, 5, id1, pelea._Recaudador.getID(),
							persoInit1.getCelda().getID(), "0;-1", pelea._Recaudador.getCeldalID(), "3;-1");
					break;
				}
				for (Entry<Integer, Luchador> entry : pelea._equipo1.entrySet()) {
					Luchador luchador = entry.getValue();
					if (!primero1) {
						enviar1.append("|+");
					}
					enviar1.append(luchador.getID() + ";" + luchador.getNombreLuchador() + ";" + luchador.getNivel());
					primero1 = false;
				}
				GestorSalida.ENVIAR_Gt_AGREGAR_NOMBRE_ESPADA(perso, id1, enviar1.toString());
				for (Entry<Integer, Luchador> entry : pelea._equipo2.entrySet()) {
					Luchador luchador = entry.getValue();
					if (!primero2) {
						enviar2.append("|+");
					}
					enviar2.append(luchador.getID() + ";" + luchador.getNombreLuchador() + ";" + luchador.getNivel());
					primero2 = false;
				}
				GestorSalida.ENVIAR_Gt_AGREGAR_NOMBRE_ESPADA(perso, id2, enviar2.toString());
			}
		}
	}

	public ArrayList<Luchador> getListaMuertos() {
		return _listaMuertos;
	}

	void borrarUnMuerto(Luchador objetivo) {
		_listaMuertos.remove(objetivo);
	}

	public static class Trampa {
		private Luchador _lanzador;
		private Celda _celda;
		private byte _tamaÑo;
		private int _hechizo;
		private StatsHechizos _trampaHechizo;
		private Pelea _pelea;
		private int _color;
		private boolean _visible = true;
		private int _paramEquipoDueÑo = -1;
		boolean activada = false;

		Trampa(Pelea pelea, Luchador lanzador, Celda celda, byte tamaÑo, StatsHechizos trampaHechizo, int hechizo) {
			_pelea = pelea;
			_lanzador = lanzador;
			_celda = celda;
			_hechizo = hechizo;
			_tamaÑo = tamaÑo;
			_trampaHechizo = trampaHechizo;
			_color = CentroInfo.getColorTrampa(hechizo);
			_paramEquipoDueÑo = lanzador.getParamEquipoAliado();
		}

		public Celda getCelda() {
			return _celda;
		}

		public int getParamEquipoDueÑo() {
			return _paramEquipoDueÑo;
		}

		public byte getTamaÑo() {
			return _tamaÑo;
		}

		public Luchador getLanzador() {
			return _lanzador;
		}

		void esVisibleParaEnemigo() {
			_visible = true;
		}

		public boolean esVisible() {
			return _visible;
		}

		public int getColor() {
			return _color;
		}

		void desaparecer() {
			String str = "GDZ-" + _celda.getID() + ";" + _tamaÑo + ";" + _color;
			GestorSalida.ENVIAR_GA_ACCION_PELEA(_pelea, _paramEquipoDueÑo, 999, _lanzador.getID() + "", str);
			str = "GDC" + _celda.getID();
			GestorSalida.ENVIAR_GA_ACCION_PELEA(_pelea, _paramEquipoDueÑo, 999, _lanzador.getID() + "", str);
			if (_visible) {
				int equipo2 = _lanzador.getParamEquipoEnemigo();
				String str2 = "GDZ-" + _celda.getID() + ";" + _tamaÑo + ";" + _color;
				GestorSalida.ENVIAR_GA_ACCION_PELEA(_pelea, equipo2, 999, _lanzador.getID() + "", str2);
				str2 = "GDC" + _celda.getID();
				GestorSalida.ENVIAR_GA_ACCION_PELEA(_pelea, equipo2, 999, _lanzador.getID() + "", str2);
			}
		}

		void aparecer(int equipo) {
			String str = "GDZ+" + _celda.getID() + ";" + _tamaÑo + ";" + _color;
			GestorSalida.ENVIAR_GA_ACCION_PELEA(_pelea, equipo, 999, _lanzador.getID() + "", str);
			str = "GDC" + _celda.getID() + ";Haaaaaaaaz3005;";
			GestorSalida.ENVIAR_GA_ACCION_PELEA(_pelea, equipo, 999, _lanzador.getID() + "", str);
		}

		void activaTrampa(Luchador trampeado) {
			if (trampeado._estaMuerto) {
				return;
			}
			activada = true;
			_pelea.getTrampasT().remove(this);
			desaparecer();
			String str = _hechizo + "," + _celda.getID() + ",0,1,1," + _lanzador.getID();
			GestorSalida.ENVIAR_GA_ACCION_PELEA(_pelea, 7, 307, trampeado.getID() + "", str);
			ArrayList<Celda> celdas = new ArrayList<>();
			celdas.add(_celda);
			for (int a = 0; a < _tamaÑo; a++) {
				char[] dirs = { 'b', 'd', 'f', 'h' };
				ArrayList<Celda> cases2 = new ArrayList<>();
				cases2.addAll(celdas);
				for (Celda aCell : cases2) {
					for (char d : dirs) {
						Celda celda = _pelea.getMapaCopia()
								.getCelda(Camino.getSigIDCeldaMismaDir(aCell.getID(), d, _pelea.getMapaCopia(), true));
						if (celda == null) {
							continue;
						}
						if (!celdas.contains(celda)) {
							celdas.add(celda);
						}
					}
				}
			}
			Luchador trampaLanzador;
			if (_lanzador.getPersonaje() == null) {
				trampaLanzador = new Luchador(_pelea, _lanzador.getMob());
			} else {
				trampaLanzador = new Luchador(_pelea, _lanzador.getPersonaje(), _lanzador);
			}
			trampaLanzador.setCeldaPelea(_celda);
			if (_trampaHechizo.getHechizoID() == 1688) {
				_trampaHechizo.aplicaTrampaAPelea(_pelea, trampaLanzador, _celda, celdas, false);
			} else {
				_trampaHechizo.aplicaTrampaAPelea(_pelea, trampaLanzador, trampeado.getCeldaPelea(), celdas, false);
			}
			_pelea.acaboPelea("activatrampa");
			// System.out.println("PISA TRAMPA");
		}
	}

	public static class HechizoLanzado {
		private int _hechizoId = 0;
		private int _sigLanzamiento = 0;
		private Luchador _objetivo = null;

		private HechizoLanzado(Luchador objetivo, StatsHechizos sHechizo, Luchador lanzador) {
			_objetivo = objetivo;
			_hechizoId = sHechizo.getHechizoID();
			if (lanzador.getTipo() == 1
					&& lanzador.getPersonaje().getHechizosSetClase().containsKey(sHechizo.getHechizoID())) {
				int modi = lanzador.getPersonaje().getModifSetClase(sHechizo.getHechizoID(), 286);
				_sigLanzamiento = sHechizo.getSigLanzamiento() - modi;
			} else {
				_sigLanzamiento = sHechizo.getSigLanzamiento();
			}
		}

		private void actuSigLanzamiento() {
			_sigLanzamiento--;
		}

		public int getSigLanzamiento() {
			return _sigLanzamiento;
		}

		public int getID() {
			return _hechizoId;
		}

		public Luchador getObjetivo() {
			return _objetivo;
		}

		public static boolean poderSigLanzamiento(Luchador lanzador, int id) {
			for (HechizoLanzado HL : lanzador.getHechizosLanzados()) {
				if (HL._hechizoId == id && HL.getSigLanzamiento() > 0) {
					if (lanzador.getPersonaje() != null) {
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(lanzador.getPersonaje(),
								"No puedes lanzar este hechizo antes de <b>" + HL.getSigLanzamiento()
										+ "</b> turno(s).",
								Colores.ROJO);
					}
					return false;
				}
			}
			return true;
		}

		public static int getNroLanzamientos(Luchador lanzador, int id) {
			int nro = 0;
			for (HechizoLanzado HL : lanzador.getHechizosLanzados()) {
				if (HL._hechizoId == id) {
					nro++;
				}
			}
			return nro;
		}

		private static int getNroLanzPorObjetivo(Luchador lanzador, Luchador objetivo, int id) {
			if (objetivo == null) {
				return 0;
			}
			int nro = 0;
			for (HechizoLanzado HL : lanzador.getHechizosLanzados()) {
				if (HL._objetivo == null) {
					continue;
				}
				if (HL._hechizoId == id && HL._objetivo.getID() == objetivo.getID()) {
					nro++;
				}
			}
			return nro;
		}
	}

	public static class Luchador {
		private int _id = 0;
		private boolean _puedeJugar = false;
		public boolean stop = false;
		public Luchador atacoAOtro = null;
		public Luchador realLuc = null;
		public boolean excepcionMover = false;
		private Pelea _pelea;
		private int _tipo = 0;
		private MobGrado _mob = null;
		private Personaje _perso = null;
		private int _equipoBin = -2;
		private Celda _celda;
		public Map<EfectoHechizo, Luchador> _buffsPelea = new HashMap<>();
		private Luchador _invocador;
		private int _PDVMAX;
		private int _PDV;
		boolean _estaMuerto;
		boolean _estaRetirado;
		private int _gfxID;
		private Map<Integer, Integer> _estados = new TreeMap<>();
		private Luchador _transportado;
		private Luchador _transportadoPor;
		private Recaudador _recaudador = null;
		private Prisma _prisma = null;
		private Personaje _doble = null;
		private ArrayList<HechizoLanzado> _hechizosLanzados = new ArrayList<>();
		private Luchador _objetivoDestZurca = null;
		private float _bonusAlineacion = 0;
		private Map<Integer, Integer> _bonusCastigo = new TreeMap<>();
		int _nroInvocaciones = 0;
		private int _invosquetuvo = 1;
		private int _idHechiLanzReto = -1;
		private int _idCeldaIniTurnoReto;
		private ArrayList<Integer> _hechiLanzadosReto = new ArrayList<>();
		ArrayList<Integer> _mobMatadosReto = new ArrayList<>();
		private boolean _intocable = false;
		private boolean _contaminacion = false;
		private boolean _contaminado = false;
		private int _turnosParaMorir = 0;
		private int _pjAtacante = 0;
		private int _prospeccionTemporal = 0;
		private int ataquedYb = 0;
		private int ataqueotros = 0;
		public boolean statico = false;
		public boolean _desconectado = false;
		private int _turnosRestantes = 10;
		public boolean isCac = false;
		public int penalizado = 0;
		public boolean realizaaccion = true;
		public boolean usoTP = false;
		public boolean usoATR = false;
		public Luchador manualObj = null;
		public Map<Integer, Integer> hechizosUsados = new TreeMap<>();
		private char _direccion = 'b';

		public void setDireccion(char direccion) {
			_direccion = direccion;
		}

		public char getDireccion() {
			return _direccion;
		}

		public void removePdv(Luchador caster, int pdv) {
			_PDV -= pdv;
		}

		public void removePdvMax(int pdv) {
			_PDVMAX = _PDVMAX - pdv;
			if (_PDV > _PDVMAX) {
				_PDV = _PDVMAX;
			}
		}

		public void setPjAtacante(int id) {
			_pjAtacante = id;
		}

		public int getPjAtacante() {
			return _pjAtacante;
		}

		void setBonusCastigo(int bonus, int stat) {
			_bonusCastigo.put(stat, bonus);
		}

		public Map<Integer, Integer> getBonusCastigo() {
			return _bonusCastigo;
		}

		int getBonusCastigo(int stat) {
			int bonus = 0;
			if (_bonusCastigo.containsKey(stat)) {
				bonus = _bonusCastigo.get(stat);
			}
			return bonus;
		}

		public Luchador getObjetivoDestZurca() {
			return _objetivoDestZurca;
		}

		public void setObjetivoDestZurca(Luchador objetivo) {
			_objetivoDestZurca = objetivo;
		}

		public int getTipo() {
			return _tipo;
		}

		Luchador(Pelea pelea, MobGrado mob) {
			_pelea = pelea;
			_tipo = 2;
			_mob = mob;
			_id = mob.getIdEnPelea();
			_PDVMAX = mob.getPDVMAX();
			_PDV = mob.getPDV();
			_gfxID = getGfxDefecto();
		}

		Luchador(Pelea pelea, Personaje perso, Luchador real) {
			_pelea = pelea;
			realLuc = real;
			if (perso.esDoble()) {
				_tipo = 10;
				_doble = perso;
			} else {
				_tipo = 1;
				_perso = perso;
			}
			_id = perso.getID();
			_PDVMAX = perso.getPDVMAX();
			_PDV = perso.getPDV();
			_gfxID = getGfxDefecto();
		}

		Luchador(Pelea pelea, Personaje perso) {
			_pelea = pelea;
			if (perso.esDoble()) {
				_tipo = 10;
				_doble = perso;
			} else {
				_tipo = 1;
				_perso = perso;
			}
			_id = perso.getID();
			_PDVMAX = perso.getPDVMAX();
			_PDV = perso.getPDV();
			_gfxID = getGfxDefecto();
		}

		private Luchador(Pelea pelea, Recaudador recaudador) {
			_pelea = pelea;
			_tipo = 5;
			_recaudador = recaudador;
			_id = -1;
			Gremio gremio = MundoDofus.getGremio(recaudador.getGremioID());
			_PDVMAX = gremio.getNivel() * 100;
			_PDV = gremio.getNivel() * 100;
			_gfxID = 6000;
		}

		private Luchador(Pelea pelea, Prisma prisma) {
			_pelea = pelea;
			_tipo = 7;
			_prisma = prisma;
			_id = -1;
			_PDVMAX = prisma.getNivel() * 10000;
			_PDV = prisma.getNivel() * 10000;
			_gfxID = prisma.getAlineacion() == 1 ? 8101 : 8100;
			prisma.actualizarStats();
		}

		public ArrayList<HechizoLanzado> getHechizosLanzados() {
			return _hechizosLanzados;
		}

		private void actualizaHechizoLanzado() {
			ArrayList<HechizoLanzado> copia = new ArrayList<>();
			copia.addAll(_hechizosLanzados);
			int i = 0;
			for (HechizoLanzado HL : copia) {
				HL.actuSigLanzamiento();
				if (HL.getSigLanzamiento() <= 0) {
					_hechizosLanzados.remove(i);
					i--;
				}
				i++;
			}
		}

		private void addHechizoLanzado(Luchador objetivo, StatsHechizos sort, Luchador lanzador) {
			HechizoLanzado lanzado = new HechizoLanzado(objetivo, sort, lanzador);
			_hechizosLanzados.add(lanzado);
		}

		public int getID() {
			return _id;
		}

		public Luchador getTransportando() {
			return _transportado;
		}

		public void setTransportado(Luchador transportado) {
			_transportado = transportado;
		}

		public Luchador getTransportadoPor() {
			return _transportadoPor;
		}

		public void setTransportadoPor(Luchador transportadoPor) {
			_transportadoPor = transportadoPor;
		}

		public int getGfxID() {
			return _gfxID;
		}

		public void setGfxID(int gfxID) {
			_gfxID = gfxID;
		}

		public Map<EfectoHechizo, Luchador> getBuffPelea() {
			return _buffsPelea;
		}

		public boolean esInvisible() {
			return tieneBuff(150);
		}

		public Celda getCeldaPelea() {
			return _celda;
		}

		public void setCeldaPelea(Celda celda) {
			_celda = celda;
		}

		public void setEquipoBin(int i) {
			_equipoBin = i;
		}

		public boolean estaMuerto() {
			if (getPelea() != null) {
				if (getPelea()._listaMuertos.contains(this)) {
					return true;
				}
			}
			return _estaMuerto;
		}

		public void setMuerto(boolean estaMuerto) {
			_estaMuerto = estaMuerto;
		}

		public void setRetirado(boolean estaMuerto) {
			_estaRetirado = estaMuerto;
		}

		public boolean estaRetirado() {
			return _estaRetirado;
		}

		public Personaje getPersonaje() {
			if (_tipo == 1 || _tipo == 0) {
				return _perso;
			}
			return null;
		}

		public Recaudador getRecau() {
			if (_tipo == 5) {
				return _recaudador;
			}
			return null;
		}

		public Prisma getPrisma() {
			if (_tipo == 7) {
				return _prisma;
			}
			return null;
		}

		private boolean calculaSiGC(int tauxCC) {
			if (tauxCC < 2) {
				return false;
			}
			int agi = getTotalStatsConBuff().getEfecto(CentroInfo.STATS_ADD_AGILIDAD);
			if (agi < 0) {
				agi = 0;
			}
			tauxCC -= getTotalStatsConBuff().getEfecto(CentroInfo.STATS_ADD_GOLPES_CRITICOS);
			tauxCC = (int) ((tauxCC * 2.9901) / Math.log(agi + 12));// Influence de l'agi
			if (tauxCC < 2) {
				tauxCC = 2;
			}
			int jet = Formulas.getRandomValor(1, tauxCC);
			return (jet == tauxCC);
		}

		public boolean testSiEsGC(int porcGC, StatsHechizos sHechizo, Luchador luchador) {
			Personaje perso = luchador.getPersonaje();
			if ((porcGC < 2) || tieneBuff(781)) {
				return false;
			}
			Stats statsConBuff = getTotalStatsConBuff();
			int agi = statsConBuff.getEfecto(CentroInfo.STATS_ADD_AGILIDAD);
			if (agi < 0) {
				agi = 0;
			}
			porcGC -= statsConBuff.getEfecto(CentroInfo.STATS_ADD_GOLPES_CRITICOS);
			if (luchador.getTipo() == 1 && perso.getHechizosSetClase().containsKey(sHechizo.getHechizoID())) {
				int modi = perso.getModifSetClase(sHechizo.getHechizoID(), 287);
				porcGC -= modi;
			}
			porcGC = (int) ((porcGC * 2.99011001130495D) / Math.log(agi + 12));
			if (porcGC < 2) {
				porcGC = 2;
			}
			int jet = Formulas.getRandomValor(1, porcGC);
			return (jet == porcGC);
		}

		public ArrayList<EfectoHechizo> getBuffsPorEfectoID(int efectotID) {
			ArrayList<EfectoHechizo> buffs = new ArrayList<>();
			Map<EfectoHechizo, Luchador> buffsPelea = new HashMap<>();
			buffsPelea.putAll(_buffsPelea);
			for (EfectoHechizo buff : buffsPelea.keySet()) {
				if (buff == null) {
					continue;
				}
				if (buff.getEfectoID() == efectotID) {
					buffs.add(buff);
				}
			}
			return buffs;
		}

		public Stats getTotalStatsSinBuff() {
			Stats stats = new Stats(new TreeMap<>());
			if (_tipo == 1 || _tipo == 0) { // Personnage
				stats = _perso.getTotalStats();
			}
			if (_tipo == 2) { // Mob
				stats = _mob.getStats();
			}
			if (_tipo == 5) { // Percepteur
				stats = MundoDofus.getGremio(_recaudador.getGremioID()).getStatsPelea();
			}
			if (_tipo == 7) {
				stats = _prisma.getStats();
			}
			if (_tipo == 10) { // Double
				stats = _doble.getTotalStats();
			}
			return stats;
		}

		public Stats getTotalStatsConBuff() {
			Stats stats = new Stats(new TreeMap<>());
			if (_tipo == 1) {
				stats = _perso.getTotalStats();
			}
			if (_tipo == 10) { // Double
				stats = _doble.getTotalStats();
			}
			if (getPelea().getTipoPelea() == 1 || getPelea().getTipoPelea() == 0 || getPelea().getTipoPelea() == 5
					|| getPelea().getTipoPelea() == 2 || getPelea().getTipoPelea() == 3) {// Personnage
				int maxresis = Emu.maximaresis;
				if (getPersonaje() != null) {
					if (getPersonaje().buffClase == 1) {
						maxresis = maxresis + 5;
					}
				}
				for (int i = 0; i < Emu.resis.length; i++) {
					if (stats.getEfecto(Emu.resis[i]) > maxresis) {
						stats.addUnStat(Emu.resis[i], -(stats.getEfecto(Emu.resis[i]) - maxresis));
					}
				}
				for (int i = 0; i < Emu.resisfijas.length; i++) {
					if (stats.getEfecto(Emu.resisfijas[i]) > 10) {
						stats.addUnStat(Emu.resisfijas[i], -(stats.getEfecto(Emu.resisfijas[i]) - 10));
					}
				}
				int limitepa = 14;
				int limitepm = 6;
				if (stats.getEfecto(111) > limitepa) {
					stats.especificarStat(111, limitepa);
				}
				if (stats.getEfecto(128) > limitepm) {
					stats.especificarStat(128, limitepm);
				}
			} else if (getPelea().getTipoPelea() == 4) {
				int limitepa = 15;
				int limitepm = 7;
				// boolean reduc = false;
				if (getPelea().getMapaCopia() != null) {
					switch (getPelea().getMapaCopia().getID()) {
					case 30063:// desafio diario
						limitepa = Emu.LIMITEPA;
						limitepm = Emu.LIMITEPM;
						break;
					case 11095:// klaramar
						limitepa = 20;
						limitepm = 10;
						break;
					}
				}
				if (stats.getEfecto(111) > limitepa) {
					stats.especificarStat(111, limitepa);
				}
				if (stats.getEfecto(128) > limitepm) {
					stats.especificarStat(128, limitepm);
				}
				int maxresis = 60;
				if (getPersonaje() != null) {
					if (getPersonaje().buffClase == 1) {
						maxresis = maxresis + 5;
					}
				}
				for (int i = 0; i < Emu.resis.length; i++) {
					if (stats.getEfecto(Emu.resis[i]) > maxresis) {
						stats.addUnStat(Emu.resis[i], -(stats.getEfecto(Emu.resis[i]) - maxresis));
					}
				}
			}
			if (_tipo == 2) {
				stats = _mob.getStats();
			}
			if (_tipo == 5) { // Percepteur
				stats = MundoDofus.getGremio(_recaudador.getGremioID()).getStatsPelea();
			}
			if (_tipo == 7) {
				stats = _prisma.getStats();
			}
			stats = Stats.acumularStats(stats, getBuffsStatsPelea());
			return stats;
		}

		public int getPA() {
			if (_tipo == 1 || _tipo == 0) {
				return getTotalStatsConBuff().getEfecto(CentroInfo.STATS_ADD_PA);
			}
			if (_tipo == 2) {
				return getTotalStatsConBuff().getEfecto(CentroInfo.STATS_ADD_PA) + _mob.getPA();
			}
			if (_tipo == 5) {
				return getTotalStatsConBuff().getEfecto(CentroInfo.STATS_ADD_PM) + 6;
			}
			if (_tipo == 10) {
				int PA = getTotalStatsConBuff().getEfecto(CentroInfo.STATS_ADD_PA) > 12 ? 12
						: getTotalStatsConBuff().getEfecto(CentroInfo.STATS_ADD_PA);
				PA += this.getBuffValor(CentroInfo.STATS_ADD_PA);
				return PA;
			}
			return 0;
		}

		public int getPM() {
			if (_tipo == 1 || _tipo == 0) {
				return getTotalStatsConBuff().getEfecto(CentroInfo.STATS_ADD_PM);
			}
			if (_tipo == 2) {
				return getTotalStatsConBuff().getEfecto(CentroInfo.STATS_ADD_PM) + _mob.getPM();
			}
			if (_tipo == 5) {
				return getTotalStatsConBuff().getEfecto(CentroInfo.STATS_ADD_PM) + 3;
			}
			if (_tipo == 10) {
				int PM = getTotalStatsConBuff().getEfecto(CentroInfo.STATS_ADD_PM);
				PM += this.getBuffValor(CentroInfo.STATS_ADD_PM);
				return PM;
			}
			return 0;
		}

		public Stats getBuffsStatsPelea() {
			Stats stats = new Stats();
			for (EfectoHechizo entry : _buffsPelea.keySet()) {
				stats.addUnStat(entry.getEfectoID(), entry.getValor());
			}
			return stats;
		}

		public String stringGM() {
			StringBuilder str = new StringBuilder();
			if (esInvisible()) {
				str.append("0;");
			} else {
				str.append(_celda.getID() + ";");
			}
			str.append(Camino.getIndexDireccion(getDireccion()) + ";");// direccion
			str.append("0;");// estrellas bonus
			str.append(_id + ";");
			str.append(getNombreLuchador() + ";");
			Stats totalStats = getTotalStatsConBuff();
			switch (_tipo) {
			case 1:
				str.append(_perso.getClase(false) + ";");
				str.append(_perso.getGfxID() + "^" + _perso.getTalla() + ";");
				str.append(_perso.getSexo() + ";");
				str.append(_perso.getNivel() + ";");
				str.append(_perso.getAlineacion() + ",");
				str.append(_perso.getNivelAlineacion() + ",");
				str.append((_perso.estaMostrandoAlas() ? _perso.getNivelAlineacion() : "0") + ",");
				str.append((_perso.getID() + _perso.getNivel()) + "," + (_perso.getDeshonor() > 0 ? 1 : 0) + ";");
				str.append((_perso.getColor1() == -1 ? "-1" : Integer.toHexString(_perso.getColor1())) + ";");
				str.append((_perso.getColor2() == -1 ? "-1" : Integer.toHexString(_perso.getColor2())) + ";");
				str.append((_perso.getColor3() == -1 ? "-1" : Integer.toHexString(_perso.getColor3())) + ";");
				str.append(_perso.getStringAccesorios() + ";");
				str.append(getPDVConBuff() + ";");
				str.append(totalStats.getEfecto(111) + ";");
				str.append(totalStats.getEfecto(128) + ";");

				str.append(totalStats.getEfecto(214) + ";");
				str.append(totalStats.getEfecto(210) + ";");
				str.append(totalStats.getEfecto(213) + ";");
				str.append(totalStats.getEfecto(211) + ";");
				str.append(totalStats.getEfecto(212) + ";");

				str.append(totalStats.getEfecto(160) + ";");// PA
				str.append(totalStats.getEfecto(161) + ";");// PM

				//str.append(totalStats.getEfecto(415) + ";");// DaÑos Fuego
				/*str.append(totalStats.getEfecto(416) + ";");// DaÑos Fuego
				str.append(totalStats.getEfecto(417) + ";");// DaÑos Fuego
				str.append(totalStats.getEfecto(418) + ";");// DaÑos Fuego
				str.append(totalStats.getEfecto(419) + ";");// DaÑos Fuego*/

				str.append(_equipoBin + ";");
				if ((_perso.estaMontando()) && (_perso.getMontura() != null)) {
					str.append(_perso.getMontura().getStringColor(_perso.stringColorDueÑoPavo()));
				}
				str.append(";");
				break;
			case 2:// mob
				str.append("-2;");
				str.append(_mob.getModelo().getGfxID() + "^" + _mob.getModelo().getTalla() + ";");
				str.append(_mob.getGrado() + ";");
				str.append(_mob.getModelo().getColores().replace(",", ";") + ";");
				str.append("550,0,0,0;");
				str.append(getPDVMaxConBuff() + ";");
				str.append(_mob.getPA()).append(";");
				str.append(_mob.getPM()).append(";");
				str.append(_equipoBin + ";");
				str.append(_mob.getNivel() + ";");
				break;
			case 5:// recaudador
				Gremio gremio = MundoDofus.getGremio(_recaudador.getGremioID());
				str.append("-6;");
				str.append("6000^100;");
				str.append(gremio.getNivel() + ";");
				str.append(getPDVMaxConBuff() + ";");
				str.append(totalStats.getEfecto(111) + ";");
				str.append(totalStats.getEfecto(128) + ";");
				str.append(totalStats.getEfecto(214) + ";");
				str.append(totalStats.getEfecto(210) + ";");
				str.append(totalStats.getEfecto(213) + ";");
				str.append(totalStats.getEfecto(211) + ";");
				str.append(totalStats.getEfecto(212) + ";");
				str.append(totalStats.getEfecto(160) + ";");
				str.append(totalStats.getEfecto(161) + ";");
				str.append(_equipoBin);
				break;
			case 7:// prisma
				str.append("-2;");
				str.append((_prisma.getAlineacion() == 1 ? 8101 : 8100) + "^100;");
				str.append(_prisma.getNivel() + ";");
				str.append("-1;-1;-1;");
				str.append("0,0,0,0;");
				str.append(getPDVMaxConBuff() + ";");
				str.append(0 + ";");
				str.append(0 + ";");
				str.append(_equipoBin);
				break;
			case 10:// doble
				str.append(_doble.getClase(false) + ";");
				str.append(_doble.getGfxID() + "^" + _doble.getTalla() + ";");
				str.append(_doble.getSexo() + ";");
				str.append(_doble.getNivel() + ";");
				str.append(_doble.getAlineacion() + ",");
				str.append(_doble.getNivelAlineacion() + ",");
				str.append((_doble.estaMostrandoAlas() ? _doble.getNivelAlineacion() : "0") + ",");
				str.append((_doble.getID() + _doble.getNivel()) + "," + (_doble.getDeshonor() > 0 ? 1 : 0) + ";");
				str.append((_doble.getColor1() == -1 ? "-1" : Integer.toHexString(_doble.getColor1())) + ";");
				str.append((_doble.getColor2() == -1 ? "-1" : Integer.toHexString(_doble.getColor2())) + ";");
				str.append((_doble.getColor3() == -1 ? "-1" : Integer.toHexString(_doble.getColor3())) + ";");
				str.append(_doble.getStringAccesorios() + ";");
				str.append(getPDVConBuff() + ";");
				str.append(totalStats.getEfecto(111) + ";");
				str.append(totalStats.getEfecto(128) + ";");
				str.append(totalStats.getEfecto(214) + ";");
				str.append(totalStats.getEfecto(210) + ";");
				str.append(totalStats.getEfecto(213) + ";");
				str.append(totalStats.getEfecto(211) + ";");
				str.append(totalStats.getEfecto(212) + ";");
				str.append(totalStats.getEfecto(160) + ";");
				str.append(totalStats.getEfecto(161) + ";");
				str.append(_equipoBin + ";");
				if ((_doble.estaMontando()) && (_doble.getMontura() != null)) {
					str.append(_doble.getMontura().getStringColor(_doble.stringColorDueÑoPavo()));
				}
				str.append(";");
				break;
			}
			return str.toString();
		}

		void setEstado(int id, int estado) {
			_estados.remove(id);
			if (estado != 0) {
				_estados.put(id, estado);
			}
		}

		boolean tieneEstado(int id) {
			if (_estados.get(id) == null) {
				return false;
			}
			return _estados.get(id) != 0;
		}

		private void disminuirEstados() {
			Map<Integer, Integer> copia = new TreeMap<>();
			Map<Integer, Integer> estados = new TreeMap<>();
			estados.putAll(_estados);
			for (Entry<Integer, Integer> est : estados.entrySet()) {
				if (est.getKey() <= 0) {
					continue;
				}
				int nVal = est.getValue() - 1;
				if (nVal == 0) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(_pelea, 7, 950, getID() + "",
							getID() + "," + est.getKey() + ",0");
					continue;
				}
				copia.put(est.getKey(), nVal);
			}
			_estados.clear();
			_estados.putAll(copia);
		}

		public int getPDVMaxConBuff() {
			return _PDVMAX + getValorBuffPelea(125);
		}

		public int getPDVConBuff() {
			return _PDV + getValorBuffPelea(125);
		}

		public int getPDVMax() {
			return _PDVMAX;
		}

		public int getPDV() {
			return _PDV;
		}

		public int getPorcPDV() {
			int vitalidad = getBuffsStatsPelea().getEfecto(125);
			if (this._PDVMAX + vitalidad <= 0) {
				return 0;
			}
			float porc = (this._PDV + vitalidad) * 100 / (this._PDVMAX + vitalidad);
			porc = Math.max(0, porc);
			porc = Math.min(100, porc);
			return (int) porc;
		}

		public void restarPDV(int pdv) {
			_PDV -= pdv;
			if (_intocable && pdv > 0) {
				_pelea._retos.remove(17);
				_pelea._retos.put(17, 2);
				GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(_pelea, 17);
				for (Luchador luch : _pelea._inicioLucEquipo1) {
					luch._intocable = false;
				}
			}
			if (_contaminacion && pdv > 0) {
				_contaminado = true;
			}
		}

		public void setPDV(int pdv) {
			_PDV = pdv;
		}

		public void setPDVMAX(int pdv) {
			_PDVMAX = pdv;
		}

		int getValorBuffPelea(int id) {
			int valor = 0;
			Map<EfectoHechizo, Luchador> buffsPelea = new HashMap<>();
			buffsPelea.putAll(_buffsPelea);
			for (EfectoHechizo entry : buffsPelea.keySet()) {
				if (entry.getEfectoID() == id) {
					valor += entry.getValor();
				}
			}
			return valor;
		}

		private void aplicarBuffInicioTurno(Pelea pelea) {


			Map<EfectoHechizo, Luchador> buffs = new HashMap<>();
			buffs.putAll(_buffsPelea);
			for (int efectoID : CentroInfo.BUFF_INICIO_TURNO) {
				if (pelea._estadoPelea == 4) {
					break;
				}
				for (EfectoHechizo EH : buffs.keySet()) {
					if (pelea._estadoPelea == 4) {
						break;
					}
					if (EH.getEfectoID() == efectoID) {
						EH.aplicarBuffDeInicioTurno(pelea, this);
					}


				}
			}
		}

private void aplicarBuffPasivaInicioTurno(Pelea pelea) {


			Map<EfectoHechizo, Luchador> buffs = new HashMap<>();
			buffs.putAll(_buffsPelea);
			for (int efectoID : CentroInfo.BUFF_INICIO_TURNO_PASIVA) {
				if (pelea._estadoPelea == 4) {
					break;
				}
				for (EfectoHechizo EH : buffs.keySet()) {
					if (pelea._estadoPelea == 4) {
						break;
					}
					if (EH.getEfectoID() == efectoID) {
						EH.aplicarBuffDeInicioTurno(pelea, this);
					}

				}
			}
		}


		int getBuffValor(int id) {
			int value = 0;
			Map<EfectoHechizo, Luchador> buffs = new HashMap<>();
			buffs.putAll(_buffsPelea);
			for (EfectoHechizo entry : buffs.keySet()) {
				if (entry.getEfectoID() == id) {
					value += entry.getValor();
				}
			}
			return value;
		}

		public int getBuffValorHechizoID(int id) {
			int value = 0;
			for (EfectoHechizo entry : _buffsPelea.keySet()) {
				if (entry.getHechizoID() == id) {
					value += entry.getValor();
				}
			}
			return value;
		}

		public EfectoHechizo getBuff(int id) {
			for (EfectoHechizo entry : _buffsPelea.keySet()) {
				if (entry == null) {
					continue;
				}
				if ((entry.getEfectoID() == id)) {
					return entry;
				}
			}
			return null;
		}

		public boolean tieneBuff(int id) {
			for (EfectoHechizo entry : _buffsPelea.keySet()) {
				if (entry == null) {
					continue;
				}
				if ((entry.getEfectoID() == id)) {
					return true;
				}
			}
			return false;
		}

		public boolean tieneBuffHechizoID(int id) {
			for (EfectoHechizo entry : _buffsPelea.keySet()) {
				if (entry == null) {
					continue;
				}
				if (entry.getHechizoID() == id) {
					return true;
				}
			}
			return false;
		}

		private void actualizarBuffsPelea() {
			Map<EfectoHechizo, Luchador> efectos = new HashMap<>();
			disminuirEstados();
			Map<EfectoHechizo, Luchador> buffsPelea = new HashMap<>();
			buffsPelea.putAll(_buffsPelea);
			for (Entry<EfectoHechizo, Luchador> buff : buffsPelea.entrySet()) {
				if (buff.getKey().disminuirDuracion() > 0) {
					efectos.put(buff.getKey(), buff.getValue());
				} else {
					switch (buff.getKey().getEfectoID()) {
					case 108:
					case 125:
						int valor = buff.getKey().getValor();
						if (buff.getKey().getHechizoID() == 441) {
							_PDVMAX = (_PDVMAX - valor);
							int pdv = 0;
							if (_PDV - valor <= 0) {
								pdv = 0;
								_pelea.agregarAMuertos(this, this, "");
								_pelea.acaboPelea("buffspelea");
								continue;
							} else {
								pdv = (_PDV - valor);
							}
							_PDV = pdv;
						}
						break;
					case 150:
						GestorSalida.ENVIAR_GA_ACCION_PELEA(_pelea, 7, 150, buff.getKey().getLanzador().getID() + "",
								getID() + ",0");
						break;
					case 950:
						String args = buff.getKey().getArgs();
						int id = -1;
						try {
							id = Integer.parseInt(args.split(";")[2]);
						} catch (Exception e) {
						}
						if (id == -1) {
							return;
						}
						setEstado(id, 0);
						GestorSalida.ENVIAR_GA_ACCION_PELEA(_pelea, 7, 950, buff.getKey().getLanzador().getID() + "",
								getID() + "," + id + ",0");
						break;
					}
				}
			}
			_buffsPelea.clear();
			_buffsPelea.putAll(efectos);
		}

		public void addPDV(int max) {
			_PDVMAX = (_PDVMAX + max);
			_PDV = (_PDV + max);
		}

		String addBuff(int id, int val, int duration, int turns, boolean desbufeable, int hechizoID, String args,
				Luchador caster, boolean isPoison, boolean evitaenvios) {
			switch (hechizoID) {
			case 20:
			case 127:
			case 89:
			case 126:
			case 115:
			case 192:
			case 4:
			case 1:
			case 6:
			case 14:
			case 18:
			case 7:
			case 284:
			case 197:
			case 704:
			case 168:
			case 45:
			case 159:
			case 171:
			case 167:
			case 511:
			case 513:
				desbufeable = true;
				break;
			case 431:
			case 433:
			case 437:
			case 443:
			case 5:
			case 441:
			case 695:
			case 99:
				desbufeable = false;
				break;
			}
			if (id == 606 || id == 607 || id == 608 || id == 609 || id == 611 || id == 125 || id == 114
					|| (hechizoID == 197
							&& (id == 149 || id == 169 || id == 183 || id == 184 || id == 168 || id == 108))) {
				desbufeable = true;
			}
			if (evitaenvios) {
				if (id == 781) {
					if (caster.getID() == this.getID()) {
						return "";
					}
					_buffsPelea.put(new EfectoHechizo(id, val, (_puedeJugar ? duration + 1 : duration), turns,
							desbufeable, caster, args, hechizoID, isPoison), caster);
				} else {
					_buffsPelea.put(new EfectoHechizo(id, val, (_puedeJugar ? duration + 1 : duration), turns,
							desbufeable, caster, args, hechizoID, isPoison), caster);
				}
			}
			StringBuilder packet = new StringBuilder("");
			switch (id) {
			case 6:// Renvoie de sort
			case 106:// Renvio de hechizo
				packet.append("GIE" + id + ";" + getID() + ";" + "-1" + ";" + val + ";" + "10" + ";" + "" + ";"
						+ duration + ";" + hechizoID);
				break;
			case 950:// Estados
				packet.append("GIE" + id + ";" + getID() + ";" + "-1" + ";" + val + ";" + "" + ";" + "" + ";" + duration
						+ ";" + hechizoID);
				break;
			case 79:// Suerte de zurcarak
				val = Integer.parseInt(args.split(";")[0]);
				String valMax = args.split(";")[1];
				String suerte = args.split(";")[2];
				packet.append("GIE" + id + ";" + getID() + ";" + val + ";" + valMax + ";" + suerte + ";" + "" + ";"
						+ duration + ";" + hechizoID);
				break;
			case 606:
			case 607:
			case 608:
			case 609:
			case 611:
				// de X sur Y tours
				String jet = args.split(";")[5];
				int min = Formulas.getMinJet(jet);
				int max = Formulas.getMaxJet(jet);
				packet.append("GIE" + id + ";" + getID() + ";" + min + ";" + max + ";" + max + ";" + "" + ";" + duration
						+ ";" + hechizoID);
				break;
			case 788:// Pone de manifiesto mensaje el tiempo de concluido Sacrificio de X sobre
				// Y
				// turnos
				val = Integer.parseInt(args.split(";")[1]);
				String valMax2 = args.split(";")[2];
				if (Integer.parseInt(args.split(";")[0]) == 108) {
					return "";
				}
				packet.append("GIE" + id + ";" + getID() + ";" + val + ";" + val + ";" + valMax2 + ";" + "" + ";"
						+ duration + ";" + hechizoID);
				break;
			case 96: // daÑos agua
			case 97: // daÑos tierra
			case 98: // daÑos aire
			case 99: // daÑos fuego
			case 100: // daÑos neutral
			case 107: // reenvio de daÑos
			case 108: // curar
			case 165: // Aumenta los daÑos % de armas
			case 781: // minimiza los efectos aleatorios
			case 782: // maximiza los efectos aleatorios
				val = Integer.parseInt(args.split(";")[0]);
				String valMax1 = args.split(";")[1];
				if (valMax1.equals("-1") || hechizoID == 82 || hechizoID == 94 || hechizoID == 132) {
					packet.append("GIE" + id + ";" + getID() + ";" + val + ";" + "" + ";" + "" + ";" + "" + ";"
							+ duration + ";" + hechizoID);
				} else if (valMax1.compareTo("-1") != 0) {
					packet.append("GIE" + id + ";" + getID() + ";" + val + ";" + valMax1 + ";" + "" + ";" + "" + ";"
							+ duration + ";" + hechizoID);
				}
				break;
			default:
				String valMax1x = args.split(";")[1];
				if (valMax1x.equals("-1")) {
					valMax1x = "";
				}
				switch (id) {
				case 169:
				case 168:
				case 127:
				case 128:
				case 101:
				case 112:
				case 78:
				case 77:
				case 120:
				case 138:
				case 125:
				case 122:
				case 220:
					valMax1x = "";
					break;
				}
				packet.append("GIE" + id + ";" + getID() + ";" + val + ";" + valMax1x + ";" + "" + ";" + "" + ";"
						+ duration + ";" + hechizoID);
				break;
			}
			if (evitaenvios) {
				GestorSalida.TODO_INICIO_PELEA(_pelea, 7, packet.toString());
			} else {
				return packet.toString();
			}
			return "";
		}

		private int iniciativa = 0;

		public int getIniciativa() {
			if (_tipo == 1 || _tipo == 0) {
				if (iniciativa == 0) {
					iniciativa = _perso.getIniciativa();
					return iniciativa;
				} else {
					return iniciativa;
				}
			}
			if (_tipo == 2) {
				if (iniciativa == 0) {
					iniciativa = _mob.getIniciativa();
					return iniciativa;
				} else {
					return iniciativa;
				}
			}
			if (_tipo == 5) {
				if (iniciativa == 0) {
					iniciativa = MundoDofus.getGremio(_recaudador.getGremioID()).getNivel();
					return iniciativa;
				} else {
					return iniciativa;
				}
			}
			if (_tipo == 7) {
				return 0;
			}
			if (_tipo == 10) {
				if (iniciativa == 0) {
					iniciativa = _doble.getIniciativa();
					return iniciativa;
				} else {
					return iniciativa;
				}
			}
			return 0;
		}

		public int getNivel() {
			if (_tipo == 1 || _tipo == 0) {
				return _perso.getNivel();
			}
			if (_tipo == 2) {
				return _mob.getNivel();
			}
			if (_tipo == 5) {
				return MundoDofus.getGremio(_recaudador.getGremioID()).getNivel();
			}
			if (_tipo == 7) {
				return _prisma.getNivel();
			}
			if (_tipo == 10) {
				return _doble.getNivel();
			}
			return 0;
		}

		private String xpString(String str) {
			if (_perso != null) {
				int max = _perso.getNivel() + 1;
				if (max > Emu.MAX_NIVEL) {
					max = Emu.MAX_NIVEL;
				}
				return MundoDofus.getExpNivel(_perso.getNivel())._personaje + str + _perso.getExperiencia() + str
						+ MundoDofus.getExpNivel(max)._personaje;
			}
			return "0" + str + "0" + str + "0";
		}

		public String getNombreLuchador() {
			if (_tipo == 1) {
				return _perso.getNombre();
			}
			if (_tipo == 2) {
				return _mob.getModelo().getID() + "";
			}
			if (_tipo == 5) {
				return (_recaudador.getN1() + "," + _recaudador.getN2());
			}
			if (_tipo == 7) {
				return (_prisma.getAlineacion() == 1 ? 1111 : 1112) + "";
			}
			if (_tipo == 10) {
				return _doble.getNombre();
			}
			return "";
		}

		public MobGrado getMob() {
			if (_tipo == 2) {
				return _mob;
			}
			return null;
		}

		public int getEquipoBin() {
			return _equipoBin;
		}

		public int getParamEquipoAliado() {
			return _pelea.getParamEquipo(_id);
		}

		public int getParamEquipoEnemigo() {
			return _pelea.getIDEquipoEnemigo(_id);
		}

		public Pelea getPelea() {
			return _pelea;
		}

		public boolean puedeJugar() {
			return _puedeJugar;
		}

		public void setPuedeJugar(boolean b) {
			_puedeJugar = b;
		}

		public int getPAConBuff() {
			return getTotalStatsConBuff().getEfecto(CentroInfo.STATS_ADD_PA);
		}

		public int getPMConBuff() {
			return getTotalStatsConBuff().getEfecto(CentroInfo.STATS_ADD_PM);
		}

		public int getTempPA(Pelea pelea) {
			return pelea._tempLuchadorPA;
		}

		public int getTempPM(Pelea pelea) {
			return pelea._tempLuchadorPM;
		}

		public void addTempPM(Pelea pelea, int pm) {
			pelea._tempLuchadorPM += pm;
		}

		public void addTempPA(Pelea pelea, int pa) {
			pelea._tempLuchadorPA += pa;
		}

		public void setInvocador(Luchador invocador) {
			_invocador = invocador;
		}

		public Luchador getInvocador() {
			return _invocador;
		}

		void aumentarInvocaciones() {
			_nroInvocaciones++;
		}

		void aumentarInvosTotales() {
			_invosquetuvo++;
		}

		public int getInvosTotales() {
			return _invosquetuvo;
		}

		public int getNroInvocaciones() {
			return _nroInvocaciones;
		}

		public boolean esInvocacion() {
			return (_invocador != null);
		}

		public boolean esRecaudador() {
			return (_recaudador != null);
		}

		public boolean esPrisma() {
			return (_prisma != null);
		}

		public boolean esDoble() {
			return (_doble != null);
		}

		synchronized String desbuffear(EfectoHechizo efecto) {
			StringBuilder strx = new StringBuilder();
			strx.append(this.getID() + "," + this.getID() + ";");

			StringBuilder hechizoid = new StringBuilder();
			StringBuilder efectoid = new StringBuilder();
			Map<EfectoHechizo, Luchador> nuevosBuffs = new HashMap<>();
			for (Entry<EfectoHechizo, Luchador> EH : _buffsPelea.entrySet()) {
				if (EH.getKey() != efecto) {
					nuevosBuffs.put(EH.getKey(), EH.getValue());
					continue;
				}
				if (hechizoid.toString().equals("")) {
					hechizoid.append(EH.getKey().getHechizoID());
				} else {
					hechizoid.append("," + EH.getKey().getHechizoID());
				}

				if (efectoid.toString().equals("")) {
					efectoid.append(EH.getKey().getEfectoID());
				} else {
					efectoid.append("," + EH.getKey().getEfectoID());
				}
				switch (EH.getKey().getEfectoID()) {
				case CentroInfo.STATS_ADD_PA:
				case CentroInfo.STATS_ADD_PA2:
					GestorSalida.ENVIAR_GA_ACCION_PELEA(_pelea, 7, 101, getID() + "",
							getID() + ",-" + EH.getKey().getValor());
					break;
				case CentroInfo.STATS_ADD_PM:
				case CentroInfo.STATS_ADD_PM2:
					GestorSalida.ENVIAR_GA_ACCION_PELEA(_pelea, 7, 127, getID() + "",
							getID() + ",-" + EH.getKey().getValor());
					break;
				case 150:
					GestorSalida.ENVIAR_GA_ACCION_PELEA(_pelea, 7, 150, getID() + "", getID() + ",0");
					GestorSalida.ENVIAR_GIC_APARECER_LUCHADORES_INVISIBLES(_pelea, 7, this);
					break;
				case 950:
					String args = EH.getKey().getArgs();
					int id = -1;
					try {
						id = Integer.parseInt(args.split(";")[2]);
					} catch (Exception e) {
					}
					if (id == -1) {
						return "";
					}
					setEstado(id, 0);
					GestorSalida.ENVIAR_GA_ACCION_PELEA(_pelea, 7, 950, EH.getValue().getID() + "",
							this.getID() + "," + id + ",0");
					break;
				}
			}
			strx.append(hechizoid + "-" + efectoid);
			_buffsPelea.clear();
			_buffsPelea.putAll(nuevosBuffs);
			return "#ER" + strx;
		}

		synchronized void desbuffear(Luchador lanzador) {
			StringBuilder strx = new StringBuilder();
			strx.append(lanzador.getID() + "," + this.getID() + ";");
			StringBuilder hechizoid = new StringBuilder();
			StringBuilder efectoid = new StringBuilder();
			Map<EfectoHechizo, Luchador> nuevosBuffs = new HashMap<>();
			for (Entry<EfectoHechizo, Luchador> EH : _buffsPelea.entrySet()) {
				if (!EH.getKey().esDesbufeable()) {
					nuevosBuffs.put(EH.getKey(), EH.getValue());
					continue;
				} else {
					if (hechizoid.toString().equals("")) {
						hechizoid.append(EH.getKey().getHechizoID());
					} else {
						hechizoid.append("," + EH.getKey().getHechizoID());
					}

					if (efectoid.toString().equals("")) {
						efectoid.append(EH.getKey().getEfectoID());
					} else {
						efectoid.append("," + EH.getKey().getEfectoID());
					}
				}
				switch (EH.getKey().getEfectoID()) {
				case CentroInfo.STATS_ADD_PA:
				case CentroInfo.STATS_ADD_PA2:
					GestorSalida.ENVIAR_GA_ACCION_PELEA(_pelea, 7, 101, getID() + "",
							getID() + ",-" + EH.getKey().getValor());
					break;
				case CentroInfo.STATS_ADD_PM:
				case CentroInfo.STATS_ADD_PM2:
					GestorSalida.ENVIAR_GA_ACCION_PELEA(_pelea, 7, 127, getID() + "",
							getID() + ",-" + EH.getKey().getValor());
					break;
				case 150:
					GestorSalida.ENVIAR_GA_ACCION_PELEA(_pelea, 7, 150, getID() + "", getID() + ",0");
					GestorSalida.ENVIAR_GIC_APARECER_LUCHADORES_INVISIBLES(_pelea, 7, this);
					break;
				case 950:
					String args = EH.getKey().getArgs();
					int id = -1;
					try {
						id = Integer.parseInt(args.split(";")[2]);
					} catch (Exception e) {
					}
					if (id == -1) {
						return;
					}
					setEstado(id, 0);
					GestorSalida.ENVIAR_GA_ACCION_PELEA(_pelea, 7, 950, EH.getKey().getLanzador().getID() + "",
							this.getID() + "," + id + ",0");
					break;
				}
			}
			strx.append(hechizoid + "-" + efectoid);
			GestorSalida.ENVIAR_GAMEACTION_A_PELEA(this.getPelea(), 7, "#EF" + strx);
			_buffsPelea.clear();
			_buffsPelea.putAll(nuevosBuffs);
			if (_perso != null && !_estaRetirado) {
				GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
			}
		}

		private void fullPDV() {
			_PDV = _PDVMAX;
		}

		public boolean esFullPdv() {
			return _PDV == _PDVMAX;
		}

		void unHide(int spellid) {
			if (spellid != -1) {
				switch (spellid) {
				case 65:
				case 69:
				case 66:
				case 71:
				case 73:
				case 77:
				case 79:
				case 80:
				case 181:
				case 196:
				case 200:
				case 219:
					return;
				}
			}
			Map<EfectoHechizo, Luchador> buffs = new HashMap<>();
			buffs.putAll(getBuffPelea());
			for (EfectoHechizo SE : buffs.keySet()) {
				if (SE.getEfectoID() == 150) {
					getBuffPelea().remove(SE);
				}
			}
			GestorSalida.ENVIAR_GA_ACCION_PELEA(_pelea, 7, 150, getID() + "", getID() + ",0");
			GestorSalida.ENVIAR_GIC_APARECER_LUCHADORES_INVISIBLES(_pelea, 7, this);
		}

		public int getPDVMAXFueraPelea() {
			if (_perso != null) {
				return _perso.getPDVMAX();
			}
			if (_mob != null) {
				return _mob.getPDVMAX();
			}
			return 0;
		}

		public int getGfxDefecto() {
			if (_perso != null) {
				return _perso.getGfxID();
			}
			if (_mob != null) {
				return _mob.getModelo().getGfxID();
			}
			return 0;
		}

		public long getXpGive() {
			if (_mob != null) {
				return _mob.getBaseXp();
			}
			return 0;
		}

		public int getPdvMaxOutFight() {
			if (_perso != null) {
				return _perso.getPDVMAX();
			}
			if (_mob != null) {
				return _mob.getPDVMAX();
			}
			return 0;
		}
	}

	static class Glifo {
		private Luchador _lanzador;
		private Celda _celda;
		private byte _tamaÑo;
		private int _hechizos;
		private StatsHechizos _glifoHechizo;
		private byte _duracion;
		private Pelea _pelea;
		private int _color;

		Glifo(Pelea pelea, Luchador lanzador, Celda celda, byte tamaÑo, StatsHechizos glifoHechizo, byte duracion,
				int hechizo) {
			_pelea = pelea;
			_lanzador = lanzador;
			_celda = celda;
			_hechizos = hechizo;
			_tamaÑo = tamaÑo;
			_glifoHechizo = glifoHechizo;
			_duracion = duracion;
			_color = CentroInfo.getColorGlifo(hechizo);
		}

		public Celda getCelda() {
			return _celda;
		}

		public byte getTamaÑo() {
			return _tamaÑo;
		}

		public Luchador getLanzador() {
			return _lanzador;
		}

		public byte getDuracion() {
			return _duracion;
		}

		private int disminuirDuracion() {
			_duracion--;
			return _duracion;
		}

		private void activarGlifo(Luchador glifeado) {
			String str = _hechizos + "," + _celda.getID() + ",0,1,1," + _lanzador.getID();
			GestorSalida.ENVIAR_GA_ACCION_PELEA(_pelea, 7, 307, glifeado.getID() + "", str);
			_glifoHechizo.aplicaHechizoAPelea(_pelea, _lanzador, glifeado.getCeldaPelea(), false, true);
		}

		private void desaparecer() {
			GestorSalida.ENVIAR_GDZ_ACTUALIZA_ZONA_EN_PELEA(_pelea, 7, "-", _celda.getID(), _tamaÑo, _color);
			GestorSalida.ENVIAR_GDC_ACTUALIZAR_CELDA_EN_PELEA(_pelea, 7, _celda.getID());
		}

		public int getColor() {
			return _color;
		}
	}
}
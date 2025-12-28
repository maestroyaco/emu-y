package variables;

import java.util.Calendar;
import java.util.Date;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import estaticos.Emu;
import estaticos.Formulas;
import estaticos.GestorSalida;
import estaticos.MundoDofus;
import servidor.Colores;
import variables.MobModelo.GrupoMobs;
import variables.Objeto.ObjetoModelo;

public class Invasion {
	public static Mapa mapainv = null;// ok
	private static int cellid = 280;// ok
	private static String mobInvasion = "";// ok
	static String posicion = "";
	public static Timer Timerminutos = null;
	public static int cuantosmin = 15;
	public static int HoraMaldicion = 8;
	public static int HoraBufo = 12;





	@SuppressWarnings("static-access")
	public static void iniciaInvasion() {
		Purgatimer();
		Purgatimer2();
		borraMaldicionesYBuf();
		borraAnteriores();
		getRandomMapa();// coge mapa,celda,coordenadas
		escogeMob();// coge mob aleatorio
		if (agregaMob(mobInvasion)) {
			GestorSalida.ENVIAR_MENSAJE_A_TODOS_CHAT_COLOR("Un <b>MEGA</b> ha invadido la zona de Astrub, estÁ en <b>["
					+ posicion + "]</b> si no lo matÁis en los prÓximos <b>" + cuantosmin
					+ " minutos</b>, <b>todas las personas</b> sufrir�n una <b>maldiciÓn</b> que <b>bajarÁ las recompensas</b> y los rates del servidor en un <b>50%</b> durante "
					+ HoraMaldicion + "h.", Colores.ROJO);


			//BOT DISCORD EVENTOS
			EjecutarBotDiscord.ejecutarBotDiscord("Un MEGA ha invadido la zona de Astrub, estÁ en ["
					+ posicion + "] si no lo matÁis en los prÓximos " + cuantosmin
					+ " minutos, todas las personas sufrir�n una maldiciÓn que bajarÁ las recompensas y los rates del servidor en un 50% durante "
					+ HoraMaldicion + "h.", "Evento Mega");



			/*MiEvento miEvento = new MiEvento(mensaje, titulo);
            BotDiscord bot = new BotDiscord(miEvento);
            try {
            bot.iniciarBot();
            } catch (LoginException e) {
                e.printStackTrace();
            }*/

			CompruebaMatado();
			timerMinutos();
		}
	}

	private static void timerMinutos() {
		try {
			if (Timerminutos == null) {
				Timerminutos = new Timer();
			}
			TimerTask lot = new TimerTask() {
				@Override
				public void run() {
					if (Emu.enPeleaMega) {
						this.cancel();
						Purgatimer();
						Purgatimer2();
						return;
					} else {
						if (mapainv != null) {
							aplicaMaldicion();
							this.cancel();
							Purgatimer();
							Purgatimer2();
						} else {
							this.cancel();
							Purgatimer();
							Purgatimer2();
						}
					}
				}
			};
			Timerminutos.schedule(lot, 900000, 900000);// 15 minutos
		} catch (Exception e) {
			Timerminutos.purge();
			System.out.println("BUG AL CERRAR UN TIMER DE INVASION2");
		}
	}

	private static void borraAnteriores() {
		borrarMobs();
		mapainv = null;
		cellid = 280;
		mobInvasion = "";
		posicion = "";
		Emu.enPeleaMega = false;
	}

	private static void borrarMobs() {
		if (mapainv != null) {
			mapainv.borrarTodosMobs();
			mapainv.borrarTodosMobsFix();
		}
	}

	private static void escogeMob() {
		mobInvasion = getRandomMob();
	}

	private static void getRandomMapa() {// TODO:
		String idmapa = "";
		int rnd = Formulas.getRandomValor(1, 14);
		switch (rnd) {
		case 1:
			idmapa = "7608,368,8,-18";
			break;
		case 2:
			idmapa = "7363,315,1,-19";
			break;
		case 3:
			idmapa = "7366,384,1,-16";
			break;
		case 4:
			idmapa = "7317,242,-2,-17";
			break;
		case 5:
			idmapa = "7299,222,-3,-19";
			break;
		case 6:
			idmapa = "7312,196,-2,-22";
			break;
		case 7:
			idmapa = "7389,283,3,-25";
			break;
		case 8:
			idmapa = "7424,324,5,-22";
			break;
		case 9:
			idmapa = "7410,312,4,-20";
			break;
		case 10:
			idmapa = "7444,294,6,-18";
			break;
		case 11:
			idmapa = "7464,193,7,-14";
			break;
		case 12:
			idmapa = "7433,251,5,-13";
			break;
		case 13:
			idmapa = "7385,166,2,-13";
			break;
		case 14:
			idmapa = "7318,279,-2,-16";
			break;
		default:
			idmapa = "7608,368,8,-18";
		}
		if (idmapa.isEmpty()) {
			return;
		}
		Mapa mapx = MundoDofus.getMapa(Short.parseShort(idmapa.split(",")[0]));
		if (mapx == null) {
			return;
		}
		cellid = Integer.parseInt(idmapa.split(",")[1]);
		posicion = idmapa.split(",")[2] + "," + idmapa.split(",")[3];
		mapainv = mapx;
	}

	private static String getRandomMob() {
		int rnd = Formulas.getRandomValor(1, 10);
		switch (rnd) {
		case 1:
			return "10020,600,600";
		case 2:
			return "10021,600,600";
		case 3:
			return "10022,400,400";
		case 4:
			return "10023,1980,1980";
		case 5:
			return "10024,1200,1200";
		case 6:
			return "40227,321,321";
		case 7:
			return "40225,220,220";
		case 8:
			return "40203,160,160";
		case 9:
			return "40195,260,260";
		case 10:
			return "40194,240,240";
		default:
			return "10020,600,600";
		}
	}

	private static boolean agregaMob(String mob) {
		if (mapainv == null) {
			return false;
		}
		if (mob.isEmpty()) {
			return true;
		}
		borrarMobs();
		mapainv.addGrupoDeUnaPelea(cellid, mob);
		return true;
	}

	private static int getRandomItem() { // moob mega
		int rand = Formulas.getRandomValor(1, 6);
		switch (rand) {
		case 1:
			return 70500;
		case 2:
			return 70501;
		case 3:
			return 36030;
		case 4:
			return 36031;
		case 5:
			return 980107;
		case 6:
			return 36019;
		default:
			return 70500;
		}
	}

	private static int getRandomItem2() {
		int rand = Formulas.getRandomValor(1, 6);
		switch (rand) { //moob angelical
		case 1:
			return 40008;
		case 2:
			return 40010;
		case 3:
			return 50205;
		case 4:
			return 70284;
		case 5:
			return 70271;
		case 6:
			return 50033;
		default:
			return 40008;
		}
	}

	private static int getRandomItem3() {
		int rand = Formulas.getRandomValor(1, 5);
		switch (rand) { //moob demonio
		case 1:
			return 40009;
		case 2:
			return 40011;
		case 3:
			return 70281;
		case 4:
			return 70287;
		case 5:
			return 50034;
		default:
			return 40011;
		}
	}

	static void darPremio(Personaje perso, boolean angel) {
		int random = Formulas.getRandomValor(1, 4);
		if (random == 2) {
			ObjetoModelo OM = null;
			if (!angel) {
				OM = MundoDofus.getObjModelo(getRandomItem());
			} else {
				if (MundoDofus.angel) {
					OM = MundoDofus.getObjModelo(getRandomItem2());// angel
				}
				else {
					OM = MundoDofus.getObjModelo(getRandomItem3());// demonio
				}
			}
			if (OM == null) {
				return;
			}
			Objeto obj = OM.crearObjDesdeModelo(1, true);
			if (!perso.addObjetoSimilar(obj, true, -1)) {
				MundoDofus.addObjeto(obj, true);
				perso.addObjetoPut(obj);
				if (perso.enLinea()) {
					GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(perso, obj);
				}
			}
			if (!angel) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "Acabas de ganar un Item Legendario por derrotar a un Mega",
						Colores.VERDE);
			} else {
				if (MundoDofus.angel) {
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso,
							"Acabas de ganar un Item Legendario por derrotar a a un Ángel", Colores.VERDE);
				} else {
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso,
							"Acabas de ganar un Item Legendario por derrotar a a un Demonio", Colores.VERDE);
				}
			}
		} else {
			if (!angel) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso,
						"No has podido dropear ning�n Item Legendario con este Mega, prueba suerte la pr�xima vez.",
						Colores.ROJO);
			} else {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso,
						"No has podido dropear ning�n Item Legendario en este combate, prueba suerte la pr�xima vez.",
						Colores.ROJO);
			}
		}
	}

	private static Timer TimerInvasion = null;

	private static void Purgatimer() {
		if (TimerInvasion != null) {
			TimerInvasion.cancel();
			TimerInvasion.purge();
			TimerInvasion = null;
		}
	}

	private static void Purgatimer2() {
		if (Timerminutos != null) {
			Timerminutos.cancel();
			Timerminutos.purge();
			Timerminutos = null;
		}
	}

	private static void borraMaldicionesYBuf() {
		Emu.Maldicion = false;
		Emu.Bufo = false;
		for (Personaje perso : MundoDofus.getPJsEnLinea()) {
			if (perso == null) {
				continue;
			}
			if (perso.getObjPosicion(23) != null) {
				perso.deleteobjBoost(23);
			}
		}
	}

	static void aplicaMaldicion() {
		Emu.enPeleaMega = false;
		Emu.Maldicion = true;
		Emu.Bufo = false;
		GestorSalida.ENVIAR_MENSAJE_A_TODOS_CHAT_COLOR(
				"NO hab�is podido derrotar al <b>MEGA</b> que estaba en <b>[" + posicion
						+ "]</b> y se ha aplicado una maldiciÓn al servidor durante " + HoraMaldicion + "h.",
				Colores.ROJO);
		//BOT DISCORD EVENTOS
		EjecutarBotDiscord.ejecutarBotDiscord("NO hab�is podido derrotar al MEGA que estaba en [" + posicion
				+ "] y se ha aplicado una maldiciÓn al servidor durante " + HoraMaldicion + "h.", "Evento Mega Fallido");

		borraAnteriores();
		// aparece otro en 5h
		setTiempo(HoraMaldicion);
		// aplica bufos
		for (Personaje perso : MundoDofus.getPJsEnLinea()) {
			if (perso.getObjPosicion(23) == null) {
				perso.objBoost(70115);
			} else {
				if (perso.getObjPosicion(23) != null) {
					if (perso.getObjPosicion(23).getModelo().getID() != 70115) {
						perso.deleteobjBoost(23);
						perso.objBoost(70115);
					}
				}
			}
		}
	}

	private static void setTiempo(int tiempo) {
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.HOUR, tiempo);
		Date expirationDate = cal.getTime();
		String exp = String.valueOf(expirationDate);
		Emu.tiempoInvasion = exp;
	}

	static void MurioMega() {
		Emu.enPeleaMega = false;
		Emu.Maldicion = false;
		Emu.Bufo = true;
		GestorSalida.ENVIAR_MENSAJE_A_TODOS_CHAT_COLOR("El monstruo <b>MEGA</b> que estaba en <b>[" + posicion
				+ "]</b> ha muerto y el servidor estar� en calma durante " + HoraBufo + "h.", Colores.VERDE);

		//BOT DISCORD EVENTOS
				EjecutarBotDiscord.ejecutarBotDiscord("El monstruo MEGA que estaba en [" + posicion
						+ "] ha muerto y el servidor estar� en calma durante " + HoraBufo + "h.", "Evento Mega Exitoso");

		// 12h de calma
		borraAnteriores();
		setTiempo(HoraBufo);
		for (Personaje perso : MundoDofus.getPJsEnLinea()) {
			if (perso.getObjPosicion(23) == null) {
				perso.objBoost(70116);
			} else {
				if (perso.getObjPosicion(23) != null) {
					if (perso.getObjPosicion(23).getModelo().getID() != 70116) {
						perso.deleteobjBoost(23);
						perso.objBoost(70116);
					}
				}
			}
		}
	}

	private static void empiezaPeleaMEGA() {
		Emu.enPeleaMega = true;
		GestorSalida.ENVIAR_MENSAJE_A_TODOS_CHAT_COLOR(
				"Han desafiado al <b>MEGA</b> de <b>[" + posicion
						+ "]</b>, si no lo consegu�s matar, la <b>maldiciÓn</b> se activar� para todos los jugadores.",
				Colores.AZUL);
		for (Entry<Integer, Pelea> id : mapainv.getPeleas().entrySet()) {
			if (id.getValue().getTipoPelea() == 4) {
				Pelea pel = id.getValue();
				pel.peleainvasion = true;

			}
		}
	}

	private static void CompruebaMatado() {
		try {
			if (TimerInvasion == null) {
				TimerInvasion = new Timer();
			}
			TimerTask lot = new TimerTask() {
				int tiempo = 0;
				boolean empezomega = false;
				boolean sigueenmapa = false;

				@Override
				public void run() {
					if (mapainv != null) {
						if (!empezomega) {
							if (mapainv.getPeleas().size() > 0) {
								for (Entry<Integer, Pelea> id : mapainv.getPeleas().entrySet()) {
									if (id.getValue().getTipoPelea() == 4) {
										empezomega = true;
									}
								}
							}
							if (mapainv.getMobGroups().size() > 0) {
								for (Entry<Integer, GrupoMobs> id : mapainv.getMobGroups().entrySet()) {
									if (mobInvasion.equals(id.getValue().getStrGrupoMob())) {
										sigueenmapa = true;
										break;
									}
								}
							} else {
								sigueenmapa = false;
							}
						}
						if (sigueenmapa) { // cuenta el tiempo
							tiempo += 1;
							if (tiempo >= 900) { // no lo consiguen
								aplicaMaldicion();
								this.cancel();
								Purgatimer();
							}
						} else if (!sigueenmapa && empezomega) {// PELEA EMPEZADA
							empiezaPeleaMEGA();
							this.cancel();
							Purgatimer();
						} else {// EMPIEZA LA PELEA, SOLO ESTADO POSICIONAMIENTO
							this.cancel();
							Purgatimer();
						}
					} else {
						this.cancel();
						Purgatimer();
					}
				}
			};
			TimerInvasion.schedule(lot, 1000, 1000);
		} catch (Exception e) {
			TimerInvasion.purge();
			System.out.println("BUG AL CERRAR UN TIMER DE INVASION");
		}
	}
}
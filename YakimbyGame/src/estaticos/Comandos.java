package estaticos;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map.Entry;
import java.util.Random;

import estaticos.MundoDofus.ItemSet;
import servidor.EntradaPersonaje;
import servidor.Estaticos;
import servidor.ServidorPersonaje;
import variables.Accion;
import variables.Cuenta;
import variables.Dragopavo;
import variables.Invasion;
import variables.Mapa;
import variables.Mapa.Cercado;
import variables.MobModelo;
import variables.MobModelo.GrupoMobs;
import variables.NPCModelo;
import variables.NPCModelo.NPC;
import variables.Objeto;
import variables.Objeto.ObjetoModelo;
import variables.Oficio.StatsOficio;
import variables.Pelea.Luchador;
import variables.Personaje;
import variables.Recaudador;

public class Comandos {
	private Cuenta _cuenta;
	private Personaje _perso;
	private PrintWriter _out;

	public Comandos(Personaje pj) {
		try {
			_cuenta = pj.getCuenta();
			_perso = pj;
			_out = _cuenta.getEntradaPersonaje().getOut();
		} catch (NullPointerException e) {
		}
	}

	public void consolaComando(String packet) {
		String msg = packet.substring(2);
		String[] infos = msg.split(" ");
		if (infos.length == 0) {
			return;
		}
		String comamdo = infos[0];
		if (_cuenta.getRango() == 1) {
			GM_lvl_1(comamdo, infos, msg);
		} else if (_cuenta.getRango() == 2) {
			GM_lvl_2(comamdo, infos, msg);
		} else if (_cuenta.getRango() == 3) {
			GM_lvl_3(comamdo, infos, msg);
		} else if (_cuenta.getRango() == 4) {
			GM_lvl_4(comamdo, infos, msg);
		} else if (_cuenta.getRango() >= 5) {
			GM_lvl_5(comamdo, infos, msg);
		} else {
			String ipBaneada = _perso.getCuenta().getActualIP();
			if (!CentroInfo.compararConIPBaneadas(ipBaneada)) {
				GestorSQL.SELECT_BANIP(ipBaneada);
				CentroInfo.BAN_IP.add(ipBaneada);
				_perso.getCuenta().setBaneado(true);
				if (GestorSQL.AGREGAR_BANIP(ipBaneada)) {
					System.out.println("Se bane� a " + _perso.getNombre() + " por intentar acceder a la consola");
				}
				if (_perso.enLinea()) {
					_perso.getCuenta().getEntradaPersonaje().salir(false);
				}
			}
			return;
		}
		GestorSQL.AGREGAR_COMANDO_GM(_perso.getNombre(), packet);
	}

	private void GM_lvl_1(String comando, String[] infos, String mensaje) {
		if (_cuenta.getRango() < 1) {
			return;
		}
		if (comando.equalsIgnoreCase("AN")) {
			infos = mensaje.split(" ", 2);
			if (infos.length < 2) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "ERROR : Mensaje no completo");
				return;
			}
			String nombrePJ = "<b>" + _perso.getNombre() + " :</b> ";
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS(nombrePJ + infos[1]);
			return;
		} else if (comando.equalsIgnoreCase("IRDONDE") || comando.equalsIgnoreCase("UNIR")) {
			Personaje P = MundoDofus.getPjPorNombre(infos[1]);
			if (P == null) {
				String str = "El personaje no existe o no esta conectado";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			int mapaID = P.getMapa().getID();
			int celdaID = P.getCelda().getID();
			Personaje objeto = _perso;
			if (infos.length > 2) {
				objeto = MundoDofus.getPjPorNombre(infos[2]);
				if (objeto == null) {
					String str = "El personaje no puede ser localizado";
					GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
					return;
				}
			}
			if (objeto.getPelea() != null) {
				String str = "El personaje a teleportar esta en combate";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			objeto.teleport(mapaID, celdaID);
			String str = "El jugador ha sido teletransportado";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
		} else if (comando.equalsIgnoreCase("TP")) {
			short mapaID = -1;
			int celdaID = -1;
			try {
				mapaID = Short.parseShort(infos[1]);
				celdaID = Integer.parseInt(infos[2]);
			} catch (Exception e) {
			}
			if (mapaID == -1 || celdaID == -1 || MundoDofus.getMapa(mapaID) == null || (MundoDofus.getMapa(mapaID).getCelda(celdaID) == null)) {
				String str = "MapaID o celdaID invalido";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			Personaje objetivo = _perso;
			if (infos.length > 3) {
				objetivo = MundoDofus.getPjPorNombre(infos[3]);
				if (objetivo == null || objetivo.getPelea() != null) {
					String str = "El personaje no ha podido ser localizado o esta en combate";
					GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
					return;
				}
			}
			objetivo.teleport(mapaID, celdaID);
			String str = "El jugador ha sido teletransportado";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
		}
	}

	private void GM_lvl_2(String comando, String[] infos, String mensaje) { // TODO:
		if (_cuenta.getRango() < 2) {
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "NO TIENES EL RANGO REQUERIDO PARA ESTA ACCION.");
			return;
		}
		if (comando.equalsIgnoreCase("KAMAS")) {
			int cantidad = 0;
			try {
				cantidad = Integer.parseInt(infos[1]);
			} catch (Exception e) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Valor incorrecto");
				return;
			}
			if (cantidad == 0) {
				return;
			}
			Personaje pj = _perso;
			if (infos.length == 3) {
				String nombre = infos[2];
				pj = MundoDofus.getPjPorNombre(nombre);
				if (pj == null) {
					pj = _perso;
				}
			}
			long curKamas = pj.getKamas();
			long newKamas = curKamas + cantidad;
			if (newKamas < 0) {
				newKamas = 0;
			}
			if (newKamas > 2000000000) {
				newKamas = 2000000000;
			}
			pj.setKamas(newKamas);
			if (pj.enLinea()) {
				GestorSalida.ENVIAR_As_STATS_DEL_PJ(pj);
			}
			String msj = "Ha sido ";
			msj += (cantidad < 0 ? "retirado" : "agregado") + " ";
			msj += Math.abs(cantidad) + " kamas a " + pj.getNombre();
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
			return;
		} else if (comando.equalsIgnoreCase("TRAER")) {
			Personaje objetivo = null;
			try {
				objetivo = MundoDofus.getPjPorNombre(infos[1]);
			} catch (ArrayIndexOutOfBoundsException e) {
				String str = "Estas usando mal el comando traer";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			if (objetivo == null) {
				String str = "El personaje no existe o no esta conectado";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			if (objetivo.getPelea() != null) {
				String str = "El personaje esta en combate";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			Personaje P = _perso;
			if (infos.length > 2) {
				P = MundoDofus.getPjPorNombre(infos[2]);
				if (P == null) {
					String str = "El personaje no puede ser localizado";
					GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
					return;
				}
			}
			if (P.enLinea()) {
				int mapaID = P.getMapa().getID();
				int celdaID = P.getCelda().getID();
				objetivo.teleport(mapaID, celdaID);
				String str = "El jugador ha sido teletransportado";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
			} else {
				String str = "El jugador no esta en linea";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
			}
		} else if (comando.equalsIgnoreCase("TALLA")) {
			int talla = -1;
			try {
				talla = Integer.parseInt(infos[1]);
			} catch (Exception e) {
			}
			if (talla == -1) {
				String str = "Talla invalida";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			Personaje objetivo = _perso;
			if (infos.length > 2) {
				objetivo = MundoDofus.getPjPorNombre(infos[2]);
				if (objetivo == null) {
					String str = "El personaje no existe o no esta conectado";
					GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
					return;
				}
			}
			objetivo.setTalla(talla);
			GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(objetivo.getMapa(), objetivo.getID());
			GestorSalida.ENVIAR_GM_AGREGAR_PJ_A_TODOS(objetivo.getMapa(), objetivo);
			String str = "La talla del personaje " + objetivo.getNombre() + " ha sido modificada";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
		} else if (comando.equalsIgnoreCase("FORMA")) {
			int idGfx = -1;
			try {
				idGfx = Integer.parseInt(infos[1]);
			} catch (Exception e) {
			}
			if (idGfx == -1) {
				String str = "Gfx ID invalida";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			Personaje objetivo = _perso;
			if (infos.length > 2) {
				objetivo = MundoDofus.getPjPorNombre(infos[2]);
				if (objetivo == null) {
					String str = "El personaje no existe o no esta conectado";
					GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
					return;
				}
			}
			objetivo.setGfxID(idGfx);
			GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(objetivo.getMapa(), objetivo.getID());
			GestorSalida.ENVIAR_GM_AGREGAR_PJ_A_TODOS(objetivo.getMapa(), objetivo);
			String str = "El personaje " + objetivo.getNombre() + " a cambiado de apariencia";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
		} else if (comando.equalsIgnoreCase("SPAWN")) {
			String Mob = null;
			try {
				Mob = infos[1];
			} catch (Exception e) {
			}
			if (Mob == null) {
				return;
			}
			_perso.getMapa().addGrupoDeUnaPelea(_perso.getCelda().getID(), Mob);
			return;
		} else if (comando.equalsIgnoreCase("ITEM") || comando.equalsIgnoreCase("!getitem")) {
			boolean isOffiCmd = comando.equalsIgnoreCase("!getitem");
			int idModelo = 0;
			int perfecto = 0;
			try {
				idModelo = Integer.parseInt(infos[1]);
			} catch (Exception e) {
			}
			try {
				perfecto = Integer.parseInt(infos[3]);
			} catch (Exception e) {
			}
			if (idModelo == 0) {
				String msj = "El objeto modelo " + idModelo + " no existe ";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
				return;
			}
			int cant = 1;
			if (infos.length >= 3) {
				try {
					cant = Integer.parseInt(infos[2]);
				} catch (Exception e) {
				}
			}
			Personaje pj = _perso;
			boolean useMax = false;
			if (infos.length == 5 && !isOffiCmd) {
				if (infos[4].equalsIgnoreCase("MAX")) {
					useMax = true;
				}
			}
			if (perfecto == 1) {
				useMax = true;
			} else {
				useMax = false;
			}
			ObjetoModelo OM = MundoDofus.getObjModelo(idModelo);
			if (OM == null) {
				String msj = "El modelo " + idModelo + " no existe ";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
				return;
			}
			if (cant < 1) {
				cant = 1;
			}
			Objeto obj = OM.crearObjDesdeModelo(cant, useMax);
			if (obj.getModelo().getPrecioVIP() > 0 && _cuenta.getRango() < 5) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "No tienes permitido sacar items VIP");
				return;
			}
			if (!pj.addObjetoSimilar(obj, true, -1)) {
				MundoDofus.addObjeto(obj, true);
				pj.addObjetoPut(obj);
				GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(pj, obj);
			}
			String str = "Creacion del objeto " + idModelo + " " + OM.getNombre() + " con cantidad " + cant + " a "
					+ pj.getNombre();
			if (useMax) {
				str += " con stats maximos";
			}
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
			GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(pj);
			return;
		} else if (comando.equalsIgnoreCase("EXPULSAR")) {
			Personaje pj = _perso;
			String nombre = null;
			try {
				nombre = infos[1];
			} catch (Exception e) {
			}
			pj = MundoDofus.getPjPorNombre(nombre);
			if (pj == null) {
				String msj = "El personaje no existe o no esta conectado";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
				return;
			}
			if (pj.enLinea()) {
				pj.getCuenta().getEntradaPersonaje().salir(false);
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Ha sido quitado " + pj.getNombre());
			} else {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "El personaje " + pj.getNombre() + " no esta conectado");
			}
			return;
		} else if (comando.equalsIgnoreCase("HONOR")) {
			int honor = 0;
			try {
				honor = Integer.parseInt(infos[1]);
			} catch (Exception e) {
			}
			Personaje objetivo = _perso;
			if (infos.length > 2) {
				objetivo = MundoDofus.getPjPorNombre(infos[2]);
				if (objetivo == null) {
					String str = "El personaje no ha podido ser ubicado";
					GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
					return;
				}
			}
			String str = "Ha sido agregado " + honor + " honor a " + objetivo.getNombre();
			if (objetivo.getAlineacion() == CentroInfo.ALINEACION_NEUTRAL) {
				str = "El personaje es neutral ...";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			objetivo.addHonor(honor);
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
			return;
		} else if (comando.equalsIgnoreCase("SALVAR") && !Emu.Salvando) {
			Thread t = new Thread(new Emu.salvarServidorPersonaje());
			t.start();
			String msj = "Salvando server!";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
			return;
		} else if (comando.equalsIgnoreCase("QUIENES")) {
			String msj = "==========\n" + "Lista de los jugadores en linea:";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
			ArrayList<String> ipdupl = new ArrayList<>();
			int cont = 0;
			ArrayList<EntradaPersonaje> clientes = new ArrayList<>();
			clientes.addAll(ServidorPersonaje.getClientes());
			for (EntradaPersonaje client : clientes) {
				if (client == null) {
					continue;
				}
				Personaje P = client.getPersonaje();
				if (P == null) {
					continue;
				}
				if (!ipdupl.contains(P.getCuenta().getActualIP())) {
					ipdupl.add(P.getCuenta().getActualIP());
				}
				msj = P.getNombre() + "(" + P.getID() + ") ";
				msj += "Nivel: " + P.getNivel() + " ";
				msj += "Mapa: " + P.getMapa().getID() + " ";
				msj += "Cuenta: " + P.getCuenta().getNombre() + " ";
				msj += "IP: " + P.getCuenta().getActualIP() + " ";
				cont += 1;
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
			}
			if (_cuenta.getRango() > 4) {
				msj = "Hay " + ipdupl.size() + " personas reales sin contar las multicuentas (" + cont
						+ " con multicuenta)==========\n";
			} else {
				msj = "Hay " + cont + " personas conectadas==========\n";
			}
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
			return;
		} else if (comando.equalsIgnoreCase("CAPITAL")) {
			int pts = -1;
			try {
				pts = Integer.parseInt(infos[1]);
			} catch (Exception e) {
			}
			if (pts == -1) {
				String str = "valor invalido";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			Personaje objetivo = _perso;
			if (infos.length > 2 && _cuenta.getRango() > 4) {
				objetivo = MundoDofus.getPjPorNombre(infos[2]);
				if (objetivo == null) {
					String str = "El personaje no existe o no esta conectado";
					GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
					return;
				}
			}
			objetivo.addCapital(pts);
			GestorSalida.ENVIAR_As_STATS_DEL_PJ(objetivo);
			String str = "La puntos de caracteristicas han sido modificados";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
			return;
		} else if (comando.equalsIgnoreCase("ADDEXP")) {
			long cantidad = 0;
			try {
				cantidad = Long.parseLong(infos[1]);
			} catch (Exception e) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Valor incorrecto");
				return;
			}
			_perso.addExp(cantidad, false);
			if (_perso.enLinea()) {
				GestorSalida.ENVIAR_SL_LISTA_HECHIZOS(_perso);
				GestorSalida.ENVIAR_AN_MENSAJE_NUEVO_NIVEL(_out, _perso.getNivel());
				GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
				_perso.actualizarInfoGrupo();
				if (_perso.getGremio() != null) {
					_perso.getMiembroGremio().setNivel(_perso.getNivel());
				}
			}
			return;
		} else if (comando.equalsIgnoreCase("NIVEL")) {
			int cantidad = 0;
			try {
				cantidad = Integer.parseInt(infos[1]);
				if (cantidad < 1) {
					cantidad = 1;
				}
				if (cantidad > Emu.MAX_NIVEL) {
					cantidad = Emu.MAX_NIVEL;
				}
				Personaje pj = _perso;
				if (infos.length == 3 && _cuenta.getRango() > 4) {
					String nombre = infos[2];
					pj = MundoDofus.getPjPorNombre(nombre);
					if (pj == null) {
						pj = _perso;
					}
				}
				if (pj.getEncarnacion() != null) {
					GestorSalida.ENVIAR_BAT2_CONSOLA(_out,
							"No se le puede subir el nivel, porque el personaje es una encarnacion.");
					return;
				}
				if (pj.getNivel() < cantidad) {
					while (pj.getNivel() < cantidad) {
						pj.subirNivel(true, true);
					}
					if (pj.enLinea()) {
						GestorSalida.ENVIAR_SL_LISTA_HECHIZOS(pj);
						GestorSalida.ENVIAR_AN_MENSAJE_NUEVO_NIVEL(_out, pj.getNivel());
						GestorSalida.ENVIAR_As_STATS_DEL_PJ(pj);
						pj.actualizarInfoGrupo();
						if (pj.getGremio() != null) {
							pj.getMiembroGremio().setNivel(pj.getNivel());
						}
					}
				}
				String msj = "Ha sido modificado el nivel de " + pj.getNombre() + " a " + cantidad;
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
			} catch (Exception e) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Valor incorrecto");
				return;
			}
			return;
		} else if (comando.equalsIgnoreCase("REINICIATODO")) {
			// _perso.getMapa().moveMobsRandomly(true);
			/*
			 * GestorSQL.test2(); GestorSQL.comenzarTransacciones(); GestorSQL.TIMER(true);
			 */
			return;
		} else if (comando.equalsIgnoreCase("PORTAL")) {
			MundoDofus.Portal();
			return;
		} else if (comando.equalsIgnoreCase("INICIARPELEA")) {
			_perso.getPelea().iniciarPelea();
			return;
		} else if (comando.equalsIgnoreCase("REINICIAITEM")) {
			int itemr = -1;
			try {
				itemr = Integer.parseInt(infos[1]);
			} catch (Exception e) {
				return;
			}
			GestorSQL.test3(itemr);
			return;
		} else if (comando.equalsIgnoreCase("ALINEACION")) {
			byte alineacion = -1;
			try {
				alineacion = Byte.parseByte(infos[1]);
			} catch (Exception e) {
			}
			if (alineacion < CentroInfo.ALINEACION_NEUTRAL || alineacion > CentroInfo.ALINEACION_MERCENARIO) {
				String str = "Valor invalido";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			Personaje objetivo = _perso;
			if (infos.length > 2) {
				objetivo = MundoDofus.getPjPorNombre(infos[2]);
				if (objetivo == null) {
					String str = "El personaje no existe o no esta conectado";
					GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
					return;
				}
			}
			objetivo.modificarAlineamiento(alineacion);
			String str = "La alineacion del personaje ha sido modificada";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
			return;
		} else if (comando.equalsIgnoreCase("APRENDEROFICIO")) {
			int oficio = -1;
			try {
				oficio = Integer.parseInt(infos[1]);
			} catch (Exception e) {
			}
			if (oficio == -1 || MundoDofus.getOficio(oficio) == null) {
				String str = "valor invalido";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			Personaje objetivo = _perso;
			if (infos.length > 2) {
				objetivo = MundoDofus.getPjPorNombre(infos[2]);
				if (objetivo == null) {
					String str = "El personaje no existe o no esta conectado";
					GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
					return;
				}
			}
			objetivo.aprenderOficio(MundoDofus.getOficio(oficio), _perso.getStatsOficios().size() + 1, false);
			String str = "El personaje " + objetivo.getNombre() + " ha aprendido el oficio";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
		} else if (comando.equalsIgnoreCase("TITULO")) {
			Personaje pj = null;
			byte tituloID = 0;
			try {
				pj = MundoDofus.getPjPorNombre(infos[1]);
				tituloID = Byte.parseByte(infos[2]);
			} catch (Exception e) {
			}
			if (pj == null) {
				String str = "El personaje no pudo ser modificado";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			pj.setTitulo(tituloID);
			pj.addTitulos(tituloID);
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Nuevo Titulo Adquirido.");
			GestorSQL.SALVAR_PERSONAJE(pj, false);
			if (pj.getPelea() == null) {
				GestorSalida.ENVIAR_GM_REFRESCAR_PJ_EN_MAPA(pj.getMapa(), pj);
			}
			return;
		} else if (comando.equalsIgnoreCase("EXPOFICIO")) {
			int job = -1;
			int xp = -1;
			try {
				job = Integer.parseInt(infos[1]);
				xp = Integer.parseInt(infos[2]);
			} catch (Exception e) {
			}
			if (job == -1 || xp < 0) {
				String str = "Valores invalidos";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			Personaje objetivo = _perso;
			if (infos.length > 3) {
				objetivo = MundoDofus.getPjPorNombre(infos[3]);
				if (objetivo == null) {
					String str = "El personaje no existe o no esta conectado";
					GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
					return;
				}
			}
			StatsOficio SM = objetivo.getOficioPorID(job);
			if (SM == null) {
				String str = "El personaje no conoce el oficio";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			SM.addXP(objetivo, xp);
			String str = "El oficio ha sido experimentado";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
			return;
		} else if (comando.equalsIgnoreCase("PUNTOSHECHIZO")) {
			int pts = -1;
			try {
				pts = Integer.parseInt(infos[1]);
			} catch (Exception e) {
			}
			if (pts == -1) {
				String str = "Valor invalido";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			Personaje objetivo = _perso;
			if (infos.length > 2) {
				objetivo = MundoDofus.getPjPorNombre(infos[2]);
				if (objetivo == null) {
					String str = "El personaje no existe o no esta conectado";
					GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
					return;
				}
			}
			objetivo.addPuntosHechizos(pts);
			GestorSalida.ENVIAR_As_STATS_DEL_PJ(objetivo);
			String str = "El numero de puntos de hechizo ha sido modificado";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
			return;
		} else if (comando.equalsIgnoreCase("APRENDERHECHIZO")) {
			int hechizo = -1;
			try {
				hechizo = Integer.parseInt(infos[1]);
			} catch (Exception e) {
			}
			if (hechizo == -1) {
				String str = "Valor invalido";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			Personaje objetivo = _perso;
			if (infos.length > 2) {
				objetivo = MundoDofus.getPjPorNombre(infos[2]);
				if (objetivo == null) {
					String str = "El personaje no existe o no esta conectado";
					GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
					return;
				}
			}
			_perso.setPosHechizo(hechizo, Encriptador.getValorHashPorNumero(1), false);
			objetivo.aprenderHechizo(hechizo, 1, false, true);
			String str = "El hechizo esta aprendido";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
			return;
		} else if (comando.equalsIgnoreCase("BANEAR")) {
			Personaje P = MundoDofus.getPjPorNombreIgnora(infos[1]);
			if (P == null) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Personaje no ubicado");
				return;
			}
			if (P.getCuenta() == null) {
				GestorSQL.CARGAR_CUENTA_POR_ID(P.getCuentaID());
			}
			if (P.getCuenta() == null) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Error");
				return;
			}
			P.getCuenta().setBaneado(true);
			GestorSQL.SALVAR_CUENTA(P.getCuenta());
			if (P.getCuenta().getEntradaPersonaje() == null) {
				P.getCuenta().getEntradaPersonaje().salir(false);
			}
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Ha sido baneado " + P.getNombre());
			return;
		} else if (comando.equalsIgnoreCase("DESBANEAR")) {
			Personaje P = MundoDofus.getPjPorNombreIgnora(infos[1]);
			if (P == null) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Personaje no ubicado");
				return;
			}
			if (P.getCuenta() == null) {
				GestorSQL.CARGAR_CUENTA_POR_ID(P.getCuentaID());
			}
			if (P.getCuenta() == null) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Error");
				return;
			}
			P.getCuenta().setBaneado(false);
			GestorSQL.SALVAR_CUENTA(P.getCuenta());
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Ha sido desbaneado " + P.getNombre());
			return;
		} else if (comando.equalsIgnoreCase("HELP")) {
			String str3 = "Comandos Disponibles: ";
			str3 = String.valueOf(str3) + "\n traer [nombre jugador]";
			str3 = String.valueOf(str3) + "\n irdonde [nombre jugador]";
			str3 = String.valueOf(str3) + "\n kamas [cantidad]";
			str3 = String.valueOf(str3) + "\n teleport [mapaid] [cellid]";
			str3 = String.valueOf(str3) + "\n talla [tamaño]";
			str3 = String.valueOf(str3) + "\n forma [id]";
			str3 = String.valueOf(str3) + "\n spawn [idmob;idmob;idmob...]";
			str3 = String.valueOf(str3) + "\n item [itemid]";
			str3 = String.valueOf(str3) + "\n expulsar [nombrejugador]";
			str3 = String.valueOf(str3) + "\n honor [cantidad]";
			str3 = String.valueOf(str3) + "\n nivel [cantidad]";
			str3 = String.valueOf(str3) + "\n capital [cantidad]";
			str3 = String.valueOf(str3) + "\n alineacion [id]";
			str3 = String.valueOf(str3) + "\n titulo [id]";
			str3 = String.valueOf(str3) + "\n aprenderoficio [id]";
			str3 = String.valueOf(str3) + "\n expoficio [id] [cantidad]";
			str3 = String.valueOf(str3) + "\n puntoshechizo [cantidad]";
			str3 = String.valueOf(str3) + "\n aprenderhechizo [id]";
			str3 = String.valueOf(str3) + "\n banear [nombrejugador]";
			str3 = String.valueOf(str3) + "\n desbanear [nombrejugador]";
			str3 = String.valueOf(str3) + "\n salvar";
			str3 = String.valueOf(str3) + "\n mutear [nombrejugador] [tiempo min]";
			str3 = String.valueOf(str3) + "\n desmutear [nombrejugador]";
			str3 = String.valueOf(str3) + "\n quienes";
			str3 = String.valueOf(str3) + "\n larvas (EVENT)";
			str3 = String.valueOf(str3) + "\n jalato (EVENT)";
			str3 = String.valueOf(str3) + "\n regalos (EVENT)";
			str3 = String.valueOf(str3) + "\n gema (EVENT)";
			str3 = String.valueOf(str3) + "\n mercader (EVENT)";
			str3 = String.valueOf(str3) + "\n reiniciarmega (EVENT)";
			str3 = String.valueOf(str3) + "\n angel (EVENT)";
			str3 = String.valueOf(str3) + "\n alineacion [align]";
			str3 = String.valueOf(str3) + "\n mapainfo";
			str3 = String.valueOf(str3) + "\n mostrarpospelea";
			str3 = String.valueOf(str3) + "\n agregarpospelea";
			str3 = String.valueOf(str3) + "\n eliminarposiciones";
			str3 = String.valueOf(str3) + "\n creargremio";
			str3 = String.valueOf(str3) + "\n deformar";
			str3 = String.valueOf(str3) + "\n GRUPOMAXMOBS";
			str3 = String.valueOf(str3) + "\n desbanearip [ip]";
			str3 = String.valueOf(str3) + "\n GRUPOMOB";
			str3 = String.valueOf(str3) + "\n GRUPOMOBFIX";
			str3 = String.valueOf(str3) + "\n AGREGARNPC [npcid]";
			str3 = String.valueOf(str3) + "\n borrarnpc [npcid]";
			str3 = String.valueOf(str3) + "\n agregarbots [cant]";
			str3 = String.valueOf(str3) + "\n borrarbots";
			str3 = String.valueOf(str3) + "\n moverbots";
			str3 = String.valueOf(str3) + "\n ABRIRTALLER";
			str3 = String.valueOf(str3) + "\n CERRARTALLER";
			str3 = String.valueOf(str3) + "\n PUERTACERCADO [cellid]";
			str3 = String.valueOf(str3) + "\n CELDAMONTURA";
			str3 = String.valueOf(str3) + "\n CELDASCERCADO";
			str3 = String.valueOf(str3) + "\n energia [cant]";
			str3 = String.valueOf(str3) + "\n agregaritemnpc [npcid] [item id]";
			str3 = String.valueOf(str3) + "\n borraritemnpc [npcid] [item id]";
			str3 = String.valueOf(str3) + "\n objetopersonal [obj]";
			str3 = String.valueOf(str3) + "\n espiarpj [character name]";
			str3 = String.valueOf(str3) + "\n objetoset [setid]";
			str3 = String.valueOf(str3) + "\n honor [cant]";
			str3 = String.valueOf(str3) + "\n consultapuntos [character name]";
			str3 = String.valueOf(str3) + "\n regalo [itemid] [character]";
			str3 = String.valueOf(str3) + "\n regaloonline [itemid] [cant] [time in hours]";
			str3 = String.valueOf(str3) + "\n iniciartorneo [cant]";
			str3 = String.valueOf(str3) + "\n torneoon [OPTIONAL 1,2,3 VALUE] only for rx people";
			str3 = String.valueOf(str3) + "\n torneooff";
			str3 = String.valueOf(str3) + "\n reload [reload map data]";
			str3 = String.valueOf(str3) + "\n objetoparatodos [objid] [cant]";
			str3 = String.valueOf(str3) + "\n haceraccion [perso] [actionid] [args]";
			str3 = String.valueOf(str3) + "\n a [msg]";
			str3 = String.valueOf(str3) + "\n ratekamas [value]";
			str3 = String.valueOf(str3) + "\n ratedrop [value]";
			str3 = String.valueOf(str3) + "\n ratexppvm [value]";
			str3 = String.valueOf(str3) + "\n ratexppvp [value]";
			str3 = String.valueOf(str3) + "\n ratexpoficio [value]";
			str3 = String.valueOf(str3) + "\n ratecrianza [value]";
			str3 = String.valueOf(str3) + "\n ratehonor [value]";
			str3 = String.valueOf(str3) + "\n rateporcfm [value]";
			str3 = String.valueOf(str3) + "\n adicpj [value]";
			str3 = String.valueOf(str3) + "\n adicmob [value]";
			str3 = String.valueOf(str3) + "\n adiccac [value]";
			str3 = String.valueOf(str3) + "\n recibir [packet]";
			str3 = String.valueOf(str3) + "\n banearip [perso]";
			str3 = String.valueOf(str3) + "\n moverperco";
			str3 = String.valueOf(str3) + "\n regalarpuntos [cant] [perso]";
			str3 = String.valueOf(str3) + "\n enviar [packet]";
			GestorSalida.ENVIAR_BAT2_CONSOLA(this._out, str3);
		} else {
			GM_lvl_1(comando, infos, mensaje);
		}
	}

	private void GM_lvl_3(String comando, String[] infos, String mensaje) {
		// FIXME GM lvl 3
		if (_cuenta.getRango() < 3) {
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "NO TIENES EL RANGO REQUERIDO PARA ESTA ACCION.");
			return;
		}
		if (comando.equalsIgnoreCase("MUTEAR")) {
			Personaje pj = _perso;
			String nombre = null;
			try {
				nombre = infos[1];
			} catch (Exception e) {
			}
			int tiempo = 0;
			try {
				tiempo = Integer.parseInt(infos[2]);
			} catch (Exception e) {
			}
			pj = MundoDofus.getPjPorNombre(nombre);
			if (pj == null || tiempo < 0) {
				String msj = "El personaje no existe o no esta conectado o la duracion es invalida.";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
				return;
			}
			String msj = "Ha sido mute " + pj.getNombre() + " por " + tiempo + " segundos";
			if (pj.getCuenta() == null) {
				msj = "(El personaje " + pj.getNombre() + " no esta conectado)";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
				return;
			}
			pj.getCuenta().mutear(true, tiempo);
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
			if (!pj.enLinea()) {
				msj = "(El personaje " + pj.getNombre() + " no esta conectado)";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
			} else {
				GestorSalida.ENVIAR_Im_INFORMACION(pj, "1124;" + tiempo);
			}
			return;
		} else if (comando.equalsIgnoreCase("LARVAS")) {
			MundoDofus.carreraLarvas();
			return;
		} else if (comando.equalsIgnoreCase("JALATO")) {
			MundoDofus.PeleaJalato();
			return;
		} else if (comando.equalsIgnoreCase("REINICIARMEGA")) {
			Invasion.iniciaInvasion();
			Date date = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.MINUTE, 14);
			Date expirationDate = cal.getTime();
			String exp = String.valueOf(expirationDate);
			Emu.tiempoInvasion = exp;
		} else if (comando.equalsIgnoreCase("MAPASCOORDENADAS")) {
			int x = -1;
			int y = -1;
			try {
				x = Integer.parseInt(infos[1]);
				y = Integer.parseInt(infos[2]);
			} catch (Exception e) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Valor incorrecto");
				return;
			}
			String str = MundoDofus.mapaPorCoordenadas(x, y);
			if (str.isEmpty()) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "No hay ID mapa para esas coordenadas");
			} else {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out,
						"Los ID mapas para las coordenas X: " + x + " Y: " + y + " son " + str);
			}
			return;
		} else if (comando.equalsIgnoreCase("DESMUTEAR")) {
			Personaje pj = _perso;
			String nombre = null;
			try {
				nombre = infos[1];
			} catch (Exception e) {
			}
			pj = MundoDofus.getPjPorNombre(nombre);
			if (pj == null) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "El personaje no existe o no esta conectado");
				return;
			}
			pj.getCuenta().mutear(false, 0);
			String msj = "Ha sido desmuteado " + pj.getNombre();
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
			if (!pj.enLinea()) {
				msj = "(El personaje " + pj.getNombre() + " no esta conectado)";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
			}
		} else if (comando.equalsIgnoreCase("ALINEACION")) {
			byte alineacion = -1;
			try {
				alineacion = Byte.parseByte(infos[1]);
			} catch (Exception e) {
			}
			if (alineacion < CentroInfo.ALINEACION_NEUTRAL || alineacion > CentroInfo.ALINEACION_MERCENARIO) {
				String str = "Valor invalido";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			Personaje objetivo = _perso;
			if (infos.length > 2) {
				objetivo = MundoDofus.getPjPorNombre(infos[2]);
				if (objetivo == null) {
					String str = "El personaje no existe o no esta conectado";
					GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
					return;
				}
			}
			objetivo.modificarAlineamiento(alineacion);
			String str = "La alineacion del personaje ha sido modificada";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);

		} else if (comando.equalsIgnoreCase("MAPAINFO")) {
			String msj = "==========\n" + "Lista de NPC del mapa:";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
			Mapa mapa = _perso.getMapa();
			for (Entry<Integer, NPC> entry : mapa.getNPCs().entrySet()) {
				msj = entry.getKey() + " " + entry.getValue().getModeloBD().getID() + " "
						+ entry.getValue().getCeldaID() + " " + entry.getValue().getModeloBD().getPreguntaID();
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
			}
			msj = "Lista de los grupos de mounstros:";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
			for (Entry<Integer, GrupoMobs> entry : mapa.getMobGroups().entrySet()) {
				msj = entry.getKey() + " " + entry.getValue().getCeldaID() + " " + entry.getValue().getAlineamiento()
						+ " " + entry.getValue().getTamaÑo();
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
			}
			msj = "==========";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
			return;

		} else if (comando.equalsIgnoreCase("MOSTRARPOSPELEA")) {
			String msj = "Lista de los lugares de pelea [EquipoID] : [CeldaID]";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
			String lugares = _perso.getMapa().getLugaresString();
			if (lugares.indexOf('|') == -1 || lugares.length() < 2) {
				msj = "Los lugares de pelea no estan definidos";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
				return;
			}
			String equipo0 = "", equipo1 = "";
			String[] p = lugares.split("\\|");
			try {
				equipo0 = p[0];
			} catch (Exception e) {
			}
			try {
				equipo1 = p[1];
			} catch (Exception e) {
			}
			msj = "Equipo 0:\n";
			for (int a = 0; a <= equipo0.length() - 2; a += 2) {
				String codigo = equipo0.substring(a, a + 2);
				msj += Encriptador.celdaCodigoAID(codigo) + ",";
			}
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
			msj = "Equipo 1:\n";
			for (int a = 0; a <= equipo1.length() - 2; a += 2) {
				String codigo = equipo1.substring(a, a + 2);
				msj += Encriptador.celdaCodigoAID(codigo) + ",";
			}
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
			return;
		} else if (comando.equalsIgnoreCase("AGREGARPOSPELEA")) {
			int equipo = -1;// TODO:
			int celda = _perso.getCelda().getID();
			try {
				equipo = Integer.parseInt(infos[1]);
			} catch (Exception e) {
			}
			if (equipo < 0 || equipo > 1) {
				String str = "Equipo o Celda Id incorrectos";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			String places = _perso.getMapa().getLugaresString();
			String[] p = places.split("\\|");
			boolean listo = false;
			String equipo0 = "", equipo1 = "";
			try {
				equipo0 = p[0];
			} catch (Exception e) {
			}
			try {
				equipo1 = p[1];
			} catch (Exception e) {
			}
			for (int a = 0; a <= equipo0.length() - 2; a += 2) {
				if (celda == Encriptador.celdaCodigoAID(equipo0.substring(a, a + 2))) {
					listo = true;
				}
			}
			for (int a = 0; a <= equipo1.length() - 2; a += 2) {
				if (celda == Encriptador.celdaCodigoAID(equipo1.substring(a, a + 2))) {
					listo = true;
				}
			}
			if (listo) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "La casilla esta dentro de la lista");
				return;
			}
			if (equipo == 0) {
				equipo0 += Encriptador.celdaIDACodigo(celda);
			} else if (equipo == 1) {
				equipo1 += Encriptador.celdaIDACodigo(celda);
			}
			String nuevosLugares = equipo0 + "|" + equipo1;
			_perso.getMapa().setPosicionesDePelea(nuevosLugares);
			if (!GestorSQL.ACTUALIZAR_MAPA_POSPELEA_NROGRUPO(_perso.getMapa())) {
				return;
			}
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Los lugares de pelea han sido modificados (" + nuevosLugares + ")");
			return;
		} else if (comando.equalsIgnoreCase("ELIMINARPOSICIONES")) {
			_perso.getMapa().setPosicionesDePelea("|");
			if (!GestorSQL.ACTUALIZAR_MAPA_POSPELEA_NROGRUPO(_perso.getMapa())) {
				return;
			}
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Las posiciones de pelea han sido borradas.");
			return;
		} else if (comando.equalsIgnoreCase("CREARGREMIO")) {
			Personaje pj = _perso;
			if (infos.length > 1) {
				pj = MundoDofus.getPjPorNombre(infos[1]);
			}
			if (pj == null) {
				String msj = "El personaje no existe o no esta conectado";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
				return;
			}
			if (!pj.enLinea()) {
				String msj = "El personaje " + pj.getNombre() + " no esta conectado";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
				return;
			}
			if (pj.getGremio() != null || pj.getMiembroGremio() != null) {
				String msj = "El personaje " + pj.getNombre() + " a dejado el gremio";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
				return;
			}
			GestorSalida.ENVIAR_gn_CREAR_GREMIO(pj);
			String msj = pj.getNombre() + ": Ventana de la creacion de gremio";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
			return;
		} else if (comando.equalsIgnoreCase("DEFORMAR")) {
			Personaje objetivo = _perso;
			if (infos.length > 1) {
				objetivo = MundoDofus.getPjPorNombre(infos[1]);
				if (objetivo == null) {
					String str = "El personaje no puede ser localizado";
					GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
					return;
				}
			}
			objetivo.deformar();
			GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(objetivo.getMapa(), objetivo.getID());
			GestorSalida.ENVIAR_GM_AGREGAR_PJ_A_TODOS(objetivo.getMapa(), objetivo);
			String str = "El jugador " + objetivo.getNombre() + " ha sido deformado";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);

		} else if (comando.equalsIgnoreCase("GRUPOMAXMOBS")) {
			infos = mensaje.split(" ", 4);
			byte id = -1;
			try {
				id = Byte.parseByte(infos[1]);
			} catch (Exception e) {
			}
			if (id == -1) {
				String str = "Valor invalido";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			String msj = "El numero de grupo de mobs ha sido modificado";
			_perso.getMapa().setMaxGrupoDeMobs(id);
			boolean ok = GestorSQL.ACTUALIZAR_MAPA_POSPELEA_NROGRUPO(_perso.getMapa());
			if (ok) {
				msj += " salvando la BDD";
			}
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
			return;
		} else if (comando.equalsIgnoreCase("DESBANEARIP")) {
			String ip = infos[1];
			CentroInfo.borrarIP(ip);
			GestorSQL.BORRAR_BANIP(ip);
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Se borro la ip " + ip + " de la lista de ip baneadas");
			return;

		} else if (comando.equalsIgnoreCase("GRUPOMOB")) {
			String grupoData = infos[1];
			_perso.getMapa().addGrupoFix(_perso.getCelda().getID(), grupoData);
			String str = "El grupo ha sido modificado";
			if (GestorSQL.SALVAR_NUEVO_GRUPOMOB(_perso.getMapa().getID(), _perso.getCelda().getID(), grupoData)) {
				str += ", salvando la BDD";
			}
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
			return;
		} else if (comando.equalsIgnoreCase("GRUPOMOBFIX")) {
			String grupoData = infos[1];
			_perso.getMapa().addGrupoFix(_perso.getCelda().getID(), grupoData);
			String str = "El grupo ha sido modificado";
			if (GestorSQL.INSERT_NUEVO_GRUPOMOB(_perso.getMapa().getID(), _perso.getCelda().getID(), grupoData)) {
				str += "y guardado en la BDD";
			}
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
			return;
		} else if (comando.equalsIgnoreCase("AGREGARNPC")) {
			int idNPCModelo = 0;
			try {
				idNPCModelo = Integer.parseInt(infos[1]);
			} catch (Exception e) {
			}
			if (idNPCModelo == 0 || MundoDofus.getNPCModelo(idNPCModelo) == null) {
				String str = "NPC ID invalido";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			NPC npc = _perso.getMapa().addNPC(idNPCModelo, _perso.getCelda().getID(), _perso.getOrientacion());
			GestorSalida.ENVIAR_GM_AGREGAR_NPC_AL_MAPA(_perso.getMapa(), npc, _perso);
			String str = "El NPC ha sido agregado";
			if (_perso.getOrientacion() == 0 || _perso.getOrientacion() == 2 || _perso.getOrientacion() == 4
					|| _perso.getOrientacion() == 6) {
				str += " NPC esta invisible (orientacion diagonal invalida).";
			}
			if (GestorSQL.AGREGAR_NPC_AL_MAPA(_perso.getMapa().getID(), idNPCModelo, _perso.getCelda().getID(),
					_perso.getOrientacion(), MundoDofus.getNPCModelo(idNPCModelo).getNombre())) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
			} else {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Error al momento de salvar la posicion");
			}
			return;
		} else if (comando.equalsIgnoreCase("GEMA")) {
			MundoDofus.Gema();
			return;
		} else if (comando.equalsIgnoreCase("REGALOS")) {
			MundoDofus.Regalos();
			return;
		} else if (comando.equalsIgnoreCase("MERCADER")) {
			MundoDofus.Mercader();
			return;
		} else if (comando.equalsIgnoreCase("BORRARNPC")) {
			int id = 0;
			try {
				id = Integer.parseInt(infos[1]);
			} catch (Exception e) {
			}
			NPC npc = _perso.getMapa().getNPC(id);
			if (id == 0 || npc == null) {
				String str = "NPC ID invalido";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			int exC = npc.getCeldaID();
			GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(_perso.getMapa(), id);
			_perso.getMapa().borrarNPCoGrupoMob(id);
			String str = "El NPC ha sido borrado";
			if (GestorSQL.BORRAR_NPC_DEL_MAPA(_perso.getMapa().getID(), exC)) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
			} else {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Error al momento de salvar la posicion");
			}
			return;

		} else if (comando.equalsIgnoreCase("BORRARBOTS")) {
			if (MundoDofus.botsServer.containsKey(_perso.getMapa().getID())) {
				MundoDofus.botsServer.remove(_perso.getMapa().getID());
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Se han borrado todos los bots del mapa.");
			} else {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "No hab�an bots en este mapa");
			}
			return;
		} else if (comando.equalsIgnoreCase("MOVERBOTS")) {
			ArrayList<Integer> mapac = new ArrayList<>();
			for (Personaje perso : MundoDofus.getPJsEnLinea()) {
				int rand = Formulas.getRandomValor(2, 2);
				if (rand == 2 && !mapac.contains(perso.getMapa().getID())) {
					if (MundoDofus.botsServer.containsKey(_perso.getMapa().getID())) {
						String packetentro = MundoDofus.botsServer.get(_perso.getMapa().getID());
						int cuantoshay = 0;
						for (String stf : packetentro.split("\\|")) {
							try {
								stf.split("\\+")[1].replace("GM", "");
								cuantoshay += 1;
							} catch (Exception e) {
							}
						}
						int randommove = Formulas.getRandomValor(1, cuantoshay);
						int lasveces = 0;
						for (String str2 : packetentro.split("\\|")) {
							try {
								lasveces += 1;
								if (lasveces >= randommove) {
									String valorfinal1 = str2.split("\\+")[1].replace("GM", "");
									int idespecial = Integer.parseInt(valorfinal1.split(";")[3]);
									int celdapj = Integer.parseInt(valorfinal1.split(";")[0]);
									int cell = _perso.getMapa().getRandomCeldaIDLibre();
									String pathstr = Camino.getShortestStringPathBetween(_perso.getMapa(), celdapj,
											cell, 0);
									if (pathstr == null) {
										return;
									}
									String elnuevo = str2.replace(celdapj + ";", cell + ";");
									packetentro = packetentro.replace(str2, elnuevo);
									MundoDofus.botsServer.remove(_perso.getMapa().getID());
									MundoDofus.botsServer.put(_perso.getMapa().getID(), packetentro);
									String packet = "GA0;1;" + idespecial + ";" + pathstr;
									GestorSalida.enviar(_perso, packet);
									break;
								}
							} catch (Exception e) {
							}
						}
					}
				}
			}
			return;
		} else if (comando.equalsIgnoreCase("AGREGARBOTS")) {
			int nrbots = 0;
			try {
				nrbots = Integer.parseInt(infos[1]);
			} catch (Exception e) {
				return;
			}
			String str = "";
			if (nrbots == 0) {
				str = "Especifica el nr de bots";
				return;
			} // GM|+455;2;0;4;iutzv;4,5~*;41^100,;1;0,0,0,210,0;-1;-1;-1;1d46,null,null,16495,null;0;;;;;0;;0.2;0
			for (int i = 0; i < nrbots; i++) {
				Random obj = new Random();
				int rand_num = obj.nextInt(0xffffff + 1);
				obj = new Random();
				int rand_num2 = obj.nextInt(0xffffff + 1);
				obj = new Random();
				int rand_num3 = obj.nextInt(0xffffff + 1);
				String colorCode = String.format("%06x", rand_num);
				String colorCode2 = String.format("%06x", rand_num2);
				String colorCode3 = String.format("%06x", rand_num3);

				int celda = _perso.getMapa().getRandomCeldaIDLibre();
				int idd = -(celda + 500);
				int orientacion = Formulas.getRandomValor(0, 7);
				int clase = Formulas.getRandomValor(1, 12);
				int sexo = Formulas.getRandomValor(0, 1);
				int alineacion = Formulas.getRandomValor(0, 2);
				int nivelalineacion = Formulas.getRandomValor(1, 4);
				int mostraralineacion = Formulas.getRandomValor(0, 1);
				int nivel = Formulas.getRandomValor(1, 231);
				String chars = "abcdefghijklmnopqrstuvwxyz";
				StringBuilder pass = new StringBuilder("");
				for (int x = 0; x < 5; x++) {
					int i1 = (int) Math.floor(Math.random() * 26);
					pass.append(chars.charAt(i1));
				}
				String[] itemarma = { "3297", "8931", "2794", "2544", "3323", "3327", "5126", "5129" };
				int itemarma2 = new Random().nextInt(itemarma.length);
				int itemarma3 = Integer.parseInt(itemarma[itemarma2]);

				String[] itemcabeza = { "2070", "2096", "7228", "9541", "8531", "8558", "8829", "8846" };
				int itemcabeza2 = new Random().nextInt(itemcabeza.length);
				int itemcabeza3 = Integer.parseInt(itemcabeza[itemcabeza2]);

				String[] itemcapa = { "8235", "7232", "8265", "8280", "2412", "7552", "6801", "677" };
				int itemcapa2 = new Random().nextInt(itemcapa.length);
				int itemcapa3 = Integer.parseInt(itemcapa[itemcapa2]);

				String[] itemmascota = { "10544", "10802", "9785", "6716", "6717", "100156", "6718", "8000", "7520",
						"91248", "9617", "9623", "10863", "91290", "1711", "91309", "91301", "91318", "91320" };
				int itemmascota2 = new Random().nextInt(itemmascota.length);
				int itemmascota3 = Integer.parseInt(itemmascota[itemmascota2]);

				String packet = "GM|+" + celda + ";" + orientacion + ";0;" + idd + ";" + pass + ";" + clase + ",;"
						+ (clase * 10) + "^100,;" + sexo + ";" + alineacion + "," + nivelalineacion + ","
						+ mostraralineacion + "," + nivel + ",0;" + colorCode + ";" + colorCode2 + ";" + colorCode3
						+ ";" + Integer.toHexString(itemarma3) + "," + Integer.toHexString(itemcabeza3) + ","
						+ Integer.toHexString(itemcapa3) + "," + Integer.toHexString(itemmascota3)
						+ ",null;0;;;;;0;;0.2;0";
				MundoDofus.idDebots.put(pass.toString(), nivel);
				if (MundoDofus.botsServer.containsKey(_perso.getMapa().getID())) {
					String newstring = MundoDofus.botsServer.get(_perso.getMapa().getID());
					MundoDofus.botsServer.remove(_perso.getMapa().getID());
					MundoDofus.botsServer.put(_perso.getMapa().getID(), newstring + (char) 0x00 + packet);
				} else {
					MundoDofus.botsServer.put(_perso.getMapa().getID(), packet);
				}
				GestorSalida.enviar(_perso, packet);
			}
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
			return;
		} else if (comando.equalsIgnoreCase("ABRIRTALLER")) {
			Emu.tallerAbierto = true;
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Se han abierto los talleres");
			return;
		} else if (comando.equalsIgnoreCase("CERRARTALLER")) {
			Emu.tallerAbierto = true;
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Se han cerrado los talleres");
			return;
		} else if (comando.equalsIgnoreCase("ACABARPELEA")) {
			if (_perso.getPelea() != null) {
				for (Luchador luchador : _perso.getPelea().luchadoresDeEquipo(3)) {
					if (_perso.getPelea() == null) {
						break;
					}
					if (luchador.getMob() != null || luchador.getPrisma() != null || luchador.getRecau() != null) {
						_perso.getPelea().agregarAMuertos(luchador, luchador, "");
					}
				}
			}
			return;
		} else if (comando.equalsIgnoreCase("PUERTACERCADO")) {
			int celda = -1;
			try {
				celda = Integer.parseInt(infos[1]);
			} catch (Exception e) {
			}
			if (celda == -1) {
				return;
			}
			_perso.getMapa().getCercado().setPuerta(celda);
			GestorSQL.ACTUALIZAR_PUERTA_CERCADO(_perso.getMapa().getID(), celda);
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Has bloqueado la celda " + celda + " para las monturas.");
			return;
		} else if (comando.equalsIgnoreCase("CELDAMONTURA")) {
			int celda = -1;
			try {
				celda = _perso.getCelda().getID();
			} catch (Exception e) {
			}
			if (celda == -1) {
				return;
			}
			Mapa mapa = _perso.getMapa();
			if (mapa.getCercado() == null) {
				return;
			}
			Cercado cercado = mapa.getCercado();
			cercado.addCeldaMontura(celda);
			int celdapuerta = cercado.getCeldaID() + ((celda - cercado.getCeldaID()) / 2);
			cercado.setPuerta(celdapuerta);
			GestorSQL.ACTUALIZAR_CELDA_MONTURA(mapa.getID(), celda);
			GestorSQL.ACTUALIZAR_PUERTA_CERCADO(mapa.getID(), celdapuerta);
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Has bloqueado la celda " + celdapuerta + " para las monturas.");
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out,
					"Has agregado la celda " + celda + " para iniciacion de la montura.");
			return;
		} else if (comando.equalsIgnoreCase("VARIARCERCADO")) {
			int tamaÑo = -1;
			int objetos = -1;
			try {
				tamaÑo = Integer.parseInt(infos[1]);
				objetos = Integer.parseInt(infos[2]);
			} catch (Exception e) {
			}
			if (tamaÑo == -1) {
				return;
			}
			Mapa mapa = _perso.getMapa();
			if (mapa.getCercado() == null) {
				return;
			}
			Cercado cercado = mapa.getCercado();
			cercado.setSizeyObjetos(tamaÑo, objetos);
			GestorSQL.ACTUALIZAR_MONTURAS_Y_OBJETOS(tamaÑo, objetos, mapa.getID());
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out,
					"Ahora el cercado tolera " + tamaÑo + " monturas y " + objetos + " objetos.");
			return;

		} else if (comando.equalsIgnoreCase("CELDAOBJETO")) {
			int celda = -1;
			try {
				celda = _perso.getCelda().getID();
			} catch (Exception e) {
			}
			if (celda == -1) {
				return;
			}
			Mapa mapa = _perso.getMapa();
			if (mapa.getCercado() == null) {
				return;
			}
			Cercado cercado = mapa.getCercado();
			cercado.addCeldaObj(celda);
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out,
					"Has agregado la celda " + celda + " para colocar objetos de crianza.");
			return;
		} else if (comando.equalsIgnoreCase("CELDASCERCADO")) {
			Mapa mapa = _perso.getMapa();
			if (mapa.getCercado() == null) {
				return;
			}
			Cercado cercado = mapa.getCercado();
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Tiene las celdas: " + cercado.getStringCeldasObj());
			return;
		} else {
			GM_lvl_2(comando, infos, mensaje);
		}
	}

	private void GM_lvl_4(String comando, String[] infos, String mensaje) {
		// FIXME GM lvl 4
		if (_cuenta.getRango() < 4) {
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "NO TIENES EL RANGO REQUERIDO PARA ESTA ACCION.");
			return;
		}
		if (comando.equalsIgnoreCase("ENERGIA")) {
			int cantidad = 0;
			try {
				cantidad = Integer.parseInt(infos[1]);
				Personaje pj = _perso;
				if (infos.length == 3) {
					String nombre = infos[2];
					pj = MundoDofus.getPjPorNombre(nombre);
					if (pj == null) {
						pj = _perso;
					}
				}
				if (cantidad > 0) {
					pj.agregarEnergia(cantidad);
				} else {
					pj.restarEnergia(cantidad);
				}
				String msj = "Ha sido modificado la energ�a de " + pj.getNombre() + " a " + pj.getEnergia();
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
			} catch (Exception e) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Valor incorrecto");
				return;
			}
			return;

		} else if (comando.equalsIgnoreCase("AGREGARITEMNPC")|| comando.equalsIgnoreCase("AITEMNPC")) {
			int npcid;
			String items = null;
			NPCModelo npcx = null;
			try {
				npcid = Integer.parseInt(infos[1]);
			} catch (Exception e) {
				String str = "has ingresado una id de npc invalida, el comando es - agregaritem npcid item1,item2,item3--";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			try {
				items = infos[2];
			} catch (Exception e) {
				String str = "ingresaste el comando mal, el comando es - agregaritem npcid item1,item2,item3--";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			if (MundoDofus.getNPCModelo(npcid) == null) {
				String str = "La id del npc es incorrecta";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			} else {
				npcx = MundoDofus.getNPCModelo(npcid);
			}
			for (String str : items.split(",")) {
				try {
					if (MundoDofus.getObjModelo(Integer.parseInt(str)) == null) {
						GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "El itemid que has ingresado " + str + " no existe");
					} else {
						ObjetoModelo obj = MundoDofus.getObjModelo(Integer.parseInt(str));
						npcx.addObjetoAVender(obj);
						GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Has agregado el item " + str + " al npc " + npcid);
					}
				} catch (Exception e) {
					GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "El itemid que has ingresado " + str + " es null");
				}
			}
			return;
		} else if (comando.equalsIgnoreCase("OBJETOPERSONAL")) {
			String objp = "";
			try {
				objp = infos[1];
			} catch (Exception e) {
				String str = "Error";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			_perso.objetopersonal = objp;
			return;
		} else if (comando.equalsIgnoreCase("ESPIARPJ")) {
			Personaje objetivo = _perso;
			if (infos.length > 1) {
				objetivo = MundoDofus.getPjPorNombre(infos[1]);
			}
			if (objetivo == null) {
				return;
			}
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out,
					"Abre tu panel de inventario para ver los items del jugador " + objetivo.getNombre());
			GestorSalida.ENVIAR_ASK_PERSONAJE_A_ESPIAR(objetivo, _perso);
			return;
		} else if (comando.equalsIgnoreCase("BORRARITEMNPC")||comando.equalsIgnoreCase("BITEMNPC") ) {
			int npcid;
			String items = null;
			NPCModelo npcx = null;
			try {
				npcid = Integer.parseInt(infos[1]);
			} catch (Exception e) {
				String str = "has ingresado una id de npc invalida, el comando es - borraritem npcid item1,item2,item3--";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			try {
				items = infos[2];
			} catch (Exception e) {
				String str = "ingresaste el comando mal, el comando es - borraritem npcid item1,item2,item3--";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			if (MundoDofus.getNPCModelo(npcid) == null) {
				String str = "La id del npc es incorrecta";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			} else {
				npcx = MundoDofus.getNPCModelo(npcid);
			}
			for (String str : items.split(",")) {
				try {
					if (MundoDofus.getObjModelo(Integer.parseInt(str)) == null) {
						GestorSalida.ENVIAR_BAT2_CONSOLA(_out,
								"El itemid que has ingresado para borrar " + str + " no existe");
					} else {
						npcx.borrarObjetoAVender(Integer.parseInt(str));
						GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Has borrado el item " + str + " de npc " + npcid);
					}
				} catch (Exception e) {
					GestorSalida.ENVIAR_BAT2_CONSOLA(_out,
							"El itemid que has ingresado para borrar " + str + " es null");
				}
			}
			return;
		} else if (comando.equalsIgnoreCase("ITEMSET")) {
			int tID = 0;
			try {
				tID = Integer.parseInt(infos[1]);
			} catch (Exception e) {
			}
			ItemSet IS = MundoDofus.getItemSet(tID);
			if (tID == 0 || IS == null) {
				String msj = "El set " + tID + " no existe";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
				return;
			}
			Personaje pj = _perso;
			if (infos.length >= 3) {
				String nombre = infos[2];
				pj = MundoDofus.getPjPorNombre(nombre);
				if (pj == null) {
					pj = _perso;
				}
			}
			boolean useMax = false;
			if (infos.length >= 4) {
				useMax = infos[3].equals("MAX");
			}
			for (ObjetoModelo OM : IS.getObjetosModelos()) {
				Objeto obj = OM.crearObjDesdeModelo(1, useMax);
				if (!pj.addObjetoSimilar(obj, true, -1)) {
					MundoDofus.addObjeto(obj, true);
					pj.addObjetoPut(obj);
					GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(pj, obj);
				}
			}
			String str = "Creacion del set " + tID + " a " + pj.getNombre();
			if (useMax) {
				str += " con stats al maximo";
			}
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
			GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(pj);
			return;

		} else if (comando.equalsIgnoreCase("HONOR")) {
			int honor = 0;
			try {
				honor = Integer.parseInt(infos[1]);
			} catch (Exception e) {
			}
			Personaje objetivo = _perso;
			if (infos.length > 2) {
				objetivo = MundoDofus.getPjPorNombre(infos[2]);
				if (objetivo == null) {
					String str = "El personaje no ha podido ser ubicado";
					GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
					return;
				}
			}
			String str = "Ha sido agregado " + honor + " honor a " + objetivo.getNombre();
			if (objetivo.getAlineacion() == CentroInfo.ALINEACION_NEUTRAL) {
				str = "El personaje es neutral ...";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			objetivo.addHonor(honor);
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
			return;
		} else if (comando.equalsIgnoreCase("RESTART")) {
			System.exit(0);
			return;
		} else if (comando.equalsIgnoreCase("CONSULTAPUNTOS")) {
			String nombre = "";
			try {
				nombre = infos[1];
			} catch (Exception e) {
			}
			Personaje consultado = MundoDofus.getPjPorNombre(nombre);
			if (consultado == null) {
				String str = "El personaje no ha podido ser ubicado";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			try {
				String str = "El personaje " + nombre + " posee " + GestorSQL.getPuntosCuenta(consultado.getCuentaID());
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
			} catch (NullPointerException e) {
			}
			return;

		} else if (comando.equalsIgnoreCase("REGALO")) {
			int regalo = 0;
			try {
				regalo = Integer.parseInt(infos[1]);
			} catch (Exception e) {
			}
			Personaje objetivo = _perso;
			if (infos.length > 2) {
				objetivo = MundoDofus.getPjPorNombre(infos[2]);
				if (objetivo == null) {
					String str = "El personaje no ha podido ser ubicado";
					GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
					return;
				}
			}
			objetivo.getCuenta().setRegalo(regalo);
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Se entrego el regalo " + regalo + " a " + objetivo.getNombre());
			return;
		} else if (comando.equalsIgnoreCase("REGALOONLINE")) {
			int regalo = 0;
			try {
				regalo = Integer.parseInt(infos[1]);
			} catch (Exception e) {
			}
			for (Personaje pj : MundoDofus.getPJsEnLinea()) {
				pj.getCuenta().setRegalo(regalo);
			}
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out,
					"Se entrego el regalo " + regalo + " a todos los jugadores en l�nea");
			return;
		} else if (comando.equalsIgnoreCase("REGALOALL")) {
			int regalo = 0;
			int cantidad = 1;
			int tiempo = 1;
			try {
				regalo = Integer.parseInt(infos[1]);
				cantidad = Integer.parseInt(infos[2]);
				tiempo = Integer.parseInt(infos[3]);
			} catch (Exception e) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out,
						"Faltan argumentos:  REGALOALL itemid cantidad tiempoenhoras : EJEMPLO: regaloall 100 10 5");
				return;
			}
			if (tiempo < 1) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "No puedes poner un tiempo inferior a 1h");
				return;
			}
			if (tiempo > 168) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "No puedes poner un tiempo superior a 168h (7 d�as)");
				return;
			}
			Date date1 = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(date1);
			cal.add(Calendar.HOUR, tiempo); // 24h
			Date expirationDate = cal.getTime();
			String exp = String.valueOf(expirationDate);
			int id = GestorSQL.MAX_ID_CUENTAS();
			if (GestorSQL.INSERT_REGALO(regalo + "," + cantidad, exp, id)) {
				MundoDofus.itemsRegalos.clear();
				GestorSQL.cargarRegalos();
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, exp + "," + cantidad + "es" + MundoDofus.itemsRegalos
						+ " Se entrego el regalo " + regalo + " a todos los jugadores");
			} else {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Error");
			}
			return;
		} else if (comando.equalsIgnoreCase("ANGEL")) {
			MundoDofus.Busqueda();
			return;
		} else if (comando.equalsIgnoreCase("INICIARTORNEO")) {
			Estaticos.iniciarTorneo();
			return;
		} else if (comando.equalsIgnoreCase("TORNEOON")) {
			int torneorx = 0;
			try {
				torneorx = Integer.parseInt(infos[1]);
			} catch (Exception e) {
			}
			if (torneorx < 0 || torneorx > 3) {
				return;
			}
			Emu.torneoR = torneorx;
			if (torneorx > 0) {
				GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS(
						"Se han abierto las inscripciones al Torneo sÓlo para los R" + torneorx + ".");
			} else {
				GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS(
						"Se han abierto las inscripciones al Torneo sÓlo para las personas sin R.");
			}
			for (Personaje px : MundoDofus.getPJsEnLinea()) {
				if (px == null) {
					continue;
				}
				if (px.enTorneo != 0) {
					px.enTorneo = 0;
				}
			}
			Emu.TorneoOn = true;
			Emu.empezoTorneo = false;
			Emu.faseTorneo = 0;
			Estaticos.PjsTorneo.clear();
			Estaticos.PjsTorneo2.clear();
			Estaticos.PjsTorneo3.clear();
			Estaticos.listaMuertos.clear();
			Estaticos.listaMuertos2.clear();
			Estaticos.listaMuertos3.clear();
			Estaticos.ganador = null;
			return;
		} else if (comando.equalsIgnoreCase("TORNEOOFF")) {
			for (Personaje px : MundoDofus.getPJsEnLinea()) {
				if (px == null) {
					continue;
				}
				if (px.enTorneo != 0) {
					px.enTorneo = 0;
				}
			}
			Emu.TorneoOn = false;
			Emu.empezoTorneo = false;
			Emu.faseTorneo = 0;
			Estaticos.PjsTorneo.clear();
			Estaticos.PjsTorneo2.clear();
			Estaticos.PjsTorneo3.clear();
			Estaticos.listaMuertos.clear();
			Estaticos.listaMuertos2.clear();
			Estaticos.listaMuertos3.clear();
			Estaticos.ganador = null;
			return;
		} else if (comando.equalsIgnoreCase("RELOAD")) {
			int actualmapa = _perso.getMapa().getID();
			MundoDofus.Mapas.remove(actualmapa);
			_perso.teleport((short) 7411, 280);
			return;
		} else if (comando.equalsIgnoreCase("OBJETOPARATODOS")) {
			int regalo = 0;
			int cant = 0;
			try {
				regalo = Integer.parseInt(infos[1]);
				cant = Integer.parseInt(infos[2]);
			} catch (Exception e) {
			}
			ObjetoModelo objMod = MundoDofus.getObjModelo(regalo);
			if (objMod == null) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Objeto modelo nulo");
				return;
			}
			if (cant < 1) {
				cant = 1;
			}
			for (Personaje pj : MundoDofus.getPJsEnLinea()) {
				Objeto obj = objMod.crearObjDesdeModelo(cant, false);
				try {
					if (!pj.addObjetoSimilar(obj, true, -1)) {
						MundoDofus.addObjeto(obj, true);
						pj.addObjetoPut(obj);
						GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(pj, obj);
					}
				} catch (Exception e) {
					continue;
				}
			}
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Se entrego el objeto " + objMod.getNombre() + " con cantidad "
					+ cant + " a todos los jugadores en l�nea");
			return;
		} else if (comando.equalsIgnoreCase("HACERACCION")) {
			if (infos.length < 4) {
				String msj = "Nombre del argumento de comando incorrecto!";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
				return;
			}
			int tipo = -100;
			String args = "", cond = "";
			Personaje pj = _perso;
			try {
				pj = MundoDofus.getPjPorNombre(infos[1]);
				if (pj == null) {
					pj = _perso;
				}
				tipo = Integer.parseInt(infos[2]);
				args = infos[3];
				if (infos.length > 4) {
					cond = infos[4];
				}
			} catch (Exception e) {
				String msj = "Argumentos de comando incorrecto!";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
				return;
			}
			(new Accion(tipo, args, cond)).aplicar(pj, null, -1, -1);
			String msj = "Accion efectuada !";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
			return;
		} else if (comando.equalsIgnoreCase("FECUNDAR")) {
			if (_perso.getMontura() == null) {
				return;
			}
			Dragopavo pavo = _perso.getMontura();
			pavo.setAmor(7500);
			pavo.setResistencia(7500);
			pavo.setMaxEnergia();
			pavo.setMaxMadurez();
			String str = "El pavo ahora es fecundo";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
			return;

		} else if (comando.equalsIgnoreCase("A")) {
			infos = mensaje.split(" ", 2);
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS(_perso.getNombre() + " : " +  infos[1]);
			return;
		} else if (comando.equalsIgnoreCase("RATEKAMAS")) {
			float cantidad = 0;
			try {
				cantidad = Float.parseFloat(infos[1]);
			} catch (Exception e) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Valor incorrecto");
				return;
			}
			Emu.RATE_KAMAS = cantidad;
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS("El Rate Kamas ha sido modificado a " + cantidad);
			return;
		} else if (comando.equalsIgnoreCase("RATEDROP")) {
			int cantidad = 0;
			try {
				cantidad = Integer.parseInt(infos[1]);
			} catch (Exception e) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Valor incorrecto");
				return;
			}
			Emu.RATE_DROP = cantidad;
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS("El Rate Drop ha sido modificado a " + cantidad);
			return;
		} else if (comando.equalsIgnoreCase("RATEXPPVM")) {
			float cantidad = 0;
			try {
				cantidad = Float.parseFloat(infos[1]);
			} catch (Exception e) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Valor incorrecto");
				return;
			}
			Emu.RATE_XP_PVM = cantidad;
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS("El Rate XP PVM ha sido modificado a " + cantidad);
			return;
		} else if (comando.equalsIgnoreCase("RATEXPPVP")) {
			float cantidad = 0;
			try {
				cantidad = Float.parseFloat(infos[1]);
			} catch (Exception e) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Valor incorrecto");
				return;
			}
			Emu.RATE_XP_PVP = cantidad;
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS("El Rate XP PVP ha sido modificado a " + cantidad);
			return;
		} else if (comando.equalsIgnoreCase("RATEXPOFICIO")) {
			float cantidad = 0;
			try {
				cantidad = Float.parseFloat(infos[1]);
			} catch (Exception e) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Valor incorrecto");
				return;
			}
			Emu.RATE_XP_OFICIO = cantidad;
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS("El Rate XP Oficio ha sido modificado a " + cantidad);
			return;
		} else if (comando.equalsIgnoreCase("RATECRIANZA")) {
			float cantidad = 0;
			try {
				cantidad = Float.parseFloat(infos[1]);
			} catch (Exception e) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Valor incorrecto");
				return;
			}
			Emu.RATE_CRIANZA_PAVOS = cantidad;
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS(
					"El Rate Crianza de Pavos ha sido modificado a " + cantidad);
			return;
		} else if (comando.equalsIgnoreCase("RATEHONOR")) {
			float cantidad = 0;
			try {
				cantidad = Float.parseFloat(infos[1]);
			} catch (Exception e) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Valor incorrecto");
				return;
			}
			Emu.RATE_HONOR = cantidad;
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS("El Rate XP Honor ha sido modificado a " + cantidad);
			return;
		} else if (comando.equalsIgnoreCase("RATEPORCFM")) {
			float cantidad = 0;
			try {
				cantidad = Float.parseFloat(infos[1]);
			} catch (Exception e) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Valor incorrecto");
				return;
			}
			Emu.RATE_PORC_FM = cantidad;
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS(
					"El Rate Exito de la Forjamagia ha sido modificado a " + cantidad);
			return;
		} else if (comando.equalsIgnoreCase("ADICPJ")) {
			float cantidad = 0;
			try {
				cantidad = Float.parseFloat(infos[1]);
			} catch (Exception e) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Valor incorrecto");
				return;
			}
			Formulas.ADIC_PJ = cantidad;
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "El AdicPJ ha sido cambiado a " + cantidad);
			return;
		} else if (comando.equalsIgnoreCase("ADICMOB")) {
			float cantidad = 0;
			try {
				cantidad = Float.parseFloat(infos[1]);
			} catch (Exception e) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Valor incorrecto");
				return;
			}
			Formulas.ADIC_MOB = cantidad;
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "El AdicMob ha sido cambiado a " + cantidad);
			return;
		} else if (comando.equalsIgnoreCase("ADICCAC")) {
			float cantidad = 0;
			try {
				cantidad = Float.parseFloat(infos[1]);
			} catch (Exception e) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Valor incorrecto");
				return;
			}
			Formulas.ADIC_CAC = cantidad;
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "El AdicCAC ha sido cambiado a " + cantidad);
			return;
		} else if (comando.equalsIgnoreCase("RECIBIR")) {
			String packet = "";
			try {
				packet = infos[1];
			} catch (Exception e) {
			}
			if (packet != "") {
				_perso.getCuenta().getEntradaPersonaje().analizar_Packets(packet, false);
			}
			return;
		} else if (comando.equalsIgnoreCase("PROSPECCIONREQ")) {
			float cantidad = 0;
			try {
				cantidad = Float.parseFloat(infos[1]);
			} catch (Exception e) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Valor incorrecto");
				return;
			}
			Formulas.PROSP_REQ = cantidad;
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "La Prospeccion Requerida ha sido cambiado a " + cantidad);
			return;
		} else if (comando.equalsIgnoreCase("CAMBIARTIPOIA")) {
			int id = 0;
			try {
				id = Integer.parseInt(infos[1]);
			} catch (Exception e) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Valor incorrecto");
				return;
			}
			MobModelo mob = MundoDofus.getMobModelo(id);
			if (mob == null) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Mob no existe");
				return;
			}
			int tipoIA = 0;
			try {
				tipoIA = Integer.parseInt(infos[2]);
			} catch (Exception e) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Valor incorrecto de IA");
				return;
			}
			mob.setTipoInteligencia(tipoIA);
			GestorSQL.ACTUALIZAR_IA_MOB(mob);
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "El mob " + mob.getNombre() + " a cambiado a IA : " + tipoIA);
			return;

		} else if (comando.equalsIgnoreCase("BANEARIP")) {
			Personaje P = null;
			try {
				P = MundoDofus.getPjPorNombreIgnora(infos[1]);
			} catch (Exception e) {
			}
			if (P == null) {
				String str = "El personaje no existe o no esta conectado";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			String ipBaneada = P.getCuenta().getActualIP();
			if (!CentroInfo.compararConIPBaneadas(ipBaneada)) {
				GestorSQL.SELECT_BANIP(ipBaneada);
				CentroInfo.BAN_IP.add(ipBaneada);
				P.getCuenta().setBaneado(true);
				if (GestorSQL.AGREGAR_BANIP(ipBaneada)) {
					GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "La IP " + ipBaneada + " esta baneada.");
				}
				if (P.enLinea()) {
					P.getCuenta().getEntradaPersonaje().salir(false);
					GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "El jugador fue retirado.");
				}
			} else {
				String str = "La IP no existe.";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			return;
		} else if (comando.equalsIgnoreCase("MOVERPERCO")) {
			if (_perso.getPelea() != null) {
				return;
			}
			Recaudador recaudador = null;
			for (Recaudador perco : MundoDofus.getTodosRecaudadores().values()) {
				if (perco.getMapaID() == _perso.getMapa().getID()) {
					recaudador = perco;
					break;
				}
			}
			if (recaudador == null) {
				return;
			}
			recaudador.moverPerco();
			recaudador.setEnRecolecta(false);
			return;
		} else {
			GM_lvl_3(comando, infos, mensaje);
		}
	}

	private static int mapa1 = 0;
	private static int celda1 = 0;
	private static int mapa2 = 0;
	private static int celda2 = 0;

	private void GM_lvl_5(String comando, String[] infos, String mensaje) {
		if (_cuenta.getRango() < 5) {
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "NO TIENES EL RANGO REQUERIDO PARA ESTA ACCION.");
			return;
		}
		if (comando.equalsIgnoreCase("REGALARPUNTOS")) {
			int puntos = 0;
			try {
				puntos = Integer.parseInt(infos[1]);
			} catch (Exception e) {
			}
			Personaje objetivo = _perso;
			if (infos.length > 2) {
				objetivo = MundoDofus.getPjPorNombre(infos[2]);
				if (objetivo == null) {
					GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "El personaje no existe o no esta conectado");
					return;
				}
			}
			int cuentaID = objetivo.getCuentaID();
			GestorSQL.setPuntoCuenta(GestorSQL.getPuntosCuenta(cuentaID) + puntos, cuentaID);
			String str = "Se le ha agregado " + puntos + " puntos de tienda a " + objetivo.getNombre();
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
			return;
		} else if (comando.equalsIgnoreCase("ENVIAR")) {
			String msgg1 = "";
			String msgg2 = "";
			String msgg3 = "";
			String msgg4 = "";
			try {
				msgg1 = infos[1];
			} catch (Exception e) {
			}
			try {
				msgg2 = infos[2];
			} catch (Exception e) {
			}
			try {
				msgg3 = infos[3];
			} catch (Exception e) {
			}
			try {
				msgg4 = infos[4];
			} catch (Exception e) {
			}
			String msj = msgg1 + " " + msgg2 + " " + msgg3 + " " + msgg4;
			GestorSalida.enviar(_perso, msj);
			msj = msj.replace("�", "|");
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Has enviado al socket " + msj);
			return;
		} else if (comando.equalsIgnoreCase("CELDATP")) {
			boolean limpia = false;
			if (mapa1 == 0) {
				mapa1 = _perso.getMapa().getID();
				celda1 = _perso.getCelda().getID();
			} else if (mapa2 == 0) {
				mapa2 = _perso.getMapa().getID();
				celda2 = _perso.getCelda().getID();
				limpia = true;
			}
			String str = "OK";
			if (mapa1 != 0 && mapa2 != 0) {
				GestorSQL.INSERT_CELDASTP(mapa1, celda1, mapa2, celda2);
				str = "agregado";
			}
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
			if (limpia) {
				MundoDofus.getMapa(mapa1).getCelda(celda1).addAccionEnUnaCelda(0, mapa2 + "," + celda2, "");
				mapa1 = 0;
				celda1 = 0;
				mapa2 = 0;
				celda2 = 0;
			}
			return;
		} else {
			GM_lvl_4(comando, infos, mensaje);
		}
	}
}
package servidor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.swing.Timer;

import estaticos.CentroInfo;
import estaticos.Comandos;
import estaticos.CondicionJugador;
import estaticos.Emu;
import estaticos.Encriptador;
import estaticos.Formulas;
import estaticos.GestorSQL;
import estaticos.GestorSalida;
import estaticos.MundoDofus;
import estaticos.MundoDofus.Intercambio;
import estaticos.MundoDofus.InvitarTaller;
import servidor.EntradaPersonaje.AccionDeJuego;
import variables.Accion;
import variables.Casa;
import variables.Cofre;
import variables.Cuenta;
import variables.Dragopavo;
import variables.EfectoHechizo;
import variables.Encarnacion;
import variables.Gremio;
import variables.Gremio.MiembroGremio;
import variables.Hechizo.StatsHechizos;
import variables.Idiomas;
import variables.Koliseo;
import variables.LibrosRunicos;
import variables.Mapa;
import variables.Mapa.Celda;
import variables.Mapa.Cercado;
import variables.Mapa.ObjetoInteractivo;
import variables.Mascota;
import variables.Mercadillo;
import variables.Mercadillo.ObjetoMercadillo;
import variables.Misiones;
import variables.NPCModelo;
import variables.NPCModelo.NPC;
import variables.NPCModelo.PreguntaNPC;
import variables.NPCModelo.RespuestaNPC;
import variables.Objeto;
import variables.Objeto.ObjetoModelo;
import variables.Objevivo;
import variables.Oficio;
import variables.Oficio.AccionTrabajo;
import variables.Oficio.StatsOficio;
import variables.Pelea;
import variables.Pelea.Luchador;
import variables.Personaje;
import variables.Personaje.Grupo;
import variables.Prisma;
import variables.Recaudador;
import variables.Tienda;

public class Estaticos {


	private static int comidaAño;

	static void cambiarNombre(String packetx, Personaje _perso, PrintWriter _out) {
		String packet = "";
		try {
			packet = packetx.split("\\|")[1];
		} catch (Exception e) {
			packet = packetx;
		}
		if (GestorSQL.personajeYaExiste(packet)) {
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out, Idiomas.getTexto(_perso.getCuenta().idioma, 0));
			GestorSalida.ENVIAR_AlE_CAMBIAR_NOMBRE(_out, "r");
			GestorSalida.ENVIAR_AlE_CAMBIAR_NOMBRE(_out, "r");
			GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso);
			return;
		}
		boolean esValido = true;
		String nombre = packet.toLowerCase();
		if (nombre.length() > 20) {
			esValido = false;
		}
		if (esValido) {
			int tiretCount = 0;
			char exLetterA = ' ';
			char exLetterB = ' ';
			for (char curLetter : nombre.toCharArray()) {
				if (!((curLetter >= 'a' && curLetter <= 'z') || curLetter == '-') || (curLetter == exLetterA && curLetter == exLetterB)) {
					esValido = false;
					break;
				}
				if (curLetter >= 'a' && curLetter <= 'z') {
					exLetterA = exLetterB;
					exLetterB = curLetter;
				}
				if (curLetter == '-') {
					if (tiretCount >= 1) {
						esValido = false;
						break;
					} else {
						tiretCount++;
					}
				}
			}
		}
		if (!esValido) {
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out, Idiomas.getTexto(_perso.getCuenta().idioma, 0));
			GestorSalida.ENVIAR_AlE_CAMBIAR_NOMBRE(_out, "r");
			GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso);
			return;
		} // 11159+
		if (!_perso.tieneObjModeloNoEquip(11159, 1)) {
			return;
		}
		_perso.removerObjetoPorModYCant(11159, 1);
		if (MundoDofus.PersonajesPorNombre.containsKey(_perso.getNombre())) {
			MundoDofus.PersonajesPorNombre.remove(_perso.getNombre());
			MundoDofus.PersonajesPorNombre.put(packet, _perso);
		}
		_perso.setNombre(packet);
		GestorSalida.ENVIAR_AlE_CAMBIAR_NOMBRE(_out, "r");
		GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out, Idiomas.getTexto(_perso.getCuenta().idioma, 1) + packet);
		GestorSalida.ENVIAR_GM_REFRESCAR_PJ_EN_MAPA(_perso.getMapa(), _perso);
		GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
		return;
	}

	static void analizar_Tutoriales(String packet, Personaje _perso, PrintWriter _out) {
		// String[] param = packet.split("\\|");
		// Tutorial tuto = _perso.getTutorial();
		_perso.setTutorial(null);
		switch (packet.charAt(1)) {
		case 'V':// fin de tutorial
			/*
			 * if (packet.charAt(2) != '0' && packet.charAt(2) != '4') { try { int index =
			 * packet.charAt(2) - 1; tuto.getRecompensa().get(index).aplicar(_perso, null,
			 * -1, -1); } catch (Exception e) { Emu.creaLogs(e); } } try { if (tuto.getFin()
			 * != null) tuto.getFin().aplicar(_perso, null, -1, -1); } catch (Exception e) {
			 * Emu.creaLogs(e); } _perso.setOcupado(false);
			 * GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso); try {
			 * _perso.setOrientacion(Integer.parseInt(param[2]));
			 * _perso.setCelda(_perso.getMapa().getCelda(Integer.parseInt(param[1]))); }
			 * catch (Exception e) { Emu.creaLogs(e); }
			 */
			break;
		}
	}

	static void analizar_Conquista(String packet, Personaje _perso, PrintWriter _out) {
		switch (packet.charAt(1)) {
		case 'b':// balance de mundo y area
			try {
				GestorSalida.ENVIAR_Cb_BALANCE_CONQUISTA(_perso, MundoDofus.getBalanceMundo(_perso.getAlineacion())
						+ ";"
						+ MundoDofus.getBalanceArea(_perso.getMapa().getSubArea().getArea(), _perso.getAlineacion()));
			} catch (NullPointerException e) {
				Emu.creaLogs(e);
			}
			break;
		case 'B':// bonus de alineacion
			try {
				double porc = MundoDofus.getBalanceMundo(_perso.getAlineacion());
				double porcN = Math.rint((_perso.getNivelAlineacion() / 2.5) + 1);
				GestorSalida.ENVIAR_CB_BONUS_CONQUISTA(_perso, porc + "," + porc + "," + porc + ";" + porcN + ","
						+ porcN + "," + porcN + ";" + porc + "," + porc + "," + porc);
			} catch (Exception e) {
				Emu.creaLogs(e);
			}
			break;
		case 'W':// info de mapa sobre los mapas
			conquista_Geoposicion(packet, _perso, _out);
			break;
		case 'I':// Modificacion de precio de venta
			conquista_Defensa(packet, _perso, _out);
			break;
		case 'F':// Cerrar ventana de compra
			conquista_Unirse_Defensa_Prisma(packet, _perso, _out);
			break;
		}
	}

	private static void conquista_Defensa(String packet, Personaje _perso, PrintWriter _out) {
		switch (packet.charAt(2)) {
		case 'J':// info de prismas defensa
			try {
				Prisma prisma = MundoDofus.getPrisma(_perso.getMapa().getSubArea().getPrismaID());
				if (prisma == null) {
					_perso.abrirMenuPrisma();
				} else {
					int peleas = 0;
					for (Prisma pr : MundoDofus.TodosPrismas()) {
						if (pr.getPelea() != null) {
							if (pr.getPelea().getEstado() == 2) {
								peleas += 1;
							}
						}
					}
					if (peleas > 1 || prisma.getSubArea() != _perso.getMapa().getSubArea()
							|| prisma.getPelea() == null) {
						_perso.abrirMenuPrisma();
					} else {
						Prisma.analizarAtaque(_perso);
						Prisma.analizarDefensa(_perso);
						Thread.sleep(300);
						GestorSalida.ENVIAR_CIJ_INFO_UNIRSE_PRISMA(_perso, _perso.analizarPrismas(prisma.getID()));
					}
				}
			} catch (Exception e) {
				Emu.creaLogs(e);
			}
			break;
		case 'V':
			GestorSalida.ENVIAR_CIV_CERRAR_INFO_CONQUISTA(_perso);
			break;
		}
	}

	private static void conquista_Geoposicion(String packet, Personaje _perso, PrintWriter _out) {
		switch (packet.charAt(2)) {
		case 'J':// info de prismas defensa
			try {
				GestorSalida.ENVIAR_CW_INFO_MUNDO_CONQUISTA(_perso,
						MundoDofus.prismasGeoposicion(_perso.getAlineacion()));
			} catch (Exception e) {
				Emu.creaLogs(e);
			}
			break;
		case 'V': // TODO: Mirar esto
			// GestorSalida.ENVIAR_CIV_CERRAR_INFO_CONQUISTA(_perso);
			break;
		}
	}

	private static void conquista_Unirse_Defensa_Prisma(String packet, Personaje _perso, PrintWriter _out) {
		switch (packet.charAt(2)) {
		case 'J':// info de prismas defensa
			try {
				int prismaID = _perso.getMapa().getSubArea().getPrismaID();
				Prisma prisma = MundoDofus.getPrisma(prismaID);
				if (prisma == null) {
					_perso.abrirMenuPrisma();
					return;
				}
				int mapaID = prisma.getMapa();
				int celdaID = prisma.getCelda();
				if ((prisma.getAlineacion() != _perso.getAlineacion()) || (_perso.getPelea() != null)) {
					return;
				}
				if (prisma.getPelea().checkUnirsePrisma(_perso)) {
					if (_perso.getMapa().getID() != mapaID) {
						_perso.setMapaDefPerco(_perso.getMapa());
						_perso.setCeldaDefPerco(_perso.getCelda());
						_perso.esperaPeleaPrisma = prisma;
						if (_perso.getMapa().getID() != mapaID) {
							_perso.teleport(mapaID, celdaID);
						}
					} else {
						if (prisma.getPelea().unirsePeleaPrisma(_perso, prismaID, mapaID, celdaID)) {
							for (Personaje z : MundoDofus.getPJsEnLinea()) {
								if (z == null || z.getAlineacion() != _perso.getAlineacion()) {
									continue;
								}
								Prisma.analizarDefensa(z);
							}
						}
					}
				}
			} catch (Exception e) {
				Emu.creaLogs(e);
			}
			break;
		}
	}

	static void analizar_Misiones(String packet, Personaje _perso, PrintWriter _out) {
		switch (packet.charAt(1)) {
		case 'L':// balance de mundo y area
			GestorSalida.ENVIAR_QL_LISTA_MISIONES(_perso);
			break;
		case 'S':// bonus de alineacion
			GestorSalida.ENVIAR_QS_PASOS_RECOMPENSA_MISION(_perso, Integer.parseInt(packet.substring(2)));
			break;
		}
	}

	static void analizar_Casas(String packet, Personaje _perso, PrintWriter _out) {
		switch (packet.charAt(1)) {
		case 'B':// Comprar casa
			packet = packet.substring(2);
			Casa.comprarCasa(_perso);
			break;
		case 'G':// Casa de Gremio
			packet = packet.substring(2);
			if (packet.isEmpty()) {
				packet = null;
			}
			Casa.analizarCasaGremio(_perso, packet);
			break;
		case 'Q':// Quitar/Expulsar de la casa
			packet = packet.substring(2);
			Casa.salir(_perso, packet);
			break;
		case 'S':// Modificacion de precio de venta
			packet = packet.substring(2);
			Casa.precioVenta(_perso, packet);
			break;
		case 'V':// Cerrar ventana de compra
			Casa.cerrarVentanaCompra(_perso);
			break;
		}
	}

	static void analizar_Claves(String packet, Personaje _perso, PrintWriter _out) {
		switch (packet.charAt(1)) {
		case 'V':// Cerrar ventana de codigo
			Casa.cerrarVentana(_perso);
			break;
		case 'K':// Envio de codigo
			casa_Codigo(packet, _perso, _out);
			break;
		}
	}

	private static void casa_Codigo(String packet, Personaje _perso, PrintWriter _out) {
		switch (packet.charAt(2)) {
		case '0':// Envio de codigo
			packet = packet.substring(4);
			if (_perso.getCofre() != null) {
				Cofre.abrirCofre(_perso, packet, false);
			} else {
				Casa.abrirCasa(_perso, packet, false);
			}
			break;
		case '1':// Cambio de codigo
			packet = packet.substring(4);
			if (_perso.getCofre() != null) {
				Cofre.codificarCofre(_perso, packet);
			} else {
				Casa.codificarCasa(_perso, packet);
			}
			break;
		}
	}

	static void analizar_Enemigos(String packet, Personaje _perso, PrintWriter _out, Cuenta _cuenta) {
		switch (packet.charAt(1)) {
		case 'A':// agregar
			enemigo_Agregar(packet, _perso, _out, _cuenta);
			break;
		case 'D':// Deletrear
			enemigo_Borrar(packet, _perso, _out, _cuenta);
			break;
		case 'L':// Lista
			GestorSalida.ENVIAR_iL_LISTA_ENEMIGOS(_perso);
			break;
		}
	}

	private static void enemigo_Agregar(String packet, Personaje _perso, PrintWriter _out, Cuenta _cuenta) {
		if (_perso == null) {
			return;
		}
		int guid = -1;
		switch (packet.charAt(2)) {
		case '%':// Numero de jugadores
			packet = packet.substring(3);
			Personaje P = MundoDofus.getPjPorNombre(packet);
			if (P == null) {
				GestorSalida.ENVIAR_FD_BORRAR_AMIGO(_perso, "Ef");
				return;
			}
			guid = P.getCuentaID();
			break;
		case '*':// Numero de cuentas
			packet = packet.substring(3);
			Cuenta C = MundoDofus.getCuentaPorApodo(packet);
			if (C == null) {
				GestorSalida.ENVIAR_FD_BORRAR_AMIGO(_perso, "Ef");
				return;
			}
			guid = C.getID();
			break;
		default:
			packet = packet.substring(2);
			if (MundoDofus.idDebots.containsKey(packet)) {
				GestorSalida.ENVIAR_iA_AGREGAR_ENEMIGO(_perso, "Ea");
				return;
			}
			Personaje Pr = MundoDofus.getPjPorNombre(packet);
			if (Pr == null ? true : !Pr.enLinea()) {
				GestorSalida.ENVIAR_FD_BORRAR_AMIGO(_perso, "Ef");
				return;
			}
			guid = Pr.getCuenta().getID();
			break;
		}
		_cuenta.addEnemigo(packet, guid);
	}

	private static void enemigo_Borrar(String packet, Personaje _perso, PrintWriter _out, Cuenta _cuenta) {
		int id = -1;
		switch (packet.charAt(2)) {
		case '%':// Nombre de jugador
			packet = packet.substring(3);
			Personaje pj = MundoDofus.getPjPorNombre(packet);
			if (pj == null) {
				GestorSalida.ENVIAR_FD_BORRAR_AMIGO(_perso, "Ef");
				return;
			}
			id = pj.getCuentaID();
			break;
		case '*':// apodo
			packet = packet.substring(3);
			Cuenta cuenta = MundoDofus.getCuentaPorApodo(packet);
			if (cuenta == null) {
				GestorSalida.ENVIAR_FD_BORRAR_AMIGO(_perso, "Ef");
				return;
			}
			id = cuenta.getID();
			break;
		default:
			packet = packet.substring(2);
			Personaje perso = MundoDofus.getPjPorNombre(packet);
			if (perso == null ? true : !perso.enLinea()) {
				GestorSalida.ENVIAR_FD_BORRAR_AMIGO(_perso, "Ef");
				return;
			}
			id = perso.getCuenta().getID();
			break;
		}
		_cuenta.borrarEnemigo(id);
	}

	static void analizar_Oficios(String packet, Personaje _perso) {
		switch (packet.charAt(1)) {
		case 'O':
			String[] infos = packet.substring(2).split("\\|");
			int posOficio = Integer.parseInt(infos[0]);
			int opciones = Integer.parseInt(infos[1]);
			int slots = Integer.parseInt(infos[2]);
			StatsOficio statOficio = _perso.getStatsOficios().get(posOficio);
			if (statOficio == null) {
				return;
			}
			statOficio.setOpciones(opciones);
			statOficio.setSlotsPublico(slots);
			GestorSalida.ENVIAR_JO_OFICIO_OPCIONES(_perso, statOficio);
			break;
		}
	}

	static void analizar_Areas(String packet, Personaje _perso, PrintWriter _out) {
		switch (packet.charAt(1)) {
		case 'U':
			zaap_Usar(packet, _perso);
			break;
		case 'u':
			zaapi_Usar(packet, _perso, _out);
			break;
		case 'v':
			zaapi_Cerrar(_perso);
			break;
		case 'V':
			zaap_Cerrar(_perso);
			break;
		case 'w':
			prisma_Cerrar(_perso);
			break;
		case 'p':
			prisma_Usar(packet, _perso, _out);
			break;
		}
	}

	//analizar zonas de teleport con comando
	static void analizar_Zonas( String packet, Personaje _perso) {
		if (_perso.getPelea() != null) {
			GestorSalida.ENVIAR_BN_NADA(_perso);
			return;
		}
		switch (packet.charAt(1)) {
		case 'U':
			try {
				_perso.usarZonas(Short.parseShort(packet.substring(2)));
			} catch (final Exception ignored) {
			}
		case 'V':
			GestorSalida.ENVIAR_zV_CERRAR_ZONAS(_perso);
			break;
		/*default:
			MainServidor.redactarLogServidorln(getStringDesconocido() + " ANALIZAR ZONAS: " + packet);
			if (_excesoPackets > MainServidor.MAX_PACKETS_DESCONOCIDOS) {
				MainServidor.redactarLogServidorln("El IP del socket que intenta usar packet desconocidos: " + _IP);
				cerrarSocket(true, "analizar_Zonas()");
			}
			if (MainServidor.MODO_DESCONECTAR_PACKET_MALO) {
				MainServidor.redactarLogServidorln("El IP del socket que intenta usar packet desconocidos: " + _IP);
				cerrarSocket(true, "analizar_Zonas()");
			}
			break;*/
		}
	}
	//analizar zonas de teleport con comando

	private static void zaapi_Cerrar(Personaje _perso) {
		_perso.cerrarZaapi();
	}

	private static void prisma_Cerrar(Personaje _perso) {
		_perso.cerrarPrisma();
	}

	private static void zaapi_Usar(String packet, Personaje _perso, PrintWriter _out) {
		if (_perso.getDeshonor() >= 2) {
			GestorSalida.ENVIAR_Im_INFORMACION(_out, "183");
			return;
		}
		_perso.usarZaapi(packet);
	}

	private static void prisma_Usar(String packet, Personaje _perso, PrintWriter _out) {
		if (_perso.getDeshonor() >= 2) {
			GestorSalida.ENVIAR_Im_INFORMACION(_out, "183");
			return;
		}
		_perso.usarPrisma(packet);
	}

	private static void zaap_Cerrar(Personaje _perso) {
		_perso.cerrarZaap();
	}

	private static void zaap_Usar(String packet, Personaje _perso) {
		short id = -1;
		try {
			id = Short.parseShort(packet.substring(2));
		} catch (Exception e) {
			Emu.creaLogs(e);
		}
		if (id == -1) {
			return;
		}
		_perso.usarZaap(id);
	}

	static void analizar_Gremio(String packet, Personaje _perso, PrintWriter _out, Cuenta _cuenta) {
		switch (packet.charAt(1)) {
		case 'B':// Stats
			gremio_Stats(packet, _perso);
			break;
		case 'b':// hechizos
			gremio_Hechizos(packet, _perso);
			break;
		case 'C':// Creacion
			gremio_Crear(packet, _perso, _out);
			break;
		case 'f':// Teleport a cercado de gremio
			gremio_Cercado(packet.substring(2), _perso, _out);
			break;
		case 'F':// Retirar recaudador
			gremio_Borrar_Recaudador(packet.substring(2), _perso);
			break;
		case 'h':// Teleport a casa del gremio
			gremio_Casa(packet.substring(2), _perso, _out);
			break;
		case 'H':// Poner recaudador
			gremio_Poner_Recaudador(_perso, _out);
			break;
		case 'I':// Infos
			gremio_Informacion(packet.charAt(2), _perso);
			break;
		case 'J':// unir al gremio
			gremio_Unirse(packet.substring(2), _perso, _out);
			break;
		case 'K':// quitar del gremio
			gremio_Expulsar(packet.substring(2), _perso);
			break;
		case 'P':// Promover de rango
			gremio_Promover_Rango(packet.substring(2), _perso);
			break;
		case 'T':// unirse al ataq de un recaudador
			gremio_Unirse_Pelea_Recaudador(packet.substring(2), _perso);
			break;
		case 'V':// cerrar panel de creacion del gremio
			GestorSalida.ENVIAR_gV_CERRAR_PANEL_GREMIO(_perso);
			break;
		}
	}

	private static void gremio_Stats(String packet, Personaje _perso) {
		if (_perso.getGremio() == null) {
			return;
		}
		Gremio G = _perso.getGremio();
		if (!_perso.getMiembroGremio().puede(CentroInfo.G_MODIFBOOST)) {
			return;
		}
		switch (packet.charAt(2)) {
		case 'p':// Prospeccion
			if ((G.getCapital() < 1) || (G.getStats(176) >= 500)) {
				return;
			}
			G.setCapital(G.getCapital() - 1);
			G.actualizarStats(176, 1);
			break;
		case 'x':// Sabiduria
			if (G.getCapital() < 1) {
				return;
			}
			if (G.getStats(124) >= 400) {
				return;
			}
			G.setCapital(G.getCapital() - 1);
			G.actualizarStats(124, 1);
			break;
		case 'o':// Pod
			if (G.getCapital() < 1) {
				return;
			}
			if (G.getStats(158) >= 5000) {
				return;
			}
			G.setCapital(G.getCapital() - 1);
			G.actualizarStats(158, 20);
			break;
		case 'k':// Numero de recaudadores
			if (G.getCapital() < 10) {
				return;
			}
			if (G.getNroRecau() >= 50) {
				return;
			}
			G.setCapital(G.getCapital() - 10);
			G.setNroRecau(G.getNroRecau() + 1);
			break;
		}
		GestorSQL.ACTUALIZAR_GREMIO(G);
		GestorSalida.GAME_SEND_gIB_PACKET(_perso, _perso.getGremio().analizarRecauAGrmio());
	}

	private static void gremio_Hechizos(String packet, Personaje _perso) {
		if (_perso.getGremio() == null) {
			return;
		}
		Gremio G2 = _perso.getGremio();
		if (!_perso.getMiembroGremio().puede(CentroInfo.G_MODIFBOOST)) {
			return;
		}
		int spellID = Integer.parseInt(packet.substring(2));
		if (G2.getHechizos().containsKey(spellID)) {
			if (G2.getCapital() < 5) {
				return;
			}
			G2.setCapital(G2.getCapital() - 5);
			G2.boostHechizo(spellID);
			GestorSQL.ACTUALIZAR_GREMIO(G2);
			GestorSalida.GAME_SEND_gIB_PACKET(_perso, _perso.getGremio().analizarRecauAGrmio());
		}
	}

	private static void gremio_Unirse_Pelea_Recaudador(String packet, Personaje _perso) {
		switch (packet.charAt(0)) {
		case 'J':// unir
			int recauID = Integer.parseInt(packet.substring(1));
			Recaudador recau = MundoDofus.getRecaudador(recauID);
			if (recau == null || _perso.getPelea() != null) {
				return;
			}
			short mapaID = (short) recau.getMapaID();
			int celdaID = recau.getCeldalID();
			if (recau.getPelea().checkUnirsePerco(_perso)) {
				if (_perso.getMapa().getID() != mapaID) {
					_perso.setMapaDefPerco(_perso.getMapa());
					_perso.setCeldaDefPerco(_perso.getCelda());
					_perso.esperaPeleaRec = recau;
					if (_perso.getMapa().getID() != mapaID) {
						_perso.teleport(mapaID, celdaID);
					}
				} else {
					if (recau.getPelea().unirsePeleaRecaudador(_perso, recauID, mapaID, celdaID)) {
						for (Personaje miembros : _perso.getGremio().getPjMiembros()) {
							if (miembros == null || !miembros.enLinea()) {
								continue;
							}
							Recaudador.analizarDefensa(miembros, _perso.getGremio().getID());
						}
					}
				}
			}
			break;
		}
	}

	private static void gremio_Borrar_Recaudador(String packet, Personaje _perso) {
		if ((_perso.getGremio() == null) || !_perso.getMiembroGremio().puede(CentroInfo.G_RECOLECTARRECAUDADOR)) {
			return;
		}
		int IDPerco = Integer.parseInt(packet);
		Recaudador recau = MundoDofus.getRecaudador(IDPerco);
		if (recau == null || recau.getEstadoPelea() > 0) {
			return;
		}
		if (_perso.getGremio().getID() != recau.getGremioID()) {
			return;
		}
		GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(_perso.getMapa(), IDPerco);
		Gremio gremio = _perso.getGremio();
		Mapa mapa = _perso.getMapa();
		gremio.addUltRecolectaMapa((short)mapa.getID());
		recau.borrarRecauPorRecolecta(recau.getID(), _perso);

		for (Personaje z : _perso.getGremio().getPjMiembros()) {
			if (z == null) {
				continue;
			}
			if (z.enLinea()) {
				GestorSalida.ENVIAR_gITM_INFO_RECAUDADOR(z, Recaudador.analizarRecaudadores(z.getGremio().getID()));
				GestorSalida.GAME_SEND_gT_PACKET(z,
						"R" + recau.getN1() + "," + recau.getN2() + "|" + recau.getMapaID() + "|"
								+ MundoDofus.getMapa((short) recau.getMapaID()).getCoordX() + "|"
								+ MundoDofus.getMapa((short) recau.getMapaID()).getCoordY() + "|" + _perso.getNombre());
			}
		}
	}

	private static void gremio_Poner_Recaudador(Personaje _perso, PrintWriter _out) {
		Gremio gremio = _perso.getGremio();
		if ((gremio == null) || !_perso.getMiembroGremio().puede(CentroInfo.G_PONERRECAUDADOR) || (gremio.getPjMiembros().size() < 10)) {
			return;
		}
		short precio = (short) (1000 + 10 * gremio.getNivel());
		if (_perso.getKamas() < precio) {
			GestorSalida.ENVIAR_Im_INFORMACION(_out, "182");
			return;
		}
		Mapa mapa = _perso.getMapa();
		if (mapa == null) {
			return;
		}
		if (Recaudador.getIDGremioPorMapaID(mapa.getID()) > 0) {
			GestorSalida.ENVIAR_Im_INFORMACION(_out, "1168;1");
			return;
		}

		if (!gremio.puedePonerRecaudadorMapa((short)mapa.getID(), _perso)) {
			//GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, "ATENCION, no puedes colocar un recaudador en este mapa hasta que pasen 2 horas.", Colores.ROJO);
			return;
		}

		if (mapa.getID() == 31998) {
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, "ATENCION, no puedes colocar un recaudador en Mapas de RAID.", Colores.ROJO);
			return;
		}

		for (Casa cas : MundoDofus.getCasas().values()) {
			if (cas.getMapasContenidos().contains(mapa.getID())) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, "No puedes poner recaudadores en las casas", Colores.ROJO);
				return;
			}
		}
		if (mapa.getLugaresString().length() < 5 || mapa.getID() == 11095) {
			GestorSalida.ENVIAR_Im_INFORMACION(_out, "113");
			return;
		}
		if (MundoDofus.cantRecauDelGremio(gremio.getID()) >= gremio.getNroRecau()) {
			return;
		}
		String random1 = Integer.toString(Formulas.getRandomValor(1, 129), 36);
		String random2 = Integer.toString(Formulas.getRandomValor(1, 227), 36);
		int id = GestorSQL.getSigIDRecaudador();
		Recaudador recaudador = new Recaudador(id, mapa.getID(), _perso.getCelda().getID(), (byte) 3, gremio.getID(),
				random1, random2, "", 0, 0);
		MundoDofus.addRecaudador(recaudador);
		GestorSalida.ENVIAR_GM_AGREGAR_RECAUDADOR_AL_MAPA(mapa);
		GestorSQL.ADD_RECAUDADOR_EN_MAPA(id, mapa.getID(), gremio.getID(), _perso.getCelda().getID(), 3, random1,
				random2);
		for (Personaje z : gremio.getPjMiembros()) {
			if (z != null && z.enLinea()) {
				GestorSalida.ENVIAR_gITM_INFO_RECAUDADOR(z, Recaudador.analizarRecaudadores(gremio.getID()));
				GestorSalida.GAME_SEND_gT_PACKET(z,
						"S" + recaudador.getN1() + "," + recaudador.getN2() + "|" + recaudador.getMapaID() + "|"
								+ MundoDofus.getMapa((short) recaudador.getMapaID()).getCoordX() + "|"
								+ MundoDofus.getMapa((short) recaudador.getMapaID()).getCoordY() + "|"
								+ _perso.getNombre());
			}
		}
	}

	private static void gremio_Cercado(String packet, Personaje _perso, PrintWriter _out) {
		if (_perso == null) {
			return;
		}
		if (_perso.getGremio() == null) {
			GestorSalida.ENVIAR_Im_INFORMACION(_out, "1135");
			return;
		}
		short mapaID = Short.parseShort(packet);
		Cercado MP = MundoDofus.getMapa(mapaID).getCercado();
		if (MP == null) {
			return;
		}
		if (MP.getGremio().getID() != _perso.getGremio().getID()) {
			GestorSalida.ENVIAR_Im_INFORMACION(_out, "1135");
			return;
		}
		int celdaID = MundoDofus.getCeldaCercadoPorMapaID(mapaID);
		if (_perso.tieneObjModeloNoEquip(9035, 1)) {
			if (_perso.getMapa().getID() != mapaID) {
				_perso.removerObjetoPorModYCant(9035, 1);
				_perso.teleport(mapaID, celdaID);
			}
		} else {
			GestorSalida.ENVIAR_Im_INFORMACION(_out, "1159");
			return;
		}
	}

	private static void gremio_Casa(String packet, Personaje _perso, PrintWriter _out) {
		if (_perso.getGremio() == null) {
			GestorSalida.ENVIAR_Im_INFORMACION(_out, "1135");
			return;
		}
		if (_perso.getPelea() != null || _perso.estaOcupado()) {
			return;
		}
		int HouseID = Integer.parseInt(packet);
		Casa h = MundoDofus.getCasas().get(HouseID);
		if (h == null) {
			return;
		}
		if (_perso.getGremio().getID() != h.getGremioID()) {
			GestorSalida.ENVIAR_Im_INFORMACION(_out, "1135");
			return;
		}
		if (!h.tieneDerecho(CentroInfo.H_TELEPORTGREMIO)) {
			GestorSalida.ENVIAR_Im_INFORMACION(_out, "1136");
			return;
		}
		if (_perso.tieneObjModeloNoEquip(8883, 1)) {
			_perso.removerObjetoPorModYCant(8883, 1);
			_perso.teleport((short) h.getMapaIDDentro(), h.getCeldaIDDentro());
		} else {
			GestorSalida.ENVIAR_Im_INFORMACION(_out, "1137");
			return;
		}
	}

	private static void gremio_Promover_Rango(String packet, Personaje _perso) {
		if (_perso.getGremio() == null) {
			return;
		}
		String[] infos = packet.split("\\|");
		int id = Integer.parseInt(infos[0]);
		int rango = Integer.parseInt(infos[1]);
		byte xpDonada = Byte.parseByte(infos[2]);
		int derecho = Integer.parseInt(infos[3]);
		Personaje perso = MundoDofus.getPersonaje(id);
		MiembroGremio cambiador = _perso.getMiembroGremio();
		if ((perso == null) || (perso.getGremio() == null)) {
			GestorSalida.ENVIAR_BN_NADA(_perso);
			return;
		}
		if (_perso.getGremio().getID() != perso.getGremio().getID()) {
			GestorSalida.ENVIAR_gK_GREMIO_BAN(_perso, "Ea");
			return;
		}
		MiembroGremio aCambiar = perso.getMiembroGremio();
		if (cambiador.getRango() == 1) {
			if (cambiador.getID() == aCambiar.getID()) {
				rango = -1;
				derecho = -1;
			} else if (rango == 1) {
				cambiador.setTodosDerechos(2, (byte) -1, 29694);
				rango = 1;
				xpDonada = -1;
				derecho = 1;
			}
		} else {
			if (aCambiar.getRango() == 1) {
				rango = -1;
				derecho = -1;
			} else {
				if ((!cambiador.puede(CentroInfo.G_MODRANGOS)) || (rango == 1)) {
					rango = -1;
				}
				if ((!cambiador.puede(CentroInfo.G_MODIFDERECHOS)) || (derecho == 1)) {
					derecho = -1;
				}
				if ((!cambiador.puede(CentroInfo.G_SUXPDONADA)) && (!cambiador.puede(CentroInfo.G_TODASXPDONADAS))
						&& (cambiador.getID() == aCambiar.getID())) {
					xpDonada = -1;
				}
			}
			if ((!cambiador.puede(CentroInfo.G_TODASXPDONADAS)) && (!cambiador.equals(aCambiar))) {
				xpDonada = -1;
			}
		}
		aCambiar.setTodosDerechos(rango, xpDonada, derecho);
		GestorSalida.GAME_SEND_gS_PACKET(_perso, _perso.getMiembroGremio());
		if ((perso != null) && (perso.getID() != _perso.getID())) {
			GestorSalida.GAME_SEND_gS_PACKET(perso, perso.getMiembroGremio());
		}
	}

	private static void gremio_Expulsar(String nombre, Personaje _perso) {
		if (_perso.getGremio() == null) {
			return;
		}
		Personaje perso = MundoDofus.getPjPorNombreIgnora(nombre);
		if (perso == null) {
			GestorSalida.ENVIAR_BN_NADA(_perso);
			return;
		}
		Gremio gremio = perso.getGremio();
		if (gremio == null) {
			gremio = MundoDofus.getGremio(_perso.getGremio().getID());
		}
		MiembroGremio aExpulsar = gremio.getMiembro(perso.getID());
		if ((aExpulsar == null) || (aExpulsar.getGremio().getID() != _perso.getGremio().getID())) {
			GestorSalida.ENVIAR_BN_NADA(_perso);
			return;
		}
		if (gremio.getID() != _perso.getGremio().getID()) {
			GestorSalida.ENVIAR_gK_GREMIO_BAN(_perso, "Ea");
			return;
		}
		MiembroGremio expulsador = _perso.getMiembroGremio();
		if ((!expulsador.puede(CentroInfo.G_BANEAR)) && (expulsador.getID() != aExpulsar.getID())) {
			GestorSalida.ENVIAR_gK_GREMIO_BAN(_perso, "Ed");
			return;
		}
		if (expulsador.getID() != aExpulsar.getID()) {
			if (aExpulsar.getRango() == 1) {
				return;
			}
			gremio.expulsarMiembro(aExpulsar.getPerso());
			if (perso != null) {
				perso.setMiembroGremio(null);
			}
			GestorSalida.ENVIAR_gK_GREMIO_BAN(_perso, "K" + _perso.getNombre() + "|" + nombre);
			if (perso != null && perso.enLinea()) {
				GestorSalida.ENVIAR_gK_GREMIO_BAN(perso, "K" + _perso.getNombre());
			}
		} else {
			if ((expulsador.getRango() == 1) && (gremio.getPjMiembros().size() > 1)) {
				for (Personaje pj : gremio.getPjMiembros()) {
					gremio.expulsarMiembro(pj);
					pj.setMiembroGremio(null);
				}
			} else {
				gremio.expulsarMiembro(_perso);
				_perso.setMiembroGremio(null);
			}
			if (gremio.getPjMiembros().isEmpty()) {
				MundoDofus.borrarGremio(gremio.getID());
			}
			GestorSalida.ENVIAR_gK_GREMIO_BAN(_perso, "K" + nombre + "|" + nombre);
		}
	}

	private static void gremio_Unirse(String packet, Personaje _perso, PrintWriter _out) {
		switch (packet.charAt(0)) {
		case 'R':// Invitar
			Personaje P = MundoDofus.getPjPorNombre(packet.substring(1));
			if (P == null || _perso.getGremio() == null || !P.enLinea()) {
				GestorSalida.GAME_SEND_gJ_PACKET(_perso, "Eu");
				return;
			}
			if (P.estaOcupado()) {
				GestorSalida.GAME_SEND_gJ_PACKET(_perso, "Eo");
				return;
			}
			if (P.getGremio() != null) {
				GestorSalida.GAME_SEND_gJ_PACKET(_perso, "Ea");
				return;
			}
			if (!_perso.getMiembroGremio().puede(CentroInfo.G_INVITAR)) {
				GestorSalida.GAME_SEND_gJ_PACKET(_perso, "Ed");
				return;
			}
			if (_perso.getGremio().getPjMiembros().size() >= (40 + _perso.getGremio().getNivel())) {
				GestorSalida.ENVIAR_Im_INFORMACION(_out, "155;" + (40 + _perso.getGremio().getNivel()));
				return;
			}
			_perso.setInvitado(P.getID());
			P.setInvitado(_perso.getID());
			GestorSalida.GAME_SEND_gJ_PACKET(_perso, "R" + packet.substring(1));
			GestorSalida.GAME_SEND_gJ_PACKET(P,
					"r" + _perso.getID() + "|" + _perso.getNombre() + "|" + _perso.getGremio().getNombre());
			break;
		case 'E':// o rechazar
			if (_perso == null) {
				return;
			}
			if (_perso.getInvitado() == 0) {
				return;
			}
			GestorSalida.ENVIAR_BN_NADA(_out);
			Personaje t = MundoDofus.getPersonaje(_perso.getInvitado());
			if (t == null) {
				return;
			}
			GestorSalida.GAME_SEND_gJ_PACKET(_perso, "Ec");
			GestorSalida.GAME_SEND_gJ_PACKET(t, "Ec");
			t.setInvitado(0);
			_perso.setInvitado(0);
			break;
		case 'K':// Aceptar
			if (packet.substring(1).equalsIgnoreCase(_perso.getInvitado() + "")) {
				Personaje p = MundoDofus.getPersonaje(_perso.getInvitado());
				if (p == null) {
					return;
				}
				Gremio G = p.getGremio();
				if (G == null) {
					return;
				}
				MiembroGremio GM = G.addNuevoMiembro(_perso);
				GestorSQL.ACTUALIZAR_MIEMBRO_GREMIO(GM);
				_perso.setMiembroGremio(GM);
				_perso.setInvitado(-1);
				p.setInvitado(-1);
				GestorSalida.GAME_SEND_gJ_PACKET(p, "Ka" + _perso.getNombre());
				GestorSalida.GAME_SEND_gS_PACKET(_perso, GM);
				GestorSalida.GAME_SEND_gJ_PACKET(_perso, "Kj");
			}
			break;
		}
	}

	private static void gremio_Informacion(char c, Personaje _perso) {
		Gremio gremio = _perso.getGremio();
		if (gremio == null) {
			return;
		}
		switch (c) {
		case 'B':// Recaudador
			GestorSalida.GAME_SEND_gIB_PACKET(_perso, gremio.analizarRecauAGrmio());
			break;
		case 'F':// Cercados
			GestorSalida.GAME_SEND_gIF_PACKET(_perso, gremio.analizarInfoCercados());
			break;
		case 'G':// General
			GestorSalida.GAME_SEND_gIG_PACKET(_perso, gremio);
			break;
		case 'H':// Casa
			GestorSalida.GAME_SEND_gIH_PACKET(_perso, Casa.analizarCasaGremio(_perso));
			break;
		case 'M':// Miembros
			GestorSalida.ENVIAR_gIM_INFO_MIEMBROS_GREMIO(_perso, gremio, '+');
			break;
		case 'T':// Recaudador
			GestorSalida.ENVIAR_gITM_INFO_RECAUDADOR(_perso, Recaudador.analizarRecaudadores(gremio.getID()));
			Recaudador.analizarAtaque(_perso, gremio.getID());
			Recaudador.analizarDefensa(_perso, gremio.getID());
			break;
		}
	}

	private static void gremio_Crear(String packet, Personaje _perso, PrintWriter _out) {
		if (_perso == null) {
			return;
		}
		if (_perso.getGremio() != null || _perso.getMiembroGremio() != null) {
			GestorSalida.ENVIAR_gC_CREAR_PANEL_GREMIO(_perso, "Ea");
			return;
		}
		if (_perso.getPelea() != null) {
			return;
		}
		try {
			String[] infos = packet.substring(2).split("\\|");
			String escudoId = Integer.toString(Integer.parseInt(infos[0]), 36);
			String colorEscudo = Integer.toString(Integer.parseInt(infos[1]), 36);
			String emblemaId = Integer.toString(Integer.parseInt(infos[2]), 36);
			String colorEmblema = Integer.toString(Integer.parseInt(infos[3]), 36);
			String nombre = infos[4];
			if (MundoDofus.nombreGremioUsado(nombre)) {
				GestorSalida.ENVIAR_gC_CREAR_PANEL_GREMIO(_perso, "Ean");
				return;
			}
			String tempName = nombre.toLowerCase();
			boolean esValido = true;
			if (tempName.length() > 20) {
				esValido = false;
			}
			if (esValido) {
				int tiretCount = 0;
				for (char curLetter : tempName.toCharArray()) {
					if (!((curLetter >= 'a' && curLetter <= 'z') || curLetter == '-')) {
						esValido = false;
						break;
					}
					if (curLetter == '-') {
						if (tiretCount >= 2) {
							esValido = false;
							break;
						} else {
							tiretCount++;
						}
					}
				}
			}
			if (!esValido) {
				GestorSalida.ENVIAR_gC_CREAR_PANEL_GREMIO(_perso, "Ean");
				return;
			}
			String emblema = escudoId + "," + colorEscudo + "," + emblemaId + "," + colorEmblema;
			if (MundoDofus.emblemaGremioUsado(emblema)) {
				GestorSalida.ENVIAR_gC_CREAR_PANEL_GREMIO(_perso, "Eae");
				return;
			}
			if (_perso.getMapa().getID() == 2196) {
				if (!_perso.tieneObjModeloNoEquip(1575, 1)) {
					GestorSalida.ENVIAR_Im_INFORMACION(_out, "14");
					return;
				}
				_perso.removerObjetoPorModYCant(1575, 1);
			}
			Gremio G = new Gremio(_perso, nombre, emblema);
			MiembroGremio gm = G.addNuevoMiembro(_perso);
			gm.setTodosDerechos(1, (byte) 0, 1);
			_perso.setMiembroGremio(gm);
			MundoDofus.addGremio(G, true);
			GestorSQL.ACTUALIZAR_MIEMBRO_GREMIO(gm);
			GestorSalida.GAME_SEND_gS_PACKET(_perso, gm);
			GestorSalida.ENVIAR_gC_CREAR_PANEL_GREMIO(_perso, "K");
			GestorSalida.ENVIAR_gV_CERRAR_PANEL_GREMIO(_perso);
			DateFormat formatTime = new SimpleDateFormat("yyyy~MM~dd~HH~mm");
			Calendar calendar = Calendar.getInstance();
			G.ultimaConex = formatTime.format(calendar.getTime());
		} catch (Exception e) {
			Emu.creaLogs(e);
			return;
		}
	}

	static void analizar_Canal(String packet, Personaje _perso) {
		switch (packet.charAt(1)) {
		case 'C':// Cambio de canal
			canal_Cambiar(packet, _perso);
			break;
		}
	}

	private static void canal_Cambiar(String packet, Personaje _perso) {
		String chan = "";
		try {
			chan = packet.charAt(3) + "";
		} catch (Exception e) {
			return;
		}
		switch (packet.charAt(2)) {
		case '+':// agregar Canal
			_perso.addCanal(chan);
			break;
		case '-':// desactivacion de canal
			_perso.removerCanal(chan);
			break;
		}
		GestorSQL.SALVAR_PERSONAJE(_perso, false);
	}

	static void analizar_Montura(String packet, Personaje _perso, PrintWriter _out, Cuenta _cuenta) {
		switch (packet.charAt(1)) {
		case 'b':// comprar cercados
			montura_Comprar_Cercado(packet, _perso, _out);
			break;
		case 'd':// Manda descripcion
			montura_Descripcion(packet, _perso);
			break;
		case 'c':// castrar montura
			montura_Castrar(_perso);
			break;
		case 'p':// Manda descripcion
			montura_Descripcion(packet, _perso);
			break;
		case 'f':
			montura_Liberar(_perso, _out);
			break;
		case 'n':// cambiar el nombre
			montura_Nombre(packet.substring(2), _perso);
			break;
		case 'o':// borrar objetos de crianza
			montura_Borrar_Objeto_Crianza(packet, _perso, _out);
			break;
		case 'r':// montar el dragopavo
			montura_Montar(_perso);
			break;
		case 's':// Vender cercado
			montura_Vender_Cercado(packet, _perso, _out);
			break;
		case 'v':// cerrar el panel de compra
			GestorSalida.GAME_SEND_R_PACKET(_perso, "v");
			break;
		case 'x':// Cambiar la experiencia donada al dragopavo
			montura_CambiarXP_Donada(packet, _perso);
			break;
		}
	}

	private static void montura_Vender_Cercado(String packet, Personaje _perso, PrintWriter _out) {
		GestorSalida.GAME_SEND_R_PACKET(_perso, "v");// cerrar panel
		int price = Integer.parseInt(packet.substring(2));
		Cercado MP1 = _perso.getMapa().getCercado();
		if (MP1.getDueÑo() == -1) {
			GestorSalida.ENVIAR_Im_INFORMACION(_out, "194");
			return;
		}
		if (MP1.getDueÑo() != _perso.getID()) {
			GestorSalida.ENVIAR_Im_INFORMACION(_out, "195");
			return;
		}
		MP1.setPrecio(price);
		GestorSQL.SALVAR_CERCADO(MP1);
		GestorSQL.SALVAR_PERSONAJE(_perso, true);
		for (Personaje z : _perso.getMapa().getPersos()) {
			GestorSalida.ENVIAR_Rp_INFORMACION_CERCADO(z, MP1);
		}
	}

	private static void montura_Comprar_Cercado(String packet, Personaje _perso, PrintWriter _out) {
		GestorSalida.GAME_SEND_R_PACKET(_perso, "v");
		Cercado cercado = _perso.getMapa().getCercado();
		Personaje vendedor = MundoDofus.getPersonaje(cercado.getDueÑo());
		if (cercado.getDueÑo() == -1) {
			GestorSalida.ENVIAR_Im_INFORMACION(_out, "196");
			return;
		}
		if (cercado.getPrecio() == 0) {
			GestorSalida.ENVIAR_Im_INFORMACION(_out, "197");
			return;
		}
		if (_perso.getGremio() == null) {
			GestorSalida.ENVIAR_Im_INFORMACION(_out, "1135");
			return;
		}
		if (_perso.getMiembroGremio().getRango() != 1) {
			GestorSalida.ENVIAR_Im_INFORMACION(_out, "198");
			return;
		}
		byte cercadosMax = (byte) Math.floor(_perso.getGremio().getNivel() / 10);
		byte cercadosTotalGremio = GestorSQL.totalCercadosDelGremio(_perso.getGremio().getID());
		if (cercadosTotalGremio >= cercadosMax) {
			GestorSalida.ENVIAR_Im_INFORMACION(_out, "1103");
			return;
		}
		if (_perso.getKamas() < cercado.getPrecio()) {
			GestorSalida.ENVIAR_Im_INFORMACION(_out, "182");
			return;
		}
		long nuevasKamas = _perso.getKamas() - cercado.getPrecio();
		_perso.setKamas(nuevasKamas);
		if (vendedor != null) {
			long NewSellerBankKamas = vendedor.getKamasBanco() + cercado.getPrecio();
			vendedor.setKamasBanco(NewSellerBankKamas);
			if (vendedor.enLinea()) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso,
						Idiomas.getTexto(_perso.getCuenta().idioma, 2) + "" + cercado.getPrecio() + ".",
						Emu.COLOR_MENSAJE);
			}
		}
		cercado.setPrecio(0);
		cercado.setPropietario(_perso.getID());
		cercado.setGremio(_perso.getGremio());
		GestorSQL.SALVAR_CERCADO(cercado);
		GestorSQL.SALVAR_PERSONAJE(_perso, true);
		for (Personaje pj : _perso.getMapa().getPersos()) {
			GestorSalida.ENVIAR_Rp_INFORMACION_CERCADO(pj, cercado);
		}
	}

	private static void montura_CambiarXP_Donada(String packet, Personaje _perso) {
		int xp = Integer.parseInt(packet.substring(2));
		if (xp < 0) {
			xp = 0;
		}
		if (xp > 90) {
			xp = 90;
		}
		_perso.setDonarXPMontura(xp);
		GestorSalida.ENVIAR_Rx_EXP_DONADA_MONTURA(_perso);
	}

	private static void montura_Borrar_Objeto_Crianza(String packet, Personaje _perso, PrintWriter _out) {
		int celda = Integer.parseInt(packet.substring(2));
		Mapa mapa = _perso.getMapa();
		if (mapa.getCercado() == null) {
			return;
		}
		Cercado cercado = mapa.getCercado();
		if (_perso.getGremio() == null) {
			GestorSalida.ENVIAR_BN_NADA(_out);
			return;
		}
		if (!_perso.getMiembroGremio().puede(8192)) {
			GestorSalida.ENVIAR_Im_INFORMACION(_out, "193");
			return;
		}
		if (cercado.delObjetoCria(celda)) {
			GestorSalida.ENVIAR_GDO_PONER_OBJETO_CRIA(mapa, celda + ";0;0");
			return;
		}
	}

	private static void montura_Nombre(String nombre, Personaje _perso) {
		if ((_perso == null) || (_perso.getMontura() == null)) {
			return;
		}
		_perso.getMontura().setNombre(nombre);
		GestorSalida.ENVIAR_Rn_CAMBIO_NOMBRE_MONTURA(_perso, nombre);
	}

	private static void montura_Montar(Personaje _perso) {
		if (_perso.getNivel() < 60 || _perso.getMontura() == null || _perso.getMontura().esMontable() == 0
				|| _perso.esFantasma()) {
			GestorSalida.ENVIAR_Re_DETALLES_MONTURA(_perso, "Er", null);
			return;
		}
		if (!Emu.MONTURAYMASCOTA) {
			if (_perso.getObjPosicion(8) != null || _perso.getObjPosicion(170) != null) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 3),
						Emu.COLOR_MENSAJE);
				return;
			}
		}
		_perso.subirBajarMontura();
	}

	private static void montura_Castrar(Personaje _perso) {
		if (_perso.getMontura() == null) {
			GestorSalida.ENVIAR_Re_DETALLES_MONTURA(_perso, "Er", null);
			return;
		}
		_perso.getMontura().castrarPavo();
		GestorSalida.ENVIAR_Re_DETALLES_MONTURA(_perso, "+", _perso.getMontura());
	}

	private static void montura_Liberar(Personaje _perso, PrintWriter _out) {
		if (_perso.getMontura() == null) {
			GestorSalida.ENVIAR_Re_DETALLES_MONTURA(_perso, "Er", null);
			return;
		}
		GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out, Idiomas.getTexto(_perso.getCuenta().idioma, 4));
	}

	private static void montura_Descripcion(String packet, Personaje _perso) {
		int DPid = -1;
		try {
			DPid = Integer.parseInt(packet.substring(2).split("\\|")[0]);
		} catch (Exception e) {
			return;
		}
		if (DPid == -1) {
			return;
		}
		if (DPid > 0) {
			DPid = -DPid;
		}
		Dragopavo DD = MundoDofus.getDragopavoPorID(DPid);
		if (DD == null) {
			return;
		}
		GestorSalida.ENVIAR_Rd_DESCRIPCION_MONTURA(_perso, DD);
	}

	static void analizar_Amigos(String packet, Personaje _perso, PrintWriter _out, Cuenta _cuenta) {
		switch (packet.charAt(1)) {
		case 'A':// Agregar
			amigo_Agregar(packet, _perso, _out, _cuenta);
			break;
		case 'D':// Borrar un amigo
			amigo_Borrar(packet, _perso, _out, _cuenta);
			break;
		case 'L':// Lista
			GestorSalida.ENVIAR_FL_LISTA_DE_AMIGOS(_perso);
			break;
		case 'O':
			switch (packet.charAt(2)) {
			case '-':
				_perso.mostrarAmigosEnLinea(false);
				GestorSalida.ENVIAR_BN_NADA(_out);
				break;
			case '+':
				_perso.mostrarAmigosEnLinea(true);
				GestorSalida.ENVIAR_BN_NADA(_out);
				break;
			}
			break;
		case 'J': // Amante
			amigo_Esposo(packet, _perso);
			break;
		}
	}

	private static void amigo_Esposo(String packet, Personaje _perso) {
		Personaje esposo = MundoDofus.getPersonaje(_perso.getEsposo());
		if (esposo == null) {
			return;
		}
		if (!esposo.enLinea()) {
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 5),
					Emu.COLOR_MENSAJE);
			GestorSalida.ENVIAR_FL_LISTA_DE_AMIGOS(_perso);
			return;
		}
		switch (packet.charAt(2)) {
		case 'S':
			if (_perso.getPelea() != null) {
				return;
			} else {
				_perso.casarse(esposo);
			}
			break;
		case 'C':
			if (packet.charAt(3) == '+') {
				if (_perso.getSiguiendo() != null) {
					_perso.getSiguiendo().getSeguidores().remove(_perso.getID());
				}
				GestorSalida.ENVIAR_IC_PERSONAJE_BANDERA_COMPAS(_perso, esposo);
				_perso.setSiguiendo(esposo);
				esposo.getSeguidores().put(_perso.getID(), _perso);
			} else {
				GestorSalida.ENVIAR_IC_BORRAR_BANDERA_COMPAS(_perso);
				_perso.setSiguiendo(null);
				esposo.getSeguidores().remove(_perso.getID());
			}
			break;
		}
	}

	private static void amigo_Borrar(String packet, Personaje _perso, PrintWriter _out, Cuenta _cuenta) {
		if (_perso == null) {
			return;
		}
		int id = -1;
		switch (packet.charAt(2)) {
		case '%':// nombre de personaje
			packet = packet.substring(3);
			Personaje P = MundoDofus.getPjPorNombre(packet);
			if (P == null) {
				GestorSalida.ENVIAR_FD_BORRAR_AMIGO(_perso, "Ef");
				return;
			}
			id = P.getCuentaID();
			break;
		case '*':// Apodo
			packet = packet.substring(3);
			Cuenta C = MundoDofus.getCuentaPorApodo(packet);
			if (C == null) {
				GestorSalida.ENVIAR_FD_BORRAR_AMIGO(_perso, "Ef");
				return;
			}
			id = C.getID();
			break;
		default:
			packet = packet.substring(2);
			Personaje Pj = MundoDofus.getPjPorNombre(packet);
			if (Pj == null ? true : !Pj.enLinea()) {
				GestorSalida.ENVIAR_FD_BORRAR_AMIGO(_perso, "Ef");
				return;
			}
			id = Pj.getCuenta().getID();
			break;
		}
		if (id == -1 || !_cuenta.esAmigo(id)) {
			GestorSalida.ENVIAR_FD_BORRAR_AMIGO(_perso, "Ef");
			return;
		}
		_cuenta.borrarAmigo(id);
	}

	private static void amigo_Agregar(String packet, Personaje _perso, PrintWriter _out, Cuenta _cuenta) {
		if (_perso == null) {
			return;
		}
		int id = -1;
		switch (packet.charAt(2)) {
		case '%':// nombre de personaje
			packet = packet.substring(3);
			Personaje P = MundoDofus.getPjPorNombre(packet);
			if (P == null ? true : !P.enLinea()) {
				GestorSalida.ENVIAR_FA_AGREGAR_AMIGO(_perso, "Ef");
				return;
			}
			id = P.getCuentaID();
			break;
		case '*':// apodo
			packet = packet.substring(3);
			Cuenta C = MundoDofus.getCuentaPorApodo(packet);
			if (C == null ? true : !C.enLinea()) {
				GestorSalida.ENVIAR_FA_AGREGAR_AMIGO(_perso, "Ef");
				return;
			}
			id = C.getID();
			break;
		default:
			packet = packet.substring(2);
			if (MundoDofus.idDebots.containsKey(packet)) {
				GestorSalida.ENVIAR_FA_AGREGAR_AMIGO(_perso, "Ea");
				return;
			}
			Personaje Pj = MundoDofus.getPjPorNombre(packet);
			if (Pj == null ? true : !Pj.enLinea()) {
				GestorSalida.ENVIAR_FA_AGREGAR_AMIGO(_perso, "Ef");
				return;
			}
			id = Pj.getCuenta().getID();
			break;
		}
		if (id == -1) {
			GestorSalida.ENVIAR_FA_AGREGAR_AMIGO(_perso, "Ef");
			return;
		}
		_cuenta.addAmigo(id);
	}

	static void analizar_Grupo(String packet, Personaje _perso, PrintWriter _out, Cuenta _cuenta) {
		switch (packet.charAt(1)) {
		case 'A':// Aceptar invitacion
			grupo_Aceptar(packet, _perso, _out);
			break;
		case 'F':
			Grupo g = _perso.getGrupo();
			if (g == null) {
				return;
			}
			int pId = -1;
			try {
				pId = Integer.parseInt(packet.substring(3));
			} catch (NumberFormatException e) {
				Emu.creaLogs(e);
				return;
			}
			if (pId == -1) {
				return;
			}
			Personaje perso = MundoDofus.getPersonaje(pId);
			if (perso == null || !perso.enLinea()) {
				return;
			}
			if (packet.charAt(2) == '+') {
				if (_perso.getSiguiendo() != null) {
					_perso.getSiguiendo().getSeguidores().remove(_perso.getID());
				}
				GestorSalida.ENVIAR_IC_PERSONAJE_BANDERA_COMPAS(_perso, perso);
				GestorSalida.ENVIAR_PF_SEGUIR_PERSONAJE(_perso, "+" + perso.getID());
				_perso.setSiguiendo(perso);
				perso.getSeguidores().put(_perso.getID(), _perso);
			} else if (packet.charAt(2) == '-') {
				GestorSalida.ENVIAR_IC_BORRAR_BANDERA_COMPAS(_perso);
				GestorSalida.ENVIAR_PF_SEGUIR_PERSONAJE(_perso, "-");
				_perso.setSiguiendo(null);
				perso.getSeguidores().remove(_perso.getID());
			}
			break;
		case 'G':
			Grupo g2 = _perso.getGrupo();
			if (g2 == null) {
				return;
			}
			int pId2 = -1;
			try {
				pId2 = Integer.parseInt(packet.substring(3));
			} catch (NumberFormatException e) {
				Emu.creaLogs(e);
				return;
			}
			if (pId2 == -1) {
				return;
			}
			Personaje P2 = MundoDofus.getPersonaje(pId2);
			if (P2 == null || !P2.enLinea()) {
				return;
			}
			if (packet.charAt(2) == '+') {
				for (Personaje integrante : g2.getPersos()) {
					if (integrante.getID() == P2.getID()) {
						continue;
					}
					if (integrante.getSiguiendo() != null) {
						integrante.getSiguiendo().getSeguidores().remove(_perso.getID());
					}
					GestorSalida.ENVIAR_IC_PERSONAJE_BANDERA_COMPAS(integrante, P2);
					GestorSalida.ENVIAR_PF_SEGUIR_PERSONAJE(integrante, "+" + P2.getID());
					integrante.setSiguiendo(P2);
					P2.getSeguidores().put(integrante.getID(), integrante);
				}
			} else if (packet.charAt(2) == '-') {
				for (Personaje integrante : g2.getPersos()) {
					if (integrante.getID() == P2.getID()) {
						continue;
					}
					GestorSalida.ENVIAR_IC_BORRAR_BANDERA_COMPAS(integrante);
					GestorSalida.ENVIAR_PF_SEGUIR_PERSONAJE(integrante, "-");
					integrante.setSiguiendo(null);
					P2.getSeguidores().remove(integrante.getID());
				}
			}
			break;
		case 'I':// invitacion
			grupo_Invitar(packet, _perso, _out);
			break;
		case 'R':// Rechazar
			grupo_Rechazar(_perso, _out);
			break;
		case 'V':// Quitar
			grupo_Expulsar(packet, _perso, _out);
			break;
		case 'W':// Localizacion del grupo
			grupo_Localizar(_perso);
			break;
		}
	}

	private static void grupo_Localizar(Personaje _perso) {
		if (_perso == null) {
			return;
		}
		Grupo g = _perso.getGrupo();
		if (g == null) {
			return;
		}
		StringBuilder str = new StringBuilder("");
		boolean primero = false;
		for (Personaje pj : g.getPersos()) {
			Mapa mapa = pj.getMapa();
			if (mapa == null) {
				continue;
			}
			if (primero) {
				str.append("|");
			}
			str.append(mapa.getCoordX() + ";" + mapa.getCoordY() + ";" + mapa.getID() + ";2;" + pj.getID() + ";"
					+ pj.getNombre());
			primero = true;
		}
		GestorSalida.ENVIAR_IH_COORDINAS_UBICACION(_perso, str.toString());
	}

	private static void grupo_Expulsar(String packet, Personaje _perso, PrintWriter _out) {
		if (_perso == null) {
			return;
		}
		Grupo grupo = _perso.getGrupo();
		if (grupo == null) {
			return;
		}
		if (packet.length() == 2) {
			grupo.dejarGrupo(_perso);
			GestorSalida.ENVIAR_PV_DEJAR_GRUPO(_out, "");
			GestorSalida.ENVIAR_IH_COORDINAS_UBICACION(_perso, "");
		} else if (grupo.esLiderGrupo(_perso.getID())) {
			int id = -1;
			try {
				id = Integer.parseInt(packet.substring(2));
			} catch (NumberFormatException e) {
				Emu.creaLogs(e);
				return;
			}
			if (id == -1) {
				return;
			}
			Personaje expulsado = MundoDofus.getPersonaje(id);
			if (expulsado == null) {
				return;
			}
			grupo.dejarGrupo(expulsado);
			if (expulsado.enLinea()) {
				GestorSalida.ENVIAR_PV_DEJAR_GRUPO(expulsado.getCuenta().getEntradaPersonaje().getOut(),
						"" + _perso.getID());
				GestorSalida.ENVIAR_IH_COORDINAS_UBICACION(expulsado, "");
			}
		}
	}

	private static void grupo_Invitar(String packet, Personaje _perso, PrintWriter _out) {
		if (_perso == null) {
			return;
		}
		String nombre = packet.substring(2);
		Personaje invitado = MundoDofus.getPjPorNombre(nombre);

		if (invitado == null) {
			return;
		}
		if (!invitado.enLinea()) {
			GestorSalida.ENVIAR_PIE_ERROR_INVITACION_GRUPO(_out, "n" + invitado.getNombre());
			return;
		}
		if (invitado.getGrupo() != null) {
			GestorSalida.ENVIAR_PIE_ERROR_INVITACION_GRUPO(_out, "a" + invitado.getNombre());
			return;
		}
		if (_perso.getGrupo() != null && _perso.getGrupo().getNumeroPjs() == 8) {
			GestorSalida.ENVIAR_PIE_ERROR_INVITACION_GRUPO(_out, "f");
			return;
		}
		invitado.setInvitado(_perso.getID());
		_perso.setInvitado(invitado.getID());



			GestorSalida.ENVIAR_PIK_INVITAR_GRUPO(_out, _perso.getNombre(), nombre);
			GestorSalida.ENVIAR_PIK_INVITAR_GRUPO(invitado.getCuenta().getEntradaPersonaje().getOut(), _perso.getNombre(),
							nombre);


	}

	private static void grupo_Rechazar(Personaje _perso, PrintWriter _out) {
		if ((_perso == null) || (_perso.getInvitado() == 0)) {
			return;
		}
		GestorSalida.ENVIAR_BN_NADA(_out);
		Personaje t = MundoDofus.getPersonaje(_perso.getInvitado());
		if (t == null) {
			return;
		}
		GestorSalida.ENVIAR_PR_RECHAZAR_INVITACION_GRUPO(t);
		t.setInvitado(0);
		_perso.setInvitado(0);
	}

	private static void grupo_Aceptar(String packet, Personaje _perso, PrintWriter _out) {
		if ((_perso == null) || (_perso.getInvitado() == 0)) {
			return;
		}
		Personaje invitado = MundoDofus.getPersonaje(_perso.getInvitado());
		if (invitado == null) {
			return;
		}
		Grupo grupo = invitado.getGrupo();
		try {
			if (grupo == null) {
				PrintWriter out = invitado.getCuenta().getEntradaPersonaje().getOut();
				if (invitado.getCuenta().getEntradaPersonaje() == null) {
					return;
				}
				grupo = new Grupo(invitado,_perso);
				GestorSalida.ENVIAR_PCK_CREAR_GRUPO(_out, grupo);
				GestorSalida.ENVIAR_PL_LIDER_GRUPO(_out, grupo);
				GestorSalida.ENVIAR_PCK_CREAR_GRUPO(out, grupo);
				GestorSalida.ENVIAR_PL_LIDER_GRUPO(out, grupo);
				invitado.setGrupo(grupo);
				GestorSalida.ENVIAR_PM_TODOS_MIEMBROS_GRUPO(out, grupo);
			} else {
				GestorSalida.ENVIAR_PCK_CREAR_GRUPO(_out, grupo);
				GestorSalida.ENVIAR_PL_LIDER_GRUPO(_out, grupo);
				GestorSalida.ENVIAR_PM_AGREGAR_PJ_GRUPO(grupo, _perso);
				grupo.addPerso(_perso);
			}
			_perso.setGrupo(grupo);
			GestorSalida.ENVIAR_PM_TODOS_MIEMBROS_GRUPO(_out, grupo);
			GestorSalida.ENVIAR_PR_RECHAZAR_INVITACION_GRUPO(invitado);
			for (Personaje pjx : _perso.getGrupo().getPersos()) {
				if (pjx == null) {
					continue;
				}
				pjx.liderMaitre = false;
				pjx.esMaitre = false;
			}
		} catch (NullPointerException e) {
			Emu.creaLogs(e);
			GestorSalida.ENVIAR_BN_NADA(_out);
		}
	}

	static void analizar_Objetos(String packet, Personaje _perso, PrintWriter _out, Cuenta _cuenta) {
		switch (packet.charAt(1)) {
		case 'd':// Supresion de un objeto
			objeto_Eliminar(packet, _perso, _out);
			break;
		case 'D':// Dejar un objeto en el suelo
			objeto_Tirar(packet, _perso, _out);
			break;
		case 'M':// mover un objeto (Equipar/desequipar)
			objeto_Mover(packet, _perso, _out, true);
			break;
		case 'U':// Utilizar un objeto (pociones)
			objeto_Usar(packet, _perso, _out);
			break;
		case 's':// cambiar Skin
			aparienciaObjevivo(packet, _perso, _out);
			break;
		case 'f':// Alimentar, dar de comer
			alimentarObjevivo(packet, _perso, _out);
			break;
		case 'x':// Desequipar
			desequiparObjevivo(packet, _perso, _out);
			break;
		}
	}

	private static synchronized void objeto_Tirar(String packet, Personaje _perso, PrintWriter _out) {
		int id = -1;
		int cant = -1;
		try {
			id = Integer.parseInt(packet.substring(2).split("\\|")[0]);
			cant = Integer.parseInt(packet.split("\\|")[1]);
		} catch (Exception e) {
			Emu.creaLogs(e);
		}
		if (id == -1 || cant <= 0 || !_perso.tieneObjetoID(id)) {
			return;
		}
		Objeto obj = MundoDofus.getObjeto(id);
		if (obj == null) {
			return;
		}
		int idObjModelo = obj.getModelo().getID();
		if (idObjModelo == 10085) {
			_perso.borrarObjetoRemove(id);
			GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_out, id);
			MundoDofus.eliminarObjeto(id);
			return;
		}
		if (obj.getModelo().getID() == 11158) {
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 6), Colores.AZUL);
			return;
		}
		if (obj.getModelo().getID() == 980125) {
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 178), Colores.AZUL);
			return;
		}
		if (obj.getModelo().getID() == 10275) {
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 7), Colores.AZUL);
			return;
		}
		if (obj.getModelo().getPrecioVIP() > 0) {
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 8),
					Emu.COLOR_MENSAJE);
			return;
		}
		int celdaDrop = CentroInfo.getCeldaIDCercanaNoUsada(_perso);
		if (celdaDrop == 0) {
			GestorSalida.ENVIAR_Im_INFORMACION(_out, "1145");
			return;
		}
		Celda celdaTirar = _perso.getMapa().getCelda(celdaDrop);
		if (cant >= obj.getCantidad()) {
			_perso.borrarObjetoRemove(id);
			celdaTirar.addObjetoTirado(obj, _perso);
			obj.setPosicion(-1);
			GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_out, id);
		} else {
			obj.setCantidad(obj.getCantidad() - cant);
			Objeto obj2 = Objeto.clonarObjeto(obj, cant);
			obj2.setPosicion(-1);
			MundoDofus.addObjeto(obj2, false);
			celdaTirar.addObjetoTirado(obj2, _perso);
			GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso, obj);
		}
		GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso);
		GestorSalida.ENVIAR_GDO_OBJETO_TIRADO_AL_SUELO(_perso.getMapa(), '+', celdaTirar.getID(), idObjModelo, 0);
		GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
	}

	private static synchronized void objeto_Usar(String packet, Personaje _perso, PrintWriter _out) {
		int id = -1;
		int idPjObjetivo = -1;
		short celdaId = -1;
		Personaje pjObjetivo = null;
		try {
			String[] infos = packet.substring(2).split("\\|");
			id = Integer.parseInt(infos[0]);
			try {
				idPjObjetivo = Integer.parseInt(infos[1]);
			} catch (Exception e) {
				idPjObjetivo = -1;
			}
			try {
				celdaId = Short.parseShort(infos[2]);
			} catch (Exception e) {
				celdaId = -1;
			}
		} catch (Exception e) {
			Emu.creaLogs(e);
			return;
		}
		if (MundoDofus.getPersonaje(idPjObjetivo) != null) {
			pjObjetivo = MundoDofus.getPersonaje(idPjObjetivo);
		}
		if (!_perso.tieneObjetoID(id)) {
			return;
		}
		Objeto obj = MundoDofus.getObjeto(id);
		if (obj == null) {
			return;
		}
		ObjetoModelo objModeloBD = obj.getModelo();
		if (_perso.getPelea() != null) {
			if (_perso.getPelea().getEstado() > 2 || (objModeloBD.getTipo() != CentroInfo.ITEM_TIPO_PAN)) {
				GestorSalida.ENVIAR_Im_INFORMACION(_out, "191");
				return;
			}
		}
		if (!objModeloBD.getCondiciones().equalsIgnoreCase("")
				&& !CondicionJugador.validaCondiciones(_perso, objModeloBD.getCondiciones())) {
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 9), Colores.ROJO);
			return;
		}
		objModeloBD.aplicarAccion(_perso, pjObjetivo, id, celdaId);
	}

	private static void objeto_Eliminar(String packet, Personaje _perso, PrintWriter _out) {
		String[] infos = packet.substring(2).split("\\|");
		try {
			int id = Integer.parseInt(infos[0]);
			int cant = 1;
			try {
				cant = Integer.parseInt(infos[1]);
			} catch (Exception e) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 10),
						Colores.ROJO);
				return;
			}
			int enviap = 0;
			try {
				enviap = Integer.parseInt(infos[2]);
			} catch (Exception e) {
				enviap = 0;
			}
			Objeto obj = MundoDofus.getObjeto(id);
			if (obj == null || !_perso.tieneObjetoID(id) || cant <= 0) {
				GestorSalida.ENVIAR_OdE_ERROR_ELIMINAR_OBJETO(_out);
				return;
			}
			if (obj.getPosicion() != -1) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 11),
						Colores.ROJO);
				return;
			}
			int nuevaCant = obj.getCantidad() - cant;
			if (nuevaCant <= 0) {
				_perso.borrarObjetoRemove(id);
				MundoDofus.eliminarObjeto(id);
				if (enviap == 0) {
					GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_out, id);
				}
			} else {
				obj.setCantidad(nuevaCant);
				if (enviap == 0) {
					GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso, obj);
				}
			}
			if (enviap == 0) {
				GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
				GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso);
			}
		} catch (Exception e) {
			Emu.creaLogs(e);
			GestorSalida.ENVIAR_OdE_ERROR_ELIMINAR_OBJETO(_out);
		}
	}

	public static void desbugItemEncima(Personaje _perso, PrintWriter _out) {
		int veces = 0;
		ArrayList<Integer> lista = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
				16, 200, 201, 202, 203, 204, 205, 170));
		for (int i = 0; i < lista.size(); i++) { // de 0 a 16
			if (!_perso.enLinea() || _perso.getPelea() != null) {
				return;
			}
			Objeto obb = _perso.getObjPosicion(lista.get(i));
			if (obb == null) {
				continue;
			}
			veces = _perso.getObjPosicionCant(lista.get(i));
			boolean movido = false;
			if (veces > 1) {
				System.out.println("El personaje " + _perso.getNombre() + " ha tenido " + veces
						+ " items encima uno del otro en la posici�n " + i);
				if (veces > 2) {
					for (int i1 = 0; i1 < (veces); i1++) {
						objeto_Mover("OM" + obb.getID() + "|-1", _perso, _out, false);
						movido = true;
					}
				}
			}
			if (obb != null && !movido) {
				if (obb.getModelo().getNivel() > _perso.getNivel()) {
					objeto_Mover("OM" + obb.getID() + "|-1", _perso, _out, false);
				}
			}
		}
	}

	public static void desequiparTodo(Personaje _perso, PrintWriter _out) {
		ArrayList<Integer> lista = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
				16, 200, 201, 202, 203, 204, 205, 170));
		for (Integer element : lista) { // de 0 a 16
			if (!_perso.enLinea() || _perso.getPelea() != null) {
				return;
			}
			Objeto obb = _perso.getObjPosicion(element);
			if (obb == null) {
				continue;
			}
			if (obb.getModelo().getNivel() > _perso.getNivel()) {
				objeto_Mover("OM" + obb.getID() + "|-1", _perso, _out, false);
			}
		}
	}

	private static void objeto_Mover(String packet, Personaje _perso, PrintWriter _out, boolean revisa) {
		if (!_perso.enLinea()) {
			return;
		}
		if (_perso.getPelea() != null) {
			if (_perso.getPelea().getEstado() > 2) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 12),
						Emu.COLOR_MENSAJE);
				return;
			}
		}
		try {
			if (_perso.getIntercambiandoCon() != 0 || _perso.getIntercambio() != null) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 170),
						Emu.COLOR_MENSAJE);
				return;
			}
			String[] infos = packet.substring(2).split("" + (char) 0x0A)[0].split("\\|");
			int cantObjMover;
			int idObjMover = Integer.parseInt(infos[0]);
			int posAMover = Integer.parseInt(infos[1]);
			try {
				cantObjMover = Integer.parseInt(infos[2]);
			} catch (Exception e) {
				cantObjMover = 1;
			}
			Objeto objMover = MundoDofus.getObjeto(idObjMover);
			if (objMover == null || !_perso.tieneObjetoID(idObjMover)) {
				return;
			}
			if (posAMover == 1 && (objMover.getModelo().getTipo() == 28 || objMover.getModelo().getTipo() == 42)) {
				return;
			}
			ObjetoModelo objetoMod = objMover.getModelo();
			if (objetoMod == null) {
				return;
			}
			if (_perso.getObjPosicion(posAMover) != null && objMover.getModelo().getTipo() != 113) {
				GestorSalida.ENVIAR_BN_NADA(_perso);
				return;
			}
			// if (revisa)
			// desbugItemEncima(_perso, _out);
			int niveldelp = _perso.getNivel();
			if (objetoMod.getNivel() > niveldelp && posAMover != -1) {
				GestorSalida.ENVIAR_Im_INFORMACION(_out, "13");
				return;
			}
			if (!objetoMod.getCondiciones().isEmpty() && posAMover != -1
					&& !CondicionJugador.validaCondiciones(_perso, objetoMod.getCondiciones())) {
				GestorSalida.ENVIAR_Im_INFORMACION(_out, "119|43");
				return;
			}
			if (!CentroInfo.esUbicacionValidaObjeto(objetoMod, posAMover) && posAMover != -1
					&& objetoMod.getTipo() != 113 && cantObjMover != 1) {
				return;
			}
			_perso.lastpodvalue = 0;
			if ((posAMover == 8) && (_perso.getObjPosicion(8) != null)) {// ALIMENTAR MASCOTAS
				alimentarMascota(objMover, _perso.getObjPosicion(8), cantObjMover, _perso, _out);
				return;
			}
			if (Emu.ARMAS_ENCARNACIONES.contains(objetoMod.getID())) {
				int segundos = Calendar.getInstance().get(Calendar.MINUTE) * 60
						+ Calendar.getInstance().get(Calendar.SECOND);
				if (_perso.getEncarnacion() == null && posAMover == 1) {
					Encarnacion encarnacion = MundoDofus.getEncarnacion(idObjMover);
					if (encarnacion == null) {
						encarnacion = new Encarnacion(idObjMover, CentroInfo.getClasePorObjMod(objetoMod.getID()), 1, 0,
								segundos, "");
						MundoDofus.addEncarnacion(encarnacion);
						GestorSQL.AGREGAR_ENCARNACION(encarnacion);
					} else if (!encarnacion.sePuedePoner(segundos)) {
						GestorSalida.ENVIAR_Im_INFORMACION(_out, "1166");
						return;
					}
					if (_perso.estaMontando()) {
						_perso.bajarMontura();
					}
					_perso.setEncarnacion(encarnacion);
					_perso.setGfxID(encarnacion.getGfx());
					GestorSalida.ENVIAR_GM_REFRESCAR_PJ_EN_MAPA(_perso.getMapa(), _perso);
					GestorSalida.ENVIAR_ASK_PERSONAJE_SELECCIONADO(_out, _perso);
					GestorSalida.ENVIAR_SL_LISTA_HECHIZOS(_perso);
				} else if (_perso.getEncarnacion() != null && posAMover == -1) {
					_perso.getEncarnacion().setSegundos(segundos);
					_perso.deformar();
					_perso.setEncarnacion(null);
					GestorSalida.ENVIAR_GM_REFRESCAR_PJ_EN_MAPA(_perso.getMapa(), _perso);
					GestorSalida.ENVIAR_ASK_PERSONAJE_SELECCIONADO(_out, _perso);
					GestorSalida.ENVIAR_SL_LISTA_HECHIZOS(_perso);
				}
			}

			//libros runicos jhon
			if (Emu.LIBROS_RUNICOS.contains(objetoMod.getID())) {
				//int segundos = Calendar.getInstance().get(Calendar.MINUTE) * 60
					//	+ Calendar.getInstance().get(Calendar.SECOND);
				if (_perso.getLibrosRunicos() == null && posAMover == 205) {
					LibrosRunicos librosRunicos = MundoDofus.getLibroRunico(idObjMover);
					if (librosRunicos == null) {
						librosRunicos = new LibrosRunicos(idObjMover, 1, 0);
						MundoDofus.addLibrosRunicos(librosRunicos);
						GestorSQL.AGREGAR_LIBROS_RUNICOS(librosRunicos);
						//_perso.setLibrosRunicos(librosRunicos);
						//GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
					} /*else if (!librosRunicos.sePuedePoner(segundos)) {
						GestorSalida.ENVIAR_Im_INFORMACION(_out, "1166");
						return;
					}*/
					//if (_perso.estaMontando())
						//_perso.bajarMontura();
					_perso.setLibrosRunicos(librosRunicos);
					//_perso.setGfxID(encarnacion.getGfx());
					//GestorSalida.ENVIAR_GM_REFRESCAR_PJ_EN_MAPA(_perso.getMapa(), _perso);
					//GestorSalida.ENVIAR_ASK_PERSONAJE_SELECCIONADO(_out, _perso);
					//GestorSalida.ENVIAR_SL_LISTA_HECHIZOS(_perso);
				} else if (_perso.getLibrosRunicos() != null && posAMover == -1) {
					//_perso.getEncarnacion().setSegundos(segundos);
					//_perso.deformar();
					_perso.setLibrosRunicos(null);
					//GestorSalida.ENVIAR_GM_REFRESCAR_PJ_EN_MAPA(_perso.getMapa(), _perso);
					//GestorSalida.ENVIAR_ASK_PERSONAJE_SELECCIONADO(_out, _perso);
					//GestorSalida.ENVIAR_SL_LISTA_HECHIZOS(_perso);
				}

				//GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
			}
			//libros runicos jhon

			if (objetoMod.getTipo() == 18) {
				if (posAMover == 8 && _perso.getObjPosicion(8) != null) {
					if (objetoMod.getTipo() == 18) {
						GestorSalida.ENVIAR_BN_NADA(_perso);
						return;
					}
				}
				if (posAMover == 8 && _perso.getObjPosicion(8) == null
						|| posAMover == 170 && _perso.getObjPosicion(170) == null) {
					if (posAMover == 8 && _perso.getObjPosicion(170) != null) {
						if (_perso.getObjPosicion(170).getModelo().getID() == objetoMod.getID()) {
							GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 38),
									Emu.COLOR_MENSAJE);
							return;
						}
					}
					if (posAMover == 170 && _perso.getObjPosicion(8) != null) {
						if (_perso.getObjPosicion(8).getModelo().getID() == objetoMod.getID()) {
							GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 38),
									Emu.COLOR_MENSAJE);
							return;
						}
					}
					if (!Emu.MONTURAYMASCOTA) {
						if (_perso.estaMontando()) {
							GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 13),
									Emu.COLOR_MENSAJE);
							return;
						}
					}
					if (objMover.getCantidad() > 1) {
						if (cantObjMover > objMover.getCantidad()) {
							cantObjMover = objMover.getCantidad();
						}
						if (objMover.getCantidad() - cantObjMover > 0) {
							int nuevaCant = objMover.getCantidad() - cantObjMover;
							Objeto nuevoObj = Objeto.clonarObjeto(objMover, nuevaCant);
							if (!_perso.addObjetoSimilar(nuevoObj, true, idObjMover)) {
								MundoDofus.addObjeto(nuevoObj, true);
								_perso.addObjetoPut(nuevoObj);
								GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_out, nuevoObj);
							}
							objMover.setCantidad(cantObjMover);
							GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso, objMover);
						}
					}
					objMover.setPosicion(posAMover);
					GestorSalida.ENVIAR_OM_MOVER_OBJETO(_perso, objMover);
					equiparMascota(objMover, _perso);
					GestorSalida.ENVIAR_Oa_CAMBIAR_ROPA(_perso.getMapa(), _perso);
					if (_perso.getPelea() != null) {
						GestorSalida.ENVIAR_Oa_CAMBIAR_ROPA_PELEA(_perso, _perso.getPelea());
					}
					return;
				}
			}
			/*
			 * if (posAMover == 16 && _perso.getMontura() != null) { //alimento montura if
			 * (CentroInfo.alimentoMontura(objetoMod.getTipo())) { if
			 * (objMover.getCantidad() > 0) { if (cantObjMover > objMover.getCantidad())
			 * cantObjMover = objMover.getCantidad(); if (objMover.getCantidad() -
			 * cantObjMover > 0) { int nuevaCant = objMover.getCantidad() - cantObjMover;
			 * objMover.setCantidad(nuevaCant);
			 * GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso, objMover); } else {
			 * _perso.borrarObjetoRemove(idObjMover); MundoDofus.eliminarObjeto(idObjMover);
			 * GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_out, idObjMover); } }
			 * _perso.getMontura().aumEnergia(objMover.getModelo().getNivel(),
			 * cantObjMover); GestorSalida.ENVIAR_Re_DETALLES_MONTURA(_perso, "+",
			 * _perso.getMontura()); return; } else {
			 * GestorSalida.ENVIAR_Im_INFORMACION(_perso, "190"); return; } }
			 */

			int idSetObjeto = objetoMod.getSetID();
			String modificacion = "";
			if ((idSetObjeto >= 81 && idSetObjeto <= 92) || (idSetObjeto >= 201 && idSetObjeto <= 212)) {
				int hechizo;
				int modif;
				if (posAMover == 2 || posAMover == 3 || posAMover == 4 || posAMover == 5 || posAMover == 6
						|| posAMover == 7 || posAMover == 0) {
					String[] stats = objetoMod.getStringStatsObj().split(",");
					for (String stat : stats) {
						String[] val = stat.split("#");
						int efecto = Integer.parseInt(val[0], 16);
						hechizo = Integer.parseInt(val[1], 16);
						modif = Integer.parseInt(val[3], 16);
						modificacion = efecto + ";" + hechizo + ";" + modif;
						GestorSalida.ENVIAR_SB_HECHIZO_BOOST_SET_CLASE(_perso, modificacion);
						_perso.addHechizosSetClase(hechizo, efecto, modif);
					}
					_perso.agregarSetClase(objetoMod.getID());
				} else if (posAMover == -1) {
					String[] stats = objetoMod.getStringStatsObj().split(",");
					for (String stat : stats) {
						String[] val = stat.split("#");
						modificacion = Integer.parseInt(val[0], 16) + ";" + Integer.parseInt(val[1], 16) + ";0";
						GestorSalida.ENVIAR_SB_HECHIZO_BOOST_SET_CLASE(_perso, modificacion);
						_perso.delHechizosSetClase(Integer.parseInt(val[1], 16));
					}
					_perso.borrarSetClase(objetoMod.getID());
				}
			}
			if (objetoMod.getTipo() == 113) {
				if (_perso.getObjPosicion(posAMover) == null) {
					GestorSalida.ENVIAR_Im_INFORMACION(_out, "1161");
					return;
				} else {
					if (_perso.getObjPosicion(posAMover).getObjeviID() != 0) {
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 14),
								Emu.COLOR_MENSAJE);
						return;
					}
					if (objMover.getCantidad() > 1) {
						if (cantObjMover > objMover.getCantidad()) {
							cantObjMover = objMover.getCantidad();
						}
						if (objMover.getCantidad() - cantObjMover > 0) {
							int nuevaCant = objMover.getCantidad() - cantObjMover;
							Objeto nuevoObj = Objeto.clonarObjeto(objMover, nuevaCant);
							if (!_perso.addObjetoSimilar(nuevoObj, true, idObjMover)) {
								MundoDofus.addObjeto(nuevoObj, true);
								_perso.addObjetoPut(nuevoObj);
								GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_out, nuevoObj);
							}
							objMover.setCantidad(cantObjMover);
							GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso, objMover);
						}
					}
					Objeto objet = _perso.getObjPosicion(posAMover);
					equiparObjevivo(objMover, objet, _perso, _out);
				}
				return;
			}
			if (posAMover != -1 && (idSetObjeto != -1 || objetoMod.getTipo() == 23)
					&& _perso.tieneEquipado(objetoMod.getID())) {
				return;
			}
			Objeto exObj = _perso.getObjPosicion(posAMover);
			int posAntes = 0;
			if (exObj != null) { // SI HAY UN OBJETO DONDE QUIERO MOVER
				posAntes = exObj.getPosicion();
				Objeto obj2 = _perso.getObjSimilarInventario(exObj);
				ObjetoModelo exObjModelo = exObj.getModelo(); // el objeto de la pos donde quiero mover
				int idSetExObj = exObj.getModelo().getSetID(); // la id dels et
				if (obj2 != null) {
					obj2.setCantidad(obj2.getCantidad() + exObj.getCantidad());
					GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso, obj2);
					_perso.borrarObjetoEliminar(exObj.getID(), 1, true);
					GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_out, exObj.getID());
				} else {
					exObj.setPosicion(-1);
					GestorSalida.ENVIAR_OM_MOVER_OBJETO(_perso, exObj);
					if ((idSetExObj >= 81 && idSetExObj <= 92) || (idSetExObj >= 201 && idSetExObj <= 212)) {
						String[] stats = exObjModelo.getStringStatsObj().split(",");
						for (String stat : stats) {
							String[] val = stat.split("#");
							modificacion = Integer.parseInt(val[0], 16) + ";" + Integer.parseInt(val[1], 16) + ";0";
							GestorSalida.ENVIAR_SB_HECHIZO_BOOST_SET_CLASE(_perso, modificacion);
							_perso.delHechizosSetClase(Integer.parseInt(val[1], 16));
						}
						_perso.borrarSetClase(exObjModelo.getID());
					}
				}
				if (_perso.getObjPosicion(1) == null) {
					GestorSalida.ENVIAR_OT_OBJETO_HERRAMIENTA(_out, -1);
					if (_perso.getMapa().esTaller() && _perso.getOficioPublico()) {
						GestorSalida.ENVIAR_EW_OFICIO_MODO_INVITACION(_out, "-", _perso.getID(), "");
					}
					_perso.setStrOficiosPublicos("");
				}
				if (idSetExObj > 0) {
					GestorSalida.ENVIAR_OS_BONUS_SET(_perso, idSetExObj, -1);
				}
			} else {
				posAntes = objMover.getPosicion();
				Objeto obj2 = _perso.getObjSimilarInventario(objMover);
				if (obj2 != null) {
					if (cantObjMover > objMover.getCantidad()) {
						cantObjMover = objMover.getCantidad();
					}
					obj2.setCantidad(obj2.getCantidad() + cantObjMover);
					GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso, obj2);
					if (objMover.getCantidad() - cantObjMover > 0) {
						objMover.setCantidad(objMover.getCantidad() - cantObjMover);
						GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso, objMover);
					} else {
						_perso.borrarObjetoEliminar(objMover.getID(), 1, true);
						GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_out, objMover.getID());
					}
				} else {



					if((objMover.getModelo().getMimos() != 1 && (posAMover == 106 || posAMover == 107 || posAMover == 108 || posAMover == 109 || posAMover == 115))) {
						objMover.setPosicion(-1);
						GestorSalida.ENVIAR_OM_MOVER_OBJETO(_perso, objMover);
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, "No puedes equipar items que no sean Mimos",Colores.ROJO);
					}else
					{
						objMover.setPosicion(posAMover);
						GestorSalida.ENVIAR_OM_MOVER_OBJETO(_perso, objMover);
						if ((objetoMod.getTipo() == 18) && (posAMover == -1)) {
							_perso.setMascota(null);
					}

					}
					if (objMover.getCantidad() > 1) {
						if (cantObjMover > objMover.getCantidad()) {
							cantObjMover = objMover.getCantidad();
						}
						if (objMover.getCantidad() - cantObjMover > 0) {
							int nuevaCant = objMover.getCantidad() - cantObjMover;
							Objeto nuevoObj = Objeto.clonarObjeto(objMover, nuevaCant);
							if (!_perso.addObjetoSimilar(nuevoObj, true, idObjMover)) {
								MundoDofus.addObjeto(nuevoObj, true);
								_perso.addObjetoPut(nuevoObj);
								GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_out, nuevoObj);
							}
							objMover.setCantidad(cantObjMover);
							GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso, objMover);
						}
					}
				}
			}
			Thread.sleep(150);
			_perso.refrescarVida(false);
			GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso);
			GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
			if (_perso.getGrupo() != null) {
				GestorSalida.ENVIAR_PM_ACTUALIZAR_INFO_PJ_GRUPO(_perso.getGrupo(), _perso);
			}

			if (posAMover == 1 || posAMover == 6 || posAMover == 7 || posAMover == 15 || posAMover == 170
					|| posAMover == 55 || (posAMover == -1 && (posAntes == 1 || posAntes == 6 || posAntes == 7
							|| posAntes == 15 || posAntes == 170 || posAntes == 55))) {
				GestorSalida.ENVIAR_Oa_CAMBIAR_ROPA(_perso.getMapa(), _perso);
			}
			Objeto arma = null;
			if (posAMover == -1 && _perso.getObjPosicion(1) == null) {
				GestorSalida.ENVIAR_OT_OBJETO_HERRAMIENTA(_out, -1);
				if (_perso.getMapa().esTaller() && _perso.getOficioPublico()) {
					GestorSalida.ENVIAR_EW_OFICIO_MODO_INVITACION(_out, "-", _perso.getID(), "");
				}
				_perso.setStrOficiosPublicos("");
			} else if (posAMover == 1 && (arma = _perso.getObjPosicion(1)) != null) {
				int idModArma = arma.getModelo().getID();
				for (Entry<Integer, StatsOficio> statOficio : _perso.getStatsOficios().entrySet()) {
					Oficio oficio = statOficio.getValue().getOficio();
					if (oficio.herramientaValida(idModArma)) {
						GestorSalida.ENVIAR_OT_OBJETO_HERRAMIENTA(_out, oficio.getID());
						String strOficioPub = CentroInfo.trabajosOficioTaller(oficio.getID());
						if (_perso.getMapa().esTaller() && _perso.getOficioPublico()) {
							GestorSalida.ENVIAR_EW_OFICIO_MODO_INVITACION(_out, "+", _perso.getID(), strOficioPub);
						}
						_perso.setStrOficiosPublicos(strOficioPub);
						break;
					}
				}
			}

			// posicion visuales



            	 if (posAMover == 106 || posAMover == 107 || posAMover == 108 || posAMover == 109 || posAMover == 115
     					|| (posAMover == -1 && (posAntes == 106 || posAntes == 107 || posAntes == 108 || posAntes == 109
     							|| posAntes == 115))) {
     				if (_perso.getPelea() != null) {
     					GestorSalida.ENVIAR_Oa_CAMBIAR_ROPA_PELEA(_perso, _perso.getPelea());
     				} else {
     					GestorSalida.ENVIAR_Oa_CAMBIAR_ROPA(_perso.getMapa(), _perso);
     					GestorSalida.ENVIAR_GM_REFRESCAR_PJ_EN_MAPA(_perso.getMapa(), _perso);
     				}
     			}else {
     				GestorSalida.ENVIAR_GM_REFRESCAR_PJ_EN_MAPA(_perso.getMapa(), _perso);
     			}



			if (idSetObjeto > 0) {
				GestorSalida.ENVIAR_OS_BONUS_SET(_perso, idSetObjeto, -1);
			}
			if (_perso.getPelea() != null) {
				GestorSalida.ENVIAR_Oa_CAMBIAR_ROPA_PELEA(_perso, _perso.getPelea());
			}
			if (objetoMod.getTipo() == 122 || objetoMod.getTipo() == 123 || objetoMod.getTipo() == 120) {
				_perso.activa = true;
				GestorSalida.ENVIAR_GM_REFRESCAR_PJ_EN_MAPA(_perso.getMapa(), _perso);
			}
		} catch (Exception e) {
			Emu.creaLogs(e);
			GestorSalida.ENVIAR_OdE_ERROR_ELIMINAR_OBJETO(_out);
		}
	}

	private static synchronized void equiparMascota(Objeto objeto, Personaje _perso) {
		for (Mascota mascota : MundoDofus.getTodasMascotas()) {
			if (objeto.getID() == mascota.getID()) {
				_perso.setMascota(mascota);
				GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso);
				GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
				return;
			}
		}
		if (objeto == null) {
			return;
		}
		Calendar calendar = Calendar.getInstance();
		int mes = calendar.get(2) + 1;
		int dia = calendar.get(5);
		int hora = calendar.get(11);
		int minuto = calendar.get(12);
		Mascota mascota = new Mascota(objeto.getID(), 10, objeto.convertirStatsAString(2), 0,
				calendar.get(Calendar.YEAR), mes, dia, hora, minuto, -1, 0, 0, objeto.getIDModelo());
		MundoDofus.addMascota(mascota);
		_perso.setMascota(mascota);
		GestorSalida.ENVIAR_Oa_ACTUALIZAR_FIGURA_PJ(_perso);
		GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso);
		GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
	}

	private synchronized static void alimentarMascota(Objeto comida, Objeto masc, int cantidad, Personaje _perso,
			PrintWriter _out) {
		try {
			Mascota mascota = MundoDofus.getMascota(masc.getID());
			if (mascota == null || _perso.getMascota() == null) {
				GestorSalida.ENVIAR_BN_NADA(_out);
				return;
			}
			if (!mascota.horaComer(_perso)) {
				// GestorSalida.ENVIAR_Im_INFORMACION(_out, "026");
				return;
			}
			if (comida.getCantidad() - cantidad > 0) {
				int nuevaCant = comida.getCantidad() - cantidad;
				comida.setCantidad(nuevaCant);
				GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso, comida);
			} else {
				GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_out, comida.getID());
				_perso.borrarObjetoRemove(comida.getID());
				MundoDofus.eliminarObjeto(comida.getID());
			}
			int idModComida = comida.getIDModelo();
			if (!mascota.esComestible(idModComida)) {
				GestorSalida.ENVIAR_Im_INFORMACION(_out, "153");
				return;
			}
			String stt = masc.convertirStatsAString(1).replace("320#0#0#a,", "").replace(",320#0#0#a", "")
					.replace("320#0#0#1,", "").replace(",320#0#0#1", "").replace("320#0#0#00,", "")
					.replace(",320#0#0#00", "");
			mascota.comerComida(idModComida);
			mascota.setPDV(mascota.getPDV() + 1);
			if (mascota.getNroComidas() == 1) {
				mascota.setStringStats(stt + "," + mascota.analizarStatsMascota());
			}
			masc.clearTodo();
			StringBuilder newstats = new StringBuilder("");
			String vidamasc = "320#0#0#" + Integer.toHexString(mascota.getPDV());
			boolean primeros = false;
			for (String str : mascota.getStringStats().split(",")) {
				if (primeros) {
					newstats.append(",");
				}
				String elstat = str.split("#")[0];
				if (elstat.equals("320")) {
					newstats.append(vidamasc);
				} else {
					newstats.append(str);
				}
				primeros = true;
			}
			mascota.setStringStats(newstats.toString());
			masc.convertirStringAStats(mascota.getStringStats());
			GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETOMASCO(_out, masc);
			GestorSalida.ENVIAR_Im_INFORMACION(_out, "032");
			GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso);
			GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
			// GestorSalida.ENVIAR_Im_INFORMACION(_out, "153"); NO LE GUSTA
		} catch (Exception e) {
			Emu.creaLogs(e);
			System.out.print("alimentar mascota " + e.getMessage());
		}
	}

	private static synchronized void aparienciaObjevivo(String packet, Personaje _perso, PrintWriter _out) {
		try {
			int idObjeto = Integer.parseInt(packet.substring(2).split("\\|")[0]);
			Objeto objeto = MundoDofus.getObjeto(idObjeto);
			if (objeto == null) {
				GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out, Idiomas.getTexto(_perso.getCuenta().idioma, 15));
				return;
			}
			int objeviId = objeto.getObjeviID();
			Objevivo objevi = MundoDofus.getObjevivos(objeviId);
			if (objevi == null) {
				GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out, Idiomas.getTexto(_perso.getCuenta().idioma, 15));
				return;
			}
			int aparienciaId = Integer.parseInt(packet.split("\\|")[2]);
			if (aparienciaId > 20 || aparienciaId <= 0 || packet.split("\\|")[2].length() > 2) {
				return;
			}
			objevi.setSkin(aparienciaId);
			GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(_out, objeto);
			GestorSalida.ENVIAR_Oa_ACTUALIZAR_FIGURA_PJ(_perso);
		} catch (Exception e) {
			Emu.creaLogs(e);
			System.out.print("apariencia objevivo " + e.getMessage());
		}
	}

	private static synchronized void alimentarObjevivo(String packet, Personaje _perso, PrintWriter _out) {
		try {
			int idObjeto = Integer.parseInt(packet.substring(2).split("\\|")[0]);
			Objeto objeto = MundoDofus.getObjeto(idObjeto);
			int idObjAlimento = Integer.parseInt(packet.split("\\|")[2]);
			Objeto objetoAlimento = MundoDofus.getObjeto(idObjAlimento);
			if (objetoAlimento == null || objeto == null) {
				GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out, Idiomas.getTexto(_perso.getCuenta().idioma, 15));
				return;
			}
			Objevivo objevi = MundoDofus.getObjevivos(objeto.getObjeviID());
			if (objevi == null) {
				GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out, Idiomas.getTexto(_perso.getCuenta().idioma, 15));
				return;
			}
			if (objetoAlimento.getCantidad() > 1) {
				if (objetoAlimento.getCantidad() - 1 > 0) {
					int nuevaCant = objetoAlimento.getCantidad() - 1;
					Objeto nuevoObj = Objeto.clonarObjeto(objetoAlimento, nuevaCant);
					if (!_perso.addObjetoSimilar(nuevoObj, true, idObjAlimento)) {
						MundoDofus.addObjeto(nuevoObj, true);
						_perso.addObjetoPut(nuevoObj);
						GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_out, nuevoObj);
					}
					objetoAlimento.setCantidad(1);
					GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso, objetoAlimento);
				}
			}
			int aÑo = Calendar.getInstance().get(1);
			int mes = Calendar.getInstance().get(2);
			int dia = Calendar.getInstance().get(5);
			int hora = Calendar.getInstance().get(11);
			int minuto = Calendar.getInstance().get(12);
			objevi.setFeedYears(aÑo);
			objevi.setFeedDate(mes * 100 + dia);
			objevi.setFeedHours(hora * 100 + minuto);
			int xp = Integer.parseInt(Integer.toString(objetoAlimento.getModelo().getNivel()));
			objevi.setXp(objevi.getXp() + xp);
			MundoDofus.eliminarObjeto(idObjAlimento);
			_perso.borrarObjetoRemove(idObjAlimento);
			GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_out, idObjAlimento);
			GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(_out, objeto);
			GestorSalida.ENVIAR_Oa_ACTUALIZAR_FIGURA_PJ(_perso);
			GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
			GestorSQL.SALVAR_OBJEVIVO(objevi);
		} catch (Exception e) {
			Emu.creaLogs(e);
			System.out.print("alimentar objevivo " + e.getMessage());
		}
	}

	private static synchronized void equiparObjevivo(Objeto objevivo, Objeto objeto, Personaje _perso,
			PrintWriter _out) {
		int idSetModelo = objeto.getModelo().getSetID();
		if (((idSetModelo >= 81) && (idSetModelo <= 92))) {
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 16));
			return;
		}
		try {
			Objevivo objevi = MundoDofus.getObjevivos(objevivo.getID());
			if (objevi != null) {
				if (objevi.getAsociado() == 0) {
					objeto.setObjeviID(objevi.getID());// tabla objetos
					objevivo.setObjeviID(0);
					// tabla objevivos
					objevi.setStat(objeto.convertirStatsAStringSinObjevivo());
					objevi.setAsociado(objeto.getID());
					objeto.convertirStringAStats(objevi.getStat());
					_perso.borrarObjetoRemove(objevivo.getID());
					GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_out, objevivo.getID());
					GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(_out, objeto);
					GestorSalida.ENVIAR_Oa_ACTUALIZAR_FIGURA_PJ(_perso);
					GestorSQL.SALVAR_OBJEVIVO(objevi);
				}
				return;
			}
			Calendar calendar = Calendar.getInstance();
			int aÑo = calendar.get(1);
			int mes = calendar.get(2);
			int dia = calendar.get(5);
			int comidaAño = mes * 100 + dia;
			int hora = calendar.get(11);
			int minuto = calendar.get(12);
			int comidaHora = hora * 100 + minuto;

			int aÑoInter = (mes + 3) / 12 + aÑo;
			int mesInterc = ((mes + 3) % 12) * 100 + dia;
			int horaInterc = hora * 100 + minuto;

			Objevivo nuevoObjevivo = new Objevivo(objevivo.getID(),
					aÑo + "~" + comidaAño + "~" + comidaHora + "~" + 1 + "~" + 1 + "~" + objeto.getModelo().getTipo()
							+ "~" + objeto.getID() + "~" + 0,
					aÑoInter + "~" + mesInterc + "~" + horaInterc + "~" + aÑo + "~" + comidaAño + "~" + comidaHora + "~"
							+ objevivo.getIDModelo(),
					objevivo.getID(), objeto.convertirStatsAString(0));
			MundoDofus.addObjevivo(nuevoObjevivo);
			GestorSQL.AGREGAR_OBJEVIVOS(nuevoObjevivo);
			objeto.setObjeviID(objevivo.getID());
			objeto.convertirStringAStats(nuevoObjevivo.getStat());
			_perso.borrarObjetoRemove(objevivo.getID());
			GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_out, objevivo.getID());
			GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(_out, objeto);
			GestorSalida.ENVIAR_Oa_ACTUALIZAR_FIGURA_PJ(_perso);
		} catch (Exception e) {
			Emu.creaLogs(e);
			System.out.print("equipar objevivo " + e.getMessage());
		}
	}

	private static synchronized void desequiparObjevivo(String packet, Personaje _perso, PrintWriter _out) {
		try {
			int id = Integer.parseInt(packet.substring(2).split("\\|")[0]);
			if ((id == -1) || (!_perso.tieneObjetoID(id))) {
				return;
			}
			Objeto objeto = MundoDofus.getObjeto(id);
			if (objeto == null) {
				return;
			}
			int idObjevivo = objeto.getObjeviID(); // coge la columna objevivo de objetos
			if (idObjevivo < 0) {
				return;
			}
			Objevivo objevivo = MundoDofus.getObjevivos(idObjevivo);
			if (objevivo == null || objeto == null) {
				GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out, Idiomas.getTexto(_perso.getCuenta().idioma, 15));
				GestorSalida.ENVIAR_BN_NADA(_out);
				return;
			}
			if (objevivo.getAsociado() != 0) {
				Objeto objObjevivo = MundoDofus.getObjeto(objevivo.getID());
				if (objObjevivo == null) {
					GestorSalida.ENVIAR_BN_NADA(_out);
					return;
				}
				objeto.convertirStringAStats(objevivo.getStat());
				objeto.setObjeviID(0);
				objObjevivo.setObjeviID(idObjevivo);// le da al item objevivo el id del objevivo
				objObjevivo.setStats(Objeto.generateNewStatsFromTemplate(objevivo.convertirAString(), false));
				objevivo.setAsociado(0);// le da valor 0 a asociado del objevivo
				objevivo.setRealModeloDB(objObjevivo.getIDModelo());
				objevivo.setStat(objevivo.convertirAString());
				_perso.addObjetoPut(objObjevivo);// se agrega a personaje el objeto objevivo
				GestorSQL.ACTUALIZAR_STATS_OBJETO(objeto, objevivo.getStat());
				GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_out, objObjevivo);
				GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(_out, objeto);
				GestorSalida.ENVIAR_Oa_ACTUALIZAR_FIGURA_PJ(_perso);
				GestorSQL.SALVAR_OBJEVIVO(objevivo);
				Thread.sleep(200);
			}
		} catch (Exception e) {
			Emu.creaLogs(e);
			System.out.print("Error al desequipar objevivo " + e.getMessage());
		}
	}

	static void analizar_Dialogos(String packet, Personaje _perso, PrintWriter _out) {
		switch (packet.charAt(1)) {
		case 'C':// Demanda la pregunta
			dialogo_Iniciar(packet, _perso, _out);
			break;
		case 'R':// Respuesta del jugador
			dialogo_Respuesta(packet, _perso, _out);
			break;
		case 'V':// Fin del dialogo
			dialogo_Fin(_perso, _out);
			break;
		}
	}

	private static void dialogo_Respuesta(String packet, Personaje _perso, PrintWriter _out) {
		try {
			String[] infos = packet.substring(2).split("\\|");
			int preguntaID = -1;
			int respuestaID = -1;
			try {
				preguntaID = Integer.parseInt(infos[0]);
				respuestaID = Integer.parseInt(infos[1]);
			} catch (Exception e) {
				return;
			}
			if (preguntaID <= 0 || respuestaID <= 0) {
				return;
			}
			PreguntaNPC pregunta = MundoDofus.getNPCPregunta(preguntaID);
			RespuestaNPC respuesta = MundoDofus.getNPCreponse(respuestaID);
			if (pregunta == null || respuesta == null) {
				GestorSalida.ENVIAR_DV_FINALIZAR_DIALOGO(_out);
				_perso.setConversandoCon(0);
				_perso.sethablandoNPC(0);
			}
			_perso.setultDialog(respuestaID);
			if (_perso.getConversandoCon() == 0) {
				return;
			}
			Misiones.checkMision(_perso, 0);
			if (_perso != null && respuesta != null) {
				NPC npcs = _perso.getMapa().getNPC(_perso.getConversandoCon());
				if (npcs == null) {
					GestorSalida.ENVIAR_DV_FINALIZAR_DIALOGO(_out);
					_perso.setConversandoCon(0);
					_perso.sethablandoNPC(0);
					return;
				}
				if (!npcs.respuestax.contains(respuestaID)) {
					int preguntaid = npcs.getModeloBD().getPreguntaID();
					getMasPreguntas(_perso, preguntaid, npcs);
				}
				if (npcs.respuestax.contains(respuestaID)) {
					respuesta.aplicar(_perso);
				} else {
					GestorSalida.ENVIAR_DV_FINALIZAR_DIALOGO(_out);
					_perso.setConversandoCon(0);
					_perso.sethablandoNPC(0);
				}
			} else {
				if (pregunta == null || respuesta == null || !respuesta.esOtroDialogo()) {
					GestorSalida.ENVIAR_DV_FINALIZAR_DIALOGO(_out);
					_perso.setConversandoCon(0);
					_perso.sethablandoNPC(0);
				}
			}
		} catch (Exception e) {
			Emu.creaLogs(e);
			GestorSalida.ENVIAR_DV_FINALIZAR_DIALOGO(_out);
			_perso.setConversandoCon(0);
			_perso.sethablandoNPC(0);
		}
	}

	private static void getMasPreguntas(Personaje _perso, int pregunta, NPC npcs) {
		if (npcs.preguntax.contains(pregunta)) {
			return;
		}
		npcs.preguntax.add(pregunta);
		if (MundoDofus.getNPCPregunta(pregunta) != null) {
			for (String resp : MundoDofus.getNPCPregunta(pregunta).getRespuestas().split(";")) {
				int numeroxx = 0;
				try {
					numeroxx = Integer.parseInt(resp);
				} catch (Exception e) {
					numeroxx = 0;
				}
				if (!npcs.respuestax.contains(numeroxx)) {
					npcs.respuestax.add(numeroxx);
				}
				if (numeroxx > 0) {
					if (MundoDofus.getNPCreponse(numeroxx) != null) {
						for (Accion acc : MundoDofus.getNPCreponse(numeroxx)._acciones) {
							int numerox = 0;
							try {
								numerox = Integer.parseInt(acc._args);
							} catch (Exception e) {
								numerox = 0;
							}
							if (acc.getID() == 1 && numerox > 0) {
								getMasPreguntas(_perso, numerox, npcs);
							}
						}
					}
				}
			}
		}
	}

	private static void dialogo_Fin(Personaje _perso, PrintWriter _out) {
		GestorSalida.ENVIAR_DV_FINALIZAR_DIALOGO(_out);
		if (_perso.getConversandoCon() != 0) {
			_perso.setConversandoCon(0);
		}
		if (_perso.gethablandoNPC() != 0) {
			_perso.sethablandoNPC(0);
		}
	}

	private static void dialogo_Iniciar(String packet, Personaje _perso, PrintWriter _out) {
		try {
			if (!_perso.puedeAbrir) {
				GestorSalida.ENVIAR_BN_NADA(_out);
				return;
			}
			int ID = Integer.parseInt(packet.substring(2).split((char) 0x0A + "")[0]);
			if (ID > -50) {
				int npcID = ID;
				NPC npc = _perso.getMapa().getNPC(npcID);
				if (npc == null) {
					return;
				}
				GestorSalida.ENVIAR_DCK_CREAR_DIALOGO(_out, npcID);
				int pID = 0;
				boolean entra = false;
				_perso.setConversandoCon(npcID);
				_perso.sethablandoNPC(npc.getModeloBD().getID());
				boolean returna = false;
				try {
					returna = Misiones.checkMision(_perso, 0);
				} catch (Exception e) {
					returna = false;
					Emu.creaLogs(e);
				}
				boolean finalizado = false;
				try {
					if (npc.getModeloBD().get_misionID() != 0) {// misionid
						int misionid = npc.getModeloBD().get_misionID();
						for (String exmd : _perso.get_misiones().split("\\|")) {
							if (exmd.equals("") || entra) {
								continue;
							}
							int misID = Integer.parseInt(exmd.split(";")[0]);
							int finaliado = Integer.parseInt(exmd.split(";")[1]);
							if (misID == misionid && finaliado == 1) {
								finalizado = true;
								entra = true;
								break;
							} else if (misID == misionid && finaliado == 0) {
								finalizado = false;
								entra = true;
								break;
							}
						}
					}
				} catch (Exception e) {
					Emu.creaLogs(e);
				}
				if (!entra) {
					if (npc.getModeloBD().getID() == 1350 && MundoDofus.liderRanking == "Ninguno"
							|| npc.getModeloBD().getID() == 1351 && MundoDofus.liderRanking2 == "Ninguno"
							|| npc.getModeloBD().getID() == 1352 && MundoDofus.liderRanking3 == "Ninguno") {
						pID = 80265;
					} else {
						pID = npc.getModeloBD().getPreguntaID();
					}
				} else {
					int randomt = Formulas.getRandomValor(1, 4);
					if (finalizado) {
						if (randomt == 1) {
							pID = 11;
						} else if (randomt == 2) {
							pID = 13;
						} else if (randomt == 3) {
							pID = 14;
						} else if (randomt == 4) {
							pID = 15;
						} else {
							pID = 15;
						}
					} else {
						pID = 10;
					}
				}
				if (returna && finalizado) {
					pID = 16;
				}
				PreguntaNPC pregunta = MundoDofus.getNPCPregunta(pID);
				if (pregunta == null) {
					GestorSalida.ENVIAR_DV_FINALIZAR_DIALOGO(_out);
					return;
				}
				/*
				 * if (pID == 1010) { StringBuilder str = new StringBuilder(); boolean fir =
				 * false; for (Integer id : MundoDofus.mazmorrasElegidas) { if (fir)
				 * str.append(";"); str.append(id); fir = true; }
				 * GestorSalida.ENVIAR_DQ_DIALOGO_PREGUNTA(_out, "1010|"+str.toString()); } else
				 */
				GestorSalida.ENVIAR_DQ_DIALOGO_PREGUNTA(_out, pregunta.stringArgParaDialogo(_perso));
				// GestorSalida.ENVIAR_MUSICA_ID(_perso, "1");
			} else {
				Recaudador recauda = MundoDofus.getRecaudador(ID);
				if (recauda == null) {
					return;
				}
				GestorSalida.ENVIAR_DCK_CREAR_DIALOGO(_out, ID);
				PreguntaNPC pregunta = MundoDofus.getNPCPregunta(1);
				if (pregunta == null) {
					GestorSalida.ENVIAR_DV_FINALIZAR_DIALOGO(_out);
					return;
				}
				Gremio gremio = MundoDofus.getGremio(recauda.getGremioID());
				GestorSalida.ENVIAR_DQ_DIALOGO_PREGUNTA(_out, pregunta.stringGremio(_perso, gremio));
				_perso.setConversandoCon(ID);
				_perso.sethablandoNPC(recauda.getID());
			}
		} catch (Exception e) {
			Emu.creaLogs(e);
		}
	}

	static void analizar_Intercambios(String packet, Personaje _perso, PrintWriter _out, Cuenta _cuenta) {
		switch (packet.charAt(1)) {
		case 'A':// aceptar abrir la ventana de intercambio
			intercambio_Aceptar(_perso, _out);
			break;
		case 'B':// compra de un item
			intercambio_Comprar(packet, _perso, _out);
			break;
		case 'f':// montura a criar
			intercambio_Cercado(packet, _perso, _out, _cuenta);
			break;
		case 'H':// Demanda precio recurso + categoria
			intercambio_Mercadillo(packet, _perso, _out);
			break;
		case 'J':// oficios
			intercambio_Oficios(packet, _perso);
			break;
		case 'K':// Ok
			intercambio_Ok(_perso, _out, packet);
			break;
		case 'L':// oficio : repetir el craft antecedente
			intercambio_Repetir(_perso);
			break;
		case 'M':// Mover (agregar retirar un objeto al intercambio)
			intercambio_Mover_Objeto(packet, _perso, _out);
			break;
		case 'q':// pregunta si desea entrar a modo mercante
			intercambio_Preg_Mercante(_perso);
			break;
		case 'P':
			intercambio_Pago_Por_Trabajo(packet, _perso);
			break;
		case 'Q':
			intercambio_Ok_Mercante(_perso, _out, _cuenta);
			break;
		case 'r':// Montura
			intercambio_Establo(packet, _perso, _out, _cuenta);
			break;
		case 'R':// iniciar
			intercambio_Iniciar(packet, _perso, _out);
			break;
		case 'S':// Venta
			intercambio_Vender(packet, _perso, _out);
			break;
		case 'V':// Fin del intercambio
			intercambio_Cerrar(_perso, _out);
			break;
		case 'W':// oficio modo publico
			intercambio_Oficio_Publico(packet, _perso, _out);
			break;
		}
	}

	private static void intercambio_Oficios(String packet, Personaje _perso) {
		switch (packet.charAt(2)) {
		case 'F':
			int Oficio = Integer.parseInt(packet.substring(3));
			boolean entra = false;
			for (Personaje artesano : MundoDofus.getPJsEnLinea()) {
				entra = true;
				if (artesano.getStatsOficios().isEmpty()) {
					GestorSalida.ENVIAR_BN_NADA(_perso);
					continue;
				}
				int id = artesano.getID();
				String nombre = artesano.getNombre();
				String colores = artesano.getColor1() + "," + artesano.getColor2() + "," + artesano.getColor3();
				String accesorios = artesano.getStringAccesorios();
				int sexo = artesano.getSexo();
				int mapa = artesano.getMapa().getID();
				int entaller = (mapa == 8731 || mapa == 8732) ? 1 : 0;
				int clase = artesano.getClase(true);
				for (StatsOficio oficio : artesano.getStatsOficios().values()) {
					if (oficio.getOficio().getID() != Oficio) {
						continue;
					}
					GestorSalida.ENVIAR_EJ_DESCRIPCION_LIBRO_ARTESANO(_perso,
							"+" + oficio.getOficio().getID() + ";" + id + ";" + nombre + ";" + oficio.getNivel() + ";"
									+ mapa + ";" + entaller + ";" + clase + ";" + sexo + ";" + colores + ";"
									+ accesorios + ";" + oficio.getOpcionBin() + "," + oficio.getSlotsPublico());
				}
			}
			if (entra) {
				GestorSalida.ENVIAR_BN_NADA(_perso);
			}
			break;
		}
	}

	private static void intercambio_Oficio_Publico(String packet, Personaje _perso, PrintWriter _out) {
		switch (packet.charAt(2)) {
		case '+':
			_perso.setOficioPublico(true);
			/*
			 * for (StatsOficio oficio : _perso.getStatsOficios().values()) { int
			 * idModOficio = oficio.getOficio().getID();
			 * GestorSalida.ENVIAR_Ej_AGREGAR_LIBRO_ARTESANO(_perso, "+" + idModOficio); }
			 */
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 17), Colores.VERDE);

			GestorSalida.ENVIAR_EW_OFICIO_MODO_PUBLICO(_out, "+");
			if (_perso.getMapa().esTaller()) {
				GestorSalida.ENVIAR_EW_OFICIO_MODO_INVITACION(_out, "+", _perso.getID(),
						_perso.getStringOficiosPublicos());
			}
			break;
		case '-':
			_perso.setOficioPublico(false);
			/*
			 * for (StatsOficio oficio : _perso.getStatsOficios().values()) {
			 * GestorSalida.ENVIAR_Ej_AGREGAR_LIBRO_ARTESANO(_perso, "-" +
			 * oficio.getOficio().getID()); }
			 */
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 18), Colores.ROJO);

			GestorSalida.ENVIAR_EW_OFICIO_MODO_PUBLICO(_out, "-");
			GestorSalida.ENVIAR_EW_OFICIO_MODO_INVITACION(_out, "-", _perso.getID(), "");
			break;
		}
	}

	private static void intercambio_Mover_Objeto(String packet, Personaje _perso, PrintWriter _out) {
		if (_perso.getTallerInvitado() != null) {
			InvitarTaller taller = _perso.getTallerInvitado();
			switch (packet.charAt(2)) {
			case 'O':
				if (packet.charAt(3) == '+') {
					String[] infos = packet.substring(4).split("\\|");
					int id = -1;
					int cant = -1;
					try {
						id = Integer.parseInt(infos[0]);
						cant = Integer.parseInt(infos[1]);
					} catch (NumberFormatException e) {
						Emu.creaLogs(e);
					}
					if (id == -1 || cant == -1) {
						return;
					}
					try {
						int cantInter = taller.getCantObjeto(id, _perso.getID());
						if (!_perso.tieneObjetoID(id)) {
							return;
						}
						Objeto obj = MundoDofus.getObjeto(id);
						if (obj == null) {
							return;
						}
						int nuevaCant = obj.getCantidad() - cantInter;
						if (cant > nuevaCant) {
							cant = nuevaCant;
						}
						taller.addObjeto(obj, cant, _perso.getID());
					} catch (NullPointerException e) {
						Emu.creaLogs(e);
					}
				} else {
					String[] infos = packet.substring(4).split("\\|");
					try {
						int id = Integer.parseInt(infos[0]);
						int cant = Integer.parseInt(infos[1]);
						if ((cant <= 0) || !_perso.tieneObjetoID(id)) {
							return;
						}
						Objeto obj = MundoDofus.getObjeto(id);
						if (obj == null) {
							return;
						}
						int cantInter = taller.getCantObjeto(id, _perso.getID());
						if (cant > cantInter) {
							cant = cantInter;
						}
						taller.borrarObjeto(obj, cant, _perso.getID());
					} catch (NumberFormatException e) {
						Emu.creaLogs(e);
					}
				}
				break;
			}
			return;
		} else if (_perso.getRecaudando()) {
			Recaudador recaudador = MundoDofus.getRecaudador(_perso.getRecaudandoRecauID());
			if (recaudador == null || recaudador.getEstadoPelea() > 0) {
				return;
			}
			switch (packet.charAt(2)) {
			case 'G':// Kamas
				if (packet.charAt(3) == '-') {
					long kamas = Integer.parseInt(packet.substring(4));
					long kamasRetiradas = recaudador.getKamas() - kamas;
					if (kamasRetiradas < 0) {
						kamasRetiradas = 0;
						kamas = recaudador.getKamas();
					}
					recaudador.setKamas(kamasRetiradas);
					_perso.addKamas(kamas);
					GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
					GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(_perso, "G" + recaudador.getKamas());
				}
				break;
			case 'O':// Objetos
				if (packet.charAt(3) == '-') {
					String[] infos = packet.substring(4).split("\\|");
					int id = 0;
					int cant = 0;
					try {
						id = Integer.parseInt(infos[0]);
						cant = Integer.parseInt(infos[1]);
					} catch (NumberFormatException e) {
						return;
					}
					if (id <= 0 || cant <= 0) {
						return;
					}
					Objeto obj = MundoDofus.getObjeto(id);
					if (obj == null) {
						return;
					}
					if (recaudador.tieneObjeto(id)) {
						recaudador.borrarDesdeRecaudador(_perso, id, cant);
					}
				}
				break;
			}
			_perso.getGremio().addXp(recaudador.getXp());
			recaudador.setXp(0);
			GestorSQL.ACTUALIZAR_GREMIO(_perso.getGremio());
			return;
		} else if (_perso.getRompiendo()) {
			if (packet.charAt(2) == 'O') {
				if (packet.charAt(3) == '+') {
					if (_perso.paramag.get(0) == null || _perso.paramag.get(1) == null) {
						String[] Infos = packet.substring(4).split("\\|");
						try {
							int id = Integer.parseInt(Infos[0]);
							int cantidad = 1;
							if (!_perso.tieneObjetoID(id)) {
								return;
							}
							Objeto Obj = MundoDofus.getObjeto(id);
							if (Obj == null) {
								return;
							}
							if (Obj.getModelo().getTipo() == 18) {
								GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out,
										Idiomas.getTexto(_perso.getCuenta().idioma, 19));
								return;
							}
							if (Obj.getModelo().getTipo() == 113) {
								GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out,
										Idiomas.getTexto(_perso.getCuenta().idioma, 20));
								return;
							} // TODO:
							/*
							 * if (_perso.paramag.get(0) == null && Obj.getModelo().getTipo() != 39) {
							 * GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out,
							 * Idiomas.getTexto(_perso.getCuenta().idioma, 37)); return; }
							 */
							if (Emu.objetosLoot.contains(Obj.getModelo().getID())) {
								GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out,
										Idiomas.getTexto(_perso.getCuenta().idioma, 37));
								return;
							}
							if (Obj.getModelo().getTipo() == 39) {
								if (_perso.paramag.get(0) != null) {
									return;
								}
								_perso.paramag.put(0, id);
							} else {
								boolean puede = false;
								switch (Obj.getModelo().getTipo()) {
								case 1:
								case 2:
								case 3:
								case 4:
								case 5:
								case 6:
								case 7:
								case 8:
								case 9:
								case 10:
								case 11:
								case 16:
								case 17:
								case 18:
								case 19:
								case 23:
								case 81:
								case 82:
								case 113:
								case 114:
									puede = true;
									break;
								}
								if (Obj.getObjeviID() != 0) {
									puede = false;
								}
								if (!puede) {
									GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out,
											Idiomas.getTexto(_perso.getCuenta().idioma, 21));
									return;
								}
								if (_perso.paramag.get(1) != null) {
									return;
								}
								_perso.paramag.put(1, id);
							}
							GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_out, 'O', "+", id + "|" + cantidad);
						} catch (Exception e) {
							Emu.creaLogs(e);
						}
					}
				} else if (packet.charAt(3) == '-') {
					String[] Infos = packet.substring(4).split("\\|");
					try {
						int id = Integer.parseInt(Infos[0]);
						int cantidad = 1;
						Objeto Obj = MundoDofus.getObjeto(id);
						if (Obj == null) {
							return;
						}
						/*
						 * if (_perso.paramag.get(1) != null) { if (_perso.paramag.get(1) !=
						 * Obj.getID()) { GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out,
						 * Idiomas.getTexto(_perso.getCuenta().idioma, 38)); return; } }
						 */
						if (Obj.getModelo().getTipo() == 39) {
							if (_perso.paramag.get(0) == null) {
								return;
							}
							_perso.paramag.remove(0);
						} else {
							if (_perso.paramag.get(1) == null) {
								return;
							}
							_perso.paramag.remove(1);
						}
						GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_out, 'O', "-", id + "|" + cantidad);
					} catch (Exception e) {
						Emu.creaLogs(e);
					}
				}
			} else if (packet.charAt(2) == 'R') {
				try {
					int veces = Integer.parseInt(packet.substring(3));
					startRomperObj(_perso, _out, veces);
				} catch (Exception e) {
					Emu.creaLogs(e);
				}
			} else if (packet.charAt(2) == 'r') {
				_perso.rompemineral = true;
				_perso.bucle = false;
			}
			return;
		} else if (_perso.getHaciendoTrabajo() != null) {
			if (!_perso.getHaciendoTrabajo().esReceta()) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 22),
						Colores.ROJO);
				return;
			}
			if (packet.charAt(2) == 'O') {
				if (packet.charAt(3) == '+') {
					String[] Infos = packet.substring(4).split("\\|");
					try {
						int id = 0;
						int cantidad = 0;
						boolean prim = false;
						if (Infos.length <= 0) {
							GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out,
									Idiomas.getTexto(_perso.getCuenta().idioma, 23));
							return;
						}
						for (String str : Infos) {
							if (str.equals("") || str.equals(" ") || str.equals("\"\"") || str.equals("\"")) {
								continue;
							}
							if (!str.contains("+") && !str.contains("-")) {
								if (!prim) {
									prim = true;
									id = Integer.parseInt(str);
								} else {
									prim = false;
									cantidad = Integer.parseInt(str);
								}
							} else if (str.contains("-")) {
								int cantspli = Integer.parseInt(str.split("\\-")[0]);
								int itemidspli = Integer.parseInt(str.split("\\-")[1]);
								if (!_perso.tieneObjetoID(itemidspli) || cantspli <= 0) {
									break;
								}
								Objeto Obj = MundoDofus.getObjeto(id);
								if (Obj == null) {
									return;
								}
								_perso.getHaciendoTrabajo().modificarIngrediente(_out, itemidspli, -cantspli);
							} else if (str.contains("+")) {
								int cantspli = Integer.parseInt(str.split("\\+")[0]);
								int itemidspli = Integer.parseInt(str.split("\\+")[1]);
								if (!_perso.tieneObjetoID(itemidspli) || cantspli <= 0) {
									break;
								}
								Objeto Obj = MundoDofus.getObjeto(id);
								if (Obj == null) {
									return;
								}
								_perso.getHaciendoTrabajo().modificarIngrediente(_out, itemidspli, cantspli);
							}
						}
						if ((cantidad <= 0) || !_perso.tieneObjetoID(id)) {
							return;
						}
						Objeto Obj = MundoDofus.getObjeto(id);
						if (Obj == null) {
							return;
						}
						if (Obj.getModelo().getTipo() == 18) {
							GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out,
									Idiomas.getTexto(_perso.getCuenta().idioma, 24));
							return;
						}
						if (Obj.getObjeviID() != 0 || Obj.getModelo().getTipo() == 113) {
							GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 25),
									Emu.COLOR_MENSAJE);
							return;
						}
						if (Obj.getCantidad() < cantidad) {
							cantidad = Obj.getCantidad();
						}
						if (cantidad <= 0) {
							return;
						}
						if (cantidad > 300) {
							GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 26),
									Emu.COLOR_MENSAJE);
							cantidad = 300;
						}
						_perso.getHaciendoTrabajo().modificarIngrediente(_out, id, cantidad);
					} catch (NumberFormatException e) {
						Emu.creaLogs(e);
					}
				} else if (packet.charAt(3) == '-') {
					String[] Infos = packet.substring(4).split("\\|");
					try {
						int id = 0;
						int cantidad = 0;
						boolean prim = false;
						for (String str : Infos) {
							if (!str.contains("-") && !str.contains("+")) {
								if (!prim) {
									prim = true;
									id = Integer.parseInt(str);
								} else {
									prim = false;
									cantidad = Integer.parseInt(str);
								}
							} else if (str.contains("-")) {
								int cantspli = Integer.parseInt(str.split("\\-")[0]);
								int itemidspli = Integer.parseInt(str.split("\\-")[1]);
								if (!_perso.tieneObjetoID(itemidspli) || cantspli <= 0) {
									break;
								}
								Objeto Obj = MundoDofus.getObjeto(id);
								if (Obj == null) {
									return;
								}
								_perso.getHaciendoTrabajo().modificarIngrediente(_out, itemidspli, -cantspli);
							} else if (str.contains("+")) {
								int cantspli = Integer.parseInt(str.split("\\+")[0]);
								int itemidspli = Integer.parseInt(str.split("\\+")[1]);
								if (!_perso.tieneObjetoID(itemidspli) || cantspli <= 0) {
									break;
								}
								Objeto Obj = MundoDofus.getObjeto(id);
								if (Obj == null) {
									return;
								}
								_perso.getHaciendoTrabajo().modificarIngrediente(_out, itemidspli, cantspli);
							}
						}
						if (cantidad <= 0) {
							return;
						}
						Objeto Obj = MundoDofus.getObjeto(id);
						if (Obj == null) {
							return;
						}
						if (Obj.getModelo().getTipo() == 18) {
							GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out,
									Idiomas.getTexto(_perso.getCuenta().idioma, 24));
							return;
						}
						if (Obj.getObjeviID() != 0 || Obj.getModelo().getTipo() == 113) {
							GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 25),
									Colores.ROJO);
							return;
						}
						_perso.getHaciendoTrabajo().modificarIngrediente(_out, id, -cantidad);
					} catch (NumberFormatException e) {
						Emu.creaLogs(e);
					}
				}
			} else if (packet.charAt(2) == 'R') {
				try {
					int veces = Integer.parseInt(packet.substring(3)) + 1;// TODO: FM
					_perso.getHaciendoTrabajo().magearNormaloFM(veces, _perso);
				} catch (Exception e) {
					Emu.creaLogs(e);
				}
			} else if (packet.charAt(2) == 'r') { // Return | Skryn :D
				try {
					_perso.getHaciendoTrabajo().breakFM(true);
				} catch (Exception e) {
					Emu.creaLogs(e);
				}
			}
			return;
		} else if (_perso.getIntercambiandoCon() < 0 && !_perso.getMochilaMontura() && !_perso.getRompiendo()) {// HDV
			switch (packet.charAt(3)) {
			case '-':
				int cheapestID = Integer.parseInt(packet.substring(4).split("\\|")[0]);
				int cant = Integer.parseInt(packet.substring(4).split("\\|")[1]);
				if (cant <= 0) {
					return;
				}
				_perso.getCuenta().recuperarObjeto(cheapestID, cant);
				break;
			case '+':// Poner un objeto en venta
				int objetoID = Integer.parseInt(packet.substring(4).split("\\|")[0]);
				int cantidad = Integer.parseInt(packet.substring(4).split("\\|")[1]);
				int precio = 0;
				try {
					precio = Integer.parseInt(packet.substring(4).split("\\|")[2]);
				} catch (ArrayIndexOutOfBoundsException e) {
					precio = 0;
				}
				if (cantidad <= 0 || precio <= 0) {
					return;
				}
				int intercamcon = _perso.getIntercambiandoCon();
				if (_perso.abrepanelinter) {
					intercamcon = -7411;
				}
				Mercadillo puesto = MundoDofus.getPuestoMerca(Math.abs(intercamcon));
				if (!_perso.tieneObjetoID(objetoID)) {
					return;
				}
				if (_perso.getCuenta().cantidadObjMercadillo(puesto.getMapaMercadillo()) >= puesto.getMaxObjCuenta()) {
					GestorSalida.ENVIAR_Im_INFORMACION(_out, "058");
					return;
				}
				try {
					GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
					Objeto obj = MundoDofus.getObjeto(objetoID);
					if (cantidad > obj.getCantidad() || obj == null) {
						return;
					}
					if (obj.getModelo().getTipo() == 113) {
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 27),
								Colores.ROJO);
						return;
					}
					int cantReal = (int) (Math.pow(10, cantidad) / 10);
					int nuevaCant = (obj.getCantidad() - cantReal);
					if (nuevaCant <= 0) {
						_perso.borrarObjetoRemove(objetoID);
						GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_out, objetoID);
					} else {
						obj.setCantidad(obj.getCantidad() - cantReal);
						GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso, obj);
						Objeto nuevoObj = Objeto.clonarObjeto(obj, cantReal);
						MundoDofus.addObjeto(nuevoObj, true);
						obj = nuevoObj;
					}
					ObjetoMercadillo objMerca = new ObjetoMercadillo(precio, cantidad, _perso.getCuenta().getID(), obj);
					puesto.addObjMercaAlPuesto(objMerca);
					GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out, '+', "", objMerca.analizarParaEmK());
					break;
				} catch (Exception e) {
					e.printStackTrace();
					Emu.creaLogs(e);
				}
			}
			return;
		} else if (_perso.enBanco()) {
			if (_perso.getIntercambio() != null) {
				return;
			}
			_perso.setOcupado(true);
			switch (packet.charAt(2)) {
			case 'G':// Kamas
				long kamas = 0;
				try {
					kamas = Integer.parseInt(packet.substring(3));
				} catch (Exception e) {// TODO: SEGUIR DESDE AQUI
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 28),
							Colores.ROJO);
					return;
				}
				if (kamas == 0) {
					return;
				}
				if (kamas > 0) {
					if (_perso.getKamas() < kamas) {
						kamas = _perso.getKamas();
					}
					if (kamas > 999999999) {
						kamas = 999999999;
					}
					if (_perso.getKamasBanco() + kamas > 999999999) {
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 28),
								Colores.ROJO);
						return;
					}
					_perso.setKamasBanco(_perso.getKamasBanco() + kamas);
					_perso.setKamas(_perso.getKamas() - kamas);
					GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
					GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(_perso, "G" + _perso.getKamasBanco());
				} else {
					kamas = -kamas;
					if (_perso.getKamasBanco() < kamas) {
						kamas = _perso.getKamasBanco();
					}
					_perso.setKamasBanco(_perso.getKamasBanco() - kamas);
					_perso.setKamas(_perso.getKamas() + kamas);
					GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
					GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(_perso, "G" + _perso.getKamasBanco());
				}
				break;
			case 'O':// Objeto
				int id = 0;
				int cant = 0;
				try {
					id = Integer.parseInt(packet.substring(4).split("\\|")[0]);
					cant = Integer.parseInt(packet.substring(4).split("\\|")[1]);
				} catch (Exception e) {
					Emu.creaLogs(e);
					return;
				}
				if (id == 0 || cant <= 0) {
					return;
				}
				if (MundoDofus.getObjeto(id) == null) {
					GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out,
							Idiomas.getTexto(_perso.getCuenta().idioma, 15));
					return;
				}
				int idModObj = MundoDofus.getObjeto(id).getModelo().getID();
				if (idModObj >= 7808 && idModObj <= 7876 && idModObj != 7864 && idModObj != 7865 && idModObj != 7819
						&& idModObj != 7811 && idModObj != 7817) {
					int color = CentroInfo.getColorDragoPavoPorPerga(idModObj);
					int idScroll = CentroInfo.getScrollporMontura(color);
					if (idScroll == -1) {
						return;
					}
					_perso.borrarObjetoRemove(id);
					MundoDofus.eliminarObjeto(id);
					GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_out, id);
					Objeto scroll = MundoDofus.getObjModelo(idScroll).crearObjDesdeModelo(2, false);
					if (!_perso.addObjetoSimilar(scroll, true, -1)) {
						MundoDofus.addObjeto(scroll, true);
						_perso.addObjetoPut(scroll);
						GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_out, scroll);
					}
					GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso);
					return;
				}
				switch (packet.charAt(3)) {
				case '+':
					_perso.addObjAlBanco(id, cant);
					break;
				case '-':
					_perso.removerDelBanco(id, cant);
					break;
				}
				break;
			}
			return;
		} else if (_perso.getMochilaMontura()) {
			Dragopavo drago = _perso.getMontura();
			if (drago == null) {
				return;
			}
			switch (packet.charAt(2)) {
			case 'O':// Objeto
				int id = 0;
				int cant = 0;
				try {
					id = Integer.parseInt(packet.substring(4).split("\\|")[0]);
					cant = Integer.parseInt(packet.substring(4).split("\\|")[1]);
				} catch (Exception e) {
					Emu.creaLogs(e);
				}
				if (id == 0 || cant <= 0) {
					return;
				}
				if (MundoDofus.getObjeto(id) == null) {
					GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out,
							Idiomas.getTexto(_perso.getCuenta().idioma, 15));
					return;
				}
				switch (packet.charAt(3)) {
				case '+':
					drago.addObjAMochila(id, cant, _perso);
					break;
				case '-':
					drago.removerDeLaMochila(id, cant, _perso);
					break;
				}
				break;
			}
			return;
		} else if (_perso.getCofre() != null) {
			if (_perso.getIntercambio() != null) {
				return;
			}
			Cofre cofre = _perso.getCofre();
			switch (packet.charAt(2)) {
			case 'G':
				long kamas = 0;
				try {
					kamas = Integer.parseInt(packet.substring(3));
				} catch (Exception e) {
					Emu.creaLogs(e);
					return;
				}
				if (kamas == 0) {
					return;
				}
				if (kamas > 0) {
					if (_perso.getKamas() < kamas) {
						kamas = _perso.getKamas();
					}
					if (kamas > 999999999) {
						kamas = 999999999;
					}
					if (cofre.getKamas() + kamas > 999999999) {
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 28),
								Colores.ROJO);
						return;
					}
					cofre.setKamas(cofre.getKamas() + kamas);
					_perso.setKamas(_perso.getKamas() - kamas);
					GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
				} else {
					kamas = -kamas;
					if (cofre.getKamas() < kamas) {
						kamas = cofre.getKamas();
					}
					cofre.setKamas(cofre.getKamas() - kamas);
					_perso.setKamas(_perso.getKamas() + kamas);
					GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
				}
				for (Personaje P : MundoDofus.getPJsEnLinea()) {
					if (P.getCofre() != null && _perso.getCofre().getID() == P.getCofre().getID()) {
						GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(P, "G" + cofre.getKamas());
					}
				}
				GestorSQL.ACTUALIZAR_COFRE(cofre);
				break;
			case 'O':
				int id = 0;
				int cant = 0;
				try {
					id = Integer.parseInt(packet.substring(4).split("\\|")[0]);
					cant = Integer.parseInt(packet.substring(4).split("\\|")[1]);
				} catch (Exception e) {
					Emu.creaLogs(e);
				}
				if (id == 0 || cant <= 0) {
					return;
				}
				switch (packet.charAt(3)) {
				case '+':
					cofre.addEnCofre(id, cant, _perso);
					break;
				case '-':
					cofre.retirarDelCofre(id, cant, _perso);
					break;
				}
				break;
			}
			return;
		} else if (_perso.getTrueque() != null) {
			switch (packet.charAt(2)) {
			case 'O':// Objeto
				if (packet.charAt(3) == '+') {
					String[] infos = packet.substring(4).split("\\|");
					try {
						int id = Integer.parseInt(infos[0]);
						int cant = Integer.parseInt(infos[1]);
						int cantInter = _perso.getTrueque().getCantObj(id, _perso.getID());
						if (!_perso.tieneObjetoID(id)) {
							return;
						}
						Objeto obj = MundoDofus.getObjeto(id);
						if (obj == null) {
							GestorSalida.ENVIAR_BN_NADA(_perso);
							return;
						}
						int nuevaCant = obj.getCantidad() - cantInter;
						if (cant > nuevaCant) {
							cant = nuevaCant;
						}
						if (_perso.revivirMascota) {
							if (cant > 1) {
								cant = 1;
							}
							if (obj.getModelo().getTipo() != 90 && obj.getModelo().getID() != 8012) {
								GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso,
										Idiomas.getTexto(_perso.getCuenta().idioma, 29), Colores.ROJO);
								return;
							}
							if ((obj.getModelo().getID() == 8012 && _perso.pusopolvo) || (obj.getModelo().getTipo() == 90 && _perso.pusomascota)) {
								GestorSalida.ENVIAR_BN_NADA(_perso);
								return;
							}
							if (obj.getModelo().getID() == 8012) {
								_perso.pusopolvo = true;
							}
							if (obj.getModelo().getTipo() == 90) {
								_perso.pusomascota = true;
							}
						}
						if (_perso.elemento) {
							boolean esvalido = false;
							switch (obj.getModelo().getTipo()) {
							case 2:
							case 3:
							case 4:
							case 5:
							case 6:
							case 7:
							case 8:
								esvalido = true;
								break;
							case 26:
								if (obj.getModelo().getID() >= 1345 && obj.getModelo().getID() <= 1348) {
									esvalido = true;
								}
								break;
							}
							if (!esvalido) {
								GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso,
										Idiomas.getTexto(_perso.getCuenta().idioma, 180), Colores.ROJO);
								return;
							}
							if (_perso.getKamas() < 1000000) {
								GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso,
										Idiomas.getTexto(_perso.getCuenta().idioma, 30), Colores.ROJO);
								return;
							}
						} else if (_perso.perfecciona) {
							boolean esvalido = false;
							switch (obj.getModelo().getTipo()) {
							case 1:
							case 9:
							case 10:
							case 11:
							case 16:
							case 17:
							case 18:
							case 2:
							case 3:
							case 4:
							case 82:
							case 5:
							case 6:
							case 7:
							case 8:
							case 19:
								esvalido = true;
								break;
							}
							if (!esvalido) {
								GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso,
										Idiomas.getTexto(_perso.getCuenta().idioma, 35), Colores.ROJO);
								return;
							}
							if (_perso.getKamas() < 200000) {
								GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso,
										Idiomas.getTexto(_perso.getCuenta().idioma, 30), Colores.ROJO);
								return;
							}
							if (_perso.pusoperfec) {
								GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso,
										Idiomas.getTexto(_perso.getCuenta().idioma, 31), Colores.ROJO);
								return;
							}
							_perso.pusoperfec = true;
						} else if (_perso.torrepvm) {
							if (!obj.getModelo().getCondiciones().contains("RS")) {
								GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso,
										Idiomas.getTexto(_perso.getCuenta().idioma, 36), Colores.ROJO);
								return;
							}
						}
						_perso.getTrueque().addObjetoTrueque(id, cant);
					} catch (NumberFormatException e) {
					}
				} else {
					String[] infos = packet.substring(4).split("\\|");
					try {
						int id = Integer.parseInt(infos[0]);
						int cant = Integer.parseInt(infos[1]);
						if (cant <= 0) {
							GestorSalida.ENVIAR_BN_NADA(_perso);
							return;
						}
						if (!_perso.tieneObjetoID(id)) {
							return;
						}
						Objeto obj = MundoDofus.getObjeto(id);
						if (obj == null) {
							GestorSalida.ENVIAR_BN_NADA(_perso);
							return;
						}
						int cantInter = _perso.getTrueque().getCantObj(id, _perso.getID());
						if (cant > cantInter) {
							cant = cantInter;
						}
						if (_perso.revivirMascota) {
							if (obj.getModelo().getID() == 8012) {
								_perso.pusopolvo = false;
							}
							if (obj.getModelo().getTipo() == 90) {
								_perso.pusomascota = false;
							}
						}
						if (_perso.perfecciona) {
							if (!_perso.pusoperfec) {
								return;
							}
							_perso.pusoperfec = false;
						}
						_perso.getTrueque().quitarObjeto(id, cant);
					} catch (NumberFormatException e) {
					}
				}
				break;
			}
			return;
		} else if (_perso.getIntercambiandoCon() == _perso.getID()) {
			switch (packet.charAt(3)) {
			case '-':
				int idObj = Integer.parseInt(packet.substring(4).split("\\|")[0]);
				int cant = Integer.parseInt(packet.substring(4).split("\\|")[1]);
				if (cant <= 0 || cant > 100) {
					GestorSalida.ENVIAR_BN_NADA(_perso);
					return;
				}
				_perso.objetoAInvetario(idObj);
				GestorSalida.ENVIAR_EiK_MOVER_OBJETO_TIENDA(_out, '-', "", idObj + "");
				GestorSQL.SALVAR_PERSONAJE(_perso, true);
				break;
			case '+':// Poner un objeto en venta
				int idObjeto = Integer.parseInt(packet.substring(4).split("\\|")[0]);
				int cantidad = Integer.parseInt(packet.substring(4).split("\\|")[1]);
				int precio = Integer.parseInt(packet.substring(4).split("\\|")[2]);
				if (!_perso.getTienda().contains(MundoDofus.getObjeto(idObjeto))) {
					if (cantidad <= 0 || precio <= 0 || !_perso.tieneObjetoID(idObjeto)) {
						return;
					}
					if (cantidad >= 101) {
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 32),
								Colores.ROJO);
						return;
					}
					if (_perso.contarTienda() >= _perso.maxTienda()) {
						GestorSalida.ENVIAR_Im_INFORMACION(_out, "176");
						return;
					}
					Objeto obj = MundoDofus.getObjeto(idObjeto);
					if (cantidad > obj.getCantidad()) {
						return;
					}
					int sobrante = obj.getCantidad() - cantidad;
					if (sobrante <= 0) {
						_perso.borrarObjetoRemove(idObjeto);
						GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_out, idObjeto);
					} else {
						obj.setCantidad(sobrante);
						GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso, obj);
						Objeto nuevoObj = Objeto.clonarObjeto(obj, cantidad);
						MundoDofus.addObjeto(nuevoObj, true);
						obj = nuevoObj;
					}
					Tienda nuevoObjeto = new Tienda(idObjeto, precio, cantidad);
					MundoDofus.agregarTienda(nuevoObjeto, true);
					String venta = obj.getID() + "|" + cantidad + "|" + obj.getModelo().getID() + "|"
							+ obj.convertirStatsAString(0) + "|" + precio;
					GestorSalida.ENVIAR_EiK_MOVER_OBJETO_TIENDA(_out, '+', "", venta + "");
					_perso.agregarObjTienda(obj);
					GestorSQL.SALVAR_PERSONAJE(_perso, true);
					break;
				} else {
					if (precio <= 0) {
						GestorSalida.ENVIAR_BN_NADA(_perso);
						return;
					}
					Objeto obj = MundoDofus.getObjeto(idObjeto);// Recupera el item
					String venta = idObjeto + "|" + cantidad + "|" + obj.getModelo().getID() + "|"
							+ obj.convertirStatsAString(0) + "|" + precio;
					GestorSalida.ENVIAR_EiK_MOVER_OBJETO_TIENDA(_out, '+', "", venta + "");
					_perso.actualizarObjTienda(idObjeto, precio);
					GestorSQL.SALVAR_PERSONAJE(_perso, true);
					break;
				}
			}
			return;
		} else if (_perso.getIntercambio() == null) {
			GestorSalida.ENVIAR_BN_NADA(_perso);
			return;
		}
		Intercambio inter = _perso.getIntercambio();
		switch (packet.charAt(2)) {
		case 'O': // INTERCAMBIO NORMAL
			if (packet.charAt(3) == '+') {
				String[] infos = packet.substring(4).split("\\|");
				int id = -1;
				int cant = -1;
				try {
					id = Integer.parseInt(infos[0]);
					cant = Integer.parseInt(infos[1]);
				} catch (NumberFormatException e) {
				}
				if (id == -1 || cant == -1) {
					return;
				}
				try {
					int cantInter = inter.getCantObjeto(id, _perso.getID());
					if (!_perso.tieneObjetoID(id)) {
						return;
					}
					Objeto obj = MundoDofus.getObjeto(id);
					if (obj == null) {
						return;
					}
					/*
					 * if (obj.getModelo().getID() == 980125) {
					 * GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso,
					 * Idiomas.getTexto(_perso.getCuenta().idioma, 179), Colores.AZUL); return; }
					 */// monedas astro
					if (obj.getModelo().getID() == 11158) {
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 33),
								Colores.AZUL);
						return;
					} // TODO:AQUI
					if (obj.getModelo().getID() == 10275) {
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 34),
								Colores.AZUL);
						return;
					}
					int nuevaCant = obj.getCantidad() - cantInter;
					if (cant > nuevaCant) {
						cant = nuevaCant;
					}
					inter.addObjeto(obj, cant, _perso.getID());
				} catch (NullPointerException e) {
				}
			} else {
				String[] infos = packet.substring(4).split("\\|");
				try {
					int id = Integer.parseInt(infos[0]);
					int cant = Integer.parseInt(infos[1]);
					if ((cant <= 0) || !_perso.tieneObjetoID(id)) {
						return;
					}
					Objeto obj = MundoDofus.getObjeto(id);
					if (obj == null) {
						return;
					}
					int cantInter = inter.getCantObjeto(id, _perso.getID());
					if (cant > cantInter) {
						cant = cantInter;
					}
					inter.borrarObjeto(obj, cant, _perso.getID());
				} catch (Exception e) {
					Emu.creaLogs(e);
				}
			}
			break;
		case 'G':// Kamas
			try {
				if (packet.contains("NaN") || packet.contains("undefined") || packet.equals("")) {
					return;
				}
				long numero = Integer.parseInt(packet.substring(3));
				if (numero > 1000000000) {
					numero = 1000000000;
				}
				if (_perso.getKamas() < numero) {
					numero = _perso.getKamas();
				}
				inter.setKamas(_perso.getID(), numero);
			} catch (Exception e) {
				Emu.creaLogs(e);
			}
			break;
		}
	}

	private static void startRomperObj(Personaje _perso, PrintWriter _out, int veces) {
		_perso.bucle = true;
		TimerTask temp = new TimerTask() {
			int vxs = veces;

			@Override
			public void run() {
				if (!_perso.enLinea() || !_perso.getRompiendo() || _perso.rompemineral || !_perso.bucle) {
					_perso.rompemineral = false;
					_perso.bucle = false;
					GestorSalida.ENVIAR_Ea_TERMINO_RECETAS(_perso, "4");
					if (veces > 0) {
						romperObjeto(_perso, _out, 0);
					}
					this.cancel();
					return;
				}
				vxs -= 1;
				GestorSalida.ENVIAR_EA_TURNO_RECETA(_perso, vxs + "");
				romperObjeto(_perso, _out, vxs);
				if (vxs <= 0) {
					this.cancel();
					return;
				}
			}
		};
		Emu._globalTime.schedule(temp, 650, 650);
	}

	private static void intercambio_Pago_Por_Trabajo(String packet, Personaje _perso) {
		if (_perso.getIntercambiandoCon() == 0) {
			GestorSalida.ENVIAR_BN_NADA(_perso);
			return;
		}
		int tipoPago = Integer.parseInt(packet.substring(2, 3));
		char caracter = packet.charAt(3);
		char signo = packet.charAt(4);
		InvitarTaller taller = _perso.getTallerInvitado();
		if (caracter == 'G') {
			long kamas = Long.parseLong(packet.substring(4));
			_perso.getTallerInvitado().setKamas(tipoPago, kamas, _perso.getKamas());
		} else {
			if (signo == '+') {
				String[] infos = packet.substring(5).split("\\|");
				int id = -1;
				int cant = -1;
				try {
					id = Integer.parseInt(infos[0]);
					cant = Integer.parseInt(infos[1]);
				} catch (NumberFormatException e) {
					Emu.creaLogs(e);
				}
				if (id == -1 || cant == -1) {
					return;
				}
				try {
					int cantInter = taller.getCantObjetoPago(id, tipoPago);
					if (!_perso.tieneObjetoID(id)) {
						return;
					}
					Objeto obj = MundoDofus.getObjeto(id);
					if (obj == null) {
						return;
					}
					int nuevaCant = obj.getCantidad() - cantInter;
					if (cant > nuevaCant) {
						cant = nuevaCant;
					}
					taller.addObjetoPaga(obj, cant, tipoPago);
				} catch (NullPointerException e) {
				}

			} else {
				String[] infos = packet.substring(5).split("\\|");
				try {
					int id = Integer.parseInt(infos[0]);
					int cant = Integer.parseInt(infos[1]);
					if ((cant <= 0) || !_perso.tieneObjetoID(id)) {
						return;
					}
					Objeto obj = MundoDofus.getObjeto(id);
					if (obj == null) {
						return;
					}
					int cantInter = taller.getCantObjetoPago(id, tipoPago);
					if (cant > cantInter) {
						cant = cantInter;
					}
					taller.borrarObjetoPaga(obj, cant, tipoPago);
				} catch (NumberFormatException e) {
					Emu.creaLogs(e);
				}
			}
		}
	}

	private static void intercambio_Preg_Mercante(Personaje _perso) {
		if (_perso.getTienda().size() <= 0) {
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 40));
			return;
		}
		if (_perso.getIntercambiandoCon() != 0) {
			return;
		}
		int objTienda = _perso.contarTienda();
		int tasa = _perso.getNivel() / 2;
		long impuesto = _perso.precioTotal() * tasa / 1000;
		if (impuesto < 0) {
			impuesto = 1;
		}
		if (tasa < 0) {
			tasa = 1;
		}
		GestorSalida.ENVIAR_Eq_PREGUNTAR_MERCANTE(_perso, objTienda, tasa, impuesto);
	}

	private static synchronized void intercambio_Ok_Mercante(Personaje _perso, PrintWriter _out, Cuenta _cuenta) {
		Mapa mapa = _perso.getMapa();
		if (mapa == null) {
			return;
		}
		int tasa = _perso.getNivel() / 2;
		long pagar = _perso.precioTotal() * tasa / 1000;
		long kamas = _perso.getKamas();
		if (_perso.getTienda().size() <= 0) {
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 40));
			return;
		}
		if (kamas >= pagar) {
			if (mapa.cantMercantes() < mapa.getCapacidad() - 1) {
				_perso.setKamas(kamas - pagar);
				_perso.setMercante(1);
				mapa.agregarMercante(_perso.getID());
				GestorSQL.SALVAR_MERCANTES(mapa);
				_cuenta.getEntradaPersonaje().salir(false);// botar al personaje
				for (Personaje z : mapa.getPersos()) {
					if (z != null && z.enLinea()) {
						GestorSalida.ENVIAR_GM_MERCANTE_A_MAPA(z);
					}
				}
				return;
			}
			GestorSalida.ENVIAR_Im_INFORMACION(_out, "125;" + mapa.getCapacidad());
		} else {
			GestorSalida.ENVIAR_Im_INFORMACION(_out, "176");
		}
	}

	private static synchronized void intercambio_Mercadillo(String packet, Personaje _perso, PrintWriter _out) {
		if (_perso.getIntercambiandoCon() > 0) {
			return;
		}
		int templateID;
		int intercamcon = _perso.getIntercambiandoCon();
		if (_perso.abrepanelinter) {
			intercamcon = -7411;
		}
		switch (packet.charAt(2)) {
		case 'B': // Confirmacion de compra
			String[] info = packet.substring(3).split("\\|");
			Mercadillo curHdv = MundoDofus.getPuestoMerca(Math.abs(intercamcon));
			int ligneID = Integer.parseInt(info[0]);
			int amount = Integer.parseInt(info[1]);
			if (curHdv.comprarObjeto(ligneID, amount, Integer.parseInt(info[2]), _perso, curHdv)) {
				GestorSalida.ENVIAR_EHm_DETALLE_LINEA_CON_PRECIOS(_perso, "-", ligneID + "");// quita la linea
				_perso.refrescarVida(false);
				GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso);
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 41),
						Colores.VERDE);
			} else {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 42),
						Colores.ROJO);
			}
			break;
		case 'l':// solicita la lista de un template (los precios)
			templateID = Integer.parseInt(packet.substring(3));
			try {
				GestorSalida.GAME_SEND_EHl(_perso, MundoDofus.getPuestoMerca(Math.abs(intercamcon)), templateID);
			} catch (NullPointerException e) {
				GestorSalida.GAME_SEND_EHM_PACKET(_perso, "-", templateID + "");
			}
			break;
		case 'P':// demanda el precio promedio
			templateID = Integer.parseInt(packet.substring(3));
			GestorSalida.GAME_SEND_EHP_PACKET(_perso, templateID);
			break;
		case 'S':// //buscador
			final String[] splt = packet.substring(3).split(Pattern.quote("|"));
			Mercadillo mercadillo = MundoDofus.getPuestoMerca(Math.abs(intercamcon));
			if (mercadillo.esTipoDeEsteMercadillo(Integer.parseInt(splt[0]))) {
				if (mercadillo.hayModeloEnEsteMercadillo(Integer.parseInt(splt[0]), Integer.parseInt(splt[1]))) {
					GestorSalida.ENVIAR_EHS_BUSCAR_OBJETO_MERCADILLO(_perso, "K");
					GestorSalida.ENVIAR_EHl_LISTA_LINEAS_OBJMERCA_POR_MODELO(_perso,
							mercadillo.strListaLineasPorModelo(Integer.parseInt(splt[1])));
				} else {
					GestorSalida.ENVIAR_EHS_BUSCAR_OBJETO_MERCADILLO(_perso, "E");
				}
			} else {
				GestorSalida.ENVIAR_EHS_BUSCAR_OBJETO_MERCADILLO(_perso, "E");
			}
			break;
		case 'T':// demanda los template de la categoria
			int categ = Integer.parseInt(packet.substring(3));
			String allTemplate = MundoDofus.getPuestoMerca(Math.abs(intercamcon)).stringModelo(categ);
			GestorSalida.GAME_SEND_EHL_PACKET(_perso, categ, allTemplate);
			break;
		}
	}

	private static synchronized void intercambio_Cercado(String packet, Personaje _perso, PrintWriter _out,
			Cuenta _cuenta) {
		if (_perso.getEnCercado() != null) {
			char c = packet.charAt(2);
			packet = packet.substring(3);
			int id = -1;
			try {
				id = Integer.parseInt(packet);
			} catch (Exception e) {
			}
			switch (c) {
			case 'g':// guardar en el establo
				if ((System.currentTimeMillis() - _perso.get_tiempoUltEstablo()) < 3000 || _perso.getPelea() != null) {
					GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
							Idiomas.getTexto(_perso.getCuenta().idioma, 43));
					return;
				}
				Dragopavo DP3 = MundoDofus.getDragopavoPorID(id);
				if (!_cuenta.getEstablo().contains(DP3)) {
					_cuenta.getEstablo().add(DP3);
				} else {
					return;
				}
				_perso.getMapa().getCercado().delCriando(id);
				GestorSalida.ENVIAR_Ef_MONTURA_A_CRIAR(_perso, '-', DP3.getID() + "");
				/*
				 * if (DP3.getFecundadaHace() >= DP3.minutosParir() && DP3.getFecundadaHace() <=
				 * 1440) { int crias = Formulas.getRandomValor(1, 2); if
				 * (DP3.getCapacidades().contains(3)) crias = crias * 2; if (DP3.getReprod() +
				 * crias > 20) crias = 20 - DP3.getReprod();
				 * GestorSalida.ENVIAR_Im_INFORMACION(_out, "1111;" + crias); Dragopavo
				 * DragoPadre = MundoDofus.getDragopavoPorID(DP3.getPareja()); for (int i = 0; i
				 * < crias; i++) { int color; if (DragoPadre != null) color =
				 * CentroInfo.colorCria(DP3.getColor(), DragoPadre.getColor()); else color =
				 * CentroInfo.colorCria(DP3.getColor(), DP3.getColor()); Dragopavo Drago = new
				 * Dragopavo(color, DP3, DragoPadre); GestorSQL.CREAR_MONTURA(Drago);
				 * GestorSalida.ENVIAR_Ee_MONTURA_A_ESTABLO(_perso, '~',
				 * Drago.detallesMontura()); DP3.aumReproduccion();
				 * _cuenta.getEstablo().add(Drago); } DP3.resAmor(7500);
				 * DP3.resResistencia(7500); DP3.setFecundadaHace(-1); } else if
				 * (DP3.getFecundadaHace() > 1440) { GestorSalida.ENVIAR_Im_INFORMACION(_out,
				 * "1112"); DP3.aumReproduccion(); DP3.resAmor(7500); DP3.resResistencia(7500);
				 * DP3.setFecundadaHace(-1); }
				 */
				GestorSalida.ENVIAR_Ee_MONTURA_A_ESTABLO(_perso, '+', DP3.detallesMontura());
				GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(_perso.getMapa(), id);
				DP3.setMapaCelda((short) -1, -1);
				_perso.set_tiempoUltEstablo(System.currentTimeMillis());
				GestorSQL.SALVAR_PERSONAJE(_perso, true);
				break;
			case 'p':// Poner a Criar
				if ((System.currentTimeMillis() - _perso.get_tiempoUltEstablo()) < 3000 || _perso.getPelea() != null) {
					GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
							Idiomas.getTexto(_perso.getCuenta().idioma, 43));
					return;
				}
				Mapa mapa = _perso.getMapa();
				if (mapa == null) {
					return;
				}
				if (mapa.getCercado().getListaCriando().size() >= mapa.getCercado().getTamaÑo()) {
					GestorSalida.ENVIAR_Im_INFORMACION(_out, "1107");
					return;
				}
				Dragopavo DP2 = MundoDofus.getDragopavoPorID(id);
				if (DP2 == null) {
					return;
				}
				if (DP2.getObjetos().size() > 0) {
					GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
							Idiomas.getTexto(_perso.getCuenta().idioma, 44));
					return;
				}
				if (_perso.estaMontando()) {
					return;
				}
				if (_cuenta.getEstablo().contains(DP2)) {
					_cuenta.getEstablo().remove(DP2);
				}
				_perso.setMontura(null);
				DP2.setDueÑo(_perso.getID());
				DP2.setMapaCelda(mapa.getID(), mapa.getCercado().getColocarCelda());// TODO:
				mapa.getCercado().addCriando(id);
				GestorSalida.ENVIAR_Ef_MONTURA_A_CRIAR(_perso, '+', DP2.detallesMontura());
				GestorSalida.ENVIAR_Ee_MONTURA_A_ESTABLO(_perso, '-', DP2.getID() + "");
				GestorSalida.ENVIAR_GM_DRAGOPAVO_A_MAPA(mapa, DP2);
				GestorSalida.ENVIAR_Re_DETALLES_MONTURA(_perso, "-", null);
				_perso.set_tiempoUltEstablo(System.currentTimeMillis());
				GestorSQL.SALVAR_PERSONAJE(_perso, true);
				break;
			}
		}
	}

	private static synchronized void intercambio_Establo(String packet, Personaje _perso, PrintWriter _out,
			Cuenta _cuenta) { // Si dentro de un cercado
		if (_perso.getEnCercado() != null) {
			char c = packet.charAt(2);
			packet = packet.substring(3);
			int id = -1;
			try {
				id = Integer.parseInt(packet);
			} catch (Exception e) {
			}
			switch (c) {
			case 'C':// pergamino => establo (Stocker)
				if (id == -1 || !_perso.tieneObjetoID(id)) {
					return;
				}
				Objeto obj = MundoDofus.getObjeto(id);
				int DPid = obj.getStats().getEfecto(995);
				Dragopavo DP = MundoDofus.getDragopavoPorID(-DPid);
				if (DP == null) {
					int color = CentroInfo.getColorDragoPavoPorPerga(obj.getModelo().getID());
					if (color < 1) {
						return;
					}
					DP = new Dragopavo(color, _perso.getID());
				} else {
					if (DP.getObjetos().size() > 0) {
						GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
								Idiomas.getTexto(_perso.getCuenta().idioma, 44));
						return;
					}
				}
				if (!_cuenta.getEstablo().contains(DP)) {
					_cuenta.getEstablo().add(DP);
				} else {
					return;
				}
				_perso.borrarObjetoEliminar(id, 1, true);
				GestorSalida.ENVIAR_Ee_MONTURA_A_ESTABLO(_perso, '+', DP.detallesMontura());
				/*
				 * if (DP.getFecundadaHace() >= DP.minutosParir() && DP.getFecundadaHace() <=
				 * 1440) { int crias = Formulas.getRandomValor(1, 2); if
				 * (DP.getCapacidades().contains(3)) crias = crias * 2; if (DP.getReprod() +
				 * crias > 20) crias = 20 - DP.getReprod();
				 * GestorSalida.ENVIAR_Im_INFORMACION(_out, "1111;" + crias); Dragopavo
				 * DragoPadre = MundoDofus.getDragopavoPorID(DP.getPareja()); for (int i = 0; i
				 * < crias; i++) { int color; if (DragoPadre != null) color =
				 * CentroInfo.colorCria(DP.getColor(), DragoPadre.getColor()); else color =
				 * CentroInfo.colorCria(DP.getColor(), DP.getColor()); Dragopavo Drago = new
				 * Dragopavo(color, DP, DragoPadre); GestorSQL.CREAR_MONTURA(Drago);
				 * GestorSalida.ENVIAR_Ee_MONTURA_A_ESTABLO(_perso, '~',
				 * Drago.detallesMontura()); DP.aumReproduccion();
				 * _cuenta.getEstablo().add(Drago); } DP.resAmor(7500); DP.resResistencia(7500);
				 * DP.setFecundadaHace(-1); } else if (DP.getFecundadaHace() > 1440) {
				 * GestorSalida.ENVIAR_Im_INFORMACION(_out, "1112"); DP.aumReproduccion();
				 * DP.resAmor(7500); DP.resResistencia(7500); DP.setFecundadaHace(-1); }
				 */
				break;
			case 'c':// establo => pergamino(intercambio)
				Dragopavo DP1 = MundoDofus.getDragopavoPorID(id);
				if (DP1 != null) {
					if (!_cuenta.getEstablo().contains(DP1)) {
						return;
					}
					if (DP1.getObjetos().size() > 0) {
						GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
								Idiomas.getTexto(_perso.getCuenta().idioma, 44));
						return;
					}
					_cuenta.getEstablo().remove(DP1);
					ObjetoModelo OM = CentroInfo.getPergaPorColorDragopavo(DP1.getColor());
					Objeto obj1 = OM.crearObjDesdeModelo(1, false);
					MundoDofus.addObjeto(obj1, true);
					obj1.clearTodo();
					obj1.getStats().addUnStat(995, -(DP1.getID()));
					obj1.addTextoStat(996, _perso.getNombre());
					obj1.addTextoStat(997, DP1.getNombre());
					_perso.addObjetoPut(obj1);
					GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_out, obj1);
					GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso);
					GestorSalida.ENVIAR_Ee_MONTURA_A_ESTABLO(_perso, '-', DP1.getID() + "");
				}
				break;
			case 'g':// Equipar dragopavo
				Dragopavo DP3 = MundoDofus.getDragopavoPorID(id);
				if (!_cuenta.getEstablo().contains(DP3) || DP3 == null) {
					GestorSalida.ENVIAR_Im_INFORMACION(_out, "1104");
					return;
				}
				if (DP3.getObjetos().size() > 0) {
					GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
							Idiomas.getTexto(_perso.getCuenta().idioma, 44));
					return;
				}
				if (_perso.getMontura() != null) {
					GestorSalida.ENVIAR_BN_NADA(_out);
					return;
				}
				if (_cuenta.getEstablo().contains(DP3)) {
					_cuenta.getEstablo().remove(DP3);
				}
				_perso.setMontura(DP3);
				GestorSalida.ENVIAR_Re_DETALLES_MONTURA(_perso, "+", DP3);
				GestorSalida.ENVIAR_Ee_MONTURA_A_ESTABLO(_perso, '-', DP3.getID() + "");
				GestorSalida.ENVIAR_Rx_EXP_DONADA_MONTURA(_perso);
				break;
			case 'p':// Equipar => Establo
				if (_perso.getMontura() != null) { // TODO:
					Dragopavo DP2 = _perso.getMontura();
					if (DP2.getObjetos().size() == 0) {
						if (_perso.estaMontando()) {
							GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 45),
									Colores.ROJO);
							return;
						}
						if (!_cuenta.getEstablo().contains(DP2)) {
							_cuenta.getEstablo().add(DP2);
						} else {
							return;
						}
						_perso.setMontura(null);
						/*
						 * if (DP2.getFecundadaHace() >= DP2.minutosParir() && DP2.getFecundadaHace() <=
						 * 1440) { int crias = Formulas.getRandomValor(1, 2); if
						 * (DP2.getCapacidades().contains(3)) crias = crias * 2; if (DP2.getReprod() +
						 * crias > 20) crias = 20 - DP2.getReprod();
						 * GestorSalida.ENVIAR_Im_INFORMACION(_out, "1111;" + crias); Dragopavo
						 * DragoPadre = MundoDofus.getDragopavoPorID(DP2.getPareja()); for (int i = 0; i
						 * < crias; i++) { int color; if (DragoPadre != null) color =
						 * CentroInfo.colorCria(DP2.getColor(), DragoPadre.getColor()); else color =
						 * CentroInfo.colorCria(DP2.getColor(), DP2.getColor()); Dragopavo Drago = new
						 * Dragopavo(color, DP2, DragoPadre); GestorSQL.CREAR_MONTURA(Drago);
						 * GestorSalida.ENVIAR_Ee_MONTURA_A_ESTABLO(_perso, '~',
						 * Drago.detallesMontura()); DP2.aumReproduccion();
						 * _cuenta.getEstablo().add(Drago); } DP2.resAmor(7500);
						 * DP2.resResistencia(7500); DP2.setFecundadaHace(-1); } else if
						 * (DP2.getFecundadaHace() > 1440) { GestorSalida.ENVIAR_Im_INFORMACION(_out,
						 * "1112"); DP2.aumReproduccion(); DP2.resAmor(7500); DP2.resResistencia(7500);
						 * DP2.setFecundadaHace(-1); }
						 */
						GestorSalida.ENVIAR_Ee_MONTURA_A_ESTABLO(_perso, '+', DP2.detallesMontura());
						GestorSalida.ENVIAR_Re_DETALLES_MONTURA(_perso, "-", null);
						GestorSalida.ENVIAR_Rx_EXP_DONADA_MONTURA(_perso);
					} else {
						GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
								Idiomas.getTexto(_perso.getCuenta().idioma, 44));
					}
				}
				break;
			}
		}
	}

	private static void intercambio_Repetir(Personaje _perso) {
		if (_perso.getHaciendoTrabajo() != null) {
			_perso.getHaciendoTrabajo().ponerIngredUltRecet();
		}
		if (_perso.getRompiendo()) {
			_perso.rompemineral = true;
			_perso.bucle = false;
		}
	}

	private static String getIdMineral(Objeto runa, Objeto real, Personaje _perso, int veces) { // TODO:Nueva fm
		int statid = -1;
		String total = "";
		int cantidad = 0;
		int maxcant = 0;
		String nombre = "";
		switch (runa.getModelo().getID()) {
		case 312:
			statid = 1001;
			maxcant = 100;
			nombre = "Hierro";
			break;
		case 313:
			statid = 1000;
			maxcant = 2;
			nombre = "Oro";
			break;
		case 350:
			statid = 1002;
			maxcant = 6;
			nombre = "Plata";
			break;
		case 441:
			statid = 1003;
			maxcant = 15;
			nombre = "Cobre";
			break;
		case 442:
			statid = 1004;
			maxcant = 20;
			nombre = "Bronce";
			break;
		case 443:
			statid = 1005;
			maxcant = 40;
			nombre = "Kobalto";
			break;
		case 444:
			statid = 1006;
			maxcant = 6;
			nombre = "EstaÑo";
			break;
		case 445:
			statid = 1007;
			maxcant = 15;
			nombre = "Manganeso";
			break;
		case 446:
			statid = 1008;
			maxcant = 5;
			nombre = "Bauxita";
			break;
		case 447:// sin usar
			statid = 1009;
			maxcant = 50;
			nombre = "Carb�n";
			break;
		case 7032:
			statid = 1010;
			maxcant = 20;
			nombre = "Silicato";
			break;
		case 7033:
			statid = 1011;
			maxcant = 10;
			nombre = "Dolom�a";
			break;
		default:
			return "null";
		}
		if (statid == -1) {
			return "null";
		}
		int cantidadstats = 0;
		for (Entry<Integer, String> rel : real.getTextoStat().entrySet()) {
			if (rel.getKey() >= 1000 && rel.getKey() <= 1013) {
				if (statid != rel.getKey()) {
					cantidadstats += 1;
				}
			}
		}
		if (cantidadstats >= 3) {
			return "max";
		}
		Objeto nuevoObj = null;
		if (veces == 0) {
			nuevoObj = Objeto.clonarObjeto(real, 1); // COGE EL OBJETO QUE YO PONGO, EL KIM
			_perso.borrarObjetoEliminar(real.getID(), 1, true); // borra de mundo y de pj
			MundoDofus.addObjeto(nuevoObj, false);
			_perso.addObjetoPut(nuevoObj);
			if (!_perso.ultrec.isEmpty()) {
				_perso.ultrec.replace(1, real.getID(), nuevoObj.getID());
			}
		} else {
			nuevoObj = real;
			if (!_perso.ultrec.isEmpty()) {
				GestorSalida.GAME_SEND_EM_PACKET(_perso, "KO+" + _perso.ultrec.get(0) + "|1");
			}
			GestorSalida.GAME_SEND_EM_PACKET(_perso, "KO+" + nuevoObj.getID() + "|1");
		}
		if (nuevoObj.getTextoStat().get(statid) != null) { // Oro : 15/15
			int cantidadreal = 1;
			String tex = nuevoObj.getTextoStat().get(statid).split(":")[1].split("/")[0].replace(" ", "");
			String agregs = "";
			try {
				agregs = nuevoObj.getTextoStat().get(statid).split("\\(")[1].split("\\)")[0].replace(" ", "");
			} catch (Exception e) {
				e.printStackTrace();
				agregs = "0";
			}
			cantidadreal = Integer.parseInt(tex) + 1;
			if (cantidadreal >= maxcant) {
				_perso.rompemineral = true;
				_perso.bucle = false;
				cantidadreal = maxcant;
			}
			nuevoObj.getTextoStat().remove(Integer.valueOf(statid));
			nombre = nombre + " : " + cantidadreal + "/" + maxcant + " (" + agregs + ")";
		} else {
			cantidad = 1;
			nombre = nombre + " : " + cantidad + "/" + maxcant + " (0)";
		}
		nuevoObj.addTextoStat(statid, nombre);
		if (veces == 0) {
			GestorSalida.GAME_SEND_Em_PACKET(_perso, "KO+" + nuevoObj.stringObjetoConPalo(1));
			GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_perso, nuevoObj);
			GestorSalida.ENVIAR_Ea_TERMINO_RECETAS(_perso, "1");
			_perso.paramag.clear();
			_perso.rompemineral = false;
			_perso.bucle = false;
		}
		if (_perso.continua) {
			if (!_perso.avisado) {
				GestorSalida.enviar(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 46));
			}
			_perso.avisado = true;
		}
		total = statid + "-" + cantidad + "-" + maxcant + "-" + nombre;
		return total;
	}

	private static void romperObjeto(Personaje _perso, PrintWriter _out, int veces) {
		Map<Integer, Integer> id = _perso.paramag;
		if (id.isEmpty()) {
			if (!_perso.ultrec.isEmpty()) {
				_perso.paramag.putAll(_perso.ultrec);
				id.putAll(_perso.paramag);
			}
		} else if (!id.isEmpty()) {
			_perso.ultrec.putAll(id);
		}
		if (id.get(0) == null || id.get(1) == null) {
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 47), Colores.ROJO);
			return;
		}
		Objeto Obj = MundoDofus.getObjeto(id.get(0));
		if (Obj == null) {
			_perso.rompemineral = true;
			_perso.bucle = false;
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 48), Colores.ROJO);
			return;
		}
		// 0 es la runa , 1 el item
		Objeto Obj2 = MundoDofus.getObjeto(id.get(1));
		if (Obj2 == null) {
			_perso.rompemineral = true;
			_perso.bucle = false;
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 49), Colores.ROJO);
			return;
		}
		_perso.borrarObjetoEliminar(Obj.getID(), 1, true);
		String total = getIdMineral(Obj, Obj2, _perso, veces);
		if (total.equals("null")) {
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 50), Colores.ROJO);
			return;
		} else if (total.equals("max")) {
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 51), Colores.ROJO);
			return;
		}
	}

	private static void intercambio_Ok(Personaje _perso, PrintWriter _out, String packet) {
		if (_perso.getTallerInvitado() != null) {
			_perso.getTallerInvitado().botonOK(_perso.getID());
		} else if (_perso.getHaciendoTrabajo() != null) {
			if (!_perso.getHaciendoTrabajo().esReceta()) {
				return;
			}
			_perso.getHaciendoTrabajo().unaMagueada();
		} else if (_perso.getRompiendo()) {
			if (_perso.bucle) {
				return;
			}
			int vec = Integer.parseInt(packet.substring(2));
			if (vec == 1) {
				vec = 0;
			}
			romperObjeto(_perso, _out, vec);
		} else if (_perso.getTrueque() != null) {
			if (_perso.revivirMascota) {
				if (!_perso.pusomascota || !_perso.pusopolvo) {
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 52),
							Colores.ROJO);
					return;
				}
				if (_perso.pusomascota && _perso.pusopolvo) {
					_perso.revivirMascota = false;
					_perso.pusomascota = false;
					_perso.pusopolvo = false;
				}
			}
			if (_perso.perfecciona) {
				if (!_perso.pusoperfec) {
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 53),
							Colores.ROJO);
					return;
				} else {
					_perso.pusoperfec = false;
					_perso.perfecciona = false;
				}
			}
			if (_perso.elemento) {
				_perso.elemento = false;
			}
			if (_perso.torrepvm) {
				_perso.torrepvm = false;
			}
			_perso.getTrueque().botonOK(_perso.getID());
		} else if (_perso.getIntercambio() != null) {
			_perso.getIntercambio().botonOK(_perso.getID());
		}
	}

	private static void intercambio_Aceptar(Personaje _perso, PrintWriter _out) {
		if (_perso.getIntercambiandoCon() != 0) {
			if (_perso.getHaciendoTrabajo() != null) {
				AccionTrabajo trabajo = _perso.getHaciendoTrabajo();
				Personaje artesano = MundoDofus.getPersonaje(_perso.getIntercambiandoCon());
				try {
					GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(artesano.getCuenta().getEntradaPersonaje().getOut(),
							12, trabajo.getCasillasMax() + ";" + trabajo.getIDTrabajo());// artesano
					GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(_out, 13,
							trabajo.getCasillasMax() + ";" + trabajo.getIDTrabajo());// invitado
				} catch (NullPointerException e) {
					GestorSalida.ENVIAR_BN_NADA(_out);
					_perso.setIntercambiandoCon(0);
					artesano.setIntercambiandoCon(0);
					return;
				}
				InvitarTaller taller = new InvitarTaller(artesano, _perso, trabajo.getCasillasMax());
				try {
					artesano.setTallerInvitado(taller);
					_perso.setTallerInvitado(taller);
				} catch (NullPointerException e) {
					GestorSalida.ENVIAR_BN_NADA(_out);
					return;
				}
			} else {
				Personaje pjInter = MundoDofus.getPersonaje(_perso.getIntercambiandoCon());
				try {
					GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(pjInter.getCuenta().getEntradaPersonaje().getOut(), 1,
							"");
					GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(_out, 1, "");
				} catch (NullPointerException e) {
					GestorSalida.ENVIAR_BN_NADA(_out);
					_perso.setIntercambiandoCon(0);
					pjInter.setIntercambiandoCon(0);
					return;
				}
				Intercambio intercambio = new Intercambio(pjInter, _perso);
				try {
					pjInter.setIntercambio(intercambio);
					_perso.setIntercambio(intercambio);
				} catch (NullPointerException e) {
					GestorSalida.ENVIAR_BN_NADA(_out);
					return;
				}
			}
		}
		// desbugItemEncima(_perso, _out);
	}

	private static void intercambio_Vender(String packet, Personaje _perso, PrintWriter _out) {
		try {
			String[] infos = packet.substring(2).split("\\|");
			int id = Integer.parseInt(infos[0]);
			int cant = Integer.parseInt(infos[1]);
			if (!_perso.tieneObjetoID(id)) {
				GestorSalida.ENVIAR_ESE_ERROR_VENTA(_out);
				return;
			}
			Objeto objc = _perso.getObjNoEquip(id, cant);
			if (objc == null) {
				return;
			}
			_perso.venderObjeto(id, cant);
		} catch (Exception e) {
			GestorSalida.ENVIAR_ESE_ERROR_VENTA(_out);
		}
	}

	private static void intercambio_Comprar(String packet, Personaje _perso, PrintWriter _out) {
		String[] infos = packet.substring(2).split("\\|");
		if (_perso.getIntercambiandoCon() < 0) {
			try {
				int idObjModelo = 0;
				int cantidad = 0;
				try {
					idObjModelo = Integer.parseInt(infos[0]);
					cantidad = Integer.parseInt(infos[1]);
				} catch (NumberFormatException e) {
				}
				if (cantidad <= 0 || idObjModelo <= 0) {
					return;
				}
				ObjetoModelo objModelo = MundoDofus.getObjModelo(idObjModelo);
				if (objModelo == null) {
					GestorSalida.ENVIAR_EBE_ERROR_DE_COMPRA(_out);
					return;
				}
				NPC npc = _perso.getMapa().getNPC(_perso.getIntercambiandoCon());
				if (npc == null) {
					GestorSalida.ENVIAR_EBE_ERROR_DE_COMPRA(_out);
					return;
				}
				NPCModelo npcMod = npc.getModeloBD();
				if (npcMod == null || !npcMod.tieneObjeto(idObjModelo)) {
					GestorSalida.ENVIAR_EBE_ERROR_DE_COMPRA(_out);
					return;
				}
				int precioUnitario = objModelo.getPrecio();
				int precioVIP = objModelo.getPrecioVIP();
				int precio = precioUnitario * cantidad;
				boolean esvip = false;
				if (npc.getModeloBD().getID() == 596) {
					String ip = _perso.getCuenta().getActualIP();
					if (MundoDofus.comprasMercader.containsKey(ip)) {
						int veces = MundoDofus.comprasMercader.get(ip);
						if (veces >= 2) {
							GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out,
									Idiomas.getTexto(_perso.getCuenta().idioma, 207));
							return;
						}
					}
				}
				if (cantidad < 0 || cantidad > 1000) {
					GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out,
							Idiomas.getTexto(_perso.getCuenta().idioma, 58));
					GestorSalida.ENVIAR_EBE_ERROR_DE_COMPRA(_out);
					return;
				}
				switch (npcMod.getID()) {
				case 1332:
				case 7500:
				case 535:
				case 856:
				case 442:
					esvip = true;
					precio = precioVIP * cantidad;
					int mispuntos = GestorSQL.getPuntosCuenta(_perso.getCuentaID());
					if (mispuntos < precio) {
						GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out,
								Idiomas.getTexto(_perso.getCuenta().idioma, 55) + (precio - mispuntos)
										+ Idiomas.getTexto(_perso.getCuenta().idioma, 56));
						return;
					}
					int puntosnuevos1 = mispuntos - precio;
					GestorSQL.setPuntoCuenta(puntosnuevos1, _perso.getCuentaID());
					GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out, Idiomas.getTexto(_perso.getCuenta().idioma, 57)
							+ (puntosnuevos1) + Idiomas.getTexto(_perso.getCuenta().idioma, 56));
					break;
				case 344: // Kolichas
					esvip = true;
					if (_perso.tieneObjModeloNoEquip(11158, precio)) {
						Objeto idobj = _perso.getObjModeloNoEquip(11158, precio);
						int cant = idobj.getCantidad();
						_perso.borrarObjetoEliminar(idobj.getID(), precio, true);
						int precd = cant - precio;
						GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out,
								Idiomas.getTexto(_perso.getCuenta().idioma, 57) + precd
										+ Idiomas.getTexto(_perso.getCuenta().idioma, 59));
					} else {
						Objeto idobj = _perso.getObjModeloNoEquip(11158, 1);
						int cantx = 0;
						if (idobj != null) {
							cantx = idobj.getCantidad();
						}
						GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out,
								Idiomas.getTexto(_perso.getCuenta().idioma, 55) + (precio - cantx)
										+ Idiomas.getTexto(_perso.getCuenta().idioma, 59));
						return;
					}
					break;
				case 440:
					esvip = true;
					if (_perso.tieneObjModeloNoEquip(7035, precio)) {
						Objeto idobj = _perso.getObjModeloNoEquip(7035, precio);
						int cant = idobj.getCantidad();
						_perso.borrarObjetoEliminar(idobj.getID(), precio, true);
						int precd = cant - precio;
						GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out,
								Idiomas.getTexto(_perso.getCuenta().idioma, 57) + precd
										+ Idiomas.getTexto(_perso.getCuenta().idioma, 198));
					} else {
						Objeto idobj = _perso.getObjModeloNoEquip(7035, 1);
						int cantx = 0;
						if (idobj != null) {
							cantx = idobj.getCantidad();
						}
						GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out,
								Idiomas.getTexto(_perso.getCuenta().idioma, 55) + (precio - cantx)
										+ Idiomas.getTexto(_perso.getCuenta().idioma, 198));
						return;
					}
					break;
				case 439:
					esvip = true;
					if (_perso.tieneObjModeloNoEquip(7662, precio)) {
						Objeto idobj = _perso.getObjModeloNoEquip(7662, precio);
						int cant = idobj.getCantidad();
						_perso.borrarObjetoEliminar(idobj.getID(), precio, true);
						int precd = cant - precio;
						GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out,
								Idiomas.getTexto(_perso.getCuenta().idioma, 57) + precd
										+ Idiomas.getTexto(_perso.getCuenta().idioma, 201));
					} else {
						Objeto idobj = _perso.getObjModeloNoEquip(7662, 1);
						int cantx = 0;
						if (idobj != null) {
							cantx = idobj.getCantidad();
						}
						GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out,
								Idiomas.getTexto(_perso.getCuenta().idioma, 55) + (precio - cantx)
										+ Idiomas.getTexto(_perso.getCuenta().idioma, 201));
						return;
					}
					break;
				case 441: // Moneda Astro
					esvip = true;
					if (_perso.tieneObjModeloNoEquip(980125, precio)) {
						Objeto idobj = _perso.getObjModeloNoEquip(980125, precio);
						int cant = idobj.getCantidad();
						_perso.borrarObjetoEliminar(idobj.getID(), precio, true);
						int precd = cant - precio;
						GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out,
								Idiomas.getTexto(_perso.getCuenta().idioma, 57) + precd
										+ Idiomas.getTexto(_perso.getCuenta().idioma, 169));
					} else {
						Objeto idobj = _perso.getObjModeloNoEquip(980125, 1);
						int cantx = 0;
						if (idobj != null) {
							cantx = idobj.getCantidad();
						}
						GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out,
								Idiomas.getTexto(_perso.getCuenta().idioma, 55) + (precio - cantx)
										+ Idiomas.getTexto(_perso.getCuenta().idioma, 169));
						return;
					}
					break;
				case 346:
					esvip = true;
					if (_perso.tieneObjModeloNoEquip(10275, precio)) {
						Objeto idobj = _perso.getObjModeloNoEquip(10275, precio);
						int cant = idobj.getCantidad();
						_perso.borrarObjetoEliminar(idobj.getID(), precio, true);
						int precd = cant - precio;
						GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out,
								Idiomas.getTexto(_perso.getCuenta().idioma, 57) + "" + precd + ""
										+ Idiomas.getTexto(_perso.getCuenta().idioma, 60));
					} else {
						Objeto idobj = _perso.getObjModeloNoEquip(10275, 1);
						int cantx = 0;
						if (idobj != null) {
							cantx = idobj.getCantidad();
						}
						GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out,
								Idiomas.getTexto(_perso.getCuenta().idioma, 55) + (precio - cantx)
										+ Idiomas.getTexto(_perso.getCuenta().idioma, 60));
						return;
					}
					break;
				case 430:// moneda fraternidad
					esvip = true;
					if (_perso.tieneObjModeloNoEquip(980245, precio)) {
						Objeto idobj = _perso.getObjModeloNoEquip(980245, precio);
						int cant = idobj.getCantidad();
						_perso.borrarObjetoEliminar(idobj.getID(), precio, true);
						int precd = cant - precio;
						GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out,
								Idiomas.getTexto(_perso.getCuenta().idioma, 57) + "" + precd + ""
										+ Idiomas.getTexto(_perso.getCuenta().idioma, 212));
					} else {
						Objeto idobj = _perso.getObjModeloNoEquip(980245, 1);
						int cantx = 0;
						if (idobj != null) {
							cantx = idobj.getCantidad();
						}
						GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out,
								Idiomas.getTexto(_perso.getCuenta().idioma, 55) + (precio - cantx)
										+ Idiomas.getTexto(_perso.getCuenta().idioma, 212));
						return;
					}
					break;
				case 432:// moneda Kamas de Hielo
					esvip = true;
					if(objModelo.getCondiciones().toString().equals("RS>0")) {
						if (_perso.tieneObjModeloNoEquip(980241, precio)) {
							Objeto idobj = _perso.getObjModeloNoEquip(980241, precio);
							int cant = idobj.getCantidad();
							_perso.borrarObjetoEliminar(idobj.getID(), precio, true);
							int precd = cant - precio;
							GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out,
									Idiomas.getTexto(_perso.getCuenta().idioma, 57) + "" + precd + ""
											+ Idiomas.getTexto(_perso.getCuenta().idioma, 213));
						} else {
							Objeto idobj = _perso.getObjModeloNoEquip(980241, 1);
							int cantx = 0;
							if (idobj != null) {
								cantx = idobj.getCantidad();
							}
							GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out,
									Idiomas.getTexto(_perso.getCuenta().idioma, 55) + (precio - cantx)
											+ Idiomas.getTexto(_perso.getCuenta().idioma, 213));
							return;
						}

					}else if (objModelo.getCondiciones().toString().equals("RS>1")) {
						if (_perso.tieneObjModeloNoEquip(31055, precio)) {
							Objeto idobj = _perso.getObjModeloNoEquip(31055, precio);
							int cant = idobj.getCantidad();
							_perso.borrarObjetoEliminar(idobj.getID(), precio, true);
							int precd = cant - precio;
							GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out,
									Idiomas.getTexto(_perso.getCuenta().idioma, 57) + "" + precd + ""
											+ Idiomas.getTexto(_perso.getCuenta().idioma, 213));
						} else {
							Objeto idobj = _perso.getObjModeloNoEquip(31055, 1);
							int cantx = 0;
							if (idobj != null) {
								cantx = idobj.getCantidad();
							}
							GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out,
									Idiomas.getTexto(_perso.getCuenta().idioma, 55) + (precio - cantx)
											+ Idiomas.getTexto(_perso.getCuenta().idioma, 213));
							return;
						}

					}
					break;
				case 434:// moneda r1
					esvip = true;
					if (_perso.tieneObjModeloNoEquip(980242, precio)) {
						Objeto idobj = _perso.getObjModeloNoEquip(980242, precio);
						int cant = idobj.getCantidad();
						_perso.borrarObjetoEliminar(idobj.getID(), precio, true);
						int precd = cant - precio;
						GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out,
								Idiomas.getTexto(_perso.getCuenta().idioma, 57) + "" + precd + ""
										+ Idiomas.getTexto(_perso.getCuenta().idioma, 213));
					} else {
						Objeto idobj = _perso.getObjModeloNoEquip(980242, 1);
						int cantx = 0;
						if (idobj != null) {
							cantx = idobj.getCantidad();
						}
						GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out,
								Idiomas.getTexto(_perso.getCuenta().idioma, 55) + (precio - cantx)
										+ Idiomas.getTexto(_perso.getCuenta().idioma, 213));
						return;
					}
					break;
				case 437:// moneda r2
					esvip = true;
					if (_perso.tieneObjModeloNoEquip(980243, precio)) {
						Objeto idobj = _perso.getObjModeloNoEquip(980243, precio);
						int cant = idobj.getCantidad();
						_perso.borrarObjetoEliminar(idobj.getID(), precio, true);
						int precd = cant - precio;
						GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out,
								Idiomas.getTexto(_perso.getCuenta().idioma, 57) + "" + precd + ""
										+ Idiomas.getTexto(_perso.getCuenta().idioma, 215));
					} else {
						Objeto idobj = _perso.getObjModeloNoEquip(980243, 1);
						int cantx = 0;
						if (idobj != null) {
							cantx = idobj.getCantidad();
						}
						GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out,
								Idiomas.getTexto(_perso.getCuenta().idioma, 55) + (precio - cantx)
										+ Idiomas.getTexto(_perso.getCuenta().idioma, 215));
						return;
					}
					break;
				case 438:// moneda r3
					esvip = true;
					if (_perso.tieneObjModeloNoEquip(980244, precio)) {
						Objeto idobj = _perso.getObjModeloNoEquip(980244, precio);
						int cant = idobj.getCantidad();
						_perso.borrarObjetoEliminar(idobj.getID(), precio, true);
						int precd = cant - precio;
						GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out,
								Idiomas.getTexto(_perso.getCuenta().idioma, 57) + "" + precd + ""
										+ Idiomas.getTexto(_perso.getCuenta().idioma, 216));
					} else {
						Objeto idobj = _perso.getObjModeloNoEquip(980244, 1);
						int cantx = 0;
						if (idobj != null) {
							cantx = idobj.getCantidad();
						}
						GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out,
								Idiomas.getTexto(_perso.getCuenta().idioma, 55) + (precio - cantx)
										+ Idiomas.getTexto(_perso.getCuenta().idioma, 216));
						return;
					}
					break;
				case 5010:// doplones
					esvip = true;
					if (_perso.tieneObjModeloNoEquip(10303, precio)) {
						Objeto idobj = _perso.getObjModeloNoEquip(10303, precio);
						int cant = idobj.getCantidad();
						_perso.borrarObjetoEliminar(idobj.getID(), precio, true);
						int precd = cant - precio;
						GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out,
								Idiomas.getTexto(_perso.getCuenta().idioma, 57) + "" + precd + ""
										+ Idiomas.getTexto(_perso.getCuenta().idioma, 217));
					} else {
						Objeto idobj = _perso.getObjModeloNoEquip(10303, 1);
						int cantx = 0;
						if (idobj != null) {
							cantx = idobj.getCantidad();
						}
						GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out,
								Idiomas.getTexto(_perso.getCuenta().idioma, 55) + (precio - cantx)
										+ Idiomas.getTexto(_perso.getCuenta().idioma, 217));
						return;
					}
					break;
				case 1234:// MONEDA COPO NIEVE
					esvip = true;
					if (_perso.tieneObjModeloNoEquip(980242, precio)) {
						Objeto idobj = _perso.getObjModeloNoEquip(980242, precio);
						int cant = idobj.getCantidad();
						_perso.borrarObjetoEliminar(idobj.getID(), precio, true);
						int precd = cant - precio;
						GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out,
								Idiomas.getTexto(_perso.getCuenta().idioma, 57) + "" + precd + ""
										+ Idiomas.getTexto(_perso.getCuenta().idioma, 221));
					} else {
						Objeto idobj = _perso.getObjModeloNoEquip(980242, 1);
						int cantx = 0;
						if (idobj != null) {
							cantx = idobj.getCantidad();
						}
						GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out,
								Idiomas.getTexto(_perso.getCuenta().idioma, 55) + (precio - cantx)
										+ Idiomas.getTexto(_perso.getCuenta().idioma, 221));
						return;
					}
					break;
				default:
					if (_perso.getKamas() < precio) {
						GestorSalida.ENVIAR_EBE_ERROR_DE_COMPRA(_out);
						return;
					}
					break;
				}
				Objeto nuevoObj = objModelo.crearObjDesdeModelo(cantidad, false);
				if (!_perso.addObjetoSimilar(nuevoObj, true, -1)) {
					MundoDofus.addObjeto(nuevoObj, true);
					_perso.addObjetoPut(nuevoObj);
					GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_out, nuevoObj);
				}
				if (!esvip) {
					long nuevasKamas = _perso.getKamas() - precio;
					_perso.setKamas(nuevasKamas);
				}
				if (npc.getModeloBD().getID() == 596) {
					String ip = _perso.getCuenta().getActualIP();
					if (MundoDofus.comprasMercader.containsKey(ip)) {
						int veces = MundoDofus.comprasMercader.get(ip);
						MundoDofus.comprasMercader.remove(ip);
						MundoDofus.comprasMercader.put(ip, veces + 1);
					} else {
						MundoDofus.comprasMercader.put(ip, 1);
					}
				}
				GestorSalida.ENVIAR_EBK_COMPRADO(_out);
				GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
				GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso);
			} catch (Exception e) {
				Emu.creaLogs(e);
				GestorSalida.ENVIAR_EBE_ERROR_DE_COMPRA(_out);
				return;
			}
		} else {
			Personaje mercante = MundoDofus.getPersonaje(_perso.getIntercambiandoCon());
			try {
				if (mercante == null) {
					return;
				}
				if (mercante.enLinea()) {
					GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
							Idiomas.getTexto(_perso.getCuenta().idioma, 61));
					GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(_out);
					return;
				}
				int id = Integer.parseInt(infos[0]);
				int cant = Integer.parseInt(infos[1]);
				if (cant <= 0) {
					return;
				}
				Objeto objeto = MundoDofus.getObjeto(id);
				Tienda tienda = MundoDofus.getObjTienda(id);
				if (objeto == null || tienda == null) {
					GestorSalida.ENVIAR_EBE_ERROR_DE_COMPRA(_out);
					return;
				}
				int precio = tienda.getPrecio() * cant;
				if (_perso.getKamas() < precio) {
					GestorSalida.ENVIAR_EBE_ERROR_DE_COMPRA(_out);
					return;
				}
				int cantObjeto = objeto.getCantidad();
				if (cant > cantObjeto) {
					cant = cantObjeto;
				}
				if (cant == cantObjeto) {
					long nuevasKamas = _perso.getKamas() - precio;
					_perso.setKamas(nuevasKamas);
					mercante.setKamas(mercante.getKamas() + precio);
					mercante.borrarObjTienda(objeto);
					if (!_perso.addObjetoSimilar(objeto, true, id)) {
						MundoDofus.eliminarObjeto(id);
						_perso.addObjetoPut(objeto);
						GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_out, objeto);
					} else {
						MundoDofus.eliminarObjeto(id);
					}
					GestorSalida.ENVIAR_EBK_COMPRADO(_out);
					GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
					GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso);
					GestorSQL.SALVAR_PERSONAJE(_perso, true);
					GestorSalida.ENVIAR_EL_LISTA_TIENDA_PERSONAJE(_out, mercante);
				} else if (cant < cantObjeto) {
					Objeto nuevoObj = Objeto.clonarObjeto(objeto, cant);
					int nuevaCant = cantObjeto - cant;
					objeto.setCantidad(nuevaCant);
					tienda.setCantidad(nuevaCant);
					long nuevasKamas = _perso.getKamas() - precio;
					_perso.setKamas(nuevasKamas);
					mercante.setKamas(mercante.getKamas() + precio);
					if (!_perso.addObjetoSimilar(nuevoObj, true, id)) {
						MundoDofus.addObjeto(nuevoObj, true);
						_perso.addObjetoPut(nuevoObj);
						GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_out, nuevoObj);
					}
					GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
					GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso);
					GestorSalida.ENVIAR_EL_LISTA_TIENDA_PERSONAJE(_out, mercante);
					GestorSalida.ENVIAR_EBK_COMPRADO(_out);
					GestorSQL.SALVAR_PERSONAJE(mercante, true);
					GestorSQL.SALVAR_PERSONAJE(_perso, true);
					GestorSQL.SALVAR_OBJETO(objeto);
					GestorSQL.ACTUALIZAR_CANT_TIENDA(id, nuevaCant);
				} else {
					GestorSalida.ENVIAR_EBE_ERROR_DE_COMPRA(_out);
					return;
				}
				if (mercante.getTienda().size() <= 0) {
					GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(mercante.getMapa(), mercante.getID());
					mercante.getMapa().removerMercante(mercante.getID());
					mercante.setMercante(0);
					GestorSQL.SALVAR_MERCANTES(mercante.getMapa());
					GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(_out);
				}
			} catch (Exception e) {
				Emu.creaLogs(e);
				GestorSalida.ENVIAR_EBE_ERROR_DE_COMPRA(_out);
				GestorSalida.ENVIAR_EL_LISTA_TIENDA_PERSONAJE(_out, mercante);
				return;
			}
		}
	}

	private static void intercambio_Cerrar(Personaje _perso, PrintWriter _out) {
		_perso.setIsOnAction(false);
		if (_perso.abrepanelinter) {
			_perso.abrepanelinter = false;
		}
		if (_perso.getIntercambio() == null && !_perso.enBanco() && _perso.getHaciendoTrabajo() == null
				&& _perso.getIntercambiandoCon() == 0 && _perso.getEnCercado() == null && _perso.getTrueque() == null
				&& !_perso.getListaArtesanos() && _perso.getCofre() == null && !_perso.getMochilaMontura()
				&& !_perso.getRompiendo() && _perso.getTallerInvitado() == null) {
			return;
		} else if (_perso.getCofre() != null) {
			_perso.setCofre(null);
			_perso.setOcupado(false);
			GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(_out);
			return;
		} else if (_perso.enBanco()) {
			_perso.setEnBanco(false);
			_perso.setOcupado(false);
			GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(_out);
			return;
		} else if (_perso.getListaArtesanos()) {
			_perso.setListaArtesanos(false);
			_perso.setIntercambiandoCon(0);
			_perso.setOcupado(false);
			GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(_out);
			return;
		} else if (_perso.getRompiendo()) {
			_perso.setOcupado(false);
			_perso.paramag.clear();
			_perso.setRompiendo(false);
			GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(_out);
			if (_perso.getHaciendoTrabajo() != null) {
				if (_perso.getHaciendoTrabajo()._celda != null) {
					GestorSalida.ENVIAR_GDF_FORZADO_MAPA(_perso.getMapa(),
							_perso.getHaciendoTrabajo()._celda.getID() + ";1;1");
				}
			}
			/*
			 * if (_perso.continua) { Interactivos.tutorialTaller2(_perso); }
			 */
			return;
		} else if (_perso.getTrueque() != null) {
			_perso.revivirMascota = false;
			_perso.pusomascota = false;
			_perso.pusopolvo = false;
			_perso.torrepvm = false;
			_perso.perfecciona = false;
			_perso.elemento = false;
			_perso.pusoperfec = false;
			_perso.setIntercambiandoCon(0);
			_perso.setOcupado(false);
			_perso.setTrueque(null);
			GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(_out);
			return;
		} else if (_perso.getIntercambio() != null) {
			_perso.setOcupado(false);
			_perso.getIntercambio().cancel();
			GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(_out);
			return;
		} else if (_perso.getMochilaMontura()) {
			_perso.setOcupado(false);
			_perso.setDragopaveando(false);
			_perso.setIntercambio(null);
			_perso.setIntercambiandoCon(0);
			GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(_out);
			return;
		} else if (_perso.getTallerInvitado() != null) {
			Personaje perso = MundoDofus.getPersonaje(_perso.getIntercambiandoCon());
			if (perso != null) {
				if (perso.enLinea()) {
					PrintWriter out = perso.getCuenta().getEntradaPersonaje().getOut();
					perso.setIntercambiandoCon(0);
					perso.setOcupado(false);
					perso.setTallerInvitado(null);
					perso.setHaciendoTrabajo(null);
					GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(out);
				}
			}
			_perso.setIntercambiandoCon(0);
			_perso.setOcupado(false);
			_perso.setTallerInvitado(null);
			_perso.setHaciendoTrabajo(null);
			_perso.sethablandoNPC(0);
			GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(_out);
			return;
		} else if (_perso.getHaciendoTrabajo() != null) {
			if (_perso.getIntercambiandoCon() > 0) {
				Personaje perso = MundoDofus.getPersonaje(_perso.getIntercambiandoCon());
				if (perso != null) {
					if (perso.enLinea()) {
						PrintWriter out = perso.getCuenta().getEntradaPersonaje().getOut();
						perso.setHaciendoTrabajo(null);
						perso.setOcupado(false);
						perso.setIntercambiandoCon(0);
						GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(out);
					}
				}
			}
			if (_perso.celdadeparo != 0) {
				GestorSalida.ENVIAR_GDF_FORZADO_PERSONAJE(_perso, _perso.celdadeparo, 1, 1);
				_perso.celdadeparo = 0;
			}
			_perso.getHaciendoTrabajo().resetReceta();
			_perso.setOcupado(false);
			_perso.setHaciendoTrabajo(null);
			_perso.setIntercambiandoCon(0);
			GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(_out);
			return;
		} else if (_perso.getEnCercado() != null) {
			_perso.salirDeCercado();
			_perso.setOcupado(false);
			GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(_out);
			return;
		} else if (_perso.getIntercambiandoCon() < 0 && !_perso.getRecaudando()) {
			_perso.setIntercambiandoCon(0);
			_perso.setOcupado(false);
			GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(_out);
			_perso.sethablandoNPC(0);
			return;
		} else if (_perso.getIntercambiandoCon() > 0 && !_perso.getRecaudando()) {
			Personaje perso = MundoDofus.getPersonaje(_perso.getIntercambiandoCon());
			if (perso != null) {
				if (perso.enLinea()) {
					PrintWriter out = perso.getCuenta().getEntradaPersonaje().getOut();
					perso.setIntercambiandoCon(0);
					perso.setIntercambio(null);
					perso.setOcupado(false);
					GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(out);
				}
			}
			_perso.setIntercambiandoCon(0);
			_perso.setOcupado(false);
			_perso.setIntercambio(null);
			_perso.sethablandoNPC(0);
			GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(_out);
			return;
		} else if (_perso.getRecaudando()) {
			Recaudador recau = MundoDofus.getRecaudador(_perso.getRecaudandoRecauID());
			for (Personaje z : MundoDofus.getGremio(recau.getGremioID()).getPjMiembros()) {
				if (z == null) {
					continue;
				}
				if (z.enLinea()) {
					GestorSalida.ENVIAR_gITM_INFO_RECAUDADOR(z, Recaudador.analizarRecaudadores(z.getGremio().getID()));
					String str = "";
					str += "G" + recau.getN1() + "," + recau.getN2();
					str += "|.|" + MundoDofus.getMapa((short) recau.getMapaID()).getCoordX() + "|"
							+ MundoDofus.getMapa((short) recau.getMapaID()).getCoordY() + "|";
					str += _perso.getNombre() + "|";
					str += recau.getXp();
					if (!recau.stringObjetos().isEmpty()) {
						str += ";" + recau.stringObjetos();
					}
					GestorSalida.GAME_SEND_gT_PACKET(z, str);
				}
			}
			recau.setEnRecolecta(false);
			_perso.getMapa().removeNPC(recau.getID());
			GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(_perso.getMapa(), recau.getID());
			recau.borrarRecauPorRecolecta(recau.getID(), _perso);
			_perso.setRecaudando(false);
			_perso.setRecaudandoRecaudadorID(0);
			_perso.setIntercambiandoCon(0);
			_perso.setOcupado(false);
			GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(_out);
			return;
		}
	}

	private static void intercambio_Iniciar(String packet, Personaje _perso, PrintWriter _out) {
		if (_perso.esFantasma() || !_perso.puedeAbrir) {
			GestorSalida.ENVIAR_BN_NADA(_out);
			return;
		}
		if (_perso.esMaitre) {
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 62), Colores.AZUL);
			return;
		}
		if (packet.substring(2, 4).equals("11")) {// abrir HDV compra
			int mapa = _perso.getMapa().getID();
			boolean falsomap = false;
			try {
				if (Integer.parseInt(packet.split("\\|")[1]) == -999 || _perso.abrepanelinter) {
					mapa = 7411;
					falsomap = true;
				}
			} catch (Exception e) {
				mapa = _perso.getMapa().getID();
			}
			if (_perso.getIntercambiandoCon() < 0) {
				GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(_out);
			}
			if (_perso.getDeshonor() >= 5) {
				GestorSalida.ENVIAR_Im_INFORMACION(_out, "183");
				return;
			}
			Mercadillo toOpen = MundoDofus.getPuestoMerca(mapa);
			if (toOpen == null) {
				return;
			}
			String info = "1,10,100;" + toOpen.getTipoObjPermitidos() + ";" + toOpen.porcentajeImpuesto() + ";"
					+ toOpen.getNivelMax() + ";" + toOpen.getMaxObjCuenta() + ";-1;" + toOpen.getTiempoVenta();
			GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(_perso, 11, info);
			_perso.setOcupado(true);
			_perso.abrepanelinter = true;
			if (!falsomap) {
				_perso.setIntercambiandoCon(0 - _perso.getMapa().getID());
			} else {
				_perso.setIntercambiandoCon(0 - 7411);
			}
			return;
		} else if (packet.substring(2, 4).equals("10")) {// abre HDV venta
			int mapa = _perso.getMapa().getID();
			boolean falsomap = false;
			try {
				if (Integer.parseInt(packet.split("\\|")[1]) == -1 && _perso.abrepanelinter) {
					mapa = 7411;
					falsomap = true;
				}
			} catch (Exception e) {
				mapa = _perso.getMapa().getID();
			}
			if (_perso.getIntercambiandoCon() < 0) {
				GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(_out);
			}
			if (_perso.getDeshonor() >= 5) {
				GestorSalida.ENVIAR_Im_INFORMACION(_out, "183");
				return;
			}
			Mercadillo mercadillo = MundoDofus.getPuestoMerca(mapa);
			if (mercadillo == null) {
				return;
			}
			String info = "1,10,100;" + mercadillo.getTipoObjPermitidos() + ";" + mercadillo.porcentajeImpuesto() + ";"
					+ mercadillo.getNivelMax() + ";" + mercadillo.getMaxObjCuenta() + ";-1;"
					+ mercadillo.getTiempoVenta();
			GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(_perso, 10, info);
			if (!falsomap) {
				_perso.setIntercambiandoCon(0 - _perso.getMapa().getID());
			} else {
				_perso.setIntercambiandoCon(0 - 7411);
			}
			_perso.setOcupado(true);
			GestorSalida.GAME_SEND_HDVITEM_SELLING(_perso);
			return;
		} else if (packet.substring(2, 4).equals("15")) {// dragopavo
			try {
				if (_perso.getIntercambiandoCon() != 0) {
					return;
				}
				Dragopavo montura = _perso.getMontura();
				int idMontura = montura.getID();
				GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(_out, 15, _perso.getMontura().getID() + "");
				GestorSalida.ENVIAR_EL_LISTA_OBJETOS_DRAGOPAVO(_out, montura);
				GestorSalida.ENVIAR_Ew_PODS_MONTURA(_perso, montura.getPodsActuales());
				_perso.setIntercambiandoCon(idMontura);
				_perso.setDragopaveando(true);
				_perso.setOcupado(true);
			} catch (Exception e) {
				Emu.creaLogs(e);
			}
			return;
		} else if (packet.substring(2, 4).equals("12")) {// invitar a taller
			try {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 63),
						Colores.ROJO);
				return;
			} catch (Exception e) {
				Emu.creaLogs(e);
			}
			try {
				String[] nuevo = packet.substring(5).split("\\|");
				int idInvitado = Integer.parseInt(nuevo[0]);
				int idTrabajo = Integer.parseInt(nuevo[1]);
				AccionTrabajo accionT = null;
				boolean paso = false;
				for (StatsOficio statOficio : _perso.getStatsOficios().values()) {
					Oficio oficio = statOficio.getOficio();
					if (oficio == null) {
						continue;
					}
					for (AccionTrabajo trabajo : CentroInfo.getTrabajosPorOficios(oficio.getID(),
							statOficio.getNivel())) {
						if (trabajo.getIDTrabajo() != idTrabajo) {
							continue;
						}
						accionT = trabajo;
						paso = true;
						break;
					}
					if (paso) {
						break;
					}
				}
				if (accionT == null) {
					GestorSalida.ENVIAR_BN_NADA(_out);
					return;
				}
				Personaje invitado = MundoDofus.getPersonaje(idInvitado);
				if (!invitado.enLinea() || invitado == null) {
					GestorSalida.ENVIAR_BN_NADA(_out);
					return;
				}
				_perso.setHaciendoTrabajo(accionT);
				invitado.setHaciendoTrabajo(accionT);
				_perso.setIntercambiandoCon(idInvitado);
				invitado.setIntercambiandoCon(_perso.getID());
				GestorSalida.ENVIAR_ERK_CONSULTA_INTERCAMBIO(_out, _perso.getID(), idInvitado, 12);// invitador
				GestorSalida.ENVIAR_ERK_CONSULTA_INTERCAMBIO(invitado.getCuenta().getEntradaPersonaje().getOut(),
						_perso.getID(), idInvitado, 13);// invitado
			} catch (Exception e) {
				Emu.creaLogs(e);
			}
			return;
		}
		switch (packet.charAt(2)) {
		case '0':// Si NPC
			try {
				int npcID = Integer.parseInt(packet.substring(4));
				NPC npc = _perso.getMapa().getNPC(npcID);
				if (npc == null) {
					return;
				}
				GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(_out, 0, npcID + "");
				GestorSalida.ENVIAR_EL_LISTA_OBJETOS_NPC(_out, npc, _perso.getNivel());
				_perso.setIntercambiandoCon(npcID);
				_perso.setOcupado(true);
				_perso.sethablandoNPC(npc.getModeloBD().getID());
				try {
					Misiones.checkMision(_perso, 0);
				} catch (Exception e) {
					Emu.creaLogs(e);
				}
				// GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, "<b>Recuerda:</b> En esta beta,
				// para no ense�ar algunos de los items nuevos p�blicos sÓlo ver�s los items
				// hasta nivel "+((_perso.getNivel())+10)+" \nTu nivel
				// ("+_perso.getNivel()+")+10", Colores.ROJO);
			} catch (NumberFormatException e) {
			}
			break;
		case '1':// Si jugador
			try {
				int idObjetivo = Integer.parseInt(packet.substring(4));
				Personaje objetivo = MundoDofus.getPersonaje(idObjetivo);
				if (objetivo == null || objetivo.getMapa() != _perso.getMapa() || !objetivo.enLinea()) {
					GestorSalida.ENVIAR_ERE_ERROR_CONSULTA(_out, 'E');
					return;
				}
				if (objetivo.estaOcupado() || _perso.estaOcupado() || objetivo.getIntercambiandoCon() != 0) {
					GestorSalida.ENVIAR_ERE_ERROR_CONSULTA(_out, 'O');
					return;
				}
				GestorSalida.ENVIAR_ERK_CONSULTA_INTERCAMBIO(_out, _perso.getID(), idObjetivo, 1);
				GestorSalida.ENVIAR_ERK_CONSULTA_INTERCAMBIO(objetivo.getCuenta().getEntradaPersonaje().getOut(),
						_perso.getID(), idObjetivo, 1);
				_perso.setIntercambiandoCon(idObjetivo);
				objetivo.setIntercambiandoCon(_perso.getID());
				_perso.setOcupado(true);
				objetivo.setOcupado(true);
			} catch (NumberFormatException e) {
			}
			break;
		case '4':// Si mercante
			try {
				int idMercante = Integer.parseInt(packet.split("\\|")[1]);
				Personaje mercante = MundoDofus.getPersonaje(idMercante);
				if (mercante == null) {
					return;
				}
				if (mercante.getTienda().size() <= 0 || mercante.getMercante() == 0) {
					GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(mercante.getMapa(), mercante.getID());
					mercante.getMapa().removerMercante(mercante.getID());
					mercante.setMercante(0);
					GestorSQL.SALVAR_MERCANTES(mercante.getMapa());
					GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(_out);
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 64),
							Colores.ROJO);
					return;
				}
				GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(_out, 4, idMercante + "");
				GestorSalida.ENVIAR_EL_LISTA_TIENDA_PERSONAJE(_out, mercante);
				_perso.setIntercambiandoCon(idMercante);
				_perso.setOcupado(true);
			} catch (NumberFormatException e) {
			}
			break;
		case '6':// si abro tienda
			try {
				if (_perso.getIntercambiandoCon() != 0 || _perso.getTrueque() != null || _perso.getEnCercado() != null
						|| _perso.getListaArtesanos() || _perso.getRompiendo() || _perso.getHaciendoTrabajo() != null
						|| _perso.getIntercambiandoCon() != 0 || _perso.getCofre() != null
						|| _perso.getIntercambio() != null || _perso.enBanco() || _perso.getMochilaMontura()
						|| _perso.getHaciendoTrabajo() != null) {
					GestorSalida.ENVIAR_BN_NADA(_perso);
					return;
				}
				GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(_out, 6, _perso.getID() + "");
				GestorSalida.ENVIAR_EL_LISTA_TIENDA_PERSONAJE(_out, _perso);
				_perso.setIntercambiandoCon(_perso.getID());
				_perso.setOcupado(true);
			} catch (Exception e) {
			}
		case '8':// Si Recaudador
			try {
				int RecaudadorID = Integer.parseInt(packet.substring(4));
				Recaudador recau = MundoDofus.getRecaudador(RecaudadorID);
				if (recau == null || recau.getEstadoPelea() > 0 || recau.getEnRecolecta() || _perso.getGremio() == null
						|| !_perso.getMiembroGremio().puede(CentroInfo.G_RECOLECTARRECAUDADOR)) {
					GestorSalida.ENVIAR_BN_NADA(_perso);
					return;
				}
				recau.setEnRecolecta(true);
				GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(_out, 8, recau.getID() + "");
				GestorSalida.ENVIAR_EL_LISTA_OBJETOS_RECAUDADOR(_out, recau);
				_perso.setIntercambiandoCon(recau.getID());
				_perso.setRecaudando(true);
				_perso.setRecaudandoRecaudadorID(recau.getID());
				_perso.setOcupado(true);
			} catch (NumberFormatException e) {
			}
			break;
		}
	}

	static void analizar_Ambiente(String packet, Personaje _perso) {
		switch (packet.charAt(1)) {
		case 'D':// cambia de direccion
			ambiente_Cambio_Direccion(packet, _perso);
			break;
		case 'U':// Emote
			ambiente_Emote(packet, _perso);
			break;
		}
	}

	private static void ambiente_Emote(String packet, Personaje _perso) {
		int emote = -1;
		try {
			emote = Integer.parseInt(packet.substring(2));
		} catch (Exception e) {
		}
		if (emote == -1 || _perso.getPelea() != null) {
			return;
		}
		switch (emote) {
		case 19:
		case 1:
			if (_perso.estaSentado()) {
				emote = 0;
			}
			_perso.setSentado(!_perso.estaSentado());
			break;
		}
		_perso.setEmoteActivado(emote);
		String tiempo = "";
		if (emote == 7) {
			tiempo = "9000";
		} else if (emote == 7) {
			tiempo = "5000";
		}
		Cercado cercado = _perso.getMapa().getCercado();
		GestorSalida.ENVIAR_eUK_EMOTE_MAPA(_perso.getMapa(), _perso.getID(), emote, tiempo);
		if ((emote == 2 || emote == 4 || emote == 3 || emote == 6 || emote == 8 || emote == 10) && cercado != null) {
			ArrayList<Dragopavo> pavos = new ArrayList<>();
			for (Integer pavo : cercado.getListaCriando()) {
				if (MundoDofus.getDragopavoPorID(pavo).getDueÑo() == _perso.getID()) {
					pavos.add(MundoDofus.getDragopavoPorID(pavo));
				}
			}
			if (pavos.size() > 0) {
				int casillas = 0;
				switch (emote) {
				case 2:
				case 4:
					casillas = 1;
					break;
				case 3:
				case 8:
					casillas = Formulas.getRandomValor(2, 3);
					break;
				case 6:
				case 10:
					casillas = Formulas.getRandomValor(4, 7);
					break;
				}
				boolean alejar;
				if (emote == 2 || emote == 3 || emote == 10) {
					alejar = false;
				} else {
					alejar = true;
				}
				Dragopavo dragopavo = pavos.get(Formulas.getRandomValor(0, pavos.size() - 1));
				if (dragopavo != null) {
					dragopavo.moverMontura(_perso, casillas, alejar);
				}
			}
		}
	}

	private static void ambiente_Cambio_Direccion(String packet, Personaje _perso) {
		try {
			if (_perso.getPelea() != null) {
				return;
			}
			int dir = Integer.parseInt(packet.substring(2));
			_perso.setOrientacion(dir);
			GestorSalida.ENVIAR_eD_CAMBIAR_ORIENTACION(_perso.getMapa(), _perso.getID(), dir);
		} catch (NumberFormatException e) {
			Emu.creaLogs(e);
			return;
		}
	}

	static void analizar_Hechizos(String packet, Personaje _perso, PrintWriter _out) {
		switch (packet.charAt(1)) {
		case 'B':
			hechizos_Boost(packet, _perso, _out);
			break;
		case 'F':
			hechizos_Olvidar(packet, _perso, _out);
			break;
		case 'M':
			hechizos_Acceso_Rapido(packet, _perso, _out);
			break;
		case 'R':
			hechizos_Acceso_remove(packet, _perso, _out);
			break;
		}
	}

	private static void hechizos_Acceso_remove(String packet, Personaje _perso, PrintWriter _out) {
		try {
			int hechizoID = Integer.parseInt(packet.substring(2));
			StatsHechizos hechizo = _perso.getStatsHechizo(hechizoID);
			if (hechizo != null) {
				_perso.setPosHechizo(hechizoID, Encriptador.getValorHashPorNumero(0), false);
			}
			GestorSalida.ENVIAR_SL_LISTA_HECHIZOS(_perso);
		} catch (Exception e) {
			Emu.creaLogs(e);
		}
	}

	private static void hechizos_Acceso_Rapido(String packet, Personaje _perso, PrintWriter _out) {
		try {
			int hechizoID = Integer.parseInt(packet.substring(2).split("\\|")[0]);
			int posicion = Integer.parseInt(packet.substring(2).split("\\|")[1]);
			StatsHechizos hechizo = _perso.getStatsHechizo(hechizoID);
			if (hechizo != null) {
				_perso.setPosHechizo(hechizoID, Encriptador.getValorHashPorNumero(posicion), false);
			}
			GestorSalida.ENVIAR_BN_NADA(_out);
		} catch (Exception e) {
			Emu.creaLogs(e);
		}
	}

	private static void hechizos_Boost(String packet, Personaje _perso, PrintWriter _out) {
		try {
			int id = Integer.parseInt(packet.substring(2));
			if (_perso.boostearHechizo(id)) {
				GestorSalida.ENVIAR_SUK_SUBIR_NIVEL_HECHIZO(_out, id, _perso.getStatsHechizo(id).getNivel());
				GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
			} else {
				GestorSalida.ENVIAR_SUE_SUBIR_NIVEL_HECHIZO_ERROR(_out);
				return;
			}
		} catch (Exception e) {
			Emu.creaLogs(e);
			GestorSalida.ENVIAR_SUE_SUBIR_NIVEL_HECHIZO_ERROR(_out);
			return;
		}
	}

	private static void hechizos_Olvidar(String packet, Personaje _perso, PrintWriter _out) {
		if (!_perso.estaOlvidandoHechizo()) {
			return;
		}
		int id = Integer.parseInt(packet.substring(2));
		if (_perso.olvidarHechizo(id)) {
			GestorSalida.ENVIAR_SUK_SUBIR_NIVEL_HECHIZO(_out, id, _perso.getStatsHechizo(id).getNivel());
			GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
			_perso.setOlvidandoHechizo(false);
		}
	}

	static void analizar_Peleas(String packet, Personaje _perso, PrintWriter _out) {
		Pelea pelea = _perso.getPelea();
		switch (packet.charAt(1)) {
		case 'D':// Detalles de un combate (lista de combates)
			int key = -1;
			try {
				key = Integer.parseInt(packet.substring(2).replace((0x0) + "", ""));
			} catch (Exception e) {
				Emu.creaLogs(e);
			}
			if (key == -1) {
				return;
			}
			GestorSalida.ENVIAR_fD_DETALLES_PELEA(_out, _perso.getMapa().getPeleas().get(key));
			break;
		case 'H':// Ayuda
			if (pelea == null) {
				return;
			}
			pelea.botonAyuda(_perso.getID());
			GestorSalida.ENVIAR_BN_NADA(_out);
			break;
		case 'L':// lista de combates
			GestorSalida.ENVIAR_fL_LISTA_PELEAS(_out, _perso.getMapa());
			break;
		case 'N':// Bloquear el combate a otros jugadores
			if (pelea == null) {
				return;
			}
			pelea.botonBloquearMasJug(_perso.getID());
			GestorSalida.ENVIAR_BN_NADA(_out);
			break;
		case 'P':// solamente el grupo
			if (pelea == null || _perso.getGrupo() == null) {
				return;
			}
			pelea.botonSoloGrupo(_perso.getID());
			GestorSalida.ENVIAR_BN_NADA(_out);
			break;
		case 'S':// Bloquear a los espectadores
			if (pelea == null) {
				return;
			}
			pelea.botonBloquearEspect(_perso.getID());
			GestorSalida.ENVIAR_BN_NADA(_out);
			break;
		}
	}

	static void analizar_Basicos(String packet, Personaje _perso, PrintWriter _out, Cuenta _cuenta) {
		switch (packet.charAt(1)) {
		case 'a':// Console
			teleportMinimapa(packet, _perso, _cuenta);
			break;
		case 'A':// Console
			basicos_Consola(packet, _perso, _cuenta);
			break;
		case 'D':// datos y hora
			StringBuilder packet2 = new StringBuilder("");
			packet2.append(ServidorPersonaje.getFechaServer());
			packet2.append((char) 0x00);
			packet2.append(ServidorPersonaje.getTiempoServer());
			GestorSalida.enviar(_perso, packet2.toString());
			break;
		case 'M':// chat mensaje
			basicos_Chat(packet, _perso, _out, _cuenta);
			break;
		case 'W':// mensaje de informacion
			basicos_Mensaje_Informacion(packet, _perso);
			break;
		case 'S':// emotico
			_perso.emote(packet.substring(2));
			break;
		case 'Y':// estado
			basicos_Estado(packet, _perso, _out);
			break;
		}
	}

	private static void basicos_Estado(String packet, Personaje _perso, PrintWriter _out) {
		switch (packet.charAt(2)) {
		case 'A': // Ausente
			if (_perso.estaAusente()) {
				GestorSalida.ENVIAR_Im_INFORMACION(_out, "038");
				_perso.setEstaAusente(false);
			} else {
				GestorSalida.ENVIAR_Im_INFORMACION(_out, "037");
				_perso.setEstaAusente(true);
			}
			break;
		case 'I': // Invisible
			if (_perso.esInvisible()) {
				GestorSalida.ENVIAR_Im_INFORMACION(_out, "051");
				_perso.setEsInvisible(false);
			} else {
				GestorSalida.ENVIAR_Im_INFORMACION(_out, "050");
				_perso.setEsInvisible(true);
			}
			break;
		}
	}

	private static void basicos_Consola(String packet, Personaje _perso, Cuenta _cuenta) {
		EntradaPersonaje entpj = _cuenta.getEntradaPersonaje();
		if (entpj.get_comando() == null) {
			entpj.set_comando(new Comandos(_perso));
		}
		entpj.get_comando().consolaComando(packet);
	}

	private static void teleportMinimapa(String packet, Personaje _perso, Cuenta _cuenta) {
		if (_perso.getCuenta().getRango() > 1) {
			switch (packet.charAt(2)) {
			case 'M':
				String coord = packet.split("M")[1];
				if (coord.contains("NaN")) {
					return;
				}
				int idmapa = MundoDofus.mapaPorCoordenadasi(Integer.parseInt(coord.split(",")[0]),
						Integer.parseInt(coord.split(",")[1]));
				if (idmapa == 0) {
					idmapa = GestorSQL.getMapa(Integer.parseInt(coord.split(",")[0]),
							Integer.parseInt(coord.split(",")[1]));
					if (idmapa == 0) {
						return;
					}
				}
				Mapa map = MundoDofus.getMapa((short) idmapa);
				int celda = 280;
				try {
					celda = map.getRandomCeldaIDLibre();
				} catch (Exception e) {
					celda = 280;
				}
				_perso.teleport((short) idmapa, celda);
				break;
			}
		}
	}

	public static boolean compruebaTps(Personaje _perso) {
		if ((_perso == null) || !_perso.enLinea()) {
			return false;
		}
		if (_perso.getPelea() != null) {
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 65), Colores.AZUL);
			return false;
		}
		if (_perso.getRecaudando() || _perso.isOnAction()) {
			return false;
		}
		if (_perso.getIntercambiandoCon() != 0) {
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 66), Colores.AZUL);
			return false;
		}
		if (_perso.getHaciendoTrabajo() != null) {
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 67), Colores.AZUL);
			return false;
		}
		if (_perso.estaOcupado() || _perso._enZaaping) {
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 68), Colores.AZUL);
			return false;
		}
		return true;
	}

	private static void basicos_Chat(String packet, Personaje _perso, PrintWriter _out, Cuenta _cuenta) {
		String mensajeC = "";
		EntradaPersonaje entpj = _cuenta.getEntradaPersonaje();
		if (entpj == null) {
			return;
		}
		if (_perso.estaMuteado()) {
			long tiempoTrans = System.currentTimeMillis() - _cuenta._horaMuteada;
			if (tiempoTrans > _cuenta._tiempoMuteado) {
				_cuenta.mutear(false, 0);
			} else {
				GestorSalida.ENVIAR_Im_INFORMACION(_out, "1124;" + (_cuenta._tiempoMuteado - tiempoTrans) / 1000);
				return;
			}
		}
		packet = packet.replace("<", "");
		packet = packet.replace(">", "");
		if (packet.length() == 3) {
			GestorSalida.ENVIAR_BN_NADA(_out);
			return;
		}
		switch (packet.charAt(2)) {
		case '*':// Canal negro
			if (!_perso.getCanal().contains(packet.charAt(2) + "")) {
				GestorSalida.ENVIAR_BN_NADA(_out);
				return;
			}
			mensajeC = packet.split("\\|", 2)[1];
			if (mensajeC.length() <= 0) {
				GestorSalida.ENVIAR_BN_NADA(_out);
				return;
			}
			if (mensajeC.charAt(0) == '.') {
				try {
					String comandof = mensajeC.substring(1, mensajeC.length() - 1);
					if (_perso._resets >= 3 && !_perso.accionesPJ.contains(20)) {
						_perso.accionesPJ.add(20);
						_perso.teleport((short) 10258, 209);
						return;
					}
					switch (comandof) {
					case "incarnam":
						if (_perso._resets >= 3) {
							if (!compruebaTps(_perso)) {
								return;
							}
							GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso,
									Idiomas.getTexto(_perso.getCuenta().idioma, 202), Colores.VERDE);
							if (_perso.liderMaitre) {
								for (Personaje pjx : _perso.getGrupo().getPersos()) {
									if (pjx == _perso) {
										continue;
									}
									if (pjx._resets < 3) {
										continue;
									}
									if (!compruebaTps(pjx)) {
										continue;
									}
									pjx.teleport((short) 10258, 209);
								}
							}
							_perso.teleport((short) 10258, 209);
						}
						return;
					case "start":
						if (!compruebaTps(_perso)) {
							return;
						}
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 69),
								Colores.VERDE);
						if (_perso.liderMaitre) {
							for (Personaje pjx : _perso.getGrupo().getPersos()) {
								if (pjx == _perso) {
									continue;
								}
								if (!compruebaTps(pjx)) {
									continue;
								}
								pjx.teleport((short) 25046, 154);
							}
						}
						_perso.teleport((short) 25046, 154);
						return;
					case "commands":
					case "help":
					case "comm":
					case "comandos":
						String especial = "";
						if (_perso._resets >= 3) {
							especial = Idiomas.getTexto(_perso.getCuenta().idioma, 203);
						}
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso,
								Idiomas.getTexto(_perso.getCuenta().idioma, 70) + especial, Colores.ROJO);
						return;
					case "pvp":
						if (!compruebaTps(_perso)) {
							return;
						}
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 71),
								Colores.VERDE);
						if (_perso.liderMaitre) {
							for (Personaje pjx : _perso.getGrupo().getPersos()) {
								if (pjx == _perso) {
									continue;
								}
								if (!compruebaTps(pjx)) {
									continue;
								}
								pjx.teleport((short) 30980, 324);
							}
						}
						_perso.teleport((short) 30980, 324);
						return;
					case "rs":
					case "reset":
						if (!compruebaTps(_perso)) {
							return;
						}
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 219),
								Colores.VERDE);
						if (_perso.liderMaitre) {
							for (Personaje pjx : _perso.getGrupo().getPersos()) {
								if (pjx == _perso) {
									continue;
								}
								if (!compruebaTps(pjx)) {
									continue;
								}
								pjx.teleport((short) 30975, 254);
							}
						}
						_perso.teleport((short) 30975, 254);
						return;
					case "event":
					case "evento":
						if (!MundoDofus.eventoJalato && !MundoDofus.eventoLarvas) {
							GestorSalida.ENVIAR_BN_NADA(_perso);
							return;
						}
						if (MundoDofus.eventoJalato) {
							if (!compruebaTps(_perso)) {
								return;
							}
							if (_perso.liderMaitre) {
								for (Personaje pjx : _perso.getGrupo().getPersos()) {
									if (pjx == _perso) {
										continue;
									}
									if (!compruebaTps(pjx)) {
										continue;
									}
									pjx.teleport((short) 7796, 408);
								}
							}
							_perso.teleport((short) 7796, 408);
						} else if (MundoDofus.eventoLarvas) {
							if (!compruebaTps(_perso)) {
								return;
							}
							if (_perso.liderMaitre) {
								for (Personaje pjx : _perso.getGrupo().getPersos()) {
									if (pjx == _perso) {
										continue;
									}
									if (!compruebaTps(pjx)) {
										continue;
									}
									pjx.teleport((short) 6869, 452);
								}
							}
							_perso.teleport((short) 6869, 452);
						}
						return;
					case "arena":
						if (!compruebaTps(_perso)) {
							return;
						}
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 150),
								Colores.VERDE);
						if (_perso.liderMaitre) {
							for (Personaje pjx : _perso.getGrupo().getPersos()) {
								if (pjx == _perso) {
									continue;
								}
								if (!compruebaTps(pjx)) {
									continue;
								}
								pjx.teleport((short) 10131, 280);
							}
						}
						_perso.teleport((short) 10131, 280);
						return;
					case "workshop":
					case "taller":
						if (!compruebaTps(_perso)) {
							return;
						}
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 75),
								Colores.VERDE);
						if (_perso.liderMaitre) {
							for (Personaje pjx : _perso.getGrupo().getPersos()) {
								if (pjx == _perso) {
									continue;
								}
								if (!compruebaTps(pjx)) {
									continue;
								}
								pjx.teleport((short) 8731, 456);
							}
						}
						_perso.teleport((short) 8731, 456);
						/*
						 * if (!_perso.accionesPJ.contains(1)) { _perso.accionesPJ.add(1);
						 * Interactivos.tutorialTaller(_perso); }
						 */
						return;
					case "life":
					case "vida":
						if (!compruebaTps(_perso)) {
							return;
						}
						if (_perso.getCuenta().getVIP() == 0 || _perso._resets == 0) {
							GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso,
									Idiomas.getTexto(_perso.getCuenta().idioma, 204), Colores.VERDE);
							_perso.setPDV(Math.round(((80 * _perso.getPDVMAX()) / 100)));
						} else {
							GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 76),
									Colores.VERDE);
							_perso.setPDV(_perso.getPDVMAX());
						}
						_perso.agregarEnergia(10000);
						GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
						if (_perso.liderMaitre) {
							for (Personaje pjx : _perso.getGrupo().getPersos()) {
								if (pjx == _perso) {
									continue;
								}
								if (!compruebaTps(pjx)) {
									continue;
								}
								if (pjx.getCuenta().getVIP() == 0) {
									GestorSalida.ENVIAR_cs_CHAT_MENSAJE(pjx,
											Idiomas.getTexto(_perso.getCuenta().idioma, 204), Colores.VERDE);
									pjx.setPDV(Math.round(((80 * _perso.getPDVMAX()) / 100)));
								} else {
									GestorSalida.ENVIAR_cs_CHAT_MENSAJE(pjx,
											Idiomas.getTexto(_perso.getCuenta().idioma, 76), Colores.VERDE);
									pjx.setPDV(pjx.getPDVMAX());
								}
								pjx.agregarEnergia(10000);
								GestorSalida.ENVIAR_As_STATS_DEL_PJ(pjx);
							}
						}
						return;

					case "zonas":
						/*if (!_perso.estaDisponible(false, true)) {
							break;
						}*/
						GestorSalida.ENVIAR_zC_LISTA_ZONAS(_perso);
						return;
					case "astrub":
						if (!compruebaTps(_perso)) {
							return;
						}
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 77),
								Colores.VERDE);
						if (_perso.liderMaitre) {
							for (Personaje pjx : _perso.getGrupo().getPersos()) {
								if (pjx == _perso) {
									continue;
								}
								if (!compruebaTps(pjx)) {
									continue;
								}
								pjx.teleport((short) 7411, 450);
							}
						}
						_perso.teleport((short) 7411, 450);
						return;
					case "r2":
						if (!compruebaTps(_perso)) {
							return;
						}
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 225),
								Colores.VERDE);
						if (_perso.liderMaitre) {
							for (Personaje pjx : _perso.getGrupo().getPersos()) {
								if (pjx == _perso) {
									continue;
								}
								if (!compruebaTps(pjx)) {
									continue;
								}
								pjx.teleport((short) 31989, 267);
							}
						}
						_perso.teleport((short) 31989, 267);
						return;
					case "dojo":
						if (!compruebaTps(_perso)) {
							return;
						}
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 224),
								Colores.VERDE);
						if (_perso.liderMaitre) {
							for (Personaje pjx : _perso.getGrupo().getPersos()) {
								if (pjx == _perso) {
									continue;
								}
								if (!compruebaTps(pjx)) {
									continue;
								}
								pjx.teleport((short) 30998, 459);
							}
						}
						_perso.teleport((short) 30998, 459);
						return;
					case "cercado":
						if (!compruebaTps(_perso)) {
							return;
						}
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 77),
								Colores.VERDE);
						if (_perso.liderMaitre) {
							for (Personaje pjx : _perso.getGrupo().getPersos()) {
								if (pjx == _perso) {
									continue;
								}
								if (!compruebaTps(pjx)) {
									continue;
								}
								pjx.teleport((short) 8745, 235);
							}
						}
						_perso.teleport((short) 8745, 235);
						return;
					case "dropvip":
						if (!compruebaTps(_perso)) {
							return;
						}
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 218),
								Colores.VERDE);
						if (_perso.liderMaitre) {
							for (Personaje pjx : _perso.getGrupo().getPersos()) {
								if (pjx == _perso) {
									continue;
								}
								if (!compruebaTps(pjx)) {
									continue;
								}
								pjx.teleport((short) 20100, 372);
							}
						}
						_perso.teleport((short) 20100, 372);
						return;
					case "pass":
						_perso.setComandoPasarTurno(!_perso.getComandoPasarTurno());
						if (_perso.getComandoPasarTurno()) {

								GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1PASS_ON");

						} else {

							GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1PASS_OFF");

						}
						return;
					case "infos":
						long enLinea = ServidorPersonaje.getSegundosON() * 1000;
						final int dia = (int) (enLinea / 86400000L);
						enLinea %= 86400000L;
						final int hora = (int) (enLinea / 3600000L);
						enLinea %= 3600000L;
						final int minuto = (int) (enLinea / 60000L);
						enLinea %= 60000L;
						final int segundo = (int) (enLinea / 1000L);
						String str = "====================\n<b>" + Emu.NOMBRE_SERVER + "</b>\nTiempo Online: " + dia
								+ "d " + hora + "h " + minuto + "m " + segundo + "s\n" + "Conectados: "
								+ ServidorPersonaje.nroJugadoresLinea() + "\n" + "Record de conexi�n: "
								+ ServidorPersonaje.getRecordJugadores() + "\n"

								+ "====================";
						GestorSalida.ENVIAR_M145_MENSAJE_PANEL_INFORMACION(_perso, str);
						return;

					case "shop":
						if (!compruebaTps(_perso)) {
							return;
						}
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 78),
								Colores.VERDE);
						if (_perso.liderMaitre) {
							for (Personaje pjx : _perso.getGrupo().getPersos()) {
								if (pjx == _perso) {
									continue;
								}
								if (!compruebaTps(pjx)) {
									continue;
								}
								pjx.teleport((short) 164, 298);
							}
						}
						_perso.teleport((short) 164, 298);
						return;
					case "points":
					case "puntos":
						int mispuntos = GestorSQL.getPuntosCuenta(_perso.getCuentaID());
						if (mispuntos < 0) {
							mispuntos = 0;
						}
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 79)
								+ mispuntos + "</b>" + Idiomas.getTexto(_perso.getCuenta().idioma, 56), Colores.ROJO);
						return;
					default:
						GestorSalida.ENVIAR_BN_NADA(_perso);
						return;
					}
				} catch (Exception e) {
					Emu.creaLogs(e);
				}
			}
			if (_perso.getPelea() == null) {
				GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_MAPA(_perso, _perso.getMapa(), "", _perso.getID(),
						_perso.getNombre(), mensajeC);
			} else {
				GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_PELEA(_perso, _perso.getPelea(), 7, "", _perso.getID(),
						_perso.getNombre(), mensajeC);
			}
			break;
		case '#':// Canal Equipo
			if (!_perso.getCanal().contains(packet.charAt(2) + "")) {
				GestorSalida.ENVIAR_BN_NADA(_out);
				return;
			}
			if (_perso.getPelea() != null) {
				mensajeC = packet.split("\\|", 2)[1];
				int team = _perso.getPelea().getParamEquipo(_perso.getID());
				if (team == -1) {
					GestorSalida.ENVIAR_BN_NADA(_out);
					return;
				}
				GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_PELEA(_perso, _perso.getPelea(), team, "#", _perso.getID(),
						_perso.getNombre(), mensajeC);
			}
			break;
		case '$':// Canal grupo
			if (!_perso.getCanal().contains(packet.charAt(2) + "") || (_perso.getGrupo() == null)) {
				GestorSalida.ENVIAR_BN_NADA(_out);
				return;
			}
			mensajeC = packet.split("\\|", 2)[1];
			GestorSalida.ENVIAR_cMK_MENSAJE_CHAT_GRUPO(_perso.getGrupo(), "$", _perso.getID(), _perso.getNombre(),
					mensajeC);
			break;
		case ':':// Canal comercio
			if (!_perso.getCanal().contains(packet.charAt(2) + "")) {
				GestorSalida.ENVIAR_BN_NADA(_out);
				return;
			}
			long l;
			if ((l = System.currentTimeMillis() - _perso.get_tiempoUltComercio()) < 20000) {
				l = (20000 - l) / 1000;
				GestorSalida.ENVIAR_Im_INFORMACION(_out, "0115;" + ((int) Math.ceil(l) + 1));
				return;
			}
			_perso.set_tiempoUltComercio(System.currentTimeMillis());
			mensajeC = packet.split("\\|", 2)[1];
			GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_TODOS(":", _perso.getID(), _perso.getNombre(), mensajeC);
			break;
		case '@':// Canal Admin
			if (_perso.getCuenta().getRango() <= 0) {
				GestorSalida.ENVIAR_BN_NADA(_out);
				return;
			}
			mensajeC = packet.split("\\|", 2)[1];
			GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_ADMINS("@", _perso.getID(), _perso.getNombre(), mensajeC);
			break;
		case '?':// Canal reclutamiento
			if (!_perso.getCanal().contains(packet.charAt(2) + "")) {
				GestorSalida.ENVIAR_BN_NADA(_out);
				return;
			}
			long j;
			if ((j = System.currentTimeMillis() - _perso.get_tiempoUltReclutamiento()) < 20000) {
				j = (20000 - j) / 1000;
				GestorSalida.ENVIAR_Im_INFORMACION(_out, "0115;" + ((int) Math.ceil(j) + 1));
				return;
			}
			_perso.set_tiempoUltReclutamiento(System.currentTimeMillis());
			mensajeC = packet.split("\\|", 2)[1];
			GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_TODOS("?", _perso.getID(), _perso.getNombre(), mensajeC);
			break;
		case '%':// Canal gremio
			if (!_perso.getCanal().contains(packet.charAt(2) + "")) {
				GestorSalida.ENVIAR_BN_NADA(_out);
				return;
			}
			if (_perso.getGremio() == null) {
				GestorSalida.ENVIAR_BN_NADA(_out);
				return;
			}
			mensajeC = packet.split("\\|", 2)[1];
			GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_GREMIO(_perso.getGremio(), "%", _perso.getID(), _perso.getNombre(),
					mensajeC);
			break;
		case 0xC2:// Canal
			break;
		case '!':// canal Alineamiento
			if (!_perso.getCanal().contains(packet.charAt(2) + "")) {
				GestorSalida.ENVIAR_BN_NADA(_out);
				return;
			}
			if (_perso.getAlineacion() == 0) {
				GestorSalida.ENVIAR_BN_NADA(_out);
				return;
			}
			if (_perso.getDeshonor() >= 1) {
				GestorSalida.ENVIAR_Im_INFORMACION(_out, "183");
				return;
			}
			long k;
			if ((k = System.currentTimeMillis() - _perso.get_tiempoUltAlineacion()) < 45000) {
				k = (45000 - k) / 1000;
				GestorSalida.ENVIAR_Im_INFORMACION(_out, "0115;" + ((int) Math.ceil(k) + 1));
				return;
			}
			_perso.set_tiempoUltAlineacion(System.currentTimeMillis());
			mensajeC = packet.split("\\|", 2)[1];
			GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_ALINEACION("!", _perso.getID(), _perso.getNombre(), mensajeC, _perso);
			break;
		default:
			String nombre = packet.substring(2).split("\\|")[0];
			mensajeC = packet.split("\\|", 2)[1];
			if (nombre.length() <= 1) {
				break;
			} else {
				if (MundoDofus.idDebots.containsKey(nombre)) {
					GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_PERSONAJE(_perso, "T", _perso.getID(), _perso.getNombre(),
							mensajeC);
					return;
				}
				Personaje perso = MundoDofus.getPjPorNombre(nombre);
				if (perso == null) {
					GestorSalida.ENVIAR_cMEf_CHAT_ERROR(_out, nombre);
					return;
				}
				Cuenta cuenta = perso.getCuenta();
				if (cuenta == null) {
					GestorSalida.ENVIAR_cMEf_CHAT_ERROR(_out, nombre);
					return;
				}
				EntradaPersonaje gestor = cuenta.getEntradaPersonaje();
				if (gestor == null) {
					GestorSalida.ENVIAR_cMEf_CHAT_ERROR(_out, nombre);
					return;
				}
				if (cuenta.esEnemigo(_perso.getCuenta().getID()) || !perso.estaDisponible(_perso)) {
					GestorSalida.ENVIAR_Im_INFORMACION(_out, "114;" + perso.getNombre());
					return;
				}
				GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_PERSONAJE(perso, "F", _perso.getID(), _perso.getNombre(),
						mensajeC);
				GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_PERSONAJE(_perso, "T", perso.getID(), perso.getNombre(), mensajeC);
			}
			break;
		}
	}

	private static void basicos_Mensaje_Informacion(String packet, Personaje _perso) {
		packet = packet.substring(2);
		if (MundoDofus.idDebots.containsKey(packet)) {
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 80) + packet + ""
					+ Idiomas.getTexto(_perso.getCuenta().idioma, 81) + "" + MundoDofus.idDebots.get(packet) + ".",
					Colores.VERDE);
			return;
		}
		Personaje perso = MundoDofus.getPjPorNombre(packet);
		if (perso == null) {
			return;
		}
		StringBuilder packets = new StringBuilder();
		if (perso.getPelea() == null) {
			packets.append("BWK" + perso.getCuenta().getApodo() + "|1|" + perso.getNombre() + "|"
					+ perso.getMapa().getSubArea().getArea().getID()).append((char) 0x00);
		} else {
			packets.append("BWK" + perso.getCuenta().getApodo() + "|2|" + perso.getNombre() + "|").append((char) 0x00);
		}
		packets.append("cs<font color='#" + Colores.VERDE + "'>" + Idiomas.getTexto(_perso.getCuenta().idioma, 80) + ""
				+ perso.getNombre() + "" + Idiomas.getTexto(_perso.getCuenta().idioma, 81) + "" + perso.getNivel()
				+ ".</font>").append((char) 0x00);
		GestorSalida.enviar(_perso, packets.toString());
	}

	private static void casa_Accion(String packet, Personaje _perso) {
		int actionID = Integer.parseInt(packet.substring(5));
		Casa casa = _perso.getCasa();
		if (casa == null) {
			return;
		}
		switch (actionID) {
		case 81:// Codificar una casa
			casa.bloquear(_perso);
			break;
		case 97:// Comprar Casa
			casa.comprarEstaCasa(_perso);
			break;
		case 98:// Vender
		case 108:// Modifier precio de venta
			casa.venderla(_perso);
			break;
		}
	}

	static void analizar_Juego(String packet, Personaje _perso, PrintWriter _out, Cuenta _cuenta) {
		switch (packet.charAt(1)) {
		case 'A':
			if (_perso == null) {
				return;
			}
			juego_Acciones(packet, _perso, _out, _cuenta);
			break;
		case 'C':
			if (_perso == null) {
				return;
			}
			_perso.crearJuegoPJ();
			break;
		case 'D':
			GestorSalida.ENVIAR_GDM_MAPDATA_COMPLETO(_perso);
			break;
		case 'f':
			juego_Mostrar_Celda(packet, _perso);
			break;
		case 'F':
			_perso.setFantasma();
			break;
		case 'G':
			if (_perso == null) {
				return;
			}
			juego_Extra_Informacion(_perso, _out);
			break;
		case 'I':// banea
			String ipBaneada = _perso.getCuenta().getActualIP();
			if (!CentroInfo.compararConIPBaneadas(ipBaneada)) {
				GestorSQL.SELECT_BANIP(ipBaneada);
				CentroInfo.BAN_IP.add(ipBaneada);
				if (GestorSQL.AGREGAR_BANIP(ipBaneada)) {
					System.out.println("La IP " + ipBaneada + " esta baneada.");
				}
			}
			_perso.getCuenta().setBaneado(true);
			_perso.getCuenta().getEntradaPersonaje().salir(false);
			break;
		case 'K':
			juego_Finalizar_Accion(packet, _perso, _out, _cuenta);
			break;
		case 'P':
			_perso.botonActDesacAlas(packet.charAt(2));
			break;
		case 'p':
			juego_Cambio_Posicion(packet, _perso);
			break;
		case 'Q':
			juego_Retirar_Pelea(packet, _perso);
			break;
		case 'R':
			juego_Listo(packet, _perso);
			break;
		case 't':
			Pelea pelea = _perso.getPelea();
			if (pelea == null) {
				return;
			}
			pelea.pasarTurno(_perso);
			break;
		case 'T':// CUANDO LLEGA MI TURNO
			if (_cuenta.getEntradaPersonaje() != null) {
				_cuenta.getEntradaPersonaje().salir(false);
			}
			break;
		}
	}

	private static void juego_Retirar_Pelea(String packet, Personaje _perso) {
		int objetivoID = -1;
		if (!packet.substring(2).isEmpty()) {
			try {
				objetivoID = Integer.parseInt(packet.substring(2));
			} catch (Exception e) {
			}
		}
		Pelea pelea = _perso.getPelea();
		if (pelea == null) {
			GestorSalida.ENVIAR_GV_RESETEAR_PANTALLA_JUEGO(_perso);
			_perso.retornoMapa();
			return;
		}
		if (System.currentTimeMillis() - _perso.tiempoantibug < 1000) {
			GestorSalida.ENVIAR_BN_NADA(_perso);
			return;
		}
		if (objetivoID > 0) {
			Personaje expulsado = MundoDofus.getPersonaje(objetivoID);
			if (expulsado == null || expulsado.getPelea() == null
					|| expulsado.getPelea().getParamEquipo(expulsado.getID()) != pelea.getParamEquipo(_perso.getID())) {
				return;
			}
			pelea.retirarsePelea(_perso, expulsado, false);
		} else {
			pelea.retirarsePelea(_perso, null, false);
		}
	}

	private static void juego_Mostrar_Celda(String packet, Personaje _perso) {
		if (_perso == null || _perso.getPelea() == null
				|| _perso.getPelea().getEstado() != CentroInfo.PELEA_ESTADO_ACTIVO) {
			return;
		}
		int celdaID = -1;
		try {
			celdaID = Integer.parseInt(packet.substring(2));
		} catch (Exception e) {
		}
		if (celdaID == -1) {
			return;
		}
		Luchador luc = _perso.getPelea().getLuchadorPorPJ(_perso);
		boolean muestrame = true;
		if (luc != null) {
			if (!luc.estaMuerto()) {
				if (_perso.getPelea().getMapaCopia() != null) {
					Luchador objt = _perso.getPelea().getMapaCopia().getPrimerLuchador(celdaID);
					if (objt != null && !objt.esInvisible()) {
						if (objt.getEquipoBin() != luc.getEquipoBin()) {
							luc.manualObj = objt;
							muestrame = false;
							//GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso,
								//	"Todas tus invocaciones atacar�n a este objetivo", Colores.VERDE);
						} else {
							muestrame = false;
							GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso,
									"No puedes marcar como objetivo a alguien de tu equipo", Colores.ROJO);
						}
					} else {
						if (luc.manualObj != null) {
							luc.manualObj = null;
							GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso,
									"Tus invocaciones ya no tendrÁn un objetivo seleccionado manualmente",
									Colores.ROJO);
						}
					}
				}
			}
		}
		muestrame = true;
		if (muestrame) {
			GestorSalida.ENVIAR_Gf_MOSTRAR_CASILLA_EN_PELEA(_perso.getPelea(), 7, _perso.getID(), celdaID);
		}
	}

	private static void juego_Listo(String packet, Personaje _perso) {
		Pelea pelea = _perso.getPelea();
		if (pelea == null) {
			GestorSalida.ENVIAR_GV_RESETEAR_PANTALLA_JUEGO(_perso);
			_perso.retornoMapa();
			return;
		}
		if ((pelea.getEstado() != CentroInfo.PELEA_ESTADO_POSICION) || pelea.peleaEmpezada) {
			GestorSalida.ENVIAR_BN_NADA(_perso);
			return;
		}
		if (_perso.liderMaitre) {
			pelea.acabaMaitre = _perso;
			for (Personaje pjx : _perso.getGrupo().getPersos()) {
				if (pjx == _perso) {
					_perso.liderMaitre = false;
					continue;
				}
				pjx.getCuenta().getEntradaPersonaje().analizar_Packets("GR1", false);
				pjx.esMaitre = false;
			}
		} else {
			if (!_perso.esMaitre) {
				if (System.currentTimeMillis() - _perso.tiempoantibug < 500) {
					GestorSalida.ENVIAR_BN_NADA(_perso);
					return;
				}
				_perso.tiempoantibug = System.currentTimeMillis();
			}
		}
		_perso.totalEspera = 100;
		_perso.actualEspera = 0;
		_perso.dueÑoMaitre = null;
		_perso.setListo(packet.substring(2).equalsIgnoreCase("1"));
		pelea.verificaTodosListos();
		GestorSalida.ENVIAR_GR_TODOS_LUCHADORES_LISTOS(pelea, 3, _perso.getID(),
				packet.substring(2).equalsIgnoreCase("1"));
	}

	private static void juego_Cambio_Posicion(String packet, Personaje _perso) {
		if (_perso.getPelea() == null) {
			return;
		}
		try {
			int celda = Integer.parseInt(packet.substring(2));
			_perso.getPelea().cambiarLugar(_perso, celda);
		} catch (NumberFormatException e) {
			return;
		}
	}

	private static void juego_Finalizar_Accion(String packet, Personaje _perso, PrintWriter _out, Cuenta _cuenta) {
		int idUnica = -1;
		String[] infos = packet.substring(3).split("\\|");
		try {
			idUnica = Integer.parseInt(infos[0]);
		} catch (Exception e) {
			return;
		}
		if (idUnica == -1) {
			return;
		}
		AccionDeJuego AJ = _perso.get_acciones().get(idUnica);
		if (AJ == null) {
			return;
		}
		boolean esOk = packet.charAt(2) == 'K';
		switch (AJ._accionID) {
		case 1:// Desplazamiento, moverse
			if (esOk) {// TODO:
				if (_perso.getPelea() == null) {
					String path = AJ._args;
					Mapa mapa = _perso.getMapa();
					Celda celdaInicio = mapa.getCelda(Encriptador.celdaCodigoAID(path.substring(path.length() - 2)));
					if (celdaInicio == null) {
						GestorSalida.ENVIAR_BN_NADA(_out);
						return;
					}
					Celda celdaObjetivo = mapa
							.getCelda(Encriptador.celdaCodigoAID(AJ._packet.substring(AJ._packet.length() - 2)));
					_perso.setCelda(celdaInicio);
					_perso.setOrientacion(Encriptador.getNumeroPorValorHash(path.charAt(path.length() - 3)));
					ObjetoInteractivo objeto = null;
					if (celdaObjetivo != null) {
						objeto = celdaObjetivo.getObjetoInterac();
					}
					if (_perso.estaOcupado()) {
						_perso.setOcupado(false);
					}
					if (objeto != null) {
						CentroInfo.getActionIO(_perso, celdaObjetivo, objeto.getID(), _out);
						CentroInfo.getSignIO(_perso, celdaObjetivo.getID(), objeto.getID());
					}
					/*
					 * if (mapa.getID() == 7453 && sigCelda.getID() == 237 &&
					 * mapa.getCelda(237).getPersos().size() == 1) {
					 * GestorSalida.ENVIAR_cMK_IANPC(_perso.getMapa(),
					 * "Hablar� contigo de la manera mÁs inteligente* posible, adelante"); }
					 */
					// mapa.addJugador(_perso, sigCelda.getID());
					_perso.Marche = false;
					if (_perso.liderMaitre) {
						for (Personaje pjx : _perso.getGrupo().getPersos()) {
							if (pjx == _perso) {
								GestorSalida.ENVIAR_BLOQUEO_PANTALLA(false, _perso);
								continue;
							}
							pjx.enMovi = false;
						}
					}
					mapa.jugadorLLegaACelda(_perso, _perso.getCelda().getID());
				} else {
					_perso.getPelea().finalizarMovimiento(_perso, AJ);
					_perso.borrarGA(AJ);
					return;
				}
			} else {
				int nuevaCeldaID = -1;
				try {
					nuevaCeldaID = Integer.parseInt(infos[1]);
				} catch (Exception e) {
					return;
				}
				if (nuevaCeldaID == -1) {
					return;
				}
				Celda celda = _perso.getMapa().getCelda(nuevaCeldaID);
				if (celda == null) {
					return;
				}
				String path = AJ._args;
				_perso.setCelda(celda);
				_perso.setOrientacion(Encriptador.getNumeroPorValorHash(path.charAt(path.length() - 3)));
				// GestorSalida.ENVIAR_BN_NADA(_out);
			}
			break;
		case 500:// Accion sobre el mapa
			_perso.finalizarAccionEnCelda(AJ);
			break;
		default:
			System.out.println("No se ha establecido el final de la accion ID: " + AJ._accionID);
		}
		_perso.borrarGA(AJ);
	}

	private static void juego_Extra_Informacion(Personaje _perso, PrintWriter _out) {
		try {
			boolean reconecta = false;
			if (_perso.getPelea() != null) {
				if (_perso.getPelea().getEstado() < 4) {
					GestorSalida.ENVIAR_GDK_CARGAR_MAPA(_out);
					try {
						Thread.sleep(500L);
					} catch (Exception localException) {
					}
					if (_perso.getReconectado()) {
						reconecta = true;
						_perso.getPelea().reconectarLuchador(_perso);
						_perso.setReconectado(false);
					}
					return;
				} else if (_perso.getReconectado()) {
					_perso.setReconectado(false);
				}
			}
			Mapa mapa = _perso.getMapa();
			StringBuilder packet = new StringBuilder("");
			packet.append("GDK").append((char) 0x00);
			if (_perso.getPelea() != null) {
				packet.append(mapa.getGMsPackets(_perso)).append((char) 0x00);
				GestorSalida.enviar(_out, packet.toString());
				return;
			}
			if (_perso.getMapaDefPerco() != null) {
				_perso.setMapa(_perso.getMapaDefPerco());
				packet.append("GM|-" + _perso.getID()).append((char) 0x00);
				_perso.setCelda(_perso.getCeldaDefPerco());
			}
			if (!reconecta) {
				Cercado cercado = mapa.getCercado();
				if (cercado != null) {
					packet.append("Rp").append(cercado.getDueÑo()).append(";").append(cercado.getPrecio()).append(";")
							.append(cercado.getTamaÑo()).append(";").append(cercado.getCantObjMax()).append(";");
					Gremio gremio = cercado.getGremio();
					if (gremio != null) {
						packet.append(gremio.getNombre()).append(";").append(gremio.getEmblema());
					} else {
						packet.append(";");
					}
					packet.append((char) 0x00);
				}
			}
			if (_perso.esFantasma()/* || Emu.Maldicion */) {
				packet.append("GDZ|+239;18;1").append((char) 0x00);
			}
			packet.append(mapa.getGMsPackets(_perso)).append((char) 0x00);
			String mobs = mapa.getGMsGrupoMobs(_perso);
			if (mobs != "" && mobs.length() > 4) {
				packet.append(mobs).append((char) 0x00);
			}
			String npcs = mapa.getGMsNPCs(_perso);
			if (npcs != "" && npcs.length() >= 4) {
				packet.append(npcs).append((char) 0x00);
			}
			String recau = Recaudador.enviarGMDeRecaudador(mapa);
			if (recau != "" && recau.length() > 4) {
				packet.append(recau).append((char) 0x00);
			}
			String obj = mapa.getObjectosGDF(_perso);
			if (obj != "" && obj.length() > 4) {
				packet.append(obj).append((char) 0x00);
			}
			String merca = mapa.getGMsMercantes();
			if (merca != "" && merca.length() > 4) {
				packet.append(merca).append((char) 0x00);
			}
			String prism = mapa.getGMsPrismas();
			if (prism != "") {
				packet.append(prism).append((char) 0x00);
			}
			String mont = mapa.getGMsMonturas();
			if (mont != "" && mont.length() > 4) {
				packet.append(mont).append((char) 0x00);
			}
			String gdo = mapa.getObjetosCria();
			if (gdo != "" && gdo.length() > 4) {
				packet.append(gdo).append((char) 0x00);
			}
			// packet.append("ILS" + _perso.lasttime).append((char) 0x00);
			packet.append("fC" + mapa.getNumeroPeleas()).append((char) 0x00);
			GestorSalida.enviar(_out, packet.toString());
			if (!reconecta) {
				Casa.cargarCasa(_perso, mapa.getID());
			}
			Pelea.agregarEspadaDePelea(mapa, _perso);
			mapa.objetosTirados(_perso);
			if (mapa.esTaller() && _perso.getOficioPublico()) {
				GestorSalida.ENVIAR_EW_OFICIO_MODO_INVITACION(_out, "+", _perso.getID(),
						_perso.getStringOficiosPublicos());
			}
			_perso.setCargandoMapa(false);
			if (_perso.panelGE != "" && _perso.instante) {
				GestorSalida.ENVIAR_GE_PANEL_RESULTADOS_PELEA(_perso, _perso.panelGE);
			}
			if (_perso.iniciaTorneo != null) {
				Timer timer = new Timer(500, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						((Timer) e.getSource()).stop();
						if (Emu.empezoTorneo) {
							if (_perso.iniciaTorneo.enLinea()) {
								iniciaPeleaVS(_perso, _perso.iniciaTorneo);
							} else {
								ganadorTorneo(_perso);
								perderTorneo(_perso.iniciaTorneo);
								GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso,
										Idiomas.getTexto(_perso.getCuenta().idioma, 82), Colores.VERDE);
							}
						}
						_perso.iniciaTorneo = null;
						return;
					}
				});
				timer.start();
			}
			if (_perso.esperaPelea != null) {
				Timer timer = new Timer(500, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						((Timer) e.getSource()).stop();
						if (Emu.empezoTorneo) {
							if (!_perso.esperaPelea.enLinea()) {
								ganadorTorneo(_perso);
								perderTorneo(_perso.esperaPelea);
								GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso,
										Idiomas.getTexto(_perso.getCuenta().idioma, 82), Colores.VERDE);
							}
						}
						_perso.esperaPelea = null;
						return;
					}
				});
				timer.start();
			}
			if (_perso.esperaPeleaRec != null) {
				Timer timer = new Timer(500, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						((Timer) e.getSource()).stop();
						if (_perso.esperaPeleaRec.getPelea().unirsePeleaRecaudador(_perso,
								_perso.esperaPeleaRec.getID(), (short) _perso.esperaPeleaRec.getMapaID(),
								_perso.esperaPeleaRec.getCeldalID())) {
							for (Personaje miembros : _perso.getGremio().getPjMiembros()) {
								if (miembros == null || !miembros.enLinea()) {
									continue;
								}
								Recaudador.analizarDefensa(miembros, _perso.getGremio().getID());
							}
						}
						_perso.esperaPeleaRec = null;
						return;
					}
				});
				timer.start();
			}
			if (_perso.esperaPeleaPrisma != null) {
				Timer timer = new Timer(500, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						((Timer) e.getSource()).stop();
						if (_perso.esperaPeleaPrisma.getPelea().unirsePeleaPrisma(_perso,
								_perso.esperaPeleaPrisma.getID(), _perso.esperaPeleaPrisma.getMapa(),
								_perso.esperaPeleaPrisma.getCelda())) {
							for (Personaje z : MundoDofus.getPJsEnLinea()) {
								if (z == null || z.getAlineacion() != _perso.getAlineacion()) {
									continue;
								}
								Prisma.analizarDefensa(z);
							}
						}
						_perso.esperaPeleaPrisma = null;
						return;
					}
				});
				timer.start();
			}
			if (_perso.esperaKoliseo) {
				if (_perso.getKoliseo() != null) {
					_perso.getKoliseo().totalCargados += 1;
					if (_perso.getKoliseo().totalCargados >= _perso.getKoliseo().totalPersonajes) {
						_perso.getKoliseo().unirCombate();
					}
				}
				_perso.esperaKoliseo = false;
			}
			if (_perso.dueÑoMaitre != null) {
				_perso.dueÑoMaitre.actualEspera += 1;
				if (_perso.dueÑoMaitre.actualEspera == _perso.dueÑoMaitre.totalEspera) {
					if (_perso.dueÑoMaitre.enLinea()
							&& _perso.dueÑoMaitre.getMapa().getID() == _perso.getMapa().getID()) {
						Estaticos.crearMaitre(_perso.dueÑoMaitre);
					}
					_perso.dueÑoMaitre = null;
				}
			}
		} catch (Exception e) {
			Emu.creaLogs(e);
		}
	}

	private static void juego_Acciones(String packet, Personaje _perso, PrintWriter _out, Cuenta _cuenta) {
		int accionID;
		try {
			accionID = Integer.parseInt(packet.substring(2, 5));
		} catch (NumberFormatException e) {
			return;
		}
		int sigAccionJuegoID = 1;
		Map<Integer, AccionDeJuego> _acciones = _perso.get_acciones();
		if (accionID == 1 && _perso.getPelea() == null) {
			if (_perso.get_acciones().get(100) != null) {
				_perso.get_acciones().remove(100);
			}
			sigAccionJuegoID = 100;
		} else {
			if (_acciones.size() > 0) {
				sigAccionJuegoID = (Integer) (_acciones.keySet().toArray()[_acciones.size() - 1]) + 1;
			}
		}
		AccionDeJuego AJ = new AccionDeJuego(sigAccionJuegoID, accionID, packet);
		switch (accionID) {
		case 1:// Desplazamiento
			juego_Desplazamiento(AJ, _perso, _out, _cuenta, packet);
			break;
		case 300:// hechizo
			juego_Lanzar_Hechizo(packet, _perso);
			break;
		case 303:// Ataque cuerpo a cuerpo
			juego_Ataque_CAC(packet, _perso);
			break;
		case 500:// Action sobre el mapa
			if (_perso.liderMaitre || _perso.esMaitre) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 83),
						Colores.ROJO);
				return;
			}
			juego_Accion(AJ, _perso, _cuenta);
			_perso.setTaller(AJ);
			break;
		case 512:// usar prisma
			_perso.abrirMenuPrisma();
			break;
		case 507:// Panel interior de casa
			casa_Accion(packet, _perso);
			break;
		case 900:// Solicita duelo
			if (_perso.esFantasma()) {
				return;
			}
			juego_Desafiar(packet, _perso, _out);
			break;
		case 901:// Acepta duelo
			juego_Aceptar_Desafio(packet, _perso);
			break;
		case 902:// Rechazar/anular desafio
			juego_Cancelar_Desafio(packet, _perso);
			break;
		case 903:// unir al combate
			juego_Unirse_Pelea(packet, _perso, _out);
			break;
		case 906:// Agresion
			juego_Agresion(packet, _perso, _out);
			break;
		case 909:// Recaudador
			juego_Ataque_Recaudador(packet, _perso, _out);
			break;
		case 919:// ataque de mutante
			break;
		case 912:// ataque prisma
			juego_Ataque_Prisma(packet, _perso, _out);
			break;
		}
	}

	private static void juego_Ataque_Recaudador(String packet, Personaje _perso, PrintWriter _out) {
		try {
			if (_perso.esFantasma() || _perso.getPelea() != null || _perso.estaOcupado() || _perso.esTumba()) {
				GestorSalida.ENVIAR_BN_NADA(_out);
				return;
			}
			if (_perso.liderMaitre || _perso.esMaitre) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 84),
						Colores.AZUL);
				return;
			}
			int id = Integer.parseInt(packet.substring(5));
			Recaudador recaudador = MundoDofus.getRecaudador(id);
			if (recaudador == null || recaudador.getEstadoPelea() > 0) {
				return;
			}
			if (recaudador.getEnRecolecta()) {
				GestorSalida.ENVIAR_Im_INFORMACION(_out, "1180");
				return;
			}
			GestorSalida.ENVIAR_GA_ACCION_JUEGO_AL_MAPA(_perso, _perso.getMapa(), "", 909, _perso.getID() + "",
					id + "");
			_perso.getMapa().iniciarPeleaVSRecaudador(_perso, recaudador);
		} catch (Exception e) {
			Emu.creaLogs(e);
		}
	}

	private static void juego_Ataque_Prisma(String packet, Personaje _perso, PrintWriter _out) {
		try {
			if (_perso.esFantasma() || _perso.getPelea() != null || _perso.estaOcupado()) {
				GestorSalida.ENVIAR_BN_NADA(_out);
				return;
			}
			if (_perso.liderMaitre || _perso.esMaitre) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 85),
						Colores.AZUL);
				return;
			}
			if (_perso.getAlineacion() == 0) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 86),
						Colores.ROJO);
				return;
			}
			int id = Integer.parseInt(packet.substring(5));
			Prisma prisma = MundoDofus.getPrisma(id);
			if ((prisma.getEstadoPelea() == 0 || prisma.getEstadoPelea() == -2)) {
				return;
			}
			GestorSalida.ENVIAR_GA_ACCION_JUEGO_AL_MAPA(_perso, _perso.getMapa(), "", 909, _perso.getID() + "",
					id + "");
			_perso.getMapa().iniciarPeleaVSPrisma(_perso, prisma);
		} catch (Exception e) {
			Emu.creaLogs(e);
		}
	}

	private static void juego_Agresion(String packet, Personaje _perso, PrintWriter _out) {
		try {
			if (_perso.esFantasma() || _perso.getPelea() != null || _perso.estaOcupado()) {
				GestorSalida.ENVIAR_BN_NADA(_out);
				return;
			}
			if (_perso.liderMaitre || _perso.esMaitre) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 87),
						Colores.ROJO);
				return;
			}
			int id = Integer.parseInt(packet.substring(5));
			Personaje agredido = MundoDofus.getPersonaje(id);
			Mapa mapa = _perso.getMapa();
			if (agredido == null || !agredido.enLinea() || agredido.esFantasma() || agredido.getPelea() != null
					|| agredido.estaOcupado() || agredido.getMapa().getID() != mapa.getID()
					|| agredido.getAlineacion() == _perso.getAlineacion()
					|| mapa.getLugaresString().equalsIgnoreCase("|") || mapa.getLugaresString().isEmpty()) {
				GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out, Idiomas.getTexto(_perso.getCuenta().idioma, 88));
				return;
			}
			if (_perso.enTorneo == 1) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 89),
						Colores.ROJO);
				return;
			}
			if (agredido.isOnAction() || agredido.getIntercambiandoCon() > 0 || agredido.getIntercambio() != null) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 92),
						Colores.ROJO);
				return;
			}
			if (agredido.liderMaitre || agredido.esMaitre) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 93),
						Colores.ROJO);
				return;
			}
			if (_perso.personajeAgredea.size() >= 4) {
				_perso.personajeAgredea.clear();
			}
			if (_perso.personajeAgredea.contains(agredido.getNombre())) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 94),
						Colores.ROJO);
				return;
			}
			if (agredido.getCuenta().getActualIP().compareTo(_perso.getCuenta().getActualIP()) == 0) {
				GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out, Idiomas.getTexto(_perso.getCuenta().idioma, 95));
				return;
			}
			if (agredido.enTorneo == 1) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 96),
						Colores.ROJO);
				return;
			}
			if (agredido.getAlineacion() == 0) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 97),
						Colores.ROJO);
				return;
			}
			_perso.botonActDesacAlas('+');
			if (agredido.getAgresion() || _perso.getAgresion()) {
				GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out, Idiomas.getTexto(_perso.getCuenta().idioma, 98));
				return;
			}
			agredido.setAgresion(true);
			_perso.setAgresion(true);
			GestorSalida.ENVIAR_GA_ACCION_JUEGO_AL_MAPA(_perso, mapa, "", 906, _perso.getID() + "", id + "");
			mapa.nuevaPelea(_perso, agredido, CentroInfo.PELEA_TIPO_PVP, false, false);
			agredido.setAgresion(false);
			_perso.setAgresion(false);
			_perso.personajeAgredea.add(agredido.getNombre());
		} catch (Exception e) {
			Emu.creaLogs(e);
		}
	}

	public static void juego_Accion(AccionDeJuego AJ, Personaje _perso, Cuenta _cuenta) {
		if (_perso.Marche) {
			_perso._accionesCola.add(AJ);
			return;
		}
		String packet = AJ._packet.substring(5);
		int celdaID = -1;
		int accionID = -1;
		try {
			celdaID = Integer.parseInt(packet.split(";")[0]);
			accionID = Integer.parseInt(packet.split(";")[1]);
		} catch (Exception e) {
			return;
		}
		if (celdaID == -1 || _perso.isOnAction() || accionID == -1 || _perso == null || _perso.getMapa() == null
				|| _perso.getMapa().getCelda(celdaID) == null) {
			return;
		}
		AJ._args = celdaID + ";" + accionID;
		_perso.addGA(AJ);
		_perso.iniciarAccionEnCelda(AJ);
	}

	private static void juego_Ataque_CAC(String packet, Personaje _perso) {
		try {
			if (_perso.getPelea() == null) {
				return;
			}
			int celdaID = -1;
			try {
				celdaID = Integer.parseInt(packet.substring(5));
			} catch (Exception e) {
				return;
			}
			if (_perso != null) {
				_perso.getPelea().intentarCaC(_perso, celdaID);
			}
		} catch (Exception e) {
			Emu.creaLogs(e);
		}
	}

	private static void juego_Lanzar_Hechizo(String packet, Personaje _perso) {
		try {
			if (packet.contains("undefined")) {
				return;
			}
			String[] splt = packet.split(";");
			int hechizoID = Integer.parseInt(splt[0].substring(5));
			int celdaID = Integer.parseInt(splt[1]);
			Pelea pelea = _perso.getPelea();
			if (pelea != null) {
				StatsHechizos SS = _perso.getStatsHechizo(hechizoID);
				if (SS == null) {
					GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1175");
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 301, _perso.getID() + "", "0");
					return;
				}
				pelea.intentarLanzarHechizo(pelea.getLuchadorPorPJ(_perso), SS, celdaID);
			}
		} catch (NumberFormatException e) {
			Emu.creaLogs(e);
			return;
		}
	}

	private static void juego_Unirse_Pelea(String packet, Personaje _perso, PrintWriter _out) {
		String[] infos = packet.substring(5).split(";");
		if (infos.length == 1) {
			try {
				if (_perso.getPelea() != null) {
					GestorSalida.ENVIAR_GA903_ERROR_PELEA(_out, 'o');
					return;
				}
				Pelea pelea = _perso.getMapa().getPelea(Integer.parseInt(infos[0]));
				if (pelea == null) {
					return;
				}
				pelea.unirseEspectador(_perso);
			} catch (Exception e) {
				Emu.creaLogs(e);
				GestorSalida.ENVIAR_BN_NADA(_out);
				return;
			}
		} else {
			try {
				if (_perso.getPelea() != null) {
					GestorSalida.ENVIAR_GA903_ERROR_PELEA(_out, 'o');
					return;
				}
				int id = Integer.parseInt(infos[1]);
				if (_perso.estaOcupado()) {
					GestorSalida.ENVIAR_GA903_ERROR_PELEA(_out, 'o');
					return;
				}
				if (_perso.esFantasma()) {
					GestorSalida.ENVIAR_GA903_ERROR_PELEA(_out, 'd');
					return;
				}
				if (id < -100) {
					int resta = (id + 100) % 3;
					if (resta == -2) {
						Prisma prisma = MundoDofus.getPrisma(id);
						if (prisma == null) {
							GestorSalida.ENVIAR_BN_NADA(_out);
							return;
						}
						int mapaID = prisma.getMapa();
						int celdaID = prisma.getCelda();
						if (prisma.getAlineacion() != _perso.getAlineacion()) {
							return;
						}
						if (prisma.getPelea().checkUnirsePrisma(_perso)) {
							if (_perso.getMapa().getID() != mapaID) {
								_perso.setMapaDefPerco(_perso.getMapa());
								_perso.setCeldaDefPerco(_perso.getCelda());
								_perso.esperaPeleaPrisma = prisma;
								_perso.teleport(mapaID, celdaID);
							} else {
								if (prisma.getPelea().unirsePeleaPrisma(_perso, id, mapaID, celdaID)) {
									for (Personaje z : MundoDofus.getPJsEnLinea()) {
										if (z == null || z.getAlineacion() != _perso.getAlineacion()) {
											continue;
										}
										Prisma.analizarDefensa(z);
									}
								}
							}
						}
					} else if (resta == 0) {
						Recaudador recau = MundoDofus.getRecaudador(id);
						if (recau == null) {
							GestorSalida.ENVIAR_BN_NADA(_out);
							return;
						}
						short mapaID = (short) recau.getMapaID();
						int celdaID = recau.getCeldalID();
						if (_perso.getPelea() != null) {
							return;
						}
						if (recau.getPelea().unirsePeleaRecaudador(_perso, id, mapaID, celdaID)) {
							for (Personaje miembros : _perso.getGremio().getPjMiembros()) {
								if (miembros == null || !miembros.enLinea()) {
									continue;
								}
								Recaudador.analizarDefensa(miembros, _perso.getGremio().getID());
							}
						}
					}
				} else {
					if (MundoDofus.getPersonaje(id).getPelea() != null) {
						MundoDofus.getPersonaje(id).getPelea().unirsePelea(_perso, id);
					}
				}
			} catch (Exception e) {
				Emu.creaLogs(e);
				GestorSalida.ENVIAR_BN_NADA(_out);
				return;
			}
		}
	}

	private static void juego_Aceptar_Desafio(String packet, Personaje _perso) {
		int id = -1;
		try {
			id = Integer.parseInt(packet.substring(5));
		} catch (NumberFormatException e) {
			return;
		}
		int idDuelo = _perso.getDueloID();
		if (idDuelo != id || idDuelo == -1) {
			return;
		}
		Mapa mapa = _perso.getMapa();
		GestorSalida.ENVIAR_GA901_ACEPTAR_DESAFIO(mapa, idDuelo, _perso.getID());
		Pelea pelea = mapa.nuevaPelea(MundoDofus.getPersonaje(idDuelo), _perso, CentroInfo.PELEA_TIPO_DESAFIO, false,
				false);
		_perso.setPelea(pelea);
		MundoDofus.getPersonaje(idDuelo).setPelea(pelea);
	}

	private static void juego_Cancelar_Desafio(String packet, Personaje _perso) {
		if (_perso.getDueloID() == -1) {
			return;
		}
		GestorSalida.ENVIAR_GA902_RECHAZAR_DESAFIO(_perso.getMapa(), _perso.getDueloID(), _perso.getID());
		try {
			Personaje desafiador = MundoDofus.getPersonaje(_perso.getDueloID());
			desafiador.setOcupado(false);
			desafiador.setDueloID(-1);
		} catch (NullPointerException e) {
			Emu.creaLogs(e);
		}
		_perso.setOcupado(false);
		_perso.setDueloID(-1);
	}

	private static void juego_Desafiar(String packet, Personaje _perso, PrintWriter _out) {
		Mapa mapa = _perso.getMapa();
		int idPerso = _perso.getID();
		if (mapa.getLugaresString().equalsIgnoreCase("|") || mapa.getLugaresString().isEmpty()) {
			GestorSalida.ENVIAR_GA903_ERROR_PELEA(_out, 'p');
			return;
		}
		try {
			int id = Integer.parseInt(packet.substring(5));
			if (_perso.estaOcupado() || _perso.getPelea() != null) {
				GestorSalida.ENVIAR_GA903_UNIRSE_PELEA_Y_ESTAR_OCUPADO(_out, idPerso);
				return;
			}
			if (_perso.enTorneo == 1) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 99),
						Colores.ROJO);
				return;
			}
			if (_perso.liderMaitre || _perso.esMaitre) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 39),
						Colores.ROJO);
				return;
			}
			Personaje desafiado = MundoDofus.getPersonaje(id);
			if (desafiado == null) {
				return;
			}
			if (desafiado.estaOcupado() || desafiado.getPelea() != null
					|| desafiado.getMapa().getID() != mapa.getID()) {
				GestorSalida.ENVIAR_GA903_UNIRSE_PELEA_Y_OPONENTE_OCUPADO(_out, idPerso);
				return;
			}
			if (desafiado.enTorneo == 1) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 90),
						Colores.ROJO);
				return;
			}
			if (desafiado.liderMaitre || desafiado.esMaitre) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 91),
						Colores.ROJO);
				return;
			}
			_perso.setDueloID(id);
			_perso.setOcupado(true);
			desafiado.setDueloID(_perso.getID());
			desafiado.setOcupado(true);
			GestorSalida.ENVIAR_GA900_DESAFIAR(mapa, idPerso, id);
		} catch (NumberFormatException e) {
			return;
		}
	}

	private static void juego_Desplazamiento(AccionDeJuego AJ, Personaje _perso, PrintWriter _out, Cuenta _cuenta,
			String packet) {
		if (_perso.isOnAction()) {
			return;
		}
		Pelea pelea = _perso.getPelea();
		if (pelea == null) {
			int persoid = -1;
			// int celdadestino = -1;
			String desplaza = "";
			String path = "";
			int celdaD = -1;
			try {
				path = packet.substring(5).split(";")[0];
				persoid = Integer.parseInt(packet.split(";")[1]);
				/*
				 * if (ruta.length() > 2) celdadestino =
				 * Encriptador.celdaCodigoAID(path.substring(path.length()-2)); else
				 * celdadestino = Encriptador.celdaCodigoAID(path);
				 */
				desplaza = packet.split(";")[2];
				celdaD = Integer.parseInt(packet.split(";")[3]);
			} catch (Exception e) {
				if (_cuenta.getEntradaPersonaje() != null) {
					_cuenta.getEntradaPersonaje().salir(false);
				}
				return;
			}
			if (_perso.esTumba()) {
				GestorSalida.ENVIAR_BN_NADA(_out);
				return;
			}
			if (_perso.esMaitre && !_perso.enMovi) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 100),
						Colores.ROJO);
			}
			if (_perso.esMaitre && !_perso.enMovi) {
				_perso.enMovi = true;
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 101),
						Colores.ROJO);
			}
			if (_perso.isOnAction()) {
				GestorSalida.ENVIAR_GA_ACCION_DE_JUEGO(_out, "", "0", "", "");
				_perso.borrarGA(AJ);
				return;
			}
			if (_perso.lastpodvalue == 0) {
				_perso.lastpodvalue = _perso.getMaxPod();
			}
			if (_perso.getPodUsados() > _perso.lastpodvalue) {
				GestorSalida.ENVIAR_Im_INFORMACION(_out, "112");
				_perso.setOcupado(false);
				GestorSalida.ENVIAR_GA_ACCION_DE_JUEGO(_out, "", "0", "", "");
				_perso.borrarGA(AJ);
				return;
			}
			if (celdaD != 0) {
				if (_perso.getMapa().getCelda(celdaD)._celdaAccion.get(0) != null) {
					int idacc = _perso.getMapa().getCelda(celdaD)._celdaAccion.get(0).getID();
					String args = _perso.getMapa().getCelda(celdaD)._celdaAccion.get(0)._args;
					if (idacc == 0) {
						Mapa mapa = MundoDofus.getMapa(Integer.parseInt(args.split(",")[0]));
						String extra = "";
						if (mapa.tienekey) {
							extra = mapa.getCodigo();
						}
						String packetx = mapa.getID() + "|" + mapa.getFecha() + "|" + extra + "|" + mapa.getAncho()
								+ "|" + mapa.getAlto() + "|" + mapa.getBgID() + "|" + mapa.getMusicID() + "|"
								+ mapa.getAmbienteID() + "|" + mapa.getOutDoor() + "|" + mapa.getCapabilities() + "|"
								+ mapa.getMapData() + "|1";
						GestorSalida.enviar(_perso, "#gr" + packetx);
					}
				}
			}
			_perso.Marche = true;
			_perso.addGA(AJ);
			AJ._args = path;
			for (Personaje z : _perso.getMapa().getPersos(_perso)) {
				GestorSalida.enviar(z, "#km" + persoid + ";" + desplaza + ";" + AJ._idUnica);
			}
			if (_perso.estaSentado()) {
				_perso.setSentado(false);
			}
			_perso.setOcupado(true);
			if (_perso.liderMaitre) {
				if (_perso.getGrupo().getPersos().size() > 1) {
					for (Personaje pjx : _perso.getGrupo().getPersos()) {
						if (!pjx.enLinea()) {
							continue;
						}
						if (pjx == _perso) {
							GestorSalida.ENVIAR_BLOQUEO_PANTALLA(true, _perso);
							continue;
						}
						if (pjx.getCelda().getID() == _perso.getCelda().getID()) {
							pjx.enMovi = true;
							if (pjx.getCuenta().getEntradaPersonaje() != null) {
								for (Personaje z : pjx.getMapa().getPersos()) {
									pjx.addGA(AJ);
									GestorSalida.enviar(z, "#km" + pjx.getID() + ";" + desplaza + ";" + AJ._idUnica);
								}
							} else {
								pjx.esMaitre = false;
							}
						}
					}
				}
			}
		} else {
			String path = AJ._packet.substring(5);
			Luchador luchador = pelea.getLuchadorPorPJ(_perso);
			if (luchador == null) {
				return;
			}
			AJ._args = path;
			pelea.puedeMoverseLuchador(luchador, AJ, "estaticos");
		}
	}

	static void listaRegalos(PrintWriter _out, Cuenta _cuenta) {
		if ((System.currentTimeMillis() - _cuenta.lastcheckRegalo > 20000)) {
			_cuenta.lastcheckRegalo = System.currentTimeMillis();
			int regalo = _cuenta.getRegalo();
			if (regalo == 0) {
				GestorSQL.CARGAR_REGALO(_cuenta);
				regalo = _cuenta.getRegalo();
			}
			if (regalo != 0) {
				if (MundoDofus.getObjModelo(regalo) != null) {
					String idModObjeto = Integer.toString(regalo, 16);
					String efectos = MundoDofus.getObjModelo(regalo).getStringStatsObj();
					GestorSalida.ENVIAR_Ag_LISTA_REGALOS(_out, regalo, "1~" + idModObjeto + "~1~~" + efectos);
				} else {
					_cuenta.setRegalo();
				}
			} else {
				String gifts = MundoDofus.compruebaRegalos(_cuenta);
				if (!gifts.isEmpty()) {
					StringBuilder data = new StringBuilder("");
					int item = -1;
					for (String object : gifts.split(";")) {
						int id = Integer.parseInt(object.split(",")[0]), qua = Integer.parseInt(object.split(",")[1]);
						if (MundoDofus.getObjModelo(id) == null) {
							continue;
						}
						if (data.toString().isEmpty()) {
							data.append("1~" + Integer.toString(id, 16) + "~" + Integer.toString(qua, 16) + "~~"
									+ MundoDofus.getObjModelo(id).getStringStatsObj());
						} else {
							data.append(";1~" + Integer.toString(id, 16) + "~" + Integer.toString(qua, 16) + "~~"
									+ MundoDofus.getObjModelo(id).getStringStatsObj());
						}
						if (item == -1) {
							item = id;
						}
					}
					GestorSalida.ENVIAR_Ag_LISTA_REGALOS(_out, item, data.toString());
				}
			}
		}
	}

	static void cuenta_Entregar_Regalo(String packet, PrintWriter _out, Cuenta _cuenta) {
		String[] info = packet.split("\\|");
		int idObjeto = Integer.parseInt(info[0]);
		int idPj = Integer.parseInt(info[1]);
		if (_cuenta.getRegalo() != 0) {
			Personaje pj = null;
			Objeto objeto = null;
			try {
				pj = MundoDofus.getPersonaje(idPj);
				objeto = MundoDofus.getObjModelo(idObjeto).crearObjDesdeModelo(1, false);
			} catch (Exception e) {
			}
			if (pj == null || objeto == null || (_cuenta.getRegalo() != idObjeto)) {
				return;
			}
			if (!pj.addObjetoSimilar(objeto, true, -1)) {
				MundoDofus.addObjeto(objeto, true);
				pj.addObjetoPut(objeto);
				GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(pj, objeto);
			}
			_cuenta.setRegalo();
			GestorSQL.ACTUALIZAR_REGALO(_cuenta);
			GestorSalida.ENVIAR_AG_SIGUIENTE_REGALO(_out);
		} else {
			int laid = MundoDofus.getIDRegalo(idObjeto, _cuenta);
			int cantidad = MundoDofus.getCantRegalo(idObjeto, _cuenta);
			if (laid != -1) {
				_cuenta.tieneRegalo.add(laid);
				String lacu = "";
				for (Integer desaf : _cuenta.tieneRegalo) {
					if (lacu.isEmpty()) {
						lacu = desaf + "";
					} else {
						lacu += ";" + desaf;
					}
				}
				Personaje pj = null;
				Objeto objeto = null;
				try {
					pj = MundoDofus.getPersonaje(idPj);
					objeto = MundoDofus.getObjModelo(idObjeto).crearObjDesdeModelo(cantidad, true);
				} catch (Exception e) {
				}
				if (!pj.addObjetoSimilar(objeto, true, -1)) {
					MundoDofus.addObjeto(objeto, true);
					pj.addObjetoPut(objeto);
					GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(pj, objeto);
				}
				GestorSQL.setRegalosCuenta2(lacu, _cuenta.getID());
				String gifts = MundoDofus.compruebaRegalos(_cuenta);
				if (!gifts.isEmpty()) {
					listaRegalos(_out, _cuenta);
				}
				GestorSalida.ENVIAR_AG_SIGUIENTE_REGALO(_out);
			} else {
				GestorSalida.ENVIAR_AG_SIGUIENTE_REGALO(_out);
			}
		}
	}

public static void cuenta_Eliminar_Personaje(String packet, PrintWriter out, Cuenta cuenta) {
    String[] split = packet.substring(2).split("\\|");
    if (split.length == 0) return;
    
    String pjData = split[0]; // Puede ser ID o Nombre
    String respuesta = split.length > 1 ? split[1] : "";
    Personaje pj = null;

    try {
        // Intentamos buscar por ID (formato antiguo)
        int id = Integer.parseInt(pjData);
        pj = cuenta.getPersonajes().get(id);
    } catch (NumberFormatException e) {
        // Si no es número, buscamos por Nombre (formato 1.43.7)
        for (Personaje p : cuenta.getPersonajes().values()) {
            if (p.getNombre().equalsIgnoreCase(pjData)) {
                pj = p;
                break;
            }
        }
    }

    if (pj == null) {
        GestorSalida.ENVIAR_ADE_ERROR_BORRAR_PJ(out);
        return;
    }

    // Validación de seguridad (Wait 10 mins, Secret Answer, etc.)
    if (pj.fechacrea != null) {
        long diff = new Date().getTime() - pj.fechacrea.getTime();
        long minutes = diff / (60 * 1000);
        if (minutes < 10 && cuenta.getRango() < 1) {
            GestorSalida.ENVIAR_M145_MENSAJE_PANEL_INFORMACION(out, "Debes esperar 10 minutos. Restante: " + (10 - minutes) + "m");
            return;
        }
    }

    if (pj.getNivel() < 100 || respuesta.equals(cuenta.getRespuesta())) {
        cuenta.borrarPerso(pj.getID());
        GestorSalida.ENVIAR_ALK_LISTA_DE_PERSONAJES(out, cuenta.getPersonajes());
    } else {
        GestorSalida.ENVIAR_ADE_ERROR_BORRAR_PJ(out);
    }
}

	static void cuenta_Crear_Personaje(String packet, PrintWriter _out, Cuenta _cuenta) {
		String[] infos = packet.substring(2).split("\\|");
		if (GestorSQL.personajeYaExiste(infos[0])) {
			GestorSalida.ENVIAR_AAEa_NOMBRE_YA_EXISTENTE(_out);
			return;
		}
		boolean esValido = true;
		String nombre = infos[0].toLowerCase();
		if (nombre.length() > 20) {
			esValido = false;
		}
		if (esValido) {
			int cantSimbol = 0;
			char letra_A = ' ';
			char letra_B = ' ';
			for (char letra : nombre.toCharArray()) {
				if (!((letra >= 'a' && letra <= 'z') || letra == '-') || (letra == letra_A && letra == letra_B)) {
					esValido = false;
					break;
				}
				if (letra >= 'a' && letra <= 'z') {
					letra_A = letra_B;
					letra_B = letra;
				}
				if (letra == '-') {
					if (cantSimbol >= 1) {
						esValido = false;
						break;
					} else {
						cantSimbol++;
					}
				}
			}
		}
		if (!esValido) {
			GestorSalida.ENVIAR_AAEa_NOMBRE_YA_EXISTENTE(_out);
			return;
		}
		if (_cuenta.getNumeroPersonajes() >= Emu.MAX_PJS_POR_CUENTA) {
			GestorSalida.ENVIAR_AAEf_MAXIMO_PJS_CREADOS(_out);
			return;
		}
		int clase = Integer.parseInt(infos[1]);
		if (clase > 12) {
			GestorSalida.ENVIAR_AAEF_ERROR_CREAR_PJ(_out);
			return;
		}
		if (_cuenta.crearPj(infos[0], Integer.parseInt(infos[2]), clase, Integer.parseInt(infos[3]),
				Integer.parseInt(infos[4]), Integer.parseInt(infos[5]))) {
			GestorSalida.ENVIAR_AAK_CREACION_PJ(_out);
			GestorSalida.ENVIAR_ALK_LISTA_DE_PERSONAJES(_out, _cuenta.getPersonajes());
		} else {
			GestorSalida.ENVIAR_AAEF_ERROR_CREAR_PJ(_out);
		}
	}

	static void analizar_Ladder(String packet, Personaje _perso, PrintWriter _out) {
		try {
			if (_perso == null) {
				return;
			}
			if (packet.equals("#LD1")) {
				GestorSalida.enviar(_perso, MundoDofus.rankingNivel);
			} else if (packet.equals("#LD2")) {
				GestorSalida.enviar(_perso, MundoDofus.rankingGremios);
			}
		} catch (Exception e) {
			Emu.creaLogs(e);
		}
	}

	static void analizar_Koliseo(String packet, Personaje _perso, PrintWriter _out) {
		try {
			if (_perso == null) {
				return;
			}
			switch (packet.charAt(1)) {
			case 'C':// Abre panel Koliseo #kC3,2,3,4,5,2,2
				String strf = _perso.getInfoKoliseo();
				GestorSalida.enviar(_perso, strf);
				break;
			case 'A':// ACEPTA
				_perso._koliacepta = 2;
				String strf12 = _perso.getInfoKoliseo();
				GestorSalida.enviar(_perso, strf12);
				break;
			case 'S':// ME INSCRIBO
				if ((System.currentTimeMillis() - _perso.get_tiempoKoli()) < 5000 || _perso.getPelea() != null) {
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 102),
							Emu.COLOR_MENSAJE);
					return;
				}
				if (_perso.esMaitre) {
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso,
							"No puedes unirte a un Koliseo estando en un grupo de Maitre", Emu.COLOR_MENSAJE);
					return;
				}
				_perso.set_tiempoKoli(System.currentTimeMillis());
				if (_perso.getKoliseo() == null) {
					Koliseo arenax = null;
					boolean encuentra = false;
					for (Koliseo arena : Koliseo.arenas) {
						if (encuentra) {
							break;
						}
						if (arena.peleaempezada) {
							continue;
						}
						/*
						 * if (arena.nospam.contains(_perso)) continue;
						 */
						if (arena.Personajes.size() < Koliseo.PersoxKoli) {
							boolean para = false;
							for (Personaje perK : arena.Personajes) {
								if (para)
								 {
									break;// 100+100 >= 50 && 100-100 <= 50
								}
								if (((_perso.getNivel() + 100) >= perK.getNivel())
										&& ((_perso.getNivel() - 100) <= perK.getNivel())
										&& (_perso._resets == perK._resets)) {
									if (_perso.getCuenta().getActualIP()
											.compareTo(perK.getCuenta().getActualIP()) == 0) {
										arenax = null;
										encuentra = false;
										para = true;
										continue;
									}
									arenax = arena;
									encuentra = true;
								} else {
									arenax = null;
									encuentra = false;
									para = true;
								}
							}

						}
					}
					if (encuentra) {
						if (arenax.Personajes.size() == 1) {
							GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso,
									Idiomas.getTexto(_perso.getCuenta().idioma, 103), Colores.VERDE);
						} else if (arenax.Personajes.size() > 1) {
							GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso,
									Idiomas.getTexto(_perso.getCuenta().idioma, 104) + "" + arenax.Personajes.size()
											+ "" + Idiomas.getTexto(_perso.getCuenta().idioma, 105),
									Colores.VERDE);
						}
						for (Personaje persox : arenax.Personajes) {
							GestorSalida.ENVIAR_cs_CHAT_MENSAJE(persox,
									_perso.getNombre() + "" + Idiomas.getTexto(_perso.getCuenta().idioma, 106),
									Colores.VERDE);
						}
						arenax.Personajes.add(_perso);
						_perso._kolietapa = 2;
						_perso.setKoliseo(arenax);
						if (arenax.Personajes.size() == Koliseo.PersoxKoli) {
							for (Personaje perK : arenax.Personajes) {
								perK._kolietapa = 3;
								GestorSalida.enviar(perK, perK.getInfoKoliseo() + ((char) 0x00) + "#kB"); // SALTA EL
																											// PANEL SE
																											// ABRE
							}
							arenax.iniciaTimer();
						}
					} else {
						arenax = new Koliseo();
						arenax.Personajes.add(_perso);
						_perso._kolietapa = 2;
						_perso.setKoliseo(arenax);
						Koliseo.arenas.add(arenax);
					}
					String strf1 = _perso.getInfoKoliseo();
					GestorSalida.enviar(_perso, strf1);
				} else {
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 107),
							Colores.ROJO);
					return;
				}
				break;
			case 'V':
				if (_perso.getKoliseo() == null) {
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 108),
							Colores.ROJO);
					return;
				} else {
					_perso.getKoliseo().abandonar(_perso, true);
				}
				break;
			}
		} catch (Exception e) {
			Emu.creaLogs(e);
		}
	}

	private static int posicionLibre() {
		ArrayList<Integer> posiciones = new ArrayList<>();
		posiciones.add(1);
		posiciones.add(2);
		posiciones.add(3);
		posiciones.add(4);
		posiciones.add(5);
		posiciones.add(6);
		posiciones.add(7);
		posiciones.add(8);
		for (Entry<Integer, Personaje> ids : PjsTorneo.entrySet()) {
			if (posiciones.contains(ids.getKey())) {
				posiciones.remove(ids.getKey());
			}
		}
		if (posiciones.size() == 0) {
			return -1;
		}
		int libreid = posiciones.get(0);
		return libreid;
	}

	public static Map<Integer, Personaje> PjsTorneo = new TreeMap<>();// fase de 8
	public static Map<Integer, Personaje> PjsTorneo2 = new TreeMap<>();// fase de 4
	public static Map<Integer, Personaje> PjsTorneo3 = new TreeMap<>();// fase de 2
	public static Personaje ganador = null;
	public static ArrayList<Personaje> listaMuertos = new ArrayList<>();
	public static ArrayList<Personaje> listaMuertos2 = new ArrayList<>();
	public static ArrayList<Personaje> listaMuertos3 = new ArrayList<>();

	private static String listaTorneo() {
		StringBuilder str = new StringBuilder();
		boolean pasad = false;
		for (Entry<Integer, Personaje> pjx : PjsTorneo.entrySet()) {
			if (pasad) {
				str.append(";");
			}
			int muerto = 0;
			if (listaMuertos.contains(pjx.getValue())) {
				muerto = 1;
			}
			Personaje pj = pjx.getValue();
			str.append(pjx.getKey() + "," + muerto + "," + pj.getColor1() + "*" + pj.getColor2() + "*" + pj.getColor3()
					+ "," + pj.getNombre() + "," + pj.getGfxID());
			pasad = true;
		}
		for (Entry<Integer, Personaje> pjx : PjsTorneo2.entrySet()) {
			if (pasad) {
				str.append(";");
			}
			int muerto = 0;
			if (listaMuertos2.contains(pjx.getValue())) {
				muerto = 1;
			}
			Personaje pj = pjx.getValue();
			str.append(pjx.getKey() + "," + muerto + "," + pj.getColor1() + "*" + pj.getColor2() + "*" + pj.getColor3()
					+ "," + pj.getNombre() + "," + pj.getGfxID());
			pasad = true;
		}
		for (Entry<Integer, Personaje> pjx : PjsTorneo3.entrySet()) {
			if (pasad) {
				str.append(";");
			}
			int muerto = 0;
			if (listaMuertos3.contains(pjx.getValue())) {
				muerto = 1;
			}
			Personaje pj = pjx.getValue();
			str.append(pjx.getKey() + "," + muerto + "," + pj.getColor1() + "*" + pj.getColor2() + "*" + pj.getColor3()
					+ "," + pj.getNombre() + "," + pj.getGfxID());
			pasad = true;
		}
		if (ganador != null) {
			str.append(";");
			str.append(15 + ",0," + ganador.getColor1() + "*" + ganador.getColor2() + "*" + ganador.getColor3() + ","
					+ ganador.getNombre() + "," + ganador.getGfxID());
		}
		return str.toString();
	}

	private static int getRandomMap() {
		int rand = Formulas.getRandomValor(1, 3);
		switch (rand) {
		case 1:
			return 10133;
		case 2:
			return 10131;
		case 3:
			return 10134;
		default:
			return 10134;
		}
	}

	public static void ganadorTorneo(Personaje _perso) {
		if (Emu.faseTorneo == 1) {
			int posantes = -1;
			for (Entry<Integer, Personaje> px : PjsTorneo.entrySet()) {
				if (px.getValue() == _perso) {
					posantes = px.getKey();
					break;
				}
			}
			if (posantes == -1) {
				return;
			}
			int newpos = -1;
			switch (posantes) {
			case 1:
			case 2:
				newpos = 9;
				break;
			case 3:
			case 4:
				newpos = 10;
				break;
			case 5:
			case 6:
				newpos = 11;
				break;
			case 7:
			case 8:
				newpos = 12;
				break;
			}
			if (newpos == -1) {
				return;
			}
			PjsTorneo2.put(newpos, _perso);
			_perso.addTitulos(124);
			if (PjsTorneo2.size() == 4) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE_TODOS(Idiomas.getTexto(_perso.getCuenta().idioma, 109),
						Colores.AZUL);
				Emu.startTorneo();
			}
		} else if (Emu.faseTorneo == 2) {
			int posantes = -1;
			for (Entry<Integer, Personaje> px : PjsTorneo2.entrySet()) {
				if (px.getValue() == _perso) {
					posantes = px.getKey();
					break;
				}
			}
			if (posantes == -1) {
				return;
			}
			int newpos = -1;
			switch (posantes) {
			case 9:
			case 10:
				newpos = 13;
				break;
			case 11:
			case 12:
				newpos = 14;
				break;
			}
			if (newpos == -1) {
				return;
			}
			PjsTorneo3.put(newpos, _perso);
			ObjetoModelo OM = MundoDofus.getObjModelo(36025);
			if (OM != null) {
				Objeto obj = OM.crearObjDesdeModelo(1, true);
				if (!_perso.addObjetoSimilar(obj, true, -1)) {
					MundoDofus.addObjeto(obj, true);
					_perso.addObjetoPut(obj);
					GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_perso, obj);
				}
				GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso);
			}
			_perso.addTitulos(124);
			if (PjsTorneo3.size() == 2) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE_TODOS(Idiomas.getTexto(_perso.getCuenta().idioma, 110),
						Colores.AZUL);
				Emu.startTorneo();
			}
		} else if (Emu.faseTorneo == 3) {
			ObjetoModelo OM = MundoDofus.getObjModelo(50071);
			if (OM != null) {
				Objeto obj = OM.crearObjDesdeModelo(1, true);
				if (!_perso.addObjetoSimilar(obj, true, -1)) {
					MundoDofus.addObjeto(obj, true);
					_perso.addObjetoPut(obj);
					GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_perso, obj);
				}
				GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso);
			}
			_perso.addTitulos(125);
			ganador = _perso;
			_perso.enTorneo = 0;
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE_TODOS("Felicidades a " + _perso.getNombre() + " por ganar el Torneo!!!",
					Colores.NARANJA);
		}
		if (_perso.enLinea() && Emu.faseTorneo != 3) {
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 111),
					Colores.VERDE);
		}
	}

	public static void perderTorneo(Personaje _perso) {
		if (Emu.empezoTorneo) {
			if (Emu.faseTorneo == 1) {
				if (!listaMuertos.contains(_perso)) {
					listaMuertos.add(_perso);
				}
			} else if (Emu.faseTorneo == 2) {
				if (!listaMuertos2.contains(_perso)) {
					listaMuertos2.add(_perso);
				}
			} else if (Emu.faseTorneo == 3) {
				if (!listaMuertos3.contains(_perso)) {
					listaMuertos3.add(_perso);
				}
			}
			if (_perso.enLinea()) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 112),
						Colores.ROJO);
			}
		} else {
			if (_perso.enLinea()) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 113),
						Colores.ROJO);
			}
			int laid = -1;
			for (Entry<Integer, Personaje> px : PjsTorneo.entrySet()) {
				if (px.getValue() == _perso) {
					laid = px.getKey();
					break;
				}
			}
			if (laid != -1) {
				PjsTorneo.remove(laid);
			}
			int orden = 1;
			Map<Integer, Personaje> PjsTorneoNEW = new TreeMap<>();
			for (Personaje px : PjsTorneo.values()) {
				PjsTorneoNEW.put(orden, px);
				orden += 1;
			}
			PjsTorneo.clear();
			PjsTorneo.putAll(PjsTorneoNEW);
		}
		_perso.enTorneo = 0;
	}

	public static void iniciarTorneo() {
		if (Emu.faseTorneo == 0) {
			if (PjsTorneo.size() == 8) {
				Emu.empezoTorneo = true;
				Emu.faseTorneo = 1;
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE_TODOS(
						"La fase " + Emu.faseTorneo
								+ "/3 del Torneo ha empezado, puedes �r a ver a los participantes escribiendo .arena",
						Colores.AZUL);
				for (int i = 1; i < 5; i++) {// 1,2,3,4
					int val1 = 0;
					int val2 = 1;
					switch (i) {
					case 1:
						val1 = 1;
						val2 = 2;
						break;
					case 2:
						val1 = 3;
						val2 = 4;
						break;
					case 3:
						val1 = 5;
						val2 = 6;
						break;
					case 4:
						val1 = 7;
						val2 = 8;
						break;
					}
					Personaje pj1 = PjsTorneo.get(val1);
					Personaje pj2 = PjsTorneo.get(val2);
					boolean listam = false;
					if (!pj1.enLinea() && !pj2.enLinea()) {
						int random = Formulas.getRandomValor(1, 2);
						if (random == 1) {
							perderTorneo(pj1);
							ganadorTorneo(pj2);
						} else {
							perderTorneo(pj2);
							ganadorTorneo(pj1);
						}
						listam = true;
					} else if (!pj1.enLinea() && pj2.enLinea()) {
						perderTorneo(pj1);
						ganadorTorneo(pj2);
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(pj2, Idiomas.getTexto(pj2.getCuenta().idioma, 82),
								Colores.VERDE);
						listam = true;
					} else if (pj1.enLinea() && !pj2.enLinea()) {
						perderTorneo(pj2);
						ganadorTorneo(pj1);
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(pj1, Idiomas.getTexto(pj1.getCuenta().idioma, 82),
								Colores.VERDE);
						listam = true;
					}
					if (listam) {
						continue;
					}
					int randommap = getRandomMap();
					if (pj1.getMapa().getID() != randommap) {
						pj1.teleport((short) randommap, 280);
					}
					if (pj2.getMapa().getID() != randommap) {
						pj2.teleport((short) randommap, 280);
					}
					pj1.iniciaTorneo = pj2;
					pj2.esperaPelea = pj1;
				}
			}
		} else if (Emu.faseTorneo == 1) {
			if (PjsTorneo2.size() == 4) {
				Emu.faseTorneo = 2;
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE_TODOS("La fase " + Emu.faseTorneo + "/3 del Torneo ha empezado",
						Colores.AZUL);
				for (int i = 1; i < 3; i++) {
					int val1 = 0;
					int val2 = 1;
					switch (i) {
					case 1:
						val1 = 9;
						val2 = 10;
						break;
					case 2:
						val1 = 11;
						val2 = 12;
						break;
					}
					Personaje pj1 = PjsTorneo2.get(val1);
					Personaje pj2 = PjsTorneo2.get(val2);
					boolean listam = false;
					if (!pj1.enLinea() && !pj2.enLinea()) {
						int random = Formulas.getRandomValor(1, 2);
						if (random == 1) {
							perderTorneo(pj1);
							ganadorTorneo(pj2);
						} else {
							perderTorneo(pj2);
							ganadorTorneo(pj1);
						}
						listam = true;
					} else if (!pj1.enLinea() && pj2.enLinea()) {
						perderTorneo(pj1);
						ganadorTorneo(pj2);
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(pj2, Idiomas.getTexto(pj2.getCuenta().idioma, 82),
								Colores.VERDE);
						listam = true;
					} else if (pj1.enLinea() && !pj2.enLinea()) {
						perderTorneo(pj2);
						ganadorTorneo(pj1);
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(pj1, Idiomas.getTexto(pj1.getCuenta().idioma, 82),
								Colores.VERDE);
						listam = true;
					}
					if (listam) {
						continue;
					}
					int randommap = getRandomMap();
					if (pj1.getMapa().getID() != randommap) {
						pj1.teleport((short) randommap, 280);
					}
					if (pj2.getMapa().getID() != randommap) {
						pj2.teleport((short) randommap, 280);
					}
					pj1.iniciaTorneo = pj2;
					pj2.esperaPelea = pj1;
				}
			}
		} else if (Emu.faseTorneo == 2) {
			if (PjsTorneo3.size() == 2) {
				Emu.faseTorneo = 3;
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE_TODOS("La fase " + Emu.faseTorneo + "/3 del Torneo ha empezado",
						Colores.AZUL);
				Personaje pj1 = PjsTorneo3.get(13);
				Personaje pj2 = PjsTorneo3.get(14);
				boolean listam = false;
				if (!pj1.enLinea() && !pj2.enLinea()) {
					int random = Formulas.getRandomValor(1, 2);
					if (random == 1) {
						perderTorneo(pj1);
						ganadorTorneo(pj2);
					} else {
						perderTorneo(pj2);
						ganadorTorneo(pj1);
					}
					listam = true;
				} else if (!pj1.enLinea() && pj2.enLinea()) {
					perderTorneo(pj1);
					ganadorTorneo(pj2);
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(pj2, Idiomas.getTexto(pj2.getCuenta().idioma, 82),
							Colores.VERDE);
					listam = true;
				} else if (pj1.enLinea() && !pj2.enLinea()) {
					perderTorneo(pj2);
					ganadorTorneo(pj1);
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(pj1, Idiomas.getTexto(pj1.getCuenta().idioma, 82),
							Colores.VERDE);
					listam = true;
				}
				if (listam) {
					return;
				}
				int randommap = getRandomMap();
				if (pj1.getMapa().getID() != randommap) {
					pj1.teleport((short) randommap, 280);
				}
				if (pj2.getMapa().getID() != randommap) {
					pj2.teleport((short) randommap, 280);
				}
				pj1.iniciaTorneo = pj2;
				pj2.esperaPelea = pj1;
			}
		}
	}

	private static void iniciaPeleaVS(Personaje pj1, Personaje pj2) {
		pj1.usaTP = false;
		pj2.usaTP = false;
		GestorSalida.ENVIAR_cs_CHAT_MENSAJE(pj1, Idiomas.getTexto(pj1.getCuenta().idioma, 114), Colores.VERDE);
		GestorSalida.ENVIAR_cs_CHAT_MENSAJE(pj2, Idiomas.getTexto(pj2.getCuenta().idioma, 114), Colores.VERDE);
		Timer timer = new Timer(3000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				((Timer) e.getSource()).stop();
				boolean finaliza = false;
				if (!pj1.enLinea() && !pj2.enLinea()) {
					int random = Formulas.getRandomValor(1, 2);
					if (random == 1) {
						perderTorneo(pj1);
						ganadorTorneo(pj2);
					} else {
						perderTorneo(pj2);
						ganadorTorneo(pj1);
					}
					finaliza = true;
				} else if (!pj1.enLinea() && pj2.enLinea()) {
					perderTorneo(pj1);
					ganadorTorneo(pj2);
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(pj2, Idiomas.getTexto(pj2.getCuenta().idioma, 82),
							Colores.VERDE);
					finaliza = true;
				} else if (pj1.enLinea() && !pj2.enLinea()) {
					perderTorneo(pj2);
					ganadorTorneo(pj1);
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(pj1, Idiomas.getTexto(pj1.getCuenta().idioma, 82),
							Colores.VERDE);
					finaliza = true;
				}
				if (pj1.getPelea() != null) {
					perderTorneo(pj1);
					ganadorTorneo(pj2);
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(pj2, Idiomas.getTexto(pj2.getCuenta().idioma, 82),
							Colores.VERDE);
					finaliza = true;
				}
				if (pj2.getPelea() != null) {
					perderTorneo(pj2);
					ganadorTorneo(pj1);
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(pj1, Idiomas.getTexto(pj1.getCuenta().idioma, 82),
							Colores.VERDE);
					finaliza = true;
				}
				if (finaliza) {
					return;
				}
				GestorSalida.ENVIAR_GA900_DESAFIAR(pj1.getMapa(), pj1.getID(), pj2.getID()); // MAPA,YO,OBJETIVO DUELOID
				GestorSalida.ENVIAR_GA901_ACEPTAR_DESAFIO(pj2.getMapa(), pj1.getID(), pj2.getID()); // PERSO
																									// MAPA,DUELOID,PERSOID
				GestorSalida.ENVIAR_GA902_RECHAZAR_DESAFIO(pj2.getMapa(), pj2.getDueloID(), pj2.getID());
				Pelea pel = pj1.getMapa().nuevaPelea(pj1, pj2, CentroInfo.PELEA_TIPO_DESAFIO, false, true);
				pel.enTorneo = true;
				pj1.usaTP = true;
				pj2.usaTP = true;
			}
		});
		timer.start();
	}

	static void analizar_DofusOnline(String packet, Personaje _perso, PrintWriter _out) {
		try {
			if (_perso == null) {
				return;
			}
			switch (packet.charAt(1)) { // #bB0;itmid^itemid^stats;recompensa2:posicion,muerto,colores,gfxiid;........
			case 'I':// desinscribirse torneo
				if (Emu.TorneoOn) {
					if (Emu.empezoTorneo) {
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 115),
								Colores.ROJO);
						return;
					}
					if (!PjsTorneo.containsValue(_perso)) {
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 116),
								Colores.ROJO);
						return;
					}
					int laid = -1;
					for (Entry<Integer, Personaje> px : PjsTorneo.entrySet()) {
						if (px.getValue() == _perso) {
							laid = px.getKey();
							break;
						}
					}
					if (laid != -1) {
						PjsTorneo.remove(laid);
					}
					int orden = 1;
					Map<Integer, Personaje> PjsTorneoNEW = new TreeMap<>();
					for (Personaje px : PjsTorneo.values()) {
						PjsTorneoNEW.put(orden, px);
						orden += 1;
					}
					PjsTorneo.clear();
					PjsTorneo.putAll(PjsTorneoNEW);
					_perso.enTorneo = 0;
					int lafase = _perso.enTorneo;
					if (Emu.faseTorneo != 0) {
						lafase = 2;
					}
					GestorSalida.enviar(_perso,
							"#bb" + lafase + ";36025^36025^" + MundoDofus.getObjModelo(36025).getStringStatsObj()
									+ ";50071^50071^" + MundoDofus.getObjModelo(50071).getStringStatsObj() + ":"
									+ listaTorneo());
				} else {
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, "No hay ning�n Torneo programado todav�a",
							Colores.ROJO);
					GestorSalida.enviar(_perso, "#bb2;36025^36025^" + MundoDofus.getObjModelo(36025).getStringStatsObj()
							+ ";50071^50071^" + MundoDofus.getObjModelo(50071).getStringStatsObj() + ":");
				}
				break;
			case 'O':// inscribirse torneo //TODO:
				if (Emu.TorneoOn) {
					if (Emu.empezoTorneo) {
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 117),
								Colores.ROJO);
						return;
					}
					if (Emu.torneoR <= _perso._resets) {
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 149),
								Colores.ROJO);
						return;
					}
					if (PjsTorneo.containsValue(_perso)) {
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 118),
								Colores.ROJO);
						return;
					}
					if (PjsTorneo.size() == 8) {
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 119),
								Colores.ROJO);
						return;
					}
					boolean multi = false;
					for (Personaje pjx : PjsTorneo.values()) {
						if (pjx.getCuenta().getActualIP().compareTo(_perso.getCuenta().getActualIP()) == 0) {
							multi = true;
							break;
						}
					}
					if (multi) {
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 120),
								Colores.ROJO);
						return;
					}
					int posl = posicionLibre();
					if (posl == -1) {
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 121),
								Colores.ROJO);
						return;
					}
					PjsTorneo.put(posl, _perso);
					_perso.enTorneo = 1;
					int lafase = _perso.enTorneo;
					if (Emu.faseTorneo != 0) {
						lafase = 2;
					}
					if (PjsTorneo.size() == 8) {
						Map<Integer, Personaje> PjsTorneoNEW = new TreeMap<>();

						List<Personaje> valuesList = new ArrayList<>(PjsTorneo.values());
						Collections.shuffle(valuesList);
						int elx = 1;
						for (Personaje prx : valuesList) {
							PjsTorneoNEW.put(elx, prx);
							elx += 1;
						}
						PjsTorneo.clear();
						PjsTorneo.putAll(PjsTorneoNEW);
						Emu.startTorneo();
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE_TODOS(Idiomas.getTexto(_perso.getCuenta().idioma, 122),
								Colores.NARANJA);
					}
					GestorSalida.enviar(_perso,
							"#bb" + lafase + ";36025^36025^" + MundoDofus.getObjModelo(36025).getStringStatsObj()
									+ ";50071^50071^" + MundoDofus.getObjModelo(50071).getStringStatsObj() + ":"
									+ listaTorneo());
				} else {
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 124),
							Colores.ROJO);
					GestorSalida.enviar(_perso, "#bb2;36025^36025^" + MundoDofus.getObjModelo(36025).getStringStatsObj()
							+ ";50071^50071^" + MundoDofus.getObjModelo(50071).getStringStatsObj() + ":");
				}
				break;
			case 'B':// torneos
				if (Emu.TorneoOn) {
					int lafase = _perso.enTorneo;
					if (Emu.faseTorneo != 0 || Emu.empezoTorneo) {
						lafase = 2;
					}
					if (!Emu.empezoTorneo) {
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 123),
								Colores.ROJO);
					}
					GestorSalida.enviar(_perso,
							"#bB" + lafase + ";36025^36025^" + MundoDofus.getObjModelo(36025).getStringStatsObj()
									+ ";50071^50071^" + MundoDofus.getObjModelo(50071).getStringStatsObj() + ":"
									+ listaTorneo());
				} else {
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 124),
							Colores.ROJO);
					GestorSalida.enviar(_perso, "#bB2;36025^36025^" + MundoDofus.getObjModelo(36025).getStringStatsObj()
							+ ";50071^50071^" + MundoDofus.getObjModelo(50071).getStringStatsObj() + ":");
				}
				break;
			case 'r': // Abre panel desafios
				String[] drop = packet.substring(2).split("\\|");
				int eltipo = Integer.parseInt(drop[0]);// 0-1-2-3-4
				int objid = Integer.parseInt(drop[1].split(";")[1]);
				Map<Integer, Integer> itemsReclama = new TreeMap<>();
				int reclamos = 0;
				switch (eltipo) {
				case 0:
					itemsReclama = _perso.itemsFinal;
					reclamos = _perso.reclamados1;
					break;
				case 1:
					itemsReclama = _perso.itemsFinaldos;
					reclamos = _perso.reclamados2;
					break;
				case 2:
					itemsReclama = _perso.itemsFinaltres;
					reclamos = _perso.reclamados3;
					break;
				case 3:
					itemsReclama = _perso.itemsFinalcua;
					reclamos = _perso.reclamados4;
					break;
				case 4:
					itemsReclama = _perso.itemsFinalcinc;
					reclamos = _perso.reclamados5;
					break;
				default:
					eltipo = 5;
					return;
				}
				if (eltipo < 0 || eltipo > 4) {
					return;
				}
				if (!itemsReclama.isEmpty()) {
					if (itemsReclama.containsKey(objid)) {
						boolean suma = false;
						ObjetoModelo OM = MundoDofus.getObjModelo(objid);
						if (OM == null) {
							GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso,
									Idiomas.getTexto(_perso.getCuenta().idioma, 125), Colores.ROJO);
							return;
						}
						switch (OM.getTipo()) {
						case 1:
						case 2:
						case 3:
						case 4:
						case 5:
						case 6:
						case 7:
						case 8:
						case 9:
						case 10:
						case 11:
						case 16:
						case 17:
						case 18:
						case 19:
						case 23:
						case 81:
						case 82:
						case 89:
						case 113:
						case 114:
							suma = true;
							break;
						}
						if (reclamos >= _perso.cuantopuede && suma) {
							GestorSalida
									.ENVIAR_cs_CHAT_MENSAJE(_perso,
											Idiomas.getTexto(_perso.getCuenta().idioma, 126) + "" + _perso.cuantopuede
													+ "" + Idiomas.getTexto(_perso.getCuenta().idioma, 127),
											Colores.ROJO);
							return;
						}

						Objeto obj = OM.crearObjDesdeModelo(itemsReclama.get(objid), false);
						if (!_perso.addObjetoSimilar(obj, true, -1)) {
							MundoDofus.addObjeto(obj, true);
							_perso.addObjetoPut(obj);
							GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_perso, obj);
						}
						String extr = "";
						if (!suma) {
							extr = Idiomas.getTexto(_perso.getCuenta().idioma, 128);
						}
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso,
								Idiomas.getTexto(_perso.getCuenta().idioma, 129) + "" + extr, Colores.VERDE);

						switch (eltipo) {
						case 0:
							_perso.itemsFinal.remove(Integer.valueOf(objid));
							if (suma) {
								_perso.reclamados1 += 1;
							}
							break;
						case 1:
							_perso.itemsFinaldos.remove(Integer.valueOf(objid));
							if (suma) {
								_perso.reclamados2 += 1;
							}
							break;
						case 2:
							_perso.itemsFinaltres.remove(Integer.valueOf(objid));
							if (suma) {
								_perso.reclamados3 += 1;
							}
							break;
						case 3:
							_perso.itemsFinalcua.remove(Integer.valueOf(objid));
							if (suma) {
								_perso.reclamados4 += 1;
							}
							break;
						case 4:
							_perso.itemsFinalcinc.remove(Integer.valueOf(objid));
							if (suma) {
								_perso.reclamados5 += 1;
							}
							break;
						}
						_perso.getTotalCombate();
					} else {
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 125),
								Colores.ROJO);
					}
				}
				break;
			case 'Q':
				GestorSalida.enviar(_perso, "#bC" + _perso.getTitulos() + ",");
				break;
			case 'v':
				int extra = 0;
				try {
					if (packet.contains("NaN")) {
						return;
					}
					extra = Integer.parseInt(packet.substring(2));
				} catch (Exception e) {
					extra += _perso.paginaCanj;
				}
				if (extra < 0) {
					extra = 0;
				}
				StringBuilder str = new StringBuilder("");
				boolean prim = false;
				int puntos = _perso.PuntosPrestigio;
				int porcent = 0;
				boolean siguiente = false;
				int ptsant = 0;
				int b = 0;
				int t = 0;
				int vl = 0;// todo:
				int maxi = 0;
				for (int ix = 1; ix < 9; ix++) {
					int newvlr = ix + extra;
					if ((newvlr + 1) > MundoDofus.Prestigios.size()) {
						maxi = 1;
					}
					if ((newvlr) > MundoDofus.Prestigios.size()) {
						continue;
					}
					String valores = MundoDofus.Prestigios.get(newvlr);
					if (prim) {
						str.append(",");
					}
					int canjeados = 0;
					if (_perso.Canjeados.contains(newvlr)) {
						canjeados = 1;
					}
					int necesario = Integer.parseInt(valores.split(",")[1]);
					if (necesario == 0) {
						maxi = 1;
						continue;
					}
					int desbloq = 0;
					if (puntos >= necesario) {
						if ((newvlr - 5) > _perso.paginaCanj) {
							_perso.paginaCanj = (newvlr - 5);
						}
						desbloq = 1;
						switch (ix) {
						case 1:
							porcent = 17;
							break;
						case 2:
							porcent = 72;
							break;
						case 3:
							porcent = 124;
							break;
						case 4:
							porcent = 177;
							break;
						case 5:
							porcent = 229;
							break;
						case 6:
							porcent = 283;
							break;
						case 7:
							porcent = 335;
							break;
						case 8:
							porcent = 388;
							break;
						}
						ptsant = necesario;
						if (puntos > necesario && ix >= 8) {
							porcent = 400;
						}
						siguiente = true;
					} else {
						int dividen = 0;
						switch (ix) {
						case 1:
							dividen = 17;
							break;
						case 2:
							dividen = 35;
							break;
						case 3:
							dividen = 52;
							break;
						case 4:
							dividen = 53;
							break;
						case 5:
							dividen = 52;
							break;
						case 6:
							dividen = 54;
							break;
						case 7:
							dividen = 52;
							break;
						case 8:
							dividen = 53;
							break;
						}
						switch (ix) {
						case 1:// 1- 17 17=100
							int newpts = puntos;
							if (newpts > 17) {
								newpts = 17;
							}
							t = (newpts * 17) / (necesario);
							ptsant += necesario;
							porcent = t;
							siguiente = false;
							break;
						case 2:
						case 3:
						case 4:
						case 5:
						case 6:
						case 7:
						case 8:
							if (!siguiente) {
								break;
							}
							vl = dividen;
							newpts = puntos - ptsant;
							t = (newpts * vl * 100) / ((necesario - ptsant) * vl);
							b = (t * vl) / (100);
							ptsant = necesario;
							if (b > vl) {
								b = vl;
							}
							porcent += b;
							siguiente = false;
							break;
						}

					}
					int vip = Integer.parseInt(valores.split(",")[3]);
					String tipo = valores.split(",")[2];
					String extrast = "";
					int idespecc = Integer.parseInt(valores.split(",")[0]);
					if (tipo.equals("item")) {
						extrast = MundoDofus.getObjModelo(idespecc).getStringStatsObj().replace(",", "^");
					}
					str.append(ix + ";" + tipo + ";" + desbloq + ";" + necesario + ";" + canjeados + ";" + idespecc
							+ ";" + vip + ";" + extrast);// id orden, tipo, desbloqueado,necesario,canjeado,id objeto
					prim = true;
				}
				str.append(",info;" + porcent + ";" + _perso.getCuenta().getVIP() + ";" + extra + ";" + maxi + ";"
						+ _perso.PuntosPrestigio);
				GestorSalida.enviar(_perso, "#Oo" + str.toString());
				break;
			case 'Y':// TITULOS
				int titul = 0;
				try {
					titul = Integer.parseInt(packet.substring(2));
				} catch (Exception e) {
					return;
				}
				boolean pasa = false;
				if (titul == 0) {
					pasa = true;
				}
				if (_perso._titulos.contains(titul) || pasa) {
					_perso.setTitulo(titul);
					if (_perso.getPelea() == null) {
						GestorSalida.ENVIAR_GM_REFRESCAR_PJ_EN_MAPA(_perso.getMapa(), _perso);
					}
				}
				break;
			case 'C':
				String[] colores = packet.substring(2).split(";");
				int puntoss = GestorSQL.getPuntosCuenta(_perso.getCuenta().getID());
				if (puntoss >= 5) {
					GestorSQL.setPuntoCuenta((puntoss) - (5), _perso.getCuenta().getID());
					_perso.setColores(Integer.parseInt(colores[0]), Integer.parseInt(colores[1]),
							Integer.parseInt(colores[2]));
					GestorSalida.ENVIAR_GM_REFRESCAR_PJ_EN_MAPA(_perso.getMapa(), _perso);
				} else {
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 130),
							Colores.ROJO);
				}
				break;

			/*
			 * case 'D' : try { String[] str = packet.substring(2).split(";"); if
			 * (packet.substring(2).isEmpty()) { _perso.getTitulo("");
			 * GestorSalida.ENVIAR_GM_REFRESCAR_PJ_EN_MAPA(_perso.getMapa(), _perso);
			 * return; } if (str[0].isEmpty()) { _perso.getTitulo("");
			 * GestorSalida.ENVIAR_GM_REFRESCAR_PJ_EN_MAPA(_perso.getMapa(), _perso);
			 * return; } String nombre = str[0]; int color = 0; try { color =
			 * Integer.parseInt(str[1]); if (color > 16777215) color = 0; } catch (Exception
			 * e) { return; } if (nombre.length() > 25) return; String abcMin =
			 * "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ "; for (char
			 * letra : nombre.toCharArray()) if (!abcMin.contains(letra + "")) return;
			 * _perso.getTitulo(nombre + "*" + color);
			 * GestorSalida.ENVIAR_GM_REFRESCAR_PJ_EN_MAPA(_perso.getMapa(), _perso); }
			 * catch (Exception e) { _perso.getTitulo("");
			 * GestorSalida.ENVIAR_GM_REFRESCAR_PJ_EN_MAPA(_perso.getMapa(), _perso); }/*
			 *
			 * case 'N' : //if (_perso != null && _perso.getPelea() == null &&
			 * !_perso.estaOcupado()) // cambiar_Nombre(packet.substring(2)); break; case
			 * 'R': String extra1 =
			 * " ---------- Mapa: "+_perso.getMapa().getID()+" Cell: "+_perso.getCelda().
			 * getID()+" Pelea: "+_perso.getPelea()+" Ocupado: "+_perso.estaOcupado()
			 * +" HaciendoTrabajo: "+_perso.getHaciendoTrabajo()+" Intercambio: "+_perso.
			 * getIntercambiandoCon()+" Koliseo: "+_perso.getKoliseo(); switch
			 * (packet.charAt(3)) { case '1': GestorSQL.INSERT_DENUNCIAS(_perso.getNombre(),
			 * "REPORTE-BUG", packet.substring(4).split("\\|")[1],
			 * packet.substring(4).split("\\|")[2]+extra1); break; case '2':
			 * GestorSQL.INSERT_DENUNCIAS(_perso.getNombre(), "SUGERENCIA",
			 * packet.substring(4).split("\\|")[1],
			 * packet.substring(4).split("\\|")[2]+extra1); break; case '3':
			 * GestorSQL.INSERT_DENUNCIAS(_perso.getNombre(), "DENUNCIA",
			 * packet.substring(4).split("\\|")[1],
			 * packet.substring(4).split("\\|")[2]+extra1); break; case '4':
			 * GestorSQL.INSERT_DENUNCIAS(_perso.getNombre(), "OTROS",
			 * packet.substring(4).split("\\|")[1],
			 * packet.substring(4).split("\\|")[2]+extra1); }
			 * GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso,
			 * Idiomas.getTexto(_perso.getCuenta().idioma, 131), Colores.VERDE); break;
			 * /*case 'n' : if (_perso._statsOficios.size() > 0) { ArrayList<StatsOficio>
			 * listaStatOficios = new ArrayList<StatsOficio>();
			 * listaStatOficios.addAll(_perso._statsOficios.values());
			 * GestorSalida.ENVIAR_JS_TRABAJO_POR_OFICIO(_perso, listaStatOficios);
			 * GestorSalida.ENVIAR_JX_EXPERINENCIA_OFICIO(_perso, listaStatOficios);
			 * GestorSalida.ENVIAR_JO_OFICIO_OPCIONES(_perso, listaStatOficios); Objeto obj
			 * = _perso.getObjPosicion(1); if (obj != null) { for (StatsOficio statOficio :
			 * listaStatOficios) { Oficio oficio = statOficio.getOficio(); if
			 * (oficio.herramientaValida(obj.getModelo().getID())) {
			 * GestorSalida.ENVIAR_OT_OBJETO_HERRAMIENTA(_out, oficio.getID()); String
			 * strOficioPub = CentroInfo.trabajosOficioTaller(oficio.getID()); if
			 * (_perso.getMapa().esTaller() && _perso._oficioPublico)
			 * GestorSalida.ENVIAR_EW_OFICIO_MODO_INVITACION(_out, "+", _perso.getID(),
			 * strOficioPub); _perso._stringOficiosPublicos = strOficioPub; } } } } break;
			 */
			case 'T': // modo caza
				if (MundoDofus.misionesPvP.containsKey(_perso.getID())) {
					Personaje objetivoc = MundoDofus.getPersonaje(MundoDofus.misionesPvP.get(_perso.getID()));
					_perso.teleport(objetivoc.getMapa().getID(), objetivoc.getCelda().getID());
				} else {
					GestorSalida.ENVIAR_BN_NADA(_perso);
				}
				break;
			case 'd': // lootbox
				if (_perso.getObjModeloNoEquip(70200, 1) != null) {
					_perso.borrarObjetoEliminar(_perso.getObjModeloNoEquip(70200, 1).getID(), 1, true);
					int item1 = randomItem();
					GestorSalida.enviar(_perso, "#kN" + item1);
					ObjetoModelo OM = MundoDofus.getObjModelo(item1);
					if (OM != null) {
						Objeto obj = OM.crearObjDesdeModelo(1, true);
						if (!_perso.addObjetoSimilar(obj, true, -1)) {
							MundoDofus.addObjeto(obj, true);
							_perso.addObjetoPut(obj);
							GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_perso, obj);
						}
					}
					GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso);
				}
				if (_perso.getObjModeloNoEquip(70200, 1) != null) {
					Thread.sleep(1900L);
					GestorSalida.enviar(_perso, "#kf");
				}
				break;
			case 'o': // magear item
				String elelem = packet.substring(2);
				int elemtip = 0;
				try {
					elemtip = Integer.parseInt(elelem);
				} catch (Exception e) {
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, "Error desconocido.", Colores.ROJO);
					return;
				}
				if (elemtip < 0 || elemtip > 4) {
					return;
				}
				Objeto object = _perso.getObjPosicion(CentroInfo.ITEM_POS_ARMA);
				if (object == null) {
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, "No tienes ning�n arma equipada.", Colores.ROJO);
					return;
				}
				String tipomageo = "";
				switch (elemtip) {
				case 1:
					tipomageo = "fuego";
					break;
				case 2:
					tipomageo = "aire";
					break;
				case 3:
					tipomageo = "tierra";
					break;
				case 4:
					tipomageo = "agua";
					break;
				default:
					tipomageo = "fuego";
					return;
				}
				int tieneactual = 0;
				for (EfectoHechizo effect : object.getEfectos()) {
					if (tieneactual != 0) {
						break;
					}
					if (effect.getEfectoID() == 100) {
						tieneactual = 100;
					} else if (effect.getEfectoID() == 98) {
						tieneactual = 98;
					} else if (effect.getEfectoID() == 99) {
						tieneactual = 99;
					} else if (effect.getEfectoID() == 96) {
						tieneactual = 96;
					} else if (effect.getEfectoID() == 97) {
						tieneactual = 97;
					}
				}
				int elemnt = 0;
				for (int i = 0; i < object.getEfectos().size(); i++) {
					if (elemnt != 0) {
						break;
					}
					switch (elemtip) {
					case 1:
						elemnt = 99;
						break;
					case 2:
						elemnt = 98;
						break;
					case 3:
						elemnt = 97;
						break;
					case 4:
						elemnt = 96;
						break;
					}
				}
				if (elemnt == 0) {
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso,
							"Hubo un problema al maguear este arma, intenta perfeccionarlo e intenta de nuevo.",
							Colores.ROJO);
					return;
				}
				for (EfectoHechizo EH : object.getEfectos()) {
					if (EH.getEfectoID() >= 96 && EH.getEfectoID() <= 100) {
						String[] infos = EH.getArgs().split(";");
						try {
							int min = Integer.parseInt(infos[0], 16);
							int max = Integer.parseInt(infos[1], 16);
							int nuevoMin = (min * 90) / 100;
							int nuevoMax = (max * 90) / 100;
							if (nuevoMin == 0) {
								nuevoMin = 1;
							}
							String nuevoRango = "1d" + (nuevoMax - nuevoMin + 1) + "+" + (nuevoMin - 1);
							String nuevosArgs = Integer.toHexString(nuevoMin) + ";" + Integer.toHexString(nuevoMax)
									+ ";-1;-1;0;" + nuevoRango;
							EH.setArgs(nuevosArgs);
							EH.setEfectoID(elemnt);
						} catch (Exception e) {
						}
					}
				}
				GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(_out, object);
				GestorSQL.SALVAR_OBJETO(object);
				GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
				GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
						"Su objeto acaba de ser mageado a " + tipomageo + " pero ha perdido un 10% de su daÑo.");
				break;
			case 't': // teleport donde prisma
				_perso.abrirMenuPrisma();
				break;
			case 'K': // TODO: Maitre
				crearMaitre(_perso);
				break;
			case 'L':// limpieza celdas
				if (_perso.getMapa().getID() == 25047) {
					GestorSalida.enviar(_perso, "#bS");
					break;
				}
				String datos = packet.substring(2);
				String limpieza = "";
				String agregaceldas = "";
				try {
					limpieza = datos.split(";")[0];
				} catch (Exception e) {
					limpieza = "";
				}
				try {
					agregaceldas = datos.split(";")[1];
				} catch (Exception e) {
					agregaceldas = "";
				}
				Mapa mapas = _perso.getMapa();
				Casa cacs = MundoDofus.getCasaDentroPorMapa(mapas.getID());
				boolean excepcione2s = false;
				if (_perso.getCuenta().getRango() >= 5) {
					excepcione2s = true;
				}
				if ((cacs == null || cacs.getDueÑoID() != _perso.getCuentaID()) && !excepcione2s) {
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 73),
							Colores.ROJO);
					break;
				}
				Pelea pel = _perso.getPelea();
				if (pel != null) {
					return;
				}
				if (!limpieza.equals("")) {
					Encriptador.liberaEspacio(mapas, limpieza, _perso);
				}
				if (!agregaceldas.equals("")) {
					Encriptador.liberaEspacio(mapas, agregaceldas, _perso);
				}
				for (Personaje px : mapas.getPersos()) {
					GestorSalida.enviar(px, "#bh");
					px.teleport(px.getMapa().getID(), px.getCelda().getID());
				}
				break;
			default:
				break;
			}
		} catch (Exception e) {
			Emu.creaLogs(e);
		}
	}

	public static void crearMaitre(Personaje _perso) {
		if (_perso.liderMaitre) {
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 132),
					Colores.VERDE);
			return;
		}
		if (_perso.getGrupo() == null) {
			return;
		}
		if (_perso.getPelea() != null) {
			GestorSalida.ENVIAR_BN_NADA(_perso);
			return;
		}
		if (_perso.esMaitre) {
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 133),
					Colores.VERDE);
			return;
		}
		if (_perso.getKoliseo() != null) {
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso,
					"No puedes unirte crear un grupo de Maitre estando en un Koliseo", Emu.COLOR_MENSAJE);
			return;
		}
		if (_perso.Marche) {
			return;
		}
		boolean todopasa = true;
		boolean todopasa2 = true;
		for (Personaje grp : _perso.getGrupo().getPersos()) {
			if (grp == _perso) {
				continue;
			}
			if (grp.getMapa().getID() != _perso.getMapa().getID()) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 134),
						Colores.VERDE);
				todopasa2 = false;
				break;
			}
			if (grp.getCuenta().getActualIP().compareTo(_perso.getCuenta().getActualIP()) == 0) {
				grp.esMaitre = true;
			} else {
				todopasa = false;
				break;
			}
		}
		if (!todopasa2) {
			return;
		}
		if (todopasa) {
			_perso.liderMaitre = true;
			_perso.esMaitre = false;
			for (Personaje grp : _perso.getGrupo().getPersos()) {
				if (grp == _perso) {
					continue;
				}
				grp.teleport(_perso.getMapa().getID(), _perso.getCelda().getID());
			}
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 135),
					Colores.VERDE);
		} else {
			for (Personaje grp : _perso.getGrupo().getPersos()) {
				if (grp == _perso) {
					continue;
				}
				grp.esMaitre = false;
			}
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 136), Colores.ROJO);
		}
	}

	private static int randomItem() {
		Random randomGenerator = new Random();
		int item = randomGenerator.nextInt(Emu.objetosLoot.size());
		int itemrnd = Emu.objetosLoot.get(item);
		return itemrnd;
	}

	public static int randomConsumible() {
		Random randomGenerator = new Random();
		int item = randomGenerator.nextInt(Emu.objetosLoot2.size());
		int itemrnd = Emu.objetosLoot2.get(item);
		return itemrnd;
	}

	public static void creaMimos(final Personaje _perso, final PrintWriter _out, final String packet) {
		final int puntoss = GestorSQL.getPuntosCuenta(_perso.getCuenta().getID());
		if (puntoss >= 50) {
			final String valores = packet.substring(3);
			final int original = Integer.parseInt(valores.split("\\|")[0]);
			final int aspectoa = Integer.parseInt(valores.split("\\|")[1]);
			final Objeto objorig = _perso.getObjNoEquip(original, 1);
			final Objeto aspectooa = _perso.getObjNoEquip(aspectoa, 1);
			if (objorig == null || aspectooa == null) {
				return;
			}
			if (objorig.getTextoStat().containsKey(2000) || aspectooa.getTextoStat().containsKey(2000)) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 139), "C60914");
				return;
			}
			boolean pasa = false;
			switch (objorig.getModelo().getTipo()) {
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 16:
			case 17:
			case 18:
			case 19:
			case 21:
			case 22:
			case 81:
			case 82: {
				pasa = true;
				break;
			}
			}
			if (!pasa) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 140), "C60914");
				return;
			}
			if (objorig.getObjeviID() > 0 || aspectooa.getObjeviID() > 0) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 141), "C60914");
				return;
			}
			boolean origesval = false;
			switch (objorig.getModelo().getTipo()) {
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 16:
			case 17:
			case 18:
			case 19:
			case 21:
			case 22:
			case 81:
			case 82: {
				origesval = true;
				break;
			}
			}
			boolean aspectoesval = false;
			switch (aspectooa.getModelo().getTipo()) {
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 16:
			case 17:
			case 18:
			case 19:
			case 21:
			case 22:
			case 81:
			case 82: {
				aspectoesval = true;
				break;
			}
			}
			if ((!origesval && aspectoesval) || (!aspectoesval && origesval) || (!origesval && !aspectoesval)) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 142), "C60914");
				return;
			}
			final Objeto.ObjetoModelo OM = MundoDofus.getObjModelo(objorig.getIDModelo());
			if (OM == null) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 23), "C60914");
				return;
			}
			Objeto obj = null;
			if (OM.getTipo() == 18) {
				obj = OM.crearObjDesdeModelo(1, objorig.convertirStatsAString(2));
			} else {
				obj = OM.crearObjDesdeModelo(1, objorig.convertirStatsAString(0));
			}
			obj.addTextoStat(2000,
					String.valueOf(aspectooa.getModelo().getNombre()) + " (" + aspectooa.getIDModelo() + ")");
			_perso.borrarObjetoEliminar(objorig.getID(), 1, true);
			_perso.borrarObjetoEliminar(aspectooa.getID(), 1, true);
			if (!_perso.addObjetoSimilar(obj, true, -1)) {
				MundoDofus.addObjeto(obj, true);
				_perso.addObjetoPut(obj);
				GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_perso, obj);
			}
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 143), "237B3B");
			GestorSQL.setPuntoCuenta(puntoss - 50, _perso.getCuenta().getID());
		} else {
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 144), "C60914");
		}
	}
}
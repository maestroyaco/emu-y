package estaticos;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.Timer;

import servidor.Colores;
import servidor.EntradaPersonaje;
import servidor.ServidorPersonaje;
import variables.Animacion;
import variables.Casa;
import variables.Cofre;
import variables.Cuenta;
import variables.Dragopavo;
import variables.EfectoHechizo;
import variables.EjecutarBotDiscord;
import variables.Encarnacion;
import variables.Gremio;
import variables.Gremio.MiembroGremio;
import variables.Hechizo;
import variables.Idiomas;
import variables.Interactivos;
import variables.LibrosRunicos;
import variables.Mapa;
import variables.Mapa.Cercado;
import variables.Mascota;
import variables.Mascota.MascotaModelo;
import variables.Mercadillo;
import variables.Mercadillo.ObjetoMercadillo;
import variables.MobModelo;
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
import variables.Personaje;
import variables.Personaje.Stats;
import variables.PiedraDeAlma;
import variables.Prisma;
import variables.RankingPVP;
import variables.Recaudador;
import variables.Reto;
import variables.Sucess;
import variables.Tienda;
import variables.Tutorial;

public class MundoDofus {
	private static Map<Integer, Cuenta> Cuentas = new TreeMap<>();
	private static Map<String, Cuenta> CuentasPorNombre = new TreeMap<>();
	private static Map<Integer, Personaje> Personajes = new TreeMap<>();
	public static Map<String, Personaje> PersonajesPorNombre = new TreeMap<>();
	static Map<Integer, Mapa> Mapas = new TreeMap<>();
	private static Map<Integer, Objeto> Objetos = new TreeMap<>();
	private static Map<Integer, ExpNivel> Experiencia = new TreeMap<>();
	private static Map<Integer, Hechizo> Hechizos = new TreeMap<>();
	private static Map<Integer, ObjetoModelo> ObjModelos = new TreeMap<>();
	private static Map<Integer, Objevivo> Objevivos = new TreeMap<>();
	private static Map<Integer, MobModelo> MobModelos = new TreeMap<>();
	private static Map<Integer, NPCModelo> NPCModelos = new TreeMap<>();
	private static Map<Integer, PreguntaNPC> NPCPreguntas = new TreeMap<>();
	private static Map<Integer, RespuestaNPC> NPCRespuesta = new TreeMap<>();
	private static Map<Integer, ObjInteractivoModelo> ObjInteractivos = new TreeMap<>();
	private static Map<Integer, Dragopavo> Dragopavos = new TreeMap<>();
	private static Map<Integer, SuperArea> SuperAreas = new TreeMap<>();
	private static Map<Integer, Area> Areas = new TreeMap<>();
	private static Map<Integer, SubArea> SubAreas = new TreeMap<>();
	private static Map<Integer, Oficio> Oficios = new TreeMap<>();
	private static Map<Integer, ArrayList<Duo<Integer, Integer>>> Recetas = new TreeMap<>();
	private static Map<Integer, ItemSet> ItemSets = new TreeMap<>();
	private static Map<Integer, Gremio> Gremios = new TreeMap<>();
	private static Map<Integer, Casa> Casas = new TreeMap<>();
	private static Map<Integer, Mercadillo> PuestosMercadillos = new TreeMap<>();
	private static Map<Integer, Map<Integer, ArrayList<ObjetoMercadillo>>> ObjMercadillos = new HashMap<>();
	private static Map<Integer, Animacion> Animaciones = new TreeMap<>();
	private static Map<Integer, Reto> Retos = new TreeMap<>();
	private static Map<Integer, Tienda> Tiendas = new TreeMap<>();
	static Map<Integer, Cercado> Cercados = new TreeMap<>();
	private static Map<Integer, Prisma> Prismas = new TreeMap<>();
	private static Map<Integer, Cofre> Cofres = new TreeMap<>();
	private static Map<Integer, Recaudador> Recaudadores = new TreeMap<>();
	private static Map<Integer, RankingPVP> RankingsPVP = new TreeMap<>();
	private static Map<Integer, Encarnacion> Encarnaciones = new TreeMap<>();
	private static Map<Integer, LibrosRunicos> LibrosRunicos = new TreeMap<>();
	private static Map<Integer, Tutorial> Tutoriales = new TreeMap<>();
	private static Map<Integer, Mascota> Mascotas = new TreeMap<>();
	private static Map<Integer, MascotaModelo> MascotasModelos = new TreeMap<>();
	public static String liderRanking = "Ninguno";
	public static String liderRanking2 = "Ninguno";
	public static String liderRanking3 = "Ninguno";
	private static int sigIDLineaMerca;
	private static int sigIDObjeto;
	private static int sigIDCofre = 0;
	private static short _estado = 1;
	private static Map<Integer, String> misionEtapa = new HashMap<>();
	private static Map<Integer, String> misionObjetivo = new HashMap<>();
	private static Map<Integer, String> misionObjetivos = new HashMap<>();
	static ArrayList<Integer> mapasCargar = new ArrayList<>();
	public static Map<String, String> comandosEmu = new HashMap<>();
	public static Map<Integer, Integer> misionesPvP = new HashMap<>();
	private static ArrayList<Integer> estrellas = new ArrayList<>();
	// public static ArrayList<Integer> _fusionados = new ArrayList<Integer>();
	static Map<Integer, String> itemsRegalos = new HashMap<>();
	public static Map<Integer, String> botsServer = new HashMap<>();
	public static Map<String, Integer> idDebots = new HashMap<>();
	public static String rankingNivel;
	public static String rankingGremios;
	public static ArrayList<Integer> mapasBoost = new ArrayList<>();
	public static Map<Integer, String> ObjetivosDiarios = new HashMap<>();
	public static Map<Integer, String> ObjetivosFaciles = new HashMap<>();
	public static ArrayList<Integer> mapasSalvar = new ArrayList<>();
	public static Mapa antesmap = null;
	public static boolean angel = true;
	// static ArrayList<Integer> mazmorrasDiarias = new ArrayList<Integer>();
	// public static ArrayList<Integer> mazmorrasElegidas = new
	// ArrayList<Integer>();
	static int diaMazmo = 0;
	private static int zonas[] = { 7344, 7360, 7376, 7392, 7408, 7441, 7458, 7460, 7461, 7462, 7446, 7430, 7383, 7367,
			7351, 7334, 7381, 7385, 7353, 7448, 7609, 7456, 7313, 7299, 7318, 7305 };
	public static Map<Personaje, Integer> personajePuntos = new HashMap<>();
	public static boolean eventoJalato = false;
	public static String objetosMercader = "";
	public static int activo = 0;
	public static boolean gemaRoja = false;
	public static Mapa mapaEvento = null;
	public static Mapa mapaPortal = null;
	public static Map<Integer, Sucess> SUCCESS = new TreeMap<>();
	public static boolean empiezaPortal = false;
	public static Mapa mapaMercader = null;
	public static Map<String, Integer> comprasMercader = new HashMap<>();
	public static boolean esKamas = true;
	public static boolean eventoLarvas = false;
	public static ArrayList<Personaje> larva1 = new ArrayList<>();
	public static ArrayList<Personaje> larva2 = new ArrayList<>();
	public static ArrayList<Personaje> larva3 = new ArrayList<>();
	public static ArrayList<Personaje> larva4 = new ArrayList<>();
	public static int boteLarva = 0;
	public static ArrayList<Personaje> ipsLarvas = new ArrayList<>();
	public static Map<Integer, String> Prestigios = new HashMap<>();


	//panel de comandos y zonas
	public static String LISTA_ZONAS = "",LISTA_COMANDOS = "";
	public static Map<Short, Short> ZONAS = new HashMap<>();

	// panel de comandos y zonas

	public static String getHechizoAprender(int etapa, Personaje perso) {
		StringBuilder hechizos = new StringBuilder("");
		ArrayList<Integer> hechizosx = null;
		switch (etapa) {
		case 1:
			hechizosx = new ArrayList<>(Arrays.asList(3, 23, 51, 65, 81, 102, 125, 142, 169, 193, 431, 686));
			break;
		case 2:
			hechizosx = new ArrayList<>(Arrays.asList(17, 34, 41, 72, 83, 103, 128, 143, 164, 183, 432, 692));
			break;
		case 3:
			hechizosx = new ArrayList<>(Arrays.asList(6, 21, 43, 61, 82, 105, 121, 141, 161, 200, 434, 687));
			break;
		case 4:
			hechizosx = new ArrayList<>(Arrays.asList(4, 26, 49, 66, 84, 109, 124, 144, 163, 198, 444, 689));
			break;
		case 5:
			hechizosx = new ArrayList<>(Arrays.asList(2, 22, 42, 68, 100, 113, 122, 145, 165, 195, 449, 690));
			break;
		case 6:
			hechizosx = new ArrayList<>(Arrays.asList(1, 35, 47, 63, 92, 111, 126, 146, 172, 182, 436, 691));
			break;
		case 7:
			hechizosx = new ArrayList<>(Arrays.asList(9, 28, 48, 74, 88, 104, 127, 147, 167, 192, 437, 688));
			break;
		case 8:
			hechizosx = new ArrayList<>(Arrays.asList(18, 37, 45, 64, 93, 119, 123, 148, 168, 197, 439, 693));
			break;
		case 9:
			hechizosx = new ArrayList<>(Arrays.asList(20, 30, 53, 79, 85, 101, 130, 154, 162, 189, 433, 694));
			break;
		case 10:
			hechizosx = new ArrayList<>(Arrays.asList(14, 27, 46, 78, 96, 107, 131, 150, 170, 181, 443, 695));
			break;
		case 11:
			hechizosx = new ArrayList<>(Arrays.asList(19, 24, 52, 71, 98, 116, 132, 151, 171, 199, 440));
			break;
		case 12:
			hechizosx = new ArrayList<>(Arrays.asList(5, 33, 44, 62, 86, 106, 133, 155, 166, 191, 442, 697));
			break;
		case 13:
			hechizosx = new ArrayList<>(Arrays.asList(16, 25, 50, 69, 89, 117, 134, 152, 173, 186, 442, 698));
			break;
		case 14:
			hechizosx = new ArrayList<>(Arrays.asList(8, 38, 54, 77, 90, 108, 135, 153, 174, 196, 445, 699));
			break;
		case 15:
			hechizosx = new ArrayList<>(Arrays.asList(12, 36, 55, 73, 87, 115, 129, 149, 176, 190, 438, 700));
			break;
		case 16:
			hechizosx = new ArrayList<>(Arrays.asList(11, 32, 56, 67, 94, 118, 136, 156, 175, 194, 446, 701));
			break;
		case 17:
			hechizosx = new ArrayList<>(Arrays.asList(10, 29, 58, 70, 99, 110, 137, 157, 178, 185, 447, 702));
			break;
		case 18:
			hechizosx = new ArrayList<>(Arrays.asList(7, 39, 59, 75, 95, 112, 138, 158, 177, 184, 448, 703));
			break;
		case 19:
			hechizosx = new ArrayList<>(Arrays.asList(15, 40, 57, 76, 91, 114, 139, 160, 179, 188, 435, 704));
			break;
		case 20:
			hechizosx = new ArrayList<>(Arrays.asList(13, 31, 60, 80, 97, 120, 140, 159, 180, 187, 450, 705));
			break;
		case 21:
			hechizosx = new ArrayList<>(
					Arrays.asList(1901, 1902, 1903, 1904, 1905, 1906, 1907, 1908, 1909, 1910, 1911, 1912));
			break;
		default:
			return "";
		}
		Collections.shuffle(hechizosx);
		int veces = 0;
		for (Integer entry : hechizosx) {
			if (veces >= 5) {
				break;
			}
			if (hechizos.toString().isEmpty()) {
				hechizos.append(entry);
			} else {
				hechizos.append(";" + entry);
			}
			veces += 1;
		}
		return hechizos.toString();
	}

	static void carreraLarvas() {
		boteLarva = 0;
		ipsLarvas.clear();
		larva1.clear();
		larva2.clear();
		larva3.clear();
		larva4.clear();
		empezocarrera = false;
		if (eventoLarvas) {
			eventoLarvas = false;
			Mapa mapa = MundoDofus.getMapa(6869);

			if (mapa == null) {
				return;
			}
			ArrayList<Integer> idsBorra = new ArrayList<>();
			for (Entry<Integer, NPC> entry : mapa.getNPCs().entrySet()) {
				int id = entry.getKey();
				NPC npc = mapa.getNPC(id);
				if (id == 0 || npc == null) {
					break;
				}
				idsBorra.add(id);
			}
			for (Integer ids : idsBorra) {
				GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(mapa, ids);
				mapa.borrarNPCoGrupoMob(ids);
			}
		} else {
			eventoLarvas = true;
			Mapa mapa = MundoDofus.getMapa(6869);
			if (mapa == null) {
				return;
			}
			NPC npc = mapa.addNPC(537, 224, 3);
			NPC npc2 = mapa.addNPC(541, 233, 1);
			NPC npc3 = mapa.addNPC(542, 177, 1);
			NPC npc4 = mapa.addNPC(538, 121, 1);
			NPC npc5 = mapa.addNPC(540, 65, 1);
			NPC npc14 = mapa.addNPC(843, 280, 1);
			ArrayList<Integer> lista = new ArrayList<>();
			lista.add(413);
			lista.add(357);
			lista.add(301);
			lista.add(245);
			Collections.shuffle(lista);
			NPC npc10 = null;
			NPC npc11 = null;
			NPC npc12 = null;
			NPC npc13 = null;
			for (Integer inte : lista) {
				if (npc10 == null) {
					npc10 = mapa.addNPC(844, inte, 1);
				} else {
					if (npc11 == null) {
						npc11 = mapa.addNPC(845, inte, 1);
					} else {
						if (npc12 == null) {
							npc12 = mapa.addNPC(846, inte, 1);
						} else {
							if (npc13 == null) {
								npc13 = mapa.addNPC(847, inte, 1);
							}
						}
					}
				}
			}

			GestorSalida.ENVIAR_GM_AGREGAR_NPC_AL_MAPA(mapa, npc, null);
			GestorSalida.ENVIAR_GM_AGREGAR_NPC_AL_MAPA(mapa, npc2, null);
			GestorSalida.ENVIAR_GM_AGREGAR_NPC_AL_MAPA(mapa, npc3, null);
			GestorSalida.ENVIAR_GM_AGREGAR_NPC_AL_MAPA(mapa, npc4, null);
			GestorSalida.ENVIAR_GM_AGREGAR_NPC_AL_MAPA(mapa, npc5, null);

			GestorSalida.ENVIAR_GM_AGREGAR_NPC_AL_MAPA(mapa, npc10, null);
			GestorSalida.ENVIAR_GM_AGREGAR_NPC_AL_MAPA(mapa, npc11, null);
			GestorSalida.ENVIAR_GM_AGREGAR_NPC_AL_MAPA(mapa, npc12, null);
			GestorSalida.ENVIAR_GM_AGREGAR_NPC_AL_MAPA(mapa, npc13, null);
			GestorSalida.ENVIAR_GM_AGREGAR_NPC_AL_MAPA(mapa, npc14, null);
			TimerLarvas();
			for (Personaje perso : MundoDofus.getPJsEnLinea()) {
				if (perso == null) {
					continue;
				}
				String extrapacket = "#GS6";
				String packet = "cs<font color='#" + Colores.ROSA + "'>"
						+ Idiomas.getTexto(perso.getCuenta().idioma, 209) + "</font>";
				GestorSalida.enviar(perso, packet + "" + ((char) 0x00) + extrapacket);
			}
		}
	}

	private static void mueveLarva(int idespecialx, int celdaactual, int celdadestino, Mapa mapa) {
		int idespecial = idespecialx;
		int celdapj = celdaactual;
		int cell = celdadestino;
		String pathstr = Camino.getShortestStringPathBetween(mapa, celdapj, cell, 0);
		if (pathstr == null) {
			return;
		}
		String packet = "GA0;1;" + idespecial + ";" + pathstr;
		for (Personaje persos : mapa.getPersos()) {
			if (persos != null && persos.getPelea() == null) {
				GestorSalida.enviar(persos, packet);
			}
		}
	}

	public static boolean empezocarrera = false;

	private static void empiezaCarrera() {
		Timer timer = new Timer(1500, new ActionListener() {
			boolean nocierrasoloespera = false;
			int paso = 0;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!eventoLarvas) {
					paso = 0;
					((Timer) e.getSource()).stop();
					return;
				}
				empezocarrera = true;
				if (nocierrasoloespera) {
					return;
				}
				Mapa mapa = MundoDofus.getMapa(6869);
				int npcs[] = { 538, 540, 541, 542 };
				int randomNPC = Formulas.getRandomValor(0, 3);
				int elecnpc = npcs[randomNPC];
				int elnpcid = 0;
				int fuegosart = 0;
				boolean encuentra = false;
				for (Entry<Integer, NPC> entry : mapa.getNPCs().entrySet()) {
					if (entry.getValue().getModeloBD().getID() == elecnpc) {
						int id = entry.getKey();
						NPC npc = mapa.getNPC(id);
						if (id == 0 || npc == null) {
							break;
						}
						elnpcid = id;
						encuentra = true;
						break;
					}
				}
				for (Entry<Integer, NPC> entry : mapa.getNPCs().entrySet()) {
					if (entry.getValue().getModeloBD().getID() == 843) {
						int id = entry.getKey();
						NPC npc = mapa.getNPC(id);
						if (id == 0 || npc == null) {
							break;
						}
						fuegosart = id;
						break;
					}
				}
				if (encuentra) {
					NPC npc = mapa.getNPC(elnpcid);
					int siguientecelda = Camino.getSigIDCeldaMismaDir(npc.getCeldaID(), 'b', mapa, false);
					mueveLarva(elnpcid, npc.getCeldaID(), siguientecelda, mapa);
					npc.setCeldaID(siguientecelda);
					boolean acabo = false;
					String ganadora = "";
					int cantidadganado = Formulas.getRandomValor(250, 450);
					if (siguientecelda == 230) {// larva zafiro
						for (Personaje px : larva1) {
							if (px == null) {
								continue;
							}
							GestorSQL.setPuntoCuenta(GestorSQL.getPuntosCuenta(px.getCuentaID()) + cantidadganado,
									px.getCuentaID());
							GestorSalida.ENVIAR_cs_CHAT_MENSAJE(px,
									"Has ganado <b>" + cantidadganado + "</b> Puntos Vip!!!", Colores.VERDE);
							px.addTitulos(135);
						}
						ganadora = "Larva Zafiro";
						acabo = true;
					} else if (siguientecelda == 286) { // larva ex campeona
						for (Personaje px : larva2) {
							if (px == null) {
								continue;
							}
							GestorSQL.setPuntoCuenta(GestorSQL.getPuntosCuenta(px.getCuentaID()) + cantidadganado,
									px.getCuentaID());
							GestorSalida.ENVIAR_cs_CHAT_MENSAJE(px,
									"Has ganado <b>" + cantidadganado + "</b> Puntos Vip!!!", Colores.VERDE);
							px.addTitulos(135);
						}
						ganadora = "Larva Ex-Campeona";
						acabo = true;
					} else if (siguientecelda == 342) { // larva esmeralda
						for (Personaje px : larva3) {
							if (px == null) {
								continue;
							}
							GestorSQL.setPuntoCuenta(GestorSQL.getPuntosCuenta(px.getCuentaID()) + cantidadganado,
									px.getCuentaID());
							GestorSalida.ENVIAR_cs_CHAT_MENSAJE(px,
									"Has ganado <b>" + cantidadganado + "</b> Puntos Vip!!!", Colores.VERDE);
							px.addTitulos(135);
						}
						ganadora = "Larva Esmeralda";
						acabo = true;
					} else if (siguientecelda == 398) { // larva rubi
						for (Personaje px : larva4) {
							if (px == null) {
								continue;
							}
							GestorSQL.setPuntoCuenta(GestorSQL.getPuntosCuenta(px.getCuentaID()) + cantidadganado,
									px.getCuentaID());
							GestorSalida.ENVIAR_cs_CHAT_MENSAJE(px,
									"Has ganado <b>" + cantidadganado + "</b> Puntos Vip!!!", Colores.VERDE);
							px.addTitulos(135);
						}
						ganadora = "Larva Rub�";
						acabo = true;
					}
					if (acabo) {
						int entrenadorid = 0;
						for (Entry<Integer, NPC> entry : mapa.getNPCs().entrySet()) {
							if (entry.getValue().getModeloBD().getID() == 537) {
								int id = entry.getKey();
								NPC npc1 = mapa.getNPC(id);
								if (id == 0 || npc1 == null) {
									break;
								}
								entrenadorid = id;
								break;
							}
						}
						for (Personaje perso : MundoDofus.getPJsEnLinea()) {
							if (perso == null) {
								continue;
							}
							String packet = "cs<font color='#" + Colores.ROSA + "'>"
									+ Idiomas.getTexto(perso.getCuenta().idioma, 211) + "</font>";
							GestorSalida.enviar(perso, packet);
						}
						for (Personaje persos : mapa.getPersos()) {
							if (persos != null && persos.getPelea() == null) {
								GestorSalida.enviar(persos, "cMK|" + entrenadorid + "|Entrenador|La " + ganadora
										+ " gana, gracias a todos por participar!!!|");
								Interactivos.fuegosArficiales(fuegosart, persos, siguientecelda);
							}
						}
						try {
							Thread.sleep(5000);
						} catch (Exception e1) {
						}
						ArrayList<Integer> idsBorra = new ArrayList<>();
						for (Entry<Integer, NPC> entry : mapa.getNPCs().entrySet()) {
							int id = entry.getKey();
							NPC npc1 = mapa.getNPC(id);
							if (id == 0 || npc1 == null) {
								break;
							}
							idsBorra.add(id);
						}
						for (Integer ids : idsBorra) {
							GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(mapa, ids);
							mapa.borrarNPCoGrupoMob(ids);
						}
						larva1.clear();
						larva2.clear();
						larva3.clear();
						larva4.clear();
						eventoLarvas = false;
						nocierrasoloespera = true;
						paso = 0;
						((Timer) e.getSource()).stop();
						return;
					}
				}
				paso += 1;
				if (paso >= 5000) {
					paso = 0;
					((Timer) e.getSource()).stop();
					return;
				}
			}
		});
		timer.start();
	}

	private static void TimerLarvas() {
		Timer timer = new Timer(1000, new ActionListener() {
			int paso = 0;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!eventoLarvas) {
					paso = 0;
					((Timer) e.getSource()).stop();
					return;
				}
				paso += 1;
				if (paso >= 180) {
					for (Personaje perso : MundoDofus.getPJsEnLinea()) {
						if (perso == null) {
							continue;
						}
						String packet = "cs<font color='#" + Colores.ROSA + "'>"
								+ Idiomas.getTexto(perso.getCuenta().idioma, 210) + "</font>";
						GestorSalida.enviar(perso, packet);
					}
					empiezaCarrera();
					paso = 0;
					((Timer) e.getSource()).stop();
					return;
				}
			}
		});
		timer.start();
	}

	static void PeleaJalato() {
		if (!eventoJalato) {
			eventoJalato = true;
			personajePuntos.clear();
			for (Personaje perso : MundoDofus.getPJsEnLinea()) {
				if (perso == null) {
					continue;
				}
				String extrapacket = "#GS6";
				String packet = "cs<font color='#" + Colores.AMARILLO + "'>"
						+ Idiomas.getTexto(perso.getCuenta().idioma, 208) + "</font>";
				GestorSalida.enviar(perso, packet + "" + ((char) 0x00) + extrapacket);

			}
			//BOT DISCORD EVENTO JALATO
			EjecutarBotDiscord.ejecutarBotDiscord("[EVENTO GLOBAL]: La temporada de caza ha empezado!!! Escribe .evento y caza todos los Jalat�s que puedas!!! Termina en 5 minutos","EVENTO CAZA JALATOS");

			TimerJalato();
		} else {
			eventoJalato = false;
			personajePuntos.clear();
		}
	}

	private static void TimerJalato() {
		Timer timer = new Timer(1000, new ActionListener() {
			int paso = 0;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!eventoJalato) {
					paso = 0;
					((Timer) e.getSource()).stop();
					return;
				}
				paso += 1;
				if (paso >= 300) {
					int maxpuntos = 0;
					int segmaxpts = 0;
					int segmaxpts2 = 0;
					Personaje persoxs = null;
					Personaje persoxs2 = null;
					Personaje persoxs3 = null;
					for (Entry<Personaje, Integer> px : personajePuntos.entrySet()) {
						int cantidad = px.getValue();
						if (cantidad > maxpuntos) {
							persoxs = px.getKey();
							maxpuntos = px.getValue();
						}
					}
					if (persoxs != null) {
						for (Entry<Personaje, Integer> px : personajePuntos.entrySet()) {
							int cantidad = px.getValue();
							if (px.getKey() == persoxs) {
								continue;
							}
							if (cantidad == maxpuntos) {
								persoxs2 = px.getKey();
								segmaxpts = cantidad;
								break;
							} else {
								if (cantidad > segmaxpts) {
									persoxs2 = px.getKey();
									segmaxpts = cantidad;
								}
							}
						}
					}
					if (persoxs2 != null) {
						for (Entry<Personaje, Integer> px : personajePuntos.entrySet()) {
							int cantidad = px.getValue();
							if (px.getKey() == persoxs || px.getKey() == persoxs2) {
								continue;
							}
							if (cantidad == segmaxpts) {
								persoxs3 = px.getKey();
								segmaxpts2 = cantidad;
								break;
							} else {
								if (cantidad > segmaxpts2) {
									persoxs3 = px.getKey();
									segmaxpts2 = cantidad;
								}
							}
						}
					}
					if (persoxs != null) {
						boolean enviarDiscord = true;
						for (Personaje perso : MundoDofus.getPJsEnLinea()) {
							if (perso == null) {
								continue;
							}
							String packet = "";
							if (persoxs != null && persoxs2 != null && persoxs3 != null) {

								packet = "cs<font color='#" + Colores.AMARILLO
										+ "'>El evento de caza ha finalizado!!! Los ganadores del evento son <b>"
										+ persoxs.getNombre() + "</b> con <b>" + maxpuntos + " Puntos</b>, <b>"
										+ persoxs2.getNombre() + "</b> con <b>" + segmaxpts + " Puntos</b> y <b>"
										+ persoxs3.getNombre() + "</b> con <b>" + segmaxpts2 + " Puntos</b></font>";

							//BOT DISCORD EVENTOS
								if (enviarDiscord) {
									enviarDiscord = false;
									EjecutarBotDiscord.ejecutarBotDiscord("El evento de caza ha finalizado!!! Los ganadores del evento son: "
											 + persoxs.getNombre() + " con " + maxpuntos + " Puntos, "
											 + persoxs2.getNombre() + " con " + segmaxpts + " Puntos y "
											  + persoxs3.getNombre() + " con " + segmaxpts2 + " Puntos", "Evento Jalato Finalizado");

								}
								}



							else if (persoxs != null && persoxs2 != null && persoxs3 == null) {
								packet = "cs<font color='#" + Colores.AMARILLO
										+ "'>El evento de caza ha finalizado!!! Los ganadores del evento son <b>"
										+ persoxs.getNombre() + "</b> con <b>" + maxpuntos + " Puntos</b> y <b>"
										+ persoxs2.getNombre() + "</b> con <b>" + segmaxpts + " Puntos</b></font>";

								//BOT DISCORD EVENTOS
								if (enviarDiscord) {
									enviarDiscord = false;
									EjecutarBotDiscord.ejecutarBotDiscord("El evento de caza ha finalizado!!! Los ganadores del evento son: "
											 + persoxs.getNombre() + " con " + maxpuntos + " Puntos, "
											 + persoxs2.getNombre() + " con " + segmaxpts + " Puntos", "Evento Jalato Finalizado");

								}
								}

							else if (persoxs != null && persoxs2 == null && persoxs3 == null) {
								packet = "cs<font color='#" + Colores.AMARILLO
										+ "'>El evento de caza ha finalizado!!! El ganador del evento es <b>"
										+ persoxs.getNombre() + "</b> que ha conseguido un total de <b>" + maxpuntos
										+ "</b> Puntos!!!</font>";
							GestorSalida.enviar(perso, packet);

							//BOT DISCORD EVENTOS
							if (enviarDiscord) {
								enviarDiscord = false;
								EjecutarBotDiscord.ejecutarBotDiscord("El evento de caza ha finalizado!!! El ganador del evento es: "
										+ persoxs.getNombre() + " que ha conseguido un total de " + maxpuntos + " Puntos!!!","EVENTO JALATO FINALIZADO");

							}

							}

						}
						int randomrecompensa = Formulas.getRandomValor(1, 6);
						if (randomrecompensa == 1) {
							int darogr = 350;
							GestorSQL.setPuntoCuenta(GestorSQL.getPuntosCuenta(persoxs.getCuentaID()) + darogr,
									persoxs.getCuentaID());
							GestorSalida.ENVIAR_cs_CHAT_MENSAJE(persoxs, "Has ganado " + darogr + " puntos vip",
									Colores.VERDE);
							if (persoxs2 != null) {
								darogr = 300;
								GestorSQL.setPuntoCuenta(GestorSQL.getPuntosCuenta(persoxs2.getCuentaID()) + darogr,
										persoxs2.getCuentaID());
								GestorSalida.ENVIAR_cs_CHAT_MENSAJE(persoxs2, "Has ganado " + darogr + " puntos vip",
										Colores.VERDE);
							}
							if (persoxs3 != null) {
								darogr = 250;
								GestorSQL.setPuntoCuenta(GestorSQL.getPuntosCuenta(persoxs3.getCuentaID()) + darogr,
										persoxs3.getCuentaID());
								GestorSalida.ENVIAR_cs_CHAT_MENSAJE(persoxs3, "Has ganado " + darogr + " puntos vip",
										Colores.VERDE);
							}
						} else if (randomrecompensa == 2) {
							persoxs.crearItem(980125, 350);
							if (persoxs2 != null) {
								persoxs2.crearItem(980125, 300);
							}
							if (persoxs3 != null) {
								persoxs3.crearItem(980125, 260);
							}
						} else if (randomrecompensa == 3) {
							persoxs.crearItem(10275, 120);
							if (persoxs2 != null) {
								persoxs2.crearItem(10275, 100);
							}
							if (persoxs3 != null) {
								persoxs3.crearItem(10275, 90);
							}
						} else if (randomrecompensa == 4) {
							persoxs.crearItem(35904, 3);
							if (persoxs2 != null) {
								persoxs2.crearItem(35904, 2);
							}
							if (persoxs3 != null) {
								persoxs3.crearItem(35904, 1);
							}
						} else if (randomrecompensa == 5) {
							persoxs.crearItem(36024, 1);
							if (persoxs2 != null) {
								persoxs2.crearItem(36024, 1);
							}
							if (persoxs3 != null) {
								persoxs3.crearItem(36024, 1);
							}
						} else {
							persoxs.crearItem(11158, 250);
							if (persoxs2 != null) {
								persoxs2.crearItem(11158, 200);
							}
							if (persoxs3 != null) {
								persoxs3.crearItem(11158, 170);
							}
						}
					}
					if (persoxs != null) {
						persoxs.addTitulos(136);
					}
					if (persoxs2 != null) {
						persoxs2.addTitulos(136);
					}
					if (persoxs3 != null) {
						persoxs3.addTitulos(136);
					}
					eventoJalato = false;
					personajePuntos.clear();
					((Timer) e.getSource()).stop();
					return;
				}
			}
		});
		timer.start();
	}

	/*
	 * static void elegirMazmo() { //Calendar cal = Calendar.getInstance(); //int
	 * dayOfMonth = cal.get(Calendar.DAY_OF_MONTH); //if (diaMazmo != dayOfMonth) {
	 * //diaMazmo = dayOfMonth; ArrayList<Integer> mazmorrasDiariasC = new
	 * ArrayList<Integer>(); mazmorrasDiariasC.addAll(mazmorrasDiarias);
	 * Collections.shuffle(mazmorrasDiariasC); int mazmos = 0;
	 * mazmorrasElegidas.clear(); for (Integer entry : mazmorrasDiariasC) { if
	 * (mazmos >= 7) break; mazmorrasElegidas.add(entry); mazmos += 1; } //} }
	 */

	public static void Portal() {
		empiezaPortal = false;
		if (mapaPortal == null) {
			Mapa mapa = MundoDofus.getMapa(4769);
			if (mapa == null) {
				return;
			}
			mapa.addNPC(666, 239, 3);
			mapaPortal = mapa;
			for (Personaje perso : MundoDofus.getPJsEnLinea()) {
				if (perso == null) {
					continue;
				}
				String extrapacket = "#GS6";
				String packet = "cs<font color='#" + Colores.NARANJA + "'>"
						+ Idiomas.getTexto(perso.getCuenta().idioma, 185) + "</font>";
				GestorSalida.enviar(perso, packet + "" + ((char) 0x00) + extrapacket);
			}
			TimerPortal();
		} else {
			for (Entry<Integer, NPC> entry : mapaPortal.getNPCs().entrySet()) {
				if (entry.getValue().getModeloBD().getID() == 666) {
					int id = entry.getKey();
					NPC npc = mapaPortal.getNPC(id);
					if (id == 0 || npc == null) {
						break;
					}
					GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(mapaPortal, id);
					mapaPortal.borrarNPCoGrupoMob(id);
					break;
				}
			}
			mapaPortal = null;
		}
	}

	private static void TimerPortal() {
		Timer timer = new Timer(1000, new ActionListener() {
			int paso = 0;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (mapaPortal == null) {
					paso = 0;
					((Timer) e.getSource()).stop();
					return;
				}
				paso += 1;
				if (paso >= 600) {
					for (Entry<Integer, NPC> entry : mapaPortal.getNPCs().entrySet()) {
						if (entry.getValue().getModeloBD().getID() == 666) {
							int id = entry.getKey();
							NPC npc = mapaPortal.getNPC(id);
							if (id == 0 || npc == null) {
								break;
							}
							GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(mapaPortal, id);
							mapaPortal.borrarNPCoGrupoMob(id);
							break;
						}
					}
					for (Personaje perso : MundoDofus.getPJsEnLinea()) {
						if (perso == null) {
							continue;
						}
						String extrapacket = "";
						String packet = "cs<font color='#" + Colores.NARANJA + "'>"
								+ Idiomas.getTexto(perso.getCuenta().idioma, 187) + "</font>";
						GestorSalida.enviar(perso, packet + "" + ((char) 0x00) + extrapacket);
					}
					empiezaPortal = true;
					mapaPortal = null;
					paso = 0;
					((Timer) e.getSource()).stop();
					return;
				}
			}
		});
		timer.start();
	}

	public static void ReclamaGema(Personaje _perso) {
		if (activo == 1) {
			Mapa mapa = _perso.getMapa();
			for (Entry<Integer, NPC> entry : mapa.getNPCs().entrySet()) {
				if (entry.getValue().getModeloBD().getID() == 387 || entry.getValue().getModeloBD().getID() == 386) {
					int id = entry.getKey();
					NPC npc = _perso.getMapa().getNPC(id);
					if (id == 0 || npc == null) {
						break;
					}
					GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(_perso.getMapa(), id);
					_perso.getMapa().borrarNPCoGrupoMob(id);
					break;
				}
			}
			if (!gemaRoja) {
				_perso.crearItem(980125, 100);
			} else {
				int darogr = 100;
				GestorSQL.setPuntoCuenta(GestorSQL.getPuntosCuenta(_perso.getCuentaID()) + darogr,
						_perso.getCuentaID());
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, "Has ganado " + darogr + " puntos vip", Colores.VERDE);
			}
			for (Personaje perso : MundoDofus.getPJsEnLinea()) {
				if (perso == null) {
					continue;
				}
				String packet = "cs<font color='#" + Colores.NARANJA + "'>"
						+ Idiomas.getTexto(perso.getCuenta().idioma, 173) + "</font>";
				GestorSalida.enviar(perso, packet);
			}
			mapaEvento = null;
			activo = 0;
		} else if (activo == 2) {
			Mapa mapa = _perso.getMapa();
			for (Entry<Integer, NPC> entry : mapa.getNPCs().entrySet()) {
				if (entry.getValue().getModeloBD().getID() == 880 || entry.getValue().getModeloBD().getID() == 887) {
					int id = entry.getKey();
					NPC npc = _perso.getMapa().getNPC(id);
					if (id == 0 || npc == null) {
						break;
					}
					GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(_perso.getMapa(), id);
					_perso.getMapa().borrarNPCoGrupoMob(id);
					break;
				}
			}
			if (!esKamas) {
				_perso.crearItem(70200, 1);
			} else {
				_perso.addKamas(5000000);
				GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
			}
			for (Personaje perso : MundoDofus.getPJsEnLinea()) {
				if (perso == null) {
					continue;
				}
				String packet = "";
				if (!esKamas) {
					packet = "cs<font color='#" + Colores.NARANJA + "'>"
							+ Idiomas.getTexto(perso.getCuenta().idioma, 177) + "</font>";
				} else {
					packet = "cs<font color='#" + Colores.NARANJA + "'>"
							+ Idiomas.getTexto(perso.getCuenta().idioma, 176) + "</font>";
				}
				GestorSalida.enviar(perso, packet);
			}
			mapaEvento = null;
			activo = 0;
		}
	}

	private static void BorraAntes() {
		if (mapaEvento != null) {
			Mapa mapa = mapaEvento;
			for (Entry<Integer, NPC> entry : mapa.getNPCs().entrySet()) {
				if (entry.getValue().getModeloBD().getID() == 880 || entry.getValue().getModeloBD().getID() == 887
						|| entry.getValue().getModeloBD().getID() == 387
						|| entry.getValue().getModeloBD().getID() == 386) {
					int id = entry.getKey();
					NPC npc = mapa.getNPC(id);
					if (id == 0 || npc == null) {
						break;
					}
					GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(mapa, id);
					mapa.borrarNPCoGrupoMob(id);
					break;
				}
			}
			mapaEvento = null;
		}
	}

	static void Regalos() {
		BorraAntes();
		activo = 2;
		int tipo = Formulas.getRandomValor(1, 2);
		if (tipo == 1) {
			esKamas = false;
		} else {
			esKamas = true;
		}
		int npcid = 880;
		if (esKamas) {
			npcid = 887;
		}
		int I = Formulas.getRandomValor(0, 25);
		Mapa mapa = MundoDofus.getMapa(zonas[I]);
		if (mapa == null) {
			return;
		}
		mapa.addNPC(npcid, 280, 3);
		mapaEvento = mapa;
		String extrapacket = "#GS4";
		if (esKamas) {
			for (Personaje perso : MundoDofus.getPJsEnLinea()) {
				if (perso == null) {
					continue;
				}
				String packet = "cs<font color='#" + Colores.NARANJA + "'>"
						+ Idiomas.getTexto(perso.getCuenta().idioma, 174) + "</font>";
				GestorSalida.enviar(perso, packet + "" + ((char) 0x00) + extrapacket);
			}
		} else {
			for (Personaje perso : MundoDofus.getPJsEnLinea()) {
				if (perso == null) {
					continue;
				}
				String packet = "cs<font color='#" + Colores.NARANJA + "'>"
						+ Idiomas.getTexto(perso.getCuenta().idioma, 175) + "</font>";
				GestorSalida.enviar(perso, packet + "" + ((char) 0x00) + extrapacket);
			}
		}
	}

	static void Mercader() {
		objetosMercader = "";
		if (mapaMercader != null) {
			for (Entry<Integer, NPC> entry : mapaMercader.getNPCs().entrySet()) {
				if (entry.getValue().getModeloBD().getID() == 596) {
					int id = entry.getKey();
					NPC npc = mapaMercader.getNPC(id);
					if (id == 0 || npc == null) {
						break;
					}
					GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(mapaMercader, id);
					mapaMercader.borrarNPCoGrupoMob(id);
					break;
				}
			}
			mapaMercader = null;
		}
		String extrapacket = "#GS4";
		for (Personaje perso : MundoDofus.getPJsEnLinea()) {
			if (perso == null) {
				continue;
			}
			String packet = "cs<font color='#" + Colores.ROSA + "'>" + Idiomas.getTexto(perso.getCuenta().idioma, 205)
					+ "</font>";
			GestorSalida.enviar(perso, packet + "" + ((char) 0x00) + extrapacket);
		}
		int zonasx[] = { 8584, 8571, 7488, 7489, 7856, 7857, 8590, 8601, 8597, 8599, 8563, 8608, 8577, 8567, 8580,
				8612 };
		int I = Formulas.getRandomValor(0, 15);
		Mapa mapa = MundoDofus.getMapa(zonasx[I]);
		if (mapa == null) {
			return;
		}
		mapaMercader = mapa;
		mapa.addNPC(596, 280, 3);
		comprasMercader.clear();
		TimerMercader();
	}

	private static void TimerMercader() {
		Mapa mapaact = mapaMercader;
		Timer timer = new Timer(1000, new ActionListener() {
			int paso = 0;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (mapaMercader == null || mapaact != mapaMercader) {
					paso = 0;
					((Timer) e.getSource()).stop();
					return;
				}
				paso += 1;
				if (paso >= 300) {
					for (Entry<Integer, NPC> entry : mapaMercader.getNPCs().entrySet()) {
						if (entry.getValue().getModeloBD().getID() == 596) {
							int id = entry.getKey();
							NPC npc = mapaMercader.getNPC(id);
							if (id == 0 || npc == null) {
								break;
							}
							GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(mapaMercader, id);
							mapaMercader.borrarNPCoGrupoMob(id);
							break;
						}
					}
					for (Personaje perso : MundoDofus.getPJsEnLinea()) {
						if (perso == null) {
							continue;
						}
						String extrapacket = "";
						String packet = "cs<font color='#" + Colores.NARANJA + "'>"
								+ Idiomas.getTexto(perso.getCuenta().idioma, 206) + "</font>";
						GestorSalida.enviar(perso, packet + "" + ((char) 0x00) + extrapacket);
						if (perso.getMapa().getID() == mapaMercader.getID()) {
							if (perso.getIntercambio() != null) {
								perso.getIntercambio().cancel();
							}
						}
					}
					mapaMercader = null;
					paso = 0;
					comprasMercader.clear();
					((Timer) e.getSource()).stop();
					return;
				}
			}
		});
		timer.start();
	}

	static void Gema() {
		BorraAntes();
		activo = 1;
		int tipo = Formulas.getRandomValor(1, 2);
		if (tipo == 1) {
			gemaRoja = true;
		} else {
			gemaRoja = false;
		}
		int npcid = 387;
		if (gemaRoja) {
			npcid = 386;
		}
		int I = Formulas.getRandomValor(0, 25);
		Mapa mapa = MundoDofus.getMapa(zonas[I]);
		if (mapa == null) {
			return;
		}
		mapa.addNPC(npcid, 280, 3);
		mapaEvento = mapa;
		String extrapacket = "#GS4";
		if (gemaRoja) {
			for (Personaje perso : MundoDofus.getPJsEnLinea()) {
				if (perso == null) {
					continue;
				}
				String packet = "cs<font color='#" + Colores.AZUL + "'>"
						+ Idiomas.getTexto(perso.getCuenta().idioma, 171) + "</font>";
				GestorSalida.enviar(perso, packet + "" + ((char) 0x00) + extrapacket);
			}
		} else {
			for (Personaje perso : MundoDofus.getPJsEnLinea()) {
				if (perso == null) {
					continue;
				}
				String packet = "cs<font color='#" + Colores.AZUL + "'>"
						+ Idiomas.getTexto(perso.getCuenta().idioma, 172) + "</font>";
				GestorSalida.enviar(perso, packet + "" + ((char) 0x00) + extrapacket);
			}
		}
	}

	static void Busqueda() {
		String grupom = "";
		int tipo = Formulas.getRandomValor(1, 2);
		if (tipo == 1) {
			angel = true;
		} else {
			angel = false;
		}
		if (angel) {
			grupom = "4095,575;4098,575";
		} else {
			grupom = "4096,575;4097,575";
		}
		int zona = Formulas.getRandomValor(1, 2);
		int zona1[] = { 8534, 8536, 7761, 7764, 7774, 7776, 7809 }; // Castillo de Ankama
		int zona2[] = { 1869, 6952, 1856, 6955, 6939, 6951, 6954 }; // la cuna
		int I = Formulas.getRandomValor(0, 6);
		int MNPC;
		String extrapacket = "#GS4";
		if (zona == 1) {
			MNPC = zona1[I];// Idiomas.getTexto(_perso.getCuenta().idioma, 2)
			if (angel) {
				for (Personaje perso : MundoDofus.getPJsEnLinea()) {
					if (perso == null) {
						continue;
					}
					String packet = "cs<font color='#" + Colores.AZUL + "'>"
							+ Idiomas.getTexto(perso.getCuenta().idioma, 151) + "</font>";
					GestorSalida.enviar(perso, packet + "" + ((char) 0x00) + extrapacket);
				}
			} else {
				for (Personaje perso : MundoDofus.getPJsEnLinea()) {
					if (perso == null) {
						continue;
					}
					String packet = "cs<font color='#" + Colores.AZUL + "'>"
							+ Idiomas.getTexto(perso.getCuenta().idioma, 152) + "</font>";
					GestorSalida.enviar(perso, packet + "" + ((char) 0x00) + extrapacket);
				}
			}
		} else if (zona == 2) {
			MNPC = zona2[I];
			if (angel) {
				for (Personaje perso : MundoDofus.getPJsEnLinea()) {
					if (perso == null) {
						continue;
					}
					String packet = "cs<font color='#" + Colores.AZUL + "'>"
							+ Idiomas.getTexto(perso.getCuenta().idioma, 153) + "</font>";
					GestorSalida.enviar(perso, packet + "" + ((char) 0x00) + extrapacket);
				}
			} else {
				for (Personaje perso : MundoDofus.getPJsEnLinea()) {
					if (perso == null) {
						continue;
					}
					String packet = "cs<font color='#" + Colores.AZUL + "'>"
							+ Idiomas.getTexto(perso.getCuenta().idioma, 154) + "</font>";
					GestorSalida.enviar(perso, packet + "" + ((char) 0x00) + extrapacket);
				}
			}
		} else {
			return;
		}
		if (antesmap != null) {
			antesmap.borrarTodosMobs();
			antesmap.borrarTodosMobsFix();
			antesmap = null;
		}
		Mapa mapa = getMapa(MNPC);
		if (mapa == null) {
			return;
		}
		antesmap = mapa;
		mapa.borrarTodosMobs();
		mapa.borrarTodosMobsFix();
		mapa.addGrupoDeUnaPelea(240, grupom);
	}

	public static int getIDRegalo(int itemid, Cuenta _cuenta) {
		int laid = -1;
		for (Entry<Integer, String> regalos : itemsRegalos.entrySet()) {
			ArrayList<Integer> regaloscuenta = _cuenta.tieneRegalo;
			if (regaloscuenta.contains(regalos.getKey())) {
				continue;
			}
			int cuentaid = Integer.parseInt(regalos.getValue().split(";")[2]);
			if (_cuenta.getID() > cuentaid) {
				continue;
			}
			String tiempo = regalos.getValue().split(";")[0];
			Date date = new Date();
			Calendar cal2 = Calendar.getInstance();
			cal2.setTime(date);
			Calendar calzz = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
			try {
				calzz.setTime(sdf.parse(tiempo));
			} catch (ParseException e) {
				e.printStackTrace();
				continue;
			}
			String elitem = regalos.getValue().split(";")[1];
			if (cal2.compareTo(calzz) < 0) {
				int laiddelitem = Integer.parseInt(elitem.split(",")[0]);
				if (itemid == laiddelitem) {
					return regalos.getKey();
				}
			}
		}
		return laid;
	}

	public static int getCantRegalo(int itemid, Cuenta _cuenta) {
		int laid = -1;
		for (Entry<Integer, String> regalos : itemsRegalos.entrySet()) {
			ArrayList<Integer> regaloscuenta = _cuenta.tieneRegalo;
			if (regaloscuenta.contains(regalos.getKey())) {
				continue;
			}
			int cuentaid = Integer.parseInt(regalos.getValue().split(";")[2]);
			if (_cuenta.getID() > cuentaid) {
				continue;
			}
			String tiempo = regalos.getValue().split(";")[0];
			Date date = new Date();
			Calendar cal2 = Calendar.getInstance();
			cal2.setTime(date);
			Calendar calzz = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
			try {
				calzz.setTime(sdf.parse(tiempo));
			} catch (ParseException e) {
				e.printStackTrace();
				continue;
			}
			String elitem = regalos.getValue().split(";")[1];
			if (cal2.compareTo(calzz) < 0) {
				int laiddelitem = Integer.parseInt(elitem.split(",")[0]);
				int cant = Integer.parseInt(elitem.split(",")[1]);
				if (itemid == laiddelitem) {
					return cant;
				}
			}
		}
		return laid;
	}

	public static String compruebaRegalos(Cuenta _cuenta) {
		StringBuilder items = new StringBuilder("");
		boolean pasa = false;
		String regalosx = GestorSQL.CARGAR_REGALOS(_cuenta.getID());
		_cuenta.tieneRegalo.clear();
		for (String reg : regalosx.split(";")) {
			if (reg.equals("") || reg.equals("0")) {
				continue;
			}
			_cuenta.tieneRegalo.add(Integer.parseInt(reg));
		}
		for (Entry<Integer, String> regalos : itemsRegalos.entrySet()) {
			ArrayList<Integer> regaloscuenta = _cuenta.tieneRegalo;
			if (regaloscuenta.contains(regalos.getKey())) {
				continue;
			}
			int cuentaid = Integer.parseInt(regalos.getValue().split(";")[2]);
			if (_cuenta.getID() > cuentaid) {
				continue;
			}
			String tiempo = regalos.getValue().split(";")[0];
			Date date = new Date();
			Calendar cal2 = Calendar.getInstance();
			cal2.setTime(date);
			Calendar calzz = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
			try {
				calzz.setTime(sdf.parse(tiempo));
			} catch (ParseException e) {
				e.printStackTrace();
				continue;
			}
			String elitem = regalos.getValue().split(";")[1];
			if (cal2.compareTo(calzz) < 0) {
				if (!pasa) {
					items.append(elitem.split(",")[0] + "," + elitem.split(",")[1]);
					pasa = true;
				} else {
					items.append(";" + elitem.split(",")[0] + "," + elitem.split(",")[1]);
				}
			}
		}
		return items.toString();
	}

	private static void iniciarEstrellas() {
		for (int i = 300; i > 0; i--) {
			int rand = Formulas.getRandomValor(5, 10);
			estrellas.add(Integer.valueOf(rand));
		}
	}

	public static Integer getRandomEstrellas() {
		Random randomGenerator = new Random();
		int index = randomGenerator.nextInt(estrellas.size());
		return estrellas.get(index);
	}

	static void subirEstrellasMobs2() {
		for (Mapa mapa : Mapas.values()) {
			mapa.subirEstrellasMobs();
		}
	}

	static void subirEstrellasMobs() {
		ArrayList<Integer> estrellasc = new ArrayList<>();
		for (Integer item : estrellas) {
			int rand = Formulas.getRandomValor(1, 15);
			item = Integer.valueOf(item.intValue() + rand);
			if (item.intValue() > 600) {
				item = Integer.valueOf(5);
			}
			estrellasc.add(item);
		}
		estrellas.clear();
		estrellas.addAll(estrellasc);
	}

	static void addMisionEtapa(int mision, String eta) {
		misionEtapa.put(mision, eta);
	}

	public static String getMisionEtapa(int mis) {
		return misionEtapa.get(mis);
	}

	static void addMisionObj(int mision, String obj) {
		misionObjetivo.put(mision, obj);
	}

	public static String getMisionObj(int obj) {
		return misionObjetivo.get(obj);
	}

	static void addMisionObjs(int mision, String obj) {
		misionObjetivos.put(mision, obj);
	}

	public static String getMisionObjs(int obj) {
		return misionObjetivos.get(obj);
	}

	private synchronized static int getSigIDObjeto() {
		return ++sigIDObjeto;
	}

	public static int getSigIDCofre() {
		return ++sigIDCofre;
	}

	static void crearServer() {
		iniciarEstrellas();
		GestorSQL.LOGGED_ZERO();
		System.out.println("Cargando Misiones");
		GestorSQL.CARGAR_MISIONES();
		System.out.println("Cargando Niveles Exp");
		GestorSQL.CARGAR_EXPERIENCIA();
		System.out.println("Cargando Objetos Modelo");
		GestorSQL.CARGAR_MODELOS_OBJETOS();
		System.out.println("Cargando Sets Objetos");
		GestorSQL.CARGAR_ITEMSETS();
		System.out.println("Cargando Accion Objetos");
		GestorSQL.CARGAR_ACCIONES_USO_OBJETOS();
		System.out.println("Cargando Interactivos");
		GestorSQL.CARGAR_INTERACTIVOS();
		System.out.println("Cargando Recetas");
		GestorSQL.CARGAR_RECETAS();
		System.out.println("Cargando Zaaps");
		GestorSQL.CARGAR_ZAAPS();
		System.out.println("Cargando BANIPs");
		GestorSQL.CARGAR_BANIP();
		System.out.println("Cargando Preguntas NPCs");
		GestorSQL.CARGAR_PREGUNTAS();
		System.out.println("Cargando Respuestas NPCs");
		GestorSQL.CARGAR_RESPUESTAS();
		System.out.println("Cargando Tutoriales");
		GestorSQL.CARGAR_TUTORIALES();
		System.out.println("Cargando Puestos Mercadillo");
		GestorSQL.CARGAR_PUESTOS_MERCADILLOS();
		System.out.println("Cargando Objetos Mercadillo");
		GestorSQL.CARGAR_OBJETOS_MERCADILLOS();
		System.out.println("Cargando Animaciones");
		GestorSQL.CARGAR_ANIMACIONES();
		System.out.println("Cargando Oficios");
		GestorSQL.CARGAR_OFICIOS();
		System.out.println("Cargando Gremios");
		GestorSQL.CARGAR_GREMIOS();
		System.out.println("Cargando Miembros Gremios");
		GestorSQL.CARGAR_MIEMBROS_GREMIO();
		System.out.println("Cargando Objevivos");
		GestorSQL.CARGAR_OBJEVIVOS();
		System.out.println("Cargando Retos");
		GestorSQL.CARGAR_RETOS();
		System.out.println("Cargando Encarnaciones");
		GestorSQL.CARGAR_ENCARNACIONES();
		System.out.println("Cargando Libros Runicos");
		GestorSQL.CARGAR_LIBROS_RUNICOS();
		System.out.print("Cargando las zonas: ");
		GestorSQL.CARGAR_ZONAS();
		System.out.println(ZONAS.size() + " zonas cargados");
		System.out.println("Cargando Cofres");
		GestorSQL.CARGAR_COFRE();
		System.out.println("Cargando Areas");
		GestorSQL.CARGAR_AREA();
		System.out.println("Cargando SubAreas");
		GestorSQL.CARGAR_SUBAREA();
		System.out.println("Cargando Prismas");
		GestorSQL.CARGAR_PRISMAS();
		System.out.println("Cargando Recaudadores");
		GestorSQL.CARGAR_RECAUDADORES();
		System.out.println("Cargando Objetos Mercantes");
		GestorSQL.CARGAR_OBJETOS_MERCANTES();
		System.out.println("Cargando Mercantes");
		GestorSQL.CARGAR_MERCANTES();
		System.out.println("Cargando Mapas Dinamicos");
		GestorSQL.CARGAR_MAPAS_D();
		System.out.println("Cargando Casas");
		GestorSQL.cargarCasas();
		System.out.println("Cargando Cercados");
		GestorSQL.cargarCercados();
		System.out.println("Cargando Comidas Mascotas");
		GestorSQL.CARGAR_COMIDAS_MASCOTAS();
		System.out.println("Cargando Mascotas");
		GestorSQL.CARGAR_MASCOTAS();
		sigIDObjeto = GestorSQL.getSigIDObjeto();
		System.out.println("Cargando RankingPvP");
		GestorSQL.CARGAR_RANKINGPVP();
		// System.out.println("Cargando Fusionados");
		// GestorSQL.cargarFusionados();
		System.out.println("Cargando Regalos");
		GestorSQL.cargarRegalos();
		System.out.println("Cargando Cuentas Mercadillos");
		GestorSQL.CARGAR_CUENTAS_MERCADILLO();
		System.out.println("Cargando Invasiones");
		GestorSQL.CARGAR_INVASIONES();
		System.out.println("Cargando Objetivos Diarios");
		GestorSQL.CARGAR_OBJETIVOSDIARIOS();
		System.out.println("Cargando Pase Batalla");
		GestorSQL.CARGAR_PASEBATALLA();
		// System.out.println("Cargando Mazmorras Diarias");
		// GestorSQL.CARGAR_MAZMORRAS();
		System.out.println("Cargando Rankings PvP");
		if (RankingsPVP.size() > 0) {
			Emu.actualizaRankings(1);
			// Emu.actualizaRankings(2);
			// Emu.actualizaRankings(3);
			liderRanking = nombreLiderRankingPVP("1");
			// liderRanking2 = nombreLiderRankingPVP("2");
			// liderRanking3 = nombreLiderRankingPVP("3");
		}
		// System.out.println("Comprobando gremios inactivos");
		// compruebaGremioInactivo();
		// rankingNivel = GestorSQL.CARGAR_RANKING_NIVEL();
		// rankingGremios = GestorSQL.CARGAR_RANKING_GREMIO();
		CompruebaLoot();
		CompruebaLoot2();
		CompruebaDiarios();
		CompruebaRegaloMis();
		Idiomas.cargarIdiomaES();
		Idiomas.cargarIdiomaEN();
		lastSave = System.currentTimeMillis();
	}

	private static void CompruebaRegaloMis() {
		ArrayList<Integer> objetosLoot = new ArrayList<>();
		objetosLoot.addAll(Emu.regaloMis);
		for (Integer iditem : Emu.regaloMis) {
			try {
				ObjetoModelo obj = MundoDofus.getObjModelo(iditem);
				if (obj == null) {
					if (objetosLoot.contains(iditem)) {
						objetosLoot.remove(Integer.valueOf(iditem));
					}
				}
			} catch (Exception e) {
			}
		}
		Emu.regaloMis.clear();
		Emu.regaloMis.addAll(objetosLoot);
	}

	@SuppressWarnings("unused")
	private static void compruebaGremioInactivo() {
		Gremio inicio = Gremios.get(Emu.GremioId);
		if (inicio != null) {
			for (MiembroGremio gr : inicio.getTodosMiembros()) {
				Personaje per = MundoDofus.getPjPorNombreIgnora(gr.getNombre());
				if (per == null) {
					per = getPjPorNombreIgnora(gr.getNombre());
				}
				if (per != null) {
					if (per.getNivel() >= 200 || gr.getHorasDeUltimaConeccion() > 170) {
						if (per.getCuenta().getRango() > 0) {
							continue;
						}
						inicio.expulsarMiembro(per);
						per.setMiembroGremio(null);
					}
				} else {
					GestorSQL.BORRAR_MIEMBROS(inicio.getID(), gr.getNombre());
				}
			}
		}
		Map<Integer, Gremio> GremiosABorrar = new TreeMap<>();
		for (Gremio gr : MundoDofus.Gremios.values()) {
			if (gr.ultimaConex.equals("") || gr.getID() == Emu.GremioId) {
				continue;
			}
			String[] dt = gr.ultimaConex.split("~");
			String dateStart = dt[1] + "/" + dt[2] + "/" + dt[0] + " " + dt[3] + ":" + dt[4] + ":00";
			DateFormat formatTime = new SimpleDateFormat("yyyy~MM~dd~HH~mm");
			Calendar calendar = Calendar.getInstance();
			String newfec[] = formatTime.format(calendar.getTime()).split("~");
			String dateStop = newfec[1] + "/" + newfec[2] + "/" + newfec[0] + " " + newfec[3] + ":" + newfec[4] + ":00";
			SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			Date d1 = null;
			Date d2 = null;
			try {
				d1 = format.parse(dateStart);
				d2 = format.parse(dateStop);
				long diff = d2.getTime() - d1.getTime();
				long diffDays = diff / (24 * 60 * 60 * 1000);
				if (diffDays > 5) {
					if (!GremiosABorrar.containsKey(gr.getID())) {
						GremiosABorrar.put(gr.getID(), gr);
					}
				}
			} catch (Exception e) {
			}
		}
		for (Entry<Integer, Gremio> gr : GremiosABorrar.entrySet()) {
			gr.getValue().borrado = true;
			String existe = GestorSQL.getNombres(gr.getValue().getID());
			for (String pjs : existe.split("\\|")) {
				GestorSQL.BORRAR_MIEMBROS(gr.getValue().getID(), pjs);
			}
			borrarGremio(gr.getValue().getID());
		}
	}

	private static void CompruebaDiarios() {
		ArrayList<Integer> objetosLoot = new ArrayList<>();
		objetosLoot.addAll(Emu.objetosDiarias);
		for (Integer iditem : Emu.objetosDiarias) {
			try {
				ObjetoModelo obj = MundoDofus.getObjModelo(iditem);
				if (obj == null) {
					if (objetosLoot.contains(iditem)) {
						objetosLoot.remove(Integer.valueOf(iditem));
					}
				}
			} catch (Exception e) {
			}
		}
		Emu.objetosDiarias.clear();
		Emu.objetosDiarias.addAll(objetosLoot);
	}

	private static void CompruebaLoot() {
		ArrayList<Integer> objetosLoot = new ArrayList<>();
		objetosLoot.addAll(Emu.objetosLoot);
		for (Integer iditem : Emu.objetosLoot) {
			try {
				ObjetoModelo obj = MundoDofus.getObjModelo(iditem);
				if (obj == null) {
					if (objetosLoot.contains(iditem)) {
						objetosLoot.remove(Integer.valueOf(iditem));
					}
				}
			} catch (Exception e) {
			}
		}
		Emu.objetosLoot.clear();
		Emu.objetosLoot.addAll(objetosLoot);
	}

	private static void CompruebaLoot2() {
		ArrayList<Integer> objetosLoot = new ArrayList<>();
		objetosLoot.addAll(Emu.objetosLoot2);
		for (Integer iditem : Emu.objetosLoot2) {
			try {
				ObjetoModelo obj = MundoDofus.getObjModelo(iditem);
				if (obj == null) {
					if (objetosLoot.contains(iditem)) {
						objetosLoot.remove(Integer.valueOf(iditem));
					}
				}
			} catch (Exception e) {
			}
		}
		Emu.objetosLoot2.clear();
		Emu.objetosLoot2.addAll(objetosLoot);
	}

	private static Area getArea(int areaID) {
		return Areas.get(areaID);
	}

	private static SuperArea getSuperArea(int areaID) {
		return SuperAreas.get(areaID);
	}

	public static SubArea getSubArea(int areaID) {
		return SubAreas.get(areaID);
	}

	static void addArea(Area area) {
		Areas.put(area.getID(), area);
	}

	private static void addSuperArea(SuperArea SA) {
		SuperAreas.put(SA.getID(), SA);
	}

	static void addSubArea(SubArea SA) {
		SubAreas.put(SA.getID(), SA);
	}

	static void addNPCreponse(RespuestaNPC respuesta) {
		NPCRespuesta.put(respuesta.getID(), respuesta);
	}

	public static RespuestaNPC getNPCreponse(int id) {
		return NPCRespuesta.get(id);
	}

	static void addExpLevel(int nivel, ExpNivel exp) {
		Experiencia.put(nivel, exp);
	}

	public static Cuenta getCuenta(int id) {
		return Cuentas.get(id);
	}

	static void addNPCPregunta(PreguntaNPC pregunta) {
		NPCPreguntas.put(pregunta.getID(), pregunta);
	}

	public static PreguntaNPC getNPCPregunta(int id) {
		return NPCPreguntas.get(id);
	}

	public static NPCModelo getNPCModelo(int id) {
		NPCModelo npc = NPCModelos.get(id);
		if (npc == null && !NPCModelos.containsKey(id)) {
			npc = GestorSQL.cargarModeloNpcs(id);
		}
		return npc;
	}

	static void addNpcModelo(NPCModelo npcModelo) {
		NPCModelos.put(npcModelo.getID(), npcModelo);
	}

	public static Mapa getMapa(int id) {
		Mapa mapa = Mapas.get(id);
		if (mapa == null && !Mapas.containsKey(id)) {
			mapa = GestorSQL.cargarMapa(id);
		}
		return mapa;
	}

	private static int cantidaddemap = 0;

	static void addMapa(Mapa mapa) {
		if (!Mapas.containsKey(mapa.getID())) {
			Mapas.put(mapa.getID(), mapa);
		}
		if (!mapasCargar.contains(mapa.getID())) {
			mapasCargar.add(mapa.getID());
			cantidaddemap += 1;
		}
	}

	public static int mapaPorCoordenadasi(int mapaX, int mapaY) {
		int mapaid = 0;
		for (Mapa mapa : Mapas.values()) {
			if (mapa.getCoordX() == mapaX && mapa.getCoordY() == mapaY) {
				mapaid = mapa.getID();
				break;
			}
		}
		return mapaid;
	}

	public static String mapaPorCoordenadas(int mapaX, int mapaY) {
		StringBuilder str = new StringBuilder("");
		for (Mapa mapa : Mapas.values()) {
			if (mapa.getCoordX() == mapaX && mapa.getCoordY() == mapaY) {
				str.append(mapa.getID() + ", ");
			}
		}
		return str.toString();
	}

	public final static Cuenta getCuentaPorNombre(String nombre) {
		Cuenta cuenta = CuentasPorNombre.get(nombre);
		if (cuenta != null) {
			return cuenta;
		} else {
			cuenta = GestorSQL.cargarCompte(nombre);
		}
		return cuenta;
	}

	public static Personaje getPersonaje(int id) {
		return Personajes.get(id);
	}

	synchronized static void addCuenta(Cuenta cuenta) {
		Cuentas.put(cuenta.getID(), cuenta);
		CuentasPorNombre.put(cuenta.getNombre(), cuenta);
	}

	public synchronized static void addPersonaje(Personaje perso) {
		Personajes.put(perso.getID(), perso);
		PersonajesPorNombre.put(perso.getNombre(), perso);
	}

	public static Personaje getPjPorNombre(String nombre) {
		Personaje p = PersonajesPorNombre.get(nombre);
		if (p != null) {
			if (p.enLinea()) {
				return p;
			}
		}
		return null;
	}

	public static Personaje getPjPorNombreIgnora(String nombre) {
		Personaje p = PersonajesPorNombre.get(nombre);
		if (p != null) {
			return p;
		} else {
			GestorSQL.cargarCuentadesdeNombre(nombre);
			p = PersonajesPorNombre.get(nombre);
			if (p != null) {
				return p;
			}
		}
		return null;
	}

	public synchronized static void eliminarPersonaje(Personaje perso) { // TODO:
		if (perso.getMontura() != null) {
			Dragopavo DD = MundoDofus.getDragopavoPorID(perso.getMontura().getID());
			MundoDofus.borrarDragopavoID(DD.getID());
			if (!DD.getObjetos().isEmpty()) {
				ArrayList<Objeto> objetos = new ArrayList<>();
				objetos.addAll(DD.getObjetos());
				borrarObjetosPorGrupo(objetos);
			}
		}
		for (Cofre cofre : Cofres.values()) {
			if (cofre.getDueÑoID() == perso.getID()) {
				GestorSQL.ReiniciarCofre(cofre.getID(), perso.getID());
			}
		}
		if (perso.getMercante() != 1) {
			perso.getMapa().removerMercante(perso.getID());
			GestorSalida.ENVIAR_GM_MERCANTES_b(perso.getMapa());
		}
		if (!perso.getTienda().isEmpty()) {
			ArrayList<Objeto> objetos = new ArrayList<>();
			objetos.addAll(perso.getTienda());
			borrarObjetosPorGrupo(objetos);
		}
		for (Casa casa : Casas.values()) {
			if (casa.getDueÑoID() == perso.getID()) {
				casa.resetear();
				GestorSQL.ACTUALIZAR_CASA(casa);
			}
		}
		for (Cercado cercado : Cercados.values()) {
			if (cercado.getDueÑo() == perso.getID()) {
				GestorSQL.GET_BORRANDO_CERCADO(perso);
				cercado.resetear();
				GestorSQL.SALVAR_CERCADO(cercado);
			} else {
				GestorSQL.GET_BORRANDO_CERCADO(perso);
			}
		}
		if (perso.getGremio() != null || perso.getMiembroGremio() != null) {
			if (perso.getGremio().getTodosMiembros().size() <= 1) {
				perso.getGremio().expulsarMiembro(perso);
				borrarGremio(perso.getGremio().getID());
			} else {
				int gremio = perso.getMiembroGremio().getGremio().getID();
				String existe = GestorSQL.getNombres(gremio);
				Personaje persos = null;
				if (perso.getGremio().getMiembro(perso.getID()).getRango() == 1) {
					for (String pjs : existe.split("\\|")) {
						if (pjs == perso.getNombre()) {
							continue;
						}
						persos = MundoDofus.getPjPorNombre(pjs);
						GestorSQL.BORRAR_MIEMBROS(gremio, pjs);
						if (persos != null) {
							if (persos.enLinea()) {
								if (persos != perso) {
									GestorSalida.ENVIAR_cs_CHAT_MENSAJE(persos,
											"El dueÑo del gremio ha borrado su personaje. El gremio acaba de ser disuelto.",
											Emu.COLOR_MENSAJE);
									GestorSalida.GAME_SEND_gK_PACKET(persos, "K" + perso.getNombre() + "|");
								}
							}
							persos.setMiembroGremio(null);
						}
					}
					borrarGremio(perso.getGremio().getID());
				} else {
					perso.getGremio().expulsarMiembro(perso);
					perso.setMiembroGremio(null);
					for (String pjs : existe.split("\\|")) {
						if (pjs == perso.getNombre()) {
							continue;
						}
						persos = MundoDofus.getPjPorNombre(pjs);
						if (persos != null) {
							if (persos.enLinea()) {
								if (persos != perso) {
									GestorSalida.ENVIAR_cs_CHAT_MENSAJE(persos,
											perso.getNombre()
													+ " acaba de borrar su personaje y ha sido eliminado del gremio.",
											Emu.COLOR_MENSAJE);
								}
							}
						}
					}
				}
			}
		}
		if (MundoDofus.estaRankingPVP(perso.getID())) {
			GestorSQL.BORRAR_RANKINGPVP(perso.getID());
			MundoDofus.delRankingPVP(perso.getID());
		}
		GestorSQL.BORRAR_PERSONAJE(perso);
		Personajes.remove(perso.getID());
		PersonajesPorNombre.remove(perso.getNombre());
	}

	private final static void borrarObjetosPorGrupo(ArrayList<Objeto> objetos) {
		if (objetos.size() == 0) {
			return;
		}
		StringBuilder str = new StringBuilder();
		int id;
		for (Objeto obj : objetos) {
			if (obj == null) {
				continue;
			}
			id = obj.getID();
			Objetos.remove(id);
			str.append(str.length() > 0 ? "," : "").append(id);
		}
		GestorSQL.DELETE_OBJETOS_LISTA(str.toString());
	}

	public static Casa getCasaDentroPorMapa(int i) {
		for (Casa casa : Casas.values()) {
			if (casa.getMapasContenidos().contains(i)) {
				return casa;
			}
		}
		return null;
	}

	public static String getAlineacionTodasSubareas() {
		StringBuilder str = new StringBuilder("");
		boolean primero = false;
		for (SubArea subarea : SubAreas.values()) {
			if (!subarea.getConquistable()) {
				continue;
			}
			if (primero) {
				str.append("|");
			}
			str.append(subarea.getID() + ";" + subarea.getAlineacion());
			primero = true;
		}
		return str.toString();
	}

	public static long getExpMinPersonaje(int nivel) {
		if (nivel > Emu.MAX_NIVEL) {
			nivel = Emu.MAX_NIVEL;
		}
		if (nivel < 1) {
			nivel = 1;
		}
		return Experiencia.get(nivel)._personaje;
	}

	public static long getExpMaxPersonaje(int nivel) {
		if (nivel >= Emu.MAX_NIVEL) {
			nivel = Emu.MAX_NIVEL - 1;
		}
		if (nivel <= 1) {
			nivel = 1;
		}
		return Experiencia.get(nivel + 1)._personaje;
	}

	public static long lastSave = 0;
	public static Object mazmorrasDiarias;

	public static void salvarServidor(Personaje salvador) {
		if (_estado != 1 || Emu.Salvando || Emu.noguarda) {
			return;
		}
		_estado = 2;
		/*
		 * for (EntradaPersonaje es : ServidorPersonaje._clientes) { if
		 * (es.getPersonaje() == null) GestorSalida.enviar(es.getOut(), "AH" +
		 * Emu.SERVER_ID + ";"+_estado+";110;1"); }
		 */
		lastSave = System.currentTimeMillis();
		try {// TODO:
			Map<Integer, SubArea> sares = SubAreas;
			Map<Integer, Prisma> pris = Prismas;
			Map<Integer, Area> ares = Areas;
			Map<Integer, Gremio> gre = Gremios;
			Map<Integer, Mascota> masc = Mascotas;
			Map<Integer, Cercado> cerca = Cercados;
			Map<Integer, Encarnacion> encar = Encarnaciones;
			Map<Integer, LibrosRunicos> lRunicos = LibrosRunicos;
			Map<Integer, Recaudador> recaux = Recaudadores;
			Map<Integer, Dragopavo> drag = Dragopavos;
			Map<Integer, Cofre> cofres = Cofres;
			Map<Integer, Mercadillo> pues = PuestosMercadillos;
			Map<Integer, RankingPVP> ran = RankingsPVP;
			Map<Integer, Casa> casas = Casas;
			Map<Integer, Objevivo> objevi = Objevivos;
			Map<Integer, Personaje> persona = Personajes;
			Emu.Salvando = true;
			// GestorSQL.TIMER(false);
			Map<Integer, Cuenta> cuentas = Cuentas;
			// System.out.println("Guardando...");
			for (Cuenta cuenta : cuentas.values()) {
				GestorSQL.SALVAR_CUENTA(cuenta);
			}
			GestorSQL.ACTUALIZAR_NPC_KAMAS(MundoDofus.getNPCModelo(408));
			// System.out.print("5% ");
			if (cantidaddemap < 250) {
				StringBuilder str = new StringBuilder();
				for (Integer splitob : MundoDofus.mapasCargar) {
					if (str.toString().isEmpty()) {
						str.append(splitob);
					} else {
						str.append(";" + splitob);
					}
				}
				GestorSQL.ACTUALIZAR_MAPA_DIN(str.toString());
			} else {
				GestorSQL.ACTUALIZAR_MAPA_DIN("");
			}
			// System.out.print("10% ");
			for (Personaje perso : persona.values()) {
				if ((perso == null) || !perso.enLinea()) {
					continue;
				}
				GestorSQL.SALVAR_PERSONAJE(perso, true);
			}
			// System.out.print("15% ");
			for (Objevivo objevivo : objevi.values()) {
				GestorSQL.SALVAR_OBJEVIVO(objevivo);
			}
			// System.out.print("20% ");
			for (Encarnacion encarnacion : encar.values()) {
				GestorSQL.SALVAR_ENCARNACION(encarnacion);
			}
			for (LibrosRunicos librosRunicos : lRunicos.values()) {
				GestorSQL.SALVAR_LIBROS_RUNICOS(librosRunicos);
			}
			// System.out.print("25% ");
			for (Mascota mascota : masc.values()) {
				GestorSQL.REPLACE_MASCOTA(mascota);
			}
			// System.out.print("30% ");
			for (Area area : ares.values()) {
				GestorSQL.ACTUALIZAR_AREA(area);
			}
			// System.out.print("35% ");
			for (SubArea subarea : sares.values()) {
				GestorSQL.ACTUALIZAR_SUBAREA(subarea);
			}
			// System.out.print("40% ");
			for (Prisma prisma : pris.values()) {
				if (Mapas.get(prisma.getMapa()).getSubArea().getPrismaID() != prisma.getID()) {
					GestorSQL.BORRAR_PRISMA(prisma.getID());
				} else {
					GestorSQL.SALVAR_PRISMA(prisma);
				}
			}
			// System.out.print("45% ");
			for (Gremio gremio : gre.values()) {
				GestorSQL.ACTUALIZAR_GREMIO(gremio);
			}
			// System.out.print("50% ");
			for (Cercado cercado : cerca.values()) {
				GestorSQL.SALVAR_CERCADO(cercado);
				GestorSQL.ACTUALIZAR_CELDAS_OBJETO(cercado.getMapa().getID(), cercado.getStringCeldasObj());
			}
			// System.out.print("55% ");
			for (Recaudador recau : recaux.values()) {
				if (recau.getEstadoPelea() > 0) {
					continue;
				}
				GestorSQL.ACTUALIZAR_RECAUDADOR(recau);
			}
			// System.out.print("60% ");
			for (Dragopavo montura : drag.values()) {
				if (montura != null) {
					GestorSQL.ACTUALIZAR_MONTURA(montura, true);
				}
			}
			// System.out.print("65% ");
			for (Casa house : casas.values()) {
				if (house != null) {
					if (house.getDueÑoID() > 0) {
						GestorSQL.ACTUALIZAR_CASA(house);
					}
				}
			}
			// System.out.print("70% ");
			for (RankingPVP rank : ran.values()) {
				GestorSQL.SALVAR_RANKINGPVP(rank);
			}
			// System.out.println("Salvando los fusionados");
			/*
			 * StringBuilder xa = new StringBuilder(""); boolean prim = false; for (Integer
			 * desaf : _fusionados) { if (!prim) xa.append(desaf); else
			 * xa.append(","+desaf); prim = true; }
			 * GestorSQL.SALVAR_FUSIONADOS(xa.toString());
			 */
			// System.out.print("75% ");
			for (Cofre cofre : cofres.values()) {
				if (cofre.getDueÑoID() > 0) {
					GestorSQL.ACTUALIZAR_COFRE(cofre);
				}
			}
			GestorSQL.ACTUALIZAR_COFRE(Cofre.getCofrePorUbicacion(0, 0));
			// System.out.print("80% ");
			ArrayList<ObjetoMercadillo> toSave = new ArrayList<>();
			for (Mercadillo puesto : pues.values()) {
				toSave.addAll(puesto.todoListaObjMercaDeUnPuesto());
			}
			GestorSQL.VACIA_Y_ACTUALIZA_OBJ_MERCADILLOS();
			GestorSQL.VACIA_Y_ACTUALIZA_OBJ_MERCADILLOS2(toSave);
			// System.out.print("85% ");
			GestorSQL.ACTUALIZAR_INVASIONES();
			// System.out.print("90% ");
			for (Integer id : mapasSalvar) {
				Mapa map = MundoDofus.getMapa(id);
				if (map == null) {
					continue;
				}
				StringBuilder strx = new StringBuilder("");
				boolean entra = false;
				for (Entry<Integer, String> stra : map._personaliza.entrySet()) {
					if (entra) {
						strx.append(";");
					}
					strx.append(stra.getKey() + "," + stra.getValue().split(",")[0] + ","
							+ stra.getValue().split(",")[1] + "," + stra.getValue().split(",")[2]);
					entra = true;
				}
				StringBuilder strx1 = new StringBuilder("");
				boolean entra1 = false;
				for (Entry<Integer, String> stra : map._limpieza.entrySet()) {
					if (entra1) {
						strx1.append(";");
					}
					strx1.append(
							stra.getKey() + "," + stra.getValue().split(",")[0] + "," + stra.getValue().split(",")[1]);
					entra1 = true;
				}
				GestorSQL.ACTUALIZA_MAPAS_PERSO(id, strx.toString(), strx1.toString());
			}
			mapasSalvar.clear();
			/*
			 * StringBuilder strx2 = new StringBuilder(""); boolean isf = false;
			 * strx2.append(diaMazmo+","); for (Integer id : mazmorrasElegidas) { if (isf)
			 * strx2.append(";"); strx2.append(id); isf = true; }
			 * GestorSQL.SALVAR_MAZMORRAS(strx2.toString());
			 */
			// System.out.print("95% 100%");
		} catch (Exception e) {
			System.out.println("Error al salvar : " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			// GestorSQL.TIMER(true);
			Emu.Salvando = false;
			_estado = 1;
			ArrayList<EntradaPersonaje> cli = ServidorPersonaje._clientes;
			for (EntradaPersonaje es : cli) {
				if (es == null) {
					continue;
				}
				if (es.getPersonaje() == null) {
					GestorSalida.enviar(es.getOut(), "AH" + Emu.SERVER_ID + ";" + _estado + ";110;1");
				}
			}
		}
	}

	public static ExpNivel getExpNivel(int nivel) {
		return Experiencia.get(nivel);
	}

	public static ObjInteractivoModelo getObjInteractivoModelo(int id) {
		return ObjInteractivos.get(id);
	}

	static void addObjInteractivo(ObjInteractivoModelo OIM) {
		ObjInteractivos.put(OIM.getID(), OIM);
	}

	public static Oficio getOficio(int id) {
		return Oficios.get(id);
	}

	static void addOficio(Oficio oficio) {
		Oficios.put(oficio.getID(), oficio);
	}

	synchronized static void addReceta(int id, ArrayList<Duo<Integer, Integer>> arrayDuos) {
		Recetas.put(id, arrayDuos);
	}

	// LISTA [459, 1001, 6868, 7653, 7654, 7655, 7656, 7657, 7658, 7659, 7660, 7661,
	// 7662, 7663, 7664, 7665, 7666, 7667, 7668, 7669, 7670, 7671, 7672, 8078]
	public static int getIDRecetaPorIngredientes(ArrayList<Integer> listaIDRecetas,
			Map<Integer, Integer> ingredientes) {
		if (listaIDRecetas == null) {
			return -1;
		}
		for (int id : listaIDRecetas) {
			ArrayList<Duo<Integer, Integer>> receta = Recetas.get(id);
			if (receta == null || receta.size() != ingredientes.size()) {
				continue;
			}
			boolean ok = true;
			for (Duo<Integer, Integer> ing : receta) {
				if (ingredientes.get(ing._primero) == null) {
					ok = false;
					break;
				}
				int primera = ingredientes.get(ing._primero);
				int segunda = ing._segundo;
				if (primera != segunda) {
					ok = false;
					break;
				}
			}
			if (ok) {
				return id;
			}
		}
		return -1;
	}

	public static Cuenta getCuentaPorApodo(String p) {
		for (Cuenta C : Cuentas.values()) {
			if (C.getApodo().equals(p)) {
				return C;
			}
		}
		return null;
	}

	static void addItemSet(ItemSet itemSet) {
		ItemSets.put(itemSet.getId(), itemSet);
	}

	public static ItemSet getItemSet(int tID) {
		return ItemSets.get(tID);
	}

	public synchronized static int getSigIDMontura() {
		int max = -101;
		for (int a : Dragopavos.keySet()) {
			if (a < max) {
				max = a;
			}
		}
		return max - 3;
	}

	public synchronized static int getSigIDPrisma() {
		int max = -102;
		for (int a : Prismas.keySet()) {
			if (a < max) {
				max = a;
			}
		}
		return max - 3;
	}

	public synchronized static void addGremio(Gremio g, boolean save) {
		Gremios.put(g.getID(), g);
		if (save) {
			GestorSQL.SALVAR_NUEVO_GREMIO(g);
		}
	}

	public synchronized static int getSigIdGremio() {
		if (Gremios.isEmpty()) {
			return 1;
		}
		int n = 0;
		for (int x : Gremios.keySet()) {
			if (n < x) {
				n = x;
			}
		}
		return n + 1;
	}

	public synchronized static boolean nombreGremioUsado(String name) {
		for (Gremio g : Gremios.values()) {
			if (g.getNombre().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	public synchronized static boolean emblemaGremioUsado(String emb) {
		for (Gremio g : Gremios.values()) {
			if (g.getEmblema().equals(emb)) {
				return true;
			}
		}
		return false;
	}

	public static Gremio getGremio(int id) {
		return Gremios.get(id);
	}

	public static long getXPMaxGremio(int nivel) {
		if (nivel >= 200) {
			nivel = 200 - 1;
		}
		if (nivel <= 1) {
			nivel = 1;
		}
		return Experiencia.get(nivel + 1)._gremio;
	}

	public static int getCeldaZaapPorMapaID(short i) {
		for (Entry<Integer, Integer> zaap : CentroInfo.ZAAPS.entrySet()) {
			if (zaap.getKey() == i) {
				return zaap.getValue();
			}
		}
		return -1;
	}

	public static int getCeldaCercadoPorMapaID(short i) {
		Cercado cercado = MundoDofus.getMapa(i).getCercado();
		if (cercado != null) {
			if (cercado.getCeldaID() > 0) {
				return cercado.getCeldaID();
			}
		}
		return -1;
	}

	static void borrarDragopavoID(int getId) {
		Dragopavos.remove(getId);
	}

	public static void borrarGremio(int id) {
		if (!Gremios.containsKey(id)) {
			return;
		}
		ArrayList<Recaudador> aBorrar = new ArrayList<>();
		Collection<Recaudador> rev = Recaudadores.values();
		for (Recaudador reca : rev) {
			if (reca == null) {
				continue;
			}
			if (reca.getGremioID() == id) {
				aBorrar.add(reca);
			}
		}
		for (Recaudador aBorr : aBorrar) {
			aBorr.borrarRecaudador(aBorr.getID());
		}
		Casa.borrarCasaGremio(id);
		GestorSQL.borrarCercadosGremio(id);
		Gremios.remove(id);
		GestorSQL.BORRAR_GREMIO(id);
	}

	public static Map<Integer, Sucess> getSucess() {
		return SUCCESS;
	}

	public void setSucess(java.util.Map<Integer, Sucess> sucess) {
		SUCCESS = sucess;
	}

	public static void addSucess(Sucess sucess) {
		SUCCESS.put(sucess.getId(), sucess);
	}

	public Sucess getSucess(int guid) {
		return SUCCESS.get(guid);
	}

	public static int cuentasIP(String ip) {
		int veces = 0;
		for (Cuenta c : Cuentas.values()) {
			if (c == null) {
				continue;
			}
			if (c.enLinea()) {
				if (c.getActualIP().compareTo(ip) == 0) {
					veces++;
				}
			}
		}
		return veces;
	}

	synchronized static Objeto objetoIniciarServer(int id, int modelo, int cant, int pos, String strStats, int idObvi) {
		ObjetoModelo objModelo = MundoDofus.getObjModelo(modelo);
		if (objModelo == null) {
			System.out.println("La id del objeto bug " + id);
			GestorSQL.BORRAR_OBJETO(id);
			return null;
		}
		if (objModelo.getTipo() == 85) {
			return new PiedraDeAlma(id, cant, modelo, pos, strStats);
		} else {
			return new Objeto(id, modelo, cant, pos, strStats, idObvi);
		}
	}

	static void addHechizo(Hechizo hechizo) {
		Hechizos.put(hechizo.getID(), hechizo);
	}

	static void addObjModelo(ObjetoModelo obj) {
		ObjModelos.put(obj.getID(), obj);
	}

	public static Hechizo getHechizo(int id) {
		Hechizo hech = Hechizos.get(id);
		if (hech == null || !Hechizos.containsKey(id)) {
			hech = GestorSQL.cargarHechizos(id);
		}
		return hech;
	}

	public static ObjetoModelo getObjModelo(int id) {
		return ObjModelos.get(id);
	}

	static void addMobModelo(int id, MobModelo mob) {
		MobModelos.put(id, mob);
	}

	public static MobModelo getMobModelo(int id) {
		MobModelo monst = MobModelos.get(id);
		if (monst == null || !MobModelos.containsKey(id)) {
			monst = GestorSQL.cargarMonstruo(id);
		}
		return monst;
	}

	public static List<Personaje> getPJsEnLinea() {
		List<Personaje> online = new ArrayList<>();
		for (Personaje perso : Personajes.values()) {
			if (perso.enLinea() && perso.getCuenta().getEntradaPersonaje() != null) {
				if (perso.getCuenta().getEntradaPersonaje() != null) {
					online.add(perso);
				}
			}
		}
		return online;
	}

	public synchronized static void addObjeto(Objeto obj, boolean salvarSQL) {
		if (obj == null) {
			return;
		}
		if (obj.getID() == 0) {
			obj.setID(getSigIDObjeto());
		}
		if (Objetos.containsKey(obj.getID())) {
			return;
		}
		Objetos.put(obj.getID(), obj);
		if (salvarSQL) {
			GestorSQL.AGREGAR_NUEVO_OBJETO(obj);
		}
	}

	public static Objeto getObjeto(int id) {
		Objeto obj = Objetos.get(id);
		if (obj == null || !Objetos.containsKey(id)) {
			obj = GestorSQL.cargarObjetos(id);
		}
		return obj;
	}

	public synchronized static void eliminarObjeto(int id) {
		Objetos.remove(id);
		GestorSQL.BORRAR_OBJETO(id);
		if (Mascotas.containsKey(id)) {
			eliminarMascota(id);
		}
	}

	public static Mascota getMascota(int id) {
		return Mascotas.get(id);
	}

	public static synchronized void addMascota(Mascota mascota) {
		if (!Mascotas.containsKey(mascota.getID())) {
			Mascotas.put(mascota.getID(), mascota);
		}
	}

	public static Collection<Mascota> getTodasMascotas() {
		return Mascotas.values();
	}

	static void agregarMascotaModelo(int id, MascotaModelo mascota) {
		MascotasModelos.put(id, mascota);
	}

	public static MascotaModelo getMascotaModelo(int id) {
		return MascotasModelos.get(id);
	}

	public static Set<Integer> getIDTodasMascotas() {
		return Mascotas.keySet();
	}

	private static void eliminarMascota(int id) {
		GestorSQL.BORRAR_MASCOTA(id);
		Mascotas.remove(id);
	}

	public static Dragopavo getDragopavoPorID(int id) {
		Dragopavo drag = Dragopavos.get(id);
		if (drag == null || !Dragopavos.containsKey(id)) {
			drag = GestorSQL.cargarDragopavo(id);
		}
		return Dragopavos.get(id);
	}

	public synchronized static void addDragopavo(Dragopavo DP) {
		Dragopavos.put(DP.getID(), DP);
	}

	static short getEstado() {
		return _estado;
	}

	public static Mercadillo getPuestoMerca(int mapaID) {
		return PuestosMercadillos.get(mapaID);
	}

	public synchronized static int sigIDLineaMercadillo() {
		sigIDLineaMerca++;
		return sigIDLineaMerca;
	}

	public synchronized static void addObjMercadillo(int cuentaID, int idPuestoMerca, ObjetoMercadillo objMercadillo) {
		if (ObjMercadillos.get(cuentaID) == null) {
			ObjMercadillos.put(cuentaID, new HashMap<>());
		}
		if (ObjMercadillos.get(cuentaID).get(idPuestoMerca) == null) {
			ObjMercadillos.get(cuentaID).put(idPuestoMerca, new ArrayList<>());
		}
		ObjMercadillos.get(cuentaID).get(idPuestoMerca).add(objMercadillo);
	}

	public synchronized static void borrarObjMercadillo(int cuentaID, int idPuestoMerca, ObjetoMercadillo objMerca) {
		ObjMercadillos.get(cuentaID).get(idPuestoMerca).remove(objMerca);
	}

	synchronized static void addPuestoMercadillo(Mercadillo mercadillo) {
		PuestosMercadillos.put(mercadillo.getMapaMercadillo(), mercadillo);
	}

	public static Map<Integer, ArrayList<ObjetoMercadillo>> getMisObjetos(int cuentaID) {
		if (ObjMercadillos.get(cuentaID) == null) {
			ObjMercadillos.put(cuentaID, new HashMap<>());
		}
		return ObjMercadillos.get(cuentaID);
	}

	public static Objevivo getObjevivos(int idObjevivo) {
		return Objevivos.get(idObjevivo);
	}

	public synchronized static void addObjevivo(Objevivo objevivo) {
		Objevivos.put(objevivo.getID(), objevivo);
	}

	public static Animacion getAnimacion(int animacionId) {
		return Animaciones.get(animacionId);
	}

	static void addAnimation(Animacion animation) {
		Animaciones.put(animation.getId(), animation);
	}

	static void addReto(Reto reto) {
		Retos.put(reto.getId(), reto);
	}

	public static Reto getReto(int id) {
		return Retos.get(id);
	}

	public synchronized static void agregarTienda(Tienda tiendas, boolean salvar) {
		Tiendas.put(tiendas.getItemId(), tiendas);
		if (salvar) {
			GestorSQL.AGREGAR_ITEM_TIENDA(tiendas);
		}
	}

	public static Tienda getObjTienda(int id) {
		return Tiendas.get(id);
	}

	public synchronized static void borrarObjTienda(int id) {
		Tiendas.remove(id);
		GestorSQL.BORRAR_ITEM_TIENDA(id);
	}

	static void agregarCasa(Casa casa) {
		Casas.put(casa.getID(), casa);
	}

	public static Map<Integer, Casa> getCasas() {
		return Casas;
	}

	public static Casa getCasa(int id) {
		Casa casa = Casas.get(id);
		if (casa == null || !Casas.containsKey(id)) {
			casa = GestorSQL.cargarCasaID(id);
		}
		return casa;
	}

	synchronized static void addCercado(Cercado cercado) {
		if (cercado != null)
		 {
			Cercados.put(cercado.getMapa().getID(), cercado);
		// cercado.startMoverDrago();
		}
	}

	static Cercado getCercadoPorMap(int mapa) {
		return Cercados.get(mapa);
	}

	public static Collection<Cercado> todosCercados() {
		return Cercados.values();
	}

	public synchronized static void addPrisma(Prisma prisma) {
		Prismas.put(prisma.getID(), prisma);
	}

	public static Prisma getPrisma(int id) {
		return Prismas.get(id);
	}

	public static void borrarPrisma(int id) {
		Prismas.remove(id);
	}

	public static Collection<Prisma> TodosPrismas() {
		if (Prismas.size() > 0) {
			return Prismas.values();
		}
		return null;
	}

	public static void addCofre(Cofre cofre) {
		Cofres.put(cofre.getID(), cofre);
		if (sigIDCofre < cofre.getID()) {
			sigIDCofre = cofre.getID();
		}
	}

	public static Map<Integer, Cofre> getCofres() {
		return Cofres;
	}

	public static void addRecaudador(Recaudador recauda) {
		Recaudadores.put(recauda.getID(), recauda);
	}

	public static Recaudador getRecaudador(int id) {
		Recaudador recau = Recaudadores.get(id);
		if (recau == null && !Recaudadores.containsKey(id)) {
			recau = GestorSQL.cargarRecaudador(id);
		}
		return recau;
	}

	public static void borrarRecaudador(int id) {
		Recaudadores.remove(id);
		GestorSQL.BORRAR_RECAUDADOR(id);
	}

	public static Map<Integer, Recaudador> getTodosRecaudadores() {
		return Recaudadores;
	}

	public static Recaudador getRecauPorMapaID(int id) {
		for (Entry<Integer, Recaudador> perco : Recaudadores.entrySet()) {
			if (perco.getValue().getMapaID() == id) {
				return Recaudadores.get(perco.getValue().getID());
			}
		}
		return null;
	}

	public static int cantRecauDelGremio(int gremiodID) {
		int i = 0;
		for (Entry<Integer, Recaudador> perco : Recaudadores.entrySet()) {
			if (perco.getValue().getGremioID() == gremiodID) {
				i++;
			}
		}
		return i;
	}

	public static void addRankingPVP(RankingPVP rank) {
		if (RankingsPVP.get(rank.getID()) == null) {
			RankingsPVP.put(rank.getID(), rank);
		}
	}

	private static void delRankingPVP(int id) {
		RankingsPVP.remove(id);
	}

	public static boolean estaRankingPVP(int id) {
		if (RankingsPVP.get(id) != null) {
			return true;
		}
		return false;
	}

	public static RankingPVP getRanking(int id) {
		return RankingsPVP.get(id);
	}

	static String nombreLiderRankingPVP(String cual) {
		Map<Integer, String> nameslist = new TreeMap<>();
		if (RankingsPVP.size() <= 0) {
			return "Ninguno";
		} else {
			for (RankingPVP rank : RankingsPVP.values()) {
				nameslist.put(rank.getVictorias(), rank.getNombre());
			}
			int size = nameslist.size();
			if (size <= 0) {
				return "Ninguno";
			}
			Object[] keys = nameslist.keySet().toArray();
			if (cual == "1") {
				if (size < 1) {
					return "Ninguno";
				}
				int prim = (int) keys[keys.length - 1];
				return nameslist.get(prim);
			} else if (cual == "2") {
				if (size < 2) {
					return "Ninguno";
				}
				int segund = (int) keys[keys.length - 2];
				return nameslist.get(segund);
			} else if (cual == "3") {
				if (size < 3) {
					return "Ninguno";
				}
				int tercer = (int) keys[keys.length - 3];
				return nameslist.get(tercer);
			}
		}
		return "";
	}

	public static float getBalanceMundo(int alineacion) {
		int cant = 0;
		for (SubArea subarea : SubAreas.values()) {
			if (subarea.getAlineacion() == alineacion) {
				cant++;
			}
		}
		if (cant == 0) {
			return 0.0F;
		}
		return (float) Math.rint(10 * cant / 4 / 10);
	}

	public static float getBalanceArea(Area area, int alineacion) {
		int cant = 0;
		if (area == null) {
			return 0.0F;
		}
		for (SubArea subarea : SubAreas.values()) {
			if ((subarea.getArea() == area) && (subarea.getAlineacion() == alineacion)) {
				cant++;
			}
		}
		if (cant == 0) {
			return 0.0F;
		}
		return (float) Math.rint(1000 * cant / area.getSubAreas().size() / 10);
	}

	public static String prismasGeoposicion(int alineacion) {
		StringBuilder str = new StringBuilder("");
		boolean primero = false;
		int subareas = 0;
		for (SubArea subarea : SubAreas.values()) {
			if (!subarea.getConquistable()) {
				continue;
			}
			if (primero) {
				str.append(";");
			}
			str.append(subarea.getID() + "," + (subarea.getAlineacion() == 0 ? -1 : subarea.getAlineacion()) + ",0,");
			if (getPrisma(subarea.getPrismaID()) == null) {
				str.append(0 + ",1");
			} else {
				str.append((subarea.getPrismaID() == 0 ? 0 : MundoDofus.getPrisma(subarea.getPrismaID()).getMapa())
						+ ",1");
			}
			primero = true;
			subareas++;
		}
		if (alineacion == 1) {
			str.append("|" + Area._bontas);
		} else if (alineacion == 2) {
			str.append("|" + Area._brakmars);
		} else if (alineacion == 3) {
			str.append("|" + Area._mercenarios);
		}
		str.append("|" + Areas.size() + "|");
		primero = false;
		for (Area area : Areas.values()) {
			if (area.getAlineacion() == 0) {
				continue;
			}
			if (primero) {
				str.append(";");
			}
			str.append(area.getID() + "," + area.getAlineacion() + ",1," + (area.getPrismaID() == 0 ? 0 : 1));
			primero = true;
		}
		if (alineacion == 1) {
			return Area._bontas + "|" + subareas + "|"
					+ (subareas - (SubArea._bontas + SubArea._brakmars + SubArea._mercenarios)) + "|" + str;
		} else if (alineacion == 2) {
			return Area._brakmars + "|" + subareas + "|"
					+ (subareas - (SubArea._bontas + SubArea._brakmars + SubArea._mercenarios)) + "|" + str;
		} else if (alineacion == 3) {
			return Area._mercenarios + "|" + subareas + "|"
					+ (subareas - (SubArea._bontas + SubArea._brakmars + SubArea._mercenarios)) + "|" + str;
		}
		return str.toString();
	}

	public static void addEncarnacion(Encarnacion encarnacion) {
		Encarnaciones.put(encarnacion.getID(), encarnacion);
	}

	public static Encarnacion getEncarnacion(int id) {
		return Encarnaciones.get(id);
	}

	public static void addLibrosRunicos(LibrosRunicos librosRunicos) {
		LibrosRunicos.put(librosRunicos.getID(), librosRunicos);
	}

	public static LibrosRunicos getLibroRunico(int id) {
		return LibrosRunicos.get(id);
	}

	static void addTutorial(Tutorial tutorial) {
		Tutoriales.put(tutorial.getID(), tutorial);
	}

	public static Tutorial getTutorial(int id) {
		return Tutoriales.get(id);
	}

	public static class Drop {
		private int _objModeloID;
		private int _prospeccion;
		private int _probabilidad;
		private int _maximo;

		public Drop(int obj, int prosp, int probabilidad, int max) {
			_objModeloID = obj;
			_prospeccion = prosp;
			_probabilidad = probabilidad;
			_maximo = max;
		}

		public void setDropMax(int max) {
			_maximo = max;
		}

		public int getObjetoID() {
			return _objModeloID;
		}

		public int getProspReq() {
			return _prospeccion;
		}

		public int getProbabilidad() {
			return _probabilidad;
		}

		public int getDropMax() {
			return _maximo;
		}
	}

	public static class ItemSet {
		private int _id;
		private ArrayList<ObjetoModelo> _objetosModelos = new ArrayList<>();
		private ArrayList<Stats> _bonus = new ArrayList<>();

		ItemSet(int id, String items, String bonuses) {
			_id = id;
			for (String str : items.split(",")) {
				try {
					ObjetoModelo t = MundoDofus.getObjModelo(Integer.parseInt(str.trim()));
					if (t == null) {
						continue;
					}
					_objetosModelos.add(t);
				} catch (Exception e) {
				}
			}
			_bonus.add(new Stats());
			for (String str : bonuses.split(";")) {
				Stats S = new Stats();
				for (String str2 : str.split(",")) {
					try {
						String[] infos = str2.split(":");
						int stat = Integer.parseInt(infos[0]);
						int value = Integer.parseInt(infos[1]);
						S.addUnStat(stat, value);
					} catch (Exception e) {
					}
				}
				_bonus.add(S);
			}
		}

		public int getId() {
			return _id;
		}

		public Stats getBonusStatPorNroObj(int numb) {
			if (numb > _bonus.size()) {
				return new Stats();
			}
			return _bonus.get(numb - 1);
		}

		public ArrayList<ObjetoModelo> getObjetosModelos() {
			return _objetosModelos;
		}
	}

	public static class SuperArea {
		private int _id;
		private ArrayList<Area> _areas = new ArrayList<>();

		private SuperArea(int id) {
			_id = id;
		}

		void addArea(Area area) {
			_areas.add(area);
		}

		public int getID() {
			return _id;
		}
	}

	public static class Area {
		private int _id;
		private SuperArea _superArea;
		private String _nombre;
		private ArrayList<SubArea> _subAreas = new ArrayList<>();
		private int _alineacion;
		static int _bontas = 0;
		static int _brakmars = 0;
		private static int _mercenarios = 0;
		private int _prisma = 0;

		Area(int id, int superArea, String nombre, int alineacion, int prisma) {
			_id = id;
			_nombre = nombre;
			_superArea = MundoDofus.getSuperArea(superArea);
			if (_superArea == null) {
				_superArea = new SuperArea(superArea);
				MundoDofus.addSuperArea(_superArea);
			}
			_alineacion = 0;
			_prisma = 0;
			if (MundoDofus.getPrisma(prisma) != null) {
				_alineacion = alineacion;
				_prisma = prisma;
			}
			if (_alineacion == 1) {
				_bontas++;
			} else if (_alineacion == 2) {
				_brakmars++;
			} else if (_alineacion == 3) {
				_mercenarios++;
			}
		}

		public int getAlineacion() {
			return _alineacion;
		}

		public int getPrismaID() {
			return _prisma;
		}

		public void setPrismaID(int prisma) {
			_prisma = prisma;
		}

		public void setAlineacion(int alineacion) {
			if (_alineacion == 1 && alineacion == -1) {
				_bontas--;
			} else if (_alineacion == 2 && alineacion == -1) {
				_brakmars--;
			} else if (_alineacion == -1 && alineacion == 1) {
				_bontas++;
			} else if (_alineacion == -1 && alineacion == 2) {
				_brakmars++;
			} else if (_alineacion == -1 && alineacion == 3) {
				_mercenarios++;
			} else if (_alineacion == 3 && alineacion == -1) {
				_mercenarios--;
			}
			_alineacion = alineacion;
		}

		public String getNombre() {
			return _nombre;
		}

		public int getID() {
			return _id;
		}

		public SuperArea getSuperArea() {
			return _superArea;
		}

		void addSubArea(SubArea sa) {
			_subAreas.add(sa);
		}

		public ArrayList<SubArea> getSubAreas() {
			return _subAreas;
		}

		public ArrayList<Mapa> getMapas() {
			ArrayList<Mapa> mapas = new ArrayList<>();
			for (SubArea SA : _subAreas) {
				mapas.addAll(SA.getMapas());
			}
			return mapas;
		}
	}

	public static class SubArea {
		private int _id;
		private Area _area;
		private int _alineacion;
		private String _nombre;
		private ArrayList<Mapa> _mapas = new ArrayList<>();
		private boolean _conquistable;
		private int _prisma;
		private static int _bontas = 0;
		private static int _brakmars = 0;
		private static int _mercenarios = 0;

		SubArea(int id, int areaID, int alineacion, String nombre, int conquistable, int prisma) {
			_id = id;
			_nombre = nombre;
			_area = MundoDofus.getArea(areaID);
			_alineacion = 0;
			_conquistable = conquistable == 0;
			_prisma = prisma;
			if (MundoDofus.getPrisma(prisma) != null) {
				_alineacion = alineacion;
				_prisma = prisma;
			}
			if (_alineacion == 1) {
				_bontas++;
			} else if (_alineacion == 2) {
				_brakmars++;
			} else if (_alineacion == 3) {
				_mercenarios++;
			}
		}

		public String getNombre() {
			return _nombre;
		}

		public int getPrismaID() {
			return _prisma;
		}

		public void setPrismaID(int prisma) {
			_prisma = prisma;
		}

		public boolean getConquistable() {
			return _conquistable;
		}

		public int getID() {
			return _id;
		}

		public Area getArea() {
			return _area;
		}

		public int getAlineacion() {
			return _alineacion;
		}

		public void setAlineacion(int alineacion) {
			if (_alineacion == 1 && alineacion == -1) {
				_bontas--;
			} else if (_alineacion == 2 && alineacion == -1) {
				_brakmars--;
			} else if (_alineacion == -1 && alineacion == 1) {
				_bontas++;
			} else if (_alineacion == -1 && alineacion == 2) {
				_brakmars++;
			} else if (_alineacion == 3 && alineacion == -1) {
				_mercenarios--;
			} else if (_alineacion == -1 && alineacion == 3) {
				_mercenarios++;
			}
			_alineacion = alineacion;
		}

		public ArrayList<Mapa> getMapas() {
			return _mapas;
		}

		public void addMapa(Mapa mapa) {
			_mapas.add(mapa);
		}
	}

	public static class Duo<L, R> {
		public L _primero;
		public R _segundo;

		public Duo(L s, R i) {
			_primero = s;
			_segundo = i;
		}
	}

	public static class ObjInteractivoModelo {
		private int _id;
		private int _tiempoRespuesta;
		private int _duracion;
		private int _animacionnPJ;
		private boolean _caminable;
		private int _nivelobj;

		ObjInteractivoModelo(int id, int tiempoRespuesta, int duracion, int spritePJ, boolean caminable, int nivel) {
			_id = id;
			_tiempoRespuesta = tiempoRespuesta;
			_duracion = duracion;
			_animacionnPJ = spritePJ;
			_caminable = caminable;
			_nivelobj = nivel;
		}

		public int getID() {
			return _id;
		}

		public int getNivelObjeto() {
			return _nivelobj;
		}

		public boolean esCaminable() {
			return _caminable;
		}

		public int getTiempoRespuesta() {
			return _tiempoRespuesta;
		}

		public int getDuracion() {
			return _duracion;
		}

		public int getAnimacionPJ() {
			return _animacionnPJ;
		}
	}

	public static class Trueque {
		private Personaje _perso;
		private PrintWriter _out;
		private ArrayList<Duo<Integer, Integer>> _objetos = new ArrayList<>();
		private boolean _ok;
		private String _objetoPedir = "";
		private String _objetoDar = "";
		private int _objetoConseguir = -1;
		private int _cantObjConseguir = -1;
		private boolean _resucitar = false;
		private boolean _perfecciona = false;
		private boolean _torrepvm = false;
		private boolean _elemento = false;
		@SuppressWarnings("unused")
		private boolean _itemmision = false;
		private int _idMascota = -1;

		public Trueque(Personaje perso, String Objetopedir, String Objetodar) {
			_perso = perso;
			_objetoPedir = Objetopedir;
			_objetoDar = Objetodar;
			if (_objetoPedir.equalsIgnoreCase("resucitar")) {
				_resucitar = true;
			} else if (_objetoPedir.equalsIgnoreCase("perfecciona")) {
				_perfecciona = true;
			} else if (_objetoPedir.equalsIgnoreCase("torrepvm")) {
				_torrepvm = true;
			} else if (_objetoPedir.equalsIgnoreCase("elemento")) {
				_elemento = true;
			} else if (_objetoPedir.equalsIgnoreCase("itemmision")) {
				_elemento = true;
			}
			_out = _perso.getCuenta().getEntradaPersonaje().getOut();
		}

		synchronized public void botonOK(int id) {
			int i = 0;
			if (_perso.getID() == id) {
				i = 1;
			}
			if (i == 1) {
				_ok = !_ok;
				GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out, _ok, id);
			} else {
				return;
			}
			int objetosr1 = 0;
			int objetosr2 = 0;
			int objetosr3 = 0;
			if (_elemento) {
				boolean esarma = false;
				boolean espocima = false;
				Objeto objidarma = null;
				int pocimaid = 0;
				for (Duo<Integer, Integer> duo1 : _objetos) {
					int idObj = duo1._primero;
					Objeto objb = _perso.getObjNoEquip(idObj, 1);
					if (objb == null) {
						return;
					}
					int cant = duo1._segundo;
					if (cant >= 2) {
						return;
					}
					switch (objb.getModelo().getTipo()) {
					case 2:
					case 3:
					case 4:
					case 5:
					case 6:
					case 7:
					case 8:
						esarma = true;
						objidarma = objb;
						break;
					case 26:
						pocimaid = objb.getModelo().getID();
						espocima = true;
						break;
					}
				}
				if (objidarma == null) {
					_ok = false;
					return;
				}
				if (esarma && espocima) {
					boolean completado = false;
					for (String misi : _perso.get_misiones().split("\\|")) {
						if (misi.equals("")) {
							continue;
						}
						int misid = Integer.parseInt(misi.split(";")[0]);
						int fin = Integer.parseInt(misi.split(";")[1]);
						if (misid == 10500 && fin == 1) {
							completado = true;
							break;
						}
					}
					if (_perso.getKamas() < 500000) {
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 181),
								Colores.ROJO);
						return;
					}
					if (_perso._resets < 1) {
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 182),
								Colores.ROJO);
						return;
					}
					if (!completado) {
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 183),
								Colores.ROJO);
						return;
					}
					Objeto objClon = Objeto.clonarObjeto(objidarma, 1);
					int elemnt = 0;
					for (int i1 = 0; i1 < objClon.getEfectos().size(); i1++) {
						if (elemnt != 0) {
							break;
						}
						switch (pocimaid) {
						case 1345:
							elemnt = 99;
							break;
						case 1347:
							elemnt = 98;
							break;
						case 1348:
							elemnt = 97;
							break;
						case 1346:
							elemnt = 96;
							break;
						}
					}
					for (EfectoHechizo EH : objClon.getEfectos()) {
						if (EH.getEfectoID() >= 96 && EH.getEfectoID() <= 100) {
							String[] infos = EH.getArgs().split(";");
							try {
								int min = Integer.parseInt(infos[0], 16);
								int max = Integer.parseInt(infos[1], 16);
								int nuevoMin = (min * 100) / 100;
								int nuevoMax = (max * 100) / 100;
								if (nuevoMin == 0) {
									nuevoMin = 1;
								}
								String nuevoRango = "1d" + (nuevoMax - nuevoMin + 1) + "+" + (nuevoMin - 1);
								String nuevosArgs = Integer.toHexString(nuevoMin) + ";" + Integer.toHexString(nuevoMax)
										+ ";-1;-1;0;" + nuevoRango;
								EH.setArgs(nuevosArgs);
								EH.setEfectoID(elemnt);
								break;
							} catch (Exception e) {
							}
						}
					}
					if (!_perso.addObjetoSimilar(objClon, true, -1)) {
						MundoDofus.addObjeto(objClon, true);
						_perso.addObjetoPut(objClon);
						GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_out, objClon);
					}
					_perso.setKamas(_perso.getKamas() - 500000);
					GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
					_ok = true;
				} else {
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_perso.getCuenta().idioma, 184),
							Colores.ROJO);
					_ok = false;
				}
			}
			if (_torrepvm) {
				for (Duo<Integer, Integer> duo : _objetos) {
					int idObj = duo._primero;
					Objeto objb = _perso.getObjNoEquip(idObj, 1);
					if (objb == null) {
						_ok = false;
						return;
					}
					int cant = duo._segundo;
					if (objb.getModelo().getCondiciones().equals("RS>0")) {
						objetosr1 += cant;
					} else if (objb.getModelo().getCondiciones().equals("RS>1")) {
						objetosr2 += cant;
					} else if (objb.getModelo().getCondiciones().equals("RS>2")) {
						objetosr3 += cant;
					}
				}
				if (objetosr1 == 3 && objetosr2 == 0 && objetosr3 == 0 && _perso.getMapa().getID() == 29940) {// dar
																												// objeto
																												// r2
					_perso.crearItem(randomr2(), 1);
				} else if (objetosr1 == 0 && objetosr2 == 2 && objetosr3 == 0 && _perso.getMapa().getID() == 29950) {// dar
																														// objeto
																														// r3
					_perso.crearItem(randomr3(), 1);
				} else if (objetosr1 == 0 && objetosr2 == 0 && objetosr3 == 2 && _perso.getMapa().getID() == 29960) {// dar
																														// objeto
																														// r3
																														// epico
					_perso.crearItem(randomr3epico(), 1);
				} else {
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, "No puedo darte nada por eso...", Colores.ROJO);
					_ok = false;
					_perso.torrepvm = true;
				}
			}
			if (_ok) {
				aplicar();
			}
		}

		private int randomr2() {
			int random = Formulas.getRandomValor(1, 11);
			switch (random) {
			case 1:
				return 50079;
			case 2:
				return 50217;
			case 3:
				return 50219;
			case 4:
				return 50222;
			case 5:
				return 50054;
			case 6:
				return 50027;
			case 7:
				return 50024;
			case 8:
				return 40213;
			case 9:
				return 50217;
			case 10:
				return 50219;
			case 11:
				return 50222;
			default:
				return 50079;
			}
		}

		private int randomr3() {
			int random = Formulas.getRandomValor(1, 13);
			switch (random) {
			case 1:
				return 50270;
			case 2:
				return 50271;
			case 3:
				return 50272;
			case 4:
				return 50273;
			case 5:
				return 50274;
			case 6:
				return 50078;
			case 7:
				return 50079;
			case 8:
				return 50080;
			case 9:
				return 50081;
			case 10:
				return 50082;
			case 11:
				return 50059;
			case 12:
				return 50060;
			case 13:
				return 50061;
			default:
				return 50270;
			}
		}

		private int randomr3epico() {
			int random = Formulas.getRandomValor(1, 8);
			switch (random) {
			case 1:
				return 50250;
			case 2:
				return 50251;
			case 3:
				return 50218;
			case 4:
				return 50253;
			case 5:
				return 50254;
			case 6:
				return 50218;
			case 7:
				return 50226;
			case 8:
				return 50227;
			default:
				return 50250;
			}
		}

		synchronized public void cancel() {
			GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(_out);
			_perso.setTrueque(null);
		}

		synchronized private void aplicar() {
			for (Duo<Integer, Integer> duo : _objetos) {
				if (_perfecciona) {
					_perfecciona = false;
					_perso.perfecciona = false;
					_perso.torrepvm = false;
					_perso.pusoperfec = false;
					int idObj = duo._primero;
					Objeto objb = _perso.getObjNoEquip(idObj, 1);
					if (objb == null) {
						return;
					}
					ObjetoModelo OM3 = MundoDofus.getObjModelo(objb.getModelo().getID());
					if (OM3 == null) {
						return;
					}
					if (objb != null) {
						if (objb.getObjeviID() > 0) {
							GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso,
									Idiomas.getTexto(_perso.getCuenta().idioma, 145), Colores.ROJO);
							return;
						}
						long precio = 1000000;
						if (_perso.getKamas() >= precio) {
							_perso.setKamas(_perso.getKamas() - precio);
							_perso.borrarObjetoEliminar(objb.getID(), 1, true);
							Objeto objx = OM3.crearObjDesdeModelo(1, true);
							MundoDofus.addObjeto(objx, true);
							objx.addTextoStat(990, "Objeto Perfeccionado");
							_perso.addObjetoPut(objx);
							GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_perso, objx);
							GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso);
							GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
							GestorSQL.SALVAR_OBJETO(objx);
							GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso,
									Idiomas.getTexto(_perso.getCuenta().idioma, 147), Colores.VERDE);
						} else {
							GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso,
									Idiomas.getTexto(_perso.getCuenta().idioma, 148), Colores.ROJO);
						}
					}
				} else {
					int idObj = duo._primero;
					int cant = duo._segundo;
					if (_resucitar) {
						if (idObj == 8012) {
							continue;
						}
					}
					if (cant == 0) {
						continue;
					}
					if (!_perso.tieneObjetoID(idObj)) {
						cant = 0;
						continue;
					}
					Objeto obj = MundoDofus.getObjeto(idObj);
					int nuevaCant = obj.getCantidad() - cant;
					if (_resucitar) {
						if (nuevaCant < 1) {
							GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_out, idObj);
							if (obj.getModelo().getTipo() != 90) {
								_perso.borrarObjetoRemove(idObj);
								MundoDofus.eliminarObjeto(idObj);
							} else {
								_idMascota = idObj;
							}
						} else {
							obj.setCantidad(nuevaCant);
							GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso, obj);
						}
					} else {
						if (nuevaCant < 1) {
							_perso.borrarObjetoRemove(idObj);
							GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_out, idObj);
							MundoDofus.eliminarObjeto(idObj);
						} else {
							obj.setCantidad(nuevaCant);
							GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso, obj);
						}
					}
				}
			}
			if (!_perfecciona) {
				if (_resucitar) {
					if (_idMascota != -1) {
						Objeto objMasc = MundoDofus.getObjeto(_idMascota);
						objMasc.setCantidad(1);
						objMasc.setPosicion(-1);
						objMasc.setIDModelo(CentroInfo.resucitarMascota(objMasc.getModelo().getID()));
						// GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out, 'O', "+", _idMascota +
						// "|1|" + CentroInfo.resucitarMascota(objMasc.getModelo().getID()) + "|" +
						// objMasc.convertirStatsAString(0) );
						GestorSalida.ENVIAR_OAKO_APARECER_OBJETOMASC(_out, objMasc);
					}

				} else {
					if (_objetoConseguir != -1 && _cantObjConseguir != -1) {
						Objeto nuevoObjeto = MundoDofus.getObjModelo(_objetoConseguir)
								.crearObjDesdeModelo(_cantObjConseguir, false);
						if (!_perso.addObjetoSimilar(nuevoObjeto, true, -1)) {
							MundoDofus.addObjeto(nuevoObjeto, true);
							_perso.addObjetoPut(nuevoObjeto);
							GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_out, nuevoObjeto);
						}
					}
				}
			}
			_perso.setTrueque(null);
			GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso);
			GestorSalida.ENVIAR_EV_INTERCAMBIO_EFECTUADO(_out, 'a');
			GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
			_perso.setOcupado(false);
		}

		private String lastadd = "";

		synchronized public void addObjetoTrueque(int idObjeto, int cantObj) {
			_ok = false;
			String str = idObjeto + "|" + cantObj;
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out, _ok, _perso.getID());
			Duo<Integer, Integer> duo = getDuoPorIDObjeto(_objetos, idObjeto);
			if (duo != null) {
				duo._segundo += cantObj;
				GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_out, 'O', "+", idObjeto + "|" + duo._segundo);
				if (_resucitar) {
					if (_idMascota != -1) {
						GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out, 'O', "-", "" + _idMascota);
					}
				} else if (_objetoConseguir != -1) {
					GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out, 'O', "-", "" + 1);
				}
				_objetoConseguir = -1;
				_idMascota = -1;
				String[] pedir = _objetoPedir.split("\\|");
				int cantSolicitadas = 0;
				int j = 0;
				for (Duo<Integer, Integer> acouple : _objetos) {
					ObjetoModelo objModelo = MundoDofus.getObjeto(acouple._primero).getModelo();
					int idModelo = objModelo.getID();
					if (_resucitar) {
						if (objModelo.getTipo() == 90) {
							_idMascota = acouple._primero;
						}
					}
					for (String apedir : pedir) {
						if (idModelo == Integer.parseInt(apedir.split(",")[0])) {
							int cantidades = (acouple._segundo / Integer.parseInt(apedir.split(",")[1]));
							if (cantidades < 1) {
								continue;
							}
							if (cantSolicitadas == 0 || cantidades < cantSolicitadas) {
								cantSolicitadas = cantidades;
							}
							j++;
							break;
						}
						continue;
					}
				}
				if (cantSolicitadas > 0 && (pedir.length == j)) {
					if (_resucitar) {
						if (_idMascota != -1) {
							Objeto mascota = MundoDofus.getObjeto(_idMascota);
							GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out, 'O', "+",
									_idMascota + "|1|" + CentroInfo.resucitarMascota(mascota.getModelo().getID()) + "|"
											+ mascota.convertirStatsAString(0) + ",320#0#0#1");
						}
					} else {
						int idObjModDar = Integer.parseInt(_objetoDar.split(",")[0]);
						int cant = Integer.parseInt(_objetoDar.split(",")[1]);
						int cantFinal = cant * cantSolicitadas;
						GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out, 'O', "+", 1 + "|" + cantFinal + "|"
								+ idObjModDar + "|" + MundoDofus.getObjModelo(idObjModDar).getStringStatsObj());
						_objetoConseguir = idObjModDar;
						_cantObjConseguir = cantFinal;
					}
				}
				return;
			}
			GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_out, 'O', "+", str);
			_objetos.add(new Duo<>(idObjeto, cantObj));
			if (_elemento) {
				boolean esarma = false;
				boolean espocima = false;
				Objeto objidarma = null;
				int pocimaid = 0;
				for (Duo<Integer, Integer> duo1 : _objetos) {
					int idObj = duo1._primero;
					Objeto objb = _perso.getObjNoEquip(idObj, 1);
					if (objb == null) {
						return;
					}
					int cant = duo1._segundo;
					if (cant >= 2) {
						return;
					}
					switch (objb.getModelo().getTipo()) {
					case 2:
					case 3:
					case 4:
					case 5:
					case 6:
					case 7:
					case 8:
						esarma = true;
						objidarma = objb;
						break;
					case 26:
						pocimaid = objb.getModelo().getID();
						espocima = true;
						break;
					}
				}
				Objeto objClon = Objeto.clonarObjeto(objidarma, 1);
				if (objClon == null) {
					return;
				}
				int elemnt = 0;
				for (int i = 0; i < objClon.getEfectos().size(); i++) {
					if (elemnt != 0) {
						break;
					}
					switch (pocimaid) {
					case 1345:
						elemnt = 99;
						break;
					case 1347:
						elemnt = 98;
						break;
					case 1348:
						elemnt = 97;
						break;
					case 1346:
						elemnt = 96;
						break;
					}
				}
				for (EfectoHechizo EH : objClon.getEfectos()) {
					if (EH.getEfectoID() >= 96 && EH.getEfectoID() <= 100) {
						String[] infos = EH.getArgs().split(";");
						try {
							int min = Integer.parseInt(infos[0], 16);
							int max = Integer.parseInt(infos[1], 16);
							int nuevoMin = (min * 100) / 100;
							int nuevoMax = (max * 100) / 100;
							if (nuevoMin == 0) {
								nuevoMin = 1;
							}
							String nuevoRango = "1d" + (nuevoMax - nuevoMin + 1) + "+" + (nuevoMin - 1);
							String nuevosArgs = Integer.toHexString(nuevoMin) + ";" + Integer.toHexString(nuevoMax)
									+ ";-1;-1;0;" + nuevoRango;
							EH.setArgs(nuevosArgs);
							EH.setEfectoID(elemnt);
							break;
						} catch (Exception e) {
						}
					}
				}
				if (esarma && espocima) {
					lastadd = "1|" + objidarma.getModelo().getID() + "|" + objClon.convertirStatsAString(1);
					GestorSalida.enviar(_out, "EmKO+1|" + lastadd);
				} else {
					if (lastadd != "") {
						GestorSalida.enviar(_out, "EmKO-1|" + lastadd);
						lastadd = "";
					}
				}
			}
			_objetoConseguir = -1;
			_idMascota = -1;
			String[] pedir = _objetoPedir.split("\\|");
			int cantSolicitadas = 0;
			int j = 0;
			for (Duo<Integer, Integer> acouple : _objetos) {
				ObjetoModelo objModelo = MundoDofus.getObjeto(acouple._primero).getModelo();
				int idModelo = objModelo.getID();
				if (_resucitar) {
					if (objModelo.getTipo() == 90) {
						_idMascota = acouple._primero;
					}
				}
				for (String apedir : pedir) {
					if (idModelo == Integer.parseInt(apedir.split(",")[0])) {
						int cantidades = (acouple._segundo / Integer.parseInt(apedir.split(",")[1]));
						if (cantidades < 1) {
							continue;
						}
						if (cantSolicitadas == 0 || cantidades < cantSolicitadas) {
							cantSolicitadas = cantidades;
						}
						j++;
						break;
					}
					continue;
				}
			}
			if (cantSolicitadas > 0 && (pedir.length == j)) {
				if (_resucitar) {
					if (_idMascota != -1) {
						Objeto mascota = MundoDofus.getObjeto(_idMascota);
						GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out, 'O', "+",
								_idMascota + "|1|" + CentroInfo.resucitarMascota(mascota.getModelo().getID()) + "|"
										+ mascota.convertirStatsAString(0) + ",320#0#0#1");
					}
				} else {
					int idObjModDar = Integer.parseInt(_objetoDar.split(",")[0]);
					int cant = Integer.parseInt(_objetoDar.split(",")[1]);
					int cantFinal = cant * cantSolicitadas;
					GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out, 'O', "+", 1 + "|" + cantFinal + "|"
							+ idObjModDar + "|" + MundoDofus.getObjModelo(idObjModDar).getStringStatsObj());
					_objetoConseguir = idObjModDar;
					_cantObjConseguir = cantFinal;
				}
			}
			return;
		}

		synchronized public void quitarObjeto(int idObjeto, int cantObjeto) {
			_ok = false;
			if (lastadd != "") {
				GestorSalida.enviar(_out, "EmKO-1|" + lastadd);
				lastadd = "";
			}
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out, _ok, _perso.getID());
			Duo<Integer, Integer> duo = getDuoPorIDObjeto(_objetos, idObjeto);
			if (duo._segundo == null) {
				return;
			}
			int nuevaCant = duo._segundo - cantObjeto;
			if (nuevaCant < 1) {
				_objetos.remove(duo);
				GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_out, 'O', "-", "" + idObjeto);
			} else {
				duo._segundo = nuevaCant;
				GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_out, 'O', "+", idObjeto + "|" + nuevaCant);
			}
			if (_resucitar) {
				if (_idMascota != -1) {
					GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out, 'O', "-", "" + _idMascota);
				}
			} else if (_objetoConseguir != -1) {
				GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out, 'O', "-", "" + 1);
			}
			_objetoConseguir = -1;
			_idMascota = -1;
			String[] pedir = _objetoPedir.split("\\|");
			int cantSolicitadas = 0;
			int j = 0;
			for (Duo<Integer, Integer> acouple : _objetos) {
				ObjetoModelo objModelo = MundoDofus.getObjeto(acouple._primero).getModelo();
				int idModelo = objModelo.getID();
				if (_resucitar) {
					if (objModelo.getTipo() == 90) {
						_idMascota = acouple._primero;
					}
				}
				for (String apedir : pedir) {
					if (idModelo == Integer.parseInt(apedir.split(",")[0])) {
						int cantidades = (acouple._segundo / Integer.parseInt(apedir.split(",")[1]));
						if (cantidades < 1) {
							continue;
						}
						if (cantSolicitadas == 0 || cantidades < cantSolicitadas) {
							cantSolicitadas = cantidades;
						}
						j++;
						break;
					}
					continue;
				}
			}
			if (cantSolicitadas > 0 && (pedir.length == j)) {
				if (_resucitar) {
					if (_idMascota != -1) {
						Objeto mascota = MundoDofus.getObjeto(_idMascota);
						GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out, 'O', "+",
								_idMascota + "|1|" + CentroInfo.resucitarMascota(mascota.getModelo().getID()) + "|"
										+ mascota.convertirStatsAString(0) + ",320#0#0#1");
					}
				} else {
					int idObjModDar = Integer.parseInt(_objetoDar.split(",")[0]);
					int cant = Integer.parseInt(_objetoDar.split(",")[1]);
					int cantFinal = cant * cantSolicitadas;
					GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out, 'O', "+", 1 + "|" + cantFinal + "|"
							+ idObjModDar + "|" + MundoDofus.getObjModelo(idObjModDar).getStringStatsObj());
					_objetoConseguir = idObjModDar;
					_cantObjConseguir = cantFinal;
				}
			}
			return;
		}

		synchronized private Duo<Integer, Integer> getDuoPorIDObjeto(ArrayList<Duo<Integer, Integer>> objetos, int id) {
			for (Duo<Integer, Integer> duo : objetos) {
				if (duo._primero == id) {
					return duo;
				}
			}
			return null;
		}

		public synchronized int getCantObj(int objetoID, int personajeId) {
			ArrayList<Duo<Integer, Integer>> objetos = null;
			if (_perso.getID() == personajeId) {
				objetos = _objetos;
			}
			for (Duo<Integer, Integer> duo : objetos) {
				if (duo._primero == objetoID) {
					return duo._segundo;
				}
			}
			return 0;
		}
	}

	public static class Intercambio {
		private Personaje _perso1;
		private Personaje _perso2;
		private PrintWriter _out1;
		private PrintWriter _out2;
		private long _kamas1 = 0;
		private long _kamas2 = 0;
		private ArrayList<Duo<Integer, Integer>> _objetos1 = new ArrayList<>();
		private ArrayList<Duo<Integer, Integer>> _objetos2 = new ArrayList<>();
		private boolean _ok1;
		private boolean _ok2;

		public Intercambio(Personaje p1, Personaje p2) {
			_perso1 = p1;
			_perso2 = p2;
			_out1 = _perso1.getCuenta().getEntradaPersonaje().getOut();
			_out2 = _perso2.getCuenta().getEntradaPersonaje().getOut();
		}

		synchronized public long getKamas(int id) {
			int i = 0;
			if (_perso1.getID() == id) {
				i = 1;
			} else if (_perso2.getID() == id) {
				i = 2;
			}
			if (i == 1) {
				return _kamas1;
			} else if (i == 2) {
				return _kamas2;
			}
			return 0;
		}

		synchronized public void botonOK(int id) {
			int i = 0;
			if (_perso1.getID() == id) {
				i = 1;
			} else if (_perso2.getID() == id) {
				i = 2;
			}
			if (i == 1) {
				_ok1 = !_ok1;
				GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out1, _ok1, id);
				GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out2, _ok1, id);
			} else if (i == 2) {
				_ok2 = !_ok2;
				GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out1, _ok2, id);
				GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out2, _ok2, id);
			} else {
				return;
			}
			if (_ok1 && _ok2) {
				aplicar();
			}
		}

		synchronized public void setKamas(int id, long k) {
			_ok1 = false;
			_ok2 = false;
			int i = 0;
			if (_perso1.getID() == id) {
				i = 1;
			} else if (_perso2.getID() == id) {
				i = 2;
			}
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out1, _ok1, _perso1.getID());
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out2, _ok1, _perso1.getID());
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out1, _ok2, _perso2.getID());
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out2, _ok2, _perso2.getID());
			if (i == 1) {
				_kamas1 = k;
				GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_out1, 'G', "", k + "");
				GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out2, 'G', "", k + "");
			} else if (i == 2) {
				_kamas2 = k;
				GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_out2, 'G', "", k + "");
				GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out1, 'G', "", k + "");
			}
		}

		synchronized public void cancel() {
			GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(_out1);
			GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(_out2);
			_perso1.setIntercambiandoCon(0);
			_perso2.setIntercambiandoCon(0);
			_perso1.setIntercambio(null);
			_perso2.setIntercambio(null);
			_perso1.setOcupado(false);
			_perso2.setOcupado(false);
		}

		synchronized private void aplicar() {
			_perso1.addKamas((-_kamas1 + _kamas2));
			_perso2.addKamas((-_kamas2 + _kamas1));
			for (Duo<Integer, Integer> duo : _objetos1) {
				int idObjeto = duo._primero;
				int cant = duo._segundo;
				if (cant == 0) {
					continue;
				}
				Objeto obj = MundoDofus.getObjeto(idObjeto);
				if ((obj.getCantidad() - cant) < 1) {
					_perso1.borrarObjetoRemove(idObjeto);
					GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_out1, idObjeto);
					if (_perso2.addObjetoSimilar(obj, true, -1)) {
						MundoDofus.eliminarObjeto(idObjeto);
					} else {
						_perso2.addObjetoPut(obj);
						GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_out2, obj);
					}
				} else {
					obj.setCantidad(obj.getCantidad() - cant);
					GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso1, obj);
					Objeto nuevoObj = Objeto.clonarObjeto(obj, cant);
					if (!_perso2.addObjetoSimilar(nuevoObj, true, idObjeto)) {
						MundoDofus.addObjeto(nuevoObj, true);
						_perso2.addObjetoPut(nuevoObj);
						GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_out2, nuevoObj);
					}
				}
			}
			for (Duo<Integer, Integer> duo : _objetos2) {
				int idObjeto = duo._primero;
				int cant = duo._segundo;
				if (cant == 0) {
					continue;
				}
				Objeto obj = MundoDofus.getObjeto(idObjeto);
				if ((obj.getCantidad() - cant) < 1) {
					_perso2.borrarObjetoRemove(idObjeto);
					GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_out2, idObjeto);
					if (_perso1.addObjetoSimilar(obj, true, -1)) {
						MundoDofus.eliminarObjeto(idObjeto);
					} else {
						_perso1.addObjetoPut(obj);
						obj.setPosicion(-1);
						GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_out1, obj);
					}
				} else {
					obj.setCantidad(obj.getCantidad() - cant);
					GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso2, obj);
					Objeto nuevoObj = Objeto.clonarObjeto(obj, cant);
					if (!_perso1.addObjetoSimilar(nuevoObj, true, idObjeto)) {
						MundoDofus.addObjeto(nuevoObj, true);
						_perso1.addObjetoPut(nuevoObj);
						nuevoObj.setPosicion(-1);
						GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_out1, nuevoObj);
					}
				}
			}
			_perso1.setIntercambiandoCon(0);
			_perso2.setIntercambiandoCon(0);
			_perso1.setIntercambio(null);
			_perso2.setIntercambio(null);
			GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso1);
			GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso2);
			GestorSalida.ENVIAR_EV_INTERCAMBIO_EFECTUADO(_out1, 'a');
			GestorSalida.ENVIAR_EV_INTERCAMBIO_EFECTUADO(_out2, 'a');
			GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso1);
			GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso2);
			_perso1.setOcupado(false);
			_perso2.setOcupado(false);
		}

		synchronized public void addObjeto(Objeto obj, int cant, int idPerso) {
			_ok1 = false;
			_ok2 = false;
			int idObj = obj.getID();
			int i = 0;
			if (_perso1.getID() == idPerso) {
				i = 1;
			} else {
				i = 2;
			}
			if (cant == 1) {
				cant = 1;
			}
			String str = idObj + "|" + cant;
			String add = "|" + obj.getModelo().getID() + "|" + obj.convertirStatsAString(0);
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out1, _ok1, _perso1.getID());
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out2, _ok1, _perso1.getID());
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out1, _ok2, _perso2.getID());
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out2, _ok2, _perso2.getID());
			if (i == 1) {
				Duo<Integer, Integer> duo = getDuoPorIDObjeto(_objetos1, idObj);
				if (duo != null) {
					duo._segundo += cant;
					GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_out1, 'O', "+", idObj + "|" + duo._segundo);
					GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out2, 'O', "+", idObj + "|" + duo._segundo + add);
					return;
				}
				GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_out1, 'O', "+", str);
				GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out2, 'O', "+", str + add);
				_objetos1.add(new Duo<>(idObj, cant));
			} else if (i == 2) {
				Duo<Integer, Integer> duo = getDuoPorIDObjeto(_objetos2, idObj);
				if (duo != null) {
					duo._segundo += cant;
					GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_out2, 'O', "+", idObj + "|" + duo._segundo);
					GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out1, 'O', "+", idObj + "|" + duo._segundo + add);
					return;
				}
				GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_out2, 'O', "+", str);
				GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out1, 'O', "+", str + add);
				_objetos2.add(new Duo<>(idObj, cant));
			}
		}

		synchronized public void borrarObjeto(Objeto obj, int cant, int idPerso) {
			int i = 0;
			if (_perso1.getID() == idPerso) {
				i = 1;
			} else {
				i = 2;
			}
			_ok1 = false;
			_ok2 = false;
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out1, _ok1, _perso1.getID());
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out2, _ok1, _perso1.getID());
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out1, _ok2, _perso2.getID());
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out2, _ok2, _perso2.getID());
			int idObj = obj.getID();
			String add = "|" + obj.getModelo().getID() + "|" + obj.convertirStatsAString(0);
			if (i == 1) {
				Duo<Integer, Integer> duo = getDuoPorIDObjeto(_objetos1, idObj);
				if (duo != null) {
					int nuevaCantidad = duo._segundo - cant;
					if (nuevaCantidad < 1) {
						_objetos1.remove(duo);
						GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_out1, 'O', "-", "" + idObj);
						GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out2, 'O', "-", "" + idObj);
					} else {
						duo._segundo = nuevaCantidad;
						GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_out1, 'O', "+", idObj + "|" + nuevaCantidad);
						GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out2, 'O', "+",
								idObj + "|" + nuevaCantidad + add);
					}
				}
			} else if (i == 2) {
				Duo<Integer, Integer> duo = getDuoPorIDObjeto(_objetos2, idObj);
				if (duo != null) {
					int nuevaCantidad = duo._segundo - cant;
					if (nuevaCantidad < 1) {
						_objetos2.remove(duo);
						GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out1, 'O', "-", "" + idObj);
						GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_out2, 'O', "-", "" + idObj);
					} else {
						duo._segundo = nuevaCantidad;
						GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out1, 'O', "+",
								idObj + "|" + nuevaCantidad + add);
						GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_out2, 'O', "+", idObj + "|" + nuevaCantidad);
					}
				}
			}
		}

		synchronized private Duo<Integer, Integer> getDuoPorIDObjeto(ArrayList<Duo<Integer, Integer>> objetos, int id) {
			for (Duo<Integer, Integer> duo : objetos) {
				if (duo._primero == id) {
					return duo;
				}
			}
			return null;
		}

		public synchronized int getCantObjeto(int objetoID, int idPerso) {
			ArrayList<Duo<Integer, Integer>> objetos;
			if (_perso1.getID() == idPerso) {
				objetos = _objetos1;
			} else {
				objetos = _objetos2;
			}
			for (Duo<Integer, Integer> duo : objetos) {
				if (duo._primero == objetoID) {
					return duo._segundo;
				}
			}
			return 0;
		}
	}

	public static class InvitarTaller {
		private Personaje _artesano1;
		private Personaje _cliente2;
		private PrintWriter _out1;
		private PrintWriter _out2;
		private long _kamasPago = 0;
		private long _kamasSiSeConsigue = 0;
		private ArrayList<Duo<Integer, Integer>> _objArtesano1 = new ArrayList<>();
		private ArrayList<Duo<Integer, Integer>> _objCliente2 = new ArrayList<>();
		private boolean _ok1;
		private boolean _ok2;
		private int _maxIngredientes;
		private ArrayList<Duo<Integer, Integer>> _objetosPago = new ArrayList<>();
		private ArrayList<Duo<Integer, Integer>> _objetosSiSeConsegui = new ArrayList<>();

		public InvitarTaller(Personaje p1, Personaje p2, int max) {
			_artesano1 = p1;
			_cliente2 = p2;
			_out1 = _artesano1.getCuenta().getEntradaPersonaje().getOut();
			_out2 = _cliente2.getCuenta().getEntradaPersonaje().getOut();
			_maxIngredientes = max;
		}

		public long getKamasSiSeConsigue() {
			return _kamasSiSeConsigue;
		}

		public long getKamasPaga() {
			return _kamasPago;
		}

		synchronized public void botonOK(int id) {
			int i = 0;
			if (_artesano1.getID() == id) {
				i = 1;
			} else if (_cliente2.getID() == id) {
				i = 2;
			}
			if (i == 1) {
				_ok1 = !_ok1;
				GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out1, _ok1, id);
				GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out2, _ok1, id);
			} else if (i == 2) {
				_ok2 = !_ok2;
				GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out1, _ok2, id);
				GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out2, _ok2, id);
			} else {
				return;
			}
			if (_ok1 && _ok2) {
				aplicar();
			}
		}

		public void setKamas(int id, long k, long kamasT) {
			_ok1 = false;
			_ok2 = false;
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out1, _ok1, _artesano1.getID());
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out2, _ok1, _artesano1.getID());
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out1, _ok2, _cliente2.getID());
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out2, _ok2, _cliente2.getID());
			if (id == 1) {
				long kamasTotal = _kamasSiSeConsigue + k;
				if (kamasTotal > kamasT) {
					k = kamasT - _kamasSiSeConsigue;
				}
				_kamasPago = k;
			} else {
				long kamasTotal = _kamasPago + k;
				if (kamasTotal > kamasT) {
					k = kamasT - _kamasPago;
				}
				_kamasSiSeConsigue = k;
			}
			GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_out1, id, "G", "+", k + "");
			GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_out2, id, "G", "+", k + "");
		}

		synchronized public void cancel() {
			GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(_out1);
			GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(_out2);
			_artesano1.setIntercambiandoCon(0);
			_cliente2.setIntercambiandoCon(0);
			_artesano1.setTallerInvitado(null);
			_cliente2.setTallerInvitado(null);
			_artesano1.setOcupado(false);
			_cliente2.setOcupado(false);
		}

		private void aplicar() {
			AccionTrabajo trabajo = _artesano1.getHaciendoTrabajo();
			boolean resultado = trabajo.iniciarTrabajoPago(_artesano1, _cliente2, _objArtesano1, _objCliente2);
			StatsOficio oficio = _artesano1.getOficioPorTrabajo(trabajo.getIDTrabajo());
			if (oficio != null) {
				if (oficio.esPagable()) {
					if (resultado) {
						_cliente2.setKamas(_cliente2.getKamas() - _kamasSiSeConsigue);
						_artesano1.setKamas(_artesano1.getKamas() + _kamasSiSeConsigue);
						for (Duo<Integer, Integer> duo : _objetosSiSeConsegui) {
							int idObjeto = duo._primero;
							int cant = duo._segundo;
							if (cant == 0) {
								continue;
							}
							Objeto obj = MundoDofus.getObjeto(idObjeto);
							if ((obj.getCantidad() - cant) < 1) {
								_cliente2.borrarObjetoRemove(idObjeto);
								GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_out2, idObjeto);
								if (_artesano1.addObjetoSimilar(obj, true, -1)) {
									MundoDofus.eliminarObjeto(idObjeto);
								} else {
									_artesano1.addObjetoPut(obj);
									GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_out1, obj);
								}
							} else {
								obj.setCantidad(obj.getCantidad() - cant);
								GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_cliente2, obj);
								Objeto nuevoObj = Objeto.clonarObjeto(obj, cant);
								if (!_artesano1.addObjetoSimilar(nuevoObj, true, idObjeto)) {
									MundoDofus.addObjeto(nuevoObj, true);
									_artesano1.addObjetoPut(nuevoObj);
									GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_out1, nuevoObj);
								}
							}
						}
					}
					if (!oficio.esGratisSiFalla() || resultado) {
						_cliente2.setKamas(_cliente2.getKamas() - _kamasPago);
						_artesano1.setKamas(_artesano1.getKamas() + _kamasPago);
						for (Duo<Integer, Integer> duo : _objetosPago) {
							int idObjeto = duo._primero;
							int cant = duo._segundo;
							if (cant == 0) {
								continue;
							}
							Objeto obj = MundoDofus.getObjeto(idObjeto);
							if ((obj.getCantidad() - cant) < 1) {
								_cliente2.borrarObjetoRemove(idObjeto);
								GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_out2, idObjeto);
								if (_artesano1.addObjetoSimilar(obj, true, -1)) {
									MundoDofus.eliminarObjeto(idObjeto);
								} else {
									_artesano1.addObjetoPut(obj);
									GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_out1, obj);
								}
							} else {
								obj.setCantidad(obj.getCantidad() - cant);
								GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_cliente2, obj);
								Objeto nuevoObj = Objeto.clonarObjeto(obj, cant);
								if (!_artesano1.addObjetoSimilar(nuevoObj, true, idObjeto)) {
									MundoDofus.addObjeto(nuevoObj, true);
									_artesano1.addObjetoPut(nuevoObj);
									GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_out1, nuevoObj);
								}
							}
						}
					}
				}
			}
			_objetosSiSeConsegui.clear();
			_objetosPago.clear();
			_objArtesano1.clear();
			_objCliente2.clear();
			_kamasPago = 0;
			_kamasSiSeConsigue = 0;
			GestorSalida.ENVIAR_As_STATS_DEL_PJ(_artesano1);
			GestorSalida.ENVIAR_As_STATS_DEL_PJ(_cliente2);
			GestorSalida.ENVIAR_Ec_INICIAR_RECETA(_artesano1, _artesano1.getForjaEcK());
			GestorSalida.ENVIAR_Ec_INICIAR_RECETA(_cliente2, _cliente2.getForjaEcK());
		}

		synchronized public void addObjeto(Objeto obj, int cant, int idPerso) {
			if (cantObjetosActual() >= _maxIngredientes) {
				return;
			}
			_ok1 = false;
			_ok2 = false;
			int idObj = obj.getID();
			int i = 0;
			if (_artesano1.getID() == idPerso) {
				i = 1;
			} else {
				i = 2;
			}
			if (cant == 1) {
				cant = 1;
			}
			String str = idObj + "|" + cant;
			String add = "|" + obj.getModelo().getID() + "|" + obj.convertirStatsAString(0);
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out1, _ok1, _artesano1.getID());
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out2, _ok1, _artesano1.getID());
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out1, _ok2, _cliente2.getID());
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out2, _ok2, _cliente2.getID());
			if (i == 1) {
				Duo<Integer, Integer> duo = getDuoPorIDObjeto(_objArtesano1, idObj);
				if (duo != null) {
					duo._segundo += cant;
					GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_out1, 'O', "+", "" + idObj + "|" + duo._segundo);
					GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out2, 'O', "+",
							"" + idObj + "|" + duo._segundo + add);
					return;
				}
				GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_out1, 'O', "+", str);
				GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out2, 'O', "+", str + add);
				_objArtesano1.add(new Duo<>(idObj, cant));
			} else if (i == 2) {
				Duo<Integer, Integer> duo = getDuoPorIDObjeto(_objCliente2, idObj);
				if (duo != null) {
					duo._segundo += cant;
					GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_out2, 'O', "+", "" + idObj + "|" + duo._segundo);
					GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out1, 'O', "+",
							"" + idObj + "|" + duo._segundo + add);
					return;
				}
				GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_out2, 'O', "+", str);
				GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out1, 'O', "+", str + add);
				_objCliente2.add(new Duo<>(idObj, cant));
			}
		}

		synchronized public void borrarObjeto(Objeto obj, int cant, int idPerso) {
			int i = 0;
			if (_artesano1.getID() == idPerso) {
				i = 1;
			} else {
				i = 2;
			}
			_ok1 = false;
			_ok2 = false;
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out1, _ok1, _artesano1.getID());
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out2, _ok1, _artesano1.getID());
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out1, _ok2, _cliente2.getID());
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out2, _ok2, _cliente2.getID());
			int idObj = obj.getID();
			String add = "|" + obj.getModelo().getID() + "|" + obj.convertirStatsAString(0);
			if (i == 1) {
				Duo<Integer, Integer> duo = getDuoPorIDObjeto(_objArtesano1, idObj);
				int nuevaCantidad = duo._segundo - cant;
				if (nuevaCantidad < 1) {
					_objArtesano1.remove(duo);
					GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_out1, 'O', "-", "" + idObj);
					GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out2, 'O', "-", "" + idObj);
				} else {
					duo._segundo = nuevaCantidad;
					GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_out1, 'O', "+", "" + idObj + "|" + nuevaCantidad);
					GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out2, 'O', "+",
							"" + idObj + "|" + nuevaCantidad + add);
				}
			} else if (i == 2) {
				Duo<Integer, Integer> duo = getDuoPorIDObjeto(_objCliente2, idObj);
				int nuevaCantidad = duo._segundo - cant;
				if (nuevaCantidad < 1) {
					_objCliente2.remove(duo);
					GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out1, 'O', "-", "" + idObj);
					GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_out2, 'O', "-", "" + idObj);
				} else {
					duo._segundo = nuevaCantidad;
					GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out1, 'O', "+",
							"" + idObj + "|" + nuevaCantidad + add);
					GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_out2, 'O', "+", "" + idObj + "|" + nuevaCantidad);
				}
			}
		}

		synchronized public void addObjetoPaga(Objeto obj, int cant, int idPago) {
			if (cantObjetosActual() >= _maxIngredientes) {
				return;
			}
			_ok1 = false;
			_ok2 = false;
			int idObj = obj.getID();
			if (cant == 1) {
				cant = 1;
			}
			String str = idObj + "|" + cant;
			String add = "|" + obj.getModelo().getID() + "|" + obj.convertirStatsAString(0);
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out1, _ok1, _artesano1.getID());
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out2, _ok1, _artesano1.getID());
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out1, _ok2, _cliente2.getID());
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out2, _ok2, _cliente2.getID());
			if (idPago == 1) {
				Duo<Integer, Integer> duo = getDuoPorIDObjeto(_objetosPago, idObj);
				if (duo != null) {
					duo._segundo += cant;
					GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_out1, idPago, "O", "+",
							idObj + "|" + duo._segundo + add);
					GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_out2, idPago, "O", "+",
							idObj + "|" + duo._segundo);
					return;
				}
				GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_out1, idPago, "O", "+", str + add);
				GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_out2, idPago, "O", "+", str);
				_objetosPago.add(new Duo<>(idObj, cant));
			} else {
				Duo<Integer, Integer> duo = getDuoPorIDObjeto(_objetosSiSeConsegui, idObj);
				if (duo != null) {
					duo._segundo += cant;
					GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_out1, idPago, "O", "+",
							idObj + "|" + duo._segundo + add);
					GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_out2, idPago, "O", "+",
							idObj + "|" + duo._segundo);
					return;
				}
				GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_out1, idPago, "O", "+", str + add);
				GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_out2, idPago, "O", "+", str);
				_objetosSiSeConsegui.add(new Duo<>(idObj, cant));
			}
		}

		synchronized public void borrarObjetoPaga(Objeto obj, int cant, int idPago) {
			int idObj = obj.getID();
			_ok1 = false;
			_ok2 = false;
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out1, _ok1, _artesano1.getID());
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out2, _ok1, _artesano1.getID());
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out1, _ok2, _cliente2.getID());
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out2, _ok2, _cliente2.getID());
			String add = "|" + obj.getModelo().getID() + "|" + obj.convertirStatsAString(0);
			if (idPago == 1) {
				Duo<Integer, Integer> duo = getDuoPorIDObjeto(_objetosPago, idObj);
				if (duo == null) {
					return;
				}
				int nuevaCantidad = duo._segundo - cant;
				GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_out1, idPago, "O", "-", idObj + "");
				GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_out2, idPago, "O", "-", idObj + "");
				if (nuevaCantidad < 1) {
					_objetosPago.remove(duo);
				} else {
					duo._segundo = nuevaCantidad;
					GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_out1, idPago, "O", "+",
							idObj + "|" + nuevaCantidad + add);
					GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_out2, idPago, "O", "+",
							idObj + "|" + nuevaCantidad);
				}
			} else {
				Duo<Integer, Integer> duo = getDuoPorIDObjeto(_objetosSiSeConsegui, idObj);
				if (duo == null) {
					return;
				}
				int nuevaCantidad = duo._segundo - cant;
				GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_out1, idPago, "O", "-", idObj + "");
				GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_out2, idPago, "O", "-", idObj + "");
				if (nuevaCantidad < 1) {
					_objetosSiSeConsegui.remove(duo);
				} else {
					duo._segundo = nuevaCantidad;
					GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_out1, idPago, "O", "+",
							idObj + "|" + nuevaCantidad + add);
					GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_out2, idPago, "O", "+",
							idObj + "|" + nuevaCantidad);
				}
			}
		}

		synchronized private Duo<Integer, Integer> getDuoPorIDObjeto(ArrayList<Duo<Integer, Integer>> objetos, int id) {
			for (Duo<Integer, Integer> duo : objetos) {
				if (duo._primero == id) {
					return duo;
				}
			}
			return null;
		}

		public synchronized int getCantObjeto(int idObj, int idPerso) {
			ArrayList<Duo<Integer, Integer>> objetos;
			if (_artesano1.getID() == idPerso) {
				objetos = _objArtesano1;
			} else {
				objetos = _objCliente2;
			}
			for (Duo<Integer, Integer> duo : objetos) {
				if (duo._primero == idObj) {
					return duo._segundo;
				}
			}
			return 0;
		}

		public synchronized int getCantObjetoPago(int idObj, int idPerso) {
			ArrayList<Duo<Integer, Integer>> objetos;
			if (idPerso == 1) {
				objetos = _objetosPago;
			} else {
				objetos = _objetosSiSeConsegui;
			}
			for (Duo<Integer, Integer> duo : objetos) {
				if (duo._primero == idObj) {
					return duo._segundo;
				}
			}
			return 0;
		}

		private int cantObjetosActual() {
			int cant = _objArtesano1.size() + _objCliente2.size();
			return cant;
		}
	}

	public static class ExpNivel {
		public long _personaje;
		public int _oficio;
		public int _montura;
		public int _pvp;
		long _gremio;

		ExpNivel(long perso, int oficio, int montura, int pvp, int encarnacion) {
			_personaje = perso;
			_oficio = oficio;
			_montura = montura;
			_pvp = pvp;
			_gremio = _personaje * 20;
		}
	}

	public static double getOverPerEffet(final int effect) {
		double r = 0.0;
		switch (effect) {
		case 111: {
			r = 0.0;
			break;
		}
		case 78: {
			r = 404.0;
			break;
		}
		case 110: {
			r = 404.0;
			break;
		}
		case 114: {
			r = 0.0;
			break;
		}
		case 115: {
			r = 3.0;
			break;
		}
		case 117: {
			r = 0.0;
			break;
		}
		case 118: {
			r = 101.0;
			break;
		}
		case 119: {
			r = 101.0;
			break;
		}
		case 120: {
			r = 0.0;
			break;
		}
		case 112: {
			r = 5.0;
			break;
		}
		case 122: {
			r = 0.0;
			break;
		}
		case 123: {
			r = 101.0;
			break;
		}
		case 124: {
			r = 33.0;
			break;
		}
		case 125: {
			r = 404.0;
			break;
		}
		case 126: {
			r = 101.0;
			break;
		}
		case 128: {
			r = 0.0;
			break;
		}
		case 138: {
			r = 50.0;
			break;
		}
		case 142: {
			r = 50.0;
			break;
		}
		case 158: {
			r = 404.0;
			break;
		}
		case 160: {
			r = 0.0;
			break;
		}
		case 161: {
			r = 0.0;
			break;
		}
		case 174: {
			r = 1010.0;
			break;
		}
		case 176: {
			r = 33.0;
			break;
		}
		case 178: {
			r = 5.0;
			break;
		}
		case 182: {
			r = 3.0;
			break;
		}
		case 210: {
			r = 16.0;
			break;
		}
		case 211: {
			r = 16.0;
			break;
		}
		case 212: {
			r = 16.0;
			break;
		}
		case 213: {
			r = 16.0;
			break;
		}
		case 214: {
			r = 16.0;
			break;
		}
		case 225: {
			r = 6.0;
			break;
		}
		case 226: {
			r = 50.0;
			break;
		}
		case 240: {
			r = 50.0;
			break;
		}
		case 241: {
			r = 50.0;
			break;
		}
		case 242: {
			r = 50.0;
			break;
		}
		case 243: {
			r = 50.0;
			break;
		}
		case 244: {
			r = 50.0;
			break;
		}
		case 250: {
			r = 16.0;
			break;
		}
		case 251: {
			r = 16.0;
			break;
		}
		case 252: {
			r = 16.0;
			break;
		}
		case 253: {
			r = 16.0;
			break;
		}
		case 254: {
			r = 16.0;
			break;
		}
		case 260: {
			r = 50.0;
			break;
		}
		case 261: {
			r = 50.0;
			break;
		}
		case 262: {
			r = 50.0;
			break;
		}
		case 263: {
			r = 50.0;
			break;
		}
		case 264: {
			r = 50.0;
			break;
		}
		}
		return r;
	}

	public static double getPwrPerEffet(final int effect) {
		double r = 0.0;
		switch (effect) {
		case 111: {
			r = 100.0;
			break;
		}
		case 78: {
			r = 90.0;
			break;
		}
		case 110: {
			r = 0.25;
			break;
		}
		case 114: {
			r = 100.0;
			break;
		}
		case 115: {
			r = 30.0;
			break;
		}
		case 117: {
			r = 51.0;
			break;
		}
		case 118: {
			r = 1.0;
			break;
		}
		case 119: {
			r = 1.0;
			break;
		}
		case 120: {
			r = 100.0;
			break;
		}
		case 112: {
			r = 20.0;
			break;
		}
		case 122: {
			r = 1.0;
			break;
		}
		case 123: {
			r = 1.0;
			break;
		}
		case 124: {
			r = 3.0;
			break;
		}
		case 125: {
			r = 0.25;
			break;
		}
		case 126: {
			r = 1.0;
			break;
		}
		case 128: {
			r = 90.0;
			break;
		}
		case 138: {
			r = 2.0;
			break;
		}
		case 142: {
			r = 2.0;
			break;
		}
		case 158: {
			r = 0.25;
			break;
		}
		case 160: {
			r = 1.0;
			break;
		}
		case 161: {
			r = 1.0;
			break;
		}
		case 174: {
			r = 0.1;
			break;
		}
		case 176: {
			r = 3.0;
			break;
		}
		case 178: {
			r = 20.0;
			break;
		}
		case 182: {
			r = 30.0;
			break;
		}
		case 210: {
			r = 6.0;
			break;
		}
		case 211: {
			r = 6.0;
			break;
		}
		case 212: {
			r = 6.0;
			break;
		}
		case 213: {
			r = 6.0;
			break;
		}
		case 214: {
			r = 6.0;
			break;
		}
		case 225: {
			r = 15.0;
			break;
		}
		case 226: {
			r = 2.0;
			break;
		}
		case 240: {
			r = 2.0;
			break;
		}
		case 241: {
			r = 2.0;
			break;
		}
		case 242: {
			r = 2.0;
			break;
		}
		case 243: {
			r = 2.0;
			break;
		}
		case 244: {
			r = 2.0;
			break;
		}
		case 250: {
			r = 6.0;
			break;
		}
		case 251: {
			r = 6.0;
			break;
		}
		case 252: {
			r = 6.0;
			break;
		}
		case 253: {
			r = 6.0;
			break;
		}
		case 254: {
			r = 6.0;
			break;
		}
		case 260: {
			r = 2.0;
			break;
		}
		case 261: {
			r = 2.0;
			break;
		}
		case 262: {
			r = 2.0;
			break;
		}
		case 263: {
			r = 2.0;
			break;
		}
		case 264: {
			r = 2.0;
			break;
		}
		}
		return r;
	}
}
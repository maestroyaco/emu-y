package estaticos;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import real.ServidorGeneral;
import servidor.Colores;
import servidor.Estaticos;
import servidor.ServidorPersonaje;
import variables.Guerras;
import variables.Idiomas;
import variables.Invasion;
import variables.Mapa;
import variables.Mapa.Cercado;
import variables.Personaje;
import variables.Recaudador;
import sincronizador.SincronizadorCliente;

@SuppressWarnings("unused")
public class Emu {
	private static final String CONFIG_ARCHIVO = "config.txt";
	private static final String CONFIG_ARCHIVO2 = "config.txt";
	public static ServidorPersonaje _servidorPersonaje;
	public static ServidorGeneral _servidorGeneral;

	//evento Minotobola
	public static boolean Minotobola = false;
	public static Mapa mapaMino1 = null;// mapa Minotobola normal
	public static Mapa mapaMino2 = null;// mapa minotobola dificil
	public static String idmapaMino1 =  "31990,385";
	public static String idmapaMino2 = "30994,398";
	public static int cellidMino;


	// INVASION MONSTRUOS
	public static boolean Maldicion;
	public static boolean Bufo;
	public static boolean enPeleaMega;
	public static String tiempoInvasion;
	public static boolean SistemaHechizo;
	public static boolean SistemaClase;
	public static int GremioId;
	// PASE BATALLA
	public static int ptsTorneo;
	public static int ptsPvP;
	public static int ptsPrism;
	public static int ptsRecaudador;
	public static int ptsQuest;
	public static int ptsDailyQuest;
	public static int ptsZaapDiscover;
	// variables torneo
	public static boolean TorneoOn;
	public static boolean empezoTorneo;
	public static int faseTorneo;
	public static boolean usaReferidos;
	public static int torneoR;
	// Servidor
	public static boolean SoloEntrante;
	public static boolean Cerrando;
	public static boolean Corriendo;
	static boolean Salvando;
	public static boolean Halloween;
	private static boolean agresionrecau;
	public static boolean activaFusiones;
	public static boolean tallerAbierto;
	public static boolean SOLOSYS;
	public static int KOLICHAS;
	public static boolean SOLOGM;
	public static int LIMITEPA;
	public static int LIMITEPM;
	public static int intentos;
	public static int resis[];
	public static int resisfijas[];
	public static int stats[];
	public static int maximaresis;
	static boolean estaIniciado;
	public static String IP_PC_SERVER = "";
	public static ArrayList<Integer> objetosLoot = new ArrayList<>();
	public static ArrayList<Integer> objetosLoot2 = new ArrayList<>();
	public static ArrayList<Integer> regaloMis = new ArrayList<>();
	public static ArrayList<Integer> objetosDiarias = new ArrayList<>();
	public static ArrayList<Integer> misionesPerso = new ArrayList<>();
	public static int tiempoLootBox;
	public static short INICIO_BONUS_ESTRELLAS_RECURSOS;
	public static int SEGUNDOS_ESTRELLAS_RECURSOS;// segundos (15 minutos)
	public static boolean PARAM_ESTRELLAS_RECURSOS;
	public static short MAX_BONUS_ESTRELLAS_RECURSOS;
	public static boolean PARAM_REINICIAR_ESTRELLAS_SI_LLEGA_MAX;
	static String BD_HOST;
	static String BD_USUARIO;
	static String BD_PASS;
	static String BD_PORT;
	static String BDDOFUSONLINE;
	public static String NOMBRE_SERVER;
	public static String MENSAJE_BIENVENIDA_1;
	public static String COLOR_MENSAJE;
	public static boolean MOSTRAR_RECIBIDOS = true;
	static boolean MOSTRAR_ENVIOS_SOS = true;
	static boolean MOSTRAR_ENVIOS_STD = true;
	static ArrayList<Integer> ALIMENTOS_MONTURA;
	public static ArrayList<Integer> ARMAS_ENCARNACIONES;
	private static String ARMAS;
	public static ArrayList<Integer> LIBROS_RUNICOS;
	private static String LIBROSRUNICOS;
	
	public static int SERVER_ID = 1;
	public static int PUERTO_SERVIDOR;
	public static int PUERTO_JUEGO;
	public static int PUERTO_SINCRONIZADOR;
	public static String IP_MULTILOGIN = "127.0.0.1";
	
	public static int PUERTO_ANTIHACK;	
	public static int MAX_PJS_POR_CUENTA;
	public static int MAX_MULTI_CUENTAS;
	public static int MAX_NIVEL_OFICIO;
	public static int TIEMPO_ARENA;
	static int BD_COMMIT;
	public static int LIMITE_JUGADORES;
	public static int INICIAR_NIVEL;
	public static int INICIAR_KAMAS;
	public static int MAX_NIVEL;
	public static int NIVEL_PA1;
	public static int CANTIDAD_PA1;
	public static int NIVEL_PM1;
	public static int CANTIDAD_PM1;
	public static boolean PERMITIR_PVP;
	public static boolean USAR_MOBS;
	public static boolean ACTIVAR_GUERRA;
	public static int LIMITE_ARTESANOS_TALLER;
	public static int CANT_DROP;
	public static int CHAPAS_MISION;
	public static int RATE_DROP;
	public static float RATE_XP_PVP;
	public static float RATE_XP_PVM;
	public static float RATE_KAMAS;
	public static boolean MONTURAYMASCOTA;
	static float RATE_HONOR;
	public static float RATE_XP_OFICIO;
	public static int PUNTOS_STATS_POR_NIVEL_OMEGA;
	public static int NIVEL_MAX_OMEGA;
	public static float RATE_PORC_FM;
	public static float RATE_CRIANZA_PAVOS;
	public static int RATE_TIEMPO_ALIMENTACION;
	public static int RATE_TIEMPO_PARIR;
	private static float DEFECTO_XP_PVM;
	private static float DEFECTO_XP_PVP;
	private static float DEFECTO_XP_OFICIO;
	private static float DEFECTO_XP_HONOR;
	private static float DEFECTO_PORC_FM;
	private static int DEFECTO_DROP;
	private static float DEFECTO_KAMAS;
	private static float DEFECTO_CRIANZA_PAVOS;
	private static int DEFECTO_TIEMPO_ALIMENTACION;
	private static int DEFECTO_TIEMPO_PARIR;
	public static Timer _globalTime;
	private static BufferedWriter Log_MJ;
	private static int volores;
	static {
		Emu.SistemaClase = true;
		Emu.Maldicion = false;
		Emu.Bufo = false;
		Emu.enPeleaMega = false;
		Emu.tiempoInvasion = "";
		Emu.SistemaHechizo = false;
		Emu.GremioId = -1;
		Emu.ptsTorneo = 20;
		Emu.ptsPvP = 6;
		Emu.ptsPrism = 2;
		Emu.ptsRecaudador = 3;
		Emu.ptsQuest = 4;
		Emu.ptsDailyQuest = 3;
		Emu.ptsZaapDiscover = 8;
		Emu.TorneoOn = false;
		Emu.empezoTorneo = false;
		Emu.faseTorneo = 0;
		Emu.usaReferidos = false;
		Emu.torneoR = 4;
		Emu.SoloEntrante = false;
		Emu.Cerrando = false;
		Emu.Corriendo = false;
		Emu.Salvando = false;
		Emu.Halloween = false;
		Emu.agresionrecau = false;
		Emu.activaFusiones = true;
		Emu.tallerAbierto = true;
		Emu.SOLOSYS = true;
		Emu.KOLICHAS = 2;
		Emu.SOLOGM = false;
		Emu.LIMITEPA = 12;
		Emu.LIMITEPM = 7;
		Emu.intentos = 0;
		Emu.resis = new int[] { 210, 211, 212, 213, 214 };
		Emu.resisfijas = new int[] { 240, 241, 242, 243, 244 };
		Emu.stats = new int[] { 118, 119, 123, 124, 126 };
		Emu.maximaresis = 50;
		Emu.PUNTOS_STATS_POR_NIVEL_OMEGA = 1;
		Emu.NIVEL_MAX_OMEGA = 401;
		Emu.estaIniciado = false;
		Emu.IP_PC_SERVER = "127.0.0.1";
		Emu.objetosLoot = new ArrayList<>();
		Emu.objetosLoot2 = new ArrayList<>();
		Emu.regaloMis = new ArrayList<>();
		Emu.objetosDiarias = new ArrayList<>();
		Emu.misionesPerso = new ArrayList<>();
		Emu.tiempoLootBox = 10800;
		Emu.INICIO_BONUS_ESTRELLAS_RECURSOS = 5;
		Emu.SEGUNDOS_ESTRELLAS_RECURSOS = 1;
		Emu.MAX_BONUS_ESTRELLAS_RECURSOS = 500;
		Emu.BD_HOST = "localhost";
		Emu.BD_USUARIO = "root";
		Emu.BD_PASS = "";
		Emu.BD_PORT = "3306";
		Emu.BDDOFUSONLINE = "";
		Emu.NOMBRE_SERVER = "Niifus";
		Emu.MENSAJE_BIENVENIDA_1 = "";
		Emu.COLOR_MENSAJE = "";
		Emu.MOSTRAR_RECIBIDOS = false;
		Emu.MOSTRAR_ENVIOS_SOS = false;
		Emu.MOSTRAR_ENVIOS_STD = false;
		Emu.ALIMENTOS_MONTURA = new ArrayList<>();
		Emu.ARMAS_ENCARNACIONES = new ArrayList<>();
		Emu.ARMAS = "9544,9545,9546,9547,9548,10125,10126,10127,10133,34737";
		Emu.LIBROS_RUNICOS = new ArrayList<>();
		Emu.LIBROSRUNICOS = "30947,30948,30949,30950";
		
		Emu.PUERTO_SERVIDOR = 474;
		Emu.PUERTO_JUEGO = 5556;
		Emu.PUERTO_SINCRONIZADOR = 9669;
		
		
		Emu.PUERTO_ANTIHACK = 5550;		
		Emu.MAX_PJS_POR_CUENTA = 5;
		Emu.MAX_MULTI_CUENTAS = 4;
		Emu.MAX_NIVEL_OFICIO = 100;
		Emu.TIEMPO_ARENA = 600000;
		Emu.BD_COMMIT = 600000;
		Emu.SERVER_ID = 2;
		Emu.LIMITE_JUGADORES = 500;
		Emu.INICIAR_NIVEL = 1;
		Emu.INICIAR_KAMAS = 1;
		Emu.MAX_NIVEL = 200;
		Emu.NIVEL_PA1 = 100;
		Emu.CANTIDAD_PA1 = 1;
		Emu.NIVEL_PM1 = 200;
		Emu.CANTIDAD_PM1 = 1;
		Emu.PERMITIR_PVP = false;
		Emu.USAR_MOBS = false;
		Emu.ACTIVAR_GUERRA = true;
		Emu.LIMITE_ARTESANOS_TALLER = 25;
		Emu.CANT_DROP = 1;
		Emu.CHAPAS_MISION = 5;
		Emu.RATE_DROP = 1;
		Emu.RATE_XP_PVP = 10.0f;
		Emu.RATE_XP_PVM = 1.0f;
		Emu.RATE_KAMAS = 1.0f;
		Emu.MONTURAYMASCOTA = true;
		Emu.RATE_HONOR = 1.0f;
		Emu.RATE_XP_OFICIO = 1.0f;
		Emu.RATE_PORC_FM = 1.0f;
		Emu.RATE_CRIANZA_PAVOS = 1.0f;
		Emu.RATE_TIEMPO_ALIMENTACION = 5;
		Emu.RATE_TIEMPO_PARIR = 1;
		Emu._globalTime = null;
		Emu.volores = 0;
		Emu.noguarda = false;
		Emu.entraOblig = true;

	}

	public static void creaLogs(Throwable e) {
		if (volores > 1000) {
			return;
		}
		if (e.getMessage() != null) {
			if (e.getMessage().contains("Socket closed")) {
				return;
			}
		}
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		String sStackTrace = sw.toString();

		File file = new File("logs.txt");
		if (file.exists()) {
		}
		String date = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + " > "
				+ Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + "h:" + Calendar.getInstance().get(+Calendar.MINUTE)
				+ "m:" + Calendar.getInstance().get(Calendar.SECOND) + "s";
		try {
			if (Log_MJ != null) {
				Log_MJ.write(e.getMessage() + " << Ubicaci�n [" + date + "] " + sStackTrace);
				Log_MJ.newLine();
				Log_MJ.flush();
			}
		} catch (IOException ex) {
		}
		++Emu.volores;
	}

	public static void main(String[] args) {
		try {
			Log_MJ = new BufferedWriter(new FileWriter("logs.txt", true));
		} catch (IOException e) {
		}
		/*
		 * if (!getPuerto()) {
		 * System.out.println("Imposible escoger puerto de servidor"); System.exit(0);
		 * return; }
		 */




		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				Corriendo = false;
				Emu.cerrarServer("shutdownhook");
				return;
			}
		});
		System.out.println("\t               NIIFUS EMU " + CentroInfo.VERSION_SERVIDOR);
		System.out.println("\t               Creado por Alex Daho,Modificado Por Timby y Jhon!");
		System.out.println("Cargando la configuracion");
		cargarConfiguracion();
		estaIniciado = true;
		System.out.print("Conexion a la base de datos :");
		if (GestorSQL.IniciarConexion()) {
			System.out.println(" Conexion OK");
		} else {
			System.out.println(" Conexion invalida");
			return;
		}
		Corriendo = true;
		System.out.println("Creaci�n del Server");		
		MundoDofus.crearServer();
		_servidorPersonaje = new ServidorPersonaje();
		_servidorGeneral = new ServidorGeneral();
		System.out.println("Lanzando el servidor en el puerto de juego " + PUERTO_JUEGO);
		System.out.println("Lanzando el servidor en el puerto de login " + PUERTO_SERVIDOR);
		System.out.println("Esperando conexiones :3");
		
		// Conectar al Multi-Login
		System.out.println("Conectando al Multi-Login...");
		SincronizadorCliente.getInstancia().iniciar();
		
		timers();
		Guerras.startWar();
		/*
		 * int actual = 70231; for (int i = 1; i < 85; i++) {
		 * System.out.println("INSERT INTO `objetos_modelo` VALUES ('"
		 * +actual+"', '123', 'Ornamento "
		 * +i+"', '1', '', '1', '-1', '10000', '0', '', '', '0', '0');");
		 *
		 * actual += 1; }
		 */
		/*
		 * BufferedReader reader; try { reader = new BufferedReader(new
		 * FileReader("original.txt")); String line = reader.readLine(); try { Log_MJ =
		 * new BufferedWriter(new FileWriter("logs.txt",true)); } catch (IOException e)
		 * {} while (line != null) { if (line=="") continue; String elstring = line; int
		 * hechizoid = Integer.parseInt(getValor("S[", "]", elstring)); String lvl1 =
		 * ""; String lvl2 = ""; String lvl3 = ""; String lvl4 = ""; String lvl5 = "";
		 * String lvl6 = "";
		 *
		 * lvl1 = (getValor("l1: ", ", l2", elstring)); lvl2 = (getValor("l2: ", ", l3",
		 * elstring)); lvl3 = (getValor("l3: ", ", l4", elstring)); lvl4 =
		 * (getValor("l4: ", ", l5", elstring)); lvl5 = (getValor("l5: ", ", l6",
		 * elstring)); if (lvl5 == null) lvl5 = (getValor("l5: ", "};", elstring)); lvl6
		 * = (getValor("l6: ", "};", elstring)); if (lvl2 == null) lvl2 = lvl1; if (lvl3
		 * == null && lvl2 != "") lvl3 = lvl2; if (lvl4 == null && lvl3 != "") lvl4 =
		 * lvl3; if (lvl5 == null && lvl4 != "") lvl5 = lvl4; if (lvl6 == null && lvl5
		 * != "") lvl6 = lvl5; GestorSQL.UPDATE_HECHIZOS(hechizoid, lvl1, lvl2, lvl3,
		 * lvl4, lvl5, lvl6); line = reader.readLine(); } reader.close(); } catch
		 * (IOException e) { e.printStackTrace(); } GestorSQL.cerrarCons();
		 */
		leerComandos();
	}

	@SuppressWarnings("unused")
	private static void test() {
		// GestorSQL.test2();
	}

	/*
	 * static String getValor(String val1, String val2, String elstr) { String title
	 * = StringUtils.substringBetween(elstr, val1, val2); return title; } private
	 * static void escribir (String texto) { try { if (Log_MJ != null) {
	 * Log_MJ.write(texto); Log_MJ.newLine(); Log_MJ.flush(); } } catch (IOException
	 * ex) {} }
	 */

	static boolean noguarda = false;

	private static void leerComandos() {
		BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
		try {
			System.out.println("\nEscribe el comando:");
			String line = buffer.readLine();
			if (line.contains("salir") || line.equalsIgnoreCase("cerrar") || line.equalsIgnoreCase("apagar")
					|| line.equalsIgnoreCase("stop") || line.equalsIgnoreCase("parar")) {
				System.out.println("Cerrando el servidor");
				System.exit(0);
			} else if (line.contains("salirsinguardar")) {
				noguarda = true;
				System.exit(0);
			} else if (line.contains("salvar")) {
				Thread t = new Thread(new salvarServidorPersonaje());
				t.start();
			} else if (line.contains("soloentrante")) {
				if (!SoloEntrante) {
					SoloEntrante = true;
				} else {
					SoloEntrante = false;
				}
			} else if (line.contains("debug")) {
				if (Emu.MOSTRAR_RECIBIDOS) {
					Emu.MOSTRAR_ENVIOS_SOS = false;
					Emu.MOSTRAR_ENVIOS_STD = false;
					Emu.MOSTRAR_RECIBIDOS = false;
				} else {
					Emu.MOSTRAR_ENVIOS_SOS = false;
					Emu.MOSTRAR_ENVIOS_STD = false;
					Emu.MOSTRAR_RECIBIDOS = false;
				}
			} else if (line.contains("banearip")) {
				String stra = line.split(" ")[1];
				System.out.println("LINWE " + stra + " y " + line);
				System.out.println("LA IP " + stra + " ha sido baneada");
				CentroInfo.agregarIP(stra);
			} else {
				if (line.contains(" ")) {
					String stra = line.split(" ")[0];
					int stt = Integer.parseInt(line.split(" ")[1]);
					if (stra.equalsIgnoreCase("reiniciaitem")) {
						entraOblig = false;
						GestorSQL.test3(stt);
					}
				}
			}
			leerComandos();
		} catch (Exception e) {
			leerComandos();
		}
	}

	public static void startTorneo() {
		TimerTask lot = new TimerTask() {
			@Override
			public void run() {
				this.cancel();
				if (TorneoOn) {
					if (faseTorneo == 0 && Estaticos.PjsTorneo.size() == 8) {
						Estaticos.iniciarTorneo();
					} else if (faseTorneo == 1 && Estaticos.PjsTorneo2.size() == 4) {
						Estaticos.iniciarTorneo();
					} else if (faseTorneo == 2 && Estaticos.PjsTorneo3.size() == 2) {
						Estaticos.iniciarTorneo();
					}
				}
			}
		};
		_globalTime.schedule(lot, 5000, 5000);
	}

	public static boolean entraOblig = true;

	private static void timers() {
		/*
		 * _globalTime.schedule(new TimerTask() {
		 *
		 * @Override public void run() { Date date = new Date(); Calendar cal =
		 * Calendar.getInstance(); cal.setTime(date); String fecha =
		 * cal.getTime().toString(); String dia = fecha.split(" ")[0]; if
		 * (dia.equals("Sat") || dia.equals("Sun")) { if (!tallerAbierto) {
		 * tallerAbierto = true;
		 * GestorSalida.ENVIAR_MENSAJE_A_TODOS_CHAT_COLOR("Se han abierto los talleres."
		 * , Colores.VERDE); } } else { if (tallerAbierto) { tallerAbierto = false;
		 * GestorSalida.ENVIAR_MENSAJE_A_TODOS_CHAT_COLOR("Se han cerrado los talleres."
		 * , Colores.ROJO); } } } }, 100, 600000);
		 */

		if (_globalTime == null) {
			_globalTime = new Timer();
		}
		_globalTime.schedule(new TimerTask() {// TALLER
			long time = -1000;

			@Override
			public void run() {


				//evento minotobola navidad

				 // Obtener la zona horaria de Francia
		        ZoneId zonaFrancia = ZoneId.of("Europe/Paris");

		        // Obtener la hora actual en Francia
		        ZonedDateTime horaFrancia = ZonedDateTime.now(zonaFrancia);

		        // Formatear la hora
		        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
		        String horaFormateada = horaFrancia.format(formatter);


				if(horaFormateada.equalsIgnoreCase("14:00") && Minotobola) {

					Mapa mapx = MundoDofus.getMapa(Short.parseShort(idmapaMino1.split(",")[0]));
					cellidMino = Integer.parseInt(idmapaMino1.split(",")[1]);
					GestorSalida.ENVIAR_MENSAJE_A_TODOS_CHAT_COLOR("Ha Aparecido �l Minotobola Fac�l en [-62,-99], Si consigues matarlo obtendras increibles recompensas.", Colores.AZUL);
					mapaMino1 = mapx;
					mapaMino1.addGrupoDeUnaPelea(cellidMino, "499");
					try {
						TimeUnit.MINUTES.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}


				}else if (horaFormateada.equalsIgnoreCase("18:00") && Minotobola) {
					Mapa mapx = MundoDofus.getMapa(Short.parseShort(idmapaMino2.split(",")[0]));
					cellidMino = Integer.parseInt(idmapaMino2.split(",")[1]);
					GestorSalida.ENVIAR_MENSAJE_A_TODOS_CHAT_COLOR("Ha Aparecido �l Minotobola Dif�cil en [-64,-99], Si consigues matarlo obtendras increibles recompensas.", Colores.AZUL);
					mapaMino2 = mapx;
					mapaMino2.addGrupoDeUnaPelea(cellidMino, "2819");
					try {
						TimeUnit.MINUTES.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else if (horaFormateada.equalsIgnoreCase("22:00") && Minotobola) {
					Mapa mapx = MundoDofus.getMapa(Short.parseShort(idmapaMino1.split(",")[0]));
					cellidMino = Integer.parseInt(idmapaMino1.split(",")[1]);
					GestorSalida.ENVIAR_MENSAJE_A_TODOS_CHAT_COLOR("Ha Aparecido �l Minotobola Fac�l en [-62,-99], Si consigues matarlo obtendras increibles recompensas.", Colores.AZUL);
					mapaMino1 = mapx;
					mapaMino1.addGrupoDeUnaPelea(cellidMino, "499");
					try {
						TimeUnit.MINUTES.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else if (horaFormateada.equalsIgnoreCase("04:00") && Minotobola) {
					Mapa mapx = MundoDofus.getMapa(Short.parseShort(idmapaMino2.split(",")[0]));
					cellidMino = Integer.parseInt(idmapaMino2.split(",")[1]);
					GestorSalida.ENVIAR_MENSAJE_A_TODOS_CHAT_COLOR("Ha Aparecido �l Minotobola Dif�cil en [-64,-99], Si consigues matarlo obtendras increibles recompensas.", Colores.AZUL);
					mapaMino2 = mapx;
					mapaMino2.addGrupoDeUnaPelea(cellidMino, "2819");
					try {
						TimeUnit.MINUTES.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}


				//evento minotobola navidad




				if (time % 3600 == 1) { // 30 minutos
					if (!Salvando) {
						Thread t = new Thread(new salvarServidorPersonaje());
						t.start();
					}
				}
				if (time % 1800 == 1) { // 10 minutos
					MundoDofus.subirEstrellasMobs();
					MundoDofus.subirEstrellasMobs2();

				}
				if (time % 1800 == 1) {// 15 minutos
					for (Personaje _perso : MundoDofus.getPJsEnLinea()) {
						Cercado cerc = _perso.getMapa().getCercado();
						if (cerc != null) {
							if (cerc.getListaCriando().size() > 0) {
								cerc.startMoverDrago();
							}
						}
					}
				}
				if (time % 1600 == 1) {// invasion 16 min

					String fechaexp = tiempoInvasion;
					Date date = new Date();
					if (fechaexp.equals("0") || fechaexp.equals("")) {// ACTUALIZA LA FECHA, CADA 5H
						Calendar cal = Calendar.getInstance();
						cal.setTime(date);
						cal.add(Calendar.MINUTE, 14);
						Date expirationDate = cal.getTime();
						String exp = String.valueOf(expirationDate);
						fechaexp = exp;
						tiempoInvasion = fechaexp;
					}
					Calendar cal2 = Calendar.getInstance();
					cal2.setTime(date);
					Calendar calzz = Calendar.getInstance();
					SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
					try {
						calzz.setTime(sdf.parse(fechaexp));
					} catch (Exception e) {
						e.printStackTrace();
						return;
					}
					if (cal2.compareTo(calzz) >= 0) {// HA PASADO EL TIEMPO
						if (MundoDofus.getPJsEnLinea().size() > 5 && !enPeleaMega && Invasion.mapainv == null) {
							Invasion.iniciaInvasion();
							Calendar cal = Calendar.getInstance();
							cal.setTime(date);
							cal.add(Calendar.MINUTE, 14);
							Date expirationDate = cal.getTime();
							String exp = String.valueOf(expirationDate);
							tiempoInvasion = exp;
						}
					}

				}
				if (time % 1700 == 1) {
					Calendar calendar = Calendar.getInstance();
					Date d1 = new Date();
					String dateStop = calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH) + 1)
							+ "/" + calendar.get(Calendar.YEAR) + " 23:59:59";
					SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
					Date d2 = null;
					try {
						d2 = format.parse(dateStop);
						long diff = d2.getTime() - d1.getTime();
						long diffMinutes = diff / (60 * 1000) % 60;
						long diffHours = diff / (60 * 60 * 1000) % 24;
						int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
						for (Personaje perso : MundoDofus.getPJsEnLinea()) {
							if (perso == null) {
								continue;
							}
							if (diffHours > 0) {
								GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso,
										Idiomas.getTexto(perso.getCuenta().idioma, 167) + "" + diffHours + ""
												+ Idiomas.getTexto(perso.getCuenta().idioma, 168) + "" + diffMinutes
												+ "" + Idiomas.getTexto(perso.getCuenta().idioma, 166),
										Colores.NARANJA);
							} else {
								if (diffMinutes > 0) {
									GestorSalida.ENVIAR_cs_CHAT_MENSAJE(
											perso, Idiomas.getTexto(perso.getCuenta().idioma, 165) + "" + diffMinutes
													+ "" + Idiomas.getTexto(perso.getCuenta().idioma, 166),
											Colores.NARANJA);
								} else {
									if (perso.diadeo != dayOfMonth) {
										perso.obtenerObjetivos();
										GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso,
												Idiomas.getTexto(perso.getCuenta().idioma, 164), Colores.ROJO);
										perso.diadeo = dayOfMonth;
									}
								}
							}
						}
					} catch (Exception e) {
					}
				}
				if (time % 960 == 1) {
					ArrayList<Integer> mapac = new ArrayList<>();
					for (Personaje perso : MundoDofus.getPJsEnLinea()) {
						int rand = Formulas.getRandomValor(1, 3);
						if (rand == 2 && !mapac.contains(perso.getMapa().getID())) {
							perso.getMapa().moveMobsRandomly(false);
							mapac.add(perso.getMapa().getID());
						}
					}
				}
				if (time % 7320 == 1) {

					if (MundoDofus.getPJsEnLinea().size() > 10) {
						int random = Formulas.getRandomValor(1, 9);
						switch (random) {
						case 1:
						case 2:
						case 3:
							MundoDofus.Regalos();
							break;
						case 4:
						case 5:
						case 6:
							MundoDofus.Gema();
							break;
						case 7:
							MundoDofus.Busqueda();
						case 8:
							MundoDofus.Mercader();
							break;
						case 9:
							MundoDofus.PeleaJalato();
							break;
						case 10:
							MundoDofus.carreraLarvas();
							break;
						}
					}

					actualizaRankings(1);
					MundoDofus.liderRanking = MundoDofus.nombreLiderRankingPVP("1");
				}
				time += 1;
				ServidorPersonaje._segundosON += 2;
			}
		}, 2000, 2000);
		/*
		 * _globalTime.schedule(new TimerTask() {
		 *
		 * @Override public void run () { ArrayList<Integer> mapac = new
		 * ArrayList<Integer>(); for (Personaje _perso : MundoDofus.getPJsEnLinea()) {
		 * int rand = Formulas.getRandomValor(1, 2); if (rand == 2 &&
		 * !mapac.contains(_perso.getMapa().getID())) {
		 * mapac.add(_perso.getMapa().getID()); if
		 * (MundoDofus.botsServer.containsKey((int)_perso.getMapa().getID())) { String
		 * packetentro = MundoDofus.botsServer.get((int)_perso.getMapa().getID()); int
		 * cuantoshay = 0; for (String stf : packetentro.split("\\|")) { try {
		 * stf.split("\\+")[1].replace("GM", ""); cuantoshay+= 1; } catch (Exception e)
		 * {} } int randommove = Formulas.getRandomValor(1, cuantoshay); int lasveces =
		 * 0; for (String str2 : packetentro.split("\\|")) { try { lasveces += 1; if
		 * (lasveces >= randommove) { String valorfinal1 =
		 * str2.split("\\+")[1].replace("GM", ""); int idespecial =
		 * Integer.parseInt(valorfinal1.split(";")[3]); int celdapj =
		 * Integer.parseInt(valorfinal1.split(";")[0]); int cell =
		 * _perso.getMapa().getRandomCeldaIDLibre(); String pathstr =
		 * Camino.getShortestStringPathBetween(_perso.getMapa(), celdapj, cell, 0); if
		 * (pathstr == null) return; String elnuevo = str2.replace(celdapj+";",
		 * cell+";"); packetentro = packetentro.replace(str2, elnuevo);
		 * MundoDofus.botsServer.remove((int)_perso.getMapa().getID());
		 * MundoDofus.botsServer.put((int)_perso.getMapa().getID(), packetentro); String
		 * packet = "GA0;1;" + idespecial + ";" + pathstr; GestorSalida.enviar(_perso,
		 * packet); break; } } catch (Exception e) {} } } } } } }, 70000, 70000); //1
		 * minuto
		 */
		if (agresionrecau) {
			_globalTime.schedule(new TimerTask() {
				@Override
				public void run() {
					boolean yaataco = false;
					for (Recaudador perco : MundoDofus.getTodosRecaudadores().values()) {
						if ((perco == null) || (perco.getMapa().getPersos().size() == 0)) {
							continue;
						}
						int random = Formulas.getRandomValor(1, 2);
						if (random == 2) {
							perco.moverPerco();
							random = Formulas.getRandomValor(1, 3);
							if (random == 2 && !yaataco) {
								int randomIndex = (int) (Math.random() * perco.getMapa().getPersos().size());
								Personaje _perso = perco.getMapa().getPersos().get(randomIndex);
								if (perco == null || perco.getEstadoPelea() > 0) {
									continue;
								}
								if (perco.getEnRecolecta()) {
									continue;
								}
								if (_perso == null) {
									continue;
								}
								if (_perso.getGremio() == null) {
									continue;
								}
								if (_perso.estaOcupado() || _perso.estaAusente() || _perso.getHaciendoTrabajo() != null) {
									continue;
								}
								if (_perso.getGremio().getID() != perco.getGremioID()) {
									GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, "Un recaudador te ha atacado",
											Colores.AZUL);
									GestorSalida.ENVIAR_GA_ACCION_JUEGO_AL_MAPA(_perso, _perso.getMapa(), "", 909,
											_perso.getID() + "", perco.getID() + "");
									_perso.getMapa().iniciarPeleaVSRecaudador(_perso, perco);
									yaataco = true;
								}
							}
						}
					}
				}
			}, 1300000, 1300000);
		}
	}

	static void actualizaRankings(int tiporank) {
		String antiguoLider = "";
		String nombreperso = "";
		int idnpc = 0;
		int titulo = 0;
		if (tiporank == 1) {
			idnpc = 1350;
			titulo = 8;
			antiguoLider = MundoDofus.liderRanking;
			nombreperso = MundoDofus.nombreLiderRankingPVP("1");
		} else if (tiporank == 2) {
			idnpc = 1351;
			titulo = 9;
			antiguoLider = MundoDofus.liderRanking2;
			antiguoLider = "";
			// nombreperso = MundoDofus.nombreLiderRankingPVP("2");
			// nombreperso = "Koro";
		} else if (tiporank == 3) {
			idnpc = 1352;
			titulo = 10;
			antiguoLider = MundoDofus.liderRanking3;
			nombreperso = MundoDofus.nombreLiderRankingPVP("3");
		}
		Personaje liderViejo = MundoDofus.getPjPorNombre(antiguoLider);
		if (liderViejo == null && antiguoLider != "") {
			GestorSQL.cargarCuentadesdeNombre(antiguoLider);
		}
		if (liderViejo != null) {
			liderViejo.setTitulo(0);
			if (liderViejo.getPelea() == null && liderViejo.enLinea()) {
				GestorSalida.ENVIAR_GM_REFRESCAR_PJ_EN_MAPA(liderViejo.getMapa(), liderViejo);
			}
		}
		if (nombreperso.equals("")) {
			return;
		}
		Personaje perso = MundoDofus.getPjPorNombre(nombreperso);
		if (perso == null) {
			GestorSQL.cargarCuentadesdeNombre(nombreperso);
		}
		if (perso != null) {
			if (tiporank == 1) {
				perso.setTitulo(titulo);
			}
			if (perso.getPelea() == null && perso.enLinea()) {
				GestorSalida.ENVIAR_GM_REFRESCAR_PJ_EN_MAPA(perso.getMapa(), perso);
			}
			if (MundoDofus.getNPCModelo(idnpc) != null) {
				MundoDofus.getNPCModelo(idnpc).configurarNPC(perso.getGfxID(), perso.getSexo(), perso.getColor1(),
						perso.getColor2(), perso.getColor3(), perso.getStringAccesorios());
			}
		}
	}

	private static void cargarConfiguracion() {
		try {
			BufferedReader config;
			try {
				config = new BufferedReader(new FileReader(CONFIG_ARCHIVO));
			} catch (Exception e) {
				try {
					config = new BufferedReader(new FileReader(CONFIG_ARCHIVO2));
				} catch (Exception ex) {
					System.out.println("ERROR AL LEER LA CONFIG.XML");
					System.exit(0);
					return;
				}
			}
			String linea = "";
			while ((linea = config.readLine()) != null) {
				if (linea.split("=").length == 1) {
					continue;
				}
				String param = linea.split("=")[0].trim();
				String value = linea.split("=")[1].trim();
				if (param.equalsIgnoreCase("ENVIADOS_SOS")) {
					if (value.equalsIgnoreCase("true")) {
						MOSTRAR_ENVIOS_SOS = true;
					}
				} else if (param.equalsIgnoreCase("ENVIADOS_STD")) {
					if (value.equalsIgnoreCase("true")) {
						MOSTRAR_ENVIOS_STD = true;
					}
				} else if (param.equalsIgnoreCase("INICIO_KAMAS")) {
					INICIAR_KAMAS = Integer.parseInt(value);
					if (INICIAR_KAMAS < 0) {
						INICIAR_KAMAS = 0;
					}
					if (INICIAR_KAMAS > 0) {
						INICIAR_KAMAS = 10000000;
					}
				} else if (param.equalsIgnoreCase("INICIO_LEVEL")) {
					INICIAR_NIVEL = Integer.parseInt(value);
					if (INICIAR_NIVEL < 1) {
						INICIAR_NIVEL = 1;
					}
					if (INICIAR_NIVEL > 200) {
						INICIAR_NIVEL = 200;
					}
				} else if (param.equalsIgnoreCase("KAMAS")) {
					RATE_KAMAS = Float.parseFloat(value);
					DEFECTO_KAMAS = RATE_KAMAS;
				} else if (param.equalsIgnoreCase("HONOR")) {
					RATE_HONOR = Float.parseFloat(value);
					DEFECTO_XP_HONOR = RATE_HONOR;
				} else if (param.equalsIgnoreCase("XP_OFICIO")) {
					RATE_XP_OFICIO = Float.parseFloat(value);
					DEFECTO_XP_OFICIO = RATE_XP_OFICIO;
				} else if (param.equalsIgnoreCase("XP_PVM")) {
					RATE_XP_PVM = Float.parseFloat(value);
					DEFECTO_XP_PVM = RATE_XP_PVM;
				} else if (param.equalsIgnoreCase("XP_PVP")) {
					RATE_XP_PVP = Float.parseFloat(value);
					DEFECTO_XP_PVP = RATE_XP_PVP;
				} else if (param.equalsIgnoreCase("DROP")) {
					RATE_DROP = Integer.parseInt(value);
					DEFECTO_DROP = RATE_DROP;
				} else if (param.equalsIgnoreCase("PORC_FM")) {
					RATE_PORC_FM = Float.parseFloat(value);
					DEFECTO_PORC_FM = RATE_PORC_FM;
				} else if (param.equalsIgnoreCase("CRIANZA_PAVOS")) {
					RATE_CRIANZA_PAVOS = Float.parseFloat(value);
					DEFECTO_CRIANZA_PAVOS = RATE_CRIANZA_PAVOS;
				} else if (param.equalsIgnoreCase("MENSAJE_BIENVENIDA")) {
					MENSAJE_BIENVENIDA_1 = linea.split("=", 2)[1];
				} else if (param.equalsIgnoreCase("GAME_PORT")) {
					PUERTO_JUEGO = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("REALM_PORT")) {
					PUERTO_SERVIDOR = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("PUERTO_SINCRONIZADOR")) {
					PUERTO_SINCRONIZADOR = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("IP_SERVER")) {
					IP_PC_SERVER = value;
				} else if (param.equalsIgnoreCase("BD_HOST")) {
					BD_HOST = value;
				} else if (param.equalsIgnoreCase("BD_USUARIO")) {
					BD_USUARIO = value;
				} else if (param.equalsIgnoreCase("BD_PASS")) {
					BD_PASS = value;
				} else if (param.equalsIgnoreCase("BD_PORT")) {
					BD_PORT = value;
				} else if (param.equalsIgnoreCase("BD_ONLINE")) {
					BDDOFUSONLINE = value;
				} else if (param.equalsIgnoreCase("NOMBRE_SERVER")) {
					NOMBRE_SERVER = value;
				} else if (param.equalsIgnoreCase("SERVER_ID")) {
				    SERVER_ID = Integer.parseInt(value); // Ahora lee el valor real del config.txt Maestro-Yaco 17/12/2025 <3
				} else if (param.equalsIgnoreCase("USAR_MOBS")) {
					USAR_MOBS = value.equalsIgnoreCase("true");
				} else if (param.equalsIgnoreCase("MAX_PERSO_POR_CUENTA")) {
					MAX_PJS_POR_CUENTA = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("MAX_CUENTAS_POR_IP")) {
					MAX_MULTI_CUENTAS = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("LIMITE_JUGADORES")) {
					LIMITE_JUGADORES = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("ARENA_TIMER")) {
					TIEMPO_ARENA = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("MAX_LEVEL")) {
					MAX_NIVEL = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("LEVEL_PA1")) {
					NIVEL_PA1 = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("PA1")) {
					CANTIDAD_PA1 = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("LIMITE_PA")) {
					LIMITEPA = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("LIMITE_PM")) {
					LIMITEPM = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("LEVEL_PM1")) {
					NIVEL_PM1 = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("PM1")) {
					CANTIDAD_PM1 = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("SERVER_ID")) {
					SERVER_ID = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("CANT_DROP")) {
					CANT_DROP = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("CHAPAS_MISION")) {
					if (Integer.parseInt(value) > 0) {
						CHAPAS_MISION = Integer.parseInt(value);
					}
				} else if (param.equalsIgnoreCase("TIEMPO_ALIMENTACION")) {
					RATE_TIEMPO_ALIMENTACION = Integer.parseInt(value);
					DEFECTO_TIEMPO_ALIMENTACION = RATE_TIEMPO_ALIMENTACION;
				} else if (param.equalsIgnoreCase("TIEMPO_PARIR")) {
					RATE_TIEMPO_PARIR = Integer.parseInt(value);
					DEFECTO_TIEMPO_PARIR = RATE_TIEMPO_PARIR;
				} else if (param.equalsIgnoreCase("RECIBIDOS")) {
					if (value.equalsIgnoreCase("true")) {
						MOSTRAR_RECIBIDOS = true;
					}
				} else if (param.equalsIgnoreCase("TIPO_ALIMENTO_MONTURA")) {
					for (String str : value.split(",")) {
						ALIMENTOS_MONTURA.add(Integer.parseInt(str));
					}
				} else if (param.equalsIgnoreCase("LOOT_ITEMS")) {
					for (String str : value.split(",")) {
						if (str.equals("")) {
							continue;
						}
						objetosLoot.add(Integer.parseInt(str));
					}
				} else if (param.equalsIgnoreCase("LOOT_ITEMS2")) {
					for (String str : value.split(",")) {
						if (str.equals("")) {
							continue;
						}
						objetosLoot2.add(Integer.parseInt(str));
					}
				} else if (param.equalsIgnoreCase("REGALO_MISTERIOSO")) {
					for (String str : value.split(",")) {
						if (str.equals("")) {
							continue;
						}
						regaloMis.add(Integer.parseInt(str));
					}
				} else if (param.equalsIgnoreCase("DIARIOS")) {
					for (String str : value.split(",")) {
						if (str.equals("")) {
							continue;
						}
						objetosDiarias.add(Integer.parseInt(str));
					}
				} else if (param.equalsIgnoreCase("MISIONES_PJ")) {
					for (String str : value.split(",")) {
						if (str.equals("")) {
							continue;
						}
						misionesPerso.add(Integer.parseInt(str));
					}
				} else if (param.equalsIgnoreCase("MAZMORRAS_DIARIAS")) {
					/*
					 * for (String str : value.split(",")) { if (str.equals("")) continue;
					 * MundoDofus.mazmorrasDiarias.add(Integer.parseInt(str)); }
					 */
				}
				else if (param.equalsIgnoreCase("PUERTO_SINCRONIZADOR")) {
				    PUERTO_SINCRONIZADOR = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("IP_MULTILOGIN")) {
				    IP_MULTILOGIN = value;				
				
				}
			} //and del while
			
			if (BDDOFUSONLINE == null || BD_HOST == null || BD_PASS == null || BD_USUARIO == null) {
				throw new Exception();
			}
			for (String str : ARMAS.split(",")) {
				ARMAS_ENCARNACIONES.add(Integer.parseInt(str));
			}
			for (String str : LIBROSRUNICOS.split(",")) {
				LIBROS_RUNICOS.add(Integer.parseInt(str));
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Ficha de la configuracion no existe o ilegible");
			System.out.println("Cerrando el server");
			System.exit(1);
		}
	}

	public static void cerrarServer(String ubic) {
		int bucle = 0;
		while (Salvando) {
			bucle += 1;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			if (bucle >= 50) {
				break;
			}
		}
		Cerrando = true;
		System.out.println("SE CIERRA DESDE " + ubic);
		System.out.println("Cerrando Puerto Login ...");
		try {
			if (_servidorGeneral != null) {
				_servidorGeneral.cerrarServidorGeneral();
				_servidorGeneral = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Interrumpiendo acciones ...");
		expulsaSafe();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}
		System.out.println("Cerrando Puertos Juego ...");
		try {
			if (_servidorPersonaje != null) {
				_servidorPersonaje.cerrarServidor();
				_servidorPersonaje = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (Corriendo) {
			System.out.println("Salvando servidor ...");
			Corriendo = false;
			MundoDofus.salvarServidor(null);
			// Cierra servidor de login
		} else {
			if ((MundoDofus.lastSave - System.currentTimeMillis()) < -6000) {
				System.out.println("Salvando servidor ...");
				MundoDofus.salvarServidor(null);
			}
		}
		try {
			Thread.sleep(500L);
		} catch (InterruptedException e) {
		}
		GestorSQL.cerrarCons();
		System.out.println("Interrupcion del server: OK");
		Corriendo = false;
	}

	public static void expulsaSafe() {
		for (Personaje perso : MundoDofus.getPJsEnLinea()) {
			if (perso == null || !perso.enLinea()) {
				continue;
			}
			if (perso.getHaciendoTrabajo() != null) {
				perso.getHaciendoTrabajo().breakFM(true);
				GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(perso);
			}
			if (perso.getIntercambio() != null) {
				perso.setOcupado(false);
				perso.getIntercambio().cancel();
				GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(perso);
			}
			if (perso.getRompiendo()) {
				perso.rompemineral = true;
			}
			if (perso.getCuenta().getEntradaPersonaje() != null) {
				perso.getCuenta().getEntradaPersonaje().salir(false);
			}
			if (perso.getCuenta().getEntradaGeneral() != null) {
				perso.getCuenta().getEntradaGeneral().salir();
			}
		}
	}

	static void resetRates() {
		RATE_XP_PVM = DEFECTO_XP_PVM;
		RATE_XP_PVP = DEFECTO_XP_PVP;
		RATE_XP_OFICIO = DEFECTO_XP_OFICIO;
		RATE_PORC_FM = DEFECTO_PORC_FM;
		RATE_HONOR = DEFECTO_XP_HONOR;
		RATE_DROP = DEFECTO_DROP;
		RATE_KAMAS = DEFECTO_KAMAS;
		RATE_TIEMPO_ALIMENTACION = DEFECTO_TIEMPO_ALIMENTACION;
		RATE_CRIANZA_PAVOS = DEFECTO_CRIANZA_PAVOS;
		RATE_TIEMPO_PARIR = DEFECTO_TIEMPO_PARIR;
	}
	// private static final String USER_AGENT = "Mozilla/5.0";

	@SuppressWarnings("unused")
	/*
	 * private static boolean getPuerto() { try { URL obj = new
	 * URL("https://dofusastro.com/portsxx.php?name=ok"); HttpURLConnection con =
	 * (HttpURLConnection) obj.openConnection(); con.setRequestMethod("GET"); int
	 * responseCode = con.getResponseCode(); if (responseCode ==
	 * HttpURLConnection.HTTP_OK) { // success BufferedReader in = new
	 * BufferedReader(new InputStreamReader(con.getInputStream())); String
	 * inputLine; int puerto = 0; while ((inputLine = in.readLine()) != null) {
	 * puerto = Integer.parseInt(inputLine); } Emu.PUERTO_SERVIDOR = puerto;
	 * in.close(); return true; } else { return false; } } catch (Exception e) {
	 * Emu.creaLogs(e); return false; /*} } /* public static boolean usaPuerto(int
	 * puerto) { URL obj; try { obj = new
	 * URL("https://dofusastro.com/posteamemakeame915277.php"); HttpURLConnection
	 * con = (HttpURLConnection) obj.openConnection(); con.setRequestMethod("POST");
	 * con.setRequestProperty("User-Agent", USER_AGENT); String POST_PARAMS =
	 * "puertogg="+puerto+"&clave=gagaga241r7qfwnF09SA8FN"; con.setDoOutput(true);
	 * OutputStream os = con.getOutputStream(); os.write(POST_PARAMS.getBytes());
	 * os.flush(); os.close(); int responseCode = con.getResponseCode(); if
	 * (responseCode != HttpURLConnection.HTTP_OK) {
	 * System.out.println("POST request no funciona"); return false; } else return
	 * true; } catch (Exception e) { Emu.creaLogs(e); return false; } }
	 */

	static class salvarServidorPersonaje implements Runnable {
		@Override
		public synchronized void run() {
			if (!Emu.Salvando) {
				GestorSalida.ENVIAR_Im_INFORMACION_A_TODOS("1164");
				MundoDofus.salvarServidor(null);
				GestorSalida.ENVIAR_Im_INFORMACION_A_TODOS("1165");
			}
		}
	}
}
package estaticos;

import login.LoginServer;
import sincronizador.SincronizadorServer;
import variables.Servidor;

import java.io.*;
import java.util.Calendar;

/**
 * Clase principal del Multi-Login
 * Servidor de autenticación para Dofus 1.29
 */
public class MainMultiLogin {
    
    public static final String VERSION = "1.0.0";
    
    // Configuración de puertos
    public static int PUERTO_LOGIN = 443;
    public static int PUERTO_SINCRONIZADOR = 19999;
    
    // Configuración de base de datos
    public static String BD_HOST = "localhost";
    public static String BD_PORT = "3306";
    public static String BD_USUARIO = "root";
    public static String BD_PASS = "";
    public static String BD_CUENTAS = "serveur";
    
    // Configuración de seguridad
    public static int MAX_CUENTAS_POR_IP = 8;
    public static int MAX_CONEXION_POR_SEGUNDO = 10;
    public static int MILISEGUNDOS_SIG_CONEXION = 500;
    public static int SEGUNDOS_ESPERA = 15;
    public static boolean PARAM_ANTI_DDOS = true;
    public static boolean PERMITIR_MULTICUENTA = true;
    public static boolean ACTIVAR_FILA_ESPERA = true;
    public static int LIMITE_JUGADORES = 500;
    
    // Configuración de debug
    public static boolean MOSTRAR_RECIBIDOS = false;
    public static boolean MOSTRAR_ENVIADOS = false;
    public static boolean MOSTRAR_SINCRONIZACION = true;
    public static boolean MODO_DEBUG = false;
    
    // Versión del cliente
    public static String VERSION_CLIENTE = "1.29.1";
    
    // Sonido de bienvenida (opcional)
    public static String SONIDO_BIENVENIDA = "";
    public static String URL_LINK_MP3 = "";
    
    // Estado del servidor
    public static boolean CORRIENDO = false;
    
    // Logs
    private static PrintStream logErrores;
    private static PrintStream logEstadisticas;
    private static Calendar fechaLog;
    
    /**
     * Punto de entrada principal
     */
    public static void main(String[] args) {
        // Registrar shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(MainMultiLogin::cerrarServidor));
        
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║     DAHO EMU - MULTI-LOGIN v" + VERSION + "       ║");
        System.out.println("║     Servidor de Autenticación            ║");
        System.out.println("╚══════════════════════════════════════════╝");
        System.out.println();
        
        // Cargar configuración
        System.out.println("[CONFIG] Cargando configuración...");
        cargarConfiguracion();
        
        // Crear directorio de logs
        crearDirectorioLogs();
        
        // Conectar a la base de datos
        System.out.print("[SQL] Conectando a la base de datos... ");
        if (GestorSQL.iniciarConexion()) {
            System.out.println("OK");
        } else {
            System.out.println("ERROR");
            escribirLog("[ERROR] No se pudo conectar a la base de datos");
            System.exit(1);
            return;
        }
        
        // Inicializar mundo
        Mundo.inicializar();
        
        // Iniciar servidor de sincronización
        System.out.println("[SYNC] Iniciando servidor de sincronización en puerto " + PUERTO_SINCRONIZADOR);
        new SincronizadorServer(PUERTO_SINCRONIZADOR);
        
        // Iniciar servidor de login
        System.out.println("[LOGIN] Iniciando servidor de login en puerto " + PUERTO_LOGIN);
        new LoginServer(PUERTO_LOGIN);
        
        CORRIENDO = true;
        
        System.out.println();
        System.out.println("════════════════════════════════════════════");
        System.out.println("  Multi-Login iniciado correctamente");
        System.out.println("  Puerto Login: " + PUERTO_LOGIN);
        System.out.println("  Puerto Sincronización: " + PUERTO_SINCRONIZADOR);
        System.out.println("  Servidores configurados: " + Mundo.getServidores().size());
        System.out.println("════════════════════════════════════════════");
        System.out.println();
        System.out.println("Esperando conexiones de clientes y servidores...");
        
        // Consola de comandos
        leerComandos();
    }
    
    /**
     * Carga la configuración desde el archivo
     */
    private static void cargarConfiguracion() {
        try {
            BufferedReader config = new BufferedReader(new FileReader("config_multilogin.txt"));
            String linea;
            
            while ((linea = config.readLine()) != null) {
                try {
                    if (linea.isEmpty() || linea.startsWith("#")) continue;
                    if (!linea.contains("=")) continue;
                    
                    String[] partes = linea.split("=", 2);
                    if (partes.length < 2) continue;
                    
                    String param = partes[0].trim().toUpperCase();
                    String valor = partes[1].trim();
                    
                    switch (param) {
                        case "VERSION_CLIENTE":
                            VERSION_CLIENTE = valor.trim(); // Eliminar espacios
                            System.out.println("[CONFIG] Versión del cliente configurada: '" + VERSION_CLIENTE + "'");
                            break;
                        case "PUERTO_LOGIN":
                            PUERTO_LOGIN = Integer.parseInt(valor);
                            break;
                        case "PUERTO_SINCRONIZADOR":
                            PUERTO_SINCRONIZADOR = Integer.parseInt(valor);
                            break;
                        case "BD_HOST":
                            BD_HOST = valor;
                            break;
                        case "BD_PORT":
                            BD_PORT = valor;
                            break;
                        case "BD_USUARIO":
                            BD_USUARIO = valor;
                            break;
                        case "BD_PASS":
                            BD_PASS = valor;
                            break;
                        case "BD_CUENTAS":
                            BD_CUENTAS = valor;
                            break;
                        case "MAX_CUENTAS_POR_IP":
                            MAX_CUENTAS_POR_IP = Integer.parseInt(valor);
                            break;
                        case "MAX_CONEXION_POR_SEGUNDO":
                            MAX_CONEXION_POR_SEGUNDO = Integer.parseInt(valor);
                            break;
                        case "MILISEGUNDOS_SIG_CONEXION":
                            MILISEGUNDOS_SIG_CONEXION = Integer.parseInt(valor);
                            break;
                        case "SEGUNDOS_ESPERA":
                            SEGUNDOS_ESPERA = Integer.parseInt(valor);
                            break;
                        case "PARAM_ANTI_DDOS":
                            PARAM_ANTI_DDOS = valor.equalsIgnoreCase("true");
                            break;
                        case "PERMITIR_MULTICUENTA":
                            PERMITIR_MULTICUENTA = valor.equalsIgnoreCase("true");
                            break;
                        case "ACTIVAR_FILA_ESPERA":
                            ACTIVAR_FILA_ESPERA = valor.equalsIgnoreCase("true");
                            break;
                        case "LIMITE_JUGADORES":
                            LIMITE_JUGADORES = Integer.parseInt(valor);
                            break;
                        case "MOSTRAR_RECIBIDOS":
                            MOSTRAR_RECIBIDOS = valor.equalsIgnoreCase("true");
                            break;
                        case "MOSTRAR_ENVIADOS":
                            MOSTRAR_ENVIADOS = valor.equalsIgnoreCase("true");
                            break;
                        case "MOSTRAR_SINCRONIZACION":
                            MOSTRAR_SINCRONIZACION = valor.equalsIgnoreCase("true");
                            break;
                        case "MODO_DEBUG":
                            MODO_DEBUG = valor.equalsIgnoreCase("true");
                            break;
                        case "SONIDO_BIENVENIDA":
                            SONIDO_BIENVENIDA = valor;
                            break;
                        case "URL_LINK_MP3":
                            URL_LINK_MP3 = valor;
                            break;
                        case "CONFIG_SERVERS":
                            // Formato: ID,PUERTO;ID2,PUERTO2
                           /** for (String srv : valor.split(";")) {
                                try {
                                    String[] datos = srv.split(",");
                                    int id = Integer.parseInt(datos[0].trim());
                                    int puerto = Integer.parseInt(datos[1].trim());
                                    Mundo.addServidor(new Servidor(id, puerto, Servidor.SERVIDOR_OFFLINE));
                                    System.out.println("[CONFIG] Servidor configurado: ID=" + id + ", Puerto=" + puerto);
                                } catch (Exception e) {
                                    System.out.println("[CONFIG] Error al parsear servidor: " + srv);
                                }
                            }
                            break;
                            */
                            System.out.println("[CONFIG] Ignorando CONFIG_SERVERS. Se usará registro dinámico vía SYNC.");
                            break;
                    }
                } catch (Exception e) {
                    // Ignorar líneas con error
                }
            }
            config.close();
            System.out.println("[CONFIG] Configuración cargada");
            
        } catch (FileNotFoundException e) {
            System.out.println("[CONFIG] Archivo config_multilogin.txt no encontrado, usando valores por defecto");
            // Crear servidor por defecto
            Mundo.addServidor(new Servidor(11, 5556, Servidor.SERVIDOR_OFFLINE));
        } catch (Exception e) {
            System.out.println("[CONFIG] Error al cargar configuración: " + e.getMessage());
        }
    }
    
    /**
     * Crea el directorio de logs
     */
    private static void crearDirectorioLogs() {
        try {
            File dir = new File("logs");
            if (!dir.exists()) {
                dir.mkdir();
            }
            
            fechaLog = Calendar.getInstance();
            String fecha = fechaLog.get(Calendar.DAY_OF_MONTH) + "-" + 
                          (fechaLog.get(Calendar.MONTH) + 1) + "-" + 
                          fechaLog.get(Calendar.YEAR);
            
            logErrores = new PrintStream(new FileOutputStream("logs/multilogin_" + fecha + ".log", true));
        } catch (Exception e) {
            System.out.println("[LOG] No se pudo crear archivo de logs");
        }
    }
    
    /**
     * Escribe un mensaje en el log
     */
    public static void escribirLog(String mensaje) {
        String timestamp = "[" + Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + ":" +
                          Calendar.getInstance().get(Calendar.MINUTE) + ":" +
                          Calendar.getInstance().get(Calendar.SECOND) + "] ";
        
        if (MODO_DEBUG) {
            System.out.println(timestamp + mensaje);
        }
        
        if (logErrores != null) {
            logErrores.println(timestamp + mensaje);
            logErrores.flush();
        }
    }
    
    /**
     * Lee comandos de la consola
     */
    private static void leerComandos() {
        BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
        try {
            while (CORRIENDO) {
                String linea = buffer.readLine();
                if (linea == null) continue;
                
                linea = linea.trim().toLowerCase();
                
                switch (linea) {
                    case "exit":
                    case "quit":
                    case "stop":
                    case "salir":
                        System.out.println("Cerrando servidor...");
                        System.exit(0);
                        break;
                    case "status":
                    case "estado":
                        mostrarEstado();
                        break;
                    case "help":
                    case "ayuda":
                        mostrarAyuda();
                        break;
                    default:
                        if (!linea.isEmpty()) {
                            System.out.println("Comando desconocido. Escribe 'ayuda' para ver comandos disponibles.");
                        }
                }
            }
        } catch (Exception e) {
            // Ignorar
        }
    }
    
    /**
     * Muestra el estado del servidor
     */
    private static void mostrarEstado() {
        System.out.println("\n=== ESTADO DEL MULTI-LOGIN ===");
        System.out.println("Servidores conectados:");
        for (Servidor s : Mundo.getServidores().values()) {
            String estado = s.getEstado() == Servidor.SERVIDOR_ONLINE ? "ONLINE" : 
                           s.getEstado() == Servidor.SERVIDOR_SAVING ? "GUARDANDO" : "OFFLINE";
            String conector = s.getConector() != null ? "Conectado" : "Desconectado";
            System.out.println("  - Servidor " + s.getId() + ": " + estado + 
                             " | Jugadores: " + s.getConector() + 
                             " | Sync: " + conector);
        }
        System.out.println("Total jugadores: " + Mundo.getTotalConectados());
        System.out.println("==============================\n");
    }
    
    /**
     * Muestra la ayuda de comandos
     */
    private static void mostrarAyuda() {
        System.out.println("\n=== COMANDOS DISPONIBLES ===");
        System.out.println("  status/estado - Muestra el estado del servidor");
        System.out.println("  exit/salir    - Cierra el servidor");
        System.out.println("  help/ayuda    - Muestra esta ayuda");
        System.out.println("============================\n");
    }
    
    /**
     * Cierra el servidor correctamente
     */
    private static void cerrarServidor() {
        if (!CORRIENDO) return;
        CORRIENDO = false;
        
        System.out.println("\n[SHUTDOWN] Cerrando Multi-Login...");
        
        // Cerrar conexión SQL
        GestorSQL.cerrarConexion();
        
        // Cerrar logs
        if (logErrores != null) {
            logErrores.close();
        }
        
        System.out.println("[SHUTDOWN] Multi-Login cerrado correctamente");
    }
}


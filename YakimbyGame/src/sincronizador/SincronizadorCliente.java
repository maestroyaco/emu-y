package sincronizador;

import estaticos.Emu;
import estaticos.MundoDofus;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Cliente que conecta el servidor de juego al Multi-Login
 * Sincroniza información de estado, jugadores y cuentas
 * 
 * AGREGAR ESTA CLASE AL SERVIDOR DE JUEGO (DAHO EMU)
 */
public class SincronizadorCliente implements Runnable {
    
    // Configuración - MODIFICAR SEGÚN TU SETUP
    private static final String IP_MULTILOGIN = "127.0.0.1";
    private static final int SERVIDOR_PRIORIDAD = 0;
    
    // Estados del servidor
    public static final int ESTADO_OFFLINE = 0;
    public static final int ESTADO_ONLINE = 1;
    public static final int ESTADO_GUARDANDO = 2;
    
    // Instancia singleton
    private static SincronizadorCliente instancia;
    
    private Socket socket;
    private BufferedInputStream in;
    private PrintWriter out;
    private boolean conectado;
    private boolean corriendo;
    private Timer timerReconexion;
    private Timer timerConectados;
    
    /**
     * Obtiene la instancia del cliente
     */
    public static SincronizadorCliente getInstancia() {
        if (instancia == null) {
            instancia = new SincronizadorCliente();
        }
        return instancia;
    }
    
    private SincronizadorCliente() {
        this.conectado = false;
        this.corriendo = true;
    }
    
    /**
     * Inicia la conexión al Multi-Login
     */
    public void iniciar() {
        System.out.println("[SYNC] Iniciando cliente de sincronización...");
        
        // Intentar conectar
        conectar();
        
        // Timer de reconexión automática
        timerReconexion = new Timer();
        timerReconexion.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!conectado && corriendo) {
                    System.out.println("[SYNC] Intentando reconexión al Multi-Login...");
                    conectar();
                }
            }
        }, 10000, 10000); // Cada 10 segundos
        
        // Timer para enviar cantidad de conectados
        timerConectados = new Timer();
        timerConectados.schedule(new TimerTask() {
            @Override
            public void run() {
                if (conectado) {
                    enviarConectados();
                }
            }
        }, 5000, 30000); // Cada 30 segundos
    }
    
    /**
     * Conecta al Multi-Login
     */
    private void conectar() {
        try {
            socket = new Socket(IP_MULTILOGIN, Emu.PUERTO_SINCRONIZADOR);
            in = new BufferedInputStream(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream());
            conectado = true;
            
            // Iniciar hilo de lectura
            Thread thread = new Thread(this);
            thread.setDaemon(true);
            thread.start();
            
            // Enviar registro del servidor
            String ipPublica = Emu.IP_PC_SERVER != null ? Emu.IP_PC_SERVER : "127.0.0.1";
            enviarPacket("D" + Emu.SERVER_ID + ";" + Emu.PUERTO_JUEGO + ";" + 
                        SERVIDOR_PRIORIDAD + ";" + ESTADO_ONLINE + ";" + ipPublica);
            
            System.out.println("[SYNC] Conectado al Multi-Login en " + IP_MULTILOGIN + ":" + Emu.PUERTO_SINCRONIZADOR);
            
        } catch (Exception e) {
            conectado = false;
            System.out.println("[SYNC] No se pudo conectar al Multi-Login: " + e.getMessage());
        }
    }
    
    @Override
    public void run() {
        try {
            int c;
            int length = -1;
            int index = 0;
            byte[] bytes = new byte[1];
            
            while (conectado && (c = in.read()) != -1) {
                if (length == -1) {
                    length = in.available();
                    bytes = new byte[length + 1];
                    index = 0;
                }
                
                bytes[index++] = (byte) c;
                
                if (bytes.length == index) {
                    String tempPacket = new String(bytes, StandardCharsets.UTF_8);
                    for (String packet : tempPacket.split("[\u0000\n\r]")) {
                        if (!packet.isEmpty()) {
                            procesarPacket(packet);
                        }
                    }
                    length = -1;
                }
            }
        } catch (Exception e) {
            System.out.println("[SYNC] Conexión perdida: " + e.getMessage());
        } finally {
            desconectar();
        }
    }
    
    /**
     * Procesa un packet recibido del Multi-Login
     */
    private void procesarPacket(String packet) {
        try {
            char tipo = packet.charAt(0);
            String datos = packet.substring(1);
            
            switch (tipo) {
                case 'A': // Notificación de conexión de cuenta
                    // Formato: cuentaId;ip
                    procesarConexionCuenta(datos);
                    break;
                    
                case 'I': // Solicitud de cantidad de IPs
                    // Formato: ip
                    procesarSolicitudIP(datos);
                    break;
                    
                default:
                    // Packet desconocido del MultiLogin
                    System.out.println("[MUNDO-SYNC] >>> PACKET DESCONOCIDO DEL MULTILOGIN <<<");
                    System.out.println("[MUNDO-SYNC] Packet: " + packet);
                    System.out.println("[MUNDO-SYNC] Tipo: '" + tipo + "' (ASCII: " + (int)tipo + ")");
                    System.out.println("[MUNDO-SYNC] Datos: " + datos);
                    System.out.println("[MUNDO-SYNC] Longitud: " + packet.length());
                    System.out.println("[MUNDO-SYNC] ========================================");
            }
        } catch (Exception e) {
            System.out.println("[SYNC] Error procesando packet: " + e.getMessage());
        }
    }
    
    /**
     * Procesa notificación de conexión de cuenta
     */
    private void procesarConexionCuenta(String datos) {
        try {
            String[] partes = datos.split(";");
            int cuentaId = Integer.parseInt(partes[0]);
            String ip = partes[1];
            
            // Contar personajes de esta cuenta en este servidor
            int cantPersonajes = contarPersonajesCuenta(cuentaId);
            
            // Responder con la cantidad de personajes
            enviarPacket("A" + cuentaId + ";" + cantPersonajes);
            
        } catch (Exception e) {
            System.out.println("[SYNC] Error procesando conexión cuenta: " + e.getMessage());
        }
    }
    
    /**
     * Procesa solicitud de cantidad de conexiones desde una IP
     */
    private void procesarSolicitudIP(String ip) {
        try {
            int cantidad = contarConexionesIP(ip);
            enviarPacket("I" + ip + ";" + cantidad);
        } catch (Exception e) {
            System.out.println("[SYNC] Error procesando solicitud IP: " + e.getMessage());
        }
    }
    
    /**
     * Cuenta los personajes de una cuenta en este servidor
     */
    private int contarPersonajesCuenta(int cuentaId) {
        // Implementar según la estructura de tu emulador
        // Por ahora retorna 0, deberás adaptarlo a MundoDofus
        try {
            // Ejemplo: return MundoDofus.getCuenta(cuentaId).getPersonajes().size();
            return 0;
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * Cuenta las conexiones desde una IP específica
     */
    private int contarConexionesIP(String ip) {
        // Implementar según la estructura de tu emulador
        // Contar cuántos jugadores conectados tienen esa IP
        try {
            int count = 0;
            for (var perso : MundoDofus.getPJsEnLinea()) {
                if (perso != null && perso.getCuenta() != null) {
                    String persoIP = perso.getCuenta().getActualIP();
                    if (ip.equals(persoIP)) {
                        count++;
                    }
                }
            }
            return count;
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * Envía la cantidad de jugadores conectados al Multi-Login
     */
    public void enviarConectados() {
        if (!conectado) return;
        int conectados = MundoDofus.getPJsEnLinea().size();
        enviarPacket("C" + conectados);
    }
    
    /**
     * Envía cambio de estado del servidor
     */
    public void enviarEstado(int estado) {
        if (!conectado) return;
        enviarPacket("S" + estado);
    }
    
    /**
     * Envía actualización de personajes de una cuenta
     */
    public void enviarActualizacionPersonajes(int cuentaId, int cantidad) {
        if (!conectado) return;
        enviarPacket("A" + cuentaId + ";" + cantidad);
    }
    
    /**
     * Envía un packet al Multi-Login
     */
    public void enviarPacket(String packet) {
        if (!conectado || out == null || packet == null) return;
        
        try {
            out.print(packet + (char) 0x00);
            out.flush();
        } catch (Exception e) {
            System.out.println("[SYNC] Error enviando packet: " + e.getMessage());
        }
    }
    
    /**
     * Desconecta del Multi-Login
     */
    private void desconectar() {
        conectado = false;
        
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            // Ignorar
        }
    }
    
    /**
     * Detiene el cliente completamente
     */
    public void detener() {
        corriendo = false;
        
        if (timerReconexion != null) {
            timerReconexion.cancel();
        }
        if (timerConectados != null) {
            timerConectados.cancel();
        }
        
        // Enviar estado offline antes de cerrar
        enviarEstado(ESTADO_OFFLINE);
        
        desconectar();
    }
    
    /**
     * Verifica si está conectado al Multi-Login
     */
    public boolean estaConectado() {
        return conectado;
    }
}


package login;

import estaticos.*;
import variables.Cuenta;
import variables.Servidor;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Manejador de la conexión individual de un cliente Dofus.
 * Refactorizado para flujo 1.43.7: Al -> Ad -> Ac -> AH -> Ax -> AX
 */
public class LoginSocket implements Runnable {
    
    // Estados del flujo
    private static final String ESTADO_CLIENTE = "CLIENTE";
    private static final String ESTADO_CUENTA = "CUENTA";
    private static final String ESTADO_PASSWORD = "PASSWORD";
    private static final String ESTADO_DEFAULT = "DEFAULT";
    
    private Socket socket;
    private BufferedInputStream in;
    private PrintWriter out;
    private String actualIP;
    private String codigoLlave;
    private String nombreCuenta;
    private String estadoActual;
    private Cuenta cuenta;
    private int intentos;
    private boolean corriendo;
    
    public LoginSocket(Socket socket) {
        try {
            this.socket = socket;
            this.actualIP = socket.getInetAddress().getHostAddress();
            this.in = new BufferedInputStream(socket.getInputStream());
            this.out = new PrintWriter(socket.getOutputStream());
            this.estadoActual = ESTADO_CLIENTE;
            this.intentos = 0;
            this.corriendo = true;
            
            LoginServer.addCliente(this);
            
            Thread thread = new Thread(this);
            thread.setDaemon(true);
            thread.start();
        } catch (IOException e) {
            desconectar();
        }
    }
    
    @Override
    public void run() {
        try {
            // Paso 1: Enviar Policy File y Código de encriptación
            GestorSalida.ENVIAR_POLICY_FILE(out);
            codigoLlave = GestorSalida.ENVIAR_HC_CODIGO_LLAVE(out);
            
            int c;
            while (corriendo && (c = in.read()) != -1) {
                int length = in.available();
                byte[] bytes = new byte[length + 1];
                bytes[0] = (byte) c;
                
                for (int i = 1; i <= length; i++) {
                    bytes[i] = (byte) in.read();
                }
                
                String tempPacket = new String(bytes, StandardCharsets.UTF_8);
                for (String packet : tempPacket.split("[\u0000\n\r]")) {
                    if (!packet.isEmpty()) {
                        procesarPacket(packet);
                    }
                }
            }
        } catch (Exception e) {
            if (corriendo) System.out.println("[LOGIN-ERR] Error en el socket: " + e.getMessage());
        } finally {
            desconectar();
        }
    }
    
    /**
     * Procesador central de paquetes con Logs
     */
    private void procesarPacket(String packet) {
        System.out.println("[IN] << " + packet); // LOG DE ENTRADA
        
        try {
            switch (estadoActual) {
                case ESTADO_CLIENTE:
                    procesarCliente(packet);
                    break;
                case ESTADO_CUENTA:
                    procesarCuenta(packet);
                    break;
                case ESTADO_PASSWORD:
                    procesarPassword(packet);
                    break;
                case ESTADO_DEFAULT:
                    procesarDefault(packet);
                    break;
            }
        } catch (Exception e) {
            System.out.println("[ERROR] Error crítico procesando " + packet + ": " + e.getMessage());
            GestorSalida.ENVIAR_AlEp_CUENTA_NO_VALIDA(out);
            desconectar();
        }
    }
    
    /**
     * Verificación de versión (Paso 1)
     */
    private void procesarCliente(String packet) {
        if (packet.equalsIgnoreCase("<policy-file-request/>")) return;
        
        // Limpiar versión (Dofus 1.43.7 envía "1.43.7|idioma")
        String versionRecibida = packet.trim().split("\\|")[0];
        String versionEsperada = MainMultiLogin.VERSION_CLIENTE.trim();
        
        if (versionEsperada.equalsIgnoreCase("ANY") || versionRecibida.equals(versionEsperada)) {
            estadoActual = ESTADO_CUENTA;
            System.out.println("[LOGIN] Versión aceptada: " + versionRecibida);
        } else {
            System.out.println("[LOGIN] Versión rechazada. Recibida: " + versionRecibida + " | Esperada: " + versionEsperada);
            GestorSalida.ENVIAR_AlEv_ERROR_VERSION(out);
            desconectar();
        }
    }
    
    /**
     * Recibir cuenta (Paso 2)
     */
    private void procesarCuenta(String packet) {
        if (packet.contains("\n")) {
            // El cliente 1.43.7 puede enviar login\npass
            String[] partes = packet.split("\n", 2);
            nombreCuenta = Encriptador.filtrar(partes[0]);
            estadoActual = ESTADO_PASSWORD;
            try { procesarPassword(partes[1]); } catch (Exception e) {}
        } else {
            nombreCuenta = Encriptador.filtrar(packet);
            estadoActual = ESTADO_PASSWORD;
        }
    }
    
    /**
     * Validación de Password y Flujo Inicial (Paso 3)
     */
    private void procesarPassword(String packet) throws Exception {
        System.out.println("[LOGIN] Intentando autenticar cuenta: " + nombreCuenta);
        
        cuenta = GestorSQL.cargarCuentaPorNombre(nombreCuenta);
        if (cuenta == null) {
            GestorSalida.ENVIAR_AlEp_CUENTA_NO_VALIDA(out);
            desconectar();
            return;
        }
        
        String passBD = GestorSQL.getContrasenaCuenta(nombreCuenta);
        boolean passOk = false;
        if (packet.startsWith("#1")) {
            passOk = packet.equals(Encriptador.encriptarContrasena(codigoLlave, passBD));
        }
        
        if (!passOk) {
            GestorSalida.ENVIAR_AlEx_PASS_INCORRECTA(out);
            desconectar();
            return;
        }
        
        if (cuenta.estaBaneada()) {
            GestorSalida.ENVIAR_AlEb_CUENTA_BANEADA(out);
            desconectar();
            return;
        }
        
        estadoActual = ESTADO_DEFAULT;
        cuenta.setSocket(this);
        
        // --- FLUJO PARA SALTAR SELECCIÓN DE SERVIDOR ---
        GestorSalida.ENVIAR_Al_LOGIN_EXITOSO(out); // Al
        GestorSalida.ENVIAR_Ad_APODO(out, GestorSQL.getApodo(nombreCuenta)); // Ad
        GestorSalida.ENVIAR_Ac_COMUNIDAD(out, 0); // Ac0
        
        // Redirección inmediata al GameServer (ID 612 en tu caso)
        // Usamos el ID del servidor configurado en tu Main o el 612 directamente
        Servidor srv = Mundo.getServidor(612); 
        if (srv != null && srv.getEstado() == 1) {
            GestorSalida.ENVIAR_AXK_CONECTAR_SERVIDOR(out, cuenta.getId(), srv.getIp(), srv.getPuerto());
            System.out.println("[LOGIN] Auto-Redirección a GameServer 612 enviada.");
        } else {
            // Si el servidor está offline, mostramos la lista para que el usuario vea el estado
            GestorSalida.ENVIAR_AH_LISTA_SERVIDORES(out);
        }
    }
    
    /**
     * Manejo de selección de servidores (Paso 4)
     */
    private void procesarDefault(String packet) {
        // El cliente solicita la lista de personajes/servidores detallada
        if (packet.equals("Ax")) {
            System.out.println("[LOGIN] Solicitud de lista de servidores (Ax)");
            LoginServer.addEscogiendoServidor(this);
            // Mandamos AxK con los 4 campos por servidor (Corregido para NaN)
            GestorSalida.ENVIAR_AxK_LISTA_SERVIDORES(out, cuenta);
        } 
        // El cliente selecciona un servidor específico
        else if (packet.startsWith("AX")) {
            try {
                int id = Integer.parseInt(packet.substring(2));
                System.out.println("[LOGIN] El cliente seleccionó el ID: " + id);
                seleccionarServidor(id);
            } catch (Exception e) {
                GestorSalida.enviar(out, "AlEn"); // Servidor no disponible
            }
        }
        // Fila de espera
        else if (packet.startsWith("Af")) {
            GestorSalida.ENVIAR_Af_SU_TURNO(out);
        }
    }
    
    /**
     * Redirección al GameServer (Paso 5)
     */
    private void seleccionarServidor(int id) {
        Servidor srv = Mundo.getServidor(id);
        
        // Verificamos que el servidor exista y esté ONLINE
        if (srv != null && srv.getEstado() == 1 && srv.getConector() != null) {
            System.out.println("[LOGIN] Redirigiendo a Servidor " + id + " (" + srv.getIp() + ":" + srv.getPuerto() + ")");
            GestorSalida.ENVIAR_AXK_CONECTAR_SERVIDOR(out, cuenta.getId(), srv.getIp(), srv.getPuerto());
        } else {
            System.out.println("[LOGIN] Servidor " + id + " no disponible o desconectado.");
            GestorSalida.enviar(out, "AlEn"); // Muestra "Servidor no disponible" en el cliente
        }
    }
    
    public void desconectar() {
        if (!corriendo) return;
        corriendo = false;
        System.out.println("[LOGIN] Cliente desconectado (" + actualIP + ")");
        
        if (cuenta != null && cuenta.getSocket() == this) {
            cuenta.setSocket(null);
        }
        
        LoginServer.removeCliente(this);
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (Exception e) {}
    }
    
    public PrintWriter getOut() { return out; }
    public String getActualIP() { return actualIP; }
}
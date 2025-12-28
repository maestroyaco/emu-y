package login;

import estaticos.GestorSQL;
import estaticos.GestorSalida;
import estaticos.MainMultiLogin;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Servidor que acepta conexiones de clientes Dofus
 * Maneja la autenticación y selección de servidor
 */
public class LoginServer implements Runnable {
    
    private final ServerSocket serverSocket;
    private final Thread thread;
    private boolean corriendo;
    
    // Clientes conectados
    private static final List<LoginSocket> clientes = new CopyOnWriteArrayList<>();
    private static final List<LoginSocket> clientesEscogiendoServidor = new CopyOnWriteArrayList<>();
    
    // Control de IPs
    private static final Map<String, Long> tiemposConexion = new ConcurrentHashMap<>();
    private static final Map<String, List<LoginSocket>> ipClientes = new ConcurrentHashMap<>();
    
    // Anti-DDoS
    private static boolean bloqueado = false;
    private static final int[] ataques = new int[3];
    private static int indiceAtaque = 0;
    
    public LoginServer(int puerto) {
        ServerSocket tempSocket = null;
        try {
            tempSocket = new ServerSocket(puerto);
            System.out.println("[LOGIN] Servidor de login iniciado en puerto " + puerto);
        } catch (IOException e) {
            System.out.println("[LOGIN] Error al iniciar servidor: " + e.getMessage());
            e.printStackTrace();
        }
        
        this.serverSocket = tempSocket;
        this.corriendo = true;
        this.thread = new Thread(this);
        this.thread.setDaemon(true);
        this.thread.start();
        
        // Iniciar timer anti-DDoS
        if (MainMultiLogin.PARAM_ANTI_DDOS) {
            iniciarAntiDDoS();
        }
    }
    
    @Override
    public void run() {
        if (serverSocket == null) return;
        
        while (corriendo && !serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                String ip = socket.getInetAddress().getHostAddress();
                
                if (MainMultiLogin.MODO_DEBUG) {
                    System.out.println("[LOGIN] Conexión entrante desde: " + ip);
                }
                
                // Verificar si está bloqueado por DDoS
                if (bloqueado) {
                    socket.close();
                    continue;
                }
                
                // Verificar si IP está baneada
                if (GestorSQL.esIPBaneada(ip)) {
                    socket.close();
                    continue;
                }
                
                // Verificar tiempo entre conexiones
                Long ultimaConexion = tiemposConexion.get(ip);
                if (ultimaConexion != null && 
                    ultimaConexion + MainMultiLogin.MILISEGUNDOS_SIG_CONEXION > System.currentTimeMillis()) {
                    socket.close();
                    continue;
                }
                
                tiemposConexion.put(ip, System.currentTimeMillis());
                
                // Registrar para anti-DDoS
                if (MainMultiLogin.PARAM_ANTI_DDOS) {
                    ataques[indiceAtaque]++;
                }
                
                // Crear handler para este cliente
                new LoginSocket(socket);
                
            } catch (IOException e) {
                if (corriendo) {
                    MainMultiLogin.escribirLog("[LOGIN] Error aceptando conexión: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Inicia el sistema anti-DDoS
     */
    private void iniciarAntiDDoS() {
        new Thread(() -> {
            while (corriendo) {
                try {
                    Thread.sleep(1000);
                    
                    // Verificar si hay ataque
                    int minAtaque = 25;
                    if (!bloqueado && ataques[0] > minAtaque && ataques[1] > minAtaque && ataques[2] > minAtaque) {
                        bloqueado = true;
                        System.out.println("[DDOS] ¡ATAQUE DETECTADO! Servidor temporalmente bloqueado");
                    } else if (bloqueado && ataques[0] < minAtaque && ataques[1] < minAtaque && ataques[2] < minAtaque) {
                        bloqueado = false;
                        System.out.println("[DDOS] Ataque terminado, servidor restablecido");
                    }
                    
                    // Rotar índice y resetear
                    indiceAtaque = (indiceAtaque + 1) % 3;
                    ataques[indiceAtaque] = 0;
                    
                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();
    }
    
    // ==================== GESTIÓN DE CLIENTES ====================
    
    /**
     * Agrega un cliente a la lista
     */
    public static void addCliente(LoginSocket cliente) {
        if (cliente == null) return;
        clientes.add(cliente);
        addIPCliente(cliente);
    }
    
    /**
     * Elimina un cliente de la lista
     */
    public static void removeCliente(LoginSocket cliente) {
        if (cliente == null) return;
        clientes.remove(cliente);
        clientesEscogiendoServidor.remove(cliente);
        removeIPCliente(cliente);
    }
    
    /**
     * Agrega un cliente a la lista de escogiendo servidor
     */
    public static void addEscogiendoServidor(LoginSocket cliente) {
        if (!clientesEscogiendoServidor.contains(cliente)) {
            clientesEscogiendoServidor.add(cliente);
        }
    }
    
    /**
     * Elimina un cliente de la lista de escogiendo servidor
     */
    public static void removeEscogiendoServidor(LoginSocket cliente) {
        clientesEscogiendoServidor.remove(cliente);
    }
    
    /**
     * Actualiza el estado de los servidores para todos los clientes que están escogiendo
     */
    public static void actualizarEstadoServidores() {
        for (LoginSocket cliente : clientesEscogiendoServidor) {
            try {
                GestorSalida.ENVIAR_AH_LISTA_SERVIDORES(cliente.getOut());
            } catch (Exception e) {
                clientesEscogiendoServidor.remove(cliente);
            }
        }
    }
    
    // ==================== GESTIÓN DE IPs ====================
    
    private static void addIPCliente(LoginSocket cliente) {
        String ip = cliente.getActualIP();
        if (ip == null) return;
        
        ipClientes.computeIfAbsent(ip, k -> new ArrayList<>());
        if (!ipClientes.get(ip).contains(cliente)) {
            ipClientes.get(ip).add(cliente);
        }
    }
    
    private static void removeIPCliente(LoginSocket cliente) {
        String ip = cliente.getActualIP();
        if (ip == null || ipClientes.get(ip) == null) return;
        ipClientes.get(ip).remove(cliente);
    }
    
    /**
     * Obtiene la cantidad de conexiones desde una IP (login + servidores de juego)
     */
    public static int getCantidadConexionesIP(String ip) {
        int cantLogin = ipClientes.get(ip) != null ? ipClientes.get(ip).size() : 0;
        int cantServidores = estaticos.Mundo.getCantidadConexionesIP(ip);
        return cantLogin + cantServidores;
    }
    
    /**
     * Cierra el servidor
     */
    public void cerrar() {
        corriendo = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            MainMultiLogin.escribirLog("[LOGIN] Error al cerrar: " + e.getMessage());
        }
    }
}


package sincronizador;

import estaticos.MainMultiLogin;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Servidor que acepta conexiones de los servidores de juego
 * Los servidores de juego se conectan aquí para sincronizar información
 */
public class SincronizadorServer implements Runnable {
    
    private final ServerSocket serverSocket;
    private final Thread thread;
    private boolean corriendo;
    
    public SincronizadorServer(int puerto) {
        ServerSocket tempSocket = null;
        try {
            tempSocket = new ServerSocket(puerto);
            System.out.println("[SYNC] Servidor de sincronización iniciado en puerto " + puerto);
        } catch (IOException e) {
            System.out.println("[SYNC] Error al iniciar servidor: " + e.getMessage());
            e.printStackTrace();
        }
        
        this.serverSocket = tempSocket;
        this.corriendo = true;
        this.thread = new Thread(this);
        this.thread.setDaemon(true);
        this.thread.start();
    }
    
    @Override
    public void run() {
        if (serverSocket == null) return;
        
        while (corriendo && !serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                String ip = socket.getInetAddress().getHostAddress();
                
                if (MainMultiLogin.MOSTRAR_SINCRONIZACION) {
                    System.out.println("[SYNC] Conexión entrante desde: " + ip);
                }
                
                // Crear handler para este servidor de juego
                new SincronizadorSocket(socket);
                
            } catch (IOException e) {
                if (corriendo) {
                    MainMultiLogin.escribirLog("[SYNC] Error aceptando conexión: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Cierra el servidor de sincronización
     */
    public void cerrar() {
        corriendo = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            MainMultiLogin.escribirLog("[SYNC] Error al cerrar: " + e.getMessage());
        }
    }
}


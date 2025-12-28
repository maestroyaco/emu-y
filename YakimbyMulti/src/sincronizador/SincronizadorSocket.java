package sincronizador;

import estaticos.Encriptador;
import estaticos.Mundo;
import login.LoginServer;
import variables.Servidor;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Gestiona la conexión con el GameServer.
 * LOGS: [SYNC]
 */
public class SincronizadorSocket implements Runnable {
    
    private Socket socket;
    private BufferedInputStream in;
    private PrintWriter out;
    private Servidor servidor;
    private String ip;
    private boolean corriendo;
    
    public SincronizadorSocket(Socket socket) {
        try {
            this.socket = socket;
            this.ip = socket.getInetAddress().getHostAddress();
            this.in = new BufferedInputStream(socket.getInputStream());
            this.out = new PrintWriter(socket.getOutputStream());
            this.corriendo = true;
            
            Thread t = new Thread(this);
            t.setDaemon(true);
            t.start();
            System.out.println("[SYNC] Nueva conexión desde GS IP: " + ip);
        } catch (IOException e) {
            desconectar();
        }
    }
    
    @Override
    public void run() {
        try {
            int c;
            while (corriendo && (c = in.read()) != -1) {
                int length = in.available();
                byte[] bytes = new byte[length + 1];
                bytes[0] = (byte) c;
                for (int i = 1; i <= length; i++) {
                    bytes[i] = (byte) in.read();
                }
                String packet = new String(bytes, StandardCharsets.UTF_8);
                for (String p : packet.split("[\u0000\n\r]")) {
                    if (!p.isEmpty()) procesar(p);
                }
            }
        } catch (Exception e) {
            desconectar();
        } finally {
            desconectar();
        }
    }
    
    private void procesar(String p) {
        try {
            char tipo = p.charAt(0);
            String datos = p.substring(1);
            
            switch (tipo) {
                case 'D': // Registro: id;puerto;prioridad;estado;ip
                    String[] partes = datos.split(";");
                    int id = Integer.parseInt(partes[0]);
                    int puerto = Integer.parseInt(partes[1]);
                    int estado = Integer.parseInt(partes[3]);
                    String ipGS = partes.length > 4 ? partes[4] : ip;
                    
                    servidor = Mundo.getServidor(id);
                    if (servidor == null) {
                        servidor = new Servidor(id, puerto, estado);
                        Mundo.addServidor(servidor);
                    } else {
                        servidor.setPuerto(puerto);
                        servidor.setEstado(estado);
                    }
                    servidor.setIp(ipGS);
                    servidor.setConector(this);
                    
                    System.out.println("[SYNC] GS ID " + id + " registrado como ONLINE.");
                    LoginServer.actualizarEstadoServidores();
                    break;
                    
                case 'S': // Cambio de estado
                    if (servidor != null) {
                        servidor.setEstado(Integer.parseInt(datos));
                        System.out.println("[SYNC] GS ID " + servidor.getId() + " cambió estado a: " + datos);
                        LoginServer.actualizarEstadoServidores();
                    }
                    break;
                    
                case 'C': // Población
                    if (servidor != null) {
                        servidor.setPoblacion(Integer.parseInt(datos));
                    }
                    break;
            }
        } catch (Exception e) {
            System.out.println("[SYNC-ERR] Error procesando: " + p);
        }
    }
    
    public void enviarPacket(String p) {
        if (out != null) {
            out.print(Encriptador.aUTF(p) + (char) 0x00);
            out.flush();
        }
    }

    public boolean estaCerrado() { return socket == null || socket.isClosed(); }

    private void desconectar() {
        corriendo = false;
        if (servidor != null) {
            System.out.println("[SYNC] GS ID " + servidor.getId() + " se ha desconectado.");
            servidor.setEstado(0); // Offline
            servidor.setConector(null);
            LoginServer.actualizarEstadoServidores();
        }
        try { if (socket != null) socket.close(); } catch (Exception e) {}
    }
}
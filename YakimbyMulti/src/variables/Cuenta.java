package variables;

import login.LoginSocket;
import java.util.Map;
import java.util.TreeMap;

/**
 * Representa una cuenta de usuario en el Multi-Login
 */
public class Cuenta {
    
    private final int id;
    private final String nombre;
    private String contrasena;
    private int rango;
    private long baneado;
    private String ultimaIP;
    private String preguntaSecreta;
    private String respuestaSecreta;
    private String apodo;
    private int tiempoAbono;
    private byte actualizar;
    
    // Personajes por servidor: Map<ServidorID, CantidadPersonajes>
    private final Map<Integer, Integer> personajesPorServidor;
    
    // Socket actual de conexión
    private LoginSocket socket;
    
    public Cuenta(int id, String nombre, String contrasena, int rango, long baneado) {
        this.id = id;
        this.nombre = nombre;
        this.contrasena = contrasena;
        this.rango = rango;
        this.baneado = baneado;
        this.ultimaIP = "";
        this.preguntaSecreta = "";
        this.respuestaSecreta = "";
        this.apodo = "";
        this.tiempoAbono = 0;
        this.actualizar = 0;
        this.personajesPorServidor = new TreeMap<>();
        this.socket = null;
    }
    
    // Getters
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getContrasena() { return contrasena; }
    public int getRango() { return rango; }
    public long getBaneado() { return baneado; }
    public String getUltimaIP() { return ultimaIP; }
    public String getPreguntaSecreta() { return preguntaSecreta; }
    public String getRespuestaSecreta() { return respuestaSecreta; }
    public String getApodo() { return apodo; }
    public int getTiempoAbono() { return tiempoAbono; }
    public byte getActualizar() { return actualizar; }
    public LoginSocket getSocket() { return socket; }
    
    // Setters
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    public void setRango(int rango) { this.rango = rango; }
    public void setBaneado(long baneado) { this.baneado = baneado; }
    public void setUltimaIP(String ultimaIP) { this.ultimaIP = ultimaIP; }
    public void setPreguntaSecreta(String preguntaSecreta) { this.preguntaSecreta = preguntaSecreta; }
    public void setRespuestaSecreta(String respuestaSecreta) { this.respuestaSecreta = respuestaSecreta; }
    public void setApodo(String apodo) { this.apodo = apodo; }
    public void setTiempoAbono(int tiempoAbono) { this.tiempoAbono = tiempoAbono; }
    public void setActualizar(byte actualizar) { this.actualizar = actualizar; }
    public void setSocket(LoginSocket socket) { this.socket = socket; }
    
    /**
     * Establece la cantidad de personajes en un servidor específico
     */
    public void setPersonajes(int servidorId, int cantidad) {
        personajesPorServidor.put(servidorId, cantidad);
    }
    
    /**
     * Obtiene la cantidad de personajes en un servidor específico
     */
    public int getPersonajes(int servidorId) {
        return personajesPorServidor.getOrDefault(servidorId, 0);
    }
    
    /**
     * Obtiene el total de personajes en todos los servidores
     */
    public int getTotalPersonajes() {
        int total = 0;
        for (int cant : personajesPorServidor.values()) {
            total += cant;
        }
        return total;
    }
    
    /**
     * Genera el string de personajes para el packet AxK
     * Formato: servidorID,cantidad;servidorID2,cantidad2
     */
    public String getStringPersonajesServidores() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, Integer> entry : personajesPorServidor.entrySet()) {
            if (sb.length() > 0) sb.append(";");
            sb.append(entry.getKey()).append(";").append(entry.getValue());
        }
        return sb.toString();
    }
    
    /**
     * Verifica si la cuenta está baneada
     */
    public boolean estaBaneada() {
        if (baneado == 0) return false;
        if (baneado == -1) return true; // Ban permanente
        return baneado > System.currentTimeMillis();
    }
    
    /**
     * Verifica si la cuenta está conectada
     */
    public boolean estaConectada() {
        return socket != null;
    }
    
    @Override
    public String toString() {
        return "Cuenta[id=" + id + ", nombre=" + nombre + ", rango=" + rango + "]";
    }
}


package variables;

import sincronizador.SincronizadorSocket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Representa un servidor de juego conectado al Multi-Login.
 * Refactorizado para compatibilidad con Dofus 1.43.7
 */
public class Servidor {
    
    // Estados estándar
    public static final int SERVIDOR_OFFLINE = 0;
    public static final int SERVIDOR_ONLINE = 1;
    public static final int SERVIDOR_SAVING = 2;
    
    private final int id;
    private int puerto;
    private String ip;
    private int estado;       // 0=offline, 1=online, 2=guardando
    private int poblacion;    // Cantidad de jugadores conectados
    private int comunidad;    // 0=ES, 1=FR, etc.
    
    // Conector físico con el GameServer
    private SincronizadorSocket conector;
    
    // Mapa de IPs para control de multicuentas
    private final Map<String, Integer> ipsCantidad;

    public Servidor(int id, int puerto, int estado) {
        this.id = id;
        this.puerto = puerto;
        this.estado = estado;
        this.ip = "127.0.0.1";
        this.poblacion = 0;
        this.comunidad = 0;
        this.conector = null;
        this.ipsCantidad = new ConcurrentHashMap<>();
    }

    /* ================= GETTERS ================= */

    public int getId() { return id; }
    public int getPuerto() { return puerto; }
    public String getIp() { return ip; }
    public int getEstado() { return estado; }
    public int getPoblacion() { return poblacion; }
    public int getComunidad() { return comunidad; }
    public SincronizadorSocket getConector() { return conector; }

    /* ================= SETTERS ================= */

    public void setPuerto(int puerto) { this.puerto = puerto; }
    public void setIp(String ip) { this.ip = ip; }
    public void setEstado(int estado) { this.estado = estado; }
    public void setPoblacion(int poblacion) { this.poblacion = poblacion; }
    public void setComunidad(int comunidad) { this.comunidad = comunidad; }
    public void setConector(SincronizadorSocket conector) { this.conector = conector; }

    /* ================= LÓGICA DE PROTOCOLO ================= */

    /**
     * Establece la cantidad de conexiones desde una IP específica (enviado por el GS)
     */
    public void setCantidadIp(String ip, int cantidad) {
        ipsCantidad.put(ip, cantidad);
    }

    /**
     * Obtiene la cantidad de conexiones desde una IP específica
     */
    public int getCantidadPorIP(String ip) {
        return ipsCantidad.getOrDefault(ip, 0);
    }

    /**
     * Genera el string para el packet AH (Lista de servidores inicial)
     * Formato 1.43.7: ID;Estado;PoblacionVisual;PuedeLoguear(1/0)
     */    
    public String getStringParaAH() {
    	// estado: 0=offline, 1=online, 2=guardando
        int estadoNum = (estado == SERVIDOR_ONLINE ? 1 : (estado == SERVIDOR_SAVING ? 2 : 0));
        int poblacionVisual = 0; 
        if (poblacion > 100) poblacionVisual = 1;
        if (poblacion > 500) poblacionVisual = 2;

        // Cambiamos 'comunidad' por '1' para decirle al cliente que SI puede loguear
        return id + ";" + estadoNum + ";" + poblacionVisual + ";1"; 
    }
    
    /**
     * Verifica si el servidor está listo para recibir jugadores
     */
    public boolean estaDisponible() {
        return estado == SERVIDOR_ONLINE && conector != null && !conector.estaCerrado();
    }
}
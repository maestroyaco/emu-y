package estaticos;

import variables.Cuenta;
import variables.Servidor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Contenedor central de datos.
 * Refactorizado: Incluye métodos de conteo de IP y sincronización.
 * LOGS: [MUNDO]
 */
public class Mundo {
    
    private static final Map<Integer, Servidor> servidores = new ConcurrentHashMap<>();
    private static final Map<Integer, Cuenta> cuentas = new ConcurrentHashMap<>();
    
    // ==================== GESTIÓN DE SERVIDORES ====================

    public static Map<Integer, Servidor> getServidores() {
        return servidores;
    }
    
    public static Servidor getServidor(int id) {
        return servidores.get(id);
    }
    
    public static void addServidor(Servidor servidor) {
        servidores.put(servidor.getId(), servidor);
        System.out.println("[MUNDO] Servidor " + servidor.getId() + " añadido al mapa global.");
    }

    /**
     * Genera la cadena de servidores para el paquete AH.
     */
    public static String strParaAH() {
        if (servidores.isEmpty()) {
            System.out.println("[MUNDO-ADVERTENCIA] Intentando generar AH pero no hay servidores registrados.");
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        for (Servidor s : servidores.values()) {
            if (sb.length() > 0) sb.append("|");
            sb.append(s.getStringParaAH());
        }
        return sb.toString();
    }
    
    public static int getTotalConectados() {
        int total = 0;
        for (Servidor s : servidores.values()) {
            total += s.getPoblacion();
        }
        return total;
    }

    /**
     * MÉTODO REINTEGRADO: Obtiene la cantidad de conexiones desde una IP en todos los GS.
     */
    public static int getCantidadConexionesIP(String ip) {
        int total = 0;
        for (Servidor s : servidores.values()) {
            total += s.getCantidadPorIP(ip);
        }
        return total;
    }
    
    /**
     * MÉTODO REINTEGRADO: Solicita a todos los GS que informen su cantidad de IPs.
     */
    public static void solicitarCantidadIPs(String ip) {
        for (Servidor s : servidores.values()) {
            if (s.getConector() != null && !s.getConector().estaCerrado()) {
                s.getConector().enviarPacket("I" + ip);
            }
        }
    }

    // ==================== GESTIÓN DE CUENTAS ====================

    public static Cuenta getCuenta(int id) {
        if (id <= 0) return null;
        Cuenta cuenta = cuentas.get(id);
        if (cuenta != null) return cuenta;
        
        cuenta = GestorSQL.cargarCuenta(id);
        if (cuenta != null) cuentas.put(id, cuenta);
        return cuenta;
    }
    
    public static Cuenta getCuentaPorNombre(String nombre) {
        int id = GestorSQL.getIdCuentaPorNombre(nombre);
        if (id > 0) return getCuenta(id);
        return null;
    }
    
    public static void addCuenta(Cuenta cuenta) {
        if (cuenta != null) cuentas.put(cuenta.getId(), cuenta);
    }

    public static void inicializar() {
        System.out.println("[MUNDO] Sistema de datos (Mundo) listo y sincronizado.");
    }
}
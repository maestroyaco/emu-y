package estaticos;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import variables.Cuenta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Gestor de base de datos para el Multi-Login
 * Maneja únicamente las operaciones relacionadas con cuentas
 */
public class GestorSQL {
    
    private static HikariDataSource dataSource;
    private static Connection connection;
    private static final Set<String> ipsBaneadas = new HashSet<>();
    private static Timer timerCommit;
    
    /**
     * Inicia la conexión a la base de datos
     */
    public static boolean iniciarConexion() {
        try {
            // Silenciar logs de HikariCP
            Logger.getLogger("com.zaxxer.hikari.pool.PoolBase").setLevel(Level.OFF);
            Logger.getLogger("com.zaxxer.hikari.pool.HikariPool").setLevel(Level.OFF);
            Logger.getLogger("com.zaxxer.hikari.HikariDataSource").setLevel(Level.OFF);
            Logger.getLogger("com.zaxxer.hikari.HikariConfig").setLevel(Level.OFF);
            
            HikariConfig config = new HikariConfig();
            config.setDataSourceClassName("org.mariadb.jdbc.MySQLDataSource");
            config.addDataSourceProperty("serverName", MainMultiLogin.BD_HOST);
            config.addDataSourceProperty("port", MainMultiLogin.BD_PORT);
            config.addDataSourceProperty("databaseName", MainMultiLogin.BD_CUENTAS);
            config.addDataSourceProperty("user", MainMultiLogin.BD_USUARIO);
            config.addDataSourceProperty("password", MainMultiLogin.BD_PASS);
            config.setAutoCommit(true);
            config.setMaximumPoolSize(5);
            config.setMinimumIdle(2);
            config.setConnectionTimeout(30000);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);
            config.setPoolName("multilogin-pool");
            
            dataSource = new HikariDataSource(config);
            connection = dataSource.getConnection();
            
            if (!connection.isValid(1000)) {
                System.out.println("[SQL] Error: Conexión inválida");
                return false;
            }
            
            // Cargar IPs baneadas en memoria
            cargarIPsBaneadas();
            
            // Timer para mantener conexión activa
            iniciarTimerCommit();
            
            return true;
        } catch (SQLException e) {
            System.out.println("[SQL] Error de conexión: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private static void iniciarTimerCommit() {
        timerCommit = new Timer();
        timerCommit.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (connection != null && !connection.isClosed()) {
                        // Ejecutar query simple para mantener conexión
                        executeQuery("SELECT 1");
                    }
                } catch (SQLException e) {
                    MainMultiLogin.escribirLog("[SQL] Error en timer: " + e.getMessage());
                }
            }
        }, 300000, 300000); // Cada 5 minutos
    }
    
    /**
     * Cierra la conexión a la base de datos
     */
    public static void cerrarConexion() {
        try {
            if (timerCommit != null) {
                timerCommit.cancel();
            }
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
            if (dataSource != null && !dataSource.isClosed()) {
                dataSource.close();
            }
        } catch (SQLException e) {
            System.out.println("[SQL] Error al cerrar: " + e.getMessage());
        }
    }
    
    private static ResultSet executeQuery(String sql) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(sql);
        return ps.executeQuery();
    }
    
    private static void closeResultSet(ResultSet rs) {
        try {
            if (rs != null) {
                rs.getStatement().close();
                rs.close();
            }
        } catch (SQLException ignored) {}
    }
    
    // ==================== OPERACIONES DE CUENTAS ====================
    
    /**
     * Obtiene el ID de una cuenta por nombre
     */
    public static int getIdCuentaPorNombre(String nombre) {
        ResultSet rs = null;
        try {
            // Estructura de BD: tabla 'cuentas', columna 'cuenta'
            PreparedStatement ps = connection.prepareStatement(
                "SELECT id FROM cuentas WHERE cuenta = ? LIMIT 1"
            );
            ps.setString(1, nombre);
            rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                System.out.println("[SQL] ✓ Cuenta '" + nombre + "' encontrada con ID: " + id);
                closeResultSet(rs);
                return id;
            }
            closeResultSet(rs);
            
            // Intentar búsqueda case-insensitive como fallback
            ps = connection.prepareStatement(
                "SELECT id FROM cuentas WHERE LOWER(cuenta) = LOWER(?) LIMIT 1"
            );
            ps.setString(1, nombre);
            rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                System.out.println("[SQL] ✓ Cuenta '" + nombre + "' encontrada (case-insensitive) con ID: " + id);
                closeResultSet(rs);
                return id;
            }
            closeResultSet(rs);
            
            System.out.println("[SQL] ✗ Cuenta '" + nombre + "' no encontrada en la tabla 'cuentas'");
        } catch (SQLException e) {
            System.out.println("[SQL] Error getIdCuentaPorNombre: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResultSet(rs);
        }
        return -1;
    }
    
    /**
     * Obtiene la contraseña de una cuenta
     * Estructura BD: tabla 'cuentas', columna nombre 'cuenta', columna pass 'pass'
     */
    public static String getContrasenaCuenta(String nombre) {
        ResultSet rs = null;
        try {
            PreparedStatement ps = connection.prepareStatement(
                "SELECT pass FROM cuentas WHERE cuenta = ? LIMIT 1"
            );
            ps.setString(1, nombre);
            rs = ps.executeQuery();
            if (rs.next()) {
                String pass = rs.getString("pass");
                if (pass != null && !pass.isEmpty()) {
                    System.out.println("[SQL] ✓ Contraseña obtenida para cuenta '" + nombre + "'");
                    closeResultSet(rs);
                    return pass;
                }
            }
            closeResultSet(rs);
            
            System.out.println("[SQL] ✗ No se pudo obtener contraseña para cuenta '" + nombre + "'");
        } catch (SQLException e) {
            System.out.println("[SQL] Error getContrasenaCuenta: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResultSet(rs);
        }
        return "";
    }
    
    /**
     * Carga una cuenta desde la base de datos
     * Estructura BD: tabla 'cuentas', columnas: id, cuenta, pass, gm, baneado, ultimaIP
     */
    public static Cuenta cargarCuenta(int id) {
        ResultSet rs = null;
        try {
            PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM cuentas WHERE id = ? LIMIT 1"
            );
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                // Estructura: id, cuenta (nombre), pass (contraseña), gm (rango), baneado, ultimaIP
                int cuentaId = rs.getInt("id");
                String cuentaNombre = rs.getString("cuenta");
                String cuentaPass = rs.getString("pass");
                
                // Obtener rango (puede ser 'gm' o 'rango')
                int rango = 0;
                try {
                    rango = rs.getInt("gm");
                } catch (SQLException e) {
                    try {
                        rango = rs.getInt("rango");
                    } catch (SQLException e2) {
                        // No hay columna de rango, usar 0
                    }
                }
                
                // Obtener baneado
                long baneado = 0;
                try {
                    baneado = rs.getLong("baneado");
                } catch (SQLException e) {
                    // No hay columna baneado, usar 0
                }
                
                Cuenta cuenta = new Cuenta(cuentaId, cuentaNombre, cuentaPass, rango, baneado);
                
                // Intentar obtener ultimaIP si existe
                try {
                    String ultimaIP = rs.getString("ultimaIP");
                    if (ultimaIP != null && !ultimaIP.isEmpty()) {
                        cuenta.setUltimaIP(ultimaIP);
                    }
                } catch (SQLException e) {
                    // Columna puede no existir
                }
                
                System.out.println("[SQL] ✓ Cuenta cargada: ID=" + cuentaId + ", Nombre=" + cuentaNombre + ", Rango=" + rango);
                return cuenta;
            }
        } catch (SQLException e) {
            System.out.println("[SQL] Error cargarCuenta: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResultSet(rs);
        }
        return null;
    }
    
    /**
     * Carga una cuenta por nombre
     */
    public static Cuenta cargarCuentaPorNombre(String nombre) {
        int id = getIdCuentaPorNombre(nombre);
        if (id > 0) {
            return cargarCuenta(id);
        }
        return null;
    }
    
    /**
     * Obtiene el tiempo de baneo de una cuenta
     */
    public static long getBaneado(String nombre) {
        ResultSet rs = null;
        try {
            PreparedStatement ps = connection.prepareStatement(
                "SELECT baneado FROM cuentas WHERE cuenta = ? LIMIT 1"
            );
            ps.setString(1, nombre);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getLong("baneado");
            }
        } catch (SQLException e) {
            MainMultiLogin.escribirLog("[SQL] Error getBaneado: " + e.getMessage());
        } finally {
            closeResultSet(rs);
        }
        return 0;
    }
    
    /**
     * Establece el tiempo de baneo de una cuenta
     */
    public static void setBaneado(String nombre, long tiempo) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "UPDATE cuentas SET baneado = ? WHERE cuenta = ?"
            );
            ps.setLong(1, tiempo);
            ps.setString(2, nombre);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            MainMultiLogin.escribirLog("[SQL] Error setBaneado: " + e.getMessage());
        }
    }
    
    /**
     * Actualiza la última IP de una cuenta
     */
    public static void actualizarUltimaIP(String ip, int cuentaId) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "UPDATE cuentas SET ultimaIP = ? WHERE id = ?"
            );
            ps.setString(1, ip);
            ps.setInt(2, cuentaId);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            MainMultiLogin.escribirLog("[SQL] Error actualizarUltimaIP: " + e.getMessage());
        }
    }
    
    // ==================== OPERACIONES DE IPs BANEADAS ====================
    
    /**
     * Carga todas las IPs baneadas en memoria
     */
    private static void cargarIPsBaneadas() {
        ResultSet rs = null;
        try {
            rs = executeQuery("SELECT ip FROM ips_baneadas");
            while (rs.next()) {
                ipsBaneadas.add(rs.getString("ip"));
            }
            System.out.println("[SQL] " + ipsBaneadas.size() + " IPs baneadas cargadas");
        } catch (SQLException e) {
            // La tabla puede no existir, la creamos
            crearTablaIPsBaneadas();
        } finally {
            closeResultSet(rs);
        }
    }
    
    private static void crearTablaIPsBaneadas() {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS ips_baneadas (" +
                "ip VARCHAR(45) PRIMARY KEY, " +
                "fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")"
            );
            ps.executeUpdate();
            ps.close();
            System.out.println("[SQL] Tabla ips_baneadas creada");
        } catch (SQLException e) {
            MainMultiLogin.escribirLog("[SQL] Error crearTablaIPsBaneadas: " + e.getMessage());
        }
    }
    
    /**
     * Verifica si una IP está baneada
     */
    public static boolean esIPBaneada(String ip) {
        return ipsBaneadas.contains(ip);
    }
    
    /**
     * Banea una IP
     */
    public static void insertarBanIP(String ip) {
        if (ipsBaneadas.contains(ip)) return;
        
        try {
            PreparedStatement ps = connection.prepareStatement(
                "INSERT IGNORE INTO ips_baneadas (ip) VALUES (?)"
            );
            ps.setString(1, ip);
            ps.executeUpdate();
            ps.close();
            ipsBaneadas.add(ip);
            MainMultiLogin.escribirLog("[SEGURIDAD] IP baneada: " + ip);
        } catch (SQLException e) {
            MainMultiLogin.escribirLog("[SQL] Error insertarBanIP: " + e.getMessage());
        }
    }
    
    /**
     * Desbanea una IP
     */
    public static void eliminarBanIP(String ip) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM ips_baneadas WHERE ip = ?"
            );
            ps.setString(1, ip);
            ps.executeUpdate();
            ps.close();
            ipsBaneadas.remove(ip);
        } catch (SQLException e) {
            MainMultiLogin.escribirLog("[SQL] Error eliminarBanIP: " + e.getMessage());
        }
    }
    
    /**
     * Obtiene la pregunta secreta de una cuenta
     */
    public static String getPreguntaSecreta(String nombre) {
        // Por ahora retorna vacío, implementar si la tabla tiene este campo
        return "";
    }
    
    /**
     * Obtiene el apodo de una cuenta
     * Si no existe columna 'apodo', retorna el nombre de la cuenta
     */
    public static String getApodo(String nombre) {
        ResultSet rs = null;
        try {
            // Intentar obtener apodo si existe la columna
            PreparedStatement ps = connection.prepareStatement(
                "SELECT apodo FROM cuentas WHERE cuenta = ? LIMIT 1"
            );
            ps.setString(1, nombre);
            rs = ps.executeQuery();
            if (rs.next()) {
                String apodo = rs.getString("apodo");
                if (apodo != null && !apodo.isEmpty()) {
                    closeResultSet(rs);
                    return apodo;
                }
            }
            closeResultSet(rs);
        } catch (SQLException e) {
            // Campo puede no existir, usar nombre de cuenta como apodo
        } finally {
            closeResultSet(rs);
        }
        // Si no hay apodo, retornar el nombre de la cuenta
        return nombre;
    }
}


package estaticos;

import variables.Cuenta;
import variables.Servidor;
import java.io.PrintWriter;

/**
 * Gestor de salida con logs de depuración.
 * LOGS: [OUT]
 */
public class GestorSalida {
    
    public static void enviar(PrintWriter out, String p) {
        if (out == null || p == null || p.isEmpty()) return;
        
        // LOG PARA IA Y HUMANO: Ver el paquete exacto que sale al cliente
        System.out.println("[OUT] >> " + p);
        
        out.print(p + (char) 0x00);
        out.flush();
    }
    
    // ==================== PAQUETES DE RED ====================
    
    public static void ENVIAR_POLICY_FILE(PrintWriter out) {
        enviar(out, "<?xml version=\"1.0\"?><cross-domain-policy><allow-access-from domain=\"*\" to-ports=\"*\" /></cross-domain-policy>");
    }
    
    public static String ENVIAR_HC_CODIGO_LLAVE(PrintWriter out) {
        String llave = Encriptador.generarCodigoLlave();
        enviar(out, "HC" + llave);
        return llave;
    }
    
    // ==================== PAQUETES DE ERROR ====================
    
    public static void ENVIAR_AlEv_ERROR_VERSION(PrintWriter out) { enviar(out, "AlEv" + MainMultiLogin.VERSION_CLIENTE.trim()); }
    public static void ENVIAR_AlEp_CUENTA_NO_VALIDA(PrintWriter out) { enviar(out, "AlEp"); }
    public static void ENVIAR_AlEx_PASS_INCORRECTA(PrintWriter out) { enviar(out, "AlEx"); }
    public static void ENVIAR_AlEb_CUENTA_BANEADA(PrintWriter out) { enviar(out, "AlEb"); }
    public static void ENVIAR_AlEd_YA_CONECTADO(PrintWriter out) { enviar(out, "AlEd"); }
    public static void ENVIAR_AlEn_SERVIDOR_NO_DISPONIBLE(PrintWriter out) { enviar(out, "AlEn"); }

    // ==================== FLUJO DOFUS 1.43.7 ====================
    
    public static void ENVIAR_Al_LOGIN_EXITOSO(PrintWriter out) { enviar(out, "Al"); }
    public static void ENVIAR_Ad_APODO(PrintWriter out, String apodo) { enviar(out, "Ad" + apodo); }
    public static void ENVIAR_Ac_COMUNIDAD(PrintWriter out, int com) { enviar(out, "Ac" + com); }

    /**
     * Paquete AH: Lista dinámica.
     * LOG: Debe mostrar AH seguido de id;estado;pob;com
     */
    public static void ENVIAR_AH_LISTA_SERVIDORES(PrintWriter out) {
        String datos = Mundo.strParaAH();
        
        if (datos.isEmpty()) {
            // Si no hay servidores por SYNC, el cliente Electron fallará.
            // Opcional: Podrías mandar un servidor 11 por defecto como Offline.
            System.out.println("[OUT-WARN] Enviando AH vacío (No hay GameServers conectados)");
        }
        
        enviar(out, "AH" + datos);
    }
    
    /**
     * Paquete AxK: Envía la lista de personajes por servidor.
     * REGLA 1.43.7: Solo 2 campos por servidor (ID;Personajes)
     */
    public static void ENVIAR_AxK_LISTA_SERVIDORES(PrintWriter out, Cuenta cuenta) {
        // subscription = 0 (No abonado por ahora para pruebas)
        StringBuilder sb = new StringBuilder("AxK0|"); 
        boolean primero = true;
        
        for (Servidor s : Mundo.getServidores().values()) {
            if (s.getEstado() == 0) continue; 
            
            if (!primero) sb.append("|"); // Separador entre servidores
            
            int cantPerso = (cuenta != null) ? cuenta.getPersonajes(s.getId()) : 0;
            
            // FORMATO ESTRICTO: ID;PERSONAJES (Solo estos 2 campos)
            sb.append(s.getId()).append(";");
            sb.append(cantPerso);
            
            primero = false;
        }
        
        System.out.println("[DEBUG-AxK] Enviando formato 2 campos: " + sb.toString());
        enviar(out, sb.toString());
    }

    /**
     * Paquete AXK: Redirección al GameServer.
     * Formato: AXK ip | puerto | ticket
     */
    public static void ENVIAR_AXK_CONECTAR_SERVIDOR(PrintWriter out, int cuentaId, String ip, int puerto) {
        // Formato AYK para que el cliente Electron entre por el bloque 'else' (texto plano)
        // AYK IP : PUERTO ; TICKET
        String ticket = String.valueOf(cuentaId);
        String packet = "AYK" + ip + ":" + puerto + ";" + ticket;
        
        System.out.println("[DEBUG-AYK] Enviando salto al GS: " + packet);
        enviar(out, packet);
    }
    
    public static void ENVIAR_Af_SU_TURNO(PrintWriter out) { enviar(out, "Af"); }
    public static void ENVIAR_AQ_PREGUNTA_SECRETA(PrintWriter out, String pre) { enviar(out, "AQ" + (pre == null ? "" : pre)); }
}
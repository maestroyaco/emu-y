package estaticos;

import java.security.MessageDigest;
import java.util.Random;

/**
 * Clase para encriptar contraseñas y generar códigos
 */
public class Encriptador {
    
    private static final char[] CARACTERES_HASH = {
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
        'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
        'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_'
    };
    
    private static final Random random = new Random();
    
    /**
     * Genera un código llave aleatorio para la encriptación
     */
    public static String generarCodigoLlave() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            sb.append(CARACTERES_HASH[random.nextInt(CARACTERES_HASH.length)]);
        }
        return sb.toString();
    }
    
    /**
     * Encripta una contraseña usando el código llave
     * Este es el algoritmo usado por el cliente Dofus 1.29 y 1.43.7
     */
    public static String encriptarContrasena(String codigoLlave, String contrasena) {
        if (contrasena == null || contrasena.isEmpty()) {
            return "";
        }
        
        StringBuilder encriptada = new StringBuilder("#1");
        for (int i = 0; i < contrasena.length(); i++) {
            char passChar = contrasena.charAt(i);
            char keyChar = codigoLlave.charAt(i % codigoLlave.length());
            
            int passVal = passChar / 16;
            int keyVal = passChar % 16;
            
            encriptada.append(CARACTERES_HASH[(passVal + keyChar) % CARACTERES_HASH.length]);
            encriptada.append(CARACTERES_HASH[(keyVal + keyChar) % CARACTERES_HASH.length]);
        }
        return encriptada.toString();
    }
    
    /**
     * Descifra una contraseña encriptada con formato #1[hash]
     * Algoritmo inverso de encriptarContrasena()
     * 
     * @param codigoLlave La clave de conexión usada para cifrar
     * @param hashEncriptado El hash en formato "#1[hash]" o solo "[hash]"
     * @return La contraseña en texto plano, o null si el formato es inválido
     */
    public static String desencriptarContrasena(String codigoLlave, String hashEncriptado) {
        if (hashEncriptado == null || hashEncriptado.length() < 3) {
            return null;
        }
        
        // Remover prefijo #1 si existe
        String hash = hashEncriptado;
        if (hash.startsWith("#1")) {
            hash = hash.substring(2);
        }
        
        // El hash debe tener longitud par (cada carácter de la contraseña genera 2 caracteres)
        if (hash.length() % 2 != 0) {
            return null;
        }
        
        StringBuilder password = new StringBuilder();
        
        // Procesar cada par de caracteres
        for (int i = 0; i < hash.length(); i += 2) {
            char char1 = hash.charAt(i);
            char char2 = hash.charAt(i + 1);
            
            // Encontrar índices en CARACTERES_HASH
            int index1 = -1, index2 = -1;
            for (int j = 0; j < CARACTERES_HASH.length; j++) {
                if (CARACTERES_HASH[j] == char1) {
                    index1 = j;
                }
                if (CARACTERES_HASH[j] == char2) {
                    index2 = j;
                }
            }
            
            if (index1 == -1 || index2 == -1) {
                return null; // Carácter inválido en el hash
            }
            
            // Obtener el carácter de la clave correspondiente
            int keyIndex = (i / 2) % codigoLlave.length();
            char PKey = codigoLlave.charAt(keyIndex);
            
            // Descifrar: ANB = (APass + PKey) % CARACTERES_HASH.length
            //            ANB2 = (AKey + PKey) % CARACTERES_HASH.length
            // Necesitamos encontrar APass y AKey en [0, 15] tales que:
            // (APass + PKey) % 64 = index1
            // (AKey + PKey) % 64 = index2
            
            // Intentar encontrar APass en [0, 15]
            int APass = -1;
            for (int a = 0; a < 16; a++) {
                if ((a + PKey) % CARACTERES_HASH.length == index1) {
                    APass = a;
                    break;
                }
            }
            
            // Intentar encontrar AKey en [0, 15]
            int AKey = -1;
            for (int a = 0; a < 16; a++) {
                if ((a + PKey) % CARACTERES_HASH.length == index2) {
                    AKey = a;
                    break;
                }
            }
            
            if (APass == -1 || AKey == -1) {
                return null; // No se pudo descifrar este carácter
            }
            
            // Reconstruir el carácter original: PPass = APass * 16 + AKey
            char PPass = (char) (APass * 16 + AKey);
            password.append(PPass);
        }
        
        return password.toString();
    }
    
    /**
     * Genera una palabra aleatoria
     */
    public static String palabraAleatoria(int longitud) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < longitud; i++) {
            sb.append(CARACTERES_HASH[random.nextInt(CARACTERES_HASH.length)]);
        }
        return sb.toString();
    }
    
    /**
     * Filtra caracteres especiales de un string
     */
    public static String filtrar(String texto) {
        if (texto == null) return "";
        return texto.replaceAll("[^a-zA-Z0-9_-]", "");
    }
    
    /**
     * Convierte a UTF-8
     */
    public static String aUTF(String texto) {
        try {
            return new String(texto.getBytes("UTF-8"), "UTF-8");
        } catch (Exception e) {
            return texto;
        }
    }
    
    /**
     * Genera hash MD5
     */
    public static String md5(String texto) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(texto.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : array) {
                sb.append(Integer.toHexString((b & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (Exception e) {
            return texto;
        }
    }
    
    /**
     * Encripta IP para el packet de conexión al servidor de juego
     */
    public static String encriptarIP(String ip) {
        StringBuilder sb = new StringBuilder();
        String[] partes = ip.split("\\.");
        for (String parte : partes) {
            int num = Integer.parseInt(parte);
            sb.append((char) (num / 16 + 'a'));
            sb.append((char) (num % 16 + 'a'));
        }
        return sb.toString();
    }
}


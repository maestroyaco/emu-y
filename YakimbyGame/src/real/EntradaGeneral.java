package real;

import java.io.BufferedInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import estaticos.CentroInfo;
import estaticos.Emu;
import estaticos.GestorSQL;
import estaticos.GestorSalida;
import estaticos.MundoDofus;
import servidor.ServidorPersonaje;
import variables.Cuenta;

public class EntradaGeneral implements Runnable {
	private String _codigoLlave;
	private int _packetNum = 0;
	private String _nombreCuenta;
	private String _idioma;
	private String _clave;
	private Cuenta _cuenta;
	private int pasos = 0;
	private BufferedInputStream _in;
	private Thread _thread;
	public PrintWriter _out;
	public Socket _socketCuenta;

	public EntradaGeneral(Socket socket) {
		try {
			_socketCuenta = socket;
			_in = new BufferedInputStream((_socketCuenta.getInputStream()));
			// _in = new BufferedReader(new
			// InputStreamReader(_socketCuenta.getInputStream(), Charset.forName("UTF8")));
			_out = new PrintWriter(_socketCuenta.getOutputStream(), false, StandardCharsets.UTF_8);
			_thread = new Thread(this);
			_thread.setDaemon(true);
			_thread.start();

		} catch (Exception e) {
			salir();
		} finally {
			if (_thread != null) {
				if (_thread.isAlive()) {
					_thread.interrupt();
				}
				_thread = null;
			}
		}
	}

	public void salir() {
		try {
			if (_cuenta != null) {
				if (_cuenta.getEntradaGeneral() != null) {
					_cuenta.setEntradaGeneral(null);
				}
				GestorSQL.SALVAR_CUENTA(_cuenta);
			}
			if (_socketCuenta != null) {
				if (!_socketCuenta.isClosed()) {
					_socketCuenta.close();
					_socketCuenta = null;
				}
			}
			try {
				if (_in != null) {
					_in.close();
					_in = null;
				}
				if (_out != null) {
					_out.close();
					_out = null;
				}
			} catch (Exception e) {
			}
			if (_thread != null) {
				if (_thread.isAlive()) {
					_thread.interrupt();
				}
				_thread = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {

			GestorSalida.ENVIAR_XML_POLICIA(_out);
			_codigoLlave = GestorSalida.ENVIAR_HC_CODIGO_LLAVE(_out);
			byte[] contents = new byte[1024];
			int bytesRead = 0;
			String packet;
			while ((bytesRead = _in.read(contents)) != -1) {
				packet = new String(contents, 0, bytesRead - 1).replace("\r", "\n").replace("\u0000", "\n");
				for (String pkt : packet.split("\n")) {
					_packetNum += 1;
					analizar_Packet_Real(pkt);
					if (_packetNum >= 6) {
						salir();
						break;
					}
				}
			}
		} catch (Exception e) {
			salir();
		} finally {
			salir();
		}
	}

	void analizar_Packet_Real(String packet) {
		// Después del login exitoso, procesar comandos por contenido en lugar de secuencial
		if (_cuenta != null && _cuenta.getEntradaGeneral() != null) {
			procesarComandoPostLogin(packet);
			return;
		}
		
		switch (_packetNum) {
		case 1:// Version
			// Limpiar espacios y caracteres especiales del packet recibido
			String packetLimpio = packet.trim();
			// Eliminar caracteres de control (null bytes, etc.) y espacios al final
			packetLimpio = packetLimpio.replaceAll("[\\x00-\\x1F]", "").trim();
			
			// El cliente 1.43.7 envía la versión con formato: "1.43.7|es" (versión|idioma)
			// Extraer solo la parte de la versión (antes del |)
			String versionRecibida = packetLimpio;
			if (packetLimpio.contains("|")) {
				versionRecibida = packetLimpio.split("\\|")[0].trim();
			}
			
			// Obtener versión esperada
			String versionEsperada = CentroInfo.CLIENT_VERSION.trim();
			
			// Verificar versión del cliente
			boolean versionValida = versionRecibida.equalsIgnoreCase(versionEsperada);
			
			if (!versionValida || pasos != 0) {
				GestorSalida.ENVIAR_AlEv_VERSION_DEL_CLIENTE(_out);
				try {
					salir();
					return;
				} catch (Exception e) {
				}
			}
			pasos++;
			break;
		case 2:// nombre de cuenta
			// El cliente 1.43.7 puede enviar "login\n#1[hash]" en un solo packet
			if (packet.contains("\n")) {
				// Formato combinado del cliente 1.43.7
				String[] partes = packet.split("\n", 2);
				if (partes.length >= 1) {
					// Extraer nombre de cuenta (puede venir solo o con idioma)
					String loginPart = partes[0];
					if (loginPart.contains(";")) {
						// Formato: nombre;idioma
						String[] loginParts = loginPart.split(";", 2);
						_nombreCuenta = loginParts[0];
						_idioma = loginParts.length > 1 ? loginParts[1].toLowerCase() : "es";
					} else {
						// Solo nombre, sin idioma
						_nombreCuenta = loginPart;
						_idioma = "es"; // Idioma por defecto
					}
					
					// Si hay contraseña en el mismo packet, procesarla
					if (partes.length >= 2 && partes[1].length() > 0) {
						_clave = partes[1];
						pasos = 3; // Saltar directamente a validación de contraseña
						
						// Validar contraseña
						boolean existe = false;
						if (Cuenta.cuentaLogin(_nombreCuenta, _clave, _codigoLlave)) {
							existe = true;
						}
						
						if (existe) {
							procesarLoginExitoso();
						} else {
							GestorSalida.ENVIAR_AlEf_LOGIN_ERROR(_out);
							salir();
						}
						return;
					}
				}
			} else {
				// Formato tradicional: solo nombre de cuenta
				if (packet.length() > 30 || pasos != 1) {
					try {
						salir();
						return;
					} catch (Exception e) {
					}
				}
				pasos++;
				try {
					_nombreCuenta = packet.split(";")[0];
					_idioma = packet.split(";")[1].toLowerCase();
				} catch (Exception e) {
					salir();
					return;
				}
			}
			break;
		case 3:// HashPass
			// Verificar que tenga formato #1 (puede venir sin prefijo en algunos casos)
			if (packet.length() >= 250 || pasos != 2) {
				try {
					salir();
					return;
				} catch (Exception e) {
				}
			}
			
			// Aceptar formato con o sin prefijo #1
			if (!packet.startsWith("#1") && packet.length() > 0) {
				// Intentar agregar prefijo si no lo tiene (compatibilidad)
				_clave = "#1" + packet;
			} else {
				_clave = packet;
			}
			
			boolean existe = false;
			if (Cuenta.cuentaLogin(_nombreCuenta, _clave, _codigoLlave)) {
				existe = true;
			}
			if (existe) {
				procesarLoginExitoso();
			} else {
				GestorSalida.ENVIAR_AlEf_LOGIN_ERROR(_out);
			}
			break;
		case 4:
			// Verificar si es un comando post-login en lugar de Af
			if (_cuenta != null && _cuenta.getEntradaGeneral() != null) {
				if (packet.startsWith("Ax") || packet.startsWith("AX") || packet.startsWith("AL") || packet.startsWith("Af")) {
					procesarComandoPostLogin(packet);
					return;
				}
			}
			
			try {
				if (_cuenta == null || _cuenta.getEntradaGeneral() == null) {
					return;
				}
				GestorSalida.ENVIAR_Af_ABONADOS_POSCOLA(_out, 1, 0, 0, "" + 1, -1);
			} catch (Exception e) {
				GestorSalida.ENVIAR_AlEc_MISMA_CUENTA_CONECTADA(_out);
			}
			break;
		case 5:
			// Verificar si es un comando post-login en lugar de AYK automático
			if (_cuenta != null && _cuenta.getEntradaGeneral() != null) {
				if (packet.startsWith("AX") && packet.length() > 2) {
					// Cliente está seleccionando servidor
					procesarComandoPostLogin(packet);
					return;
				}
			}
			
			if (_cuenta == null) {
				return;
			}
			ServidorPersonaje.addEsperandoCuenta(_cuenta);
			GestorSalida.ENVIAR_AXK_O_AYK_IP_SERVER(_out, _cuenta.getID());
			break;
		default:
			// Packet desconocido - posiblemente del cliente 1.43.7
			String ip = _socketCuenta != null ? _socketCuenta.getInetAddress().getHostAddress() : "unknown";
			String nombre = _cuenta != null ? _cuenta.getNombre() : "sin_cuenta";
			System.out.println("[MUNDO-LOGIN] >>> PACKET DESCONOCIDO <<<");
			System.out.println("[MUNDO-LOGIN] IP: " + ip + " | Usuario: " + nombre);
			System.out.println("[MUNDO-LOGIN] Packet: " + packet);
			System.out.println("[MUNDO-LOGIN] PacketNum: " + _packetNum);
			System.out.println("[MUNDO-LOGIN] Longitud: " + packet.length());
			System.out.println("[MUNDO-LOGIN] ========================================");
			salir();
			break;
		}
	}

	/**
	 * Procesa un login exitoso después de validar las credenciales
	 */
	private void procesarLoginExitoso() {
		_cuenta = MundoDofus.getCuentaPorNombre(_nombreCuenta);
		if (_cuenta.enLinea()) {
			if (_cuenta.getEntradaGeneral() != null) {
				_cuenta.getEntradaGeneral().salir();
			} else if (_cuenta.getEntradaPersonaje() != null) {
				_cuenta.getEntradaPersonaje().salir(true);
			} else {
				GestorSalida.ENVIAR_AlEc_MISMA_CUENTA_CONECTADA(_out);
				return;
			}
		}
		if (Emu.SOLOGM && _cuenta.getRango() < 1) {
			return;
		}
		if (_cuenta.estaBaneado()) {
			GestorSalida.ENVIAR_AlEb_CUENTA_BANEADA(_out);
			return;
		}
		String ip = _socketCuenta.getInetAddress().getHostAddress();
		if (CentroInfo.compararConIPBaneadas(ip)) {
			GestorSalida.ENVIAR_AlEb_CUENTA_BANEADA(_out);
			return;
		}
		_cuenta.setTempIP(ip);

		if (MundoDofus.cuentasIP(ip) >= Emu.MAX_MULTI_CUENTAS) {
			GestorSalida.REALM_SEND_TOO_MANY_PLAYER_ERROR(_out);
			return;
		}

		_cuenta.claveSeguridad = _codigoLlave;
		_cuenta.setEntradaGeneral(this);
		_cuenta.idioma = _idioma;
		GestorSalida.ENVIAR_Ad_Ac_AH_AlK_AQ_INFO_CUENTA_Y_SERVER(_out, _cuenta.getApodo(),
				(_cuenta.getRango() > 0 ? (1) : (0)), _cuenta.getPregunta());
	}

	public String get_hashKey() {
		return _codigoLlave;
	}

	public void setClave(String _hashKey) {
		_codigoLlave = _hashKey;
	}

	public void addPacket() {
		_packetNum++;
	}
	
	/**
	 * Procesa comandos después del login exitoso (Ax, AX, AL, etc.)
	 * El cliente 1.43.7 envía comandos por contenido, no secuencialmente
	 */
	private void procesarComandoPostLogin(String packet) {
		if (packet == null || packet.isEmpty()) {
			return;
		}
		
		String comando = packet.length() >= 2 ? packet.substring(0, 2) : packet;
		
		switch (comando) {
			case "Ax": // Solicitud de lista de servidores
				// El cliente 1.43.7 espera AxK con formato: [subscription]|[id,count;id2,count2;...]
				// Usar el método correcto que genera el formato esperado
				GestorSalida.ENVIAR_AxK_LISTA_SERVIDORES(_out, _cuenta);
				break;
				
			case "AX": // Selección de servidor
				// Extraer ID del servidor
				String serverIdStr = packet.length() > 2 ? packet.substring(2) : "";
				try {
					int serverId = Integer.parseInt(serverIdStr);
					if (serverId == Emu.SERVER_ID) {
						// Servidor válido, enviar AXK con datos de conexión
						ServidorPersonaje.addEsperandoCuenta(_cuenta);
						
						// Generar ticket
						long timestamp = System.currentTimeMillis();
						String ticket = _cuenta.getID() + "_" + timestamp + "_" + (int)(Math.random() * 10000);
						
						// Formato AXK: ip|port|ticket
						String axkConectar = "AXK" + Emu.IP_PC_SERVER + "|" + Emu.PUERTO_JUEGO + "|" + ticket;
						GestorSalida.enviar(_out, axkConectar);
					} else {
						// Servidor no válido
						GestorSalida.ENVIAR_AlEn_SERVIDOR_NO_DISPONIBLE(_out);
					}
				} catch (NumberFormatException e) {
					GestorSalida.ENVIAR_AlEn_SERVIDOR_NO_DISPONIBLE(_out);
				}
				break;
				
			case "Af": // Solicitud de posición en cola
				try {
					if (_cuenta.getEntradaGeneral() == null) {
						return;
					}
					GestorSalida.ENVIAR_Af_ABONADOS_POSCOLA(_out, 1, 0, 0, "" + 1, -1);
				} catch (Exception e) {
					GestorSalida.ENVIAR_AlEc_MISMA_CUENTA_CONECTADA(_out);
				}
				break;
				
			case "AL": // Solicitud de lista de personajes
				// Enviar lista de personajes
				GestorSalida.ENVIAR_ALK_LISTA_DE_PERSONAJES(_out, _cuenta.getPersonajes());
				break;
				
			default:
				System.out.println("[MUNDO-LOGIN] Comando post-login desconocido: " + comando + " (packet: " + packet + ")");
				break;
		}
	}

}
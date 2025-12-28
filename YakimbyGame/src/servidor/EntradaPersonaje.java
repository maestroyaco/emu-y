package servidor;

import java.io.BufferedInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Map.Entry;

import estaticos.CentroInfo;
import estaticos.Comandos;
import estaticos.Emu;
import estaticos.Encriptador;
import estaticos.GestorSQL;
import estaticos.GestorSalida;
import estaticos.MundoDofus;
import variables.Casa;
import variables.Cuenta;
import variables.Idiomas;
import variables.Mapa;
import variables.Pelea;
import variables.Personaje;

public class EntradaPersonaje implements Runnable {
	private Comandos _comando;
	private Cuenta _cuenta;
	private Personaje _perso;
	private int ddosnr = 0;
	private String lastpacket = "";
	private BufferedInputStream _in;
	public Thread _thread;
	private PrintWriter _out;
	private Socket _socket;

	// Variables para 1.43.7e (Modern)
	private String _llaveSesion = ""; 
	private boolean _cifradoActivo = false;

	public EntradaPersonaje(Socket socket) {
		try {
			_socket = socket;
			_in = new BufferedInputStream((_socket.getInputStream()));
			_out = new PrintWriter(_socket.getOutputStream(), false, Charset.forName("UTF-8"));
			
			// HG es el primer paquete que espera el cliente Modern
			System.out.println("[GS-AUTH] Enviando HG a " + _socket.getInetAddress().getHostAddress());
			_out.print("HG" + (char) 0x00);
			_out.flush();

			_thread = new Thread(this);
			_thread.setDaemon(true);
			_thread.start();
		} catch (Exception e) {
			salir(false);
		}
	}

	@Override
	public void run() {
		try {
			byte[] contents = new byte[2048];
			int bytesRead = 0;
			String packet;
			while ((bytesRead = _in.read(contents)) != -1) {
				packet = new String(contents, 0, bytesRead, Charset.forName("UTF-8")).replace("\u0000", "\n");
				
				for (String pkt : packet.split("\n")) {
					if (pkt.isEmpty()) continue;
					analizar_Packets(pkt, true);
				}
			}
		} catch (Exception e) {
			System.out.println("[GS-ERR] Conexion cerrada.");
		} finally {
			salir(false);
		}
	}

public void analizar_Packets(String packet, boolean original) {
    if (packet == null || packet.isEmpty()) return;

    // =============================================================
    // 1. DESENCRIPTACIÓN PARA CLIENTE MODERN (ù = ASCII 249)
    // =============================================================
    if (packet.charAt(0) == (char) 249) { 
        try {
            // El cliente Modern envuelve el paquete: [ù][KeyIndex][Checksum][DATOS_XOR][ù]
            String checksumHex = packet.substring(2, 3);
            int offset = Integer.parseInt(checksumHex, 16) * 2;
            
            // Buscamos el segundo marcador ù que cierra el paquete
            int lastMarker = packet.lastIndexOf((char) 249);
            if (lastMarker > 3) {
                // Extraemos solo lo que está en medio (los datos cifrados)
                String cipheredData = packet.substring(3, lastMarker);
                packet = Encriptador.decypherData(cipheredData, _llaveSesion, offset);
                
                if (Emu.MOSTRAR_RECIBIDOS) {
                    System.out.println("[GS-DECRYPT] >> " + packet);
                }
            }
        } catch (Exception e) {
            return; 
        }
    }

    // Captura de activación de cifrado
    if (packet.startsWith("Ak")) {
        _cifradoActivo = true;
    }

    // =============================================================
    // 2. SEGURIDAD Y PROTECCIÓN (Legacy)
    // =============================================================
    if (_perso != null) {
        if (!_perso.is_primerentro()) _perso.set_primerentro(true);
        if (_perso.cambiarNombre()) {
            Estaticos.cambiarNombre(packet, _perso, _out);
            return;
        }
        // Anti-Flood
        if (_perso.getPelea() == null && _perso.getHaciendoTrabajo() == null && _perso.getIntercambio() == null) {
            if (packet.equalsIgnoreCase(lastpacket)) {
                if (ddosnr >= 10) { salir(false); return; }
                ddosnr += 1;
            } else { ddosnr = 0; }
        }
    } else {
        if (packet.startsWith("A")) {
            if (!ServidorPersonaje._clientes.contains(this)) ServidorPersonaje._clientes.add(this);
        } else {
            salir(false); return;
        }
    }

    if (packet.length() > 1000) return;
    lastpacket = packet;

    // =============================================================
    // 3. SWITCH DE COMANDOS (Refactorizado para carga de Mapa)
    // =============================================================
    char estb = packet.charAt(0);
    switch (estb) {
        case 'A': analizar_Cuenta(packet); break;
        case 'B': Estaticos.analizar_Basicos(packet, _perso, _out, _cuenta); break;
        
        case 'G': // JUEGO (Aquí es donde cargamos el mapa)
            analizar_Juego_Modern(packet); 
            break;

        case 'M': procesarComandoM(packet); break;
        case 'L': procesarComandoL(packet); break;
        case 'c': Estaticos.analizar_Canal(packet, _perso); break;
        case 'D': Estaticos.analizar_Dialogos(packet, _perso, _out); break;
        case 'E': Estaticos.analizar_Intercambios(packet, _perso, _out, _cuenta); break;
        case 'f': Estaticos.analizar_Peleas(packet, _perso, _out); break;
        case 'g': Estaticos.analizar_Gremio(packet, _perso, _out, _cuenta); break;
        case 'h': Estaticos.analizar_Casas(packet, _perso, _out); break;
        case 'i': Estaticos.analizar_Enemigos(packet, _perso, _out, _cuenta); break;
        case 'O': Estaticos.analizar_Objetos(packet, _perso, _out, _cuenta); break;
        case 'P': Estaticos.analizar_Grupo(packet, _perso, _out, _cuenta); break;
        case 'S': Estaticos.analizar_Hechizos(packet, _perso, _out); break;
        case 'R': Estaticos.analizar_Montura(packet, _perso, _out, _cuenta); break;
        case 'W': Estaticos.analizar_Areas(packet, _perso, _out); break;
        case 'p': if (packet.equals("ping")) GestorSalida.ENVIAR_pong(_out); break;
        case 'q': if (packet.equals("qping")) GestorSalida.ENVIAR_qpong(_out); break;
        case 'z': case 'Z': Estaticos.analizar_Zonas(packet, _perso); break;
        default: break;
    }
}
	private void analizar_Cuenta(String packet) {
		switch (packet.charAt(1)) {
		case 'T': // Ticket Auth
			try {
				String ticket = packet.substring(2); 
				int idCuenta = Integer.parseInt(ticket);
				_cuenta = MundoDofus.getCuenta(idCuenta);
				if (_cuenta == null) _cuenta = GestorSQL.cargarCompteID(idCuenta); 

				if (_cuenta != null) {
					_cuenta.setEntradaPersonaje(this);
					_out.print("ATK0" + (char) 0x00);
					// MODERN: Generar llave XOR
					_llaveSesion = Encriptador.generarLlaveHex(32);
					_out.print("AK0" + _llaveSesion + (char) 0x00);
					_out.flush();
				} else {
					_out.print("ATE" + (char) 0x00);
					_out.flush();
				}
			} catch (Exception e) {
				_out.print("ATE" + (char) 0x00);
				_out.flush();
			}
			break;
		case 'V':
			GestorSalida.ENVIAR_AV_VERSION_REGIONAL(_out);
			break;
		case 'L': // AL (Lista personajes)
			GestorSalida.ENVIAR_ALK_LISTA_DE_PERSONAJES(_out, _cuenta.getPersonajes());
			break;
		case 'S': // AS (Selección personaje)
			try {
				int idPerso = Integer.parseInt(packet.substring(2));
				if (_cuenta.getPersonajes().get(idPerso) != null) {
					if (_cuenta.getEntradaGeneral() != null) _cuenta.getEntradaGeneral().salir();
					_cuenta.setEntradaGeneral(null);
					_perso = _cuenta.getPersonajes().get(idPerso);
					_perso.primerRefresh = true;
					_perso.Conectarse();
					return;
				}
			} catch (Exception e) {}
			GestorSalida.ENVIAR_ASE_SELECCION_PERSONAJE_FALLIDA(_out);
			break;
		case 'A': Estaticos.cuenta_Crear_Personaje(packet, _out, _cuenta); break;
		case 'D': Estaticos.cuenta_Eliminar_Personaje(packet, _out, _cuenta); break;
		case 'g': Estaticos.listaRegalos(_out, _cuenta); break;
		case 'G': Estaticos.cuenta_Entregar_Regalo(packet.substring(2), _out, _cuenta); break;
		case 'i': _cuenta.setClaveCliente(packet.substring(2)); break;
		}
	}

	private void analizar_Juego_Modern(String packet) {
	    if (packet.length() < 2) {
	        Estaticos.analizar_Juego(packet, _perso, _out, _cuenta);
	        return;
	    }

	    switch (packet.charAt(1)) {
	        case 'C': // Recibe GC1
	            GestorSalida.enviar(_out, "GCK|1|" + _perso.getNombre());
	            break;

	        case 'I': // Recibe GI (El cliente pide los datos del mundo)
	        case 'i':
	            // PASO 1: Enviar los datos de las celdas (Indispensable para Electron)
	            // Esto le da al cliente el terreno, líneas de visión y celdas caminables
	            GestorSalida.enviar(_out, "GDM|" + _perso.getMapa().getMapData());

	            // PASO 2: Enviar los personajes y criaturas (GM)
	            String gms = _perso.getMapa().getGMsPackets(_perso);
	            if (!gms.isEmpty()) GestorSalida.enviar(_out, gms);

	            // PASO 3: Enviar objetos interactivos (GDF)
	            String gdf = _perso.getMapa().getObjectosGDF(_perso);
	            if (!gdf.isEmpty()) GestorSalida.enviar(_out, gdf);

	            // PASO 4: Enviar cantidad de peleas (fC)
	            GestorSalida.enviar(_out, "fC" + _perso.getMapa().getNumeroPeleas());
	            break;

	        default:
	            Estaticos.analizar_Juego(packet, _perso, _out, _cuenta);
	            break;
	    }
	}

	// Métodos Legacy de Soporte para EntradaPersonaje
	private void procesarComandoM(String packet) {
		if (_perso == null) return;
		if (!Estaticos.compruebaTps(_perso)) return;
		Mapa mapas = _perso.getMapa();
		Casa cacs = MundoDofus.getCasaDentroPorMapa(mapas.getID());
		if ((cacs == null || cacs.getDueÑoID() != _perso.getCuentaID()) && _cuenta.getRango() < 5) {
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, Idiomas.getTexto(_cuenta.idioma, 73), Colores.ROJO);
			return;
		}
		int ancho = _perso.getMapa().getAncho(); int alto = _perso.getMapa().getAlto();
		float pos1 = 0; float pos2 = 0;
		switch (alto) { case 10: pos2 = 81; break; case 13: pos2 = 40.5f; break; case 12: pos2 = 54; break; case 14: pos2 = 27; break; case 11: pos2 = 67.5f; break; case 15: pos2 = 13.5f; break; case 16: pos2 = 0; break; case 17: pos2 = -13.5f; break; case 18: pos2 = -27; break; case 22: pos2 = -67.5f; break; case 23: pos2 = -82; break; }
		switch (ancho) { case 8: pos1 = 159; break; case 9: pos1 = 132.5f; break; case 10: pos1 = 106; break; case 11: pos1 = 79.5f; break; case 12: pos1 = 53; break; case 13: pos1 = 26.5f; break; case 14: pos1 = 0; break; case 15: pos1 = -26.5f; break; case 16: pos1 = -53; break; case 19: pos1 = -93; break; }
		GestorSalida.enviar(_perso, "#bF0;" + pos1 + ";" + pos2 + ";" + Encriptador.getCeldasLimp(_perso.getMapa()));
	}

	private void procesarComandoL(String packet) {
		if (_perso == null) return;
		Mapa mapa = _perso.getMapa();
		Casa cac = MundoDofus.getCasaDentroPorMapa(mapa.getID());
		if ((cac == null || cac.getDueÑoID() != _perso.getCuentaID()) && _cuenta.getRango() < 5) return;
		mapa._limpieza.clear(); mapa._personaliza.clear();
		mapa.setMapData(mapa.getMapDataBack());
		for (Personaje px : mapa.getPersos()) { GestorSalida.enviar(px, "#bh"); px.teleport(mapa.getID(), px.getCelda().getID()); }
	}

	private void procesarComandoX(String packet) {
		if (_perso == null || _perso.buffClase != -1) return;
		try {
			int valor = Integer.parseInt(packet.substring(1));
			_perso.buffClase = valor;
			_perso.etapa += 1;
			GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
		} catch (Exception e) {}
	}

	public void salir(boolean nullea) {
		try {
			ServidorPersonaje.delGestorCliente(this);
			if (_cuenta != null && _cuenta.getEntradaPersonaje() != null) _cuenta.desconexion(nullea);
			if (_socket != null && !_socket.isClosed()) _socket.close();
			if (_in != null) _in.close();
			if (_out != null) _out.close();
			if (_thread != null) _thread.interrupt();
		} catch (Exception e) {}
	}

	// --- GETTERS LEGACY PARA COMPATIBILIDAD CON EL RESTO DEL CÓDIGO ---
	public Personaje getPersonaje() { return _perso; }
	public Cuenta getCuenta() { return _cuenta; }
	public PrintWriter getOut() { return _out; }
	public void set_comando(Comandos comando) { _comando = comando; }
	public Comandos get_comando() { return _comando; }

	// Inner class AccionDeJuego (RESTAURADA)
	public static class AccionDeJuego {
		public int _idUnica;
		public int _accionID;
		public String _packet;
		public String _args;

		public AccionDeJuego(int id, int accionId, String packet) {
			_idUnica = id;
			_accionID = accionId;
			_packet = packet;
		}
	}
}
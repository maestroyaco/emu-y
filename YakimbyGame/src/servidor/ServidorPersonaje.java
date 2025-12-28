package servidor;

import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import estaticos.CentroInfo;
import estaticos.Emu;
import real.TcpServer;
import real.TcpServer.Event;
import real.TcpServer.Listener;
import variables.Cuenta;

public class ServidorPersonaje {
	private static long _tiempoInicio;
	public static int _recordJugadores = 0;
	public static ArrayList<EntradaPersonaje> _clientes = new ArrayList<>();
	private static ArrayList<Cuenta> _esperando = new ArrayList<>();
	public static Map<String, Long> connections = new TreeMap<>();
	public static Map<String, Integer> ipsBan = new TreeMap<>();
	public static int _segundosON = 0;

	private TcpServer ts;

	public ServidorPersonaje() {
		try {
			ts = new TcpServer(Emu.PUERTO_JUEGO);
			ts.addTcpServerListener(new Listener() {
				@Override
				public void socketReceived(Event evt) {
					
					try {
					
						Socket socket = evt.getSocket();
						socket.setTcpNoDelay(true);
						String ip = socket.getInetAddress().getHostAddress();
						// ESTE LOG ES VITAL: Si no ves esto en consola, el cliente no está llegando al puerto.
				        System.out.println("[GS] << NUEVA CONEXIÓN DETECTADA DESDE: " + ip);
						if (CentroInfo.compararConIPBaneadas(ip)) {
				            System.out.println("[GS] >> IP Baneada: " + ip);
				            socket.close();				            
							return;
						}
						long time = System.currentTimeMillis();
						if (Emu.SoloEntrante) {
							System.out.println("Game IP " + ip);
						}
						if (connections.containsKey(ip)) {
							long time2 = connections.get(ip);
							if (time - time2 > 100) {
								connections.remove(ip);
								connections.put(ip, time2);
							} else {
								if (ipsBan.containsKey(ip)) {
									int timeban = ipsBan.get(ip);
									if (timeban >= 10) {
										System.out.println("IP BANEADA " + ip);
										CentroInfo.agregarIP(ip);
										socket.close();
										return;
									} else {
										ipsBan.remove(ip);
										ipsBan.put(ip, timeban + 1);
									}
								} else {
									ipsBan.put(ip, 1);
								}
							}
						} else {
							connections.put(ip, time);
						}
						if (!Emu.entraOblig) {
							socket.close();
							return;
						}
						new EntradaPersonaje(socket);
					} catch (Exception ex) {
						ex.printStackTrace();
						System.out.println("[GS-ERR] Error al recibir socket: " + ex.getMessage());
					}
				}
			});
			ts.start();

		} catch (Exception e) {
			System.out.println("IOException: " + e.getMessage());
			System.exit(0);
		}
	}

	public void cerrarServidor() {
		ts.stop();
		ts = null;
		ArrayList<EntradaPersonaje> clientes = new ArrayList<>();
		clientes.addAll(_clientes);
		for (EntradaPersonaje EP : clientes) {
			if (EP == null) {
				continue;
			}
			EP.salir(true);
		}
	}

	public static void delGestorCliente(EntradaPersonaje entradaPersonaje) {
		if (_clientes.contains(entradaPersonaje)) {
			_clientes.remove(entradaPersonaje);
		}
	}

	public static synchronized void delEsperandoCuenta(Cuenta cuenta) {
		if (_esperando.contains(cuenta)) {
			_esperando.remove(cuenta);
		}
	}

	public static synchronized void addEsperandoCuenta(Cuenta cuenta) {
		if (!_esperando.contains(cuenta)) {
			_esperando.add(cuenta);
		}
	}

	public static synchronized Cuenta getEsperandoCuenta(int id) {
		for (Cuenta element : _esperando) {
			if ((element).getID() == id) {
				return element;
			}
		}
		return null;
	}

	public static ArrayList<EntradaPersonaje> getClientes() {
		return _clientes;
	}

	public static long getTiempoInicio() {
		return _tiempoInicio;
	}

	public static String getTiempoServer() {
		Date actDate = new Date();
		return "BT" + (actDate.getTime() + 3600000);
	}

	public static int nroJugadoresLinea() {
		return _clientes.size();
	}

	public static int getRecordJugadores() {
		if (_clientes.size() > _recordJugadores) {
			_recordJugadores = _clientes.size();
		}
		return _recordJugadores;
	}

	public static int getSegundosON() {
		return _segundosON;
	}

	public static String getFechaServer() {
		Date actDate = new Date();
		DateFormat fecha = new SimpleDateFormat("dd");
		String dia = Integer.parseInt(fecha.format(actDate)) + "";
		while (dia.length() < 2) {
			dia = "0" + dia;
		}
		fecha = new SimpleDateFormat("MM");
		String mes = (Integer.parseInt(fecha.format(actDate)) - 1) + "";
		while (mes.length() < 2) {
			mes = "0" + mes;
		}
		fecha = new SimpleDateFormat("yyyy");
		String aÑo = (Integer.parseInt(fecha.format(actDate)) - 1370) + "";
		return "BD" + aÑo + "|" + mes + "|" + dia;
	}
}
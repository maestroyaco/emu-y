package real;

import java.net.Socket;
import java.util.Map;
import java.util.TreeMap;

import estaticos.CentroInfo;
import estaticos.Emu;
import real.TcpServer.Event;
import real.TcpServer.Listener;

public class ServidorGeneral {
	public static Map<String, Long> connections = new TreeMap<>();
	public static Map<String, Integer> ipsBan = new TreeMap<>();
	private TcpServer ts;

	public ServidorGeneral() {
		try {
			ts = new TcpServer(Emu.PUERTO_SERVIDOR);
			ts.addTcpServerListener(new Listener() {
				@Override
				public void socketReceived(Event evt) {
					try {
						Socket socket = evt.getSocket();
						socket.setTcpNoDelay(true);
						String ip = socket.getInetAddress().getHostName();
						if (Emu.SoloEntrante) {
							System.out.println("Login IP " + ip);
						}
						if (CentroInfo.compararConIPBaneadas(ip)) {
							socket.close();
							return;
						}
						long time = System.currentTimeMillis();
						if (connections.containsKey(ip)) {
							long time2 = connections.get(ip);
							if (time - time2 > 100) {
								connections.remove(ip);
								connections.put(ip, time2);
							} else {
								if (ipsBan.containsKey(ip)) {
									int timeban = ipsBan.get(ip);
									if (timeban >= 3) {
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
						new EntradaGeneral(socket);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			});
			ts.start();
		} catch (Exception e) {
			System.out.println("IOException: " + e.getMessage());
			System.exit(0);
		}
	}

	public void cerrarServidorGeneral() {
		try {
			ts.stop();
			ts = null;
		} catch (Exception e) {
		}
	}
}
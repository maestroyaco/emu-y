package variables;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import estaticos.CentroInfo;
import estaticos.Emu;
import estaticos.GestorSQL;
import estaticos.GestorSalida;
import estaticos.MundoDofus;
import servidor.Colores;

public class Casa {
	private int _id;
	private short _mapaIDFuera;
	private int _celdaIDFuera;
	private int _dueÑoID;
	private int _precioVenta;
	private int _gremioID;
	private int _derechosGremio;
	private int _acceso;
	private String _clave;
	private int _mapaIDDentro;
	private int _celdaIDDentro;
	private Map<Integer, Boolean> _tieneDerecho = new TreeMap<>();
	private ArrayList<Integer> _mapasContenidos = new ArrayList<>();
	public String _impuesto = "";

	public Casa(int id, short mapaIDFuera, int celdaIDFuera, int dueÑo, int precio, int gremioID, int acceso,
			String key, int derechosGremio, int mapaIDDentro, int celdaIDDentro, String mapasContenidos,
			String impuesto) {
		_id = id;
		_mapaIDFuera = mapaIDFuera;
		_celdaIDFuera = celdaIDFuera;
		_dueÑoID = dueÑo;
		_precioVenta = precio;
		_gremioID = gremioID;
		_acceso = acceso;
		_clave = key;
		_derechosGremio = derechosGremio;
		analizarDerechos(derechosGremio);
		_mapaIDDentro = mapaIDDentro;
		_celdaIDDentro = celdaIDDentro;
		for (String str : mapasContenidos.split(";")) {
			try {
				_mapasContenidos.add(Integer.parseInt(str));
			} catch (Exception localException) {
			}
		}
		_impuesto = impuesto;
		if (_impuesto.equals("")) {
			LocalDateTime today = LocalDateTime.now();
			LocalDateTime tomorrow = today.plusDays(7);
			String fechapagado = tomorrow.getYear() + "~" + tomorrow.getMonthValue() + "~" + tomorrow.getDayOfMonth()
					+ "~" + tomorrow.getHour() + "~" + tomorrow.getMinute();
			_impuesto = fechapagado;
			GestorSQL.ACTUALIZAR_CASA(this);
		}
		compruebaDia(null);
	}

	void compruebaDia(Personaje perso) {
		String[] dt = _impuesto.split("~");
		String dateStart = dt[1] + "/" + dt[2] + "/" + dt[0] + " " + dt[3] + ":" + dt[4] + ":00";
		DateFormat formatTime = new SimpleDateFormat("yyyy~MM~dd~HH~mm");
		Calendar calendar = Calendar.getInstance();
		String newfec[] = formatTime.format(calendar.getTime()).split("~");
		String dateStop = newfec[1] + "/" + newfec[2] + "/" + newfec[0] + " " + newfec[3] + ":" + newfec[4] + ":00";
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		Date d1 = null;
		Date d2 = null;
		try {
			d1 = format.parse(dateStart);
			d2 = format.parse(dateStop);
			long diff = d2.getTime() - d1.getTime();
			long diffDays = diff / (24 * 60 * 60 * 1000);
			if (perso != null) {
				if (diffDays == 0) {
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso,
							"<b>Hoy es tu Último d�a</b> para pagar el impuesto de tu casa o la perder�s, v� al banco y p�galo.",
							Colores.ROJO);
				}
			}
			if (diffDays < 0) {
				if (perso != null) {
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "Recuerda que en <b>" + (-diffDays)
							+ "</b> d�as tendrÁs que pagar el impuesto por tu casa.", Colores.NARANJA);
				}
			} else if (diffDays > 0) {
				if (perso != null) {
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "Has perdido tu casa por no pagar los impuestos.",
							Colores.ROJO);
				}
				perso.setCasa(null);
				resetear();
				_impuesto = "";
				GestorSQL.ACTUALIZAR_CASA(this);
			}
		} catch (Exception e) {
		}
	}

	void pagar(Personaje perso) {
		int mispuntos = GestorSQL.getPuntosCuenta(perso.getCuentaID());
		if (mispuntos < 200) {
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "El impuesto de la Casa son 200 Puntos VIP", Colores.ROJO);
			return;
		} else {
			GestorSQL.setPuntoCuenta((mispuntos - 200), perso.getCuentaID());
			String[] dt = _impuesto.split("~");
			String dateStart = dt[1] + "/" + dt[2] + "/" + dt[0] + " " + dt[3] + ":" + dt[4] + ":00";
			DateFormat formatTime = new SimpleDateFormat("yyyy~MM~dd~HH~mm");
			Calendar calendar = Calendar.getInstance();
			String newfec[] = formatTime.format(calendar.getTime()).split("~");
			String dateStop = newfec[1] + "/" + newfec[2] + "/" + newfec[0] + " " + newfec[3] + ":" + newfec[4] + ":00";
			SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			Date d1 = null;
			Date d2 = null;
			try {
				d1 = format.parse(dateStart);
				d2 = format.parse(dateStop);
				long diff = d2.getTime() - d1.getTime();
				long diffDays = diff / (24 * 60 * 60 * 1000);
				if (diffDays <= 0) {
					String lesuma = (-diffDays) + "";
					int suma = Integer.parseInt(lesuma);
					if (suma < 0) {
						suma = 0;
					}
					LocalDateTime today = LocalDateTime.now();
					LocalDateTime tomorrow = today.plusDays(7 + suma);
					String fechapagado = tomorrow.getYear() + "~" + tomorrow.getMonthValue() + "~"
							+ tomorrow.getDayOfMonth() + "~" + tomorrow.getHour() + "~" + tomorrow.getMinute();
					_impuesto = fechapagado;
					GestorSQL.ACTUALIZAR_CASA(this);
					GestorSalida
							.ENVIAR_cs_CHAT_MENSAJE(
									perso, "Has pagado con Éxito el impuesto de tu casa, vuelve dentro de <b>"
											+ (-diffDays) + "</b> d�as para pagar el siguiente impuesto.",
									Colores.VERDE);
				} else {
					LocalDateTime today = LocalDateTime.now();
					LocalDateTime tomorrow = today.plusDays(7);
					String fechapagado = tomorrow.getYear() + "~" + tomorrow.getMonthValue() + "~"
							+ tomorrow.getDayOfMonth() + "~" + tomorrow.getHour() + "~" + tomorrow.getMinute();
					_impuesto = fechapagado;
					GestorSQL.ACTUALIZAR_CASA(this);
					GestorSalida
							.ENVIAR_cs_CHAT_MENSAJE(
									perso, "Has pagado con Éxito el impuesto de tu casa, vuelve dentro de <b>"
											+ (-diffDays) + "</b> d�as para pagar el siguiente impuesto.",
									Colores.VERDE);
				}
			} catch (Exception e) {
			}
		}
	}

	public void resetear() {
		Mapa map = MundoDofus.getMapa(_mapaIDFuera);
		map._limpieza.clear();
		map._personaliza.clear();
		map.setMapData(map.getMapDataBack());
		if (!MundoDofus.mapasSalvar.contains(map.getID())) {
			MundoDofus.mapasSalvar.add(map.getID());
		}
		_dueÑoID = 0;
		_precioVenta = (int) 1000000L;
		_gremioID = 0;
		_acceso = 0;
		_clave = "-";
		_derechosGremio = 0;
		analizarDerechos(0);
	}

	public int getID() {
		return _id;
	}

	public ArrayList<Integer> getMapasContenidos() {
		return _mapasContenidos;
	}

	public short getMapaIDFuera() {
		return _mapaIDFuera;
	}

	public int getCeldaIDFuera() {
		return _celdaIDFuera;
	}

	public int getDueÑoID() {
		return _dueÑoID;
	}

	public void setDueÑoID(int id) {
		_dueÑoID = id;
	}

	public int getPrecioVenta() {
		return _precioVenta;
	}

	public void setPrecio(int precio) {
		_precioVenta = precio;
	}

	public int getGremioID() {
		return _gremioID;
	}

	public void setGremioID(int gremioID) {
		_gremioID = gremioID;
	}

	public int getDerechosGremio() {
		return _derechosGremio;
	}

	public void setDerechosGremio(int derechosGremio) {
		_derechosGremio = derechosGremio;
	}

	public int getAcceso() {
		return _acceso;
	}

	public void setAcceso(int accesso) {
		_acceso = accesso;
	}

	public String getClave() {
		if (_clave.length() > 10) {
			return "-";
		}
		return _clave;
	}

	public void setClave(String clave) {
		_clave = clave;
	}

	public int getMapaIDDentro() {
		return _mapaIDDentro;
	}

	public int getCeldaIDDentro() {
		return _celdaIDDentro;
	}

	static Casa getCasaPorUbicacion(int mapaID, int celdaID) {
		for (Entry<Integer, Casa> casa : MundoDofus.getCasas().entrySet()) {
			if (casa.getValue().getMapaIDFuera() == mapaID && casa.getValue().getCeldaIDFuera() == celdaID) {
				return casa.getValue();
			}
		}
		return null;
	}

	public static void cargarCasa(Personaje perso, int nuevoMapaID) {
		StringBuilder packet = new StringBuilder("");
		StringBuilder packet1 = new StringBuilder("");
		for (Entry<Integer, Casa> casa : MundoDofus.getCasas().entrySet()) {
			if (casa.getValue().getMapaIDFuera() == nuevoMapaID) {
				if (!packet.toString().equals("")) {
					packet.append((char) 0x00);
				}
				packet.append("hP" + casa.getValue().getID() + "|");
				if (casa.getValue().getDueÑoID() > 0) {
					Cuenta C = MundoDofus.getCuenta(casa.getValue().getDueÑoID());
					if (C == null) {
						packet.append("undefined;");
					} else {
						packet.append(MundoDofus.getCuenta(casa.getValue().getDueÑoID()).getApodo() + ";");
					}
				} else {
					packet.append(";");
				}
				if (casa.getValue().getPrecioVenta() > 0) {
					packet.append("1");
				} else {
					packet.append("0");
				}
				if (casa.getValue().getGremioID() > 0) {
					Gremio gremio = MundoDofus.getGremio(casa.getValue().getGremioID());
					if (gremio == null) {
						casa.getValue().setGremioID(0);
						return;
					}
					String nombreGremio = gremio.getNombre();
					String emblemaGremio = gremio.getEmblema();
					if (gremio.getPjMiembros().size() < 10) {
						GestorSQL.ACTUALIZAR_CASA_GREMIO(casa.getValue(), 0, 0);
					}
					if (perso.getGremio() != null && perso.getGremio().getID() == casa.getValue().getGremioID()
							&& casa.getValue().tieneDerecho(CentroInfo.H_GBLASON)
							&& gremio.getPjMiembros().size() > 9) {
						packet.append(";" + nombreGremio + ";" + emblemaGremio);
					} else if (casa.getValue().tieneDerecho(CentroInfo.H_OBLASON)
							&& gremio.getPjMiembros().size() > 9) {
						packet.append(";" + nombreGremio + ";" + emblemaGremio);
					}
				}
				if (casa.getValue().getDueÑoID() == perso.getCuentaID()) {
					if (!packet1.toString().equals("")) {
						packet1.append((char) 0x00);
					}
					packet1.append("L+|" + casa.getValue().getID() + ";" + casa.getValue().getAcceso() + ";");
					if (casa.getValue().getPrecioVenta() <= 0) {
						packet1.append("0;" + casa.getValue().getPrecioVenta());
					} else if (casa.getValue().getPrecioVenta() > 0) {
						packet1.append("1;" + casa.getValue().getPrecioVenta());
					}
				}
			}
		}
		GestorSalida.enviar(perso, packet.toString() + (char) 0x00 + packet1.toString());
	}

	void respondeA(Personaje perso) {
		if (perso.getPelea() != null || perso.getConversandoCon() != 0 || perso.getIntercambiandoCon() != 0
				|| perso.getHaciendoTrabajo() != null || perso.getIntercambio() != null) {
			return;
		}
		if (_dueÑoID == perso.getID()
				|| perso.getGremio() != null && perso.getGremio().getID() == _gremioID && tieneDerecho(8)) {
			abrirCasa(perso, "-", true);
		} else if (_dueÑoID > 0) {
			GestorSalida.ENVIAR_K_CLAVE(perso, "CK0|8");
		} else {
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "Abriendo puerta...", Colores.VERDE);
			// GestorSalida.ENVIAR_MUSICA_ID(perso, "9");
			abrirCasa(perso, "-", false);
		}
	}

	public static void abrirCasa(Personaje perso, String packet, boolean esHogar) {
		Casa casa = perso.getCasa();
		if (casa.esSuCasa(perso, casa)) {
			perso.teleport((short) casa.getMapaIDDentro(), casa.getCeldaIDDentro());
			cerrarVentana(perso);
			return;
		}
		if ((!casa.tieneDerecho(CentroInfo.H_ABRIRGREMIO) && (packet.compareTo(casa.getClave()) == 0)) || esHogar) {
			perso.teleport((short) casa.getMapaIDDentro(), casa.getCeldaIDDentro());
			cerrarVentana(perso);
		} else if ((packet.compareTo(casa.getClave()) != 0) || casa.tieneDerecho(CentroInfo.H_ABRIRGREMIO)) {
			GestorSalida.ENVIAR_K_CLAVE(perso, "KE");
			cerrarVentana(perso);
		}
	}

	public void comprarEstaCasa(Personaje perso) {
		Casa casa = perso.getCasa();
		if (casa == null) {
			return;
		}
		String str = "CK" + casa.getID() + "|" + casa.getPrecioVenta();
		GestorSalida.ENVIAR_h_CASA(perso, str);
	}

	public static void comprarCasa(Personaje perso) {
		Casa casa = perso.getCasa();
		if (tieneOtraCasa(perso)) {
			GestorSalida.ENVIAR_Im_INFORMACION(perso, "132;1");
			return;
		}
		if (perso.getKamas() < casa.getPrecioVenta()) {
			return;
		}
		long nuevasKamas = perso.getKamas() - casa.getPrecioVenta();
		perso.setKamas(nuevasKamas);
		int kamasCofre = 0;
		for (Cofre cofre : Cofre.getCofresPorCasa(casa)) {
			if (casa.getDueÑoID() > 0) {
				cofre.moverCofreABanco(MundoDofus.getCuenta(casa.getDueÑoID()));
			}
			kamasCofre += cofre.getKamas();
			cofre.setKamas(0);
			cofre.setClave("-");
			cofre.setDueÑoID(0);
			GestorSQL.ACTUALIZAR_COFRE(cofre);
		}
		if (casa.getDueÑoID() > 0) {
			Cuenta cuentaVendedor = MundoDofus.getCuenta(casa.getDueÑoID());
			if (cuentaVendedor == null) {
				GestorSQL.CARGAR_CUENTA_POR_ID(casa.getDueÑoID());
				cuentaVendedor = MundoDofus.getCuenta(casa.getDueÑoID());
			}
			long bancoKamas = cuentaVendedor.getKamasBanco() + casa.getPrecioVenta() + kamasCofre;
			cuentaVendedor.setKamasBanco(bancoKamas);
			Personaje vendedor = cuentaVendedor.getTempPersonaje();
			if (vendedor != null) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(vendedor,
						"Una casa ha sido vendida a " + casa.getPrecioVenta() + " kamas.", Emu.COLOR_MENSAJE);
				GestorSQL.SALVAR_PERSONAJE(vendedor, true);
			}
			GestorSQL.SALVAR_CUENTA(cuentaVendedor);
		}
		DateFormat formatTime = new SimpleDateFormat("yyyy~MM~dd~HH~mm");
		Calendar calendar = Calendar.getInstance();
		casa._impuesto = formatTime.format(calendar.getTime());
		casa._dueÑoID = perso.getID();
		if (perso.getGremio() != null) {
			casa._gremioID = perso.getGremio().getID();
		}
		GestorSQL.SALVAR_PERSONAJE(perso, true);
		GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso);
		GestorSQL.COMPRAR_CASA(perso, casa);
		cerrarVentanaCompra(perso);
		for (Personaje z : perso.getMapa().getPersos()) {
			cargarCasa(z, z.getMapa().getID());
		}
	}

	public void venderla(Personaje perso) {
		Casa casa = perso.getCasa();
		if (esSuCasa(perso, casa)) {
			String str = "CK" + casa.getID() + "|" + casa.getPrecioVenta();
			GestorSalida.ENVIAR_h_CASA(perso, str);
			return;
		} else {
			return;
		}
	}

	public static void precioVenta(Personaje perso, String packet) {
		Casa casa = perso.getCasa();
		int precio = Integer.parseInt(packet);
		if (casa.esSuCasa(perso, casa)) {
			GestorSalida.ENVIAR_h_CASA(perso, "V");
			GestorSalida.ENVIAR_h_CASA(perso, "SK" + casa.getID() + "|" + precio);
			GestorSQL.VENDER_CASA(casa, precio);
			for (Personaje z : perso.getMapa().getPersos()) {
				cargarCasa(z, z.getMapa().getID());
			}
			return;
		} else {
			return;
		}
	}

	private boolean esSuCasa(Personaje perso, Casa casa) {
		if (casa.getDueÑoID() == perso.getCuentaID()) {
			return true;
		} else {
			return false;
		}
	}

	public static void cerrarVentana(Personaje perso) {
		GestorSalida.ENVIAR_K_CLAVE(perso, "V");
	}

	public static void cerrarVentanaCompra(Personaje perso) {
		GestorSalida.ENVIAR_h_CASA(perso, "V");
	}

	public void bloquear(Personaje perso) {
		GestorSalida.ENVIAR_K_CLAVE(perso, "CK1|8");
	}

	public static void codificarCasa(Personaje perso, String packet) {
		Casa casa = perso.getCasa();
		if (casa.esSuCasa(perso, casa)) {
			casa.setClave(packet);
			if (packet.length() > 10) {
				return;
			}
			GestorSQL.CODIGO_CASA(perso, casa, packet);
			cerrarVentana(perso);
			return;
		} else {
			cerrarVentana(perso);
			return;
		}
	}

	public static String analizarCasaGremio(Personaje perso) {
		boolean primero = true;
		StringBuilder packet = new StringBuilder("+");
		for (Entry<Integer, Casa> entry : MundoDofus.getCasas().entrySet()) {
			Casa casa = entry.getValue();
			if (casa.getGremioID() == perso.getGremio().getID() && casa.getDerechosGremio() > 0) {
				if (!primero) {
					packet.append("|");
				}
				packet.append(entry.getKey() + ";");
				if (MundoDofus.getCuenta(casa.getDueÑoID()) == null) {
					GestorSQL.cargarCompteID(casa.getDueÑoID());
					if (MundoDofus.getCuenta(casa.getDueÑoID()) == null) {
						packet.append("DUE�O BUGEADO;");
					} else {
						packet.append(MundoDofus.getCuenta(casa.getDueÑoID()).getApodo() + ";");
					}
				} else {
					packet.append(MundoDofus.getCuenta(casa.getDueÑoID()).getApodo() + ";");
				}
				packet.append(MundoDofus.getMapa((short) casa.getMapaIDDentro()).getCoordX() + ","
						+ MundoDofus.getMapa((short) casa.getMapaIDDentro()).getCoordY() + ";");
				packet.append(";");
				packet.append(casa.getDerechosGremio());
				primero = false;
			}
		}
		return packet.toString();
	}

	static boolean tieneOtraCasa(Personaje perso) {
		for (Entry<Integer, Casa> casa : MundoDofus.getCasas().entrySet()) {
			if (casa.getValue().getDueÑoID() == perso.getCuentaID()) {
				return true;
			}
		}
		return false;
	}

	public static void analizarCasaGremio(Personaje perso, String packet) {
		Casa casa = perso.getCasa();
		if (perso.getGremio() == null) {
			GestorSalida.ENVIAR_BN_NADA(perso);
			return;
		}
		if (packet != null) {
			if (packet.charAt(0) == '+') {
				byte maxCasasPorGremio = (byte) Math.floor(perso.getGremio().getNivel() / 10);
				if ((casaGremio(perso.getGremio().getID()) >= maxCasasPorGremio) || (perso.getGremio().getPjMiembros().size() < 10)) {
					return;
				}
				GestorSQL.ACTUALIZAR_CASA_GREMIO(casa, perso.getGremio().getID(), 0);
				analizarCasaGremio(perso, null);
			} else if (packet.charAt(0) == '-') {
				GestorSQL.ACTUALIZAR_CASA_GREMIO(casa, 0, 0);
				analizarCasaGremio(perso, null);
			} else {
				GestorSQL.ACTUALIZAR_CASA_GREMIO(casa, casa.getGremioID(), Integer.parseInt(packet));
				casa.analizarDerechos(Integer.parseInt(packet));
			}
			GestorSalida.ENVIAR_BN_NADA(perso);
		} else if (packet == null) {
			if (casa.getGremioID() <= 0) {
				GestorSalida.ENVIAR_h_CASA(perso, "G" + casa.getID());
			} else if (casa.getGremioID() > 0) {
				GestorSalida.ENVIAR_h_CASA(perso, "G" + casa.getID() + ";" + perso.getGremio().getNombre() + ";"
						+ perso.getGremio().getEmblema() + ";" + casa.getDerechosGremio());
			}
		}
	}

	static byte casaGremio(int gremioID) {
		byte i = 0;
		for (Entry<Integer, Casa> casa : MundoDofus.getCasas().entrySet()) {
			if (casa.getValue().getGremioID() == gremioID) {
				i++;
			}
		}
		return i;
	}

	public boolean tieneDerecho(int derecho) {
		return _tieneDerecho.get(derecho);
	}

	private void iniciarDerechos() {
		_tieneDerecho.put(CentroInfo.H_GBLASON, false);
		_tieneDerecho.put(CentroInfo.H_OBLASON, false);
		_tieneDerecho.put(CentroInfo.H_SINCODIGOGREMIO, false);
		_tieneDerecho.put(CentroInfo.H_ABRIRGREMIO, false);
		_tieneDerecho.put(CentroInfo.C_SINCODIGOGREMIO, false);
		_tieneDerecho.put(CentroInfo.C_ABRIRGREMIO, false);
		_tieneDerecho.put(CentroInfo.H_DESCANSOGREMIO, false);
		_tieneDerecho.put(CentroInfo.H_TELEPORTGREMIO, false);
	}

	private void analizarDerechos(int total) {
		if (_tieneDerecho.isEmpty()) {
			iniciarDerechos();
		}
		if (total == 1) {
			return;
		}
		if (_tieneDerecho.size() > 0) {
			_tieneDerecho.clear();
		}
		iniciarDerechos();
		Integer[] mapKey = _tieneDerecho.keySet().toArray(new Integer[_tieneDerecho.size()]);
		while (total > 0) {
			for (int i = _tieneDerecho.size() - 1; i < _tieneDerecho.size(); i--) {
				if (mapKey[i].intValue() <= total) {
					total ^= mapKey[i].intValue();
					_tieneDerecho.put(mapKey[i], true);
					break;
				}
			}
		}
	}

	public static void salir(Personaje perso, String packet) {
		Casa casa = perso.getCasa();
		if (!casa.esSuCasa(perso, casa)) {
			return;
		}
		int Pid = Integer.parseInt(packet);
		Personaje objetivo = MundoDofus.getPersonaje(Pid);
		if (objetivo == null || !objetivo.enLinea() || objetivo.getPelea() != null
				|| objetivo.getMapa().getID() != perso.getMapa().getID()) {
			return;
		}
		objetivo.teleport(casa.getMapaIDFuera(), casa.getCeldaIDFuera());
		GestorSalida.ENVIAR_Im_INFORMACION(objetivo, "018;" + perso.getNombre());
	}

	static Casa getCasaDePj(Personaje perso) {
		try {
			for (Entry<Integer, Casa> entry : MundoDofus.getCasas().entrySet()) {
				Casa casa = entry.getValue();
				if (casa.getDueÑoID() == perso.getCuentaID()) {
					return casa;
				}
			}
		} catch (NullPointerException e) {
			return null;
		}
		return null;
	}

	public static void borrarCasaGremio(int gremioID) {
		for (Entry<Integer, Casa> entry : MundoDofus.getCasas().entrySet()) {
			Casa casa = entry.getValue();
			if (casa.getGremioID() == gremioID) {
				casa.setDerechosGremio(0);
				casa.setGremioID(0);
			} else {
				continue;
			}
		}
		GestorSQL.BORRAR_CASA_GREMIO(gremioID);
	}
}
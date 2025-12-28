package variables;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import estaticos.Emu;
import estaticos.Encriptador;
import estaticos.GestorSQL;
import estaticos.GestorSalida;
import estaticos.MundoDofus;
import real.EntradaGeneral;
import servidor.Colores;
import servidor.EntradaPersonaje;
import servidor.Estaticos;
import variables.Mercadillo.ObjetoMercadillo;

public class Cuenta {
	private int _ID;
	private String _nombre;
	private String _contraseña;
	String _apodo;
	private String _key;
	public String claveSeguridad = "";
	private String _ultimoIP = "";
	private String _pregunta;
	private String _respuesta;
	private boolean _baneado = false;
	private int _rango = 0;
	int _vip = 0;
	public int tiempopasado = 0;
	public int generadores = 0;
	private String _tempIP = "";
	private String _ultimaFechaConeccion = "";
	private Personaje _tempPerso;
	private long _kamasBanco = 0;
	private Map<Integer, Objeto> _objetosEnBanco = new TreeMap<>();
	private ArrayList<Integer> _idsAmigos = new ArrayList<>();
	private ArrayList<Integer> _idsEnemigos = new ArrayList<>();
	private ArrayList<Dragopavo> _establo = new ArrayList<>();
	private boolean _muteado = false;
	public long _tiempoMuteado;
	private int _primeraVez;
	private Map<Integer, ArrayList<ObjetoMercadillo>> _objMercadillos;// Contiene los items de HDV
	String tiempoLoot = "0";
	private Map<Integer, Personaje> _personajes = new TreeMap<>();
	private int _regalo;
	public ArrayList<Integer> tieneRegalo = new ArrayList<>();
	// public ArrayList<Integer> accionesCuenta = new ArrayList<Integer>();//13 es
	// el final del dragon
	private EntradaPersonaje _entradaPersonaje;
	private EntradaGeneral _entradaGeneral;
	public String idioma = "es";
	public long lastcheckRegalo = 0;

	public Cuenta(int ID, String nombre, String password, String apodo, String pregunta, String respuesta, int nivelGM,
			int vip, boolean baneado, String ultimaIP, String ultimaConeccion, String banco, int kamasBanco,
			String amigos, String enemigos, String establo, int primeravez, int regalo, String tiempoloo, int logged,
			String tieneregalos, int generad, int tiempogen, String tutoriales) {
		_ID = ID;
		_nombre = nombre;
		_contraseña = password;
		_apodo = apodo;
		_pregunta = pregunta;
		_respuesta = respuesta;
		_rango = nivelGM;
		tiempopasado = tiempogen;
		generadores = generad;
		_vip = vip;
		_baneado = baneado;
		_ultimoIP = ultimaIP;
		/*
		 * for (String reg : tutoriales.split(";")) { if (reg.equals("")) continue;
		 * accionesCuenta.add(Integer.parseInt(reg)); }
		 */
		for (String reg : tieneregalos.split(";")) {
			if (reg.equals("")) {
				continue;
			}
			tieneRegalo.add(Integer.parseInt(reg));
		}
		_ultimaFechaConeccion = ultimaConeccion;
		_kamasBanco = kamasBanco;
		_objMercadillos = MundoDofus.getMisObjetos(_ID);
		// cargando el banco
		for (String item : banco.split("\\|")) {
			if (item.equals("")) {
				continue;
			}
			String[] infos = item.split(":");
			int id = Integer.parseInt(infos[0]);
			Objeto obj = MundoDofus.getObjeto(id);
			if (obj == null) {
				continue;
			}
			_objetosEnBanco.put(obj.getID(), obj);
		}
		// Cargando lista de amigos
		for (String f : amigos.split(";")) {
			try {
				_idsAmigos.add(Integer.parseInt(f));
			} catch (Exception E) {
			}
		}
		// Cargando lista de enemigos
		for (String f : enemigos.split(";")) {
			try {
				_idsEnemigos.add(Integer.parseInt(f));
			} catch (Exception E) {
			}
		}
		for (String d : establo.split(";")) {
			try {
				Dragopavo DP = MundoDofus.getDragopavoPorID(Integer.parseInt(d));
				if (DP != null) {
					_establo.add(DP);
				}
			} catch (Exception E) {
			}
		}
		_primeraVez = primeravez;
		_regalo = regalo;
		if (tiempoloo.equals("0")) {
			Date date = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			Date expirationDate = cal.getTime();
			String exp = String.valueOf(expirationDate);
			tiempoloo = exp;
			tiempoLoot = exp;
			GestorSQL.setTiempoLootCuenta(exp, this.getID());
		} else {
			tiempoLoot = tiempoloo;
		}
		lastcheckRegalo = System.currentTimeMillis();
	}

	public String getTutoriales() {
		/*
		 * StringBuilder strx = new StringBuilder(); boolean prim = false; for (Integer
		 * str : accionesCuenta) { if (prim) strx.append(";"); strx.append(str); prim =
		 * true; } return strx.toString();
		 */
		return "";
	}

	public ArrayList<Dragopavo> getEstablo() {
		return _establo;
	}

	public int getPrimeraVez() {
		return _primeraVez;
	}

	public void setKamasBanco(long i) {
		_kamasBanco = i;
		GestorSQL.SALVAR_CUENTA(this);
	}

	boolean estaMuteado() {
		return _muteado;
	}

	public int getRegalo() {
		return _regalo;
	}

	public void setRegalo() {
		_regalo = 0;
	}

	public void setRegalo(int regalo) {
		_regalo = regalo;
	}

	public long _horaMuteada = 0;

	public void mutear(boolean b, int tiempo) {
		_muteado = b;
		String msg = "";
		if (_muteado) {
			msg = "Ha sido muteado";
		} else {
			msg = "Ha sido desmuteado";
		}
		GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_tempPerso, msg, Emu.COLOR_MENSAJE);
		if (tiempo == 0) {
			return;
		}
		_tiempoMuteado = tiempo * 1000;
		_horaMuteada = System.currentTimeMillis();
	}

	public String stringBancoObjetosBD() {
		StringBuilder str = new StringBuilder("");
		for (Entry<Integer, Objeto> entry : _objetosEnBanco.entrySet()) {
			Objeto obj = entry.getValue();
			str.append(obj.getID() + "|");
		}
		return str.toString();
	}

	public Map<Integer, Objeto> getBanco() {
		return _objetosEnBanco;
	}

	public long getKamasBanco() {
		return _kamasBanco;
	}

	public void setTempIP(String ip) {
		_tempIP = ip;
	}

	public String getUltimaConeccion() {
		return _ultimaFechaConeccion;
	}

	public void setUltimoIP(String ultimoIP) {
		_ultimoIP = ultimoIP;
	}

	public void setUltimaConeccion(String ultimaConeccion) {
		_ultimaFechaConeccion = ultimaConeccion;
	}

	public void setEntradaPersonaje(EntradaPersonaje t) {
		_entradaPersonaje = t;
	}

	public EntradaPersonaje getEntradaPersonaje() {
		return _entradaPersonaje;
	}

	public EntradaGeneral getEntradaGeneral() {
		return _entradaGeneral;
	}

	public void setEntradaGeneral(EntradaGeneral thread) {
		_entradaGeneral = thread;
	}

	public int getID() {
		return _ID;
	}

	public String getNombre() {
		return _nombre;
	}

	public String getContraseña() {
		return _contraseña;
	}

	public String getApodo() {
		if (_apodo.isEmpty() || _apodo == "") {
			_apodo = _nombre;
		}
		return _apodo;
	}

	public String getClaveCliente() {
		return _key;
	}

	public void setClaveCliente(String aKey) {
		_key = aKey;
	}

	public Map<Integer, Personaje> getPersonajes() {
		return _personajes;
	}

	public String getUltimoIP() {
		return _ultimoIP;
	}

	public String getPregunta() {
		return _pregunta;
	}

	public Personaje getTempPersonaje() {
		return _tempPerso;
	}

	public String getRespuesta() {
		return _respuesta;
	}

	public boolean estaBaneado() {
		return _baneado;
	}

	public void setBaneado(boolean baneado) {
		_baneado = baneado;
	}

	public boolean enLinea() {
		if (_entradaGeneral != null || _entradaPersonaje != null) {
			return true;
		}
		return false;
	}

	public int getRango() {
		return _rango;
	}

	public String getActualIP() {
		return _tempIP;
	}

	public void cambiarContraseña(String nueva) {
		_contraseña = nueva;
	}

	public int getNumeroPersonajes() {
		return _personajes.size();
	}

	public static boolean cuentaLogin(String nombre, String contraseña, String codigoLlave) {
		Cuenta cuenta = MundoDofus.getCuentaPorNombre(nombre);
		if (cuenta != null && cuenta.esContraseñaValida(contraseña, codigoLlave)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean esContraseñaValida(String contraseña, String codigoLlave) {
		// Si la contraseña recibida tiene formato #1, descifrarla y comparar con la almacenada
		if (contraseña != null && contraseña.startsWith("#1")) {
			String contraseñaDescifrada = Encriptador.desencriptarPassword(codigoLlave, contraseña);
			if (contraseñaDescifrada != null) {
				// Comparar contraseña descifrada con la almacenada
				return contraseñaDescifrada.equals(_contraseña);
			}
			// Si el descifrado falla, intentar método tradicional
		}
		
		// Método tradicional: comparar hash recibido con hash generado
		// Esto mantiene compatibilidad con clientes que envían hash directamente
		return contraseña.equalsIgnoreCase(Encriptador.encriptarPassword(codigoLlave, _contraseña));
	}

	public void addPerso(Personaje perso) {
		if (_personajes.containsKey(perso.getID())) {
			return;
		}
		_personajes.put(perso.getID(), perso);
	}

	public boolean crearPj(String nombre, int sexo, int clase, int color1, int color2, int color3) {
		Personaje perso = Personaje.crearPersonaje(nombre, sexo, clase, color1, color2, color3, this);
		if (perso == null) {
			return false;
		}
		_personajes.put(perso.getID(), perso);
		GestorSalida.ENVIAR_TB_CINEMA_INICIO_JUEGO(perso);
		return true;
	}

	public void borrarPerso(int id) {
		if (!_personajes.containsKey(id)) {
			return;
		}
		MundoDofus.eliminarPersonaje(_personajes.get(id));
		_personajes.remove(id);
	}

	public void setTempPerso(Personaje perso) {
		_tempPerso = perso;
	}

	public synchronized void desconexion(boolean nulea) {
		resetTodosPjs();
		if (!nulea) {
			_entradaPersonaje = null;
			_entradaGeneral = null;
		}
		GestorSQL.SALVAR_PERSONAJE(_tempPerso, true);
		_tempPerso = null;
		// _tempIP = "";
		GestorSQL.SALVAR_CUENTA(this);
	}

	private synchronized void resetTodosPjs() {
		try {
			for (Personaje perso : _personajes.values()) {
				if (!perso.is_primerentro()) {
					continue;
				} else {
					perso.set_primerentro(false);
				}
				if (perso.getIntercambio() != null) {
					perso.getIntercambio().cancel();
				}
				if (perso.getHaciendoTrabajo() != null) {
					perso.getHaciendoTrabajo()._ingredientes.clear();
					perso.getHaciendoTrabajo()._ultimoTrabajo.clear();
				}
				if (perso.generador != null) {
					if (perso.generador.isRunning()) {
						perso.generador.stop();
					}
					perso.generador = null;
				}
				perso._objTemporal = "";
				Pelea pelea = perso.getPelea();
				boolean borrame = false;
				perso.setEnLinea(false);
				if (pelea != null) {
					if (pelea.getEspectadores().containsKey(perso.getID())) {
						perso.salirEspectador(true);
						borrame = true;
					} else {
						if (pelea.getEstado() != 3 || (pelea.getTipoPelea() == 0 && !pelea.enKoliseo)) {
							pelea.retirarsePelea(perso, null, true);
							borrame = true;
						} else if (pelea.getEstado() == 3 && pelea.getTipoPelea() <= 4) { // si la pelea estÃ empezada
							// perso.getCelda().removerPersonaje(perso.getID());
							pelea.desconectarLuchador(perso);
							continue;
						} else {
							pelea.retirarsePelea(perso, null, true);
						}
					}
				} else if (perso.getMapa() != null) {
					GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(perso.getMapa(), perso.getID());
				}
				if (perso.celdadeparo != 0) {
					perso.celdadeparo = 0;
				}
				perso.abrepanelinter = false;
				if (perso.getKoliseo() != null) {
					perso.getKoliseo().abandonar(perso, false);
				}
				if (perso.enTorneo == 1) {
					Estaticos.perderTorneo(perso);
				}
				if (perso.getGrupo() != null) {
					perso.getGrupo().dejarGrupo(perso);
				}
				if (perso.getRompiendo()) {
					perso.setOcupado(false);
					perso.paramag.clear();
					perso.setRompiendo(false);
				}
				try {
					if (MundoDofus.misionesPvP.containsKey(perso.getID())) {
						int pobj = MundoDofus.misionesPvP.get(perso.getID());
						Personaje perj = MundoDofus.getPersonaje(pobj);
						if (perj != null && perj.enLinea()) {
							GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perj, "El personaje <b>" + perso.getNombre()
									+ "</b> acaba de desconectarse y se ha cancelado tu misiï¿½n de caza.  Se le ha penalizado con <b>-50</b> puntos de honor y tu has ganado <b>+50</b> puntos a cambio.",
									Colores.ROJO);
						}
						MundoDofus.misionesPvP.remove(perso.getID());
						perso.setHonor(perso.getHonor() - 50);
						if (perso.getHonor() < 0) {
							perso.setHonor(0);
						}
						perj.addHonor(50);
					} else if (MundoDofus.misionesPvP.containsValue(perso.getID())) {
						int keydelgan = 0;
						for (Entry<Integer, Integer> entry : MundoDofus.misionesPvP.entrySet()) {
							if (entry.getValue() == perso.getID()) {
								keydelgan = entry.getKey();
								break;
							}
						}
						if (keydelgan != 0) {
							Personaje perj = MundoDofus.getPersonaje(keydelgan);
							if (perj != null && perj.enLinea()) {
								GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perj,
										"Se ha cancelado tu misiï¿½n de caza porque el personaje <b>" + perso.getNombre()
												+ "</b> se ha desconectado. Se le ha penalizado con <b>-50</b> puntos de honor y tu has ganado <b>+50</b> puntos a cambio.",
										Colores.ROJO);
							}
							MundoDofus.misionesPvP.remove(keydelgan);
							perso.setHonor(perso.getHonor() - 50);
							if (perso.getHonor() < 0) {
								perso.setHonor(0);
							}
							perj.addHonor(50);
						}
					}
				} catch (Exception e) {
					System.out.println("Error en misionesPvP cuenta");
				}
				if (borrame) {
					GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(perso.getMapa(), perso.getID());
				}
				perso.getMapa().borrarJugador(perso);
				perso.resetVariables();
			}
		} catch (Exception e) {
			Emu.creaLogs(e);
		}
	}

	void mensajeAAmigos() {
		for (int i : _idsAmigos) {
			Personaje perso = MundoDofus.getPersonaje(i);
			if (perso != null && perso.mostrarConeccionAmigo() && perso.enLinea()) {
				GestorSalida.ENVIAR_Im0143_AMIGO_CONECTADO(_tempPerso, perso);
			}
		}
	}

	public void addAmigo(int id) {
		if (_ID == id) {
			GestorSalida.ENVIAR_FA_AGREGAR_AMIGO(_tempPerso, "Ey");
			return;
		}
		if (_idsEnemigos.contains(id)) {
			GestorSalida.ENVIAR_iA_AGREGAR_ENEMIGO(_tempPerso, "Ea");
			return;
		}
		if (!_idsAmigos.contains(id)) {
			_idsAmigos.add(id);
			Cuenta amigo = MundoDofus.getCuenta(id);
			GestorSalida.ENVIAR_FA_AGREGAR_AMIGO(_tempPerso,
					"K" + amigo.getApodo() + amigo.getTempPersonaje().analizarListaAmigos(_ID));
		} else {
			GestorSalida.ENVIAR_FA_AGREGAR_AMIGO(_tempPerso, "Ea");
		}
	}

	public void borrarAmigo(int id) {
		_idsAmigos.remove((Object) id);
		GestorSalida.ENVIAR_FD_BORRAR_AMIGO(_tempPerso, "K");
	}

	public boolean esAmigo(int id) {
		return _idsAmigos.contains(id);
	}

	public String analizarListaAmigosABD() {
		StringBuilder str = new StringBuilder("");
		for (int i : _idsAmigos) {
			if (!str.toString().equalsIgnoreCase("")) {
				str.append(";");
			}
			str.append(i + "");
		}
		return str.toString();
	}

	public String stringListaAmigos() {
		StringBuilder str = new StringBuilder("");
		for (int i : _idsAmigos) {
			Cuenta C = MundoDofus.getCuenta(i);
			if (C == null) {
				C = GestorSQL.cargarCompteID(i);
			}
			if (C == null) {
				continue;
			}
			str.append("|" + C.getApodo());
			if (!C.enLinea()) {
				continue;
			}
			Personaje P = C.getTempPersonaje();
			if (P == null) {
				continue;
			}
			str.append(P.analizarListaAmigos(_ID));
		}
		return str.toString();
	}

	public void addEnemigo(String packet, int id) {
		if (_ID == id) {
			GestorSalida.ENVIAR_iA_AGREGAR_ENEMIGO(_tempPerso, "Ey");
			return;
		}
		if (_idsAmigos.contains(id)) {
			GestorSalida.ENVIAR_FA_AGREGAR_AMIGO(_tempPerso, "Ea");
			return;
		}
		if (!_idsEnemigos.contains(id)) {
			_idsEnemigos.add(id);
			Cuenta amigo = MundoDofus.getCuenta(id);
			GestorSalida.ENVIAR_iA_AGREGAR_ENEMIGO(_tempPerso,
					"K" + amigo.getApodo() + amigo.getTempPersonaje().analizarListaEnemigos(_ID));
		} else {
			GestorSalida.ENVIAR_iA_AGREGAR_ENEMIGO(_tempPerso, "Ea");
		}
	}

	public void borrarEnemigo(int id) {
		_idsEnemigos.remove((Object) id);
		GestorSalida.ENVIAR_iD_BORRAR_ENEMIGO(_tempPerso, "K");
	}

	public boolean esEnemigo(int id) {
		return _idsEnemigos.contains(id);
	}

	public String stringListaEnemigosABD() {
		StringBuilder str = new StringBuilder("");
		for (int i : _idsEnemigos) {
			if (!str.toString().equalsIgnoreCase("")) {
				str.append(";");
			}
			str.append(i + "");
		}
		return str.toString();
	}

	public String stringListaEnemigos() {
		StringBuilder str = new StringBuilder("");
		for (int i : _idsEnemigos) {
			Cuenta C = MundoDofus.getCuenta(i);
			if (C == null) {
				C = GestorSQL.cargarCompteID(i);
			}
			if (C == null) {
				continue;
			}
			str.append("|" + C.getApodo());
			if (!C.enLinea()) {
				continue;
			}
			Personaje P = C.getTempPersonaje();
			if (P == null) {
				continue;
			}
			str.append(P.analizarListaEnemigos(_ID));
		}
		return str.toString();
	}

	public void setPrimeraVez(int valor) {
		_primeraVez = valor;
	}

	public synchronized String stringIDsEstablo() {
		StringBuilder str = new StringBuilder("");
		boolean primero = false;
		for (Dragopavo DD : _establo) {
			if (primero) {
				str.append(";");
			}
			str.append(DD.getID());
			primero = true;
		}
		return str.toString();
	}

	public int getVIP() {
		return _vip;
	}

	public boolean recuperarObjeto(int lineaID, int cantidad) {
		if ((_tempPerso == null) || (_tempPerso.getIntercambiandoCon() >= 0)) {
			return false;
		}
		int idPuestoMerca = Math.abs(_tempPerso.getIntercambiandoCon());
		ObjetoMercadillo objMerca = null;
		try {
			for (ObjetoMercadillo tempEntry : _objMercadillos.get(idPuestoMerca)) {
				if (tempEntry.getObjeto().getID() == lineaID || tempEntry.getLineaID() == lineaID) {
					objMerca = tempEntry;
				}
			}
		} catch (NullPointerException e) {
			return false;
		}
		try {
			if (objMerca == null) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_tempPerso, "El objeto ya no estÃ disponible", Colores.ROJO);
				getEntradaPersonaje().analizar_Packets("EV", false);
				return false;
			}
			Objeto obj = objMerca.getObjeto();
			if (_tempPerso.addObjetoSimilar(obj, true, -1)) {
				MundoDofus.eliminarObjeto(obj.getID());
			} else {
				_tempPerso.addObjetoPut(obj);
				GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_entradaPersonaje.getOut(), obj);
			}
			MundoDofus.getPuestoMerca(idPuestoMerca).borrarObjMercaDelPuesto(objMerca, _tempPerso);// retira el item de
																									// HDV
			_objMercadillos.get(idPuestoMerca).remove(objMerca);// Retira el item de la lista de objetos a vender en la
																// cuenta
			GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_entradaPersonaje.getOut(), '-', "", lineaID + "");
		} catch (Exception e) {
			Emu.creaLogs(e);
		}
		return true;
	}

	public ObjetoMercadillo[] getObjMercaDePuesto(int idPuestoMerca) {
		ObjetoMercadillo[] listaObjMercadillos = null;
		try {
			if (_objMercadillos.get(idPuestoMerca) == null) {
				return new ObjetoMercadillo[1];
			}
		} catch (Exception ex) {
			Emu.creaLogs(ex);
		}
		try {
			listaObjMercadillos = new ObjetoMercadillo[20];
		} catch (Exception ex) {
			Emu.creaLogs(ex);
		}
		try {
			for (int i = 0; i < _objMercadillos.get(idPuestoMerca).size(); i++) {
				ObjetoMercadillo elvalor = _objMercadillos.get(idPuestoMerca).get(i);
				if (elvalor == null) {
					continue;
				}
				listaObjMercadillos[i] = elvalor;
			}
		} catch (Exception e) {
			Emu.creaLogs(e);
		}
		return listaObjMercadillos;
	}

	public int cantidadObjMercadillo(int idPuestoMerca) {
		if (_objMercadillos.get(idPuestoMerca) == null) {
			return 0;
		}
		return _objMercadillos.get(idPuestoMerca).size();
	}
}

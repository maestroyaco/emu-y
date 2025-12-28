package variables;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.Timer;

import estaticos.Camino;
import estaticos.CentroInfo;
import estaticos.Emu;
import estaticos.Formulas;
import estaticos.GestorSQL;
import estaticos.GestorSalida;
import estaticos.MundoDofus;
import estaticos.MundoDofus.Duo;
import estaticos.MundoDofus.Intercambio;
import estaticos.MundoDofus.InvitarTaller;
import estaticos.MundoDofus.ItemSet;
import estaticos.MundoDofus.Trueque;
import servidor.Colores;
import servidor.EntradaPersonaje.AccionDeJuego;
import servidor.Estaticos;
import variables.Gremio.MiembroGremio;
import variables.Hechizo.StatsHechizos;
import variables.Mapa.Celda;
import variables.Mapa.Cercado;
import variables.Mapa.ObjetoInteractivo;
import variables.Objeto.ObjetoModelo;
import variables.Oficio.AccionTrabajo;
import variables.Oficio.StatsOficio;
import variables.Pelea.Luchador;

public class Personaje {




	private int _ID;
	private String _nombre;
	private int _sexo;
	private int _clase;
	private int _color1;
	private int _color2;
	private int _color3;
	private long _kamas;
	public int _puntosHechizo;
	private int _capital;
	private int _energia;
	private int _nivel;
	private long _experiencia;
	private int _nivelOmega = 0;
	private int _talla;
	private int _gfxID;
	private int _esMercante = 0;
	private int _orientacion = 1;
	public long tiempoantibug = 0;
	private Cuenta _cuenta;
	private int _cuentaID;
	private String _emotes = "7667711";
	private byte _alineacion = 0;
	private int _deshonor = 0;
	private int _honor = 0;
	private boolean _mostrarAlas = true;
	public boolean penaliza = false;
	private int _nivelAlineacion = 0;
	private MiembroGremio _miembroGremio;
	private boolean _mostrarConeccionAmigos;
	public boolean perfecciona = false;
	public boolean itemmision = false;
	public boolean torrepvm = false;
	public boolean elemento = false;
	public boolean pusoperfec = false;
	public boolean revivirMascota = false;
	public boolean pusomascota = false;
	public boolean pusopolvo = false;
	private String _canales;
	Stats _baseStats;
	private Pelea _pelea = null;
	private Mascota _mascota;
	private boolean _ocupado;
	private Mapa _mapa;
	Celda _celda;
	private int _PDV;
	private boolean _estarBanco;
	private int _PDVMAX;
	private boolean _sentado;
	private boolean _listo = false;
	private boolean _enLinea = false;
	private Grupo _grupo;
	private int _intercambioCon = 0;
	private Intercambio _intercambio;
	private InvitarTaller _tallerInvitado;
	private int _conversandoCon = 0;
	private int _invitando = 0;
	private int _dueloID = -1;
	public Map<Integer, StatsHechizos> _hechizos = new TreeMap<>();
	Map<Integer, Character> _lugaresHechizos = new TreeMap<>();
	private Map<Integer, Objeto> _objetos = new TreeMap<>();
	private ArrayList<Objeto> _tienda = new ArrayList<>();
	private Map<Integer, StatsOficio> _statsOficios = new TreeMap<>();
	private String _puntoSalvado;
	private Cercado _enCercado;
	private int _emoteActivado = 0;
	private AccionTrabajo _haciendoTrabajo;
	private Dragopavo _montura;
	private int _xpDonadaMontura = 0;
	private boolean _montando = false;
	public boolean _enZaaping = false;
	private ArrayList<Integer> _zaaps = new ArrayList<>();
	private boolean _ausente = false;
	private boolean _invisible = false;
	private Personaje _siguiendo = null;
	private boolean _olvidandoHechizo = false;
	private boolean _esDoble = false;
	private boolean _recaudando = false;
	private boolean _dragopaveando = false;
	private int _recaudandoRecaudadorID = 0;
	private int _titulo = 0;
	private int _esposo = 0;
	public int PuntosPrestigio = 0;
	private int _esOK = 0;
	private AccionDeJuego _taller = null;
	private Trueque _trueque = null;
	private ArrayList<Integer> _setClase = new ArrayList<>();
	private Map<Integer, Duo<Integer, Integer>> _hechizosSetClase = new TreeMap<>();
	private boolean _esFantasma = false;
	private Mapa _tempMapaDefPerco = null;
	private Celda _tempCeldaDefPerco = null;
	private Map<Integer, Personaje> _seguidores = new TreeMap<>();
	private Cofre _cofre;
	private Casa _casa;
	private int _scrollFuerza = 0;
	private int _scrollInteligencia = 0;
	private int _scrollAgilidad = 0;
	private int _scrollSuerte = 0;
	private int _scrollVitalidad = 0;
	private int _scrollSabiduria = 0;
	private boolean _oficioPublico = false;
	String _stringOficiosPublicos = "";
	private boolean _listaArtesanos = false;
	private int _bendHechizo = 0;
	private int _bendEfecto = 0;
	private int _bendModif = 0;
	private boolean _cambiarNombre = false;
	private int _restriccionesA;
	private int _restriccionesB;
	private boolean _puedeAgredir;
	private boolean _puedeDesafiar;
	private boolean _puedeIntercambiar;
	private boolean _puedeAtacarAMutante;
	private boolean _puedeChatATodos;
	private boolean _puedeMercante;
	private boolean _puedeUsarObjetos;
	private boolean _puedeInteractuarRecaudador;
	private boolean _puedeInteractuarObjetos;
	private boolean _puedeHablarNPC;
	private boolean _puedeAtacarMobsDungCuandoMutante;
	private boolean _puedeMoverTodasDirecciones;
	private boolean _puedeAtacarMobsCualquieraCuandoMutante;
	private boolean _puedeInteractuarPrisma;
	private boolean _puedeSerAgredido;
	private boolean _puedeSerDesafiado;
	private boolean _puedeHacerIntercambio;
	private boolean _puedeSerAtacado;
	private boolean _forzadoCaminar;
	private boolean _esLento;
	private boolean _puedeSwitchModoCriatura;
	private boolean _esTumba;
	private String _forjaEcK; // Comrpobar trabajo pago
	private boolean _pescarKuakua = false;
	private Encarnacion _encarnacion;
	private int _idEncarnacion = -1;
	private LibrosRunicos _librosRunicos;
	private int _idLibrosRunicos = -1;
	private Tutorial _tutorial;
	String _misiones;
	String _objMisiones;
	private Map<Integer, Integer> _ultMobMatado = new TreeMap<>();
	private int _hablandoNPC = 0;
	private int _ultDialog = 0;
	String _objTemporal = "";
	public boolean Marche = false;
	private Map<Integer, AccionDeJuego> _acciones = new TreeMap<>();
	public ArrayList<AccionDeJuego> _accionesCola = new ArrayList<>();
	private long _tiempoUltComercio = 0, _tiempoUltReclutamiento = 0;
	long _tiempoRefrescaMobs = 0;
	private long _tiempoUltSalvada = 0;
	private long _tiempoUltEstablo = 0;
	private long _tiempoUltSubidaBajada = 0;
	private long _tiempoUltKoli = 0;
	private long _tiempoUltAlineacion = 0;
	private long _tiempoUltClasificatoria = 0;
	private boolean _isOnAction = false;
	private boolean _primerentro = false;
	public String panelGE = "";
	private int _enCasar = 0;
	private int _seguidor = 0;
	public int celdadeparo = 0;
	public ArrayList<String> personajeAgredea = new ArrayList<>();
	public ArrayList<String> ligadosAnteriores = new ArrayList<>();
	ArrayList<Integer> personajesDeMisiones = new ArrayList<>();
	private boolean _reconectado;
	private Koliseo _koli = null;
	public boolean abrepanelinter = false;
	public int cuantopuede = 2;
	public Map<Integer, Integer> paramag = new TreeMap<>();
	public int reclamados1 = 0;
	public int reclamados2 = 0;
	public int reclamados3 = 0;
	public int reclamados4 = 0;
	public int reclamados5 = 0;
	int combateactual = 0;
	boolean actualizainv = false;
	public boolean antilag = false;
	public Map<Integer, Integer> itemsFinal = new HashMap<>();
	public Map<Integer, Integer> itemsFinaldos = new HashMap<>();
	public Map<Integer, Integer> itemsFinaltres = new HashMap<>();
	public Map<Integer, Integer> itemsFinalcua = new HashMap<>();
	public Map<Integer, Integer> itemsFinalcinc = new HashMap<>();
	public Map<Integer, Integer> ultrec = new HashMap<>();
	public boolean rompemineral = false;
	public boolean bucle = false;
	private boolean avisavoto = true;
	// OBJETIVOS DIARIOS: TODO:
	private Map<Integer, String> ObjetivosDiarios = new HashMap<>();// id objetivo -
																					// tipo,completado,necesario,obtenido,npc
	public int _resets = 0;
	public int desafdiario = 0;
	public String objetopersonal = "";
	public Timer generador = null;
	public ArrayList<Integer> accionesPJ = new ArrayList<>();// libre = 30
	public int enTorneo = 0;
	public Personaje iniciaTorneo = null;
	public Personaje esperaPelea = null;
	public Recaudador esperaPeleaRec = null;
	public Prisma esperaPeleaPrisma = null;
	public boolean usaTP = true;
	boolean pagadoImpuesto = false;
	public long ultimaPelea = 0;
	public boolean esperaKoliseo = false;
	public String larva = "";
	public ArrayList<Integer> Canjeados = new ArrayList<>();
	public int paginaCanj = 0;

	private boolean _comandoPasarTurno;

	void organizaInvent() {
		if (combateactual >= 5) {
			itemsFinal.clear();
			itemsFinaldos.clear();
			itemsFinaltres.clear();
			itemsFinalcua.clear();
			itemsFinalcinc.clear();
			reclamados1 = 0;
			reclamados2 = 0;
			reclamados3 = 0;
			reclamados4 = 0;
			reclamados5 = 0;
			combateactual = 0;
		}
	}

	// #GB4;6839,252|4;859,252|4;853,6840|0|0~0|0|0|0|0
	public void getTotalCombate() {
		StringBuilder fin = new StringBuilder("#GB");
		fin.append(getFinalCombate(itemsFinal) + "|");
		fin.append(getFinalCombate(itemsFinaldos) + "|");
		fin.append(getFinalCombate(itemsFinaltres) + "|");
		fin.append(getFinalCombate(itemsFinalcua) + "|");
		fin.append(getFinalCombate(itemsFinalcinc));
		GestorSalida.enviar(this, fin.toString());
	}

	private String getFinalCombate(Map<Integer, Integer> itemsFinal2) {
		StringBuilder fin = new StringBuilder();
		if (!itemsFinal2.isEmpty()) {
			fin.append(cuantopuede + ";");
			boolean paso = false;
			for (Entry<Integer, Integer> str : itemsFinal2.entrySet()) {
				if (!paso) {
					fin.append(str.getKey());
				} else {
					fin.append("," + str.getKey());
				}
				paso = true;
			}
		} else {
			fin.append("0");
		}
		return fin.toString();
	}

	public int _kolietapa = 1;

	public String _kolivictoriasyderrotas = "0;0"; // victorias;derrotas
	public int _kolirango = 1;// no se usa
	public int _kolipuntos = 0;

	public int _koliacepta = 1;

	// 1-1-1-1-1
	public String getInfoKoliseo() {
		String strf = "";
		strf = "#kC" + _kolietapa + "," + _kolirango + "," + _kolivictoriasyderrotas + "," + _kolipuntos + ",";
		if (this.getKoliseo() == null) {
			strf = strf + "1";
		} else {
			strf = strf + "" + _koliacepta;
		}
		if (_kolietapa == 3) {
			strf = strf + ",60000";
		} else {
			strf = strf + ",0";
		}
		return strf;
	}

	public ArrayList<Integer> getAllSucess() { // list Sucess
		return getAllSucess();
	}

	void subirStats(int victorias, int derrotas) {
		String newvalor = (Integer.parseInt(_kolivictoriasyderrotas.split(";")[0]) + victorias) + ";"
				+ (Integer.parseInt(_kolivictoriasyderrotas.split(";")[0]) + derrotas);
		_kolivictoriasyderrotas = newvalor;
	}

	//agregar comando pass en pelea
	public void setComandoPasarTurno(boolean _comandoPasarTurno) {
		this._comandoPasarTurno = _comandoPasarTurno;
	}

	public boolean getComandoPasarTurno() {
		return _comandoPasarTurno;
	}

	//agregar comando pass en pelea

	void addPuntos(int cantidad) {
		if (cantidad >= 0) {
			_kolipuntos += cantidad;
			if (_kolipuntos >= 100) {
				if (_kolirango < 6) {
					_kolirango += 1;
					_kolipuntos = 0;
				} else if (_kolirango == 6) {
					_kolipuntos = 100;
				}
			}
		}
	}

	void restarPuntos(int cantidad) {
		_kolipuntos -= cantidad;
		if (_kolipuntos < 0) {
			if (_kolirango > 1) {
				_kolirango -= 1;
				_kolipuntos = 0;
			} else if (_kolirango == 1) {
				_kolipuntos = 0;
			}
		}
	}

	public void setReconectado(boolean recon) {
		_reconectado = recon;
	}

	public boolean getReconectado() {
		return _reconectado;
	}

	public void setMascota(Mascota mascota) {
		_mascota = mascota;
	}

	public Mascota getMascota() {
		return _mascota;
	}

	public int getEnCasar() {
		return _enCasar;
	}

	public void setEnCasar(int rango) {
		_enCasar = rango;
	}

	private boolean _rompiendo;

	public void setRompiendo(boolean romper) {
		_rompiendo = romper;
	}

	public boolean getRompiendo() {
		return _rompiendo;
	}

	public boolean isOnAction() {
		return _isOnAction;
	}

	public void setIsOnAction(boolean is) {
		_isOnAction = is;
	}

	public void setMobMatado(Map<Integer, Integer> mobs) {
		_ultMobMatado = mobs;
	}

	public Map<Integer, Integer> getMobMatado() {
		return _ultMobMatado;
	}

	public void borrarGA(AccionDeJuego AJ) {
		if (AJ == null) {
			return;
		}
		if (AJ._accionID == 1 && getPelea() == null) {
			String packet = "";
			ArrayList<AccionDeJuego> accionesCola = new ArrayList<>();
			accionesCola.addAll(_accionesCola);
			for (AccionDeJuego curAction : accionesCola) {
				short cellID = 0;
				if (curAction != null) {
					try {
						packet = curAction._packet.substring(5).split(";")[0];
						cellID = (short) Camino.getCeldaMasCercanaAlrededor(getMapa(),
								(short) Integer.parseInt(packet.split(";")[0]), getCelda().getID(), null);
					} catch (Exception e) {
						continue;
					}
					int dist = Camino.distanciaEntreDosCeldas(getMapa(), cellID, getCelda().getID());
					if (dist <= 30) {
						Estaticos.juego_Accion(curAction, this, _cuenta);
					}
				}
			}
		}
		if (_acciones.containsKey(AJ._idUnica)) {
			_acciones.remove(AJ._idUnica);
		}
	}

	public void addGA(AccionDeJuego AJ) {
		if (isOnAction()) {
			return;
		}
		if (AJ._accionID == 1 && getPelea() == null) {
			Marche = true;
			_accionesCola.clear();
		}
		_acciones.put(AJ._idUnica, AJ);
	}

	void set_tiempoUltSalvada(long tiempoUltSalvada) {
		_tiempoUltSalvada = tiempoUltSalvada;
	}

	long get_tiempoUltSalvada() {
		return _tiempoUltSalvada;
	}

	public void set_tiempoUltEstablo(long tiempoUltSalvada) {
		_tiempoUltEstablo = tiempoUltSalvada;
	}

	public long get_tiempoUltEstablo() {
		return _tiempoUltEstablo;
	}

	public void set_tiempoUltAlineacion(long tiempoUltAlineacion) {
		_tiempoUltAlineacion = tiempoUltAlineacion;
	}

	public long get_tiempoUltAlineacion() {
		return _tiempoUltAlineacion;
	}

	public void set_tiempoUltReclutamiento(long tiempoUltReclutamiento) {
		_tiempoUltReclutamiento = tiempoUltReclutamiento;
	}

	public long get_tiempoUltReclutamiento() {
		return _tiempoUltReclutamiento;
	}

	public void set_tiempoUltComercio(long tiempoUltComercio) {
		_tiempoUltComercio = tiempoUltComercio;
	}

	public long get_tiempoUltComercio() {
		return _tiempoUltComercio;
	}

	public void set_acciones(Map<Integer, AccionDeJuego> acciones) {
		_acciones = acciones;
	}

	public Map<Integer, AccionDeJuego> get_acciones() {
		return _acciones;
	}

	int getultDialog() {
		return _ultDialog;
	}

	public void setultDialog(int ultDialog) {
		_ultDialog = ultDialog;
	}

	public int gethablandoNPC() {
		return _hablandoNPC;
	}

	public void sethablandoNPC(int conversando) {
		_hablandoNPC = conversando;
	}

	public void setTutorial(Tutorial tuto) {
		_tutorial = tuto;
	}

	public Tutorial getTutorial() {
		return _tutorial;
	}

	public void setEncarnacion(Encarnacion encarnacion) {
		_encarnacion = encarnacion;
		if (encarnacion != null) {
			_idEncarnacion = encarnacion.getID();
		} else {
			_idEncarnacion = -1;
		}
	}

	public int getIDEncarnacion() {
		return _idEncarnacion;
	}

	public Encarnacion getEncarnacion() {
		return _encarnacion;
	}

	public void setLibrosRunicos(LibrosRunicos librosRunicos) {
		_librosRunicos = librosRunicos;
		if (librosRunicos != null) {
			_idLibrosRunicos = librosRunicos.getID();
		} else {
			_idLibrosRunicos = -1;
		}
	}

	public int getIDLibrosRunicos() {
		return _idLibrosRunicos;
	}

	public LibrosRunicos getLibrosRunicos() {
		return _librosRunicos;
	}



	public void setPescarKuakua(boolean pescar) {
		_pescarKuakua = pescar;
	}

	public boolean getPescarKuakua() {
		return _pescarKuakua;
	}

	public void setForjaEcK(String forja) {
		_forjaEcK = forja;
	}

	public String getForjaEcK() {
		return _forjaEcK;
	}

	private void efectuarRestriccionesA() {
		_puedeAgredir = (_restriccionesA & 1) != 1;
		_puedeDesafiar = (_restriccionesA & 2) != 2;
		_puedeIntercambiar = (_restriccionesA & 4) != 4;
		_puedeAtacarAMutante = (_restriccionesA & 8) == 8;
		_puedeChatATodos = (_restriccionesA & 16) != 16;
		_puedeMercante = (_restriccionesA & 32) != 32;
		_puedeUsarObjetos = (_restriccionesA & 64) != 64;
		_puedeInteractuarRecaudador = (_restriccionesA & 128) != 128;
		_puedeInteractuarObjetos = (_restriccionesA & 256) != 256;
		_puedeHablarNPC = (_restriccionesA & 512) != 512;
		_puedeAtacarMobsDungCuandoMutante = (_restriccionesA & 4096) == 4096;
		_puedeMoverTodasDirecciones = (_restriccionesA & 8192) == 8192;
		_puedeAtacarMobsCualquieraCuandoMutante = (_restriccionesA & 16384) == 16384;
		_puedeInteractuarPrisma = (_restriccionesA & 32768) != 32768;
	}

	public int getRestriccionesA() {
		int restr = 0;
		if (!_puedeAgredir) {
			restr += 1;
		}
		if (!_puedeDesafiar) {
			restr += 2;
		}
		if (!_puedeIntercambiar) {
			restr += 4;
		}
		if (_puedeAtacarAMutante) {
			restr += 8;
		}
		if (!_puedeChatATodos) {
			restr += 16;
		}
		if (!_puedeMercante) {
			restr += 32;
		}
		if (!_puedeUsarObjetos) {
			restr += 64;
		}
		if (!_puedeInteractuarRecaudador) {
			restr += 128;
		}
		if (!_puedeInteractuarObjetos) {
			restr += 256;
		}
		if (!_puedeHablarNPC) {
			restr += 512;
		}
		if (_puedeAtacarMobsDungCuandoMutante) {
			restr += 4096;
		}
		if (_puedeMoverTodasDirecciones) {
			restr += 8192;
		}
		if (_puedeAtacarMobsCualquieraCuandoMutante) {
			restr += 16384;
		}
		if (!_puedeInteractuarPrisma) {
			restr += 32768;
		}
		_restriccionesA = restr;
		return restr;
	}

	private void efectuarRestriccionesB() {
		_puedeSerAgredido = (_restriccionesB & 1) != 1;
		_puedeSerDesafiado = (_restriccionesB & 2) != 2;
		_puedeHacerIntercambio = (_restriccionesB & 4) != 4;
		_puedeSerAtacado = (_restriccionesB & 8) != 8;
		_forzadoCaminar = (_restriccionesB & 16) == 16;
		_esLento = (_restriccionesB & 32) == 32;
		_puedeSwitchModoCriatura = (_restriccionesB & 64) != 64;
		_esTumba = (_restriccionesB & 128) == 128;
	}

	public int getRestriccionesB() {
		int restr = 0;
		if (!_puedeSerAgredido) {
			restr += 1;
		}
		if (!_puedeSerDesafiado) {
			restr += 2;
		}
		if (!_puedeHacerIntercambio) {
			restr += 4;
		}
		if (!_puedeSerAtacado) {
			restr += 8;
		}
		if (_forzadoCaminar) {
			restr += 16;
		}
		if (_esLento) {
			restr += 32;
		}
		if (!_puedeSwitchModoCriatura) {
			restr += 64;
		}
		if (_esTumba) {
			restr += 128;
		}
		_restriccionesB = restr;
		return restr;
	}

	public Map<Integer, Personaje> getSeguidores() {
		return _seguidores;
	}

	public Map<Integer, Duo<Integer, Integer>> getHechizosSetClase() {
		return _hechizosSetClase;
	}

	public void delHechizosSetClase(int hechizo) {
		if (_hechizosSetClase.containsKey(hechizo)) {
			_hechizosSetClase.remove(hechizo);
		}
	}

	public void addHechizosSetClase(int hechizo, int efecto, int modificacion) {
		if (!_hechizosSetClase.containsKey(hechizo)) {
			_hechizosSetClase.put(hechizo, new Duo<>(efecto, modificacion));
		}
	}

	public void setListaArtesanos(boolean viendo) {
		_listaArtesanos = viendo;
	}

	public boolean getListaArtesanos() {
		return _listaArtesanos;
	}

	void addScrollFuerza(int scroll) {
		_scrollFuerza += scroll;
	}

	void addScrollAgilidad(int scroll) {
		_scrollAgilidad += scroll;
	}

	void addScrollSuerte(int scroll) {
		_scrollSuerte += scroll;
	}

	void addScrollVitalidad(int scroll) {
		_scrollVitalidad += scroll;
	}

	void addScrollSabiduria(int scroll) {
		_scrollSabiduria += scroll;
	}

	void addScrollInteligencia(int scroll) {
		_scrollInteligencia += scroll;
	}

	public int getScrollFuerza() {
		return _scrollFuerza;
	}

	public int getScrollAgilidad() {
		return _scrollAgilidad;
	}

	public int getScrollSuerte() {
		return _scrollSuerte;
	}

	public int getScrollVitalidad() {
		return _scrollVitalidad;
	}

	public int getScrollSabiduria() {
		return _scrollSabiduria;
	}

	public int getScrollInteligencia() {
		return _scrollInteligencia;
	}

	public void setMapaDefPerco(Mapa mapa) {
		_tempMapaDefPerco = mapa;
	}

	public void setCeldaDefPerco(Celda celda) {
		_tempCeldaDefPerco = celda;
	}

	public Celda getCeldaDefPerco() {
		return _tempCeldaDefPerco;
	}

	public Mapa getMapaDefPerco() {
		return _tempMapaDefPerco;
	}

	public ArrayList<Integer> getSetClase() {
		return _setClase;
	}

	public void setSetClase(ArrayList<Integer> SetClase) {
		_setClase = SetClase;
	}

	public void agregarSetClase(int item) {
		if (!_setClase.contains(item)) {
			_setClase.add(item);
		}
		return;
	}

	public void borrarSetClase(int item) {
		if (_setClase.contains(item)) {
			int index = _setClase.indexOf(item);
			_setClase.remove(index);
		}
	}

	public int getMercante() {
		return _esMercante;
	}

	public void setMercante(int mercante) {
		_esMercante = mercante;
	}

	public Trueque getTrueque() {
		return _trueque;
	}

	public void setTrueque(Trueque trueque) {
		_trueque = trueque;
	}

	public AccionDeJuego getTaller() {
		return _taller;
	}

	public void setTaller(AccionDeJuego Taller) {
		_taller = Taller;
	}

	public boolean liderMaitre = false;
	public boolean esMaitre = false;
	public boolean enMovi = false;



	public static class Grupo {
		private CopyOnWriteArrayList<Personaje> _personajesGrupo = new CopyOnWriteArrayList<>();
		private Personaje _liderGrupo;

		public Grupo(Personaje p1,Personaje p2) {
			_liderGrupo = p1;
			_personajesGrupo.add(p1);
			_personajesGrupo.add(p2);
		}

		public boolean esLiderGrupo(int id) {
			return _liderGrupo.getID() == id;
		}

		public void addPerso(Personaje perso) {

			if (_personajesGrupo.contains(perso)) {
				return;
			}
			_personajesGrupo.add(perso);
			perso.setGrupo(this);
		}




		public ArrayList<Integer> getIDsPersos() {
			ArrayList<Integer> lista = new ArrayList<>();
			for (Personaje perso : _personajesGrupo) {
				lista.add(perso.getID());
			}
			return lista;
		}

		public int getNumeroPjs() {
			return _personajesGrupo.size();
		}

		public int getNivelGrupo() {
			int nivel = 0;
			for (Personaje p : _personajesGrupo) {
				nivel += p.getNivel();
			}
			return nivel;
		}

		public CopyOnWriteArrayList<Personaje> getPersos() {
			return _personajesGrupo;
		}

		public Personaje getLiderGrupo() {
			return _liderGrupo;
		}

		public void dejarGrupo(Personaje p) {
			if (!_personajesGrupo.contains(p)) {
				return;
			}
			if (p.liderMaitre) {
				for (Personaje pjx : p.getGrupo().getPersos()) {
					if (pjx == p) {
						p.liderMaitre = false;
						continue;
					}
					pjx.esMaitre = false;
				}
			}
			if (p.getGrupo().getPersos().size() <= 2) {
				if (p.esMaitre) {
					for (Personaje pjx : p.getGrupo().getPersos()) {
						if (pjx == p) {
							p.esMaitre = false;
							continue;
						}
						pjx.liderMaitre = false;
					}
				}
			}
			if (p.esMaitre) {
				p.esMaitre = false;
			}
			p.setGrupo(null);
			_personajesGrupo.remove(p);
			if (_personajesGrupo.size() == 1) {
				_personajesGrupo.get(0).setGrupo(null);
				if (_personajesGrupo.get(0).getCuenta() == null
						|| _personajesGrupo.get(0).getCuenta().getEntradaPersonaje() == null) {
					return;
				}
				GestorSalida.ENVIAR_PV_DEJAR_GRUPO(_personajesGrupo.get(0).getCuenta().getEntradaPersonaje().getOut(),
						"");
			} else {
				GestorSalida.ENVIAR_PM_EXPULSAR_PJ_GRUPO(this, p.getID());
			}
		}
	}

	public static class Stats {
		private Map<Integer, Integer> _statsEfecto = new TreeMap<>();

		Stats(boolean addBases, Personaje perso) {
			_statsEfecto = new TreeMap<>();
			if (!addBases) {
				return;
			}
			_statsEfecto.put(111, perso.getNivel() < Emu.NIVEL_PA1 ? 6 : (6 + Emu.CANTIDAD_PA1));
			_statsEfecto.put(CentroInfo.STATS_ADD_PM, 3);
			_statsEfecto.put(176, perso.getClase(false) == CentroInfo.CLASE_ANUTROF ? 140 : 100);
			_statsEfecto.put(158, 1000);
			_statsEfecto.put(182, 1);
			_statsEfecto.put(174, 1);
		}

		private Stats(Map<Integer, Integer> stats, boolean addBases, Personaje perso) {
			_statsEfecto = stats;
			if (!addBases) {
				return;
			}
			_statsEfecto.put(111, perso.getNivel() < Emu.NIVEL_PA1 ? 6 : (6 + Emu.CANTIDAD_PA1));
			_statsEfecto.put(CentroInfo.STATS_ADD_PM, 3);
			_statsEfecto.put(176, perso.getClase(false) == CentroInfo.CLASE_ANUTROF ? 140 : 100);
			_statsEfecto.put(158, 1000);
			_statsEfecto.put(182, 1);
			_statsEfecto.put(174, 1);
		}

		Stats(Map<Integer, Integer> stats) {
			_statsEfecto = stats;
		}

		public Stats() {
			_statsEfecto = new TreeMap<>();
		}

		public int addUnStat(int stat, int valor) {
			if (_statsEfecto.get(stat) == null) {
				_statsEfecto.put(stat, valor);
			} else {
				int nuevoValor = (_statsEfecto.get(stat)) + valor;
				_statsEfecto.remove(stat);
				_statsEfecto.put(stat, nuevoValor);
			}
			return _statsEfecto.get(stat);
		}

		int especificarStat(int stat, int valor) {
			if (_statsEfecto.get(stat) == null) {
				_statsEfecto.put(stat, valor);
			} else {
				_statsEfecto.remove(stat);
				_statsEfecto.put(stat, valor);
			}
			return _statsEfecto.get(stat);
		}

		boolean sonStatsIguales(Stats otros) {
			for (Entry<Integer, Integer> entry : _statsEfecto.entrySet()) {
				if (otros.getStatsComoMap().get(entry.getKey()) == null
						|| otros.getStatsComoMap().get(entry.getKey()) != entry.getValue()) {
					return false;
				}
			}
			for (Entry<Integer, Integer> entry : otros.getStatsComoMap().entrySet()) {
				if (_statsEfecto.get(entry.getKey()) == null || _statsEfecto.get(entry.getKey()) != entry.getValue()) {
					return false;
				}
			}
			return true;
		}

		public int getEfecto(int id) {
			int val;
			if (_statsEfecto.get(id) == null) {
				val = 0;
			} else {
				val = _statsEfecto.get(id);
			}
			switch (id) {
			case 160:
				if (_statsEfecto.get(CentroInfo.STATS_REM_PROB_PERD_PA) != null) {
					val -= (getEfecto(CentroInfo.STATS_REM_PROB_PERD_PA));
				}
				if (_statsEfecto.get(124) != null) {
					val += getEfecto(124) / 4;
				}
				break;
			case 161:
				if (_statsEfecto.get(CentroInfo.STATS_REM_PROB_PERD_PM) != null) {
					val -= (getEfecto(CentroInfo.STATS_REM_PROB_PERD_PM));
				}
				if (_statsEfecto.get(124) != null) {
					val += getEfecto(124) / 4;
				}
				break;
			case 174:
				if (_statsEfecto.get(CentroInfo.STATS_REM_INIT) != null) {
					val -= _statsEfecto.get(CentroInfo.STATS_REM_INIT);
				}
				break;
			case 119:
				if (_statsEfecto.get(CentroInfo.STATS_REM_AGILIDAD) != null) {
					val -= _statsEfecto.get(CentroInfo.STATS_REM_AGILIDAD);
				}
				break;
			case 118:
				if (_statsEfecto.get(CentroInfo.STATS_REM_FUERZA) != null) {
					val -= _statsEfecto.get(CentroInfo.STATS_REM_FUERZA);
				}
				break;
			case 123:
				if (_statsEfecto.get(CentroInfo.STATS_REM_SUERTE) != null) {
					val -= _statsEfecto.get(CentroInfo.STATS_REM_SUERTE);
				}
				break;
			case 126:
				if (_statsEfecto.get(CentroInfo.STATS_REM_INTELIGENCIA) != null) {
					val -= _statsEfecto.get(CentroInfo.STATS_REM_INTELIGENCIA);
				}
				break;
			case 111:
				if (_statsEfecto.get(CentroInfo.STATS_ADD_PA2) != null) {
					val += _statsEfecto.get(CentroInfo.STATS_ADD_PA2);
				}
				if (_statsEfecto.get(CentroInfo.STATS_REM_PA) != null) {
					val -= _statsEfecto.get(CentroInfo.STATS_REM_PA);
				}
				if (_statsEfecto.get(CentroInfo.STATS_REM_PA_NOESQ) != null) { // No esquivable
					val -= _statsEfecto.get(CentroInfo.STATS_REM_PA_NOESQ);
				}
				break;
			case 128:
				if (_statsEfecto.get(CentroInfo.STATS_ADD_PM2) != null) {
					val += _statsEfecto.get(CentroInfo.STATS_ADD_PM2);
				}
				if (_statsEfecto.get(CentroInfo.STATS_REM_PM) != null) {
					val -= _statsEfecto.get(CentroInfo.STATS_REM_PM);
				}
				if (_statsEfecto.get(CentroInfo.STATS_REM_PM_NOESQ) != null) { // No esquivable
					val -= _statsEfecto.get(CentroInfo.STATS_REM_PM_NOESQ);
				}
				break;
			case 117:
				if (_statsEfecto.get(CentroInfo.STATS_REM_ALCANCE) != null) {
					val -= _statsEfecto.get(CentroInfo.STATS_REM_ALCANCE);
				}
				break;
			case 125:
				if (_statsEfecto.get(110) != null) {
					val += _statsEfecto.get(110);
				}
				if (_statsEfecto.get(CentroInfo.STATS_REM_VITALIDAD) != null) {
					val -= _statsEfecto.get(CentroInfo.STATS_REM_VITALIDAD);
				}
				break;
			case 112:
				if (_statsEfecto.get(CentroInfo.STATS_REM_DAÑOS) != null) {
					val -= _statsEfecto.get(CentroInfo.STATS_REM_DAÑOS);
				}
				break;
			case 158:
				if (_statsEfecto.get(CentroInfo.STATS_REM_PODS) != null) {
					val -= _statsEfecto.get(CentroInfo.STATS_REM_PODS);
				}
				break;
			case 176:
				if (_statsEfecto.get(CentroInfo.STATS_REM_PROSPECCION) != null) {
					val -= _statsEfecto.get(CentroInfo.STATS_REM_PROSPECCION);
				}
				if (_statsEfecto.get(123) != null) {
					val += (_statsEfecto.get(123) / 10);
				}
				break;
			case 242:
				if (_statsEfecto.get(CentroInfo.STATS_REM_R_TIERRA) != null) {
					val -= _statsEfecto.get(CentroInfo.STATS_REM_R_TIERRA);
				}
				break;
			case 243:
				if (_statsEfecto.get(CentroInfo.STATS_REM_R_AGUA) != null) {
					val -= _statsEfecto.get(CentroInfo.STATS_REM_R_AGUA);
				}
				break;
			case 244:
				if (_statsEfecto.get(CentroInfo.STATS_REM_R_AIRE) != null) {
					val -= _statsEfecto.get(CentroInfo.STATS_REM_R_AIRE);
				}
				break;
			case 240:
				if (_statsEfecto.get(CentroInfo.STATS_REM_R_FUEGO) != null) {
					val -= _statsEfecto.get(CentroInfo.STATS_REM_R_FUEGO);
				}
				break;
			case 241:
				if (_statsEfecto.get(CentroInfo.STATS_REM_R_NEUTRAL) != null) {
					val -= _statsEfecto.get(CentroInfo.STATS_REM_R_NEUTRAL);
				}
				break;
			case 210:
				if (_statsEfecto.get(CentroInfo.STATS_REM_RP_TER) != null) {
					val -= _statsEfecto.get(CentroInfo.STATS_REM_RP_TER);
				}
				break;
			case 211:
				if (_statsEfecto.get(CentroInfo.STATS_REM_RP_EAU) != null) {
					val -= _statsEfecto.get(CentroInfo.STATS_REM_RP_EAU);
				}
				break;
			case 212:
				if (_statsEfecto.get(CentroInfo.STATS_REM_RP_AIR) != null) {
					val -= _statsEfecto.get(CentroInfo.STATS_REM_RP_AIR);
				}
				break;
			case 213:
				if (_statsEfecto.get(CentroInfo.STATS_REM_RP_FEU) != null) {
					val -= _statsEfecto.get(CentroInfo.STATS_REM_RP_FEU);
				}
				break;
			case 214:
				if (_statsEfecto.get(CentroInfo.STATS_REM_RP_NEU) != null) {
					val -= _statsEfecto.get(CentroInfo.STATS_REM_RP_NEU);
				}
				break;
			case CentroInfo.STATS_ADD_DOMINIO:
				if (_statsEfecto.get(CentroInfo.STATS_ADD_DOMINIO) != null) {
					val = _statsEfecto.get(CentroInfo.STATS_ADD_DOMINIO);
				}
				break;
			case 138:
				if (_statsEfecto.get(CentroInfo.STATS_REM_DAÑOS_PORC) != null) {
					val -= _statsEfecto.get(CentroInfo.STATS_REM_DAÑOS_PORC);
				}
				break;
			case 178:
				if (_statsEfecto.get(CentroInfo.STATS_REM_CURAS) != null) {
					val -= _statsEfecto.get(CentroInfo.STATS_REM_CURAS);
				}
				break;
			}
			return val;
		}

		static Stats acumularStats(Stats s1, Stats s2) {
			TreeMap<Integer, Integer> stats = new TreeMap<>();
			for (int a = 0; a <= CentroInfo.ID_EFECTO_MAXIMO; a++) {
				if ((s1._statsEfecto.get(a) == null || s1._statsEfecto.get(a) == 0)
						&& (s2._statsEfecto.get(a) == null || s2._statsEfecto.get(a) == 0)) {
					continue;
				}
				int som = 0;
				if (s1._statsEfecto.get(a) != null) {
					som += s1._statsEfecto.get(a);
				}
				if (s2._statsEfecto.get(a) != null) {
					som += s2._statsEfecto.get(a);
				}
				stats.put(a, som);
			}
			return new Stats(stats, false, null);
		}

		public Map<Integer, Integer> getStatsComoMap() {
			return _statsEfecto;
		}

		public String convertirStatsAString() {
			StringBuilder str = new StringBuilder("");
			for (Entry<Integer, Integer> entry : _statsEfecto.entrySet()) {
				if (str.length() > 0) {
					str.append(",");
				}
				str.append(Integer.toHexString(entry.getKey()) + "#" + Integer.toHexString(entry.getValue()) + "#0#0");
			}
			return str.toString();
		}
	}

	public Personaje(int id, String nombre, int sexo, int clase, int color1, int color2, int color3, long kamas,
			int pts, int capital, int energia, int nivel, long exp, int talla, int gfxID, byte alineacion, int cuenta,
			Map<Integer, Integer> stats, int mostrarAmigos, byte mostarAlineacion, String canal, short mapa, int celda,
			String inventario, int pdvPorc, String hechizos, String ptoSalvada, String oficios, int xpMontura,
			int montura, int honor, int deshonor, int nivelAlineacion, String zaaps, int i, int esposoId, String tienda,
			int mercante, int ScrollFuerza, int ScrollInteligencia, int ScrollAgilidad, int ScrollSuerte,
			int ScrollVitalidad, int ScrollSabiduria, int restriccionesA, int restriccionesB, int encarnacion,
			String misiones, String objMisiones, String kolis, int dia, String objetivosdiarios, int actualprecio,
			int resets, int desafd, String datos, String titulos, int etapax, int hechi, String pasebatalla) {
		_encarnacion = MundoDofus.getEncarnacion(encarnacion);
		if (_encarnacion != null) {
			_idEncarnacion = encarnacion;
		}
		//_librosRunicos = MundoDofus.getLibroRunico(librosRunicos);
		_oficioPublico = false;
		_scrollAgilidad = ScrollAgilidad;
		_scrollFuerza = ScrollFuerza;
		_scrollInteligencia = ScrollInteligencia;
		_scrollSabiduria = ScrollSabiduria;
		_scrollSuerte = ScrollSuerte;
		_scrollVitalidad = ScrollVitalidad;
		_ID = id;
		set_misiones(misiones);
		_objMisiones = objMisiones;
		_nombre = nombre;
		_sexo = sexo;
		_clase = clase;
		_color1 = color1;
		_color2 = color2;
		_color3 = color3;
		_kamas = kamas;
		_puntosHechizo = pts;
		_capital = capital;
		_alineacion = alineacion;
		_honor = honor;
		_deshonor = deshonor;
		_nivelAlineacion = nivelAlineacion;
		_energia = energia;
		_nivel = nivel;
		_experiencia = exp;
		if (montura != -1) {
			_montura = MundoDofus.getDragopavoPorID(montura);
		}
		_talla = talla;
		_gfxID = gfxID;
		_xpDonadaMontura = xpMontura;
		_baseStats = new Stats(stats, true, this);
		_cuentaID = cuenta;
		_cuenta = MundoDofus.getCuenta(cuenta);
		_mostrarConeccionAmigos = mostrarAmigos == 1;
		_esposo = esposoId;
		if (getAlineacion() != 0) { // TODO:
			_mostrarAlas = mostarAlineacion == 1;
		} else {
			_mostrarAlas = false;
		}
		_canales = canal;
		_mapa = MundoDofus.getMapa(mapa);
		_puntoSalvado = ptoSalvada;
		if (_mapa == null) {
			_mapa = MundoDofus.getMapa((short) 7411);
			_celda = _mapa.getCelda(280);
		} else if (_mapa != null) {
			_celda = _mapa.getCelda(celda);
			if (_celda == null) {// TODO:
				_mapa = MundoDofus.getMapa((short) 7411);
				_celda = _mapa.getCelda(280);
			}
		}
		_cambiarNombre = false;
		for (String str : zaaps.split(",")) {
			if (str.equals("")) {
				continue;
			}
			try {
				_zaaps.add(Integer.parseInt(str));
			} catch (Exception e) {
			}
		}

		if (_mapa == null || _celda == null) {
			System.out.println("Mapa o celda invalido del personaje " + _nombre);
		}
		if (!inventario.equals("")) {
			if (inventario.charAt(inventario.length() - 1) == '|') {
				inventario = inventario.substring(0, inventario.length() - 1);
			}
			GestorSQL.CARGAR_OBJETOS(inventario.replace("|", ","));
		}
		for (String item : inventario.split("\\|")) {
			if (item.equals("")) {
				continue;
			}
			String[] infos = item.split(":");
			int guid = Integer.parseInt(infos[0]);
			Objeto obj = MundoDofus.getObjeto(guid);
			if (obj == null) {
				continue;
			}
			_objetos.put(obj.getID(), obj);
		}
		if (!tienda.equals("")) {
			if (tienda.charAt(tienda.length() - 1) == '|') {
				tienda = tienda.substring(0, tienda.length() - 1);
			}
			GestorSQL.CARGAR_OBJETOS(tienda.replace("|", ","));
		}
		for (String item : tienda.split("\\|")) {
			if (item.equals("")) {
				continue;
			}
			String[] infos = item.split(":");
			int idObjeto = Integer.parseInt(infos[0]);
			Objeto obj = MundoDofus.getObjeto(idObjeto);
			if (obj == null) {
				continue;
			}
			_tienda.add(obj);
		}
		_esMercante = mercante;
		if (_encarnacion != null) {
			_PDVMAX = _encarnacion.getPDVMAX();
		} else {
			_PDVMAX = (nivel - 1) * 5 + (_nivel > 200 ? (_nivel - 200) * (_clase == 11 ? 2 : 1) * 5 : 0)
					+ CentroInfo.getBasePDV(clase) + getTotalStats().getEfecto(125);
		}
		if (pdvPorc > 100) {
			_PDV = (_PDVMAX * 100) / 100;
		} else {
			_PDV = (_PDVMAX * pdvPorc) / 100;
		}
		if (hechizos == "") {
			if (!Emu.SistemaHechizo) {
				_hechizos = CentroInfo.getHechizosIniciales(clase);
				for (int a = 1; a <= getNivel(); a++) {
					CentroInfo.subirNivelAprenderHechizos(this, a);
				}
				_lugaresHechizos = CentroInfo.getLugaresHechizosIniciales(clase);
			}
		} else {
			analizarPosHechizos(hechizos);
		}
		if (!oficios.equals("")) {
			int posx = 0;
			for (String aJobData : oficios.split(";")) {
				String[] infos = aJobData.split(",");
				try {
					int oficioID = Integer.parseInt(infos[0]);
					long xp = Long.parseLong(infos[1]);
					if (xp > 581687) {
						xp = 581687;
					}
					Oficio oficio = MundoDofus.getOficio(oficioID);
					int pos = aprenderOficio(oficio, posx, false);
					if (pos == -1) {
						continue;
					}
					StatsOficio statsOficio = _statsOficios.get(pos);
					statsOficio.addXP(this, xp);
					posx += 1;
				} catch (Exception e) {
				}
			}
		}
		_titulo = i;
		_restriccionesA = restriccionesA;
		_restriccionesB = restriccionesB;
		efectuarRestriccionesA();
		efectuarRestriccionesB();
		Objeto mascota = getObjPosicion(8);
		if (mascota != null) {
			_mascota = MundoDofus.getMascota(mascota.getID());
		}
		if (nivel < 100) {
			cuantopuede = 4;
		} else if (nivel < 150) {
			cuantopuede = 3;
		} else if (nivel < 200) {
			cuantopuede = 2;
		} else if (nivel <= 301) {
			cuantopuede = 1;
		} else {
			cuantopuede = 1;
		}
		diadeo = dia;
		if (!objetivosdiarios.isEmpty()) {
			String[] str = objetivosdiarios.split(";");
			for (String entry : str) {
				ObjetivosDiarios.put(Integer.parseInt(entry.split("\\*")[0]), entry.split("\\*")[1]);
			}
		}
		actualPrecio = actualprecio;
		_resets = resets;
		desafdiario = desafd;
		try {
			_kolivictoriasyderrotas = kolis.split("-")[0];
		} catch (Exception e1) {
			_kolivictoriasyderrotas = "0;0";
		}
		try {
			_kolirango = Integer.parseInt(kolis.split("-")[1].split(";")[0]);
		} catch (Exception e1) {
			_kolirango = 1;
		}
		try {
			_kolipuntos = Integer.parseInt(kolis.split("-")[1].split(";")[1]);
		} catch (Exception e1) {
			_kolipuntos = 0;
		}
		for (String reg : datos.split(";")) {
			if (reg.equals("")) {
				continue;
			}
			accionesPJ.add(Integer.parseInt(reg));
		}
		for (String reg : titulos.split(",")) {
			if (reg.equals("")) {
				continue;
			}
			_titulos.add(Integer.parseInt(reg));
		}
		etapa = etapax;
		buffClase = hechi;
		if (!pasebatalla.equals("")) {
			String[] splitx = pasebatalla.split(";");
			if (!splitx[0].isEmpty()) {
				for (String str : splitx[0].split(",")) {
					Canjeados.add(Integer.parseInt(str));
				}
			}
			PuntosPrestigio = Integer.parseInt(splitx[1]);
			paginaCanj = Integer.parseInt(splitx[2]);
		}
	}

	public ArrayList<Integer> _titulos = new ArrayList<>();

	public String getTitulos() {
		StringBuilder strx = new StringBuilder();
		boolean prim = false;
		for (Integer str : _titulos) {
			if (prim) {
				strx.append(",");
			}
			strx.append(str);
			prim = true;
		}
		return strx.toString();
	}

	public void addTitulos(int id) {
		if (!_titulos.contains(id) && id != 0 && id != 8 && id != 9 && id != 10) {
			_titulos.add(id);
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(this, "Acabas de ganar un tÍtulo", Colores.ROJO);
		}
	}

	public String getAcciones() {
		StringBuilder strx = new StringBuilder();
		boolean prim = false;
		for (Integer str : accionesPJ) {
			if (prim) {
				strx.append(";");
			}
			strx.append(str);
			prim = true;
		}
		return strx.toString();
	}

	public int diadeo = 0;

	// Doble
	private Personaje(int id, String nombre, int sexo, int clase, int color1, int color2, int color3, int nivel,
			int talla, int gfxid, Map<Integer, Integer> stats, Map<Integer, Objeto> objetos, int pdvPorc,
			byte mostarAlineacion, int montura, int nivelAlineacion, byte alineacion) {
		_ID = id;
		_nombre = nombre;
		_sexo = sexo;
		_clase = clase;
		_color1 = color1;
		_color2 = color2;
		_color3 = color3;
		_nivel = nivel;
		_nivelAlineacion = nivelAlineacion;
		_talla = talla;
		_gfxID = gfxid;
		_baseStats = new Stats(stats, true, this);
		_objetos.putAll(objetos);
		if (_encarnacion != null) {
			_PDVMAX = _encarnacion.getPDVMAX();
		} else {
			_PDVMAX = (nivel - 1) * 5 + (_nivel > 200 ? (_nivel - 200) * 5 : 0) + CentroInfo.getBasePDV(clase)
					+ getTotalStats().getEfecto(125);
		}
		_PDV = (_PDVMAX * pdvPorc) / 100;
		_alineacion = alineacion;
		if (getAlineacion() != 0) {
			_mostrarAlas = mostarAlineacion == 1;
		} else {
			_mostrarAlas = false;
		}
		if (montura != -1) {
			_montura = MundoDofus.getDragopavoPorID(montura);
		}
	}

	synchronized static Personaje crearPersonaje(String nombre, int sexo, int clase, int color1, int color2, int color3,
			Cuenta cuenta) {
		String zaaps = "164,7411";
		long kamas = 0;
		String objetos = "";
		int nivel = Emu.INICIAR_NIVEL;
		Objeto obj = MundoDofus.getObjModelo(1737).crearObjDesdeModelo(20, false);
		Objeto obj2 = MundoDofus.getObjModelo(580).crearObjDesdeModelo(10, false);
		Objeto obj3 = MundoDofus.getObjModelo(548).crearObjDesdeModelo(10, false);
		obj.setPosicion(35);
		obj2.setPosicion(36);
		obj3.setPosicion(37);
		MundoDofus.addObjeto(obj, true);
		MundoDofus.addObjeto(obj2, true);
		MundoDofus.addObjeto(obj3, true);
		objetos += obj.getID() + "|" + obj2.getID() + "|" + obj3.getID();
		if (cuenta.getPrimeraVez() == 1) {
			Objeto obj4 = MundoDofus.getObjModelo(70200).crearObjDesdeModelo(1, false);
			MundoDofus.addObjeto(obj4, true);
			objetos += "|" + obj4.getID();
			kamas = Emu.INICIAR_KAMAS;
			cuenta.setPrimeraVez(0);
			kamas = 0;
			GestorSQL.ACTUALIZAR_PRIMERA_VEZ(cuenta, 0);
		}
		int cell = 0;
		int map = 0;
		map = 25047;
		cell = 241;
		Personaje nuevoPersonaje = new Personaje(GestorSQL.getSigIDPersonaje(), nombre, sexo, clase, color1, color2,
				color3, kamas, ((nivel - 1) * 1), 150, 10000, nivel, MundoDofus.getExpMinPersonaje(nivel), 100,
				Integer.parseInt(clase + "" + sexo), (byte) 0, cuenta.getID(), new TreeMap<>(), 1,
				(byte) 0, "*#%!pi$:?", (short) map, cell, objetos, 100, "", "25046,154", "", 0, -1, 0, 0, 0, zaaps, 0,
				0, "", 0, 0, 0, 0, 0, 0, 0, 8192, 0, -1, "", "", "0;0-1;0", 0, "", 100, 0, 0, "", "", 1, -1, "");
		nuevoPersonaje.fechacrea = new Date();
		nuevoPersonaje.primerRefresh = true;
		if (!Emu.SistemaHechizo) {
			nuevoPersonaje._hechizos = CentroInfo.getHechizosIniciales(clase);
			for (int a = 1; a <= nuevoPersonaje.getNivel(); a++) {
				CentroInfo.subirNivelAprenderHechizos(nuevoPersonaje, a);
			}
			nuevoPersonaje._lugaresHechizos = CentroInfo.getLugaresHechizosIniciales(clase);
		}
		if (!GestorSQL.AGREGAR_PJ_EN_BD(nuevoPersonaje, objetos)) {
			return null;
		}
		MundoDofus.addPersonaje(nuevoPersonaje);
		nuevoPersonaje.primeravez = true;
		nuevoPersonaje.setEncarnacion(null);
		return nuevoPersonaje;
	}

	public int yaactualizo = 0;
	private boolean primeravez = false;
	public boolean primerRefresh = false;
	public int etapa = 1;

public void Conectarse() {
    if (_cuenta.getEntradaPersonaje() == null) {
        return;
    }
    PrintWriter out = _cuenta.getEntradaPersonaje().getOut();
    _enLinea = true;
    _cuenta.setTempPerso(this);

    // =========================================================================
    // 1. PROTOCOLO DE IDENTIDAD (Indispensable para el Cliente Modern)
    // =========================================================================
    
    // ASK: Confirma la selección del personaje y sus items visuales
    GestorSalida.ENVIAR_ASK_PERSONAJE_SELECCIONADO(out, this);

    // GCK: Inicializa el motor de juego en el cliente
    GestorSalida.enviar(this, "GCK|1|" + _nombre);

    // =========================================================================
    // 2. ATRIBUTOS Y HABILIDADES
    // =========================================================================
    
    // Estadísticas completas
    GestorSalida.ENVIAR_As_STATS_DEL_PJ(this);
    
    // Capacidad de carga
    GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(this);

    // Lista de hechizos y emotes
    GestorSalida.ENVIAR_SL_LISTA_HECHIZOS(this);
    GestorSalida.enviar(this, "eL" + _emotes + "|0");

    // Canales de chat
    GestorSalida.enviar(this, "cC+" + _canales + "^" + (_cuenta.getRango() > 1 ? "@?" : ""));

    // =========================================================================
    // 3. RESTRICCIONES Y ALINEACIÓN
    // =========================================================================
    
    // AR: Envía restricciones (caminar, agredir, etc)
    GestorSalida.enviar(this, "AR" + Long.toString(getRestriccionesA(), 36));
    
    // ZS y AL: Especialización y alineación de zonas
    GestorSalida.enviar(this, "ZS" + _alineacion);
    GestorSalida.enviar(this, "al|" + MundoDofus.getAlineacionTodasSubareas());

    // =========================================================================
    // 4. BLOQUE SOCIAL (Gremio / Amigos / Esposo)
    // =========================================================================
    
    if (_miembroGremio != null) {
        if (_miembroGremio.getGremio().borrado) {
            setMiembroGremio(null);
            GestorSalida.GAME_SEND_gK_PACKET(this, "KEl Administrador|");
        } else {
            Gremio g = _miembroGremio.getGremio();
            GestorSalida.enviar(this, "gS" + g.getNombre() + "|" + g.getEmblema().replace(',', '|') + "|" + _miembroGremio.analizarDerechos());
            
            // Actualizar tiempos de conexión
            DateFormat formatTime = new SimpleDateFormat("yyyy~MM~dd~HH~mm");
            String fecha = formatTime.format(Calendar.getInstance().getTime());
            g.ultimaConex = fecha;
            _miembroGremio.setUltConeccion(fecha);
        }
    }

    _cuenta.mensajeAAmigos();
    GestorSalida.enviar(this, "FO" + (_mostrarConeccionAmigos ? "+" : "-"));

    // =========================================================================
    // 5. CARGA DEL MUNDO (GDM / MAPA)
    // =========================================================================
    
    // Celdas de Teleport (Custom Mod) -- REMOVED for Modern Client compatibility
    // GestorSalida.enviar(this, "#gm" + this.getMapa().celdasTPs);

    // GDM: Inicia la carga del mapa. Esto activará el paquete 'GI' desde el cliente.
    // Usamos MAPDATA_COMPLETO para enviar la geometria inmediatamente, necesario para loader43.
    GestorSalida.ENVIAR_GDM_MAPDATA_COMPLETO(this);

    // =========================================================================
    // 6. LÓGICA LEGACY Y COMPROBACIONES (Items, Oficios, Niveles)
    // =========================================================================
    
    // Desactivar modo mercante si estaba activo
    if (_esMercante == 1) {
        _mapa.removerMercante(_ID);
        _esMercante = 0;
        GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(_mapa, _ID);
        GestorSQL.SALVAR_MERCANTES(getMapa());
    }

    // Monturas
    if (_montura != null) {
        GestorSalida.ENVIAR_Re_DETALLES_MONTURA(this, "+", _montura);
    }
    GestorSalida.ENVIAR_Rx_EXP_DONADA_MONTURA(this);

    // Bonus de Sets
    ArrayList<Integer> setsCargados = new ArrayList<>();
    for (int i = 0; i < 17; i++) {
        Objeto obj = getObjPosicion(i);
        if (obj != null) {
            int setid = obj.getModelo().getSetID();
            if (setid > 0 && !setsCargados.contains(setid)) {
                if (MundoDofus.getItemSet(setid) != null) {
                    setsCargados.add(setid);
                    GestorSalida.ENVIAR_OS_BONUS_SET(this, setid, getNroObjEquipadosDeSet(setid));
                }
            }
        }
    }

    // Oficios
    if (_statsOficios.size() > 0) {
        enviarPacketsOficios(); // Método extraído para limpiar el código
    }

    // Nivel Inicial y Resets
    verificarNivelYAccionesIniciales();

    // =========================================================================
    // 7. MENSAJES DE BIENVENIDA Y EVENTOS
    // =========================================================================
    
    if (Guerras.estaEnGuerra) {
        GestorSalida.ENVIAR_cs_CHAT_MENSAJE(this, "<b>EVENTO :</b> La guerra de BONTAS vs BRAKS estÁ activa.", Colores.AZUL);
    }
    GestorSalida.ENVIAR_cs_CHAT_MENSAJE(this, Emu.MENSAJE_BIENVENIDA_1, Colores.ROJO);

    enviarAlertasEventos(); // Notificaciones de Invasión, Gema, Mercader, etc.

    // =========================================================================
    // 8. FINALIZACIÓN DE ESTADO
    // =========================================================================
    
    this.primerRefresh = true;
    this._enLinea = true;
    
    // Reconexión en pelea
    if (_pelea != null) {
        _reconectado = true;
        if (_pelea._espectadores.containsKey(getID())) {
            salirEspectador();
        } else {
            return;
        }
    }
    
    // Hechizos por etapa (Sistema Custom)
    if (Emu.SistemaHechizo) {
        enviarHechizosPorEtapa();
    }
}

// --- MÉTODOS DE APOYO PARA NO ENSUCIAR LA FUNCIÓN PRINCIPAL ---

private void enviarPacketsOficios() {
    ArrayList<StatsOficio> listaStatOficios = new ArrayList<>(_statsOficios.values());
    StringBuilder js = new StringBuilder("JS");
    StringBuilder jx = new StringBuilder("JX");
    StringBuilder jo = new StringBuilder();
    
    for (StatsOficio sm : listaStatOficios) {
        js.append(sm.analizarTrabajolOficio());
        jx.append("|").append(sm.getOficio().getID()).append(";").append(sm.getNivel()).append(";").append(sm.getXpString(";")).append(";");
        jo.append("JO").append(sm.getPosicion()).append("|").append(sm.getOpcionBin()).append("|").append(sm.getSlotsPublico()).append((char) 0x00);
    }
    
    GestorSalida.enviar(this, js.toString() + (char) 0x00 + jx.toString() + (char) 0x00 + jo.toString());

    // Herramienta de oficio
    Objeto obj = getObjPosicion(1);
    if (obj != null) {
        for (StatsOficio statOficio : listaStatOficios) {
            Oficio oficio = statOficio.getOficio();
            if (oficio.herramientaValida(obj.getModelo().getID())) {
                GestorSalida.enviar(this, "OT" + oficio.getID());
                String strOficioPub = CentroInfo.trabajosOficioTaller(oficio.getID());
                if (_mapa.esTaller() && _oficioPublico) {
                    GestorSalida.ENVIAR_EW_OFICIO_MODO_INVITACION(_cuenta.getEntradaPersonaje().getOut(), "+", _ID, strOficioPub);
                }
                _stringOficiosPublicos = strOficioPub;
                break;
            }
        }
    }
}

private void enviarAlertasEventos() {
    if (Invasion.mapainv != null) {
        GestorSalida.ENVIAR_cs_CHAT_MENSAJE(this, "<b>Recuerda: </b> Un MEGA ha invadido [" + Invasion.posicion + "]", Colores.ROJO);
    }
    // Lógica de Buffs de Clase (Guerrero/Sacer/Mago)
    if (buffClase != -1) {
        int objId = (buffClase == 1) ? 30944 : (buffClase == 2) ? 30945 : 30946;
        if (getObjPosicion(27) == null || getObjPosicion(27).getModelo().getID() != objId) {
            deleteobjBoost(27);
            objBoost(objId);
        }
    }
}
	
	// poner check cada 15 min que actualice las misiones automaticamente

	public String actualHechizos = "";
	public int buffClase = -1;
	public boolean puedeAbrir = true;
	public boolean avisado = false;
	public boolean continua = false;
	public Date fechacrea = null;

	public void checkTimerGen() {
		if (generador == null) {
			Personaje ps = this;
			generador = new Timer(10000, new ActionListener() {
				int veces = 0;
				int acumulado = 0;

				@Override
				public void actionPerformed(ActionEvent e) {
					if (getCuenta().generadores <= 0 || !ps._enLinea) {
						generador.stop();
						generador = null;
						return;
					}
					ps.getCuenta().tiempopasado += 10;
					if (ps.getCuenta().tiempopasado >= 3600) {
						darKamasGenerador();
						ps.getCuenta().tiempopasado = 0;
						veces = 0;
					}
					veces += 1;
					if (veces >= 80) {
						veces = 0;
						int cuantashice = Math.round(((600 * 300000) / 3600) * getCuenta().generadores);
						acumulado += cuantashice;
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(ps,
								"Tus generadores ya han generado " + acumulado + " kamas.", Colores.NARANJA);
					}
				}
			});
		}
		if (!generador.isRunning()) {
			generador.start();
		}
	}

	private void darKamasGenerador() {
		long kamas = getCuenta().generadores * 50000;
		long curKamas = getKamas();
		long newKamas = curKamas + kamas;
		if (newKamas < 0) {
			newKamas = 0;
		}
		if (newKamas > 2000000000) {
			newKamas = 2000000000;
		}
		setKamas(newKamas);
		if (enLinea()) {
			GestorSalida.ENVIAR_As_STATS_DEL_PJ(this);
		}
		if (getCuenta().generadores > 1) {
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(this, "Has ganado +" + kamas + " kamas por los "
					+ getCuenta().generadores + " generadores de Kamas de tu casa", Colores.NARANJA);
		} else if (getCuenta().generadores == 1) {
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(this,
					"Has ganado +" + kamas + " kamas por el generador de Kamas de tu casa", Colores.NARANJA);
		}
	}

	public void obtenerObjetivos() {
		ObjetivosDiarios.clear();
		magueados.clear();
		Map<Integer, String> objBack = new TreeMap<>();
		List<Entry<Integer, String>> list;
		int vecess = 15;
		if (_resets > 0) {
			list = new ArrayList<>(MundoDofus.ObjetivosDiarios.entrySet());
		} else if (_resets <= 0 && _nivel < 250) {
			list = new ArrayList<>(MundoDofus.ObjetivosFaciles.entrySet());
		} else {
			list = new ArrayList<>(MundoDofus.ObjetivosDiarios.entrySet());
		}
		Collections.shuffle(list);
		int veces = 1;
		// desafio diario
		String stra = MundoDofus.ObjetivosDiarios.get(1);
		Map<Integer, String> desafDiar = new TreeMap<>();
		desafDiar.put(1, stra);
		for (Entry<Integer, String> entry : desafDiar.entrySet()) {
			String val = entry.getValue();
			String tipo = val.split(",")[1];
			int itemnecesario = Integer.parseInt(val.split(",")[3]);
			int cantidadnecesaria = Integer.parseInt(val.split(",")[4]);
			int itemconseguido = 0;
			int finalizado = 0;
			String tiporecompensa = "kamas";
			String valorrecompensa = "100000";
			objBack.put(1, val.split(",")[0] + " [DÍa " + (desafdiario + 1) + "]," + val.split(",")[2] + "," + tipo
					+ "," + itemnecesario + "," + cantidadnecesaria + "," + tiporecompensa + "," + valorrecompensa + ","
					+ val.split(",")[6] + "," + itemconseguido + "," + finalizado + "," + val.split(",")[5]); // nombre,descripcion,tipo,itemnecesario,cantidadnecesaria,tiporecompensa,recompensa,mobid
																												// (id
																												// del
																												// mob,cantidad),itemconseguido,finalizado,magueado
		}
		for (Entry<Integer, String> entry : list) {
			if (entry.getKey() == 1) {
				continue;
			}
			if (veces >= vecess) {
				break;
			}
			veces += 1;
			String val = entry.getValue();
			String tipo = val.split(",")[1];
			int itemnecesario = Integer.parseInt(val.split(",")[3]);
			int cantidadnecesaria = Integer.parseInt(val.split(",")[4]);
			int itemconseguido = 0;
			int finalizado = 0;
			String larecom = randomRecompensa();
			String tiporecompensa = larecom.split(",")[0];
			String valorrecompensa = larecom.split(",")[1];
			objBack.put(veces,
					val.split(",")[0] + "," + val.split(",")[2] + "," + tipo + "," + itemnecesario + ","
							+ cantidadnecesaria + "," + tiporecompensa + "," + valorrecompensa + "," + val.split(",")[6]
							+ "," + itemconseguido + "," + finalizado + "," + val.split(",")[5]); // nombre,descripcion,tipo,itemnecesario,cantidadnecesaria,tiporecompensa,recompensa,mobid
																									// (id del
																									// mob,cantidad),itemconseguido,finalizado,magueado
		}
		actualPrecio = 100;
		ObjetivosDiarios.putAll(objBack);
	}

	private static int randomItem() {
		if (Emu.objetosDiarias.size() <= 0) {
			return -1;
		}
		Random randomGenerator = new Random();
		int item = randomGenerator.nextInt(Emu.objetosDiarias.size());
		if (Emu.objetosDiarias.get(item) == null) {
			return -1;
		}
		int itemrnd = Emu.objetosDiarias.get(item);
		return itemrnd;
	}

	private String randomRecompensa() {// tiporecompensa,valor
		if (_resets > 0) {// TODO:
			int randomtipo = Formulas.getRandomValor(1, 7);
			switch (randomtipo) {
			case 1:// puntosvip
				int randompts = Formulas.getRandomValor(10, 40);
				return "puntosvip," + randompts;
			case 2:// nivel
				if (_resets != 3) {
					int randomlvl = Formulas.getRandomValor(1, 3);
					return "nivel," + randomlvl;
				} else {
					int randomkama = Formulas.getRandomValor(50000, 200000);
					return "kamas," + randomkama;
				}
			case 3:// item
				int elitem = randomItem();
				if (elitem <= 0) {
					int randomkama = Formulas.getRandomValor(50000, 200000);
					return "kamas," + randomkama;
				}
				return "item," + elitem + "-1";
			case 4:// kamas
			case 5:
				int randomkama = Formulas.getRandomValor(50000, 200000);
				return "kamas," + randomkama;
			case 6:// honor
			case 7:
				int randomhonor = Formulas.getRandomValor(5, 22);
				return "honor," + randomhonor;
			}
		} else {
			int randomtipo = Formulas.getRandomValor(1, 10);
			switch (randomtipo) {
			case 1:// puntosvip
			case 2:
			case 3:
				int randompts = Formulas.getRandomValor(30, 60);
				return "puntosvip," + randompts;
			case 4:// nivel
				int randomlvl = Formulas.getRandomValor(7, 12);
				return "nivel," + randomlvl;
			case 5:// item
				int elitem = randomItem();
				if (elitem <= 0) {
					int randomkama = Formulas.getRandomValor(90000, 400000);
					return "kamas," + randomkama;
				}
				return "item," + elitem + "-1";
			case 6:// kamas
			case 7:
			case 8:
			case 9:
				int randomkama = Formulas.getRandomValor(90000, 400000);
				return "kamas," + randomkama;
			case 10:// honor
				int randomhonor = Formulas.getRandomValor(5, 22);
				return "honor," + randomhonor;
			}
		}
		return "kamas,100000";
	}

	public String getObjetivosSave() {
		StringBuilder str = new StringBuilder();
		boolean primero = false;
		Map<Integer, String> ObjetivosDiariox = new TreeMap<>();
		ObjetivosDiariox.putAll(ObjetivosDiarios);
		for (Entry<Integer, String> entry : ObjetivosDiariox.entrySet()) {
			if (primero) {
				str.append(";");
			}
			String val = entry.getValue();
			str.append(entry.getKey() + "*" + val);// nombre,descripcion,tipo,itemnecesario,cantidadnecesaria,tiporecompensa,recompensa
													// (en item es itemid-cant),mobid (id del
													// mob-cantidad),itemconseguido,finalizado,magueado
			primero = true;
		}
		return str.toString();
	}

	public int actualPrecio = 200;

	public void getObjetivo(int objet) {
		String entry = ObjetivosDiarios.get(objet);
		if (entry == null) {
			return;
		}
		GestorSalida.enviar(this, "#kx" + entry + "," + actualPrecio + "," + objet);
	}

	public void getListaObjetivos(boolean actualiza) {
		StringBuilder str = new StringBuilder();
		boolean primero = false;
		for (Entry<Integer, String> entry : ObjetivosDiarios.entrySet()) {
			if (primero) {
				str.append(";");
			}
			String val = entry.getValue();
			str.append(entry.getKey() + "," + val);// nombre,descripcion,tipo,itemnecesario,cantidadnecesaria,tiporecompensa,recompensa
													// (en item es itemid-cant),mobid (id del
													// mob-cantidad),itemconseguido,finalizado,magueado
			primero = true;
		}
		if (actualiza) {
			str.append(1);
		} else {
			str.append(0);
		}
		GestorSalida.enviar(this, "#kr" + str.toString());
	}

	public int getCantComplet() {
		int veces = 0;
		for (Entry<Integer, String> entry : ObjetivosDiarios.entrySet()) {
			String val = entry.getValue();
			int finalizado = Integer.parseInt(val.split(",")[9]);
			if (finalizado == 1) {
				veces += 1;
			}
		}
		return veces;
	}

	boolean acaboMision(int objet) {
		String entry = ObjetivosDiarios.get(objet);
		if (entry == null) {
			return false;
		}
		String[] str = entry.split(",");
		int finalizado = Integer.parseInt(str[9]);
		if (finalizado == 1) {
			return true;
		}
		return false;
	}

	public void completarObjetivos(int objet) {
		String entry = ObjetivosDiarios.get(objet);
		if (entry == null) {
			return;
		}
		String[] str = entry.split(",");
		int finalizado = Integer.parseInt(str[9]);
		if (finalizado == 1) {
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(this, "Este objetivo ya estÁ completo", Colores.ROJO);
			return;
		}
		if (objet == 1) {
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(this, "Este objetivo no se puede completar con Puntos VIP",
					Colores.ROJO);
			return;
		}
		int mispuntos = GestorSQL.getPuntosCuenta(this.getCuentaID());
		if (mispuntos < actualPrecio) {
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(this,
					"Necesitas <b>" + actualPrecio + "</b> Puntos VIP para completar este objetivo.", Colores.AZUL);
			return;
		}
		int puntosnuevos = mispuntos - actualPrecio;
		GestorSQL.setPuntoCuenta(puntosnuevos, this.getCuentaID());
		if (str[2].equals("recolectar")) {
			updateObjetivoRecolecta(Integer.parseInt(str[3]), Integer.parseInt(str[4]), "recolectar", true);
		} else if (str[2].equals("mazmorra")) {
			updateObjetivoRecolecta(Integer.parseInt(str[3]), Integer.parseInt(str[4]), "mazmorra", true);
		} else if (str[2].equals("matar")) {
			String valormob = str[7];
			int mobid = Integer.parseInt(valormob.split("-")[0]);
			int mobcant = Integer.parseInt(valormob.split("-")[1]);
			Map<Integer, Integer> mobs = new TreeMap<>();// Mob matado de misiones
			mobs.put(mobid, mobcant);
			setMobMatado(mobs);
			updateObjetivoRecolecta(0, 0, "matar", true);
		} else if (str[2].equals("maguear")) {
			String magueo = "";
			try {
				magueo = str[10];
			} catch (Exception e) {
				magueo = "";
			}
			if (magueo != "") {
				int cantidadnecesaria = Integer.parseInt(str[4]);
				int runaid = Integer.parseInt(magueo.split("-")[0]);
				magueados.put(runaid, cantidadnecesaria);
				updateObjetivoRecolecta(0, 0, "maguear", true);
			}
		}
		actualPrecio += 150;
		this.PuntosPrestigio += Emu.ptsDailyQuest;
		GestorSalida.ENVIAR_cs_CHAT_MENSAJE(this, "Objetivo completado con Éxito.", Colores.VERDE);
	}

	void updateObjetivoRecolecta(int itemid, int cant, String tipox, boolean completa) {
		Map<Integer, String> objBack = new TreeMap<>();
		boolean completado = false;
		String tiporecompensax = "";
		String valorrecompensax = "";
		for (Entry<Integer, String> entry : ObjetivosDiarios.entrySet()) {
			String val = entry.getValue();
			String tipo = val.split(",")[2];
			int itemnecesario = Integer.parseInt(val.split(",")[3]);
			int cantidadnecesaria = Integer.parseInt(val.split(",")[4]);
			int itemconseguido = Integer.parseInt(val.split(",")[8]);
			int finalizado = Integer.parseInt(val.split(",")[9]);
			String magueo = "";
			try {
				magueo = val.split(",")[10];
			} catch (Exception e) {
				magueo = "";
			}
			String valormob = val.split(",")[7];
			String tiporecompensa = val.split(",")[5];
			String valorrecompensa = val.split(",")[6];
			if (tipo.equals("recolectar") && tipox.equals("recolectar")) {
				if (itemnecesario == itemid && finalizado == 0) {
					itemconseguido += cant;
					if (itemconseguido >= cantidadnecesaria) {
						finalizado = 1;
						itemconseguido = cantidadnecesaria;
						tiporecompensax = val.split(",")[5];
						valorrecompensax = val.split(",")[6];
						completado = true;
					}
				}
			} else if (tipo.equals("matar") && tipox.equals("matar")) {
				Map<Integer, Integer> strm = getMobMatado();
				int mobid = Integer.parseInt(valormob.split("-")[0]);
				int mobcant = Integer.parseInt(valormob.split("-")[1]);
				if (strm.containsKey(mobid) && finalizado == 0) {
					int cantkm = strm.get(mobid);
					itemconseguido += cantkm;
					if (itemconseguido >= mobcant) {
						finalizado = 1;
						itemconseguido = mobcant;
						if (entry.getKey() == 1) {
							desafdiario += 1;
						}
						tiporecompensax = val.split(",")[5];
						valorrecompensax = val.split(",")[6];
						completado = true;
					}
				}
			} else if (tipo.equals("maguear") && tipox.equals("maguear")) {
				if (finalizado == 0) {
					for (String str : magueo.split(";")) { // 1557
						int runaid = Integer.parseInt(str.split("-")[0]);
						if (magueados.containsKey(runaid)) {
							int tiene = magueados.get(runaid);
							itemconseguido += tiene;
							magueados.remove(runaid);
							if (itemconseguido >= cantidadnecesaria) {
								finalizado = 1;
								itemconseguido = cantidadnecesaria;
								tiporecompensax = val.split(",")[5];
								valorrecompensax = val.split(",")[6];
								completado = true;
								break;
							}
						}
					}
				}
			} else if (tipo.equals("mazmorra") && tipox.equals("mazmorra")) {
				if (itemnecesario == itemid && finalizado == 0) {
					itemconseguido += cant;
					if (itemconseguido >= cantidadnecesaria) {
						finalizado = 1;
						itemconseguido = cantidadnecesaria;
						tiporecompensax = val.split(",")[5];
						valorrecompensax = val.split(",")[6];
						completado = true;
					}
				}
			}
			objBack.put(entry.getKey(),
					val.split(",")[0] + "," + val.split(",")[1] + "," + tipo + "," + itemnecesario + ","
							+ cantidadnecesaria + "," + tiporecompensa + "," + valorrecompensa + "," + valormob + ","
							+ itemconseguido + "," + finalizado + "," + magueo); // nombre,descripcion,tipo,itemnecesario,cantidadnecesaria,tiporecompensa,recompensa,mobid
																					// (id del
																					// mob-cantidad),itemconseguido,finalizado,magueado
		}
		_ultMobMatado.clear();
		ObjetivosDiarios.clear();
		ObjetivosDiarios.putAll(objBack);
		darRecompensaObj(tiporecompensax, valorrecompensax, completa);
		if (getCantComplet() == ObjetivosDiarios.size() && completado) {
			int itemid1 = 70200;
			int cantid = 1;
			ObjetoModelo OM = MundoDofus.getObjModelo(itemid1);
			if (OM == null) {
				return;
			}
			if (cantid < 1) {
				cantid = 1;
			}
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(this,
					"Acabas de completar todos los Objetivos Diarios y has ganado x1 <b>Regalo Legendario Misterioso</b>",
					Colores.AZUL);
			Objeto obj = OM.crearObjDesdeModelo(cantid, false);
			if (!this.addObjetoSimilar(obj, true, -1)) {
				MundoDofus.addObjeto(obj, true);
				this.addObjetoPut(obj);
				GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(this, obj);
			}
		}
	}

	Map<Integer, Integer> magueados = new TreeMap<>();

	private void darRecompensaObj(String tiporecompensa, String valorre, boolean completa) {
		switch (tiporecompensa) {
		case "puntosvip":
			int cantp = Integer.parseInt(valorre);
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(this,
					"Acabas de completar un Objetivo Diario y has ganado +" + cantp + " Puntos VIP", Colores.NARANJA);
			GestorSQL.setPuntoCuenta(GestorSQL.getPuntosCuenta(this.getCuentaID()) + cantp, this.getCuentaID());
			break;
		case "nivel":
			if (_resets != 3) {
				int cantpd = Integer.parseInt(valorre) + 1;
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(this,
						"Acabas de completar un Objetivo Diario y has ganado +" + cantpd + " nivel(es)",
						Colores.NARANJA);
				int anteslvl = this.getNivel();
				for (int i = 1; i < cantpd; i++) { // 5==4
					this.subirNivel(true, false);
				}
				if (anteslvl != this.getNivel()) {
					GestorSalida.ENVIAR_SL_LISTA_HECHIZOS(this);
					GestorSalida.ENVIAR_AN_MENSAJE_NUEVO_NIVEL(this.getCuenta().getEntradaPersonaje().getOut(),
							this.getNivel());
					GestorSalida.ENVIAR_As_STATS_DEL_PJ(this);
					this.actualizarInfoGrupo();
					if (this.getGremio() != null) {
						this.getMiembroGremio().setNivel(this.getNivel());
					}
				}
			} else {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(this,
						"Acabas de completar un Objetivo Diario y has ganado +15 Puntos VIP, (los R3 no pueden ganar nivel)",
						Colores.NARANJA);
				GestorSQL.setPuntoCuenta(GestorSQL.getPuntosCuenta(this.getCuentaID()) + 15, this.getCuentaID());
			}
			break;
		case "item":
			int itemid = Integer.parseInt(valorre.split("-")[0]);
			int cantid = Integer.parseInt(valorre.split("-")[1]);
			ObjetoModelo OM = MundoDofus.getObjModelo(itemid);
			if (OM == null) {
				return;
			}
			if (cantid < 1) {
				cantid = 1;
			}
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(this,
					"Acabas de completar un Objetivo Diario y has ganado x" + cantid + " " + OM.getNombre(),
					Colores.NARANJA);
			Objeto obj = OM.crearObjDesdeModelo(cantid, false);
			if (!this.addObjetoSimilar(obj, true, -1)) {
				MundoDofus.addObjeto(obj, true);
				this.addObjetoPut(obj);
				GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(this, obj);
			}
			break;
		case "honor":
			if (this.getAlineacion() == 0) {
				long curKamas = getKamas();
				long newKamas = curKamas + 300000;
				if (newKamas < 0) {
					newKamas = 0;
				}
				if (newKamas > 2000000000) {
					newKamas = 2000000000;
				}
				setKamas(newKamas);
				if (enLinea()) {
					GestorSalida.ENVIAR_As_STATS_DEL_PJ(this);
				}
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(this,
						"Acabas de completar un Objetivo Diario y has ganador honor, al no tener alineaciÓn, te compensamos con +300.000 Kamas",
						Colores.NARANJA);
			} else {
				int honor = Integer.parseInt(valorre);
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(this,
						"Acabas de completar un Objetivo Diario y has ganado +" + honor + " puntos de honor",
						Colores.NARANJA);
				this.addHonor(honor);
			}
			break;
		case "kamas":
			int kamas = Integer.parseInt(valorre);
			long curKamas = getKamas();
			long newKamas = curKamas + kamas;
			if (newKamas < 0) {
				newKamas = 0;
			}
			if (newKamas > 2000000000) {
				newKamas = 2000000000;
			}
			setKamas(newKamas);
			if (enLinea()) {
				GestorSalida.ENVIAR_As_STATS_DEL_PJ(this);
			}
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(this,
					"Acabas de completar un Objetivo Diario y has ganado +" + kamas + " kamas", Colores.NARANJA);
			break;
		}
		if (!tiporecompensa.equals("")) {
			getListaObjetivos(completa);
		}
	}

	void restarVidaMascota(Mascota mascota) {
		Objeto masc = null;
		if (mascota == null) {
			masc = getObjPosicion(8);
		} else {
			masc = MundoDofus.getObjeto(mascota.getID());
		}
		boolean sigue = true;
		if (masc != null) {
			switch (masc.getModelo().getID()) {
			case 70006:
				sigue = false;
				break;
			}
		}
		if (masc != null && sigue) {
			int idMascota = masc.getID();
			if (mascota == null) {
				mascota = MundoDofus.getMascota(idMascota);
			}
			if (mascota == null) {
				return;
			}
			if (mascota.getPDV() > 1) {
				mascota.setPDV(mascota.getPDV() - 1);
				StringBuilder newstats = new StringBuilder("");
				String vidamasc = "320#0#0#" + Integer.toHexString(mascota.getPDV());
				boolean primeros = false;
				for (String str : mascota.getStringStats().split(",")) {
					if (primeros) {
						newstats.append(",");
					}
					String elstat = str.split("#")[0];
					if (elstat.equals("320")) {
						newstats.append(vidamasc);
					} else {
						newstats.append(str);
					}
					primeros = true;
				}
				mascota.setStringStats(newstats.toString());
				GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETOMASCO(this, masc);
			} else {
				mascota.setPDV(0);
				GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(this, idMascota);
				int nuevoModelo = CentroInfo.fantasmaMascota(masc.getIDModelo());
				if (nuevoModelo != 0) {
					masc.setPosicion(-1);
					masc.setIDModelo(nuevoModelo);
					masc.clearTodo();
					StringBuilder newstats = new StringBuilder("");
					String vidamasc = "320#0#0#00";
					boolean primeros = false;
					for (String str : mascota.getStringStats().split(",")) {
						if (primeros) {
							newstats.append(",");
						}
						String elstat = str.split("#")[0];
						if (elstat.equals("327") || elstat.equals("326") || elstat.equals("328")) {

						} else if (elstat.equals("320")) {
							newstats.append(vidamasc);
						} else {
							newstats.append(str);
						}
						primeros = true;
					}
					masc.convertirStringAStats(newstats.toString());
					GestorSalida.ENVIAR_OAKO_APARECER_OBJETOMASC(this, masc);
					GestorSalida.ENVIAR_Oa_CAMBIAR_ROPA(this.getMapa(), this);
					_mascota = null;
				} else {
					borrarObjetoRemove(idMascota);
					MundoDofus.eliminarObjeto(idMascota);
					_mascota = null;
				}
				refrescarVida(false);
				GestorSalida.ENVIAR_As_STATS_DEL_PJ(this);
				GestorSalida.ENVIAR_Im_INFORMACION(this, "154");
			}
		}
	}

	public void comprobarLoot() {
		try {
			String fechaexp = this.getCuenta().tiempoLoot;
			Personaje perso = this;
			Timer timer = new Timer(10000, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (!perso.enLinea()) {
						((Timer) e.getSource()).stop();
						return;
					}
					if ((perso.getPelea() != null) || fechaexp.equals("0") || fechaexp.equals("")) {
						return;
					}
					String anteshora = fechaexp;
					Date date = new Date();
					Calendar cal2 = Calendar.getInstance();
					cal2.setTime(date);
					Calendar calzz = Calendar.getInstance();
					SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
					try {
						calzz.setTime(sdf.parse(anteshora));
					} catch (java.text.ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					if (cal2.compareTo(calzz) >= 0) {
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso,
								"Acabas de recibir un regalo Misterioso :3, recibirÁs otro en " + Emu.tiempoLootBox
										+ " minutos",
								Colores.VERDE);
						ObjetoModelo OM = MundoDofus.getObjModelo(70051);
						Objeto obj = OM.crearObjDesdeModelo(1, false);
						if (!perso.addObjetoSimilar(obj, true, -1)) {
							MundoDofus.addObjeto(obj, true);
							perso.addObjetoPut(obj);
							GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(perso, obj);
						}
						Calendar cal = Calendar.getInstance();
						cal.setTime(date);
						cal.add(Calendar.MINUTE, Emu.tiempoLootBox);
						Date expirationDate = cal.getTime();
						String exp = String.valueOf(expirationDate);
						perso.getCuenta().tiempoLoot = exp;
						GestorSQL.setTiempoLootCuenta(exp, perso.getCuentaID());
						((Timer) e.getSource()).stop();
						return;
					}
				}
			});
			timer.start();
		} catch (Exception e) {
			System.out.println("2BUG AL CERRAR UN TIMER " + this.getNombre());
		}
	}

	Integer regaloRandom() {
		Random randomGenerator = new Random();
		int item = randomGenerator.nextInt(Emu.regaloMis.size());
		int itemrnd = Emu.regaloMis.get(item);
		return itemrnd;
	}

	public void crearJuegoPJ() {
		if (_cuenta.getEntradaPersonaje() == null) {
			return;
		}
		PrintWriter out = _cuenta.getEntradaPersonaje().getOut();
		if (out == null)
		 {
			return;// #bh
		}
		StringBuilder str = new StringBuilder();
		str.append("GCK|1|" + (char) 0x00);// TODO: CUIDADO CON EL CODIGO
		str.append(stringStatsPacket() + (char) 0x00);
		str.append("GDM|" + _mapa.getID() + "|" + _mapa.getFecha());
		setMapaGDM(_mapa);
		GestorSalida.enviar(out, str.toString());
		if (_pelea != null) {
			return;
		}
		if (primerRefresh) {
			primerRefresh = false;
			_mapa.addJugador(this, true);
		}
	}

	public void setHechizos(Map<Integer, StatsHechizos> hechizos) {
		_hechizos.clear();
		_lugaresHechizos.clear();
		_hechizos = hechizos;
		_lugaresHechizos = CentroInfo.getLugaresHechizosIniciales(_clase);
	}

	void cambiarSexo() {
		if (_sexo == 1) {
			_sexo = 0;
		} else {
			_sexo = 1;
		}
	}

	public void setPtosHechizos(int puntos) {
		_puntosHechizo = puntos;
	}

	public boolean enLinea() {
		return _enLinea;
	}

	public void setGrupo(Grupo grupo) {
		_grupo = grupo;
	}

	public Grupo getGrupo() {
		return _grupo;
	}

	public boolean aprenderHechizo(int hechizoID, int nivel, boolean conectando, boolean enviar) {
		if (_encarnacion != null && !conectando) {
			return false;
		}
		Hechizo aprender = MundoDofus.getHechizo(hechizoID);
		if (aprender == null || aprender.getStatsPorNivel(nivel) == null) {
			System.out.println("[ERROR]Hechizo " + hechizoID + " nivel " + nivel + " no ubicado.");
			return false;
		}
		_hechizos.remove(hechizoID);
		_hechizos.put(hechizoID, aprender.getStatsPorNivel(nivel));
		if (enviar) {
			GestorSalida.ENVIAR_SL_LISTA_HECHIZOS(this);
			GestorSalida.ENVIAR_Im_INFORMACION(this, "03;" + hechizoID);
		}
		return true;
	}

	public String analizarHechizosABD() {
		StringBuilder hechizos = new StringBuilder("");
		if (_hechizos.isEmpty()) {
			return "";
		}
		for (int key : _hechizos.keySet()) {
			StatsHechizos SH = _hechizos.get(key);
			hechizos.append(SH.getHechizoID() + ";" + SH.getNivel() + ";");
			if (_lugaresHechizos.get(key) != null) {
				hechizos.append(_lugaresHechizos.get(key));
			} else {
				hechizos.append("_");
			}
			hechizos.append(",");
		}
		return hechizos.substring(0, hechizos.length() - 1);
	}

	private void analizarPosHechizos(String str) {
		String[] hechizos = str.split(",");
		for (String e : hechizos) {
			try {
				int id = Integer.parseInt(e.split(";")[0]);
				int nivel = Integer.parseInt(e.split(";")[1]);
				char pos = e.split(";")[2].charAt(0);
				aprenderHechizo(id, nivel, true, false);
				_lugaresHechizos.put(id, pos);
			} catch (NumberFormatException e1) {
				continue;
			}
		}
	}

	public String getPtoSalvada() {
		return _puntoSalvado;
	}

	public void setOficioPublico(boolean publico) {
		_oficioPublico = publico;
	}

	public void setStrOficiosPublicos(String oficios) {
		_stringOficiosPublicos = oficios;
	}

	public boolean getOficioPublico() {
		return _oficioPublico;
	}

	public String getStringOficiosPublicos() {
		return _stringOficiosPublicos;
	}

	public void setSalvarZaap(String savePos) {
		_puntoSalvado = savePos;
	}

	public int getIntercambiandoCon() {
		return _intercambioCon;
	}

	public void setIntercambiandoCon(int intercambiando) {
		_intercambioCon = intercambiando;
	}

	public int getConversandoCon() {
		return _conversandoCon;
	}

	public void setConversandoCon(int conversando) {
		_conversandoCon = conversando;
	}

	public long getKamas() {
		return _kamas;
	}

	public void setKamas(long l) {
		if (l < 0) {
			l = 0;
		}
		_kamas = l;
	}

	public Cuenta getCuenta() {
		return _cuenta;
	}

	public int getPuntosHechizos() {
		return _puntosHechizo;
	}

	public Gremio getGremio() {
		if (_miembroGremio == null) {
			return null;
		}
		return _miembroGremio.getGremio();
	}

	public void setMiembroGremio(MiembroGremio gremio) {
		_miembroGremio = gremio;
	}

	boolean estaListo() {
		return _listo;
	}

	public void setListo(boolean listo) {
		_listo = listo;
	}

	public int getDueloID() {
		return _dueloID;
	}

	public Pelea getPelea() {
		return _pelea;
	}

	public int getPeleaNR() {
		if (getPelea() == null) {
			return 0;
		} else {
			return 1;
		}
	}

	public void setDueloID(int dueloID) {
		_dueloID = dueloID;
	}

	public int getEnergia() {
		return _energia;
	}

	public boolean mostrarConeccionAmigo() {
		return _mostrarConeccionAmigos;
	}

	public boolean mostrarAlas() {
		return _mostrarAlas;
	}

	public String getCanal() {
		return _canales;
	}

	public boolean esTumba() {
		return _esTumba;
	}

	public void setRestriccionesA(int restr) {
		_restriccionesA = restr;
		efectuarRestriccionesA();
		GestorSalida.ENVIAR_AR_RESTRICCIONES_PERSONAJE(_cuenta.getEntradaPersonaje().getOut(),
				Integer.toString(_restriccionesA, 36));
	}

	public void setRestriccionesB(int restr) {
		_restriccionesB = restr;
		efectuarRestriccionesB();
	}

	private void convertirTumba() {
		try {
			_gfxID = _clase * 10 + 3;
			_esFantasma = false;
			_esTumba = true;
			_puedeAgredir = false;
			_puedeSerAgredido = false;
			_puedeSerDesafiado = false;
			_puedeHacerIntercambio = false;
			_puedeIntercambiar = false;
			_puedeHablarNPC = false;
			_puedeMercante = false;
			_puedeInteractuarRecaudador = false;
			_puedeInteractuarPrisma = false;
			_puedeUsarObjetos = false;
			_forzadoCaminar = true;
			_esLento = true;
			_ocupado = true;
			_puedeAtacarAMutante = false;
			_puedeDesafiar = false;
			_puedeSerAtacado = false;
			_puedeInteractuarObjetos = false;
			GestorSalida.ENVIAR_GM_REFRESCAR_PJ_EN_MAPA(_mapa, this);
			GestorSalida.ENVIAR_AR_RESTRICCIONES_PERSONAJE(_cuenta.getEntradaPersonaje().getOut(),
					Integer.toString(getRestriccionesA(), 36));
			GestorSalida.ENVIAR_M1_MENSAJE_SERVER(this, "12", "", "");
		} catch (Exception e) {
		}
	}

	public void agregarEnergia(int energia) {
		int exEnergia = _energia;
		_energia += energia;
		if (_energia > 10000) {
			_energia = 10000;
		}
		if (_esFantasma && exEnergia <= 0 && _energia > 0) {
			if (_encarnacion != null) {
				_gfxID = _encarnacion.getGfx();
			} else {
				deformar();
			}
			_energia = energia;
			_esTumba = false;
			_esFantasma = false;
			_puedeAgredir = true;
			_puedeSerAgredido = true;
			_puedeSerDesafiado = true;
			_puedeHacerIntercambio = true;
			_puedeIntercambiar = true;
			_puedeHablarNPC = true;
			_puedeMercante = true;
			_puedeInteractuarRecaudador = true;
			_puedeInteractuarPrisma = true;
			_puedeUsarObjetos = true;
			_esLento = false;
			_ocupado = false;
			_forzadoCaminar = false;
			_puedeAtacarAMutante = true;
			_puedeDesafiar = true;
			_puedeSerAtacado = true;
			_puedeInteractuarObjetos = true;
			GestorSalida.ENVIAR_As_STATS_DEL_PJ(this);
			GestorSalida.ENVIAR_GM_REFRESCAR_PJ_EN_MAPA(_mapa, this);
			GestorSalida.ENVIAR_AR_RESTRICCIONES_PERSONAJE(_cuenta.getEntradaPersonaje().getOut(),
					Long.toString(getRestriccionesA(), 36));
		}
	}

	public void restarEnergia(int energia) {
		_energia -= energia;
		if (_energia <= 0) {
			convertirTumba();
		} else if (_energia < 1500) {
			GestorSalida.ENVIAR_M1_MENSAJE_SERVER(this, "11", energia + "", "");
		}
	}

	public void setFantasma() {
		_gfxID = 8004;
		_esTumba = false;
		_esFantasma = true;
		_puedeAgredir = false;
		_puedeSerAgredido = false;
		_puedeSerDesafiado = false;
		_puedeHacerIntercambio = false;
		_puedeIntercambiar = false;
		_puedeHablarNPC = false;
		_puedeMercante = false;
		_puedeInteractuarRecaudador = false;
		_puedeInteractuarPrisma = false;
		_puedeUsarObjetos = false;
		_forzadoCaminar = true;
		_esLento = true;
		_ocupado = true;
		_puedeAtacarAMutante = false;
		_puedeDesafiar = false;
		_puedeSerAtacado = false;
		_puedeInteractuarObjetos = false;
		GestorSalida.ENVIAR_GM_REFRESCAR_PJ_EN_MAPA(_mapa, this);
		GestorSalida.enviar(this, "GDZ|+239;18;1");
		GestorSalida.enviar(this,
				"IH-11;-54|2;-12|-41;-17|5;-9|25;-4|36;5|12;12|10;19|-10;13|-14;31|-43;0|-60;-3|-58;18|24;-43|27;-33");
		GestorSalida.ENVIAR_AR_RESTRICCIONES_PERSONAJE(_cuenta.getEntradaPersonaje().getOut(),
				Long.toString(getRestriccionesA(), 36));
		teleport((short) 268, 297);
		GestorSalida.ENVIAR_M1_MENSAJE_SERVER(this, "15", "", "");
	}

	public void setRevivir() {
		if (_encarnacion != null) {
			_gfxID = _encarnacion.getGfx();
		} else {
			deformar();
		}
		_energia = 1000;
		_esTumba = false;
		_esFantasma = false;
		_puedeAgredir = true;
		_puedeSerAgredido = true;
		_puedeSerDesafiado = true;
		_puedeHacerIntercambio = true;
		_puedeIntercambiar = true;
		_puedeHablarNPC = true;
		_puedeMercante = true;
		_puedeInteractuarRecaudador = true;
		_puedeInteractuarPrisma = true;
		_puedeUsarObjetos = true;
		_esLento = false;
		_ocupado = false;
		_forzadoCaminar = false;
		_puedeAtacarAMutante = true;
		_puedeDesafiar = true;
		_puedeSerAtacado = true;
		_puedeInteractuarObjetos = true;
		GestorSalida.ENVIAR_As_STATS_DEL_PJ(this);
		GestorSalida.ENVIAR_GM_REFRESCAR_PJ_EN_MAPA(_mapa, this);
		GestorSalida.enviar(this, "IH");
		GestorSalida.ENVIAR_AR_RESTRICCIONES_PERSONAJE(_cuenta.getEntradaPersonaje().getOut(),
				Long.toString(getRestriccionesA(), 36));
	}

	public int getNivel() {
		return _nivel;
	}

	public void setNivel(int nivel) {
		_nivel = nivel;
	}

	public long getExperiencia() {
		if (_experiencia > MundoDofus.getExpNivel(Emu.MAX_NIVEL)._personaje) {
			_experiencia = MundoDofus.getExpNivel(Emu.MAX_NIVEL)._personaje;
		}
		return _experiencia;
	}

	public Celda getCelda() {
		return _celda;
	}

	Celda celdaanteriorx = _celda;

	public void setCelda(Celda celda) {
		_mapa.borrarJugador(this);
		celdaanteriorx = _celda;
		_celda = celda;
		_mapa.addJugador(this, celda.getID());
	}

	public int getTalla() {
		return _talla;
	}

	public void setTalla(int talla) {
		_talla = talla;
	}

	void caramelosRes(Pelea pelea) {
		try {
			if (getPelea() != null && pelea.getEstado() >= 3) {
				for (int i = 20; i < 21; i++) {
					Objeto obj = getObjPosicion(i);
					if ((obj == null) || obj.getIDModelo() == 81695 || obj.getIDModelo() == 81696) {
						continue;
					}
					int idObj = obj.getID();
					String stats = obj.convertirStatsAString(0);
					String[] arg = stats.split(",");
					obj.clearTodo();
					try {
						for (String efec : arg) {
							String[] val = efec.split("#");
							if (Integer.parseInt(val[0], 16) == 811) {
								int turnos = Integer.parseInt(val[3], 16);
								if (turnos <= 1) {
									borrarObjetoRemove(idObj);
									MundoDofus.eliminarObjeto(idObj);
									GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(this, idObj);
									if (i == 21) {
										_bendEfecto = 0;
										_bendHechizo = 0;
										_bendModif = 0;
									}
									Thread.sleep(200);
								} else {
									String antiguo = "32b#0#0#" + Integer.toString(turnos, 16);
									String nuevo = "32b#0#0#" + Integer.toString(turnos - 1, 16);
									stats = stats.replace(antiguo, nuevo);
									obj.convertirStringAStats(stats);
									GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(this, obj);
									Thread.sleep(200);
								}
							}
						}
					} catch (Exception e) {
						if (i == 21) {
							this.deformar();
						}
						borrarObjetoRemove(idObj);
						MundoDofus.eliminarObjeto(idObj);
						GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(this, idObj);
					}
				}
			}
		} catch (Exception e) {
		}
	}

	public void setPelea(Pelea pelea) {
		_pelea = pelea;
		if (pelea == null) {
			return;
		}
		if (_montando && _montura != null) {
			_montura.energiaPerdida(20);
		}
	}

	public int getGfxID() {
		return _gfxID;
	}

	public void setGfxID(int gfxid) {
		_gfxID = gfxid;
	}

	public void deformar() {
		_gfxID = _clase * 10 + _sexo;
	}

	public int getID() {
		return _ID;
	}

	public Mapa getMapa() {
		return _mapa;
	}

	public String getNombre() {
		return _nombre;
	}

	public boolean estaOcupado() {
		return _ocupado;
	}

	public void setOcupado(boolean ocupado) {
		_ocupado = ocupado;
	}

	public boolean estaSentado() {
		return _sentado;
	}

	public int getSexo() {
		return _sexo;
	}

	public int getClase(boolean original) {
		if (_encarnacion != null && !original) {
			return _encarnacion.getClase();
		}
		return _clase;
	}

	public void setClase(int clase) {
		_clase = clase;
	}

	public void setExperiencia(long exp) {
		_experiencia = exp;
	}

	public int getColor1() {
		return _color1;
	}

	public int getColor2() {
		return _color2;
	}

	public Stats getBaseStats() {
		return _baseStats;
	}

	public int getColor3() {
		return _color3;
	}

	public int getCapital() {
		return _capital;
	}

	public void resetearStats() {
		_baseStats.addUnStat(125, -_baseStats.getEfecto(125) + _scrollVitalidad);
		_baseStats.addUnStat(124, -_baseStats.getEfecto(124) + _scrollSabiduria);
		_baseStats.addUnStat(118, -_baseStats.getEfecto(118) + _scrollFuerza);
		_baseStats.addUnStat(123, -_baseStats.getEfecto(123) + _scrollSuerte);
		_baseStats.addUnStat(119, -_baseStats.getEfecto(119) + _scrollAgilidad);
		_baseStats.addUnStat(126, -_baseStats.getEfecto(126) + _scrollInteligencia);
	}

	public boolean tieneHechizoID(int hechizo) {
		if (_encarnacion != null) {
			return _encarnacion.tieneHechizoID(hechizo);
		}
		return _hechizos.get(hechizo) != null;
	}

	public boolean boostearHechizo(int hechizoID) {
		if (_encarnacion != null || _hechizos.get(hechizoID) == null) {
			return false;
		}
		int antNivel = _hechizos.get(hechizoID).getNivel();
		if (antNivel == 6) {
			return false;
		}
		if (_puntosHechizo >= antNivel
				&& MundoDofus.getHechizo(hechizoID).getStatsPorNivel(antNivel + 1).getReqNivel() <= _nivel) {
			if (aprenderHechizo(hechizoID, antNivel + 1, false, false)) {
				_puntosHechizo -= antNivel;
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean nerfearHechizo(int hechizoID) {
		if (_encarnacion != null || _hechizos.get(hechizoID) == null) {
			return false;
		}
		int antNivel = _hechizos.get(hechizoID).getNivel();
		if (antNivel <= 1) {
			return false;
		}
		if (aprenderHechizo(hechizoID, (antNivel - 1), false, false)) {
			int total = 0;
			for (int i = (antNivel - 1); i < antNivel; i++) {
				total += i;
			}
			_puntosHechizo += total;
			return true;
		}
		return false;
	}

	public boolean olvidarHechizo(int hechizoID) {
		if (_encarnacion != null || _hechizos.get(hechizoID) == null) {
			return false;
		}
		int antNivel = _hechizos.get(hechizoID).getNivel();
		if (antNivel <= 1) {
			return false;
		}
		if (aprenderHechizo(hechizoID, 1, false, false)) {
			int total = 0;
			for (int i = 1; i < antNivel; i++) {
				total += i;
			}
			_puntosHechizo += total;
			return true;
		}
		return false;
	}

	public String stringListaHechizos() {
		if (_encarnacion != null) {
			return _encarnacion.stringListaHechizos();
		}
		StringBuilder str = new StringBuilder("");
		for (StatsHechizos SH : _hechizos.values()) {
			if (_lugaresHechizos.get(SH.getHechizoID()) == null) {
				str.append(SH.getHechizoID() + "~" + SH.getNivel() + "~_;");
			} else {
				str.append(
						SH.getHechizoID() + "~" + SH.getNivel() + "~" + _lugaresHechizos.get(SH.getHechizoID()) + ";");
			}
		}
		return str.toString();
	}

	public void setPosHechizo(int hechizo, char pos, boolean salvar) {
		if (_encarnacion != null) {
			_encarnacion.setPosHechizo(hechizo, pos);
			return;
		}
		reemplazarHechizoEnPos(pos);
		_lugaresHechizos.remove(hechizo);
		_lugaresHechizos.put(hechizo, pos);
		if (salvar) {
			GestorSQL.SALVAR_PERSONAJE(this, false);
		}
	}

	private void reemplazarHechizoEnPos(char pos) {
		for (int key : _hechizos.keySet()) {
			if (_lugaresHechizos.get(key) != null) {
				if (_lugaresHechizos.get(key).equals(pos)) {
					_lugaresHechizos.remove(key);
				}
			}
		}
	}

	public StatsHechizos getStatsHechizo(int hechizoID) {
		if (_encarnacion != null) {
			return _encarnacion.getStatsHechizo(hechizoID);
		}
		return _hechizos.get(hechizoID);
	}

	public String stringParaListaPJsServer() {
	    StringBuilder str = new StringBuilder();
	    str.append(_ID).append(";");
	    str.append(_nombre).append(";");
	    str.append(_nivel).append(";");
	    str.append(_gfxID).append(";");
	    str.append((_color1 != -1 ? Integer.toHexString(_color1) : "-1")).append(";");
	    str.append((_color2 != -1 ? Integer.toHexString(_color2) : "-1")).append(";");
	    str.append((_color3 != -1 ? Integer.toHexString(_color3) : "-1")).append(";");
	    str.append(getStringAccesorios()).append(";");
	    str.append((_esMercante == 1 ? "1" : "0")).append(";");
	    str.append(Emu.SERVER_ID).append(";");
	    str.append("0;"); // isDead
	    str.append(";");  // deathCount
	    str.append(Emu.MAX_NIVEL);
	    return str.toString();
	}

	public void mostrarAmigosEnLinea(boolean mostrar) {
		_mostrarConeccionAmigos = mostrar;
	}

	public String analizarFiguraDelPJ() {
		return "Oa" + _ID + "|" + getStringAccesorios();
	}




	public String stringGMmercante() {
		StringBuilder str = new StringBuilder("");
		str.append(_celda.getID() + ";");// 0
		str.append("1;");// 1
		str.append("0" + ";");// 2
		str.append(_ID + ";");// 3
		str.append(_nombre + ";");// 4
		str.append("-5");// 5, hay q poner -5
		str.append((getTitulo() > 0 ? ("," + getTitulo() + ";") : (";")));// 5
		str.append(_gfxID + "^" + _talla + ";");// gfxID^size//6
		str.append((_color1 == -1 ? "-1" : Integer.toHexString(_color1)) + ";");// 7
		str.append((_color2 == -1 ? "-1" : Integer.toHexString(_color2)) + ";");// 8
		str.append((_color3 == -1 ? "-1" : Integer.toHexString(_color3)) + ";");// 9
		str.append(getStringAccesorios() + ";");// 10,accesorios
		if (_miembroGremio != null && _miembroGremio.getGremio().getPjMiembros().size() > 9) {
			str.append(_miembroGremio.getGremio().getNombre() + ";" + _miembroGremio.getGremio().getEmblema() + ";");// 12
		}
		else {
			str.append(";;");// 11,12
		}
		str.append("0");// tipo de gift de mercante, comunmente 0 = todos, 1= armas, 2= pocimas, 3 =
						// recursos
		return str.toString();
	}

	private static int getSeguidor(int itemid) {
		switch (itemid) {
		case 70201:
			return 1005;
		case 70202:
			return 1011;
		case 70203:
			return 1015;
		case 70204:
			return 1090;
		case 70205:
			return 1093;
		case 70206:
			return 1237;
		case 70207:
			return 1390;
		case 70208:
			return 1351;
		case 70209:
			return 9084;
		case 70210:
			return 1083;
		case 70211:
			return 1139;
		case 70212:
			return 1013;
		case 70213:
			return 1474;
		case 70214:
			return 1546;
		case 70215:
			return 1009;
		case 70216:
			return 1046;
		case 70217:
			return 1490;
		case 70218:
			return 1269;
		case 70219:
			return 1558;
		case 70220:
			return 1372;
		case 70221:
			return 1510;
		case 70222:
			return 1232;
		case 70223:
			return 9081;
		}
		return 0;
	}

	public boolean activa = true;

	private int getOrnamento(int itemid) {
		int actual = 70231;
		int ix = 0;
		for (int i = 1; i < 87; i++) {
			ix = i;
			if (actual == itemid) {
				break;
			}
			actual += 1;
		}
		return ix;
	}

	private static int getAura(int itemid) {
		switch (itemid) {
		case 70317:
			return 5;// dejar en 5
		case 70318:
			return 6;
		case 70319:
			return 7;
		case 70320:
			return 8;
		case 70321:
			return 16;
		case 70322:
			return 17;
		case 30914:
			return 1;
		case 30915:
			return 2;
		case 30916:
			return 3;
		}
		return 0;
	}

	private int ornamento = 0;
	private int aura = 0;

	public String stringGM() {
		if (activa) { // 28
			if (this.getObjPosicion(204) != null) {
				_seguidor = getSeguidor(this.getObjPosicion(204).getIDModelo());
			} else {
				_seguidor = 0;
			}
			if (this.getObjPosicion(205) != null) {
				ornamento = getOrnamento(this.getObjPosicion(205).getIDModelo());
			} else {
				ornamento = 0;
			}
			if (this.getObjPosicion(202) != null) {
				aura = getAura(this.getObjPosicion(202).getIDModelo());
			} else {
				aura = 0;
			}
			activa = false;
		}
		StringBuilder str = new StringBuilder();
		if (_pelea != null) {
			return "";
		}
		str.append(_celda.getID()).append(";");
		str.append(_orientacion).append(";");
		str.append(ornamento).append(";");
		str.append(_ID).append(";");
		str.append(_nombre).append(";");
		str.append(_clase);
		str.append(_titulo > 0 ? ("," + _titulo + ";") : (";"));
		if (_seguidor == 0) {
			str.append(_gfxID).append("^").append(_talla).append(",");
		} else {
			str.append(_gfxID).append("^").append(_talla).append(",").append(_seguidor);
		}
		if (!_seguidores.isEmpty()) {
			str.append(",").append(_seguidores);
		}
		str.append(";");
		str.append(_sexo).append(";");
		str.append(_alineacion).append(",");
		str.append(getNivelAlineacion() + ","); // 8
		str.append((_mostrarAlas ? getNivelAlineacion() : "0") + ",");// 8
		str.append(_nivel + ",");// 8
		str.append((_deshonor > 0 ? 1 : 0) + ";"); // 8
		str.append((_color1 == -1 ? "-1" : Integer.toHexString(_color1)) + ";");// 9
		str.append((_color2 == -1 ? "-1" : Integer.toHexString(_color2)) + ";");// 10
		str.append((_color3 == -1 ? "-1" : Integer.toHexString(_color3)) + ";");// 11
		str.append(getStringAccesorios() + ";");// 12,accesorios
		if (_montando) {
			aura = 0;
		}
		str.append(aura + ";");

		str.append(";");// Emote 14
		str.append(";");// Emote timer 15
		if (_miembroGremio != null && _miembroGremio.getGremio().getPjMiembros().size() > 9) {
			str.append(_miembroGremio.getGremio().getNombre() + ";" + _miembroGremio.getGremio().getEmblema() + ";");// 17
		}
		else {
			str.append(";;");// 16,17 miembro vacio
		}
		str.append(Integer.toString(getRestriccionesB(), 36) + ";");// 18= RESTRICCIONES getRestriccionesB()
		str.append((_montando && _montura != null ? _montura.getStringColor(stringColorDueÑoPavo()) : "") + ";"); // 19
		String veloc = "0";
		if (!_montando) {
			veloc = "0.10";
		}
		else {
			veloc = "0.25";// resets,vip,
		}
		str.append(veloc + ";" + _resets + "," + _cuenta.getVIP() + ",,,,,");// velocidad;reset
		return str.toString();
	}



	// Nuevo método auxiliar para evitar el texto "null"
	private String getModeloObjHex(int pos, int pos2) {
	    Objeto obj = getObjPosicion(pos2);
	    if (obj == null) obj = getObjPosicion(pos);
	    
	    // Si no hay objeto, devolvemos cadena vacía
	    if (obj == null) return "";

	    int modeloID = obj.getIDModelo();
	    // Soporte para Mimobiontes
	    if (obj.getTextoStat().containsKey(2000)) {
	        try {
	            modeloID = Integer.parseInt(obj.getTextoStat().get(2000).split("\\(")[1].replace(")", ""));
	        } catch (Exception e) {
	            modeloID = obj.getIDModelo();
	        }
	    }
	    return Integer.toHexString(modeloID);
	}
	
	public String getStringAccesorios() {
	    String arma = getModeloObjHex(1, 1);
	    String sombrero = getModeloObjHex(6, 106);
	    String capa = getModeloObjHex(7, 107);
	    String mascota = getModeloObjHex(8, 108);
	    String escudo = getModeloObjHex(15, 115);

	    // CRÍTICO: Si todos están vacíos, devolvemos cadena VACÍA, sin comas.
	    if (arma.isEmpty() && sombrero.isEmpty() && capa.isEmpty() && mascota.isEmpty() && escudo.isEmpty()) {
	        return "";
	    }

	    // Si hay al menos uno, construimos la cadena con comas
	    StringBuilder str = new StringBuilder();
	    str.append(arma).append(",");
	    str.append(sombrero).append(",");
	    str.append(capa).append(",");
	    str.append(mascota).append(",");
	    str.append(escudo);
	    return str.toString();
	}

	public String stringStatsPacket() {
		Stats objEquipStats = getStatsObjEquipados();
		Stats totalStats = getTotalStats();
		Stats boostStats = getStatsBoost();
		Stats benMaldStats = getStatsBendMald();
		refrescarVida(false);
		StringBuffer str = new StringBuffer("As");
		str.append(xpString(",") + "|");
		str.append(_kamas + "|");
		if (_encarnacion != null) {
			str.append("0|0|");
		} else {
			str.append(_capital + "|" + _puntosHechizo + "|");
		}
		str.append(_alineacion + "~" + _alineacion + "," + _nivelAlineacion + "," + _nivelAlineacion + "," + _honor
				+ "," + _deshonor + "," + (_mostrarAlas ? "1" : "0") + "|");
		int pdv = getPDV();
		int pdvMax = getPDVMAX();
		if (_pelea != null) {
			Luchador luchador = _pelea.getLuchadorPorPJ(this);
			if (luchador != null) {
				pdv = luchador.getPDVConBuff();
				pdvMax = luchador.getPDVMaxConBuff();
			}
		}
		str.append(pdv + "," + pdvMax + "|");
		str.append(_energia + ",10000|");
		str.append(getIniciativa() + "|");
		str.append(totalStats.getEfecto(176) + "|");
		str.append(_baseStats.getEfecto(111) + "," + objEquipStats.getEfecto(111) + "," + benMaldStats.getEfecto(111)
				+ "," + boostStats.getEfecto(111) + "," + totalStats.getEfecto(111) + "|");
		str.append(_baseStats.getEfecto(128) + "," + objEquipStats.getEfecto(128) + "," + benMaldStats.getEfecto(128)
				+ "," + boostStats.getEfecto(128) + "," + totalStats.getEfecto(128) + "|");
		str.append(_baseStats.getEfecto(118) + "," + objEquipStats.getEfecto(118) + "," + benMaldStats.getEfecto(118)
				+ "," + boostStats.getEfecto(118) + "," + totalStats.getEfecto(118) + "|");
		str.append(_baseStats.getEfecto(125) + "," + objEquipStats.getEfecto(125) + "," + benMaldStats.getEfecto(125)
				+ "," + boostStats.getEfecto(125) + "," + totalStats.getEfecto(125) + "|");
		str.append(_baseStats.getEfecto(124) + "," + objEquipStats.getEfecto(124) + "," + benMaldStats.getEfecto(124)
				+ "," + boostStats.getEfecto(124) + "," + totalStats.getEfecto(124) + "|");
		str.append(_baseStats.getEfecto(123) + "," + objEquipStats.getEfecto(123) + "," + benMaldStats.getEfecto(123)
				+ "," + boostStats.getEfecto(123) + "," + totalStats.getEfecto(123) + "|");
		str.append(_baseStats.getEfecto(119) + "," + objEquipStats.getEfecto(119) + "," + benMaldStats.getEfecto(119)
				+ "," + boostStats.getEfecto(119) + "," + totalStats.getEfecto(119) + "|");
		str.append(_baseStats.getEfecto(126) + "," + objEquipStats.getEfecto(126) + "," + benMaldStats.getEfecto(126)
				+ "," + boostStats.getEfecto(126) + "," + totalStats.getEfecto(126) + "|");
		str.append(_baseStats.getEfecto(117) + "," + objEquipStats.getEfecto(117) + "," + benMaldStats.getEfecto(117)
				+ "," + boostStats.getEfecto(117) + "," + totalStats.getEfecto(117) + "|");
		str.append(_baseStats.getEfecto(182) + "," + objEquipStats.getEfecto(182) + "," + benMaldStats.getEfecto(182)
				+ "," + boostStats.getEfecto(182) + "," + totalStats.getEfecto(182) + "|");
		str.append(_baseStats.getEfecto(112) + "," + objEquipStats.getEfecto(112) + "," + benMaldStats.getEfecto(112)
				+ "," + boostStats.getEfecto(112) + "," + totalStats.getEfecto(112) + "|");
		str.append(_baseStats.getEfecto(142) + "," + objEquipStats.getEfecto(142) + "," + benMaldStats.getEfecto(142)
				+ "," + boostStats.getEfecto(142) + "," + totalStats.getEfecto(142) + "|");
		str.append("0,0,0,0|");
		str.append(_baseStats.getEfecto(138) + "," + objEquipStats.getEfecto(138) + "," + benMaldStats.getEfecto(138)
				+ "," + boostStats.getEfecto(138) + "," + totalStats.getEfecto(138) + "|");
		str.append(_baseStats.getEfecto(178) + "," + objEquipStats.getEfecto(178) + "," + benMaldStats.getEfecto(178)
				+ "," + boostStats.getEfecto(178) + "," + totalStats.getEfecto(178) + "|");
		str.append(_baseStats.getEfecto(225) + "," + objEquipStats.getEfecto(225) + "," + benMaldStats.getEfecto(225)
				+ "," + boostStats.getEfecto(225) + "," + totalStats.getEfecto(225) + "|");
		str.append(_baseStats.getEfecto(226) + "," + objEquipStats.getEfecto(226) + "," + benMaldStats.getEfecto(226)
				+ "," + boostStats.getEfecto(226) + "," + totalStats.getEfecto(226) + "|");
		str.append(_baseStats.getEfecto(220) + "," + objEquipStats.getEfecto(220) + "," + benMaldStats.getEfecto(220)
				+ "," + boostStats.getEfecto(220) + "," + totalStats.getEfecto(220) + "|");
		str.append(_baseStats.getEfecto(115) + "," + objEquipStats.getEfecto(115) + "," + benMaldStats.getEfecto(115)
				+ "," + boostStats.getEfecto(115) + "," + totalStats.getEfecto(115) + "|");
		str.append(_baseStats.getEfecto(122) + "," + objEquipStats.getEfecto(122) + "," + benMaldStats.getEfecto(122)
				+ "," + boostStats.getEfecto(122) + "," + totalStats.getEfecto(122) + "|");
		str.append(_baseStats.getEfecto(160) + "," + objEquipStats.getEfecto(160) + "," + benMaldStats.getEfecto(160)
				+ "," + boostStats.getEfecto(160) + "," + totalStats.getEfecto(160) + "|");
		str.append(_baseStats.getEfecto(161) + "," + objEquipStats.getEfecto(161) + "," + benMaldStats.getEfecto(161)
				+ "," + boostStats.getEfecto(161) + "," + totalStats.getEfecto(161) + "|");
		str.append(_baseStats.getEfecto(241) + "," + objEquipStats.getEfecto(241) + "," + benMaldStats.getEfecto(241)
				+ "," + boostStats.getEfecto(241) + "," + totalStats.getEfecto(241) + "|");
		str.append(_baseStats.getEfecto(214) + "," + objEquipStats.getEfecto(214) + "," + benMaldStats.getEfecto(214)
				+ "," + boostStats.getEfecto(214) + "," + totalStats.getEfecto(214) + "|");
		str.append(_baseStats.getEfecto(264) + "," + objEquipStats.getEfecto(264) + "," + benMaldStats.getEfecto(264)
				+ "," + boostStats.getEfecto(264) + "," + totalStats.getEfecto(264) + "|");
		str.append(_baseStats.getEfecto(254) + "," + objEquipStats.getEfecto(254) + "," + benMaldStats.getEfecto(254)
				+ "," + boostStats.getEfecto(254) + "," + totalStats.getEfecto(254) + "|");
		str.append(_baseStats.getEfecto(242) + "," + objEquipStats.getEfecto(242) + "," + benMaldStats.getEfecto(242)
				+ "," + boostStats.getEfecto(242) + "," + totalStats.getEfecto(242) + "|");
		str.append(_baseStats.getEfecto(210) + "," + objEquipStats.getEfecto(210) + "," + benMaldStats.getEfecto(210)
				+ "," + boostStats.getEfecto(210) + "," + totalStats.getEfecto(210) + "|");
		str.append(_baseStats.getEfecto(260) + "," + objEquipStats.getEfecto(260) + "," + benMaldStats.getEfecto(260)
				+ "," + boostStats.getEfecto(260) + "," + totalStats.getEfecto(260) + "|");
		str.append(_baseStats.getEfecto(250) + "," + objEquipStats.getEfecto(250) + "," + benMaldStats.getEfecto(250)
				+ "," + boostStats.getEfecto(250) + "," + totalStats.getEfecto(250) + "|");
		str.append(_baseStats.getEfecto(243) + "," + objEquipStats.getEfecto(243) + "," + benMaldStats.getEfecto(243)
				+ "," + boostStats.getEfecto(243) + "," + totalStats.getEfecto(243) + "|");
		str.append(_baseStats.getEfecto(211) + "," + objEquipStats.getEfecto(211) + "," + benMaldStats.getEfecto(211)
				+ "," + boostStats.getEfecto(211) + "," + totalStats.getEfecto(211) + "|");
		str.append(_baseStats.getEfecto(261) + "," + objEquipStats.getEfecto(261) + "," + benMaldStats.getEfecto(261)
				+ "," + boostStats.getEfecto(261) + "," + totalStats.getEfecto(261) + "|");
		str.append(_baseStats.getEfecto(251) + "," + objEquipStats.getEfecto(251) + "," + benMaldStats.getEfecto(251)
				+ "," + boostStats.getEfecto(251) + "," + totalStats.getEfecto(251) + "|");
		str.append(_baseStats.getEfecto(244) + "," + objEquipStats.getEfecto(244) + "," + benMaldStats.getEfecto(244)
				+ "," + boostStats.getEfecto(244) + "," + totalStats.getEfecto(244) + "|");
		str.append(_baseStats.getEfecto(212) + "," + objEquipStats.getEfecto(212) + "," + benMaldStats.getEfecto(212)
				+ "," + boostStats.getEfecto(212) + "," + totalStats.getEfecto(212) + "|");
		str.append(_baseStats.getEfecto(262) + "," + objEquipStats.getEfecto(262) + "," + benMaldStats.getEfecto(262)
				+ "," + boostStats.getEfecto(262) + "," + totalStats.getEfecto(262) + "|");
		str.append(_baseStats.getEfecto(252) + "," + objEquipStats.getEfecto(252) + "," + benMaldStats.getEfecto(252)
				+ "," + boostStats.getEfecto(252) + "," + totalStats.getEfecto(252) + "|");
		str.append(_baseStats.getEfecto(240) + "," + objEquipStats.getEfecto(240) + "," + benMaldStats.getEfecto(240)
				+ "," + boostStats.getEfecto(240) + "," + totalStats.getEfecto(240) + "|");
		str.append(_baseStats.getEfecto(213) + "," + objEquipStats.getEfecto(213) + "," + benMaldStats.getEfecto(213)
				+ "," + boostStats.getEfecto(213) + "," + totalStats.getEfecto(213) + "|");
		str.append(_baseStats.getEfecto(263) + "," + objEquipStats.getEfecto(263) + "," + benMaldStats.getEfecto(263)
				+ "," + boostStats.getEfecto(263) + "," + totalStats.getEfecto(263) + "|");
		str.append(_baseStats.getEfecto(253) + "," + objEquipStats.getEfecto(253) + "," + benMaldStats.getEfecto(253)
				+ "," + boostStats.getEfecto(253) + "," + totalStats.getEfecto(253) + "|");
		str.append(_baseStats.getEfecto(410) + "," + objEquipStats.getEfecto(410) + "," + benMaldStats.getEfecto(410)
				+ "," + boostStats.getEfecto(410) + "," + totalStats.getEfecto(410) + "|");
		str.append(_baseStats.getEfecto(413) + "," + objEquipStats.getEfecto(413) + "," + benMaldStats.getEfecto(413)
				+ "," + boostStats.getEfecto(413) + "," + totalStats.getEfecto(413) + "|");
		str.append(_baseStats.getEfecto(419) + "," + objEquipStats.getEfecto(419) + "," + benMaldStats.getEfecto(419)
				+ "," + boostStats.getEfecto(419) + "," + totalStats.getEfecto(419) + "|");
		str.append(_baseStats.getEfecto(418) + "," + objEquipStats.getEfecto(418) + "," + benMaldStats.getEfecto(418)
				+ "," + boostStats.getEfecto(418) + "," + totalStats.getEfecto(418) + "|");
		str.append(_baseStats.getEfecto(417) + "," + objEquipStats.getEfecto(417) + "," + benMaldStats.getEfecto(417)
				+ "," + boostStats.getEfecto(417) + "," + totalStats.getEfecto(417) + "|");
		str.append(_baseStats.getEfecto(416) + "," + objEquipStats.getEfecto(416) + "," + benMaldStats.getEfecto(416)
				+ "," + boostStats.getEfecto(416) + "," + totalStats.getEfecto(416) + "|");
		str.append(_baseStats.getEfecto(415) + "," + objEquipStats.getEfecto(415) + "," + benMaldStats.getEfecto(415)
				+ "," + boostStats.getEfecto(415) + "," + totalStats.getEfecto(415) + "|");
		return str.toString();
	}

	String xpString(String c) {
		return _experiencia + c + (MundoDofus.getExpMinPersonaje(_nivel)) + c + MundoDofus.getExpMaxPersonaje(_nivel);
	}

	public int emoteActivado() {
		return _emoteActivado;
	}

	public void setEmoteActivado(int emoteActivado) {
		_emoteActivado = emoteActivado;
	}

	private Stats getStatsObjEquipados() {
		Stats stats = new Stats(false, null);
		ArrayList<Integer> listaSetsEquipados = new ArrayList<>();
		Map<Integer, Objeto> objetos = new HashMap<>();
		objetos.putAll(_objetos);
		for (Objeto objeto : objetos.values()) {
			if (objeto == null) {
				continue;
			}
			if (objeto.getPosicion() != -1
					&& ((objeto.getPosicion() >= 0 && objeto.getPosicion() <= 15)
							|| (objeto.getPosicion() >= 20 && objeto.getPosicion() <= 27))
					|| objeto.getPosicion() == 200 || objeto.getPosicion() == 201 || objeto.getPosicion() == 202
					|| objeto.getPosicion() == 203 || objeto.getPosicion() == 204 || objeto.getPosicion() == 205
					|| objeto.getPosicion() == 170) {
				stats = Stats.acumularStats(stats, objeto.getStats());
				int setID = objeto.getModelo().getSetID();
				if (setID > 0 && !listaSetsEquipados.contains(setID)) {
					listaSetsEquipados.add(setID);
					ItemSet IS = MundoDofus.getItemSet(setID);
					if (IS != null) {
						stats = Stats.acumularStats(stats, IS.getBonusStatPorNroObj(getNroObjEquipadosDeSet(setID)));
					}
				}
			}
		}
		if (_montando && _montura != null) {
			stats = Stats.acumularStats(stats, _montura.getStats());
		}
		/*
		 * if (getPelea() != null) { //if (getPelea().getTipoPelea() <= 2 ||
		 * getPelea().getTipoPelea() == 5){ int valor1 = (stats.getEfecto(138)); if
		 * (valor1 > 250) valor1 = 250; stats.especificarStat(138, valor1); //daÑo %
		 *
		 * int valor2 = (stats.getEfecto(112)); //daÑo + if (valor2 > 280) valor2 = 280;
		 * stats.especificarStat(112, valor2);
		 *
		 * if (stats.getEfecto(178) > 80) stats.especificarStat(178, 80); if
		 * (stats.getEfecto(117) > 10) {//alcance stats.especificarStat(117, 10); } //}
		 * }//limite daÑo
		 */
		return stats;
	}

	private Stats getStatsBoost() {
		Stats stats = new Stats(false, null);
		return stats;
	}

	private Stats getStatsBendMald() {
		Stats stats = new Stats(false, null);
		return stats;
	}

	public Stats getTotalStats() {
		Stats total = new Stats(false, null);
		total = Stats.acumularStats(total, _baseStats);
		total = Stats.acumularStats(total, getStatsObjEquipados());
		total = Stats.acumularStats(total, getStatsBendMald());
		total = Stats.acumularStats(total, getStatsBoost());
		return total;
	}

	public int getOrientacion() {
		return _orientacion;
	}

	public void setOrientacion(int orientacion) {
		_orientacion = orientacion;
	}

	public int getIniciativa() {
		Stats objEquipados = getTotalStats();
		int fact = 4;
		int pvmax = _PDVMAX - CentroInfo.getBasePDV(_clase);
		int pv = _PDV - CentroInfo.getBasePDV(_clase);
		if (_clase == 11) {
			fact = 8;
		}
		double coef = pvmax / fact;
		coef += getStatsObjEquipados().getEfecto(174);
		coef += objEquipados.getEfecto(119);
		coef += objEquipados.getEfecto(123);
		coef += objEquipados.getEfecto(126);
		coef += objEquipados.getEfecto(118);
		int init = 1;
		if (pvmax != 0) {
			init = (int) (coef * ((double) pv / (double) pvmax));
		}
		if (init < 0) {
			init = 0;
		}
		return init;
	}

	public int getPodUsados() {
		int pod = 0;
		Collection<Objeto> objx = _objetos.values();
		for (Objeto objeto : objx) {
			if (objeto == null) {
				continue;
			}
			pod += objeto.getModelo().getPeso() * objeto.getCantidad();
		}
		return pod;
	}

	public int lastpodvalue = 0;

	public int getMaxPod() {
		int pods = getTotalStats().getEfecto(158);
		pods += (getTotalStats().getEfecto(118) * 5);
		Collection<StatsOficio> stx = _statsOficios.values();
		for (StatsOficio SO : stx) {
			pods += SO.getNivel() * 5;
			if (SO.getNivel() == 100) {
				pods += 1000;
			}
		}
		if (pods < 1000) {
			pods = 1000;
		}
		return pods;
	}

	public int getPDV() {
		if (_PDV <= 0) {
			_PDV = 1;
		}
		return _PDV;
	}

	public void setPDV(int pdv) {
		if (pdv <= 0) {
			pdv = 1;
		}
		/*
		 * if (pdv == 1) { GestorSalida.ENVIAR_cs_CHAT_MENSAJE(this,
		 * "<b>Recuerde: </b> Puedes regenerar tu vida estando solamente en la Taberna."
		 * , Colores.ROJO); }
		 */
		if (pdv > getPDVMAX()) {
			pdv = getPDVMAX();
		}
		_PDV = pdv;
		actualizarInfoGrupo();
	}

	public int getPDVMAX() {
		if (_encarnacion != null) {
			return _encarnacion.getPDVMAX();
		}
		return _PDVMAX;
	}

	public void setPDVMAX(int pdvmax) {
		_PDVMAX = pdvmax;
		actualizarInfoGrupo();
	}

	public void actualizarInfoGrupo() {
		if (_grupo != null) {
			GestorSalida.ENVIAR_PM_ACTUALIZAR_INFO_PJ_GRUPO(_grupo, this);
		}
	}



	public void setSentado(boolean sentado) {
		_sentado = sentado;
		if (((_emoteActivado == 1) || (_emoteActivado == 19)) && (!sentado)) {
			_emoteActivado = 0;
		}
	}

	public byte getAlineacion() {
		return _alineacion;
	}

	public int getPorcPDV() {
		int porcPDV = 100;
		porcPDV = (100 * _PDV) / _PDVMAX;
		if (porcPDV > 100) {
			return 100;
		}
		return porcPDV;
	}

	public void emote(String str) {
		try {
			int id = Integer.parseInt(str);
			Mapa mapa = _mapa;
			if (_pelea == null) {
				GestorSalida.ENVIAR_cS_EMOTICON_MAPA(mapa, _ID, id);
			} else {
				GestorSalida.ENVIAR_cS_EMOTE_EN_PELEA(_pelea, 7, _ID, id);
			}
		} catch (NumberFormatException e) {
			return;
		}
	}

	public boolean instante = false;

	public void retornoMapa() {
		_pelea = null;
		_ocupado = false;
		_listo = false;
		_dueloID = -1;
		instante = true;
		_mapa.addJugador(this, true);
	}

	void retornoPtoSalvadaRecau() {
		_pelea = null;
		_ocupado = false;
		_listo = false;
		_dueloID = -1;
		if (_energia > 0) {
			if (_tempMapaDefPerco != null && _tempCeldaDefPerco != null) {
				teleport(_tempMapaDefPerco.getID(), _tempCeldaDefPerco.getID());
			}
		}
		GestorSalida.ENVIAR_GV_RESETEAR_PANTALLA_JUEGO(this);
		_tempMapaDefPerco = null;
		_tempCeldaDefPerco = null;
		instante = false;
	}

	private void retornoPtoSalvada() {
		_pelea = null;
		_ocupado = false;
		_listo = false;
		_dueloID = -1;
		if (_energia > 0) {
			String[] infos = _puntoSalvado.split(",");
			teleport(Short.parseShort(infos[0]), Integer.parseInt(infos[1]));
		}
		GestorSalida.ENVIAR_GV_RESETEAR_PANTALLA_JUEGO(this);
		instante = false;
	}

	void retornoPtoSalvadaPocima() {
		_pelea = null;
		_ocupado = false;
		_listo = false;
		_dueloID = -1;
		try {
			String[] infos = _puntoSalvado.split(",");
			teleport(Short.parseShort(infos[0]), Integer.parseInt(infos[1]));
		} catch (Exception e) {
		}
		instante = false;
	}

	public void boostStat(int stat) {
		int value = 0;
		switch (stat) {
		case 10:// Fuerza
			value = _baseStats.getEfecto(118);
			break;
		case 13:// Suerte
			value = _baseStats.getEfecto(123);
			break;
		case 14:// Agilidad
			value = _baseStats.getEfecto(119);
			break;
		case 15:// Inteligencia
			value = _baseStats.getEfecto(126);
			break;
		}
		int cout = CentroInfo.getRepartoPuntoSegunClase(_clase, stat, value);
		if (cout <= _capital) {
			switch (stat) {
			case 11:// Vitalidad
				if (_clase != 11) {
					_baseStats.addUnStat(125, 1);
				} else {
					_baseStats.addUnStat(125, 2);
				}
				break;
			case 12:// Sabiduria
				_baseStats.addUnStat(124, 1);
				break;
			case 10:// Fuerza
				_baseStats.addUnStat(118, 1);
				break;
			case 13:// suerte
				_baseStats.addUnStat(123, 1);
				break;
			case 14:// Agilidad
				_baseStats.addUnStat(119, 1);
				break;
			case 15:// Inteligencia
				_baseStats.addUnStat(126, 1);
				break;
			default:
				return;
			}
			_capital -= cout;
			GestorSalida.ENVIAR_As_STATS_DEL_PJ(this);
		}
	}

	public void boostStat(int stat, int cant) {
		int valor = 0;
		if (cant > _capital) {
			cant = _capital;
		}
		int capital = cant;
		int cantidad = 0;
		while (capital >= cantidad) {
			switch (stat) {
			case 10:
				valor = _baseStats.getEfecto(118);
				break;
			case 13:
				valor = _baseStats.getEfecto(123);
				break;
			case 14:
				valor = _baseStats.getEfecto(119);
				break;
			case 15:
				valor = _baseStats.getEfecto(126);
				break;
			case 11:
			case 12:
				break;
			}
			cantidad = CentroInfo.getRepartoPuntoSegunClase(_clase, stat, valor);
			if (cantidad <= _capital) {
				switch (stat) {
				case 11:
					if (_clase != 11) {
						_baseStats.addUnStat(125, (short) 1);
					} else {
						_baseStats.addUnStat(125, (short) 2);
					}
					break;
				case 12:
					_baseStats.addUnStat(124, (short) 1);
					break;
				case 10:
					_baseStats.addUnStat(118, (short) 1);
					break;
				case 13:
					_baseStats.addUnStat(123, (short) 1);
					break;
				case 14:
					_baseStats.addUnStat(119, (short) 1);
					break;
				case 15:
					_baseStats.addUnStat(126, (short) 1);
					break;
				default:
					return;
				}
				capital -= cantidad;
				_capital -= cantidad;
			}
		}
		GestorSalida.ENVIAR_As_STATS_DEL_PJ(this);
	}

	void boostStat2(int stat) {
		int value = 0;
		int capital = _capital;
		int cout = 0;
		while (capital >= cout) {
			switch (stat) {
			case 10:// Fuerza
				value = _baseStats.getEfecto(118);
				break;
			case 13:// Suerte
				value = _baseStats.getEfecto(123);
				break;
			case 14:// Agilidad
				value = _baseStats.getEfecto(119);
				break;
			case 15:// Inteligencia
				value = _baseStats.getEfecto(126);
				break;
			}
			cout = CentroInfo.getRepartoPuntoSegunClase(_clase, stat, value);
			if (cout <= _capital) {
				switch (stat) {
				case 11:// Vitalidad
					if (_clase != 11) {
						_baseStats.addUnStat(125, 1);
					} else {
						_baseStats.addUnStat(125, 2);
					}
					break;
				case 12:// Sabiduria
					_baseStats.addUnStat(124, 1);
					break;
				case 10:// Fuerza
					_baseStats.addUnStat(118, 1);
					break;
				case 13:// suerte
					_baseStats.addUnStat(123, 1);
					break;
				case 14:// Agilidad
					_baseStats.addUnStat(119, 1);
					break;
				case 15:// Inteligencia
					_baseStats.addUnStat(126, 1);
					break;
				default:
					return;
				}
				capital -= cout;
			}
		}
		_capital = capital;
		GestorSalida.ENVIAR_As_STATS_DEL_PJ(this);
	}

	public boolean estaMuteado() {
		return _cuenta.estaMuteado();
	}

	public void setMapa(Mapa mapa) {
		_mapa = mapa;
	}

	public String stringObjetosABD() {
		StringBuilder str = new StringBuilder("");
		for (Entry<Integer, Objeto> entry : _objetos.entrySet()) {
			Objeto obj = entry.getValue();
			str.append(obj.getID() + "|");
		}
		return str.toString();
	}

	public Objeto getObjSimilarInventario(Objeto objeto) {
		ObjetoModelo objModelo = objeto.getModelo();
		if (objModelo.getTipo() == 85 || objModelo.getTipo() == 18
				|| Emu.ARMAS_ENCARNACIONES.contains(objModelo.getID())) {
			return null;
		}
		for (Entry<Integer, Objeto> entry : _objetos.entrySet()) {
			Objeto obj = entry.getValue();
			if (obj.getPosicion() == -1 && objeto.getID() != obj.getID() && obj.getModelo().getID() == objModelo.getID()
					&& obj.getStats().sonStatsIguales(objeto.getStats())) {
				return obj;
			}
		}
		return null;
	}

	public boolean addObjetoSimilar(Objeto objeto, boolean tieneSimilar, int idAntigua) {
		ObjetoModelo objModelo = objeto.getModelo();
		if (objModelo.getTipo() == 85 || objModelo.getTipo() == 18 || objModelo.getTipo() == 113
				|| Emu.ARMAS_ENCARNACIONES.contains(objModelo.getID())) {
			return false;
		}
		if (tieneSimilar) {
			for (Entry<Integer, Objeto> entry : _objetos.entrySet()) {
				Objeto obj = entry.getValue();
				if (obj.getPosicion() == -1 && obj.getID() != idAntigua && obj.getModelo().getID() == objModelo.getID()
						&& obj.getStats().sonStatsIguales(objeto.getStats())
						&& obj.getTextoStat().equals(objeto.getTextoStat())) {
					obj.setCantidad(obj.getCantidad() + objeto.getCantidad());
					GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(this, obj);
					return true;
				}
			}
		}
		return false;
	}

	public void addObjetoPut(Objeto objeto) {
		if (objeto == null) {
			return;
		}
		_objetos.put(objeto.getID(), objeto);
	}

	public Map<Integer, Objeto> getObjetos() {
		return _objetos;
	}

	public String stringPersonajeElegido() {
		StringBuilder str = new StringBuilder("");
		Objeto objeto = getObjPosicion(21);
		try {
			if (objeto != null) {
				String stats = objeto.convertirStatsAString(0);
				String[] arg = stats.split(",");
				for (String efec : arg) {
					String[] val = efec.split("#");
					int efecto = Integer.parseInt(val[0], 16);
					if (efecto >= 281 || efecto <= 292) {
						_bendEfecto = efecto;
						_bendHechizo = Integer.parseInt(val[1], 16);
						_bendModif = Integer.parseInt(val[3], 16);
					}
				}
			}
		} catch (Exception e) {
			if (objeto != null) {
				borrarObjetoRemove(objeto.getID());
				MundoDofus.eliminarObjeto(objeto.getID());
				GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(this, objeto.getID());
			}
		}
		for (Objeto obj : _objetos.values()) {
			if (obj.getObjeviID() != 0 && obj.getModelo().getTipo() != 113) {
				Objevivo objev = MundoDofus.getObjevivos(obj.getObjeviID());
				if (objev != null) {
					obj.convertirStringAStats(objev.getStat());
				}
			}
			if (obj.getModelo().getTipo() == 90 || obj.getModelo().getTipo() == 18) {
				str.append(obj.stringObjetoConGuiÓnMasco());
			} else {
				str.append(obj.stringObjetoConGuiÓn());
			}

		}
		return str.toString();
	}

	public String getObjetosBancoPorID(String splitter) {
		StringBuilder str = new StringBuilder("");
		for (int entry : _cuenta.getBanco().keySet()) {
			str.append(entry + splitter);
		}
		return str.toString();
	}

	public String getObjetosPersonajePorID(String splitter) {
		StringBuilder str = new StringBuilder("");
		for (int entry : _objetos.keySet()) {
			if (str.length() != 0) {
				str.append(splitter);
			}
			str.append(entry);
		}
		return str.toString();
	}

	public boolean tieneObjetoID(int id) {
		return _objetos.get(id) != null ? _objetos.get(id).getCantidad() > 0 : false;
	}

	public void venderObjeto(int id, int cant) {
		if (cant <= 0) {
			return;
		}
		Objeto objeto = _objetos.get(id);
		if (objeto == null) {
			return;
		}
		ObjetoModelo objModelo = objeto.getModelo();
		int precioUnitario = objModelo.getPrecio();
		// int precioVIP = objModelo.getPrecioVIP();
		/*
		 * if (precioUnitario == 0 && precioVIP > 0) { int ptosAconseguirx = cant * 10;
		 * int misPuntos = GestorSQL.getPuntosCuenta(_cuentaID);
		 * GestorSQL.setPuntoCuenta(ptosAconseguirx + misPuntos, _cuentaID); int
		 * nuevaCant = objeto.getCantidad() - cant;
		 * GestorSalida.ENVIAR_cs_CHAT_MENSAJE(this, "Has ganado <b>+"
		 * +ptosAconseguirx+"</b> puntos por vender un objeto VIP (10 puntos por objeto)"
		 * , Colores.VERDE); if (nuevaCant <= 0) { //if
		 * (MundoDofus._fusionados.contains(id))
		 * //MundoDofus._fusionados.remove(Integer.valueOf(id)); _objetos.remove(id);
		 * MundoDofus.eliminarObjeto(id); GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(this,
		 * id); } else { objeto.setCantidad(nuevaCant);
		 * GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(this, objeto); } } else {
		 */
		if (objeto.getCantidad() < cant) {
			cant = objeto.getCantidad();
		}
		int precio = cant * (precioUnitario / 10);
		if (precio > 1000000) {
			precio = 1000000;
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(this,
					"El mÁximo precio al cual se puede vender un objeto es de 1.000.000 Kamas", Colores.VERDE);
		}
		int nuevaCant = objeto.getCantidad() - cant;
		if (nuevaCant <= 0) {
			// if (MundoDofus._fusionados.contains(id))
			// MundoDofus._fusionados.remove(Integer.valueOf(id));
			_objetos.remove(id);
			MundoDofus.eliminarObjeto(id);
			GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(this, id);
		} else {
			objeto.setCantidad(nuevaCant);
			GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(this, objeto);
		}
		_kamas = _kamas + precio;
		// }
		GestorSalida.ENVIAR_As_STATS_DEL_PJ(this);
		GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(this);
		GestorSalida.ENVIAR_ESK_VENDIDO(this);
	}

	public void borrarObjetoRemove(int id) {
		_objetos.remove(id);
	}

	public void borrarObjetoEliminar(int idObjeto, int cantidad, boolean borrarMundoDofus) {
		Objeto obj = _objetos.get(idObjeto);
		if (obj == null) {
			return;
		}
		if (cantidad > obj.getCantidad()) {
			cantidad = obj.getCantidad();
		}
		if (obj.getCantidad() >= cantidad) {
			int nuevaCant = obj.getCantidad() - cantidad;
			if (nuevaCant > 0) {
				obj.setCantidad(nuevaCant);
				if (_enLinea) {
					GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(this, obj);
				}
			} else {
				// if (MundoDofus._fusionados.contains(idObjeto))
				// MundoDofus._fusionados.remove(Integer.valueOf(idObjeto));
				_objetos.remove(obj.getID());
				if (borrarMundoDofus) {
					MundoDofus.eliminarObjeto(obj.getID());
				}
				if (_enLinea) {
					GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(this, obj.getID());
				}
			}
		}
	}

	public Objeto getObjPosicion(int pos) {
		if (pos == -1) {
			return null;
		}
		for (Objeto objeto : _objetos.values()) {
			if (objeto.getPosicion() == pos) {
				return objeto;
			}
		}
		return null;
	}

	public int getObjPosicionCant(int pos) {
		if (pos == -1) {
			return 0;
		}
		int cant = 0;
		for (Objeto objeto : _objetos.values()) {
			if (objeto.getPosicion() == pos) {
				cant += 1;
			}
		}
		return cant;
	}

	private void subirNivelOmega(final boolean expDeNivel, long xp) {

		if (_nivelOmega == Emu.NIVEL_MAX_OMEGA || _encarnacion != null) {
			return;
		}
		_nivelOmega += 1;
		_capital += Emu.PUNTOS_STATS_POR_NIVEL_OMEGA;
		_experiencia += xp;
		while (_experiencia >= MundoDofus.getExpMaxPersonaje(_nivel) && _nivel < Emu.MAX_NIVEL) {
			if (this.getNivel() >= Emu.MAX_NIVEL) {
				break;
			}

		}
		Sucess.launch(this, (byte) 1, null, 0);
	}

	public void refrescarVida(boolean regenera) {
		if (regenera) {
			if (_mapa == null || _pelea != null || _PDV >= _PDVMAX) {
				return;
			}
			_PDV += 1;
		} else {
			float actPdvPorc = 100.0F * _PDV / _PDVMAX;
			if (_encarnacion != null) {
				_PDVMAX = _encarnacion.getPDVMAX();
			} else {
				_PDVMAX = (_nivel - 1) * 5 + CentroInfo.getBasePDV(_clase) + getTotalStats().getEfecto(125);
			}
			_PDV = Math.round(_PDVMAX * actPdvPorc / 100.0F);
		}
	}

	public int nivelessubidos = 0;

	public void subirNivel(boolean addXp, boolean ligadoa) {
		if (_nivel >= Emu.MAX_NIVEL || _encarnacion != null || (_librosRunicos != null)) {
			_experiencia = MundoDofus.getExpNivel(_nivel)._personaje;
			return;
		}
		_nivel++;
		_capital += 5;
		_PDVMAX += 5;
		if (_nivel > 200) {
			_PDVMAX += 5;
		}
		_puntosHechizo++;
		if (_nivel == Emu.NIVEL_PA1) {
			_baseStats.addUnStat(111, Emu.CANTIDAD_PA1);
		}
		if (_nivel == Emu.NIVEL_PM1) {
			_baseStats.addUnStat(128, Emu.CANTIDAD_PM1);
		}
		if (!Emu.SistemaHechizo) {
			CentroInfo.subirNivelAprenderHechizos(this, _nivel);
		}
		if (addXp) {
			_experiencia = MundoDofus.getExpNivel(_nivel)._personaje;
		}
		_PDV = _PDVMAX;
	}

	private boolean revisado = false;

	public void addExp(long xp, boolean ligadoa) {
		if (xp < 0) {
			xp = 0;
		}
		if (_encarnacion != null) {
			_encarnacion.addExp(xp, this);
			return;
		}
		if (_librosRunicos != null) {
			_librosRunicos.addExp(xp/2, this);

			return;
		}
		_experiencia += xp;
		int exNivel = _nivel;
		while (_experiencia >= MundoDofus.getExpMaxPersonaje(_nivel) && _nivel < Emu.MAX_NIVEL) {
			if (this.getNivel() >= Emu.MAX_NIVEL) {
				break;
			}
			subirNivel(false, ligadoa);
		}
		if (_enLinea) {
			if (exNivel < _nivel) {
				try {
					if (getGremio() != null) {
						getMiembroGremio().setNivel(_nivel);
					}
					GestorSalida.ENVIAR_SL_LISTA_HECHIZOS(this);
					GestorSalida.ENVIAR_AN_MENSAJE_NUEVO_NIVEL(this, _nivel);
					actualizarInfoGrupo();
					if (!revisado && Emu.usaReferidos) {
						if (_nivel >= 100) {
							if (!getCuenta()._apodo.equals("")) {
								if (GestorSQL.ES_REFERIDO(getCuenta()._apodo)) {
									GestorSQL.setPuntoCuenta(GestorSQL.getPuntosCuenta(this.getCuentaID()) + 200,
											this.getCuentaID());
									GestorSalida.ENVIAR_cs_CHAT_MENSAJE(this,
											Idiomas.getTexto(this.getCuenta().idioma, 138), Colores.VERDE);
									// GestorSalida.ENVIAR_cs_CHAT_MENSAJE(this, "Has superado el nivel 99 y le has
									// dado <b>+500 Puntos VIP</b> a la persona que te invitÓ a registrarte con su
									// enlace de referido.", Colores.VERDE);
								}
							}
							revisado = true;
						}
					}
				} catch (Exception e) {
					Emu.creaLogs(e);
				}
			}
			GestorSalida.ENVIAR_As_STATS_DEL_PJ(this);
		}
	}

	public void addKamas(long kamas) {
		_kamas += kamas;
	}

	public void setIntercambio(Intercambio inter) {
		_intercambio = inter;
	}

	public Intercambio getIntercambio() {
		return _intercambio;
	}

	public void setTallerInvitado(InvitarTaller inter) {
		_tallerInvitado = inter;
	}

	public InvitarTaller getTallerInvitado() {
		return _tallerInvitado;
	}

	public int aprenderOficio(Oficio oficio, int pos, boolean resta) {
		for (Entry<Integer, StatsOficio> entry : _statsOficios.entrySet()) {
			if (entry.getValue().getOficio().getID() == oficio.getID()) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(this, "Ya has aprendido este oficio anteriormente.", Colores.ROJO);
				return -1;
			}
		}
		int cantOficios = _statsOficios.size();
		if (cantOficios > 32) {
			return -1;
		}
		String totalofi = CentroInfo.getLevelsJobs();
		int oficiosbasicos = totalOficiosBasicos();
		int oficiosfm = totalOficiosFM();
		int puedebasico = Integer.parseInt(totalofi.split("-")[0]);
		int puedefm = Integer.parseInt(totalofi.split("-")[1]);
		int mID = oficio.getID();
		if (mID == 2 || mID == 11 || mID == 13 || mID == 14 || mID == 15 || mID == 16 || mID == 17 || mID == 18
				|| mID == 19 || mID == 20 || mID == 24 || mID == 25 || mID == 26 || mID == 27 || mID == 28 || mID == 31
				|| mID == 36 || mID == 41 || mID == 56 || mID == 58 || mID == 60 || mID == 65) {
			if (oficiosbasicos >= puedebasico) {
				if (_enLinea) {
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(this,
							"SÓlo puedes tener " + oficiosbasicos + " oficios bÁsicos.", Colores.ROJO);
				}
				return -1;
			}
		}
		if (mID == 43 || mID == 44 || mID == 45 || mID == 46 || mID == 47 || mID == 48 || mID == 49 || mID == 50
				|| mID == 62 || mID == 63 || mID == 64) {
			if (oficiosfm >= puedefm) {
				if (_enLinea) {
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(this, "SÓlo puedes tener " + oficiosfm + " oficios especiales.",
							Colores.ROJO);
				}
				return -1;
			}
		}
		StatsOficio statOficio = new StatsOficio(pos, oficio, 1, 0);
		_statsOficios.put(pos, statOficio);
		if (resta) {
			int kamasApostar = 100000;
			long tempKamas = getKamas();
			if (tempKamas < kamasApostar) {
				GestorSalida.ENVIAR_Im_INFORMACION(this, "182");
				return -1;
			}
			setKamas(tempKamas - kamasApostar);
			statOficio.addXP(this, 62491);
		}
		if (_enLinea && resta) {
			ArrayList<StatsOficio> list = new ArrayList<>();
			list.add(statOficio);
			GestorSalida.ENVIAR_Im_INFORMACION(this, "02;" + oficio.getID());
			GestorSalida.ENVIAR_JS_TRABAJO_POR_OFICIO(this, list);
			GestorSalida.ENVIAR_JX_EXPERINENCIA_OFICIO(this, list);
			GestorSalida.ENVIAR_JO_OFICIO_OPCIONES(this, list);
			GestorSalida.ENVIAR_As_STATS_DEL_PJ(this);
			Objeto obj = getObjPosicion(1);
			if (obj != null) {
				if (oficio.herramientaValida(obj.getModelo().getID())) {
					PrintWriter out = _cuenta.getEntradaPersonaje().getOut();
					GestorSalida.ENVIAR_OT_OBJETO_HERRAMIENTA(out, oficio.getID());
					String strOficioPub = CentroInfo.trabajosOficioTaller(oficio.getID());
					if (_mapa.esTaller() && _oficioPublico) {
						GestorSalida.ENVIAR_EW_OFICIO_MODO_INVITACION(out, "+", _ID, strOficioPub);
					}
					_stringOficiosPublicos = strOficioPub;
				}
			}
		}
		return pos;
	}

	void olvidarOficio(int pos) {
		_statsOficios.remove(pos);
	}

	public boolean tieneEquipado(int id) {
		for (Entry<Integer, Objeto> entry : _objetos.entrySet()) {
			Objeto objeto = entry.getValue();
			if (objeto.getModelo().getID() == id && objeto.getPosicion() != -1) {
				return true;
			}
		}
		return false;
	}

	public void setInvitado(int invitando) {
		_invitando = invitando;
	}





	public int getInvitado() {
		return _invitando;
	}

	public String stringInfoGrupo() {
		StringBuilder str = new StringBuilder(_ID + ";");
		str.append(_nombre + ";");
		str.append(_gfxID + ";");
		str.append(_color1 + ";");
		str.append(_color2 + ";");
		str.append(_color3 + ";");
		str.append(getStringAccesorios() + ";");
		str.append(_PDV + "," + _PDVMAX + ";");
		str.append(_nivel + ";");
		str.append(getIniciativa() + ";");
		str.append(getTotalStats().getEfecto(176) + ";");
		str.append("1");
		return str.toString();
	}

	public int getNroObjEquipadosDeSet(int setID) {
		int nro = 0;
		for (Objeto objeto : _objetos.values()) {
			if (objeto.getPosicion() == -1) {
				continue;
			}
			if (objeto.getModelo().getSetID() == setID) {
				nro++;
			}
		}
		return nro;
	}

	public void iniciarAccionEnCelda(AccionDeJuego GA) {
		int celdaID = -1;
		int accion = -1;
		try {
			celdaID = Integer.parseInt(GA._args.split(";")[0]);
			accion = Integer.parseInt(GA._args.split(";")[1]);
		} catch (Exception e) {
		}
		if (celdaID == -1 || accion == -1 || !_mapa.getCelda(celdaID).puedeHacerAccion(accion, _pescarKuakua))
		 {
			return;/// TODO:
		}
		_mapa.getCelda(celdaID).iniciarAccion(this, GA);
	}

	public void finalizarAccionEnCelda(AccionDeJuego AJ) {
		int celdaID = -1;
		try {
			celdaID = Integer.parseInt(AJ._args.split(";")[0]);
		} catch (Exception e) {
		}
		if (celdaID == -1 || AJ == null || _mapa.getCelda(celdaID) == null) {
			return;
		}
		_mapa.getCelda(celdaID).finalizarAccion(this, AJ);
	}

	private int mapaanterior = 7411;
	private int celdaanterior = 450;
	boolean cerrojocasa = false;

	public void teleport(int nuevoMapaID, int nuevaCeldaID) {
		if (penaliza) {
			return;
		}
		continua = false;
		if ((getPelea() != null) || (_tutorial != null)) {
			return;
		}
		PrintWriter out = null;
		if (_cuenta.getEntradaPersonaje() != null) {
			out = _cuenta.getEntradaPersonaje().getOut();
		}
		Mapa nuevoMapa = MundoDofus.getMapa(nuevoMapaID);
		if (nuevoMapa == null || out == null) {
			return;
		}
		if (nuevoMapa.getCelda(nuevaCeldaID) == null) {
			nuevaCeldaID = nuevoMapa.getRandomCeldaIDLibre();
			if (nuevaCeldaID <= 0) {
				return;
			}
		}
		if (gethablandoNPC() != 0) {
			GestorSalida.ENVIAR_DV_FINALIZAR_DIALOGO(out);
			sethablandoNPC(0);
		}
		if (nuevoMapa.getID() == 8757 || nuevoMapa.getID() == 8756 || nuevoMapa.getID() == 2187) {
			if (Emu.tallerAbierto) {
				if (nuevoMapa.getPersos().size() > Emu.LIMITE_ARTESANOS_TALLER) {
					GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(this,
							"No puedes ingresar al taller porque esta lleno, porfavor regresa mÁs tarde o intenta en otro taller.");
					nuevoMapaID = mapaanterior;
					nuevaCeldaID = celdaanterior;
					nuevoMapa = MundoDofus.getMapa(nuevoMapaID);
				}
			} else {
				GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(this,
						"No puedes ingresar al taller porque estÁ cerrado, los talleres sÓlo abren los fines de semana de manera aleatoria.");
				nuevoMapaID = mapaanterior;
				nuevaCeldaID = celdaanterior;
				nuevoMapa = MundoDofus.getMapa(nuevoMapaID);
			}
		}
		_casa = MundoDofus.getCasaDentroPorMapa(nuevoMapaID);
		instante = false;
		GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(_mapa, _ID);
		_mapa.borrarJugador(this);
		_mapa = nuevoMapa;
		setMapaGDM(getMapa());
		_celda = _mapa.getCelda(nuevaCeldaID);
		_mapa.addJugador(this, true);
		mapaanterior = nuevoMapaID;
		celdaanterior = nuevaCeldaID;
		setCargandoMapa(true);
		// Enviamos movimiento y luego carga de mapa completa
		GestorSalida.enviar(this, "GA;2;" + _ID);
		GestorSalida.ENVIAR_GDM_MAPDATA_COMPLETO(this);

		/* String packet = "GA;2;" + _ID + ";" + (char) 0x00 + "GDM|" + getMapa().getID() + "|" + getMapa().getFecha()
				+ "|" + getMapa().getCodigo(); */
		if (getIntercambiandoCon() != 0 || cerrojocasa || getCasa() != null || getTrueque() != null
				|| getEnCercado() != null || getListaArtesanos() || getRompiendo() || getHaciendoTrabajo() != null
				|| getIntercambiandoCon() != 0 || getCofre() != null || getIntercambio() != null || enBanco()
				|| getMochilaMontura() || getHaciendoTrabajo() != null) {
			if (getIntercambiandoCon() > 0) {
				Personaje persxo = MundoDofus.getPersonaje(getIntercambiandoCon());
				if (persxo != null) {
					if (persxo.enLinea()) {
						PrintWriter out1 = persxo.getCuenta().getEntradaPersonaje().getOut();
						persxo.setHaciendoTrabajo(null);
						persxo.setOcupado(false);
						GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(out1);
					}
				}
			}
			if (abrepanelinter) {
				abrepanelinter = false;
			}
			if (getCofre() != null) {
				setCofre(null);
			}
			if (enBanco()) {
				setEnBanco(false);
			}
			if (getIntercambio() != null) {
				getIntercambio().cancel();
			}
			if (getMochilaMontura()) {
				setDragopaveando(false);
				setIntercambio(null);
			}
			if (getRompiendo()) {
				paramag.clear();
				setRompiendo(false);
			}
			if (getTrueque() != null) {
				revivirMascota = false;
				pusomascota = false;
				perfecciona = false;
				itemmision = false;
				pusoperfec = false;
				torrepvm = false;
				elemento = false;
				pusopolvo = false;
				setTrueque(null);
			}
			if (getEnCercado() != null) {
				salirDeCercado();
				setOcupado(false);
			}
			if (getHaciendoTrabajo() != null) {
				getHaciendoTrabajo().resetReceta();
				setHaciendoTrabajo(null);
				setIntercambio(null);
				sethablandoNPC(0);
			}
			if (getListaArtesanos()) {
				setListaArtesanos(false);
			}
			setIntercambiandoCon(0);
			setOcupado(false);
			if (getCasa() != null || cerrojocasa) {
				cerrojocasa = false;
				GestorSalida.ENVIAR_K_CLAVE(this, "V");
				GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(this);
			} else {
				GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(this);
			}
		}
		// GestorSalida.ENVIAR_GDM_MAPDATA_COMPLETO(this);
		if (!_seguidores.isEmpty()) {
			ArrayList<Personaje> seguidores = new ArrayList<>();
			try {
				seguidores.addAll(_seguidores.values());
			} catch (ConcurrentModificationException e) {
			}
			for (Personaje seguido : seguidores) {
				if (seguido._enLinea) {
					GestorSalida.ENVIAR_IC_PERSONAJE_BANDERA_COMPAS(seguido, this);
				} else {
					_seguidores.remove(seguido.getID());
				}
			}
		}
		// NPC INTERACTIVOS :
		// GestorSalida.enviar(this, packet + (char) 0x00 + "#gm" + this.getMapa().celdasTPs);
		switch (nuevoMapaID) {
		case 7549: // Mapa Start
			Interactivos.mensajeNPC(-2, "Banquero", "Hola aventurero, cÓmo te puedo ayudar???", this, nuevoMapaID);
			break;
		case 25046:
			Interactivos.mensajeNPC(-1, "Sadini Cio", "Hola, Bienvenido!!", this, nuevoMapaID);
			if (!accionesPJ.contains(7)) {

				accionesPJ.add(7);
			} else {
				if (!accionesPJ.contains(7)) {
					Interactivos.mensajeNPC(-2, "Reto Mazmorras",
							"Recuerda que tienes que completar todas las Mazmorras", this, nuevoMapaID);
					accionesPJ.add(7);
				} else {
					if (!accionesPJ.contains(8) && !this.acaboMision(1)) {
						Interactivos.mensajeNPC(-6, "DesafÍo Diario", "Tu desafÍo diario estÁ disponible!!!", this,
								nuevoMapaID);
						accionesPJ.add(8);
					} else {
						if (!accionesPJ.contains(9)) {
							Interactivos.mensajeNPC(-4, "Todo Oficios",
									"Necesitas aprender algun oficio?? Yo te puedo ayudar!!!", this, nuevoMapaID);
							accionesPJ.add(9);
						}
					}
				}
			}
			break;
		case 164: // Mapa Shop
			Interactivos.mensajeNPC(-21, "Tienda VIP",
					"Aventurero,Aqui!! Tengo los mejores Objetos del mundo de los Doce", this, nuevoMapaID);
			if (!accionesPJ.contains(10)) {
				Interactivos.mensajeNPC(-12, "Tienda De Mimobiontes",
						"Necesitas algÚn aspecto?? Tengo muy buena mercancÍa", this, nuevoMapaID);
				accionesPJ.add(10);
			} else {
				if (!accionesPJ.contains(11)) {
					accionesPJ.add(11);
				}
			}
			break;
		case 7411: // Astrub
			Interactivos.mensajeNPC(-2, "Maestro [Rx]", "Aventurero! Has llegado al nivel mÁximo? H�blame", this,
					nuevoMapaID);
			if (!accionesPJ.contains(12)) {
				Interactivos.mensajeNPC(-1, "Torre PVM", "Entra y podrÁs ganar mÁs de 30 Nuevos Objetos!!!", this,
						nuevoMapaID);
				accionesPJ.add(12);
			}
			if (!accionesPJ.contains(14)) {
				accionesPJ.add(14);
			} else {
				if (!accionesPJ.contains(13)) {
					Interactivos.mensajeNPC(-6, "Mercado Global",
							"Necesitas vender algo? Yo me puedo ocupar de ello!!!", this, nuevoMapaID);
					accionesPJ.add(13);

				}
			}
			break;
		case 30969: // Incarnam 2.0
			Interactivos.mensajeNPC(-1, "Migu�l �guila", "Ha ocurrido algo terrible!!!", this, nuevoMapaID);
			break;
		case 7396: // Shop 2
			Interactivos.mensajeNPC(-15, "Tienda VIP",
					"Aventurero,Aqui!! Tengo los mejores Objetos del mundo de los Doce", this, nuevoMapaID);
			break;
		case 10354:// incarnam
			if (!accionesPJ.contains(22)) {
				Interactivos.mensajeNPC(-1, "Jefe de los Guardianes Malvados", "Otra cerveza mujer!!!", this,
						nuevoMapaID);
				accionesPJ.add(22);
			} else {
				if (accionesPJ.contains(24)) {
					if (!accionesPJ.contains(25)) {
						Misiones.addListaMisiones(12140, this, true);
						Interactivos.mensajeNPC(-8, "Erem Dran",
								"Gracias por expulsar a esos guardias, te estoy muy agradecida, s� que es mucho pedir, pero... podrÍas encargarte de las araÑas que hay en el sÓtano?? me espantan a los clientes...",
								this, nuevoMapaID);
						accionesPJ.add(25);
					} else {
						if (accionesPJ.contains(27) && !accionesPJ.contains(28)) {
							Interactivos.mensajeNPC(-12, "Tabernero",
									"Gracias por matar a esas AraÑas, aunque tarde o temprano volverÁn...", this,
									nuevoMapaID);
							accionesPJ.add(28);
						}
					}
				}
			}
			break;
		case 28181:// incarnam toros boss
			Interactivos.mensajeNPC(-1, "PapÁ Toro", "CÓmo te atreves a hacer daÑo a mis hijos!!!", this, nuevoMapaID);
			break;
		case 10358:// incarnam taberna
			if (!accionesPJ.contains(23)) {
				Interactivos.mensajeNPC(-5, "Sapo", "Ayuda!!!", this, nuevoMapaID);
				accionesPJ.add(23);
			}
			break;
		case 10258:// incarnam tutorial
			if (_resets >= 3 && !accionesPJ.contains(21)) {
				accionesPJ.add(21);
				Interactivos.tutorialIncarnam(this);
			}
			break;
		case 29997: // impuestopagado
			pagadoImpuesto = false;
			break;
		case 25047: // Inicia tutorial
			if (nuevaCeldaID == 384) {
				if (!accionesPJ.contains(3)) {
					Interactivos.iniciaTutorial(this);
					accionesPJ.add(3);
				}
			}
			break;
		}
		panelGE = "";
	}

	private boolean _cargandoMapa;

	public boolean getCargandoMapa() {
		return _cargandoMapa;
	}

	public void setCargandoMapa(boolean b) {
		this._cargandoMapa = b;
	}

	private Mapa _mapaGDM;

	public void setMapaGDM(Mapa mapa) {
		_mapaGDM = mapa;
	}

	public Mapa getMapaGDM() {
		return _mapaGDM;
	}

	private boolean _agresion = false;

	public void setAgresion(boolean agre) {
		_agresion = agre;
	}

	public boolean getAgresion() {
		return _agresion;
	}

	public int getCostoAbrirBanco() {
		return _cuenta.getBanco().size();
	}

	String getStringVar(String str) {
		if (str.equals("nombre")) {
			return _nombre;
		}
		if (str.equals("costoBanco")) {
			return getCostoAbrirBanco() + "";
		}
		return "";
	}

	public void setKamasBanco(long i) {
		_cuenta.setKamasBanco(i);
		GestorSQL.SALVAR_CUENTA(_cuenta);
	}

	public long getKamasBanco() {
		return _cuenta.getKamasBanco();
	}

	public void setEnBanco(boolean b) {
		_estarBanco = b;
	}

	public boolean enBanco() {
		return _estarBanco;
	}

	public String stringBanco() {
		StringBuilder packet = new StringBuilder("");
		for (Entry<Integer, Objeto> entry : _cuenta.getBanco().entrySet()) {
			packet.append("O" + entry.getValue().stringObjetoConGuiÓn());
		}
		if (getKamasBanco() != 0) {
			packet.append("G" + getKamasBanco());
		}
		return packet.toString();
	}

	public void addCapital(int pts) {
		_capital += pts;
	}

	public void setCapital(int capital) {
		_capital = capital;
	}

	public void addPuntosHechizos(int pts) {
		_puntosHechizo += pts;
	}

	public void addObjAlBanco(int id, int cant) {
		Objeto objAGuardar = MundoDofus.getObjeto(id);
		if ((_objetos.get(id) == null) || (objAGuardar.getPosicion() != -1)) {
			return;
		}
		Objeto objBanco = getSimilarObjetoBanco(objAGuardar);
		int nuevaCant = objAGuardar.getCantidad() - cant;
		if (objBanco == null) {
			if (nuevaCant <= 0) {
				borrarObjetoRemove(objAGuardar.getID());
				_cuenta.getBanco().put(objAGuardar.getID(), objAGuardar);
				String str = "O+" + objAGuardar.getID() + "|" + objAGuardar.getCantidad() + "|"
						+ objAGuardar.getModelo().getID() + "|" + objAGuardar.convertirStatsAString(0);
				GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(this, str);
				GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(this, id);
			} else {
				objAGuardar.setCantidad(nuevaCant);
				objBanco = Objeto.clonarObjeto(objAGuardar, cant);
				MundoDofus.addObjeto(objBanco, true);
				_cuenta.getBanco().put(objBanco.getID(), objBanco);
				String str = "O+" + objBanco.getID() + "|" + objBanco.getCantidad() + "|" + objBanco.getModelo().getID()
						+ "|" + objBanco.convertirStatsAString(0);
				GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(this, str);
				GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(this, objAGuardar);
			}
		} else {
			if (nuevaCant <= 0) {
				borrarObjetoRemove(objAGuardar.getID());
				objBanco.setCantidad(objBanco.getCantidad() + objAGuardar.getCantidad());
				String str = "O+" + objBanco.getID() + "|" + objBanco.getCantidad() + "|" + objBanco.getModelo().getID()
						+ "|" + objBanco.convertirStatsAString(0);
				MundoDofus.eliminarObjeto(objAGuardar.getID());
				GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(this, str);
				GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(this, id);
			} else {
				objAGuardar.setCantidad(nuevaCant);
				objBanco.setCantidad(objBanco.getCantidad() + cant);
				String str = "O+" + objBanco.getID() + "|" + objBanco.getCantidad() + "|" + objBanco.getModelo().getID()
						+ "|" + objBanco.convertirStatsAString(0);
				GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(this, str);
				GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(this, objAGuardar);
			}
		}
		GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(this);
		// GestorSQL.SALVAR_CUENTA(_cuenta);
	}

	private Objeto getSimilarObjetoBanco(Objeto obj) {
		for (Objeto value : _cuenta.getBanco().values()) {
			ObjetoModelo objetoMod = value.getModelo();
			if (objetoMod.getTipo() == 85) {
				continue;
			}
			if (objetoMod.getID() == obj.getModelo().getID() && value.getStats().sonStatsIguales(obj.getStats())) {
				return value;
			}
		}
		return null;
	}

	public void removerDelBanco(int id, int cant) {
		Objeto objBanco = MundoDofus.getObjeto(id);
		if (_cuenta.getBanco().get(id) == null) {
			return;
		}
		Objeto objetoARecibir = getObjSimilarInventario(objBanco);
		int nuevaCant = objBanco.getCantidad() - cant;
		if (objetoARecibir == null) {
			if (nuevaCant <= 0) {
				_cuenta.getBanco().remove(id);
				_objetos.put(id, objBanco);
				GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(this, objBanco);
				String str = "O-" + id;
				GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(this, str);
			} else {
				objetoARecibir = Objeto.clonarObjeto(objBanco, cant);
				objBanco.setCantidad(nuevaCant);
				MundoDofus.addObjeto(objetoARecibir, true);
				_objetos.put(objetoARecibir.getID(), objetoARecibir);
				GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(this, objetoARecibir);
				String str = "O+" + objBanco.getID() + "|" + objBanco.getCantidad() + "|" + objBanco.getModelo().getID()
						+ "|" + objBanco.convertirStatsAString(0);
				GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(this, str);
			}
		} else {
			if (nuevaCant <= 0) {
				_cuenta.getBanco().remove(objBanco.getID());
				objetoARecibir.setCantidad(objetoARecibir.getCantidad() + objBanco.getCantidad());
				GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(this, objetoARecibir);
				MundoDofus.eliminarObjeto(objBanco.getID());
				String str = "O-" + id;
				GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(this, str);
			} else {
				objBanco.setCantidad(nuevaCant);
				objetoARecibir.setCantidad(objetoARecibir.getCantidad() + cant);
				GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(this, objetoARecibir);
				String str = "O+" + objBanco.getID() + "|" + objBanco.getCantidad() + "|" + objBanco.getModelo().getID()
						+ "|" + objBanco.convertirStatsAString(0);
				GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(this, str);
			}
		}
		GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(this);
		// GestorSQL.SALVAR_CUENTA(_cuenta);
	}

	void abrirCercado() {
		if (getDeshonor() >= 5) {
			GestorSalida.ENVIAR_Im_INFORMACION(this, "183");
			return;
		}
		_enCercado = _mapa.getCercado();
		_ocupado = true;
		String str = analizarListaDrago();
		GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(this, 16, str);
	}

	private String analizarListaDrago() {
		StringBuilder packet = new StringBuilder("");
		boolean Primero = false;
		if (_cuenta.getEstablo().size() > 0) {
			for (Dragopavo DD : _cuenta.getEstablo()) {
				if (Primero) {
					packet.append(";");
				}
				packet.append(DD.detallesMontura());
				Primero = true;
				continue;
			}
		}
		packet.append("~");
		if (_enCercado.getListaCriando().size() > 0) {
			boolean primero = false;
			for (Integer pavo : _enCercado.getListaCriando()) {
				Dragopavo dragopavo = MundoDofus.getDragopavoPorID(pavo);
				if (dragopavo.getDueÑo() == _ID) {
					if (primero) {
						packet.append(";");
					}
					packet.append(dragopavo.detallesMontura());
					primero = true;
					continue;
				}
				if (getMiembroGremio() != null) {
					if (getMiembroGremio().puede(CentroInfo.G_OTRASMONTURAS) && _enCercado.getDueÑo() != -1) {
						if (primero) {
							packet.append(";");
						}
						packet.append(dragopavo.detallesMontura());
						primero = true;
					}
				}
			}
		}
		return packet.toString();
	}

	public void salirDeCercado() {
		if (_enCercado == null) {
			return;
		}
		_enCercado = null;
	}

	public Cercado getEnCercado() {
		return _enCercado;
	}

	void fullPDV() {
		_PDV = _PDVMAX;
	}

	public void removerObjetoPorModYCant(int objModeloID, int cantidad) {
		ArrayList<Objeto> lista = new ArrayList<>();
		lista.addAll(_objetos.values());
		ArrayList<Objeto> listaObjBorrar = new ArrayList<>();
		int cantTemp = cantidad;
		for (Objeto obj : lista) {
			if (obj.getModelo().getID() != objModeloID) {
				continue;
			}
			if (obj.getCantidad() >= cantidad) {
				int nuevaCant = obj.getCantidad() - cantidad;
				if (nuevaCant > 0) {
					obj.setCantidad(nuevaCant);
					GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(this, obj);
				} else {
					// if (MundoDofus._fusionados.contains(obj.getID()))
					// MundoDofus._fusionados.remove(Integer.valueOf(obj.getID()));
					_objetos.remove(obj.getID());
					MundoDofus.eliminarObjeto(obj.getID());
					GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(this, obj.getID());
				}
				return;
			} else {
				if (obj.getCantidad() >= cantTemp) {
					int nuevaCant = obj.getCantidad() - cantTemp;
					if (nuevaCant > 0) {
						obj.setCantidad(nuevaCant);
						GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(this, obj);
					} else {
						listaObjBorrar.add(obj);
					}
					for (Objeto objBorrar : listaObjBorrar) {
						// if (MundoDofus._fusionados.contains(objBorrar.getID()))
						// MundoDofus._fusionados.remove(Integer.valueOf(objBorrar.getID()));
						_objetos.remove(objBorrar.getID());
						MundoDofus.eliminarObjeto(objBorrar.getID());
						GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(this, objBorrar.getID());
					}
				} else {
					cantTemp -= obj.getCantidad();
					listaObjBorrar.add(obj);
				}
			}
		}
	}

	public Map<Integer, StatsOficio> getStatsOficios() {
		return _statsOficios;
	}

	void iniciarAccionOficio(int idTrabajo, ObjetoInteractivo objInterac, AccionDeJuego GA, Celda celda) {
		StatsOficio SO = getOficioPorTrabajo(idTrabajo);
		if (SO == null) {
			return;
		}
		SO.iniciarTrabajo(idTrabajo, this, objInterac, GA, celda);
	}

	void finalizarAccionOficio(int idTrabajo, ObjetoInteractivo objInterac, Celda celda) {
		StatsOficio SO = getOficioPorTrabajo(idTrabajo);
		if (SO == null) {
			return;
		}
		SO.finalizarTrabajo(idTrabajo, this, objInterac, celda);
	}

	public String stringOficios() {
		StringBuilder str = new StringBuilder("");
		for (StatsOficio SO : _statsOficios.values()) {
			if (str.length() > 0) {
				str.append(";");
			}
			str.append(SO.getOficio().getID() + "," + SO.getXP());
		}
		return str.toString();
	}

	private int totalOficiosBasicos() {
		int i = 0;
		for (StatsOficio SO : _statsOficios.values()) {
			int idOficio = SO.getOficio().getID();
			if (idOficio == 2 || idOficio == 11 || idOficio == 13 || idOficio == 14 || idOficio == 15 || idOficio == 16
					|| idOficio == 17 || idOficio == 18 || idOficio == 19 || idOficio == 20 || idOficio == 24
					|| idOficio == 25 || idOficio == 26 || idOficio == 27 || idOficio == 28 || idOficio == 31
					|| idOficio == 36 || idOficio == 41 || idOficio == 56 || idOficio == 58 || idOficio == 60
					|| idOficio == 65) {
				i++;
			}
		}
		return i;
	}

	private int totalOficiosFM() {
		int i = 0;
		for (StatsOficio SO : _statsOficios.values()) {
			int idOficio = SO.getOficio().getID();
			if (idOficio == 43 || idOficio == 44 || idOficio == 45 || idOficio == 46 || idOficio == 47 || idOficio == 48
					|| idOficio == 49 || idOficio == 50 || idOficio == 62 || idOficio == 63 || idOficio == 64) {
				i++;
			}
		}
		return i;
	}

	public void setHaciendoTrabajo(AccionTrabajo JA) {
		_haciendoTrabajo = JA;
	}

	public AccionTrabajo getHaciendoTrabajo() {
		return _haciendoTrabajo;
	}

	public StatsOficio getOficioPorTrabajo(int trabajoID) {
		for (StatsOficio SO : _statsOficios.values()) {
			if (SO.esValidoTrabajo(trabajoID)) {
				return SO;
			}
		}
		return null;
	}

	String analizarListaAmigos(int id) {
		StringBuilder str = new StringBuilder(";");
		str.append("?;");
		str.append(_nombre + ";");
		if (_cuenta.esAmigo(id)) {
			str.append(_nivel + ";");
			str.append(_alineacion + ";");
		} else {
			str.append("?;");
			str.append("-1;");
		}
		str.append(_clase + ";");
		str.append(_sexo + ";");
		str.append(_gfxID);
		return str.toString();
	}

	String analizarListaEnemigos(int id) {
		StringBuilder str = new StringBuilder(";");
		str.append("?;");
		str.append(_nombre + ";");
		if (_cuenta.esEnemigo(id)) {
			str.append(_nivel + ";");
			str.append(_alineacion + ";");
		} else {
			str.append("?;");
			str.append("-1;");
		}
		str.append(_clase + ";");
		str.append(_sexo + ";");
		str.append(_gfxID);
		return str.toString();
	}

	public StatsOficio getOficioPorID(int oficio) {
		for (StatsOficio SO : _statsOficios.values()) {
			if (SO.getOficio().getID() == oficio) {
				return SO;
			}
		}
		return null;
	}

	public boolean estaMontando() {
		return _montando;
	}

	public void bajarMontura() {
		_montando = !_montando;
		GestorSalida.ENVIAR_Rr_ESTADO_MONTADO(this, _montando ? "+" : "-");
	}

	private void set_tiempoSubidaBajada(long tiempo) {
		_tiempoUltSubidaBajada = tiempo;
	}

	public void set_tiempoKoli(long tiempo) {
		_tiempoUltKoli = tiempo;
	}

	private long get_tiempoSubidaBajada() {
		return _tiempoUltSubidaBajada;
	}

	public long get_tiempoKoli() {
		return _tiempoUltKoli;
	}

	public void subirBajarMontura() {
		if ((System.currentTimeMillis() - get_tiempoSubidaBajada()) < 5000 || getPelea() != null) {
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(this,
					"Espera 5 segundos antes de volver a subirte/bajarte del dragopavo", Emu.COLOR_MENSAJE);
			return;
		}
		if (!Emu.MONTURAYMASCOTA) {
			if (!_montando) {
				if (getObjPosicion(8) != null || getObjPosicion(170) != null) {
					return;
				}
			}
		}
		if (_encarnacion != null) {
			GestorSalida.ENVIAR_Im_INFORMACION(this, "134|44");
			return;
		}
		if (_montura.getEnergia() <= 0) {
			GestorSalida.ENVIAR_Im_INFORMACION(this, "1113");
			return;
		}
		_montando = (!_montando);
		Objeto mascota = getObjPosicion(8);
		Objeto mascota2 = getObjPosicion(170);
		/*if ((_montando) && (mascota != null || mascota2 != null)) {
			if (mascota != null) {
				GestorSalida.ENVIAR_OM_MOVER_OBJETO(this, mascota);
				mascota.setPosicion(-1);
			}
			if (mascota2 != null) {
				GestorSalida.ENVIAR_OM_MOVER_OBJETO(this, mascota2);
				mascota2.setPosicion(-1);
			}
			_mascota = null;
		}*/
		GestorSalida.ENVIAR_Re_DETALLES_MONTURA(this, "+", _montura);
		if (_pelea == null) {
			GestorSalida.ENVIAR_GM_REFRESCAR_PJ_EN_MAPA(_mapa, this);
		} else if (_pelea.getEstado() == 2) {
			GestorSalida.ENVIAR_GM_REFRESCAR_PJ_EN_PELEA(_pelea, _pelea.getLuchadorPorPJ(this));
		}
		GestorSalida.ENVIAR_Rr_ESTADO_MONTADO(this, _montando ? "+" : "-");
		GestorSalida.ENVIAR_As_STATS_DEL_PJ(this);
		set_tiempoSubidaBajada(System.currentTimeMillis());
		_montura.energiaPerdida(15);
	}

	public int getXpDonadaMontura() {
		return _xpDonadaMontura;
	}

	public Dragopavo getMontura() {
		return _montura;
	}

	public void setMontura(Dragopavo DP) {
		_montura = DP;
	}

	public void setDonarXPMontura(int xp) {
		_xpDonadaMontura = xp;
	}

	public void setEnLinea(boolean linea) {
		_enLinea = linea;
	}

	void resetVariables() {
		_intercambioCon = 0;
		_intercambio = null;
		_conversandoCon = 0;
		_ocupado = false;
		enTorneo = 0;
		_emoteActivado = 0;
		_listo = false;
		_estarBanco = false;
		_invitando = -1;
		_sentado = false;
		_haciendoTrabajo = null;
		rompemineral = false;
		revivirMascota = false;
		pusomascota = false;
		totalEspera = 100;
		actualEspera = 0;
		dueÑoMaitre = null;
		perfecciona = false;
		itemmision = false;
		primerRefresh = false;
		torrepvm = false;
		larva = "";
		elemento = false;
		pusoperfec = false;
		esperaKoliseo = false;
		usaTP = true;
		penaliza = false;
		pusopolvo = false;
		_enZaaping = false;
		_enCercado = null;
		iniciaTorneo = null;
		puedeAbrir = true;
		avisado = false;
		continua = false;
		esperaPelea = null;
		_montando = false;
		_recaudando = false;
		_recaudandoRecaudadorID = 0;
		bucle = false;
		_esDoble = false;
		_olvidandoHechizo = false;
		_ausente = false;
		_invisible = false;
		_trueque = null;
		_tempMapaDefPerco = null;
		_tempCeldaDefPerco = null;
		_cofre = null;
		_casa = null;
		_listaArtesanos = false;
		_cambiarNombre = false;
		_dragopaveando = false;
		_siguiendo = null;
		_tallerInvitado = null;
		_tutorial = null;
		if (esMaitre) {
			esMaitre = false;
		}
		if (liderMaitre) {
			liderMaitre = false;
		}
		panelGE = "";
		_isOnAction = false;
		abrepanelinter = false;
	}

	public void addCanal(String canal) {
		if (_canales.indexOf(canal) >= 0) {
			return;
		}
		_canales += canal;
		GestorSalida.ENVIAR_cC_SUSCRIBIR_CANAL(this, '+', canal);
	}

	public void removerCanal(String canal) {
		_canales = _canales.replace(canal, "");
		GestorSalida.ENVIAR_cC_SUSCRIBIR_CANAL(this, '-', canal);
	}

	public void modificarAlineamiento(byte a) {
		_honor = 0;
		_deshonor = 0;
		_alineacion = a;
		_nivelAlineacion = 1;
		GestorSalida.ENVIAR_ZC_CAMBIAR_ALINEACION(this, a);
		GestorSalida.ENVIAR_GM_REFRESCAR_PJ_EN_MAPA(this.getMapa(), this);
		GestorSalida.ENVIAR_As_STATS_DEL_PJ(this);
	}

	public void setDeshonor(int deshonor) {
		_deshonor = deshonor;
	}

	public int getDeshonor() {
		return _deshonor;
	}

	boolean estaMostrandoAlas() {
		return _mostrarAlas;
	}

	public void setMostrarAlas(boolean mostrarAlas) {
		_mostrarAlas = mostrarAlas;
	}

	public void setHonor(int honor) {
		_honor = honor;
		if (_honor < 0) {
			_honor = 0;
		}
	}

	public int getHonor() {
		return _honor;
	}

	public int getNivelAlineacion() {
		if (_alineacion == -1) {
			return 1;
		}
		return _nivelAlineacion;
	}

	public void botonActDesacAlas(char c) {
		if (_alineacion == -1) {
			return;
		}
		int hloose = _honor * 5 / 100;
		switch (c) {
		case '*':
			GestorSalida.GAME_SEND_GIP_PACKET(this, hloose);
			return;
		case '+':
			_mostrarAlas = true;
			GestorSalida.ENVIAR_As_STATS_DEL_PJ(this);
			break;
		case '-':
			_mostrarAlas = false;
			_honor -= hloose;
			GestorSalida.ENVIAR_As_STATS_DEL_PJ(this);
			break;
		}
	}

	public void addHonor(int honor) {
		int nivelAntes = _nivelAlineacion;
		if (honor > 0) {
			_honor += honor;
			if (honor > 0) {
				GestorSalida.ENVIAR_Im_INFORMACION(this, "080;" + honor + ";");
			}
		} else {
			_honor -= honor;
			if (honor > 0) {
				GestorSalida.ENVIAR_Im_INFORMACION(this, "081;" + honor + ";");
			}
		}
		if (_honor < 0) {
			_honor = 0;
		} else if (_honor >= 17500) {
			_nivelAlineacion = 10;
			_honor = 17500;
		}
		for (int n = 1; n <= 10; n++) {
			if (_honor < MundoDofus.getExpNivel(n)._pvp) {
				_nivelAlineacion = n - 1;
				break;
			}
		}
		if (nivelAntes < _nivelAlineacion) {
			GestorSalida.ENVIAR_Im_INFORMACION(this, "082;" + _nivelAlineacion);
		} else if (nivelAntes > _nivelAlineacion) {
			GestorSalida.ENVIAR_Im_INFORMACION(this, "083;" + _nivelAlineacion);
		}
	}

	void restarHonor(int honor) {
		int nivelAntes = _nivelAlineacion;
		_honor -= honor;
		if (_honor < 0) {
			_honor = 0;
		}
		for (int n = 1; n <= 10; n++) {
			if (_honor < MundoDofus.getExpNivel(n)._pvp) {
				_nivelAlineacion = n - 1;
				break;
			}
		}
		if (nivelAntes == _nivelAlineacion) {
			return;
		} else if (nivelAntes < _nivelAlineacion) {
			GestorSalida.ENVIAR_Im_INFORMACION(this, "082;" + _nivelAlineacion);
		} else if (nivelAntes > _nivelAlineacion) {
			GestorSalida.ENVIAR_Im_INFORMACION(this, "083;" + _nivelAlineacion);
		}
	}

	public MiembroGremio getMiembroGremio() {
		return _miembroGremio;
	}

	public int getCuentaID() {
		return _cuentaID;
	}

	public void setCuenta(Cuenta c) {
		_cuenta = c;
	}

	public String stringListaZaap() {
		String map = _mapa.getID() + "";
		try {
			map = _puntoSalvado.split(",")[0];
		} catch (Exception e) {
		}
		StringBuilder str = new StringBuilder(map + "");
		int SubAreaID = _mapa.getSubArea().getArea().getSuperArea().getID();
		for (int i : _zaaps) {
			if ((MundoDofus.getMapa(i) == null) || (MundoDofus.getMapa(i).getSubArea().getArea().getSuperArea().getID() != SubAreaID)) {
				continue;
			}
			int cost = Formulas.calcularCosteZaap(_mapa, MundoDofus.getMapa(i));
			if (i == _mapa.getID()) {
				cost = 0;
			}
			str.append("|" + i + ";" + cost);
		}
		return str.toString();
	}

	public String stringListaPrismas() {
		if (_mapa == null) {
			return "";
		}
		String map = _mapa.getID() + "";
		StringBuilder str = new StringBuilder(map);
		int SubAreaID = _mapa.getSubArea().getArea().getSuperArea().getID();
		for (Prisma prisma : MundoDofus.TodosPrismas()) {
			if (prisma.getAlineacion() != _alineacion) {
				continue;
			}
			int mapaID = prisma.getMapa();
			if ((MundoDofus.getMapa(mapaID) == null) || (MundoDofus.getMapa(mapaID).getSubArea().getArea().getSuperArea().getID() != SubAreaID)) {
				continue;
			}
			if (prisma.getEstadoPelea() == 0 || prisma.getEstadoPelea() == -2) {
				str.append("|" + mapaID + ";0");
			} else {
				int costo = Formulas.calcularCosteZaap(_mapa, MundoDofus.getMapa(mapaID));
				if (mapaID == _mapa.getID()) {
					costo = 0;
				}
				str.append("|" + mapaID + ";" + costo);
			}
		}
		return str.toString();
	}

	private boolean tieneZaap(int mapID) {
		for (int i : _zaaps) {
			if (i == mapID) {
				return true;
			}
		}
		return false;
	}

	void abrirMenuZaap() {
		if (_pelea == null) {
			if (getDeshonor() >= 3) {
				GestorSalida.ENVIAR_Im_INFORMACION(this, "183");
				return;
			}
			_enZaaping = true;
			if (!tieneZaap(_mapa.getID()) && _mapa.getID() != 18001) {
				_zaaps.add(_mapa.getID());
				this.PuntosPrestigio += Emu.ptsZaapDiscover;
				GestorSalida.ENVIAR_Im_INFORMACION(this, "024");
				GestorSQL.SALVAR_PERSONAJE(this, false);
			}
			GestorSalida.ENVIAR_WC_MENU_ZAAP(this);
		}
	}

	public void abrirMenuPrisma() {
		if (_pelea == null) {
			if (getDeshonor() >= 3) {
				GestorSalida.ENVIAR_Im_INFORMACION(this, "183");
				return;
			}
			_enZaaping = true;
			GestorSalida.ENVIAR_Wp_MENU_PRISMA(this);
		}
	}

	public void usarZaap(short mapaID) {
		if (!_enZaaping || _pelea != null || !tieneZaap(mapaID)) {
			return;
		}
		int costo = Formulas.calcularCosteZaap(_mapa, MundoDofus.getMapa(mapaID));
		if (_kamas < costo) {
			return;
		}
		int superAreaID = _mapa.getSubArea().getArea().getSuperArea().getID();
		int celdaID = MundoDofus.getCeldaZaapPorMapaID(mapaID);
		Mapa zaapMapa = MundoDofus.getMapa(mapaID);
		if (zaapMapa == null) {
			System.out.println("El mapa " + mapaID + " no esta implantado, Zaap rechazada");
			GestorSalida.GAME_SEND_WUE_PACKET(this);
			return;
		}
		if (zaapMapa.getCelda(celdaID) == null) {
			System.out.println("La celda asociada un zaap " + mapaID + " no esta implatado, Zaap rechazada");
			GestorSalida.GAME_SEND_WUE_PACKET(this);
			return;
		}
		if (!zaapMapa.getCelda(celdaID).esCaminable(true)) {
			System.out.println("La celda asociada a un zaap " + mapaID + " no esta 'walkable', Zaap rechazada");
			GestorSalida.GAME_SEND_WUE_PACKET(this);
			return;
		}
		if (zaapMapa.getSubArea().getArea().getSuperArea().getID() != superAreaID) {
			GestorSalida.GAME_SEND_WUE_PACKET(this);
			return;
		}
		if ((_alineacion == 2 && mapaID == 4263) || (_alineacion == 1 && mapaID == 5295)) {
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(this, "El zaap al que deseas ir, es de alineaciÓn enemiga.");
			GestorSalida.GAME_SEND_WUE_PACKET(this);
			return;
		}
		_kamas -= costo;
		teleport(mapaID, celdaID);
		GestorSalida.ENVIAR_As_STATS_DEL_PJ(this);
		GestorSalida.ENVIAR_WV_CERRAR_ZAAP(this);
		_enZaaping = false;
	}

	public String stringZaaps() {
		StringBuilder str = new StringBuilder("");
		boolean primero = false;
		for (int i : _zaaps) {
			if (primero) {
				str.append(",");
			}
			str.append(i + "");
			primero = true;
		}
		return str.toString();
	}

	public void cerrarZaap() {
		if (!_enZaaping) {
			return;
		}
		_enZaaping = false;
		GestorSalida.ENVIAR_WV_CERRAR_ZAAP(this);
	}

	public void cerrarPrisma() {
		if (!_enZaaping) {
			return;
		}
		_enZaaping = false;
		GestorSalida.ENVIAR_Ww_CERRAR_PRISMA(this);
	}

	public void cerrarZaapi() {
		if (!_enZaaping) {
			return;
		}
		_enZaaping = false;
		GestorSalida.ENVIAR_Wv_CERRAR_ZAPPI(this);
	}

	public void usarZaapi(String packet) {
		int mapax = Integer.parseInt(packet.substring(2));
		if (mapax == 9999) {
			if (getCasa() != null) {
				teleport(getCasa().getMapaIDDentro(), getCasa().getCeldaIDDentro());
			}
			return;
		}
		Mapa mapa = MundoDofus.getMapa(mapax);
		short celdaId = 100;
		if (mapa != null) {
			for (Entry<Integer, Celda> entry : mapa.getCeldas().entrySet()) {
				ObjetoInteractivo obj = entry.getValue().getObjetoInterac();
				if (obj != null) {
					if (obj.getID() == 7031 || obj.getID() == 7030) {
						celdaId = (short) (entry.getValue().getID() + 18);
					}
				}
			}
		}
		if (mapa.getSubArea().getArea().getID() == 7 || mapa.getSubArea().getArea().getID() == 11) {
			int price = 20;
			if (getAlineacion() == 1 || getAlineacion() == 2) {
				price = 10;
			}
			if (_kamas < price) {
				_kamas = 0;
			} else {
				_kamas -= price;
			}
			GestorSalida.ENVIAR_As_STATS_DEL_PJ(this);
			teleport(mapax, celdaId);
			GestorSalida.ENVIAR_Wv_CERRAR_ZAPPI(this);
		}
	}

	public void usarPrisma(String packet) {
		int celdaID = 340;
		int mapaID = 7411;
		for (Prisma prisma : MundoDofus.TodosPrismas()) {
			if (prisma.getMapa() == Short.valueOf(packet.substring(2))) {
				celdaID = prisma.getCelda();
				mapaID = prisma.getMapa();
				break;
			}
		}
		int costo = Formulas.calcularCosteZaap(_mapa, MundoDofus.getMapa(mapaID));
		if (mapaID == _mapa.getID()) {
			costo = 0;
		}
		if (_kamas < costo) {
			_kamas = 0;
		} else {
			_kamas -= costo;
		}
		GestorSalida.ENVIAR_As_STATS_DEL_PJ(this);
		teleport(Short.valueOf(packet.substring(2)), celdaID);
		GestorSalida.ENVIAR_Ww_CERRAR_PRISMA(this);
	}

	public boolean tieneObjModeloNoEquip(int id, int cantidad) {
		for (Objeto obj : _objetos.values()) {
			if (obj.getPosicion() != -1 && obj.getPosicion() < 35 || obj.getModelo().getID() != id) {
				continue;
			}
			if (obj.getCantidad() >= cantidad && id == obj.getModelo().getID()) {
				return true;
			}
		}
		return false;
	}

	boolean tieneObjNoEquip(int id, int cantidad) {
		for (Objeto obj : _objetos.values()) {
			if (obj.getPosicion() != -1 && obj.getPosicion() < 35 || obj.getID() != id) {
				continue;
			}
			if (obj.getCantidad() >= cantidad && id == obj.getID()) {
				return true;
			}
		}
		return false;
	}

	public Objeto getObjModeloNoEquip(int id, int cantidad) {
		for (Objeto obj : _objetos.values()) {
			if (obj.getPosicion() != -1 && obj.getPosicion() < 35 || obj.getPosicion() != -1) {
				continue;
			}
			if (obj.getCantidad() >= cantidad && id == obj.getModelo().getID()) {
				return obj;
			}
		}
		return null;
	}

	public Objeto getObjNoEquip(int id, int cantidad) {
		for (Objeto obj : _objetos.values()) {
			if (obj.getPosicion() != -1 && obj.getPosicion() < 35 || obj.getPosicion() != -1) {
				continue;
			}
			if (obj.getCantidad() >= cantidad && id == obj.getID()) {
				return obj;
			}
		}
		return null;
	}

	public Objeto getObjModeloNoEquip(int id, int cantidad, String stats) {
		for (Objeto obj : _objetos.values()) {
			if (obj.getPosicion() != -1 && obj.getPosicion() < 35 || obj.getModelo().getID() != id) {
				continue;
			}
			if (obj.getCantidad() >= cantidad) {
				if (obj.convertirStatsAString(0).equals(stats)) {
					return obj;
				}
			}
		}
		return null;
	}

	public void setZaaping(boolean zaaping) {
		_enZaaping = zaaping;
	}

	public void setOlvidandoHechizo(boolean olvidandoHechizo) {
		_olvidandoHechizo = olvidandoHechizo;
	}

	public boolean estaOlvidandoHechizo() {
		return _olvidandoHechizo;
	}

	public boolean estaDisponible(Personaje perso) {
		if (_ausente) {
			return false;
		}
		if (_invisible) {
			return _cuenta.esAmigo(perso.getCuenta().getID());
		}
		return true;
	}

	public void setSiguiendo(Personaje perso) {
		_siguiendo = perso;
	}

	public Personaje getSiguiendo() {
		return _siguiendo;
	}

	public boolean estaAusente() {
		return _ausente;
	}

	public void setEstaAusente(boolean ausente) {
		_ausente = ausente;
	}

	public boolean esInvisible() {
		return _invisible;
	}

	public boolean esFantasma() {
		return _esFantasma;
	}

	public void setEsInvisible(boolean invisible) {
		_invisible = invisible;
	}

	boolean esDoble() {
		return _esDoble;
	}

	private void esDoble(boolean esDoble) {
		_esDoble = esDoble;
	}

	public boolean getRecaudando() {
		return _recaudando;
	}

	public void setRecaudando(boolean recaudando) {
		_recaudando = recaudando;
	}

	public void setDragopaveando(boolean recaudando) {
		_dragopaveando = recaudando;
	}

	public boolean getMochilaMontura() {
		return _dragopaveando;
	}

	public int getRecaudandoRecauID() {
		return _recaudandoRecaudadorID;
	}

	public void setRecaudandoRecaudadorID(int recaudadorID) {
		_recaudandoRecaudadorID = recaudadorID;
	}

	public void setTitulo(int titulo) {
		_titulo = titulo;
		addTitulos(titulo);
	}

	public void setTituloVIP(int titulo) {
		_titulo = titulo;
		addTitulos(titulo);
	}

	public int getTitulo() {
		return _titulo;
	}

	public boolean cambiarNombre() {
		return _cambiarNombre;
	}

	void cambiarNombre(boolean cambiar) {
		_cambiarNombre = cambiar;
	}

	public void setNombre(String nombre) {
		_nombre = nombre;
		_cambiarNombre = false;
		GestorSQL.ACTUALIZAR_NOMBRE(this);
		if (getMiembroGremio() != null) {
			GestorSQL.ACTUALIZAR_MIEMBRO_GREMIO(getMiembroGremio());
		}
	}

	static Personaje personajeClonado(Personaje perso, int id) {
		TreeMap<Integer, Integer> stats = new TreeMap<>();
		Stats statsBase = perso.getBaseStats();
		stats.put(125, statsBase.getEfecto(125));
		stats.put(118, statsBase.getEfecto(118));
		stats.put(124, statsBase.getEfecto(124));
		stats.put(126, statsBase.getEfecto(126));
		stats.put(123, statsBase.getEfecto(123));
		stats.put(119, statsBase.getEfecto(119));
		stats.put(111, statsBase.getEfecto(111));
		stats.put(128, statsBase.getEfecto(128));
		stats.put(214, statsBase.getEfecto(214));
		stats.put(210, statsBase.getEfecto(210));
		stats.put(213, statsBase.getEfecto(213));
		stats.put(211, statsBase.getEfecto(211));
		stats.put(212, statsBase.getEfecto(212));
		stats.put(160, statsBase.getEfecto(160));
		stats.put(161, statsBase.getEfecto(161));
		byte mostrarAlas = 0;
		int nivelAlineacion = 0;
		if (perso._alineacion != 0 && perso._mostrarAlas) {
			mostrarAlas = 1;
			nivelAlineacion = perso.getNivelAlineacion();
		}
		int monturaID = -1;
		if (perso._montura != null) {
			monturaID = perso._montura.getID();
		}
		Personaje clon = new Personaje(id, perso._nombre, perso._sexo, perso._clase, perso._color1, perso._color2,
				perso._color3, perso._nivel, 100, perso._gfxID, stats, perso._objetos, 100, mostrarAlas, monturaID,
				nivelAlineacion, perso._alineacion);
		clon.esDoble(true);
		if (perso._montando) {
			clon._montando = true;
		}
		return clon;
	}

	public void esposoDe(Personaje esposo) {
		_esposo = esposo.getID();
		GestorSQL.SALVAR_PERSONAJE(this, true);
	}

	public String getEsposoListaAmigos() {
		Personaje esposo = MundoDofus.getPersonaje(_esposo);
		StringBuilder str = new StringBuilder("");
		if (esposo != null) {
			str.append(esposo._nombre + "|" + esposo._clase + esposo._sexo + "|" + esposo._color1 + "|" + esposo._color2
					+ "|" + esposo._color3 + "|");
			if (!esposo._enLinea) {
				str.append("|");
			} else {
				str.append(esposo.stringUbicEsposo() + "|");
			}
		} else {
			str.append("|");
		}
		return str.toString();
	}

	private String stringUbicEsposo() {
		int p = 0;
		if (_pelea != null) {
			p = 1;
		}
		return _mapa.getID() + "|" + _nivel + "|" + p;
	}

	public void casarse(Personaje perso) {
		if (perso == null) {
			return;
		}
		int dist = ((_mapa.getCoordX() - perso._mapa.getCoordX()) * (_mapa.getCoordX() - perso._mapa.getCoordX()))
				+ ((_mapa.getCoordY() - perso._mapa.getCoordY()) * (_mapa.getCoordY() - perso._mapa.getCoordY()));
		if (dist > 100) {
			if (perso.getSexo() == 0) {
				GestorSalida.ENVIAR_Im_INFORMACION(this, "178");
			} else {
				GestorSalida.ENVIAR_Im_INFORMACION(this, "179");
			}
			return;
		}
		int celdaPosicion = CentroInfo.getCeldaIDCercanaNoUsada(perso);
		if (celdaPosicion == 0) {
			if (perso.getSexo() == 0) {
				GestorSalida.ENVIAR_Im_INFORMACION(this, "141");
			} else {
				GestorSalida.ENVIAR_Im_INFORMACION(this, "142");
			}
			return;
		}
		teleport(perso._mapa.getID(), celdaPosicion);
	}

	void divorciar() {
		if (_enLinea) {
			GestorSalida.ENVIAR_Im_INFORMACION(this, "047;" + MundoDofus.getPersonaje(_esposo).getNombre());
		}
		_esposo = 0;
		GestorSQL.SALVAR_PERSONAJE(this, true);
	}

	public int getEsposo() {
		return _esposo;
	}

	public int setEsOK(int ok) {
		return _esOK = ok;
	}

	public int getEsOK() {
		return _esOK;
	}

	void cambiarOrientacion(int orientacion) {
		if (_orientacion == 0 || _orientacion == 2 || _orientacion == 4 || _orientacion == 6) {
			setOrientacion(orientacion);
			GestorSalida.ENVIAR_eD_CAMBIAR_ORIENTACION(_mapa, getID(), orientacion);
		}
	}

	public void setCofre(Cofre cofre) {
		_cofre = cofre;
	}

	public Cofre getCofre() {
		return _cofre;
	}

	public void setCasa(Casa casa) {
		_casa = casa;
	}

	public Casa getCasa() {
		return _casa;
	}

	String stringColorDueÑoPavo() {
		return (_color1 == -1 ? "" : Integer.toHexString(_color1)) + ","
				+ (_color2 == -1 ? "" : Integer.toHexString(_color2)) + ","
				+ (_color3 == -1 ? "" : Integer.toHexString(_color3));
	}

	private String getModeloObjEnPos(int posicion, int posicion2) {
		Objeto objtem = this.getObjPosicion(posicion2);
		if(objtem==null) {
			objtem = getObjPosicion(posicion);
			if (objtem == null) {
				return null;
			}
		}
		/*if (posicion == -1)
			return null;*/
Objeto obj = objtem;
		//for (Objeto obj : _objetos.values()) {
			//if (obj.getPosicion() == posicion) {
				if (obj.getObjeviID() != 0) {
					try {
						Objevivo objevi = MundoDofus.getObjevivos(obj.getObjeviID());
						return Integer.toHexString(objevi.getrealtemplate()) + "~" + objevi.getType() + "~"
								+ objevi.getMascara();
					} catch (Exception e) {
						obj.setObjeviID(0);
						return Integer.toHexString(obj.getIDModelo());
					}
				}
				int realmodelo = obj.getIDModelo();
				try {
					if (obj.getTextoStat().containsKey(2000)) {
						realmodelo = Integer.parseInt(obj.getTextoStat().get(2000).split("\\(")[1].replace(")", ""));
					}
				} catch (Exception e) {
					realmodelo = obj.getIDModelo();
				}
				return Integer.toHexString(realmodelo);

			//}

		//}
		//return null;
	}

	public boolean objetoAInvetario(int id) {
		if (this == null || _intercambioCon != _ID) {
			return false;
		}
		Objeto objMovido = null;
		for (Objeto objeto : _tienda) {
			if (objeto.getID() == id) {
				objMovido = objeto;
				break;
			}
		}
		if (objMovido == null) {
			return false;
		}
		_tienda.remove(objMovido);
		if (addObjetoSimilar(objMovido, true, -1)) {
			MundoDofus.eliminarObjeto(objMovido.getID());
		} else {
			addObjetoPut(objMovido);
			GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(this, objMovido);
		}
		MundoDofus.borrarObjTienda(objMovido.getID());
		return true;
	}

	public int contarTienda() {
		int cantidad = _tienda.size();
		return cantidad;
	}

	public int maxTienda() {
		int cantidad = _nivel / 10;
		return cantidad;
	}

	public String getStringTienda() {
		StringBuilder str = new StringBuilder("");
		boolean prim = false;
		for (Objeto objetos : _tienda) {
			if (!prim) {
				str.append(objetos.getID());
			} else {
				str.append("|" + objetos.getID());
			}
			prim = true;
		}
		return str.toString();
	}

	public void agregarObjTienda(Objeto objeto) {
		_tienda.add(objeto);
	}

	public void borrarObjTienda(Objeto objeto) {
		if (_tienda.contains(objeto)) {
			_tienda.remove(objeto);
			MundoDofus.borrarObjTienda(objeto.getID());
		}
		GestorSQL.SALVAR_PERSONAJE(this, true);
	}

	public String listaTienda() {
		StringBuilder lista = new StringBuilder("");
		boolean esPrimero = true;
		if (_tienda.isEmpty()) {
			return "";
		}
		for (Objeto objeto : _tienda) {
			if (!esPrimero) {
				lista.append("|");
			}
			int idobjeto = objeto.getID();
			Tienda tienda = MundoDofus.getObjTienda(idobjeto);
			if (tienda == null) {
				continue;
			}
			lista.append(idobjeto + ";" + tienda.getCantidad() + ";" + objeto.getModelo().getID() + ";"
					+ objeto.convertirStatsAString(0) + ";" + tienda.getPrecio());
			esPrimero = false;
		}
		return lista.toString();
	}

	public ArrayList<Objeto> getTienda() {
		return _tienda;
	}

	public void actualizarObjTienda(int itemId, int precio) {
		Tienda tienda = MundoDofus.getObjTienda(itemId);
		tienda.setPrecio(precio);
		GestorSQL.ACTUALIZAR_PRECIO_TIENDA(itemId, precio);
	}

	public long precioTotal() {
		long precio = 0;
		for (Objeto obj : _tienda) {
			Tienda tienda = MundoDofus.getObjTienda(obj.getID());
			if (tienda == null) {
				return 0;
			}
			precio += tienda.getPrecio();
		}
		return precio;
	}

	private void refrescarSetClase() {
		for (int j = 2; j < 8; j++) {
			if (getObjPosicion(j) == null) {
				continue;
			}
			Objeto obj = getObjPosicion(j);
			int template = obj.getModelo().getID();
			int set = obj.getModelo().getSetID();
			if ((set >= 81 && set <= 92) || (set >= 201 && set <= 212)) {
				String[] stats = obj.getModelo().getStringStatsObj().split(",");
				for (String stat : stats) {
					String[] val = stat.split("#");
					int efecto = Integer.parseInt(val[0], 16);
					int hechizo = Integer.parseInt(val[1], 16);
					int modif = Integer.parseInt(val[3], 16);
					String modificacion = efecto + ";" + hechizo + ";" + modif;
					GestorSalida.ENVIAR_SB_HECHIZO_BOOST_SET_CLASE(this, modificacion);
					addHechizosSetClase(hechizo, efecto, modif);
				}
				if (!_setClase.contains(template)) {
					_setClase.add(template);
				}
			}
		}
	}

	int getModifSetClase(int hechizo, int efecto) {
		int modif = 0;
		if (_bendHechizo == hechizo && _bendEfecto == efecto) {
			modif += _bendModif;
		}
		if (_hechizosSetClase.containsKey(hechizo)) {
			if (_hechizosSetClase.get(hechizo)._primero == efecto) {
				modif += _hechizosSetClase.get(hechizo)._segundo;
				return modif;
			}
		}
		return modif;
	}

	public String analizarPrismas(int prismaid) {
		String str = "";
		Prisma prisma = MundoDofus.getPrisma(prismaid);
		if (prisma == null) {
			str = "-3";
		} else if (prisma.getEstadoPelea() == 0 && prisma.getPelea() != null) {
			str = "0;"
					+ Math.round(
							Pelea.tiempoPPrisma - (System.currentTimeMillis() - prisma.getPelea()._tiempoInicioPelea))
					+ ";" + Pelea.tiempoPPrisma + ";7";
		} else {
			str = prisma.getEstadoPelea() + "";
		}
		return str;
	}

	public String getObjMisiones() {
		return _objMisiones;
	}

	void setObjTemporal(int ID, int cant) {
		_objTemporal = ID + "," + cant;
	}

	public String getObjTemporal() {
		return _objTemporal;
	}

	private void set_misiones(String misiones) {
		_misiones = misiones;
	}

	public String get_misiones() {
		return _misiones;
	}

	public boolean is_primerentro() {
		return _primerentro;
	}

	public void set_primerentro(boolean primerentro) {
		_primerentro = primerentro;
	}

	public void setNivelOmega(int nivelOmega) {
		this._nivelOmega = nivelOmega;
	}

	public int getNivelOmega() {
		return _nivelOmega;
	}

	public long get_tiempoUltClasificatoria() {
		return _tiempoUltClasificatoria;
	}

	public void set_tiempoUltClasificatoria(long time) {
		_tiempoUltClasificatoria = time;
	}

	public void setColores(int c1, int c2, int c3) {
		_color1 = c1;
		_color2 = c2;
		_color3 = c3;
		GestorSQL.UPDATE_COLORES_PJ(this);
	}

	public int getSeguidor() {
		return _seguidor;
	}

	public void setSeguidor(int seguidor) {
		_seguidor = seguidor;
	}

	void deleteobjBoost(int pos) {
		Objeto obje = this.getObjPosicion(pos);
		if (obje != null) {
			int idObj = obje.getID();
			this.borrarObjetoRemove(idObj);
			MundoDofus.eliminarObjeto(idObj);
			GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(this, idObj);
			GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(this);
			GestorSalida.ENVIAR_As_STATS_DEL_PJ(this);
		}
	}

	private void deleteobjBoostPorObjid(int pos, int ido) {
		Objeto obje = this.getObjPosicion(pos);
		if (obje != null) {
			int idObj = obje.getID();
			if (idObj == ido) {
				this.borrarObjetoRemove(idObj);
				MundoDofus.eliminarObjeto(idObj);
				GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(this, idObj);
				GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(this);
				GestorSalida.ENVIAR_As_STATS_DEL_PJ(this);
			}
		}
	}

	void objBoost(int _args) {
		int objBoost = _args;
		int pos = 20;
		switch (objBoost) {
		case 8169:// evento halloween
		case 8170:
			pos = 20;
			break;
		case 81692:// boost rey
			pos = 22;
			break;
		case 50255:// boost r0
		case 50256:// Bendicion R1
		case 50257:// Bendici�n R2
		case 50258:// Bendici�n R3
		case 50259:// Bendici�n R4
		case 50260:// Bendici�n OM
			pos = 21;
			break;
		case 70115:// bendicion y maldicion de los megas
		case 70116:
			pos = 23;
			break;

		case 30944: //guerrero
		case 30945: //sacer
		case 30946: //mago
			pos = 27;
			GestorSalida.ENVIAR_GM_REFRESCAR_PJ_EN_MAPA(_mapa, this);
			break;

		}
		Objeto obje = this.getObjPosicion(pos);
		if (obje != null) {
			int idObj = obje.getID();
			this.borrarObjetoRemove(idObj);
			MundoDofus.eliminarObjeto(idObj);
			GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(this, idObj);
		}
		Objeto nuevo = MundoDofus.getObjModelo(objBoost).crearObjPosDesdeModelo(1, pos, false);
		if (!this.addObjetoSimilar(nuevo, true, -1)) {
			MundoDofus.addObjeto(nuevo, true);
			this.addObjetoPut(nuevo);
			GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(this, nuevo);
		}
		GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(this);
		GestorSalida.ENVIAR_As_STATS_DEL_PJ(this);
	}

	public Koliseo getKoliseo() {
		return _koli;
	}

	public void setKoliseo(Koliseo koli) {
		_koli = koli;
	}

	public void salirDePelea(String packet, int tipo, boolean enkoliseo, Pelea pelea, boolean ganadores, boolean premio,
			Personaje salirMaitre, boolean enTorneo, boolean angel, int paraespera) {
		ultimaPelea = System.currentTimeMillis();
		int tipoSalida = 0;
		int aplicafinal = -1;
		if (panelGE.contains("NO")) {
			panelGE = "";
		} else {
			panelGE = packet;
		}
		if (!ganadores) {// para perdedores
			if (pelea != null) {
				Luchador luch = pelea.getLuchadorPorPJ(this);
				if (luch != null) {
					if (luch._desconectado) {
						luch._estaMuerto = true;
						setEnLinea(false);
						setReconectado(false);
						resetVariables();
						GestorSQL.SALVAR_PERSONAJE(this, true);
					}
				}
			}
			if (tipo < 2) {
				if (enkoliseo) {
					if (getKoliseo() != null) {
						getKoliseo().perdedor(this);
					}
					tipoSalida = 1;
				} else {
					if (enTorneo) {
						tipoSalida = 1;
						Estaticos.perderTorneo(this);
					} else {
						tipoSalida = 0;
					}
				}
			} else {
				if ((tipo == 5 || tipo == 2) && getMapaDefPerco() != null) {
					tipoSalida = 2;
				} else {
					tipoSalida = 1;
				}
			}
			if (getMapa().getID() == 10357) {
				setGfxID(8037);
			}
			// restarVidaMascota(null);
		} else {// para ganadores
			if (tipo < 2) {
				if (enkoliseo) {
					if (getKoliseo() != null) {
						getKoliseo().ganador(this);
					}
					tipoSalida = 1;
				} else {
					if (enTorneo) {
						tipoSalida = 1;
						this.PuntosPrestigio += Emu.ptsTorneo;
						Estaticos.ganadorTorneo(this);
					} else {
						tipoSalida = 0;
					}
				}
			} else {
				if ((tipo == 5 || tipo == 2) && getMapaDefPerco() != null) {
					tipoSalida = 2;
				} else if ((tipo == 5 || tipo == 2) && getMapaDefPerco() == null) {
					if (pelea._Recaudador != null) {
						if (getGremio() != null) {
							if (getGremio().getID() != pelea._Recaudador.getGremioID()
									&& getGremio().lastidpelea != pelea.getID()) {


								getGremio().puntosextra += 5;
								getGremio().lastidpelea = pelea.getID();
								for (Personaje z : getGremio().getPjMiembros()) {
									GestorSalida.ENVIAR_cs_CHAT_MENSAJE(z,
											"El gremio ha ganado <b>+5</b> puntos de poder por derrotar a un recaudador enemigo",
											Colores.VERDE);
								}
							}
						}
					}
					tipoSalida = 0;

				} else {
					tipoSalida = 0;
				}
			}
			if (tipo == 1 || tipo == 0) {
				if ((getGfxID() == 1111 || getGfxID() == 1117)) {
					deleteobjBoostPorObjid(21, 8169);
					deleteobjBoostPorObjid(21, 8170);
					if (getGfxID() == 1111 || getGfxID() == 1117) {
						deformar();
					}
				}
			}
			aplicafinal = tipo;
			if (angel) {
				Invasion.darPremio(this, true);
			}
			if (premio) {
				Invasion.darPremio(this, false);
			}
			if (getMapa().getID() == 10357) {
				if (getGfxID() == 8037) {
					deformar();
				}
			}
		}
		if (tipo == 5) {
			if (pelea._Recaudador != null) {
				GestorSalida.ENVIAR_gITP_INFO_DEFENSORES_RECAUDADOR(this, pelea.getListaDefensores());
			}
		} else if (tipo == 2) {
			if (pelea._Prisma != null) {
				GestorSalida.ENVIAR_CP_INFO_DEFENSORES_PRISMA(this, pelea.getListaDefensores());
			}
		}
		Marche = false;
		Personaje perso = this;
		int tipoSalidax = tipoSalida;
		int aplicafinalx = aplicafinal;
		get_acciones().clear();
		if (salirMaitre != null && salirMaitre == perso) {
			totalEspera = paraespera;
		} else {
			dueÑoMaitre = salirMaitre;
		}
		if (perso.actualizainv) {
			perso.getTotalCombate();
			perso.combateactual += 1;
			perso.actualizainv = false;
		}
		updateObjetivoRecolecta(0, 0, "matar", false);// lo coge del panel ge
		switch (tipoSalidax) {
		case 0:
			perso.retornoMapa();
			GestorSalida.ENVIAR_GV_RESETEAR_PANTALLA_JUEGO(perso);
			if (aplicafinalx != -1) {
				if (perso.getMapa()._accionFinPelea.get(aplicafinalx) != null) {
					perso.getMapa().aplicarAccionFinCombate(aplicafinalx, perso);
				}
			}
			break;
		case 1: {
			if (ganadores) {
				this.PuntosPrestigio += Emu.ptsPvP;
			}
			perso.retornoPtoSalvada();
			break;
		}
		case 2: {
			if (ganadores) {
				this.PuntosPrestigio += Emu.ptsPrism;
			}
			perso.retornoPtoSalvadaRecau();
			break;
		}
		case 5: {
			if (ganadores) {
				this.PuntosPrestigio += Emu.ptsRecaudador;
			}
			perso.retornoPtoSalvadaRecau();
			break;
		}
		default:
			perso.retornoMapa();
			GestorSalida.ENVIAR_GV_RESETEAR_PANTALLA_JUEGO(perso);
			break;
		}
		return;
	}

	public int totalEspera = 100;
	public int actualEspera = 0;
	public Personaje dueÑoMaitre = null;

	public void crearItem(int itemid, int cantidad) {
		ObjetoModelo OM = MundoDofus.getObjModelo(itemid);
		if (OM == null) {
			return;
		}
		if (cantidad < 1) {
			cantidad = 1;
		}
		Objeto obj = OM.crearObjDesdeModelo(cantidad, false);
		if (!addObjetoSimilar(obj, true, -1)) {
			MundoDofus.addObjeto(obj, true);
			addObjetoPut(obj);
			GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(this, obj);
		}
		GestorSalida.ENVIAR_Im_INFORMACION(this, "021;" + cantidad + "~" + obj.getModelo().getID());
		GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(this);
	}

	void salirEspectador(boolean sacalo) {
		if (getPelea() != null) {
			if (getPelea()._espectadores.containsKey(this.getID())) {
				getPelea()._espectadores.remove(this.getID());
			}
		}
		salirEspectador();
	}

	public boolean tieneMisione(int mision) {
		for (String exm : _misiones.split("\\|")) {
			if (exm.equals("")) {
				continue;
			}
			int misID = Integer.parseInt(exm.split(";")[0]);
			if (misID == mision) {
				return true;
			}
		}
		return false;
	}

	void salirEspectador() {
		setPelea(null);
		setOcupado(false);
		setDueloID(-1);
		setListo(false);
		panelGE = "";
		retornoMapa();
		GestorSalida.ENVIAR_GV_RESETEAR_PANTALLA_JUEGO(this);
	}

	public String getCanjeados() {
		StringBuilder strx = new StringBuilder();
		boolean prim = false;
		for (Integer str : Canjeados) {
			if (prim) {
				strx.append(",");
			}
			strx.append(str);
			prim = true;
		}
		return strx.toString();
	}

	public int getPointsSucces() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setPointsSucces1(int i) {
		// TODO Auto-generated method stub

	}

	public void setPointsSucces(int i) {
		// TODO Auto-generated method stub

	}

	public void usarZonas(final short mapaID) {
		try {
			if (MundoDofus.getMapa(mapaID) == null || MundoDofus.ZONAS.get(mapaID) == null) {
				return;
			}
			teleport(mapaID, MundoDofus.ZONAS.get(mapaID));
		} catch (final Exception ignored) {}
	}


	
	// Este método contiene la lógica de niveles y acciones iniciales que estaba en tu código original
	private void verificarNivelYAccionesIniciales() {
	    if (this.getNivel() > Emu.MAX_NIVEL) {
	        _nivel = Emu.MAX_NIVEL;
	    }
	    
	    // Si es la primera vez que entra y no tiene la acción 15
	    if (!accionesPJ.contains(15) && !primeravez) {
	        resetearStats();
	        setCapital(0);
	        setPtosHechizos(0);
	        setNivel(1);
	        setExperiencia(MundoDofus.getExpNivel(1)._personaje);
	        
	        if (!Emu.SistemaHechizo) {
	            _hechizos = CentroInfo.getHechizosIniciales(this.getClase(true));
	            _lugaresHechizos = CentroInfo.getLugaresHechizosIniciales(this.getClase(true));
	            for (int a = 1; a <= this.getNivel(); a++) {
	                CentroInfo.subirNivelAprenderHechizos(this, a);
	            }
	        }
	        
	        // Subir hasta el nivel inicial configurado
	        while (getNivel() < Emu.INICIAR_NIVEL) {
	            if (getNivel() >= Emu.MAX_NIVEL) {
	                break;
	            }
	            subirNivel(false, false);
	        }
	        accionesPJ.add(15);
	    }

	    // Lógica de títulos por ranking
	    if (_titulo == 8 && MundoDofus.liderRanking != this.getNombre()
	            || _titulo == 9 && MundoDofus.liderRanking2 != this.getNombre()
	            || _titulo == 10 && MundoDofus.liderRanking3 != this.getNombre()) {
	        setTitulo(0);
	    }
	}

	// Este método maneja el sistema de selección de hechizos por etapas
	private void enviarHechizosPorEtapa() {
	    if (Emu.SistemaHechizo) {
	        if (etapa < 22) {
	            if (actualHechizos.equals("")) {
	                actualHechizos = MundoDofus.getHechizoAprender(etapa, this);
	            }
	            GestorSalida.enviar(this, "#kd" + actualHechizos + "," + etapa + ";" + yaactualizo);
	        } else if (etapa == 22) {
	            if (buffClase == -1) {
	                GestorSalida.enviar(this, "#kdSTOP");
	            }
	        }
	    } else if (buffClase == -1) {
	        GestorSalida.enviar(this, "#kdSTOP");
	    }
	}



}
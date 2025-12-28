package variables;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.Timer;

import estaticos.Camino;
import estaticos.CentroInfo;
import estaticos.CondicionJugador;
import estaticos.Emu;
import estaticos.Encriptador;
import estaticos.Formulas;
import estaticos.GestorSQL;
import estaticos.GestorSalida;
import estaticos.MundoDofus;
import estaticos.MundoDofus.ObjInteractivoModelo;
import estaticos.MundoDofus.SubArea;
import servidor.Colores;
import servidor.EntradaPersonaje.AccionDeJuego;
import servidor.Estaticos;
import variables.MobModelo.GrupoMobs;
import variables.MobModelo.MobGrado;
import variables.NPCModelo.NPC;
import variables.Pelea.Luchador;

public class Mapa {
	int _sigIDMapaInfo = -1;
	private int _id;
	private String _fecha;
	private short _ancho;
	private short _alto;
	private String _key;
	private String _posicionesDePelea;
	private Map<Integer, Celda> _celdas = new TreeMap<>();
	private Map<Integer, Pelea> _peleas = new TreeMap<>();
	private ArrayList<MobGrado> _mobPosibles = new ArrayList<>();
	Map<Integer, GrupoMobs> _grupoMobs = new TreeMap<>();
	private Map<Integer, GrupoMobs> _grupoMobsFix = new TreeMap<>();
	private Map<Integer, NPC> _npcs = new TreeMap<>();
	private ArrayList<Integer> _mercante = new ArrayList<>();
	private ArrayList<Celda> _casillasObjInterac = new ArrayList<>();
	private short _X = 0;
	private short _Y = 0;
	private SubArea _subArea;
	private Cercado _cercado;
	private short _maxGrupoDeMobs = 3;
	Map<Integer, ArrayList<Accion>> _accionFinPelea = new TreeMap<>();
	private int _maxMobsPorGrupo;
	private int _capacidadMercantes = 0;
	private String _mapData = "";
	private String _mapDataBACKUP = "";
	private int _descripcion = 0;
	private int _capabilities;
	private int _bgID;
	private int _musicID;
	private int _ambienteID;
	private int _outDoor;
	public boolean tienekey = false;
	public Map<Integer, String> _personaliza = new HashMap<>();
	public Map<Integer, String> _limpieza = new HashMap<>();
	private Map<Personaje, Integer> _personajes = new HashMap<>(); // perso, celda
	private Map<Luchador, Short> _luchadores = new HashMap<>();
	public StringBuilder celdasTPs = new StringBuilder();

	public Mapa(int ID, String fecha, byte ancho, byte alto, String key, String posicionesDePelea, String mapData,
			String mobs, byte X, byte Y, int subarea, byte maxGrupoDeMobs, int maxMobsPorGrupo, int capacidad,
			int descripcion, int bgID, int musicID, int ambienteID, int outDoor, int parametros, String personalizado,
			String limpieza) {
		_id = ID;
		_fecha = fecha;
		_ancho = ancho;
		_alto = alto;
		_key = key;
		_posicionesDePelea = posicionesDePelea;
		_maxGrupoDeMobs = (byte) (maxGrupoDeMobs + 1);
		_maxMobsPorGrupo = maxMobsPorGrupo;
		_capacidadMercantes = capacidad;
		_descripcion = descripcion;
		if (!key.isEmpty()) {
			tienekey = true;
		} else {
			if (!key.trim().isEmpty()) {
				mapData = Encriptador.decifrarMapData(key, mapData);
			}
		} // cellid,objid,original;
		for (String pers : personalizado.split(";")) {
			if (pers.isEmpty()) {
				continue;
			}
			int celdaid = Integer.parseInt(pers.split(",")[0]);
			String objid = pers.split(",")[1];
			String origin = pers.split(",")[2];
			int objdev = Integer.parseInt(pers.split(",")[3]);
			_personaliza.put(celdaid, objid + "," + origin + "," + objdev);
		}
		for (String pers : limpieza.split(";")) {
			if (pers.isEmpty()) {
				continue;
			}
			int celdaid = Integer.parseInt(pers.split(",")[0]);
			String modif = pers.split(",")[1];
			String origin = pers.split(",")[2];
			_limpieza.put(celdaid, modif + "," + origin);
		}
		_mapDataBACKUP = mapData;
		_mapData = Encriptador.decompilarMapaDataOBJ(mapData, _personaliza, _limpieza, _id, this);
		_celdas = Encriptador.decompilarMapaData(this, _mapData);
		this._bgID = bgID;
		this._musicID = musicID;
		this._ambienteID = ambienteID;
		this._outDoor = outDoor;
		this._capabilities = parametros;
		try {
			_X = X;
			_Y = Y;
			_subArea = MundoDofus.getSubArea(subarea);
			if (_subArea != null) {
				_subArea.addMapa(this);
			}
		} catch (Exception e) {
			System.out.println("Error al cargar el mapa ID " + ID + ": El campo MapPos es invalido");
			System.exit(0);
		}
		for (String mob : mobs.split("\\|")) {
			if (mob.equals("")) {
				continue;
			}
			int id = 0;
			int nivel = 0;
			try {
				id = Integer.parseInt(mob.split(",")[0]);
			} catch (Exception e) {
				id = 0;
			}
			try {
				nivel = Integer.parseInt(mob.split(",")[1]);
			} catch (Exception e) {
				nivel = 0;
			}
			if ((id == 0) || (MundoDofus.getMobModelo(id) == null)) {
				continue;
			}
			if (MundoDofus.getMobModelo(id).getGradoPorNivel(nivel) == null) {
				nivel = MundoDofus.getMobModelo(id).getRandomGrado().getNivel();
			}
			if (MundoDofus.getMobModelo(id).getGradoPorNivel(nivel) == null) {
				continue;
			}
			if (Emu.Halloween) {
				switch (id) {
				case 98:// Tofu
					if (MundoDofus.getMobModelo(794) != null) {
						if (MundoDofus.getMobModelo(794).getGradoPorNivel(nivel) != null) {
							id = 794;
						}
					}
					break;
				case 101:// Bouftou
					if (MundoDofus.getMobModelo(793) != null) {
						if (MundoDofus.getMobModelo(793).getGradoPorNivel(nivel) != null) {
							id = 793;
						}
					}
					break;
				}
			}
			_mobPosibles.add(MundoDofus.getMobModelo(id).getGradoPorNivel(nivel));
		}
		if (Emu.USAR_MOBS) {
			if (_maxGrupoDeMobs == 0) {
				return;
			}
			spawnGrupo(-1, _maxGrupoDeMobs, false, -1);// neutral
			spawnGrupo(1, 1, false, -1);// bontariano
			spawnGrupo(2, 1, false, -1);// brakmariano
		}
	}

	public Map<Luchador, Short> getLuchadores(int celda) {
		Map<Luchador, Short> luchadores = new HashMap<>();
		Map<Luchador, Short> luchador = new HashMap<>();
		luchador.putAll(_luchadores);
		for (Entry<Luchador, Short> entry : luchador.entrySet()) {
			if (entry.getValue() == celda) {
				luchadores.put(entry.getKey(), entry.getValue());
			}
		}
		return luchadores;
	}

	public boolean luchadoresen(int celda) {
		for (Short entry : _luchadores.values()) {
			if (celda == entry) {
				return true;
			}
		}
		return false;
	}

	public Luchador getPrimerLuchador(int celda) {
		if (_luchadores.isEmpty()) {
			return null;
		}
		for (Entry<Luchador, Short> entry : _luchadores.entrySet()) {
			if (entry == null) {
				continue;
			}
			if (celda == entry.getValue()) {
				return entry.getKey();
			}
		}
		return null;
	}

	void addLuchador(Luchador luchador) {
		if (luchador == null) {
			return;
		}
		if (!_luchadores.containsKey(luchador)) {
			_luchadores.put(luchador, luchador.getCeldaPelea().getID());
		} else {
			_luchadores.replace(luchador, luchador.getCeldaPelea().getID());
		}
	}

	void removerLuchador(Luchador luchador) {
		if (_luchadores.containsKey(luchador)) {
			_luchadores.remove(luchador);
		}
	}

	private void addCasillaObjInteractivo(Celda celda) {
		_casillasObjInterac.add(celda);
	}

	public ArrayList<Celda> getCasillasObjInter() {
		return _casillasObjInterac;
	}

	public int getBgID() {
		return this._bgID;
	}

	public int getMusicID() {
		return this._musicID;
	}

	public int getAmbienteID() {
		return this._ambienteID;
	}

	public int getOutDoor() {
		return this._outDoor;
	}

	public int getCapabilities() {
		return this._capabilities;
	}

	void aplicarAccionFinCombate(int tipo, Personaje perso) {
		if (_accionFinPelea.get(tipo) == null) {
			return;
		}
		for (Accion accion : _accionFinPelea.get(tipo)) {
			accion.aplicar(perso, null, -1, -1);
		}
	}

	public void setDescripcion(int d) {
		_descripcion = d;
	}

	public boolean esTaller() {
		return (_descripcion & 1) == 1;
	}

	boolean esArena() {
		return (_descripcion & 2) == 2;
	}

	boolean esMazmorra() {
		return (_descripcion & 4) == 4;
	}

	public boolean esPVP() {
		return (_descripcion & 8) == 8;
	}

	boolean esCasa() {
		return (_descripcion & 16) == 16;
	}

	public void addAccionFinPelea(int tipo, Accion accion) {
		if (_accionFinPelea.get(tipo) == null) {
			_accionFinPelea.put(tipo, new ArrayList<>());
		}
		// delAccionFinPelea(tipo, accion.getID());
		_accionFinPelea.get(tipo).add(accion);
	}

	public void borrarTodoAcciones() {
		_accionFinPelea.clear();
	}

	public void setCercado(Cercado cercado) {
		_cercado = cercado;
	}

	public Cercado getCercado() {
		return _cercado;
	}

	private Mapa(int id, String fecha, short _ancho2, short _alto2, String key, String posPelea) {
		_id = id;
		_fecha = fecha;
		_ancho = _ancho2;
		_alto = _alto2;
		_key = key;
		_posicionesDePelea = posPelea;
		_celdas = new TreeMap<>();
	}

	public SubArea getSubArea() {
		return _subArea;
	}

	public int getCapacidad() {
		return _capacidadMercantes;
	}

	public int getCoordX() {
		return _X;
	}

	public int getCoordY() {
		return _Y;
	}

	public String getMapData() {
		return _mapData;
	}

	public String getMapDataBack() {
		return _mapDataBACKUP;
	}

	public void setMapData(String mapdata) {
		_mapData = mapdata;
	}

	public Map<Integer, NPC> getNPCs() {
		return _npcs;
	}

	public NPC addNPC(int npcID, int celdaID, int dir) {
		NPCModelo npcModelo = MundoDofus.getNPCModelo(npcID);
		if (npcModelo == null || getCelda(celdaID) == null) {
			return null;
		}
		NPC npc = new NPC(npcModelo, _sigIDMapaInfo, celdaID, (byte) dir, npcModelo.getNombre());
		_npcs.put(_sigIDMapaInfo, npc);
		_sigIDMapaInfo--;
		return npc;
	}

	public ArrayList<Integer> removerMercante(int personaje) {
		if (_mercante.size() < 1) {
			return _mercante;
		}
		if (_mercante.contains(personaje)) {
			int index = _mercante.indexOf(personaje);
			_mercante.remove(index);
		}
		return _mercante;
	}

	public ArrayList<Integer> agregarMercante(int personaje) {
		if ((_mercante.size() >= _capacidadMercantes) || _mercante.contains(personaje)) {
			return _mercante;
		}
		_mercante.add(personaje);
		return _mercante;
	}

	public ArrayList<Integer> addMercantesMapa(String personajes) {
		if (personajes == "|" || personajes.isEmpty()) {
			return null;
		}
		String[] persos = personajes.split("\\|");
		for (String personaje : persos) {
			if (personaje.equals("")) {
				continue;
			}
			_mercante.add(Integer.parseInt(personaje));
		}
		return _mercante;
	}

	public String getMercantes() {
		StringBuilder personajes = new StringBuilder("");
		boolean primero = true;
		if (_mercante.size() == 0) {
			return personajes.toString();
		}
		for (Integer personaje : _mercante) {
			if (!primero) {
				personajes.append("|");
			}
			personajes.append(Integer.toString(personaje));
			primero = false;
		}
		return personajes.toString();
	}

	public int cantMercantes() {
		int cantidad = _mercante.size();
		return cantidad;
	}

	private GrupoMobs ultGrp = null;

	void refrescarFix(int celda) {
		GrupoMobs grupo = null;
		for (Entry<Integer, GrupoMobs> gr : _grupoMobs.entrySet()) {
			if (gr.getValue().getCeldaID() == celda) {
				grupo = gr.getValue();
				ultGrp = grupo;
				grupo.setEsFixeado(true);
				break;
			}
		}
		if (grupo == null) {
			if (ultGrp == null) {
				return;
			} else {
				grupo = ultGrp;
			}
		}
		_sigIDMapaInfo--;
		try {
			Thread.sleep(400L);
		} catch (InterruptedException e) {
		}
		GestorSalida.ENVIAR_GM_GRUPOMOB_A_MAPA(this, grupo);
	}

	private GrupoMobs _ultimoGrupo = null;

	public void setUltimoGrupoMob(GrupoMobs mob) {
		_ultimoGrupo = mob;
	}

	public void addGrupoFix(int celda, String strGrupoMob) {
		GrupoMobs grupoMob;
		if (_ultimoGrupo == null) {
			grupoMob = new GrupoMobs(_sigIDMapaInfo, celda, strGrupoMob);
			if (grupoMob.getMobs().isEmpty()) {
				return;
			}
			_sigIDMapaInfo--;
		} else {
			grupoMob = _ultimoGrupo;
			_ultimoGrupo = null;
		}
		_grupoMobs.put(grupoMob.getID(), grupoMob);
		_grupoMobsFix.put(grupoMob.getID(), grupoMob);
		GestorSalida.ENVIAR_GM_GRUPOMOB_A_MAPA(this, grupoMob);
	}

	void spawnGrupo(int alineacion, int cantGrupos, boolean log, int celdaID) {
		if ((cantGrupos < 1) || (_grupoMobs.size() - _grupoMobsFix.size() >= _maxGrupoDeMobs)) {
			return;
		}
		for (int a = 1; a <= cantGrupos; a++) {
			GrupoMobs grupo = new GrupoMobs(_sigIDMapaInfo, alineacion, _mobPosibles, this, celdaID, _maxMobsPorGrupo);
			if (grupo.getMobs().isEmpty()) {
				continue;
			}
			_grupoMobs.put(_sigIDMapaInfo, grupo);
			grupo.setEsFixeado(false);
			_sigIDMapaInfo--;
			if (log) {
				try {
					Thread.sleep(500L);
				} catch (InterruptedException e) {
				}
				GestorSalida.ENVIAR_GM_GRUPOMOB_A_MAPA(this, grupo);
			}
		}
	}

	public void addGrupoDeUnaPelea(int celda, String grupoMob) {
		GrupoMobs grupo = new GrupoMobs(_sigIDMapaInfo, celda, grupoMob);
		if (grupo.getMobs().isEmpty()) {
			return;
		}
		_grupoMobs.put(_sigIDMapaInfo, grupo);
		grupo.setEsFixeado(false);
		if (Emu.MOSTRAR_RECIBIDOS) {
			System.out.println("Grupo de mounstros agregados al mapa: " + _id + " ID: " + _sigIDMapaInfo);
		}
		GestorSalida.ENVIAR_GM_GRUPOMOB_A_MAPA(this, grupo);
		_sigIDMapaInfo--;
	}

	public void setPosicionesDePelea(String posiciones) {
		_posicionesDePelea = posiciones;
	}

	public NPC getNPC(int id) {
		return _npcs.get(id);
	}

	public NPC removeNPC(int id) {
		return _npcs.remove(id);
	}

	public Celda getCelda(int id) {
		return _celdas.get(id);
	}

	public ArrayList<Personaje> getPersos(Personaje persox) {
		ArrayList<Personaje> personajes = new ArrayList<>();
		for (Personaje perso : _personajes.keySet()) {
			if (perso == null) {
				continue;
			}
			if (persox != null) {
				if (persox == perso) {
					continue;
				}
			}
			if (perso.enLinea() && perso.is_primerentro()) {
				personajes.add(perso);
			}
		}
		return personajes;
	}

	public ArrayList<Personaje> getPersos() {
		ArrayList<Personaje> personajes = new ArrayList<>();
		for (Personaje perso : _personajes.keySet()) {
			if (perso == null) {
				continue;
			}
			if (perso.enLinea() && perso.is_primerentro()) {
				personajes.add(perso);
			}
		}
		return personajes;
	}

	public ArrayList<Integer> getPersosID() {
		ArrayList<Integer> personajes = new ArrayList<>();
		for (Personaje perso : _personajes.keySet()) {
			if (perso == null) {
				continue;
			}
			if (perso.enLinea() && perso.is_primerentro()) {
				personajes.add(perso.getID());
			}
		}
		return personajes;
	}

	public void borrarJugador(Personaje perso) {
		if (_personajes.containsKey(perso)) {
			_personajes.remove(perso);
		}
	}

	public int getID() {
		return _id;
	}

	public String getFecha() {
		return _fecha;
	}

	public short getAncho() {
		return _ancho;
	}

	public int ultimaCeldaID() {
		int celda = (_ancho * _alto * 2) - (_alto + _ancho);
		return celda;
	}

	private boolean esCeldaLadoIzq(int celda) {
		int ladoIzq = _ancho;
		for (int i = 0; i < _alto; i++) {
			if (celda == ladoIzq) {
				return true;
			}
			ladoIzq = ladoIzq + (_ancho * 2) - 1;
		}
		return false;
	}

	private boolean esCeldaLadoDer(int celda) {
		int ladoDer = 2 * (_ancho - 1);
		for (int i = 0; i < _alto; i++) {
			if (celda == ladoDer) {
				return true;
			}
			ladoDer = ladoDer + (_ancho * 2) - 1;
		}
		return false;
	}

	boolean celdaSalienteLateral(int celda1, int celda2) {
		if (esCeldaLadoIzq(celda1)) {
			if (celda2 == celda1 + (_ancho - 1) || celda2 == celda1 - _ancho) {
				return true;
			}
		}
		if (esCeldaLadoDer(celda1)) {
			if (celda2 == celda1 + _ancho || celda2 == celda1 - (_ancho - 1)) {
				return true;
			}
		}
		return false;
	}

	public short getAlto() {
		return _alto;
	}

	public String getCodigo() {
		return _key;
	}

	public String getLugaresString() {
		return _posicionesDePelea;
	}

	public void addJugador(Personaje perso, int celda) {
		if (!_personajes.containsKey(perso)) {
			_personajes.put(perso, celda);
		} else {
			_personajes.replace(perso, celda);
		}
	}

	void addJugador(Personaje perso, boolean muestra) {
		if (!perso.enLinea()) {
			return;
		}
		if (!_personajes.containsKey(perso)) {
			_personajes.put(perso, perso.getCelda()._id);
		} else {
			_personajes.replace(perso, perso.getCelda()._id);
		}
		if (muestra) {
			GestorSalida.ENVIAR_GM_AGREGAR_PJ_A_TODOS(this, perso);
		}
	}

	public String getGMsPackets(Personaje persox) {
		StringBuilder packets = new StringBuilder("");
		Map<Personaje, Integer> personajes = new HashMap<>();
		personajes.putAll(_personajes);
		for (Personaje perso : personajes.keySet()) {
			if (perso == null) {
				continue;
			}
			if (perso.getPelea() == null) {
				/*
				 * if (persox == perso) { for (Entry<Integer, String> bots :
				 * MundoDofus.botsServer.entrySet()) { if (bots.getKey() == this.getID()) {
				 * GestorSalida.enviar(perso, bots.getValue()); } } }
				 */
				packets.append("GM|+" + perso.stringGM() + '\u0000');
			}
		}
		return packets.toString();
	}

	public String getGMsLuchadores() {
		StringBuilder packets = new StringBuilder("");
		Map<Luchador, Short> luchadores = new HashMap<>();
		luchadores.putAll(_luchadores);
		for (Luchador luchador : luchadores.keySet()) {
			if (luchador == null) {
				continue;
			}
			packets.append("GM|+" + luchador.stringGM() + '\u0000');
		}
		return packets.toString();
	}

	public String getGMsGrupoMobs(Personaje perso) {
		if (_grupoMobs.isEmpty()) {
			return "";
		}
		StringBuilder packets = new StringBuilder("GM|");
		boolean primero = true;
		for (GrupoMobs grupoMob : _grupoMobs.values()) {
			boolean continua = true;
			switch (perso.getMapa().getID()) {
			case 10340:
				if (perso.accionesPJ.contains(29)) {
					continua = false;
				}
				break;
			}
			if (!continua) {
				continue;
			}
			String GM = grupoMob.enviarGM(perso);
			if (GM.equals("")) {
				continue;
			}
			if (!primero) {
				packets.append("|");
			}
			packets.append(GM);
			primero = false;
		}
		return packets.toString();
	}

	public String getGMsPrismas() {
		if (MundoDofus.TodosPrismas() == null) {
			return "";
		}
		String str = "";
		for (Prisma prisma : MundoDofus.TodosPrismas()) {
			if (prisma.getMapa() == _id) {
				str = prisma.getGMPrisma();
				break;
			}
		}
		return str;
	}

	public String getGMsNPCs(Personaje _perso) {
		if (_npcs.isEmpty()) {
			return "";
		}
		StringBuilder packets = new StringBuilder("GM|");
		boolean primero = true;
		for (NPC npc : _npcs.values()) {
			boolean admite = true;
			switch (_perso.getMapa().getID()) {
			case 10354:
				if (_perso.accionesPJ.contains(24)) {
					if (npc.getModeloBD().getID() != 206 && npc.getModeloBD().getID() != 220) {
						admite = false;
					}
				} else {
					if (npc.getModeloBD().getID() != 204 && npc.getModeloBD().getID() != 205
							&& npc.getModeloBD().getID() != 206) {
						admite = false;
					}
				}
				break;
			case 10358:
				if (_perso.accionesPJ.contains(26)) {
					if (npc.getModeloBD().getID() == 198) {
						admite = false;
					}
				} else {
					if (npc.getModeloBD().getID() != 198) {
						admite = false;
					}
				}
				break;
			case 10308:
				if (_perso.accionesPJ.contains(30)) {
					if (npc.getModeloBD().getID() == 213) {
						admite = false;
					}
				}
				break;
			case 10287:
				if (_perso.accionesPJ.contains(31)) {
					if (npc.getModeloBD().getID() == 215) {
						admite = false;
					}
				}
				break;
			}
			if (!admite) {
				continue;
			}
			String GM = npc.analizarGM(_perso);
			if (GM.equals("")) {
				continue;
			}
			if (!primero) {
				packets.append("|");
			}
			packets.append(GM);
			primero = false;
		}
		return packets.toString();
	}

	public String getGMsMercantes() {
		if (_mercante.isEmpty() || _mercante.size() == 0) {
			return "";
		}
		StringBuilder packets = new StringBuilder("GM|+");
		boolean primero = true;
		for (Integer idperso : _mercante) {
			try {
				String GM = MundoDofus.getPersonaje(idperso).stringGMmercante();
				if (GM.equals("")) {
					continue;
				}
				if (!primero) {
					packets.append("|+");
				}
				packets.append(GM);
				primero = false;
			} catch (Exception e) {
				continue;
			}
		}
		return packets.toString();
	}

	public String getGMsMonturas() {
		if (_cercado == null || _cercado.getListaCriando().size() == 0) {
			return "";
		}
		StringBuilder packets = new StringBuilder("GM|+");
		boolean primero = true;
		for (Integer idmontura : _cercado.getListaCriando()) {
			Dragopavo drag = MundoDofus.getDragopavoPorID(idmontura);
			if (drag == null) {
				_cercado.getListaCriando().remove(idmontura);
				continue;
			}
			String GM = drag.getCriarMontura(_cercado);
			if (GM.equals("")) {
				continue;
			}
			if (!primero) {
				packets.append("|+");
			}
			packets.append(GM);
			primero = false;
		}
		return packets.toString();
	}

	public String getObjetosCria() {
		if (_cercado == null || _cercado.getObjetosColocados().size() == 0) {
			return "";
		}
		StringBuilder packets = new StringBuilder("GDO+");
		boolean primero = true;
		for (Entry<Integer, Map<Integer, Integer>> entry : _cercado.getObjetosColocados().entrySet()) {
			for (Entry<Integer, Integer> entry2 : entry.getValue().entrySet()) {
				if (!primero) {
					packets.append("|");
				}
				packets.append(entry.getKey() + ";" + entry2.getKey() + ";1;1000;1000");
				primero = false;
			}
		}
		return packets.toString();
	}

	public String getObjectosGDF(Personaje _perso) {
		StringBuilder packets = new StringBuilder("GDF|");
		boolean primero = true;
		for (Celda celda : _celdas.values()) {
			if (celda.getObjetoInterac() != null) {
				if (!primero) {
					packets.append("|");
				}
				int celdaID = celda.getID();
				ObjetoInteractivo object = celda.getObjetoInterac();
				if (object != null) {
					packets.append(celdaID + ";" + object.getEstado() + ";" + (object.esInteractivo() ? "1" : "0") + ";"
							+ object.getEstado());
				}
				primero = false;
			}
		}
		return packets.toString();
	}

	public int getNumeroPeleas() {
		return _peleas.size();
	}

	public Map<Integer, Pelea> getPeleas() {
		return _peleas;
	}

	void quitarPelea(int id) {
		if (_peleas.containsKey(id)) {
			_peleas.remove(id);
		}
	}

	public Pelea nuevaPelea(Personaje init1, Personaje init2, int tipo, boolean eskoli, boolean esTorneo) {
		int id = 1;
		if (!_peleas.isEmpty()) {
			id = ((Integer) (_peleas.keySet().toArray()[_peleas.size() - 1])) + 1;
		}
		if (!eskoli) {
			if (init1.getKoliseo() != null) {
				init1.getKoliseo().abandonar(init1, true);
			}
			if (init2.getKoliseo() != null) {
				init2.getKoliseo().abandonar(init2, true);
			}
		}
		if (!esTorneo) {
			if (init1.enTorneo == 1) {
				Estaticos.perderTorneo(init1);
			}
			if (init2.enTorneo == 1) {
				Estaticos.perderTorneo(init2);
			}
		}
		Pelea f = new Pelea(tipo, id, this, init1, init2);
		_peleas.put(id, f);
		GestorSalida.ENVIAR_fC_CANTIDAD_DE_PELEAS(this);
		return f;
	}

	public int getRandomCeldaIDLibre() {
		ArrayList<Short> celdaLibre = new ArrayList<>();
		for (Entry<Integer, Celda> entry : _celdas.entrySet()) {
			Celda celda = entry.getValue();
			if (!celda.esCaminable(true)) {
				continue;
			}
			boolean ok = true;
			for (Entry<Integer, GrupoMobs> mgEntry : _grupoMobs.entrySet()) {
				if (mgEntry.getValue().getCeldaID() == entry.getValue().getID()) {
					ok = false;
				}
			}
			if (!ok) {
				continue;
			}
			ok = true;
			for (Entry<Integer, NPC> npcEntry : _npcs.entrySet()) {
				if (npcEntry.getValue().getCeldaID() == celda.getID()) {
					ok = false;
				}
			}
			if (!ok || getPersos(celda.getID())) {
				continue;
			}
			celdaLibre.add(celda.getID());
		}
		if (celdaLibre.isEmpty()) {
			System.out.println("Alguna celda libre no esta ubicada en el mapa " + _id + " : grupo no spawn");
			return -1;
		}
		if (celdaLibre.size() <= 0) {
			return 0;
		}
		int rand = Formulas.getRandomValor(0, celdaLibre.size() - 1);
		return celdaLibre.get(rand);
	}

	public boolean getPersos(int celda) {
		for (Integer celdas : _personajes.values()) {
			if (celdas == celda) {
				return true;
			}
		}
		return false;
	}

	public ArrayList<Short> getCeldasIDLibre2() {
		ArrayList<Short> celdaLibre = new ArrayList<>();
		int celdarandom = getRandomCeldaIDLibre();
		if (getPersos().size() > 0 || celdarandom > 0) {
			short celd = 0;
			if ((getPersos().size() > 0)) {
				for (Personaje gr : getPersos()) {
					if (gr != null) {
						celd = gr.getCelda().getID();
						break;
					}
				}
			} else {
				celd = (short) celdarandom;
			}
			if (celd != 0) {
				celdaLibre = contieneCelda2(celd, celdaLibre);
				int cantc = 0;
				for (Short element : celdaLibre) {
					if (cantc >= 20) {
						break;
					}
					if (element == 0) {
						continue;
					}
					celdaLibre = contieneCelda2(element, celdaLibre);
					cantc += 1;
				}
				for (Short element : celdaLibre) {
					if (cantc >= 40) {
						break;
					}
					if (element == 0) {
						continue;
					}
					celdaLibre = contieneCelda2(element, celdaLibre);
					cantc += 1;
				}
			}
		}
		if (celdaLibre.size() <= 0) {
			return null;
		}
		return celdaLibre;
	}

	public ArrayList<Short> getCeldasIDLibre() {
		ArrayList<Short> celdaLibre = new ArrayList<>();
		if (getPersos().size() > 0) {
			short celd = 0;
			for (Personaje gr : getPersos()) {
				if (gr != null) {
					celd = gr.getCelda().getID();
					break;
				}
			}
			if (celd != 0) {
				celdaLibre = contieneCelda(celd, celdaLibre);
				int cantc = 0;
				for (Short element : celdaLibre) {
					if (cantc >= 20) {
						break;
					}
					if (element == 0) {
						continue;
					}
					celdaLibre = contieneCelda(element, celdaLibre);
					cantc += 1;
				}
				for (Short element : celdaLibre) {
					if (cantc >= 40) {
						break;
					}
					if (element == 0) {
						continue;
					}
					celdaLibre = contieneCelda(element, celdaLibre);
					cantc += 1;
				}
			}
		}
		if (celdaLibre.isEmpty()) {
			if (getMobGroups().size() > 0) {
				int celd = 0;
				for (GrupoMobs gr : _grupoMobs.values()) {
					if (gr != null) {
						celd = gr.getCeldaID();
						break;
					}
				}
				if (celd != 0) {
					celdaLibre = contieneCelda((short) celd, celdaLibre);
					for (Short element : celdaLibre) {
						if (element == 0) {
							continue;
						}
						celdaLibre = contieneCelda(element, celdaLibre);
					}
					if (celdaLibre.size() <= 10) {
						for (Short element : celdaLibre) {
							if (element == 0) {
								continue;
							}
							celdaLibre = contieneCelda(element, celdaLibre);
						}
					}
				}
			}
			if (celdaLibre.isEmpty()) {
				System.out.println("Alguna celda libre no esta ubicada en el mapa " + _id + " : grupo no spawn");
				return null;
			}
		}
		if (celdaLibre.size() <= 0) {
			return null;
		}
		return celdaLibre;
	}

	private ArrayList<Short> contieneCelda(short estacelda, ArrayList<Short> misCeldas) {
		ArrayList<Short> celdaLibre = misCeldas;
		char[] dirs = { 'b', 'd', 'f', 'h' };
		int direccion = 0;
		for (char d : dirs) {
			short sigCelda = (short) Camino.getSigIDCeldaMismaDir(estacelda, d, this, true);
			Celda C = this.getCelda(sigCelda);
			if (C == null) {
				break;
			}
			if (C.esCaminable(true)) {
				direccion += 1;
				if (direccion >= 2) {
					if (!celdaLibre.contains(sigCelda)) {
						celdaLibre.add(sigCelda);
					}
				}
			}
		}
		return celdaLibre;
	}

	private ArrayList<Short> contieneCelda2(short estacelda, ArrayList<Short> misCeldas) {
		ArrayList<Short> celdaLibre = misCeldas;
		char[] dirs = { 'b', 'd', 'f', 'h' };
		int direccion = 0;
		for (char d : dirs) {
			short sigCelda = (short) Camino.getSigIDCeldaMismaDir(estacelda, d, this, true);
			if (sigCelda > 387 || sigCelda < 131) {
				continue;
			}
			Celda C = this.getCelda(sigCelda);
			if (C == null) {
				continue;
			}
			if (C.esCaminable(true)) {
				direccion += 1;
				if (direccion >= 3) {
					if (!celdaLibre.contains(sigCelda)) {
						celdaLibre.add(sigCelda);
					}
				}
			}
		}
		return celdaLibre;
	}

	void refrescarGrupoMobs(Personaje perso) {
		if (Invasion.mapainv != null) {
			if (this == Invasion.mapainv) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE("No puedes utilizarlo en un mapa que ha sido invadido.",
						Colores.ROJO);
				return;
			}
		}
		for (int id : _grupoMobs.keySet()) {
			GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(this, id);
		}
		_grupoMobs.clear();
		_grupoMobs.putAll(_grupoMobsFix);
		for (GrupoMobs mg : _grupoMobsFix.values()) {
			GestorSalida.ENVIAR_GM_GRUPOMOB_A_MAPA(this, mg);
		}
		spawnGrupo(-1, _maxGrupoDeMobs, true, -1);
		spawnGrupo(1, 1, true, -1);
		spawnGrupo(2, 1, true, -1);
	}

	public void subirEstrellasMobs() {
		for (GrupoMobs mg : _grupoMobs.values()) {
			if (mg != null) {
				mg.aumentarEstrellas();
			}
		}
	}

	public void moveMobsRandomly(boolean all) {
		if (getMobGroups().size() == 0) {
			return;
		}
		int rndGroup = Formulas.getRandomValor(1, getMobGroups().size());
		int i = 0;
		try {
			for (Entry<Integer, GrupoMobs> entry : getMobGroups().entrySet()) {
				i++;
				if ((!all && i != rndGroup) || entry.getValue().esFixeado()) {
					continue;
				}
				ArrayList<Integer> celdas = Camino.getCeldas(this, entry.getValue().getCeldaID(), 4, null, null, null);
				int cell = -1;
				try {
					int randomIndex = (int) (Math.random() * celdas.size());
					cell = celdas.get(randomIndex);
				} catch (Exception e) {
					cell = -1;
				}
				if (cell <= 0) {
					cell = getRandomCeldaIDLibre();
				}
				String pathstr = Camino.getShortestStringPathBetween(this, entry.getValue().getCeldaID(), cell, 0);
				if (pathstr == null) {
					return;
				}
				entry.getValue().setCeldaID(cell);
				String packet = "GA0;1;" + entry.getValue().getID() + ";" + pathstr;
				for (Personaje z : getPersos()) {
					GestorSalida.enviar(z, packet);
				}
			}
		} catch (Exception e) {
			System.out.println("Hubo un error al mover los mobs " + e.getMessage());
		}
	}

	public void jugadorLLegaACelda(Personaje perso, int celdaID) {
		if (_celdas.get(celdaID) == null) {
			return;
		}
		Objeto obj = _celdas.get(celdaID).getObjetoTirado();
		if (obj != null) {
			if (!perso.addObjetoSimilar(obj, true, -1)) {
				perso.addObjetoPut(obj);
				GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(perso, obj);
			} else {
				MundoDofus.eliminarObjeto(obj.getID());
			}
			GestorSalida.ENVIAR_GDO_OBJETO_TIRADO_AL_SUELO(this, '-', celdaID, 0, 0);
			GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(perso);
			_celdas.get(celdaID).borrarObjetoTirado();
		}
		if (!perso.esMaitre) {
			_celdas.get(celdaID).aplicarAccionCeldaPosicionada(perso);
		}
		if (perso.getMapa().getID() != _id || perso.esFantasma() || perso.esTumba() || ((System.currentTimeMillis() - perso.ultimaPelea) < 1000)) {
			return;
		}
		for (GrupoMobs grupo : _grupoMobs.values()) {
			if (Camino.distanciaEntreDosCeldas(this, (short) celdaID, (short) grupo.getCeldaID()) <= grupo
					.getDistanciaAgresion()) {
				if ((grupo.getAlineamiento() == -1 || ((perso.getAlineacion() == 1 || perso.getAlineacion() == 2)
						&& (perso.getAlineacion() != grupo.getAlineamiento())))
						&& CondicionJugador.validaCondiciones(perso, grupo.getCondicion())) {
					if (perso.esMaitre) {
						return;
					}
					if (perso.enTorneo != 0) {
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "No puedes iniciar una pelea estando en un Torneo",
								Colores.ROJO);
						return;
					}
					if (this.getID() == 27105) {
						if (MundoDofus.mapaPortal != null) {
							if (!MundoDofus.empiezaPortal) {
								GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "El evento no ha iniciado todav�a",
										Colores.ROJO);
								return;
							}
						}
					}
					if (getID() == 7796 && !MundoDofus.eventoJalato) {
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "No hay ning�n evento todav�a", Colores.ROJO);
						return;
					}
					if (!perso.esMaitre) {
						perso._celda = perso.celdaanteriorx;
						iniciarPeleaVSMobs(perso, grupo);
					}
					if (perso.liderMaitre) {
						Timer timer = new Timer(500, new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								((Timer) e.getSource()).stop();
								for (Personaje pjx : perso.getGrupo().getPersos()) {
									if (pjx == perso) {
										continue;
									}
									if (!pjx.enLinea()) {
										continue;
									}
									if (pjx.getPelea() != null) {
										continue;
									}
									if (perso.getPelea() == null) {
										continue;
									}
									String packet = "GA903" + perso.getPelea().getID() + ";" + perso.getID();
									pjx.getCuenta().getEntradaPersonaje().analizar_Packets(packet, false);
								}
							}
						});
						timer.start();
					}
					return;
				}
			}
		}
	}

	void iniciarPeleaVSMobs(Personaje perso, GrupoMobs grupoMob) {
		int id = 1;
		perso.panelGE = "";
		if (!_peleas.isEmpty()) {
			id = ((Integer) (_peleas.keySet().toArray()[_peleas.size() - 1])) + 1;
		}
		if (!grupoMob.esFixeado()) {
			_grupoMobs.remove(grupoMob.getID());
		}
		if (perso.getKoliseo() != null) {
			perso.getKoliseo().abandonar(perso, true);
		}
		if (perso.enTorneo == 1) {
			Estaticos.perderTorneo(perso);
		}
		_peleas.put(id, new Pelea(id, this, perso, grupoMob, 4));
		GestorSalida.ENVIAR_fC_CANTIDAD_DE_PELEAS(this);
	}

	void iniciarPeleaVSDopeul(Personaje perso, GrupoMobs grupoMob) {
		int id = 1;
		if (!_peleas.isEmpty()) {
			id = ((Integer) (_peleas.keySet().toArray()[_peleas.size() - 1])) + 1;
		}
		if (perso.getKoliseo() != null) {
			perso.getKoliseo().abandonar(perso, true);
		}
		if (perso.enTorneo == 1) {
			Estaticos.perderTorneo(perso);
		}
		_peleas.put(id, new Pelea(id, this, perso, grupoMob, 3));
		GestorSalida.ENVIAR_fC_CANTIDAD_DE_PELEAS(this);
	}

	public void iniciarPeleaVSRecaudador(Personaje perso, Recaudador recaudador) {
		int id = 1;
		if (!_peleas.isEmpty()) {
			id = ((Integer) (_peleas.keySet().toArray()[_peleas.size() - 1])) + 1;
		}
		if (perso.getKoliseo() != null) {
			perso.getKoliseo().abandonar(perso, true);
		}
		if (perso.enTorneo == 1) {
			Estaticos.perderTorneo(perso);
		}
		_peleas.put(id, new Pelea(id, this, perso, recaudador));
		GestorSalida.ENVIAR_fC_CANTIDAD_DE_PELEAS(this);
	}

	public void iniciarPeleaVSPrisma(Personaje perso, Prisma prisma) {
		int id = 1;
		if (!_peleas.isEmpty()) {
			id = ((Integer) (_peleas.keySet().toArray()[_peleas.size() - 1])) + 1;
		}
		if (perso.getKoliseo() != null) {
			perso.getKoliseo().abandonar(perso, true);
		}
		if (perso.enTorneo == 1) {
			Estaticos.perderTorneo(perso);
		}
		_peleas.put(id, new Pelea(id, this, perso, prisma));

		GestorSalida.ENVIAR_fC_CANTIDAD_DE_PELEAS(this);
	}

	public String getPosicionesAleatorias() {
		StringBuilder posiciones = new StringBuilder();
		ArrayList<Short> celdax = getCeldasIDLibre();
		ArrayList<Short> equipo1 = new ArrayList<>();
		ArrayList<Short> equipo2 = new ArrayList<>();
		if (celdax.isEmpty()) {
			return "";
		}
		int sizex = celdax.size(); // 13
		boolean esimp = false;
		if (sizex % 2 == 0) {
			esimp = true;
			sizex -= 1;// 12
		}
		int div = sizex / 2; // 6

		int restante = (sizex) - div;// 12-6=6
		if (esimp) {
			restante = (sizex + 1) - div;// 13-6=7
		}
		for (int i = 0; i < div; i++) {
			int rand = Formulas.getRandomValor(0, celdax.size() - 1);
			equipo1.add(celdax.get(rand));
			celdax.remove(rand);
		}
		for (int i = 0; i < restante; i++) {
			int rand = Formulas.getRandomValor(0, celdax.size() - 1);
			equipo2.add(celdax.get(rand));
			celdax.remove(rand);
		}
		for (int celda : equipo1) {
			posiciones.append(Encriptador.celdaIDACodigo(celda));
		}
		posiciones.append("|");
		for (int celda : equipo2) {
			posiciones.append(Encriptador.celdaIDACodigo(celda));
		}
		return posiciones.toString();
	}

	private String getPosicionesAleatorias3() {
		StringBuilder posiciones = new StringBuilder();
		ArrayList<Short> celdax = getPosicionesAleatorias2();
		ArrayList<Short> equipo1 = new ArrayList<>();
		ArrayList<Short> equipo2 = new ArrayList<>();
		if (celdax.isEmpty()) {
			return "";
		}
		int sizex = celdax.size(); // 13
		boolean esimp = false;
		if (sizex % 2 == 0) {
			esimp = true;
			sizex -= 1;// 12
		}
		int div = sizex / 2; // 6

		int restante = (sizex) - div;// 12-6=6
		if (esimp) {
			restante = (sizex + 1) - div;// 13-6=7
		}
		for (int i = 0; i < div; i++) {
			int rand = Formulas.getRandomValor(0, celdax.size() - 1);
			equipo1.add(celdax.get(rand));
			celdax.remove(rand);
		}
		for (int i = 0; i < restante; i++) {
			int rand = Formulas.getRandomValor(0, celdax.size() - 1);
			equipo2.add(celdax.get(rand));
			celdax.remove(rand);
		}
		for (int celda : equipo1) {
			posiciones.append(Encriptador.celdaIDACodigo(celda));
		}
		posiciones.append("|");
		for (int celda : equipo2) {
			posiciones.append(Encriptador.celdaIDACodigo(celda));
		}
		return posiciones.toString();
	}

	ArrayList<Short> getPosicionesAleatorias2() {
		ArrayList<Short> celdax = getCeldasIDLibre2();
		if (celdax == null) {
			return null;
		}
		return celdax;
	}

	boolean actualizaactual = false;

	Mapa copiarMapa() {
		Map<Integer, Celda> casillas = new TreeMap<>();
		String posiciones = _posicionesDePelea;
		if (posiciones.equals("") || posiciones.equals("|")) {
			posiciones = getPosicionesAleatorias();
			if (posiciones.equals("") || posiciones.equals("|")) {
				posiciones = getPosicionesAleatorias3();
			}
			actualizaactual = true;
			_posicionesDePelea = posiciones;
		} else {
			String lugares = getLugaresString();
			String equipo0 = "", equipo1 = "";
			String[] p = lugares.split("\\|");
			try {
				equipo0 = p[0];
			} catch (Exception e) {
			}
			try {
				equipo1 = p[1];
			} catch (Exception e) {
			}
			if (equipo0.length() <= 0 || equipo1.length() <= 0) {
				posiciones = getPosicionesAleatorias();
			} else {
				posiciones = compruebaCelda(_posicionesDePelea);
			}
		}
		Mapa mapa = new Mapa(_id, _fecha, _ancho, _alto, _key, posiciones);
		for (Entry<Integer, Celda> entry : _celdas.entrySet()) {
			Celda celda = entry.getValue();
			casillas.put(entry.getKey(),
					new Celda(mapa, celda.getID(), celda.esCaminable(false), celda.esLineaDeVista(),
							(celda.getObjetoInterac() == null ? -1 : celda.getObjetoInterac().getID())));
		}
		mapa.setCeldas(casillas);
		return mapa;
	}

	private String compruebaCelda(String clds) {
		ArrayList<Short> celdaLibresEq1 = new ArrayList<>();
		ArrayList<Short> celdaLibresEq2 = new ArrayList<>();
		String lugares = clds;
		String equipo0 = "", equipo1 = "";
		String[] p = lugares.split("\\|");
		try {
			equipo0 = p[0];
		} catch (Exception e) {
		}
		try {
			equipo1 = p[1];
		} catch (Exception e) {
		}
		for (int a = 0; a <= equipo0.length() - 2; a += 2) {
			String codigo = equipo0.substring(a, a + 2);
			short celdaid = Encriptador.celdaCodigoAID(codigo);
			Celda c = this.getCelda(celdaid);
			if (c == null) {
				continue;
			}
			if (c.esCaminable(true)) {
				if (!celdaLibresEq1.contains(celdaid)) {
					celdaLibresEq1.add(celdaid);
				}
			}
		}
		for (int a = 0; a <= equipo1.length() - 2; a += 2) {
			String codigo = equipo1.substring(a, a + 2);
			short celdaid = Encriptador.celdaCodigoAID(codigo);
			Celda c = this.getCelda(celdaid);
			if (c == null) {
				continue;
			}
			if (c.esCaminable(true)) {
				if (!celdaLibresEq2.contains(celdaid)) {
					celdaLibresEq2.add(celdaid);
				}
			}
		}
		ArrayList<Short> celdaR1 = new ArrayList<>();
		ArrayList<Short> celdaR2 = new ArrayList<>();
		if (celdaLibresEq1.size() < 8) {
			actualizaactual = true;
			if (celdaLibresEq1.size() > 0) {
				celdaLibresEq1 = getCeldaCercana(celdaLibresEq1);
			} else {
				celdaR1.add((short) getRandomCeldaIDLibre());
				celdaLibresEq1 = getCeldaCercana(celdaR1);
			}
		}
		if (celdaLibresEq2.size() < 8) {
			actualizaactual = true;
			if (celdaLibresEq2.size() > 0) {
				celdaLibresEq2 = getCeldaCercana(celdaLibresEq2);
			} else {
				celdaR2.add((short) getRandomCeldaIDLibre());
				celdaLibresEq2 = getCeldaCercana(celdaR2);
			}
		}
		StringBuilder posiciones = new StringBuilder();
		for (int celda : celdaLibresEq1) {
			posiciones.append(Encriptador.celdaIDACodigo(celda));
		}
		posiciones.append("|");
		for (int celda : celdaLibresEq2) {
			posiciones.append(Encriptador.celdaIDACodigo(celda));
		}
		return posiciones.toString();
	}

	private ArrayList<Short> getCeldaCercana(ArrayList<Short> celdaLibres2) {
		char[] dirs = { 'b', 'd', 'f', 'h' };
		ArrayList<Short> celdaLibresx = celdaLibres2;
		ArrayList<Short> celdaLibres = new ArrayList<>();
		celdaLibres = celdaLibres2;
		int celdasenc = 0;
		for (short counter = 0; counter < celdaLibresx.size(); counter++) {
			if (celdaLibresx.get(counter) == 0) {
				continue;
			}
			if (celdasenc >= 7) {
				break;
			}
			for (char d : dirs) {
				short sigCelda = (short) Camino.getSigIDCeldaMismaDir(celdaLibresx.get(counter), d, this, true);
				Celda C = this.getCelda(sigCelda);
				if (C == null) {
					break;
				}
				if (C.esCaminable(true)) {
					if (!celdaLibres.contains(sigCelda)) {
						celdaLibres.add(sigCelda);
						celdasenc += 1;
					}
				}
			}
		}
		return celdaLibres;
	}

	public void setCeldas(Map<Integer, Celda> casillas) {
		_celdas = casillas;
	}

	public ObjetoInteractivo getPuertaCercado() {
		for (Celda c : _celdas.values()) {
			ObjetoInteractivo objInt = c.getObjetoInterac();
			if (objInt == null) {
				continue;
			}
			int idObjInt = objInt.getID();
			if (idObjInt == 6763 || idObjInt == 6766 || idObjInt == 6767 || idObjInt == 6772) {
				return objInt;
			}
		}
		return null;
	}

	public Map<Integer, GrupoMobs> getMobGroups() {
		return _grupoMobs;
	}

	public void borrarNPCoGrupoMob(int id) {
		_npcs.remove(id);
		_grupoMobs.remove(id);
	}

	public void borrarTodosMobs() {
		_mobPosibles.clear();
		for (int id : _grupoMobs.keySet()) {
			GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(this, id);
		}
		_grupoMobs.clear();
	}

	public void borrarTodosMobsFix() {
		_grupoMobsFix.clear();
		for (int id : _grupoMobsFix.keySet()) {
			GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(this, id);
		}
		GestorSQL.BORRAR_MOBSFIX_MAPA(_id);
		_grupoMobs.clear();
	}

	public int getMaxGrupoDeMobs() {
		return _maxGrupoDeMobs;
	}

	public void setMaxGrupoDeMobs(byte id) {
		_maxGrupoDeMobs = id;
	}

	public Pelea getPelea(int id) {
		return _peleas.get(id);
	}

	public void objetosTirados(Personaje perso) {
		for (Celda c : _celdas.values()) {
			if (c.getObjetoTirado() != null) {
				GestorSalida.ENVIAR_GDO_OBJETO_TIRADO_AL_SUELO(perso, '+', c.getID(),
						c.getObjetoTirado().getModelo().getID(), 0);
			}
		}
	}

	public Map<Integer, Celda> getCeldas() {
		return _celdas;
	}

	public static class Cercado {
		private int _propietario;
		private ObjetoInteractivo _antespuerta;
		private int _tamano;
		private Gremio _gremio;
		private Mapa _mapa;
		private int _celda = -1;
		private int _precio;
		private int _colocarcelda;
		private CopyOnWriteArrayList<Integer> _criando = new CopyOnWriteArrayList<>();
		private int _puerta;
		private ArrayList<Integer> _celdasobjeto = new ArrayList<>();
		private int _cantObjetosMax;
		private Map<Integer, Map<Integer, Integer>> _objetoscrianza = new TreeMap<>();
		private Map<Integer, Integer> _CeldayObjeto = new TreeMap<>();

		// private Timer _moverDragopavo;
		public Cercado(int propietario, Mapa mapa, int cellid, int tamano, int guild, int precio, int colocarcelda,
				String criando, int puerta, String celdasobjeto, int cantobjetos, String objetoscrianza) {
			_propietario = propietario;
			_antespuerta = mapa.getPuertaCercado();
			_tamano = tamano;
			_gremio = MundoDofus.getGremio(guild);
			_mapa = mapa;
			_celda = cellid;
			_precio = precio;
			_colocarcelda = colocarcelda;
			_puerta = puerta;
			_cantObjetosMax = cantobjetos;
			if (!objetoscrianza.isEmpty()) {
				for (String objetos : objetoscrianza.split("\\|")) {
					String[] infos = objetos.split(";");
					int celda = Integer.parseInt(infos[0]);
					int objeto = Integer.parseInt(infos[1]);
					int dueÑo = Integer.parseInt(infos[2]);
					Map<Integer, Integer> otro = new TreeMap<>();
					otro.put(objeto, dueÑo);
					_CeldayObjeto.put(celda, objeto);
					_objetoscrianza.put(celda, otro);
				}
			}
			if (!celdasobjeto.isEmpty()) {
				for (String celda : celdasobjeto.split(";")) {
					int Celda = Integer.parseInt(celda);
					if (Celda <= 0) {
						continue;
					}
					_celdasobjeto.add(Celda);
				}
			}
			if (!criando.isEmpty()) {
				String[] dragopavos = criando.split(";");
				for (String pavo : dragopavos) {
					_criando.add(Integer.parseInt(pavo));
				}
			}
			if (_mapa != null) {
				_mapa.setCercado(this);
			}
		}

		public void resetear() {
			_propietario = 0;
			_gremio = null;
			_CeldayObjeto.clear();
			_objetoscrianza.clear();
			_criando.clear();
			_precio = (int) 3000000L;
		}

		public synchronized void startMoverDrago() {
			if (_criando.size() > 0) {
				char[] direcciones = { 'b', 'd', 'f', 'h' };
				for (Integer montura : _criando) {
					Dragopavo dragopavo = MundoDofus.getDragopavoPorID(montura);
					if (dragopavo != null) {
						char dir = direcciones[Formulas.getRandomValor(0, 3)];
						dragopavo.moverMonturaAuto(dir, 3, false);
						try {
							Thread.sleep(300);
						} catch (InterruptedException e) {
						}
					}
				}
			}
		}

		public Map<Integer, Integer> getCeldayObjeto() {
			return _CeldayObjeto;
		}

		public void setSizeyObjetos(int size, int objetos) {
			_tamano = size;
			_cantObjetosMax = objetos;
		}

		void addObjetoCria(int celda, int objeto, int dueÑo) {
			if (_objetoscrianza.containsKey(celda)) {
				_objetoscrianza.remove(celda);
				_CeldayObjeto.remove(celda);
			}
			Map<Integer, Integer> otro = new TreeMap<>();
			otro.put(objeto, dueÑo);
			_CeldayObjeto.put(celda, objeto);
			_objetoscrianza.put(celda, otro);
		}

		public boolean delObjetoCria(int celda) {
			if (!_objetoscrianza.containsKey(celda)) {
				return false;
			}
			_objetoscrianza.remove(celda);
			_CeldayObjeto.remove(celda);
			return true;
		}

		public String getStringObjetosCria() {
			StringBuilder str = new StringBuilder("");
			boolean primero = false;
			if (_objetoscrianza.size() == 0) {
				return str.toString();
			}
			for (Entry<Integer, Map<Integer, Integer>> entry : _objetoscrianza.entrySet()) {
				if (primero) {
					str.append("|");
				}
				str.append(entry.getKey());
				for (Entry<Integer, Integer> entry2 : entry.getValue().entrySet()) {
					str.append(";" + entry2.getKey() + ";" + entry2.getValue());
				}
				primero = true;
			}
			return str.toString();
		}

		public int getCantObjColocados() {
			return _objetoscrianza.size();
		}

		public Map<Integer, Map<Integer, Integer>> getObjetosColocados() {
			return _objetoscrianza;
		}

		public void addCeldaObj(int celda) {
			if (_celdasobjeto.contains(celda) || (celda <= 0)) {
				return;
			}
			_celdasobjeto.add(celda);
		}

		public void addCeldaMontura(int celda) {
			_colocarcelda = celda;
		}

		public int getCantObjMax() {
			return _cantObjetosMax;
		}

		public String getStringCeldasObj() {
			boolean primero = false;
			StringBuilder str = new StringBuilder("");
			if (_celdasobjeto.size() == 0) {
				return str.toString();
			}
			for (Integer celda : _celdasobjeto) {
				if (primero) {
					str.append(";");
				}
				str.append(celda);
				primero = true;
			}
			return str.toString();
		}

		public ArrayList<Integer> getCeldasObj() {
			return _celdasobjeto;
		}

		public int getColocarCelda() {
			return _colocarcelda;
		}

		public int getPuerta() {
			return _puerta;
		}

		public void setPuerta(int puerta) {
			_puerta = puerta;
		}

		public String getCriando() {
			StringBuilder str = new StringBuilder("");
			boolean primero = true;
			if (_criando.size() == 0) {
				return "";
			}
			for (Integer pavo : _criando) {
				if (!primero) {
					str.append(";");
				}
				str.append(pavo);
				primero = false;
			}
			return str.toString();
		}

		public void addCriando(int pavo) {
			_criando.add(pavo);
		}

		public void delCriando(int pavo) {
			if (_criando.contains(pavo)) {
				int index = _criando.indexOf(pavo);
				_criando.remove(index);
			}
		}

		public int cantCriando() {
			return _criando.size();
		}

		public CopyOnWriteArrayList<Integer> getListaCriando() {
			return _criando;
		}

		public int getDueÑo() {
			return _propietario;
		}

		public void setPropietario(int AccID) {
			_propietario = AccID;
		}

		public ObjetoInteractivo get_door() {
			return _antespuerta;
		}

		public int getTamaÑo() {
			return _tamano;
		}

		public Gremio getGremio() {
			return _gremio;
		}

		public void setGremio(Gremio guild) {
			_gremio = guild;
		}

		public Mapa getMapa() {
			return _mapa;
		}

		public int getCeldaID() {
			return _celda;
		}

		public int getPrecio() {
			return _precio;
		}

		public void setPrecio(int price) {
			_precio = price;
		}
	}

	public static class ObjetoInteractivo {
		private int _id;
		private int _estado;
		private Mapa _mapa;
		private Celda _celda;
		private boolean _interactivo = true;
		private Timer _tiempoRefrescar;
		private ObjInteractivoModelo _interacMod;
		private short _estrellas = -1;
		private long _proxSubidaEstrella = -1;

		private ObjetoInteractivo(Mapa mapa, Celda celda, int id) {
			_id = id;
			_mapa = mapa;
			_celda = celda;
			_estado = CentroInfo.IO_ESTADO_LLENO;
			int tiempoRespuesta = 10000;
			_estrellas = Emu.INICIO_BONUS_ESTRELLAS_RECURSOS;
			_interacMod = MundoDofus.getObjInteractivoModelo(_id);
			if (_interacMod != null) {
				tiempoRespuesta = _interacMod.getTiempoRespuesta();
			}
			_tiempoRefrescar = new Timer(tiempoRespuesta, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					_tiempoRefrescar.stop();
					_estado = CentroInfo.IO_ESTADO_LLENANDO;
					_interactivo = true;
					GestorSalida.ENVIAR_GDF_ESTADO_OBJETO_INTERACTIVO(_mapa, _celda);
					_estado = CentroInfo.IO_ESTADO_LLENO;
					/*
					 * if (Emu.PARAM_ESTRELLAS_RECURSOS) { restartSubirEstrellas(); }
					 */
				}
			});
		}

		public void subirEstrella() {
			if (!Emu.PARAM_ESTRELLAS_RECURSOS || _proxSubidaEstrella <= 0 || Emu.SEGUNDOS_ESTRELLAS_RECURSOS <= 0) {
				return;
			}
			if (System.currentTimeMillis() - _proxSubidaEstrella >= 0) {
				subirEstrellas(20);
				restartSubirEstrellas();
			}
		}

		private void subirEstrellas(int cant) {
			if (_estrellas < 0) {
				_estrellas = 0;
				cant -= 20;
			} else if (_estrellas < Emu.INICIO_BONUS_ESTRELLAS_RECURSOS) {
				_estrellas = Emu.INICIO_BONUS_ESTRELLAS_RECURSOS;
				cant -= 20;
			}
			if (cant > 0) {
				_estrellas += cant;
				if (Emu.PARAM_REINICIAR_ESTRELLAS_SI_LLEGA_MAX) {
					while (_estrellas > Emu.MAX_BONUS_ESTRELLAS_RECURSOS + 60) {
						_estrellas -= (Emu.MAX_BONUS_ESTRELLAS_RECURSOS + 60);
					}
				} else {
					if (_estrellas > Emu.MAX_BONUS_ESTRELLAS_RECURSOS) {
						_estrellas = Emu.MAX_BONUS_ESTRELLAS_RECURSOS;
					}
				}
			}
		}

		private void restartSubirEstrellas() {
			_proxSubidaEstrella = System.currentTimeMillis() + (Emu.SEGUNDOS_ESTRELLAS_RECURSOS * 1000);
		}

		public short getEstrellas() {
			return _estrellas > 0 ? _estrellas : 0;
		}

		public int getID() {
			return _id;
		}

		public boolean esInteractivo() {
			return _interactivo;
		}

		public void setInteractivo(boolean b) {
			_interactivo = b;
		}

		public int getEstado() {
			return _estado;
		}

		public void setEstado(int estado) {
			_estado = estado;
		}

		public int getDuracion() {
			int duracion = 1500;
			if (_interacMod != null) {
				duracion = _interacMod.getDuracion();
			}
			return duracion;
		}

		public int getAnimacionPJ() {
			int idAnimacion = 4;
			if (_interacMod != null) {
				idAnimacion = _interacMod.getAnimacionPJ();
			}
			return idAnimacion;
		}

		private boolean esCaminable() {
			if (_interacMod == null) {
				return false;
			}
			return _interacMod.esCaminable() && _estado == CentroInfo.IO_ESTADO_LLENO;
		}

		void iniciarTiempoRefresco() {
			if (_tiempoRefrescar == null) {
				return;
			}
			_estado = CentroInfo.IO_ESTADO_VACIANDO;
			_tiempoRefrescar.restart();
		}
	}

	public static class Celda {
		private int _id;
		private boolean _caminable = true;
		private boolean _lineaDeVista = true;
		private int _mapaID;
		public Mapa _mapa;
		public ArrayList<Accion> _celdaAccion;
		private ObjetoInteractivo _objetoInterac;
		private Objeto _objetoTirado;

		public Celda(Mapa mapa, int id, boolean caminable, boolean lineaDeVista, int objID) {
			_mapaID = mapa.getID();
			_mapa = mapa;
			_id = id;
			if (_mapaID == 29997) {
				_caminable = true;
			} else {
				_caminable = caminable;
			}
			_lineaDeVista = lineaDeVista;
			if (objID == -1) {
				return;
			}
			mapa.addCasillaObjInteractivo(this);
			_objetoInterac = new ObjetoInteractivo(mapa, this, objID);
		}

		public ObjetoInteractivo getObjetoInterac() {
			return _objetoInterac;
		}

		public Objeto getObjetoTirado() {
			return _objetoTirado;
		}

		public short getID() {
			return (short) _id;
		}

		public void addAccionEnUnaCelda(int id, String args, String cond) {
			if (_celdaAccion == null) {
				_celdaAccion = new ArrayList<>();
			}
			_celdaAccion.add(new Accion(id, args, cond));
		}

		public void aplicarAccionCeldaPosicionada(Personaje perso) {
			if (_celdaAccion == null) {
				return;
			}
			for (Accion act : _celdaAccion) {
				act.aplicar(perso, null, -1, -1);
			}
		}

		public boolean esCaminable(boolean usaObjeto) {
			if (_objetoInterac != null && usaObjeto) {
				return _caminable && _objetoInterac.esCaminable();
			}
			return _caminable;
		}

		public boolean lineaDeVistaBloqueada() {
			if (!_mapa.luchadoresen(_id)) {
				return _lineaDeVista;
			}
			boolean luchador = true;
			for (Luchador f : _mapa._luchadores.keySet()) {
				if (!f.esInvisible() && f.getCeldaPelea().getID() == _id) {
					luchador = false;
					break;
				}
			}
			return _lineaDeVista && luchador;
		}

		private boolean esLineaDeVista() {
			return _lineaDeVista;
		}

		boolean puedeHacerAccion(int accionID, boolean pescarKuakua) {
			if (_objetoInterac == null) {
				return false;
			}
			switch (accionID) {
			case 122:
			case 47:
				return _objetoInterac.getID() == 7007;
			case 45:
				switch (_objetoInterac.getID()) {
				case 7511:
					return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
				}
				return false;
			case 53:
				switch (_objetoInterac.getID()) {
				case 7515:
					return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
				}
				return false;
			case 57:
				switch (_objetoInterac.getID()) {
				case 7517:
					return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
				}
				return false;
			case 46:
				switch (_objetoInterac.getID()) {
				case 7512:
					return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
				}
				return false;
			case 50:
			case 68:
				switch (_objetoInterac.getID()) {
				case 7513:
					return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
				}
				return false;
			case 159:
				switch (_objetoInterac.getID()) {
				case 7550:
					return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
				}
				return false;
			case 52:
				switch (_objetoInterac.getID()) {
				case 7516:
					return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
				}
				return false;
			case 58:
				switch (_objetoInterac.getID()) {
				case 7518:
					return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
				}
				return false;
			case 69:
			case 54:
				switch (_objetoInterac.getID()) {
				case 7514:
					return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
				}
				return false;
			case 101:
				return _objetoInterac.getID() == 7003;
			case 6:
				switch (_objetoInterac.getID()) {
				case 7500:
					return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
				}
				return false;
			case 39:
				switch (_objetoInterac.getID()) {
				case 7501:
					return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
				}
				return false;
			case 40:
				switch (_objetoInterac.getID()) {
				case 7502:
					return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
				}
				return false;
			case 10:
				switch (_objetoInterac.getID()) {
				case 7503:
					return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
				}
				return false;
			case 141:
				switch (_objetoInterac.getID()) {
				case 7542:
					return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
				}
				return false;
			case 139:
				switch (_objetoInterac.getID()) {
				case 7541:
					return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
				}
				return false;
			case 37:
				switch (_objetoInterac.getID()) {
				case 7504:
					return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
				}
				return false;
			case 154:
				switch (_objetoInterac.getID()) {
				case 7553:
					return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
				}
				return false;
			case 33:
				switch (_objetoInterac.getID()) {
				case 7505:
					return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
				}
				return false;
			case 41:
				switch (_objetoInterac.getID()) {
				case 7506:
					return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
				}
				return false;
			case 34:
				switch (_objetoInterac.getID()) {
				case 7507:
					return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
				}
				return false;
			case 174:
				switch (_objetoInterac.getID()) {
				case 7557:
					return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
				}
				return false;
			case 38:
				switch (_objetoInterac.getID()) {
				case 7508:
					return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
				}
				return false;
			case 35:
				switch (_objetoInterac.getID()) {
				case 7509:
					return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
				}
				return false;
			case 155:
				switch (_objetoInterac.getID()) {
				case 7554:
					return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
				}
				return false;
			case 158:
				switch (_objetoInterac.getID()) {
				case 7552:
					return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
				}
				return false;
			case 102:
				switch (_objetoInterac.getID()) {
				case 7519:
					return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
				}
				return false;
			case 48:
				return _objetoInterac.getID() == 7005;// 7510
			case 32:
				return _objetoInterac.getID() == 7002;
			case 24:
				switch (_objetoInterac.getID()) {
				case 7520:
					return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
				}
				return false;
			case 25:
				switch (_objetoInterac.getID()) {
				case 7522:
					return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
				}
				return false;
			case 26:
				switch (_objetoInterac.getID()) {
				case 7523:
					return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
				}
				return false;
			case 28:
				switch (_objetoInterac.getID()) {
				case 7525:
					return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
				}
				return false;
			case 56:
				switch (_objetoInterac.getID()) {
				case 7524:
					return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
				}
				return false;
			case 162:
				switch (_objetoInterac.getID()) {
				case 7556:
					return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
				}
				return false;
			case 55:
				switch (_objetoInterac.getID()) {
				case 7521:
					return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
				}
				return false;
			case 29:
				switch (_objetoInterac.getID()) {
				case 7526:
					return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
				}
				return false;
			case 31:
				switch (_objetoInterac.getID()) {
				case 7528:
					return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
				}
				return false;
			case 30:
				switch (_objetoInterac.getID()) {
				case 7527:
					return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
				}
				return false;
			case 161:
				switch (_objetoInterac.getID()) {
				case 7555:
					return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
				}
				return false;
			case 23:
				return _objetoInterac.getID() == 7019;
			case 71:
				switch (_objetoInterac.getID()) {
				case 7533:
					return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
				}
				return false;
			case 72:
				switch (_objetoInterac.getID()) {
				case 7534:
					return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
				}
				return false;
			case 73:
				switch (_objetoInterac.getID()) {
				case 7535:
					return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
				}
				return false;
			case 74:
				switch (_objetoInterac.getID()) {
				case 7536:
					return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
				}
				return false;
			case 160:
				switch (_objetoInterac.getID()) {
				case 7551:
					return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
				}
				return false;
			case 133:
				return _objetoInterac.getID() == 7024;
			case 128:
				switch (_objetoInterac.getID()) {
				case 7530:
					return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
				}
				return false;
			case 124:
				switch (_objetoInterac.getID()) {
				case 7529:
					return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
				}
				return false;
			case 136:
				switch (_objetoInterac.getID()) {
				case 7544:
					return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
				}
				return false;
			case 140:
				switch (_objetoInterac.getID()) {
				case 7543:
					return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
				}
				return false;
			case 125:
				switch (_objetoInterac.getID()) {
				case 7532:
					return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
				}
				return false;
			case 129:
				switch (_objetoInterac.getID()) {
				case 7531:
					return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
				}
				return false;
			case 126:
				switch (_objetoInterac.getID()) {
				case 7537:
					return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
				}
				return false;
			case 130:
				switch (_objetoInterac.getID()) {
				case 7538:
					return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
				}
				return false;
			case 127:
				switch (_objetoInterac.getID()) {
				case 7539:
					return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
				}
				return false;
			case 131:
				switch (_objetoInterac.getID()) {
				case 7540:
					return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
				}
				return false;
			case 109:
			case 27:
				return _objetoInterac.getID() == 7001;
			case 135:
				return _objetoInterac.getID() == 7022;
			case 134:
				return _objetoInterac.getID() == 7023;
			case 132:
				return _objetoInterac.getID() == 7025;
			case 157:
				return (_objetoInterac.getID() == 7030 || _objetoInterac.getID() == 7031);
			case 44:// guardar el zaap
			case 114:// utilizar el zaap
				switch (_objetoInterac.getID()) {
				// Zaaps
				case 7000:
				case 7026:
				case 7029:
				case 4287:
					return true;
				}
				return false;
			case 175:// Acceder
			case 176:// Comprar
			case 177:// Vender
			case 178:// Modificar el precio de venta
				switch (_objetoInterac.getID()) {
				// Cercados
				case 6763:
				case 6766:
				case 6767:
				case 6772:
					return true;
				}
				return false;
			// Se retorna a ircanam
			case 183:
				switch (_objetoInterac.getID()) {
				case 1845:
				case 1853:
				case 1854:
				case 1855:
				case 1856:
				case 1857:
				case 1858:
				case 1859:
				case 1860:
				case 1861:
				case 1862:
				case 2319:
					return true;
				}
				return false;
			// yunque magico
			case 1:
			case 113:
			case 115:
			case 116:
			case 117:
			case 118:
			case 119:
			case 120:
				return _objetoInterac.getID() == 7020;
			// yunque
			case 19:
			case 143:
			case 145:
			case 144:
			case 142:
			case 146:
			case 67:
			case 21:
			case 65:
			case 66:
			case 20:
			case 18:
				return _objetoInterac.getID() == 7012;
			// sastremago
			case 167:
			case 165:
			case 166:
				return _objetoInterac.getID() == 7036;
			// Zapateromago
			case 164:
			case 163:
				return _objetoInterac.getID() == 7037;
			// Joyeromago
			case 168:
			case 169:
				return _objetoInterac.getID() == 7038;
			// Bricoleur
			case 171:
			case 182:
				return _objetoInterac.getID() == 7039;
			// Forgeur Bouclier
			case 156:
				return _objetoInterac.getID() == 7027;
			// Zapatero
			case 13:
			case 14:
				return _objetoInterac.getID() == 7011;
			// Tailleur (Dos)
			case 123:
			case 64:
				return _objetoInterac.getID() == 7015;
			// Escultor
			case 17:
			case 16:
			case 147:
			case 148:
			case 149:
			case 15:
				return _objetoInterac.getID() == 7013;
			// Tailleur (Haut)
			case 63:
				return (_objetoInterac.getID() == 7014 || _objetoInterac.getID() == 7016);
			case 11:
			case 12:
				return (_objetoInterac.getID() >= 7008 && _objetoInterac.getID() <= 7010);
			// casa
			case 81:// poner cerrojo
			case 84:// comprar
			case 97:// Entrar
			case 98:// Vender
			case 108:// modificar el precio de venta
				return (_objetoInterac.getID() >= 6700 && _objetoInterac.getID() <= 6776);
			case 104:// abrir cofre
			case 105:// codigo
				return (_objetoInterac.getID() >= 7350 && _objetoInterac.getID() <= 7353);
			case 170:// lista de artesanos
				return _objetoInterac.getID() == 7035;
			case 121:
			case 181:
				return _objetoInterac.getID() == 7021;
			case 152:
				return _objetoInterac.getID() == 7549 && pescarKuakua;
			case 150:
				return (_objetoInterac.getID() == 7546 || _objetoInterac.getID() == 7547);
			case 153:
				return _objetoInterac.getID() == 7352;
			case 151: // Etabli pyrotechnique
				return _objetoInterac.getID() == 7028;
			case 62:// fontaine de jouvence
				return _objetoInterac.getID() == 7004;
			default:
				System.out.println("Bug al verificar si se puede realizar la accion ID = " + accionID);
				return false;
			}
		}

		void iniciarAccion(Personaje perso, AccionDeJuego GA) {
			int accionID = -1;
			short celdaID = -1;
			try {
				accionID = Integer.parseInt(GA._args.split(";")[1]);
				celdaID = Short.parseShort(GA._args.split(";")[0]);
			} catch (Exception e) {
			}
			if (perso.getMapa().getID() == 29997) {
				if (!perso.pagadoImpuesto) {
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "No tienes permiso para usar los recursos de este mapa",
							Colores.ROJO);
					return;
				}
			}
			if (accionID == -1) {
				return;
			}
			if (CentroInfo.esTrabajo(accionID)) {
				perso.iniciarAccionOficio(accionID, _objetoInterac, GA, this);
				return;
			}
			switch (accionID) {
			case 62: // Source de jouvence
				if (GA != null) {
					perso.borrarGA(GA);
				}
				if (perso.getPDV() < perso.getPDVMAX() && perso.getNivel() < 16) {
					perso.fullPDV();
					GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso);
				} else {
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso,
							"AcciÓn imposible, tienes la vida entera o un nivel superior a 15!", "ff0000");
					return;
				}
				break;
			case 44:// Salvar posicion
				String str = _mapaID + "," + _id;
				perso.setSalvarZaap(str);
				GestorSalida.ENVIAR_Im_INFORMACION(perso, "06");
				if (GA != null) {
					perso.borrarGA(GA);
				}
				break;
			case 102:// pozo de agua
				if ((_objetoInterac == null) || !_objetoInterac.esInteractivo() || _objetoInterac.getEstado() != CentroInfo.IO_ESTADO_LLENO) {
					return;
				}
				perso.setOcupado(true);
				_objetoInterac.setEstado(CentroInfo.IO_ESTADO_ESPERA);
				_objetoInterac.setInteractivo(false);
				GestorSalida.ENVIAR_GA_ACCION_JUEGO_AL_MAPA(perso, perso.getMapa(), "" + GA._idUnica, 501,
						perso.getID() + "",
						_id + "," + _objetoInterac.getDuracion() + "," + _objetoInterac.getAnimacionPJ());
				GestorSalida.ENVIAR_GDF_ESTADO_OBJETO_INTERACTIVO(perso.getMapa(), this);
				break;
			case 114:// Utiliser (zaap)
				perso.abrirMenuZaap();
				if (GA != null) {
					perso.borrarGA(GA);
				}
				break;
			case 152:// Pescar Kuakuas
				if (!_objetoInterac.esInteractivo() || _objetoInterac.getEstado() != CentroInfo.IO_ESTADO_LLENO) {
					return;
				}
				perso.setPescarKuakua(false);
				_objetoInterac.setEstado(CentroInfo.IO_ESTADO_ESPERA);
				_objetoInterac.setInteractivo(false);
				GestorSalida.ENVIAR_GA_ACCION_JUEGO_AL_MAPA(perso, perso.getMapa(), "" + GA._idUnica, 501,
						perso.getID() + "",
						_id + "," + _objetoInterac.getDuracion() + "," + _objetoInterac.getAnimacionPJ());
				GestorSalida.ENVIAR_GDF_ESTADO_OBJETO_INTERACTIVO(perso.getMapa(), this);
				break;
			case 157: // zaapi
				boolean pasala = false;
				Casa cac = null;
				if (perso.getCasa() != null) {
					cac = MundoDofus.getCasaDentroPorMapa(perso.getCasa().getMapaIDFuera());
				}
				if ((cac != null && cac.getDueÑoID() == perso.getCuentaID())) {
					pasala = true;
				}
				StringBuilder listaZaapi = new StringBuilder("");
				if (perso.getMapa()._subArea.getArea().getID() == 7
						&& (perso.getAlineacion() == 1 || perso.getAlineacion() == 0 || perso.getAlineacion() == 3)
						|| pasala) {
					String[] Zaapis = CentroInfo.ZAAPI_BONTA.split(",");
					int cantidad = 0;
					int precio = 20;
					if (perso.getAlineacion() == 1) {
						precio = 10;
					}
					if (cac != null) {
						listaZaapi.append("9999;0|");
					}
					for (String s : Zaapis) {
						if (cantidad >= Zaapis.length - 1) {
							listaZaapi.append(s + ";" + precio);
						} else {
							listaZaapi.append(s + ";" + precio + "|");
						}
						cantidad++;
					}

					perso.setZaaping(true);
					GestorSalida.ENVIAR_Wc_LISTA_ZAPPIS(perso, listaZaapi.toString());
				}
				if (perso.getMapa()._subArea.getArea().getID() == 11
						&& (perso.getAlineacion() == 2 || perso.getAlineacion() == 0 || perso.getAlineacion() == 3)) {
					String[] Zaapis = CentroInfo.ZAAPI_BRAKMAR.split(",");
					int cantidad = 0;
					int precio = 20;
					if (perso.getAlineacion() == 2) {
						precio = 10;
					}
					for (String s : Zaapis) {
						if (cantidad == Zaapis.length) {
							listaZaapi.append(s + ";" + precio);
						} else {
							listaZaapi.append(s + ";" + precio + "|");
						}
						cantidad++;
					}
					perso.setZaaping(true);
					GestorSalida.ENVIAR_Wc_LISTA_ZAPPIS(perso, listaZaapi.toString());
				}
				if (GA != null) {
					perso.borrarGA(GA);
				}
				break;
			case 175:// Acceder a un cercado
				perso.abrirCercado();
				if (GA != null) {
					perso.borrarGA(GA);
				}
				break;
			case 176:// comprar cercado
				Cercado cercado = perso.getMapa().getCercado();
				if (cercado.getDueÑo() == -1) {// Publico
					GestorSalida.ENVIAR_Im_INFORMACION(perso, "196");
					return;
				}
				if (cercado.getPrecio() == 0) {// no en venta
					GestorSalida.ENVIAR_Im_INFORMACION(perso, "197");
					return;
				}
				if (perso.getGremio() == null) {// para el gremio
					GestorSalida.ENVIAR_Im_INFORMACION(perso, "1135");
					return;
				}
				if (perso.getMiembroGremio().getRango() != 1) {// no miembros
					GestorSalida.ENVIAR_Im_INFORMACION(perso, "198");
					return;
				}
				GestorSalida.GAME_SEND_R_PACKET(perso, "D" + cercado.getPrecio() + "|" + cercado.getPrecio());
				if (GA != null) {
					perso.borrarGA(GA);
				}
				break;
			case 177:// Vender cercado
			case 178:// Modificar el precio de venta
				Cercado cercado1 = perso.getMapa().getCercado();
				if (cercado1.getDueÑo() == -1) {
					GestorSalida.ENVIAR_Im_INFORMACION(perso, "194");
					return;
				}
				if (cercado1.getDueÑo() != perso.getID()) {
					GestorSalida.ENVIAR_Im_INFORMACION(perso, "195");
					return;
				}
				GestorSalida.GAME_SEND_R_PACKET(perso, "D" + cercado1.getPrecio() + "|" + cercado1.getPrecio());
				if (GA != null) {
					perso.borrarGA(GA);
				}
				break;
			case 183:// Retornar a ircanam
				/*
				 * if (perso.getNivel() > 15) { GestorSalida.ENVIAR_Im_INFORMACION(perso,
				 * "1127"); perso.getCuenta().getEntradaPersonaje().borrarGA(GA); return; }
				 */
				short mapaID = CentroInfo.getMapaInicio(perso.getClase(true));
				int celdaId = 314;
				perso.teleport(mapaID, celdaId);
				if (GA != null) {
					perso.borrarGA(GA);
				}
				break;
			case 81:// cerrojear casa
				perso.cerrojocasa = true;
				Casa casa1 = Casa.getCasaPorUbicacion(perso.getMapa().getID(), celdaID);
				if (casa1 == null) {
					return;
				}
				perso.setCasa(casa1);
				casa1.bloquear(perso);
				if (GA != null) {
					perso.borrarGA(GA);
				}
				break;
			case 84:// teleportar a una casa
				Casa casa2 = Casa.getCasaPorUbicacion(perso.getMapa().getID(), celdaID);
				if (casa2 == null) {
					return;
				}
				perso.setCasa(casa2);
				casa2.respondeA(perso);
				if (GA != null) {
					perso.borrarGA(GA);
				}
				break;
			case 97:// Comprar casa
				Casa casa3 = Casa.getCasaPorUbicacion(perso.getMapa().getID(), celdaID);
				if (casa3 == null) {
					return;
				}
				perso.setCasa(casa3);
				casa3.comprarEstaCasa(perso);
				if (GA != null) {
					perso.borrarGA(GA);
				}
				break;
			case 104:// abrir cofre privado
				Cofre cofre = Cofre.getCofrePorUbicacion(perso.getMapa().getID(), celdaID);
				if (cofre == null) {
					try {
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "El cofre estÁ bugeado.... Intentando arreglarlo...",
								Colores.ROJO);
						Mapa mapa = perso.getMapa();
						Casa casa = MundoDofus.getCasaDentroPorMapa(perso.getMapa().getID());
						if (casa == null) {
							return;
						}
						Cofre cofre2 = new Cofre(MundoDofus.getSigIDCofre(), casa.getID(), mapa.getID(), celdaID, "", 0,
								"-", casa.getDueÑoID());
						MundoDofus.addCofre(cofre2);
						GestorSQL.INSERT_COFRE(cofre2);
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso,
								"El cofre se arregl� autom�ticamente, vuelva a abrirlo.", Colores.VERDE);
					} catch (Exception e) {
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso,
								"El cofre no se pudo arreglar, contacte con un administrador para arreglarlo.",
								Colores.ROJO);
					}
					return;
				}
				perso.setCofre(cofre);
				cofre.chekeadoPor(perso);
				if (GA != null) {
					perso.borrarGA(GA);
				}
				break;
			case 153:// abrir cofre privado
				Cofre cofre3 = Cofre.getCofrePorUbicacion(0, 0);
				if (cofre3 == null) {
					System.out.println("COFRE BUGEADO EN MAPA: " + perso.getMapa().getID() + " CELDAID : " + celdaID);
					return;
				}
				perso.setCofre(cofre3);
				Cofre.abrirCofre(perso, "-", true);
				if (GA != null) {
					perso.borrarGA(GA);
				}
				break;
			case 105:// cerrojear cofre
				Cofre cofre1 = Cofre.getCofrePorUbicacion(perso.getMapa().getID(), celdaID);
				if (cofre1 == null) {
					try {
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "El cofre estÁ bugeado.... Intentando arreglarlo...",
								Colores.ROJO);
						Mapa mapa = perso.getMapa();
						Casa casa = MundoDofus.getCasaDentroPorMapa(perso.getMapa().getID());
						if (casa == null) {
							return;
						}
						Cofre cofre2 = new Cofre(MundoDofus.getSigIDCofre(), casa.getID(), mapa.getID(), celdaID, "", 0,
								"-", casa.getDueÑoID());
						MundoDofus.addCofre(cofre2);
						GestorSQL.INSERT_COFRE(cofre2);
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso,
								"El cofre se arregl� autom�ticamente, vuelva a abrirlo.", Colores.VERDE);
					} catch (Exception localException13) {
						System.out.println("s" + localException13.getMessage());
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso,
								"El cofre no se pudo arreglar, contacte con un administrador para arreglarlo.",
								Colores.ROJO);
					}
					return;
				}
				perso.setCofre(cofre1);
				cofre1.bloquear(perso);
				if (GA != null) {
					perso.borrarGA(GA);
				}
				break;
			case 98:// Vender
			case 108:// Modificar precio de venta
				Casa casa4 = Casa.getCasaPorUbicacion(perso.getMapa().getID(), celdaID);
				if (casa4 == null) {
					return;
				}
				perso.setCasa(casa4);
				casa4.venderla(perso);
				if (GA != null) {
					perso.borrarGA(GA);
				}
				break;
			case 170:// libro de artesanos
				perso.setListaArtesanos(true);
				GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(perso, 14,
						"15;16;17;18;19;20;24;25;26;27;28;31;36;41;43;44;45;46;47;48;49;50;62;63;64");
				if (GA != null) {
					perso.borrarGA(GA);
				}
				break;
			case 181:
			case 121:
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "Deshabilitado temporalmente!!!", Colores.ROJO);
				/*
				 * if (!perso.puedeAbrir) {
				 * GestorSalida.ENVIAR_BN_NADA(perso.getCuenta().getEntradaPersonaje()); return;
				 * } if (!perso.getRompiendo())
				 * GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(perso, 3, "2;181");
				 * perso.setRompiendo(true); if (perso.continua) { GestorSalida.enviar(perso,
				 * "cMK|-99|Zino|Ahora combina el Casco y el Hierro|"); }
				 */
				if (GA != null) {
					perso.borrarGA(GA);
				}
				break;
			case 150:
				_caminable = false;
				_objetoInterac.setEstado(CentroInfo.IO_ESTADO_ESPERA);
				_objetoInterac.setInteractivo(false);
				GestorSalida.ENVIAR_GA_ACCION_JUEGO_AL_MAPA(perso, perso.getMapa(), "" + GA._idUnica, 501,
						perso.getID() + "",
						_id + "," + _objetoInterac.getDuracion() + "," + _objetoInterac.getAnimacionPJ());
				GestorSalida.ENVIAR_GDF_FORZADO_PERSONAJE(perso, celdaID, 3, 0);
				break;
			default:
				System.out.println("Bug al iniciar la accion ID = " + accionID);
				break;
			}
		}

		void finalizarAccion(Personaje perso, AccionDeJuego GA) {
			int accionID = -1;
			try {
				accionID = Integer.parseInt(GA._args.split(";")[1]);
			} catch (Exception e) {
			}
			if (accionID == -1) {
				return;
			}
			if (CentroInfo.esTrabajo(accionID)) {
				if (GA != null) {
					perso.borrarGA(GA);
				}
				perso.finalizarAccionOficio(accionID, _objetoInterac, this);
				return;
			}
			switch (accionID) {
			case 62:// jouvence
				break;
			case 44:// Salvar un zaap
			case 81:// V�rouiller maison
			case 84:// abrir casa
			case 97:// comprar casa
			case 98:// Vender
			case 104:// abrir cofre
			case 108:// Modificar precio de venta
			case 157:// Zaapi
			case 183: // retornar a incarna
			case 114:// Utiliser (zaap)
			case 175:// Acceder a un cercado
			case 176:// comprar cercado
			case 177:// Vender cercado
			case 178:// Modificar el precio de venta
			case 105:// cerrojear cofre
			case 181:
			case 121:
				break;
			case 150:// maquina de fuerza
				_objetoInterac.setInteractivo(false);
				_objetoInterac.iniciarTiempoRefresco();
				_objetoInterac.setEstado(CentroInfo.IO_ESTADO_VACIO);
				break;
			case 152:// pescar kuakua
			case 102:// Pozo de Agua
				perso.setOcupado(false);
				if (_objetoInterac == null) {
					return;
				}
				_objetoInterac.setInteractivo(false);
				_objetoInterac.iniciarTiempoRefresco();
				GestorSalida.ENVIAR_GDF_ESTADO_OBJETO_INTERACTIVO(perso.getMapa(), this);
				Objeto obj = null;
				int cantidad = 1;
				if (accionID == 102) {
					cantidad = Formulas.getRandomValor(1, 10);// se a entre 1 a 10 agua
					obj = MundoDofus.getObjModelo(311).crearObjDesdeModelo(cantidad, false);
				} else if (accionID == 152) {
					Random rand = new Random();
					int x = rand.nextInt(6);
					if (x == 5) {
						GestorSalida.enviar(perso, "cS" + perso.getID() + "|11");
						obj = MundoDofus.getObjModelo(6659).crearObjDesdeModelo(1, false);
					} else {
						GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso, "Que l�stima, prueba en otra ocasi�n");
						GestorSalida.enviar(perso, "cS" + perso.getID() + "|12");
						_objetoInterac.setEstado(CentroInfo.IO_ESTADO_VACIO);
						break;
					}
				}
				if (!perso.addObjetoSimilar(obj, true, -1)) {
					MundoDofus.addObjeto(obj, true);
					perso.addObjetoPut(obj);
					GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(perso, obj);
				}
				if (obj != null) {
					perso.updateObjetivoRecolecta(obj.getModelo().getID(), cantidad, "recolectar", false);
				}
				GestorSalida.ENVIAR_IQ_NUMERO_ARRIBA_PJ(perso, perso.getID(), cantidad);
				_objetoInterac.setEstado(CentroInfo.IO_ESTADO_VACIO);
				break;
			default:
				System.out.println("Bug al finalizar la accion ID = " + accionID);
				break;
			}
			if (GA != null) {
				perso.borrarGA(GA);
			}
		}

		public void nullearCeldaAccion() {
			_celdaAccion = null;
		}

		public void addObjetoTirado(Objeto obj, Personaje objetivo) {
			_objetoTirado = obj;
		}

		private void borrarObjetoTirado() {
			_objetoTirado = null;
		}

		public boolean lineaDeVistaLibre() {
			if (!_mapa.luchadoresen(_id) || !_lineaDeVista) {
				return _lineaDeVista;
			}
			boolean libre = true;
			for (Luchador f : _mapa._luchadores.keySet()) {
				if (!f.esInvisible() && f.getCeldaPelea().getID() == _id) {
					libre = false;
					break;
				}
			}
			return _lineaDeVista && libre;
		}

	}
}
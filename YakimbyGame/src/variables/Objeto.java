package variables;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import estaticos.CentroInfo;
import estaticos.Emu;
import estaticos.Formulas;
import estaticos.MundoDofus;
import estaticos.MundoDofus.Duo;
import variables.Personaje.Stats;

public class Objeto {
	ObjetoModelo _modelo;
	protected int _cantidad = 1;
	protected int _posicion = -1;
	protected int _id;
	private Stats _stats = new Stats();
	private ArrayList<EfectoHechizo> _efectos = new ArrayList<>();
	private Map<Integer, String> _textoStats = new TreeMap<>();
	private ArrayList<String> _hechizoStats = new ArrayList<>();
	private int _objevivoID;
	protected int _idObjModelo;

	public Objeto() {
		_cantidad = 1;
		_posicion = -1;
		_stats = new Stats();
		_efectos = new ArrayList<>();
		_textoStats = new TreeMap<>();
		_hechizoStats = new ArrayList<>();
	}

	public Objeto(int id, int modeloBD, int cant, int pos, String strStats, int idObjevi) {
		_id = id;
		_modelo = MundoDofus.getObjModelo(modeloBD);
		_cantidad = cant;
		_posicion = pos;
		_stats = new Stats();
		_textoStats = new TreeMap<>();
		_hechizoStats = new ArrayList<>();
		_objevivoID = idObjevi;
		_idObjModelo = modeloBD;
		convertirStringAStats(strStats);
	}

	private Objeto(int id, int modeloBD, int cant, int pos, String strStats, ArrayList<EfectoHechizo> efectos,
			int idObjevi) {
		_id = id;
		_modelo = MundoDofus.getObjModelo(modeloBD);
		_cantidad = cant;
		_posicion = pos;
		_stats = new Stats();
		_textoStats = new TreeMap<>();
		_hechizoStats = new ArrayList<>();
		_efectos = efectos;
		_objevivoID = idObjevi;
		_idObjModelo = modeloBD;
		convertirStringAStats(strStats);
	}

	public int getObjeviID() {
		return _objevivoID;
	}

	public void setObjeviID(int id) {
		_objevivoID = id;
	}

	public int getIDModelo() {
		return _idObjModelo;
	}

	public void setIDModelo(int idModelo) {
		_idObjModelo = idModelo;
		_modelo = MundoDofus.getObjModelo(idModelo);
	}

	public boolean esStatExo(int statID) {
		if (!CentroInfo.esStatDePelea(statID)) {
			return false;
		}
		return !_modelo.tieneStatInicial(statID);// siempre sera positivo
	}

	public boolean esStatOver(final int statID, int valor) {
		if (!CentroInfo.esStatDePelea(statID)) {
			return false;
		}
		Duo<Integer, Integer> duo = _modelo.getDuoInicial(statID);
		if (duo == null) {
			return false;
		}
		return valor > duo._segundo;
	}

	public void convertirStringAStats(String strStats) {
		_stats = new Stats();
		_textoStats = new TreeMap<>();
		_hechizoStats = new ArrayList<>();
		_efectos = new ArrayList<>();
		String[] split = strStats.split(",");
		for (String s : split) {
			try {
				String hechizo = s;
				String[] stats = s.split("#");
				int statID = Integer.parseInt(stats[0], 16);
				if (statID == 812) {
					_textoStats.put(statID, stats[4]);
					continue;
				}
				if (CentroInfo.esStatTexto(statID)) {
					if (statID == 998 || statID == 997 || statID == 996 || statID == 994 || statID == 989
							|| statID == 990 || statID == 988 || statID == 987 || statID == 986 || statID == 985
							|| statID == 983 || statID >= 1000 && statID <= 1013 || statID == 2000) {
						_textoStats.put(statID, stats[4]);
					} else if (statID == 800 || statID == 811 || statID == 961 || statID == 962 || statID == 960
							|| statID == 950 || statID == 951) {
						_textoStats.put(statID, stats[3]);
					} else {
						_textoStats.put(statID, stats[3]);
					}
					continue;
				}
				if (statID >= 281 && statID <= 294) {
					_hechizoStats.add(hechizo);
					continue;
				}
				String jet = stats[4];
				boolean siguiente = true;
				for (int a : CentroInfo.ID_EFECTOS_ARMAS) {
					if (a == statID) {
						int id = statID;
						String min = stats[1];
						String max = stats[2];
						String args = min + ";" + max + ";-1;-1;0;" + jet;
						_efectos.add(new EfectoHechizo(id, args, 0, -1));
						siguiente = false;
					}
				}
				if (!siguiente) {
					continue;
				}
				int valor = Integer.parseInt(stats[1], 16);
				_stats.addUnStat(statID, valor);
			} catch (Exception e) {
				continue;
			}
		}
		if (Emu.ARMAS_ENCARNACIONES.contains(_idObjModelo)) {
			Encarnacion encarnacion = MundoDofus.getEncarnacion(_id);
			if (encarnacion == null) {
				_stats.addUnStat(118, 1);
				_stats.addUnStat(119, 1);
				_stats.addUnStat(123, 1);
				_stats.addUnStat(126, 1);
			} else {
				int valor = encarnacion.getNivel();
				_stats.addUnStat(118, valor);
				_stats.addUnStat(119, valor);
				_stats.addUnStat(123, valor);
				_stats.addUnStat(126, valor);
			}
		}

		//libros runicos jhon

			if (Emu.LIBROS_RUNICOS.contains(_idObjModelo)) {
				LibrosRunicos librosRunicos = MundoDofus.getLibroRunico(_id);
				if (librosRunicos == null) {

					if(_idObjModelo == 30947) { //fuego
						_stats.addUnStat(126, 1);
						_stats.addUnStat(178, 1);
					}else if(_idObjModelo == 30944) { // suerte
						_stats.addUnStat(123, 1);
						_stats.addUnStat(176, 1);
					}else if(_idObjModelo == 30949) { //aire
						_stats.addUnStat(119, 1);
						_stats.addUnStat(161, 1);
					}else if(_idObjModelo == 30950) {//fuerza
						_stats.addUnStat(118, 1);
						_stats.addUnStat(226, 1);
					}


				} else {
					int valor = librosRunicos.getNivel();

					if(_idObjModelo == 30947) {//fuego
						_stats.addUnStat(126, valor);
						_stats.addUnStat(178, valor);
					}else if(_idObjModelo == 30948) {//suerte
						_stats.addUnStat(123, valor);
						_stats.addUnStat(176, valor);
					}else if(_idObjModelo == 30949) {//aire
						_stats.addUnStat(119, valor);
						_stats.addUnStat(161, valor);
					}else if(_idObjModelo == 30950) {//fuerza
						_stats.addUnStat(118, valor);
						_stats.addUnStat(226, valor);
					}

				}
			}

		//libros runicos jhon





	}

	public String convertirStatsAString(int tipoc) {
		if (_modelo == null) {
			return "";
		}
		int tipoModelo = _modelo.getTipo();
		StringBuilder stats = new StringBuilder("");
		int idSetModelo = _modelo.getSetID();
		if (Emu.ARMAS_ENCARNACIONES.contains(_idObjModelo) || tipoModelo == 33 || tipoModelo == 76 || tipoModelo == 13
				|| tipoModelo == 42 || tipoModelo == 49 || tipoModelo == 69 || tipoModelo == 12 || tipoModelo == 83) {
			stats.append(_modelo.getStringStatsObj());
			return stats.toString();
		}
		if(Emu.LIBROS_RUNICOS.contains(_idObjModelo)) {
			stats.append(_modelo.getStringStatsObj());
			return stats.toString();
		}
		if (tipoc == 0) {
			if ((tipoModelo == 18 || tipoModelo == 90) && (MundoDofus.getIDTodasMascotas().contains(_id))) {
				Mascota mascota = MundoDofus.getMascota(_id);
				if (tipoModelo == 18) {
					return mascota.analizarStatsMascota();
				}
			}
		} else if (tipoc == 2) {
			if ((tipoModelo == 18 || tipoModelo == 90) && (MundoDofus.getIDTodasMascotas().contains(_id))) {
				Mascota mascota = MundoDofus.getMascota(_id);
				if (tipoModelo == 18) {
					return mascota.getStringStats();
				}
			}
		}
		if (tipoModelo == 113) {
			if (_objevivoID == 0) {
				byte tipo = 0;
				if (_idObjModelo == 9233 || _idObjModelo == 12002 || _idObjModelo == 47012 || _idObjModelo == 12004
						|| _idObjModelo == 43001 || _idObjModelo == 34674 || _idObjModelo == 43003
						|| _idObjModelo == 43005 || _idObjModelo == 31004) {
					tipo = 17;
				} else if (_idObjModelo == 9234 || _idObjModelo == 12001 || _idObjModelo == 47000 || _idObjModelo == 12003
						|| _idObjModelo == 43000 || _idObjModelo == 43002 || _idObjModelo == 43004 || _idObjModelo == 31003) {
					tipo = 16;
				} else if (_idObjModelo == 9255) {
					tipo = 1;
				} else if (_idObjModelo == 9256) {
					tipo = 9;
				} else if (_idObjModelo == 47021) {
					tipo = 82;
				}
				stats.append(
						"3cc#0#0#1,3cb#0#0#1,3cd#0#0#" + Integer.toHexString(tipo) + "," + "3ca#0#0#0," + "3ce#0#0#0");
			} else {
				Objevivo objevi = MundoDofus.getObjevivos(_objevivoID);
				if (objevi == null) {
					_objevivoID = 0;
				} else {
					if (stats.length() > 0) {
						stats.append(",");
					}
					stats.append(objevi.convertirAString());
				}
			}
			return stats.toString();
		}
		if ((idSetModelo >= 81 && idSetModelo <= 92) || (idSetModelo >= 201 && idSetModelo <= 212)) {
			for (String hechizo : _hechizoStats) {
				if (stats.length() > 0) {
					stats.append(",");
				}
				String[] hechi = hechizo.split("#");
				int idhechizo = Integer.parseInt(hechi[1], 16);
				String cantidad = "";
				try {
					cantidad = hechi[3].split("\\+")[1];
				} catch (ArrayIndexOutOfBoundsException e) {
					cantidad = hechi[3].split("\\+")[0];
				}
				stats.append(hechi[0] + "#" + hechi[1] + "#0#" + cantidad + "#0d0+" + idhechizo);
			}
		}
		for (EfectoHechizo EH : _efectos) {
			if (stats.length() > 0) {
				stats.append(",");
			}
			String[] infos = EH.getArgs().split(";");
			try {
				stats.append(
						Integer.toHexString(EH.getEfectoID()) + "#" + infos[0] + "#" + infos[1] + "#0#" + infos[5]);
			} catch (Exception e) {
				continue;
			}
		}
		for (Entry<Integer, Integer> entry : _stats.getStatsComoMap().entrySet()) {
			int statID = entry.getKey();
			if (statID == 998 || statID == 997 || statID == 996 || statID == 994 || statID == 988 || statID == 987
					|| statID == 986 || statID == 985 || statID == 983 || statID == 960 || statID == 961
					|| statID == 962 || statID == 963 || statID == 964 || statID >= 1000 && statID <= 1013
					|| statID == 2000) {
				continue;
			}
			if (stats.length() > 0) {
				stats.append(",");
			}
			boolean esExo = esStatOver(entry.getKey(), entry.getValue());
			String jet = "0d0+" + entry.getValue();
			stats.append(Integer.toHexString(statID) + "#" + Integer.toHexString(entry.getValue()) + "#0#"
					+ (esExo ? "18B5B" : "0") + "#" + jet);
		}
		for (Entry<Integer, String> entry : _textoStats.entrySet()) {
			int statID = entry.getKey();
			if (stats.length() > 0) {
				stats.append(",");
			}
			if (statID == 800 || statID == 811 || statID == 961 || statID == 962 || statID == 960 || statID == 950
					|| statID == 951) {
				stats.append(Integer.toHexString(statID) + "#0#0#" + entry.getValue());
			} else if (statID == 812) {
				stats.append(Integer.toHexString(statID) + "#" + entry.getValue() + "#" + entry.getValue() + "#"
						+ entry.getValue());
			} else {
				stats.append(Integer.toHexString(statID) + "#0#0#0#" + entry.getValue());
			}
		}
		if (_objevivoID != 0) {
			Objevivo objevi = MundoDofus.getObjevivos(_objevivoID);
			if (objevi == null) {
				_objevivoID = 0;
			} else {
				if (stats.length() > 0) {
					stats.append(",");
				}
				stats.append(objevi.convertirAString());
			}
		}
		return stats.toString();
	}

	public String convertirStatsAStringSinObjevivo() {
		int tipoModelo = _modelo.getTipo();
		StringBuilder stats = new StringBuilder("");
		int idSetModelo = _modelo.getSetID();
		if (Emu.ARMAS_ENCARNACIONES.contains(_idObjModelo) || tipoModelo == 33 || tipoModelo == 42 || tipoModelo == 49
				|| tipoModelo == 69 || tipoModelo == 12 || tipoModelo == 83) {
			stats.append(_modelo.getStringStatsObj());
			return stats.toString();
		}
		if(Emu.LIBROS_RUNICOS.contains(_idObjModelo)) {
			stats.append(_modelo.getStringStatsObj());
			return stats.toString();
		}
		if ((idSetModelo >= 81 && idSetModelo <= 92) || (idSetModelo >= 201 && idSetModelo <= 212)) {
			for (String hechizo : _hechizoStats) {
				if (stats.length() > 0) {
					stats.append(",");
				}
				String[] hechi = hechizo.split("#");
				int idhechizo = Integer.parseInt(hechi[1], 16);
				String cantidad = "";
				try {
					cantidad = hechi[3].split("\\+")[1];
				} catch (ArrayIndexOutOfBoundsException e) {
					cantidad = hechi[3].split("\\+")[0];
				}
				stats.append(hechi[0] + "#" + hechi[1] + "#0#" + cantidad + "#0d0+" + idhechizo);
			}
		}
		for (EfectoHechizo EH : _efectos) {
			if (stats.length() > 0) {
				stats.append(",");
			}
			String[] infos = EH.getArgs().split(";");
			try {
				stats.append(
						Integer.toHexString(EH.getEfectoID()) + "#" + infos[0] + "#" + infos[1] + "#0#" + infos[5]);
			} catch (Exception e) {
				continue;
			}
		}
		for (Entry<Integer, Integer> entry : _stats.getStatsComoMap().entrySet()) {
			int statID = entry.getKey();
			if (statID == 998 || statID == 997 || statID == 996 || statID == 994 || statID == 988 || statID == 987
					|| statID == 986 || statID == 985 || statID == 983 || statID == 960 || statID == 961
					|| statID == 962 || statID == 963 || statID == 964 || statID >= 1000 && statID <= 1013
					|| statID == 2000) {
				continue;
			}
			if (stats.length() > 0) {
				stats.append(",");
			}
			String jet = "0d0+" + entry.getValue();
			stats.append(Integer.toHexString(statID) + "#" + Integer.toHexString(entry.getValue()) + "#0#0#" + jet);
		}
		for (Entry<Integer, String> entry : _textoStats.entrySet()) {
			int statID = entry.getKey();
			if (stats.length() > 0) {
				stats.append(",");
			}
			if (statID == 800 || statID == 811 || statID == 961 || statID == 962 || statID == 960 || statID == 950
					|| statID == 951) {
				stats.append(Integer.toHexString(statID) + "#0#0#" + entry.getValue());
			} else {
				stats.append(Integer.toHexString(statID) + "#0#0#0#" + entry.getValue());
			}
		}
		return stats.toString();
	}

	public void addTextoStat(int i, String s) {
		_textoStats.put(i, s);
	}

	public Map<Integer, String> getTextoStat() {
		return _textoStats;
	}

	public String getNombreMision() {
		for (Entry<Integer, String> entry : _textoStats.entrySet()) {
			if (Integer.toHexString(entry.getKey()).compareTo("3dd") == 0) {
				return entry.getValue();
			}
		}
		return null;
	}

	public Stats getStats() {
		return _stats;
	}

	public int getCantidad() {
		return _cantidad;
	}

	public void setCantidad(int cantidad) {
		_cantidad = cantidad;
	}

	public int getPosicion() {
		return _posicion;
	}

	public void setPosicion(int posicion) {
		_posicion = posicion;
	}

	public ObjetoModelo getModelo() {
		return _modelo;
	}

	public int getID() {
		return _id;
	}

	public void setID(int id) {
		_id = id;
	}

	public String stringObjetoConGuiÓn() {
		StringBuilder str = new StringBuilder();
		str.append(Integer.toHexString(_id) + "~" + Integer.toHexString(_idObjModelo) + "~"
				+ Integer.toHexString(_cantidad) + "~" + (_posicion == -1 ? "" : Integer.toHexString(_posicion)) + "~"
				+ convertirStatsAString(0));
		if (Emu.ARMAS_ENCARNACIONES.contains(_idObjModelo)) {
			Encarnacion encarnacion = MundoDofus.getEncarnacion(_id);
			if (encarnacion == null) {

				str.append(",76#1#0#0#0d0+1,77#1#0#0#0d0+1,7b#1#0#0#0d0+1,7e#1#0#0#0d0+1,29d#0#0#1");
			} else {
				String a = "#" + Integer.toHexString(encarnacion.getNivel()) + "#0#0#0d0+" + encarnacion.getNivel();
				str.append(",76" + a + ",77" + a + ",7b" + a + ",7e" + a + ",29d#0#0#"
						+ Integer.toHexString(encarnacion.getNivel()));
			}
		}

		if (Emu.LIBROS_RUNICOS.contains(_idObjModelo)) {
			LibrosRunicos librosRunicos = MundoDofus.getLibroRunico(_id);
			if (librosRunicos == null) {
				if(_idObjModelo == 30947) { //fuego

					str.append(",7e#1#0#0#0d0+1,b2#1#0#0#0d0+1,1c8#0#0#1");
				}else if(_idObjModelo == 30948) { // suerte

					str.append(",7b#1#0#0#0d0+1,b0#1#0#0#0d0+1,1c8#0#0#1");
				}else if(_idObjModelo == 30949) { //aire

					str.append(",77#1#0#0#0d0+1,a1#1#0#0#0d0+1,1c8#0#0#1");
				}else if(_idObjModelo == 30950) {//fuerza

					str.append(",76#1#0#0#0d0+1,e2#1#0#0#0d0+1,1c8#0#0#1");

				}

			} else {
				String a = "#" + Integer.toHexString(librosRunicos.getNivel()) + "#0#0#0d0+" + librosRunicos.getNivel();
				if(_idObjModelo == 30947) { //fuego
					str.append(",7e" + a + ",b2"  + a + ",1c8#0#0#"
							+ Integer.toHexString(librosRunicos.getNivel()));

				}else if(_idObjModelo == 30948) { // suerte
					str.append(",7b" + a + ",b0"  + a + ",1c8#0#0#"
							+ Integer.toHexString(librosRunicos.getNivel()));

				}else if(_idObjModelo == 30949) { //aire

					str.append(",77" + a + ",a1"  + a + ",1c8#0#0#"
							+ Integer.toHexString(librosRunicos.getNivel()));

				}else if(_idObjModelo == 30950) {//fuerza

					str.append(",76" + a + ",e2"  + a + ",1c8#0#0#"
							+ Integer.toHexString(librosRunicos.getNivel()));


				}

			}
		}

		return str.toString() + ";";
	}

	public String stringObjetoConGuiÓnMasco() {
		StringBuilder str = new StringBuilder();
		str.append(Integer.toHexString(_id) + "~" + Integer.toHexString(_idObjModelo) + "~"
				+ Integer.toHexString(_cantidad) + "~" + (_posicion == -1 ? "" : Integer.toHexString(_posicion)) + "~"
				+ convertirStatsAString(2));
		return str.toString() + ";";
	}

	public String stringObjetoConPalo(int cantidad) {// sirve para enviar la info a la pantalla de personajes
		StringBuilder str = new StringBuilder();
		str.append(_id + "|" + cantidad + "|" + _idObjModelo + "|" + convertirStatsAString(0));
		if (Emu.ARMAS_ENCARNACIONES.contains(_idObjModelo)) {
			Encarnacion encarnacion = MundoDofus.getEncarnacion(_id);
			if (encarnacion == null) {
				str.append(",76#1#0#0#0d0+1,77#1#0#0#0d0+1,7b#1#0#0#0d0+1,7e#1#0#0#0d0+1,29d#0#0#1");
			} else {
				String a = "#" + Integer.toHexString(encarnacion.getNivel()) + "#0#0#0d0+" + encarnacion.getNivel();
				str.append(",76" + a + ",77" + a + ",7b" + a + ",7e" + a + ",29d#0#0#"
						+ Integer.toHexString(encarnacion.getNivel()));
			}
		}

		if (Emu.LIBROS_RUNICOS.contains(_idObjModelo)) {
			LibrosRunicos librosRunicos = MundoDofus.getLibroRunico(_id);
			if (librosRunicos == null) {
				if(_idObjModelo == 30947) { //fuego

					str.append(",7e#1#0#0#0d0+1,b2#1#0#0#0d0+1,1c8#0#0#1");
				}else if(_idObjModelo == 30948) { // suerte

					str.append(",7b#1#0#0#0d0+1,b0#1#0#0#0d0+1,1c8#0#0#1");
				}else if(_idObjModelo == 30949) { //aire

					str.append(",77#1#0#0#0d0+1,a1#1#0#0#0d0+1,1c8#0#0#1");
				}else if(_idObjModelo == 30950) {//fuerza

					str.append(",76#1#0#0#0d0+1,e2#1#0#0#0d0+1,1c8#0#0#1");

				}

			} else {
				String a = "#" + Integer.toHexString(librosRunicos.getNivel()) + "#0#0#0d0+" + librosRunicos.getNivel();
				if(_idObjModelo == 30947) { //fuego
					str.append(",7e" + a + ",b2"  + a + ",1c8#0#0#"
							+ Integer.toHexString(librosRunicos.getNivel()));

				}else if(_idObjModelo == 30948) { // suerte
					str.append(",7b" + a + ",b0"  + a + ",1c8#0#0#"
							+ Integer.toHexString(librosRunicos.getNivel()));

				}else if(_idObjModelo == 30949) { //aire

					str.append(",77" + a + ",a1"  + a + ",1c8#0#0#"
							+ Integer.toHexString(librosRunicos.getNivel()));

				}else if(_idObjModelo == 30950) {//fuerza

					str.append(",76" + a + ",e2"  + a + ",1c8#0#0#"
							+ Integer.toHexString(librosRunicos.getNivel()));


				}

			}
		}

		return str.toString();
	}

	String convertirStatsAStringFM(String statsstr, Objeto obj, int agregar, boolean negativo) {
		StringBuilder stats = new StringBuilder("");
		boolean primero = false;
		for (EfectoHechizo SE : obj._efectos) {
			if (primero) {
				stats.append(",");
			}
			String[] infos = SE.getArgs().split(";");
			try {
				stats.append(
						Integer.toHexString(SE.getEfectoID()) + "#" + infos[0] + "#" + infos[1] + "#0#" + infos[5]);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			primero = true;
		}
		for (Entry<Integer, Integer> entry : obj._stats.getStatsComoMap().entrySet()) {
			int statID = entry.getKey();
			if (statID == 998 || statID == 997 || statID == 996 || statID == 994 || statID == 988 || statID == 987
					|| statID == 986 || statID == 985 || statID == 983 || statID == 960 || statID == 961
					|| statID == 962 || statID == 963 || statID == 964 || statID >= 1000 && statID <= 1013
					|| statID == 2000) {
				continue;
			}
			if (primero) {
				stats.append(",");
			}
			if (Integer.toHexString(statID).compareTo(statsstr) == 0) {
				int newstats = 0;
				if (negativo) {
					newstats = entry.getValue() - agregar;
					if (newstats < 1) {
						continue;
					}
				} else {
					newstats = entry.getValue() + agregar;
				}
				String jet = "0d0+0" + newstats;
				stats.append(Integer.toHexString(statID) + "#" + Integer.toHexString(entry.getValue() + agregar)
						+ "#0#0#" + jet);
			} else {
				String jet = "0d0+" + entry.getValue();
				stats.append(Integer.toHexString(statID) + "#" + Integer.toHexString(entry.getValue()) + "#0#0#" + jet);
			}
			primero = true;
		}
		for (Entry<Integer, String> entry : obj._textoStats.entrySet()) {
			int statID = entry.getKey();
			if (primero) {
				stats.append(",");
			}
			if (statID == 800 || statID == 811 || statID == 961 || statID == 962 || statID == 960 || statID == 950
					|| statID == 951) {
				stats.append(Integer.toHexString(statID) + "#0#0#" + entry.getValue());
			} else {
				stats.append(Integer.toHexString(statID) + "#0#0#0#" + entry.getValue());
			}
		}
		return stats.toString();
	}

	String stringStatsFCForja(Objeto obj, double runa) {
		StringBuilder stats = new StringBuilder("");
		boolean primero = false;
		for (EfectoHechizo EH : obj._efectos) {
			if (primero) {
				stats.append(",");
			}
			String[] infos = EH.getArgs().split(";");
			try {
				stats.append(
						Integer.toHexString(EH.getEfectoID()) + "#" + infos[0] + "#" + infos[1] + "#0#" + infos[5]);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			primero = true;
		}
		for (Entry<Integer, Integer> entry : obj._stats.getStatsComoMap().entrySet()) {
			int nuevosStats = 0;
			int statID = entry.getKey();
			int valor = entry.getValue();
			if (statID == 152 || statID == 154 || statID == 155 || statID == 157 || statID == 116 || statID == 153) {
				float a = (float) ((valor * runa) / 100);
				if (a < 1) {
					a = 1;
				}
				float chute = valor + a;
				nuevosStats = (int) Math.floor(chute);
				if (nuevosStats > Oficio.getStatBaseMax(obj._modelo, Integer.toHexString(statID))) {
					nuevosStats = Oficio.getStatBaseMax(obj._modelo, Integer.toHexString(statID));
				}
			} else {
				if (statID == 127 || statID == 101) {
					continue;
				}
				float chute = (float) (valor - ((valor * runa) / 100));
				nuevosStats = (int) Math.floor(chute);
			}
			if (nuevosStats < 1) {
				continue;
			}
			String jet = "0d0+" + nuevosStats;
			if (primero) {
				stats.append(",");
			}
			stats.append(Integer.toHexString(statID) + "#" + Integer.toHexString(nuevosStats) + "#0#0#" + jet);
			primero = true;
		}
		for (Entry<Integer, String> entry : obj._textoStats.entrySet()) {
			int statID = entry.getKey();
			if (primero) {
				stats.append(",");
			}
			if (statID == 800 || statID == 811 || statID == 961 || statID == 962 || statID == 960 || statID == 950
					|| statID == 951) {
				stats.append(Integer.toHexString(statID) + "#0#0#" + entry.getValue());
			} else {
				stats.append(Integer.toHexString(statID) + "#0#0#0#" + entry.getValue());
			}
			primero = true;
		}
		return stats.toString();
	}

	public ArrayList<EfectoHechizo> getEfectos() {
		return _efectos;
	}

	public ArrayList<EfectoHechizo> getEfectosCriticos() {
		ArrayList<EfectoHechizo> efectos = new ArrayList<>();
		for (EfectoHechizo SE : _efectos) {
			try {
				boolean boost = false;
				if (SE.getEfectoID() == 101) {
					boost = true;
				}
				String[] infos = SE.getArgs().split(";");
				if (boost) {
					efectos.add(SE);
				} else {
					int min = Integer.parseInt(infos[0], 16);
					int max = Integer.parseInt(infos[1], 16);
					int nuevoMin = (min * 200) / 100;
					int nuevoMax = (max * 200) / 100;
					if (nuevoMin == 0) {
						nuevoMin = 1;
					}
					String nuevoRango = "1d" + (nuevoMax + 1) + "+" + (nuevoMin - 1);
					String nuevosArgs = Integer.toHexString(nuevoMin) + ";" + Integer.toHexString(nuevoMax)
							+ ";-1;-1;0;" + nuevoRango;
					efectos.add(new EfectoHechizo(SE.getEfectoID(), nuevosArgs, 0, -1));
				}
			} catch (Exception e) {
			}
		}
		return efectos;
	}

	public ArrayList<EfectoHechizo> getEfectosNormales() {
		ArrayList<EfectoHechizo> efectos = new ArrayList<>();
		for (EfectoHechizo SE : _efectos) {
			try {
				boolean boost = false;
				if (SE.getEfectoID() == 101) {
					boost = true;
				}
				String[] infos = SE.getArgs().split(";");
				if (boost) {
					efectos.add(SE);
				} else {
					int min = Integer.parseInt(infos[0], 16);
					int max = Integer.parseInt(infos[1], 16);
					int nuevoMin = (min * 120) / 100;
					int nuevoMax = (max * 120) / 100;
					if (nuevoMin == 0) {
						nuevoMin = 1;
					}
					String nuevoRango = "1d" + (nuevoMax + 1) + "+" + (nuevoMin - 1);
					String nuevosArgs = Integer.toHexString(nuevoMin) + ";" + Integer.toHexString(nuevoMax)
							+ ";-1;-1;0;" + nuevoRango;

					efectos.add(new EfectoHechizo(SE.getEfectoID(), nuevosArgs, 0, -1));
				}
			} catch (Exception localException) {
			}
		}
		return efectos;
	}

	public synchronized static Objeto clonarObjeto(Objeto obj, int cantidad) {
		Objeto objeto;
		if (cantidad < 1) {
			cantidad = 1;
		}
		int tipo = obj.getModelo().getTipo();
		if (tipo == 33 || tipo == 42 || tipo == 49 || tipo == 69 || tipo == 12) {
			objeto = new Objeto(0, obj._idObjModelo, cantidad, -1, obj._modelo._statsModelo, 0);
		} else if (tipo == 85) {
			objeto = new PiedraDeAlma(0, cantidad, obj._idObjModelo, -1, obj._modelo._statsModelo);
		} else if (Emu.ARMAS_ENCARNACIONES.contains(obj._idObjModelo)) {
			objeto = new Objeto(0, obj._idObjModelo, cantidad, -1, obj._modelo._statsModelo, 0);
		} else if (Emu.LIBROS_RUNICOS.contains(obj._idObjModelo)) {
			objeto = new Objeto(0, obj._idObjModelo, cantidad, -1, obj._modelo._statsModelo, 0);
		} else {
			objeto = new Objeto(0, obj._idObjModelo, cantidad, -1, obj.convertirStatsAString(0), obj.getEfectos(), 0);
		}
		return objeto;
	}

	public void clearTodo() {
		_stats = new Stats();
		_efectos.clear();
		_textoStats.clear();
		_hechizoStats.clear();
	}

	public void setStats(Stats SS) {
		_stats = SS;
	}

	public static class ObjetoModelo {
		private int _idModelo;
		private String _statsModelo;
		private String _nombre;
		private int _tipo;
		private int _nivel;
		private int _peso;
		private int _precio;
		private int _setID;
		private int _precioVIP;
		private String _condiciones;
		private int _costePA, _alcanceMinimo, _alcanceMax, _porcentajeGC, _porcentajeFC, _bonusGC;
		private boolean _esDosManos;
		private ArrayList<Accion> _accionesDeUso = new ArrayList<>();
		private long _vendidos;
		private int _precioMedio;
		private int _mimos;
		private final Map<Integer, Duo<Integer, Integer>> _statsIniciales = new TreeMap<>();

		public ObjetoModelo(int id, String statModeloDB, String nombre, int tipo, int nivel, int peso, int precio,
				int setObjeto, String condiciones, String infoArma, int vendidos, int precioMedio, int precioVIP, int mimos) {
			_idModelo = id;
			_statsModelo = statModeloDB;
			_nombre = nombre;
			_tipo = tipo;
			_nivel = nivel;
			_peso = peso;
			_precio = precio;
			_setID = setObjeto;
			_condiciones = condiciones;
			_costePA = -1;
			_alcanceMinimo = 1;
			_alcanceMax = 1;
			_porcentajeGC = 100;
			_porcentajeFC = 10;
			_bonusGC = 0;
			_vendidos = vendidos;
			_precioMedio = precioMedio;
			_precioVIP = precioVIP;
			_mimos = mimos;
			setStatsModelo(statModeloDB);
			if (!infoArma.isEmpty()) {
				try {
					infoArma = infoArma.replace(",", ";");
					String[] infos = infoArma.split(";");
					_costePA = Integer.parseInt(infos[0]);
					_alcanceMinimo = Integer.parseInt(infos[1]);
					_alcanceMax = Integer.parseInt(infos[2]);
					_porcentajeGC = Integer.parseInt(infos[3]);
					_porcentajeFC = Integer.parseInt(infos[4]);
					_bonusGC = Integer.parseInt(infos[5]);
					if (_bonusGC == 0) {
						_bonusGC = 5;
					}
					try {
						_esDosManos = infos[6].equals("1");
					} catch (Exception e) {
						_esDosManos = false;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		public void setStatsModelo(final String nuevosStats) {
			_statsModelo = nuevosStats;
			_statsIniciales.clear();
			if (_statsModelo.isEmpty()) {
				return;
			}

			for (final String stat : _statsModelo.split(",")) {
				if (stat.isEmpty()) {
					continue;
				}
				final String[] stats = stat.split("#");
				int statID = Integer.parseInt(stats[0], 16);

				int statPositivo = CentroInfo.getStatPositivoDeNegativo(statID);
				if (CentroInfo.esStatDePelea(statID)) {
					int min = Integer.parseInt(stats[1], 16);
					int max = Integer.parseInt(stats[2], 16);
					if (max <= 0) {
						max = min;
					}
					if (statPositivo != statID) {
						min = -min;
						max = -max;
					}
					Duo<Integer, Integer> duo = new Duo<>(Math.min(min, max), Math.max(min, max));
					_statsIniciales.put(statID, duo);
				}
			}
		}

		public Map<Integer, Duo<Integer, Integer>> getStatsIniciales() {
			return _statsIniciales;
		}

		public Duo<Integer, Integer> getDuoInicial(int statID) {
			return _statsIniciales.get(statID);
		}

		public boolean tieneStatInicial(int statID) {
			return _statsIniciales.get(statID) != null;
		}

		public void addAccion(Accion A) {
			_accionesDeUso.add(A);
		}

		public boolean esDosManos() {
			return _esDosManos;
		}

		public int getBonusGC() {
			return _bonusGC;
		}

		public int getPrecioVIP() {
			return _precioVIP;
		}

		public int getAlcMinimo() {
			return _alcanceMinimo;
		}

		public int getAlcanceMax() {
			return _alcanceMax;
		}

		public int getPorcGC() {
			return _porcentajeGC;
		}

		public int getPorcFC() {
			return _porcentajeFC;
		}

		public int getCostePA() {
			return _costePA;
		}

		public int getID() {
			return _idModelo;
		}

		public String getStringStatsObj() {
			return _statsModelo;
		}

		public String getNombre() {
			return _nombre;
		}

		public int getTipo() {
			return _tipo;
		}
		public int getMimos() {
			return _mimos;
		}

		public int getNivel() {
			return _nivel;
		}

		public int getPeso() {
			return _peso;
		}

		public int getPrecio() {
			return _precio;
		}

		public int getSetID() {
			return _setID;
		}

		public String getCondiciones() {
			return _condiciones;
		}

		public Objeto crearObjDesdeModelo(int cantidad, String maxStats) {
			Objeto objeto = new Objeto(0, _idModelo, cantidad, -1, maxStats, generarEfectoModelo(maxStats), 0);
			return objeto;
		}

		public Objeto crearObjDesdeModelo(int cantidad, boolean maxStats) {
			Objeto objeto;
			if (cantidad < 1) {
				cantidad = 1;
			}
			if (_tipo == 33 || _tipo == 42 || _tipo == 49 || _tipo == 69 || _tipo == 12) {
				objeto = new Objeto(0, _idModelo, cantidad, -1, _statsModelo, 0);
			} else if (_tipo == 85) {
				objeto = new PiedraDeAlma(0, cantidad, _idModelo, -1, _statsModelo);
			} else if (Emu.ARMAS_ENCARNACIONES.contains(_idModelo)) {
				objeto = new Objeto(0, _idModelo, cantidad, -1, _statsModelo, 0);
			} else if (Emu.LIBROS_RUNICOS.contains(_idModelo)) {
				objeto = new Objeto(0, _idModelo, cantidad, -1, _statsModelo, 0);
			} else {
				objeto = new Objeto(0, _idModelo, cantidad, -1, generarStatsModeloDB(_statsModelo, maxStats),
						generarEfectoModelo(_statsModelo), 0);
			}
			return objeto;
		}

		Objeto crearObjPosDesdeModelo(int cantidad, int pos, boolean maxStats) {// solo para caramelos
			Objeto objeto;
			if (cantidad < 1) {
				cantidad = 1;
			}
			if (_tipo == 33 || _tipo == 42 || _tipo == 49 || _tipo == 69 || _tipo == 12) {
				objeto = new Objeto(0, _idModelo, cantidad, pos, _statsModelo, 0);
			} else if (_tipo == 85) {
				objeto = new PiedraDeAlma(0, cantidad, _idModelo, pos, _statsModelo);
			} else if (Emu.ARMAS_ENCARNACIONES.contains(_idModelo)) {
				objeto = new Objeto(0, _idModelo, cantidad, pos, _statsModelo, 0);
			} else if (Emu.LIBROS_RUNICOS.contains(_idModelo)) {
				objeto = new Objeto(0, _idModelo, cantidad, pos, _statsModelo, 0);
			} else {
				objeto = new Objeto(0, _idModelo, cantidad, pos, generarStatsModeloDB(_statsModelo, maxStats),
						generarEfectoModelo(_statsModelo), 0);
			}
			return objeto;
		}

		public static String generarStatsModeloDB(String statsModelo, boolean maxStats) {
			StringBuilder statsObjeto = new StringBuilder("");
			if (statsModelo.equals("") || statsModelo == null) {
				return "";
			}
			String[] splitted = statsModelo.split(",");
			boolean primero = false;
			for (String s : splitted) {
				String[] stats = s.split("#");
				int statID = -1;
				try {
					statID = Integer.parseInt(stats[0], 16);
				} catch (Exception e) {
					continue;
				}
				for (int a : CentroInfo.ID_EFECTOS_ARMAS) {
					if (a == statID) {
						continue;
					}
				}
				if (primero) {
					statsObjeto.append(",");
				}
				String rango = "";
				int valor = 1;
				if (statID == 811 || statID == 800) {
					statsObjeto.append(stats[0] + "#0#0#" + stats[3]);
					primero = true;
					continue;
				}
				try {
					rango = stats[4];
					if (maxStats) {
						try {
							int min = Integer.parseInt(stats[1], 16);
							int max = Integer.parseInt(stats[2], 16);
							valor = min;
							if (max != 0) {
								valor = max;
							}
						} catch (Exception e) {
							valor = Formulas.getRandomValor(rango);
						}
					} else {
						valor = Formulas.getRandomValor(rango);
					}
				} catch (Exception e) {
				}
				statsObjeto.append(stats[0] + "#" + Integer.toHexString(valor) + "#0#"
						+ Integer.toHexString(Integer.parseInt(stats[3], 16)) + "#0d0+" + valor);
				primero = true;
			}
			return statsObjeto.toString();
		}

		private ArrayList<EfectoHechizo> generarEfectoModelo(String statsModelo) {
			ArrayList<EfectoHechizo> efectos = new ArrayList<>();
			if (statsModelo.equals("") || statsModelo == null) {
				return efectos;
			}
			String[] splitted = statsModelo.split(",");
			for (String s : splitted) {
				String[] stats = s.split("#");
				int statID = -1;
				try {
					statID = Integer.parseInt(stats[0], 16);
				} catch (Exception e) {
					continue;
				}
				for (int a : CentroInfo.ID_EFECTOS_ARMAS) {
					if (a == statID) {
						int id = statID;
						String min = stats[1];
						String max = stats[2];
						String jet = stats[4];
						String args = min + ";" + max + ";-1;-1;0;" + jet;
						efectos.add(new EfectoHechizo(id, args, 0, -1));
					}
				}
			}
			return efectos;
		}

		String stringDeStatsParaTienda() {// TODO:
			String str = "";
			str += _idModelo + ";";
			String[] stra = _statsModelo.split(",");
			StringBuilder xtra = new StringBuilder();
			boolean prim = false;
			for (String strx : stra) {
				if (strx.toString().contains("32c#")) {// resistencias armas
					String news = "32c#" + strx.split("#")[1] + "#" + strx.split("#")[4] + "#" + strx.split("#")[4]
							+ "#" + strx.split("#")[4];
					if (prim) {
						xtra.append(",");
					}
					xtra.append(news);
				} else {
					if (prim) {
						xtra.append(",");
					}
					xtra.append(strx);
				}
				prim = true;
			}
			str += xtra.toString();
			return str;
		}

		public void aplicarAccion(Personaje pj, Personaje objetivo, int objID, short celda) {
			for (Accion a : _accionesDeUso) {
				a.aplicar(pj, objetivo, objID, celda);
			}
		}

		public int getPrecioPromedio() {
			return _precioMedio;
		}

		public long getVendidos() {
			return _vendidos;
		}

		synchronized void nuevoPrecio(int cantidad, int precio) {
			long viejaVenta = _vendidos;
			_vendidos += cantidad;
			_precioMedio = (int) ((_precioMedio * viejaVenta + precio) / _vendidos);
		}
	}

	public static Stats generateNewStatsFromTemplate(String statsTemplate, boolean useMax) {
		Stats itemStats = new Stats(false, null);
		if (statsTemplate.equals("") || statsTemplate == null) {
			return itemStats;
		}
		String[] splitted = statsTemplate.split(",");
		for (String s : splitted) {
			String[] stats = s.split("#");
			int statID = 0;
			try {
				statID = Integer.parseInt(stats[0], 16);
			} catch (Exception e) {
				continue;
			}
			boolean follow = true;
			for (int a : CentroInfo.ID_EFECTOS_ARMAS) {
				if (a == statID) {
					follow = false;
				}
			}
			if (!follow) {
				continue;
			}
			String jet = "";
			int value = 1;
			try {
				jet = stats[4];
				value = Formulas.getRandomValor(jet);
				if (useMax) {
					try {
						int min = Integer.parseInt(stats[1], 16);
						int max = Integer.parseInt(stats[2], 16);
						value = min;
						if (max != 0) {
							value = max;
						}
					} catch (Exception e) {
						value = Formulas.getRandomValor(jet);
					}
				}
			} catch (Exception e) {
			}
			itemStats.addUnStat(statID, value);
		}
		return itemStats;
	}

	public static int getStatBase(Objeto obj, String statsModif) {
		for (Entry<Integer, Integer> entry : obj.getStats().getStatsComoMap().entrySet()) {
			if (Integer.toHexString(entry.getKey()).compareTo(statsModif) > 0)// Effets inutiles
			{
				continue;
			} else if (Integer.toHexString(entry.getKey()).compareTo(statsModif) == 0)// L'effet existe bien !
			{
				int JetActual = entry.getValue();
				return JetActual;
			}
		}
		return 0;
	}

	public static int getStatBaseMax(ObjetoModelo objMod, String statsModif) {
		String[] splitted = objMod.getStringStatsObj().split(",");
		for (String s : splitted) {
			String[] stats = s.split("#");
			if (stats[0].compareTo(statsModif) > 0) {
				continue;
			} else if (stats[0].compareTo(statsModif) == 0) {
				int max = Integer.parseInt(stats[2], 16);
				if (max == 0) {
					max = Integer.parseInt(stats[1], 16);
				}
				return max;
			}
		}
		return 0;
	}
}
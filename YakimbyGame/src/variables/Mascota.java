package variables;

import java.util.ArrayList;
import java.util.Calendar;

import estaticos.GestorSalida;
import estaticos.MundoDofus;
import variables.Objeto.ObjetoModelo;

public class Mascota {
	private int _objID;
	private int _nroComidas;
	private int _pdv;
	private String _stringStats;
	private String _fechaUltComida;
	private int _año;
	private int _mes;
	private int _dia;
	private int _hora;
	private int _minuto;
	private MascotaModelo _mascModelo;
	private int _idModelo;
	private int _ultimaComida;
	private int _obeso;
	private int _delgado;

	public Mascota(int objetoID, int PDV, String stats, int nrComidas, int año, int mes, int dia, int hora, int minuto,
			int ultComida, int obeso, int delgado, int idModelo) {
		_idModelo = idModelo;
		_objID = objetoID;
		_nroComidas = nrComidas;
		_pdv = PDV;
		_stringStats = stats;
		_año = año;
		_mes = mes;
		_dia = dia;
		_hora = hora;
		_minuto = minuto;
		_ultimaComida = ultComida;
		_obeso = obeso;
		_delgado = delgado;
		_fechaUltComida = "328#" + Integer.toHexString(_año) + "#" + Integer.toHexString((_mes - 1) * 100 + _dia) + "#"
				+ Integer.toHexString(_hora * 100 + _minuto);
		_mascModelo = MundoDofus.getMascotaModelo(_idModelo);
	}

	public int getUltimaComida() {
		return _ultimaComida;
	}

	public int getIDModelo() {
		return _idModelo;
	}

	public int getID() {
		return _objID;
	}

	public int getAño() {
		return _año;
	}

	public int getMes() {
		return _mes;
	}

	public int getDia() {
		return _dia;
	}

	public int getHora() {
		return _hora;
	}

	public int getMinuto() {
		return _minuto;
	}

	public int getNroComidas() {
		return _nroComidas;
	}

	public int getPDV() {
		return _pdv;
	}

	public void setPDV(int pdv) {
		_pdv = pdv;
	}

	public int getMinutosDia() {
		int total = 0;
		total += ((_mes - 1) * 43200);
		total += ((_dia - 1) * 1440);
		total += (_hora * 60);
		total += _minuto;
		return total;
	}

	private void setFecha(int Año, int Mes, int Dia, int Hora, int Minuto) {
		_año = Año;
		_mes = Mes;
		_dia = Dia;
		_hora = Hora;
		_minuto = Minuto;
	}

	public String getStringStats() {
		return _stringStats;
	}

	public void setStringStats(String stats) {
		_stringStats = stats;
	}

	public boolean esComestible(int idModComida) {
		if (idModComida == 2239) {
			return true;
		}
		/*
		 * ArrayList<Comida> comidas = _mascModelo._comidas; for (Comida comi : comidas)
		 * { if (comi.getIDComida() < 0) { ObjetoModelo objMod =
		 * MundoDofus.getObjModelo(idModComida); if (Math.abs(comi.getIDComida()) ==
		 * objMod.getTipo()) { return true; } } else { if (comi.getIDComida() ==
		 * idModComida) { return true; } } }
		 */
		return false;
	}

	public boolean getDelgado() {
		return _delgado == 7;
	}

	public boolean getObeso() {
		return _obeso == 7;
	}

	public void setCorpulencia(int numero) {
		if (numero == 0) {
			_obeso = 0;
			_delgado = 0;
		} else if (numero == 1) {
			_obeso = 7;
			_delgado = 0;
		} else if (numero == 2) {
			_obeso = 0;
			_delgado = 7;
		}
	}

	public void comerComida(int idModComida) {
		_nroComidas++;
		_ultimaComida = idModComida;
		Comida comida = null;
		if (idModComida != 2239) { // SI NO ES ESTA COMIDA CARGA DE LA DB
			for (Comida comi : _mascModelo._comidas) { // Comidas de la mascotaid
				if (comi.getIDComida() < 0) {
					ObjetoModelo objMod = MundoDofus.getObjModelo(idModComida);
					if (Math.abs(comi.getIDComida()) == objMod.getTipo()) {
						comida = comi;
						break;
					}
				} else {
					if (comi.getIDComida() == idModComida) {
						comida = comi;
						break;
					}
				}
			}
			if (comida == null) { // Si pasa la comidaid de la db es valida
				return;
			}
		}
		Calendar calendar = Calendar.getInstance();
		int año = calendar.get(Calendar.YEAR);
		int mes = calendar.get(2) + 1;
		int dia = calendar.get(5);
		int hora = calendar.get(11);
		int minuto = calendar.get(12);

		_fechaUltComida = "328#" + Integer.toHexString(año) + "#" + Integer.toHexString((mes - 1) * 100 + dia) + "#"
				+ Integer.toHexString(hora * 100 + minuto);
		String ultimaco = "327#0#0#" + Integer.toHexString(_ultimaComida);
		String newstats = "";
		boolean primeros = false;
		for (String str : _stringStats.split(",")) {
			if (primeros) {
				newstats += ",";
			}
			String elstat = str.split("#")[0];
			if (elstat.equals("327")) {
				newstats += ultimaco;
			} else if (elstat.equals("328")) {
				newstats += _fechaUltComida;
			} else {
				newstats += str;
			}
			primeros = true;
		}
		_stringStats = newstats;
		return;
	}

	public boolean horaComer(Personaje _perso) {
		Calendar actual = Calendar.getInstance();
		if (_año != Calendar.YEAR) {
			_año = Calendar.YEAR;
		}
		int bmes = actual.get(Calendar.MONTH) + 1;
		int bdia = actual.get(Calendar.DAY_OF_MONTH);
		int bhora = actual.get(Calendar.HOUR_OF_DAY);
		int bminuto = actual.get(Calendar.MINUTE);
		long total = 0;
		total += ((bmes - 1) * 43200);
		total += ((bdia - 1) * 1440);
		total += (bhora * 60);
		total += bminuto;
		long resta = total - getMinutosDia();
		if (resta >= 2) {
			setFecha(actual.get(Calendar.YEAR), bmes, bdia, bhora, bminuto);
			_fechaUltComida = "328#" + Integer.toHexString(_año) + "#" + Integer.toHexString((_mes - 1) * 100 + _dia)
					+ "#" + Integer.toHexString(_hora * 100 + _minuto);
			setCorpulencia(0);
			return true;
		} else {
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso,
					"Podrï¿½s alimentar a tu mascota en " + (2 - resta) + " minutos");
		}
		return false;
	}

	public String analizarStatsMascota() {
		String stats = "";
		stats += "320#0#0#" + Integer.toHexString(getPDV()) + "," + _fechaUltComida + ",326#0#" + _obeso + "#"
				+ _delgado;
		if (_ultimaComida != -1) {
			stats += ",327#0#0#" + Integer.toHexString(_ultimaComida);
		}
		return stats;
	}

	private static class Comida { // id;cant;stats
		private int _idComida;

		private Comida(int idModelo, int cant, int idStat) {
			_idComida = idModelo;
		}

		public int getIDComida() {
			return _idComida;
		}
	}

	public static class MascotaModelo {
		private int _maxStats;
		private ArrayList<Comida> _comidas = new ArrayList<>();

		public MascotaModelo(int maxStas, String comidas) {
			_maxStats = maxStas;
			if (!comidas.isEmpty()) {
				for (String comida : comidas.split("\\|")) {
					if (comida.isEmpty()) {
						continue;
					}
					String[] str = comida.split(";");
					try {
						Comida comi = new Comida(Integer.parseInt(str[0]), Integer.parseInt(str[1]),
								Integer.parseInt(str[2]));
						_comidas.add(comi);
					} catch (Exception e) {
						continue;
					}
				}
			}
		}

		public int getMaxStats() {
			return _maxStats;
		}
	}
}

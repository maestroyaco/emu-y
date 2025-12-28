package estaticos;

import java.util.ArrayList;

import com.singularsys.jep.Jep;

import variables.Personaje;
import variables.Personaje.Stats;

public class CondicionJugador {
	public static boolean validaCondiciones(Personaje perso, String condiciones) {
		if ((condiciones == null) || (condiciones.equals("")) || (condiciones.equals(" "))
				|| (condiciones.equalsIgnoreCase("EVENTO")) || condiciones.contains("Pg") || condiciones.contains("CO")
				|| condiciones.contains("XO") || condiciones.contains("SO")) {
			return true;
		}
		if (condiciones.contains("BI") || condiciones.contains("Pg")) { // son los dones
			return false;
		}
		if (condiciones.contains("Pj") || condiciones.contains("PJ")) {
			return havePj(condiciones, perso);
		}
		Jep jep = new Jep();
		if (condiciones.contains("PO")) {
			condiciones = tieneObjetoModelo(condiciones, perso);
		}
		condiciones = condiciones.replace("&", "&&").replace("=", "==").replace("|", "||").replace("!", "!=");
		try {
			Stats totalStas = perso.getTotalStats();
			jep.addVariable("CI", totalStas.getEfecto(CentroInfo.STATS_ADD_INTELIGENCIA));
			jep.addVariable("CV", totalStas.getEfecto(CentroInfo.STATS_ADD_VITALIDAD));
			jep.addVariable("CA", totalStas.getEfecto(CentroInfo.STATS_ADD_AGILIDAD));
			jep.addVariable("CW", totalStas.getEfecto(CentroInfo.STATS_ADD_SABIDURIA));
			jep.addVariable("CC", totalStas.getEfecto(CentroInfo.STATS_ADD_SUERTE));
			jep.addVariable("CS", totalStas.getEfecto(CentroInfo.STATS_ADD_FUERZA));
			jep.addVariable("Ci", perso.getBaseStats().getEfecto(CentroInfo.STATS_ADD_INTELIGENCIA));
			jep.addVariable("Cs", perso.getBaseStats().getEfecto(CentroInfo.STATS_ADD_FUERZA));
			jep.addVariable("Cv", perso.getBaseStats().getEfecto(CentroInfo.STATS_ADD_VITALIDAD));
			jep.addVariable("Ca", perso.getBaseStats().getEfecto(CentroInfo.STATS_ADD_AGILIDAD));
			jep.addVariable("Cw", perso.getBaseStats().getEfecto(CentroInfo.STATS_ADD_SABIDURIA));
			jep.addVariable("Cc", perso.getBaseStats().getEfecto(CentroInfo.STATS_ADD_SUERTE));
			jep.addVariable("CM", perso.getBaseStats().getEfecto(CentroInfo.STATS_ADD_PM));
			jep.addVariable("Ps", perso.getAlineacion());
			jep.addVariable("Pa", perso.getNivelAlineacion());
			jep.addVariable("PP", perso.getNivelAlineacion());
			jep.addVariable("PL", perso.getNivel());
			jep.addVariable("Pe", perso.getPeleaNR());
			jep.addVariable("RS", perso._resets);
			jep.addVariable("Vd", perso.getPDV());
			jep.addVariable("SI", perso.getMapa().getID());
			jep.addVariable("VD", perso.getPDVMAX());
			jep.addVariable("PK", perso.getKamas());
			jep.addVariable("PG", perso.getClase(true));
			jep.addVariable("PS", perso.getSexo());
			jep.addVariable("PZ", true);
			jep.addVariable("esCasado", perso.getEsposo());
			jep.addVariable("siKamas", perso.getKamas());
			jep.addVariable("MiS", perso.getID());
			jep.parse(condiciones);
			Object resultado = jep.evaluate();
			boolean ok = false;
			if (resultado != null) {
				ok = Boolean.valueOf(resultado.toString());
			}
			return ok;
		} catch (Exception e) {
			Emu.creaLogs(e);
		}
		return true;
	}

	private static boolean havePj(String c, Personaje p) {
		if (c.equalsIgnoreCase("")) {
			return false;
		}
		c = c.substring(3).replace("PJ", "");
		for (String s : c.split("\\|\\|")) {
			int id = 0;
			int lvl = 0;
			try {
				id = Integer.parseInt(s.split(",")[0]);
			} catch (Exception e) {
				continue;
			}
			try {
				lvl = Integer.parseInt(s.split(",")[1]);
			} catch (Exception e) {
				continue;
			}
			if (id == 0 && lvl == 0) {
				continue;
			}
			if (p.getOficioPorID(id) == null) {
				continue;
			} else if (p.getOficioPorID(id).getNivel() >= lvl) {
				return true;
			}
		}
		return false;
	}

	private static String tieneObjetoModelo(String condiciones, Personaje perso) {
		try {
			String[] str = condiciones.replaceAll("[ ()]", "").split("[|&]");
			ArrayList<Integer> valores = new ArrayList<>(str.length);
			for (String condicion : str) {
				if (!condicion.contains("PO")) {
					continue;
				}
				int modeloid = -1;
				try {
					modeloid = Integer.parseInt(condicion.split("[=]")[1]);
				} catch (Exception e) {
					continue;
				}
				if (perso.tieneObjModeloNoEquip(modeloid, 1)) {
					valores.add(Integer.parseInt(condicion.split("[=]")[1]));
				} else {
					valores.add(-1);
				}
			}
			for (int valor : valores) {
				condiciones = condiciones.replaceFirst("PO", valor + "");
			}
		} catch (Exception e) {
			Emu.creaLogs(e);
		}
		return condiciones;
	}
}
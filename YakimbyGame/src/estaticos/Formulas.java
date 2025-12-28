package estaticos;

import java.security.SecureRandom;
import java.util.ArrayList;

import variables.Dragopavo;
import variables.EfectoHechizo;
import variables.Gremio;
import variables.Gremio.MiembroGremio;
import variables.Mapa;
import variables.Pelea;
import variables.Pelea.Luchador;
import variables.Personaje;
import variables.Personaje.Stats;
import variables.Recaudador;

public class Formulas {
	static float ADIC_PJ = 1.3f;// el daÑo que hacen las habilidades
	static float ADIC_MOB = 1.4f;
	static float ADIC_CAC = 0.7f; // cuerpo a cuerpo el daÑo
	public static float PROSP_REQ = 1;
	private static float TOLER_DAñO = 0.1f;
	private static final SecureRandom RANDOM = new SecureRandom();
	private static int DAñO_MARGEN = 500;

	public static int getRandomValor(int i1, int i2) {
		try {
			if (i1 < 0) {
				return i2;
			}
			if (i2 < 0) {
				return i1;
			}
			if (i1 > i2) {
				return RANDOM.nextInt(i1 - i2 + 1) + i2;
			}
			return RANDOM.nextInt(i2 - i1 + 1) + i1;
		} catch (Exception e) {
			return 0;
		}
	}

	public static int getRandomValor(String rango) {// 1d4+5 2d5+10 0d0+157
		try {
			int num = 0;
			int veces = Integer.parseInt(rango.split("d")[0]); // 0
			int margen = Integer.parseInt(rango.split("d")[1].split("\\+")[0]); // 0
			int adicional = Integer.parseInt(rango.split("d")[1].split("\\+")[1]); // 3
			if (margen == 0 && adicional == 0) {
				num = getRandomValor(0, veces);
			} else {
				for (int a = 0; a < veces; a++) {
					num += getRandomValor(1, margen);
				}
			}
			num += adicional;
			return num;
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	static int getMaxValor(String rango) {
		try {
			int num = 0;
			int veces = Integer.parseInt(rango.split("d")[0]);
			int margen = Integer.parseInt(rango.split("d")[1].split("\\+")[0]);
			int adicional = Integer.parseInt(rango.split("d")[1].split("\\+")[1]);
			for (int a = 0; a < veces; a++) {
				num += margen;
			}
			num += adicional;
			return num;
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	public static int getMinJet(String jet) {
		int num = 0;
		try {
			int des = Integer.parseInt(jet.split("d")[0]);
			int add = Integer.parseInt(jet.split("d")[1].split("\\+")[1]);
			num = des + add;
			return num;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public static int getMaxJet(String jet) {
		int num = 0;
		try {
			int des = Integer.parseInt(jet.split("d")[0]);
			int faces = Integer.parseInt(jet.split("d")[1].split("\\+")[0]);
			int add = Integer.parseInt(jet.split("d")[1].split("\\+")[1]);
			for (int a = 0; a < des; a++) {
				num += faces;
			}
			num += add;
			return num;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public static int calculFinalCura(Luchador curador, int rango, boolean esCaC) {
		return calculFinalCura(curador, rango, esCaC, -1);
	}

	public static int calculFinalCura(Luchador curador, int rango, boolean esCaC, int hechizoid) {
		int inteligencia = curador.getTotalStatsConBuff().getEfecto(126);
		int curas = curador.getTotalStatsConBuff().getEfecto(178);
		if (inteligencia < 0) {
			inteligencia = 0;
		}
		float adic = 100;
		if (esCaC) {
			adic = 105;
		}
		if (curador.getPersonaje() != null) {
			if (rango > 200) {
				rango = 200;
			}
		}
		int vidacurada = (int) (rango * ((100.00 + inteligencia) / adic) + curas / 2);
		if (vidacurada > 2300 && hechizoid == 140) {
			vidacurada = Formulas.getRandomValor(1900, 2000);
		}
		if (vidacurada > 600 && hechizoid == 450) {
			vidacurada = 400;
		}
		if (vidacurada > 1000) {
			if (curador.esInvocacion()) {
				if (curador.getInvocador().getPersonaje() != null) {
					vidacurada = Formulas.getRandomValor(700, 1000);
				}
			}
		}
		return vidacurada;
	}

	@SuppressWarnings("unused")
	private static float daÑoPorEspalda(Pelea pelea, Luchador lanzador, Luchador objetivo, float daÑo) {
		if (lanzador.getDireccion() == objetivo.getDireccion()) {
			Mapa map = pelea.getMapaCopia();
			if (Camino.getEnemigoAlrededor(lanzador.getCeldaPelea().getID(), map, pelea) != null) {
				daÑo += (daÑo * 20 / 100);
				// GestorSalida.ENVIAR_cS_EMOTE_EN_PELEA(pelea, 7, objetivo.getID(), 61);
				// GestorSalida.ENVIAR_cS_EMOTE_EN_PELEA(pelea, 7, lanzador.getID(), 68);
			}
		}
		return daÑo;
	}

	@SuppressWarnings("unused")
	private static float reduccionHechizos(int hechizoid, float daÑo) {
		switch (hechizoid) {
		case 157:// espada celeste
			daÑo -= (daÑo * 35 / 100);
			break;
		case 150:// corte
			daÑo -= (daÑo * 5 / 100);
			break;
		case 141:// presion
			daÑo -= (daÑo * 10 / 100);
			break;
		case 158:// concentracion
			daÑo -= (daÑo * 25 / 100);
			break;
		case 160:// espada de yopuka
			daÑo += (daÑo * 20 / 100);
			break;
		case 146:// espada del destino
			daÑo += (daÑo * 50 / 100);
			break;
		case 159:// ira de yopuka
			daÑo -= (daÑo * 35 / 100);
			break;
		case 43:// lanzamiento de pala
			// daÑo += (daÑo * 20 / 100);
			break;
		case 48:// terraplenado
			daÑo += (daÑo * 20 / 100);
			break;
		case 36:// golpe crujidor
			daÑo += (daÑo * 30 / 100);
			break;
		case 21:// garra espectral
			daÑo -= (daÑo * 10 / 100);
			break;
		case 24:// cuervo
			daÑo -= (daÑo * 10 / 100);
			break;
		case 167:// flecha expiacion
			daÑo -= (daÑo * 35 / 100);
			break;
		case 171:// flecha castigadora
			daÑo += (daÑo * 25 / 100);
			break;
		case 179:// flecha explosiva
			daÑo -= (daÑo * 15 / 100);
			break;
		case 178:// flecha absorbente
			daÑo -= (daÑo * 30 / 100);
			break;
		case 110:// garra juguetona
			daÑo -= (daÑo * 20 / 100);
			break;
		case 76:// ataque mortal
			daÑo += (daÑo * 10 / 100);
			break;
		case 61:// engaÑo
			daÑo += (daÑo * 20 / 100);
			break;
		case 70:// estafa
			daÑo += (daÑo * 5 / 100);
			break;
		case 66:// veneno insidioso
			daÑo += (daÑo * 45 / 100);
			break;
		case 83:// aguja
			daÑo += (daÑo * 15 / 100);
			break;
		case 92:// rayo oscuro
			daÑo += (daÑo * 10 / 100);
			break;
		case 184:// fuego montes
			daÑo += (daÑo * 20 / 100);
			break;
		case 351:// glifo flam�gero
			daÑo -= (daÑo * 30 / 100);
			break;
		case 519:// ataque natural
			daÑo -= (daÑo * 25 / 100);
			break;
		case 9:// ataque nuboso
			daÑo -= (daÑo * 25 / 100);
			break;
		case 8:// pu�alada
			daÑo -= (daÑo * 45 / 100);
			break;
		case 185:// hierba loca
			daÑo -= (daÑo * 50 / 100);
			break;
		case 195:// l�grima
			daÑo -= (daÑo * 30 / 100);
			break;
		case 95:// Reloj de Pared
			daÑo -= (daÑo * 15 / 100);
			break;
		case 175:// flecha destructora
			daÑo -= (daÑo * 10 / 100);
			break;
		case 446:// Castigo
			daÑo += (daÑo * 30 / 100);
			break;
		case 156:// Tempestad de Potencia
			daÑo -= (daÑo * 30 / 100);
			break;
		case 161:// flecha m�gica
			daÑo -= (daÑo * 30 / 100);
			break;
		case 96:
			daÑo += (daÑo * 20 / 100);
			break;
		case 49:// pala fantasmal
			daÑo -= (daÑo * 20 / 100);
			break;
		}
		return daÑo;
	}

	public static int calculFinalDommage(Pelea fight, Luchador lanzador, Luchador objetivo, int statID, int daÑo,
			boolean isHeal, boolean isCaC, int spellid, boolean esVeneno, boolean glifo) {
		Stats totalLanzador = lanzador.getTotalStatsConBuff();
		Stats totalObjetivo = objetivo.getTotalStatsConBuff();
		float adicPj = ADIC_PJ;
		float dominioArma = 1;
		float num = 0;
		float masDaÑos = totalLanzador.getEfecto(112);
		float porcDaÑos = totalLanzador.getEfecto(138);
		int multiplicaDaÑos = totalLanzador.getEfecto(114);
		float resMasT = 0;
		float resPorcT = 0;
		int statC = 0;
		if (multiplicaDaÑos < 1) {
			multiplicaDaÑos = 1;
		}
		switch (statID) {
		case 0:// neutral
			statC = totalLanzador.getEfecto(118);// fuerza
			resMasT = totalObjetivo.getEfecto(241);// add r neutral
			resPorcT = totalObjetivo.getEfecto(214);// res neutral
			if (objetivo.getPersonaje() != null) {
				resPorcT += totalObjetivo.getEfecto(254);
				resMasT += totalObjetivo.getEfecto(264);
			}
			masDaÑos += totalLanzador.getEfecto(142);
			masDaÑos += totalLanzador.getEfecto(CentroInfo.MAS_DAÑOS_DE_NEUTRAL);
			resMasT += totalObjetivo.getEfecto(184);// reduccion fisica
			break;
		case 1:// tierra
			statC = totalLanzador.getEfecto(118);
			resMasT = totalObjetivo.getEfecto(242);
			resPorcT = totalObjetivo.getEfecto(210);
			if (objetivo.getPersonaje() != null) {
				resPorcT += totalObjetivo.getEfecto(250);
				resMasT += totalObjetivo.getEfecto(260);
			}
			masDaÑos += totalLanzador.getEfecto(142);
			masDaÑos += totalLanzador.getEfecto(CentroInfo.MAS_DAÑOS_DE_TIERRA);
			resMasT += totalObjetivo.getEfecto(184);// reduccion fisica
			break;
		case 2:// agua
			statC = totalLanzador.getEfecto(123);
			resMasT = totalObjetivo.getEfecto(243);
			resPorcT = totalObjetivo.getEfecto(211);
			if (objetivo.getPersonaje() != null) {
				resPorcT += totalObjetivo.getEfecto(251);
				resMasT += totalObjetivo.getEfecto(261);
			}
			masDaÑos += totalLanzador.getEfecto(CentroInfo.MAS_DAÑOS_DE_AGUA);
			resMasT += totalObjetivo.getEfecto(183);// reduccion magica
			break;
		case 3:// fuego
			statC = totalLanzador.getEfecto(126);
			resMasT = totalObjetivo.getEfecto(240);
			resPorcT = totalObjetivo.getEfecto(213);
			if (objetivo.getPersonaje() != null) {
				resPorcT += totalObjetivo.getEfecto(253);
				resMasT += totalObjetivo.getEfecto(263);
			}
			masDaÑos += totalLanzador.getEfecto(CentroInfo.MAS_DAÑOS_DE_FUEGO);
			resMasT += totalObjetivo.getEfecto(183);// reduccion magica
			break;
		case 4: //aire
			statC = totalLanzador.getEfecto(119);
			resMasT = totalObjetivo.getEfecto(244);
			resPorcT = totalObjetivo.getEfecto(212);
			if (objetivo.getPersonaje() != null) {
				resPorcT += totalObjetivo.getEfecto(252);
				resMasT += totalObjetivo.getEfecto(262);
			}
			masDaÑos += totalLanzador.getEfecto(CentroInfo.MAS_DAÑOS_DE_AIRE);
			resMasT += totalObjetivo.getEfecto(183);// reduccion magica
			break;
		}
		if ((objetivo.getPersonaje() != null) && (resPorcT > 75.0F)) {
			resPorcT = 75.0F;
		}
		if (statC < 0) {
			statC = 0;
		}
		Personaje perso = lanzador.getPersonaje();
		if (perso != null && isCaC) {
			adicPj = ADIC_CAC;
			int armaTipo = 0;
			try {
				armaTipo = perso.getObjPosicion(1).getModelo().getTipo();
			} catch (Exception localException) {
			}
			float i = 0;
			int porc = 90;
			int clase = perso.getClase(true);
			switch (armaTipo) {
			case 2:
				if (lanzador.tieneBuffHechizoID(392)) {
					i = lanzador.getBuffValorHechizoID(392);
				}
				if (clase == 4) {
					porc = 95;
				} else {
					if (clase != 9) {
						break;
					}
					porc = 100;
				}
				break;
			case 3:
				if (lanzador.tieneBuffHechizoID(394)) {
					i = lanzador.getBuffValorHechizoID(394);
				}
				if ((clase == 1) || (clase == 5)) {
					porc = 95;
				} else {
					if (clase != 7) {
						break;
					}
					porc = 100;
				}
				break;
			case 4:
				if (lanzador.tieneBuffHechizoID(390)) {
					i = lanzador.getBuffValorHechizoID(390);
				}
				if ((clase == 7) || (clase == 2) || (clase == 12)) {
					porc = 95;
				} else {
					if ((clase != 1) && (clase != 10)) {
						break;
					}
					porc = 100;
				}
				break;
			case 5:
				if (lanzador.tieneBuffHechizoID(395)) {
					i = lanzador.getBuffValorHechizoID(395);
				}
				if ((clase == 9) || (clase == 6)) {
					porc = 95;
				} else {
					if (clase != 4) {
						break;
					}
					porc = 100;
				}
				break;
			case 6:
				if (lanzador.tieneBuffHechizoID(391)) {
					i = lanzador.getBuffValorHechizoID(391);
				}
				if ((clase != 8) && (clase != 6)) {
					break;
				}
				porc = 100;
				break;
			case 7:
				if (lanzador.tieneBuffHechizoID(393)) {
					i = lanzador.getBuffValorHechizoID(393);
				}
				if ((clase == 3) || (clase == 8) || (clase == 10)) {
					porc = 95;
				} else {
					if ((clase != 2) && (clase != 5)) {
						break;
					}
					porc = 100;
				}
				break;
			case 8:
				if (lanzador.tieneBuffHechizoID(396)) {
					i = lanzador.getBuffValorHechizoID(396);
				}
				if (clase != 3) {
					break;
				}
				porc = 100;
				break;
			case 19:
				if (lanzador.tieneBuffHechizoID(397)) {
					i = lanzador.getBuffValorHechizoID(397);
				}
				if (clase != 12) {
					break;
				}
				porc = 100;
				break;
			}
			dominioArma = (porc + i) / 100.0F;
		}
		num = daÑo;
		if (statID >= 10) {
			statC = 0;
			masDaÑos = 0;
			porcDaÑos = 0;
			multiplicaDaÑos = 1;
		}
		if (isCaC) {
			multiplicaDaÑos = 1;
			porcDaÑos = porcDaÑos / 2;
			num =(num*((100+dominioArma)/100f));
		}
		if (lanzador.getMob() != null && (lanzador.getMob().getModelo().getID() == 116)) {
			num = lanzador.getPDVConBuff();
		} else {
			if (!isHeal) {
				num = (num*(100+statC+porcDaÑos)/100f);
				num += masDaÑos;
			}
				//num = (((100 + masDaÑos) / 100f * ((daÑo * ((DA�O_MARGEN + statC) / DA�O_MARGEN)) * dominioArma))* multiplicaDaÑos) + porcDaÑos;
 else {
				return calculFinalCura(lanzador, daÑo, isCaC, spellid);
			}
		}
		boolean usareen = true;
		/*
		 * switch (fight.getTipoPelea()) { case 0: case 1: case 4: case 5: case 6:
		 * usareen = false; break; }
		 */
		// if (usareen)
		// num = daÑoPorEspalda(fight, lanzador, objetivo, num);
		if (lanzador.getPersonaje() != null) {
			if (lanzador.getPersonaje().getClase(true) == 3 || lanzador.getPersonaje().getClase(true) == 7 || lanzador.getPersonaje().getClase(true) == 11) { // anu y
																												// aniripsa , sacro
				float extrad = (num * 15) / 100;
				num += extrad;
			}
		}
		if (num < 1) {
			return 0;
		}
		int renvoie = objetivo.getTotalStatsConBuff().getEfecto(CentroInfo.STATS_REENVIA_DAÑOS);
		if (renvoie > 0 && !isHeal && !esVeneno && !lanzador.tieneBuff(105) && !glifo && usareen) {
			if (renvoie > num) {
				renvoie = (int) num;
			}
			// num -= renvoie;
			if (renvoie > 0) {
				GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 107, "-1", objetivo.getID() + "," + renvoie);
			}
			if (renvoie > lanzador.getPDVConBuff()) {
				renvoie = lanzador.getPDVConBuff();
			}
			if (num < 1) {
				num = 0;
			}
			if (renvoie > 0) {
				lanzador.restarPDV(renvoie);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 100, lanzador.getID() + "",
						lanzador.getID() + ",-" + renvoie);
			}
			if (lanzador.getPDVConBuff() <= 0) {
				fight.agregarAMuertos(lanzador, lanzador, "reenvio");
			}
		}
		if (!isHeal)
		 {
			num -= resMasT;// reducciones
		}
		if (!isHeal) {
			int reduc = (int) (num * resPorcT / 100);
			num -= reduc;
		}
		if (!esVeneno) {
			int armadura = getResisArmadura(objetivo, statID);
			if (armadura > 0 && !isHeal) {
				GestorSalida.ENVIAR_GA_ACCION_PELEA(fight, 7, 105, lanzador.getID() + "",
						objetivo.getID() + "," + armadura);
				num -= armadura;
			}
		}
		if (num < 1) {
			return 0;
		}
		if (lanzador.getMob() != null) {
			if (lanzador.getMob().getModelo().getID() == 116) {
				return (int) num;
			}
			float calculCoef = 5.5F * lanzador.getNivel() / (200 + lanzador.getNivel());
			if (calculCoef < 1) {
				calculCoef = 1;
			}
			if (calculCoef > 5) {
				calculCoef = 5;
			}
			if (lanzador.esInvocacion()) {
				return (int) (num * calculCoef * (ADIC_MOB - 0.5));
			} else {
				return (int) (num * calculCoef * ADIC_MOB);
			}
		}
		num = num * adicPj;
		if (isCaC) {
			if (perso != null) {
				int clase = perso.buffClase;
				if (clase == 1) {
					num += (num * 20 / 100);
				}
			}
		} else {
			if (perso != null) {
				int clase = perso.buffClase;
				if (clase == 3) {
					num += (num * 25 / 100);
				}
			}
		}
		// num = reduccionHechizos(spellid, num);
		int pdvMax = objetivo.getPDVMax();
		pdvMax -= (int) (num * TOLER_DAñO);
		if (pdvMax < 0) {
			pdvMax = 0;
		}
		objetivo.setPDVMAX(pdvMax);
		return (int) num;
	}

	public static int calcularCosteZaap(Mapa map1, Mapa map2) {
		return 10
				* (Math.abs(map2.getCoordX() - map1.getCoordX()) + Math.abs(map2.getCoordY() - map1.getCoordY()) - 1);
	}

	private static int getResisArmadura(Luchador afectado, int statID) {
		int defensa = 0;
		float adic = 100f;
		Luchador lanzArmadura;
		if (statID >= 10) {
			statID -= 10;
		}
		for (EfectoHechizo EH : afectado.getBuffsPorEfectoID(265)) {// daÑos reducidos armaduras
			int div = 6;
			lanzArmadura = EH.getLanzador();
			switch (EH.getHechizoID()) {
			case 1:
				if (statID != 3) {
					continue;
				}
				div = 4;
				break;
			case 6:
				if ((statID != 1) && (statID != 0)) {
					continue;
				}
				div = 4;
				break;
			case 14:
				if (statID != 4) {
					continue;
				}
				div = 4;
				break;
			case 18:
				if (statID != 2) {
					continue;
				}
				div = 4;
				break;
			}
			Stats statsLanzArmadura = lanzArmadura.getTotalStatsConBuff();
			int inteligencia = statsLanzArmadura.getEfecto(126);
			int carac = 0;
			switch (statID) {
			case 4:// aire
				carac = statsLanzArmadura.getEfecto(119);
				break;
			case 3:// fuego
				carac = statsLanzArmadura.getEfecto(126);
				break;
			case 2:// agua
				carac = statsLanzArmadura.getEfecto(123);
				break;
			case 0:// neutral
			case 1:// tierra
				carac = statsLanzArmadura.getEfecto(118);
				break;
			}
			int valor = EH.getValor();
			int a = (int) (valor * ((100 + (inteligencia / div) + (carac / div)) / adic));
			defensa += a;
		}
		for (EfectoHechizo EH : afectado.getBuffsPorEfectoID(105)) {// daÑos reducidos tregua
			Stats statsLanzArmadura = EH.getLanzador().getTotalStatsConBuff();
			int inteligencia = statsLanzArmadura.getEfecto(126);
			int carac = 0;
			switch (statID) {
			case 4:
				carac = statsLanzArmadura.getEfecto(119);
				break;
			case 3:
				carac = statsLanzArmadura.getEfecto(126);
				break;
			case 2:
				carac = statsLanzArmadura.getEfecto(123);
				break;
			case 0:
			case 1:
				carac = statsLanzArmadura.getEfecto(118);
				break;
			}
			int valor = EH.getValor();
			int a = (int) (valor * ((100 + (inteligencia / 5f) + (carac / 5f)) / adic));
			defensa += a;
		}
		return defensa;
	}

	public static int getPuntosPerdidos(char z, int value, Luchador caster, Luchador target) {
		float esquiveC = (z == 'a') ? caster.getTotalStatsConBuff().getEfecto(160)
				: caster.getTotalStatsConBuff().getEfecto(161);
		float esquiveT = (z == 'a') ? target.getTotalStatsConBuff().getEfecto(160)
				: target.getTotalStatsConBuff().getEfecto(161);
		float ptsMax = (z == 'a') ? target.getTotalStatsSinBuff().getEfecto(111)
				: target.getTotalStatsSinBuff().getEfecto(128);
		int retrait = 0;
		for (int i = 0; i < value; ++i) {
			if (ptsMax == 0.0f && target.getMob() != null) {
				ptsMax = ((z == 'a') ? target.getMob().getPA() : target.getMob().getPM());
			}
			final float pts = (z == 'a') ? target.getPA() : target.getPM();
			final float ptsAct = pts - retrait;
			if (esquiveT <= 0.0f) {
				esquiveT = 1.0f;
			}
			if (esquiveC <= 0.0f) {
				esquiveC = 1.0f;
			}
			final float a = esquiveC / esquiveT;
			final float b = ptsAct / ptsMax;
			final float pourcentage = a * b * 50.0f;
			int chance = (int) Math.ceil(pourcentage);
			if (chance < 0) {
				chance = 0;
			}
			if (chance > 100) {
				chance = 100;
			}
			int jet = getRandomValor(0, 99);
			if (caster.getPersonaje() != null) {
				if (caster.getPersonaje().getClase(true) == 5) { // xelor
					jet = getRandomValor(0, 79);
				}
			}
			if (jet < chance) {
				++retrait;
			}
		}
		return retrait;
	}

	public static long getXpGanadaRecau(Recaudador recaudador, long totalXP) {
		Gremio G = MundoDofus.getGremio(recaudador.getGremioID());
		float sabi = G.getStats(CentroInfo.STATS_ADD_SABIDURIA);
		float coef = (sabi + 100) / 100;
		long xpGanada = 0;
		xpGanada = (int) (coef * totalXP);
		float elrate = Emu.RATE_XP_PVM;
		if (!Emu.Maldicion) {
			return (long) (xpGanada * elrate);
		} else {
			elrate = (Math.round((Emu.RATE_XP_PVM / 2)));
			if (elrate <= 1) {
				elrate = 1;
			}
			return (long) (xpGanada * elrate);
		}
	}

	public static long getXpGanadaPVM(Luchador perso, ArrayList<Luchador> ganadores, ArrayList<Luchador> perdedores,
			long grupoXP) {
		if (perso.getPersonaje() == null) {
			return 0L;
		}
		if (ganadores.contains(perso)) {
			float sabiduria = perso.getTotalStatsConBuff().getEfecto(124);
			float coef = (sabiduria + 100.0F) / 100.0F;
			long xpGanada = 0L;
			int nivelMax = 0;
			for (Luchador entry : ganadores) {
				if (entry.getNivel() > nivelMax) {
					nivelMax = entry.getNivel();
				}
			}
			int nro = 0;
			for (Luchador entry : ganadores) {
				if (entry.getNivel() > nivelMax / 3) {
					nro++;
				}
			}
			float bonus = 1.0F;
			if (nro == 2) {
				bonus = 1.1F;
			}
			if (nro == 3) {
				bonus = 1.3F;
			}
			if (nro == 4) {
				bonus = 2.2F;
			}
			if (nro == 5) {
				bonus = 2.5F;
			}
			if (nro == 6) {
				bonus = 2.8F;
			}
			if (nro == 7) {
				bonus = 3.1F;
			}
			if (nro >= 8) {
				bonus = 3.5F;
			}
			int nivelPerdedores = 0;
			for (Luchador entry : perdedores) {
				nivelPerdedores += entry.getNivel();
			}
			int nivelGanadores = 0;
			for (Luchador entry : ganadores) {
				nivelGanadores += entry.getNivel();
			}
			float porcEntreGyP = 1.0F + nivelPerdedores / nivelGanadores;
			if (porcEntreGyP <= 1.3D) {
				porcEntreGyP = 1.3F;
			}
			int nivel = perso.getNivel();
			float porcEntrePjyG = 1.0F + nivel / nivelGanadores;
			xpGanada = (long) (grupoXP * porcEntreGyP * bonus * coef * porcEntrePjyG);
			float elrate = Emu.RATE_XP_PVM;
			if (!Emu.Maldicion) {
				return (long) (xpGanada * elrate);
			} else {
				elrate = (Math.round((Emu.RATE_XP_PVM / 2)));
				if (elrate <= 1) {
					elrate = 1;
				}
				return (long) (xpGanada * elrate);
			}
		}
		return 0L;
	}

	public static long getXPDonadaGremio(Luchador perso, long xpGanada) {
		if ((perso.getPersonaje() == null) || (perso.getPersonaje().getMiembroGremio() == null)) {
			return 0;
		}
		MiembroGremio gm = perso.getPersonaje().getMiembroGremio();
		float xp = xpGanada, Lvl = perso.getNivel(), LvlGuild = perso.getPersonaje().getGremio().getNivel(),
				porcXPDonada = (float) gm.getPorcXpDonada() / 100;
		float maxP = xp * porcXPDonada * 0.10F;
		float diff = Math.abs(Lvl - LvlGuild);
		float alGremio;
		if (diff >= 70) {
			alGremio = maxP * 0.10F;
		} else if (diff >= 31 && diff <= 69) {
			alGremio = (float) (maxP - ((maxP * 0.10F) * (Math.floor((diff + 30) / 10))));
		} else if (diff >= 10 && diff <= 30) {
			alGremio = (float) (maxP - ((maxP * 0.20F) * (Math.floor(diff / 10))));
		} else {
			alGremio = maxP;
		}
		return Math.round(alGremio);
	}

	public static long getXPDonadaDragopavo(Luchador luchador, long xpGanada) {
		Personaje perso = luchador.getPersonaje();
		if (perso == null) {
			return 0;
		}
		Dragopavo pavo = perso.getMontura();
		if (pavo == null) {
			return 0;
		}
		float xp = xpGanada;
		float coef = 1.0F;
		float porcMontura = (float) perso.getXpDonadaMontura() / 100;
		if (pavo.getNivel() < 50) {
			coef = 1.0F;
		} else if (pavo.getNivel() < 100) {
			coef = 0.75F;
		} else if (pavo.getNivel() < 150) {
			coef = 0.5F;
		} else if (pavo.getNivel() <= 200) {
			coef = 0.25F;
		}
		long xpdonada = (long) (xp * porcMontura * coef) / 100;
		return xpdonada;
	}

	public static long getKamasGanadas(Luchador luchador, long maxkamas, long minkamas) {
		int prospeccion = luchador.getTotalStatsConBuff().getEfecto(176);
		float coef = (prospeccion + 100.0F) / 100.0F;
		maxkamas++;
		long kamas = (long) ((Math.random() * (maxkamas - minkamas)) + minkamas);
		return (long) (kamas * coef * Emu.RATE_KAMAS);
	}

	public static long getKamasGanadaRecau(long maxkamas, long minkamas) {
		maxkamas++;
		long kamas = (long) (Math.random() * (maxkamas - minkamas)) + minkamas;
		return (long) (kamas * Emu.RATE_KAMAS);
	}

	public static int calculoPorcCambioElenemto(int nivelOficio, int nivelObjeto, int nivelRunaElemento) {
		int K = 1;
		if (nivelRunaElemento == 1) {
			K = 100;
		} else if (nivelRunaElemento == 25) {
			K = 175;
		} else if (nivelRunaElemento == 50) {
			K = 350;
		}
		return (nivelOficio * 100) / (K + nivelObjeto);
	}

	public static int calcularHonorGanado(ArrayList<Luchador> ganadores, Luchador luchador) {
		int totalganadores = 0;
		for (Luchador ga : ganadores) {
			if (ga.esInvocacion() || ga == null) {
				continue;
			}
			totalganadores += 1;
		}
		int honorganado = 0;
		int alineacionpj = luchador.getPersonaje().getNivelAlineacion();
		if (alineacionpj == 0 || alineacionpj == 1) {
			honorganado = 100;
		} else if (alineacionpj == 2) {
			honorganado = 200;
		} else if (alineacionpj == 3) {
			honorganado = 400;
		} else if (alineacionpj == 4) {
			honorganado = 400;
		} else if (alineacionpj == 5) {
			honorganado = 500;
		} else if (alineacionpj == 6) {
			honorganado = 500;
		} else if (alineacionpj == 7) {
			honorganado = 500;
		} else if (alineacionpj == 8) {
			honorganado = 500;
		} else if (alineacionpj == 9) {
			honorganado = 500;
		} else if (alineacionpj == 10) {
			honorganado = 500;
		} else {
			honorganado = 0;
		}
		honorganado = Math.round(honorganado / totalganadores);
		return honorganado;
	}

	public static float suerteFM(int pesoTotalBase, int pesoTotalActual, int pesoStatActual, int runa, int diferencia,
			float coef) {
		float porcentaje = 0;
		// float resta = 0;
		float a = (int) ((pesoTotalBase + diferencia) * coef * Emu.RATE_PORC_FM + 2);
		float b = (int) Math.sqrt(pesoTotalActual + pesoStatActual) + runa;
		if (b < 1) {
			b = 1;
		}
		porcentaje = (a / b);
		if (porcentaje < 1) {
			porcentaje = 1;
		}
		return porcentaje;
	}
}
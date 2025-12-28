package variables;

import estaticos.GestorSalida;
import servidor.Colores;

public class Guerras {
	public static boolean estaEnGuerra = false;
	public static int angelKills = 0;
	public static int devilKills = 0;
	public static byte winningAlignment = 0;
	public static short bonusPercent = 0;

	public static void startWar() {
		if (!estaEnGuerra) {
			winningAlignment = 0;
			bonusPercent = 30;
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE("<b>EVENTO :</b> La guerra de BONTAS vs BRAKS acaba de empezar.",
					Colores.AZUL);
			estaEnGuerra = true;
		}
	}

	public static void stopWar() {
		if (estaEnGuerra) {
			String winner = "No hay ning�n ganador todav�a";
			if (angelKills > devilKills) {
				bonusPercent = 30;
				winningAlignment = 1;
				winner = "Los ganadores son los Bontarianos que ganar�n un 100% de EXP en PVM una semana entera.";
			} else if (devilKills > angelKills) {
				bonusPercent = 30;
				winningAlignment = 2;
				winner = "Los ganadores son los Brakmarianos que ganar�n un 100% de EXP en PVM una semana entera.";
			} else if (devilKills == angelKills) {
				bonusPercent = 30;
				winningAlignment = 0;
				winner = "Hubo un empate en la guerra, tanto los Bontas como los Braks reciben un 50% de EXP en PVM una semana entera.";
			}
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE("La guerra de BONTAS vs BRAKS se ha acabado! " + winner
					+ "\n Los resultados son : " + "\n Bontas : " + angelKills + "\n Brakmars : " + devilKills,
					Colores.NARANJA);
			angelKills = 0;
			devilKills = 0;
			estaEnGuerra = false;
		}
	}

	public static void kill(byte align) {
		if (estaEnGuerra) {
			if (align == 1) {
				devilKills += 1;
			} else if (align == 2) {
				angelKills += 1;
			}
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE("Ha muerto un " + (align == 1 ? "Bontariano" : "Brakmariano")
					+ " , la alineacion contraria gana <b>+1</b> punto.", Colores.VERDE);
		}
	}
}
package variables;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import estaticos.Camino;
import estaticos.GestorSalida;

public class Interactivos {
	public static void tutorialIncarnam(Personaje perso) {
		Timer timer = new Timer(1000, new ActionListener() {
			int paso = 0;

			@Override
			public void actionPerformed(ActionEvent e) {
				int mapaid = perso.getMapa().getID();
				if (mapaid != 10258 || !perso.enLinea() || perso.getPelea() != null) {
					paso = 0;
					((Timer) e.getSource()).stop();
					return;
				}
				switch (paso) {
				case 0:
					GestorSalida.enviar(perso, "GM|+403;5;0;-99;99;-4;70^100;0;ffffff;-1;-1;0,205f,205e,0,0;;0");
					break;
				case 1:
					GestorSalida.enviar(perso, "cMK|-99|Mylo|Te voy a contar una historia sobre esta zona|");
					break;
				case 8:
					GestorSalida.enviar(perso,
							"cMK|-99|Mylo|Muchas criaturas malignas viven aquÉ y se han hecho con el control de todo!!!|");
					break;
				case 15:
					GestorSalida.enviar(perso,
							"cMK|-99|Mylo|Los ciudadanos estÁn muy asustados debido a la alta cantidad de monstruos que aparecen|");
					break;
				case 22:
					GestorSalida.enviar(perso,
							"cMK|-99|Mylo|La mazmorra de Incarnam ha sido ocupada completamente por ellos|");
					break;
				case 29:
					GestorSalida.enviar(perso,
							"cMK|-99|Mylo|Tendr�s que completar la mazmorra y averiguar por quÉ salen tantas criaturas|");
					break;
				case 36:
					GestorSalida.enviar(perso,
							"cMK|-99|Mylo|Y si puedes, intenta echarles una mano tambi�n a las personas que te encuentres, seguro que te compensar�n muy bien!!!|");
					break;
				case 37:
					paso = 0;
					((Timer) e.getSource()).stop();
					return;
				}
				paso += 1;
			}
		});
		timer.start();
	}

	public static void iniciaTutorial(Personaje perso) {
		Timer timer = new Timer(1000, new ActionListener() {
			int paso = 1;

			@Override
			public void actionPerformed(ActionEvent e) {
				int mapaid = perso.getMapa().getID();
				if (mapaid != 25047 || !perso.enLinea() || perso.getPelea() != null) {
					paso = 0;
					perso.puedeAbrir = true;
					((Timer) e.getSource()).stop();
					return;
				}
				switch (paso) {
				case 1:
					perso.puedeAbrir = false;
					GestorSalida.enviar(perso, "GM|+280;1;0;-99;99;-4;70^100;0;ffffff;-1;-1;0,205f,205e,0,0;;0");
					break;
				case 2:
					GestorSalida.enviar(perso, "cMK|-99|Mylo|" + Idiomas.getTexto(perso.getCuenta().idioma, 188));
					break;
				case 8:
					GestorSalida.enviar(perso, "cMK|-99|Mylo|" + Idiomas.getTexto(perso.getCuenta().idioma, 189));
					break;
				case 15:
					GestorSalida.enviar(perso, "cMK|-99|Mylo|" + Idiomas.getTexto(perso.getCuenta().idioma, 190));
					perso.getCuenta().getEntradaPersonaje().analizar_Packets("r", false);
					break;
				case 22:
					GestorSalida.enviar(perso, "cMK|-99|Mylo|" + Idiomas.getTexto(perso.getCuenta().idioma, 191));
					break;
				case 29:
					GestorSalida.enviar(perso,
							"cMK|-99|Mylo|" + Idiomas.getTexto(perso.getCuenta().idioma, 192) + ((char) 0x00) + "#kj");
					break;
				case 36:
					GestorSalida.enviar(perso, "cMK|-99|Mylo|" + Idiomas.getTexto(perso.getCuenta().idioma, 193));
					break;
				case 43:
					GestorSalida.enviar(perso, "cMK|-99|Mylo|" + Idiomas.getTexto(perso.getCuenta().idioma, 194));
					break;
				case 50:
					GestorSalida.enviar(perso, "cMK|-99|Mylo|" + Idiomas.getTexto(perso.getCuenta().idioma, 195));
					perso.getCuenta().getEntradaPersonaje().analizar_Packets("M", false);
					break;
				case 57:
					GestorSalida.enviar(perso,
							"cMK|-99|Mylo|" + Idiomas.getTexto(perso.getCuenta().idioma, 196) + ((char) 0x00) + "#bS");
					break;
				case 64:
					GestorSalida.enviar(perso, "cMK|-99|Mylo|" + Idiomas.getTexto(perso.getCuenta().idioma, 197));
					break;
				case 65:
					perso.puedeAbrir = true;
					paso = 0;
					((Timer) e.getSource()).stop();
					return;
				}
				paso += 1;
			}
		});
		timer.start();
	}

	public static void fuegosArficiales(int npcid, Personaje perso, int celda) {
		StringBuilder packet = new StringBuilder("");
		packet.append("GA0;228;" + npcid + ";" + celda + ",2900,11,8,2").append((char) 0x00);
		packet.append("GA0;228;" + npcid + ";" + celda + ",2901,11,8,2").append((char) 0x00);
		packet.append("GA0;228;" + npcid + ";" + celda + ",2902,11,8,2").append((char) 0x00);
		packet.append("GA0;228;" + npcid + ";" + celda + ",2901,11,8,2").append((char) 0x00);
		packet.append("GA0;228;" + npcid + ";" + celda + ",2902,11,8,2").append((char) 0x00);
		GestorSalida.enviar(perso, packet.toString());
	}

	@SuppressWarnings("unused")
	private static String muevePJ(int idespecialx, int celdaactual, int celdadestino, Personaje perso) {
		int idespecial = idespecialx;
		int celdapj = celdaactual;
		int cell = celdadestino;
		String pathstr = Camino.getShortestStringPathBetween(perso.getMapa(), celdapj, cell, 0);
		if (pathstr == null) {
			return "";
		}
		String packet = "GA0;1;" + idespecial + ";" + pathstr;
		return packet;
	}

	static void mensajeNPC(int npc, String nombrenpc, String mensaje, Personaje perso, int nuevomapa) {
		Timer timer = new Timer(1000, new ActionListener() {
			int paso = 0;

			@Override
			public void actionPerformed(ActionEvent e) {
				int mapaid = perso.getMapa().getID();
				if (mapaid != nuevomapa || !perso.enLinea() || perso.getPelea() != null) {
					paso = 0;
					((Timer) e.getSource()).stop();
					return;
				}
				paso += 1;
				switch (paso) {
				case 2:
					GestorSalida.enviar(perso, "cMK|" + npc + "|" + nombrenpc + "|" + mensaje + "|");
					break;
				case 3:
					paso = 0;
					((Timer) e.getSource()).stop();
					break;
				}
			}
		});
		timer.start();
	}
}
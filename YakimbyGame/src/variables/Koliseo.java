package variables;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import estaticos.CentroInfo;
import estaticos.Emu;
import estaticos.Formulas;
import estaticos.GestorSalida;
import estaticos.MundoDofus;
import servidor.Colores;
import variables.Objeto.ObjetoModelo;

public class Koliseo {
	public static ArrayList<Koliseo> arenas = new ArrayList<>();
	private Timer TimerKoliseo = new Timer();
	int randommap = 7410;
	public ArrayList<Personaje> Personajes = new ArrayList<>();
	public static int PersoxKoli = 4;
	// public ArrayList<Personaje> nospam = new ArrayList<Personaje>();
	public boolean peleaempezada = false;

	public void abandonar(Personaje _perso, boolean msg) {
		if (_perso.enLinea() && msg) {
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, "Te has salido del Koliseo", Colores.VERDE);
			for (Personaje persox : Personajes) {
				if (persox == _perso) {
					continue;
				}
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(persox, _perso.getNombre() + " se ha salido del grupo de Koliseo",
						Colores.ROJO);
			}
		}
		if (Personajes.contains(_perso)) {
			Personajes.remove(_perso);
		}
		for (Personaje perK : Personajes) {
			perK._kolietapa = 2;
			GestorSalida.enviar(perK, perK.getInfoKoliseo());
		}
		_perso.setKoliseo(null);
		_perso._koliacepta = 1;
		_perso._kolietapa = 1;
		String strf1 = _perso.getInfoKoliseo();
		if (_perso.enLinea()) {
			GestorSalida.enviar(_perso, strf1);
		}
		Purgatimer();
		// nospam.add(_perso);
		if (Personajes.size() <= 0) {
			// nospam.clear();
			Koliseo.arenas.remove(this);
		}
	}

	private void Purgatimer() {
		if (TimerKoliseo != null) {
			TimerKoliseo.cancel();
			TimerKoliseo.purge();
			TimerKoliseo = null;
		}
	}

	private void borrarMobs() {
		Mapa map = MundoDofus.getMapa(randommap);
		if (map != null) {
			map.borrarTodosMobs();
			map.borrarTodosMobsFix();
		}
	}

	public int totalCargados = 0;
	public int totalPersonajes = 0;

	private void empiezaCombate() {
		randommap = getRandomMap();
		borrarMobs();
		for (Personaje perK : Personajes) {
			GestorSalida.enviar(perK, "#kL");
		}
		for (Personaje perK : Personajes) {
			if (perK.getMapa().getID() != randommap) {
				perK.esperaKoliseo = true;
				totalPersonajes += 1;
				perK.teleport((short) randommap, 280);
			}
		}
	}

	private int getRandomMap() {
		int random = Formulas.getRandomValor(1, 7);
		switch (random) {
		case 1:
			return 7811;
		case 2:
			return 7470;
		case 3:
			return 7314;
		case 4:
			return 7423;
		case 5:
			return 215;
		case 6:
			return 5717;
		case 7:
			return 7423;
		default:
			return 7423;
		}
	}

	/*
	 * private int getRandomItem() { //11144,11145,11146,11147,11148,11149,11150
	 * Random rand = new Random(); switch (rand.nextInt(7) + 1) { case 1: return
	 * 11144; case 2: return 11145; case 3: return 11146; case 4: return 11147; case
	 * 5: return 11148; case 6: return 11149; case 7: return 11150; default: return
	 * 11144; } }
	 */

	void ganador(Personaje perso) {// sube 5-10
		perso.setKamas(perso.getKamas() + 10000000);
		perso.subirStats(1, 0);
		int random = Formulas.getRandomValor(5, 10);
		perso.addPuntos(random);
		if (perso.enLinea()) {
			GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso);
		}
		int rnd = Formulas.getRandomValor(5, 15);
		GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "Acabas de ganar una pelea de Koliseo y te llevas 10MK, " + rnd
				+ " Kolichas y ganas +" + random + " puntos", Colores.ROJO);
		ObjetoModelo OM = MundoDofus.getObjModelo(11158);
		if (OM == null) {
			return;
		}
		Objeto obj = OM.crearObjDesdeModelo(rnd, true);
		if (!perso.addObjetoSimilar(obj, true, -1)) {
			MundoDofus.addObjeto(obj, true);
			perso.addObjetoPut(obj);
			if (perso.enLinea()) {
				GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(perso, obj);
			}
		}
		perso.getKoliseo().abandonar(perso, false);
		if (perso.enLinea()) {
			GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(perso);
		}
	}

	void perdedor(Personaje perso) {// resta 8
		perso.subirStats(0, 1);
		perso.restarPuntos(8);
		GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "Has perdido 8 puntos por perder la pelea de Koliseo", Colores.ROJO);
		perso.getKoliseo().abandonar(perso, false);
	}

	public void unirCombate() {
		try {
			try {
				Thread.sleep(800);
			} catch (InterruptedException e) {
			}
			GestorSalida.ENVIAR_GA900_DESAFIAR(Personajes.get(0).getMapa(), Personajes.get(0).getID(),
					Personajes.get(1).getID()); // MAPA,YO,OBJETIVO DUELOID
			try {
				Thread.sleep(150);
			} catch (InterruptedException e) {
			}
			GestorSalida.ENVIAR_GA901_ACEPTAR_DESAFIO(Personajes.get(1).getMapa(), Personajes.get(0).getID(),
					Personajes.get(1).getID()); // PERSO MAPA,DUELOID,PERSOID
			GestorSalida.ENVIAR_GA902_RECHAZAR_DESAFIO(Personajes.get(1).getMapa(), Personajes.get(1).getDueloID(),
					Personajes.get(1).getID());
			Pelea pel = Personajes.get(0).getMapa().nuevaPelea(Personajes.get(0), Personajes.get(1),
					CentroInfo.PELEA_TIPO_DESAFIO, true, false);
			pel.enKoliseo = true;
			boolean unoyuno = false;
			Thread.sleep(500L);
			ArrayList<Personaje> pja = Personajes;
			for (Personaje perK : pja) {
				if (perK.getPelea() == null) {
					if (unoyuno) {
						unoyuno = false;
						Personajes.get(0).getPelea().unirsePelea(perK, Personajes.get(0).getID());
					} else {
						unoyuno = true;
						Personajes.get(1).getPelea().unirsePelea(perK, Personajes.get(1).getID());
					}
				}
				Thread.sleep(150);
			}
			peleaempezada = true;
		} catch (Exception e) {
			Emu.creaLogs(e);
		}
	}

	public void iniciaTimer() {
		try {
			TimerKoliseo = new Timer();
			TimerTask lot = new TimerTask() {
				int timert = 0;

				@Override
				public void run() {
					if (Personajes.size() != Koliseo.PersoxKoli) {
						this.cancel();
						Purgatimer();
						return;
					} else {
						boolean falta = false;
						for (Personaje perK : Personajes) {
							if (perK._koliacepta != 2) {
								if (timert >= 60) {
									this.cancel();
									abandonar(perK, false);
									return;
								}
								falta = true;
							}
						}
						timert += 1;
						if (!falta) {
							this.cancel();
							Purgatimer();
							empiezaCombate();
						}
					}
					// System.out.println("CORRE");
				}
			};
			TimerKoliseo.schedule(lot, 1000, 1000);
		} catch (Exception e) {
			TimerKoliseo.purge();
			System.out.println("BUG AL CERRAR UN TIMER DE KOLISEO");
		}
	}
}
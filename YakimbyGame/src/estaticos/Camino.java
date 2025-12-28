package estaticos;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import variables.Hechizo.StatsHechizos;
import variables.Mapa;
import variables.Mapa.Celda;
import variables.Pelea;
import variables.Pelea.Luchador;
import variables.Pelea.Trampa;

public class Camino {
	private static Integer _nroMovimientos = 0;

	public static short caminoValido(Mapa mapa, short celdaID, AtomicReference<String> pathRef, Pelea pelea,
			Luchador luchador) {
		synchronized (_nroMovimientos) {
			_nroMovimientos = 0;
			short nuevaCelda = celdaID;
			short movimientos = 0;
			String path = pathRef.get();
			StringBuilder nuevoPath = new StringBuilder("");
			for (int i = 0; i < path.length(); i += 3) {
				if (path.length() < (i + 3)) {
					return movimientos;
				}
				String miniPath = path.substring(i, i + 3);
				char dir = miniPath.charAt(0);
				short celdaIDDeco = Encriptador.celdaCodigoAID(miniPath.substring(1));
				_nroMovimientos = 0;
				if (pelea != null && i != 0 && getEnemigoAlrededor(nuevaCelda, mapa, pelea) != null) {
					pathRef.set(nuevoPath.toString());
					return movimientos;
				}
				if (pelea != null && i != 0) {
					for (Trampa p : pelea.getTrampas()) {
						int dist = distanciaEntreDosCeldas(mapa, p.getCelda().getID(), nuevaCelda);
						if (dist <= p.getTamaÑo()) {
							pathRef.set(nuevoPath.toString());
							return movimientos;
						}
					}
				}
				String[] aPathInfos = ValidSinglePath(nuevaCelda, miniPath, mapa, pelea, luchador).split(":");
				if (aPathInfos[0].equalsIgnoreCase("stop")) {
					nuevaCelda = Short.parseShort(aPathInfos[1]);
					movimientos += _nroMovimientos;
					nuevoPath.append(dir + Encriptador.celdaIDACodigo(nuevaCelda));
					pathRef.set(nuevoPath.toString());
					return (short) -movimientos;
				} else if (aPathInfos[0].equalsIgnoreCase("ok")) {
					nuevaCelda = celdaIDDeco;
					movimientos += _nroMovimientos;
				} else {
					pathRef.set(nuevoPath.toString());
					return -1000;
				}
				nuevoPath.append(dir + Encriptador.celdaIDACodigo(nuevaCelda));
			}
			pathRef.set(nuevoPath.toString());
			return movimientos;
		}
	}

	private static String ValidSinglePath(int celdaID, String Path, Mapa mapa, Pelea pelea, Luchador luchador) {
		_nroMovimientos = 0;
		char dir = Path.charAt(0);
		int celdaIDDeco = Encriptador.celdaCodigoAID(Path.substring(1));
		if (pelea != null && pelea.celdaOcupada(celdaIDDeco)) {
			return "no:";
		}
		int ultimaCelda = celdaID;
		for (_nroMovimientos = 1; _nroMovimientos <= 64; _nroMovimientos++) {
			if (luchador != null) {
				if (luchador.getTempPM(pelea) < _nroMovimientos) {
					return "stop:" + ultimaCelda;
				}
			}
			if (getSigIDCeldaMismaDir(ultimaCelda, dir, mapa, pelea != null) == celdaIDDeco) {
				if (pelea != null && pelea.celdaOcupada(celdaIDDeco)) {
					return "stop:" + ultimaCelda;
				}
				if (mapa.getCelda(celdaIDDeco).esCaminable(true)) {
					return "ok:";
				} else {
					_nroMovimientos--;
					return ("stop:" + ultimaCelda);
				}
			} else {
				ultimaCelda = getSigIDCeldaMismaDir(ultimaCelda, dir, mapa, pelea != null);
			}
			if (pelea != null && pelea.celdaOcupada(ultimaCelda)) {
				return "no:";
			}
			if (pelea != null) {
				if (getEnemigoAlrededor(ultimaCelda, mapa, pelea) != null) {
					return "stop:" + ultimaCelda;
				}
				for (Trampa p : pelea.getTrampas()) {
					int dist = distanciaEntreDosCeldas(mapa, p.getCelda().getID(), ultimaCelda);
					if (dist <= p.getTamaÑo()) {
						return "stop:" + ultimaCelda;
					}
				}
			}
		}
		return "no:";
	}

	public static byte getIndexDireccion(char c) {
		byte b = 0;
		char[] dirs = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h' };
		for (char a : dirs) {
			if (a == c) {
				return b;
			}
			b++;
		}
		return 0;
	}

	public static ArrayList<Luchador> getEnemigosAlrededor(Luchador lanzador, Mapa mapa, Pelea pelea) {
		char[] dirs = { 'b', 'd', 'f', 'h' };
		ArrayList<Luchador> enemy = new ArrayList<>();
		for (char dir : dirs) {
			if (mapa == null) {
				continue;
			}
			Celda sigCelda = mapa.getCelda(getSigIDCeldaMismaDir(lanzador.getCeldaPelea().getID(), dir, mapa, false));
			if (sigCelda == null) {
				continue;
			}
			Luchador luchador = mapa.getPrimerLuchador(sigCelda.getID());
			if (luchador != null) {
				if (luchador.getEquipoBin() != lanzador.getEquipoBin()) {
					enemy.add(luchador);
				}
			}
		}
		if (enemy.size() == 0 || enemy.size() == 4) {
			return null;
		}
		return enemy;
	}

	public static Luchador getEnemigoAlrededor(int celdaID, Mapa mapa, Pelea pelea) {
		char[] dirs = { 'b', 'd', 'f', 'h' };
		for (char dir : dirs) {
			if (mapa == null) {
				break;
			}
			Celda sigCelda = mapa.getCelda(getSigIDCeldaMismaDir(celdaID, dir, mapa, false));
			if (sigCelda == null) {
				continue;
			}
			Luchador luchador = mapa.getPrimerLuchador(sigCelda.getID());
			if (luchador != null) {
				if (pelea.LucActual != null) {
					if (luchador.getEquipoBin() != pelea.LucActual.getEquipoBin()) {
						return luchador;
					}
				}
			}
		}
		return null;
	}

	public static boolean hayAlrededor(Mapa mapa, Pelea pelea, Luchador l, boolean amigo) {
		char[] dirs = { 'b', 'd', 'f', 'h' };
		if (l == null || l.getCeldaPelea() == null || mapa == null) {
			return false;
		}
		for (char dir : dirs) {
			Celda sigCelda = mapa.getCelda(getSigIDCeldaMismaDir(l.getCeldaPelea().getID(), dir, mapa, false));
			if (sigCelda == null) {
				continue;
			}
			Luchador luchador = mapa.getPrimerLuchador(sigCelda.getID());
			if (luchador != null) {
				if (amigo) {
					if (luchador.getEquipoBin() == l.getEquipoBin()) {
						return true;
					}
				} else {
					if (luchador.getEquipoBin() != l.getEquipoBin()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	static boolean esSiguienteA(int celda1, int celda2, Mapa mapa) {
		int ancho = mapa.getAncho();
		if (celda1 + (ancho - 1) == celda2 || celda1 + (ancho) == celda2 || celda1 - (ancho - 1) == celda2
				|| celda1 - ancho == celda2) {
			return true;
		} else {
			return false;
		}
	}

	public static int getSigIDCeldaMismaDir(int celdaID, int direccion, Mapa mapa, boolean combate) {
		if (mapa == null) {
			return -1;
		}
		switch (direccion) {
		case 0:// para la derecha
			return combate ? -1 : celdaID + 1;
		case 1:
			return celdaID + mapa.getAncho(); // diagonal derecha abajo
		case '2':
			return combate ? -1 : celdaID + (mapa.getAncho() * 2 - 1);
		case '3':
			return celdaID + (mapa.getAncho() - 1); // diagonal izquierda abajo
		case 4:
			return combate ? -1 : celdaID - 1;
		case 5:
			return celdaID - mapa.getAncho(); // diagonal izquierda arriba
		case 6:
			return combate ? -1 : celdaID - (mapa.getAncho() * 2 - 1);
		case 7:
			return celdaID - mapa.getAncho() + 1;// diagonal derecha arriba
		}
		return -1;
	}

	public static int getSigIDCeldaMismaDir(int celdaID, char direccion, Mapa mapa, boolean combate) {
		if (mapa == null) {
			return -1;
		}
		switch (direccion) {
		case 'a':// para la derecha
			return combate ? -1 : celdaID + 1;
		case 'b':
			return celdaID + mapa.getAncho(); // diagonal derecha abajo
		case 'c':
			return combate ? -1 : celdaID + (mapa.getAncho() * 2 - 1);
		case 'd':
			return celdaID + (mapa.getAncho() - 1); // diagonal izquierda abajo
		case 'e':
			return combate ? -1 : celdaID - 1;
		case 'f':
			return celdaID - mapa.getAncho(); // diagonal izquierda arriba
		case 'g':
			return combate ? -1 : celdaID - (mapa.getAncho() * 2 - 1);
		case 'h':
			return celdaID - mapa.getAncho() + 1;// diagonal derecha arriba
		}
		return -1;
	}

	public static int getSigIDCeldaMismaDir(int celdaID, char direccion, int mapaID) {
		Mapa mapa = MundoDofus.getMapa(mapaID);
		if (mapa == null) {
			return -1;
		}
		switch (direccion) {
		case 'b':
			return celdaID + mapa.getAncho(); // diagonal derecha abajo
		case 'd':
			return celdaID + (mapa.getAncho() - 1); // diagonal izquierda abajo
		case 'f':
			return celdaID - mapa.getAncho(); // diagonal izquierda arriba
		case 'h':
			return celdaID - mapa.getAncho() + 1;// diagonal derecha arriba
		}
		return -1;
	}

	static boolean cellArroundCaseIDisOccuped(Pelea fight, int cell) {
		final char[] dirs = { 'b', 'd', 'f', 'h' };
		final ArrayList<Integer> Cases = new ArrayList<>();
		char[] array;
		for (int length = (array = dirs).length, i = 0; i < length; ++i) {
			final char dir = array[i];
			final int caseID = getSigIDCeldaMismaDir(cell, dir, fight.getMapaCopia(), true);
			Cases.add(caseID);
		}
		int ha = 0;
		for (Integer element : Cases) {
			if (fight.getMapaCopia().getCelda(element) != null) {
				if (fight.getMapaCopia().getPrimerLuchador(element) != null) {
					++ha;
				}
			}
		}
		return ha != 4;
	}

	static boolean haveFighterOnThisCell(int cell, Pelea fight) {
		for (Luchador f : fight.luchadoresDeEquipo(3)) {
			if (f.getCeldaPelea().getID() == cell && !f.estaMuerto() && !f.estaRetirado()
					|| f.getCeldaPelea().getID() == cell && f.statico) {
				return true;
			}
		}
		return false;
	}

	public static int distanciaEntreDosCeldas(Mapa mapa, int id1, int id2) {
		if ((id1 == id2) || (mapa == null)) {
			return 0;
		}
		int diffX = Math.abs(getCeldaCoordenadaX(mapa, id1) - getCeldaCoordenadaX(mapa, id2));
		int diffY = Math.abs(getCeldaCoordenadaY(mapa, id1) - getCeldaCoordenadaY(mapa, id2));
		return (diffX + diffY);
	}

	public static int getNuevaCeldaDespuesGolpe(Mapa mapa, Celda celdaInicio, Celda celdaObjetivo, int valor,
			Pelea pelea, Luchador objetivo) {
		if (celdaInicio.getID() == celdaObjetivo.getID()) {
			return 0;
		}
		char c = getDirEntreDosCeldas(celdaInicio.getID(), celdaObjetivo.getID(), mapa, true);
		int idCelda = celdaObjetivo.getID();
		if (valor < 0) {
			c = getDireccionOpuesta(c);
			valor = -valor;
		}
		for (int a = 0; a < valor; a++) {
			int sigCelda = getSigIDCeldaMismaDir(idCelda, c, mapa, true);
			if (mapa.getCelda(sigCelda) != null && mapa.getCelda(sigCelda).esCaminable(false)
					&& mapa.getLuchadores(sigCelda).isEmpty()) {
				idCelda = sigCelda;
				for (Trampa trampa : pelea.getTrampas()) {
					int dist = Camino.distanciaEntreDosCeldas(pelea.getMapaCopia(), trampa.getCelda().getID(), idCelda);
					if (dist <= trampa.getTamaÑo()) {
						return idCelda;
					}
				}
			} else {
				return (short) -(valor - a);
			}
		}
		if (idCelda == celdaObjetivo.getID()) {
			idCelda = 0;
		}
		return idCelda;
	}

	public static int getCeldaDespEmpujon(Mapa mapa, Celda celdaLanz, Celda celdaObje, int valor, Pelea pelea,
			Luchador objetivo) {
		if (celdaLanz.getID() == celdaObje.getID()) {
			return 0;
		}
		char c = getDirEntreDosCeldas(celdaLanz.getID(), celdaObje.getID(), mapa, true);
		int id = celdaObje.getID();
		if (valor < 0) {
			c = getDireccionOpuesta(c);
			valor = -valor;
		}
		for (int a = 0; a < valor; a++) {
			int sigCelda = getSigIDCeldaMismaDir(id, c, mapa, true);
			if (mapa.getCelda(sigCelda) != null && mapa.getCelda(sigCelda).esCaminable(true)
					&& mapa.getLuchadores(sigCelda).isEmpty()) {
				id = sigCelda;
				for (Trampa trampa : pelea.getTrampas()) {
					int dist = Camino.distanciaEntreDosCeldas(pelea.getMapaCopia(), trampa.getCelda().getID(), id);
					if (dist <= trampa.getTamaÑo()) {
						return id;
					}
				}
			} else {
				return -(valor - a);
			}
		}
		if (id == celdaObje.getID()) {
			id = 0;
		}
		return id;
	}

	public static char getDireccionOpuesta(char c) {
		switch (c) {
		case 'a':
			return 'e';
		case 'b':
			return 'f';
		case 'c':
			return 'g';
		case 'd':
			return 'h';
		case 'e':
			return 'a';
		case 'f':
			return 'b';
		case 'g':
			return 'c';
		case 'h':
			return 'd';
		}
		return 0x00;
	}

	public static boolean siCeldasEstanEnMismaLinea(Mapa map, int c1, int c2, char dir) {
		if (c1 == c2) {
			return true;
		}
		if (dir != 'z') {
			for (int a = 0; a < 70; a++) {
				if (getSigIDCeldaMismaDir(c1, dir, map, true) == c2) {
					return true;
				}
				if (getSigIDCeldaMismaDir(c1, dir, map, true) == -1) {
					break;
				}
				c1 = getSigIDCeldaMismaDir(c1, dir, map, true);
			}
		} else {
			char[] dirs = { 'b', 'd', 'f', 'h' };
			for (char d : dirs) {
				int c = c1;
				for (int a = 0; a < 70; a++) {
					if (getSigIDCeldaMismaDir(c, d, map, true) == c2) {
						return true;
					}
					c = getSigIDCeldaMismaDir(c, d, map, true);
				}
			}
		}
		return false;
	}

	public static ArrayList<Luchador> getObjetivosZonaArma(Pelea pelea, int tipo, Celda celda, int celdaIDLanzador) {
		ArrayList<Luchador> objetivos = new ArrayList<>();
		char c = getDirEntreDosCeldas(celdaIDLanzador, celda.getID(), pelea.getMapaCopia(), true);
		Mapa map = celda._mapa;
		if (c == 0) {
			Luchador lucprim = map.getPrimerLuchador(celda.getID());
			if (lucprim != null) {
				objetivos.add(lucprim);
			}
			return objetivos;
		}
		if (pelea.getMapaCopia() == null) {
			return objetivos;
		}
		switch (tipo) {
		case CentroInfo.ITEM_TIPO_MARTILLO:
			Luchador f = getFighter2CellBefore(celdaIDLanzador, c, pelea.getMapaCopia());
			if (f != null) {
				objetivos.add(f);
			}
			Luchador g = getPrimerLuchadorMismaDireccion(pelea.getMapaCopia(), celdaIDLanzador, (char) (c - 1));
			if (g != null)
			 {
				objetivos.add(g);// Agregar casilla a izquierda
			}
			Luchador h = getPrimerLuchadorMismaDireccion(pelea.getMapaCopia(), celdaIDLanzador, (char) (c + 1));
			if (h != null)
			 {
				objetivos.add(h);// Agregar casilla a derecha
			}
			Luchador i = map.getPrimerLuchador(celda.getID());
			if (i != null) {
				objetivos.add(i);
			}
			break;
		case CentroInfo.ITEM_TIPO_BASTON:
			Luchador j = getPrimerLuchadorMismaDireccion(pelea.getMapaCopia(), celdaIDLanzador, (char) (c - 1));
			if (j != null)
			 {
				objetivos.add(j);// Agregar casilla a izquierda
			}
			Luchador k = getPrimerLuchadorMismaDireccion(pelea.getMapaCopia(), celdaIDLanzador, (char) (c + 1));
			if (k != null)
			 {
				objetivos.add(k);// Agregar casilla a derecha
			}
			Luchador l = map.getPrimerLuchador(celda.getID());
			if (l != null)
			 {
				objetivos.add(l);// Agregar casilla objetivo
			}
			break;
		case CentroInfo.ITEM_TIPO_PICO:
		case CentroInfo.ITEM_TIPO_ESPADA:
		case CentroInfo.ITEM_TIPO_GUADAÑA:
		case CentroInfo.ITEM_TIPO_DAGAS:
		case CentroInfo.ITEM_TIPO_VARITA:
		case CentroInfo.ITEM_TIPO_PALA:
		case CentroInfo.ITEM_TIPO_ARCO:
		case CentroInfo.ITEM_TIPO_HACHA:
			Luchador m = map.getPrimerLuchador(celda.getID());
			if (m != null) {
				objetivos.add(m);
			}
			break;
		}
		return objetivos;
	}

	private static Luchador getPrimerLuchadorMismaDireccion(Mapa mapa, int id, char dir) {
		if (mapa == null) {
			return null;
		}
		if (dir == (char) ('a' - 1)) {
			dir = 'h';
		}
		if (dir == (char) ('h' + 1)) {
			dir = 'a';
		}
		return mapa.getPrimerLuchador(getSigIDCeldaMismaDir(id, dir, mapa, false));
	}

	private static Luchador getFighter2CellBefore(int celdaID, char dir, Mapa mapa) {
		int nueva2CeldaID = getSigIDCeldaMismaDir(getSigIDCeldaMismaDir(celdaID, dir, mapa, false), dir, mapa, false);
		Luchador lucpr = mapa.getPrimerLuchador(nueva2CeldaID);
		if (lucpr != null) {
			return lucpr;
		} else {
			return null;
		}
	}

	public static char getDirEntreDosCeldas(Mapa mapa, int id1, int id2) {
		if ((id1 == id2) || (mapa == null)) {
			return 0;
		}
		int difX = (getCeldaCoordenadaX(mapa, id1) - getCeldaCoordenadaX(mapa, id2));
		int difY = (getCeldaCoordenadaY(mapa, id1) - getCeldaCoordenadaY(mapa, id2));
		int difXabs = Math.abs(difX);
		int difYabs = Math.abs(difY);
		if (difXabs > difYabs) {
			if (difX > 0) {
				return 'f';
			} else {
				return 'b';
			}
		} else {
			if (difY > 0) {
				return 'h';
			} else {
				return 'd';
			}
		}
	}

	public static char getDirEntreDosCeldas(int celdaID1, int celdaID2, Mapa mapa, boolean combate) {
		ArrayList<Character> direcciones = new ArrayList<>();
		direcciones.add('b');
		direcciones.add('d');
		direcciones.add('f');
		direcciones.add('h');
		if (!combate) {
			direcciones.add('a');
			direcciones.add('b');
			direcciones.add('c');
			direcciones.add('d');
		}
		for (char c : direcciones) {
			int celda = celdaID1;
			for (int i = 0; i <= 64; i++) {
				if (getSigIDCeldaMismaDir(celda, c, mapa, combate) == celdaID2) {
					return c;
				}
				celda = getSigIDCeldaMismaDir(celda, c, mapa, combate);
			}
		}
		return 0;
	}

	public static ArrayList<Celda> getCeldasAfectadasEnElArea(Mapa mapa, int celdaID, int celdaIDLanzador,
			String afectados, int posTipoAlcance, boolean esGC) {
		ArrayList<Celda> cases = new ArrayList<>();
		if ((mapa == null) || (mapa.getCelda(celdaID) == null)) {
			return cases;
		}
		cases.add(mapa.getCelda(celdaID));
		int tamaÑo = Encriptador.getNumeroPorValorHash(afectados.charAt(posTipoAlcance + 1));
		switch (afectados.charAt(posTipoAlcance)) {
		case 'C':// Circulo
			for (int a = 0; a < tamaÑo; a++) {
				char[] dirs = { 'b', 'd', 'f', 'h' };
				ArrayList<Celda> cases2 = new ArrayList<>();
				cases2.addAll(cases);
				for (Celda aCell : cases2) {
					for (char d : dirs) {
						Celda cell = mapa.getCelda(Camino.getSigIDCeldaMismaDir(aCell.getID(), d, mapa, true));
						if (cell == null) {
							continue;
						}
						if (!cases.contains(cell)) {
							cases.add(cell);
						}
					}
				}
			}
			break;
		case 'X':// Croix
			char[] dirs = { 'b', 'd', 'f', 'h' };
			for (char d : dirs) {
				int cID = celdaID;
				for (int a = 0; a < tamaÑo; a++) {
					int C = getSigIDCeldaMismaDir(cID, d, mapa, true);
					if (C == -1 || mapa.getCelda(C) == null) {
						break;
					}
					cases.add(mapa.getCelda(C));
					cID = C;
				}
			}
			break;
		case 'L':// Ligne
			char dir = getDirEntreDosCeldas(celdaIDLanzador, celdaID, mapa, true);
			for (int a = 0; a < tamaÑo; a++) {
				celdaID = getSigIDCeldaMismaDir(celdaID, dir, mapa, true);
				if (celdaID == -1 || mapa.getCelda(celdaID) == null) {
					break;
				}
				cases.add(mapa.getCelda(celdaID));

			}
			break;
		case 'P':// Jugador?
		case 'T':// Jugador?
			break;
		default:
			System.out.println(
					"[FIXME]Tipo de alcance no reconocido: " + afectados.charAt(posTipoAlcance) + " y " + afectados);
			break;
		}
		return cases;
	}

	private static int getCeldaCoordenadaX(Mapa mapa, int celdaID) {
		if (mapa == null) {
			return 0;
		}
		int w = mapa.getAncho();
		return ((celdaID - (w - 1) * getCeldaCoordenadaY(mapa, celdaID)) / w);
	}

	private static int getCeldaCoordenadaY(Mapa mapa, int celdaID) {
		int w = mapa.getAncho();
		int loc5 = celdaID / ((w * 2) - 1);
		int loc6 = celdaID - loc5 * ((w * 2) - 1);
		int loc7 = loc6 % w;
		return (loc5 - loc7);
	}

	public static boolean checkearLineaDeVista(Mapa mapa, int celda1, int celda2, Luchador luchador) {
		if (luchador.getPersonaje() != null) {
			return true;
		}
		int dist = distanciaEntreDosCeldas(mapa, celda1, celda2);
		ArrayList<Integer> los = new ArrayList<>();
		if (dist > 2) {
			los = getLineaDeVista(celda1, celda2, mapa);
		}
		if (los != null && dist > 2) {
			for (int i : los) {
				if (i != celda1 && i != celda2 && !mapa.getCelda(i).lineaDeVistaBloqueada()) {
					return false;
				}
			}
		}
		if (dist > 2) {
			int cell = getCeldaMasCercanaAlrededor(mapa, celda2, celda1, null);
			if (cell != -1 && !mapa.getCelda(cell).lineaDeVistaBloqueada()) {
				return false;
			}
		}
		return true;
	}

	public static int getCeldaMasCercanaAlrededor(Mapa mapa, int celdaInicio, int celdaFinal,
			ArrayList<Celda> celdasProhibidas) {
		int dist = 1000;
		int celdaID = celdaInicio;
		if (celdasProhibidas == null) {
			celdasProhibidas = new ArrayList<>();
		}
		char[] dirs = { 'b', 'd', 'f', 'h' };
		for (char d : dirs) {
			int sigCelda = Camino.getSigIDCeldaMismaDir(celdaInicio, d, mapa, true);
			Celda C = mapa.getCelda(sigCelda);
			if (C == null) {
				break;
			}
			int dis = Camino.distanciaEntreDosCeldas(mapa, celdaFinal, sigCelda);
			if (dis < dist && C.esCaminable(true) && mapa.getPrimerLuchador(C.getID()) == null
					&& !celdasProhibidas.contains(C) && mapa.getLuchadores(sigCelda).size() == 0) {
				dist = dis;
				celdaID = sigCelda;
			}
		}
		return celdaID == celdaInicio ? -1 : celdaID;
	}

	static int celdaLejana(ArrayList<Integer> celdas, int celdadestino, Mapa mapa) {
		int dist = 0;
		int celdafinal = -1;
		for (Integer celda : celdas) {
			int dis = Camino.distanciaEntreDosCeldas(mapa, celda, celdadestino);
			Celda C = mapa.getCelda(celda);
			if (C == null) {
				continue;
			}
			if (dis > dist && C.esCaminable(false) && mapa.getLuchadores(celda).size() <= 0) {
				celdafinal = celda;
				dist = dis;
			}
		}
		return celdafinal;
	}

	static int celdaCercanas(ArrayList<Integer> celdas, int celdadestino, Mapa mapa) {
		int dist = 1000;
		int celdafinal = -1;
		for (Integer celda : celdas) {
			int dis = Camino.distanciaEntreDosCeldas(mapa, celda, celdadestino);
			Celda C = mapa.getCelda(celda);
			if (C == null) {
				continue;
			}
			if (dis < dist && C.esCaminable(false) && mapa.getLuchadores(celda).size() <= 0) {
				celdafinal = celda;
				dist = dis;
			}
		}
		return celdafinal;
	}

	static int getCeldaMasCercanaAlrededor2(Mapa mapa, int celdaInicio, int celdaFinal, int alcanceMin,
			int alcanceMax) {
		int dist = 1000;
		int celdaID = celdaInicio;
		char d = getDirEntreDosCeldas(mapa, celdaInicio, celdaFinal);
		int celdaInicio2 = celdaInicio;
		int sigCelda = 0;
		int i = 0;
		while (i < alcanceMax) {
			sigCelda = Camino.getSigIDCeldaMismaDir(celdaInicio2, d, mapa, true);
			celdaInicio2 = sigCelda;
			i++;
			if (i > alcanceMin) {
				Celda C = mapa.getCelda(sigCelda);
				if (C == null) {
					continue;
				}
				if (C.esCaminable(false) && mapa.getPrimerLuchador(sigCelda) == null) {
					break;
				}
			}
		}
		Celda C = mapa.getCelda(sigCelda);
		if (C == null) {
			return -1;
		}
		int dis = Camino.distanciaEntreDosCeldas(mapa, celdaFinal, sigCelda);
		if (dis < dist && C.esCaminable(true) && mapa.getPrimerLuchador(sigCelda) == null) {
			dist = dis;
			celdaID = sigCelda;
		}
		return celdaID == celdaInicio ? -1 : celdaID;
	}

	public static ArrayList<Integer> getCeldas(Mapa mapa, int celdaInicio, int alcanceMax, StatsHechizos sH,
			Pelea pelea, Luchador lanzador) {
		ArrayList<Integer> tempPath = new ArrayList<>();
		int distmax = alcanceMax;
		char[] dirs = { 'b', 'd', 'f', 'h' };
		int lastcell = 0;
		for (char d : dirs) {
			lastcell = celdaInicio;
			for (int a = 0; a < distmax; a++) {
				int sigCelda = Camino.getSigIDCeldaMismaDir(lastcell, d, mapa, true);
				if (sigCelda != 0) {
					lastcell = sigCelda;
				}
				if (mapa == null) {
					break;
				}
				Celda C = mapa.getCelda(sigCelda);
				if (C == null) {
					continue;
				}
				if (C.esCaminable(false) && mapa.getPrimerLuchador(sigCelda) == null) {
					if (!tempPath.contains(sigCelda)) {
						if (sH != null) {
							if (pelea.puedeLanzarHechizo(lanzador, sH, C, lanzador.getCeldaPelea().getID(), true,
									true)) {
								tempPath.add(sigCelda);
							}
						} else {
							tempPath.add(sigCelda);
						}
						if (a > 0) {
							int milastc = 0;
							for (int b = 0; b < a; b++) {
								if (milastc == 0) {
									milastc = sigCelda;
								}
								char dx = 0;
								switch (d) {
								case 'b':
									dx = 'e';
									break;
								case 'd':
									dx = 'g';
									break;
								case 'f':
									dx = 'a';
									break;
								case 'h':
									dx = 'c';
									break;
								}
								if (dx != 0) {
									int lacell = Camino.getSigIDCeldaMismaDir(milastc, dx, mapa, false);
									milastc = lacell;
									Celda C1 = mapa.getCelda(lacell);
									if (C1 == null) {
										continue;
									}
									if (C1.esCaminable(false) && mapa.getPrimerLuchador(lacell) == null) {
										if (!tempPath.contains(lacell)) {
											if (sH != null) {
												if (pelea.puedeLanzarHechizo(lanzador, sH, C1,
														lanzador.getCeldaPelea().getID(), true, true)) {
													tempPath.add(lacell);
												}
											} else {
												tempPath.add(lacell);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return tempPath;
	}

	public static ArrayList<Luchador> getCeldasEnemigo(Mapa mapa, int celdaInicio, int alcanceMax, StatsHechizos sH,
			Pelea pelea, Luchador lanzador) {
		ArrayList<Luchador> tempLuchadores = new ArrayList<>();
		int distmax = alcanceMax;
		char[] dirs = { 'b', 'd', 'f', 'h' };
		int lastcell = 0;
		for (char d : dirs) {
			lastcell = celdaInicio;
			for (int a = 0; a < distmax; a++) {
				int sigCelda = Camino.getSigIDCeldaMismaDir(lastcell, d, mapa, true);
				if (sigCelda != 0) {
					lastcell = sigCelda;
				}
				if (mapa == null) {
					break;
				}
				Celda C = mapa.getCelda(sigCelda);
				if (C == null) {
					continue;
				}
				if (C.esCaminable(false)) {
					Luchador lucp = mapa.getPrimerLuchador(sigCelda);
					if (lucp != null) {
						if (!tempLuchadores.contains(lucp)) {
							if (pelea.puedeLanzarHechizo(lanzador, sH, C, lanzador.getCeldaPelea().getID(), true,
									true)) {
								tempLuchadores.add(lucp);
							}
							if (a > 0) {
								int milastc = 0;
								for (int b = 0; b < a; b++) {
									if (milastc == 0) {
										milastc = sigCelda;
									}
									char dx = 0;
									switch (d) {
									case 'b':
										dx = 'e';
										break;
									case 'd':
										dx = 'g';
										break;
									case 'f':
										dx = 'a';
										break;
									case 'h':
										dx = 'c';
										break;
									}
									if (dx != 0) {
										int lacell = Camino.getSigIDCeldaMismaDir(milastc, dx, mapa, false);
										milastc = lacell;
										Celda C1 = mapa.getCelda(lacell);
										if (C1 == null) {
											continue;
										}
										if (C1.esCaminable(false)) {
											lucp = mapa.getPrimerLuchador(lacell);
											if (lucp != null) {
												if (!tempLuchadores.contains(lucp)) {
													if (pelea.puedeLanzarHechizo(lanzador, sH, C1,
															lanzador.getCeldaPelea().getID(), true, true)) {
														tempLuchadores.add(lucp);
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return tempLuchadores;
	}

	public static ArrayList<Celda> pathMasCortoEntreDosCeldas(Mapa map, int start, int dest, int distMax) {
		ArrayList<Celda> curPath = new ArrayList<>();
		ArrayList<Celda> curPath2 = new ArrayList<>();
		ArrayList<Celda> closeCells = new ArrayList<>();
		int limit = 1000;
		Celda curCase = map.getCelda(start);
		int stepNum = 0;
		boolean stop = false;
		while (!stop && stepNum++ <= limit) {
			int nearestCell = getCeldaMasCercanaAlrededor(map, curCase.getID(), dest, closeCells);
			if (nearestCell == -1) {
				closeCells.add(curCase);
				if (curPath.size() > 0) {
					curPath.remove(curPath.size() - 1);
					if (curPath.size() > 0) {
						curCase = curPath.get(curPath.size() - 1);
					} else {
						curCase = map.getCelda(start);
					}
				} else {
					curCase = map.getCelda(start);
				}
			} else if (distMax == 0 && nearestCell == dest) {
				curPath.add(map.getCelda(dest));
				break;
			} else if (distMax > Camino.distanciaEntreDosCeldas(map, nearestCell, dest)) {
				curPath.add(map.getCelda(dest));
				break;
			} else {
				curCase = map.getCelda(nearestCell);
				closeCells.add(curCase);
				curPath.add(curCase);
			}
		}
		curCase = map.getCelda(start);
		closeCells.clear();
		if (!curPath.isEmpty()) {
			closeCells.add(curPath.get(0));
		}
		while (!stop && stepNum++ <= limit) {
			int nearestCell = getCeldaMasCercanaAlrededor(map, curCase.getID(), dest, closeCells);
			if (nearestCell == -1) {
				closeCells.add(curCase);
				if (curPath2.size() > 0) {
					curPath2.remove(curPath2.size() - 1);
					if (curPath2.size() > 0) {
						curCase = curPath2.get(curPath2.size() - 1);
					} else {
						curCase = map.getCelda(start);
					}
				} else {
					curCase = map.getCelda(start);
				}
			} else if (distMax == 0 && nearestCell == dest) {
				curPath2.add(map.getCelda(dest));
				break;
			} else if (distMax > Camino.distanciaEntreDosCeldas(map, nearestCell, dest)) {
				curPath2.add(map.getCelda(dest));
				break;
			} else {
				curCase = map.getCelda(nearestCell);
				closeCells.add(curCase);
				curPath2.add(curCase);
			}
		}
		if ((curPath2.size() < curPath.size() && curPath2.size() > 0) || curPath.isEmpty()) {
			curPath = curPath2;
		}
		return curPath;
	}

	public static String getShortestStringPathBetween(Mapa map, int start, int dest, int distMax) {
		if (start == dest) {
			return null;
		}
		ArrayList<Celda> path = pathMasCortoEntreDosCeldas(map, start, dest, distMax);
		if (path == null) {
			return null;
		}
		String pathstr = "";
		int curCaseID = start;
		char curDir = Character.MIN_VALUE;
		for (Celda c : path) {
			char d = getDirEntreDosCeldas(curCaseID, c.getID(), map, true);
			if (d == '\000') {
				return null;
			}
			if (curDir != d) {
				if (path.indexOf(c) != 0) {
					pathstr = String.valueOf(pathstr) + Encriptador.celdaIDACodigo(curCaseID);
				}
				pathstr = String.valueOf(pathstr) + d;
				curDir = d;
			}
			curCaseID = c.getID();
		}
		if (curCaseID != start) {
			pathstr = String.valueOf(pathstr) + Encriptador.celdaIDACodigo(curCaseID);
		}
		if (pathstr == "") {
			return null;
		}
		return "a" + Encriptador.celdaIDACodigo(start) + pathstr;
	}

	static ArrayList<Integer> listaCeldasDesdeLuchador(Pelea pelea, Luchador luchador) {
		ArrayList<Integer> celdas = new ArrayList<>();
		int celdaInicio = luchador.getCeldaPelea().getID();
		int[] tempPath;
		int i = 0;
		if (luchador.getTempPM(pelea) > 0) {
			tempPath = new int[luchador.getTempPM(pelea)];
		} else {
			return null;
		}
		if (tempPath.length == 0) {
			return null;
		}
		while (tempPath[0] != 5) {
			tempPath[i]++;
			if (tempPath[i] == 5 && i != 0) {
				tempPath[i] = 0;
				i--;
			} else {
				int tempCelda = getCeldaDesdePath(celdaInicio, tempPath, pelea.getMapaCopia());
				if (tempCelda == -1) {
					continue;
				}
				Celda celdaTemp = pelea.getMapaCopia().getCelda(tempCelda);
				if (celdaTemp == null) {
					continue;
				}
				if (celdaTemp.esCaminable(true) && pelea.getMapaCopia().getPrimerLuchador(tempCelda) == null) {
					if (!celdas.contains(tempCelda)) {
						celdas.add(tempCelda);
						if (i < tempPath.length - 1) {
							i++;
						}
					}
				}
			}
		}
		return listaCeldasPorDistancia(pelea, luchador, celdas);
	}

	private static int getCeldaDesdePath(int inicio, int[] path, Mapa mapa) {
		int celda = inicio, i = 0;
		if (mapa == null) {
			return -1;
		}
		int ancho = mapa.getAncho();
		while (i < path.length) {
			if (path[i] == 1) {
				celda -= ancho;
			}
			if (path[i] == 2) {
				celda -= (ancho - 1);
			}
			if (path[i] == 3) {
				celda += ancho;
			}
			if (path[i] == 4) {
				celda += (ancho - 1);
			}
			i++;
		}
		return celda;
	}

	private static ArrayList<Integer> listaCeldasPorDistancia(Pelea pelea, Luchador luchador,
			ArrayList<Integer> celdas) {
		ArrayList<Integer> celdasPelea = new ArrayList<>();
		ArrayList<Integer> copiaCeldas = celdas;
		int dist = 100;
		int tempCelda = 0;
		int tempIndex = 0;
		while (copiaCeldas.size() > 0) {
			dist = 200;
			for (int celda : copiaCeldas) {
				int d = distanciaEntreDosCeldas(pelea.getMapaCopia(), luchador.getCeldaPelea().getID(), celda);
				if (dist > d) {
					dist = d;
					tempCelda = celda;
					tempIndex = copiaCeldas.indexOf(celda);
				}
			}
			celdasPelea.add(tempCelda);
			copiaCeldas.remove(tempIndex);
		}
		return celdasPelea;
	}

	private static boolean esBorde1(int id) {
		int[] bords = { 1, 30, 59, 88, 117, 146, 175, 204, 233, 262, 291, 320, 349, 378, 407, 436, 465, 15, 44, 73, 102,
				131, 160, 189, 218, 247, 276, 305, 334, 363, 392, 421, 450, 479 };
		ArrayList<Integer> test = new ArrayList<>();
		for (int i : bords) {
			test.add(i);
		}
		if (test.contains(id)) {
			return true;
		} else {
			return false;
		}
	}

	private static boolean esBorde2(int id) {
		int[] bords = { 16, 45, 74, 103, 132, 161, 190, 219, 248, 277, 306, 335, 364, 393, 422, 451, 29, 58, 87, 116,
				145, 174, 203, 232, 261, 290, 319, 348, 377, 406, 435, 464 };
		ArrayList<Integer> test = new ArrayList<>();
		for (int i : bords) {
			test.add(i);
		}
		if (test.contains(id)) {
			return true;
		} else {
			return false;
		}
	}

	private static ArrayList<Integer> getLineaDeVista(int celda1, int celda2, Mapa mapa) {
		ArrayList<Integer> lineasDeVista = new ArrayList<>();
		int celda = celda1;
		boolean siguiente = false;
		int ancho = mapa.getAncho();
		int alto = mapa.getAlto();
		int ultCelda = mapa.ultimaCeldaID();
		int[] dir1 = { 1, -1, (ancho + alto), -(ancho + alto), ancho, (ancho - 1), -(ancho), -(ancho - 1) };
		for (int i : dir1) {
			lineasDeVista.clear();
			celda = celda1;
			lineasDeVista.add(celda);
			siguiente = false;
			while (!siguiente) {
				celda += i;
				lineasDeVista.add(celda);
				if (esBorde2(celda) || esBorde1(celda) || celda <= 0 || celda >= ultCelda) {
					siguiente = true;
				}
				if (celda == celda2) {
					return lineasDeVista;
				}
			}
		}
		return null;
	}

	public static int celdaMovPerco(Mapa mapa, int celda) {
		ArrayList<Integer> celdasPosibles = new ArrayList<>();
		int ancho = mapa.getAncho();
		int[] dir = { -(ancho), -(ancho - 1), (ancho - 1), ancho };
		for (int element : dir) {
			if (celda + element > 14 || celda + element < 464) {
				if (mapa.getCelda(celda + element).esCaminable(false)) {
					celdasPosibles.add(celda + element);
				}
			}
		}
		if (celdasPosibles.size() <= 0) {
			return -1;
		}
		int celda_mov = celdasPosibles.get(Formulas.getRandomValor(0, celdasPosibles.size() - 1));
		return celda_mov;
	}
}
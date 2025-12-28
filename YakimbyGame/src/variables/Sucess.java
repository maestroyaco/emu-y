package variables;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;

import estaticos.MundoDofus;

/** @author Orion **/

public class Sucess {
	private int id;
	private byte type;
	private String args;
	// Variables
	private int valeur, recompense, points, _art, _categoria;
	private String name, recompenseArgs;
	public static Map<Integer, Sucess> sucess = new TreeMap<>();
	public static CopyOnWriteArrayList<Sucess> suc = new CopyOnWriteArrayList<>();
	// public Map<Integer, Integer> listMobsWin = new TreeMap<Integer, Integer>();

	public Sucess(int id) {
		setId(id);
	}

	public Sucess(int id, String name, byte type, String args, int recompense, String recompenseArgs, int points,
			int art, int categoria) {
		this.setId(id);
		this.setName(name);
		this.setType(type);
		this.setArgs(args);
		this.setRecompense(recompense);
		this.setRecompenseArgs(recompenseArgs);
		this.setPoints(points);
		this.setArt(art);
		this.setCategoria(categoria);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public int getArt() {
		return _art;
	}

	public void setArt(int type) {
		this._art = type;
	}

	public void setCategoria(int type) {
		this._categoria = type;
	}

	public int getCategoria() {
		return _categoria;
	}

	public String getArgs() {
		return args;
	}

	public void setArgs(String args) {
		this.args = args;
	}

	public static void launch(Personaje p, byte type, Map<Integer, Integer> mobs, int luchadores) {
		if (p == null) {
			return;
		}
		Sucess e = null;
		int args = 0;
		int cant = 0;
		for (Entry<Integer, Sucess> s : MundoDofus.getSucess().entrySet()) {
			if (s.getValue().getType() == type) {
				if (suc.contains(s.getValue())) {
					continue;
				}
				suc.add(s.getValue());
			}
		}
		switch (type) {
		case 1:// NivelesOmega
			if (suc == null) {
				return;
			}
			for (Sucess s : suc) {
				try {
					args = Integer.parseInt(s.getArgs());
				} catch (Exception m) {
					continue;
				}
				if (e != null) {
					e = null;
				}
				if (args <= p.getNivelOmega() && s.getType() == type) {
					e = new Sucess(s.getId(), s.getName(), s.getType(), s.getArgs(), s.getRecompense(),
							s.getRecompenseArgs(), s.getPoints(), s.getArt(), s.getCategoria());

					if (e != null) {
						if (p.getAllSucess().contains(e.getId())) {
							continue;
						}
						p.getAllSucess().add(e.getId());
						giveRecompense(e, p);
						p.setPointsSucces(p.getPointsSucces() + e.getPoints());
						return;
					}
				}

			}
			return;
		}
		launch(p, (byte) 15, null, 0);

	}

	private static void giveRecompense(Sucess e, Personaje p) {
		// TODO Auto-generated method stub

	}

	public int getRecompense() {
		return recompense;
	}

	public void setRecompense(int recompense) {
		this.recompense = recompense;
	}

	public void setPoints(int Points) {
		this.points = Points;
	}

	public int getValeur() {
		return valeur;
	}

	public void setValeur(int valeur) {
		this.valeur = valeur;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRecompenseArgs() {
		return recompenseArgs;
	}

	public int getPoints() {
		return points;
	}

	public void setRecompenseArgs(String recompenseArgs) {
		this.recompenseArgs = recompenseArgs;
	}
}

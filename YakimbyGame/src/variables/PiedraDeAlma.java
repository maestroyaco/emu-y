package variables;

import java.util.ArrayList;

import estaticos.MundoDofus;
import estaticos.MundoDofus.Duo;

public class PiedraDeAlma extends Objeto {
	private ArrayList<Duo<Integer, Integer>> _mobs;

	public PiedraDeAlma(int id, int cantidad, int modelo, int pos, String strStats) {
		_id = id;
		_modelo = MundoDofus.getObjModelo(modelo); // 7010 = piedra de alma
		_idObjModelo = _modelo.getID();
		_cantidad = 1;
		_posicion = -1;
		_mobs = new ArrayList<>();
		convertirStringAStats(strStats);
	}

	@Override
	public void convertirStringAStats(String mounstros) {
		String[] split = mounstros.split(",");
		for (String s : split) {
			try {
				int mob = Integer.parseInt(s.split("#")[3], 16);
				int nivel = Integer.parseInt(s.split("#")[1], 16);
				_mobs.add(new Duo<>(mob, nivel));
			} catch (Exception e) {
				continue;
			}
		}
	}
}
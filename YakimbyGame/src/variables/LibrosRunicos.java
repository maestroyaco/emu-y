package variables;
import estaticos.GestorSalida;
import estaticos.MundoDofus;


public class LibrosRunicos {

	private int _id;
	private long _experiencia;
	private int _nivel;


	public LibrosRunicos(int id,  int nivel, long experiencia) {
		_id = id;
		_nivel = nivel;
		_experiencia = experiencia;

	}


	private void subirNivel(Personaje perso) {
		if (_nivel == 50) {
			return;
		}
		_nivel++;
		GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(perso, MundoDofus.getObjeto(_id));

	}

	void addExp(long xp, Personaje perso) {
		_experiencia += xp;
		while (_experiencia >= MundoDofus.getExpMaxPersonaje(_nivel) && _nivel < 50) {
			subirNivel(perso);
		}
		GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso);
	}

	public int getID() {
		return _id;
	}
	public long getExperiencia() {
		return _experiencia;
	}

	public int getNivel() {
		return _nivel;
	}
}

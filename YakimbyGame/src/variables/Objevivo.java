package variables;

public class Objevivo {
	private int id;
	private int comidaAño;
	private int comidaFecha;
	private int comidaHora;
	private int humor;
	private int mascara;
	private int tipo;
	private int xp;
	private int interAño;
	private int interFecha;
	private int interHora;
	private int adqAño;
	private int adqFecha;
	private int adqHora;
	private int asociado;// asociado algun item 0 = no, 1 = si
	private int realModeloDB;// item original (objevivo 9233 capa, 9234 sombrero, 9255 amuleto, 9256 anillo
	private int itemObjevivo; // item del objevivo original
	private String stats;

	public Objevivo(int ID, String todo, String todo2, int objevivo, String stats) {
		this.id = ID;
		String[] todos = todo.split("~");
		if (todos[0].length() > 0) {
			comidaAño = Integer.parseInt(todos[0]);
			comidaFecha = Integer.parseInt(todos[1]);
			comidaHora = Integer.parseInt(todos[2]);
			humor = Integer.parseInt(todos[3]);
			mascara = Integer.parseInt(todos[4]);
			tipo = Integer.parseInt(todos[5]);
			asociado = Integer.parseInt(todos[6]);
			xp = Integer.parseInt(todos[7]);
		}
		String[] todos22 = todo2.split("~");
		if (todos22[0].length() > 0) {
			interAño = Integer.parseInt(todos22[0]);
			interFecha = Integer.parseInt(todos22[1]);
			interHora = Integer.parseInt(todos22[2]);
			adqAño = Integer.parseInt(todos22[3]);
			adqFecha = Integer.parseInt(todos22[4]);
			adqHora = Integer.parseInt(todos22[5]);
			realModeloDB = Integer.parseInt(todos22[6]);
		}
		this.stats = stats;
		this.itemObjevivo = objevivo;
	}

	public Objevivo(int ID, int comidaAno, int comidaFecha, int comidaHora, int humor, int mascara, int tipo, int XP,
			int interAno, int interFecha, int interHoras, int adqAno, int adqFecha, int adqHora, int asociado,
			int realModeloDB, int objevivo, String stats) {
		this.id = ID;
		this.comidaAño = comidaAno;
		this.comidaFecha = comidaFecha;
		this.comidaHora = comidaHora;
		this.humor = humor;
		this.mascara = mascara;
		this.tipo = tipo;
		this.xp = XP;
		this.interAño = interAno;
		this.interFecha = interFecha;
		this.interHora = interHoras;
		this.adqAño = adqAno;
		this.adqFecha = adqFecha;
		this.adqHora = adqHora;
		this.asociado = asociado;
		this.realModeloDB = realModeloDB;
		this.itemObjevivo = objevivo;
		this.stats = stats;

	}

	public int getID() {
		return id;
	}

	public int getFeedYears() {
		return comidaAño;
	}

	public void setFeedYears(int feedyears) {
		this.comidaAño = feedyears;
	}

	public int getFeedDate() {
		return comidaFecha;
	}

	public void setFeedDate(int feeddate) {
		this.comidaFecha = feeddate;
	}

	public int getFeedHours() {
		return comidaHora;
	}

	public void setFeedHours(int feedhours) {
		this.comidaHora = feedhours;
	}

	public int getHumeur() {
		return humor;
	}

	public void setHumeur(int humeurId) {
		this.humor = humeurId;
	}

	public int getMascara() {
		return mascara;
	}

	public void setSkin(int skinId) {
		this.mascara = skinId;
	}

	public int getType() {
		return tipo;
	}

	public void setType(int typeId) {
		this.tipo = typeId;
	}

	public int getXp() {
		return xp;
	}

	public void setXp(int XP) {
		this.xp = XP;
	}

	public int getToYears() {
		return interAño;
	}

	public void setToYears(int toyears) {
		this.interAño = toyears;
	}

	public int getToDate() {
		return interFecha;
	}

	public void setToDate(int todate) {
		this.interFecha = todate;
	}

	public int getToHours() {
		return interHora;
	}

	public void setToHours(int tohours) {
		this.interHora = tohours;
	}

	public int getHasYears() {
		return adqAño;
	}

	public void setHasYears(int hasyears) {
		this.adqAño = hasyears;
	}

	public int getHasDate() {
		return adqFecha;
	}

	public void setHasDate(int hasdate) {
		this.adqFecha = hasdate;
	}

	public int getHasHours() {
		return adqHora;
	}

	public void setHasHours(int hashours) {
		this.adqHora = hashours;
	}

	public int getAsociado() {
		return asociado;
	}

	public void setAsociado(int asociado) {
		this.asociado = asociado;
	}

	public int getrealtemplate() {
		return realModeloDB;
	}

	public void setRealModeloDB(int modelo) {
		this.realModeloDB = modelo;
	}

	public int getItemObjevivo() {
		return itemObjevivo;
	}

	public void setItemObjevivo(int itemObjevivo) {
		this.itemObjevivo = itemObjevivo;
	}

	public String getStat() {
		return stats;
	}

	public void setStat(String stat) {
		this.stats = stat;
	}

	public String convertirAString() {
		String str = "328#" + Integer.toHexString(comidaAño) + "#" + Integer.toHexString(comidaFecha) + "#"
				+ Integer.toHexString(comidaHora) + ",3cb#0#0#" + Integer.toBinaryString(humor) + ",3cc#0#0#"
				+ Integer.toHexString(mascara) + ",3cd#0#0#" + Integer.toHexString(tipo) + ",3ca#0#0#"
				+ Integer.toHexString(realModeloDB) + ",3ce#0#0#" + Integer.toHexString(xp) + ",325#"
				+ Integer.toHexString(adqAño) + "#" + Integer.toHexString(adqFecha) + "#" + Integer.toHexString(adqHora)
				+ ",3d7#" + Integer.toHexString(interAño) + "#" + Integer.toHexString(interFecha) + "#"
				+ Integer.toHexString(interHora);
		return str;
	}
}

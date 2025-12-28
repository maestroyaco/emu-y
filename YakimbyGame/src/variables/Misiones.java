package variables;

import java.util.Map;

import estaticos.Emu;
import estaticos.GestorSQL;
import estaticos.GestorSalida;
import estaticos.MundoDofus;
import variables.Objeto.ObjetoModelo;

public class Misiones {
	public static String getListaMisiones(Personaje _perso) {
		StringBuilder noac = new StringBuilder("");
		StringBuilder ac = new StringBuilder("");
		for (String exm : _perso._misiones.split("\\|")) {
			if (exm.equals("")) {
				continue;
			}
			int misID = Integer.parseInt(exm.split(";")[0]);
			int acabado = Integer.parseInt(exm.split(";")[1]);
			if (acabado == 1) {
				ac.append("|" + misID + ";" + acabado);
			} else {
				noac.append("|" + misID + ";" + acabado);
			}
		}
		return ac.toString() + noac.toString();
	}

	static void addListaMisiones(int mis, Personaje _perso, boolean mensaje) {
		try {
			for (String exm : _perso._misiones.split("\\|")) {
				if (exm.equals("")) {
					continue;
				}
				int misID = Integer.parseInt(exm.split(";")[0]);
				if (misID == mis) {
					if (mensaje) {
						GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "Ya tienes esta misi�n.");
					}
					if (!_perso._objTemporal.equals("")) {
						int idobj = Integer.parseInt(_perso._objTemporal.split(",")[0]);
						int cant = Integer.parseInt(_perso._objTemporal.split(",")[1]);
						if (cant > 0) {
							_perso.removerObjetoPorModYCant(idobj, cant);
							GestorSalida.ENVIAR_Im_INFORMACION(_perso, "022;" + -cant + "~" + idobj);
						} else {
							ObjetoModelo OM = MundoDofus.getObjModelo(idobj);
							if (OM == null) {
								return;
							}
							Objeto obj = OM.crearObjDesdeModelo(-cant, false);
							if (!_perso.addObjetoSimilar(obj, true, -1)) {
								MundoDofus.addObjeto(obj, true);
								_perso.addObjetoPut(obj);
								GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_perso, obj);
							}
							GestorSalida.ENVIAR_Im_INFORMACION(_perso, "021;" + cant + "~" + idobj);
						}
						_perso._objTemporal = "";
						GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso);
					}
					return;
				}
			}
			String objetivos = MundoDofus.getMisionObj(mis);
			String toaddQ = "";
			if (_perso.get_misiones() != "") {
				toaddQ = "|" + mis + ";0";
			} else {
				toaddQ = mis + ";0";
			}
			_perso._misiones += toaddQ;
			StringBuilder newobj = new StringBuilder("");
			boolean first = true;
			for (String splitob : objetivos.split(";")) {
				if (splitob.equals("")) {
					continue;
				}
				if (first) {
					first = false;
					newobj.append(splitob + ",0");
				} else {
					newobj.append(";" + splitob + ",0");
				}
			}
			String toaddO = "|" + mis + "-" + newobj;
			_perso._objMisiones += toaddO;
			GestorSQL.ACTUALIZAR_NUEVA_MISION(_perso.getID(), _perso.getObjMisiones(), getListaMisiones(_perso));
			GestorSalida.ENVIAR_NUEVA_MISION(_perso, mis);
		} catch (Exception e) {
			Emu.creaLogs(e);
		}
	}

	public static String getObjetivoMision(int str, Personaje _perso) {
		String newobj = "";
		try {
			String bla = "";
			String stra = "";
			boolean reemplaza = false;
			for (String objMis : _perso._objMisiones.split("\\|")) {
				if (objMis.equals("") || !newobj.equals("")) {
					continue;
				}
				int misID = Integer.parseInt(objMis.split("-")[0]);
				if (misID != str) {
					continue;
				}
				String objRIDM = String.valueOf(MundoDofus.getMisionObj(misID));
				int tienel = objMis.split(";").length;
				int totl = objRIDM.split(";").length;
				if (tienel < objRIDM.split(";").length) {
					bla = objMis;
					stra = arreglaMision(str, _perso, tienel, totl);
					reemplaza = true;
				}
				if (MundoDofus.getMisionEtapa(misID) != null) {
					String etapa = MundoDofus.getMisionEtapa(misID).replace(" ", "");
					String newsp = objMis.split("-")[1];
					newobj = misID + "|" + etapa.split("-")[0] + "|" + newsp + "|||17" /* + etapa.split("-")[4] */;
				}
				break;
			}
			if (reemplaza) {
				String newobjx = _perso._objMisiones.replace(bla, stra);
				_perso._objMisiones = newobjx;
			}
		} catch (Exception e) {
			Emu.creaLogs(e);
		}
		return newobj;
	}

	private static String arreglaMision(int str, Personaje _perso, int cuantas, int total) {
		String newobj = "";
		String newfinal = "";
		try {
			for (String objMis : _perso._objMisiones.split("\\|")) {
				if (objMis.equals("") || !newobj.equals("")) {
					continue;
				}
				int misID = Integer.parseInt(objMis.split("-")[0]);
				if (misID != str) {
					continue;
				}
				String objRIDM = String.valueOf(MundoDofus.getMisionObj(misID));
				if (objMis.split(";").length < objRIDM.split(";").length) {
					newfinal = objMis;
					newobj = objMis;
				}
				break;
			}
			int ignora = 0;
			for (String objMis : MundoDofus.getMisionObj(str).split(";")) {
				ignora += 1;
				if (ignora <= cuantas) {
					continue;
				}
				newfinal += ";" + objMis + ",0";
			}
		} catch (Exception e) {
			Emu.creaLogs(e);
		}
		return newfinal;
	}

	public static boolean checkMision(Personaje _perso, int valor) {
		boolean returna = false;
		for (String misiones : _perso._misiones.split("\\|")) {
			if (misiones.equals("")) {
				continue;
			}
			int misID = Integer.parseInt(misiones.split(";")[0]);
			int acabado = Integer.parseInt(misiones.split(";")[1]);
			if (acabado == 0) {
				returna = comprobarMision(misID, _perso);
			}
		}
		if (returna) {
			return true;
		} else {
			return false;
		}
	}

	private static boolean comprobarMision(int mision, Personaje _perso) {
		boolean paso = false;
		boolean acabo = true;
		boolean recom = false;
		boolean returna = false;
		for (String objMis : _perso._objMisiones.split("\\|")) {
			if (objMis.equals("")) {
				continue;
			}
			if (paso) {
				break;
			}
			int objMID = Integer.parseInt(objMis.split("-")[0]);
			if (objMID == mision) {
				String objetivosMis = objMis.split("-")[1];
				for (String splitob : objetivosMis.split(";")) {
					if (splitob.equals("")) {
						continue;
					}
					if (paso) {
						acabo = false;
						continue;
					}
					if (splitob.equals("")) {
						continue;
					}
					int objrID = Integer.parseInt(splitob.split(",")[0]);
					if (MundoDofus.getMisionObjs(objrID) == null) {
						continue;
					}
					int estado = Integer.parseInt(splitob.split(",")[1]);
					if (estado == 0) {
						paso = true;
						String objRIDM = String.valueOf(MundoDofus.getMisionObjs(objrID));
						int tipo = Integer.parseInt(objRIDM.split("-")[0]);
						String objetos = objRIDM.split("-")[1];
						int npc = 0;
						String monstruos = objRIDM.split("-")[3];
						String acabam = objRIDM.split("-")[4];
						int darmis = Integer.parseInt(objRIDM.split("-")[5]);
						switch (tipo) {
						case 1:
						case 9:
							npc = Integer.parseInt(objRIDM.split("-")[2].split(";")[0]);
							int mapaid = 0;
							try {
								mapaid = Integer.parseInt(objRIDM.split("-")[2].split(";")[1]);
							} catch (Exception e) {
								mapaid = 0;
							}
							if (mapaid == 0) {
								mapaid = _perso.getMapa().getID();
							}
							if (_perso.gethablandoNPC() == npc && mapaid == _perso.getMapa().getID() && !recom) {
								if (Integer.parseInt(acabam) == 0) {
									recom = true;
									GestorSalida.ENVIAR_ACTUALIZAR_MISION(_perso, mision);
									returna = actualizarObjetivo(mision, objrID, _perso);
									if (darmis != 0) {
										addListaMisiones(darmis, _perso, false);
									}
								} else {
									boolean tiene = false;
									if (Integer.parseInt(acabam) != 0) {
										for (String dialg : acabam.split(";")) {
											if (dialg.contains("" + _perso.getultDialog())) {
												tiene = true;
											}
										}
										if (tiene) {
											recom = true;
											GestorSalida.ENVIAR_ACTUALIZAR_MISION(_perso, mision);
											returna = actualizarObjetivo(mision, objrID, _perso);
										}
									}
									if (darmis != 0) {
										addListaMisiones(darmis, _perso, false);
									}
								}
							}
							break;
						case 3:
							npc = Integer.parseInt(objRIDM.split("-")[2]);
							if (objetos.equals("0")) {
								break;
							}
							boolean tieneitems = false;
							for (String items : objetos.split(";")) {
								if (items.isEmpty()) {
									continue;
								}
								int objid = Integer.parseInt(items.split(",")[0]);
								int cant = Integer.parseInt(items.split(",")[1]);
								if (_perso.tieneObjModeloNoEquip(objid, cant)) {
									tieneitems = true;
									break;
								} else {
									tieneitems = false;
								}
							}
							if (tieneitems && !recom && _perso.gethablandoNPC() == npc) {
								for (String items : objetos.split(";")) {
									if (items.isEmpty()) {
										continue;
									}
									int objid = Integer.parseInt(items.split(",")[0]);
									int cant = Integer.parseInt(items.split(",")[1]);
									_perso.removerObjetoPorModYCant(objid, cant);
								}
								recom = true;
								GestorSalida.ENVIAR_ACTUALIZAR_MISION(_perso, mision);
								returna = actualizarObjetivo(mision, objrID, _perso);
							}
							break;
						case 6:
						case 7: // monstruos
							Map<Integer, Integer> mobsmat = _perso.getMobMatado();
							int mobid = Integer.parseInt(monstruos.split(",")[0]);
							int cant = Integer.parseInt(monstruos.split(",")[1]);
							if (mobsmat.containsKey(mobid)) {
								if (mobsmat.get(mobid) >= cant) {
									recom = true;
									GestorSalida.ENVIAR_ACTUALIZAR_MISION(_perso, mision);
									returna = actualizarObjetivo(mision, objrID, _perso);
								}
							}
							break;
						}
					}
				}
				if (recom && acabo && paso) {
					acaboMision(mision, _perso);
					GestorSalida.ENVIAR_ACABAR_MISION(_perso, mision);
					darPremio(mision, _perso);
					GestorSQL.ACTUALIZAR_NUEVA_MISION(_perso.getID(), _perso.getObjMisiones(),
							getListaMisiones(_perso));
				} else {
					GestorSQL.ACTUALIZAR_NUEVA_MISION(_perso.getID(), _perso.getObjMisiones(),
							getListaMisiones(_perso));
				}
			}
		}
		if (returna) {
			return true;
		} else {
			return false;
		}
	}

	private static void darPremio(int mision, Personaje _perso) {
		String var = MundoDofus.getMisionEtapa(mision);
		int kamas = Integer.parseInt(var.split("-")[1]);
		String objetos = var.split("-")[2];
		long expAgregar = Long.parseLong(var.split("-")[3]);
		if (kamas != 0) {
			long tempKamas = _perso.getKamas();
			long nuevasKamas = tempKamas + kamas;
			if (nuevasKamas < 0) {
				nuevasKamas = 0;
			}
			_perso.setKamas(nuevasKamas);
			if (kamas > 0) {
				GestorSalida.ENVIAR_Im_INFORMACION(_perso, "045;" + kamas);
			} else {
				GestorSalida.ENVIAR_Im_INFORMACION(_perso, "046;" + kamas);
			}
		}
		if (expAgregar != 0) {
			if (expAgregar < 1) {
				return;
			}
			long totalXp = _perso.getExperiencia() + expAgregar;
			_perso.addExp(totalXp, false);
			GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
			GestorSalida.ENVIAR_Im_INFORMACION(_perso, "08;" + expAgregar);
		}
		if (!objetos.isEmpty() && !objetos.equals("0")) {
			for (String item : objetos.split(";")) {
				if (item.isEmpty()) {
					continue;
				}
				String[] infos = item.split(",");
				ObjetoModelo OT = MundoDofus.getObjModelo(Integer.parseInt(infos[0]));
				Objeto objeto = OT.crearObjDesdeModelo(Integer.parseInt(infos[1]), false);
				if (objeto == null) {
					return;
				}
				if (!_perso.addObjetoSimilar(objeto, true, -1)) {
					MundoDofus.addObjeto(objeto, true);
					_perso.addObjetoPut(objeto);
					GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_perso, objeto);
				}
				GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso);
				GestorSalida.ENVIAR_Im_INFORMACION(_perso, "021;" + 1 + "~" + Integer.parseInt(infos[0]) + ";");
			}
		}
		_perso.PuntosPrestigio += Emu.ptsQuest;
		GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
	}

	private static void acaboMision(int mision, Personaje _perso) {
		StringBuilder newmis = new StringBuilder("");
		for (String objMis : _perso._misiones.split("\\|")) {
			if (objMis.equals("")) {
				continue;
			}
			int misID = Integer.parseInt(objMis.split(";")[0]);
			int acabado = Integer.parseInt(objMis.split(";")[1]);
			if (misID == mision) {
				newmis.append("|" + misID + ";1");
			} else {
				newmis.append("|" + misID + ";" + acabado);
			}
		}
		_perso._misiones = newmis.toString();
	}

	private static boolean actualizarObjetivo(int mision, int objetivo, Personaje _perso) {
		StringBuilder newobj = new StringBuilder("");
		StringBuilder objo = new StringBuilder("");
		boolean paso = false;
		boolean trim = false;
		for (String objMis : _perso._objMisiones.split("\\|")) {
			if (objMis.equals("")) {
				continue;
			}
			int misID = Integer.parseInt(objMis.split("-")[0]);
			String obj = objMis.split("-")[1];
			if (misID == mision) {
				for (String splitob : obj.split(";")) {
					if (splitob.equals("")) {
						continue;
					}
					int objmID = Integer.parseInt(splitob.split(",")[0]);
					int acabado = Integer.parseInt(splitob.split(",")[1]);
					if (objmID == objetivo && !trim && acabado == 0) {
						if (!paso) {
							objo.append(objmID + ",1");
						} else {
							objo.append(";" + objmID + ",1");
						}
						trim = true;
						paso = true;
					} else {
						if (!paso) {
							objo.append(objmID + "," + acabado);
						} else {
							objo.append(";" + objmID + "," + acabado);
						}
						paso = true;
					}
				}
				newobj.append("|" + misID + "-" + objo);
			} else {
				newobj.append("|" + misID + "-" + obj);
			}
		}
		_perso._objMisiones = newobj.toString();
		return true;
	}
}
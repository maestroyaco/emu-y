package variables;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.Timer;

import estaticos.CentroInfo;
import estaticos.CondicionJugador;
import estaticos.Emu;
import estaticos.Encriptador;
import estaticos.Formulas;
import estaticos.GestorSQL;
import estaticos.GestorSalida;
import estaticos.MundoDofus;
import servidor.Colores;
import servidor.Estaticos;
import variables.Objeto.ObjetoModelo;
import variables.Oficio.StatsOficio;

@SuppressWarnings("unused")
public class Accion {
	private int _ID;
	public String _args;
	private String _cond;

	public Accion(final int id, final String args, final String cond) {
		this._ID = id;
		this._args = args;
		this._cond = cond;
	}

	public String getCondiciones() {
		return this._cond;
	}

	public void aplicar(final Personaje perso, final Personaje objetivo, final int objUsadoID, final int celda) {
		if ((perso == null) || (perso.getCuenta().getEntradaPersonaje() == null)) {
			return;
		}
		if (!this._cond.equalsIgnoreCase("") && !this._cond.equalsIgnoreCase("-1")
				&& !CondicionJugador.validaCondiciones(perso, this._cond)) {
			GestorSalida.ENVIAR_Im_INFORMACION(perso, "119");
			return;
		}
		final PrintWriter out = perso.getCuenta().getEntradaPersonaje().getOut();
		if (this._ID != 1 && perso.getConversandoCon() != 0) {
			GestorSalida.ENVIAR_DV_FINALIZAR_DIALOGO(perso.getCuenta().getEntradaPersonaje().getOut());
			perso.setConversandoCon(0);
			if (perso.gethablandoNPC() != 0) {
				perso.sethablandoNPC(0);
			}
		}
		switch (this._ID) {
		case -2: { // crear gremio bug
			try {
				if (perso.estaOcupado()) {
					return;
				}
				if (perso.getGremio() != null || perso.getMiembroGremio() != null) {
					GestorSalida.ENVIAR_gC_CREAR_PANEL_GREMIO(perso, "Ea");
					return;
				}
				GestorSalida.ENVIAR_gn_CREAR_GREMIO(perso);
			} catch (final Exception e) {
			}
			break;
		}
		case -22:// Crear Gremio Fix
			if (perso.estaOcupado()) {
				return;
			}
			if (perso.getGremio() != null || perso.getMiembroGremio() != null) {
				GestorSalida.ENVIAR_gC_CREAR_PANEL_GREMIO(perso, "Ea");
				return;
			}
			break;
		case -1: {
			try {
				GestorSQL.SALVAR_PERSONAJE(perso, true);
				if (perso.getDeshonor() >= 1) {
					GestorSalida.ENVIAR_Im_INFORMACION(perso, "183");
					return;
				}
				final int costo = perso.getCostoAbrirBanco();
				if (costo > 0) {
					final long nKamas = perso.getKamas() - costo;
					if (nKamas < 0L) {
						GestorSalida.ENVIAR_Im_INFORMACION(perso, "1128;" + costo);
						GestorSalida.ENVIAR_M1_MENSAJE_SERVER(perso, "10", "", "");
						return;
					}
					perso.setKamas(nKamas);
					GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso);
					GestorSalida.ENVIAR_Im_INFORMACION(perso, "020;" + costo);
				}
				GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(perso, 5, "");
				GestorSalida.ENVIAR_EL_LISTA_OBJETOS_BANCO(perso);
				perso.setOcupado(true);
				perso.setEnBanco(true);
			} catch (final Exception ex2) {
			}
			break;
		}
		case 590: {
			try {
				final short newMapID = Short.parseShort(this._args.split(",")[0]);
				final int newCellID = Integer.parseInt(this._args.split(",")[1]);
				final int ObjetNeed1 = Integer.parseInt(this._args.split(",")[2]);
				final int ObjetNeed2 = Integer.parseInt(this._args.split(",")[3]);
				if (perso.tieneObjModeloNoEquip(ObjetNeed1, 1) && perso.tieneObjModeloNoEquip(ObjetNeed2, 1)) {
					perso.teleport(newMapID, newCellID);
					perso.removerObjetoPorModYCant(ObjetNeed1, 1);
					perso.removerObjetoPorModYCant(ObjetNeed2, 1);
					GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(perso);
					break;
				}
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "No posees las llaves necesarias.", "C60914");
				break;
			} catch (final Exception e) {
			}
		}
		case 59: {
			try {
				final int idSet = Integer.parseInt(this._args.split(";")[0]);
				final int fichas = Integer.parseInt(this._args.split(";")[1]);
				final MundoDofus.ItemSet OS = MundoDofus.getItemSet(idSet);
				if (OS == null) {
					return;
				}
				if (perso.tieneObjModeloNoEquip(1749, fichas)) {
					perso.removerObjetoPorModYCant(1749, fichas);
					for (final Objeto.ObjetoModelo objM : OS.getObjetosModelos()) {
						final Objeto obj = objM.crearObjDesdeModelo(1, false);
						if (!objetivo.addObjetoSimilar(obj, true, -1)) {
							MundoDofus.addObjeto(obj, true);
							objetivo.addObjetoPut(obj);
							GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(objetivo, obj);
						}
						GestorSalida.ENVIAR_Im_INFORMACION(perso, "021;1~" + objM.getID());
					}
				} else {
					GestorSalida.ENVIAR_Im_INFORMACION(perso, "14|43");
				}
				GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(objetivo);
			} catch (final Exception ex3) {
			}
			return;
		}
		case 0: {
			try {
				if (perso.getHaciendoTrabajo() != null || perso.getRompiendo() || !perso.usaTP) {
					return;
				}
				if (!Estaticos.compruebaTps(perso)) {
					return;
				}
				final int anteriormapa = perso.getMapa().getID();
				final short nuevoMapaID = Short.parseShort(this._args.split(",", 2)[0]);
				final int nuevaCeldaID = Integer.parseInt(this._args.split(",", 2)[1]);
				perso.teleport(nuevoMapaID, nuevaCeldaID);
				if (perso.liderMaitre) {
					final Timer timer = new Timer(500, new ActionListener() {
						@Override
						public void actionPerformed(final ActionEvent e) {
							((Timer) e.getSource()).stop();
							for (final Personaje pjx : perso.getGrupo().getPersos()) {
								if (pjx == perso) {
									continue;
								}
								if (pjx.getMapa().getID() != anteriormapa) {
									continue;
								}
								pjx.teleport(nuevoMapaID, nuevaCeldaID);
							}
						}
					});
					timer.start();
					break;
				}
				break;
			} catch (final Exception e) {
			}
		}
		case 528: {
			GestorSalida.enviar(perso, "#Ap");
			break;
		}
		case 1: {
			try {
				if (this._args.equalsIgnoreCase("DV")) {
					GestorSalida.ENVIAR_DV_FINALIZAR_DIALOGO(out);
					perso.setConversandoCon(0);
				} else {
					int qID = -1;
					try {
						qID = Integer.parseInt(this._args);
					} catch (final NumberFormatException ex4) {
					}
					final NPCModelo.PreguntaNPC quest = MundoDofus.getNPCPregunta(qID);
					if (quest == null) {
						GestorSalida.ENVIAR_DV_FINALIZAR_DIALOGO(out);
						perso.setConversandoCon(0);
						return;
					}
					GestorSalida.ENVIAR_DQ_DIALOGO_PREGUNTA(out, quest.stringArgParaDialogo(perso));
				}
			} catch (final Exception ex5) {
			}
			break;
		}
		case 2: {
			try {
				final String quitar = this._args.split(";")[0];
				final String[] azar = this._args.split(";")[1].split("\\|");
				final int id = Integer.parseInt(quitar.split(",")[0]);
				final int cant = Integer.parseInt(quitar.split(",")[1]);
				if (cant < 0) {
					perso.removerObjetoPorModYCant(id, -cant);
					GestorSalida.ENVIAR_Im_INFORMACION(perso, "022;" + -cant + "~" + id);
				}
				final String objetoazar = azar[Formulas.getRandomValor(0, azar.length - 1)];
				final int ID = Integer.parseInt(objetoazar.split(",")[0]);
				final int cantidad = Integer.parseInt(objetoazar.split(",")[1]);
				boolean enviar = true;
				if (objetoazar.split(",").length > 2) {
					enviar = objetoazar.split(",")[2].equals("1");
				}
				if (cantidad > 0) {
					final Objeto.ObjetoModelo OM = MundoDofus.getObjModelo(ID);
					if (OM == null) {
						return;
					}
					final Objeto obj2 = OM.crearObjDesdeModelo(cantidad, false);
					if (!perso.addObjetoSimilar(obj2, true, -1)) {
						MundoDofus.addObjeto(obj2, true);
						perso.addObjetoPut(obj2);
						GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(out, obj2);
					}
				} else {
					perso.removerObjetoPorModYCant(ID, -cantidad);
				}
				if (perso.enLinea() && enviar) {
					if (cantidad >= 0) {
						GestorSalida.ENVIAR_Im_INFORMACION(perso, "021;" + cantidad + "~" + ID);
					} else if (cantidad < 0) {
						GestorSalida.ENVIAR_Im_INFORMACION(perso, "022;" + -cantidad + "~" + ID);
					}
				}
				GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(perso);
			} catch (final Exception ex6) {
			}
			break;
		}
		case 4: {
			try {
				final long cant2 = Integer.parseInt(this._args);
				final long tempKamas = perso.getKamas();
				long nuevasKamas = tempKamas + cant2;
				if (nuevasKamas < 0L) {
					nuevasKamas = 0L;
				}
				perso.setKamas(nuevasKamas);
				if (perso.enLinea()) {
					GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso);
				}
			} catch (final Exception ex7) {
			}
			break;
		}
		case 52: {
			final Objeto objf = MundoDofus.getObjeto(objUsadoID);
			if (objf == null) {
				break;
			}
			perso.removerObjetoPorModYCant(objf.getModelo().getID(), 1);
			if (perso.enLinea()) {
				GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(perso);
				GestorSalida.ENVIAR_Im_INFORMACION(perso, "022;-1~" + objf.getModelo().getID());
				break;
			}
			break;
		}
		case 5: {
			try {
				final int id2 = Integer.parseInt(this._args.split(",")[0]);
				final int cant3 = Integer.parseInt(this._args.split(",")[1]);
				boolean send = true;
				if (this._args.split(",").length > 2) {
					send = this._args.split(",")[2].equals("1");
				}
				if (cant3 > 0) {
					final Objeto.ObjetoModelo OM2 = MundoDofus.getObjModelo(id2);
					if (OM2 == null) {
						return;
					}
					final Objeto obj = OM2.crearObjDesdeModelo(cant3, false);
					if (!perso.addObjetoSimilar(obj, true, -1)) {
						MundoDofus.addObjeto(obj, true);
						perso.addObjetoPut(obj);
						GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(out, obj);
					}
				} else {
					perso.removerObjetoPorModYCant(id2, -cant3);
				}
				if (perso.enLinea()) {
					GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(perso);
					if (send) {
						if (cant3 >= 0) {
							GestorSalida.ENVIAR_Im_INFORMACION(perso, "021;" + cant3 + "~" + id2);
						} else if (cant3 < 0) {
							GestorSalida.ENVIAR_Im_INFORMACION(perso, "022;" + -cant3 + "~" + id2);
						}
					}
				}
				perso.setObjTemporal(id2, cant3);
			} catch (final Exception ex8) {
			}
			break;
		}
		case 550: {
			try {
				final int id2 = Integer.parseInt(this._args.split(",")[0]);
				final int cant3 = Integer.parseInt(this._args.split(",")[1]);
				boolean send = true;
				if (this._args.split(",").length > 2) {
					send = this._args.split(",")[2].equals("1");
				}
				if (cant3 > 0) {
					final Objeto.ObjetoModelo OM2 = MundoDofus.getObjModelo(id2);
					if (OM2 == null) {
						return;
					}
					final Objeto obj = OM2.crearObjDesdeModelo(cant3, false);
					if (!perso.addObjetoSimilar(obj, true, -1)) {
						MundoDofus.addObjeto(obj, true);
						perso.addObjetoPut(obj);
						GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(out, obj);
					}
				} else {
					perso.removerObjetoPorModYCant(id2, -cant3);
				}
				if (perso.enLinea()) {
					GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(perso);
					if (send) {
						if (cant3 >= 0) {
							GestorSalida.ENVIAR_Im_INFORMACION(perso, "021;" + cant3 + "~" + id2);
						} else if (cant3 < 0) {
							GestorSalida.ENVIAR_Im_INFORMACION(perso, "022;" + -cant3 + "~" + id2);
						}
					}
				}
				perso.setObjTemporal(id2, cant3);
			} catch (final Exception ex9) {
			}
			break;
		}
		case 6: {
			try {
				final int mID = Integer.parseInt(this._args);
				if (MundoDofus.getOficio(mID) == null) {
					return;
				}
				if (mID == 2 || mID == 11 || mID == 13 || mID == 14 || mID == 15 || mID == 16 || mID == 17 || mID == 18
						|| mID == 19 || mID == 20 || mID == 24 || mID == 25 || mID == 26 || mID == 27 || mID == 28
						|| mID == 31 || mID == 36 || mID == 41 || mID == 56 || mID == 58 || mID == 60 || mID == 65) {
					if (perso.getOficioPorID(mID) != null) {
						GestorSalida.ENVIAR_Im_INFORMACION(perso, "111");
						return;
					}
					perso.aprenderOficio(MundoDofus.getOficio(mID), perso.getStatsOficios().size() + 1, true);
				} else {
					if (mID != 43 && mID != 44 && mID != 45 && mID != 46 && mID != 47 && mID != 48 && mID != 49
							&& mID != 50 && mID != 62 && mID != 63 && mID != 64) {
						break;
					}
					if (perso.getOficioPorID(mID) != null) {
						GestorSalida.ENVIAR_Im_INFORMACION(perso, "111");
						return;
					}
					perso.aprenderOficio(MundoDofus.getOficio(mID), perso.getStatsOficios().size() + 1, true);
				}
			} catch (final Exception ex10) {
				break;
			}
		}
		case 7: {
			if (perso.getPelea() == null) {
				perso.retornoPtoSalvadaPocima();
				break;
			}
			break;
		}
		case 8: {
			try {
				final int statID = Integer.parseInt(this._args.split(",", 2)[0]);
				final int cantidad2 = Integer.parseInt(this._args.split(",", 2)[1]);
				int mensajeID = 0;
				switch (statID) {
				case 124: {
					if (perso.getScrollSabiduria() >= 500) {
						GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso,
								"Llegaste al mÁximo de scroll en esta caracter�stica.");
						return;
					}
					perso.addScrollSabiduria(cantidad2);
					mensajeID = 9;
					break;
				}
				case 118: {
					if (perso.getScrollFuerza() >= 500) {
						GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso,
								"Llegaste al mÁximo de scroll en esta caracter�stica.");
						return;
					}
					perso.addScrollFuerza(cantidad2);
					mensajeID = 10;
					break;
				}
				case 123: {
					if (perso.getScrollSuerte() >= 500) {
						GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso,
								"Llegaste al mÁximo de scroll en esta caracter�stica.");
						return;
					}
					perso.addScrollSuerte(cantidad2);
					mensajeID = 11;
					break;
				}
				case 119: {
					if (perso.getScrollAgilidad() >= 500) {
						GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso,
								"Llegaste al mÁximo de scroll en esta caracter�stica.");
						return;
					}
					perso.addScrollAgilidad(cantidad2);
					mensajeID = 12;
					break;
				}
				case 125: {
					if (perso.getScrollVitalidad() >= 500) {
						GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso,
								"Llegaste al mÁximo de scroll en esta caracter�stica.");
						return;
					}
					perso.addScrollVitalidad(cantidad2);
					mensajeID = 13;
					break;
				}
				case 126: {
					if (perso.getScrollInteligencia() >= 500) {
						GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso,
								"Llegaste al mÁximo de scroll en esta caracter�stica.");
						return;
					}
					perso.addScrollInteligencia(cantidad2);
					mensajeID = 14;
					break;
				}
				}
				perso.getBaseStats().addUnStat(statID, cantidad2);
				GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso);
				if (mensajeID > 0) {
					GestorSalida.ENVIAR_Im_INFORMACION(perso, "0" + mensajeID + ";" + cantidad2);
				}
			} catch (final Exception ex11) {
			}
			break;
		}
		case 9: {
			try {
				final int sID = Integer.parseInt(this._args);
				if (MundoDofus.getHechizo(sID) == null) {
					return;
				}
				perso.aprenderHechizo(sID, 1, false, true);
			} catch (final Exception ex12) {
			}
			break;
		}
		case 10: {
			try {
				final int min = Integer.parseInt(this._args.split(",", 2)[0]);
				int max = Integer.parseInt(this._args.split(",", 2)[1]);
				int val = 0;
				if (max == 0) {
					max = min;
				}
				if (min == -1 && max == -1) {
					val = perso.getPDVMAX();
				} else {
					val = Formulas.getRandomValor(min, max);
				}
				if (objetivo != null) {
					if (objetivo.getPDV() + val > objetivo.getPDVMAX()) {
						val = objetivo.getPDVMAX() - objetivo.getPDV();
					}
					objetivo.setPDV(objetivo.getPDV() + val);
					objetivo.agregarEnergia(10000);
					GestorSalida.ENVIAR_As_STATS_DEL_PJ(objetivo);
				} else {
					if (perso.getPDV() + val > perso.getPDVMAX()) {
						val = perso.getPDVMAX() - perso.getPDV();
					}
					perso.setPDV(perso.getPDV() + val);
					perso.agregarEnergia(10000);
					GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso);
					if (perso.getPelea() != null) {
						GestorSalida.ENVIAR_GTM_INFO_STATS_TODO_LUCHADORES(perso.getPelea(), perso);
					}
				}
			} catch (final Exception ex13) {
			}
			break;
		}
		case 11: {
			try {
				final byte nuevaAlin = Byte.parseByte(this._args.split(",", 2)[0]);
				final boolean remplaza = Integer.parseInt(this._args.split(",", 2)[1]) == 1;
				if (perso.getAlineacion() != -1 && !remplaza) {
					return;
				}
				perso.modificarAlineamiento(nuevaAlin);
			} catch (final Exception ex14) {
			}
			break;
		}
		case 12: {
			break;
		}
		case 13: {
			try {
				perso.resetearStats();
				final int mires = 1;
				final int captotal = perso.getNivel() * 5;
				perso.setCapital(captotal + mires);
				GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso);
			} catch (final Exception ex15) {
			}
			break;
		}
		case 14: {
			try {
				perso.setOlvidandoHechizo(true);
				GestorSalida.GAME_SEND_FORGETSPELL_INTERFACE('+', perso);
			} catch (final Exception ex16) {
			}
			break;
		}
		case 15: {
			try {

				final short nuevoMapaID = Short.parseShort(this._args.split(",")[0]);
				final int nuevaCeldaID = Integer.parseInt(this._args.split(",")[1]);
				final int objNecesario = Integer.parseInt(this._args.split(",")[2]);
				final int mapaNecesario = Integer.parseInt(this._args.split(",")[3]);
				if (objNecesario == 0) {
					perso.teleport(nuevoMapaID, nuevaCeldaID);
					return;
				}
				if (objNecesario <= 0) {
					break;
				}

				//mitre  mazmorr accion 15

				if (perso.liderMaitre) {
					final Timer timer = new Timer(500, new ActionListener() {
						@Override
						public void actionPerformed(final ActionEvent e) {
							((Timer) e.getSource()).stop();
							for (final Personaje pjx : perso.getGrupo().getPersos()) {
								//codigo original
								if (pjx.tieneObjModeloNoEquip(objNecesario, 1)) {
									pjx.removerObjetoPorModYCant(objNecesario, 1);
									GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(perso);
									if (mapaNecesario == 0) {
										pjx.teleport(nuevoMapaID, nuevaCeldaID);
									} else if (mapaNecesario > 0) {
										if (pjx.getMapa().getID() == mapaNecesario) {
											pjx.teleport(nuevoMapaID, nuevaCeldaID);
										} else if (pjx.getMapa().getID() != mapaNecesario) {
											GestorSalida.ENVIAR_Im_INFORMACION(pjx, "113");
										}
									}
									//return;

								}else {
									GestorSalida.ENVIAR_Im_INFORMACION(pjx, "14|45");
								}

								//codigo original
							}
						}
					});
					timer.start();
					break;
				}else {
					if (perso.tieneObjModeloNoEquip(objNecesario, 1)) {
						perso.removerObjetoPorModYCant(objNecesario, 1);
						GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(perso);
						if (mapaNecesario == 0) {
							perso.teleport(nuevoMapaID, nuevaCeldaID);
						} else if (mapaNecesario > 0) {
							if (perso.getMapa().getID() == mapaNecesario) {
								perso.teleport(nuevoMapaID, nuevaCeldaID);
							} else if (perso.getMapa().getID() != mapaNecesario) {
								GestorSalida.ENVIAR_Im_INFORMACION(perso, "113");
							}
						}
						return;
					} else {
						GestorSalida.ENVIAR_Im_INFORMACION(perso, "14|45");
						return;
					}
				}






				//maitre  mazmorra accion 15





			} catch (final Exception ex17) {
				break;
			}
		}
		case 16: {
			if (perso.getAlineacion() != 0) {
				final int addHonor = Integer.parseInt(this._args);
				perso.addHonor(addHonor);
				break;
			}
			break;
		}
		case 60: {
			boolean existe = false;
			for (final Personaje persox : MundoDofus.getMapa(perso.getMapa().getID()).getPersos()) {
				if ((persox.esFantasma() && persox.enLinea()) || persox.getGfxID() == 8004) {
					persox.setRevivir();
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "Acabas de revivir a <b>+" + persox.getNombre() + "</b>",
							"237B3B");
					existe = true;
				}
			}
			if (existe) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "No hay nadie a qui�n revivir", "C60914");
				break;
			}
			break;
		}
		case 61: {
			if (objetivo != null && objetivo.esTumba() && objetivo.enLinea()) {
				objetivo.setFantasma();
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "Acabas de liberar a <b>+" + objetivo.getNombre() + "</b>",
						"237B3B");
				break;
			}
			break;
		}
		case 17: {
			final int JobID = Integer.parseInt(this._args.split(",")[0]);
			final int XpValue = Integer.parseInt(this._args.split(",")[1]);
			if (perso.getOficioPorID(JobID) != null) {
				perso.getOficioPorID(JobID).addXP(perso, XpValue);
			}
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "Acabas de ganar <b>+" + XpValue + "</b> puntos de experiencia",
					"237B3B");
			break;
		}
		case 18: {
			if (!Casa.tieneOtraCasa(perso)) {
				break;
			}
			final Objeto obj3 = MundoDofus.getObjeto(objUsadoID);
			if (!perso.tieneObjModeloNoEquip(obj3.getModelo().getID(), 1)) {
				break;
			}
			perso.removerObjetoPorModYCant(obj3.getModelo().getID(), 1);
			final Casa h = Casa.getCasaDePj(perso);
			if (h == null) {
				return;
			}
			perso.teleport((short) h.getMapaIDDentro(), h.getCeldaIDDentro());
			break;
		}
		case 19: {
			GestorSalida.GAME_SEND_GUILDHOUSE_PACKET(perso);
			break;
		}
		case 20: {
			final int pts = Integer.parseInt(this._args);
			if (pts < 1) {
				return;
			}
			perso.addPuntosHechizos(pts);
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "Acabas de ganar <b>+" + pts + "</b> punto/s de hechizo",
					"237B3B");
			GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso);
			break;
		}
		case 21: {
			final int energia = Integer.parseInt(this._args);
			if (energia < 1) {
				return;
			}
			perso.agregarEnergia(energia);
			GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso);
			break;
		}
		case 22: {
			final int expAgregar = Short.parseShort(this._args.split(",")[0]);
			final int expAgregarmax = Integer.parseInt(this._args.split(",")[1]);
			final long expAgregarf = Formulas.getRandomValor(expAgregar, expAgregarmax);
			if (expAgregarf < 1L) {
				return;
			}
			perso.addExp(expAgregarf, false);
			GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso);
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso,
					"Acabas de ganar <b>+" + expAgregarf + "</b> puntos de experiencia", "237B3B");
			break;
		}
		case 23: {
			final int oficio = Integer.parseInt(this._args);
			if (oficio < 1) {
				return;
			}
			final Oficio.StatsOficio statsOficio = perso.getOficioPorID(oficio);
			if (statsOficio == null) {
				return;
			}
			final int pos = statsOficio.getPosicion();
			perso.olvidarOficio(pos);
			GestorSalida.ENVIAR_JR_OLVIDAR_OFICIO(perso, oficio);
			GestorSQL.SALVAR_PERSONAJE(perso, false);
			break;
		}
		case 24: {
			final int gfxID = Integer.parseInt(this._args);
			if (gfxID < 0) {
				return;
			}
			perso.setGfxID(gfxID);
			GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(perso.getMapa(), perso.getID());
			GestorSalida.ENVIAR_GM_AGREGAR_PJ_A_TODOS(perso.getMapa(), perso);
			break;
		}
		case 25: {
			final int gfxOriginal = perso.getClase(true) * 10 + perso.getSexo();
			perso.setGfxID(gfxOriginal);
			GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(perso.getMapa(), perso.getID());
			GestorSalida.ENVIAR_GM_AGREGAR_PJ_A_TODOS(perso.getMapa(), perso);
			break;
		}
		case 26: {
			GestorSalida.GAME_SEND_GUILDENCLO_PACKET(perso);
			break;
		}
		case 62: {
			try {
				final short nuevoMapaID2 = Short.parseShort(this._args.split(",", 2)[0]);
				final int nuevaCeldaID2 = Integer.parseInt(this._args.split(",", 2)[1]);
				if (perso.tieneObjModeloNoEquip(300030, 1) && perso.tieneObjModeloNoEquip(300040, 1)
						&& perso.tieneObjModeloNoEquip(300050, 1) && perso.tieneObjModeloNoEquip(300060, 1)) {
					perso.teleport(nuevoMapaID2, nuevaCeldaID2);
					perso.removerObjetoPorModYCant(300030, 1);
					perso.removerObjetoPorModYCant(300040, 1);
					perso.removerObjetoPorModYCant(300050, 1);
					perso.removerObjetoPorModYCant(300060, 1);
				} else {
					GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso, "No tienes las cuatro Gemas Elementales");
				}
			} catch (final Exception e2) {
			}
			break;
		}
		case 610: {
			try {
				final short nuevoMapaID2 = Short.parseShort(this._args.split(",", 4)[0]);
				final int nuevaCeldaID2 = Integer.parseInt(this._args.split(",", 4)[1]);
				final int objR = Integer.parseInt(this._args.split(",", 4)[2]);
				final int objD = Integer.parseInt(this._args.split(",", 4)[3]);
				if (perso.tieneObjModeloNoEquip(objR, 1)) {
					perso.teleport(nuevoMapaID2, nuevaCeldaID2);
					perso.removerObjetoPorModYCant(objR, 1);
					final Objeto nuevoObj = MundoDofus.getObjModelo(objD).crearObjDesdeModelo(1, false);
					if (!perso.addObjetoSimilar(nuevoObj, true, -1)) {
						MundoDofus.addObjeto(nuevoObj, true);
						perso.addObjetoPut(nuevoObj);
						GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(out, nuevoObj);
					}
				} else {
					GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso, "No tienes el objeto necesario");
				}
			} catch (final Exception e2) {
			}
			break;
		}
		case 600: {
			try {
				final int ObjetNeed3 = Integer.parseInt(this._args.split("@")[0]);
				final int ObjetNeed4 = Integer.parseInt(this._args.split("@")[1]);
				if (perso.tieneObjModeloNoEquip(ObjetNeed3, 1) && perso.tieneObjModeloNoEquip(ObjetNeed4, 1)) {
					perso.removerObjetoPorModYCant(ObjetNeed3, 1);
					perso.removerObjetoPorModYCant(ObjetNeed4, 1);
					GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(perso);
					final String mobs = this._args.split("@")[2];
					String ValidMobGroup1 = "";
					String[] split;
					for (int length = (split = mobs.split("\\|")).length, j = 0; j < length; ++j) {
						final String MobAndLevel = split[j];
						int monsterID = -1;
						int monsterLevel = -1;
						final String[] MobOrLevel = MobAndLevel.split(",");
						monsterID = Integer.parseInt(MobOrLevel[0]);
						monsterLevel = Integer.parseInt(MobOrLevel[1]);
						if (MundoDofus.getMobModelo(monsterID) == null
								|| MundoDofus.getMobModelo(monsterID).getGradoPorNivel(monsterLevel) == null) {
							System.out.println("MobGrupo invalido mobID:" + monsterID + " mobNivel:" + monsterLevel);
						} else {
							ValidMobGroup1 = String.valueOf(ValidMobGroup1) + monsterID + "," + monsterLevel + ","
									+ monsterLevel + ";";
						}
					}
					if (ValidMobGroup1.isEmpty()) {
						return;
					}
					final MobModelo.GrupoMobs group = new MobModelo.GrupoMobs(perso.getMapa()._sigIDMapaInfo,
							perso.getCelda().getID(), ValidMobGroup1);
					perso.getMapa().iniciarPeleaVSMobs(perso, group);
				} else {
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "No posees las llaves necesarias", "C60914");
				}
			} catch (final Exception e2) {
				e2.printStackTrace();
			}
			break;
		}
		case 27: {
			try {
				String mobGrupo = "";
				String[] split2;
				for (int length2 = (split2 = this._args.split("\\|")).length, k = 0; k < length2; ++k) {
					final String mobYNivel = split2[k];
					int mobID = -1;
					int mobNivel = -1;
					final String[] mobONivel = mobYNivel.split(",");
					mobID = Integer.parseInt(mobONivel[0]);
					mobNivel = Integer.parseInt(mobONivel[1]);
					if (MundoDofus.getMobModelo(mobID) == null
							|| MundoDofus.getMobModelo(mobID).getGradoPorNivel(mobNivel) == null) {
						System.out.println("MobGrupo invalido mobID:" + mobID + " mobNivel:" + mobNivel);
					} else {
						mobGrupo = String.valueOf(mobGrupo) + mobID + "," + mobNivel + "," + mobNivel + ";";
					}
				}
				if (mobGrupo.isEmpty()) {
					return;
				}
				final MobModelo.GrupoMobs grupo = new MobModelo.GrupoMobs(perso.getMapa()._sigIDMapaInfo,
						perso.getCelda().getID(), mobGrupo);
				perso.getMapa().iniciarPeleaVSMobs(perso, grupo);
			} catch (final Exception ex18) {
			}
			break;
		}
		case 28: {
			try {
				if (perso.getMontura() != null) {
					if (perso.getMontura().esMontable() == 1) {
						perso.subirBajarMontura();
					} else {
						GestorSalida.ENVIAR_Im_INFORMACION(perso, "1176");
					}
				} else {
					GestorSalida.ENVIAR_Im_INFORMACION(perso, "MOUNT_NO_EQUIP");
				}
			} catch (final Exception ex19) {
			}
			break;
		}
		case 29: {
			try {
				final int mapa = perso.getMapa().getID();
				switch (mapa) {
				case 13105: {
					if (perso.getBaseStats().getEfecto(118) < 21) {
						perso.getBaseStats().addUnStat(118, 1);
						break;
					}
					break;
				}
				case 13125: {
					if (perso.getBaseStats().getEfecto(126) < 21) {
						perso.getBaseStats().addUnStat(126, 1);
						break;
					}
					break;
				}
				case 13145: {
					if (perso.getBaseStats().getEfecto(119) < 21) {
						perso.getBaseStats().addUnStat(119, 1);
						break;
					}
					break;
				}
				case 13165: {
					if (perso.getBaseStats().getEfecto(123) < 21) {
						perso.getBaseStats().addUnStat(123, 1);
						break;
					}
					break;
				}
				case 13110: {
					if (perso.getBaseStats().getEfecto(118) < 41) {
						perso.getBaseStats().addUnStat(118, 1);
						break;
					}
					break;
				}
				case 13130: {
					if (perso.getBaseStats().getEfecto(126) < 41) {
						perso.getBaseStats().addUnStat(126, 1);
						break;
					}
					break;
				}
				case 13150: {
					if (perso.getBaseStats().getEfecto(119) < 41) {
						perso.getBaseStats().addUnStat(119, 1);
						break;
					}
					break;
				}
				case 13170: {
					if (perso.getBaseStats().getEfecto(123) < 41) {
						perso.getBaseStats().addUnStat(123, 1);
						break;
					}
					break;
				}
				case 13115: {
					if (perso.getBaseStats().getEfecto(118) < 81) {
						perso.getBaseStats().addUnStat(118, 1);
						break;
					}
					break;
				}
				case 13135: {
					if (perso.getBaseStats().getEfecto(126) < 81) {
						perso.getBaseStats().addUnStat(126, 1);
						break;
					}
					break;
				}
				case 13155: {
					if (perso.getBaseStats().getEfecto(119) < 81) {
						perso.getBaseStats().addUnStat(119, 1);
						break;
					}
					break;
				}
				case 13175: {
					if (perso.getBaseStats().getEfecto(123) < 81) {
						perso.getBaseStats().addUnStat(123, 1);
						break;
					}
					break;
				}
				case 13120: {
					if (perso.getBaseStats().getEfecto(118) < 101) {
						perso.getBaseStats().addUnStat(118, 1);
						break;
					}
					break;
				}
				case 13140: {
					if (perso.getBaseStats().getEfecto(126) < 101) {
						perso.getBaseStats().addUnStat(126, 1);
						break;
					}
					break;
				}
				case 13160: {
					if (perso.getBaseStats().getEfecto(119) < 101) {
						perso.getBaseStats().addUnStat(119, 1);
						break;
					}
					break;
				}
				case 13180: {
					if (perso.getBaseStats().getEfecto(123) < 101) {
						perso.getBaseStats().addUnStat(123, 1);
						break;
					}
					break;
				}
				}
				perso.teleport(6954, 268);
			} catch (final Exception ex20) {
			}
			break;
		}
		case 30: {
			if (System.currentTimeMillis() - perso._tiempoRefrescaMobs < 3000L || perso.getPelea() != null) {
				GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso, Idiomas.getTexto(perso.getCuenta().idioma, 43));
				return;
			}
			perso._tiempoRefrescaMobs = System.currentTimeMillis();
			if (!perso.tieneObjModeloNoEquip(8478, 1)) {
				return;
			}
			perso.removerObjetoPorModYCant(8478, 1);
			perso.getMapa().refrescarGrupoMobs((Personaje) null);
			break;
		}
		case 428: // subir prestigio
			if (perso._resets > 1) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "Ya dispones del mÁximo poder!", Colores.ROJO);
				return;
			}
			// if (perso._resets > 2) {
			// GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "El mÁximo de R actualmente es 3",
			// Colores.ROJO);
			// return;
			// }
			if (perso.getNivel() < Emu.MAX_NIVEL) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "Necesitas ser nivel " + Emu.MAX_NIVEL + " para evolucionar",
						Colores.ROJO);
				return;
			}
			if (perso._resets == 0) {
				if (perso.getKamas() < 10000000) {
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "Necesitas <b>10.000.000 Kamas</b> para evolucionar",
							Colores.ROJO);
					return;
				}
			} else if (perso._resets == 1) {
				if (perso.getKamas() < 20000000) {
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "Necesitas <b>20.000.000 Kamas</b> para evolucionar",
							Colores.ROJO);
					return;
				}
			} else if (perso._resets == 2) {
				if (perso.getKamas() < 30000000) {
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "Necesitas <b>30.000.000 Kamas</b> para evolucionar",
							Colores.ROJO);
					return;
				}
			} else if (perso._resets == 3) {
				if (perso.getKamas() < 50000000) {
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "Necesitas <b>50.000.000 Kamas</b> para evolucionar",
							Colores.ROJO);
					return;
				}
			} else {
				return;
			}
			if (perso._resets == 0) {
				if (perso.getObjPosicion(21) != null) {
					perso.deleteobjBoost(21);
					perso.objBoost(50256);
				} else {
					perso.objBoost(50256);
				}
				perso.setKamas(perso.getKamas() - 10000000);
			} else if (perso._resets == 1) {
				if (perso.getObjPosicion(21) != null) {
					perso.deleteobjBoost(21);
					perso.objBoost(50257);
				} else {
					perso.objBoost(50257);
				}
				perso.setKamas(perso.getKamas() - 20000000);
				Misiones.addListaMisiones(11040, perso, false);
			} else if (perso._resets == 2) {
				if (perso.getObjPosicion(21) != null) {
					perso.deleteobjBoost(21);
					perso.objBoost(50258);
				} else {
					perso.objBoost(50258);
				}
				perso.setKamas(perso.getKamas() - 30000000);
			} else if (perso._resets == 3) {
				if (perso.getObjPosicion(21) != null) {
					perso.deleteobjBoost(21);
					perso.objBoost(50259);
				} else {
					perso.objBoost(50259);

				}
			} else if (perso._resets == 4) {
				if (perso.getObjPosicion(21) != null) {
					perso.deleteobjBoost(21);
					perso.objBoost(50260);
				} else {
					perso.objBoost(50260);

				}
				perso.setKamas(perso.getKamas() - 50000000);
			}
			perso._resets += 1;
			perso.resetearStats();
			perso.setCapital(0);
			perso.setPtosHechizos(0);
			if (perso.getNivel() >= Emu.NIVEL_PA1) {
				perso.getBaseStats().addUnStat(111, -Emu.CANTIDAD_PA1);
			}
			if (perso.getNivel() >= Emu.NIVEL_PM1) {
				perso.getBaseStats().addUnStat(128, -Emu.CANTIDAD_PM1);
			}
			perso.setNivel(1);
			perso.setExperiencia(MundoDofus.getExpNivel(Emu.INICIAR_NIVEL)._personaje);
			if (!Emu.SistemaHechizo) {
				perso.setHechizos(CentroInfo.getHechizosIniciales(perso.getClase(true)));
				perso._lugaresHechizos = CentroInfo.getLugaresHechizosIniciales(perso.getClase(true));
			}
			perso.deformar();
			while (perso.getNivel() < Emu.INICIAR_NIVEL) {
				if (perso.getNivel() >= Emu.MAX_NIVEL) {
					break;
				}
				perso.subirNivel(false, false);
			}
			if (perso.getStatsOficios().size() > 0) {
				ArrayList<StatsOficio> listaStatOficios = new ArrayList<>();
				listaStatOficios.addAll(perso.getStatsOficios().values());
				StringBuilder packet = new StringBuilder("JS");
				StringBuilder packet2 = new StringBuilder("JX");
				StringBuilder packet3 = new StringBuilder("");
				for (StatsOficio sm : listaStatOficios) {
					packet.append(sm.analizarTrabajolOficio());
					packet2.append(
							"|" + sm.getOficio().getID() + ";" + sm.getNivel() + ";" + sm.getXpString(";") + ";");
					packet3.append("JO" + sm.getPosicion() + "|" + sm.getOpcionBin() + "|" + sm.getSlotsPublico() + ""
							+ (char) 0x00);
				}
				StringBuilder finals = new StringBuilder(
						packet + "" + ((char) 0x00) + "" + packet2 + "" + ((char) 0x00) + "" + packet3);
				Objeto obj = perso.getObjPosicion(1);
				if (obj != null) {
					for (StatsOficio statOficio : listaStatOficios) {
						Oficio oficio1 = statOficio.getOficio();
						if (oficio1.herramientaValida(obj.getModelo().getID())) {
							// GestorSalida.ENVIAR_OT_OBJETO_HERRAMIENTA(out, oficio.getID());
							String strOficioPub = CentroInfo.trabajosOficioTaller(oficio1.getID());
							perso._stringOficiosPublicos = strOficioPub;
							finals.append("OT" + oficio1.getID());
							break;
						}
					}
				}
				GestorSalida.enviar(perso, finals.toString());
			}
			GestorSalida.ENVIAR_GM_REFRESCAR_PJ_EN_MAPA(perso.getMapa(), perso);
			GestorSalida.ENVIAR_ASK_PERSONAJE_SELECCIONADO(perso.getCuenta().getEntradaPersonaje().getOut(), perso);
			GestorSalida.ENVIAR_SL_LISTA_HECHIZOS(perso);
			GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso);
			if (perso._resets == 1) {
				Misiones.addListaMisiones(10514, perso, true);
			} else if (perso._resets == 2) {
				Misiones.addListaMisiones(10520, perso, true);
			} else if (perso._resets == 3) {
				Misiones.addListaMisiones(10529, perso, true);
			} else if (perso._resets == 4) {
				Misiones.addListaMisiones(10529, perso, true);
			}
			Timer timer = new Timer(500, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					((Timer) e.getSource()).stop();
					Estaticos.desequiparTodo(perso, perso.getCuenta().getEntradaPersonaje().getOut());
				}
			});
			timer.start();
			break;
		case 31: {
			try {
				if (perso.getEncarnacion() == null) {
					final int clase = Integer.parseInt(this._args);
					if (clase == perso.getClase(true)) {
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "Usted ya pertenece a esta clase.", "C60914");
					} else {
						perso.setClase(clase);
						perso.resetearStats();
						perso.setCapital(0);
						if (perso.getNivel() >= Emu.NIVEL_PA1) {
							perso.getBaseStats().addUnStat(111, -Emu.CANTIDAD_PA1);
						}
						if (perso.getNivel() >= Emu.NIVEL_PM1) {
							perso.getBaseStats().addUnStat(128, -Emu.CANTIDAD_PM1);
						}
						perso.setNivel(1);
						if (!Emu.SistemaHechizo) {
							perso.setHechizos(CentroInfo.getHechizosIniciales(perso.getClase(true)));
							perso._lugaresHechizos = CentroInfo.getLugaresHechizosIniciales(perso.getClase(true));
						}
						perso.setExperiencia(MundoDofus.getExpNivel(Emu.INICIAR_NIVEL)._personaje);
						perso.deformar();
						while (perso.getNivel() < Emu.INICIAR_NIVEL && perso.getNivel() < Emu.MAX_NIVEL) {
							perso.subirNivel(false, false);
						}
						Estaticos.desequiparTodo(perso, perso.getCuenta().getEntradaPersonaje().getOut());
						GestorSalida.ENVIAR_GM_REFRESCAR_PJ_EN_MAPA(perso.getMapa(), perso);
						GestorSalida.ENVIAR_ASK_PERSONAJE_SELECCIONADO(out, perso);
						GestorSalida.ENVIAR_SL_LISTA_HECHIZOS(perso);
						GestorSQL.CAMBIAR_SEXO_CLASE(perso);
						GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso);
					}
				}
			} catch (final Exception ex21) {
			}
			break;
		}
		case 422: {
			if (perso.acaboMision(1)) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "Ya completaste el DesafÍo Diario de hoy, vuelve ma�ana",
						"C60914");
				break;
			}
			perso.teleport(30063, 378);
			break;
		}
		case 32: {
			try {
				perso.cambiarSexo();
				Thread.sleep(300L);
				perso.deformar();
				GestorSalida.ENVIAR_GM_REFRESCAR_PJ_EN_MAPA(perso.getMapa(), perso);
				GestorSQL.CAMBIAR_SEXO_CLASE(perso);
			} catch (final Exception ex22) {
			}
			break;
		}
		case 33: {
			final int objBoost = Integer.parseInt(this._args);
			perso.objBoost(objBoost);
			break;
		}
		case 34: {
			final int posAMover = Integer.parseInt(this._args);
			final Objeto objetoPos = perso.getObjPosicion(posAMover);
			if (objetoPos != null) {
				final String maxStats = Objeto.ObjetoModelo
						.generarStatsModeloDB(objetoPos.getModelo().getStringStatsObj(), true);
				objetoPos.clearTodo();
				objetoPos.convertirStringAStats(maxStats);
				perso.borrarObjetoEliminar(objUsadoID, 1, true);
				GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(out, objetoPos);
				GestorSQL.SALVAR_OBJETO(objetoPos);
				GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso);
				break;
			}
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso, "No se encontr� un objeto en dicha posici�n.");
			break;
		}
		case 35: {
			try {
				final int kamasApostar = Integer.parseInt(this._args);
				final long tempKamas2 = perso.getKamas();
				if (tempKamas2 < kamasApostar) {
					GestorSalida.ENVIAR_Im_INFORMACION(perso, "182");
				} else {
					perso.setKamas(tempKamas2 - kamasApostar);
					GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso);
					perso.setPescarKuakua(true);
				}
			} catch (final Exception ex23) {
			}
			break;
		}
		case 158: {
			try {
				final String libro = this._args;
				GestorSalida.ENVIAR_ACCION_dCK(perso, libro);
			} catch (final Exception ex24) {
			}
			break;
		}
		case 36: {
			try {
				final long precio = Integer.parseInt(this._args.split(";")[0]);
				int tutorial = Integer.parseInt(this._args.split(";")[1]);
				if (tutorial == 30) {
					final int aleatorio = Formulas.getRandomValor(1, 200);
					if (aleatorio == 100) {
						tutorial = 31;
					}
				}
				final Tutorial tuto = MundoDofus.getTutorial(tutorial);
				if (tuto != null) {
					if (perso.getKamas() >= precio) {
						if (precio != 0L) {
							perso.setKamas(perso.getKamas() - precio);
							GestorSalida.ENVIAR_Im_INFORMACION(perso, "046;" + precio);
						}
						try {
							tuto.getInicio().aplicar(perso, null, -1, -1);
						} catch (final Exception ex25) {
						}
						GestorSalida.enviar(perso, "TC" + tutorial + "|7001010000");
						perso.setTutorial(tuto);
						perso.setOcupado(true);
					} else {
						GestorSalida.ENVIAR_Im_INFORMACION(perso, "182");
					}
				}
			} catch (final Exception ex26) {
			}
			break;
		}
		case 37: {
			try {
				final String[] strs = this._args.split("\\|");
				final String[] strs2 = strs[1].split(",");
				final int objNecesario2 = Integer.parseInt(strs[0]);
				if (perso.tieneObjModeloNoEquip(objNecesario2, 1)) {
					perso.removerObjetoPorModYCant(objNecesario2, 1);
					final int objNuevo = Integer.parseInt(strs2[Formulas.getRandomValor(0, strs2.length - 1)]);
					final Objeto nuevoObj2 = MundoDofus.getObjModelo(objNuevo).crearObjDesdeModelo(1, false);
					if (!perso.addObjetoSimilar(nuevoObj2, true, -1)) {
						MundoDofus.addObjeto(nuevoObj2, true);
						perso.addObjetoPut(nuevoObj2);
						GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(out, nuevoObj2);
					}
					GestorSalida.ENVIAR_Im_INFORMACION(perso, "022;1~" + objNecesario2);
					GestorSalida.ENVIAR_Im_INFORMACION(perso, "021;1~" + objNuevo);
				} else {
					GestorSalida.ENVIAR_Im_INFORMACION(perso, "134|43");
				}
			} catch (final Exception ex27) {
			}
			break;
		}
		case 38: {
			try {
				final int nuevaCelda = Integer.parseInt(this._args);
				final Mapa mapa2 = perso.getMapa();
				GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(mapa2, perso.getID());
				perso.setCelda(mapa2.getCelda(nuevaCelda));
				GestorSalida.ENVIAR_GM_AGREGAR_PJ_A_TODOS(perso.getMapa(), perso);
				if (mapa2.getID() == 6863) {
					GestorSalida.enviar(perso, "cMK|-1|For Inkas|P�satelo bien!!!|");
				}
			} catch (final Exception ex28) {
			}
			break;
		}
		case 39: {
			try {
				final NPCModelo npcModelo = MundoDofus.getNPCModelo(408);
				perso.setKamas(perso.getKamas() + npcModelo.getKamas());
				GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso);
				npcModelo.setKamas(100000L);
			} catch (final Exception ex29) {
			}
			break;
		}
		case 40: {
			try {
				final NPCModelo npcModelo = MundoDofus.getNPCModelo(408);
				npcModelo.setKamas(npcModelo.getKamas() + 5000L);
			} catch (final Exception ex30) {
			}
			break;
		}
		case 41: {
			try {
				String mobGrupo2 = "";
				final int mobID2 = Integer.parseInt(this._args);
				final int mobNivel2 = ((perso.getNivel() - 1) / 20 + 1) * 20;
				if (MundoDofus.getMobModelo(mobID2) == null
						|| MundoDofus.getMobModelo(mobID2).getGradoPorNivel(mobNivel2) == null) {
					System.out.println("MobGrupo invalido mobID:" + mobID2 + " mobNivel:" + mobNivel2);
				} else {
					mobGrupo2 = String.valueOf(mobGrupo2) + mobID2 + "," + mobNivel2 + "," + mobNivel2 + ";";
					final MobModelo.GrupoMobs grupo2 = new MobModelo.GrupoMobs(perso.getMapa()._sigIDMapaInfo,
							perso.getCelda().getID(), mobGrupo2);
					perso.getMapa().iniciarPeleaVSDopeul(perso, grupo2);
				}
			} catch (final Exception ex31) {
			}
			break;
		}
		case 50: {
			if (perso.getAlineacion() == 0) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "Necesitas una alineaciÓn para poder pedir una misi�n.",
						"C60914");
				break;
			}
			if (MundoDofus.misionesPvP.containsKey(perso.getID())) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "Ya tienes una misi�n activa, ac�bala antes de pedir otra.",
						"C60914");
				break;
			}
			if (MundoDofus.misionesPvP.containsValue(perso.getID())) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso,
						"No puedes pedir una misi�n ahora porque hay alguien que te tiene como objetivo.", "C60914");
				break;
			}
			Personaje tempP = null;
			final ArrayList<Personaje> victimas = new ArrayList<>();
			for (final Personaje victima : MundoDofus.getPJsEnLinea()) {
				if (victima != null) {
					if (victima == perso) {
						continue;
					}
					if (victima.getAlineacion() == perso.getAlineacion() || victima.estaOcupado()
							|| victima.getAlineacion() == 0 || victima.getIntercambiandoCon() > 0
							|| victima.getIntercambio() != null || !victima.mostrarAlas()
							|| victima.getHaciendoTrabajo() != null || victima.getPelea() != null) {
						continue;
					}
					if (victima.isOnAction()) {
						continue;
					}
					if (perso.personajesDeMisiones.contains(victima.getID())) {
						continue;
					}
					if (victima.personajesDeMisiones.contains(perso.getID())) {
						continue;
					}
					if (!victima.mostrarAlas()) {
						continue;
					}
					if (MundoDofus.misionesPvP.containsKey(victima.getID())) {
						continue;
					}
					if (MundoDofus.misionesPvP.containsValue(victima.getID())) {
						continue;
					}
					if (victima.getCuenta().getRango() > 0) {
						continue;
					}
					if (victima.getCuenta().getActualIP().compareTo(perso.getCuenta().getActualIP()) == 0) {
						continue;
					}
					if (victima._resets != perso._resets) {
						continue;
					}
					if (perso.getNivel() + 110 < victima.getNivel() || perso.getNivel() - 110 > victima.getNivel()) {
						continue;
					}
					victimas.add(victima);
				}
			}
			if (victimas.size() == 0) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso,
						"<b>[Thomas Sacre]</b> No hemos encontrado un objetivo a tu altura. Porfavor vuelve mÁs tarde.",
						"C60914");
				break;
			}
			tempP = victimas.get(Formulas.getRandomValor(0, victimas.size() - 1));
			final String nombreVict = tempP.getNombre();
			MundoDofus.misionesPvP.put(perso.getID(), tempP.getID());
			perso.personajesDeMisiones.add(tempP.getID());
			tempP.personajesDeMisiones.add(perso.getID());
			final String mipj = "<a href='asfunction:onHref,ShowPlayerPopupMenu," + perso.getNombre() + "'>"
					+ perso.getNombre() + "</a>";
			final String enemigo = "<a href='asfunction:onHref,ShowPlayerPopupMenu," + nombreVict + "'>" + nombreVict
					+ "</a>";
			if (perso.personajeAgredea.contains(tempP.getNombre())) {
				perso.personajeAgredea.remove(tempP.getNombre());
			}
			if (tempP.personajeAgredea.contains(perso.getNombre())) {
				tempP.personajeAgredea.remove(perso.getNombre());
			}
			if (tempP.getMapa().getID() != 30980) {
				tempP.teleport(30980, 226);
				final int rand = Formulas.getRandomValor(1, 3);
				if (tempP.getNivel() > perso.getNivel()) {
					switch (rand) {
					case 1: {
						GestorSalida.enviar(perso,
								"cMK|-2|TomÁs Sacre|AquÉ tienes a tu objetivo, parece muy fuerte, ten mucho cuidado!!!|");
						break;
					}
					case 2: {
						GestorSalida.enviar(perso,
								"cMK|-2|TomÁs Sacre|AquÉ tienes a tu objetivo, te recomendar�a que tuvieras cuidado con �l...|");
						break;
					}
					case 3: {
						GestorSalida.enviar(perso,
								"cMK|-2|TomÁs Sacre|AquÉ tienes a tu objetivo, yo personalmente no me enfrentar�a a �l...|");
						break;
					}
					}
				} else {
					switch (rand) {
					case 1: {
						GestorSalida.enviar(perso, "cMK|-2|TomÁs Sacre|AquÉ tienes a tu objetivo, buena suerte!!!|");
						break;
					}
					case 2: {
						GestorSalida.enviar(perso,
								"cMK|-2|TomÁs Sacre|AquÉ tienes a tu objetivo, no creo que te suponga un gr�n problema|");
						break;
					}
					case 3: {
						GestorSalida.enviar(perso, "cMK|-2|TomÁs Sacre|AquÉ tienes a tu objetivo, tr�talo bien :3|");
						break;
					}
					}
				}
			}
			tempP.penaliza = true;
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(tempP,
					"<b>RECUERDA:</b> No puedes abandonar el mapa PvP hasta acabar la misi�n, si te desconectas o te retiras del combate, se te penalizar�.",
					"C60914");
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE_TODOS("(PVP) - El personaje <b>" + mipj
					+ "</b> estÁ en modo caza.\nSu objetivo es <b>" + enemigo + "</b>.\nEl <b>pvp</b> os espera",
					"B45F04");
			break;
		}
		case 53: {
			try {
				final String objetodar = this._args.split(";")[0];
				final String objetopedir = this._args.split(";")[1];
				final MundoDofus.Trueque trueque = new MundoDofus.Trueque(perso, objetopedir, objetodar);
				perso.setTrueque(trueque);
				if (objetopedir.equalsIgnoreCase("resucitar")) {
					perso.revivirMascota = true;
				}
				perso.setTrueque(trueque);
				if (objetopedir.equalsIgnoreCase("itemmision")) {
					perso.itemmision = true;
				}
				if (objetopedir.equalsIgnoreCase("perfecciona")) {
					perso.perfecciona = true;
				}
				if (objetopedir.equalsIgnoreCase("torrepvm")) {
					perso.torrepvm = true;
				}
				if (objetopedir.equalsIgnoreCase("elemento")) {
					perso.elemento = true;
				}
				GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(out, 9, "");
			} catch (final Exception ex32) {
			}
			break;
		}
		case 54: {
			try {
				if (!this._args.isEmpty()) {
					final int Args = Integer.parseInt(this._args);
					if (Args == 10 || Args == 11 || Args == 12 || Args == 13 || Args == 14 || Args == 15) {
						perso.boostStat2(Args);
					}
				}
			} catch (final Exception ex33) {
			}
			break;
		}
		case 55: {
			try {
				if (!this._args.isEmpty()) {
					final long precio2 = Integer.parseInt(this._args.split(";")[0]);
					final int accion = Integer.parseInt(this._args.split(";")[1]);
					if (perso.getKamas() >= precio2) {
						perso.setKamas(perso.getKamas() - precio2);
						new Accion(accion, "", "").aplicar(perso, null, -1, -1);
					} else {
						GestorSalida.ENVIAR_Im_INFORMACION(perso, "182");
					}
				}
			} catch (final Exception ex34) {
			}
			break;
		}
		case 56: {
			try {
				final String quitar2 = this._args.split(";")[0];
				final String agregar = this._args.split(";")[1];
				final int objModelo = Integer.parseInt(quitar2.split(",")[0]);
				final int cant4 = Integer.parseInt(quitar2.split(",")[1]);
				final int cant = Integer.parseInt(quitar2.split(",")[1]);
				final int objNuevo2 = Integer.parseInt(agregar.split(",")[0]);
				final int cantNuevo = Integer.parseInt(agregar.split(",")[1]);
				if (perso.tieneObjModeloNoEquip(objModelo, cant4)) {
					perso.removerObjetoPorModYCant(objModelo, -cant4);
					final Objeto nuevoObj3 = MundoDofus.getObjModelo(objNuevo2).crearObjDesdeModelo(cantNuevo, false);
					if (!perso.addObjetoSimilar(nuevoObj3, true, -1)) {
						MundoDofus.addObjeto(nuevoObj3, true);
						perso.addObjetoPut(nuevoObj3);
						GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(out, nuevoObj3);
					} else {
						perso.removerObjetoPorModYCant(objModelo, -cant);
					}

					GestorSalida.ENVIAR_Im_INFORMACION(perso, "022;" + cant4 + "~" + objModelo);
					GestorSalida.ENVIAR_Im_INFORMACION(perso, "021;" + cantNuevo + "~" + objNuevo2);
				} else {
					GestorSalida.ENVIAR_Im_INFORMACION(perso, "134|43");

				}

			} catch (final Exception ex35) {
			}
			break;
		}
		case 599:// agregar o quitar objeto
			try {
				int id = Integer.parseInt(_args.split(",")[0]);
				int cant = Integer.parseInt(_args.split(",")[1]);
				boolean send = true;
				if (_args.split(",").length > 2) {
					send = _args.split(",")[2].equals("1");
				}
				if (cant > 0) {
					ObjetoModelo OM = MundoDofus.getObjModelo(id);
					if (OM == null) {
						return;
					}
					Objeto obj = OM.crearObjDesdeModelo(cant, false);
					if (!perso.addObjetoSimilar(obj, true, -1)) {
						MundoDofus.addObjeto(obj, true);
						perso.addObjetoPut(obj);
						GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(out, obj);
					}
				} else {
					perso.removerObjetoPorModYCant(id, -cant);
				}
				if (perso.enLinea()) {
					GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(perso);
					if (send) {
						if (cant >= 0) {
							GestorSalida.ENVIAR_Im_INFORMACION(perso, "021;" + cant + "~" + id);
						} else if (cant < 0) {
							GestorSalida.ENVIAR_Im_INFORMACION(perso, "022;" + -cant + "~" + id);
						}
					}
				}
				perso.setObjTemporal(id, cant);
			} catch (Exception e) {
			}
			break;
		case 57: {
			try {
				GestorSalida.ENVIAR_AlE_CAMBIAR_NOMBRE(out, "r");
				perso.cambiarNombre(true);
			} catch (final Exception ex36) {
			}
			break;
		}
		case 58: {
			try {
				final String quitar2 = this._args.split(";")[0];
				final String[] azar2 = this._args.split(";")[1].split("\\|");
				final int id3 = Integer.parseInt(quitar2.split(",")[0]);
				final int cant4 = Integer.parseInt(quitar2.split(",")[1]);
				if (cant4 < 0) {
					perso.removerObjetoPorModYCant(id3, -cant4);
					GestorSalida.ENVIAR_Im_INFORMACION(perso, "022;" + -cant4 + "~" + id3);
				}
				String[] array;
				for (int length3 = (array = azar2).length, l = 0; l < length3; ++l) {
					final String objetoazar2 = array[l];
					final int ID2 = Integer.parseInt(objetoazar2.split(",")[0]);
					final int cantidad3 = Integer.parseInt(objetoazar2.split(",")[1]);
					boolean enviar2 = true;
					if (objetoazar2.split(",").length > 2) {
						enviar2 = objetoazar2.split(",")[2].equals("1");
					}
					if (cantidad3 > 0) {
						final Objeto.ObjetoModelo OM3 = MundoDofus.getObjModelo(ID2);
						if (OM3 == null) {
							break;
						}
						final Objeto obj4 = OM3.crearObjDesdeModelo(cantidad3, false);
						if (!perso.addObjetoSimilar(obj4, true, -1)) {
							MundoDofus.addObjeto(obj4, true);
							perso.addObjetoPut(obj4);
							GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(out, obj4);
						}
					} else {
						perso.removerObjetoPorModYCant(ID2, -cantidad3);
					}
					if (perso.enLinea() && enviar2) {
						if (cantidad3 >= 0) {
							GestorSalida.ENVIAR_Im_INFORMACION(perso, "021;" + cantidad3 + "~" + ID2);
						} else if (cantidad3 < 0) {
							GestorSalida.ENVIAR_Im_INFORMACION(perso, "022;" + -cantidad3 + "~" + ID2);
						}
					}
				}
				GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(perso);
			} catch (final Exception ex37) {
			}
			break;
		}
		case 100: {
			if (perso.getMontura() != null) {
				int habilidad = 0;
				if (this._args.split(",").length == 2) {
					habilidad = Formulas.getRandomValor(Integer.parseInt(this._args.split(",")[0]),
							Integer.parseInt(this._args.split(",")[1]));
				} else {
					habilidad = Integer.parseInt(this._args);
				}
				final Dragopavo montura = perso.getMontura();
				montura.setHabilidad(new StringBuilder(String.valueOf(habilidad)).toString());
				perso.setMontura(montura);
				GestorSalida.ENVIAR_Re_DETALLES_MONTURA(perso, "+", MundoDofus.getDragopavoPorID(montura.getID()));
				GestorSQL.ACTUALIZAR_MONTURA(montura, false);
				break;
			}
			break;
		}
		case 101: {
			break;
		}
		case 103: {
			if (perso.getKamas() < 50000L) {
				break;
			}
			perso.setKamas(perso.getKamas() - 50000L);
			final Personaje wife = MundoDofus.getPersonaje(perso.getEsposo());
			wife.divorciar();
			perso.divorciar();
			break;
		}
		case 104: {
			final int energy = Integer.parseInt(this._args);
			perso.agregarEnergia(energy);
			GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso);
			break;
		}
		case 206: {
			int idModelo = MundoDofus.getObjeto(objUsadoID).getModelo().getID();
			if (!perso.tieneObjetoID(objUsadoID)) {
				break;
			}
			final Mapa mapaac = perso.getMapa();
			boolean excepcion = false;
			if (perso.getCuenta().getRango() >= 5) {
				excepcion = true;
			}
			if (this._args.equals("")) {
				break;
			}
			final String[] stra = this._args.split("-");
			final String dataobj = stra[0].replace("@", "-");
			final int objid = Integer.parseInt(stra[1]);
			final int caminable = Integer.parseInt(stra[2]);
			final int devuelve = Integer.parseInt(stra[3]);
			final Casa c = MundoDofus.getCasaDentroPorMapa(mapaac.getID());
			if ((c == null || c.getDueÑoID() != perso.getCuentaID()) && !excepcion) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "SÓlo puedes usar este objeto estando en tu casa", "C60914");
				break;
			}
			if (devuelve == 0) {
				idModelo = 0;
			}
			Encriptador.replaceEnCelda(mapaac, celda, dataobj, perso, false, caminable, objid, idModelo, objUsadoID);
			break;
		}
		case 209: {
			final Mapa macap2 = perso.getMapa();
			boolean excepcione2 = false;
			if (perso.getCuenta().getRango() >= 5) {
				excepcione2 = true;
			}
			if (!perso.tieneObjetoID(objUsadoID)) {
				break;
			}
			final Casa cac = MundoDofus.getCasaDentroPorMapa(macap2.getID());
			if ((cac == null || cac.getDueÑoID() != perso.getCuentaID()) && !excepcione2) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "SÓlo puedes usar este objeto estando en tu casa", "C60914");
				break;
			}
			if (!macap2._personaliza.containsKey(celda)) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "No hay ning�n objeto para girar", "C60914");
				break;
			}
			if (macap2.getPersos(celda)) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "No puedes girar nada porque hay una persona.", "C60914");
				break;
			}
			Encriptador.girarObjeto(macap2, celda, perso);
			break;
		}
		case 842: {
			GestorSalida.enviar(perso, "#kn1");
			break;
		}
		case 207: {
			final Mapa macap3 = perso.getMapa();
			boolean excepcione3 = false;
			if (perso.getCuenta().getRango() >= 5) {
				excepcione3 = true;
			}
			if (!perso.tieneObjetoID(objUsadoID)) {
				break;
			}
			final Casa ca = MundoDofus.getCasaDentroPorMapa(macap3.getID());
			if ((ca == null || ca.getDueÑoID() != perso.getCuentaID()) && !excepcione3) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "SÓlo puedes usar este objeto estando en tu casa", "C60914");
				break;
			}
			if (macap3._personaliza.containsKey(celda)) {
				final String back = macap3._personaliza.get(celda).split(",")[1];
				final int objdevolver = Integer.parseInt(macap3._personaliza.get(celda).split(",")[2]);
				if (objdevolver != 0) {
					final Objeto.ObjetoModelo OM4 = MundoDofus.getObjModelo(objdevolver);
					if (OM4 != null) {
						final Objeto obj5 = OM4.crearObjDesdeModelo(1, false);
						if (!perso.addObjetoSimilar(obj5, true, -1)) {
							MundoDofus.addObjeto(obj5, true);
							perso.addObjetoPut(obj5);
							GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(out, obj5);
						}
						GestorSalida.ENVIAR_Im_INFORMACION(perso, "021;1~" + objdevolver);
					}
				}
				Encriptador.replaceEnCelda(macap3, celda, back, perso, true, 0, 0, -1, 0);
				break;
			}
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "No hay ning�n objeto para desmontar en esta posici�n",
					"C60914");
			break;
		}
		case 200: {
			final Mapa mapa3 = perso.getMapa();
			final int idModelo2 = MundoDofus.getObjeto(objUsadoID).getModelo().getID();
			if (mapa3.getCercado() == null) {
				break;
			}
			final Mapa.Cercado cercado = mapa3.getCercado();
			if (perso.getCuenta().getRango() < 4) {
				if (perso.getGremio() == null) {
					GestorSalida.ENVIAR_BN_NADA(perso);
					break;
				}
				if (!perso.getMiembroGremio().puede(8192)) {
					GestorSalida.ENVIAR_Im_INFORMACION(perso, "193");
					break;
				}
				if (cercado.getCeldasObj().size() == 0 || !cercado.getCeldasObj().contains(celda)) {
					GestorSalida.ENVIAR_BN_NADA(perso);
					break;
				}
			}
			if (cercado.getCantObjColocados() < cercado.getCantObjMax()) {
				cercado.addObjetoCria(celda, idModelo2, perso.getID());
				GestorSalida.ENVIAR_GDO_PONER_OBJETO_CRIA(mapa3,
						String.valueOf(celda) + ";" + idModelo2 + ";1;1000;1000");
				perso.borrarObjetoEliminar(objUsadoID, 1, true);
				break;
			}
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso,
					"Ya llegaste al mÁximo de objetos de crianza en este cercado");
			break;
		}
		case 512: {
			final int mapaid = perso.getMapa().getID();
			int itemid = 0;
			final int kamas = 0;
			String msg = "";
			int nextmap = 0;
			int nextcell = 0;
			boolean telep = false;
			int valorpr = 0;
			try {
				valorpr = Integer.parseInt(this._args);
			} catch (final Exception ex38) {
			}
			switch (mapaid) {
			case 2084:
			case 10438: {
				telep = false;
				msg = "Has ganado x4 <b>Llave Maestra R0</b>.";
				itemid = 50100;
				nextmap = 25046;
				nextcell = 154;
				break;
			}
			case 10439: {
				telep = false;
				msg = "Has ganado x4 <b>Llaves Maestra R1</b>.";
				itemid = 50101;
				nextmap = 25046;
				nextcell = 154;
				break;
			}
			case 10440: {
				telep = false;
				msg = "Has ganado x4 <b>Llaves Maestra R2</b>.";
				itemid = 50102;
				nextmap = 25046;
				nextcell = 154;
				break;
			}
			case 10441: {
				telep = false;
				msg = "Has ganado x4 <b>Llaves Maestra R3</b>.";
				itemid = 50103;
				nextmap = 25046;
				nextcell = 154;
				break;
			}
			}
			perso.updateObjetivoRecolecta(mapaid, 1, "mazmorra", false);
			if (kamas > 0) {
				final long cant5 = kamas;
				final long tempKamas3 = perso.getKamas();
				long nuevasKamas2 = tempKamas3 + cant5;
				if (nuevasKamas2 < 0L) {
					nuevasKamas2 = 0L;
				}
				perso.setKamas(nuevasKamas2);
				if (perso.enLinea()) {
					GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso);
				}
				if (msg != "") {
					GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso, msg);
				}
			}
			if (mapaid >= 10438 && mapaid <= 10441 && itemid != 0) {
				perso.crearItem(itemid, 4);
			}
			if (nextmap != 0 && nextcell != 0) {
				perso.teleport((short) nextmap, nextcell);
			}
			if (telep) {
				perso.teleport(7411, 450);
				break;
			}
			break;
		}
		case 551: {
			int pocimalvl = 0;
			try {
				pocimalvl = Integer.parseInt(this._args);
			} catch (final Exception ex39) {
			}
			switch (pocimalvl) {
			case 1: {
				perso.crearItem(50280, 1);
				break;
			}
			case 2: {
				perso.crearItem(50281, 1);
				break;
			}
			case 3: {
				perso.crearItem(50282, 1);
				break;
			}
			}
			perso.teleport(7411, 450);
			break;
		}
		case 552: {
			int subenivel = 0;
			try {
				subenivel = Integer.parseInt(this._args);
			} catch (final Exception ex40) {
			}
			if (subenivel <= 0) {
				break;
			}
			final int lvl = perso.getNivel() + subenivel;
			while (perso.getNivel() < lvl && perso.getNivel() < Emu.MAX_NIVEL) {
				perso.subirNivel(true, false);
			}
			GestorSalida.ENVIAR_ASK_PERSONAJE_SELECCIONADO(out, perso);
			GestorSalida.ENVIAR_SL_LISTA_HECHIZOS(perso);
			GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso);
			break;
		}
		case 201: {
			try {
				final int celdapj = perso.getCelda().getID();
				final Mapa tMapa = perso.getMapa();
				final MundoDofus.SubArea subarea = tMapa.getSubArea();
				final MundoDofus.Area area = subarea.getArea();
				final int alineacion = perso.getAlineacion();
				if (celdapj > 0) {
					if (perso.getNivel() < 250) {
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso,
								"Necesitas tener un nivel superior a 250 para poner un prisma", "C60914");
					} else if (alineacion == 0 || alineacion == 3) {
						GestorSalida.ENVIAR_Im_INFORMACION(perso, "134|43");
					} else if (!perso.mostrarAlas()) {
						GestorSalida.ENVIAR_Im_INFORMACION(perso, "1148");
					} else if (tMapa.esArena() || tMapa.esCasa() || tMapa.esTaller() || tMapa.getID() > 13000) {
						GestorSalida.ENVIAR_Im_INFORMACION(perso, "1146");
					} else {
						if (MundoDofus.mapasBoost.contains(tMapa.getID())) {
							GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "No puedes poner prismas en este mapa",
									"C60914");
							return;
						}
						if (tMapa.getAncho() < 15) {
							GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "No puedes poner prismas en este mapa",
									"C60914");
							return;
						}
						if (subarea.getAlineacion() != 0 || !subarea.getConquistable()
								|| tMapa.getSubArea().getPrismaID() != 0) {
							GestorSalida.ENVIAR_Im_INFORMACION(perso, "1149");
						} else {
							if (!perso.tieneObjModeloNoEquip(8990, 1)) {
								return;
							}
							perso.removerObjetoPorModYCant(8990, 1);
							final Prisma prisma = new Prisma(MundoDofus.getSigIDPrisma(), alineacion, 1, tMapa.getID(),
									celdapj, 0, -1);
							subarea.setAlineacion(alineacion);
							subarea.setPrismaID(prisma.getID());
							for (final Personaje z : MundoDofus.getPJsEnLinea()) {
								if (z.getAlineacion() == 0) {
									GestorSalida.ENVIAR_am_MENSAJE_ALINEACION_SUBAREA(z,
											String.valueOf(subarea.getID()) + "|" + alineacion + "|1");
									if (area.getAlineacion() != 0) {
										continue;
									}
									GestorSalida.ENVIAR_aM_MENSAJE_ALINEACION_AREA(z,
											String.valueOf(area.getID()) + "|" + alineacion);
								} else {
									GestorSalida.ENVIAR_am_MENSAJE_ALINEACION_SUBAREA(z,
											String.valueOf(subarea.getID()) + "|" + alineacion + "|0");
									if (area.getAlineacion() != 0) {
										continue;
									}
									GestorSalida.ENVIAR_aM_MENSAJE_ALINEACION_AREA(z,
											String.valueOf(area.getID()) + "|" + alineacion);
								}
							}
							if (area.getAlineacion() == 0) {
								area.setPrismaID(prisma.getID());
								area.setAlineacion(alineacion);
								prisma.setAreaConquistada(area.getID());
							}
							MundoDofus.addPrisma(prisma);
							GestorSQL.AGREGAR_PRISMA(prisma);
							GestorSalida.ENVIAR_GM_PRISMA_A_MAPA(tMapa, prisma);
						}
					}
				}
			} catch (final Exception ex41) {
			}
			break;
		}
		case 350: {
			final int item1 = perso.regaloRandom();
			final int item2 = perso.regaloRandom();
			final int item3 = perso.regaloRandom();
			final int item4 = perso.regaloRandom();
			int itemfinal = 0;
			final StringBuilder finalobj = new StringBuilder("");
			for (int i = 0; i < 4; ++i) {
				switch (i) {
				case 0: {
					itemfinal = item1;
					break;
				}
				case 1: {
					itemfinal = item2;
					break;
				}
				case 2: {
					itemfinal = item3;
					break;
				}
				case 3: {
					itemfinal = item4;
					break;
				}
				}
				if (!Emu.regaloMis.contains(itemfinal)) {
					break;
				}
				final Objeto.ObjetoModelo OM5 = MundoDofus.getObjModelo(itemfinal);
				final Objeto obj6 = OM5.crearObjDesdeModelo(1, false);
				if (!perso.addObjetoSimilar(obj6, true, -1)) {
					MundoDofus.addObjeto(obj6, true);
					perso.addObjetoPut(obj6);
					finalobj.append("OAKO" + obj6.stringObjetoConGuiÓn()).append('\0');
				}
				try {
					Thread.sleep(100L);
				} catch (final InterruptedException ex42) {
				}
			}
			GestorSalida.enviar(perso, finalobj.toString());
			GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(perso);
			break;
		}
		case 228: {
			final int animacionID = Integer.parseInt(this._args);
			final Animacion animacion = MundoDofus.getAnimacion(animacionID);
			if (perso.getPelea() != null) {
				return;
			}
			perso.cambiarOrientacion(1);
			GestorSalida.ENVIAR_GA_ACCION_JUEGO_AL_MAPA(perso, perso.getMapa(), "0", 228,
					String.valueOf(perso.getID()) + ";" + celda + "," + Animacion.preparaAGameAccion(animacion), "");
			break;
		}
		case 1000: {
			final int misionID = Integer.parseInt(this._args);
			Misiones.addListaMisiones(misionID, perso, true);
			break;
		}
		case 1001: {
			Misiones.checkMision(perso, 0);
			break;
		}
		case 223: {
			if (perso.getKamas() < 200000L) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "No tienes suficientes Kamas para el viaje.", "C60914");
				return;
			}
			perso.setKamas(perso.getKamas() - 200000L);
			perso.teleport(10427, 369);
			break;
		}
		case 1005: {
			if (System.currentTimeMillis() - perso.get_tiempoUltSalvada() < 360000L || perso.getPelea() != null) {
				GestorSalida.ENVIAR_BN_NADA(out);
				return;
			}
			perso.set_tiempoUltSalvada(System.currentTimeMillis());
			GestorSQL.SALVAR_PERSONAJE(perso, true);
			break;
		}
		case 1006: {
			final StringBuilder strc = new StringBuilder();
			boolean prim = false;
			String msgc = "";
			for (final Map.Entry<String, String> coms : MundoDofus.comandosEmu.entrySet()) {
				if (coms.getKey().equalsIgnoreCase("comandos")) {
					msgc = "\n" + coms.getValue().split("�")[4] + "\n\n";
				} else {
					if (!prim) {
						strc.append("." + coms.getKey() + " = " + coms.getValue().split("�")[3]);
					} else {
						strc.append("\n." + coms.getKey() + " = " + coms.getValue().split("�")[3]);
					}
					prim = true;
				}
			}
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, String.valueOf(msgc) + strc.toString(), Emu.COLOR_MENSAJE);
			break;
		}
		case 277: {
			final int darogr = Integer.parseInt(this._args);
			GestorSQL.setPuntoCuenta(GestorSQL.getPuntosCuenta(perso.getCuentaID()) + darogr, perso.getCuentaID());
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "Has ganado " + darogr + " puntos vip", "237B3B");
			break;
		}
		case 1007: {
			int mispuntos = GestorSQL.getPuntosCuenta(perso.getCuentaID());
			if (mispuntos < 0) {
				mispuntos = 0;
			}
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "Tienes un total de " + mispuntos + " puntos vip",
					Emu.COLOR_MENSAJE);
			break;
		}
		case 931: {
			final int idtit = Integer.parseInt(this._args);
			perso.addTitulos(idtit);
			break;
		}
		case 944: {
			final Casa cas = Casa.getCasaDePj(perso);
			if (cas != null) {
				cas.pagar(perso);
				break;
			}
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "No tienes ninguna casa para pagar el impuesto", "C60914");
			break;
		}
		case 995: {
			if (perso.pagadoImpuesto) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso,
						"Ya tienes un permiso, eres libre de coger lo que quieras!!!", "C60914");
				return;
			}
			final int mispuntos2 = GestorSQL.getPuntosCuenta(perso.getCuentaID());
			if (mispuntos2 < 40) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "Necesitas <b>40 Puntos VIP</b> para obtener el permiso",
						"C60914");
				return;
			}
			perso.pagadoImpuesto = true;
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "Permiso concedido!!!", "237B3B");
			break;
		}
		case 5124: {
			MundoDofus.ReclamaGema(perso);
			break;
		}
		case 5151: {
			final int requis = Integer.parseInt(this._args);
			if (perso.liderMaitre) {
				//final Timer timer1 = new Timer(500, new ActionListener() {
					//@Override
					//public void actionPerformed(final ActionEvent e) {
						//((Timer) e.getSource()).stop();
						for (final Personaje pjx : perso.getGrupo().getPersos()) {
							//codigo original

							if (requis == 1) {
								if (pjx._resets < 1) {
									GestorSalida.ENVIAR_cs_CHAT_MENSAJE(pjx, "Necesitas ser R1 para entrar a esta mazmorra",
											"C60914");
									//break;
								}
								pjx.teleport(25100, 280);
								//break;
							} else if (requis == 2) {
								if (pjx._resets < 2) {
									GestorSalida.ENVIAR_cs_CHAT_MENSAJE(pjx, "Necesitas ser R2 para entrar a esta mazmorra",
											"C60914");
									//break;
									continue;
								}
								pjx.teleport(25101, 280);
								//break;
							} else if (requis == 3) {
								if (pjx._resets < 3) {
									GestorSalida.ENVIAR_cs_CHAT_MENSAJE(pjx, "Necesitas ser R3 para entrar a esta mazmorra",
											"C60914");
								//	break;
									continue;
								}
								pjx.teleport(25102, 280);
								//break;
							} else {
								if (requis != 4) {
									//break;
									continue;
								}
								if (!pjx.accionesPJ.contains(18)) {
									GestorSalida.ENVIAR_cs_CHAT_MENSAJE(pjx,
											"Necesitas el tÍtulo de Campe�n para entrar a esta mazmorra", "C60914");
									//break;
									continue;
								}
								pjx.teleport(25115, 280);
								//break;
							}
							//codigo original
						}
					//}
				//});
				//timer1.start();
				break;
			}else {
				if (requis == 1) {
					if (perso._resets < 1) {
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "Necesitas ser R1 para entrar a esta mazmorra",
								"C60914");
						break;
					}
					perso.teleport(25100, 280);
					break;
				} else if (requis == 2) {
					if (perso._resets < 2) {
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "Necesitas ser R2 para entrar a esta mazmorra",
								"C60914");
						break;
					}
					perso.teleport(25101, 280);
					break;
				} else if (requis == 3) {
					if (perso._resets < 3) {
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "Necesitas ser R3 para entrar a esta mazmorra",
								"C60914");
						break;
					}
					perso.teleport(25102, 280);
					break;
				} else {
					if (requis != 4) {
						break;
					}
					if (!perso.accionesPJ.contains(18)) {
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso,
								"Necesitas el tÍtulo de Campe�n para entrar a esta mazmorra", "C60914");
						break;
					}
					perso.teleport(25115, 280);
					break;
				}
			}



		}
		case 5252: {
			if (!perso._titulos.contains(129)) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso,
						"Necesitas el tÍtulo de Infernal para entrar a esta mazmorra", "C60914");
				break;
			}
			perso.teleport(27124, 741);
			break;
		}
		case 5253: {
			final int rndd = Formulas.getRandomValor(1, 7);
			int elidd = 0;
			switch (rndd) {
			case 1: {
				elidd = 50017;
				break;
			}
			case 2: {
				elidd = 50016;
				break;
			}
			case 3: {
				elidd = 10190;
				break;
			}
			case 4: {
				elidd = 30014;
				break;
			}
			case 5: {
				elidd = 30015;
				break;
			}
			case 6: {
				elidd = 50080;
				break;
			}
			case 7: {
				elidd = 30016;
				break;
			}
			}
			if (elidd != 0) {
				perso.crearItem(elidd, 1);
			}
			perso.addTitulos(130);
			perso.teleport(7411, 280);
			break;
		}
		case 5150: {
			final int eltipo = Integer.parseInt(this._args);
			final int randomitem = Formulas.getRandomValor(1, 7);
			int iditem = 0;
			if (eltipo == 1) {
				switch (randomitem) {
				case 1: {
					iditem = 980107;
					break;
				}
				case 2: {
					iditem = 70501;
					break;
				}
				case 3: {
					iditem = 34714;
					break;
				}
				case 4: {
					iditem = 34715;
					break;
				}
				case 5: {
					iditem = 34716;
					break;
				}
				case 6: {
					iditem = 34717;
					break;
				}
				case 7: {
					iditem = 40020;
					break;
				}
				default: {
					iditem = 980107;
					break;
				}
				}
				perso.addTitulos(125);
				perso.crearItem(iditem, 1);
				if (!perso.accionesPJ.contains(16)) {
					perso.accionesPJ.add(16);
				}
			} else if (eltipo == 2) {
				switch (randomitem) {
				case 1: {
					iditem = 34741;
					break;
				}
				case 2: {
					iditem = 980107;
					break;
				}
				case 3: {
					iditem = 36032;
					break;
				}
				case 4: {
					iditem = 50226; // dolmanax corrompido
					break;
				}
				case 5: {
					iditem = 43000;
					break;
				}
				case 6: {
					iditem = 43001;
					break;
				}
				case 7: {
					iditem = 30972;
					break;
				}
				default: {
					iditem = 34741;
					break;
				}
				}
				perso.addTitulos(126);
				perso.crearItem(iditem, 1);
				if (!perso.accionesPJ.contains(17)) {
					perso.accionesPJ.add(17);
				}
			} else if (eltipo == 3) {
				switch (randomitem) {
				case 1: {
					iditem = 50274;
					break;
				}
				case 2: {
					iditem = 50058;
					break;
				}
				case 3: {
					iditem = 50081;
					break;
				}
				case 4: {
					iditem = 40013;
					break;
				}
				case 5: {
					iditem = 50028;
					break;
				}
				case 6: {
					iditem = 50031;
					break;
				}
				case 7: {
					iditem = 50252;
					break;
				}
				default: {
					iditem = 50252;
					break;
				}
				}
				perso.addTitulos(127);
				perso.crearItem(iditem, 1);
				if (!perso.accionesPJ.contains(18)) {
					perso.accionesPJ.add(18);
				}
			} else if (eltipo == 4) {
				perso.addTitulos(129);
				perso.crearItem(985505, 1);
			}
			if (perso.accionesPJ.contains(16) && perso.accionesPJ.contains(17) && perso.accionesPJ.contains(18)) {
				perso.addTitulos(128);
			}
			perso.teleport(7411, 280);
			break;
		}
		case 5281: {
			if (MundoDofus.mapaPortal == null) {
				break;
			}
			for (final Personaje perso2 : MundoDofus.getPJsEnLinea()) {
				if (perso2 == null) {
					continue;
				}
				final String packet = "cs<font color='#B45F04'>" + perso.getNombre()
						+ Idiomas.getTexto(perso2.getCuenta().idioma, 186) + "</font>";
				GestorSalida.enviar(perso2, packet);
			}
			perso.teleport(27105, 301);
			break;
		}
		case 5161: {
			final int randomrec = Formulas.getRandomValor(1, 2);
			if (randomrec == 1) {
				perso.crearItem(985506, 1);
			} else {
				perso.crearItem(40016, 1);
			}
			perso.teleport(7411, 280);
			break;
		}
		case 1201: {
			if (perso.tieneMisione(12130)) {
				perso.teleport(10358, 338);
				break;
			}
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "No puedes entrar a esta torre sin haber aceptado la misi�n",
					"C60914");
			break;
		}
		case 1202: {
			if (!perso.accionesPJ.contains(26)) {
				perso.teleport(10357, 323);
				break;
			}
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "Las princesas no te dejan subir a la parte alta del castillo",
					"C60914");
			break;
		}
		case 1203: {
			if (!perso.accionesPJ.contains(27)) {
				perso.teleport(10356, 196);
				break;
			}
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "El sÓtano estÁ cerrado!!!", "C60914");
			break;
		}
		case 1500: {
			perso.addTitulos(131);
			if (!perso.accionesPJ.contains(24)) {
				perso.accionesPJ.add(24);
			}
			perso.teleport(10302, 341);
			break;
		}
		case 1501: {
			perso.addTitulos(132);
			if (!perso.accionesPJ.contains(26)) {
				perso.accionesPJ.add(26);
			}
			perso.teleport(10307, 325);
			break;
		}
		case 1502: {
			if (!perso.accionesPJ.contains(27)) {
				perso.accionesPJ.add(27);
			}
			perso.teleport(10354, 123);
			break;
		}
		case 1503: {
			perso.crearItem(281, 100);
			if (!perso.accionesPJ.contains(29)) {
				perso.accionesPJ.add(29);
			}
			perso.teleport(10340, 221);
			break;
		}
		case 1504: {
			if (!perso.accionesPJ.contains(30)) {
				perso.crearItem(71500, 15);
				perso.accionesPJ.add(30);
				break;
			}
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "SÓlo te puedes servir 1 vez de este ingrediente tan especial",
					"C60914");
			break;
		}
		case 1505: {
			if (!perso.accionesPJ.contains(31)) {
				perso.crearItem(71501, 15);
				perso.accionesPJ.add(31);
				break;
			}
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "SÓlo te puedes servir 1 vez de este ingrediente tan especial",
					"C60914");
			break;
		}
		case 1506: {
			perso.getCuenta()._vip = 1;
			GestorSQL.ACTUALIZAR_VIP(perso.getCuenta());
			GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso);
			GestorSalida.ENVIAR_GM_REFRESCAR_PJ_EN_MAPA(perso.getMapa(), perso);
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "Felicidades, acabas de convertirte en un miembro VIP!!!",
					"237B3B");
			break;
		}
		case 1510: {
			if (!MundoDofus.eventoLarvas) {
				return;
			}
			if (MundoDofus.empezocarrera) {
				return;
			}
			final int larva = Integer.parseInt(this._args);
			if (larva < 0 && larva > 4) {
				break;
			}
			final int puntoslarva = GestorSQL.getPuntosCuenta(perso.getCuentaID());
			if (puntoslarva < 50) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso,
						"Necesitas <b>50 Puntos VIP</b> para participar en este evento", "C60914");
				break;
			}
			boolean multi = false;
			for (final Personaje pjs : MundoDofus.ipsLarvas) {
				if (pjs == perso) {
					continue;
				}
				if (pjs.getCuenta().getActualIP().compareTo(perso.getCuenta().getActualIP()) == 0) {
					multi = true;
					break;
				}
			}
			if (multi) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "No se admiten multicuentas en este evento", "C60914");
				break;
			}
			if (MundoDofus.larva1.contains(perso) || MundoDofus.larva2.contains(perso)
					|| MundoDofus.larva3.contains(perso) || MundoDofus.larva4.contains(perso)) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso,
						"Has abandonado tu apuesta por la <b>" + perso.larva + "</b>", "C60914");
				if (MundoDofus.larva1.contains(perso)) {
					MundoDofus.larva1.remove(perso);
				}
				if (MundoDofus.larva2.contains(perso)) {
					MundoDofus.larva2.remove(perso);
				}
				if (MundoDofus.larva3.contains(perso)) {
					MundoDofus.larva3.remove(perso);
				}
				if (MundoDofus.larva4.contains(perso)) {
					MundoDofus.larva4.remove(perso);
				}
			}
			String nombre = "";
			switch (larva) {
			case 1: {
				MundoDofus.larva1.add(perso);
				nombre = "Larva Zafiro";
				break;
			}
			case 2: {
				MundoDofus.larva2.add(perso);
				nombre = "Larva Ex-Campeona";
				break;
			}
			case 3: {
				MundoDofus.larva3.add(perso);
				nombre = "Larva Esmeralda";
				break;
			}
			case 4: {
				MundoDofus.larva4.add(perso);
				nombre = "Larva Rub�";
				break;
			}
			default: {
				return;
			}
			}
			if (MundoDofus.ipsLarvas.contains(perso)) {
				MundoDofus.ipsLarvas.remove(perso);
			}
			MundoDofus.boteLarva += 50;
			perso.larva = nombre;
			MundoDofus.ipsLarvas.add(perso);
			GestorSQL.setPuntoCuenta(GestorSQL.getPuntosCuenta(perso.getCuentaID()) - 50, perso.getCuentaID());
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "Has apostado por <b>" + nombre + "</b> buena suerte!!!",
					"C60914");
			break;
		}
		case 912: {
			if (!perso.tieneObjModeloNoEquip(985545, 1)) {
				return;
			}
			perso.removerObjetoPorModYCant(985545, 1);
			perso._lugaresHechizos.clear();
			perso._hechizos.clear();
			perso.etapa = 1;
			perso.buffClase = -1;
			perso.actualHechizos = MundoDofus.getHechizoAprender(perso.etapa, perso);
			GestorSalida.enviar(perso, "#kd" + perso.actualHechizos + "," + perso.etapa + ";" + perso.yaactualizo);
			break;
		}
		case 1997:{ // ir a mapa RAID, 1 pj x ip
			int ips = 0;
			if (perso.liderMaitre) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "No Puedes Ingresar a la Raid si tienes Maitre Activo.!",
						Colores.ROJO);
				return;


			}
			Mapa mapa = MundoDofus.getMapa(31998);
			for (final Personaje pjs : mapa.getPersos()) {

					if (pjs.getCuenta().getActualIP().compareTo(perso.getCuenta().getActualIP()) == 0) {
						ips += 1;
					}
			}
			if (ips > 0) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, Idiomas.getTexto(perso.getCuenta().idioma, 222),
						Colores.ROJO);
				return;
			}
			perso.teleport(31998, 220);
		}
		default: {
			System.out.println("Accion ID = " + this._ID + " no implantado");
			break;
		}
		}
	}

	public int getID() {
		return this._ID;
	}
}
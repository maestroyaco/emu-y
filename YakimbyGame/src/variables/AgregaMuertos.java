package variables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import estaticos.CentroInfo;
import estaticos.Emu;
import estaticos.GestorSalida;
import estaticos.MundoDofus;
import servidor.Colores;
import variables.Pelea.Glifo;
import variables.Pelea.Luchador;
import variables.Pelea.Trampa;

public class AgregaMuertos extends Thread {
	ArrayList<Luchador> listaMuertos = new ArrayList<>();
	private Pelea pelea = null;
	boolean rompe = false;
	private Luchador iniciante = null;
	private Luchador atacante = null;

	public AgregaMuertos(Pelea pel, Luchador luc, Luchador ata) {
		pelea = pel;
		iniciante = luc;
		atacante = ata;
		listaMuertos.add(luc);
	}

	@Override
	public void run() {
		try {
			for (Luchador luchador : pelea.luchadoresDeEquipo(iniciante.getParamEquipoAliado())) {
				if (rompe) {
					break;
				}
				if (luchador == null || luchador._estaMuerto || luchador._estaRetirado) {
					continue;
				}
				if (pelea._estadoPelea == 4) {
					rompe = true;
					break;
				}
				if (luchador.getInvocador() == iniciante) {
					listaMuertos.add(luchador);
				}
			}
			for (Luchador lx : listaMuertos) {
				if (rompe) {
					break;
				}
				if (pelea._estadoPelea == 4) {
					rompe = true;
					break;
				}
				agregarAMuertos(lx);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			interrupt();
		}
	}

	public void agregarAMuertos(Luchador victima) {
		try {
			/*
			 * if ((Emu.ACTIVAR_GUERRA) && (victima != null) && (victima.getPersonaje() !=
			 * null) && (getTipoPelea() == 1) && (victima.getPersonaje().getAlineacion() >
			 * 0) && (victima.getPersonaje().getAlineacion() != 3)) {
			 * Guerras.kill(victima.getPersonaje().getAlineacion()); }
			 */
			if (pelea._estadoPelea == 4) {
				rompe = true;
				return;
			}
			if (victima.getMob() != null && pelea.LucActual != null) {
				if (pelea.LucActual == victima) {
					if (pelea._IAThreads != null) {
						try {
							if (pelea._IAThreads.getThread() != null) {
								if (!pelea._IAThreads.getThread().isInterrupted()) {
									pelea._IAThreads.getThread().interrupt();
								}
								pelea._IAThreads = null;
							}
						} catch (Exception e) {
						}
					}
				} else {
					if (pelea.LucActual.getMob() != null) {
						pelea.esperaHechizo = false;
					}
				}
			}
			int idVictima = victima.getID();
			if (pelea._tipo == 4 || pelea._tipo == 3) {
				if (!victima.esInvocacion() && pelea._equipo2.values().contains(victima)) {
					pelea._mobsMuertosReto.add(idVictima);// agrega el mob a la lista negra
				}
			}
			try {
				if (!victima.statico && pelea._estadoPelea != 4) {
					if (pelea.getPosicionLuchador(victima) != -1) {
						pelea.turnosPelea.remove(pelea.getPosicionLuchador(victima));
					}
				}
			} catch (Exception e) {
			}
			if (pelea.esAngel) {
				if (victima.getMob() != null) {
					if (victima.getMob().getModelo().getID() == 4097 || victima.getMob().getModelo().getID() == 4098) {
						if (!MundoDofus.angel) {
							GestorSalida.ENVIAR_cs_CHAT_MENSAJE_A_PELEA(pelea, 7,
									"Hab�is matado al curandero del demonio y la rabia le estÁ empezando a consumir, corran!!!",
									Colores.VERDE);
						} else {
							GestorSalida.ENVIAR_cs_CHAT_MENSAJE_A_PELEA(pelea, 7,
									"Hab�is matado al curandero del Ángel y no se lo ha tomado muy bien, tengan cuidado!!!",
									Colores.VERDE);
						}
						for (Luchador res : pelea.luchadoresDeEquipo(victima.getParamEquipoAliado())) {
							if ((res == null) || res.estaMuerto() || res.esInvocacion() || res.estaRetirado()) {
								continue;
							}
							int hechizoid = 1267;
							if (MundoDofus.angel) {
								hechizoid = 3665;
							}
							res.addBuff(125, 15000, 100, 1, false, hechizoid, "81;90;-1;20;0;1d10+80", res, false,
									true);// vida
							res.addBuff(138, 80, 100, 1, false, hechizoid, "70;-1;-1;0;0;0d0+80", res, false, true);// daÑo
																													// 45%
							res.addBuff(210, 30, 100, 1, false, hechizoid, "30;-1;-1;4;0;0d0+30", res, false, true);// resis
							res.addBuff(211, 30, 100, 1, false, hechizoid, "30;-1;-1;4;0;0d0+30", res, false, true);
							res.addBuff(212, 30, 100, 1, false, hechizoid, "30;-1;-1;4;0;0d0+30", res, false, true);
							res.addBuff(213, 30, 100, 1, false, hechizoid, "30;-1;-1;4;0;0d0+30", res, false, true);
							res.addBuff(214, 30, 100, 1, false, hechizoid, "30;-1;-1;4;0;0d0+30", res, false, true);
						}
					}
				}
			}
			Luchador asesino = null;
			if (atacante != null) {
				asesino = atacante;
			} else {
				asesino = pelea.LucActual;
			}
			if (!pelea._retos.isEmpty()) {
				if ((pelea._tipo == 4 || pelea._tipo == 3) && victima.getMob() != null && !victima.esInvocacion()
						&& asesino != null) {
					Map<Integer, Integer> copiaRetos = new TreeMap<>();
					copiaRetos.putAll(pelea._retos);
					for (Entry<Integer, Integer> entry : copiaRetos.entrySet()) {
						int reto = entry.getKey();
						int exitoReto = entry.getValue();
						if (exitoReto != 0) {
							continue;
						}
						int cant2 = 0;
						int nivelV = victima.getNivel();
						switch (reto) {
						case 3:// elegido voluntario
							if (pelea._mobsMuertosReto.size() > 0) {
								if (pelea._mobsMuertosReto.get(0) == pelea._idMobReto) {
									GestorSalida.ENVIAR_GdaK_RETO_REALIZADO(pelea, reto);
									exitoReto = 1;
								} else {
									GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(pelea, reto);
									exitoReto = 2;
								}
							}
							break;
						case 4:// aplazamiento
							cant2 = pelea._inicioLucEquipo2.size();
							if (pelea._mobsMuertosReto.size() == cant2) {
								if (pelea._mobsMuertosReto.get(cant2 - 1) == pelea._idMobReto) {
									GestorSalida.ENVIAR_GdaK_RETO_REALIZADO(pelea, reto);
									exitoReto = 1;
								} else {
									GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(pelea, reto);
									exitoReto = 2;
								}
							}
							break;
						case 10:// cruel
						case 25:// ordenado
							for (Entry<Integer, Integer> e : pelea._ordenNivelMobs.entrySet()) {
								if (e.getValue() == nivelV) {
									if (e.getKey() == idVictima) {
										pelea._ordenNivelMobs.remove(idVictima);
										break;
									}
								} else {
									GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(pelea, reto);
									exitoReto = 2;
									break;
								}
							}
							break;
						case 28:// ni pias ni sumisas
							if (asesino.getPersonaje() == null) {
								continue;
							}
							if (asesino.getPersonaje().getSexo() == 0) {
								GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(pelea, reto);
								exitoReto = 2;
							}
							break;
						case 29: // ni pios ni sumisos
							if (asesino.getPersonaje() == null) {
								continue;
							}
							if (asesino.getPersonaje().getSexo() == 1) {
								GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(pelea, reto);
								exitoReto = 2;
							}
							break;
						case 30:// los pequeÑos antes
							if (asesino.getPersonaje() == null) {
								continue;
							}
							if (asesino.getID() != pelea._luchMenorNivelReto) {
								GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(pelea, reto);
								exitoReto = 2;
							}
							break;
						case 31:// focalizacion
							if (pelea._idMobReto == 0) {
								pelea._idMobReto = idVictima;
							} else if (pelea._mobsMuertosReto.contains(pelea._idMobReto)) {
								pelea._idMobReto = 0;
							}
							break;
						case 32:// elitista
							if (pelea._idMobReto == idVictima) {
								GestorSalida.ENVIAR_GdaK_RETO_REALIZADO(pelea, reto);
								exitoReto = 1;
							} else {
								GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(pelea, reto);
								exitoReto = 2;
							}
							break;
						case 35:// asesino a sueldo
							boolean siguiente = false;
							for (Entry<Integer, Luchador> e : pelea._ordenLuchMobs.entrySet()) {
								if (e.getKey() == idVictima) {
									pelea._ordenLuchMobs.remove(idVictima);
									siguiente = true;
									break;
								} else {
									GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(pelea, reto);
									exitoReto = 2;
									break;
								}
							}
							if (siguiente) {
								for (Entry<Integer, Luchador> e : pelea._ordenLuchMobs.entrySet()) {
									GestorSalida.ENVIAR_Gf_MOSTRAR_CASILLA_EN_PELEA(pelea, 5, e.getKey(),
											e.getValue().getCeldaPelea().getID());
									break;
								}
							}
							break;
						case 42: // el dos por uno
							pelea._cantMobsMuerto++;
							if (pelea._cantMobsMuerto > 2) {
								GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(pelea, reto);
								exitoReto = 2;
							}
							break;
						case 44:// reparto
						case 46:// cada uno con su monstruo
							if (asesino.getPersonaje() == null) {
								continue;
							}
							asesino._mobMatadosReto.add(idVictima);
							break;
						}
						if (exitoReto != 0) {
							pelea._retos.remove(reto);
							pelea._retos.put(reto, exitoReto);
						}
					}
				}
			}
			if ((pelea._inicioLucEquipo1.contains(victima)) && (!victima.esInvocacion())
					&& (!pelea._muertesEquipo1.contains(idVictima))) {
				pelea._muertesEquipo1.add(idVictima);
			}
			if ((pelea._inicioLucEquipo2.contains(victima)) && (!victima.esInvocacion())
					&& (!pelea._muertesEquipo2.contains(idVictima))) {
				pelea._muertesEquipo2.add(idVictima);
			}
			GestorSalida.ENVIAR_GA103_JUGADOR_MUERTO(pelea, 7, idVictima);// HA MUERTO Y DESAPARECE CON ANIMACION
			if (victima.getPelea() != null) {
				Map<EfectoHechizo, Luchador> buffsAQuitar = new HashMap<>();
				for (Luchador luchador : pelea.luchadoresDeEquipo(3)) {
					Map<EfectoHechizo, Luchador> bufes = luchador.getBuffPelea();
					for (Entry<EfectoHechizo, Luchador> eff : bufes.entrySet()) {
						if (victima.getID() == eff.getValue().getID()) {
							buffsAQuitar.put(eff.getKey(), luchador);
						}
					}
				}
				StringBuilder buffsPendientes = new StringBuilder("#ER" + ((char) 0x00));
				if (buffsAQuitar.size() > 0) {
					for (Entry<EfectoHechizo, Luchador> lx : buffsAQuitar.entrySet()) {
						if (lx.getValue() == victima)
						 {
							continue; // value = luchador a quien le tengo que quitar buffs, key es el que le hechiz�
						}
						if (buffsPendientes.toString().equals("")) {
							buffsPendientes.append(lx.getValue().desbuffear(lx.getKey()));
						} else {
							buffsPendientes.append(((char) 0x00) + lx.getValue().desbuffear(lx.getKey()));
						}
						if (lx.getValue().getPersonaje() != null && !lx.getValue()._estaRetirado
								&& !lx.getValue().estaMuerto()) {
							GestorSalida.ENVIAR_As_STATS_DEL_PJ(lx.getValue().getPersonaje());
						}
					}
					if (!buffsPendientes.toString().equals("")) {
						GestorSalida.ENVIAR_GAMEACTION_A_PELEA(pelea, 7, buffsPendientes.toString());
					}
				}
				if (pelea._mapaCopia.getLuchadores(victima.getCeldaPelea().getID()).containsKey(victima)) {
					pelea._mapaCopia.removerLuchador(victima);
					if (victima.tieneEstado(3)) {
						Luchador transportado = victima.getTransportando();
						transportado.setEstado(CentroInfo.ESTADO_TRANSPORTADO, 0);
						victima.setEstado(CentroInfo.ESTADO_PORTADOR, 0);
						transportado.setTransportadoPor(null);
						victima.setTransportado(null);
						GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 950, victima.getID() + "",
								victima.getID() + "," + 3 + ",0");
						GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 950, transportado.getID() + "",
								transportado.getID() + "," + 8 + ",0");
					} else if (victima.tieneEstado(8)) {
						Luchador portador = victima.getTransportadoPor();
						victima.setEstado(CentroInfo.ESTADO_TRANSPORTADO, 0);
						portador.setEstado(CentroInfo.ESTADO_PORTADOR, 0);
						victima.setTransportadoPor(null);
						portador.setTransportado(null);
						GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 950, portador.getID() + "",
								portador.getID() + "," + 3 + ",0");
						GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 950, victima.getID() + "",
								victima.getID() + "," + 8 + ",0");
					}
				}
			}
			TreeMap<Integer, Luchador> team = new TreeMap<>();
			if (victima.getEquipoBin() == 0) {
				team.putAll(pelea._equipo1);
			} else if (victima.getEquipoBin() == 1) {
				team.putAll(pelea._equipo2);
			}
			if (victima.getMob() != null && asesino != null) {
				try {
					boolean esEstatico = false;
					if (victima.getMob() != null) {
						for (int id : CentroInfo.INVOCACIONES_ESTATICAS) {
							if (id == victima.getMob().getModelo().getID()) {
								esEstatico = true;
							}
						}
					}
					if (victima.esInvocacion() && !esEstatico && !victima.statico) {
						victima.getInvocador()._nroInvocaciones--;
						if (pelea._equipo1.containsKey(idVictima)) {
							pelea._equipo1.remove(idVictima);
						} else if (pelea._equipo2.containsKey(idVictima)) {
							pelea._equipo2.remove(idVictima);
						}
						GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 999, idVictima + "",
								pelea.stringOrdenJugadores());
					}
				} catch (Exception e) {
				}
			}
			ArrayList<Glifo> glifos = new ArrayList<>();
			glifos.addAll(pelea._glifos);
			for (Glifo glifo : glifos) {
				if (glifo.getLanzador().getID() == idVictima) {
					int celdaID = glifo.getCelda().getID();
					GestorSalida.ENVIAR_GDZ_ACTUALIZA_ZONA_EN_PELEA(pelea, 7, "-", celdaID, glifo.getTamaÑo(), 4);
					GestorSalida.ENVIAR_GDC_ACTUALIZAR_CELDA_EN_PELEA(pelea, 7, celdaID);
					pelea._glifos.remove(glifo);
				}
			}
			ArrayList<Trampa> trampas = new ArrayList<>();
			trampas.addAll(pelea.getTrampas());
			for (Trampa trampa : trampas) {
				if (trampa.getLanzador().getID() == idVictima) {
					trampa.desaparecer();
					pelea._trampas.remove(trampa);
				}
			}
			// System.out.println("Muertos 1: "+cantLuchIniMuertos(1)+" TamaÑo:
			// "+_inicioLucEquipo1.size()+" Muertos 2: "+cantLuchIniMuertos(2)+" TamaÑo:
			// "+_inicioLucEquipo2.size()+" puedeJugar "+victima.puedeJugar()+" getPersonaje
			// "+victima.getPersonaje()+" getMob "+victima.getMob()+" esInvocacion
			// "+victima.esInvocacion()+" getInvocador "+victima.getInvocador());
			pelea._tempAccion = "";
			if (pelea.cantLuchIniMuertos(1) != pelea._inicioLucEquipo1.size()
					&& pelea.cantLuchIniMuertos(2) != pelea._inicioLucEquipo2.size() && pelea._estadoPelea != 4) {
				try {
					GestorSalida.ENVIAR_GTL_ORDEN_JUGADORES(pelea, 7);
				} catch (Exception e) {
				}
			}
			if (pelea._tipo == 5 && victima.esRecaudador()) {
				pelea.acaboPelea("agrega1");
				return;
			} else if ((pelea._tipo == 2) && (victima.esPrisma())) {
				pelea.acaboPelea("agrega2");
				return;
			} else if (pelea.cantLuchIniMuertos(1) == pelea._inicioLucEquipo1.size()
					|| pelea.cantLuchIniMuertos(2) == pelea._inicioLucEquipo2.size()) {
				pelea.acaboPelea("agrega3");
				return;
			} else if (victima.puedeJugar() && victima.getPersonaje() == null && victima.getMob() != null
					&& !victima.esInvocacion() && victima.getInvocador() == null) {
				pelea.finTurno("agregaamuertos5");
			} else if (victima.puedeJugar() && victima.getPersonaje() != null && victima.getMob() == null
					&& !victima.esInvocacion() && victima.getInvocador() == null) {
				pelea.finTurno("agregaamuertos4");
			} else if (victima.puedeJugar() && victima.getPersonaje() == null && victima.getMob() != null
					&& victima.esInvocacion() && victima.getInvocador() != null) {
				pelea.finTurno("agregaamuertos3");
			}
		} catch (Exception e) {
			Emu.creaLogs(e);
		}
	}
}
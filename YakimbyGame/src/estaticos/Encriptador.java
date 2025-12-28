package estaticos;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import servidor.Colores;
import servidor.Estaticos;
import variables.Mapa;
import variables.Mapa.Celda;
import variables.Personaje;

public class Encriptador {
	private static char[] HASH = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
			'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
			'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', '-', '_' };

	public static String encriptarPassword(String codigoLlave, String Password) {
		StringBuilder encriptado = new StringBuilder("#1");
		for (int i = 0; i < Password.length(); i++) {
			char PPass = Password.charAt(i);
			char PKey = codigoLlave.charAt(i % codigoLlave.length());
			int APass = PPass / 16;
			int AKey = PPass % 16;
			int ANB = (APass + PKey) % HASH.length;
			int ANB2 = (AKey + PKey) % HASH.length;
			encriptado.append(HASH[ANB]);
			encriptado.append(HASH[ANB2]);
		}
		return encriptado.toString();	}
	




	/**
	 * Descifra una contraseña encriptada con formato #1[hash]
	 * Algoritmo inverso de encriptarPassword()
	 * 
	 * @param codigoLlave La clave de conexión usada para cifrar
	 * @param hashEncriptado El hash en formato "#1[hash]" o solo "[hash]"
	 * @return La contraseña en texto plano, o null si el formato es inválido
	 */
	public static String desencriptarPassword(String codigoLlave, String hashEncriptado) {
		if (hashEncriptado == null || hashEncriptado.length() < 3) {
			return null;
		}
		
		// Remover prefijo #1 si existe
		String hash = hashEncriptado;
		if (hash.startsWith("#1")) {
			hash = hash.substring(2);
		}
		
		// El hash debe tener longitud par (cada carácter de la contraseña genera 2 caracteres)
		if (hash.length() % 2 != 0) {
			return null;
		}
		
		StringBuilder password = new StringBuilder();
		
		// Procesar cada par de caracteres
		for (int i = 0; i < hash.length(); i += 2) {
			char char1 = hash.charAt(i);
			char char2 = hash.charAt(i + 1);
			
			// Encontrar índices en HASH
			int index1 = -1, index2 = -1;
			for (int j = 0; j < HASH.length; j++) {
				if (HASH[j] == char1) {
					index1 = j;
				}
				if (HASH[j] == char2) {
					index2 = j;
				}
			}
			
			if (index1 == -1 || index2 == -1) {
				return null; // Carácter inválido en el hash
			}
			
			// Obtener el carácter de la clave correspondiente
			int keyIndex = (i / 2) % codigoLlave.length();
			char PKey = codigoLlave.charAt(keyIndex);
			
			// Descifrar: ANB = (APass + PKey) % HASH.length
			//            ANB2 = (AKey + PKey) % HASH.length
			// Necesitamos encontrar APass y AKey en [0, 15] tales que:
			// (APass + PKey) % 64 = index1
			// (AKey + PKey) % 64 = index2
			
			// Intentar encontrar APass en [0, 15]
			int APass = -1;
			for (int a = 0; a < 16; a++) {
				if ((a + PKey) % HASH.length == index1) {
					APass = a;
					break;
				}
			}
			
			// Intentar encontrar AKey en [0, 15]
			int AKey = -1;
			for (int a = 0; a < 16; a++) {
				if ((a + PKey) % HASH.length == index2) {
					AKey = a;
					break;
				}
			}
			
			if (APass == -1 || AKey == -1) {
				return null; // No se pudo descifrar este carácter
			}
			
			// Reconstruir el carácter original: PPass = APass * 16 + AKey
			char PPass = (char) (APass * 16 + AKey);
			password.append(PPass);
		}
		
		return password.toString();
	}

	public static String celdaIDACodigo(int celdaID) {
		int char1 = celdaID / 64, char2 = celdaID % 64;
		return HASH[char1] + "" + HASH[char2];
	}

	public static short celdaCodigoAID(String celdaCodigo) {
		char char1 = celdaCodigo.charAt(0), char2 = celdaCodigo.charAt(1);
		int code1 = 0, code2 = 0, a = 0;
		while (a < HASH.length) {
			if (HASH[a] == char1) {
				code1 = a * 64;
			}
			if (HASH[a] == char2) {
				code2 = a;
			}
			a++;
		}
		return (short) (code1 + code2);
	}

	public static int getNumeroPorValorHash(char c) {
		for (int a = 0; a < HASH.length; a++) {
			if (HASH[a] == c) {
				return a;
			}
		}
		return -1;
	}

	public static char getValorHashPorNumero(int c) {
		return HASH[c];
	}

	public static ArrayList<Celda> analizarInicioCelda(Mapa mapa, int num) {
		ArrayList<Celda> listaCeldas = null;
		String infos = null;
		if (!mapa.getLugaresString().equalsIgnoreCase("-1")) {
			infos = mapa.getLugaresString().split("\\|")[num];
			int a = 0;
			listaCeldas = new ArrayList<>();
			while (a < infos.length()) {
				Celda cell = mapa.getCelda(
						(getNumeroPorValorHash(infos.charAt(a)) << 6) + getNumeroPorValorHash(infos.charAt(a + 1)));
				if (cell == null) {
					continue;
				}
				listaCeldas.add(cell);
				a = a + 2;
			}
		}
		return listaCeldas;
	}

	public static String decifrarMapData(String key, String preData) {
		String data = preData;
		try {
			key = prepareKey(key);
			data = decypherData(preData, key, "" + checksum(key));
		} catch (Exception exception) {
		}
		return data;
	}

	private static String reeplazarEl(String str, int index, char replace) {
		if (str == null) {
			return str;
		} else if (index < 0 || index >= str.length()) {
			return str;
		}
		char[] chars = str.toCharArray();
		chars[index] = replace;
		return String.valueOf(chars);
	}

	public static void girarObjeto(Mapa mapa, int celdadest, Personaje perso) {
		String mapData = mapa.getMapData();
		StringBuilder str = new StringBuilder();
		boolean actualiza = false;
		for (int f = 0; f < mapData.length(); f += 10) {
			String CellData = mapData.substring(f, f + 10);
			int celda = (short) (f / 10);
			if (celda == celdadest) {
				actualiza = true;
				String origin = CellData.substring(7, CellData.length());
				int objetoidorig = -1;
				if (mapa._personaliza.containsKey(celda)) {
					origin = mapa._personaliza.get(celda).split(",")[1];
					objetoidorig = Integer.parseInt(mapa._personaliza.get(celda).split(",")[2]);
					mapa._personaliza.remove(celda);
				}
				switch (CellData.charAt(7)) {
				case 'd':
					CellData = reeplazarEl(CellData, 7, 'h');
					break;
				case 'h':
					CellData = reeplazarEl(CellData, 7, 'd');
					break;
				}
				mapa._personaliza.put(celda,
						CellData.substring(7, CellData.length()) + "," + origin + "," + objetoidorig);
				str.append(CellData);
			} else {
				str.append(CellData);
			}
		}
		if (actualiza) {
			if (!MundoDofus.mapasSalvar.contains(mapa.getID())) {
				MundoDofus.mapasSalvar.add(mapa.getID());
			}
			mapa.setMapData(str.toString());
			GestorSalida.enviar(perso, "#bh");
			perso.teleport(perso.getMapa().getID(), perso.getCelda().getID());
			for (Personaje px : perso.getMapa().getPersos()) {
				if (px == perso) {
					continue;
				}
				GestorSalida.enviar(px, "#bh");
				px.teleport(px.getMapa().getID(), px.getCelda().getID());
			}
			// perso.setMapaGDM(mapa);
			// perso.setCargandoMapa(true);
			// int random = Formulas.getRandomValor(1, 10);
			// GestorSalida.enviar(perso, "#kz"+mapa.getID()+((char) 0x00)+"GDM|" +
			// (mapa.getID()+celdadest+random) + "|" + mapa.getFecha() +
			// "|"+mapa.getCodigo());
		}
	}

	public static String getCeldasLimp(Mapa mapa) {
		StringBuilder str = new StringBuilder();
		StringBuilder str2 = new StringBuilder();
		String mapData = mapa.getMapData();
		boolean fir = false;
		boolean fir2 = false;
		// int celdas = 0;
		for (int f = 0; f < mapData.length(); f += 10) {
			// celdas += 1;
			String CellData = mapData.substring(f, f + 10);
			int celda = (short) (f / 10);
			if (mapa._personaliza.containsKey(celda) || mapa._limpieza.containsKey(celda)) {
				continue;
			}
			List<Byte> celdaInfo = new ArrayList<>();
			for (int i = 0; i < CellData.length(); i++) {
				celdaInfo.add((byte) getNumeroPorValorHash(CellData.charAt(i)));
			}
			int layerObject2 = ((celdaInfo.get(0) & 2) << 12) + ((celdaInfo.get(7) & 1) << 12) + (celdaInfo.get(8) << 6)
					+ celdaInfo.get(9);
			boolean expasa = false;
			switch (layerObject2) {
			case 479:
			case 1030:
			case 7350:
			case 904:
				expasa = true;
				break;
			}
			int caminable = ((celdaInfo.get(2) & 56) >> 3);
			if (caminable == 2) {
				expasa = true;
			}
			if (expasa) {
				continue;
			}
			boolean layerObjeto2Interac = ((celdaInfo.get(7) & 2) >> 1) != 0;
			int objid = (layerObjeto2Interac ? layerObject2 : -1);
			if (layerObject2 > 0 && !layerObjeto2Interac || objid > 0 && !layerObjeto2Interac) {
				if (fir) {
					str.append(",");
				}
				str.append(celda);
				fir = true;
			} else {
				// boolean activo = (celdaInfo.get(0) & 32) >> 5 != 0;
				if (caminable <= 1/* || caminable == 4 && !activo */) {
					if (fir2) {
						str2.append(",");
					}
					str2.append(celda);
					fir2 = true;
				}
			}
		}
		return str.toString() + ";" + str2.toString();
	}

	public static void liberaEspacio(Mapa mapa, String limpieza, Personaje perso) {
		String mapData = mapa.getMapData();
		StringBuilder str = new StringBuilder();
		boolean actualiza = false;
		ArrayList<Integer> celdasLimp = new ArrayList<>();
		for (String str1 : limpieza.split(",")) {
			if (str1.equals("")) {
				continue;
			}
			int celda = Integer.parseInt(str1);
			if (!celdasLimp.contains(celda)) {
				celdasLimp.add(celda);
			}
		}
		for (int f = 0; f < mapData.length(); f += 10) {
			String CellData = mapData.substring(f, f + 10);
			String CellDataBack = CellData;
			int celda = (short) (f / 10);
			if (celdasLimp.contains(celda)) {
				if (mapa.getPersos(celda)) {
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso,
							"No puedes quitar quitar lo que hay en este sitio porque hay una persona.", Colores.ROJO);
					str.append(CellData);
					continue;
				}
				List<Byte> celdaInfo = new ArrayList<>();
				for (int i = 0; i < CellData.length(); i++) {
					celdaInfo.add((byte) getNumeroPorValorHash(CellData.charAt(i)));
				}
				int layerObject2 = ((celdaInfo.get(0) & 2) << 12) + ((celdaInfo.get(7) & 1) << 12)
						+ (celdaInfo.get(8) << 6) + celdaInfo.get(9);
				boolean lineaDeVista = (celdaInfo.get(0) & 1) != 0;
				boolean layerObjeto2Interac = ((celdaInfo.get(7) & 2) >> 1) != 0;
				int objid = (layerObjeto2Interac ? layerObject2 : -1);
				boolean expasa = false;
				if (mapa._personaliza.containsKey(celda)) {
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso,
							"No puedes limpiar una celda porque posicionaste algo que puedes quitar con el Desmontador para Casas",
							Colores.ROJO);
					str.append(CellData);
					continue;
				}
				if (layerObject2 == 0) {
					int layerObject3 = ((celdaInfo.get(0) & 2) << 12) + ((1 & 1) << 12) + (celdaInfo.get(5) << 6)
							+ celdaInfo.get(6);
					if (layerObject3 != 0) {
						if (layerObject3 == 5126) {
							GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso,
									"No puedes limpiar una celda porque hay un objeto interactivo.", Colores.ROJO);
							str.append(CellData);
							continue;
						}
					}
				}
				switch (layerObject2) {
				case 479:
				case 7350:
				case 1030:
				case 904:
					expasa = true;
					break;
				}
				int caminable = ((celdaInfo.get(2) & 56) >> 3);
				if (caminable == 2) {
					expasa = true;
				}
				if (expasa) {
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "No puedes quitar un objeto en una celda", Colores.ROJO);
					str.append(CellData);
					continue;
				}
				if (layerObjeto2Interac && perso.getCuenta().getRango() < 1) {
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso,
							"No puedes limpiar una celda porque hay un objeto interactivo.", Colores.ROJO);
					str.append(CellData);
					continue;
				}
				actualiza = true;// capa2
				switch (CellData.charAt(2)) {
				case 'a':
					CellData = reeplazarEl(CellData, 2, 'G');
					break;
				case 'b':
					CellData = reeplazarEl(CellData, 2, 'H');
					break;
				case 'c':
					CellData = reeplazarEl(CellData, 2, 'I');
					break;
				case 'd':
					CellData = reeplazarEl(CellData, 2, 'J');
					break;
				case 'e':
					CellData = reeplazarEl(CellData, 2, 'K');
					break;
				case 'f':
					CellData = reeplazarEl(CellData, 2, 'L');
					break;
				case 'g':
					CellData = reeplazarEl(CellData, 2, 'M');
					break;
				case 'h':
					CellData = reeplazarEl(CellData, 2, 'N');
					break;
				}
				actualiza = true;// capa 1
				/*
				 * CellData = reeplazarEl(CellData,4,'e'); CellData =
				 * reeplazarEl(CellData,5,'a'); CellData = reeplazarEl(CellData,6,'a');
				 */
				CellData = CellData.substring(0, CellData.length() - 3) + "aaa";
				if (mapa.getCeldas().containsKey(celda)) {
					mapa.getCeldas().remove(celda);
					mapa.getCeldas().put(celda, new Celda(mapa, (short) (f / 10), true, lineaDeVista, objid));
				} else {
					mapa.getCeldas().put(celda, new Celda(mapa, (short) (f / 10), true, lineaDeVista, objid));
				}
				str.append(CellData);
				mapa._limpieza.put(celda, CellData + "," + CellDataBack);
			} else {
				str.append(CellData);
			}
		}
		if (actualiza) {
			if (!MundoDofus.mapasSalvar.contains(mapa.getID())) {
				MundoDofus.mapasSalvar.add(mapa.getID());
			}
			mapa.setMapData(str.toString());
		}
	}

	private static boolean capaUno(Mapa mapa, int celdaS) {
		String mapData = mapa.getMapData();
		for (int f = 0; f < mapData.length(); f += 10) {
			int celda = (short) (f / 10);
			if (celda == celdaS) {
				String CellData = mapData.substring(f, f + 10);
				List<Byte> celdaInfo = new ArrayList<>();
				for (int i = 0; i < CellData.length(); i++) {
					celdaInfo.add((byte) getNumeroPorValorHash(CellData.charAt(i)));
				}
				int objetoPos = ((celdaInfo.get(0) & 2) << 12) + ((celdaInfo.get(7) & 1) << 12)
						+ (celdaInfo.get(8) << 6) + celdaInfo.get(9);
				if (objetoPos != 0) {
					return false;
				} else {
					return true;
				}
			}
		}
		return false;
	}

	public static void replaceEnCelda(Mapa mapa, int celdaPoner, String dataObj, Personaje perso, boolean desmonta,
			int caminablex, int objid, int objetoidorig, int real) {
		String mapData = mapa.getMapData();
		StringBuilder str = new StringBuilder();
		boolean actualiza = false;
		boolean generador = false;
		int objetoL = 0;
		for (int f = 0; f < mapData.length(); f += 10) {
			int celda = (short) (f / 10);
			String CellData = mapData.substring(f, f + 10);
			if (celda == celdaPoner) {
				boolean capauno = capaUno(mapa, celda);
				List<Byte> celdaInfo = new ArrayList<>();
				for (int i = 0; i < CellData.length(); i++) {
					celdaInfo.add((byte) getNumeroPorValorHash(CellData.charAt(i)));
				}
				boolean lineaDeVista = (celdaInfo.get(0) & 1) != 0;
				if (!capauno && !desmonta || mapa.getPersos(celdaPoner)) {
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso,
							"No puedes colocar el objeto en esta posici�n porque ya hay algo ah�.", Colores.ROJO);
					str.append(CellData);
				} else {
					actualiza = true;
					objetoL = ((celdaInfo.get(0) & 2) << 12) + ((celdaInfo.get(7) & 1) << 12) + (celdaInfo.get(8) << 6)
							+ celdaInfo.get(9);
					if (objetoL == 4076 || objetoL == 4810 || objid == 4076 || objid == 4810) {
						generador = true;
					}
					if (!desmonta) {
						// System.out.println("ENTRA "+CellData);
						String origin = CellData.substring(7, CellData.length());
						if (mapa._personaliza.containsKey(celda)) {
							origin = mapa._personaliza.get(celda).split(",")[1];
							mapa._personaliza.remove(celda);
						}
						boolean caminable = true;
						if (caminablex == 0) {
							switch (CellData.charAt(2)) {
							case 'G':
								CellData = reeplazarEl(CellData, 2, 'a');
								break;
							case 'H':
								CellData = reeplazarEl(CellData, 2, 'b');
								break;
							case 'I':
								CellData = reeplazarEl(CellData, 2, 'c');
								break;
							case 'J':
								CellData = reeplazarEl(CellData, 2, 'd');
								break;
							case 'K':
								CellData = reeplazarEl(CellData, 2, 'e');
								break;
							case 'L':
								CellData = reeplazarEl(CellData, 2, 'f');
								break;
							case 'M':
								CellData = reeplazarEl(CellData, 2, 'g');
								break;
							case 'N':
								CellData = reeplazarEl(CellData, 2, 'h');
								break;
							}
							caminable = false;
						}
						if (!perso.objetopersonal.equals("")) {
							str.append(perso.objetopersonal);
						} else {
							str.append(CellData.substring(0, CellData.length() - 3) + "" + dataObj);
						}
						// System.out.println("SALE "+CellData.substring(0,CellData.length() -
						// 3)+""+dataObj);
						if (perso.objetopersonal.equals("")) {
							if (generador) {
								if (objetoL == 4076 || objid == 4076) {
									perso.getCuenta().generadores += 1;
								} else if (objetoL == 4810 || objid == 4810) {
									perso.getCuenta().generadores += 3;
								}
							}
							if (caminable) {
								if (mapa.getCeldas().containsKey(celda)) {
									mapa.getCeldas().remove(celda);
									mapa.getCeldas().put(celda,
											new Celda(mapa, (short) (f / 10), caminable, lineaDeVista, objid));
								} else {
									mapa.getCeldas().put(celda,
											new Celda(mapa, (short) (f / 10), caminable, lineaDeVista, objid));
								}
							}
							mapa._personaliza.put(celda, dataObj + "," + origin + "," + objetoidorig);
						} else {
							boolean andarencima = false;
							switch (perso.objetopersonal.charAt(2)) {
							case 'G':
							case 'H':
							case 'I':
							case 'J':
							case 'K':
							case 'L':
							case 'M':
							case 'N':
								andarencima = true;
								break;
							}
							if (!andarencima) {
								mapa._personaliza.put(celda, perso.objetopersonal + "," + origin + "," + objetoidorig);
							} else {
								mapa._personaliza.put(celda,
										perso.objetopersonal.substring(7, perso.objetopersonal.length()) + "," + origin
												+ "," + objetoidorig);
							}
							perso.objetopersonal = "";
						}
					} else {
						if (generador) {
							if (objetoL == 4076 || objid == 4076) {
								perso.getCuenta().generadores -= 1;
								if (perso.getCuenta().generadores < 0) {
									perso.getCuenta().generadores = 0;
								}
							} else if (objetoL == 4810 || objid == 4810) {
								perso.getCuenta().generadores -= 3;
								if (perso.getCuenta().generadores < 0) {
									perso.getCuenta().generadores = 0;
								}
							}
						}
						str.append(CellData.substring(0, CellData.length() - 3) + "" + dataObj);
						mapa._personaliza.remove(celda);
					}
				}
			} else {
				str.append(CellData);
			}
		}
		if (actualiza) {
			if (!MundoDofus.mapasSalvar.contains(mapa.getID())) {
				MundoDofus.mapasSalvar.add(mapa.getID());
			}
			if (!desmonta && objetoidorig != 0) {
				perso.borrarObjetoEliminar(real, 1, true);
			}
			if (generador) {
				if (objetoL == 4076 || objid == 4076) {
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso,
							"Acabas de posicionar un generador de Kamas y cada 1h ganar�s +300.000 Kamas",
							Colores.VERDE);
				} else if (objetoL == 4810 || objid == 4810) {
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso,
							"Acabas de posicionar un generador de Kamas y cada 1h ganar�s +900.000 Kamas",
							Colores.NARANJA);
				}
				perso.checkTimerGen();
			}
			if (!generador) {
				if (!desmonta) {
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "El objeto ha sido agregado con Éxito", Colores.VERDE);
				} else {
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "El objeto ha sido removido con Éxito", Colores.VERDE);
				}
			}
			if (Estaticos.compruebaTps(perso)) {
				mapa.setMapData(str.toString());
				GestorSalida.enviar(perso, "#bh");
				perso.teleport(perso.getMapa().getID(), perso.getCelda().getID());
				for (Personaje px : perso.getMapa().getPersos()) {
					if (px == perso) {
						continue;
					}
					GestorSalida.enviar(px, "#bh");
					px.teleport(px.getMapa().getID(), px.getCelda().getID());
				}
				// int random = Formulas.getRandomValor(1, 10);
				// GestorSalida.enviar(perso, "#kz"+mapa.getID()+((char) 0x00)+"GDM|" +
				// (mapa.getID()+celdaPoner+random) + "|" + mapa.getFecha() +
				// "|"+mapa.getCodigo());
			}
		}
	}

	public static String decompilarMapaDataOBJ(String dData, Map<Integer, String> _personaliza,
			Map<Integer, String> _limpieza, int id, Mapa mapa) {
		StringBuilder str = new StringBuilder();
		for (int f = 0; f < dData.length(); f += 10) {
			String CellData = "";
			try {
				CellData = dData.substring(f, f + 10);
			} catch (Exception exception) {
				System.out.println("Revisa el mapa: " + id);
				continue;
			}
			int celda = (short) (f / 10);
			if (_limpieza.containsKey(celda)) {
				CellData = _limpieza.get(celda).split(",")[0];
			}
			if (_personaliza.containsKey(celda)) {
				String objid = _personaliza.get(celda).split(",")[0];
				if (objid.length() == 3) {
					str.append(CellData.substring(0, CellData.length() - 3) + "" + objid);
				} else {
					switch (CellData.charAt(2)) {
					case 'G':
						CellData = reeplazarEl(CellData, 2, 'a');
						break;
					case 'H':
						CellData = reeplazarEl(CellData, 2, 'b');
						break;
					case 'I':
						CellData = reeplazarEl(CellData, 2, 'c');
						break;
					case 'J':
						CellData = reeplazarEl(CellData, 2, 'd');
						break;
					case 'K':
						CellData = reeplazarEl(CellData, 2, 'e');
						break;
					case 'L':
						CellData = reeplazarEl(CellData, 2, 'f');
						break;
					case 'M':
						CellData = reeplazarEl(CellData, 2, 'g');
						break;
					case 'N':
						CellData = reeplazarEl(CellData, 2, 'h');
						break;
					}
					str.append(CellData.substring(0, CellData.length() - 3) + "" + objid.substring(7, objid.length()));
				}
			} else {
				/*
				 * List<Byte> celdaInfo = new ArrayList<Byte>(); for (int i = 0; i <
				 * CellData.length(); i++) celdaInfo.add((byte)
				 * getNumeroPorValorHash(CellData.charAt(i))); int layerObject2 =
				 * ((celdaInfo.get(0) & 2) << 12) + ((celdaInfo.get(7) & 1) << 12) +
				 * (celdaInfo.get(8) << 6) + celdaInfo.get(9); boolean layerObjeto2Interac =
				 * ((celdaInfo.get(7) & 2) >> 1) != 0; int objeto = (layerObjeto2Interac ?
				 * layerObject2 : -1); if (mapa.getID() == 8731 && celda == 226) { boolean
				 * falsea = false; if (objeto > 0) { if
				 * (MundoDofus.getObjInteractivoModelo(objeto) != null) { falsea =
				 * MundoDofus.getObjInteractivoModelo(objeto).esCaminable(); if (!falsea) {
				 * /*switch (CellData.charAt(2)) { case 'a':
				 * str.append(reeplazarEl(CellData,2,'G')); break; case 'b':
				 * str.append(reeplazarEl(CellData,2,'H')); break; case 'c':
				 * str.append(reeplazarEl(CellData,2,'I')); break; case 'd':
				 * str.append(reeplazarEl(CellData,2,'J')); break; case 'e':
				 * str.append(reeplazarEl(CellData,2,'K')); break; case 'f':
				 * str.append(reeplazarEl(CellData,2,'L')); break; case 'g':
				 * str.append(reeplazarEl(CellData,2,'M')); break; case 'h':
				 * str.append(reeplazarEl(CellData,2,'N')); break; case 'i':
				 * str.append(reeplazarEl(CellData,2,'O')); break; case 'j':
				 * str.append(reeplazarEl(CellData,2,'P')); break; }
				 * System.out.println("ES "+CellData); str.append("HhbaeaadTS"); } else
				 * str.append(CellData); } else { str.append(CellData); } } else {
				 * str.append(CellData); } } else
				 */
				str.append(CellData);
			}
		}
		return str.toString();
	}

	public static Map<Integer, Celda> decompilarMapaData(Mapa mapa, String dData) {
		Map<Integer, Celda> celdas = new TreeMap<>();
		for (int f = 0; f < dData.length(); f += 10) {
			String CellData = dData.substring(f, f + 10);
			List<Byte> celdaInfo = new ArrayList<>();
			for (int i = 0; i < CellData.length(); i++) {
				celdaInfo.add((byte) getNumeroPorValorHash(CellData.charAt(i)));
			}
			int caminable = (celdaInfo.get(2) & 56) >> 3;// 0 = no, 1 = medio, 4 = si
			boolean lineaDeVista = (celdaInfo.get(0) & 1) != 0;
			int layerObject2 = ((celdaInfo.get(0) & 2) << 12) + ((celdaInfo.get(7) & 1) << 12) + (celdaInfo.get(8) << 6)
					+ celdaInfo.get(9);
			boolean layerObjeto2Interac = ((celdaInfo.get(7) & 2) >> 1) != 0;
			int objeto = (layerObjeto2Interac ? layerObject2 : -1);
			int CeldaID = (f / 10);
			/*
			 * if (mapa.getID() == 8731 && CeldaID == 226) { if (objeto > 0) { if
			 * (MundoDofus.getObjInteractivoModelo(objeto) != null) { if
			 * (MundoDofus.getObjInteractivoModelo(objeto).esCaminable()) caminable = 1;
			 * System.out.println("caminable "+caminable+" y "+CellData+" objeto "
			 * +objeto+" lineaDeVista "+lineaDeVista+" layerObject2 "
			 * +layerObject2+" layerObjeto2Interac "+layerObjeto2Interac); } } }
			 */
			Celda celd = new Celda(mapa, (short) CeldaID, caminable != 0, lineaDeVista, objeto);
			celdas.put(f / 10, celd);
		}
		return celdas;
	}
	
	// Mapas Maestro-Yaco 28/12/2025
	// =========================================================================
    // SISTEMA DE DESENCRIPTACIÓN XOR (PARA PAQUETES ù Y MAPAS)
    // =========================================================================

    /**
     * Desencripta datos usando el algoritmo XOR de Dofus.
     * Se usa tanto para paquetes de red (ù) como para la data de los mapas (GDM).
     */
    public static String decypherData(String data, String key, int offset) {
        StringBuilder out = new StringBuilder();
        int keyLen = key.length();
        
        if (keyLen == 0) return data; // Evitar división por cero

        for (int i = 0; i < data.length(); i++) {
            // Operación XOR entre el caracter de la data y el de la llave (usando el offset)
            char dataChar = data.charAt(i);
            char keyChar = key.charAt((i + offset) % keyLen);
            out.append((char) (dataChar ^ keyChar));
        }
        
        // El cliente Modern a menudo espera que el resultado pase por un unescape
        return unescape(out.toString());
    }

    /**
     * Prepara la llave hexadecimal del mapa para ser usada en decypherData
     */
    public static String prepareKey(String d) {
        StringBuilder _loc3 = new StringBuilder("");
        try {
            for (int _loc4 = 0; _loc4 < d.length(); _loc4 += 2) {
                _loc3.append((char) Integer.parseInt(d.substring(_loc4, _loc4 + 2), 16));
            }
        } catch (Exception e) {
            return d;
        }
        return unescape(_loc3.toString());
    }

    
    // Esta constante es NECESARIA para el método checksum que calcula el offset de la llave
    private static final char[] HEX_CHARS = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    /**
     * Helper para procesar caracteres especiales (%xx) en llaves y paquetes.
     * Es fundamental para que el XOR no falle con caracteres raros.
     */
    public static String unescape(String s) {
        if (s == null) return null;
        try {
            // Usamos "UTF-8" (con guión) que es el estándar de Java
            return java.net.URLDecoder.decode(s, "UTF-8");
        } catch (Exception e) {
            return s;
        }
    }

	private static String decypherData(String d, String k, String checksum) {
		int c = Integer.parseInt(checksum, 16) * 2;
		StringBuilder _loc5 = new StringBuilder();
		int _loc6 = k.length();
		int _loc7 = 0;
		int _loc9 = 0;
		for (; _loc9 < d.length(); _loc9 += 2) {
			_loc5.append(
					(char) (Integer.parseInt(d.substring(_loc9, _loc9 + 2), 16) ^ k.codePointAt((_loc7 + c) % _loc6)));
			_loc7++;
		}
		_loc5 = new StringBuilder(unescape(_loc5.toString()));
		return _loc5.toString();
	}

	private static char checksum(String s) {
		int _loc3 = 0;
		int _loc4 = 0;
		for (; _loc4 < s.length(); _loc4++) {
			_loc3 += s.codePointAt(_loc4) % 16;
		}
		return HEX_CHARS[_loc3 % 16];
	}

	public static String aUTF(String entrada) {
		String _out = "";
		try {
			_out = new String(entrada.getBytes("UTF8"));
		} catch (Exception e) {
			System.out.println("Conversion en UTF-8 fallida! : " + e.getMessage());
		}
		return _out;
	}

	public static String aUnicode(String entrada) {
		String _out = "";
		try {
			_out = new String(entrada.getBytes(), "UTF8");
		} catch (Exception e) {
			System.out.println("Conversion en UNICODE fallida! : " + e.getMessage());
		}
		return _out;
	}
	
	// llaves mapas 1.43.7 Maestro-Yaco 28/12/2025
	public static String generarLlaveHex(int longitud) {
	    String caracteres = "0123456789abcdef";
	    StringBuilder sb = new StringBuilder();
	    java.util.Random rnd = new java.util.Random();
	    while (sb.length() < longitud) {
	        sb.append(caracteres.charAt(rnd.nextInt(caracteres.length())));
	    }
	    return sb.toString();
	}
}
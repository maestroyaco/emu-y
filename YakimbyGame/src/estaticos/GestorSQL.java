package estaticos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import estaticos.MundoDofus.Area;
import estaticos.MundoDofus.Drop;
import estaticos.MundoDofus.Duo;
import estaticos.MundoDofus.ExpNivel;
import estaticos.MundoDofus.ItemSet;
import estaticos.MundoDofus.ObjInteractivoModelo;
import estaticos.MundoDofus.SubArea;
import variables.Accion;
import variables.Animacion;
import variables.Casa;
import variables.Cofre;
import variables.Cuenta;
import variables.Dragopavo;
import variables.Encarnacion;
import variables.Gremio;
import variables.Gremio.MiembroGremio;
import variables.Hechizo;
import variables.Hechizo.StatsHechizos;
import variables.LibrosRunicos;
import variables.Mapa;
import variables.Mapa.Cercado;
import variables.Mascota;
import variables.Mascota.MascotaModelo;
import variables.Mercadillo;
import variables.Mercadillo.ObjetoMercadillo;
import variables.MobModelo;
import variables.NPCModelo;
import variables.NPCModelo.PreguntaNPC;
import variables.NPCModelo.RespuestaNPC;
import variables.Objeto;
import variables.Objeto.ObjetoModelo;
import variables.Objevivo;
import variables.Oficio;
import variables.Personaje;
import variables.Prisma;
import variables.RankingPVP;
import variables.Recaudador;
import variables.Reto;
import variables.Tienda;
import variables.Tutorial;

public class GestorSQL {
	private static Connection bdDofusOnline;
	// private static Timer timerComienzo;
	// private static boolean necesitaComenzar;

	private static void closeResultSet(ResultSet RS) {
		try {
			if (RS != null) {
				RS.getStatement().close();
				RS.close();
			}
		} catch (SQLException e) {
			Emu.creaLogs(e);
		}
	}

	private static void closePreparedStatement(PreparedStatement p) {
		try {
			if (p != null) {
				p.clearParameters();
				p.close();
			}
		} catch (SQLException e) {
			Emu.creaLogs(e);
		}
	}

	private synchronized static ResultSet executeQuery(String consultaSQL, String DBNAME) throws SQLException {
		if (!Emu.estaIniciado) {
			return null;
		}
		Connection DB = bdDofusOnline;
		PreparedStatement stat = DB.prepareStatement(consultaSQL);
		ResultSet RS = stat.executeQuery();
		stat.setQueryTimeout(300);
		return RS;
	}

	private synchronized static PreparedStatement nuevaTransaccion(String consultaSQL, Connection coneccionSQL)
			throws SQLException {
		PreparedStatement aRetornar = coneccionSQL.prepareStatement(consultaSQL);
		// necesitaComenzar = true;
		return aRetornar;
	}

	/*
	 * private static int buclev = 0; public synchronized static void
	 * comenzarTransacciones () { try { if (bdDofusOnline.isClosed()) {
	 * cerrarCons(); IniciarConexion(); } bdDofusOnline.commit(); } catch
	 * (SQLException e) { System.out.println("SQL ERROR:" + e.getMessage());
	 * Emu.creaLogs(e); if (buclev >= 10) { return; } buclev += 1;
	 * comenzarTransacciones(); } }
	 */

	synchronized static void cerrarCons() {
		try {
			bdDofusOnline.close();
		} catch (Exception e) {
			System.out.println("Error en la ventana de conexiones SQL:" + e.getMessage());
			Emu.creaLogs(e);
		}
	}

	static final boolean IniciarConexion() {
		try {
			Logger.getLogger("com.zaxxer.hikari.pool.PoolBase").setLevel(Level.OFF);
			Logger.getLogger("com.zaxxer.hikari.pool.HikariPool").setLevel(Level.OFF);
			Logger.getLogger("com.zaxxer.hikari.HikariDataSource").setLevel(Level.OFF);
			Logger.getLogger("com.zaxxer.hikari.HikariConfig").setLevel(Level.OFF);
			Logger.getLogger("com.zaxxer.hikari.util.DriverDataSource").setLevel(Level.OFF);
			HikariConfig configEstatica = new HikariConfig();
			configEstatica.setDataSourceClassName("org.mariadb.jdbc.MySQLDataSource");
			configEstatica.addDataSourceProperty("serverName", Emu.BD_HOST);
			configEstatica.addDataSourceProperty("port", Emu.BD_PORT);
			configEstatica.addDataSourceProperty("databaseName", Emu.BDDOFUSONLINE);
			configEstatica.addDataSourceProperty("user", Emu.BD_USUARIO);
			configEstatica.addDataSourceProperty("password", Emu.BD_PASS);
			configEstatica.setAutoCommit(true);
			configEstatica.setMaximumPoolSize(50);
			configEstatica.setMinimumIdle(1);
			configEstatica.setPoolName("mysql");
			@SuppressWarnings("resource")
			HikariDataSource ds = new HikariDataSource(configEstatica);
			bdDofusOnline = ds.getConnection();
			if (!bdDofusOnline.isValid(1000)) {
				System.out.println("SQLError : Conexion a la BDD invalida!");
				System.exit(0);
				return false;
			}
			// necesitaComenzar = false;
			// TIMER(true);
			return true;
		} catch (SQLException e) {
			System.out.println("Host: " + Emu.BD_HOST + " Puerto: " + Emu.BD_PORT + " BD: " + Emu.BDDOFUSONLINE
					+ " USUARIO: " + Emu.BD_USUARIO + " PASS: " + Emu.BD_PASS);
			Emu.creaLogs(e);
			System.exit(0);
			return false;
		}
	}

	public static int getSigIDPersonaje() {
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT id FROM personajes ORDER BY id DESC LIMIT 1;", Emu.BDDOFUSONLINE);
			if (!RS.first()) {
				return 1;
			}
			int id = RS.getInt("id");
			id++;
			return id;
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
			Emu.cerrarServer("GET SIDIDPJ");
		} finally {
			closeResultSet(RS);
		}
		return 0;
	}

	public static int getPuntosCuenta(int cuentaID) {
		int puntos = 0;
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT `puntos` FROM `cuentas` WHERE `id` = " + cuentaID + ";", Emu.BDDOFUSONLINE);
			boolean encontrado = RS.first();
			if (encontrado) {
				puntos = RS.getInt("puntos");
			}
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closeResultSet(RS);
		}
		return puntos;
	}

	public static void setPuntoCuenta(int puntos, int cuentaID) {
		PreparedStatement p = null;
		String consultaSQL = "UPDATE `cuentas` SET `puntos`=? WHERE `id`= ?";
		try {
			p = nuevaTransaccion(consultaSQL, bdDofusOnline);
			p.setLong(1, puntos);
			p.setInt(2, cuentaID);
			p.execute();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		} finally {
			closePreparedStatement(p);
		}
	}

	public static void setTiempoLootCuenta(String tiempo, int cuentaID) {
		PreparedStatement p = null;
		String consultaSQL = "UPDATE `cuentas` SET `tiempoLoot`=? WHERE `id`= ?";
		try {
			p = nuevaTransaccion(consultaSQL, bdDofusOnline);
			p.setString(1, tiempo);
			p.setInt(2, cuentaID);
			p.execute();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		} finally {
			closePreparedStatement(p);
		}
	}

	public static void setRegalosCuenta2(String tiempo, int cuentaID) {
		PreparedStatement p = null;
		String consultaSQL = "UPDATE `cuentas` SET `tieneRegalos`=? WHERE `id`= ?";
		try {
			p = nuevaTransaccion(consultaSQL, bdDofusOnline);
			p.setString(1, tiempo);
			p.setInt(2, cuentaID);
			p.execute();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		} finally {
			closePreparedStatement(p);
		}
	}

	@SuppressWarnings("deprecation")
	public static void INSERT_DENUNCIAS(String nombre, String tipo, String tema, String detalle) {
		String consultaSQL = "INSERT INTO denuncias(`perso`, `tipo`,`asunto`,`detalle`,`fecha`) VALUES(?,?,?,?,?);";
		PreparedStatement p = null;
		Date fechaActual = new Date();
		try {
			p = nuevaTransaccion(consultaSQL, bdDofusOnline);
			p.setString(1, nombre);
			p.setString(2, tipo);
			p.setString(3, tema);
			p.setString(4, detalle);
			p.setString(5, fechaActual.toLocaleString());
			p.executeUpdate();
		} catch (Exception e) {
			System.out.println("Error SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
			Emu.creaLogs(e);
		} finally {
			closePreparedStatement(p);
		}
	}

	public static void UPDATE_COLORES_PJ(Personaje perso) {
		String consultaSQL = "UPDATE `personajes` SET `color1` = ?, `color2`= ?, `color3` = ? WHERE `id` = ? ;";
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion(consultaSQL, bdDofusOnline);
			p.setInt(1, perso.getColor1());
			p.setInt(2, perso.getColor2());
			p.setInt(3, perso.getColor3());
			p.setInt(4, perso.getID());
			p.executeUpdate();
		} catch (Exception e) {
			System.out.println("Error SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
			Emu.creaLogs(e);
		} finally {
			closePreparedStatement(p);
		}
	}

	public static void SALVAR_CUENTA(Cuenta cuenta) {
		PreparedStatement p = null;
		try {
			String consultaSQL = "UPDATE cuentas SET `kamas` = ?,`objetos` = ?,`gm` = ?,`establo` = ?,`baneado` = ?,"
					+ "`amigos` = ?,`enemigos` = ?,`ultimaIP` = ?,`ultimaConexion` = ?,`logeado` = ?,`generadores` = ?,`tiempogen` = ?,`tutoriales` = ? WHERE `id` = ?;";
			p = nuevaTransaccion(consultaSQL, bdDofusOnline);
			p.setLong(1, cuenta.getKamasBanco());
			p.setString(2, cuenta.stringBancoObjetosBD());
			p.setInt(3, cuenta.getRango());
			p.setString(4, cuenta.stringIDsEstablo());
			p.setInt(5, (cuenta.estaBaneado() ? 1 : 0));
			p.setString(6, cuenta.analizarListaAmigosABD());
			p.setString(7, cuenta.stringListaEnemigosABD());
			p.setString(8, cuenta.getActualIP());
			p.setString(9, cuenta.getUltimaConeccion());
			p.setInt(10, cuenta.enLinea() ? 1 : 0);
			p.setInt(11, cuenta.generadores);
			p.setInt(12, cuenta.tiempopasado);
			p.setString(13, cuenta.getTutoriales());
			p.setInt(14, cuenta.getID());
			p.executeUpdate();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closePreparedStatement(p);
		}
	}

	public static void ACTUALIZAR_PRIMERA_VEZ(Cuenta cuenta, int valor) {
		PreparedStatement p = null;
		String consultaSQL = "UPDATE cuentas SET primeravez = ? WHERE `id` = ?;";
		try {
			p = nuevaTransaccion(consultaSQL, bdDofusOnline);
			p.setInt(1, valor);
			p.setInt(2, cuenta.getID());
			p.executeUpdate();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closePreparedStatement(p);
		}
	}

	/*
	 * public static void CARGAEMAILS () { ResultSet RS = null; try { RS =
	 * executeQuery("SELECT email from cuentas;", Emu.BDDOFUSONLINE); while
	 * (RS.next()) { String email = RS.getString("email"); if (email.isEmpty() ||
	 * !email.contains("@")) continue; if (!EnvioEmails.EMAIL_TO.contains(email))
	 * EnvioEmails.EMAIL_TO.add(email); } } catch (SQLException e) {
	 * System.out.println("ERROR SQL: " + e.getMessage()); Emu.creaLogs(e); }
	 * finally { closeResultSet(RS); } }
	 */

	/*
	 * static void ARREGLAIA () { ResultSet RS = null; try { RS =
	 * executeQuery("SELECT * from mobs_back;", Emu.BDDOFUSONLINE); while
	 * (RS.next()) { int idmob = RS.getInt("id"); int idia = RS.getInt("tipoIA");
	 * String nombre = RS.getString("nombre"); ARREGLAIA2(idmob, nombre, idia); } }
	 * catch (SQLException e) { System.out.println("ERROR SQL: " + e.getMessage());
	 * Emu.creaLogs(e); } finally { closeResultSet(RS); } }
	 *
	 * static void ARREGLAIA2 (int idmob, String nombre, int tipoia) {
	 * PreparedStatement p = null; try { p =
	 * nuevaTransaccion("UPDATE `mobs` SET `tipoIA` = ?,`nombre` = ? WHERE id = ?;",
	 * bdDofusOnline); p.setInt(1, tipoia); p.setString(2, nombre); p.setInt(3,
	 * idmob); p.execute(); } catch (SQLException e) { Emu.creaLogs(e); } finally {
	 * closePreparedStatement(p); } }
	 */
	static void CARGAR_RECETAS() {
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT * from recetas;", Emu.BDDOFUSONLINE);
			while (RS.next()) {
				ArrayList<Duo<Integer, Integer>> arrayDuos = new ArrayList<>();
				boolean continua = false;
				for (String str : RS.getString("receta").split(";")) {
					try {
						String[] s = str.split("\\*");
						int idModeloObj = Integer.parseInt(s[0]);
						if (MundoDofus.getObjModelo(idModeloObj) == null) {
							continue;
						}
						int cantidad = Integer.parseInt(s[1]);
						arrayDuos.add(new Duo<>(idModeloObj, cantidad));
						continua = true;
					} catch (Exception e) {
						Emu.creaLogs(e);
						continua = false;
					}
				}
				if (continua) {
					MundoDofus.addReceta(RS.getInt("id"), arrayDuos);
				}
			}
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closeResultSet(RS);
		}
	}

	public static void CARGAR_ZONAS() {
		ResultSet RS = null;
		try {
			RS  = executeQuery("SELECT * FROM zonas;", Emu.BDDOFUSONLINE);
			while (RS.next()) {
				MundoDofus.ZONAS.put(RS.getShort("mapa"), RS.getShort("celda"));
				MundoDofus.LISTA_ZONAS += "|" + RS.getString("nombre") + ";" + RS.getShort("mapa");
			}

		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closeResultSet(RS);
		}
	}

	static void CARGAR_GREMIOS() {
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT * from gremios;", Emu.BDDOFUSONLINE);
			while (RS.next()) {
				MundoDofus.addGremio(new Gremio(RS.getInt("id"), RS.getString("nombre"), RS.getString("emblema"),
						RS.getInt("nivel"), RS.getLong("xp"), RS.getInt("capital"), RS.getInt("recaudadores"),
						RS.getString("hechizos"), RS.getString("stats"), RS.getString("tiempo"),
						RS.getInt("alineacion"), RS.getInt("puntos")), false);
			}
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closeResultSet(RS);
		}
	}

	static void CARGAR_OBJETIVOSDIARIOS() {
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT * from objetivosdiarios;", Emu.BDDOFUSONLINE);
			while (RS.next()) {
				String muestra = RS.getString("muestra");
				int objid = RS.getInt("id");
				String nombre = RS.getString("nombre");
				String tipo = RS.getString("tipo");
				String descrip = RS.getString("descripcion");
				int itemid = RS.getInt("itemid");
				int neces = RS.getInt("necesario");
				String magu = RS.getString("magueo");
				String mobcant = RS.getString("mobcant");
				switch (muestra) {
				case "facil":
					MundoDofus.ObjetivosFaciles.put(objid, nombre + "," + tipo + "," + descrip + "," + itemid + ","
							+ neces + "," + magu + "," + mobcant + ",puntosvip,0");
					break;
				case "ambos":
					MundoDofus.ObjetivosFaciles.put(objid, nombre + "," + tipo + "," + descrip + "," + itemid + ","
							+ neces + "," + magu + "," + mobcant + ",puntosvip,0");
					MundoDofus.ObjetivosDiarios.put(objid, nombre + "," + tipo + "," + descrip + "," + itemid + ","
							+ neces + "," + magu + "," + mobcant + ",puntosvip,0");
					break;
				case "normal":
					MundoDofus.ObjetivosDiarios.put(objid, nombre + "," + tipo + "," + descrip + "," + itemid + ","
							+ neces + "," + magu + "," + mobcant + ",puntosvip,0");
					break;
				}
			}
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closeResultSet(RS);
		}
	}

	static void CARGAR_PASEBATALLA() {
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT * from pasebatalla ORDER BY id ASC;", Emu.BDDOFUSONLINE);
			while (RS.next()) {
				int idpos = RS.getInt("id");
				int idesp = RS.getInt("idespec");
				int puntos = RS.getInt("puntos");
				String tipo = RS.getString("tipo");
				int vip = RS.getInt("vip");
				MundoDofus.Prestigios.put(idpos, idesp + "," + puntos + "," + tipo + "," + vip);
			}
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closeResultSet(RS);
		}
	}

	static void CARGAR_MIEMBROS_GREMIO() {
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT * from miembros_gremio;", Emu.BDDOFUSONLINE);
			while (RS.next()) {
				Gremio G = MundoDofus.getGremio(RS.getInt("gremio"));
				if (G == null) {
					BORRAR_MIEMBRO_GREMIO(RS.getInt("id"));
					continue;
				}
				G.addMiembro(RS.getInt("id"), RS.getString("nombre"), RS.getInt("nivel"), RS.getInt("gfxid"),
						RS.getInt("rango"), RS.getByte("porcXp"), RS.getLong("xpDonada"), RS.getInt("derechos"),
						RS.getString("ultimaConexion").replaceAll("-", "~"));
			}
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closeResultSet(RS);
		}
		return;
	}

	static void CARGAR_ITEMSETS() {
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT * from objetos_set;", Emu.BDDOFUSONLINE);
			while (RS.next()) {
				MundoDofus.addItemSet(new ItemSet(RS.getInt("id"), RS.getString("objetos"), RS.getString("bonus")));
			}
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closeResultSet(RS);
		}
	}

	static void CARGAR_INTERACTIVOS() {
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT * from objetos_interactivos;", Emu.BDDOFUSONLINE);
			while (RS.next()) {
				MundoDofus.addObjInteractivo(new ObjInteractivoModelo(RS.getInt("id"), RS.getInt("recarga"),
						RS.getInt("duracion"), RS.getInt("accionPJ"), RS.getInt("caminable") == 1, RS.getInt("nivel")));
			}
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closeResultSet(RS);
		}
	}

	static void CARGAR_OFICIOS() {
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT * from oficios;", Emu.BDDOFUSONLINE);
			while (RS.next()) {
				MundoDofus
						.addOficio(new Oficio(RS.getInt("id"), RS.getString("herramientas"), RS.getString("recetas")));
			}
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closeResultSet(RS);
		}
	}

	static void CARGAR_AREA() {
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT * from areas;", Emu.BDDOFUSONLINE);
			while (RS.next()) {
				Area A = new Area(RS.getInt("id"), RS.getInt("superarea"), RS.getString("nombre"),
						RS.getInt("alineacion"), RS.getInt("prisma"));
				MundoDofus.addArea(A);
				A.getSuperArea().addArea(A);
			}
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closeResultSet(RS);
		}
	}

	static void CARGAR_SUBAREA() {
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT * from subareas;", Emu.BDDOFUSONLINE);
			while (RS.next()) {
				SubArea SA = new SubArea(RS.getInt("id"), RS.getInt("area"), RS.getInt("alineacion"),
						RS.getString("nombre"), RS.getInt("conquistable"), RS.getInt("prisma"));
				MundoDofus.addSubArea(SA);
				if (SA.getArea() != null) {
					SA.getArea().addSubArea(SA);
				}
			}
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closeResultSet(RS);
		}
	}

	static void ACTUALIZAR_SUBAREA(SubArea subarea) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("UPDATE `subareas` SET `alineacion` = ?, `prisma` = ? WHERE id = ?;", bdDofusOnline);
			p.setInt(1, subarea.getAlineacion());
			p.setInt(2, subarea.getPrismaID());
			p.setInt(3, subarea.getID());
			p.execute();
		} catch (SQLException e) {
			Emu.creaLogs(e);
		} finally {
			closePreparedStatement(p);
		}
	}

	static void ACTUALIZAR_AREA(Area area) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("UPDATE `areas` SET `alineacion` = ?, `prisma` = ? WHERE id = ?;", bdDofusOnline);
			p.setInt(1, area.getAlineacion());
			p.setInt(2, area.getPrismaID());
			p.setInt(3, area.getID());
			p.execute();
		} catch (SQLException e) {
			Emu.creaLogs(e);
		} finally {
			closePreparedStatement(p);
		}
	}

	static boolean BORRAR_PERSONAJE(Personaje perso) {
		int id = perso.getID();
		PreparedStatement p = null;
		PreparedStatement pd = null;
		try {
			p = nuevaTransaccion("DELETE FROM `personajes` WHERE `id` = ?;", bdDofusOnline);
			p.setInt(1, id);
			p.execute();
			String obj = perso.getObjetosPersonajePorID(",");
			if (!obj.equals("")) {
				pd = nuevaTransaccion("DELETE FROM `objetos` WHERE id IN (" + obj + ");", bdDofusOnline);
				pd.execute();
			}
			if (perso.contarTienda() > 0) {
				String tienda = perso.getStringTienda();
				for (String item : tienda.split("\\|")) {
					if (item.equals("")) {
						continue;
					}
					MundoDofus.borrarObjTienda(Integer.parseInt(item));
				}
			}
			return true;
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
			return false;
		} finally {
			closePreparedStatement(p);
			closePreparedStatement(pd);
		}
	}

	public static boolean AGREGAR_PJ_EN_BD(Personaje perso, String objetos) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion(
					"INSERT INTO personajes(`id`,`nombre`,`sexo`,`clase`,`color1`,`color2`,`color3`,`kamas`,`puntosHechizo`,`capital`,`energia`,`nivel`,`xp`,`talla`,`gfx`,`cuenta`,`celda`,`mapa`,`hechizos`,`objetos`,`tienda`,`misiones`,`objMisiones`,`objetivosdiarios`,`datos`,`titulos`,`pasebatalla`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,'','','','','','','');",
					bdDofusOnline);
			p.setInt(1, perso.getID());
			p.setString(2, perso.getNombre());
			p.setInt(3, perso.getSexo());
			p.setInt(4, perso.getClase(true));
			p.setInt(5, perso.getColor1());
			p.setInt(6, perso.getColor2());
			p.setInt(7, perso.getColor3());
			p.setLong(8, perso.getKamas());
			p.setInt(9, perso.getPuntosHechizos());
			p.setInt(10, perso.getCapital());
			p.setInt(11, perso.getEnergia());
			p.setInt(12, perso.getNivel());
			p.setLong(13, perso.getExperiencia());
			p.setInt(14, perso.getTalla());
			p.setInt(15, perso.getGfxID());
			p.setInt(16, perso.getCuentaID());
			p.setInt(17, perso.getCelda().getID());
			p.setInt(18, perso.getMapa().getID());
			p.setString(19, perso.analizarHechizosABD());
			p.setString(20, objetos);
			p.execute();
			return true;
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			return false;
		} finally {
			closePreparedStatement(p);
		}
	}

	static void CARGAR_EXPERIENCIA() {
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT * from experiencia;", Emu.BDDOFUSONLINE);
			while (RS.next()) {
				MundoDofus.addExpLevel(RS.getInt("nivel"), new ExpNivel(RS.getLong("personaje"), RS.getInt("oficio"),
						RS.getInt("dragopavo"), RS.getInt("pvp"), RS.getInt("encarnacion")));
			}
		} catch (SQLException e) {
			System.out.println("Experiencia ERROR SQL: " + e.getMessage());
			System.exit(1);
		} finally {
			closeResultSet(RS);
		}
	}

	static void CARGAR_MISIONES() {
		ResultSet RS = null;
		ResultSet RC = null;
		try {
			RS = executeQuery("SELECT * from misiones;", Emu.BDDOFUSONLINE);
			RC = executeQuery("SELECT * from misiones_objetivo;", Emu.BDDOFUSONLINE);
			while (RS.next()) {
				MundoDofus.addMisionEtapa(RS.getInt("misionid"), RS.getString("etapa") + "-" + RS.getInt("kamas") + "-"
						+ RS.getString("objetos") + "-" + RS.getLong("xp") + "-" + RS.getInt("preguntaID"));
				MundoDofus.addMisionObj(RS.getInt("misionid"), RS.getString("objetivos"));
			}
			while (RC.next()) {
				MundoDofus.addMisionObjs(RC.getInt("id"),
						RC.getInt("tipo") + "-" + RC.getString("objetos") + "-" + RC.getString("npc") + "-"
								+ RC.getString("monstruos") + "-" + RC.getString("acabamision") + "-"
								+ RC.getString("darmision"));
			}
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.exit(1);
		} finally {
			closeResultSet(RS);
			closeResultSet(RC);
		}
	}

	public static void UPDATE_HECHIZOS(int hechizoid, String lvl1, String lvl2, String lvl3, String lvl4, String lvl5,
			String lvl6) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion(
					"UPDATE `hechizos` SET `nivel1`=?, `nivel2`= ?, `nivel3`= ?, `nivel4`= ?, `nivel5`= ?, `nivel6`= ? WHERE `id`= ?",
					bdDofusOnline);
			p.setString(1, lvl1);
			p.setString(2, lvl2);
			p.setString(3, lvl3);
			p.setString(4, lvl4);
			p.setString(5, lvl5);
			p.setString(6, lvl6);
			p.setInt(7, hechizoid);
			p.execute();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
		} finally {
			closePreparedStatement(p);
		}
	}

	public static void CAMBIAR_SEXO_CLASE(Personaje perso) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("UPDATE `personajes` SET `sexo`=?, `clase`= ?, `hechizos`= ? WHERE `id`= ?",
					bdDofusOnline);
			p.setInt(1, perso.getSexo());
			p.setInt(2, perso.getClase(true));
			p.setString(3, perso.analizarHechizosABD());
			p.setInt(4, perso.getID());
			p.execute();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
		} finally {
			closePreparedStatement(p);
		}
	}

	public static void ACTUALIZAR_NOMBRE(Personaje perso) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("UPDATE `personajes` SET `nombre` = ? WHERE `id` = ? ;", bdDofusOnline);
			p.setString(1, perso.getNombre());
			p.setInt(2, perso.getID());
			p.executeUpdate();
		} catch (Exception e) {
			System.out.println("ERROR SQL: " + e.getMessage());
		} finally {
			closePreparedStatement(p);
		}
	}

	public static void SALVAR_PERSONAJE(Personaje perso, boolean salvarObjetos) {
		if (perso == null) {
			return;
		}
		String consultaSQL = "UPDATE `personajes` SET `mostrarAmigos`= ?,`canal`= ?,`porcVida`= ?,`mapa`= ?,`celda`= ?,"
				+ "`vitalidad`= ?,`fuerza`= ?,`sabiduria`= ?,`inteligencia`= ?,`suerte`= ?,`agilidad`= ?,`alineacion`= ?,"
				+ "`honor`= ?,`deshonor`= ?,`nivelAlin`= ?,`gfx`= ?,`xp`= ?,`nivel`= ?,`energia`= ?,`capital`= ?,"
				+ "`puntosHechizo`= ?,`kamas`= ?,`talla` = ?,`hechizos` = ?,`objetos` = ?,`posSalvada` = ?,"
				+ "`xpMontura` = ?,`zaaps` = ?,`montura` = ?,`mostrarAlineacion` = ?,`titulo` = ?,`esposo` = ?,`tienda` = ?,"
				+ "`mercante` = ?,`sFuerza`=?,`sInteligencia`=?,`sAgilidad`=?,`sSuerte`=?,`sVitalidad`=?,`sSabiduria`=?, `restriccionesA`= ?, `restriccionesB`= ?, `oficios`= ?, `encarnacion`=?, `koliseo`=?, `objetivosdiarios`=?, `dia`=?, `actualprecio`=?, `resets`=?, `desafdiario`=?, `datos`=?, `titulos`=?, `etapahechi`=?, `claseesp`=?, `pasebatalla`=? WHERE `id` = ? LIMIT 1 ;";
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion(consultaSQL, bdDofusOnline);
			p.setInt(1, (perso.mostrarConeccionAmigo() ? 1 : 0));
			p.setString(2, perso.getCanal());
			p.setInt(3, perso.getPorcPDV());
			if (perso.getMapa() != null) {
				p.setInt(4, perso.getMapa().getID());
				p.setInt(5, perso.getCelda().getID());
			} else {
				p.setInt(4, 7411);
				p.setInt(5, 280);
			}
			p.setInt(6, perso.getBaseStats().getEfecto(CentroInfo.STATS_ADD_VITALIDAD));
			p.setInt(7, perso.getBaseStats().getEfecto(CentroInfo.STATS_ADD_FUERZA));
			p.setInt(8, perso.getBaseStats().getEfecto(CentroInfo.STATS_ADD_SABIDURIA));
			p.setInt(9, perso.getBaseStats().getEfecto(CentroInfo.STATS_ADD_INTELIGENCIA));
			p.setInt(10, perso.getBaseStats().getEfecto(CentroInfo.STATS_ADD_SUERTE));
			p.setInt(11, perso.getBaseStats().getEfecto(CentroInfo.STATS_ADD_AGILIDAD));
			p.setInt(12, perso.getAlineacion());
			p.setInt(13, perso.getHonor());
			p.setInt(14, perso.getDeshonor());
			p.setInt(15, perso.getNivelAlineacion());
			p.setInt(16, perso.getGfxID());
			p.setLong(17, perso.getExperiencia());
			p.setInt(18, perso.getNivel());
			p.setInt(19, perso.getEnergia());
			p.setInt(20, perso.getCapital());
			p.setInt(21, perso.getPuntosHechizos());
			p.setLong(22, perso.getKamas());
			p.setInt(23, perso.getTalla());
			p.setString(24, perso.analizarHechizosABD());
			p.setString(25, perso.stringObjetosABD());
			p.setString(26, perso.getPtoSalvada());
			p.setInt(27, perso.getXpDonadaMontura());
			p.setString(28, perso.stringZaaps());
			p.setInt(29, (perso.getMontura() != null ? perso.getMontura().getID() : -1));
			p.setInt(30, (perso.mostrarAlas() ? 1 : 0));
			p.setInt(31, perso.getTitulo());
			p.setInt(32, perso.getEsposo());
			p.setString(33, perso.getStringTienda());
			p.setInt(34, perso.getMercante());
			p.setInt(35, perso.getScrollFuerza());
			p.setInt(36, perso.getScrollInteligencia());
			p.setInt(37, perso.getScrollAgilidad());
			p.setInt(38, perso.getScrollSuerte());
			p.setInt(39, perso.getScrollVitalidad());
			p.setInt(40, perso.getScrollSabiduria());
			p.setLong(41, perso.getRestriccionesA());
			p.setLong(42, perso.getRestriccionesB());
			p.setString(43, perso.stringOficios());
			p.setInt(44, perso.getIDEncarnacion());
			p.setString(45, perso._kolivictoriasyderrotas + "-" + perso._kolirango + ";" + perso._kolipuntos); // victorias;derrotas-puntos;rango
			p.setString(46, perso.getObjetivosSave());
			p.setInt(47, perso.diadeo);
			p.setInt(48, perso.actualPrecio);
			p.setInt(49, perso._resets);
			p.setInt(50, perso.desafdiario);
			p.setString(51, perso.getAcciones());
			p.setString(52, perso.getTitulos());
			p.setInt(53, perso.etapa);
			p.setInt(54, perso.buffClase);
			p.setString(55, perso.getCanjeados() + ";" + perso.PuntosPrestigio + ";" + perso.paginaCanj);
			p.setInt(56, perso.getID());
			p.executeUpdate();
		} catch (Exception e) {
			Emu.creaLogs(e);
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
			System.out.println("Personaje salvado " + perso.getNombre());
			System.out.println("NO SE PUDO SALVAR EL PERSO: " + perso.getNombre() + " 1 "
					+ (perso.mostrarConeccionAmigo() ? 1 : 0) + " 2 " + perso.getCanal() + " 3 " + perso.getPorcPDV()
					+ " 4 " + perso.getMapa().getID() + " 5 " + perso.getCelda().getID());
		} finally {
			closePreparedStatement(p);
			if (perso.getMiembroGremio() != null) {
				ACTUALIZAR_MIEMBRO_GREMIO(perso.getMiembroGremio());
			}
			if (salvarObjetos) {
				consultaSQL = "REPLACE INTO `objetos` VALUES(?,?,?,?,?,?);";
				try {
					p = nuevaTransaccion(consultaSQL, bdDofusOnline);
					try {
						Map<Integer, Objeto> objs = perso.getObjetos();
						for (Objeto obj : objs.values()) {
							if (obj == null) {
								continue;
							}
							p.setInt(1, obj.getID());
							p.setInt(2, obj.getIDModelo());
							p.setInt(3, obj.getCantidad());
							p.setInt(4, obj.getPosicion());
							if (obj.getModelo().getTipo() == 18) {
								p.setString(5, obj.convertirStatsAString(1));
							} else {
								p.setString(5, obj.convertirStatsAString(0));
							}
							p.setInt(6, obj.getObjeviID());
							p.execute();
						}
					} catch (Exception e) {
					}
					if (perso.getCuenta() != null) {
						try {
							String strac = perso.getObjetosBancoPorID(":");
							for (String idStr : strac.split(":")) {
								if (idStr.equals("")) {
									continue;
								}
								int id = Integer.parseInt(idStr);
								Objeto obj = MundoDofus.getObjeto(id);
								if (obj == null) {
									continue;
								}
								p.setInt(1, obj.getID());
								p.setInt(2, obj.getIDModelo());
								p.setInt(3, obj.getCantidad());
								p.setInt(4, obj.getPosicion());
								p.setString(5, obj.convertirStatsAString(0));
								p.setInt(6, obj.getObjeviID());
								p.execute();
							}
						} catch (Exception e) {
							Emu.creaLogs(e);
						}
					}
				} catch (Exception e1) {
					Emu.creaLogs(e1);
				} finally {
					closePreparedStatement(p);
				}
			}
		}
	}

	static void CARGAR_MODELOS_OBJETOS() {
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT * from objetos_modelo;", Emu.BDDOFUSONLINE);
			while (RS.next()) {
				MundoDofus.addObjModelo(new ObjetoModelo(RS.getInt("id"), RS.getString("statsModelo"),
						RS.getString("nombre"), RS.getInt("tipo"), RS.getInt("nivel"), RS.getInt("pod"),
						RS.getInt("precio"), RS.getInt("set"), RS.getString("condicion"), RS.getString("infosArma"),
						RS.getInt("vendidos"), RS.getInt("precioMedio"), RS.getInt("puntosVIP"),RS.getInt("mimos")));
			}
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.exit(1);
		} finally {
			closeResultSet(RS);
		}
	}

	private static String convertHechizo(String str) {
		ArrayList<String> stat = CentroInfo.convertirStringArray(str.replace("null", "-1"));
		String efectosNormales = stat.get(0).replace("],[", "COMA").replace("], [", "COMA").replace("] ,[", "COMA")
				.replace("[", "").replace("]", "").replace(",", ";").replace("COMA", "|").replace(" ", "");
		String efectosCriticos = stat.get(1).replace("],[", "COMA").replace("], [", "COMA").replace("] ,[", "COMA")
				.replace("[", "").replace("]", "").replace(",", ";").replace("COMA", "|").replace(" ", "");
		byte costePA = Byte.parseByte(stat.get(2));
		byte alcMin = Byte.parseByte(stat.get(3));
		byte alcMax = Byte.parseByte(stat.get(4));
		short golpesCriticos = Short.parseShort(stat.get(5));
		short fallosCriticos = Short.parseShort(stat.get(6));
		boolean lineaRecta = stat.get(7).equalsIgnoreCase("true");
		boolean lineaVista = stat.get(8).equalsIgnoreCase("true");
		boolean celdaVacia = stat.get(9).equalsIgnoreCase("true");
		boolean alcanceModificable = stat.get(10).equalsIgnoreCase("true");
		byte tipoHechizo = Byte.parseByte(stat.get(11));
		byte maxPorTurno = Byte.parseByte(stat.get(12));
		byte maxPorObjetivo = Byte.parseByte(stat.get(13));
		byte sigLanzamiento = Byte.parseByte(stat.get(14));
		String areaAfectados = stat.get(15);
		String estadosNecesarios = stat.get(16).replace("[]", "-1").replace("[", "").replace("]", "").replace(",", ";")
				.replace(" ", "");
		String estadosProhibidos = stat.get(17).replace("[]", "-1").replace("[", "").replace("]", "").replace(",", ";")
				.replace(" ", "");
		int nivelReq = Integer.parseInt(stat.get(18));
		boolean finTurnoSiFC = stat.get(19).equalsIgnoreCase("true");
		return efectosNormales + "," + efectosCriticos + "," + costePA + "," + alcMin + "," + alcMax + ","
				+ golpesCriticos + "," + fallosCriticos + "," + lineaRecta + "," + lineaVista + "," + celdaVacia + ","
				+ alcanceModificable + "," + tipoHechizo + "," + maxPorTurno + "," + maxPorObjetivo + ","
				+ sigLanzamiento + "," + areaAfectados + "," + estadosNecesarios + "," + estadosProhibidos + ","
				+ nivelReq + "," + finTurnoSiFC;
	}

	private static StatsHechizos analizarHechizoStats(int id, int lvl, String str) {
		try {
			if (str.contains("[[[") && str.contains("]")) {
				str = convertHechizo(str).replace("\"", "");
			}
			StatsHechizos stats = null;
			String[] stat = str.split(",");
			String effets = stat[0];
			String CCeffets = stat[1];
			int PACOST = 6;
			try {
				PACOST = Integer.parseInt(stat[2].trim());
			} catch (NumberFormatException e) {
			}

			int POm = Integer.parseInt(stat[3].trim());
			int POM = Integer.parseInt(stat[4].trim());
			int TCC = Integer.parseInt(stat[5].trim());
			int TEC = Integer.parseInt(stat[6].trim());
			boolean line = stat[7].trim().equalsIgnoreCase("true");
			boolean LDV = stat[8].trim().equalsIgnoreCase("true");
			boolean emptyCell = stat[9].trim().equalsIgnoreCase("true");
			boolean MODPO = stat[10].trim().equalsIgnoreCase("true");
			// int unk = Integer.parseInt(stat[11]);//All 0
			int MaxByTurn = Integer.parseInt(stat[12].trim());
			int MaxByTarget = Integer.parseInt(stat[13].trim());
			int CoolDown = Integer.parseInt(stat[14].trim());
			String type = stat[15].trim();
			int level = Integer.parseInt(stat[stat.length - 2].trim());
			boolean endTurn = stat[19].trim().equalsIgnoreCase("true");
			stats = new StatsHechizos(id, lvl, PACOST, POm, POM, TCC, TEC, line, LDV, emptyCell, MODPO, MaxByTurn,
					MaxByTarget, CoolDown, level, endTurn, effets, CCeffets, type);
			return stats;
		} catch (Exception e) {
			Emu.creaLogs(e);
			int numero = 0;
			System.out.println("[DEBUG]Hechizo " + id + " nivel " + lvl);
			for (String z : str.split(",")) {
				System.out.println("[DEBUG]" + numero + " " + z);
				numero++;
			}
			System.exit(1);
			return null;
		}
	}

	/*
	 * private static StatsHechizos analizarHechizoStats(int hechizoid, int id, int
	 * nivel, String str) { try { /*if (str.contains("[[[") && str.contains("]")) {
	 * str = convertHechizo(str); if (nivel == 1) { UPDATE_NIVEL1(hechizoid, str); }
	 * else if (nivel == 2) { UPDATE_NIVEL2(hechizoid, str); } else if (nivel == 3)
	 * { UPDATE_NIVEL3(hechizoid, str); } else if (nivel == 4) {
	 * UPDATE_NIVEL4(hechizoid, str); } else if (nivel == 5) {
	 * UPDATE_NIVEL5(hechizoid, str); } else if (nivel == 6) {
	 * UPDATE_NIVEL6(hechizoid, str); } } StatsHechizos stats = null; String[] stat
	 * = str.split(","); String efectos = stat[0]; String efectosCriticos = ""; try
	 * { efectosCriticos = stat[1]; } catch (ArrayIndexOutOfBoundsException e) {
	 * return null; } int costePA = 6; try { costePA =
	 * Integer.parseInt(stat[2].trim()); } catch (NumberFormatException e) {} int
	 * alcMin = Integer.parseInt(stat[3].trim()); int alcMax =
	 * Integer.parseInt(stat[4].trim()); int afectados =
	 * Integer.parseInt(stat[5].trim()); int afectadosCriticos =
	 * Integer.parseInt(stat[6].trim()); boolean linea =
	 * stat[7].trim().equalsIgnoreCase("true"); boolean linedaVuelo =
	 * stat[8].trim().equalsIgnoreCase("true"); boolean celdaVacia =
	 * stat[9].trim().equalsIgnoreCase("true"); boolean alcMod =
	 * stat[10].trim().equalsIgnoreCase("true"); byte tipoHechizo =
	 * Byte.parseByte(stat[11].trim()); // 11 tipo de hechizos int maxPorTurno =
	 * Integer.parseInt(stat[12].trim()); int maxPorObjetivo =
	 * Integer.parseInt(stat[13].trim()); int CoolDown =
	 * Integer.parseInt(stat[14].trim()); String tipoAfectados =
	 * stat[15].trim().replace("|", ":").replace(",", ";"); String estadosNecesarios
	 * = stat[16].trim(); String estadosProhibidos = stat[17].trim(); int nivelMin =
	 * Integer.parseInt(stat[18].trim()); boolean finTurnoSiFC =
	 * stat[19].trim().equalsIgnoreCase("true"); /*stats = new StatsHechizos(id,
	 * nivel, costePA, alcMin, alcMax, afectados, afectadosCriticos, linea,
	 * linedaVuelo, celdaVacia, alcMod, maxPorTurno, maxPorObjetivo, CoolDown,
	 * nivelMin, finTurnoSiFC, efectos, efectosCriticos, tipoAfectados,
	 * estadosProhibidos, estadosNecesarios, MundoDofus.getHechizo(id),
	 * tipoHechizo); return stats; } catch (Exception e) { Emu.creaLogs(e); int
	 * numero = 0; System.out.println("[DEBUG]Hechizo " + id + " nivel " + nivel);
	 * for (String z : str.split(",")) { System.out.println("[DEBUG]" + numero + " "
	 * + z); numero++; } System.exit(1); return null; } }
	 */
	public static void ACTUALIZAR_NPC_COLOR_SEXO(NPCModelo npc) {
		String consultaSQL = "UPDATE npcs_modelo SET `gfxID` = ?, `sexo` = ?, `color1` = ?, `color2` = ?, `color3` = ?, `accesorios` = ? WHERE `id` = ?;";
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion(consultaSQL, bdDofusOnline);
			p.setInt(1, npc.getGfxID());
			p.setInt(2, npc.getSexo());
			p.setInt(3, npc.getColor1());
			p.setInt(4, npc.getColor2());
			p.setInt(5, npc.getColor3());
			p.setString(6, npc.getAccesorios());
			p.setInt(7, npc.getID());
			p.executeUpdate();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
			Emu.creaLogs(e);
		} finally {
			closePreparedStatement(p);
		}
	}

	static void ACTUALIZAR_NPC_KAMAS(NPCModelo npc) {
		String consultaSQL = "UPDATE npcs_modelo SET `kamas` = ? WHERE `id` = ?;";
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion(consultaSQL, bdDofusOnline);
			p.setLong(1, npc.getKamas());
			p.setInt(2, npc.getID());
			p.executeUpdate();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
			Emu.creaLogs(e);
		} finally {
			closePreparedStatement(p);
		}
	}

	static void SALVAR_MAZMORRAS(String str) {
		String consultaSQL = "UPDATE datos SET `dato` = ? WHERE `id` = ?;";
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion(consultaSQL, bdDofusOnline);
			p.setString(1, str);
			p.setInt(2, 4);
			p.executeUpdate();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
			Emu.creaLogs(e);
		} finally {
			closePreparedStatement(p);
		}
	}

	static void ACTUALIZAR_MAPA_DIN(String str) {
		String consultaSQL = "UPDATE datos SET `dato` = ? WHERE `id` = ?;";
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion(consultaSQL, bdDofusOnline);
			p.setString(1, str);
			p.setInt(2, 1);
			p.executeUpdate();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
			Emu.creaLogs(e);
		} finally {
			closePreparedStatement(p);
		}
	}

	public static void ACTUALIZAR_NPC_VENTAS(NPCModelo npc) {
		String consultaSQL = "UPDATE npcs_modelo SET `ventas` = ? WHERE `id` = ?;";
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion(consultaSQL, bdDofusOnline);
			p.setString(1, npc.actualizarStringBD());
			p.setInt(2, npc.getID());
			p.executeUpdate();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
			Emu.creaLogs(e);
		} finally {
			closePreparedStatement(p);
		}
	}

	public static void ACTUALIZAR_STATS_OBJETO(Objeto objeto, String stats) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("REPLACE INTO `objetos` VALUES (?,?,?,?,?,?);", bdDofusOnline);
			p.setInt(1, objeto.getID());
			p.setInt(2, objeto.getIDModelo());
			p.setInt(3, objeto.getCantidad());
			p.setInt(4, objeto.getPosicion());
			p.setString(5, stats);
			p.setInt(6, objeto.getObjeviID());
			p.execute();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
		} finally {
			closePreparedStatement(p);
		}
	}

	static void AGREGAR_NUEVO_OBJETO(Objeto objeto) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("REPLACE INTO `objetos` VALUES(?,?,?,?,?,?);", bdDofusOnline);
			p.setInt(1, objeto.getID());
			p.setInt(2, objeto.getIDModelo());
			p.setInt(3, objeto.getCantidad());
			p.setInt(4, objeto.getPosicion());
			p.setString(5, objeto.convertirStatsAString(0));
			p.setInt(6, objeto.getObjeviID());
			p.execute();
		} catch (SQLException e) {
			Emu.creaLogs(e);
		} finally {
			closePreparedStatement(p);
		}
	}

	static boolean SALVAR_NUEVO_GRUPOMOB(int mapaID, int celdaID, String grupoData) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("REPLACE INTO `mobs_fix` VALUES(?,?,?)", bdDofusOnline);
			p.setInt(1, mapaID);
			p.setInt(2, celdaID);
			p.setString(3, grupoData);
			p.execute();
			return true;
		} catch (SQLException e) {
			Emu.creaLogs(e);
		} finally {
			closePreparedStatement(p);
		}
		return false;
	}

	static boolean INSERT_NUEVO_GRUPOMOB(int mapaID, int celdaID, String grupoData) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("INSERT INTO mobs_fix(`mapa`,`celda`,`mobs`) VALUES(?,?,?);", bdDofusOnline);
			p.setInt(1, mapaID);
			p.setInt(2, celdaID);
			p.setString(3, grupoData);
			p.execute();
			return true;
		} catch (SQLException e) {
			Emu.creaLogs(e);
		} finally {
			closePreparedStatement(p);
		}
		return false;
	}

	static boolean INSERT_REGALO(String item, String tiempo, int maxcuenta) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("INSERT INTO regalos(`itemcant`,`tiempo`, `maxcuenta`) VALUES(?,?,?);", bdDofusOnline);
			p.setString(1, item);
			p.setString(2, tiempo);
			p.setInt(3, maxcuenta);
			p.execute();
			return true;
		} catch (SQLException e) {
			Emu.creaLogs(e);
		} finally {
			closePreparedStatement(p);
		}
		return false;
	}

	static void CARGAR_PREGUNTAS() {
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT * FROM npc_preguntas;", Emu.BDDOFUSONLINE);
			while (RS.next()) {
				MundoDofus.addNPCPregunta(new PreguntaNPC(RS.getInt("id"), RS.getString("respuestas"),
						RS.getString("params"), RS.getString("condicion"), RS.getInt("ifFalse")));
			}
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.exit(1);
		} finally {
			closeResultSet(RS);
		}
	}

	static void CARGAR_RESPUESTAS() {
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT * FROM npc_respuestas;", Emu.BDDOFUSONLINE);
			while (RS.next()) {
				int id = RS.getInt("id");
				int tipo = RS.getInt("accion");
				String args = RS.getString("args");
				if (MundoDofus.getNPCreponse(id) == null) {
					MundoDofus.addNPCreponse(new RespuestaNPC(id));
				}
				MundoDofus.getNPCreponse(id).addAccion(new Accion(tipo, args, ""));
			}
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.exit(1);
		} finally {
			closeResultSet(RS);
		}
	}

	static int CARGAR_ACCIONES_USO_OBJETOS() {
		int numero = 0;
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT * FROM objetos_accion;", Emu.BDDOFUSONLINE);
			while (RS.next()) {
				int id = RS.getInt("objetoModelo");
				if (MundoDofus.getObjModelo(id) == null) {
					continue;
				}
				int tipo = RS.getInt("accion");
				String args = RS.getString("args");
				MundoDofus.getObjModelo(id).addAccion(new Accion(tipo, args, ""));
				numero++;
			}
			return numero;
		} catch (SQLException e) {
			System.out.println("OBJETO ACCION ERROR SQL: " + e.getMessage());
		} finally {
			closeResultSet(RS);
		}
		return numero;
	}

	static void CARGAR_TUTORIALES() {
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT * FROM tutoriales;", Emu.BDDOFUSONLINE);
			while (RS.next()) {
				int id = RS.getInt("id");
				String inicio = RS.getString("inicio");
				String recompensa = RS.getString("recompensa1") + "," + RS.getString("recompensa2") + ","
						+ RS.getString("recompensa3") + "," + RS.getString("recompensa4");
				String fin = RS.getString("final");
				MundoDofus.addTutorial(new Tutorial(id, recompensa, inicio, fin));
			}
			return;
		} catch (SQLException e) {
			System.out.println("TUTORIAL ERROR SQL: " + e.getMessage());
		} finally {
			closeResultSet(RS);
		}
		return;
	}

	public static void CARGAR_OBJETOS(String ids) {
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT * FROM objetos WHERE id IN (" + ids + ");", Emu.BDDOFUSONLINE);
			while (RS.next()) {
				int id = RS.getInt("id");
				int modeloID = RS.getInt("modelo");
				int cantidad = RS.getInt("cantidad");
				int posicion = RS.getInt("posicion");
				String stats = RS.getString("stats");
				int objevivo = RS.getInt("objevivo");
				MundoDofus.addObjeto(MundoDofus.objetoIniciarServer(id, modeloID, cantidad, posicion, stats, objevivo),
						false);
			}
		} catch (SQLException e) {
			System.out.println("CARGANDO OBJETOS " + ids + " ERROR SQL: " + e.getMessage());
		} finally {
			closeResultSet(RS);
		}
	}

	static void BORRAR_OBJETO(int id) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("DELETE FROM objetos WHERE id = ?;", bdDofusOnline);
			p.setInt(1, id);
			p.execute();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
		} finally {
			closePreparedStatement(p);
		}
	}

	public static void SALVAR_OBJETO(Objeto objeto) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("REPLACE INTO `objetos` VALUES (?,?,?,?,?,?);", bdDofusOnline);
			p.setInt(1, objeto.getID());
			p.setInt(2, objeto.getIDModelo());
			p.setInt(3, objeto.getCantidad());
			p.setInt(4, objeto.getPosicion());
			p.setString(5, objeto.convertirStatsAString(0));
			p.setInt(6, objeto.getObjeviID());
			p.execute();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
		} finally {
			closePreparedStatement(p);
		}
	}

	public static void CREAR_MONTURA(Dragopavo DP) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion(
					"REPLACE INTO `dragopavos`(`id`,`color`,`sexo`,`nombre`,`xp`,`nivel`,`resistencia`,`amor`,`madurez`,`serenidad`,`reproducciones`,`fatiga`,`objetos`,"
							+ "`ancestros`,`energia`,`talla`,`celda`,`mapa`,`dueno`,`orientacion`,`fecundable`,`pareja`,`vip`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);",
					bdDofusOnline);
			p.setInt(1, DP.getID());
			p.setInt(2, DP.getColor());
			p.setInt(3, DP.getSexo());
			p.setString(4, DP.getNombre());
			p.setLong(5, DP.getExp());
			p.setInt(6, DP.getNivel());
			p.setInt(7, DP.getResistencia());
			p.setInt(8, DP.getAmor());
			p.setInt(9, DP.getMadurez());
			p.setInt(10, DP.getSerenidad());
			p.setInt(11, DP.getReprod());
			p.setInt(12, DP.getFatiga());
			p.setString(13, DP.stringObjetosBD());
			p.setString(14, DP.getAncestros());
			p.setInt(15, DP.getEnergia());
			p.setInt(16, DP.getTalla());
			p.setInt(17, DP.getCelda());
			p.setInt(18, DP.getMapa());
			p.setInt(19, DP.getDueÑo());
			p.setInt(20, DP.getOrientacion());
			p.setInt(21, DP.getFecundadaHace());
			p.setInt(22, DP.getPareja());
			p.setString(23, DP.getVIP());
			p.execute();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
		} finally {
			closePreparedStatement(p);
		}
	}

	static String CARGAR_REGALOS(int id) {
		ResultSet RS = null;
		String tieneReg = "";
		try {
			RS = executeQuery("SELECT tieneRegalos from cuentas WHERE `id` = '" + id + "';", Emu.BDDOFUSONLINE);
			if (RS.next()) {
				tieneReg = RS.getString("tieneRegalos");
			}
			return tieneReg;
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closeResultSet(RS);
		}
		return tieneReg;
	}

	public static String CARGAR_REGALO(Cuenta cuenta) {
		ResultSet RS = null;
		String tieneReg = "";
		try {
			RS = executeQuery("SELECT regalo from cuentas WHERE `id` = '" + cuenta.getID() + "';", Emu.BDDOFUSONLINE);
			if (RS.next()) {
				cuenta.setRegalo(RS.getInt("regalo"));
			}
			return tieneReg;
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closeResultSet(RS);
		}
		return tieneReg;
	}

	public static void CARGAR_CUENTA_POR_ID(int id) {
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT * from cuentas WHERE `id` = '" + id + "';", Emu.BDDOFUSONLINE);
			if (RS.next()) {
				Cuenta cuenta = new Cuenta(RS.getInt("id"), RS.getString("cuenta").toLowerCase(), RS.getString("pass"),
						RS.getString("apodo"), RS.getString("pregunta"), RS.getString("respuesta"), RS.getInt("gm"),
						RS.getInt("vip"), (RS.getInt("baneado") == 1), RS.getString("ultimaIP"),
						RS.getString("ultimaConexion"), RS.getString("objetos"), RS.getInt("kamas"),
						RS.getString("amigos"), RS.getString("enemigos"), RS.getString("establo"),
						RS.getInt("primeravez"), RS.getInt("regalo"), RS.getString("tiempoLoot"), RS.getInt("logeado"),
						RS.getString("tieneRegalos"), RS.getInt("generadores"), RS.getInt("tiempogen"),
						RS.getString("tutoriales"));
				MundoDofus.addCuenta(cuenta);
				cargarPj(cuenta);
			}
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closeResultSet(RS);
		}
	}

	public static void ACTUALIZAR_REGALO(Cuenta cuenta) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("UPDATE cuentas SET `regalo` = 0 WHERE `id` = ?;", bdDofusOnline);
			p.setInt(1, cuenta.getID());
			p.executeUpdate();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closePreparedStatement(p);
		}
	}

	public static void ACTUALIZAR_VIP(Cuenta cuenta) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("UPDATE cuentas SET `vip` = 1 WHERE `id` = ?;", bdDofusOnline);
			p.setInt(1, cuenta.getID());
			p.executeUpdate();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closePreparedStatement(p);
		}
	}

	public static void ACTUALIZAR_NUEVA_MISION(int id, String objMisiones, String misiones) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("UPDATE personajes SET `misiones` = ?,`objMisiones` = ? WHERE `id` = ?;",
					bdDofusOnline);
			p.setString(1, misiones);
			p.setString(2, objMisiones);
			p.setInt(3, id);
			p.executeUpdate();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closePreparedStatement(p);
		}
	}

	public static void ACTUALIZAR_MONTURA(Dragopavo DP, boolean salvarObjetos) {
		String consultaSQL = "UPDATE dragopavos SET `nombre` = ?,`xp` = ?,`nivel` = ?,`resistencia` = ?,`amor` = ?,"
				+ "`madurez` = ?,`serenidad` = ?,`reproducciones` = ?,`fatiga` = ?,`energia` = ?,`ancestros` = ?,"
				+ "`objetos` = ?,`habilidad` = ?, `talla`=?,`celda`=?,`mapa`=?,`dueno`=?,`orientacion`= ?,"
				+ " `fecundable`=?, `pareja`=?, `vip`=? WHERE `id` = ?;";
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion(consultaSQL, bdDofusOnline);
			p.setString(1, DP.getNombre());
			p.setLong(2, DP.getExp());
			p.setInt(3, DP.getNivel());
			p.setInt(4, DP.getResistencia());
			p.setInt(5, DP.getAmor());
			p.setInt(6, DP.getMadurez());
			p.setInt(7, DP.getSerenidad());
			p.setInt(8, DP.getReprod());
			p.setInt(9, DP.getFatiga());
			p.setInt(10, DP.getEnergia());
			p.setString(11, DP.getAncestros());
			p.setString(12, DP.stringObjetosBD());
			p.setString(13, DP.getHabilidad());
			p.setInt(14, DP.getTalla());
			p.setInt(15, DP.getCelda());
			p.setInt(16, DP.getMapa());
			p.setInt(17, DP.getDueÑo());
			p.setInt(18, DP.getOrientacion());
			p.setInt(19, DP.getFecundadaHace());
			p.setInt(20, DP.getPareja());
			p.setString(21, DP.getVIP());
			p.setInt(22, DP.getID());
			p.execute();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
			Emu.creaLogs(e);
		} finally {
			closePreparedStatement(p);
		}
		if (salvarObjetos) {
			consultaSQL = "REPLACE INTO `objetos` VALUES (?,?,?,?,?,?);";
			try {
				p = nuevaTransaccion(consultaSQL, bdDofusOnline);
				for (Objeto obj : DP.getObjetos()) {
					try {
						if (obj == null) {
							continue;
						}
						p.setInt(1, obj.getID());
						p.setInt(2, obj.getIDModelo());
						p.setInt(3, obj.getCantidad());
						p.setInt(4, obj.getPosicion());
						p.setString(5, obj.convertirStatsAString(0));
						p.setInt(6, obj.getObjeviID());
						p.execute();
					} catch (Exception e) {
						continue;
					}
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			} finally {
				closePreparedStatement(p);
			}
		}
	}

	static int GET_BORRANDO_CERCADO(Personaje perso) {
		int nbr = 0;
		PreparedStatement p = null;
		ResultSet RS = null;
		StringBuilder ids;
		try {
			RS = executeQuery("SELECT `id` from `dragopavos` WHERE `dueno` = '" + perso.getID() + "';",
					Emu.BDDOFUSONLINE);
			ids = new StringBuilder();
			while (RS.next()) {
				nbr = RS.getInt("id");
				if (ids.toString().isEmpty()) {
					ids.append(nbr);
				} else {
					ids.append("," + nbr);
				}
			}
			if (!ids.toString().isEmpty()) {
				for (String idx : ids.toString().split(",")) {
					if (idx.equals("")) {
						continue;
					}
					for (Cercado cercado : MundoDofus.Cercados.values()) {
						if (cercado.getCriando().isEmpty()) {
							continue;
						}
						for (String pavo : cercado.getCriando().split(";")) {
							if (pavo.equals("")) {
								continue;
							}
							if (pavo == idx) {
								cercado.delCriando(Integer.parseInt(pavo));
								Dragopavo DD1 = MundoDofus.getDragopavoPorID(Integer.parseInt(pavo));
								if (DD1 == null) {
									continue;
								}
								if (perso.getCuenta().getEstablo().contains(DD1)) {
									perso.getCuenta().getEstablo().remove(DD1);
								}
								MundoDofus.borrarDragopavoID(DD1.getID());
							}
						}
					}
				}
				String consultaSQL = "DELETE FROM `dragopavos` WHERE `id` IN (" + ids.toString() + ");";
				p = nuevaTransaccion(consultaSQL, bdDofusOnline);
				p.executeUpdate();
			}
		} catch (SQLException e) {
			System.out.println("SQL ERROR: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closeResultSet(RS);
			closePreparedStatement(p);
		}
		return nbr;
	}

	static void BORRAR_MIEMBROS(int guild, String name) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("DELETE FROM `miembros_gremio` WHERE `gremio`=? AND `nombre`=?;", bdDofusOnline);
			p.setInt(1, guild);
			p.setString(2, name);
			p.execute();
		} catch (SQLException e) {
			System.out.println("Game: SQL ERROR: " + e.getMessage());
		} finally {
			closePreparedStatement(p);
		}
	}

	static String getNombres(int guild) {
		StringBuilder names = new StringBuilder("");
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT `nombre` FROM `miembros_gremio` WHERE `gremio` = " + guild + ";",
					Emu.BDDOFUSONLINE);
			while (RS.next()) {
				if (names.toString().isEmpty()) {
					names.append(RS.getString("nombre"));
				} else {
					names.append("|" + RS.getString("nombre"));
				}
			}
		} catch (Exception e) {
			System.out.println("HUBO UN ERROR " + e.getMessage());
		} finally {
			closeResultSet(RS);
		}
		return names.toString();
	}

	public static int getMapa(int x, int y) {
		ResultSet RS = null;
		int idmapa = 0;
		try {
			RS = executeQuery("SELECT `id` FROM `mapas` WHERE `X` = " + x + " AND `Y` = " + y + ";", Emu.BDDOFUSONLINE);
			if (RS.next()) {
				idmapa = RS.getInt("id");
			}
		} catch (Exception e) {
			System.out.println("HUBO UN ERROR " + e.getMessage());
		} finally {
			closeResultSet(RS);
		}
		return idmapa;
	}

	static void DELETE_OBJETOS_LISTA(String str) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("DELETE FROM `objetos` WHERE `id` IN (" + str + ");", bdDofusOnline);
			p.executeUpdate();
		} catch (Exception e) {
			Emu.creaLogs(e);
		} finally {
			closePreparedStatement(p);
		}
	}

	public static void SALVAR_CERCADO(Cercado cercado) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion(
					"UPDATE `cercados` SET `celda`=?, `propietario` =?, `gremio`=?, `precio`=? , `criando`=?, `objetoscolocados`=? WHERE `mapa`=?;",
					bdDofusOnline);
			p.setInt(1, cercado.getCeldaID());
			p.setInt(2, cercado.getDueÑo());
			p.setInt(3, (cercado.getGremio() == null ? -1 : cercado.getGremio().getID()));
			p.setInt(4, cercado.getPrecio());
			p.setString(5, cercado.getCriando());
			p.setString(6, cercado.getStringObjetosCria());
			p.setInt(7, cercado.getMapa().getID());
			p.execute();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
		} finally {
			closePreparedStatement(p);
		}
	}

	static void SALVAR_RANKINGPVP(RankingPVP rank) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion(
					"UPDATE `ranking_pvp` SET `victorias`=?, `derrotas` =?, `nivelAlineacion`=?, `nombre`=?  WHERE `id`=?;",
					bdDofusOnline);
			p.setInt(1, rank.getVictorias());
			p.setInt(2, rank.getDerrotas());
			p.setInt(3, rank.getNivelAlin());
			p.setString(4, rank.getNombre());
			p.setInt(5, rank.getID());
			p.execute();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
		} finally {
			closePreparedStatement(p);
		}
	}

	static void ACTUALIZAR_MONTURAS_Y_OBJETOS(int monturas, int objetos, int mapa) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("UPDATE `cercados` SET `tamano`=?, `objetos` =? WHERE `mapa`=?;", bdDofusOnline);
			p.setInt(1, monturas);
			p.setInt(2, objetos);
			p.setInt(3, mapa);
			p.execute();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
		} finally {
			closePreparedStatement(p);
		}
	}

	static boolean BORRAR_RANKINGPVP(int id) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("DELETE FROM `ranking_pvp` WHERE `id` = ? ;", bdDofusOnline);
			p.setInt(1, id);
			p.execute();
			return true;
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
		} finally {
			closePreparedStatement(p);
		}
		return false;
	}

	public static void AGREGAR_RANKINGPVP(RankingPVP rank) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion(
					"INSERT INTO ranking_pvp(`id`,`nombre`,`victorias`,`derrotas`,`nivelAlineacion`) VALUES(?,?,0,0,?);",
					bdDofusOnline);
			p.setInt(1, rank.getID());
			p.setString(2, rank.getNombre());
			p.setInt(3, rank.getNivelAlin());
			p.execute();
		} catch (SQLException e) {
			Emu.creaLogs(e);
		} finally {
			closePreparedStatement(p);
		}
	}

	public static boolean ACTUALIZAR_MAPA_POSPELEA_NROGRUPO(Mapa mapa) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("UPDATE `mapas` SET `posPelea` = ?, `nroGrupo` = ? WHERE id = ?;", bdDofusOnline);
			p.setString(1, mapa.getLugaresString());
			p.setInt(2, mapa.getMaxGrupoDeMobs());
			p.setInt(3, mapa.getID());
			p.executeUpdate();
			return true;
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
		} finally {
			closePreparedStatement(p);
		}
		return false;
	}

	static boolean BORRAR_NPC_DEL_MAPA(int m, int c) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("DELETE FROM npcs_ubicacion WHERE mapa = ? AND celda = ?;", bdDofusOnline);
			p.setInt(1, m);
			p.setInt(2, c);
			p.execute();
			return true;
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
		} finally {
			closePreparedStatement(p);
		}
		return false;
	}

	static void BORRAR_RECAUDADOR(int id) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("DELETE FROM recaudadores WHERE id = ?;", bdDofusOnline);
			p.setInt(1, id);
			p.execute();
			return;
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
		} finally {
			closePreparedStatement(p);
		}
		return;
	}

	static boolean INSERT_CELDASTP(int mapa, int celda, int mapa2, int celda2) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("INSERT INTO `celdas_teleport` VALUES (?,?,?,?,?,?);", bdDofusOnline);
			p.setInt(1, mapa);
			p.setInt(2, celda);
			p.setInt(3, 0);
			p.setString(4, mapa2 + "," + celda2);
			p.setString(5, "");
			p.setInt(6, 1);
			p.execute();
			return true;
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
		} finally {
			closePreparedStatement(p);
		}
		return false;
	}

	static boolean AGREGAR_NPC_AL_MAPA(int mapa, int id, int celda, int direccion, String nombre) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("INSERT INTO `npcs_ubicacion` VALUES (?,?,?,?,?);", bdDofusOnline);
			p.setInt(1, mapa);
			p.setInt(2, id);
			p.setInt(3, celda);
			p.setInt(4, direccion);
			p.setString(5, nombre);
			p.execute();
			return true;
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
		} finally {
			closePreparedStatement(p);
		}
		return false;
	}

	public static boolean ADD_RECAUDADOR_EN_MAPA(int id, int mapa, int guildID, int celda, int o, String N1,
			String N2) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("INSERT INTO `recaudadores`" + " VALUES (?,?,?,?,?,?,?,?,?,?);", bdDofusOnline);
			p.setInt(1, id);
			p.setInt(2, mapa);
			p.setInt(3, celda);
			p.setInt(4, o);
			p.setInt(5, guildID);
			p.setString(6, N1);
			p.setString(7, N2);
			p.setString(8, "");
			p.setLong(9, 0);
			p.setLong(10, 0);
			p.execute();
			return true;
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
		} finally {
			closePreparedStatement(p);
		}
		return false;
	}

	static void ACTUALIZAR_RECAUDADOR(Recaudador P) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion(
					"UPDATE `recaudadores` SET `objetos` = ?,`kamas` = ?,`xp` = ?,`orientacion` = ?, `celda`=? WHERE id = ?;",
					bdDofusOnline);
			p.setString(1, P.stringListaObjetosBD());
			p.setLong(2, P.getKamas());
			p.setLong(3, P.getXp());
			p.setInt(4, P.getOrientacion());
			p.setInt(5, P.getCeldalID());
			p.setInt(6, P.getID());
			p.execute();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
		} finally {
			closePreparedStatement(p);
		}
	}

	static void SALVAR_NUEVO_GREMIO(Gremio g) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion(
					"INSERT INTO `gremios` (`id`,`nombre`,`emblema`,`hechizos`,`stats`,`tiempo`) VALUES(?,?,?,?,?,?);",
					bdDofusOnline);
			p.setInt(1, g.getID());
			p.setString(2, g.getNombre());
			p.setString(3, g.getEmblema());
			p.setString(4, "462;0|461;0|460;0|459;0|458;0|457;0|456;0|455;0|454;0|453;0|452;0|451;0|");
			p.setString(5, "176;100|158;1000|124;100|");
			DateFormat formatTime = new SimpleDateFormat("yyyy~MM~dd~HH~mm");
			Calendar calendar = Calendar.getInstance();
			p.setString(6, formatTime.format(calendar.getTime()));
			p.execute();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
		} finally {
			closePreparedStatement(p);
		}
	}

	static void BORRAR_GREMIO(int id) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("DELETE FROM `gremios` WHERE `id` = ?;", bdDofusOnline);
			p.setInt(1, id);
			p.execute();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
		} finally {
			closePreparedStatement(p);
		}
	}

	public static void BORRAR_MIEMBRO_GREMIO(int id) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("DELETE FROM `miembros_gremio` WHERE `id` = ?;", bdDofusOnline);
			p.setInt(1, id);
			p.execute();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
		} finally {
			closePreparedStatement(p);
		}
	}

	public static void ACTUALIZAR_ALINEA(int align, int id) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("UPDATE `gremios` SET `alineacion` = ? WHERE id = ?;", bdDofusOnline);
			p.setInt(1, align);
			p.setLong(2, id);
			p.execute();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
		} finally {
			closePreparedStatement(p);
		}
	}

	public static void ACTUALIZAR_GREMIO(Gremio g) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion(
					"UPDATE `gremios` SET `nivel` = ?,`xp` = ?,`capital` = ?,`recaudadores` = ?,`hechizos` = ?,"
							+ "`stats` = ?,`tiempo` = ?,`puntos` = ? WHERE id = ?;",
					bdDofusOnline);
			p.setInt(1, g.getNivel());
			p.setLong(2, g.getXP());
			p.setInt(3, g.getCapital());
			p.setInt(4, g.getNroRecau());
			p.setString(5, g.compilarHechizo());
			p.setString(6, g.compilarStats());
			p.setString(7, g.ultimaConex);
			p.setInt(8, g.puntosextra);
			p.setInt(9, g.getID());
			p.execute();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
		} finally {
			closePreparedStatement(p);
		}
	}

	public static void ACTUALIZAR_MIEMBRO_GREMIO(MiembroGremio gm) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("REPLACE INTO `miembros_gremio` VALUES(?,?,?,?,?,?,?,?,?,?);", bdDofusOnline);
			p.setInt(1, gm.getID());
			p.setInt(2, gm.getGremio().getID());
			p.setString(3, gm.getVerdaderoNombre());
			p.setInt(4, gm.getNivel());
			p.setInt(5, gm.getGfx());
			p.setInt(6, gm.getRango());
			p.setLong(7, gm.getXpDonada());
			p.setInt(8, gm.getPorcXpDonada());
			p.setInt(9, gm.getDerechos());
			p.setString(10, gm.getUltimaConeccino());
			p.execute();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
		} finally {
			closePreparedStatement(p);
		}
	}

	private static int esPJenGremio(int id) {
		int guildId = -1;
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT gremio FROM `miembros_gremio` WHERE id=" + id + ";", Emu.BDDOFUSONLINE);
			boolean found = RS.first();
			if (found) {
				guildId = RS.getInt("gremio");
			}
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closeResultSet(RS);
		}
		return guildId;
	}

	static void CARGAR_RANKINGPVP() {
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT * FROM ranking_pvp;", Emu.BDDOFUSONLINE);
			while (RS.next()) {
				int id = RS.getInt("id");
				String nombre = RS.getString("nombre");
				int victorias = RS.getInt("victorias");
				int derrotas = RS.getInt("derrotas");
				int nivelAlin = RS.getInt("nivelAlineacion");
				MundoDofus.addRankingPVP(new RankingPVP(id, nombre, victorias, derrotas, nivelAlin));
			}
		} catch (SQLException e) {
			System.out.println("RankingPVP ERROR SQL: " + e.getMessage());
		} finally {
			closeResultSet(RS);
		}
	}

	public static int GET_VOTADO(int idCuenta) {
		int puntos = 0;
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT `compartido` FROM `cuentas` WHERE `id` = '" + idCuenta + "';", Emu.BDDOFUSONLINE);
			boolean encontrado = RS.first();
			if (encontrado) {
				puntos = RS.getInt("compartido");
			}
		} catch (Exception e) {
			System.out.println("Error SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closeResultSet(RS);
		}
		return puntos;
	}

	public static boolean ES_REFERIDO(String apodo) {
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT * FROM `referidos` WHERE `cuenta` = '" + apodo + "';", Emu.BDDOFUSONLINE);
			if (RS.next()) {
				int premio = RS.getInt("tienepremio");
				if (premio == 1) {
					return false;
				} else {
					String referidopor = RS.getString("referidopor");
					int puntosrf = getPuntosApodo(referidopor);
					GestorSQL.setPuntoApodo(puntosrf + 300, referidopor);
					settounoRef(apodo);
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			System.out.println("Error SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closeResultSet(RS);
		}
		return false;
	}

	private static void settounoRef(String cuentaID) {
		PreparedStatement p = null;
		String consultaSQL = "UPDATE `referidos` SET `tienepremio`=? WHERE `cuenta`= ?";
		try {
			p = nuevaTransaccion(consultaSQL, bdDofusOnline);
			p.setInt(1, 1);
			p.setString(2, cuentaID);
			p.execute();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		} finally {
			closePreparedStatement(p);
		}
	}

	private static void setPuntoApodo(int puntos, String cuentaID) {
		PreparedStatement p = null;
		String consultaSQL = "UPDATE `cuentas` SET `puntos`=? WHERE `apodo`= ?";
		try {
			p = nuevaTransaccion(consultaSQL, bdDofusOnline);
			p.setLong(1, puntos);
			p.setString(2, cuentaID);
			p.execute();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		} finally {
			closePreparedStatement(p);
		}
	}

	private static int getPuntosApodo(String cuentaID) {
		int puntos = 0;
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT `puntos` FROM `cuentas` WHERE `apodo` = '" + cuentaID + "';", Emu.BDDOFUSONLINE);
			if (RS.next()) {
				puntos = RS.getInt("puntos");
			}
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closeResultSet(RS);
		}
		return puntos;
	}

	/*
	 * static void TIMER (boolean start) { if (start) { timerComienzo = new Timer();
	 * timerComienzo.schedule(new TimerTask() { public void run () { if
	 * (!necesitaComenzar) return; comenzarTransacciones(); necesitaComenzar =
	 * false; } }, Emu.BD_COMMIT, Emu.BD_COMMIT); } else timerComienzo.cancel(); }
	 */

	public static boolean personajeYaExiste(String nombre) {
		boolean exist = false;
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT COUNT(*) AS exist FROM personajes WHERE nombre LIKE '" + nombre + "';",
					Emu.BDDOFUSONLINE);
			boolean found = RS.first();
			if (found) {
				if (RS.getInt("exist") != 0) {
					exist = true;
				}
			}
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closeResultSet(RS);
		}
		return exist;
	}

	public static void COMPRAR_CASA(Personaje P, Casa h) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("UPDATE `casas` SET `precio`='0', `dueno`='" + P.getCuentaID()
					+ "', `gremio`='0', `acceso`='0', `clave`='-', `derechosGremio`='0' WHERE `id`='" + h.getID()
					+ "';", bdDofusOnline);
			p.execute();
			h.setPrecio(0);
			h.setDueÑoID(P.getCuentaID());
			h.setGremioID(0);
			h.setAcceso(0);
			h.setClave("-");
			h.setDerechosGremio(0);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
		} finally {
			closePreparedStatement(p);
		}
		ArrayList<Cofre> trunks = Cofre.getCofresPorCasa(h);
		for (Cofre trunk : trunks) {
			trunk.setDueÑoID(P.getCuentaID());
			trunk.setClave("-");
		}
		try {
			p = nuevaTransaccion("UPDATE `cofres` SET `dueno`=?, `clave`='-' WHERE `casa`=?;", bdDofusOnline);
			p.setInt(1, P.getCuentaID());
			p.setInt(2, h.getID());
			p.execute();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
		} finally {
			closePreparedStatement(p);
		}
	}

	static void ReiniciarCofre(int casaid, int dueno) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion(
					"UPDATE `cofres` SET `dueno`=?, `clave`='-', `kamas`='0', `objetos`='' WHERE `casa`=?;",
					bdDofusOnline);
			p.setInt(1, dueno);
			p.setInt(2, casaid);
			p.execute();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
		} finally {
			closePreparedStatement(p);
		}
	}

	public static void VENDER_CASA(Casa h, int precio) {
		h.setPrecio(precio);
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("UPDATE `casas` SET `precio`='" + precio + "' WHERE `id`='" + h.getID() + "';",
					bdDofusOnline);
			p.execute();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
		} finally {
			closePreparedStatement(p);
		}
	}

	public static void CODIGO_CASA(Personaje perso, Casa casa, String packet) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("UPDATE `casas` SET `clave`='" + packet + "' WHERE `id`='" + casa.getID()
					+ "' AND dueno='" + perso.getCuentaID() + "';", bdDofusOnline);
			p.execute();
			casa.setClave(packet);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
		} finally {
			closePreparedStatement(p);
		}
	}

	public static void ACTUALIZAR_CASA_GREMIO(Casa casa, int gremioID, int derechosGremio) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("UPDATE `casas` SET `gremio`='" + gremioID + "', `derechosGremio`='" + derechosGremio
					+ "' WHERE `id`='" + casa.getID() + "';", bdDofusOnline);
			p.execute();
			casa.setGremioID(gremioID);
			casa.setDerechosGremio(derechosGremio);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
		} finally {
			closePreparedStatement(p);
		}
	}

	public static void BORRAR_CASA_GREMIO(int gremioID) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion(
					"UPDATE `casas` SET `derechosGremio`='0', `gremio`='0' WHERE `gremio`='" + gremioID + "';",
					bdDofusOnline);
			p.execute();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
		} finally {
			closePreparedStatement(p);
		}
	}

	public static void ACTUALIZAR_CASA(Casa h) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion(
					"UPDATE `casas` SET `dueno` = ?,`precio` = ?,`gremio` = ?,`acceso` = ?,`clave` = ?,`derechosGremio` = ?,`impuesto` = ? WHERE id = ?;",
					bdDofusOnline);
			p.setInt(1, h.getDueÑoID());
			p.setInt(2, h.getPrecioVenta());
			p.setInt(3, h.getGremioID());
			p.setInt(4, h.getAcceso());
			p.setString(5, h.getClave());
			p.setInt(6, h.getDerechosGremio());
			p.setString(7, h._impuesto);
			p.setInt(8, h.getID());
			p.execute();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
		} finally {
			closePreparedStatement(p);
		}
	}

	static void borrarCercadosGremio(int gremio) {
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT `mapa` FROM cercados WHERE gremio='" + gremio + "';", Emu.BDDOFUSONLINE);
			while (RS.next()) {
				int mapa = (short) RS.getInt("mapa");
				Cercado cercado = MundoDofus.Cercados.get(mapa);
				MundoDofus.getCercadoPorMap(mapa).resetear();
				GestorSQL.SALVAR_CERCADO(cercado);
			}
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closeResultSet(RS);
		}
		return;
	}

	public static byte totalCercadosDelGremio(int getId) {
		byte i = 0;
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT * FROM cercados WHERE gremio='" + getId + "';", Emu.BDDOFUSONLINE);
			while (RS.next()) {
				i++;
			}
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closeResultSet(RS);
		}
		return i;
	}

	public static void test2() {
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT * FROM objetos WHERE `modelo` > 11080;", Emu.BDDOFUSONLINE);
			while (RS.next()) {
				int id = RS.getInt("id");
				int modelo = RS.getInt("modelo");
				ObjetoModelo obje = MundoDofus.getObjModelo(modelo);
				if (obje != null) {
					obje.crearObjDesdeModelo(1, true);
					UPDATE_ITEM(id, obje.getStringStatsObj());
					System.out.println("Se actualiz� el objeto " + id + " modelo " + modelo);
				}
			}
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closeResultSet(RS);
		}
		return;
	}

	public static void test3() {
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT * FROM objetos;", Emu.BDDOFUSONLINE);
			// RS = executeQuery("SELECT * FROM objetos WHERE `modelo` >= 11091;",
			// Emu.BDDOFUSONLINE);
			while (RS.next()) {
				String stats = RS.getString("stats");
				if (stats.contains("(") || stats.contains("/")) {
					boolean perfec = false;
					if (stats.contains("Perfeccionado")) {
						perfec = true;
					}
					int id = RS.getInt("id");
					int modelo = RS.getInt("modelo");
					ObjetoModelo obje = MundoDofus.getObjModelo(modelo);
					if (obje != null) {
						boolean suma = false;
						switch (obje.getTipo()) {
						case 1:
						case 2:
						case 3:
						case 4:
						case 5:
						case 6:
						case 7:
						case 8:
						case 9:
						case 10:
						case 11:
						case 16:
						case 17:
						case 18:
						case 19:
						case 23:
						case 81:
						case 82:
						case 113:
						case 114:
						case 121:
							suma = true;
							break;
						}
						if (suma) {
							String maxStats = ObjetoModelo.generarStatsModeloDB(obje.getStringStatsObj(), perfec);
							if (perfec) {
								maxStats = maxStats + ",3de#0#0#0#Objeto Perfeccionado";
							}
							UPDATE_ITEM(id, maxStats);
							// UPDATE_ITEM2(modelo, maxStats);
							System.out.println("Se actualiz� el objeto " + id + " modelo " + modelo);
						}
					}
				}
			}
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closeResultSet(RS);
		}
		return;
	}

	public static void test3(int item) {
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT * FROM objetos WHERE `modelo` = " + item + ";", Emu.BDDOFUSONLINE);
			// RS = executeQuery("SELECT * FROM objetos WHERE `modelo` >= 11091;",
			// Emu.BDDOFUSONLINE);
			while (RS.next()) {
				int id = RS.getInt("id");
				int modelo = RS.getInt("modelo");
				ObjetoModelo obje = MundoDofus.getObjModelo(modelo);
				if (obje != null) {
					boolean suma = false;
					switch (obje.getTipo()) {
					case 1:
					case 2:
					case 3:
					case 4:
					case 5:
					case 6:
					case 7:
					case 8:
					case 9:
					case 10:
					case 11:
					case 16:
					case 17:
					case 18:
					case 19:
					case 23:
					case 81:
					case 82:
					case 113:
					case 114:
					case 121:
						suma = true;
						break;
					}
					if (suma) {
						String maxStats = ObjetoModelo.generarStatsModeloDB(obje.getStringStatsObj(), false);
						// if (!maxStats.equals(""))
						// maxStats = maxStats+",3de#0#0#0#Objeto Perfeccionado";
						UPDATE_ITEM(id, maxStats);
						UPDATE_ITEM2(modelo, maxStats);
						System.out.println("Se actualiz� el objeto " + id + " modelo " + modelo);
					}
				}
			}
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closeResultSet(RS);
		}
		return;
	}

	private static void UPDATE_ITEM2(int id, String stats) {
		PreparedStatement p = null;
		String consultaSQL = "UPDATE `mascotas` SET `stats`=? WHERE `idModelo`= ?";
		try {
			p = nuevaTransaccion(consultaSQL, bdDofusOnline);
			p.setString(1, stats);
			p.setInt(2, id);
			p.execute();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		} finally {
			closePreparedStatement(p);
		}
	}

	private static void UPDATE_ITEM(int id, String stats) {
		PreparedStatement p = null;
		String consultaSQL = "UPDATE `objetos` SET `stats`=? WHERE `id`= ?";
		try {
			p = nuevaTransaccion(consultaSQL, bdDofusOnline);
			p.setString(1, stats);
			p.setInt(2, id);
			p.execute();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		} finally {
			closePreparedStatement(p);
		}
	}

	public static int getSigIDRecaudador() {
		int i = -100;
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT `id` FROM `recaudadores` ORDER BY `id` ASC LIMIT 0 , 1;", Emu.BDDOFUSONLINE);
			while (RS.next()) {
				i = RS.getInt("id") - 3;
			}
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closeResultSet(RS);
		}
		return i;
	}

	static int CARGAR_ZAAPS() {
		int i = 0;
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT mapa, celda FROM zaaps;", Emu.BDDOFUSONLINE);
			while (RS.next()) {
				CentroInfo.ZAAPS.put(RS.getInt("mapa"), RS.getInt("celda"));
				i++;
			}
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closeResultSet(RS);
		}
		return i;
	}

	static int getSigIDObjeto() {
		ResultSet RS = null;
		int intent = 0;
		try {
			RS = executeQuery("SELECT MAX(id) AS max FROM objetos;", Emu.BDDOFUSONLINE);
			int id = 1;
			boolean encontrado = RS.first();
			if (encontrado) {
				id = RS.getInt("max");
			}
			return id;
		} catch (SQLException e) {
			getSigIDObjeto();
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
			if (intent >= 3) {
				Emu.cerrarServer("GETSIGIDOBJETO");
			}
			intent += 1;
		} finally {
			closeResultSet(RS);
		}
		return 1;
	}

	static int CARGAR_BANIP() {
		int i = 0;
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT ip FROM banip;", Emu.BDDOFUSONLINE);
			while (RS.next()) {
				String ip = RS.getString("ip");
				if (!CentroInfo.BAN_IP.contains(ip)) {
					CentroInfo.BAN_IP.add(ip);
				}
				i++;
			}
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closeResultSet(RS);
		}
		return i;
	}

	public static boolean AGREGAR_BANIP(String ip) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("INSERT INTO `banip` (ip) VALUES (?);", bdDofusOnline);
			p.setString(1, ip);
			p.execute();
			return true;
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
		} finally {
			closePreparedStatement(p);
		}
		return false;
	}

	static boolean BORRAR_BANIP(String ip) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("DELETE FROM `banip` WHERE ip = ?;", bdDofusOnline);
			p.setString(1, ip);
			p.execute();
			return true;
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
		} finally {
			closePreparedStatement(p);
		}
		return false;
	}

	public static void SELECT_BANIP(String ip) {
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT id FROM `cuentas` WHERE `ultimaIP` = '" + ip + "';", Emu.BDDOFUSONLINE);
			while (RS.next()) {
				int idcuenta = RS.getInt("id");
				Cuenta acc = MundoDofus.getCuenta(idcuenta);
				if (acc != null) {
					acc.setBaneado(true);
				} else {
					UPDATE_BANEADO(idcuenta);
				}
			}
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closeResultSet(RS);
		}
	}

	private static void UPDATE_BANEADO(int cuentaid) {
		PreparedStatement p = null;
		String consultaSQL = "UPDATE `cuentas` SET `baneado`=? WHERE `id`= ?";
		try {
			p = nuevaTransaccion(consultaSQL, bdDofusOnline);
			p.setInt(1, 1);
			p.setInt(2, cuentaid);
			p.execute();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		} finally {
			closePreparedStatement(p);
		}
	}

	static void CARGAR_PUESTOS_MERCADILLOS() {
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT * FROM `mercadillos` ORDER BY id ASC", Emu.BDDOFUSONLINE);
			while (RS.next()) {
				MundoDofus.addPuestoMercadillo(
						new Mercadillo(RS.getInt("mapa"), RS.getFloat("porcVenta"), RS.getShort("tiempoVenta"),
								RS.getShort("cantidad"), RS.getShort("nivelMax"), RS.getString("categorias")));
			}
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closeResultSet(RS);
		}
	}

	static void CARGAR_OBJETOS_MERCADILLOS() {
		ResultSet RS = null;
		ResultSet RSC = null;
		try {
			RS = executeQuery(
					"SELECT i.*" + " FROM `objetos` AS i,`mercadillo_objetos` AS h" + " WHERE i.id = h.objeto",
					Emu.BDDOFUSONLINE);
			while (RS.next()) {
				int id = RS.getInt("id");
				int modeloID = RS.getInt("modelo");
				int cantidad = RS.getInt("cantidad");
				int posicion = RS.getInt("posicion");
				String stats = RS.getString("stats");
				int idOdjevivo = RS.getInt("objevivo");
				MundoDofus.addObjeto(
						MundoDofus.objetoIniciarServer(id, modeloID, cantidad, posicion, stats, idOdjevivo), false);
			}
			RSC = executeQuery("SELECT * FROM `mercadillo_objetos`", Emu.BDDOFUSONLINE);
			while (RSC.next()) {
				Mercadillo puesto = MundoDofus.getPuestoMerca(RSC.getInt("mapa"));
				int dueno = RSC.getInt("dueno");
				if (puesto == null) {
					continue;
				}
				puesto.addObjMercaAlPuesto(new ObjetoMercadillo(RSC.getInt("precio"), RSC.getByte("cantidad"), dueno,
						MundoDofus.getObjeto(RSC.getInt("objeto"))));
			}
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closeResultSet(RS);
			closeResultSet(RSC);
		}
	}

	static void CARGAR_CUENTAS_MERCADILLO() {
		ResultSet RSC = null;
		ArrayList<Integer> cuentacar = new ArrayList<>();
		try {
			RSC = executeQuery("SELECT dueno FROM `mercadillo_objetos`", Emu.BDDOFUSONLINE);
			while (RSC.next()) {
				int dueno = RSC.getInt("dueno");
				if (!cuentacar.contains(dueno)) {
					Cuenta cuenta = MundoDofus.getCuenta(dueno);
					if (cuenta == null) {
						cuentacar.add(dueno);
						GestorSQL.CARGAR_CUENTA_POR_ID(dueno);
					}
				}
			}
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closeResultSet(RSC);
		}
	}

	static void VACIA_Y_ACTUALIZA_OBJ_MERCADILLOS() {
		PreparedStatement p2 = null;
		try {
			p2 = nuevaTransaccion("TRUNCATE TABLE `mercadillo_objetos`", bdDofusOnline);
			p2.execute();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closePreparedStatement(p2);
		}
	}

	static void VACIA_Y_ACTUALIZA_OBJ_MERCADILLOS2(ArrayList<ObjetoMercadillo> lista) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("INSERT INTO `mercadillo_objetos` (`mapa`,`dueno`,`precio`,`cantidad`,`objeto`) "
					+ "VALUES(?,?,?,?,?);", bdDofusOnline);
			for (ObjetoMercadillo objMerca : lista) {
				if (objMerca.getDueÑo() == -1) {
					continue;
				}
				p.setInt(1, objMerca.getIDDelPuesto());
				p.setInt(2, objMerca.getDueÑo());
				p.setInt(3, objMerca.getPrecio());
				p.setInt(4, objMerca.getTipoCantidad(false));
				p.setInt(5, objMerca.getObjeto().getID());
				p.execute();
			}
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closePreparedStatement(p);
		}
	}

	static void CARGAR_ANIMACIONES() {
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT * from animaciones;", Emu.BDDOFUSONLINE);
			while (RS.next()) {
				MundoDofus.addAnimation(new Animacion(RS.getInt("id"), RS.getInt("id2"), RS.getString("nombre"),
						RS.getInt("area"), RS.getInt("accion"), RS.getInt("talla")));
			}
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closeResultSet(RS);
		}
	}

	static void CARGAR_OBJEVIVOS() {
		ResultSet RS = null;
		ResultSet RS2 = null;
		PreparedStatement p = null;
		try {
			RS = executeQuery("SELECT * from objevivos;", Emu.BDDOFUSONLINE);
			while (RS.next()) {
				int objevivo = RS.getInt("objevivo");
				int idobj = RS.getInt("id");
				RS2 = executeQuery("SELECT `id` FROM `objetos` WHERE `id`=" + objevivo + ";", Emu.BDDOFUSONLINE);
				if (RS2.next()) {
					MundoDofus.addObjevivo(new Objevivo(idobj, RS.getInt("anoComida"), RS.getInt("fechaComida"),
							RS.getInt("horaComida"), RS.getInt("humor"), RS.getInt("mascara"), RS.getInt("tipo"),
							RS.getInt("xp"), RS.getInt("anoInter"), RS.getInt("fechaInter"), RS.getInt("horaInter"),
							RS.getInt("anoObtenido"), RS.getInt("fechaObtenido"), RS.getInt("horaObtenido"),
							RS.getInt("asociado"), RS.getInt("modeloReal"), objevivo, RS.getString("stats")));
				} else {
					try {
						p = nuevaTransaccion("DELETE FROM `objevivos` WHERE id = ?;", bdDofusOnline);
						p.setInt(1, idobj);
						p.execute();
					} catch (SQLException e) {
						System.out.println("ERROR SQL: " + e.getMessage());
					} finally {
						closePreparedStatement(p);
					}
				}
			}
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closeResultSet(RS);
		}
	}

	public static boolean AGREGAR_OBJEVIVOS(Objevivo objevivo) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("INSERT INTO `objevivos` VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);",
					bdDofusOnline);
			p.setInt(1, objevivo.getID());
			p.setInt(2, objevivo.getFeedYears());
			p.setInt(3, objevivo.getFeedDate());
			p.setInt(4, objevivo.getFeedHours());
			p.setInt(5, objevivo.getHumeur());
			p.setInt(6, objevivo.getMascara());
			p.setInt(7, objevivo.getType());
			p.setLong(8, objevivo.getXp());
			p.setInt(9, objevivo.getToYears());
			p.setInt(10, objevivo.getToDate());
			p.setInt(11, objevivo.getToHours());
			p.setInt(12, objevivo.getHasYears());
			p.setInt(13, objevivo.getHasDate());
			p.setInt(14, objevivo.getHasHours());
			p.setInt(15, objevivo.getAsociado());
			p.setInt(16, objevivo.getrealtemplate());
			p.setInt(17, objevivo.getItemObjevivo());
			p.setString(18, objevivo.getStat());
			p.execute();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			return false;
		} finally {
			closePreparedStatement(p);
		}
		return true;
	}

	public static void SALVAR_OBJEVIVO(Objevivo obvi) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("UPDATE `objevivos` SET `xp` = ?,`mascara` = ?,`stats` = ?,`humor` = ?,`asociado` = ?,"
					+ "`modeloReal` = ? WHERE id = ?;", bdDofusOnline);
			p.setLong(1, obvi.getXp());
			p.setInt(2, obvi.getMascara());
			p.setString(3, obvi.getStat());
			p.setInt(4, obvi.getHumeur());
			p.setInt(5, obvi.getAsociado());
			p.setInt(6, obvi.getrealtemplate());
			p.setInt(7, obvi.getID());
			p.execute();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
		} finally {
			closePreparedStatement(p);
		}
	}

	static void CARGAR_RETOS() {
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT * from retos;", Emu.BDDOFUSONLINE);
			while (RS.next()) {
				MundoDofus.addReto(new Reto(RS.getInt("id"), RS.getString("bonus")));
			}
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closeResultSet(RS);
		}
	}

	static void CARGAR_MERCANTES() { // TODO:
		ResultSet RS = null;
		ResultSet RS2 = null;
		try {
			RS = executeQuery("SELECT * from `mercante_mapas` WHERE `personajes`!='';", Emu.BDDOFUSONLINE);
			while (RS.next()) {
				short idmapa = (short) RS.getInt("idmapa");
				String pjs = RS.getString("personajes");
				Mapa map = MundoDofus.getMapa(idmapa);
				String[] persos = pjs.split("\\|");
				for (String personaje : persos) {
					int idpj = Integer.parseInt(personaje);
					RS2 = executeQuery("SELECT `cuenta` FROM `personajes` WHERE `id`=" + idpj + ";", Emu.BDDOFUSONLINE);
					if (RS2.next()) {
						int idcu = RS2.getInt("cuenta");
						cargarCompteID(idcu);
					}
				}
				if (map != null) {
					map.addMercantesMapa(pjs);
				}
			}
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closeResultSet(RS);
		}
	}

	public static void SALVAR_MERCANTES(Mapa mapa) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("UPDATE `mercante_mapas` SET `personajes` = ? WHERE idmapa = ?;", bdDofusOnline);
			p.setString(1, mapa.getMercantes());
			p.setInt(2, mapa.getID());
			p.execute();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
		} finally {
			closePreparedStatement(p);
		}
	}

	static void ACTUALIZAR_IA_MOB(MobModelo mob) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("UPDATE `mobs` SET `tipoIA` = ? WHERE id = ?;", bdDofusOnline);
			p.setInt(1, mob.getTipoInteligencia());
			p.setInt(2, mob.getID());
			p.execute();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
		} finally {
			closePreparedStatement(p);
		}
	}

	static void LOGGED_ZERO() {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("UPDATE `cuentas` SET logeado=0;", bdDofusOnline);
			p.execute();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
		} finally {
			closePreparedStatement(p);
		}
	}

	static void CARGAR_OBJETOS_MERCANTES() {
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT * from mercante_objetos;", Emu.BDDOFUSONLINE);
			while (RS.next()) {
				MundoDofus.agregarTienda(new Tienda(RS.getInt("objeto"), RS.getInt("precio"), RS.getInt("cantidad")),
						false);
			}
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closeResultSet(RS);
		}
	}

	static void AGREGAR_ITEM_TIENDA(Tienda tienda) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("REPLACE INTO `mercante_objetos` VALUES(?,?,?);", bdDofusOnline);
			p.setInt(1, tienda.getItemId());
			p.setInt(2, tienda.getPrecio());
			p.setInt(3, tienda.getCantidad());
			p.execute();
		} catch (SQLException e) {
			Emu.creaLogs(e);
		} finally {
			closePreparedStatement(p);
		}
	}

	static void BORRAR_ITEM_TIENDA(int id) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("DELETE FROM `mercante_objetos` WHERE objeto = ?;", bdDofusOnline);
			p.setInt(1, id);
			p.execute();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
		} finally {
			closePreparedStatement(p);
		}
	}

	public static void ACTUALIZAR_PRECIO_TIENDA(int objeto, int precio) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("UPDATE `mercante_objetos` SET `precio` = ? WHERE objeto = ?;", bdDofusOnline);
			p.setInt(1, precio);
			p.setInt(2, objeto);
			p.execute();
		} catch (SQLException e) {
			Emu.creaLogs(e);
		} finally {
			closePreparedStatement(p);
		}
	}

	public static void ACTUALIZAR_CANT_TIENDA(int objeto, int cantidad) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("UPDATE `mercante_objetos` SET `cantidad` = ? WHERE objeto = ?;", bdDofusOnline);
			p.setInt(1, cantidad);
			p.setInt(2, objeto);
			p.execute();
		} catch (SQLException e) {
			Emu.creaLogs(e);
		} finally {
			closePreparedStatement(p);
		}
	}

	static void ACTUALIZAR_PUERTA_CERCADO(int mapa, int celda) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("UPDATE cercados SET `celdapuerta` = ? WHERE `mapa` = ?;", bdDofusOnline);
			p.setInt(1, celda);
			p.setInt(2, mapa);
			p.execute();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closePreparedStatement(p);
		}
	}

	static void ACTUALIZAR_CELDAS_OBJETO(int mapa, String celdas) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("UPDATE cercados SET `celdasobjeto` = ? WHERE `mapa` = ?;", bdDofusOnline);
			p.setString(1, celdas);
			p.setInt(2, mapa);
			p.execute();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closePreparedStatement(p);
		}
	}

	static void ACTUALIZAR_CELDA_MONTURA(int mapa, int celdas) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("UPDATE cercados SET `celdamontura` = ? WHERE `mapa` = ?;", bdDofusOnline);
			p.setInt(1, celdas);
			p.setInt(2, mapa);
			p.execute();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closePreparedStatement(p);
		}
	}

	public static void BORRAR_MOBSFIX_MAPA(int mapa) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("DELETE FROM mobs_fix WHERE mapa = ?;", bdDofusOnline);
			p.setInt(1, mapa);
			p.execute();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closePreparedStatement(p);
		}
	}

	static void CARGAR_ENCARNACIONES() {
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT * from encarnaciones;", Emu.BDDOFUSONLINE);
			while (RS.next()) {
				MundoDofus.addEncarnacion(new Encarnacion(RS.getInt("id"), RS.getInt("clase"), RS.getInt("nivel"),
						RS.getLong("experiencia"), RS.getInt("segundos"), RS.getString("hechizos")));
			}
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closeResultSet(RS);
		}
		return;
	}

	static void CARGAR_LIBROS_RUNICOS() {
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT * from libros_runicos;", Emu.BDDOFUSONLINE);
			while (RS.next()) {
				MundoDofus.addLibrosRunicos(new LibrosRunicos(RS.getInt("id"), RS.getInt("nivel"),
						RS.getLong("experiencia")));
			}
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closeResultSet(RS);
		}
		return;
	}

	public static void AGREGAR_PRISMA(Prisma prisma) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion(
					"INSERT INTO prismas(`id`,`alineacion`,`nivel`,`mapa`,`celda`,`area`, `honor`) VALUES(?,?,?,?,?,?,?);",
					bdDofusOnline);
			p.setInt(1, prisma.getID());
			p.setInt(2, prisma.getAlineacion());
			p.setInt(3, prisma.getNivel());
			p.setInt(4, prisma.getMapa());
			p.setInt(5, prisma.getCelda());
			p.setInt(6, prisma.getAreaConquistada());
			p.setInt(7, prisma.getHonor());
			p.execute();
		} catch (SQLException e) {
			Emu.creaLogs(e);
		} finally {
			closePreparedStatement(p);
		}
	}

	public static void AGREGAR_ENCARNACION(Encarnacion encarnacion) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion(
					"INSERT INTO encarnaciones(`id`,`nivel`,`clase`,`experiencia`,`segundos`,`hechizos`) VALUES(?,?,?,?,?,?);",
					bdDofusOnline);
			p.setInt(1, encarnacion.getID());
			p.setInt(2, encarnacion.getNivel());
			p.setInt(3, encarnacion.getClase());
			p.setLong(4, encarnacion.getExperiencia());
			p.setInt(5, encarnacion.getSegundos());
			p.setString(6, encarnacion.stringHechizosABD());
			p.execute();
		} catch (SQLException e) {
			Emu.creaLogs(e);
		} finally {
			closePreparedStatement(p);
		}
	}

	public static void AGREGAR_LIBROS_RUNICOS(LibrosRunicos librosRunicos) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion(
					"INSERT INTO libros_runicos(`id`,`nivel`,`experiencia`) VALUES(?,?,?);",
					bdDofusOnline);
			p.setInt(1, librosRunicos.getID());
			p.setInt(2, librosRunicos.getNivel());
			p.setLong(3, librosRunicos.getExperiencia());

			p.execute();
		} catch (SQLException e) {
			Emu.creaLogs(e);
		} finally {
			closePreparedStatement(p);
		}
	}

	public static void BORRAR_PRISMA(int id) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("DELETE FROM prismas WHERE id = ?;", bdDofusOnline);
			p.setInt(1, id);
			p.execute();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
		} finally {
			closePreparedStatement(p);
		}
	}

	static void SALVAR_ENCARNACION(Encarnacion encarnacion) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion(
					"UPDATE encarnaciones SET `nivel` = ?, `experiencia` = ?, `hechizos`= ?, `segundos`= ? WHERE `id` = ?;",
					bdDofusOnline);
			p.setInt(1, encarnacion.getNivel());
			p.setLong(2, encarnacion.getExperiencia());
			p.setString(3, encarnacion.stringHechizosABD());
			p.setInt(4, encarnacion.getSegundos());
			p.setInt(5, encarnacion.getID());
			p.execute();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closePreparedStatement(p);
		}
	}

	static void SALVAR_LIBROS_RUNICOS(LibrosRunicos librosRunicos) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion(
					"UPDATE libros_runicos SET `nivel` = ?, `experiencia` = ? WHERE `id` = ?;",
					bdDofusOnline);
			p.setInt(1, librosRunicos.getNivel());
			p.setLong(2, librosRunicos.getExperiencia());
			p.setInt(3, librosRunicos.getID());
			p.execute();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closePreparedStatement(p);
		}
	}

	public static void INSERT_COFRE(Cofre cofre) {
		PreparedStatement p = null;
		try {
			String consultaSQL = "INSERT INTO cofres(`id`,`casa`,`mapa`,`celda`,`objetos`,`kamas`, `clave`,`dueno` ) VALUES(?,?,?,?,?,?,?,?);";
			p = nuevaTransaccion(consultaSQL, bdDofusOnline);
			p.setInt(1, cofre.getID());
			p.setInt(2, cofre.getCasaPorID());
			p.setInt(3, cofre.getMapaID());
			p.setInt(4, cofre.getCeldaID());
			p.setString(5, "");
			p.setInt(6, 0);
			p.setString(7, "-");
			p.setInt(8, cofre.getDueÑoID());
			p.executeUpdate();
		} catch (Exception e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closePreparedStatement(p);
		}
	}

	static void SALVAR_PRISMA(Prisma prisma) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("UPDATE prismas SET `nivel` = ?, `honor` = ?, `area`= ? WHERE `id` = ?;",
					bdDofusOnline);
			p.setInt(1, prisma.getNivel());
			p.setInt(2, prisma.getHonor());
			p.setInt(3, prisma.getAreaConquistada());
			p.setInt(4, prisma.getID());
			p.execute();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closePreparedStatement(p);
		}
	}

	static void CARGAR_COFRE() {
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT * from cofres;", Emu.BDDOFUSONLINE);
			while (RS.next()) {
				MundoDofus.addCofre(new Cofre(RS.getInt("id"), RS.getInt("casa"), RS.getShort("mapa"),
						RS.getInt("celda"), RS.getString("objetos"), RS.getInt("kamas"), RS.getString("clave"),
						RS.getInt("dueno")));
			}
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closeResultSet(RS);
		}
	}

	static int MAX_ID_CUENTAS() {
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT MAX(id) AS max FROM cuentas;", Emu.BDDOFUSONLINE);
			int id = 0;
			boolean encontrado = RS.first();
			if (encontrado) {
				id = RS.getInt("max");
			}
			return id;
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closeResultSet(RS);
		}
		return 1;
	}

	static void CARGAR_MAPAS_D() {
		String str = "";
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT `dato` from `datos` WHERE `id`=1;", Emu.BDDOFUSONLINE);
			if (RS.next()) {
				str = RS.getString("dato");
				for (String splitob : str.split(";")) {
					if (splitob.equals("")) {
						continue;
					}
					if (!MundoDofus.mapasCargar.contains(Integer.parseInt(splitob))) {
						MundoDofus.mapasCargar.add(Integer.parseInt(splitob));
						cargarMapa(Integer.parseInt(splitob));
					}
				}
			}
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closeResultSet(RS);
		}
	}

	static void CARGAR_INVASIONES() {
		String str = "";
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT `dato` from `datos` WHERE `id`=3;", Emu.BDDOFUSONLINE);
			if (RS.next()) {
				str = RS.getString("dato");
				if (!str.equals("0")) {
					boolean valor = false;
					if (str.split("\\*")[1].equals("true")) {
						valor = true;
					} else {
						valor = false;
					}
					Emu.Maldicion = valor;
					if (str.split("\\*")[2].equals("true")) {
						valor = true;
					} else {
						valor = false;
					}
					Emu.Bufo = valor;
					Emu.tiempoInvasion = str.split("\\*")[0];
				}
			}
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closeResultSet(RS);
		}
	}

	static void ACTUALIZA_MAPAS_PERSO(int mapid, String personaliza, String limpieza) {
		String consultaSQL = "UPDATE mapas SET `personaliza` = ?,`limpieza` = ? WHERE `id` = ?;";
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion(consultaSQL, bdDofusOnline);
			p.setString(1, personaliza);
			p.setString(2, limpieza);
			p.setInt(3, mapid);
			p.executeUpdate();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
			Emu.creaLogs(e);
		} finally {
			closePreparedStatement(p);
		}
	}

	static void ACTUALIZAR_INVASIONES() {
		String consultaSQL = "UPDATE datos SET `dato` = ? WHERE `id` = ?;";
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion(consultaSQL, bdDofusOnline);
			p.setString(1, Emu.tiempoInvasion + "*" + Emu.Maldicion + "*" + Emu.Bufo);
			p.setInt(2, 3);
			p.executeUpdate();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
			Emu.creaLogs(e);
		} finally {
			closePreparedStatement(p);
		}
	}

	public static void CODIFICAR_COFRE(Personaje P, Cofre t, String packet) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("UPDATE `cofres` SET `clave`=? WHERE `id`=? AND dueno=?;", bdDofusOnline);
			p.setString(1, packet);
			p.setInt(2, t.getID());
			p.setInt(3, P.getCuentaID());
			p.execute();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
		} finally {
			closePreparedStatement(p);
		}
	}

	public static void ACTUALIZAR_COFRE(Cofre cofre) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("UPDATE `cofres` SET `kamas`=?, `objetos`=? WHERE `id`=?", bdDofusOnline);
			p.setLong(1, cofre.getKamas());
			p.setString(2, cofre.analizarObjetoCofreABD());
			p.setInt(3, cofre.getID());
			p.execute();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
		} finally {
			closePreparedStatement(p);
		}
	}

	static void AGREGAR_COMANDO_GM(String gm, String comando) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("INSERT INTO comandos(`nombre gm`,`comando`) VALUES(?,?);", bdDofusOnline);
			p.setString(1, gm);
			p.setString(2, comando);
			p.execute();
		} catch (SQLException e) {
			Emu.creaLogs(e);
		} finally {
			closePreparedStatement(p);
		}
	}

	static NPCModelo cargarModeloNpcs(int idC) {
		NPCModelo modelo = null;
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT * FROM `npcs_modelo` WHERE `id` = " + idC + ";", Emu.BDDOFUSONLINE);
			if (RS.next()) {
				int id = RS.getInt("id");
				int bonusValue = RS.getInt("bonusValue");
				int gfxID = RS.getInt("gfxID");
				int escalaX = RS.getInt("scaleX");
				int escalaY = RS.getInt("scaleY");
				int sexo = RS.getInt("sexo");
				int color1 = RS.getInt("color1");
				int color2 = RS.getInt("color2");
				int color3 = RS.getInt("color3");
				String accesorios = RS.getString("accesorios");
				int extraClip = RS.getInt("extraClip");
				int customArtWork = RS.getInt("customArtWork");
				int preguntaID = RS.getInt("pregunta");
				String ventas = RS.getString("ventas");
				String nombre = RS.getString("nombre");
				int misionid = RS.getInt("misionID");
				long kamas = RS.getLong("kamas");
				modelo = new NPCModelo(id, bonusValue, gfxID, escalaX, escalaY, sexo, color1, color2, color3,
						accesorios, extraClip, customArtWork, preguntaID, ventas, nombre, kamas, misionid);
				MundoDofus.addNpcModelo(modelo);
			}
		} catch (Exception e) {
			System.out.println("SQL ERROR(NpcTemplateData): " + e.getMessage());
		} finally {
			closeResultSet(RS);
		}
		return modelo;
	}

	static Mapa cargarMapa(int id) {
		Mapa map = null;
		ResultSet RS = null;
		ResultSet RSD = null;
		try {
			RS = executeQuery("SELECT * FROM `mapas` WHERE `id` = " + id + ";", Emu.BDDOFUSONLINE);
			if (RS.next()) {
				map = new Mapa(RS.getInt("id"), RS.getString("fecha"), RS.getByte("ancho"), RS.getByte("alto"),
						RS.getString("key"), RS.getString("posPelea"), RS.getString("mapData"), RS.getString("mobs"),
						RS.getByte("X"), RS.getByte("Y"), RS.getInt("subArea"), RS.getByte("nroGrupo"),
						RS.getInt("maxMobs"), RS.getInt("capacidad"), RS.getInt("descripcion"), RS.getInt("bgID"),
						RS.getInt("musicID"), RS.getInt("ambienteID"), RS.getInt("outDoor"), RS.getInt("capabilities"),
						RS.getString("personaliza"), RS.getString("limpieza"));
				MundoDofus.addMapa(map);
				// cargarCasas(map);
				// cargarCercados(id);
				cargarNpcs(id);
				cargarCeldasTeleport(id);
				accionFinPelea(map);
			}
			RSD = executeQuery("SELECT * from `mobs_fix` WHERE `mapa` = " + id + ";", Emu.BDDOFUSONLINE);
			while (RSD.next()) {
				if (map == null || map.getCelda(RSD.getInt("celda")) == null) {
					continue;
				}
				map.addGrupoFix(RSD.getInt("celda"), RSD.getString("mobs"));
			}
		} catch (SQLException e) {
			Emu.creaLogs(e);
		} finally {
			closeResultSet(RS);
			closeResultSet(RSD);
		}
		return map;
	}

	static void CARGAR_RECAUDADORES() {
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT * FROM `recaudadores`;", Emu.BDDOFUSONLINE);
			while (RS.next()) {
				Mapa mapa = MundoDofus.getMapa(RS.getShort("mapa"));
				if (mapa == null) {
					return;
				}
				MundoDofus.addRecaudador(new Recaudador(RS.getInt("id"), RS.getShort("mapa"), RS.getInt("celda"),
						RS.getByte("orientacion"), RS.getInt("gremio"), RS.getString("nombre1"),
						RS.getString("nombre2"), RS.getString("objetos"), RS.getLong("kamas"), RS.getLong("xp")));
			}
		} catch (SQLException e) {
			Emu.creaLogs(e);
		} finally {
			closeResultSet(RS);
		}
		return;
	}

	static void CARGAR_PRISMAS() {
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT * FROM `prismas`;", Emu.BDDOFUSONLINE);
			while (RS.next()) {
				Mapa mapa = MundoDofus.getMapa(RS.getShort("mapa"));
				if (mapa == null) {
					return;
				}
				int alin = RS.getInt("alineacion");
				int prismid = RS.getInt("id");
				MundoDofus.addPrisma(new Prisma(prismid, alin, RS.getInt("nivel"), RS.getShort("mapa"),
						RS.getInt("celda"), RS.getInt("honor"), RS.getInt("area")));
				SubArea subarea = mapa.getSubArea();
				Area area = subarea.getArea();
				subarea.setAlineacion(alin);
				subarea.setPrismaID(prismid);
				area.setPrismaID(prismid);
				area.setAlineacion(alin);
			}
		} catch (SQLException e) {
			Emu.creaLogs(e);
		} finally {
			closeResultSet(RS);
		}
		return;
	}

	static void cargarCasas() {
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT * FROM `casas`;", Emu.BDDOFUSONLINE);
			while (RS.next()) {
				Mapa mapa = MundoDofus.getMapa(RS.getShort("mapaFuera"));
				if (mapa == null) {
					continue;
				}
				int dueno = RS.getInt("dueno");
				if (dueno != 0) {
					Cuenta acc = MundoDofus.getCuenta(dueno);
					if (acc == null) {
						GestorSQL.cargarCompteID(dueno);
					}
				}
				MundoDofus.agregarCasa(new Casa(RS.getInt("id"), RS.getShort("mapaFuera"), RS.getInt("celdaFuera"),
						dueno, RS.getInt("precio"), RS.getInt("gremio"), RS.getInt("acceso"), RS.getString("clave"),
						RS.getInt("derechosGremio"), RS.getInt("mapaDentro"), RS.getInt("celdaDentro"),
						RS.getString("mapasContenidos"), RS.getString("impuesto")));
			}
		} catch (Exception e) {
			System.out.println("3 SQL ERROR(Casas): " + e.getMessage());
		} finally {
			closeResultSet(RS);
		}
		return;
	}

	static void cargarCercados() {
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT * FROM `cercados`;", Emu.BDDOFUSONLINE);
			while (RS.next()) {
				Mapa mapa = MundoDofus.getMapa(RS.getShort("mapa"));
				if (mapa == null) {
					continue;
				}
				MundoDofus.addCercado(new Cercado(RS.getInt("propietario"), mapa, RS.getInt("celda"),
						RS.getInt("tamano"), RS.getInt("gremio"), RS.getInt("precio"), RS.getInt("celdamontura"),
						RS.getString("criando"), RS.getInt("celdapuerta"), RS.getString("celdasobjeto"),
						RS.getInt("objetos"), RS.getString("objetoscolocados")));
			}
		} catch (Exception e) {
			System.out.println("SQL ERROR(MountparkData): " + e.getMessage());
		} finally {
			closeResultSet(RS);
		}
		return;
	}

	/*
	 * static void cargarFusionados() { ResultSet RS = null; try { RS =
	 * executeQuery("SELECT dato FROM `datos` WHERE `id` = 3;", Emu.BDDOFUSONLINE);
	 * if (RS.next()) { String itemFusionados = RS.getString("dato"); for (String
	 * str : itemFusionados.split(",")) { if (str.equals("")) continue; if
	 * (Emu.activaFusiones) { try {
	 * //MundoDofus._fusionados.add(Integer.parseInt(str)); } catch (Exception e) {}
	 * } else { try { BORRAR_OBJETO(Integer.parseInt(str)); } catch (Exception e) {}
	 * } } if (!Emu.activaFusiones) { SALVAR_FUSIONADOS(""); } } } catch (Exception
	 * e) { System.out.println("SQL ERROR(MountparkData): " + e.getMessage()); }
	 * finally { closeResultSet(RS); } return; }
	 */

	static void cargarRegalos() {
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT * FROM `regalos`;", Emu.BDDOFUSONLINE);
			while (RS.next()) {
				String tiempo = RS.getString("tiempo");
				Date date = new Date();
				Calendar cal2 = Calendar.getInstance();
				cal2.setTime(date);
				Calendar calzz = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
				try {
					calzz.setTime(sdf.parse(tiempo));
				} catch (ParseException e) {
					Emu.creaLogs(e);
					return;
				}
				if (cal2.compareTo(calzz) < 0) { // SOLO ENTRAN LOS DE LA HORA INDICADA
					String valor = tiempo + ";" + RS.getString("itemcant") + ";" + RS.getString("maxcuenta");
					MundoDofus.itemsRegalos.put(RS.getInt("id"), valor);
				}
			}
		} catch (Exception e) {
			System.out.println("SQL ERROR(MountparkData): " + e.getMessage());
		} finally {
			closeResultSet(RS);
		}
		return;
	}

	private static void cargarNpcs(int id) {
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT * FROM `npcs_ubicacion` WHERE `mapa` = " + id + ";", Emu.BDDOFUSONLINE);
			while (RS.next()) {
				Mapa mapa = MundoDofus.getMapa(RS.getShort("mapa"));
				if (mapa == null) {
					continue;
				}
				mapa.addNPC(RS.getInt("npc"), RS.getInt("celda"), RS.getInt("orientacion"));
			}
		} catch (Exception e) {
			System.out.println("SQL ERROR(NpcData): " + e.getMessage());
		} finally {
			closeResultSet(RS);
		}
		return;
	}

	private static void accionFinPelea(Mapa map) {
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT * FROM `accion_pelea` WHERE `mapa` = " + map.getID(), Emu.BDDOFUSONLINE);
			while (RS.next()) {
				Mapa mapa = MundoDofus.getMapa(RS.getShort("mapa"));
				if (mapa == null) {
					continue;
				}
				if (!MundoDofus.mapasBoost.contains(mapa.getID())) {
					MundoDofus.mapasBoost.add(mapa.getID());
				}
				int accion = RS.getInt("accion");
				String args = RS.getString("args");
				if (accion == 0) {
					int mapid = Integer.parseInt(args.split(",")[0]);
					if (!MundoDofus.mapasBoost.contains(mapid)) {
						MundoDofus.mapasBoost.add(mapid);
					}
				}
				mapa.addAccionFinPelea(RS.getInt("tipoPelea"),
						new Accion(accion, RS.getString("args"), RS.getString("condicion")));
			}
		} catch (Exception e) {
			System.out.println("SQL ERROR(MapData): " + e.getMessage());
		} finally {
			closeResultSet(RS);
		}
	}

	private static void cargarCeldasTeleport(int mapid) {
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT * FROM `celdas_teleport` WHERE `mapa` = " + mapid + ";", Emu.BDDOFUSONLINE);
			while (RS.next()) {
				Mapa mapa = MundoDofus.getMapa(RS.getShort("mapa"));
				if (mapa == null || mapa.getCelda(RS.getInt("celda")) == null) {
					continue;
				}
				switch (RS.getInt("evento")) {
				case 1:
					int accion = RS.getInt("accion");
					int celda = RS.getInt("celda");
					if (accion == 0) {
						if (mapa.celdasTPs.toString().equals("")) {
							mapa.celdasTPs.append(celda);
						} else {
							mapa.celdasTPs.append("," + celda);
						}
					}
					mapa.getCelda(celda).addAccionEnUnaCelda(accion, RS.getString("args"), RS.getString("condiciones"));
					break;
				default:
					System.out.println("Accion Evento " + RS.getInt("evento") + " no implantado");
					break;
				}
			}
		} catch (Exception e) {
			System.out.println("SQL ERROR(CellData): " + e.getMessage());
		} finally {
			closeResultSet(RS);
		}
		return;
	}

	static Recaudador cargarRecaudador(int id) {
		Recaudador collector = null;
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT * FROM `recaudadores` WHERE `id` = " + id + ";", Emu.BDDOFUSONLINE);
			if (RS.next()) {
				Mapa mapa = MundoDofus.getMapa(RS.getShort("mapa"));
				if (mapa != null) {
					collector = new Recaudador(RS.getInt("id"), RS.getShort("mapa"), RS.getInt("celda"),
							RS.getByte("orientacion"), RS.getInt("gremio"), RS.getString("nombre1"),
							RS.getString("nombre2"), RS.getString("objetos"), RS.getLong("kamas"), RS.getLong("xp"));
					MundoDofus.addRecaudador(collector);
				}
			}
		} catch (Exception e) {
			System.out.println("SQL ERROR(recaudadores): " + e.getMessage());
		} finally {
			closeResultSet(RS);
		}
		return collector;
	}

	static Hechizo cargarHechizos(int ids) {
		Hechizo hechizo = null;
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT * FROM `hechizos` WHERE `id` = " + ids + ";", Emu.BDDOFUSONLINE);
			if (RS == null) {
				return null;
			}
			if (RS.next()) {
				int id = RS.getInt("id");
				String afectados = RS.getString("afectados").replace("|", ":").replace(",", ";");
				hechizo = new Hechizo(id, RS.getString("nombre"), RS.getInt("sprite"), RS.getString("spriteInfos"),
						afectados, RS.getInt("duracion"), RS.getInt("tipo"));
				MundoDofus.addHechizo(hechizo);
				StatsHechizos l1 = null;
				if (!RS.getString("nivel1").equalsIgnoreCase("") && !RS.getString("nivel1").equalsIgnoreCase("-1")) {
					l1 = analizarHechizoStats(id, 1, RS.getString("nivel1"));
				}
				StatsHechizos l2 = null;
				if (!RS.getString("nivel2").equalsIgnoreCase("") && !RS.getString("nivel2").equalsIgnoreCase("-1")) {
					l2 = analizarHechizoStats(id, 2, RS.getString("nivel2"));
				}
				StatsHechizos l3 = null;
				if (!RS.getString("nivel3").equalsIgnoreCase("") && !RS.getString("nivel3").equalsIgnoreCase("-1")) {
					l3 = analizarHechizoStats(id, 3, RS.getString("nivel3"));
				}
				StatsHechizos l4 = null;
				if (!RS.getString("nivel4").equalsIgnoreCase("") && !RS.getString("nivel4").equalsIgnoreCase("-1")) {
					l4 = analizarHechizoStats(id, 4, RS.getString("nivel4"));
				}
				StatsHechizos l5 = null;
				if (!RS.getString("nivel5").equalsIgnoreCase("") && !RS.getString("nivel5").equalsIgnoreCase("-1")) {
					l5 = analizarHechizoStats(id, 5, RS.getString("nivel5"));
				}
				StatsHechizos l6 = null;
				if (!RS.getString("nivel6").equalsIgnoreCase("") && !RS.getString("nivel6").equalsIgnoreCase("-1")) {
					l6 = analizarHechizoStats(id, 6, RS.getString("nivel6"));
				}
				hechizo.addStatsHechizos(1, l1);
				hechizo.addStatsHechizos(2, l2);
				hechizo.addStatsHechizos(3, l3);
				hechizo.addStatsHechizos(4, l4);
				hechizo.addStatsHechizos(5, l5);
				hechizo.addStatsHechizos(6, l6);
			}
		} catch (Exception e) {
			System.out.println("SQL ERROR(hechizos): " + e.getMessage());
		} finally {
			closeResultSet(RS);
		}
		return hechizo;
	}

	static MobModelo cargarMonstruo(int ids) {
		MobModelo monster = null;
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT * FROM `mobs` WHERE `id` = " + ids + ";", Emu.BDDOFUSONLINE);
			while (RS.next()) {
				int id = RS.getInt("id");
				String nombre = RS.getString("nombre");
				int gfxID = RS.getInt("gfxID");
				int alineacion = RS.getInt("alineacion");
				String colores = RS.getString("colores");
				String grados = RS.getString("grados").replaceAll(" ", "").replaceAll(",g", "g")
						.replaceAll(":\\{l:", "@").replaceAll(",r:\\[", ",").replaceAll("\\]", "|")
						.replaceAll("\\]\\}", "|");
				String hechizos = RS.getString("hechizos").replace("|", ";");
				String stats = RS.getString("stats");
				String pdvs = RS.getString("pdvs");
				String pts = RS.getString("puntos");
				String iniciativa = RS.getString("iniciativa");
				int mK = RS.getInt("minKamas");
				int MK = RS.getInt("maxKamas");
				int tipoIA = RS.getInt("tipoIA");
				String xp = RS.getString("exps");
				int talla = RS.getInt("talla");
				boolean capturable;
				if (RS.getInt("capturable") == 1) {
					capturable = true;
				} else {
					capturable = false;
				}
				monster = new MobModelo(id, nombre, gfxID, alineacion, colores, grados, hechizos, stats, pdvs, pts,
						iniciativa, mK, MK, xp, tipoIA, capturable, talla);
				MundoDofus.addMobModelo(id, monster);
				cargarDrops(id);
			}
		} catch (Exception e) {
			Emu.creaLogs(e);
		} finally {
			closeResultSet(RS);
		}
		return monster;
	}

	private static void BORRAR_DROPC(int objeto, int mob) {
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion("DELETE FROM `drops` WHERE `objeto` =" + objeto + " AND `mob` =" + mob + ";",
					bdDofusOnline);
			p.execute();
		} catch (SQLException e) {
			Emu.creaLogs(e);
		} finally {
			closePreparedStatement(p);
		}
	}

	private static void cargarDrops(int mob) {
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT * FROM `drops` WHERE `mob` = " + mob + ";", Emu.BDDOFUSONLINE);
			ArrayList<Integer> duplica = new ArrayList<>();
			while (RS.next()) {
				int obj = RS.getInt("objeto");
				int elmob = RS.getInt("mob");
				if (duplica.contains(obj)) {
					BORRAR_DROPC(obj, elmob);
					continue;
				}
				MobModelo MM = MundoDofus.getMobModelo(elmob);
				if (MM == null) {
					continue;
				}
				MM.addDrop(new Drop(RS.getInt("objeto"), RS.getInt("prospeccion"), RS.getInt("porcentaje"),
						RS.getInt("max")));
				duplica.add(obj);
			}
		} catch (Exception e) {
			System.out.println("SQL ERROR(drops): " + e.getMessage());
		} finally {
			closeResultSet(RS);
		}
		return;
	}

	static Objeto cargarObjetos(int ids) {
		Objeto item = null;
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT * FROM `objetos` WHERE `id` = " + ids + ";", Emu.BDDOFUSONLINE);
			if (RS.next()) {
				int id = RS.getInt("id");
				int modeloID = RS.getInt("modelo");
				int cantidad = RS.getInt("cantidad");
				int posicion = RS.getInt("posicion");
				String stats = RS.getString("stats");
				int objevivo = RS.getInt("objevivo");
				item = new Objeto(id, modeloID, cantidad, posicion, stats, objevivo);
				MundoDofus.addObjeto(item, false);
			}
		} catch (Exception e) {
			System.out.println("SQL ERROR(ItemData): " + e.getMessage());
		} finally {
			closeResultSet(RS);
		}
		return item;
	}

	static Dragopavo cargarDragopavo(int id) {
		Dragopavo mount = null;
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT * FROM `dragopavos` WHERE `id` = " + id + ";", Emu.BDDOFUSONLINE);
			if (RS.next()) {
				mount = new Dragopavo(RS.getInt("id"), RS.getInt("color"), RS.getInt("sexo"), RS.getInt("amor"),
						RS.getInt("resistencia"), RS.getInt("nivel"), RS.getLong("xp"), RS.getString("nombre"),
						RS.getInt("fatiga"), RS.getInt("energia"), RS.getInt("reproducciones"), RS.getInt("madurez"),
						RS.getInt("serenidad"), RS.getString("objetos"), RS.getString("ancestros"),
						RS.getString("habilidad"), RS.getInt("talla"), RS.getInt("celda"), RS.getShort("mapa"),
						RS.getInt("dueno"), RS.getInt("orientacion"), RS.getInt("fecundable"), RS.getInt("pareja"),
						RS.getString("vip"));
				MundoDofus.addDragopavo(mount);
			}
		} catch (Exception e) {
			System.out.println("SQL ERROR(dragopavo): " + e.getMessage());
		} finally {
			closeResultSet(RS);
		}
		return mount;
	}

	static Casa cargarCasaID(int id) {
		Casa house = null;
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT * FROM `casas` WHERE `id` = " + id + ";", Emu.BDDOFUSONLINE);
			if (RS.next()) {
				Mapa mapa = MundoDofus.getMapa(RS.getShort("mapaFuera"));
				if (mapa != null) {
					house = new Casa(RS.getInt("id"), RS.getShort("mapaFuera"), RS.getInt("celdaFuera"),
							RS.getInt("dueno"), RS.getInt("precio"), RS.getInt("gremio"), RS.getInt("acceso"),
							RS.getString("clave"), RS.getInt("derechosGremio"), RS.getInt("mapaDentro"),
							RS.getInt("celdaDentro"), RS.getString("mapasContenidos"), RS.getString("impuesto"));
					MundoDofus.agregarCasa(house);
				}
			}
		} catch (Exception e) {
			System.out.println("2 SQL ERROR(casas): " + e.getMessage());
		} finally {
			closeResultSet(RS);
		}
		return house;
	}

	static void cargarCuentadesdeNombre(String nombre) {
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT `cuenta` FROM `personajes` WHERE `nombre` = '" + nombre + "';",
					Emu.BDDOFUSONLINE);
			if (RS.next()) {
				int idacc = (RS.getInt("cuenta"));
				cargarCompteID(idacc);
			}
		} catch (Exception e) {
			System.out.println("SQL ERROR(casas): " + e.getMessage());
		} finally {
			closeResultSet(RS);
		}
		return;
	}

	static Cuenta cargarCompte(String name) {
		Cuenta account = null;
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT * FROM `cuentas` WHERE `cuenta` = '" + name + "'", Emu.BDDOFUSONLINE);
			if (RS.next()) {
				if (RS.getString("cuenta").equals(name)) {
					account = new Cuenta(RS.getInt("id"), RS.getString("cuenta"), RS.getString("pass"),
							RS.getString("apodo"), RS.getString("pregunta"), RS.getString("respuesta"), RS.getInt("gm"),
							RS.getInt("vip"), (RS.getInt("baneado") == 1), RS.getString("ultimaIP"),
							RS.getString("ultimaConexion"), RS.getString("objetos"), RS.getInt("kamas"),
							RS.getString("amigos"), RS.getString("enemigos"), RS.getString("establo"),
							RS.getInt("primeravez"), RS.getInt("regalo"), RS.getString("tiempoLoot"),
							RS.getInt("logeado"), RS.getString("tieneRegalos"), RS.getInt("generadores"),
							RS.getInt("tiempogen"), RS.getString("tutoriales"));
					MundoDofus.addCuenta(account);
					cargarPj(account);
				}
			}
		} catch (Exception e) {
			System.out.println("SQL ERROR(cuentas): " + e.getMessage());
		} finally {
			closeResultSet(RS);
		}
		return account;
	}

	public static Cuenta cargarCompteID(int id) {
		Cuenta cuenta = null;
		ResultSet RS = null;
		if (MundoDofus.getCuenta(id) != null) {
			return MundoDofus.getCuenta(id);
		}
		try {
			RS = executeQuery("SELECT * FROM `cuentas` WHERE `id` = '" + id + "'", Emu.BDDOFUSONLINE);
			if (RS.next()) {
				cuenta = new Cuenta(RS.getInt("id"), RS.getString("cuenta").toLowerCase(), RS.getString("pass"),
						RS.getString("apodo"), RS.getString("pregunta"), RS.getString("respuesta"), RS.getInt("gm"),
						RS.getInt("vip"), (RS.getInt("baneado") == 1), RS.getString("ultimaIP"),
						RS.getString("ultimaConexion"), RS.getString("objetos"), RS.getInt("kamas"),
						RS.getString("amigos"), RS.getString("enemigos"), RS.getString("establo"),
						RS.getInt("primeravez"), RS.getInt("regalo"), RS.getString("tiempoLoot"), RS.getInt("logeado"),
						RS.getString("tieneRegalos"), RS.getInt("generadores"), RS.getInt("tiempogen"),
						RS.getString("tutoriales"));
				MundoDofus.addCuenta(cuenta);
				cargarPj(cuenta);
			}
		} catch (Exception e) {
			System.out.println("SQL ERROR(cuentas): " + e.getMessage());
		} finally {
			closeResultSet(RS);
		}
		return cuenta;
	}

	private static void cargarPj(Cuenta obj) {
		Personaje pj = null;
		ResultSet RS = null;
		ResultSet RSX = null;
		try {
			RS = executeQuery("SELECT * FROM `personajes` WHERE `cuenta` = " + obj.getID() + ";", Emu.BDDOFUSONLINE);
			while (RS.next()) {
				TreeMap<Integer, Integer> stats = new TreeMap<>();
				stats.put(CentroInfo.STATS_ADD_VITALIDAD, RS.getInt("vitalidad"));
				stats.put(CentroInfo.STATS_ADD_FUERZA, RS.getInt("fuerza"));
				stats.put(CentroInfo.STATS_ADD_SABIDURIA, RS.getInt("sabiduria"));
				stats.put(CentroInfo.STATS_ADD_INTELIGENCIA, RS.getInt("inteligencia"));
				stats.put(CentroInfo.STATS_ADD_SUERTE, RS.getInt("suerte"));
				stats.put(CentroInfo.STATS_ADD_AGILIDAD, RS.getInt("agilidad"));
				pj = new Personaje(RS.getInt("id"), RS.getString("nombre"), RS.getInt("sexo"), RS.getInt("clase"),
						RS.getInt("color1"), RS.getInt("color2"), RS.getInt("color3"), RS.getLong("kamas"),
						RS.getInt("puntosHechizo"), RS.getInt("capital"), RS.getInt("energia"), RS.getInt("nivel"),
						RS.getLong("xp"), RS.getInt("talla"), RS.getInt("gfx"), RS.getByte("alineacion"),
						RS.getInt("cuenta"), stats, RS.getInt("mostrarAmigos"), RS.getByte("mostrarAlineacion"),
						RS.getString("canal"), RS.getShort("mapa"), RS.getInt("celda"), RS.getString("objetos"),
						RS.getInt("porcVida"), RS.getString("hechizos"), RS.getString("posSalvada"),
						RS.getString("oficios"), RS.getInt("xpMontura"), RS.getInt("montura"), RS.getInt("honor"),
						RS.getInt("deshonor"), RS.getInt("nivelAlin"), RS.getString("zaaps"), RS.getInt("titulo"),
						RS.getInt("esposo"), RS.getString("tienda"), RS.getInt("mercante"), RS.getInt("sFuerza"),
						RS.getInt("sInteligencia"), RS.getInt("sAgilidad"), RS.getInt("sSuerte"),
						RS.getInt("sVitalidad"), RS.getInt("sSabiduria"), RS.getInt("restriccionesA"),
						RS.getInt("restriccionesB"), RS.getInt("encarnacion"), RS.getString("misiones"),
						RS.getString("objMisiones"), RS.getString("koliseo"), RS.getInt("dia"),
						RS.getString("objetivosdiarios"), RS.getInt("actualprecio"), RS.getInt("resets"),
						RS.getInt("desafdiario"), RS.getString("datos"), RS.getString("titulos"),
						RS.getInt("etapahechi"), RS.getInt("claseesp"), RS.getString("pasebatalla"));
				MundoDofus.addPersonaje(pj);
				if (MundoDofus.getCuenta(RS.getInt("cuenta")) != null) {
					MundoDofus.getCuenta(RS.getInt("cuenta")).addPerso(pj);
				}
				int guildId = esPJenGremio(pj.getID());
				if (guildId >= 0) {
					Gremio guild = MundoDofus.getGremio(guildId);
					if (guild != null) {
						MiembroGremio member = guild.getMiembro(RS.getInt("id"));
						if (member != null) {
							pj.setMiembroGremio(member);
						}
					}
				}
				int esposo = RS.getInt("esposo");
				if (esposo != 0) {
					RSX = executeQuery("SELECT cuenta FROM `personajes` WHERE `id` = " + esposo + ";",
							Emu.BDDOFUSONLINE);
					if (RSX.next()) {
						cargarCompteID(RSX.getInt("cuenta"));
					}
				}
			}
		} catch (Exception e) {
			System.out.println("SQL ERROR(personajes): " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			if (RSX != null) {
				closeResultSet(RSX);
			}
			closeResultSet(RS);
		}
		return;
	}

	static void REPLACE_MASCOTA(Mascota mascota) {
		String consultaSQL = "REPLACE INTO `mascotas` VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?);";
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion(consultaSQL, bdDofusOnline);
			p.setInt(1, mascota.getID());
			p.setInt(2, mascota.getPDV());
			p.setString(3, mascota.getStringStats());
			p.setInt(4, mascota.getNroComidas());
			p.setInt(5, mascota.getObeso() ? 7 : 0);
			p.setInt(6, mascota.getDelgado() ? 7 : 0);
			p.setInt(7, mascota.getUltimaComida());
			p.setInt(8, mascota.getAño());
			p.setInt(9, mascota.getMes());
			p.setInt(10, mascota.getDia());
			p.setInt(11, mascota.getHora());
			p.setInt(12, mascota.getMinuto());
			p.setInt(13, mascota.getIDModelo());
			p.executeUpdate();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closePreparedStatement(p);
		}
	}

	static void BORRAR_MASCOTA(int id) {
		String consultaSQL = "DELETE FROM mascotas WHERE objeto = ?;";
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion(consultaSQL, bdDofusOnline);
			p.setInt(1, id);
			p.executeUpdate();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
			Emu.creaLogs(e);
		} finally {
			closePreparedStatement(p);
		}
	}

	static void CARGAR_MASCOTAS() {
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT * from mascotas;", Emu.BDDOFUSONLINE);
			while (RS.next()) {
				MundoDofus.addMascota(new Mascota(RS.getInt("objeto"), RS.getInt("pdv"), RS.getString("stats"),
						RS.getInt("comidas"), RS.getInt("aÑo"), RS.getInt("mes"), RS.getInt("dia"), RS.getInt("hora"),
						RS.getInt("minuto"), RS.getInt("ultimaComida"), RS.getInt("obeso"), RS.getInt("delgado"),
						RS.getInt("idModelo")));
			}
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closeResultSet(RS);
		}
	}

	static int CARGAR_COMIDAS_MASCOTAS() {
		int numero = 0;
		ResultSet RS = null;
		try {
			RS = executeQuery("SELECT * from comida_mascotas;", Emu.BDDOFUSONLINE);
			while (RS.next()) {
				MundoDofus.agregarMascotaModelo(RS.getInt("mascota"),
						new MascotaModelo(RS.getInt("maximoComidas"), RS.getString("comidas")));
				numero++;
			}
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			Emu.creaLogs(e);
		} finally {
			closeResultSet(RS);
		}
		return numero;
	}
}
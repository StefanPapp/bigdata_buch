package de.jofre.hivemanager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HiveManager {

	private final static Logger log = Logger.getLogger(HiveManager.class
			.getName());
	
	private static String driverName = "org.apache.hive.jdbc.HiveDriver";
	Connection con = null;

	public static int PAGING_SIZE = 50;

	// Trenne die Verbindung zu Hive, falls eine besteht
	public void disconnect() {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				log.log(Level.SEVERE, "Fehler beim Schließen der Verbindung!");
				e.printStackTrace();
			}
		}
	}

	// Verbinde zu Hive
	public boolean connect(String host, String port, String user,
			String password) {

		try {
			Class.forName(driverName);
		} catch (ClassNotFoundException e1) {
			log.log(Level.SEVERE,
					"Fehler beim Herstellen der Verbindung - Treiberklasse nicht gefunden!");
			e1.printStackTrace();
			return false;
		}

		try {
			con = DriverManager.getConnection("jdbc:hive2://" + host + ":"
					+ port, user, password);
		} catch (SQLException e) {
			log.log(Level.SEVERE, "Fehler beim Herstellen der Verbindung!");
			e.printStackTrace();
			return false;
		}

		if (con != null) {
			
			// Registriere UDF um Paging zu ermöglichen
			Statement stmt;
			try {
				stmt = con.createStatement();
				stmt.execute("ADD JAR /usr/local/hive/lib/15_HiveUDFs-0.0.1-SNAPSHOT-jar-with-dependencies.jar");
				stmt.execute("CREATE TEMPORARY FUNCTION auto_inc as 'de.jofre.hive.udf.AutoIncrement'");
			} catch (SQLException e) {
				log.log(Level.SEVERE,
						"Fehler beim Registrieren der temporären UDFs.");
				e.printStackTrace();
			}
			
			return true;
		} else {
			return false;
		}
	}

	// Rufe Datenbanken ab
	public List<String> getDatabases() {
		log.log(Level.INFO, "Erfrage Datenbanken...");
		List<String> databases = new ArrayList<String>();
		try {
			Statement stmt = con.createStatement();
			String sql = "show databases";
			ResultSet res = stmt.executeQuery(sql);
			while (res.next()) {
				databases.add(res.getString(1));
				log.log(Level.INFO, "\t" + res.getString(1));
			}
		} catch (SQLException e) {
			log.log(Level.SEVERE, "Fehler beim Abfragen der Datenbanken!");
			e.printStackTrace();
		}
		return databases;
	}

	// Rufe alle Tabellen einer Datenbank ab
	public List<String> getTables(String database) {
		log.log(Level.INFO, "Erfrage Tabellen aus " + database + "...");
		List<String> tables = new ArrayList<String>();
		try {
			switchDatabase(database); // Wechsle Datenbank
			Statement stmt = con.createStatement();
			String sql = "show tables";
			ResultSet res = stmt.executeQuery(sql);
			while (res.next()) {
				tables.add(res.getString(1));
				log.log(Level.INFO, "\t" + res.getString(1));
			}
		} catch (SQLException e) {
			log.log(Level.SEVERE,
					"Fehler beim Abfragen der Tabellen aus Datenbank "
							+ database + ".");
			e.printStackTrace();
		}
		return tables;
	}

	// Wechsle Datenbank / Schema
	public void switchDatabase(String database) {
		try {
			// con.setCatalog(database); // Nicht implementiert!

			// Von "use DATABSE" via JDBC wird in der Regel abgeraten!
			Statement stmt = con.createStatement();
			stmt.execute("use " + database);
		} catch (SQLException e) {
			log.log(Level.SEVERE, "Fehler beim Selektieren der Datenbank "
					+ database + ".");
			e.printStackTrace();
		}
	}

	// Zähle Einträge in einer Tabelle
	public int getCount(String table) {
		int result = -1;
		try {
			Statement stmt = con.createStatement();
			String sql = "SELECT COUNT(*) FROM " + table;
			ResultSet res = stmt.executeQuery(sql);
			if (res.next()) {
				result = res.getInt(1);
			}
		} catch (SQLException e) {
			log.log(Level.SEVERE, "Fehler beim Abfragen der Einträge aus "
					+ table + ".");
			e.printStackTrace();
		}
		return result;
	}

	// Führe eine beliebige Abfrage aus
	public ResultSet executeQuery(String query) {
		log.log(Level.INFO, "Führe Query '" + query + "' aus.");
		ResultSet res = null;
		try {
			Statement stmt = con.createStatement();
			res = stmt.executeQuery(query);
		} catch (SQLException e) {
			log.log(Level.SEVERE, "Fehler beim Ausführen des Statements.");
			e.printStackTrace();
		}
		return res;
	}

	// Liste alle Rollen in Hive auf
	public List<String> getRoles() {
		List<String> roles = new ArrayList<String>();
		try {
			Statement stmt = con.createStatement();
			String sql = "show roles";
			ResultSet res = stmt.executeQuery(sql);
			while (res.next()) {
				roles.add(res.getString(1));
			}
		} catch (SQLException e) {
			log.log(Level.SEVERE, "Fehler beim der Rollen.");
			e.printStackTrace();
		}
		return roles;
	}
	
	public List<String> getRolesOfUser(String user) {
		List<String> roles = new ArrayList<String>();
		try {
			Statement stmt = con.createStatement();
			String sql = "show role grant user " + user;
			ResultSet res = stmt.executeQuery(sql);
			while (res.next()) {
				roles.add(res.getString(1));
			}
		} catch (SQLException e) {
			log.log(Level.SEVERE, "Fehler beim der Rollen.");
			e.printStackTrace();
		}
		return roles;		
	}
}

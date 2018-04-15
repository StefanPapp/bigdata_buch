package de.jofre.mysqlexport;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

public class Main {

	private static Connection conn;
	private static final String DB_HOST = "single";
	private static final String DB_PORT = "3306";
	private static final String DB_DATABASE = "company";
	private static final String DB_TABLE = "people";
	private static final String DB_USER = "mysqluser";
	private static final String DB_PASSWORD = "mysqluser";
	
	private static final String jobs[] = { "Lehrer", "Consultant", "Verkäufer",
			"Gärtner", "Sportler", "Finanzberater", "Musiker", "Bestatter",
			"Fahrzeughändler", "Bänker", "Informatiker", "Student", "Pastor",
			"Polizist", "Krankenpfleger", "Arzt", "Fahrlehrer", "Pirat",
			"Bürgermeister", "Politiker", "Geologe", "Professor",
			"Wissenschaftler", "Übersetzer", "Kellner", "Schneider", "Koch",
			"Security", "Schauspieler", "Regisseur", "Schuhmacher", "Müller",
			"Fleischer" };
	
	// Verbindung zum MySQL-Server herstellen
	private static void connect() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://" + DB_HOST + ":"
					+ DB_PORT + "/?" + "user=" + DB_USER
					+ "&" + "password=" + DB_PASSWORD);
			
			// Anlegen der DB falls sie nicht existiert
			Statement statement = conn.createStatement();
			String sql = "CREATE DATABASE IF NOT EXISTS " + DB_DATABASE;
			statement.executeUpdate(sql);
			
			// Wechsle zur gerade erstellten Datenbank
			conn.setCatalog(DB_DATABASE);
			
			// Anlegen der Tabelle falls sie nicht existiert
			if (!tableExists()) {
				System.out.println("Tabelle '" + DB_TABLE + "' wird angelegt...");
				tableCreate();
			}
			
			System.out.println("Verbindung hergestellt!");
		} catch (Exception e) {
			System.out
					.println("Fehler bei der Verbindungsherstellung zur Datenbank.");
			e.printStackTrace();
		}

	}

	private static boolean tableExists() {
		DatabaseMetaData dbm;
		try {
			dbm = conn.getMetaData();
			ResultSet tables = dbm.getTables(null, null, DB_TABLE, null);
			if (tables.next()) {
				tables.close();
				return true;
			}
		} catch (SQLException e1) {
			System.out.println("Fehler beim Überprüfen der Existenz der Tabelle '" + DB_TABLE + "'.");
			e1.printStackTrace();
		}
		return false;
	}
	
	private static void tableCreate() {
		Statement statement;
		try {
			statement = conn.createStatement();
			String sql = "CREATE TABLE " + DB_TABLE + " (pid INTEGER not NULL AUTO_INCREMENT,name VARCHAR(40),job VARCHAR(30),PRIMARY KEY(pid))";
			statement.executeUpdate(sql);
			System.out.println("Tabelle erstellt!");
		} catch (SQLException e) {
			System.out.println("Fehler beim Anlegen der Tabelle '" + DB_TABLE + "'.");
			e.printStackTrace();
		}

	}
	
	private static void readData() {
		try {
			Statement query = conn.createStatement();
			String sql = "SELECT * FROM " + DB_TABLE;
			ResultSet res = query.executeQuery(sql);
			while (res.next()) {
				int key = res.getInt(1); // Spaltenindex beginnt bei 1
				String name = res.getString(2);
				String job = res.getString(3);
				System.out.println(key + ", " + name + ", " + job);
			}
			res.close();
			query.close();
			System.out.println("Daten wurden gelesen!");
		} catch (Exception e) {
			System.out.println("Fehler beim Abfragen der Daten!");
			e.printStackTrace();
		}
	}

	private static void generateData(String args[]) {
		int count = 2000;
		if (args.length == 2) {
			count = Integer.parseInt(args[1]);
		} else {
			System.out
					.println("Keine Anzahl für Datengenerierung angegeben - Verwende "
							+ count);
		}
				
		// Insert der Daten
		String sql = "INSERT INTO " + DB_TABLE + "(name, job) VALUES(?,?)";
		try {
			Random r = new Random();
			PreparedStatement ps = conn.prepareStatement(sql);
			for (int i = 0; i < count; i++) {
				ps.setString(1, NamesByGender.getRandomName() + " "
						+ NamesByGender.getRandomLastName());
				ps.setString(2, jobs[r.nextInt(jobs.length)]);
				ps.executeUpdate();
			}
			ps.close();
			System.out.println("Daten wurden erzeugt!");
		} catch (Exception e) {
			System.out.println("Fehler beim Generieren der Daten.");
			e.printStackTrace();
		}
	}
	
	private static void clearData() {
		String sql = "DELETE * FROM " + DB_TABLE;
		Statement statement;
		try {
			statement = conn.createStatement();
			statement.executeUpdate(sql);
			statement.close();
			System.out.println("Daten wurden gelöscht!");
		} catch (SQLException e) {
			System.out.println("Fehler beim Löschen der Datensätze.");
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		if (args.length < 1) {
			System.out
					.println("Argument erwartet\n\t-clear - Löscht alle Testdaten\n\t-read - Gibt die Testaten aus\n\t-generate n - Generiert n Testeinträge");
		}

		connect();
		if (conn != null) {

			if (args[0].equals("-clear")) {

				clearData();
				
			} else if (args[0].equals("-read")) {

				readData();

			} else if (args[0].equals("-generate")) {

				generateData(args);

			} else {
				System.out.println("Befehl '" + args[0] + "' nicht erkannt.");
			}

			// Schließe die Verbindung zur DB
			try {
				conn.close();
			} catch (Exception e) {
				System.out.println("Fehler beim Schließen der Verbindung.");
				e.printStackTrace();
			}
		}
		System.out.println("Programm wird beendet!");
	}

}

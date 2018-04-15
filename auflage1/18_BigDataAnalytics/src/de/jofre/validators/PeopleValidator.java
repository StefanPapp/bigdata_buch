package de.jofre.validators;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.jofre.hadoopcontroller.HadoopProperties;
import de.jofre.types.Person;

public class PeopleValidator {
	
	private final static Logger log = Logger.getLogger(PeopleValidator.class
			.getName());

	/**
	 * Handelt es sich bei dem Eingabestring um den Namen einer berühmten Person?
	 * 
	 * @param input
	 * @return
	 */
	public static Person isFamousPerson(String input) {
		Person p = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");

			Connection con = DriverManager.getConnection("jdbc:mysql://"
					+ HadoopProperties.get("mysql_address") + ":"
					+ HadoopProperties.get("mysql_port") + "/"
					+ HadoopProperties.get("mysql_db") + "?user="
					+ HadoopProperties.get("mysql_user") + "&password="
					+ HadoopProperties.get("mysql_password"));

			Statement stmt = con.createStatement();
			ResultSet resultSet = stmt
					.executeQuery("SELECT * FROM "+HadoopProperties.get("mysql_db")+".birthdays WHERE LOWER(name) LIKE LOWER('%"
							+ input + "%')");

			System.out.println("Suche person");
			if (resultSet.next()) {
				System.out.println("Person gefunden");
				p = new Person();
				p.setBirthday(resultSet.getDate("birthday"));
				String name = resultSet.getString("name");
				String description = name.substring(name.indexOf(',')+1, name.length());
				name = name.substring(0, name.indexOf(','));
				System.out.println("Descr: " + description + " name: " + name);
				p.setDescription(description);
				p.setName(name);
			}
			con.close();
		} catch (Exception e) {
			log.log(Level.SEVERE,
					"Fehler beim Herstellen der Verbindung zu MySQL.");
			e.printStackTrace();
		}
		return p;
	}

	/**
	 * Hat an diesem Datum eine bekannte Person Geburtstag?
	 * 
	 * @param input
	 * @return
	 */
	public static Person isFamousBirthday(String input) {

		Date date = DateValidator.isDate(input);
		Person p = null;
		if (date != null) {

			try {
				Class.forName("com.mysql.jdbc.Driver");

				Connection con = DriverManager.getConnection("jdbc:mysql://"
						+ HadoopProperties.get("mysql_address") + ":"
						+ HadoopProperties.get("mysql_port") + "/"
						+ HadoopProperties.get("mysql_db") + "?user="
						+ HadoopProperties.get("mysql_user") + "&password="
						+ HadoopProperties.get("mysql_password"));

				Statement stmt = con.createStatement();
				ResultSet resultSet = stmt
						.executeQuery("SELECT * FROM "+HadoopProperties.get("mysql_db")+".birthdays WHERE birthday='"
								+ date.toString() + "'");

				if (resultSet.next()) {
					p = new Person();
					p.setBirthday(resultSet.getDate("birthday"));
					String name = resultSet.getString("name");
					String description = name.substring(name.indexOf(','), name.length());
					name = name.substring(0, name.indexOf(','));
					p.setDescription(description);
					p.setName(name);
					return p;
				}
				con.close();
			} catch (Exception e) {
				log.log(Level.SEVERE,
						"Fehler beim Herstellen der Verbindung zu MySQL.");
				e.printStackTrace();
			}
		}

		return p;
	}
}

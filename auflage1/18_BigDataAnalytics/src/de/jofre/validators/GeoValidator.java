package de.jofre.validators;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.jofre.hadoopcontroller.HadoopProperties;
import de.jofre.types.City;
import de.jofre.types.Country;
import de.jofre.types.Region;

public class GeoValidator {

	private final static Logger log = Logger.getLogger(GeoValidator.class
			.getName());

	/**
	 * Kann es sich bei dem Eingabestring um Längen- oder Breitengrad handeln?
	 * 
	 * @param input
	 * @return
	 */
	public static boolean isLongLatFormat(String input) {
		String regEx = "[-]{0,1}\\d{1,3}\\.\\d{0,8}";
		if (input.matches(regEx)) {
			if (NumberValidator.isDouble(input)) {
				Double d = Double.parseDouble(input);
				if ((d <= 180) && (d >= -180)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Repräsentiert der Längen- und Breitengrad eine Stadt?
	 * 
	 * @param longitude
	 * @param latitude
	 * @return
	 */
	public static City isLongLatLocation(String longitude, String latitude) {
		City c = null;
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
					.executeQuery("SELECT * FROM "+HadoopProperties.get("mysql_db")+".cities WHERE latitude="+ latitude + " AND longitude=" + longitude);

			if (resultSet.next()) {
				c = new City();
				c.setCity(resultSet.getString("city"));
				c.setCountry(resultSet.getString("country"));
				c.setLatitude(resultSet.getString("latitude"));
				c.setLongitude(resultSet.getString("longitude"));
				c.setPopulation(resultSet.getInt("population"));
			}
			con.close();
		} catch (Exception e) {
			log.log(Level.SEVERE,
					"Fehler beim Herstellen der Verbindung zu MySQL.");
			e.printStackTrace();
		}

		return c;
	}
	
	/**
	 * Ist eine Stadt unter dem Eingabestring bekannt?
	 * 
	 * @param input
	 * @return
	 */
	public static City isCity(String input) {
		City c = null;
		log.log(Level.INFO, "Duchsuche Datenbank nach Stadt '" + input + "'.");
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
					.executeQuery("SELECT * FROM "+HadoopProperties.get("mysql_db")+".cities WHERE LOWER(city)=LOWER('"
							+ input + "')");

			if (resultSet.next()) {
				log.log(Level.INFO, "Stadt '" + input + "' gefunden.");
				c = new City();
				c.setCity(resultSet.getString("city"));
				c.setCountry(resultSet.getString("country"));
				c.setLatitude(resultSet.getString("latitude"));
				c.setLongitude(resultSet.getString("longitude"));
				c.setPopulation(resultSet.getInt("population"));
			}
			con.close();
		} catch (Exception e) {
			log.log(Level.SEVERE,
					"Fehler beim Herstellen der Verbindung zu MySQL.");
			e.printStackTrace();
		}

		return c;
	}

	/**
	 * Existiert ein Land mit dem angegebenen Namen?
	 * 
	 * @param input
	 * @return
	 */
	public static Country isCountry(String input) {
		Country c = null;
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
					.executeQuery("SELECT * FROM "+HadoopProperties.get("mysql_db")+".countries WHERE LOWER(name)=LOWER('"
							+ input + "')");
			
			if (resultSet.next()) {
				c = new Country();
				c.setArea(resultSet.getDouble("area"));
				c.setCapital(resultSet.getString("capital"));
				c.setCode(resultSet.getString("code"));
				c.setContinent(resultSet.getString("continent"));
				c.setCurrencycode(resultSet.getString("currencycode"));
				c.setCurrencyname(resultSet.getString("currencyname"));
				c.setLanguage(resultSet.getString("languages"));
				c.setName(resultSet.getString("name"));
				c.setNeighbors(resultSet.getString("neighbors"));
				c.setPhone(resultSet.getString("phone"));
				c.setPopulation(resultSet.getInt("population"));
				c.setTopleveldomain(resultSet.getString("topleveldomain"));
			}
			
			con.close();
		} catch (Exception e) {
			log.log(Level.SEVERE,
					"Fehler beim Herstellen der Verbindung zu MySQL.");
			e.printStackTrace();
		}

		return c;
	}
	
	/**
	 * Bezeichnet input eine Topleveldomain? 
	 * 
	 * @param input
	 * @return
	 */
	public static String isDomain(String input) {
		String result = null;
		
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
					.executeQuery("SELECT name FROM "+HadoopProperties.get("mysql_db")+".countries WHERE LOWER(topleveldomain)=LOWER('"
							+ input + "')");

			if (resultSet.next()) {
				result = resultSet.getString("name");
			}
			con.close();
		} catch (Exception e) {
			log.log(Level.SEVERE,
					"Fehler beim Herstellen der Verbindung zu MySQL.");
			e.printStackTrace();
		}

		return result;
	}
	
	/**
	 * In welchen Ländern wird diese Sprache gesprochen?
	 * 
	 * @param language
	 * @return
	 */
	public static List<Country> isLanguageSpokenIn(String language) {
		Country c = null;
		log.log(Level.INFO, "Überprüfe Länder, in denen die Sprache '" + language + "' gesprochen wird.");
		
		List<Country> res = new ArrayList<Country>();
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
					.executeQuery("SELECT * FROM "+HadoopProperties.get("mysql_db")+".countries WHERE languages LIKE '%"
							+ language+"%'");

			while (resultSet.next()) {
				c = new Country();
				c.setArea(resultSet.getDouble("area"));
				c.setCapital(resultSet.getString("capital"));
				c.setCode(resultSet.getString("code"));
				c.setContinent(resultSet.getString("continent"));
				c.setCurrencycode(resultSet.getString("currencycode"));
				c.setCurrencyname(resultSet.getString("currencyname"));
				c.setLanguage(resultSet.getString("languages"));
				c.setName(resultSet.getString("name"));
				c.setNeighbors(resultSet.getString("neighbors"));
				c.setPhone(resultSet.getString("phone"));
				c.setPopulation(resultSet.getInt("population"));
				c.setTopleveldomain(resultSet.getString("topleveldomain"));
				res.add(c);
			}
			con.close();
		} catch (Exception e) {
			log.log(Level.SEVERE,
					"Fehler beim Herstellen der Verbindung zu MySQL.");
			e.printStackTrace();
		}

		return res;
	}
	
	/**
	 * Ist unter diesem Namen eine Region bekannt? Bisher werden nur deutsche
	 * Bundesländer gefunden.
	 * 
	 * @param input
	 * @return
	 */
	public static Region isRegion(String input) {
		Region r = null;
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
					.executeQuery("SELECT * FROM "+HadoopProperties.get("mysql_db")+".regions WHERE LOWER(name)=LOWER('"
							+ input + "') OR LOWER(shortname)=LOWER('" + input + "')");

			if (resultSet.next()) {
				r = new Region();
				r.setCountryId(resultSet.getInt("country"));
				r.setName(resultSet.getString("name"));
				r.setShortName(resultSet.getString("shortname"));
			}
			
			con.close();
			
		} catch (Exception e) {
			log.log(Level.SEVERE,
					"Fehler beim Herstellen der Verbindung zu MySQL.");
			e.printStackTrace();
		}

		return r;
	}	
}

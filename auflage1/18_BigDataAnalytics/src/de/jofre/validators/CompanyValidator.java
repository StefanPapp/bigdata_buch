package de.jofre.validators;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.jofre.hadoopcontroller.HadoopProperties;
import de.jofre.types.Company;

public class CompanyValidator {

	private final static Logger log = Logger.getLogger(CompanyValidator.class
			.getName());


	/**
	 * Ist die Firma in der Wissensdatenbank vorhanden? Wenn ja, gib sie zurück.
	 * 
	 * @param input
	 * @return
	 */
	public static Company isCompany(String input) {
		Company c = null;
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
					.executeQuery("SELECT * FROM "+HadoopProperties.get("mysql_db")+".companies WHERE LOWER(name)=LOWER('"
							+ input + "')");

			if (resultSet.next()) {
				c = new Company();
				c.setIndustry(resultSet.getString("industry"));
				c.setName(resultSet.getString("name"));
				c.setSector(resultSet.getString("sector"));
				c.setShortName(resultSet.getString("shortname"));
			}
			
			con.close();
		} catch (Exception e) {
			log.log(Level.SEVERE,
					"Fehler beim Herstellen der Verbindung zu MySQL.");
			e.printStackTrace();
		}

		return c;
	}


}

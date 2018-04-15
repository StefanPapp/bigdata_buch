package de.jofre.helper;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HadoopProperties {

	private static Properties prop = null;
	private final static Logger log = Logger.getLogger(HadoopProperties.class
			.getName());
	
	// Klasse wird einmalig initialisiert
	private static void initProps() {
		prop = new Properties();
		try {
			
			// Lesen der Datei aus dem ClassPath WebContent/WEB-INF/classes/
			prop.load(HadoopProperties.class.getClassLoader().getResourceAsStream("hadoop.properties"));
		} catch (IOException e) {
			log.log(Level.SEVERE, "Konnte Properties-Datei nicht laden!");
			e.printStackTrace();
		}
	}
	
	// Zugriff auf eine Eigenschaft in der hadoop.properties 
	public static String get(String key) {
		if (prop == null) initProps();
		String property = prop.getProperty(key);
		if (property == null) {
			log.log(Level.SEVERE, "Property '"+key+"' konnte nicht gefunden werden!");
		}
		return property;
	}
}

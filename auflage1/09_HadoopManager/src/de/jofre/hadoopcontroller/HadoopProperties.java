package de.jofre.hadoopcontroller;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HadoopProperties {

	private static Properties prop = null;
	private final static Logger log = Logger.getLogger(HadoopProperties.class
			.getName());
	
	private static void initProps() {
		prop = new Properties();
		try {
			prop.load(HadoopProperties.class.getClassLoader().getResourceAsStream("hadoop.properties"));
		} catch (IOException e) {
			log.log(Level.SEVERE, "Konnte Properties-Datei nicht laden!");
			e.printStackTrace();
		}
	}
	
	public static String get(String key) {
		if (prop == null) initProps();
		String property = prop.getProperty(key);
		if (property == null) {
			log.log(Level.SEVERE, "Property '"+key+"' konnte nicht gefunden werden!");
		}
		return property;
	}
}

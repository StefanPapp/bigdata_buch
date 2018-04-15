package de.jofre.helper;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.jsp.JspWriter;


public class JSPHelper {

	private final static Logger log = Logger.getLogger(JSPHelper.class
			.getName());
	
	public static void writeToJsp(JspWriter writer, String text) {
		if (writer != null) {
			try {
				writer.println(text);
			} catch (IOException e) {
				log.log(Level.WARNING, "Fehler beim Schreiben der JSP.");
				e.printStackTrace();
			}
		}
	}
}

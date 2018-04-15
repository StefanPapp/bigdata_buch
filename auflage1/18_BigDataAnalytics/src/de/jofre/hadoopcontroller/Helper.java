package de.jofre.hadoopcontroller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.jsp.JspWriter;

public class Helper {
	
	private final static Logger log = Logger.getLogger(Helper.class.getName());

	
	/**
	 * Zerlegt die URL eines Pfades auf dem HDFS in mehrere kleine Links
	 * 
	 * @param path: Gesamter Pfad, der aufgesplittet werden soll.
	 * @return Ein konstruiertes HTML-Element bestehend aus Links und Texten
	 */
	public static String hdfsPathToLinks(String path) {
		
		// Der Pfad muss mit hdfs:// beginnen
		if (path.indexOf("hdfs://") == -1) {
			log.log(Level.SEVERE, "HDFS path seems to be invalid: "+path);
			return "";
		}
		
		// Wenn der Pfad nicht auf / ended, dann hänge ein / an.
		if (!path.endsWith("/")) {
			path = path + "/";
		}
		
		// Der absolute Pfad des Links
		List<String> fullPath = new ArrayList<String>();
		
		// Der Name des Links
		List<String> linkName = new ArrayList<String>();
		
		String currentLinkName = "";
		
		// Splitte den Pfad auf
		for(int i="hdfs://".length(); i<path.length(); i++) {
			
			// Erstelle bei jedem / einen neuen Eintrag.
			if (path.charAt(i) == '/') {
				String newFullURL = path.substring(0,i);
				try {
					newFullURL = URLEncoder.encode(newFullURL, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					log.log(Level.WARNING, "Fehler beim Enkodieren des Strings: "+newFullURL);
					e.printStackTrace();
				}
				fullPath.add(newFullURL);
				linkName.add(currentLinkName);
				currentLinkName = "";
			} else {
				
				// Ist das aktuelle Zeichen kein /, dann füge das Zeichen
				// dem aktuellen Namen hinzu
				currentLinkName +=path.charAt(i);
			}
		}
		
		// Konstruiere den HTML-Code
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<fullPath.size(); i++) {
			sb.append("<a href=\"hdfs.jsp?currentDir="+fullPath.get(i)+"/\">"+linkName.get(i)+"</a>");
			if (i<fullPath.size()-1) {
				sb.append("/");
			}
		}
		
		return sb.toString();
	}
	
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

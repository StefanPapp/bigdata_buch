package de.jofre.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Liest die entsprechenden Inhalte der jeweiligen Testdaten aus den
 * angegebenen Dateien aus.
 * 
 * @author J. Freiknecht
 *
 */
public class DiagramDummyData {
	
	private static Logger log = Logger.getLogger(DiagramDummyData.class
			.getName());
	
	private static String readFile(String file) {
		InputStream input = DiagramDummyData.class.getResourceAsStream(file);
		InputStreamReader is = new InputStreamReader(input);
		StringBuilder sb=new StringBuilder();
		BufferedReader br = new BufferedReader(is);
		String read;
		try {
			read = br.readLine();
			while(read != null) {
			    sb.append(read);
			    read =br.readLine();

			}			
		} catch (IOException e) {
			log.log(Level.SEVERE, "Fehler beim Lesen der Dummy-Daten von '" + file + "'.");
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static String BUBBLE_CHART() {
		return DiagramDummyData.readFile("/diagramdummydata/bubblechart.json");
	}	
	
	public static String CALENDAR_CHART() {
		return DiagramDummyData.readFile("/diagramdummydata/calendarchart.json");
	}	
	
	public static String CHORD_CHART() {
		return DiagramDummyData.readFile("/diagramdummydata/chordchart.json");
	}	
	
	public static String CHORD_CHART_MATRIX() {
		return DiagramDummyData.readFile("/diagramdummydata/chordmatrix.json");
	}	
		
	public static String CHOROPLETH() {
		return DiagramDummyData.readFile("/diagramdummydata/choropleth.json");
	}	
	
	public static String COLLAPSIBLE_INTENDED_TREEVIEW() {
		return DiagramDummyData.readFile("/diagramdummydata/collapsibleintendedtree.json");
	}

	public static String COLLAPSIBLE_TREEVIEW() {
		return DiagramDummyData.readFile("/diagramdummydata/collapsibletree.json");
	}	
	
	public static String FLARE_CHART() {
		return DiagramDummyData.readFile("/diagramdummydata/flarechart.json");
	}	
	
	public static String GLOBE_CHART() {
		return DiagramDummyData.readFile("/diagramdummydata/globe3d.json");
	}
	
	public static String HIERARCHY_BAR() {
		return DiagramDummyData.readFile("/diagramdummydata/hierarchybar.json");
	}
	
	public static String SUNBURST_CHART() {
		return DiagramDummyData.readFile("/diagramdummydata/sunburstchart.json");
	}
	
	public static String WORD_CLOUD() {
		return DiagramDummyData.readFile("/diagramdummydata/wordcloud.txt");
	}	
	
	public static String ZOOMABLE_TREEMAP() {
		return DiagramDummyData.readFile("/diagramdummydata/zoomabletreemap.json");
	}
	
	public static String LINE_CHART() {
		return DiagramDummyData.readFile("/diagramdummydata/linechart.json");
	}	
}

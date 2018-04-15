package de.jofre.diagrams;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Generiert einen gewichteten Beziehungskreis. Eingehende und ausgehende
 * Beziehungen werden über die dicke der Verbindungslinien gewichtet.
 * 
 * Datentyp: JSON
 * 
 * Format:
 * (Bezeichner)
 * [
 * 	{
 * 		"name": "Uruguay",
 * 		"color": "#E41A1C"
 * 	},
 * 	{
 * 		"name": "Italien",
 * 		"color": "#FFFF33"
 * 	}, ...
 * ]
 * 
 * und Beziehung als Adjazenzmatrix (Anzahl der Daten pro Spalte und Zeile müssen der
 * der Bezeichner entsprechen)
 * 
 * [[0,0,0.00016780050176848078,0,0,0,0,0,0,0,0,0,0,0,0], ... ]
 */
public class ChordChart {

	private static Logger log = Logger.getLogger(ChordChart.class
			.getName());
	
	private int height;
	private int width;
	private String input;
	private String inputMatrix;
	
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public String getInput() {
		return input;
	}
	public void setInput(String input) {
		this.input = input;
	}
	public String getInputMatrix() {
		return inputMatrix;
	}
	public void setInputMatrix(String inputMatrix) {
		this.inputMatrix = inputMatrix;
	}	

	public ChordChart() {
		this.height = 400;
		this.width = 400;
	}
	
	public String getAdditionalJavaScript() {
		return "<!-- Chord-Chart benötigt keine weiteren JavaScript-Bibliotheken -->";
	}	
	
	public String getStyleSheet() {
		StringBuilder sb = new StringBuilder();
		sb.append("<style type=\"text/css\">\n");
		sb.append("\t#circle circle {fill: none; pointer-events: all; }\n");
		sb.append("\t.group path {fill-opacity: .5; }\n");
		sb.append("\tpath.chord {stroke: #000; stroke-width: .25px; }\n");
		sb.append("\t#circle:hover path.fade {display: none; }\n");
		sb.append("</style>\n\n");
		return sb.toString();
	}
	
	public String getJavaScript() {
		StringBuilder sb = new StringBuilder();
		Random r = new Random(1234);
		String strImageTagID = "id" + String.valueOf(r.nextInt(100000));
		
		if (this.input == null || this.input.equals("")) {
			log.log(Level.WARNING, "Erzeuge ein Chord-Chart ohne Input.");
		}
		
		// Div-Element
		sb.append("<div id=\"" + strImageTagID + "\"></div>\n");
		
		// Diagram-Script
		sb.append("<script type=\"text/javascript\">\n");
		
		
		sb.append("var width = "+width+", height = "+height+", outerRadius = Math.min(width, height) / 2 - 10, innerRadius = outerRadius - 24;\n\n");
		sb.append("var formatPercent = d3.format(\".1%\"); var arc = d3.svg.arc().innerRadius(innerRadius).outerRadius(outerRadius);\n\n");

		sb.append("var layout = d3.layout.chord().padding(.04).sortSubgroups(d3.descending).sortChords(d3.ascending);\n");
		sb.append("var path = d3.svg.chord().radius(innerRadius);\n");
		sb.append("var svg = d3.select(\"#"+strImageTagID+"\").append(\"svg\").attr(\"width\", width).attr(\"height\", height)\n");
		sb.append("\t.append(\"g\").attr(\"id\", \"circle\").attr(\"transform\", \"translate(\" + width / 2 + \",\" + height / 2 + \")\");\n\n");
		
		sb.append("svg.append(\"circle\").attr(\"r\", outerRadius);\n");
		
		// [ {"Year":"1985", "Month":"12"}, { ... } ]
		sb.append("var entities = JSON.parse('"+input+"');\n\n");
		sb.append("var matrix = JSON.parse('"+inputMatrix+"');\n\n");
		
		sb.append("layout.matrix(matrix);\n");
		sb.append("var group = svg.selectAll(\".group\").data(layout.groups).enter().append(\"g\")\n");
		sb.append("\t.attr(\"class\", \"group\").on(\"mouseover\", mouseover);\n\n");
		
		sb.append("group.append(\"title\").text(function(d, i) {\n");
		sb.append("\treturn entities[i].name + \": \" + formatPercent(d.value) + \" der Ursprünge\";\n");
		sb.append("});\n\n");
		
		sb.append("var groupPath = group.append(\"path\").attr(\"id\", function(d, i) { return \"group\" + i; })\n");
		sb.append("\t.attr(\"d\", arc).style(\"fill\", function(d, i) { return entities[i].color; });\n\n");
		
		sb.append("var groupText = group.append(\"text\").attr(\"x\", 6).attr(\"dy\", 15);\n\n");
		
		sb.append("groupText.append(\"textPath\").attr(\"xlink:href\", function(d, i) { return \"#group\" + i; })\n");
		sb.append("\t.text(function(d, i) { return entities[i].name; });\n\n");
		
		sb.append("groupText.filter(function(d, i) { return groupPath[0][i].getTotalLength() / 2 - 16 < this.getComputedTextLength(); }).remove();\n\n");
		
		sb.append("var chord = svg.selectAll(\".chord\").data(layout.chords).enter().append(\"path\").attr(\"class\", \"chord\")\n");
		sb.append("\t.style(\"fill\", function(d) { return entities[d.source.index].color; }).attr(\"d\", path);\n\n");
		
		sb.append("chord.append(\"title\").text(function(d) {\n");
		sb.append("\treturn entities[d.source.index].name + \" -> \" + entities[d.target.index].name\n");
		sb.append("\t+ \": \" + formatPercent(d.source.value) + \"\\n\" + entities[d.target.index].name\n");
		sb.append("\t+ \" -> \" + entities[d.source.index].name + \": \" + formatPercent(d.target.value);\n");
		sb.append("});\n\n");
		
		sb.append("function mouseover(d, i) { chord.classed(\"fade\", function(p) { return p.source.index != i && p.target.index != i; });\n}\n\n");
		
		sb.append("</script>");
		
		return sb.toString();
	}	
}

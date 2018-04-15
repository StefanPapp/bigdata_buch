package de.jofre.diagrams;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Generiert ein einfaches Liniendiagramm.
 * 
 * Datentyp: JSON
 * 
 * Format:
 * [["1999", "100"], ["2000", "200"], ...] 
 */
public class LineChart {

	private static Logger log = Logger.getLogger(LineChart.class
			.getName());
	
	private int height;
	private int width;
	private String input;
	private String yAxisCaption;
	
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

	public String getyAxisCaption() {
		return yAxisCaption;
	}

	public void setyAxisCaption(String yAxisCaption) {
		this.yAxisCaption = yAxisCaption;
	}
	
	public LineChart() {
		this.height = 400;
		this.width = 400;
		this.yAxisCaption = "value";
	}
	
	public String getStyleSheet() {
		StringBuilder sb = new StringBuilder();
		sb.append("<style type=\"text/css\">\n");
		sb.append(".axis path,.axis line { fill: none; stroke: #000; shape-rendering: crispEdges; }\n");
		sb.append(".x.axis path { display: none;}\n");
		sb.append(".line {fill: none; stroke: steelblue; stroke-width: 1.5px; }\n");
		sb.append("</style>\n\n");
		return sb.toString();
	}
	
	public String getAdditionalJavaScript() {
		return "<!-- Line-Chart benötigt keine weiteren JavaScript-Bibliotheken -->";
	}
	
	public String getJavaScript() {
		StringBuilder sb = new StringBuilder();
		Random r = new Random(1234);
		String strImageTagID = "id" + String.valueOf(r.nextInt(100000));
		
		if (this.input == null || this.input.equals("")) {
			log.log(Level.WARNING, "Erzeuge ein Bubble-Chart ohne Input.");
		}
		
		// Div-Element
		sb.append("<div id=\"" + strImageTagID + "\"></div>\n");
		
		// Diagram-Script
		sb.append("<script type=\"text/javascript\">\n");
		sb.append("\tvar margin = {top: 20, right: 20, bottom: 30, left: 50}, width = "+this.getWidth()+" - margin.left - margin.right, height = "+this.getHeight()+" - margin.top - margin.bottom;\n");
		sb.append("\tvar parseDate = d3.time.format(\"%d-%b-%y\").parse;\n");
		sb.append("\tvar x = d3.time.scale().range([0, width]);\n");

		sb.append("\tvar y = d3.scale.linear().range([height, 0]);\n");

		sb.append("\tvar xAxis = d3.svg.axis().scale(x).orient(\"bottom\");\n");

		sb.append("\tvar yAxis = d3.svg.axis().scale(y).orient(\"left\");\n");

		sb.append("\tvar line = d3.svg.line().x(function(d) { return x(d.date); }).y(function(d) { return y(d.value); });\n");

		sb.append("\tvar svg = d3.select(\"#" + strImageTagID+ "\").append(\"svg\").attr(\"width\", width + margin.left + margin.right).attr(\"height\", height + margin.top + margin.bottom)\n");
		sb.append("\t\t.append(\"g\").attr(\"transform\", \"translate(\" + margin.left + \",\" + margin.top + \")\");\n");

		//sb.append("\td3.tsv(\"data.tsv\", function(error, data) {\n");
		
		// sb.append("\tvar data = d3.tsv.parse('"+this.getInput()+"');\n");
		
		sb.append("\tvar data = JSON.parse('"+this.getInput()+"');\n\n");	
		
		sb.append("\t\tdata.forEach(function(d) {d.date = parseDate(d.date); d.value = +d.value; });\n");

		sb.append("\t\tx.domain(d3.extent(data, function(d) { return d.date; }));\n");
		sb.append("\t\ty.domain(d3.extent(data, function(d) { return d.value; }));\n");

		sb.append("\t\tsvg.append(\"g\").attr(\"class\", \"x axis\").attr(\"transform\", \"translate(0,\" + height + \")\").call(xAxis);\n");

		sb.append("\t\tsvg.append(\"g\").attr(\"class\", \"y axis\").call(yAxis).append(\"text\").attr(\"transform\", \"rotate(-90)\").attr(\"y\", 6)\n");
		sb.append("\t\t\t.attr(\"dy\", \".71em\").style(\"text-anchor\", \"end\").text(\""+this.getyAxisCaption()+"\");\n");

		sb.append("\tsvg.append(\"path\").datum(data).attr(\"class\", \"line\").attr(\"d\", line);\n");
		//sb.append("\t});\n");
		sb.append("</script>");		
		
		return sb.toString();
	}
}

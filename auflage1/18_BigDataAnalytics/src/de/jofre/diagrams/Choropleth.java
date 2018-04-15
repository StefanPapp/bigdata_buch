package de.jofre.diagrams;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Generiert eine Landkarte auf Basis der gewählten Kartendaten im Verzeichnis
 * WebContent/maps.
 * 
 * Datentyp: JSON
 * 
 * Format: (Bezeichner) [ {"id":"NW", "rate":644320}, {"id":"BW",
 * "rate":333408}, ... ]
 * 
 * und Kartendaten (siehe de.json) wobei die id der Bezeichner in den Daten der
 * Abkürzung (abr) der Areale in den Kartendaten entsprechen muss.
 * 
 */
public class Choropleth {

	private static Logger log = Logger.getLogger(Choropleth.class.getName());

	private int width;
	private int height;
	private String country;
	private int min;
	private int max;
	private int mapScale;
	private boolean labelSubunits;
	private boolean labelCapitals;
	private String input;

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public int getMapScale() {
		return mapScale;
	}

	public void setMapScale(int mapScale) {
		this.mapScale = mapScale;
	}

	public boolean isLabelSubunits() {
		return labelSubunits;
	}

	public void setLabelSubunits(boolean labelSubunits) {
		this.labelSubunits = labelSubunits;
	}

	public boolean isLabelCapitals() {
		return labelCapitals;
	}

	public void setLabelCapitals(boolean labelCapitals) {
		this.labelCapitals = labelCapitals;
	}

	public Choropleth() {
		this.width = 400;
		this.height = 400;
		this.country = "DE";
		this.labelCapitals = true;
		this.labelSubunits = false;
		this.mapScale = 1500;
		this.min = -1;
		this.max = -1;
	}

	public String getAdditionalJavaScript() {
		return "<script src=\"js/topojson.v0.min.js\" charset=\"utf-8\"></script>";
	}

	public String getStyleSheet() {
		StringBuilder sb = new StringBuilder();
		sb.append("<style type=\"text/css\">\n");
		sb.append("\t.q0-9 { fill:rgb(247,251,255); }\n");
		sb.append("\t.q1-9 { fill:rgb(222,235,247); }\n");
		sb.append("\t.q2-9 { fill:rgb(198,219,239); }\n");
		sb.append("\t.q3-9 { fill:rgb(158,202,225); }\n");
		sb.append("\t.q4-9 { fill:rgb(107,174,214); }\n");
		sb.append("\t.q5-9 { fill:rgb(66,146,198); }\n");
		sb.append("\t.q6-9 { fill:rgb(33,113,181); }\n");
		sb.append("\t.q7-9 { fill:rgb(8,81,156); }\n");
		sb.append("\t.q8-9 { fill:rgb(8,48,107); }\n");

		sb.append("\t.subunit-boundary {fill: none; stroke-width:1px; stroke: #777; stroke-dasharray: 2,2; stroke-linejoin: round; }\n");

		sb.append("\t.place, .place-label {fill: #444; font-size:14px; }\n");

		sb.append("\ttext { font-family: \"Helvetica Neue\", Helvetica, Arial, sans-serif; font-size: 20px; pointer-events: none; }\n");
		sb.append("\t.subunit-label {fill: #777; fill-opacity: .5; font-size: 30px; font-weight: 200; text-anchor: middle; }\n");
		sb.append("</style>\n\n");
		return sb.toString();
	}

	public String getJavaScript() {
		StringBuilder sb = new StringBuilder();
		Random r = new Random(1234);
		String strImageTagID = "id" + String.valueOf(r.nextInt(100000));

		if (this.input == null || this.input.equals("")) {
			log.log(Level.WARNING, "Erzeuge eine Landkarte ohne Input.");
		}

		if (!this.country.equals("DE")) {
			log.log(Level.SEVERE,
					"Koordinaten für dieses Land werden nicht unterstützt!");
		}

		// Div-Element
		sb.append("<div id=\"" + strImageTagID + "\"></div>\n");

		// Diagram-Script
		sb.append("<script type=\"text/javascript\">\n");

		sb.append("var width = " + this.getWidth() + ", height = "
				+ this.getHeight() + ";\n");
		sb.append("var path = d3.geo.path();\n");
		sb.append("var svg = d3.select(\"#"
				+ strImageTagID
				+ "\").append(\"svg\").attr(\"width\", width).attr(\"height\", height);\n");
		sb.append("var rateByName = d3.map();\n\n");

		sb.append("var minValue = 0, maxValue = 0, counter = 0;\n\n");

		sb.append("var jsonData = JSON.parse('" + this.getInput() + "');\n");
		sb.append("for (var myKey in jsonData) {\n");
		sb.append("\tif (jsonData.hasOwnProperty(myKey)) {\n");
		sb.append("\t\t\trateByName.set(jsonData[myKey].id, jsonData[myKey].rate);\n");
		sb.append("\t\t\tif(counter == 0) { minValue = jsonData[myKey].rate; maxValue = jsonData[myKey].rate; counter = 1; } else {\n");
		sb.append("\t\t\t\tif(jsonData[myKey].rate < minValue) { minValue = jsonData[myKey].rate; }\n");
		sb.append("\t\t\t\tif(jsonData[myKey].rate > maxValue) { maxValue = jsonData[myKey].rate; }\n\t\t\t}\n");
		sb.append("\t}\n}\n\n");

		if (this.getMin() != -1) {
			if (this.getMax() != -1) {
				sb.append("var quantize = d3.scale.quantize().domain(["
						+ this.getMin()
						+ ", "
						+ this.getMax()
						+ "]).range(d3.range(9).map(function(i) { return \"q\" + i + \"-9\"; }));\n\n");

			} else {
				sb.append("var quantize = d3.scale.quantize().domain(["
						+ this.getMin()
						+ ", maxValue]).range(d3.range(9).map(function(i) { return \"q\" + i + \"-9\"; }));\n\n");
			}
		}
		if ((this.getMin() == -1) && (this.getMax() == -1)) {
			sb.append("var quantize = d3.scale.quantize().domain([minValue, maxValue]).range(d3.range(9).map(function(i) { return \"q\" + i + \"-9\"; }));\n\n");
		}

		if (country.equals("DE")) {
			sb.append("d3.json(\"maps/de.json\", showData);\n\n");
		}

		sb.append("function showData(error, country) {\n");
		sb.append("\tvar subunits = topojson.object(country, country.objects.subunits);\n");
		sb.append("\tvar projection = d3.geo.mercator().center([10.5, 51.35]).scale("
				+ mapScale + ").translate([width / 2, height / 2]);\n");
		sb.append("\tvar path = d3.geo.path().projection(projection).pointRadius(0);\n");
		sb.append("\tsvg.append(\"path\").datum(subunits).attr(\"d\", path);\n");
		sb.append("\tsvg.selectAll(\".subunit\").data(topojson.object(country, country.objects.subunits).geometries).enter().append(\"path\")\n");
		sb.append("\t\t.attr(\"class\", function(d) {\n");
		sb.append("\t\treturn quantize( rateByName.get(d.properties.abr));\n\t}).attr(\"d\", path);\n");
		sb.append("\tsvg.append(\"path\").datum(topojson.mesh(country, country.objects.subunits, function(a,b) { if (a!==b){var ret = a;}return ret;}))\n");
		sb.append("\t\t.attr(\"d\", path).attr(\"class\", \"subunit-boundary\");\n\n");

		sb.append("\tsvg.append(\"path\").datum(topojson.object(country, country.objects.places)).attr(\"d\", path).attr(\"class\", \"place\");\n\n");

		if (this.labelCapitals) {
			sb.append("\tsvg.selectAll(\".place-label\").data(topojson.object(country, country.objects.places).geometries).enter().append(\"text\")\n");
			sb.append("\t\t.attr(\"class\", \"place-label\").attr(\"transform\", function(d) { return \"translate(\" + projection(d.coordinates) + \")\"; })\n");
			sb.append("\t\t.attr(\"dy\", \".35em\").text(function(d) { return d.properties.name; })\n");
			sb.append("\t\t.attr(\"x\", function(d) { return d.coordinates[0] > -1 ? 6 : -6; })\n");
			sb.append("\t\t.style(\"text-anchor\", function(d) { return d.coordinates[0] > -1 ? \"start\" : \"end\"; });\n\n");
		}

		if (this.labelSubunits) {
			sb.append("\tsvg.selectAll(\".subunit-label\").data(topojson.object(country, country.objects.subunits).geometries).enter().append(\"text\")\n");
			sb.append("\t\t.attr(\"class\", function(d) { return \"subunit-label \" + d.properties.name; })\n");
			sb.append("\t\t.attr(\"transform\", function(d) { return \"translate(\" + path.centroid(d) + \")\"; })\n");
			sb.append("\t\t.attr(\"dy\", function(d){\n");
			sb.append("\t\t\tif(d.properties.name===\"Sachsen\"||d.properties.name===\"Thüringen\"||d.properties.name===\"Sachsen-Anhalt\"||d.properties.name===\"Rheinland-Pfalz\")\n");
			sb.append("\t\t\t{\n\t\t\t\treturn \".9em\";\n\t\t\t}\n");
			sb.append("\t\t\telse if(d.properties.name===\"Brandenburg\"||d.properties.name===\"Hamburg\")\n");
			sb.append("\t\t\t{\n\t\t\t\treturn \"1.5em\";\n\t\t\t}\n");
			sb.append("\t\t\telse if(d.properties.name===\"Berlin\"||d.properties.name===\"Bremen\")\n");
			sb.append("\t\t\t{\n\t\t\t\treturn \"-1em\";}else{return \".35em\";}\n");
			sb.append("\t}).text(function(d) { return d.properties.name; });\n");
		}

		sb.append("}\n\n");

		sb.append("</script>");

		return sb.toString();
	}

}

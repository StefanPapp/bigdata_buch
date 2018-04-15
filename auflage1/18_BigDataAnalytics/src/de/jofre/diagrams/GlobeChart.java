package de.jofre.diagrams;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 3D-Globus, der Daten auf bestimmten Längen- und Breitengraden visualisiert.
 * 
 * Datentyp: JSON
 * 
 * Format: [[Jahr, [Längengrad, Breitengrad, Wert, Längengrad, ...]], [Jahr2,
 * [Längengrad, ...]], ...]
 * 
 * z.B.: [["1990",[6,159,0.001,30,99,0.002, ...]
 */

public class GlobeChart {

	private static Logger log = Logger.getLogger(GlobeChart.class.getName());

	private int width;
	private int height;
	private List<Integer> years;
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

	public List<Integer> getYears() {
		return years;
	}

	public void setYears(List<Integer> years) {
		this.years = years;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public GlobeChart() {
		this.width = 400;
		this.height = 400;
		this.years = new ArrayList<Integer>();
	}

	public String getAdditionalJavaScript() {
		StringBuilder sb = new StringBuilder();
		sb.append("<script src=\"js/ThreeWebGL.js\" charset=\"utf-8\"></script>\n");
		sb.append("<script src=\"js/ThreeExtras.js\" charset=\"utf-8\"></script>\n");
		sb.append("<script src=\"js/Detector.js\" charset=\"utf-8\"></script>\n");
		sb.append("<script src=\"js/Tween.js\" charset=\"utf-8\"></script>\n");
		sb.append("<script src=\"js/globe.js\" charset=\"utf-8\"></script>\n");
		return sb.toString();
	}

	public String getStyleSheet() {
		StringBuilder sb = new StringBuilder();
		sb.append("<style type=\"text/css\">\n");
		sb.append("\t.globe {margin: 0; padding: 0; background: #000000 url(globe_loading.gif) center center no-repeat;\n");
		sb.append("\tcolor: #ffffff; font-family: sans-serif; font-size: 13px; line-height: 20px;}\n");
		sb.append("\t#currentInfo {background-color: rgba(0,0,0,1); border-top: 1px solid rgba(255,255,255,0.4); height: 120px;}\n");
		sb.append("\t.year {font: 16px Georgia; line-height: 26px; height: 30px; text-align: center;\n");
		sb.append("\tfloat: left; width: 90px; color: rgba(255, 255, 255, 0.4); cursor: pointer; -webkit-transition: all 0.1s ease-out; }\n");
		sb.append("\t.year:hover, .year.active {font-size: 23px; color: #fff; }\n");
		sb.append("</style>\n\n");
		return sb.toString();
	}

	public List<Integer> getYearsFromInput() {
		List<Integer> years = new ArrayList<Integer>();
		if ((this.input != null) && (!this.input.trim().equals(""))) {
			Matcher m = Pattern.compile("\"[0-9]{4}\"").matcher(input);
			while (m.find()) {
				Integer newYear = Integer.parseInt(m.group().substring(1, m.group().length()-1));
				years.add(newYear);
			}
		}
		return years;
	}

	public String getJavaScript() {
		StringBuilder sb = new StringBuilder();
		Random r = new Random(1234);
		String strImageTagID = "id" + String.valueOf(r.nextInt(100000));

		if (this.input == null || this.input.equals("")) {
			log.log(Level.WARNING, "Erzeuge Globe-Chart ohne Input.");
		}

		// Erzeuge eine Liste aus Jahren
		StringBuilder strYears = new StringBuilder();
		for (int i = 0; i < this.years.size(); i++) {
			strYears.append("'" + this.years.get(i) + "'");
			if (i < this.years.size() - 1)
				strYears.append(",");
		}

		// Diagram-Script
		sb.append("<table style=\"width:" + this.width + "px;\"><tr><td>");
		sb.append("<div id=\"currentInfo\">\n");

		for (int i = 0; i < this.years.size(); i++) {
			sb.append("<span id=\"year" + this.years.get(i)
					+ "\" class=\"year\">" + this.years.get(i) + "</span>\n");
		}
		sb.append("</div>\n\n");
		sb.append("</td></tr><tr><td>");

		// Set image Tag
		sb.append("<div id=\"" + strImageTagID
				+ "\" class=\"globe\" style=\"width:" + this.width
				+ "px; height:" + (this.height - 120) + "px;\"></div>\n");

		// Draw diagram
		sb.append("<script type=\"text/javascript\">\n");

		// Helper function
		sb.append("\tfunction FireEvent( ElementId, EventName )\n\t{\n");
		sb.append("\t\tif( document.getElementById(ElementId) != null )\n\t\t{\n");
		sb.append("\t\t\tif( document.getElementById( ElementId ).fireEvent )\n\t\t\t{\n");
		sb.append("\t\t\t\tdocument.getElementById( ElementId ).fireEvent( 'on' + EventName );\n\t\t\t}\n");
		sb.append("\t\t\telse\n\t\t\t{\n");
		sb.append("\t\t\t\tvar evObj = document.createEvent( 'Events' );\n");
		sb.append("\t\t\t\tevObj.initEvent( EventName, true, false );\n");
		sb.append("\t\t\t\tdocument.getElementById( ElementId ).dispatchEvent( evObj );\n");
		sb.append("\t\t\t}\n\t\t}\n\t}\n\n");

		// Check if WebGL is supported
		sb.append("\tif(!Detector.webgl){\n\t\tDetector.addGetWebGLMessage();\n\t} else {\n");

		sb.append("\t\tvar years = [" + strYears + "];\n");
		sb.append("\t\tvar container = document.getElementById('"
				+ strImageTagID + "');\n");
		sb.append("\t\tvar globe = new DAT.Globe(container);\n\t\tvar i, j, tweens = [];\n");

		sb.append("\t\tvar settime = function(globe, t) {\n\t\t\treturn function() {\n");
		sb.append("\t\t\t\tnew TWEEN.Tween(globe).to({time: t/years.length},500).easing(TWEEN.Easing.Cubic.EaseOut).start();\n");
		sb.append("\t\t\t\tvar y = document.getElementById('year'+years[t]);\n");
		sb.append("\t\t\t\tif (y.getAttribute('class') === 'year active') { return; }\n");
		sb.append("\t\t\t\tvar yy = document.getElementsByClassName('year');\n");
		sb.append("\t\t\t\tfor(i=0; i<yy.length; i++) {\n\t\t\t\t\tyy[i].setAttribute('class','year');\n\t\t\t\t}\n");
		sb.append("\t\t\ty.setAttribute('class', 'year active');\n\t\t};\n\t};\n\n");

		sb.append("\tfor(var i = 0; i<years.length; i++) {\n");
		sb.append("\t\tvar y = document.getElementById('year'+years[i]);\n");
		sb.append("\t\ty.addEventListener('mouseover', settime(globe,i), false);\n");
		sb.append("\t}\n\n");

		sb.append("\tTWEEN.start();\n\n");

		sb.append("\tvar data = JSON.parse('" + input + "');\n");
		sb.append("\twindow.data = data;\n");
		sb.append("\tvar maxSize = 0;\n\tvar minSize = 10000000;\n");
		sb.append("\tfor (i = 0; i < data.length; i ++) {\n\t\tvar d2 = data[i][1];\n");
		sb.append("\t\tfor(j=2; j<(d2).length; j +=3) {\n\t\t\tif (d2[j] > maxSize) {\n");
		sb.append("\t\t\t\tmaxSize = d2[j];\n");
		sb.append("\t\t\t}\n\t\t\tif (d2[j] < minSize) {\n\t\t\t\tminSize = d2[j];\n\t\t\t}\n\t\t}\n\t}\n");

		sb.append("\tfor (i = 0; i < data.length; i ++) {\n\t\tvar d2 = data[i][1];\n");
		sb.append("\t\tfor(j=2; j<(d2).length; j +=3) {\n\t\t\td2[j] = d2[j] / (maxSize - minSize) / 100;\n");
		sb.append("\t\t}\n\t}\n\n");

		sb.append("\tfor (i=0;i<data.length;i++) {\n");
		sb.append("\t\tglobe.addData(data[i][1], {format: 'magnitude', name: data[i][0], animated: true});\n");
		sb.append("\t}\n\tglobe.createPoints();\n\tFireEvent('year'+years[0], 'mouseover');\n\tglobe.animate();\n\t}\n\n");

		sb.append("</script>");
		sb.append("</td></tr></table>");

		return sb.toString();
	}
}

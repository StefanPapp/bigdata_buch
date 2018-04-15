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
 * Generiert eine tagesgenaue Kalenderansicht wobei die Differenz zwischen Min
 * und Max farblich dargestellt wird.
 * 
 * Datentyp: JSON
 * 
 * Format: [{"Date":"2010-10-01","Min":"10789.72","Max":"10829.68"},
 * {"Date":"2010-09-30","Min":"10835.96","Max":"10788.05"}, ...];
 */
public class CalendarChart {

	private static Logger log = Logger.getLogger(CalendarChart.class.getName());

	private int width;
	private int height;
	private int cellsize;
	private int startyear;
	private int endyear;
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

	public int getCellsize() {
		return cellsize;
	}

	public void setCellsize(int cellsize) {
		this.cellsize = cellsize;
	}

	public int getStartyear() {
		return startyear;
	}

	public void setStartyear(int startyear) {
		this.startyear = startyear;
	}

	public int getEndyear() {
		return endyear;
	}

	public void setEndyear(int endyear) {
		this.endyear = endyear;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public CalendarChart() {
		this.width = 960;
		this.height = 200;
		this.cellsize = 17;
		this.startyear = 2010;
		this.endyear = 2013;
	}

	public String getAdditionalJavaScript() {
		return "<!-- Calendar-Chart benötigt keine weiteren JavaScript-Bibliotheken -->";
	}

	public String getStyleSheet() {
		StringBuilder sb = new StringBuilder();

		sb.append("<style type=\"text/css\">\n");
		sb.append("\t.day {fill: #fff; stroke: #ccc; }\n");
		sb.append("\t.month {fill: none; stroke: #000; stroke-width: 2px; }\n");

		sb.append("\t.RdYlGn .q0-11{fill:rgb(165,0,38)}\n");
		sb.append("\t.RdYlGn .q1-11{fill:rgb(215,48,39)}\n");
		sb.append("\t.RdYlGn .q2-11{fill:rgb(244,109,67)}\n");
		sb.append("\t.RdYlGn .q3-11{fill:rgb(253,174,97)}\n");
		sb.append("\t.RdYlGn .q4-11{fill:rgb(254,224,139)}\n");
		sb.append("\t.RdYlGn .q5-11{fill:rgb(255,255,191)}\n");
		sb.append("\t.RdYlGn .q6-11{fill:rgb(217,239,139)}\n");
		sb.append("\t.RdYlGn .q7-11{fill:rgb(166,217,106)}\n");
		sb.append("\t.RdYlGn .q8-11{fill:rgb(102,189,99)}\n");
		sb.append("\t.RdYlGn .q9-11{fill:rgb(26,152,80)}\n");
		sb.append("\t.RdYlGn .q10-11{fill:rgb(0,104,55)}\n");
		sb.append("</style>\n\n");

		return sb.toString();
	}

	/**
	 * Ermitteln des höchsten Jahres im Eingabedatensatz
	 * 
	 * @return
	 */
	public int getMaxYear() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date maxDate = new Date();
		try {
			maxDate = sdf.parse("0001-01-01");
			if (this.getInput() != null) {

				List<String> matches = new ArrayList<String>();
				Matcher m = Pattern.compile("[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])")
						.matcher(input);
				while (m.find()) {
					matches.add(m.group());
						Date d = sdf.parse(m.group());
						if (d.after(maxDate)) {
							maxDate = d;
						}
				}
			} else {
				log.log(Level.WARNING, "Maximaldatum konnte nicht ermittelt werden, da Inputdaten noch nicht gesetzt waren.");
			}
		} catch (ParseException e) {
			log.log(Level.SEVERE, "Fehler beim Ermitteln des Maximaldatums.");
			e.printStackTrace();
		}

		Calendar calendar = Calendar.getInstance();  
        calendar.setTime(maxDate);  
		return calendar.get(Calendar.YEAR);
	}
	
	/**
	 * Ermitteln des niedrigsten Jahres im Eingabedatensatz
	 * 
	 * @return
	 */
	public int getMinYear() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date minDate = new Date();
		try {
			minDate = sdf.parse("9999-01-01");
			if (this.getInput() != null) {

				List<String> matches = new ArrayList<String>();
				Matcher m = Pattern.compile("[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])")
						.matcher(input);
				while (m.find()) {
					matches.add(m.group());
						Date d = sdf.parse(m.group());
						if (d.before(minDate)) {
							minDate = d;
						}
				}
			} else {
				log.log(Level.WARNING, "Minimaldatum konnte nicht ermittelt werden, da Inputdaten noch nicht gesetzt waren.");
			}
		} catch (ParseException e) {
			log.log(Level.SEVERE, "Fehler beim Ermitteln des Minimaldatum.");
			e.printStackTrace();
		}

		Calendar calendar = Calendar.getInstance();  
        calendar.setTime(minDate);  
		return calendar.get(Calendar.YEAR);
	}	

	public String getJavaScript() {
		StringBuilder sb = new StringBuilder();
		Random r = new Random(1234);
		String strImageTagID = "id" + String.valueOf(r.nextInt(100000));

		if (this.input == null || this.input.equals("")) {
			log.log(Level.WARNING, "Erzeuge ein Calendar-Chart ohne Input.");
		}

		// Div-Element
		sb.append("<div id=\"" + strImageTagID + "\"></div>\n");

		// Diagram-Script
		sb.append("<script type=\"text/javascript\">\n");
		sb.append("var width = " + width + ",\nheight = " + height
				+ ",\ncellSize = " + cellsize + ";\n\n");

		sb.append("var day = d3.time.format(\"%w\"),\nweek = d3.time.format(\"%U\"),\npercent = d3.format(\".1%\"),\nformat = d3.time.format(\"%Y-%m-%d\");\n\n");
		sb.append("var color = d3.scale.quantize().domain([-.05, .05]).range(d3.range(11).map(function(d) { return \"q\" + d + \"-11\"; }));\n\n");
		sb.append("var svg = d3.select(\"#" + strImageTagID
				+ "\").selectAll(\"svg\").data(d3.range(" + startyear + ", "
				+ endyear + ")).enter().append(\"svg\")");
		sb.append(".attr(\"width\", width).attr(\"height\", height).attr(\"class\", \"RdYlGn\").append(\"g\")");
		sb.append(".attr(\"transform\", \"translate(\" + ((width - cellSize * 53) / 2) + \",\" + (height - cellSize * 7 - 1) + \")\");\n\n");

		sb.append("svg.append(\"text\").attr(\"transform\", \"translate(-6,\" + cellSize * 3.5 + \")rotate(-90)\")");
		sb.append(".style(\"text-anchor\", \"middle\").text(function(d) { return d; });\n\n");

		sb.append("var rect = svg.selectAll(\".day\").data(function(d) { return d3.time.days(new Date(d, 0, 1), new Date(d + 1, 0, 1)); })");
		sb.append(".enter().append(\"rect\").attr(\"class\", \"day\").attr(\"width\", cellSize).attr(\"height\", cellSize)");
		sb.append(".attr(\"x\", function(d) { return week(d) * cellSize; }).attr(\"y\", function(d) { return day(d) * cellSize; }).datum(format);\n\n");

		sb.append("rect.append(\"title\").text(function(d) { return d; });\n\n");

		sb.append("svg.selectAll(\".month\").data(function(d) { return d3.time.months(new Date(d, 0, 1), new Date(d + 1, 0, 1)); })");
		sb.append(".enter().append(\"path\").attr(\"class\", \"month\").attr(\"d\", monthPath);\n\n");

		sb.append("var csv =" + input + ";\n\n");

		sb.append("var data = d3.nest().key(function(d) { return d.Date; }).rollup(function(d) { return (d[0].Max - d[0].Min) / d[0].Min; }).map(csv);\n\n");
		sb.append("rect.filter(function(d) { return d in data; }).attr(\"class\", function(d) { return \"day \" + color(data[d]); })");
		sb.append(".select(\"title\").text(function(d) { return d + \": \" + percent(data[d]); });\n\n");

		sb.append("function monthPath(t0) { var t1 = new Date(t0.getFullYear(), t0.getMonth() + 1, 0), d0 = +day(t0), w0 = +week(t0),");
		sb.append("d1 = +day(t1), w1 = +week(t1); return \"M\" + (w0 + 1) * cellSize + \",\" + d0 * cellSize + \"H\" + w0 * cellSize + \"V\" + 7 * cellSize");
		sb.append("+ \"H\" + w1 * cellSize + \"V\" + (d1 + 1) * cellSize + \"H\" + (w1 + 1) * cellSize + \"V\" + 0 + \"H\" + (w0 + 1) * cellSize + \"Z\"; }\n\n");
		sb.append("</script>");

		return sb.toString();
	}

}

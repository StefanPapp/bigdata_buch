package de.jofre.diagrams;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * m-n-Beziehungsdiagramm in Kreisform. Eingehende Links werden grün
 * dargestellt, ausgehende Relationen rot.
 * 
 * Datentyp: JSON
 * 
 * Format:
 * [
 * {"name":"Uta_Chambers","rellinks":[]},
 * {"name":"Robin_Berger","rellinks":["Uta_Chambers"]},
 * ...
 * ]
 */
public class FlareChart {
	private static Logger log = Logger.getLogger(FlareChart.class
			.getName());
	
	public int width;
	public int height;
	public String input;
	
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
	
	public FlareChart() {
		this.width = 400;
		this.height = 400;
	}
	
	public String getAdditionalJavaScript() {
		return "<script src=\"js/flarepackages.js\" charset=\"utf-8\"></script>";
	}	
	
	public String getStyleSheet() {
		StringBuilder sb = new StringBuilder();
		sb.append("<style type=\"text/css\">\n");
		sb.append("\tpath.arc {cursor: move; fill: #fff; }\n");
		sb.append("\t.node {font-size: 10px; }\n");
		sb.append("\t.node:hover {fill: #1f77b4; }\n");
		sb.append("\t.link {fill: none; stroke: #1f77b4; stroke-opacity: .4; pointer-events: none; }\n");
		sb.append("\t.link.source, .link.target { stroke-opacity: 1; stroke-width: 2px; }\n");
		sb.append("\t.node.target {fill: #d62728 !important; }\n");
		sb.append("\t.link.source {stroke: #d62728; }\n");
		sb.append("\t.node.source {fill: #2ca02c; }\n");
		sb.append("\t.link.target {stroke: #2ca02c; }\n");
		sb.append("</style>\n\n");
		return sb.toString();
	}	
	
	public String getJavaScript() {
		StringBuilder sb = new StringBuilder();
		Random r = new Random(1234);
		String strImageTagID = "id" + String.valueOf(r.nextInt(100000));
		
		if (this.input == null || this.input.equals("")) {
			log.log(Level.WARNING, "Erzeuge Flare-Chart ohne Input.");
		}
		
		// Div-Element
		sb.append("<div id=\"" + strImageTagID + "\"></div>\n");
		
		// Diagram-Script
		sb.append("<script type=\"text/javascript\">\n");
		
		sb.append("var w = "+this.width+", h = "+this.height+", rx = w / 2, ry = h / 2, m0, rotate = 0; var splines = [];\n\n");
		
		sb.append("var cluster = d3.layout.cluster().size([360, ry - 120]).sort(function(a, b) { return d3.ascending(a.key, b.key); });\n\n");
		
		sb.append("var bundle = d3.layout.bundle();\n\n");
		sb.append("var line = d3.svg.line.radial().interpolate(\"bundle\").tension(.85).radius(function(d) { return d.y; }).angle(function(d) { return d.x / 180 * Math.PI; });\n\n");
		
		sb.append("var div = d3.select(\"#"+strImageTagID+"\").insert(\"div\", \"h2\")\n");
		sb.append(".style(\"width\", w + \"px\").style(\"height\", w + \"px\")\n");		
		sb.append(".style(\"-webkit-backface-visibility\", \"hidden\");\n\n"); // .style(\"position\", \"absolute\")
		sb.append("var svg = div.append(\"svg:svg\").attr(\"width\", w).attr(\"height\", w).append(\"svg:g\").attr(\"transform\", \"translate(\" + rx + \",\" + ry + \")\");\n\n");
		
		sb.append("svg.append(\"svg:path\").attr(\"class\", \"arc\").attr(\"d\", d3.svg.arc().outerRadius(ry - 120).innerRadius(0).startAngle(0).endAngle(2 * Math.PI))\n");
		sb.append(".on(\"mousedown\", mousedown);\n\n");
		
		sb.append("var classes = JSON.parse('"+input+"');\n\n");
		
		// Drawing diagram
		sb.append("var nodes = cluster.nodes(packages.root(classes)), links = packages.rellinks(nodes), splines = bundle(links);\n\n");
		sb.append("var path = svg.selectAll(\"path.link\").data(links).enter().append(\"svg:path\")\n");
		sb.append(".attr(\"class\", function(d) { return \"link source-\" + d.source.key + \" target-\" + d.target.key; })\n");
		sb.append(".attr(\"d\", function(d, i) { return line(splines[i]); });\n\n");
		
		sb.append("svg.selectAll(\"g.node\").data(nodes.filter(function(n) { return !n.children; }))\n");
		sb.append(".enter().append(\"svg:g\").attr(\"class\", \"node\").attr(\"id\", function(d) { return \"node-\" + d.key; })\n");
		sb.append(".attr(\"transform\", function(d) { return \"rotate(\" + (d.x - 90) + \")translate(\" + d.y + \")\"; })\n");
		sb.append(".append(\"svg:text\").attr(\"dx\", function(d) { return d.x < 180 ? 8 : -8; }).attr(\"dy\", \".31em\")\n");
		sb.append(".attr(\"text-anchor\", function(d) { return d.x < 180 ? \"start\" : \"end\"; })\n");
		sb.append(".attr(\"transform\", function(d) { return d.x < 180 ? null : \"rotate(180)\"; })\n");
		sb.append(".text(function(d) { return d.key; }).on(\"mouseover\", mouseover).on(\"mouseout\", mouseout);\n\n");

		sb.append("d3.select(\"input[type=range]\").on(\"change\", function() { line.tension(this.value / 100);\n");
		sb.append("path.attr(\"d\", function(d, i) { return line(splines[i]); }); \n});\n\n");
		// Finished

		sb.append("d3.select(window).on(\"mousemove\", mousemove).on(\"mouseup\", mouseup);\n\n");
		sb.append("function mouse(e) { return [e.pageX - rx, e.pageY - ry]; \n }\n");
		
		sb.append("function mousedown() { m0 = mouse(d3.event); d3.event.preventDefault();\n\n }\n\n");
		
		sb.append("function mousemove() { if (m0) { var m1 = mouse(d3.event),\n");
		sb.append("dm = Math.atan2(cross(m0, m1), dot(m0, m1)) * 180 / Math.PI;\n\n");
		sb.append("div.style(\"-webkit-transform\", \"translateY(\" + (ry - rx) + \"px)rotateZ(\" + dm + \"deg)translateY(\" + (rx - ry) + \"px)\"); \n\n }\n\n }\n\n");

		sb.append("function mouseup() { if (m0) { var m1 = mouse(d3.event), dm = Math.atan2(cross(m0, m1), dot(m0, m1)) * 180 / Math.PI;\n\n");
		sb.append(" rotate += dm; if (rotate > 360) rotate -= 360; else if (rotate < 0) rotate += 360; m0 = null;\n\n");
		
		sb.append("div.style(\"-webkit-transform\"); svg.attr(\"transform\", \"translate(\" + rx + \",\" + ry + \")rotate(\" + rotate + \")\")\n");
		sb.append(".selectAll(\"g.node text\").attr(\"dx\", function(d) { return (d.x + rotate) % 360 < 180 ? 8 : -8; \n\n })\n\n");
		sb.append(".attr(\"text-anchor\", function(d) { return (d.x + rotate) % 360 < 180 ? \"start\" : \"end\"; \n\n })");
		sb.append(".attr(\"transform\", function(d) { return (d.x + rotate) % 360 < 180 ? null : \"rotate(180)\"; \n\n }); \n\n } }");

		sb.append("function mouseover(d) { svg.selectAll(\"path.link.target-\" + d.key).classed(\"target\", true)\n");
		sb.append(".each(updateNodes(\"source\", true));\n\n");
		
		sb.append("svg.selectAll(\"path.link.source-\" + d.key).classed(\"source\", true).each(updateNodes(\"target\", true)); \n\n }\n\n");
		sb.append("function mouseout(d) { svg.selectAll(\"path.link.source-\" + d.key).classed(\"source\", false).each(updateNodes(\"target\", false));\n\n");
		
		sb.append("svg.selectAll(\"path.link.target-\" + d.key).classed(\"target\", false).each(updateNodes(\"source\", false)); \n\n }\n\n");
		
		sb.append("function updateNodes(name, value) { return function(d) { if (value) this.parentNode.appendChild(this); svg.select(\"#node-\" + d[name].key).classed(name, value); \n\n  }; \n\n }\n\n");
		
		sb.append("function cross(a, b) { return a[0] * b[1] - a[1] * b[0]; }\n\n");
		
		sb.append("function dot(a, b) { return a[0] * b[0] + a[1] * b[1]; }\n\n");
		
		sb.append("</script>");
		
		return sb.toString();
	}	
}

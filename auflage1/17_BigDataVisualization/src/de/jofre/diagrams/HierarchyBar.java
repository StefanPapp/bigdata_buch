package de.jofre.diagrams;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hierarchisches, interaktives Balkendiagramm.
 * 
 * Datentyp: JSON
 * 
 * Format:
 * {
 * "name": "Instrumente",
 * "children": [
 *  {
 *   "name": "Blasinstrumente",
 *   "children": [
 *    {
 *     "name": "Blasinstrumente 1",
 *     "children": [
 *      {"name": "Tuba", "size": 3938},
 *      {"name": "Posaune", "size": 3812},
 *      {"name": "Oboe", "size": 6714},
 *      {"name": "Trompete", "size": 5012}
 *     ]
 *    }, ...
 *    ]
 *  }
 *  ]
 * }
 */
public class HierarchyBar {
	private static Logger log = Logger.getLogger(HierarchyBar.class
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
	
	public HierarchyBar() {
		this.width = 800;
		this.height = 400;
	}
	
	public String getAdditionalJavaScript() {
		return "<!-- Hierarchy-Bar benötigt keine weiteren JavaScript-Bibliotheken -->";
	}	
	
	public String getStyleSheet() {
		StringBuilder sb = new StringBuilder();
		sb.append("<style type=\"text/css\">\n");
		sb.append("svg {font-size: 14px;}");
		sb.append("rect.background {fill: none; pointer-events: all; }");
		sb.append(".axis {shape-rendering: crispEdges; }");
		sb.append(".axis path, .axis line {fill: none; stroke: #000; stroke-width: .5px; }");
		sb.append("</style>\n\n");
		return sb.toString();
	}	
	
	public String getJavaScript() {
		StringBuilder sb = new StringBuilder();
		Random r = new Random(1234);
		String strImageTagID = "id" + String.valueOf(r.nextInt(100000));
		
		if (this.input == null || this.input.equals("")) {
			log.log(Level.WARNING, "Erzeuge Hierarchy-Bar ohne Input.");
		}
		
		// Div-Element
		sb.append("<div id=\"" + strImageTagID + "\"></div>\n");
		
		// Diagram-Script
		sb.append("<script type=\"text/javascript\">\n");
		
		sb.append("var m = [80, 160, 0, 160], w = "+this.width+" - m[1] - m[3], h = "+this.height+" - m[0] - m[2], x = d3.scale.linear().range([0, w]), y = 25, z = d3.scale.ordinal().range([\"steelblue\", \"#aaa\"]);\n\n");

		sb.append("var hierarchy = d3.layout.partition().value(function(d) { return d.size; });\n\n");
		sb.append("var xAxis = d3.svg.axis().scale(x).orient(\"top\");\n\n");
		sb.append("var svg = d3.select(\"#"+strImageTagID+"\").append(\"svg:svg\").attr(\"width\", w + m[1] + m[3]).attr(\"height\", h + m[0] + m[2])\n");
		sb.append(".append(\"svg:g\").attr(\"transform\", \"translate(\" + m[3] + \",\" + m[0] + \")\");\n\n");

		sb.append("svg.append(\"svg:rect\").attr(\"class\", \"background\").attr(\"width\", w).attr(\"height\", h).on(\"click\", up);\n\n");
		
		sb.append("svg.append(\"svg:g\").attr(\"class\", \"x axis\");\n\n");
		
		sb.append("svg.append(\"svg:g\").attr(\"class\", \"y axis\").append(\"svg:line\").attr(\"y1\", \"100%\");\n\n");

		// Write data
		sb.append("var root = JSON.parse('"+this.input+"');\n\n");
		sb.append("hierarchy.nodes(root);\n\n");
		sb.append("x.domain([0, root.value]).nice();\n\n");
		sb.append("down(root, 0);\n\n");
		// Finished

		sb.append("function down(d, i) {\n");
		sb.append("if (!d.children || this.__transition__) return;\n");
		sb.append("var duration = d3.event && d3.event.altKey ? 7500 : 750, delay = duration / d.children.length;\n");
		
		sb.append("var exit = svg.selectAll(\".enter\").attr(\"class\", \"exit\");\n\n");

		sb.append("exit.selectAll(\"rect\").filter(function(p) { return p === d; }).style(\"fill-opacity\", 1e-6);\n\n");
		
		sb.append("var enter = bar(d).attr(\"transform\", stack(i)).style(\"opacity\", 1);\n");

		sb.append("enter.select(\"text\").style(\"fill-opacity\", 1e-6);\n");
		sb.append("enter.select(\"rect\").style(\"fill\", z(true));\n");
		sb.append("x.domain([0, d3.max(d.children, function(d) { return d.value; })]).nice();\n");
		
		sb.append("svg.selectAll(\".x.axis\").transition().duration(duration).call(xAxis);\n");

		sb.append("var enterTransition = enter.transition().duration(duration)");
		sb.append(".delay(function(d, i) { return i * delay; }).attr(\"transform\", function(d, i) { return \"translate(0,\" + y * i * 1.2 + \")\"; });\n");

		sb.append("enterTransition.select(\"text\").style(\"fill-opacity\", 1);\n\n");
		
		sb.append("enterTransition.select(\"rect\").attr(\"width\", function(d) { return x(d.value); }).style(\"fill\", function(d) { return z(!!d.children); });\n\n");
		
		sb.append("var exitTransition = exit.transition().duration(duration).style(\"opacity\", 1e-6).remove();\n\n");
		
		sb.append("exitTransition.selectAll(\"rect\").attr(\"width\", function(d) { return x(d.value); });\n\n");
		
		sb.append("svg.select(\".background\").data([d]).transition().duration(duration * 2); d.index = i; }\n\n");

		sb.append("function up(d) { if (!d.parent || this.__transition__) return; var duration = d3.event && d3.event.altKey ? 7500 : 750,\n");
		sb.append("delay = duration / d.children.length;\n\n");
		
		sb.append("var exit = svg.selectAll(\".enter\").attr(\"class\", \"exit\");\n");
		
		sb.append("var enter = bar(d.parent).attr(\"transform\", function(d, i) { return \"translate(0,\" + y * i * 1.2 + \")\"; })\n");
		sb.append(".style(\"opacity\", 1e-6);\n");
		
		sb.append("enter.select(\"rect\").style(\"fill\", function(d) { return z(!!d.children); })\n");
		sb.append(".filter(function(p) { return p === d; }).style(\"fill-opacity\", 1e-6);\n\n");

		sb.append("x.domain([0, d3.max(d.parent.children, function(d) { return d.value; })]).nice();\n\n");
		
		
		sb.append("svg.selectAll(\".x.axis\").transition().duration(duration * 2).call(xAxis);\n\n");
		
		sb.append("var enterTransition = enter.transition().duration(duration * 2).style(\"opacity\", 1);\n\n");
		
		sb.append("enterTransition.select(\"rect\").attr(\"width\", function(d) { return x(d.value); })\n");
		sb.append(".each(\"end\", function(p) { if (p === d) d3.select(this).style(\"fill-opacity\"); });\n\n");

		
		sb.append("var exitTransition = exit.selectAll(\"g\").transition().duration(duration)\n");
		sb.append(".delay(function(d, i) { return i * delay; }).attr(\"transform\", stack(d.index));\n\n");
		
		sb.append("exitTransition.select(\"text\").style(\"fill-opacity\", 1e-6);\n\n");
		
		sb.append("exitTransition.select(\"rect\").attr(\"width\", function(d) { return x(d.value); }).style(\"fill\", z(true));\n\n");

		sb.append("exit.transition().duration(duration * 2).remove();\n\n");
		
		sb.append("svg.select(\".background\").data([d.parent]).transition().duration(duration * 2);\n }\n\n");
		
		sb.append("function bar(d) { var bar = svg.insert(\"svg:g\", \".y.axis\").attr(\"class\", \"enter\").attr(\"transform\", \"translate(0,5)\")\n");
		sb.append(".selectAll(\"g\").data(d.children).enter().append(\"svg:g\").style(\"cursor\", function(d) { return !d.children ? null : \"pointer\"; }).on(\"click\", down);\n\n");

		sb.append("bar.append(\"svg:text\").attr(\"x\", -6).attr(\"y\", y / 2).attr(\"dy\", \".35em\")\n");
		sb.append(".attr(\"text-anchor\", \"end\").text(function(d) { return d.name; });\n\n");
		
		sb.append("bar.append(\"svg:rect\").attr(\"width\", function(d) { return x(d.value); }).attr(\"height\", y);\n\n");
		sb.append("return bar;\n	}\n\n");
		
		sb.append("function stack(i) { var x0 = 0; return function(d) { var tx = \"translate(\" + x0 + \",\" + y * i * 1.2 + \")\";\n");
		sb.append("x0 += x(d.value); return tx; }; }\n\n");
		
		sb.append("</script>");
		
		return sb.toString();
	}	
}

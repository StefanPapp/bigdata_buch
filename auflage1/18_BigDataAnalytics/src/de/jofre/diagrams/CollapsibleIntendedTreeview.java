package de.jofre.diagrams;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Generiert eine interaktive Baumansicht von hierarchischen Daten.
 * 
 * Datentyp: JSON
 * 
 * Format:
 * {
 *  "name": "Instrumente",
 *  "children": [
 *   {
 *    "name": "Blasinstrumente",
 *    "children": [
 *     {
 *      "name": "Blasinstrumente 1",
 *      "children": [
 *       {"name": "Tuba"},
 *       {"name": "Posaune"},
 *       {"name": "Oboe"},
 *       {"name": "Trompete"}
 *      ]
 *     }, ...
 *    ]
 *   }
 *  ]
 * }
 */
public class CollapsibleIntendedTreeview {

	private static Logger log = Logger.getLogger(CollapsibleIntendedTreeview.class
			.getName());
	
	private int width;
	private int height;
	private int barHeight;
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
	public int getBarHeight() {
		return barHeight;
	}
	public void setBarHeight(int barHeight) {
		this.barHeight = barHeight;
	}
	public String getInput() {
		return input;
	}
	public void setInput(String input) {
		this.input = input;
	}
	
	public CollapsibleIntendedTreeview() {
		this.width = 800;
		this.height = 600;
		this.barHeight = 20;
	}
	
	public String getAdditionalJavaScript() {
		return "<!-- Collapsible-Intended-Treeview benötigt keine weiteren JavaScript-Bibliotheken -->";
	}	
	
	public String getStyleSheet() {
		StringBuilder sb = new StringBuilder();
		sb.append("<style type=\"text/css\">\n");
		sb.append("\t.node rect {cursor: pointer; fill: #fff; fill-opacity: .5; stroke: #3182bd; stroke-width: 1.5px; }\n");
		sb.append("\t.node text {font: 10px sans-serif; pointer-events: none; }\n");
		sb.append("\tpath.link {fill: none; stroke: #9ecae1; stroke-width: 1.5px; }\n");
		sb.append("</style>\n\n");
		return sb.toString();
	}
	
	public String getJavaScript() {
		StringBuilder sb = new StringBuilder();
		Random r = new Random(1234);
		String strImageTagID = "id" + String.valueOf(r.nextInt(100000));
		
		if (this.input == null || this.input.equals("")) {
			log.log(Level.WARNING, "Erzeuge Collapsible-Intended-Treeview ohne Input.");
		}
		
		// Div-Element
		sb.append("<div id=\"" + strImageTagID + "\"></div>\n");
		
		// Diagram-Script
		sb.append("<script type=\"text/javascript\">\n");
		
		sb.append("var w = "+this.width+",h = "+this.height+",i = 0,barHeight = "+this.barHeight+",barWidth = w * .8,duration = 400,root;\n");
		sb.append("var tree = d3.layout.tree().size([h, 100]);\n");
		sb.append("var diagonal = d3.svg.diagonal().projection(function(d) { return [d.y, d.x]; });\n");
		sb.append("var vis = d3.select(\"#"+strImageTagID+"\").append(\"svg:svg\").attr(\"width\", w)\n");
		sb.append("\t.attr(\"height\", h).append(\"svg:g\").attr(\"transform\", \"translate(20,30)\");\n");
		
		sb.append("var json = JSON.parse('"+input+"');\n\n");
		
		sb.append("json.x0 = 0; json.y0 = 0; update(root = json);\n\n");
		
		sb.append("function update(source) {\n\tvar nodes = tree.nodes(root);\n\tnodes.forEach(function(n, i) {\n");
		sb.append("\t\tn.x = i * barHeight;\n");
		sb.append("\t});\n\n");
		
		sb.append("var node = vis.selectAll(\"g.node\").data(nodes, function(d) { return d.id || (d.id = ++i); });\n\n");
		
		sb.append("var nodeEnter = node.enter().append(\"svg:g\").attr(\"class\", \"node\")\n");
		sb.append("\t.attr(\"transform\", function(d) { return \"translate(\" + source.y0 + \",\" + source.x0 + \")\"; })\n");
		sb.append("\t.style(\"opacity\", 1e-6);\n\n");
		
		sb.append("nodeEnter.append(\"svg:rect\").attr(\"y\", -barHeight / 2).attr(\"height\", barHeight)\n");
		sb.append("\t.attr(\"width\", barWidth).style(\"fill\", color).on(\"click\", click);\n\n");
		
		sb.append("nodeEnter.append(\"svg:text\").attr(\"dy\", 3.5).attr(\"dx\", 5.5).text(function(d) { return d.name; });\n\n");
		
		sb.append("nodeEnter.transition().duration(duration).attr(\"transform\", function(d) { return \"translate(\" + d.y + \",\" + d.x + \")\"; })\n");
		sb.append("\t.style(\"opacity\", 1);\n\n");
		
		sb.append("node.transition().duration(duration).attr(\"transform\", function(d) { return \"translate(\" + d.y + \",\" + d.x + \")\"; })\n");
		sb.append("\t.style(\"opacity\", 1).select(\"rect\").style(\"fill\", color);\n\n");
		
		sb.append("node.exit().transition().duration(duration).attr(\"transform\", function(d) { return \"translate(\" + source.y + \",\" + source.x + \")\"; })\n");
		sb.append("\t.style(\"opacity\", 1e-6).remove();\n\n");
		
		sb.append("var link = vis.selectAll(\"path.link\").data(tree.links(nodes), function(d) { return d.target.id; });\n\n");
		
		sb.append("link.enter().insert(\"svg:path\", \"g\").attr(\"class\", \"link\").attr(\"d\", function(d) {\n");
		sb.append("\tvar o = {x: source.x0, y: source.y0};\n\treturn diagonal({source: o, target: o});\n");
		sb.append("}).transition().duration(duration).attr(\"d\", diagonal);\n\n");
		
		sb.append("link.transition().duration(duration).attr(\"d\", diagonal);\n\n");
		
		sb.append("link.exit().transition().duration(duration).attr(\"d\", function(d) {\n");
		sb.append("\tvar o = {x: source.x, y: source.y};\n\treturn diagonal({source: o, target: o});\n");
		sb.append("}).remove();\n\n");
		
		sb.append("nodes.forEach(function(d) {\n\td.x0 = d.x;\td.y0 = d.y;\n});\n}\n\n");
		
		sb.append("function click(d) {\n\tif (d.children) {\n\t\td._children = d.children;\n\td.children = null;\n");
		sb.append("\t} else {\n\t\td.children = d._children;\n\t\td._children = null;\n\t}\n\tupdate(d);\n}\n\n");
		
		sb.append("function color(d) {\n\treturn d._children ? \"#3182bd\" : d.children ? \"#c6dbef\" : \"#fd8d3c\";\n}\n\n");
		
		sb.append("</script>");
		
		return sb.toString();
	}		
}

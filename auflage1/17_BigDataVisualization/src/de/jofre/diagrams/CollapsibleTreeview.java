package de.jofre.diagrams;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Ausklappbare Baumansicht.
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
public class CollapsibleTreeview {

	private static Logger log = Logger.getLogger(CollapsibleTreeview.class
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
	
	public CollapsibleTreeview() {
		this.width = 400;
		this.height = 400;
	}
	
	public String getAdditionalJavaScript() {
		return "<!-- Collapsible-Treeview benötigt keine weiteren JavaScript-Bibliotheken -->";
	}
	
	public String getStyleSheet() {
		StringBuilder sb = new StringBuilder();
		sb.append("<style type=\"text/css\">\n");
		sb.append("\t.node circle {cursor: pointer; fill: #fff; stroke: steelblue; stroke-width: 1.5px; }\n");
		sb.append("\t.node text {font-size: 11px; }\n");
		sb.append("\tpath.link {fill: none; stroke: #ccc; stroke-width: 1.5px; }\n");
		sb.append("</style>\n\n");
		return sb.toString();
	}	
	
	public String getJavaScript() {
		StringBuilder sb = new StringBuilder();
		Random r = new Random(1234);
		String strImageTagID = "id" + String.valueOf(r.nextInt(100000));
		
		if (this.input == null || this.input.equals("")) {
			log.log(Level.WARNING, "Erzeuge Collapsible-Treeview ohne Input.");
		}
		
		// Div-Element
		sb.append("<div id=\"" + strImageTagID + "\"></div>\n");
		
		// Diagram-Script
		sb.append("<script type=\"text/javascript\">\n");
		
		sb.append("var m = [0, 0, 0, 0], w = "+this.width+" - m[1] - m[3], h = "+this.height+" - m[0] - m[2], i = 0, root;\n");
		sb.append("var tree = d3.layout.tree().size([h, w]);\n");
		sb.append("var diagonal = d3.svg.diagonal().projection(function(d) { return [d.y, d.x]; });\n");
		
		sb.append("var vis = d3.select(\"#"+strImageTagID+"\").append(\"svg:svg\").attr(\"width\", w + m[1] + m[3])\n");
		sb.append(".attr(\"height\", h + m[0] + m[2]).append(\"svg:g\").attr(\"transform\", \"translate(\" + m[3] + \",\" + m[0] + \")\");\n\n");
		
		sb.append("var json = JSON.parse('"+this.input+"');\n\n");
		sb.append("root = json; root.x0 = h / 2; root.y0 = 0;\n");
		
		sb.append("function toggleAll(d) { if (d.children) { d.children.forEach(toggleAll); toggle(d); } }\n");
		
		sb.append("update(root);\n");

		sb.append("function update(source) { var duration = d3.event && d3.event.altKey ? 5000 : 500;\n");

		sb.append("var nodes = tree.nodes(root).reverse(); nodes.forEach(function(d) { d.y = d.depth * 180; });\n");
		
		sb.append("var node = vis.selectAll(\"g.node\").data(nodes, function(d) { return d.id || (d.id = ++i); });\n");
		
		sb.append("var nodeEnter = node.enter().append(\"svg:g\").attr(\"class\", \"node\")\n");
		sb.append(".attr(\"transform\", function(d) { return \"translate(\" + source.y0 + \",\" + source.x0 + \")\"; })\n");
		sb.append(".on(\"click\", function(d) { toggle(d); update(d); });\n");
		
		sb.append("nodeEnter.append(\"svg:circle\").attr(\"r\", 1e-6)\n");
		sb.append(".style(\"fill\", function(d) { return d._children ? \"lightsteelblue\" : \"#fff\"; });\n");

		sb.append("nodeEnter.append(\"svg:text\").attr(\"x\", function(d) { return d.children || d._children ? -10 : 10; })\n");
		sb.append(".attr(\"dy\", \".35em\").attr(\"text-anchor\", function(d) { return d.children || d._children ? \"end\" : \"start\"; })\n");
		sb.append(".text(function(d) { return d.name; }).style(\"fill-opacity\", 1e-6);\n");
		
		sb.append("var nodeUpdate = node.transition().duration(duration).attr(\"transform\", function(d) { return \"translate(\" + d.y + \",\" + d.x + \")\"; });\n");
		
		sb.append("nodeUpdate.select(\"circle\").attr(\"r\", 4.5).style(\"fill\", function(d) { return d._children ? \"lightsteelblue\" : \"#fff\"; });\n");
		
		sb.append("nodeUpdate.select(\"text\").style(\"fill-opacity\", 1);\n");
		
		sb.append("var nodeExit = node.exit().transition().duration(duration).attr(\"transform\", function(d) { return \"translate(\" + source.y + \",\" + source.x + \")\"; })\n");
		
		sb.append(".remove();\n\n");
		
		sb.append("nodeExit.select(\"circle\").attr(\"r\", 1e-6);\n\n");
		
		sb.append("nodeExit.select(\"text\").style(\"fill-opacity\", 1e-6);\n\n");
		
		sb.append("var link = vis.selectAll(\"path.link\").data(tree.links(nodes), function(d) { return d.target.id; });\n\n");
		
		sb.append("link.enter().insert(\"svg:path\", \"g\").attr(\"class\", \"link\").attr(\"d\", function(d) {\n");
		sb.append("var o = {x: source.x0, y: source.y0}; return diagonal({source: o, target: o}); })\n");
		sb.append(".transition().duration(duration).attr(\"d\", diagonal);\n\n");
		
		sb.append("link.transition().duration(duration).attr(\"d\", diagonal);\n\n");
		
		sb.append("link.exit().transition().duration(duration).attr(\"d\", function(d) {var o = {x: source.x, y: source.y}; \n");
		sb.append("return diagonal({source: o, target: o}); }).remove();\n\n");
		
		sb.append("nodes.forEach(function(d) { d.x0 = d.x; d.y0 = d.y; }); }\n");
		
		sb.append("function toggle(d) { if (d.children) { d._children = d.children; d.children = null;\n");
		sb.append("} else {d.children = d._children; d._children = null; } }\n\n");
		sb.append("</script>");
		
		return sb.toString();
	}
}

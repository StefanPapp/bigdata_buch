package de.jofre.diagrams;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Generiert eine hierarchische Treemap, deren einzelnen Gruppierungen hervorgehoben werden
 * können.
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
public class ZoomableTreemap {
	private static Logger log = Logger.getLogger(ZoomableTreemap.class
			.getName());
	
	public int width;
	public int height;
	public String input;
	public boolean sortBySize;
	
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
	public boolean isSortBySize() {
		return sortBySize;
	}
	public void setSortBySize(boolean sortBySize) {
		this.sortBySize = sortBySize;
	}	
	
	public ZoomableTreemap() {
		this.width = 800;
		this.height = 400;
		this.sortBySize = true;
	}
	
	public String getAdditionalJavaScript() {
		return "<!-- Zoomable-Treemap benötigt keine weiteren JavaScript-Bibliotheken -->";
	}	
	
	public String getStyleSheet() {
		StringBuilder sb = new StringBuilder();
		sb.append("<style type=\"text/css\">\n");
		sb.append("\t.chart {display: block; margin: auto; margin-top: 40px;}\n");
		sb.append("\ttext {font-size: 11px; }\n");
		sb.append("\trect {fill: none;}\n");
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
		
		sb.append("var w = "+this.width+" - 80, h = "+this.height+" - 180, x = d3.scale.linear().range([0, w]), y = d3.scale.linear().range([0, h]), color = d3.scale.category20c(), root, node;\n\n");

		sb.append("var treemap = d3.layout.treemap().round(false).size([w, h]).sticky(true).value(function(d) { return d.size; });\n\n");
		
		sb.append("var svg = d3.select(\"#"+strImageTagID+"\").append(\"div\").attr(\"class\", \"chart\").style(\"width\", w + \"px\").style(\"height\", h + \"px\")\n");
		
		sb.append("\t.append(\"svg:svg\").attr(\"width\", w).attr(\"height\", h).append(\"svg:g\").attr(\"transform\", \"translate(.5,.5)\");\n\n");
		
		sb.append("var data = JSON.parse('"+this.input+"');\n\n");

			//d3.json("flare.json", function(data) {
		sb.append("node = root = data;\nvar nodes = treemap.nodes(root).filter(function(d) { return !d.children; });\n\n");
		
		sb.append("var cell = svg.selectAll(\"g\").data(nodes).enter().append(\"svg:g\").attr(\"class\", \"cell\")\n");
		sb.append("\t.attr(\"transform\", function(d) { return \"translate(\" + d.x + \",\" + d.y + \")\"; })\n");
		sb.append("\t.on(\"click\", function(d) { return zoom(node == d.parent ? root : d.parent); });\n\n");
		
		sb.append("cell.append(\"svg:rect\").attr(\"width\", function(d) { return d.dx - 1; })\n");
		sb.append("\t.attr(\"height\", function(d) { return d.dy - 1; }).style(\"fill\", function(d) { return color(d.parent.name); });\n\n");
		
		sb.append("cell.append(\"svg:text\").attr(\"x\", function(d) { return d.dx / 2; }).attr(\"y\", function(d) { return d.dy / 2; })\n");
		sb.append("\t.attr(\"dy\", \".35em\").attr(\"text-anchor\", \"middle\").text(function(d) { return d.name; })\n");
		sb.append("\t.style(\"opacity\", function(d) { d.w = this.getComputedTextLength(); return d.dx > d.w ? 1 : 0; });\n\n");
		
		sb.append("d3.select(window).on(\"click\", function() { zoom(root); });\n\n");

		sb.append("d3.select(\"select\").on(\"change\", function() {\n");
		//sb.append("treemap.value(this.value == \"size\" ? size : count).nodes(root);\n");
		sb.append("\ttreemap.value("+this.sortBySize+" ? size : count).nodes(root);\n");
		sb.append("\tzoom(node);\n});\n\n");
		
		sb.append("function size(d) { return d.size;} \n\n function count(d) { return 1; }\n\n");
		
		sb.append("function zoom(d) { var kx = w / d.dx, ky = h / d.dy; x.domain([d.x, d.x + d.dx]); y.domain([d.y, d.y + d.dy]);\n\n");
		
		sb.append("var t = svg.selectAll(\"g.cell\").transition().duration(d3.event.altKey ? 7500 : 750)\n");
		sb.append("\t.attr(\"transform\", function(d) { return \"translate(\" + x(d.x) + \",\" + y(d.y) + \")\"; });\n\n");
		
		sb.append("t.select(\"rect\").attr(\"width\", function(d) { return kx * d.dx - 1; })\n");
		sb.append("\t.attr(\"height\", function(d) { return ky * d.dy - 1; });\nt.select(\"text\")");
		sb.append(".attr(\"x\", function(d) { return kx * d.dx / 2; }).attr(\"y\", function(d) { return ky * d.dy / 2; })\n");
		sb.append("\t.style(\"opacity\", function(d) { return kx * d.dx > d.w ? 1 : 0; });\n\n");
		sb.append("node = d; d3.event.stopPropagation();\n}\n");		
		
		sb.append("</script>");
		
		return sb.toString();
	}	
}

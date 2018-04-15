package de.jofre.diagrams;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Generiert eine hierarchische Kugelansicht, wobei die Größe und Beschriftung
 * jedes Elements spezifiziert werden können.
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
public class BubbleChart {

	private static Logger log = Logger.getLogger(BubbleChart.class
			.getName());
	
	private int height;
	private int width;
	private String input;
	
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
	
	public BubbleChart() {
		this.height = 400;
		this.width = 400;
	}
	
	public String getStyleSheet() {
		return "<!-- Bubble-Chart benötigt kein CSS -->";
	}
	
	public String getAdditionalJavaScript() {
		return "<!-- Bubble-Chart benötigt keine weiteren JavaScript-Bibliotheken -->";
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
		sb.append("\tvar width=" + this.getWidth() + ", height=" + this.getHeight() + ", format = d3.format(\",d\"), color = d3.scale.category20c();\n");
		sb.append("\tvar bubble = d3.layout.pack().sort(null).size([width, height]).padding(1.5);\n");
		sb.append("\tvar svg = d3.select(\"#"+strImageTagID+"\").append(\"svg\").attr(\"width\", width).attr(\"height\", height).attr(\"class\", \"bubble\");\n");
		sb.append("\tvar root = JSON.parse('"+input+"');\n\n");		
		sb.append("\tvar node = svg.selectAll(\".node\").data(bubble.nodes(classes(root)).filter(function(d) { return !d.children; }))\n");
		sb.append("\t\t.enter().append(\"g\").attr(\"class\", \"node\").attr(\"transform\", function(d) { return \"translate(\" + d.x + \",\" + d.y + \")\"; });\n");
		sb.append("\tnode.append(\"title\").text(function(d) { return d.className + \": \" + format(d.value); });\n");
		sb.append("\tnode.append(\"circle\").attr(\"r\", function(d) { return d.r; }).style(\"fill\", function(d) { return color(d.packageName); });\n");
		sb.append("\tnode.append(\"text\").attr(\"dy\", \".3em\").style(\"text-anchor\", \"middle\").text(function(d) { return d.className.substring(0, d.r / 3); });\n\n");
		
		sb.append("\tfunction classes(root) {\n");
		sb.append("\t\tvar classes = [];\n");
		sb.append("\t\tfunction recurse(name, node) {\n");
		sb.append("\t\t\tif (node.children) node.children.forEach(function(child) { recurse(node.name, child); });\n");
		sb.append("\t\t\telse classes.push({packageName: name, className: node.name, value: node.size});\n");
		sb.append("\t\t}\n");
		
		sb.append("\t\trecurse(null, root);\n");
		sb.append("\t\treturn {children: classes};\n");
		sb.append("\t}\n\n");
		
		sb.append("\td3.select(self.frameElement).style(\"height\", height + \"px\");\n");
		sb.append("</script>");		
		
		return sb.toString();
	}
}

package de.jofre.diagrams;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Generiert ein Diagramm in Form einer Schnecke, das hierarchische Daten
 * farblich und ungewichtet darstellt.
 * 
 * Datentyp: JSON
 * 
 * Format:
 * (Bezeichner)
 * [
 *  {
 *   "name": "Disneyfilme",
 *   "children": [
 *    {
 *     "name": "Die Schöne und das Biest", "color": "#f9f0ab"
 *    },
 *    {
 *     "name": "Tarzan", "color": "#e8e596"
 *    }
 *   ]
 *  }, ...
 * ]
 * 
 */
public class SunburstChart {

	private static Logger log = Logger.getLogger(SunburstChart.class
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
	
	public SunburstChart() {
		this.height = 400;
		this.width = 400;
	}
	
	public String getStyleSheet() {
		return "<!-- Sunburst-Chart benötigt kein CSS -->";
	}
	
	public String getAdditionalJavaScript() {
		return "<!-- Sunburst-Chart benötigt keine weiteren JavaScript-Bibliotheken -->";
	}
	
	public String getJavaScript() {
		StringBuilder sb = new StringBuilder();
		Random r = new Random(1234);
		String strImageTagID = "id" + String.valueOf(r.nextInt(100000));
		
		if (this.input == null || this.input.equals("")) {
			log.log(Level.WARNING, "Erzeuge ein Sunburst-Chart ohne Input.");
		}
		
		// Div-Element
		sb.append("<div id=\"" + strImageTagID + "\"></div>\n");
		
		// Diagram-Script
		sb.append("<script type=\"text/javascript\">\n");
		
		sb.append("var width = "+this.width+",height = "+this.height+",radius = width / 2,x = d3.scale.linear().range([0, 2 * Math.PI]),\n");
		sb.append("\ty = d3.scale.pow().exponent(1.3).domain([0, 1]).range([0, radius]),padding = 5,duration = 1000;\n\n");
		
		sb.append("var div = d3.select(\"#"+strImageTagID+"\")\n");

		sb.append("var vis = div.append(\"svg\").attr(\"width\", width + padding * 2).attr(\"height\", height + padding * 2)\n");
		sb.append("\t.append(\"g\").attr(\"transform\", \"translate(\" + [radius + padding, radius + padding] + \")\");\n\n");
		
		sb.append("var partition = d3.layout.partition().sort(null).value(function(d) { return 5.8 - d.depth; });\n\n");
		
		sb.append("var arc = d3.svg.arc().startAngle(function(d) { return Math.max(0, Math.min(2 * Math.PI, x(d.x))); })\n");
		sb.append("\t.endAngle(function(d) { return Math.max(0, Math.min(2 * Math.PI, x(d.x + d.dx))); })\n");
		sb.append("\t.innerRadius(function(d) { return Math.max(0, d.y ? y(d.y) : d.y); })\n");
		sb.append("\t.outerRadius(function(d) { return Math.max(0, y(d.y + d.dy)); });\n\n");
		
		sb.append("var json = JSON.parse('"+this.input+"');\n\n");

			//d3.json(\"wheel.json\", function(json) {
		sb.append("var nodes = partition.nodes({children: json});\n");
		sb.append("var path = vis.selectAll(\"path\").data(nodes);\n");
		sb.append("path.enter().append(\"path\").attr(\"id\", function(d, i) { return \"path-\" + i; }).attr(\"d\", arc)\n");
		sb.append("\t.attr(\"fill-rule\", \"evenodd\").style(\"fill\", color).on(\"click\", click);\n\n");
		
		sb.append("var text = vis.selectAll(\"text\").data(nodes);\n");
		sb.append("var textEnter = text.enter().append(\"text\").style(\"fill-opacity\", 1).style(\"fill\", function(d) {\n");
		sb.append("\treturn brightness(d3.rgb(color(d))) < 125 ? \"#eee\" : \"#000\";\n");
		sb.append("}).attr(\"text-anchor\", function(d) {\n");
		sb.append("\treturn x(d.x + d.dx / 2) > Math.PI ? \"end\" : \"start\";\n");
		sb.append("}).attr(\"dy\", \".2em\").attr(\"transform\", function(d) {\n");
		sb.append("\tvar multiline = (d.name || \"\").split(\" \").length > 1,\n");
		sb.append("\tangle = x(d.x + d.dx / 2) * 180 / Math.PI - 90, rotate = angle + (multiline ? -.5 : 0);\n");
		sb.append("\treturn \"rotate(\" + rotate + \")translate(\" + (y(d.y) + padding) + \")rotate(\" + (angle > 90 ? -180 : 0) + \")\";\n");
		sb.append("}).on(\"click\", click);\n\n");
		
		sb.append("textEnter.append(\"tspan\").attr(\"x\", 0).text(function(d) { return d.depth ? d.name.split(\" \")[0] : \"\";\n});\n\n");
		
		sb.append("textEnter.append(\"tspan\").attr(\"x\", 0).attr(\"dy\", \"1em\")\n");
		sb.append("\t.text(function(d) { return d.depth ? d.name.split(\" \")[1] || \"\" : \"\";\n});\n\n");

		sb.append("function click(d) {\n");
		sb.append("\tpath.transition().duration(duration).attrTween(\"d\", arcTween(d));\n");
		sb.append("text.style(\"visibility\", function(e) {\n");
		sb.append("\treturn isParentOf(d, e) ? null : d3.select(this).style(\"visibility\");\n");
		sb.append("}).transition().duration(duration).attrTween(\"text-anchor\", function(d) {\n");
		sb.append("\treturn function() {\n");
		sb.append("\t\treturn x(d.x + d.dx / 2) > Math.PI ? \"end\" : \"start\";\n\t};\n})");
		sb.append(".attrTween(\"transform\", function(d) {\n");
		sb.append("\tvar multiline = (d.name || \"\").split(\" \").length > 1;\n");
		sb.append("\treturn function() {\n");
		sb.append("\t\tvar angle = x(d.x + d.dx / 2) * 180 / Math.PI - 90, rotate = angle + (multiline ? -.5 : 0);\n");
		sb.append("\t\treturn \"rotate(\" + rotate + \")translate(\" + (y(d.y) + padding) + \")rotate(\" + (angle > 90 ? -180 : 0) + \")\";\n");
		sb.append("\t};\n}).style(\"fill-opacity\", function(e) { return isParentOf(d, e) ? 1 : 1e-6; }).each(\"end\", function(e) {\n");
		sb.append("\td3.select(this).style(\"visibility\", isParentOf(d, e) ? null : \"hidden\");\n");
		sb.append("});\n}\n");
			//});

		sb.append("function isParentOf(p, c) {\n\tif (p === c) return true;\n\tif (p.children) {\n");
		sb.append("\t\treturn p.children.some(function(d) {\n\t\t\treturn isParentOf(d, c);\n\t\t});\n\t}\n\treturn false;\n}\n\n");
		
		sb.append("function color(d) {\n\tif (d.children) {\n\t\tvar colors = d.children.map(color),a = d3.hsl(colors[0]),b = d3.hsl(colors[1]);\n");
		sb.append("\t\treturn d3.hsl((a.h + b.h) / 2, a.s * 1.2, a.l / 1.2);\n");
		sb.append("\t}\n\treturn d.color || \"#fff\";\n}\n\n");
		
		sb.append("function arcTween(d) {\n\tvar my = maxY(d),xd = d3.interpolate(x.domain(), [d.x, d.x + d.dx]),\n");
		sb.append("\t\tyd = d3.interpolate(y.domain(), [d.y, my]),yr = d3.interpolate(y.range(), [d.y ? 20 : 0, radius]);\n");
		sb.append("\treturn function(d) {\n\t\treturn function(t) { x.domain(xd(t)); y.domain(yd(t)).range(yr(t)); return arc(d);};\n");
		sb.append("\t};\n}\n\n");
		
		sb.append("function maxY(d) {\n\treturn d.children ? Math.max.apply(Math, d.children.map(maxY)) : d.y + d.dy;\n}\n\n");

		sb.append("function brightness(rgb) {\n\treturn rgb.r * .299 + rgb.g * .587 + rgb.b * .114;\n}\n");
		
		sb.append("</script>");		
		
		return sb.toString();
	}
}

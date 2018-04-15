package de.jofre.diagrams;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
Copyright (c) 2013, Jason Davies.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

  * Redistributions of source code must retain the above copyright notice, this
    list of conditions and the following disclaimer.

  * Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

  * The name Jason Davies may not be used to endorse or promote products
    derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL JASON DAVIES BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */

/**
 * Generiert eine Wordcloud aus einer CSV-Datei, bestehend aus einzelnen Begriffen.
 * Der Wordcloud-Code wurde von Jason Davies verfasst und unterliegt dem oben stehenden
 * Copyright.
 * 
 * Datentyp: CSV
 * 
 * Format:
 * "Wort 1", "Wort 2", ...
 */
public class WordCloud {
	private static Logger log = Logger.getLogger(WordCloud.class
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
	
	public WordCloud() {
		this.width = 400;
		this.height = 400;
	}
	
	public String getAdditionalJavaScript() {
		return "<script src=\"js/d3.js.layout.clouds.js\" charset=\"utf-8\"></script>\n";
	}	
	
	public String getStyleSheet() {
		return "<!-- Word-Cloud benötigt keine weiteren JavaScript-Bibliotheken -->";
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
		
		sb.append("var fill = d3.scale.category20();");
		
		sb.append("var width="+this.width+", height="+this.height+";");
		
		sb.append("d3.layout.cloud().size([width, height]).words([");
		
		// Format "This", "is", "a", "test"
		sb.append(this.input);
		
		sb.append("].map(function(d) { return {text: d, size: 10 + Math.random() * 90}; }))");
		sb.append(".rotate(function() { return ~~(Math.random() * 2) * 90; }).font(\"Impact\")");
		sb.append(".fontSize(function(d) { return d.size; }).on(\"end\", draw).start();");
		
		sb.append("function draw(words) { d3.select(\"#"+strImageTagID+"\").append(\"svg\")");
		sb.append(".attr(\"width\", width).attr(\"height\", height).append(\"g\")");
		sb.append(".attr(\"transform\", \"translate(\"+(width/2)+\", \"+(height/2)+\")\")");
		sb.append(".selectAll(\"text\").data(words).enter().append(\"text\")");
		sb.append(".style(\"font-size\", function(d) { return d.size + \"px\"; })");
		sb.append(".style(\"font-family\", \"Impact\").style(\"fill\", function(d, i) { return fill(i); })");
		sb.append(".attr(\"text-anchor\", \"middle\").attr(\"transform\", function(d) {");
		sb.append("return \"translate(\" + [d.x, d.y] + \")rotate(\" + d.rotate + \")\";");
		sb.append("}).text(function(d) { return d.text; }); }");
		
		sb.append("</script>");
		
		return sb.toString();
	}	
}

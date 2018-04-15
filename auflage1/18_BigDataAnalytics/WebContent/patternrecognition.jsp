<%@page import="de.jofre.textmining.uima.CVAnalyzer"%>
<%@page import="de.jofre.textmining.uima.Cv"%>
<%@page import="java.util.List"%>
<%@page import="de.jofre.table.EnrichmentPlace"%>
<%@page import="de.jofre.table.EnrichmentType"%>
<%@page import="de.jofre.table.BDTable"%>
<%@page import="de.jofre.nlp.Classifier"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="shortcut icon" href="img/favicon.ico" type="image/x-icon" />
<link rel="stylesheet" href="css/style.css" type="text/css" />
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Big-Data-Analytics</title>
</head>
<body>


	<div id="container">
		<div id="header">
			<h1>Big-Data-Analytics</h1>
		</div>

		<%@include file="WEB-INF//menu.jsp"%>

		<div id="content">
			<h2>Mustererkennung</h2>
			
		<%
			// Auswerten der Parameter
			String inputText = request.getParameter("mytext");
			boolean headersIncluded = (session.getAttribute("headline") != null);
			if (headersIncluded) System.out.println("Headers");
			// Frage die zuvor über HDFS, Hive oder HBase geladenen Daten ab
			if ((session.getAttribute("currentinput") != null) && (inputText == null)) {
				inputText = (String)session.getAttribute("currentinput");
			}
			
			// Wurden bestimmte Spalten ausgewählt?
			String startX = request.getParameter("startx");
			String startY = request.getParameter("starty");
			String endX = request.getParameter("endx");
			String endY = request.getParameter("endy");
			
			BDTable table = new BDTable();
			if (inputText != null) {
				table.readTableFromText(inputText, "\t", headersIncluded);

				// Anwenden der Spaltenfilter
				if ((startX != null) && (startY != null) && (endX != null) && (endY != null)) {
					boolean filterResult = table.filterByColumns(Integer.parseInt(startX), Integer.parseInt(startY), Integer.parseInt(endX), Integer.parseInt(endY));
					if (filterResult) {
						out.println("<b>Spalten- und Zeilenfilter wurden angewandt.</b><br>");
					} else {
						out.println("<b>Spalten- und Zeilenfilter konnte nicht angewandt werden. Überprüfen Sie die richtigkeit der Parameter!</b><br>");
					}
				}
				
				// Auswahl von Zellen und Spalten (HTML Code)
				out.println("<form action=\"classification.jsp\" method=\"get\"><table><tr><td><b>Auswahl der zu analysierenden Daten:</b></td><td>Startzeile:</td><td>");
				out.println("<select name=\"starty\">");
				for(int i=0; i<table.getCells().size(); i++) {
					out.println("<option>"+i+"</option>");
				}
				out.println("</select></td><td>Startspalte:<select name=\"startx\">");
				if (table.getCells().size()>0) {
					for(int i=0; i<table.getCells().get(0).size(); i++) {
						out.println("<option>"+i+"</option>");
					}
				}
				out.println("</select></td><td>Endzeile:</td><td>");
				out.println("<select name=\"endy\">");
				for(int i=0; i<table.getCells().size(); i++) {
					out.println("<option>"+i+"</option>");
				}
				out.println("</select></td><td>Endspalte:<select name=\"endx\">");
				if (table.getCells().size()>0) {
					for(int i=0; i<table.getCells().get(0).size(); i++) {
						out.println("<option>"+i+"</option>");
					}
				}
				out.println("</select></td><td><input type=\"submit\" value=\"Auswählen\" /></tr></table></form>");			
				
				// Mustererkennung
				Cv cv = CVAnalyzer.analyzeCV(table.toCSV(headersIncluded));
				
				if (cv != null) {
					out.println("Erkannte Personendaten:<br>");
					out.println("<table style=\"width:100%; border: 1px solid #4F4F4F; border-collapse: collapse;\"><tr><td><b>Name</b></td><td><b>Alter</b></td><td><b>Ausbildung</b></td><td><b>Geschlecht</b></td><td><b>Email</b></td></tr>");
					out.println("<tr><td>"+ cv.getName() + "</td><td>" + cv.getAge()+"</td><td>"+ cv.getEducation() + "</td><td>" + cv.getGender() + "</td><td>" + cv.getEmail()+"</td></tr></table>");
					out.println("<br>");
				}				
				
				out.println(table.toHTMLTable());
			} else {
				out.println("Wählen Sie vor der Mustererkennung Quelldaten aus HDFS, Hive oder HBase.");
			}
		 %>			
		</div>
		<div id="footer">
			Big-Data in der Praxis | <a href="www.jofre.de">www.jofre.de</a>
		</div>
	</div>
</body>
</html>
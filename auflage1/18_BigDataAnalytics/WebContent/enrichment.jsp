<%@page import="de.jofre.table.EnrichmentType"%>
<%@page import="de.jofre.table.EnrichmentPlace"%>
<%@page import="java.util.List"%>
<%@page import="de.jofre.table.BDTable"%>
<%@page import="de.jofre.nlp.Sentiment"%>
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
		
		<%
			String inputText = (String)session.getAttribute("currentinput");
			boolean headersIncluded = (request.getParameter("headline") != null);
			if (headersIncluded) {
				session.setAttribute("headline", true);
			} else {
				session.removeAttribute("headline");
			}
		 %>
		 
		 	

		<div id="content">
			<h2>Daten anreichern</h2>
			
			<form action="enrichment.jsp" method="get">
				<input type="checkbox" name="headline" <% if (headersIncluded) out.print("checked"); %> /> Daten enthalten Titelzeile
				<input type="submit" value="Aktualisieren" />
			</form><br>
			
			
			<%
				if (inputText == null) {
					out.println("Wählen Sie vor dem Anreichern Daten aus HDFS, Hive oder HBase");
				} else {
					BDTable table = new BDTable();
					table.readTableFromText(inputText, "\t", headersIncluded);
					
					// Sollen Daten angereichert werden?
					if (table.getCells().size() > 0) {
						for(int i=0; i<table.getCells().get(0).size(); i++) {
							if(request.getParameter("column" + i) != null) {
								String enrichmentType = request.getParameter("column" + i);
								EnrichmentType et = EnrichmentType.getTypeByCaption(enrichmentType);
								table.enrichColumn(i, et, EnrichmentPlace.NEW_COLUMN);
							}	
						}
					}
					
					// Enrichment-Boxen
					if (table.getCells().size() > 0) {
						out.print("<table style=\"width: 100%; border: 1px solid #4F4F4F; border-collapse: collapse;\"><tr>\n");
						int startRow = 0;
						for(int i=0; i<table.getCells().get(startRow).size(); i++) {				
							List<EnrichmentType> types = table.getEnrichmentOptions(table.getCells().get(startRow).get(i));
							out.print("<td><form action=\"enrichment.jsp\" method=\"post\"><select name=\"column" + i + "\">");
							for(int j=0; j<types.size(); j++) {
								out.print("<option>" + types.get(j).getCaption() + "</option>");
							}
							out.print("</select>");
							if (headersIncluded) {
								out.print("<input type=\"hidden\" name=\"headline\" />");
							}
							out.print("<input type=\"submit\" value=\"Anreichern\" />");
							
							out.print("</form></td>\n");
						}
						out.print("</tr></table>\n");
					} else {
						out.println("<b>Keine Daten zum Anreichern in Eingabedaten gefunden!</b><br>");
					}
					out.println(table.toHTMLTable());
					
					// Aktualisiere die Daten in der Session
					session.setAttribute("currentinput", table.toCSV(headersIncluded));
					if (headersIncluded) {
						session.setAttribute("headline", true);
					} else {
						session.removeAttribute("headline");
					}
				}
			 %>
		</div>
		<div id="footer">
			Big-Data in der Praxis | <a href="www.jofre.de">www.jofre.de</a>
		</div>
	</div>
</body>
</html>
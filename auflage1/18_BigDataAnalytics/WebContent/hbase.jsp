<%@page import="java.util.ArrayList"%>
<%@page import="de.jofre.hadoopcontroller.HadoopProperties"%>
<%@page import="org.apache.hadoop.hbase.util.Bytes"%>
<%@page import="org.apache.hadoop.hbase.Cell"%>
<%@page import="java.util.List"%>
<%@page import="de.jofre.hbasemanager.HBaseManager"%>
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
	<%
		HBaseManager manager = new HBaseManager(HadoopProperties.get("hbase_address"));
		List<String> tables = manager.getTables();
		
		// Ist eine Tabelle selektiert?
		String currentTable = (String)request.getParameter("table");
		if (currentTable == null) {
			if (tables.size() > 0) {
				currentTable = tables.get(0);
			}
		}
				 
	 %>
	<div id="container">
		<div id="header">
			<h1>Big-Data-Analytics</h1>
		</div>
		
		<%@include file="WEB-INF//menu.jsp"%>
	
		<div id="content">
			<%
							
				// Tabelle auswählen
				out.println("Tabelle: <form name=\"pickTableForm\" action=\"hbase.jsp\"><select name=\"table\" method=\"get\">");
				for(int i=0; i<tables.size(); i++) {
					if ((currentTable != null) && (currentTable.equalsIgnoreCase(tables.get(i)))) {
						out.println("<option name=\"" + tables.get(i)+"\" value=\""+tables.get(i)+"\" selected>"+tables.get(i)+"</option>");
					} else {
						out.println("<option name=\"" + tables.get(i)+"\" value=\""+tables.get(i)+"\">"+tables.get(i)+"</option>");
					}
				}
				out.println("</select>");
				if (request.getParameterValues("cfandname") != null) {
					out.println("<input type=\"checkbox\" name=\"cfandname\" value=\"checked\" checked/>Column-Family und Column-Name mit ausgeben<br>");
				} else {
					out.println("<input type=\"checkbox\" name=\"cfandname\" value=\"checked\"/>Column-Family und Column-Name mit ausgeben<br>");
				}
				out.println("<input type=\"submit\" value=\"Auswählen\"/>");
				out.println("</form>");
				
				// Übernehmen der Daten für die Analyse
				if (request.getParameter("currentinput") != null) {
					out.println("<b>Daten erfolgreich übernommen!</b><br>");
					session.setAttribute("currentinput",
							request.getParameter("currentinput"));
				}

				// Beginn der Darstellung
				if (currentTable == null) {
					out.println("<h2>Tabelleninspektor</h2>");
					out.println("Es sind keine Tabellen in Ihrer HBase-Instanz vorhanden!");
				} else {
					out.println("<h2>" + currentTable + "</h2>");
					
					// Auslesen der Zeilen
					List<List<Cell>> rows = manager.getRows(currentTable, null, manager.getFetchSize());
					
					// Zähle die maximale Spaltenzahl pro Resultset
					int max = 0;
					for(int i=0; i<rows.size(); i++) {
						if (rows.get(i).size() > max) {
							max = rows.get(i).size();
						}
					}
									
					// Zeichne die Werte in eine Textarea
					out.println("<br>Inhalt von Tabelle <i>" + currentTable + "</i>:<br>");
					// Anzeigen des Dateiinhalts
					out.println("<form action=\"hbase.jsp\" method=\"post\">");
					out.println("<textarea id=\"text\" name=\"currentinput\" cols=\"105\" rows=\"15\">");
					for(int i=0; i<rows.size(); i++) {
						
						// Lese alle Zellen aus						
						for(int j=0; j<rows.get(i).size(); j++) {
						
							// Gebe vor der ersten Spalte den Row-Key aus
							if (j==0) {
								out.print(Bytes.toString(rows.get(i).get(j).getRow()) + "\t");
							}

							// Ausgabe mit ColumnFamily und Spaltennamen?							
							String[] printCFandNames = request.getParameterValues("cfandname");
							boolean doPrintCF = false;
							if (printCFandNames != null) {
								doPrintCF = true;
							}
							
							// ... dann jede Zelle
							if (doPrintCF) {
								out.print(Bytes.toString(rows.get(i).get(j).getFamily()) + ":" + Bytes.toString(rows.get(i).get(j).getQualifier()) + 
									" = " + Bytes.toString(rows.get(i).get(j).getValue()));
							} else {
								// Nur den Wert ausgeben
								out.print(Bytes.toString(rows.get(i).get(j).getValue()));
							}
							
							if (j<rows.get(i).size()-1) {
								out.print("\t");
							}
						}
						if (i<rows.size()-1) {
							out.print("\n");
						}
					}
					out.print("</textarea>");
					out.println("<input type=\"submit\" value=\"Als Daten übernehmen\">");
					out.println("</form>");
				}
			 %>

			</div>
		<div id="footer">Big-Data in der Praxis | <a href="www.jofre.de">www.jofre.de</a></div>
	</div>
</body>
</html>
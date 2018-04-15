<%@page import="de.jofre.hadoopcontroller.HadoopProperties"%>
<%@page import="java.sql.Types"%>
<%@page import="java.sql.ResultSetMetaData"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.util.ArrayList"%>
<%@page import="de.jofre.hivemanager.HiveManager"%>
<%@page import="java.util.List"%>
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
			<h2>Hive-Manager</h2>
			<%
				HiveManager hm = new HiveManager();
				boolean connected = hm.connect(HadoopProperties.get("hive_address"), HadoopProperties.get("hive_port"), HadoopProperties.get("hive_user"), HadoopProperties.get("hive_password"));
				if (connected) {
					List<String> databases = hm.getDatabases();
					out.println("<table style=\"border-width: 1px; border-style: solid; width: 100%;\">");
					out.println("<tr><td><b>Datenbank/Schema</b></td><td><b>Tabellen</b></td></tr>");
					for(int i=0; i<databases.size(); i++) {
						out.println("<tr><td>" + databases.get(i) + "</td>");
						List<String> tables = hm.getTables(databases.get(i));
						
						out.println("<td>");
						for(int j=0; j<tables.size(); j++) {
							out.println("<a href=\"hive.jsp?db="+databases.get(i)+"&table="+tables.get(j)+"\">"+tables.get(j)+"</a>");
							if (j<tables.size()-1) {
								out.println(",");
							}
						}
						out.println("</td>");
						
						out.println("</tr>");
					}
					out.println("</table>");
					



					// Wurde bereits eine Datenbank und eine Tabelle gewählt?
					String db = (String) request.getParameter("db");
					String table = (String) request.getParameter("table");

					if (db != null && !db.trim().equals("") && table != null
							&& !table.trim().equals("")) {

						hm.switchDatabase(db);

						ResultSet rs;
						rs = hm.executeQuery("SELECT * FROM " + table + " LIMIT " + HiveManager.PAGING_SIZE);

						// Verarbeite Daten
						ResultSetMetaData rsmd = rs.getMetaData();
						
						out.println("<br>Inhalt von Tabelle <i>" + db + ":" + table + "</i>:<br>");
						// Anzeigen des Dateiinhalts
						out.println("<form action=\"hive.jsp\" method=\"post\">");
						out.println("<textarea id=\"text\" name=\"currentinput\" cols=\"105\" rows=\"15\">");
						
						// Spaltenkopf
						int columnCount = rsmd.getColumnCount();

						for (int i = 1; i <= columnCount; i++) {
							out.print(rsmd.getColumnName(i));
							if (i<columnCount) out.print("\t");
						}

						// Daten in Tabelle ausgeben
						while (rs.next()) {
							out.print("\n");

							// Letzte Spalte ist der row_count -> Wird weggelassen
							for (int i = 1; i <= columnCount; i++) {
								out.print(rs.getString(i));
								if (i<columnCount) out.print("\t");
							}
						}
						out.print("</textarea>");
						out.println("<input type=\"submit\" value=\"Als Daten übernehmen\">");
						out.println("</form>");
					}
					
				} else {
					out.println("Konnte keine Verbindung zu Hive herstellen!");
				}
				hm.disconnect();
				
				// Übernehmen der Daten für die Analyse
				if (request.getParameter("currentinput") != null) {
					out.println("<b>Daten erfolgreich übernommen!</b><br>");
					session.setAttribute("currentinput",
							request.getParameter("currentinput"));
				}
			 %>

		</div>
		<div id="footer">Big-Data in der Praxis | <a href="www.jofre.de">www.jofre.de</a></div>
	</div>
</body>
</html>
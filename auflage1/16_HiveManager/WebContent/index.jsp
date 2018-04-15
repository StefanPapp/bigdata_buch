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
<title>Hive-Manager</title>
</head>
<body>
	<div id="container">
		<div id="header">
			<h1>Hive-Manager</h1>
		</div>
		
		<div id="navigation">
			<ul>
				<li><a href="index.jsp"><b>Datenbanken und Tabellen</b></a></li>
				<li><a href="data.jsp">Daten</a></li>
				<li><a href="query.jsp">Abfragen</a></li>
				<li><a href="users.jsp">Benutzerverwaltung</a></li>
			</ul>
		</div>
		
	
	
		<div id="content">
			<%
				HiveManager hm = new HiveManager();
				boolean connected = hm.connect("single", "10000", "hduser", "hduser");
				if (connected) {
					List<String> databases = hm.getDatabases();
					out.println("<table style=\"border-width: 1px; border-style: solid; width: 100%;\">");
					out.println("<tr><td><b>Datenbank/Schema</b></td><td><b>Tabellen</b></td></tr>");
					for(int i=0; i<databases.size(); i++) {
						out.println("<tr><td>" + databases.get(i) + "</td>");
						List<String> tables = hm.getTables(databases.get(i));
						
						out.println("<td>");
						for(int j=0; j<tables.size(); j++) {
							out.println("<a href=\"data.jsp?db="+databases.get(i)+"&table="+tables.get(j)+"\">"+tables.get(j)+"</a>");
							if (j<tables.size()-1) {
								out.println(",");
							}
						}
						out.println("</td>");
						
						out.println("</tr>");
					}
					out.println("</table>");
				} else {
					out.println("Konnte keine Verbindung zu Hive herstellen!");
				}
				hm.disconnect();
			 %>

		</div>
		<div id="footer">Big-Data in der Praxis | <a href="www.jofre.de">www.jofre.de</a></div>
	</div>
</body>
</html>
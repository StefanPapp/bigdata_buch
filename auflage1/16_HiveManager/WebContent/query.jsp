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
<title>Hive-Manager</title>
</head>
<body>
	<div id="container">
		<div id="header">
			<h1>Hive-Manager</h1>
		</div>
		
		<div id="navigation">
			<ul>
				<li><a href="index.jsp">Datenbanken und Tabellen</a></li>
				<li><a href="data.jsp">Daten</a></li>
				<li><a href="query.jsp"><b>Abfragen</b></a></li>
				<li><a href="users.jsp">Benutzerverwaltung</a></li>
			</ul>
		</div>
		
	
	
		<div id="content">
			<%
				HiveManager hm = new HiveManager();
				boolean connected = hm.connect("single", "10000", "hduser", "hduser");
				if (connected) {

					// Wurde eine Abfrage abgesetzt?
					String query = (String)request.getParameter("query");
					String db = (String)request.getParameter("db");
					
					List<String> databases = hm.getDatabases();
					
									
					// Form für Query
					out.println("<form name=\"queryForm\" action=\"query.jsp\" method=\"post\"><table>");
						out.println("<tr><td>Query:</td>");
						if (query != null) {
							out.println("<td><textarea name=\"query\" cols=\"90\" rows=\"5\">"+query+"</textarea></td></tr>");
						} else {
							out.println("<td><textarea name=\"query\" cols=\"90\" rows=\"5\"></textarea></td></tr>");
						}
						
						out.println("<tr><td>Datenbank:</td>");
						out.println("<td><select name=\"db\">");
						for(int i=0; i<databases.size(); i++) {
							if ((db != null) && (db.equalsIgnoreCase(databases.get(i)))) { 
								out.println("<option name=\"" + databases.get(i)+"\" value=\""+databases.get(i)+"\" selected>"+databases.get(i)+"</option>");
							} else {
								out.println("<option name=\"" + databases.get(i)+"\" value=\""+databases.get(i)+"\">"+databases.get(i)+"</option>");
							}
						}
						out.println("</select></td></tr>");
						
						out.println("<tr><td><input type=\"submit\" value=\"Absenden\" /></td></tr>");
					out.println("</table></form>");
					
					if (query != null && !query.trim().equals("") && db != null && !db.trim().equals("")) {
					
						hm.switchDatabase(db);
						
						ResultSet rs = hm.executeQuery(query);
						
						if (rs != null) {
						
							// Verarbeite Daten
							ResultSetMetaData rsmd = rs.getMetaData();
							out.println("<table style=\"border-width: 1px; border-style: solid; width: 100%;\">");
							
							// Spaltenkopf
							int columnCount = rsmd.getColumnCount();
							out.println("<tr>");
							
							// Letzte Spalte ist der row_count -> Wird weggelassen, da nicht zu den Daten gehörend
							for(int i=1; i<columnCount; i++) {
								out.println("<td><b>"+rsmd.getColumnName(i)+"</b></td>");
							}
							out.println("</tr>");
							
							// Daten in Tabelle ausgeben
							while(rs.next()) {
								out.println("<tr>");
								
								// Letzte Spalte ist der row_count -> Wird weggelassen
								for(int i=1; i<columnCount; i++) {
									switch(rsmd.getColumnType(i)) {
										case Types.ARRAY: out.println("<td>"+rs.getArray(i)+"</td>"); break;
										
										case Types.INTEGER: out.println("<td>"+rs.getInt(i)+"</td>"); break;
										case Types.TINYINT: out.println("<td>"+rs.getByte(i)+"</td>"); break;
										case Types.SMALLINT: out.println("<td>"+rs.getShort(i)+"</td>"); break;
										case Types.BIGINT: out.println("<td>"+rs.getLong(i)+"</td>"); break;
										
										case Types.VARCHAR:
										case Types.LONGNVARCHAR:
										case Types.CHAR: out.println("<td>" + rs.getString(i) + "</td>"); break;
										
										case Types.BIT:
										case Types.BOOLEAN: out.println("<td>" + rs.getBoolean(i) + "</td>"); break;
										
										case Types.DATE: out.println("<td>" + rs.getDate(i) + "</td>"); break;
										
										case Types.TIME: out.println("<td>" + rs.getTime(i) + "</td>"); break;
										
										case Types.TIMESTAMP: out.println("<td>" + rs.getTimestamp(i) + "</td>"); break;
										
										case Types.REAL:
										case Types.FLOAT: out.println("<td>" + rs.getFloat(i) + "</td>"); break;
										
										case Types.DOUBLE: out.println("<td>" + rs.getDouble(i) + "</td>"); break;
										
										case Types.BINARY:
										case Types.VARBINARY: out.println("<td>" + rs.getBinaryStream(i) + "</td>"); break;
										
										default: out.println("<td>" + rs.getString(i) + "</td>");							
									}
								}
								out.println("</tr>");
							}
							
							
							out.println("</table>");
						} else {
							out.println("Fehler in der Abfrage '" + query+"'.");
						}
												
					}
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
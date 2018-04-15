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
				<li><a href="data.jsp"><b>Daten</b></a></li>
				<li><a href="query.jsp">Abfragen</a></li>
				<li><a href="users.jsp">Benutzerverwaltung</a></li>
			</ul>
		</div>

		<div id="content">
			<%
				HiveManager hm = new HiveManager();
				boolean connected = hm.connect("single", "10000", "hduser",
						"hduser");
				if (connected) {

					// Wurde bereits eine Datenbank und eine Tabelle gewählt?
					String db = (String) request.getParameter("db");
					String table = (String) request.getParameter("table");

					// Paging benötigt?
					String from = (String) request.getParameter("from");

					if (db != null && !db.trim().equals("") && table != null
							&& !table.trim().equals("")) {

						hm.switchDatabase(db);

						ResultSet rs;
						if (from != null) {
							rs = hm.executeQuery("SELECT * FROM (SELECT *, auto_inc() as row_count FROM "
									+ table + ") A WHERE A.row_count >= " + from + " AND A.row_count < "
									+ (Integer.parseInt(from) + HiveManager.PAGING_SIZE) + " LIMIT " + HiveManager.PAGING_SIZE);
						} else {
							rs = hm.executeQuery("SELECT * FROM (SELECT *, auto_inc() as row_count FROM "
									+ table + ") A WHERE A.row_count < " + HiveManager.PAGING_SIZE + " LIMIT " + HiveManager.PAGING_SIZE);
						}

						// Paging-Buttons
						out.println("<table style=\"width: 100%;\">");
						if (from != null && !from.equals("0")) {
							int from_paging = Integer.parseInt(from)
									- HiveManager.PAGING_SIZE >= 0 ? Integer
									.parseInt(from) - HiveManager.PAGING_SIZE : 0;
							int to_paging = Integer.parseInt(from)
									+ HiveManager.PAGING_SIZE;
							out.println("<tr><td style=\"text-align: left;\"><a href=\"data.jsp?db="
									+ db + "&table=" + table + "&from=" + from_paging
									+ "\"><<</a></td><td style=\"text-align: center;\">Daten aus <b>"
									+ db + "." + table
									+ "</b></td><td style=\"text-align: right;\"><a href=\"data.jsp?db="
									+ db + "&table=" + table + "&from=" + to_paging + "\">>></a></td></tr>");
						} else {
							out.println("<tr><td></td><td style=\"text-align: center;\">Daten aus <b>"
									+ db + "." + table
									+ "</b></td><td style=\"text-align: right;\"><a href=\"data.jsp?db="
									+ db + "&table=" + table + "&from=" + HiveManager.PAGING_SIZE
									+ "\">>></a></td></tr>");
						}
						out.println("</table>");

						// Verarbeite Daten
						ResultSetMetaData rsmd = rs.getMetaData();
						out.println("<table style=\"border-width: 1px; border-style: solid; width: 100%;\">");

						// Spaltenkopf
						int columnCount = rsmd.getColumnCount();
						out.println("<tr>");

						// Letzte Spalte ist der row_count -> Wird weggelassen, da nicht zu den Daten gehörend
						for (int i = 1; i < columnCount; i++) {
							out.println("<td><b>" + rsmd.getColumnName(i)
									+ "</b></td>");
						}
						out.println("</tr>");

						// Daten in Tabelle ausgeben
						while (rs.next()) {
							out.println("<tr>");

							// Letzte Spalte ist der row_count -> Wird weggelassen
							for (int i = 1; i < columnCount; i++) {
								switch (rsmd.getColumnType(i)) {
								case Types.ARRAY:
									out.println("<td>" + rs.getArray(i) + "</td>");
									break;

								case Types.INTEGER:
									out.println("<td>" + rs.getInt(i) + "</td>");
									break;
								case Types.TINYINT:
									out.println("<td>" + rs.getByte(i) + "</td>");
									break;
								case Types.SMALLINT:
									out.println("<td>" + rs.getShort(i) + "</td>");
									break;
								case Types.BIGINT:
									out.println("<td>" + rs.getLong(i) + "</td>");
									break;

								case Types.VARCHAR:
								case Types.LONGNVARCHAR:
								case Types.CHAR:
									out.println("<td>" + rs.getString(i) + "</td>");
									break;

								case Types.BIT:
								case Types.BOOLEAN:
									out.println("<td>" + rs.getBoolean(i) + "</td>");
									break;

								case Types.DATE:
									out.println("<td>" + rs.getDate(i) + "</td>");
									break;

								case Types.TIME:
									out.println("<td>" + rs.getTime(i) + "</td>");
									break;

								case Types.TIMESTAMP:
									out.println("<td>" + rs.getTimestamp(i)
											+ "</td>");
									break;

								case Types.REAL:
								case Types.FLOAT:
									out.println("<td>" + rs.getFloat(i) + "</td>");
									break;

								case Types.DOUBLE:
									out.println("<td>" + rs.getDouble(i) + "</td>");
									break;

								case Types.BINARY:
								case Types.VARBINARY:
									out.println("<td>" + rs.getBinaryStream(i)
											+ "</td>");
									break;

								default:
									out.println("<td>" + rs.getString(i) + "</td>");
								}
							}
							out.println("</tr>");
						}
						out.println("</table>");

						// Paging-Buttons
						out.println("<table style=\"width: 100%;\">");
						if (from != null && !from.equals("0")) {
							int from_paging = Integer.parseInt(from)
									- HiveManager.PAGING_SIZE >= 0 ? Integer
									.parseInt(from) - HiveManager.PAGING_SIZE : 0;
							int to_paging = Integer.parseInt(from)
									+ HiveManager.PAGING_SIZE;
							out.println("<tr><td style=\"text-align: left;\"><a href=\"data.jsp?db="
									+ db + "&table=" + table + "&from=" + from_paging
									+ "\"><<</a></td><td style=\"text-align: center;\">Daten aus <b>"
									+ db + "." + table
									+ "</b></td><td style=\"text-align: right;\"><a href=\"data.jsp?db="
									+ db + "&table=" + table + "&from=" + to_paging + "\">>></a></td></tr>");
						} else {
							out.println("<tr><td></td><td style=\"text-align: center;\">Daten aus <b>"
									+ db + "." + table
									+ "</b></td><td style=\"text-align: right;\"><a href=\"data.jsp?db="
									+ db + "&table=" + table + "&from=" + HiveManager.PAGING_SIZE
									+ "\">>></a></td></tr>");
						}
						out.println("</table>");

					} else {
						out.println("Bitte wählen Sie eine Datenbank und eine Tabelle.");
					}
				} else {
					out.println("Konnte keine Verbindung zu Hive herstellen!");
				}
				hm.disconnect();
			%>

		</div>
		<div id="footer">
			Big-Data in der Praxis | <a href="www.jofre.de">www.jofre.de</a>
		</div>
	</div>
</body>
</html>
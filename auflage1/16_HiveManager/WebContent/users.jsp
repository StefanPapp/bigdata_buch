<%@page import="de.jofre.hivemanager.StringListHelper"%>
<%@page import="de.jofre.hivemanager.HiveManager"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map.Entry"%>
<%@page import="java.util.Iterator"%>
<%@page import="de.jofre.hivemanager.HiveUserManager"%>
<%@page import="java.sql.Types"%>
<%@page import="java.sql.ResultSetMetaData"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.util.ArrayList"%>
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
				<li><a href="query.jsp">Abfragen</a></li>
				<li><a href="users.jsp"><b>Benutzerverwaltung</b></a></li>
			</ul>
		</div>



		<div id="content">
			<%
				HiveUserManager hum = new HiveUserManager();
				HiveManager hm = new HiveManager();
				boolean connected = hm.connect("single", "10000", "hduser", "hduser");
				if (connected) {				
				
				// Parameter verarbeiten
				String userToDelete = (String)request.getParameter("delete");
				if (userToDelete != null) {
					boolean deleteResult = hum.removeUser(userToDelete);
					if (deleteResult) {
						response.sendRedirect("users.jsp");
					} else {
						out.println("Benutzer " + userToDelete + " konnte nicht gelöscht werden!");
					}
				}
				
				String newUser = (String)request.getParameter("newuser");
				String newPassword = (String)request.getParameter("newpassword");
				if (newUser != null && !newUser.trim().equals("") && newPassword != null && !newPassword.trim().equals("")) {
					boolean addResult = hum.addUser(newUser, newPassword);
					if (addResult) {
						response.sendRedirect("users.jsp");
					} else {
						out.println("Benutzer " + newUser + " konnte nicht hinzugefügt werden!");
					}
				}
				
				// Benutzerliste
				HashMap<String,String> users = hum.getUsers();
				out.println("<table style=\"border-width: 1px; border-style: solid; width: 100%;\">");
				out.println("<tr><td><b>Benutzername</b></td><td><b>Passwort</b></td><td><b>Rollen</b></td><td><b>Aktion</b></td></tr>");
				Iterator<Entry<String,String>> it = users.entrySet().iterator();
				while (it.hasNext()) {
					Entry<String,String> entry = (Entry<String,String>) it.next();
					List<String> roles = hm.getRolesOfUser(entry.getKey());
					String rolesAsString = StringListHelper.joinStringList(roles);
					out.println("<tr><td>"+entry.getKey()+"</td><td>****</td> <td>"+rolesAsString+"</td><td><a href=\"users.jsp?delete="+entry.getKey()+"\">Löschen</a></td></tr>");
					it.remove();
				}				
				
				// Neuen Benutzer hinzufügen
				out.println("<form name=\"addUsersForm\" action=\"users.jsp\" method=\"post\">");
				out.println("<table style=\"border-width: 1px; border-style: solid; width: 100%;\"><tr><td>Neuer Benutzer: <input type=\"text\" name=\"newuser\" /></td>");
				out.println("<td>Passwort:<input type=\"password\" name=\"newpassword\" /></td><td><input type=\"submit\" value=\"Hinzufügen\" /></td></tr></table></form>");
				} else {
					out.println("Konnte keine Verbindung zu Hive herstellen.");
				}
			%>

		</div>
		<div id="footer">
			Big-Data in der Praxis | <a href="www.jofre.de">www.jofre.de</a>
		</div>
	</div>
</body>
</html>
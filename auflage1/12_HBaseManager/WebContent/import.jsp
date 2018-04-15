<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Arrays"%>
<%@page import="org.apache.hadoop.yarn.security.AdminACLsManager"%>
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
<title>HBase-Manager</title>
</head>
<body>
	<%
		HBaseManager manager = new HBaseManager("single");
		List<String> tables = manager.getTables();				 
	 %>
	<div id="container">
		<div id="header">
			<h1>HBase-Manager</h1>
		</div>
		
		<div id="navigation">
			<ul>
				<li><a href="index.jsp">Anzeigen</a></li>
				<li><a href="edit.jsp">Bearbeiten</a></li>
				<li><a href="search.jsp">Suchen</a></li>
				<li><a href="import.jsp"><b>Daten importieren</b></a></li>
			</ul>
		</div>
		
	<%
		// Verarbeite Parameter
		String table = (String)request.getParameter("table");
		String newvalues = (String)request.getParameter("newvalues");
		String columnfamily = (String)request.getParameter("columnfamily");
		if ((table != null) && (newvalues != null) && (columnfamily != null)) {
			manager.importData(table, columnfamily, newvalues);
			response.sendRedirect("import.jsp");
		}
	 %>
	
		<div id="content">
		
			<%
				// Tabelle auswählen
				out.println("<form name=\"importForm\" action=\"import.jsp\" method=\"post\"><table><tr>");
				out.println("<td>Tabelle:</td><td><select name=\"table\">");
				for(int i=0; i<tables.size(); i++) {
					if ((table != null) && (table.equalsIgnoreCase(tables.get(i)))) { 
						out.println("<option name=\"" + tables.get(i)+"\" value=\""+tables.get(i)+"\" selected>"+tables.get(i)+"</option>");
					} else {
						out.println("<option name=\"" + tables.get(i)+"\" value=\""+tables.get(i)+"\">"+tables.get(i)+"</option>");
					}
				}
				out.println("</select></td></tr>");
				
				out.println("<tr><td>Existierende Column-Family:</td><td><input type=\"text\" name=\"columnfamily\" /></td></tr>");
				out.println("<tr><td>Werte mit Header:</td><td><textarea name=\"newvalues\" cols=\"50\" rows=\"20\"></textarea></td></tr>");
				out.println("<tr><td><input type=\"submit\" value=\"Import\" /></td><td></td></tr>");
				out.println("</table></form>");
				
			 %>			
			

			</div>
		<div id="footer">Big-Data in der Praxis | <a href="www.jofre.de">www.jofre.de</a></div>
	</div>
</body>
</html>
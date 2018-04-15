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
				<li><a href="edit.jsp"><b>Bearbeiten</b></a></li>
				<li><a href="search.jsp">Suchen</a></li>
				<li><a href="import.jsp">Daten importieren</a></li>
			</ul>
		</div>
		
	<%
	
		// Verarbeite Parameter
		String table = (String)request.getParameter("table");
		String edit = (String)request.getParameter("edit");
		String newcf = (String)request.getParameter("newcf");
		String newtable = (String)request.getParameter("newtable");
		
		if ((edit != null) && (table != null)) {
			
			if (manager.tableExists(table)) {
				if (edit.equalsIgnoreCase("disable")) {
					manager.disableTable(table);
				} else if (edit.equalsIgnoreCase("enable")) {
					manager.enableTable(table);
				} else if (edit.equalsIgnoreCase("delete")) {
					manager.deleteTable(table);
				} else if (edit.equalsIgnoreCase("clear")) {
					manager.emptyTable(table);
				}
				response.sendRedirect("edit.jsp");
			} else {
				out.println("Die zu verändernde Tabelle '"+table+"' existiert nicht.");
			}
		}
		
		if ((newcf != null) && (table != null)) {
			manager.addColumnFamily(table, newcf);
			response.sendRedirect("edit.jsp");
		}
		
		if ((newtable != null)) {
			manager.addTable(newtable);
			response.sendRedirect("edit.jsp");
		}
	
	 %>
	
		<div id="content">
		
			<%
				out.println("<table style=\"border-width: 1px; border-style: solid; width: 100%;\">");
				for(int i=0; i<tables.size(); i++) {
					out.println("<tr>");
					out.println("<td style=\"border: 1px solid black;\">" + tables.get(i) +"</td>");
					if (manager.isTableEnabled(tables.get(i))) {
						out.println("<td style=\"border: 1px solid black;\"><a href=\"edit.jsp?table="+tables.get(i)+"&edit=disable\">Disable</a></td>");
						out.println("<td style=\"border: 1px solid black;\">Delete (disable first)</td>");
					} else {
						out.println("<td style=\"border: 1px solid black;\"><a href=\"edit.jsp?table="+tables.get(i)+"&edit=enable\">Enable</a></td>");
						out.println("<td style=\"border: 1px solid black;\"><a href=\"edit.jsp?table="+tables.get(i)+"&edit=delete\">Delete</a></td>");
					}
					
					// Liste ColumnFamilies auf
					List<String> cfs = manager.getColumnFamilies(tables.get(i));
					StringBuilder sb = new StringBuilder();
					for(int j=0; j<cfs.size(); j++) {
						sb.append(cfs.get(j));
						if (j<cfs.size()-1) {
							sb.append(", ");
						}
					}
					
					out.println("<td style=\"border: 1px solid black;\">"+sb.toString()+" "+
						"<form action=\"edit.jsp\">"+
						"<input type=\"text\" name=\"newcf\" value=\"Neue Column-Family\""+
							" onblur=\"if (this.value == '') {this.value = 'Neue Column-Family';}\""+
							" onfocus=\"if (this.value == 'Neue Column-Family') {this.value = '';}\""+
						" />"+
						"<input type=\"hidden\" name=\"table\" value=\""+tables.get(i)+"\" />"+
						"<input type=\"submit\" value=\"Add\" /></form></td>");
						
					out.println("<td style=\"border: 1px solid black;\"><a href=\"edit.jsp?table="+tables.get(i)+"&edit=clear\">Clear</a></td>");
					
					out.println("</tr>");
				}
				out.println("</table><br />");

				// Erzeugen einer neuen Tabelle
				out.println("<form action=\"edit.jsp\"><table><tr>"+
					"<td>Neue Tabelle: <input type=\"text\" name=\"newtable\" /></td>" +
					"<td><input type=\"submit\" value=\"Erzeugen\" /></td>"+
					"</tr></table></form>");
				
			 %>			
			

			</div>
		<div id="footer">Big-Data in der Praxis | <a href="www.jofre.de">www.jofre.de</a></div>
	</div>
</body>
</html>
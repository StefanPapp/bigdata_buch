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
				<li><a href="search.jsp"><b>Suchen</b></a></li>
				<li><a href="import.jsp">Daten importieren</a></li>
			</ul>
		</div>
		
	<%
		// Verarbeite Parameter
		String table = (String)request.getParameter("table");
		String rowkey = (String)request.getParameter("rowkey");
		String columnfamily = (String)request.getParameter("columnfamily");
		String column = (String)request.getParameter("column");
		String value = (String)request.getParameter("value");
		String checked = (String)request.getParameter("checked");
		
		String rowkeyselected = "";
		if ((checked == null) || ((checked != null) && (checked.equalsIgnoreCase("rowkey")))) {
			rowkeyselected = "checked";
		}
		String columnfamilyselected = "";
		if ((checked != null) && (checked.equalsIgnoreCase("columnfamily"))) {
			columnfamilyselected = "checked";
		}
		String prefilledrowkey = "";
		if (rowkey != null) prefilledrowkey = rowkey;
		String prefilledcolumnfamily = "";
		if (columnfamily != null) prefilledcolumnfamily = columnfamily;
		String prefilledcolumn = "";
		if (column != null) prefilledcolumn = column;
		String prefilledvalue = "";
		if (value != null) prefilledvalue = value;
	 %>
	
		<div id="content">
		
			<%
				// Tabelle auswählen
				out.println("<form name=\"searchForm\" action=\"search.jsp\" method=\"post\"><table><tr>");
				out.println("<td>Tabelle:</td><td><select name=\"table\">");
				for(int i=0; i<tables.size(); i++) {
					if ((table != null) && (table.equalsIgnoreCase(tables.get(i)))) { 
						out.println("<option name=\"" + tables.get(i)+"\" value=\""+tables.get(i)+"\" selected>"+tables.get(i)+"</option>");
					} else {
						out.println("<option name=\"" + tables.get(i)+"\" value=\""+tables.get(i)+"\">"+tables.get(i)+"</option>");
					}
				}
				out.println("</select></td></tr>");
				
				out.println("<tr><td><input type=\"radio\" name=\"checked\" value=\"rowkey\" "+rowkeyselected+"/></td><td>Row key:</td><td><input type=\"text\" name=\"rowkey\" value=\""+prefilledrowkey+"\" /></td><td></td><td></td></tr>");
				out.println("<tr><td><input type=\"radio\" name=\"checked\" value=\"columnfamily\" "+columnfamilyselected+" /></td><td>Column family:</td><td><input type=\"text\" name=\"columnfamily\" value=\""+prefilledcolumnfamily+"\" /></td>" +
					"<td>Column:</td><td><input type=\"text\" name=\"column\" value=\""+prefilledcolumn+"\" /></td>"+
					"<td>Value:</td><td><input type=\"text\" name=\"value\" value=\""+prefilledvalue+"\" /></td>"+
				"</tr>");
				out.println("<tr><td><input type=\"submit\" value=\"Suchen\"/></td><td></td></tr>");
				out.println("</form></table>");
				
				// Suche ausführen
				if (checked != null) {
					List<List<Cell>> rows = null;
					if (checked.equalsIgnoreCase("rowkey")) {
						// Einfacher zu handhaben
						rows = new ArrayList<List<Cell>>();
						List<Cell> r = manager.getRow(table, rowkey);
						if (r != null) rows.add(r);
					} else if (checked.equalsIgnoreCase("columnfamily")) {
						rows = manager.search(table, columnfamily, column, value);
					}
					
					// Zähle die maximale Spaltenzahl pro Resultset
					int max = 0;
					for(int i=0; i<rows.size(); i++) {
						if (rows.get(i).size() > max) {
							max = rows.get(i).size();
						}
					}
					
					// Zeichne die Werte in einer Tabelle
					out.println("<table style=\"border-width: 1px; border-style: solid; width: 100%; \">");
					for(int i=0; i<rows.size(); i++) {
						String id = "";
						out.println("<tr>");
						
						// Lese alle Zellen aus						
						for(int j=0; j<rows.get(i).size(); j++) {
						
							// Gebe vor der ersten Spalte den Row-Key aus
							if (j==0) {
								out.println("<td style=\"border: 1px solid black; widht: " + (100 / (rows.get(i).size()))+ "%;\">" + Bytes.toString(rows.get(i).get(j).getRow())+"</td>");
								id = Bytes.toString(rows.get(i).get(j).getRow());
							}
							
							// ... dann jede Zelle samt CF
							out.println("<td style=\"border: 1px solid black; widht: " + (100 / (rows.get(i).size()))+ "%;\">" + Bytes.toString(rows.get(i).get(j).getFamily()) + ":" + Bytes.toString(rows.get(i).get(j).getQualifier()) + 
							" = " + Bytes.toString(rows.get(i).get(j).getValue()) + "</td>");
						}
						
						// Fülle die letzten Spalten auf
						for(int j=rows.get(i).size(); j<max; j++) {
							out.println("<td style=\"border: 1px solid black; widht: " + (100 / (rows.get(i).size()))+ "%;\"></td>");
						}
	
					}
					out.println("</table>");
				}
			 %>			
			

			</div>
		<div id="footer">Big-Data in der Praxis | <a href="www.jofre.de">www.jofre.de</a></div>
	</div>
</body>
</html>
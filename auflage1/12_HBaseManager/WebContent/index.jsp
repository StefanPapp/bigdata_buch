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

<script type="text/javascript">
function validateNewColumn()
{
	var columnfamily=document.forms["newColumnForm"]["attrib_newcolumnfamily"].value;
	var newcolumn=document.forms["newColumnForm"]["attrib_newcolumn"].value;
	var newvalue=document.forms["newColumnForm"]["attrib_newvalue"].value;
	
	if (columnfamily==null || columnfamily=="") {
  		alert("Bitte geben Sie eine Column-Family für die neue Spalte an.");
  		return false;
  	}
	if (newcolumn==null || newcolumn=="") {
  		alert("Bitte geben Sie einen Namen für die neue Spalte an.");
  		return false;
  	}
	if (newvalue==null || newvalue=="") {
  		alert("Bitte geben Sie einen Wert an.");
  		return false;
  	}  	
}

function validateNewDataset()
{
	var newkey=document.forms["newDataSetForm"]["newkey"].value;
	var columnfamily=document.forms["newDataSetForm"]["columnfamily"].value;
	var newvalue=document.forms["newDataSetForm"]["newvalue"].value;
	var newcolumn=document.forms["newDataSetForm"]["newcolumn"].value;
	
	if (newkey==null || newkey=="") {
  		alert("Bitte geben Sie einen Schlüssel für den neuen Datensatz an.");
  		return false;
  	}
	if (columnfamily==null || columnfamily=="") {
  		alert("Bitte geben Sie eine Column-Family für den neuen Datensatz an.");
  		return false;
  	}
	if (newcolumn==null || newcolumn=="") {
  		alert("Bitte geben Sie einen Namen für die neue Spalte an.");
  		return false;
  	}  	
	if (newvalue==null || newvalue=="") {
  		alert("Bitte geben Sie einen Wert für den neuen Datensatz an.");
  		return false;
  	}
}
</script>
</head>
<body>
	<%
		HBaseManager manager = new HBaseManager("single");
		List<String> tables = manager.getTables();
		
		// Ist eine Tabelle selektiert?
		String currentTable = (String)request.getParameter("table");
		if (currentTable == null) {
			if (tables.size() > 0) {
				currentTable = tables.get(0);
			}
		}
		
		// Beginnen wir die Zeilen ab einem bestimmten Key zu lesen?
		String startRow = (String)request.getParameter("startRow");
		
		// Soll eine Zeile gelöscht werden?
		String delete = (String)request.getParameter("delete");
		if ((currentTable != null) && (delete != null)) {
			manager.deleteRow(currentTable, delete);
			response.sendRedirect("index.jsp?table="+currentTable);
		}
		
		// Neues Attribut hinzufügen?
		String attrib_newcolumnfamily = (String)request.getParameter("attrib_newcolumnfamily");
		String attrib_newcolumn = (String)request.getParameter("attrib_newcolumn");
		String attrib_newvalue = (String)request.getParameter("attrib_newvalue");
		String attrib_rowkey = (String)request.getParameter("attrib_rowkey");
		if ((attrib_newcolumnfamily != null) && (attrib_newcolumn != null) && (attrib_newvalue != null)) {
			manager.add(currentTable, attrib_rowkey, attrib_newcolumnfamily, attrib_newcolumn, attrib_newvalue);
			response.sendRedirect("index.jsp?table="+currentTable);
		}
		
		// Neuen Datensatz hinzufügen?
		String newkey = (String)request.getParameter("newkey");
		String newcolumnfamily = (String)request.getParameter("newcolumnfamily");
		String newcolumn = (String)request.getParameter("newcolumn");
		String newvalue = (String)request.getParameter("newvalue");
		if ((newkey != null) && (newcolumnfamily != null) && (newcolumn != null) && (newvalue != null)) {
			manager.add(currentTable, newkey, newcolumnfamily, newcolumn, newvalue);
			response.sendRedirect("index.jsp?table="+currentTable);
		}
				 
	 %>
	<div id="container">
		<div id="header">
			<h1>HBase-Manager</h1>
		</div>
		
		<div id="navigation">
			<ul>
				<li><a href="index.jsp"><b>Anzeigen</b></a></li>
				<li><a href="edit.jsp">Bearbeiten</a></li>
				<li><a href="search.jsp">Suchen</a></li>
				<li><a href="import.jsp">Daten importieren</a></li>
			</ul>
		</div>
		
	
	
		<div id="content">
		
			<%
					
				// Tabelle auswählen
				out.println("Tabelle: <form name=\"pickTableForm\" action=\"index.jsp\"><select name=\"table\" method=\"get\">");
				for(int i=0; i<tables.size(); i++) {
					if ((currentTable != null) && (currentTable.equalsIgnoreCase(tables.get(i)))) {
						out.println("<option name=\"" + tables.get(i)+"\" value=\""+tables.get(i)+"\" selected>"+tables.get(i)+"</option>");
					} else {
						out.println("<option name=\"" + tables.get(i)+"\" value=\""+tables.get(i)+"\">"+tables.get(i)+"</option>");
					}
				}
				out.println("</select>");
				out.println("<input type=\"submit\" value=\"Auswählen\"/>");
				out.println("</form>");

				// Beginn der Darstellung
				if (currentTable == null) {
					out.println("<h2>Tabelleninspektor</h2>");
					out.println("Es sind keine Tabellen in Ihrer HBase-Instanz vorhanden!");
				} else {
					out.println("<h2>" + currentTable + "</h2>");
					
					// Auslesen der Zeilen
					List<List<Cell>> rows = manager.getRows(currentTable, startRow, manager.getFetchSize());
					
					// ID der letzten Zeile wir gelesen, um dorthin navigieren zu können
					String lastRow = "";
					if ((rows != null) && (rows.size() > 0) && (rows.get(rows.size()-1).size() > 0)) {
						lastRow = Bytes.toString(rows.get(rows.size()-1).get(0).getRow());
					}
					
					// Navigator zeichnen
					if (startRow == null) {
						out.println("<table style=\"width:100%;\"><tr><td style=\"text-align: left;\"></td><td style=\"text-align: right;\">"+
						"<a href=\"index.jsp?table="+currentTable+"&startRow="+lastRow+"\">></a></td></tr></table>");
					} else {
						out.println("<table style=\"width:100%;\"><tr>"+
						"<td style=\"text-align: left;\"><a href=\"index.jsp?table="+currentTable+"\"><<</a></td>"+
						"<td style=\"text-align: right;\"><a href=\"index.jsp?table="+currentTable+"&startRow=" + lastRow + "\">></a></td>"+
						"</tr></table>");
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
								out.println("<td style=\"border: 1px solid black; widht: " + (100 / (rows.get(i).size()+2))+ "%;\">" + Bytes.toString(rows.get(i).get(j).getRow())+"</td>");
								id = Bytes.toString(rows.get(i).get(j).getRow());
							}
							
							// ... dann jede Zelle samt CF
							out.println("<td style=\"border: 1px solid black; widht: " + (100 / (rows.get(i).size()+2))+ "%;\">" + Bytes.toString(rows.get(i).get(j).getFamily()) + ":" + Bytes.toString(rows.get(i).get(j).getQualifier()) + 
							" = " + Bytes.toString(rows.get(i).get(j).getValue()) + "</td>");
						}
						
						// Fülle die letzten Spalten auf
						for(int j=rows.get(i).size(); j<max; j++) {
							out.println("<td style=\"border: 1px solid black; widht: " + (100 / (rows.get(i).size()+2))+ "%;\"></td>");
						}
						
						// Neues Attribut
						out.println("<td style=\"border: 1px solid black; widht: " + (100 / (rows.get(i).size()+2))+ "%;\">"+
							"<form action=\"index.jsp\" name=\"newColumnForm\" onsubmit=\"return validateNewColumn()\" method=\"post\">"+
							"<input type=\"text\" name=\"attrib_newcolumnfamily\" value=\"Column Family\" style=\"width: 90%;\""+
								" onblur=\"if (this.value == '') {this.value = 'Column Family';}\""+
								" onfocus=\"if (this.value == 'Column Family') {this.value = '';}\""+
							 "/><br>"+
							"<input type=\"text\" name=\"attrib_newcolumn\" value=\"Neue Spalte\" style=\"width: 90%;\""+
								" onblur=\"if (this.value == '') {this.value = 'Neue Spalte';}\""+
								" onfocus=\"if (this.value == 'Neue Spalte') {this.value = '';}\""+ 
							"/><br>"+
							"<input type=\"text\" name=\"attrib_newvalue\" value=\"Neuer Wert\" style=\"width: 90%;\""+ 
								" onblur=\"if (this.value == '') {this.value = 'Neuer Wert';}\""+
								" onfocus=\"if (this.value == 'Neuer Wert') {this.value = '';}\""+ 
							"/>"+
							"<input type=\"hidden\" name=\"attrib_rowkey\" value=\""+id+"\" /><br>"+
							"<input type=\"hidden\" name=\"table\" value=\""+currentTable+"\" />"+
							"<input type=\"submit\" value=\"Add\" style=\"width: 90%;\" />"+
							"</form>"+
							"</td>");
							
						// Delete-Button
						out.println("<td style=\"border: 1px solid black; widht: " + (100 / (rows.get(i).size()+2))+ "%;\"><a href=\"index.jsp?table=" + currentTable + "&delete=" + id +"\">x</a></td>");
						out.println("</tr>");	
					}
					out.println("</table>");
					
					// Navigator zeichnen
					if (startRow == null) {
						out.println("<table style=\"width:100%;\"><tr><td style=\"text-align: left;\"></td><td style=\"text-align: right;\">"+
						"<a href=\"index.jsp?table="+currentTable+"&startRow="+lastRow+"\">></a></td></tr></table>");
					} else {
						out.println("<table style=\"width:100%;\"><tr>"+
						"<td style=\"text-align: left;\"><a href=\"index.jsp?table="+currentTable+"\"><<</a></td>"+
						"<td style=\"text-align: right;\"><a href=\"index.jsp?table="+currentTable+"&startRow=" + lastRow + "\">></a></td>"+
						"</tr></table>");
					}		
					
					// Neuer Datensatz
					out.println("<br>Neuen Datensatz hinzufügen:<br>");
					out.println("<form action=\"index.jsp\" name=\"newDataSetForm\" onsubmit=\"return validateNewDataset()\" method=\"post\">"+
						"<table style=\"border-width: 1px; border-style: solid; width: 100%; \">"+
						"<td style=\"border: 1px solid black;\">Row key:"+
						"<input type=\"text\" name=\"newkey\" /></td>"+
						"<td style=\"border: 1px solid black;\">Column Family:"+
						"<input type=\"text\" name=\"newcolumnfamily\" /></td>"+
						"<td style=\"border: 1px solid black;\">Neue Spalte:"+
						"<input type=\"text\" name=\"newcolumn\" /></td>"+
						"<td style=\"border: 1px solid black;\">Neuer Wert:"+
						"<input type=\"text\" name=\"newvalue\" />"+
						"<input type=\"hidden\" name=\"table\" value=\"" +currentTable+"\" /></td>"+	
						"<td style=\"border: 1px solid black;\"><input type=\"submit\" value=\"Add\" /></td>"+
						"</table>"+
					"</form>");
				}
			 %>

			</div>
		<div id="footer">Big-Data in der Praxis | <a href="www.jofre.de">www.jofre.de</a></div>
	</div>
</body>
</html>
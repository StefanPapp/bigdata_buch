<%@page import="java.io.File"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="de.jofre.hadoopcontroller.Helper"%>
<%@page import="java.net.URLDecoder"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="org.apache.hadoop.fs.FileStatus"%>
<%@page import="org.apache.hadoop.yarn.api.records.NodeReport"%>
<%@page import="de.jofre.hadoopcontroller.YarnCluster"%>
<%@page import="java.util.ArrayList"%>
<%@page import="org.apache.hadoop.mapred.ClusterStatus"%>
<%@page import="java.util.List"%>
<%@page import="de.jofre.hadoopcontroller.HDFS"%>
<%@page import="de.jofre.hadoopcontroller.HadoopProperties"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="shortcut icon" href="img/favicon.ico" type="image/x-icon" /> 
<link rel="stylesheet" href="css/style.css" type="text/css" />
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Hadoop-Monitor - HDFS Explorer</title>
</head>
<body>
	<div id="container">
		<div id="header">
			<h1>Hadoop-Monitor</h1>
		</div>
		
		<div id="navigation">
			<ul>
				<li><a href="hdfs.jsp"><b>HDFS Explorer</b></a></li>
				<li><a href="yarn.jsp">Yarn-Cluster</a></li>
				<li><a href="apps.jsp">Laufende Anwendungen</a></li>
			</ul>
		</div>
		
		<script type="text/javascript">
			var file_selected = false;
			function showNoFile() {
			    if(!file_selected) {
			    	alert('Es wurde keine Datei zum Hochladen ausgewählt.');
			    	return false;
			    } else {
			    	return true;
			    }
			}
		</script>
		
		<div id="content">
			<h2>HDFS Explorer</h2>
		
			<%
			
				// Verbindung zum HDFS initiieren
				HDFS hdfsc = new HDFS();
				hdfsc.init(HadoopProperties.get("hdfs_address"), HadoopProperties.get("hadoop_user"));
			
				// Auslesen des aktuellen Verzeichnisses in dem wir uns im
				// HDFS befinden.
				String currentDir = (String)request.getParameter("currentDir");
				if ((currentDir == null) || (currentDir.trim().equals(""))) {
					currentDir = HadoopProperties.get("hdfs_address") + '/';
				} else {
					currentDir = URLDecoder.decode(currentDir, "UTF-8");
				}
				
				// Soll ein neuer Ordner angelegt werden?
				String newDir = (String)request.getParameter("newFolder");
				if (newDir != null) {
					boolean creationResult = hdfsc.createNewFolder(currentDir + "/" + newDir);
					if (creationResult) {
						out.println("Neuer Ordner '"+ currentDir + "/" + newDir +"' angelegt.<br><br>");
					} else {
						out.println("<font color=\"#FF0000\">Erstellung des Ordners '"+currentDir + "/" + newDir + "' fehlgeschlagen.</font><br><br>");
					}
				}
				
				// Soll eine Datei oder ein Ordner gelöscht werden?
				String deleteEntitiy = (String)request.getParameter("delete");
				if (deleteEntitiy != null) {
					boolean deletionResult = hdfsc.deleteFileOrFolder(deleteEntitiy);
					if (deletionResult) {
						out.println("Datei/Ordner '"+ deleteEntitiy +"' erfolgreich gelöscht.<br><br>");
					} else {
						out.println("<font color=\"#FF0000\">Datei/Ordner '"+ deleteEntitiy + "' konnte nicht gelöscht werden.</font><br><br>");
					}
				}
				
				String currentDirEncoded = URLEncoder.encode(currentDir, "UTF-8");
				out.println("Aktueller Pfad: "+Helper.hdfsPathToLinks(currentDir)+"<br><br>");
				
				// Buttons zum Anlegen von Ordnern und löschen von Ordnern und zum Hochladen von Dateien
				out.println("<table><tr>");
				out.println("<td><form action=\"hdfs.jsp\">Neuer Ordner: <input name=\"newFolder\" type=\"text\" />  <input type=\"hidden\" name=\"currentDir\" value=\""+currentDirEncoded+"\" />  <input type=\"submit\" value=\"Anlegen\" /></form></td><td>|</td>");
				out.println("<td><form action=\"upload?targetDir="+currentDirEncoded+"\" method=\"post\" enctype=\"multipart/form-data\" onsubmit='return showNoFile();' >Datei hochladen: <input name=\"dataFile\" type=\"file\" id=\"fileChooser\" onchange=\"file_selected = true;\"/><input type=\"submit\" value=\"Hochladen\" /></form></td>");
				out.println("</tr></table>");
				
				// Tabelle mit Ordnern und Dateien zeichnen
				out.println("<table style=\"border-width: 1px; border-style: solid; width: 100%;\"><tr><td><b>Name</b></td><td><b>Blockgröße</b></td><td><b>Repliken</b></td><td><b>Datum</b></td><td><b>Löschen</b></td></tr>");
				
				// Datumsformatierung
				DateFormat dfmt = new SimpleDateFormat( "dd.MM.yy hh:mm:ss" );
				
				// Liste alle Dateien und Ordner auf
				List<FileStatus> entries = hdfsc.getEntriesFromDir(currentDir);
				
				if (entries == null) {
					out.println("<font color=\"#FF0000\">Fehler beim Auslesen des Pfades '"+currentDir+"'.</font><br>");
				} else {
					for(int i=0; i<entries.size(); i++) {
						
						Date lastAccess = new Date(entries.get(i).getAccessTime());
						File currentFile = new File(entries.get(i).getPath().toString());
						
						// Ist es ein Ordner? Dann betritt ihn beim Anklicken.
						if (entries.get(i).isDirectory()) {
							String link = URLEncoder.encode(entries.get(i).getPath().toString(), "UTF-8");
							out.println("<tr>");
							out.print("<td><a href=\"hdfs.jsp?currentDir="+link+"\">"+currentFile.getName()+"</a></td>");
							out.print("<td>"+entries.get(i).getBlockSize()+"</td>");
							out.print("<td>"+entries.get(i).getReplication()+"</td>");
							out.print("<td>"+dfmt.format(lastAccess)+"</td>");
							out.print("<td><a href=\"hdfs.jsp?delete=" + URLEncoder.encode(entries.get(i).getPath().toString(), "UTF-8") + "&currentDir="+currentDir+"\" onclick=\"return confirm('"+currentFile.getName()+" wirklich löschen?')\" \">Löschen</a></td>");
							out.print("</tr>");
						}
						
						// Ist es ein Datei? Lade sie durch einen Klick herunter
						if (entries.get(i).isFile()) {
							out.println("<tr>");
							out.print("<td><a href=\"download?dl=" + URLEncoder.encode(entries.get(i).getPath().toString(), "UTF-8") + "\"  target=\"_blank\">"+currentFile.getName()+"</a></td>");
							out.print("<td>"+entries.get(i).getBlockSize()+"</td>");
							out.print("<td>"+entries.get(i).getReplication()+"</td>");
							out.print("<td>"+dfmt.format(lastAccess)+"</td>");
							out.print("<td><a href=\"hdfs.jsp?delete=" + URLEncoder.encode(entries.get(i).getPath().toString(), "UTF-8") + "&currentDir="+currentDir+"\" onclick=\"return confirm('"+currentFile.getName()+" wirklich löschen?')\" \">Löschen</a></td>");
							out.print("</tr>");
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
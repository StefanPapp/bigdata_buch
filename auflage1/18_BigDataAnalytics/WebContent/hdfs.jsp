<%@page import="java.io.File"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="de.jofre.hadoopcontroller.Helper"%>
<%@page import="java.net.URLDecoder"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="org.apache.hadoop.fs.FileStatus"%>
<%@page import="org.apache.hadoop.yarn.api.records.NodeReport"%>
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
<title>Big-Data-Analytics</title>
</head>
<body>
		<div id="container">
			<div id="header">
				<h1>Big-Data-Analytics</h1>
			</div>

			<%@include file="WEB-INF//menu.jsp"%>

			<div id="content">
				<h2>HDFS Explorer</h2>

				<%
				// Verbindung zum HDFS initiieren
				HDFS hdfsc = new HDFS();
				hdfsc.init(HadoopProperties.get("hdfs_address"),
						HadoopProperties.get("hadoop_user"));

				// Auslesen des aktuellen Verzeichnisses in dem wir uns im
				// HDFS befinden.
				String currentDir = (String) request.getParameter("currentDir");
				if ((currentDir == null) || (currentDir.trim().equals(""))) {
					currentDir = HadoopProperties.get("hdfs_address") + '/';
				} else {
					currentDir = URLDecoder.decode(currentDir, "UTF-8");
				}

				String currentDirEncoded = URLEncoder.encode(currentDir, "UTF-8");
				out.println("Aktueller Pfad: " + Helper.hdfsPathToLinks(currentDir)
						+ "<br><br>");

				// Tabelle mit Ordnern und Dateien zeichnen
				out.println("<table style=\"border-width: 1px; border-style: solid; width: 100%;\"><tr><td><b>Name</b></td><td><b>Blockgröße</b></td><td><b>Repliken</b></td><td><b>Datum</b></td></tr>");

				// Datumsformatierung
				DateFormat dfmt = new SimpleDateFormat("dd.MM.yy hh:mm:ss");

				// Liste alle Dateien und Ordner auf
				List<FileStatus> entries = hdfsc.getEntriesFromDir(currentDir);

				if (entries == null) {
					out.println("<font color=\"#FF0000\">Fehler beim Auslesen des Pfades '"
							+ currentDir + "'.</font><br>");
				} else {
					for (int i = 0; i < entries.size(); i++) {

						Date lastAccess = new Date(entries.get(i).getAccessTime());
						File currentFile = new File(entries.get(i).getPath()
								.toString());

						// Ist es ein Ordner? Dann betritt ihn beim Anklicken.
						if (entries.get(i).isDirectory()) {
							String link = URLEncoder.encode(entries.get(i)
									.getPath().toString(), "UTF-8");
							out.println("<tr>");
							out.print("<td><a href=\"hdfs.jsp?currentDir=" + link
									+ "\">" + currentFile.getName() + "</a></td>");
							out.print("<td>" + entries.get(i).getBlockSize()
									+ "</td>");
							out.print("<td>" + entries.get(i).getReplication()
									+ "</td>");
							out.print("<td>" + dfmt.format(lastAccess) + "</td>");
							out.print("</tr>");
						}

						// Ist es ein Datei? Lade sie durch einen Klick herunter
						if (entries.get(i).isFile()) {
							out.println("<tr>");
							out.print("<td><a href=\"hdfs.jsp?show="
									+ URLEncoder.encode(entries.get(i).getPath()
											.toString(), "UTF-8") + "\">"
									+ currentFile.getName() + "</a></td>");
							out.print("<td>" + entries.get(i).getBlockSize()
									+ "</td>");
							out.print("<td>" + entries.get(i).getReplication()
									+ "</td>");
							out.print("<td>" + dfmt.format(lastAccess) + "</td>");
							out.print("</tr>");
						}
					}

					out.println("</table>");
				}
			%>

			<!-- Übernehmen der Daten für die Analyse -->
			<%
				if (request.getParameter("currentinput") != null) {
					out.println("<b>Daten erfolgreich übernommen!</b><br>");
					session.setAttribute("currentinput",
							request.getParameter("currentinput"));
				}
			%>

				<%
				if (request.getParameter("show") != null) {
					out.println("<br>Inhalt von: <i>" + request.getParameter("show") + "</i>:<br>");
				%>
				<!-- Anzeigen des Dateiinhalts  -->
				<form action="hdfs.jsp" method="post">
					<textarea id="text" name="currentinput" cols="105" rows="15"><%
							out.println(hdfsc.getFileContet(request.getParameter("show"),
									50).trim());
					%></textarea>
					<input type="submit" value="Als Daten übernehmen">
				</form>
				<%
				}
			%>

			</div>
			<div id="footer">
				Big-Data in der Praxis | <a href="www.jofre.de">www.jofre.de</a>
			</div>
		</div>
	</div>
</body>
</html>
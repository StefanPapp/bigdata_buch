<%@page import="de.jofre.hadoopcontroller.HadoopProperties"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="org.apache.hadoop.yarn.api.records.ApplicationReport"%>
<%@page import="org.apache.hadoop.yarn.api.records.NodeReport"%>
<%@page import="de.jofre.hadoopcontroller.YarnCluster"%>
<%@page import="java.util.ArrayList"%>
<%@page import="org.apache.hadoop.mapred.ClusterStatus"%>
<%@page import="java.util.List"%>
<%@page import="de.jofre.hadoopcontroller.HDFS"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="shortcut icon" href="img/favicon.ico" type="image/x-icon" /> 
<link rel="stylesheet" href="css/style.css" type="text/css" />
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Hadoop-Monitor - Laufende Anwendungen</title>
</head>
<body>
	<div id="container">
		<div id="header">
			<h1>Hadoop-Monitor</h1>
		</div>
		
		<div id="navigation">
			<ul>
				<li><a href="hdfs.jsp">HDFS Explorer</a></li>
				<li><a href="yarn.jsp">Yarn-Cluster</a></li>
				<li><a href="apps.jsp"><b>Laufende Anwendungen</b></a></li>
			</ul>
		</div>
		
		<div id="content">
			<h2>Laufende Anwendungen</h2>
		
			<%	
			
				DateFormat dfmt = new SimpleDateFormat("dd.MM.yy hh:mm:ss");

				out.println("<br><br><table style=\"width: 100%; border-width: 1px; border-style: solid;\">");
				out.print("<tr>");
				out.print("<td><b>Name</b></td>");
				out.print("<td><b>Typ</b></td>");
				out.print("<td><b>Host</b></td>");
				out.print("<td><b>Benutzer</b></td>");
				out.print("<td><b>Fortschritt</b></td>");
				out.print("<td><b>Start</b></td>");
				out.print("<td><b>Fertigstellung</b></td>");
				out.print("</tr>");
				
				// Clusterdaten		
				YarnCluster yc = new YarnCluster();
				yc.init(HadoopProperties.get("scheduler_address"), HadoopProperties.get("resourcemgr_address"), HadoopProperties.get("task_tracker_address"), HadoopProperties.get("hadoop_user"));
				List<ApplicationReport> apps = yc.getApplications();
				for(int i=0; i<apps.size(); i++) {
					out.println("<tr>");
					out.println("<td>"+apps.get(i).getName()+"</td>");
					out.println("<td>"+apps.get(i).getApplicationType()+"</td>");
					out.println("<td>"+apps.get(i).getHost()+"</td>");
					out.println("<td>"+apps.get(i).getUser()+"</td>");
					out.println("<td>"+(apps.get(i).getProgress()*100)+"%</td>");
					out.println("<td>"+dfmt.format(new Date(apps.get(i).getStartTime()))+"</td>");
					out.println("<td>"+dfmt.format(new Date(apps.get(i).getFinishTime()))+"</td>");
					out.println("</tr>");
				}
				
				out.println("</table>");
			%>
			
			</div>
		<div id="footer">Big-Data in der Praxis | <a href="www.jofre.de">www.jofre.de</a></div>
	</div>
</body>
</html>
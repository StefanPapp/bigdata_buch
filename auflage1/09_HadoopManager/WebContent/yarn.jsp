<%@page import="de.jofre.hadoopcontroller.HadoopProperties"%>
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
<title>Hadoop-Monitor - Yarn-Cluster</title>
</head>
<body>
	<div id="container">
		<div id="header">
			<h1>Hadoop-Monitor</h1>
		</div>
		
		<div id="navigation">
			<ul>
				<li><a href="hdfs.jsp">HDFS Explorer</a></li>
				<li><a href="yarn.jsp"><b>Yarn-Cluster</b></a></li>
				<li><a href="apps.jsp">Laufende Anwendungen</a></li>
			</ul>
		</div>
		
		<div id="content">
			<h2>Yarn-Cluster</h2>
		
			<%	
				out.println("<br><br><table style=\"width: 100%; border-width: 1px; border-style: solid;\"><tr><td><b>Adresse</b></td><td><b>Status</b></td><td><b>Containers</b></td><td><b>Genutzte Ressourcen</b></td><td><b>Verfügbare Ressourcen</b></td></tr>");
				
				// Clusterdaten		
				YarnCluster yc = new YarnCluster();
				yc.init(HadoopProperties.get("scheduler_address"), HadoopProperties.get("resourcemgr_address"), HadoopProperties.get("task_tracker_address"), HadoopProperties.get("hadoop_user"));
				List<NodeReport> nodes = yc.getNodes();
				
				if (nodes != null) {
					for(int i=0; i<nodes.size(); i++) {
						out.println("<tr>");
						out.println("<td>"+nodes.get(i).getHttpAddress()+"</td>");
						out.println("<td>"+nodes.get(i).getNodeState().toString()+"</td>");
						out.println("<td>"+nodes.get(i).getNumContainers()+"</td>");
						if (nodes.get(i).getUsed() != null) {
							out.println("<td>"+nodes.get(i).getUsed().getMemory()+"MB RAM / "+nodes.get(i).getUsed().getVirtualCores()+" Kerne</td>");
						} else {
							out.println("<td> - </td>");
						}
						out.println("<td>"+nodes.get(i).getCapability().getMemory()+"MB RAM / "+nodes.get(i).getCapability().getVirtualCores()+" Kerne</td>");
						out.println("</tr>");
					}
				}
				
				out.println("</table>");
				yc.uninit();
			%>
			
			</div>
		<div id="footer">Big-Data in der Praxis | <a href="www.jofre.de">www.jofre.de</a></div>
	</div>
</body>
</html>
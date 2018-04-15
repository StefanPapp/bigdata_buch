<%@page import="de.jofre.data.DiagramDummyData"%>
<%@page import="de.jofre.diagrams.CalendarChart"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="shortcut icon" href="img/favicon.ico" type="image/x-icon" />
<link rel="stylesheet" href="css/style.css" type="text/css" />
<script src="js/d3.v3.min.js" charset="utf-8"></script>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Big-Data-Visualisierung</title>

<%
	CalendarChart calendarchart = new CalendarChart();
	calendarchart.setWidth(800);
	calendarchart.setHeight(150);
	calendarchart.setCellsize(15);
	out.println(calendarchart.getStyleSheet());
%>

</head>
<body>


	<div id="container">
		<div id="header">
			<h1>Big-Data-Visualisierung</h1>
		</div>

		<%@include file="WEB-INF//menu.jsp"%>

		<div id="content">
			<%
				calendarchart.setInput(DiagramDummyData.CALENDAR_CHART());
				out.println(calendarchart.getJavaScript());
			%>
		</div>
		<div id="footer">
			Big-Data in der Praxis | <a href="www.jofre.de">www.jofre.de</a>
		</div>
	</div>
</body>
</html>
<%@page import="de.jofre.diagrams.FlareChart"%>
<%@page import="de.jofre.diagrams.LineChart"%>
<%@page import="de.jofre.diagrams.GlobeChart"%>
<%@page import="de.jofre.diagrams.Choropleth"%>
<%@page import="de.jofre.diagrams.CalendarChart"%>
<%@page import="de.jofre.diagrams.ZoomableTreemap"%>
<%@page import="de.jofre.diagrams.WordCloud"%>
<%@page import="de.jofre.diagrams.HierarchyBar"%>
<%@page import="de.jofre.diagrams.CollapsibleTreeview"%>
<%@page import="de.jofre.diagrams.CollapsibleIntendedTreeview"%>
<%@page import="de.jofre.diagrams.BubbleChart"%>
<%@page import="de.jofre.table.DiagramType"%>
<%@page import="java.util.List"%>
<%@page import="de.jofre.table.BDTable"%>
<%@page import="de.jofre.nlp.Sentiment"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script src="js/d3.v3.min.js" charset="utf-8"></script>
<link rel="shortcut icon" href="img/favicon.ico" type="image/x-icon" />
<link rel="stylesheet" href="css/style.css" type="text/css" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Big-Data-Analytics</title>
<%
	// Lese Parameter
	String chartName = request.getParameter("chartname");
	int column1 = -1;
	if (request.getParameter("column1") != null) {
		column1 = Integer.parseInt(request.getParameter("column1"));
	}
	int column2 = -1;
	if (request.getParameter("column2") != null) {
		column2 = Integer.parseInt(request.getParameter("column2"));
	}
	int column3 = -1;
	if (request.getParameter("column3") != null) {
		column3 = Integer.parseInt(request.getParameter("column3"));
	}
	int column4 = -1;
	if (request.getParameter("column4") != null) {
		column4 = Integer.parseInt(request.getParameter("column4"));
	}

	// Erstelle Objekte
	BubbleChart bc = null;
	CollapsibleIntendedTreeview cit = null;
	CollapsibleTreeview ct = null;
	HierarchyBar hb = null;
	WordCloud wc = null;
	ZoomableTreemap zt = null;
	CalendarChart cc = null;
	Choropleth cp = null;
	GlobeChart gc = null;
	LineChart lc = null;
	FlareChart fc = null;

	if (chartName != null) {
		if (chartName.equals(DiagramType.BUBBLE_CHART.getName())) {
			bc = new BubbleChart();
			bc.setWidth(900);
			bc.setHeight(650);
			out.println(bc.getAdditionalJavaScript());
			out.println(bc.getStyleSheet());
		}
		if (chartName.equals(DiagramType.COLLAPSIBLE_INTENDED_TREEVIEW
				.getName())) {
			cit = new CollapsibleIntendedTreeview();
			cit.setWidth(800);
			cit.setHeight(800);
			out.println(cit.getAdditionalJavaScript());
			out.println(cit.getStyleSheet());
		}
		if (chartName
				.equals(DiagramType.COLLAPSIBLE_TREEVIEW.getName())) {
			ct = new CollapsibleTreeview();
			ct.setWidth(800);
			ct.setHeight(800);
			out.println(ct.getAdditionalJavaScript());
			out.println(ct.getStyleSheet());
		}
		if (chartName.equals(DiagramType.HIERARCHY_BAR.getName())) {
			hb = new HierarchyBar();
			hb.setWidth(800);
			hb.setHeight(800);
			out.println(hb.getAdditionalJavaScript());
			out.println(hb.getStyleSheet());
		}
		if (chartName.equals(DiagramType.WORD_CLOUD.getName())) {
			wc = new WordCloud();
			wc.setWidth(800);
			wc.setHeight(650);
			out.println(wc.getAdditionalJavaScript());
			out.println(wc.getStyleSheet());
		}
		if (chartName.equals(DiagramType.ZOOMABLE_TREEMAP.getName())) {
			zt = new ZoomableTreemap();
			zt.setWidth(800);
			zt.setHeight(800);
			out.println(zt.getAdditionalJavaScript());
			out.println(zt.getStyleSheet());
		}
		if (chartName.equals(DiagramType.CALENDAR_CHART.getName())) {
			cc = new CalendarChart();
			cc.setWidth(900);
			cc.setHeight(200);
			cc.setCellsize(14);
			out.println(cc.getAdditionalJavaScript());
			out.println(cc.getStyleSheet());
		}
		if (chartName.equals(DiagramType.CHOROPLETH.getName())) {
			cp = new Choropleth();
			cp.setWidth(800);
			cp.setHeight(800);
			cp.setMapScale(3000);
			out.println(cp.getAdditionalJavaScript());
			out.println(cp.getStyleSheet());
		}
		if (chartName.equals(DiagramType.GLOBE_CHART.getName())) {
			gc = new GlobeChart();
			gc.setWidth(800);
			gc.setHeight(800);
			out.println(gc.getAdditionalJavaScript());
			out.println(gc.getStyleSheet());
		}
		if (chartName.equals(DiagramType.LINE_CHART.getName())) {
			lc = new LineChart();
			lc.setWidth(960);
			lc.setHeight(500);
			out.println(lc.getAdditionalJavaScript());
			out.println(lc.getStyleSheet());
		}
		if (chartName.equals(DiagramType.FLARE_CHART.getName())) {
			fc = new FlareChart();
			fc.setWidth(800);
			fc.setHeight(800);
			out.println(fc.getAdditionalJavaScript());
			out.println(fc.getStyleSheet());
		}
	}
%>
</head>
<body>


	<div id="container">
		<div id="header">
			<h1>Big-Data-Analytics</h1>
		</div>

		<%@include file="WEB-INF//menu.jsp"%>

		<%
			String inputText = request.getParameter("mytext");
			boolean headline = (session.getAttribute("headline") != null);
			// Frage die zuvor über HDFS, Hive oder HBase geladenen Daten ab
			if ((session.getAttribute("currentinput") != null)
					&& (inputText == null)) {
				inputText = (String) session.getAttribute("currentinput");
			}
		%>

		<div id="content">
			<h2>Visualisierung</h2>

			<%
				if (inputText != null) {
					BDTable table = new BDTable();
					table.readTableFromText(inputText, "\t", headline);

					out.println(table.getDiagramChoiceForm());

					String input = "";
					String output = "";
					if (chartName != null) {
						if (chartName.equals(DiagramType.BUBBLE_CHART.getName())) {
							if ((column1 > -1) && (column2 > -1)) {

								// Wird in diesem Parameter 99 übergeben, dann heißt das, dass das
								// Bubble-Chart keine eigene Spalte für einen zählbaren, nummerischen
								// Wert aufweist, sondern die Vorkommen von column1 auszählen und
								// darstellen soll.

								if (column2 == 99) {
									input = table.toBubbleChartData(column1, 0,
											false);
								} else {
									input = table.toBubbleChartData(column1,
											column2, true);
								}
								bc.setInput(input);
								output = bc.getJavaScript();
							}
						}

						if (chartName
								.equals(DiagramType.COLLAPSIBLE_INTENDED_TREEVIEW
										.getName())) {
							if ((column1 > -1) && (column2 > -1)) {
								if (column2 == 99) {
									input = table.toBubbleChartData(column1, 0,
											false);
								} else {
									input = table.toBubbleChartData(column1,
											column2, true);
								}
								cit.setInput(input);
								output = cit.getJavaScript();
							}
						}

						if (chartName.equals(DiagramType.COLLAPSIBLE_TREEVIEW
								.getName())) {
							if ((column1 > -1) && (column2 > -1)) {
								if (column2 == 99) {
									input = table.toBubbleChartData(column1, 0,
											false);
								} else {
									input = table.toBubbleChartData(column1,
											column2, true);
								}
								ct.setInput(input);
								output = ct.getJavaScript();
							}
						}

						if (chartName.equals(DiagramType.HIERARCHY_BAR.getName())) {
							if ((column1 > -1) && (column2 > -1)) {
								if (column2 == 99) {
									input = table.toBubbleChartData(column1, 0,
											false);
								} else {
									input = table.toBubbleChartData(column1,
											column2, true);
								}
								hb.setInput(input);
								output = hb.getJavaScript();
							}
						}

						if (chartName.equals(DiagramType.WORD_CLOUD.getName())) {
							if (column1 > -1) {
								input = table.toWordCloudData(column1);
							}
							wc.setInput(input);
							output = wc.getJavaScript();
						}

						if (chartName
								.equals(DiagramType.ZOOMABLE_TREEMAP.getName())) {
							if ((column1 > -1) && (column2 > -1)) {
								if (column2 == 99) {
									input = table.toBubbleChartData(column1, 0,
											false);
								} else {
									input = table.toBubbleChartData(column1,
											column2, true);
								}
								zt.setInput(input);
								output = zt.getJavaScript();
							}
						}

						if (chartName.equals(DiagramType.CALENDAR_CHART.getName())) {
							if ((column1 > -1) && (column2 > -1)) {
								input = table.toCalendarChartData(column1, column2);
								cc.setInput(input);
								cc.setStartyear(cc.getMinYear());
								cc.setEndyear(cc.getMaxYear() + 1); // +1 benötigt der JavaScript-Code
								output = cc.getJavaScript();
							}
						}

						if (chartName.equals(DiagramType.CHOROPLETH.getName())) {
							if ((column1 > -1) && (column2 > -1)) {
								input = table.toChoroplethData(column1, column2);
								cp.setInput(input);
								output = cp.getJavaScript();
							}
						}

						if (chartName.equals(DiagramType.GLOBE_CHART.getName())) {
							if ((column1 > -1) && (column2 > -1) && (column3 > -1)
									&& (column2 > -1)) {
								input = table.toGlobeChartData(column1, column2,
										column3, column4);
								gc.setInput(input);
								gc.setYears(gc.getYearsFromInput());
								output = gc.getJavaScript();
							}
						}

						if (chartName.equals(DiagramType.LINE_CHART.getName())) {
							if ((column1 > -1) && (column2 > -1)) {
								input = table.toLineChartData(column1, column2);
								lc.setInput(input);
								output = lc.getJavaScript();
							}
						}

						if (chartName.equals(DiagramType.FLARE_CHART.getName())) {
							if ((column1 > -1) && (column2 > -1)) {
								input = table.toFlareChartData(column1, column2);
								fc.setInput(input);
								output = fc.getJavaScript();
							}
						}

						out.println(output);
					}
				} else {
					out.println("Wählen Sie vor dem Anreichern Daten aus HDFS, Hive oder HBase");
				}
			%>

		</div>
		<div id="footer">
			Big-Data in der Praxis | <a href="www.jofre.de">www.jofre.de</a>
		</div>
	</div>
</body>
</html>
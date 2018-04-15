<%@page import="de.jofre.mrstarter.ChainedMRStarter"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>JobChaining - Auswertung des geschlechterspezifischen Notendurchschnitts</title>
</head>
<body>

	<%
		out.println("<b>Initialisiere Job-Starter...</b><br>");
		ChainedMRStarter mrstarter = new ChainedMRStarter();
		out.println("Erledigt!<br><br>");
		if (mrstarter.deleteOutput()) {
			out.println("<b>Ausgabeverzeichnis wurde gelöscht...</b><br><br>");
		}
		mrstarter.startJobs(out);
	%>
</body>
</html>
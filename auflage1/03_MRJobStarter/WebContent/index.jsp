<%@page import="java.util.List"%>
<%@page import="de.jofre.mrstarter.MRStarter"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>MRJobStarter - Starte Map-Reduce-Jobs via Java!</title>
</head>
<body>

	<%
		out.println("Initialisiere Job-Starter...<br>");
			MRStarter mrstarter = new MRStarter();
			out.println("Erledigt!<br><br>");
			if (mrstarter.deleteOutput()) {
		out.println("Ausgabeverzeichnis wurde gelöscht...<br><br>");
			}
			mrstarter.startJob(out);
	%>
</body>
</html>
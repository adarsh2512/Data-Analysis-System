<?xml version='1.0' encoding='UTF-8' ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@ taglib prefix="t" uri="http://myfaces.apache.org/tomahawk"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>s17g304</title>
<link rel="stylesheet" href="css/styleSheet.css" />
</head>
<body>
	<f:view>
		<div class="main">
			<hr />
			<h2>Main Menu</h2>
			<hr />
			<h:form>
				<br />
				<a href="import.jsp">Data Import</a>
				<br />
				<br />
				<a href="export.jsp">Data Export</a>
				<br />
				<br />
				<a href="view.jsp">Database Options</a>
				<br />
				<br />
				<a href="statistics.jsp">Descriptive Statistics</a>
				<br />
				<br />
				<a href="graphs.jsp">Graphical Analysis</a>
				<br />
				<br />
				<a href="logs.jsp">Access Logs</a>
				<br />
				<br />
				<h:commandButton value="Logout" action="#{mainMenu.processLogout}"
					styleClass="button" />
			</h:form>
		</div>
	</f:view>
</body>
</html>
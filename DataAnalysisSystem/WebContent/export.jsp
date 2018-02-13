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
		<div class="report">
			<center>
				<h2>Export</h2>
				<h:form>
					<hr />
					<h:commandButton value="Main Menu" action="#{mainMenu.reset}"
						styleClass="button" /> &nbsp;
                    <h:commandButton value="Logout"
						action="#{mainMenu.processLogout}" styleClass="button" />
					<hr />
					<h:outputText value="Schema : " />
					<h:selectOneMenu id="schema" value="#{mainMenu.schema}"
						onchange="submit()"
						valueChangeListener="#{mainMenu.schemaChangeEvent}"
						rendered="#{mainMenu.renderSchema}">
						<f:selectItems value="#{mainMenu.schemaList}" />
					</h:selectOneMenu>
					<br />
					<br />
					<h:panelGrid columns="3" styleClass="panel">
						<h:commandButton value="TableList" action="#{export.getTables}"
							styleClass="button" />
						<h:commandButton value="Export CSV " action="#{export.exportCSV}"
							styleClass="button" />
						<h:commandButton value="Export XML " action="#{export.exportXML}"
							styleClass="button" />
					</h:panelGrid>
					<pre>
						<h:outputText value="#{export.message}"
							rendered="#{export.renderMessage}" style="color:red" />
					</pre>
					<h:panelGrid columns="1">
						<h:selectOneListbox id="selectOneCb"
							style="width:150px; height:100px" value="#{export.tableSelected}"
							rendered="#{export.renderTablename}" size="5">
							<f:selectItems value="#{export.tableList}" />
						</h:selectOneListbox>
					</h:panelGrid>
				</h:form>
			</center>
		</div>
	</f:view>
</body>
</html>
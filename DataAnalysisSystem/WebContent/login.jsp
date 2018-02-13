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
	<div class="main">
		<hr />
		<h2>Database Login</h2>
		<hr />
		<a href="index.jsp">Home</a><br /> <br /> <br />
		<div class="box" align="center">
			<f:view>
				<h:form>
					<h:panelGrid columns="3" id="p" style="background-color: Beige;">
						<h:outputLabel value="Username* :" />
						<h:inputText id="username" value="#{dbAccess.username}"
							style="width:100%" required="true"
							requiredMessage="Required field" />
						<h:message for="username" style="color:red; font-size:90%" />
						<h:outputLabel value="Password* :" />
						<h:inputSecret id="password" value="#{dbAccess.password}"
							style="width:100%" required="true"
							requiredMessage="Required field" />
						<h:message for="password" style="color:red; font-size:90%" />
						<h:outputLabel value="Database* :" />
						<h:selectOneMenu id="database" value="#{dbAccess.dbms}"
							style="width:100%" required="true"
							requiredMessage="Required field">
							<f:selectItem itemValue="" itemLabel="" />
							<f:selectItem itemValue="mysql" itemLabel="MySQL" />
							<f:selectItem itemValue="db2" itemLabel="DB2" />
							<f:selectItem itemValue="oracle" itemLabel="Oracle" />
						</h:selectOneMenu>
						<h:message for="database" style="color:red; font-size:90%" />
						<h:outputLabel value="Server* :" />
						<h:selectOneMenu id="server" value="#{dbAccess.host}"
							style="width:100%" required="true"
							requiredMessage="Required field">
							<f:selectItem itemValue="" itemLabel="" />
							<f:selectItem itemValue="localhost" itemLabel="localhost" />
							<f:selectItem itemValue="131.193.209.54" itemLabel="server54" />
							<f:selectItem itemValue="131.193.209.57" itemLabel="server57" />
						</h:selectOneMenu>
						<h:message for="server" style="color:red; font-size:90%" />
						<h:outputLabel value="Schema* :" />
						<h:inputText id="schema" value="#{dbAccess.schema}"
							style="width:100%" required="true"
							requiredMessage="Required field" />
						<h:message for="schema" style="color:red; font-size:90%" />
						<h:outputText value="" />
						<h:commandButton value="Login" action="#{mainMenu.processLogin}"
							styleClass="button" />
						<br />
					</h:panelGrid>
					<br />
					<h:outputText value="#{dbAccess.message}"
						rendered="#{dbAccess.renderMessage}"
						style="color:red; font-weight:bold" />
				</h:form>
			</f:view>
		</div>
	</div>
</body>
</html>
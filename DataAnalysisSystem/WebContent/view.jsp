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
			<h2>Database Options</h2>
			<h:form>
				<hr />
				<h:commandButton value="Main Menu" action="#{mainMenu.reset}" styleClass="button" /> &nbsp;
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
				<div align="center">
					<h:panelGrid columns="8"
						style="background-color: Beige;
border-bottom-style: solid;
border-top-style: solid;
border-left-style: solid;
border-right-style: solid">
						<h:commandButton value="TableList"
							action="#{dbOperations.getTables}" styleClass="button" />
						<h:commandButton value="ColumnList"
							action="#{dbOperations.getColumnNames}" styleClass="button" />
						<h:commandButton value="DisplayTable"
							action="#{dbOperations.getTableData}" styleClass="button" />
						<h:commandButton value="DisplaySelectedColumns"
							action="#{dbOperations.getColumnData}" styleClass="button" />
						<h:commandButton value="ProcessSQLQuery"
							action="#{dbOperations.processQuery}" styleClass="button" />
						<h:commandButton value="DropTables"
							action="#{dbOperations.dropTable}" styleClass="button" />
							<h:commandButton value="Initiate Compute"
							action="#{dbOperations.renderComputes}" styleClass="button" />
							<h:commandButton value="Compute"
							action="#{dbOperations.compute}" styleClass="button" />
					</h:panelGrid>
				</div>
				<pre>
						<h:outputText value="#{dbOperations.message}"
						rendered="#{dbOperations.renderMessage}" style="color:red" />
					</pre>
				<div align = "center">
				<h:panelGrid columns="4" style="background-color: Beige;">
				<h:outputText value="Select Table"
						rendered="#{dbOperations.renderTablename}" />
				<h:outputText value="Select Column"
						rendered="#{dbOperations.columnRender}" />
				<h:outputText value="Write Query"
						rendered="#{dbOperations.columnRender}" />
						 <h:outputText value=""
						 />
				<h:selectOneListbox id="selectOneCb"
					style="width:150px; height:100px"
					value="#{dbOperations.tableSelected}"
					rendered="#{dbOperations.renderTablename}" size="5">
					<f:selectItems value="#{dbOperations.tableList}" />
				</h:selectOneListbox> <h:selectManyListbox id="selectcolumns"
					style="width:150px; height:100px"
					value="#{dbOperations.columnSelected}"
					rendered="#{dbOperations.columnRender}" size="5">
					<f:selectItems value="#{dbOperations.columnsList}" />
				</h:selectManyListbox> <h:inputTextarea rows="6" cols="40"
					style="height:100px" value="#{dbOperations.userQuery}" rendered="#{dbOperations.columnRender}" /> </h:panelGrid>
					</div>
				<br />
				<br />
				<h:outputText value="Query : "
					rendered="#{dbOperations.renderTabledata}" />
				<h:outputText value="#{dbOperations.userQuery}"
					rendered="#{dbOperations.renderTabledata}" />
				<br />
				<h:outputText value="Rows : "
					rendered="#{dbOperations.renderTabledata}" />
				<h:outputText value="#{dbOperations.rowsAffected}"
					rendered="#{dbOperations.renderTabledata}" />
				<br />
				<h:outputText value="Columns : "
					rendered="#{dbOperations.renderTabledata}" />
				<h:outputText value="#{dbOperations.columnCount}"
					rendered="#{dbOperations.renderTabledata}" />
				<hr />
				<div align = "center">
				<h:panelGrid columns="2" style="background-color: Beige;">
				<h:outputText value="Select Source"
						rendered="#{dbOperations.renderCompute}" />
				<h:outputText value="Select Destination"
						rendered="#{dbOperations.renderCompute}" />
				<h:selectOneListbox id="selectSource"
					style="width:150px; height:100px"
					value="#{dbOperations.source}"
					rendered="#{dbOperations.renderCompute}" size="5">
					<f:selectItems value="#{dbOperations.sourcecolList}" />
				</h:selectOneListbox>
				<h:selectOneListbox id="selectDestination"
					style="width:150px; height:100px"
					value="#{dbOperations.destination}"
					rendered="#{dbOperations.renderCompute}" size="5">
					<f:selectItems value="#{dbOperations.sourcecolList}" />
				</h:selectOneListbox>
				</h:panelGrid>
				</div>
				<div
					style="background-attachment: scroll; overflow: auto; height: 300px; background-repeat: repeat"
					align="center">
					<t:dataTable value="#{dbOperations.result}" var="row"
						rendered="#{dbOperations.renderTabledata}" border="1"
						cellspacing="0" cellpadding="1" columnClasses="columnClass border"
						headerClass="headerClass" footerClass="footerClass"
						rowClasses="rowClass2" styleClass="dataTableEx" width="900px">
						<t:columns var="col" value="#{dbOperations.columnSelected}">
							<f:facet name="header">
								<t:outputText styleClass="outputHeader" value="#{col}" />
							</f:facet>
							<t:outputText styleClass="outputText" value="#{row[col]}" />
						</t:columns>
					</t:dataTable>
				</div>
				<hr />
			</h:form>
		</div>
	</f:view>
</body>
</html>
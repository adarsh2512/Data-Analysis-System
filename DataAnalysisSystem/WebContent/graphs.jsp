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
			<h2>Graphical Analysis</h2>
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
				<h:commandButton value="Generate Charts"
					action="#{graphics.generateChart}" styleClass="button"
					rendered="#{graphics.renderGenerategraphButton}" />
				<pre>
						<h:outputText value="#{graphics.message}"
						rendered="#{graphics.renderMessage}" style="color:red" />
					</pre>
				<div align="center">
					<h:panelGrid columns="4">
						<h:selectOneListbox id="displayCharts"
							value="#{graphics.chartType}" onchange="submit()"
							valueChangeListener="#{graphics.chartValueChanged}" size="5">
							<f:selectItem itemValue="0" itemLabel="Select Chart Type" />
							<f:selectItem itemValue="1" itemLabel="Pie Chart" />
							<f:selectItem itemValue="2" itemLabel="Bar Graph" />
							<f:selectItem itemValue="3" itemLabel="Scatterplot" />
							<f:selectItem itemValue="4" itemLabel="TimeSeries" />
						</h:selectOneListbox>
						<h:selectOneListbox id="selectOneCb"
							style="width:150px; height:100px"
							value="#{graphics.tableSelected}"
							rendered="#{graphics.renderTables}" size="5" onchange="submit()"
							valueChangeListener="#{graphics.tableValueChanged}">
							<f:selectItems value="#{statisticsBean.tableList}" />
						</h:selectOneListbox>
						<h:selectOneListbox id="Graphs"
							value="#{graphics.chartColumnSelected}"
							rendered="#{graphics.renderChartColumn}" size="5">
							<f:selectItems value="#{statisticsBean.numericData}" />
						</h:selectOneListbox>
						<h:selectOneListbox id="predictor1"
							value="#{graphics.predictorValue}" size="5"
							rendered="#{graphics.renderXYGraphColumns}">
							<f:selectItem itemValue="0" itemLabel="Select Predictor Value" />
							<f:selectItems value="#{statisticsBean.numericData}" />
						</h:selectOneListbox>
						<h:selectOneListbox id="response2"
							value="#{graphics.responseValue}" size="5"
							rendered="#{graphics.renderXYGraphColumns}">
							<f:selectItem itemValue="0" itemLabel="Select Response Value" />
							<f:selectItems value="#{statisticsBean.numericData}" />
						</h:selectOneListbox>
					</h:panelGrid>
					<hr />
					<div
						style="background-attachment: scroll; overflow: auto; height: 300px; background-repeat: repeat"
						align="center">
						<h:graphicImage value="#{graphics.pieChartPath}" width="600"
							height="600" rendered="#{graphics.renderPieChart}" />
						<h:graphicImage value="#{graphics.barChartPath}" width="600"
							height="600" rendered="#{graphics.renderBarChart}" />
						<h:graphicImage value="#{graphics.xyChartPath}" width="600"
							height="600" rendered="#{graphics.xySeriesChart}" />
						<h:graphicImage value="#{graphics.xyTimeSeriesPath}" width="600"
							height="600" rendered="#{graphics.renderTimeSeriesChart}" />
						<br />
					</div>
				</div>
			</h:form>
		</div>
	</f:view>
</body>
</html>
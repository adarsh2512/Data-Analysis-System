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
			<h2>Access logs</h2>
			<h:form>
				<hr />
				<h:commandButton value="Main Menu" action="#{mainMenu.reset}" styleClass="button" /> &nbsp;
                    <h:commandButton value="Logout"
					action="#{mainMenu.processLogout}" styleClass="button" />
				<hr />
				<br />
				<h:commandButton value="GenerateLogs"
					action="#{dbOperations.getIpTableData}" styleClass="button" />
				<br />
				<pre>
						<h:outputText value="#{log.message}"
						rendered="#{log.renderMessage}" style="color:red" />
						<h:outputText value="#{dbOperations.message}"
						rendered="#{dbOperations.renderMessage}" style="color:red" />
					</pre>
				<br />
					<div style="background-attachment: scroll; overflow:auto;
                    		height:300px; background-repeat: repeat" align="center">
                        <t:dataTable value="#{dbOperations.result1}" var="row"
				            rendered="#{dbOperations.renderLogTable}"
				            border="1" cellspacing="0" cellpadding="1"
                            columnClasses="columnClass border"
				            headerClass="headerClass" footerClass="footerClass"
                            rowClasses="rowClass2" styleClass="dataTableEx"
				            width="900px">
                            <t:columns var="col" value="#{dbOperations.logColumns}">
                                <f:facet name="header">
                                    <t:outputText styleClass="outputHeader" value="#{col}" />
                                </f:facet>
                                <t:outputText styleClass="outputText" value="#{row[col]}" />
                            </t:columns>
                        </t:dataTable>	
				    </div> 
			</h:form>
		</div>
	</f:view>
</body>
</html>
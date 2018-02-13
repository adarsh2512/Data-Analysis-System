package edu.uic.ids.bean;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.jstl.sql.Result;
import javax.servlet.jsp.jstl.sql.ResultSupport;

import com.mysql.jdbc.DatabaseMetaData;

@ManagedBean(name="dbOperations")
@SessionScoped
public class DbOperations {

	private String tableSelected;
	private boolean columnRender;
	private boolean columnDataRender;
	private ResultSet resultSet;
	private boolean renderTablename;
	private ResultSetMetaData resultSetMetaData;
	private Result result;
	private Result result1;
	private boolean renderButton;
	private boolean renderTabledata;
	private List<String> tableList;
	private List<String> columnsList;
	private List<String> sourcecolList;
	public List<String> getSourcecolList() {
		return sourcecolList;
	}
	public void setSourcecolList(List<String> sourcecolList) {
		this.sourcecolList = sourcecolList;
	}


	public void setResult1(Result result1) {
		this.result1 = result1;
	}
	
	private List<String> logColumns;
	public List<String> getLogColumns() {
		return logColumns;
	}


	public void setLogColumns(List<String> logColumns) {
		this.logColumns = logColumns;
	}
	
	private boolean renderLogTable;

	public boolean isRenderLogTable() {
		return renderLogTable;
	}


	public void setRenderLogTable(boolean renderLogTable) {
		this.renderLogTable = renderLogTable;
	}


	private List<String> columnSelected;
	private List<String> columns = new ArrayList<String>();
	private List<String> schemaList;
	private List<Double> sourceList;
	private List<Double> destinationList;
	private List<Double> resultList;
	private String userQuery;
	private int rowsAffected;
	private int columnCount;
	private DatabaseMetaData metaData;
	private boolean renderSchema;
	private String message;
	private boolean renderMessage;
	private String userSchema;
	@ManagedProperty(value="#{dbAccess}")
	private DbAccess dbAccess;
	private String source = "";
	private String destination = "";
	private boolean renderCompute;
	
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public boolean isRenderCompute() {
		return renderCompute;
	}

	public void setRenderCompute(boolean renderCompute) {
		this.renderCompute = renderCompute;
	}

	

	public DbOperations(){
/*		FacesContext context = FacesContext.getCurrentInstance();
		if(context!=null){
			HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
			dbAccess = (DbAccess)session.getAttribute("dbAccess");
		}*/
		columnRender = false;
		rowsAffected = 0;
		renderCompute = false;
		columnDataRender = false;
		renderTablename = false;
		renderTabledata = false;
		renderLogTable = false;
		renderMessage = false;
		renderSchema = false;
		tableList = new ArrayList<String>();
		columnsList = new ArrayList<String>();
		sourcecolList = new ArrayList<String>();
		columnSelected = new ArrayList<String>();
		schemaList = new ArrayList<String>();
		logColumns = new ArrayList<String>();
	}

	public void valueChanged() 
	{
		reset();
		userQuery = null;
		tableSelected=null;
		columnSelected.clear();
		columnRender = false;
		columnDataRender = false;
		renderTabledata = false;
		renderTablename=false;
		renderLogTable=false;
		logColumns.clear();
		getTables();
	}

	public void renderTableList()
	{
		renderMessage=false;
		if(tableList.isEmpty())
		{
			message = "No tables found in the schema.";
			renderMessage = true;
			renderTabledata = false;
			renderMessage = true;
			renderTablename = false;
			userQuery = "";
		}
		else
			renderTablename = true;
	}

	public String getTables()
	{
		try {
			reset();
			renderCompute = false;
			tableList.clear();
			String tablenames;
			resultSet = dbAccess.getTables();
			if(resultSet!=null)
			{
				while(resultSet.next())
				{
					tablenames = resultSet.getString("TABLE_NAME");
					tableList.add(tablenames);
				}
				renderTableList();
				return "SUCCESS";
			}
			else
			{
				message = dbAccess.getMessage();
				renderMessage = true;
				return "FAIL";
			}
		} catch (Exception e) {
			message = e.getMessage();
			renderMessage = true;
			return "FAIL";
		}
	}

	public String getColumnNames ()
	{
		try {
			sourcecolList.clear();
			reset();
			renderCompute = false;
			if (tableList.isEmpty())
			{
				message = "There are no tables to display.";
				renderMessage = true;
				return "FAIL";
			}
			if(tableSelected.isEmpty())
			{
				message = "Please select a table.";
				renderMessage = true;
			}
			else
			{
				String sqlQuery = "select * from " + dbAccess.getSchema() +
						"." + tableSelected;
				ResultSet resultSet = dbAccess.getColumnNames(sqlQuery);
				userQuery = "";
				if(resultSet!=null)
				{
					columnsList.clear();
					ResultSetMetaData  resultSetmd=(ResultSetMetaData) resultSet.getMetaData();
					int columnCount = resultSetmd.getColumnCount();
					for (int i = 1; i <= columnCount; i++ ) {
						String name = resultSetmd.getColumnName(i);
						String datatype = resultSetmd.getColumnTypeName(i);
						columns.add(name);
						columnsList.add(name + " " + datatype);
						sourcecolList.add(name);
					}
					columnRender= true;
				}
				else
				{
					message = dbAccess.getMessage();
					renderMessage = true;
				}
			}
			return "SUCCESS";
		} catch(Exception e) {
			message = e.getMessage();
			renderMessage = true;
			return "FAIL";
		}
	}

	public String getColumnData()
	{
		reset();
		renderCompute = false;
		if (tableList.isEmpty())
		{
			message = "There are no tables to display.";
			renderMessage = true;
			return "FAIL";
		}
		if(tableSelected.isEmpty())
		{
			message = "Please select a table and a column.";
			renderMessage = true;
			return "FAIL";
		}
		if (columnSelected.isEmpty())
		{
			message = "Please select a column.";
			renderMessage = true;
			return "FAIL";
		}
		String data = columnSelected.get(0);
		int index = data.indexOf(" ");
		if (index < 0)
		{
			message = "Please select a column.";
			renderMessage = true;
			return "FAIL";
		}
		else
		{
			splitColumns();
			return "SUCCESS";
		}
	}
	private void generateMetaData()
	{
		try {
			if(resultSet!=null)
			{
				columnSelected = new ArrayList<String>();
				resultSetMetaData = resultSet.getMetaData();
				result = ResultSupport.toResult(resultSet);
				columnCount = resultSetMetaData.getColumnCount();
				rowsAffected = result.getRowCount();
				String columnNameList [] = result.getColumnNames();
				for(int i=0; i<columnCount; i++) 
				{
					columnSelected.add(columnNameList[i]);
				}
				renderTabledata = true;
			}
			else
			{
				message = dbAccess.getMessage();
				renderMessage = true;
			}
		} catch(Exception e) {
			message = e.getMessage();
			renderMessage = true;
		}
	}

	public String getTableData()
	{
		try {
			reset();
			renderCompute = false;
			if (tableList.isEmpty())
			{
				message = "There are no tables to display.";
				renderMessage = true;
				return "FAIL";
			}
			if(tableSelected.isEmpty())
			{
				message = "Please select a table.";
				renderMessage = true;
				return "FAIL";
			}
			else
			{
				String sqlQuery="select * from " + dbAccess.getSchema() + "." + tableSelected;
				resultSet = dbAccess.processSelect(sqlQuery);
				userQuery = sqlQuery;
				if(resultSet != null)
				{
					generateMetaData();
					return "SUCCESS";
				}
				else
				{
					message = dbAccess.getMessage();	
					renderMessage = true;
					return "FAIL";
				}
			}
		} catch (Exception e) {
			message = e.getMessage();
			renderMessage = true;
			return "FAIL";
		}
	}
	
	public String getIpTableData()
	{
		try {
				reset();
				String sqlQuery="select * from " + dbAccess.getSchema() + ".s17g304_log";
				resultSet = dbAccess.processSelect(sqlQuery);
				userQuery = sqlQuery;
				if(resultSet != null)
				{
					logColumns.add("LogID");
					logColumns.add("Username");
					logColumns.add("dbms");
					logColumns.add("LoginTime");
					logColumns.add("LogoutTime");
					logColumns.add("IPAddress");
					logColumns.add("SessionID");
					//generateMetaData();
					result1 = ResultSupport.toResult(resultSet);
					renderLogTable=true;
					return "SUCCESS";
				}
				else
				{
					message = "No log table in database to display";	
					renderMessage = true;
					return "FAIL";
				}
			
		} catch (Exception e) {
			message = e.getMessage();
			renderMessage = true;
			return "FAIL";
		}
	}

	public Result getResult1() {
		return result1;
	}
	public String processQuery()
	{
		try {
			renderCompute = false;
			reset();
			if(userQuery.isEmpty())
			{
				message = "Please enter a query.";
				renderMessage = true;
				return "FAIL";
			}
			else
			{
				userQuery=userQuery.toLowerCase();
				int index = userQuery.indexOf(" ");
				if (index < 0)
				{
					message = "Please enter a valid query.";
					renderMessage = true;
					return "FAIL";
				}
				String subString = userQuery.substring(0, index);
				switch(subString)
				{
				case "select":
					reset();
					resultSet = dbAccess.processSelect(userQuery);
					if(resultSet!=null)
					{
						generateMetaData();
					}
					else
					{
						message = dbAccess.getMessage();
						renderMessage = true;
					}
					break;
				case "update":
					reset();
					rowsAffected = dbAccess.processUpdate(userQuery);
					if(rowsAffected < 0)
					{
						message = dbAccess.getMessage();
						renderMessage = true;
					}
					else
					{
						message = "Successfully updated " + rowsAffected + " rows.";
						renderMessage = true;
					}
					break;
				case "drop":
					reset();
					rowsAffected = dbAccess.processUpdate(userQuery);
					if(rowsAffected < 0)
					{
						message = dbAccess.getMessage();
						renderMessage = true;
					}
					else
					{
						message = "Table dropped successfully";
						renderMessage = true;
						getTables();
					}
					break;
				case "create":
					reset();
					rowsAffected = dbAccess.processUpdate(userQuery);
					if(rowsAffected < 0)
					{
						message = dbAccess.getMessage();
						renderMessage = true;
					}
					else
					{
						message = "Successfully created the table.";
						renderMessage = true;
					}
					break;
				case "delete":
					reset();
					rowsAffected = dbAccess.processUpdate(userQuery);
					if(rowsAffected < 0)
					{
						message = dbAccess.getMessage();
						renderMessage = true;
					}
					else
					{
						message = "Successfully deleted " + rowsAffected + " rows.";
						renderMessage = true;
					}
					break;
				case "insert":
					reset();
					rowsAffected = dbAccess.processUpdate(userQuery);
					if(rowsAffected < 0)
					{
						message = dbAccess.getMessage();
						renderMessage = true;
					}
					else
					{
						message = "Successfully inserted " + rowsAffected + " rows.";
						renderMessage = true;
					}
					break;
				default:
					message = "Please enter a valid query.";
					renderMessage = true;
					break;
				}	
			}
		} catch(Exception e) {
			message = e.getMessage();
			renderMessage = true;
		}
		return "SUCCESS";
	}

	public void splitColumns()
	{
		try {
			if(tableSelected != null && columnSelected != null )
			{
				List<String> columnSeperated = new ArrayList<String>();
				for (int i = 0; i < columnSelected.size(); i++) 
				{
					String data = columnSelected.get(i);
					int index = data.indexOf(" ");
					data = data.substring(0, index);
					columnSeperated.add(data);
				}
				columnSelected = new ArrayList<String>();
				columnSelected = columnSeperated;
				columnSeperated = null;
				StringBuilder rString = new StringBuilder();
				for (String each : columnSelected) 
				{
					rString.append(",").append(each);
				}
				String sqlQuery = rString.toString();
				int index = sqlQuery.indexOf(",");
				sqlQuery = sqlQuery.substring(index+1, sqlQuery.length());
				sqlQuery = "select " + sqlQuery + " from "+ dbAccess.getSchema() +
						"." + tableSelected;
				resultSet = dbAccess.processSelect(sqlQuery);
				userQuery = sqlQuery;
				if(resultSet!=null)
				{
					generateMetaData();
				}
				else
				{
					message = dbAccess.getMessage();
					renderMessage = true;
				}
			}
			else
			{
				message = "Please select a table and a column.";
			}
		} catch (Exception e) {
			message = e.getMessage();
			renderMessage = true;
		}
	}

	public String dropTable()
	{
		try {
			renderCompute = false;
			reset();
			if (tableList.isEmpty())
			{
				message = "There are no tables to drop.";
				renderMessage = true;
				return "FAIL";
			}
			if(tableSelected.isEmpty())
			{
				message = "Please select a table to drop.";
				renderMessage = true;
				return "FAIL";
			}
			if(dbAccess.getSchema().equalsIgnoreCase(userSchema))
			{
				String sqlQuery = "drop table " + dbAccess.getSchema() +
						"." + tableSelected;
				rowsAffected = dbAccess.processUpdate(sqlQuery);
				userQuery = sqlQuery;
				if(rowsAffected < 0)
				{
					message = dbAccess.getMessage();
					renderMessage = true;
				}
				else
					getTables();
			}
			else
			{
				message = "Tables cannot be dropped from World schema.";
				renderMessage = true;
			}
			return "SUCCESS";
		} catch(Exception e)
		{
			message = e.getMessage();
			renderMessage = true;
			return "FAIL";
		}
	}

	public void reset()
	{
		message = "";
		renderMessage = false;
		rowsAffected = 0;
		columnCount = 0;
		renderTabledata = false;
		renderLogTable = false;
		logColumns.clear();
	}

	public String renderComputes(){
		try{if (tableList.isEmpty())
		{
			message = "There are no tables to display.";
			renderMessage = true;
			return "Fail";
		}
		if(tableSelected.isEmpty())
		{
			message = "Please select a table.";
			renderMessage = true;
			return "Fail";
		}
		if(sourcecolList.size()==0)
		{
			message = "Click on Column list and then click on Initiate Compute to choose source and destination";
			renderMessage = true;
			return "Fail";
		}
		else{
		renderCompute = true;
		renderTabledata = false;
		return "Success";
		}}
		catch (Exception e){
			System.err.println(e.getMessage());
			message = "Error";
			return "Fail";
		}
	}
	public String compute()
	{
		String status = "fail";
		sourceList = new ArrayList<Double>();
		destinationList = new ArrayList<Double>();
		resultList = new ArrayList<Double>();
		String sqlQuery = "select * from " + dbAccess.getSchema() +
				"." + tableSelected;
		//System.out.println(sqlQuery);
		try {
			if (tableList.isEmpty())
			{
				message = "There are no tables to display.";
				renderMessage = true;
				return status;
			}
			if(tableSelected.isEmpty())
			{
				message = "Please select a table.";
				renderMessage = true;
				return status;
			}
			if(sourcecolList.size()==0)
			{
				message = "Click on Column list and then click on Initiate Compute to choose source and destination";
				renderMessage = true;
				return status;
			}
			if(source.equals("")||destination.equals(""))
			{
				message = "Choose source and destination (If haven't clicked on Initiate Compute, then click it first)";
				renderMessage = true;
				return status;
			}
			else{
			//System.out.println("inside resultset");
			resultSet = dbAccess.processSelect(sqlQuery);
			if (resultSet != null){
				//System.out.println("inside resultset12");
				resultSet.beforeFirst();
			while(resultSet.next()){
				sourceList.add(resultSet.getDouble(source));
				destinationList.add(resultSet.getDouble(destination));
			}
			resultList.add(0.0);
			for(int i=1; i<sourceList.size(); i++){
				resultList.add(Math.log(sourceList.get(i)/sourceList.get(i-1)));
			}
			dbAccess.processUpdate("alter table "+tableSelected+" add id int;");
			dbAccess.processUpdate("alter table "+tableSelected+" modify id int AUTO_INCREMENT primary key;");
			//System.out.println("alter table "+tableSelected+" add id int not null auto_increment;");
			for(int i=0; i<resultList.size(); i++){
				dbAccess.processUpdate("update "+tableSelected+" set "+destination+" = "+resultList.get(i).toString().replace("[", "").replace("]", "")+" where id = "+Integer.toString(i+1)+";");
			//System.out.println("update "+tableSelected+" set "+destination+" = "+resultList.get(i).toString().replace("[", "").replace("]", "")+" where id = "+Integer.toString(i+1)+";");
			}
			dbAccess.processUpdate("alter table "+tableSelected+" drop column id;");
			//System.out.println("alter table "+tableSelected+" drop column id;");
			//System.out.println(source);
			//System.out.println(destination);
			}
			message = "Computation Done";
			renderMessage = true;
			status = "success";
			return status;
			}
		} 
		catch (SQLException e) {
			
			System.err.println(e.getSQLState());
			System.err.println(e.getMessage());
			System.err.println(e.getErrorCode());
			message = "Destination of wrong format";
			renderMessage = true;
			return status;
		}
		catch (Exception e){
			System.err.println(e.getMessage());
			message = "Choose Numeric Source";
			renderMessage = true;
			return status;
		}
		
	}
	public List<String> getTableList() {
		return tableList;
	}

	public void setTableList(List<String> tableList) {
		this.tableList = tableList;
	}

	public DbAccess getdbAccess() {
		return dbAccess;
	}

	public void setdbAccess(DbAccess dbAccess) {
		this.dbAccess = dbAccess;
	}

	public String getTableSelected() {
		return tableSelected;
	}

	public void setTableSelected(String tableSelected) {
		this.tableSelected = tableSelected;
	}

	public List<String> getColumnSelected() {
		return columnSelected;
	}

	public void setColumnSelected(List<String> columnSelected) {
		this.columnSelected = columnSelected;
	}

	public boolean isColumnRender() {
		return columnRender;
	}

	public void setColumnRender(boolean columnRender) {
		this.columnRender = columnRender;
	}

	public boolean isColumnDataRender() {
		return columnDataRender;
	}

	public void setColumnDataRender(boolean columnDataRender) {
		this.columnDataRender = columnDataRender;
	}

	public ResultSet getResultSet() {
		return resultSet;
	}

	public void setResultSet(ResultSet resultSet) {
		this.resultSet = resultSet;
	}

	public boolean isRenderTablename() {
		return renderTablename;
	}

	public void setRenderTablename(boolean renderTablename) {
		this.renderTablename = renderTablename;
	}

	public ResultSetMetaData getResultSetMetaData() {
		return resultSetMetaData;
	}

	public void setResultSetMetaData(ResultSetMetaData resultSetMetaData) {
		this.resultSetMetaData = resultSetMetaData;
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	public boolean isRenderTable() {
		return renderTabledata;
	}

	public void setRenderTable(boolean renderTable) {
		this.renderTabledata = renderTable;
	}

	public List<String> getColumnsList() {
		return columnsList;
	}

	public void setColumnsList(List<String> columnsList) {
		this.columnsList = columnsList;
	}

	public String getUserQuery() {
		return userQuery;
	}

	public void setUserQuery(String userQuery) {
		this.userQuery = userQuery;
	}

	public int getRowsAffected() {
		return rowsAffected;
	}

	public void setRowsAffected(int rowsAffected) {
		this.rowsAffected = rowsAffected;
	}

	public DbAccess getDbAccess() {
		return dbAccess;
	}

	public void setDbAccess(DbAccess dbAccess) {
		this.dbAccess = dbAccess;
	}

	public int getColumnCount() {
		return columnCount;
	}

	public void setColumnCount(int columnCount) {
		this.columnCount = columnCount;
	}

	public List<String> getSchemaList() {
		return schemaList;
	}
	public void setSchemaList(List<String> schemaList) {
		this.schemaList = schemaList;
	}
	public DatabaseMetaData getMetaData() {
		return metaData;
	}
	public void setMetaData(DatabaseMetaData metaData) {
		this.metaData = metaData;
	}

	public List<String> getColumns() {
		return columns;
	}

	public void setColumns(List<String> columns) {
		this.columns = columns;
	}

	public boolean isRenderSchema() {
		return renderSchema;
	}

	public void setRenderSchema(boolean renderSchema) {
		this.renderSchema = renderSchema;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isRenderTabledata() {
		return renderTabledata;
	}

	public void setRenderTabledata(boolean renderTabledata) {
		this.renderTabledata = renderTabledata;
	}

	public boolean isRenderMessage() {
		return renderMessage;
	}

	public void setRenderMessage(boolean renderMessage) {
		this.renderMessage = renderMessage;
	}

	public boolean isRenderButton() {
		return renderButton;
	}

	public void setRenderButton(boolean renderButton) {
		this.renderButton = renderButton;
	}

	public String getUserSchema() {
		return userSchema;
	}

	public void setUserSchema(String userSchema) {
		this.userSchema = userSchema;
	}

}
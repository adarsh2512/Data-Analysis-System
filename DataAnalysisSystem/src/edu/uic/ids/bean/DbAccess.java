package edu.uic.ids.bean;


import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.DatabaseMetaData;
import com.mysql.jdbc.ResultSetMetaData;
import com.mysql.jdbc.Statement;

@ManagedBean(name="dbAccess")
@SessionScoped
public class DbAccess {

	private String username;
	private String password;
	private String url;
	private String dbms;
	private String port;
	private String host;
	private String schema;
	private String jdbcDriver;
	private Connection connection;
	private Statement statement;
	private ResultSet resultSet;
	private String message;
	private Boolean renderMessage;
	private ResultSetMetaData resultSetMetaData;
	private DatabaseMetaData metaData;
	private String userSchema;

	// constants for SQLException
	private static final String ACCESS_DENIED = "28000";
	private static final String INVALID_DB_SCHEMA = "42000";
	private static final String TIMEOUT = "08S01";

	public String connect()
	{
		try
		{
			if(connection != null)
			{
				if (resultSet != null)
					resultSet.close();
				if (statement != null)
					statement.close();
				connection.close();
			}
			switch (dbms)
			{
			case "mysql":
				jdbcDriver = "com.mysql.jdbc.Driver";
				port = "3306";
				url = "jdbc:mysql://" + host + ":" + port + "/" + schema;
				break;
			case "db2":
				jdbcDriver = "com.ibm.db2.jcc.DB2Driver";
				port = "50000";
				url = "jdbc:db2://" + host + ":" + port + "/" + schema;
				break;
			case "oracle":
				jdbcDriver = "oracle.jdbc.driver.OracleDriver";
				port = "1521";
				url = "jdbc:oracle:thin:@" + host + ":" + port + "/" + schema;
				break;
			default:
				message =  "Database: " + dbms + " not supported.";
				renderMessage = true;
				return "FAIL";
			}
			Class.forName(jdbcDriver);
			connection = (Connection) DriverManager.getConnection(url, username, password);
			//connection= DriverManager.getConnection(url + "?" + "user="+userName + "&" + "useSSL=false" + "&" + "password=" + password);
			statement = (Statement) getConnection().createStatement();
			return "SUCCESS";
		} catch(ClassNotFoundException ce) {
			message =  "Database: " + dbms + " not supported.";
			port = "";
			renderMessage = true;
			return "FAIL";
		} catch(SQLException se) {
			if (se.getSQLState().equals(ACCESS_DENIED))
			{
				message = "Error Code: " + se.getErrorCode() + "\n" +
						"SQL State: " + se.getSQLState() + "\n" +
						"Message :" + se.getMessage() + "\n\n" +
						"Invalid credentials!";
			}
			else if (se.getSQLState().equals(INVALID_DB_SCHEMA))
			{
				message = "Error Code: " + se.getErrorCode() + "\n" +
						"SQL State: " + se.getSQLState() + "\n" +
						"Message :" + se.getMessage() + "\n\n" +
						"Invalid database schema!";
			}
			else if (se.getSQLState().equals(TIMEOUT))
			{
				message = "Error Code: " + se.getErrorCode() + "\n" +
						"SQL State: " + se.getSQLState() + "\n" +
						"Message :" + se.getMessage() + "\n\n" +
						"Please check server.";
			}
			else {
				message = "Error Code: " + se.getErrorCode() + "\n" +
						"SQL State: " + se.getSQLState() + "\n" +
						"Message :" + se.getMessage() + "\n\n" +
						"Unknown SQL Exception occurred!";
			}
			renderMessage = true;
			return "FAIL";
		} catch(Exception e) {
			message = "Exception occurred: " + e.getMessage();
			if(connection != null) {
				try {
					connection.close();
				} catch (SQLException se) {
					message = "Error Code: " + se.getErrorCode() + "\n" +
							"SQL State: " + se.getSQLState() + "\n" +
							"Message :" + se.getMessage() + "\n\n" +
							"SQLException occurred. Couldn't close connection.";
				}
			}
			renderMessage = true;
			return "FAIL";
		}
	}

	public ResultSet getColumnNames(String sqlQuery)
	{
		try
		{
			ResultSet resultSet = statement.executeQuery(sqlQuery);
			return resultSet;
		} catch (SQLException se) {
			message = "Error Code: " + se.getErrorCode() + "\n" +
					"SQL State: " + se.getSQLState() + "\n" +
					"Message :" + se.getMessage() + "\n\n" +
					"SQLException while getting table columns.";
			return resultSet = null;
		} catch (Exception e) {
			message = "Exception occurred: " + e.getMessage();
			return resultSet = null;
		}
	}

	public ResultSet getTables()
	{
		try {
			DatabaseMetaData meta = (DatabaseMetaData) connection.getMetaData();
			resultSet = meta.getTables(null, null, "%", null);
			return resultSet;
		} catch (SQLException se) {
			message = "Error Code: " + se.getErrorCode() + "\n" +
					"SQL State: " + se.getSQLState() + "\n" +
					"Message :" + se.getMessage() + "\n\n" +
					"SQLException while getting tables.";
			return resultSet = null;
		} catch (Exception e) {
			message = "Exception occurred: " + e.getMessage();
			return resultSet = null;
		}
	}


	public int insertLogdata(String sqlQuery, String username, String dbms,
			String ipAddress, String sessionID)
	{
		try
		{
			java.sql.PreparedStatement query = connection.prepareStatement(sqlQuery);
			query.setString(1,username);
			query.setString(2,dbms);
			query.setTimestamp(3, new java.sql.Timestamp(new Date().getTime()));
			query.setString(4, ipAddress);
			query.setString(5, sessionID);
			query.executeUpdate();
			return 0;
		} catch (SQLException se) {
			message = "Error Code: " + se.getErrorCode() + "\n" +
					"SQL State: " + se.getSQLState() + "\n" +
					"Message :" + se.getMessage() + "\n\n" +
					"SQLException while getting column data.";
			return -1;
		} catch (Exception e) {
			message = "Exception occurred: " + e.getMessage();
			return -1;
		}
	}

	public int updateLogoutTime(String sqlQuery, String userName, int rowId)
	{
		try
		{
			java.sql.PreparedStatement query = connection.prepareStatement(sqlQuery);
			query.setTimestamp(1, new java.sql.Timestamp(new Date().getTime()));
			query.setString(2,userName);
			query.setInt(3,rowId);
			int rows = query.executeUpdate();
			return rows;
		} catch (SQLException se) {
			message = "Error Code: " + se.getErrorCode() + "\n" +
					"SQL State: " + se.getSQLState() + "\n" +
					"Message :" + se.getMessage() + "\n\n" +
					"SQLException while getting column data.";
			return -1;
		} catch (Exception e) {
			message = "Exception occurred: " + e.getMessage();
			return -1;
		}
	}

	public ResultSet getColumnData(String query)
	{
		try {
			ResultSet resultSet = statement.executeQuery(query);
			return resultSet;
		} catch (SQLException se) {
			message = "Error Code: " + se.getErrorCode() + "\n" +
					"SQL State: " + se.getSQLState() + "\n" +
					"Message :" + se.getMessage() + "\n\n" +
					"SQLException while getting column data.";
			return resultSet = null;
		} catch (Exception e) {
			message = "Exception occurred: " + e.getMessage();
			return resultSet = null;
		}
	}

	public ResultSet processSelect(String query)
	{
		try {
			resultSet = statement.executeQuery(query);
			return resultSet;
		} catch (SQLException se) {
			message = "Error Code: " + se.getErrorCode() + "\n" +
					"SQL State: " + se.getSQLState() + "\n" +
					"Message :" + se.getMessage() + "\n\n" +
					"SQLException while processing query.";
			return resultSet = null;
		} catch (Exception e) {
			message = "Exception occurred: " + e.getMessage();
			return resultSet = null;
		}
	}

	public int processUpdate(String sqlQuery)
	{
		try {       
			int count = statement.executeUpdate(sqlQuery);
			return count;
		} catch (SQLException se) {
			message = "Error Code: " + se.getErrorCode() + "\n" +
					"SQL State: " + se.getSQLState() + "\n" +
					"Message :" + se.getMessage() + "\n\n" +
					"SQLException while processing query.";
			return -1;
		} catch (Exception e) {
			message = "Exception occurred: " + e.getMessage();
			return -1;
		}
	}

	public ResultSet getTableData(String sqlQuery)
	{
		try {
			resultSet = statement.executeQuery(sqlQuery);
			return resultSet;
		} catch (SQLException se) {
			message = "Error Code: " + se.getErrorCode() + "\n" +
					"SQL State: " + se.getSQLState() + "\n" +
					"Message :" + se.getMessage() + "\n\n" +
					"SQLException while getting table data.";
			return resultSet = null;
		} catch (Exception e) {
			message = "Exception occurred: " + e.getMessage();
			return resultSet = null;
		}
	}

	public String createTables(String sql)
	{
		try {
			if(schema.equals(username))
			{
				statement.executeUpdate(sql);
				return "SUCCESS";
			}
			else
			{
				closeResources();
				if(connect().equalsIgnoreCase("success"))
				{
					statement.executeUpdate(sql);
					return "SUCCESS";
				}
				else
				{
					message = "Unable to connect to database schema!";
					return "FAIL";
				}
			}
		} catch (SQLException se) {
			message = "Error Code: " + se.getErrorCode() + "\n" +
					"SQL State: " + se.getSQLState() + "\n" +
					"Message :" + se.getMessage() + "\n\n" +
					"SQLException while creating table.";
			return "FAIL";
		} catch (Exception e) {
			message = "Exception occurred: " + e.getMessage();
			return "FAIL";
		}
	}

	public void closeResources()
	{
		try {
			resultSet.close();
			statement.close();
			connection.close();
		} catch (SQLException se) {
			message = "Error Code: " + se.getErrorCode() + "\n" +
					"SQL State: " + se.getSQLState() + "\n" +
					"Message :" + se.getMessage() + "\n\n" +
					"SQLException while closing connections.";
		}
		catch (Exception e)
		{
			message = "Exception occurred: " + e.getMessage();
		}
	}

	// getters
	public Statement getStatement() {
		return statement;
	}

	public String getUrl() {
		return url;
	}

	public String getDbms() {
		return dbms;
	}

	public String getPort() {
		return port;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public Connection getConnection() {
		return connection;
	}

	public ResultSet getResultSet() {
		return resultSet;
	}

	public String getJdbcDriver() {
		return jdbcDriver;
	}

	public String getHost() {
		return host;
	}

	public String getSchema() {
		return schema;
	}

	public String getMessage() {
		return message;
	}

	public Boolean getRenderMessage() {
		return renderMessage;
	}

	// setters
	public void setStatement(Statement statement) {
		this.statement = statement;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setDbms(String dbms) {
		this.dbms = dbms;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public void setResultSet(ResultSet resultSet) {
		this.resultSet = resultSet;
	}

	public void setJdbcDriver(String jdbcDriver) {
		this.jdbcDriver = jdbcDriver;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setRenderMessage(Boolean renderMessage) {
		this.renderMessage = renderMessage;
	}
	public ResultSetMetaData getResultSetMetaData() {
		return resultSetMetaData;
	}

	public void setResultSetMetaData(ResultSetMetaData resultSetMetaData) {
		this.resultSetMetaData = resultSetMetaData;
	}

	public DatabaseMetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(DatabaseMetaData metaData) {
		this.metaData = metaData;
	}

	public String getUserSchema() {
		return userSchema;
	}

	public void setUserSchema(String userSchema) {
		this.userSchema = userSchema;
	}
}

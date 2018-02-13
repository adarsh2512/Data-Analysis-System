package edu.uic.ids.bean;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@ManagedBean(name="log")
@SessionScoped
public class Log {
	@ManagedProperty(value="#{dbAccess}")
	private DbAccess dbAccess;
	private DbOperations dbOperations;
	private String message;
	private ResultSet resultSet;
	private List <String> tableList;
	private String currentUserName;
	private String password;
	private String userSchema;
	private Date loginTime;
	private Date logoutTime;
	private String getIpAddress;
	private String getSessionId;
	private String loginTimeString;
	private String logoutTimeString;
	private boolean renderLogs;
	private boolean renderMessage;
	private String dbms;
	private int maxRowId;

	private static final String USERNAME = "f16gxxx";
	private static final String PASSWORD = "f16gxxxR02S";

	public Log() {
/*		FacesContext context = FacesContext.getCurrentInstance();
		if(context!=null){
			HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
			dbAccess = (DbAccess)session.getAttribute("dbAccess");
		}*/
		tableList = new ArrayList < String > ();  
		renderLogs=false; 
		renderMessage=false;
	}

	public boolean createAccessLogs()
	{
		try
		{
			currentUserName = dbAccess.getUsername();
			password=dbAccess.getPassword();
			userSchema=dbAccess.getSchema();
			String sqlQuery = "Create table if not exists f16gxxx.s17g304_log (LogID INT(6)" +
					" NOT NULL AUTO_INCREMENT , Username char(50) not null, " +
					"dbms char(50) ,LoginTime char(50) null, LogoutTime char(50) null, " +
					"IPAddress char(50), SessionID char(50), PRIMARY KEY (LogID)) " +
					"ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;";
			FacesContext fCtx = FacesContext.getCurrentInstance();
			HttpSession session = (HttpSession) fCtx.getExternalContext().getSession(false);
			String sessionId = session.getId(); 
			boolean connect = connectToLogschema();
			HttpServletRequest request =
					(HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
			String ipAddress = request.getHeader("X-FORWARDED-FOR");
			if (ipAddress == null) 
				ipAddress = request.getRemoteAddr();
			//ipAddress = InetAddress.getLocalHost().getHostAddress().toString();
			if (connect) 
			{
				resultSet = dbAccess.getTables();
				if (resultSet != null) 
				{
					while (resultSet.next()) 
					{
						String tablenames = resultSet.getString("TABLE_NAME");
						tableList.add(tablenames);
					}
					if (tableList.contains("s17g304_log")) 
					{
						String insertQuery = "Insert into f16gxxx.s17g304_log (Username,dbms," +
								"LoginTime,IPAddress,SessionID)" +
								"values ( ?,?,?,?,?)";
						int rowsInserted = dbAccess.insertLogdata(insertQuery, currentUserName,
								userSchema, ipAddress, sessionId);
						if (rowsInserted < 0)
						{
							message = dbAccess.getMessage();
							reConnect();
							return false; 
						}
						else
						{
							maxRowId=getMaxRowId();
							reConnect();
							return true;
						}
					} 
					else
					{
						int rows = dbAccess.processUpdate(sqlQuery);
						if (rows < 0) 
						{
							message = dbAccess.getMessage();
							reConnect();
							return false;
						}
						else
						{
							String insertQuery = "Insert into f16gxxx.s17g304_log " +
									"(Username,dbms,LoginTime,IPAddress,SessionID)" +
									"values ( ?,?,?,?,?)";
							int rowsInserted = dbAccess.insertLogdata(insertQuery,
									currentUserName, userSchema, ipAddress, sessionId);
							if (rowsInserted < 0) 
							{
								message = dbAccess.getMessage();
								reConnect();
								return false;
							}
							else
							{
								maxRowId=getMaxRowId();
								reConnect();
								return true;
							}
						}
					}
				} 
				else 
				{
					message = dbAccess.getMessage();
					reConnect();
					return false;
				}
			}
			else
			{
				reConnect();
				return false;
			}
		} catch (Exception e) {
			message = e.getMessage();
			reConnect();
			return false;
		}
	}

	public int getMaxRowId() {
		int rowNumber = 0;
		try {
			String getRowId = "Select LogID from f16gxxx.s17g304_log where Username = '" + 
					currentUserName + "' order by LogID desc LIMIT 1";
			if(connectToLogschema())
			{
				resultSet = dbAccess.processSelect(getRowId);
				if (resultSet != null)
				{
					while (resultSet.next()) 
					{
						rowNumber = resultSet.getInt("LogID");
					}
				} 
				else 
				{
					message=dbAccess.getMessage();
					renderMessage=true;
					return rowNumber = -1;
				}
				return rowNumber;
			}
			else
			{
				message = dbAccess.getMessage();
				renderMessage = true;
				return -1;
			}
		} catch (Exception e) {
			message = e.getMessage();
			return rowNumber = -1;
		}
	}

	public boolean connectToLogschema() {
		dbAccess.setUsername(USERNAME);
		dbAccess.setPassword(PASSWORD);
		dbAccess.setSchema(USERNAME);
		if(dbAccess.connect().equalsIgnoreCase("SUCCESS"))
			return true;
		else
			return false;
	}

	public void reConnect() 
	{
		dbAccess.setUsername(currentUserName);
		dbAccess.setPassword(password);
		dbAccess.setSchema(userSchema);
		dbAccess.connect();
	}

	public boolean processLogout()
	{
		try
		{
			connectToLogschema();
			int rows = getMaxRowId();
			if(rows==0)
			{
				message = "Sorry no data found.";
				renderMessage=true;
				reConnect();
				return false;
			}
			else
			{
				String updateQuery="Update f16gxxx.s17g304_log set LogoutTime= ? where " +
						"Username = ? and LogID = ? and LoginTime is not null order " +
						"by LoginTime desc limit 1";
				dbAccess.updateLogoutTime(updateQuery, currentUserName, maxRowId);
			}
			return true;
		}
		catch(Exception e)
		{
			message=e.getMessage();
			return false;
		}
	}

	public String getLogData()
	{
		try {
			if(connectToLogschema())
			{
				int userRows = 0;
				String selectQuery="Select count(*)  count from f16gxxx.s17g304_log where userName = '" + 
						currentUserName + "' and LogID <> " + maxRowId + " order by logintime desc limit 1";
				resultSet=dbAccess.processSelect(selectQuery);
				if(resultSet!=null)
				{
					while(resultSet.next())
					{
						userRows=resultSet.getInt("count");
					}

					if(userRows == 0)
					{
						message = "This is your first login. Come back again to check your logs.";
						renderMessage = true;
					}
					else
					{
						String sqlQuery = "select Username,dbms,LoginTime,LogoutTime,sessionID,ipAddress from " +
								"f16gxxx.s17g304_log where Username = '" + currentUserName + "' and LogID <> " +
								maxRowId + " order by LoginTime DESC LIMIT 1";
						resultSet = dbAccess.processSelect(sqlQuery);
						if (resultSet != null)
						{
							while (resultSet.next())
							{
								currentUserName = resultSet.getString("Username");
								dbms = resultSet.getString("dbms");
								loginTimeString = resultSet.getTimestamp("LoginTime").toString();
								if(loginTimeString == null)
								{
									message = "This is your first login. Come back again to check your logs.";
									renderMessage = true;
									renderLogs = false;
								}
								else
								{
									getSessionId = resultSet.getString("sessionID");
									getIpAddress = resultSet.getString("ipAddress");
									try
									{
										logoutTimeString = resultSet.getTimestamp("LogoutTime").toString();
									} catch (NullPointerException ne) {
										logoutTimeString = "Log out was not successful the last time.";
									} catch(SQLException se) {
										logoutTimeString = "Log out was not successful the last time.";
									} catch (Exception e) {
										logoutTimeString = "Log out was not successful the last time.";
									}
								}
								renderLogs=true;
							}
						}
						else
						{
							message=dbAccess.getMessage();
							renderMessage=true;
						}
					}
				}
				else
				{
					message=dbAccess.getMessage();
					renderMessage=true;
				}
			}
			else
			{
				message = dbAccess.getMessage();
				renderMessage = true;
			}
			reConnect();
			processLogout();
			return "SUCCESS";
		} catch (SQLException e) {
			message=e.getMessage();
			renderMessage=true;
			reConnect();
			return "FAIL";
		} catch(Exception e) {
			reConnect();
			return "FAIL";
		}
	}

	public DbAccess getDbAccess() {
		return dbAccess;
	}

	public void setDbAccess(DbAccess dbAccess) {
		this.dbAccess = dbAccess;
	}

	public DbOperations getDbOperations() {
		return dbOperations;
	}

	public void setDbOperations(DbOperations dbOperations) {
		this.dbOperations = dbOperations;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ResultSet getResultSet() {
		return resultSet;
	}

	public void setResultSet(ResultSet resultSet) {
		this.resultSet = resultSet;
	}

	public List < String > getTableList() {
		return tableList;
	}

	public void setTableList(List < String > tableList) {
		this.tableList = tableList;
	}

	public String getCurrentUserName() {
		return currentUserName;
	}

	public void setCurrentUserName(String currentUserName) {
		this.currentUserName = currentUserName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserSchema() {
		return userSchema;
	}

	public void setUserSchema(String userSchema) {
		this.userSchema = userSchema;
	}

	public Date getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}

	public Date getLogoutTime() {
		return logoutTime;
	}

	public void setLogoutTime(Date logoutTime) {
		this.logoutTime = logoutTime;
	}

	public String getDbms() {
		return dbms;
	}

	public void setDbms(String dbms) {
		this.dbms = dbms;
	}

	public String getLoginTimeString() {
		return loginTimeString;
	}

	public void setLoginTimeString(String loginTimeString) {
		this.loginTimeString = loginTimeString;
	}

	public String getLogoutTimeString() {
		return logoutTimeString;
	}

	public void setLogoutTimeString(String logoutTimeString) {
		this.logoutTimeString = logoutTimeString;
	}

	public String getGetIpAddress() {
		return getIpAddress;
	}

	public void setGetIpAddress(String getIpAddress) {
		this.getIpAddress = getIpAddress;
	}

	public String getGetSessionId() {
		return getSessionId;
	}

	public void setGetSessionId(String getSessionId) {
		this.getSessionId = getSessionId;
	}

	public boolean isRenderLogs() {
		return renderLogs;
	}

	public void setRenderLogs(boolean renderLogs) {
		this.renderLogs = renderLogs;
	}

	public boolean isRenderMessage() {
		return renderMessage;
	}

	public void setRenderMessage(boolean renderMessage) {
		this.renderMessage = renderMessage;
	}
}
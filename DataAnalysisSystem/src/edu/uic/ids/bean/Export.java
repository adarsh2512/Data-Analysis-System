package edu.uic.ids.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.jstl.sql.Result;
import javax.servlet.jsp.jstl.sql.ResultSupport;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mysql.jdbc.DatabaseMetaData;

@ManagedBean(name="export")
@SessionScoped
public class Export {

	private String schema;
	private String tableSelected;
	private ResultSet resultSet;
	private boolean renderTablename;
	private ResultSetMetaData resultSetMetaData;
	private Result result;
	private boolean renderButton;
	private List<String> tableList;
	private List<String> schemaList;
	private DatabaseMetaData metaData;
	private UploadedFile uploadedFile;
	private String fileLabel;
	private String uploadedFileContents;
	private String fileName;
	private long fileSize;
	private String fileContentType;
	private String message;
	private boolean renderMessage;
	private String userSchema;
	@ManagedProperty(value="#{dbAccess}")
	private DbAccess dbAccess;
	private List<String> tableNames;

	public Export(){
/*		FacesContext context = FacesContext.getCurrentInstance();
		if(context!=null){
			HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
			dbAccess = (DbAccess)session.getAttribute("dbAccess");
		}*/
		renderTablename = false;
		renderMessage = false;
		tableList = new ArrayList<String>();
		schemaList = new ArrayList<String>();
	}

	public void renderTableList()
	{
		renderMessage=false;
		if(tableList.isEmpty())
		{
			message = "No tables found in the schema.";
			renderMessage = true;
			renderTablename = false;
		}
		else
			renderTablename = true;
	}

	public String exportCSV()
	{
		try {
			resetMessage();
			if(tableList.isEmpty())
			{
				message = "No tables found in the schema.";
				renderMessage = true;
				return "FAIL";
			}
			if(tableSelected.isEmpty())
			{
				message = "Please select table to export data.";
				renderMessage=true;
				return "FAIL";
			}
			else
			{
				FacesContext fc = FacesContext.getCurrentInstance();
				ExternalContext ec = fc.getExternalContext();
				FileOutputStream fos = null;
				String path = fc.getExternalContext().getRealPath("/temp");
				File dir = new File(path);
				if(!dir.exists())
					new File(path).mkdirs();
				ec.setResponseCharacterEncoding("UTF-8");
				String fileNameBase = dbAccess.getUsername()+"_"+tableSelected+".csv";
				String fileName = path + "/" + dbAccess.getUsername() + "_" + fileNameBase;
				File f = new File(fileName);
				resultSet = null;
				String sqlQuery = "select * from " + dbAccess.getSchema() + "." + tableSelected ;
				resultSet=dbAccess.processSelect(sqlQuery);
				if(resultSet!=null)
				{
					result = ResultSupport.toResult(resultSet);
					Object [][] sData = result.getRowsByIndex();
					String columnNames [] = result.getColumnNames();
					StringBuffer sb = new StringBuffer();
					try {
						fos = new FileOutputStream(fileName);
						for(int i=0; i<columnNames.length; i++) 
						{
							sb.append(columnNames[i].toString() + ",");
						}
						sb.append("\n");
						fos.write(sb.toString().getBytes());
						for(int i = 0; i < sData.length; i++) {
							sb = new StringBuffer();
							for(int j=0; j<sData[0].length; j++) {
								if(sData[i][j]==null)
								{
									String value2="0";
									value2=value2.replaceAll("[^A-Za-z0-9.]", " . ");
									if(value2.isEmpty())
									{
										value2="0";
									}
									sb.append(value2 + ",");
								}
								else
								{
									String value =sData[i][j].toString();
									if(value.contains(","))
									{
										int index=value.indexOf(",");
										String newValue=value.substring(0, index-1);
										value=newValue+value.substring(index+1,value.length());
									}
									//value=value.replaceAll("[^A-Za-z0-9,.]", " ");
									if(value.isEmpty())
									{
										value="0";
									}
									sb.append(value + ",");
								}
							}
							sb.append("\n");
							fos.write(sb.toString().getBytes());
						}
						fos.flush();
						fos.close();
					} catch (FileNotFoundException e) {
						message = e.getMessage();
						renderMessage = true; 
					} catch (IOException io) {
						message = io.getMessage();
						renderMessage = true;
					}
					String mimeType = ec.getMimeType(fileName);
					FileInputStream in = null;
					byte b;
					ec.responseReset();
					ec.setResponseContentType(mimeType);
					ec.setResponseContentLength((int) f.length());
					ec.setResponseHeader("Content-Disposition",
							"attachment; filename=\"" + fileNameBase + "\"");
					try {
						in = new FileInputStream(f);
						OutputStream output = ec.getResponseOutputStream();
						while(true) {
							b = (byte) in.read();
							if(b < 0)
								break;
							output.write(b);
						}
					} catch (IOException e) {
						message=e.getMessage();
						renderMessage=true;
					}
					finally
					{
						try { 
							in.close(); 
						} catch (IOException e) {
							message=e.getMessage();
							renderMessage=true;
						}
					}
					fc.responseComplete();
				} 
				else
				{
					message=dbAccess.getMessage();
					renderMessage=true;
				}
			}
			return "SUCCESS";
		} catch (Exception e) {
			message = e.getMessage();
			renderMessage = true;
			return "FAIL";
		}
	}

	public String exportXML() 
	{
		try {
			resetMessage();
			if(tableList.isEmpty())
			{
				message = "No tables found in the schema.";
				renderMessage = true;
				return "FAIL";
			}
			if(tableSelected.isEmpty())
			{
				message = "Please select table to export data.";
				renderMessage = true;
				return "FAIL";
			}
			else
			{
				try {
					FacesContext fc = FacesContext.getCurrentInstance();
					ExternalContext ec = fc.getExternalContext();
					String path = fc.getExternalContext().getRealPath("/temp");ec.setResponseCharacterEncoding("UTF-8");
					String fileNameBase = dbAccess.getUsername()+"_"+tableSelected+".xml";
					String fileName = path + "/" + dbAccess.getUsername() + "_" + fileNameBase;
					String sqlQuery = "select * from " + dbAccess.getSchema() + "." + tableSelected ;
					resultSet=dbAccess.processSelect(sqlQuery);
					if(resultSet!=null)
					{
						DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
						DocumentBuilder builder = factory.newDocumentBuilder();
						Document doc = builder.newDocument();
						Element results = doc.createElement(tableSelected);
						doc.appendChild(results);
						ResultSetMetaData rsmd = resultSet.getMetaData();
						int colCount = rsmd.getColumnCount();
						while (resultSet.next())
						{
							Element row = doc.createElement("Row");
							results.appendChild(row);
							int column=0;
							String stringColumn=null;
							int floatColumn=0;
							int smallInt=0;
							double doubleColumn=0;
							for (int i = 1; i <= colCount; i++)
							{
								String columnName = rsmd.getColumnName(i);
								String dataType=rsmd.getColumnTypeName(i);
								Element node = doc.createElement(columnName);
								switch(dataType.toLowerCase())
								{
								case("int") :
									column=resultSet.getInt(i);
								node.appendChild(doc.createTextNode(String.valueOf(column)));
								row.appendChild(node);     
								break;
								case("char"):
									stringColumn=resultSet.getString(i);
								node.appendChild(doc.createTextNode(String.valueOf(stringColumn)));
								row.appendChild(node);    
								break;
								case("smallint"):
									smallInt=resultSet.getInt(i);
								node.appendChild(doc.createTextNode(String.valueOf(smallInt)));
								row.appendChild(node);
								break;
								case("double") :
									doubleColumn=resultSet.getInt(i);
								node.appendChild(doc.createTextNode(String.valueOf(doubleColumn)));
								row.appendChild(node);
								break;
								case("float") :
									floatColumn=resultSet.getInt(i);
								node.appendChild(doc.createTextNode(String.valueOf(floatColumn)));
								row.appendChild(node);
								break;
								}
							}
						}
						TransformerFactory transformerFactory = TransformerFactory.newInstance();
						Transformer transformer = transformerFactory.newTransformer();
						transformer.setOutputProperty(OutputKeys.INDENT, "yes");
						DOMSource source = new DOMSource(doc);
						StreamResult file = new StreamResult(new File(fileName));
						transformer.transform(source, file);
						HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();
						response.setHeader("Content-Disposition", "attachment;filename=\"" + fileNameBase + "\"");
						response.setContentLength((int) fileName.length());
						FileInputStream input= null;
						try {
							int i= 0;
							input = new FileInputStream(fileName);  
							byte[] buffer = new byte[1024];
							while ((i = input.read(buffer)) != -1) {  
								response.getOutputStream().write(buffer,0,i);  
								response.getOutputStream().flush();  
							}               
							fc.responseComplete();
							fc.renderResponse();
							return "SUCCESS";
						} catch (IOException e) {
							message=e.getMessage();
							renderMessage=true;
							return "FAIL";
						} finally {
							try {
								if(input != null) {
									input.close();
								}
							} catch(IOException e) {
								message=e.getMessage();
								renderMessage=true;
								return "FAIL";
							}
						}
					}
					else
					{
						message=dbAccess.getMessage();
						renderMessage=true;
						return "FAIL";
					}
				} catch(ParserConfigurationException pe) {
					message=pe.getMessage();
					renderMessage=true;
					return "FAIL";
				} catch(SQLException se) {
					message=se.getMessage();
					renderMessage=true;
					return "FAIL";
				} catch(Exception e) {
					message=e.getMessage();
					renderMessage=true;
					return "FAIL";
				}
			}
		} catch (Exception e) {
			message = e.getMessage();
			renderMessage = true;
			return "FAIL";
		}
	}

	public String getTables()
	{
		try {
			resetMessage();
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

	public void importExportSchemaChange()
	{
		renderMessage=false;
		renderTablename=false;
		getTables();
	}

	public void resetMessage()
	{
		renderMessage=false;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getTableSelected() {
		return tableSelected;
	}

	public void setTableSelected(String tableSelected) {
		this.tableSelected = tableSelected;
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

	public boolean isRenderButton() {
		return renderButton;
	}

	public void setRenderButton(boolean renderButton) {
		this.renderButton = renderButton;
	}

	public List<String> getTableList() {
		return tableList;
	}

	public void setTableList(List<String> tableList) {
		this.tableList = tableList;
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

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isRenderMessage() {
		return renderMessage;
	}

	public void setRenderMessage(boolean renderMessage) {
		this.renderMessage = renderMessage;
	}

	public String getUserSchema() {
		return userSchema;
	}

	public void setUserSchema(String userSchema) {
		this.userSchema = userSchema;
	}

	public DbAccess getDbAccess() {
		return dbAccess;
	}

	public void setDbAccess(DbAccess dbAccess) {
		this.dbAccess = dbAccess;
	}

	public UploadedFile getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(UploadedFile uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

	public String getUploadedFileContents() {
		return uploadedFileContents;
	}

	public void setUploadedFileContents(String uploadedFileContents) {
		this.uploadedFileContents = uploadedFileContents;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public String getFileContentType() {
		return fileContentType;
	}

	public void setFileContentType(String fileContentType) {
		this.fileContentType = fileContentType;
	}

	public String getFileLabel() {
		return fileLabel;
	}

	public void setFileLabel(String fileLabel) {
		this.fileLabel = fileLabel;
	}

	public List<String> getTableNames() {
		return tableNames;
	}

	public void setTableNames(List<String> tableNames) {
		this.tableNames = tableNames;
	}

}

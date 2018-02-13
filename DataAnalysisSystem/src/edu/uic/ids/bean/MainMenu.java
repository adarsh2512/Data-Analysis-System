package edu.uic.ids.bean;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.http.HttpSession;

@ManagedBean(name="mainMenu")
@SessionScoped
public class MainMenu {

	@ManagedProperty(value="#{dbOperations}")
	private DbOperations dbOperations;
	@ManagedProperty(value="#{graphics}")
	private Graphics graphics;
	@ManagedProperty(value="#{export}")
	private Export export;
	@ManagedProperty(value="#{statisticsBean}")
	private StatisticsBean statisticsBean;
	@ManagedProperty(value="#{dbAccess}")
	private DbAccess dbAccess;
	@ManagedProperty(value="#{log}")
	private Log log;
	@ManagedProperty(value="#{dataImport}")
	private DataImport dataImport;
	private String message;
	private List<String> schemaList;
	private String userSchema;
	private String schema;
	private boolean renderSchema;
	public MainMenu()
	{
/*		FacesContext context = FacesContext.getCurrentInstance();
		if(context!=null){
			HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
			dbAccess = (DbAccess)session.getAttribute("dbAccess");
			statisticsBean = (StatisticsBean)session.getAttribute("statisticsBean");
			dbOperations = (DbOperations)session.getAttribute("dbOperations");
			graphics = (Graphics)session.getAttribute("graphics");
			export = (Export)session.getAttribute("export");
			log = (Log)session.getAttribute("log");
			dataImport = (DataImport)session.getAttribute("dataImport");
		}*/
		renderSchema=false;
		schemaList=new ArrayList<String>();
	}

	public void schemaChangeEvent(ValueChangeEvent event)
	{
		dbAccess.setSchema(event.getNewValue().toString());
		export.setSchema(event.getNewValue().toString());
		statisticsBean.setSchema((event.getNewValue().toString()));
		dbOperations.setUserSchema(dbAccess.getUsername());
		graphics.setSchema(event.getNewValue().toString());
		log.setUserSchema(event.getNewValue().toString());
		String status = dbAccess.connect();
		if(status.equals("SUCCESS"))
		{
			dbOperations.valueChanged();
			statisticsBean.statisticsSchemaChange();
			export.importExportSchemaChange();
			graphics.schemaValueChanged();
		}
		else
		{
			message = dbAccess.getMessage();
			dbOperations.setMessage(message);
			dbOperations.setRenderMessage(true);
			statisticsBean.setMessage(message);
			statisticsBean.setRenderMessage(true);
			graphics.setMessage(message);
			graphics.setRenderMessage(true);
			dbOperations.setMessage(message);
		}
	}

	public String processLogin()
	{
		String status = dbAccess.connect();
		if(status.equalsIgnoreCase("SUCCESS"))
		{
			log.createAccessLogs();
			getSchemas();
			dbOperations.getTables();
			statisticsBean.getTables();
			export.getTables();
			return "menu";
		}
		else
			return "FAIL";
	}
	
	public String reset(){
		try
		{
			dbOperations.reset();
			dbOperations.setRenderMessage(false);
			dbOperations.setRenderTablename(false);
			dbOperations.setColumnRender(false);
			dbOperations.setRenderCompute(false);
			
			graphics.reset();
			graphics.setRenderPieChart(false);
			graphics.setRenderBarChart(false);
			graphics.setXySeriesChart(false);
			graphics.setRenderTimeSeriesChart(false);
			graphics.setRenderXYGraphColumns(false);
			graphics.setRenderChartColumn(false);
			graphics.setRenderTables(false);
			
			statisticsBean.reset();
			statisticsBean.setRenderTablename(false);
			statisticsBean.setColumnRender(false);
			statisticsBean.setRenderRegressionColumn(false);
			statisticsBean.setRenderTableMetaData(false);
			
			export.setRenderMessage(false);
			export.setRenderTablename(false);
			
			dataImport.reset();
			return "menu";
			//FacesContext.getCurrentInstance().getExternalContext().redirect("menu.jsp");
		} catch(Exception e) {
			System.err.println(e.getMessage());
			return "FAIL";
		}
	}

	public DataImport getDataImport() {
		return dataImport;
	}

	public void setDataImport(DataImport dataImport) {
		this.dataImport = dataImport;
	}

	public String processLogout()
	{
		try
		{
			log.processLogout();
			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
			ec.invalidateSession();
			return "login";
		} catch(Exception e) {
			message = e.getMessage();
			dbOperations.setMessage(message);
			dbOperations.setRenderMessage(true);
			statisticsBean.setMessage(message);
			statisticsBean.setRenderMessage(true);
			graphics.setMessage(message);
			graphics.setRenderMessage(true);
			dbOperations.setMessage(message);
			return "FAIL";
		}
	}

	public Log getLog() {
		return log;
	}

	public void setLog(Log log) {
		this.log = log;
	}

	public void getSchemas ()
	{
		userSchema = dbAccess.getUsername();
		dbOperations.setUserSchema(userSchema);
		schema = dbAccess.getSchema();
		if(schema.equalsIgnoreCase("world"))
		{
			schemaList.add(schema);
			schemaList.add(userSchema);
		}
		else
		{
			schemaList.add(schema);
			schemaList.add("world");
		}
		renderSchema = true;
	}

	public DbOperations getDbOperations() {
		return dbOperations;
	}


	public void setDbOperations(DbOperations dbOperations) {
		this.dbOperations = dbOperations;
	}


	public Graphics getGraphics() {
		return graphics;
	}


	public void setGraphics(Graphics graphicAnalysis) {
		this.graphics = graphicAnalysis;
	}


	public Export getExport() {
		return export;
	}


	public void setExport(Export export) {
		this.export = export;
	}


	public StatisticsBean getStatisticsBean() {
		return statisticsBean;
	}


	public void setStatisticsBean(StatisticsBean statisticsBean) {
		this.statisticsBean = statisticsBean;
	}

	public DbAccess getDbAccess() {
		return dbAccess;
	}


	public void setDbAccess(DbAccess dbAccess) {
		this.dbAccess = dbAccess;
	}


	public Log getlog() {
		return log;
	}


	public void setlog(Log log) {
		this.log = log;
	}


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}
	public List<String> getSchemaList() {
		return schemaList;
	}

	public void setSchemaList(List<String> schemaList) {
		this.schemaList = schemaList;
	}

	public String getUserSchema() {
		return userSchema;
	}

	public void setUserSchema(String userSchema) {
		this.userSchema = userSchema;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public boolean isRenderSchema() {
		return renderSchema;
	}

	public void setRenderSchema(boolean renderSchema) {
		this.renderSchema = renderSchema;
	}
}
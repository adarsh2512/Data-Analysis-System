package edu.uic.ids.bean;
import java.sql.ResultSet;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.jstl.sql.Result;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.date.MonthConstants;

import com.mysql.jdbc.ResultSetMetaData;

@ManagedBean(name="visualAnalysis")
@SessionScoped
public class VisualAnalysis {

	private JFreeChart pieChart;
	private Result result;
	@ManagedProperty(value="#{dbAccess}")
	private DbAccess dbAccess;
	private ResultSet resultSet;
	private DefaultPieDataset pieModel;
	private ResultSetMetaData resultSetMetaData;
	private String errorMessage;
	private boolean renderErrorMessage;
	private boolean renderPieChart;
	private XYDataset data ;
	private DefaultCategoryDataset dataset ;
	@ManagedProperty(value="#{descStatistics}")
	private DescStatistics descStatistics;

	public VisualAnalysis()
	{		/*FacesContext context = FacesContext.getCurrentInstance();
	if(context!=null){
		HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
		dbAccess = (DbAccess)session.getAttribute("dbAccess");
		descStatistics = (DescStatistics)session.getAttribute("descStatistics");
	}
	*/	pieModel=new DefaultPieDataset();
		dataset = new DefaultCategoryDataset();
	}

	public boolean generateChart(String pieChartColumnSelected,String tableSelected) 
	{
		try
		{
			pieModel.clear();
			dataset.clear();
			double Q1 = descStatistics.getQ1();
			double Q3 = descStatistics.getQ3();
			double median = descStatistics.getMedian();
			int countMedian = 0;
			int countQ1 =0;
			int countQ3	=0;
			int greaterThanQ3 = 0;
			String sqlQuery="Select" + " "+ pieChartColumnSelected + " " +
					"from" + " "+dbAccess.getSchema()+"."+tableSelected;
			resultSet=dbAccess.processSelect(sqlQuery);
			resultSetMetaData = (ResultSetMetaData) resultSet.getMetaData();
			String datatype =resultSetMetaData.getColumnTypeName(1);
			int value=0;
			float floatColumn=0;
			double doubleColumn=0;
			int smallIntColumn=0;
			long longColumn=0;
			if(resultSet!=null)
			{
				while(resultSet.next())
				{
					switch(datatype.toLowerCase())
					{
					case "int":
						value=resultSet.getInt(1);
						if(value <= Q1)
						{
							countQ1++;
						}
						if(value> Q1 && value<=median)
							countMedian++;
						if(value> median && value<=Q3)
							countQ3++;
						if(value> Q3)
							greaterThanQ3++;
						break;
					case "smallint":
						smallIntColumn=resultSet.getInt(1);
						if(smallIntColumn <= Q1)
							countQ1++;
						if(smallIntColumn> Q1 && smallIntColumn<=median)
							countMedian++;
						if(smallIntColumn> median && smallIntColumn<=Q3)
							countQ3++;
						if(smallIntColumn> Q3)
							greaterThanQ3++;
						break;
					case "float":
						floatColumn=resultSet.getFloat(1);
						if(floatColumn <= Q1)
							countQ1++;
						if(floatColumn> Q1 && floatColumn<=median)
							countMedian++;
						if(floatColumn> median && floatColumn<=Q3)
							countQ3++;
						if(floatColumn> Q3)
							greaterThanQ3++;
						break;
					case "double":
						doubleColumn=resultSet.getDouble(1);
						if(doubleColumn <= Q1)
							countQ1++;
						if(doubleColumn> Q1 && doubleColumn<=median)
							countMedian++;
						if(doubleColumn> median && doubleColumn<=Q3)
							countQ3++;
						if(doubleColumn> Q3)
							greaterThanQ3++;
						break;
					case "long":
						longColumn=resultSet.getLong(1);
						if(longColumn <= Q1)
							countQ1++;
						if(longColumn> Q1 && longColumn<=median)
							countMedian++;
						if(longColumn> median && longColumn<=Q3)
							countQ3++;
						if(longColumn> Q3)
							greaterThanQ3++;
						break;
					}
				}
				pieModel.setValue("First Quartile", countQ1);
				pieModel.setValue("Between First Quartile and Median", countMedian);
				pieModel.setValue("Between Median and Third Quartile", countQ3);
				pieModel.setValue("Greater than Third Quartile", greaterThanQ3);
				dataset.addValue(countQ1," First Quartile ", "Category 1");
				dataset.addValue(countMedian,"Between First Quartile and Median ", "Category 2");
				dataset.addValue(countQ3,"Between Median and Third Quartile","Category 3");
				dataset.addValue(greaterThanQ3," Greater than Third Quartile ", "Category 4");
				return true;
			}
			else
			{
				errorMessage=dbAccess.getMessage();
				return false;
			}
		} catch(Exception e) {
			errorMessage=e.getMessage();
			return false;
		}
	}

	public boolean generateTimeSeriesPlot(String schema,String tableSelected,String columnSelected)
	{
		try
		{
			String sqlQuery="Select  "  + columnSelected +" " +  "from" + " "+schema +"."+
					tableSelected ;
			resultSet=dbAccess.processSelect(sqlQuery);
			resultSetMetaData = (ResultSetMetaData) resultSet.getMetaData();
			String datatype=resultSetMetaData.getColumnTypeName(1);
			TimeSeries series = new TimeSeries("Random Data");
			Day current = new Day(1, MonthConstants.JANUARY, 2016);
			if(resultSet!=null)
			{
				while(resultSet.next())
				{
					switch(datatype.toLowerCase())
					{
					case "int":
						series.add(current, resultSet.getInt(columnSelected));
						break;
					case "smallint":
						series.add(current, resultSet.getInt(1));
						break;
					case "float":
						series.add(current, resultSet.getFloat(1));
						break;
					case "double":
						series.add(current, resultSet.getDouble(1));
						break;
					case "long":
						series.add(current, resultSet.getLong(1));
						break;
					}
					current = (Day) current.next();
				}
				data = new TimeSeriesCollection(series);
				return true;
			}
			else
			{
				errorMessage=dbAccess.getMessage();
				return false;
			}
		} catch(Exception e) {
			errorMessage = e.getMessage();
			return false;
		}
	}

	JFreeChart chart = ChartFactory.createTimeSeriesChart(
			"Time Series Chart", "Date", "Rate",
			data, true, true, false);

	public DbAccess getDbAccess() {
		return dbAccess;
	}

	public void setDbAccess(DbAccess dbAccess) {
		this.dbAccess = dbAccess;
	}

	public DefaultPieDataset getPieModel() {
		return pieModel;
	}

	public void setPieModel(DefaultPieDataset pieModel) {
		this.pieModel = pieModel;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public ResultSet getResultSet() {
		return resultSet;
	}

	public void setResultSet(ResultSet resultSet) {
		this.resultSet = resultSet;
	}

	public JFreeChart getPieChart() {
		return pieChart;
	}

	public void setPieChart(JFreeChart pieChart) {
		this.pieChart = pieChart;
	}

	public ResultSetMetaData getResultSetMetaData() {
		return resultSetMetaData;
	}

	public void setResultSetMetaData(ResultSetMetaData resultSetMetaData) {
		this.resultSetMetaData = resultSetMetaData;
	}

	public boolean isRenderErrorMessage() {
		return renderErrorMessage;
	}

	public void setRenderErrorMessage(boolean renderErrorMessage) {
		this.renderErrorMessage = renderErrorMessage;
	}

	public boolean isRenderPieChart() {
		return renderPieChart;
	}

	public void setRenderPieChart(boolean renderPieChart) {
		this.renderPieChart = renderPieChart;
	}

	public XYDataset getData() {
		return data;
	}

	public void setData(XYDataset data) {
		this.data = data;
	}

	public DefaultCategoryDataset getDataset() {
		return dataset;
	}

	public void setDataset(DefaultCategoryDataset dataset) {
		this.dataset = dataset;
	}

	public DescStatistics getDescStatistics() {
		return descStatistics;
	}

	public void setDescStatistics(DescStatistics descStatistics) {
		this.descStatistics = descStatistics;
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}
}

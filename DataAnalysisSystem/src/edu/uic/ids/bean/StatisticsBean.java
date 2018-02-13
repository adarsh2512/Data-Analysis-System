package edu.uic.ids.bean;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.jstl.sql.Result;

import org.apache.commons.math3.distribution.FDistribution;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.mysql.jdbc.DatabaseMetaData;
import com.mysql.jdbc.ResultSetMetaData;

@ManagedBean(name="statisticsBean")
@SessionScoped
public class StatisticsBean {

	private int columnCount;
	private int rowsAffected;
	private String message;
	private String tableSelected;
	private String predictorValue;
	private String responseValue;
	private boolean columnRender;	
	private boolean renderSchema;
	private boolean buttonDisable;
	private boolean renderMessage;
	private boolean renderReport;
	private boolean renderTabledata;
	private boolean renderTablename;
	private boolean renderRegressionColumn;
	private boolean renderRegressionButton;
	private boolean renderColumnListbutton;
	private boolean renderRegressionResult;
	private boolean renderTableMetaData;

	private List<String> numericData;
	private List<String> categoricalData;
	private List<String> columnSelected;
	private List<String> columnsList;
	private List<String> tableList;
	private List<String> columns;
	private List<String> list;
	private XYSeries xySeries;
	private XYSeriesCollection xySeriesVariables;
	private double quartile1;
	private double quartile3;
	private double median1;
	private List<DescStatistics> statisticList;
	private Result result;
	private ResultSet resultSet;
	@ManagedProperty(value="#{dbAccess}")
	private DbAccess dbAccess;
	private DatabaseMetaData metaData;
	private ResultSetMetaData resultSetMetaData;
	@ManagedProperty(value="#{descStatistics}")
	private DescStatistics descStatistics;
	@ManagedProperty(value="#{regression}")
	private Regression regression;
	private XYSeriesCollection xySeriesVariable;
	private XYSeriesCollection xyTimeSeriesCollection; 
	private XYSeries predictorSeries;
	private XYSeries responseSeries;
	private String errorMessage;
	private int rowcount;
	private int colcount;
	private String schema;

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public StatisticsBean(){		
/*		FacesContext context = FacesContext.getCurrentInstance();
		if(context!=null){
			HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
			dbAccess = (DbAccess)session.getAttribute("dbAccess");
			descStatistics = (DescStatistics)session.getAttribute("descStatistics");
			regression = (Regression)session.getAttribute("regression");
		}*/
		columnSelected = new ArrayList<String>();
		columnsList = new ArrayList<String>();
		columns= new ArrayList<String>();	
		renderTabledata=false;
		statisticList= new ArrayList<DescStatistics>();
		categoricalData= new ArrayList<String>();
		numericData= new ArrayList<String>();
		buttonDisable=false;
		renderRegressionButton=true;
		renderReport=false;
		tableList=new ArrayList<String>();
		list= new ArrayList<String>();
		xySeries= new XYSeries("Random");
		xySeriesVariable= new XYSeriesCollection();
		renderTablename=false;
		xyTimeSeriesCollection = new XYSeriesCollection();
		predictorSeries= new XYSeries("Predictor");
		responseSeries = new XYSeries("Response");	
		renderTableMetaData=false;
		getTables();
	}

	public String getTables()
	{
		try {
			reset();
			tableList = new ArrayList<String>();
			String tablenames;
			resultSet = dbAccess.getTables();
			if(resultSet != null)
			{
				while(resultSet.next())
				{
					tablenames = resultSet.getString("TABLE_NAME");
					tableList.add(tablenames);
				}
				renderTableList();
			}
			else
			{
				message = dbAccess.getMessage();
				renderMessage = true;
			}
			return "SUCCESS";
		} catch (Exception e) {
			message = e.getMessage();
			renderMessage = true;
			return "FAIL";
		}
	}

	public void renderTableList()
	{
		reset();
		if(tableList.isEmpty())
		{
			message = "No tables found in the schema.";
			columnRender=false;
			renderRegressionResult=false;
			columnRender = false;
			renderRegressionColumn=false;
			renderTabledata = false;
			renderMessage = true;
			renderTablename = false;
			columnRender = false;
			renderMessage = true;
		}
		else
			renderTablename = true;
	}

	public String getRegressionColumnNames()
	{
		reset();
		if(tableList.isEmpty())
		{		
			message = "No tables found in the schema.";
			renderMessage = true;
			return "FAIL";
		}
		if(tableSelected.isEmpty())
		{		
			message = "Please select a table.";
			renderMessage = true;
			return "FAIL";
		}
		if(generateRegressionColumns())
		{
			return "SUCCESS";
		}
		else
		{
			renderMessage=true;
			return "FAIL";
		}
	}

	public boolean generateRegressionColumns()
	{
		try
		{
			String sqlQuery = "select * from " + dbAccess.getSchema() +
					"." + tableSelected;		
			resultSet = dbAccess.getColumnNames(sqlQuery);
			if(resultSet != null)
			{
				columnsList.clear();
				categoricalData.clear();
				numericData.clear();
				ResultSetMetaData  resultSetmd=(ResultSetMetaData) resultSet.getMetaData();
				int columnCount = resultSetmd.getColumnCount();
				for (int i = 1; i <= columnCount; i++ ) {
					String name = resultSetmd.getColumnName(i);
					String datatype = resultSetmd.getColumnTypeName(i);
					if(datatype.equalsIgnoreCase("char")||datatype.equalsIgnoreCase("varchar"))
					{
						categoricalData.add(name);
					}
					else
						numericData.add(name);
				}
				columnRender = true;
			}
			else
			{
				message = dbAccess.getMessage();
				renderMessage = true;
				return false;
			}
			return true;
		} catch(Exception e) {
			message = e.getMessage();
			renderMessage = true;
			return false;
		}
	}

	public String splitColumns()
	{
		try {
			reset();
			if(tableSelected != null && columnSelected != null)
			{
				List<String> columnSeperated = new ArrayList<String>();
				for (int i = 0; i < columnSelected.size(); i++) 
				{
					String data = columnSelected.get(i);
					int index = data.indexOf(" ");
					String column = data.substring(0, index);
					String datatype = data.substring((index + 1), data.length());
					if(datatype.equalsIgnoreCase("CHAR")|| datatype.equalsIgnoreCase("VARCHAR"))
					{
						message = "Categorical variables are not allowed";
						return "FAIL";
					}
					else{
						columnSeperated.add(column);
					}
				}
				columnSelected = new ArrayList<String>();
				columnSelected = columnSeperated;
				list.clear();
				list = columnSelected;
				columnSeperated = null;
				return "SUCCESS";
			}
			else
			{
				message = "Please select a table and a column.";
				return "FAIL";
			}
		} catch (Exception e) {
			message = e.getMessage();
			renderMessage = true;
			return "FAIL";
		}
	}

	public String generateReport()
	{
		statisticList= new ArrayList<DescStatistics>();
		reset();
		if(tableList.isEmpty())
		{
			message = "No tables found in the schema.";
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
		if(splitColumns().equalsIgnoreCase("FAIL"))
		{
			renderMessage = true;
			return "FAIL";
		}
		else
		{
			if(calculateVariables().equals("FAIL"))
			{
				renderMessage=true;
				return "FAIL";
			}
			else
			{
				return "SUCCESS";
			}
		}
	}

	public String calculateVariables()
	{
		try
		{
			for(int k=0;k<list.size();k++)
			{
				String sqlQuery = "select " + list.get(k) + " from " + dbAccess.getSchema() +
						"." + tableSelected;
				resultSet = dbAccess.processSelect(sqlQuery);
				if (resultSet == null)
				{
					message = dbAccess.getMessage();
					renderMessage = true;
					return "FAIL";
				}
				resultSetMetaData = (ResultSetMetaData) resultSet.getMetaData();
				columnCount = resultSetMetaData.getColumnCount();
				String columnName;
				for(int j=1;j<columnCount+1;j++)
				{
					List<Double> values = new ArrayList<Double>();
					columnName = resultSetMetaData.getColumnName(j);
					String columnType=resultSetMetaData.getColumnTypeName(j);
					while(resultSet.next())
					{
						switch(columnType.toLowerCase())
						{
						case "int":
							values.add((double) resultSet.getInt(columnName));
							break;
						case "smallint":
							values.add((double) resultSet.getInt(columnName));
							break;
						case "float":
							values.add((double) resultSet.getFloat(columnName));
							break;
						case "double":
							values.add((double) resultSet.getDouble(columnName));
							break;
						case "long":
							values.add((double) resultSet.getLong(columnName));
							break;
						default:
							values.add((double) resultSet.getDouble(columnName));
							break;
						}
					}
					double[] valuesArray= new double[values.size()];
					for(int i=0;i<values.size();i++)
					{
						valuesArray[i]= (double)values.get(i);
					}
					double minValue = MathUtil.round(StatUtils.min(valuesArray),100);
					double maxValue =  MathUtil.round(StatUtils.max(valuesArray),100);
					double mean =  MathUtil.round(StatUtils.mean(valuesArray),100);
					double variance =  MathUtil.round(StatUtils.variance(valuesArray, mean),100);
					double std =  MathUtil.round(Math.sqrt(variance),100);
					double median =  MathUtil.round(StatUtils.percentile(valuesArray, 50.0),100);
					double q1 =  MathUtil.round(StatUtils.percentile(valuesArray, 25.0),100);
					double q3 =  MathUtil.round(StatUtils.percentile(valuesArray, 75.0),100);
					double iqr = q3 - q1;
					double range = maxValue - minValue;
					statisticList.add(new DescStatistics(columnName, minValue, maxValue,
							mean, variance, std, median, q1, q3, iqr, range));
					descStatistics.setVariablesforGraph(q1, q3, median);
				}
				renderTabledata = true;
			}
			return "SUCCESS";
		} catch(Exception e) {
			message = e.getMessage();
			renderMessage = true;
			return "FAIL";
		}
	}

	public String getColumnNames ()
	{
		try {
			reset();
			getTables();
			if(tableList.isEmpty())
			{
				message = "No tables found in the schema.";
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
				columnsList.clear();
				String sqlQuery = "select * from " + dbAccess.getSchema() +
						"." + tableSelected;
				ResultSet resultSet = dbAccess.getColumnNames(sqlQuery);
				if(resultSet!=null)
				{

					ResultSetMetaData  resultSetmd=(ResultSetMetaData) resultSet.getMetaData();
					int columnCount = resultSetmd.getColumnCount();
					for (int i = 1; i <= columnCount; i++ ) {
						String name = resultSetmd.getColumnName(i);
						String datatype = resultSetmd.getColumnTypeName(i);
						columns.add(name);
						columnsList.add(name + " " + datatype);
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

	public String displayColumnsforRegression()
	{
		reset();
		if(tableList.isEmpty())
		{
			message = "No tables found in the schema.";
			renderMessage = true;
			renderColumnListbutton = true;
			renderReport = true;
			renderRegressionColumn = false;
			return "FAIL";
		}
		if(tableSelected == null)
		{
			message = "Please select a table.";
			renderMessage = true;
			renderColumnListbutton = true;
			renderReport=true;
			return "FAIL";
		}
		String status = getRegressionColumnNames();
		if(status.equalsIgnoreCase("SUCCESS"))
		{
			columnRender = false;
			renderRegressionButton = false;
			renderRegressionColumn = true;
			renderColumnListbutton = true;
			renderReport = true;

			return "SUCCESS";
		}
		else {
			renderMessage=true;
			return "FAIL";
		}
	}

	public String generateRegressionReport()
	{
		reset();
		if (tableList.isEmpty())
		{
			message = "No tables found in the schema.";
			renderMessage = true;
			renderColumnListbutton = true;
			renderReport = true;
			renderRegressionColumn = false;
			return "FAIL";
		}
		if (tableSelected == null)
		{
			message = "Please select a table.";
			renderMessage=true;
			return "FAIL";
		}
		if (predictorValue == null || responseValue == null)
		{
			message = "Please select a predictor and a response variable.";
			renderMessage = true;
			return "FAIL";
		}

		if(responseValue.equals("0") || predictorValue.equals("0"))
		{
			message="Please select a predictor and a response variable.";
			renderMessage=true;
			return "FAIL";
		}
		if(responseValue.equals(predictorValue) || predictorValue.equals(responseValue))
		{
			message="Predictor and Response variable cannot be the same";
			renderMessage=true;
			return "FAIL";
		}
		if (calculateRegressionVariables())
		{
			return "SUCCESS";
		}
		else
			return "FAIL";
	}

	public boolean calculateRegressionVariables()
	{
		try {			
			responseSeries.clear();
			predictorSeries.clear();
			xySeries.clear();
			xySeriesVariable.removeAllSeries();
			xyTimeSeriesCollection.removeAllSeries();
			String sqlQuery = "select " + predictorValue + ", " + responseValue + 
					" from "+ dbAccess.getSchema() + "." + tableSelected;
			resultSet = dbAccess.processSelect(sqlQuery);
			if(resultSet!= null)
			{
				resultSetMetaData = (ResultSetMetaData) resultSet.getMetaData();
				String predictorName = resultSetMetaData.getColumnTypeName(1);
				String responseName = resultSetMetaData.getColumnTypeName(2);
				List<Double> predictorList = new ArrayList<Double>();
				List<Double> responseList = new ArrayList<Double>();
				while(resultSet.next())
				{
					switch(predictorName.toLowerCase())
					{
					case "int":
						predictorList.add((double) resultSet.getInt(1));
						break;
					case "smallint":
						predictorList.add((double) resultSet.getInt(1));
						break;
					case "float":
						predictorList.add((double)resultSet.getFloat(1));
						break;
					case "double":
						predictorList.add((double) resultSet.getDouble(1));
						break;
					case "long":
						predictorList.add((double) resultSet.getLong(1));
						break;
					default:
						predictorList.add((double) resultSet.getDouble(1));
						break;
					}
					switch(responseName.toLowerCase())
					{
					case "int":
						responseList.add((double) resultSet.getInt(2));
						break;
					case "smallint":
						responseList.add((double) resultSet.getInt(2));
						break;
					case "float":
						responseList.add((double)resultSet.getFloat(2));
						break;
					case "double":
						responseList.add((double) resultSet.getDouble(2));
						break;
					case "long":
						responseList.add((double) resultSet.getLong(2));
						break;
					default:
						responseList.add((double) resultSet.getDouble(2));
						break;
					}
				}
				double[] predictorArray = new double[predictorList.size()];
				for(int i=0;i<predictorList.size();i++)
				{
					predictorArray[i]= (double)predictorList.get(i);
					predictorSeries.add(i+1, (double)predictorList.get(i));
				}
				double[] responseArray= new double[responseList.size()];
				for(int i=0;i<responseList.size();i++)
				{
					responseArray[i]= (double)responseList.get(i);
					responseSeries.add(i+1, (double)responseList.get(i));
				}
				xyTimeSeriesCollection.addSeries(predictorSeries);
				xyTimeSeriesCollection.addSeries(responseSeries);
				SimpleRegression sr = new SimpleRegression();
				if(responseArray.length > predictorArray.length)
				{
					for(int i=0;i<predictorArray.length;i++)
					{
						sr.addData(predictorArray[i], responseArray[i]);
						xySeries.add(predictorArray[i], responseArray[i]);
					}
				}
				else
				{
					for(int i=0;i<responseArray.length;i++)
					{
						sr.addData(predictorArray[i], responseArray[i]);
						xySeries.add(predictorArray[i], responseArray[i]);
					}
				}
				xySeriesVariable.addSeries(xySeries);
				int totalDF = responseArray.length-1;
				TDistribution tDistribution = new TDistribution(totalDF);
				double intercept = sr.getIntercept();
				double interceptStandardError = sr.getInterceptStdErr();
				double tStatistic = 0;
				int predictorDF = 1;
				int residualErrorDF = totalDF - predictorDF;
				double rSquare = sr.getRSquare();
				double rSquareAdjusted = rSquare - (1 - rSquare)/(totalDF - predictorDF - 1);
				if(interceptStandardError!=0){
					tStatistic = (double)intercept/interceptStandardError;
				}
				double interceptPValue =
						(double)2*tDistribution.cumulativeProbability(-Math.abs(tStatistic));
				double slope = sr.getSlope();
				double slopeStandardError = sr.getSlopeStdErr();
				double tStatisticpredict = 0;
				if(slopeStandardError != 0) {
					tStatisticpredict = (double)slope/slopeStandardError;
				}
				double pValuePredictor =
						(double)2*tDistribution.cumulativeProbability(-Math.abs(tStatisticpredict));
				double standardErrorModel = Math.sqrt(StatUtils.variance(responseArray))*(Math.sqrt(1-rSquareAdjusted));
				double regressionSumSquares = sr.getRegressionSumSquares();
				double sumSquaredErrors = sr.getSumSquaredErrors();
				double totalSumSquares = sr.getTotalSumSquares();
				double meanSquare = 0;
				if(predictorDF!=0) {
					meanSquare = regressionSumSquares/predictorDF;
				}
				double meanSquareError = 0;
				if(residualErrorDF != 0) {
					meanSquareError = (double)(sumSquaredErrors/residualErrorDF);
				}
				double fValue = 0;
				if(meanSquareError != 0) {
					fValue = meanSquare/meanSquareError;
				}
				String regressionEquation = responseValue +
						" = " + intercept + " + (" + slope + ") " +
						predictorValue;
				FDistribution fDistribution = new FDistribution(predictorDF, residualErrorDF);
				double pValue = (double)(1-fDistribution.cumulativeProbability(fValue));
				boolean regressionResultsStatus =
						regression.setRegressionAnalysisVariables(
								regressionEquation, intercept, interceptStandardError,
								tStatistic, interceptPValue, slope, slopeStandardError,
								tStatisticpredict, pValuePredictor, standardErrorModel,
								rSquare, rSquareAdjusted, predictorDF, residualErrorDF,
								totalDF, regressionSumSquares, sumSquaredErrors, totalSumSquares,
								meanSquare, meanSquareError, fValue, pValue);
				if(regressionResultsStatus)
				{
					renderRegressionResult = true;
					return true;
				}
				else
				{
					message = regression.getMessage();
					renderMessage = true;
					return false;
				}
			}
			else
			{
				message=dbAccess.getMessage();
				renderMessage=true;
				return false;
			}
		} catch(Exception e) {
			message = e.getMessage();
			renderMessage = true;
			return false;
		}
	}

	public boolean onChartTypeChange()
	{
		if(getTables().equals("SUCCESS"))
		{
			renderRegressionColumn=false;
			renderTablename=false;
			return true;
		}
		else
		{
			errorMessage=message;
			return false;
		}
	}

	public boolean generateResultsforGraph()
	{
		if(calculateVariables().equals("SUCCESS"))
		{
			renderTabledata=false;
			return true;
		}
		renderTabledata=false;
		errorMessage = message;
		return false;
	}

	public boolean onTableChange()
	{
		if(generateRegressionColumns())
		{
			renderRegressionColumn=false;
			return true;
		}
		else
		{
			errorMessage = message;
			return false;
		}
	}

	public boolean generateRegressionResults()
	{
		xySeries.clear();
		xySeriesVariable.removeAllSeries();
		if(calculateRegressionVariables())
		{
			renderRegressionResult=false;
			return true;
		}
		else
		{
			errorMessage = message;
			return false;
		}
	}

	public void statisticsSchemaChange()
	{
		columnsList = new ArrayList<String>();
		tableList=new ArrayList<String>();
		list= new ArrayList<String>();
		statisticList= new ArrayList<DescStatistics>();
		categoricalData= new ArrayList<String>();
		columnSelected = new ArrayList<String>();
		columnsList = new ArrayList<String>();
		columns= new ArrayList<String>();	
		columnRender=false;
		renderTabledata=false;
		renderRegressionResult=false;
		renderMessage=false;
		columnRender=false;
		resetButton();
	}

	public String resetButton()
	{
		columnRender = false;
		renderRegressionButton = true;
		renderColumnListbutton = false;
		renderRegressionColumn = false;
		renderRegressionResult = false;
		renderReport = false;
		renderMessage = false;
		renderTableMetaData=false;
		columnSelected.clear();
		statisticList.clear();
		renderTabledata = false;
		return "SUCCESS";
	}
	public String getTableMetaData()
	{
		try {
			reset();
			getTables();
			if(tableList.isEmpty())
			{
				message = "No tables found in the schema.";
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
				String sqlQuery = "select * from " + dbAccess.getSchema() +
						"." + tableSelected;
				ResultSet resultSet = dbAccess.getColumnNames(sqlQuery);
				if(resultSet!=null)
				{

					ResultSetMetaData  resultSetmd=(ResultSetMetaData) resultSet.getMetaData();
					setColcount(resultSetmd.getColumnCount());
					resultSet.last();
					setRowcount(resultSet.getRow());
					for (int i = 1; i <= columnCount; i++ ) {
						String name = resultSetmd.getColumnName(i);
						String datatype = resultSetmd.getColumnTypeName(i);
						columns.add(name);
						columnsList.add(name + " " + datatype);
					}
					renderTableMetaData=true;

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


	public boolean isRenderTableMetaData() {
		return renderTableMetaData;
	}

	public void setRenderTableMetaData(boolean renderTableMetaData) {
		this.renderTableMetaData = renderTableMetaData;
	}

	public void reset() {
		renderMessage = false;
		renderTabledata=false;
		renderRegressionResult=false;
		
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

	public List<String> getColumnsList() {
		return columnsList;
	}

	public void setColumnsList(List<String> columnsList) {
		this.columnsList = columnsList;
	}

	public boolean isColumnRender() {
		return columnRender;
	}

	public void setColumnRender(boolean columnRender) {
		this.columnRender = columnRender;
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

	public List<String> getTableList() {
		return tableList;
	}

	public void setTableList(List<String> tableList) {
		this.tableList = tableList;
	}

	public DatabaseMetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(DatabaseMetaData metaData) {
		this.metaData = metaData;
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

	public boolean isRenderMessage() {
		return renderMessage;
	}

	public void setRenderMessage(boolean renderMessage) {
		this.renderMessage = renderMessage;
	}

	public DbAccess getDbAccess() {
		return dbAccess;
	}

	public void setDbAccess(DbAccess dbAccess) {
		this.dbAccess = dbAccess;
	}

	public List<String> getColumns() {
		return columns;
	}

	public void setColumns(List<String> columns) {
		this.columns = columns;
	}

	public int getColumnCount() {
		return columnCount;
	}

	public void setColumnCount(int columnCount) {
		this.columnCount = columnCount;
	}

	public int getRowsAffected() {
		return rowsAffected;
	}

	public void setRowsAffected(int rowsAffected) {
		this.rowsAffected = rowsAffected;
	}

	public List<DescStatistics> getStatisticList() {
		return statisticList;
	}

	public void setStatisticList(List<DescStatistics> statisticList) {
		this.statisticList = statisticList;
	}

	public boolean isRenderTabledata() {
		return renderTabledata;
	}

	public void setRenderTabledata(boolean renderTabledata) {
		this.renderTabledata = renderTabledata;
	}

	public DescStatistics getDescStatistics() {
		return descStatistics;
	}

	public void setDescStatistics(DescStatistics descStatistics) {
		this.descStatistics = descStatistics;
	}

	public boolean isRenderRegressionColumn() {
		return renderRegressionColumn;
	}

	public void setRenderRegressionColumn(boolean renderRegressionColumn) {
		this.renderRegressionColumn = renderRegressionColumn;
	}

	public boolean isRenderColumnListbutton() {
		return renderColumnListbutton;
	}

	public void setRenderColumnListbutton(boolean renderColumnListbutton) {
		this.renderColumnListbutton = renderColumnListbutton;
	}

	public boolean isRenderRegressionButton() {
		return renderRegressionButton;
	}

	public void setRenderRegressionButton(boolean renderRegressionButton) {
		this.renderRegressionButton = renderRegressionButton;
	}

	public boolean isButtonDisable() {
		return buttonDisable;
	}

	public void setButtonDisable(boolean buttonDisable) {
		this.buttonDisable = buttonDisable;
	}

	public List<String> getCategoricalData() {
		return categoricalData;
	}

	public void setCategoricalData(List<String> categoricalData) {
		this.categoricalData = categoricalData;
	}

	public List<String> getNumericData() {
		return numericData;
	}

	public void setNumericData(List<String> numericData) {
		this.numericData = numericData;
	}

	public String getPredictorValue() {
		return predictorValue;
	}

	public void setPredictorValue(String predictorValue) {
		this.predictorValue = predictorValue;
	}

	public String getResponseValue() {
		return responseValue;
	}

	public void setResponseValue(String responseValue) {
		this.responseValue = responseValue;
	}

	public boolean isRenderReport() {
		return renderReport;
	}

	public void setRenderReport(boolean renderReport) {
		this.renderReport = renderReport;
	}

	public boolean isRenderRegressionResult() {
		return renderRegressionResult;
	}

	public void setRenderRegressionResult(boolean renderRegressionResult) {
		this.renderRegressionResult = renderRegressionResult;
	}




	public Regression getRegression() {
		return regression;
	}

	public void setRegression(Regression regression) {
		this.regression = regression;
	}

	public double getMedian1() {
		return median1;
	}

	public void setMedian1(double median1) {
		this.median1 = median1;
	}

	public double getQuartile1() {
		return quartile1;
	}

	public void setQuartile1(double quartile1) {
		this.quartile1 = quartile1;
	}

	public double getQuartile3() {
		return quartile3;
	}

	public void setQuartile3(double quartile3) {
		this.quartile3 = quartile3;
	}



	public XYSeriesCollection getXySeriesVariable() {
		return xySeriesVariable;
	}

	public void setXySeriesVariable(XYSeriesCollection xySeriesVariable) {
		this.xySeriesVariable = xySeriesVariable;
	}
	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public XYSeriesCollection getXyTimeSeriesCollection() {
		return xyTimeSeriesCollection;
	}

	public void setXyTimeSeriesCollection(XYSeriesCollection xyTimeSeriesCollection) {
		this.xyTimeSeriesCollection = xyTimeSeriesCollection;
	}

	public XYSeriesCollection getXySeriesVariables() {
		return xySeriesVariables;
	}

	public void setXySeriesVariables(XYSeriesCollection xySeriesVariables) {
		this.xySeriesVariables = xySeriesVariables;
	}

	public int getColcount() {
		return colcount;
	}

	public void setColcount(int colcount) {
		this.colcount = colcount;
	}

	public int getRowcount() {
		return rowcount;
	}

	public void setRowcount(int rowcount) {
		this.rowcount = rowcount;
	}
}

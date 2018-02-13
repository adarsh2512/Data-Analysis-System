package edu.uic.ids.bean;

import java.awt.Color;
import java.awt.Shape;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.http.HttpSession;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.function.LineFunction2D;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.statistics.Regression;
import org.jfree.data.xy.XYDataset;
import org.jfree.util.ShapeUtilities;

@ManagedBean(name="graphics")
@SessionScoped
public class Graphics {

	private String tableSelected;
	private String message;
	private boolean renderMessage;
	private boolean renderChartType;
	private String chartColumnSelected;
	private boolean renderChartColumn;
	private boolean renderXYGraphColumns;
	private String predictorValue;
	private String responseValue;
	@ManagedProperty(value="#{statisticsBean}")
	private StatisticsBean statisticsBean;
	private String chartType;
	private boolean renderRegressionColumn;
	private boolean renderTables;
	private boolean renderGenerategraphButton;
	private String schema;
	@ManagedProperty(value="#{visualAnalysis}")
	private VisualAnalysis visualAnalysis;
	private String pieChartPath;
	@ManagedProperty(value="#{dbAccess}")
	private DbAccess dbAccess;
	private boolean renderPieChart;
	private boolean renderBarChart;
	private boolean xySeriesChart;
	private boolean renderTimeSeriesChart;
	private String xyChartPath;
	private String barChartPath;
	private boolean  xyTimeSeriesChart;
	private String xyTimeSeriesPath;
	// DbAccess dbAccess;

	public Graphics()
	{
/*		FacesContext context = FacesContext.getCurrentInstance();
		if(context!=null){
			HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
			dbAccess = (DbAccess)session.getAttribute("dbAccess");
			visualAnalysis = (VisualAnalysis)session.getAttribute("visualAnalysis");
			statisticsBean = (StatisticsBean)session.getAttribute("statisticsBean");
		}*/
		renderGenerategraphButton=false;
		renderChartColumn=false;
		renderTables=false;
		renderRegressionColumn=false;
		renderXYGraphColumns=false;
		renderChartColumn=false;
		renderPieChart=false;
		renderBarChart=false;
		xySeriesChart=false;
		renderTimeSeriesChart=false;
		xySeriesChart=false;
	}

	public void schemaValueChanged()
	{
		renderGenerategraphButton=false;
		renderRegressionColumn=false;
		renderXYGraphColumns=false;
		renderChartColumn=false;
		renderPieChart=false;
		renderBarChart=false;
		xySeriesChart=false;
		renderTimeSeriesChart=false;
		xySeriesChart=false;
		renderTables=false;
		tableSelected=null;
		chartColumnSelected=null;
		predictorValue=null;
		responseValue=null;
		if(statisticsBean.getTables().equals("SUCCESS"))
		{
			statisticsBean.setRenderTablename(false);
			renderTables=true;
		}
	}

	public void chartValueChanged(ValueChangeEvent event) 
	{
		renderPieChart = false;
		renderBarChart = false;
		xySeriesChart = false;
		chartColumnSelected = null;
		renderTimeSeriesChart = false;
		reset();
		if (event.getNewValue() != null)
		{
			chartType = event.getNewValue().toString();
		}
		if(statisticsBean.onChartTypeChange())
		{
			renderTables=true;
			if(tableSelected!=null)
			{
				if(statisticsBean.onTableChange())
				{
					renderGenerategraphButton=true;
					switch(chartType)
					{
					case("1"):
					case("2"):
						renderChartColumn=true;
					renderXYGraphColumns=false;
					break;
					case("3"):
					case("4"):
						renderChartColumn=false;
					renderXYGraphColumns=true;
					break;
					default:
						message = "Please select a chart type.";
						renderMessage = true;
					}
				}
				else
				{
					message=statisticsBean.getErrorMessage();
					renderMessage=true;
				}
			}
			else
			{
				renderChartColumn=false;
			}
		}
		else
		{
			message=statisticsBean.getErrorMessage();
			renderMessage=true;
			renderGenerategraphButton=false;
		}
	}

	public void tableValueChanged(ValueChangeEvent table)
	{
		reset();
		if (table.getNewValue() != null)
		{
			tableSelected = table.getNewValue().toString();
		}
		renderPieChart=false;
		renderBarChart=false;
		xySeriesChart=false;
		renderTimeSeriesChart=false;
		statisticsBean.setTableSelected(tableSelected);
		if(statisticsBean.onTableChange())
		{
			renderGenerategraphButton=true;
			switch(chartType)
			{
			case("1"):
			case("2"):
				renderChartColumn=true;
			renderXYGraphColumns=false;
			break;
			case("3"):
			case("4"):	
				renderChartColumn=false;
			renderXYGraphColumns=true;
			break;
			default:
				message = "Please select a chart type.";
				renderMessage = true;
			}
		}
		else
		{
			message=statisticsBean.getErrorMessage();
			renderMessage=true;
		}
	}

	public String generateChart() 
	{
		reset();
		try
		{
			if(tableSelected==null)
			{
				message="Please select table";
				renderMessage=true;
				return "FAIL";
			}
			if(chartType==null)
			{
				message="Please select a chart";
				renderMessage=true;
				return "FAIL";
			}
			FacesContext context = FacesContext.getCurrentInstance();
			String path = context.getExternalContext().getRealPath("/ChartImages");
			File dir = new File(path);
			if(!dir.exists())
			{
				new File(path).mkdirs();
			}
			if(statisticsBean.getTableList().isEmpty())
			{
				message = "No tables found in the schema.";
				renderMessage = true;
				renderTables=false;
				renderXYGraphColumns=false;
				renderChartColumn = false;
				return "FAIL";
			}
			if(tableSelected == null)
			{
				message = "Please select a table.";
				renderMessage=true;
				return "FAIL";
			}
			switch(chartType)
			{
			case("1"):
				if(chartColumnSelected.isEmpty())
				{
					message="Select a column to generate Chart";
					renderMessage=true;
					return "FAIL";
				}
			List<String> columnList= new ArrayList<String>();
			columnList.add(chartColumnSelected);
			statisticsBean.getList().clear();
			statisticsBean.setList(columnList);
			if(statisticsBean.generateResultsforGraph())
			{
				if(visualAnalysis.generateChart(chartColumnSelected,tableSelected))
				{
					JFreeChart chart = ChartFactory.createPieChart
							(chartColumnSelected, visualAnalysis.getPieModel(), true, true, false);
					File outPie = new File(path+"/"+dbAccess.getUsername()+"_piechart.png");
					ChartUtilities.saveChartAsPNG(outPie, chart, 600, 450);
					pieChartPath = "/ChartImages/"+dbAccess.getUsername()+"_piechart.png";
					renderPieChart=true;
					renderBarChart=false;
					xySeriesChart=false;
					renderTimeSeriesChart=false;
					statisticsBean.getList().clear();
					return "SUCCESS";
				}
				else
				{
					message=visualAnalysis.getErrorMessage();
					renderMessage=true;
					statisticsBean.getList().clear();
					return "fail";
				}
			}
			else
			{
				message=statisticsBean.getErrorMessage();
				renderMessage=true;
				statisticsBean.getList().clear();
				return "FAIL";
			}
			case("2"):
				if(chartColumnSelected.isEmpty())
				{
					message = "Select a column.";
					renderMessage=true;
					return "FAIL";
				}
			columnList= new ArrayList<String>();
			columnList.add(chartColumnSelected);
			statisticsBean.getList().clear();
			statisticsBean.setList(columnList);
			if(statisticsBean.generateResultsforGraph())
			{
				if(visualAnalysis.generateChart(chartColumnSelected,tableSelected))
				{
					JFreeChart chart = ChartFactory.createBarChart
							(chartColumnSelected, "Category","Value",
									visualAnalysis.getDataset(),PlotOrientation.VERTICAL,
									true, true, false);
					File outBar = new File(path+"/"+dbAccess.getUsername()+"_barGraph.png");
					ChartUtilities.saveChartAsPNG(outBar, chart, 600, 450);
					barChartPath = "/ChartImages/"+dbAccess.getUsername()+"_barGraph.png";
					renderPieChart=false;
					renderBarChart=true;
					xySeriesChart=false;
					renderTimeSeriesChart=false;
					statisticsBean.getList().clear();
					return "SUCCESS";
				}
				else
				{
					message=visualAnalysis.getErrorMessage();
					renderMessage=true;
					statisticsBean.getList().clear();
					return "fail";
				}
			}
			else
			{
				message=statisticsBean.getErrorMessage();
				renderMessage=true;
				statisticsBean.getList().clear();
				return "fail";
			}
			case("3"):
				if(responseValue == null || predictorValue == null)
				{
					message="Select a response/predictor values to generate Chart";
					renderMessage=true;
					return "FAIL";
				}
			if(responseValue.equals("0") || predictorValue.equals("0"))
			{
				message="Select a response/predictor values to generate Chart";
				renderMessage=true;
				return "FAIL";
			}
			statisticsBean.setPredictorValue(predictorValue);
			statisticsBean.setResponseValue(responseValue);
			if(statisticsBean.generateRegressionResults())
			{
				JFreeChart chart = ChartFactory.createScatterPlot(
						"Scatter Plot", predictorValue, responseValue,
						statisticsBean.getXySeriesVariable(), PlotOrientation.VERTICAL,
						true, true, false);
				XYPlot plot = chart.getXYPlot();
				Shape cross = ShapeUtilities.createUpTriangle(3);
				plot.getRenderer().setSeriesShape(0, cross);
				plot.getRenderer().setSeriesPaint(0, Color.blue);


				double regressionParameters[] = Regression.getOLSRegression(statisticsBean.getXySeriesVariable(), 0);

				// Prepare a line function using the found parameters
				LineFunction2D linefunction2d = new LineFunction2D(
						regressionParameters[0], regressionParameters[1]);

				// Creates a dataset by taking sample values from the line function
				XYDataset dataset = DatasetUtilities.sampleFunction2D(linefunction2d,
						plot.getDomainAxis(0).getLowerBound() , plot.getDomainAxis(0).getUpperBound(), 100, "Fitted Regression Line");

				// Draw the line dataset
				XYPlot xyplot = chart.getXYPlot();
				xyplot.setDataset(1, dataset);
				XYLineAndShapeRenderer xylineandshaperenderer = new XYLineAndShapeRenderer(
						true, false);
				xylineandshaperenderer.setSeriesPaint(0, Color.red);
				xyplot.setRenderer(1, xylineandshaperenderer);



				File xy = new File(path+"/"+dbAccess.getUsername()+"_scatterplot.png");
				ChartUtilities.saveChartAsPNG(xy, chart, 600, 450);
				xyChartPath = "/ChartImages/"+dbAccess.getUsername()+"_scatterplot.png";
				renderPieChart=false;
				renderBarChart=false;
				xySeriesChart=true;
				renderTimeSeriesChart=false;
				return "SUCCESS";
			}
			else
			{
				message=statisticsBean.getErrorMessage();
				renderMessage=true;
				return "fail";
			}
			case("4") :
				if(responseValue==null || predictorValue==null)
				{
					message="Select a response/predictor values to generate Chart";
					renderMessage=true;
					return "FAIL";
				}
			if(responseValue.equals("0") || predictorValue.equals("0"))
			{
				message="Select a response/predictor values to generate Chart";
				renderMessage=true;
				return "FAIL";
			}
			statisticsBean.setPredictorValue(predictorValue);
			statisticsBean.setResponseValue(responseValue);
			if(statisticsBean.generateRegressionResults())
			{
				JFreeChart chart = ChartFactory.createXYLineChart
						("Time Series", "Number of Observations",
								"Predictor/Response Values",
								statisticsBean.getXyTimeSeriesCollection());
				File xyTSeries = new File(path+"/"+dbAccess.getUsername()+"_Time_Series.png");
				ChartUtilities.saveChartAsPNG(xyTSeries, chart, 600, 450);
				xyTimeSeriesPath = "/ChartImages/"+dbAccess.getUsername()+"_Time_Series.png";
				renderPieChart=false;
				renderBarChart=false;
				xySeriesChart=false;
				renderTimeSeriesChart=true;
				return "SUCCESS";
			}
			else
			{
				message=statisticsBean.getErrorMessage();
				renderMessage=true;
				return "fail";
			}
			}
		} catch(IOException io) {
			message=io.getMessage();
			renderMessage=true;
			return "fail";
		} catch(Exception e) {
			message=e.getMessage();
			renderMessage=false;
			return "fail";
		}
		return "SUCCESS";
	}

	public void reset()
	{
		renderMessage=false;
	}

	public String getTableSelected() {
		return tableSelected;
	}

	public void setTableSelected(String tableSelected) {
		this.tableSelected = tableSelected;
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

	public boolean isRenderChartType() {
		return renderChartType;
	}

	public void setRenderChartType(boolean renderChartType) {
		this.renderChartType = renderChartType;
	}

	public String getChartColumnSelected() {
		return chartColumnSelected;
	}

	public void setChartColumnSelected(String chartColumnSelected) {
		this.chartColumnSelected = chartColumnSelected;
	}

	public boolean isRenderChartColumn() {
		return renderChartColumn;
	}

	public void setRenderChartColumn(boolean renderChartColumn) {
		this.renderChartColumn = renderChartColumn;
	}

	public boolean isRenderXYGraphColumns() {
		return renderXYGraphColumns;
	}

	public void setRenderXYGraphColumns(boolean renderXYGraphColumns) {
		this.renderXYGraphColumns = renderXYGraphColumns;
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

	public StatisticsBean getStatisticsBean() {
		return statisticsBean;
	}

	public void setStatisticsBean(StatisticsBean statisticsBean) {
		this.statisticsBean = statisticsBean;
	}

	public String getChartType() {
		return chartType;
	}

	public void setChartType(String chartType) {
		this.chartType = chartType;
	}

	public boolean isRenderRegressionColumn() {
		return renderRegressionColumn;
	}

	public void setRenderRegressionColumn(boolean renderRegressionColumn) {
		this.renderRegressionColumn = renderRegressionColumn;
	}

	public boolean isRenderTables() {
		return renderTables;
	}

	public void setRenderTables(boolean renderTables) {
		this.renderTables = renderTables;
	}

	public boolean isRenderGenerategraphButton() {
		return renderGenerategraphButton;
	}

	public void setRenderGenerategraphButton(boolean renderGenerategraphButton) {
		this.renderGenerategraphButton = renderGenerategraphButton;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public VisualAnalysis getVisualAnalysis() {
		return visualAnalysis;
	}

	public void setVisualAnalysis(VisualAnalysis visualData) {
		this.visualAnalysis = visualData;
	}

	public String getPieChartPath() {
		return pieChartPath;
	}

	public void setPieChartPath(String pieChartPath) {
		this.pieChartPath = pieChartPath;
	}

	public DbAccess getDbAccess() {
		return dbAccess;
	}

	public void setDbAccess(DbAccess dbAccess) {
		this.dbAccess = dbAccess;
	}

	public boolean isRenderPieChart() {
		return renderPieChart;
	}

	public void setRenderPieChart(boolean renderPieChart) {
		this.renderPieChart = renderPieChart;
	}

	public boolean isRenderBarChart() {
		return renderBarChart;
	}

	public void setRenderBarChart(boolean renderBarChart) {
		this.renderBarChart = renderBarChart;
	}

	public boolean isXySeriesChart() {
		return xySeriesChart;
	}

	public void setXySeriesChart(boolean xySeriesChart) {
		this.xySeriesChart = xySeriesChart;
	}

	public boolean isRenderTimeSeriesChart() {
		return renderTimeSeriesChart;
	}

	public void setRenderTimeSeriesChart(boolean renderTimeSeriesChart) {
		this.renderTimeSeriesChart = renderTimeSeriesChart;
	}

	public String getXyChartPath() {
		return xyChartPath;
	}

	public void setXyChartPath(String xyChartPath) {
		this.xyChartPath = xyChartPath;
	}		

	public String getXyTimeSeriesPath() {
		return xyTimeSeriesPath;
	}

	public void setXyTimeSeriesPath(String xyTimeSeriesPath) {
		this.xyTimeSeriesPath = xyTimeSeriesPath;
	}

	public boolean isXyTimeSeriesChart() {
		return xyTimeSeriesChart;
	}

	public void setXyTimeSeriesChart(boolean xyTimeSeriesChart) {
		this.xyTimeSeriesChart = xyTimeSeriesChart;
	}

	public String getBarChartPath() {
		return barChartPath;
	}

	public void setBarChartPath(String barChartPath) {
		this.barChartPath = barChartPath;
	}

}




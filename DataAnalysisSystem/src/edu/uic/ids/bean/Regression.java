package edu.uic.ids.bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name="regression")
@SessionScoped
public class Regression {

	private String message;
	private String regressionEquation;
	private double intercept;
	private double interceptStandardError;
	private double tStatistic;
	private double interceptPValue;
	private double slope;
	private double predictorDF;
	private double residualErrorDF;
	private double totalDF;
	private double regressionSumSquares;
	private double sumSquaredErrors;
	private double totalSumSquares;
	private double meanSquare;
	private double meanSquareError;
	private double fValue;
	private double pValue;
	private double slopeStandardError;
	private double tStatisticPredictor;
	private double pValuePredictor;
	private double standardErrorModel;
	private double rSquare;
	private double rSquareAdjusted;
	
	public Regression()
	{}
	
	public boolean setRegressionAnalysisVariables(
			String regressionEquation, double intercept,
			double interceptStandardError, double tStatistic,
			double interceptPValue, double slope,
			double slopeStandardError, double tStatisticPredictor,
			double pValuePredictor, double standardErrorModel,
			double rSquare, double rSquareAdjusted,
			double predictorDF,double residualErrorDF,
			double totalDF, double regressionSumSquares,
			double sumSquaredErrors, double totalSumSquares,
			double meanSquare, double meanSquareError,
			double fValue, double pValue)
	{
		try {
			this.intercept = intercept;
			this.interceptStandardError = interceptStandardError;
			this.tStatistic = tStatistic;
			this.interceptPValue = interceptPValue;
			this.slope = slope;
			this.slopeStandardError = slopeStandardError;
			this.tStatisticPredictor = tStatisticPredictor;
			this.pValuePredictor = pValuePredictor;
			this.standardErrorModel = standardErrorModel;
			this.rSquare = rSquare;
			this.rSquareAdjusted = rSquareAdjusted;
			this.predictorDF = predictorDF;
			this.residualErrorDF = residualErrorDF;
			this.totalDF = totalDF;
			this.regressionSumSquares = regressionSumSquares;
			this.sumSquaredErrors = sumSquaredErrors;
			this.totalSumSquares = totalSumSquares;
			this.meanSquare = meanSquare;
			this.meanSquareError = meanSquareError;
			this.fValue = fValue;
			this.pValue = pValue;
			this.regressionEquation = regressionEquation;
			return true;
		} catch(Exception e) {
			message = e.getMessage();
			return false;
		}
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getRegressionEquation() {
		return regressionEquation;
	}

	public void setRegressionEquation(String regressionEquation) {
		this.regressionEquation = regressionEquation;
	}

	public double getIntercept() {
		return intercept;
	}

	public void setIntercept(double intercept) {
		this.intercept = intercept;
	}

	public double getInterceptStandardError() {
		return interceptStandardError;
	}

	public void setInterceptStandardError(double interceptStandardError) {
		this.interceptStandardError = interceptStandardError;
	}

	public double gettStatistic() {
		return tStatistic;
	}

	public void settStatistic(double tStatistic) {
		this.tStatistic = tStatistic;
	}

	public double getInterceptPValue() {
		return interceptPValue;
	}

	public void setInterceptPValue(double interceptPValue) {
		this.interceptPValue = interceptPValue;
	}

	public double getSlope() {
		return slope;
	}

	public void setSlope(double slope) {
		this.slope = slope;
	}

	public double getPredictorDF() {
		return predictorDF;
	}

	public void setPredictorDF(double predictorDF) {
		this.predictorDF = predictorDF;
	}

	public double getResidualErrorDF() {
		return residualErrorDF;
	}

	public void setResidualErrorDF(double residualErrorDF) {
		this.residualErrorDF = residualErrorDF;
	}

	public double getTotalDF() {
		return totalDF;
	}

	public void setTotalDF(double totalDF) {
		this.totalDF = totalDF;
	}

	public double getRegressionSumSquares() {
		return regressionSumSquares;
	}

	public void setRegressionSumSquares(double regressionSumSquares) {
		this.regressionSumSquares = regressionSumSquares;
	}

	public double getSumSquaredErrors() {
		return sumSquaredErrors;
	}

	public void setSumSquaredErrors(double sumSquaredErrors) {
		this.sumSquaredErrors = sumSquaredErrors;
	}

	public double getTotalSumSquares() {
		return totalSumSquares;
	}

	public void setTotalSumSquares(double totalSumSquares) {
		this.totalSumSquares = totalSumSquares;
	}

	public double getMeanSquare() {
		return meanSquare;
	}

	public void setMeanSquare(double meanSquare) {
		this.meanSquare = meanSquare;
	}

	public double getMeanSquareError() {
		return meanSquareError;
	}

	public void setMeanSquareError(double meanSquareError) {
		this.meanSquareError = meanSquareError;
	}

	public double getfValue() {
		return fValue;
	}

	public void setfValue(double fValue) {
		this.fValue = fValue;
	}

	public double getpValue() {
		return pValue;
	}

	public void setpValue(double pValue) {
		this.pValue = pValue;
	}

	public double getSlopeStandardError() {
		return slopeStandardError;
	}

	public void setSlopeStandardError(double slopeStandardError) {
		this.slopeStandardError = slopeStandardError;
	}

	public double gettStatisticPredictor() {
		return tStatisticPredictor;
	}

	public void settStatisticPredictor(double tStatisticPredictor) {
		this.tStatisticPredictor = tStatisticPredictor;
	}

	public double getpValuePredictor() {
		return pValuePredictor;
	}

	public void setpValuePredictor(double pValuePredictor) {
		this.pValuePredictor = pValuePredictor;
	}

	public double getStandardErrorModel() {
		return standardErrorModel;
	}

	public void setStandardErrorModel(double standardErrorModel) {
		this.standardErrorModel = standardErrorModel;
	}

	public double getrSquare() {
		return rSquare;
	}

	public void setrSquare(double rSquare) {
		this.rSquare = rSquare;
	}

	public double getrSquareAdjusted() {
		return rSquareAdjusted;
	}

	public void setrSquareAdjusted(double rSquareAdjusted) {
		this.rSquareAdjusted = rSquareAdjusted;
	}

}
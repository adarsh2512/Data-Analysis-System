package edu.uic.ids.bean;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FilenameUtils;
import org.apache.myfaces.custom.fileupload.UploadedFile;

import com.mysql.jdbc.Statement;

@ManagedBean(name="dataImport")
@SessionScoped
public class DataImport {
	private boolean renderRowMessage;
	private boolean renderTabledata;
	public boolean isRenderTabledata() {
		return renderTabledata;
	}

	public void setRenderTabledata(boolean renderTabledata) {
		this.renderTabledata = renderTabledata;
	}
	private String errorRowMessage=""; 
	List<String> errorRowList=null;
	public List<String> getErrorRowList() {
		return errorRowList;
	}

	public void setErrorRowList(List<String> errorRowList) {
		this.errorRowList = errorRowList;
	}
	private List<String> errorList=null;
	public List<String> getErrorList() {
		return errorList;
	}

	public void setErrorList(List<String> errorList) {
		this.errorList = errorList;
	}
	private String message1;
	public String getMessage1() {
		return message1;
	}

	public void setMessage1(String message1) {
		this.message1 = message1;
	}
	private int countm;
	private int countd;
	private int whichRow;
	private String fileLabel;
	private String fileName;
	private String fileType;
	private String sqlQuery ="";
	private Statement statement;
	private Connection connection;
	private ResultSet resultSet;
	private long fileSize;
	private String fileContentType;
	private int numberRows;
	private int numberColumns;
	private String uploadedFileContents;
	private boolean fileImport;
	private boolean fileImportError;
	private String filePath;
	private String tempFileName;
	private String[] data1;
	private FacesContext facesContext;
	private List<String> headerList = null;
	private List<String> metaHeaderList = null;
	private List<String> dataList = null;
	private List<String> metaDataList = null;
	private String datasetLabel;
	private List<String> variableList = null;
	private List<String> dataTypeList = null;
	private List<String> inputTypeList = null;
	private List<String> computeVariable = null;
	private List<String> nocomputeVariable = null;
	private List<String> computeDatatype = null;
	private List<String> nocomputeDatatype = null;
	private List<String> SQLList = null;
	private List<String> comList = null;
	private List<String> nocomList = null;
	private List<String> a = null;
	private List<String> b = null;
	private List<String> cd = null;
	private int computeStatus = 0;
	public List<String> getSQLList() {
		return SQLList;
	}

	public void setSQLList(List<String> sQLList) {
		SQLList = sQLList;
	}
	private String baseName;
	@ManagedProperty(value="#{dbAccess}")
	private DbAccess dbAccess;
	private String headerRow;
	private String format;
	private List<String> ab=null;
	private String ip;
	private UploadedFile uploadedFile;
	private String message;
	private String errorMessage="";
	private List<String> head = null;
	private List<String> headr = null;
	private List<String> dataList1 = null;
	private String success = "";
	public String getSuccess() {
		return success;
	}

	public void setSuccess(String success) {
		this.success = success;
	}

	public boolean isSuccessRendered() {
		return successRendered;
	}

	public void setSuccessRendered(boolean successRendered) {
		this.successRendered = successRendered;
	}
	private boolean successRendered = false;
	public List<String> getHeadr() {
		return headr;
	}

	public void setHeadr(List<String> headr) {
		this.headr = headr;
	}

	public List<String> getHead() {
		return head;
	}

	public void setHead(List<String> head) {
		this.head = head;
	}

	public String reset(){
		try{
			fileLabel = "";
			datasetLabel = "";
			fileImport = false;
			successRendered = false;
			renderMessage = false;
			renderRowMessage = false;
			renderTabledata = false;
			return "SUCCESS";
		}
		catch(Exception e){
			message="error";
			return "FAIL";
		}
	}

	public DataImport(){
/*		FacesContext context = FacesContext.getCurrentInstance();
		if(context!=null){
			HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
			dbAccess = (DbAccess)session.getAttribute("dbAccess");
		}*/
	}

	public String uploadTypeFile(){


		String status = processFileUpload();
		if(!status.equalsIgnoreCase("SUCCESS"))
			return "FAIL";
		if(fileLabel.equals("")){

			message ="Can't create a table without file name";
			renderMessage = true;
			fileImport = false;
		}
		else if(datasetLabel.equals("")){
			message ="Can't create a table without specifying Data Label";
			renderMessage = true;
			fileImport = false;
		}
		else if(fileType.equals("")){
			message ="Can't create a table without specifying File type label";
			renderMessage = true;
			fileImport = false;
		}
		else{
			try{
				switch (fileType.toLowerCase()) {
				case "data": // data file import
					status = processDataFile();
					break;
				case "metadata": // metadata file import
					status = processMetaDataFile();
					break;
				default: // general file import
					status = processFile();
					break;
				}}
			catch(NullPointerException e){
				System.err.println(e.getMessage());
			}

		}
		return status;
	}

	public String processMetaDataFile(){
		renderTabledata = false;
		renderMessage = false;
		message = "";
		countm=0;
		computeStatus = 0;
		countd=0;
		whichRow = 0;
		errorRowMessage = "";
		errorMessage = "";
		renderRowMessage = false;
		successRendered = false;
		metaHeaderList = null;
		metaDataList = new ArrayList<String>();
		variableList = new ArrayList<String>();
		dataTypeList = new ArrayList<String>();
		inputTypeList = new ArrayList<String>();
		computeVariable = new ArrayList<String>();
		nocomputeVariable = new ArrayList<String>();
		computeDatatype = new ArrayList<String>();
		nocomputeDatatype = new ArrayList<String>();
		SQLList = new ArrayList<String>();
		comList = new ArrayList<String>();
		nocomList = new ArrayList<String>();
		head = new ArrayList<String>();
		headr = new ArrayList<String>();

		int nc = 0;
		int row = 0;
		FileReader in;
		int n=0;
		String status = "FAIL";
		try {

			in = new FileReader(tempFileName);
			Iterable<CSVRecord> records = CSVFormat.TDF.parse(in);

			if(headerRow.equals("yes")){
				for (CSVRecord record : records) {
					nc = record.size();
					if(row++ == 0) {
						metaHeaderList = new ArrayList<String>(nc); 
						for (String field : record) {
							metaHeaderList.add(field);
						}
						continue;
					}
					// data row needs new array for each data line // 
					if(row > 0)
					{
						int col = 0;
						for (String field : record) {
							metaDataList.add(field);
						}

					}
				}
			}
			else{
				for (CSVRecord record : records) {
					nc = record.size();

					if(row >= 0)
					{
						int col = 0;
						for (String field : record) {
							metaDataList.add(field);
						}

					}
				}
			}
			if(format.equals("csv")){
				for(int r = 0; r < metaDataList.size(); r++) 
				{ 

					String[] metaDatali = metaDataList.get(r).split(",");
					variableList.add(metaDatali[0]); 
					dataTypeList.add(metaDatali[1]);
					inputTypeList.add(metaDatali[2]);

				}
				for(int i=0; i<dataTypeList.size(); i++)
				{
					if(dataTypeList.get(i).equalsIgnoreCase("byte")){
						dataTypeList.set(i, "tinyint");
					}
					if(dataTypeList.get(i).equalsIgnoreCase("short")){
						dataTypeList.set(i, "smallint");
					}
					if(dataTypeList.get(i).equalsIgnoreCase("long")){
						dataTypeList.set(i, "bigint");
					}
					if(dataTypeList.get(i).equalsIgnoreCase("float")){
						dataTypeList.set(i, "real");
					}
					if(dataTypeList.get(i).equalsIgnoreCase("boolean")){
						dataTypeList.set(i, "varchar (50)");
					}
					if(dataTypeList.get(i).equalsIgnoreCase("char")){
						dataTypeList.set(i, "varchar (50)");
					}
					if(dataTypeList.get(i).equalsIgnoreCase("string")){
						dataTypeList.set(i, "varchar (50)");
					}	
				}
				for(int g =0; g<inputTypeList.size(); g++){
					if(inputTypeList.get(g).equalsIgnoreCase("computed")){
						computeStatus++;
						computeVariable.add(variableList.get(g));
						computeDatatype.add(dataTypeList.get(g));}
					else{
						nocomputeVariable.add(variableList.get(g));
						nocomputeDatatype.add(dataTypeList.get(g));
					}
				}
			}
			else{
				for(int r = 0; r < metaDataList.size()/3; r++){
					variableList.add(metaDataList.get(3*r));

					dataTypeList.add(metaDataList.get(3*r+1));
					inputTypeList.add(metaDataList.get(3*r+2));

				}
				for(int i=0; i<dataTypeList.size(); i++)
				{
					if(dataTypeList.get(i).equalsIgnoreCase("byte")){
						dataTypeList.set(i, "tinyint");
					}
					if(dataTypeList.get(i).equalsIgnoreCase("short")){
						dataTypeList.set(i, "smallint");
					}
					if(dataTypeList.get(i).equalsIgnoreCase("long")){
						dataTypeList.set(i, "bigint");
					}
					if(dataTypeList.get(i).equalsIgnoreCase("float")){
						dataTypeList.set(i, "real");
					}
					if(dataTypeList.get(i).equalsIgnoreCase("boolean")){
						dataTypeList.set(i, "varchar (50)");
					}
					if(dataTypeList.get(i).equalsIgnoreCase("char")){
						dataTypeList.set(i, "varchar (50)");
					}
					if(dataTypeList.get(i).equalsIgnoreCase("string")){
						dataTypeList.set(i, "varchar (50)");
					}	
				}
				for(int g =0; g<inputTypeList.size(); g++){
					if(inputTypeList.get(g).equalsIgnoreCase("computed")){
						computeStatus++;
						computeVariable.add(variableList.get(g));
						computeDatatype.add(dataTypeList.get(g));}
					else{
						nocomputeVariable.add(variableList.get(g));
						nocomputeDatatype.add(dataTypeList.get(g));
					}
				}
			}
			for(int d=0; d<computeVariable.size(); d++){
				comList.add(computeVariable.get(d)+" "+computeDatatype.get(d)+" DEFAULT 0");
			}
			for(int d=0; d<nocomputeVariable.size(); d++){
				nocomList.add(nocomputeVariable.get(d)+" "+nocomputeDatatype.get(d));
			}
			for(int i = 0; i< variableList.size(); i++){
				SQLList.add(variableList.get(i)+" "+dataTypeList.get(i));
			}
			//for(int k = 0; k<variableList.size(); k=k+variableList.size()-1){
			String k = String.join(",", SQLList);
			head.add("Rownumber");
			head.add(k);
			String h = String.join(",", head);
			headr.add(h);
			//}
			if(computeVariable.size()>0){

				sqlQuery = "create table "+dbAccess.getSchema()+ ".s17g304_"+datasetLabel+" ("+nocomList.toString().replace("[", "").replace("]", "") +", "+comList.toString().replace("[", "").replace("]", "")  +" );";
				success = "Compute Table Created with "+Integer.toString(numberRows)+" columns";
				successRendered = true;
				//System.out.println(sqlQuery);
			}

			else{
				sqlQuery = "create table "+dbAccess.getSchema()+ ".s17g304_"+datasetLabel+" ("+SQLList.toString().replace("[", "").replace("]", "") + " );";

				success = "Table Created with "+Integer.toString(numberRows)+" columns";
				successRendered = true;
			}
			if (dbAccess.getConnection() != null && dbAccess.getStatement() != null) {
				dbAccess.getStatement().executeUpdate(sqlQuery);
			}
			status = "PROCESSED";
		}

		catch (SQLException e) {
			if(e.getErrorCode()==1050){
				successRendered = false;
				message ="Table already exists with this name";
				fileImport = false;
				renderMessage=true;
			}
			else{
				successRendered = false;
				message = "Invalid Syntax (May be Wrong datatype)";
				fileImport = false;
				renderMessage = true;}
			System.err.println(e.getSQLState());
			System.err.println(e.getMessage());
			System.err.println(e.getErrorCode());

		}
		catch (Exception e) {
			System.err.println(e.getMessage());
		}

		return status;

	}
	public String good(){
		success = "Only "+Integer.toString(numberRows-countm-countd)+" valid rows out of "+Integer.toString(numberRows)+" inserted";
		successRendered = true;
		renderMessage = false;
		renderTabledata = false;
		return "good";
	}
	public String udado(){
		try{if (dbAccess.getConnection() != null && dbAccess.getStatement() != null) {
			sqlQuery = "truncate table "+dbAccess.getSchema()+ ".s17g304_"+datasetLabel+";";
			dbAccess.getStatement().executeUpdate(sqlQuery);
			success = "Values not inserted in table";
			successRendered = true;
			renderMessage = false;
			renderTabledata = false;
		}
		return "Success";
		}
		catch (SQLException e) {

			System.err.println(e.getSQLState());
			System.err.println(e.getMessage());
			System.err.println(e.getErrorCode());
			return "fail";
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			return "fail";
		}
	}
	public String processFile(){
		return "PROCESSED";
	}

	public String processDataFile() {
		renderTabledata = false;
		renderMessage = false;
		message = "";
		countm=0;
		countd=0;
		whichRow = 0;
		errorRowMessage = "";
		errorMessage = "";
		errorRowList=null;	
		errorList = null;
		headerList = null;
		dataList = new ArrayList<String>();
		a = new ArrayList<String>();
		b = new ArrayList<String>();
		int nc = 0;
		int row = 0;
		FileReader in;
		int n=0;
		String status = "FAIL";
		try {
			in = new FileReader(tempFileName);
			Iterable<CSVRecord> records = CSVFormat.TDF.parse(in);
			if(headerRow.equals("yes")){
				for (CSVRecord record : records) {
					//System.out.println("inside records");
					nc = record.size();
					if(row++ == 0) {
						headerList = new ArrayList<String>(nc); 
						headerList.add("RowNumber");
						for (String field : record) {
							headerList.add(field);
						}
						continue;
					}
					// data row needs new array for each data line // 
					if(row > 0)
					{

						int col = 0;
						for (String field : record) {
							dataList.add(field);
						}

					}
				}
			}
			else{
				for (CSVRecord record : records) {
					nc = record.size();

					// data row needs new array for each data line // 
					if(row >= 0)
					{
						int col = 0;
						for (String field : record) {
							dataList.add(field);
						}

					}
				}
			}

			//non Compute
			if(computeStatus == 0){
				if(format.equals("csv")){
					errorRowList=new ArrayList<String>();	
					errorList = new ArrayList<String>();
					String invalid = "";
					for(int i =0; i<dataList.size(); i++){
						sqlQuery = "insert into "+dbAccess.getSchema()+ ".s17g304_"+datasetLabel+ " values"+" ("+dataList.get(i).toString().replace("[", "").replace("]", "") + " );";

						if (dbAccess.getConnection() != null && dbAccess.getStatement() != null) {
							try{

								dbAccess.getStatement().executeUpdate(sqlQuery);

							}

							catch(SQLException e){
								successRendered = false;
								whichRow=i+1;
								String err=String.join(",", Integer.toString(whichRow),dataList.get(whichRow-1).toString().replace("[", "").replace("]", ""));
								errorRowList.add(err);
								errorList.add(Integer.toString(whichRow));
								if(e.getErrorCode()==1054){
									countm=countm+1;
									System.err.println(e.getSQLState());
									System.err.println(e.getMessage());
									System.err.println(e.getErrorCode());		
								}
								else if(e.getErrorCode()==1064){
									countd=countd+1;

									System.err.println(e.getSQLState());
									System.err.println(e.getMessage());
									System.err.println(e.getErrorCode());
								}
								else{
									invalid = " There is some syntax error or connection error";
									System.err.println(e.getSQLState());
									System.err.println(e.getMessage());
									System.err.println(e.getErrorCode());
								}
								message="Bad data: Either due to null values or wrong datatype; "+"Number of rows in which missing values: "+countm +" , Number of rows in which wrong data type: "+countd;
								success = invalid;
								if(countm>0 || countd>0){
									renderMessage=true;
									errorRowMessage = String.join(",",errorRowList);
									renderTabledata=true;
									//renderRowMessage=true;
									errorMessage="Error caused due to row number: "+String.join(",", errorList);
									renderMessage=true;}
								else{renderMessage =false;
								successRendered = true;
								}
							}
						}

					}
					if(whichRow==0){
						success = Integer.toString(numberRows)+" rows inserted";
						successRendered = true;}
				}
				//Tab delimmited
				else{
					errorRowList=new ArrayList<String>();	
					errorList = new ArrayList<String>();
					dataList1 = new ArrayList<String>();
					String invalid ="";
					ab = new ArrayList<String>();
					int k = 0;
					for(int i =0; i<dataList.size()/variableList.size(); i++){
						int p = 0;
						int l = 0;
						l = l+k;
						for(int j=k; j<variableList.size()+l; j++){

							ab.add(p, dataList.get(j));
							ab.set(p, dataList.get(j));
							p++;
							k=j;
						}

						sqlQuery = "insert into "+dbAccess.getSchema()+ ".s17g304_"+datasetLabel+ " values"+" ("+ab.toString().replace("[", "").replace("]","")+ " );";


						String tabdel = String.join(",",ab);
						dataList1.add(tabdel);

						ab.clear();
						k=k+1;
						try{

							if (dbAccess.getConnection() != null && dbAccess.getStatement() != null) {
								dbAccess.getStatement().executeUpdate(sqlQuery);
							}


						}
						catch(SQLException e){
							successRendered = false;
							whichRow=i+1;
							String err=String.join(",", Integer.toString(whichRow),dataList1.get(whichRow-1).toString().replace("[", "").replace("]", ""));
							errorRowList.add(err);
							errorList.add(Integer.toString(whichRow));
							if(e.getErrorCode()==1054){
								countm=countm+1;
								System.err.println(e.getSQLState());
								System.err.println(e.getMessage());
								System.err.println(e.getErrorCode());		
							}
							else if(e.getErrorCode()==1064){
								countd=countd+1;

								System.err.println(e.getSQLState());
								System.err.println(e.getMessage());
								System.err.println(e.getErrorCode());
							}
							else{
								invalid = " There is some syntax error or connection error";
								System.err.println(e.getSQLState());
								System.err.println(e.getMessage());
								System.err.println(e.getErrorCode());
							}
							message="Bad data: Either due to null values or wrong datatype; "+"Number of rows in which missing values: "+countm +" , Number of rows in which wrong data type: "+countd;
							success = invalid;
							if(countm>0 || countd>0){
								renderMessage=true;
								errorRowMessage = String.join(",",errorRowList);
								renderTabledata=true;
								//renderRowMessage=true;
								errorMessage="Error caused due to row number: "+String.join(",", errorList);
								renderMessage=true;}
							else{renderMessage =false;
							successRendered = true;
							}

						}
					}
					if(whichRow==0){
						success = Integer.toString(numberRows)+" rows inserted";
						successRendered = true;}
				}}

			//Computed
			else{
				//System.out.println("inside compute");

				if(format.equals("csv")){
					errorRowList=new ArrayList<String>();	
					errorList = new ArrayList<String>();
					String invalid = "";
					for(int j=0; j<dataList.size(); j++){
						String[] comp = dataList.get(j).split(",");
						for(int i =0; i<nocomList.size(); i++){
							a.add(comp[i]);
						}
						String gj = String.join(",", a);
						b.add(gj);
						a.clear();
					}
					for(int i =0; i<b.size(); i++){
						sqlQuery = "insert into "+dbAccess.getSchema()+ ".s17g304_"+datasetLabel+" ("+nocomputeVariable.toString().replace("[", "").replace("]", "")+" )" +" values"+" ("+b.get(i).toString().replace("[", "").replace("]", "") + " );";

						if (dbAccess.getConnection() != null && dbAccess.getStatement() != null) {
							try{

								dbAccess.getStatement().executeUpdate(sqlQuery);

							}

							catch(SQLException e){
								successRendered = false;
								whichRow=i+1;
								String err=String.join(",", Integer.toString(whichRow),b.get(whichRow-1).toString().replace("[", "").replace("]", ""));
								errorRowList.add(err);
								errorList.add(Integer.toString(whichRow));
								if(e.getErrorCode()==1054){
									countm=countm+1;
									System.err.println(e.getSQLState());
									System.err.println(e.getMessage());
									System.err.println(e.getErrorCode());		
								}
								else if(e.getErrorCode()==1064){
									countd=countd+1;

									System.err.println(e.getSQLState());
									System.err.println(e.getMessage());
									System.err.println(e.getErrorCode());
								}
								else{
									invalid = " There is some syntax error or connection error";
									System.err.println(e.getSQLState());
									System.err.println(e.getMessage());
									System.err.println(e.getErrorCode());
								}
								message="Bad data: Either due to null values or wrong datatype; "+"Number of rows in which missing values: "+countm +" , Number of rows in which wrong data type: "+countd;
								success = invalid;
								if(countm>0 || countd>0){
									renderMessage=true;
									errorRowMessage = String.join(",",errorRowList);
									renderTabledata=true;
									//renderRowMessage=true;
									errorMessage="Error caused due to row number: "+String.join(",", errorList);
									renderMessage=true;}
								else{renderMessage =false;
								successRendered = true;
								}
							}
						}

					}
					if(whichRow==0){
						success = Integer.toString(numberRows)+" rows inserted";
						successRendered = true;}
				}
				//Tab delimmited
				else{
					//System.out.println("inside tab delimmited");
					errorRowList=new ArrayList<String>();	
					errorList = new ArrayList<String>();
					dataList1 = new ArrayList<String>();
					String invalid ="";
					ab = new ArrayList<String>();
					cd = new ArrayList<String>();
					int k = 0;
					for(int i =0; i<dataList.size()/variableList.size(); i++){
						//System.out.println("inside array 1");
						int p = 0;
						int l = 0;
						l = l+k;
						for(int j=k; j<variableList.size()+l; j++){
							//System.out.println("inside array 2");
							ab.add(p, dataList.get(j));
							ab.set(p, dataList.get(j));

							p++;
							k=j;
						}
						for(int w=0; w<nocomList.size(); w++){
							cd.add(w, ab.get(w));
						}
						//System.out.println(cd.toString());
						sqlQuery = "insert into "+dbAccess.getSchema()+ ".s17g304_"+datasetLabel+ " ("+nocomputeVariable.toString().replace("[", "").replace("]", "")+" )" +" values"+" ("+cd.toString().replace("[", "").replace("]", "")+ " );";


						String tabdel = String.join(",",ab);
						dataList1.add(tabdel);
						cd.clear();
						ab.clear();
						k=k+1;
						try{

							if (dbAccess.getConnection() != null && dbAccess.getStatement() != null) {
								dbAccess.getStatement().executeUpdate(sqlQuery);
							}


						}
						catch(SQLException e){
							successRendered = false;
							whichRow=i+1;
							String err=String.join(",", Integer.toString(whichRow),dataList1.get(whichRow-1).toString().replace("[", "").replace("]", ""));
							errorRowList.add(err);
							errorList.add(Integer.toString(whichRow));
							if(e.getErrorCode()==1054){
								countm=countm+1;
								System.err.println(e.getSQLState());
								System.err.println(e.getMessage());
								System.err.println(e.getErrorCode());		
							}
							else if(e.getErrorCode()==1064){
								countd=countd+1;

								System.err.println(e.getSQLState());
								System.err.println(e.getMessage());
								System.err.println(e.getErrorCode());
							}
							else{
								invalid = " There is some syntax error or connection error";
								System.err.println(e.getSQLState());
								System.err.println(e.getMessage());
								System.err.println(e.getErrorCode());
							}
							message="Bad data: Either due to null values or wrong datatype; "+"Number of rows in which missing values: "+countm +" , Number of rows in which wrong data type: "+countd;
							success = invalid;
							if(countm>0 || countd>0){
								renderMessage=true;
								errorRowMessage = String.join(",",errorRowList);
								renderTabledata=true;
								//renderRowMessage=true;
								errorMessage="Error caused due to row number: "+String.join(",", errorList);
								renderMessage=true;}
							else{renderMessage =false;
							successRendered = true;
							}

						}
					}
					if(whichRow==0){
						success = Integer.toString(numberRows)+" rows inserted";
						successRendered = true;}
				}	
			}
			status = "PROCESSED";
		}

		catch (Exception e) {
			System.err.println(e.getMessage());
		}	


		return status;

	}

	public String processFileUpload() {

		uploadedFileContents = null;
		facesContext = FacesContext.getCurrentInstance();

		renderMessage=false;
		File tempFile = null;
		FileOutputStream fos = null; 
		int n=0;
		fileImport = false; 
		try {
			filePath = facesContext.getExternalContext().getRealPath("/temp");
			fileName = uploadedFile.getName();

			baseName = FilenameUtils.getBaseName(fileName);
			if(baseName.equals("")){
				baseName="Please select a file";
			}
			else{
				baseName = FilenameUtils.getBaseName(fileName);
			}
			fileSize = uploadedFile.getSize();
			fileContentType = uploadedFile.getContentType();
			// next line if want upload in String for memory processing
			uploadedFileContents = new String(uploadedFile.getBytes());
			tempFileName = filePath + "/" + baseName; 
			tempFile = new File(tempFileName);
			fos = new FileOutputStream(tempFile);
			// next line if want file uploaded to disk 
			fos.write(uploadedFile.getBytes()); 
			fos.close();
			Scanner s = new Scanner(tempFile);
			String input="";
			while(s.hasNext()) {
				input = s.nextLine();
				n++; 
			}
			if(getHeaderRow().equals("yes")){
				numberRows = n-1;
			}
			else
			{
				numberRows = n;
			}
			fileImport = true;
			s.close(); 
		} 

		catch(NullPointerException e){
			fileImportError = true;
			renderMessage=true;
			message="Please select a File";
			e.getMessage();
			return "FAIL";
		}
		catch (IOException e) {
			fileImportError = true;
			e.getMessage(); 
			return "FAIL";
		} 
		return "SUCCESS";
	}

	
	public DbAccess getDbAccess() {
		return dbAccess;
	}

	public void setDbAccess(DbAccess dbAccess) {
		this.dbAccess = dbAccess;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}



	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Boolean getRenderMessage() {
		return renderMessage;
	}

	public void setRenderMessage(Boolean renderMessage) {
		this.renderMessage = renderMessage;
	}
	private Boolean renderMessage;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getHeaderRow() {
		return headerRow;
	}

	public void setHeaderRow(String headerRow) {
		this.headerRow = headerRow;
	}


	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public List<String> getHeaderList() {
		return headerList;
	}

	public void setHeaderList(List<String> headerList) {
		this.headerList = headerList;
	}

	public List<String> getDataList() {
		return dataList;
	}

	public void setDataList(List<String> dataList) {
		this.dataList = dataList;
	}
	public String getDatasetLabel() {
		return datasetLabel;
	}

	public void setDatasetLabel(String datasetLabel) {
		this.datasetLabel = datasetLabel;
	}


	public UploadedFile getUploadedFile() {
		return uploadedFile;
	}
	public void setUploadedFile(UploadedFile uploadedFile) {
		this.uploadedFile = uploadedFile;
	}
	public String getFileLabel() {
		return fileLabel;
	}
	public void setFileLabel(String fileLabel) {
		this.fileLabel = fileLabel;
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
	public int getNumberRows() {
		return numberRows;
	}
	public void setNumberRows(int numberRows) {
		this.numberRows = numberRows;
	}
	public int getNumberColumns() {
		return numberColumns;
	}
	public void setNumberColumns(int numberColumns) {
		this.numberColumns = numberColumns;
	}
	public String getUploadedFileContents() {
		return uploadedFileContents;
	}
	public void setUploadedFileContents(String uploadedFileContents) {
		this.uploadedFileContents = uploadedFileContents;
	}
	public boolean isFileImport() {
		return fileImport;
	}
	public void setFileImport(boolean fileImport) {
		this.fileImport = fileImport;
	}
	public boolean isFileImportError() {
		return fileImportError;
	}
	public void setFileImportError(boolean fileImportError) {
		this.fileImportError = fileImportError;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getTempFileName() {
		return tempFileName;
	}
	public void setTempFileName(String tempFileName) {
		this.tempFileName = tempFileName;
	}
	public FacesContext getFacesContext() {
		return facesContext;
	}
	public void setFacesContext(FacesContext facesContext) {
		this.facesContext = facesContext;
	}
	public boolean isRenderRowMessage() {
		return renderRowMessage;
	}

	public void setRenderRowMessage(boolean renderRowMessage) {
		this.renderRowMessage = renderRowMessage;
	}

	public String getErrorRowMessage() {
		return errorRowMessage;
	}

	public void setErrorRowMessage(String errorRowMessage) {
		this.errorRowMessage = errorRowMessage;
	}


}

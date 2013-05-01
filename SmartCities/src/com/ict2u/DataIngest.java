package com.ict2u;

public class DataIngest {
	  public String dataTitle;
	  public String timestamp, temporalType;
	  public float latTop, longLeft;
	  public int iRows, iCols;
	  public float rowHeight, colWidth;
	  public float[][] aDataIn;
	  
	  public DataIngest() {
		  this.dataTitle = "No Title";
		  this.timestamp = "19700101T000000";
		  this.temporalType = "Annually";
		  this.iRows = 10;
		  this.iCols = 10;
		  this.rowHeight = 0.01f;
		  this.colWidth = 0.01f;
	  }
	  
	  public void InitialiseDataIn( int irows, int icols) {
		  for (int i=0;i<irows;i++)
			  for (int j=0;j<icols;j++)
				  this.aDataIn[i][j] = 0.0f;
	  }
	  
	  public void Show() {
	  		System.out.println(this.dataTitle);
	  		System.out.println("Time: " + this.timestamp+ " [Type:"+ this.temporalType + "]");
	  		System.out.println(this.iRows);
	  		System.out.println(this.iCols);
	  		System.out.println(this.rowHeight);
	  		System.out.println(this.colWidth);
	  }
}

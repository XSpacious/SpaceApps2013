package com.ict2u;

public class DataInNode {
	  private FPoint fDataLatLng;
	  private String strDataValue;
	  
	  public DataInNode() {
		  this.fDataLatLng = new FPoint();
		  this.strDataValue = "undefined";
	  }
	  
	  public DataInNode(float fLat, float fLng, String strValue) {
		  this.fDataLatLng = new FPoint(fLat, fLng);
		  this.strDataValue = strValue;
	  }
	    
	  public void setDataNode(float fLat, float fLng, String strValue) {
//		  this.fDataLat = fLat;
//		  this.fDataLng = fLng;
		  this.fDataLatLng.setFPoint(fLat, fLng);
//		  this.fDataLatLng = new FPoint(fLat, fLng);
		  this.strDataValue = strValue;
	  }
	    
	  public String getDataNodeValue() {
		  return( this.strDataValue );
	  }
	    
	  public FPoint getDataNodePos() {
		  return( this.fDataLatLng );
	  }
	    
//	  public void getDataNodeFPoint(float fLat, float fLng, String strValue) {
//		  this.fDataLatLng = new FPoint(fLat, fLng);
//		  this.strDataValue = strValue;
//	  }

	  public void Show() {
		  	System.out.println(this.fDataLatLng.getFPX() + "," + this.fDataLatLng.getFPY() + "," +this.strDataValue);
	  }

}

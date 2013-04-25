package com.ict2u;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.FileReader;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.xml.sax.SAXException;

import au.com.bytecode.opencsv.CSVReader;

public class SCDataTxm {

	private static final int MAX_DATAPOINTS = 100;
	private static String postCode = "LE1 1RE";
    private static float fLatTop = 52.634556f, fLngLeft=-1.127991f, fCellWidth=0.02f, fCellHeight=0.02f;
    private static int iRows=10, iCols=10, iDataNodes;
    private static boolean fVerbose = false;
    private static DataInNode []dataNode = new DataInNode[MAX_DATAPOINTS];
    private static DataInNode [][]dataOutNodes;
    private static String ofName = "..\\Data\\DataOuput.csv", ifName="..\\Data\\Mortality_2011_Source.csv";
    private static float []tanLU = new float[360];
	private static final int BEARING_STEP = 10;

    
  // URL prefix to the geocoder
  private static final String GEOCODER_REQUEST_PREFIX_FOR_XML = "http://maps.google.com/maps/api/geocode/xml";

  public static final void main (String[] argv) throws IOException, XPathExpressionException, ParserConfigurationException, SAXException {

    // query address
//    String address = "1600 Amphitheatre Parkway, Mountain View, CA";
//	    String PC2LatLon;
	    FPoint PC2LatLon, dataLatLng;

//    System.out.println(argv[0]);
//	if (argv.length>0)
//		postCode = argv[0];
	
    for (int j = 0; j < argv.length; j++) {
        char CLIoption = argv[j].charAt(1);
        switch (CLIoption) {
        case 'f':
        	// define the input CSV file
        	ifName = argv[j].substring(2);
            System.out.println("Input file: "+ifName);
            break;
        case 'o':
        	// define the output CSV file
        	ofName = argv[j].substring(2);
            System.out.println("Output file: "+ofName);
            break;
        case 't':
        	// Set the latitude of the top of the rectangle
        	fLatTop = Float.parseFloat(argv[j].substring(2));
            System.out.println("Top Latitude: "+String.valueOf(fLatTop));
            break;
        case 'l':
        	// Set the longitude of the left of the rectangle
        	fLngLeft = Float.parseFloat(argv[j].substring(2));
            System.out.println("Left Longitude: "+String.valueOf(fLngLeft));
            break;
        case 'r':
        	// Set the number of rows in the resampled rectangle
        	iRows = Integer.parseInt(argv[j].substring(2));
            System.out.println("Number of rows: "+String.valueOf(iRows));
            break;
        case 'c':
        	// Set the number of columns in the resampled rectangle
        	iCols = Integer.parseInt(argv[j].substring(2));
            System.out.println("Number of columns: "+String.valueOf(iCols));
            break;
        case 'w':
        	// Set the cell width in degrees for the resampled rectangle
        	fCellWidth = Float.parseFloat(argv[j].substring(2));
            System.out.println("Cell Width: "+String.valueOf(fCellWidth));
            break;
        case 'h':
        	// Set the cell height in degrees for the resampled rectangle
        	fCellHeight = Float.parseFloat(argv[j].substring(2));
            System.out.println("Cell Height: "+String.valueOf(fCellHeight));
            break;
        case 'p':
        	// Set the latitude of the top of the rectangle
        	postCode = argv[j].substring(2);
            System.out.println("Postcode: "+postCode);
            break;
        case 'v':
        	// Set the latitude of the top of the rectangle
            fVerbose = true;
            break;
        default:
            System.err.println("WARNING: Unsupported Command Line Option -  " + CLIoption);
            break;
        }
    }

    // set up Tan lookup table for speed
    for (int angle=0; angle<360; angle++)
    	tanLU[angle] = (float)Math.tan(Math.toRadians(angle));
    
	PC2LatLon = PostCodeGeoCoder.PCGetLatLon(postCode);

//    CSVReader reader = new CSVReader(new FileReader("..\\Data\\Mortality_2011_Source.csv"), ',');
    CSVReader reader = new CSVReader(new FileReader(ifName), ',');
    String [] nextLine;
    DataIngest dataInHeader = new DataIngest();
    iDataNodes = 0;
    
    try { 
    	nextLine = reader.readNext(); // Read Title
    	dataInHeader.dataTitle = nextLine[0];
    	nextLine = reader.readNext(); // Read time stamp and temporal type
    	dataInHeader.timestamp = nextLine[0];
    	dataInHeader.temporalType = nextLine[1];
       	while ((nextLine = reader.readNext()) != null) {
    		// nextLine[] is an array of values from the line
       		// if there are only two entries then generate Lat/Lon from postcode
       		
           	if (fVerbose)
           		System.out.print(nextLine[0] + ", ");
           	
       		if (nextLine.length==2) {
       			dataLatLng = PostCodeGeoCoder.PCGetLatLon(nextLine[0]);
       			// Data in CSV is in lat, lng i.e. y,x so invert for now
//       			dataNode[iDataNodes] = new DataInNode(dataLatLng.getFPX(), dataLatLng.getFPY(), nextLine[1]);
       			dataNode[iDataNodes] = new DataInNode(dataLatLng.getFPY(), dataLatLng.getFPX(), nextLine[1]);
//       			dataNode[iDataNodes].setDataNode(dataLatLng.getFPX(), dataLatLng.getFPY(), nextLine[1]); 
       	       	if (fVerbose){
       	       		System.out.print( String.valueOf(dataLatLng.getFPX()) + ", " + String.valueOf(dataLatLng.getFPY()) + ", ");
       	       		System.out.println(nextLine[1]);
       	       	}
       		} else {
//       			dataNode[iDataNodes].setDataNode(Float.parseFloat(nextLine[1]), Float.parseFloat(nextLine[2]), nextLine[3]); 
//       			dataNode[iDataNodes] = new DataInNode(Float.parseFloat(nextLine[1]), Float.parseFloat(nextLine[2]), nextLine[3]);
       			// Data in CSV is in lat, lng i.e. y,x so invert for now
       			dataNode[iDataNodes] = new DataInNode(Float.parseFloat(nextLine[2]), Float.parseFloat(nextLine[1]), nextLine[3]);
//       			dataNode[iDataNodes] = new DataInNode(-11.2f, 15.8f, "36.2");
//       			dataNode[iDataNodes].setDataNode(-11.2f, 15.8f, "36.2"); 
       	       	if (fVerbose){
       	       		System.out.print(nextLine[1] + ","+nextLine[2] + ", ");
       	       		System.out.println(nextLine[3]);
       	       	}
    		}
       		iDataNodes++;
    	}
       	if (fVerbose){
           	dataInHeader.Show();
           	for (int node=0; node <iDataNodes; node++)
           		dataNode[node].Show();
       	}
       	
       	resampleData();
//   		System.out.println(getCircularData(51.2f,-1.2f));
   		resampleDatatoCircle(-1.14f,52.7f);
       	
    } catch(Exception e) { 
    	System.err.println("Parse Error: " + e.getMessage()); 
    } 
  
  }
  
  private static void resampleData() {
	  // We have a top left corner, a cell width & height, the number of rows and cols of the output
	  // resampled data and a selection of nodes within that rectangle
	  dataOutNodes = new DataInNode [iRows][iCols];
	  float fCumWeight, fNodeWeight;
	  float fx, fy, fCellValue;
	  
	  for (int node=0; node <iDataNodes; node++)
       		dataNode[node].Show();

	  fy = fLatTop;
	  for (int row=0; row<iRows; row++) {
		  fx = fLngLeft;
		  for (int col=0; col<iCols; col++) {
        	 fCumWeight = 0.0f;
       	  	 fCellValue = 0.0f;
        	 for (int node=0; node <iDataNodes; node++) {
        		 fCumWeight += GetNodeWeight(fx, fy, dataNode[node].getDataNodePos().getFPX(),dataNode[node].getDataNodePos().getFPY());
        	 }
//     			System.out.println("CW " + fCumWeight);
        	 for (int node=0; node <iDataNodes; node++) {
        		 fNodeWeight = GetNodeWeight(fx, fy, dataNode[node].getDataNodePos().getFPX(),dataNode[node].getDataNodePos().getFPY())/fCumWeight;
            	 fCellValue += Float.parseFloat(dataNode[node].getDataNodeValue())*fNodeWeight;
//            		System.out.println("("+row+","+col+") "+fNodeWeight + " " +fCellValue);
        	 }
        	 
        	 dataOutNodes[row][col] = new DataInNode(fx, fy, String.valueOf(fCellValue));
        	 fx += fCellWidth;
         }
     	 fy -= fCellHeight;
	  }
	
	  for (int row=0; row<iRows; row++) {
         for (int col=0; col<iCols; col++) {
	       		System.out.print(dataOutNodes[row][col].getDataNodeValue()+",");
//  	       		dataOutNodes[row][col].Show();
	         }
       		System.out.println();
	  }
  }

  private static float GetNodeWeight(float x1, float y1, float x2, float y2) {
	  // Initial implementation as a distance squared fall off. 
	  float weight;
	  
	  weight = (x2-x1)*(x2-x1) + (y2-y1)*(y2-y1);
	  return( weight );
  }

  private static void resampleDatatoCircle( float x, float y) {
	  // from defined location, derive weighted cumulative value in a bearing and repeat for all 360 degs
	  // For each angle, find the intersecting side edge and intersecting top or bottom edge
	  // divide line of point to intersection of relevant edge into 4 (initial) samples along the line then
	  // weight each point in a sliding scale
	  // x' , y' (xd,yd) is the point of intersection with the upper or lower edge and 
	  // x'' , y'' (xdd,ydd) is the point of intersection with the left or right side edge
	  // Liberal use of if statements to optimise performance 
	  float ix, iy, xi, yi;
	  float maxVal=-100000.0f, minVal=100000.0f, value;
	  float []weighting = {0.53f,0.27f,0.13f,0.007f};
	  int gridx, gridy;
	  FPoint fpCentre = new FPoint(x,y);
	  FPoint fpInter = new FPoint();
	  float []BearingValues = new float[360]; 

	  System.out.println(fLatTop+", "+(fLngLeft+fCellWidth*iCols)+", "+(fLatTop - fCellHeight*iRows)+", "+fLngLeft);

	  for (int bearing=0; bearing<360; bearing+=BEARING_STEP) {
//		  for (int bearing=0; bearing<360; bearing++) {
		  fpInter =  getIntersectionPoint( bearing, fpCentre);

		  ix = fpInter.getFPX();
		  iy = fpInter.getFPY();
		
//   		  System.out.println(bearing+": ("+ix+","+iy+")");
   		  value = 0;
		  for (int i=0; i<weighting.length; i++) {
			  xi = (i+1)*(ix-x)/(weighting.length+1)+x;
			  yi = (i+1)*(iy-y)/(weighting.length+1)+y;
			  gridx = (int)Math.floor((xi-fLngLeft)/fCellWidth);
			  gridy = (int)Math.floor((yi-(fLatTop - fCellHeight*iRows))/fCellHeight);
			  value += Float.parseFloat(dataOutNodes[gridy][gridx].getDataNodeValue())*weighting[i];
		  }
		  if (value>maxVal) maxVal = value;
		  if (value<minVal) minVal = value;
		  System.out.println(value);
		  BearingValues[bearing] = value;
	  }
	  System.out.println(minVal + " - " + maxVal);
	  
	  for (int bearing=0; bearing<360; bearing+=BEARING_STEP) {
		  System.out.println(100.0*(BearingValues[bearing]-minVal)/(maxVal-minVal));
	  }
  }

  private static FPoint getIntersectionPoint( int bearing, FPoint centre) {
	  // from defined location, derive weighted cumulative value in a bearing and repeat for all 360 degs
	  // For each angle, find the intersecting side edge and intersecting top or bottom edge
	  // divide line of point to intersection of relevant edge into 4 (initial) samples along the line then
	  // weight each point in a sliding scale
	  // x' , y' (xd,yd) is the point of intersection with the upper or lower edge and 
	  // x'' , y'' (xdd,ydd) is the point of intersection with the left or right side edge
	  // Liberal use of if statements to optimise performance 
	  float xd, yd, xdd, ydd, ix, iy;
	  float x, y;
	  FPoint fpInter = new FPoint();

	  x = centre.getFPX();
	  y = centre.getFPY();
//	  System.out.println(fLatTop+", "+(fLngLeft+fCellWidth*iCols)+", "+(fLatTop - fCellHeight*iRows)+", "+fLngLeft);

	  // check for NSEW bearings directly
	  if (bearing==0) { fpInter.setFPoint( x, fLatTop);    		
//	  	System.out.println(bearing+": ("+ix+","+iy+")");
	  	return( fpInter);}
	  if (bearing==90) { fpInter.setFPoint( fLngLeft+fCellWidth*iCols, y);    		
	  	return( fpInter);}
	  if (bearing==180) { fpInter.setFPoint( x, fLatTop - fCellHeight*iRows );    		
	  	return( fpInter);}
	  if (bearing==270) { fpInter.setFPoint( fLngLeft, y);    		
	  	return( fpInter);}
	  
	  // Find side edge
	  if (bearing <180){
		  // right hand edge
		  if (bearing >90){ // SE Quadrant
			  yd = fLatTop - fCellHeight*iRows;
			  xd = tanLU[180-bearing]*(y-yd) + x;
			  xdd = fLngLeft+fCellWidth*iCols;
			  ydd = y-((xdd-x)/tanLU[180-bearing]);
			  if (xdd<xd) {
				  ix = xdd; iy = ydd;
			  } else {
				  ix = xd; iy = yd;
			  }
		  }
		  else {
			  // bearing <90 - NE Quadrant
			  yd = fLatTop;
			  xd = tanLU[bearing]*(yd-y) + x;
			  xdd = fLngLeft+fCellWidth*iCols;
			  ydd = (xdd-x)/tanLU[bearing]+y;
			  if (xdd<xd) {
				  ix = xdd; iy = ydd;
			  } else {
				  ix = xd; iy = yd;
			  }
		  }
	  } else {
		  // bearing >180)
		  // left hand edge
		  if (bearing <270){ //SW quadrant
			  yd = fLatTop - fCellHeight*iRows;
			  xd = x-(tanLU[bearing-180]*(y-yd));
			  xdd = fLngLeft;
			  ydd = y-((x-xdd)/tanLU[bearing-180]);
			  if (xdd>xd) {
				  ix = xdd; iy = ydd;
			  } else {
				  ix = xd; iy = yd;
			  }
		  }
		  else {
			  // bearing <90 - NW Quadrant
			  yd = fLatTop;
			  xd = x-(tanLU[360-bearing]*(yd-y));
			  xdd = fLngLeft;
			  ydd = (x-xdd)/tanLU[360-bearing]+y;
			  if (xdd>xd) {
				  ix = xdd; iy = ydd;
			  } else {
				  ix = xd; iy = yd;
			  }
		  }
	  }
//	System.out.println(bearing+": ("+ix+","+iy+")");

	fpInter.setFPoint( ix, iy );    		
	return( fpInter);
  }

  private static String getCircularData( float x, float y) {
	  // from defined location, derive value in a bearing and repeat for all 360 degs
	  String circleData = "";
	  float fVal,fRange,fNodeDiff;
	  float [][]fNodes = {{0,10.0f},{40,15.0f},{80,25.0f},{120,80.0f},{160,120.0f},{200,65.0f},{240,10.0f},{280,195.0f},{320,45.0f},{360,10.0f}};
	  
	  for (int bearing=0; bearing<360; bearing++) {
		  for (int i=0;i<fNodes.length-1; i++)
		  {
			  if (bearing >= fNodes[i][0] && bearing<fNodes[i+1][0]) {
				  fRange = fNodes[i+1][0]-fNodes[i][0];
				  fNodeDiff = fNodes[i+1][1]-fNodes[i][1];
				  fVal = (bearing-fNodes[i][0])*fNodeDiff/fRange +fNodes[i][1];
				  circleData = circleData + String.valueOf(fVal);
				  if (bearing<359)
					  circleData = circleData + ",";
				  break;
			  }
		  }
	  }
	  return( circleData );
  }
}

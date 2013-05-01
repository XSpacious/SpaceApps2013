/** XSpacious
 *  International Space Apps Challenge 2013
 *  
 *  Licensed under a Creative Commons Attribution 3.0 Unported License.
 *  http://creativecommons.org/licenses/by/3.0/
 *  
 *  When using this code please attribute as follows
 *  including code authors and team members:
 *  
 *    This code was developed by the XSpacious project team
 *    as part of the International Space Apps Challenge 2013.
 *  
 *    Code Authors:
 *    Ron Herrema
 *  
 *    Team Members:
 *    Abdulrazaq Abba
 *    Rallou Dadioti
 *    Matthew Forman
 *    Ron Herrema
 *    Joel Mitchelson, Ogglebox Sensory Computing Ltd.
 *    Konstantina Saranti
 *    Andy Schofield
 *    Tim Trent
 *  
 *  Please note that the data used for this visualization, though borrowed from Council resources, should not be assumed to be accurate and are presented here for demonstration purposes only.
 */

class Dot {

  float lat, lon, newLat, newLon, max, min, dataNum, circ; 
  String data;


  Dot(float Tmin, float Tmax, int Trow, Table tableName) {

    lat = tableName.getFloat(Trow, 1);
    lon = tableName.getFloat(Trow, 2);
    newLat = map(lat, 52.586139, 52.678343, height, 0 ); 
    newLon = map(lon, -1.216413, -1.043721, 0, width ); 
    dataNum = tableName.getFloat(Trow, 3);
    circ = map(dataNum, Tmin, Tmax, 5, 35);
    
  }

  void display( int red, int green, int blue, int alpha, int offset) {
    // draw the dot
    fill(red, green, blue, alpha);
    noStroke();
    ellipse(newLon+offset, newLat, circ, circ);
  }

  void hover(int Trow, Table tableName, int offset) {
    if ((mouseX > (newLon-10)+offset) && (mouseX < (newLon+10)+offset) && (mouseY > (newLat-10)) && (mouseY < (newLat+10))) {
      fill(40);
      textSize(20);
      text(tableName.getString(Trow, 3), newLon, newLat-20);
    }
  }
}



Table table, table2;
PFont font; 
PImage map;
int rowCount, rowCount2;
ArrayList dots, dots2;



void setup() {
  size(900, 800);
  background(255);
  frameRate(30);
  frame.setBackground(java.awt.Color.BLACK);

  font = loadFont("ArialUnicodeMS-24.vlw");

  map = loadImage("Leicester3.png");


  table = loadTable("AvgConsDomGas.csv", "header");
  rowCount = table.getRowCount();

  table2 = loadTable("PopulationDensity.csv", "header");
  rowCount2 = table2.getRowCount();

  dots = new ArrayList();
  for (int i = 0; i<rowCount; i++) {
    dots.add(new Dot(11444, 23842, i, table));
  }



  dots2 = new ArrayList();
  for (int i = 0; i<rowCount2; i++) {
    dots2.add(new Dot(5.2, 171.6, i, table2));
  }
}


void draw() {
  background(255);


  image(map, 0, 0); 

  fill(0, 0, 200);
  textSize(24);
  text("Average Annual Gas Consumption", 500, 50);

fill(200, 0, 0);
  textSize(24);
  text("Population Density", 155, 65);


  for (int i = 0; i<rowCount; i++) {
    Dot dot = (Dot) dots.get(i);
    dot.display(0, 0, 255, 150, 0);
  }

  for (int i = 0; i<rowCount2; i++) {
    Dot dot2 = (Dot) dots2.get(i);
    dot2.display(255, 0, 0, 150, 25);
  }

  for (int i = 0; i<rowCount; i++) {
    Dot dot = (Dot) dots.get(i);
    dot.hover(i, table, 0);
  }

  for (int i = 0; i<rowCount2; i++) {
    Dot dot2 = (Dot) dots2.get(i);
    dot2.hover(i, table2, 25);
  }

}


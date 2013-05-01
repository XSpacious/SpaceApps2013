package com.ict2u;

public class FPoint {
	  private float fX, fY;

	  public FPoint() {
		  this.fX = 0.0f;
		  this.fY = 0.0f;
	  }

	  public FPoint(float x, float y) {
		  this.fX = x;
		  this.fY = y;
	  }

	  public void setFPoint(float x, float y) {
		  this.fX = x;
		  this.fY = y;
	  }

	  public float getFPX() {
		  return( this.fX );
	  }

	  public float getFPY() {
		  return( this.fY );
	  }
}

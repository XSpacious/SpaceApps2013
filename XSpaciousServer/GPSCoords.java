/** XSpacious
 *  International Space Apps Challenge 2013
 *  
 *  Licensed under a Creative Commons Attribution 3.0 Unported License.
 *  http://creativecommons.org/licenses/by/3.0/
 *  
 *  When using this code please attribute as follows including code authors
 *  and team members:
 *  
 *    Contains code developed by the XSpacious project team as part of the
 *    International Space Apps Challenge 2013.
 *  
 *    Code Authors:
 *    Joel Mitchelson, Ogglebox Sensory Computing Ltd.
 *  
 *    Team Members:
 *    Abdulrazaq Hassan Abba
 *    Rallou Dadioti
 *    Matthew Forman
 *    Ron Herrema
 *    Joel Mitchelson, Ogglebox Sensory Computing Ltd.
 *    Konstantina Saranti
 *    Andy Schofield
 *    Tim Trent
 *  
 *  If redistributing any or all of the code in this file please include this
 *  notice and the above license and attribution information.
 */

/** Represent GPS latitude and longitude, and parse them from HTTP request strings received */
public class GPSCoords {

	private float coordLat;
	private float coordLong;
	
	/** Construct from incoming request.
	 *  Request must be formatted like <request string>:<latitude>:<longitude> */
	public static GPSCoords fromHTTPRequest(String req) {
		
		String[] reqArray = req.split(":");
		if (reqArray.length == 3) {
			try {
				return new GPSCoords(Float.parseFloat(reqArray[1]), Float.parseFloat(reqArray[2]));
			}
			catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		else {
			System.err.println("Unexpected string format in HTTP request");
		}
		
		return null;
	}
	
	private GPSCoords(float coordLat, float coordLong) {
		this.coordLat = coordLat;
		this.coordLong = coordLong;
	}
	
	public float getCoordLat() {
		return coordLat;
	}
	
	public float getCoordLong() {
		return coordLong;
	}
}

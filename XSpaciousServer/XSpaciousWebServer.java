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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

/** XSpacious web server which allows upload and download of crowd-sourced measurements based on GPS coordinates,
 *  using HTTP filenames and responses to communicate.
 * 
 * Run with JETTY distribution 7.4.5.v20110725 or later available from:
 * http://archive.eclipse.org/jetty/7.4.5.v20110725/dist/
 * 
 */
public class XSpaciousWebServer extends AbstractHandler {

	// Name of file to store readings from crowd-sourced upload
	static final String READINGS_OUTPUT_FILENAME = "readings.txt";

	// The set of files used by our app and available via this server
	static final String[] staticFileName = {
	
			"testapp.html",
			"testapp.css",
			"testapp.manifest",
			"testapp.js",
			"testappicon.png",
			"testappstartup.png",			
			"tree.png",			
			"about.html"
			
	};
	
	// A virtual 'file' name for requesting data
	static final String DATASERVICEREQUEST = "data";
	
	// A virtual 'file' name for sending data
	static final String UPLOADREQUEST = "upload";

	private HashMap<String, StaticFileCache> staticFile;
	private OutputStreamWriter readingsOutputWriter;
	
	
	/** Entry point for XSpacious web server */
	public static void main(String[] args) {
		
		System.err.println("XSpacious Web Server");
		System.err.println("International Space Apps Challenge 2013");
		
		try {
			// Create server - will run until program is  manually stopped
			XSpaciousWebServer myServer = new XSpaciousWebServer();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/** Construct a new server process and run on the current thread */
	private XSpaciousWebServer() throws Exception {
		
		// Build list of all the statically available files
		staticFile = new HashMap<String, StaticFileCache> ();
		for (String fileName : staticFileName) {
			StaticFileCache cache = new StaticFileCache(fileName);
			staticFile.put(fileName, cache);
		}
		
		
		// Open results file for data uploads
		// - will be appended to, not overwritten
		readingsOutputWriter = new OutputStreamWriter(new FileOutputStream(READINGS_OUTPUT_FILENAME, true), "UTF-8");

		// Create the web server
	    Server server = new Server(8080);
	    
	    // Handle requests via this class
	    server.setHandler(this);
	 
	    // Start the server and join it to this program thread
	    // So the server will continue until program is stopped
	    server.start();
	    server.join();
	}

	/** Custom handling of incoming HTTP requests to fetch files, get data, or receive crowd-sourced data */
	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		// System.err.println(request.getRequestURI());
		String fileName = request.getRequestURI().substring(1);
		StaticFileCache cache = staticFile.get(fileName);
		if (cache != null) {
		    baseRequest.setHandled(true);
		    cache.sendToClient(response);
		}
		else if (fileName.startsWith(DATASERVICEREQUEST)) {		
			
			GPSCoords gps = GPSCoords.fromHTTPRequest(fileName);
			if (gps == null) {
				// The filename was not formatted correctly - report as a 'file not found' error
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
			else {
				
				// retrieve data based on latitude and longitude provided from client app in HTTP request
				String locationData = getData(gps.getCoordLat(), gps.getCoordLong());
				
				// format and write a JSON response
				String json = "{\"data\":[";
				json += locationData;
				json += "]}";		
			    response.setStatus(HttpServletResponse.SC_OK);
				response.setContentType("text/plain");
				response.setHeader("cache-control", "no-cache");
				response.getWriter().write(json);

				// tell the client it was handled ok
				baseRequest.setHandled(true);						
			}			
		}
		else if (fileName.startsWith(UPLOADREQUEST)) {
			readingsOutputWriter.write(fileName + " ");
			readingsOutputWriter.flush();
		}
	}

	/** Retrieve the data based on GPS coords */
	String getData(float x, float y) {
		
		// Uncomment to use random data in range 0-255
		// return getSimulatedData();
		
		// Use data set supplied by Andy
		return getExampleData();
		
		// TODO: Call function in Andy's code to retrieve data based on x, y coords
	}	
	
	/** Simulate data using 360 comma-separated random numbers in range 0-255 */
	private String getSimulatedData() {
		float[] data = new float[360];
		for (int i = 0; i < data.length; i++) {
			data[i] = 255.0f * (float)Math.random();
		}
		String csv = "";
		int numValues = (data != null) ? data.length : 0;
		for (int index = 0; index < numValues; index++) {
			if (index > 0){
				csv += ",";
			}
			csv += data[index];
		}
		return csv;
	}

	/** Retrieve example data set as provided by Andy */
	private String getExampleData() {
		String example = "10.0,10.125,10.25,10.375,10.5,10.625,10.75,10.875,11.0,11.125,11.25,11.375,11.5,11.625,11.75,11.875,12.0,12.125,12.25,12.375,12.5,12.625,12.75,12.875,13.0,13.125,13.25,13.375,13.5,13.625,13.75,13.875,14.0,14.125,14.25,14.375,14.5,14.625,14.75,14.875,15.0,15.25,15.5,15.75,16.0,16.25,16.5,16.75,17.0,17.25,17.5,17.75,18.0,18.25,18.5,18.75,19.0,19.25,19.5,19.75,20.0,20.25,20.5,20.75,21.0,21.25,21.5,21.75,22.0,22.25,22.5,22.75,23.0,23.25,23.5,23.75,24.0,24.25,24.5,24.75,25.0,26.375,27.75,29.125,30.5,31.875,33.25,34.625,36.0,37.375,38.75,40.125,41.5,42.875,44.25,45.625,47.0,48.375,49.75,51.125,52.5,53.875,55.25,56.625,58.0,59.375,60.75,62.125,63.5,64.875,66.25,67.625,69.0,70.375,71.75,73.125,74.5,75.875,77.25,78.625,80.0,81.0,82.0,83.0,84.0,85.0,86.0,87.0,88.0,89.0,90.0,91.0,92.0,93.0,94.0,95.0,96.0,97.0,98.0,99.0,100.0,101.0,102.0,103.0,104.0,105.0,106.0,107.0,108.0,109.0,110.0,111.0,112.0,113.0,114.0,115.0,116.0,117.0,118.0,119.0,120.0,118.625,117.25,115.875,114.5,113.125,111.75,110.375,109.0,107.625,106.25,104.875,103.5,102.125,100.75,99.375,98.0,96.625,95.25,93.875,92.5,91.125,89.75,88.375,87.0,85.625,84.25,82.875,81.5,80.125,78.75,77.375,76.0,74.625,73.25,71.875,70.5,69.125,67.75,66.375,65.0,63.625,62.25,60.875,59.5,58.125,56.75,55.375,54.0,52.625,51.25,49.875,48.5,47.125,45.75,44.375,43.0,41.625,40.25,38.875,37.5,36.125,34.75,33.375,32.0,30.625,29.25,27.875,26.5,25.125,23.75,22.375,21.0,19.625,18.25,16.875,15.5,14.125,12.75,11.375,10.0,14.625,19.25,23.875,28.5,33.125,37.75,42.375,47.0,51.625,56.25,60.875,65.5,70.125,74.75,79.375,84.0,88.625,93.25,97.875,102.5,107.125,111.75,116.375,121.0,125.625,130.25,134.875,139.5,144.125,148.75,153.375,158.0,162.625,167.25,171.875,176.5,181.125,185.75,190.375,195.0,191.25,187.5,183.75,180.0,176.25,172.5,168.75,165.0,161.25,157.5,153.75,150.0,146.25,142.5,138.75,135.0,131.25,127.5,123.75,120.0,116.25,112.5,108.75,105.0,101.25,97.5,93.75,90.0,86.25,82.5,78.75,75.0,71.25,67.5,63.75,60.0,56.25,52.5,48.75,45.0,44.125,43.25,42.375,41.5,40.625,39.75,38.875,38.0,37.125,36.25,35.375,34.5,33.625,32.75,31.875,31.0,30.125,29.25,28.375,27.5,26.625,25.75,24.875,24.0,23.125,22.25,21.375,20.5,19.625,18.75,17.875,17.0,16.125,15.25,14.375,13.5,12.625,11.75,10.875";
		return example;
	}
}

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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

/** Represent a static file available from this server.
 *  Includes computation of MIME type information based on file extension,
 *  and the ability to send a server response containing this file and MIME information. 
 */
public class StaticFileCache {

	MimeType mimeType;
	byte[] dataCache;
	
	StaticFileCache(String fileName) throws IOException {
		
		this.mimeType = MimeType.fromFileName(fileName);
		InputStream is = getClass().getResourceAsStream(fileName);
		BufferedInputStream bis = new BufferedInputStream(is);
		int size = bis.available();
		this.dataCache = new byte[size];
	    bis.read(dataCache, 0, dataCache.length);
	}
	
	public void sendToClient(HttpServletResponse response) throws IOException {

	    response.setStatus(HttpServletResponse.SC_OK);
	    response.setContentType(mimeType.getMimeString());
	    OutputStream responseBody = response.getOutputStream();
	    responseBody.write(dataCache);
	    responseBody.close();
	
	}

}

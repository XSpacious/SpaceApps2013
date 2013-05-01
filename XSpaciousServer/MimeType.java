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

import java.io.IOException;

/** Represent HTTP MIME types and parse them from filenames, based on file extension */
public enum MimeType {

	HTML(".html", "text/html"),
	CSS(".css", "text/css"),
	MANIFEST(".manifest", "text/cache-manifest"),
	PNG(".png", "image/png"),
	JS(".js", "application/x-javascript");
	
	private String suffix;
	private String mimeString;
	
	MimeType(String suffix, String mimeString) {
		this.suffix = suffix;
		this.mimeString = mimeString;
	}
	
	public static MimeType fromFileName(String fileName) throws IOException {
		for (MimeType m : MimeType.values()) {
			if (fileName.endsWith(m.suffix)) {
				return m;
			}
		}			
		throw new IOException("MIME type not found for: " + fileName);
	}
	
	public String getMimeString() {
		return mimeString;
	}
}

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

// Set this to true to use accelerometer instead of compass
// - useful for diagnostics on devices without compass (e.g. iPhone 3GS).
var acceldiag = false;

// Set this to true to update the display even when no compass readings are available
// - useful for checking display on devices without compass
var displaydiag = false;

// The current request for data
var dataRequest = null;

// Most recent data received from server based on GPS coords
var data = null;

// Diagnostic counter of data requests performed
var counter = 0;

// Will be true if necessary compass sensors are found
var haveorient = false;

// Example static image for display
var img = new Image();
var imgbearing = 0;
img.src = "tree.png";

// Helper to set text in status field on main page
function setStatus(text) {
	document.getElementById("statustext").innerHTML = text;
}

// Perform a data refresh every second 
var interval = self.setInterval("refreshData()", 1000);

// Register to receive compass events
// (Or acceleration events when using acceldiag diagnostic mode)
if (acceldiag) {
	if (window.DeviceMotionEvent) {
		window.addEventListener('devicemotion', motionHandler, false);
		acceldiag = true;
		setStatus("Accel Test");
	} 
	else {
		setStatus("No motion sensors detected");
	}
}
else if (window.DeviceOrientationEvent) {
	window.addEventListener('deviceorientation', motionHandler, false);
	setStatus("Orientation On");
	haveorient = true;
} 
else {
	setStatus("No motion sensors detected");
}

// Request a reading upload (when user hits button)
function newReading() {
	if(navigator.geolocation) {
		// Prompt for geo-location
		// The handler method will provide a prompt to enter a reading
		navigator.geolocation.getCurrentPosition(promptForReadingAtLocation, noPosition,{maximumAge: 300000, timeout:1000});
	}
	else {
		setStatus("No geo-location");
	}
}

// This should be received from the GPS API after the user has clicked to upload a new reading
// Prompts the user to enter a reading and uploads it
function promptForReadingAtLocation(position) {
	var loc = position.coords.latitude.toFixed(4) + ":" + position.coords.longitude.toFixed(4);
	var reading = prompt("New reading at " + loc,"");
	if (reading != null) {
		setStatus("New reading: " + reading);
		var uploadRequest = new XMLHttpRequest();
		uploadRequest.open("GET","/upload:" + loc + ":" + reading,false);
		uploadRequest.send();
	}
}

// The error method which gets called if GPS readings fail for any reason
// (usually when device permissions disallow it)
function noPosition(error) {
	setStatus(error.message);
}

// Request a new reading at current location
function refreshData() {
	if(navigator.geolocation) {
		navigator.geolocation.getCurrentPosition(requestDataAtLocation, noPosition,{maximumAge: 300000, timeout:1000});
	}
	else {
		setStatus("No geo-location");
	}
}

// This should be received at regular intervals as a result of the interval timer and refreshData
function requestDataAtLocation(position) {
	
	// format the request for data at this location
	var loc = position.coords.latitude.toFixed(4) + ":" + position.coords.longitude.toFixed(4);
	var virtual_filename = "/data:" + loc;

	// send the request
	dataRequest = new XMLHttpRequest();
	dataRequest.open("GET",virtual_filename,false);
	dataRequest.onreadystatechange = processJSON;
	dataRequest.send();
	
	// diagnostic counter
	counter++;
	
	// optionally display now
	if (displaydiag) {
		drawData(7);
	}
}

// Process incoming data (should happen when the server replies to request sent during requestDataAtLocation)
function processJSON() {
	if ( dataRequest.readyState == 4 && dataRequest.status == 200 ) {    
		showJSON(dataRequest.responseText);
	}
}

// Display incoming data (must be JSON with 360 values in the JSON field)
function showJSON(input) {
	data = JSON.parse(input).data;
	if (data.length != 360) {
		setStatus("Data length was not 360");
	}
}

// Handle incoming compass update
function motionHandler(eventData) {
	var sensor_value;
	if (haveorient) {
		if (eventData.webkitCompassHeading != null) {
			// iOS format
			sensor_value = Math.round(eventData.webkitCompassHeading - 1.0);
		}
		else if (eventData.alpha != null) {
			// Android format
			sensor_value = Math.round(eventData.alpha - 1.0);
		}
		else {
			// Some problem - no reading found - just use zero
			sensor_value = 0;
		}
	}
	else if (acceldiag) {
		// Diagnostic mode using acceleration
		sensor_value = Math.round( Math.abs( 20.0 * eventData.accelerationIncludingGravity.z ) );
	}
	else {
		// No motion sensors available - just set to zero
		sensor_value = 0;
	}
	
	// Draw the data
	drawData(sensor_value);
}

// Draw current data at compass reading (parameter sensor_value should be in degrees) 
function drawData(sensor_value) {
	
	// lookup the current data value based on compass heading
	var data_value = Math.round( data[sensor_value] );
	
	// Output to text fields
	var data_text = data_value.toString();
	document.getElementById("compasstext").innerHTML = sensor_value.toString();
	document.getElementById("datatext").innerHTML = data_text;
	
	// Graphics context for graphics display
	var canvas = document.getElementById("compasscanvas");
	var c = canvas.getContext("2d");

	// Draw background rectangle with colour which represents data value
	c.fillStyle = "rgb(" + data_text + ", 0, 0)";
	c.fillRect(0,0,320,320);

	// Draw the top part of a compass 'wheel' at the bottom of the screen
	c.fillStyle = "rgb(128, 128, 128)";
	c.beginPath();
	c.arc(160, 1105, 921, -110.0*Math.PI/180.0, -70.0*Math.PI/180.0, false);
	c.fill();

	// Show compass marks every 10 degrees
	var deg_a = sensor_value % 10;
	var deg_b = 10 - deg_a;
	var mark_a = Math.PI * deg_a / 180.0;
	var mark_b = Math.PI * deg_b / 180.0;
	c.strokeStyle = "rgb(0, 0, 0)";
	c.beginPath();
	c.moveTo(160, 1105);
	c.lineTo(160 - 921*Math.sin(mark_a), 1105 - 921*Math.cos(mark_a));
	c.moveTo(160, 1105);
	c.lineTo(160 + 921*Math.sin(mark_b), 1105 - 921*Math.cos(mark_b));
	c.stroke();

	// Display example image on compass
	var img_rel = Math.PI * (imgbearing - sensor_value) / 180.0; 
	c.drawImage(img, 160 - (img.width/2.0) + 921*Math.sin(img_rel), 1105 - 951*Math.cos(img_rel));
}

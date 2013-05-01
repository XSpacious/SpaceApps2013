XSpacious
=========
This repository contains the code from the [XSpacious project](http://spaceappschallenge.org/project/xspacious), undertaken as part of the 
International Space Apps Challenge 2013 during the weekend of 20th and 21st April 2013.

Building the App
----------------

Eclipse projects are provided for building and running the web server 
(which includes HTML5/Javascript content for the app itself).
Use Eclipse's _Import existing project to workspace_ feature to import the
XSpaciousServer folder into the Eclipse workspace.  We used Eclipse Indio 20120216-1857.
The project also requires the open source JETTY web server to be configured witin Eclipse
as a User Libary. [JETTY distribution 7.4.5.v20110725](http://archive.eclipse.org/jetty/7.4.5.v20110725/dist/)
or later should be used.

The folder SmartCities contains necessary processing to present a variety of example data sources within the mobile app 
(must be linked to the web server to enable this).

The prototypes for the map visualisations can also be viewed from within the 
Processing environment (see [processing.org](http://processing.org)) which was originally used to create them.
To view, load Xspacious_map.pde .

License
-------

Licensed under a Creative Commons Attribution 3.0 Unported License.
http://creativecommons.org/licenses/by/3.0/

When using any code from this repository please attribute as follows including team members:
  
__Contains code developed by the XSpacious project team
 as part of the International Space Apps Challenge 2013.__
  
__Team Members__
* Abdulrazaq Hassan Abba
* Rallou Dadioti
* Matthew Forman
* Ron Herrema
* Joel Mitchelson, Ogglebox Sensory Computing Ltd.
* Konstantina Saranti
* Andy Schofield
* Tim Trent

If redistributing any code from this repository please attribute as directed in the source file headers 
including code authors and team members.

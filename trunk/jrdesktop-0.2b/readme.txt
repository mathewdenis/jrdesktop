Java Remote Desktop 

jrdesktop 0.2 Beta

May 1st, 2008

Overview
	Java Remote Desktop (jrdesktop) is an open source software for viewing and/or controlling a distance PC.

	Besides then screenshots, keyboard and mouse events transfer, jrdesktop includes many additional features (like: file transfer, data compression, color conversion, ...).

	jrdesktop uses RMI (Remote Method Invocation) with SSL/TLS to establish a secured connection between the viewer and the server.

	Java Remote Desktop (jrdesktop) is intended to run across different platforms (based on JVM).

	jrdesktop is intended to run across different platforms (based on JVM).

Requirements: JDK 6 (JRE 6) or above

Main features

    * Screenshots, keyboard and mouse events transfer
    * Control functions : Start, Stop, Pause and Resume, view only, full control
    * Screen functions : full screen, custom size, scale, ...
    * Data compression (with level selection)
    * JPEG quality compression (with level selection)
    * Color quality (full colors, 16 bits, 256 colors, gray color)
    * Clipboard transfer (texts and images only)
    * File transfer
    * Connection infos : duration, transferred data size, speed
    * Authentication & encryption
    * Multi-sessions
    * ....

Execution
	•	Make sure that you already added java bin directory to the environment variable : PATH
			(example : PATH = 	...; c:\Program Files\Java\jdk1.6.0_03\bin)
	
	•	Simply double click on jrdesktop.jar to start the application
	
	•	Or manually from the command line:
			
			Starting the server
				java -jar jrdesktop.jar -server <port> <username> <password> <ssl-enabled> <multihomed-enabled>
			
			Starting the viewer	
				java -jar jrdesktop.jar -viewer <server-address> <server-port> <username> <password> <ssl-enabled>

			Where
				<ssl-enabled> = true for a secured connection 
				<multihomed-enabled> = true case of a server with many network interfaces (multiple ip addresses)
			
			Example	
				java -jar jrdesktop.jar -server 6666 admin pwd false false
				java -jar jrdesktop.jar -viewer 192.168.1.15 6666 admin pwd false
			
Any comments, bugs, suggestions, questions; please contact us.

http://jrdesktop.sourceforge.net/

pfe062008@gmail.com

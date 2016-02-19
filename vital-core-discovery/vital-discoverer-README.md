VITAL-Discoverer
================

- Author: Salvatore Guzzo Bonifacio
- Summary: This is the Discoverer module of VITAL
- Target Project: Vital (<http://vital-iot.eu/>)
- Source: <https://gitlab.atosresearch.eu/vital-iot/vital-core-discovery>

System requirements
-------------------

For this project you need:

- Java 8.0 (Java SDK 1.8) (<https://www.oracle.com/java/index.html>)
- Maven 3.3 (<https://maven.apache.org/>)
- Wildfly 9.0.X (<http://www.wildfly.org>)

1. Follow installation instructions of Java, Maven and Wildfly for your system

Start WildFly
-------------------------------------------

1. Open a command line and navigate to the root of the Wildfly server directory.
2. The following shows the command line to start the server with the web profile:

        For Linux:   WILDFLY_HOME/bin/standalone.sh
        For Windows: WILDFLY_HOME\bin\standalone.bat


Build and Deploy Discoverer Module
----------------------------------------

1. Checkout the code from the repository:

        git clone http://gitlab.atosresearch.eu/vital-iot/vital-core-discovery.git

2. Make sure you have started the WildFly Server as described above.
3. Open a command line and navigate to the root directory of the project.
4. Open file **_src/main/resources/discovery.properties_** and set the values for the properties:

	```	
	DISCOVERER_ENDPOINT_ADDRESS=http://baseaddress.com
	DISCOVERER_ENDPOINT_PORT=8080
	DMS_ENDPOINT_ADDRESS=http://baseaddress.com/vital/dms
	```
	
**Note:** if DMS endpoint address is not set to default port, include the port in the address (E.g. http://baseaddress.com:8180/vital/dms) 
5. Type this command to build and deploy the archive:

        mvn clean package wildfly:deploy

6. This will deploy `vital-core-discovery/target/discoverer.war` to the running instance of the server.

Access the module 
---------------------

The deployed module is available at the active base address of the running Wildfly instance with path **_/discoverer_**


Undeploy the Discoverer Module
------------------------------

1. Make sure you have started the WildFly Server as described above.
2. Open a command line and navigate to the root directory of this quickstart.
3. When you are finished testing, type this command to undeploy the archive:

        mvn wildfly:undeploy
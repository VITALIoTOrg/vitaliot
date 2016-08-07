# VITALIoT

## The future of Smart Cities
VITAL is an ambitious R&D programme to develop a prototype application platform that will revolutionise real-time, location-based provision of goods and services in future smart cities, by integrating and interacting with a multitude of different Internet of Things data sources and systems.

vital-iot
================

- Author: VITAL
- Summary: This is the VITAL Integrated Platform
- Target Project: VITAL (<http://vital-iot.eu/>)
- Source: <http://gitlab.atosresearch.eu/vital-iot/vital-iot.git>

System requirements
-------------------

For this project you need:

- Java 8.0 (Java SDK 1.8) (<http://openjdk.java.net> or <https://www.oracle.com/java/index.html>)
- Maven 3.1 or better (<https://maven.apache.org/>)
- Wildfly 9.0.X (<http://www.wildfly.org>)
- MongoDB 3.2 (<https://www.mongodb.org>)
- Node.js (<https://nodejs.org/>)
- Grunt (<http://gruntjs.com/>)
- Bower (<http://bower.io/>)

1. Follow installation instructions of Java, Maven, Wildfly, MongoDB and NodeJS for your system
2. Install grunt and bower with the following commands:

        npm install -g grunt-cli
        npm install -g bower

Start MongoDB
--------------

1. Open a command line and navigate to the home directory of MongoDB
2. Start mongo with (replace MONGO_HOME with the actual directory of mongo):

        For Linux:   $MONGO_HOME/bin/mongod --dbpath $MONGO_HOME/data
        For Windows: %MONGO_HOME%\bin\mongod --dbpath %MONGO_HOME%/data

3. Check that mongo is started and listens to connections on localhost and port 27017 (default).

Start WildFly
-------------------------------------------

1. Copy files from folder wildfly in project to the application server

        cp ./wildfly/* $WILDFLY_HOME/standalone/configuration/

    - Check that the properties in vital-properties.xml reflect your environment

2. Open a command line and navigate to the root of the Wildfly server directory.
3. The following shows the command line to start the server with the web profile:

        For Linux:   $WILDFLY_HOME/bin/standalone.sh -c standalone-vital.xml
        For Windows: %WILDFLY_HOME%\bin\standalone.bat -c standalone-vital.xml

Build and Deploy the VITAL Platform
----------------------------------------

1. Checkout the code from the repository:

        git clone http://gitlab.atosresearch.eu/vital-iot/vital-iot.git
        git submodule init
        git submodule update

    - The project uses git submodules, check <https://git-scm.com/book/en/v2/Git-Tools-Submodules> for more details.

2. Make sure you have started the Wildfly Server as described above.
3. Open a command line and navigate to the root directory of the project.
4. Type this command to build and deploy the archive:

        mvn clean package wildfly:deploy

5. This will deploy all VITAL components to the running instance of the server.

Access the application
---------------------

Access the Management Platform at the following URL: <http://localhost:8080/vital-management-ui>
Access the Orchestrator at the following URL: <http://localhost:8080/vital-orchestrator>

Undeploy the VITAL Platform
--------------------------------

1. Make sure you have started the JBoss Server as described above.
2. Open a command line and navigate to the root directory of this quickstart.
3. When you are finished testing, type this command to undeploy the archive:

        mvn wildfly:undeploy

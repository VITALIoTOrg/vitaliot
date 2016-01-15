vital-management
================

- Author: Angelos Lenis
- Summary: This is the Vital Management Platform of vital
- Target Project: Vital (<http://vital-iot.eu/>)
- Source: <http://31.210.156.219/anglen/vital-management>

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
4. If mongo is installed on another machine than jboss, edit the file:

        WILDFLY_HOME/standalone/configuration/standalone.xml

and add this property in system properties:

         <system-properties>
                <property name="vital.mongodb.host" value="<host name or ip>"/>
         </system-properties>

Start WildFly
-------------------------------------------

1. Open a command line and navigate to the root of the Wildfly server directory.
2. The following shows the command line to start the server with the web profile:

        For Linux:   WILDFLY_HOME/bin/standalone.sh
        For Windows: WILDFLY_HOME\bin\standalone.bat

Import CONFIGURATION data to MongoDB
------------------------------------

1. Create a configuration.json file, similar to the one in etc/configuration.json:

        {
          "dms_url": {
            "host": "vmvital03.deri.ie",
            "port": "8011"
          },
          "discovery_url": "http://vital-integration.atosresearch.eu:8180/discoverer/ppi",
          "system_urls": [
            "http://vital-integration.atosresearch.eu:8180/vital-orchestrator-web/rest/ppi",
            "http://vital-integration.atosresearch.eu:8180/discoverer/ppi",
            "http://vital-integration.atosresearch.eu:8180/filtering/ppi",
            "http://vital-integration.atosresearch.eu:8180/iot-data-adapter-ppi/ppi/system",
            "http://vital-integration.atosresearch.eu:8180/camden-footfall-ppi/ppi/system",
            "http://vital-integration.atosresearch.eu:8180/cep",
            "http://vital-integration.atosresearch.eu:8180/hireplyppi"
          ]
        }

2. Execute the following commands to import the file in MongoDB

        mongo vital-management --eval "db.CONFIGURATION.remove({})"
        mongoimport --db vital-management --collection CONFIGURATION --file configuration.json


Build and Deploy the Management Platform
----------------------------------------

1. Checkout the code from the repository:

        git clone http://31.210.156.219/anglen/vital-management.git

2. Make sure you have started the JBoss Server as described above.
3. Open a command line and navigate to the root directory of the project.
4. Type this command to build and deploy the archive:

        mvn clean package wildfly:deploy

5. This will deploy `vital-management-ear/target/vital-management-ear.ear` and `vital-management-ui/target/vital-management-ui.war` to the running instance of the server.

Access the application 
---------------------

The first time the application is started point to this URL: <http://localhost:8080/vital-management-web/api/admin/sync> to sync with other systems metadata

Access the Management Platform at the following URL: <http://localhost:8080/vital-management-ui>


Undeploy the Management Platform
--------------------------------

1. Make sure you have started the JBoss Server as described above.
2. Open a command line and navigate to the root directory of this quickstart.
3. When you are finished testing, type this command to undeploy the archive:

        mvn wildfly:undeploy
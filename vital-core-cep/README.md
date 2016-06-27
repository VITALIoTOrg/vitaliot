# VITALCEP Module

* Author: Elisa Herrmann, Miguel Mateos
* Summary: This is the CEPVITAL Module
* Target Project: VITAL (<http://vital-iot.eu>)
* Source: <http://gitlab.atosresearch.eu/ari/vital-core-cep.git>

## System requirements

For this project you need:

* Java 8.0 (Java SDK 1.8) (<http://openjdk.java.net> or <https://www.oracle.com/java/index.html>)
* Maven (<https://maven.apache.org>)
* WildFly (10.X.X or later recommended) (<http://www.wildfly.org>)
* Mosquitto (1.3) (<http://mosquitto.org/download/>)
* MongoDB (v3.0.8 or later)

Follow installation instructions of Java, Maven, WildFly, Mosquitto and MongDB.

## Configure and Start WildFly

###Configure

Open file **_WILDFLY_HOME/standalone/configuration/standalone.xml_** and perform the following changes:

  In section **_management->security-realms_** add the following text (change the attributes values with those for your keystore):

        ```xml
        <security-realm name="UndertowRealm">
            <server-identities>
                <ssl>
                    <keystore path="my.jks" relative-to="jboss.server.config.dir" keystore-password="password" alias="mycert" key-password="password"/>
                </ssl>
            </server-identities>
        </security-realm>
        ```

  Under **_profile->subsystem (the undertow one)->server_** make sure to have:

        ```xml
        <http-listener name="default" redirect-socket="https" socket-binding="http"/>
        <https-listener name="https" security-realm="UndertowRealm" socket-binding="https"/>
        ```
  
###Start Wildfly

      $ WILDFLY_HOME/bin/standalone.sh

## Configure, Build and Deploy the VITALCEP Module

###Clone

        $ git clone http://gitlab.atosresearch.eu/vital-core-cep.git

###Configure

Copy the file **_src/main/resources/cep.properties_** to the **jboss.server.config.dir** (WILDFLY_HOME/bin/ or WILDFLY_HOME/standalone/configuration) and set the values as needed.

  ```
  # CEP configuration
  cep.serverName=bcep
  cep.ip=localhost
  cep.path=/home/vital/bcep/sources    #This is the path where the μCEP has been built
  cep.resourceshostname="vital-integration.atosresearch.eu" 

  # Mosquitto configuration
  mosquitto.brokerUrl="tcp://localhost:1883"
  mosquito.ip=localhost
  mosquito.port=1883

  # MongoDB configuration
  mongo.ip=localhost
  mongo.port=27017
  ```

###Build

Build the application from the project root to create the **_target/cep.war_** package.

        $ mvn package

###Deploy

Copy the package **_target/cep.war_** to the **_standalone/deployments_** directory of the running instance of the server.

Or 

Use the **_jboss-cli.sh_** script from the project root folder.

        $ /opt/wildfly/bin/jboss-cli.sh

        $ connect https-remoting://localhost:9993

        $ deploy --force target/cep.war

## Install  μCEP

### Copy μCEP

Copy the folder ucep to a selected location e.g

        $ cp ./ucep /home/vital
      
### Configure

Edit the confFile.ini to configure the LOGGER properties. 
Set the logger.level the logger.file as needed.
      
###Sanity checks


Test mosquitto

      $ mosquito
    
    
You should get something like this:

      1453884511: mosquitto version 1.4.7 (build date Tue, 22 Dec 2015 12:47:28 +0000) starting
      1453884511: Using default config.
      1453884511: Opening ipv4 listen socket on port 1883.
    
    
Start Mosquitto 

      $ sudo service mosquitto start

Run the μCEP

      $ ./bcep -d pp -mi mqin -mo mqou -f confFile.ini
    
Publish a test message to the μCEP input queue. Open a new terminal.

      $ mosquitto_pub -t 'mqin' -m '1 topicname pos location 23.435\\14.567 int value 20 string sensorid x'

Kill the μCEP test process with `$ Ctrl+c  ` from the cep terminal

## Access the CEP module

The CEP Module is available at the hostname and port of your WildFly instance at path **_/cep_**.

## Undeploy the CEP Module

Stop the WildFly server and remove the application related files from the **_standalone/deployments_** directory of WildFly.

Or 

Use the **_jboss-cli.sh_** script.
   
        $ /opt/wildfly/bin/jboss-cli.sh

        $ connect https-remoting://localhost:9993

        $ undeploy cep.war
    
Start WildFly.




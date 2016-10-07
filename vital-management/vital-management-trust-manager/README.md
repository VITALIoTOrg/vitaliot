# VITAL TRUST Module

* Author: Elisa Herrmann, Miguel Mateos
* Summary: This is the TRUST MANAGER Module
* Target Project: VITAL (<http://vital-iot.eu>)
* Source: <http://gitlab.atosresearch.eu/ari/vital-management-trust-manager.git>

## System requirements

For this project you need:

* Java 8.0 (Java SDK 1.8) (<http://openjdk.java.net> or <https://www.oracle.com/java/index.html>)
* Maven (<https://maven.apache.org>)
* MongoDB (v3.0.8 or later)

Follow installation instructions of Java, Maven, WildFly, Mosquitto and MongDB.

## Configure and Start WildFly

### Configure

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

## Configure, Build and Deploy the Module

### Clone

        $ git clone http://gitlab.atosresearch.eu/vital-management-trust-manager.git


Build the application from the project root to create the **_target/vital-management-trust-manager.war_** package.

        $ mvn package

### Deploy

Copy the package **_target/vital-management-trust-manager.war_** to the **_standalone/deployments_** directory of the running instance of the server.

Or 

Use the **_jboss-cli.sh_** script from the project root folder.

        $ /opt/wildfly/bin/jboss-cli.sh

        $ connect https-remoting://localhost:9993

        $ deploy --force target/vital-management-trust-manager.war


Start WildFly.




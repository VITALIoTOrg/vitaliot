# vital-ppi-gateway

* Author: Lorenzo Bracco
* Summary: This is the VITAL PPI Gateway
* Target Project: VITAL (<http://vital-iot.eu>)
* Source: <http://31.210.156.219/devtry/ppi-gateway.git>

## System requirements

For this project you need:

* Git (<https://git-scm.com>)
* Java 8.0 (Java SDK 1.8) (<http://openjdk.java.net> or <https://www.oracle.com/java/index.html>)
* Maven (<https://maven.apache.org>)
* WildFly (10.X.X or later recommended) (<http://www.wildfly.org>)

Follow installation instructions of Git, Java, Maven and WildFly.

## Configure and Start WildFly

1. Open file **_WILDFLY_HOME/standalone/configuration/standalone.xml_** and perform the following changes:
  1. In section **_management->security-realms_** add the following text (change the attributes values with those for your keystore):

        ```xml
        <security-realm name="UndertowRealm">
            <server-identities>
                <ssl>
                    <keystore path="my.jks" relative-to="jboss.server.config.dir" keystore-password="password" alias="mycert" key-password="password"/>
                </ssl>
            </server-identities>
        </security-realm>
        ```

  2. Under **_profile->subsystem (the undertow one)->server_** make sure to have:

        ```xml
        <http-listener name="default" redirect-socket="https" socket-binding="http"/>
        <https-listener name="https" security-realm="UndertowRealm" socket-binding="https"/>
        ```

  3. HTTPS should now be enabled.
2. Open a command line and navigate to the root of the WildFly server directory.
3. The following shows the command line to start the server:

        For Linux:   WILDFLY_HOME/bin/standalone.sh
        For Windows: WILDFLY_HOME\bin\standalone.bat

## Configure, Build and Deploy the PPI Gateway

1. Checkout the code from the repository:

        git clone http://31.210.156.219/devtry/ppi-gateway.git

2. Open file **_src/main/resources/config.properties_** and set the values defined there to match the locations of the security module and the proxy running the Policy Agent. For example:

      ```
      PROXY_HOST=vitalproxy.com
      SECURITY_HOST=vitalsecurity.com
      USERNAME=ppigateway
      PASSWORD=youllneverguess
      ```

3. Open a command line and navigate to the root directory of the project.
4. Type this command to build the application and create a WAR package:

        mvn package

5. Make sure you have started the JBoss Server as described above.
6. In order to deploy copy the package **_target/ppigateway.war_** to the **_standalone/deployments_** directory of the running instance of the server.

## Access the module

The PPI Gateway is available at the hostname and port of your WildFly instance at path **_/ppigateway_**.

## Undeploy the PPI Gateway

1. Stop the WildFly server (by killing the script used to start it).
2. Remove the application related files from the **_standalone/deployments_** directory of WildFly.
3. Restart WildFly.

Or use any other method offered by WildFly, such as the **_jboss-cli_** interface.


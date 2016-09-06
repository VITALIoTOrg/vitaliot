Security setup guide
====================

The security system of the project is based on the OpenDJ and OpenAM open source projects. In particular it uses the OpenAM Identity Provider and Apache Web Policy Agent and the OpenDJ Data Store.

VITAL provides the code for the needed components including a few fixes, customizations and default configurations.

The installation of these modules is needed in order for the VITAL platform to work.

Sources
-------

* https://backstage.forgerock.com/#!/docs/opendj/3/install-guide
* https://backstage.forgerock.com/#!/docs/openam/13/install-guide
* https://backstage.forgerock.com/#!/docs/openam-policy-agents/3.3.0/web-install-guide

Steps
-----

* Add the FQDN (Fully Qualified Domain Name) to "/etc/host":

      ```
      127.0.0.1       local.vital-iot-test.com localhost
      127.0.1.1       local.vital-iot-test.com <anything>
      ```

* Generate of Test Certificates and Test Keystore with these commands:
    
    * sudo openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout vital-test.key -out vital-test.crt
    * sudo openssl pkcs12 -export -name vital -in vital-test.crt -inkey vital-test.key -out keystore.p12
    * sudo keytool -importkeystore -destkeystore vital-test.jks -srckeystore keystore.p12 -srcstoretype pkcs12 -alias vital

* ONLY UNDER PROXY
    
    * Set proxy settings on your system

    * Set proxy exception rules for all FQDN

    * Set npm proxy:

        ```
        npm config set proxy http/https http://your.proxy.com:your_port
        ```

    * Set git proxy and rule:

        ```
        git config --global url."https://".insteadOf git://
        git config --global http/s.proxy http://your.proxy.com:your_port
        ```

    * Add JAVA_OPTS proxy settings:

        ```
        -Dhttp.proxyHost=your.proxy.com -Dhttp.proxyPort=your_port -Dhttps.proxyHost=your.proxy.com -Dhttps.proxyPort=your_port   
        ```

* Install OpenJDK 8. For Ubuntu 16.04:

      ```
      sudo add-apt-repository ppa:openjdk-r/ppa
      sudo apt-get update
      sudo apt-get install openjdk-8-jdk
      ```

  * If you have multiple JDKs installed make the OpenJDK 8 the default one
  * Also set the environment variable JAVA_HOME, e.g.:

        ```
        export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
        ```

* Add the following to "/etc/security/limits.conf"

      ```
      <username> soft nofile 65536
      <username> hard nofile 131072
      ```

* Clone OpenDJ repository and build it (resulting ".zip" in "opendj-server-legacy/target/package")

      ```
      git clone http://gitlab.atosresearch.eu/vital-iot/vital-core-security-opendj.git
      mvn clean install
      ```

* Unzip OpenDJ to the desired location and install it

      ```
      ./setup --cli
      ```

  * Leave default root user and enter a password
  * When asked for FQDN use the one used in "/etc/hosts"
  * Leave default ports
  * Answer "yes" to "Do you want to create base DNs in the server?"
  * Choose JE backend
  * Base DN in this example is dc=vital-iot-test,dc=com
  * When asked choose "Only create the base entry"
  * You should have a setup summary similar to this:

        ```
        LDAP Listener Port:            1389
        Administration Connector Port: 4444
        JMX Listener Port:             
        LDAP Secure Access:            disabled
        Root User DN:                  cn=Directory Manager
        Directory Data:                Backend Type: JE Backend
        Create New Base DN dc=vital-iot-test,dc=com
        Base DN Data: Only Create Base Entry (dc=vital-iot-test,dc=com)
        ```

* Clone OpenAM repository and build it (resulting ".war" is found in "openam-server/target")

      ```
      git clone http://gitlab.atosresearch.eu/vital-iot/vital-core-security-openam.git
      mvn package
      ```

* Install Apache Tomcat 8
    
  * Download and install Tomcat with:
    ```
    apt-get install tomcat8
    ```

  * Add admin user (in file "conf/tomcat-users.xml")

        ```xml
        <user username="admin" password="password" roles="manager-gui,admin-gui"/>
        ```

  * Activate and configure HTTPS connector ("conf/server.xml", an example file is provided)

        ```xml
        <Connector port="8453" protocol="HTTP/1.1" maxThreads="150" SSLEnabled="true" scheme="https" secure="true" clientAuth="false" sslProtocol="TLS" URIEncoding="UTF-8" keystoreFile="/your/tomcat/installation/path/conf/vital-test.jks" keystorePass="password" />
        ```
        
  * Define AJP Connector ("conf/server.xml", an example file is provided)
    
        ```
        <!-- Define an AJP 1.3 Connector on port 8009 -->
        <Connector port="8009" protocol="AJP/1.3" redirectPort="8453" />
        ```

  * Define a port redirection Connector:

        ```
        <Connector port="8090" protocol="HTTP/1.1"
            connectionTimeout="20000"
            redirectPort="8453" />
        ```

  * Replace the name parameter inside the Host section with your FQDN

  * Replace defaultHost parameter inside the Engine section with your FQDN

* For testing local installation a certificate is provided to be used with Tomcat, WildFly and Apache. To add the self-signed certificate to your java trust store run:

      ```
      keytool -import -trustcacerts -alias vital -file vital-test.crt -keystore
      $JAVA_HOME/jre/lib/security/cacerts
      ```

* Copy the OpenAM ".war" package to Tomcat to deploy it

* Rename the OpenAm ".war" as vital-openam.war

* Configure OpenAM from the web interface
  * Go to "https://local.vital-iot-test.com:8453/vital-openam"
  * Choose "Create New Configuration"
  * Choose to use OpenDJ for both configuration and user data: enter OpenDJ configuration info
    * Port 1389
    * Root suffix: dc=vital-iot-test,dc=com
    * No SSL/TLS unless it was set up during OpenDJ configuration
    * No load balancing
    * Agent password must be different: "agentpassword" for example

* After the initial configuration enter the OpenAM console:
  * SNMP: top realm -> configuration -> system -> monitoring -> enable monitoring and SNMP interface

* Install the Apache Web Server. Enable SSL and proxy modules:

      ```
      sudo a2enmod proxy_http
      sudo a2enmod proxy
      sudo a2enmod ssl
      ```

* Use the provided configuration file for for a default proxy configuration (you may want to copy it to "sites-enabled/000-default.conf" in Apache system folder)

* Clone the Web Policy Agent repository and build it (resulting ".zip" in "build")

      ```
      git clone http://gitlab.atosresearch.eu/vital-iot/vital-core-security-web-agents.git
      make
      ```

* Unzip the built package 

* Create a web agent profile from OpenAM console. With the test configuration:
  * OpenAM URL: "https://local.vital-iot-test.com:8453/vital-openam"
  * Agent URL: "https://local.vital-iot-test.com:443"

* Go into "web_agents/apache24_agent/bin" and install the agent:

      ```
      echo password > /tmp/pwd.txt
      chmod 400 /tmp/pwd.txt
      sudo ./agentadmin --i
      ```
  * Accept licence
  * Ignore existing properties file
  * Insert URLs and name used while creating the profile
  * Provide the installer with the "/tmp/pwd.txt" password file
  * Restart apache2
  * Enable cookie security for the agent in OpenAM Web Agent SSO section
  * Change cookie name value to "vitalAccessToken" for the agent in OpenAm Web Agent SSO section

* Create group "administrators", create a user and make it part of the group

* From OpenAM console give "Read and write access to all realm and policy properties" privilege to the group

* Create a policy to allow GET on "https://local.vital-iot-test.com/vital/snmp/openam_stats" for group"administrators"

* Give "Read and write access for policy administration (includes related REST endpoints)" privilege to "internal" and "developers" groups (which should be created)

* From the OpenAM console go into "OpenAM services" section of the agent profile and set the OpenAM Login URL to "https://local.vital-iot-test.com:8443/securitywrapper/rest/unauthorized"

* Perform any other needed configuration either from the OpenAM console or using the VITAL management application

* It's very important to add a policy for every module that Vital provides, otherwise the content won't be available and will give a 403 error.
    
    ```
      https://localhost:8443/vital-management-web/*
    ```


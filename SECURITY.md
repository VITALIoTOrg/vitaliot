Security setup and configuration
================================

The security system of the project is based on the OpenDJ and OpenAM open source
projects. In particular it uses the OpenAM Identity Provider and Apache Web
Policy Agent and the OpenDJ Data Store.

VITAL provides the code for the needed components including a few fixes,
customizations and default configurations.

The installation of this components is needed in order for the VITAL platform to
work.

Sources
-------

https://backstage.forgerock.com/#!/docs/openam/13/install-guide
https://backstage.forgerock.com/#!/docs/opendj/3/install-guide

Steps
-----

* Clone code and compile OpenAM (mvn package), find war in openam-server/target

* Add FQDN to /etc/hosts… will look like:
    127.0.0.1       local.vital-iot-test.com localhost
    127.0.1.1       local.vital-iot-test.com <anything>

    # The following lines are desirable for IPv6 capable hosts
    ::1     ip6-localhost ip6-loopback
    fe00::0 ip6-localnet
    ff00::0 ip6-mcastprefix
    ff02::1 ip6-allnodes
    ff02::2 ip6-allrouters

* Install openjdk8 (command for Ubuntu 14.04)
    * sudo add-apt-repository ppa:openjdk-r/ppa
    * sudo apt-get update
    * sudo apt-get install openjdk-8-jdk
    * If you have multiple JDKs installed make the just installed one the default
    * Also set the environment variable JAVA_HOME:
        export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64 (substitute with your path)

* Add the following to /etc/security/limits.conf
    <username> soft nofile 65536
    <username> hard nofile 131072

* Clone code and compile OpenDJ
    * mvn clean install
    * result in opendj-server-legacy/target/package (folder + zip)

* Configure OpenDJ (bundle configuration file? Wizard to customize?)
    * ./setup --cli
    * Leave default root user and enter a password
    * When asked for FQDN use the one you put in /etc/hosts
    * Leave default ports
    * Do you want to create base DNs in the server? Yes
    * Choose JE backend
    * Base DN in our example is dc=vital-iot-test,dc=com
    * When asked choose "Only create the base entry"
    * You should have a setup summary like this:
        LDAP Listener Port:            1389
        Administration Connector Port: 4444
        JMX Listener Port:             
        LDAP Secure Access:            disabled
        Root User DN:                  cn=Directory Manager
        Directory Data:                Backend Type: JE Backend
        Create New Base DN dc=vital-iot-test,dc=com
        Base DN Data: Only Create Base Entry (dc=vital-iot-test,dc=com)

* Proceed with OpenAM installation

* If you are using VITAL custom OpenAM CORS is already enabled, no need to go into
the war and modify web.xml (you also have the FineGrainedAccess application type
already defined, so it's highly recommended)

* Install Apache Tomcat8

* Add admin user
    * <user username="admin" password="password" roles="manager-gui,admin-gui"/>
        (in file conf/tomcat-users.xml)
    * Activate and configure HTTPS connector as written in OpenAM guide (conf/server.xml)
        <Connector port="8443" protocol="HTTP/1.1" maxThreads="150" SSLEnabled="true" scheme="https" secure="true" clientAuth="false" sslProtocol="TLS" URIEncoding="UTF-8" keystoreFile="/opt/tomcat/conf/vital-test.jks" keystorePass="password" />

* We provide the configuration file for this

* Copy war to tomcat to deploy

* Start custom installation from web interface (choose new configuration)
    * Access it from https://local.vital-iot-test.com:8453/vital-openam
    * SSL/TLS disable -> communication broken, matter of certificates?
    * Localhost should be fine unless you want to distribute -> choose OpenDJ and insert data from its configuration
    * port 1389
    * Root suffix: dc=vital-iot-test,dc=com
    * Insert OpenDJ password
    * Next step almost identical
    * Agent password is different: "agentpassword" for example

* To add the self-signed certificate to your java trust store run:
    keytool -import -trustcacerts -alias vital -file vital-test.crt -keystore
    $JAVA_HOME/jre/lib/security/cacerts

* For test local installation we provide the certificate

* SNMP: top realm -> configuration -> system -> monitoring -> enable monitoring and SNMP interface

* Install apache2.4

* sudo a2enmod proxy_http

* sudo a2enmod proxy

* sudo a2enmod ssl

* Use provided configuration file for sites-enabled/000-default.conf

* Download web agent code, make to build, unzip zip in build directory

* Create web agent from OpenAM console
    * OpenAM URL: https://local.vital-iot-test.com:8453/vital-openam
    * Agent URL: https://local.vital-iot-test.com:443

* Go into web_agents/apache24_agent/bin
    * echo password > /tmp/pwd.txt
    * chmod 400 /tmp/pwd.txt
    * sudo ./agentadmin --i
    * Accept licence
    * Ignore existing properties file
    * Insert URLs and name used while creating the profile
    * Give /tmp/pwd.txt password file
    * Restart apache2
    * Enable cookie security for agent in OpenAM console
    * Enable CDSSO

* Create group administrators, create a user and put it in the group

* From OpenAM console give "Read and write access to all realm and policy properties" privilege to the group

* Create policy to allow GET on "https://local.vital-iot-test.com/vital/snmp/openam_stats" for group
administrators

* Give "Read and write access for policy administration (includes related REST endpoints)" privilege to internal group


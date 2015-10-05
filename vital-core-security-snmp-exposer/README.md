# SNMP Exposer

---

This project contains the code for the SNMP Exposer. The module, made to run on
WildFly and Java 8, exposes a REST endpoint to access some monitoring data from
the SNMP interface of OpenAM.

## Configuration

In order to change the configuration you need to edit file
"src/main/resources/config.properties" and change the values of the properties:

* **IDP_HOST** is the address of the server running OpenAM

* **SNMP_PORT** is the port number where the OpenAM SNMP interface is listening
   (used for monitoring)

To change the path where WildFly will deploy the application you have to change
the value of "context-root" in file "src/main/webapp/WEB-INF/jboss-web.xml".

## Build and run

In order to build the application you will need the following tool to be
installed on your machine:

* **Maven** (https://maven.apache.org/)

Then you can build issuing the following command:

```
mvn package
```

You will then find a ".war" file in the "target" folder; you can use it to
deploy the application on WildFly (tested on WildFly 9.0.1 with OpenJDK 8).

You may also want to take a look at the Vital Deployer project, featuring a
script to automatically build and deploy this application plus other components
of the Vital system.


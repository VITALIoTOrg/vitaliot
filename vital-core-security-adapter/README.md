# Vital Security Adapter

---

This project contains the code for the Vital Security Adapter. The module, made
to run on WildFly and Java 8, interacts with the OpenAM server and exposes a
REST interface to access Vital security features.

## Configuration

In order to change the configuration you need to edit file
"src/main/resources/config.properties" and change the values of the properties:

 * **IDP_HOST** is the address of the server running OpenAM

 * **IDP_PORT** is the port number where OpenAM is listening (REST interface)

 * **SNMP_PORT** is the port number where the OpenAM SNMP interface is listening
   (used for monitoring)

 * **AUTH_TOKEN** is the name of the token cookie used by OpenAM

 * **MAN_TOKEN** is the name of the alternative token cookie currently used by
   the Security Management application

## Build and run

In order to build the application you will need the following tool to be
installed on your machine:

 * **Maven** (https://maven.apache.org/)

Then you can build issuing the following command:

```
mvn package
```

You will then find a ".war" file in the "target" folder; you can use it to
deploy the application on WildFly (tested on WildFly 9.0.1 with OpenJDK 8) over
HTTPS.

You may also want to take a look at the Vital Deployer project, featuring a
script to automatically build and deploy this application plus the Security
Management UI, another component of the Vital security system.


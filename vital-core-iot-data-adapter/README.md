### VITAL - IoT Data Adapter

The IoT Data Adapter in the Platform Agnostic Data Acquisition Layer in VITAL.

#### Dependencies
* Java SE Development Kit 8 >= 1.8.0_25
* MongoDB >= 3.2.3
* Maven >= 3.1.1
* WildFly >= 8.2.1.Final

#### How to build it

Execute the command:

	mvn clean package
	
#### How to install it

Execute the command:

	./bin/jboss-cli.sh --connect

and then enter:

	/system-property=vital.properties.file:add(value=${jboss.server.config.dir}/vital-properties.xml)

Execute the command:

	curl -X PUT "http://[es-host]:{es-port]/vital-core-iot-data-adapter"
		
#### How to deploy it

Change `src/main/resources/vital-properties.xml` (if necessary).

Copy `src/main/resources/vital-properties.xml` into `standalone/configuration` in Wildfly.

Execute the command:

	mvn wildfly:deploy
	
#### How to undeploy it

Execute the command:

	mvn wildfly:undeploy

#### How to test it

Go to http://[host]:[port]/vital-core-iot-data-adapter.

#### Notes

The property injection is based on the [CDI injecting properties](https://github.com/ChrisRitchie/CDI-injecting-properties) project at GitHub.

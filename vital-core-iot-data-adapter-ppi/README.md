### VITAL - IoT Data Adapter PPI

The PPI implementation for the VITAL IoT Data Adapter.

#### Dependencies
* Java SE Development Kit 8 >= 1.8.0_25
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

Go to http://[host]:[port]/vital-core-iot-data-adapter-ppi.

#### How to use it

Get system metadata.

	curl -X POST http://[host]:[port]/vital-core-iot-data-adapter-ppi/ppi/system/metadata
	
Get service metadata.

	curl -X POST http://[host]:[port]/vital-core-iot-data-adapter-ppi/ppi/system/service/metadata

Get sensor metadata.

	curl -X POST http://[host]:[port]/vital-core-iot-data-adapter-ppi/ppi/system/sensor/metadata

Get system status.

	curl -X POST http://[host]:[port]/vital-core-iot-data-adapter-ppi/ppi/system/status

Get sensor status.

	curl -H "Content-Type: application/json" -X POST -d "{ \"id\": [ \"http://[host]:[port]/vital-core-iot-data-adapter-ppi/sensor/monitoring\" ] }" http://[host]:[port]/vital-core-iot-data-adapter-ppi/ppi/system/sensor/status

Get sensor data.

	curl -H "Content-Type: application/json" -X POST -d "{ \"sensor\": [ \"http://[host]:[port]/vital-core-iot-data-adapter-ppi/sensor/monitoring\" ], \"property\": \"http://vital-iot.eu/ontology/ns/OperationalState\" }" http://[host]:[port]/vital-core-iot-data-adapter-ppi/ppi/system/measurement

#### Notes

The property injection is based on the [CDI injecting properties](https://github.com/ChrisRitchie/CDI-injecting-properties) project at GitHub.

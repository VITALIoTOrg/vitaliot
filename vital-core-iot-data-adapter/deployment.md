Send the war.

	scp iot-data-adapter.war lenis.aggelos@5.79.79.172:/var/tmp

Deploy the war.

	printf 'connect https-remoting://localhost:9993\nconnect https-remoting://localhost:9993\nmanagewildfly\nW#mat.31\ndeploy --force "/var/tmp/iot-data-adapter.war"\n' | /opt/wildfly/bin/jboss-cli.sh
	
Re-create the IoT Data Adapter index.

	curl -X DELETE http://localhost:9200/iot-data-adapter
	curl -X PUT http://localhost:9200/iot-data-adapter

	curl -X DELETE http://vital-integration.atosresearch.eu:9200/iot-data-adapter
	curl -X PUT http://vital-integration.atosresearch.eu:9200/iot-data-adapter

Re-create the DMS index.

	curl -X DELETE http://localhost:9200/dms
	curl -X PUT http://localhost:9200/dms
	
	curl -X DELETE http://vital-integration.atosresearch.eu:9200/dms
	curl -X PUT http://vital-integration.atosresearch.eu:9200/dms
	
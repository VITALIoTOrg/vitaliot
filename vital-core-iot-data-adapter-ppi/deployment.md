	scp target/vital-core-iot-data-adapter-ppi.war AIT@vital-integration.atosresearch.eu:/var/tmp

	printf 'connect https-remoting://localhost:9993\nconnect https-remoting://localhost:9993\nAIT\nA#gvt.23\ndeploy --force "/var/tmp/vital-core-iot-data-adapter-ppi.war"\n' | /opt/wildfly/bin/jboss-cli.sh

Send the war.

	scp iot-data-adapter.war AIT@vital-integration.atosresearch.eu:/var/tmp

Deploy the war.

	printf 'connect https-remoting://localhost:9993\nconnect https-remoting://localhost:9993\nAIT\nA#gvt.23\ndeploy --force "/var/tmp/iot-data-adapter.war"\n' | /opt/wildfly/bin/jboss-cli.sh

	scp iot-data-adapter-ppi.war lenis.aggelos@5.79.79.172:/var/tmp

	printf 'connect https-remoting://localhost:9993\nconnect https-remoting://localhost:9993\nmanagewildfly\nW#mat.31\ndeploy --force "/var/tmp/iot-data-adapter-ppi.war"\n' | /opt/wildfly/bin/jboss-cli.sh
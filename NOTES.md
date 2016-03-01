#####ABOUT

https://git-scm.com/book/en/v2/Git-Tools-Submodules

#####ADD

	git submodule add git@gitlab.atosresearch.eu:vital-iot/vital-core-discovery.git

	git submodule add git@gitlab.atosresearch.eu:vital-iot/vital-core-filtering.git

	git submodule add git@gitlab.atosresearch.eu:vital-iot/vital-core-security-adapter.git
	git submodule add git@gitlab.atosresearch.eu:vital-iot/vital-core-security-gateway.git

	git submodule add git@gitlab.atosresearch.eu:vital-iot/vital-core-orchestrator.git

	git submodule add git@gitlab.atosresearch.eu:vital-iot/vital-core-dms.git

	git submodule add git@gitlab.atosresearch.eu:vital-iot/vital-core-iot-data-adapter-ppi.git
	git submodule add git@gitlab.atosresearch.eu:vital-iot/vital-core-iot-data-adapter.git

	git submodule add git@gitlab.atosresearch.eu:vital-iot/vital-core-cep.git

	git submodule add git@gitlab.atosresearch.eu:vital-iot/vital-core-security-snmp-exposer.git

#####UPDATE

	git submodule update --remote

#####BUILD

	git clone git@gitlab.atosresearch.eu:vital-iot/vital-iot.git
	cd vital-iot
	git submodule init
	git submodule update
	mvn clean package wildfly:deploy

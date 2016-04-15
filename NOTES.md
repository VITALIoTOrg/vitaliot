##### ABOUT

https://git-scm.com/book/en/v2/Git-Tools-Submodules

##### ADD modules (already done)

	git submodule add http://gitlab.atosresearch.eu/vital-iot/vital-core-discovery.git

	git submodule add http://gitlab.atosresearch.eu/vital-iot/vital-core-filtering.git

	git submodule add http://gitlab.atosresearch.eu/vital-iot/vital-core-security-adapter.git
	git submodule add http://gitlab.atosresearch.eu/vital-iot/vital-core-security-gateway.git

	git submodule add http://gitlab.atosresearch.eu/vital-iot/vital-core-orchestrator.git

	git submodule add http://gitlab.atosresearch.eu/vital-iot/vital-core-dms.git

	git submodule add http://gitlab.atosresearch.eu/vital-iot/vital-core-iot-data-adapter-ppi.git
	git submodule add http://gitlab.atosresearch.eu/vital-iot/vital-core-iot-data-adapter.git

	git submodule add http://gitlab.atosresearch.eu/ari/vital-core-cep.git

	git submodule add http://gitlab.atosresearch.eu/vital-iot/vital-core-security-snmp-exposer.git

##### UPDATE before doing changes:

	# This will pull latest commits from "master" branch of each module
	git submodule update --remote --recursive

	# This will checkout master branch for each module
    git submodule foreach --recursive git checkout master
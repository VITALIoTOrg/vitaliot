#!/usr/bin/env bash

curl -XDELETE '127.0.0.1:9200/vital-orchestrator?pretty'
curl -XPUT '127.0.0.1:9200/vital-orchestrator?pretty'
curl -XPUT 'localhost:9200/vital-orchestrator/_mapping/system/?pretty' -d @mappings/system-mapping.json
curl -XPUT 'localhost:9200/vital-orchestrator/_mapping/sensor/?pretty' -d @mappings/sensor-mapping.json
curl -XPUT 'localhost:9200/vital-orchestrator/_mapping/service/?pretty' -d @mappings/service-mapping.json

curl -XPUT 'localhost:9200/vital-orchestrator/CONFIGURATION/1?pretty=true' -d '@configuration.json'


#curl -XDELETE '5.79.79.172:9200/vital-orchestrator?pretty'
#curl -XPUT '5.79.79.172:9200/vital-orchestrator?pretty'
#curl -XPUT '5.79.79.172:9200/vital-orchestrator/_mapping/system/?pretty' -d @mappings/system-mapping.json
#curl -XPUT '5.79.79.172:9200/vital-orchestrator/_mapping/sensor/?pretty' -d @mappings/sensor-mapping.json
#curl -XPUT '5.79.79.172:9200/vital-orchestrator/_mapping/service/?pretty' -d @mappings/service-mapping.json
#
#curl -XPUT '5.79.79.172:9200/vital-orchestrator/CONFIGURATION/1?pretty=true' -d '@configuration.json'
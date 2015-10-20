#!/usr/bin/env bash

# Localhost Management
curl -XDELETE '127.0.0.1:9200/vital-management?pretty'
curl -XPUT '127.0.0.1:9200/vital-management?pretty'
curl -XPUT 'localhost:9200/vital-management/_mapping/system/?pretty' -d @mappings/system-mapping.json
curl -XPUT 'localhost:9200/vital-management/_mapping/sensor/?pretty' -d @mappings/sensor-mapping.json
curl -XPUT 'localhost:9200/vital-management/_mapping/service/?pretty' -d @mappings/service-mapping.json
curl -XPUT 'localhost:9200/vital-management/_mapping/measurement/?pretty' -d @mappings/measurement-mapping.json

curl -XPUT 'localhost:9200/vital-management/configuration/1?pretty=true' -d '@configuration.json'

# Vital-Integration Management
#curl -XDELETE 'vital-integration.atosresearch.eu:9200/vital-management?pretty'
#curl -XPUT 'vital-integration.atosresearch.eu:9200/vital-management?pretty'
#curl -XPUT 'vital-integration.atosresearch.eu:9200/vital-management/_mapping/system/?pretty' -d @mappings/system-mapping.json
#curl -XPUT 'vital-integration.atosresearch.eu:9200/vital-management/_mapping/sensor/?pretty' -d @mappings/sensor-mapping.json
#curl -XPUT 'vital-integration.atosresearch.eu:9200/vital-management/_mapping/service/?pretty' -d @mappings/service-mapping.json
#curl -XPUT 'vital-integration.atosresearch.eu:9200/vital-management/_mapping/measurement/?pretty' -d @mappings/measurement-mapping.json
#
#curl -XPUT 'vital-integration.atosresearch.eu:9200/vital-management/configuration/1?pretty=true' -d '@configuration.json'

# Vital-Integration DMS
#curl -XDELETE 'vital-integration.atosresearch.eu:9200/vital-dms?pretty'
#curl -XPUT 'vital-integration.atosresearch.eu:9200/vital-dms?pretty'
#curl -XPUT 'vital-integration.atosresearch.eu:9200/vital-dms/_mapping/system/?pretty' -d @mappings/system-mapping.json
#curl -XPUT 'vital-integration.atosresearch.eu:9200/vital-dms/_mapping/sensor/?pretty' -d @mappings/sensor-mapping.json
#curl -XPUT 'vital-integration.atosresearch.eu:9200/vital-dms/_mapping/service/?pretty' -d @mappings/service-mapping.json
#curl -XPUT 'vital-integration.atosresearch.eu:9200/vital-dms/_mapping/measurement/?pretty' -d @mappings/measurement-mapping.json

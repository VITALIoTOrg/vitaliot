#!/usr/bin/env bash

curl -XPUT 'vital-integration.atosresearch.eu:9200/vital-orchestrator/CONFIGURATION/1?pretty=true' -d '@configuration.json'
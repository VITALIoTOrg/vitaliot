#!/usr/bin/env bash

mongo vital-orchestrator --eval "db.CONFIGURATION.remove({})"
mongoimport --db vital-orchestrator --collection CONFIGURATION --file ./configuration.json
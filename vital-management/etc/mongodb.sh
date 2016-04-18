#!/usr/bin/env bash

mongo vital-management --eval "db.CONFIGURATION.remove({})";

mongoimport --db vital-management --collection CONFIGURATION --file ./configuration.json
#!/usr/bin/env bash

mongo vital-management --eval "db.SENSOR.remove({})";
mongo vital-management --eval "db.SERVICE.remove({})";
mongo vital-management --eval "db.SYSTEM.remove({})";
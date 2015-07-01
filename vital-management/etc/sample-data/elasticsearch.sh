#!/usr/bin/env bash

curl -XDELETE 'localhost:9200/vital-management?pretty'

curl -XPUT 'localhost:9200/vital-management?pretty'
#define mappings for every type
curl -XPUT 'localhost:9200/vital-management/_mapping/sensor/?pretty' -d @mappings/sensor-mapping.json
curl -XPUT 'localhost:9200/vital-management/_mapping/monitoring-sensor/?pretty' -d @mappings/monitoring-sensor-mapping.json
curl -XPUT 'localhost:9200/vital-management/_mapping/system/?pretty' -d @mappings/system-mapping.json
curl -XPUT 'localhost:9200/vital-management/_mapping/observation/?pretty' -d @mappings/observation-mapping.json

#index sample documents: systems
#curl -XPUT 'localhost:9200/vital-management/system/http:%2F%2Fwww.example.com%2Fsystem%2F1' -d @sample-data/system-icomanager.jsonld
#curl -XPUT 'localhost:9200/vital-management/system/http:%2F%2Fwww.example.com%2Fsystem%2F2' -d @sample-data/system-observationmanager.jsonld
#curl -XPUT 'localhost:9200/vital-management/system/http:%2F%2Fwww.example.com%2Fsystem%2F3' -d @sample-data/system-servicediscovery.jsonld
#curl -XPUT 'localhost:9200/vital-management/system/http:%2F%2Fwww.example.com%2Fsystem%2F4' -d @sample-data/system-managementservice.jsonld

#sensors
#curl -XPUT 'localhost:9200/vital-management/sensor/http:%2F%2Fwww.example.com%2Fico%2F1' -d @sample-data/sensor-temp1.jsonld
#curl -XPUT 'localhost:9200/vital-management/sensor/http:%2F%2Fwww.example.com%2Fico%2F2' -d @sample-data/sensor-temp2.jsonld
#curl -XPUT 'localhost:9200/vital-management/sensor/http:%2F%2Fwww.example.com%2Fico%2F3' -d @sample-data/sensor-traffic1.jsonld
#curl -XPUT 'localhost:9200/vital-management/sensor/http:%2F%2Fwww.example.com%2Fico%2F4' -d @sample-data/sensor-traffic2.jsonld
#curl -XPUT 'localhost:9200/vital-management/sensor/http:%2F%2Fwww.example.com%2Fico%2F5' -d @sample-data/sensor-traffic3.jsonld
#curl -XPUT 'localhost:9200/vital-management/sensor/http:%2F%2Fwww.example.com%2Fico%2F6' -d @sample-data/sensor-traffic4.jsonld
#curl -XPUT 'localhost:9200/vital-management/monitoring-sensor/http:%2F%2Fwww.example.com%2Fico%2F1232%2Fperformance%2F75676' -d @sample-data/sensor-performance.jsonld

#observation templates
#curl -XPUT 'localhost:9200/vital-management/observation/http:%2F%2Fwww.example.com%2Fico%2F1%2Fobservation%2F1' -d @sample-data/observation-temp1.jsonld
#curl -XPUT 'localhost:9200/vital-management/observation/http:%2F%2Fwww.example.com%2Fico%2F2%2Fobservation%2F1' -d @sample-data/observation-temp2.jsonld
#curl -XPUT 'localhost:9200/vital-management/observation/http:%2F%2Flocalhost:8080%2Fservice%2Fvital2_I_TrS_1%2Fproperty%2FSpeed' -d @sample-data/observation-speed1.jsonld
#curl -XPUT 'localhost:9200/vital-management/observation/http:%2F%2Flocalhost:8080%2Fservice%2Fvital2_I_TrS_2%2Fproperty%2FSpeed' -d @sample-data/observation-speed2.jsonld
#curl -XPUT 'localhost:9200/vital-management/observation/http:%2F%2Flocalhost:8080%2Fservice%2Fvital2_I_TrS_3%2Fproperty%2FSpeed' -d @sample-data/observation-speed3.jsonld
#curl -XPUT 'localhost:9200/vital-management/observation/http:%2F%2Flocalhost:8080%2Fservice%2Fvital2_I_TrS_4%2Fproperty%2FSpeed' -d @sample-data/observation-speed4.jsonld
#curl -XPUT 'localhost:9200/vital-management/observation/http:%2F%2Flocalhost:8080%2Fservice%2Fvital2_I_TrS_1%2Fproperty%2FColor' -d @sample-data/observation-color1.jsonld
#curl -XPUT 'localhost:9200/vital-management/observation/http:%2F%2Flocalhost:8080%2Fservice%2Fvital2_I_TrS_2%2Fproperty%2FColor' -d @sample-data/observation-color2.jsonld
#curl -XPUT 'localhost:9200/vital-management/observation/http:%2F%2Flocalhost:8080%2Fservice%2Fvital2_I_TrS_3%2Fproperty%2FColor' -d @sample-data/observation-color3.jsonld
#curl -XPUT 'localhost:9200/vital-management/observation/http:%2F%2Flocalhost:8080%2Fservice%2Fvital2_I_TrS_4%2Fproperty%2FColor' -d @sample-data/observation-color4.jsonld
#curl -XPUT 'localhost:9200/vital-management/observation/http:%2F%2Flocalhost:8080%2Fservice%2Fvital2_I_TrS_3%2Fproperty%2FReverseSpeed' -d @sample-data/observation-reversespeed3.jsonld
#curl -XPUT 'localhost:9200/vital-management/observation/http:%2F%2Flocalhost:8080%2Fservice%2Fvital2_I_TrS_4%2Fproperty%2FReverseSpeed' -d @sample-data/observation-reversespeed4.jsonld
#curl -XPUT 'localhost:9200/vital-management/observation/http:%2F%2Flocalhost:8080%2Fservice%2Fvital2_I_TrS_3%2Fproperty%2FReverseColor' -d @sample-data/observation-reversecolor3.jsonld
#curl -XPUT 'localhost:9200/vital-management/observation/http:%2F%2Flocalhost:8080%2Fservice%2Fvital2_I_TrS_4%2Fproperty%2FReverseColor' -d @sample-data/observation-reversecolor4.jsonld
#curl -XPUT 'localhost:9200/vital-management/observation/http:%2F%2Flocalhost:8080%2Fico%2F1232%2Fperformance%2F75676%2Fobsvn%2F1' -d @sample-data/observation-errors.jsonld
#curl -XPUT 'localhost:9200/vital-management/observation/http:%2F%2Flocalhost:8080%2Fico%2F1232%2Fperformance%2F75676%2Fobsvn%2F2' -d @sample-data/observation-maxrequests.jsonld
#curl -XPUT 'localhost:9200/vital-management/observation/http:%2F%2Flocalhost:8080%2Fico%2F1232%2Fperformance%2F75676%2Fobsvn%2F3' -d @sample-data/observation-memavailable.jsonld
#curl -XPUT 'localhost:9200/vital-management/observation/http:%2F%2Flocalhost:8080%2Fico%2F1232%2Fperformance%2F75676%2Fobsvn%2F4' -d @sample-data/observation-memused.jsonld
#curl -XPUT 'localhost:9200/vital-management/observation/http:%2F%2Flocalhost:8080%2Fico%2F1232%2Fperformance%2F75676%2Fobsvn%2F5' -d @sample-data/observation-pendingrequests.jsonld
#curl -XPUT 'localhost:9200/vital-management/observation/http:%2F%2Flocalhost:8080%2Fico%2F1232%2Fperformance%2F75676%2Fobsvn%2F6' -d @sample-data/observation-servedrequests.jsonld
#curl -XPUT 'localhost:9200/vital-management/observation/http:%2F%2Flocalhost:8080%2Fico%2F1232%2Fperformance%2F75676%2Fobsvn%2F7' -d @sample-data/observation-systemload.jsonld
#curl -XPUT 'localhost:9200/vital-management/observation/http:%2F%2Flocalhost:8080%2Fico%2F1232%2Fperformance%2F75676%2Fobsvn%2F8' -d @sample-data/observation-uptime.jsonld

curl -XPOST 'localhost:9200/vital-management/observation/' -d @observation-templates/Color.jsonld
curl -XPOST 'localhost:9200/vital-management/observation/' -d @observation-templates/MaxRequests.jsonld
curl -XPOST 'localhost:9200/vital-management/observation/' -d @observation-templates/MemUsed.jsonld
curl -XPOST 'localhost:9200/vital-management/observation/' -d @observation-templates/ReverseColor.jsonld
curl -XPOST 'localhost:9200/vital-management/observation/' -d @observation-templates/Speed.jsonld
curl -XPOST 'localhost:9200/vital-management/observation/' -d @observation-templates/Temperature.jsonld
curl -XPOST 'localhost:9200/vital-management/observation/' -d @observation-templates/Errors.jsonld
curl -XPOST 'localhost:9200/vital-management/observation/' -d @observation-templates/MemAvailable.jsonld
curl -XPOST 'localhost:9200/vital-management/observation/' -d @observation-templates/PendingRequests.jsonld
curl -XPOST 'localhost:9200/vital-management/observation/' -d @observation-templates/ServedRequests.jsonld
curl -XPOST 'localhost:9200/vital-management/observation/' -d @observation-templates/SystemLoad.jsonld
curl -XPOST 'localhost:9200/vital-management/observation/' -d @observation-templates/Uptime.jsonld
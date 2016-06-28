/**
 * Created by anglen on 11/3/15.
 */
'use strict';

//1. FIRST INPUT;
//{
//    "lat": 51.539011,
//    "lng": -0.142555,
//    "type1" : "AvailableBikes",
//    "type2" : "Speed"
//}

//Operation1, 2: Get List of Sensors measuring <type>

function execute(input) {
    input.sensorList = sensorAdapter.searchByObservationType('http://vital-iot.eu/ontology/ns/' + input.type1);
    return input;
}

function execute(input) {
    input.sensorList = sensorAdapter.searchByObservationType('http://vital-iot.eu/ontology/ns/' + input.type2);
    return input;
}

//Operation2: FindNearestSensor

//{
//    "lat": 51.539011,
//    "lng": -0.142555,
//    "sensorList": []
//}

function execute(input) {
    var i, tmp,
        minDistance = Number.MAX_VALUE,
        sensor,
        result1, result2;

    for (i = 0; i < input.sensorList.length; i++) {
        sensor = input.sensorList[i];
        tmp = distance(
            input.lat,
            input.lng,
            sensor['http://vital-iot.eu/ontology/ns/hasLastKnownLocation']['http://www.w3.org/2003/01/geo/wgs84_pos#lat'],
            sensor['http://vital-iot.eu/ontology/ns/hasLastKnownLocation']['http://www.w3.org/2003/01/geo/wgs84_pos#lon']
        );
        if (tmp < minDistance) {
            result2 = result1;
            result1 = sensor;
        }
    }

    return {
        type: input.type,
        sensor1: result1,
        sensor2: result2
    };

    function distance(lat1, lon1, lat2, lon2) {
        var R = 6371; // Radius of the earth in km
        var dLat = deg2rad(lat2 - lat1);  // deg2rad below
        var dLon = deg2rad(lon2 - lon1);
        var a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
            Math.sin(dLon / 2) * Math.sin(dLon / 2);
        var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        var d = R * c; // Distance in km
        return d;

        function deg2rad(deg) {
            return deg * (Math.PI / 180);
        }
    }
}

//Operations 3,4: Get Last Observation from Sensors
//{
//  "sensor": {
//    "@id": ""
//  }
//}

function execute(input) {
    var observation;
    observation = observationAdapter.get(input.sensor1['@id'], 'http://vital-iot.eu/ontology/ns/' + input.type);
    return {
        measurementDate: observation['http://purl.oclc.org/NET/ssnx/ssn#observationResultTime']
            ['http://www.w3.org/2006/time#inXSDDateTime']
            ['@value'],
        measurementValue: observation['http://purl.oclc.org/NET/ssnx/ssn#observationResult']
            ['http://purl.oclc.org/NET/ssnx/ssn#hasValue']
            ['http://vital-iot.eu/ontology/ns/value']
    };
}

function execute(input) {
    var observation;
    observation = observationAdapter.get(input.sensor2['@id'], 'http://vital-iot.eu/ontology/ns/' + input.type);
    return {
        measurementDate: observation['http://purl.oclc.org/NET/ssnx/ssn#observationResultTime']
            ['http://www.w3.org/2006/time#inXSDDateTime']
            ['@value'],
        measurementValue: observation['http://purl.oclc.org/NET/ssnx/ssn#observationResult']
            ['http://purl.oclc.org/NET/ssnx/ssn#hasValue']
            ['http://vital-iot.eu/ontology/ns/value']
    };
}


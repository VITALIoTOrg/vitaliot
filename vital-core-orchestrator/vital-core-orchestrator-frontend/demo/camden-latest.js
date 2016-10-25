'use strict';

//Operation1: Get List of Sensors measuring <type>

function execute(input) {
    var output = {
        lat: input.input.lat,
        lng: input.input.lng,
        atDate: input.input.atDate,
        sensorList: sensorAdapter.searchByObservationType('http://vital-iot.eu/ontology/ns/AvailableBikes')
    };
    return output;
}

//Operation2: FindNearestSensor
function execute(input) {
    var i, tmp,
        minDistance = Number.MAX_VALUE,
        sensor,
        result;

    for (i = 0; i < input.operation0.sensorList.length; i++) {
        sensor = input.operation0.sensorList[i];
        tmp = distance(
            input.operation0.lat,
            input.operation0.lng,
            sensor['http://vital-iot.eu/ontology/ns/hasLastKnownLocation']['http://www.w3.org/2003/01/geo/wgs84_pos#lat'],
            sensor['http://vital-iot.eu/ontology/ns/hasLastKnownLocation']['http://www.w3.org/2003/01/geo/wgs84_pos#long'] || sensor['http://vital-iot.eu/ontology/ns/hasLastKnownLocation']['http://www.w3.org/2003/01/geo/wgs84_pos#lon']
        );
        if (tmp < minDistance) {
            minDistance = tmp;
            result = sensor;
        }
    }

    var output = {
        lat: input.operation0.lat,
        lng: input.operation0.lng,
        atDate: input.operation0.atDate,
        sensor: result
    };
    return output;

    // Distance function
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
//Operation 3: Get Last Observation from Sensor
//{
//  "sensor": {
//    "@id": ""
//  }
//}

function execute(input) {
    var observation;
    observation = observationAdapter.get(input.operation1.sensor['@id'], "http://vital-iot.eu/ontology/ns/AvailableBikes");
    return {
        measurementDate: observation['http://purl.oclc.org/NET/ssnx/ssn#observationResultTime']
            ['http://www.w3.org/2006/time#inXSDDateTime']
            ['@value'],
        measurementValue: observation['http://purl.oclc.org/NET/ssnx/ssn#observationResult']
            ['http://purl.oclc.org/NET/ssnx/ssn#hasValue']
            ['http://vital-iot.eu/ontology/ns/value']
    };
}

'use strict';

//Operation1, 2: Get List of Sensors measuring <type>

function execute(data) {
    var type = data.input.type1;
    data.sensorList = sensorAdapter.searchByObservationType('http://vital-iot.eu/ontology/ns/' + type);
    return data;
}

function execute(data) {
    var type = data.input.type2;
    data.sensorList = sensorAdapter.searchByObservationType('http://vital-iot.eu/ontology/ns/' + type);
    return data;
}

//Operation2,3: FindNearestSensor

function execute(input) {
    var i, tmp,
        minDistance = Number.MAX_VALUE,
        sensor = null,
        result = null;
    var sensorList = input.operation0.sensorList;

    for (i = 0; i < sensorList.length; i++) {
        sensor = sensorList[i];
        tmp = distance(
            input.lat,
            input.lng,
            sensor['http://vital-iot.eu/ontology/ns/hasLastKnownLocation']['http://www.w3.org/2003/01/geo/wgs84_pos#lat'],
            sensor['http://vital-iot.eu/ontology/ns/hasLastKnownLocation']['http://www.w3.org/2003/01/geo/wgs84_pos#lon']
        );
        if (tmp < minDistance) {
            result = sensor;
        }
    }

    input.closestSensor = result;
    return input;

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

function execute(input) {
    var i, tmp,
        minDistance = Number.MAX_VALUE,
        sensor = null,
        result = null;
    var sensorList = input.operation1.sensorList;

    for (i = 0; i < sensorList.length; i++) {
        sensor = sensorList[i];
        tmp = distance(
            input.lat,
            input.lng,
            sensor['http://vital-iot.eu/ontology/ns/hasLastKnownLocation']['http://www.w3.org/2003/01/geo/wgs84_pos#lat'],
            sensor['http://vital-iot.eu/ontology/ns/hasLastKnownLocation']['http://www.w3.org/2003/01/geo/wgs84_pos#lon']
        );
        if (tmp < minDistance) {
            result = sensor;
        }
    }

    input.closestSensor = result;
    return input;

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

function execute(input) {
    var sensor = input.operation2.closestSensor;
    var type = input.operation2.operation0.input.type1;
    var observation = observationAdapter.get(sensor['@id'], 'http://vital-iot.eu/ontology/ns/' + type);
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
    var sensor = input.operation3.closestSensor;
    var type = input.operation3.operation1.input.type2;
    var observation = observationAdapter.get(sensor['@id'], 'http://vital-iot.eu/ontology/ns/' + type);
    return {
        measurementDate: observation['http://purl.oclc.org/NET/ssnx/ssn#observationResultTime']
            ['http://www.w3.org/2006/time#inXSDDateTime']
            ['@value'],
        measurementValue: observation['http://purl.oclc.org/NET/ssnx/ssn#observationResult']
            ['http://purl.oclc.org/NET/ssnx/ssn#hasValue']
            ['http://vital-iot.eu/ontology/ns/value']
    };
}

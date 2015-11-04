/**
 * Created by anglen on 11/3/15.
 */
'use strict';

// Camden - Get Footfall prediction for area in the future
//1. FIRST INPUT;
//{
//    "lat": 51.539011,
//    "lng": -0.142555,
//    "atDate" : "2015-06-14T19:37:22Z"
//}

//Operation1: Get List of Sensors measuring footfall

function execute(input) {
    input.sensorList = sensorAdapter.searchByObservationType('http://vital-iot.eu/ontology/ns/Footfall');
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
        result;

    for (i = 0; i < input.sensorList.length; i++) {
        sensor = input.sensorList[i];
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

    input.sensor = sensor;
    return input;

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


//Operation 3: Get All Observations from Sensor
function execute(input) {
    var observationList = observationAdapter.fetchAllBySensorAndType(input.sensor['@id'], 'http://vital-iot.eu/ontology/ns/Footfall');

    input.observationList = observationList;
    return input;
}


// Operation4: Run prediction on results
function execute(input) {
    var values = [];
    for (var i = 0; i < input.observationList.length; i++) {
        var observation = input.observationList[i];
        var value = observation
            ['http://purl.oclc.org/NET/ssnx/ssn#observationResult']
            ['http://purl.oclc.org/NET/ssnx/ssn#hasValue']
            ['http://vital-iot.eu/ontology/ns/value'];
        values.push(value);
    }

    // Do prediction here:
    //r.parseEvalQ('library(forecast)');
    //r.parseEvalQ('value <- c(' + values.join(', ') + ')');
    //r.parseEvalQ('sensor<-ts(value,frequency=24)');
    //r.parseEvalQ('fit <- auto.arima(sensor)');
    //r.parseEvalQ('LH.pred<-predict(fit,n.ahead=1)');
    //var prediction = r.parseEval("LH.pred$pred");
    var prediction = 13;

    // ------------------

    return prediction;
}

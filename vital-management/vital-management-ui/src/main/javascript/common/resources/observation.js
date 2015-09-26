'use strict';
angular.module('common.resources.observation', [])

    .factory('observationResource', [
        '$http', '$q', 'API_PATH',
        function($http, $q, API_PATH) {
            // INIT DATA
            var observationDefaults = {
                '@context': 'http://vital-iot.org/contexts/measurement.jsonld',
                'uri': null,
                'type': null,
                'ssn:observationProperty': {
                    'type': 'http://reply.eu/vital/Speed'
                },
                'ssn:observationResultTime': {
                    'inXSDDateTime': '2014-08-20T16:47:32+01:00'
                },
                'dul:hasLocation': {
                    'type': 'geo:Point',
                    'geo:lat': '55.701',
                    'geo:long': '12.552',
                    'geo:alt': '4.33'
                },
                'ssn:observationQuality': {
                    'ssn:hasMeasurementProperty': {
                        'type': 'Reliability',
                        'hasValue': 'HighReliability'
                    }
                },
                'ssn:observationResult': {
                    'type': 'ssn:SensorOutput',
                    'ssn:hasValue': {
                        'type': 'ssn:ObservationValue',
                        'value': '85.0',
                        'qudt:unit': 'qudt:Kmh'
                    }
                }
            };

            var defaultQuery = {
                //'@context': 'http://vital-iot.org/contexts/query.jsonld',
                'sensor': null,
                'property': null
            };

            var service = {

                fetch: function(sensor_uri, observation_uri) {
                    defaultQuery.sensor = sensor_uri;
                    defaultQuery.property = observation_uri;
                    defaultQuery.property = 'http://vital-iot.eu/ontology/ns/Speed';
                    return $http.post(API_PATH + '/observation', defaultQuery)
                        .then(function(response) {
                            return response.data;
                        });
                },

                fetchPerformanceMetric: function(sensor_uri, observation_property) {
                    defaultQuery.sensor = sensor_uri;
                    defaultQuery.property = observation_property;
                    return $http.post(API_PATH + '/observation/performance', defaultQuery)
                        .then(function(response) {
                            return response.data;
                        });
                }
            };

            // return service
            return service;
        }
    ]);

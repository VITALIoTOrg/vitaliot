'use strict';
angular.module('common.resources.sensor', [])

    .factory('sensorResource', [
        '$http', '$q', 'API_PATH',
        function ($http, $q, API_PATH) {
            function randomInt(min, max) {
                return Math.floor(Math.random() * (max - min)) + min;
            }

            function randomBoolean() {
                return Math.random() < 0.5;
            }

            var sensorDefaults =
            {
                '@id': 'http://104.131.128.70:8080/istanbul-traffic/sensor/36-F',
                '@type': 'http://vital-iot.eu/ontology/ns/VitalSensor',
                'http://www.w3.org/2000/01/rdf-schema#comment': '',
                'http://www.w3.org/2000/01/rdf-schema#label': '',
                'http://purl.oclc.org/NET/ssnx/ssn#observes': [],
                'http://vital-iot.eu/ontology/ns/hasLastKnownLocation': {
                    '@type': 'http://www.w3.org/2003/01/geo/wgs84_pos#Point',
                    'http://www.w3.org/2003/01/geo/wgs84_pos#lat': null,
                    'http://www.w3.org/2003/01/geo/wgs84_pos#lon': null
                },
                'http://vital-iot.eu/ontology/ns/status': {
                    '@id': 'http://vital-iot.eu/ontology/ns/Unavailable'
                },
                'system': '',

                //These won't be provided from backend at this point
                'hasMovementPattern': {
                    'type': 'Stationary' // {Stationary|Mobile|Predicted}
                },
                'hasNetworkConnection': {
                    'hasStability': {
                        'type': 'Continuous'
                    },
                    'hasNetworkSupport': {
                        'net:connectedNetworks': {
                            'type': 'net:WiredNetwork'
                        }
                    }
                },
                'deviceHardware': {
                    'hard:status': 'hard:HardwareStatus_ON',
                    'hard:builtInMemory': {
                        'size': 131072
                    },
                    'hard:cpu': {
                        'type': 'hard:CPU',
                        'maxCpuFrequency': 10
                    }
                }
            };

            var defaultQuery = {
                '@context': 'http://vital-iot.org/contexts/query.jsonld'
            };

            // The public API of the service
            var service = {

                fetchList: function () {
                    return $http.get(API_PATH + '/sensor')
                        .then(function (response) {
                            var sensors = [];
                            angular.forEach(response.data, function (sensor) {
                                sensors.push(sensor);
                            });
                            return sensors;
                        });
                },

                search: function (query) {
                    return $http.post(API_PATH + '/sensor/search', query)
                        .then(function (response) {
                            var sensors = [];
                            angular.forEach(response.data, function (sensor) {
                                sensors.push(sensor);
                            });
                            return sensors;
                        });
                },

                fetch: function (sensor_id) {
                    defaultQuery.sensor = sensor_id;
                    return $http.post(API_PATH + '/sensor/metadata', defaultQuery)
                        .then(function (response) {
                            return angular.extend(sensorDefaults, response.data);
                        });
                },

                fetchStatus: function (sensor_id) {
                    defaultQuery.sensor = sensor_id;
                    return $http.post(API_PATH + '/sensor/metadata/status', defaultQuery)
                        .then(function (response) {
                            return response.data;
                        });
                }
            };

            // return service
            return service;
        }
    ]);

'use strict';
angular.module('common.resources.system', [])
    .factory('systemResource', [
        '$http', '$q', 'API_PATH',
        function ($http, $q, API_PATH) {
            var systemDefaults = {
                '@id': '',
                '@type': 'http://vital-iot.eu/ontology/ns/IotSystem',
                'http://www.w3.org/2000/01/rdf-schema#comment': '',
                'http://www.w3.org/2000/01/rdf-schema#label': '',
                'http://vital-iot.eu/ontology/ns/operator': {
                    '@id': ''
                },
                'http://vital-iot.eu/ontology/ns/providesService': [],
                'http://vital-iot.eu/ontology/ns/managesSensor': [],
                // Additional elements to show some content:
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
                    return $http.get(API_PATH + '/system')
                        .then(function (response) {
                            var systems = [];
                            angular.forEach(response.data, function (system) {
                                systems.push(system);
                            });
                            return systems;
                        });
                },

                search: function (query) {
                    return $http.post(API_PATH + '/system/search', query)
                        .then(function (response) {
                            var systems = [];
                            angular.forEach(response.data, function (system) {
                                systems.push(system);
                            });
                            return systems;
                        });
                },

                fetch: function (system_id) {
                    defaultQuery.system = system_id;
                    return $http.post(API_PATH + '/system/metadata', defaultQuery)
                        .then(function (response) {
                            return angular.extend(angular.copy(systemDefaults), response.data);
                        });
                },

                fetchStatus: function (system_id) {
                    defaultQuery.system = system_id;
                    return $http.post(API_PATH + '/system/metadata/status', defaultQuery)
                        .then(function (response) {
                            return response.data;
                        });
                }

            };

            return service;
        }
    ]);

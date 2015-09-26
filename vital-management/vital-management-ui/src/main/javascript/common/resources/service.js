'use strict';
angular.module('common.resources.service', [])
    .factory('serviceResource', [
        '$http', '$q', 'API_PATH',
        function ($http, $q, API_PATH) {
            var defaultQuery = {
                '@context': 'http://vital-iot.org/contexts/query.jsonld'
            };

            // The public API of the service
            var service = {

                fetchList: function () {
                    return $http.get(API_PATH + '/service')
                        .then(function (response) {
                            var systems = [];
                            angular.forEach(response.data, function (system) {
                                systems.push(system);
                            });
                            return systems;
                        });
                },

                fetch: function (service_id) {
                    defaultQuery.service = service_id;
                    return $http.post(API_PATH + '/service/metadata', defaultQuery)
                        .then(function (response) {
                            return response.data;
                        });
                },

                fetchBySystem: function (system_id) {
                    defaultQuery.system = system_id;
                    return $http.post(API_PATH + '/service/metadata', defaultQuery)
                        .then(function (response) {
                            return response.data;
                        });
                }

            };

            return service;
        }
    ]);

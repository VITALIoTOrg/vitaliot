'use strict';
angular.module('common.resources.management', [])

    .factory('managementResource', [
        '$http', '$q', 'API_PATH',
        function($http, $q, API_PATH) {

            var defaultQuery = {
                //'@context': 'http://vital-iot.org/contexts/query.jsonld',
                'sensor': null,
                'property': null
            };

            var service = {

                fetchSupportedPerformanceMetricList: function(systemId) {
                    var query = {
                        system: systemId
                    };
                    return $http.post(API_PATH + '/management/monitoring/supported', query)
                        .then(function(response) {
                            return _.groupBy(response.data, function(metric) {
                                return metric.type;
                            });
                        });
                },

                fetchPerformanceMetric: function(systemId, metricList) {
                    var query = {
                        system: systemId,
                        metric: metricList

                    };
                    return $http.post(API_PATH + '/management/monitoring', query)
                        .then(function(response) {
                            return response.data;
                        });
                },

                fetchConfiguration: function(systemId) {
                    var query = {
                        system: systemId
                    };
                    return $http.post(API_PATH + '/management/configuration', query)
                        .then(function(response) {
                            return response.data;
                        });
                },

                saveConfiguration: function(systemId, configuration) {
                    var query = {
                        system: systemId,
                        configuration: configuration
                    };
                    return $http.post(API_PATH + '/management/configuration/update', query)
                        .then(function(response) {
                            return response.data;
                        });
                }
            };

            // return service
            return service;
        }
    ]);

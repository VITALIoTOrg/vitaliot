'use strict';
angular.module('main.governance.trust', [
    'ngRoute',
    'main.governance.trust.widgets'
])


/************
 ** Config **
 ************/
    .config([
        '$routeProvider', function($routeProvider) {

            $routeProvider.when('/governance/trust', {
                templateUrl: 'main/governance/trust/trust.tpl.html',
                controller: 'TrustController',
                controllerAs: 'tCtrl',
                resolve: {
                    systemList: [
                        'systemResource',
                        function(systemResource) {
                            return systemResource.fetchList();
                        }
                    ]
                }
            });

            $routeProvider.when('/governance/trust/edit', {
                templateUrl: 'main/governance/trust/trust-edit.tpl.html',
                controller: 'TrustEditController',
                controllerAs: 'tCtrl',
                resolve: {
                    systemList: [
                        '$q', 'systemResource', 'managementResource',
                        function($q, systemResource, managementResource) {
                            return systemResource.fetchList()
                                .then(function(systemList) {
                                    var supportedSlaMetricsPromises = [];
                                    _.forEach(systemList, function(system) {
                                        supportedSlaMetricsPromises.push(managementResource.fetchSupportedSlaParameterList(system.id));
                                    });
                                    return $q.all(supportedSlaMetricsPromises)
                                        .then(function(data) {
                                            _.forEach(systemList, function(system, index) {
                                                system.slaParameterMap = data[index];
                                            });
                                            return systemList;
                                        });
                                });
                        }
                    ],
                    trustManager: [
                        'trustResource',
                        function(trustResource) {
                            return trustResource.fetchManager();
                        }
                    ]
                }
            });

            $routeProvider.when('/governance/trust/:uri', {
                templateUrl: 'main/governance/trust/trust-system.tpl.html',
                controller: 'TrustSystemController',
                controllerAs: 'tsCtrl',
                resolve: {
                    system: [
                        '$route', '$filter', 'systemResource',
                        function($route, $filter, systemResource) {
                            var uri = $filter('decodeHistoryComponent')($route.current.params.uri);
                            return systemResource.fetch(uri);
                        }
                    ],
                    supportedMetrics: [
                        '$route', '$filter', 'managementResource',
                        function($route, $filter, managementResource) {
                            var uri = $filter('decodeHistoryComponent')($route.current.params.uri);
                            return managementResource.fetchSupportedSlaParameterList(uri);
                        }
                    ]
                }
            });
        }
    ])

    /******************
     ** Controllers ***
     ******************/

    .controller('TrustController', [
        'trustResource', 'systemList',
        function(trustResource, systemList) {
            var ctrl = this;
            ctrl.systemList = systemList;

            ctrl.stop = function() {
                trustResource.stop()
                    .then(function() {
                        // Do something
                    })
            }
        }
    ])

    .controller('TrustEditController', [
        'trustResource', 'systemList', 'trustManager',
        function(trustResource, systemList, trustManager) {
            var ctrl = this;
            ctrl.systemList = systemList;
            ctrl.trustManager = trustManager;

            ctrl.supportsSlaParameters = function(system) {
                return !_.isEmpty(system.slaParameterMap);
            };

            ctrl.submit = function(ngModelCtrl) {
                trustResource.start(ctrl.trustManager)
                    .then(function() {
                        // Do something
                    });
            }
        }
    ])

    .controller('TrustSystemController', [
        '$scope', '$interval', 'trustResource', 'system', 'supportedMetrics',
        function($scope, $interval, trustResource, system, supportedMetrics) {
            var ctrl = this;
            ctrl.system = system;
            ctrl.supportedMetrics = supportedMetrics;

            ctrl.chart = {
                trustScore: []
            };

            fetchThroughputData();
            // var throughputInterval = $interval(fetchThroughputData, 10000);

            // Actions

            // Watchers, Events
            $scope.$on('destroy', function() {
                $interval.cancel(throughputInterval);
            });

            // Functions

            function fetchThroughputData() {
                trustResource.fetchTrustScore(ctrl.system.id)
                    .then(function(data) {
                        ctrl.chart.trustScore.length = 0;
                        angular.forEach(data, function(score) {
                            ctrl.chart.trustScore.push(score);
                        });
                    })
            }

        }
    ])

    .directive('trustScore', [
        function() {
            return {
                restrict: 'E',
                replace: 'true',
                template: '<div></div>',
                scope: {
                    data: '='
                },
                link: function(scope, element, attrs) {
                    var historyChart = Morris.Line({
                        element: element,
                        data: [],
                        xkey: 'timestamp',
                        ykeys: ['value'],
                        labels: ['Trust'],
                        hideHover: 'auto'
                    });
                    // Init
                    scope.$watchCollection('data', function(scores) {
                        var data = _.map(scores[0], function(score) {
                            return {
                                timestamp: score.timeS['$date'],
                                value: score.trustScore
                            };
                        });
                        historyChart.setData(data);
                    });
                }
            };
        }
    ])

    /***************
     ** Services ***
     ***************/

    .factory('trustResource', [
        '$http', '$q', 'TRUST_API_PATH',
        function($http, $q, TRUST_API_PATH) {

            // The public API of the service
            var service = {

                fetchManager: function() {
                    var trustManager = {
                        "IoTSystems": []
                    };
                    return $q.when(_
                        .chain(trustManager.IoTSystems)
                        .map(function(iotSystem) {
                            return {
                                id: iotSystem.id,
                                sla_params: _.keyBy(iotSystem.sla_params, function(slaParam) {
                                    return slaParam.id;
                                })
                            }
                        })
                        .keyBy(function(iotSystem) {
                            return iotSystem.id
                        })
                        .value()
                    );
                },

                start: function(data) {
                    var iotSystems = _.chain(data)
                        .map(function(systemSLA, systemId) {
                            return {
                                id: systemId,
                                sla_params: _.map(systemSLA.sla_params, function(sla_param, id) {
                                    return {
                                        id: id,
                                        upthreshold: '' + sla_param.upthreshold,
                                        downthreshold: '' + sla_param.downthreshold,
                                        address: '' + sla_param.address
                                    };
                                })
                            }
                        });

                    return $http.post(TRUST_API_PATH + '/startTrustManager', {
                        "IoTSystems": iotSystems
                    }).then(function(response) {
                        return response.data;
                    });
                },

                stop: function() {
                    return $http.delete(TRUST_API_PATH + '/stopTrustManager')
                        .then(function(response) {
                            return response.data;
                        });
                },

                fetchTrustScore: function(systemId) {
                    return $http.post(TRUST_API_PATH + '/getIoTsystemTrustScore', {
                        "IoTSystemID": systemId
                    }).then(function(response) {
                        return response.data;
                    });
                }
            };

            return service;
        }
    ]);

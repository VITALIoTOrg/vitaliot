'use strict';
angular.module('main.governance.trust', [
    'ngRoute'
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
                        'systemResource',
                        function(systemResource) {
                            return systemResource.fetchList();
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
            ctrl.slaParameterList = [
                "http://vital-iot.eu/ontology/ns/UsedMem",
                "http://vital-iot.eu/ontology/ns/AvailableMem",
                "http://vital-iot.eu/ontology/ns/AvailableDisk",
                "http://vital-iot.eu/ontology/ns/SysLoad",
                "http://vital-iot.eu/ontology/ns/ServedRequests",
                "http://vital-iot.eu/ontology/ns/Errors",
                "Http://vital-iot.eu/ontology/ns/SysUptime",
                "http://vital-iot.eu/ontology/ns/PendingRequests"
            ];

            ctrl.submit = function(ngModelCtrl) {
                trustResource.start(ctrl.trustManager)
                    .then(function() {
                        // Do something
                    });
            }
        }
    ])

    .controller('TrustSystemController', [
        '$scope', '$interval', 'trustResource', 'system',
        function($scope, $interval, trustResource, system) {
            var ctrl = this;
            ctrl.system = system;
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
                        angular.forEach(data.IoTSystemScore, function(score) {
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
                        labels: ['Throughput'],
                        hideHover: 'auto'
                    });
                    // Init
                    scope.$watchCollection('data', function(data) {
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
                                    return angular.extend({
                                        id: id
                                    }, sla_param)
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

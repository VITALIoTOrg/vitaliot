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
                        angular.forEach(data, function(datum) {
                            ctrl.chart.trustScore.push(datum);
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

                fetchTrustScore: function(systemId) {
                    return $http.get(TRUST_API_PATH)
                        .then(function(response) {
                            return response.data;
                        });

                }
            };

            return service;
        }
    ]);

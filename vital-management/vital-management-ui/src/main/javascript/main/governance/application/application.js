'use strict';
angular.module('main.governance.application', [
    'ngRoute'
])


/************
 ** Config **
 ************/
    .config(['$routeProvider', function ($routeProvider) {

        $routeProvider.when('/governance/application', {
            templateUrl: 'main/governance/application/application.tpl.html',
            controller: 'ApplicationController',
            controllerAs: 'aCtrl',
            resolve: {
                groupList: [
                    'applicationResource',
                    function (applicationResource) {
                        return applicationResource.fetchGroupList();
                    }
                ]
            }
        });

        $routeProvider.when('/governance/application/access/:groupName', {
            templateUrl: 'main/governance/application/application-access.tpl.html',
            controller: 'ApplicationAccessController',
            controllerAs: 'aaCtrl',
            resolve: {
                systemList: [
                    'applicationResource',
                    function (applicationResource) {
                        return applicationResource.fetchSystemList();
                    }
                ],
                group: [
                    '$route', 'applicationResource',
                    function ($route, applicationResource) {

                        return applicationResource.fetchGroup($route.current.params.groupName);
                    }
                ],
                access: [
                    '$route', 'applicationResource',
                    function ($route, applicationResource) {
                        return applicationResource.fetchAccess($route.current.params.groupName);
                    }
                ]
            }
        });

        $routeProvider.when('/governance/application/sla/:groupName', {
            templateUrl: 'main/governance/application/application-sla.tpl.html',
            controller: 'ApplicationSlaController',
            controllerAs: 'asCtrl',
            resolve: {
                group: [
                    '$route', 'applicationResource',
                    function ($route, applicationResource) {

                        return applicationResource.fetchGroup($route.current.params.groupName);
                    }
                ]
            }
        });
    }])

    /******************
     ** Controllers ***
     ******************/

    .controller('ApplicationController', [
        'applicationResource', 'groupList',
        function (applicationResource, groupList) {
            var ctrl = this;
            ctrl.groupList = groupList;
        }
    ])

    .controller('ApplicationAccessController', [
        'applicationResource', 'systemList', 'group', 'access',
        function (applicationResource, systemList, group, access) {
            var ctrl = this;

            // Map Options
            ctrl.access = extendWithSystems(access, systemList);
            ctrl.systemList = systemList;
            ctrl.group = group;

            // Actions

            ctrl.save = function (ngFormController) {
                applicationResource.updateAccess(ctrl.group.username, ctrl.access)
                    .then(function (newAccess) {
                        ctrl.access = newAccess;
                    });
            };

            // Watchers, Events
            // Functions

            function extendWithSystems(access, systemList) {
                _.forEach(systemList, function (system) {
                    if (_.isUndefined(access[system.id]) || _.isNull(access[system.id])) {
                        access[system.id] = true;
                    }
                });
                return access;
            }
        }
    ])

    .controller('ApplicationSlaController', [
        '$scope', '$interval', 'applicationResource', 'group',
        function ($scope, $interval, applicationResource, group) {
            var ctrl = this;
            ctrl.group = group;
            ctrl.chart = {
                throughput: []
            };

            fetchThroughputData();
            // var throughputInterval = $interval(fetchThroughputData, 10000);

            // Actions

            // Watchers, Events
            $scope.$on('destroy', function () {
                $interval.cancel(throughputInterval);
            });

            // Functions

            function fetchThroughputData() {
                applicationResource.fetchSLAObservations(ctrl.group.username, 'throughput')
                    .then(function (data) {
                        ctrl.chart.throughput.length = 0;
                        angular.forEach(data, function (datum) {
                            ctrl.chart.throughput.push(datum);
                        });
                    })
            }

        }
    ])

    .directive('slaThroughput', [
        function () {
            return {
                restrict: 'E',
                replace: 'true',
                template: '<div></div>',
                scope: {
                    data: '='
                },
                link: function (scope, element, attrs) {
                    var historyChart = Morris.Line({
                        element: element,
                        data: [],
                        xkey: 'timestamp',
                        ykeys: ['value'],
                        labels: ['Throughput'],
                        hideHover: 'auto'
                    });
                    // Init
                    scope.$watchCollection('data', function (data) {
                        historyChart.setData(data);
                    });
                }
            };
        }
    ])

    /***************
     ** Services ***
     ***************/

    .factory('applicationResource', [
        '$http', '$q', 'API_PATH',
        function ($http, $q, API_PATH) {

            // The public API of the service
            var service = {

                fetchSystemList: function () {
                    return $http.get(API_PATH + '/system')
                        .then(function (response) {
                            return response.data;
                        });
                },

                fetchGroupList: function () {
                    var path = API_PATH.replace('/api', '/security') + '/groups';
                    return $http.get(path)
                        .then(function (response) {
                            return response.data.result;
                        });
                },

                fetchGroup: function (groupName) {
                    var path = API_PATH.replace('/api', '/security') + '/group/' + groupName;
                    return $http.get(path)
                        .then(function (response) {
                            return response.data;
                        });
                },

                fetchAccess: function (groupName) {
                    return $http.get(API_PATH + '/governance/access/' + groupName)
                        .then(function (response) {
                            return response.data;
                        });
                },

                updateAccess: function (groupName, access) {
                    return $http.put(API_PATH + '/governance/access/' + groupName, access)
                        .then(function (response) {
                            return response.data;
                        });

                },

                fetchSLAObservations: function (groupName, slaType) {
                    return $http.get(API_PATH + '/governance/sla/' + groupName + '/' + slaType)
                        .then(function (response) {
                            return response.data;
                        });

                }
            };

            return service;
        }
    ]);

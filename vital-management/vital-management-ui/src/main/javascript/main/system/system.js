'use strict';
angular.module('main.system', [
    'ngRoute'
])
    .config(['$routeProvider', function ($routeProvider) {

        $routeProvider.when('/system/list', {
            templateUrl: 'main/system/system-list.tpl.html',
            controller: 'SystemListController',
            resolve: {
                systemList: [
                    'systemResource',
                    function (systemResource) {
                        return systemResource.fetchList();
                    }
                ]
            }
        });

        $routeProvider.when('/system/view/:uri', {
            templateUrl: 'main/system/system-view.tpl.html',
            controller: 'SystemViewController',
            resolve: {
                system: [
                    '$route', 'systemResource', '$filter',
                    function ($route, systemResource, $filter) {
                        var uri = $filter('decodeHistoryComponent')($route.current.params.uri);
                        return systemResource.fetch(uri);
                    }
                ],
                supportedMetrics: [
                    '$route', 'managementResource', '$filter',
                    function ($route, managementResource, $filter) {
                        var uri = $filter('decodeHistoryComponent')($route.current.params.uri);
                        return managementResource.fetchSupportedPerformanceMetricList(uri)
                            .then(function (supportedMetrics) {
                                return supportedMetrics;
                            }, function (error) {
                                return [];
                            });
                    }
                ]
            }
        });

        $routeProvider.when('/system/edit/:uri', {
            templateUrl: 'main/system/system-edit.tpl.html',
            controller: 'SystemEditController',
            resolve: {
                system: [
                    '$route', 'systemResource', '$filter',
                    function ($route, systemResource, $filter) {
                        var uri = $filter('decodeHistoryComponent')($route.current.params.uri);
                        return systemResource.fetch(uri);
                    }
                ]
            }
        });

    }])

/**
 * SystemController
 */
    .controller('SystemListController', [
        '$scope', 'systemResource', 'systemList',
        function ($scope, systemResource, systemList) {
            $scope.systems = systemList;
        }
    ])

    .controller('SystemViewController', [
        '$scope', 'systemResource', 'system', 'supportedMetrics',
        function ($scope, systemResource, system, supportedMetrics) {
            $scope.system = system;
            $scope.supportedMetrics = supportedMetrics;
        }
    ])

    .controller('SystemEditController', [
        '$scope', 'systemResource', 'system',
        function ($scope, systemResource, system) {
            $scope.system = system;
        }
    ]);

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

            $scope.search = {
                label: null,
                comment: null,
                status: null,
                clear: function () {
                    $scope.search.label = null;
                    $scope.search.comment = null;
                    $scope.search.status = null;
                },
                submit: function (ngFormController) {
                    if (ngFormController.$invalid) {
                        return;
                    }
                    $scope.systems.length = 0;
                    var query = {
                        '$and': []
                    };
                    if ($scope.search.label) {
                        query['$and'].push({
                            'http://www\\u002ew3\\u002eorg/2000/01/rdf-schema#label': {
                                $regex: $scope.search.label
                            }
                        });
                    }
                    if ($scope.search.comment) {
                        query['$and'].push({
                            'http://www\\u002ew3\\u002eorg/2000/01/rdf-schema#comment': {
                                $regex: $scope.search.comment
                            }
                        });
                    }
                    if ($scope.search.status) {
                        query['$and'].push({
                            'http://vital-iot\\u002eeu/ontology/ns/status.@id': {
                                $regex: $scope.search.status
                            }
                        });
                    }
                    if (query['$and'].length === 0) {
                        query = {};
                    }

                    return systemResource.search(query)
                        .then(function (systemList) {
                            angular.forEach(systemList, function (system) {
                                $scope.systems.push(system);
                            });
                        });
                }
            }
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

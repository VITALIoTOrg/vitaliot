'use strict';
angular.module('main.metaservice', [])

    .config(['$routeProvider', function($routeProvider) {

        $routeProvider.when('/metaservice/list', {
            templateUrl: 'main/metaservice/metaservice-list.tpl.html',
            controller: 'MetaserviceListController',
            controllerAs: 'metaserviceListCtrl',
            resolve: {
                metaserviceList: [
                    '$route', 'metaserviceResource',
                    function($route, metaserviceResource) {
                        return metaserviceResource.fetchList();
                    }
                ]
            }
        });

        $routeProvider.when('/metaservice/view/:id?', {
            templateUrl: 'main/metaservice/metaservice-view.tpl.html',
            controller: 'MetaserviceViewController',
            controllerAs: 'metaserviceViewCtrl',
            resolve: {
                metaservice: [
                    '$route', 'metaserviceResource',
                    function($route, metaserviceResource) {
                        var id = $route.current.params.id;
                        if (!!id) {
                            // Just retrieve from server
                            return metaserviceResource.fetch(id);
                        } else {
                            return {
                                'name': '',
                                'status': 'DISABLED',
                                'operationList': []
                            };
                        }
                    }
                ],
                operationList: [
                    '$route', 'operationResource',
                    function($route, operationResource) {
                        return operationResource.fetchList();
                    }
                ]
            }
        });
    }])

    .controller('MetaserviceListController', [
        '$scope', 'metaserviceResource', 'metaserviceList',
        function($scope, metaserviceResource, metaserviceList) {
            var metaserviceListCtrl = this;
            metaserviceListCtrl.metaserviceList = metaserviceList;
            metaserviceListCtrl.state = {
                errors: []
            };
            metaserviceListCtrl.actions = {
                delete: function(metaservice) {
                    metaserviceListCtrl.state.errors.length = 0;
                    metaserviceResource.delete(metaservice.id)
                        .then(function(saveMetaservice) {
                            metaserviceListCtrl.metaserviceList.splice(metaserviceListCtrl.metaserviceList.indexOf(metaservice), 1);
                        }, function(error) {
                            metaserviceListCtrl.state.errors.push(error);
                        });
                }
            };
        }
    ])


    .controller('MetaserviceViewController', [
        '$scope', '$location', 'metaserviceResource', 'metaservice', 'SERVICE_URL',
        function($scope, $location, metaserviceResource, metaservice, SERVICE_URL) {
            var thisCtrl = this;
            this.serviceURL = SERVICE_URL + '/execute/service/' + metaservice.id;
            thisCtrl.metaservice = metaservice;
        }
    ])

    .factory('metaserviceResource', [
        '$http', '$q', 'SERVICE_URL',
        function($http, $q, SERVICE_URL) {
            var ROOT_URL = SERVICE_URL + '/metaservice';
            var EXECUTE_URL = SERVICE_URL + '/execute';

            // The public API of the metaservice
            var metaservice = {

                fetchList: function() {
                    return $http.get(ROOT_URL)
                        .then(function(response) {
                            return response.data;
                        });
                },

                fetch: function(id) {
                    return $http.get(ROOT_URL + '/' + id)
                        .then(function(response) {
                            return response.data;
                        });
                },

                save: function(metaservice) {
                    if (!!metaservice.id) {
                        return $http.put(ROOT_URL + '/' + metaservice.id, metaservice)
                            .then(function(response) {
                                return response.data;
                            });
                    } else {
                        return $http.post(ROOT_URL, metaservice)
                            .then(function(response) {
                                return response.data;
                            });
                    }

                },

                delete: function(id) {
                    return $http.delete(ROOT_URL + '/' + id);
                },

                execute: function(metaservice, input) {
                    return $http.post(EXECUTE_URL + '/metaservice', {
                        input: input,
                        metaservice: metaservice
                    }).then(function(response) {
                        return response.data;
                    });
                }
            };

            return metaservice;
        }
    ]);

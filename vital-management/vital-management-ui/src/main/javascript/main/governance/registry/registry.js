'use strict';
angular.module('main.governance.registry', [
        'ngRoute'
    ])


    /************
     ** Config **
     ************/
    .config(['$routeProvider', function ($routeProvider) {

        $routeProvider.when('/governance/registry/list', {
            templateUrl: 'main/governance/registry/registry-list.tpl.html',
            controller: 'RegistryListController',
            controllerAs: 'regCtrl',
            resolve: {
                systemList: [
                    'registryResource',
                    function (registryResource) {
                        return registryResource.fetchList();
                    }
                ]
            }
        });

        $routeProvider.when('/governance/registry/edit/:id', {
            templateUrl: 'main/governance/registry/registry-edit.tpl.html',
            controller: 'RegistryEditController',
            controllerAs: 'regCtrl',
            resolve: {
                system: [
                    '$route', 'registryResource',
                    function ($route, registryResource) {
                        var system_id = $route.current.params.id;
                        return registryResource.fetch(system_id);
                    }
                ]
            }
        });

        $routeProvider.when('/governance/registry/edit', {
            templateUrl: 'main/governance/registry/registry-edit.tpl.html',
            controller: 'RegistryEditController',
            controllerAs: 'regCtrl',
            resolve: {
                system: [
                    function () {
                        return {
                            authenticationInfo: {}
                        };
                    }
                ]
            }
        });

    }])


    /******************
     ** Controllers ***
     ******************/

    .controller('RegistryListController', [
        '$scope', '$route', 'registryResource', 'systemList',
        function ($scope, $route, registryResource, systemList) {
            var ctrl = this;
            ctrl.systems = systemList;


            ctrl.refresh = function (system) {
                registryResource.refresh(system.id)
                    .then(function () {
                    });
            };

            ctrl.deregister = function (system) {
                registryResource.remove(system.id)
                    .then(function () {
                        $route.reload();
                    });
            }
        }
    ])


    .controller('RegistryEditController', [
        '$scope', '$location', 'registryResource', 'system',
        function ($scope, $location, registryResource, system) {
            var ctrl = this;
            ctrl.system = system;
            ctrl.requiresBasicAuth = (!!ctrl.system.authenticationInfo.username) || (!!ctrl.system.authenticationInfo.password);

            ctrl.submit = function (ngFormController) {
                if (ngFormController.$invalid) {
                    return;
                }
                if (system.id != null) {
                    registryResource.update(ctrl.system)
                        .then(function () {
                            $location.path("/governance/registry/list");
                        });
                } else {
                    registryResource.create(ctrl.system)
                        .then(function () {
                            $location.path("/governance/registry/list");
                        });
                }
            }
        }
    ])

    /***************
     ** Services ***
     ***************/

    .factory('registryResource', [
        '$http', '$q', 'API_PATH',
        function ($http, $q, API_PATH) {

            var basePath = API_PATH.replace('/api', '/iot-data-adapter');

            // The public API of the service
            var service = {

                fetchList: function () {
                    return $http.get(basePath + '/systems')
                        .then(function (response) {
                            var systems = [];
                            angular.forEach(response.data, function (system) {
                                systems.push(system);
                            });
                            return systems;
                        });
                },

                fetch: function (id) {
                    return $http.get(basePath + '/systems/' + id)
                        .then(function (response) {
                            return response.data;
                        });
                },

                create: function (system) {
                    return $http.put(basePath + '/systems', system)
                        .then(function (response) {
                            return response.data;
                        });

                },

                update: function (system) {
                    return $http.post(basePath + '/systems/' + system.id, system)
                        .then(function (response) {
                            return response.data;
                        });

                },

                remove: function (systemId) {
                    return $http.delete(basePath + '/systems/' + systemId)
                        .then(function (response) {
                            return response.data;
                        });
                },

                refresh: function (systemId) {
                    return $http.get(basePath + '/systems/' + systemId + '/refresh')
                        .then(function (response) {
                            return response.data;
                        });

                }
            };

            return service;
        }
    ]);

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
        '$scope', '$route', 'registryResource', 'securityResource', 'systemList',
        function ($scope, $route, registryResource, securityResource, systemList) {
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
                        return securityResource.deleteGroup(system.name, $scope);
                    }).then(function () {
                        return securityResource.deletePolicy(system.name, $scope);
                    })/*.then(function () {
                        return securityResource.deletePolicy(system.name + " fine-grained", $scope);
                    })*/.then(function () {
                        $route.reload();
                    });
            };

            ctrl.syncResults = null;
            ctrl.sync = function () {
                ctrl.syncResults = null;
                registryResource.sync(ctrl.system)
                    .then(function (syncResults) {
                        ctrl.syncResults = syncResults;
                    }, function (errorResponse) {
                        ctrl.syncResults = errorResponse;
                    });
            }
        }
    ])


    .controller('RegistryEditController', [
        '$scope', '$location', 'registryResource', 'securityResource', 'system',
        function ($scope, $location, registryResource, securityResource, system) {
            var ctrl = this;
            var originalName = system.name;
            ctrl.system = system;
            ctrl.requiresBasicAuth = (!!ctrl.system.authenticationInfo.username) || (!!ctrl.system.authenticationInfo.password);

            ctrl.submit = function (ngFormController) {
                if (ngFormController.$invalid) {
                    return;
                }
                if (system.id != null) {
                    registryResource.update(ctrl.system)
                        .then(function () {
                            // If the name it's still the same update existing policies
                            if (system.name == originalName) {
                                // Update basic policy
                                var policyForm = {};
                                policyForm.description = "Automatically generated policy regulating access to " + system.name;
                                policyForm.resources = [system.ppi + "*", system.ppi + "/*"];
                                policyForm.actions = {};
                                policyForm.actions['GET'] = true;
                                policyForm.actions['POST'] = true;
                                policyForm.groups = [system.name];
                                policyForm.active = true;
                                policyForm.nogr = false;
                                policyForm.nores = false;
                                policyForm.noact = false;
                                securityResource.updatePolicy(system.name, policyForm, $scope, "sysupd")
                                   /* .then(function() {
                                        // Update fine-grained policy
                                        policyForm.description = "Automatically generated policy regulating access to " + system.name + " data (fine-grained access control)";
                                        policyForm.resources = [ "id$" + system.ppi + ".*"];
                                        policyForm.actions = {};
                                        policyForm.actions['RETRIEVE'] = true;
                                        policyForm.groups = [system.name];
                                        policyForm.active = true;
                                        policyForm.nogr = false;
                                        policyForm.nores = false;
                                        policyForm.noact = false;
                                        return securityResource.updatePolicy(system.name + " fine-grained", policyForm, $scope, "sysupd");
                                    })*/.then(function() {
                                        $location.path("/governance/registry/list");
                                    });
                            }
                            // Otherwise delete old ones and create new ones
                            else {
                                // Delete old group and policies
                                var groupForm = {};
                                var policyForm = {};
                                securityResource.deleteGroup(originalName, $scope)
                                    .then(function() {
                                        return securityResource.deletePolicy(originalName, $scope);
                                    }).then(function() {
                                        return securityResource.deletePolicy(originalName + " fine-grained", $scope);
                                    }).then(function() {
                                        // Create group for the system
                                        groupForm.name = system.name;
                                        return securityResource.createGroup(groupForm, $scope);
                                    }).then(function() {
                                        // Create policy to give access to the system to just created group
                                        policyForm.name = system.name;
                                        policyForm.description = "Automatically generated policy regulating access to " + system.name;
                                        policyForm.appname = "iPlanetAMWebAgentService";
                                        policyForm.resources = [system.ppi + "*", system.ppi + "/*"];
                                        policyForm.actions = {};
                                        policyForm.actions['GET'] = true;
                                        policyForm.actions['POST'] = true;
                                        policyForm.groups = [system.name];
                                        return securityResource.createPolicy(policyForm, $scope);
                                    })/*.then(function() {
                                        // Create fine-grained policy for the PPI and group
                                        policyForm.name = system.name + " fine-grained";
                                        policyForm.description = "Automatically generated policy regulating access to " + system.name + " data (fine-grained access control)";
                                        policyForm.appname = "Data access control";
                                        policyForm.resources = [ "id$" + system.ppi + ".*"];
                                        policyForm.actions = {};
                                        policyForm.actions['RETRIEVE'] = true;
                                        policyForm.groups = [system.name];
                                        return securityResource.createPolicy(policyForm, $scope);
                                    })*/.then(function() {
                                        $location.path("/governance/registry/list");
                                    });
                            }
                        });
                } else {
                    registryResource.create(ctrl.system)
                        .then(function () {
                            // Create group for the new system
                            var groupForm = {};
                            var policyForm = {};
                            groupForm.name = system.name;
                            securityResource.createGroup(groupForm, $scope)
                                .then(function() {
                                    // Create policy to give access to the new system to just created group
                                    policyForm.name = system.name;
                                    policyForm.description = "Automatically generated policy regulating access to " + system.name;
                                    policyForm.appname = "iPlanetAMWebAgentService";
                                    policyForm.resources = [system.ppi + "*", system.ppi + "/*"];
                                    policyForm.actions = {};
                                    policyForm.actions['GET'] = true;
                                    policyForm.actions['POST'] = true;
                                    policyForm.groups = [system.name];
                                    return securityResource.createPolicy(policyForm, $scope);
                                })/*.then(function() {
                                    // Create fine-grained policy for the PPI and group
                                    policyForm.name = system.name + " fine-grained";
                                    policyForm.description = "Automatically generated policy regulating access to " + system.name + " data (fine-grained access control)";
                                    policyForm.appname = "Data access control";
                                    policyForm.resources = [ "id$" + system.ppi + ".*"];
                                    policyForm.actions = {};
                                    policyForm.actions['RETRIEVE'] = true;
                                    policyForm.groups = [system.name];
                                    return securityResource.createPolicy(policyForm, $scope)
                                })*/.then(function() {
                                    // Go back to list of systems
                                    $location.path("/governance/registry/list");
                                });
                        });
                }
            };
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

                },

                sync: function () {
                    return $http.get(API_PATH + '/admin/sync')
                        .then(function (response) {
                            return response.data;
                        });
                }
            };

            return service;
        }
    ]);
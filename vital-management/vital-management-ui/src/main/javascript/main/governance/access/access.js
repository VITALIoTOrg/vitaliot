'use strict';
angular.module('main.governance.access', [
    'ngRoute'
])


/************
 ** Config **
 ************/
    .config([
        '$routeProvider', function($routeProvider) {

            $routeProvider.when('/governance/access', {
                templateUrl: 'main/governance/access/access.tpl.html',
                controller: 'AccessController',
                controllerAs: 'aCtrl',
                resolve: {
                    groupList: [
                        'accessResource',
                        function(accessResource) {
                            return accessResource.fetchGroupList();
                        }
                    ]
                }
            });

            $routeProvider.when('/governance/access/:groupName', {
                templateUrl: 'main/governance/access/access-group.tpl.html',
                controller: 'AccessGroupController',
                controllerAs: 'aaCtrl',
                resolve: {
                    systemList: [
                        'accessResource',
                        function(accessResource) {
                            return accessResource.fetchSystemList();
                        }
                    ],
                    group: [
                        '$route', 'accessResource',
                        function($route, accessResource) {

                            return accessResource.fetchGroup($route.current.params.groupName);
                        }
                    ],
                    access: [
                        '$route', 'accessResource',
                        function($route, accessResource) {
                            return accessResource.fetchAccess($route.current.params.groupName);
                        }
                    ]
                }
            });
        }
    ])

    /******************
     ** Controllers ***
     ******************/

    .controller('AccessController', [
        'accessResource', 'groupList',
        function(accessResource, groupList) {
            var ctrl = this;
            ctrl.groupList = groupList;
        }
    ])

    .controller('AccessGroupController', [
        'accessResource', 'systemList', 'group', 'access',
        function(accessResource, systemList, group, access) {
            var ctrl = this;

            // Map Options
            ctrl.access = extendWithSystems(access, systemList);
            ctrl.systemList = systemList;
            ctrl.group = group;

            // Actions

            ctrl.save = function(ngFormController) {
                accessResource.updateAccess(ctrl.group.username, ctrl.access)
                    .then(function(newAccess) {
                        ctrl.access = newAccess;
                    });
            };

            // Watchers, Events
            // Functions

            function extendWithSystems(access, systemList) {
                _.forEach(systemList, function(system) {
                    if (_.isUndefined(access[system.id]) || _.isNull(access[system.id])) {
                        access[system.id] = true;
                    }
                });
                return access;
            }
        }
    ])

    /***************
     ** Services ***
     ***************/

    .factory('accessResource', [
        '$http', '$q', 'API_PATH',
        function($http, $q, API_PATH) {

            // The public API of the service
            var service = {

                fetchSystemList: function() {
                    return $http.get(API_PATH + '/system')
                        .then(function(response) {
                            return response.data;
                        });
                },

                fetchGroupList: function() {
                    var path = API_PATH.replace('/api', '/security') + '/groups';
                    return $http.get(path)
                        .then(function(response) {
                            return response.data.result;
                        });
                },

                fetchGroup: function(groupName) {
                    var path = API_PATH.replace('/api', '/security') + '/group/' + groupName;
                    return $http.get(path)
                        .then(function(response) {
                            return response.data;
                        });
                },

                fetchAccess: function(groupName) {
                    return $http.get(API_PATH + '/governance/access/' + groupName)
                        .then(function(response) {
                            return response.data;
                        });
                },

                updateAccess: function(groupName, access) {
                    return $http.put(API_PATH + '/governance/access/' + groupName, access)
                        .then(function(response) {
                            return response.data;
                        });

                }
            };

            return service;
        }
    ]);

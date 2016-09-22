'use strict';
angular.module('main.query', [
        'ngRoute'
    ])

    .config([
        '$routeProvider',
        function ($routeProvider) {

            $routeProvider.when('/query', {
                templateUrl: 'main/query/query.tpl.html',
                controller: 'QueryController',
                controllerAs: 'queryCtrl'
            });

        }
    ])

    /**
     * SystemController
     */
    .controller('QueryController', [
        'queryResource',
        function (queryResource) {
            var ctrl = this;

            ctrl.resourceType = '';
            ctrl.query = '';
            ctrl.encodeKeys = true;
            ctrl.result = null;

            ctrl.submitQuery = function (ngFormController) {
                if (ngFormController.$invalid) {
                    return;
                }
                ctrl.result = null;
                queryResource.query(ctrl.resourceType, ctrl.query, ctrl.encodeKeys)
                    .then(function (data) {
                        ctrl.result = data;
                    }, function (error) {
                        ctrl.result = error.data;
                    });
            }
        }
    ])

    .factory('queryResource', [
        '$http', '$q', 'API_PATH',
        function ($http, $q, API_PATH) {

            var service = {

                query: function (resourceType, query, encodeKeys) {
                    return $http.post(API_PATH + '/query/' + resourceType + '?encodeKeys=' + encodeKeys, angular.fromJson(query))
                        .then(function (response) {
                            return response.data;
                        });
                }
            };

            // return service
            return service;
        }
    ]);


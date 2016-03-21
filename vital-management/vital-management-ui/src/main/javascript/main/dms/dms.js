'use strict';
angular.module('main.dms', [
        'ngRoute'
    ])

    .config([
        '$routeProvider',
        function ($routeProvider) {

            $routeProvider.when('/dms', {
                templateUrl: 'main/dms/dms.tpl.html',
                controller: 'DmsController',
                controllerAs: 'dmsCtrl'
            });

        }
    ])

    /**
     * SystemController
     */
    .controller('DmsController', [
        'dmsResource',
        function (dmsResource) {
            var ctrl = this;

            ctrl.resourceType = '';
            ctrl.query = '';
            ctrl.result = null;

            ctrl.queryDms = function (resourceType, query, ngFormController) {

                ctrl.result = null;
                dmsResource(resourceType, query)
                    .then(function (data) {
                        ctrl.result = data;
                    });
            }
        }
    ])


    .factory('dmsResource', [
        '$http', '$q', 'API_PATH',
        function ($http, $q, API_PATH) {

            var service = {

                query: function (resourceType, query) {

                    return $http.post(API_PATH + '/dms/' + resourceType, query)
                        .then(function (response) {
                            return response.data;
                        });
                }
            };

            // return service
            return service;
        }
    ]);


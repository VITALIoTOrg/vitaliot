'use strict';
angular.module('main.modules', [
    'ngRoute'
])
    .config(['$routeProvider', function ($routeProvider) {

        $routeProvider.when('/modules', {
            templateUrl: 'main/modules/modules.tpl.html',
            controller: 'ModulesController'
        });

    }])

/**
 * ModulesController
 */
    .controller('ModulesController', [
        '$scope',
        function ($scope) {
        }
    ]);

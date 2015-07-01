'use strict';
angular.module('app.main.home', [
    'ngRoute'
])
    .config(['$routeProvider', function($routeProvider) {

        $routeProvider.when('/home', {
            templateUrl: 'main/home/home.tpl.html',
            controller: 'HomeController'
        });
    }])


/**
 * HomeController
 */
    .controller('HomeController', [
        '$window', '$location', 'pathService', '$scope',
        function($window, $location, pathService, $scope) {
            $scope.cmOption = {
                lineNumbers: true,
                indentWithTabs: true,
                mode: 'javascript'
            };
            $scope.cmModel = '';
        }
    ]);

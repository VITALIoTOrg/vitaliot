'use strict';
angular.module('main.home', [
    'ngRoute'
])
    .config(['$routeProvider', function ($routeProvider) {

        $routeProvider.when('/home', {
            templateUrl: 'main/home/home.tpl.html',
            controller: 'HomeController'
        });
    }])


/**
 * HomeController
 */
    .controller('HomeController', [
        '$window', '$location', '$scope',
        function ($window, $location, $scope) {

        }
    ]);

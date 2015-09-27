'use strict';
angular.module('main', [
    'common',
    'main.templates',
    'main.home',
    'main.sensor',
    'main.system',
    'main.modules'
])

    .config(['$routeProvider', function ($routeProvider) {

        $routeProvider.otherwise({
            redirectTo: '/system/list'
        });

    }])

/**
 * MainController
 */
    .controller('MainController', [
        '$location', '$scope',
        function ($location, $scope) {

            $scope.title = '';
            $scope.subtitle = '';

            $scope.$watch(function () {
                return $location.path();
            }, function (path) {
                if (path === '/sensor/list') {
                    $scope.title = 'List of ICOs/Sensors';
                } else if (path === '/sensor/map') {
                    $scope.title = 'Map of ICOs/Sensors';
                } else if (/^\/sensor\/view\/./.test(path)) {
                    $scope.title = 'Sensor/ICO Details';
                } else if (/^\/system\/view\/./.test(path)) {
                    $scope.title = 'IOT System Details';
                } else if (path === '/system/list') {
                    $scope.title = 'List of IoTs/Services';
                } else if (/^\/system\/edit\/./.test(path)) {
                    $scope.title = 'System Configuration';
                }
            });
        }
    ]);

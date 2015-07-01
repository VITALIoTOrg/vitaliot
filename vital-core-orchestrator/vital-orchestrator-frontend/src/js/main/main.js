'use strict';
angular.module('app.main', [
    'common',
    'app.main.home',
    'app.main.operation',
    'app.main.workflow',
    'app.main.metaservice',
    'templates-main'
])

    .constant('SERVICE_URL', '/vital-orchestrator-web/rest')

    .config(['$routeProvider', function($routeProvider) {

        $routeProvider.otherwise({
            redirectTo: '/service/list'
        });

    }])

/**
 * MainController
 */
    .controller('MainController', [
        '$location', '$scope',
        function($location, $scope) {
        }
    ]);

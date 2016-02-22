'use strict';
angular.module('main', [
    'common',
    'main.templates',
    'main.home',
    'main.operation',
    'main.workflow',
    'main.metaservice'
])

    .constant('SERVICE_URL', '/vital-orchestrator-web/rest')

    .config(['$routeProvider', function ($routeProvider) {

        $routeProvider.otherwise({
            redirectTo: '/service/list'
        });

    }])

/**
 * MainController
 */
    .controller('MainController', [
        '$location', '$scope',
        function ($location, $scope) {
        }
    ]);

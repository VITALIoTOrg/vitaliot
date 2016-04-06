'use strict';
angular.module('main', [
        'common',
        'main.templates',
        'main.home',
        'main.login',
        'main.operation',
        'main.workflow',
        'main.metaservice'
    ])

    .constant('SERVICE_URL', '/vital-core-orchestrator-web/rest')

    .config(['$routeProvider', function ($routeProvider) {

        $routeProvider.otherwise({
            redirectTo: '/service/list'
        });

    }])

    /**
     * MainController
     */
    .controller('MainController', [
        '$scope', 'authentication',
        function ($scope, authentication) {

            /** Security **/
            $scope.isAuthenticated = function () {
                return authentication.isAuthenticated();
            };
            $scope.logout = function () {
                authentication.logout();
            };
            $scope.$watch(function () {
                return authentication.loggedOnUser;
            }, function (loggedOnUser) {
                $scope.loggedOnUser = loggedOnUser;
            });
            /** end: Security **/

        }
    ]);

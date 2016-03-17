'use strict';
angular.module('common.route', [])

    .run([
        '$rootScope', '$location',
        function ($rootScope, $location) {
            $rootScope.$on('$routeChangeError', function () {
                // When a route is rejected, for whatever reason, redirect to not-found
                //$location.path('/not-found');
                // Temporary disabled since reject for 401 triggers this
            });
        }
    ])

    .config([
        '$routeProvider',
        function ($routeProvider) {
            // Common not-found route
            $routeProvider.when('/not-found', {
                templateUrl: 'common/route/not-found.tpl.html'
            });
        }
    ]);

/**
 * Created by anglen on 03/03/16.
 */
'use strict';
angular.module('common.authentication', [])

    /**
     * Globally intercept http calls and in case of 401 redirect to login
     */
    .config(['$httpProvider', function ($httpProvider) {
        $httpProvider.interceptors.push('authenticationInterceptor');
    }])

    .run(['$rootScope', '$location', 'securityResource', 'Shared',
        function ($rootScope, $location, securityResource, Shared) {
            $rootScope.$on('$locationChangeStart', function () {
                securityResource.getId($rootScope, true).then(function () {
                    if ($location.path() === '/login' || Shared.signedIn) {
                        return;
                    } else {
                        Shared.requestedPage = $location.path();
                        $location.path('/login');
                    }
                });
            });
        }])

    .factory('authenticationInterceptor', [
        '$q', '$injector', '$location', 'Shared',
        function ($q, $injector, $location, Shared) {
            return {
                'responseError': function (rejection) {
                    if (rejection.status === 401) {
                        Shared.requestedPage = $location.path();
                        $location.path('/login');
                    }
                    return $q.reject(rejection);
                }
            };
        }]);


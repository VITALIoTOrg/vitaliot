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

    .run(['$rootScope', '$location', '$route', 'authentication',
        function ($rootScope, $location, $route, authentication) {
            $rootScope.$on('$locationChangeStart', function (event, next, current) {
                if ($location.path() === '/login' || authentication.isAuthenticated()) {
                    return;
                }
                // Load User and return to this path, if success.
                // If not, the 401 authenticationInterceptor will redirect to login
                authentication.fetchLoggedOnUser()
                    .then(function () {
                        $route.reload();
                    });
                event.preventDefault();
            });
        }
    ])

    .factory('authenticationInterceptor', [
        '$q', '$injector',
        function ($q, $injector) {
            return {
                'responseError': function (rejection) {
                    var $security = $injector.get('authentication');
                    if (rejection.status === 401) {
                        $security.showLogin();
                    }
                    return $q.reject(rejection);
                }
            };
        }])

    /**
     * Authentication Provider
     */
    .provider('authentication', [
        function () {
            var provider = this;
            provider.$get = [
                '$window', '$http', '$q', '$location', '$cookies', '$timeout', 'API_PATH',
                function ($window, $http, $q, $location, $cookies, $timeout, API_PATH) {
                    var service = {

                        loggedOnUser: null,

                        // Ask the backend to see if a user is already authenticated - this may be from a previous session.
                        fetchLoggedOnUser: function () {
                            if (service.isAuthenticated()) {
                                return $q.when(service.loggedOnUser);
                            } else {
                                return $http.get(API_PATH + '/authentication/logged-on')
                                    .then(function (resp) {
                                        // Set some default values
                                        var user = {
                                            name: resp.data.uid,
                                            fullname: resp.data.uid,
                                            avatar: 'https://www.gravatar.com/avatar/' + (resp.data.mailhash || resp.data.creation)
                                        };
                                        // Override with data from server
                                        angular.extend(user, resp.data);
                                        // Set service variable
                                        service.loggedOnUser = user;
                                        // Return result
                                        return service.loggedOnUser;
                                    }, function (errResponse) {
                                        service.showLogin();
                                        return $q.reject(errResponse);
                                    });
                            }
                        },

                        // Ask the backend to see if a user is already authenticated - this may be from a previous session.
                        login: function (loginData) {
                            return $http({
                                method: 'POST',
                                url: API_PATH + '/authentication/login',
                                withCredentials: true,
                                data: $.param(loginData),
                                headers: {'Content-Type': 'application/x-www-form-urlencoded'}
                            }).then(function (resp) {
                                // Set some default values
                                var user = {
                                    name: resp.data.uid,
                                    fullname: resp.data.uid,
                                    avatar: 'https://www.gravatar.com/avatar/' + (resp.data.mailhash || resp.data.creation)
                                };
                                // Override with data from server
                                angular.extend(user, resp.data);
                                // Set service variable
                                service.loggedOnUser = user;
                                // Return result
                                return service.loggedOnUser;
                            });
                        },

                        // Is the current user authenticated?
                        isAuthenticated: function () {
                            return !!service.loggedOnUser;
                        },

                        showLogin: function () {
                            $timeout(function () {
                                service.loggedOnUser = null;
                                $location.path('/login');
                            });
                        },

                        // Logout the current user and redirect
                        logout: function () {
                            var formData = {};
                            return $http({
                                method: 'POST',
                                url: API_PATH + '/authentication/logout',
                                withCredentials: true,
                                data: '',
                                headers: {'Content-Type': 'application/x-www-form-urlencoded'}
                            }).then(function (response) {
                                service.loggedOnUser = null;
                                service.showLogin();
                            });
                        }
                    };
                    return service;
                }
            ];
        }
    ]);

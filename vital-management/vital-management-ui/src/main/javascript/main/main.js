'use strict';
angular.module('main', [
        'common',
        'main.templates',
        'main.home',
        'main.login',
        'main.sensor',
        'main.system',
        'main.modules',
        'main.security'
    ])

    .service('Shared', function () {
        this.signedIn = false;
    })

    .config(['$routeProvider', function ($routeProvider) {

        $routeProvider.otherwise({
            redirectTo: '/system/list'
        });

    }])

    /**
     * MainController
     */
    .controller('MainController', [
        '$location', '$scope', '$timeout', '$interval', '$route', 'authentication',
        function ($location, $scope, $timeout, $interval, $route, authentication) {

            $scope.title = '';
            $scope.subtitle = '';
            $scope.active = '';
            $scope.genloginLoading = false;
            $scope.genlogoutLoading = false;

            $scope.$watch(function () {
                return $location.path();
            }, function (path) {
                // Management and Monitoring paths
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

                // Security paths
                if (path.substring(0, 9) === '/security') {
                    $scope.active = 'active';
                }
                if (path.substring(0, 18) === '/security/systconf') {
                    $scope.title = 'Guided System Configuration';
                    $scope.subtitle = '';
                    $scope.titlelink = '#/security/systconf';
                } else if (path.substring(0, 15) === '/security/users') {
                    $scope.title = 'Users Control Panel';
                    $scope.subtitle = '';
                    $scope.titlelink = '#/security/users';
                    if (path === '/security/users/create') {
                        $scope.subtitle = 'Add User';
                    }
                    if (path === '/security/users/details') {
                        $scope.subtitle = 'User Details';
                    }
                } else if (path.substring(0, 16) === '/security/groups') {
                    $scope.title = 'Groups Control Panel';
                    $scope.subtitle = '';
                    $scope.titlelink = '#/security/groups';
                    if (path === '/security/groups/create') {
                        $scope.subtitle = 'Add Group';
                    }
                    if (path === '/security/groups/details') {
                        $scope.subtitle = 'Group Details';
                    }
                } else if (path.substring(0, 18) === '/security/policies') {
                    $scope.title = 'Policies Control Panel';
                    $scope.subtitle = '';
                    $scope.titlelink = '#/security/policies';
                    if (path === '/security/policies/create') {
                        $scope.subtitle = 'Add Policy';
                    }
                    if (path === '/security/policies/details') {
                        $scope.subtitle = 'Policy Details';
                    }
                } else if (path.substring(0, 21) === '/security/datacontrol') {
                    $scope.title = 'Data Access Control';
                    $scope.subtitle = '';
                    $scope.titlelink = '#/security/datacontrol';
                } else if (path.substring(0, 22) === '/security/applications') {
                    $scope.title = 'Applications Control Panel';
                    $scope.subtitle = '';
                    $scope.titlelink = '#/security/applications';
                    if (path === '/security/applications/create') {
                        $scope.subtitle = 'Add Application';
                    }
                    if (path === '/security/applications/details') {
                        $scope.subtitle = 'Application Details';
                    }
                } else if (path === '/security/monitor') {
                    $scope.title = 'Security Monitor';
                    $scope.subtitle = '';
                    $scope.titlelink = '#/security/monitor';
                } else if (path === '/security/access') {
                    $scope.title = 'Access Control Test';
                    $scope.subtitle = '';
                    $scope.titlelink = '#/security/access';
                } else if (path === '/security/changepass') {
                    $scope.title = 'Password Change';
                    $scope.subtitle = '';
                    $scope.titlelink = '#/security/changepass';
                } else if (path === '/security/register') {
                    $scope.title = 'Sign up';
                    $scope.subtitle = '';
                    $scope.titlelink = '#/security/register';
                }
            });

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

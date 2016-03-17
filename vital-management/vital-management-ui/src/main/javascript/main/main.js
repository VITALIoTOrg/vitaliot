'use strict';
angular.module('main', [
        'common',
        'main.templates',
        'main.login',
        'main.sensor',
        'main.system',
        'main.modules',
        'main.security'
    ])

    .service('Shared', function () {
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
        '$location', '$scope', '$timeout', '$interval', '$route', 'securityResource', 'Shared',
        function ($location, $scope, $timeout, $interval, $route, securityResource, Shared) {

            // Enable swipe gestures to show and hide the menu sidebar
            $('body').hammer().on('swiperight', function(e) {
                e.preventDefault();
                // OPEN
                console.log('swiperight');
                if($(window).width() > (768 - 1)) {
                    if($('body').hasClass('sidebar-collapse')) {
                        $('body').removeClass('sidebar-collapse');
                    }
                } else {
                    if(!$('body').hasClass('sidebar-open')) {
                        $('body').addClass('sidebar-open');
                    }
                }

            });

            $('body').hammer().on('swipeleft', function(e) {
                e.preventDefault();
                // CLOSE
                console.log('swipeleft');
                if($(window).width() > (768 - 1)) {
                    if(!$('body').hasClass('sidebar-collapse')) {
                        $('body').addClass('sidebar-collapse');
                    }
                } else {
                    if($('body').hasClass('sidebar-open')) {
                        $('body').removeClass('sidebar-open');
                        $('body').removeClass('sidebar-collapse');
                    }
                }

            });

            $scope.title = '';
            $scope.subtitle = '';
            $scope.active1 = '';
            $scope.active2 = '';
            $scope.genloginLoading = false;
            $scope.genlogoutLoading = false;
            Shared.signedIn = false;

            securityResource.getId($scope, true);

            $scope.signin = function(data) {
                $scope.genloginLoading = true;
                securityResource.authenticate(data, $scope, true);
            };

            $scope.signout = function() {
                $scope.genlogoutLoading = true;
                securityResource.logout($scope, true);
            };

            $scope.doFocus = function(data) {
                $timeout(function() { document.getElementById('loginfoc').focus(); }, 100);
            };

            $scope.isSignedIn = function() {
                $scope.loggedUser = Shared.loggedUser;
                return Shared.signedIn;
            };

            $scope.checkSession = function() {
                securityResource.getId($scope, true)
                .then(function(response) {
                    if(response.data.hasOwnProperty('valid')) {
                        if(!response.data.valid) {
                            securityResource.forgetLogin();
                        }
                    }
                }, function() {
                });
            };

            $interval($scope.checkSession, 5 * 60 * 1000);

            $scope.$watch(function () {
                return $location.path();
            }, function (path) {
                // Login path
                if (path.substring(0, 6) === '/login') {
                    $scope.title = '';
                    $scope.subtitile = '';
                    $scope.titlelink = '';
                } else {
                    Shared.requestedPage = path;
                }
                if (path.substring(0, 5) === '/home') {
                    $scope.title = '';
                    $scope.subtitile = '';
                    $scope.titlelink = '';
                } 
                // Management and Monitoring paths
                if (path.substring(0, 7) === '/sensor') {
                    $scope.active1 = 'active';
                }
                if (path === '/sensor/list') {
                    $scope.title = 'List of ICOs/Sensors';
                    $scope.subtitile = '';
                    $scope.titlelink = '#/sensor/list';
                } else if (path === '/sensor/map') {
                    $scope.title = 'Map of ICOs/Sensors';
                    $scope.subtitile = '';
                    $scope.titlelink = '#/sensor/map';
                } else if (/^\/sensor\/view\/./.test(path)) {
                    $scope.title = 'Sensor/ICO Details';
                    $scope.subtitile = '';
                    $scope.titlelink = '';
                } else if (/^\/system\/view\/./.test(path)) {
                    $scope.title = 'IOT System Details';
                    $scope.subtitile = '';
                    $scope.titlelink = '';
                } else if (path === '/system/list') {
                    $scope.title = 'List of IoTs/Services';
                    $scope.subtitile = '';
                    $scope.titlelink = '#/system/list';
                } else if (/^\/system\/edit\/./.test(path)) {
                    $scope.title = 'System Configuration';
                    $scope.subtitile = '';
                    $scope.titlelink = '';
                }

                // Security paths
                if (path.substring(0, 9) === '/security') {
                    $scope.active2 = 'active';
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

        }
    ]);

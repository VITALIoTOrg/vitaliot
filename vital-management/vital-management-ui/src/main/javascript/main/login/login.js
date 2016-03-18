'use strict';
angular.module('main.login', [
        'ngRoute'
    ])
    .config(['$routeProvider', function ($routeProvider) {

        $routeProvider.when('/login', {
            templateUrl: 'main/login/login.tpl.html',
            controller: 'LoginController'
        });

    }])

    .controller('LoginController', [
        '$scope', '$location', 'securityResource', 'Shared', '$route',
        function ($scope, $location, securityResource, Shared, $route) {

            $scope.data = {
                username: null,
                password: null
            };

            securityResource.getId($scope, true)
                .then(function () {
                    $scope.signedIn = Shared.signedIn;
                });

            $scope.login = function (ngFormController) {
                if (ngFormController.$invalid) {
                    return;
                }
                securityResource.authenticate($scope.data, $scope, true)
                    .then(function () {
                        if ($scope.respLogin.status != 400 && $scope.respLogin.status != 500) {
                            if (Shared.requestedPage !== '') {
                                console.log(Shared.requestedPage);
                                $location.path(Shared.requestedPage);
                            }
                        }
                    });
            }

            $scope.logout = function () {
                securityResource.logout($scope, true)
                    .then(function () {
                        $route.reload();
                    });
            }
        }
    ]);

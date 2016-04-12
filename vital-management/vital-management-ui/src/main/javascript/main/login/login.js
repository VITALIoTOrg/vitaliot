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
        '$scope', '$location', 'authentication',
        function ($scope, $location, authentication) {

            $scope.data = {
                username: null,
                password: null
            };
            $scope.errorMessage = null;

            $scope.login = function (ngFormController) {
                if (ngFormController.$invalid) {
                    return;
                }
                $scope.errorMessage = null;
                authentication.login($scope.data)
                    .then(function (user) {
                        $location.path('/');
                    }, function (errorResponse) {
                        if (errorResponse.status === 400)
                            $scope.errorMessage = 'Wrong username or password';
                        else
                            $scope.errorMessage = errorResponse.data.error;
                    });
            }

        }
    ]);

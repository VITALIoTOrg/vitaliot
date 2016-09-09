(function () {
    'use strict';

    angular
        .module('app')
        .controller('LoginController', LoginController);

    LoginController.$inject = ['$location', 'AuthenticationService', 'FlashService'];
    function LoginController($location, AuthenticationService, FlashService) {
        var vm = this;

        vm.login = login;

        (function initController() {
            // reset login status
            AuthenticationService.ClearCredentials();
        })();

        function login() {
            vm.dataLoading = true;

            // $http.get('https://vitalgateway.cloud.reply.eu/securitywrapper/rest/authenticate')
            //     .success(function(data, foo, bar) {
            //         console.log(arguments); // includes data, status, etc including unlisted ones if present
            //     })
            //     .error(function(baz, foo, bar, idontknow) {
            //         console.log(arguments); // includes data, status, etc including unlisted ones if present
            //     });

            // $http({
            //     method: 'POST',
            //     url: 'https://vitalgateway.cloud.reply.eu/securitywrapper/rest/authenticate',
            //     data: "name="+vm.username+"&"+"password"+vm.password,
            //     headers: {'Content-Type': 'application/x-www-form-urlencoded'}
            // }).then(function successCallback(response) {
            //         AuthenticationService.SetCredentials(vm.username, vm.password);
            //         $location.path('/');
            //     }, function errorCallback(response) {
            //         FlashService.Error(response.message);
            //         vm.dataLoading = false;
            //     });

            AuthenticationService.Login(vm.username, vm.password, function (response) {
                if (response.success) {
                    AuthenticationService.SetCredentials(vm.username, vm.password);

                    $location.path('/');
                } else {
                    FlashService.Error(response.message);
                    //FlashService.Error("Username or password is incorrect");
                    vm.dataLoading = false;
                }
            });
        };
    }

})();

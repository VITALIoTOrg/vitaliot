(function () {
    'use strict';

    angular
        .module('app')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['$http', 'UserService', '$rootScope'];
    function HomeController($http, UserService, $rootScope) {

        var vm = this;

        vm.user = null;
        vm.allUsers = [];
        vm.deleteUser = deleteUser;

        initController();

        function initController() {
            loadCurrentUser();
            loadAllUsers();
            //ReaderPPI();
        }

        function loadCurrentUser() {
            UserService.GetByUsername($rootScope.globals.currentUser.username)
                .then(function (user) {
                    vm.user = user;
                });
        }

        function loadAllUsers() {
            UserService.GetAll()
                .then(function (users) {
                    vm.allUsers = users;
                });
        }

        function deleteUser(id) {
            UserService.Delete(id)
            .then(function () {
                loadAllUsers();
            });
        }


        var reqPPI = {
            method: 'POST',
            url: 'http://localhost:8080/discoverer/ppi/metadata',
            responseType: 'text',
            cache: false,
            headers: {
                'Content-Type': 'application/json',
                'Access-Control-Allow-Origin': '*',
                //'Access-Control-Allow-Headers': '*',
                //'Access-Control-Allow-Origin' : 'http://localhost:8080',
                //'Access-Control-Request-Headers': 'x-requested-with',
                'Access-Control-Request-Headers': '*',
                'Access-Control-Allow-Headers': 'Content-Type',
                //'Content-Type': 'application/ld+json'
            },
        }
        $http(reqPPI).then(function(response){
            $rootScope.DiscovererData = response.data;
            console.log(response);
        });
    }

})();
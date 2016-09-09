(function () {
    'use strict';

    angular
        .module('app')
        .controller('QueriesController', QueriesController);

    QueriesController.$inject = ['$location', 'AuthenticationService', 'FlashService','$http', 'UserService', '$rootScope', '$window'];
    function QueriesController($location, AuthenticationService, FlashService, $http, UserService, $rootScope, $window) {

        var qu = this;

        qu.sendQuery = sendQuery;

        // initController();
        //
        // function initController() {
        // }

        function sendQuery() {
            qu.collectData = true;

            var DiscovererBASE_URL="http://localhost:8080/discoverer/";
            var QUERY_URL;
            var TextQuery;
            console.log(entityType);

            //var entityType = document.getElementById("SelectEntityID").value;
            var entityType = qu.SelectEntityID;
            console.log(entityType);

            if (entityType == "ico") {
                QUERY_URL = DiscovererBASE_URL+"ico";
            }
            if (entityType == "system") {
                QUERY_URL = DiscovererBASE_URL+"system";
            }
            if (entityType == "service") {
                QUERY_URL = DiscovererBASE_URL+"service";
            }
            //console.log(QUERY_URL);
            //TextQuery = document.getElementById("TextQueryID").value;

            TextQuery = qu.TextQueryID;
            console.log(TextQuery);

            $http({
                method: 'POST',
                url: QUERY_URL,
                headers: {'Content-Type': 'application/json' ,
                    //'Access-Control-Allow-Origin': '*',
                    'Cookie': 'vitalAccessToken=AQIC5wM2LY4Sfcxg-WQ0ZsIyu6g3BJo8EsbQIv42BfWUZHE.*AAJTSQACMDEAAlNLABM2Nzg1NDgxMzY2NjUzNTI0MzQ3*',
                },
                data: TextQuery
            }).then(function(response){
                $rootScope.ResultQueries = response.data;
                console.log(response);
            });


        }

    }

})();
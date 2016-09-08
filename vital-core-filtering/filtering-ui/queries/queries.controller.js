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

            var FilteringBASE_URL="http://localhost:8080/filtering/";
            var QUERY_URL;
            var TextQuery;
            console.log(entityType);

            //var entityType = document.getElementById("SelectEntityID").value;
            var entityType = qu.SelectEntityID;
            console.log(entityType);

            if (entityType == "resampling") {
                QUERY_URL = FilteringBASE_URL+"resampling";
            }
            if (entityType == "threshold") {
                QUERY_URL = FilteringBASE_URL+"threshold";
            }
            //console.log(QUERY_URL);
            //TextQuery = document.getElementById("TextQueryID").value;

            TextQuery = qu.TextQueryID;

            $http({
                method: 'POST',
                url: QUERY_URL,
                headers: {'Content-Type': 'application/json' ,
                    'Access-Control-Allow-Origin': '*',
                    'Cookie': 'vitalAccessToken=AQIC5wM2LY4SfcwpWRYlK-UGegq__NjIMmrmw_WzmlRiyaQ.*AAJTSQACMDEAAlNLABMtMTc1MTY4MjgwMjY0MzQ3NDQx*',
                },
                data: TextQuery
            }).then(function(response){
                $rootScope.ResultQueries = response.data;
                console.log(response);
            });


        }

    }

})();
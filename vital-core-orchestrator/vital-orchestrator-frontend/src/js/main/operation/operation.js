'use strict';
angular.module('app.main.operation', [
    'ngRoute'
])
    .config(['$routeProvider', function($routeProvider) {

        $routeProvider.when('/operation/list', {
            templateUrl: 'main/operation/operation-list.tpl.html',
            controller: 'OperationListController',
            controllerAs: 'operationListCtrl',
            resolve: {
                operationList: [
                    '$route', 'operationResource',
                    function($route, operationResource) {
                        return operationResource.fetchList();
                    }
                ]
            }
        });

        $routeProvider.when('/operation/edit/:id?', {
            templateUrl: 'main/operation/operation-edit.tpl.html',
            controller: 'OperationEditController',
            controllerAs: 'operationEditCtrl',
            resolve: {
                operation: [
                    '$route', 'operationResource',
                    function($route, operationResource) {
                        var id = $route.current.params.id;
                        if (!!id) {
                            // Just retrieve from server
                            return operationResource.fetch(id);
                        } else {
                            return {
                                'script': 'function execute(input) {\n' +
                                '\tvar output;\n' +
                                '\n' +
                                '\t/*** Insert you code here ***/\n' +
                                '\t \n' +
                                '\t \n' +
                                '\t \n  ' +
                                '\t/*** End: Insert you code here ***/\n' +
                                '\n' +
                                '\treturn output;\n' +
                                '}\n'
                            };
                        }
                    }
                ]
            }
        });

    }])

    .controller('OperationListController', [
        '$scope', 'operationResource', 'operationList',
        function($scope, operationResource, operationList) {
            var operationListCtrl = this;
            operationListCtrl.operationList = operationList;
            operationListCtrl.state = {
                errors: []
            };
            operationListCtrl.actions = {
                delete: function(operation) {
                    operationListCtrl.state.errors.length = 0;
                    operationResource.delete(operation.id)
                        .then(function(saveOperation) {
                            operationListCtrl.operationList.splice(operationListCtrl.operationList.indexOf(operation), 1);
                        }, function(error) {
                            operationListCtrl.state.errors.push(error);
                        });
                }
            };
        }
    ])


    .controller('OperationEditController', [
        '$scope', '$location', 'operationResource', 'operation',
        function($scope, $location, operationResource, operation) {
            var thisCtrl = this;
            thisCtrl.options = {
                cmOption: {
                    script: {
                        theme: 'solarized',
                        lineNumbers: true,
                        indentWithTabs: true,
                        mode: 'javascript'
                    },
                    inputData: {
                        theme: 'solarized',
                        lineNumbers: true,
                        indentWithTabs: true,
                        mode: 'javascript'
                    }
                },
                errors: []
            };
            thisCtrl.operation = operation;
            thisCtrl.output = null;

            thisCtrl.actions = {
                save: function(ngFormController) {
                    if (ngFormController.$invalid) {
                        return;
                    }
                    thisCtrl.options.errors.length = 0;
                    operationResource.save(thisCtrl.operation)
                        .then(function(savedOperation) {
                            angular.copy(savedOperation, thisCtrl.operation);
                            ngFormController.$setPristine();
                        }, function(error) {
                            thisCtrl.options.errors.push(error);
                        });
                },
                execute: function() {
                    thisCtrl.output = null;
                    thisCtrl.options.errors.length = 0;
                    operationResource.execute(thisCtrl.operation, angular.fromJson(thisCtrl.operation.inputData))
                        .then(function(output) {
                            thisCtrl.output = output;
                        }, function(error) {
                            thisCtrl.options.errors.push(error);
                        });
                }
            };

        }
    ])

    .directive('operationOutput', function() {
        return {
            restrict: 'E',
            replace: true,
            templateUrl: 'main/operation/operation-output.tpl.html',
            scope: {
                'output': '=',
            },
            transclude: true,
            controller: [
                '$scope',
                function($scope) {
                    this.id = Math.floor(Math.random() * 1000) + (new Date()).getTime();
                    this.output = $scope.output;
                }
            ],
            controllerAs: 'operationOutputCtrl'
        };
    })

    .factory('operationResource', [
        '$http', '$q', 'SERVICE_URL',
        function($http, $q, SERVICE_URL) {
            var ROOT_URL = SERVICE_URL + '/operation';

            var EXECUTE_URL = SERVICE_URL + '/execute';

            // The public API of the operation
            var operation = {

                fetchList: function() {
                    return $http.get(ROOT_URL)
                        .then(function(response) {
                            return response.data;
                        });
                },

                fetch: function(id) {
                    return $http.get(ROOT_URL + '/' + id)
                        .then(function(response) {
                            return response.data;
                        });
                },

                save: function(operation) {
                    if (!!operation.id) {
                        return $http.put(ROOT_URL + '/' + operation.id, operation)
                            .then(function(response) {
                                return response.data;
                            });
                    } else {
                        return $http.post(ROOT_URL, operation)
                            .then(function(response) {
                                return response.data;
                            });
                    }
                },

                delete: function(id) {
                    return $http.delete(ROOT_URL + '/' + id);
                },

                execute: function(operation, input) {
                    return $http.post(EXECUTE_URL + '/operation/', {
                        operation: operation,
                        input: input
                    }).then(function(response) {
                        return response.data;
                    });
                }
            };

            return operation;
        }
    ]);

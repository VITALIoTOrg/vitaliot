'use strict';
angular.module('app.main.workflow', [
    'ngRoute'
])
    .config(['$routeProvider', function($routeProvider) {

        $routeProvider.when('/workflow/list', {
            templateUrl: 'main/workflow/workflow-list.tpl.html',
            controller: 'WorkflowListController',
            controllerAs: 'workflowListCtrl',
            resolve: {
                workflowList: [
                    '$route', 'workflowResource',
                    function($route, workflowResource) {
                        return workflowResource.fetchList();
                    }
                ]
            }
        });

        $routeProvider.when('/workflow/edit/:id?', {
            templateUrl: 'main/workflow/workflow-edit.tpl.html',
            controller: 'WorkflowEditController',
            controllerAs: 'workflowEditCtrl',
            resolve: {
                workflow: [
                    '$route', 'workflowResource',
                    function($route, workflowResource) {
                        var id = $route.current.params.id;
                        if (!!id) {
                            // Just retrieve from server
                            return workflowResource.fetch(id);
                        } else {
                            return {
                                'name': '',
                                'status': 'DISABLED',
                                'operationList': []
                            };
                        }
                    }
                ],
                operationList: [
                    '$route', 'operationResource',
                    function($route, operationResource) {
                        return operationResource.fetchList();
                    }
                ]
            }
        });

        $routeProvider.when('/workflow/execute/:id', {
            templateUrl: 'main/workflow/workflow-execute.tpl.html',
            controller: 'WorkflowExecuteController',
            controllerAs: 'workflowExecuteCtrl',
            resolve: {
                workflow: [
                    '$route', 'workflowResource',
                    function($route, workflowResource) {
                        return workflowResource.fetch($route.current.params.id);

                    }
                ]
            }
        });
    }])

    .controller('WorkflowListController', [
        '$scope', 'workflowResource', 'workflowList',
        function($scope, workflowResource, workflowList) {
            var workflowListCtrl = this;
            workflowListCtrl.workflowList = workflowList;
            workflowListCtrl.state = {
                errors: []
            };
            workflowListCtrl.actions = {
                createMetaservice: function(workflow) {
                    var metaservice = {
                        workflow: angular.copy(workflow)
                    };
                    workflowListCtrl.state.errors.length = 0;
                    workflowResource.createMetaservice(metaservice)
                        .then(function(savedMetaservice) {
                            workflowListCtrl.state.errors.push("Deployed");
                        }, function(error) {
                            workflowListCtrl.state.errors.push(error);
                        });
                },
                delete: function(workflow) {
                    workflowListCtrl.state.errors.length = 0;
                    workflowResource.delete(workflow.id)
                        .then(function(saveWorkflow) {
                            workflowListCtrl.workflowList.splice(workflowListCtrl.workflowList.indexOf(workflow), 1);
                        }, function(error) {
                            workflowListCtrl.state.errors.push(error);
                        });
                }
            };
        }
    ])


    .controller('WorkflowEditController', [
        '$scope', '$location', 'workflowResource', 'workflow', 'operationList',
        function($scope, $location, workflowResource, workflow, operationList) {
            var thisCtrl = this;

            thisCtrl.workflow = workflow;
            thisCtrl.output = null;
            thisCtrl.data = {
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
                errors: [],
                operationList: operationList,
                operation: null
            };

            thisCtrl.actions = {
                addOperation: function(operation) {
                    workflow.operationList.push(angular.copy(operation));
                },
                removeOperation: function(operation) {
                    workflow.operationList.splice(workflow.operationList.indexOf(operation), 1);
                },
                save: function(ngFormController) {
                    if (ngFormController.$invalid) {
                        return;
                    }
                    workflowResource.save(thisCtrl.workflow)
                        .then(function(savedWorkflow) {
                            angular.copy(savedWorkflow, thisCtrl.workflow);
                        }, function(error) {

                        });
                },
                execute: function() {
                    var inputData = angular.fromJson(thisCtrl.workflow.operationList[0].inputData);
                    thisCtrl.output = null;
                    thisCtrl.data.errors.length = 0;
                    workflowResource.execute(thisCtrl.workflow, inputData)
                        .then(function(output) {
                            thisCtrl.output = output;
                        }, function(error) {
                            thisCtrl.options.errors.push(error);
                        });
                }
            };

        }
    ])

    .filter('jsonString', [
        '$filter',
        function($filter) {
            return function(jsonString) {
                var obj = angular.fromJson(jsonString);
                return $filter('json')(obj);
            };
        }
    ])

    .factory('workflowResource', [
        '$http', '$q', 'SERVICE_URL', 'metaserviceResource',
        function($http, $q, SERVICE_URL, metaserviceResource) {
            var ROOT_URL = SERVICE_URL + '/workflow';
            var EXECUTE_URL = SERVICE_URL + '/execute';

            // The public API of the workflow
            var workflow = {

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

                save: function(workflow) {
                    if (!!workflow.id) {
                        return $http.put(ROOT_URL + '/' + workflow.id, workflow)
                            .then(function(response) {
                                return response.data;
                            });
                    } else {
                        return $http.post(ROOT_URL, workflow)
                            .then(function(response) {
                                return response.data;
                            });
                    }

                },

                delete: function(id) {
                    return $http.delete(ROOT_URL + '/' + id);
                },

                createMetaservice: metaserviceResource.save,

                execute: function(workflow, input) {
                    return $http.post(EXECUTE_URL + '/workflow', {
                        input: input,
                        workflow: workflow
                    }).then(function(response) {
                        return response.data;
                    });
                }
            };

            return workflow;
        }
    ]);

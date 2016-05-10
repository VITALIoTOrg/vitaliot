'use strict';
angular.module('main.workflow', [])

    .config(['$routeProvider', function ($routeProvider) {

        $routeProvider.when('/workflow/list', {
            templateUrl: 'main/workflow/workflow-list.tpl.html',
            controller: 'WorkflowListController',
            controllerAs: 'workflowListCtrl',
            resolve: {
                workflowList: [
                    '$route', 'workflowResource',
                    function ($route, workflowResource) {
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
                    function ($route, workflowResource) {
                        var id = $route.current.params.id;
                        if (!!id) {
                            // Just retrieve from server
                            return workflowResource.fetch(id);
                        } else {
                            return {
                                'name': '',
                                'status': 'DISABLED',
                                'nodes': {
                                    input: {
                                        name: 'Start',
                                        inputData: null,
                                        incoming: []
                                    },
                                    output: {
                                        name: 'End',
                                        incoming: ['input']
                                    }
                                }
                            };
                        }
                    }
                ],
                operationList: [
                    '$route', 'operationResource',
                    function ($route, operationResource) {
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
                    function ($route, workflowResource) {
                        return workflowResource.fetch($route.current.params.id);

                    }
                ]
            }
        });
    }])

    .controller('WorkflowListController', [
        '$scope', 'workflowResource', 'workflowList',
        function ($scope, workflowResource, workflowList) {
            var workflowListCtrl = this;
            workflowListCtrl.workflowList = workflowList;
            workflowListCtrl.state = {
                errors: []
            };
            workflowListCtrl.actions = {
                createMetaservice: function (workflow) {
                    var metaservice = {
                        workflow: angular.copy(workflow)
                    };
                    workflowListCtrl.state.errors.length = 0;
                    workflowResource.createMetaservice(metaservice)
                        .then(function (savedMetaservice) {
                            workflowListCtrl.state.errors.push('Deployed');
                        }, function (error) {
                            workflowListCtrl.state.errors.push(error);
                        });
                },
                delete: function (workflow) {
                    workflowListCtrl.state.errors.length = 0;
                    workflowResource.delete(workflow.id)
                        .then(function (saveWorkflow) {
                            workflowListCtrl.workflowList.splice(workflowListCtrl.workflowList.indexOf(workflow), 1);
                        }, function (error) {
                            workflowListCtrl.state.errors.push(error);
                        });
                }
            };
        }
    ])


    .controller('WorkflowEditController', [
        '$scope', '$location', 'workflowResource', 'workflow', 'operationList',
        function ($scope, $location, workflowResource, workflow, operationList) {
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
                currentNodeList: function () {
                    return _.keys(thisCtrl.workflow.nodes);
                },
                incomingNodeName: null,
                newNodeName: null,
                operation: null
            };

            thisCtrl.actions = {
                addNode: function (operation) {
                    function nextName() {
                        var i = _.keys(thisCtrl.workflow.nodes).length - 2;
                        var nextName = 'operation' + i;
                        while (_.isObject(thisCtrl.workflow.nodes[nextName])) {
                            i += 1;
                            nextName = 'operation' + i;
                        }
                        return nextName;
                    }

                    var nodeName = nextName();
                    var operation = operation || {
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
                    var node = {
                        operation: angular.copy(operation),
                        incoming: []
                    };
                    workflow.nodes[nodeName] = node;
                },
                removeNode: function (name) {
                    if (name === 'input' || name === 'output') {
                        // Cannot delete the input and output nodes
                        return;
                    }
                    delete workflow.nodes[name];
                },
                addEdge: function (fromNodeName, toNodeName) {
                    workflow.nodes[toNodeName].incoming.push(fromNodeName);
                },
                removeEdge: function (fromNodeName, toNodeName) {
                    var index = workflow.nodes[toNodeName].incoming.indexOf(fromNodeName);
                    workflow.nodes[toNodeName].incoming.splice(index, 1);
                },
                save: function (ngFormController) {
                    // Check form
                    if (ngFormController.$invalid) {
                        return;
                    }
                    // Check that the graph is connected (path exists from input to output)


                    //
                    workflowResource.save(thisCtrl.workflow)
                        .then(function (savedWorkflow) {
                            angular.copy(savedWorkflow, thisCtrl.workflow);
                        }, function (error) {

                        });
                },
                execute: function () {
                    var inputData = angular.fromJson(thisCtrl.workflow.nodes.input.inputData);
                    thisCtrl.output = null;
                    thisCtrl.data.errors.length = 0;
                    workflowResource.execute(thisCtrl.workflow, inputData)
                        .then(function (output) {
                            thisCtrl.output = output;
                        }, function (error) {
                            thisCtrl.options.errors.push(error);
                        });
                }
            };

        }
    ])

    .filter('jsonString', [
        '$filter',
        function ($filter) {
            return function (jsonString) {
                var obj = angular.fromJson(jsonString);
                return $filter('json')(obj);
            };
        }
    ])

    .factory('workflowResource', [
        '$http', '$q', 'SERVICE_URL', 'metaserviceResource',
        function ($http, $q, SERVICE_URL, metaserviceResource) {
            var ROOT_URL = SERVICE_URL + '/workflow';
            var EXECUTE_URL = SERVICE_URL + '/execute';

            // The public API of the workflow
            var workflow = {

                fetchList: function () {
                    return $http.get(ROOT_URL)
                        .then(function (response) {
                            return response.data;
                        });
                },

                fetch: function (id) {
                    return $http.get(ROOT_URL + '/' + id)
                        .then(function (response) {
                            return response.data;
                        });
                },

                save: function (workflow) {
                    if (!!workflow.id) {
                        return $http.put(ROOT_URL + '/' + workflow.id, workflow)
                            .then(function (response) {
                                return response.data;
                            });
                    } else {
                        return $http.post(ROOT_URL, workflow)
                            .then(function (response) {
                                return response.data;
                            });
                    }

                },

                delete: function (id) {
                    return $http.delete(ROOT_URL + '/' + id);
                },

                createMetaservice: metaserviceResource.save,

                execute: function (workflow, input) {
                    return $http.post(EXECUTE_URL + '/workflow', {
                        input: input,
                        workflow: workflow
                    }).then(function (response) {
                        return response.data;
                    });
                }
            };

            return workflow;
        }
    ]);

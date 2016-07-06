'use strict';
angular.module('main.workflow', [])

/*****************
 * Configuration
 *****************/

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

    /*****************
     * Controllers
     *****************/

    .controller('WorkflowEditController', [
        '$scope', '$location', 'workflowResource', 'workflow', 'operationList',
        function ($scope, $location, workflowResource, workflow, operationList) {
            var thisCtrl = this;

            thisCtrl.workflow = workflow;
            thisCtrl.output = null;
            thisCtrl.data = {
                cmOption: {
                    script: {
                        theme: 'solarized light',
                        lineNumbers: true,
                        indentWithTabs: true,
                        mode: 'text/javascript'
                    },
                    inputData: {
                        theme: 'solarized light',
                        lineNumbers: true,
                        indentWithTabs: true,
                        mode: 'application/json'
                    }
                },
                errors: [],
                operationList: operationList,
                selectedNodeName: 'input',
                selectedOperation: null,
                currentNodeList: function () {
                    return _.keys(thisCtrl.workflow.nodes);
                },
                incomingNodeList: function (currentNodeName) {
                    // Remove output, nodeName and nodes that are already use nodeName as input
                    var list = _.filter(_.keys(thisCtrl.workflow.nodes), function (nodeName) {
                        if (nodeName === 'output' || nodeName === currentNodeName) {
                            return false;
                        }
                        return thisCtrl.workflow.nodes[nodeName].incoming.indexOf(currentNodeName) < 0;
                    });
                    return list;
                }
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
                    thisCtrl.workflow.nodes[nodeName] = node;
                },
                removeNode: function (name) {
                    if (name === 'input' || name === 'output') {
                        // Cannot delete the input and output nodes
                        return;
                    }
                    delete thisCtrl.workflow.nodes[name];

                    // Also delete all edges to other nodes
                    _.forEach(thisCtrl.workflow.nodes, function (node, nodeName) {
                        thisCtrl.actions.removeEdge(name, nodeName);
                    });
                },
                addEdge: function (fromNodeName, toNodeName) {
                    thisCtrl.workflow.nodes[toNodeName].incoming.push(fromNodeName);
                },
                removeEdge: function (fromNodeName, toNodeName) {
                    var index = workflow.nodes[toNodeName].incoming.indexOf(fromNodeName);
                    if (index >= 0) {
                        thisCtrl.workflow.nodes[toNodeName].incoming.splice(index, 1);
                    }
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

    /************
     * Services
     ***********/

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
    ])

    /************
     * Filters
     ***********/

    .filter('jsonString', [
        '$filter',
        function ($filter) {
            return function (jsonString) {
                var obj = angular.fromJson(jsonString);
                return $filter('json')(obj);
            };
        }
    ])

    /*************
     * Directives
     *************/

    .directive('workflowGraph', function () {
        return {
            restrict: 'E',
            replace: true,
            template: '<div></div>',
            scope: {
                nodes: '=',
                selectedNodeName: '='
            },
            link: function (scope, element, attrs) {

                var width = element.parent().width() || 960;
                var height = 200;
                var graph = {
                    nodes: [],
                    links: []
                };

                var svg = d3.select(element[0]).append('svg')
                    .attr('width', width)
                    .attr('height', height);
                var force = d3.layout.force()
                    .nodes(graph.nodes)
                    .links(graph.links)
                    .size([width, height])
                    .linkDistance(150)
                    .charge(-300)
                    .on('tick', tick);

                // build the arrow.
                svg.append('svg:defs')
                    .selectAll('marker')
                    .data(['end'])      // Different link/path types can be defined here
                    .enter().append('svg:marker')    // This section adds in the arrows
                    .attr('id', String)
                    .attr('viewBox', '0 -5 10 10')
                    .attr('refX', 15)
                    .attr('refY', -1.5)
                    .attr('markerWidth', 6)
                    .attr('markerHeight', 6)
                    .attr('orient', 'auto')
                    .append('svg:path')
                    .attr('d', 'M0,-5L10,0L0,5');

                // add the links and the arrows
                var path = svg.append('svg:g').selectAll('path');
                // define the nodes
                var node = svg.selectAll('.node');

                // add the curvy lines
                function tick() {
                    path.attr('d', function (d) {
                        var dx = d.target.x - d.source.x,
                            dy = d.target.y - d.source.y,
                            dr = Math.sqrt(dx * dx + dy * dy);
                        return 'M' +
                            d.source.x + ',' +
                            d.source.y + 'A' +
                            dr + ',' + dr + ' 0 0,1 ' +
                            d.target.x + ',' +
                            d.target.y;
                    });

                    node.attr('transform', function (d) {
                        return 'translate(' + d.x + ',' + d.y + ')';
                    });
                }

                function restart() {
                    path = path.data(force.links());
                    path.exit().remove();
                    path.enter()
                        .append('svg:path')
                        .attr('class', 'link')
                        .attr('marker-end', 'url(#end)');

                    // define the nodes
                    node = node.data(force.nodes());
                    node.classed('selected', function (nodeData) {
                        return scope.selectedNodeName === nodeData.name;
                    });
                    node.exit().remove();
                    var g = node.enter().append('g')
                        .attr('class', 'node')
                        .classed('input', function (nodeData) {
                            return nodeData.name === 'input';
                        })
                        .classed('output', function (nodeData) {
                            return nodeData.name === 'output';
                        })
                        .on('click', function (nodeData) {
                            scope.$applyAsync(function () {
                                scope.selectedNodeName = scope.selectedNodeName === nodeData.name ? null : nodeData.name;
                            });
                            restart();
                        })
                        .call(force.drag);
                    g.append('circle')
                        .attr('r', 5);
                    g.append('text')
                        .attr('x', 10)
                        .attr('dy', '.35em')
                        .text(function (d) {
                            return d.name;
                        });

                    force.start();
                }

                // functions
                function workflowToGraph(nodesMap) {
                    var graph = {
                        nodes: _.flatMap(nodesMap, function (value, key) {
                            var copy = _.cloneDeep(value);
                            copy.name = key;
                            if (copy.name === 'input') {
                                copy.fixed = true;
                                copy.x = 10;
                                copy.y = (height - 10) / 2;
                            }
                            if (copy.name === 'output') {
                                copy.fixed = true;
                                copy.x = width - 60;
                                copy.y = (height - 10) / 2;
                            }
                            return copy;
                        }),
                        links: []
                    };

                    _.forEach(graph.nodes, function (node) {
                        _.forEach(node.incoming, function (incomingNodeName) {
                            var link = {
                                source: _.find(graph.nodes, {name: incomingNodeName}),
                                target: node
                            };
                            graph.links.push(link);
                        });
                    });
                    return graph;
                }

                //watchers
                scope.$watch('nodes', function (newNodes) {
                    var newGraph = workflowToGraph(newNodes);

                    // Copy to existing array, so that force will detect the changes
                    graph.nodes.length = 0;
                    _.forEach(newGraph.nodes, function (node) {
                        graph.nodes.push(node);
                    });
                    graph.links.length = 0;
                    _.forEach(newGraph.links, function (link) {
                        graph.links.push(link);
                    });

                    // Redraw
                    restart();
                }, true);


                scope.$watch('selectedNodeName', function () {
                    // Redraw
                    restart();
                });
            }


        };

        // end: functions
    });


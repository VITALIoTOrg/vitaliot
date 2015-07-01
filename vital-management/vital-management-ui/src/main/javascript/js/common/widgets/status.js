'use strict';
angular.module('common.widgets.status', [])

    .directive('widgetStatusSensor', [
        '$interval', 'sensorResource',
        function($interval, sensorResource) {
            return {
                restrict: 'EA',
                replace: true,
                templateUrl: 'common/widgets/status.tpl.html',
                scope: {
                    sensor: '='
                },
                link: function(scope, element, attrs) {
                    scope.data = {
                        status: '...s'
                    };

                    scope.actions = {
                        fetchStatus: function() {
                            scope.data.status = '...';
                            sensorResource.fetchStatus(scope.sensor['@id'])
                                .then(function(statusObj) {
                                    scope.data.status = statusObj['http://purl.oclc.org/NET/ssnx/ssn#observationResult']
                                        ['http://purl.oclc.org/NET/ssnx/ssn#hasValue']
                                        ['http://vital-iot.eu/ontology/ns/value'];

                                    scope.data.date = statusObj['http://purl.oclc.org/NET/ssnx/ssn#observationResultTime']
                                        ['http://www.w3.org/2006/time#inXSDDateTime']
                                        ['@value'];
                                });
                        }
                    };

                    // Init:
                    scope.actions.fetchStatus();
                }
            };
        }
    ])

    .directive('widgetStatusSystem', [
        '$interval', 'systemResource',
        function($interval, systemResource) {
            return {
                restrict: 'EA',
                replace: true,
                templateUrl: 'common/widgets/status.tpl.html',
                scope: {
                    system: '='
                },
                link: function(scope, element, attrs) {
                    scope.data = {
                        status: '...s'
                    };

                    scope.actions = {
                        fetchStatus: function() {
                            scope.data.status = '...';
                            systemResource.fetchStatus(scope.system['@id'])
                                .then(function(statusObj) {
                                    scope.data.status = statusObj['http://purl.oclc.org/NET/ssnx/ssn#observationResult']
                                        ['http://purl.oclc.org/NET/ssnx/ssn#hasValue']
                                        ['http://vital-iot.eu/ontology/ns/value'];

                                    scope.data.date = statusObj['http://purl.oclc.org/NET/ssnx/ssn#observationResultTime']
                                        ['http://www.w3.org/2006/time#inXSDDateTime']
                                        ['@value'];
                                });
                        }
                    };

                    // Init:
                    scope.actions.fetchStatus();
                }
            };
        }
    ]);

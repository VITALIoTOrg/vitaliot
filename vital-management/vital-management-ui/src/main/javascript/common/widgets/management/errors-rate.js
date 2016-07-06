'use strict';
angular.module('common.widgets.errors-rate', ['common.resources.observation'])

    .directive('widgetErrorsRate', [
        '$interval', 'managementResource',
        function($interval, managementResource) {
            return {
                restrict: 'EA',
                templateUrl: 'common/widgets/management/errors-rate.tpl.html',
                scope: {
                    system: '=',
                    supportedMetrics: '='
                },
                link: function(scope, element, attrs) {
                    // Validate
                    if (!_.has(scope.supportedMetrics, 'http://vital-iot.eu/ontology/ns/Errors')) {
                        return;
                    }

                    var data = [];
                    var historyChart;
                    var prev = {
                        timestamp: 0,
                        errors: 0
                    };

                    // Validate:
                    if (!_.has(scope.supportedMetrics, 'http://vital-iot.eu/ontology/ns/Errors')) {
                        return;
                    }

                    // Init:
                    historyChart = Morris.Line({
                        element: element.find('div[data-chart=errors-rate]'),
                        data: [],
                        xkey: 'timestamp',
                        ykeys: ['errorsPerSecond'],
                        labels: ['Error History'],
                        hideHover: 'auto'
                    });

                    var interval = $interval(function() {
                        managementResource.fetchPerformanceMetric(
                            scope.system['@id'], [
                                'http://vital-iot.eu/ontology/ns/Errors'
                            ])
                            .then(function(observation) {
                                var current = {
                                    timestamp: observation[0]['http://purl.oclc.org/NET/ssnx/ssn#observationResultTime']
                                        ['http://www.w3.org/2006/time#inXSDDateTime']
                                        ['@value'],
                                    errors: observation[0]['http://purl.oclc.org/NET/ssnx/ssn#observationResult']
                                        ['http://purl.oclc.org/NET/ssnx/ssn#hasValue']
                                        ['http://vital-iot.eu/ontology/ns/value']
                                };
                                if (new Date(current.timestamp).getSeconds() - new Date(prev.timestamp).getSeconds() === 0) {
                                    return;
                                }
                                var result = {
                                    timestamp: current.timestamp,
                                    errorsPerSecond: (current.errors - prev.errors) / (new Date(current.timestamp).getSeconds() - new Date(prev.timestamp).getSeconds())
                                };

                                data.push(result);
                                if (data.length > 20) {
                                    data.shift();
                                }

                                historyChart.setData(data);

                                prev.timestamp = current.timestamp;
                                prev.errors = current.errors;
                            });
                    }, 5000);

                    scope.$on('$destroy', function(event) {
                            $interval.cancel(interval);
                        }
                    );
                }
            };
        }
    ]);

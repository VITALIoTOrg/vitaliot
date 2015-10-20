'use strict';
angular.module('common.widgets.throughput', ['common.resources.observation'])

    .directive('widgetThroughput', [
        '$interval', 'managementResource',
        function($interval, managementResource) {
            return {
                restrict: 'EA',
                templateUrl: 'common/widgets/management/throughput.tpl.html',
                scope: {
                    system: '=',
                    supportedMetrics: '='
                },
                link: function(scope, element, attrs) {
                    // Validate
                    if (!_.has(scope.supportedMetrics, 'http://vital-iot.eu/ontology/ns/ServedRequests')) {
                        return;
                    }

                    var data = [];
                    var historyChart = Morris.Line({
                        element: element.find('div[data-chart=throughput]'),
                        data: [],
                        xkey: 'timestamp',
                        ykeys: ['value'],
                        labels: ['Throughput History'],
                        hideHover: 'auto'
                    });
                    var prev = {
                        timestamp: 0,
                        load: 0
                    };

                    // Init
                    var interval = $interval(function() {

                        managementResource.fetchPerformanceMetric(
                            scope.system['@id'], [
                                'http://vital-iot.eu/ontology/ns/ServedRequests'
                            ])
                            .then(function(results) {
                                var current = {
                                    timestamp: results[0]['http://purl.oclc.org/NET/ssnx/ssn#observationResultTime']
                                        ['http://www.w3.org/2006/time#inXSDDateTime']
                                        ['@value'],
                                    load: results[0]['http://purl.oclc.org/NET/ssnx/ssn#observationResult']
                                        ['http://purl.oclc.org/NET/ssnx/ssn#hasValue']
                                        ['http://vital-iot.eu/ontology/ns/value']
                                };
                                if (new Date(current.timestamp).getSeconds() - new Date(prev.timestamp).getSeconds() === 0) {
                                    return;
                                }


                                var result = {
                                    timestamp: current.timestamp,
                                    value: (current.load - prev.load) / (new Date(current.timestamp).getSeconds() - new Date(prev.timestamp).getSeconds())
                                };
                                data.push(result);
                                if (data.length > 20) {
                                    data.shift();
                                }

                                historyChart.setData(data);

                                prev.timestamp = current.timestamp;
                                prev.load = current.load;

                            });
                    }, 5000);

                    scope.$on('$destroy', function(event) {
                            $interval.cancel(interval);
                        }
                    );
                }
            };
        }
    ])
;

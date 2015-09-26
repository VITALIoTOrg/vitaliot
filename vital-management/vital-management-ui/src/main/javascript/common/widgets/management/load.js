'use strict';
angular.module('common.widgets.load', ['common.resources.observation'])

    .directive('widgetLoad', [
        '$interval', 'managementResource',
        function($interval, managementResource) {
            return {
                restrict: 'EA',
                templateUrl: 'common/widgets/management/load.tpl.html',
                scope: {
                    system: '=',
                    supportedMetrics: '='
                },
                link: function(scope, element, attrs) {
                    var data = [];
                    var currentChart, historyChart;

                    // Validate
                    if (!_.has(scope.supportedMetrics, 'http://vital-iot.eu/ontology/ns/SysLoad')) {
                        return;
                    }

                    // Init:
                    currentChart = Morris.Donut({
                        element: element.find('div[data-chart=load-current]'),
                        data: [
                            {
                                label: 'Current',
                                value: 0
                            }
                        ],
                        colors: ['#3c8dbc']
                    });

                    historyChart = Morris.Line({
                        element: element.find('div[data-chart=load-history]'),
                        data: [],
                        xkey: 'timestamp',
                        ykeys: ['load'],
                        labels: ['Load History'],
                        hideHover: 'auto'
                    });


                    var interval = $interval(function() {
                        managementResource.fetchPerformanceMetric(
                            scope.system['@id'],
                            ['http://vital-iot.eu/ontology/ns/SysLoad']
                        ).then(function(observation) {
                                var datum = {
                                    timestamp: observation[0]['http://purl.oclc.org/NET/ssnx/ssn#observationResultTime']
                                        ['http://www.w3.org/2006/time#inXSDDateTime']
                                        ['@value'],
                                    load: observation[0]['http://purl.oclc.org/NET/ssnx/ssn#observationResult']
                                        ['http://purl.oclc.org/NET/ssnx/ssn#hasValue']
                                        ['http://vital-iot.eu/ontology/ns/value']
                                };

                                if (!(data.length && datum.timestamp === data[data.length - 1].timestamp)) {
                                    data.push(datum);
                                }
                                if (data.length > 20) {
                                    data.shift();
                                }

                                historyChart.setData(data);
                                currentChart.setData([
                                    {
                                        label: 'Current',
                                        value: datum.load
                                    }

                                ]);
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

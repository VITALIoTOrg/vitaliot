'use strict';
angular.module('common.widgets.memory', ['common.resources.observation'])

    .directive('widgetMemory', [
        '$interval', '$q', 'managementResource',
        function($interval, $q, managementResource) {

            return {
                restrict: 'EA',
                templateUrl: 'common/widgets/management/memory.tpl.html',
                scope: {
                    system: '=',
                    supportedMetrics: '='
                },
                link: function(scope, element, attr) {
                    var chart;

                    // Validate supported:
                    if (!_.has(scope.supportedMetrics, 'http://vital-iot.eu/ontology/ns/AvailableMem') ||
                        !_.has(scope.supportedMetrics, 'http://vital-iot.eu/ontology/ns/UsedMem')) {
                        return;
                    }

                    // Init:
                    chart = Morris.Donut({
                        data: [
                            {
                                label: 'used',
                                value: 1
                            },
                            {
                                label: 'available',
                                value: 1
                            }
                        ],
                        element: element.find('div[data-chart=memory]'),
                        colors: ['red', 'green']
                    });

                    var interval = $interval(function() {
                        managementResource.fetchPerformanceMetric(
                            scope.system['@id'],
                            [
                                'http://vital-iot.eu/ontology/ns/AvailableMem',
                                'http://vital-iot.eu/ontology/ns/UsedMem'
                            ]
                        ).then(function(data) {
                                getMemoryData.apply(this, data);
                            });
                    }, 10000);

                    scope.$on('$destroy', function(event) {
                            $interval.cancel(interval);
                        }
                    );

                    function getMemoryData(memoryAvailable, memoryUsed) {
                        var data = [
                            {
                                label: 'used',
                                value: memoryUsed['http://purl.oclc.org/NET/ssnx/ssn#observationResult']
                                    ['http://purl.oclc.org/NET/ssnx/ssn#hasValue']
                                    ['http://vital-iot.eu/ontology/ns/value']
                            },
                            {
                                label: 'available',
                                value: memoryAvailable['http://purl.oclc.org/NET/ssnx/ssn#observationResult']
                                    ['http://purl.oclc.org/NET/ssnx/ssn#hasValue']
                                    ['http://vital-iot.eu/ontology/ns/value']
                            }
                        ];

                        chart.setData(data);
                        chart.select(0);
                    }
                }
            };
        }
    ]);

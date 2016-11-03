'use strict';
angular.module('main.governance.trust.widgets.sla', [
    'common.resources.observation'
])

    .directive('widgetSla', [
        '$interval', '$q', 'managementResource',
        function($interval, $q, managementResource) {
            return {
                restrict: 'EA',
                templateUrl: 'main/governance/trust/widgets/sla.tpl.html',
                scope: {
                    system: '=',
                    supportedMetrics: '='
                },
                link: function(scope, element, attrs) {
                    // Validate
                    var chart = Morris.Bar({
                        element: element.find('div[data-chart=sla]'),
                        data: [],
                        xkey: 'label',
                        ykeys: ['value'],
                        labels: ['SLA Parameters']
                    });


                    // Init
                    var interval = $interval(function() {
                        refresh();
                    }, 10000);

                    scope.$on('$destroy', function(event) {
                            $interval.cancel(interval);
                        }
                    );

                    refresh();

                    function refresh() {
                        managementResource.fetchSlaParameter(
                            scope.system['@id'],
                            _.keys(scope.supportedMetrics)
                        ).then(function(data) {
                            scope.metrics = _.map(data, function(d) {
                                return {
                                    label: d['http://purl.oclc.org/NET/ssnx/ssn#observationProperty']['@type'].replace('http://vital-iot.eu/ontology/ns/', '').replace(/([a-z])([A-Z])/g, '$1 $2'),
                                    value: d['http://purl.oclc.org/NET/ssnx/ssn#observationResult']['http://purl.oclc.org/NET/ssnx/ssn#hasValue']['http://vital-iot.eu/ontology/ns/value'],
                                    time: d['http://purl.oclc.org/NET/ssnx/ssn#observationResultTime']['http://www.w3.org/2006/time#inXSDDateTime']['@value']
                                }
                            });
                            chart.setData(scope.metrics);
                        });
                    }

                }
            };
        }
    ]);

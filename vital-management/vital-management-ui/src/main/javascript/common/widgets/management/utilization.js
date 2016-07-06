'use strict';
angular.module('common.widgets.utilization', ['common.resources.observation'])

    .directive('widgetUtilization', [
        '$interval', '$q', 'managementResource',
        function ($interval, $q, managementResource) {
            return {
                restrict: 'EA',
                templateUrl: 'common/widgets/management/utilization.tpl.html',
                scope: {
                    system: '=',
                    supportedMetrics: '='
                },
                link: function (scope, element, attrs) {
                    // Validate
                    if (!_.has(scope.supportedMetrics, 'http://vital-iot.eu/ontology/ns/ServedRequests') ||
                        !_.has(scope.supportedMetrics, 'http://vital-iot.eu/ontology/ns/MaxRequests')) {
                        return;
                    }

                    scope.utilization = 0;
                    var prev = {
                        timestamp: 0,
                        load: 0
                    };

                    // Init
                    var interval = $interval(function () {
                        managementResource.fetchPerformanceMetric(
                            scope.system['@id'], [
                                'http://vital-iot.eu/ontology/ns/ServedRequests',
                                'http://vital-iot.eu/ontology/ns/MaxRequests'
                            ]).then(function (data) {
                                console.log(data);
                                getUtil.apply(this, data);
                            });
                    }, 5000);

                    scope.$on('$destroy', function (event) {
                            $interval.cancel(interval);
                        }
                    );

                    function getUtil(servedRequests, maxRequests) {
                        var current = {
                            timestamp: servedRequests['http://purl.oclc.org/NET/ssnx/ssn#observationResultTime']
                                ['http://www.w3.org/2006/time#inXSDDateTime']
                                ['@value'],
                            load: servedRequests['http://purl.oclc.org/NET/ssnx/ssn#observationResult']
                                ['http://purl.oclc.org/NET/ssnx/ssn#hasValue']
                                ['http://vital-iot.eu/ontology/ns/value']
                        };
                        var maxRequestsValue = maxRequests['http://purl.oclc.org/NET/ssnx/ssn#observationResult']
                            ['http://purl.oclc.org/NET/ssnx/ssn#hasValue']
                            ['http://vital-iot.eu/ontology/ns/value'];

                        scope.utilization = (current.load - prev.load) / (maxRequestsValue * ((new Date(current.timestamp).getSeconds() - new Date(prev.timestamp).getSeconds())));

                        prev.load = current.load;
                        prev.timestamp = current.timestamp;
                    }
                }
            };
        }
    ]);

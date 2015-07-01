'use strict';
angular.module('common.widgets.errors', ['common.resources.observation'])

    .directive('widgetErrors', [
        '$interval', '$q', 'managementResource',
        function($interval, $q, managementResource) {
            return {
                restrict: 'EA',
                templateUrl: 'common/widgets/management/errors.tpl.html',
                scope: {
                    system: '=',
                    supportedMetrics: '='
                },
                link: function(scope, element, attrs) {
                    scope.errors = 0;


                    function getErrors(error, servedRequest) {
                        var errors = error['http://purl.oclc.org/NET/ssnx/ssn#observationResult']
                            ['http://purl.oclc.org/NET/ssnx/ssn#hasValue']
                            ['http://vital-iot.eu/ontology/ns/value'];
                        var servedRequests = servedRequest['http://purl.oclc.org/NET/ssnx/ssn#observationResult']
                            ['http://purl.oclc.org/NET/ssnx/ssn#hasValue']
                            ['http://vital-iot.eu/ontology/ns/value'];
                        scope.errors = (errors * 100) / servedRequests;
                    }

                    var interval = $interval(function() {

                        managementResource.fetchPerformanceMetric(
                            scope.system['@id'], [
                                'http://vital-iot.eu/ontology/ns/Errors',
                                'http://vital-iot.eu/ontology/ns/ServedRequests'
                            ])
                            .then(function(data) {
                                getErrors.apply(this, data);
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

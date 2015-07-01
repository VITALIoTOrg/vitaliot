'use strict';
angular.module('common.widgets.observations', [])

    .directive('widgetObservations', [
        '$interval', 'sensorResource', 'observationResource',
        function($interval, sensorResource, observationResource) {
            return {
                restrict: 'EA',
                templateUrl: 'common/widgets/observations.tpl.html',
                scope: {
                    sensor: '='
                },
                controller: [
                    '$scope',
                    function($scope) {
                        $scope.data = {
                            observationList: [],
                            observation: {}
                        };

                        $scope.actions = {

                            fetchObservationValue: function(sensorId, observationType) {
                                observationResource.fetch(sensorId, observationType)
                                    .then(function(observationList) {
                                        var obj = observationList[observationList.length - 1];

                                        $scope.data.observation['@id'] = obj['@id'];
                                        $scope.data.observation.value = obj['http://purl.oclc.org/NET/ssnx/ssn#observationResult']
                                            ['http://purl.oclc.org/NET/ssnx/ssn#hasValue']
                                            ['http://vital-iot.eu/ontology/ns/value'];
                                        $scope.data.observation.date = obj['http://purl.oclc.org/NET/ssnx/ssn#observationResultTime']
                                            ['http://www.w3.org/2006/time#inXSDDateTime']
                                            ['@value'];
                                        $scope.data.observation.unit = obj['http://purl.oclc.org/NET/ssnx/ssn#observationResult']
                                            ['http://purl.oclc.org/NET/ssnx/ssn#hasValue']
                                            ['http://qudt.org/vocab/unit#unit']
                                            ['@id'];

                                    });
                            },

                            hideObservationValue: function() {
                                $scope.data.observation = {};
                            }
                        };


                        // Init:
                        $scope.data.observationList = angular.copy($scope.sensor['http://purl.oclc.org/NET/ssnx/ssn#observes']);
                        if (!angular.isArray($scope.data.observationList)) {
                            $scope.data.observationList = [
                                $scope.data.observationList
                            ];
                        }
                    }
                ]
            };
        }
    ]);

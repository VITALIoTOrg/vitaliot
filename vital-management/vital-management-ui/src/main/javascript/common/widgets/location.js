'use strict';
angular.module('common.widgets.location', [])

    .directive('widgetLocation', [
        '$compile', '$interval', 'sensorResource',
        function($compile, $interval, sensorResource) {
            // Functions
            function sensorToMarker(sensor) {
                var marker = {
                    lat: parseFloat(sensor
                        ['http://vital-iot.eu/ontology/ns/hasLastKnownLocation']
                        ['http://www.w3.org/2003/01/geo/wgs84_pos#lat']),
                    lng: parseFloat(sensor
                        ['http://vital-iot.eu/ontology/ns/hasLastKnownLocation']
                        ['http://www.w3.org/2003/01/geo/wgs84_pos#lon']),
                    focus: false,
                    draggable: false,
                    riseOnHover: true,
                    message: sensor['@id']
                };
                return marker;
            }

            return {
                restrict: 'EA',
                replace: true,
                templateUrl: 'common/widgets/location.tpl.html',
                scope: {
                    sensor: '='
                },
                controller: [
                    '$scope',
                    function($scope) {
                        // Map Configuration
                        $scope.mapOptions = {
                            center: {
                                lat: parseFloat($scope.sensor
                                    ['http://vital-iot.eu/ontology/ns/hasLastKnownLocation']
                                    ['http://www.w3.org/2003/01/geo/wgs84_pos#lat']),
                                lng: parseFloat($scope.sensor
                                    ['http://vital-iot.eu/ontology/ns/hasLastKnownLocation']
                                    ['http://www.w3.org/2003/01/geo/wgs84_pos#lon']),
                                zoom: 10
                            },
                            markers: {
                                mainMarker: sensorToMarker($scope.sensor)
                            },
                            defaults: {
                                minZoom: 1,
                                maxZoom: 14,
                                scrollWheelZoom: true
                            }
                        };
                    }]
            };
        }
    ]);


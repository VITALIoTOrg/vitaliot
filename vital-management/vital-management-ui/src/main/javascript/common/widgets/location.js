'use strict';
angular.module('common.widgets.location', [])

    .directive('widgetLocation', [
        '$compile', '$interval', 'sensorResource',
        function($compile, $interval, sensorResource) {
            // Functions
            function sensorToMarker(sensor) {
                var lattitude = parseFloat(sensor
                    ['http://vital-iot.eu/ontology/ns/hasLastKnownLocation']
                    ['http://www.w3.org/2003/01/geo/wgs84_pos#lat']);

                var longitude = parseFloat(
                    sensor
                        ['http://vital-iot.eu/ontology/ns/hasLastKnownLocation']
                        ['http://www.w3.org/2003/01/geo/wgs84_pos#lon'] ||
                    sensor
                        ['http://vital-iot.eu/ontology/ns/hasLastKnownLocation']
                        ['http://www.w3.org/2003/01/geo/wgs84_pos#long']);
                if (!lattitude || !longitude) {
                    return null;
                }

                var marker = {
                    lat: lattitude,
                    lng: longitude,
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
                            center: {},
                            defaults: {
                                minZoom: 1,
                                maxZoom: 14,
                                scrollWheelZoom: true
                            }
                        };

                        if (!_.has($scope.sensor, ['http://vital-iot.eu/ontology/ns/hasLastKnownLocation'])) {
                            return;
                        }

                        var marker = sensorToMarker($scope.sensor);
                        if (marker) {
                            $scope.mapOptions.center = {
                                lat: marker.lat,
                                lng: marker.lng,
                                zoom: 10
                            };
                            $scope.mapOptions.markers = {
                                mainMarker: marker
                            }
                        }
                    }]
            };
        }
    ]);


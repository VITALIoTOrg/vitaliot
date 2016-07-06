'use strict';
angular.module('common.widgets.area', [])

    .directive('widgetArea', [
        '$compile', '$interval', 'systemResource',
        function ($compile, $interval, systemResource) {

            return {
                restrict: 'EA',
                replace: true,
                templateUrl: 'common/widgets/area.tpl.html',
                scope: {
                    system: '='
                },
                controller: [
                    '$scope',
                    function ($scope) {
                        var area = _.get($scope.system, ['http://vital-iot.eu/ontology/ns/serviceArea', '@id'], '');

                        var lat, lng;
                        if (area.indexOf('Camden_Town') >= 0) {
                            lat = 51.539011;
                            lng = -0.142555;
                        } else if (area.indexOf('Istanbul') >= 0) {
                            lat = 41.01759913903529;
                            lng = 28.98124694824219;
                        } else {
                            // No map info
                            return;
                        }

                        $scope.mapOptions = {
                            center: {
                                lat: lat,
                                lng: lng,
                                zoom: 10
                            },
                            defaults: {
                                scrollWheelZoom: true
                            },
                            markers: {
                                systemArea: {
                                    lat: lat,
                                    lng: lng
                                }
                            }
                        };
                    }
                ]
            };
        }
    ]);

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
                        $scope.mapOptions = {
                            center: {
                                lat: 41.01759913903529,
                                lng: 28.98124694824219,
                                zoom: 10
                            },
                            defaults: {
                                scrollWheelZoom: true
                            },
                            paths: {
                                p1: {
                                    color: '#008000',
                                    weight: 8,
                                    latlngs: [
                                        {lat: 51.50, lng: -0.082},
                                        {lat: 48.83, lng: 2.37},
                                        {lat: 41.91, lng: 12.48}
                                    ]
                                }
                            },
                            markers: {
                                istanbul: {
                                    lat: 41.01759913903529,
                                    lng: 28.98124694824219
                                }
                            }
                        };
                    }
                ]
            };
        }
    ]);

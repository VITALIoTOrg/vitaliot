'use strict';
angular.module('common.widgets.hardware', [])

    .directive('widgetHardware', [
        '$interval', 'sensorResource',
        function ($interval, sensorResource) {
            return {
                restrict: 'EA',
                templateUrl: 'common/widgets/hardware.tpl.html',
                scope: {
                    sensor: '='
                },
                link: function (scope, element, attrs) {
                }
            };
        }
    ]);

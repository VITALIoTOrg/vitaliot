'use strict';
angular.module('common.widgets.info', [])

    .directive('widgetInfoSensor', [
        '$interval', 'sensorResource',
        function ($interval, sensorResource) {
            return {
                restrict: 'EA',
                replace: true,
                templateUrl: 'common/widgets/info-sensor.tpl.html',
                scope: {
                    sensor: '='
                },
                link: function (scope, element, attrs) {
                }
            };
        }
    ])

    .directive('widgetInfoSystem', [
        '$interval', 'systemResource',
        function ($interval, systemResource) {
            return {
                restrict: 'EA',
                replace: true,
                templateUrl: 'common/widgets/info-system.tpl.html',
                scope: {
                    system: '='
                },
                link: function (scope, element, attrs) {
                }
            };
        }
    ]);

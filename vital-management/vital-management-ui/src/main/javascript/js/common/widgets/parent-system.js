'use strict';
angular.module('common.widgets.parent-system', [])

    .directive('widgetParentSystem', [
        '$interval', 'sensorResource',
        function ($interval, sensorResource) {
            return {
                restrict: 'EA',
                templateUrl: 'common/widgets/parent-system.tpl.html',
                scope: {
                    sensor: '='
                },
                link: function (scope, element, attrs) {
                }
            };
        }
    ]);
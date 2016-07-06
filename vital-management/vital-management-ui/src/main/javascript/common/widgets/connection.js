'use strict';
angular.module('common.widgets.connection', [])

    .directive('widgetConnection', [
        '$interval', 'sensorResource',
        function ($interval, sensorResource) {
            return {
                restrict: 'EA',
                templateUrl: 'common/widgets/connection.tpl.html',
                scope: {
                    sensor: '='
                },
                link: function (scope, element, attrs) {

                }
            };
        }
    ]);

'use strict';
angular.module('common', [
        'ngRoute',
        'ngCookies',
        'ngSanitize',
        'ngAnimate',
        'ngFx',
        'leaflet-directive',
        'common.templates',
        'common.resources',
        'common.route',
        'common.authentication',
        'common.widgets'
    ])

    /**
     * Common Constants
     */
    .constant('API_PATH', '/vital-management-web/api')

    .filter('encodeHistoryComponent', function () {
        return function (input) {
            return encodeURIComponent((input || '').replace(/\//g, '\\'));
        };
    })

    .filter('decodeHistoryComponent', function () {
        return function (input) {
            return decodeURIComponent(input).replace(/\\/g, '/');
        };
    })


    .filter('statusDisplay', function () {
        return function (input) {
            var array = input.split('/');
            return array[array.length - 1];
        };
    });

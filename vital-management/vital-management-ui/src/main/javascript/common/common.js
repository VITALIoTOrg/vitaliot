'use strict';
angular.module('common', [
    'ngRoute',
    'ngCookies',
    'ngSanitize',
    'ngAnimate',
    'ngFx',
    'common.templates',
    'common.resources',
    'common.route',
    'common.widgets'
])

    .filter('encodeHistoryComponent', function() {
        return function(input) {
            return encodeURIComponent(input.replace(/\//g, '\\'));
        };
    })

    .filter('decodeHistoryComponent', function() {
        return function(input) {
            return decodeURIComponent(input).replace(/\\/g, '/');
        };
    })


    .filter('statusDisplay', function() {
        return function(input) {
            var array = input.split('/');
            return array[array.length - 1];
        };
    });

'use strict';
angular.module('common', [
    'ngRoute',
    'ngCookies',
    'ngSanitize',
    'ui.codemirror',
    'templates-common'
])

    .provider('pathService', function() {
        var path = document.querySelector('meta[name=path]').getAttribute('content');
        var prefix = [];

        var slashesNo = (path.match(/[\/]/g) || []).length;
        while (slashesNo--) {
            prefix.push('../');
        }

        return {
            path: path,
            prefix: prefix.join(''),
            $get: function() {
                return {
                    path: path,
                    prefix: prefix.join('')
                };
            }
        };
    })

    .filter('encodeURI', function() {
        return function(input) {
            return encodeURIComponent(input);
        };
    });

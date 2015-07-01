'use strict';
angular.module('common', [
    'ngRoute',
    'ngCookies',
    'ngSanitize',
    'ngAnimate',
    'ngFx',
    'pascalprecht.translate',
    'templates-common',
    'common.resources',
    'common.route',
    'common.widgets'
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

    .config(['pathServiceProvider', '$translateProvider', function(pathServiceProvider, $translateProvider) {
        $translateProvider.useStaticFilesLoader({
            prefix: pathServiceProvider.prefix + 'locale/',
            suffix: '.json'
        });
        $translateProvider.useCookieStorage();
        $translateProvider.preferredLanguage('el');
    }])


    .filter('encodeHistoryComponent', function() {
        return function(input) {
            return encodeURIComponent(input.replace(/\//g, '\\'));
        };
    })

    .
    filter('decodeHistoryComponent', function() {
        return function(input) {
            return decodeURIComponent(input).replace(/\\/g, '/');
        };
    });

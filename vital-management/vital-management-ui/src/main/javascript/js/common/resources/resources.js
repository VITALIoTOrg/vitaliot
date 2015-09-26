'use strict';
angular.module('common.resources', [
    'common.resources.system',
    'common.resources.sensor',
    'common.resources.observation',
    'common.resources.service',
    'common.resources.management'
])
    .constant('API_PATH', '/vital-management-web/api')
    .constant('BASE_SYSTEM_URL','http://www.example.com/system/')
    .constant('BASE_ICO_URL','http://www.example.com/ico/');

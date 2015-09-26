'use strict';
angular.module('common.resources', [
    'common.resources.system',
    'common.resources.sensor',
    'common.resources.observation',
    'common.resources.service',
    'common.resources.management'
])
    .constant('API_PATH', '/vital-management-web/api')

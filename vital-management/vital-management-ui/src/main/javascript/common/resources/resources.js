'use strict';
angular.module('common.resources', [
        'common.resources.system',
        'common.resources.sensor',
        'common.resources.observation',
        'common.resources.service',
        'common.resources.management',
        'common.resources.security'
    ])

    .constant('API_PATH', '/vital-management-web/api')

    .constant('SEC_ADAPTER_PROTOCOL', 'https') // e.g. https
    .constant('SEC_ADAPTER_HOST', 'vitalgateway.cloud.reply.eu') // Do not include slash at the end
    .constant('SEC_ADAPTER_PORT', '443')
    .constant('SEC_ADAPTER_CONTEXT', 'securitywrapper'); // Do not include slash at the beginning and at the end

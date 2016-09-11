var config = {};

// The port where the router listens.
config.port = 3999;

// The port range that can be used.
config.port_range = {
    start: 4000,
    stop: 5000
};

config.vital = {
    security: {
        url: 'https://vital-integration.atosresearch.eu:8843/securitywrapper/rest',
        user: 'development-tools',
        password: '12345678'
    },
    dms: {
        url: 'https://vital-integration.atosresearch.eu:8843/vital-core-dms'
    },
    discovery: {
        url: 'https://vital-integration.atosresearch.eu:8843/discoverer'
    },
    filtering: {
        url: 'https://vital-integration.atosresearch.eu:8843/filtering'
    },
    orchestration: {
        url: 'https://vital-integration.atosresearch.eu:8843/vital-orchestrator-web'
    },
    cep: {
        url: 'https://vital-integration.atosresearch.eu:8843/vital-core-cep'
    }
};

// The directory where all environments reside.
config.environments_directory = '/root/environments';

// The directory where all flows reside.
config.flows_directory = '/root/flows';

// The directory where all user data reside.
config.user_data_directory = '/root/user-data';

// The host where all environments reside.
config.environment_host = '138.68.48.183';

module.exports = config;
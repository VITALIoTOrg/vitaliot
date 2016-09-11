var config = {};

// The port where the router listens.
config.port = 3999;

// The port range that can be used.
config.port_range = {
    start: 4000,
    stop: 5000
};

config.vital.security.url = 'https://vital-integration.atosresearch.eu:8843/securitywrapper/rest';

// The host where all environments reside.
config.environment_host = '138.68.48.183';

module.exports = config;
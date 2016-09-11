var config = {};

// The port where the router listens.
config.port = 3999;

// The port range that can be used.
config.port_range = {
    start: 4000,
    stop: 5000
};

// The URL to the REST API that VITAL security component exposes.
config.security_url = 'https://vital-integration.atosresearch.eu:8843/securitywrapper/rest'

// The directory where all environments reside.
config.environment_directory = '/home/vital/environments';

// The host where all environments reside.
config.environment_host = '138.68.48.183';

module.exports = config;
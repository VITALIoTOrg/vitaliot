STUB_ENVIRONMENT='/root/vitaliot/vital-development-tools'
ENVIRONMENTS_DIRECTORY='/root/environments'
FLOWS_DIRECTORY='/root/flows'
USER_DATA_DIRECTORY='/root/user-data'

username=$1
port=$2

cp -R $STUB_ENVIRONMENT $ENVIRONMENTS_DIRECTORY/$username

cat <<EOT >> $ENVIRONMENTS_DIRECTORY/$username/settings.json
module.exports = {
    uiPort: $port,
    mqttReconnectTime: 15000,
    serialReconnectTime: 15000,
    debugMaxLength: 1000,
    flowFile: $FLOWS_DIRECTORY + '/flows-' + $username + '.json',
    flowFilePretty: true,
    userDir: $USER_DATA_DIRECTORY + '/' + $username,
    functionGlobalContext: {},
    logging: {
        console: {
            level: "info",
            metrics: false,
            audit: false
        }
    },
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
}
EOT

cd $ENVIRONMENTS_DIRECTORY/$username
forever start app.js

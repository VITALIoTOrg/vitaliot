module.exports = function (RED) {

    var http = require('follow-redirects').http;
    var https = require('follow-redirects').https;
    var urllib = require('url');

    function DiscoverSensors(config) {

        RED.nodes.createNode(this, config);

        this.sensorType = config.sensorType;
        this.latitude = config.latitude;
        this.longitude = config.longitude;
        this.radius = config.radius;
        this.observes = config.observes;

        var node = this;

        this.on('input', function (msg) {

            var securl = RED.settings.security.url + '/authenticate';

            var secopts = urllib.parse(securl);
            secopts.method = 'POST';
            secopts.headers = {};
            secopts.headers['content-type'] = 'application/x-www-form-urlencoded';

            var secpayload = 'name=' + RED.settings.security.user + '&password=' + RED.settings.security.password;

            process.env.NODE_TLS_REJECT_UNAUTHORIZED = '0';

            var secreq = ((/^https/.test(securl)) ? https : http).request(secopts, function (secres) {

                secres.setEncoding('utf8');
                secres.statusCode = secres.statusCode;
                var cookie = secres.headers['set-cookie'][0].split(';')[0];

                var discurl = RED.settings.discovery.url + '/ico';

                var discopts = urllib.parse(discurl);
                discopts.method = 'POST';
                discopts.headers = {};
                discopts.headers['content-type'] = 'application/json';
                discopts.headers['cookie'] = cookie;

                var data = {
                };
                if (msg.type || node.sensorType) {
                    data.type = msg.type ? msg.type : node.sensorType;
                }
                if (msg.position || (node.latitude && node.longitude && node.radius)) {
                    data.position = msg.position ? msg.position : {
                        latitude: node.latitude,
                        longitude: node.longitude,
                        radius: node.radius
                    };
                }
                if (msg.observes || node.observes) {
                    data.observes = msg.observes ? msg.observes : node.observes;
                }
                if (msg.movementPattern) {
                    data.movementPattern = msg.movementPattern;
                }
                if (msg.connectionStability) {
                    data.connectionStability = msg.connectionStability;
                }
                if (msg.hasLocalizer) {
                    data.hasLocalizer = msg.hasLocalizer;
                }
                if (msg.timeWindow) {
                    data.timeWindow = msg.timeWindow;
                }
                var discpayload = JSON.stringify(data);

                var discreq = ((/^https/.test(discurl)) ? https : http).request(discopts, function (discres) {
                    discres.setEncoding('utf8');
                    msg.statusCode = discres.statusCode;
                    msg.payload = '';
                    discres.on('data', function (chunk) {
                        msg.payload += chunk;
                    });
                    discres.on('end', function () {
                        node.send(msg);
                    });
                });

                discreq.on('error', function (err) {
                    msg.payload = 'Failed to discover sensors (' + err.toString() + ').';
                    msg.statusCode = err.code;
                    node.send(msg);
                });

                if (discpayload) {
                    discreq.write(discpayload);
                }

                discreq.end();
            });

            secreq.on('error', function (err) {
                msg.payload = 'Failed to authenticate (' + err.toString() + ').';
                msg.statusCode = err.code;
                node.send(msg);
            });

            if (secpayload) {
                secreq.write(secpayload);
            }

            secreq.end();
        });

    }

    RED.nodes.registerType('discover sensors', DiscoverSensors);
}

module.exports = function (RED) {

    var http = require('follow-redirects').http;
    var https = require('follow-redirects').https;
    var urllib = require('url');

    function Threshold(config) {

        RED.nodes.createNode(this, config);

        this.ico = config.ico;
        this.observationProperty = config.observationProperty;
        this.inequality = config.inequality;
        this.value = config.value;

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

                var filturl = RED.settings.filtering.url + '/threshold';

                var filtopts = urllib.parse(filturl);
                filtopts.method = 'POST';
                filtopts.headers = {};
                filtopts.headers['content-type'] = 'application/json';
                filtopts.headers['cookie'] = cookie;

                var data = {
                };
                if (msg.ico || node.ico) {
                    data.ico = msg.ico ? msg.ico : node.ico;
                }
                if (msg.observationProperty || node.observationProperty) {
                    data.observationProperty = msg.observationProperty ? msg.observationProperty : node.observationProperty;
                }
                if (msg.inequality || node.inequality) {
                    data.inequality = msg.inequality ? msg.inequality : node.inequality;
                }
                if (msg.value || node.value) {
                    data.value = parseInt(msg.value ? msg.value : node.value);
                }
                if (msg.from) {
                    data.from = msg.from;
                }
                if (msg.to) {
                    data.to = msg.to;
                }
                if (msg.position) {
                    data.position = msg.position;
                }
                var filtpayload = JSON.stringify(data);

                var filtreq = ((/^https/.test(filturl)) ? https : http).request(filtopts, function (filtres) {
                    filtres.setEncoding('utf8');
                    msg.statusCode = filtres.statusCode;
                    msg.payload = '';
                    filtres.on('data', function (chunk) {
                        msg.payload += chunk;
                    });
                    filtres.on('end', function () {
                        node.send(msg);
                    });
                });

                filtreq.on('error', function (err) {
                    msg.payload = 'Failed to apply threshold (' + err.toString() + ').';
                    msg.statusCode = err.code;
                    node.send(msg);
                });

                if (filtpayload) {
                    filtreq.write(filtpayload);
                }

                filtreq.end();
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

    RED.nodes.registerType('threshold', Threshold);
}

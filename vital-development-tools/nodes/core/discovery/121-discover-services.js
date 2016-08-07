module.exports = function (RED) {

    var http = require('follow-redirects').http;
    var https = require('follow-redirects').https;
    var urllib = require('url');

    function DiscoverServices(config) {

        RED.nodes.createNode(this, config);

        this.serviceType = config.serviceType;
        this.system = config.system;

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

                var discurl = RED.settings.discovery.url + '/service';

                var discopts = urllib.parse(discurl);
                discopts.method = 'POST';
                discopts.headers = {};
                discopts.headers['content-type'] = 'application/json';
                discopts.headers['cookie'] = cookie;

                var data = {
                    type: msg.type ? msg.type : node.serviceType,
                    system: msg.system ? msg.type : node.system
                };
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
                    msg.payload = 'Failed to discover services (' + err.toString() + ').';
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

    RED.nodes.registerType('discover services', DiscoverServices);
}

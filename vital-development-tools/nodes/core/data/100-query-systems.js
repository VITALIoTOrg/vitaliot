module.exports = function (RED) {

    var http = require('follow-redirects').http;
    var https = require('follow-redirects').https;
    var urllib = require('url');

    function QuerySystems(config) {

        RED.nodes.createNode(this, config);

        this.query = config.query;

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

                var dmsurl = RED.settings.dms.url + '/querySystem';

                var dmsopts = urllib.parse(dmsurl);
                dmsopts.method = 'POST';
                dmsopts.headers = {};
                dmsopts.headers['content-type'] = 'application/json';
                dmsopts.headers['cookie'] = cookie;

                var dmspayload = msg.query ? JSON.stringify(msg.query) : node.query;

                var dmsreq = ((/^https/.test(dmsurl)) ? https : http).request(dmsopts, function (dmsres) {
                    dmsres.setEncoding('utf8');
                    msg.statusCode = dmsres.statusCode;
                    msg.payload = '';
                    dmsres.on('data', function (chunk) {
                        msg.payload += chunk;
                    });
                    dmsres.on('end', function () {
                        node.send(msg);
                    });
                });

                dmsreq.on('error', function (err) {
                    msg.payload = 'Failed to query systems (' + err.toString() + ').';
                    msg.statusCode = err.code;
                    node.send(msg);
                });

                if (dmspayload) {
                    dmsreq.write(dmspayload);
                }

                dmsreq.end();
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

    RED.nodes.registerType('query systems', QuerySystems);
}

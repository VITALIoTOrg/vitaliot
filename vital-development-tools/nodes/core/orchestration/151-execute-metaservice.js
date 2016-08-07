module.exports = function (RED) {

    var http = require('follow-redirects').http;
    var https = require('follow-redirects').https;
    var urllib = require('url');

    function ExecuteMetaservice(config) {

        RED.nodes.createNode(this, config);

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

                var filturl = RED.settings.filtering.url + '/resampling';

                var filtopts = urllib.parse(filturl);
                filtopts.method = 'POST';
                filtopts.headers = {};
                filtopts.headers['content-type'] = 'application/json';
                filtopts.headers['cookie'] = cookie;

                var data = {
                    ico: msg.ico,
                    timeValue: msg.timeValue,
                    timeUnit: msg.timeUnit,
                    observationProperty: msg.observationProperty,
                    from: msg.from,
                    to: msg.to
                };
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
                    msg.payload = 'Failed to re-sample (' + err.toString() + ').';
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

    RED.nodes.registerType('execute metaservice', ExecuteMetaservice);
}

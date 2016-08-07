module.exports = function (RED) {

    var http = require('follow-redirects').http;
    var https = require('follow-redirects').https;
    var urllib = require('url');

    function Sensors(config) {

        RED.nodes.createNode(this, config);

        this.ppi = RED.nodes.getNode(config.ppi);
        this.sensorId = config.sensorId ? config.sensorId.split(',') : null;
        this.sensorType = config.sensorType ? config.sensorType.split(',') : null;

        var node = this;

        this.on('input', function (msg) {

            var theppi = msg.ppi || node.ppi;

            var url = theppi.url + '/sensor/metadata';

            var opts = urllib.parse(url);
            opts.method = 'POST';
            opts.headers = {};
            opts.headers['content-type'] = 'application/json';
            if (theppi.credentials) {
                opts.auth = theppi.credentials.user + ':' + (theppi.credentials.password || '');
            }

            var data = {
                id: msg.id ? msg.id : node.sensorId,
                type: msg.type ? msg.type : node.sensorType
            };
            var payload = JSON.stringify(data);

            var req = ((/^https/.test(url)) ? https : http).request(opts, function (res) {

                res.setEncoding('utf8');

                msg.statusCode = res.statusCode;

                msg.payload = '';
                res.on('data', function (chunk) {
                    msg.payload += chunk;
                });
                res.on('end', function () {
                    node.send(msg);
                });
            });

            req.on('error', function (err) {
                msg.payload = err.toString();
                msg.statusCode = err.code;
                node.send(msg);
            });

            if (payload) {
                req.write(payload);
            }

            req.end();
        });
    }

    RED.nodes.registerType('sensors', Sensors);
}

module.exports = function (RED) {

    var http = require('follow-redirects').http;
    var https = require('follow-redirects').https;
    var urllib = require('url');

    function Observations(config) {

        RED.nodes.createNode(this, config);

        this.ppi = RED.nodes.getNode(config.ppi);
        this.sensor = config.sensor ? config.sensor.split(',') : null;
        this.property = config.property;

        var node = this;

        this.on('input', function (msg) {

            var theppi = msg.ppi || node.ppi;

            var serurl = theppi.url + '/service/metadata';

            var seropts = urllib.parse(serurl);
            seropts.method = 'POST';
            seropts.headers = {};
            seropts.headers['content-type'] = 'application/json';
            if (theppi.credentials) {
                seropts.auth = theppi.credentials.user + ':' + (theppi.credentials.password || '');
            }

            var data = {
                type: ['http://vital-iot.eu/ontology/ns/ObservationService']
            };
            var serpayload = JSON.stringify(data);

            var serreq = ((/^https/.test(serurl)) ? https : http).request(seropts, function (serres) {

                serres.setEncoding('utf8');

                var result = '';
                serres.on('data', function (chunk) {
                    result += chunk;
                });
                serres.on('end', function () {

                    var services = JSON.parse(result)[0];
                    var operations = services.operations;

                    for (var i = 0; i < operations.length; i++) {

                        var operation = operations[i];
                        var type = operation.type;
                        if (type != 'vital:GetObservations') {
                            continue;
                        }

                        var method = operation['hrest:hasMethod'];
                        method = method.substring(method.lastIndexOf(':') + 1, method.length);

                        var obsurl = operation['hrest:hasAddress'];

                        var obsopts = urllib.parse(obsurl);
                        obsopts.method = method;
                        obsopts.headers = {};
                        obsopts.headers['content-type'] = 'application/json';
                        if (theppi.credentials) {
                            obsopts.auth = theppi.credentials.user + ':' + (theppi.credentials.password || '');
                        }

                        var data = {
                            sensor: msg.sensor ? msg.sensor : node.sensor,
                            property: msg.property ? msg.property : node.property,
                            from: msg.from,
                            to: msg.to
                        };
                        var obspayload = JSON.stringify(data);

                        var obsreq = ((/^https/.test(obsurl)) ? https : http).request(obsopts, function (obsres) {

                            obsres.setEncoding('utf8');

                            msg.statusCode = obsres.statusCode;

                            msg.payload = '';
                            obsres.on('data', function (chunk) {
                                msg.payload += chunk;
                            });
                            obsres.on('end', function () {
                                node.send(msg);
                            });
                        });

                        obsreq.on('error', function (err) {
                            msg.payload = err.toString();
                            msg.statusCode = err.code;
                            node.send(msg);
                        });

                        if (obspayload) {
                            obsreq.write(obspayload);
                        }

                        obsreq.end();
                    }
                });
            });

            serreq.on('error', function (err) {
                msg.payload = err.toString();
                msg.statusCode = err.code;
                node.send(msg);
            });

            if (serpayload) {
                serreq.write(serpayload);
            }

            serreq.end();
        });
    }

    RED.nodes.registerType('observations', Observations);
}

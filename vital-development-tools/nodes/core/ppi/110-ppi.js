module.exports = function (RED) {

    function PPI(config) {
        RED.nodes.createNode(this, config);
        this.url = config.url;
        if (!((this.url.indexOf('http://') === 0) || (this.url.indexOf('https://') === 0))) {
            this.url = 'http://' + this.url;
        }
    }

    RED.nodes.registerType('PPI', PPI, {
        credentials: {
            user: {
                type: 'text'
            },
            password: {
                type: 'password'
            }
        }
    });
}

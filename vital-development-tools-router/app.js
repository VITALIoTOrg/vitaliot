var express = require('express');
var body_parser = require('body-parser')
var session = require('express-session');
var cookie_parser = require('cookie-parser');
var fs = require('fs');
var flash = require('connect-flash');
var path = require('path');
var request = require('request');
var sqlite3 = require('sqlite3').verbose();
var config = require('./config');

var file = 'users.db';
var exists = fs.existsSync(file);
var db = new sqlite3.Database(file);
var ports = {};
if (!exists) {
    db.run('CREATE TABLE users (username TEXT PRIMARY KEY, port INTEGER)', function (error) {
        db.each('SELECT port FROM users', function (error, row) {
            ports[row.port] = true;
        });
    });
}

var app = express();
app.use(express.static('public'));
app.use(body_parser.urlencoded({
    extended: false
}));
app.use(cookie_parser('secret'));
app.use(session({
    secret: 'secret',
    resave: true,
    saveUninitialized: true
}));
app.use(flash());
app.set('view engine', 'jade');
app.set('view options', {
    layout: false
});


app.get('/', function (req, res) {
    res.render('index');
});

app.post('/', function (req, res) {
    var username = req.body.username;
    var password = req.body.password;

    if (!username) {
        res.status(400).render('index', {
            error: 'Please give your username.'
        });
        return;
    }

    if (!password) {
        res.status(400).render('index', {
            username: username,
            error: 'Please give your password.'
        });
        return;
    }

    request.post(
        config.vital.security.url + '/authenticate', {
            form: {
                name: username,
                password: password
            },
            rejectUnauthorized: false
        },
        function (error, response, body) {
            if (error || !response || response.statusCode != 200) {
                console.log('Sign in failed (error:', error, ', code:', response ? response.statusCode : '-', ')');
                res.render('index', {
                    username: username,
                    error: 'Failed to sign in. Please try again.'
                });
                return;
            }
            db.each('SELECT port FROM users WHERE username=?', username, function (error, row) {
                console.log('Old user', username, 'signed in.');
                res.redirect('http://' + config.environment_host + ':' + row.port);
            }, function (error, rows) {
                if (rows == 0) {
                    var port = 0;
                    do {
                        port = config.port_range.start + Math.floor(Math.random() * (config.port_range.stop - config.port_range.start + 1));
                        if (!(port in ports)) {
                            break;
                        }
                        console.log('Port', port, 'is taken.');
                    } while (true);
                    console.log('New user', username, 'signed in and assigned port', port, '.');
                    db.run('INSERT INTO users(username, port) VALUES(?, ?)', username, port);
                    var settings = {
                        uiPort: port,
                        mqttReconnectTime: 15000,
                        serialReconnectTime: 15000,
                        debugMaxLength: 1000,
                        flowFile: config.flows_directory + '/flows-' + username + '.json',
                        flowFilePretty: true,
                        userDir: config.user_data_directory + '/' + username,
                        functionGlobalContext: {},

                        logging: {
                            console: {
                                level: "info",
                                metrics: false,
                                audit: false
                            }
                        },
                        security: config.vital.security,
                        dms: config.vital.dms,
                        discovery: config.vital.discovery,
                        filtering: config.vital.filtering,
                        orchestration: config.vital.orchestration,
                        cep: config.vital.cep
                    };
                    fs.writeFile('settings.js', 'module.exports = ' + JSON.stringify(settings, null, 4), function (error) {
                        res.redirect('http://' + config.environment_host + ':' + port);
                    });
                }
            });
        }
    );
});


app.listen(config.port);
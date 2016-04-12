'use strict';
module.exports = function (grunt) {

    // Load grunt tasks automatically, when needed
    require('jit-grunt')(grunt, {
        connect: 'grunt-contrib-connect',
        useminPrepare: 'grunt-usemin',
        ngtemplates: 'grunt-angular-templates',
        protractor: 'grunt-protractor-runner',
        injector: 'grunt-injector',
        configureProxies: 'grunt-connect-proxy',
        cachebreaker: 'grunt-cache-breaker'
    });

    // Time how long tasks take. Can help when optimizing build times
    require('time-grunt')(grunt);

    // Define the configuration for all the tasks
    grunt.initConfig({

        // Project settings
        pkg: grunt.file.readJSON('package.json'),

        /**********************
         * Build Targets ******
         *********************/

        // Empties folders to start fresh
        clean: {
            build: './target/build',
            dist: './target/dist'
        },
        // Copies remaining files to places other tasks can use
        copy: {
            build: {
                files: [{
                    expand: true,
                    dot: true,
                    cwd: './src/main/javascript',
                    dest: './target/build',
                    src: [
                        '*.{ico,png,txt}',
                        '.htaccess',
                        'vendor/**/*',
                        'assets/**/*',
                        'common/**/*',
                        'main/**/*',
                        'index.html',
                        '!**/*.spec.js',
                        '!**/*.mock.js',
                        '!**/*.less'
                    ]
                }]
            },
            dist: {
                files: [{
                    expand: true,
                    dot: true,
                    cwd: './target/build',
                    dest: './target/dist',
                    src: [
                        '*.{ico,png,txt}',
                        '.htaccess',
                        'vendor/**/*',
                        'assets/**/*',
                        'index.html'
                    ]
                }]
            }
        },

        // Package all the html partials into a single javascript payload
        ngtemplates: {
            'common': {

                cwd: './target/build/',
                src: ['common/**/*.tpl.html'],
                dest: './target/build/common/common.templates.js',
                options: {
                    module: 'common.templates',
                    standalone: true
                }
            },
            'main': {
                cwd: './target/build/',
                src: ['main/**/*.tpl.html'],
                dest: './target/build/main/main.templates.js',
                options: {
                    module: 'main.templates',
                    standalone: true
                }
            }
        },

        // Compiles Less to CSS
        less: {
            options: {
                paths: [
                    './src/main/javascript/'
                ]
            },
            build: {
                files: {
                    './target/build/vital-orchestrator.css': './src/main/javascript/less/main.less'
                }
            }
        },

        // Automatically inject Bower components into the app
        wiredep: {
            build: {
                src: './target/build/index.html',
                cwd: '.',
                ignorePath: '../../src/main/javascript/', // This is to create the proper path in the html file
                exclude: [
                    /angular-mocks.js/,
                    /font-awesome.css/
                ],
                'overrides': {
                    'angular-bootstrap': {
                        'main': [
                            './ui-bootstrap.js',
                            './ui-bootstrap-tpls.js'
                        ]
                    },
                    'admin-lte': {
                        'main': [
                            'index2.html',
                            'dist/css/AdminLTE.css',
                            'dist/css/skins/skin-blue.css',
                            'dist/js/app.js',
                            'build/less/AdminLTE.less'
                        ]
                    },
                    'bootstrap': {
                        'main': [
                            'less/bootstrap.less',
                            'dist/js/bootstrap.js',
                            'dist/css/bootstrap.css'
                        ]
                    },
                    'leaflet.markercluster': {
                        'main': [
                            'dist/leaflet.markercluster.js',
                            'dist/MarkerCluster.css',
                            'dist/MarkerCluster.Default.css'
                        ]
                    }
                }
            }
        },

        injector: {
            options: {
                addRootSlash: false
            },
            // Inject application script files into index.html (doesn't include bower)
            build_scripts: {
                options: {
                    transform: function (filePath) {
                        filePath = filePath.replace('./', '');
                        filePath = filePath.replace('target/build/', '');
                        return '<script src="' + filePath + '"></script>';
                    },
                    starttag: '<!-- injector:js -->',
                    endtag: '<!-- endinjector -->'
                },
                files: {
                    './target/build/index.html': [
                        [
                            // Order matters
                            './target/build/common/**/*.js',
                            './target/build/main/**/*.js',
                        ]
                    ]
                }
            },

            // Inject component css into index.html
            build_css: {
                options: {
                    transform: function (filePath) {
                        filePath = filePath.replace('./', '');
                        filePath = filePath.replace('target/build/', '');
                        return '<link rel="stylesheet" href="' + filePath + '">';
                    },
                    starttag: '<!-- injector:css -->',
                    endtag: '<!-- endinjector -->'
                },
                files: {
                    './target/build/index.html': [
                        './target/build/**/*.css',
                        '!./target/build/vendor/**/*.css'
                    ]
                }
            },

            dist_scripts: {
                options: {
                    transform: function (filePath) {
                        filePath = filePath.replace('./', '');
                        filePath = filePath.replace('target/dist/', '');
                        return '<script src="' + filePath + '"></script>';
                    },
                    starttag: '<!-- injector:js -->',
                    endtag: '<!-- endinjector -->'
                },
                files: {
                    './target/dist/index.html': [
                        [
                            // Order matters
                            './target/dist/common.js',
                            './target/dist/main.js'
                        ]
                    ]
                }
            },

            // Inject component css into index.html
            dist_css: {
                options: {
                    transform: function (filePath) {
                        filePath = filePath.replace('./', '');
                        filePath = filePath.replace('target/build/', '');
                        return '<link rel="stylesheet" href="' + filePath + '">';
                    },
                    starttag: '<!-- injector:css -->',
                    endtag: '<!-- endinjector -->'
                },
                files: {
                    './target/build/index.html': [
                        './target/dist/**/*.css',
                        '!./target/dist/vendor/**/*.css'
                    ]
                }
            }
        },

        /*********************
         ** Dist targets *****
         *********************/

        // Allow the use of non-minsafe AngularJS files. Automatically makes it
        // minsafe compatible so Uglify does not destroy the ng references
        ngAnnotate: {
            dist: {
                files: [{
                    expand: true,
                    cwd: './target/build/',
                    src: [
                        '{app,origination}/**/*.js'
                    ],
                    dest: './target/build/'
                }]
            }
        },

        concat: {
            options: {
                separator: ';'
            },
            dist: {
                files: {
                    './target/dist/app.js': ['./target/build/app/**/*.js'],
                    './target/dist/origination.js': ['./target/build/origination/**/*.js']
                }
            }
        },

        uglify: {
            options: {
                compress: {
                    global_defs: {
                        'DEBUG': false
                    },
                    dead_code: true
                },
                preserveComments: false,
                screwIE8: true,
                quoteStyle: 1 // Use single quotes everywhere
            },
            dist: {
                files: {
                    './target/dist/app.js': ['./target/dist/app.js'],
                    './target/dist/origination.js': ['./target/dist/origination.js']
                }
            }
        },

        cssmin: {
            options: {
                shorthandCompacting: false,
                roundingPrecision: -1
            },
            target: {
                files: {
                    './target/dist/origination.css': ['./target/build/origination.css']
                }
            }
        },

        cachebreaker: {
            dist: {
                options: {
                    match: [{
                        'origination.js': './target/dist/origination.js',
                        'origination.css': './target/dist/origination.css',
                        'app.js': './target/dist/app.js',
                    }],
                    replacement: 'md5'
                },
                files: {
                    src: ['./target/dist/index.html']
                }
            }
        },

        /********************************
         ** Development server targets **
         ********************************/

        connect: {
            options: {
                port: 9000,
                hostname: 'localhost',
                base: './target/build',
                middleware: function (connect, options) {
                    var proxy = require('grunt-connect-proxy/lib/utils').proxyRequest;
                    var serveStatic = require('serve-static');
                    return [
                        // Include the proxy first
                        proxy,
                        // Serve static files.
                        serveStatic(options.base[0])
                    ];
                }
            },
            proxies: [
                {
                    context: '/vital-core-orchestrator-web',
                    host: 'localhost',
                    port: '8080',
                    https: false,
                    changeOrigin: false
                }
            ]
        },

        watch: {
            rebuild: {
                files: [
                    './src/main/javascript/*',
                    './src/main/javascript/**/*',
                    '!{app,origination}/**/*.spec.js',
                    '!{app,origination}/**/*.mock.js',
                ],
                options: {
                    livereload: false
                },
                tasks: ['build']
            }
        },

        // Make sure code styles are up to par and there are no obvious mistakes
        jshint: {
            options: {
                jshintrc: '.jshintrc',
                reporter: require('jshint-stylish')
            },
            all: [
                './src/main/javascript/{main,common}/**/*.js',
                './src/main/javascript/{main,common}/**/*.spec.js',
                './src/main/javascript/{main,common}/**/*.mock.js'
            ]
        }

    });

    /*****************
     ** Run Targets **
     *****************/

    grunt.registerTask('build', [
        // Copies all files from ./src/main/javascript (html, css, assets) to ./target/build/, except .less and
        'copy:build',
        // Convert tpl.html to .templates.js files
        'ngtemplates',
        // Creates css file in ./target/build/
        'less:build',
        // Injectors for ./target/build/index.html)
        // 1. Injects bower scripts in ./target/build/index.html
        'wiredep:build',
        // 2. Injects scripts in ./target/build/index.html
        'injector:build_scripts',
        // 3. Injects css in ./target/build/index.html, except origination.css
        'injector:build_css'
    ]);

    grunt.registerTask('build-clean', [
        'clean',
        'build'
    ]);

    grunt.registerTask('dist', [
        'clean',
        'build',
        // Copy files, except our scripts to dist folder (html, assets, bower, vendor)
        'copy:dist',
        // Annotate, concat and uglify our scripts
        'ngAnnotate',
        'concat',
        'uglify',
        'cssmin',
        // Inject the minified files in index.html
        'injector:dist_scripts',
        'injector:dist_css',
        //Rename files
        'cachebreaker'
    ]);

    grunt.registerTask('serve', function (target) {
        grunt.task.run([
            'build-clean',
            'configureProxies:connect',
            'connect',
            'watch'
        ]);
    });

    // Used for delaying livereload until after server has restarted
    grunt.registerTask('wait', function () {
        grunt.log.ok('Waiting for server reload...');

        var done = this.async();

        setTimeout(function () {
            grunt.log.writeln('Done waiting!');
            done();
        }, 1500);
    });
};

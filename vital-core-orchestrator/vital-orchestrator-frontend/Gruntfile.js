module.exports = function(grunt) {
    'use strict';

    /**
     * A utility function to get all app HTML sources.
     */
    function filterForHTML(files) {
        return files.filter(function(file) {
            return file.match(/\.html$/);
        });
    }

    /**
     * A utility function to get all app JavaScript sources.
     */
    function filterForJS(files) {
        return files.filter(function(file) {
            return file.match(/\.js$/);
        });
    }

    /**
     * A utility function to get all app CSS sources.
     */
    function filterForCSS(files) {
        return files.filter(function(file) {
            return file.match(/\.css$/);
        });
    }

    /**
     * Load required Grunt tasks. These are installed based on the versions listed
     * in `package.json` when you do `npm install` in this directory.
     */
    grunt.loadNpmTasks('grunt-contrib-clean');
    grunt.loadNpmTasks('grunt-contrib-copy');
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-contrib-cssmin');
    grunt.loadNpmTasks('grunt-contrib-less');
    grunt.loadNpmTasks('grunt-contrib-jshint');
    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.loadNpmTasks('grunt-contrib-connect');
    grunt.loadNpmTasks('grunt-connect-proxy');
    grunt.loadNpmTasks('grunt-html2js');
    grunt.loadNpmTasks('grunt-karma');

    /**
     * The html template includes the stylesheet and javascript sources
     * based on dynamic names calculated in this Gruntfile. This task assembles
     * the list into variables for the template to use and then runs the
     * compilation.
     */
    grunt.registerMultiTask('processhtml', 'Process html templates', function() {
        var thisTask = this;
        var srcRE = new RegExp('^(' + grunt.config('src_folder') + ')\/', 'g');
        var dirRE = new RegExp('^(' + grunt.config('build_folder') + '|' + grunt.config('dist_folder') + ')\/', 'g');
        var htmlFiles = filterForHTML(thisTask.filesSrc);
        htmlFiles.forEach(function(file) {
            var depth = (file.match(/\//g) || []).length - 1;
            var prefix = (function() {
                var s = '';
                for (var i = 0; i < depth; i++) {
                    s += '../';
                }
                return s;
            })();
            var jsFiles = filterForJS(thisTask.filesSrc).map(function(jsFile) {
                return prefix + jsFile.replace(dirRE, '');
            });
            var cssFiles = filterForCSS(thisTask.filesSrc).map(function(cssFile) {
                return prefix + cssFile.replace(dirRE, '');
            });
            grunt.file.copy(file, thisTask.data.dir + '/' + file.replace(srcRE, ''), {
                process: function(contents) {
                    return grunt.template.process(contents, {
                        data: {
                            path: file.replace(srcRE, ''),
                            scripts: jsFiles,
                            styles: cssFiles,
                            version: grunt.config('pkg.version') + '_' + (new Date()).getTime()
                        }
                    });
                }
            });
        });

    });

    /**
     * This is the configuration object Grunt uses to give each plugin its
     * instructions.
     */

    grunt.initConfig({

        /**
         * We read in our `package.json` file so we can access the package name and
         * version. It's already there, so we don't repeat ourselves here.
         */
        pkg: grunt.file.readJSON('package.json'),

        /**
         * Contains information about the project structure (source files, destination directories etc
         */
        src_folder: 'src',
        vendor_folder: 'vendor',
        build_folder: 'build',
        dist_folder: 'dist',

        app_files: {
            js: ['<%= src_folder %>/js/**/*.js', '!<%= src_folder %>/js/**/*.spec.js'],

            mtpl: ['<%= src_folder %>/js/main/**/*.tpl.html'],
            ctpl: ['<%= src_folder %>/js/common/**/*.tpl.html'],

            html: ['<%= src_folder %>/**/*.html', '!<%= src_folder %>/**/*.tpl.html'],

            less: '<%= src_folder %>/less/main.less',

            jsunit: ['<%= src_folder %>/js/**/*.spec.js']
        },
        vendor_files: {
            js: [
                '<%= vendor_folder %>/jquery/dist/jquery.js',
                '<%= vendor_folder %>/lodash/dist/lodash.js',
                '<%= vendor_folder %>/codemirror/lib/codemirror.js',
                '<%= vendor_folder %>/codemirror/mode/javascript/javascript.js',
                '<%= vendor_folder %>/bootstrap/dist/js/bootstrap.js',
                '<%= vendor_folder %>/angular/angular.js',
                '<%= vendor_folder %>/angular-route/angular-route.js',
                '<%= vendor_folder %>/angular-cookies/angular-cookies.js',
                '<%= vendor_folder %>/angular-sanitize/angular-sanitize.js',
                '<%= vendor_folder %>/angular-ui-codemirror/ui-codemirror.js'
            ],
            css: [
                '<%= vendor_folder %>/codemirror/lib/codemirror.css',
                '<%= vendor_folder %>/codemirror/theme/ambiance.css',
                '<%= vendor_folder %>/codemirror/theme/solarized.css'
            ],
            assets_fonts: [
                '<%= vendor_folder %>/bootstrap/fonts/*',
                '<%= vendor_folder %>/fontawesome/fonts/*'
            ],
            assets_images: []
        },
        test_files: {
            js: [
                '<%= vendor_folder %>/angular-mocks/angular-mocks.js'
            ]
        },

        /**
         * The banner is the comment that is placed at the top of our compiled
         * source files. It is first processed as a Grunt template, where the `<%=`
         * pairs are evaluated based on this very configuration object.
         */
        meta: {
            banner: '/**\n' +
            ' * <%= pkg.name %> - v<%= pkg.version %> - <%= grunt.template.today("yyyy-mm-dd") %>\n' +
            ' * <%= pkg.homepage %>\n' +
            ' *\n' +
            ' * Copyright (c) <%= grunt.template.today("yyyy") %> <%= pkg.author %>\n' +
            ' * Licensed <%= pkg.licenses.type %> <%= pkg.licenses.url %>\n' +
            ' */\n'
        },

        /**
         * The directories to delete when `grunt clean` is executed.
         */
        clean: [
            '<%= build_folder %>',
            '<%= dist_folder %>'
        ],

        /**
         * The `copy` task just copies files from A to B. We use it here to copy
         * our project assets (images, fonts, etc.) and javascripts into
         * `build_folder`, and then to copy the assets to `dist_folder`.
         */
        copy: {
            app_js: {
                files: [
                    {
                        cwd: '.',
                        src: ['<%= app_files.js %>'],
                        dest: '<%= build_folder %>/',
                        expand: true
                    }
                ]
            },
            app_assets: {
                files: [
                    {
                        cwd: '<%= src_folder %>/assets',
                        src: ['**'],
                        dest: '<%= build_folder %>/assets/',
                        expand: true
                    },
                    {
                        cwd: '<%= src_folder %>/locale',
                        src: ['**'],
                        dest: '<%= build_folder %>/locale/',
                        expand: true
                    }
                ]
            },

            vendor_js: {
                files: [
                    {
                        cwd: '.',
                        src: ['<%= vendor_files.js %>'],
                        dest: '<%= build_folder %>/',
                        expand: true
                    }
                ]
            },
            vendor_assets: {
                files: [
                    {
                        cwd: '.',
                        src: ['<%= vendor_files.assets_fonts %>'],
                        dest: '<%= build_folder %>/assets/fonts/',
                        expand: true,
                        flatten: true
                    },
                    {
                        cwd: '.',
                        src: ['<%= vendor_files.assets_images %>'],
                        dest: '<%= build_folder %>/assets/images/',
                        expand: true,
                        flatten: true
                    }
                ]
            },

            dist_assets: {
                files: [
                    {
                        cwd: '<%= build_folder %>/assets',
                        src: ['**'],
                        dest: '<%= dist_folder %>/assets/',
                        expand: true
                    },
                    {
                        cwd: '<%= build_folder %>/locale',
                        src: ['**'],
                        dest: '<%= dist_folder %>/locale/',
                        expand: true
                    }
                ]
            }
        },

        /**
         * Compile Less files to CSS
         */
        less: {
            build: {
                options: {},
                files: {
                    '<%= build_folder %>/assets/<%= pkg.name %>-<%= pkg.version %>.css': '<%= app_files.less %>'
                }
            }
        },

        /**
         * HTML2JS is a Grunt plugin that takes all of your template files and
         * places them into JavaScript files as strings that are added to
         * AngularJS's template cache. This means that the templates too become
         * part of the initial payload as one JavaScript file. Neat!
         */
        html2js: {
            /**
             * These are the templates from `src/app`.
             */
            main: {
                options: {
                    base: '<%= src_folder %>/js/'
                },
                src: ['<%= app_files.mtpl %>'],
                dest: '<%= build_folder %>/templates-main.js'
            },

            /**
             * These are the templates from `src/common`.
             */
            common: {
                options: {
                    base: '<%= src_folder %>/js/'
                },
                src: ['<%= app_files.ctpl %>'],
                dest: '<%= build_folder %>/templates-common.js'
            }
        },

        /**
         * `grunt concat` concatenates multiple source files into a single file.
         */
        concat: {
            /**
             * The `build_css` target concatenates compiled CSS and vendor CSS
             * together.
             */
            build_css: {
                src: [
                    '<%= vendor_files.css %>',
                    '<%= build_folder %>/assets/<%= pkg.name %>-<%= pkg.version %>.css'
                ],
                dest: '<%= build_folder %>/assets/<%= pkg.name %>-<%= pkg.version %>.css'
            },
            /**
             * The `dist_js` target is the concatenation of our application source
             * code and all specified vendor source code into a single file.
             */
            dist_js: {
                options: {
                    banner: '<%= meta.banner %>'
                },
                src: [
                    '<%= vendor_files.js %>',
                    'module.prefix',
                    '<%= build_folder %>/src/**/*.js',
                    '<%= html2js.main.dest %>',
                    '<%= html2js.common.dest %>',
                    'module.suffix'
                ],
                dest: '<%= dist_folder %>/<%= pkg.name %>-<%= pkg.version %>.js'
            }
        },

        /**
         * Minify the js sources!
         */
        uglify: {
            compile: {
                options: {
                    banner: '<%= meta.banner %>'
                },
                files: {
                    '<%= concat.dist_js.dest %>': '<%= concat.dist_js.dest %>'
                }
            }
        },

        /**
         * Minify CSS
         */
        cssmin: {
            minify: {
                files: {
                    '<%= dist_folder %>/assets/<%= pkg.name %>-<%= pkg.version %>.css': '<%= dist_folder %>/assets/<%= pkg.name %>-<%= pkg.version %>.css'
                }
            }
        },

        /**
         * `jshint` defines the rules of our linter as well as which files we
         * should check. This file, all javascript sources, and all our unit tests
         * are linted based on the policies listed in `options`. But we can also
         * specify exclusionary patterns by prefixing them with an exclamation
         * point (!); this is useful when code comes from a third party but is
         * nonetheless inside `src/`.
         */
        jshint: {
            src: [
                '<%= app_files.js %>'
            ],
            test: [
                '<%= app_files.jsunit %>'
            ],
            gruntfile: [
                'Gruntfile.js'
            ],
            globals: {},
            options: {
                jshintrc: '.jshintrc'
            }
        },

        /**
         * The Karma configurations.
         */
        karma: {
            options: {
                configFile: 'karma.conf.js'
            },
            unit: {
                runnerPort: 9101,
                background: true
            },
            continuous: {
                singleRun: true
            }
        },

        /**
         * The `processhtml` task compiles the `*.html` files as a Grunt template. CSS
         * and JS files co-exist here but they get split apart later.
         */
        processhtml: {

            /**
             * During development, we don't want to have wait for compilation,
             * concatenation, minification, etc. So to avoid these steps, we simply
             * add all script files directly to the `<head>` of `*.html`. The
             * `src` property contains the list of included files.
             */
            build: {
                dir: '<%= build_folder %>',
                src: [
                    '<%= app_files.html %>',
                    '<%= build_folder %>/assets/<%= pkg.name %>-<%= pkg.version %>.css',
                    '<%= vendor_files.js %>',
                    '<%= app_files.js %>',
                    '<%= html2js.common.dest %>',
                    '<%= html2js.main.dest %>'
                ]
            },

            /**
             * When it is time to have a completely compiled application, we can
             * alter the above to include only a single JavaScript and a single CSS
             * file. Now we're back!
             */
            dist: {
                dir: '<%= dist_folder %>',
                src: [
                    '<%= app_files.html %>',
                    '<%= dist_folder %>/assets/<%= pkg.name %>-<%= pkg.version %>.css',
                    '<%= concat.dist_js.dest %>'
                ]
            }
        },

        /**
         * And for rapid development, we have a watch set up that checks to see if
         * any of the files listed below change, and then to execute the listed
         * tasks when they do. This just saves us from having to type "grunt" into
         * the command-line every time we want to see what we're working on; we can
         * instead just leave "grunt watch" running in a background terminal. Set it
         * and forget it, as Ron Popeil used to tell us.
         *
         * But we don't need the same thing to happen for all the files.
         */
        delta: {
            /**
             * By default, we want the Live Reload to work for all tasks; this is
             * overridden in some tasks (like this file) where browser resources are
             * unaffected. It runs by default on port 35729, which your browser
             * plugin should auto-detect.
             */
            options: {
                livereload: true
            },

            /**
             * When our JavaScript source files change, we want to run lint them and
             * run our unit tests.
             */
            js_src: {
                files: [
                    '<%= app_files.js %>'
                ],
                tasks: ['copy:app_js']
            },

            /**
             * When assets are changed, copy them. Note that this will *not* copy new
             * files, so this is probably not very useful.
             */
            assets: {
                files: [
                    '<%= src_folder %>/assets/**',
                    '<%= src_folder %>/locale/**'
                ],
                tasks: ['copy:app_assets']
            },

            /**
             * When *.html changes, we need to compile it.
             */
            html: {
                files: ['<%= app_files.html %>'],
                tasks: ['processhtml:build']
            },

            /**
             * When our templates change, we only rewrite the template cache.
             */
            tpls: {
                files: [
                    '<%= app_files.mtpl %>',
                    '<%= app_files.ctpl %>'
                ],
                tasks: ['html2js']
            },

            /**
             * When the CSS files change, we need to compile and minify them.
             */
            less: {
                files: ['<%= src_folder %>/**/*.less'],
                tasks: ['less:build', 'concat:build_css']
            },

            /**
             * When a JavaScript unit test file changes, we only want to lint it and
             * run the unit tests. We don't want to do any live reloading.
             */
            jsunit: {
                files: [
                    '<%= app_files.jsunit %>'
                ],
                tasks: ['jshint:test', 'karma:unit:run'],
                options: {
                    livereload: false
                }
            }

        },

        connect: {
            server: {
                options: {
                    hostname: '*',
                    port: 8001,
                    base: '<%= build_folder %>',
                    middleware: function(connect, options) {
                        if (!Array.isArray(options.base)) {
                            options.base = [options.base];
                        }

                        // Setup the proxy
                        var middlewares = [require('grunt-connect-proxy/lib/utils').proxyRequest];

                        // Serve static files
                        options.base.forEach(function(base) {
                            middlewares.push(connect.static(base));
                        });

                        // Make directory browse-able
                        var directory = options.directory || options.base[options.base.length - 1];
                        middlewares.push(connect.directory(directory));
                        return middlewares;
                    }
                },
                proxies: [
                    {
                        context: '/vital-orchestrator-web',
                        host: 'localhost',
                        port: 8080,
                        https: false,
                        changeOrigin: false
                    }
                ]
            },
            distServer: {
                options: {
                    hostname: 'localhost',
                    port: 8001,
                    base: '<%= dist_folder %>',
                    keepalive: true,
                    middleware: function(connect, options) {
                        if (!Array.isArray(options.base)) {
                            options.base = [options.base];
                        }

                        // Setup the proxy
                        var middlewares = [require('grunt-connect-proxy/lib/utils').proxyRequest];

                        // Serve static files
                        options.base.forEach(function(base) {
                            middlewares.push(connect.static(base));
                        });

                        // Make directory browse-able
                        var directory = options.directory || options.base[options.base.length - 1];
                        middlewares.push(connect.directory(directory));

                        return middlewares;
                    }
                },
                proxies: [
                    {
                        context: '/vital-orchestrator-web',
                        host: 'localhost',
                        port: 8080,
                        https: false,
                        changeOrigin: false
                    }
                ]
            }
        }
    });

    /**
     * In order to make it safe to just compile or copy *only* what was changed,
     * we need to ensure we are starting from a clean, fresh build. So we rename
     * the `watch` task to `delta` (that's why the configuration var above is
     * `delta`) and then add a new task called `watch` that does a clean build
     * before watching for changes.
     */
    grunt.renameTask('watch', 'delta');

    grunt.registerTask('watch', ['build', 'delta']);
    grunt.registerTask('server', ['configureProxies:server', 'connect:server', 'watch']);
    grunt.registerTask('dist-server', ['dist', 'configureProxies:distServer', 'connect:distServer']);

    /**
     * The `build` task gets your app ready to run for development and testing.
     */
    grunt.registerTask('build', [
        'clean',
        'html2js',
        'jshint',
        'less:build',
        'concat:build_css',
        'copy:app_assets',
        'copy:vendor_assets',
        'copy:app_js',
        'copy:vendor_js',
        'processhtml:build'
    ]);

    grunt.registerTask('test', [
        'build',
        'karma:continuous'
    ]);

    /**
     * The `compile` task gets your app ready for deployment by concatenating and
     * minifying your code.
     */
    grunt.registerTask('dist', [
        'build',
        'copy:dist_assets',
        'concat:dist_js',
        'uglify',
        'cssmin',
        'processhtml:dist'
    ]);

    /**
     * The default task is to build and create distribution.
     */
    grunt.registerTask('default', ['dist']);

}
;

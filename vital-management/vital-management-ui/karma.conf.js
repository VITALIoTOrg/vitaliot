// Karma configuration
// http://karma-runner.github.io/0.10/config/configuration-file.html

module.exports = function (config) {
	config.set({
		// base path, that will be used to resolve files and exclude
		basePath: '',

		// list of files / patterns to load in the browser
		files: [
			'vendor/jquery/dist/jquery.js',
			'vendor/angular/angular.js',
			'vendor/angular-route/angular-route.js',
			'vendor/angular-cookies/angular-cookies.js',
			'vendor/angular-translate/angular-translate.js',
			'vendor/angular-translate-storage-cookie/angular-translate-storage-cookie.js',
			'vendor/angular-translate-loader-static-files/angular-translate-loader-static-files.js',
			'vendor/angular-foundation/mm-foundation.js',
			'vendor/angular-foundation/mm-foundation-tpls.js',
			'vendor/angular-mocks/angular-mocks.js',
			'vendor/ng-grid/ng-grid-2.0.7.debug.js',
			'build/templates*.js',
			'src/js/**/*.js'
		],

		// list of files / patterns to exclude
		exclude: [
			'src/assets/**/*.js'
		],

		// testing framework to use (jasmine/mocha/qunit/...)
		frameworks: ['jasmine'],

		plugins: [ 'karma-jasmine', 'karma-firefox-launcher', 'karma-chrome-launcher', 'karma-phantomjs-launcher' ],

		/**
		 * How to report, by default.
		 */
		reporters: 'dots',

		/**
		 * On which port should the browser connect, on which port is the test runner
		 * operating, and what is the URL path for the browser to use.
		 */

		port: 9018,
		runnerPort: 9100,
		urlRoot: '/',

		// level of logging
		// possible values: LOG_DISABLE || LOG_ERROR || LOG_WARN || LOG_INFO || LOG_DEBUG
		logLevel: config.LOG_INFO,

		/**
		 * Disable file watching by default.
		 */
		autoWatch: false,

		/**
		 * The list of browsers to launch to test on. This includes only "Firefox" by
		 * default, but other browser names include:
		 * Chrome, ChromeCanary, Firefox, Opera, Safari, PhantomJS
		 *
		 * Note that you can also use the executable name of the browser, like "chromium"
		 * or "firefox", but that these vary based on your operating system.
		 *
		 * You may also leave this blank and manually navigate your browser to
		 * http://localhost:9018/ when you're running tests. The window/tab can be left
		 * open and the tests will automatically occur there during the build. This has
		 * the aesthetic advantage of not launching a browser every time you save.
		 */
		browsers: [
			'PhantomJS'
		]
	});
};

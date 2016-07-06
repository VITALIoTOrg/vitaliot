'use strict';
angular.module('common.widgets.services', [])

    .directive('widgetServices', [
        '$interval', 'serviceResource',
        function($interval, serviceResource) {
            return {
                restrict: 'EA',
                templateUrl: 'common/widgets/services.tpl.html',
                scope: {
                    system: '='
                },
                link: function(scope, element, attrs) {
                    scope.data = {
                        serviceList: []
                    };

                    scope.actions = {
                        fetchServices: function() {
                            serviceResource.fetchBySystem(scope.system['@id'])
                                .then(function(services) {
                                    angular.forEach(services, function(service) {
                                        if (!angular.isArray(service['http://iserve.kmi.open.ac.uk/ns/msm#hasOperation'])) {
                                            service['http://iserve.kmi.open.ac.uk/ns/msm#hasOperation'] = [
                                                service['http://iserve.kmi.open.ac.uk/ns/msm#hasOperation']
                                            ];
                                        }
                                    });
                                    scope.data.serviceList = services;
                                });
                        }
                    };

                    // Init:
                    scope.actions.fetchServices();

                }
            };
        }
    ]);

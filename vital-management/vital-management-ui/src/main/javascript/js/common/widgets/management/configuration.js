'use strict';
angular.module('common.widgets.configuration', [
    'common.resources.management'
])

    .directive('widgetConfiguration', [
        '$interval', 'managementResource',
        function($interval, managementResource) {
            return {
                restrict: 'EA',
                templateUrl: 'common/widgets/management/configuration.tpl.html',
                scope: {
                    system: '='
                },
                controllerAs: 'widgetConfigurationCtrl',
                bindToController: true,
                controller: [
                    function() {
                        var ctrl = this;
                        ctrl.config = {};

                        ctrl.actions = {
                            errors: {
                                data: [],
                                clear: function() {
                                    ctrl.actions.errors.data.length = 0;
                                },
                                add: function(error) {
                                    ctrl.actions.errors.data.push(error);
                                }
                            },

                            saving: false,

                            save: function(ngFormController) {
                                function canSave() {
                                    return ngFormController.$valid;
                                }

                                var params;

                                if (canSave()) {
                                    ctrl.actions.errors.clear();

                                    params = [];
                                    angular.forEach(ctrl.config.parameters, function(param) {
                                        if (param.permissions === 'rw') {
                                            params.push({
                                                name: param.name,
                                                value: param.value
                                            });
                                        }
                                    });
                                    ctrl.actions.saving = true;
                                    managementResource.saveConfiguration(ctrl.system['@id'], {
                                        parameters: params
                                    }).then(function(config) {
                                        angular.copy(config, ctrl.config);
                                        ctrl.actions.saving = false;
                                    }, function(error) {
                                        ctrl.actions.errors.add(error);
                                        ctrl.actions.saving = false;
                                    });
                                }
                            }
                        };


                        //Init:
                        managementResource.fetchConfiguration(ctrl.system['@id'])
                            .then(function(config) {
                                angular.copy(config, ctrl.config);
                            }, function(error) {
                                ctrl.actions.errors.add(error);
                            });
                    }
                ]
            };
        }
    ]);

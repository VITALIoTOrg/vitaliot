'use strict';
angular.module('main.security', [
    'ngRoute',
    'main.security.resource'
])
    .config(['$routeProvider', function($routeProvider) {

        $routeProvider.when('/security/systconf', {
            templateUrl: 'main/security/security-systconfig.tpl.html',
            controller: 'SecuritySystemConfigController'
        });

        $routeProvider.when('/security/datacontrol', {
            templateUrl: 'main/security/security-datacontrol.tpl.html',
            controller: 'SecurityDataControlController'
        });

        $routeProvider.when('/security/users', {
            templateUrl: 'main/security/security-users.tpl.html',
            controller: 'SecurityUsersController'
        });

        $routeProvider.when('/security/groups', {
            templateUrl: 'main/security/security-groups.tpl.html',
            controller: 'SecurityGroupsController'
        });

        $routeProvider.when('/security/policies', {
            templateUrl: 'main/security/security-policies.tpl.html',
            controller: 'SecurityPoliciesController'
        });

        $routeProvider.when('/security/applications', {
            templateUrl: 'main/security/security-applications.tpl.html',
            controller: 'SecurityApplicationsController'
        });

        $routeProvider.when('/security/monitor', {
            templateUrl: 'main/security/security-monitor.tpl.html',
            controller: 'SecurityMonitorController'
        });

        $routeProvider.when('/security/access', {
            templateUrl: 'main/security/security-access.tpl.html',
            controller: 'SecurityAccessController'
        });

        $routeProvider.when('/security/changepass', {
            templateUrl: 'main/security/security-changepass.tpl.html',
            controller: 'SecurityChangePassController'
        });

        $routeProvider.when('/security/users/create', {
            templateUrl: 'main/security/security-create_user.tpl.html',
            controller: 'SecurityCreateUserController'
        });

        $routeProvider.when('/security/users/details', {
            templateUrl: 'main/security/security-user_details.tpl.html',
            controller: 'SecurityUserDetailsController'
        });

        $routeProvider.when('/security/groups/create', {
            templateUrl: 'main/security/security-create_group.tpl.html',
            controller: 'SecurityCreateGroupController'
        });

        $routeProvider.when('/security/groups/details', {
            templateUrl: 'main/security/security-group_details.tpl.html',
            controller: 'SecurityGroupDetailsController'
        });

        $routeProvider.when('/security/policies/create', {
            templateUrl: 'main/security/security-create_policy.tpl.html',
            controller: 'SecurityCreatePolicyController'
        });

        $routeProvider.when('/security/policies/details', {
            templateUrl: 'main/security/security-policy_details.tpl.html',
            controller: 'SecurityPolicyDetailsController'
        });

        $routeProvider.when('/security/applications/create', {
            templateUrl: 'main/security/security-create_application.tpl.html',
            controller: 'SecurityCreateApplicationController'
        });

        $routeProvider.when('/security/applications/details', {
            templateUrl: 'main/security/security-application_details.tpl.html',
            controller: 'SecurityApplicationDetailsController'
        });

        $routeProvider.when('/security/register', {
            templateUrl: 'main/security/security-register.tpl.html',
            controller: 'SecurityRegisterController'
        });

    }])

/**
 * SecuritySystemConfigController
 */
    .controller('SecuritySystemConfigController', [
        '$scope', 'securityResource', '$window',
        function($scope, securityResource, $window) {
            $window.alert('This page is only a tentative mockup! It won\'t work');
            $scope.resources = ['Advanced_Users', 'Dev_Users', 'Base_Users'];
            $scope.addGroup = function(data) {
                securityResource.createGroup(data, $scope);
            };
        }
    ])

/**
 * SecurityDataControlController
 */
    .controller('SecurityDataControlController', [
        '$scope', 'securityResource',
        function($scope, securityResource) {
            $scope.resources = ['Advanced_Users', 'Dev_Users', 'Base_Users'];
            $scope.addGroup = function(data) {
                securityResource.createGroup(data, $scope);
            };
        }
    ])

/**
 * SecurityUsersController
 */
    .controller('SecurityUsersController', [
        '$scope', 'securityResource',
        function($scope, securityResource) {
            $scope.deleting = false;
            $scope.gotUsers = false;
            $scope.getUsersError = false;
            securityResource.getUsers($scope);
            $scope.delUser = function(name) {
                $scope.todelete = name;
                $scope.deleting = true;
                securityResource.deleteUser(name, $scope);
            };
        }
    ])

/**
 * SecurityGroupsController
 */
    .controller('SecurityGroupsController', [
        '$scope', 'securityResource',
        function($scope, securityResource) {
            $scope.deleting = false;
            $scope.gotGroups = false;
            $scope.getGroupsError = false;
            securityResource.getGroups($scope);
            $scope.delGroup = function(name) {
                $scope.todelete = name;
                $scope.deleting = true;
                securityResource.deleteGroup(name, $scope);
            };
        }
    ])

/**
 * SecurityPoliciesController
 */
    .controller('SecurityPoliciesController', [
        '$scope', 'securityResource',
        function($scope, securityResource) {
            $scope.deleting = false;
            $scope.gotPolicies = false;
            $scope.getPoliciesError = false;
            securityResource.getPolicies($scope);
            $scope.delPolicy = function(name) {
                $scope.todelete = name;
                $scope.deleting = true;
                securityResource.deletePolicy(name, $scope);
            };
        }
    ])

/**
 * SecurityApplicationsController
 */
    .controller('SecurityApplicationsController', [
        '$scope', '$routeParams', 'securityResource',
        function($scope, $routeParams, securityResource) {
            $scope.deleting = false;
            $scope.gotApplications = false;
            $scope.getApplicationsError = false;
            securityResource.getApplications($scope, $routeParams);
            $scope.delApplication = function(name) {
                $scope.todelete = name;
                $scope.deleting = true;
                securityResource.deleteApplication(name, $scope, $routeParams);
            };
        }
    ])

/**
 * SecurityMonitorController
 */
    .controller('SecurityMonitorController', [
        '$scope', 'securityResource',
        function($scope, securityResource) {
            $scope.gotStats = false;
            $scope.getStatsError = false;
            securityResource.getStats($scope);
        }
    ])

/**
 * SecurityAccessController
 */
    .controller('SecurityAccessController', [
        '$scope', 'securityResource',
        function($scope, securityResource) {
            $scope.getting = false;
            $scope.evaluating = false;
            $scope.loginLoading = false;
            $scope.logoutLoading = false;
            $scope.loggedIn = false;
            $scope.gotEvaluation = false;
            $scope.getEvaluationError = false;
            $scope.evaluationOk = false;
            $scope.gotId = false;
            $scope.gotPermissions = false;
            $scope.gotResContent = false;
            securityResource.getId($scope, false);
            $scope.login = function(data) {
                $scope.loginLoading = true;
                securityResource.authenticate(data, $scope, false);
            };
            $scope.logout = function() {
                $scope.logoutLoading = true;
                securityResource.logout($scope, false);
            };
            $scope.reset = function() {
                $scope.gotEvaluation = false;
                $scope.getEvaluationError = false;
                $scope.evaluationOk = false;
            };
            $scope.evaluate = function(data) {
                $scope.evaluating = true;
                $scope.evaluationOk = false;
                $scope.getEvaluationError = false;
                $scope.gotEvaluation = false;
                var formData = {};
                formData.resources = [];
                formData.resources.push(data.res);
                $scope.resource = data.res;
                data.res = null;
                securityResource.evaluate(formData, $scope);
            };
        }
    ])

/**
 * SecurityChangePassController
 */
    .controller('SecurityChangePassController', [
        '$scope', 'securityResource', 'Shared',
        function($scope, securityResource, Shared) {
            $scope.changing = false;
            $scope.isSignedIn = function() {
                return Shared.signedIn;
            };
            $scope.changePass = function(data) {
                $scope.changing = true;
                securityResource.changePassword(data, $scope);
            };
        }
    ])

/**
 * SecurityCreateUserController
 */
    .controller('SecurityCreateUserController', [
        '$scope', 'securityResource',
        function($scope, securityResource) {
            $scope.creating = false;
            $scope.addUser = function(data) {
                $scope.creating = true;
                securityResource.createUser(data, $scope);
            };
        }
    ])

/**
 * SecurityUserDetailsController
 */
    .controller('SecurityUserDetailsController', [
        '$scope', '$routeParams', 'securityResource',
        function($scope, $routeParams, securityResource) {
            $scope.gotUser = false;
            $scope.gotGroups = false;
            $scope.wrongpars = true;
            $scope.getUserError = false;
            $scope.getGroupsError = false;
            $scope.edit = false;
            $scope.saving = false;
            $scope.adding = false;
            $scope.removing = false;

            if($routeParams.hasOwnProperty('name') && $routeParams.name !== '' && $routeParams.name !== null && (typeof $routeParams.name === 'string')) {
                $scope.wrongpars = false;
                $scope.enableEdit = function() {
                    $scope.edit = true;
                };
                $scope.disableEdit = function() {
                    $scope.edit = false;
                };
                securityResource.getGroups($scope);
                securityResource.getUser($scope, $routeParams.name);
                $scope.addUserToGroup = function(group) {
                    $scope.adding = true;
                    securityResource.addUser2Group(group, $routeParams.name, $scope);
                };
                $scope.rmUserGroup = function(group) {
                    $scope.toremove = group;
                    $scope.removing = true;
                    securityResource.delUserFromGroup(group, $routeParams.name, $scope);
                };
                $scope.updUser = function(formData) {
                    if($scope.edit === true) {
                        $scope.saving = true;
                        securityResource.updateUser($routeParams.name, formData, $scope);
                    }
                };
            }
            else {
                $scope.gotUser = true;
                $scope.gotGroups = true;
            }
        }
    ])

/**
 * SecurityCreateGroupController
 */
    .controller('SecurityCreateGroupController', [
        '$scope', 'securityResource',
        function($scope, securityResource) {
            $scope.creating = false;
            $scope.addGroup = function(data) {
                $scope.creating = true;
                securityResource.createGroup(data, $scope);
            };
        }
    ])

/**
 * SecurityGroupDetailsController
 */
    .controller('SecurityGroupDetailsController', [
        '$scope', '$routeParams', 'securityResource',
        function($scope, $routeParams, securityResource) {
            $scope.gotGroup = false;
            $scope.gotUsers = false;
            $scope.wrongpars = true;
            $scope.getGroupError = false;
            $scope.getUsersError = false;
            $scope.adding = false;
            $scope.removing = false;

            if($routeParams.hasOwnProperty('name') && $routeParams.name !== '' && $routeParams.name !== null && (typeof $routeParams.name === 'string')) {
                $scope.wrongpars = false;
                securityResource.getUsers($scope);
                securityResource.getGroup($scope, $routeParams.name);
                $scope.addUserToGroup = function(user) {
                    $scope.adding = true;
                    securityResource.addUser2Group($routeParams.name, user, $scope);
                };
                $scope.rmUserGroup = function(user) {
                    $scope.toremove = user;
                    $scope.removing = true;
                    securityResource.delUserFromGroup($routeParams.name, user, $scope);
                };
            }
            else {
                $scope.gotGroup = true;
                $scope.gotUsers = true;
            }
        }
    ])

/**
 * SecurityCreatePolicyController
 */
    .controller('SecurityCreatePolicyController', [
        '$scope', '$routeParams', 'securityResource',
        function($scope, $routeParams, securityResource) {
            $scope.gotApplications = false;
            $scope.getApplicationsError = false;
            $scope.gotGroups = false;
            $scope.getGroupsError = false;
            $scope.creating = false;

            $scope.nores = false;
            $scope.nogr = false;
            $scope.adderr = false;

            securityResource.getGroups($scope);
            securityResource.getApplications($scope, $routeParams);
            $scope.addPolicy = function(data) {
                if(data.resources.length === 0) {
                    $scope.nores = true;
                }
                else {
                    $scope.nores = false;
                }
                if(!$scope.nores) {
                    $scope.creating = true;
                    securityResource.createPolicy(data, $scope);
                }
            };
            $scope.addRes = function(res) {
                if(res !== null && res !== '') {
                    if($scope.data.resources.indexOf(res) === -1) {
                        $scope.data.resources.push(res);
                        $scope.tmp.res = null;
                        $scope.nores = false;
                        $scope.adderr = false;
                    }
                    else {
                        $scope.adderr = true;
                    }
                }
            };
            $scope.rmRes = function(res) {
                var index = $scope.data.resources.indexOf(res);
                $scope.data.resources.splice(index, 1);
            };
            $scope.addGr = function(gr) {
                $scope.data.groups.push(gr);
            };
            $scope.rmGr = function(gr) {
                var index = $scope.data.groups.indexOf(gr);
                $scope.data.groups.splice(index, 1);
            };
            $scope.delAction = function(actions, action) {
                if(typeof actions !== 'undefined') {
                    if(actions.hasOwnProperty(action)) {
                        delete actions[action];
                    }
                }
            };
        }
    ])

/**
 * SecurityPolicyDetailsController
 */
    .controller('SecurityPolicyDetailsController', [
        '$scope', '$routeParams', 'securityResource',
        function($scope, $routeParams, securityResource) {

            $scope.gotPolicy = false;
            $scope.gotGroups = false;
            $scope.wrongpars = true;
            $scope.getPolicyError = false;
            $scope.getGroupsError = false;
            $scope.edit = false;
            $scope.actionsEdit = false;
            $scope.duplicate = false;
            $scope.lastone = false;
            $scope.saving = false;
            $scope.addingGr = false;
            $scope.removingGr = false;
            $scope.addingRes = false;
            $scope.removingRes = false;
            $scope.savingAct = false;

            if($routeParams.hasOwnProperty('name') && $routeParams.name !== '' && $routeParams.name !== null && (typeof $routeParams.name === 'string')) {
                $scope.wrongpars = false;
                $scope.enableEdit = function() {
                    $scope.edit = true;
                };
                $scope.disableEdit = function() {
                    $scope.edit = false;
                };
                $scope.enableActionsEdit = function() {
                    $scope.actionsEdit = true;
                };
                $scope.disableActionsEdit = function() {
                    $scope.actionsEdit = false;
                };
                securityResource.getGroups($scope);
                securityResource.getPolicy($scope, $routeParams.name);
                $scope.addGr = function(group) {
                    $scope.addingGr = true;
                    var info = {};
                    info.groups = JSON.parse(JSON.stringify($scope.policyGroups)); // deep copy
                    info.resources = JSON.parse(JSON.stringify($scope.policy.resources)); // deep copy
                    info.groups.push(group);
                    info.name = $scope.policy.name;
                    info.active = $scope.policy.active;
                    info.description = $scope.policy.description;
                    info.nogr = false;
                    info.nores = false;
                    info.noact = false;
                    if(info.groups.length === 0) {
                        info.nogr = true;
                    }
                    if(info.resources.length === 0) {
                        info.nores = true;
                    }
                    securityResource.updatePolicy($routeParams.name, info, $scope, 'addGr');
                };
                $scope.rmGr = function(group) {
                    $scope.toremoveGr = group;
                    $scope.removingGr = true;
                    var info = {};
                    info.groups = JSON.parse(JSON.stringify($scope.policyGroups)); // deep copy
                    info.resources = JSON.parse(JSON.stringify($scope.policy.resources)); // deep copy
                    var index = info.groups.indexOf(group);
                    info.groups.splice(index, 1);
                    info.name = $scope.policy.name;
                    info.active = $scope.policy.active;
                    info.description = $scope.policy.description;
                    info.nogr = false;
                    info.nores = false;
                    info.noact = false;
                    if(info.groups.length === 0) {
                        info.nogr = true;
                    }
                    if(info.resources.length === 0) {
                        info.nores = true;
                    }
                    securityResource.updatePolicy($routeParams.name, info, $scope, 'rmGr');
                };
                $scope.addRes = function(resource) {
                    var info = {};
                    info.groups = JSON.parse(JSON.stringify($scope.policyGroups)); // deep copy
                    info.resources = JSON.parse(JSON.stringify($scope.policy.resources)); // deep copy
                    if(info.resources.indexOf(resource) === -1) {
                        $scope.addingRes = true;
                        $scope.duplicate = false;
                        info.resources.push(resource);
                        info.name = $scope.policy.name;
                        info.active = $scope.policy.active;
                        info.description = $scope.policy.description;
                        info.nogr = false;
                        info.nores = false;
                        info.noact = false;
                        if(info.groups.length === 0) {
                            info.nogr = true;
                        }
                        if(info.resources.length === 0) {
                            info.nores = true;
                        }
                        $scope.lastone = false;
                        securityResource.updatePolicy($routeParams.name, info, $scope, 'addRes');
                        $scope.tmp.res = null;
                    }
                    else {
                        $scope.duplicate = true;
                    }
                };
                $scope.rmRes = function(resource) {
                    var info = {};
                    info.groups = JSON.parse(JSON.stringify($scope.policyGroups)); // deep copy
                    info.resources = JSON.parse(JSON.stringify($scope.policy.resources)); // deep copy
                    var index = info.resources.indexOf(resource);
                    info.resources.splice(index, 1);
                    info.name = $scope.policy.name;
                    info.active = $scope.policy.active;
                    info.description = $scope.policy.description;
                    info.nores = false;
                    info.nogr = false;
                    info.noact = false;
                    if(info.groups.length === 0) {
                        info.nogr = true;
                    }
                    if(info.resources.length === 0) {
                        $scope.lastone = true;
                        info.nores = true;
                    }
                    else {
                        $scope.toremoveRes = resource;
                        $scope.removingRes = true;
                        securityResource.updatePolicy($routeParams.name, info, $scope, 'rmRes');
                    }
                };
                $scope.updPolicy = function(formData) {
                    if($scope.edit === true) {
                        $scope.saving = true;
                        formData.nores = false;
                        formData.nogr = false;
                        formData.noact = false;
                        securityResource.updatePolicy($routeParams.name, formData, $scope, 'upd');
                    }
                };
                $scope.updActions = function(actions) {
                    if($scope.actionsEdit === true) {
                        $scope.savingAct = true;
                        var formData = {};
                        formData.nores = false;
                        formData.nogr = false;
                        formData.noact = true;
                        formData.actions = actions;
                        formData.name = $scope.policy.name;
                        formData.active = $scope.policy.active;
                        formData.description = $scope.policy.description;
                        securityResource.updatePolicy($routeParams.name, formData, $scope, 'actionsUpd');
                    }
                };
                $scope.delAction = function(actions, action) {
                    if(typeof actions !== 'undefined') {
                        if(actions.hasOwnProperty(action)) {
                            delete actions[action];
                        }
                    }
                };
            }
            else {
                $scope.gotPolicy = true;
                $scope.gotGroups = true;
            }
        }
    ])


/**
 * SecurityCreateApplicationController
 */
    .controller('SecurityCreateApplicationController', [
        '$scope', 'securityResource',
        function($scope, securityResource) {
            $scope.nores = false;
            $scope.adderr = false;
            $scope.creating = false;

            securityResource.getApplicationTypes($scope);

            $scope.addApplication = function(data) {
                if(data.resources.length === 0) {
                    $scope.nores = true;
                }
                else {
                    $scope.nores = false;
                }
                if(!$scope.nores) {
                    $scope.creating = true;
                    securityResource.createApplication(data, $scope);
                }
            };
            $scope.addRes = function(res) {
                if(res !== null && res !== '') {
                    if($scope.data.resources.indexOf(res) === -1) {
                        $scope.data.resources.push(res);
                        $scope.tmp.res = null;
                        $scope.nores = false;
                        $scope.adderr = false;
                    }
                    else {
                        $scope.adderr = true;
                    }
                }
            };
            $scope.rmRes = function(res) {
                var index = $scope.data.resources.indexOf(res);
                $scope.data.resources.splice(index, 1);
            };
        }
    ])

/**
 * SecurityApplicationDetailsController
 */
    .controller('SecurityApplicationDetailsController', [
        '$scope', '$routeParams', 'securityResource',
        function($scope, $routeParams, securityResource) {

            $scope.gotApplication = false;
            $scope.gotApplicationPolicies = false;
            $scope.wrongpars = true;
            $scope.getApplicationError = false;
            $scope.edit = false;
            $scope.actionsEdit = false;
            $scope.duplicate = false;
            $scope.lastone = false;
            $scope.refreshedPol = false;
            $scope.deleting = false;
            $scope.saving = false;
            $scope.addingRes = false;
            $scope.removingRes = false;
            $scope.savingAct = false;

            if($routeParams.hasOwnProperty('name') && $routeParams.name !== '' && $routeParams.name !== null && (typeof $routeParams.name === 'string')) {
                $scope.wrongpars = false;
                $scope.enableEdit = function() {
                    $scope.edit = true;
                };
                $scope.disableEdit = function() {
                    $scope.edit = false;
                };
                $scope.enableActionsEdit = function() {
                    $scope.actionsEdit = true;
                };
                $scope.disableActionsEdit = function() {
                    $scope.actionsEdit = false;
                };
                securityResource.getApplication($scope, $routeParams.name);
                securityResource.getApplicationPolicies($scope, $routeParams.name);
                $scope.rmPol = function(policyName) {
                    $scope.todelete = policyName;
                    $scope.deleting = true;
                    $scope.refreshedPol = false;
                    securityResource.deletePolicy(policyName, $scope).then(function() { securityResource.getApplicationPolicies($scope, $routeParams.name); });
                };
                $scope.addRes = function(resource) {
                    var info = {};
                    info.resources = JSON.parse(JSON.stringify($scope.application.resources)); // deep copy
                    if(info.resources.indexOf(resource) === -1) {
                        $scope.addingRes = true;
                        $scope.duplicate = false;
                        info.resources.push(resource);
                        info.name = $scope.application.name;
                        info.description = $scope.application.description;
                        info.type = $scope.application.applicationType;
                        info.nores = false;
                        info.noact = false;
                        if(info.resources.length === 0) {
                            info.nores = true;
                        }
                        $scope.lastone = false;
                        securityResource.updateApplication($routeParams.name, info, $scope, 'addRes');
                        $scope.tmp.res = null;
                    }
                    else {
                        $scope.duplicate = true;
                    }
                };
                $scope.rmRes = function(resource) {
                    var info = {};
                    info.resources = JSON.parse(JSON.stringify($scope.application.resources)); // deep copy
                    var index = info.resources.indexOf(resource);
                    info.resources.splice(index, 1);
                    info.name = $scope.application.name;
                    info.description = $scope.application.description;
                    info.type = $scope.application.applicationType;
                    info.nores = false;
                    info.noact = false;
                    if(info.resources.length === 0) {
                        $scope.lastone = true;
                        info.nores = true;
                    }
                    else {
                        $scope.toremoveRes = resource;
                        $scope.removingRes = true;
                        securityResource.updateApplication($routeParams.name, info, $scope, 'rmRes');
                    }
                };
                $scope.updApplication = function(formData) {
                    $scope.saving = true;
                    if($scope.edit === true) {
                        formData.nores = false;
                        formData.noact = false;
                        formData.type = $scope.application.applicationType;
                        securityResource.updateApplication($routeParams.name, formData, $scope, 'upd');
                    }
                };
                $scope.updActions = function(actions) {
                    if($scope.actionsEdit === true) {
                        $scope.savingAct = true;
                        var formData = {};
                        formData.nores = false;
                        formData.noact = true;
                        formData.actions = actions;
                        formData.id = $scope.application.name;
                        formData.type = $scope.application.applicationType;
                        formData.description = $scope.application.description;
                        securityResource.updateApplication($routeParams.name, formData, $scope, 'actionsUpd');
                    }
                };
                $scope.delAction = function(actions, action) {
                    if(typeof actions !== 'undefined') {
                        if(actions.hasOwnProperty(action)) {
                            delete actions[action];
                        }
                    }
                };
            }
            else {
                $scope.gotApplication = true;
            }
        }
    ])

/**
 * SecurityRegisterController
 */
    .controller('SecurityRegisterController', [
        '$scope', '$routeParams', 'securityResource',
        function($scope, $routeParams, securityResource) {
            $scope.confirmation = false;
            $scope.creating = false;
            $scope.registering = false;
            if($routeParams.hasOwnProperty('confirmationId') && $routeParams.hasOwnProperty('email') && $routeParams.hasOwnProperty('tokenId') && $routeParams.hasOwnProperty('realm')) {
                $scope.confirmation = true;
                $scope.signup = function(formData) {
                    $scope.creating = true;
                    formData.confirmationId = $routeParams.confirmationId;
                    formData.tokenId = $routeParams.tokenId;
                    console.log(formData.tokenId);
                    formData.mail = $routeParams.email;
                    securityResource.signup(formData, $scope);
                };
            } else {
                $scope.register = function(formData) {
                    $scope.registering = true;
                    securityResource.register(formData, $scope);
                };
            }
        }
    ]);


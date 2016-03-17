'use strict';
angular.module('common.resources.security', [])
   .factory('securityResource', [
        '$route', '$http', '$q', '$location', 'API_PATH', 'Shared',
        function ($route, $http, $q, $location, API_PATH, Shared) {

            var basePath = API_PATH.replace('/api', '/security');

            // The public API of the service
            var service = {

                authenticate: function(formData, $scope, genlog) {
                    formData.testCookie = !genlog;
                    return $http({
                        method: 'POST',
                        url: basePath + '/authenticate',
                        data: $.param(formData),
                        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                        withCredentials: true,
                        params: { 'foobar': new Date().getTime() }
                    })
                    .then(function(response) {
                        if(genlog === true) {
                            $scope.respLogin = response;
                            $scope.loggedUser = {};
                            $scope.loggedUser.uid = response.data.uid;
                            if(response.data.hasOwnProperty('name')) {
                                $scope.loggedUser.name = response.data.name;
                            }
                            else {
                                $scope.loggedUser.name = response.data.uid;
                            }
                            if(response.data.hasOwnProperty('fullname')) {
                                $scope.loggedUser.fullname = response.data.fullname;
                            }
                            else {
                                $scope.loggedUser.fullname = response.data.uid;
                            }
                            $scope.loggedUser.avatar = 'https://www.gravatar.com/avatar/';
                            if(response.data.hasOwnProperty('mailhash')) {
                                $scope.loggedUser.avatar = $scope.loggedUser.avatar + response.data.mailhash;
                            }
                            if(response.data.hasOwnProperty('creation')) {
                                $scope.loggedUser.creation = response.data.creation;
                            }
                            else {
                                $scope.loggedUser.creation = false;
                            }
                            Shared.signedIn = true;
                            Shared.loggedUser = $scope.loggedUser;
                            $scope.genloginLoading = false;
                        }
                        else {
                            $scope.response = response;
                            $scope.username = response.data.uid;
                            $scope.getIdError = false;
                            $scope.loggedIn = true;
                            $scope.loginLoading = false;
                            service.permissions($scope);
                        }
                    }, function(response) {
                        if(genlog === true) {
                            $scope.respLogin = response;
                            $scope.genloginLoading = false;
                        }
                        else {
                            $scope.response = response;
                            $scope.loginLoading = false;
                        }
                    });
                },

                forgetLogin: function() {
                    Shared.signedIn = false;
                    $location.path('/login');
                },

                logout: function($scope, genlog) {
                    var formData = {};
                    formData.testCookie = !genlog;
                    return $http({
                        method: 'POST',
                        url: basePath + '/logout',
                        data: $.param(formData),
                        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                        withCredentials: true,
                        params: { 'foobar': new Date().getTime() }
                    })
                    .then(function(response) {
                        if(genlog === true) {
                            $scope.respLogout = response;
                            service.forgetLogin();
                            $scope.genlogoutLoading = false;
                        }
                        else {
                            $scope.response = response;
                            $scope.loggedIn = false;
                            $scope.reset();
                            $scope.logoutLoading = false;
                        }
                    }, function(response) {
                        if(genlog === true) {
                            $scope.respLogout = response;
                            $scope.genlogoutLoading = false;
                        }
                        else {
                            $scope.response = response;
                            $scope.logoutLoading = false;
                        }
                    });
                },

                getId: function($scope, genlog) {
                    var formData = {};
                    formData.testCookie = !genlog;
                    formData.foobar = new Date().getTime();
                    return $http({
                        method: 'GET',
                        url: basePath + '/user',
                        params: formData,
                        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                        withCredentials: true,
                    })
                    .then(function(response) {
                        if(response.data.valid) {
                            if(genlog) {
                                $scope.loggedUser = {};
                                $scope.loggedUser.uid = response.data.uid;
                                if(response.data.hasOwnProperty('name')) {
                                    $scope.loggedUser.name = response.data.name;
                                }
                                else {
                                    $scope.loggedUser.name = response.data.uid;
                                }
                                if(response.data.hasOwnProperty('fullname')) {
                                    $scope.loggedUser.fullname = response.data.fullname;
                                }
                                else {
                                    $scope.loggedUser.fullname = response.data.uid;
                                }
                                $scope.loggedUser.avatar = 'https://www.gravatar.com/avatar/';
                                if(response.data.hasOwnProperty('mailhash')) {
                                    $scope.loggedUser.avatar = $scope.loggedUser.avatar + response.data.mailhash;
                                }
                                if(response.data.hasOwnProperty('creation')) {
                                    $scope.loggedUser.creation = response.data.creation;
                                }
                                else {
                                    $scope.loggedUser.creation = false;
                                }
                                Shared.signedIn = true;
                                Shared.loggedUser = $scope.loggedUser;
                            }
                            else {
                                $scope.loggedIn = true;
                                $scope.username = response.data.uid;
                                service.permissions($scope);
                            }
                        } else if (genlog) {
                            Shared.signedIn = false
                        }
                        $scope.gotId = true;
                        return response;
                    }, function(response) {
                        $scope.getIdError = true;
                        $scope.getIdResponse = response;
                        $scope.gotId = true;
                        if(response.hasOwnProperty('data') && response.data !== null) {
                            if(response.data.code === 401) {
                                service.forgetLogin();
                            }
                        }
                    });
                },

                validate: function($scope, genlog) {
                    var formData = {};
                    formData.testCookie = !genlog;
                    formData.foobar = new Date().getTime();
                    return $http({
                        method: 'GET',
                        url: basePath + '/validate',
                        params: formData,
                        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                        withCredentials: true
                    })
                    .then(function(response) {
                        return response;
                    }, function(response) {
                        return response;
                    });
                },

                evaluate: function(formData, $scope) {
                    formData.testCookie = true;
                    $http({
                        method: 'POST',
                        url: basePath + '/evaluate',
                        data: $.param(formData),
                        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                        withCredentials: true,
                        params: { 'foobar': new Date().getTime() }
                    })
                    .then(function(response) {
                        $scope.evaluation = response.data;
                        if(Object.keys($scope.evaluation.responses[0].actions).length > 0) {
                            $scope.evaluationOk = true;
                        }
                        $scope.gotEvaluation = true;
                        if($scope.evaluation.responses[0].actions.hasOwnProperty('GET')) {
                            $scope.getting = true;
                            var pars = {};
                            pars.resource = formData.resources[0];
                            pars.testCookie = true;
                            $http({
                                method: 'GET',
                                url: basePath + '/getresource',
                                params: pars,
                                withCredentials: true
                            })
                            .then(function(response) {
                                if(pars.resource.indexOf('istanbul-ppi') >= 0 || pars.resource.indexOf('hireplyppi') >= 0 || pars.resource.indexOf('istanbul-hireply') >= 0) {
                                    $scope.resContent = JSON.stringify(response.data, null, 4);
                                    if($scope.resContent.indexOf('Forbidden') > -1) {
                                        $scope.resContent = response.data;
                                    }
                                }
                                else {
                                    $scope.resContent = response.data;
                                }
                                $scope.gotResContent = true;
                                $scope.getting = false;
                            }, function(response) {
                                $scope.getting = false;
                            });
                        }
                        $scope.evaluating = false;
                    }, function(response) {
                        $scope.getEvaluationError = true;
                        $scope.getEvaluationResponse = response;
                        $scope.gotEvaluation = true;
                        if(response.hasOwnProperty('data') && response.data !== null) {
                            if(response.data.code === 401) {
                                service.forgetLogin();
                            }
                        }
                        $scope.evaluating = false;
                    });
                },

                getUsers: function($scope) {
                    $http({
                        method: 'GET',
                        url: basePath + '/users',
                        withCredentials: true,
                        params: { 'foobar': new Date().getTime() }
                    })
                    .then(function(response) {
                        $scope.users = response.data;
                        $scope.gotUsers = true;
                    }, function(response) {
                        $scope.getUsersError = true;
                        $scope.getUsersResponse = response;
                        $scope.gotUsers = true;
                        if(response.hasOwnProperty('data') && response.data !== null) {
                            if(response.data.code === 401) {
                                service.forgetLogin();
                            }
                        }
                    });
                },

                getGroups: function($scope) {
                    $http({
                        method: 'GET',
                        url: basePath + '/groups',
                        withCredentials: true,
                        params: { 'foobar': new Date().getTime() }
                    })
                    .then(function(response) {
                        $scope.groups = response.data;
                        $scope.gotGroups = true;
                    }, function(response) {
                        $scope.getGroupsError = true;
                        $scope.getGroupsResponse = response;
                        $scope.gotGroups = true;
                        if(response.hasOwnProperty('data') && response.data !== null) {
                            if(response.data.code === 401) {
                                service.forgetLogin();
                            }
                        }
                    });
                },

                getPolicies: function($scope) {
                    $http({
                        method: 'GET',
                        url: basePath + '/policies',
                        withCredentials: true,
                        params: { 'foobar': new Date().getTime() }
                    })
                    .then(function(response) {
                        $scope.policies = response.data;
                        $scope.gotPolicies = true;
                    }, function(response) {
                        $scope.getPoliciesError = true;
                        $scope.getPoliciesResponse = response;
                        $scope.gotPolicies = true;
                        if(response.hasOwnProperty('data') && response.data !== null) {
                            if(response.data.code === 401) {
                                service.forgetLogin();
                            }
                        }
                    });
                },

                getApplications: function($scope, $routeParams) {
                    $http({
                        method: 'GET',
                        url: basePath + '/applications',
                        withCredentials: true,
                        params: { 'foobar': new Date().getTime() }
                    })
                    .then(function(response) {
                        var toTakeOut = [ 'openProvisioning', 'sunIdentityServerLibertyPPService', 'crestPolicyService', 'sunBank', 'paycheck', 'sunIdentityServerDiscoveryService', 'sunAMDelegationService', 'im', 'calendar'];
                        var index;
                        $scope.applications = {};
                        $scope.applications.resultCount = 0;
                        $scope.applications.result = [];
                        for(index = 0; index < response.data.result.length; index++) {
                            if(toTakeOut.indexOf(response.data.result[index].name.trim()) === -1) {
                                $scope.applications.result.push(response.data.result[index]);
                                $scope.applications.resultCount = $scope.applications.resultCount + 1;
                            }
                        }
                        $scope.defaultApp = null;
                        if($routeParams.hasOwnProperty('app') && $routeParams.app !== '' && $routeParams.app !== null && (typeof $routeParams.app === 'string')) {
                            for(index = 0; index < $scope.applications.result.length; index++) {
                                if($routeParams.app === $scope.applications.result[index].name) {
                                    $scope.defaultApp = $routeParams.app;
                                }
                            }
                        }
                        $scope.gotApplications = true;
                    }, function(response) {
                        $scope.getApplicationsError = true;
                        $scope.getApplicationsResponse = response;
                        $scope.gotApplications = true;
                        if(response.hasOwnProperty('data') && response.data !== null) {
                            if(response.data.code === 401) {
                                service.forgetLogin();
                            }
                        }
                    });
                },

                getStats: function($scope) {
                    $http({
                        method: 'GET',
                        url: basePath + '/stats',
                        withCredentials: true,
                        params: { 'foobar': new Date().getTime() }
                    })
                    .then(function(response) {
                        $scope.stats = response.data;
                        $scope.gotStats = true;
                    }, function(response) {
                        $scope.getStatsError = true;
                        $scope.getStatsResponse = response;
                        $scope.gotStats = true;
                    });
                },

                getUserGroups: function($scope, name) {
                    $http({
                        method: 'GET',
                        url: basePath + '/user/' + name + '/groups',
                        withCredentials: true,
                        params: { 'foobar': new Date().getTime() }
                    })
                    .then(function(response) {
                        $scope.userGroups = response.data;
                        $scope.gotUserGroups = true;
                    });
                },

                getApplicationPolicies: function($scope, name) {
                    $http({
                        method: 'GET',
                        url: basePath + '/application/' + name + '/policies',
                        withCredentials: true,
                        params: { 'foobar': new Date().getTime() }
                    })
                    .then(function(response) {
                        $scope.applicationPolicies = response.data;
                        $scope.gotApplicationPolicies = true;
                        $scope.refreshedPol = true;
                    });
                },

                getUser: function($scope, name) {
                    $http({
                        method: 'GET',
                        url: basePath + '/user/' + name,
                        withCredentials: true,
                        params: { 'foobar': new Date().getTime() }
                    })
                    .then(function(response) {
                        $scope.user = response.data;
                        $scope.firstName = '';
                        if($scope.user.hasOwnProperty('givenName') && $scope.user.givenName.length > 0) {
                            $scope.firstName = $scope.user.givenName[0];
                        } else if($scope.user.hasOwnProperty('givenname') && $scope.user.givenname.length > 0) {
                            $scope.firstName = $scope.user.givenname[0];
                        }
                        $scope.gotUser = true;
                        service.getUserGroups($scope, name);
                    }, function(response) {
                        $scope.getUserError = true;
                        $scope.getUserResponse = response;
                        $scope.gotUser = true;
                        if(response.hasOwnProperty('data') && response.data !== null) {
                            if(response.data.code === 401) {
                                service.forgetLogin();
                            }
                        }
                    });
                },

                getGroupUsers: function($scope) {
                    $scope.groupUsers = [];
                    if($scope.group.hasOwnProperty('uniqueMember')) {
                        for(var index = 0; index < $scope.group.uniqueMember.length; index++) {
                            $scope.groupUsers[index] = $scope.group.uniqueMember[index].match(/uid=(.*),ou/g)[0].replace('uid=', '').replace(',ou', '');
                        }
                    }
                    $scope.gotGroupUsers = true;
                },

                getGroup: function($scope, name) {
                    $http({
                        method: 'GET',
                        url: basePath + '/group/' + name,
                        withCredentials: true,
                        params: { 'foobar': new Date().getTime() }
                    })
                    .then(function(response) {
                        $scope.group = response.data;
                        $scope.gotGroup = true;
                        service.getGroupUsers($scope);
                    }, function(response) {
                        $scope.getGroupError = true;
                        $scope.getGroupResponse = response;
                        $scope.gotGroup = true;
                        if(response.hasOwnProperty('data') && response.data !== null) {
                            if(response.data.code === 401) {
                                service.forgetLogin();
                            }
                        }
                    });
                },

                getPolicy: function($scope, name) {
                    $http({
                        method: 'GET',
                        url: basePath + '/policy/' + name,
                        withCredentials: true,
                        params: { 'foobar': new Date().getTime() }
                    })
                    .then(function(response) {
                        $scope.policy = response.data;
                        $scope.policyGroups = [];
                        var type;
                        $scope.poldefined = {};
                        if($scope.policy.subject.type === 'Identity') {
                            for(var index = 0; index < $scope.policy.subject.subjectValues.length; index++) {
                                type = $scope.policy.subject.subjectValues[index].match(/ou=([^,]*),dc/g)[0].replace('ou=', '').replace(',dc', '');
                                if(type === 'group') {
                                    $scope.policyGroups[index] = $scope.policy.subject.subjectValues[index].match(/id=(.*),ou/g)[0].replace('id=', '').replace(',ou', '');
                                }
                            }
                        }
                        service.getApplication($scope, $scope.policy.applicationName)
                        .then(function(responseApp) {
                            if($scope.policy.hasOwnProperty('actionValues')) {
                                for(var action1 in responseApp.data.actions) {
                                    if($scope.policy.actionValues.hasOwnProperty(action1)) {
                                        $scope.poldefined[action1] = true;
                                    }
                                    else {
                                        $scope.policy.actionValues[action1] = false;
                                        $scope.poldefined[action1] = false;
                                    }
                                }
                            }
                            else {
                                for(var action2 in responseApp.data.actions) {
                                    $scope.policy.actionValues[action2] = false;
                                    $scope.poldefined[action2] = false;
                                }
                            }
                            $scope.gotPolicy = true;
                        }, function(responseApp) {
                        });
                    }, function(response) {
                        $scope.getPolicyError = true;
                        $scope.getPolicyResponse = response;
                        $scope.gotPolicy = true;
                        if(response.hasOwnProperty('data') && response.data !== null) {
                            if(response.data.code === 401) {
                                service.forgetLogin();
                            }
                        }
                    });
                },

                getApplication: function($scope, name) {
                    return $http({
                        method: 'GET',
                        url: basePath + '/application/' + name,
                        withCredentials: true,
                        params: { 'foobar': new Date().getTime() }
                    })
                    .then(function(response) {
                        $scope.application = response.data;
                        $scope.appdefined = {};
                        service.getApplicationType($scope, $scope.application.applicationType)
                        .then(function(responseAppType) {
                            if($scope.application.hasOwnProperty('actions')) {
                                for(var action1 in responseAppType.data.actions) {
                                    if($scope.application.actions.hasOwnProperty(action1)) {
                                        $scope.appdefined[action1] = true;
                                    }
                                    else {
                                        $scope.application.actions[action1] = false;
                                        $scope.appdefined[action1] = false;
                                    }
                                }
                            }
                            else {
                                for(var action2 in responseAppType.data.actions) {
                                    $scope.applications.actions[action2] = false;
                                    $scope.appdefined[action2] = false;
                                }
                            }
                            $scope.gotApplication = true;
                        }, function(responseAppType) {
                        });
                        return response;
                    }, function(response) {
                        $scope.getApplicationError = true;
                        $scope.getApplicationResponse = response;
                        $scope.gotApplication = true;
                        if(response.hasOwnProperty('data') && response.data !== null) {
                            if(response.data.code === 401) {
                                service.forgetLogin();
                            }
                        }
                        return response;
                    });
                },

                getApplicationType: function($scope, name) {
                    return $http({
                        method: 'GET',
                        url: basePath + '/apptype/' + name,
                        withCredentials: true,
                        params: { 'foobar': new Date().getTime() }
                    })
                    .then(function(response) {
                        $scope.applicationType = response.data;
                        $scope.gotApplicationType = true;
                        return response;
                    }, function(response) {
                        $scope.getApplicationTypeError = true;
                        $scope.getApplicationTypeResponse = response;
                        $scope.gotApplicationType = true;
                        if(response.hasOwnProperty('data') && response.data !== null) {
                            if(response.data.code === 401) {
                                service.forgetLogin();
                            }
                        }
                        return response;
                    });
                },

                getApplicationTypes: function($scope) {
                    return $http({
                        method: 'GET',
                        url: basePath + '/apptypes',
                        withCredentials: true,
                        params: { 'foobar': new Date().getTime() }
                    })
                    .then(function(response) {
                        $scope.applicationTypes = response.data;
                        $scope.gotApplicationTypes = true;
                        return response;
                    }, function(response) {
                        $scope.getApplicationTypesError = true;
                        $scope.getApplicationTypesResponse = response;
                        $scope.gotApplicationsTypes = true;
                        if(response.hasOwnProperty('data') && response.data !== null) {
                            if(response.data.code === 401) {
                                service.forgetLogin();
                            }
                        }
                        return response;
                    });
                },

                createUser: function(formData, $scope) {
                    $http({
                        method: 'POST',
                        url: basePath + '/user/create',
                        data: $.param(formData),
                        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                        withCredentials: true,
                        params: { 'foobar': new Date().getTime() }
                    })
                    .then(function(response) {
                        $scope.response = response;
                        $scope.createdUser = formData.name;
                        $scope.creating = false;
                    }, function(response) {
                        $scope.response = response;
                        if(response.hasOwnProperty('data') && response.data !== null) {
                            if(response.data.code === 401) {
                                service.forgetLogin();
                            }
                        }
                        $scope.creating = false;
                    });
                },

                deleteUser: function(name, $scope) {
                    $http({
                        method: 'POST',
                        url: basePath + '/user/delete',
                        data: $.param({'name': name}),
                        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                        withCredentials: true,
                        params: { 'foobar': new Date().getTime() }
                    })
                    .then(function(response) {
                        $scope.response = response;
                        $scope.deletedUser = name;
                        service.getUsers($scope);
                        $scope.deleting = false;
                    }, function(response) {
                        $scope.response = response;
                        if(response.hasOwnProperty('data') && response.data !== null) {
                            if(response.data.code === 401) {
                                service.forgetLogin();
                            }
                        }
                        $scope.deleting = false;
                    });
                },

                updateUser: function(name, formData, $scope) {
                    $http({
                        method: 'POST',
                        url: basePath + '/user/' + name,
                        data: $.param(formData),
                        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                        withCredentials: true,
                        params: { 'foobar': new Date().getTime() }
                    })
                    .then(function(response) {
                        $scope.msgEvent = 'update';
                        $scope.response = response;
                        $scope.user = response.data;
                        $scope.firstName = '';
                        if($scope.user.hasOwnProperty('givenName') && $scope.user.givenName.length > 0) {
                            $scope.firstName = $scope.user.givenName[0];
                        } else if($scope.user.hasOwnProperty('givenname') && $scope.user.givenname.length > 0) {
                            $scope.firstName = $scope.user.givenname[0];
                        }
                        $scope.saving = false;
                        $scope.disableEdit();
                    }, function(response) {
                        $scope.msgEvent = 'update';
                        $scope.response = response;
                        if(response.hasOwnProperty('data') && response.data !== null) {
                            if(response.data.code === 401) {
                                service.forgetLogin();
                            }
                        }
                        $scope.saving = false;
                    });
                },

                changePassword: function(formData, $scope) {
                    $http({
                        method: 'POST',
                        url: basePath + '/user/changePassword',
                        data: $.param(formData),
                        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                        withCredentials: true,
                        params: { 'foobar': new Date().getTime() }
                    })
                    .then(function(response) {
                        $scope.response = response;
                        $scope.changing = false;
                    }, function(response) {
                        $scope.response = response;
                        if(response.hasOwnProperty('data') && response.data !== null) {
                            if(response.data.code === 401) {
                                service.forgetLogin();
                            }
                        }
                        $scope.changing = false;
                    });
                },

                createGroup: function(formData, $scope) {
                    $http({
                        method: 'POST',
                        url: basePath + '/group/create',
                        data: $.param(formData),
                        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                        withCredentials: true,
                        params: { 'foobar': new Date().getTime() }
                    })
                    .then(function(response) {
                        $scope.response = response;
                        $scope.createdGroup = formData.name;
                        $scope.creating = false;
                    }, function(response) {
                        $scope.response = response;
                        if(response.hasOwnProperty('data')  && response.data !== null) {
                            if(response.data.code === 401) {
                                service.forgetLogin();
                            }
                        }
                        $scope.creating = false;
                    });
                },

                deleteGroup: function(name, $scope) {
                    $http({
                        method: 'POST',
                        url: basePath + '/group/delete',
                        data: $.param({'name': name}),
                        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                        withCredentials: true,
                        params: { 'foobar': new Date().getTime() }
                    })
                    .then(function(response) {
                        $scope.response = response;
                        $scope.deletedGroup = name;
                        service.getGroups($scope);
                        $scope.deleting = false;
                    }, function(response) {
                        $scope.response = response;
                        if(response.hasOwnProperty('data') && response.data !== null) {
                            if(response.data.code === 401) {
                                service.forgetLogin();
                            }
                        }
                        $scope.deleting = false;
                    });
                },

                createPolicy: function(formData, $scope) {
                    for(var index = 0; index < formData.resources.length; index++) {
                        if(formData.resources[index] === null || formData.resources[index] === '') {
                            formData.resources.splice(index, 1);
                            index--;
                        }
                    }
                    $http({
                        method: 'POST',
                        url: basePath + '/policy/create',
                        data: $.param(formData),
                        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                        withCredentials: true,
                        params: { 'foobar': new Date().getTime() }
                    })
                    .then(function(response) {
                        $scope.response = response;
                        $scope.createdPolicy = formData.name;
                        $scope.creating = false;
                    }, function(response) {
                        $scope.response = response;
                        if(response.hasOwnProperty('data') && response.data !== null) {
                            if(response.data.code === 401) {
                                service.forgetLogin();
                            }
                        }
                        $scope.creating = false;
                    });
                },

                deletePolicy: function(name, $scope) {
                    return $http({
                        method: 'POST',
                        url: basePath + '/policy/delete',
                        data: $.param({'name': name}),
                        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                        withCredentials: true,
                        params: { 'foobar': new Date().getTime() }
                    })
                    .then(function(response) {
                        $scope.response = response;
                        $scope.deletedPolicy = name;
                        service.getPolicies($scope);
                        $scope.msgEvent = 'deletePolicy';
                        $scope.deleting = false;
                    }, function(response) {
                        $scope.response = response;
                        $scope.msgEvent = 'deletePolicy';
                        if(response.hasOwnProperty('data') && response.data !== null) {
                            if(response.data.code === 401) {
                                service.forgetLogin();
                            }
                        }
                        $scope.deleting = false;
                    });
                },

                updatePolicy: function(name, formData, $scope, action) {
                    $http({
                        method: 'POST',
                        url: basePath + '/policy/' + name,
                        data: $.param(formData),
                        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                        withCredentials: true,
                        params: { 'foobar': new Date().getTime() }
                    })
                    .then(function(response) {
                        var index;
                        if(action === 'addGr') {
                            $scope.msgEvent = 'addGroup';
                            for(index = 0; index < formData.groups.length; index++) {
                                if($scope.policyGroups.indexOf(formData.groups[index]) === -1) {
                                    $scope.addedGroup = formData.groups[index];
                                }
                            }
                            $scope.addingGr = false;
                        }
                        else if(action === 'rmGr') {
                            $scope.msgEvent = 'deleteGroup';
                            for(index = 0; index < $scope.policyGroups.length; index++) {
                                if(formData.groups.indexOf($scope.policyGroups[index]) === -1) {
                                    $scope.deletedGroup = $scope.policyGroups[index];
                                }
                            }
                            $scope.removingGr = false;
                        }
                        if(action === 'addRes') {
                            $scope.msgEvent = 'addResource';
                            for(index = 0; index < formData.resources.length; index++) {
                                if($scope.policy.resources.indexOf(formData.resources[index]) === -1) {
                                    $scope.addedResource = formData.resources[index];
                                }
                            }
                            $scope.addingRes = false;
                        }
                        else if(action === 'rmRes') {
                            $scope.msgEvent = 'deleteResource';
                            for(index = 0; index < $scope.policy.resources.length; index++) {
                                if(formData.resources.indexOf($scope.policy.resources[index]) === -1) {
                                    $scope.deletedResource = $scope.policy.resources[index];
                                }
                            }
                            $scope.removingRes = false;
                        }
                        else if(action === 'upd') {
                            $scope.msgEvent = 'update';
                            $scope.saving = false;
                            $scope.disableEdit();
                        }
                        else if(action === 'actionsUpd') {
                            $scope.msgEvent = 'actionsUpdate';
                            $scope.savingAct = false;
                        }
                        $scope.response = response;
                        $scope.policy = response.data;
                        $scope.policyGroups = [];
                        if($scope.policy.subject.type === 'Identity') {
                            for(index = 0; index < $scope.policy.subject.subjectValues.length; index++) {
                                $scope.policyGroups[index] = $scope.policy.subject.subjectValues[index].match(/id=(.*),ou/g)[0].replace('id=', '').replace(',ou', '');
                            }
                        }
                        service.getApplication($scope, $scope.policy.applicationName)
                        .then(function(responseApp) {
                            if($scope.policy.hasOwnProperty('actionValues')) {
                                for(var action1 in responseApp.data.actions) {
                                    if($scope.policy.actionValues.hasOwnProperty(action1)) {
                                        $scope.poldefined[action1] = true;
                                    }
                                    else {
                                        $scope.policy.actionValues[action1] = false;
                                        $scope.poldefined[action1] = false;
                                    }
                                }
                            }
                            else {
                                for(var action2 in responseApp.data.actions) {
                                    $scope.policy.actionValues[action2] = false;
                                    $scope.poldefined[action2] = false;
                                }
                            }
                            if(action === 'actionsUpd') {
                                $scope.disableActionsEdit();
                            }
                        }, function(responseApp) {
                        });
                    }, function(response) {
                        if(action === 'addGr') {
                            $scope.msgEvent = 'addGroup';
                            $scope.addingGr = false;
                        }
                        else if(action === 'rmGr') {
                            $scope.msgEvent = 'deleteGroup';
                            $scope.removingGr = false;
                        }
                        if(action === 'addRes') {
                            $scope.msgEvent = 'addResource';
                            $scope.addingRes = false;
                        }
                        else if(action === 'rmRes') {
                            $scope.msgEvent = 'deleteResource';
                            $scope.removingRes = false;
                        }
                        else if(action === 'upd') {
                            $scope.msgEvent = 'update';
                            $scope.saving = false;
                        }
                        else if(action === 'actionsUpd') {
                            $scope.msgEvent = 'actionsUpdate';
                            $scope.savingAct = false;
                        }
                        $scope.response = response;
                        if(response.hasOwnProperty('data') && response.data !== null) {
                            if(response.data.code === 401) {
                                service.forgetLogin();
                            }
                        }
                    });
                },

                createApplication: function(formData, $scope) {
                    for(var index = 0; index < formData.resources.length; index++) {
                        if(formData.resources[index] === null || formData.resources[index] === '') {
                            formData.resources.splice(index, 1);
                            index--;
                        }
                    }
                    $http({
                        method: 'POST',
                        url: basePath + '/application/create',
                        data: $.param(formData),
                        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                        withCredentials: true,
                        params: { 'foobar': new Date().getTime() }
                    })
                    .then(function(response) {
                        $scope.response = response;
                        $scope.createdApplication = formData.name;
                        $scope.creating = false;
                    }, function(response) {
                        $scope.response = response;
                        if(response.hasOwnProperty('data') && response.data !== null) {
                            if(response.data.code === 401) {
                                service.forgetLogin();
                            }
                        }
                        $scope.creating = false;
                    });
                },

                deleteApplication: function(name, $scope, $routeParams) {
                    $http({
                        method: 'POST',
                        url: basePath + '/application/delete',
                        data: $.param({'name': name}),
                        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                        withCredentials: true,
                        params: { 'foobar': new Date().getTime() }
                    })
                    .then(function(response) {
                        $scope.response = response;
                        $scope.deletedApplication = name;
                        service.getApplications($scope, $routeParams);
                        $scope.deleting = false;
                    }, function(response) {
                        $scope.response = response;
                        if(response.hasOwnProperty('data') && response.data !== null) {
                            if(response.data.code === 401) {
                                service.forgetLogin();
                            }
                        }
                        $scope.deleting = false;
                    });
                },

                updateApplication: function(name, formData, $scope, action) {
                    $http({
                        method: 'POST',
                        url: basePath + '/application/' + name,
                        data: $.param(formData),
                        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                        withCredentials: true,
                        params: { 'foobar': new Date().getTime() }
                    })
                    .then(function(response) {
                        var index;
                        if(action === 'addRes') {
                            $scope.msgEvent = 'addResource';
                            for(index = 0; index < formData.resources.length; index++) {
                                if($scope.application.resources.indexOf(formData.resources[index]) === -1) {
                                    $scope.addedResource = formData.resources[index];
                                }
                            }
                            $scope.addingRes = false;
                        }
                        else if(action === 'rmRes') {
                            $scope.msgEvent = 'deleteResource';
                            for(index = 0; index < $scope.application.resources.length; index++) {
                                if(formData.resources.indexOf($scope.application.resources[index]) === -1) {
                                    $scope.deletedResource = $scope.application.resources[index];
                                }
                            }
                            $scope.removingRes = false;
                        }
                        else if(action === 'upd') {
                            $scope.msgEvent = 'update';
                            $scope.saving = false;
                            $scope.disableEdit();
                        }
                        else if(action === 'actionsUpd') {
                            $scope.msgEvent = 'actionsUpdate';
                            $scope.savingAct = false;
                        }
                        $scope.response = response;
                        $scope.application = response.data;
                        service.getApplicationType($scope, $scope.application.applicationType)
                        .then(function(responseAppType) {
                            if($scope.application.hasOwnProperty('actions')) {
                                for(var action1 in responseAppType.data.actions) {
                                    if($scope.application.actions.hasOwnProperty(action1)) {
                                        $scope.appdefined[action1] = true;
                                    }
                                    else {
                                        $scope.application.actions[action1] = false;
                                        $scope.appdefined[action1] = false;
                                    }
                                }
                            }
                            else {
                                for(var action2 in responseAppType.data.actions) {
                                    $scope.application.actions[action2] = false;
                                    $scope.appdefined[action2] = false;
                                }
                            }
                            if(action === 'actionsUpd') {
                                $scope.disableActionsEdit();
                            }
                        }, function(responseAppType) {
                        });
                    }, function(response) {
                        if(action === 'addRes') {
                            $scope.msgEvent = 'addResource';
                            $scope.addingRes = false;
                        }
                        else if(action === 'rmRes') {
                            $scope.msgEvent = 'deleteResource';
                            $scope.removingRes = false;
                        }
                        else if(action === 'upd') {
                            $scope.msgEvent = 'update';
                            $scope.saving = false;
                        }
                        else if(action === 'actionsUpd') {
                            $scope.msgEvent = 'actionsUpdate';
                            $scope.savingAct = false;
                        }
                        $scope.response = response;
                        if(response.hasOwnProperty('data') && response.data !== null) {
                            if(response.data.code === 401) {
                                service.forgetLogin();
                            }
                        }
                    });
                },

                addUser2Group: function(group, user, $scope) {
                    $http({
                        method: 'POST',
                        url: basePath + '/group/' + group + '/addUser',
                        data: $.param({'user': user}),
                        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                        withCredentials: true,
                        params: { 'foobar': new Date().getTime() }
                    })
                    .then(function(response) {
                        $scope.msgEvent = 'add';
                        $scope.response = response;
                        $scope.addedGroup = group;
                        $scope.addedUser = user;
                        $scope.group = response.data;
                        service.getGroupUsers($scope);
                        service.getUserGroups($scope, user);
                        $scope.adding = false;
                    }, function(response) {
                        $scope.msgEvent = 'add';
                        $scope.response = response;
                        if(response.hasOwnProperty('data') && response.data !== null) {
                            if(response.data.code === 401) {
                                service.forgetLogin();
                            }
                        }
                        $scope.adding = false;
                    });
                },

                delUserFromGroup: function(group, user, $scope) {
                    $http({
                        method: 'POST',
                        url: basePath + '/group/' + group + '/delUser',
                        data: $.param({'user': user}),
                        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                        withCredentials: true,
                        params: { 'foobar': new Date().getTime() }
                    })
                    .then(function(response) {
                        $scope.msgEvent = 'delete';
                        $scope.response = response;
                        $scope.deletedGroup = group;
                        $scope.deletedUser = user;
                        $scope.group = response.data;
                        service.getGroupUsers($scope);
                        service.getUserGroups($scope, user);
                        $scope.removing = false;
                    }, function(response) {
                        $scope.msgEvent = 'delete';
                        $scope.response = response;
                        if(response.hasOwnProperty('data') && response.data !== null) {
                            if(response.data.code === 401) {
                                service.forgetLogin();
                            }
                        }
                        $scope.removing = false;
                    });
                },

                register: function(formData, $scope) {
                    $http({
                        method: 'POST',
                        url: basePath + '/user/register',
                        data: $.param(formData),
                        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                        withCredentials: true,
                        params: { 'foobar': new Date().getTime() }
                    })
                    .then(function(response) {
                        $scope.response = response;
                        $scope.userEmail = formData.mail;
                        $scope.registering = false;
                    }, function(response) {
                        $scope.response = response;
                        $scope.registering = false;
                    });
                },

                signup: function(formData, $scope) {
                    $http({
                        method: 'POST',
                        url: basePath + '/user/signup',
                        data: $.param(formData),
                        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                        withCredentials: true,
                        params: { 'foobar': new Date().getTime() }
                    })
                    .then(function(response) {
                        $scope.response = response;
                        $scope.createdUser = formData.name;
                        $scope.creating = false;
                    }, function(response) {
                        $scope.response = response;
                        $scope.creating = false;
                    });
                },

                permissions: function($scope) {
                    var formData = {};
                    formData.testCookie = true;
                    formData.foobar = new Date().getTime();
                    return $http({
                        method: 'GET',
                        url: basePath + '/permissions',
                        params: formData,
                        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                        withCredentials: true
                    })
                    .then(function(response) {
                        $scope.permissions = response.data;
                        $scope.ralen = Object.keys($scope.permissions.retrieve.allowed).length;
                        $scope.rdlen = Object.keys($scope.permissions.retrieve.denied).length;
                        $scope.salen = Object.keys($scope.permissions.store.allowed).length;
                        $scope.sdlen = Object.keys($scope.permissions.store.denied).length;
                        $scope.gotPermissions = true;
                    }, function(response) {
                    });
                }

            };

            return service;
        }
    ]);


'use strict';
angular.module('main.sensor', [
        'ngRoute'
    ])
    .config(['$routeProvider', function ($routeProvider) {

        $routeProvider.when('/sensor/list', {
            templateUrl: 'main/sensor/sensor-list.tpl.html',
            controller: 'SensorListController',
            resolve: {
                sensorList: [
                    'sensorResource',
                    function (sensorResource) {
                        return sensorResource.fetchList();
                    }
                ]
            }
        });

        $routeProvider.when('/sensor/map', {
            templateUrl: 'main/sensor/sensor-map.tpl.html',
            controller: 'SensorMapController',
            resolve: {
                sensorList: [
                    'sensorResource',
                    function (sensorResource) {
                        return sensorResource.fetchList();
                    }
                ]
            }
        });

        $routeProvider.when('/sensor/view/:uri', {
            templateUrl: 'main/sensor/sensor-view.tpl.html',
            controller: 'SensorViewController',
            resolve: {
                sensor: [
                    '$route', 'sensorResource', '$filter',
                    function ($route, sensorResource, $filter) {
                        var uri = $filter('decodeHistoryComponent')($route.current.params.uri);
                        return sensorResource.fetch(uri);
                    }
                ]
            }
        });

    }])

    .controller('SensorListController', [
        '$scope', '$compile', 'sensorResource', 'sensorList',
        function ($scope, $compile, sensorResource, sensorList) {
            $scope.sensors = sensorList;

            $scope.search = {
                label: null,
                comment: null,
                status: null,
                clear: function () {
                    $scope.search.label = null;
                    $scope.search.comment = null;
                    $scope.search.status = null;
                },
                submit: function (ngFormController) {
                    if (ngFormController.$invalid) {
                        return;
                    }
                    var query = {
                        '$and': []
                    };
                    if ($scope.search.label) {
                        query['$and'].push({
                            'http://www\\u002ew3\\u002eorg/2000/01/rdf-schema#label': {
                                $regex: $scope.search.label
                            }
                        });
                    }
                    if ($scope.search.comment) {
                        query['$and'].push({
                            'http://www\\u002ew3\\u002eorg/2000/01/rdf-schema#comment': {
                                $regex: $scope.search.comment
                            }
                        });
                    }
                    if ($scope.search.status) {
                        query['$and'].push({
                            'http://vital-iot\\u002eeu/ontology/ns/status.@id': {
                                $regex: $scope.search.status
                            }
                        });
                    }
                    if (query['$and'].length === 0) {
                        query = {};
                    }

                    $scope.sensors.length = 0;
                    return sensorResource.search(query)
                        .then(function (sensorList) {
                            angular.forEach(sensorList, function (sensor) {
                                $scope.sensors.push(sensor);
                            });
                        });
                }
            };
        }
    ])

    .directive('sensorPopup', [
        function () {
            return {
                restrict: 'E',
                replace: true,
                scope: {
                    'sensor': '='
                },
                templateUrl: 'main/sensor/sensor-popup.tpl.html',
                link: function (scope, element, attrs) {
                }
            };
        }
    ])

    .controller('SensorMapController', [
        '$scope', '$compile', '$filter', 'sensorResource', 'sensorList',
        function ($scope, $compile, $filter, sensorResource, sensorList) {

            // Functions
            function sensorToMarker(sensor) {
                var marker = {};
                var lattitude = parseFloat(sensor
                    ['http://vital-iot.eu/ontology/ns/hasLastKnownLocation']
                    ['http://www.w3.org/2003/01/geo/wgs84_pos#lat']);
                var longitude = parseFloat(
                    sensor
                        ['http://vital-iot.eu/ontology/ns/hasLastKnownLocation']
                        ['http://www.w3.org/2003/01/geo/wgs84_pos#lon'] ||
                    sensor
                        ['http://vital-iot.eu/ontology/ns/hasLastKnownLocation']
                        ['http://www.w3.org/2003/01/geo/wgs84_pos#long']);
                if (!lattitude || !longitude) {
                    return null;
                }
                var id = sensor['@id']
                    .replace('http://', '')
                    .replace(/:/g, '_')
                    .replace(/\./g, '_')
                    .replace(/\//g, '_')
                    .replace(/-/g, '0');
                marker[id] = {
                    layer: 'sensors',
                    lat: lattitude,
                    lng: longitude,
                    focus: false,
                    draggable: false,
                    riseOnHover: true,
                    icon: {
                        type: 'awesomeMarker',
                        prefix: 'fa',
                        icon: sensor['http://vital-iot.eu/ontology/ns/status']['@id'].indexOf('Running') >= 0 ? 'eye' : 'exclamation-triangle',
                        markerColor: sensor['http://vital-iot.eu/ontology/ns/status']['@id'].indexOf('Running') >= 0 ? 'green' : 'orange'
                    },
                    message: '<sensor-popup sensor="sensor"></sensor-popup>',
                    popupOptions: {
                        sensor: sensor,
                        minWidth: 320
                    }
                };
                return marker;
            }

            function readSensors(sensorList) {
                $scope.sensors.length = 0;
                $scope.mapOptions.markers = {};
                angular.forEach(sensorList, function (sensor) {
                    var marker;

                    $scope.sensors.push(sensor);
                    if (angular.isObject(sensor['http://vital-iot.eu/ontology/ns/hasLastKnownLocation'])) {
                        marker = sensorToMarker(sensor);
                        if (marker) {
                            angular.extend($scope.mapOptions.markers, marker);
                        }
                    }
                });
            }

            // Scope Data
            $scope.sensors = [];

            // Map Configuration
            $scope.mapOptions = {
                markers: {},
                center: {
                    lat: 45.4058528,
                    lng: 12.1008673,
                    zoom: 4
                },
                defaults: {
                    minZoom: 1,
                    maxZoom: 14,
                    scrollWheelZoom: true
                },
                layers: {
                    baselayers: {
                        osm: {
                            name: 'OpenStreetMap',
                            type: 'xyz',
                            url: 'http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
                            layerOptions: {
                                subdomains: ['a', 'b', 'c'],
                                attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
                                continuousWorld: true
                            }
                        }
                    },
                    overlays: {
                        sensors: {
                            name: 'Sensors',
                            type: 'markercluster',
                            visible: true,
                            'layerParams': {},
                            'layerOptions': {}
                        }
                    }
                },
                events: {
                    map: {
                        enable: ['click', 'popupopen'],
                        logic: 'emit'
                    }
                }
            };

            $scope.$on('leafletDirectiveMap.popupopen', function (event, leafletEvent) {
                var newScope = $scope.$new();
                newScope.sensor = leafletEvent.leafletEvent.popup.options.sensor;
                $compile(leafletEvent.leafletEvent.popup._contentNode)(newScope);
            });

            // Parse input
            readSensors(sensorList);
        }
    ])

    .controller('SensorViewController', [
        '$scope', '$compile', 'sensorResource', 'sensor',
        function ($scope, $compile, sensorResource, sensor) {
            $scope.sensor = sensor;
        }
    ]);

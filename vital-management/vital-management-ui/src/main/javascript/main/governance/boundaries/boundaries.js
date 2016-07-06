'use strict';
angular.module('main.governance.boundaries', [
    'ngRoute'
])


/************
 ** Config **
 ************/
    .config(['$routeProvider', function ($routeProvider) {

        $routeProvider.when('/governance/boundaries', {
            templateUrl: 'main/governance/boundaries/boundaries.tpl.html',
            controller: 'BoundariesController',
            controllerAs: 'bCtrl',
            resolve: {
                sensorList: [
                    'boundariesResource',
                    function (boundariesResource) {
                        return boundariesResource.fetchSensorList();
                    }
                ],
                observationTypeList: [
                    'boundariesResource',
                    function (boundariesResource) {
                        return boundariesResource.fetchObservationTypeList();
                    }
                ],
                boundaries: [
                    'boundariesResource',
                    function (boundariesResource) {
                        return boundariesResource.fetch();
                    }
                ]
            }
        });
    }])

    /******************
     ** Controllers ***
     ******************/

    .controller('BoundariesController', [
        '$scope', 'leafletDrawEvents', 'boundariesResource', 'sensorList', 'observationTypeList', 'boundaries',
        function ($scope, leafletDrawEvents, boundariesResource, sensorList, observationTypeList, boundaries) {
            var ctrl = this;

            // Map Options
            ctrl.boundaries = extendWithObservationTypes(boundaries, observationTypeList);

            var drawnItems = new L.geoJson();
            if (ctrl.boundaries.mapArea) {
                drawnItems.addData(ctrl.boundaries.mapArea);
            }


            var drawEvents = leafletDrawEvents.getAvailableEvents();
            ctrl.mapOptions = {
                markers: sensorsToMarkers(sensorList),
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
                draw: {
                    position: 'bottomright',
                    draw: {
                        polygon: false,
                        polyline: false,
                        circle: false,
                        marker: false
                    },
                    edit: {
                        featureGroup: drawnItems,
                        remove: true
                    }
                },
                onDraw: {
                    created: function (event, leafletEvent) {
                        drawnItems.clearLayers();
                        drawnItems.addLayer(leafletEvent.layer);
                        ctrl.boundaries.mapArea = drawnItems.toGeoJSON();
                    },
                    edited: function () {
                        ctrl.boundaries.mapArea = drawnItems.toGeoJSON();
                    },
                    deleted: function () {
                        drawnItems.clearLayers();
                        ctrl.boundaries.mapArea = drawnItems.toGeoJSON();
                    },
                    drawstart: angular.noop,
                    drawstop: angular.noop,
                    editstart: angular.noop,
                    editstop: angular.noop,
                    deletestart: angular.noop,
                    deletestop: angular.noop
                }
            };


            // Actions

            ctrl.save = function (ngFormController) {
                boundariesResource.update(ctrl.boundaries)
                    .then(function (newBoundaries) {
                        ctrl.boundaries = extendWithObservationTypes(newBoundaries, observationTypeList);
                        drawnItems.clearLayers();
                        drawnItems.addData(ctrl.boundaries.mapArea);
                    });
            };

            // Watchers, Events
            drawEvents.forEach(function (eventName) {
                $scope.$on('leafletDirectiveDraw.' + eventName, function (e, payload) {
                    var leafletEvent, leafletObject, model, modelName; //destructuring not supported by chrome yet :(
                    leafletEvent = payload.leafletEvent;
                    leafletObject = payload.leafletObject;
                    model = payload.model;
                    modelName = payload.modelName;
                    ctrl.mapOptions.onDraw[eventName.replace('draw:', '')](e, leafletEvent, leafletObject, model, modelName);
                });
            });


            // Functions
            function extendWithObservationTypes(boundaries, observationTypeList) {
                if (!boundaries.observationTypes) {
                    boundaries.observationTypes = {};
                }
                angular.forEach(observationTypeList, function (observationType) {
                    if (!_.has(boundaries.observationTypes, observationType)) {
                        boundaries.observationTypes[observationType] = true;
                    }
                });

                return boundaries;
            }

            function sensorsToMarkers(sensorList) {
                var markers = {};
                angular.forEach(sensorList, function (sensor) {
                    if (angular.isObject(sensor['http://vital-iot.eu/ontology/ns/hasLastKnownLocation'])) {
                        angular.extend(markers, sensorToMarker(sensor));
                    }
                });
                return markers;
            }

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
                    popupOptions: {
                        sensor: sensor,
                        minWidth: 320
                    }
                };
                return marker;
            }
        }
    ])

    /***************
     ** Services ***
     ***************/

    .factory('boundariesResource', [
        '$http', '$q', 'API_PATH',
        function ($http, $q, API_PATH) {

            // The public API of the service
            var service = {

                fetchSensorList: function () {
                    return $http.get(API_PATH + '/sensor')
                        .then(function (response) {
                            return response.data;
                        });
                },

                fetchObservationTypeList: function () {
                    return $http.get(API_PATH + '/observation/types')
                        .then(function (response) {
                            var sensors = [];
                            angular.forEach(response.data, function (sensor) {
                                sensors.push(sensor);
                            });
                            return sensors;
                        });
                },

                fetch: function () {
                    return $http.get(API_PATH + '/governance/boundaries')
                        .then(function (response) {
                            return response.data;
                        });
                },

                update: function (boundaries) {
                    return $http.post(API_PATH + '/governance/boundaries', boundaries)
                        .then(function (response) {
                            return response.data;
                        });

                }
            };

            return service;
        }
    ]);

// 1. System (IoT)

// POST BASE_URL/external/metadata
var system = {

    '/external/metadata': {
        action: 'POST',
        request: {
            "@context": "http://vital-iot.org/contexts/query.jsonld",
            "type": "vital:iotSystem"
        },
        response: {
            "@context": "http://vital-iot.org/contexts/system.jsonld",
            "uri": "http://www.example.com",
            "name": "Sample IoT system",
            "description": "This is a VITAL compliant IoT system.",
            "operator": "http://www.example.com",
            "serviceArea": "http://dbpedia.org/page/Camden_Town",
            "status": "vital:Running",
            "providesService": [{
                // ICO Manager
                "@context": "http://vital-iot.org/contexts/service.jsonld",
                "type": "ICOManager",
                "msm:hasOperation": [{
                    "type": "GetMetadata",
                    "hrest:hasAddress": "http://www.example.com/ico/metadata",
                    "hrest:hasMethod": "hrest:POST"
                }]
            }, {
                // Observation Manager
                "@context": "http://vital-iot.org/contexts/service.jsonld",
                "type": "ObservationManager",
                "msm:hasOperation": [{
                    "type": "GetObservations",
                    "hrest:hasAddress": "http://www.example.com/observation",
                    "hrest:hasMethod": "hrest:POST"
                }, {
                    "type": "SubscribeToObservationStream",
                    "hrest:hasAddress": "http://www.example.com/observation/stream/subscribe",
                    "hrest:hasMethod": "hrest:POST"
                }, {
                    "type": "UnsubscribeFromObservationStream",
                    "hrest:hasAddress": "http://www.example.com/observation/stream/unsubscribe",
                    "hrest:hasMethod": "hrest:POST"
                }]
            }, {
                // Service Disovery
                "@context": "http://vital-iot.org/contexts/service.jsonld",
                "type": "ServiceDiscovery",
                "description": "This is the VITAL Service Discovery module.",
                "status": "running",
                "msm:hasOperation": [{
                    "type": "ConnDMS",
                    "hrest:hasAddress": "BASE_URL/discoverer/ConnDMS",
                    "hrest:hasMethod": "hrest:GET"
                }, {
                    "type": "nICOs",
                    "hrest:hasAddress": "BASE_URL/discoverer/nICOs",
                    "hrest:hasMethod": "hrest:GET"
                }, {
                    "type": "getICOs",
                    "hrest:hasAddress": "BASE_URL/discoverer/getICOs",
                    "hrest:hasMethod": "hrest:GET",
                    "hrest:hasParameters": "double:longitude, double:latitude, double:radius, string:ObservationProperty"
                }, {
                    "type": "getICO",
                    "hrest:hasAddress": "BASE_URL/discoverer/getICO",
                    "hrest:hasMethod": "hrest:GET",
                    "hrest:hasParameters": "string:uri"
                }, {
                    "type": "getICOsMobility",
                    "hrest:hasAddress": "BASE_URL/discoverer/getICOsMobility",
                    "hrest:hasMethod": "hrest:GET",
                    "hrest:hasParameters": "string:mobilityType"
                }, {
                    "type": "getICOsConnectionStability",
                    "hrest:hasAddress": "BASE_URL/discoverer/getICOsConnectionStability",
                    "hrest:hasMethod": "hrest:GET",
                    "hrest:hasParameters": "string:stabilityType"
                }, {
                    "type": "getICOsLocalizerService",
                    "hrest:hasAddress": "BASE_URL/discoverer/getICOsLocalizerService",
                    "hrest:hasMethod": "hrest:GET"
                }]
            }, {
                // Filtering Service
                "@context": "http://vital-iot.org/contexts/service.jsonld",
                "type": "Filtering",
                "description": "This is the VITAL Filtering module.",
                "status": "running",
                "msm:hasOperation": [{
                    "type": "ConnSD",
                    "hrest:hasAddress": "BASE_URL/ConnSD",
                    "hrest:hasMethod": "hrest:GET"
                }, {
                    "type": "DataElaboration",
                    "hrest:hasAddress": "BASE_URL/DataElaboration",
                    "hrest:hasMethod": "hrest:GET",
                    "hrest:hasParameters": "double:longitude, double:latitude, double:radius, string:ObservationProperty, string: operation, double: value"
                }]
            }, {
                // Management Service
                "@context": "http://vital-iot.org/contexts/service.jsonld",
                "type": "ManagementService",
                "msm:hasOperation": [{
                    "type": "GetPerformanceMetrics",
                    "hrest:hasAddress": "http://www.example.com/performance/monitoring",
                    "hrest:hasMethod": "hrest:GET"
                }, {
                    "type": "GetConfigurationOptions",
                    "hrest:hasAddress": "http://www.example.com/performance/configuration",
                    "hrest:hasMethod": "hrest:GET"
                }, {
                    "type": "SetConfigurationOptions",
                    "hrest:hasAddress": "http://www.example.com/performance/configuration",
                    "hrest:hasMethod": "hrest:POST"
                }]
            }]
        }
    },

    '/external/lifecycle_information': {
        action: 'POST',
        request: {
            "@context": "http://vital-iot.org/contexts/query.jsonld",
            "type": "vital:iotSystem"
        },
        response: {
            "@context": "http://vital-iot.org/contexts/system.jsonld",
            "uri": "http://www.example.com",
            "status": "vital:Running"
        }
    }
};

var icoManagerService = {
    'ico/metadata': {
        action: 'POST',
        request: {
            "@context": "http://vital-iot.org/contexts/query.jsonld",
            "icos": [
                "http://www.example.com/ico/123/"
            ]
        },
        response: [{
            "@context": "http://vital-iot.org/contexts/sensor.jsonld",
            "uri": "http://www.example.com/ico/123",
            "name": "A sensor.",
            "type": "VitalSensor",
            "description": "A sensor.",
            "status": "vital:Running",
            "hasLastKnownLocation": {
                "type": "geo:Point",
                "geo:lat": 53.2719,
                "geo:long": -9.0849
            },
            "hasMovementPattern": {
                "type": "Stationary",
                "hasPredictedSpeed": {
                    "value": "3.1",
                    "qudt:unit": "qudt:KilometerPerHour"
                },
                "hasPredictedDirection": {
                    "type": "NormalVector",
                    "geo:lat": "53.2719",
                    "geo:long": "-9.0489"
                }
            },
            "hasLocalizer": {
                "type": "GpsService",
                "msm:hasOperation": {
                    "type": "GetLocation",
                    "hrest:hasMethod": "hrest:GET",
                    "hrest:hasAddress": "http://www.example.com/ico/123/location/"
                }
            },
            "hasNetworkConnection": {
                "hasStability": {
                    "type": "Continuous"
                },
                "hasNetworkSupport": {
                    "net:connectedNetworks": {
                        "type": "net:WiredNetwork"
                    }
                }
            },
            "deviceHardware": {
                "hard:status": "hard:HardwareStatus_ON",
                "hard:builtInMemory": {
                    "size": 131072
                },
                "hard:cpu": {
                    "type": "hard:CPU",
                    "maxCpuFrequency": 10
                }
            },
            "ssn:observes": [{
                "type": "http://lsm.deri.ie/OpenIot/Temperature",
                "uri": "http://www.example.com/ico/123/temperature"
            }]
        }]
    }
};

var observation = {

    '/observation': {
        action: 'POST',
        request: {
            "@context": "http://vital-iot.org/contexts/query.jsonld",
            "ico": "http://www.example.com/ico/123",
            "property": "http://lsm.deri.ie/OpenIot/Temperature",
            "from": "2014-11-17T09:00:00+02:00",
            "to": "2014-11-17T11:00:00+02:00"
        },
        response: [{
            "@context": "http://vital-iot.org/contexts/measurement.jsonld",
            "uri": "http://www.example.com/ico/123/observation/1",
            "type": "ssn:Observation",
            "ssn:observationProperty": {
                "type": "http://lsm.deri.ie/OpenIoT/Temperature"
            },
            "ssn:observationResultTime": {
                "inXSDDateTime": "2014-08-20T16:47:32+01:00"
            },
            "dul:hasLocation": {
                "type": "geo:Point",
                "geo:lat": "55.701",
                "geo:long": "12.552",
                "geo:alt": "4.33"
            },
            "ssn:observationQuality": {
                "ssn:hasMeasurementProperty": {
                    "type": "Reliability",
                    "hasValue": "HighReliability"
                }
            },
            "ssn:observationResult": {
                "type": "ssn:SensorOutput",
                "ssn:hasValue": {
                    "type": "ssn:ObservationValue",
                    "value": "21.0",
                    "qudt:unit": "qudt:DegreeCelsius"
                }
            }
        }]
    },
    '/observation/stream/subscribe': {
        action: 'POST',
        request: {
            "@context": "http://vital-iot.org/contexts/query.jsonld",
            "ico": "http://www.example.com/ico/123",
            "property": "http://lsm.deri.ie/OpenIot/Temperature",
            "url": "http://www.example.com/vital/observation/push"
        },
        response: {
            "subscriptionId": "d670460b4b4aece5915caf5c68d12f560a9fe3e4"
        }
    },
    '/observation/stream/unsubscribe': {
        action: 'POST',
        request: {
            "subscriptionId": "d670460b4b4aece5915caf5c68d12f560a9fe3e4"
        },
        response: {}
    }
};


var dataManagementService = {
    '/dms/children/g57skd/measurements': {
        action: 'POST',
        request: {
            "@context": "http://vital-iot.org/contexts/query.jsonld",
            "temperature": {
                "gt": 60.0
            },
            "time": {
                "start": "2014-11-10T00:00:00+01:00",
                "end": "2014-11-11T10:00:00+00:00"
            }
        },
        response: [{
            "url": "http://vital.example.com/ico/g57skd/measurements/0f7s3j",
            "time": "2014-11-10T06:45:03+01:00",
            "temperture": "62.5"
        }, {
            "url": "http://vital.example.com/ico/g57skd/measurements/jd92ls",
            "time": "2014-11-10T19:12:54+01:00",
            "temperture": "62.5"
        }, {
            "url": "http://vital.example.com/ico/g57skd/measurements/8sl33f",
            "time": "2014-11-10T06:45:09+01:00",
            "temperture": "61.1"
        }]
    },
    '/dms/children/h9sh33/measurements/0f7s3j': {
        action: 'GET',
        request: null,
        response: {
            "url": "http://vital.example.com/ico/d72h9d/measurements/0f7s3j",
            "time": "2014-11-10T06:45:03+01:00",
            "temperture": "62.5"
        }
    }
};

var serviceDiscovery = {
    '/discoverer/ConnDMS': {
        action: 'GET',
        request: null,
        response: {
            "@context": "http://vital-iot.org/contexts/service.jsonld",
            "type": "ServiceDiscovery/ConnDMS",
            "hrest:hasAddress": "BASE_URL/ConnDMS",
            "hrest:hasMethod": "hrest:GET",
            "hrest:status": "OFF "
        }
    },

    '/discoverer/getICOs': {
        action: 'GET',
        request: null,
        response: 12 // A number
    },

    '/discoverer/getICO': {
        action: 'GET',
        request: null,
        response: {
            "@context": "http://vital-iot.org/contexts/sensor.jsonld",
            "uri": "http://www.example.com/ico/123"
        }
    },
};

var managementService = {
    '/performance': {
        action: 'GET',
        request: null,
        response: {
            "@context": "http://vital-iot.org/contexts/sensor.jsonld",
			"uri": "http://www.example.com/ico/1232/performance/75676",
			"name": "Performance sensor",
			"type": "PerformanceSensor",
			"description": "The performance monitoring sensor.",
			"status": "vital:Running",
            "ssn:observes": [{
                "type": "<ontology definition URI>/upTime",
                "uri": "http://www.example.com/iot/{id}/perf/upTime"
            }, {
                "type": "<ontology definition URI>/systemLoad",
                "uri": "http://www.example.com/ iot/{id}/perf/systemLoad"
            }, {
                "type": "<ontology definition URI>/memUsed",
                "uri": "http://www.example.com/iot/{id}/perf/memUsed"
            }, {
                "type": "<ontology definition URI>/memAvailable",
                "uri": "http://www.example.com/iot/{id}/perf/memAvailable"
            }, {
                "type": "<ontology definition URI>/servedRequests",
                "uri": "http://www.example.com/iot/{id}/perf/servedRequests"
            }, {
                "type": "<ontology definition URI>/pendingRequests",
                "uri": "http://www.example.com/iot/{id}/perf/pendingRequests"
            }, {
                "type": "<ontology definition URI>/maxRequests",
                "uri": "http://www.example.com/iot/{id}/perf/maxRequests"
            }, {
                "type": "<ontology definition URI>/errors",
                "uri": "http://www.example.com/iot/{id}/perf/errors"
            }]
        }
    },
    '/configurationOptionsGET': {
        action: 'GET',
        request: null,
        response: [{
            "name": "c1",
            "value": "v1",
            "type": "<ontology URI>/string | number | complex",
            "permissions": "rw"
        }, {
            "name": "c2",
            "value": "v2",
            "type": "<ontology URI>/string | number | complex",
            "permissions": "r"
        }]
    },
    '/configurationOptionsPOST': {
        action: 'POST',
        request: {
            "configurationOptions": [{
                "name": "c1",
                "value": "new value v1",
            }, {
                "name": "c2",
                "value": "new value v2",
            }, ]
        },
        response: {}
    }
};

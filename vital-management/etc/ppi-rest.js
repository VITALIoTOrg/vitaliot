// 1. System (IoT)
var system = {
    // Metadata
    'PPI_BASE_URL/metadata': {
        action: 'POST',
        request: {},
        response: {
                    "@context": "http://vital-iot.eu/contexts/system.jsonld",
                    "id": "http://example.com",
                    "name": "Sample IoT system",
                    "description": "This is a VITAL compliant IoT system.",
                    "operator": "http://example.com/people#john_doe",
                    "serviceArea": "http://dbpedia.org/page/Camden_Town",
                    "sensors":
                    [
                      "http://example.com/sensor/1",
                      "http://example.com/sensor/2"
                    ],
                    "services":
                    [
                      "http://example.com/service/1",
                      "http://example.com/service/2",
                      "http://example.com/service/3"
                    ]
                  }
    },
    'PPI_BASE_URL/service/metadata': {
        action: 'POST',
        request: {},
        response: [{
            "@context": "http://vital-iot.eu/contexts/service.jsonld",
            "id": "http://example.com/service/3",
            "type": "vital:ObservationService",
            "operations": [{
                "type": "vital:GetObservations",
                "hrest:hasAddress": "http://example.com/sensor/observation",
                "hrest:hasMethod": "hrest:POST"
            }, {
                "type": "SubscribeToObservationStream",
                "hrest:hasAddress": "http://example.com/observation/stream/subscribe",
                "hrest:hasMethod": "hrest:POST"
            }, {
                "type": "UnsubscribeFromObservationStream",
                "hrest:hasAddress": "http://example.com/observation/stream/unsubscribe",
                "hrest:hasMethod": "hrest:POST"
            }]
        }, {
            "@context": "http://vital-iot.eu/contexts/service.jsonld",
            "id": "http://example.com/service/1",
            "type": "vital:ConfigurationService",
            "msm:hasOperation": [{
                "type": "vital:GetConfiguration",
                "hrest:hasAddress": "http://example.com/service/1",
                "hrest:hasMethod": "hrest:GET"
            }, {
                "type": "vital:SetConfiguration",
                "hrest:hasAddress": "http://example.com/service/1",
                "hrest:hasMethod": "hrest:POST"
            }]
        }, {
            "@context": "http://vital-iot.eu/contexts/service.jsonld",
            "id": "http://example.com/service/2",
            "type": "vital:MonitoringService",
            "msm:hasOperation": [{
                "type": "vital:GetSystemStatus",
                "hrest:hasAddress": "http://example.com/system/status",
                "hrest:hasMethod": "hrest:POST"
            }, {
                "type": "vital:GetSensorStatus",
                "hrest:hasAddress": "http://example.com/sensor/status",
                "hrest:hasMethod": "hrest:POST"
            }, {
                "type": "vital:GetSupportedPerformanceMetrics",
                "hrest:hasAddress": "http://example.com/system/performance",
                "hrest:hasMethod": "hrest:GET"
            }, {
                "type": "vital:GetPerformanceMetrics",
                "hrest:hasAddress": "http://example.com/system/performance",
                "hrest:hasMethod": "hrest:POST"
            }, {
                "type": "vital:GetSupportedSLAParameters",
                "hrest:hasAddress": "http://example.com/system/sla",
                "hrest:hasMethod": "hrest:GET"
            }, {
                "type": "vital:GetSLAParameters",
                "hrest:hasAddress": "http://example.com/system/sla",
                "hrest:hasMethod": "hrest:POST"
            }]
        }]
    },
    'PPI_BASE_URL/sensor/metadata': {
        action: 'POST',
        request: {},
        response: [{
            "@context": "http://vital-iot.eu/contexts/sensor.jsonld",
            "id": "http://example.com/sensor/1",
            "type": "vital:MonitoringSensor",
            "name": "System Monitoring Sensor",
            "description": "A virtual sensor that monitors the operational state of the system, as well as its services and sensors.",
            "status": "vital:Running",
            "ssn:observes": [{
                "type": "vital:OperationalState",
                "id": "http://example.com/sensor/1/operationalState"
            }, {
                "type": "vital:SysUptime",
                "id": "http://example.com/sensor/1/sysUptime"
            }, {
                "type": "vital:SysLoad",
                "id": "http://example.com/sensor/1/sysLoad"
            }, {
                "type": "vital:Errors",
                "id": "http://example.com/sensor/1/errors"
            }]
        }, {
            "@context": "http://vital-iot.eu/contexts/sensor.jsonld",
            "id": "http://example.com/sensor/2",
            "name": "A sensor.",
            "type": "VitalSensor",
            "description": "A sensor.",
            "hasLastKnownLocation": {
                "type": "geo:Point",
                "geo:lat": 53.2719,
                "geo:long": -9.0849
            },
            "ssn:observes": [{
                "type": "openiot:Temperature",
                "id": "http://example.com/sensor/2/temperature"
            }]
        }]
    },

    // Status
    'PPI_BASE_URL/status': {
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
        },
    'PPI_BASE_URL/sensor/status': {
                action: 'POST',
                request: {
                    "id": [
                        "http://example.com/sensor/2"
                    ]
                },
                response: [{
                    "@context": "http://vital-iot.eu/contexts/measurement.jsonld",
                    "id": "http://example.com/sensor/1/observation/2",
                    "type": "ssn:Observation",
                    "ssn:observationProperty": {
                        "type": "vital:OperationalState"
                    },
                    "ssn:observationResultTime": {
                        "inXSDDateTime": "2014-08-20T16:47:32+01:00"
                    },
                    "ssn:featureOfInterest": "http://example.com/sensor/2",
                    "ssn:observationResult": {
                        "type": "ssn:SensorOutput",
                        "ssn:hasValue": {
                            "type": "ssn:ObservationValue",
                            "value": "vital:Running"
                        }
                    }
                }]
            }
};

var observationService = {
    // Data
    'PPI_BASE_URL/sensor/observation': {
        action: 'POST',
        request: {
           "sensor": "http://example.com/sensor/2",
           "property": "http://lsm.deri.ie/OpenIot/Temperature"
           "from": "2014-11-17T09:00:00+02:00",
           "to": "2014-11-17T11:00:00+02:00"
        },
        response: [{
            "@context": "http://vital-iot.eu/contexts/measurement.jsonld",
            "id": "http://example.com/sensor/1/observation/2",
            "type": "ssn:Observation",
            "ssn:observationProperty": {
                "type": "vital:OperationalState"
            },
            "ssn:observationResultTime": {
                "inXSDDateTime": "2014-08-20T16:47:32+01:00"
            },
            "ssn:featureOfInterest": "http://example.com/sensor/2",
            "ssn:observationResult": {
                "type": "ssn:SensorOutput",
                "ssn:hasValue": {
                    "type": "ssn:ObservationValue",
                    "value": "vital:Running"
                }
            }
        }]
    }
};

// Optional
var configurationService = {
    'PPI_BASE_URL/management/configuration': [{
        action: 'GET',
        request: {},
        response: {
            "options": [{
                "name": "c1",
                "value": "1",
                "type": "http://www.w3.org/2001/XMLSchema#int",
                "permissions": "rw"
            }, {
                "name": "c2",
                "value": "v2",
                "type": " http://www.w3.org/2001/XMLSchema#string",
                "permissions": "rw"
            }]
        }
    }, {
        action: 'POST',
        request: {
            "options": [{
                "name": "c1",
                "value": "2"
            }, {
                "name": "c2",
                "value": "v3"
            }]
        },
        response: {}
    }]
};
var monitoringService = {
    'PPI_BASE_URL/management/performance/supported': {
        action: 'GET',
        request: {},
        response: {
            "metrics": [{
                "type": "http://vital-iot.eu/ontology/ns/SysUptime",
                "id": "http://example.com/sensor/1/sysUptime"
            }, {
                "type": "http://vital-iot.eu/ontology/ns/SysLoad",
                "id": "http://example.com/sensor/1/sysLoad"
            }, {
                "type": "http://vital-iot.eu/ontology/ns/Errors",
                "id": "http://example.com/sensor/1/errors"
            }]
        }
    },
    'PPI_BASE_URL/management/performance/observations': {
        action: 'GET',
        request: {
            "metric": [
                "http://vital-iot.eu/ontology/ns/SysLoad",
                "http://vital-iot.eu/ontology/ns/SysUptime"
            ]
        },
        response: [{
            "@context": "http://vital-iot.eu/contexts/measurement.jsonld",
            "id": "http://example.com/sensor/1/observation/3",
            "type": "ssn:Observation",
            "ssn:observationProperty": {
                "type": "vital:SysLoad"
            },
            "ssn:observationResultTime": {
                "inXSDDateTime": "2014-08-20T16:47:32+01:00"
            },
            "ssn:featureOfInterest": "http://example.com",
            "ssn:observationResult": {
                "type": "ssn:SensorOutput",
                "ssn:hasValue": {
                    "type": "ssn:ObservationValue",
                    "value": "80",
                    "qudt:unit": "qudt:Percent"
                }
            }
        }, {
            "@context": "http://vital-iot.eu/contexts/measurement.jsonld",
            "id": "http://example.com/sensor/1/observation/4",
            "type": "ssn:Observation",
            "ssn:observationProperty": {
                "type": "vital:SysUptime"
            },
            "ssn:observationResultTime": {
                "inXSDDateTime": "2014-08-20T16:47:32+01:00"
            },
            "ssn:featureOfInterest": "http://example.com",
            "ssn:observationResult": {
                "type": "ssn:SensorOutput",
                "ssn:hasValue": {
                    "type": "ssn:ObservationValue",
                    "value": "800000",
                    "qudt:unit": "qudt:MilliSecond"
                }
            }
        }]
    }
};

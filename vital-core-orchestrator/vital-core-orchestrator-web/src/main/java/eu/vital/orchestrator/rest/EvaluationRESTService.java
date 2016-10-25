/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.vital.orchestrator.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.util.Calendar;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/evaluation")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EvaluationRESTService extends RESTService {

    @Inject
    ObjectMapper objectMapper;

    @POST
    @Path("/latest")
    public Response executeLatestScenario(JsonNode input) throws Exception {
        String dmsUrl = "https://local.vital-iot.eu:8443/vital-core-dms";

        // 1. Get List of sensors from DMS observing AvailableBikes
        Client dmsClient = ClientBuilder.newClient();
        WebTarget dmsTarget = dmsClient.target(dmsUrl).path("querySensor").queryParam("encodeKeys", "false");
        ObjectNode sensorQuery = objectMapper.createObjectNode();
        sensorQuery.put("http://purl\\u002eoclc\\u002eorg/NET/ssnx/ssn#observes.@type", "http://vital-iot.eu/ontology/ns/Speed");
        ArrayNode sensorList = dmsTarget.request(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(sensorQuery), ArrayNode.class);

        // 2. Find the nearest sensor
        double minDistance = Double.MAX_VALUE;
        JsonNode nearestSensor = null;
        for (int i = 0; i < sensorList.size(); i++) {
            JsonNode sensor = sensorList.get(i);

            // Calculate Distance:
            double tmp = distance(
                    input.get("lat").asDouble(),
                    input.get("lng").asDouble(),
                    sensor.get("hasLastKnownLocation").get("geo:lat").asDouble(),
                    sensor.get("hasLastKnownLocation").has("geo:long") ?
                            sensor.get("hasLastKnownLocation").get("geo:long").asDouble() :
                            sensor.get("hasLastKnownLocation").get("geo:lon").asDouble()
            );
            if (tmp < minDistance) {
                minDistance = tmp;
                nearestSensor = sensor;
            }
        }

        // 3. Find the System of the Sensor
        dmsTarget = dmsClient.target(dmsUrl).path("querySystem").queryParam("encodeKeys", "false");
        ObjectNode systemQuery = objectMapper.createObjectNode();
        systemQuery.put("http://vital-iot\\u002eeu/ontology/ns/managesSensor.@id", nearestSensor.get("id").asText());
        ArrayNode systemList = dmsTarget.request(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(systemQuery), ArrayNode.class);
        JsonNode system = systemList.get(0);

        // 4. Find the Observation Service of the System
        dmsTarget = dmsClient.target(dmsUrl).path("queryService").queryParam("encodeKeys", "false");

        ObjectNode serviceAndQuery = objectMapper.createObjectNode();
        ArrayNode serviceAndParameters = objectMapper.createArrayNode();
        serviceAndQuery.put("$and", serviceAndParameters);

        ObjectNode serviceIdQuery = objectMapper.createObjectNode();
        ObjectNode serviceInQuery = objectMapper.createObjectNode();
        serviceInQuery.put("$in", system.get("services"));
        serviceIdQuery.put("@id", serviceInQuery);
        serviceAndParameters.add(serviceIdQuery);

        ObjectNode serviceTypeQuery = objectMapper.createObjectNode();
        serviceTypeQuery.put("@type", "http://vital-iot.eu/ontology/ns/ObservationService");
        serviceAndParameters.add(serviceTypeQuery);

        ArrayNode serviceList = dmsTarget.request(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(serviceAndQuery), ArrayNode.class);
        JsonNode observationService = serviceList.get(0);

        // 5. Call GetObservation operation of the service
        String operationUrl = observationService.get("operations").get("hrest:hasAddress").asText();
        Client systemClient = ClientBuilder.newClient();
        WebTarget systemTarget = systemClient.target(operationUrl);
        ObjectNode operationInput = objectMapper.createObjectNode();
        ArrayNode sensorsArray = objectMapper.createArrayNode();
        sensorsArray.add(nearestSensor.get("id").asText());
        operationInput.put("sensor", sensorsArray);
        operationInput.put("property", "http://vital-iot.eu/ontology/ns/Speed");

        ArrayNode observationList = systemTarget.request(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(operationInput), ArrayNode.class);
        JsonNode latestObservation = observationList.get(0);

        // 6. Parse Result and return response
        ObjectNode result = objectMapper.createObjectNode();
        result.put("measurementValue", latestObservation.get("ssn:observationResult").get("ssn:hasValue").get("value"));
        result.put("measurementDate", latestObservation.get("ssn:observationResultTime").get("time:inXSDDateTime"));

        return Response.ok(result).build();
    }

    @POST
    @Path("/prediction")
    public Response executePredictionScenario(JsonNode input) throws Exception {
        String dmsUrl = "https://local.vital-iot.eu:8443/vital-core-dms";

        // 1. Get List of sensors from DMS observing AvailableBikes
        Client dmsClient = ClientBuilder.newClient();
        WebTarget dmsTarget = dmsClient.target(dmsUrl).path("querySensor").queryParam("encodeKeys", "false");
        ObjectNode sensorQuery = objectMapper.createObjectNode();
        sensorQuery.put("http://purl\\u002eoclc\\u002eorg/NET/ssnx/ssn#observes.@type", "http://vital-iot.eu/ontology/ns/Speed");
        ArrayNode sensorList = dmsTarget.request(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(sensorQuery), ArrayNode.class);

        // 2. Find the nearest sensor
        double minDistance = Double.MAX_VALUE;
        JsonNode nearestSensor = null;
        for (int i = 0; i < sensorList.size(); i++) {
            JsonNode sensor = sensorList.get(i);

            // Calculate Distance:
            double tmp = distance(
                    input.get("lat").asDouble(),
                    input.get("lng").asDouble(),
                    sensor.get("hasLastKnownLocation").get("geo:lat").asDouble(),
                    sensor.get("hasLastKnownLocation").has("geo:long") ?
                            sensor.get("hasLastKnownLocation").get("geo:long").asDouble() :
                            sensor.get("hasLastKnownLocation").get("geo:lon").asDouble()
            );
            if (tmp < minDistance) {
                minDistance = tmp;
                nearestSensor = sensor;
            }
        }

        // 3. Get all observations of this sensor from DMS archive
        dmsTarget = dmsClient.target(dmsUrl).path("queryObservation").queryParam("encodeKeys", "false");
        ObjectNode observationQuery = objectMapper.createObjectNode();
        observationQuery.put("http://purl\\u002eoclc\\u002eorg/NET/ssnx/ssn#observedBy.@value", nearestSensor.get("id").asText());
        observationQuery.put("http://purl\\u002eoclc\\u002eorg/NET/ssnx/ssn#observationProperty.@type", "http://vital-iot.eu/ontology/ns/Speed");

        ArrayNode observationList = dmsTarget.request(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(observationQuery), ArrayNode.class);

        // 4. Run the prediction algorithm
        SimpleRegression regression = new SimpleRegression();
        for (int i = 0; i < observationList.size(); i++) {
            JsonNode observation = observationList.get(i);
            double value = observation.get("ssn:observationResult").get("ssn:hasValue").get("value").asDouble();
            String dateStr = observation.get("ssn:observationResultTime").get("time:inXSDDateTime").asText();
            Calendar date = javax.xml.bind.DatatypeConverter.parseDateTime(dateStr);
            regression.addData(date.getTimeInMillis(), value);
        }
        double futureMillis = javax.xml.bind.DatatypeConverter.parseDateTime(input.get("atDate").asText()).getTimeInMillis();
        double prediction = regression.predict(futureMillis);

        // 5. Return the result:
        ObjectNode result = objectMapper.createObjectNode();
        result.put("predictionValue", prediction);
        result.put("predictionDate", input.get("atDate").asText());

        return Response.ok(result).build();
    }


    private double distance(double lat1, double lon1, double lat2, double lon2) {
        int R = 6371; // Radius of the earth in km
        double dLat = deg2rad(lat2 - lat1);  // deg2rad below
        double dLon = deg2rad(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c; // Distance in km
        return d;
    }

    private double deg2rad(double deg) {
        return deg * (Math.PI / 180);
    }
}

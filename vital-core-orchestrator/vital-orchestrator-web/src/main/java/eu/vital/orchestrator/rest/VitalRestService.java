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
import eu.vital.orchestrator.service.ConfigurationDAO;
import eu.vital.orchestrator.service.MetaserviceDAO;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Path("/ppi")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class VitalRestService extends RESTService {

	@Inject
	ConfigurationDAO configurationDAO;

	@Inject
	MetaserviceDAO metaserviceDAO;

	@Inject
	ObjectMapper objectMapper;

	private DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

	@POST
	@Path("/metadata")
	public Response getSystemMetadata() throws Exception {
		JsonNode configuration = configurationDAO.get();
		// Get configuration.orchestrator.system template
		JsonNode systemJSON = configuration.get("orchestrator").get("system");
		// Replace url
		String url = configuration.get("orchestrator").get("url").asText();
		String result = systemJSON.toString().replaceAll("<orchestrator.url>", url);
		// Return result
		return Response.ok(result).build();
	}

	@POST
	@Path("/sensor/metadata")
	public Response getSensorMetadata() throws Exception {
		JsonNode configuration = configurationDAO.get();
		// Get configuration.orchestrator.sensor template
		JsonNode sensorJSON = configuration.get("orchestrator").get("sensor");
		// Replace url
		String url = configuration.get("orchestrator").get("url").asText();
		String result = sensorJSON.toString().replaceAll("<orchestrator.url>", url);
		// Return result
		return Response.ok(result).build();
	}

	@POST
	@Path("/service/metadata")
	public Response getServiceMetadata() throws Exception {
		JsonNode configuration = configurationDAO.get();

		// Get configuration.orchestrator.service template
		ArrayNode serviceArray = (ArrayNode) configuration.get("orchestrator").get("service");

		// Add to serviceArray, deployed metaservice operations
		ObjectNode metaService = objectMapper.createObjectNode();
		ArrayNode metaServiceOperations = objectMapper.createArrayNode();
		metaService.put("@context", "<orchestrator.url>/contexts/service.jsonld");
		metaService.put("id", "<orchestrator.url>/service/meta");
		metaService.put("type", "vital:OrchestratorMetaService");
		metaService.put("operations", metaServiceOperations);
		for (JsonNode metaserviceNode : metaserviceDAO.getMetaserviceList()) {
			ObjectNode metaServiceOperation = objectMapper.createObjectNode();
			metaServiceOperation.put("type", "vital:" + metaserviceNode.get("workflow").get("name").asText());
			metaServiceOperation.put("hrest:hasAddress", "<orchestrator.url>" + "/execute/" + metaserviceNode.get("id").asText());
			metaServiceOperation.put("hrest:hasMethod", "hrest:POST");

			metaServiceOperations.add(metaServiceOperation);
		}

		// Replace url
		String url = configuration.get("orchestrator").get("url").asText();
		String result = serviceArray.toString().replaceAll("<orchestrator.url>", url);
		// Return result
		return Response.ok(result).build();
	}

	@POST
	@Path("/status")
	public Response getStatus() throws Exception {
		JsonNode configuration = configurationDAO.get();

		// Get configuration.orchestrator.status template
		JsonNode status = configuration.get("orchestrator").get("status");

		// Replace url
		String url = configuration.get("orchestrator").get("url").asText();
		String result = status.toString().replaceAll("<orchestrator.url>", url);

		// Return result
		return Response.ok(result).build();
	}

	@POST
	@Path("/sensor/status")
	public Response getSensorStatus(JsonNode sensorList) throws Exception {
		JsonNode configuration = configurationDAO.get();

		// Get configuration.orchestrator.status template
		JsonNode status = configuration.get("orchestrator").get("status");
		ArrayNode statusList = objectMapper.createArrayNode();
		statusList.add(status);

		// Replace url
		String url = configuration.get("orchestrator").get("url").asText();
		String result = statusList.toString().replaceAll("<orchestrator.url>", url);

		// Return resulte
		return Response.ok(result).build();
	}

	@GET
	@Path("/performance")
	public Response getSupportedPerformanceMetrics() throws Exception {
		JsonNode configuration = configurationDAO.get();

		// Get configuration.orchestrator.performance template
		JsonNode performance = configuration.get("orchestrator").get("performance");

		// Replace url
		String url = configuration.get("orchestrator").get("url").asText();
		String result = performance.toString().replaceAll("<orchestrator.url>", url);

		// Return result
		return Response.ok(result).build();
	}

	@POST
	@Path("/performance")
	public Response getPerformanceMetric(JsonNode query) throws Exception {
		JsonNode configuration = configurationDAO.get();
		String url = configuration.get("orchestrator").get("url").asText();
		/*
		"metric": [
		 	"http://vital-iot.eu/ontology/ns/SysLoad",
		 	"http://vital-iot.eu/ontology/ns/SysUptime"
		]
		*/
		ArrayNode metrics = (ArrayNode) query.get("metric");

		ArrayNode observationsArray = objectMapper.createArrayNode();

		for (JsonNode metric : metrics) {
			String observationType = metric.asText();
			observationType = observationType.substring(observationType.lastIndexOf("/") + 1);

			// Get configuration.orchestrator.performance template
			ObjectNode observation = configuration.get("orchestrator").get("observation").deepCopy();
			//1. URI
			observation.put("uri", url + "/sensor/monitoring/observation/" + observationType + "/" + System.currentTimeMillis());
			//2. TYPE
			ObjectNode observationProperty = (ObjectNode) observation.get("ssn:observationProperty");
			observationProperty.put("type", "vital:" + Character.toLowerCase(observationType.charAt(0)) + observationType.substring(1));
			//3. Value
			ObjectNode observationValue = (ObjectNode) observation.get("ssn:observationResult").get("ssn:hasValue");
			observationValue.put("value", 10);
			//4. Date
			ObjectNode observationTime = (ObjectNode) observation.get("ssn:observationResultTime");
			observationTime.put("time:inXSDDateTime", sdf.format(new Date()));

			// Add to array
			observationsArray.add(observation);
		}
		// Replace url
		String result = observationsArray.toString().replaceAll("<orchestrator.url>", url);

		// Return result
		return Response.ok(result).build();
	}
}

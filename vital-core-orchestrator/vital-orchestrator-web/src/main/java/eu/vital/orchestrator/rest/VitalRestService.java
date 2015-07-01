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

	@POST
	@Path("/metadata")
	public Response getSystemMetadata() throws Exception {
		JsonNode configuration = configurationDAO.get();
		JsonNode systemJSON = configuration.get("orchestrator").get("system");
		String url = configuration.get("orchestrator").get("url").asText();
		String result = systemJSON.toString().replaceAll("<orchestrator.url>", url);
		return Response.ok(result).build();
	}


	@POST
	@Path("/sensor/metadata")
	public Response getSensorMetadata() throws Exception {
		JsonNode configuration = configurationDAO.get();
		JsonNode sensorJSON = configuration.get("orchestrator").get("sensor");
		String url = configuration.get("orchestrator").get("url").asText();
		String result = sensorJSON.toString().replaceAll("<orchestrator.url>", url);
		return Response.ok(result).build();
	}


	@POST
	@Path("/service/metadata")
	public Response getServiceMetadata() throws Exception {
		JsonNode configuration = configurationDAO.get();
		ArrayNode serviceArray = (ArrayNode) configuration.get("orchestrator").get("service");
		// Add to serviceArray, deployed metaservices
		/*
		{
			"@context": "<orchestrator.url>/contexts/service.jsonld",
				"id": "<orchestrator.url>/service/meta",
				"type": "vital:OrchestratorMetaService",
				"operations": [{
					"type": "vital:UndeployMetaService",
						"hrest:hasAddress": "<orchestrator.url>/metaservice/{id}",
						"hrest:hasMethod": "hrest:DELETE"
					}
				]
		}
		*/
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

		String url = configuration.get("orchestrator").get("url").asText();
		String result = serviceArray.toString().replaceAll("<orchestrator.url>", url);
		return Response.ok(result).build();
	}

	@POST
	@Path("/status")
	public Response getStatus() throws Exception {
		JsonNode configuration = configurationDAO.get();
		JsonNode status = configuration.get("orchestrator").get("status");
		String url = configuration.get("orchestrator").get("url").asText();
		String result = status.toString().replaceAll("<orchestrator.url>", url);
		return Response.ok(result).build();
	}

	@GET
	@Path("/performance")
	public Response getSupportedPerformanceMetrics() throws Exception {
		return Response.ok("[]").build();
	}
}

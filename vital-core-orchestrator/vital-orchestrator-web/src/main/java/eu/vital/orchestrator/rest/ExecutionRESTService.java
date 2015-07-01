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
import com.fasterxml.jackson.databind.node.ArrayNode;
import eu.vital.orchestrator.engine.WorkFlowEngine;
import eu.vital.orchestrator.service.MetaserviceDAO;
import eu.vital.orchestrator.service.WorkflowDAO;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("/execute")
public class ExecutionRESTService extends RESTService {

	@Inject
	WorkFlowEngine workFlowEngine;

	@Inject
	WorkflowDAO workFlowDAO;

	@Inject
	MetaserviceDAO metaserviceDAO;


	@POST
	@Path("/operation")
	public Response executeOperationOnce(JsonNode data) throws Exception {
		JsonNode input = data.get("input");
		JsonNode workflow = data.get("operation");
		JsonNode response = workFlowEngine.executeOperation(workflow, input);
		return Response.ok(response).build();
	}

	@POST
	@Path("/workflow")
	public Response executeWorkflowOnce(JsonNode data) throws Exception {
		JsonNode input = data.get("input");
		JsonNode workflow = data.get("workflow");
		JsonNode response = workFlowEngine.executeWorkflow(workflow, input);
		return Response.ok(response).build();
	}

	@PUT
	@Path("/service/{id}")
	public Response executeWorkflow(@PathParam("id") String serviceId, JsonNode input) throws Exception {
		JsonNode service = metaserviceDAO.getMetaservice(serviceId);
		JsonNode workflow = service.get("workflow");
		ArrayNode response = workFlowEngine.executeWorkflow(workflow, input);
		JsonNode lastOperation = response.get(response.size() - 1);

		//1. Check for errors:
		if (lastOperation.has("error")) {
			throw new Exception(lastOperation.get("error").asText());
		}
		//2. Return result
		return Response.ok(lastOperation.get("outputData")).build();
	}

}

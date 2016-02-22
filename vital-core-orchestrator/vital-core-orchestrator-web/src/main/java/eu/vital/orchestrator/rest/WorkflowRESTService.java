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
import eu.vital.orchestrator.service.WorkflowDAO;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/workflow")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class WorkflowRESTService extends RESTService {


	@Inject
	WorkflowDAO workflowDAO;

	/**
	 * ****************
	 * Workflows
	 * ****************
	 */

	@GET
	public Response getList() throws Exception {
		return Response.ok(workflowDAO.getWorkflowList()).build();
	}

	@GET
	@Path("/{id}")
	public Response get(@PathParam("id") String workflowId) throws Exception {
		JsonNode serviceJSON = workflowDAO.getWorkflow(workflowId);
		return Response.ok(serviceJSON).build();
	}

	@POST
	public Response create(JsonNode workflowJSON) throws Exception {
		return Response.ok(workflowDAO.createWorkflow(workflowJSON)).build();
	}

	@PUT
	@Path("/{id}")
	public Response update(@PathParam("id") String workflowId, JsonNode workflowJSON) throws Exception {
		return Response.ok(workflowDAO.update(workflowId, workflowJSON)).build();
	}

	@DELETE
	@Path("/{id}")
	public Response delete(@PathParam("id") String workflowId) throws Exception {
		workflowDAO.delete(workflowId);
		return Response.noContent().build();
	}
}

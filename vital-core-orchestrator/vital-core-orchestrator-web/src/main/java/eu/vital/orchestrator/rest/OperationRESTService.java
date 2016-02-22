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
import eu.vital.orchestrator.service.OperationDAO;

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

@Path("/operation")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class OperationRESTService extends RESTService {

	@Inject
	OperationDAO operationDAO;

	/**
	 * ****************
	 * Operations
	 * ****************
	 */

	@GET
	public Response getList() throws Exception {
		return Response.ok(operationDAO.getList()).build();
	}

	@GET
	@Path("/{id}")
	public Response get(@PathParam("id") String operationId) throws Exception {
		JsonNode serviceJSON = operationDAO.get(operationId);
		return Response.ok(serviceJSON).build();
	}

	@POST
	public Response create(JsonNode operationJSON) throws Exception {
		return Response.ok(operationDAO.create(operationJSON)).build();
	}

	@PUT
	@Path("/{id}")
	public Response update(@PathParam("id") String operationId, JsonNode operationJSON) throws Exception {
		return Response.ok(operationDAO.update(operationId, operationJSON)).build();
	}

	@DELETE
	@Path("/{id}")
	public Response delete(@PathParam("id") String operationId) throws Exception {
		operationDAO.delete(operationId);
		return Response.noContent().build();
	}
}

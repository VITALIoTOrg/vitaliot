package eu.vital.orchestrator.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Exception> {

	@Inject
	ObjectMapper objectMapper;

	public Response toResponse(Exception exception) {
		ObjectNode error = objectMapper.createObjectNode();
		error.put("error", exception.getMessage());
		return Response.status(Response.Status.CONFLICT).entity(error).build();
	}

}


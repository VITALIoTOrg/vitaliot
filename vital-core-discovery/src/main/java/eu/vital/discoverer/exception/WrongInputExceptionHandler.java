package eu.vital.discoverer.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.core.JsonParseException;

@Provider
public class WrongInputExceptionHandler implements ExceptionMapper<JsonParseException> {
	public Response toResponse(JsonParseException exception)
	    {
	        return Response.status(Status.BAD_REQUEST).build();
	    }
}

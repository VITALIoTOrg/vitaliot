package eu.vital.filtering.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

import com.fasterxml.jackson.core.JsonParseException;


public class WrongJSONFormatExceptionHandler implements ExceptionMapper<JsonParseException> {
	public Response toResponse(JsonParseException exception)
    {
        return Response.status(Status.BAD_REQUEST).build(); 
    }
}

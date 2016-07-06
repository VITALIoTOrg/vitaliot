package eu.vital.filtering.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
 
@Provider
public class WrongInputExceptionHandler implements ExceptionMapper<WrongInputException> {
	public Response toResponse(WrongInputException exception)
	    {
	        return Response.status(Status.BAD_REQUEST).build(); 
	    }
}





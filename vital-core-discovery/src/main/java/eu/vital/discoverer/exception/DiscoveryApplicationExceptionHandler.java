package eu.vital.discoverer.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class DiscoveryApplicationExceptionHandler implements ExceptionMapper<DiscoveryApplicationException> {
	public Response toResponse(DiscoveryApplicationException exception)
	    {
	        return Response.status(Status.BAD_REQUEST).build(); 
	    }
}

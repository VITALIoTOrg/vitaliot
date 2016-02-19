package eu.vital.filtering.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

public class ConnectionErrorExceptionHandler implements ExceptionMapper<ConnectionErrorException>{

	public Response toResponse(ConnectionErrorException exception) {
		
//		return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		return Response.status(Status.SERVICE_UNAVAILABLE).build();
	}

}

/**
* @Author: Riccardo Petrolo <riccardo> - Salvatore Guzzo Bonifacio <salvatore>
* @Date:   2016-03-30T17:37:24+02:00
* @Email:  riccardo.petrolo@inria.fr
* @Last modified by:   riccardo
* @Last modified time: 2016-03-30T18:27:05+02:00
*/


package eu.vital.discoverer.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

import org.jboss.resteasy.core.ExceptionHandler;

public class ConnectionErrorExceptionHandler implements ExceptionMapper<ConnectionErrorException>{

	public Response toResponse(ConnectionErrorException exception) {
//		return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		return Response.status(Status.SERVICE_UNAVAILABLE).build();
	}

}

package eu.vital.management.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import eu.vital.management.service.ServiceDAO;
import eu.vital.management.service.SystemDAO;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@Path("/service")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class ServiceRestService {

	@Inject
	SystemDAO systemDAO;

	@Inject
	ServiceDAO serviceDAO;

	@Inject
	private Logger log;

	@GET
	public Response list() throws Exception {
		ArrayNode systemList = serviceDAO.list();
		return Response.ok(systemList).build();
	}

	@POST
	@Path("/metadata")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(JsonNode query) throws Exception {
		//Query must contain a system URI
		/*
		{
            "@context": "http://vital-iot.org/contexts/query.jsonld",
            "service" : "http://www.example.com",
            "system" : "http://www.example.com"
        }
		*/
		String serviceId = query.has("service") ? query.get("service").asText() : null;
		String systemId = query.has("system") ? query.get("system").asText() : null;

		if (serviceId != null) {
			JsonNode serviceJSON = serviceDAO.get(serviceId);
			return Response.ok(serviceJSON).build();
		}
		if (systemId != null) {
			ArrayNode services = serviceDAO.searchBySystem(systemDAO.get(systemId));
			return Response.ok(services).build();
		}

		return Response.status(Response.Status.BAD_REQUEST).build();
	}

}


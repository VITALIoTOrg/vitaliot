package eu.vital.management.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import eu.vital.management.service.SystemDAO;
import eu.vital.management.util.OntologyParser;

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

@Path("/system")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class SystemRestService {

	@Inject
	SystemDAO systemDAO;

	@Inject
	private Logger log;

	@GET
	public Response list() throws Exception {
		ArrayNode systemList = systemDAO.list();
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
            "system" : "http://www.example.com"
        }
		*/
		String systemId = query.get("system").asText();
		JsonNode systemJSON = systemDAO.get(systemId);

		return Response.ok(systemJSON).build();
	}

	@POST
	@Path("/metadata/status")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getStatus(JsonNode query) throws Exception {
		//Query must contain a system URI
		/*
		{
            "@context": "http://vital-iot.org/contexts/query.jsonld",
            "system" : "http://www.example.com"
        }
		*/
		String systemId = query.get("system").asText();
		JsonNode statusJSON = systemDAO.fetchStatus(systemId);

		return Response.ok(statusJSON).build();
	}


}


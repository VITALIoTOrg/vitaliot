package eu.vital.management.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import eu.vital.management.service.SensorDAO;

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

@Path("/sensor")
@RequestScoped
public class SensorRestService {

	@Inject
	SensorDAO sensorDAO;

	@Inject
	private Logger log;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response list() throws Exception {
		ArrayNode sensorList = sensorDAO.list();

		return Response.ok(sensorList).build();
	}

	@POST
	@Path("/metadata")
	@Produces(MediaType.APPLICATION_JSON)
	public Response find(JsonNode query) throws Exception {
		/*
		{
            "@context": "http://vital-iot.org/contexts/query.jsonld",
            "system" : "http://www.example.com",
            "icos": ["http://www.example.com/ico/123"]
        }
		*/
		String sensorId = query.get("sensor").asText();
		JsonNode sensorJSON = sensorDAO.get(sensorId);
		return Response.ok(sensorJSON).build();
	}

	@POST
	@Path("/metadata/status")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getStatus(JsonNode query) throws Exception {
		//Query must contain a system URI
		/*
		{
            "@context": "http://vital-iot.org/contexts/query.jsonld",
            "sensor" : "http://www.example.com"
        }
		*/
		String sensorId = query.get("sensor").asText();
		JsonNode statusJSON = sensorDAO.fetchStatus(sensorId);

		return Response.ok(statusJSON).build();
	}

}

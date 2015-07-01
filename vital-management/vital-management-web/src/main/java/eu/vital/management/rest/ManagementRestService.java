package eu.vital.management.rest;

import com.fasterxml.jackson.databind.JsonNode;
import eu.vital.management.service.ManagementService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/management/")
@RequestScoped
public class ManagementRestService {

	@Inject
	ManagementService managementService;

	@POST
	@Path("/monitoring/supported")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSupportedPerformanceMetrics(JsonNode query) throws Exception {
		/*
		{
            "@context": "http://vital-iot.org/contexts/query.jsonld",
            "system" :"uri": "http://www.example.com"
        }
		*/
		String systemURI = query.get("system").asText();
		JsonNode metrics = managementService.getSupportedPerformanceMetrics(systemURI);

		return Response.ok(metrics).build();
	}

	@POST
	@Path("/monitoring")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPerformanceMetrics(JsonNode query) throws Exception {
		/*
		{
            "@context": "http://vital-iot.org/contexts/query.jsonld",
            "system" : "http://www.example.com",
  			"metric": [
    			"http://vital-iot.eu/ontology/ns/SysLoad",
    			"http://vital-iot.eu/ontology/ns/SysUptime"
  			]
        }
		*/
		String systemURI = query.get("system").asText();
		JsonNode metricsObservations = managementService.getPerformanceMetrics(systemURI, query);

		return Response.ok(metricsObservations).build();
	}

	@POST
	@Path("/configuration")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSystemConfiguration(JsonNode query) throws Exception {
		/*
		{
            "@context": "http://vital-iot.org/contexts/query.jsonld",
            "system" :"uri": "http://www.example.com",
        }
		*/
		String systemURI = query.get("system").asText();
		JsonNode configuration = managementService.getSystemConfiguration(systemURI);

		return Response.ok(configuration).build();
	}

	@POST
	@Path("/configuration/update")
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateSystemConfiguration(JsonNode query) throws Exception {
		/*
		{
            "@context": "http://vital-iot.org/contexts/query.jsonld",
            "system" :"uri": "http://www.example.com",
            "configuration": { parameters: [{}, {}] }
        }
		*/
		String systemURI = query.get("system").asText();
		managementService.updateSystemConfiguration(systemURI, query.get("configuration"));

		JsonNode configuration = managementService.getSystemConfiguration(systemURI);

		return Response.ok(configuration).build();
	}


}

package eu.vital.management.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.vital.management.service.GovernanceService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@Path("/governance")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class GovernanceRestService {

	@Inject
	private Logger log;

	@Inject
	private GovernanceService governanceService;

	@Inject
	private ObjectMapper objectMapper;

	@GET
	@Path("/boundaries")
	public Response getBoundaries() throws Exception {
		JsonNode boundaries = governanceService.getBoundaries();
		return Response.ok(boundaries).build();
	}

	@POST
	@Path("/boundaries")
	public Response saveBoundaries(JsonNode boundaries) throws Exception {
		JsonNode saved = governanceService.saveBoundaries(boundaries);
		return Response.ok(saved).build();
	}

	@GET
	@Path("/access/{groupName}")
	public Response getAccess(@PathParam("groupName") String groupName) throws Exception {
		JsonNode boundaries = governanceService.getAccess(groupName);
		return Response.ok(boundaries).build();
	}

	@PUT
	@Path("/access/{groupName}")
	public Response saveAccess(@PathParam("groupName") String groupName, JsonNode access) throws Exception {
		JsonNode saved = governanceService.saveAccess(groupName, access);
		return Response.ok(saved).build();
	}

	@GET
	@Path("/sla/{groupName}/{slaType}")
	public Response getSLAObservations(@PathParam("groupName") String groupName, @PathParam("slaType") String slaType) throws Exception {
		JsonNode result = governanceService.getSLAObservations(groupName, slaType);
		return Response.ok(result).build();
	}

}

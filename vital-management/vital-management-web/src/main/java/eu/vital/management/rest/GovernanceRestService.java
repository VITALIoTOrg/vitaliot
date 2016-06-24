package eu.vital.management.rest;

import com.fasterxml.jackson.databind.JsonNode;
import eu.vital.management.service.GovernanceService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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

	@GET
	@Path("/boundaries")
	public Response get() throws Exception {
		JsonNode boundaries = governanceService.getBoundaries();
		return Response.ok(boundaries).build();
	}

	@POST
	@Path("/boundaries")
	public Response save(JsonNode boundaries) throws Exception {
		JsonNode saved = governanceService.saveBoundaries(boundaries);
		return Response.ok(saved).build();
	}

}

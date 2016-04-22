package eu.vital.orchestrator.rest;

import eu.vital.orchestrator.service.AdminService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/admin")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class AdminRestService {

	@Inject
	AdminService adminService;

	@GET
	@Path("/sync")
	public Response executeSync() throws Exception {
		List<String> results = adminService.syncSystems();

		return Response.ok(results).build();
	}

}


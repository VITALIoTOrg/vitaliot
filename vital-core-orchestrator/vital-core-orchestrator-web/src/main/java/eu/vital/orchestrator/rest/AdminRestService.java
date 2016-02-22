package eu.vital.orchestrator.rest;

import eu.vital.orchestrator.job.SyncSystemsJob;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/admin")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class AdminRestService {

	@Inject
	SyncSystemsJob syncSystemsJob;

	@GET
	@Path("/sync")
	public Response executeSync() throws Exception {
		syncSystemsJob.launch();

		return Response.ok().build();
	}

}


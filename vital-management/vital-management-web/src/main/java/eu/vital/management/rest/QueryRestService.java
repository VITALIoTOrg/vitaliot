package eu.vital.management.rest;

import com.fasterxml.jackson.databind.JsonNode;

import eu.vital.management.service.QueryService;

import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/query")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class QueryRestService {

    @Inject
    QueryService queryService;

    @Inject
    private Logger log;

    @POST
    @Path("/{resourceType}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response get(@PathParam("resourceType") String resourceType,
                        @DefaultValue("true") @QueryParam("encodeKeys") boolean encodeKeys,
                        JsonNode query) throws Exception {
        JsonNode result = queryService.query(resourceType, query, encodeKeys);
        return Response.ok(result).build();
    }

}


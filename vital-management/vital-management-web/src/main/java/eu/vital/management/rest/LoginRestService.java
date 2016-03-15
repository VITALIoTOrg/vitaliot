package eu.vital.management.rest;

import com.fasterxml.jackson.databind.JsonNode;
import eu.vital.management.security.SecurityService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

@Path("/authentication/")
@RequestScoped
public class LoginRestService {

	@Inject
	SecurityService securityService;

	@GET
	@Path("/logged-on")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getLoggedOnUser(@HeaderParam("Cookie") String cookie) throws Exception {
        String authCookie = null;
        if(cookie != null)
            authCookie = cookie.replaceAll(".*" + securityService.getCookieName() + "=([^;]*).*", "$1");
		if (authCookie == null) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}

		JsonNode user = securityService.getLoggedOnUser(authCookie);
		if (user == null) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}

		return Response.ok(user).build();
	}

	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(@FormParam("username") String username, @FormParam("password") String password) throws Exception {
		String authToken = securityService.login(username, password);
		if (authToken == null) {
			return Response.status(Response.Status.FORBIDDEN).build();
		}

		JsonNode userData = securityService.getLoggedOnUser(authToken);
		Cookie authCookie = new Cookie(securityService.getCookieName(), authToken, "/", null);

		return Response.ok(userData)
				.cookie(new NewCookie(authCookie))
				.build();
	}

}

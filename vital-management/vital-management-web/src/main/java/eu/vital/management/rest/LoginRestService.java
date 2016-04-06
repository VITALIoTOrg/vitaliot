package eu.vital.management.rest;

import com.fasterxml.jackson.databind.JsonNode;
import eu.vital.management.security.SecurityService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.logging.Logger;

@Path("/authentication/")
@RequestScoped
public class LoginRestService {

	@Inject
	SecurityService securityService;

	@Inject
	Logger logger;

	private Cookie readAuthCookie(@Context HttpHeaders hh) {
		Map<String, Cookie> pathParams = hh.getCookies();
		return pathParams.get(securityService.getCookieName());
	}

	@GET
	@Path("/logged-on")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getLoggedOnUser(@Context HttpHeaders hh) throws Exception {

		// Check if cookie is valid
		Cookie authCookie = readAuthCookie(hh);
		if (authCookie == null) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}

		JsonNode user = securityService.getLoggedOnUser(authCookie.getValue());
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
		// Login
		String authToken = securityService.login(username, password);
		if (authToken == null) {
			return Response.status(Response.Status.FORBIDDEN).build();
		}

		// Get User Data
		JsonNode userData = securityService.getLoggedOnUser(authToken);

		// Return response with auth-cookie
		NewCookie authCookie = new NewCookie(securityService.getCookieName(), authToken, "/", null, "Vital Cookie", NewCookie.DEFAULT_MAX_AGE, false);
		return Response.ok(userData).cookie(authCookie).build();
	}

	@POST
	@Path("/logout")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response logout(@Context HttpHeaders hh) throws Exception {
		// Check if cookie is valid
		Cookie authCookie = readAuthCookie(hh);
		if (authCookie == null) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}

		// Notify Security Service
		securityService.logout(authCookie.getValue());

		// Remove cookie from client
		NewCookie authRemoveCookie = new NewCookie(securityService.getCookieName(), null, "/", null, "Vital Cookie Remove", 0, false);
		return Response.ok().cookie(authRemoveCookie).build();
	}

}

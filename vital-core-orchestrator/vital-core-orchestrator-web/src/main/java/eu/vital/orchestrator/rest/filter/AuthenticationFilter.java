package eu.vital.orchestrator.rest.filter;

import com.fasterxml.jackson.databind.JsonNode;
import eu.vital.orchestrator.security.SecurityService;
import eu.vital.orchestrator.security.VitalUserPrincipal;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Logger;

@Provider
@PreMatching
public class AuthenticationFilter implements ContainerRequestFilter {

	@Inject
	Logger logger;

	@Inject
	SecurityService securityService;

	@Inject
	VitalUserPrincipal userPrincipal;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		// Exclude /security
		UriInfo uriInfo = requestContext.getUriInfo();
		if (uriInfo.getPath().startsWith("/authentication")) {
			return;
		}

		// Check if cookie exists
		if (!requestContext.getCookies().containsKey(securityService.getCookieName())) {
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
			return;
		}

		// Check if cookie is valid
		Cookie authCookie = requestContext.getCookies().get(securityService.getCookieName());
		JsonNode user = securityService.getLoggedOnUser(authCookie.getValue());

		userPrincipal.setUser(user);
		userPrincipal.setToken(authCookie.getValue());

		if (user == null) {
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
			return;
		}
	}
}

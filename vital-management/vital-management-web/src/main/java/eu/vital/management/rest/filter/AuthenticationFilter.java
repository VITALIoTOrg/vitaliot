package eu.vital.management.rest.filter;

import com.fasterxml.jackson.databind.JsonNode;
import eu.vital.management.security.SecurityService;
import eu.vital.management.security.VitalUserPrincipal;

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
		if (uriInfo.getPath().startsWith("/authentication") || uriInfo.getPath().startsWith("/admin")) {
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
		if (user == null) {
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
			return;
		}

		// Authorize User for this URL:
		if (!securityService.canUserAccessResource(authCookie.getValue(), uriInfo.getAbsolutePath(), requestContext.getMethod())) {
            logger.warning("Cookie: " + authCookie.getValue());
			logger.warning("Not authorized to access (" + requestContext.getMethod() + "): " + uriInfo.getAbsolutePath());
			//requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
			//return;
		}

		// Update user principal
		userPrincipal.setUser(user);
		userPrincipal.setToken(authCookie.getValue());
	}
}

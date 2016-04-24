package eu.vital_iot.iotda.filter;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import eu.vital_iot.iotda.service.Guard;
import eu.vital_iot.iotda.util.Property;

@Provider
@PreMatching
public class SecurityFilter implements ContainerRequestFilter {

	/**
	 * The logger.
	 */
	@Inject
	private Logger logger;

	/**
	 * The security guard.
	 */
	@Inject
	private Guard guard;

	/**
	 * The name of the cookie that contains the SSO token.
	 */
	@Inject
	@Property(name = "vital-core-iot-data-adapter.sso-token")
	private String cookieName;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		if (!requestContext.getCookies().containsKey(cookieName)) {
			logger.log(Level.WARNING, "Someone tried to access me without a cookie.");
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
			return;
		}

		final Cookie cookie = requestContext.getCookies().get(cookieName);
		if (!guard.isAuthorised(cookie.getValue(), requestContext.getUriInfo().getAbsolutePath(),
				requestContext.getMethod())) {
			logger.log(Level.WARNING, "Someone tried to access me without the proper authorization.");
			requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
			return;
		}
	}
}

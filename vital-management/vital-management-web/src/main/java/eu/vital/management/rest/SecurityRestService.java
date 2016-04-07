package eu.vital.management.rest;

import eu.vital.management.rest.proxy.ProxyRestService;
import eu.vital.management.security.SecurityService;

import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import java.net.URL;

@WebServlet(urlPatterns = "/security/*", asyncSupported = true)
public class SecurityRestService extends ProxyRestService {

	@Inject
	SecurityService securityService;

	protected URL getRemoteUrl() {
		return securityService.getSecurityProxyUrl();
	}
}

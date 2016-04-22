package eu.vital.management.security;

import com.fasterxml.jackson.databind.JsonNode;
import eu.vital.management.util.VitalConfiguration;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by anglen on 29/02/16.
 */
@ApplicationScoped
public class SecurityService {

	@Inject
	private Logger log;

	@Inject
	VitalConfiguration vitalConfiguration;

	private Map<String, JsonNode> loggedOnUserMap = new HashMap<>();
	private Map<String, Date> loggedOnDateMap = new HashMap<>();
	private Map<String, Map<String, Boolean>> loggedOnAccessMap = new HashMap<>();

	public String getCookieName() {
		return vitalConfiguration.getProperty("vital-management.security.sso-token", "vitalAccessToken");
	}

	public String getTestCookieName() {
		return vitalConfiguration.getProperty("vital-management.security.alt-token", "vitalTestToken");
	}

	public URL getSecurityProxyUrl() {
		try {
			return new URL(vitalConfiguration.getProperty("vital-management.security", "https://localhost:8080/securitywrapper/rest"));
		} catch (MalformedURLException e) {
			log.warning("vital-management.security is malformed" + vitalConfiguration.getProperty("vital-management.security"));
			return null;
		}
	}

	public String getSystemAuthenticationToken() {
		// Do a login of the platform
		String user = vitalConfiguration.getProperty("vital-management.system.user", "manplatform");
		String password = vitalConfiguration.getProperty("vital-management.system.password", "password");
		return login(user, password);
	}

	public JsonNode getLoggedOnUser(String authToken) {

		// Check Cache
		if (loggedOnDateMap.containsKey(authToken)) {
			Date loggedOnDate = loggedOnDateMap.get(authToken);
			Date now = new Date();
			Long duration = now.getTime() - loggedOnDate.getTime();
			if (duration <= 30000) { // 30.000 msec
				return loggedOnUserMap.get(authToken);
			}
		}

		// Retrieve from Security server
		Client client = ClientBuilder.newClient();
		NewCookie cookie = new NewCookie(getCookieName(), authToken);
		try {
			JsonNode userData = client
					.target(getSecurityProxyUrl() + "/user")
					.request()
					.cookie(cookie)
					.accept(MediaType.APPLICATION_JSON_TYPE)
					.get(JsonNode.class);
			if (userData.has("valid") && !userData.get("valid").asBoolean()) {
				return null;
			}

			// Update cache
			loggedOnUserMap.put(authToken, userData);
			loggedOnDateMap.put(authToken, new Date());

			return userData;
		} catch (WebApplicationException e) {
			return null;
		} finally {
			client.close();
		}
	}

	public boolean canUserAccessResource(String authToken, URI requestUrl, String method) {

		// For development only
		boolean isLocalhost = requestUrl.getHost().equals("127.0.0.1") || requestUrl.getHost().equals("localhost");
		if (isLocalhost) {
			return true;
		}
		// end:For development only

		String accessKey = method + "@" + requestUrl;

		// Check Cache
		if (loggedOnDateMap.containsKey(authToken)) {
			Date loggedOnDate = loggedOnDateMap.get(authToken);
			Date now = new Date();
			Long duration = now.getTime() - loggedOnDate.getTime();
			if (duration <= 30000) { // 30.000 msec
				Map<String, Boolean> accesMap = loggedOnAccessMap.get(authToken);
				if (accesMap.containsKey(accessKey)) {
					return accesMap.get(accessKey);
				}
			}
		}
		// end: Check Cache

		// Connect to Security Server
		Client client = ClientBuilder.newClient();

		String systemAuthToken = getSystemAuthenticationToken();

		NewCookie userAuthCookie = new NewCookie(getTestCookieName(), authToken);
		NewCookie systemAuthCookie = new NewCookie(getCookieName(), systemAuthToken);

		Form form = new Form();
		form.param("resources[]", requestUrl.toString());
		form.param("testCookie", "true");

		try {
			JsonNode evaluation = client
					.target(getSecurityProxyUrl() + "/evaluate")
					.request(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
					.cookie(systemAuthCookie)
					.cookie(userAuthCookie)
					.accept(MediaType.APPLICATION_JSON_TYPE)
					.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), JsonNode.class);

			JsonNode actions = evaluation.get("responses").get(0).get("actions");
			boolean canAccess = actions.has(method) && actions.get(method).booleanValue();

			// Update cache
			Map accessMap = loggedOnAccessMap.containsKey(authToken) ? loggedOnAccessMap.get(authToken) : new HashMap<>();
			accessMap.put(accessKey, canAccess);
			loggedOnAccessMap.put(authToken, accessMap);
			// end: Update cache

			return canAccess;
		} catch (WebApplicationException e) {
			return false;
		} finally {
			client.close();
		}
	}

	public String login(String username, String password) {
		Client client = ClientBuilder.newClient();

		Form form = new Form();
		form.param("name", username);
		form.param("password", password);

		try {
			Response response = client
					.target(getSecurityProxyUrl() + "/authenticate")
					.request(MediaType.APPLICATION_JSON_TYPE)
					.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), Response.class);

			Cookie cookie = response.getCookies().get(getCookieName());
			if (cookie != null) {
				return cookie.getValue();
			}
			return null;
		} catch (WebApplicationException e) {
			return null;
		} finally {
			client.close();
		}
	}

	public void logout(String authToken) {

		// Clear cache
		loggedOnDateMap.remove(authToken);
		loggedOnUserMap.remove(authToken);
		loggedOnAccessMap.remove(authToken);

		// Notify security service
		Client client = ClientBuilder.newClient();
		try {
			client.target(getSecurityProxyUrl() + "/logout")
					.request(MediaType.APPLICATION_FORM_URLENCODED)
					.cookie(new NewCookie(getCookieName(), authToken))
					.get(JsonNode.class);

		} catch (WebApplicationException e) {
			log.log(Level.INFO, e.getMessage(), e);
		} finally {
			client.close();
		}
	}

}

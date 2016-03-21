package eu.vital.management.security;

import com.fasterxml.jackson.databind.JsonNode;
import eu.vital.management.util.VitalConfiguration;

import javax.ejb.Stateless;
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
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by anglen on 29/02/16.
 */
@Stateless
public class SecurityService {

	@Inject
	private Logger log;

	@Inject
	VitalConfiguration vitalConfiguration;

	public String getCookieName() {
		return vitalConfiguration.getProperty("vital-management.security.cookie-name", "vitalAccessToken");
	}

	public URL getSecurityProxyUrl() {
		try {
			return new URL(vitalConfiguration.getProperty("vital-management.security", "https://localhost:8080/securitywrapper/rest"));
		} catch (MalformedURLException e) {
			log.warning("vital-management.security is malformed" + vitalConfiguration.getProperty("vital-management.security"));
			return null;
		}
	}

	public JsonNode getLoggedOnUser(String authToken) {
		Client client = ClientBuilder.newClient();
		try {
			JsonNode userData = client
					.target(getSecurityProxyUrl() + "/user")
					.request(MediaType.APPLICATION_JSON_TYPE)
					.cookie(new NewCookie(getCookieName(), authToken))
					.accept(MediaType.APPLICATION_JSON_TYPE)
					.get(JsonNode.class);

			return userData;
		} catch (WebApplicationException e) {
			return null;
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
			return cookie.getValue();
		} catch (WebApplicationException e) {
			return null;
		} finally {
			client.close();
		}
	}

	public void logout(String authToken) {
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

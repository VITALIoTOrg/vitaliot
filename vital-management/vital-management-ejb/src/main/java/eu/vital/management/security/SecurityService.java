package eu.vital.management.security;

import com.fasterxml.jackson.databind.JsonNode;

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
import java.util.logging.Logger;

/**
 * Created by anglen on 29/02/16.
 */
@Stateless
public class SecurityService {

	public static final String COOKIE_NAME = "vitalAccessToken";
	public static final String AUTHENTICATION_URL = "https://vitalgateway.cloud.reply.eu/securitywrapper/rest";

	@Inject
	private Logger log;

	public JsonNode getLoggedOnUser(String authToken) {
		Client client = ClientBuilder.newClient();
		try {
			JsonNode userData = client
					.target(AUTHENTICATION_URL + "/user")
					.request(MediaType.APPLICATION_JSON_TYPE)
					.cookie(new NewCookie(COOKIE_NAME, authToken))
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
					.target(AUTHENTICATION_URL + "/authenticate")
					.request(MediaType.APPLICATION_JSON_TYPE)
					.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), Response.class);

			Cookie cookie = response.getCookies().get(COOKIE_NAME);
			return cookie.getValue();
		} catch (WebApplicationException e) {
			return null;
		} finally {
			client.close();
		}
	}

}

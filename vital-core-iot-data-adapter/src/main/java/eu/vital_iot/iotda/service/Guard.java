package eu.vital_iot.iotda.service;

import java.net.URI;
import java.util.logging.Logger;

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

import com.fasterxml.jackson.databind.JsonNode;

import eu.vital_iot.iotda.util.Property;

/**
 * The security guard.
 * 
 * @author katerina
 *
 */
@ApplicationScoped
public class Guard {

	/**
	 * The logger.
	 */
	@SuppressWarnings("unused")
	@Inject
	private Logger logger;

	/**
	 * The name of the cookie that contains the SSO token.
	 */
	@Inject
	@Property(name = "vital-core-iot-data-adapter.sso-token")
	private String cookieName;

	/**
	 * The name of the cookie that contains the alternative SSO token.
	 */
	@Inject
	@Property(name = "vital-core-iot-data-adapter.alt-token")
	private String alternativeCookieName;

	/**
	 * The system user.
	 */
	@Inject
	@Property(name = "vital-core-iot-data-adapter.system.user")
	private String systemUser;

	/**
	 * The password of the system user.
	 */
	@Inject
	@Property(name = "vital-core-iot-data-adapter.system.password")
	private String systemPassword;

	/**
	 * The base URL to Security.
	 */
	@Inject
	@Property(name = "vital-core-iot-data-adapter.security")
	private String security;

	/**
	 * Checks whether the user with the given token is authorised to perform the
	 * given method on the given URI.
	 * 
	 * @param token
	 *            the token.
	 * @param uri
	 *            the URI.
	 * @param method
	 *            the method.
	 * @return whether the user with the given token can perform the given
	 *         method on the given URI.
	 */
	public boolean isAuthorised(String token, URI uri, String method) {

		final Client client = ClientBuilder.newClient();
		final String systemToken = systemLogin();
		final NewCookie cookie = new NewCookie(cookieName, systemToken);
		final NewCookie alternativeCookie = new NewCookie(alternativeCookieName, token);
		final Form form = new Form();
		form.param("resources[]", uri.toString());
		form.param("testCookie", "true");
		try {
			final JsonNode evaluation = client.target(security + "/evaluate")
					.request(MediaType.APPLICATION_FORM_URLENCODED_TYPE).cookie(cookie).cookie(alternativeCookie)
					.accept(MediaType.APPLICATION_JSON_TYPE)
					.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), JsonNode.class);
			final JsonNode actions = evaluation.get("responses").get(0).get("actions");
			return actions.has(method) && actions.get(method).booleanValue();
		} catch (WebApplicationException wae) {
			return false;
		} finally {
			client.close();
		}
	}

	/**
	 * Performs a login with the system user.
	 * 
	 * @return the SSO token for the system user.
	 */
	public String systemLogin() {
		return login(systemUser, systemPassword);
	}

	/**
	 * Performs a login with the given username and the given password.
	 * 
	 * @param username
	 *            the username.
	 * @param password
	 *            the password.
	 * @return the SSO token for the logged in user.
	 */
	public String login(String username, String password) {
		final Client client = ClientBuilder.newClient();
		final Form form = new Form();
		form.param("name", username);
		form.param("password", password);
		try {
			final Response response = client.target(security + "/authenticate").request(MediaType.APPLICATION_JSON_TYPE)
					.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), Response.class);
			final Cookie cookie = response.getCookies().get(cookieName);
			return cookie == null ? null : cookie.getValue();
		} catch (WebApplicationException wae) {
			return null;
		} finally {
			client.close();
		}
	}

	/**
	 * Performs a logout of the user with the given token.
	 * 
	 * @param token
	 *            the token.
	 */
	public void logout(String token) {
		final Client client = ClientBuilder.newClient();
		try {
			client.target(security + "/logout").request(MediaType.APPLICATION_FORM_URLENCODED)
					.cookie(new NewCookie(cookieName, token)).get(JsonNode.class);
		} catch (WebApplicationException wae) {
		} finally {
			client.close();
		}
	}
}
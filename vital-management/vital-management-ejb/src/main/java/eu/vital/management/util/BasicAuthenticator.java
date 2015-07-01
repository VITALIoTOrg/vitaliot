package eu.vital.management.util;


import javax.inject.Inject;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;

public class BasicAuthenticator implements ClientRequestFilter {

	@Inject
	Logger logger;


	private final String user;
	private final String password;

	public BasicAuthenticator(String user, String password) {
		this.user = user;
		this.password = password;
	}

	public void filter(ClientRequestContext requestContext) throws IOException {
		MultivaluedMap<String, Object> headers = requestContext.getHeaders();
		final String basicAuthentication = getBasicAuthentication();
		headers.add("Authorization", basicAuthentication);

	}

	private String getBasicAuthentication() {
		String token = this.user + ":" + this.password;
		try {
			return "Basic " + DatatypeConverter.printBase64Binary(token.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException ex) {
			throw new IllegalStateException("Cannot encode with UTF-8", ex);
		}
	}
}

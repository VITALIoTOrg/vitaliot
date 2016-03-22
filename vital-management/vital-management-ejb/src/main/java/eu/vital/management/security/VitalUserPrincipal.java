package eu.vital.management.security;

import com.fasterxml.jackson.databind.JsonNode;

import javax.enterprise.context.RequestScoped;

/**
 * Created by anglen on 29/02/16.
 */
@RequestScoped
public class VitalUserPrincipal {

	private JsonNode user;

	private String token;

	public VitalUserPrincipal() {
	}

	public VitalUserPrincipal(JsonNode user, String token) {
		this.user = user;
		this.token = token;
	}

	public JsonNode getUser() {
		return user;
	}

	public void setUser(JsonNode user) {
		this.user = user;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}

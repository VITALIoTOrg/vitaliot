package util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

public class DMSPermission {

	final static String authURL = "https://vitalgateway.cloud.reply.eu/securitywrapper/rest/authenticate";
	final static String permissionURL = "https://vitalgateway.cloud.reply.eu/securitywrapper/rest/permissions";

	final static String user = "dmsuser";
	final static String password = "password";

	static DBObject permission;
	final public static int unsuccessful = -1;
	final public static int exception = -2;
	final public static int successfulPermission = 0;
	final public static int accessTokenNotFound = 1;

	static String DMSToken, userToken;
	final static BasicCookieStore cookieStore = new org.apache.http.impl.client.BasicCookieStore();

	static BasicClientCookie testCookie, userCookie;

	static HttpResponse<JsonNode> jsonResponse;

	static RequestConfig customizedRequestConfig;
	static HttpClientBuilder customizedClientBuilder;
	static CloseableHttpClient client;

	public static void securityDMSAuth() {

		try {
			Unirest.setHttpClient(org.apache.http.impl.client.HttpClients
					.custom().setDefaultCookieStore(cookieStore).build());

			jsonResponse = Unirest.post(authURL).field("name", user)
					.field("password", password).field("testCookie", true)
					.asJson();

			for (final Cookie cookie : cookieStore.getCookies()) {
				if (cookie.getName().equals("vitalTestToken")) {
					DMSToken = cookie.getValue();
					testCookie = new BasicClientCookie("vitalTestToken",
							DMSToken);
					testCookie.setDomain(".cloud.reply.eu");
					testCookie.setAttribute(BasicClientCookie.DOMAIN_ATTR,
							"true");

					System.out.println("DMS Authenticated: " + DMSToken);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int checkPermission(Cookie cookie) {
		if (cookie.equals("") || cookie.equals(null)) {
			permission = null;
			return accessTokenNotFound;
		} else {

			userCookie = (BasicClientCookie) cookie;
			userCookie.setDomain(".cloud.reply.eu");
			userCookie.setAttribute(BasicClientCookie.DOMAIN_ATTR, "true");
			// System.out.println("userCookie: " + userCookie.getValue());
			try {

				cookieStore.addCookie(userCookie);
				// cookieStore.addCookie(testCookie);
				/*
				 * System.out.println("Cookies..."); for (final Cookie cook :
				 * cookieStore.getCookies()) { System.out
				 * .println(cook.getName() + " : " + cook.getValue()); }
				 */
				HttpResponse<JsonNode> resp = Unirest.get(permissionURL)
						.asJson();

				DBObject objResponse = new BasicDBObject();
				objResponse = (DBObject) JSON.parse(resp.getBody().toString());

				// System.out.println("objResponse: " + objResponse);
				if (objResponse.containsField("retrieve")) {
					permission = objResponse;
					return successfulPermission;
				} else {
					permission = null;
					return unsuccessful;
				}

			} catch (UnirestException e) {
				System.out.println("Error: " + e.getMessage());
				e.printStackTrace();
				return exception;
			} catch (Exception e) {
				permission = null;
				return exception;
			}
		}
	}

	public static DBObject getPermission() {
		return permission;
	}

	public static DBObject permissionFilter(DBObject perm, DBObject query) {

		List<BasicDBObject> searchArguments = new ArrayList<BasicDBObject>();
		BasicDBList innerSearchArguments = new BasicDBList();

		List<BasicDBObject> orObject = new ArrayList<BasicDBObject>();

		BasicDBObject andObject = new BasicDBObject();
		DBObject retrieve = (DBObject) perm.get("retrieve");

		DBObject allowed = (DBObject) retrieve.get("allowed");
		DBObject denied = (DBObject) retrieve.get("denied");

		Set<String> allowedKeys = allowed.keySet();
		Set<String> deniedKeys = denied.keySet();

		orObject.add((BasicDBObject) query);

		for (String key : allowedKeys) {

			innerSearchArguments = new BasicDBList();
			searchArguments = new ArrayList<BasicDBObject>();

			searchArguments.add(new BasicDBObject(key, new BasicDBObject(
					"$exists", false)));
			// innerSearchArguments = (BasicDBList) allowed.get(key);
			innerSearchArguments.addAll((ArrayList<Object>) allowed.get(key));
			for (Object value : innerSearchArguments) {
				String valueAsString = (String) value;
				// searchArguments.add(new BasicDBObject(key, valueAsString));
				valueAsString = "^" + valueAsString + "$";
				searchArguments.add(new BasicDBObject(key, new BasicDBObject(
						"$regex", valueAsString).append("$options", "i")));
			}
			orObject.add(new BasicDBObject("$or", searchArguments));
		}

		for (String key : deniedKeys) {

			innerSearchArguments = new BasicDBList();
			searchArguments = new ArrayList<BasicDBObject>();

			// innerSearchArguments = (BasicDBList) denied.get(key);
			innerSearchArguments.addAll((ArrayList<Object>) denied.get(key));
			for (Object value : innerSearchArguments) {
				String valueAsString = (String) value;
				valueAsString = "^" + valueAsString + "$";
				// searchArguments.add(new BasicDBObject(key, valueAsString));
				searchArguments.add(new BasicDBObject(key, new BasicDBObject(
						"$regex", valueAsString).append("$options", "i")));

			}
			orObject.add(new BasicDBObject("$nor", searchArguments));
		}

		andObject.append("$and", orObject);

		return andObject;
	}

}

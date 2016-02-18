package util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import spark.Request;

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

	@SuppressWarnings({ "deprecation", "resource" })
	public static void securityDMSAuth() {
		try {
			HttpPost post = new HttpPost(authURL);
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("name", user));
			nvps.add(new BasicNameValuePair("password", password));
			nvps.add(new BasicNameValuePair("testCookie", "true"));
			post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			DefaultHttpClient httpDMSClient = new DefaultHttpClient();
			CookieStore cookieStore;
			HttpResponse response = httpDMSClient.execute(post);
			cookieStore = httpDMSClient.getCookieStore();
			List<Cookie> cookies = cookieStore.getCookies();
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("vitalTestToken")) {
					DMSToken = cookie.getValue();
					System.out.println("Authenticated. DMSToken: " + DMSToken);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static int checkPermission(Request request) {
		if (request.cookie("vitalAccessToken") == null) {
			permission = null;
			return accessTokenNotFound;
		} else {
			userToken = request.cookie("vitalAccessToken");
			try {
				DefaultHttpClient httpClient = new DefaultHttpClient();
				CookieStore cookieStore = httpClient.getCookieStore();
				BasicClientCookie dmsCookie = new BasicClientCookie(
						"vitalTestToken", DMSToken);
				dmsCookie.setDomain(".cloud.reply.eu");
				dmsCookie.setPath("/");

				BasicClientCookie userCookie = new BasicClientCookie(
						"vitalAccessToken", userToken);

				userCookie.setDomain(".cloud.reply.eu");
				userCookie.setPath("/");

				cookieStore.addCookie(dmsCookie);
				cookieStore.addCookie(userCookie);

				httpClient.setCookieStore(cookieStore);

				HttpGet get = new HttpGet(permissionURL);

				HttpResponse response = httpClient.execute(get);
				HttpEntity entity = response.getEntity();
				String responseString = EntityUtils.toString(entity, "UTF-8");
				/*
				 * List<Cookie> cookies = cookieStore.getCookies(); for (Cookie
				 * cookie : cookies) { System.out.println("Cookie: " +
				 * cookie.getName() + " : " + cookie.getValue()); }
				 */
				DBObject objResponse = new BasicDBObject();
				objResponse = (DBObject) JSON.parse(responseString);
				if (objResponse.containsField("retrieve")) {
					permission = objResponse;
					return successfulPermission;
				} else {
					permission = null;
					return unsuccessful;
				}

			} catch (Exception e) {
				permission = null;
				return exception;
			}
		}
	}

	public DBObject getPermission() {
		if (permission != null) {
			return permission;
		}
		return null;
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
			innerSearchArguments = (BasicDBList) allowed.get(key);
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

			innerSearchArguments = (BasicDBList) denied.get(key);
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

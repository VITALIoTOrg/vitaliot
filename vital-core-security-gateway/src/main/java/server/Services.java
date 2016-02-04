package server;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import clients.OpenAMClient;
import jsonpojos.PPIResponse;
import jsonpojos.PPIResponseArray;
import jsonpojos.PermissionsCollection;
import jsonpojos.RegexStringList;
import utils.JsonUtils;

@Path("")
public class Services {
	
	private OpenAMClient client;
	
	public Services() {
		client = new OpenAMClient();
	}
	
	@Path("{endpoint: .+}")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response ppiget(
			@PathParam("endpoint") String endpoint,
			@CookieParam("vitalAccessToken") String vitalToken,
			String body) {
		return forwardAndFilter("GET", endpoint, vitalToken, body);
	}
	
	@Path("{endpoint: .+}")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response ppipost(
			@PathParam("endpoint") String endpoint,
			@CookieParam("vitalAccessToken") String vitalToken,
			String body) {
		return forwardAndFilter("POST", endpoint, vitalToken, body);
	}
	
	private Response forwardAndFilter(String method, String endpoint, String vitalToken, String body) {
		Cookie ck;
		String internalToken;
		CloseableHttpClient httpclient;
		HttpRequestBase httpaction;
		PermissionsCollection perm;
		boolean wasEmpty;
		int code;

		httpclient = HttpClients.createDefault();

		URI uri = null;
		try {
			// Prepare to forward the request on the proxy
			uri = new URI("https://" + client.getProxyHost() + "/vital/" + endpoint);
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}

		if (method.equals("GET")) {
			httpaction = new HttpGet(uri);
		}
		else {
			httpaction = new HttpPost(uri);
		}

		// Get token or authenticate if null or invalid
		internalToken = client.getToken();
		ck = new Cookie("vitalAccessToken", internalToken);

		httpaction.setHeader("Cookie", ck.toString());
    	httpaction.setConfig(RequestConfig.custom().setConnectionRequestTimeout(3000).setConnectTimeout(3000).setSocketTimeout(3000).build());
    	httpaction.setHeader("Content-Type", "application/json");
		StringEntity strEntity = new StringEntity(body, StandardCharsets.UTF_8);
		if (method.equals("POST")) {
			((HttpPost) httpaction).setEntity(strEntity);
		}

		// Execute and get the response.
		CloseableHttpResponse response = null;
		try {
			response = httpclient.execute(httpaction);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			try {
				// Try again with a higher timeout
				try {
					Thread.sleep(1000); // do not retry immediately
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
		    	httpaction.setConfig(RequestConfig.custom().setConnectionRequestTimeout(7000).setConnectTimeout(7000).setSocketTimeout(7000).build());
				response = httpclient.execute(httpaction);
			} catch (ClientProtocolException ea) {
				ea.printStackTrace();
			} catch (IOException ea) {
				try {
					// Try again with a higher timeout
					try {
						Thread.sleep(1000); // do not retry immediately
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
			    	httpaction.setConfig(RequestConfig.custom().setConnectionRequestTimeout(12000).setConnectTimeout(12000).setSocketTimeout(12000).build());
					response = httpclient.execute(httpaction);
				} catch (ClientProtocolException eaa) {
					ea.printStackTrace();
				} catch (IOException eaa) {
					ea.printStackTrace();
					return Response.ok()
						.entity(eaa.getMessage())
						.build();
				}
			}
		}

		HttpEntity entity = response.getEntity();
		String respString = "";

		if (entity != null) {
			try {
				respString = EntityUtils.toString(entity);
				response.close();
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
		
		// Get user permissions from the security module
		perm = client.getPermissions(vitalToken);
		if (perm.getAdditionalProperties().containsKey("code")) {
			if (perm.getAdditionalProperties().get("code").getClass() == Integer.class) {
				code = (Integer) perm.getAdditionalProperties().get("code");
				if (code >= 400 || code < 500) {
					return Response.status(Status.BAD_REQUEST)
						.entity(perm)
						.build();
				} else if (code >= 500 || code < 600) {
					return Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(perm)
						.build();
				}
			}
		}
		
		// Convert string to object and filter by specific fields values (you may get an array or not)
		if (respString.charAt(0) == '[') {
			respString = "{ \"documents\": " + respString + " }";
			PPIResponseArray array = null;
			try {
				array = (PPIResponseArray) JsonUtils.deserializeJson(respString, PPIResponseArray.class);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (array != null) {
				wasEmpty = array.getDocuments().isEmpty();
				// Check dynamically on specified attributes
				Iterator<Map.Entry<String, RegexStringList>> it = perm.getRetrieve().getDenied().entrySet().iterator();
			    while (it.hasNext()) {
			    	Map.Entry<String, RegexStringList> pair = it.next();
				    array.getDocuments().removeIf(p -> 
						p.getProperties().containsKey(pair.getKey()) && ((RegexStringList) pair.getValue()).contains(p.getProperties().get(pair.getKey())));
			    }
			    it = perm.getRetrieve().getAllowed().entrySet().iterator();
			    while (it.hasNext()) {
			    	Map.Entry<String, RegexStringList> pair = it.next();
				    array.getDocuments().removeIf(p -> 
						p.getProperties().containsKey(pair.getKey()) && !((RegexStringList) pair.getValue()).contains(p.getProperties().get(pair.getKey())));
			    }
				try {
					if (!wasEmpty && array.getDocuments().isEmpty()) {
						return Response.status(Status.FORBIDDEN)
							.entity("{ \"code\": 403, \"reason\": \"Forbidden\", \"message\": \"Not enough permissions to access the requested data!\"}")
							.build();
					} else {
						respString = JsonUtils.serializeJson(array.getDocuments());
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else if (respString.charAt(0) == '{') {
			PPIResponse resp = null;
			try {
				resp = (PPIResponse) JsonUtils.deserializeJson(respString, PPIResponse.class);
				if (resp.getProperties().containsKey("code")) {
					if (resp.getProperties().get("code").getClass() == Integer.class) {
						code = (Integer) resp.getProperties().get("code");
						if (code >= 400 || code < 500) {
							return Response.status(Status.BAD_REQUEST)
								.entity(resp)
								.build();
						} else if (code >= 500 || code < 600) {
							return Response.status(Status.INTERNAL_SERVER_ERROR)
								.entity(resp)
								.build();
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (resp != null) {
				Iterator<Map.Entry<String, RegexStringList>> it = perm.getRetrieve().getDenied().entrySet().iterator();
			    while (it.hasNext()) {
			    	Map.Entry<String, RegexStringList> pair = it.next();
			    	System.out.println("Denied " + pair.getKey() + " " + resp.getProperties().containsKey(pair.getKey()) + " " + ((RegexStringList) pair.getValue()).contains(resp.getProperties().get(pair.getKey())));
				    if(resp.getProperties().containsKey(pair.getKey()) && ((RegexStringList) pair.getValue()).contains(resp.getProperties().get(pair.getKey())))
				    	return Response.status(Status.FORBIDDEN)
							.entity("{ \"code\": 403, \"reason\": \"Forbidden\", \"message\": \"Not enough permissions to access the requested data!\"}")
							.build();
			    }
			    it = perm.getRetrieve().getAllowed().entrySet().iterator();
			    while (it.hasNext()) {
			    	Map.Entry<String, RegexStringList> pair = it.next();
			    	System.out.println("Allowed " + pair.getKey() + " " + resp.getProperties().containsKey(pair.getKey()) + " " + ((RegexStringList) pair.getValue()).contains(resp.getProperties().get(pair.getKey())));
			        if(resp.getProperties().containsKey(pair.getKey()) && !((RegexStringList) pair.getValue()).contains(resp.getProperties().get(pair.getKey())))
				    	return Response.status(Status.FORBIDDEN)
							.entity("{ \"code\": 403, \"reason\": \"Forbidden\", \"message\": \"Not enough permissions to access the requested data!\"}")
							.build();
			    }
			}
		}

		return Response.ok()
			.entity(respString)
			.build();
	}
}

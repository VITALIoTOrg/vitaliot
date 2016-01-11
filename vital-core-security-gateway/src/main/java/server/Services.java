package server;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import clients.OpenAMClient;
import jsonpojos.AttributeValue;
import jsonpojos.PPIResponseArray;
import jsonpojos.PermissionsCollection;
import utils.JsonUtils;

@Path("/ppi")
public class Services {
	
	private OpenAMClient client;
	
	public Services() {
		client = new OpenAMClient();
	}
	
	@Path("{endpoint: .+}")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response ppiget(
			@PathParam("endpoint") String endpoint,
			@CookieParam("vitalAccessToken") String vitalToken) {

		return forwardAndFilter("GET", endpoint, vitalToken);
	}
	
	@Path("{endpoint: .+}")
	@POST
	@Produces(MediaType.TEXT_HTML)
	public Response ppipost(
			@PathParam("endpoint") String endpoint,
			@CookieParam("vitalAccessToken") String vitalToken) {

		return forwardAndFilter("POST", endpoint, vitalToken);
	}
	
	private Response forwardAndFilter(String method, String endpoint, String vitalToken) {
		Cookie ck;
		String internalToken;
		CloseableHttpClient httpclient;
		HttpRequestBase httpaction;
		PermissionsCollection perm;

		httpclient = HttpClients.createDefault();

		URI uri = null;
		try {
			// Prepare to forward the request on the proxy
			uri = new URI(client.getProxyLocation() + endpoint);
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
		
		// Convert string to object and filter by specific fields values (you may get an array or not)
		if(respString.charAt(0) == '[') {
			PPIResponseArray array = null;
			try {
				array = (PPIResponseArray) JsonUtils.deserializeJson(respString, PPIResponseArray.class);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(array != null) {
				array.getDocuments().removeIf(p -> 
					p.getId() != null && (perm.getRetrieve().getDenied().contains(new AttributeValue("id", p.getId())) ||
						perm.getRetrieve().getAllowed().contains(new AttributeValue("id", p.getId()))) ||
					p.getType() != null && (perm.getRetrieve().getDenied().contains(new AttributeValue("type", p.getType())) ||
						!perm.getRetrieve().getAllowed().contains(new AttributeValue("type", p.getType())))
				);
				try {
					respString = JsonUtils.serializeJson(array);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return Response.ok()
				.entity(respString)
				.build();
	}
		
}

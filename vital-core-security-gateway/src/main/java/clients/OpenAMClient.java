package clients;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Cookie;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import jsonpojos.AuthenticationResponse;
import jsonpojos.PermissionsCollection;
import jsonpojos.Validation;
import utils.ConfigReader;
import utils.JsonUtils;
import utils.HttpCommonClient;

public class OpenAMClient {

	private HttpCommonClient httpclient;
	private ConfigReader configReader;
	
	private String proxyhost;
	private String securityhost;
	private String username;
	private String password;
	private static String token = null;
	
	public OpenAMClient() {
		httpclient = HttpCommonClient.getInstance();
		configReader = ConfigReader.getInstance();
		
		proxyhost = configReader.get(ConfigReader.PROXY_HOST);
		securityhost = configReader.get(ConfigReader.SECURITY_HOST);
		username = configReader.get(ConfigReader.USERNAME);
		password = configReader.get(ConfigReader.PASSWORD);
	}
	
	public String getProxyHost() {
		return proxyhost;
	}
	
	public String getToken() {
		if (token == null) {
			token = (String) authenticate(username, password).getAdditionalProperties().get((Object) "token");
		} else if (getUserIdFromToken(token).getAdditionalProperties().containsKey("code")) {
			if ((int)getUserIdFromToken(token).getAdditionalProperties().get("code") == 400)
				token = (String) authenticate(username, password).getAdditionalProperties().get((Object) "token");
		} else if (!getUserIdFromToken(token).getValid()) {
			token = (String) authenticate(username, password).getAdditionalProperties().get((Object) "token");
		}
		return token;
	}
	
    private String performRequest(HttpRequestBase request, StringBuilder token) {
    	String response = "";
    	HttpEntity httpent;

    	request.setConfig(RequestConfig.custom().setConnectionRequestTimeout(3000).setConnectTimeout(3000).setSocketTimeout(3000).build());

    	CloseableHttpResponse resp;
		try {
			resp = httpclient.httpc.execute(request);
			httpent = resp.getEntity();
			if(httpent != null) {
				response = EntityUtils.toString(httpent);
			}
			if(token != null && resp.containsHeader("Set-Cookie")) {
				String header = resp.getHeaders("Set-Cookie")[0].getValue();
				token.append(header.substring(header.indexOf('=') + 1, header.indexOf(';')));
			}
            resp.close();
		} catch (Exception e) {
			try {
				// Try again with a higher timeout
				try {
					Thread.sleep(1000); // do not retry immediately
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
		    	request.setConfig(RequestConfig.custom().setConnectionRequestTimeout(7000).setConnectTimeout(7000).setSocketTimeout(7000).build());
		    	resp = httpclient.httpc.execute(request);
				httpent = resp.getEntity();
				if(httpent != null) {
					response = EntityUtils.toString(httpent);
				}
				if(token != null && resp.containsHeader("Set-Cookie")) {
					String header = resp.getHeaders("Set-Cookie").toString();
					token.append(header.substring(header.indexOf('=') + 1, header.indexOf(';')));
				}
	            resp.close();
			} catch (Exception ea) {
				// Try again with an even higher timeout
				try {
					Thread.sleep(1000); // do not retry immediately
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
		    	request.setConfig(RequestConfig.custom().setConnectionRequestTimeout(12000).setConnectTimeout(12000).setSocketTimeout(12000).build());
		    	try {
					resp = httpclient.httpc.execute(request);
					httpent = resp.getEntity();
					if(httpent != null) {
						response = EntityUtils.toString(httpent);
					}
					if(token != null && resp.containsHeader("Set-Cookie")) {
						String header = resp.getHeaders("Set-Cookie").toString();
						token.append(header.substring(header.indexOf('=') + 1, header.indexOf(';')));
					}
		            resp.close();
				} catch (ClientProtocolException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}

    	return response;
    }
	
	public AuthenticationResponse authenticate(String name, String password) {
		StringBuilder token;
		
		URI uri = null;
		try {
			uri = new URIBuilder()
			.setScheme("https")
			.setPort(443)
			.setHost(securityhost)
			.setPath("/securitywrapper/rest/authenticate")
			.build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		HttpPost httppost = new HttpPost(uri);
		httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);  
		
		nameValuePairs.add(new BasicNameValuePair("name", name));  
		nameValuePairs.add(new BasicNameValuePair("password", password));
		nameValuePairs.add(new BasicNameValuePair("testCookie", "false"));
		
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		token = new StringBuilder();
		String respString = performRequest(httppost, token);
		
		AuthenticationResponse auth = new AuthenticationResponse();
		try {
			auth = (AuthenticationResponse) JsonUtils.deserializeJson(respString, AuthenticationResponse.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		auth.setAdditionalProperty("token", token.toString());
		
		return auth;
	}
	
	public Validation getUserIdFromToken(String usertoken) {
		Cookie ck;
		
		ck = new Cookie("vitalAccessToken", usertoken);
		
		URI uri = null;
		try {
			uri = new URIBuilder()
			.setScheme("https")
			.setPort(443)
			.setHost(securityhost)
			.setPath("/securitywrapper/rest/user")
			.setCustomQuery("testCookie=false")
			.build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		HttpGet httpget = new HttpGet(uri);
		httpget.setHeader("Cookie", ck.toString());

		String respString = performRequest(httpget, null);
		
		Validation validation = new Validation();
		
		try {
			validation = (Validation) JsonUtils.deserializeJson(respString, Validation.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		return validation;
	}

	public PermissionsCollection getPermissions(String userToken) {
		Cookie ck, ckuser;

		String token = getToken();
		ck = new Cookie("vitalAccessToken", token);
		ckuser = new Cookie("vitalTestToken", userToken);

		URI uri = null;
		try {
			uri = new URIBuilder()
			.setScheme("https")
			.setPort(443)
			.setHost(securityhost)
			.setPath("/securitywrapper/rest/permissions")
			.setCustomQuery("testCookie=true")
			.build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}

		HttpGet httpget = new HttpGet(uri);
		httpget.setHeader("Cookie", ck.toString() + "; " + ckuser.toString());
		
		System.out.println(ck.toString() + "; " + ckuser.toString());

		String respString = performRequest(httpget, null);
		
		System.out.println(respString);
		
		PermissionsCollection perm = new PermissionsCollection();
		
		try {
			perm = (PermissionsCollection) JsonUtils.deserializeJson(respString, PermissionsCollection.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		return perm;
	}
}

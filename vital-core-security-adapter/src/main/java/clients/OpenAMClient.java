package clients;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import jsonpojos.ActionValues__;
import jsonpojos.ActionValues___;
import jsonpojos.Application;
import jsonpojos.Applications;
import jsonpojos.Authenticate;
import jsonpojos.ChangePasswordRequest;
import jsonpojos.ChangePasswordResponse;
import jsonpojos.DecisionRequest;
import jsonpojos.Group;
import jsonpojos.GroupModel;
import jsonpojos.GroupModelWithUsers;
import jsonpojos.Groups;
import jsonpojos.LogoutResponse;
import jsonpojos.Monitor;
import jsonpojos.Policies;
import jsonpojos.Policy;
import jsonpojos.PolicyAuthenticatedModel;
import jsonpojos.PolicyIdentityModel;
import jsonpojos.Result;
import jsonpojos.SubjectAuthenticated;
import jsonpojos.Subject__;
import jsonpojos.Subject___;
import jsonpojos.User;
import jsonpojos.UserModel;
import jsonpojos.Users;
import jsonpojos.Validation;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import utils.Action;
import utils.ConfigReader;
import utils.JsonUtils;
import utils.SessionUtils;

public class OpenAMClient {

	private HttpClient httpclient;
	private ConfigReader configReader;
	
	private String idpHost;
	private int idpPort;
	private String userAdmin;
	private String pwdAdmin;
	private String authToken;
	private String manToken;
	
	public OpenAMClient() {
		httpclient = HttpClients.createDefault();
		configReader = ConfigReader.getInstance();
		
		idpHost = configReader.get(ConfigReader.IDP_HOST);
		idpPort = Integer.parseInt(configReader.get(ConfigReader.IDP_PORT));
		authToken = configReader.get(ConfigReader.AUTH_TOKEN);
		manToken = configReader.get(ConfigReader.MAN_TOKEN);
		
	}
	
	public String getSSOcookieName() {
		return authToken;
	}
	
	public String getManTokenCookieName() {
		return manToken;
	}
	
	private boolean isTokenValid(String token) {
		
		URI uri = null;
		try {
			uri = new URIBuilder()
			.setScheme("http")
			.setHost(idpHost)
			.setPort(idpPort)
			.setPath(" /idp/json/sessions/"+token)
			.setCustomQuery("_action=validate")
			.build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		HttpPost httppost = new HttpPost(uri);
		httppost.setHeader("Content-Type", "application/json");
		Builder requestConfigBuilder = RequestConfig.custom();
    	requestConfigBuilder.setConnectionRequestTimeout(4000).setConnectTimeout(4000).setSocketTimeout(4000);
    	httppost.setConfig(requestConfigBuilder.build());
		
		// Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			try {
				response = httpclient.execute(httppost);
			} catch (ClientProtocolException ea) {
				ea.printStackTrace();
			} catch (IOException ea) {
				ea.printStackTrace();
			}
		}
		HttpEntity entity = response.getEntity();

		String respString = "";
		
		if (entity != null) {
		    
			try {
				respString = EntityUtils.toString(entity);
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}    
		}
		
		Validation validation = new Validation();
		
		try {
			validation = (Validation) JsonUtils.deserializeJson(respString, Validation.class);
			if (validation.getValid()) {
				return true;
			}
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public LogoutResponse logout(String token) {
		
		LogoutResponse resp = new LogoutResponse();
		
		if(!isTokenValid(token)) {
			try {
				resp = (LogoutResponse) JsonUtils.deserializeJson("{\"result\":\"Successfully logged out\"}", LogoutResponse.class);
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return resp;
		}
		
		URI uri = null;
		try {
			uri = new URIBuilder()
			.setScheme("http")
			.setHost(idpHost)
			.setPort(idpPort)
			.setPath("/idp/json/sessions")
			.setCustomQuery("_action=logout")
			.build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		HttpPost httppost = new HttpPost(uri);
		httppost.setHeader("Content-Type", "application/json");
		httppost.setHeader(authToken, token);
		
		StringEntity strEntity = new StringEntity("{}", StandardCharsets.UTF_8);
		httppost.setEntity(strEntity);
		Builder requestConfigBuilder = RequestConfig.custom();
    	requestConfigBuilder.setConnectionRequestTimeout(4000).setConnectTimeout(4000).setSocketTimeout(4000);
    	httppost.setConfig(requestConfigBuilder.build());

		// Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			try {
				response = httpclient.execute(httppost);
			} catch (ClientProtocolException ea) {
				ea.printStackTrace();
			} catch (IOException ea) {
				ea.printStackTrace();
			}
		}
		HttpEntity entity = response.getEntity();

		String respString = "";
		
		if (entity != null) {
		    
			try {
				respString = EntityUtils.toString(entity);
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		    
		}

		try {
			resp = (LogoutResponse) JsonUtils.deserializeJson(respString, LogoutResponse.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return resp;
		
	}
	
	public Authenticate authenticate(String name, String password) {
		
		URI uri = null;
		try {
			uri = new URIBuilder()
			.setScheme("http")
			.setHost(idpHost)
			.setPort(idpPort)
			.setPath("/idp/json/authenticate")
			.build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		HttpPost httppost = new HttpPost(uri);
		httppost.setHeader("Content-Type", "application/json");
		if(name != null) {
			httppost.setHeader("X-OpenAM-Username", name);
			httppost.setHeader("X-OpenAM-Password", password);
		} else {
			httppost.setHeader("X-OpenAM-Username", userAdmin);
			httppost.setHeader("X-OpenAM-Password", pwdAdmin);
		}
		Builder requestConfigBuilder = RequestConfig.custom();
    	requestConfigBuilder.setConnectionRequestTimeout(4000).setConnectTimeout(4000).setSocketTimeout(4000);
    	httppost.setConfig(requestConfigBuilder.build());

		// Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			try {
				response = httpclient.execute(httppost);
			} catch (ClientProtocolException ea) {
				ea.printStackTrace();
			} catch (IOException ea) {
				ea.printStackTrace();
			}
		}
		HttpEntity entity = response.getEntity();

		String respString = "";
		
		if (entity != null) {
		    
			try {
				respString = EntityUtils.toString(entity);
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		    
		}
		
		Authenticate auth = new Authenticate();
		try {
			auth = (Authenticate) JsonUtils.deserializeJson(respString, Authenticate.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(name == null) {
			SessionUtils.setAdminAuthToken(auth.getTokenId());
		}
		
		return auth;
		
	}
	
	public ChangePasswordResponse changePassword(String token, String userPass, String currPass) {
		
		/*boolean currentSessionIsValid = isTokenValid();
		
		if (!currentSessionIsValid) {
			authenticate(null, null);
		}
		
		String adminAuthToken = SessionUtils.getAdminAuhtToken();*/
		
		ChangePasswordRequest req = new ChangePasswordRequest();
		
		req.setCurrentpassword(currPass);
		req.setUserpassword(userPass);
		
		String newReq = "";
		
		try {
			newReq = JsonUtils.serializeJson(req);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		URI uri = null;
		try {
			uri = new URIBuilder()
			.setScheme("http")
			.setHost(idpHost)
			.setPort(idpPort)
			.setPath(" /idp/json/users/"+getUserIdFromToken(token).getUid())
			.setCustomQuery("_action=changePassword")
			.build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		StringEntity strEntity = new StringEntity(newReq, StandardCharsets.UTF_8);
		strEntity.setContentType("application/json");
		
		HttpPost httppost = new HttpPost(uri);
		httppost.setHeader("Content-Type", "application/json");
		httppost.setHeader(authToken, token);
		httppost.setEntity(strEntity);
		Builder requestConfigBuilder = RequestConfig.custom();
    	requestConfigBuilder.setConnectionRequestTimeout(4000).setConnectTimeout(4000).setSocketTimeout(4000);
    	httppost.setConfig(requestConfigBuilder.build());
		
		// Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			try {
				response = httpclient.execute(httppost);
			} catch (ClientProtocolException ea) {
				ea.printStackTrace();
			} catch (IOException ea) {
				ea.printStackTrace();
			}
		}
		HttpEntity entity = response.getEntity();

		String respString = "";
		
		if (entity != null) {
		    
			try {
				respString = EntityUtils.toString(entity);
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}    
		}

		ChangePasswordResponse resp = new ChangePasswordResponse();
		
		try {
			resp = (ChangePasswordResponse) JsonUtils.deserializeJson(respString, ChangePasswordResponse.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		return resp;
		
	}
	
	public boolean evaluate(String token, ArrayList<String> resources, StringBuilder goingOn, String tokenUser) {
		
		/*boolean currentSessionIsValid = isTokenValid();
		
		if (!currentSessionIsValid) {
			authenticate(null, null);
		}
		
		String adminAuthToken = SessionUtils.getAdminAuhtToken();*/
		
		DecisionRequest req = new DecisionRequest();
		SubjectAuthenticated sub = new SubjectAuthenticated();
		sub.setSsoToken(token);
		
		req.setSubject(sub);
		req.setResources(resources);
		
		String newReq = "";
		
		try {
			newReq = JsonUtils.serializeJson(req);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		URI uri = null;
		try {
			uri = new URIBuilder()
			.setScheme("http")
			.setHost(idpHost)
			.setPort(idpPort)
			.setPath(" /idp/json/policies/")
			.setCustomQuery("_action=evaluate")
			.build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		StringEntity strEntity = new StringEntity(newReq, StandardCharsets.UTF_8);
		strEntity.setContentType("application/json");
		
		HttpPost httppost = new HttpPost(uri);
		httppost.setHeader("Content-Type", "application/json");
		httppost.setHeader(authToken, tokenUser);
		httppost.setEntity(strEntity);
		Builder requestConfigBuilder = RequestConfig.custom();
    	requestConfigBuilder.setConnectionRequestTimeout(4000).setConnectTimeout(4000).setSocketTimeout(4000);
    	httppost.setConfig(requestConfigBuilder.build());
		
		// Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			try {
				response = httpclient.execute(httppost);
			} catch (ClientProtocolException ea) {
				ea.printStackTrace();
			} catch (IOException ea) {
				ea.printStackTrace();
			}
		}
		HttpEntity entity = response.getEntity();

		String respString = "";
		
		if (entity != null) {
		    
			try {
				respString = EntityUtils.toString(entity);
			} catch (ParseException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}    
		}

		if (respString.contains("\"code\":")) {
			goingOn.append(respString);
		} else {
			goingOn.append("{ \"responses\" : " + respString + " }");
		}
		
		return true;
		
	}
	
	public Validation getUserIdFromToken(String userToken) {
		
		/*boolean currentSessionIsValid = isTokenValid();
		
		if (!currentSessionIsValid) {
			authenticate(null, null);
		}
		
		String adminAuthToken = SessionUtils.getAdminAuhtToken();*/
		
		URI uri = null;
		try {
			uri = new URIBuilder()
			.setScheme("http")
			.setHost(idpHost)
			.setPort(idpPort)
			.setPath(" /idp/json/sessions/"+userToken)
			.setCustomQuery("_action=validate")
			.build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		HttpPost httppost = new HttpPost(uri);
		httppost.setHeader("Content-Type", "application/json");
		Builder requestConfigBuilder = RequestConfig.custom();
    	requestConfigBuilder.setConnectionRequestTimeout(4000).setConnectTimeout(4000).setSocketTimeout(4000);
    	httppost.setConfig(requestConfigBuilder.build());
		
		// Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			try {
				response = httpclient.execute(httppost);
			} catch (ClientProtocolException ea) {
				ea.printStackTrace();
			} catch (IOException ea) {
				ea.printStackTrace();
			}
		}
		HttpEntity entity = response.getEntity();

		String respString = "";
		
		if (entity != null) {
		    
			try {
				respString = EntityUtils.toString(entity);
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}    
		}
		
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
	
	public Validation validateToken(String token, String userToken) {
		
		/*boolean currentSessionIsValid = isTokenValid();
		
		if (!currentSessionIsValid) {
			authenticate(null, null);
		}
		
		String adminAuthToken = SessionUtils.getAdminAuhtToken();*/
		
		URI uri = null;
		try {
			uri = new URIBuilder()
			.setScheme("http")
			.setHost(idpHost)
			.setPort(idpPort)
			.setPath(" /idp/json/sessions/")
			.setCustomQuery("_action=isActive&tokenId="+userToken)
			.build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		HttpPost httppost = new HttpPost(uri);
		httppost.setHeader("Content-Type", "application/json");
		httppost.setHeader(authToken, token);
		Builder requestConfigBuilder = RequestConfig.custom();
    	requestConfigBuilder.setConnectionRequestTimeout(4000).setConnectTimeout(4000).setSocketTimeout(4000);
    	httppost.setConfig(requestConfigBuilder.build());
		
		// Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			try {
				response = httpclient.execute(httppost);
			} catch (ClientProtocolException ea) {
				ea.printStackTrace();
			} catch (IOException ea) {
				ea.printStackTrace();
			}
		}
		HttpEntity entity = response.getEntity();

		String respString = "";
		
		if (entity != null) {
		    
			try {
				respString = EntityUtils.toString(entity);
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}    
		}
		
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
	
	public Users getUsers(String token) {
		
		/*boolean currentSessionIsValid = isTokenValid();
		
		if (!currentSessionIsValid) {
			authenticate(null, null);
		}
		
		String adminAuthToken = SessionUtils.getAdminAuhtToken();*/
		
		URI uri = null;
		try {
			uri = new URIBuilder()
			.setScheme("http")
			.setHost(idpHost)
			.setPort(idpPort)
			.setPath(" /idp/json/users")
			.setCustomQuery("_queryID=*")
			.build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		HttpGet httppost = new HttpGet(uri);
		httppost.setHeader("Content-Type", "application/json");
		httppost.setHeader(authToken, token);
		Builder requestConfigBuilder = RequestConfig.custom();
    	requestConfigBuilder.setConnectionRequestTimeout(4000).setConnectTimeout(4000).setSocketTimeout(4000);
    	httppost.setConfig(requestConfigBuilder.build());
		
		// Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			try {
				response = httpclient.execute(httppost);
			} catch (ClientProtocolException ea) {
				ea.printStackTrace();
			} catch (IOException ea) {
				ea.printStackTrace();
			}
		}
		HttpEntity entity = response.getEntity();

		String respString = "";
		
		if (entity != null) {
		    
			try {
				respString = EntityUtils.toString(entity);
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		    
		}
		
		Users users = new Users();
		
		try {
			users = (Users) JsonUtils.deserializeJson(respString, Users.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return users;
	}
	
	public Groups getGroups(String token) {
		
		/*boolean currentSessionIsValid = isTokenValid();
		
		if (!currentSessionIsValid) {
			authenticate(null, null);
		}
		
		String adminAuthToken = SessionUtils.getAdminAuhtToken();*/
		
		URI uri = null;
		try {
			uri = new URIBuilder()
			.setScheme("http")
			.setHost(idpHost)
			.setPort(idpPort)
			.setPath(" /idp/json/groups")
			.setCustomQuery("_queryID=*")
			.build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		HttpGet httppost = new HttpGet(uri);
		httppost.setHeader("Content-Type", "application/json");
		httppost.setHeader(authToken, token);
		Builder requestConfigBuilder = RequestConfig.custom();
    	requestConfigBuilder.setConnectionRequestTimeout(4000).setConnectTimeout(4000).setSocketTimeout(4000);
    	httppost.setConfig(requestConfigBuilder.build());
		
		// Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			try {
				response = httpclient.execute(httppost);
			} catch (ClientProtocolException ea) {
				ea.printStackTrace();
			} catch (IOException ea) {
				ea.printStackTrace();
			}
		}
		HttpEntity entity = response.getEntity();

		String respString = "";
		
		if (entity != null) {
		    
			try {
				respString = EntityUtils.toString(entity);
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		    
		}
		
		Groups groups = new Groups();
		
		try {
			groups = (Groups) JsonUtils.deserializeJson(respString, Groups.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return groups;
		
	}
	
	public Applications getApplications(String token) {
		
		/*boolean currentSessionIsValid = isTokenValid();
		
		if (!currentSessionIsValid) {
			authenticate(null, null);
		}
		
		String adminAuthToken = SessionUtils.getAdminAuhtToken();*/
		
		URI uri = null;
		try {
			uri = new URIBuilder()
			.setScheme("http")
			.setHost(idpHost)
			.setPort(idpPort)
			.setPath(" /idp/json/applications")
			.setCustomQuery("_queryFilter=true")
			.build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		HttpGet httppost = new HttpGet(uri);
		httppost.setHeader("Content-Type", "application/json");
		httppost.setHeader(authToken, token);
		Builder requestConfigBuilder = RequestConfig.custom();
    	requestConfigBuilder.setConnectionRequestTimeout(4000).setConnectTimeout(4000).setSocketTimeout(4000);
    	httppost.setConfig(requestConfigBuilder.build());
		
		// Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (ClientProtocolException ea) {
			ea.printStackTrace();
		} catch (IOException ea) {
			ea.printStackTrace();
		}
		HttpEntity entity = response.getEntity();

		String respString = "";
		
		if (entity != null) {
		    
			try {
				respString = EntityUtils.toString(entity);
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		    
		}
		
		Applications applications = new Applications();
		
		try {
			applications = (Applications) JsonUtils.deserializeJson(respString, Applications.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return applications;
	}
	
	public Policies getPolicies(String token) {
		
		/*boolean currentSessionIsValid = isTokenValid();
		
		if (!currentSessionIsValid) {
			authenticate(null, null);
		}
		
		String adminAuthToken = SessionUtils.getAdminAuhtToken();*/
		
		URI uri = null;
		try {
			uri = new URIBuilder()
			.setScheme("http")
			.setHost(idpHost)
			.setPort(idpPort)
			.setPath(" /idp/json/policies")
			.setCustomQuery("_queryID=*")
			.build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		HttpGet httppost = new HttpGet(uri);
		httppost.setHeader("Content-Type", "application/json");
		httppost.setHeader(authToken, token);
		Builder requestConfigBuilder = RequestConfig.custom();
    	requestConfigBuilder.setConnectionRequestTimeout(4000).setConnectTimeout(4000).setSocketTimeout(4000);
    	httppost.setConfig(requestConfigBuilder.build());
		
		// Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			try {
				response = httpclient.execute(httppost);
			} catch (ClientProtocolException ea) {
				ea.printStackTrace();
			} catch (IOException ea) {
				ea.printStackTrace();
			}
		}
		HttpEntity entity = response.getEntity();

		String respString = "";
		
		if (entity != null) {
		    
			try {
				respString = EntityUtils.toString(entity);
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		    
		}
		
		Policies policies = new Policies();
		
		try {
			policies = (Policies) JsonUtils.deserializeJson(respString, Policies.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return policies;
	}
	
	public Monitor getStats() {
		
		URI uri = null;
		try {
			uri = new URIBuilder()
			.setScheme("http")
			.setHost(idpHost)
			.setPort(idpPort)
			.setPath(" /snmp/openam_stats")
			.build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		HttpGet httpget = new HttpGet(uri);
		Builder requestConfigBuilder = RequestConfig.custom();
    	requestConfigBuilder.setConnectionRequestTimeout(4000).setConnectTimeout(4000).setSocketTimeout(4000);
    	httpget.setConfig(requestConfigBuilder.build());
		
		// Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httpget);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			try {
				response = httpclient.execute(httpget);
			} catch (ClientProtocolException ea) {
				ea.printStackTrace();
			} catch (IOException ea) {
				ea.printStackTrace();
			}
		}
		HttpEntity entity = response.getEntity();

		String respString = "";
		
		if (entity != null) {
		    
			try {
				respString = EntityUtils.toString(entity);
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		    
		}
		
		Monitor values = new Monitor();
		
		try {
			values = (Monitor) JsonUtils.deserializeJson(respString, Monitor.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return values;
	}
	
	public User getUser(String username, String token) {
		
		/*boolean currentSessionIsValid = isTokenValid();
		
		if (!currentSessionIsValid) {
			authenticate(null, null);
		}
		
		String adminAuthToken = SessionUtils.getAdminAuhtToken();*/
		
		URI uri = null;
		try {
			uri = new URIBuilder()
			.setScheme("http")
			.setHost(idpHost)
			.setPort(idpPort)
			.setPath(" /idp/json/users/"+username)
			.build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		HttpGet httppost = new HttpGet(uri);
		httppost.setHeader("Content-Type", "application/json");
		httppost.setHeader(authToken, token);
		Builder requestConfigBuilder = RequestConfig.custom();
    	requestConfigBuilder.setConnectionRequestTimeout(4000).setConnectTimeout(4000).setSocketTimeout(4000);
    	httppost.setConfig(requestConfigBuilder.build());
		
		// Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			try {
				response = httpclient.execute(httppost);
			} catch (ClientProtocolException ea) {
				ea.printStackTrace();
			} catch (IOException ea) {
				ea.printStackTrace();
			}
		}
		HttpEntity entity = response.getEntity();

		String respString = "";
		
		if (entity != null) {
		    
			try {
				respString = EntityUtils.toString(entity);
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		    
		}
		
		User user = new User();
		
		try {
			user = (User) JsonUtils.deserializeJson(respString, User.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		List<String> gn = null;
		gn = user.getGivenName();
		if((gn != null) && (!gn.isEmpty())) { // send back the first name if available
			if(gn.get(0).equals(" "))
				user.setGivenName(null);
		}
		
		gn = user.getGivenname();
		if((gn != null) && (!gn.isEmpty())) {
			if(gn.get(0).equals(" "))
				user.setGivenName(null);
		}
		
		return user;
	}
	
	public Group getGroup(String groupId, String token) {
		
		/*boolean currentSessionIsValid = isTokenValid();
		
		if (!currentSessionIsValid) {
			authenticate(null, null);
		}
		
		String adminAuthToken = SessionUtils.getAdminAuhtToken();*/
		
		URI uri = null;
		try {
			uri = new URIBuilder()
			.setScheme("http")
			.setHost(idpHost)
			.setPort(idpPort)
			.setPath(" /idp/json/groups/"+groupId)
			.build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		HttpGet httppost = new HttpGet(uri);
		httppost.setHeader("Content-Type", "application/json");
		httppost.setHeader(authToken, token);
		Builder requestConfigBuilder = RequestConfig.custom();
    	requestConfigBuilder.setConnectionRequestTimeout(4000).setConnectTimeout(4000).setSocketTimeout(4000);
    	httppost.setConfig(requestConfigBuilder.build());
		
		// Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			try {
				response = httpclient.execute(httppost);
			} catch (ClientProtocolException ea) {
				ea.printStackTrace();
			} catch (IOException ea) {
				ea.printStackTrace();
			}
		}
		HttpEntity entity = response.getEntity();

		String respString = "";
		
		if (entity != null) {
		    
			try {
				respString = EntityUtils.toString(entity);
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		    
		}
		
		Group group = new Group();
		
		try {
			group = (Group) JsonUtils.deserializeJson(respString, Group.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return group;
	}
	
	public Policy getPolicy(String policyId, String token) {
			
		/*boolean currentSessionIsValid = isTokenValid();
		
		if (!currentSessionIsValid) {
			authenticate(null, null);
		}
		
		String adminAuthToken = SessionUtils.getAdminAuhtToken();*/
		
		URI uri = null;
		try {
			uri = new URIBuilder()
			.setScheme("http")
			.setHost(idpHost)
			.setPort(idpPort)
			.setPath(" /idp/json/policies/"+policyId)
			.build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		HttpGet httppost = new HttpGet(uri);
		httppost.setHeader("Content-Type", "application/json");
		httppost.setHeader(authToken, token);
		Builder requestConfigBuilder = RequestConfig.custom();
    	requestConfigBuilder.setConnectionRequestTimeout(4000).setConnectTimeout(4000).setSocketTimeout(4000);
    	httppost.setConfig(requestConfigBuilder.build());
		
		// Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			try {
				response = httpclient.execute(httppost);
			} catch (ClientProtocolException ea) {
				ea.printStackTrace();
			} catch (IOException ea) {
				ea.printStackTrace();
			}
		}
		HttpEntity entity = response.getEntity();

		String respString = "";
		
		if (entity != null) {
		    
			try {
				respString = EntityUtils.toString(entity);
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		    
		}
		
		Policy policy = new Policy();
		
		try {
			policy = (Policy) JsonUtils.deserializeJson(respString, Policy.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return policy;
	}
	
	public Application getApplication(String applicationId, String token) {
		
		/*boolean currentSessionIsValid = isTokenValid();
		
		if (!currentSessionIsValid) {
			authenticate(null, null);
		}
		
		String adminAuthToken = SessionUtils.getAdminAuhtToken();*/
		
		URI uri = null;
		try {
			uri = new URIBuilder()
			.setScheme("http")
			.setHost(idpHost)
			.setPort(idpPort)
			.setPath(" /idp/json/applications/"+applicationId)
			.build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		HttpGet httppost = new HttpGet(uri);
		httppost.setHeader("Content-Type", "application/json");
		httppost.setHeader(authToken, token);
		Builder requestConfigBuilder = RequestConfig.custom();
    	requestConfigBuilder.setConnectionRequestTimeout(4000).setConnectTimeout(4000).setSocketTimeout(4000);
    	httppost.setConfig(requestConfigBuilder.build());
		
		// Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			try {
				response = httpclient.execute(httppost);
			} catch (ClientProtocolException ea) {
				ea.printStackTrace();
			} catch (IOException ea) {
				ea.printStackTrace();
			}
		}
		HttpEntity entity = response.getEntity();

		String respString = "";
		
		if (entity != null) {
		    
			try {
				respString = EntityUtils.toString(entity);
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		    
		}
		
		Application application = new Application();
		
		try {
			application = (Application) JsonUtils.deserializeJson(respString, Application.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return application;
	}
	
	public boolean createUser(String givenName, String surname, String username, String password, String mail, StringBuilder goingOn, String token) {
		
		/*boolean currentSessionIsValid = isTokenValid();
		
		if (!currentSessionIsValid) {
			authenticate(null, null);
		}
		
		String adminAuthToken = SessionUtils.getAdminAuhtToken();*/
		
		if (getUser(username, token).getUsername()!=null) {
			// user with the same name already existing
			return false;
		}
		
		UserModel userModel = new UserModel();
		
		userModel.setUsername(username);
		userModel.setUserpassword(password);
		userModel.setMail(mail);
		if(givenName != null && !givenName.isEmpty()) {
			userModel.setAdditionalProperty("givenName", givenName);
		}
		if(surname != null && !surname.isEmpty()) {
			userModel.setAdditionalProperty("sn", surname);
		}
		
		String newUser = "";
		
		try {
			newUser = JsonUtils.serializeJson(userModel);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		URI uri = null;
		try {
			uri = new URIBuilder()
			.setScheme("http")
			.setHost(idpHost)
			.setPort(idpPort)
			.setPath(" /idp/json/users/")
			.setCustomQuery("_action=create")
			.build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		StringEntity strEntity = new StringEntity(newUser, StandardCharsets.UTF_8);
		strEntity.setContentType("application/json");
		
		HttpPost httppost = new HttpPost(uri);
		httppost.setHeader("Content-Type", "application/json");
		httppost.setHeader(authToken, token);
		httppost.setEntity(strEntity);
		Builder requestConfigBuilder = RequestConfig.custom();
    	requestConfigBuilder.setConnectionRequestTimeout(4000).setConnectTimeout(4000).setSocketTimeout(4000);
    	httppost.setConfig(requestConfigBuilder.build());
		
		// Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			try {
				response = httpclient.execute(httppost);
			} catch (ClientProtocolException ea) {
				ea.printStackTrace();
			} catch (IOException ea) {
				ea.printStackTrace();
			}
		}
		HttpEntity entity = response.getEntity();

		String respString = "";
		
		if (entity != null) {
		    
			try {
				respString = EntityUtils.toString(entity);
			} catch (ParseException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}    
		}
		
		goingOn.append(respString);
		
		return true;
	}
	
	public boolean createGroup(String groupId, StringBuilder goingOn, String token) {
		
		/*boolean currentSessionIsValid = isTokenValid();
		
		if (!currentSessionIsValid) {
			authenticate(null, null);
		}
		
		String adminAuthToken = SessionUtils.getAdminAuhtToken();*/
		
		if (getGroup(groupId, token).getUsername()!=null) {
			// group with the same ID already existing 
			return false;
		}
		
		GroupModel groupModel = new GroupModel();
		groupModel.setUsername(groupId);
		
		String newGroup = "";
		
		try {
			newGroup = JsonUtils.serializeJson(groupModel);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		URI uri = null;
		try {
			uri = new URIBuilder()
			.setScheme("http")
			.setHost(idpHost)
			.setPort(idpPort)
			.setPath(" /idp/json/groups/")
			.setCustomQuery("_action=create")
			.build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		StringEntity strEntity = new StringEntity(newGroup, StandardCharsets.UTF_8);
		strEntity.setContentType("application/json");
		
		HttpPost httppost = new HttpPost(uri);
		httppost.setHeader("Content-Type", "application/json");
		httppost.setHeader(authToken, token);
		httppost.setEntity(strEntity);
		Builder requestConfigBuilder = RequestConfig.custom();
    	requestConfigBuilder.setConnectionRequestTimeout(4000).setConnectTimeout(4000).setSocketTimeout(4000);
    	httppost.setConfig(requestConfigBuilder.build());
		
		// Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			try {
				response = httpclient.execute(httppost);
			} catch (ClientProtocolException ea) {
				ea.printStackTrace();
			} catch (IOException ea) {
				ea.printStackTrace();
			}
		}
		HttpEntity entity = response.getEntity();

		String respString = "";
		
		if (entity != null) {
		    
			try {
				respString = EntityUtils.toString(entity);
			} catch (ParseException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}    
		}
		
		goingOn.append(respString);
			
		return true;
	}
	
	public boolean createApplication(String applicationName, String description, ArrayList<String> resources, StringBuilder goingOn, String token) {
		
		/*boolean currentSessionIsValid = isTokenValid();
		
		if (!currentSessionIsValid) {
			authenticate(null, null);
		}
		
		String adminAuthToken = SessionUtils.getAdminAuhtToken();*/
		
		if (getApplication(applicationName, token).getName() != null) {
			// application already existing, return false
			return false;
		}
		
		Application application = new Application();
		
		application.setName(applicationName);
		application.setDescription(description);
		application.setResources(resources);
		application.setAdditionalProperty("applicationType", "iPlanetAMWebAgentService");
		application.setAdditionalProperty("entitlementCombiner", "DenyOverride");
		
		List<String> subjects = Arrays.asList("AND", "OR", "NOT", "AuthenticatedUsers", "Identity", "JwtClaim");
		List<String> conditions = Arrays.asList(
				"AND",
		        "OR",
		        "NOT",
		        "AMIdentityMembership",
		        "AuthLevel",
		        "AuthScheme",
		        "AuthenticateToRealm",
		        "AuthenticateToService",
		        "IPv4",
		        "IPv6",
		        "LDAPFilter",
		        "LEAuthLevel",
		        "OAuth2Scope",
		        "ResourceEnvIP",
		        "Session",
		        "SessionProperty",
		        "SimpleTime");
		
		application.setSubjects(subjects);
		application.setConditions(conditions);
				
		String newApplication = "";
		try {
			newApplication = JsonUtils.serializeJson(application);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		URI uri = null;
		try {
			uri = new URIBuilder()
			.setScheme("http")
			.setHost(idpHost)
			.setPort(idpPort)
			.setPath(" /idp/json/applications/")
			.setCustomQuery("_action=create")
			.build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		StringEntity strEntity = new StringEntity(newApplication, StandardCharsets.UTF_8);
		strEntity.setContentType("application/json");
		
		HttpPost httppost = new HttpPost(uri);
		httppost.setHeader("Content-Type", "application/json");
		httppost.setHeader(authToken, token);
		httppost.setEntity(strEntity);
		Builder requestConfigBuilder = RequestConfig.custom();
    	requestConfigBuilder.setConnectionRequestTimeout(4000).setConnectTimeout(4000).setSocketTimeout(4000);
    	httppost.setConfig(requestConfigBuilder.build());
		
		// Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			try {
				response = httpclient.execute(httppost);
			} catch (ClientProtocolException ea) {
				ea.printStackTrace();
			} catch (IOException ea) {
				ea.printStackTrace();
			}
		}
		HttpEntity entity = response.getEntity();

		String respString = "";
		
		if (entity != null) {
		    
			try {
				respString = EntityUtils.toString(entity);
				if (respString.contains(applicationName)) {
					goingOn.append(respString);
					return true;
				}
			} catch (ParseException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}    
		}
		
		goingOn.append(respString);
			
		return false;
	}
	
	public boolean deleteUser(String username, StringBuilder goingOn, String token) {
		
		/*boolean currentSessionIsValid = isTokenValid();
		
		if (!currentSessionIsValid) {
			authenticate(null, null);
		}
		
		String adminAuthToken = SessionUtils.getAdminAuhtToken();*/
		
		String currentUserName = getUser(username, token).getUsername();
		
		if (currentUserName == null) {
			// non existing user
			return false;
		}
		
		URI uri = null;
		try {
			uri = new URIBuilder()
			.setScheme("http")
			.setHost(idpHost)
			.setPort(idpPort)
			.setPath(" /idp/json/users/"+username)
			.build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		HttpDelete httpdelete = new HttpDelete(uri);
		httpdelete.setHeader("Content-Type", "application/json");
		httpdelete.setHeader(authToken, token);
		Builder requestConfigBuilder = RequestConfig.custom();
    	requestConfigBuilder.setConnectionRequestTimeout(4000).setConnectTimeout(4000).setSocketTimeout(4000);
    	httpdelete.setConfig(requestConfigBuilder.build());
		
		// Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httpdelete);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			try {
				response = httpclient.execute(httpdelete);
			} catch (ClientProtocolException ea) {
				ea.printStackTrace();
			} catch (IOException ea) {
				ea.printStackTrace();
			}
		}
		HttpEntity entity = response.getEntity();

		String respString = "";
		
		if (entity != null) {
		    
			try {
				respString = EntityUtils.toString(entity);
				if (respString.contains("success")) {
					goingOn.append(respString);
					return true;
				}
			} catch (ParseException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		    
		}
		
		goingOn.append(respString);
		
		return false;
	}
	
	public boolean deleteGroup(String groupId, StringBuilder goingOn, String token) {
		
		/*boolean currentSessionIsValid = isTokenValid();
		
		if (!currentSessionIsValid) {
			authenticate(null, null);
		}
		
		String adminAuthToken = SessionUtils.getAdminAuhtToken();*/
		
		String currentGroupName = getGroup(groupId, token).getUsername();
		
		if (currentGroupName == null) {
			return false;
		}
		
		URI uri = null;
		try {
			uri = new URIBuilder()
			.setScheme("http")
			.setHost(idpHost)
			.setPort(idpPort)
			.setPath(" /idp/json/groups/"+groupId)
			.build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		HttpDelete httpdelete = new HttpDelete(uri);
		httpdelete.setHeader("Content-Type", "application/json");
		httpdelete.setHeader(authToken, token);
		Builder requestConfigBuilder = RequestConfig.custom();
    	requestConfigBuilder.setConnectionRequestTimeout(4000).setConnectTimeout(4000).setSocketTimeout(4000);
    	httpdelete.setConfig(requestConfigBuilder.build());
		
		// Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httpdelete);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			try {
				response = httpclient.execute(httpdelete);
			} catch (ClientProtocolException ea) {
				ea.printStackTrace();
			} catch (IOException ea) {
				ea.printStackTrace();
			}
		}
		HttpEntity entity = response.getEntity();

		String respString = "";
		
		if (entity != null) {
		    
			try {
				respString = EntityUtils.toString(entity);
				if (respString.contains("success")) {
					goingOn.append(respString);
					return true;
				}
			} catch (ParseException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		    
		}
		
		goingOn.append(respString);
		
		return false;
	}
	
	public boolean deletePolicy(String policyId, StringBuilder goingOn, String token) {
		
		/*boolean currentSessionIsValid = isTokenValid();
		
		if (!currentSessionIsValid) {
			authenticate(null, null);
		}
		
		String adminAuthToken = SessionUtils.getAdminAuhtToken();*/
		
		String currentPolicyName = getPolicy(policyId, token).getName();
		
		if (currentPolicyName == null) {
			return false;
		}
		
		URI uri = null;
		try {
			uri = new URIBuilder()
			.setScheme("http")
			.setHost(idpHost)
			.setPort(idpPort)
			.setPath(" /idp/json/policies/"+policyId)
			.build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		HttpDelete httpdelete = new HttpDelete(uri);
		httpdelete.setHeader("Content-Type", "application/json");
		httpdelete.setHeader(authToken, token);
		Builder requestConfigBuilder = RequestConfig.custom();
    	requestConfigBuilder.setConnectionRequestTimeout(4000).setConnectTimeout(4000).setSocketTimeout(4000);
    	httpdelete.setConfig(requestConfigBuilder.build());
		
		// Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httpdelete);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			try {
				response = httpclient.execute(httpdelete);
			} catch (ClientProtocolException ea) {
				ea.printStackTrace();
			} catch (IOException ea) {
				ea.printStackTrace();
			}
		}
		HttpEntity entity = response.getEntity();

		String respString = "";
		
		if (entity != null) {
		    
			try {
				respString = EntityUtils.toString(entity);
				if (respString.equals("{}")) {
					goingOn.append(respString);
					return true;
				}
			} catch (ParseException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		    
		}
		
		goingOn.append(respString);
		
		return false;
	}
	
	public boolean deleteApplication(String applicationId, StringBuilder goingOn, String token) {
		
		/*boolean currentSessionIsValid = isTokenValid();
		
		if (!currentSessionIsValid) {
			authenticate(null, null);
		}
		
		String adminAuthToken = SessionUtils.getAdminAuhtToken();*/
		
		String currentApplicationName = getApplication(applicationId, token).getName();
		
		if (currentApplicationName == null) {
			return false;
		}
		
		URI uri = null;
		try {
			uri = new URIBuilder()
			.setScheme("http")
			.setHost(idpHost)
			.setPort(idpPort)
			.setPath(" /idp/json/applications/"+applicationId)
			.build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		HttpDelete httpdelete = new HttpDelete(uri);
		httpdelete.setHeader("Content-Type", "application/json");
		httpdelete.setHeader(authToken, token);
		Builder requestConfigBuilder = RequestConfig.custom();
    	requestConfigBuilder.setConnectionRequestTimeout(4000).setConnectTimeout(4000).setSocketTimeout(4000);
    	httpdelete.setConfig(requestConfigBuilder.build());
		
		// Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httpdelete);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			try {
				response = httpclient.execute(httpdelete);
			} catch (ClientProtocolException ea) {
				ea.printStackTrace();
			} catch (IOException ea) {
				ea.printStackTrace();
			}
		}
		HttpEntity entity = response.getEntity();

		String respString = "";
		
		if (entity != null) {
		    
			try {
				respString = EntityUtils.toString(entity);
				if (respString.equals("{}")) {
					goingOn.append(respString);
					return true;
				}
			} catch (ParseException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		    
		}
		
		goingOn.append(respString);
		
		return false;
	}
	
	public boolean updateUser(String username, String givenName, String surname, String mail, String status, StringBuilder goingOn, String token) {
		
		/*boolean currentSessionIsValid = isTokenValid();
		
		if (!currentSessionIsValid) {
			authenticate(null, null);
		}
		
		String adminAuthToken = SessionUtils.getAdminAuhtToken();*/
		
		UserModel userModel = new UserModel();
		
		userModel.setMail(mail);
		if(givenName != null && !givenName.isEmpty()) {
			userModel.setAdditionalProperty("givenName", givenName);
		} else {
			userModel.setAdditionalProperty("givenName", " ");
		}
		if(surname != null && !surname.isEmpty()) {
			userModel.setAdditionalProperty("sn", surname);
		}
		
		String uid = getUserIdFromToken(token).getUid();
		if(!uid.equals(username) || uid.equals("amAdmin")) {
			userModel.setAdditionalProperty("inetUserStatus", status);
		}
		
		String newUserInfo = "";
		
		try {
			newUserInfo = JsonUtils.serializeJson(userModel);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
		URI uri = null;
		try {
			uri = new URIBuilder()
			.setScheme("http")
			.setHost(idpHost)
			.setPort(idpPort)
			.setPath(" /idp/json/users/"+username)
			.build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		StringEntity strEntity = new StringEntity(newUserInfo, StandardCharsets.UTF_8);
		strEntity.setContentType("application/json");
		
		HttpPut httpput = new HttpPut(uri);
		httpput.setHeader("Content-Type", "application/json");
		httpput.setHeader(authToken, token);
		httpput.setEntity(strEntity);
		Builder requestConfigBuilder = RequestConfig.custom();
    	requestConfigBuilder.setConnectionRequestTimeout(4000).setConnectTimeout(4000).setSocketTimeout(4000);
    	httpput.setConfig(requestConfigBuilder.build());
		
		// Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httpput);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			try {
				response = httpclient.execute(httpput);
			} catch (ClientProtocolException ea) {
				ea.printStackTrace();
			} catch (IOException ea) {
				ea.printStackTrace();
			}
		}
		HttpEntity entity = response.getEntity();

		String respString = "";
		
		if (entity != null) {
		    
			try {
				respString = EntityUtils.toString(entity);
			} catch (ParseException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}    
		}
		
		goingOn.append(respString);
		
		return true;
	}
	
	/**
	 * Set policy with subject "AuthenticatedUsers", only authenticated users can access
	 * @param policyName
	 * @param actions
	 * @param resources
	 */
	public boolean createAuthenticatedPolicy(String policyName, String description, ArrayList<Action> actions, ArrayList<String> resources, String token) {
		
		/*boolean currentSessionIsValid = isTokenValid();
		
		if (!currentSessionIsValid) {
			authenticate(null, null);
		}
		
		String adminAuthToken = SessionUtils.getAdminAuhtToken();*/
		
		if (getPolicy(policyName, token).getName() != null) {
			// existing policy with the same name
			return false;
		}
		
		PolicyAuthenticatedModel policyAuthenticatedModel = new PolicyAuthenticatedModel();
		
		policyAuthenticatedModel.setName(policyName);
		policyAuthenticatedModel.setActive(true);
		policyAuthenticatedModel.setDescription(description);
		policyAuthenticatedModel.setResources(resources);
		
		ActionValues__ actVal = new ActionValues__();
		
		for (int i = 0; i<actions.size(); i++) {
			Action currentAction = actions.get(i);
			String strAction = currentAction.getAction();
			
			if (strAction.equals("POST")) {
				actVal.setPOST(currentAction.getState());
			} else if (strAction.equals("PATCH")) {
				actVal.setPATCH(currentAction.getState());
			} else if (strAction.equals("GET")) {
				actVal.setGET(currentAction.getState());
			} else if (strAction.equals("DELETE")) {
				actVal.setDELETE(currentAction.getState());
			} else if (strAction.equals("OPTIONS")) {
				actVal.setOPTIONS(currentAction.getState());
			} else if (strAction.equals("PUT")) {
				actVal.setPUT(currentAction.getState());
			} else if (strAction.equals("HEAD")) {
				actVal.setHEAD(currentAction.getState());
			}
				
		}
		
		policyAuthenticatedModel.setActionValues(actVal);
		
		Subject__ sbj = new Subject__();
		sbj.setType("AuthenticatedUsers");
		
		policyAuthenticatedModel.setSubject(sbj);
				
		String newPolicy = "";
		try {
			newPolicy = JsonUtils.serializeJson(policyAuthenticatedModel);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		URI uri = null;
		try {
			uri = new URIBuilder()
			.setScheme("http")
			.setHost(idpHost)
			.setPort(idpPort)
			.setPath(" /idp/json/policies/")
			.setCustomQuery("_action=create")
			.build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		StringEntity strEntity = new StringEntity(newPolicy, StandardCharsets.UTF_8);
		strEntity.setContentType("application/json");
		
		HttpPost httppost = new HttpPost(uri);
		httppost.setHeader("Content-Type", "application/json");
		httppost.setHeader(authToken, token);
		httppost.setEntity(strEntity);
		Builder requestConfigBuilder = RequestConfig.custom();
    	requestConfigBuilder.setConnectionRequestTimeout(4000).setConnectTimeout(4000).setSocketTimeout(4000);
    	httppost.setConfig(requestConfigBuilder.build());
		
		// Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			try {
				response = httpclient.execute(httppost);
			} catch (ClientProtocolException ea) {
				ea.printStackTrace();
			} catch (IOException ea) {
				ea.printStackTrace();
			}
		}
		HttpEntity entity = response.getEntity();

		String respString = "";
		
		if (entity != null) {
		    
			try {
				respString = EntityUtils.toString(entity);
				if (respString.contains(policyName)) {
					return true;
				}
			} catch (ParseException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}    
		}
		
		return false;
		
	}
	
	/**
	 * Set policy: only the defined users have access 
	 * @param policyName
	 * @param actions
	 * @param resources
	 * @param users
	 * @return
	 */
	public boolean createIdentityUsersPolicy(String policyName, String description, ArrayList<Action> actions, ArrayList<String> resources, ArrayList<String> users, String token) {
		
		/*boolean currentSessionIsValid = isTokenValid();
		
		if (!currentSessionIsValid) {
			authenticate(null, null);
		}
		
		String adminAuthToken = SessionUtils.getAdminAuhtToken();*/
		
		if (getPolicy(policyName, token).getName()!=null) {
			// existing policy with the same name
			return false;
		}
		
		// Check if whether all the groups exist, otherwise return false
		// If they are OK construct the array of universal IDs
		
		ArrayList<String> usersId = new ArrayList<String>();
		
		for (int i = 0; i < users.size(); i++) {
			String currentUser = users.get(i);
			if (getUser(currentUser, token).getUsername() == null) {
				return false;
			} else {
				usersId.add(getUser(currentUser, token).getUniversalid().get(0)); 
			}
		}
		
		PolicyIdentityModel policyIdentityModel = new PolicyIdentityModel();
		
		policyIdentityModel.setName(policyName);
		policyIdentityModel.setActive(true);
		policyIdentityModel.setDescription(description);
		policyIdentityModel.setResources(resources);
		
		ActionValues___ actVal = new ActionValues___();
		
		for (int i = 0; i<actions.size(); i++) {
			Action currentAction = actions.get(i);
			String strAction = currentAction.getAction();
			
			if (strAction.equals("POST")) {
				actVal.setPOST(currentAction.getState());
			} else if (strAction.equals("PATCH")) {
				actVal.setPATCH(currentAction.getState());
			} else if (strAction.equals("GET")) {
				actVal.setGET(currentAction.getState());
			} else if (strAction.equals("DELETE")) {
				actVal.setDELETE(currentAction.getState());
			} else if (strAction.equals("OPTIONS")) {
				actVal.setOPTIONS(currentAction.getState());
			} else if (strAction.equals("PUT")) {
				actVal.setPUT(currentAction.getState());
			} else if (strAction.equals("HEAD")) {
				actVal.setHEAD(currentAction.getState());
			}
				
		}
		
		policyIdentityModel.setActionValues(actVal);
		
		Subject___ sbj = new Subject___();
		sbj.setType("Identity");
		sbj.setSubjectValues(usersId);
		
		policyIdentityModel.setSubject(sbj);
				
		String newPolicy = "";
		try {
			newPolicy = JsonUtils.serializeJson(policyIdentityModel);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		URI uri = null;
		try {
			uri = new URIBuilder()
			.setScheme("http")
			.setHost(idpHost)
			.setPort(idpPort)
			.setPath(" /idp/json/policies/")
			.setCustomQuery("_action=create")
			.build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		StringEntity strEntity = new StringEntity(newPolicy, StandardCharsets.UTF_8);
		strEntity.setContentType("application/json");
		
		HttpPost httppost = new HttpPost(uri);
		httppost.setHeader("Content-Type", "application/json");
		httppost.setHeader(authToken, token);
		httppost.setEntity(strEntity);
		Builder requestConfigBuilder = RequestConfig.custom();
    	requestConfigBuilder.setConnectionRequestTimeout(4000).setConnectTimeout(4000).setSocketTimeout(4000);
    	httppost.setConfig(requestConfigBuilder.build());
		
		// Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			try {
				response = httpclient.execute(httppost);
			} catch (ClientProtocolException ea) {
				ea.printStackTrace();
			} catch (IOException ea) {
				ea.printStackTrace();
			}
		}
		HttpEntity entity = response.getEntity();

		String respString = "";
		
		if (entity != null) {
		    
			try {
				respString = EntityUtils.toString(entity);
				if (respString.contains(policyName)) {
					return true;
				}
			} catch (ParseException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}    
		}
		
		return false;
	}
	
	public boolean createIdentityGroupsPolicy(String policyName, String description, ArrayList<Action> actions, ArrayList<String> resources, ArrayList<String> groups, String applicationName, StringBuilder goingOn, String token) {
	
		/*boolean currentSessionIsValid = isTokenValid();
		
		if (!currentSessionIsValid) {
			authenticate(null, null);
		}
		
		String adminAuthToken = SessionUtils.getAdminAuhtToken();*/
		
		if (getPolicy(policyName, token).getName() != null) {
			// existing policy with the same name
			return false;
		}
		
		// Check if whether all the groups exist, otherwise return false
		// If they are OK construct the array of universal IDs
		
		ArrayList<String> groupsId = new ArrayList<String>();
		
		for (int i = 0; i < groups.size();i++) {
			String currentGroup = groups.get(i);
			if (getGroup(currentGroup, token).getUsername() == null) {
				return false;
			} else {
				groupsId.add(getGroup(currentGroup, token).getUniversalid().get(0)); 
			}
		}
		
		PolicyIdentityModel policyIdentityModel = new PolicyIdentityModel();
		
		policyIdentityModel.setName(policyName);
		policyIdentityModel.setActive(true);
		policyIdentityModel.setDescription(description);
		policyIdentityModel.setResources(resources);
		policyIdentityModel.setApplicationName(applicationName);
		
		ActionValues___ actVal = new ActionValues___();
		
		for (int i = 0; i<actions.size(); i++) {
			Action currentAction = actions.get(i);
			String strAction = currentAction.getAction();
			
			if (strAction.equals("POST")) {
				actVal.setPOST(currentAction.getState());
			} else if (strAction.equals("PATCH")) {
				actVal.setPATCH(currentAction.getState());
			} else if (strAction.equals("GET")) {
				actVal.setGET(currentAction.getState());
			} else if (strAction.equals("DELETE")) {
				actVal.setDELETE(currentAction.getState());
			} else if (strAction.equals("OPTIONS")) {
				actVal.setOPTIONS(currentAction.getState());
			} else if (strAction.equals("PUT")) {
				actVal.setPUT(currentAction.getState());
			} else if (strAction.equals("HEAD")) {
				actVal.setHEAD(currentAction.getState());
			}
				
		}
		
		policyIdentityModel.setActionValues(actVal);
		
		Subject___ sbj = new Subject___();
		sbj.setType("Identity");
		sbj.setSubjectValues(groupsId);
		
		policyIdentityModel.setSubject(sbj);
				
		String newPolicy = "";
		try {
			newPolicy = JsonUtils.serializeJson(policyIdentityModel);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		URI uri = null;
		try {
			uri = new URIBuilder()
			.setScheme("http")
			.setHost(idpHost)
			.setPort(idpPort)
			.setPath(" /idp/json/policies/")
			.setCustomQuery("_action=create")
			.build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		StringEntity strEntity = new StringEntity(newPolicy, StandardCharsets.UTF_8);
		strEntity.setContentType("application/json");
		
		HttpPost httppost = new HttpPost(uri);
		httppost.setHeader("Content-Type", "application/json");
		httppost.setHeader(authToken, token);
		httppost.setEntity(strEntity);
		Builder requestConfigBuilder = RequestConfig.custom();
    	requestConfigBuilder.setConnectionRequestTimeout(4000).setConnectTimeout(4000).setSocketTimeout(4000);
    	httppost.setConfig(requestConfigBuilder.build());
		
		// Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			try {
				response = httpclient.execute(httppost);
			} catch (ClientProtocolException ea) {
				ea.printStackTrace();
			} catch (IOException ea) {
				ea.printStackTrace();
			}
		}
		HttpEntity entity = response.getEntity();

		String respString = "";
		
		if (entity != null) {
		    
			try {
				respString = EntityUtils.toString(entity);
				if (respString.contains(policyName)) {
					goingOn.append(respString);
					return true;
				}
			} catch (ParseException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}    
		}
		
		goingOn.append(respString);
			
		return false;
	}
	
	public boolean updatePolicyIdentity(String name, String description, Boolean active, ArrayList<String> groups, Boolean nogr, ArrayList<String> resources, Boolean nores, ArrayList<Action> actions, Boolean noact, StringBuilder goingOn, String token) {
		
		/*boolean currentSessionIsValid = isTokenValid();
		
		if (!currentSessionIsValid) {
			authenticate(null, null);
		}
		
		String adminAuthToken = SessionUtils.getAdminAuhtToken();*/
		
		PolicyIdentityModel policyModel = new PolicyIdentityModel();
		Subject___ sub = new Subject___();
		policyModel.setName(name); // to be sure it not included in the JSON (name is used in the URL)
		policyModel.setActive(active);
		policyModel.setDescription(description);
		policyModel.setApplicationName(getPolicy(name, token).getApplicationName());
		
		if(resources.isEmpty() && !nores) {
			policyModel.setResources(getPolicy(name, token).getResources());
		}
		else if(!nores) {
			policyModel.setResources(resources);
		}
		
		if(groups.isEmpty() && !nogr) {
			try {
				policyModel.setSubject((Subject___) JsonUtils.deserializeJson(JsonUtils.serializeJson(getPolicy(name, token).getSubject()), sub.getClass()));
			} catch (JsonParseException e2) {
				e2.printStackTrace();
			} catch (JsonMappingException e2) {
				e2.printStackTrace();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		} else {
			ArrayList<String> groupsId = new ArrayList<String>();
			
			for(int i = 0; i < groups.size(); i++) {
				String currentGroup = groups.get(i);
				if(getGroup(currentGroup, token).getUsername() == null) {
					return false;
				} else {
					groupsId.add(getGroup(currentGroup, token).getUniversalid().get(0)); 
				}
			}
			
			Subject___ sbj = new Subject___();
			sbj.setType("Identity");
			sbj.setSubjectValues(groupsId);
			
			policyModel.setSubject(sbj);
		}
		
		ActionValues___ actVal = new ActionValues___();
		
		if(!actions.isEmpty()) {
			
			for (int i = 0; i<actions.size(); i++) {
				Action currentAction = actions.get(i);
				String strAction = currentAction.getAction();
				
				if (strAction.equals("POST")) {
					actVal.setPOST(currentAction.getState());
				} else if (strAction.equals("PATCH")) {
					actVal.setPATCH(currentAction.getState());
				} else if (strAction.equals("GET")) {
					actVal.setGET(currentAction.getState());
				} else if (strAction.equals("DELETE")) {
					actVal.setDELETE(currentAction.getState());
				} else if (strAction.equals("OPTIONS")) {
					actVal.setOPTIONS(currentAction.getState());
				} else if (strAction.equals("PUT")) {
					actVal.setPUT(currentAction.getState());
				} else if (strAction.equals("HEAD")) {
					actVal.setHEAD(currentAction.getState());
				}
					
			}
			
			policyModel.setActionValues(actVal);
		} else if(!noact) {
			try {
				policyModel.setActionValues((ActionValues___) JsonUtils.deserializeJson(JsonUtils.serializeJson(getPolicy(name, token).getActionValues()), actVal.getClass()));
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
				
		String newPolicyInfo = "";
		
		try {
			newPolicyInfo = JsonUtils.serializeJson(policyModel);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
		URI uri = null;
		try {
			uri = new URIBuilder()
			.setScheme("http")
			.setHost(idpHost)
			.setPort(idpPort)
			.setPath(" /idp/json/policies/"+name)
			.build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		StringEntity strEntity = new StringEntity(newPolicyInfo, StandardCharsets.UTF_8);
		strEntity.setContentType("application/json");
		
		HttpPut httpput = new HttpPut(uri);
		httpput.setHeader("Content-Type", "application/json");
		httpput.setHeader(authToken, token);
		httpput.setEntity(strEntity);
		Builder requestConfigBuilder = RequestConfig.custom();
    	requestConfigBuilder.setConnectionRequestTimeout(4000).setConnectTimeout(4000).setSocketTimeout(4000);
    	httpput.setConfig(requestConfigBuilder.build());
		
		// Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httpput);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			try {
				response = httpclient.execute(httpput);
			} catch (ClientProtocolException ea) {
				ea.printStackTrace();
			} catch (IOException ea) {
				ea.printStackTrace();
			}
		}
		HttpEntity entity = response.getEntity();

		String respString = "";
		
		if (entity != null) {
		    
			try {
				respString = EntityUtils.toString(entity);
			} catch (ParseException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}    
		}
		
		goingOn.append(respString);
		
		return true;
	}
	
	public boolean updatePolicyAuthenticated(String name, String description, Boolean active, ArrayList<String> groups, Boolean nogr, ArrayList<String> resources, Boolean nores, ArrayList<Action> actions, Boolean noact, StringBuilder goingOn, String token) {
		
		/*boolean currentSessionIsValid = isTokenValid();
		
		if (!currentSessionIsValid) {
			authenticate(null, null);
		}
		
		String adminAuthToken = SessionUtils.getAdminAuhtToken();*/
		
		PolicyAuthenticatedModel policyModel = new PolicyAuthenticatedModel();
		Subject__ sub = new Subject__();
		policyModel.setName(name); // to be sure it not included in the JSON (name is used in the URL)
		policyModel.setActive(active);
		policyModel.setDescription(description);
		
		if(resources.isEmpty() && !nores) {
			policyModel.setResources(getPolicy(name, token).getResources());
		}
		else if(!nores) {
			policyModel.setResources(resources);
		}
		
		if(groups.isEmpty() && !nogr) {
			try {
				policyModel.setSubject((Subject__) JsonUtils.deserializeJson(JsonUtils.serializeJson(getPolicy(name, token).getSubject()), sub.getClass()));
			} catch (JsonParseException e2) {
				e2.printStackTrace();
			} catch (JsonMappingException e2) {
				e2.printStackTrace();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		} else {
			ArrayList<String> groupsId = new ArrayList<String>();
			
			for (int i=0; i<groups.size();i++) {
				String currentGroup = groups.get(i);
				if (getGroup(currentGroup, token).getUsername() == null) {
					return false;
				} else {
					groupsId.add(getGroup(currentGroup, token).getUniversalid().get(0)); 
				}
			}
			
			Subject__ sbj = new Subject__();
			sbj.setType("AuthenticatedUsers");
			
			policyModel.setSubject(sbj);
		}
		
		ActionValues__ actVal = new ActionValues__();
		
		if(!actions.isEmpty()) {
			
			for (int i = 0; i<actions.size(); i++) {
				Action currentAction = actions.get(i);
				String strAction = currentAction.getAction();
				
				if (strAction.equals("POST")) {
					actVal.setPOST(currentAction.getState());
				} else if (strAction.equals("PATCH")) {
					actVal.setPATCH(currentAction.getState());
				} else if (strAction.equals("GET")) {
					actVal.setGET(currentAction.getState());
				} else if (strAction.equals("DELETE")) {
					actVal.setDELETE(currentAction.getState());
				} else if (strAction.equals("OPTIONS")) {
					actVal.setOPTIONS(currentAction.getState());
				} else if (strAction.equals("PUT")) {
					actVal.setPUT(currentAction.getState());
				} else if (strAction.equals("HEAD")) {
					actVal.setHEAD(currentAction.getState());
				}
					
			}
			
			policyModel.setActionValues(actVal);
		} else  if(!noact) {
			try {
				policyModel.setActionValues((ActionValues__) JsonUtils.deserializeJson(JsonUtils.serializeJson(getPolicy(name, token).getActionValues()), actVal.getClass()));
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		String newPolicyInfo = "";
		
		try {
			newPolicyInfo = JsonUtils.serializeJson(policyModel);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
		URI uri = null;
		try {
			uri = new URIBuilder()
			.setScheme("http")
			.setHost(idpHost)
			.setPort(idpPort)
			.setPath(" /idp/json/policies/"+name)
			.build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		StringEntity strEntity = new StringEntity(newPolicyInfo, StandardCharsets.UTF_8);
		strEntity.setContentType("application/json");
		
		HttpPut httpput = new HttpPut(uri);
		httpput.setHeader("Content-Type", "application/json");
		httpput.setHeader(authToken, token);
		httpput.setEntity(strEntity);
		Builder requestConfigBuilder = RequestConfig.custom();
    	requestConfigBuilder.setConnectionRequestTimeout(4000).setConnectTimeout(4000).setSocketTimeout(4000);
    	httpput.setConfig(requestConfigBuilder.build());
		
		// Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httpput);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			try {
				response = httpclient.execute(httpput);
			} catch (ClientProtocolException ea) {
				ea.printStackTrace();
			} catch (IOException ea) {
				ea.printStackTrace();
			}
		}
		HttpEntity entity = response.getEntity();

		String respString = "";
		
		if (entity != null) {
		    
			try {
				respString = EntityUtils.toString(entity);
			} catch (ParseException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}    
		}
		
		goingOn.append(respString);
		
		return true;
	}
	
	public boolean updateGroup(String groupId, GroupModelWithUsers groupInfo, StringBuilder goingOn, String token) {
		
		/*boolean currentSessionIsValid = isTokenValid();
		
		if (!currentSessionIsValid) {
			authenticate(null, null);
		}
		
		String adminAuthToken = SessionUtils.getAdminAuhtToken();*/
		
		String newGroup = "";
		
		try {
			newGroup = JsonUtils.serializeJson(groupInfo);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		URI uri = null;
		try {
			uri = new URIBuilder()
			.setScheme("http")
			.setHost(idpHost)
			.setPort(idpPort)
			.setPath(" /idp/json/groups/"+groupId)
			.build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		StringEntity strEntity = new StringEntity(newGroup, StandardCharsets.UTF_8);
		strEntity.setContentType("application/json");
		
		HttpPut httpput = new HttpPut(uri);
		httpput.setHeader("Content-Type", "application/json");
		httpput.setHeader(authToken, token);
		httpput.setEntity(strEntity);
		Builder requestConfigBuilder = RequestConfig.custom();
    	requestConfigBuilder.setConnectionRequestTimeout(4000).setConnectTimeout(4000).setSocketTimeout(4000);
    	httpput.setConfig(requestConfigBuilder.build());
		
		// Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httpput);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			try {
				response = httpclient.execute(httpput);
			} catch (ClientProtocolException ea) {
				ea.printStackTrace();
			} catch (IOException ea) {
				ea.printStackTrace();
			}
		}
		HttpEntity entity = response.getEntity();

		String respString = "";
		
		if (entity != null) {
		    
			try {
				respString = EntityUtils.toString(entity);
			} catch (ParseException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}    
		}
		
		goingOn.append(respString);
		
		return true;
		
	}
	
	public boolean updateApplication(String applicationName, String description, ArrayList<String> resources, Boolean nores, StringBuilder goingOn, String token) {
		
		/*boolean currentSessionIsValid = isTokenValid();
		
		if (!currentSessionIsValid) {
			authenticate(null, null);
		}
		
		String adminAuthToken = SessionUtils.getAdminAuhtToken();*/
		
		Application application = new Application();
		
		application.setName(applicationName);
		application.setDescription(description);
		if(resources.isEmpty() && !nores) {
			application.setResources(getApplication(applicationName, token).getResources());
		}
		else if(!nores) {
			application.setResources(resources);
		}
		application.setAdditionalProperty("applicationType", "iPlanetAMWebAgentService");
		application.setAdditionalProperty("entitlementCombiner", "DenyOverride");
		
		List<String> subjects = Arrays.asList("AND", "OR", "NOT", "AuthenticatedUsers", "Identity", "JwtClaim");
		List<String> conditions = Arrays.asList(
				"AND",
		        "OR",
		        "NOT",
		        "AMIdentityMembership",
		        "AuthLevel",
		        "AuthScheme",
		        "AuthenticateToRealm",
		        "AuthenticateToService",
		        "IPv4",
		        "IPv6",
		        "LDAPFilter",
		        "LEAuthLevel",
		        "OAuth2Scope",
		        "ResourceEnvIP",
		        "Session",
		        "SessionProperty",
		        "SimpleTime");
		
		application.setSubjects(subjects);
		application.setConditions(conditions);
				
		String newApplicationInfo = "";
		
		try {
			newApplicationInfo = JsonUtils.serializeJson(application);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
		URI uri = null;
		try {
			uri = new URIBuilder()
			.setScheme("http")
			.setHost(idpHost)
			.setPort(idpPort)
			.setPath(" /idp/json/applications/"+applicationName)
			.build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		StringEntity strEntity = new StringEntity(newApplicationInfo, StandardCharsets.UTF_8);
		strEntity.setContentType("application/json");
		
		HttpPut httpput = new HttpPut(uri);
		httpput.setHeader("Content-Type", "application/json");
		httpput.setHeader(authToken, token);
		httpput.setEntity(strEntity);
		Builder requestConfigBuilder = RequestConfig.custom();
    	requestConfigBuilder.setConnectionRequestTimeout(4000).setConnectTimeout(4000).setSocketTimeout(4000);
    	httpput.setConfig(requestConfigBuilder.build());
		
		// Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httpput);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			try {
				response = httpclient.execute(httpput);
			} catch (ClientProtocolException ea) {
				ea.printStackTrace();
			} catch (IOException ea) {
				ea.printStackTrace();
			}
		}
		HttpEntity entity = response.getEntity();

		String respString = "";
		
		if (entity != null) {
		    
			try {
				respString = EntityUtils.toString(entity);
			} catch (ParseException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}    
		}
		
		goingOn.append(respString);
		
		return true;
	}
	
	public boolean addUsersToGroup(String groupId, ArrayList<String> users, StringBuilder goingOn, String token) {
				
		Group currentGroup = getGroup(groupId, token);
		
		if (currentGroup.getUsername() == null) {
			// the group does not exist
			return false;
		}
		
		List<String> currentUsers = currentGroup.getUniqueMember(); // users currently in the group
		ArrayList<String> usersId = new ArrayList<String>(); // IDs of users to add
		
		
		for (int i = 0; i < users.size(); i++) {
			String currentUser = users.get(i);
			if (getUser(currentUser, token).getUsername() == null) {
				// one of the users does not exist
				return false;
			} else {
				usersId.add(getUser(currentUser, token).getDn().get(0)); 
			}
		}
		
		// If there are already users in the group,
		// check for duplicates and do not add them (delete them from the input list)
		if (currentUsers.size() > 0) {
			for (int i = 0; i < usersId.size(); i++) {
				String auxId = usersId.get(i);
				if (currentUsers.contains(auxId)) {
					usersId.remove(i);
				}
			}
		}
		
		for(int i = 0; i < usersId.size(); i++) {
			currentUsers.add(usersId.get(i));
		}
		
		GroupModelWithUsers newGroupInfo = new GroupModelWithUsers();
		
		newGroupInfo.setUniqueMember(currentUsers);
		
		return updateGroup(groupId, newGroupInfo, goingOn, token);
		
	}
	
	public boolean deleteUsersFromGroup(String groupId, ArrayList<String> users, StringBuilder goingOn, String token) {
		
		Group currentGroup = getGroup(groupId, token);
		
		if (currentGroup.getUsername() == null) {
			// the group does not exist
			return false;
		}
		
		List<String> currentUsers = currentGroup.getUniqueMember(); // users currently in the group
		ArrayList<String> usersId = new ArrayList<String>(); // IDs of users to add
		
		
		for (int i = 0; i < users.size(); i++) {
			String currentUser = users.get(i);
			if (getUser(currentUser, token).getUsername() != null) {
				usersId.add(getUser(currentUser, token).getDn().get(0));
			}
		}
		
		if (currentUsers.size() > 0) {
			for (int i = 0; i < usersId.size(); i++){
				if (currentUsers.contains(usersId.get(i))) {
					currentUsers.remove(usersId.get(i));
				}
			}
		} else {
			// no users to remove
			return true;
		}
		
		GroupModelWithUsers newGroupInfo = new GroupModelWithUsers();
		
		newGroupInfo.setUniqueMember(currentUsers);
		
		return updateGroup(groupId, newGroupInfo, goingOn, token);
		
	}
	
	public boolean userIsInGroup(String userId, String groupId, String token) {
		
		User currentUser = getUser(userId, token);
		Group currentGroup = getGroup(groupId, token);
		
		if (currentUser.getUsername() == null) {
			// the user does not exist
			return false;
		}
		if (currentGroup.getUsername() == null) {
			// the group does not exist
			return false;
		}
		
		List<String> groupUsers = currentGroup.getUniqueMember();
		String currentUserId = currentUser.getDn().get(0);
		
		if (groupUsers.contains(currentUserId)) {
			return true;
		}
		
		return false;
	}
	
	public Groups listUserGroups(String userId, String token) {
		
		Groups groups = getGroups(token);
		List<String> list = groups.getResult();
		Iterator<String> iter = list.listIterator();
		
		while (iter.hasNext()) {
			String group = iter.next();
			if(!userIsInGroup(userId, group, token)) {
				iter.remove();
				groups.setResultCount(groups.getResultCount()-1);
			}
		}
		groups.setResult(list);
		
		return groups;
	}
	
	public Policies listApplicationPolicies(String appName, String token) {
		
		Policies policies = getPolicies(token);
		List<Result> list = policies.getResult();
		Iterator<Result> iter = list.listIterator();
		
		while (iter.hasNext()) {
			Result policy = iter.next();
			if(!policy.getApplicationName().equals(appName)) {
				iter.remove();
				policies.setResultCount(policies.getResultCount()-1);
			}
		}
		policies.setResult(list);
		
		return policies;
	}
	
}

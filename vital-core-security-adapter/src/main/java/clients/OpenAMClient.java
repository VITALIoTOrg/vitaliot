package clients;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jsonpojos.ActionValues__;
import jsonpojos.ActionValues___;
import jsonpojos.Authenticate;
import jsonpojos.Group;
import jsonpojos.GroupModel;
import jsonpojos.GroupModelWithUsers;
import jsonpojos.Groups;
import jsonpojos.Policies;
import jsonpojos.Policy;
import jsonpojos.PolicyAuthenticatedModel;
import jsonpojos.PolicyIdentityModel;
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
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
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
	
	public OpenAMClient() {
		httpclient = HttpClients.createDefault();
		configReader = ConfigReader.getInstance();
		
		idpHost = configReader.get(ConfigReader.IDP_HOST);
		idpPort = Integer.parseInt(configReader.get(ConfigReader.IDP_PORT));
		userAdmin = configReader.get(ConfigReader.USER_ADM);
		pwdAdmin = configReader.get(ConfigReader.PWD_ADM);
		authToken = configReader.get(ConfigReader.AUTH_TOKEN);
		
	}
	
	private boolean isTokenValid() {
		
		String adminAuthToken = SessionUtils.getAdminAuhtToken();
		
		URI uri = null;
		try {
			uri = new URIBuilder()
			.setScheme("http")
			.setHost(idpHost)
			.setPort(idpPort)
			.setPath(" /idp/json/sessions/"+adminAuthToken)
			.setQuery("_action=validate")
			.build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		HttpPost httppost = new HttpPost(uri);
		httppost.setHeader("Content-Type", "application/json");
		httppost.setHeader(authToken, adminAuthToken);
		
		//Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
	
	private void authenticate() {
		
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
		httppost.setHeader("X-OpenAM-Username", userAdmin);
		httppost.setHeader("X-OpenAM-Password", pwdAdmin);
		

		//Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
		//this.adminAuthToken = auth.getTokenId();
		SessionUtils.setAdminAuthToken(auth.getTokenId());
		
	}
	
	public String getUserIdFromToken(String userToken) {
		
		boolean currentSessionIsValid = isTokenValid();
		
		if (!currentSessionIsValid) {
			authenticate();
		}
		
		String uid = "";
		
		String adminAuthToken = SessionUtils.getAdminAuhtToken();
		
		URI uri = null;
		try {
			uri = new URIBuilder()
			.setScheme("http")
			.setHost(idpHost)
			.setPort(idpPort)
			.setPath(" /idp/json/sessions/"+userToken)
			.setQuery("_action=validate")
			.build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		HttpPost httppost = new HttpPost(uri);
		httppost.setHeader("Content-Type", "application/json");
		httppost.setHeader(authToken, adminAuthToken);
		
		//Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
			uid = validation.getUid();
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		
		return uid;
	}
	
	public Users getUsers() {
		
		boolean currentSessionIsValid = isTokenValid();
		
		if (!currentSessionIsValid) {
			authenticate();
		}
		
		String adminAuthToken = SessionUtils.getAdminAuhtToken();
		
		URI uri = null;
		try {
			uri = new URIBuilder()
			.setScheme("http")
			.setHost(idpHost)
			.setPort(idpPort)
			.setPath(" /idp/json/users")
			.setQuery("_queryID=*")
			.build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		HttpGet httppost = new HttpGet(uri);
		httppost.setHeader("Content-Type", "application/json");
		httppost.setHeader(authToken, adminAuthToken);
		
		//Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
	
	public Groups getGroups() {
		
		boolean currentSessionIsValid = isTokenValid();
		
		if (!currentSessionIsValid) {
			authenticate();
		}
		
		String adminAuthToken = SessionUtils.getAdminAuhtToken();
		
		URI uri = null;
		try {
			uri = new URIBuilder()
			.setScheme("http")
			.setHost(idpHost)
			.setPort(idpPort)
			.setPath(" /idp/json/groups")
			.setQuery("_queryID=*")
			.build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		HttpGet httppost = new HttpGet(uri);
		httppost.setHeader("Content-Type", "application/json");
		httppost.setHeader(authToken, adminAuthToken);
		
		//Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
	
	public Policies getPolicies() {
		
		boolean currentSessionIsValid = isTokenValid();
		
		if (!currentSessionIsValid) {
			authenticate();
		}
		
		String adminAuthToken = SessionUtils.getAdminAuhtToken();
		
		URI uri = null;
		try {
			uri = new URIBuilder()
			.setScheme("http")
			.setHost(idpHost)
			.setPort(idpPort)
			.setPath(" /idp/json/policies")
			.setQuery("_queryID=*")
			.build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		HttpGet httppost = new HttpGet(uri);
		httppost.setHeader("Content-Type", "application/json");
		httppost.setHeader(authToken, adminAuthToken);
		
		//Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
	
	public User getUser(String username) {
		
		boolean currentSessionIsValid = isTokenValid();
		
		if (!currentSessionIsValid) {
			authenticate();
		}
		
		String adminAuthToken = SessionUtils.getAdminAuhtToken();
		
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
		httppost.setHeader(authToken, adminAuthToken);
		
		//Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
		
		
		return user;
	}
	
	public Group getGroup(String groupId) {
		
		boolean currentSessionIsValid = isTokenValid();
		
		if (!currentSessionIsValid) {
			authenticate();
		}
		
		String adminAuthToken = SessionUtils.getAdminAuhtToken();
		
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
		httppost.setHeader(authToken, adminAuthToken);
		
		//Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
	
	public Policy getPolicy(String policyId) {
			
		boolean currentSessionIsValid = isTokenValid();
		
		if (!currentSessionIsValid) {
			authenticate();
		}
		
		String adminAuthToken = SessionUtils.getAdminAuhtToken();
		
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
		httppost.setHeader(authToken, adminAuthToken);
		
		//Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
	
	public boolean createUser(String givenName, String surname, String username, String password, String mail) {
		
		boolean currentSessionIsValid = isTokenValid();
		
		if (!currentSessionIsValid) {
			authenticate();
		}
		
		String adminAuthToken = SessionUtils.getAdminAuhtToken();
		
		if (getUser(username).getUsername()!=null) {
			//utente con stesso nome è già presente
			return false;
		}
		
		UserModel userModel = new UserModel();
		
		userModel.setUsername(username);
		userModel.setUserpassword(password);
		userModel.setMail(mail);
		userModel.setAdditionalProperty("givenName", givenName);
		userModel.setAdditionalProperty("sn", surname);
		
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
			.setQuery("_action=create")
			.build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		StringEntity strEntity = new StringEntity(newUser, HTTP.UTF_8);
		strEntity.setContentType("application/json");
		
		HttpPost httppost = new HttpPost(uri);
		httppost.setHeader("Content-Type", "application/json");
		httppost.setHeader(authToken, adminAuthToken);
		httppost.setEntity(strEntity);
		
		//Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
		
		return true;
	}
	
	
	public boolean createGroup(Group group) {
		
		boolean currentSessionIsValid = isTokenValid();
		
		if (!currentSessionIsValid) {
			authenticate();
		}
		
		String adminAuthToken = SessionUtils.getAdminAuhtToken();
		
		GroupModelWithUsers groupModelWithUsers = new GroupModelWithUsers();
		groupModelWithUsers.setUsername(group.getUsername());
		groupModelWithUsers.setUniqueMember(group.getUniqueMember());
		
		String newGroup = "";
		
		try {
			newGroup = JsonUtils.serializeJson(groupModelWithUsers);
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
			.setQuery("_action=create")
			.build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		StringEntity strEntity = new StringEntity(newGroup, HTTP.UTF_8);
		strEntity.setContentType("application/json");
		
		HttpPost httppost = new HttpPost(uri);
		httppost.setHeader("Content-Type", "application/json");
		httppost.setHeader(authToken, adminAuthToken);
		httppost.setEntity(strEntity);
		
		
		//Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
		
		return true;
	}
	
	
	public boolean createGroup(String groupId) {
		
		boolean currentSessionIsValid = isTokenValid();
		
		if (!currentSessionIsValid) {
			authenticate();
		}
		
		String adminAuthToken = SessionUtils.getAdminAuhtToken();
		
		if (getGroup(groupId).getUsername()!=null) {
			//un gruppo con lo stesso id è già presente
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
			.setQuery("_action=create")
			.build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		StringEntity strEntity = new StringEntity(newGroup, HTTP.UTF_8);
		strEntity.setContentType("application/json");
		
		HttpPost httppost = new HttpPost(uri);
		httppost.setHeader("Content-Type", "application/json");
		httppost.setHeader(authToken, adminAuthToken);
		httppost.setEntity(strEntity);
		
		
		//Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
			
		return true;
	}
	
	public boolean deleteUser(String username) {
		
		boolean currentSessionIsValid = isTokenValid();
		
		if (!currentSessionIsValid) {
			authenticate();
		}
		
		String adminAuthToken = SessionUtils.getAdminAuhtToken();
		
		String currentUserName = getUser(username).getUsername();
		
		if (currentUserName == null) {
			//utente non presente
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
		httpdelete.setHeader(authToken, adminAuthToken);
		
		//Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httpdelete);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		HttpEntity entity = response.getEntity();

		String respString = "";
		
		if (entity != null) {
		    
			try {
				respString = EntityUtils.toString(entity);
				if (respString.contains("success")) {
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
	
	public boolean deleteGroup(String groupId) {
		
		boolean currentSessionIsValid = isTokenValid();
		
		if (!currentSessionIsValid) {
			authenticate();
		}
		
		String adminAuthToken = SessionUtils.getAdminAuhtToken();
		
		String currentGroupName = getGroup(groupId).getUsername();
		
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
		httpdelete.setHeader(authToken, adminAuthToken);
		
		//Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httpdelete);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		HttpEntity entity = response.getEntity();

		String respString = "";
		
		if (entity != null) {
		    
			try {
				respString = EntityUtils.toString(entity);
				if (respString.contains("success")) {
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
	
	public boolean deletePolicy(String policyId) {
		
		boolean currentSessionIsValid = isTokenValid();
		
		if (!currentSessionIsValid) {
			authenticate();
		}
		
		String adminAuthToken = SessionUtils.getAdminAuhtToken();
		
		String currentPolicyName = getPolicy(policyId).getName();
		
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
		httpdelete.setHeader(authToken, adminAuthToken);
		
		//Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httpdelete);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		HttpEntity entity = response.getEntity();

		String respString = "";
		
		if (entity != null) {
		    
			try {
				respString = EntityUtils.toString(entity);
				if (respString.equals("{}")) {
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
	
	public boolean updateUser(String username, String givenName, String surname, String mail, String status) {
		
		boolean currentSessionIsValid = isTokenValid();
		
		if (!currentSessionIsValid) {
			authenticate();
		}
		
		String adminAuthToken = SessionUtils.getAdminAuhtToken();
		
		UserModel userModel = new UserModel();
		
		userModel.setUsername(null); // to be sure it not included in the JSON (username is used in the URL) 
		userModel.setMail(mail);
		userModel.setAdditionalProperty("givenName", givenName);
		userModel.setAdditionalProperty("sn", surname);
		userModel.setAdditionalProperty("inetUserStatus", status);
		
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
		
		StringEntity strEntity = new StringEntity(newUserInfo, HTTP.UTF_8);
		strEntity.setContentType("application/json");
		
		HttpPut httpput = new HttpPut(uri);
		httpput.setHeader("Content-Type", "application/json");
		httpput.setHeader(authToken, adminAuthToken);
		httpput.setEntity(strEntity);
		
		//Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httpput);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
		
		return true;
	}
	
	/**
	 * Set policy with subject "AuthenticatedUsers", only authenticated users can access
	 * @param policyName
	 * @param actions
	 * @param resources
	 */
	public boolean createAuthenticatedPolicy(String policyName, ArrayList<Action> actions, ArrayList<String> resources) {
		
		boolean currentSessionIsValid = isTokenValid();
		
		if (!currentSessionIsValid) {
			authenticate();
		}
		
		String adminAuthToken = SessionUtils.getAdminAuhtToken();
		
		if (getPolicy(policyName).getName()!=null) {
			//policy con stesso nome già presente, return false
			return false;
		}
		
		PolicyAuthenticatedModel policyAuthenticatedModel = new PolicyAuthenticatedModel();
		
		policyAuthenticatedModel.setName(policyName);
		policyAuthenticatedModel.setActive(true);
		policyAuthenticatedModel.setDescription(policyName+" created from REST.");
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
		
		//policy creata, procedi con la chiamata REST per inserirla in OpenAM
		
		URI uri = null;
		try {
			uri = new URIBuilder()
			.setScheme("http")
			.setHost(idpHost)
			.setPort(idpPort)
			.setPath(" /idp/json/policies/")
			.setQuery("_action=create")
			.build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		StringEntity strEntity = new StringEntity(newPolicy, HTTP.UTF_8);
		strEntity.setContentType("application/json");
		
		HttpPost httppost = new HttpPost(uri);
		httppost.setHeader("Content-Type", "application/json");
		httppost.setHeader(authToken, adminAuthToken);
		httppost.setEntity(strEntity);
		
		//Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
	public boolean createIdentityUsersPolicy(String policyName, ArrayList<Action> actions, ArrayList<String> resources, ArrayList<String> users) {
		
		boolean currentSessionIsValid = isTokenValid();
		
		if (!currentSessionIsValid) {
			authenticate();
		}
		
		String adminAuthToken = SessionUtils.getAdminAuhtToken();
		
		if (getPolicy(policyName).getName()!=null) {
			//policy con stesso nome già presente, return false
			return false;
		}
		
		//controllo che gli utenti da inserire nella policy siano tutti presenti, altrimento return false
		//altrimenti popolo l'array con gli universalid
		
		ArrayList<String> usersId = new ArrayList<String>();
		
		for (int i=0; i<users.size();i++) {
			String currentUser = users.get(i);
			if (getUser(currentUser).getUsername() == null) {
				return false;
			} else {
				usersId.add(getUser(currentUser).getUniversalid().get(0)); 
			}
		}
		
		PolicyIdentityModel policyIdentityModel = new PolicyIdentityModel();
		
		policyIdentityModel.setName(policyName);
		policyIdentityModel.setActive(true);
		policyIdentityModel.setDescription(policyName+" creted from REST.");
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
		
		//policy creata, procedi con la chiamata REST per inserirla in OpenAM
		
		URI uri = null;
		try {
			uri = new URIBuilder()
			.setScheme("http")
			.setHost(idpHost)
			.setPort(idpPort)
			.setPath(" /idp/json/policies/")
			.setQuery("_action=create")
			.build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		StringEntity strEntity = new StringEntity(newPolicy, HTTP.UTF_8);
		strEntity.setContentType("application/json");
		
		HttpPost httppost = new HttpPost(uri);
		httppost.setHeader("Content-Type", "application/json");
		httppost.setHeader(authToken, adminAuthToken);
		httppost.setEntity(strEntity);
		
		//Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
	
	public boolean createIdentityGroupsPolicy(String policyName, ArrayList<Action> actions, ArrayList<String> resources, ArrayList<String> groups) {
	
		boolean currentSessionIsValid = isTokenValid();
		
		if (!currentSessionIsValid) {
			authenticate();
		}
		
		String adminAuthToken = SessionUtils.getAdminAuhtToken();
		
		if (getPolicy(policyName).getName()!=null) {
			//policy con stesso nome già presente, return false
			return false;
		}
		
		//controllo che i gruppi da inserire nella policy siano tutti presenti, altrimento return false
		//altrimenti popolo l'array con gli universalid
		
		ArrayList<String> groupsId = new ArrayList<String>();
		
		for (int i=0; i<groups.size();i++) {
			String currentGroup = groups.get(i);
			if (getGroup(currentGroup).getUsername() == null) {
				return false;
			} else {
				groupsId.add(getGroup(currentGroup).getUniversalid().get(0)); 
			}
		}
		
		PolicyIdentityModel policyIdentityModel = new PolicyIdentityModel();
		
		policyIdentityModel.setName(policyName);
		policyIdentityModel.setActive(true);
		policyIdentityModel.setDescription(policyName+" created from REST.");
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
		
		//policy creata, procedi con la chiamata REST per inserirla in OpenAM
		
		URI uri = null;
		try {
			uri = new URIBuilder()
			.setScheme("http")
			.setHost(idpHost)
			.setPort(idpPort)
			.setPath(" /idp/json/policies/")
			.setQuery("_action=create")
			.build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		StringEntity strEntity = new StringEntity(newPolicy, HTTP.UTF_8);
		strEntity.setContentType("application/json");
		
		HttpPost httppost = new HttpPost(uri);
		httppost.setHeader("Content-Type", "application/json");
		httppost.setHeader(authToken, adminAuthToken);
		httppost.setEntity(strEntity);
		
		//Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
	
	public boolean updatePolicy(String name, String description, Boolean active, StringBuilder goingOn) {
		
		boolean currentSessionIsValid = isTokenValid();
		
		if (!currentSessionIsValid) {
			authenticate();
		}
		
		String adminAuthToken = SessionUtils.getAdminAuhtToken();
		
		PolicyIdentityModel policyModel = new PolicyIdentityModel();
		
		policyModel.setName(name); // to be sure it not included in the JSON (name is used in the URL)
		policyModel.setActive(active);
		policyModel.setDescription(description);
		policyModel.setResources(getPolicy(name).getResources());
				
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
		
		StringEntity strEntity = new StringEntity(newPolicyInfo, HTTP.UTF_8);
		strEntity.setContentType("application/json");
		
		HttpPut httpput = new HttpPut(uri);
		httpput.setHeader("Content-Type", "application/json");
		httpput.setHeader(authToken, adminAuthToken);
		httpput.setEntity(strEntity);
		
		//Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httpput);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
	
	public boolean updateGroup(String groupId, GroupModelWithUsers groupInfo) {
		
		boolean currentSessionIsValid = isTokenValid();
		
		if (!currentSessionIsValid) {
			authenticate();
		}
		
		String adminAuthToken = SessionUtils.getAdminAuhtToken();
		
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
		
		StringEntity strEntity = new StringEntity(newGroup, HTTP.UTF_8);
		strEntity.setContentType("application/json");
		
		HttpPut httpput = new HttpPut(uri);
		httpput.setHeader("Content-Type", "application/json");
		httpput.setHeader(authToken, adminAuthToken);
		httpput.setEntity(strEntity);
		
		
		//Execute and get the response.
		HttpResponse response = null;
		try {
			response = httpclient.execute(httpput);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
		
		return true;
		
	}
	
	public boolean addUsersToGroup(String groupId, ArrayList<String> users) {
				
		Group currentGroup = getGroup(groupId);
		
		if (currentGroup.getUsername() == null) {
			//il gruppo non esiste
			return false;
		}
		
		
		List<String> currentUsers = currentGroup.getUniqueMember(); //utenti al momento presenti nel gruppo
		ArrayList<String> usersId = new ArrayList<String>(); //uid degli utenti da aggiungere
		
		
		for (int i=0; i<users.size();i++) {
			String currentUser = users.get(i);
			if (getUser(currentUser).getUsername() == null) {
				//uno degli utenti richiesti non esiste, esci
				return false;
			} else {
				usersId.add(getUser(currentUser).getDn().get(0)); 
			}
		}
		
		//se già ci sono utenti nel gruppo,
		//controllare possibili duplicati, e nel caso non aggiungerli (li cancello dalla lista passata in input)
		if (currentUsers.size() > 0) {
			for (int i=0; i<usersId.size();i++) {
				String auxId = usersId.get(i);
				if (currentUsers.contains(auxId)) {
					usersId.remove(i);
				}
			}
		}
		
		for(int i=0; i<usersId.size(); i++) {
			currentUsers.add(usersId.get(i));
		}
		
		GroupModelWithUsers newGroupInfo = new GroupModelWithUsers();
		
		newGroupInfo.setUniqueMember(currentUsers);
		
		return updateGroup(groupId, newGroupInfo);
		
	}
	
	public boolean deleteUsersFromGroup(String groupId, ArrayList<String> users) {
		
		Group currentGroup = getGroup(groupId);
		
		if (currentGroup.getUsername() == null) {
			//il gruppo non esiste
			return false;
		}
		
		List<String> currentUsers = currentGroup.getUniqueMember(); //utenti al momento presenti nel gruppo
		ArrayList<String> usersId = new ArrayList<String>(); //uid degli utenti da aggiungere
		
		
		for (int i=0; i<users.size();i++) {
			String currentUser = users.get(i);
			if (getUser(currentUser).getUsername() != null) {
				usersId.add(getUser(currentUser).getDn().get(0));
			}
		}
		
		if (currentUsers.size() > 0) {
			for (int i=0;i<usersId.size();i++){
				if (currentUsers.contains(usersId.get(i))) {
					currentUsers.remove(usersId.get(i));
				}
			}
		} else {
			//non ci sono utenti da eliminare!
			return true;
		}
		
		GroupModelWithUsers newGroupInfo = new GroupModelWithUsers();
		
		newGroupInfo.setUniqueMember(currentUsers);
		
		return updateGroup(groupId, newGroupInfo);
		
	}
	
	public boolean userIsInGroup(String userId, String groupId) {
		
		User currentUser = getUser(userId);
		Group currentGroup = getGroup(groupId);
		
		if (currentUser.getUsername() == null) {
			//l'utente non esiste
			return false;
		}
		if (currentGroup.getUsername() == null) {
			//il gruppo non esiste
			return false;
		}
		
		List<String> groupUsers = currentGroup.getUniqueMember();
		String currentUserId = currentUser.getDn().get(0);
		
		if (groupUsers.contains(currentUserId)) {
			return true;
		}
		
		return false;
	}
	
	public Groups listUserGroups(String userId) {
		
		Groups groups = getGroups();
		List<String> list = groups.getResult();
		Iterator<String> iter = list.listIterator();
		while (iter.hasNext()) {
			String group = iter.next();
			if(!userIsInGroup(userId, group)) {
				iter.remove();
				groups.setResultCount(groups.getResultCount()-1);
			}
		}
		groups.setResult(list);
		
		return groups;
	}
	
}

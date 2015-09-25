package securitywrapper;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.List;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import jsonpojos.Application;
import jsonpojos.Applications;
import jsonpojos.Group;
import jsonpojos.Groups;
import jsonpojos.Policies;
import jsonpojos.Policy;
import jsonpojos.SimpleDate;
import jsonpojos.User;
import jsonpojos.Users;
import jsonpojos.Validation;
import jsonpojos.Monitor;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import utils.JsonUtils;
import utils.MD5Util;
import clients.OpenAMClient;

@Path("/rest")
public class GetServices {
	
	private OpenAMClient client;
	
	public GetServices() {
		client = new OpenAMClient();
	}
	
	@Path("/user/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUser(
            @PathParam("id") String userId,
            @CookieParam("vitalManToken") String token) {
		
		User user;
		String answer;
		int code;
		
		answer = null;
		code = 0;
		user = client.getUser(userId, token);
		
		try {
			answer = JsonUtils.serializeJson(user);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(user.getAdditionalProperties().containsKey("code")) {
			if(user.getAdditionalProperties().get("code").getClass() == Integer.class) {
				code = (Integer) user.getAdditionalProperties().get("code");
			}
		}
		if(code >= 400 && code < 500) {
			return Response.status(Status.BAD_REQUEST)
				.entity(answer)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
				.build();
		}
		else if(code >= 500 && code < 600) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(answer)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
		}
		else {
			return Response.ok()
					.entity(answer)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
		}
		
	}
	
	@Path("/user/{id}/groups")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserGroups(
            @PathParam("id") String userId,
            @CookieParam("vitalManToken") String token) {
		
		String answer;
		
		answer = null;
		
		Groups groups = client.listUserGroups(userId, token);
		
		try {
			answer = JsonUtils.serializeJson(groups);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return Response.ok()
				.entity(answer)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
				.build();
		
	}
	
	@Path("/group/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getGroup(
            @PathParam("id") String groupId,
            @CookieParam("vitalManToken") String token)  {
		
		Group group;
		String answer;
		int code;
		
		answer = null;
		code = 0;
		group = client.getGroup(groupId, token);
		
		try {
			answer = JsonUtils.serializeJson(group);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(group.getAdditionalProperties().containsKey("code")) {
			if(group.getAdditionalProperties().get("code").getClass() == Integer.class) {
				code = (Integer) group.getAdditionalProperties().get("code");
			}
		}
		if(code >= 400 && code < 500) {
			return Response.status(Status.BAD_REQUEST)
				.entity(answer)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
				.build();
		}
		else if(code >= 500 && code < 600) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(answer)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
		}
		else {
			return Response.ok()
					.entity(answer)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
		}
		
	}
	
	@Path("/policy/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPolicy(
            @PathParam("id") String policyId,
            @CookieParam("vitalManToken") String token) {
		
		Policy policy;
		String answer;
		int code;
		
		answer = null;
		code = 0;
		policy = client.getPolicy(policyId, token);
		
		try {
			answer = JsonUtils.serializeJson(policy);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(policy.getAdditionalProperties().containsKey("code")) {
			if(policy.getAdditionalProperties().get("code").getClass() == Integer.class) {
				code = (Integer) policy.getAdditionalProperties().get("code");
			}
		}
		if(code >= 400 && code < 500) {
			return Response.status(Status.BAD_REQUEST)
				.entity(answer)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
				.build();
		}
		else if(code >= 500 && code < 600) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(answer)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
		}
		else {
			return Response.ok()
					.entity(answer)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
		}
		
	}
	
	@Path("/application/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getApplication(
            @PathParam("id") String applicationId,
            @CookieParam("vitalManToken") String token) {
		
		Application application;
		String answer;
		int code;
		
		answer = null;
		code = 0;
		application = client.getApplication(applicationId, token);
		
		try {
			answer = JsonUtils.serializeJson(application);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(application.getAdditionalProperties().containsKey("code")) {
			if(application.getAdditionalProperties().get("code").getClass() == Integer.class) {
				code = (Integer) application.getAdditionalProperties().get("code");
			}
		}
		if(code >= 400 && code < 500) {
			return Response.status(Status.BAD_REQUEST)
				.entity(answer)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
				.build();
		}
		else if(code >= 500 && code < 600) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(answer)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
		}
		else {
			return Response.ok()
					.entity(answer)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
		}
		
	}
	
	@Path("/application/{id}/policies")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getApplicationPolicies(
            @PathParam("id") String appName,
            @CookieParam("vitalManToken") String token) {
		
		String answer;
		
		answer = null;
		
		Policies policies = client.listApplicationPolicies(appName, token);
		
		try {
			answer = JsonUtils.serializeJson(policies);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return Response.ok()
				.entity(answer)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
				.build();
		
	}
	
	@Path("/users")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUsers(
            @CookieParam("vitalManToken") String token) {
		
		Users users;
		String answer;
		int code;
		
		answer = null;
		code = 0;
		users = client.getUsers(token);
		
		try {
			answer = JsonUtils.serializeJson(users);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(users.getAdditionalProperties().containsKey("code")) {
			if(users.getAdditionalProperties().get("code").getClass() == Integer.class) {
				code = (Integer) users.getAdditionalProperties().get("code");
			}
		}
		if(code >= 400 && code < 500) {
			return Response.status(Status.BAD_REQUEST)
				.entity(answer)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
				.build();
		}
		else if(code >= 500 && code < 600) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(answer)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
		}
		else {
			return Response.ok()
					.entity(answer)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
		}
		
	}
	
	@Path("/groups")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getGroups(
			@CookieParam("vitalManToken") String token) {
		
		Groups groups;
		String answer;
		int code;
		
		answer = null;
		code = 0;
		groups = client.getGroups(token);
		
		try {
			answer = JsonUtils.serializeJson(groups);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(groups.getAdditionalProperties().containsKey("code")) {
			if(groups.getAdditionalProperties().get("code").getClass() == Integer.class) {
				code = (Integer) groups.getAdditionalProperties().get("code");
			}
		}
		if(code >= 400 && code < 500) {
			return Response.status(Status.BAD_REQUEST)
				.entity(answer)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
				.build();
		}
		else if(code >= 500 && code < 600) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(answer)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
		}
		else {
			return Response.ok()
					.entity(answer)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
		}
		
	}
	
	@Path("/policies")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPolicies(
			@CookieParam("vitalManToken") String token) {
		
		Policies policies;
		String answer;
		int code;
		
		answer = null;
		code = 0;
		policies = client.getPolicies(token);
		
		try {
			answer = JsonUtils.serializeJson(policies);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(policies.getAdditionalProperties().containsKey("code")) {
			if(policies.getAdditionalProperties().get("code").getClass() == Integer.class) {
				code = (Integer) policies.getAdditionalProperties().get("code");
			}
		}
		if(code >= 400 && code < 500) {
			return Response.status(Status.BAD_REQUEST)
				.entity(answer)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
				.build();
		}
		else if(code >= 500 && code < 600) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(answer)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
		}
		else {
			return Response.ok()
					.entity(answer)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
		}
		
	}
	
	@Path("/applications")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getApplications(
			@CookieParam("vitalManToken") String token) {
		
		Applications apps;
		String answer;
		int code;
		
		answer = null;
		code = 0;
		apps = client.getApplications(token);
		
		try {
			answer = JsonUtils.serializeJson(apps);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(apps.getAdditionalProperties().containsKey("code")) {
			if(apps.getAdditionalProperties().get("code").getClass() == Integer.class) {
				code = (Integer) apps.getAdditionalProperties().get("code");
			}
		}
		if(code >= 400 && code < 500) {
			return Response.status(Status.BAD_REQUEST)
				.entity(answer)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
				.build();
		}
		else if(code >= 500 && code < 600) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(answer)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
		}
		else {
			return Response.ok()
					.entity(answer)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
		}
		
	}
	
	@Path("/stats")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSessions() {
		
		Monitor values = new Monitor();
		String answer, value;
		String oidValue;
		Boolean ok;
		
		answer = value = null;
		ok = true;
		
		
		if(ok) {
			// Active Sessions
			oidValue = ".1.3.6.1.4.1.42.2.230.3.1.1.2.1.11.1.0";
			value = client.getStatValue(oidValue);
			if(value.startsWith("Error")) {
				ok = false;
			}
			else {
				values.setActiveSessions(Long.parseLong(value));
			}
		}
		
		if(ok) {
			// Current Internal Sessions
			oidValue = ".1.3.6.1.4.1.36733.1.2.1.1.1.0";
			value = client.getStatValue(oidValue);
			if(value.startsWith("Error")) {
				ok = false;
			}
			else {
				values.setCurrInternalSessions(Long.parseLong(value));
			}
		}
		
		if(ok) {
			// Current Remote Sessions
			oidValue = ".1.3.6.1.4.1.36733.1.2.1.2.1.0";
			value = client.getStatValue(oidValue);
			if(value.startsWith("Error")) {
				ok = false;
			}
			else {
				values.setCurrRemoteSessions(Long.parseLong(value));
			}
		}

		if(ok) {
			// Cumulative Policy Evaluations (specific resource)
			oidValue = ".1.3.6.1.4.1.36733.1.2.2.1.1.1.0";
			value = client.getStatValue(oidValue);
			if(value.startsWith("Error")) {
				ok = false;
			}
			else {
				values.setCumPolicyEval(Long.parseLong(value));
			}
		}
		
		if(ok) {
			// Average rate of policy evaluations for specific resources
			oidValue = ".1.3.6.1.4.1.36733.1.2.2.1.1.2.0";
			value = client.getStatValue(oidValue);
			if(value.startsWith("Error")) {
				ok = false;
			}
			else {
				values.setAvgPolicyEval(Long.parseLong(value));
			}
		}
		
		if(ok) {
			// Average rate of policy evaluations for a tree of resources (subtree)
			oidValue = ".1.3.6.1.4.1.36733.1.2.2.1.2.1.0";
			value = client.getStatValue(oidValue);
			if(value.startsWith("Error")) {
				ok = false;
			}
			else {
				values.setAvgPolicyEvalTree(Long.parseLong(value));
			}
		}
		
		if(ok) {
			try {
				answer = JsonUtils.serializeJson(values);
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return Response.ok()
					.entity(answer)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
		}
		else {
			answer = value.substring(7);
			return Response.status(Status.BAD_REQUEST)
					.entity(answer)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
		}
		
	}
	
	@Path("/user")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserFromToken(
			@CookieParam("vitalManToken") String token,
			@CookieParam("vitalAccessToken") String vitalToken,
			@QueryParam("altCookie") boolean altCookie) {
		
		Validation val;
		String answer;
		int code;
		
		answer = null;
		code = 0;
		if(altCookie) {
			val = client.getUserIdFromToken(token);
		} else {
			val = client.getUserIdFromToken(vitalToken);
		}
		
		// Let's give back some additional info about the user
		User user = client.getUser(val.getUid(), token); // get the info
		List<String> gn = null;
		gn = user.getGivenName();
		if((gn != null) && (!gn.isEmpty())) { // send back the first name if available
			val.setName(gn.get(0));
		} else {
			gn = user.getGivenname();
			if((gn != null) && (!gn.isEmpty())) {
				val.setName(gn.get(0));
			}
		}
		List<String> ln = null;
		ln = user.getSn();
		if((gn != null) && (!gn.isEmpty()) && (ln != null) && (!ln.isEmpty())) { // send back the full name if available
			val.setFullname(gn.get(0) + " " + ln.get(0)); // composed by first name + last name
		} else { // otherwise use common name, but it is not the full name for sure
			List<String> cn = null;
			cn = user.getCn();
			if((cn != null) && (!cn.isEmpty())) {
				val.setFullname(cn.get(0));
			}
		}
		
		List<String> mail = null;
		mail = user.getMail();
		if((mail != null) && (!mail.isEmpty())) {
			val.setMailhash(MD5Util.md5Hex(mail.get(0)));
		}
		
		List<String> time = user.getCreateTimestamp();
		SimpleDate date = new SimpleDate();
		if((time != null) && (!time.isEmpty())) {
			date.setYear(time.get(0).substring(0, 4));
			int m = Integer.parseInt(time.get(0).substring(4, 6));
			date.setMonth(new DateFormatSymbols().getMonths()[m - 1]);
			date.setDay(time.get(0).substring(6, 8));
			val.setCreation(date);
		}
		
		try {
			answer = JsonUtils.serializeJson(val);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(val.getAdditionalProperties().containsKey("code")) {
			if(val.getAdditionalProperties().get("code").getClass() == Integer.class) {
				code = (Integer) val.getAdditionalProperties().get("code");
			}
		}
		if(code >= 400 && code < 500) {
			return Response.status(Status.BAD_REQUEST)
				.entity(answer)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
				.build();
		}
		else if(code >= 500 && code < 600) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(answer)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
		}
		else {
			return Response.ok()
					.entity(answer)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
		}
		
	}
	
	@Path("/validate")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response validateToken(
			@CookieParam("vitalManToken") String token,
			@CookieParam("vitalAccessToken") String vitalToken,
			@QueryParam("altCookie") boolean altCookie) {
		
		Validation val;
		String answer;
		int code;
		
		answer = null;
		code = 0;
		if(altCookie) {
			val = client.validateToken(token, token);
		} else {
			val = client.validateToken(token, vitalToken);
		}
		
		try {
			answer = JsonUtils.serializeJson(val);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(val.getAdditionalProperties().containsKey("code")) {
			if(val.getAdditionalProperties().get("code").getClass() == Integer.class) {
				code = (Integer) val.getAdditionalProperties().get("code");
			}
		}
		if(code >= 400 && code < 500) {
			return Response.status(Status.BAD_REQUEST)
				.entity(answer)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
				.build();
		}
		else if(code >= 500 && code < 600) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(answer)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
		}
		else {
			return Response.ok()
					.entity(answer)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
		}
		
	}
		
}

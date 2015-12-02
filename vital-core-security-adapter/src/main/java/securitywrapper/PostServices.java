package securitywrapper;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import jsonpojos.Application;
import jsonpojos.Authenticate;
import jsonpojos.AuthenticationResponse;
import jsonpojos.ChangePasswordResponse;
import jsonpojos.DecisionArray;
import jsonpojos.Group;
import jsonpojos.LogoutResponse;
import jsonpojos.Policy;
import jsonpojos.SimpleDate;
import jsonpojos.User;
import utils.Action;
import utils.JsonUtils;
import utils.MD5Util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import clients.OpenAMClient;

@Path("/rest")
public class PostServices {

	private OpenAMClient client;
	
	public PostServices() {
		client = new OpenAMClient();
	}
	
	@Path("/user/create")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response createUser(
			@FormParam("givenName") String givenName,
			@FormParam("surname") String surname,
			@FormParam("name") String username,
			@FormParam("password") String password,
			@FormParam("mail") String mail,
			@CookieParam("vitalAccessToken") String token) {
		
		int code;
		StringBuilder answer = new StringBuilder();
		User user = new User();
		
		code = 0;
		
		if(client.createUser(givenName, surname, username, password, mail, answer, token)) {
			
			try {
				user = (User) JsonUtils.deserializeJson(answer.toString(), User.class);
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
					.entity(answer.toString())
					.build();
			}
			else if(code >= 500 && code < 600) {
				return Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(answer.toString())
						.build();
			}
			else {
				return Response.ok()
						.entity(answer.toString())
						.build();
			}
		} else {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(answer.toString())
					.build();
		}
		
	}
	
	@Path("/user/delete")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteUser(
			@FormParam("name") String username,
			@CookieParam("vitalAccessToken") String token) {
		
		Boolean result;
		int code;
		StringBuilder answer = new StringBuilder();
		User user = new User();
		
		code = 0;
		
		result = client.deleteUser(username, answer, token);
		
		try {
			user = (User) JsonUtils.deserializeJson(answer.toString(), User.class);
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
		
		if(result) {
			return Response.ok()
				.entity(answer.toString())
				.build();
		} else {
			
			if(code >= 400 && code < 500) {
				return Response.status(Status.BAD_REQUEST)
					.entity(answer.toString())
					.build();
			}
			else {
				return Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(answer.toString())
						.build();
			}
		}
		
	}
	
	@Path("/group/create")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response createGroup(
			@FormParam("name") String name,
			@CookieParam("vitalAccessToken") String token) {
		
		int code;
		StringBuilder answer = new StringBuilder();
		Group group = new Group();
		
		code = 0;
		
		if(client.createGroup(name, answer, token)) {
			
			try {
				group = (Group) JsonUtils.deserializeJson(answer.toString(), Group.class);
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
					.entity(answer.toString())
					.build();
			}
			else if(code >= 500 && code < 600) {
				return Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(answer.toString())
						.build();
			}
			else {
				return Response.ok()
						.entity(answer.toString())
						.build();
			}
		} else {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(answer.toString())
					.build();
		}
		
	}
	
	@Path("/group/delete")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteGroup(
			@FormParam("name") String name,
			@CookieParam("vitalAccessToken") String token) {
		
		Boolean result;
		int code;
		StringBuilder answer = new StringBuilder();
		Group group = new Group();
		
		code = 0;
		
		result = client.deleteGroup(name, answer, token);
		
		try {
			group = (Group) JsonUtils.deserializeJson(answer.toString(), Group.class);
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
		
		if(result) {

			return Response.ok()
				.entity(answer.toString())
				.build();
		} else {
			
			if(code >= 400 && code < 500) {
				return Response.status(Status.BAD_REQUEST)
					.entity(answer.toString())
					.build();
			}
			else {
				return Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(answer.toString())
						.build();
			}
		}
		
	}
	
	@Path("/group/{id}/addUser")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response addUserToGroup(
			@PathParam("id") String groupId,
			@FormParam("user") String username,
			@CookieParam("vitalAccessToken") String token) {
		
		int code;
		StringBuilder answer = new StringBuilder();
		Group group = new Group();
		
		ArrayList<String> usersList = new ArrayList<String>();
		usersList.add(username);
		
		code = 0;
		
		if(client.addUsersToGroup(groupId, usersList, answer, token)) {
			
			try {
				group = (Group) JsonUtils.deserializeJson(answer.toString(), Group.class);
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
					.entity(answer.toString())
					.build();
			}
			else if(code >= 500 && code < 600) {
				return Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(answer.toString())
						.build();
			}
			else {
				return Response.ok()
						.entity(answer.toString())
						.build();
			}
		} else {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(answer.toString())
					.build();
		}
		
	}
	
	@Path("/group/{id}/delUser")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeUserFromGroup(
			@PathParam("id") String groupId,
			@FormParam("user") String username,
			@CookieParam("vitalAccessToken") String token) {
		
		int code;
		StringBuilder answer = new StringBuilder();
		Group group = new Group();
		
		ArrayList<String> usersList = new ArrayList<String>();
		usersList.add(username);
		
		code = 0;
		
		if(client.deleteUsersFromGroup(groupId, usersList, answer, token)) {
			
			try {
				group = (Group) JsonUtils.deserializeJson(answer.toString(), Group.class);
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
					.entity(answer.toString())
					.build();
			}
			else if(code >= 500 && code < 600) {
				return Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(answer.toString())
						.build();
			}
			else {
				return Response.ok()
						.entity(answer.toString())
						.build();
			}
		} else {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(answer.toString())
					.build();
		}
		
	}
	
	@Path("/policy/create")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response createPolicy(
			@FormParam("name") String name,
			@FormParam("description") String description,
			@FormParam("appname") String appname,
			@FormParam("resources[]") ArrayList<String> res,
			@FormParam("groups[]") ArrayList<String> grs,
			@FormParam("actions[DELETE]") Boolean delete,
			@FormParam("actions[GET]") Boolean get,
			@FormParam("actions[HEAD]") Boolean head,
			@FormParam("actions[OPTIONS]") Boolean options,
			@FormParam("actions[PATCH]") Boolean patch,
			@FormParam("actions[POST]") Boolean post,
			@FormParam("actions[PUT]") Boolean put,
			@CookieParam("vitalAccessToken") String token) {
		
		int code;
		StringBuilder answer = new StringBuilder();
		Policy policy = new Policy();
		ArrayList<Action> actions = new ArrayList<Action>();
		Boolean result;
		
		if(delete != null) {
			actions.add(new Action("DELETE", delete.booleanValue()));
		}
		if(get != null) {
			actions.add(new Action("GET", get.booleanValue()));
		}
		if(head != null) {
			actions.add(new Action("HEAD", head.booleanValue()));
		}
		if(options != null) {
			actions.add(new Action("OPTIONS", options.booleanValue()));
		}
		if(patch != null) {
			actions.add(new Action("PATCH", patch.booleanValue()));
		}
		if(post != null) {
			actions.add(new Action("POST", post.booleanValue()));
		}
		if(put != null) {
			actions.add(new Action("PUT", put.booleanValue()));
		}
		
		code = 0;
		
		result = client.createIdentityGroupsPolicy(name, description, actions, res, grs, appname, answer, token);
		
		try {
			policy = (Policy) JsonUtils.deserializeJson(answer.toString(), Policy.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(result) {
			return Response.ok()
				.entity(answer.toString())
				.build();
		} else {
			if(policy.getAdditionalProperties().containsKey("code")) {
				if(policy.getAdditionalProperties().get("code").getClass() == Integer.class) {
					code = (Integer) policy.getAdditionalProperties().get("code");
				}
			}
			if(code >= 400 && code < 500) {
				return Response.status(Status.BAD_REQUEST)
					.entity(answer.toString())
					.build();
			}
			else {
				return Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(answer.toString())
						.build();
			}
		}

	}
	
	
	@Path("/policy/delete")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response deletePolicy(
			@FormParam("name") String name,
			@CookieParam("vitalAccessToken") String token) {
		
		Boolean result;
		int code;
		StringBuilder answer = new StringBuilder();
		Policy policy = new Policy();
		
		code = 0;
		
		result = client.deletePolicy(name, answer, token);
		
		try {
			policy = (Policy) JsonUtils.deserializeJson(answer.toString(), Policy.class);
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
		
		if(result) {
			
			return Response.ok()
				.entity(answer.toString())
				.build();
		} else {
			
			if(code >= 400 && code < 500) {
				return Response.status(Status.BAD_REQUEST)
					.entity(answer.toString())
					.build();
			}
			else {
				return Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(answer.toString())
						.build();
			}
		}
		
	}
	
	@Path("/application/create")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response createApplication(
			@FormParam("name") String name,
			@FormParam("description") String description,
			@FormParam("resources[]") ArrayList<String> res,
			@CookieParam("vitalAccessToken") String token) {
		
		int code;
		StringBuilder answer = new StringBuilder();
		Application application = new Application();
		Boolean result;
		
		code = 0;
		
		result = client.createApplication(name, description, res, answer, token);
		
		try {
			application = (Application) JsonUtils.deserializeJson(answer.toString(), Application.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(result) {
			return Response.ok()
				.entity(answer.toString())
				.build();
		} else {
			if(application.getAdditionalProperties().containsKey("code")) {
				if(application.getAdditionalProperties().get("code").getClass() == Integer.class) {
					code = (Integer) application.getAdditionalProperties().get("code");
				}
			}
			if(code >= 400 && code < 500) {
				return Response.status(Status.BAD_REQUEST)
					.entity(answer.toString())
					.build();
			}
			else {
				return Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(answer.toString())
						.build();
			}
		}

	}
	
	@Path("/application/delete")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteApplication(
			@FormParam("name") String name,
			@CookieParam("vitalAccessToken") String token) {
		
		Boolean result;
		int code;
		StringBuilder answer = new StringBuilder();
		// Change policy with application once we have the class
		Application application = new Application();
		
		code = 0;
		
		result = client.deleteApplication(name, answer, token);
		
		try {
			application = (Application) JsonUtils.deserializeJson(answer.toString(), Application.class);
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
		
		if(result) {
			
			return Response.ok()
				.entity(answer.toString())
				.build();
		} else {
			
			if(code >= 400 && code < 500) {
				return Response.status(Status.BAD_REQUEST)
					.entity(answer.toString())
					.build();
			}
			else {
				return Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(answer.toString())
						.build();
			}
		}
		
	}
	
	@Path("/user/{id}")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateUser(
			@PathParam("id") String userId,
			@FormParam("givenName") String givenName,
			@FormParam("surname") String surname,
			@FormParam("mail") String mail,
			@FormParam("status") String status,
			@CookieParam("vitalAccessToken") String token) {
		
		int code;
		StringBuilder answer = new StringBuilder();
		String userJson = null;
		User user = new User();
		
		code = 0;
		
		if(client.updateUser(userId, givenName, surname, mail, status, answer, token)) {
			
			try {
				user = (User) JsonUtils.deserializeJson(answer.toString(), User.class);
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
					.entity(answer.toString())
					.build();
			}
			else if(code >= 500 && code < 600) {
				return Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(answer.toString())
						.build();
			}
			else {
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
				try {
					userJson = JsonUtils.serializeJson(user);
				} catch (JsonParseException e) {
					e.printStackTrace();
				} catch (JsonMappingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return Response.ok()
						.entity(userJson)
						.build();
			}
		} else {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(answer.toString())
					.build();
		}
		
	}
	
	@Path("/user/changePassword")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response changePassword(
			@CookieParam("vitalAccessToken") String token,
			@FormParam("userpass") String userPass,
			@FormParam("currpass") String currPass) {
		
		ChangePasswordResponse resp;
		String answer;
		int code;
		
		answer = null;
		code = 0;
		resp = client.changePassword(token, userPass, currPass);
		
		try {
			answer = JsonUtils.serializeJson(resp);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(resp.getAdditionalProperties().containsKey("code")) {
			if(resp.getAdditionalProperties().get("code").getClass() == Integer.class) {
				code = (Integer) resp.getAdditionalProperties().get("code");
			}
		}
		if(code >= 400 && code < 500) {
			return Response.status(Status.BAD_REQUEST)
				.entity(answer)
				.build();
		}
		else if(code >= 500 && code < 600) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(answer)
					.build();
		}
		else {
			return Response.ok()
					.entity(answer)
					.build();
		}
		
	}
	
	@Path("/policy/{id}")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response updatePolicy(
			@PathParam("id") String name,
			@FormParam("description") String description,
			@FormParam("active") Boolean active,
			@FormParam("groups[]") ArrayList<String> groups,
			@FormParam("nogr") Boolean nogr,
			@FormParam("resources[]") ArrayList<String> resources,
			@FormParam("nores") Boolean nores,
			@FormParam("actions[DELETE]") Boolean delete,
			@FormParam("actions[GET]") Boolean get,
			@FormParam("actions[HEAD]") Boolean head,
			@FormParam("actions[OPTIONS]") Boolean options,
			@FormParam("actions[PATCH]") Boolean patch,
			@FormParam("actions[POST]") Boolean post,
			@FormParam("actions[PUT]") Boolean put,
			@FormParam("noact") Boolean noact,
			@CookieParam("vitalAccessToken") String token) {
		
		int code;
		StringBuilder answer = new StringBuilder();
		Policy policy = new Policy();
		ArrayList<Action> actions = new ArrayList<Action>();
		boolean res;
		
		if(delete != null) {
			actions.add(new Action("DELETE", delete.booleanValue()));
		}
		if(get != null) {
			actions.add(new Action("GET", get.booleanValue()));
		}
		if(head != null) {
			actions.add(new Action("HEAD", head.booleanValue()));
		}
		if(options != null) {
			actions.add(new Action("OPTIONS", options.booleanValue()));
		}
		if(patch != null) {
			actions.add(new Action("PATCH", patch.booleanValue()));
		}
		if(post != null) {
			actions.add(new Action("POST", post.booleanValue()));
		}
		if(put != null) {
			actions.add(new Action("PUT", put.booleanValue()));
		}
		
		code = 0;
		
		if(client.getPolicy(name, token).getSubject().getType().equals("Identity")) {
			res = client.updatePolicyIdentity(name, description, active, groups, nogr, resources, nores, actions, noact, answer, token);
		} else {
			res = client.updatePolicyAuthenticated(name, description, active, groups, nogr, resources, nores, actions, noact, answer, token);
		}
		if(res) {
			
			try {
				policy = (Policy) JsonUtils.deserializeJson(answer.toString(), Policy.class);
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
					.entity(answer.toString())
					.build();
			}
			else if(code >= 500 && code < 600) {
				return Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(answer.toString())
						.build();
			}
			else {
				return Response.ok()
						.entity(answer.toString())
						.build();
			}
		} else {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(answer.toString())
					.build();
		}
	}
	
	@Path("/application/{id}")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateApplication(
			@PathParam("id") String name,
			@FormParam("description") String description,
			@FormParam("resources[]") ArrayList<String> res,
			@FormParam("nores") Boolean nores,
			@CookieParam("vitalAccessToken") String token) {
		
		int code;
		StringBuilder answer = new StringBuilder();
		Application application = new Application();
		
		code = 0;
		
		if(client.updateApplication(name, description, res, nores, answer, token)) {
			
			try {
				application = (Application) JsonUtils.deserializeJson(answer.toString(), Application.class);
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
					.entity(answer.toString())
					.build();
			}
			else if(code >= 500 && code < 600) {
				return Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(answer.toString())
						.build();
			}
			else {
				return Response.ok()
						.entity(answer.toString())
						.build();
			}
		} else {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(answer.toString())
					.build();
		}
	}
	
	@Path("/authenticate")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response authenticate(
			@FormParam("name") String name,
			@FormParam("password") String password,
			@FormParam("testCookie") boolean testCookie,
			@Context UriInfo uri) {
		
		Authenticate auth;
		String answer;
		int code;
		
		answer = null;
		code = 0;
		auth = client.authenticate(name, password);
		
		try {
			answer = JsonUtils.serializeJson(auth);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(auth.getAdditionalProperties().containsKey("code")) {
			if(auth.getAdditionalProperties().get("code").getClass() == Integer.class) {
				code = (Integer) auth.getAdditionalProperties().get("code");
			}
		}
		if(code >= 400 && code < 500) {
			return Response.status(Status.BAD_REQUEST)
				.entity(answer)
				.build();
		}
		else if(code >= 500 && code < 600) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(answer)
					.build();
		}
		else {
			Cookie ck;
			
			// Let's give back some info about the user
			User user = client.getUser(name, auth.getTokenId()); // get the info
			AuthenticationResponse resp = new AuthenticationResponse();
			resp.setUid(name); // always give back the login username
			List<String> gn = null;
			gn = user.getGivenName();
			if((gn != null) && (!gn.isEmpty())) { // send back the first name if available
				resp.setName(gn.get(0));
			} else {
				gn = user.getGivenname();
				if((gn != null) && (!gn.isEmpty())) {
					resp.setName(gn.get(0));
				}
			}
			List<String> ln = null;
			ln = user.getSn();
			if((gn != null) && (!gn.isEmpty()) && (ln != null) && (!ln.isEmpty())) { // send back the full name if available
				resp.setFullname(gn.get(0) + " " + ln.get(0)); // composed by first name + last name
			} else { // otherwise use common name, but it is not the full name for sure
				List<String> cn = null;
				cn = user.getCn();
				if((cn != null) && (!cn.isEmpty())) {
					resp.setFullname(cn.get(0));
				}
			}
			
			List<String> mail = null;
			mail = user.getMail();
			if((mail != null) && (!mail.isEmpty())) {
				resp.setMailhash(MD5Util.md5Hex(mail.get(0)));
			}
			
			List<String> time = user.getCreateTimestamp();
			SimpleDate date = new SimpleDate();
			if((time != null) && (!time.isEmpty())) {
				date.setYear(time.get(0).substring(0, 4));
				int m = Integer.parseInt(time.get(0).substring(4, 6));
				date.setMonth(new DateFormatSymbols().getMonths()[m - 1]);
				date.setDay(time.get(0).substring(6, 8));
				resp.setCreation(date);
			}

			try {
				answer = JsonUtils.serializeJson(resp);
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			// Will all services be over HTTPS? Will any client side script need to access the cookies?
			// If the answer is (Yes, No) then we can keep secure and HttpOnly flags
			String domain = uri.getBaseUri().getHost();
			Pattern pattern = Pattern.compile("^[^.]*(..*)$");
			Matcher matcher = pattern.matcher(domain);
			if (matcher.find()) {
			    domain = matcher.group(1);
			}

			if(!testCookie) {
				ck = new Cookie(client.getSSOCookieName(), auth.getTokenId(), "/", domain);
				return Response.ok()
						.entity(answer)
						.header("SET-COOKIE", ck.toString() + "; secure" + "; HttpOnly")
						.build();
			} else {
				ck = new Cookie(client.getTestCookieName(), auth.getTokenId(), "/", domain);
				return Response.ok()
						.entity(answer)
						.header("SET-COOKIE", ck.toString() + "; secure" + "; HttpOnly")
						.build();
			}
		}
		
	}
	
	@Path("/logout")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response logout(
			@CookieParam("vitalTestToken") String testToken,
			@CookieParam("vitalAccessToken") String vitalToken,
			@FormParam("testCookie") boolean testCookie,
			@Context UriInfo uri) {
		
		LogoutResponse resp;
		String answer;
		int code;
		
		answer = null;
		code = 0;
		if(testCookie) {
			resp = client.logout(testToken);
		}
		else {
			resp = client.logout(vitalToken);
		}
		
		try {
			answer = JsonUtils.serializeJson(resp);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(resp.getAdditionalProperties().containsKey("code")) {
			if(resp.getAdditionalProperties().get("code").getClass() == Integer.class) {
				code = (Integer) resp.getAdditionalProperties().get("code");
			}
		}
		if(code >= 400 && code < 500) {
			return Response.status(Status.BAD_REQUEST)
				.entity(answer)
				.build();
		}
		else if(code >= 500 && code < 600) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(answer)
					.build();
		}
		else {
			Cookie ck;
			
			String domain = uri.getBaseUri().getHost();
			Pattern pattern = Pattern.compile("^[^.]*(..*)$");
			Matcher matcher = pattern.matcher(domain);
			if (matcher.find()) {
			    domain = matcher.group(1);
			}
			
			if(!testCookie) {
				ck = new Cookie(client.getSSOCookieName(), "", "/", domain);
				return Response.ok()
						.entity(answer)
						.header("SET-COOKIE", ck.toString() + "; secure" + "; HttpOnly")
						.build();
			} else {
				ck = new Cookie(client.getTestCookieName(), "", "/", domain);
				return Response.ok()
						.entity(answer)
						.header("SET-COOKIE", ck.toString() + "; secure" + "; HttpOnly")
						.build();
			}
		}
	}
	
	@Path("/evaluate")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response evaluate(
			@CookieParam("vitalAccessToken") String vitalToken,
			@CookieParam("vitalTestToken") String testToken,
			@FormParam("testCookie") boolean testCookie,
			@FormParam("resources[]") ArrayList<String> res) {
		
		int code;
		String tokenPerformer, tokenUser;
		StringBuilder answer = new StringBuilder();
		DecisionArray resp = new DecisionArray();
		
		code = 0;
		if(testCookie) {
			tokenPerformer = vitalToken;
			tokenUser = testToken;
		} else {
			tokenPerformer = testToken;
			tokenUser = vitalToken;
		}
		
		if(client.evaluate(tokenUser, res, answer, tokenPerformer)) {
			
			try {
				resp = (DecisionArray) JsonUtils.deserializeJson(answer.toString(), DecisionArray.class);
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if(resp.getAdditionalProperties().containsKey("code")) {
				if(resp.getAdditionalProperties().get("code").getClass() == Integer.class) {
					code = (Integer) resp.getAdditionalProperties().get("code");
				}
			}
			if(code >= 400 && code < 500) {
				return Response.status(Status.BAD_REQUEST)
					.entity(answer.toString())
					.build();
			}
			else if(code >= 500 && code < 600) {
				return Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(answer.toString())
						.build();
			}
			else {
				return Response.ok()
						.entity(answer.toString())
						.build();
			}
		} else {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(answer.toString())
					.build();
		}
	}
}

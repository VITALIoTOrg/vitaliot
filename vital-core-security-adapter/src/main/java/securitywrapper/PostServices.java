package securitywrapper;

import java.io.IOException;
import java.util.ArrayList;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import jsonpojos.Application;
import jsonpojos.Authenticate;
import jsonpojos.DecisionArray;
import jsonpojos.Group;
import jsonpojos.LogoutResponse;
import jsonpojos.Policy;
import jsonpojos.User;
import utils.Action;
import utils.JsonUtils;

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
            @HeaderParam("TokenId") String token) {
		
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
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
			}
			else if(code >= 500 && code < 600) {
				return Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(answer.toString())
						.header("Access-Control-Allow-Origin", "*")
						.header("Access-Control-Allow-Credentials", "true")
						.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
						.build();
			}
			else {
				return Response.ok()
						.entity(answer.toString())
						.header("Access-Control-Allow-Origin", "*")
						.header("Access-Control-Allow-Credentials", "true")
						.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
						.build();
			}
		} else {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(answer.toString())
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
		}
		
	}
	
	@Path("/user/delete")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteUser(
			@FormParam("name") String username,
            @HeaderParam("TokenId") String token) {
		
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
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
				.build();
		} else {
			
			if(code >= 400 && code < 500) {
				return Response.status(Status.BAD_REQUEST)
					.entity(answer.toString())
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
			}
			else {
				return Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(answer.toString())
						.header("Access-Control-Allow-Origin", "*")
						.header("Access-Control-Allow-Credentials", "true")
						.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
						.build();
			}
		}
		
	}
	
	@Path("/group/create")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response createGroup(
			@FormParam("name") String name,
            @HeaderParam("TokenId") String token) {
		
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
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
			}
			else if(code >= 500 && code < 600) {
				return Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(answer.toString())
						.header("Access-Control-Allow-Origin", "*")
						.header("Access-Control-Allow-Credentials", "true")
						.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
						.build();
			}
			else {
				return Response.ok()
						.entity(answer.toString())
						.header("Access-Control-Allow-Origin", "*")
						.header("Access-Control-Allow-Credentials", "true")
						.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
						.build();
			}
		} else {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(answer.toString())
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
		}
		
	}
	
	@Path("/group/delete")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteGroup(
			@FormParam("name") String name,
            @HeaderParam("TokenId") String token) {
		
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
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
				.build();
		} else {
			
			if(code >= 400 && code < 500) {
				return Response.status(Status.BAD_REQUEST)
					.entity(answer.toString())
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
			}
			else {
				return Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(answer.toString())
						.header("Access-Control-Allow-Origin", "*")
						.header("Access-Control-Allow-Credentials", "true")
						.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
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
            @HeaderParam("TokenId") String token) {
		
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
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
			}
			else if(code >= 500 && code < 600) {
				return Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(answer.toString())
						.header("Access-Control-Allow-Origin", "*")
						.header("Access-Control-Allow-Credentials", "true")
						.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
						.build();
			}
			else {
				return Response.ok()
						.entity(answer.toString())
						.header("Access-Control-Allow-Origin", "*")
						.header("Access-Control-Allow-Credentials", "true")
						.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
						.build();
			}
		} else {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(answer.toString())
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
		}
		
	}
	
	@Path("/group/{id}/delUser")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeUserFromGroup(
			@PathParam("id") String groupId,
			@FormParam("user") String username,
            @HeaderParam("TokenId") String token) {
		
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
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
			}
			else if(code >= 500 && code < 600) {
				return Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(answer.toString())
						.header("Access-Control-Allow-Origin", "*")
						.header("Access-Control-Allow-Credentials", "true")
						.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
						.build();
			}
			else {
				return Response.ok()
						.entity(answer.toString())
						.header("Access-Control-Allow-Origin", "*")
						.header("Access-Control-Allow-Credentials", "true")
						.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
						.build();
			}
		} else {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(answer.toString())
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
		}
		
	}
	
	@Path("/policy/create")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response createPolicy(
			@FormParam("name") String name,
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
            @HeaderParam("TokenId") String token) {
		
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
		
		result = client.createIdentityGroupsPolicy(name, actions, res, grs, appname, answer, token);
		
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
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
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
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
			}
			else {
				return Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(answer.toString())
						.header("Access-Control-Allow-Origin", "*")
						.header("Access-Control-Allow-Credentials", "true")
						.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
						.build();
			}
		}

	}
	
	
	@Path("/policy/delete")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response deletePolicy(
			@FormParam("name") String name,
            @HeaderParam("TokenId") String token) {
		
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
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
				.build();
		} else {
			
			if(code >= 400 && code < 500) {
				return Response.status(Status.BAD_REQUEST)
					.entity(answer.toString())
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
			}
			else {
				return Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(answer.toString())
						.header("Access-Control-Allow-Origin", "*")
						.header("Access-Control-Allow-Credentials", "true")
						.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
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
            @HeaderParam("TokenId") String token) {
		
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
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
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
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
			}
			else {
				return Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(answer.toString())
						.header("Access-Control-Allow-Origin", "*")
						.header("Access-Control-Allow-Credentials", "true")
						.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
						.build();
			}
		}

	}
	
	@Path("/application/delete")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteApplication(
			@FormParam("name") String name,
            @HeaderParam("TokenId") String token) {
		
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
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
				.build();
		} else {
			
			if(code >= 400 && code < 500) {
				return Response.status(Status.BAD_REQUEST)
					.entity(answer.toString())
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
			}
			else {
				return Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(answer.toString())
						.header("Access-Control-Allow-Origin", "*")
						.header("Access-Control-Allow-Credentials", "true")
						.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
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
            @HeaderParam("TokenId") String token) {
		
		int code;
		StringBuilder answer = new StringBuilder();
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
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
			}
			else if(code >= 500 && code < 600) {
				return Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(answer.toString())
						.header("Access-Control-Allow-Origin", "*")
						.header("Access-Control-Allow-Credentials", "true")
						.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
						.build();
			}
			else {
				return Response.ok()
						.entity(answer.toString())
						.header("Access-Control-Allow-Origin", "*")
						.header("Access-Control-Allow-Credentials", "true")
						.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
						.build();
			}
		} else {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(answer.toString())
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
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
            @HeaderParam("TokenId") String token) {
		
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
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
			}
			else if(code >= 500 && code < 600) {
				return Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(answer.toString())
						.header("Access-Control-Allow-Origin", "*")
						.header("Access-Control-Allow-Credentials", "true")
						.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
						.build();
			}
			else {
				return Response.ok()
						.entity(answer.toString())
						.header("Access-Control-Allow-Origin", "*")
						.header("Access-Control-Allow-Credentials", "true")
						.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
						.build();
			}
		} else {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(answer.toString())
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
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
            @HeaderParam("TokenId") String token) {
		
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
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
			}
			else if(code >= 500 && code < 600) {
				return Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(answer.toString())
						.header("Access-Control-Allow-Origin", "*")
						.header("Access-Control-Allow-Credentials", "true")
						.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
						.build();
			}
			else {
				return Response.ok()
						.entity(answer.toString())
						.header("Access-Control-Allow-Origin", "*")
						.header("Access-Control-Allow-Credentials", "true")
						.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
						.build();
			}
		} else {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(answer.toString())
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
		}
	}
	
	@Path("/authenticate")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response authenticate(
			@FormParam("name") String name,
			@FormParam("password") String password) {
		
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
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
				.build();
		}
		else if(code >= 500 && code < 600) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(answer)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
		}
		else {
			return Response.ok()
					.entity(answer)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
		}
		
	}
	
	@Path("/logout")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response logout(
			@HeaderParam("TokenId") String token) {
		
		LogoutResponse resp;
		String answer;
		int code;
		
		answer = null;
		code = 0;
		resp = client.logout(token);
		
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
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
				.build();
		}
		else if(code >= 500 && code < 600) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(answer)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
		}
		else {
			return Response.ok()
					.entity(answer)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
		}
		
	}
	
	@Path("/evaluate")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response evaluate(
			@FormParam("token") String token,
			@FormParam("resources[]") ArrayList<String> res,
            @HeaderParam("TokenId") String tokenUser) {
		
		int code;
		StringBuilder answer = new StringBuilder();
		DecisionArray resp = new DecisionArray();
		
		code = 0;
		
		if(client.evaluate(token, res, answer, tokenUser)) {
			
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
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
			}
			else if(code >= 500 && code < 600) {
				return Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(answer.toString())
						.header("Access-Control-Allow-Origin", "*")
						.header("Access-Control-Allow-Credentials", "true")
						.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
						.build();
			}
			else {
				return Response.ok()
						.entity(answer.toString())
						.header("Access-Control-Allow-Origin", "*")
						.header("Access-Control-Allow-Credentials", "true")
						.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
						.build();
			}
		} else {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(answer.toString())
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
		}
		
	}
	
}


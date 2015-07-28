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

import jsonpojos.Application;
import jsonpojos.Group;
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
			@FormParam("mail") String mail) {
		
		int code;
		StringBuilder answer = new StringBuilder();
		User user = new User();
		
		code = 0;
		
		if(client.createUser(givenName, surname, username, password, mail, answer)) {
			
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
			@FormParam("name") String username) {
		
		Boolean result;
		int code;
		StringBuilder answer = new StringBuilder();
		User user = new User();
		
		code = 0;
		
		result = client.deleteUser(username, answer);
		
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
			@FormParam("name") String name) {
		
		int code;
		StringBuilder answer = new StringBuilder();
		Group group = new Group();
		
		code = 0;
		
		if(client.createGroup(name, answer)) {
			
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
			@FormParam("name") String name) {
		
		Boolean result;
		int code;
		StringBuilder answer = new StringBuilder();
		Group group = new Group();
		
		code = 0;
		
		result = client.deleteGroup(name, answer);
		
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
			@FormParam("user") String username) {
		
		int code;
		StringBuilder answer = new StringBuilder();
		Group group = new Group();
		
		ArrayList<String> usersList = new ArrayList<String>();
		usersList.add(username);
		
		code = 0;
		
		if(client.addUsersToGroup(groupId, usersList, answer)) {
			
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
			@FormParam("user") String username) {
		
		int code;
		StringBuilder answer = new StringBuilder();
		Group group = new Group();
		
		ArrayList<String> usersList = new ArrayList<String>();
		usersList.add(username);
		
		code = 0;
		
		if(client.deleteUsersFromGroup(groupId, usersList, answer)) {
			
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
			@FormParam("groups[]") ArrayList<String> grs) {
		
		int code;
		StringBuilder answer = new StringBuilder();
		Policy policy = new Policy();
		ArrayList<Action> actions = new ArrayList<Action>();
		Boolean result;
		
		code = 0;
		
		result = client.createIdentityGroupsPolicy(name, actions, res, grs, appname, answer);
		
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
			@FormParam("name") String name) {
		
		Boolean result;
		int code;
		StringBuilder answer = new StringBuilder();
		Policy policy = new Policy();
		
		code = 0;
		
		result = client.deletePolicy(name, answer);
		
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
			@FormParam("resources[]") ArrayList<String> res) {
		
		int code;
		StringBuilder answer = new StringBuilder();
		Application application = new Application();
		Boolean result;
		
		code = 0;
		
		result = client.createApplication(name, description, res, answer);
		
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
			@FormParam("name") String name) {
		
		Boolean result;
		int code;
		StringBuilder answer = new StringBuilder();
		// Change policy with application once we have the class
		Application application = new Application();
		
		code = 0;
		
		result = client.deleteApplication(name, answer);
		
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
			@FormParam("status") String status) {
		
		int code;
		StringBuilder answer = new StringBuilder();
		User user = new User();
		
		code = 0;
		
		if(client.updateUser(userId, givenName, surname, mail, status, answer)) {
			
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
			@FormParam("nores") Boolean nores) {
		
		int code;
		StringBuilder answer = new StringBuilder();
		Policy policy = new Policy();
		boolean res;
		
		code = 0;
		
		System.out.println(client.getPolicy(name).getSubject().getType());
		
		if(client.getPolicy(name).getSubject().getType().equals("Identity")) {
			res = client.updatePolicyIdentity(name, description, active, groups, nogr, resources, nores, answer);
			System.out.println("Updating Identity");
		} else {
			res = client.updatePolicyAuthenticated(name, description, active, groups, nogr, resources, nores, answer);
			System.out.println("Updating Authenticated");
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
	
}

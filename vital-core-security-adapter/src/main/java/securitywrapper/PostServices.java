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
		String answer;
		
		answer = null;
		
		if(client.createUser(givenName, surname, username, password, mail)) {
			try {
				answer = JsonUtils.serializeJson(client.getUser(username));
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
		} else {
			try {
				answer = JsonUtils.serializeJson(client.getUser(username));
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return Response.status(Status.BAD_REQUEST)
					.entity(answer)
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
		String answer;
		
		answer = null;
		
		if(client.deleteUser(username)) {
			try {
				answer = JsonUtils.serializeJson(client.getUser(username));
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
		} else {
			try {
				answer = JsonUtils.serializeJson(client.getUser(username));
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return Response.status(Status.BAD_REQUEST)
					.entity(answer)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
		}
	}
	
	@Path("/group/create")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response createGroup(
			@FormParam("name") String name) {
		String answer;
		
		answer = null;
		
		if(client.createGroup(name)) {
			try {
				answer = JsonUtils.serializeJson(client.getGroup(name));
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
		} else {
			try {
				answer = JsonUtils.serializeJson(client.getGroup(name));
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return Response.status(Status.BAD_REQUEST)
					.entity(answer)
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
		String answer;
		
		answer = null;
		
		if(client.deleteGroup(name)) {
			try {
				answer = JsonUtils.serializeJson(client.getGroup(name));
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
		} else {
			try {
				answer = JsonUtils.serializeJson(client.getGroup(name));
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return Response.status(Status.BAD_REQUEST)
					.entity(answer)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
		}
	}
	
	@Path("/group/{id}/addUser")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response addUserToGroup(
			@PathParam("id") String groupId,
			@FormParam("user") String username) {
		String answer;
		
		answer = null;
		
		ArrayList<String> usersList = new ArrayList<String>();
		usersList.add(username);
		
		if(client.addUsersToGroup(groupId, usersList)) {
			try {
				answer = JsonUtils.serializeJson(client.getGroup(groupId));
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
		} else {
			try {
				answer = JsonUtils.serializeJson(client.getGroup(groupId));
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return Response.status(Status.BAD_REQUEST)
					.entity(answer)
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
		String answer;
		
		answer = null;
		
		ArrayList<String> usersList = new ArrayList<String>();
		usersList.add(username);
		
		if(client.deleteUsersFromGroup(groupId, usersList)) {
			try {
				answer = JsonUtils.serializeJson(client.getGroup(groupId));
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
		} else {
			try {
				answer = JsonUtils.serializeJson(client.getGroup(groupId));
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return Response.status(Status.BAD_REQUEST)
					.entity(answer)
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
			@FormParam("resources[]") ArrayList<String> res,
			@FormParam("groups[]") ArrayList<String> grs) {
		String answer;
		
		answer = null;
		ArrayList<Action> actions = new ArrayList<Action>();
		
		if(client.createIdentityGroupsPolicy(name, actions, res, grs)) {
			try {
				answer = JsonUtils.serializeJson(client.getPolicy(name));
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
		} else {
			try {
				answer = JsonUtils.serializeJson(client.getPolicy(name));
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return Response.status(Status.BAD_REQUEST)
					.entity(answer)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
		}
	}
	
	
	@Path("/policy/delete")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response deletePolicy(
			@FormParam("name") String name) {
		String answer;
		
		answer = null;
		
		if(client.deletePolicy(name)) {
			try {
				answer = JsonUtils.serializeJson(client.getPolicy(name));
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
		} else {
			try {
				answer = JsonUtils.serializeJson(client.getPolicy(name));
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return Response.status(Status.BAD_REQUEST)
					.entity(answer)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
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
		String answer;
		
		answer = null;
		
		if(client.updateUser(userId, givenName, surname, mail, status)) {
			try {
				answer = JsonUtils.serializeJson(client.getUser(userId));
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
		} else {
			try {
				answer = JsonUtils.serializeJson(client.getUser(userId));
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return Response.status(Status.BAD_REQUEST)
					.entity(answer)
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
			@FormParam("active") Boolean active) {
		StringBuilder answer = new StringBuilder();
		
		if(client.updatePolicy(name, description, active, answer)) {
			return Response.ok()
					.entity(answer.toString())
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
		} else {
			return Response.status(Status.BAD_REQUEST)
					.entity(answer.toString())
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
		}
	}
	
}

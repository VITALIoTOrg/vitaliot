package securitywrapper;

import java.io.IOException;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.FormParam;

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
			@FormParam("name") String username,
			@FormParam("password") String password,
			@FormParam("mail") String mail) {
		String answer;
		
		answer = null;
		
		if(client.createUser(username, password, mail)) {
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
			@FormParam("name") String username) {
		String answer;
		
		answer = null;
		
		if(client.createGroup(username)) {
			try {
				answer = JsonUtils.serializeJson(client.getGroup(username));
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
				answer = JsonUtils.serializeJson(client.getGroup(username));
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
			@FormParam("name") String username) {
		String answer;
		
		answer = null;
		
		if(client.deleteGroup(username)) {
			try {
				answer = JsonUtils.serializeJson(client.getGroup(username));
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
				answer = JsonUtils.serializeJson(client.getGroup(username));
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
	
	@Path("/group/addUser")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public String addUserToGroup() {
		return null;
	}
	
	@Path("/group/delUser")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public String removeUserToGroup() {
		return null;
	}
	
	@Path("/policy/create")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public String createPolicy() {
		return null;
	}
	
	
	@Path("/policy/delete")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public String deletePolicy() {
		return null;
	}
	
}

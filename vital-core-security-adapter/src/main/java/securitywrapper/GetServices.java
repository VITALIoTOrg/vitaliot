package securitywrapper;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import jsonpojos.User;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import utils.JsonUtils;
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
	public Response getUser(@PathParam("id") String userId){
		
		String answer;
		
		answer = null;
		
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
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
				.build();
	}
	
	@Path("/group/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getGroup(@PathParam("id") String groupId) {
		
		client.getGroup(groupId);
		
		return null;
	}
	
	@Path("/policy/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getPolicy(@PathParam("id") String policyId) {
		
		client.getPolicy(policyId);
		
		return null;
	}
	
	@Path("/users")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUsers() {
		
		String answer;
		
		answer = null;
		
		try {
			answer = JsonUtils.serializeJson(client.getUsers());
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
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
				.build();
	}
	
	@Path("/groups")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getGroups() {
		
		String answer;
		
		answer = null;
		
		try {
			answer = JsonUtils.serializeJson(client.getGroups());
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
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
				.build();
	}
	
	@Path("/policies")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPolicies() {
		
		String answer;
		
		answer = null;
		
		try {
			answer = JsonUtils.serializeJson(client.getPolicies());
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
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
				.build();
	}
	
}
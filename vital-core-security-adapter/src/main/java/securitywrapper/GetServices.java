package securitywrapper;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
	public String getUser(@PathParam("id") String userId){
		
		client.getUser(userId);
		
		
		return null;
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
	public String getUsers() {
		
		client.getUsers();
		
		return null;
	}
	
	@Path("/groups")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getGroups() {
		
		client.getGroups();
		
		return null;
	}
	
	@Path("/policies")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getPolicies() {
		
		client.getPolicies();
		
		return null;
	}
	
}

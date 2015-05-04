package securitywrapper;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
	public String createUser() {
		return null;
	}
	
	@Path("/user/delete")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteUser() {
		return null;
	}
	
	@Path("/group/create")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public String createGroup() {
		return null;
	}
	
	@Path("/group/delete")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteGroup() {
		return null;
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

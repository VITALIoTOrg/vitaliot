package securitywrapper;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import utils.Action;
import clients.OpenAMClient;


@Path("/test")
public class TestSerives implements Serializable {

	
	@Path("/fool")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String foo() {
		OpenAMClient test = new OpenAMClient();
		
		try {
			//test.getUsers();
			//test.getGroups();
			//test.getPolicies();
			//test.getUser("user");
			//test.getGroup("test");
			//test.getPolicy("VitalPolicy");
			//test.createUser("javauser", "javapwd123456", "java@mail.it");
			//test.createGroup("javaGroup");
			//test.deleteUser("javauser");
			//test.deleteGroup("javaGroup");
			//test.deletePolicy("TestPolicy");
			
			
			//ArrayList<Action> actions = new ArrayList<Action>();
			//ArrayList<String> resources = new ArrayList<String>();
			ArrayList<String> users = new ArrayList<String>();
			//ArrayList<String> groups = new ArrayList<String>();
			
			users.add("user0");
			//users.add("VitalUser");
			
			//groups.add("test");
			//groups.add("test2");
			
			//resources.add("*://*:*/*?*");
			//resources.add("*://*:*/*");
			
			//Action a = new Action("GET",true);
			//actions.add(a);
			
			//a = new Action("POST",false);
			//actions.add(a);
			
			//a = new Action("PATCH",true);
			//actions.add(a);
			
			//a = new Action("DELETE",true);
			//actions.add(a);
			
			//test.createAuthenticatedPolicy("PolicyFromJavaWrapper", actions, resources);
			//test.createIdentityUsersPolicy("IdentityPolicyFromWrapper", actions, resources, users);
			//test.createIdentityGroupsPolicy("IdentityGroupPolicyFromWrapper", actions, resources, groups);
				
			//test.addUsersToGroup("javaGroup", users);
			//test.deleteUsersFromGroup("javaGroup", users);
			
			//test.userIsInGroup("user1", "test");
			test.getUserIdFromToken("AQIC5wM2LY4SfcxQMjAjc7phwFv27cS9lT4i0jpj9MSiKxo.*AAJTSQACMDEAAlNLABQtNjAyNTMwNDM1NTM5MzI1NDY2Mg..*");
			
			
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		
		return "fooool!!";
	}
	
	
}

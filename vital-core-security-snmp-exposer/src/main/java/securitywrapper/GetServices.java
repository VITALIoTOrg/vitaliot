package securitywrapper;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import jsonpojos.Monitor;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import utils.JsonUtils;
import clients.OpenAMClient;

@Path("")
public class GetServices {
	
	private OpenAMClient client;
	
	public GetServices() {
		client = new OpenAMClient();
	}
	
	@Path("/openam_stats")
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
			if(value.contains("message")) {
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
			if(value.contains("message")) {
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
			if(value.contains("message")) {
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
			if(value.contains("message")) {
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
			if(value.contains("message")) {
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
			if(value.contains("message")) {
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
					.build();
		}
		else {
			answer = value;
			return Response.status(Status.BAD_REQUEST)
					.entity(answer)
					.build();
		}
		
	}
		
}

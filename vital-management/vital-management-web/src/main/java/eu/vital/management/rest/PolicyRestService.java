/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.vital.management.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.Arrays;

import eu.vital.management.security.SecurityService;
import eu.vital.management.rest.proxy.ProxyRestService;
import eu.vital.management.util.VitalConfiguration;

import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

@Path("/policy")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class PolicyRestService {

        
    private static Logger logger = Logger.getLogger(PolicyRestService.class.getName());

    @Inject
    VitalConfiguration vitalConfiguration;
    
    @Inject
    SecurityService securityService;

    public String getTestCookieName() {
        return vitalConfiguration.getProperty("vital-management.security.alt-token", "vitalTestToken");
    }
    
    public String getCookieName() {
        return vitalConfiguration.getProperty("vital-management.security.sso-token", "vitalAccessToken");
    }
    
    private Cookie readAuthCookie(@Context HttpHeaders hh) {
            Map<String, Cookie> pathParams = hh.getCookies();
            return pathParams.get(getCookieName());
    }


@Path("/list/{name}")
    @GET
//    @Produces("text/xml")
    @Produces(MediaType.APPLICATION_JSON)

    public Response getPolicies(@Context HttpHeaders hh,@PathParam("name") String name) {

	String resource = null;
        Client client = ClientBuilder.newClient();

        // Check if cookie is valid
        Cookie authCookie = readAuthCookie(hh);
        if (authCookie == null) {
                return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        JsonNode user = securityService.getLoggedOnUser(authCookie.getValue());
        if (user == null) {
                return Response.status(Response.Status.UNAUTHORIZED).build();
        }

 	JsonNode gotPolicies = null;
            try {
                    gotPolicies = client
                                    .target(securityService.getSecurityProxyUrl() + "/policies")
                                    .request(MediaType.APPLICATION_JSON_TYPE)
                                    .cookie(authCookie)
                                    .accept(MediaType.APPLICATION_JSON_TYPE)
                                    .get(JsonNode.class);
            } catch (WebApplicationException e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

            } finally {
                    client.close();
            }

    

        String groupName = name;
        String resourceValue = resource;
        ArrayNode policies = (ArrayNode) gotPolicies.get("result");
	String answer="",answer_header="",answer_found="[",sep="",init="";



            for (JsonNode policy : policies) {
                ArrayNode policyResources = (ArrayNode) policy.get("resources");

                JsonNode action = (JsonNode) policy.get("actionValues");
		String enable="";	
                ArrayList<String> actionEnableiList = new ArrayList<String>();
		actionEnableiList.add("POST");
		actionEnableiList.add("PATCH");
		actionEnableiList.add("GET");
		actionEnableiList.add("DELETE");
		actionEnableiList.add("OPTIONS");
		actionEnableiList.add("PUT");
		actionEnableiList.add("HEAD");
		actionEnableiList.add("RETRIVE");
		actionEnableiList.add("STORE");

		for (String actionEnable : actionEnableiList){
			if (action.get(actionEnable)!=null) {
                		boolean en = action.get(actionEnable).asBoolean();
				if (en && ((!enable.equals("") && enable.equals("true")) || enable.equals("")))
					enable="true";
				else
				if (!en && ((!enable.equals("") && enable.equals("false")) || enable.equals("")))
					enable="false";
				else
					enable="misc";
			}
		}
		
                boolean resourceFound = false;

                JsonNode subject = policy.get("subject");
                ArrayNode subjects = (ArrayNode) subject.get("subjectValues");
                String suYbjectType = subject.get("type").textValue();
                String policyName = policy.get("name").textValue();
                String policyDescription = policy.get("description").textValue();
		answer_header=init+"{\"id\":\""+policyName+"\",";
                for(JsonNode policyResource : policyResources){
		    sep="\"resources\":[";
		    if(resourceFound){
			answer_header="";
			sep=",";
                     }
                    for(JsonNode groupResource : subjects){
                            String groupResourceName = groupResource.textValue();
                            String[] groupIdString = groupResourceName.split(",");
                            String groupId = groupIdString[0].split("=")[1];
                    	    if(groupId.equals(name)){
				answer=answer_header+sep+answer;
				answer=answer+"{\"name\":"+policyResource+",";
				answer=answer+" \"enabled\":\""+enable+"\"}";
                        	resourceFound = true;
				answer_header="";
				if(resourceFound){
					answer_found=answer_found+answer+"";	
					answer_header="";
                		}	
                    	    }	
                    	}
		answer="";
                }
	        if(resourceFound){
			answer_found=answer_found+"]}";	
			init=",";
                }	
		resourceFound=false;
            }
	answer_found=answer_found+"]";


	return Response.ok()
                .entity(answer_found)
                .build();


  }


    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createService(
            @Context HttpHeaders hh, 
            @FormParam("name") String name, 
            @FormParam("resource") String resource) throws Exception {

        // Connect to Security Server
        Client client = ClientBuilder.newClient();
        Client clientBis = ClientBuilder.newClient();

        
        // Check if cookie is valid
        Cookie authCookie = readAuthCookie(hh);
        if (authCookie == null) {
                return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        JsonNode user = securityService.getLoggedOnUser(authCookie.getValue());
        if (user == null) {
                return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        String groupName = name + "_group";
        String policyName = name + "_policy";
        
        Form groupForm = new Form();
        groupForm.param("name", groupName);
        
        Form policyForm = new Form();
        policyForm.param("resources[]", resource);
        policyForm.param("appname", "iPlanetAMWebAgentService");
        policyForm.param("groups[]", groupName);
        policyForm.param("name", policyName);
        policyForm.param("description", policyName + " description");
        policyForm.param("actions[POST]", "true");
        policyForm.param("actions[GET]", "true");
        policyForm.param("actions[PUT]", "true");
        policyForm.param("actions[DELETE]", "true");

        
        JsonNode groupCreatedResponse = null;
        JsonNode policyCreatedResponse = null;
        try {
                groupCreatedResponse = client
                                .target(securityService.getSecurityProxyUrl() + "/group/create")
                                .request(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
                                .cookie(authCookie)
                                .accept(MediaType.APPLICATION_JSON_TYPE)
                                .post(Entity.entity(groupForm, MediaType.APPLICATION_FORM_URLENCODED_TYPE), JsonNode.class);                
            
                policyCreatedResponse = clientBis
                                .target(securityService.getSecurityProxyUrl() + "/policy/create")
                                .request(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
                                .cookie(authCookie)
                                .accept(MediaType.APPLICATION_JSON_TYPE)
                                .post(Entity.entity(policyForm, MediaType.APPLICATION_FORM_URLENCODED_TYPE), JsonNode.class);                
        } catch (WebApplicationException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } finally {
                client.close(); 
                clientBis.close();
                return Response.ok(policyCreatedResponse).build();
        }
    }
    
    @POST
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateServiceAccess(
            @Context HttpHeaders hh, 
            JsonNode data
    ) throws Exception {

        // Connect to Security Server
        Client client = ClientBuilder.newClient();
        
        // Check if cookie is valid
        Cookie authCookie = readAuthCookie(hh);
        if (authCookie == null) {
                return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        JsonNode user = securityService.getLoggedOnUser(authCookie.getValue());
        if (user == null) {
                return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        /*
        
        {
            "_id" : "gruppo",
            "id" : "gruppo",
            "resources" : [
                {
                    "name" : "https://vital-integration.atosresearch.eu:8280/cep",
                    "enabled" : true
                },
                {
                    "name" : "https://vital-integration.atosresearch.eu:8280/hireplyppi",
                    "enabled" : false
                }
            ]
        }
        
        */
        
        JsonNode gotPolicies = null;
            try {
                    gotPolicies = client
                                    .target(securityService.getSecurityProxyUrl() + "/policies")
                                    .request(MediaType.APPLICATION_JSON_TYPE)
                                    .cookie(authCookie)
                                    .accept(MediaType.APPLICATION_JSON_TYPE)
                                    .get(JsonNode.class);
            } catch (WebApplicationException e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            } finally {
                    client.close();
            }
        
        String groupName = data.get("id").textValue();
        ArrayNode resources = (ArrayNode) data.get("resources");
        ArrayNode policies = (ArrayNode) gotPolicies.get("result");
        
        
        for (JsonNode resource : resources) {
            String resourceValue = resource.get("name").textValue();
            boolean toEnable = resource.get("enabled").asBoolean();
            
            for (JsonNode policy : policies) {
                ArrayNode policyResources = (ArrayNode) policy.get("resources");
                boolean resourceFound = false;
                
                ArrayList<String> resourcesToAdd = new ArrayList<String>();
                ArrayList<String> groups = new ArrayList<String>();
                
                for(JsonNode policyResource : policyResources){
                    resourcesToAdd.add(policyResource.textValue());
                    if(policyResource.textValue().equals(resourceValue)){
                        resourceFound = true;
                    }
                }
                
                
                
                if(resourceFound){
                    JsonNode subject = policy.get("subject");
                    ArrayNode subjects = (ArrayNode) subject.get("subjectValues");
                    String subjectType = subject.get("type").textValue();
                    for(JsonNode groupResource : subjects){
                        if(subjectType.equals("Identity")){
                            String groupResourceName = groupResource.textValue();
                            String[] groupIdString = groupResourceName.split(",");
                            String groupId = groupIdString[0].split("=")[1];
                            groups.add(groupId);
                        }
                        
                    }
                    
                    String policyName = policy.get("name").textValue();
                    String policyDescription = policy.get("description").textValue();
                  
                    int groupIndex = groups.indexOf(groupName);
                    
                    if(toEnable){
                        if(groupIndex<0){
                            groups.add(groupName);
                        }
                    }
                    else {
                        if(groupIndex>=0){
                            groups.remove(groupIndex);
                        }
                    }  
                    
                    Form policyForm = new Form();
                    
                    for(String resourceToAdd : resourcesToAdd){
                        policyForm.param("resources[]", resourceToAdd);
                    }
                    
                    for(String groupToAdd : groups){
                        policyForm.param("groups[]", groupToAdd);
                    }
                    
                    policyForm.param("name", policyName);
                    policyForm.param("description", policyDescription);
                    
                    
                    if(groups.size()>0)
                        policyForm.param("nogr", "true");
                    else
                        policyForm.param("nogr", "false");
                    
                    if(policyResources.size()>0)
                        policyForm.param("nores", "false");
                    else
                        policyForm.param("nores", "true");
                                       
                    if(policy.get("active").asBoolean())
                        policyForm.param("active", "true");
                    else
                        policyForm.param("active", "false");
                   
                    JsonNode actionValues = (JsonNode) policy.get("actionValues");
                        
                    policyForm.param("noact", "false");
                    
                    Client clientBis = ClientBuilder.newClient();
                    
                    JsonNode policyEditedResponse = null;
                    try {
                            policyEditedResponse = clientBis
                                            .target(securityService.getSecurityProxyUrl() + "/policy/"+policyName)
                                            .request(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
                                            .cookie(authCookie)
                                            .accept(MediaType.APPLICATION_JSON_TYPE)
                                            .post(Entity.entity(policyForm, MediaType.APPLICATION_FORM_URLENCODED_TYPE), JsonNode.class);                
                    } catch (WebApplicationException e) {
                            logger.log(Level.SEVERE, e.getMessage(), e);
                            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
                    } finally {
                            client.close(); 
                    }
                }
                
            }
            
            
        }
        
        return Response.status(Response.Status.OK).build();
    }
}

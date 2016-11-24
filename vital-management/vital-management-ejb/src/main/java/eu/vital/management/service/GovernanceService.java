package eu.vital.management.service;

import com.fasterxml.jackson.databind.JsonNode;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


import eu.vital.management.security.SecurityService;
import eu.vital.management.storage.DocumentManager;
import eu.vital.management.util.VitalClient;
import eu.vital.management.util.VitalConfiguration;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Date;

import java.util.*;

import javax.inject.Inject;
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



@Stateless
public class GovernanceService {

	@Inject
	private DocumentManager documentManager;

	@Inject
	private ObjectMapper objectMapper;

	@Inject
	private VitalClient vitalClient;
	
	
    

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

	public JsonNode getBoundaries(@Context HttpHeaders hh) throws Exception {
		ArrayNode boundariesResult = documentManager.getList(DocumentManager.DOCUMENT_TYPE.BOUNDARIES.toString());
		if (boundariesResult.size() > 0) {
			return boundariesResult.get(0);
		} else {
			return saveBoundaries(hh,objectMapper.createObjectNode());
		}
	}

	public JsonNode saveBoundaries(@Context HttpHeaders hh,JsonNode boundaries) throws Exception {
		documentManager.update(DocumentManager.DOCUMENT_TYPE.BOUNDARIES.toString(), "BOUNDARIES", boundaries);
		JsonNode savedBoundaries = getBoundaries(hh);

		//-----------------------------
		// TODO: Add code here to update global security policies based on "savedBoundaries"
		// vitalClient.doPost(....);
		//-----------------------------		
		
		
		String new_bound="{\"name\":\"http://purl.oclc.org:80/NET/ssnx/ssn#observationProperty|@type$",ii="",groupName="location";
		String new_bound_meta="{\"name\":\"http://purl.oclc.org:80/NET/ssnx/ssn#observes|@type$";
		String bound_ob="";
		String policyNameArea="GlobalAreaBoundaryPolicy",policyNameObservation="GlobalObservationsBoundaryPolicy",mapLimit="";
		String pre_ang_lat="{http://vital-iot.eu/ontology/ns/hasLastKnownLocation|http://www.w3.org/2003/01/geo/wgs84_pos#lat|@value${\"$gt\":";
		String pre_ang_long="{http://vital-iot.eu/ontology/ns/hasLastKnownLocation|http://www.w3.org/2003/01/geo/wgs84_pos#long|@value${\"$gt\":";

		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> result = mapper.convertValue(boundaries, Map.class);
		Boolean bound_found=false,map_found=false;
		for(String key: result.keySet()){
            	if(key.indexOf("id")==-1 ){
        		Map<String, Object> bound = mapper.convertValue(result.get(key), Map.class);
        		for(String first: bound.keySet()){
        			if( key.indexOf("mapArea")==-1 && bound.get(first).toString().equals("false")){
    		        		bound_ob=bound_ob+ii+new_bound+first+"\"";
        				ii="},";
					bound_ob=bound_ob+ii+new_bound_meta+first+"\"";
        				bound_found=true;


        			}
        			else if ( key.indexOf("mapArea") == 0 && first.equals("features")){
        				String search=bound.get(first).toString();
        				if(!search.equals("[]")){
        					search=search.substring(search.indexOf("coordinates=[[")+15,search.length()-6);
        					String[] ary = search.split("\\]\\,\\ \\[");
        					String a1=ary[1].split(",")[0];
        					String a2=ary[1].split(",")[1];
        					String b1=ary[2].split(",")[0];
        					String c2=ary[3].split(",")[1];
        					mapLimit="{\"$and\": ["+pre_ang_lat+c2+", \"$lt\":"+a2+"}},"+pre_ang_long+a1+", \"$lt\":"+b1+"}}]}";
        					map_found=true;
        				}
            		}
        		}
            	}
		}
		
		JsonNode createServiceNode=boundaries,actualObj=boundaries;
		bound_ob="{\"resources\" : ["+bound_ob+"}]}";


		ObjectMapper mapper2 = new ObjectMapper();

		deletePolicy(hh,policyNameObservation);
		if (bound_found){
		 	actualObj = mapper2.readValue(bound_ob,JsonNode.class);
			createServiceNode = createBoundariesPolicy(hh,policyNameObservation,actualObj,null,false);
		}
		
		deletePolicy(hh,policyNameArea);
		if (map_found){
		 	createServiceNode = createBoundariesPolicy(hh,policyNameArea,null,mapLimit,true);
		}

		return savedBoundaries;
	}

	public JsonNode getAccess(@Context HttpHeaders hh,String groupName) throws Exception {
		JsonNode access = documentManager.get(DocumentManager.DOCUMENT_TYPE.ACCESS.toString(), groupName);
		if (access == null) {
			return saveAccess(hh,groupName, objectMapper.createObjectNode());
		} else {
			return access;
		}
	}

	public JsonNode saveAccess(@Context HttpHeaders hh,String groupName, JsonNode access) throws Exception {
		documentManager.update(DocumentManager.DOCUMENT_TYPE.ACCESS.toString(), groupName, access);
		JsonNode savedAccess = getAccess(hh,groupName);

		//-----------------------------
		// TODO: Add code here to update group policies based on "savedAccess"
		// vitalClient.doPost(....);
		//-----------------------------
		String new_access="",ii="";

		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> result = mapper.convertValue(access, Map.class);

        
	 	String answer_header="{\"_id\":\""+groupName+"\",\"id\":\""+groupName+"\",\"resources\" : [";
	 	String answer="",sep="";
	 	for(String key: result.keySet()){
            	if(key.indexOf("id")==-1){
            		answer=answer+sep;
            		answer=answer+"{\"name\":\""+key+"*\",";
            		answer=answer+"\"enabled\":"+access.get(key).asText()+"}";
            		sep=",";
            	}
	 	}	
	 	answer=answer_header+answer+"]}";   
	 	ObjectMapper mapper1 = new ObjectMapper();
	 	JsonNode actualObj = mapper1.readValue(answer, JsonNode.class);

	 	JsonNode updateServiceeNode =updateAccess(hh,groupName,actualObj);

		if(updateServiceeNode==null )
			throw new Exception("Problem in updating Access");
		return savedAccess;

		
	}
	public JsonNode  createBoundariesPolicy(
            @Context HttpHeaders hh, 
            @FormParam("name") String name, 
            JsonNode data,
            @FormParam("name") String map,
            @FormParam("enable") Boolean enable) throws Exception {
		
        // Connect to Security Server
        Client client = ClientBuilder.newClient();
        Client clientBis = ClientBuilder.newClient();

        // Check if cookie is valid
        Cookie authCookie = readAuthCookie(hh);
        if (authCookie == null) {
                return null;//Response.status(Response.Status.UNAUTHORIZED).build();
        }
        JsonNode user = securityService.getLoggedOnUser(authCookie.getValue());
        if (user == null) {
                return null;//Response.status(Response.Status.UNAUTHORIZED).build();
        }

        String policyName = name;
        
        Form groupForm = new Form();
        groupForm.param("name", policyName);

        Form policyForm = new Form();
        if(data!=null) {
        	ArrayNode resources = (ArrayNode) data.get("resources");
        	for(JsonNode resource : resources){
        		String resourceValue = resource.get("name").textValue();
        		policyForm.param("resources[]", resourceValue);
        	}
        }else
        	policyForm.param("resources[]",map);
        policyForm.param("appname", "Data access control");
        policyForm.param("name", policyName);
        policyForm.param("description", policyName + " to enforce sensor retrieval boundaries");
        policyForm.param("groups[]", "developers");
        policyForm.param("groups[]", "internal");

        if (enable){
        	policyForm.param("actions[RETRIEVE]", "true");
        	policyForm.param("actions[STORE]", "true");
        }else {
        	policyForm.param("actions[RETRIEVE]", "false");
            policyForm.param("actions[STORE]", "false");
        }
        
        JsonNode groupCreatedResponse = null;
        JsonNode policyCreatedResponse = null;
        try {
                policyCreatedResponse = clientBis
                                .target(securityService.getSecurityProxyUrl() + "/policy/create")
                                .request(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
                                .cookie(authCookie)
                                .accept(MediaType.APPLICATION_JSON_TYPE)
                                .post(Entity.entity(policyForm, MediaType.APPLICATION_FORM_URLENCODED_TYPE), JsonNode.class);      

        } catch (WebApplicationException e) {
                return null; //Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } finally {

                clientBis.close();
        }
        return  policyCreatedResponse;

    }

	public JsonNode  createService(
            @Context HttpHeaders hh, 
            @FormParam("name") String name, 
            @FormParam("resource") String resource) throws Exception {

        // Connect to Security Server
        Client client = ClientBuilder.newClient();
        Client clientBis = ClientBuilder.newClient();

        // Check if cookie is valid
        Cookie authCookie = readAuthCookie(hh);
        if (authCookie == null) {
                return null;//Response.status(Response.Status.UNAUTHORIZED).build();
        }
        JsonNode user = securityService.getLoggedOnUser(authCookie.getValue());
        if (user == null) {
                return null;//Response.status(Response.Status.UNAUTHORIZED).build();
        }

        String groupName = name + "_group";
        String policyName = name + "_policy";
        
        Form groupForm = new Form();
        groupForm.param("name", groupName);

        Form policyForm = new Form();
        
        
        policyForm.param("resources[]",resource);
        
        policyForm.param("appname", "iPlanetAMWebAgentService");
        if(name.indexOf("location")==-1){
            policyForm.param("groups[]", groupName);
        }
        policyForm.param("name", policyName);
        policyForm.param("description", policyName + " description");
        policyForm.param("actions[POST]", "true");
        policyForm.param("actions[GET]", "true");
        policyForm.param("actions[PUT]", "true");
        policyForm.param("actions[DELETE]", "true");

        JsonNode groupCreatedResponse = null;
        JsonNode policyCreatedResponse = null;
        try {
        		if(name.indexOf("location")==-1){
        			groupCreatedResponse = client
                                .target(securityService.getSecurityProxyUrl() + "/group/create")
                                .request(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
                                .cookie(authCookie)
                                .accept(MediaType.APPLICATION_JSON_TYPE)
                                .post(Entity.entity(groupForm, MediaType.APPLICATION_FORM_URLENCODED_TYPE), JsonNode.class);                
        		}
                policyCreatedResponse = clientBis
                                .target(securityService.getSecurityProxyUrl() + "/policy/create")
                                .request(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
                                .cookie(authCookie)
                                .accept(MediaType.APPLICATION_JSON_TYPE)
                                .post(Entity.entity(policyForm, MediaType.APPLICATION_FORM_URLENCODED_TYPE), JsonNode.class);      

        } catch (WebApplicationException e) {
                return null; //Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } finally {
    		if(name.indexOf("location")==-1){
                client.close(); 
    		}	
                clientBis.close();
        }
        return  policyCreatedResponse;

    }
    
    
	public JsonNode  deletePolicy(
            @Context HttpHeaders hh, 
            @FormParam("name") String name) throws Exception {

        // Connect to Security Server
        Client client = ClientBuilder.newClient();
        Client clientBis = ClientBuilder.newClient();

        // Check if cookie is valid
        Cookie authCookie = readAuthCookie(hh);
        if (authCookie == null) {
                return null;//Response.status(Response.Status.UNAUTHORIZED).build();
        }
        JsonNode user = securityService.getLoggedOnUser(authCookie.getValue());
        if (user == null) {
                return null;//Response.status(Response.Status.UNAUTHORIZED).build();
        }


        Form policyForm = new Form();
        policyForm.param("name", name);

        JsonNode policyDeletedResponse = null;
        try {
                policyDeletedResponse = clientBis
                                .target(securityService.getSecurityProxyUrl() + "/policy/delete")
                                .request(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
                                .cookie(authCookie)
                                .accept(MediaType.APPLICATION_JSON_TYPE)
                                .post(Entity.entity(policyForm, MediaType.APPLICATION_FORM_URLENCODED_TYPE), JsonNode.class);      

        } catch (WebApplicationException e) {
                return null; //Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } finally {
    		if(name.indexOf("location")==-1){
                client.close(); 
    		}	
                clientBis.close();
        }
        return  policyDeletedResponse;

    }
    
	
	
    public JsonNode updateAccess(
            @Context HttpHeaders hh, 
            @FormParam("name") String name, 
             JsonNode data
    ) throws Exception {
    	  Client client = ClientBuilder.newClient();

          // Check if cookie is valid
          Cookie authCookie = readAuthCookie(hh);
          if (authCookie == null) {
                  return null;
          }

          JsonNode user = securityService.getLoggedOnUser(authCookie.getValue());
          if (user == null) {
                  return null;
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
                  return null;
          } finally {
                  client.close();
          }
      
      String groupName = data.get("id").textValue();
      
      ArrayNode resources = (ArrayNode) data.get("resources");
      ArrayNode policies = (ArrayNode) gotPolicies.get("result");
      JsonNode policyEditedResponse = null;

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
                  
                  try {
                          policyEditedResponse = clientBis
                                          .target(securityService.getSecurityProxyUrl() + "/policy/"+policyName)
                                          .request(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
                                          .cookie(authCookie)
                                          .accept(MediaType.APPLICATION_JSON_TYPE)
                                          .post(Entity.entity(policyForm, MediaType.APPLICATION_FORM_URLENCODED_TYPE), JsonNode.class);                
                  } catch (WebApplicationException e) {
                          return null;
                  } finally {
                          client.close(); 
                  }
              }
              
          }
          
          
      }
      
      return policyEditedResponse;    	
    }
   
    public JsonNode updateBoundaries(
            @Context HttpHeaders hh, 
             JsonNode data
    ) throws Exception {
    	  Client client = ClientBuilder.newClient();

          // Check if cookie is valid
          Cookie authCookie = readAuthCookie(hh);
          if (authCookie == null) {
                  return null;
          }

          JsonNode user = securityService.getLoggedOnUser(authCookie.getValue());
          if (user == null) {
                  return null;
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
                  return null;
          } finally {
                  client.close();
          }

      ArrayNode resources = (ArrayNode) data.get("resources");
      ArrayNode policies = (ArrayNode) gotPolicies.get("result");
      JsonNode policyEditedResponse = null;

      for (JsonNode resource : resources) {
          String resourceValue = resource.get("name").textValue();
          boolean toEnable = resource.get("enabled").asBoolean();

          for (JsonNode policy : policies) {
              ArrayNode policyResources = (ArrayNode) policy.get("resources");
              boolean resourceFound = false;
              
              ArrayList<String> resourcesToAdd = new ArrayList<String>();
              
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

                  
                  String policyName = policy.get("name").textValue();
                  String policyDescription = policy.get("description").textValue();
                

                  
                  Form policyForm = new Form();
                  
                  for(String resourceToAdd : resourcesToAdd){
                      policyForm.param("resources[]", resourceToAdd);
                  }
                  
                  policyForm.param("name", policyName);
                  policyForm.param("description", policyDescription);
                  
                  

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
                  
                  try {
                          policyEditedResponse = clientBis
                                          .target(securityService.getSecurityProxyUrl() + "/policy/"+policyName)
                                          .request(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
                                          .cookie(authCookie)
                                          .accept(MediaType.APPLICATION_JSON_TYPE)
                                          .post(Entity.entity(policyForm, MediaType.APPLICATION_FORM_URLENCODED_TYPE), JsonNode.class);                
                  } catch (WebApplicationException e) {
                          return null;
                  } finally {
                          client.close(); 
                  }
              }
              
          }
          
          
      }
      
      return policyEditedResponse;    	
    }
	public JsonNode getSLAObservations(String groupName, String slaType) throws Exception {
		//-----------------------------
		// TODO: Add code here to retrieve a set of SLA metric for the slaType
		//-----------------------------
		if ("throughput".equals(slaType)) {
			// Return a random array now:
			ArrayNode slas = objectMapper.createArrayNode();
			Date now = new Date();
			for (int i = 200; i > 0; i--) {
				ObjectNode sla = objectMapper.createObjectNode();
				sla.put("timestamp", now.getTime() - i * 10000);
				sla.put("value", 1 + Math.random() * 100);

				slas.add(sla);
			}
			return slas;
		}
		throw new Exception("Not supported Type");
	}

}
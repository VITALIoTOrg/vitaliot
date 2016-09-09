package eu.vital.TrustManager.restApp;


import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import eu.vital.TrustManager.MongoDriver;
import eu.vital.TrustManager.TrustManager;


@Path("")
public class TRUSTMANAGER {

	final static Logger logger = Logger.getLogger(TRUSTMANAGER.class);
	
	public TRUSTMANAGER() throws IOException {
        
        
    }
	
	@POST
	@Path("getIoTsystemTrustScore")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public Response getTrustScores(String body){
		JSONObject jo = new JSONObject(body);
		if (jo.has("IoTSystemID")){
			String id = jo.getString("IoTSystemID");
			MongoDriver md;
			try {
				md = MongoDriver.getInstance();
			
				if (md!= null){
					String ans = md.getTrustScores(id);
					return Response.status(Response.Status.OK).entity(new JSONObject(ans).toString()).build(); 
				}
			} catch (Exception e) {
				logger.error(e,e);
			}
		}
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		
		
	}
	
	
    @POST
    @Path("startTrustManager")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response startTrustManager(String body) {
    	TrustManager tm;
    
        try{
        	tm = TrustManager.getNewInstance(body);
        
        	boolean result = tm.initSystem();
        	
        	if (!result)
            	tm = null;
        }catch (Exception e){
        	logger.error(e,e);
        	tm =null;
        }
        
        
        
        if (tm != null){
        	return Response.status(Response.Status.OK).build();
        }else{
        	return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    
    @DELETE
    @Path("stopTrustManager")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response stopTrustManager() throws IOException {
        
    	TrustManager tm = TrustManager.getInstance();
        
        
        if (tm != null){
        	tm.removeInstance();
        	return Response.status(Response.Status.OK).build();
        }else{
        	return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}


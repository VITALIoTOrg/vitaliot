/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.vital.vitalcep.restApp.vuaippi;

import eu.vital.vitalcep.conf.PropertyLoader;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Context;

import org.apache.log4j.Logger;
import org.json.JSONObject;


import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.util.JSON;
import eu.vital.vitalcep.security.Security;
import org.bson.Document;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import org.bson.types.ObjectId;

// TODO: Auto-generated Javadoc
/**
 * The Class FilteringRestREST.
 */
@Path("")
public class Observation {

    /** The Constant logger. */
    final static Logger logger = Logger.getLogger(Observation.class);
    
    private final String ONTOLOGY = "http://vital-iot.eu/ontology/ns/";
    
    private final String OBSERVATIONSERVICE_TYPE = ONTOLOGY
            +"ObservationService";
    
    private final String CEPFILTERINGSERVICE_TYPE = ONTOLOGY
            +"CEPFitleringService";

    private final String CEPICOMANAGEMENTSERVICE_TYPE = ONTOLOGY
            +"CEPICOManagementService";

    private final String MONITORINGSERVICE_TYPE = ONTOLOGY+"MonitoringService";

    private final String MONITORINGSENSOR_TYPE = ONTOLOGY+"MonitoringSensor";
    
    private final String CEPICOSENSOR_TYPE = ONTOLOGY+"CEPSensor";

    private final String CEPFILTERSTATICDATASENSOR_TYPE = 
            ONTOLOGY+"CEPFilterStaticDataSensor";

    private final String CEPFILTERSTATICQUERYSENSOR_TYPE = 
            ONTOLOGY+"CEPFilterStaticQuerySensor";
    
    private final String CEPFILTERSENSOR_TYPE = 
            ONTOLOGY+"CEPFilterSensor";

    private Properties config ;
    
    private PropertyLoader props;
    
    private String host;
    
    private String mongoIp;
    
    private int mongoPort;
    
    private String mongoDB;
    private String cookie;

    
   // @Context private javax.servlet.http.HttpServletRequest hsr;
    
    public Observation() throws IOException {

        props = new PropertyLoader();

        mongoPort = Integer.parseInt(props.getProperty("mongo.port"));
        mongoIp= props.getProperty("mongo.ip");
        mongoDB = props.getProperty("mongo.db");
        host = props.getProperty("cep.resourceshostname");                      
        
        if (host == null || host.isEmpty()){
             host = "localhost:8180";       
        }

    }
   
    
     /**
     * Gets sensors metadata .
     *
     * @return the metadata of the sensors 
     * @throws java.io.FileNotFoundException 
     */
    @POST
    @Path("observation/stream/subscribe")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response subscribeToObservations(String info,
            @Context HttpServletRequest req) throws FileNotFoundException, 
            IOException {
        
        StringBuilder ck = new StringBuilder();
        Security slogin = new Security();
                  
        Boolean token = slogin.login(req.getHeader("name")
                ,req.getHeader("password"),false,ck);
        if (!token){
              return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        this.cookie = ck.toString(); 
                  
        MongoClient mongo = new MongoClient(mongoIp, mongoPort);

        MongoDatabase db = mongo.getDatabase(mongoDB);
                
        DBObject dbObject = (DBObject) JSON.parse(info);
        
        if (!dbObject.containsField("sensor")){
            return Response.status(Response.Status.BAD_REQUEST)
                    .build();
        }
        if (!dbObject.containsField("property")){
            return Response.status(Response.Status.BAD_REQUEST)
                    .build();
        }
        if (!dbObject.containsField("url")){
            return Response.status(Response.Status.BAD_REQUEST)
                    .build();
        }
        
        Document doc = new Document(dbObject.toMap());

        try{
            db.getCollection("subscriptions").insertOne(doc);
            String id = doc.get("_id").toString();
            return Response.status(Response.Status.OK)
                .entity("{\"subscription\":\""+id+"\"}").build();
        }catch(MongoException ex
                ){
            return Response.status(Response.Status.BAD_REQUEST)
                    .build();
        }
         
    }
    
    @POST
    @Path("observation/stream/unsubscribe")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response unSubscribeToObservations(String info,
            @Context HttpServletRequest req) throws FileNotFoundException, 
            IOException {
         JSONObject jObject = new JSONObject();
        try{
            jObject = new JSONObject(info);
            if (!jObject.has("subscription")){
                return Response.status(Response.Status.BAD_REQUEST)
                        .build();
            }
           
        }catch(Exception e){
             return Response.status(Response.Status.BAD_REQUEST)
                    .build();
        }
        
               
        
        StringBuilder ck = new StringBuilder();
        Security slogin = new Security();
                  
        Boolean token = slogin.login(req.getHeader("name")
                ,req.getHeader("password"),false,ck);
        if (!token){
              return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        this.cookie = ck.toString(); 
        
        MongoClient mongo = new MongoClient(mongoIp, mongoPort);

        MongoDatabase db = mongo.getDatabase(mongoDB);
        
        Document doc = new Document("_id",
                new ObjectId(jObject.getString("subscription")));
        
        DeleteResult deleted =  db.getCollection("subscriptions")
                                .deleteOne(doc);
        
        if (deleted.wasAcknowledged()!= true || deleted.getDeletedCount()!=1 ){
            return Response.status(Response.Status.NOT_FOUND)
                    .build();
        }
        
        return Response.status(Response.Status.OK).build();
    }
}

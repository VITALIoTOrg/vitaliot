/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.vital.vitalcep.restApp.filteringApi;


import eu.vital.vitalcep.conf.PropertyLoader;
import eu.vital.vitalcep.entities.dolceHandler.DolceSpecification;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;


import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONObject;
import org.json.JSONArray;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;

import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;

import com.mongodb.util.JSON;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.gte;
import com.mongodb.client.result.DeleteResult;
import eu.vital.vitalcep.cep.CEP;
import eu.vital.vitalcep.connectors.mqtt.MQTT_connector_subscriper;
import eu.vital.vitalcep.connectors.mqtt.MessageProcessor_publisher;
import eu.vital.vitalcep.connectors.mqtt.MqttConnectorContainer;

import java.util.logging.Level;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;
import org.apache.commons.lang.RandomStringUtils;
import org.bson.Document;
import org.json.JSONException;


// TODO: Auto-generated Javadoc
/**
 * The Class ContinuosFiltering.
 */
@Path("filtering")
public class ContinuosFiltering {

    /** The Constant logger. */
    final static Logger logger = Logger.getLogger(ContinuosFiltering.class);
    
     private Properties config ;
    
    private PropertyLoader props;
    
    private String host;
    
    private String mongoIp;
    
    private int mongoPort;
    
    private String mongoDB;
    
    public ContinuosFiltering() throws IOException {

        props = new PropertyLoader();

        mongoPort = Integer.parseInt(props.getProperty("mongo.port"));
        mongoIp= props.getProperty("mongo.ip");
        mongoDB = props.getProperty("mongo.db");
                              
        String filePath = System
                .getProperty("jboss.server.config.dir")+"/rest_interface.conf";
                      
        Properties config2 = new Properties() ;
        
        try {
            config2.load(new FileInputStream(filePath.toString()));
            config = config2;
            host = config.getProperty("cep-ip-address").concat(":8180");
        } catch (IOException e) {
              host = "localhost:8180";
        }
        
    }
    
    
    //@Context private javax.servlet.http.HttpServletRequest hsr;

    /**
     * Gets the filters.
     *
     * @return the filters
     */
    @GET
    @Path("getcontinuousfilters")
    @Produces(MediaType.APPLICATION_JSON)
    public Response  getFilterings() {
        
        
        MongoClient mongo = new MongoClient(mongoIp, mongoPort);
            MongoDatabase db = mongo.getDatabase(mongoDB);
            
            try {
               db.getCollection("staticdatafilters");
            } catch (Exception e) {
              //System.out.println("Mongo is down");
              mongo.close();
              return Response.status(Response
                                .Status.INTERNAL_SERVER_ERROR).build();
                    
            }
        

        // create an empty query
        BasicDBObject query = new BasicDBObject(); 
        BasicDBObject fields = new BasicDBObject().append("_id",false);
        fields.append("dolceSpecification", false);
        
        FindIterable<Document> coll = db.getCollection("filters")
                .find(query).projection(fields);
                
        final JSONArray AllJson = new JSONArray();
        
        coll.forEach(new Block<Document>() {
            @Override
            public void apply(final Document document) {
                 String aux = document.toJson();
                JSONObject sensoraux = new JSONObject(aux);  
                AllJson.put(sensoraux);
            }
        });
            
        return Response.status(Response
                                .Status.OK).entity(AllJson.toString()).build();

    }

    /**
     * Creates a filter.
     *
     * @param filter
     * @return the filter id 
     * @throws java.io.IOException 
     */
    @POST
    @Path("createcontinuousfilter")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createcontinuousfilter(String filter) throws IOException {
        
       
        JSONObject jo = new JSONObject(filter);
        MongoClient mongo = new MongoClient(mongoIp, mongoPort);
            MongoDatabase db = mongo.getDatabase(mongoDB);
            
            try {
               db.getCollection("staticdatafilters");
            } catch (Exception e) {
              //System.out.println("Mongo is down");
              mongo.close();
              return Response.status(Response
                                .Status.INTERNAL_SERVER_ERROR).build();
                    
            }
        

        // create an empty query
        BasicDBObject query = new BasicDBObject(); 
        BasicDBObject fields = new BasicDBObject().append("_id",false);
        fields.append("dolceSpecification", false);
        
               
        if ( jo.has("dolceSpecification")) {
            
            //Filter oFilter = new Filter(filter);
            JSONObject dsjo = jo.getJSONObject("dolceSpecification");
            String str = dsjo.toString();//"{\"dolceSpecification\": "+ dsjo.toString()+"}";

            
            try{
                
                DolceSpecification ds = new DolceSpecification(str);
                
                if(ds instanceof DolceSpecification) {
                

                    UUID uuid = UUID.randomUUID();
                    String randomUUIDString = uuid.toString();
                            
                    DBObject dbObject = 
                            createCEPFilterSensor(filter, randomUUIDString, dsjo);

                    String mqin = RandomStringUtils.randomAlphanumeric(8);
                    String mqout = RandomStringUtils.randomAlphanumeric(8);
                    
                    

                    CEP cepProcess = new CEP(CEP.CEPType.DATA,ds.toString()
                        ,mqin,mqout);
                
                
                    String clientName = cepProcess.fileName;

                    if (cepProcess.PID<1){
                        return Response.status(Response
                                .Status.INTERNAL_SERVER_ERROR).build();
                    }
               
                   
                //MIGUEL
                MessageProcessor_publisher Publisher_MsgProcc = new MessageProcessor_publisher();//555
                MQTT_connector_subscriper publisher = new MQTT_connector_subscriper (mqout,Publisher_MsgProcc);
                MqttConnectorContainer.addConnector(publisher.getClientName(), publisher);
                
                //TODO --> DESTROY DEL CONNECTOR.
                
                MqttConnectorContainer.deleteConnector(publisher.getClientName());
                
                
                //
                   // String cepPath = "/home/a601149/workspace/BCEP/bcepCode/bcep/source";

                    //Runtime r = Runtime.getRuntime();

                    //Process p = r.exec(cepPath+"bcep -a");
                    
                    Document doc = new Document(dbObject.toMap());

                    try{
                        db.getCollection("filters").insertOne(doc);
                        
                        
                        JSONObject aOutput = new JSONObject();
                        aOutput.put("id", "http://"+ host.toString()+"/cep/sensor/"
                            +randomUUIDString);
                        return Response.status(Response.Status.OK)
                            .entity(aOutput.toString()).build();
                       
                    }catch(MongoException ex
                            ){
                        return Response.status(Response.Status.BAD_REQUEST)
                                .build();
                    }

//                    WriteResult result = coll.insert(dbObject);
//                    
//                    if (result.getLastError().getErrorMessage()!= null){
//                        return Response.status(Response.Status.BAD_REQUEST)
//                                .build();
//                    }else{
//                         return Response.status(Response.Status.OK)
//                            .entity("{\"uri\":\"http://"+ host+"/cep/sensor/"
//                            +randomUUIDString+"\"}").build();
//                    }
                   
                   // String  id = dbObject.get("_id").toString();
      
                }else{
                    return Response.status(Response.Status.BAD_REQUEST).build();
                }
            }catch(Exception e){
                 return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }   
        
        return Response.status(Response.Status.BAD_REQUEST).build();
         
    }

    private DBObject createCEPFilterSensor(String filter, String randomUUIDString, JSONObject dsjo) throws JSONException {
        DBObject dbObject = (DBObject) JSON.parse(filter);
        dbObject.removeField("id");
        dbObject.put("id", "http://"+ host.toString()+"/cep/sensor/"
                +randomUUIDString);
        dbObject.put("type", "CEPFilterSensor");
        dbObject.put("status", "vitalRunning");
        JSONArray observes =  new JSONArray();
        JSONArray compl = dsjo.getJSONArray("complex");
        for (int i = 0; i < compl.length(); i++) {
            JSONObject oComplex = new JSONObject(
                    compl.get(i).toString());
            JSONObject oObserves = new JSONObject();
            
            oObserves.put("type", "vital:ComplexEvent");
            //oObserves.put("uri", "http://"+ host.toString()
            //        +"/cep/sensor/"+randomUUIDString
            //        +"/"+oComplex.getString("id").toString());
            oObserves.put("id", "http://"+ host.toString()
                    +"/cep/sensor/"+randomUUIDString
                    +"/"+oComplex.getString("id").toString());
            
            observes.put(oObserves);
            
        }
        DBObject dbObject2 = (DBObject)JSON.parse(observes
                            .toString());
        dbObject.put("ssn:observes",dbObject2);
        return dbObject;
    }
    
    /**
     * Gets a filter.
     *
     * @param filterId
     * @return the filter 
     */
    @POST
    @Path("getcontinuousfilter")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getcontinuousfilter(String filterId) {
        
        JSONObject jo = new JSONObject(filterId);
        String idjo = jo.getString("id");
             
       MongoClient mongo = new MongoClient(mongoIp, mongoPort);
            MongoDatabase db = mongo.getDatabase(mongoDB);
            
            try {
               db.getCollection("filters");
            } catch (Exception e) {
              //System.out.println("Mongo is down");
              mongo.close();
              return Response.status(Response
                                .Status.INTERNAL_SERVER_ERROR).build();
                    
            }
        
        BasicDBObject searchById = new BasicDBObject("id",idjo);
        String found = null;
        BasicDBObject fields = new BasicDBObject().append("_id",false)
                .append("dolceSpecification", false);

       FindIterable<Document> coll = db.getCollection("filters")
                .find(searchById).projection(fields);
        
        try {
            found = coll.first().toJson();
        }catch(Exception e){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        
        if (found == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }else{
            return Response.status(Response.Status.OK)
                    .entity(found.toString()).build();
        }
        
         
    }
    
    /**
     * Gets a filter.
     *
     * @param filterId
     * @return the filter 
     */
    @POST
    @Path("deletecontinuousfilter")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deletecontinuousfilter(String filterId) {
        
        JSONObject jo = new JSONObject(filterId);
        String idjo = jo.getString("id");
             
        MongoClient mongo = new MongoClient(mongoIp, mongoPort);
            MongoDatabase db = mongo.getDatabase(mongoDB);
            
            try {
               db.getCollection("filters");
            } catch (Exception e) {
              //System.out.println("Mongo is down");
              mongo.close();
              return Response.status(Response
                                .Status.INTERNAL_SERVER_ERROR).build();
                    
            }
            
            
        MongoCollection<Document> coll = db.getCollection("filters");
        DBObject searchById = new BasicDBObject("id",idjo);
        DBObject found = null;
        
        DeleteResult deleteResult = coll.deleteOne(gte("id",idjo));
        System.out.println(deleteResult.getDeletedCount());
        
        
        if (deleteResult.getDeletedCount() < 1){
            return Response.status(Response.Status.NOT_FOUND).build();
        }else{
            
                return Response.status(Response.Status.OK)
                    .build();
        }
    }
	
}

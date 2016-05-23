/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.vital.vitalcep.restApp.filteringApi;


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
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;

import com.mongodb.util.JSON;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import static com.mongodb.client.model.Filters.eq;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import eu.vital.vitalcep.cep.CEP;
import eu.vital.vitalcep.cep.CepProcess;
import eu.vital.vitalcep.conf.ConfigReader;
import eu.vital.vitalcep.publisher.MQTT_connector_subscriper;
import eu.vital.vitalcep.publisher.MessageProcessor_publisher;
import eu.vital.vitalcep.connectors.mqtt.MqttConnectorContainer;
import eu.vital.vitalcep.security.Security;

import java.util.logging.Level;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.core.Context;
import org.apache.commons.lang.RandomStringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.JSONException;


// TODO: Auto-generated Javadoc
/**
 * The Class ContinuosFiltering.
 */
@Path("filtering")
public class ContinuosFiltering {

    /** The Constant logger. */
    final static Logger logger = Logger.getLogger(ContinuosFiltering.class);
    
    private final String host;
    private final String mongoURL;
    private final String mongoDB;
    private final String dmsURL;
    private String cookie;
    
    public ContinuosFiltering() throws IOException {

        ConfigReader configReader = ConfigReader.getInstance();
        
        mongoURL = configReader.get(ConfigReader.MONGO_URL);
        mongoDB = configReader.get(ConfigReader.MONGO_DB);
        dmsURL = configReader.get(ConfigReader.DMS_URL);
        host = configReader.get(ConfigReader.CEP_BASE_URL);
        
    }
    
    /**
     * Gets the filters.
     *
     * @return the filters
     */
    @GET
    @Path("getcontinuousfilters")
    @Produces(MediaType.APPLICATION_JSON)
    public Response  getFilterings(@Context HttpServletRequest req) {
        
        StringBuilder ck = new StringBuilder();
        Security slogin = new Security();
                  
        Boolean token = slogin.login(req.getHeader("name")
                ,req.getHeader("password"),false,ck);
        if (!token){
              return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        this.cookie = ck.toString(); 
        
        MongoClient mongo = new MongoClient(new MongoClientURI (mongoURL));
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
     * @param req
     * @return the filter id 
     * @throws java.io.IOException 
     */
    @POST
    @Path("createcontinuousfilter")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createcontinuousfilter(String filter
            ,@Context HttpServletRequest req) throws IOException {
        
        StringBuilder ck = new StringBuilder();
        Security slogin = new Security();
        
        JSONObject credentials = new JSONObject();
                  
        Boolean token = slogin.login(req.getHeader("name")
                ,req.getHeader("password"),false,ck);
            credentials.put("username", req.getHeader("name"));
            credentials.put("password", req.getHeader("password"));
        if (!token){
              return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        this.cookie = ck.toString(); 
                
        JSONObject jo = new JSONObject(filter);
        MongoClient mongo = new MongoClient(new MongoClientURI (mongoURL));
        MongoDatabase db = mongo.getDatabase(mongoDB);

        try {
           db.getCollection("continuousfilters");
        } catch (Exception e) {
          //System.out.println("Mongo is down");
          mongo.close();
          return Response.status(Response
                            .Status.INTERNAL_SERVER_ERROR).build();
        }
               
        if ( jo.has("dolceSpecification")) {
            
            JSONObject dsjo = jo.getJSONObject("dolceSpecification");
            String str = dsjo.toString();//"{\"dolceSpecification\": "+ dsjo.toString()+"}";
            
            try{
                
                DolceSpecification ds = new DolceSpecification(str);
                
                if(ds instanceof DolceSpecification) {
                
                    UUID uuid = UUID.randomUUID();
                    String randomUUIDString = uuid.toString();
                            
                 
                    String mqin = RandomStringUtils.randomAlphanumeric(8);
                    String mqout = RandomStringUtils.randomAlphanumeric(8);
                    
                    CEP cepProcess = new CEP();
                   
                    if (!(cepProcess.CEPStart(CEP.CEPType.CONTINUOUS, ds, mqin,
                            mqout, jo.getJSONArray("source").toString(), credentials))){
                        return Response.status(Response
                                .Status.INTERNAL_SERVER_ERROR).build();
                    }
                    
                    
                    if (cepProcess.PID<1){
                        return Response.status(Response
                                .Status.INTERNAL_SERVER_ERROR).build();
                    }
               
                    DBObject dbObject = 
                            createCEPFilterSensor(filter, randomUUIDString
                                    , dsjo,cepProcess.id);
                    
                    Document doc = new Document(dbObject.toMap());

                    try{
                        db.getCollection("continuousfilters").insertOne(doc);
                        
                        JSONObject aOutput = new JSONObject();
                        String sensorId = host+"/sensor/"+randomUUIDString;
                        aOutput.put("id",sensorId);
                        
                        //MIGUEL
                        MessageProcessor_publisher Publisher_MsgProcc 
                                = new MessageProcessor_publisher(this.dmsURL
                                ,this.cookie,sensorId,"continuosfiltersobservations",
                                this.mongoURL,this.mongoDB);//555
                        MQTT_connector_subscriper publisher 
                                = new MQTT_connector_subscriper (mqout,Publisher_MsgProcc);
                        MqttConnectorContainer.addConnector(publisher.getClientName(), publisher);

                        //TODO --> DESTROY DEL CONNECTOR.

    //                    MqttConnectorContainer.deleteConnector(publisher
//                            .getClientName());
                        
                        JSONObject opState = createOperationalStateObservation(
                                randomUUIDString);

                        DBObject oPut =  (DBObject)JSON.parse(opState.toString());
                        Document doc1 = new Document(oPut.toMap());

                        try{
                            db.getCollection("continuosfiltersobservations")
                                    .insertOne(doc1);
                            String id = doc1.get("_id").toString();

                        }catch(MongoException ex
                                ){
                            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                    .build();
                        }
                        return Response.status(Response.Status.OK)
                            .entity(aOutput.toString()).build();
                       
                    }catch(MongoException ex
                            ){
                        return Response.status(Response.Status.BAD_REQUEST)
                                .build();
                    }
                          
                }else{
                    return Response.status(Response.Status.BAD_REQUEST).build();
                }
            }catch(JSONException | IOException e){
                 return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }   
        
        return Response.status(Response.Status.BAD_REQUEST).build();
         
    }

    private DBObject createCEPFilterSensor(String filter, 
            String randomUUIDString, JSONObject dsjo
    ,String cepInstance) throws JSONException {
        DBObject dbObject = (DBObject) JSON.parse(filter);
        dbObject.put("@context","http://vital-iot.eu/contexts/sensor.jsonld");
        dbObject.put("id", host+"/sensor/"
                +randomUUIDString);
        dbObject.put("type", "vital:CEPFilterSensor");
        dbObject.put("status", "vitalRunning");
        JSONArray observes =  new JSONArray();
        JSONArray compl = dsjo.getJSONArray("complex");
        for (int i = 0; i < compl.length(); i++) {
            JSONObject oComplex = new JSONObject(
                    compl.get(i).toString());
            JSONObject oObserves = new JSONObject();
            
            oObserves.put("type", "vital:ComplexEvent");
            //oObserves.put("uri", host+"/sensor/"+randomUUIDString
            //        +"/"+oComplex.getString("id").toString());
            oObserves.put("id", host+"/sensor/"+randomUUIDString
                    +"/"+oComplex.getString("id"));
            
            observes.put(oObserves);
            
        }
        DBObject dbObject2 = (DBObject)JSON.parse(observes
                            .toString());
        dbObject.put("ssn:observes",dbObject2);
        dbObject.put("cepinstance",cepInstance);
        return dbObject;
    }
    
    /**
     * Gets a filter.
     *
     * @param filterId
     * @param req
     * @return the filter 
     */
    @POST
    @Path("getcontinuousfilter")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getcontinuousfilter(String filterId,
            @Context HttpServletRequest req) {
        
        StringBuilder ck = new StringBuilder();
        Security slogin = new Security();
                  
        Boolean token = slogin.login(req.getHeader("name")
                ,req.getHeader("password"),false,ck);
        if (!token){
              return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        this.cookie = ck.toString(); 
        JSONObject jo = new JSONObject(filterId);
        String idjo = jo.getString("id");
             
       MongoClient mongo = new MongoClient(new MongoClientURI (mongoURL));
            MongoDatabase db = mongo.getDatabase(mongoDB);
            
            try {
               db.getCollection("continuousfilters");
            } catch (Exception e) {
              //System.out.println("Mongo is down");
              mongo.close();
              return Response.status(Response
                                .Status.INTERNAL_SERVER_ERROR).build();
            }
        
        BasicDBObject searchById = new BasicDBObject("id",idjo);
        String found;
        BasicDBObject fields = new BasicDBObject().append("_id",false)
                .append("dolceSpecification", false);

       FindIterable<Document> coll = db.getCollection("continuousfilters")
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
                    .entity(found).build();
        }
        
    }
    
    /**
     * Gets a filter.
     *
     * @param filterId
     * @param req
     * @return the filter 
     */
    @DELETE
    @Path("deletecontinuousfilter")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deletecontinuousfilter(String filterId,
            @Context HttpServletRequest req) throws IOException {
        
        StringBuilder ck = new StringBuilder();
        Security slogin = new Security();
                  
        Boolean token = slogin.login(req.getHeader("name")
                ,req.getHeader("password"),false,ck);
        if (!token){
              return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        this.cookie = ck.toString(); 
        
        
        JSONObject jo = new JSONObject(filterId);
        String idjo = jo.getString("id");
             
        MongoClient mongo = new MongoClient(new MongoClientURI (mongoURL));
        MongoDatabase db = mongo.getDatabase(mongoDB);

        try {
           db.getCollection("continuousfilters");
        } catch (Exception e) {
          //System.out.println("Mongo is down");
          mongo.close();
          return Response.status(Response
                            .Status.INTERNAL_SERVER_ERROR).build();

        }
            
        MongoCollection<Document> coll = db.getCollection("continuousfilters");
        
      
        Bson filter = Filters.eq("id",idjo );
        
        FindIterable<Document> iterable = coll.find(filter);
        
        String cepInstance;
        
        CEP cepProcess = new CEP();
        
        if (iterable!= null && iterable.first()!=null){
            Document doc = iterable.first();
            cepInstance = doc.getString("cepinstance");
                
            MongoCollection<Document> collInstances = db.getCollection("cepinstances");

            ObjectId ci = new ObjectId(cepInstance);
            Bson filterInstances = Filters.eq("_id",ci);

            FindIterable<Document> iterable2 = collInstances.find(filterInstances);
        
            if (iterable2!= null){
                Document doc2 = iterable2.first();
                cepProcess.PID = doc2.getInteger("PID");
                cepProcess.fileName = doc2.getString("fileName");
                cepProcess.cepFolder = doc2.getString("cepFolder");
                cepProcess.type = CEP.CEPType.CONTINUOUS.toString();
                CepProcess cp = new CepProcess(null, null,null);
                cp.PID=doc2.getInteger("PID");
                
                cepProcess.cp =cp;
                
                if (!cepProcess.cepDispose()){
                    java.util.logging.Logger.getLogger
                    (ContinuosFiltering.class.getName()).log(Level.SEVERE, 
                    "bcep Instance not terminated" );
                }else{
    
                    Bson filter1 = Filters.eq("_id",ci);
                    Bson update =  new Document("$set"
                            ,new Document("status","terminated"));
                    UpdateOptions options = new UpdateOptions().upsert(false);
                    UpdateResult updateDoc =  db.getCollection("cepinstances")
                            .updateOne(filter1,update,options);
  
                };
                
            }
        }
    
        
        DeleteResult deleteResult = coll.deleteOne(eq("id",idjo));     
        
        if (deleteResult.getDeletedCount() < 1){
            return Response.status(Response.Status.NOT_FOUND).build();
        }else{
            
                return Response.status(Response.Status.OK)
                    .build();
        }
    }
    
    private JSONObject createOperationalStateObservation(
            String randomUUIDString) throws JSONException {
        JSONObject opState = new JSONObject();
        opState.put("@context",
                "http://vital-iot.eu/contexts/measurement.jsonld");
        opState.put("id", host+"/sensor/" 
                + randomUUIDString + "/observation/1");
        opState.put("type","ssn:Observation");
        opState.put("ssn:featureOfInterest", host+"/sensor/" + randomUUIDString);
        JSONObject property = new JSONObject();
        property.put("type","vital:OperationalState");
        opState.put("ssn:observationProperty",property);
        JSONObject resultTime = new JSONObject();
        Date date = new Date();
        resultTime.put("time:inXSDDateTime",getXSDDateTime(date));//check format
        opState.put("ssn:observationResultTime",resultTime);
        //"time:inXSDDateTime": "2015-10-14T11:59:11+02:00"
        JSONObject hasValue = new JSONObject();
        hasValue.put( "type","ssn:ObservationValue");
        hasValue.put( "value","vital:Running");
        JSONObject observationResult = new JSONObject();
        observationResult.put("ssn:hasValue",hasValue);
        observationResult.put("type","ssn:SensorOutput");
        opState.put("ssn:observationResult",observationResult);
        return opState;
    }
    
    private String getXSDDateTime(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return  dateFormat.format(date);
    }

	
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.vital.vitalcep.restApp.filteringApi;

import eu.vital.vitalcep.connectors.mqtt.MqttMsg;
import eu.vital.vitalcep.connectors.mqtt.TMessageProc;
import eu.vital.vitalcep.entities.dolceHandler.DolceSpecification;
import eu.vital.vitalcep.cep.CEP;
import eu.vital.vitalcep.cep.CepContainer;
import eu.vital.vitalcep.connectors.mqtt.MqttAllInOne;
import eu.vital.vitalcep.collector.decoder.Decoder;
import eu.vital.vitalcep.connectors.dms.DMSManager;
import eu.vital.vitalcep.publisher.MessageProcessor_publisher;
import eu.vital.vitalcep.publisher.encoder.Encoder;
import eu.vital.vitalcep.security.Security;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Context;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;
import eu.vital.vitalcep.conf.ConfigReader;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import org.bson.Document;

import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import java.util.logging.Level;
import javax.ws.rs.GET;

import org.apache.commons.lang.RandomStringUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class FilteringRestREST.
 */
@Path("filtering")
public class StaticFiltering {

    /** The Constant logger. */
    final static Logger logger = Logger.getLogger(StaticFiltering.class);
       
    private final String host;
    private final String mongoURL;
    private final String mongoDB;
    
    private String dmsURL;
    private String cookie;
    private String confFile;
    
    public StaticFiltering() throws IOException {
    
        ConfigReader configReader = ConfigReader.getInstance();
        
        mongoURL = configReader.get(ConfigReader.MONGO_URL);
        mongoDB = configReader.get(ConfigReader.MONGO_DB);
        dmsURL = configReader.get(ConfigReader.DMS_URL);
        host = configReader.get(ConfigReader.CEP_BASE_URL);
        confFile = configReader.get(ConfigReader.CEP_CONF_FILE);

    }
    
    
/**
 * Creates a filter.
 *
 * @param info
 * @return the filter id 
 * @throws java.io.IOException 
 */
@POST
@Path("filterstaticdata")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public Response filterstaticdata(String info,@Context HttpServletRequest req)
        throws IOException, UnsupportedEncodingException, NoSuchAlgorithmException {
        
    JSONObject jo = new JSONObject(info);

    if ( jo.has("dolceSpecification")&& jo.has("data")) { // && jo.has("data") for demo

        MongoClient mongo = new MongoClient(new MongoClientURI (mongoURL));
        MongoDatabase db = mongo.getDatabase(mongoDB);

        try {
           db.getCollection("staticdatafilters");
        } catch (Exception e) {
          //System.out.println("Mongo is down");
        	db = null;
            if (mongo!= null){
            	mongo.close();
            	mongo= null;
            }
          return Response.status(Response
                            .Status.INTERNAL_SERVER_ERROR).build();

        }

        if ( jo.has("dolceSpecification")) {

            //Filter oFilter = new Filter(filter);
            JSONObject dsjo = jo.getJSONObject("dolceSpecification");
            String str = dsjo.toString();//"{\"dolceSpecification\": "+ dsjo.toString()+"}";

            try{

                DolceSpecification ds = new DolceSpecification(str);

                if(!(ds instanceof DolceSpecification)) {
                    return Response.status(Response.Status.BAD_REQUEST).build();
                }

                String mqin = RandomStringUtils.randomAlphanumeric(8);
                String mqout = RandomStringUtils.randomAlphanumeric(8);
                
                JSONArray aData =  jo.getJSONArray("data");

                CEP cepProcess = new CEP();
                   
                if (!(cepProcess.CEPStart(CEP.CEPType.DATA, ds, mqin,
                        mqout, confFile, aData.toString(), null))){
                    return Response.status(Response
                            .Status.INTERNAL_SERVER_ERROR).build();
                }
                    
                String clientName = "collector_"+RandomStringUtils
                        .randomAlphanumeric(4);

                if (cepProcess.PID<1){
                    return Response.status(Response
                            .Status.INTERNAL_SERVER_ERROR).build();
                }
               

                UUID uuid = UUID.randomUUID();
                String randomUUIDString = uuid.toString();

                DBObject dbObject = createCEPFilterStaticSensorJsonld(info
                        ,randomUUIDString, jo, dsjo
                        ,"vital:CEPFilterStaticDataSensor");
                Document doc = new Document(dbObject.toMap());

                try{
                    db.getCollection("staticdatafilters").insertOne(doc);
                    String id = doc.get("_id").toString();

                }catch(MongoException ex
                        ){
                	db = null;
                    if (mongo!= null){
                    	mongo.close();
                    	mongo= null;
                    }
                    return Response.status(Response.Status.BAD_REQUEST)
                            .build();
                }

                JSONObject opState = createOperationalStateObservation(randomUUIDString);

                DBObject oPut =  (DBObject)JSON.parse(opState.toString());
                Document doc1 = new Document(oPut.toMap());

                try{
                    db.getCollection("staticdatafiltersobservations")
                            .insertOne(doc1);
                    String id = doc1.get("_id").toString();

                }catch(MongoException ex
                        ){
                	db = null;
                    if (mongo!= null){
                    	mongo.close();
                    	mongo= null;
                    }
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .build();
                }

                /////////////////////////////////////////////////////
                // creates client and messages process
                //
                MqttAllInOne oMqtt = new MqttAllInOne();
                TMessageProc MsgProcc = new TMessageProc();

/////////////////////////////////////////////////////////////////////////
                // PREPARING DOLCE INPUT
                Decoder decoder = new Decoder();
                ArrayList<String> simpleEventAL = decoder
                        .JsonldArray2DolceInput(aData);

                String sal =simpleEventAL.toString();
/////////////////////////////////////////////////////////////////////////////
                // SENDING TO MOSQUITTO
                oMqtt.sendMsg(MsgProcc, clientName, simpleEventAL,mqin,mqout);                          

/////////////////////////////////////////////////////////////////////////////
                //RECEIVING FROM MOSQUITO               
                ArrayList<MqttMsg> mesagges = MsgProcc.getMsgs();

                ArrayList<Document> outputL;
                outputL = new ArrayList<>();
                
                 Encoder encoder = new Encoder();

                outputL= encoder.dolceOutputList2ListDBObject
                    (mesagges, host, randomUUIDString);
                               
                String sOutput = "[";
                for (int i = 0; i < outputL.size(); i++) {
                    Document element = outputL.get(i);
                    
                    if(i==0){
                        sOutput = sOutput + element.toJson();
                    }
                     sOutput = sOutput + ","+element.toJson();
                }
                
                sOutput = sOutput + "]";
                                
                StringBuilder ck = new StringBuilder();
                
                 try {
                    Security slogin = new Security();
                  

                    Boolean token = slogin.login(req.getHeader("name")
                            ,req.getHeader("password"),false,ck);
                    if (!token){
                          return Response.status(Response.Status.UNAUTHORIZED)
                                  .build();
                    }
                    cookie = ck.toString(); 
                    
                    DMSManager oDMS = new DMSManager(dmsURL,cookie);
                    
                    MongoCollection<Document> collection = 
                            db.getCollection("staticdatafiltersobservations");
                    
                    if (outputL.size()>0){
                        collection.insertMany(outputL);
                        if (!oDMS.pushObservations(sOutput)){
                           java.util.logging.Logger.getLogger
                           (StaticFiltering.class.getName())
                                   .log(Level.SEVERE, "couldn't save to the DMS" );
                       }
                    }
                    
                } catch (KeyManagementException | KeyStoreException ex) {
                	db = null;
                    if (mongo!= null){
                    	mongo.close();
                    	mongo= null;
                    }
                    java.util.logging.Logger.getLogger(MessageProcessor_publisher
                            .class.getName()).log(Level.SEVERE, null, ex);
                }
                
                CepContainer.deleteCepProcess(cepProcess.PID);
                
                if (!cepProcess.cepDispose()){
                    java.util.logging.Logger.getLogger
                           (StaticFiltering.class.getName())
                                   .log(Level.SEVERE, "couldn't terminate ucep" );
                }
                
                db = null;
                if (mongo!= null){
                	mongo.close();
                	mongo= null;
                }
                return Response.status(Response.Status.OK)
                        .entity(sOutput).build();

            }catch(IOException | JSONException | java.text.ParseException e){
            	db = null;
                if (mongo!= null){
                	mongo.close();
                	mongo= null;
                }
                 return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
    }   

    return Response.status(Response.Status.BAD_REQUEST).build();


    }   

    return Response.status(Response.Status.BAD_REQUEST).build();

}

    private JSONObject createOperationalStateObservation(String randomUUIDString) 
            throws JSONException {
        JSONObject opState = new JSONObject();
        opState.put("@context",
                "http://vital-iot.eu/contexts/measurement.jsonld");
        opState.put("id", host+"/sensor/" + randomUUIDString + "/observation/1");
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
        return  dateFormat.format(date);
    }
    
 private DBObject createCEPFilterStaticSensorJsonld(String info,
        String randomUUIDString, JSONObject jo, JSONObject dsjo,
        String type) throws JSONException {
        DBObject dbObject = (DBObject) JSON.parse(info);

        dbObject.removeField("id");
        dbObject.put("@context","http://vital-iot.eu/contexts/sensor.jsonld");
        dbObject.put("id", host+"/sensor/" + randomUUIDString);
        dbObject.put("name",jo.getString("name") );
        dbObject.put("type", "vital:"+type);
        //dbObject.put("type", "vital:CEPSensor");
        dbObject.put("description",jo.getString("description") );
        //demo
//                        JSONArray data =  jo.getJSONArray("data");
//                        dbObject.put("data",data.toString());
        JSONArray compl = dsjo.getJSONArray("complex");
        JSONArray observes =  new JSONArray();
        for (int i = 0; i < compl.length(); i++) {
            JSONObject oComplex = new JSONObject(
                    compl.get(i).toString());
            JSONObject oObserves = new JSONObject();
            oObserves.put("type", "vital:ComplexEvent");
            //oObserves.put("uri",  host.toString()
            //        +"/sensor/"+randomUUIDString
            //        +"/"+oComplex.getString("id").toString());
            oObserves.put("id", host+"/sensor/" + randomUUIDString + "/" 
                    + oComplex.getString("id").toString());
            observes.put(oObserves);
        }
        DBObject dbObject2 = (DBObject)JSON.parse(observes
                .toString());
        dbObject.put("ssn:observes",dbObject2);
        dbObject.put("status","vital:running");
        return dbObject;
    }
 
/**
 * Gets a filter.
 *
 * @param info
     * @param req
 * @return the filter 
 * @throws java.io.IOException 
 */
@POST
@Path("filterstaticquery")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public Response filterstaticquery(String info,@Context HttpServletRequest req) throws IOException {
 
    JSONObject jo = new JSONObject(info);

    if ( jo.has("dolceSpecification")&& jo.has("query")) {
        // && jo.has("data") for demo
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
        
        MongoClient mongo = new MongoClient(new MongoClientURI (mongoURL));
        MongoDatabase db = mongo.getDatabase(mongoDB);

        try {
           db.getCollection("staticqueryfilters");
        } catch (Exception e) {
          //System.out.println("Mongo is down");
        	db = null;
            if (mongo!= null){
            	mongo.close();
            	mongo= null;
            }
          return Response.status(Response
                            .Status.INTERNAL_SERVER_ERROR).build();

        }

        if ( jo.has("dolceSpecification")) {

            //Filter oFilter = new Filter(filter);
            JSONObject dsjo = jo.getJSONObject("dolceSpecification");
            String str = dsjo.toString();//"{\"dolceSpecification\": "+ dsjo.toString()+"}";

            try{

                DolceSpecification ds = new DolceSpecification(str);

                if(!(ds instanceof DolceSpecification)) {
                    return Response.status(Response.Status.BAD_REQUEST).build();
                }

                String mqin = RandomStringUtils.randomAlphanumeric(8);
                String mqout = RandomStringUtils.randomAlphanumeric(8);

                CEP cepProcess = new CEP();
                   
                if (!(cepProcess.CEPStart(CEP.CEPType.QUERY, ds, mqin,
                        mqout, confFile, jo.getString("query"), null))){
                    return Response.status(Response
                            .Status.INTERNAL_SERVER_ERROR).build();
                }


                String clientName =  "collector_"+RandomStringUtils
                        .randomAlphanumeric(4);

                if (cepProcess.PID<1){
                    return Response.status(Response
                            .Status.INTERNAL_SERVER_ERROR).build();
                }

                UUID uuid = UUID.randomUUID();
                String randomUUIDString = uuid.toString();

                DBObject dbObject = createCEPFilterStaticSensorJsonld(info
                    , randomUUIDString, jo, dsjo
                    ,"vital:CEPFilterStaticQuerySensor");
                Document doc = new Document(dbObject.toMap());

                try{
                    db.getCollection("staticqueryfilters").insertOne(doc);
                    String id = doc.get("_id").toString();

                }catch(MongoException ex
                        ){db = null;
                        if (mongo!= null){
                        	mongo.close();
                        	mongo= null;
                        }
                    return Response.status(Response.Status.BAD_REQUEST)
                            .build();
                }

                JSONObject opState = createOperationalStateObservation(
                        randomUUIDString);

                DBObject oPut =  (DBObject)JSON.parse(opState.toString());
                Document doc1 = new Document(oPut.toMap());

                try{
                    db.getCollection("staticqueryfiltersobservations")
                            .insertOne(doc1);
                    String id = doc1.get("_id").toString();

                }catch(MongoException ex
                        ){
                	db = null;
                    if (mongo!= null){
                    	mongo.close();
                    	mongo= null;
                    }
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .build();
                }

                /////////////////////////////////////////////////////
                // creates client and messages process
                //
                MqttAllInOne oMqtt = new MqttAllInOne();
                TMessageProc MsgProcc = new TMessageProc();
                
                JSONArray aData =  new JSONArray();
                                
                 try {
                                        
                    DMSManager oDMS = new DMSManager(dmsURL,cookie);
        
                    aData = oDMS.getObservations(jo.getString("query"));
                    
                } catch (KeyManagementException | KeyStoreException ex) {
                    java.util.logging.Logger.getLogger(StaticFiltering
                            .class.getName()).log(Level.SEVERE, null, ex);
                }
	

                //DMSManager oDMS = new DMSManager(dmsURL,req.getHeader("vitalAccessToken"));
      
/////////////////////////////////////////////////////////////////////////
                // PREPARING DOLCE INPUT
                Decoder decoder = new Decoder();
                ArrayList<String> simpleEventAL = decoder
                        .JsonldArray2DolceInput(aData);
                
/////////////////////////////////////////////////////////////////////////////
                // SENDING TO MOSQUITTO
                oMqtt.sendMsg(MsgProcc, clientName, simpleEventAL,mqin,mqout);

/////////////////////////////////////////////////////////////////////////////
                //RECEIVING FROM MOSQUITO
                ArrayList<MqttMsg> mesagges = MsgProcc.getMsgs();

                //FORMATTING OBSERVATIONS OUTPUT
                Encoder encoder = new Encoder();
                
                ArrayList<Document> outputL;
                outputL = new ArrayList<>();
                
                outputL= encoder.dolceOutputList2ListDBObject
                    (mesagges, host, randomUUIDString);
                

                String sOutput = "[";
                for (int i = 0; i < outputL.size(); i++) {
                    Document element = outputL.get(i);
                    
                    if(i==0){
                        sOutput = sOutput + element.toJson();
                    }
                     sOutput = sOutput + ","+element.toJson();
                }
                
                sOutput = sOutput + "]";
               
                try {

                    DMSManager pDMS = new DMSManager(dmsURL,cookie);
                    
                     MongoCollection<Document> collection = 
                            db.getCollection("staticqueryfiltersobservations");
                    
                    if (outputL.size()>0){
                     collection.insertMany(outputL);
                     if (!pDMS.pushObservations(sOutput)){
                        java.util.logging.Logger.getLogger
                        (StaticFiltering.class.getName())
                                .log(Level.SEVERE, "coudn't save to the DMS" );
                        }
                    }
        
                    
                } catch (IOException | KeyManagementException 
                        | NoSuchAlgorithmException | KeyStoreException ex) {
                	db = null;
                    if (mongo!= null){
                    	mongo.close();
                    	mongo= null;
                    }
                    java.util.logging.Logger.getLogger
                        (StaticFiltering.class.getName())
                            .log(Level.SEVERE, null, ex);
                }
                
                CepContainer.deleteCepProcess(cepProcess.PID);
                             
                if (!cepProcess.cepDispose()){
                     java.util.logging.Logger.getLogger
                    (StaticFiltering.class.getName()).log(Level.SEVERE, 
                "bcep Instance not terminated" );
                };
                db = null;
                if (mongo!= null){
                	mongo.close();
                	mongo= null;
                }
                    return Response.status(Response.Status.OK)
                        .entity(sOutput).build();

            }catch(IOException | JSONException | NoSuchAlgorithmException 
                    | java.text.ParseException e){
            	db = null;
                if (mongo!= null){
                	mongo.close();
                	mongo= null;
                }
                 return Response.status(Response.Status
                         .INTERNAL_SERVER_ERROR).build();
            }
    }   

    return Response.status(Response.Status.BAD_REQUEST).build();


    }   

    return Response.status(Response.Status.BAD_REQUEST).build();
    
}

/*
     * test launcher
     */
    @POST
    @Path("test")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response test_luncher(String info,@Context HttpServletRequest req){
     
      StringBuilder ck = new StringBuilder();
         Security slogin = new Security();
                   
     JSONArray aData =  new JSONArray();
     JSONObject credentials = new JSONObject();
     
     Boolean token = slogin.login("elisa", "elisotas1",false,ck);
//            credentials.put("username", req.getHeader("trustUser"));
//            credentials.put("password", req.getHeader("querty1234"));
    if (!token){
          return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    this.cookie = ck.toString();  


     
    String body = info;

    try {
             
             DMSManager oDMS = new DMSManager(dmsURL,cookie);
 
             aData = oDMS.getSystems(body);
             
         } catch (KeyManagementException | KeyStoreException | NoSuchAlgorithmException | IOException ex) {
             java.util.logging.Logger.getLogger(StaticFiltering
                     .class.getName()).log(Level.SEVERE, null, ex);
         }

     return Response.status(Response.Status.OK).build();
     
    }

}

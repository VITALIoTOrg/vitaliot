/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.vital.vitalcep.restApp.vuaippi;

import com.mongodb.BasicDBList;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Context;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;


import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Projections.excludeId;
import static com.mongodb.client.model.Projections.fields;
import com.mongodb.util.JSON;
import eu.vital.vitalcep.collector.listener.DMSListener;
import eu.vital.vitalcep.conf.ConfigReader;
import eu.vital.vitalcep.security.Security;
import org.bson.Document;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

// TODO: Auto-generated Javadoc
/**
 * The Class FilteringRestREST.
 */
@Path("sensor")
public class Sensor {

    /** The Constant logger. */
    final static Logger logger = Logger.getLogger(Sensor.class);
    
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

    private final String host;
    
    private final String mongoURL;
    
    private final String mongoDB;
  
    public Sensor() throws IOException {

         ConfigReader configReader = ConfigReader.getInstance();
        
        mongoURL = configReader.get(ConfigReader.MONGO_URL);
        mongoDB = configReader.get(ConfigReader.MONGO_DB);
        host = configReader.get(ConfigReader.CEP_BASE_URL);

    }
    
     /**
     * Gets sensors metadata .
     *
     * @param info
     * @param req
     * @return the metadata of the sensors 
     * @throws java.io.FileNotFoundException 
     */
    @POST
    @Path("metadata")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensorMetadata(String info,
            @Context HttpServletRequest req) throws FileNotFoundException, 
            IOException {
        
        StringBuilder ck = new StringBuilder();
        Security slogin = new Security();
                  
        Boolean token = slogin.login(req.getHeader("name")
                ,req.getHeader("password"),false,ck);
        if (!token){
              return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        
        MongoClient mongo = new MongoClient(new MongoClientURI(mongoURL));

        MongoDatabase db = mongo.getDatabase(mongoDB);
                  
       
        
        try{
            JSONObject filter = new JSONObject(info);
            
            if(!filter.has("type")&& !filter.has("id") ){
        
            final JSONArray sensorspool1 = new JSONArray();    
            // create an empty query
            BasicDBObject query = new BasicDBObject(); 
            BasicDBObject fields = new BasicDBObject().append("_id",false)
                    .append("query",false).append("data",false)
                    .append("complexEvent", false)
                    .append("source",false).append("event",false)
                    .append("dolceSpecification",false); 
                    
            FindIterable<Document> coll = db.getCollection("continuousfilters")
                .find(query).projection(fields);
        
            coll.forEach(new Block<Document>() {
                @Override
                public void apply(final Document document) {
                    String aux = document.toJson();
                    JSONObject sensoraux = new JSONObject(aux);  
                    sensoraux.put("status","vital:Running");
                    sensorspool1.put(sensoraux);
                }
            });

            
            BasicDBObject query3 = new BasicDBObject(); 

            BasicDBObject fields3 = new BasicDBObject().append("_id", false)
                    .append("query", false).append("data", false)
                    .append("complexEvent", false)
                    .append("source", false).append("event", false)
                    .append("dolceSpecification", false);
            
            FindIterable<Document> coll3 = db.getCollection("staticdatafilters")
                        .find(query3).projection(fields3);

            coll3.forEach(new Block<Document>() {
                @Override
                public void apply(final Document document) {
                    String aux = document.toJson();
                    JSONObject sensoraux = new JSONObject(aux) ;  
                    sensoraux.put("status","vital:Running");
                    sensorspool1.put(sensoraux);
                }
            });
            

                            
            BasicDBObject query4 = new BasicDBObject(); 

            BasicDBObject fields4 = new BasicDBObject().append("_id", false)
                    .append("query", false).append("data", false)
                    .append("complexEvent", false)
                    .append("source", false).append("event", false)
                    .append("dolceSpecification", false);
            
            FindIterable<Document> coll4 = db.getCollection("staticqueryfilters")
                    .find(query4).projection(fields4);

            coll4.forEach(new Block<Document>() {
                @Override
                public void apply(final Document document) {
                    String aux = document.toJson();
                    JSONObject sensoraux = new JSONObject(aux);  
                    sensoraux.put("status","vital:Running");
                    sensorspool1.put(sensoraux);
                }
            });


            // create an empty query
            BasicDBObject fieldscep = new BasicDBObject().append("_id",false)
                    .append("source",false).append("query",false)
                    .append("complexEvent", false)
                    .append("data",false).append("event",false)
                    .append("dolceSpecification",false);
            
            FindIterable<Document> coll2 = db.getCollection("cepicos")
                    .find(query).projection(fieldscep);

            coll2.forEach(new Block<Document>() {
                @Override
                public void apply(final Document document) {
                    String aux = document.toJson();
                    JSONObject sensoraux = new JSONObject(aux);  
                    sensoraux.put("status","vital:Running");
                    sensorspool1.put(sensoraux);
                }
            });
             

            JSONObject monSensor = new JSONObject();

            monSensor.put("@context","http://vital-iot.eu/contexts/sensor.jsonld");
            monSensor.put("id",host+"/sensor/1");
            monSensor.put("type","vital:MonitoringSensor");
            monSensor.put("name","CEP System Monitoring Sensor");
            monSensor.put("description",
                    "A virtual sensor that monitors the operationalstate of the CEP system");

            JSONObject observesItem1 = new JSONObject();

            observesItem1.put("type","vital:OperationalState");
            observesItem1.put("id",host+"/sensor/1/operationalState");


            JSONObject observesItem2 = new JSONObject();

            observesItem2.put("type","vital:SysUptime");
            observesItem2.put("id",host+"/sensor/1/sysUptime");


            JSONObject observesItem3 = new JSONObject();

            observesItem3.put("type","vital:SysLoad");
            observesItem3.put("id",host+"/sensor/1/sysLoad");

            JSONObject observesItem4 = new JSONObject();

            observesItem4.put("type","vital:errors");
            observesItem4.put("id",host+"/sensor/1/errors");

            JSONArray observes = new JSONArray();

            observes.put(observesItem1);
            observes.put(observesItem2);
            observes.put(observesItem3);
            //observes.put(observesItem4);     

            monSensor.put("ssn:observes",observes);
            monSensor.put("status", "vital:Running");

            sensorspool1.put(monSensor);
            
            return Response.status(Response.Status.OK)
                                .entity(sensorspool1.toString()).build();
            
        }else if(filter.has("type")){
            JSONArray filteredSensors = new JSONArray();
            final JSONArray sensorspool = new JSONArray();  
            
            JSONArray types = filter.getJSONArray("type");
            for (int i = 0; i < types.length(); i++) {

                String type = types.getString(i);

                switch (type) {
                case CEPICOSENSOR_TYPE: case "vital:CEPSensor": case "CEPSensor":

                    // create an empty query
                    BasicDBObject fieldscep = new BasicDBObject().append("_id",false)
                            .append("source",false).append("query",false)
                            .append("complexEvent", false)
                            .append("data",false).append("event",false)
                            .append("dolceSpecification",false);
                    BasicDBObject query = new BasicDBObject(); 
                    
                    FindIterable<Document> coll2 = db.getCollection("cepicoss")
                            .find(query).projection(fieldscep);

                    coll2.forEach(new Block<Document>() {
                        @Override
                        public void apply(final Document document) {
                            String aux = document.toJson();
                            JSONObject sensoraux = new JSONObject(aux);  
                            sensoraux.put("status","vital:Running");
                            sensorspool.put(sensoraux);
                        }
                    });

                    break;
                case MONITORINGSENSOR_TYPE:
                    JSONObject monSensor = new JSONObject();

                    monSensor.put("@context","http://vital-iot.eu/contexts/sensor.jsonld");
                    monSensor.put("id",host+"/sensor/1");
                    monSensor.put("type","vital:MonitoringSensor");
                    monSensor.put("name","CEP System Monitoring Sensor");
                    monSensor.put("description",
                            "A virtual sensor that monitors the operationalstate of the CEP system");

                    JSONObject observesItem1 = new JSONObject();

                    observesItem1.put("type","vital:OperationalState");
                    observesItem1.put("id",host+"/sensor/1/operationalState");


                    JSONObject observesItem2 = new JSONObject();

                    observesItem2.put("type","vital:SysUptime");
                    observesItem2.put("id",host+"/sensor/1/sysUptime");


                    JSONObject observesItem3 = new JSONObject();

                    observesItem3.put("type","vital:SysLoad");
                    observesItem3.put("id",host+"/sensor/1/sysLoad");

                    JSONObject observesItem4 = new JSONObject();

                    observesItem4.put("type","vital:errors");
                    observesItem4.put("id",host+"/sensor/1/errors");

                    JSONArray observes = new JSONArray();

                    observes.put(observesItem1);
                    observes.put(observesItem2);
                    observes.put(observesItem3);
                    //observes.put(observesItem4);     

                    monSensor.put("ssn:observes",observes);
                    monSensor.put("status", "vital:Running");

                    sensorspool.put(monSensor);
                    filteredSensors.put(monSensor);
                    break;
                    
                case CEPFILTERSTATICDATASENSOR_TYPE: case "vital:CEPFilterStaticDataSensor":
                case "CEPFilterStaticDataSensor":
                    
                    BasicDBObject query3 = new BasicDBObject(); 

                    BasicDBObject fields3 = new BasicDBObject().append("_id", false)
                            .append("query", false).append("data", false)
                            .append("complexEvent", false)
                            .append("source", false).append("event", false)
                            .append("dolceSpecification", false);

                    FindIterable<Document> coll3 = db
                            .getCollection("staticdatafilters")
                            .find(query3).projection(fields3);
                       
                    coll3.forEach(new Block<Document>() {
                        @Override
                        public void apply(final Document document) {
                            String aux = document.toJson();
                            JSONObject sensoraux = new JSONObject(aux);  
                            sensoraux.put("status","vital:Running");
                            sensorspool.put(sensoraux);
                        }
                    });
                    
                    break;
                    
                case CEPFILTERSTATICQUERYSENSOR_TYPE: 
                case "vital:CEPFilterStaticQuerySensor": 
                case "CEPFilterStaticQuerySensor":
                    BasicDBObject query4 = new BasicDBObject(); 

                    BasicDBObject fields4 = new BasicDBObject().append("_id", false)
                            .append("query", false).append("data", false)
                            .append("complexEvent", false)
                            .append("source", false).append("event", false)
                            .append("dolceSpecification", false);
                    
                    FindIterable<Document> coll4 = db
                            .getCollection("staticqueryfilters")
                            .find(query4).projection(fields4);

                    coll4.forEach(new Block<Document>() {
                        @Override
                        public void apply(final Document document) {
                            String aux = document.toJson();
                            JSONObject sensoraux = new JSONObject(aux);  
                            sensoraux.put("status","vital:Running");
                            sensorspool.put(sensoraux);
                        }
                    });

                    break;
                    
                case CEPFILTERSENSOR_TYPE: case "vital:CEPFilterSensor":
                case "CEPFilterSensor":
                    
                    // create an empty query
                    BasicDBObject query2 = new BasicDBObject(); 
                    BasicDBObject fields = new BasicDBObject().append("_id",false)
                            .append("query",false).append("data",false)
                            .append("complexEvent", false)
                            .append("source",false).append("event",false)
                            .append("dolceSpecification",false); 

                    FindIterable<Document> coll = db.getCollection("continuousfilters")
                            .find(query2).projection(fields);

                    coll.forEach(new Block<Document>() {
                        @Override
                        public void apply(final Document document) {
                            String aux = document.toJson();
                            JSONObject sensoraux = new JSONObject(aux);  
                            sensoraux.put("status","vital:Running");
                            sensorspool.put(sensoraux);
                        }
                    });

                    break;
                    
                default: 
                     return Response.status(Response.Status.BAD_REQUEST)
                        .build();
                }         
            }
            return Response.status(Response.Status.OK)
                                .entity(sensorspool.toString()).build();
            
        }else if (filter.has("id")){
            final JSONArray sensorspool2 = new JSONArray();    
            JSONArray ids = filter.getJSONArray("id");
            for (int i = 0; i < ids.length(); i++) {

                String id = ids.getString(i);
                // create an empty query
                BasicDBObject query = new BasicDBObject("id",id); 
                BasicDBObject fields = new BasicDBObject().append("_id",false)
                        .append("query",false).append("data",false)
                        .append("complexEvent", false)
                        .append("source",false).append("event",false)
                        .append("dolceSpecification",false); 
                
                FindIterable<Document> coll = db.getCollection("filters")
                        .find(query).projection(fields);

                coll.forEach(new Block<Document>() {
                        @Override
                        public void apply(final Document document) {
                            String aux = document.toJson();
                            JSONObject sensoraux = new JSONObject(aux);  
                            sensoraux.put("status","vital:Running");
                            sensorspool2.put(sensoraux);
                        }
                    });


                BasicDBObject fields3 = new BasicDBObject().append("_id", false)
                        .append("query", false).append("data", false)
                        .append("complexEvent", false)
                        .append("source", false).append("event", false)
                        .append("dolceSpecification", false);
                
                FindIterable<Document> coll3 = db.getCollection("staticdatafilters")
                        .find(query).projection(fields3);

                coll3.forEach(new Block<Document>() {
                        @Override
                        public void apply(final Document document) {
                            String aux = document.toJson();
                            JSONObject sensoraux = new JSONObject(aux);  
                            sensoraux.put("status","vital:Running");
                            sensorspool2.put(sensoraux);
                        }
                });

                BasicDBObject fields4 = new BasicDBObject().append("_id", false)
                        .append("query", false).append("data", false)
                        .append("complexEvent", false)
                        .append("source", false).append("event", false)
                        .append("dolceSpecification", false);
                
                FindIterable<Document> coll4 = db.getCollection("staticqueryfilters")
                        .find(query).projection(fields4);


                coll4.forEach(new Block<Document>() {
                        @Override
                        public void apply(final Document document) {
                            String aux = document.toJson();
                            JSONObject sensoraux = new JSONObject(aux);  
                            sensoraux.put("status","vital:Running");
                            sensorspool2.put(sensoraux);
                        }
                });


                // create an empty query
                BasicDBObject fieldscep = new BasicDBObject().append("_id",false)
                        .append("source",false).append("query",false)
                        .append("complexEvent", false)
                        .append("data",false).append("event",false)
                        .append("dolceSpecification",false);
                
                FindIterable<Document> coll2 = db.getCollection("ceps")
                         .find(query).projection(fieldscep);

                coll2.forEach(new Block<Document>() {
                        @Override
                        public void apply(final Document document) {
                            String aux = document.toJson();
                            JSONObject sensoraux = new JSONObject(aux);  
                            sensoraux.put("status","vital:Running");
                            sensorspool2.put(sensoraux);
                        }
                });
                
                if (id.equals(host+"/sensor/1")){
                    JSONObject monSensor = new JSONObject();

                    monSensor.put("@context","http://vital-iot.eu/contexts/sensor.jsonld");
                    monSensor.put("id",host+"/sensor/1");
                    monSensor.put("type","vital:MonitoringSensor");
                    monSensor.put("name","CEP System Monitoring Sensor");
                    monSensor.put("description",
                            "A virtual sensor that monitors the operationalstate of the CEP system");

                    JSONObject observesItem1 = new JSONObject();

                    observesItem1.put("type","vital:OperationalState");
                    observesItem1.put("id",host+"/sensor/1/operationalState");


                    JSONObject observesItem2 = new JSONObject();

                    observesItem2.put("type","vital:SysUptime");
                    observesItem2.put("id",host+"/sensor/1/sysUptime");


                    JSONObject observesItem3 = new JSONObject();

                    observesItem3.put("type","vital:SysLoad");
                    observesItem3.put("id",host+"/sensor/1/sysLoad");

                    JSONObject observesItem4 = new JSONObject();

                    observesItem4.put("type","vital:errors");
                    observesItem4.put("id",host+"/sensor/1/errors");

                    JSONArray observes = new JSONArray();

                    observes.put(observesItem1);
                    observes.put(observesItem2);
                    observes.put(observesItem3);
                    //observes.put(observesItem4);     

                    monSensor.put("ssn:observes",observes);
                    monSensor.put("status", "vital:Running");

                    sensorspool2.put(monSensor);
              
                }    
            }
              return Response.status(Response.Status.OK)
                                .entity(sensorspool2.toString()).build();
        }
    } catch (JSONException ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .build();
    }
        
        
    return Response.status(Response.Status.BAD_REQUEST)
                    .build();      
        
      
         
    }
    
    
     /**
     * Gets sensors status .
     *
     * @param info
     * @return the metadata of the sensors 
     * @throws java.io.FileNotFoundException 
     */
    @POST
    @Path("status")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensorStatus(String info,
            @Context HttpServletRequest req) throws FileNotFoundException, 
            IOException {

        StringBuilder ck = new StringBuilder();
        Security slogin = new Security();
                  
        Boolean token = slogin.login(req.getHeader("name")
                ,req.getHeader("password"),false,ck);
        if (!token){
              return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        
        MongoClient mongo = new MongoClient(new MongoClientURI(mongoURL));

        MongoDatabase db = mongo.getDatabase(mongoDB);

        final JSONArray sensorspool = new JSONArray();                 

        try{
            JSONObject filter = new JSONObject(info);
            
            if(filter.has("id") ){
                
                JSONArray ids = filter.getJSONArray("id");
                for (int i = 0; i < ids.length(); i++) {
                    
                    String id = ids.getString(i);
                    BasicDBObject query = new BasicDBObject
                        ("ssn:featureOfInterest",id);
                    BasicDBObject fields = new BasicDBObject().append("_id",false);
                    FindIterable<Document> coll = db.getCollection("filtersobservations")
                        .find(query).projection(fields);
                   
                                       
                    coll.forEach(new Block<Document>() {
                        @Override
                        public void apply(final Document document) {
                            String aux = document.toJson();
                            JSONObject sensoraux = new JSONObject(aux);
                            sensorspool.put(sensoraux);
                        }
                    });

                    
                     BasicDBObject fields3 = new BasicDBObject().append("_id", false)
                            .append("query", false).append("data", false)
                            .append("complexEvent", false)
                            .append("source", false).append("event", false)
                            .append("dolceSpecification", false);
                    
                    FindIterable<Document> coll3 = db
                        .getCollection("staticdatafiltersobservations")
                        .find(query).projection(fields3);
                    //DBCollection coll3 = db.getCollection("staticdatafiltersobservations");
                    
                    coll3.forEach(new Block<Document>() {
                        @Override
                        public void apply(final Document document) {
                            String aux = document.toJson();
                            JSONObject sensoraux = new JSONObject(aux);
                            sensorspool.put(sensoraux);
                        }
                    });
                   
                    
                   
                    BasicDBObject fields4 = new BasicDBObject().append("_id", false)
                            .append("query", false).append("data", false)
                            .append("complexEvent", false)
                            .append("source", false).append("event", false)
                            .append("dolceSpecification", false);
                    
                    FindIterable<Document> coll4 = db
                        .getCollection("staticqueryfiltersobservations")
                        .find(query).projection(fields4);
                    
                    //DBCollection coll4 = db.getCollection("staticqueryfiltersobservations");
                    
                    coll4.forEach(new Block<Document>() {
                        @Override
                        public void apply(final Document document) {
                            String aux = document.toJson();
                            JSONObject sensoraux = new JSONObject(aux);
                            sensorspool.put(sensoraux);
                        }
                    });
                    

                    

                    BasicDBObject fieldscep = new BasicDBObject().append("_id",false)
                            .append("source",false).append("query",false)
                            .append("complexEvent", false)
                            .append("data",false).append("event",false)
                            .append("dolceSpecification",false);
                    //DBCollection coll2 = db.getCollection("cepsobservations");
                    // create an empty query
                    FindIterable<Document> coll2 = db
                        .getCollection("cepsobservations")
                        .find(query).projection(fieldscep);
                    
                    coll2.forEach(new Block<Document>() {
                        @Override
                        public void apply(final Document document) {
                            String aux = document.toJson();
                            JSONObject sensoraux = new JSONObject(aux);
                            sensorspool.put(sensoraux);
                        }
                    });
                    

                    
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
                    
                    if (id.equals(host+"/sensor/1")){
                        JSONObject opState = new JSONObject();
                        
                        opState.put("@context",
                                "http://vital-iot.eu/contexts/measurement.jsonld");
                        
                        opState.put("id",host+"/sensor/1/observation/1" );
                        
                        opState.put("type","ssn:Observation");
                        
                        opState.put("ssn:featureOfInterest",host+"/sensor/1");
                        
                        JSONObject property = new JSONObject();
                        property.put("type","vital:OperationalState");
                        opState.put("ssn:observationProperty",property);
                        
                        JSONObject resultTime = new JSONObject();
                        
                        Date date = new Date();
                        
                        resultTime.put("time:inXSDDateTime",dateFormat.format(date));//check format
                        
                        opState.put("ssn:observationResultTime",resultTime);
                        //"time:inXSDDateTime": "2015-10-14T11:59:11+02:00"
                        
                        JSONObject hasValue = new JSONObject();
                        hasValue.put( "type","ssn:ObservationValue");
                        hasValue.put( "value","vital:Running");
                        JSONObject observationResult = new JSONObject();
                        observationResult.put("ssn:hasValue",hasValue);
                        observationResult.put("type","ssn:SensorOutput");
                        opState.put("ssn:observationResult",observationResult);
                        sensorspool.put(opState);
                        
                        JSONObject sysUpTime = new JSONObject();
                        
                        sysUpTime.put("@context","http://vital-iot.eu/contexts/measurement.jsonld");
                        sysUpTime.put("id",host+"/sensor/1/observation/2");
                        sysUpTime.put("type","ssn:Observation");
                        
                        JSONObject property2 = new JSONObject();
                        property2.put("type","vital:SysUptime");
                        sysUpTime.put("ssn:observationProperty",property2);
                        
                        JSONObject resultTime2 = new JSONObject();
                        Date date2 = new Date();
                        
                        resultTime2.put("time:inXSDDateTime",dateFormat.format(date2));//check format
                        sysUpTime.put("ssn:observationResultTime",resultTime2);
                        sysUpTime.put("ssn:featureOfInterest",host+"/sensor/1");
                        
                        JSONObject hasValue2 = new JSONObject();
                        hasValue2.put( "type","ssn:ObservationValue");
                        RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
                        long uptime = rb.getUptime();
                        hasValue2.put( "value",""+uptime);
                        hasValue2.put( "qudt:unit","qudt:Milliseconds");
                        JSONObject observationResult2 = new JSONObject();
                        observationResult2.put("ssn:hasValue",hasValue2);
                        observationResult2.put("type","ssn:SensorOutput");
                        sysUpTime.put("ssn:observationResult",observationResult2);
                        sensorspool.put(sysUpTime);
                                              
                        JSONObject sysLoad = new JSONObject();
                        
                        sysLoad.put("@context","http://vital-iot.eu/contexts/measurement.jsonld");
                        sysLoad.put("id",host+"/sensor/1/observation/3");
                        sysLoad.put("type","ssn:Observation");
                        
                        JSONObject property3 = new JSONObject();
                        property3.put("type","vital:SysLoad");
                        sysLoad.put("ssn:observationProperty",property3);
                                                
                        JSONObject resultTime3 = new JSONObject();
                        
                        Date date3 = new Date();
                        
                        resultTime3.put("time:inXSDDateTime",dateFormat.format(date3));//check format
                        sysLoad.put("ssn:observationResultTime",resultTime3);
                        sysLoad.put("ssn:featureOfInterest",host+"/sensor/1");
                        
                        JSONObject hasValue3 = new JSONObject();
                        hasValue3.put( "type","ssn:ObservationValue");
                        hasValue3.put( "value",ThreadLocalRandom.current().nextInt(1, 5 + 1)+"%");
                        hasValue3.put( "qudt:unit","qudt:Percent");
                        JSONObject observationResult3 = new JSONObject();
                        observationResult3.put("ssn:hasValue",hasValue3);
                        observationResult3.put("type","ssn:SensorOutput");
                        sysLoad.put("ssn:observationResult",observationResult3);
                        sensorspool.put(sysLoad);
                    }
                }
            } else if(filter.has("type")){

            JSONArray types = filter.getJSONArray("type");
            for (int i = 0; i < types.length(); i++) {

                String type = types.getString(i);
                
                BasicDBObject query = new BasicDBObject(); 

                switch (type) {
                case CEPICOSENSOR_TYPE:

                    //DBCollection coll2 = db.getCollection("cepsobservations");
                    // create an empty query
                    BasicDBObject fieldscep = new BasicDBObject().append("_id",false)
                            .append("source",false).append("query",false)
                            .append("complexEvent", false)
                            .append("data",false).append("event",false)
                            .append("dolceSpecification",false);
                   
                    FindIterable<Document> coll2 = db
                        .getCollection("cepsobservations")
                        .find(query).projection(fieldscep);
                    
                    coll2.forEach(new Block<Document>() {
                        @Override
                        public void apply(final Document document) {
                            String aux = document.toJson();
                            JSONObject sensoraux = new JSONObject(aux);
                            sensorspool.put(sensoraux);
                        }
                    });
                    

                    break;
                case MONITORINGSENSOR_TYPE:
                    
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
                                     
                    JSONObject opState = new JSONObject();
                        
                    opState.put("@context",
                            "http://vital-iot.eu/contexts/measurement.jsonld");

                    opState.put("id",host+"/sensor/1/observation/1" );

                    opState.put("type","ssn:Observation");

                    opState.put("ssn:featureOfInterest",host+"/sensor/1");

                    JSONObject property = new JSONObject();
                    property.put("type","vital:OperationalState");
                    opState.put("ssn:observationProperty",property);

                    JSONObject resultTime = new JSONObject();

                    Date date = new Date();

                    resultTime.put("time:inXSDDateTime",dateFormat.format(date));//check format

                    opState.put("ssn:observationResultTime",resultTime);
                    //"time:inXSDDateTime": "2015-10-14T11:59:11+02:00"

                    JSONObject hasValue = new JSONObject();
                    hasValue.put( "type","ssn:ObservationValue");
                    hasValue.put( "value","vital:Running");
                    JSONObject observationResult = new JSONObject();
                    observationResult.put("ssn:hasValue",hasValue);
                    observationResult.put("type","ssn:SensorOutput");
                    opState.put("ssn:observationResult",observationResult);
                    sensorspool.put(opState);

                    JSONObject sysUpTime = new JSONObject();

                    sysUpTime.put("@context","http://vital-iot.eu/contexts/measurement.jsonld");
                    sysUpTime.put("id",host+"/sensor/1/observation/2");
                    sysUpTime.put("type","ssn:Observation");

                    JSONObject property2 = new JSONObject();
                    property2.put("type","vital:SysUptime");
                    sysUpTime.put("ssn:observationProperty",property2);

                    JSONObject resultTime2 = new JSONObject();
                    Date date2 = new Date();

                    resultTime2.put("time:inXSDDateTime",dateFormat.format(date2));//check format
                    sysUpTime.put("ssn:observationResultTime",resultTime2);
                    sysUpTime.put("ssn:featureOfInterest",host+"/sensor/1");

                    JSONObject hasValue2 = new JSONObject();
                    hasValue2.put( "type","ssn:ObservationValue");
                    RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
                    long uptime = rb.getUptime();
                    hasValue2.put( "value",""+uptime);
                    hasValue2.put( "qudt:unit","qudt:Milliseconds");
                    JSONObject observationResult2 = new JSONObject();
                    observationResult2.put("ssn:hasValue",hasValue2);
                    observationResult2.put("type","ssn:SensorOutput");
                    sysUpTime.put("ssn:observationResult",observationResult2);
                    sensorspool.put(sysUpTime);

                    JSONObject sysLoad = new JSONObject();

                    sysLoad.put("@context","http://vital-iot.eu/contexts/measurement.jsonld");
                    sysLoad.put("id",host+"/sensor/1/observation/3");
                    sysLoad.put("type","ssn:Observation");

                    JSONObject property3 = new JSONObject();
                    property3.put("type","vital:SysLoad");
                    sysLoad.put("ssn:observationProperty",property3);

                    JSONObject resultTime3 = new JSONObject();

                    Date date3 = new Date();

                    resultTime3.put("time:inXSDDateTime",dateFormat.format(date3));//check format
                    sysLoad.put("ssn:observationResultTime",resultTime3);
                    sysLoad.put("ssn:featureOfInterest",host+"/sensor/1");

                    JSONObject hasValue3 = new JSONObject();
                    hasValue3.put( "type","ssn:ObservationValue");
                    hasValue3.put( "value",ThreadLocalRandom.current().nextInt(1, 5 + 1)+"%");
                    hasValue3.put( "qudt:unit","qudt:Percent");
                    JSONObject observationResult3 = new JSONObject();
                    observationResult3.put("ssn:hasValue",hasValue3);
                    observationResult3.put("type","ssn:SensorOutput");
                    sysLoad.put("ssn:observationResult",observationResult3);
                    sensorspool.put(sysLoad);
                     break;
                case CEPFILTERSTATICDATASENSOR_TYPE:
                    //DBCollection coll3 = db.getCollection("staticdatafiltersobservations");

                     BasicDBObject fields3 = new BasicDBObject().append("_id", false)
                            .append("query", false).append("data", false)
                            .append("complexEvent", false)
                            .append("source", false).append("event", false)
                            .append("dolceSpecification", false);
                    FindIterable<Document> coll3 = db
                        .getCollection("staticdatafiltersobservations")
                        .find(query).projection(fields3);
                    
                    coll3.forEach(new Block<Document>() {
                        @Override
                        public void apply(final Document document) {
                            String aux = document.toJson();
                            JSONObject sensoraux = new JSONObject(aux);
                            sensorspool.put(sensoraux);
                        }
                    });


                    break;
                case CEPFILTERSTATICQUERYSENSOR_TYPE:
                    //DBCollection coll4 = db.getCollection("staticqueryfiltersobservations");

                    BasicDBObject fields4 = new BasicDBObject().append("_id", false)
                            .append("query", false).append("data", false)
                            .append("complexEvent", false)
                            .append("source", false).append("event", false)
                            .append("dolceSpecification", false);

                    FindIterable<Document> coll4 = db
                        .getCollection("staticqueryfiltersobservations")
                        .find(query).projection(fields4);
                    
                    coll4.forEach(new Block<Document>() {
                        @Override
                        public void apply(final Document document) {
                            String aux = document.toJson();
                            JSONObject sensoraux = new JSONObject(aux);
                            sensorspool.put(sensoraux);
                        }
                    });

                    break;
                case CEPFILTERSENSOR_TYPE:
                    //DBCollection coll = db.getCollection("filtersobservations");

                    BasicDBObject fields = new BasicDBObject().append("_id",false)
                            .append("query",false).append("data",false)
                            .append("complexEvent", false)
                            .append("source",false).append("event",false)
                            .append("dolceSpecification",false); 
                    FindIterable<Document> coll = db
                        .getCollection("filtersobservations")
                        .find(query).projection(fields);

                    coll.forEach(new Block<Document>() {
                        @Override
                        public void apply(final Document document) {
                            String aux = document.toJson();
                            JSONObject sensoraux = new JSONObject(aux);
                            sensorspool.put(sensoraux);
                        }
                    });

                    break;
                default: 
                     return Response.status(Response.Status.BAD_REQUEST)
                        .build();
                }         
            }
            }else {

                //DBCollection coll = db.getCollection("filtersobservations");
                // create an empty query
                BasicDBObject query = new BasicDBObject();
                BasicDBObject fields = new BasicDBObject().append("_id",false);
                FindIterable<Document> coll = db
                        .getCollection("filtersobservations")
                        .find(query).projection(fields);

                coll.forEach(new Block<Document>() {
                    @Override
                    public void apply(final Document document) {
                        JSONObject curr = new JSONObject();

                    curr.put("@context",
                            "http://vital-iot.eu/contexts/measurement.jsonld");

                    curr.put("id",document.get("id"));

                    curr.put("type","ssn:Observation");

                    curr.put("ssn:featureOfInterest",document.get("id"));

                    JSONObject property = new JSONObject();
                    property.put("type","vital:ComplexEvent");
                    curr.put("ssn:observationProperty",property);

                    JSONObject resultTime = new JSONObject();

                    DateFormat dateFormat = new SimpleDateFormat
                            ("yyyy-MM-dd'T'HH:mm:ssXXX");
                    Date date = new Date();

                    resultTime.put("time:inXSDDateTime",dateFormat.format(date));//check format

                    curr.put("ssn:observationResultTime",resultTime);
                    //"time:inXSDDateTime": "2015-10-14T11:59:11+02:00"

                    JSONObject hasValue = new JSONObject();
                    hasValue.put( "type","ssn:ObservationValue");
                    hasValue.put( "value","vital:Running");
                    JSONObject observationResult = new JSONObject();
                    observationResult.put("ssn:hasValue",hasValue);
                    observationResult.put("type","ssn:SensorOutput");
                    curr.put("ssn:observationResult",observationResult);

                    sensorspool.put(curr);
                    }
                });
                
                BasicDBObject fields3 = new BasicDBObject().append("_id", false);
                FindIterable<Document> coll3 = db
                        .getCollection("filtersobservations")
                        .find(query).projection(fields3);

                coll3.forEach(new Block<Document>() {
                    @Override
                    public void apply(final Document document) {
                        JSONObject curr = new JSONObject();
                        
                        curr.put("@context",
                                "http://vital-iot.eu/contexts/measurement.jsonld");
                        
                        curr.put("id",document.get("id"));
                        
                        curr.put("type","ssn:Observation");
                        
                        curr.put("ssn:featureOfInterest",document
                                .get("ssn:featureOfInterest"));
                        
                        JSONObject property = new JSONObject();
                        property.put("type","vital:ComplexEvent");
                        curr.put("ssn:observationProperty",property);
                        
                        JSONObject resultTime = new JSONObject();
                        
                        DateFormat dateFormat = new SimpleDateFormat
                                ("yyyy-MM-dd'T'HH:mm:ssXXX");
                        Date date = new Date();
                        
                        resultTime.put("time:inXSDDateTime",dateFormat.format(date));//check format
                        
                        curr.put("ssn:observationResultTime",resultTime);
                        //"time:inXSDDateTime": "2015-10-14T11:59:11+02:00"
                        
                        JSONObject hasValue = new JSONObject();
                        hasValue.put( "type","ssn:ObservationValue");
                        hasValue.put( "value","vital:Running");
                        JSONObject observationResult = new JSONObject();
                        observationResult.put("ssn:hasValue",hasValue);
                        observationResult.put("type","ssn:SensorOutput");
                        curr.put("ssn:observationResult",observationResult);
                        
                        sensorspool.put(curr);
                    }
                });

                BasicDBObject fields4 = new BasicDBObject().append("_id", false)
                        .append("query", false).append("data", false)
                        .append("complexEvent", false)
                        .append("source", false).append("event", false)
                        .append("dolceSpecification", false);
                FindIterable<Document> coll4 = db
                        .getCollection("staticqueryfiltersobservations")
                        .find(query).projection(fields4);
                    
                coll4.forEach(new Block<Document>() {
                    @Override
                    public void apply(final Document document) {
                        JSONObject curr = new JSONObject();
                        
                        curr.put("@context",
                                "http://vital-iot.eu/contexts/measurement.jsonld");
                        
                        curr.put("id",document.get("id"));
                        
                        curr.put("type","ssn:Observation");
                        
                        curr.put("ssn:featureOfInterest",document
                                .get("ssn:featureOfInterest"));
                        
                        JSONObject property = new JSONObject();
                        property.put("type","vital:ComplexEvent");
                        curr.put("ssn:observationProperty",property);
                        
                        JSONObject resultTime = new JSONObject();
                        
                        DateFormat dateFormat = new SimpleDateFormat
                                ("yyyy-MM-dd'T'HH:mm:ssXXX");
                        Date date = new Date();
                        
                        resultTime.put("time:inXSDDateTime",dateFormat.format(date));//check format
                        
                        curr.put("ssn:observationResultTime",resultTime);
                        //"time:inXSDDateTime": "2015-10-14T11:59:11+02:00"
                        
                        JSONObject hasValue = new JSONObject();
                        hasValue.put( "type","ssn:ObservationValue");
                        hasValue.put( "value","vital:Running");
                        JSONObject observationResult = new JSONObject();
                        observationResult.put("ssn:hasValue",hasValue);
                        observationResult.put("type","ssn:SensorOutput");
                        curr.put("ssn:observationResult",observationResult);
                        
                        sensorspool.put(curr);
                    }
                });

                BasicDBObject fieldscep = new BasicDBObject().append("_id",false);
                FindIterable<Document> coll2 = db
                       .getCollection("cepsobservations")
                       .find(query).projection(fieldscep);
                    
                coll2.forEach(new Block<Document>() {
                    @Override
                    public void apply(final Document document) {
                        if (document.containsKey("id")){
                            JSONObject curr = new JSONObject();

                            curr.put("@context",
                                    "http://vital-iot.eu/contexts/measurement.jsonld");

                            curr.put("id",document.get("id"));

                            curr.put("type","ssn:Observation");

                            curr.put("ssn:featureOfInterest",document
                                    .get("ssn:featureOfInterest"));//ver

                            JSONObject property = new JSONObject();
                            property.put("type","vital:ComplexEvent");
                            curr.put("ssn:observationProperty",property);

                            JSONObject resultTime = new JSONObject();

                            DateFormat dateFormat = new SimpleDateFormat
                                ("yyyy-MM-dd'T'HH:mm:ssXXX");
                            Date date = new Date();

                            resultTime.put("time:inXSDDateTime",dateFormat.format(date));//check format

                            curr.put("ssn:observationResultTime",resultTime);
                            //"time:inXSDDateTime": "2015-10-14T11:59:11+02:00"

                            JSONObject hasValue = new JSONObject();
                            hasValue.put( "type","ssn:ObservationValue");
                            hasValue.put( "value","vital:Running");
                            JSONObject observationResult = new JSONObject();
                            observationResult.put("ssn:hasValue",hasValue);
                            observationResult.put("type","ssn:SensorOutput");
                            curr.put("ssn:observationResult",observationResult);

                            sensorspool.put(curr);
                        }
                    }
                });
               
                JSONObject opState = new JSONObject();
                
                opState.put("@context",
                        "http://vital-iot.eu/contexts/measurement.jsonld");
                
                opState.put("id",host+"/sensor/1/observation/1" );
                
                opState.put("type","ssn:Observation");
                
                opState.put("ssn:featureOfInterest",host+"/sensor/1");
                
                JSONObject property = new JSONObject();
                property.put("type","vital:OperationalState");
                opState.put("ssn:observationProperty",property);
                
                JSONObject resultTime = new JSONObject();
                
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
                Date date = new Date();
                
                resultTime.put("time:inXSDDateTime",dateFormat.format(date));//check format
                
                opState.put("ssn:observationResultTime",resultTime);
                //"time:inXSDDateTime": "2015-10-14T11:59:11+02:00"
                
                JSONObject hasValue = new JSONObject();
                hasValue.put( "type","ssn:ObservationValue");
                hasValue.put( "value","vital:Running");
                JSONObject observationResult = new JSONObject();
                observationResult.put("ssn:hasValue",hasValue);
                observationResult.put("type","ssn:SensorOutput");
                opState.put("ssn:observationResult",observationResult);
                
                JSONObject sysUpTime = new JSONObject();
                
                sysUpTime.put("@context","http://vital-iot.eu/contexts/measurement.jsonld");
                sysUpTime.put("id",host+"/sensor/1/observation/2");
                sysUpTime.put("type","ssn:Observation");
                
                JSONObject property2 = new JSONObject();
                property2.put("type","vital:SysUptime");
                sysUpTime.put("ssn:observationProperty",property2);
                
                JSONObject resultTime2 = new JSONObject();
                Date date2 = new Date();
                
                resultTime2.put("time:inXSDDateTime",dateFormat.format(date2));//check format
                sysUpTime.put("ssn:observationResultTime",resultTime2);
                sysUpTime.put("ssn:featureOfInterest",host+"/sensor/1");
                
                JSONObject hasValue2 = new JSONObject();
                hasValue2.put( "type","ssn:ObservationValue");
                RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
                long uptime = rb.getUptime();
                hasValue2.put( "value",""+uptime);
                hasValue2.put( "qudt:unit","qudt:Milliseconds");
                JSONObject observationResult2 = new JSONObject();
                observationResult2.put("ssn:hasValue",hasValue2);
                observationResult2.put("type","ssn:SensorOutput");
                sysUpTime.put("ssn:observationResult",observationResult2);


                JSONObject sysLoad = new JSONObject();

                sysLoad.put("@context","http://vital-iot.eu/contexts/measurement.jsonld");
                sysLoad.put("id",host+"/sensor/1/observation/3");
                sysLoad.put("type","ssn:Observation");

                JSONObject property3 = new JSONObject();
                property3.put("type","vital:SysLoad");
                sysLoad.put("ssn:observationProperty",property3);

                JSONObject resultTime3 = new JSONObject();

                Date date3 = new Date();

                resultTime3.put("time:inXSDDateTime",dateFormat.format(date3));//check format
                sysLoad.put("ssn:observationResultTime",resultTime3);
                sysLoad.put("ssn:featureOfInterest",host+"/sensor/1");

                JSONObject hasValue3 = new JSONObject();
                hasValue3.put( "type","ssn:ObservationValue");
                hasValue3.put( "value",ThreadLocalRandom.current().nextInt(1, 5 + 1)+"%");
                hasValue3.put( "qudt:unit","qudt:Percent"); 
                JSONObject observationResult3 = new JSONObject();
                observationResult3.put("ssn:hasValue",hasValue3);
                observationResult3.put("type","ssn:SensorOutput");
                sysLoad.put("ssn:observationResult",observationResult3);


                sensorspool.put(opState);
                sensorspool.put(sysUpTime);
                sensorspool.put(sysLoad);    
            }
        } catch (JSONException ex) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .build();
        }
        
        return Response.status(Response.Status.OK)
                                .entity(sensorspool.toString()).build();
         
    }
    

      /**
     * Gets sensors metadata .
     *
     * @return the metadata of the sensors 
     * @throws java.io.FileNotFoundException 
     */
    @POST
    @Path("observation")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensorsObservations(String info,
            @Context HttpServletRequest req) throws FileNotFoundException, 
            IOException {
        
        StringBuilder ck = new StringBuilder();
        Security slogin = new Security();
                  
        Boolean token = slogin.login(req.getHeader("name")
                ,req.getHeader("password"),false,ck);
        if (!token){
              return Response.status(Response.Status.UNAUTHORIZED).build();
        }
                         
        DBObject request = (DBObject) JSON.parse(info);
        BasicDBList sensors;
        String property;
        
        if (!request.containsField("sensor")||!request.containsField("property")){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }else{
            
            try {
                sensors = (BasicDBList) request.get("sensor");
                property = (String)request.get("property");
            }catch(Exception e){
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            
        }
        
        if (request.containsField("from") && request.containsField("to")){
            
           if(!isDateValid((String)request.get("from"))|| 
                   !isDateValid((String)request.get("to"))){
                 return Response.status(Response.Status.BAD_REQUEST)
                    .build();
           }
        }
        
        JSONArray observations ;
        
        if (request.containsField("from") && request.containsField("to")){

            observations = getObservations(sensors,"cepicosobservations",property
                    ,(String)request.get("from"),(String)request.get("to"));
        
        }else if(request.containsField("from")){
            //now
            
            Date NOW = new Date();
            
            observations = getObservations(sensors,"cepicosobservations",property
                    ,(String)request.get("from"),getXSDDateTime(NOW));
            
        }else{
            
            observations = getObservations(sensors,"cepicosobservations",property
                    ,null,null);
        
        }
                 
        return Response.status(Response.Status.OK)
                .entity (observations.toString()).build();
                
    }

    
    private JSONArray getObservations(BasicDBList sensor, String collection
    , String property,String from, String to) {
        
        JSONArray aData = new JSONArray();
             
        BasicDBObject query = new BasicDBObject();
        
        final JSONArray oObservations = new JSONArray();
            
        if (!(to == null) && !(from == null)){
            try{
                String queryS = createQuery(sensor, property, from, to);
                Object queryO = com.mongodb.util.JSON
                        .parse(queryS);
                query = (BasicDBObject) queryO;
                
                MongoClient mongo = new MongoClient(new MongoClientURI(mongoURL));
                final MongoDatabase db = mongo.getDatabase(mongoDB);


                Block<Document> block = new Block<Document>() {
                    @Override
                    public void apply(final Document document) {
                        JSONObject oCollector = new JSONObject(document.toJson());
                        oObservations.put(oCollector);

                    }
                };

                db.getCollection("cepicosobservations").find(query)
                        .projection(fields(excludeId())).forEach(block);
                db.getCollection("continuosfiltersobservations").find(query)
                        .projection(fields(excludeId())).forEach(block);
                db.getCollection("alertsobservations").find(query)
                        .projection(fields(excludeId())).forEach(block);
                db.getCollection("staticdatafiltersobservations").find(query)
                        .projection(fields(excludeId())).forEach(block);
                db.getCollection("staticqueryfiltersobservations").find(query)
                        .projection(fields(excludeId())).forEach(block);
               
            }catch (Exception e){
                String a= "a";
            }
                            
        }else{
            try{
                Object queryO = com.mongodb.util.JSON
                            .parse(createQuery(sensor, property,null,null));
                query = (BasicDBObject) queryO;

                MongoClient mongo = new MongoClient(new MongoClientURI(mongoURL));
                final MongoDatabase db = mongo.getDatabase(mongoDB);


                Block<Document> block = new Block<Document>() {
                    @Override
                    public void apply(final Document document) {
                        JSONObject oCollector = new JSONObject(document.toJson());
                        oObservations.put(oCollector);

                    }
                };

                BasicDBObject sortObject = new BasicDBObject().append("_id", -1);

                db.getCollection("cepicosobservations").find(query)
                        .projection(fields(excludeId())).sort(sortObject)
                        .limit(1).forEach(block);
                db.getCollection("continuosfiltersobservations").find(query)
                        .projection(fields(excludeId()))
                        .sort(sortObject).limit(1).forEach(block);
                db.getCollection("alertsobservations").find(query)
                        .projection(fields(excludeId()))
                        .sort(sortObject).limit(1).forEach(block);
                db.getCollection("staticdatafiltersobservations").find(query)
                        .projection(fields(excludeId()))
                        .sort(sortObject).limit(1).forEach(block);
                db.getCollection("staticqueryfiltersobservations").find(query)
                        .projection(fields(excludeId()))
                        .sort(sortObject).limit(1).forEach(block);  

            }catch (Exception e){
               String a= "a";
            }
                  
        }
         
        return oObservations;
        
    }

    private JSONObject createQueryOr(String sensor, String property
            , String from, String to) throws JSONException {
        
        JSONObject simplequery = new JSONObject();

        //JSONObject propertyJ= new JSONObject();
       // propertyJ.put("type",property );

        simplequery.put("ssn:observationProperty.type",property);
        simplequery.put("ssn:observedBy",sensor);
        
        if (!(from==null)&&!(to==null)){
            
            String timeValue =  " {\"$gt\": \""
                + from
                +"\", \"$lt\": \""
                + to
                +"\"}" ;
            JSONObject timeObject = new JSONObject(timeValue);
            simplequery.put("ssn:observationResultTime.time:inXSDDateTime",timeObject);
        }
                
        return simplequery;
    }
    
    private String createQuery(BasicDBList sensor, String property, String from
                                ,String to) {
        
        String mongoquery ="";
        JSONObject completequery = new JSONObject();
        
        if (sensor.size()<=1){
          
            try{
                String sensor1=(String)sensor.get(0);
                JSONObject cQO = createQueryOr(sensor1, property,from,to);
                mongoquery = cQO.toString();
                return  mongoquery; 
                
            }catch(Exception ex){
                java.util.logging.Logger.getLogger(DMSListener
                        .class.getName())
                        .log(Level.SEVERE, null, ex);
            }

        }else{
            
            try{
           
                JSONArray ors = new JSONArray();

                for (Object sensor1 : sensor) {
                    ors.put(createQueryOr((String) sensor1, property,from,to));
                }

                completequery.put("$or",ors);
                // DMSManager oDMS = new DMSManager(dmsURL,cookie);
                
                // aData = oDMS.getObservations(completequery.toString());
                return completequery.toString();
            }catch(Exception ex){
                java.util.logging.Logger.getLogger(DMSListener
                        .class.getName())
                        .log(Level.SEVERE, null, ex);
            }
        }
        return mongoquery;
    }
  
    final static String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssXXX";

    public static boolean isDateValid(String date) 
    {
            try {
                DateFormat df = new SimpleDateFormat(DATE_FORMAT);
                df.setLenient(false);
                df.parse(date);
                return true;
            } catch (ParseException e) {
                return false;
            }
    }
     private String getXSDDateTime(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        return  dateFormat.format(date);
     }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.vital.vitalcep.restApp.vuaippi;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Context;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.json.JSONArray;


import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import eu.vital.vitalcep.conf.ConfigReader;
import eu.vital.vitalcep.security.Security;
import org.bson.Document;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;
import javax.servlet.http.HttpServletRequest;

// TODO: Auto-generated Javadoc
/**
 * The Class FilteringRestREST.
 */
@Path("")
public class System {

    /** The Constant logger. */
    final static Logger logger = Logger.getLogger(System.class);
    
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
    
    public System()  throws IOException {
    
        ConfigReader configReader = ConfigReader.getInstance();
        
        mongoURL = configReader.get(ConfigReader.MONGO_URL);
        mongoDB = configReader.get(ConfigReader.MONGO_DB);
        host = configReader.get(ConfigReader.CEP_BASE_URL);

    }
    
    /**
     * Gets the cep version.
     *
     * @return the cep version
     * @throws java.io.IOException
     */
    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public String getCEPversion() throws IOException {
       
//        host = config.getProperty("cep-ip-address")
//                .concat(":8180");
        JSONObject CEPJson = new JSONObject();
        CEPJson.put("module","CEP");
        CEPJson.put("version","0.1");
            
        return CEPJson.toString();

    }

    @POST
    @Path("metadata")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSystemMetadata(String info,
            @Context HttpServletRequest req) throws FileNotFoundException,
            IOException {
        
//        StringBuilder ck = new StringBuilder();
//        Security slogin = new Security();
//                  
//        Boolean token = slogin.login(req.getHeader("name")
//                ,req.getHeader("password"),false,ck);
//        if (!token){
//              return Response.status(Response.Status.UNAUTHORIZED).build();
//        }      
        
        MongoClient mongo = new MongoClient(new MongoClientURI (mongoURL));
        MongoDatabase db = mongo.getDatabase(mongoDB);
        
        BasicDBObject query = new BasicDBObject(); 
        BasicDBObject fields = new BasicDBObject().append("_id",false);
        fields.append("dolceSpecification", false);
        
        FindIterable<Document> collStaticData = db.getCollection("staticdatafilters")
                .find(query).projection(fields);
       
        
        final JSONArray sensors = new JSONArray(); 
        
        collStaticData.forEach(new Block<Document>() {
            @Override
            public void apply(final Document document) {
                sensors.put(document.get("id"));
            }
        });
        
        FindIterable<Document> collStaticQuery = db.getCollection("staticqueryfilters")
                .find(query).projection(fields);
            
        
        collStaticQuery.forEach(new Block<Document>() {
            @Override
            public void apply(final Document document) {
                sensors.put(document.get("id"));
            }
        });
        
         FindIterable<Document> collContinuous = db.getCollection("continuousfilters")
                .find(query).projection(fields);
            
        
        collContinuous.forEach(new Block<Document>() {
            @Override
            public void apply(final Document document) {
                sensors.put(document.get("id"));
            }
        });
        

        BasicDBObject querycep = new BasicDBObject(); 
        BasicDBObject fieldscep = new BasicDBObject().append("_id",false)
                .append("dolceSpecification", false);
        
        FindIterable<Document> collcepicos = db.getCollection("cepicos")
                .find(querycep).projection(fieldscep);
        // create an empty query
        
        collcepicos.forEach(new Block<Document>() {
            @Override
            public void apply(final Document document) {
                sensors.put(document.get("id"));
            }
        });
        
        BasicDBObject queryalert = new BasicDBObject(); 
        BasicDBObject fieldsalert = new BasicDBObject().append("_id",false)
                .append("dolceSpecification", false);
        
        FindIterable<Document> collalerts = db.getCollection("alerts")
                .find(queryalert).projection(fieldsalert);
        // create an empty query
        
        collalerts.forEach(new Block<Document>() {
            @Override
            public void apply(final Document document) {
                sensors.put(document.get("id"));
            }
        });
        
        sensors.put(host+"/sensor/1");
                JSONObject metadata = new JSONObject();
                 
        JSONArray services = new JSONArray(); 
        services.put(host+"/service/monitoring");
        services.put(host+"/service/cepicosmanagement");
        services.put(host+"/service/filtering");
        services.put(host+"/service/alertingmanagement");
        services.put(host+"/service/observation");
                
        metadata.put("@context","http://vital-iot.eu/contexts/system.jsonld");
        metadata.put("id",host);
        metadata.put("type","vital:VitalSystem");
        metadata.put("name","CEP IoT system");
        metadata.put("description","This is a VITAL compliant IoT system.");
        metadata.put("operator","elisa.herrmann@atos.net");
        metadata.put("status","vital:Running");
        metadata.put("services", services);
//        metadata.put("sensors", sensors);
        metadata.append("sensors", sensors);
        
        return Response.status(Response.Status.OK)
                                .entity(metadata.toString()).build();
    }
    
    @POST
    @Path("system/status")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSystemStatus(String info,
            @Context HttpServletRequest req) throws FileNotFoundException,
            IOException {
        
//        StringBuilder ck = new StringBuilder();
//        Security slogin = new Security();
//                  
//        Boolean token = slogin.login(req.getHeader("name")
//                ,req.getHeader("password"),false,ck);
//        if (!token){
//              return Response.status(Response.Status.UNAUTHORIZED).build();
//        }
        
        JSONObject metadata = new JSONObject();
        
        metadata.put("@context","http://vital-iot.eu/contexts/measurement.jsonld");
        metadata.put("id", host+"/sensor/1/observation/1");
        metadata.put("type","ssn:Observation");
        
        JSONObject obsProp = new JSONObject();
        
        obsProp.put("type","vital:OperationalState");
        metadata.put("ssn:observationProperty",obsProp);
        
        JSONObject obsResultTime = new JSONObject();
        
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        
        Date date = new Date();
        
        obsResultTime.put("time:inXSDDateTime",dateFormat.format(date));
        
        metadata.put("ssn:observationResultTime",obsResultTime);
        metadata.put( "ssn:featureOfInterest", host);
        
        JSONObject observationResult = new JSONObject();
        
       
        observationResult.put("type", "ssn:SensorOutput");
        
        JSONObject hasValue = new JSONObject();
       
        hasValue.put("type", "ssn:ObservationValue");
        hasValue.put("value", "vital:Running");
        
        observationResult.put("ssn:hasValue", hasValue);
        
        metadata.put("ssn:observationResult",observationResult);
                        
        return Response.status(Response.Status.OK)
                                .entity(metadata.toString()).build();
    }
    
    
     /**
     * Gets sensors status .
     *
     * @return the metadata of the sensors 
     * @throws java.io.FileNotFoundException 
     */
    @POST
    @Path("system/performance")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPerformanceMetrics(String info,
            @Context HttpServletRequest req) throws FileNotFoundException, 
            IOException {
//        StringBuilder ck = new StringBuilder();
//        Security slogin = new Security();
//                  
//        Boolean token = slogin.login(req.getHeader("name")
//                ,req.getHeader("password"),false,ck);
//        if (!token){
//              return Response.status(Response.Status.UNAUTHORIZED).build();
//        }
        
        JSONObject metric1 = new JSONObject();
        
        metric1.put("@context","http://vital-iot.eu/contexts/measurement.jsonld");
        metric1.put("id", host+"/sensor/1/observation/3");
        metric1.put("type","ssn:Observation");
        
        JSONObject property = new JSONObject();
        property.put("type","vital:SysLoad");
        metric1.put("ssn:observationProperty",property);
            
       
        
        JSONObject resultTime = new JSONObject();
        
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        Date date = new Date();
       
        resultTime.put("time:inXSDDateTime",dateFormat.format(date));//check format
        metric1.put("ssn:observationResultTime",resultTime);
        metric1.put("ssn:featureOfInterest",host);
        
        JSONObject hasValue = new JSONObject();
        hasValue.put( "type","ssn:ObservationValue");
        hasValue.put( "value",ThreadLocalRandom.current().nextInt(1, 5 + 1)+"%");
        hasValue.put( "qudt:unit","qudt:Percent"); 
        JSONObject observationResult = new JSONObject();
        observationResult.put("ssn:hasValue",hasValue);
        observationResult.put("type","ssn:SensorOutput");
        metric1.put("ssn:observationResult",observationResult);
        
        JSONObject metric2 = new JSONObject();
        
        metric2.put("@context","http://vital-iot.eu/contexts/measurement.jsonld");
        metric2.put("id", host+"/sensor/1/observation/2");
        metric2.put("type","ssn:Observation");
        
        JSONObject property2 = new JSONObject();
        property2.put("type","vital:SysUptime");
        metric2.put("ssn:observationProperty",property2);
                  
        JSONObject resultTime2 = new JSONObject();
        DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        Date date2 = new Date();
       
        resultTime2.put("time:inXSDDateTime",dateFormat2.format(date2));//check format
        metric2.put("ssn:observationResultTime",resultTime2);
        metric2.put("ssn:featureOfInterest",host);
        
        JSONObject hasValue2 = new JSONObject();
        hasValue2.put( "type","ssn:ObservationValue");
        RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
        long uptime = rb.getUptime();
        hasValue2.put( "value",""+uptime);
        hasValue2.put( "qudt:unit","qudt:Milliseconds"); 
        JSONObject observationResult2 = new JSONObject();
        observationResult2.put("ssn:hasValue",hasValue2);
        observationResult2.put("type","ssn:SensorOutput");
        metric2.put("ssn:observationResult",observationResult2);
        
        JSONArray metrics = new JSONArray();
        
        metrics.put(metric1);
        metrics.put(metric2);
        
        return Response.status(Response.Status.OK)
                                .entity(metrics.toString()).build();
         
    }
    
     /**
     * Gets supported performance metrics .
     *
     * @return the metadata of the sensors 
     * @throws java.io.FileNotFoundException 
     */
    @GET
    @Path("system/performance")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSupportedPerformanceMetrics(
            @Context HttpServletRequest req) throws FileNotFoundException, 
            IOException {
        
//        StringBuilder ck = new StringBuilder();
//        Security slogin = new Security();
//                  
//        Boolean token = slogin.login(req.getHeader("name")
//                ,req.getHeader("password"),false,ck);
//        if (!token){
//              return Response.status(Response.Status.UNAUTHORIZED).build();
//        }

        JSONObject metric1 = new JSONObject();
        
        metric1.put("id",host+"/sensor/1/sysUptime");
        metric1.put("type","http://vital-iot.eu/ontology/ns/SysUptime");
               
        JSONObject metric2 = new JSONObject();
        
        metric2.put("id",host+"/sensor/1/sysLoad");
        metric2.put("type","http://vital-iot.eu/ontology/ns/SysLoad");
                
        JSONArray metrics = new JSONArray();
             
        metrics.put(metric1);
        metrics.put(metric2);
        
        JSONObject jObj = new JSONObject();
        
        jObj.put("metrics",metrics);
        
        return Response.status(Response.Status.OK)
                                .entity(jObj.toString()).build();
    }
    
    
}

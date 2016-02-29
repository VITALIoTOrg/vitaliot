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
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Context;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;


import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;
import org.bson.Document;

// TODO: Auto-generated Javadoc
/**
 * The Class FilteringRestREST.
 */
@Path("")
public class VUAIPPI {

    /** The Constant logger. */
    final static Logger logger = Logger.getLogger(VUAIPPI.class);
    
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
    

    
   // @Context private javax.servlet.http.HttpServletRequest hsr;
    
    public VUAIPPI() throws IOException {

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
    public Response getSystemMetadata(String info) throws FileNotFoundException,
            IOException {

        MongoClient mongo = new MongoClient(mongoIp, mongoPort);

        MongoDatabase db = mongo.getDatabase(mongoDB);
        
        BasicDBObject query = new BasicDBObject(); 
        BasicDBObject fields = new BasicDBObject().append("_id",false);
        fields.append("dolceSpecification", false);
        
        FindIterable<Document> coll = db.getCollection("filters")
                .find(query).projection(fields);
       
        
        final JSONArray sensors = new JSONArray(); 
        
        coll.forEach(new Block<Document>() {
            @Override
            public void apply(final Document document) {
                sensors.put(document.get("id"));
            }
        });
        
//        try (DBCursor cursor = coll.find(query,fields)) {
//            while(cursor.hasNext()) {
//                 if (cursor.next().containsField("id")){
//                     sensors.put(cursor.curr().get("id"));
//                 }
//            }
//        }   

        BasicDBObject querycep = new BasicDBObject(); 
        BasicDBObject fieldscep = new BasicDBObject().append("_id",false)
                .append("dolceSpecification", false);
        
        FindIterable<Document> collcep = db.getCollection("ceps")
                .find(querycep).projection(fieldscep);
        // create an empty query
        
        collcep.forEach(new Block<Document>() {
            @Override
            public void apply(final Document document) {
                sensors.put(document.get("id"));
            }
        });
        
//        try (DBCursor cursor2 = collcep.find(querycep,fieldscep)) {
//            while(cursor2.hasNext()) {
//                 if (cursor2.next().containsField("id")){
//                     sensors.put(cursor2.curr().get("id"));
//                 }
//            }
//        }
        
        sensors.put("http://"+host.toString()+"/cep/sensor/1");
                JSONObject metadata = new JSONObject();
                 
        JSONArray services = new JSONArray(); 
        services.put("http://"+host.toString()+"/cep/service/monitoring");
        services.put("http://"+host.toString()+"/cep/service/cepicosmanagement");
        services.put("http://"+host.toString()+"/cep/service/filtering");
        services.put("http://"+host.toString()+"/cep/service/observation");
                
        metadata.put("@context","http://vital-iot.eu/contexts/system.jsonld");
        metadata.put("id","http://"+ host+"/cep");
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
    public Response getSystemStatus(String info) throws FileNotFoundException,
            IOException {
        
        JSONObject metadata = new JSONObject();
        
        metadata.put("@context","http://vital-iot.eu/contexts/measurement.jsonld");
        metadata.put("id","http://"+ host+"/cep/sensor/1/observation/1");
        metadata.put("type","ssn:Observation");
        
        JSONObject obsProp = new JSONObject();
        
        obsProp.put("type","vital:OperationalState");
        metadata.put("ssn:observationProperty",obsProp);
        
        JSONObject obsResultTime = new JSONObject();
        
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        
        Date date = new Date();
        
        obsResultTime.put("time:inXSDDateTime",dateFormat.format(date));
        
        metadata.put("ssn:observationResultTime",obsResultTime);
        metadata.put( "ssn:featureOfInterest", "http://"+ host+"/cep");
        
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
     * Gets service metadata .
     *
     * @return the metadata of the services
     */
    @POST
    @Path("service/metadata")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getServiceMetadata(String info) throws FileNotFoundException,
            IOException {
      
        JSONObject monitoring = new JSONObject();
        
        monitoring.put("@context", 
                "http://vital-iot.eu/contexts/service.jsonld");
        
        monitoring.put("id", 
                "http://"+host.toString()+"/cep/service/monitoring");
        
        monitoring.put("type","vital:MonitoringService");
        
        JSONObject GetSystemStatus = new JSONObject();
        JSONObject GetSensorStatus = new JSONObject();
        JSONObject GetSupportedPerformanceMetrics = new JSONObject();
        JSONObject GetPerformanceMetrics = new JSONObject();
        JSONObject GetSupportedSLAParameters = new JSONObject();
        JSONObject GetSLAParameters = new JSONObject();
        
        GetSystemStatus.put("type","vital:GetSystemStatus");
        GetSystemStatus.put("hrest:hasAddress","http://"+host.toString()
                +"/cep/system/status");
        GetSystemStatus.put("hrest:hasMethod","hrest:POST");

        GetSensorStatus.put("type","vital:GetSensorStatus");
        GetSensorStatus.put("hrest:hasAddress","http://"+host.toString()
                +"/cep/sensor/status");
        GetSensorStatus.put("hrest:hasMethod","hrest:POST");
        
        GetSupportedPerformanceMetrics
                .put("type","vital:GetSupportedPerformanceMetrics");
        GetSupportedPerformanceMetrics.put("hrest:hasAddress",
                "http://"+host.toString()+"/cep/system/performance");
        GetSupportedPerformanceMetrics.put("hrest:hasMethod","hrest:GET");

        GetPerformanceMetrics.put("type","vital:GetPerformanceMetrics");
        GetPerformanceMetrics.put("hrest:hasAddress","http://"
                +host.toString()+"/cep/system/performance");
        GetPerformanceMetrics.put("hrest:hasMethod","hrest:POST");
        
        GetSupportedSLAParameters.put("type","vital:GetSupportedSLAParameters");
        GetSupportedSLAParameters.put("hrest:hasAddress","http://"
                +host.toString()+"/cep/system/sla");
        GetSupportedSLAParameters.put("hrest:hasMethod","hrest:GET");
        
        GetSLAParameters.put("type","vital:GetSLAParameters");
        GetSLAParameters.put("hrest:hasAddress","http://"
                +host.toString()+"/cep/system/sla");
        GetSLAParameters.put("hrest:hasMethod","hrest:POST");

        JSONArray monitoringOperations = new JSONArray();
        
        monitoringOperations.put(GetSystemStatus);
        monitoringOperations.put(GetSensorStatus);
        monitoringOperations.put(GetSupportedPerformanceMetrics);
        monitoringOperations.put(GetPerformanceMetrics);
        //monitoringOperations.put(GetSupportedSLAParameters);
        //monitoringOperations.put(GetSLAParameters);
        
        monitoring.put("msm:hasOperation",monitoringOperations );
        
        
        JSONObject cepico = new JSONObject();
        
        cepico.put("@context", 
                "http://vital-iot.eu/contexts/service.jsonld");
        
        cepico.put("id", 
                "http://"+host.toString()+"/cep/service/cepicosmanagement");
        
        cepico.put("type","vital:CEPICOManagementService");
        
        JSONObject getCepicos = new JSONObject();
        JSONObject getCepico = new JSONObject();
        JSONObject createCepico = new JSONObject();
        JSONObject deleteCepico = new JSONObject();

        
        getCepicos.put("type","vital:GetCEPICOs");
        getCepicos.put("hrest:hasAddress","http://"+host.toString()
                +"/cep/getcepicos");
        getCepicos.put("hrest:hasMethod","hrest:GET");

        getCepico.put("type","vital:GetCEPICO");
        getCepico.put("hrest:hasAddress","http://"+host.toString()
                +"/cep/getcepico");
        getCepico.put("hrest:hasMethod","hrest:POST");
        
        createCepico.put("type","vital:CreateCEPICO");
        createCepico.put("hrest:hasAddress",
                "http://"+host.toString()+"/cep/createcepico");
        createCepico.put("hrest:hasMethod","hrest:PUT");

        deleteCepico.put("type","vital:DeleteCEPICO");
        deleteCepico.put("hrest:hasAddress","http://"
                +host.toString()+"/cep/deletecepico");
        deleteCepico.put("hrest:hasMethod","hrest:DELETE");

        JSONArray cepicosOperations = new JSONArray();
        
        cepicosOperations.put(getCepicos);
        cepicosOperations.put(getCepico);
        cepicosOperations.put(createCepico);
        cepicosOperations.put(deleteCepico);
        
        cepico.put("msm:hasOperation",cepicosOperations );
        
        JSONObject filtering = new JSONObject();
        
        filtering.put("@context", 
                "http://vital-iot.eu/contexts/service.jsonld");
        
        filtering.put("id", 
                "http://"+host.toString()+"/cep/service/filtering");
        
        filtering.put("type","vital:CEPFitleringService");
        
        JSONObject CreateContinuousFilter = new JSONObject();
        JSONObject GetContinuousFilters = new JSONObject();
        JSONObject GetContinuousFilter = new JSONObject();
        JSONObject DeleteContinuousFilter = new JSONObject();
        JSONObject FilterStaticData = new JSONObject();
        JSONObject FilterStaticQuery = new JSONObject();
        
        CreateContinuousFilter.put("type","vital:CreateContinuousFilter");
        CreateContinuousFilter.put("hrest:hasAddress","http://"+host.toString()
                +"/cep/filtering/createcontinuousfilter");
        CreateContinuousFilter.put("hrest:hasMethod","hrest:GET");

        GetContinuousFilters.put("type","vital:GetContinuousFilters");
        GetContinuousFilters.put("hrest:hasAddress","http://"+host.toString()
                +"/cep/filtering/getcontinuousfilters");
        GetContinuousFilters.put("hrest:hasMethod","hrest:GET");
        
        GetContinuousFilter.put("type","vital:GetContinuousFilter");
        GetContinuousFilter.put("hrest:hasAddress",
                "http://"+host.toString()+"/cep/filtering/getcontinuousfilter");
        GetContinuousFilter.put("hrest:hasMethod","hrest:POST");

        DeleteContinuousFilter.put("type","vital:DeleteContinuousFilter");
        DeleteContinuousFilter.put("hrest:hasAddress","http://"
                +host.toString()+"/cep/filtering/deletecontinuousfilter");
        DeleteContinuousFilter.put("hrest:hasMethod","hrest:DELETE");

        FilterStaticData.put("type","vital:FilterStaticData");
        FilterStaticData.put("hrest:hasAddress",
                "http://"+host.toString()+"/cep/filtering/filterstaticdata");
        FilterStaticData.put("hrest:hasMethod","hrest:POST");

        FilterStaticQuery.put("type","vital:FilterStaticQuery");
        FilterStaticQuery.put("hrest:hasAddress","http://"
                +host.toString()+"/cep/filtering/filterstaticquery");
        FilterStaticQuery.put("hrest:hasMethod","hrest:POST");

        JSONArray filteringOperations = new JSONArray();
        
        filteringOperations.put(CreateContinuousFilter);
        filteringOperations.put(GetContinuousFilters);
        filteringOperations.put(GetContinuousFilter);
        filteringOperations.put(DeleteContinuousFilter);
        filteringOperations.put(FilterStaticData);
        filteringOperations.put(FilterStaticQuery);
        
        filtering.put("msm:hasOperation",filteringOperations );
        
        
        JSONObject observation = new JSONObject();
        
        observation.put("@context", 
                "http://vital-iot.eu/contexts/service.jsonld");
        
        observation.put("id", 
                "http://"+host.toString()+"/cep/service/observation");
        
        observation.put("type","vital:ObservationService");
        
        JSONObject operation1 = new JSONObject();
              
        operation1.put("type","vital:SubscribeToObservationStream");
        operation1.put("hrest:hasAddress","http://"+host.toString()
                +"/cep/observation/stream/subscribe");
        operation1.put("hrest:hasMethod","hrest:POST");

        JSONObject operation2 = new JSONObject();
              
        operation2.put("type","vital:UnsubscribeFromObservationStream");
        operation2.put("hrest:hasAddress","http://"+host.toString()
                +"/cep/observation/stream/unsubscribe");
        operation2.put("hrest:hasMethod","hrest:POST");

        JSONArray observationOperations = new JSONArray();
        
        observationOperations.put(operation1);
        observationOperations.put(operation2);
        
        observation.put("operations",observationOperations );
        
        JSONArray services = new JSONArray();

        services.put(monitoring);
        services.put(cepico);
        services.put(filtering);         
        services.put(observation);  
        
        if(!info.equals("")){

            try{
                JSONObject filter = new JSONObject(info);
                                
                if(filter.has("type")){
                    JSONArray filteredServices = new JSONArray();

                    JSONArray types = filter.getJSONArray("type");
                    for (int i = 0; i < types.length(); i++) {
                        
                        String type = types.getString(i);
                                               
                        switch (type) {
                        case OBSERVATIONSERVICE_TYPE:  
                            filteredServices.put(observation);
                            break;
                        case MONITORINGSERVICE_TYPE:
                            filteredServices.put(monitoring);
                                 break;
                        case CEPFILTERINGSERVICE_TYPE:
                            filteredServices.put(filtering);
                                 break;
                        case CEPICOMANAGEMENTSERVICE_TYPE:
                            filteredServices.put(cepico);
                                 break;
                        default: 
                             return Response.status(Response.Status.BAD_REQUEST)
                                .build();
                        }         
                    }
                    services = filteredServices;
                 }else if (filter.has("id")){
                    JSONArray filteredServices = new JSONArray();
                     
                    JSONArray ids = filter.getJSONArray("id");
                    for (int i = 0; i < ids.length(); i++) {
                        
                        String id = ids.getString(i);
                                               
                        for (int j = 0; j < services.length(); j++) {
                            String auxid = services.getJSONObject(j).getString("id");
                            if (auxid.equals(id)){
                                filteredServices.put(services.getJSONObject(j));                             
                            }
                        }
                    }
                    services = filteredServices;
                 }
                
            }
            catch(JSONException ex ){
                return Response.status(Response.Status.BAD_REQUEST)
                    .build();
            }
            
            
        }
             
        return Response.status(Response.Status.OK)
                                .entity(services.toString()).build();
         
    }
    
     /**
     * Gets sensors metadata .
     *
     * @param info
     * @return the metadata of the sensors 
     * @throws java.io.FileNotFoundException 
     */
    @POST
    @Path("sensor/metadata")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensorMetadata(String info) throws FileNotFoundException, 
            IOException {
        
        MongoClient mongo = new MongoClient(mongoIp, mongoPort);

        MongoDatabase db = mongo.getDatabase(mongoDB);
                  
        final JSONArray sensorspool = new JSONArray();    
        
        try{
            JSONObject filter = new JSONObject(info);
            
            if(!filter.has("type")&& !filter.has("id") ){
        
            // create an empty query
            BasicDBObject query = new BasicDBObject(); 
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
                    sensorspool.put(sensoraux);
                }
            });
            
//            try (DBCursor cursor = coll.find(query,fields)) {
//                while(cursor.hasNext()) {
//                    String aux = cursor.next().toString();
//                    JSONObject sensoraux = new JSONObject(aux);  
//                    sensoraux.put("status","vital:Running");
//                    sensorspool.put(sensoraux);
//                }
//
//            }
            
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
                    sensorspool.put(sensoraux);
                }
            });
            
//            try (DBCursor cursor3 = coll3.find(query3, fields3)) {
//                while (cursor3.hasNext()) {
//                    String aux = cursor3.next().toString();
//                    JSONObject sensoraux = new JSONObject(aux);
//                    sensoraux.put("status", "vital:Running");
//                    sensorspool.put(sensoraux);
//                }
//
//            }
                            
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
                    sensorspool.put(sensoraux);
                }
            });

//            try (DBCursor cursor4 = coll4.find(query4, fields4)) {
//                while (cursor4.hasNext()) {
//                    String aux = cursor4.next().toString();
//                    JSONObject sensoraux = new JSONObject(aux);
//                    sensoraux.put("status", "vital:Running");
//                    sensorspool.put(sensoraux);
//                }
//
//            }

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
                    sensorspool.put(sensoraux);
                }
            });
             
//            try (DBCursor cursor2 = coll2.find(query,fieldscep)) {
//                while(cursor2.hasNext()) {
//                    if (cursor2.next().containsField("id")){
//                        String aux = cursor2.curr().toString();
//                        JSONObject sensoraux = new JSONObject(aux);  
//                        sensoraux.put("status","vital:Running");
//                        sensorspool.put(sensoraux);
//                    }
//                }
//            }


            JSONObject monSensor = new JSONObject();

            monSensor.put("@context","http://vital-iot.eu/contexts/sensor.jsonld");
            monSensor.put("id","http://"+ host+"/cep/sensor/1");
            monSensor.put("type","vital:MonitoringSensor");
            monSensor.put("name","CEP System Monitoring Sensor");
            monSensor.put("description",
                    "A virtual sensor that monitors the operationalstate of the CEP system");

            JSONObject observesItem1 = new JSONObject();

            observesItem1.put("type","vital:OperationalState");
            observesItem1.put("id","http://"+host+"/cep/sensor/1/operationalState");


            JSONObject observesItem2 = new JSONObject();

            observesItem2.put("type","vital:SysUptime");
            observesItem2.put("id","http://"+host+"/cep/sensor/1/sysUptime");


            JSONObject observesItem3 = new JSONObject();

            observesItem3.put("type","vital:SysLoad");
            observesItem3.put("id","http://"+host+"/cep/sensor/1/sysLoad");

            JSONObject observesItem4 = new JSONObject();

            observesItem4.put("type","vital:errors");
            observesItem4.put("id","http://"+host+"/cep/sensor/1/errors");

            JSONArray observes = new JSONArray();

            observes.put(observesItem1);
            observes.put(observesItem2);
            observes.put(observesItem3);
            //observes.put(observesItem4);     

            monSensor.put("ssn:observes",observes);
            monSensor.put("status", "vital:Running");

            sensorspool.put(monSensor);
            
        }else if(filter.has("type")){
            JSONArray filteredSensors = new JSONArray();

            JSONArray types = filter.getJSONArray("type");
            for (int i = 0; i < types.length(); i++) {

                String type = types.getString(i);

                switch (type) {
                case CEPICOSENSOR_TYPE:

                    // create an empty query
                    BasicDBObject fieldscep = new BasicDBObject().append("_id",false)
                            .append("source",false).append("query",false)
                            .append("complexEvent", false)
                            .append("data",false).append("event",false)
                            .append("dolceSpecification",false);
                    BasicDBObject query = new BasicDBObject(); 
                    
                    FindIterable<Document> coll2 = db.getCollection("ceps")
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

//                    try (DBCursor cursor2 = coll2.find(query,fieldscep)) {
//                        while(cursor2.hasNext()) {
//                            if (cursor2.next().containsField("id")){
//                                String aux = cursor2.curr().toString();
//                                JSONObject sensoraux = new JSONObject(aux);  
//                                sensoraux.put("status","vital:Running");
//                                sensorspool.put(sensoraux);
//                            }
//                        }
//                    }

                    break;
                case MONITORINGSENSOR_TYPE:
                    JSONObject monSensor = new JSONObject();

                    monSensor.put("@context","http://vital-iot.eu/contexts/sensor.jsonld");
                    monSensor.put("id","http://"+ host+"/cep/sensor/1");
                    monSensor.put("type","vital:MonitoringSensor");
                    monSensor.put("name","CEP System Monitoring Sensor");
                    monSensor.put("description",
                            "A virtual sensor that monitors the operationalstate of the CEP system");

                    JSONObject observesItem1 = new JSONObject();

                    observesItem1.put("type","vital:OperationalState");
                    observesItem1.put("id","http://"+host+"/cep/sensor/1/operationalState");


                    JSONObject observesItem2 = new JSONObject();

                    observesItem2.put("type","vital:SysUptime");
                    observesItem2.put("id","http://"+host+"/cep/sensor/1/sysUptime");


                    JSONObject observesItem3 = new JSONObject();

                    observesItem3.put("type","vital:SysLoad");
                    observesItem3.put("id","http://"+host+"/cep/sensor/1/sysLoad");

                    JSONObject observesItem4 = new JSONObject();

                    observesItem4.put("type","vital:errors");
                    observesItem4.put("id","http://"+host+"/cep/sensor/1/errors");

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
                case CEPFILTERSTATICDATASENSOR_TYPE:
                    
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
                    
//                    try (DBCursor cursor3 = coll3.find(query3, fields3)) {
//                        while (cursor3.hasNext()) {
//                            String aux = cursor3.next().toString();
//                            JSONObject sensoraux = new JSONObject(aux);
//                            sensoraux.put("status", "vital:Running");
//                            sensorspool.put(sensoraux);
//                        }
//
//                    }
                    break;
                case CEPFILTERSTATICQUERYSENSOR_TYPE:
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
                    
//                    try (DBCursor cursor4 = coll4.find(query4, fields4)) {
//                        while (cursor4.hasNext()) {
//                            String aux = cursor4.next().toString();
//                            JSONObject sensoraux = new JSONObject(aux);
//                            sensoraux.put("status", "vital:Running");
//                            sensorspool.put(sensoraux);
//                        }
//                    }
                    break;
                case CEPFILTERSENSOR_TYPE:
                    // create an empty query
                    BasicDBObject query2 = new BasicDBObject(); 
                    BasicDBObject fields = new BasicDBObject().append("_id",false)
                            .append("query",false).append("data",false)
                            .append("complexEvent", false)
                            .append("source",false).append("event",false)
                            .append("dolceSpecification",false); 

                    FindIterable<Document> coll = db.getCollection("filters")
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
//                    try (DBCursor cursor = coll.find(query2,fields)) {
//                        while(cursor.hasNext()) {
//                            String aux = cursor.next().toString();
//                            JSONObject sensoraux = new JSONObject(aux);  
//                            sensoraux.put("status","vital:Running");
//                            sensorspool.put(sensoraux);
//                        }
//
//                    }
                    break;
                default: 
                     return Response.status(Response.Status.BAD_REQUEST)
                        .build();
                }         
            }
        }else if (filter.has("id")){

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
                            sensorspool.put(sensoraux);
                        }
                    });
//                try (DBCursor cursor = coll.find(query,fields)) {
//                    while(cursor.hasNext()) {
//                        String aux = cursor.next().toString();
//                        JSONObject sensoraux = new JSONObject(aux);  
//                        sensoraux.put("status","vital:Running");
//                        sensorspool.put(sensoraux);
//                        break;
//                    }
//
//                }

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
                            sensorspool.put(sensoraux);
                        }
                });
//                try (DBCursor cursor3 = coll3.find(query, fields3)) {
//                    while (cursor3.hasNext()) {
//                        String aux = cursor3.next().toString();
//                        JSONObject sensoraux = new JSONObject(aux);
//                        sensoraux.put("status", "vital:Running");
//                        sensorspool.put(sensoraux);
//                        break;
//                    }
//
//                }


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
                            sensorspool.put(sensoraux);
                        }
                });
//                try (DBCursor cursor4 = coll4.find(query, fields4)) {
//                    while (cursor4.hasNext()) {
//                        String aux = cursor4.next().toString();
//                        JSONObject sensoraux = new JSONObject(aux);
//                        sensoraux.put("status", "vital:Running");
//                        sensorspool.put(sensoraux);
//                        break;
//                    }
//                }

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
                            sensorspool.put(sensoraux);
                        }
                });
                
//                try (DBCursor cursor2 = coll2.find(query,fieldscep)) {
//                    while(cursor2.hasNext()) {
//                        if (cursor2.next().containsField("id")){
//                            String aux = cursor2.curr().toString();
//                            JSONObject sensoraux = new JSONObject(aux);  
//                            sensoraux.put("status","vital:Running");
//                            sensorspool.put(sensoraux);
//                            break;
//                        }
//                    }
//                }

                if (id.equals("http://"+ host+"/cep/sensor/1")){
                    JSONObject monSensor = new JSONObject();

                    monSensor.put("@context","http://vital-iot.eu/contexts/sensor.jsonld");
                    monSensor.put("id","http://"+ host+"/cep/sensor/1");
                    monSensor.put("type","vital:MonitoringSensor");
                    monSensor.put("name","CEP System Monitoring Sensor");
                    monSensor.put("description",
                            "A virtual sensor that monitors the operationalstate of the CEP system");

                    JSONObject observesItem1 = new JSONObject();

                    observesItem1.put("type","vital:OperationalState");
                    observesItem1.put("id","http://"+host+"/cep/sensor/1/operationalState");


                    JSONObject observesItem2 = new JSONObject();

                    observesItem2.put("type","vital:SysUptime");
                    observesItem2.put("id","http://"+host+"/cep/sensor/1/sysUptime");


                    JSONObject observesItem3 = new JSONObject();

                    observesItem3.put("type","vital:SysLoad");
                    observesItem3.put("id","http://"+host+"/cep/sensor/1/sysLoad");

                    JSONObject observesItem4 = new JSONObject();

                    observesItem4.put("type","vital:errors");
                    observesItem4.put("id","http://"+host+"/cep/sensor/1/errors");

                    JSONArray observes = new JSONArray();

                    observes.put(observesItem1);
                    observes.put(observesItem2);
                    observes.put(observesItem3);
                    //observes.put(observesItem4);     

                    monSensor.put("ssn:observes",observes);
                    monSensor.put("status", "vital:Running");

                    sensorspool.put(monSensor);
                }    
            }
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
    @Path("observation/stream/subscribe")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response subscribeToObservations(String info) throws FileNotFoundException, 
            IOException {
                  
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
        
//        WriteResult result = coll.insert(dbObject);
//
//        if (result.getLastError().getErrorMessage()!= null){
//            return Response.status(Response.Status.BAD_REQUEST)
//                    .build();
//        }else{
//            ObjectId id = (ObjectId)dbObject.get( "_id" );
//             return Response.status(Response.Status.OK)
//                .entity("{\"subscription\":\""+id+"\"}").build();
//        }
         
    }
    
    @POST
    @Path("observation/stream/unsubscribe")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response unSubscribeToObservations(String info) throws FileNotFoundException, 
            IOException {

        MongoClient mongo = new MongoClient(mongoIp, mongoPort);

        MongoDatabase db = mongo.getDatabase(mongoDB);
        
        BasicDBObject query = new BasicDBObject(); 
        BasicDBObject fields = new BasicDBObject().append("_id",false);
        fields.append("dolceSpecification", false);
                 
//        coll.findOneAndDelete(query);
//       
//        
//        final JSONArray sensors = new JSONArray(); 
//        
//        coll.forEach(new Block<Document>() {
//            @Override
//            public void apply(final Document document) {
//                sensors.put(document.get("id"));
//            }
//        });
//        
//        DBObject body = (DBObject) JSON.parse(info);
//        
//        if (!body.containsField("subscription")){
//            return Response.status(Response.Status.BAD_REQUEST)
//                    .build();
//        }
//               
//        //DB db = mongo.getDB("vital");
//       // DBCollection coll = db.getCollection("subscriptions");
//        
//        String sub = body.get("subscription").toString();
//        Document findDocument = new Document("_id", body.get("subscription").toString());
//        MongoCollection<Document>  coll = db.getCollection("subscriptions");
//      
//        ObjectId ob1 = new ObjectId(sub);
//    
//        DBObject findSubscription = new BasicDBObject("_id", ob1);
//        
//        DBObject doc = coll.findOne(findSubscription);
//                
//        if (doc == null){
//            return Response.status(Response.Status.NOT_FOUND)
//                    .build();
//        }
// 
//        coll.remove(doc);
        return Response.status(Response.Status.OK).build();
    }
    
     /**
     * Gets sensors status .
     *
     * @param info
     * @return the metadata of the sensors 
     * @throws java.io.FileNotFoundException 
     */
    @POST
    @Path("sensor/status")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensorStatus(String info) throws FileNotFoundException, 
            IOException {

        MongoClient mongo = new MongoClient(mongoIp, mongoPort);

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
//                    try (DBCursor cursor = coll.find(query,fields)) {
//                        while(cursor.hasNext()) {
//                            String aux = cursor.next().toString();
//                            JSONObject sensoraux = new JSONObject(aux);
//                            sensorspool.put(sensoraux);
//                            break;
//                        }
//                        
//                    }
                    
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
                   
                    
//                    try (DBCursor cursor3 = coll3.find(query, fields3)) {
//                        while (cursor3.hasNext()) {
//                            String aux = cursor3.next().toString();
//                            JSONObject sensoraux = new JSONObject(aux);
//                            sensorspool.put(sensoraux);
//                            break;
//                        }
//                        
//                    }
                    
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
                    
//                    try (DBCursor cursor4 = coll4.find(query, fields4)) {
//                        while (cursor4.hasNext()) {
//                            String aux = cursor4.next().toString();
//                            JSONObject sensoraux = new JSONObject(aux);
//                            sensorspool.put(sensoraux);
//                            break;
//                        }
//                    }
                    

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
                    
//                    try (DBCursor cursor2 = coll2.find(query,fieldscep)) {
//                        while(cursor2.hasNext()) {
//                            if (cursor2.next().containsField("id")){
//                                String aux = cursor2.curr().toString();
//                                JSONObject sensoraux = new JSONObject(aux);
//                                sensorspool.put(sensoraux);
//                                break;
//                            }
//                        }
//                    }
                    
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
                    
                    if (id.equals("http://"+ host+"/cep/sensor/1")){
                        JSONObject opState = new JSONObject();
                        
                        opState.put("@context",
                                "http://vital-iot.eu/contexts/measurement.jsonld");
                        
                        opState.put("id","http://"+host+"/cep/sensor/1/observation/1" );
                        
                        opState.put("type","ssn:Observation");
                        
                        opState.put("ssn:featureOfInterest","http://"+host+"/cep/sensor/1");
                        
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
                        sysUpTime.put("id","http://"+ host+"/cep/sensor/1/observation/2");
                        sysUpTime.put("type","ssn:Observation");
                        
                        JSONObject property2 = new JSONObject();
                        property2.put("type","vital:SysUptime");
                        sysUpTime.put("ssn:observationProperty",property2);
                        
                        JSONObject resultTime2 = new JSONObject();
                        Date date2 = new Date();
                        
                        resultTime2.put("time:inXSDDateTime",dateFormat.format(date2));//check format
                        sysUpTime.put("ssn:observationResultTime",resultTime2);
                        sysUpTime.put("ssn:featureOfInterest","http://"+ host+"/cep/sensor/1");
                        
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
                        sysLoad.put("id","http://"+ host+"/cep/sensor/1/observation/3");
                        sysLoad.put("type","ssn:Observation");
                        
                        JSONObject property3 = new JSONObject();
                        property3.put("type","vital:SysLoad");
                        sysLoad.put("ssn:observationProperty",property3);
                                                
                        JSONObject resultTime3 = new JSONObject();
                        
                        Date date3 = new Date();
                        
                        resultTime3.put("time:inXSDDateTime",dateFormat.format(date3));//check format
                        sysLoad.put("ssn:observationResultTime",resultTime3);
                        sysLoad.put("ssn:featureOfInterest","http://"+ host+"/cep/sensor/1");
                        
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
                    
//                    try (DBCursor cursor2 = coll2.find(query,fieldscep)) {
//                        while(cursor2.hasNext()) {
//                            if (cursor2.next().containsField("id")){
//                                String aux = cursor2.curr().toString();
//                                JSONObject sensoraux = new JSONObject(aux);  
//                                sensorspool.put(sensoraux);
//                            }
//                        }
//                    }

                    break;
                case MONITORINGSENSOR_TYPE:
                    
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
                                     
                    JSONObject opState = new JSONObject();
                        
                    opState.put("@context",
                            "http://vital-iot.eu/contexts/measurement.jsonld");

                    opState.put("id","http://"+host+"/cep/sensor/1/observation/1" );

                    opState.put("type","ssn:Observation");

                    opState.put("ssn:featureOfInterest","http://"+host+"/cep/sensor/1");

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
                    sysUpTime.put("id","http://"+ host+"/cep/sensor/1/observation/2");
                    sysUpTime.put("type","ssn:Observation");

                    JSONObject property2 = new JSONObject();
                    property2.put("type","vital:SysUptime");
                    sysUpTime.put("ssn:observationProperty",property2);

                    JSONObject resultTime2 = new JSONObject();
                    Date date2 = new Date();

                    resultTime2.put("time:inXSDDateTime",dateFormat.format(date2));//check format
                    sysUpTime.put("ssn:observationResultTime",resultTime2);
                    sysUpTime.put("ssn:featureOfInterest","http://"+ host+"/cep/sensor/1");

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
                    sysLoad.put("id","http://"+ host+"/cep/sensor/1/observation/3");
                    sysLoad.put("type","ssn:Observation");

                    JSONObject property3 = new JSONObject();
                    property3.put("type","vital:SysLoad");
                    sysLoad.put("ssn:observationProperty",property3);

                    JSONObject resultTime3 = new JSONObject();

                    Date date3 = new Date();

                    resultTime3.put("time:inXSDDateTime",dateFormat.format(date3));//check format
                    sysLoad.put("ssn:observationResultTime",resultTime3);
                    sysLoad.put("ssn:featureOfInterest","http://"+ host+"/cep/sensor/1");

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

//                    try (DBCursor cursor3 = coll3.find(query, fields3)) {
//                        while (cursor3.hasNext()) {
//                            String aux = cursor3.next().toString();
//                            JSONObject sensoraux = new JSONObject(aux);
//                            sensorspool.put(sensoraux);
//                        }
//
//                    }
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
//                    try (DBCursor cursor4 = coll4.find(query, fields4)) {
//                        while (cursor4.hasNext()) {
//                            String aux = cursor4.next().toString();
//                            JSONObject sensoraux = new JSONObject(aux);
//                            sensorspool.put(sensoraux);
//                        }
//
//                    }
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
//                    try (DBCursor cursor = coll.find(query,fields)) {
//                        while(cursor.hasNext()) {
//                            String aux = cursor.next().toString();
//                            JSONObject sensoraux = new JSONObject(aux);  
//                            sensorspool.put(sensoraux);
//                        }
//
//                    }
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
//                try (DBCursor cursor = coll.find(query,fields)) {
//                    while(cursor.hasNext()) {
//                        JSONObject curr = new JSONObject();
//                        
//                        curr.put("@context",
//                                "http://vital-iot.eu/contexts/measurement.jsonld");
//                        
//                        curr.put("id",cursor.curr().get("id"));
//                        
//                        curr.put("type","ssn:Observation");
//                        
//                        curr.put("ssn:featureOfInterest",cursor.curr().get("id"));
//                        
//                        JSONObject property = new JSONObject();
//                        property.put("type","vital:ComplexEvent");
//                        curr.put("ssn:observationProperty",property);
//                        
//                        JSONObject resultTime = new JSONObject();
//                        
//                        DateFormat dateFormat = new SimpleDateFormat
//                                ("yyyy-MM-dd'T'HH:mm:ssXXX");
//                        Date date = new Date();
//                        
//                        resultTime.put("time:inXSDDateTime",dateFormat.format(date));//check format
//                        
//                        curr.put("ssn:observationResultTime",resultTime);
//                        //"time:inXSDDateTime": "2015-10-14T11:59:11+02:00"
//                        
//                        JSONObject hasValue = new JSONObject();
//                        hasValue.put( "type","ssn:ObservationValue");
//                        hasValue.put( "value","vital:Running");
//                        JSONObject observationResult = new JSONObject();
//                        observationResult.put("ssn:hasValue",hasValue);
//                        observationResult.put("type","ssn:SensorOutput");
//                        curr.put("ssn:observationResult",observationResult);
//                        
//                        sensorspool.put(curr);
//                    }
//
//                }

                //DBCollection coll3 = db.getCollection("staticdatafiltersobservations");
                
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
//                try (DBCursor cursor3 = coll3.find(query, fields3)) {
//                    while (cursor3.hasNext()) {
//                        JSONObject curr = new JSONObject();
//                        
//                        curr.put("@context",
//                                "http://vital-iot.eu/contexts/measurement.jsonld");
//                        
//                        curr.put("id",cursor3.curr().get("id"));
//                        
//                        curr.put("type","ssn:Observation");
//                        
//                        curr.put("ssn:featureOfInterest",cursor3.curr()
//                                .get("ssn:featureOfInterest"));
//                        
//                        JSONObject property = new JSONObject();
//                        property.put("type","vital:ComplexEvent");
//                        curr.put("ssn:observationProperty",property);
//                        
//                        JSONObject resultTime = new JSONObject();
//                        
//                        DateFormat dateFormat = new SimpleDateFormat
//                                ("yyyy-MM-dd'T'HH:mm:ssXXX");
//                        Date date = new Date();
//                        
//                        resultTime.put("time:inXSDDateTime",dateFormat.format(date));//check format
//                        
//                        curr.put("ssn:observationResultTime",resultTime);
//                        //"time:inXSDDateTime": "2015-10-14T11:59:11+02:00"
//                        
//                        JSONObject hasValue = new JSONObject();
//                        hasValue.put( "type","ssn:ObservationValue");
//                        hasValue.put( "value","vital:Running");
//                        JSONObject observationResult = new JSONObject();
//                        observationResult.put("ssn:hasValue",hasValue);
//                        observationResult.put("type","ssn:SensorOutput");
//                        curr.put("ssn:observationResult",observationResult);
//                        
//                        sensorspool.put(curr);
//                    }
//
//                }

                
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
//////                try (DBCursor cursor4 = coll4.find(query, fields4)) {
//////                    while (cursor4.hasNext()) {
//////                        JSONObject curr = new JSONObject();
//////                        
//////                        curr.put("@context",
//////                                "http://vital-iot.eu/contexts/measurement.jsonld");
//////                        
//////                        curr.put("id",cursor4.curr().get("id"));
//////                        
//////                        curr.put("type","ssn:Observation");
//////                        
//////                        curr.put("ssn:featureOfInterest",cursor4.curr()
//////                                .get("ssn:featureOfInterest"));
//////                        
//////                        JSONObject property = new JSONObject();
//////                        property.put("type","vital:ComplexEvent");
//////                        curr.put("ssn:observationProperty",property);
//////                        
//////                        JSONObject resultTime = new JSONObject();
//////                        
//////                        DateFormat dateFormat = new SimpleDateFormat
//////                                ("yyyy-MM-dd'T'HH:mm:ssXXX");
//////                        Date date = new Date();
//////                        
//////                        resultTime.put("time:inXSDDateTime",dateFormat.format(date));//check format
//////                        
//////                        curr.put("ssn:observationResultTime",resultTime);
//////                        //"time:inXSDDateTime": "2015-10-14T11:59:11+02:00"
//////                        
//////                        JSONObject hasValue = new JSONObject();
//////                        hasValue.put( "type","ssn:ObservationValue");
//////                        hasValue.put( "value","vital:Running");
//////                        JSONObject observationResult = new JSONObject();
//////                        observationResult.put("ssn:hasValue",hasValue);
//////                        observationResult.put("type","ssn:SensorOutput");
//////                        curr.put("ssn:observationResult",observationResult);
//////                        
//////                        sensorspool.put(curr);
//////                    }
//////                    
//////                }

                //DBCollection coll2 = db.getCollection("cepsobservations");
                // create an empty query
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

//                try (DBCursor cursor2 = coll2.find(query,fieldscep)) {
//                    while(cursor2.hasNext()) {
//                        if (cursor2.next().containsField("id")){
//                            JSONObject curr = new JSONObject();
//                            
//                            curr.put("@context",
//                                    "http://vital-iot.eu/contexts/measurement.jsonld");
//                            
//                            curr.put("id",cursor2.curr().get("id"));
//                            
//                            curr.put("type","ssn:Observation");
//                            
//                            curr.put("ssn:featureOfInterest",cursor2.curr()
//                                    .get("ssn:featureOfInterest"));//ver
//                            
//                            JSONObject property = new JSONObject();
//                            property.put("type","vital:ComplexEvent");
//                            curr.put("ssn:observationProperty",property);
//                            
//                            JSONObject resultTime = new JSONObject();
//                            
//                            DateFormat dateFormat = new SimpleDateFormat
//                                ("yyyy-MM-dd'T'HH:mm:ssXXX");
//                            Date date = new Date();
//                            
//                            resultTime.put("time:inXSDDateTime",dateFormat.format(date));//check format
//                            
//                            curr.put("ssn:observationResultTime",resultTime);
//                            //"time:inXSDDateTime": "2015-10-14T11:59:11+02:00"
//                            
//                            JSONObject hasValue = new JSONObject();
//                            hasValue.put( "type","ssn:ObservationValue");
//                            hasValue.put( "value","vital:Running");
//                            JSONObject observationResult = new JSONObject();
//                            observationResult.put("ssn:hasValue",hasValue);
//                            observationResult.put("type","ssn:SensorOutput");
//                            curr.put("ssn:observationResult",observationResult);
//                            
//                            sensorspool.put(curr);
//                        }
//                    }
                //}
                
                JSONObject opState = new JSONObject();
                
                opState.put("@context",
                        "http://vital-iot.eu/contexts/measurement.jsonld");
                
                opState.put("id","http://"+host+"/cep/sensor/1/observation/1" );
                
                opState.put("type","ssn:Observation");
                
                opState.put("ssn:featureOfInterest","http://"+host+"/cep/sensor/1");
                
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
                sysUpTime.put("id","http://"+ host+"/cep/sensor/1/observation/2");
                sysUpTime.put("type","ssn:Observation");
                
                JSONObject property2 = new JSONObject();
                property2.put("type","vital:SysUptime");
                sysUpTime.put("ssn:observationProperty",property2);
                
                JSONObject resultTime2 = new JSONObject();
                Date date2 = new Date();
                
                resultTime2.put("time:inXSDDateTime",dateFormat.format(date2));//check format
                sysUpTime.put("ssn:observationResultTime",resultTime2);
                sysUpTime.put("ssn:featureOfInterest","http://"+ host+"/cep/sensor/1");
                
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
                sysLoad.put("id","http://"+ host+"/cep/sensor/1/observation/3");
                sysLoad.put("type","ssn:Observation");

                JSONObject property3 = new JSONObject();
                property3.put("type","vital:SysLoad");
                sysLoad.put("ssn:observationProperty",property3);

                JSONObject resultTime3 = new JSONObject();

                Date date3 = new Date();

                resultTime3.put("time:inXSDDateTime",dateFormat.format(date3));//check format
                sysLoad.put("ssn:observationResultTime",resultTime3);
                sysLoad.put("ssn:featureOfInterest","http://"+ host+"/cep/sensor/1");

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
     * Gets sensors status .
     *
     * @return the metadata of the sensors 
     * @throws java.io.FileNotFoundException 
     */
    @POST
    @Path("system/performance")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPerformanceMetrics(String info) throws FileNotFoundException, 
            IOException {
        
        JSONObject metric1 = new JSONObject();
        
        metric1.put("@context","http://vital-iot.eu/contexts/measurement.jsonld");
        metric1.put("id","http://"+ host+"/cep/sensor/1/observation/3");
        metric1.put("type","ssn:Observation");
        
        JSONObject property = new JSONObject();
        property.put("type","vital:SysLoad");
        metric1.put("ssn:observationProperty",property);
            
       
        
        JSONObject resultTime = new JSONObject();
        
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        Date date = new Date();
       
        resultTime.put("time:inXSDDateTime",dateFormat.format(date));//check format
        metric1.put("ssn:observationResultTime",resultTime);
        metric1.put("ssn:featureOfInterest","http://"+ host+"/cep");
        
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
        metric2.put("id","http://"+ host+"/cep/sensor/1/observation/2");
        metric2.put("type","ssn:Observation");
        
        JSONObject property2 = new JSONObject();
        property2.put("type","vital:SysUptime");
        metric2.put("ssn:observationProperty",property2);
                  
        JSONObject resultTime2 = new JSONObject();
        DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        Date date2 = new Date();
       
        resultTime2.put("time:inXSDDateTime",dateFormat2.format(date2));//check format
        metric2.put("ssn:observationResultTime",resultTime2);
        metric2.put("ssn:featureOfInterest","http://"+ host+"/cep");
        
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
    public Response getSupportedPerformanceMetrics() throws FileNotFoundException, 
            IOException {

        JSONObject metric1 = new JSONObject();
        
        metric1.put("id","http://"+ host+"/cep/sensor/1/sysUptime");
        metric1.put("type","http://vital-iot.eu/ontology/ns/SysUptime");
               
        JSONObject metric2 = new JSONObject();
        
        metric2.put("id","http://"+ host+"/cep/sensor/1/sysLoad");
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

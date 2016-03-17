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
import eu.vital.vitalcep.security.Security;
import org.bson.Document;

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
import javax.servlet.http.HttpServletRequest;

// TODO: Auto-generated Javadoc
/**
 * The Class FilteringRestREST.
 */
@Path("service")
public class Service {

    /** The Constant logger. */
    final static Logger logger = Logger.getLogger(Service.class);
    
    private final String ONTOLOGY = "http://vital-iot.eu/ontology/ns/";
    
    private final String OBSERVATIONSERVICE_TYPE = ONTOLOGY
            +"ObservationService";
    
    private final String CEPFILTERINGSERVICE_TYPE = ONTOLOGY
            +"CEPFitleringService";

    private final String CEPICOMANAGEMENTSERVICE_TYPE = ONTOLOGY
            +"CEPICOManagementService";

    private final String MONITORINGSERVICE_TYPE = ONTOLOGY+"MonitoringService";

    private PropertyLoader props;
    
    private String host;
    
    private String mongoIp;
    
    private int mongoPort;
    
    private String mongoDB;
    private String cookie;

    
   // @Context private javax.servlet.http.HttpServletRequest hsr;
    
    public Service() throws IOException {

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
     * Gets service metadata .
     *
     * @return the metadata of the services
     */
    @POST
    @Path("metadata")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getServiceMetadata(String info
            ,@Context HttpServletRequest req) throws FileNotFoundException,
            IOException {
        
        StringBuilder ck = new StringBuilder();
        Security slogin = new Security();
                  
        Boolean token = slogin.login(req.getHeader("name")
                ,req.getHeader("password"),false,ck);
        if (!token){
              return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        this.cookie = ck.toString(); 
      
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
        
        JSONObject operation3 = new JSONObject();
              
        operation3.put("type","vital:GetObservations");
        operation3.put("hrest:hasAddress","http://"+host.toString()
                +"/cep/observation");
        operation3.put("hrest:hasMethod","hrest:POST");

        JSONArray observationOperations = new JSONArray();
        
        observationOperations.put(operation1);
        observationOperations.put(operation2);
        observationOperations.put(operation3);
        
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
    
    
    
}

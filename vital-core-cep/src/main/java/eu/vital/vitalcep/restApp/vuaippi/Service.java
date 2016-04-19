/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.vital.vitalcep.restApp.vuaippi;

import eu.vital.vitalcep.conf.ConfigReader;
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

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.FileNotFoundException;
import java.io.IOException;

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

    private final String ALERTINGMANAGEMENTSERVICE_TYPE = ONTOLOGY+"AlertingService";
          
    private final String host;
   
           
    public Service() throws IOException {

        ConfigReader configReader = ConfigReader.getInstance();
              
        host = configReader.get(ConfigReader.CEP_BASE_URL);

    }
    
    
    /**
     * Gets service metadata .
     *
     * @param req
     * @return the metadata of the services
     * @throws java.io.IOException
     */
    @POST
    @Path("metadata")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getServiceMetadata(String info
            ,@Context HttpServletRequest req) throws FileNotFoundException,
            IOException {
        
//        StringBuilder ck = new StringBuilder();
//        Security slogin = new Security();
//                  
//        Boolean token = slogin.login(req.getHeader("name")
//                ,req.getHeader("password"),false,ck);
//        if (!token){
//              return Response.status(Response.Status.UNAUTHORIZED).build();
//        }
      
        JSONObject monitoring = new JSONObject();
        
        monitoring.put("@context", 
                "http://vital-iot.eu/contexts/service.jsonld");
        
        monitoring.put("id", host+"/service/monitoring");
        
        monitoring.put("type","vital:MonitoringService");
        
        JSONObject GetSystemStatus = new JSONObject();
        JSONObject GetSensorStatus = new JSONObject();
        JSONObject GetSupportedPerformanceMetrics = new JSONObject();
        JSONObject GetPerformanceMetrics = new JSONObject();
        JSONObject GetSupportedSLAParameters = new JSONObject();
        JSONObject GetSLAParameters = new JSONObject();
        
        GetSystemStatus.put("type","vital:GetSystemStatus");
        GetSystemStatus.put("hrest:hasAddress", host+"/system/status");
        GetSystemStatus.put("hrest:hasMethod","hrest:POST");

        GetSensorStatus.put("type","vital:GetSensorStatus");
        GetSensorStatus.put("hrest:hasAddress",host+"/sensor/status");
        GetSensorStatus.put("hrest:hasMethod","hrest:POST");
        
        GetSupportedPerformanceMetrics
                .put("type","vital:GetSupportedPerformanceMetrics");
        GetSupportedPerformanceMetrics.put("hrest:hasAddress",
                host+"/system/performance");
        GetSupportedPerformanceMetrics.put("hrest:hasMethod","hrest:GET");

        GetPerformanceMetrics.put("type","vital:GetPerformanceMetrics");
        GetPerformanceMetrics.put("hrest:hasAddress",host+"/system/performance");
        GetPerformanceMetrics.put("hrest:hasMethod","hrest:POST");
        
        GetSupportedSLAParameters.put("type","vital:GetSupportedSLAParameters");
        GetSupportedSLAParameters.put("hrest:hasAddress",host+"/system/sla");
        GetSupportedSLAParameters.put("hrest:hasMethod","hrest:GET");
        
        GetSLAParameters.put("type","vital:GetSLAParameters");
        GetSLAParameters.put("hrest:hasAddress",host+"/system/sla");
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
        
        cepico.put("id", host+"/service/cepicosmanagement");
        
        cepico.put("type","vital:CEPICOManagementService");
        
        JSONObject getCepicos = new JSONObject();
        JSONObject getCepico = new JSONObject();
        JSONObject createCepico = new JSONObject();
        JSONObject deleteCepico = new JSONObject();

        
        getCepicos.put("type","vital:GetCEPICOs");
        getCepicos.put("hrest:hasAddress",host+"/getcepicos");
        getCepicos.put("hrest:hasMethod","hrest:GET");

        getCepico.put("type","vital:GetCEPICO");
        getCepico.put("hrest:hasAddress",host+"/getcepico");
        getCepico.put("hrest:hasMethod","hrest:POST");
        
        createCepico.put("type","vital:CreateCEPICO");
        createCepico.put("hrest:hasAddress",
                host+"/createcepico");
        createCepico.put("hrest:hasMethod","hrest:PUT");

        deleteCepico.put("type","vital:DeleteCEPICO");
        deleteCepico.put("hrest:hasAddress",host+"/deletecepico");
        deleteCepico.put("hrest:hasMethod","hrest:DELETE");

        JSONArray cepicosOperations = new JSONArray();
        
        cepicosOperations.put(getCepicos);
        cepicosOperations.put(getCepico);
        cepicosOperations.put(createCepico);
        cepicosOperations.put(deleteCepico);
        
        cepico.put("msm:hasOperation",cepicosOperations );
        
        JSONObject alert = new JSONObject();
        
        alert.put("@context", 
                "http://vital-iot.eu/contexts/service.jsonld");
        
        alert.put("id", host+"/service/alertingmanagement");
        
        alert.put("type","vital:AlertingManagementService");
        
        JSONObject getAlerts = new JSONObject();
        JSONObject getAlert = new JSONObject();
        JSONObject createAlert = new JSONObject();
        JSONObject deleteAlert = new JSONObject();

        
        getAlerts.put("type","vital:GetAlerts");
        getAlerts.put("hrest:hasAddress",host+"/getalerts");
        getAlerts.put("hrest:hasMethod","hrest:GET");

        getAlert.put("type","vital:GetAlert");
        getAlert.put("hrest:hasAddress",host+"/getalert");
        getAlert.put("hrest:hasMethod","hrest:POST");
        
        createAlert.put("type","vital:CreateAlert");
        createAlert.put("hrest:hasAddress",
                host+"/createalert");
        createAlert.put("hrest:hasMethod","hrest:PUT");

        deleteAlert.put("type","vital:DeleteAlert");
        deleteAlert.put("hrest:hasAddress",host+"/deletealert");
        deleteAlert.put("hrest:hasMethod","hrest:DELETE");

        JSONArray AlertsOperations = new JSONArray();
        
        AlertsOperations.put(getAlerts);
        AlertsOperations.put(getAlert);
        AlertsOperations.put(createAlert);
        AlertsOperations.put(deleteAlert);
        
        alert.put("msm:hasOperation",AlertsOperations );
        
        JSONObject filtering = new JSONObject();
        
        filtering.put("@context", 
                "http://vital-iot.eu/contexts/service.jsonld");
        
        filtering.put("id", 
                host+"/service/filtering");
        
        filtering.put("type","vital:CEPFitleringService");
        
        JSONObject CreateContinuousFilter = new JSONObject();
        JSONObject GetContinuousFilters = new JSONObject();
        JSONObject GetContinuousFilter = new JSONObject();
        JSONObject DeleteContinuousFilter = new JSONObject();
        JSONObject FilterStaticData = new JSONObject();
        JSONObject FilterStaticQuery = new JSONObject();
        
        CreateContinuousFilter.put("type","vital:CreateContinuousFilter");
        CreateContinuousFilter.put("hrest:hasAddress",
                host+"/filtering/createcontinuousfilter");
        CreateContinuousFilter.put("hrest:hasMethod","hrest:GET");

        GetContinuousFilters.put("type","vital:GetContinuousFilters");
        GetContinuousFilters.put("hrest:hasAddress",
                host+"/filtering/getcontinuousfilters");
        GetContinuousFilters.put("hrest:hasMethod","hrest:GET");
        
        GetContinuousFilter.put("type","vital:GetContinuousFilter");
        GetContinuousFilter.put("hrest:hasAddress",
                host+"/filtering/getcontinuousfilter");
        GetContinuousFilter.put("hrest:hasMethod","hrest:POST");

        DeleteContinuousFilter.put("type","vital:DeleteContinuousFilter");
        DeleteContinuousFilter.put("hrest:hasAddress",
                host+"/filtering/deletecontinuousfilter");
        DeleteContinuousFilter.put("hrest:hasMethod","hrest:DELETE");

        FilterStaticData.put("type","vital:FilterStaticData");
        FilterStaticData.put("hrest:hasAddress",
                host+"/filtering/filterstaticdata");
        FilterStaticData.put("hrest:hasMethod","hrest:POST");

        FilterStaticQuery.put("type","vital:FilterStaticQuery");
        FilterStaticQuery.put("hrest:hasAddress",
                host+"p/filtering/filterstaticquery");
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
                host+"/service/observation");
        
        observation.put("type","vital:ObservationService");
        
        JSONObject operation1 = new JSONObject();
              
        operation1.put("type","vital:SubscribeToObservationStream");
        operation1.put("hrest:hasAddress",
                host+"/observation/stream/subscribe");
        operation1.put("hrest:hasMethod","hrest:POST");

        JSONObject operation2 = new JSONObject();
              
        operation2.put("type","vital:UnsubscribeFromObservationStream");
        operation2.put("hrest:hasAddress",
                host+"/observation/stream/unsubscribe");
        operation2.put("hrest:hasMethod","hrest:POST");
        
        JSONObject operation3 = new JSONObject();
              
        operation3.put("type","vital:GetObservations");
        operation3.put("hrest:hasAddress",host+"/observation");
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
        services.put(alert);  
         
        if(!info.equals("")){

            try{
                JSONObject filter = new JSONObject(info);
                                
                if(filter.has("type")){
                    JSONArray filteredServices = new JSONArray();

                    JSONArray types = filter.getJSONArray("type");
                    for (int i = 0; i < types.length(); i++) {
                        
                        String type = types.getString(i);
                                               
                        switch (type) {
                        case OBSERVATIONSERVICE_TYPE: case "ObservationService":
                        case "vital:ObservationService":
                            filteredServices.put(observation);
                            break;
                        case MONITORINGSERVICE_TYPE: case "MonitoringService":
                        case "vital:MonitoringService":
                            filteredServices.put(monitoring);
                                 break;
                        case CEPFILTERINGSERVICE_TYPE: case "CEPFitleringService":
                        case "vital:CEPFitleringService":
                            filteredServices.put(filtering);
                                 break;
                        case CEPICOMANAGEMENTSERVICE_TYPE: case "CEPICOManagementService":
                        case "vital:CEPICOManagementService":
                            filteredServices.put(cepico);
                                 break;
                        case ALERTINGMANAGEMENTSERVICE_TYPE: case "AlertingManagementService":
                        case "vital:AlertingManagementService":
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.vital.vitalcep.collector.listener;

import eu.vital.vitalcep.conf.PropertyLoader;
import eu.vital.vitalcep.connectors.dms.DMSManager;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author a601149
 */
public class DMSListener {

    private final  PropertyLoader props;
    private final String dmsURL;
    private final String cookie;

 
    
    public DMSListener (String cookie)
            
        throws FileNotFoundException, IOException{
        
        Logger logger = Logger.getLogger(this.getClass().getName());
        
        this.props = new PropertyLoader();
        this.dmsURL = props.getProperty("dms.base_url");
        this.cookie= cookie; 
                    
    }
    
    public JSONArray getObservations(JSONArray sources, JSONArray properties,
            String from ) throws IOException, UnsupportedEncodingException,
            KeyManagementException, NoSuchAlgorithmException, KeyStoreException{

        JSONObject completequery = new JSONObject();

        JSONArray aData = new JSONArray();

        
        String mongoquery="";
        
        if (from == null){
            Date NOW = new Date();
            from = getXSDDateTime(NOW);
        }
        
        if (properties.length()+sources.length()==2){
          
            try{
                String property1=properties.getString(0) ;
                String sensor1=sources.getString(0) ;
                mongoquery = "{\"http://purl.oclc.org/NET/ssnx/ssn#observationProperty\": "
                        + "[{\"@type\": ["
                        +"\""+property1 +"\"]}],"
                        + "\"http://purl.oclc.org/NET/ssnx/ssn#observedBy\": ["
                        + "{\"@value\": \""+sensor1 +"\"}],"
                        + "\"http://purl.oclc.org/NET/ssnx/ssn#observationResultTime\": "
                        + "{\"$elemMatch\":{ "
                        + "\"http://www.w3.org/2006/time#inXSDDateTime\": "
                        + "{\"$elemMatch\":{ \"@value\" : {\"$gt\": \""
                        + from 
                        +"\"}}}}}}";

                DMSManager oDMS = new DMSManager(dmsURL,cookie);


                aData = oDMS.getObservations(mongoquery);
            }catch(Exception ex){
                java.util.logging.Logger.getLogger(DMSListener
                                    .class.getName())
                                        .log(Level.SEVERE, null, ex);
            }

        }else if (properties.length()+sources.length()>2){
            
            try{
           
                JSONArray ors = new JSONArray();

                int x=0;

                for (int i = 0; i < properties.length(); i++) {
                    for (int j = 0; j < sources.length(); j++) {
                        JSONObject simplequery = new JSONObject();
                        String innerProperty = "[\""+properties.getString(i) +"\"]}]" ;
                        String sensorvalue = sources.getString(j) ;
                        String timeValue = " {\"$elemMatch\":{"
                            + "\"http://www.w3.org/2006/time#inXSDDateTime\": "
                            + "{\"$elemMatch\":{ \"@value\" : {\"$gt\": \""
                            + from 
                            +"\"}}}}}" ;
                        JSONArray propertyarrayInner = new JSONArray(innerProperty);
                        //JSONArray sensorarrayInner = new JSONArray(sensorvalue);
                        JSONObject timeObject = new JSONObject(timeValue);

                        JSONObject property= new JSONObject();
                        JSONObject sensor = new JSONObject();

                        sensor.put("@value",sensorvalue);
                        property.put("@type",propertyarrayInner );

                        JSONArray propertyArray = new JSONArray();
                        JSONArray sensorArray = new JSONArray();

                        propertyArray.put(property);
                        sensorArray.put(sensor);

                        simplequery.put("http://purl.oclc.org/NET/ssnx/ssn#observationProperty",
                                propertyArray);
                        simplequery.put("http://purl.oclc.org/NET/ssnx/ssn#observedBy",
                                sensorArray);
                        simplequery.put("http://purl.oclc.org/NET/ssnx/ssn#observationResultTime",
                                timeObject);

                        ors.put(simplequery);
                        x++;
                    }

                }

                completequery.put("$or",ors);
                DMSManager oDMS = new DMSManager(dmsURL,cookie);

                aData = oDMS.getObservations(completequery.toString());
            }catch(Exception ex){
                java.util.logging.Logger.getLogger(DMSListener
                                    .class.getName())
                                        .log(Level.SEVERE, null, ex);
            }
        }
        
        return aData;
    
    }

    public JSONObject saveSources(JSONArray sources, JSONArray properties,
            String from ) throws IOException, UnsupportedEncodingException,
            KeyManagementException, NoSuchAlgorithmException, KeyStoreException{

        JSONObject completequery = new JSONObject();
        
        String mongoquery="";
        
        if (from == null){
            Date NOW = new Date();
            from = getXSDDateTime(NOW);
        }
        
        if (properties.length()+sources.length()==2){
          
            String property1=properties.getString(0) ;
            String sensor1=sources.getString(0) ;
            mongoquery = "{\"http://purl.oclc.org/NET/ssnx/ssn#observationProperty\": "
                    + "[{\"@type\": ["
                    +"\""+property1 +"\"]}],"
                    + "\"http://purl.oclc.org/NET/ssnx/ssn#observedBy\": ["
                    + "{\"@value\": \""+sensor1 +"\"}],"
                    + "\"http://purl.oclc.org/NET/ssnx/ssn#observationResultTime\": "
                    + "{\"$elemMatch\":{ "
                    + "\"http://www.w3.org/2006/time#inXSDDateTime\": "
                    + "{\"$elemMatch\":{ \"@value\" : {\"$gt\": \""
                    + from 
                    +"\"}}}}}}";
            
             DMSManager oDMS = new DMSManager(dmsURL,cookie);
        
            JSONObject  completequeryAux = new JSONObject(mongoquery);
            
            completequery = completequeryAux;

        }else if (properties.length()+sources.length()>2){
           
            JSONArray ors = new JSONArray();
          
            int x=0;
             
            for (int i = 0; i < properties.length(); i++) {
                for (int j = 0; j < sources.length(); j++) {
                    JSONObject simplequery = new JSONObject();
                    String innerProperty = "[\""+properties.getString(i) +"\"]}]" ;
                    String sensorvalue = sources.getString(j) ;
                    String timeValue = " {\"$elemMatch\":{"
                        + "\"http://www.w3.org/2006/time#inXSDDateTime\": "
                        + "{\"$elemMatch\":{ \"@value\" : {\"$gt\": \""
                        + from 
                        +"\"}}}}}" ;
                    JSONArray propertyarrayInner = new JSONArray(innerProperty);
                    //JSONArray sensorarrayInner = new JSONArray(sensorvalue);
                    JSONObject timeObject = new JSONObject(timeValue);

                    JSONObject property= new JSONObject();
                    JSONObject sensor = new JSONObject();

                    sensor.put("@value",sensorvalue);
                    property.put("@type",propertyarrayInner );

                    JSONArray propertyArray = new JSONArray();
                    JSONArray sensorArray = new JSONArray();

                    propertyArray.put(property);
                    sensorArray.put(sensor);

                    simplequery.put("http://purl.oclc.org/NET/ssnx/ssn#observationProperty",
                            propertyArray);
                    simplequery.put("http://purl.oclc.org/NET/ssnx/ssn#observedBy",
                            sensorArray);
                    simplequery.put("http://purl.oclc.org/NET/ssnx/ssn#observationResultTime",
                            timeObject);

                    ors.put(simplequery);
                    x++;
                }
            
            }
        
            completequery.put("$or",ors);
        }
        return completequery;

    
    }
    
    public JSONObject createRequest(JSONArray sources, JSONArray properties,
            String from ) throws IOException, UnsupportedEncodingException,
            KeyManagementException, NoSuchAlgorithmException, KeyStoreException{

        JSONObject completequery = new JSONObject();
        
        String mongoquery="";
        
        if (from == null){
            Date NOW = new Date();
            from = getXSDDateTime(NOW);
        }
        
        if (properties.length()+sources.length()==2){
          
            String property1=properties.getString(0) ;
            String sensor1=sources.getString(0) ;
            mongoquery = "{\"http://purl.oclc.org/NET/ssnx/ssn#observationProperty\": "
                    + "[{\"@type\": ["
                    +"\""+property1 +"\"]}],"
                    + "\"http://purl.oclc.org/NET/ssnx/ssn#observedBy\": ["
                    + "{\"@value\": \""+sensor1 +"\"}],"
                    + "\"http://purl.oclc.org/NET/ssnx/ssn#observationResultTime\": "
                    + "{\"$elemMatch\":{ "
                    + "\"http://www.w3.org/2006/time#inXSDDateTime\": "
                    + "{\"$elemMatch\":{ \"@value\" : {\"$gt\": \""
                    + from 
                    +"\"}}}}}}";
            
             DMSManager oDMS = new DMSManager(dmsURL,cookie);
        
            JSONObject  completequeryAux = new JSONObject(mongoquery);
            
            completequery = completequeryAux;

        }else if (properties.length()+sources.length()>2){
           
            JSONArray ors = new JSONArray();
          
            int x=0;
             
            for (int i = 0; i < properties.length(); i++) {
                for (int j = 0; j < sources.length(); j++) {
                    JSONObject simplequery = new JSONObject();
                    String innerProperty = "[\""+properties.getString(i) +"\"]}]" ;
                    String sensorvalue = sources.getString(j) ;
                    String timeValue = " {\"$elemMatch\":{"
                        + "\"http://www.w3.org/2006/time#inXSDDateTime\": "
                        + "{\"$elemMatch\":{ \"@value\" : {\"$gt\": \""
                        + from 
                        +"\"}}}}}" ;
                    JSONArray propertyarrayInner = new JSONArray(innerProperty);
                    //JSONArray sensorarrayInner = new JSONArray(sensorvalue);
                    JSONObject timeObject = new JSONObject(timeValue);

                    JSONObject property= new JSONObject();
                    JSONObject sensor = new JSONObject();

                    sensor.put("@value",sensorvalue);
                    property.put("@type",propertyarrayInner );

                    JSONArray propertyArray = new JSONArray();
                    JSONArray sensorArray = new JSONArray();

                    propertyArray.put(property);
                    sensorArray.put(sensor);

                    simplequery.put("http://purl.oclc.org/NET/ssnx/ssn#observationProperty",
                            propertyArray);
                    simplequery.put("http://purl.oclc.org/NET/ssnx/ssn#observedBy",
                            sensorArray);
                    simplequery.put("http://purl.oclc.org/NET/ssnx/ssn#observationResultTime",
                            timeObject);

                    ors.put(simplequery);
                    x++;
                }
            
            }
        
            completequery.put("$or",ors);
        }
        return completequery;

    
    }
      
    private String getXSDDateTime(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        return  dateFormat.format(date);
    }
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.vital.vitalcep.restApp.filteringApi;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import eu.vital.vitalcep.conf.PropertyLoader;
import eu.vital.vitalcep.connectors.mqtt.MessageProcessor;
import eu.vital.vitalcep.connectors.mqtt.MqttConnector;
import eu.vital.vitalcep.connectors.mqtt.MsgQueue;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONObject;
import org.json.JSONArray;

import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import com.mongodb.util.JSON;
import eu.vital.vitalcep.collector.listener.DMSListener;
import eu.vital.vitalcep.publisher.MQTT_connector_subscriper;
import eu.vital.vitalcep.publisher.MessageProcessor_publisher;
import eu.vital.vitalcep.connectors.mqtt.MqttConnectorContainer;
import eu.vital.vitalcep.entities.dolceHandler.DolceSpecification;
import eu.vital.vitalcep.security.Security;

import java.io.FileInputStream;
import java.io.IOException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;

import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.ElasticsearchException;
import org.json.JSONException;







import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;


import javax.net.ssl.SSLContext;


//import trust.*;

import java.io.*;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import javax.ws.rs.HeaderParam;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.bson.Document;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;


// TODO: Auto-generated Javadoc
/**
 * The Class FilteringRestREST.
 */
@Path("")
public class PusshingDMS {

    /** The Constant logger. */
    final static Logger logger = Logger.getLogger(PusshingDMS.class);
   // @Context private javax.servlet.http.HttpServletRequest hsr;
     private Properties config ;
    
    private PropertyLoader props;
    
    private String host;
    
    private String mongoIp;
    
    private int mongoPort;
    
    private String mongoDB;
    
    private String dmsURL;
    
    public PusshingDMS() throws IOException {

        props = new PropertyLoader();

        mongoPort = Integer.parseInt(props.getProperty("mongo.port"));
        mongoIp= props.getProperty("mongo.ip");
        mongoDB = props.getProperty("mongo.db");
                              
        host = props.getProperty("cep.resourceshostname");     
         
        this.dmsURL= props.getProperty("dms.base_url");   
        
        if (host == null || host.isEmpty()){
             host = "localhost:8180";       
        }
        
    }
  
     /**
     * Creates a Observations to dms.
     *
     * @param info
     * @param req
     * @return the filter id 
     * @throws java.io.IOException 
     */
    @POST
    @Path("pusho")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response pushObservationsToDMS(@Context HttpServletRequest req) throws IOException {
        
        String name = req.getHeader("name");
        String password = req.getHeader("password");
       // String remoteHost = req.getRemoteHost();
        String localAddr = "vital-integration.atosresearch.eu";
        int localPort = req.getLocalPort();
        
        System.getenv("PORT");
        String host =  "http"+this.host.concat(":"+localPort);
        
        String url = "http://vital-integration.atosresearch.eu:8180/hireplyppi/sensor/observation";
        String sensor ="http://vital-integration.atosresearch.eu:8180/hireplyppi/sensor/vital2-I_TrS_45";
        String property = "http://vital-iot.eu/ontology/ns/Speed";
        
        JSONArray aData =  getPPIObservations(url,sensor,property);
        
        URL url2 = new URL
       ("https://vitalsp.cloud.reply.eu/vital/vital-core-dms/insertObservation");
        // ("https://vitalsp.cloud.reply.eu:443/vital/dms/insertObservation");

        if (pushPPIObservationsToMongoDMS( name,password,aData,url2)){
              return Response.status(Response.Status.OK)
                            .entity(aData.toString()).build();
        }else{
             return Response.status(Response.Status.BAD_REQUEST).build();
        }
        
    }
    
     /**
     * Creates a Observations to dms.
     *
     * @param info
     * @param req
     * @return the filter id 
     * @throws java.io.IOException 
     */
    @POST
    @Path("pushs")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response pushSensorsToDMS(@Context HttpServletRequest req) throws IOException {
        
        
       // String remoteHost = req.getRemoteHost();
        String localAddr = "vital-integration.atosresearch.eu";
        int localPort = req.getLocalPort();
        
        Properties config = new Properties();
        config.load(new 
        FileInputStream(System.getProperty("jboss.server.config.dir")
                +"/rest_interface.conf"));
        //String host = config.getProperty("cep-ip-address")
          //      .concat(":"+ hsr.getServerPort());
        System.getenv("PORT");
        String host =  localAddr.concat(":"+localPort);
        
        String url = "http://vital-integration.atosresearch.eu:8180/hireplyppi/sensor/metadata";
          
        JSONArray aData =  getPPISensors(url);
        
        URL url2 = new URL
        ("https://vitalsp.cloud.reply.eu:443/vital/dms/insertSensor");

        if (pushPPIObservationsToMongoDMS("elisa","elisotas1",aData,url2)){
              return Response.status(Response.Status.OK)
                            .entity(aData.toString()).build();
        }else{
             return Response.status(Response.Status.BAD_REQUEST).build();
        }
                    
                 
        

         
    }


 
private JSONArray getPPIObservations( String url,
            String sensor,
            String property) throws ParseException {
        JSONArray data = new JSONArray();
        HttpClientBuilder builder = putToSecureDMS();
        
        try {
            final CloseableHttpClient client = builder.build();
            Date NOW = new Date();
            //Date from = new Date();
//            String url = "http://vital-integration.atosresearch.eu:8180/hireplyppi/sensor/observation";
//            String sensor ="http://vital-integration.atosresearch.eu:8180/hireplyppi/sensor/vital2-I_TrS_46";
//            String property = "http://vital-iot.eu/ontology/ns/Speed";
            //String sfrom = from == null ? null : DATE_FORMAT.format(from);
            String sfrom = "2014-11-17T09:00:00+02:00"; 
            HttpPost post = new HttpPost(url);
            post.setHeader(HTTP.CONTENT_TYPE, "application/json");
            JSONObject bodyrequest = new JSONObject();
            JSONArray sensors = new JSONArray();
            sensors.put(sensor);
            bodyrequest.put("sensor", sensors);
            bodyrequest.put("property", property);
            bodyrequest.put("from", sfrom);
            bodyrequest.put("to","2016-11-17T09:00:00+02:00");

            HttpEntity entity = new StringEntity(bodyrequest.toString());
            post.setEntity(entity);
            HttpResponse clientresponse = client.execute(post);
            if (clientresponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
                return  null;
            final String sdata = EntityUtils.toString(clientresponse.getEntity(), StandardCharsets.UTF_8);
            data = new JSONArray( sdata);
        } catch (IOException ioe) {
            logger.error(ioe);
        }
        
        return data;
    }

     private JSONArray getPPISensors( String url
            ) throws ParseException {
        JSONArray data = new JSONArray();
        HttpClientBuilder builder = putToSecureDMS();
        
        try {
            final CloseableHttpClient client = builder.build();
            Date NOW = new Date();
            //Date from = new Date();
//            String url = "http://vital-integration.atosresearch.eu:8180/hireplyppi/sensor/observation";
//            String sensor ="http://vital-integration.atosresearch.eu:8180/hireplyppi/sensor/vital2-I_TrS_46";
//            String property = "http://vital-iot.eu/ontology/ns/Speed";
            //String sfrom = from == null ? null : DATE_FORMAT.format(from);
        //    String sfrom = "2014-11-17T09:00:00+02:00"; 
            HttpPost post = new HttpPost(url);
            post.setHeader(HTTP.CONTENT_TYPE, "application/json");
            JSONObject bodyrequest = new JSONObject();
        //    JSONArray sensors = new JSONArray();
          //  sensors.put(sensor);
           // bodyrequest.put("sensor", sensors);
           // bodyrequest.put("property", property);
        //    bodyrequest.put("from", sfrom);
        //    bodyrequest.put("to","2015-11-17T09:00:00+02:00");

            HttpEntity entity = new StringEntity(bodyrequest.toString());
            post.setEntity(entity);
            HttpResponse clientresponse = client.execute(post);
            if (clientresponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
                return  null;
            final String sdata = EntityUtils.toString(clientresponse.getEntity(), StandardCharsets.UTF_8);
            data = new JSONArray( sdata);
        } catch (IOException ioe) {
            logger.error(ioe);
        }
        
        return data;
    }
     
    private HttpClientBuilder putToSecureDMS() {
        HttpClientBuilder builder = HttpClientBuilder.create();
        StringBuilder ck = new StringBuilder();
        String username = "elisa";
        String pass = "elisotas1";
        String resp = null;
        resp = login(username, pass, false, ck);
        String cookie = ck.toString(); // the SSO cookie used for users
        // Initializing connection variable (will be used throughout the code)
        // String cookieAdv = ck.toString(); // a test session cookie (no SSO)
        HttpURLConnection connection = null;
        // Of course everything will go over HTTPS (here we trust anything, we do not check the certificate)
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("TLS");
        } catch(NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        URL url;
        InputStream is;
        BufferedReader rd;
        char cbuf[] = new char[10000];
        int len;
        String resource = null;
        String urlParameters = "resources[]=" + resource + "&testCookie=false"; // test cookie is the user performing the evalaution
        // The array of resources to evaluate policies on must be included
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        try {
            url = new URL("https://vitalgateway.cloud.reply.eu/securitywrapper/rest/evaluate");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            connection.setUseCaches(false);
            connection.setDoOutput(true);
            
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Content-Length", Integer.toString(postDataLength));

            connection.setRequestProperty("Cookie", cookie ); // Include cookies (permissions evaluated for normal user, advanced user has the rights to evaluate)
            
            // Send request
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.write(postData);
            wr.close();
            
            // Get Response  
            is = connection.getInputStream();
            rd = new BufferedReader(new InputStreamReader(is));
            len = rd.read(cbuf);
            resp = String.valueOf(cbuf).substring(0, len);
            rd.close();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(connection != null) {
                connection.disconnect(); 
            }
        }
        return builder;
    }

    
    private JSONObject createOperationalStateObservation(String host1, String randomUUIDString) throws JSONException {
        JSONObject opState = new JSONObject();
        opState.put("@context",
                "http://vital-iot.eu/contexts/measurement.jsonld");
        opState.put("id", "http://" + host1.toString() + "/cep/sensor/" + randomUUIDString + "/observation/1");
        opState.put("type","ssn:Observation");
        opState.put("ssn:featureOfInterest", "http://" + host1.toString() + "/cep/sensor/" + randomUUIDString);
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

    private DBObject createCEPSensorJsonld(String info,String host1, String randomUUIDString, JSONObject jo, JSONObject dsjo) throws JSONException {
        DBObject dbObject = (DBObject) JSON.parse(info);

        dbObject.removeField("id");
        dbObject.put("@context","http://vital-iot.eu/contexts/sensor.jsonld");
        dbObject.put("id", "http://" + host1.toString() + "/cep/sensor/" + randomUUIDString);
        dbObject.put("name",jo.getString("name") );
        //dbObject.put("type", "vital:CEPFilterStaticDataSensor");
        dbObject.put("type", "vital:CEPSensor");
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
            //oObserves.put("uri", "http://"+ host.toString()
            //        +"/cep/sensor/"+randomUUIDString
            //        +"/"+oComplex.getString("id").toString());
            oObserves.put("id", "http://" + host1.toString() + "/cep/sensor/" + randomUUIDString + "/" + oComplex.getString("id").toString());
            observes.put(oObserves);
        }
        DBObject dbObject2 = (DBObject)JSON.parse(observes
                .toString());
        dbObject.put("ssn:observes",dbObject2);
        dbObject.put("status","vital:running");
        return dbObject;
    }
    
private JSONArray pushToMongoDMS(JSONArray aOutput) throws IOException, JSONException, RuntimeException {
    JSONArray data = new JSONArray();
        HttpClientBuilder builder = HttpClientBuilder.create();
        try {
            final CloseableHttpClient client = builder.build();
            Date NOW = new Date();
            //Date from = new Date();
            String url = "http://vital-integration.atosresearch.eu:8180/hireplyppi/sensor/observation";
            String sensor ="http://vital-integration.atosresearch.eu:8180/hireplyppi/sensor/vital2-I_TrS_45";
            String property = "http://vital-iot.eu/ontology/ns/Speed";
            //String sfrom = from == null ? null : DATE_FORMAT.format(from);
            String sfrom = "2014-11-17T09:00:00+02:00"; 
            HttpPost post = new HttpPost(url);
            post.setHeader(HTTP.CONTENT_TYPE, "application/json");
            JSONObject bodyrequest = new JSONObject();
            JSONArray sensors = new JSONArray();
            sensors.put(sensor);
            bodyrequest.put("sensor", sensors);
            bodyrequest.put("property", property);
            bodyrequest.put("from", sfrom);
            bodyrequest.put("to","2015-11-17T09:00:00+02:00");

            HttpEntity entity = new StringEntity(bodyrequest.toString());
            post.setEntity(entity);
            HttpResponse clientresponse = client.execute(post);
            if (clientresponse.getStatusLine()
                    .getStatusCode() != HttpStatus.SC_OK){
                return null;
            }
            final String sdata = EntityUtils.toString(clientresponse
                    .getEntity(), StandardCharsets.UTF_8);
            data = new JSONArray( sdata);
        } catch (IOException ioe) {
            logger.error(ioe);
        }
        
        return data;
}

private boolean pushPPIObservationsToMongoDMS( String username,String pass,JSONArray aOutput, URL url) throws IOException, JSONException, RuntimeException {
     HttpClientBuilder builder = HttpClientBuilder.create();
        StringBuilder ck = new StringBuilder();
       
        String resp = login(username, pass, false, ck);
        String cookie = ck.toString(); // the SSO cookie used for users
        // Initializing connection variable (will be used throughout the code)
        // String cookieAdv = ck.toString(); // a test session cookie (no SSO)
        HttpURLConnection connection = null;
        // Of course everything will go over HTTPS (here we trust anything, we do not check the certificate)
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("TLS");
        } catch(NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        
        InputStream is;
        BufferedReader rd;
        char cbuf[] = new char[10000];
        int len;

        String urlParameters = aOutput.toString(); // test cookie is the user performing the evalaution
        // The array of resources to evaluate policies on must be included
        
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            connection.setUseCaches(false);
            connection.setDoOutput(true);
            
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Content-Length", Integer.toString(postDataLength));

            connection.setRequestProperty("Cookie", cookie ); // Include cookies (permissions evaluated for normal user, advanced user has the rights to evaluate)
         try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
             wr.write(postData);
         }
            
            
            // Get Response  
            is = connection.getInputStream();
            rd = new BufferedReader(new InputStreamReader(is));
            len = rd.read(cbuf);
            String resp2 = String.valueOf(cbuf).substring(0, len);
            rd.close();
        } catch(Exception e) {
            return false;
        } finally {
            if(connection != null) {
                connection.disconnect(); 
            }
        }
        return true;
}

   
class mqttAllInOne{
     public boolean sendMsg (MessageProcessor processor, String name, ArrayList<String> simpleEvents,
             String mqin, String mqout ){
      MessageProcessor msgProc = processor;
      MsgQueue queue = new MsgQueue(msgProc);
      //                                  ( name, msgQueue, cepInputTopicName, cepOutputTopicName, qos)
      MqttConnector connector = new MqttConnector(name, queue, mqin, mqout, 2);
      
      if (connector!=null){
          
          try{
///          PrintWriter writer = new PrintWriter("/home/a601149/elastic_input.txt", "UTF-8");
       
          for (int i=0; i<simpleEvents.size();i++){
            connector.publishMsg(simpleEvents.get(i));
   //         writer.println(simpleEvents.get(i));
          }
   //       writer.close();
          connector.disconnect();

          }catch(Exception e){}
       try {
            Thread.sleep(1000);                 //1000 milliseconds is one second.
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
       return true;
      }else
       return false;
      
     }
     
    
    }


 private static String login(String user, String password, boolean testCookie, StringBuilder cookie) {
        URL url;
        InputStream is;
        BufferedReader rd;
        char cbuf[] = new char[10000];
        String resp = null;
        int len;
        HttpURLConnection connection = null;
        String ck;

        // Create connection
        String urlParameters;
        if(testCookie) {
            urlParameters = "name=" + user + "&password=" + password + "&testCookie=true";
        }
        else {
            urlParameters = "name=" + user + "&password=" + password + "&testCookie=false";
        }
        // The parameters are user name, password and a flag saying if you want the test cookie or not...
        // ... the test cookie is not the SSO cookie
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        try {
            url = new URL("https://vitalgateway.cloud.reply.eu/securitywrapper/rest/authenticate");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            connection.setUseCaches(false);
            connection.setDoOutput(true);
            
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Content-Length", Integer.toString(postDataLength));

            // Send request
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.write(postData);
            wr.close();

            // Get Response  
            is = connection.getInputStream();
            rd = new BufferedReader(new InputStreamReader(is));
            len = rd.read(cbuf);
            resp = String.valueOf(cbuf).substring(0, len);
            rd.close();
            String headerName = null;
            // Look for the session cookie
            for(int i = 1; (headerName = connection.getHeaderFieldKey(i)) != null; i++) {
                 if(headerName.equalsIgnoreCase("Set-Cookie")) {                  
                     ck = connection.getHeaderField(i);
                     cookie.append(ck.substring(0, ck.indexOf(";")));
                 }
            }
        } catch(Exception e) {
            //log
        } finally {
            if(connection != null) {
                connection.disconnect(); 
            }
            return resp;
        }
    }
 
 
  /**
     * Creates a Observations to dms.
     *
     * @param obstime
     * @param info
     * @param req
     * @return the filter id 
     * @throws java.io.IOException 
     */
    @POST
    @Path("date")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response dataa(String data 
            ,@Context HttpServletRequest req ) throws IOException,
        java.text.ParseException,
        UnsupportedEncodingException,
        KeyManagementException,
        NoSuchAlgorithmException,
        KeyStoreException {

       
        
//        MessageProcessor_publisher Publisher_MsgProcc = 
//                new MessageProcessor_publisher(dmsURL,cookie);//555
//        MQTT_connector_subscriper publisher = new MQTT_connector_subscriper
//         ("mqout",Publisher_MsgProcc);
//        MqttConnectorContainer.addConnector(publisher.getClientName(), publisher);

        //TODO --> DESTROY DEL CONNECTOR.

       // MqttConnectorContainer.deleteConnector(publisher.getClientName());
        
//        StringBuilder ck = new StringBuilder();
//        Security slogin = new Security();
//                  
//        Boolean token = slogin.login(req.getHeader("name")
//                ,req.getHeader("password"),false,ck);
//        if (!token){
//              return Response.status(Response.Status.UNAUTHORIZED).build();
//        }
//        
//        JSONObject jdata = new JSONObject(data);
//        
//        DMSListener dms = new DMSListener( ck.toString());
//        
//        
//        
//        JSONArray odata = dms.getObservations(jdata.getJSONArray("sources")
//                , jdata.getJSONArray("properties"), jdata.getString("from") );
//      
//        return Response.status(Response.Status.OK).entity(odata.toString()).build();
        
        props = new PropertyLoader();
        
        props.getProperty("cep.ip");

        mongoPort = Integer.parseInt(props.getProperty("mongo.port"));
        mongoIp = props.getProperty("mongo.ip");
        mongoDB = props.getProperty("mongo.db");
        
        JSONObject jo = new JSONObject(data);
        JSONObject dsjo = jo.getJSONObject("dolceSpecification");
        JSONArray sources = jo.getJSONArray("sources");
        String str = dsjo.toString();
        DolceSpecification ds = new DolceSpecification(str);

        if(!(ds instanceof DolceSpecification)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        MongoClient mongo = new MongoClient(mongoIp, mongoPort);
            MongoDatabase db = mongo.getDatabase(mongoDB);
            
        Document doc = new Document();
        BasicDBList sourcesB = (BasicDBList) JSON.parse(sources.toString());
                      doc.put("sources", sourcesB);
        BasicDBList propertiesB = (BasicDBList) JSON
                             .parse(ds.getEvents().toString());

        doc.put("properties", propertiesB);
                      //doc.put("lastRequest", getXSDDateTime(NOW));
                      
        db.getCollection("prueba").insertOne(doc);
        return Response.status(Response.Status.OK).build();
    }

}

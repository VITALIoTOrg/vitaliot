/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.vital.vitalcep.restApp.filteringApi;

import eu.vital.vitalcep.conf.PropertyLoader;
import eu.vital.vitalcep.connectors.mqtt.MqttMsg;
import eu.vital.vitalcep.connectors.mqtt.TMessageProc;
import eu.vital.vitalcep.entities.dolceHandler.DolceSpecification;
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
import com.mongodb.MongoException;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import org.bson.Document;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;

import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import eu.vital.vitalcep.cep.CEP;
import eu.vital.vitalcep.connectors.mqtt.MqttAllInOne;
import eu.vital.vitalcep.collector.decoder.Decoder;
import eu.vital.vitalcep.connectors.dms.DMSManager;
import eu.vital.vitalcep.publisher.MessageProcessor_publisher;
import eu.vital.vitalcep.publisher.encoder.Encoder;
import eu.vital.vitalcep.security.Security;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.ElasticsearchException;
import org.json.JSONException;

// TODO: Auto-generated Javadoc
/**
 * The Class FilteringRestREST.
 */
@Path("filtering")
public class StaticFiltering {

    /** The Constant logger. */
    final static Logger logger = Logger.getLogger(StaticFiltering.class);
   // @Context private javax.servlet.http.HttpServletRequest hsr;
    
    private PropertyLoader props;
    
    private String host;
    private String hostname;
    private String mongoIp;
    
    private int mongoPort;
    
    private String mongoDB;
    
    private String dmsURL;
    private String cookie;
    
    public StaticFiltering() throws IOException {

        props = new PropertyLoader();
        mongoPort = Integer.parseInt(props.getProperty("mongo.port"));
        mongoIp= props.getProperty("mongo.ip");
        mongoDB = props.getProperty("mongo.db");
        host = props.getProperty("cep.ip").concat(":8180");
        hostname = props.getProperty("cep.resourceshostname");
        dmsURL = props.getProperty("dms.base_url");
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
    
    int localPort = req.getLocalPort();
    
     
    System.getenv("PORT");
    String hostnameport =  this.hostname.concat(":"+localPort);

    JSONObject jo = new JSONObject(info);

    if ( jo.has("dolceSpecification")&& jo.has("data")) { // && jo.has("data") for demo

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

        //DB db = mongo.getDB("vital");
        //DBCollection coll = db.getCollection("staticdatafilters");

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

                CEP cepProcess = new CEP(CEP.CEPType.DATA,ds
                        ,mqin,mqout,aData.toString());
                
                
                String clientName = cepProcess.fileName;

                if (cepProcess.PID<1){
                    return Response.status(Response
                            .Status.INTERNAL_SERVER_ERROR).build();
                }
               

                UUID uuid = UUID.randomUUID();
                String randomUUIDString = uuid.toString();

                DBObject dbObject = createCEPFilterStaticSensorJsonld(info
                        ,hostnameport, randomUUIDString, jo, dsjo
                        ,"vital:CEPFilterStaticDataSensor");
                Document doc = new Document(dbObject.toMap());

                try{
                    db.getCollection("staticdatafilters").insertOne(doc);
                    String id = doc.get("_id").toString();

                }catch(MongoException ex
                        ){
                    return Response.status(Response.Status.BAD_REQUEST)
                            .build();
                }

                JSONObject opState = createOperationalStateObservation(hostnameport
                        ,randomUUIDString);

                DBObject oPut =  (DBObject)JSON.parse(opState.toString());
                Document doc1 = new Document(oPut.toMap());

                try{
                    db.getCollection("staticdatafiltersobservations").insertOne(doc1);
                    String id = doc1.get("_id").toString();

                }catch(MongoException ex
                        ){
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
                oMqtt.sendMsg(MsgProcc, "wildfly", simpleEventAL,mqin,mqout);
//                            
//                            //prueba de data jsonld de entrada
//                   
//                            ArrayList<String> mesagges = DolceInputOutput.speedJsonldArray2DolceInput
//        (data);
//
/////////////////////////////////////////////////////////////////////////////
                //RECEIVING FROM MOSQUITO               
                ArrayList<MqttMsg> mesagges = MsgProcc.getMsgs();

                //FORMATTING OBSERVATIONS OUTPUT
                JSONArray aOutput ;
                
                 Encoder encoder = new Encoder();

                aOutput= encoder.dolceOutputList2JsonldArray
                    (mesagges, hostnameport, randomUUIDString);
                
                StringBuilder ck = new StringBuilder();
                
                 try {
                    Security slogin = new Security();
                  

                    Boolean token = slogin.login(req.getHeader("name")
                            ,req.getHeader("password"),false,ck);
                    if (!token){
                          return Response.status(Response.Status.UNAUTHORIZED).build();
                    }
                    cookie = ck.toString(); 
                    
                    DMSManager oDMS = new DMSManager(dmsURL,cookie);
        
                    if (!oDMS.pushObservations(aOutput.toString())){
                           //
                    } else {
                    }
                    
                    
                    
                    
                } catch (KeyManagementException | KeyStoreException ex) {
                    java.util.logging.Logger.getLogger(MessageProcessor_publisher
                            .class.getName()).log(Level.SEVERE, null, ex);
                }
                
                
                if (!cepProcess.cepDispose()){
                    //TODO: log
                };
///////////////////////////////////////////////////////////////////////////////
                    //SENDING TO DMSManager

//                        if (aOutput.length()>0){
//                            //pushToElasticDMS(aOutput);
//                        }

                    return Response.status(Response.Status.OK)
                        .entity(aOutput.toString()).build();


            }catch(IOException | JSONException | java.text.ParseException e){
                 return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
    }   

    return Response.status(Response.Status.BAD_REQUEST).build();


    }   

    return Response.status(Response.Status.BAD_REQUEST).build();

}

 

    
     /**
     * Creates a CEPICO.
     *
     * @param info
     * @param req
     * @return the filter id 
     * @throws java.io.IOException 
     */
    @POST
    @Path("publish")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response publish(String info,@Context HttpServletRequest req) throws IOException {
        
        ArrayList<MqttMsg> myMessage = getComplex("myTopic");
                        
        return Response.status(Response.Status.OK)
                            .entity(myMessage.toString()).build();

    }

    private ArrayList<MqttMsg> getComplex(String subscribTopic) {
        //hacer algo
       // MqttAllInOne oMqtt = new MqttAllInOne();
        TMessageProc MsgProcc = new TMessageProc();
        ArrayList<MqttMsg> mesagges = MsgProcc.getMsgs();
        
        //FORMATTING OBSERVATIONS OUTPUT
        
        return mesagges;
    }
   
    private JSONArray getPPIObservations() throws ParseException {
        JSONArray data = new JSONArray();
        HttpClientBuilder builder = HttpClientBuilder.create();
        try {
            final CloseableHttpClient client = builder.build();
            //Date from = new Date();
            String url = "http://vital-integration.atosresearch.eu:8180/hireplyppi/sensor/observation";
            String sensor ="http://vital-integration.atosresearch.eu:8180/hireplyppi/sensor/vital2-I_TrS_45";
            String property = "http://vital-iot.eu/ontology/ns/Speed";
            //String sfrom = from == null ? null : DATE_FORMAT.format(from);
            String sfrom = "2013-11-17T09:00:00+02:00"; 
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
//
//    private JSONArray getDMSObservations(String query) throws ParseException {
//        JSONArray data = new JSONArray();
//        HttpClientBuilder builder = HttpClientBuilder.create();
//        try {
//            final CloseableHttpClient client = builder.build();
//            Date NOW = new Date();
//            //Date from = new Date();
//            String url = "http://vital-integration.atosresearch.eu:8180/dms/queryObservation";
//           
//            //String sfrom = from == null ? null : DATE_FORMAT.format(from);
//            HttpPost post = new HttpPost(url);
//            post.setHeader(HTTP.CONTENT_TYPE, "application/json");
//            JSONObject bodyrequest = new JSONObject(query);
//
//            HttpEntity entity = new StringEntity(bodyrequest.toString());
//            post.setEntity(entity);
//            HttpResponse clientresponse = client.execute(post);
//            if (clientresponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
//                return  null;
//            final String sdata = EntityUtils.toString(clientresponse.getEntity(), StandardCharsets.UTF_8);
//            data = new JSONArray( sdata);
//        } catch (IOException ioe) {
//            logger.error(ioe);
//        }
//        
//        return data;
//    }

    private JSONArray getFromElasticDMS() throws ElasticsearchException, JSONException {
        /////////////////////////////////////////////////////////////////////////
        // GETTING DATA FROM elasticsearch DMSManager
        
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("cluster.name", "vital").build();
        Client esClient = new TransportClient(settings)
                .addTransportAddress
                                (new InetSocketTransportAddress
                                ("vital-integration.atosresearch.eu",9300));
        JSONArray aData = new JSONArray();
        int scrollSize = 1000;
        List<Map<String,Object>> esData = new ArrayList<>();
        SearchResponse response = null;
        int z = 0;
        // while( response == null || response.getHits().hits().length != 0){ // for demo to take just 1000 docs
        response = esClient.prepareSearch("dms")
                .setTypes("measurement")
                .setQuery(QueryBuilders
                        .matchQuery("ssn:observationProperty.type",
                                "vital:Speed"))
                .setSize(scrollSize)
                .setFrom(z * scrollSize)
                .execute()
                .actionGet();
        for(SearchHit hit : response.getHits()){
                            esData.add(hit.getSource());
                            JSONObject oDta = new JSONObject
                                    (hit.getSourceAsString());
                            aData.put(oDta);
        }
        z++;
        //  }
        return aData;
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
    
 private DBObject createCEPFilterStaticSensorJsonld(String info,
         String host1, String randomUUIDString, JSONObject jo, JSONObject dsjo,
         String type) throws JSONException {
        DBObject dbObject = (DBObject) JSON.parse(info);

        dbObject.removeField("id");
        dbObject.put("@context","http://vital-iot.eu/contexts/sensor.jsonld");
        dbObject.put("id", "http://" + host1.toString() + "/cep/sensor/" + randomUUIDString);
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
 
//private JSONArray pushToMongoDMS(JSONArray aOutput) throws IOException, JSONException, RuntimeException {
//    JSONArray data = new JSONArray();
//        HttpClientBuilder builder = HttpClientBuilder.create();
//        try {
//            final CloseableHttpClient client = builder.build();
//            Date NOW = new Date();
//            //Date from = new Date();
//            String url = "http://vital-integration.atosresearch.eu:8180/hireplyppi/sensor/observation";
//            String sensor ="http://vital-integration.atosresearch.eu:8180/hireplyppi/sensor/vital2-I_TrS_45";
//            String property = "http://vital-iot.eu/ontology/ns/Speed";
//            //String sfrom = from == null ? null : DATE_FORMAT.format(from);
//            String sfrom = "2014-11-17T09:00:00+02:00"; 
//            HttpPost post = new HttpPost(url);
//            post.setHeader(HTTP.CONTENT_TYPE, "application/json");
//            JSONObject bodyrequest = new JSONObject();
//            JSONArray sensors = new JSONArray();
//            sensors.put(sensor);
//            bodyrequest.put("sensor", sensors);
//            bodyrequest.put("property", property);
//            bodyrequest.put("from", sfrom);
//            bodyrequest.put("to","2015-11-17T09:00:00+02:00");
//
//            HttpEntity entity = new StringEntity(bodyrequest.toString());
//            post.setEntity(entity);
//            HttpResponse clientresponse = client.execute(post);
//            if (clientresponse.getStatusLine()
//                    .getStatusCode() != HttpStatus.SC_OK){
//                return null;
//            }
//            final String sdata = EntityUtils.toString(clientresponse
//                    .getEntity(), StandardCharsets.UTF_8);
//            data = new JSONArray( sdata);
//        } catch (IOException ioe) {
//            logger.error(ioe);
//        }
//        
//        return data;
//}

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
 
     int localPort = req.getLocalPort();
        
     
    System.getenv("PORT");
    String hostnameport =  this.hostname.concat(":"+localPort);

    JSONObject jo = new JSONObject(info);

    if ( jo.has("dolceSpecification")&& jo.has("query")) { // && jo.has("data") for demo

        MongoClient mongo = new MongoClient(mongoIp, mongoPort);
        MongoDatabase db = mongo.getDatabase(mongoDB);

        try {
           db.getCollection("staticqueryfilters");
        } catch (Exception e) {
          //System.out.println("Mongo is down");
          mongo.close();
          return Response.status(Response
                            .Status.INTERNAL_SERVER_ERROR).build();

        }

        //DB db = mongo.getDB("vital");
        //DBCollection coll = db.getCollection("staticdatafilters");

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

                CEP cepProcess = new CEP(CEP.CEPType.QUERY,ds
                        ,mqin,mqout,jo.getString("query"));

                String clientName = cepProcess.fileName;

                if (cepProcess.PID<1){
                    return Response.status(Response
                            .Status.INTERNAL_SERVER_ERROR).build();
                }

                UUID uuid = UUID.randomUUID();
                String randomUUIDString = uuid.toString();

                DBObject dbObject = createCEPFilterStaticSensorJsonld(info
                    , hostnameport, randomUUIDString, jo, dsjo
                    ,"vital:CEPFilterStaticQuerySensor");
                Document doc = new Document(dbObject.toMap());

                try{
                    db.getCollection("staticqueryfilters").insertOne(doc);
                    String id = doc.get("_id").toString();

                }catch(MongoException ex
                        ){
                    return Response.status(Response.Status.BAD_REQUEST)
                            .build();
                }

                JSONObject opState = createOperationalStateObservation(hostnameport
                        ,randomUUIDString);

                DBObject oPut =  (DBObject)JSON.parse(opState.toString());
                Document doc1 = new Document(oPut.toMap());

                try{
                    db.getCollection("staticqueryfiltersobservations")
                            .insertOne(doc1);
                    String id = doc1.get("_id").toString();

                }catch(MongoException ex
                        ){
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .build();
                }

                /////////////////////////////////////////////////////
                // creates client and messages process
                //
                MqttAllInOne oMqtt = new MqttAllInOne();
                TMessageProc MsgProcc = new TMessageProc();
                
                JSONArray aData =  new JSONArray();
                StringBuilder ck = new StringBuilder();
                
                 try {
                    Security slogin = new Security();
                  

                    
                    Boolean token = slogin.login(req.getHeader("name")
                            ,req.getHeader("password"),false,ck);
                    if (!token){
                          return Response.status(Response.Status.UNAUTHORIZED).build();
                    }
                    cookie = ck.toString(); 
                    
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
                oMqtt.sendMsg(MsgProcc, "wildfly", simpleEventAL,mqin,mqout);
//                            
//                            //prueba de data jsonld de entrada
//                   
//                            ArrayList<String> mesagges = DolceInputOutput.speedJsonldArray2DolceInput
//        (data);
//
/////////////////////////////////////////////////////////////////////////////
                //RECEIVING FROM MOSQUITO
                ArrayList<MqttMsg> mesagges = MsgProcc.getMsgs();

                //FORMATTING OBSERVATIONS OUTPUT
                JSONArray aOutput ;
                Encoder encoder = new Encoder();
                aOutput= encoder.dolceOutputList2JsonldArray
                    (mesagges, hostnameport, randomUUIDString);
               
                try {

                    DMSManager pDMS = new DMSManager(dmsURL,cookie);
        
                    if (!pDMS.pushObservations(aOutput.toString())){
                        java.util.logging.Logger.getLogger
        (StaticFiltering.class.getName())
                                .log(Level.SEVERE, "coudn't save to the DMS" );
                    }
                } catch (IOException | KeyManagementException 
                        | NoSuchAlgorithmException | KeyStoreException ex) {
                    java.util.logging.Logger.getLogger
        (StaticFiltering.class.getName())
                            .log(Level.SEVERE, null, ex);
                }
                             
                if (!cepProcess.cepDispose()){
                     java.util.logging.Logger.getLogger
        (StaticFiltering.class.getName()).log(Level.SEVERE, 
                "bcep Instance not terminated" );
                };
///////////////////////////////////////////////////////////////////////////////
                    //SENDING TO DMSManager

//                        if (aOutput.length()>0){
//                            //pushToElasticDMS(aOutput);
//                        }
                    return Response.status(Response.Status.OK)
                        .entity(aOutput.toString()).build();

            }catch(IOException | JSONException | NoSuchAlgorithmException 
                    | java.text.ParseException e){
                 return Response.status(Response.Status
                         .INTERNAL_SERVER_ERROR).build();
            }
    }   

    return Response.status(Response.Status.BAD_REQUEST).build();


    }   

    return Response.status(Response.Status.BAD_REQUEST).build();
    
}
 
    
   



}

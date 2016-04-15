/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.vital.vitalcep.collector;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import eu.vital.vitalcep.connectors.mqtt.MqttAllInOne;
import eu.vital.vitalcep.connectors.mqtt.TMessageProc;
import eu.vital.vitalcep.collector.decoder.Decoder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import eu.vital.vitalcep.collector.listener.DMSListener;
import eu.vital.vitalcep.collector.listener.PPIListener;
import eu.vital.vitalcep.conf.ConfigReader;
import eu.vital.vitalcep.security.Security;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.logging.Level;
import org.apache.commons.lang.RandomStringUtils;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.JSONException;
/**
 *
 * @author a601149
 */
public class Collector {
    
      /** The Constant logger. */
    final static Logger logger = Logger.getLogger(Collector.class);
    
    protected boolean      isStopped    = false;
    private String mongoURL;
    private String mongoDB;
    public final JSONArray sensors = new JSONArray(); 
    private static Collector instance = null;
    
    public static Collector getInstance() throws IOException {
      if(instance == null) {
         instance = new Collector();
      }
      return instance;
   }
    
    private Collector()  throws IOException {

        ConfigReader configReader = ConfigReader.getInstance();
        
        mongoURL = configReader.get(ConfigReader.MONGO_URL);
        mongoDB = configReader.get(ConfigReader.MONGO_DB);
        
        getCollectorList();

        ScheduledExecutorService exec = Executors.newScheduledThreadPool(2);
        
        Runnable collectoRunnable;
        collectoRunnable = new Runnable() {
            @Override
            public void run() {
                Date NOW = new Date();
                String nowString = getXSDDateTime(NOW);
                for (int i = 0; i < sensors.length(); i++) {
                    try {
                        String cookie = getListenerCredentials(i);
                        
                        JSONArray aData = new JSONArray();
                        String type = sensors.getJSONObject(i)
                                .getString("cepType");
                        if (type.equals("CONTINUOUS")){
                            try {
                                DMSListener oDMS = new DMSListener(cookie);
                                
                                aData = oDMS.getObservations(sensors
                                        .getJSONObject(i).getJSONArray("sources")
                                    ,sensors
                                        .getJSONObject(i).getJSONArray("properties")
                                    ,sensors
                                        .getJSONObject(i).getString("lastRequest"));
                                              
                            } catch (IOException | KeyManagementException 
                                    | NoSuchAlgorithmException 
                                    | KeyStoreException ex) {
                                java.util.logging.Logger.getLogger(Collector
                                    .class.getName())
                                        .log(Level.SEVERE, null, ex);
                            }
                            
                            if (aData.length()>0){
                                    sendData2CEP(aData, i);
                            }
                            
                        }else{
                            try {
                                
                                JSONObject sensor =new JSONObject();
                                sensor = sensors.getJSONObject(i);
                                JSONArray requests = new JSONArray();
                                requests = sensor.getJSONArray("requests");
                                PPIListener oPPI = new PPIListener(cookie);

                                aData = oPPI.getObservations(requests
                                    ,sensor
                                    .getString("lastRequest"));

                                if (aData.length()>0){
                                    sendData2CEP(aData, i);
                                }

                            } catch (IOException | KeyManagementException 
                                    | NoSuchAlgorithmException 
                                    | KeyStoreException ex) {
                                java.util.logging.Logger.getLogger(Collector
                                    .class.getName())
                                        .log(Level.SEVERE, null, ex);
                                }
                            
                        }
                        
                        sensors.getJSONObject(i).put("lastRequest"
                                                ,nowString);
                        MongoClient mongo = new MongoClient(new MongoClientURI(mongoURL));
        
                        MongoDatabase db = mongo.getDatabase(mongoDB);
                        
                        Bson filter = Filters.eq("_id"
                                , new ObjectId(sensors.getJSONObject(i)
                                      .getString("id")));
                        
                        Bson update =  new Document("$set"
                                ,new Document("lastRequest",nowString));
                        
                        UpdateOptions options = new UpdateOptions().upsert(false);

                        UpdateResult updateDoc =  db.getCollection("cepinstances")
                                .updateOne(filter,update,options);
                      
                    } catch (GeneralSecurityException | IOException 
                            | ParseException ex) {
                        java.util.logging.Logger
                        .getLogger(Collector.class.getName())
                                .log(Level.SEVERE, null, ex);
                    }
                    
                }
            }

            private void sendData2CEP(JSONArray aData, int i) throws JSONException, ParseException {
                Decoder decoder = new Decoder();
                ArrayList<String> simpleEventAL = decoder
                        .JsonldArray2DolceInput(aData);
                MqttAllInOne oMqtt = new MqttAllInOne();
                TMessageProc MsgProcc = new TMessageProc();
                
                //TODO: check the client name. see from cep instances and what happen when if the topic exist 
                String clientName = "collector_"+RandomStringUtils.randomAlphanumeric(4);
                
                oMqtt.sendMsg(MsgProcc, clientName
                        , simpleEventAL
                        ,sensors.getJSONObject(i).getString("mqin")
                        ,sensors.getJSONObject(i).getString("mqout"));
            }

            private String getListenerCredentials(int i) throws IOException
                    , GeneralSecurityException, JSONException {
                StringBuilder ck = new StringBuilder();
                Security slogin = new Security();
                JSONObject credentials = new JSONObject();
//                Boolean token = slogin.login(sensors.getJSONArray(i)
//                        .getJSONObject(0)
//                        .getString("username")
//                        ,decrypt(sensors.getJSONArray(i)
//                                .getJSONObject(0)
//                                .getString("password")),false,ck);
                Boolean token = slogin.login("elisa"
                        ,"elisotas1",false,ck);
                if (!token){
                    //throw new
                    
                }
                String cookie = ck.toString();
                return cookie;
            }
        };


        exec.scheduleAtFixedRate(collectoRunnable , 0, 1, TimeUnit.MINUTES);

    }

    private void getCollectorList() {
        try{
        MongoClient mongo = new MongoClient(new MongoClientURI(mongoURL));
        
        final MongoDatabase db = mongo.getDatabase(mongoDB);
        
        BasicDBObject clause1 = new BasicDBObject("cepType", "CONTINUOUS");
        BasicDBObject clause2 = new BasicDBObject("cepType", "CEPICO");
        BasicDBList or = new BasicDBList();
        or.add(clause1);
        or.add(clause2);
        BasicDBObject query = new BasicDBObject("$or", or);
        
        
        FindIterable<Document> coll;
        coll = db.getCollection("cepinstances").find(query);
        
        coll.forEach(new Block<Document>() {
            @Override
            public void apply(final Document document) {
                
                JSONObject oCollector = new JSONObject();
                
                oCollector.put("id",document.getObjectId("_id").toString());
                oCollector.put("mqin", document.getString("mqin"));
                oCollector.put("mqout", document.getString("mqout"));
                oCollector.put("cepType", document.getString("cepType"));
              

                if (document.getString("cepType").equals("CONTINUOUS")){
                    
                    final Document doc = new Document("sources", document
                            .get("sources"));
                    final String jsonStringSources = doc.toJson();
                    JSONObject sources = new JSONObject(jsonStringSources);
                    
                    final Document docproperties = new Document("properties", 
                             document.get("properties"));
                    final String jsonStringproperties = docproperties.toJson();
                    JSONObject sourcesproperties 
                            = new JSONObject(jsonStringproperties);
                    
                    oCollector.put("sources",sources.getJSONArray("sources"));
                    oCollector.put("properties",sourcesproperties
                            .getJSONArray("properties"));
                    oCollector.put("username",document.getString("username"));
                    oCollector.put("password",document.getString("password"));
                    
                }else{

                    final Document doc = new Document( "requests",document.get("requests"));
                    final String jsonStringRequests = doc.toJson();
                    JSONObject requestsObject = new JSONObject(jsonStringRequests);
                    
                    
                    oCollector.put("requests", requestsObject.getJSONArray("requests") );
                    oCollector.put("username",document.getString("username"));
                    oCollector.put("password",document.getString("password"));
                }
                oCollector.put("lastRequest",document.getString("lastRequest"));
                sensors.put(oCollector);
                
            }
        });
        
        }catch (Exception e){
        String a= "a";
        }
    }
        
    public boolean start(String sensorId,String name, String mqin, 
            String mqout,int qos){
        //add new values to the list
        return true;
    }
    
    public boolean stop(){
        //delete the values to the list
        return true;
    }
    
    public boolean restartAll() throws IOException {
      // leer de la base de datos
        return true;
    }
    
    private String getXSDDateTime(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        return  dateFormat.format(date);
    }
     
    private static final char[] PASSWORD = "vital-Iot".toCharArray();
    private static final byte[] SALT = {
        (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
        (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
    };
    
    private static String decrypt(String property) throws 
            GeneralSecurityException, IOException {
        SecretKeyFactory keyFactory = SecretKeyFactory
                .getInstance("PBEWithMD5AndDES");
        SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
        Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
        pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
        return  new String(Base64.getDecoder().decode(property)
                ,StandardCharsets.UTF_8);
    }
}

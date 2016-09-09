/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.vital.vitalcep.publisher;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.client.MongoDatabase;

import eu.vital.vitalcep.connectors.dms.DMSManager;
import eu.vital.vitalcep.connectors.mqtt.MessageProcessor;
import eu.vital.vitalcep.connectors.mqtt.MqttMsg;
import eu.vital.vitalcep.publisher.encoder.Encoder;
import java.text.ParseException;


/**
 *
 * @author a601149
 */
public class MessageProcessor_publisher  implements MessageProcessor {
    
    private final String cookie;
    private final String dms_URL;
    private final String sensorId;
    private final String mongocollection;
    private final String mongoURL;
    private final String mongoDB;
    private Logger logger;
    
    public MessageProcessor_publisher(String dms_url, String cookie,
             String sensorId,String mongocollection,String mongo_url,
             String mongo_db){
    	
    	this.logger = Logger.getLogger(this.getClass().getName());
        this.dms_URL=dms_url;
        this.cookie=cookie;
        this.sensorId=sensorId;
        this.mongocollection = mongocollection;
        mongoURL = mongo_url;
        mongoDB = mongo_db;
    }
    
        
    @Override
     public boolean processMsg(MqttMsg mqttMsg) {

    	Encoder encoder = new Encoder();
        Date date = new Date();
        String xsdTime = getXSDDateTime(date);
        UUID uuid = UUID.randomUUID();
        String id = uuid.toString();
        
        JSONObject observation = null;
        try {
            observation = encoder.dolceOutput2Jsonld
                (mqttMsg.msg, id,this.sensorId,xsdTime);
        } catch (ParseException ex) {
            java.util.logging.Logger.getLogger(MessageProcessor_publisher.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
         
        Document doc=null;
        try {
            doc = encoder.dolceOutput2Document(mqttMsg.msg,id
                    , this.sensorId, xsdTime);
        } catch (ParseException ex) {
            java.util.logging.Logger.getLogger(MessageProcessor_publisher.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        logger.debug("MQTTMessage received: "+ mqttMsg.msg);
        
        MongoClient mongo=null;
        MongoDatabase db=null;
        try{
        	mongo = new MongoClient(new MongoClientURI (this.mongoURL));
            db = mongo.getDatabase(this.mongoDB);
            db.getCollection(mongocollection)
                    .insertOne(doc);
        }catch(MongoException ex
                ){
        	logger.error("observation not saved");
        }finally{
        	if (db != null)
        		db = null;
        	if (mongo != null)
        		mongo.close();
        }
        
        JSONArray body = new JSONArray();
        body.put(observation);
        
        DMSManager oDMS = new DMSManager(dms_URL,cookie);
        
        try {
            if (!oDMS.pushObservations(body.toString())){
                logger.error("couldn't push to DMS");
                 return false;
            } else{
                return true;
            }
        } catch (IOException | KeyManagementException 
                | NoSuchAlgorithmException | KeyStoreException ex) {
        	logger.error(ex);
        }
	
        return true;
         
           
        
    }
        
     
    private String getXSDDateTime(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return  dateFormat.format(date);
    }
   
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.vital.vitalcep.publisher;

import eu.vital.vitalcep.connectors.mqtt.MessageProcessor;
import eu.vital.vitalcep.connectors.mqtt.MqttMsg;
import eu.vital.vitalcep.publisher.encoder.Encoder;
import org.json.JSONArray;
import org.json.JSONObject;
import eu.vital.vitalcep.connectors.dms.DMSManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author a601149
 */
public class MessageProcessor_publisher  implements MessageProcessor {
    
    private String cookie;
    private String dms_URL;
   
    
    public MessageProcessor_publisher(String dms_url, String cookie){
        this.dms_URL=dms_url;
        this.cookie=cookie;
    }
    
        
    @Override
     public boolean processMsg(MqttMsg mqttMsg) {
           
        //encoder and emitter

 
	Encoder encoder = new Encoder();
        
        JSONObject observation = encoder.dolceOutput2Jsonld
        (mqttMsg.msg, "id", "sensorId","observationTime");
        
        JSONArray body = new JSONArray();
        body.put(observation);
        
        DMSManager oDMS = new DMSManager(dms_URL,cookie);
        
        try {
            if (!oDMS.pushObservations(body.toString())){
                //log
                 return false;
            } else{
                return true;
            }
        } catch (IOException ex) {
            Logger.getLogger(MessageProcessor_publisher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KeyManagementException ex) {
            Logger.getLogger(MessageProcessor_publisher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(MessageProcessor_publisher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KeyStoreException ex) {
            Logger.getLogger(MessageProcessor_publisher.class.getName()).log(Level.SEVERE, null, ex);
        }
	
        return true;
         
           
        
    }
        
   
}

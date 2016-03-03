/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.vital.vitalcep.connectors.mqtt;

import eu.vital.vitalcep.encoder.Encoder;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 *
 * @author a601149
 */
public class MessageProcessor_publisher  implements MessageProcessor {
   
    
    public MessageProcessor_publisher(){
    }
    
        
    @Override
     public boolean processMsg(MqttMsg mqttMsg) {
           
        //encoder and emitter

 
	Encoder encoder = new Encoder();
        
        JSONObject observation = encoder.dolceOutput2Jsonld
        (mqttMsg.msg, "id", "sensorId","observationTime");
        
        
         
//        File fout = new File("/lastCep/out.txt");
//	FileOutputStream fos;
//        try {
//            fos = new FileOutputStream(fout);
//            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
//            bw.write(observation.toString());
//            bw.newLine();
//            bw.close();
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(MessageProcessor_publisher.class.getName())
//                    .log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(MessageProcessor_publisher.class.getName())
//                    .log(Level.SEVERE, null, ex);
//        }
	
        
         
           
        return true;
    }
        
   
}

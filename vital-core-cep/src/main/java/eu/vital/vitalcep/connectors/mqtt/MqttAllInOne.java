/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.vital.vitalcep.connectors.mqtt;

import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 *
 * @author a601149
 */
public class MqttAllInOne {
	
	Logger logger = null;
	
	public MqttAllInOne (){
		logger = Logger.getLogger(this.getClass().getName().toString());
	}
    
     public boolean sendMsg (MessageProcessor processor, String name, ArrayList<String> simpleEvents,
             String mqin, String mqout ){
      MessageProcessor msgProc = null;//processor;
      MsgQueue queue = null;//new MsgQueue(msgProc);
      //                                  ( name, msgQueue, cepInputTopicName, cepOutputTopicName, qos)
      MqttConnector connector = new MqttConnector(name, queue, mqin, mqout, 2, true);
      
      if (connector!=null){
          
          try{
///          PrintWriter writer = new PrintWriter("/home/a601149/elastic_input.txt", "UTF-8");
       
          for (int i=0; i<simpleEvents.size();i++){
        	  logger.debug("Sending MQTTmessage: " + simpleEvents.get(i));
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

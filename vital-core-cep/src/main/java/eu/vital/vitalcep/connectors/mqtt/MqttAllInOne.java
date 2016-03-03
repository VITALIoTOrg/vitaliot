/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.vital.vitalcep.connectors.mqtt;

import java.util.ArrayList;

/**
 *
 * @author a601149
 */
public class MqttAllInOne {
    
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
     
     
     
    
     
//     public boolean sendMsg (MessageProcessor processor, String name, String simpleEvent){
//      MessageProcessor msgProc = processor;
//      MsgQueue queue = new MsgQueue(msgProc);
//      //                                  ( name, msgQueue, cepInputTopicName, cepOutputTopicName, qos)
//      MqttConnector connector = new MqttConnector("miCEP", queue, "mqin", "mqou", 2);
//      
//      if (connector!=null){
//        connector.publishMsg(simpleEvent);
//        connector.disconnect();
//       
//       return true;
//      }else
//       return false;
//      
//     }
     
    
}

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
public class TMessageProc implements MessageProcessor{
    
    ArrayList<MqttMsg> internalBuffer;
    
    public TMessageProc (){
        internalBuffer = new ArrayList<>();
    }
    
    
    @Override
     public boolean processMsg(MqttMsg mqttMsg){
           
            internalBuffer.add(mqttMsg);
           
            return true;
        }
        
    public ArrayList<MqttMsg> getMsgs(){
        return internalBuffer;
    } 
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.vital.vitalcep.publisher;

import eu.vital.vitalcep.connectors.mqtt.MQTT_conn_interface;
import eu.vital.vitalcep.connectors.mqtt.MqttConnector;
import eu.vital.vitalcep.connectors.mqtt.MsgQueue;
import org.apache.commons.lang.RandomStringUtils;

/**
 *
 * @author a601149
 */
public class MQTT_connector_subscriper implements MQTT_conn_interface{
    
    public MqttConnector connector;
    private String clientName;
    private String queueName;
    
    public MQTT_connector_subscriper(String queueName, 
            MessageProcessor_publisher MsgProcc){
        
        if (connector == null){
            this.queueName = queueName;
            clientName = "publisher_"+RandomStringUtils.randomAlphanumeric(4);
            MsgQueue oQueue = new MsgQueue(MsgProcc);
            connector = new MqttConnector(clientName, 
                     oQueue,"", queueName, 2,false);
       }
    }
    
    @Override
    public String getClientName(){
        return clientName;
    }
    @Override
    public String getQueuename(){
        return queueName;
    }
}

package eu.vital.vitalcep.connectors.mqtt;


import java.util.HashMap;

public class MqttConnectorContainer {

	static public HashMap<String, MQTT_conn_interface> MqttContainer;
	
	private MqttConnectorContainer() {
		
	}
	
	static public MQTT_conn_interface getConnector (String name){
            if (MqttContainer == null)
                MqttContainer = new HashMap<>();
            
            return MqttContainer.get(name);
	}
	
	static public boolean addConnector (String name, MQTT_conn_interface conector){
            if (MqttContainer == null)
                MqttContainer = new HashMap<>();
            
            if (MqttContainer.put(name, conector) != null)
                    return true; //FIX ME, this means that there was previous connector with the same name

            return true;
	}
	
	static public boolean deleteConnector (String name){
            if (MqttContainer == null)
                MqttContainer = new HashMap<>();
            
            if (MqttContainer.remove(name)!=null)
                    return true;

            return false;
	}
}

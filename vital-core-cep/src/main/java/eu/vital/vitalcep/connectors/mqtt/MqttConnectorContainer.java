package eu.vital.vitalcep.connectors.mqtt;


import java.util.HashMap;

public class MqttConnectorContainer {

	HashMap<String, MqttConnector> MqttContainer;
	
	public MqttConnectorContainer() {
		MqttContainer = new HashMap<String, MqttConnector>();
	}
	
	public MqttConnector getConnector (String name){
		return MqttContainer.get(name);
	}
	
	public boolean addConnector (String name, MqttConnector conector){
		if (MqttContainer.put(name, conector) != null)
			return true; //FIX ME, this means that there was previous connector with the same name
		
		return true;
	}
	
	public boolean deleteConnector (String name){
		if (MqttContainer.remove(name)!=null)
			return true;
		
		return false;
	}
}

package eu.vital.vitalcep.connectors.mqtt;

public interface MessageProcessor {

	public boolean processMsg(MqttMsg mqttMsg);
}

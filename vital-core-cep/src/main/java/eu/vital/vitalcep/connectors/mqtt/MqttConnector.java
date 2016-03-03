package eu.vital.vitalcep.connectors.mqtt;


import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

public class MqttConnector implements MqttCallback,MQTT_conn_interface{
	
    String CepInstanceID;
	
            MqttClient myClient;
    MqttConnectOptions connOpt;
    String publishTopic;
    String subscribedTopic;
    int qos;

    String BrokerUrl;
    Logger logger;
    //Configuration conf;
    MsgQueue msgQueue;
    String clientName;

    public MqttConnector (String name, 
            /*RESTIntfProperties conf, */MsgQueue msgQueue,
            String cepInputTopicName, String cepOutputTopicName,
            int qos /*fixed to 2*/){

            logger = Logger.getLogger(this.getClass().getName());
            this.logger = logger;
            this.msgQueue = msgQueue;
            this.publishTopic = cepInputTopicName;
            this.subscribedTopic = cepOutputTopicName;
            this.qos = qos;
            this.clientName = name;

            //BrokerUrl = "tcp://"+conf.getMqttBroker_ip()+":"+conf.getMqttBroker_port();
            BrokerUrl = "tcp://localhost:1883";
            subscribe();

    }


    public void subscribe (){
        //setup MQTT Client
        connOpt = new MqttConnectOptions();
        connOpt.setCleanSession(true);
        connOpt.setKeepAliveInterval(30);

        //connect to broker
        try{
                myClient = new MqttClient(BrokerUrl, clientName);
                myClient.setCallback(this);
                myClient.connect(connOpt);
        }catch (MqttException e){
                logger.error(e,e);
                return;
        }

       // logger.info("Connected to: "+BrokerUrl+", client name:"+clientName);

        //subscribe to topic
        try{
                myClient.subscribe(subscribedTopic,qos);
        }catch (Exception e){
                logger.error(e,e);
        }
    }


    public void publishMsg (String msg){
            MqttTopic mqttTopic = myClient.getTopic(publishTopic);

            MqttMessage message = new MqttMessage(msg.getBytes());
            message.setQos(qos);
            message.setRetained(false);

           // logger.debug("Publishing to topic: \"" + publishTopic 
            //+ "\" qos: " + qos + " MSG:"+ message.toString());

          //  String ss= publishTopic + " qos: " + qos + " MSG:"+ message.toString();
            MqttDeliveryToken token = null;
            try{
                    token = mqttTopic.publish(message);
                    token.waitForCompletion();
            }catch (Exception e){
                    logger.error(e,e);
            }
    }

    @Override
    public void messageArrived(String arg0, MqttMessage arg1) throws Exception {
//            logger.debug("-------------------------------------------------");
//            logger.debug("| Topic:" + arg0);// topic.getName());
//            logger.debug("| Message: " + new String(new String (arg1.getPayload())));
//            logger.debug("-------------------------------------------------");

            MqttMsg msg = new MqttMsg ();
            msg.msg = new String (arg1.getPayload());
            msg.clientName = clientName;
            synchronized (msgQueue){
                    msgQueue.insertMsg(msg);
            }
//            logger.debug("-------------------------------------------------");
//            logger.debug("| Topic:" + arg0);// topic.getName());
//            logger.debug("| Message: " + msg.msg);
//            logger.debug("-------------------------------------------------");

    }

    public void disconnect(){

            // disconnect
            try {
                    // wait to ensure subscribed messages are delivered
                    Thread.sleep(50);
                    myClient.disconnect();
            } catch (Exception e) {
                    logger.error (e,e);
            }
    }

    @Override
    public void connectionLost(Throwable arg0) {
            logger.warn("Connection lost");
            // TODO Auto-generated method stub

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken arg0) {
            // TODO Auto-generated method stub

    }
    
    @Override
    public String getClientName(){
        return clientName;
    }
    @Override
    public String getQueuename(){
        return subscribedTopic;
    }

}


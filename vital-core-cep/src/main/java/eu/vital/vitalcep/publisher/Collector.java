/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.vital.vitalcep.publisher;

import eu.vital.vitalcep.conf.PropertyLoader;
import eu.vital.vitalcep.connectors.mqtt.MqttAllInOne;
import eu.vital.vitalcep.connectors.mqtt.MqttConnector;
import eu.vital.vitalcep.connectors.mqtt.MqttMsg;
import eu.vital.vitalcep.connectors.mqtt.MsgQueue;
import eu.vital.vitalcep.connectors.mqtt.TMessageProc;
import eu.vital.vitalcep.decoder.Decoder;
import eu.vital.vitalcep.encoder.Encoder;
import eu.vital.vitalcep.restApp.filteringApi.StaticFiltering;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.json.JSONArray;

/**
 *
 * @author a601149
 */
public class Collector {
    
      /** The Constant logger. */
    final static Logger logger = Logger.getLogger(Collector.class);
   // @Context private javax.servlet.http.HttpServletRequest hsr;
    
    private PropertyLoader props;
    protected boolean      isStopped    = false;
    private String host;
    private String hostname;
    private String mongoIp;
    
    private int mongoPort;
    
    private String mongoDB;
    
    public Collector(String sensorId,String name, String mqin, String mqout,int qos)  throws IOException {

        props = new PropertyLoader();
        mongoPort = Integer.parseInt(props.getProperty("mongo.port"));
        mongoIp= props.getProperty("mongo.ip");
        mongoDB = props.getProperty("mongo.db");
        host = props.getProperty("cep.ip").concat(":8180");
        hostname = props.getProperty("cep.resourceshostname");
        
        List<String> cepSensorSubscribeTopicList = new ArrayList<>();
 
        
        String hostnameport =  this.hostname.concat(":8180");
        Date date = new Date();
        
//        //obtener todos los valores de la base de datos la primera vez que inicia y cargarlos en memoria
//        // add 4 different values to list
//		cepSensorSubscribeTopicList.add("1");
//		cepSensorSubscribeTopicList.add("2");
//		cepSensorSubscribeTopicList.add("3");
//		cepSensorSubscribeTopicList.add("4");
//        //lista de los procesos a recolectar
        
        TMessageProc MsgProcc = new TMessageProc();//555

        MsgQueue oQueue = new MsgQueue(MsgProcc);
        MqttConnector connector = new MqttConnector("wildfly", 
                 oQueue,mqin, mqout, 2);
        
        
////////////////////////////////////////////////////////////////////////////
                //RECEIVING FROM MOSQUITO
        ArrayList<MqttMsg> mesagges = MsgProcc.getMsgs();

      
        
       
        
    }

    private JSONArray getComplex(String subscribTopic, String hostnameport,String randomUUIDString) {
        //hacer algo
        MqttAllInOne oMqtt = new MqttAllInOne();
        TMessageProc MsgProcc = new TMessageProc();
        ArrayList<MqttMsg> mesagges = MsgProcc.getMsgs();
        
        //FORMATTING OBSERVATIONS OUTPUT
        JSONArray aOutput ;
        
        Encoder encoder = new Encoder();
        
        aOutput= encoder.dolceOutputList2JsonldArray
                                (mesagges, hostnameport, randomUUIDString );
        
        return aOutput;
    }
    
    public void run(){
                
        while(!isStopped()){
            try {
               String a = "a";
            } catch (Exception e) {
                
            }
           
        }
        
        System.out.println("Server Stopped.");
    }
    
    public boolean start(){
                
        //add new values to the list
        return true;
    }
    
    public boolean stop(){
                
        //delete the values to the list
        return true;
    }
    
    public boolean restartAll() throws IOException {
      // leer de la base de datos
        return true;
    }
    
    private boolean isStopped() {
        return this.isStopped;
    }
}

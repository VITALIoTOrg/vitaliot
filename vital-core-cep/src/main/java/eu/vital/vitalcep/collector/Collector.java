/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.vital.vitalcep.collector;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import eu.vital.vitalcep.conf.PropertyLoader;
import eu.vital.vitalcep.connectors.mqtt.MqttAllInOne;
import eu.vital.vitalcep.connectors.mqtt.MqttConnector;
import eu.vital.vitalcep.connectors.mqtt.MqttMsg;
import eu.vital.vitalcep.connectors.mqtt.MsgQueue;
import eu.vital.vitalcep.connectors.mqtt.TMessageProc;
import eu.vital.vitalcep.collector.decoder.Decoder;
import eu.vital.vitalcep.publisher.encoder.Encoder;
import eu.vital.vitalcep.restApp.filteringApi.StaticFiltering;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import static java.util.concurrent.TimeUnit.SECONDS;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

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
    private final ScheduledExecutorService scheduler;
    private final JSONArray sensors = new JSONArray(); 
    
    public Collector()  throws IOException {

        scheduler = Executors.newScheduledThreadPool(1);
        props = new PropertyLoader();
        mongoPort = Integer.parseInt(props.getProperty("mongo.port"));
        mongoIp= props.getProperty("mongo.ip");
        mongoDB = props.getProperty("mongo.db");
        host = props.getProperty("cep.ip").concat(":8180");
        hostname = props.getProperty("cep.resourceshostname");
        
        MongoClient mongo = new MongoClient(mongoIp, mongoPort);

        MongoDatabase db = mongo.getDatabase(mongoDB);
        
        BasicDBObject clause1 = new BasicDBObject("cepType", "CONTINUOUS");  
        BasicDBObject clause2 = new BasicDBObject("cepType", "CEPICO");    
        BasicDBList or = new BasicDBList();
        or.add(clause1);
        or.add(clause2);
        BasicDBObject query = new BasicDBObject("$or", or);
   
        
        FindIterable<Document> coll;
        coll = db.getCollection("cepinstances").find(query);
        
        coll.forEach(new Block<Document>() {
            @Override
            public void apply(final Document document) {
                JSONObject oCollector = new JSONObject();

                oCollector.append("mqin", document.getString("mqin"));
                oCollector.append("mqout", document.getString("mqout"));
                oCollector.append("requests", document.get("requests"));
                oCollector.append("lastRequest", document.getString("lastRequest"));

                sensors.put(oCollector);
            }
        });
        
      
        String hostnameport =  this.hostname.concat(":8180");
        Date date = new Date();

        ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
        
        Runnable drawRunnable;
        drawRunnable = new Runnable() {
            public void run() {
                
            //    draw();
            }
        };


        exec.scheduleAtFixedRate(drawRunnable , 0, 1, TimeUnit.MINUTES);

        
//        //obtener todos los valores de la base de datos la primera vez que inicia y cargarlos en memoria
//        // add 4 different values to list
//		cepSensorSubscribeTopicList.add("1");
//		cepSensorSubscribeTopicList.add("2");
//		cepSensorSubscribeTopicList.add("3");
//		cepSensorSubscribeTopicList.add("4");
//        //lista de los procesos a recolectar
        
        //leer from mongo y poner en la lista
//        
//        TMessageProc MsgProcc = new TMessageProc();//555
//
//        MsgQueue oQueue = new MsgQueue(MsgProcc);
//        MqttConnector connector = new MqttConnector("wildfly", 
//                 oQueue,mqin, mqout, 2);
//        
        
////////////////////////////////////////////////////////////////////////////
                //RECEIVING FROM MOSQUITO
//        ArrayList<MqttMsg> mesagges = MsgProcc.getMsgs();

      
        
       
        
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
    
    public boolean start(String sensorId,String name, String mqin, String mqout,int qos){
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

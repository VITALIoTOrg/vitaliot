/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.vital.vitalcep.listener;

import eu.vital.vitalcep.cep.CEP;
import eu.vital.vitalcep.cep.CepProcess;
import eu.vital.vitalcep.conf.PropertyLoader;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.bson.Document;

/**
 *
 * @author a601149
 */
public class Listener {
    
    private  PropertyLoader props;
    private  String mongoIp;
    private  int mongoPort;
    private  String mongoDB;
    public String path;
    public String fileName;
    public int PID;
    public String mqin;
    public String mqout;
    public String doceSpecification;
    public CEP.CEPType type;
    final private CepProcess cp;
    
    public Listener ()
            
        throws FileNotFoundException, IOException{
        
        Logger logger = Logger.getLogger(this.getClass().getName());
        
        props = new PropertyLoader();
        
        props.getProperty("cep.ip");

        mongoPort = Integer.parseInt(props.getProperty("mongo.port"));
        mongoIp = props.getProperty("mongo.ip");
        mongoDB = props.getProperty("mongo.db");
        
        CepProcess cp = new CepProcess(doceSpecification, mqin, mqout);
        this.cp = cp;
        cp.startCEP();
        this.PID = cp.PID;
        
        String T = type.toString();
        
        if (cp.PID>0){
            
            this.fileName = cp.fileName;         
            MongoClient mongo = new MongoClient(mongoIp, mongoPort);
            MongoDatabase db = mongo.getDatabase(mongoDB);
            
            try{
                
                db.getCollection("cepinstances").insertOne(
                    new Document("PID",PID).append("mqin",mqin )
                                .append("mqout", mqout)
            //    .append("dolceSpecification", dolceSpecification)
                .append("dolcefile", cp.cepFolder+"/"+cp.fileName)
                .append("cepType", T)
                .append("clientId", fileName));

            }catch(Exception ex){
                    String a= "";
            }
                
        }else{
            this.fileName = "";
        }
      
    }
    
    
}

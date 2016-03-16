/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.vital.vitalcep.cep;

import eu.vital.vitalcep.conf.PropertyLoader;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.log4j.Logger;

import org.bson.Document;
import eu.vital.vitalcep.cep.CepProcess;
import java.util.Arrays;
import org.json.JSONArray;
import static java.util.Arrays.asList;

/**
 *
 * @author a601149
 */
public class CEP {
    
    public enum CEPType {
        DATA, QUERY, CEPICO, CONTINUOUS
    }
    
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
    public CEPType type;
    final private CepProcess cp;
    
    public CEP (CEPType type,String dolceSpecification,String mqin, String mqout,
            String sources)
            throws FileNotFoundException, IOException{
        
        Logger logger = Logger.getLogger(this.getClass().getName());
        
        props = new PropertyLoader();
        
        props.getProperty("cep.ip");

        mongoPort = Integer.parseInt(props.getProperty("mongo.port"));
        mongoIp = props.getProperty("mongo.ip");
        mongoDB = props.getProperty("mongo.db");
        
        CepProcess cp = new CepProcess(dolceSpecification, mqin, mqout);
        this.cp = cp;
        cp.startCEP();
        this.PID = cp.PID;
        
        String T = type.toString();
        
        if (cp.PID>0){
            
            this.fileName = cp.fileName;         
            MongoClient mongo = new MongoClient(mongoIp, mongoPort);
            MongoDatabase db = mongo.getDatabase(mongoDB);
            
            try{
                
                
              
                Document doc = new Document();
                
                doc.put("PID",PID);
                doc.put("mqin",mqin );
                doc.put("mqout", mqout);
                doc.put("dolceSpecification", dolceSpecification);
                doc.put("dolcefile", cp.cepFolder+"/"+cp.fileName);
                doc.put("cepType", T);
                doc.put("clientId", fileName);
                
                switch (T) {
                    case "DATA":
                        doc.put("data", sources);
                        break;
                    case "QUERY":
                        doc.put("querys", sources);
                        break;
                    default:
                        JSONArray aSources = new JSONArray(sources);
                        String a[]= new String[10000];
                        for (int i = 0; i < sources.length(); i++) {
                            a[i] = aSources.getString(i);
                            
                        }   doc.put("sources",asList(a));
                        break;    
                }
                
                
                db.getCollection("cepinstances").insertOne(doc);

            }catch(Exception ex){
                    String a= "";
            }
                
        }else{
            this.fileName = "";
        }
      
    }
    
    public boolean cepDispose() throws IOException{
        
        this.cp.stopCEP();
        
        if(this.cp.PID==0){
             return true;
        }else{
            return false;
        }

    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.vital.vitalcep.cep;

import com.mongodb.BasicDBList;
import eu.vital.vitalcep.conf.PropertyLoader;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.log4j.Logger;

import org.bson.Document;
import eu.vital.vitalcep.collector.Collector;
import eu.vital.vitalcep.conf.ConfigReader;
import eu.vital.vitalcep.entities.dolceHandler.DolceSpecification;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.logging.Level;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.ws.rs.ServerErrorException;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;


/**
 *
 * @author a601149
 */
public class CEP {

    public enum CEPType {
        DATA, QUERY, CEPICO, CONTINUOUS
    }
    
    private  String mongoURL;
    private  String mongoDB;
    public String path;
    public String fileName;
    public int PID;
    public String mqin;
    public String mqout;
    public String doceSpecification;
    public CEPType type;
    final private CepProcess cp;
    
    public CEP (CEPType type,DolceSpecification dolceSpecification
            ,String mqin, String mqout,
            String sources, JSONObject credentials)
            throws FileNotFoundException, IOException{

        ConfigReader configReader = ConfigReader.getInstance();
        
        mongoURL = configReader.get(ConfigReader.MONGO_URL);
        mongoDB = configReader.get(ConfigReader.MONGO_DB);
        
        cp = new CepProcess(dolceSpecification.toString(), mqin, mqout);
        cp.startCEP();
        this.PID = cp.PID;
               
        String T = type.toString();
        
        if (cp.PID>0){
            
            this.fileName = cp.fileName;         
            MongoClient mongo = new MongoClient(new MongoClientURI (mongoURL));
            MongoDatabase db = mongo.getDatabase(mongoDB);
            
            try{
                
                Document doc = new Document();
                
                doc.put("PID",PID);
                doc.put("mqin",mqin );
                doc.put("mqout", mqout);
                doc.put("dolceSpecification", dolceSpecification.toString());
                doc.put("dolcefile", cp.cepFolder+"/"+cp.fileName);
                doc.put("cepType", T);
                doc.put("clientId", fileName);
                
                Date NOW = new Date();

                switch (T) {
                    case "DATA":
                        doc.put("data", sources);
                        break;
                    case "QUERY":
                        doc.put("querys", sources);
                        break;
                        
                    case "CEPICO":
                        doc.put("lastRequest", getXSDDateTime(NOW));
                        BasicDBList sourcesCEPICO = (BasicDBList) JSON.parse(sources);
                        doc.put("requests", sourcesCEPICO);
                        doc.put("lastRequest", getXSDDateTime(NOW));
                        doc.put("username", credentials.getString("username"));
                        doc.put("password",encrypt(credentials.getString("password")));
                        break;
                        
                    case "CONTINUOUS":  

                        BasicDBList sourcesB = (BasicDBList) JSON.parse(sources);
                        BasicDBList propertiesB = (BasicDBList) JSON
                               .parse(dolceSpecification.getEvents().toString());
                        doc.put("sources", sourcesB);
                        doc.put("properties", propertiesB);
                        doc.put("lastRequest", getXSDDateTime(NOW));
                        doc.put("username", credentials.getString("username"));
                        doc.put("password",encrypt(credentials.getString("password")));
                        break;
                }
                
                doc.put("status","OK");
                db.getCollection("cepinstances").insertOne(doc);
                ObjectId id = (ObjectId)doc.get( "_id" );
                
                Boolean insertIntoCollectorList = insertIntoCollectorList(doc,id);
                
                if (!insertIntoCollectorList){
                    db.getCollection("cepinstances").updateOne(doc
                            , new Document("$set", new Document("status"
                                    , "no collector available")));
                     throw new ServerErrorException(500);
                }
               
            }catch(JSONException | 
                    GeneralSecurityException | 
                    UnsupportedEncodingException | ServerErrorException ex){
                    String a= "";
            }
                
        }else{
            this.fileName = "";
        }
      
    }

    private Boolean insertIntoCollectorList( Document doc,ObjectId id)  {
        
        try {JSONObject oCollector = new JSONObject();
        
            oCollector.put("id",id.toString());
            oCollector.put("mqin", doc.getString("mqin"));
            oCollector.put("mqout", doc.getString("mqout"));
            oCollector.put("cepType", doc.getString("cepType"));

            if (doc.getString("cepType").equals("CONTINUOUS")){

                final Document doc1 = new Document("sources", doc
                        .get("sources"));
                final String jsonStringSources = doc1.toJson();
                JSONObject sources = new JSONObject(jsonStringSources);

                final Document docproperties = new Document("properties",
                        doc.get("properties"));
                final String jsonStringproperties = docproperties.toJson();
                JSONObject sourcesproperties
                        = new JSONObject(jsonStringproperties);

                oCollector.put("sources",sources.getJSONArray("sources"));
                oCollector.put("properties",sourcesproperties
                        .getJSONArray("properties"));
                oCollector.put("username",doc.getString("username"));
                oCollector.put("password",doc.getString("password"));

            }else{

                final Document doc1 = new Document( "requests"
                        ,doc.get("requests"));
                final String jsonStringRequests = doc1.toJson();
                JSONObject requestsObject = new JSONObject(jsonStringRequests);


                oCollector.put("requests"
                        , requestsObject.getJSONArray("requests") );
                oCollector.put("username",doc.getString("username"));
                oCollector.put("password",doc.getString("password"));
            }
            oCollector.put("lastRequest",doc.getString("lastRequest"));
            
            Collector oCol = Collector.getInstance( );    
            oCol.sensors.put(oCollector);
            
            
            
            
            return true;
        }catch( JSONException | IOException ee ){
            java.util.logging.Logger.getLogger(CEP.class.getName())
                    .log(Level.SEVERE, null, ee);
        }
        return false;
    }
    
    public boolean cepDispose() throws IOException{
        
        this.cp.stopCEP();
        return this.cp.PID==0;

    }
    
     private String getXSDDateTime(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        return  dateFormat.format(date);
    }
    private static final char[] PASSWORD = "vital-Iot".toCharArray();
    private static final byte[] SALT = {
        (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
        (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
    };
    private static String encrypt(String property) throws GeneralSecurityException, UnsupportedEncodingException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
        Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
        pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
        return Base64.getEncoder().encodeToString(property.getBytes(StandardCharsets.UTF_8));
    }
    
}

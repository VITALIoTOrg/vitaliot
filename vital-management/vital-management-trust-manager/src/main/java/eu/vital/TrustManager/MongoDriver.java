package eu.vital.TrustManager;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.bson.Document;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import eu.vital.TrustManager.conf.ConfigReader;


/**
 * 
 * @author adminuser
 * implementation of the mongo driver. it is a single tone class
 */
public class MongoDriver {

	static MongoClient mongoClient; 
	static String mongoDB;
	MongoDatabase db;

	String mongoURL;
    String MongoHost;
    String MongoPort;
    String MongoDBName;
    
    private static MongoDriver instance = null;
    
    
    /**
     * private class constructor
     */
    private MongoDriver(){ //String host, String port){
    	ConfigReader configReader = ConfigReader.getInstance();
    	mongoURL = configReader.get(ConfigReader.MONGO_URL);
    	mongoDB = "trustManager";//configReader.get(ConfigReader.MONGO_DB);
//    	MongoHost = host;
//    	MongoPort = port;
    	//MongoDBName = name;
    	
    	mongoClient = new MongoClient(new MongoClientURI (mongoURL));
    	db = mongoClient.getDatabase(mongoDB);
    	
    }
    
//    public static  MongoDriver  getInstance(String host, String port) throws Exception{
//		if (instance == null){
//			instance = new MongoDriver();
//		}
//		return instance;
//	}
    
    /**
     * 
     * @return the instance of the class
     * @throws Exception
     */
    public static  MongoDriver  getInstance( ) throws Exception{
		if (instance == null){
			instance = new MongoDriver();
		}
		return instance;
	}

    /**
     * method to insert a metric into mongo db
     * @param message: metric in bson format
     */
    public void instertIoTSystemMetric (String message){
    	MongoCollection<Document> coll = db.getCollection("IoTSystemMetric");
    	Document doc = Document.parse(message);
    	String sdate = (String)doc.get("timeS");
    	doc.remove("timeS");
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    	sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    	Date dateConverted;
		try {
			dateConverted = sdf.parse(sdate);
			doc.put("timeS",dateConverted);
	    	coll.insertOne(doc);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    			
    }
    
    
    //MIGEUL. SIMULACION: METE EN MONGO LOS SCORE DE TRUST QUE GENERARA LA MAQUINA DE TRUST CUANDO ESTE.
    /**
     * method used to simulate the trust engine, it inserts into mongodb the simutaled trustScores.
     * @param message, score in bson format
     */
    public void instertIoTSystemScore (String message){
    	MongoCollection<Document> coll = db.getCollection("IoTSystemScore");
    	Document doc = Document.parse(message);
    	String sdate = (String)doc.get("timeS");
    	doc.remove("timeS");
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    	sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    	Date dateConverted;
		try {
			dateConverted = sdf.parse(sdate);
			doc.put("timeS",dateConverted);
	    	coll.insertOne(doc);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /**
     * method to retrieve metrics from mongodb by iotsystem name. 
     * THIS IS AN EXAMPLE FOR ELENA
     * @param dateInit
     * @param dateEnd
     * @return
     */
    public String getSystemMetrics(String dateInit, String dateEnd){
    	
    	MongoCollection<Document> coll = db.getCollection("IoTSystemMetric");
    	Document docQuery = new Document ("id","http://vital-integration.atosresearch.eu:8280/cep");
    	//doc.Query = buildFilter(docQuery, dateInit, dateEnd);
    	FindIterable<Document> it = coll.find(docQuery);
    	StringBuilder sb = new StringBuilder();
    	
    	sb.append("{");
    	sb.append("\"IoTSystemMetrics\":[");		
    	MongoCursor<Document> iterator =  it.iterator();
    	while (iterator.hasNext()){
    		sb.append(iterator.next().toJson(new JsonWriterSettings(JsonMode.STRICT, true)));
    		if (iterator.hasNext()){
        		sb.append(",");    			
    		}
    	}
		sb.append("]");    	
		sb.append("}");		
		return sb.toString();
	}

    
// public String getTrustScores(String dateInit, String dateEnd){
//    	
//    	MongoCollection<Document> coll = db.getCollection("TrustScores");
//    	
//    	//TODO
//    	Document docQuery = new Document ("?????????","");
//    	//doc.Query = buildFilter(docQuery, dateInit, dateEnd);
//    	FindIterable<Document> it = coll.find(docQuery);
//    	StringBuilder sb = new StringBuilder();
//    	
//    	sb.append("{");
//    	sb.append("\"TrustScores\":[");		
//    	MongoCursor<Document> iterator =  it.iterator();
//    	while (iterator.hasNext()){
//    		sb.append(iterator.next().toJson(new JsonWriterSettings(JsonMode.STRICT, true)));
//    		if (iterator.hasNext()){
//        		sb.append(",");    			
//    		}
//    	}
//		sb.append("]");    	
//		sb.append("}");		
//		return sb.toString();
//	}
 
 
 /**
  * method to retrieve trust scores from mongodb by iotsystem name
  * TODO: it should retrieve only scores from last month, for the time being it retrieves all scores from a system.
  * TODO: to be fixed at ending august.
  * @param entity_id
  * @return
  * @throws Exception
  */
public String getTrustScores(String entity_id) throws Exception {
	 
	 MongoCollection<Document> coll = db.getCollection("IoTTrusResult");
	 Document docQuery = new Document("IoTSystem_id", entity_id);
	 
	 Date date = new Date();
	 Date oldDate =new Date();
	 DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss'Z'");
	 int currentMonth = oldDate.getMonth();
	 int lastMonth=0;
	 if (currentMonth==1)
		 lastMonth=12;
	 else
		 lastMonth=currentMonth-1;
	 
	 oldDate.setMonth(lastMonth);
	 //docQuery = docQuery.append("timesS", new Document("$gte",dateFormat.format(oldDate)).append("$lte", dateFormat.format(date))); 
			 
			 //buildFilter(docQuery, device_id, initTime, endTime);
	 FindIterable<Document> it = coll.find(docQuery);
	 StringBuilder sb = new StringBuilder();
	 sb.append("{");
	 sb.append("\""+"IoTSystemScore"+"\":[");
	 MongoCursor<Document> iterator = it.iterator();
	 while (iterator.hasNext()) {
		 sb.append(iterator.next().toJson(new JsonWriterSettings(JsonMode.STRICT, true)));
		 if (iterator.hasNext()) {
			 sb.append(",");
		 }
	 }
	 sb.append("]");
	 sb.append("}");
	 return sb.toString();
 } 
 



}




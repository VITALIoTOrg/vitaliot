package net.atos.ari.vital.mongo;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;
import org.bson.Document;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import net.atos.ari.vital.conf.ConfigReader;
import net.atos.ari.vital.mongo.data.JSONParserException;
import net.atos.ari.vital.mongo.data.MetricMongoData;
import net.atos.ari.vital.mongo.data.SystemMongoData;
import net.atos.ari.vital.mongo.data.SystemMongoDataParser;
import net.atos.ari.vital.taasaggregator.ThingServiceTrust;

@Component("MongoDBDriver")
public class MongoDriver implements InitializingBean{
	private static Logger logger = Logger.getLogger(MongoDriver.class);

	static final String COLLECTION_NAME = "IoTSystemMetric";
	static final String RESULT_NAME = "IoTTrusResult";
	MongoClient mongo; 
    MongoDatabase mongoDB; 

//	private static final String NAME = "eu.atos.trust.mongodb.name";
//	private static final String HOST = "eu.atos.trust.mongodb.host";
//	private static final String PORT = "eu.atos.trust.mongodb.port";
//
//	@Value("TRUST{" + NAME + "}")
//	private String name;
//	@Value("TRUST{" + HOST + "}")
//	private String host;
//	@Value("TRUST{" + PORT + "}")
//	private String port;
	
    private String name;
	private String mongoURL;
    
    public MongoDriver(){
    	name = "trustManager";
    	ConfigReader cr = ConfigReader.getInstance();
    	mongoURL =cr.get(ConfigReader.MONGO_URL);
    	
    }
    
    
    
    public void instertIoTSystemMetric (String message){
    	MongoCollection<Document> coll = mongoDB.getCollection(COLLECTION_NAME);
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
    private BasicDBObject buildDateFilter(Date startTime, Date endTime){
    	BasicDBObject filter; 
    	if (startTime!=null) 
    		filter = new BasicDBObject("timeS",
    			   new BasicDBObject("$gte",startTime).append("$lt",endTime ));
    	else
    		filter = new BasicDBObject("timeS",
     			   new BasicDBObject("$lt",endTime ));
    	return filter;

    }
    public List<SystemMongoData> getSystemMetrics(String id, Date startTime, Date endTime) throws JSONParserException{
    	ArrayList<SystemMongoData> result = new ArrayList<SystemMongoData>();
    	MongoCollection<Document> coll = mongoDB.getCollection(COLLECTION_NAME);
    	BasicDBObject dateFilter = buildDateFilter(startTime, endTime);
    	
    	
    	BasicDBObject andQuery = new BasicDBObject();
    	List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
    	obj.add(new BasicDBObject("id", id));
    	obj.add(dateFilter);
    	andQuery.put("$and", obj);

    	FindIterable<Document> it = coll.find(andQuery);
    	
    	/*Document docQuery = new Document ("id",id);
    	FindIterable<Document> it = coll.find(docQuery);*/
    	
    	MongoCursor<Document> iterator =  it.iterator();
    	while (iterator.hasNext()){
    		Document doc = iterator.next();
    		SystemMongoDataParser parser = new SystemMongoDataParser();
    		String serializedData = doc.toJson(new JsonWriterSettings(JsonMode.STRICT, true));
    		SystemMongoData data = parser.parse(serializedData);
    		result.add(data);

    	}
		return result;
	}

    public List<String> getSystemsRunning(Date startTime, Date endTime){
    	MongoCollection<Document> coll = mongoDB.getCollection(COLLECTION_NAME);
    	BasicDBObject filter = buildDateFilter(startTime, endTime);
    	DistinctIterable<String> idsString = coll.distinct( "id" , filter, String.class);
    	List<String> result = new ArrayList<String>();
    	idsString.into(result);
    	return result;
	}
    
//    public void instertTrustResult (ThingServiceTrust trusData) throws JSONParserException{
//    	MongoCollection<Document> coll = mongoDB.getCollection(RESULT_NAME);
//    	ThingServiceTrustSerializer serialize = new ThingServiceTrustSerializer();
//    	String message = serialize.serialize(trusData);
//    	Document doc = Document.parse(message);
//    	coll.insertOne(doc);
//    }
    
    public void instertTrustResult (ThingServiceTrust trustData){
    	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    	String message;
    	StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\'IoTSystem_id\':\'" + trustData.getIoTSystem_id() +"\',");
		sb.append("\'timeS\':\'"+ dateFormat.format(trustData.getTimeS()) + "\',");
		sb.append("\'trustScore\':\'"+ trustData.getTrustScore() + "\'");
		sb.append("}");
		message = sb.toString();
    	
    	MongoCollection<Document> coll = mongoDB.getCollection(RESULT_NAME);
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

    public static void main(String args[])throws Exception{
		MongoDriver md = new MongoDriver();
		
		md.name = "trust";
		md.afterPropertiesSet();
    	Date now = new Date();
    	List<String> systemsIdsRunning= md.getSystemsRunning(null, now);
    	if (systemsIdsRunning.size()==0){
    		System.out.println("Bo data");
    		return;
    	}
    	String systemId = systemsIdsRunning.get(0);
		List<SystemMongoData> periodData = md.getSystemMetrics(systemId, null, now);
		System.out.println(systemId);
    	for (SystemMongoData singlePeriodData:periodData){
    		System.out.println("--");
    		MetricMongoData[] metrics = singlePeriodData.getMetric();
    		if (metrics!=null)
	    		for (MetricMongoData m:metrics){
	        		System.out.println(m.getMetric_name());
	        		System.out.println(m.getFulfilment());
	        		System.out.println(m.getDate());
	    		}
    		else
    			System.out.println("Is null");
    	}
/*
    	Date now2 = new Date();
    	res= md.getSystemsRunning(now, now2);
    	for (String r:res){
    		System.out.println(r);
    	}*/

    }



	public void afterPropertiesSet() throws Exception {
		logger.info("Creating mongodb client with url: "+ mongoURL);
    	mongo = new MongoClient(new MongoClientURI (mongoURL));
    	mongoDB = mongo.getDatabase(name);
	}

}



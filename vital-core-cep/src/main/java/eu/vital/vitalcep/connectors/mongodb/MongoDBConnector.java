package eu.vital.vitalcep.connectors.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

import eu.vital.vitalcep.conf.ConfigReader;

public class MongoDBConnector {
	
	static private MongoClient mongo = null;
	static private String mongoURL;
	static String mongoDB = null;
	
	private MongoDBConnector(){
		
	}
	
	static MongoClient getMongo(){
		if (mongo == null){
			ConfigReader configReader = ConfigReader.getInstance();
	        
			String mongoURL = configReader.get(ConfigReader.MONGO_URL);
			mongoDB = configReader.get(ConfigReader.MONGO_DB);
			
			mongo = new MongoClient(new MongoClientURI (mongoURL));
		}
		return mongo;
	}
	
	public static MongoDatabase getMongoDB(){
		if (mongo != null)
			getMongo();
		MongoDatabase db = mongo.getDatabase(mongoDB);
		return db;
		
	}
	
	

}

package util;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class DMSDatabase {

	private final static String DB_NAME = "vital-dms";
	private static MongoClient mongoClient;

	private static DB database, db;
	private static boolean dbActive;

	public static DB getDB() {
		if (dbActive) {
			return database;
		}
		try {
			String url = VitalConfiguration.getProperty("vital-dms.mongo", "mongodb://localhost:27017");
			mongoClient = new MongoClient(new MongoClientURI(url));
			database = mongoClient.getDB(DB_NAME);
			dbActive = true;
			return database;
		} catch (Exception e) {
			e.printStackTrace();
			dbActive = false;
			return null;
		} finally {

		}
	}

	public static DBCollection getCollection(String collection) {
		db = getDB();
		return db.getCollection(collection);
	}
}

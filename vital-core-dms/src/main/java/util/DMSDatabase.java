package util;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

public class DMSDatabase {
	static MongoClient mongoClient;
	final static String HOST = "localhost";
	// final static String HOST = "vmvital03.deri.ie";
	final static int PORT = 27017; // for localhost
	// final static int PORT = 8025; // for server
	final static String dbName = "dmsdata";
	static DB database, db;
	static boolean dbActive;

	public static DB getDB() {
		if (dbActive) {
			return database;
		}
		try {
			mongoClient = new MongoClient(HOST, PORT);
			database = mongoClient.getDB(dbName);
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

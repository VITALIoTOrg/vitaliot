package api;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.DMSDatabase;
import util.DMSUtils;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class VitalSystem {

	private final static Logger logger = LoggerFactory
			.getLogger(VitalSystem.class);

	final static String systemCollectionName = "system";
	final static String systemContext = "http://vital-iot.eu/contexts/system.jsonld";
	static DBCollection systemCollection;

	public static boolean insertSystem(String data) {
		try {

			logger.info("insertSystem function called.");
			systemCollection = DMSDatabase.getCollection(systemCollectionName);

			return DMSUtils.insertData(systemCollection, data);

		} catch (Exception e) {
			logger.error("Error in insertSystem function: " + e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	public static ArrayList<DBObject> querySystem(DBObject query) {
		try {
			logger.info("querySystem function called.");
			systemCollection = DMSDatabase.getCollection(systemCollectionName);

			return DMSUtils.queryData(systemCollection, query, systemContext);

		} catch (Exception e) {
			logger.error("Error in querySystem function: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

}

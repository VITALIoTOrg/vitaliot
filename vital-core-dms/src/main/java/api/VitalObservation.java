package api;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.DMSDatabase;
import util.DMSUtils;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class VitalObservation {

	private final static Logger logger = LoggerFactory
			.getLogger(VitalObservation.class);

	final static String observationCollectionName = "observation";
	final static String observationContext = "http://vital-iot.eu/contexts/measurement.jsonld";

	static DBCollection observationCollection;

	public static boolean insertObservation(String data) {
		try {

			logger.info("insertObservation function called.");

			observationCollection = DMSDatabase
					.getCollection(observationCollectionName);

			return DMSUtils.insertData(observationCollection, data);

		} catch (Exception e) {
			logger.error("Error in insertObservation function: " + e.getMessage());
			e.printStackTrace();
			return false;
		}

	}

	public static ArrayList<DBObject> queryObservation(DBObject query) {
		try {

			logger.info("queryObservation function called.");

			observationCollection = DMSDatabase
					.getCollection(observationCollectionName);

			return DMSUtils.queryData(observationCollection, query,
					observationContext);

		} catch (Exception e) {
			logger.error("Error in queryObservation function: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
}

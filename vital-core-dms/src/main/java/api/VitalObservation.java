package api;

import java.util.ArrayList;

import util.DMSDatabase;
import util.DMSUtils;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class VitalObservation {
	final static String observationCollectionName = "observation";
	final static String observationContext = "http://vital-iot.eu/contexts/measurement.jsonld";

	static DBCollection observationCollection;

	public static boolean insertObservation(String data) {
		try {
			
			observationCollection = DMSDatabase
					.getCollection(observationCollectionName);

			return DMSUtils.insertData(observationCollection, data);
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	public static ArrayList<DBObject> queryObservation(DBObject query) {
		try {

			observationCollection = DMSDatabase
					.getCollection(observationCollectionName);
			
			return DMSUtils.queryData(observationCollection, query,
					observationContext);
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}

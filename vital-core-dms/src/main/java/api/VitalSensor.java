package api;

import java.util.ArrayList;

import util.DMSDatabase;
import util.DMSUtils;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class VitalSensor {
	final static String sensorCollectionName = "sensor";
	final static String sensorContext = "http://vital-iot.eu/contexts/sensor.jsonld";

	static DBCollection sensorCollection;

	public static boolean insertSensor(String data) {
		try {

			sensorCollection = DMSDatabase.getCollection(sensorCollectionName);

			return DMSUtils.insertData(sensorCollection, data);

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	public static ArrayList<DBObject> querySensor(DBObject query) {
		try {

			sensorCollection = DMSDatabase.getCollection(sensorCollectionName);
			
			return DMSUtils.queryData(sensorCollection, query, sensorContext);

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}

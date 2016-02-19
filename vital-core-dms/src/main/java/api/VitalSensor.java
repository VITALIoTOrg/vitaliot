package api;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.DMSDatabase;
import util.DMSUtils;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class VitalSensor {
	private final static Logger logger = LoggerFactory
			.getLogger(VitalSensor.class);

	final static String sensorCollectionName = "sensor";
	final static String sensorContext = "http://vital-iot.eu/contexts/sensor.jsonld";

	static DBCollection sensorCollection;

	public static boolean insertSensor(String data) {
		try {

			logger.info("insertSensor function called.");

			sensorCollection = DMSDatabase.getCollection(sensorCollectionName);

			return DMSUtils.insertData(sensorCollection, data);

		} catch (Exception e) {
			logger.error("Error in insertService function: " + e.getMessage());
			e.printStackTrace();
			return false;
		}

	}

	public static ArrayList<DBObject> querySensor(DBObject query) {
		try {

			logger.info("querySensor function called.");

			sensorCollection = DMSDatabase.getCollection(sensorCollectionName);

			return DMSUtils.queryData(sensorCollection, query, sensorContext);

		} catch (Exception e) {
			logger.error("Error in insertService function: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
}

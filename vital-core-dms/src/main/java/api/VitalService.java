package api;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.DMSDatabase;
import util.DMSUtils;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class VitalService {
	private final static Logger logger = LoggerFactory
			.getLogger(VitalService.class);

	final static String serviceCollectionName = "service";
	final static String serviceContext = "http://vital-iot.eu/contexts/service.jsonld";

	static DBCollection serviceCollection;

	public static boolean insertService(String data) {
		try {

			logger.info("insertService function called.");

			serviceCollection = DMSDatabase
					.getCollection(serviceCollectionName);

			return DMSUtils.insertData(serviceCollection, data);

		} catch (Exception e) {
			logger.error("Error in insertService function: " + e.getMessage());
			e.printStackTrace();
			return false;
		}

	}

	public static ArrayList<DBObject> queryService(DBObject query) {
		try {

			logger.info("queryService function called.");

			serviceCollection = DMSDatabase
					.getCollection(serviceCollectionName);

			return DMSUtils.queryData(serviceCollection, query, serviceContext);

		} catch (Exception e) {
			logger.error("Error in queryService function: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
}

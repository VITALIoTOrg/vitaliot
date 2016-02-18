package api;

import java.util.ArrayList;

import util.DMSDatabase;
import util.DMSUtils;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class VitalService {

	final static String serviceCollectionName = "service";
	final static String serviceContext = "http://vital-iot.eu/contexts/service.jsonld";

	static DBCollection serviceCollection;

	public static boolean insertService(String data) {
		try {

			serviceCollection = DMSDatabase
					.getCollection(serviceCollectionName);

			return DMSUtils.insertData(serviceCollection, data);

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	public static ArrayList<DBObject> queryService(DBObject query) {
		try {

			serviceCollection = DMSDatabase
					.getCollection(serviceCollectionName);
			
			return DMSUtils.queryData(serviceCollection, query, serviceContext);

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}

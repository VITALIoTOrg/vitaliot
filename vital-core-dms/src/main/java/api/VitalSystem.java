package api;

import java.util.ArrayList;

import util.DMSDatabase;
import util.DMSUtils;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class VitalSystem {

	final static String systemCollectionName = "system";
	final static String systemContext = "http://vital-iot.eu/contexts/system.jsonld";
	static DBCollection systemCollection;

	public static boolean insertSystem(String data) {
		try {

			systemCollection = DMSDatabase.getCollection(systemCollectionName);

			return DMSUtils.insertData(systemCollection, data);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static ArrayList<DBObject> querySystem(DBObject query) {
		try {

			systemCollection = DMSDatabase.getCollection(systemCollectionName);

			return DMSUtils.queryData(systemCollection, query, systemContext);

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}

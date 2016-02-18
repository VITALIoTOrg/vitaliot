package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import spark.Response;

import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

public class DMSUtils {

	public static String expandJSON(DBObject d) {
		try {
			JsonLdOptions options = new JsonLdOptions();
			Object jsonObject = JsonUtils.fromString(d.toString());
			Object result = JsonLdProcessor.expand(jsonObject, options);
			return JsonUtils.toPrettyString(result);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String compactJSON(DBObject dbObject, String ctx) {
		try {
			final Map<String, Object> context = new HashMap();
			context.put("@context", ctx);
			JsonLdOptions options = new JsonLdOptions();
			options.format = "application/ld+json";
			options.setBase("http://vital-iot.eu/ontology/ns");
			options.setCompactArrays(true);
			Object jsonObject = JsonUtils.fromString(dbObject.toString());
			Object compactJSON = JsonLdProcessor.compact(jsonObject, context,
					options);
			return JsonUtils.toPrettyString(compactJSON);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	static public DBObject sendException(Response response, Exception e) {
		DBObject objRet = new BasicDBObject();

		response.status(500);
		objRet.put("status", "failed");
		objRet.put("message", e.getMessage());
		// e.printStackTrace();
		return objRet;
	}

	static public DBObject sendError(String message, int code) {
		DBObject objRet = new BasicDBObject();
		objRet.put("status", "failed");
		objRet.put("message", message);
		objRet.put("code", code);
		return objRet;
	}

	public static DBObject encodeKeys(DBObject dbObject) {
		DBObject newMongoObject = new BasicDBObject();
		for (String key : dbObject.keySet()) {
			String newKey = key.replaceAll("\\.", "\\\\u002e");
			Object value = dbObject.get(key);
			if (value instanceof BasicDBObject) {
				newMongoObject.put(newKey, encodeKeys((BasicDBObject) value));
			} else if (value instanceof ArrayList) {
				ArrayList newList = new ArrayList();
				for (Object item : (ArrayList) value) {
					if (item instanceof BasicDBObject) {
						newList.add(encodeKeys((BasicDBObject) item));
					} else {
						newList.add(item);
					}
				}
				newMongoObject.put(newKey, newList);
			} else {
				newMongoObject.put(newKey, value);
			}
		}
		return newMongoObject;
	}

	public static DBObject decodeKeys(DBObject dbObject) {
		DBObject newMongoDocument = new BasicDBObject();
		for (String key : dbObject.keySet()) {
			String newKey = key.replaceAll("\\\\u002e", "\\.");
			Object value = dbObject.get(key);
			if (value instanceof BasicDBObject) {
				newMongoDocument.put(newKey, decodeKeys((BasicDBObject) value));
			} else if (value instanceof ArrayList) {
				ArrayList newList = new ArrayList();
				for (Object item : (ArrayList) value) {
					if (item instanceof BasicDBObject) {
						newList.add(decodeKeys((BasicDBObject) item));
					} else {
						newList.add(item);
					}
				}
				newMongoDocument.put(newKey, newList);
			} else {
				newMongoDocument.put(newKey, value);
			}
		}
		return newMongoDocument;
	}

	public static boolean insertData(DBCollection collection, String data) {
		try {
			DBObject basicDbObject, internalDBObject;
			BasicDBList basicDBList, internalDBList;

			Object o = JSON.parse(data.trim());

			if (o instanceof ArrayList) {
				basicDBList = (BasicDBList) o;
				for (int i = 0; i < basicDBList.size(); i++) {
					basicDbObject = (BasicDBObject) basicDBList.get(i);
					if (basicDbObject.containsField("id")) {
						basicDbObject.put("_id", basicDbObject.get("id"));
					}
					internalDBList = (BasicDBList) JSON.parse(DMSUtils
							.expandJSON(basicDbObject));
					for (int j = 0; j < internalDBList.size(); j++) {
						internalDBObject = (BasicDBObject) internalDBList
								.get(j);
						if (internalDBObject.containsField("@id")) {
							internalDBObject.put("_id",
									internalDBObject.get("@id"));
						}
						collection.save(encodeKeys(internalDBObject));
					}
				}
				return true;
			} else if (o instanceof DBObject) {
				basicDbObject = (DBObject) o;
				String s = DMSUtils.expandJSON(basicDbObject);
				basicDBList = (BasicDBList) JSON.parse(s);
				for (int i = 0; i < basicDBList.size(); i++) {
					basicDbObject = (BasicDBObject) basicDBList.get(i);
					if (basicDbObject.containsField("@id")) {
						basicDbObject.put("_id", basicDbObject.get("@id"));
					}
					collection.save(DMSUtils.encodeKeys(basicDbObject));
				}
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

	public static ArrayList<DBObject> queryData(DBCollection collection,
			DBObject query, String context) {
		try {
			DBCursor cursor = null;
			ArrayList<DBObject> listDBObjects = new ArrayList<DBObject>();
			DBObject dbObject = new BasicDBObject();
			cursor = collection.find(query);
			while (cursor.hasNext()) {
				dbObject = cursor.next();
				if (dbObject
						.containsField("http://vital-iot\\u002eeu/ontology/ns/_id")) {
					dbObject.removeField("http://vital-iot\\u002eeu/ontology/ns/_id");
					dbObject.removeField("_id");
				}
				dbObject = DMSUtils.decodeKeys(dbObject);
				dbObject = (DBObject) JSON.parse(DMSUtils.compactJSON(dbObject,
						context));
				dbObject.put("@context", context);
				listDBObjects.add(dbObject);
			}
			return listDBObjects;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}

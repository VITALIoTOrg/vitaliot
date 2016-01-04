package eu.vital.management.storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Logger;

import static com.mongodb.client.model.Filters.eq;

@ApplicationScoped
public class DocumentManager implements Serializable {

	private static final long serialVersionUID = -6384206813479073234L;

	public enum DOCUMENT_TYPE {
		SYSTEM,
		SERVICE,
		SENSOR,
		CONFIGURATION
	}

	@Inject
	private Logger log;

	@Inject
	private ObjectMapper objectMapper;

	private MongoClient mongoClient;

	private MongoDatabase mongoDatabase;

	private static final String MAIN_INDEX = "vital-management";

	@PostConstruct
	public void produceMongoClient() {
		log.info("produceMongoClient");

		String url = System.getProperty("vital.mongodb.host");
		if (url == null) {
			url = "localhost";
		}
		this.mongoClient = new MongoClient(url, 27017);
		this.mongoDatabase = mongoClient.getDatabase(MAIN_INDEX);
	}

	@PreDestroy
	public void disposeMongoClient() {
		log.info("disposeMongoClient");
		if (this.mongoClient != null) {
			this.mongoClient.close();
		}
	}

	private Document encodeKeys(Document mongoDocument) {
		Document newMongoDocument = new Document();
		for (String key : mongoDocument.keySet()) {
			String newKey = key.replaceAll("\\.", "\\\\u002e");
			Object value = mongoDocument.get(key);
			if (value instanceof Document) {
				newMongoDocument.put(newKey, encodeKeys((Document) value));
			} else if (value instanceof ArrayList) {
				ArrayList newList = new ArrayList();
				for (Object item : (ArrayList) value) {
					if (item instanceof Document) {
						newList.add(encodeKeys((Document) item));
					} else {
						newList.add(value);
					}
				}
				newMongoDocument.put(newKey, newList);
			} else {
				newMongoDocument.put(newKey, value);
			}
		}
		return newMongoDocument;
	}

	private Document decodeKeys(Document mongoDocument) {
		Document newMongoDocument = new Document();
		for (String key : mongoDocument.keySet()) {
			String newKey = key.replaceAll("\\\\u002e", "\\.");
			Object value = mongoDocument.get(key);
			if (value instanceof Document) {
				newMongoDocument.put(newKey, decodeKeys((Document) value));
			} else if (value instanceof ArrayList) {
				ArrayList newList = new ArrayList();
				for (Object item : (ArrayList) value) {
					if (item instanceof Document) {
						newList.add(decodeKeys((Document) item));
					} else {
						newList.add(value);
					}
				}
				newMongoDocument.put(newKey, newList);
			} else {
				newMongoDocument.put(newKey, value);
			}
		}
		return newMongoDocument;
	}

	/**
	 * ***************
	 * CRUD Functions:
	 * ****************
	 */

	public String create(String type, JsonNode document) {
		try {
			Document mongoDocument = Document.parse(objectMapper.writeValueAsString(document));
			MongoCollection mongoCollection = mongoDatabase.getCollection(type);
			mongoCollection.insertOne(encodeKeys(mongoDocument));

			return document.get("_id").asText();
		} catch (JsonProcessingException e) {
			// Should never happen, just log it and return null
			log.severe(e.getMessage());
			return null;
		}
	}


	public void update(String type, String documentId, JsonNode document) {
		try {
			Document mongoDocument = Document.parse(objectMapper.writeValueAsString(document));
			mongoDocument.put("_id", documentId);

			MongoCollection mongoCollection = mongoDatabase.getCollection(type);
			FindOneAndReplaceOptions options = new FindOneAndReplaceOptions();
			options.upsert(true);
			mongoCollection.findOneAndReplace(eq("_id", documentId), encodeKeys(mongoDocument), options);

		} catch (JsonProcessingException e) {
			// Should never happen, just log it and return null
			log.severe(e.getMessage());
		}
	}

	public ArrayNode getList(String type) {
		try {
			ArrayNode arrayNode = objectMapper.createArrayNode();
			MongoCollection mongoCollection = mongoDatabase.getCollection(type);
			MongoCursor<Document> cursor = mongoCollection.find().iterator();
			try {
				while (cursor.hasNext()) {
					Document mongoDocument = decodeKeys(cursor.next());
					ObjectNode objectNode = (ObjectNode) objectMapper.readTree(mongoDocument.toJson());
					arrayNode.add(objectNode);
				}
			} finally {
				cursor.close();
			}
			return arrayNode;
		} catch (IOException e) {
			// Should never happen, just log it and return null
			log.severe(e.getMessage());
			return null;
		}
	}

	public ArrayNode search(String type, Bson query) {
		try {
			ArrayNode arrayNode = objectMapper.createArrayNode();
			MongoCollection mongoCollection = mongoDatabase.getCollection(type);
			MongoCursor<Document> cursor = mongoCollection.find(query).iterator();
			try {
				while (cursor.hasNext()) {
					Document mongoDocument = decodeKeys(cursor.next());
					ObjectNode objectNode = (ObjectNode) objectMapper.readTree(mongoDocument.toJson());
					arrayNode.add(objectNode);
				}
			} finally {
				cursor.close();
			}
			return arrayNode;
		} catch (IOException e) {
			// Should never happen, just log it and return null
			log.severe(e.getMessage());
			return null;
		}
	}

	public ObjectNode get(String type, String documentId) {
		try {
			MongoCollection mongoCollection = mongoDatabase.getCollection(type);
			Document mongoDocument = (Document) mongoCollection.find(eq("_id", documentId)).first();

			return (ObjectNode) objectMapper.readTree(decodeKeys(mongoDocument).toJson());
		} catch (IOException e) {
			// Should never happen, just log it and return null
			log.severe(e.getMessage());
			return null;
		}
	}
}

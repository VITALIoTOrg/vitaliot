package eu.vital.orchestrator.storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.result.DeleteResult;
import eu.vital.orchestrator.util.VitalConfiguration;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Logger;

import static com.mongodb.client.model.Filters.in;

@ApplicationScoped
public class OrchestratorStorage implements Serializable {

	private static final long serialVersionUID = -6384206813479073234L;

	public enum DOCUMENT_TYPE {
		SYSTEM,
		SERVICE,
		SENSOR,
		CONFIGURATION,
		OPERATION,
		WORKFLOW,
		METASERVICE
	}

	@Inject
	private Logger log;

	@Inject
	private VitalConfiguration vitalConfiguration;

	@Inject
	private ObjectMapper objectMapper;

	private MongoClient mongoClient;

	private MongoDatabase mongoDatabase;

	private final String MAIN_INDEX = "vital-orchestrator";

	@PostConstruct
	public void produceMongoClient() {
		log.info("produceMongoClient");
		String url = vitalConfiguration.getProperty("vital-core-orchestrator.mongo", "mongodb://localhost:27017");
		this.mongoClient = new MongoClient(new MongoClientURI(url));
		this.mongoDatabase = mongoClient.getDatabase(MAIN_INDEX);
		log.info("produceMongoClient:done");
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

	/**
	 * ***************
	 * CRUD Functions:
	 * ****************
	 */

	public String create(String type, JsonNode document) {
		try {
			Document mongoDocument = encodeKeys(Document.parse(objectMapper.writeValueAsString(document)));
			MongoCollection mongoCollection = mongoDatabase.getCollection(type);
			mongoCollection.insertOne(mongoDocument);

			return mongoDocument.get("_id").toString();
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
			mongoCollection.findOneAndReplace(queryById(documentId), encodeKeys(mongoDocument), options);

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
					objectNode.put("id", mongoDocument.get("_id").toString());
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
					objectNode.put("id", mongoDocument.get("_id").toString());
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
			Document mongoDocument = (Document) mongoCollection.find(queryById(documentId)).first();

			ObjectNode objectNode = (ObjectNode) objectMapper.readTree(decodeKeys(mongoDocument).toJson());
			objectNode.put("id", mongoDocument.get("_id").toString());

			return objectNode;
		} catch (IOException e) {
			// Should never happen, just log it and return null
			log.severe(e.getMessage());
			return null;
		}
	}

	public long delete(String type, String documentId) {
		MongoCollection mongoCollection = mongoDatabase.getCollection(type);
		DeleteResult result = mongoCollection.deleteOne(queryById(documentId));
		return result.getDeletedCount();
	}

	public long delete(String type, Bson query) {
		MongoCollection mongoCollection = mongoDatabase.getCollection(type);
		DeleteResult result = mongoCollection.deleteMany(query);
		return result.getDeletedCount();
	}

	private Bson queryById(String documentId) {
		if (ObjectId.isValid(documentId)) {
			return in("_id", documentId, new ObjectId(documentId));
		} else {
			return in("_id", documentId);
		}
	}

}

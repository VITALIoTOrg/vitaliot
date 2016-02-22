package eu.vital.orchestrator.storage;

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
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Logger;

import static com.mongodb.client.model.Filters.eq;

@ApplicationScoped
public class DmsStorage implements Serializable {

	private static final long serialVersionUID = -6384206813479073234L;

	public enum DOCUMENT_TYPE {
		MEASUREMENT
	}

	@Inject
	private Logger log;

	@Inject
	private ObjectMapper objectMapper;

	private MongoClient mongoClient;

	private MongoDatabase mongoDatabase;

	private final String MAIN_INDEX = "dms";

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

	/**
	 * ***************
	 * CRUD Functions:
	 * ****************
	 */

	public ArrayNode getList(String type) {
		try {
			ArrayNode arrayNode = objectMapper.createArrayNode();
			MongoCollection mongoCollection = mongoDatabase.getCollection(type);
			MongoCursor<Document> cursor = mongoCollection.find().iterator();
			try {
				while (cursor.hasNext()) {
					Document mongoDocument = cursor.next();
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
					Document mongoDocument = cursor.next();
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

			ObjectNode objectNode = (ObjectNode) objectMapper.readTree(mongoDocument.toJson());
			return objectNode;
		} catch (IOException e) {
			// Should never happen, just log it and return null
			log.severe(e.getMessage());
			return null;
		}
	}

}

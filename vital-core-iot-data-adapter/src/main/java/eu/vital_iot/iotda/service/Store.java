package eu.vital_iot.iotda.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import eu.vital_iot.iotda.common.IoTSystem;
import eu.vital_iot.iotda.exception.StoreException;
import eu.vital_iot.iotda.util.Property;

/**
 * This class represents stores for IoT systems.
 * 
 * @author k4t3r1n4
 *
 * @see IoTSystem
 */
@Singleton
public class Store {

	/**
	 * The name of the collection for the IoT systems.
	 */
	private static final String IOT_SYSTEM_COLLECTION_NAME = "iot-systems";

	/**
	 * The logger.
	 */
	@Inject
	private Logger logger;

	/**
	 * The URI to connect to MongoDB.
	 */
	@Inject
	@Property(name = "vital-core-iot-data-adapter.mongo.uri")
	private String uri;

	/**
	 * The name of the MongoDB database.
	 */
	@Inject
	@Property(name = "vital-core-iot-data-adapter.mongo.db")
	private String db;

	/**
	 * The client.
	 */
	private MongoClient client;

	/**
	 * The database.
	 */
	private MongoDatabase database;

	/**
	 * The mapper that maps Java objects to JSON and vice versa.
	 */
	final ObjectMapper mapper = new ObjectMapper();

	/**
	 * Initialises this store.
	 */
	@PostConstruct
	public void init() {

		logger.log(Level.FINE, "Initialise.");

		// Construct the client and the database.
		client = new MongoClient(new MongoClientURI(uri));
		database = client.getDatabase(db);

		mapper.registerModule(new JodaModule());
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);

		logger.log(Level.FINE, "Initialised.");
	}

	/**
	 * Reads the IoT systems that satisfy the given criteria.
	 * 
	 * @param query
	 *            the query in the Query DSL.
	 * @return a list that contains all IoT systems that satisfy the given
	 *         criteria.
	 * 
	 * @throws StoreException
	 *             in case reading fails.
	 */
	public List<IoTSystem> read(String query) throws StoreException {

		logger.log(Level.FINE, "Read systems [ query: " + query + " ].");

		// Search for all IoT systems.
		final MongoCollection<Document> collection = database.getCollection(IOT_SYSTEM_COLLECTION_NAME);
		final FindIterable<Document> iterable = StringUtils.isBlank(query) ? collection.find() : collection.find(BsonDocument.parse(query));

		// JSON -> Java.
		final List<IoTSystem> iotsystems = new ArrayList<>();
		for (final Document document : iterable) {
			try {
				final String id = ((ObjectId) document.get("_id")).toString();
				document.remove("_id");
				final IoTSystem iotsystem = mapper.readValue(document.toJson(), IoTSystem.class);
				iotsystem.setId(id);
				iotsystems.add(iotsystem);
			} catch (IOException ioe) {
				logger.log(Level.SEVERE, "Failed to read systems.", ioe);
				throw new StoreException("Failed to read systems.", ioe);
			}
		}

		logger.log(Level.FINE, "Read " + iotsystems.size() + " systems [ query: " + query + " ].");

		return iotsystems;
	}

	/**
	 * Create the given IoT system.
	 * 
	 * @param iotsystem
	 *            the IoT system to create.
	 * @return the created IoT system (namely, the given IoT system with its ID
	 *         set).
	 * 
	 * @throws StoreException
	 *             in case creating fails.
	 */
	public IoTSystem create(IoTSystem iotsystem) {

		logger.log(Level.FINE, "Create system [ system: " + iotsystem + " ].");

		final MongoCollection<Document> collection = database.getCollection(IOT_SYSTEM_COLLECTION_NAME);

		// Java -> JSON.
		String json = null;
		try {
			json = mapper.writeValueAsString(iotsystem);
		} catch (IOException ioe) {
			logger.log(Level.SEVERE, "Failed to create system.", ioe);
			throw new StoreException("Failed to create system.", ioe);
		}

		// Index the IoT system.
		final Document document = Document.parse(json);
		collection.insertOne(document);
		final ObjectId id = (ObjectId) document.get("_id");
		iotsystem.setId(id.toString());

		logger.log(Level.FINE, "Created system [ system: " + iotsystem + " ].");

		return iotsystem;
	}

	/**
	 * Update the given IoT system.
	 * 
	 * @param iotsystem
	 *            the IoT system to update.
	 * @return the updated IoT system.
	 * @throws StoreException
	 *             in case updating fails.
	 */
	public IoTSystem update(IoTSystem iotsystem) throws StoreException {

		logger.log(Level.FINE, "Update system [ system: " + iotsystem + " ].");

		final MongoCollection<Document> collection = database.getCollection(IOT_SYSTEM_COLLECTION_NAME);

		// Java -> JSON.
		String json = null;
		try {
			json = mapper.writeValueAsString(iotsystem);
		} catch (IOException ioe) {
			logger.log(Level.SEVERE, "Failed to update system.", ioe);
			throw new StoreException("Failed to update system.", ioe);
		}

		// Update the IoT system.
		final Document document = Document.parse(json);
		document.remove("id");
		collection.replaceOne(BsonDocument.parse("{\"_id\": ObjectId(\"" + iotsystem.getId() + "\") }"), document);

		logger.log(Level.FINE, "Updated system [ system: " + iotsystem + " ].");

		return iotsystem;
	}

	/**
	 * Deletes the IoT system with the given ID.
	 * 
	 * @param id
	 *            the ID of an IoT system.
	 * @throws StoreException
	 *             in case deleting fails.
	 */
	public void delete(String id) throws StoreException {

		logger.log(Level.FINE, "Delete system [ id: " + id + " ].");

		final MongoCollection<Document> collection = database.getCollection(IOT_SYSTEM_COLLECTION_NAME);

		// Delete the IoT system with that ID.
		collection.deleteOne(BsonDocument.parse("{\"_id\": ObjectId(\"" + id + "\") }"));

		logger.log(Level.FINE, "Deleted system [ id: " + id + " ].");
	}

	/**
	 * Destroys this store.
	 */
	@PreDestroy
	public void destroy() {

		logger.log(Level.FINE, "Destroy.");

		// Close the client (if necessary).
		if (client != null) {
			client.close();
		}

		logger.log(Level.FINE, "Destroyed.");
	}
}
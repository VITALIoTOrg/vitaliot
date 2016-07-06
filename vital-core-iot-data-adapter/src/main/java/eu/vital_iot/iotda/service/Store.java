package eu.vital_iot.iotda.service;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.inject.Inject;

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
	 * The name of the collection for the actions.
	 */
	private static final String ACTION_COLLECTION_NAME = "actions";

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
	 * Reads all IoT systems.
	 * 
	 * @return a list that contains all IoT systems.
	 * 
	 * @throws StoreException
	 *             in case reading fails.
	 */
	public List<IoTSystem> read() throws StoreException {

		logger.log(Level.FINE, "Read all IoT systems.");

		// Search for all IoT systems.
		final MongoCollection<Document> collection = database.getCollection(IOT_SYSTEM_COLLECTION_NAME);
		final FindIterable<Document> iterable = collection.find();

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
				logger.log(Level.SEVERE, "Failed to read.", ioe);
				throw new StoreException("Failed to read.", ioe);
			}
		}

		logger.log(Level.FINE, "Read " + iotsystems.size() + " IoT systems.");

		return iotsystems;
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

		logger.log(Level.FINE, "Read IoT systems [ query: " + query + " ].");

		// Search for all IoT systems.
		final MongoCollection<Document> collection = database.getCollection(IOT_SYSTEM_COLLECTION_NAME);
		final FindIterable<Document> iterable = collection.find(BsonDocument.parse(query));

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
				logger.log(Level.SEVERE, "Failed to read.", ioe);
				throw new StoreException("Failed to read.", ioe);
			}
		}

		logger.log(Level.FINE, "Read " + iotsystems.size() + " IoT systems [ query: " + query + " ].");

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

		logger.log(Level.FINE, "Create IoT system [ iot-system: " + iotsystem + " ].");

		final MongoCollection<Document> collection = database.getCollection(IOT_SYSTEM_COLLECTION_NAME);

		// Java -> JSON.
		String json = null;
		try {
			json = mapper.writeValueAsString(iotsystem);
		} catch (IOException ioe) {
			logger.log(Level.SEVERE, "Failed to create.", ioe);
			throw new StoreException("Failed to create.", ioe);
		}

		// Index the IoT system.
		final Document document = Document.parse(json);
		collection.insertOne(document);
		final ObjectId id = (ObjectId) document.get("_id");
		iotsystem.setId(id.toString());

		logger.log(Level.FINE, "Created IoT system [ iot-system: " + iotsystem + " ].");

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

		logger.log(Level.FINE, "Update IoT system [ iot-system: " + iotsystem + " ].");

		final MongoCollection<Document> collection = database.getCollection(IOT_SYSTEM_COLLECTION_NAME);

		// Java -> JSON.
		String json = null;
		try {
			json = mapper.writeValueAsString(iotsystem);
		} catch (IOException ioe) {
			logger.log(Level.SEVERE, "Failed to update.", ioe);
			throw new StoreException("Failed to update.", ioe);
		}

		// Update the IoT system.
		final Document document = Document.parse(json);
		document.remove("id");
		collection.replaceOne(BsonDocument.parse("{\"_id\": ObjectId(\"" + iotsystem.getId() + "\") }"), document);

		logger.log(Level.FINE, "Updated IoT system [ iot-system: " + iotsystem + " ].");

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

		logger.log(Level.FINE, "Delete IoT system [ id: " + id + " ].");

		final MongoCollection<Document> collection = database.getCollection(IOT_SYSTEM_COLLECTION_NAME);

		// Delete the IoT system with that ID.
		collection.deleteOne(BsonDocument.parse("{\"_id\": ObjectId(\"" + id + "\") }"));

		logger.log(Level.FINE, "Deleted IoT system [ id: " + id + " ].");
	}

	/**
	 * Creates an action on the given sensor at the given time.
	 * 
	 * @param sensor
	 *            the sensor that the action was performed on.
	 * @param timestamp
	 *            the time when the action was performed.
	 */
	public void action(String sensor, Date timestamp) {

		final DateFormat FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		logger.log(Level.FINE, "Create action [ sensor: " + sensor + " timestamp: " + FORMAT.format(timestamp) + " ].");

		final MongoCollection<Document> collection = database.getCollection(ACTION_COLLECTION_NAME);

		final Document document = new Document().append("sensor", sensor).append("timestamp", timestamp.getTime());
		collection.insertOne(document);

		logger.log(Level.FINE,
				"Created action [ sensor: " + sensor + " timestamp: " + FORMAT.format(timestamp) + " ].");
	}

	/**
	 * Gets the time of the last action that was performed on the given sensor.
	 * 
	 * @param sensor
	 *            the sensor.
	 * @return the time when the last action was performed on the given sensor.
	 */
	public Date lastAction(String sensor) {

		logger.log(Level.FINE, "Get last action [ sensor: " + sensor + " ].");

		final MongoCollection<Document> collection = database.getCollection(ACTION_COLLECTION_NAME);

		final FindIterable<Document> iterable = collection
				.find(BsonDocument.parse("{\"term\": {\"sensor\": \"" + sensor + "\"}}"))
				.sort(BsonDocument.parse("{\"sensor\": 1}")).limit(1);
		for (final Document document : iterable) {
			final Date timestamp = new Date(document.getLong("timestamp"));
			final DateFormat FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
			logger.log(Level.FINE, "Last action: " + FORMAT.format(timestamp) + ".");
		}
		logger.log(Level.FINE, "No action.");
		return null;
	}

	/**
	 * Destroys this store.
	 */
	@PreDestroy
	public void destroy() {

		logger.log(Level.FINE, "Destroy.");

		// Close the client (if necessary).
		if (client != null)
			client.close();

		logger.log(Level.FINE, "Destroyed.");
	}
}
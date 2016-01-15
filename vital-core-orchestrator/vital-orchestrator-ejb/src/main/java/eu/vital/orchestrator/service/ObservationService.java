package eu.vital.orchestrator.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.vital.orchestrator.storage.DmsStorage;
import eu.vital.orchestrator.storage.OrchestratorStorage;
import eu.vital.orchestrator.util.OntologyParser;
import eu.vital.orchestrator.util.VitalClient;
import org.bson.conversions.Bson;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
@Stateless
public class ObservationService {

	@Inject
	private Logger log;

	@Inject
	private OrchestratorStorage orchestratorStorage;

	@Inject DmsStorage dmsStorage;

	@Inject
	private ObjectMapper objectMapper;

	@Inject
	private SystemDAO systemDAO;

	@Inject
	private SensorDAO sensorDAO;

	@Inject
	private ServiceDAO serviceDAO;

	@Inject
	private VitalClient vitalClient;

	public ArrayNode fetchLatestBySensorAndType(String sensorURI, String observationType) throws Exception {

		try {
			// Connect to ES and retrieve result
			JsonNode sensor = sensorDAO.get(sensorURI);

			String systemURI = sensor.get("system").asText();
			ObjectNode system = systemDAO.get(systemURI);
			ArrayNode systemServices = serviceDAO.searchBySystem(system);

			// Find ObservationService service, get URL:
			String operationURL = OntologyParser.findOperationURL(
					"http://vital-iot.eu/ontology/ns/ObservationService",
					"http://vital-iot.eu/ontology/ns/GetObservations",
					systemServices);

			// Connect to this URL and fetch all sensors:
			ObjectNode operationInput = objectMapper.createObjectNode();
			ArrayNode sensorsArray = objectMapper.createArrayNode();
			sensorsArray.add(sensorURI);
			operationInput.put("sensor", sensorsArray);
			operationInput.put("property", observationType);
			JsonNode result = vitalClient.doPost(operationURL, operationInput);

			if (result.isArray()) {
				return (ArrayNode) result;
			} else {
				return objectMapper.createArrayNode().add(result);
			}
		} catch (Exception e) {
			log.log(Level.WARNING, "", e);
			return objectMapper.createArrayNode();
		}
	}

	public ArrayNode fetchAll() throws Exception {

		try {
			ArrayNode result = dmsStorage.getList(DmsStorage.DOCUMENT_TYPE.MEASUREMENT.toString());
			// Expand JSON-LD documents:
			result = (ArrayNode) vitalClient.expand(result);
			// Return result
			return result;

		} catch (Exception e) {
			log.log(Level.WARNING, "", e);
			return objectMapper.createArrayNode();
		}
	}

	public ArrayNode fetchAllByType(String observationType) throws Exception {

		try {
			// Connect to DMS-ES and retrieve result
			// TODO: How to do this dynamically???
			observationType = observationType.replace("http://vital-iot.com/ontology#", "vital:");

			Bson query = eq("ssn:observationProperty.type", observationType);
			ArrayNode result = dmsStorage.search(DmsStorage.DOCUMENT_TYPE.MEASUREMENT.toString(), query);



			// Expand JSON-LD documents:
			result = (ArrayNode) vitalClient.expand(result);
			// Return result
			return result;

		} catch (Exception e) {
			log.log(Level.WARNING, "", e);
			return objectMapper.createArrayNode();
		}
	}

	public ArrayNode fetchAllBySensorAndType(String sensorURI, String observationType) throws Exception {

		try {
			// Connect to DMS-ES and retrieve result
			observationType = observationType.replace("http://vital-iot.com/ontology#", "vital:");

			Bson query = and(
					eq("ssn:observedBy", sensorURI),
					eq("ssn:observationProperty.type", observationType));

			ArrayNode result = dmsStorage.search(DmsStorage.DOCUMENT_TYPE.MEASUREMENT.toString(), query);
			// Expand JSON-LD documents:
			result = (ArrayNode) vitalClient.expand(result);
			// Return result
			return result;

		} catch (Exception e) {
			log.log(Level.WARNING, "", e);
			return objectMapper.createArrayNode();
		}
	}

}

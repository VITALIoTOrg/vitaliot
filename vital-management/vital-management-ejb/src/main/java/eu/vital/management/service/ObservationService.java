package eu.vital.management.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.vital.management.storage.DocumentManager;
import eu.vital.management.util.OntologyParser;
import eu.vital.management.util.VitalClient;
import org.bson.BsonDocument;
import org.bson.BsonInt32;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.datatype.DatatypeFactory;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class ObservationService {

	@Inject
	private Logger log;

	@Inject
	private DocumentManager documentManager;

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

	public Set<String> getObservationTypes() throws Exception {

		BsonDocument query = new BsonDocument();
		BsonDocument projection = new BsonDocument();
		projection.put("http://purl\\u002eoclc\\u002eorg/NET/ssnx/ssn#observes.@type", new BsonInt32(1));

		ArrayNode result = documentManager.search(DocumentManager.DOCUMENT_TYPE.SENSOR.toString(), query, projection);

		Set<String> types = new HashSet<>();
		for (JsonNode node : result) {
			JsonNode observes = node.get("http://purl.oclc.org/NET/ssnx/ssn#observes");
			if (observes.isArray()) {
				ArrayNode arrayNode = (ArrayNode) observes;
				for (JsonNode type : arrayNode) {
					types.add(type.get("@type").asText());
				}
			} else {
				types.add(observes.get("@type").asText());
			}
		}
		return types;
	}

	public ArrayNode fetchObservation(String sensorURI, String observationType) throws Exception {

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
			ArrayNode sensorArray = objectMapper.createArrayNode();
			sensorArray.add(sensorURI);
			operationInput.put("sensor", sensorArray);
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

	/**********************
	 * Random Observations:
	 **********************/

	public ArrayNode fetchRandomObservation(String sensorId, String property) throws Exception {
		// Return result with randomized data
		ArrayNode observations = objectMapper.createArrayNode();
		observations.add(randomizeTemplate(property));
		return observations;
	}

	private JsonNode randomizeTemplate(String type) throws Exception {
		String template = "{" +
				"\"@context\" : \"http://vital-iot.org/contexts/measurement.jsonld\"," +
				"\"uri\" : \"http://www.example.com/ico/3/observation/1\"," +
				"\"type\" : \"ssn:Observation\"," +
				"\"ssn:observationProperty\" : {" +
				"\"type\" : \"Color\"		}," +
				"\"ssn:observationResultTime\" : {" +
				"\"inXSDDateTime\" : \"2014-12-01T12:50:00\"" +
				"}," +
				"\"dul:hasLocation\" : {" +
				"\"type\" : \"geo:Point\"," +
				"\"geo:lat\" : 41.09226678," +
				"\"geo:long\" : 29.08480696," +
				"\"geo:alt\" : 0.0" +
				"}," +
				"\"ssn:observationResult\" : {" +
				"\"type\" : \"ssn:SensorOutput\"," +
				"\"ssn:hasValue\" : {" +
				"\"type\" : \"ssn:ObservationValue\"," +
				"\"value\" : \"1\"," +
				"\"qudt:unit\" : \"qudt:Color\"" +
				"}}," +
				"\"ssn:observedBy\" : \"http://www.example.com/ico/3\"		" +
				"}";

		ObjectNode obsTemplate = (ObjectNode) objectMapper.readTree(template);
		((ObjectNode) obsTemplate.get("ssn:observationProperty")).put("type", type);

		String obsType = obsTemplate.get("ssn:observationProperty").get("type").textValue();
		JsonNode obsTimeNode = obsTemplate.get("ssn:observationResultTime");
		if (obsTimeNode.isObject()) {
			((ObjectNode) obsTimeNode).put("inXSDDateTime",
					DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()).toString());
		}
		JsonNode obsResultNode = obsTemplate.get("ssn:observationResult").get("ssn:hasValue");
		if (obsResultNode.isObject()) {
			Random rand = new Random();
			if (obsType.endsWith("Temperature")) {
				((ObjectNode) obsResultNode).put("value", (double) Math.round((50 * rand.nextDouble() - 10) * 100) / 100);
			} else if (obsType.endsWith("Speed")) {
				((ObjectNode) obsResultNode).put("value", (double) Math.round(80 * rand.nextDouble() * 100) / 100);
			} else if (obsType.endsWith("Color")) {
				((ObjectNode) obsResultNode).put("value", rand.nextInt(3) + 1);
			} else if (obsType.endsWith("Errors")) {
				((ObjectNode) obsResultNode).put("value", rand.nextInt(11));
			} else if (obsType.endsWith("MaxRequests")) {
				((ObjectNode) obsResultNode).put("value", rand.nextInt(50) + 1);
			} else if (obsType.endsWith("MemAvailable"))
				((ObjectNode) obsResultNode).put("value", (double) Math.round(100 * rand.nextDouble() * 100) / 100);
			else if (obsType.endsWith("MemUsed")) {
				((ObjectNode) obsResultNode).put("value", (double) Math.round(100 * rand.nextDouble() * 100) / 100);
			} else if (obsType.endsWith("PendingRequests")) {
				((ObjectNode) obsResultNode).put("value", rand.nextInt(50) + 1);
			} else if (obsType.endsWith("ServedRequests")) {
				((ObjectNode) obsResultNode).put("value", rand.nextInt(50) + 1);
			} else if (obsType.endsWith("SystemLoad")) {
				((ObjectNode) obsResultNode).put("value", rand.nextInt(3) + 1);
			} else if (obsType.endsWith("UpTime")) {
				((ObjectNode) obsResultNode).put("value", (double) Math.round(2000 * rand.nextDouble() * 100) / 100);
			}
		}
		return obsTemplate;
	}

}

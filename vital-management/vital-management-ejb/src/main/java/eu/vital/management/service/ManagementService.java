package eu.vital.management.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.vital.management.storage.DocumentManager;
import eu.vital.management.util.OntologyParser;
import eu.vital.management.util.VitalClient;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.logging.Logger;

@Stateless
public class ManagementService {

	public static final String PERFORMANCE = "performance";
	public static final String CONFIGURATION = "configuration";

	@Inject
	private Logger log;

	@Inject
	private DocumentManager documentManager;

	@Inject
	private ObjectMapper objectMapper;

	@Inject
	SystemDAO systemDAO;

	@Inject
	ServiceDAO serviceDAO;

	@Inject
	private VitalClient vitalClient;

	public ArrayNode getSupportedPerformanceMetrics(String systemURI) throws Exception {
		// Connect to ES and get system info
		ObjectNode systemJSON = systemDAO.get(systemURI);
		ArrayNode systemServices = serviceDAO.searchBySystem(systemJSON);

		// Connect to GetSupportedPerformanceMetrics Service of this system:
		String operationURL = OntologyParser.findOperationURL(
				"http://vital-iot.eu/ontology/ns/MonitoringService",
				"http://vital-iot.eu/ontology/ns/GetSupportedPerformanceMetrics",
				systemServices);
		JsonNode result = vitalClient.doGet(operationURL);
		ArrayNode metricList = (ArrayNode) result;
		for (JsonNode metric : metricList) {
			ObjectNode metricJSON = (ObjectNode) metric;
			metricJSON.put("system", systemJSON.get("@id").asText());
		}
		return metricList;
	}

	public ArrayNode getPerformanceMetrics(String systemURI, JsonNode metricsQuery) throws Exception {
		/**
		 * "metric": [
		 * 	"http://vital-iot.eu/ontology/ns/SysLoad",
		 * 	"http://vital-iot.eu/ontology/ns/SysUptime"
		 * ]
		 */
		// Connect to ES and get system info
		ObjectNode systemJSON = systemDAO.get(systemURI);
		ArrayNode systemServices = serviceDAO.searchBySystem(systemJSON);

		// Connect to GetSupportedPerformanceMetrics Service of this system:
		String operationURL = OntologyParser.findOperationURL(
				"http://vital-iot.eu/ontology/ns/MonitoringService",
				"http://vital-iot.eu/ontology/ns/GetPerformanceMetrics",
				systemServices);
		JsonNode result = vitalClient.doPost(operationURL, metricsQuery);
		ArrayNode metrics = (ArrayNode) result;

		return metrics;
	}

	public ObjectNode getSystemConfiguration(String systemURI) throws Exception {
		// Connect to ES and get system info
		ObjectNode systemJSON = systemDAO.get(systemURI);
		ArrayNode systemServices = serviceDAO.searchBySystem(systemJSON);

		// Connect to GetSupportedPerformanceMetrics Service of this system:
		String operationURL = OntologyParser.findOperationURL(
				"http://vital-iot.eu/ontology/ns/ConfigurationService",
				"http://vital-iot.eu/ontology/ns/GetConfiguration",
				systemServices);
		JsonNode result = vitalClient.doGet(operationURL);
		ObjectNode configuration = (ObjectNode) result;
		configuration.put("system", systemJSON.get("@id").asText());
		return configuration;
	}

	public ObjectNode updateSystemConfiguration(String systemURI, JsonNode configurationData) throws Exception {
		// Connect to ES and get system info
		ObjectNode systemJSON = systemDAO.get(systemURI);
		ArrayNode systemServices = serviceDAO.searchBySystem(systemJSON);

		// Connect to GetSupportedPerformanceMetrics Service of this system:
		String operationURL = OntologyParser.findOperationURL(
				"http://vital-iot.eu/ontology/ns/ConfigurationService",
				"http://vital-iot.eu/ontology/ns/SetConfiguration",
				systemServices);

		vitalClient.doPost(operationURL, configurationData);
		return getSystemConfiguration(systemURI);
	}

}

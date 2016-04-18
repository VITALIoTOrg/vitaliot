package eu.vital.management.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.vital.management.security.SecurityService;
import eu.vital.management.security.VitalUserPrincipal;
import eu.vital.management.util.VitalClient;
import eu.vital.management.util.VitalConfiguration;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by anglen on 08/04/16.
 */
@Stateless
public class AdminService {

	@Inject
	Logger log;

	@EJB
	private SystemDAO systemDAO;

	@EJB
	private SensorDAO sensorDAO;

	@EJB
	private ServiceDAO serviceDAO;

	@Inject
	private VitalClient vitalClient;

	@Inject
	private ObjectMapper objectMapper;

	@Inject
	private ConfigurationDAO configurationDAO;

	@Inject
	private VitalConfiguration vitalConfiguration;

	@Inject
	VitalUserPrincipal userPrincipal;

	@Inject
	SecurityService securityService;

	public List<String> syncSystems() {

		List<String> result = new ArrayList<>();

		result.add("Starting SyncSystemsJob");

		// Login as platform to get authToken:
		String systemAuthToken = securityService.getSystemAuthenticationToken();
		userPrincipal.setToken(systemAuthToken);
		userPrincipal.setUser(securityService.getLoggedOnUser(systemAuthToken));
		result.add("SyncSystemsJob: Login success");
		try {
			for (String systemURL : getSystemUrls()) {
				result.add("----------");
				result.add("Syncing System " + systemURL);
				try {
					result.add("1. Connecting to: " + systemURL + "/metadata");
					JsonNode systemJSON = syncSystem(systemURL);
					result.add("Retrieved system: " + systemJSON.get("@id").asText());
					result.add("2. Connecting to: " + systemURL + "/sensor/metadata");
					ArrayNode sensorList = syncSensors(systemJSON);
					result.add("Retrieved system/sensors: " + sensorList.size());
					result.add("3. Connecting to: " + systemURL + "/service/metadata");
					ArrayNode serviceList = syncServices(systemJSON);
					result.add("4. Retrieved system/services: " + serviceList.size());
				} catch (Exception e) {
					result.add("Failure: " + e.getMessage());
				}
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "Failed to sync", e);
		}
		result.add("Finished SyncSystemsJob");

		return result;
	}

	private Set<String> getSystemUrls() throws Exception {
		Set<String> systemUrls = new HashSet<>();

		// 1. Read from configuration
		ArrayNode configurationURLs = (ArrayNode) configurationDAO.get().get("system_urls");
		for (int i = 0; i < configurationURLs.size(); i++) {
			systemUrls.add(configurationURLs.get(i).asText());
		}

		// 2. Connect to IotDataAdapter:
		try {
			String iotDataAdapterUrl = vitalConfiguration.getProperty("vital-management.iot-data-adapter", "http://localhost:8080/vital-core-iot-data-adapter/rest") + "/systems";
			JsonNode iotDataAdapterSystems = vitalClient.doGet(iotDataAdapterUrl);
			if (iotDataAdapterSystems.isArray()) {
				ArrayNode arrayNode = (ArrayNode) iotDataAdapterSystems;
				for (int i = 0; i < arrayNode.size(); i++) {
					systemUrls.add(arrayNode.get(i).get("ppi").asText());
				}
			}
		} catch (Exception e) {
			// Do nothing
		}

		// 3. TODO: Connect to discovery

		return systemUrls;
	}

	private ObjectNode syncSystem(String systemURL) throws Exception {
		ObjectNode postData = objectMapper.createObjectNode();

		ObjectNode systemJSON = (ObjectNode) vitalClient.doPost(systemURL + "/metadata", postData);
		systemJSON.put("url", systemURL);

		systemJSON = systemDAO.save(systemJSON);

		return systemJSON;
	}

	private ArrayNode syncSensors(JsonNode systemJSON) throws Exception {
		String systemURL = systemJSON.get("url").asText();

		ObjectNode postData = objectMapper.createObjectNode();
		ArrayNode sensorList = (ArrayNode) vitalClient.doPost(systemURL + "/sensor/metadata", postData);

		for (JsonNode sensor : sensorList) {
			ObjectNode sensorJSON = (ObjectNode) sensor;
			sensorJSON.put("system", systemJSON.get("@id").asText());
			sensorDAO.save(sensorJSON);
		}
		return sensorList;
	}

	private ArrayNode syncServices(JsonNode systemJSON) throws Exception {
		String systemURL = systemJSON.get("url").asText();

		ObjectNode postData = objectMapper.createObjectNode();
		ArrayNode serviceList = (ArrayNode) vitalClient.doPost(systemURL + "/service/metadata", postData);

		for (JsonNode service : serviceList) {
			ObjectNode serviceJSON = (ObjectNode) service;
			serviceJSON.put("system", systemJSON.get("@id").asText());
			serviceDAO.save(serviceJSON);
		}
		return serviceList;
	}
}

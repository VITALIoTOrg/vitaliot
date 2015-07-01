package eu.vital.orchestrator.job;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.vital.orchestrator.service.ConfigurationDAO;
import eu.vital.orchestrator.service.SensorDAO;
import eu.vital.orchestrator.service.ServiceDAO;
import eu.vital.orchestrator.service.SystemDAO;
import eu.vital.orchestrator.util.VitalClient;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.ExecuteInJTATransaction;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.ejb.EJB;
import javax.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;

@DisallowConcurrentExecution
@ExecuteInJTATransaction
public class SyncSystemsJob implements Job {

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


	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		launch();
	}

	public void launch() {
		log.info("Starting SyncSystemsJob");

		try {
			ArrayNode systemURLs = (ArrayNode) configurationDAO.get().get("system_urls");
			for (int i = 0; i < systemURLs.size(); i++) {
				String systemURL = systemURLs.get(i).asText();
				log.info("SyncSystem " + systemURL);
				JsonNode systemJSON = syncSystem(systemURL);
				log.info("SyncSystem: " + systemJSON.get("@id").asText());
				ArrayNode sensorList = syncSensors(systemJSON);
				log.info("SyncSystem/Sensors: " + sensorList.size());
				ArrayNode serviceList = syncServices(systemJSON);
				log.info("SyncSystem/Services: " + serviceList.size());
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "Failed to sync", e);
		}
		log.info("Finished SyncSystemsJob");
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


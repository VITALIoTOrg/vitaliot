package eu.vital.orchestrator.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import eu.vital.orchestrator.storage.OrchestratorStorage;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class ConfigurationDAO {

	@Inject
	private OrchestratorStorage orchestratorStorage;

	@Inject
	private ObjectMapper objectMapper;

	public ArrayNode list() throws Exception {
		// Connect to ES and retrieve result
		return orchestratorStorage.getList(OrchestratorStorage.DOCUMENT_TYPE.CONFIGURATION.toString());
	}

	public JsonNode get() throws Exception {
		return list().get(0);
	}

}

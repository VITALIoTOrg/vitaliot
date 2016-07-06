package eu.vital.orchestrator.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.vital.orchestrator.storage.OrchestratorStorage;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Date;
import java.util.logging.Logger;

@Stateless
public class OperationDAO {

	@Inject
	Logger log;

	@Inject
	OrchestratorStorage orchestratorStorage;

	/**
	 * ****************
	 * Services
	 * ****************
	 */

	public ArrayNode getList() throws Exception {
		return orchestratorStorage.getList(OrchestratorStorage.DOCUMENT_TYPE.OPERATION.toString());
	}

	public JsonNode get(String id) throws Exception {
		return orchestratorStorage.get(OrchestratorStorage.DOCUMENT_TYPE.OPERATION.toString(), id);
	}

	public JsonNode create(JsonNode operation) throws Exception {
		ObjectNode operationObject = (ObjectNode) operation;
		// Default values
		operationObject.put("dateCreated", new Date().getTime());
		String id = orchestratorStorage.create(OrchestratorStorage.DOCUMENT_TYPE.OPERATION.toString(), operationObject);
		return orchestratorStorage.get(OrchestratorStorage.DOCUMENT_TYPE.OPERATION.toString(), id);
	}

	public JsonNode update(String id, JsonNode operation) throws Exception {
		orchestratorStorage.update(OrchestratorStorage.DOCUMENT_TYPE.OPERATION.toString(), id, operation);
		return orchestratorStorage.get(OrchestratorStorage.DOCUMENT_TYPE.OPERATION.toString(), id);
	}

	public void delete(String id) throws Exception {
		orchestratorStorage.delete(OrchestratorStorage.DOCUMENT_TYPE.OPERATION.toString(), id);
	}

}

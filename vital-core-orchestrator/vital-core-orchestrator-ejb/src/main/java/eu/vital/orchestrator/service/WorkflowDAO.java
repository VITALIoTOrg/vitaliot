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
public class WorkflowDAO {

	@Inject
	Logger log;

	@Inject
	OrchestratorStorage orchestratorStorage;

	/**
	 * ****************
	 * Services
	 * ****************
	 */

	public ArrayNode getWorkflowList() throws Exception {
		return orchestratorStorage.getList(OrchestratorStorage.DOCUMENT_TYPE.WORKFLOW.toString());
	}

	public JsonNode getWorkflow(String id) throws Exception {
		return orchestratorStorage.get(OrchestratorStorage.DOCUMENT_TYPE.WORKFLOW.toString(), id);
	}

	public JsonNode createWorkflow(JsonNode workflow) throws Exception {
		ObjectNode workflowObject = (ObjectNode) workflow;
		// Default values
		workflowObject.put("dateCreated", new Date().getTime());
		if (!workflowObject.has("status")) {
			workflowObject.put("status", "DISABLED");
		}
		if (!workflowObject.has("access")) {
			workflowObject.put("access", "PRIVATE");
		}
		String id = orchestratorStorage.create(OrchestratorStorage.DOCUMENT_TYPE.WORKFLOW.toString(), workflowObject);
		return orchestratorStorage.get(OrchestratorStorage.DOCUMENT_TYPE.WORKFLOW.toString(), id);
	}

	public JsonNode update(String id, JsonNode workflow) throws Exception {
		orchestratorStorage.update(OrchestratorStorage.DOCUMENT_TYPE.WORKFLOW.toString(), id, workflow);
		return orchestratorStorage.get(OrchestratorStorage.DOCUMENT_TYPE.WORKFLOW.toString(), id);
	}

	public void delete(String id) throws Exception {
		orchestratorStorage.delete(OrchestratorStorage.DOCUMENT_TYPE.WORKFLOW.toString(), id);
	}

}

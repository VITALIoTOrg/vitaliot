package eu.vital.orchestrator.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.vital.orchestrator.storage.DocumentManager;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Date;
import java.util.logging.Logger;

@Stateless
public class MetaserviceDAO {

	@Inject
	Logger log;

	@Inject
	DocumentManager documentManager;

	/**
	 * ****************
	 * Services
	 * ****************
	 */

	public ArrayNode getMetaserviceList() throws Exception {
		return documentManager.getList(DocumentManager.DOCUMENT_TYPE.METASERVICE.toString());
	}

	public JsonNode getMetaservice(String id) throws Exception {
		return documentManager.get(DocumentManager.DOCUMENT_TYPE.METASERVICE.toString(), id);
	}

	public JsonNode createMetaservice(JsonNode workflow) throws Exception {
		ObjectNode workflowObject = (ObjectNode) workflow;
		// Default values
		workflowObject.put("dateCreated", new Date().getTime());
		String id = documentManager.create(DocumentManager.DOCUMENT_TYPE.METASERVICE.toString(), workflowObject);
		return documentManager.get(DocumentManager.DOCUMENT_TYPE.METASERVICE.toString(), id);
	}

	public JsonNode update(String id, JsonNode workflow) throws Exception {
		documentManager.update(DocumentManager.DOCUMENT_TYPE.METASERVICE.toString(), id, workflow);
		return documentManager.get(DocumentManager.DOCUMENT_TYPE.METASERVICE.toString(), id);
	}

	public void delete(String id) throws Exception {
		documentManager.delete(DocumentManager.DOCUMENT_TYPE.METASERVICE.toString(), id);
	}

}

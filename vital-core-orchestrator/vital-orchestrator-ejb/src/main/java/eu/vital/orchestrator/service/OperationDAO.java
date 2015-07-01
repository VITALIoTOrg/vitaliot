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
public class OperationDAO {

	@Inject
	Logger log;

	@Inject
	DocumentManager documentManager;

	/**
	 * ****************
	 * Services
	 * ****************
	 */

	public ArrayNode getList() throws Exception {
		return documentManager.getList(DocumentManager.DOCUMENT_TYPE.OPERATION.toString());
	}

	public JsonNode get(String id) throws Exception {
		return documentManager.get(DocumentManager.DOCUMENT_TYPE.OPERATION.toString(), id);
	}

	public JsonNode create(JsonNode operation) throws Exception {
		ObjectNode operationObject = (ObjectNode) operation;
		// Default values
		operationObject.put("dateCreated", new Date().getTime());
		String id = documentManager.create(DocumentManager.DOCUMENT_TYPE.OPERATION.toString(), operationObject);
		return documentManager.get(DocumentManager.DOCUMENT_TYPE.OPERATION.toString(), id);
	}

	public JsonNode update(String id, JsonNode operation) throws Exception {
		documentManager.update(DocumentManager.DOCUMENT_TYPE.OPERATION.toString(), id, operation);
		return documentManager.get(DocumentManager.DOCUMENT_TYPE.OPERATION.toString(), id);
	}

	public void delete(String id) throws Exception {
		documentManager.delete(DocumentManager.DOCUMENT_TYPE.OPERATION.toString(), id);
	}

}

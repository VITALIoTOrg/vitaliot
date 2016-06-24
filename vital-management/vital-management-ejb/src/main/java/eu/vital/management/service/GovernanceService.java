package eu.vital.management.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import eu.vital.management.storage.DocumentManager;
import eu.vital.management.util.VitalClient;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class GovernanceService {

	@Inject
	private DocumentManager documentManager;

	@Inject
	private ObjectMapper objectMapper;

	@Inject
	private VitalClient vitalClient;

	public JsonNode getBoundaries() throws Exception {
		ArrayNode boundariesResult = documentManager.getList(DocumentManager.DOCUMENT_TYPE.BOUNDARIES.toString());
		if (boundariesResult.size() > 0) {
			return boundariesResult.get(0);
		} else {
			return saveBoundaries(objectMapper.createObjectNode());
		}
	}

	public JsonNode saveBoundaries(JsonNode boundaries) throws Exception {
		documentManager.update(DocumentManager.DOCUMENT_TYPE.BOUNDARIES.toString(), "BOUNDARIES", boundaries);
		JsonNode savedBoundaries = getBoundaries();

		//-----------------------------
		// TODO: Add code here to update global security policies based on "savedBoundaries"
		// vitalClient.doPost(....);
		//-----------------------------

		return savedBoundaries;
	}

}

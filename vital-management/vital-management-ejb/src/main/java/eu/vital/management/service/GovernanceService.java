package eu.vital.management.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.vital.management.storage.DocumentManager;
import eu.vital.management.util.VitalClient;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Date;

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

	public JsonNode getAccess(String groupName) throws Exception {
		JsonNode access = documentManager.get(DocumentManager.DOCUMENT_TYPE.ACCESS.toString(), groupName);
		if (access == null) {
			return saveAccess(groupName, objectMapper.createObjectNode());
		} else {
			return access;
		}
	}

	public JsonNode saveAccess(String groupName, JsonNode access) throws Exception {
		documentManager.update(DocumentManager.DOCUMENT_TYPE.ACCESS.toString(), groupName, access);
		JsonNode savedAccess = getAccess(groupName);

		//-----------------------------
		// TODO: Add code here to update group policies based on "savedAccess"
		// vitalClient.doPost(....);
		//-----------------------------

		return savedAccess;
	}

	public JsonNode getSLAObservations(String groupName, String slaType) throws Exception {
		//-----------------------------
		// TODO: Add code here to retrieve a set of SLA metric for the slaType
		//-----------------------------
		if ("throughput".equals(slaType)) {
			// Return a random array now:
			ArrayNode slas = objectMapper.createArrayNode();
			Date now = new Date();
			for (int i = 200; i > 0; i--) {
				ObjectNode sla = objectMapper.createObjectNode();
				sla.put("timestamp", now.getTime() - i * 10000);
				sla.put("value", 1 + Math.random() * 100);

				slas.add(sla);
			}
			return slas;
		}
		throw new Exception("Not supported Type");
	}

}

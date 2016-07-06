package eu.vital.orchestrator.engine.adapter;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;

public class LogAdapter {

	@Inject
	private ObjectMapper objectMapper;

	private ArrayList<String> logArray = new ArrayList<>();

	public void log(String message) {
		Date d = new Date();
		logArray.add(d.toString() + ": " + message);
	}

	public ArrayNode getLogs() {
		ArrayNode logs = objectMapper.createArrayNode();
		for (String log : logArray) {
			logs.add(log);
		}
		return logs;
	}

	public void clearLogs() {
		this.logArray.clear();
	}
}
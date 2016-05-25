package eu.vital.orchestrator.engine;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.vital.orchestrator.engine.adapter.DmsAdapter;
import eu.vital.orchestrator.engine.adapter.LogAdapter;
import eu.vital.orchestrator.engine.adapter.ObservationAdapter;
import eu.vital.orchestrator.engine.adapter.SensorAdapter;
import eu.vital.orchestrator.engine.adapter.SystemAdapter;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import javax.inject.Inject;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

public class WorkFlowEngine {

	@Inject
	Logger log;

	@Inject
	ObjectMapper objectMapper;

	@Inject
	LogAdapter logAdapter;

	@Inject
	SystemAdapter systemAdapter;

	@Inject
	ObservationAdapter observationAdapter;

	@Inject
	SensorAdapter sensorAdapter;

	@Inject
	DmsAdapter dmsAdapter;

	private Map convertToMap(ObjectNode node) {
		return objectMapper.convertValue(node, Map.class);
	}

	private Collection<Map> convertToCollection(ArrayNode array) {
		Collection<Map> mapCollection = new ArrayList<>();
		for (JsonNode node : array) {
			mapCollection.add(convertToMap((ObjectNode) node));
		}
		return mapCollection;
	}

	private JsonNode covertToNode(Object object) {
		if (object instanceof ScriptObjectMirror) {
			ScriptObjectMirror scriptObjectMirror = (ScriptObjectMirror) object;
			if (scriptObjectMirror.isArray()) {
				ArrayNode arrayNode = objectMapper.createArrayNode();
				for (Object o : scriptObjectMirror.values()) {
					JsonNode jsNode = objectMapper.valueToTree(o);
					arrayNode.add(jsNode);
				}
				return arrayNode;

			}
		}
		return objectMapper.valueToTree(object);
	}

	private ScriptEngine createScriptEngine() {
		ScriptEngineManager engineManager = new ScriptEngineManager();
		ScriptEngine scriptEngine = engineManager.getEngineByName("js");
		scriptEngine.put("log", logAdapter);
		scriptEngine.put("systemAdapter", systemAdapter);
		scriptEngine.put("sensorAdapter", sensorAdapter);
		scriptEngine.put("dmsAdapter", dmsAdapter);
		scriptEngine.put("observationAdapter", observationAdapter);
		scriptEngine.put("objectMapper", objectMapper);
		return scriptEngine;
	}

	public JsonNode executeOperation(JsonNode operation, JsonNode input) {
		logAdapter.log(operation.get("name").asText());
		ObjectNode output = objectMapper.createObjectNode();
		output.put("name", operation.get("name"));

		try {
			ScriptEngine scriptEngine = createScriptEngine();
			String script = operation.get("script").asText();
			scriptEngine.eval(script);
			Invocable inv = (Invocable) scriptEngine;

			if (input.isArray()) {
				Collection<Map> inputData = convertToCollection((ArrayNode) input);
				Object outputData = inv.invokeFunction("execute", inputData);
				output.put("outputData", covertToNode(outputData));
			} else {
				Map inputData = convertToMap((ObjectNode) input);
				Object outputData = inv.invokeFunction("execute", inputData);
				output.put("outputData", covertToNode(outputData));
			}
		} catch (Exception e) {
			output.put("error", "" + e.getMessage());
		}
		logAdapter.log("End: " + operation.get("name").asText());

		output.put("log", logAdapter.getLogs());
		return output;
	}

	public Map<String, JsonNode> executeWorkflow(JsonNode workflow, JsonNode input) throws ScriptException, NoSuchMethodException, IOException {
		ObjectNode nodes = (ObjectNode) workflow.get("nodes");

		// Results contains all the outputs of each execution
		Map<String, JsonNode> workflowResults = new HashMap<>();

		// Add input
		ObjectNode inputOutput = objectMapper.createObjectNode();
		inputOutput.put("outputData", input);
		inputOutput.put("log", objectMapper.createArrayNode());
		inputOutput.put("name", "input");
		workflowResults.put("input", inputOutput);

		boolean finished = false;
		while (!finished) {

			//1. Which nodes can start
			Map<String, JsonNode> readyToExecute = new HashMap<>();
			Iterator<String> nodeNames = nodes.fieldNames();
			while (nodeNames.hasNext()) {
				String nodeName = nodeNames.next();
				JsonNode node = nodes.get(nodeName);
				if (!workflowResults.containsKey(nodeName) && node.has("operation")) {
					ArrayNode incomingNodes = (ArrayNode) node.get("incoming");
					boolean ready = true;
					for (JsonNode incomingNode : incomingNodes) {
						if (!workflowResults.containsKey(incomingNode.asText())) {
							ready = false;
							break;
						} else {

						}
					}
					if (ready) {
						readyToExecute.put(nodeName, node);
					}
				}
			}

			if (readyToExecute.isEmpty()) {
				// Cannot continue, stop the loop
				finished = true;
			} else {
				//2. Execute operations
				for (String nodeName : readyToExecute.keySet()) {
					JsonNode node = readyToExecute.get(nodeName);
					ObjectNode operationInput = objectMapper.createObjectNode();
					ArrayNode incomingNodes = (ArrayNode) node.get("incoming");
					for (JsonNode incomingNode : incomingNodes) {
						operationInput.put(incomingNode.asText(), workflowResults.get(incomingNode.asText()).get("outputData"));
					}
					JsonNode operation = node.get("operation");
					// Execute
					JsonNode operationResult = executeOperation(operation, operationInput);
					// Store Result
					workflowResults.put(nodeName, operationResult);
					logAdapter.clearLogs();
					// In case of error, break workflow execution and return results so far
					if (operationResult.has("error")) {
						return workflowResults;
					}
				}
			}
		}

		//3. Gather output results and add them
		ObjectNode outputOutput = objectMapper.createObjectNode();
		ObjectNode operationOutputData = objectMapper.createObjectNode();
		ArrayNode incomingNodes = (ArrayNode) nodes.get("output").get("incoming");
		for (JsonNode incomingNode : incomingNodes) {
			operationOutputData.put(incomingNode.asText(), workflowResults.get(incomingNode.asText()).get("outputData"));
		}
		outputOutput.put("outputData", operationOutputData);
		outputOutput.put("log", objectMapper.createArrayNode());
		outputOutput.put("name", "output");
		workflowResults.put("output", outputOutput);

		return workflowResults;
	}
}

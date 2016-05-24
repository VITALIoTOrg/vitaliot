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

	public ArrayNode executeWorkflow(JsonNode workflow, JsonNode input) throws ScriptException, NoSuchMethodException, IOException {
		ArrayNode operationList = (ArrayNode) workflow.get("operationList");
		ArrayNode workflowResult = objectMapper.createArrayNode();

		JsonNode operationResult;
		for (JsonNode operation : operationList) {
			operationResult = executeOperation(operation, input);
			workflowResult.add(operationResult);
			if (operationResult.has("error")) {
				return workflowResult;
			}
			input = operationResult.get("outputData");
			logAdapter.clearLogs();
		}
		return workflowResult;
	}
}

/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.vital.management.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class OntologyParser {

	public static ObjectNode findOperation(String serviceType, String operationType, ArrayNode serviceList) throws Exception {
		for (JsonNode service : serviceList) {
			if (service.get("@type").asText().equals(serviceType)) {
				JsonNode operationList = (JsonNode) service.get("http://iserve.kmi.open.ac.uk/ns/msm#hasOperation");
				if (operationList.isArray()) {
					for (JsonNode operation : operationList) {
						if (operation.get("@type").asText().equals(operationType)) {
							return (ObjectNode) operation;
						}
					}
				} else {
					if (operationList.get("@type").asText().equals(operationType)) {
						return (ObjectNode) operationList;
					}
				}
			}
		}
		throw new Exception("operation of " + serviceType + " " + operationType + " not supported");
	}


	public static String findOperationURL(String serviceType, String operationType, ArrayNode serviceList) throws Exception {
		ObjectNode operation = findOperation(serviceType, operationType, serviceList);
		String operationURL = operation
				.get("http://www.wsmo.org/ns/hrests#hasAddress")
				.get("@value")
				.asText();
		return operationURL;
	}

}

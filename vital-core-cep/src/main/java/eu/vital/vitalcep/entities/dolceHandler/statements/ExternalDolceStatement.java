/* 
 *	CEP REST Interface
 *	Copyright (c) Atos S.A.
 *	Research & Innovation - Internet of Everything Lab
 *	All Rights Reserved.
 *	
 *	ATOS PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*/
package eu.vital.vitalcep.entities.dolceHandler.statements;

import java.io.Serializable;
import java.util.StringTokenizer;

import org.json.JSONObject;

// TODO: Auto-generated Javadoc
/**
 * The Class ExternalDolceStatement.
 * 
 * External Statement container
 */
public class ExternalDolceStatement extends DolceStatement implements
		Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The type. */
	private String type = "";
	
	/** The value. */
	private String value = "";

	/** The string b_statement. */
	private StringBuilder stringB_statement = null;

	// Empty constructor
	/**
	 * Instantiates a new external dolce statement.
	 */
	public ExternalDolceStatement() {
	}

	// JSON constructor
	/**
	 * Instantiates a new external dolce statement from a JSON object
	 *
	 * @param obj the obj
	 */
	public ExternalDolceStatement(JSONObject obj) {
		id = obj.getString("name");
		type = obj.getString("type");
		value = obj.getString("value");
	}

	// param constructor
	/**
	 * Instantiates a new external dolce statement from its params in json format
	 *
	 * @param name the name
	 * @param type the type
	 * @param externalType the external type
	 * @param externalValue the external value
	 */
	public ExternalDolceStatement(String name, String type,
			String externalType, String externalValue) {
		super();
		this.id = name;
		this.type = externalType;
		this.value = externalValue;
	}

	// Plain text constructor
	/**
	 * Instantiates a new external dolce statement from its declaration in dolce language
	 *
	 * @param body the body
	 * @throws Exception the exception
	 */
	public ExternalDolceStatement(String body) throws Exception {

		StringTokenizer st = new StringTokenizer(body);

		String s = st.nextToken();

		if (s.equalsIgnoreCase("external")) {
			type = st.nextToken();
			id = st.nextToken();
			value = body.substring(body.indexOf("=") + 1).replace(";", "")
					.trim();

		} else
			throw new Exception(
					"Dolce Exception, body do not correspondes to a known statement.");

	}

	// To Plain Text String
	/**
	 * To plain text string.
	 *
	 * @return the external statement in dolce language format
	 */
	public String toPlainTextString() {
		stringB_statement = new StringBuilder();
		stringB_statement.append("external " + this.type + " " + this.id
				+ " = " + this.value + ";");

		return stringB_statement.toString();
	}

	// To JSON object
	/**
	 * To json.
	 *
	 * @return the JSON object representing the external
	 */
	public JSONObject toJson() {
		JSONObject obj = new JSONObject();
		obj.put("name", id);
		obj.put("type", type);
		obj.put("value", value);

		return obj;
	}

	/**
	 * ************************************************************
	 * GETTERS AND SETTERS *
	 * ************************************************************.
	 *
	 * @return the id
	 */

	/**
	 * @return the name
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param name            the name to set
	 */
	public void setId(String name) {
		this.id = name;
	}

	/**
	 * Gets the external type.
	 *
	 * @return the externalType
	 */
	public String getExternalType() {
		return type;
	}

	/**
	 * Sets the external type.
	 *
	 * @param externalType            the externalType to set
	 */
	public void setExternalType(String externalType) {
		this.type = externalType;
	}

	/**
	 * Gets the external value.
	 *
	 * @return the externalValue
	 */
	public String getExternalValue() {
		return value;
	}

	/**
	 * Sets the external value.
	 *
	 * @param externalValue            the externalValue to set
	 */
	public void setExternalValue(String externalValue) {
		this.value = externalValue;
	}

}

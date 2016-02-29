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
 * The Class EventStatement.
 * 
 * Event Dolce Statement Container
 */
public class EventStatement extends DolceStatement implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The definition. */
	private String definition = "";

	/** The string b_statement. */
	private StringBuilder stringB_statement = null;

	// Empty constructor
	/**
	 * Instantiates a new event statement.
	 */
	public EventStatement() {
	}

	// JSON constructor
	/**
	 * Instantiates a new event statement from a JSON object
	 *
	 * @param obj the obj
	 */
	public EventStatement(JSONObject obj) {
		id = obj.getString("id");
		definition = obj.getString("definition");
	}

	// param constructor
	/**
	 * Instantiates a new event statement from its params in json format
	 *
	 * @param id the id
	 * @param definition the definition
	 */
	public EventStatement(String id, String definition) {
		this.id = id;
		this.definition = definition;
	}

	// Plain text constructor
	/**
	 * Instantiates a new event statement from its declaration in dolce language
	 *
	 * @param body the body
	 * @throws Exception the exception
	 */
	public EventStatement(String body) throws Exception {

		StringTokenizer st = new StringTokenizer(body);

		id = st.nextToken();
		this.definition = body.substring(body.indexOf("{") + 1,
				body.lastIndexOf("}"));
		this.definition = this.definition.trim().replace("\t", "")
				.replace("	", "");
		// this.definition = "\t"+this.definition;

	}

	// To Plain Text String
	/**
	 * To plain text string.
	 *
	 * @return the event statement in dolce language format
	 */
	public String toPlainTextString() {
		stringB_statement = new StringBuilder();
		stringB_statement.append("event " + this.id + " " + "{\n\t"
				+ this.definition.replace(";", ";\n\t") + "}");

		return stringB_statement.toString();
	}

	// To JSON object
	/**
	 * To json.
	 *
	 * @return the JSON object representing the event
	 */
	public JSONObject toJson() {
		JSONObject obj = new JSONObject();
		obj.put("id", id);
		obj.put("definition", definition.trim());

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
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Gets the definitione.
	 *
	 * @return the definition
	 */
	public String getDefinitione() {
		return definition;
	}

	/**
	 * Sets the definitione.
	 *
	 * @param definition the new definitione
	 */
	public void setDefinitione(String definition) {
		this.definition = definition;
	}

}

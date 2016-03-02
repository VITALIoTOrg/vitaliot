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

// TODO: Auto-generated Javadoc
/**
 * A factory for creating DolceStatement objects.
 */
public class DolceStatementFactory implements Serializable {

	/** The Constant EXTERNAL. */
	public static final String EXTERNAL = "external";
	
	/** The Constant COMPLEX. */
	public static final String COMPLEX = "complex";
	
	/** The Constant EVENT. */
	public static final String EVENT = "event";

	// private String name="";
	// private String body="";
	// private String type="";
	// private String externalType="";
	// private String externalValue="";
	// private StringBuilder stringB_statement = null;

	/**
	 * Instantiates a new dolce statement factory.
	 */
	private DolceStatementFactory() {
	}

	// Plain text constructor
	/**
	 * Gets a dolce statement from its dolce language declaration.
	 *
	 * @param body the body
	 * @return the dolce statement
	 * @throws Exception the exception
	 */
	public static DolceStatement getDolceStatement(String body)
			throws Exception {

		String name = "";
		String type = "";
		String externalType = "";
		String externalValue = "";
		StringBuilder stringB_statement = null;

		StringTokenizer st = new StringTokenizer(body);

		String s = st.nextToken();

		if (s.equalsIgnoreCase("event")) {
			type = s;
			name = st.nextToken();
			body = body.substring(body.indexOf("{") + 1, body.lastIndexOf("}"));
			body.trim();
			// body = "\t"+body;
			EventStatement ds = new EventStatement();
			ds.setId(name);
			ds.setDefinition(body);

			return ds;

		} else if (s.equalsIgnoreCase("complex")) {
			type = s;
			name = st.nextToken();
			body = body.substring(body.indexOf("{") + 1, body.lastIndexOf("}"));
			body.trim();
			// body = "\t"+body;
			ComplexStatement ds = new ComplexStatement();
			ds.setId(name);
			ds.setDefinitione(body);

			return ds;

		} else if (s.equalsIgnoreCase("external")) {
			type = s;
			externalType = st.nextToken();
			name = st.nextToken();
			externalValue = body.substring(body.indexOf("=") + 1)
					.replace(";", "").trim();
			body = "";
			ExternalDolceStatement ds = new ExternalDolceStatement();
			ds.setId(name);
			ds.setExternalType(externalType);
			ds.setExternalValue(externalValue);

			return ds;

		} else {
			throw new Exception(
					"Dolce Exception, body do not correspondes to a known statement.");

		}

	}

	// //To Plain Text String
	// public String toPlainTextString (){
	// if (this.type.equalsIgnoreCase("external")){
	// stringB_statement = new StringBuilder();
	// stringB_statement.append("external " + this.externalType + " " +
	// this.name + " = " + this.externalValue + ";");
	// }else if (this.type.equalsIgnoreCase("event")){
	// stringB_statement = new StringBuilder();
	// stringB_statement.append("event " + this.name + "\r{\r" + this.body +
	// "\r}");
	// }else if (this.type.equalsIgnoreCase("complex")){
	// stringB_statement = new StringBuilder();
	// stringB_statement.append("complex " + this.name + "\r{\r" + this.body +
	// "\r}");
	// }
	// return stringB_statement.toString();
	// }
	//
	// //To JSON String
	// public String toString(){
	// // if (this.type.equalsIgnoreCase("external")){
	// stringB_statement = new StringBuilder();
	// stringB_statement.append(" type : ").append(this.type)
	// .append(" externalType : ").append(this.externalType)
	// .append(" name : ").append(this.name)
	// .append(" externalValue : ").append(this.externalValue)
	// .append(" body : ").append(this.body);
	// // }else if (this.type.equalsIgnoreCase("event")){
	// // stringB_statement = new StringBuilder();
	// // stringB_statement.append(" type : ").append(this.type)
	// // .append(" externalType : ").append(this.externalType)
	// // .append(" name : ").append(this.name)
	// // .append(" externalValue ").append()
	// // .append(" body : ").append(this.body);
	// // }else if (this.type.equalsIgnoreCase("complex")){
	// // stringB_statement = new StringBuilder();
	// // stringB_statement.append("complex " + this.name + "\r{\r" + this.body
	// + "\r}");
	// // }
	// return stringB_statement.toString();
	// }
	//
	//
	//
	// /**************************************************************
	// * GETTERS AND SETTERS *
	// **************************************************************/
	//
	//
	// /**
	// * @return the name
	// */
	// public String getName() {
	// return name;
	// }
	//
	// /**
	// * @param name the name to set
	// */
	// public void setName(String name) {
	// this.name = name;
	// }
	//
	// /**
	// * @return the body
	// */
	// public String getBody() {
	// return body;
	// }
	//
	// /**
	// * @param body the body to set
	// */
	// public void setBody(String body) {
	// this.body = body;
	// }
	//
	// /**
	// * @return the type
	// */
	// public String getType() {
	// return type;
	// }
	//
	// /**
	// * @param type the type to set
	// */
	// public void setType(String type) {
	// this.type = type;
	// }
	//
	// /**
	// * @return the externalType
	// */
	// public String getExternalType() {
	// return externalType;
	// }
	//
	// /**
	// * @param externalType the externalType to set
	// */
	// public void setExternalType(String externalType) {
	// this.externalType = externalType;
	// }
	//
	// /**
	// * @return the externalValue
	// */
	// public String getExternalValue() {
	// return externalValue;
	// }
	//
	// /**
	// * @param externalValue the externalValue to set
	// */
	// public void setExternalValue(String externalValue) {
	// this.externalValue = externalValue;
	// }
	//

}

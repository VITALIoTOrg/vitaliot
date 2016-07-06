package eu.vital.discoverer.util;

import java.util.HashMap;



public final class OntologyUtils {

	// static map for all possible available abbreviations
	private static final HashMap<String, String> abbreviations=new HashMap<String, String>();

	// populate map with abbreviations and their expansions
	static{
		abbreviations.put("dcn", "http://www.w3.org/2007/uwa/context/deliveryContext.owl#");
		abbreviations.put("dul","http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#");
		abbreviations.put("geo","http://www.w3.org/2003/01/geo/wgs84_pos#");
		abbreviations.put("msm","http://iserve.kmi.open.ac.uk/ns/msm#");
		abbreviations.put("net","http://www.w3.org/2007/uwa/context/network.owl#");
		abbreviations.put("otn","http://www.pms.ifi.lmu.de/rewerse-wga1/otn/OTN.owl");
		abbreviations.put("owl","http://www.w3.org/2002/07/owl#");
		abbreviations.put("rdf","http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		abbreviations.put("ssn","http://purl.oclc.org/NET/ssnx/ssn#");
		abbreviations.put("wsl","http://www.wsmo.org/ns/wsmo-lite#");
		abbreviations.put("xml","http://www.w3.org/XML/1998/namespace");
		abbreviations.put("xsd","http://www.w3.org/2001/XMLSchema#");
		abbreviations.put("foaf","http://xmlns.com/foaf/");
		abbreviations.put("hard","http://www.w3.org/2007/uwa/context/hardware.owl#");
		abbreviations.put("http","http://www.w3.org/2011/http#");
		abbreviations.put("qudt","http://qudt.org/schema/qudt#");
		abbreviations.put("rdfs","http://www.w3.org/2000/01/rdf-schema#");
		abbreviations.put("s4ac","http://ns.inria.fr/s4ac/v2#");
		abbreviations.put("soft","http://www.w3.org/2007/uwa/context/software.owl#");
		abbreviations.put("time","http://www.w3.org/2006/time#");
		abbreviations.put("hrest","http://www.wsmo.org/ns/hrests#");
		abbreviations.put("vital","http://vital-iot.eu/ontology/ns/");
		abbreviations.put("sawsdl","http://www.w3.org/ns/sawsdl#");
		abbreviations.put("openiot","http://openiot.eu/ontology/ns/");

	}
	/**
	 * compares two Strings to verify if they are equals considering both expanded and compacted version of ontology
	 *
	 * @param primary first object used for the equals method. Same as {@code this} in standard equals
	 * @param comparison second object used for the equals
	 * @return {@code true} if the two strings are equivalent
	 */
	public static boolean equals(String primary, String comparison){

		// verify that both are not null
		if(primary==null || comparison==null){
			return false;
		}

		String expandedPrimary=new String(primary);
		String expandedComparison=new String(comparison);

		// if one of the input is compacted transform in expanded version
		if(isCompacted(primary)){
			expandedPrimary=expand(primary);
		}

		if(isCompacted(comparison)){
			expandedComparison=expand(comparison);
		}

		// compare both expanded version
		return 	expandedPrimary.equals(expandedComparison);


	}

	/**
	 * Verifies if a string represents an ontology value in expanded version
	 *
	 * @param expanded value to verify
	 * @return {@code true} if the received string is expanded
	 */
	public static boolean isExpanded(String expanded){
		if(expanded==null)
			return false;
		for(String s:abbreviations.values()){
			if(expanded.contains(s))
				return true;
		}

		return false;
	}


	/**
	 * Verifies if a string represents an ontology value in compacted version
	 *
	 * @param value value to verify
	 * @return {@code true} if the received string is compacted
	 */
	public static boolean isCompacted(String value){


		int index=value.indexOf(":");
		if(index<0){
			return false;
		}
		String token=value.substring(0, index);

		// since all expanded versions start with http, it is compacted only if char next to : is different from /
		if(token.equals("http")){
			if(value.charAt(index+1)=='/'){
				return false;
			}
		}

		if(abbreviations.containsKey(token)){
			return true;
		}

		return false;
	}


	/**
	 * Produce an expanded version of received string. If received string is not compacted returned value
	 * is same as input
	 * @param param String to expand
	 * @return Expanded string. Same as input if non valid compacted input
	 */
	public static String expand(String param){
		if(isCompacted(param)){
			int index=param.indexOf(":");
			String prefix=param.substring(0, index);
			String value=param.substring(index+1, param.length());
			String expanded=abbreviations.get(prefix)+value;
			return expanded;
		}
		else
			return param;
	}


	/**
	 * Produce a compacted version of received string. If received string is not expanded returned value
	 * is same as input
	 *
	 * @param expanded string to compact
	 * @return Compacted string. Same as input if non valid expanded input
	 */
	public static String compress(String expanded){
		if(isExpanded(expanded)){

			for(String s:abbreviations.keySet()){
				if(expanded.contains(abbreviations.get(s))){

					String compressed=s+":"+expanded.replace(abbreviations.get(s), "");
					return compressed;
				}
			}

		}

		return expanded;

	}


}

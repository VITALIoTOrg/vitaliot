/**
* @Author: Riccardo Petrolo <riccardo>
* @Date:   2016-02-26T09:52:37+01:00
* @Email:  riccardo.petrolo@inria.fr
* @Last modified by:   riccardo
* @Last modified time: 2016-03-30T18:26:52+02:00
*/



package eu.vital.discoverer.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DiscoveryMetadataReader {


	public enum MetadataSelector {
		SYSTEM_METADATA("/sys_metadata.jsonld"),
		SERVICE_METADATA("/ser_metadata.jsonld"),
		SENSOR_METADATA("/sen_metadata.jsonld"),
		STATUS_METADATA("/status.jsonld");

	    private final String name;

	    private MetadataSelector(String s) {
	        name = s;
	    }

	    public String getName() {
	       return this.name;
	    }
	}

	public String getMetadata(MetadataSelector selector) throws IOException{
		InputStream fis=this.getClass().getResourceAsStream(selector.getName());
		StringBuilder sb=new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
		String line;
		while((line=reader.readLine())!=null){
			sb.append(line);
		}

		return sb.toString();
	}

}

/**
* @Author: Riccardo Petrolo <riccardo>
* @Date:   2016-03-30T17:37:24+02:00
* @Email:  riccardo.petrolo@inria.fr
* @Last modified by:   riccardo
* @Last modified time: 2016-03-30T18:26:51+02:00
*/



package eu.vital.discoverer.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.jboss.resteasy.logging.Logger;


public class DiscoveryProperties {

	private static final String PROPERTIES_FILENAME="/vital-properties.xml";

	public static enum DiscoveryProperty{
		DMS_ENDPOINT_ADDRESS,
		DMS_ENDPOINT_PORT,
		DISCOVERER_ENDPOINT_ADDRESS,
		DISCOVERER_ENDPOINT_PORT
	}

	private Properties properties;

	final static Logger logger=Logger.getLogger(DiscoveryProperties.class);

	public DiscoveryProperties() {
		properties=new Properties();

		String fileName = System.getProperty("jboss.server.config.dir") + PROPERTIES_FILENAME;
		File propertyFile=new File(fileName);
		if(propertyFile.exists()){
			logger.debug("Local configuration file found");
			try {
				properties.loadFromXML(new FileInputStream(propertyFile));
				logger.info("Contained Keys\n"+properties.keySet());
			}
			catch (IOException e) {
				logger.error("Unable to load Properties", e);
			}
		}
		else{
			logger.info("Loading default configuration file");
			try {
				properties.loadFromXML(this.getClass().getResourceAsStream(PROPERTIES_FILENAME));
			} catch (IOException e) {
				logger.error("Unable to load Properties", e);
			}
		}
	}

	public String getProperty(DiscoveryProperty property){
		return properties.getProperty(property.toString());
	}


}

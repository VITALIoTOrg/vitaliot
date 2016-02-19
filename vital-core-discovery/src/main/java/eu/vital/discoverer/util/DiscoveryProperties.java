package eu.vital.discoverer.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.jboss.resteasy.logging.Logger;


public class DiscoveryProperties {

	private static final String PROPERTIES_FILENAME="/discovery.properties";
	
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
				properties.load(new FileInputStream(propertyFile));
				logger.info("Contained Keys\n"+properties.keySet());
			} 
			catch (IOException e) {
				logger.error("Unable to load Properties", e);
			}
		}
		else{
			logger.info("Loading default configuration file");
			try {
				properties.load(this.getClass().getResourceAsStream(PROPERTIES_FILENAME));
			} catch (IOException e) {
				logger.error("Unable to load Properties", e);
			}
		}
	}
	
	public String getProperty(DiscoveryProperty property){
		return properties.getProperty(property.toString());
	}


}

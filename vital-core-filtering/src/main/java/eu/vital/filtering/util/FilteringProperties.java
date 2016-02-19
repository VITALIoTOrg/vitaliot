package eu.vital.filtering.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.jboss.resteasy.logging.Logger;

public class FilteringProperties {

	private static final String PROPERTIES_FILENAME="/filtering.properties";
	
	public static enum FilteringProperty{
		DMS_ENDPOINT_ADDRESS,
		DMS_ENDPOINT_PORT,
		FILTERING_ENDPOINT_ADDRESS,
		FILTERING_ENDPOINT_PORT
	}
	
	private Properties properties;

	final static Logger logger=Logger.getLogger(FilteringProperties.class);

	public FilteringProperties() {
		properties=new Properties();

		String fileName = System.getProperty("jboss.server.config.dir") + PROPERTIES_FILENAME;
		File propertyFile=new File(fileName);
		if(propertyFile.exists()){
			logger.info("Local configuration file found");
			try {
				properties.load(new FileInputStream(propertyFile));
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


	public String getProperty(FilteringProperty property){
		return properties.getProperty(property.toString());
	}

}

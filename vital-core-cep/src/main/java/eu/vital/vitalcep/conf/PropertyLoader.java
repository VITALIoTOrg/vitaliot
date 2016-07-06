package eu.vital.vitalcep.conf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 * Loads properties from a file
 * 
 * @author a601149
 *
 */
public class PropertyLoader {

    private final Map<String, String> properties;

    public PropertyLoader() throws IOException {
        
        Logger logger = Logger.getLogger(this.getClass().getName());

        this.properties = new HashMap<>();
        final Properties props = new Properties();

        String PROPERTIES_FILE = "/cep.properties";
        
        String fileName = System.getProperty("jboss.server.config.dir") 
                +  PROPERTIES_FILE;
        File propertyFile=new File(fileName);
        if(propertyFile.exists()){
                try {
                    props.load(new FileInputStream(System
                    .getProperty("jboss.server.config.dir")
                    +PROPERTIES_FILE));
                        
                } 
                catch (IOException e) {
                        logger.error("Property file is not available", e);
                }
        }
        else{
                try {
                    props.load(this.getClass().getResourceAsStream(PROPERTIES_FILE));
                } catch (IOException e) {
                        logger.error("Unable to find and load Properties", e);
                }
        }
        
        for (final String name : props.stringPropertyNames())
                        this.properties.put(name, props.getProperty(name));

       

    }

    /**
     * Gets the value of the property with the specified name.
     * 
     * @param name the name.
     * @return the value of the property with the specified name.
     */
    public String getProperty(String name) {
            return properties.get(name);
    }
}
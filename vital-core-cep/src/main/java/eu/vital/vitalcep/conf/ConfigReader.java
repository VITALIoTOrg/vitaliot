/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.vital.vitalcep.conf;

import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;

public class ConfigReader {
    
    private static ConfigReader instance;
    private Properties config;
    
    public static final String AUTH_URL = "vital-core-cep.security";
    public static final String MONGO_URL = "vital-core-cep.mongo.uri";
    public static final String MONGO_DB = "vital-core-cep.mongo.db";
    public static final String IOTDA_URL = "vital-core-cep.iotda";
    public static final String CEP_BASE_URL = "vital-core-cep-ppi.base-url";
    public static final String MSQUITTO_URL = "vital-core-cep.mosquitto.uri";
    public static final String UCEP_PATH = "vital-core-cep.ucep.path";
    public static final String DMS_URL = "vital-core-cep.dms";
    public static final String CEP_CONF_FILE = "vital-core-cep.configuraiong.file";
      
    private ConfigReader() {
        String fileName = System.getProperty("jboss.server.config.dir") + "/vital-properties.xml";
        File file = new File(fileName);

        config = new Properties();
        
        try {
            config.loadFromXML(new FileInputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static ConfigReader getInstance() {
        if (instance == null) {
            instance = new ConfigReader();
        }
        return instance;
    }
    
    public String get(String key) {
        return config.getProperty(key);
    }
}

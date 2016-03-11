package utils;

import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;

public class ConfigReader {
    private static ConfigReader instance;
    private Properties config;

    public static final String PROXY_HOST = "vital-core-security-gateway.proxy-host";
    public static final String PROXY_PORT = "vital-core-security-gateway.proxy-port";
    public static final String PROXY_PPI_PATH = "vital-core-security-gateway.proxy-ppi-path";
    public static final String SECURITY_HOST = "vital-core-security-gateway.security-host";
    public static final String SECURITY_PORT = "vital-core-security-gateway.security-port";
    public static final String USERNAME = "vital-core-security-gateway.username";
    public static final String PASSWORD = "vital-core-security-gateway.password";

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


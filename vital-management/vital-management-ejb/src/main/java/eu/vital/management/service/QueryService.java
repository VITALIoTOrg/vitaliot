package eu.vital.management.service;

import com.fasterxml.jackson.databind.JsonNode;

import eu.vital.management.util.VitalClient;
import eu.vital.management.util.VitalConfiguration;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class QueryService {

    @Inject
    private Logger log;

    @Inject
    private VitalClient vitalClient;

    @Inject
    private VitalConfiguration configuration;

    public JsonNode query(String resourceType, JsonNode query, boolean encodeKeys) throws Exception {

        String dmsUrl = configuration.getProperty("vital-management.dms", "http://localhost:8080/");

        if (resourceType.equalsIgnoreCase("system")) {
            dmsUrl += "/querySystem";
        } else if (resourceType.equalsIgnoreCase("service")) {
            dmsUrl += "/queryService";
        } else if (resourceType.equalsIgnoreCase("sensor")) {
            dmsUrl += "/querySensor";
        } else if (resourceType.equalsIgnoreCase("observation")) {
            dmsUrl += "/queryObservation";
        }
        dmsUrl += "?encodeKeys=" + encodeKeys;

        // Connect to this URL and fetch all sensors:
        JsonNode result = vitalClient.doPost(dmsUrl, query);
        return result;
    }

}

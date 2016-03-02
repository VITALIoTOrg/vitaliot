/* 
 *	CEP REST Interface
 *	Copyright (c) Atos S.A.
 *	Research & Innovation - Internet of Everything Lab
 *	All Rights Reserved.
 *	
 *	ATOS PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
*/
package eu.vital.vitalcep.entities.dolceHandler;


import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import eu.vital.vitalcep.entities.dolceHandler.statements.ComplexStatement;
import eu.vital.vitalcep.entities.dolceHandler.statements.DolceStatement;
import eu.vital.vitalcep.entities.dolceHandler.statements.DolceStatementFactory;
import eu.vital.vitalcep.entities.dolceHandler.statements.EventStatement;
import eu.vital.vitalcep.entities.dolceHandler.statements.ExternalDolceStatement;

// TODO: Auto-generated Javadoc
/**
 * The Class DolceSpecification.
 * 
 * This Class is a dolce specification container
 * 
 */
public class DolceSpecification {

    /** The Constant logger. */
    final static Logger logger = Logger.getLogger(DolceSpecification.class);

    /** The id. */
    private String id;

    /** The external hm. */
    private HashMap<String, ExternalDolceStatement> externalHM = new HashMap<>();

    /** The event hm. */
    private HashMap<String, EventStatement> eventHM = new HashMap<>();

    /** The complex hm. */
    private HashMap<String, ComplexStatement> complexHM = new HashMap<>();

    // constructor form json as String
    /**
     * Instantiates a new dolce specification.
     *
     * @param json the json in string format
     */
    public DolceSpecification(String json) {
        JSONObject jo = new JSONObject(json);

        //JSONObject dsjo = jo.getJSONObject("dolceSpecification");

        // get id
        id = jo.getString("id");

        // get externals
        if (jo.has("external")){
            JSONArray extJA = jo.getJSONArray("external");
            for (int i = 0; i < extJA.length(); i++) {
                ExternalDolceStatement extDS = new ExternalDolceStatement(
                                (JSONObject) (extJA.get(i)));
                externalHM.put(extDS.id, extDS);
            }
        }

        // get events
        if (jo.has("event")){
            JSONArray eventJA = jo.getJSONArray("event");
            for (int i = 0; i < eventJA.length(); i++) {
                EventStatement evtDS = new EventStatement(
                                (JSONObject) (eventJA.get(i)));
                eventHM.put(evtDS.id, evtDS);
            }
        }

        // getComplexs
        if (jo.has("complex")){
            JSONArray compJA = jo.getJSONArray("complex");
            for (int i = 0; i < compJA.length(); i++) {
                ComplexStatement compDS = new ComplexStatement(
                                (JSONObject) (compJA.get(i)));
                complexHM.put(compDS.id, compDS);
            }
        }
    }


    public String toString() {

        StringBuilder sb = new StringBuilder();

        Iterator<String> et = externalHM.keySet().iterator();

        if (et.hasNext()){
            sb.append("\n//    EXTERNALS   \n");
        }

        while (et.hasNext()) {
            ExternalDolceStatement ds = externalHM.get(et.next());
            // sb.append("// External: " + ds.id + "\n");
            sb.append(ds.toPlainTextString()).append("\n");
        }

        Iterator<String> evt = eventHM.keySet().iterator();

        if (evt.hasNext()){
            sb.append("\n\n//    SIMPLE EVENTS   \n");
        }

        while (evt.hasNext()) {
            EventStatement ds = eventHM.get(evt.next());
            // sb.append("// Simple Event: " + ds.id + "\n");
            sb.append(ds.toPlainTextString()).append("\n");
        }

        Iterator<String> cp = complexHM.keySet().iterator();

        if (cp.hasNext()){
            sb.append("\n\n//    COMPLEX EVENTS   \n");
        }

        while (cp.hasNext()) {
            ComplexStatement ds = complexHM.get(cp.next());
            // sb.append("// Complex Event: " + ds.id + "\n");
            sb.append(ds.toPlainTextString()).append("\n");
        }

        return sb.toString();
    }

    // return a JSONObject with the Dolce Specification
    /**
     * Gets the dolce specification as json obj.
     *
     * @return the json obj
     */
    public JSONObject getJsonObj() {

        JSONArray externalsJA = new JSONArray();
        JSONArray eventsJA = new JSONArray();
        JSONArray complexJA = new JSONArray();
        boolean external = false;
        boolean event = false;
        boolean complex = false;

        if (externalHM.size() > 0) {
            Iterator<String> it = externalHM.keySet().iterator();
            external = true;
            while (it.hasNext()) {
                    JSONObject jo;
                    ExternalDolceStatement es = externalHM.get(it.next());
                    jo = es.toJson();
                    externalsJA.put(jo);
            }
        }

        if (eventHM.size() > 0) {
            Iterator<String> it = eventHM.keySet().iterator();
            event = true;
            while (it.hasNext()) {
                    JSONObject jo;
                    EventStatement es = eventHM.get(it.next());
                    jo = es.toJson();
                    eventsJA.put(jo);
            }
        }

        if (complexHM.size() > 0) {
            Iterator<String> it = complexHM.keySet().iterator();
            complex = true;
            while (it.hasNext()) {
                    JSONObject jo;
                    ComplexStatement cs = complexHM.get(it.next());
                    jo = cs.toJson();
                    complexJA.put(jo);
            }
        }

        JSONObject mainObject = new JSONObject();
        JSONObject dsObj = new JSONObject();

        dsObj.put("id", id);

        if (external)
            dsObj.put("external", externalsJA);
        if (event)
            dsObj.put("event", eventsJA);
        if (complex)
            dsObj.put("complex", complexJA);

        mainObject.put("dolceSpecification", dsObj);

        return mainObject;
    }

}

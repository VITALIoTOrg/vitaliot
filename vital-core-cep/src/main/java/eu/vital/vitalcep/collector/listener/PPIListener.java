/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.vital.vitalcep.collector.listener;

import eu.vital.vitalcep.connectors.ppi.PPIManager;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author a601149
 */
public class PPIListener {
    
    //private final String ppiURL;
    private final String cookie;
    
    public PPIListener (String cookie)
            
        throws FileNotFoundException, IOException{
        this.cookie= cookie; 
                
    }
    
    public JSONArray getObservations(JSONArray requests,
            String from ) throws IOException, UnsupportedEncodingException,
            KeyManagementException, NoSuchAlgorithmException, KeyStoreException{

        JSONArray aData = new JSONArray();
            
        for (int j = 0; j < requests.length(); j++) {
            
            try{
                JSONObject simplequery = new JSONObject();

                simplequery = requests.getJSONObject(j)
                        .getJSONObject("body").put("from",from);

                PPIManager oPPI = new PPIManager(cookie);

                aData = oPPI.getObservations(requests.getJSONObject(j)
                        .getString("ppiURL"),simplequery
                        .toString());
            }catch(JSONException | IOException | KeyManagementException 
                    | NoSuchAlgorithmException | KeyStoreException ex){
                java.util.logging.Logger.getLogger(PPIListener
                                    .class.getName())
                                        .log(Level.SEVERE, null, ex);
            }
        }
       
        return aData;
    
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.vital.vitalcep.collector.listener;

import eu.vital.vitalcep.conf.PropertyLoader;
import eu.vital.vitalcep.connectors.ppi.PPIManager;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author a601149
 */
public class PPIListener {
    
    private final  PropertyLoader props;
    //private final String ppiURL;
    private final String cookie;
    
    public PPIListener (String cookie)
            
        throws FileNotFoundException, IOException{
        
        Logger logger = Logger.getLogger(this.getClass().getName());
        
        this.props = new PropertyLoader();
        //this.ppiURL = props.getProperty("dms.base_url");
        this.cookie= cookie; 
                
    }
    
    public JSONArray getObservations(JSONArray requests,
            String from ) throws IOException, UnsupportedEncodingException,
            KeyManagementException, NoSuchAlgorithmException, KeyStoreException{

        JSONObject completequery = new JSONObject();

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
            }catch(Exception ex){
                java.util.logging.Logger.getLogger(PPIListener
                                    .class.getName())
                                        .log(Level.SEVERE, null, ex);
            }
        }
       
        return aData;
    
    }
    
    private String getXSDDateTime(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        return  dateFormat.format(date);
    }

}

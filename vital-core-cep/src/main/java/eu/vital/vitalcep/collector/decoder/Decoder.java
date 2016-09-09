/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.vital.vitalcep.collector.decoder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author a601149
 */
public class Decoder {
    
    final static Logger logger = Logger.getLogger(Decoder.class);
    
      /**
     * Transform the observations in JSONLD array into dolce inputs
     *
     * @param input the json array
     * @return  the dolce input string 
     * @throws java.text.ParseException 
     */
     public  ArrayList<String> JsonldArray2DolceInput
        (JSONArray input) throws ParseException {
        
        ArrayList<String> dolceInputs = new ArrayList<>();
        
        for(int i = 0; i < input.length(); i++){
            
            String dolceInput;
            
            String number = "1";

            // get sensor
            String id = input.getJSONObject(i).getString("ssn:observedBy");

            String oType = input.getJSONObject(i)
                    .getJSONObject("ssn:observationProperty")
                    .getString("type");

            String observationType="";
            if (oType.contains(":")) {
                String[] vect = oType.split(":");
                 observationType= vect[vect.length-1];
            } else if (oType.contains("#")) {
                String[] vect = oType.split("#");
                observationType = vect[vect.length-1];
            }else{
                observationType =oType; 
            }

            JSONObject oResoult = input.getJSONObject(i)
                    .getJSONObject("ssn:observationResult");

            Object aObj = oResoult.getJSONObject("ssn:hasValue")
                    .get("value");
           
            String stringValue = "";
            
            if(aObj instanceof Integer){
                int value = oResoult.getJSONObject("ssn:hasValue")
                    .getInt("value");
                stringValue = "int value "+ Integer.toString(value);
            }
            
            if(aObj instanceof Double){
                Double value = oResoult.getJSONObject("ssn:hasValue")
                    .getDouble("value");
                stringValue = "float value "+Double.toString(value);
            }
            
            if(aObj instanceof String){
                String value = oResoult.getJSONObject("ssn:hasValue")
                    .getString("value");
                stringValue = "string value "+ value;
            }
            
            if(aObj instanceof Boolean){
                Boolean value = oResoult.getJSONObject("ssn:hasValue")
                    .getBoolean("value");
                stringValue = "int " + (value ? "1": "0");
            }
            
            if(aObj instanceof JSONObject ){
                JSONObject value = oResoult.getJSONObject("ssn:hasValue")
                    .getJSONObject("value");
                stringValue = "string value "+value.toString();
            }
            
            if(aObj instanceof JSONArray ){
                JSONArray value = oResoult.getJSONObject("ssn:hasValue")
                    .getJSONArray("value");
                stringValue = "string value "+value.toString();
            }
            
            String location = "";
            if (input.getJSONObject(i)
                    .has("dul:hasLocation")){
            
                 JSONObject oLoc = input.getJSONObject(i)
                    .getJSONObject("dul:hasLocation");

                Double glat = oLoc.getDouble("geo:lat");
                Double glong = oLoc.getDouble("geo:long");
            
                location ="pos location "
                        +glong.toString()
                        +"\\"+glat.toString();
            }
            
            String observationResultTime ="";

            if (input.getJSONObject(i)
                    .has("ssn:observationResultTime")){
            
                String oTime = input.getJSONObject(i)
                    .getJSONObject("ssn:observationResultTime")
                         .getString("time:inXSDDateTime");
            
                String time = getDolceDateTime(oTime);
                //observationResultTime = "time observationTime "
                //    + oTime ;
                observationResultTime = "time observationTime "
                   + time ;
                
            }else {
                Date NOW = new Date();
                observationResultTime = "time observationTime "
                    + getDolceDateTime(getXSDDateTime(NOW) );
            }
            
           

            dolceInput = number +" "+observationType
                + " " + location
                +" string id "+ id 
                + " "+ stringValue 
                +" "+ observationResultTime;
               // +" "+" int value "+(long)Math.floor(value + 0.5d);
            dolceInputs.add(dolceInput);
            
        }
        
        return dolceInputs;
      
    }
    
    private String getDolceDateTime(String observationTime) throws ParseException {
        
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd'@'HH:mm:ss");
             
        Date date = input.parse(observationTime);
        output.setTimeZone(TimeZone.getTimeZone("UTC"));
        
        return  output.format(date);
    }
    
    private String getXSDDateTime(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return  dateFormat.format(date);
    }

}

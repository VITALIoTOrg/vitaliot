/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.vital.vitalcep.publisher.encoder;
import com.mongodb.BasicDBList;
import com.mongodb.util.JSON;
import eu.vital.vitalcep.connectors.mqtt.MqttMsg;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author a601149
 */
public class Encoder {
    
     /**
     * Transform the dolce output into vital observations in jsonld 
     *
     * @param input the json 
     * @param id 
     * @param sensor 
     * @param observationTime ***see whether to put it
     * @return  the dolce input string 
     */
    public JSONObject dolceOutput2Jsonld(String input,String id, 
            String sensor, String observationTime) {

            JSONObject outputObservation = new JSONObject();
            
            String[] values = input.split(" ");
            
            String valueEvent= null;
            String locationEvent= null;
            String timeEvent= null;
            String idEvent= null;
            String complexEvent= null;
            
            Boolean hasLoc = false;
            
            complexEvent = values[1];
            
            JSONArray payload = new JSONArray();
                     
            for (int z=3;z<values.length;z++){
                JSONObject payloadLine = new JSONObject();
                String token = values[z];
                if (token.compareToIgnoreCase("SensorId")==0)
                   idEvent=values[z+1];
                if (token.compareToIgnoreCase("Position")==0)
                    {   locationEvent = values [z+1];
                    hasLoc = true;}
                      
                else if (token.compareToIgnoreCase("Time")==0)
                    timeEvent = values [z+1];
                //from observationTime
                
                else if ((z % 3)==0) {
                    valueEvent = values [z+1];
               
                    payloadLine.put("dataType",values [z-1]);
                    payloadLine.put("name",values [z]);
                    payloadLine.put("value",values [z+1]);

                    payload.put(payloadLine); 
                }
            }           
            
            outputObservation.put("@context",
                    "http://vital-iot.eu/contexts/measurement.jsonld");
            
            outputObservation.put("id",sensor+"/observation/"+id );
            
            outputObservation.put("type","ssn:Observation");
            
            outputObservation.put("ssn:observedBy",sensor);
            
            JSONObject property = new JSONObject();
            property.put("type","vital:ComplexEvent");
            outputObservation.put("ssn:observationProperty",property);
            
            JSONObject resultTime = new JSONObject();
            resultTime.put("time:inXSDDateTime",timeEvent);//check format
            outputObservation.put("ssn:observationResultTime",resultTime);
                //"time:inXSDDateTime": "2015-10-14T11:59:11+02:00"
            
            JSONObject hasValue = new JSONObject();
            hasValue.put( "type","ssn:ObservationValue");
            
            JSONObject valuex = new JSONObject();
            JSONObject value = new JSONObject();
            value.put("complexEvent",complexEvent);
            value.put("payload",payload);
            
           /* busca localization*/
            if (hasLoc){
                String[] aLoc = locationEvent.split("\\\\");  //de Elisa values[10].split("\\");
            
                JSONObject loc = new JSONObject();
                loc.put("type","geo:Point");
                loc.put("geo:lat",aLoc[1]);//ver
                loc.put("geo:long",aLoc[0]);//ver
                value.put("dul:hasLocation",loc);
            }

            
            /* busca el value*/
            JSONObject speedObs = new JSONObject();
            speedObs.put("type","ssn:SensorOutput");
            JSONObject speedValue = new JSONObject();
            speedValue.put("type","ssn:ObservationValue");
            speedValue.put("value",valueEvent);   //de Elisavalues[5]);
            speedValue.put("qudt:unit","qudt:KilometerPerHour");
            
            speedObs.put("ssn:hasValue",speedValue);
           
            //value.put("ssn:ObservationResult",speedObs);
            //ver si falta algo
            
            value.put("ssn:observedBy",idEvent);
            
            valuex.put("value",value);
            valuex.put("type","ssn:ObservationValue");

            JSONObject observationResult = new JSONObject();
            observationResult.put("ssn:hasValue",valuex);
            observationResult.put("type","ssn:SensorOutput");
            outputObservation.put("ssn:observationResult",observationResult);
           
        return outputObservation;
           
    }

    public JSONArray dolceOutputList2JsonldArray(ArrayList<MqttMsg> mesagges, 
            String hostnameport, String randomUUIDString) {
        
        JSONArray aOutput = new JSONArray();
        
        for (int i = 0; i < mesagges.size(); i++) {
            UUID uuidobservs = UUID.randomUUID();
            String randomObservsUUIDString = uuidobservs.toString();
            String sOutput = mesagges.get(i).msg.replace("\r", "")
                    .replace("\n", "");
            Date date = new Date();
           
            JSONObject item = this.dolceOutput2Jsonld(sOutput,
                    randomObservsUUIDString, hostnameport
                    + "/sensor/" 
                    + randomUUIDString
                    , this.getXSDDateTime(date));
            aOutput.put(item);
        }
        return aOutput;
    }
    
    /**
     *
     * @param mesagges
     * @param hostnameport
     * @param randomUUIDString
     * @return
     */
    public ArrayList<Document> dolceOutputList2ListDBObject(ArrayList<MqttMsg> mesagges, 
            String hostnameport, String randomUUIDString) {
        
        ArrayList<Document> documents ;
        documents = new ArrayList<Document>() {};
        
        for (int i = 0; i < mesagges.size(); i++) {
            UUID uuidobservs = UUID.randomUUID();
            String randomObservsUUIDString = uuidobservs.toString();
            String sOutput = mesagges.get(i).msg.replace("\r", "")
                    .replace("\n", "");
            Date date = new Date();
                                  
           
            Document item = this.dolceOutput2Document(sOutput,
                    randomObservsUUIDString, hostnameport 
                    + "/sensor/" 
                    + randomUUIDString
                    , this.getXSDDateTime(date));
            documents.add(item);
        }
        return documents;
    }
    
    private String getXSDDateTime(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return  dateFormat.format(date);
    }
    
        public Document dolceOutput2Document(String input,String id, 
            String sensor, String observationTime) {

            Document outputObservation = new Document();
            
            String[] values = input.split(" ");
            
            String valueEvent= null;
            String locationEvent= null;
            String timeEvent= null;
            String idEvent= null;
            String complexEvent= null;
            
            Boolean hasLoc = false;
            
            complexEvent = values[1];
            
            BasicDBList payloadBDBList = new BasicDBList();
            
       // payload = new ArrayList<Document>() {};
                     
            for (int z=3;z<values.length;z++){
                Document payloadLine = new Document();
                String token = values[z];
                if (token.compareToIgnoreCase("SensorId")==0){
                    idEvent=values[z+1];
                    payloadLine.put("dataType","string");
                    payloadLine.put("name",values [z]);
                    payloadLine.put("value",values [z+1]);

                    payloadBDBList.add(payloadLine); 
                }else if (token.compareToIgnoreCase("Position")==0)
                    {   locationEvent = values [z+1];
                    payloadLine.put("dataType","pos");
                    payloadLine.put("name",values [z]);
                    payloadLine.put("value",values [z+1]);

                    payloadBDBList.add(payloadLine); 
                    hasLoc = true;}
                      
                else if (token.compareToIgnoreCase("Time")==0){
                    
                    timeEvent = values [z+1];
                    payloadLine.put("dataType","time");
                    payloadLine.put("name",values [z]);
                    payloadLine.put("value",values [z+1]);

                    payloadBDBList.add(payloadLine);
                //from observationTime
                
                }else if ((z % 3)==0) {
                    valueEvent = values [z+1];
               
                    payloadLine.put("dataType",values [z-1]);
                    payloadLine.put("name",values [z]);
                    payloadLine.put("value",values [z+1]);

                    payloadBDBList.add(payloadLine); 
                }
            }           
            
            outputObservation.put("@context",
                    "http://vital-iot.eu/contexts/measurement.jsonld");
            
            outputObservation.put("id",sensor+"/observation/"+id );
            
            outputObservation.put("type","ssn:Observation");
            
            outputObservation.put("ssn:observedBy",sensor);
            
            Document property = new Document();
            property.put("type","vital:ComplexEvent");
            outputObservation.put("ssn:observationProperty",property);
            
            Document resultTime = new Document();
            if (timeEvent!=null){
                resultTime.put("time:inXSDDateTime",timeEvent);//check format
            }else{
                resultTime.put("time:inXSDDateTime",observationTime);
            }
            outputObservation.put("ssn:observationResultTime",resultTime);
                //"time:inXSDDateTime": "2015-10-14T11:59:11+02:00"
            
            Document hasValue = new Document();
            hasValue.put( "type","ssn:ObservationValue");
            
            Document valuex = new Document();
            Document value = new Document();
            value.put("complexEvent",complexEvent);
            value.put("payload",payloadBDBList);
            
           /* busca localization*/
            if (hasLoc){
                String[] aLoc = locationEvent.split("\\\\");  //de Elisa values[10].split("\\");
            
                Document loc = new Document();
                loc.put("type","geo:Point");
                loc.put("geo:lat",aLoc[1]);//ver
                loc.put("geo:long",aLoc[0]);//ver
                value.put("dul:hasLocation",loc);
            }

            
            /* busca el value*/
            Document speedObs = new Document();
            speedObs.put("type","ssn:SensorOutput");
            Document speedValue = new Document();
            speedValue.put("type","ssn:ObservationValue");
            speedValue.put("value",valueEvent);   //de Elisavalues[5]);
            speedValue.put("qudt:unit","qudt:KilometerPerHour");
            
            speedObs.put("ssn:hasValue",speedValue);
           
            //value.put("ssn:ObservationResult",speedObs);
            //ver si falta algo
            
            value.put("ssn:observedBy",idEvent);
            
            valuex.put("value",value);
            valuex.put("type","ssn:ObservationValue");

            Document observationResult = new Document();
            observationResult.put("ssn:hasValue",valuex);
            observationResult.put("type","ssn:SensorOutput");
            outputObservation.put("ssn:observationResult",observationResult);
           
        return outputObservation;
           
    }

}

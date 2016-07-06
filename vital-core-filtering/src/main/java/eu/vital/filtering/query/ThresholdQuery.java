package eu.vital.filtering.query;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.vital.filtering.exception.WrongInputException;
import eu.vital.filtering.inputJSON.Threshold_JSON_Object;
import eu.vital.filtering.inputJSON.Threshold_JSON_Object.INEQUALITY_TYPE;
import eu.vital.filtering.util.DMSManager;
import eu.vital.filtering.util.GPSPoint;
import eu.vital.filtering.util.MapSelectionSquare;
import eu.vital.filtering.util.DMSManager.DMS_Index;

public class ThresholdQuery extends FilteringQuery {

	final static Logger logger=Logger.getLogger(ThresholdQuery.class);
	private Threshold_JSON_Object inputObject;


	@Override
	public void setInputJSON(JSONObject inputJSON) {

		ObjectMapper mapper=new ObjectMapper();
		if(inputJSON!=null){

			try {
				inputObject=mapper.readValue(inputJSON.toJSONString(), Threshold_JSON_Object.class);
			} catch (JsonParseException e) {
				logger.error("Error unmarshaling input JSON object",e);
				throw new WrongInputException("Wrong JSON object format");
			} catch (JsonMappingException e) {
				logger.error("Error mapping input JSON object",e);
				throw new WrongInputException("Wrong JSON object format");				
			} catch (IOException e) {
				logger.error("IO Error on input JSON object",e);
				throw new WrongInputException("Wrong JSON object format");
			} catch (Exception e){
				logger.error("Exception in JSON to POJO convertion", e);
				throw new WrongInputException("Wrong JSON object format");
			}

			inputObject.setIncludedKeys(inputJSON);
			this.inputJSON=inputJSON;
		}
		else{
			throw new WrongInputException("Wrong JSON object format");
		}
	}

	@Override
	public void executeQuery() {

		DMSManager manager=new DMSManager(DMS_Index.OBSERVATION, this.cookie);

		//Extract time range from request
		Date from= this.inputObject.hasFrom() ? this.inputObject.getFrom() : null;
		Date to=null;
		if(from!=null && this.inputObject.hasTo())
			to=this.inputObject.getTo();
		if(from!=null && !this.inputObject.hasTo())
			to=new Date();

		try {

			if(this.inputObject.hasIco()){
				logger.debug("Query threshold by ICO");
				LinkedList<JSONObject> queryObservations=manager.getObservationsById(this.inputObject.getIco(), this.inputObject.getObeservationProperty(),from, to, this.inputObject.getInequality(), this.inputObject.getValue());
				logger.debug("Observations Number:"+queryObservations.size());
				this.result_in_JSON=fitlerByInequality(queryObservations, this.inputObject.getInequality(), this.inputObject.getValue());

			}
			if(this.inputObject.hasPosition()){
				logger.debug("Query threshold by position");
				MapSelectionSquare queryArea=new MapSelectionSquare(new GPSPoint(this.inputObject.getPosition().getLatitude(), this.inputObject.getPosition().getLongitude()), this.inputObject.getPosition().getRadius());
				LinkedList<JSONObject> queryObservations=manager.getObservationsByPosition(queryArea, this.inputObject.getObeservationProperty(),from, to, this.inputObject.getInequality(), this.inputObject.getValue());
				logger.debug("Observations number: "+queryObservations.size());
				this.result_in_JSON=fitlerByInequality(queryObservations, this.inputObject.getInequality(), this.inputObject.getValue());
			}

		} catch (Exception e) {
			logger.error("Exception in ElasticSearch query", e);
		}

	}

	@Override
	public boolean isInputAppropriate() {
		if(this.inputJSON==null){
			return false;
		}
		else{
			if(inputJSON.keySet().size()==0){
				return false;
			}
			else{
				if(inputObject.hasIco()||inputObject.hasPosition()){
					return true;
				}else{
					return false;
				}

			}
		}
	}

	private static LinkedList<JSONObject> fitlerByInequality(LinkedList<JSONObject> input, INEQUALITY_TYPE inequality, double value){
		LinkedList<JSONObject> result=new LinkedList<JSONObject>();


		for(JSONObject obj:input){
			JSONObject observationResult=(JSONObject) obj.get("ssn:observationResult");
			JSONObject observationValue=(JSONObject) observationResult.get("ssn:hasValue");
			double measuredValue=Double.parseDouble(observationValue.get("value").toString());

			switch(inequality){
			case gt:
				if(measuredValue>value){
					result.add(obj);
				}
				break;
			case lt:
				if(measuredValue<value){
					result.add(obj);
				}
				break;
			case gte:
				if(measuredValue>=value){
					result.add(obj);
				}
				break;
			case lte:
				if(measuredValue<=value){
					result.add(obj);
				}
				break;

			}

		}
		return result;
	}




}

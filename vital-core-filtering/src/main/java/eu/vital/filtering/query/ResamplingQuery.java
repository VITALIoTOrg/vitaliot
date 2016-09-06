package eu.vital.filtering.query;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.vital.filtering.exception.WrongInputException;
import eu.vital.filtering.inputJSON.Resampling_JSON_Object;
import eu.vital.filtering.inputJSON.Resampling_JSON_Object.TIME_UNIT;
import eu.vital.filtering.util.DMSManager;
import eu.vital.filtering.util.DMSManager.DMS_Index;
import eu.vital.filtering.util.FilteringProperties;


public class ResamplingQuery extends FilteringQuery {

	final static Logger logger=Logger.getLogger(ResamplingQuery.class);
	private Resampling_JSON_Object inputObject;
	FilteringProperties props = new FilteringProperties();
	private final String OBSERVED_BY_RESULT=props.getProperty(FilteringProperties.FILTERING_ENDPOINT_ADDRESS)+":"+props.getProperty(FilteringProperties.FILTERING_ENDPOINT_PORT)+"/filtering";

	@Override
	public void setInputJSON(JSONObject inputJSON) {

		ObjectMapper mapper=new ObjectMapper();
		if(inputJSON!=null){

			try {
				inputObject=mapper.readValue(inputJSON.toJSONString(), Resampling_JSON_Object.class);
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

		DMSManager manager=new DMSManager(DMS_Index.OBSERVATION, cookie);

		try {
			LinkedList<JSONObject> result=manager.getObservationsInTimeRange(this.inputObject.getIco(),this.inputObject.getObservationProperty(), this.inputObject.getFrom(), this.inputObject.getTo());
			if(result.size()>0){
			double[][] data=extractData(result);
			rebaseTime(data);
			DateFormat formatter=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
			SplineInterpolator interpolator=new SplineInterpolator();
			PolynomialSplineFunction function=interpolator.interpolate(data[0], data[1]);

			// compute number of samples
			long samplingInterval=samplingIntervalToMillisecond(this.inputObject.getTimeValue(),this.inputObject.getTimeUnit());
			long samplesNumber=samplesNumber(this.inputObject.getFrom(), this.inputObject.getTo(), samplingInterval);

			Date start=this.inputObject.getFrom();
			JSONObject baseResult=result.getFirst();
			JSONParser parser=new JSONParser();

			for(long i=0;i<samplesNumber;i++){
				JSONObject newSample=(JSONObject)parser.parse(baseResult.toJSONString());
				Date newSampleTime=addTime(start, inputObject.getTimeValue(), inputObject.getTimeUnit());

				double newSamplingInstant=i*samplingIntervalToMillisecond(inputObject.getTimeValue(), inputObject.getTimeUnit());
				if(function.isValidPoint(newSamplingInstant)){
					double newSampleValue=function.value(newSamplingInstant);
					newSample.replace("ssn:observedBy", OBSERVED_BY_RESULT);
					newSample.replace("id", OBSERVED_BY_RESULT+"/"+(Long.toString(new Date().getTime())));
					JSONObject oldTime=(JSONObject)newSample.get("ssn:observationResultTime");
					oldTime.replace("time:inXSDDateTime", formatter.format(newSampleTime));
					JSONObject oldObservation=(JSONObject) newSample.get("ssn:observationResult");
					JSONObject oldValue=(JSONObject) oldObservation.get("ssn:hasValue");
					oldValue.replace("value", Math.round(newSampleValue));
					logger.debug("Current resampling instant: "+i*samplingIntervalToMillisecond(inputObject.getTimeValue(), inputObject.getTimeUnit()));
					this.result_in_JSON.addLast(newSample);

				}

				start=addTime(start, inputObject.getTimeValue(), inputObject.getTimeUnit());

			} //for

		}// if

		} catch (ParseException e) {
			e.printStackTrace();
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
					return true;
				}
		}
	}


	private Date addTime(Date start, int amount, TIME_UNIT unit){
		Calendar cal = Calendar.getInstance();
	    cal.setTime(start);
	    switch(unit){
	    case second:
	    	cal.add(Calendar.SECOND, amount);
	    	break;
	    case minute:
	    	cal.add(Calendar.MINUTE, amount);
	    	break;
	    case hour:
	    	cal.add(Calendar.HOUR_OF_DAY, amount);
	    	break;
	    case day:
	    	cal.add(Calendar.DAY_OF_YEAR, amount);
	    	break;
	    }

	   return cal.getTime();
	}

	private void rebaseTime(double[][] data){
		double base_time=data[0][0];
		for(int i=0;i<data[0].length;i++){
			data[0][i]=data[0][i]-base_time;

		}


	}


	private double[][] extractData(LinkedList<JSONObject> observations) {
		//raw[0] time
		//raw[1] value

		DateFormat formatter=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
		double[][] raw=new double[2][observations.size()];
		int i=0;
		for(JSONObject current:observations){

			//Rertieve Value
			if(current.containsKey("ssn:observationResult")){
				Map observationResult=(Map)current.get("ssn:observationResult");
				if(observationResult.containsKey("ssn:hasValue")){
					Map hasValue=(Map)observationResult.get("ssn:hasValue");
					if (hasValue.containsKey("value")){
						raw[1][i]= Double.parseDouble(hasValue.get("value").toString());
					}
				}
			}

			//Retrieve Time
			if(current.containsKey("ssn:observationResultTime")){
				Map observationResultTime=(Map)current.get("ssn:observationResultTime");
				if(observationResultTime.containsKey("time:inXSDDateTime")){
					Date instant;
					try {
						instant = formatter.parse((String)observationResultTime.get("time:inXSDDateTime"));
						raw[0][i]=instant.getTime();
					} catch (java.text.ParseException e) {
						e.printStackTrace();
					}

				}
			}

			i++;
		}

		return raw;
	}

	private long samplesNumber(Date from, Date to, long samplingInterval){

		long timeWindow=to.getTime()-from.getTime();
		long samples=Math.floorDiv(timeWindow, samplingInterval);
		return samples;
	}

	private long samplingIntervalToMillisecond(int timeValue, TIME_UNIT timeUnit){
		long toMillisecond=0;
		switch(timeUnit){
		case second:
			toMillisecond=1000;
			break;
		case minute:
			toMillisecond=60*1000;
			break;
		case hour:
			toMillisecond=60*60*1000;
			break;
		case day:
			toMillisecond=24*60*60*1000;
			break;
		default:

		}

		long interval=timeValue*toMillisecond;
		return interval;
	}


}

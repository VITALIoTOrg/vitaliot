package eu.vital.filtering.inputJSON;

import java.util.Date;

import org.json.simple.JSONObject;

public class Resampling_JSON_Object implements RequestJSONObjectInterface{

	// Enumeration for time units definition
	
	public enum TIME_UNIT{
		second,
		minute,
		hour,
		day;
	}
	
	// Instance variables
	
	private String ico, observationProperty;
	private TIME_UNIT timeUnit;
	private int timeValue;
	private Date from, to;
	private boolean hasIco, hasTimeUnit, hasTimeValue, hasFrom, hasTo, hasObservationProperty;
	
	
	
	// Getters and Setters
	
	public String getIco() {
		return ico;
	}
	public void setIco(String ico) {
		this.ico = ico;
	}
	public TIME_UNIT getTimeUnit() {
		return timeUnit;
	}
	public void setTimeUnit(TIME_UNIT timeUnit) {
		this.timeUnit = timeUnit;
	}
	public int getTimeValue() {
		return timeValue;
	}
	public void setTimeValue(int timeValue) {
		this.timeValue = timeValue;
	}
	public Date getFrom() {
		return from;
	}
	public void setFrom(Date from) {
		this.from = from;
	}
	public Date getTo() {
		return to;
	}
	public void setTo(Date to) {
		this.to = to;
	}
	
	
	public boolean hasIco(){
		return this.hasIco;
	}
	public boolean hasTimeUnit(){
		return this.hasTimeUnit;
	}
	public boolean hasTimeValue(){
		return this.hasTimeValue;
	}
	public boolean hasFrom(){
		return this.hasFrom;
	}
	public boolean hasTo(){
		return this.hasTo;
	}
	public String getObservationProperty() {
		return observationProperty;
	}
	public void setObservationProperty(String obsProp) {
		this.observationProperty = obsProp;
	}
	
	public void setIncludedKeys(JSONObject inputObject) {
		
		hasIco= inputObject.containsKey("ico");
		hasTimeUnit=inputObject.containsKey("timeUnit");
		hasTimeValue=inputObject.containsKey("timeValue");
		hasFrom=inputObject.containsKey("from");
		hasTo=inputObject.containsKey("to");
		hasObservationProperty=inputObject.containsKey("observationProperty");
	}

}

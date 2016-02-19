package eu.vital.filtering.inputJSON;

import java.util.Date;
import java.util.HashMap;

import org.json.simple.JSONObject;

public class Threshold_JSON_Object implements RequestJSONObjectInterface {
	
	
	private String ico, observationProperty;
	private INEQUALITY_TYPE inequality;
	private SelectionArea position;
	private double value;
	private Date from, to;
	private boolean hasIco, hasInequality, hasValue, hasObservationProperty, hasFrom, hasTo, hasPosition;
	public enum INEQUALITY_TYPE{
		gt,
		lt,
		lte,
		gte;
	}
	
	public void setIco(String ico){
		this.ico=ico;
	}
	public String getIco(){
		return this.ico;
	}
	public void setInequality(INEQUALITY_TYPE ineq){
		this.inequality=ineq;
	}
	public INEQUALITY_TYPE getInequality(){
		return this.inequality;
	}
	public void setValue(double val){
		this.value=val;
	}
	public double getValue(){
		return this.value;
	}
	public void setObservationProperty(String obsProp){
		this.observationProperty=obsProp;
	}
	public String getObeservationProperty(){
		return this.observationProperty;
	}
	public void setFrom(Date from){
		this.from=from;
	}
	public Date getFrom(){
		return this.from;
	}
	public void setTo(Date to){
		this.to=to;
	}
	public Date getTo(){
		return this.to;
	}
	public void setPosition(SelectionArea position){
		this.position=position;
	}
	public SelectionArea getPosition(){
		return this.position;
	}
	
	public boolean hasIco(){
		return this.hasIco;
	}
	public boolean hasInequality(){
		return this.hasInequality;
	}
	public boolean hasValue(){
		return this.hasValue;
	}
	public boolean hasObservationProperty(){
		return this.hasObservationProperty;
	}
	public boolean hasFrom(){
		return this.hasFrom;
	}
	public boolean hasTo(){
		return this.hasTo;
	}
	public boolean hasPosition(){
		return this.hasPosition;
	}
	
	public void setIncludedKeys(JSONObject inputObject) {
		
		this.hasIco=inputObject.containsKey("ico");
		this.hasInequality=inputObject.containsKey("inequality");
		this.hasValue=inputObject.containsKey("value");
		this.hasObservationProperty=inputObject.containsKey("observationProperty");
		this.hasFrom=inputObject.containsKey("from");
		this.hasTo=inputObject.containsKey("to");
		if(!inputObject.containsKey("position")){
			this.hasPosition=false;
		}else{			
			HashMap receivedPosition=(HashMap) inputObject.get("position");
			if(!receivedPosition.containsKey("latitude")||!receivedPosition.containsKey("longitude")||!receivedPosition.containsKey("radius"))
				this.hasPosition=false;
			else
				this.hasPosition=true;
			
		}
	}

}

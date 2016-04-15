/**
* @Author: Riccardo Petrolo <riccardo> - Salvatore Guzzo Bonifacio <salvatore>
* @Date:   2016-03-30T17:37:24+02:00
* @Email:  riccardo.petrolo@inria.fr
* @Last modified by:   riccardo
* @Last modified time: 2016-03-30T18:27:05+02:00
*/



package eu.vital.discoverer.inputJSON;

import java.util.HashMap;

import org.jboss.resteasy.logging.Logger;
import org.json.simple.JSONObject;


public class Discover_ICO_JSON_Object implements RequestJSONObjectInterface {

	final static Logger logger=Logger.getLogger(Discover_ICO_JSON_Object.class);
	private SelectionArea position;
	private String system, observes, type, movementPattern, connectionStability;
	private boolean hasLocalizer;
	private int timeWindow;

	// boolean variables to keep track of defined keys in the input JSON object
	private boolean hasSystem, hasPosition, hasObserves, hasType, hasMovementPattern, hasConnectionStability, hasHasLocalizer, hasTimeWindow;

	public SelectionArea getPosition() {
		return position;
	}

	public void setPosition(SelectionArea position) {
		this.position = position;
	}

	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
	}

	public String getObserves() {
		return observes;
	}

	public void setObserves(String observes) {
		this.observes = observes;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMovementPattern() {
		return movementPattern;
	}

	public void setMovementPattern(String movementPattern) {
		this.movementPattern = movementPattern;
	}

	public String getConnectionStability() {
		return connectionStability;
	}

	public void setConnectionStability(String connectionStability) {
		this.connectionStability = connectionStability;
	}

	public boolean isHasLocalizer() {
		return hasLocalizer;
	}

	public void setHasLocalizer(boolean hasLocalizer) {
		this.hasLocalizer = hasLocalizer;
	}

	public int getTimeWindow() {
		return timeWindow;
	}

	public void setTimeWindow(int timeWindow) {
		this.timeWindow = timeWindow;
	}

	public boolean hasSystem(){

	return this.hasSystem;
	}

	public boolean hasPosition(){
		return this.hasPosition;
	}

	public boolean hasObserves(){
		return this.hasObserves;
	}

	public boolean hasType(){
		return this.hasType;
	}

	public boolean hasMovementPattern(){
		return this.hasMovementPattern;
	}

	public boolean hasConnectionStability(){
		return this.hasConnectionStability;
	}

	public boolean hasHasLocalizer(){
		return this.hasHasLocalizer;
	}

	public boolean hasTimeWindow(){
		return this.hasTimeWindow;
	}

	public void setIncludedKeys(JSONObject inputObject) {

		this.hasObserves=inputObject.containsKey("observes");
		this.hasType=inputObject.containsKey("type");
		this.hasMovementPattern=inputObject.containsKey("movementPattern");
		this.hasConnectionStability=inputObject.containsKey("connectionStability");
		this.hasTimeWindow=inputObject.containsKey("timeWindow");
		this.hasHasLocalizer=inputObject.containsKey("hasLocalizer");
		this.hasSystem=inputObject.containsKey("system");

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

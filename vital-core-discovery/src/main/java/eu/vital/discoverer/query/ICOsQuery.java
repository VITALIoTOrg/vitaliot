package eu.vital.discoverer.query;

import java.io.IOException;
import java.util.LinkedList;

import org.jboss.resteasy.logging.Logger;
import org.json.simple.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.vital.discoverer.exception.DiscoveryApplicationException;
import eu.vital.discoverer.inputJSON.Discover_ICO_JSON_Object;
import eu.vital.discoverer.inputJSON.SelectionArea;
import eu.vital.discoverer.util.DMSManager;
import eu.vital.discoverer.util.DataExtractor;
import eu.vital.discoverer.util.GPSPoint;
import eu.vital.discoverer.util.GPSUtils;
import eu.vital.discoverer.util.MapSelectionSquare;
import eu.vital.discoverer.util.DMSManager.DMS_Index;

public class ICOsQuery extends DiscoverQuery {

	final static Logger logger=Logger.getLogger(ICOsQuery.class);
	private Discover_ICO_JSON_Object inputObject;
	private final String OBSERVES_KEY="http://purl.oclc.org/NET/ssnx/ssn#observes";
	private final String TYPE_KEY="type";
	private final String MOVEMENT_PATTERN_KEY="hasMovementPattern.type";
	private final String CONNECTION_STABILITY_KEY="connectionstability";

	public ICOsQuery() {
		super();
	}

	@Override
	public void setInputJSON(JSONObject inputJSON) {
		this.inputJSON=inputJSON;

		ObjectMapper mapper = new ObjectMapper();
		try {
			inputObject = mapper.readValue(inputJSON.toString(), Discover_ICO_JSON_Object.class);
		} catch (JsonParseException e) {
			logger.error("Error unmarshaling input JSON object",e);
			throw new DiscoveryApplicationException();

		} catch (JsonMappingException e) {
			logger.error("Error mapping input JSON object",e);
			throw new DiscoveryApplicationException();

		} catch (IOException e) {
			logger.error("IO Error on input JSON object",e);
			throw new DiscoveryApplicationException();

		}

		inputObject.setIncludedKeys(inputJSON);

	}

	@Override
	public void executeQuery() {

		DMSManager manager=new DMSManager(DMS_Index.SENSOR, this.cookie);

		LinkedList<JSONObject> result = null;

		if(this.inputObject.hasPosition()){
			SelectionArea area=inputObject.getPosition();
		 	GPSPoint areaCenter=new GPSPoint(area.getLatitude(), area.getLongitude());
		 	MapSelectionSquare areaSelection=new MapSelectionSquare(areaCenter, area.getRadius());
		 	result=combineResults(result, manager.searchInRegion(areaSelection));

		 	MapSelectionSquare predictionSelection=new MapSelectionSquare(areaCenter, 5*area.getRadius());
		 	LinkedList<JSONObject> predictedICOs=manager.searchPredictedInRegion(predictionSelection);

		 	int timeWindow= inputObject.hasTimeWindow() ? inputObject.getTimeWindow() : 15;
		 	LinkedList<JSONObject> predicted=computeMobility(predictedICOs,areaSelection, timeWindow);
		 	for(JSONObject mobile:predicted)
		 		result.addLast(mobile);
		}

		if(this.inputObject.hasType()){
			result=combineResults(result, manager.getByField("@"+TYPE_KEY, inputObject.getType()));
		}

		if(this.inputObject.hasObserves()){
			//result=combineResults(result, manager.getByField(OBSERVES_KEY, inputObject.getObserves()));
			result=combineResults(result, manager.getByObser(OBSERVES_KEY, inputObject.getObserves()));
		}

		if(this.inputObject.hasMovementPattern()){
			result=combineResults(result, manager.getByField("@"+MOVEMENT_PATTERN_KEY, inputObject.getMovementPattern()));
		}

		if(this.inputObject.hasConnectionStability()){
			result=combineResults(result, manager.getByField("@"+CONNECTION_STABILITY_KEY, inputObject.getConnectionStability()));
		}


		this.result_in_JSON=result;
	}

	@Override
	public boolean checkJSONInput(JSONObject object) {
		if(object.keySet().size()==0)

			return false;
		else
			return true;
	}


private LinkedList<JSONObject> computeMobility(LinkedList<JSONObject> mobileICOs, MapSelectionSquare selectionArea, int timeWindow){

		LinkedList<JSONObject> mobilityResult=new LinkedList<JSONObject>();
		int i=1;
//	 	logger.debug("Data for prediction:\n");
//	 	logger.debug("Selection Area:"+selectionArea.toString());
//	 	logger.debug("timeWindow: "+timeWindow);
	 	for(JSONObject obj:mobileICOs){
	 		GPSPoint position=DataExtractor.extractKnownLocation(obj);
	 		GPSPoint prediction=DataExtractor.extractPredictedDirection(obj);
//	 		logger.debug("Predicted #"+i);
//	 		logger.debug("Position: "+position.toString());
//	 		logger.debug("Prediction direction: "+prediction.toString());
	 		if(!selectionArea.isPointInSelection(position)){
	 			double angle=GPSUtils.angleBetweenPoints_V3(position, prediction);
	 			double distance=DataExtractor.getPredictedTraveledDistance(obj, timeWindow);
	 			GPSPoint destination=GPSUtils.getDestinationPoint(position, angle, distance);
	 			logger.debug("Predicted new position:"+destination.toString());
	 			if(selectionArea.isPointInSelection(destination)){
	 				logger.debug("new position in selection area");
	 				mobilityResult.addLast(obj);
	 			}
	 		}
	 		i++;
	 	}

		return mobilityResult;
	}

}

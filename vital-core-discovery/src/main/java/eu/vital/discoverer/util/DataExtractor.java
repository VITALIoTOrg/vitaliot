/**
* @Author: Riccardo Petrolo <riccardo> - Salvatore Guzzo Bonifacio <salvatore>
* @Date:   2016-03-30T17:37:24+02:00
* @Email:  riccardo.petrolo@inria.fr
* @Last modified by:   riccardo
* @Last modified time: 2016-03-30T18:27:05+02:00
*/



package eu.vital.discoverer.util;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

public class DataExtractor {

	static final Logger logger=Logger.getLogger(DataExtractor.class);

	// Map with convertion factor from qudt speed units to kilometer per hour
	private static HashMap<String, Double> convertionFactors=new HashMap<String, Double>();
	static{
		convertionFactors.put("qudt:MilePerHour", 1.609344);
		convertionFactors.put("qudt:FootPerHour", 0.0003048);
		convertionFactors.put("qudt:Knot", 1.852);
		convertionFactors.put("qudt:MeterPerMinute", 0.0600000001);
		convertionFactors.put("qudt:MilePerMinute", 96.56064);
		convertionFactors.put("qudt:MeterPerSecond", 3.6);
		convertionFactors.put("qudt:FootPerMinute", 0.018288);
		convertionFactors.put("qudt:InchPerSecond", 0.09144);
		convertionFactors.put("qudt:MeterPerHour", 0.001);
		convertionFactors.put("qudt:FootPerSecond", 1.09728);
		convertionFactors.put("qudt:CentimeterPerSecond", 0.036);
		convertionFactors.put("qudt:KilometerPerHour", 1.0);
		convertionFactors.put("qudt:KilometerPerSecond", 3600.0);
	}

	/**
	 * Return an object containing latitude and longitude stored in hasLastKnownLocation of a VITAL ICO
	 *
	 * @param input JSONObject containing ICO data. Used to extract latitude and longitude of last known location
	 * @return Object containing last known location. If info is not present returns null
	 */
	public static GPSPoint extractKnownLocation(JSONObject input){
		double latitude, longitude;
		if(input.containsKey("hasLastKnownLocation")){
			JSONObject location=(JSONObject)input.get("hasLastKnownLocation");
			if(location.containsKey("geo:lat")){
				latitude=Double.parseDouble(location.get("geo:lat").toString());
			}
			else{
				return null;
			}
			if(location.containsKey("geo:long")){
				longitude=Double.parseDouble(location.get("geo:long").toString());
			}
			else{
				return null;
			}

			return new GPSPoint(latitude, longitude);

		}
		else{
			return null;
		}
	}

	/**
	 * Return an object containing latitude and longitude stored in hasPredictedDirection of a VITAL ICO
	 *
	 * @param input JSONObject containing ICO data. Used to extract latitude and longitude of predicted direction
	 * @return Object containing predicted direction coordinates. If info is not present returns null
	 */
	public static GPSPoint extractPredictedDirection(JSONObject input){
		double latitude, longitude;
		if(input.containsKey("hasMovementPattern")){
			JSONObject pattern=(JSONObject)input.get("hasMovementPattern");

			if(pattern.containsKey("hasPredictedDirection")){
				JSONObject location=(JSONObject)pattern.get("hasPredictedDirection");
				if(location.containsKey("geo:lat")){
					latitude=Double.parseDouble(location.get("geo:lat").toString());
				}
				else{
					return null;
				}
				if(location.containsKey("geo:long")){
					longitude=Double.parseDouble(location.get("geo:long").toString());
				}
				else{
					return null;
				}

				return new GPSPoint(latitude, longitude);
			}
			else{
				return null;
			}

		}
		else{
			return null;
		}
	}


	/**
	 * Computes predicted traveled distance according
	 *
	 * @param input JSON object describing an ICO
	 * @param timeInMinutes time value used to compute distance
	 * @return expected traveled distance
	 */
	public static double getPredictedTraveledDistance(JSONObject input, int timeInMinutes){
		double distance=0;
		double speed=0;
		double convertionFactor;


		if(input.containsKey("hasMovementPattern")){
			JSONObject pattern=(JSONObject)input.get("hasMovementPattern");

			if(pattern.containsKey("hasPredictedSpeed")){
				JSONObject speedPrediction=(JSONObject)pattern.get("hasPredictedSpeed");
				if(speedPrediction.containsKey("value")){
					speed=Double.parseDouble(speedPrediction.get("value").toString());
					logger.debug("Extracted Speed: "+speed);
				}
				if(speedPrediction.containsKey("qudt:unit")){
					String unit=(String)speedPrediction.get("qudt:unit");
					convertionFactor=getSpeedConvertionFactor(unit);
					speed=speed*convertionFactor;
					logger.debug("extracted unit: "+unit);
					logger.debug("converted speed: "+speed);
				}
				// compute traveled dinstance in kilometers using speed in kilometers per hour and time in minutes
				distance=speed*((double)timeInMinutes/60);

			}


		}

		logger.debug("Predicted traveled distance: "+distance);

		return distance;
	}

	/**
	 * computes a conversion factor to pass from received speed unit to kilometers per hour
	 *
	 * @param speedUnit received speed unit
	 * @return convertion factor to kilometers per hour
	 */
	private static double getSpeedConvertionFactor(String speedUnit){

		if(convertionFactors.containsKey(speedUnit)){
			logger.debug("speed conversion factor: "+convertionFactors.get(speedUnit));
			return convertionFactors.get(speedUnit);
		}
		else{
			// if speed unit is not handled mobile node is ignored
			return 0;
		}
	}


}

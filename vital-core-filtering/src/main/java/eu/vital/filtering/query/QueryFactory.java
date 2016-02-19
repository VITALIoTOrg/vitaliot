package eu.vital.filtering.query;

import org.apache.log4j.Logger;

public class QueryFactory {

	private final static Logger logger=Logger.getLogger(QueryFactory.class);
	
	public static FilteringQuery getQuery(QueryTypeEnumeration queryType){
		
		switch(queryType){
		case THRESHOLD_QUERY:
			logger.info("Created new Threshold Query");
			return new ThresholdQuery();
		case RESAMPLING_QUERY:
			logger.info("Created new Resampling Query");
			return new ResamplingQuery();
		default:
			throw new RuntimeException("Unsupported query type");
				
		}
	}
}

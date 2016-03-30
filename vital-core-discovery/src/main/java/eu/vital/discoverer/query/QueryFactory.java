/**
* @Author: Riccardo Petrolo <riccardo>
* @Date:   2016-02-26T09:52:37+01:00
* @Email:  riccardo.petrolo@inria.fr
* @Last modified by:   riccardo
* @Last modified time: 2016-03-30T18:27:03+02:00
*/



package eu.vital.discoverer.query;

public class QueryFactory {

	public static DiscoverQuery getQuery(QueryTypeEnum queryType){

		switch(queryType){
			case ICOS_QUERY:
				return new ICOsQuery();
//				break;
			case SYSTEMS_QUERY:
				return new SystemsQuery();
//				break;
			case SERVICES_QUERY:
				return new ServicesQuery();
//				break;
			default:
				throw new RuntimeException("Unsupported query type");
		}
	}

}

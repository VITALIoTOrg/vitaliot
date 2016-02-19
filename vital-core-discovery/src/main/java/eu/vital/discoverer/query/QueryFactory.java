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

/**

Copyright 2014 ATOS SPAIN S.A.


Authors Contact:
Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atos.net
**/

package net.atos.ari.vital.tassproxy;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import net.atos.ari.vital.external.ThingInformation;
import net.atos.ari.vital.taasaggregator.ThingServiceTrust;

@Service("TaaSBDMClient")
public class TaaSBDMClient 
{
	@Autowired
	SystemsManager systemsManager;

	
	private static Logger logger = Logger.getLogger(TaaSBDMClient.class);
	
	private TaaSBDMClient ()
	{
	
	}
	
	
	public void storeTrustData (ThingServiceTrust trustObject)
	{
		systemsManager.instertTrustResult(trustObject);
	}	
	
	public ThingServiceTrust getTrustData (String idThingService)
	{
		return null;
	}
	
	public ArrayList<ThingTrustData> getThingData (String idThing)
	{
		ArrayList<ThingTrustData> myResult = new ArrayList<ThingTrustData>();
		
		
		return myResult;
	}	
	
	public ThingInformation getThingInformation (String idThing)
	{
		
		return null;
	}
	
	public ArrayList<Long> getThingFailures (String idThingService, String idThing)
	{
		ArrayList<Long> failuresResults = new ArrayList<Long>();
		
		return failuresResults;
	}
	
	public ThingLocation getThingLastLocation (String idThing)
	{
		ThingLocation myResult = null;
		
		
		return myResult;
	}
}

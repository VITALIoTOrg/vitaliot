/**

Copyright 2014 ATOS SPAIN S.A.

Licensed under the Apache License, Version 2.0 (the License);
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Authors Contact:
Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atos.net
**/

package net.atos.ari.vital.taasaggregator;

import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.atos.ari.vital.external.ThingInformation;
import net.atos.ari.vital.taastrustcalculator.QoSFulfillmentCalculator;
import net.atos.ari.vital.tassproxy.SystemsManager;
import net.atos.ari.vital.tassproxy.TaaSBDMClient;
import net.atos.ari.vital.tassproxy.TaaSCMClient;
import net.atos.ari.vital.tassproxy.ThingTrust;
import net.atos.ari.vital.tassproxy.ThingTrustData;


@Component
public class ThingServiceTrustCalculator 
{
	private static Logger logger = Logger.getLogger(ThingServiceTrustCalculator.class);
	@Autowired
	private TaaSBDMClient myBDMClient;
	@Autowired
	SystemsManager systemsManager;
	
	@Autowired
	private TaaSCMClient myCM;
	@Autowired
	private QoSFulfillmentCalculator qosCalculator;
	
	
	public ThingServiceTrustCalculator ()
	{
	}
	
	public ThingServiceTrust calculateThingServiceTrust (String thingServiceId, Date previousTime, Date currentTime)
	{
		// Step 1 -> Check the Thing Service exists and retrieve basic data
		/*
		if (basicData==null)
		{
			// If basic data isn't available, just return default value and don't store data
			logger.info("Not enough basic data found! Providing default values!");
			return new ThingServiceTrust(thingServiceId, 2.5);
		}*/
		
		// Step 3 -> Calculate QoS fulfillment		
		/*VITAL POR AQUI******************************************************************/
		float qos = qosCalculator.calculateTrustAspect(thingServiceId, previousTime, currentTime);
				
		
		// Step 9 -> Generate trust result
		ThingServiceTrust fullTrustResult = new ThingServiceTrust(thingServiceId, qos, currentTime);
		
		// Step 10 -> Store generated information
		myBDMClient.storeTrustData(fullTrustResult);
		
		// Step 11 -> Report to the monitoring panel
		
		return fullTrustResult;
	}
	
	private ThingTrust retrieveBasicData(String thingServiceId)
	{
	
		ThingTrust basicData = new ThingTrust (thingServiceId);
		logger.debug("Retrieving basic data for {" +thingServiceId+"}");
		 
		// Retrieving thing identifier for that thing service
		String thingId = myCM.retrieveThingIdentifier(thingServiceId);
		if (thingId == null || thingId.equalsIgnoreCase(""))
		{
			logger.error ("It was not possible to retrieve basic data for thing service " + thingServiceId);
			return null;
		}
		basicData.setThingId(thingId);
		
		// Retrieve all basic data: units
		//from external betaas jaar
		ThingInformation fullInfo = myBDMClient.getThingInformation(thingServiceId /* EGO thingId*/);
		if (fullInfo==null)
		{
			logger.error ("It was not possible to retrieve basic data for thing info " + thingServiceId /* EGO thingId*/);
			return null;
		}
		basicData.setUnits(fullInfo.getUnit());
		String type = fullInfo.getType();
		// Retrieve last data generated
		ArrayList<ThingTrustData> thingDataList = myBDMClient.getThingData(thingServiceId /* EGO thingId*/);
		basicData.setDataList(thingDataList);
		logger.debug ("Thing Data size of list received for {"+thingServiceId +"}: {" + thingDataList.size()+"}");
		
		logger.debug("Basic data retrieval finished!");
		
		return basicData;
	}
}

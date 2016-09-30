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

package net.atos.ari.vital.tassproxy;

import java.util.Date;
import java.util.List;

import net.atos.ari.vital.external.QoSManagerInternalIF;
import net.atos.ari.vital.external.SLACalculation;
import net.atos.ari.vital.mongo.data.MetricMongoData;
import net.atos.ari.vital.mongo.data.SystemMongoData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
public class TaaSQoSMClient 
{

	@Autowired
	SystemsManager systemsManager; 
	
	private QoSManagerInternalIF myClient;	
	private static Logger logger = LoggerFactory.getLogger(TaaSQoSMClient.class);
	
	String thingServiceId;
	Date startDate;
	Date endDate;
	
	public TaaSQoSMClient (String thingServiceId, Date startDate, Date endDate)
	{
		this.thingServiceId = thingServiceId;
		this.startDate = startDate;
		this.endDate = endDate;
/*EGO		// Retrieve the BundleContext from the OSGi Framework
		BundleContext context = FrameworkUtil.getBundle(TaaSQoSMClient.class).getBundleContext();
				
		// Retrieve the QoS Manager object
		// Open tracker in order to retrieve QoS Manager services
		ServiceTracker myTracker = new ServiceTracker(context, QoSManagerInternalIF.class.getName(), null); 
		myTracker.open();		
		Object [] providers = myTracker.getServices(); 
		
		// Select a provider
		int n = 0;
		if ( providers != null && providers.length > 0 ) 
		{ 		
			logger.debug("Number of providers found for TaaS QoSM: " + providers.length);			
			myClient = (QoSManagerInternalIF) providers[n];
			logger.info("Taas QoS Manager Service found!");
		}
		else
		{
			logger.error("No providers were found for the TaaS QoS Manager");			
		}
		
		// Close the tracker
		myTracker.close();*/		
	}
	
	
	
	public SLACalculation retrieveSLACalculations()
	{
		logger.debug("Retrieving SLA data for Thing Services...");
		SLACalculation result = null;
		int numFullfillment = 0;
		int numNonFullfillment = 0;
		try
		{				
			List<SystemMongoData> mongoDataList = systemsManager.getSystemMetricsFromId(thingServiceId, startDate, endDate);
			if ((mongoDataList!=null) && (mongoDataList.size()>0)){
				result = new SLACalculation();
				for (SystemMongoData mongoData : mongoDataList){
					logger.debug("Data from id {}",mongoData.getId());		
					for (MetricMongoData metricMongoData :mongoData.getMetric()){
						logger.debug("\t{} - {} - {}", metricMongoData.getMetric_name(), metricMongoData.getFulfilment(), metricMongoData.getDate());		
						if (metricMongoData.getFulfilment().equals("1"))
							numFullfillment++;
						else 
							numNonFullfillment++;
					}
				}
				result.setThingServiceId(thingServiceId);
				result.setQoSparamsFulfill(numFullfillment);
				result.setQoSparamsNoFulfill(numNonFullfillment);
			}
			/* EGO result = myClient.calculateSLA(thingServiceId); myClient siempre es null	*/
		}
		catch (Exception ex)
		{
			logger.error("An error occurred when retrieving QoS data: " + ex.toString());
		}
		
		return result;
	}
}

package net.atos.ari.vital.thread;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import net.atos.ari.vital.taasaggregator.ThingServiceTrust;
import net.atos.ari.vital.taasaggregator.ThingServiceTrustCalculator;
import net.atos.ari.vital.tassproxy.SystemsManager;


@Component
@Configuration
@EnableAsync
@EnableScheduling
public class TaaSMonitoringThread implements InitializingBean, ITaaSMonitoringThread{

	private static Logger logger = Logger.getLogger(TaaSMonitoringThread.class);
	//private static final String CRON = "eu.atos.trust.calculator.cron";
	private final HashMap<String, Double> servicesAlerts;
	@Autowired
	SystemsManager systemsManager;
	@Autowired
	ThingServiceTrustCalculator myCalculator;
	//@Value("TRUST{0 * * * * *}")
	private String cron;
	private Date previousTime;

	public TaaSMonitoringThread() 
	{
		servicesAlerts = new HashMap<String, Double>();		
	}
	

	@Scheduled(cron = "0 * * * * *")
	public void runClaculateTrust(){
		try
		{
			
							
			Date currentTime = new Date(); 
			List<String>thingsServList = systemsManager.getActiveSystems(previousTime, currentTime);
			
			
			for (int i=0; i<thingsServList.size(); i++)
			{
				String thingID = thingsServList.get(i);
				logger.info("Calculating trust for thing service: " + thingID);
				ThingServiceTrust trustResult = myCalculator.calculateThingServiceTrust(thingID, previousTime, currentTime);
				
			}								
			previousTime= currentTime;
				
			
			
		}
		catch (Exception ex)
		{
			logger.error("Failure when recalculating Trust for Things Services!", ex);
		}			
		
		if (!servicesAlerts.isEmpty())
		{
			//logger.info ("Checking alerts...");
			checkAlerts();
		}
		
		//logger.info ("EndOf runClaculateTrust");
	}
	
	private void checkAlerts()
	{							
		// Per each Thing Service Id, retrieve trust value and compare with the threshold			
		logger.debug("Checking subscriptions about Things Services");			
		Set<String> servicesList = servicesAlerts.keySet();
		Iterator<String> servIterator = servicesList.iterator();
		while (servIterator.hasNext())
		{
			String currentId = servIterator.next();
			double threshold = servicesAlerts.get(currentId);
			try
			{
				// Actions for retrieving the trust here!
				double currentVal = 2.5; //test value
				if (currentVal<threshold)
				{
				}
			}
			catch (Exception ex)
			{
			}				
		}
		
		logger.debug("All subscriptions for alerts processed.");
	}

	public void afterPropertiesSet() throws Exception {
		logger.debug("Frequency to invoke calculation: cron[{"+cron+"}]" );
	}

	
}

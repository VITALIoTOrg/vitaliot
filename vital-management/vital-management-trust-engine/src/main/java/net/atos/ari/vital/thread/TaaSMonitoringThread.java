package net.atos.ari.vital.thread;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.atos.ari.vital.taasaggregator.ThingServiceTrust;
import net.atos.ari.vital.taasaggregator.ThingServiceTrustCalculator;
import net.atos.ari.vital.tassproxy.SystemsManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@Configuration
@EnableAsync
@EnableScheduling
public class TaaSMonitoringThread implements InitializingBean, ITaaSMonitoringThread{

	private static Logger logger = LoggerFactory.getLogger(TaaSMonitoringThread.class);
	private static final String CRON = "eu.atos.trust.calculator.cron";
	private final HashMap<String, Double> servicesAlerts;
	@Autowired
	SystemsManager systemsManager;
	@Autowired
	ThingServiceTrustCalculator myCalculator;
	@Value("TRUST{" + CRON + "}")
	private String cron;
	private Date previousTime;

	public TaaSMonitoringThread() 
	{
		servicesAlerts = new HashMap<String, Double>();		
	}
	

	@Scheduled(cron = "TRUST{" + CRON + "}")
	public void runClaculateTrust(){
		try
		{
			logger.info ("StartOf runClaculateTrust");
			// Create the Trust Calculator to be used
			//EGO autowired ThingServiceTrustCalculator myCalculator = new ThingServiceTrustCalculator ();
							
			Date currentTime = new Date(); 
			List<String>thingsServList = systemsManager.getActiveSystems(previousTime, currentTime);
			
			logger.info("Rest of Active Things Services: " + thingsServList.size());
				
			// Calculate trust for the non-priority list
			for (int i=0; i<thingsServList.size(); i++)
			{
				String thingID = thingsServList.get(i);
				logger.info("Calculating trust for thing service: " + thingID);
				ThingServiceTrust trustResult = myCalculator.calculateThingServiceTrust(thingID, previousTime, currentTime);
				logger.info("Value calculated {}: {}", thingID, trustResult.getTrustScore());
			}								
			previousTime= currentTime;
				
			
			// Perform trust calculation for the priority list
			//EGO NO SE PARA QUE SE USA logger.info("Active Things Services: " + priorityList.size());
		}
		catch (Exception ex)
		{
			logger.error("Failure when recalculating Trust for Things Services!", ex);
		}			
		
		if (!servicesAlerts.isEmpty())
		{
			logger.info ("Checking alerts...");
			checkAlerts();
		}
		
		logger.info ("EndOf runClaculateTrust");
	}
	//EGO ni idea para que sirve, tengo la sensaci�n que para nada
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
		logger.debug("Frequency to invoke calculation: cron[{}]", cron);
	}

	/*
	public synchronized boolean subscribeAlert (double threshold, String idEntity)
	{
		try
		{
			servicesAlerts.put(idEntity, new Double (threshold));			
			logger.info("Subscription accepted for " + idEntity + " with threshold " + threshold);				
		}
		catch (Exception ex)
		{
			logger.error("Failure in the subscription for " + idEntity + ": " + ex.getMessage());
			return false;
		}
		return true;
	}
	
	public synchronized boolean unSubscribeAlert (String idEntity)
	{
		try
		{
			servicesAlerts.remove(idEntity);			
			logger.info("Subcription removed for " + idEntity);
		}
		catch (Exception ex)
		{
			logger.error("Failure in the unsubscription for " + idEntity + ": " + ex.getMessage());
			return false;
		}			
		return true;
	}
	*/
}

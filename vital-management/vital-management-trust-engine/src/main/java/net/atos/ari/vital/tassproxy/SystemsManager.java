package net.atos.ari.vital.tassproxy;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.atos.ari.vital.mongo.MongoDriver;
import net.atos.ari.vital.mongo.data.JSONParserException;
import net.atos.ari.vital.mongo.data.SystemMongoData;
import net.atos.ari.vital.taasaggregator.ThingServiceTrust;

@Service("SystemsManager")
public class SystemsManager {
	private static Logger logger = Logger.getLogger(SystemsManager.class);
	@Autowired
	MongoDriver mongoDriver;

	public List<String> getActiveSystems(Date startTime, Date endTime)
	{
		logger.debug ("Calling Get Thing Services to the CM for getting the full list");
		List<String> list = mongoDriver.getSystemsRunning(startTime, endTime);
		logger.debug("Invocation done! Retrieved: " + list.size());
		return list;
	}


	public List<SystemMongoData> getSystemMetricsFromId(String serviceId, Date startTime, Date endTime)
	{
		logger.debug ("Calling Get Thing Services to the CM for getting the full list");
		List<SystemMongoData> list = null;
		try {
			list = mongoDriver.getSystemMetrics(serviceId, startTime, endTime);
		} catch (JSONParserException e) {
			logger.error("Invocation done! Fatal error: ", e);
			return null;
		}
		logger.debug("Invocation done! Retrieved: " + list.size());
		return list;
	}
	
    public void instertTrustResult (ThingServiceTrust data){
		logger.debug ("Start instertTrustResult");
		try {
			mongoDriver.instertTrustResult(data);
		} catch (Exception e) {
			logger.error("No data recorded! Fatal error: ", e);
		}
		logger.debug("End instertTrustResult");
    }


}

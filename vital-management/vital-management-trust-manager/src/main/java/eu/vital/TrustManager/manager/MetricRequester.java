package eu.vital.TrustManager.manager;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import eu.vital.TrustManager.MongoDriver;
import eu.vital.TrustManager.conf.ConfigReader;
import eu.vital.TrustManager.params.IoTSystem;
import eu.vital.TrustManager.taskExecutor.Tasker;

/**
 * @author adminuser
 * this class implement the code to retrieve metrics from all monitored systems.
 */
/**
 * @author adminuser
 *iotshm hashmap with all info related ot iotsystems.
 */
public class MetricRequester implements Tasker{

	public String name;
	private String dmsURL;
	private String iotdaURL;
    private String cookie;
    private ConcurrentHashMap<String, IoTSystem>iotshm;
    
	Logger logger = Logger.getLogger(this.getClass().getName());
	
		
	/**
	 * @param hashmap of iotsystems, it contains all info about monitored iotsystems.
	 * 
	 */
	public MetricRequester(ConcurrentHashMap<String, IoTSystem>iotshm){
		ConfigReader configReader = ConfigReader.getInstance();
		dmsURL = configReader.get(ConfigReader.DMS_URL);
		iotdaURL = configReader.get(ConfigReader.IOTDA_URL);
		this.iotshm = iotshm;
		
	}

	/* (non-Javadoc)
	 * @see eu.vital.TrustManager.taskExecutor.Tasker#doTask()
	 * this method is the one used to retrieve metrics. the name is fixed by the Tasker interface implemented by this class.
	 */
	@Override
	public boolean doTask() {

		try {

			Iterator<Entry<String, IoTSystem>> it= iotshm.entrySet().iterator();
			MongoDriver md = MongoDriver.getInstance();
			while (it.hasNext()){
				IoTSystem iots = it.next().getValue();

				///////////////////////////////
				//iots.generateValues(); 
				//for simulation purposes only

				iots.getSystemMetricValues();
				StringBuilder sb = new StringBuilder(iots.getDocument());



				md.instertIoTSystemMetric(sb.toString());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e,e);
		}


		return true;
	}
}

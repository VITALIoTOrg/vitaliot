package eu.vital.TrustManager;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import eu.vital.TrustManager.taskExecutor.Tasker;
import eu.vital.vitalcep.conf.ConfigReader;

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
				iots.generateValues(); 
				//TODO esto deberia ser la consulta a los IoT systems para recuperar las metricas

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

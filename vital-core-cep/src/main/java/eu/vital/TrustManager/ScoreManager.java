package eu.vital.TrustManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.json.JSONArray;

import eu.vital.TrustManager.taskExecutor.Tasker;
import eu.vital.vitalcep.conf.ConfigReader;
import eu.vital.vitalcep.connectors.dms.DMSManager;
import eu.vital.vitalcep.security.Security;

/**
 * @author adminuser
 * this class is used to manage the simulation of trust scores. it insert into mongodb the simulated values of scores.
 */
public class ScoreManager  implements Tasker {
		
	MongoDriver md;
	ConcurrentHashMap iotshm;
	Random r;
	String cookie;
	String dmsURL;
	
	String lastValueTime;
	
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
	Date date = new Date();
	
	Logger logger = Logger.getLogger(this.getClass().getName());
	
	/**
	 * class constructor
	 * @param iotshm: hashmap of iotsystem conatining all info about iotsystems.
	 */
	public ScoreManager(ConcurrentHashMap iotshm){
		
		this.iotshm = iotshm;
		try {
			md = MongoDriver.getInstance();
			r = new Random();
			
			ConfigReader configReader = ConfigReader.getInstance();
			dmsURL = configReader.get(ConfigReader.DMS_URL);
			
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see eu.vital.TrustManager.taskExecutor.Tasker#doTask()
	 * this method is used to insert into mongodb the simulated trustscores. it is implemented according to Tasker interface.
	 */
	public  boolean doTask(){
		
		
		StringBuilder sb;// = new StringBuilder();
		Date date = new Date();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		
		Iterator<Entry<String, IoTSystem>> it = iotshm.entrySet().iterator();
		lastValueTime=dateFormat.format(date);
		int counter = 1;
		double reputation;
		
		MongoDriver md;
		try {
			md = MongoDriver.getInstance();
		
			while (it.hasNext()){
				
				//TODO elfeo is the simulated value of the trust score
				double elfeo = ((r.nextFloat()*300)+700)/1000;
				reputation = elfeo;
				sb = new StringBuilder();
				String name = it.next().getValue().name;
				sb.append("{");
				sb.append("\'IoTSystem_id\':\'" + name +"\',");
				sb.append("\'timeS\':\'"+ dateFormat.format(date) + "\',");
				sb.append("\'trustScore\':\'"+ reputation + "\'");
				sb.append("}");
				
				//TODO fake trustScores are inserted into mongoDB to simulate the trust machine working.
				md.instertIoTSystemScore(sb.toString());
			}
		} catch (Exception e) {
			
			logger.error(e,e);
		}
		return true;
	}
	


}

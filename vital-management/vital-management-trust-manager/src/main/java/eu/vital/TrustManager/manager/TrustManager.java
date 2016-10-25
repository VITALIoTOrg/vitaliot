package eu.vital.TrustManager.manager;

import java.util.concurrent.ConcurrentHashMap;

import eu.vital.TrustManager.MongoDriver;
import eu.vital.TrustManager.params.IOTsystemJSONParser;
import eu.vital.TrustManager.params.IoTSystem;
import eu.vital.TrustManager.taskExecutor.Scheduler;


public class TrustManager {
	ConcurrentHashMap<String, IoTSystem>IoTSytemHM;
	MongoDriver md;
	
	
	MetricRequester metr;
	//ScoreManager scorem;
	
	
	Scheduler metricSheduler;
	//Scheduler scoreScheduler;
	
	public static TrustManager tm;
	
	private TrustManager (String jsondef) throws Exception{
		IoTSytemHM = new ConcurrentHashMap<String, IoTSystem>();
		if (!IOTsystemJSONParser.extractIOTsystems(IoTSytemHM, jsondef))
			throw new Exception ("Error while parssing body of the request, it looks like to be malformed");
		
		metr =new MetricRequester(IoTSytemHM);
		//scorem =new ScoreManager(IoTSytemHM);
	}
	
	public static TrustManager getNewInstance(String jsonDef)throws Exception{
	if (tm != null)
			tm.removeInstance();
			tm = null;
			tm = new TrustManager(jsonDef);
		return tm;
	}
	
	public static TrustManager getInstance(){
		return tm;
	}
	
	public boolean removeInstance(){
		if (tm != null){
			
			
			//slaReqScheduler.stop_while();
			metricSheduler.stop_while();
			//scoreScheduler.stop_while();
			
			tm = null;
			return true;
		}else
			return false;
	}
	
	public boolean initSystem(){
				
		//slar.doTask();
		//TODO
		metr.doTask();
		//scorem.doTask();
		
		
		
		
		metricSheduler = new Scheduler(metr, 120000);
		metricSheduler.start();
		
		//scoreScheduler =new Scheduler(scorem, 120000);
		//scoreScheduler.start();
		
		return true;
	}

}

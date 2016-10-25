package eu.vital.TrustManager.params;

import java.util.HashMap;

/**
 * @author adminuser
 * this class stores info needed to simulate values of the metrics of each sla. the info is:
 * sla name, int value of the VARIANCE used in simulation.
 */
public class SLAconstants {
	
	static HashMap<String, String[]> slas;
	
	public final static String[] USED_MEM ={ "http://vital-iot.eu/ontology/ns/UsedMem","20"};
	public final static String[] AVA_MEM ={ "http://vital-iot.eu/ontology/ns/AvailableMem","15"};
	public final static String[] AVA_DISK={ "http://vital-iot.eu/ontology/ns/AvailableDisk","30"};
	public final static String[] SYS_LOAD={ "http://vital-iot.eu/ontology/ns/SysLoad","25"};
	public final static String[] SERVED_REQ={ "http://vital-iot.eu/ontology/ns/ServedRequests","20"};
	public final static String[] ERRORS={ "http://vital-iot.eu/ontology/ns/Errors","2"};
	public final static String[] SLA_SYSUPTIME ={ "Http://vital-iot.eu/ontology/ns/SysUptime","10"};
	public final static String[] PENDING_REQ = {"http://vital-iot.eu/ontology/ns/PendingRequests","10"};

	private static SLAconstants slast= null;
	private SLAconstants (){
		slas =new HashMap<String, String[]>();
		slas.put(USED_MEM[0], USED_MEM);
		slas.put(AVA_MEM[0], USED_MEM);
		slas.put(AVA_DISK[0], AVA_DISK);
		slas.put(SYS_LOAD[0], SYS_LOAD);
		slas.put(SERVED_REQ[0], SERVED_REQ);
		slas.put(ERRORS[0], ERRORS);
		slas.put(SLA_SYSUPTIME[0], SLA_SYSUPTIME);
		slas.put(PENDING_REQ[0], PENDING_REQ);
		
	}
	
	public static HashMap<String, String[]> getSLAHM(){
		if (slast == null)
			slast = new SLAconstants();
		return slas;
	}
}

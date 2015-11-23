package clients;

import java.io.IOException;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import utils.ConfigReader;

public class OpenAMClient {

	private ConfigReader configReader;
	private String idpHost;
	private String snmpPort;
	
	public OpenAMClient() {
		configReader = ConfigReader.getInstance();
		idpHost = configReader.get(ConfigReader.IDP_HOST);
		snmpPort = configReader.get(ConfigReader.SNMP_PORT);
	}
	
	public String getStatValue(String oidValue) {
		
		String answer = null;

		int snmpVersion  = SnmpConstants.version2c;
		String community = "public";

	    // Create TransportMapping and Listen
	    TransportMapping transport = null;
		try {
			transport = new DefaultUdpTransportMapping();
			transport.listen();
		} catch (IOException e) {
			e.printStackTrace();
		}

	    // Create Target Address object
	    CommunityTarget comtarget = new CommunityTarget();
	    comtarget.setCommunity(new OctetString(community));
	    comtarget.setVersion(snmpVersion);
	    comtarget.setAddress(new UdpAddress(idpHost + "/" + snmpPort));
	    comtarget.setRetries(3);
	    comtarget.setTimeout(2000);

	    // Create the PDU object
	    PDU pdu = new PDU();
	    pdu.add(new VariableBinding(new OID(oidValue)));
	    pdu.setType(PDU.GET);
	    pdu.setRequestID(new Integer32(1));

		// Create SNMP object for sending data to Agent
		Snmp snmp = new Snmp(transport);

		ResponseEvent response = null;
		try {
			response = snmp.get(pdu, comtarget);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Process Agent Response
		if(response != null) {
			PDU responsePDU = response.getResponse();
			if(responsePDU != null) {
				int errorStatus = responsePDU.getErrorStatus();
		        String errorStatusText = responsePDU.getErrorStatusText();

		        if(errorStatus == PDU.noError) {
		        	answer = responsePDU.getVariableBindings().firstElement().toString();
		        	String delims = "[=]";
		        	answer = answer.split(delims)[1].substring(1);
		        }
		        else {
		        	answer = "{ \"message\": \"" + errorStatusText + "\" }";
		        }
			}
		    else {
		    	answer = "{ \"message\": \"" + "Response PDU is null" + "\" }";
		    }
		}
		else {
			answer = "{ \"message\": \"" + "Agent Timeout" + "\" }";
		}
		try {
			snmp.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return answer;   
	}

}

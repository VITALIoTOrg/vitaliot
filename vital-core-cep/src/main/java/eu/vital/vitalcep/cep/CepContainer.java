package eu.vital.vitalcep.cep;

import java.util.HashMap;

public class CepContainer {
	
	private CepContainer(){}
	
	private static HashMap<String, CepProcess> cepProcMap = new HashMap();
	
	public static boolean putCepProc (CepProcess cp){
		cepProcMap.put((new Integer(cp.PID)).toString(), cp);
		
		return true;
	}
	
	public static CepProcess getCepProc(int PID){
		CepProcess cp = null;
		
		cp = cepProcMap.get((new Integer(PID)).toString());
		
		return cp;
		
	}
	
	public static boolean deleteCepProcess (int PID){
		if ((cepProcMap.remove((new Integer(PID)).toString()))== null)
			return false;
		else
			return true;
	}

}

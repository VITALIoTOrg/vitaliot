package net.atos.ari.vital;


import net.atos.ari.vital.tassproxy.TaaSBDMClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/service")
public class TrustManagerRestService {
/*EGO perhaps nothing has to be done	private static Logger logger = LoggerFactory.getLogger(TrustManagerRestService.class);

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public @ResponseBody Double getTrust (@PathVariable(value="id") String idThingsService){
		logger.info("StartOf getTrust  /{}",idThingsService);
		TaaSBDMClient myClient = TaaSBDMClient.instance();
		ThingServiceTrust trustValue = myClient.getTrustData(idThingsService);
		if (trustValue == null)
		{
			logger.error("No trust calculated for " + idThingsService + " was found -> Default value provided");
			return 2.0;
		}
		Double result = trustValue.getThingServiceTrust();
		
		logger.info("EndOf getTrust  /{}",idThingsService);
		return result;
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	public Boolean registerThingsService (@PathVariable(value="id")String idThingsService){
		logger.info("StartOf registerThingsService /{}",idThingsService);
		// there was no code for it in TaaSTrustManagerImpl
		logger.info("Taas Trust Manager Service registering Things Service " + idThingsService);
		return true;
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public Boolean removeThingsService (@PathVariable(value="id")  String idThingsService){
		logger.info("StartOf removeThingsService /{}",idThingsService);
		// there was no code for it in TaaSTrustManagerImpl
		boolean result = true;
		logger.debug("Taas Trust Manager Service removing Things Service " + idThingsService);
		logger.info("EndOf removeThingsService /{}",idThingsService);
		return result;
	}
	
	@RequestMapping(value = "/{id}/subscribe", method = RequestMethod.POST)
	public Boolean subscribeThreshold (@PathVariable(value="id")  String idThingsService, @RequestParam(required=true, value="id") double threshold){
		logger.info("StartOf subscribeThreshold /{}",idThingsService);
		TrustTaaSThread activeThread = TrustTaaSThread.instance();
		boolean result = activeThread.subscribeThreshold(threshold, idThingsService);;
		logger.info("EndOf subscribeThreshold /{}",idThingsService);
		return result;
	}
	
	@RequestMapping(value = "/{id}/subscribe", method = RequestMethod.DELETE)
	public Boolean removeThreshold (@PathVariable(value="id")  String idThingsService){
		logger.info("StartOf subscribeThreshold /{}",idThingsService);
		TrustTaaSThread activeThread = TrustTaaSThread.instance();
		boolean result= activeThread.removeThreshold(idThingsService);
		logger.info("EndOf removeThreshold /{}",idThingsService);
		return result;
	}*/
	
}
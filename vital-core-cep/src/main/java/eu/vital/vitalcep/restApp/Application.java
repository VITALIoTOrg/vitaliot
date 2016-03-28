package eu.vital.vitalcep.restApp;

import eu.vital.vitalcep.collector.Collector;
import eu.vital.vitalcep.restApp.cepRESTApi.CEPICO;
import eu.vital.vitalcep.restApp.filteringApi.ContinuosFiltering;
import eu.vital.vitalcep.restApp.vuaippi.VUAIPPI;
import eu.vital.vitalcep.restApp.filteringApi.StaticFiltering;
import eu.vital.vitalcep.restApp.filteringApi.PusshingDMS;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;

/**
 * The JAX-RS application.
 * 
 * @author a601149
 *
 */
@ApplicationPath("/cep")

public class Application extends javax.ws.rs.core.Application {
    
    private Set<Object> singletons = new HashSet();
    private Set<Class<?>> empty = new HashSet();
    
    public Application() throws FileNotFoundException, IOException {
        // ADD YOUR RESTFUL RESOURCES HERE
        this.singletons.add(new Collector());
        this.singletons.add(new StaticFiltering());
        this.singletons.add(new eu.vital.vitalcep.restApp.vuaippi.System());
        this.singletons.add(new eu.vital.vitalcep.restApp.vuaippi.Service());
        this.singletons.add(new eu.vital.vitalcep.restApp.vuaippi.Sensor());
        this.singletons.add(new eu.vital.vitalcep.restApp.vuaippi.Observation());
        this.singletons.add(new ContinuosFiltering());
        this.singletons.add(new CEPICO());
        this.singletons.add(new PusshingDMS());
   
    }
 
    public Set<Class<?>> getClasses()
    {
        return this.empty;
    }
 
    public Set<Object> getSingletons()
    {
        return this.singletons;
    }
    

        
}
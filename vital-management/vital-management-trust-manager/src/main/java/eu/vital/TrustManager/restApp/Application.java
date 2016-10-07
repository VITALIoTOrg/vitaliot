package eu.vital.TrustManager.restApp;

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
@ApplicationPath("/trust-manager")

public class Application extends javax.ws.rs.core.Application {
    
    private Set<Object> singletons = new HashSet();
    private Set<Class<?>> empty = new HashSet();
    
    public Application() throws FileNotFoundException, IOException {

        this.empty.add(CORSResponseFilter.class);
        
        this.singletons.add(new TRUSTMANAGER());
   
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
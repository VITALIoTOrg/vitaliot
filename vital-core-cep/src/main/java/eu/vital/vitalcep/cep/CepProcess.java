/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.vital.vitalcep.cep;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.logging.Level;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;

import eu.vital.vitalcep.conf.ConfigReader;

/**
 *
 * @author a601149
 */
public class CepProcess {
    
    public int PID;
    
    public String dolce;
    
    public String cepFolder;
      
    public boolean isUp;
    
    public String mqin;
    
    public String mqout;
    
    public String fileName;
    
    public String confFile;
    
    private Logger logger = null;
    
    public InputStream is= null;
    public InputStream es= null;
    
    BufferedInputStream bsr = null;
    
    Buffer_eraser be = null;
    
    /**
     *
     * @param dolce
     * @param mqin
     * @param mqout
     * @throws java.io.IOException
     */
    public CepProcess(String dolce, String mqin, String mqout, String confFile) 
            throws IOException{
        
    	logger = Logger.getLogger(this.getClass().getName());
        
    	
        ConfigReader configReader = ConfigReader.getInstance();
        cepFolder = configReader.get(ConfigReader.UCEP_PATH);
        
        this.dolce = dolce;
        this.mqin = mqin;
        this.mqout = mqout;
        this.confFile = confFile;

    }
    
    public void startCEP() throws FileNotFoundException, IOException {
                              
        this.fileName = RandomStringUtils.randomAlphanumeric(8);
        
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(cepFolder//+"/"+dolceFile
                                +"/"+fileName+"_dolce"), "utf-8"))) {
            writer.write(dolce);
            writer.close();
            
            String cmd = cepFolder + "/bcep -d " + cepFolder 
                + "/"+fileName+"_dolce -mi " + mqin + " -mo "
                +mqout+ " -f " + cepFolder + "/" + confFile + " &>/dev/null &";
            
            logger.debug("starting bCEP with command: " + cmd);
            try {
            	
            	Process pr = Runtime.getRuntime().exec(cmd);
            	
            	is = pr.getInputStream();
            	es = pr.getErrorStream();
            	
            	be = new Buffer_eraser();
            	be.start();
                
                               
                PID = getPid(pr);
                
                
                if (PID==-1){
                    java.util.logging.Logger.getLogger
                           (CepProcess.class.getName())
                                   .log(Level.SEVERE, "couldn't create the process" );
                    isUp = false;
                }else{
                    isUp = true;
                    logger.debug("new bCEP created: "+ PID);
                    logger.debug("mqin: " + mqin);
                    logger.debug("mqout: " + mqout);
                }
            } catch (IOException e) {
                java.util.logging.Logger.getLogger
                           (CepProcess.class.getName())
                                   .log(Level.SEVERE, e.getMessage());
                PID = -3;
                isUp = false;

            }
        } catch (IOException ex) {
            PID = -2;
            isUp = false;
            this.fileName = "";
            java.util.logging.Logger.getLogger
                           (CepProcess.class.getName())
                                   .log(Level.SEVERE, ex.getMessage());
        } 
        
    }
    
    public void stopCEP() throws FileNotFoundException, IOException {
                       
        String cmd = "kill -9 " + Integer.toString(PID);
        be.stop_while();
        
        try {
            Process pr = Runtime.getRuntime()
                    .exec(cmd);
            
            if(pr.exitValue()==0){
                logger.debug("bCEP stoped: " + PID);
            	PID = -1;
                
                try{
                    File file = new File(cepFolder//+"/"+dolceFile
                                    +"/"+fileName+"_dolce");
                    file.delete();
                }catch(Exception e){
                }
            }
        } catch (IOException e) {

        }

    }
    
    public static Boolean static_stopCEP(int PID, String cepFolder,
            String fileName) throws FileNotFoundException, IOException {
                       
    	String cmd = "kill -9 " + Integer.toString(PID);

        try {
            Process pr = Runtime.getRuntime()
                    .exec(cmd);
            if(pr.exitValue()==0){
                PID = -1;
                try{
                    File file = new File(cepFolder//+"/"+dolceFile
                                    +"/"+fileName+"_dolce");
                    file.delete();
                }catch(Exception e){
                }
            }
        } catch (IOException e) {
            return false;
        }
        
        return true;

    }
    
    
    private int getPid(Process process) {
        try {
            
            Class<?> cProcessImpl = process.getClass();
            java.lang.reflect.Field fPid = cProcessImpl.getDeclaredField("pid");
            
            if (!fPid.isAccessible()) {
                fPid.setAccessible(true);
            }else{
                return -1;
            }
            
            return fPid.getInt(process);
        } catch (NoSuchFieldException | SecurityException | 
                IllegalArgumentException | IllegalAccessException e) {
            return -2;
        }
    }
    
    public void freeBSR(){
    	
    	try {
    		logger.debug ("MIGULE, bytes disponibles en stdin del cep: " +bsr.available());
			bsr.reset();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.debug(e.toString());
		}
    	
    }
    
    public class Buffer_eraser extends Thread{
    	boolean execute = true;
    	
        @Override
        public void run(){
            try{
            	int c=-1;
            	int d=-1;
            
            	while (execute){
            		while (((c= is.read()) !=1)||((d= es.read()) !=1)){
            			if (c!=-1)
            				logger.debug("cep std buffer read: " + c);
            			if (d!=-1)
            				logger.debug("cep err buffer read: " + d);
            		}
            	}
            }catch(Exception e){
            	logger.error(e);
            }
        }
        
        public boolean stop_while(){
        	execute = false;
        	return true;
        }
    	
    }
    
}

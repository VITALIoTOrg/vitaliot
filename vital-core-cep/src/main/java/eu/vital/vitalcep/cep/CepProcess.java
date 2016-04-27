/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.vital.vitalcep.cep;

import eu.vital.vitalcep.conf.ConfigReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.logging.Level;
import org.apache.commons.lang.RandomStringUtils;

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
    
    
    /**
     *
     * @param dolce
     * @param mqin
     * @param mqout
     * @throws java.io.IOException
     */
    public CepProcess(String dolce, String mqin, String mqout) 
            throws IOException{
        
        ConfigReader configReader = ConfigReader.getInstance();
        cepFolder = configReader.get(ConfigReader.UCEP_PATH);
        
        this.dolce = dolce;
        this.mqin = mqin;
        this.mqout = mqout;

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
                +mqout+ " nohop &";
              
            try {
                Process pr = Runtime.getRuntime()
                        .exec(cmd);
                PID = getPid(pr);
                if (PID==-1){
                    java.util.logging.Logger.getLogger
                           (CepProcess.class.getName())
                                   .log(Level.SEVERE, "couldn't create the process" );
                    isUp = false;
                }else{
                    isUp = true;
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

        }

    }
    
    public static Boolean stopCEP(int PID, String cepFolder,
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
    
    
    private static int getPid(Process process) {
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
    
}

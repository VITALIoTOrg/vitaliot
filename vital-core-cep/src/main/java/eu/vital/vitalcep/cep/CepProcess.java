/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.vital.vitalcep.cep;

import eu.vital.vitalcep.conf.PropertyLoader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.apache.commons.lang.RandomStringUtils;

/**
 *
 * @author a601149
 */
public class CepProcess {
    
    public int PID;
    
    public String dolce;
    
    public String cepFolder;
    
    private PropertyLoader props; 
    
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
        
        props = new PropertyLoader();
        
        props.getProperty("cep.ip");
        
        cepFolder = props.getProperty("cep.path");
                
        this.dolce = dolce;
        
        this.mqin = mqin;
        
        this.mqout = mqout;

    }
    
    public void startCEP() throws FileNotFoundException, IOException {
                       
        String fileName = RandomStringUtils.randomAlphanumeric(8);
        
        this.fileName = fileName;
        
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
                    isUp = false;
                }else{
                    isUp = true;
                }
            } catch (IOException e) {
                PID = -3;
                isUp = false;

            }
        } catch (IOException ex) {
            PID = -2;
            isUp = false;
            this.fileName = "";
        } 
    }
    
    public void stopCEP() throws FileNotFoundException, IOException {
                       
        String cmd = "kill -9 " + Integer.toString(PID);

        try {
            Process pr = Runtime.getRuntime()
                    .exec(cmd);
            PID = getPid(pr);
            
            if(PID == 0){
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
    
    
    private static int getPid(Process process) {
        try {
            
            Class<?> cProcessImpl = process.getClass();
            java.lang.reflect.Field fPid = cProcessImpl.getDeclaredField("pid");
            
            if (!fPid.isAccessible()) {
                fPid.setAccessible(true);
            }else{
                return 0;
            }
            
            return fPid.getInt(process);
        } catch (NoSuchFieldException | SecurityException | 
                IllegalArgumentException | IllegalAccessException e) {
            return -1;
        }
    }
    
}

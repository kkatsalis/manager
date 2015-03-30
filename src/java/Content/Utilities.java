/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Content;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import javax.swing.text.html.HTML;

import org.apache.commons.io.IOUtils;

import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author nitlab
 */
public class Utilities {
    
    private final static Logger LOGGER = Logger.getLogger(Utilities.class.getName()); 
  

    
    public static String remoteExecution(String slice,String command) throws IOException{
    
       String address="nitlab3.inf.uth.gr";
       String response="";
       
        // This block configure the logger with handler and formatter  
        LOGGER.setLevel(Level.ALL);
        FileHandler fh;
        fh = new FileHandler("manager.log");  
        LOGGER.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();  
        fh.setFormatter(formatter);   
        
        try{
            
             JSch jsch=new JSch();

            
             Session session=jsch.getSession(slice, address, 22);

             jsch.setKnownHosts("/home/nitlab/.ssh/known_hosts");
             jsch.addIdentity("/home/nitlab/.ssh/id_rsa");
   
   // If two machines have SSH passwordless logins setup, the following line is not needed:
             //    session.setPassword("YOURPASSWORD");
                session.setConfig("StrictHostKeyChecking", "no");
                 session.connect();


                 Channel channel=session.openChannel("exec");
                 ((ChannelExec)channel).setCommand(command);

                 //channel.setInputStream(System.in);
                 channel.setInputStream(null);
                 ((ChannelExec)channel).setErrStream(System.err);

                 InputStream in=channel.getInputStream();
                 channel.connect();
                  response = IOUtils.toString(in);

                 byte[] tmp=new byte[1024];
                 while(true){
                   while(in.available()>0){
                     int i=in.read(tmp, 0, 1024);
                     if(i<0)break;
                     System.out.print(new String(tmp, 0, i));
                   }
                   if(channel.isClosed()){
                     System.out.println("exit-status: "+channel.getExitStatus());
                     break;
                   }
                   try{Thread.sleep(1000);}catch(Exception ee){ee.printStackTrace();}
                 }
                 channel.disconnect();
                 session.disconnect();
               }
               catch(Exception e){
                 System.out.println(e);
                
                 LOGGER.log(Level.INFO,e.toString());
              }
 
        return response;
  
          }       //end main

  public static String executeImageLoad(String slice,String node) throws IOException{
    
     //OMF 5
     //command="omf load -i baseline.ndz -t omf.nitos."+node;
    String command ="omf6 load -t "+node+" -i baseline.ndz ";    
    String response=Utilities.remoteExecution(slice,command);
    String jsonResponse="";         
             
    if(response.toLowerCase().contains("Load proccess completed".toLowerCase()))
       response="success";
    else 
       response="failure";
             
        jsonResponse="{\"node\":\""+node+"\",\"action\":\""+"imageLoad"+"\",\"status\":\""+response+"\"}";

             return jsonResponse;
    }    

    public static String executeAction(String slice,String node, String action) throws IOException{
    
        String command="omf6 tell -a "+action+" -t "+node;
        String jsonResponse="";           
        String  response=Utilities.remoteExecution(slice,command);
            
        if(response.toLowerCase().contains("Proccess complete".toLowerCase()))
           response="success";
        else 
            response="failure";
            
            jsonResponse="{\"node\":\""+node+"\",\"action\":\""+action+"\",\"status\":\""+response+"\"}";
 
            return jsonResponse;
    }
    
    public static String findCMStatus(String slice,String node) throws IOException{
    
           String command="omf6 stat -t "+node;
           String jsonResponse="";     
           
           String  response=remoteExecution(slice,command);
            
           if(response.toLowerCase().contains("status is: on".toLowerCase()))
              response="on";
           else 
              response="off";
            
           jsonResponse="{\"node\":\""+node+"\",\"action\":\""+"nodeStatus"+"\",\"status\":\""+response+"\"}";
          
           return jsonResponse;
    }

    public static String createNetworkConfig(String slice,String node, Hashtable<String,String> parameters){
    
        String response="";
        String nodeAddress="root@"+node;
        String command="ssh -oStrictHostKeyChecking=no $h "+node+" echo 'kostas'";
        
        
        return response;
    }
}

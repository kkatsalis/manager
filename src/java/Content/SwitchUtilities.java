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
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author nitlab
 */
public class SwitchUtilities {
    
      private final static Logger LOGGER = Logger.getLogger(SwitchUtilities.class.getName()); 
  
   
     public static String remoteExecution(String user,String command) throws IOException{
    
      String address="10.64.92.123";
      
      String response="";
       
        // This block configure the logger with handler and formatter  
        LOGGER.setLevel(Level.ALL);
        FileHandler fh;
        fh = new FileHandler("switch-manager.log");  
        LOGGER.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();  
        fh.setFormatter(formatter);   
        
        try{
            
             JSch jsch=new JSch();

             Session session=jsch.getSession(user, address, 22);
   
             jsch.setKnownHosts("/home/nitlab/.ssh/known_hosts");
             jsch.addIdentity("/home/nitlab/.ssh/id_rsa");
             
   // If two machines have SSH passwordless logins setup, the following line is not needed:
                session.setPassword("kon$ol[");
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
}

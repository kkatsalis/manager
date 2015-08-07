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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import javax.swing.text.html.HTML;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;

import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.json.JsonArray;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    public static String executeImageLoad(String slice,String node, String omfVersion) throws IOException{
    
        String command;
        String response;
        String jsonResponse="";   

//        Properties property=new Properties();
//        String filename="manager.properties";
//        InputStream input=Utilities.class.getClassLoader().getResourceAsStream(filename);
//        property.load(input);

         if(omfVersion.equals("omf5")){
            command="omf load -i baseline.ndz -t omf.nitos."+node;
            response=Utilities.remoteExecution(slice,command);

            if(response.toLowerCase().contains("node successfully imaged".toLowerCase()))
               response="success";
            else 
               response="failure";

            jsonResponse="{\"node\":\""+node+"\",\"action\":\""+"imageLoad"+"\",\"status\":\""+response+"\"}";
         }
         if(omfVersion.equals("omf6")){
           command ="omf6 load -t "+node+" -i baseline.ndz ";
            response=Utilities.remoteExecution(slice,command);

            if(response.toLowerCase().contains("Load proccess completed".toLowerCase()))
               response="success";
            else 
               response="failure";

            jsonResponse="{\"node\":\""+node+"\",\"action\":\""+"imageLoad"+"\",\"status\":\""+response+"\"}";
         }
    
             return jsonResponse;
    }    

    public static String cmExecuteAction(String slice,String node, String action, String omf) throws IOException{
    
        String command;
        String response;
        String jsonResponse="";   

//        Properties property=new Properties();
//        String filename="manager.properties";
//        InputStream input=Utilities.class.getClassLoader().getResourceAsStream(filename);
//        property.load(input);
//
//        String framework=(String)property.getProperty("omf");
          
     if(omf.equals("omf5")){
        command="omf tell -a "+action+"-t omf.nitos."+node;
        response=Utilities.remoteExecution(slice,command);
        
        if(response.toLowerCase().contains("".toLowerCase()))
           response="success";
        else 
           response="failure";
             
        jsonResponse="{\"node\":\""+node+"\",\"action\":\""+"imageLoad"+"\",\"status\":\""+response+"\"}";
     }
     if(omf.equals("omf6")){
       
         if(action.equals("reboot"))
           action="reset";
       
        command="omf6 tell -a "+action+" -t "+node;
       
        response=Utilities.remoteExecution(slice,command);
        
        if(response.toLowerCase().contains("Proccess complete".toLowerCase()))
           response="success";
        else 
           response="failure";
             
           jsonResponse="{\"node\":\""+node+"\",\"action\":\""+action+"\",\"status\":\""+response+"\"}";     
     }
            
            return jsonResponse;
    }
    
    public static String cmStatus(String slice,String node, String omf) throws IOException{
    
        String jsonResponse="";  
        
         if(omf.equals("omf5")){
         jsonResponse="{\"node\":\""+node+"\",\"action\":\""+"nodeStatus"+"\",\"status\":\""+"not supported"+"\"}";
          
           return jsonResponse;
         }
         else if(omf.equals("omf6")){ 
       
           String command="omf6 stat -t "+node;
           
           String  response=remoteExecution(slice,command);
            
           if(response.toLowerCase().contains("status is: on".toLowerCase()))
              response="on";
           else 
              response="off";
            
           jsonResponse="{\"node\":\""+node+"\",\"action\":\""+"status"+"\",\"status\":\""+response+"\"}";
         }
           return jsonResponse;
    }

    static Boolean prepareNodeNetworkConfig(String slice, String node) {
     
        String command="";
        
        String prefix="ssh -oStrictHostKeyChecking=no $h root@"+node+" ";
        
        try {
          
            command=prefix+"apt-get update";
            Utilities.remoteExecution(slice, command);
            command=prefix+"apt-get install vlan";
            Utilities.remoteExecution(slice, command);
            command=prefix+"modprobe 8021q";
            Utilities.remoteExecution(slice, command);
            command=prefix+"apt-get update";
            Utilities.remoteExecution(slice, command);
            command=prefix+"apt-get install bridge-utils";
            Utilities.remoteExecution(slice, command);
            command=prefix+"modprobe ath9k";
            Utilities.remoteExecution(slice, command);
        
        }  catch (Exception e) {
            
            System.out.println(e);
            LOGGER.log(Level.INFO,e.toString());
          
        }
       return true;
        
    }

    public static String createNodeNetworkConfig(String slice,String node, Hashtable<String,String> parameters){
    
        String command="";
        String jsonResponse="{\"node\":\""+node+"\",\"action\":\""+"networkConfiguration"+"\",\"status\":\""+"failure"+"\"}"; 
        
        String prefix="ssh -oStrictHostKeyChecking=no $h root@"+node+" ";
        
        try {
                      
           
            command=prefix+"ifconfig wlan0 up";
            Utilities.remoteExecution(slice, command);
            command=prefix+"ifconfig eth1 up";
            Utilities.remoteExecution(slice, command);
            command=prefix+"vconfig add eth1 "+parameters.get("vlan").toString();
            Utilities.remoteExecution(slice, command);
            command=prefix+"ip link set eth1."+parameters.get("vlan").toString()+" up";
            Utilities.remoteExecution(slice, command);
            command=prefix+"brctl addbr "+parameters.get("bridge").toString();
            Utilities.remoteExecution(slice, command);
            command=prefix+"brctl addif "+parameters.get("bridge").toString()+" eth1."+parameters.get("vlan").toString();
            Utilities.remoteExecution(slice, command);
            command=prefix+"ip link set up dev "+parameters.get("bridge").toString();
            Utilities.remoteExecution(slice, command);
            command=prefix+"ip link set up dev "+parameters.get("bridge").toString();
            Utilities.remoteExecution(slice, command);
            command=prefix+"ifconfig "+parameters.get("bridge").toString()+" "+parameters.get("address").toString()+" netmask "+parameters.get("netmask").toString();
            Utilities.remoteExecution(slice, command);

            jsonResponse="{\"node\":\""+node+"\",\"action\":\""+"networkConfiguration"+"\",\"status\":\""+"success"+"\"}";
          
           
        } catch (Exception e) {
            
            System.out.println(e);
            LOGGER.log(Level.INFO,e.toString());
            
            
        }
       return jsonResponse;
    }

   
    static String createAccessPointConfig(String slice, String nodeID, Hashtable<String, String> parameters) {
               
        String command="";
        String jsonResponse="{\"node\":\""+nodeID+"\",\"action\":\""+"networkConfiguration"+"\",\"status\":\""+"failure"+"\"}"; 
        
           
        String prefix="ssh -oStrictHostKeyChecking=no $h root@"+nodeID+" ";
        
        List<String> lines=new ArrayList<String>();
        lines.add("interface="+parameters.get("intrface").toString());
        lines.add("bridge="+parameters.get("bridge").toString());
        lines.add("driver="+parameters.get("driver").toString());
        lines.add("ssid="+parameters.get("ssid").toString());
        lines.add("channel="+parameters.get("channel").toString());
        lines.add("hw_mode="+parameters.get("hw_mode").toString());
        lines.add("wmm_enabled="+parameters.get("wmm_enabled").toString());
        lines.add("ieee80211n="+parameters.get("ieee80211n").toString());
        lines.add("ht_capab="+parameters.get("ht_capab").toString());
          
                
        try {
            //STEP 1: Install hostapd
            command=prefix+"apt-get update";
            Utilities.remoteExecution(slice, command);
            
            command=prefix+"apt-get install hostapd";
            Utilities.remoteExecution(slice, command);
            
             //STEP 2: Create hostapd file
            command=prefix+"touch /etc/hostapd/hostapd.conf";
            Utilities.remoteExecution(slice, command);
            String string;
            
            for (Iterator<String> it = lines.iterator(); it.hasNext();) {
                string = it.next();
                command="echo "+string+" |"+prefix+" 'cat>>/etc/hostapd/hostapd.conf'";
                Utilities.remoteExecution(slice, command);
            }
           
           //STEP 3: Make Hostapd Service and run
            String line="DAEMON_CONF=\"/etc/hostapd/hostapd.conf\"";
            command="echo "+line+" |"+prefix+" 'cat>>/etc/default/hostapd'";
            Utilities.remoteExecution(slice, command);
           
            command=prefix+"'service hostapd start'";
            Utilities.remoteExecution(slice, command);
            

            jsonResponse="{\"node\":\""+nodeID+"\",\"action\":\""+"networkConfiguration"+"\",\"status\":\""+"success"+"\"}";
          
           
        } catch (Exception e) {
            
            System.out.println(e);
            LOGGER.log(Level.INFO,e.toString());
            
            
        }
       return jsonResponse;
        
    }
    
    static public List<String> parseHostApdConfig(JSONObject body){
    
        JsonHostApd hostApd=new JsonHostApd();
        List<String> hostApdLines=new ArrayList<String>();
        
        try {
         
            hostApd.setChannel(body.getInt("channel"));
            hostApd.setHw_mode(body.getString("hw_mode"));
            hostApd.setDriver(body.getString("driver"));
            hostApd.setWmm_enabled(body.getInt("wmm_enabled"));
            hostApd.setIeee80211n(body.getInt("ieee80211n"));
            hostApd.setBeacon_int(body.getInt("beacon_int"));
            hostApd.setMax_num_sta(body.getInt("max_num_sta"));
            hostApd.setHt_capab(body.getString("ht_capab"));
        
            hostApdLines.add("channel="+String.valueOf(hostApd.getChannel()));
            hostApdLines.add("hw_mode="+String.valueOf(hostApd.getHw_mode()));
            hostApdLines.add("driver="+String.valueOf(hostApd.getDriver()));
            hostApdLines.add("wmm_enabled="+String.valueOf(hostApd.getWmm_enabled()));
            hostApdLines.add("ieee80211n="+String.valueOf(hostApd.getIeee80211n()));
            hostApdLines.add("beacon_int="+String.valueOf(hostApd.getBeacon_int()));
            hostApdLines.add("ht_capab="+String.valueOf(hostApd.getHt_capab()));
            
            if(hostApdLines.isEmpty())
                return null;
            
        } catch (JSONException ex) {
            Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        return hostApdLines;
        
    }
    
    static public List<String> parseVapsConfig(JSONObject body) throws JSONException{
    
        List<JsonVap> vaps=new ArrayList<JsonVap>();
        List<String> vapsLines=new ArrayList<String>();
     
        JSONArray vapArray=body.getJSONArray("virtual-access-points");
        int vap_number=vapArray.length();
        JSONObject vap ;
        
        for (int i = 0; i < vap_number; ++i) {
            vap = vapArray.getJSONObject(i);
            vaps.add(new JsonVap());
           
            vaps.get(i).setId(i);
            vaps.get(i).setVlan(vap.getInt("vlan"));
            vaps.get(i).setMax_customers(vap.getInt("max-customers"));
            vaps.get(i).setMax_rate(vap.getDouble("max-rate"));
            vaps.get(i).setMin_rate(vap.getDouble("min-rate"));
            vaps.get(i).setRatio_rate(vap.getDouble("ratio-rate"));
            vaps.get(i).setSsid(vap.getString("ssid"));
            vaps.get(i).setPassword(vap.getString("password"));
            vaps.get(i).setNetwork(vap.getString("network"));  
            vaps.get(i).setNetMask(vap.getString("netmask"));  
 
            
        }
             
        
        return vapsLines;
        
    }
    
    public static List<String> parseApQosConfig(JSONObject body){
    
       JsonApQoSParams qosObject=new JsonApQoSParams();
       List<String> qosLines=new ArrayList<String>();
       
        try {
        
            qosObject.setWmm_ac_bk_cwmin(body.getDouble("wmm_ac_bk_cwmin"));
            qosObject.setWmm_ac_bk_cwmax(body.getDouble("wmm_ac_bk_cwmax"));
            qosObject.setWmm_ac_bk_aifs(body.getDouble("wmm_ac_bk_aifs"));
            qosObject.setWmm_ac_bk_txop_limit(body.getDouble("wmm_ac_bk_txop_limit"));
            qosObject.setWmm_ac_bk_acm(body.getDouble("wmm_ac_bk_acm"));

            qosObject.setWmm_ac_be_aifs(body.getDouble("wmm_ac_be_aifs"));
            qosObject.setWmm_ac_be_cwmin(body.getDouble("wmm_ac_be_cwmin"));
            qosObject.setWmm_ac_be_cwmax(body.getDouble("wmm_ac_be_cwmax"));
            qosObject.setWmm_ac_be_txop_limit(body.getDouble("wmm_ac_be_txop_limit"));
            qosObject.setWmm_ac_be_acm(body.getDouble("wmm_ac_be_acm"));

            qosObject.setWmm_ac_vi_aifs(body.getDouble("wmm_ac_vi_aifs"));
            qosObject.setWmm_ac_vi_cwmin(body.getDouble("wmm_ac_vi_cwmin"));
            qosObject.setWmm_ac_vi_cwmax(body.getDouble("wmm_ac_vi_cwmax"));
            qosObject.setWmm_ac_vi_txop_limit(body.getDouble("wmm_ac_vi_txop_limit"));
            qosObject.setWmm_ac_vi_acm(body.getDouble("wmm_ac_vi_acm"));

            qosObject.setWmm_ac_vo_aifs(body.getDouble("wmm_ac_vo_aifs"));
            qosObject.setWmm_ac_vo_cwmin(body.getDouble("wmm_ac_vo_cwmin"));
            qosObject.setWmm_ac_vo_cwmax(body.getDouble("wmm_ac_vo_cwmax"));
            qosObject.setWmm_ac_vo_txop_limit(body.getDouble("wmm_ac_vo_txop_limit"));
            qosObject.setWmm_ac_vo_acm(body.getDouble("wmm_ac_vo_acm"));

            qosObject.setTx_queue_data3_aifs(body.getDouble("tx_queue_data3_aifs"));
            qosObject.setTx_queue_data3_cwmin(body.getDouble("tx_queue_data3_cwmin"));
            qosObject.setTx_queue_data3_cwmax(body.getDouble("tx_queue_data3_cwmax"));
            qosObject.setTx_queue_data3_burst(body.getDouble("tx_queue_data3_burst"));

            qosObject.setTx_queue_data2_aifs(body.getDouble("tx_queue_data2_aifs"));
            qosObject.setTx_queue_data2_cwmin(body.getDouble("tx_queue_data2_cwmin"));
            qosObject.setTx_queue_data2_cwmax(body.getDouble("tx_queue_data2_cwmax"));
            qosObject.setTx_queue_data2_burst(body.getDouble("tx_queue_data2_burst"));

            qosObject.setTx_queue_data1_aifs(body.getDouble("tx_queue_data1_aifs"));
            qosObject.setTx_queue_data1_cwmin(body.getDouble("tx_queue_data1_cwmin"));
            qosObject.setTx_queue_data1_cwmax(body.getDouble("tx_queue_data1_cwmax"));
            qosObject.setTx_queue_data1_burst(body.getDouble("tx_queue_data1_burst"));

            qosObject.setTx_queue_data0_aifs(body.getDouble("tx_queue_data0_aifs"));
            qosObject.setTx_queue_data0_cwmin(body.getDouble("tx_queue_data0_cwmin"));
            qosObject.setTx_queue_data0_cwmax(body.getDouble("tx_queue_data0_cwmax"));
            qosObject.setTx_queue_data0_burst(body.getDouble("tx_queue_data0_burst"));
       
             ///////////// Lines
            
            qosLines.add("wmm_ac_bk_cwmin="+String.valueOf(qosObject.getWmm_ac_bk_cwmin()));
            qosLines.add("wmm_ac_bk_cwmax="+String.valueOf(qosObject.getWmm_ac_bk_cwmax()));
            qosLines.add("wmm_ac_bk_aifs="+String.valueOf(qosObject.getWmm_ac_bk_aifs()));
            qosLines.add("wmm_ac_bk_txop_limit="+String.valueOf(qosObject.getWmm_ac_bk_txop_limit()));
            qosLines.add("wmm_ac_bk_acm="+String.valueOf(qosObject.getWmm_ac_bk_acm()));

            qosLines.add("wmm_ac_be_aifs="+String.valueOf(qosObject.getWmm_ac_be_aifs()));
            qosLines.add("wmm_ac_be_cwmin="+String.valueOf(qosObject.getWmm_ac_be_cwmin()));
            qosLines.add("wmm_ac_be_cwmax="+String.valueOf(qosObject.getWmm_ac_be_cwmax()));
            qosLines.add("wmm_ac_be_txop_limit="+String.valueOf(qosObject.getWmm_ac_be_txop_limit()));
            qosLines.add("wmm_ac_be_acm="+String.valueOf(qosObject.getWmm_ac_be_acm()));

            qosLines.add("wmm_ac_vi_aifs="+String.valueOf(qosObject.getWmm_ac_vi_aifs()));
            qosLines.add("wmm_ac_vi_cwmin="+String.valueOf(qosObject.getWmm_ac_vi_cwmin()));
            qosLines.add("wmm_ac_vi_cwmax="+String.valueOf(qosObject.getWmm_ac_vi_cwmax()));
            qosLines.add("wmm_ac_vi_txop_limit="+String.valueOf(qosObject.getWmm_ac_vi_txop_limit()));
            qosLines.add("wmm_ac_vi_acm="+String.valueOf(qosObject.getWmm_ac_vi_acm()));

            qosLines.add("wmm_ac_vo_aifs="+String.valueOf(qosObject.getWmm_ac_vo_aifs()));
            qosLines.add("wmm_ac_vo_cwmin="+String.valueOf(qosObject.getWmm_ac_vo_cwmin()));
            qosLines.add("wmm_ac_vo_cwmax="+String.valueOf(qosObject.getWmm_ac_vo_cwmax()));
            qosLines.add("wmm_ac_vo_txop_limit="+String.valueOf(qosObject.getWmm_ac_vo_txop_limit()));
            qosLines.add("wmm_ac_vo_acm="+String.valueOf(qosObject.getWmm_ac_vo_acm()));

            qosLines.add("tx_queue_data3_aifs="+String.valueOf(qosObject.getTx_queue_data3_aifs()));
            qosLines.add("tx_queue_data3_cwmin="+String.valueOf(qosObject.getTx_queue_data3_cwmin()));
            qosLines.add("tx_queue_data3_cwmax="+String.valueOf(qosObject.getTx_queue_data3_cwmax()));
            qosLines.add("tx_queue_data3_burst="+String.valueOf(qosObject.getTx_queue_data3_burst()));

            qosLines.add("tx_queue_data2_aifs="+String.valueOf(qosObject.getTx_queue_data2_aifs()));
            qosLines.add("tx_queue_data2_cwmin="+String.valueOf(qosObject.getTx_queue_data2_cwmin()));
            qosLines.add("tx_queue_data2_cwmax="+String.valueOf(qosObject.getTx_queue_data2_cwmax()));
            qosLines.add("tx_queue_data2_burst="+String.valueOf(qosObject.getTx_queue_data2_burst()));

            qosLines.add("tx_queue_data1_aifs="+String.valueOf(qosObject.getTx_queue_data1_aifs()));
            qosLines.add("tx_queue_data1_cwmin="+String.valueOf(qosObject.getTx_queue_data1_cwmin()));
            qosLines.add("tx_queue_data1_cwmax="+String.valueOf(qosObject.getTx_queue_data1_cwmax()));
            qosLines.add("tx_queue_data1_burst="+String.valueOf(qosObject.getTx_queue_data1_burst()));

            qosLines.add("tx_queue_data0_aifs="+String.valueOf(qosObject.getTx_queue_data0_aifs()));
            qosLines.add("tx_queue_data0_cwmin="+String.valueOf(qosObject.getTx_queue_data0_cwmin()));
            qosLines.add("tx_queue_data0_cwmax="+String.valueOf(qosObject.getTx_queue_data0_cwmax()));
            qosLines.add("tx_queue_data0_burst="+String.valueOf(qosObject.getTx_queue_data0_burst()));
            
            
            if(qosLines.isEmpty())
                return null;
            
       } catch (JSONException ex) {
            Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return qosLines;
        
    }
    
}

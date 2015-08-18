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

    static Hashtable prepareNodeLibraries(String slice, String node) {
     
        String command="";
        
        Hashtable response=new Hashtable();
        
        String prefix="ssh -oStrictHostKeyChecking=no $h root@"+node+" ";
        
        try {
          
            command=prefix+"apt-get update";
            Utilities.remoteExecution(slice, command);
            
            // install libraries
            command=prefix+"apt-get install vlan";
            Utilities.remoteExecution(slice, command);
            command=prefix+"apt-get install bridge-utils";
            Utilities.remoteExecution(slice, command);
            command=prefix+"apt-get install hostapd";
            Utilities.remoteExecution(slice, command);
           
            //modprobes
            command=prefix+"modprobe ath9k";
            Utilities.remoteExecution(slice, command);
            command=prefix+"modprobe 8021q";
            Utilities.remoteExecution(slice, command);
                       
            response.put("librariesAdded","yes");
        
        }  catch (Exception e) {
            
            System.out.println(e);
            LOGGER.log(Level.INFO,e.toString());
          
        }
        
        return response;
    }

    public static Hashtable createNodeNetworkConfig(String slice,String node, JsonHostApd hostApd,List<JsonVap> vaps){
    
        String command="";
        Hashtable responseParams=new Hashtable();
        
        String prefix="ssh -oStrictHostKeyChecking=no $h root@"+node+" ";
        
        try {
                      
            command=prefix+"ifconfig wlan0 up";
            Utilities.remoteExecution(slice, command);
            command=prefix+"ifconfig eth1 up";
            Utilities.remoteExecution(slice, command);
            
            for(int i=0;i<vaps.size();i++){
                
                //step 1 - add the neccessary bridges
                command=prefix+"brctl addbr br"+String.valueOf(i);
                Utilities.remoteExecution(slice, command);
                
                //step 2 - add vlans on the eth1 interface
                if(vaps.get(i).getVlan()>0){
                    command=prefix+"vconfig add eth1 "+String.valueOf(vaps.get(i).getVlan());
                    Utilities.remoteExecution(slice, command);

                     command=prefix+"ip link set eth1."+String.valueOf(vaps.get(i).getVlan())+" up";
                     Utilities.remoteExecution(slice, command);
                }
                       
                // step 3 - bring up all the bridges   
                command=prefix+"ip link set up dev br"+String.valueOf(i);
                Utilities.remoteExecution(slice, command);
                command=prefix+"ifconfig br"+String.valueOf(i)+" "+vaps.get(i).getNetwork()+" netmask "+vaps.get(i).getNetMask();
                Utilities.remoteExecution(slice, command);
                   
            }
            
            for(int i=0;i<vaps.size();i++){
                if(i==0&vaps.get(i).getVlan()<0)
                   command=prefix+"brctl addif br"+String.valueOf(i)+" eth1";
                else
                   command=prefix+"brctl addif br"+String.valueOf(i)+" eth1."+String.valueOf(vaps.get(i).getVlan());
               
                Utilities.remoteExecution(slice, command);
            }

          
           
        } catch (Exception e) {
            
            System.out.println(e);
            LOGGER.log(Level.INFO,e.toString());
            
            
        }
       return responseParams;
    }

    static Hashtable loadAndStartHostApdService(String slice, String nodeID, List<String> hostApdlines) {
               
        String command="";
        Hashtable response=new Hashtable(); 
        
           
        String prefix="ssh -oStrictHostKeyChecking=no $h root@"+nodeID+" ";
                
        try {
             // Create hostapd file
            command=prefix+"rm /etc/hostapd/hostapd.conf";
            Utilities.remoteExecution(slice, command);
            
            command=prefix+"touch /etc/hostapd/hostapd.conf";
            Utilities.remoteExecution(slice, command);

            // Add Lines to the hostapd file
            String string;
            
            for (Iterator<String> it = hostApdlines.iterator(); it.hasNext();) {
                string = it.next();
                command="echo "+string+" |"+prefix+" 'cat>>/etc/hostapd/hostapd.conf'";
                Utilities.remoteExecution(slice, command);
            }
           
           //STEP 3: Make Hostapd Service and run
            String line="DAEMON_CONF=\"/etc/hostapd/hostapd.conf\"";
            command="echo "+line+" |"+prefix+" 'cat>>/etc/default/hostapd'";
            Utilities.remoteExecution(slice, command);
            response.put("hostApdServiceCreated", "yes");
            
            command=prefix+"'service hostapd start'";
            Utilities.remoteExecution(slice, command);
            response.put("hostApdServiceStarted", "yes");

           
          
           
        } catch (Exception e) {
            
            System.out.println(e);
            LOGGER.log(Level.INFO,e.toString());
            
            
        }
       return response;
        
    }
    
    static public JsonHostApd parseHostApdConfig(JSONObject body){
    
        JsonHostApd hostApd=new JsonHostApd();
        
        try {
         
            hostApd.setChannel(body.getInt("channel"));
            hostApd.setHw_mode(body.getString("hw_mode"));
            hostApd.setDriver(body.getString("driver"));
            hostApd.setMax_num_sta(body.getInt("max_num_sta"));
            
        } catch (JSONException ex) {
            Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        return hostApd;
        
    }
    
    static public List<JsonVap> parseVapsConfig(JSONObject body) throws JSONException{
    
        List<JsonVap> vaps=new ArrayList<JsonVap>();
        List<JsonApQoSParams> vaps_qos=new ArrayList<JsonApQoSParams>();
        
        JSONArray vapArray=body.getJSONArray("virtual-access-points");
        int vap_number=vapArray.length();
        JSONObject vap ;
        
        for (int i = 0; i < vap_number; ++i) {
            vap = vapArray.getJSONObject(i);
            
            vaps.add(new JsonVap());
           
            vaps.get(i).setId(i);
            vaps.get(i).setSsid(vap.getString("ssid"));
            vaps.get(i).setPassword(vap.getString("password"));
            vaps.get(i).setVlan(vap.getInt("vlan"));
            vaps.get(i).setNetwork(vap.getString("network"));  
            vaps.get(i).setNetMask(vap.getString("netmask")); 
            vaps.get(i).setMax_rate(vap.getDouble("max-rate"));
            vaps.get(i).setMin_rate(vap.getDouble("min-rate"));
            vaps.get(i).setRatio_rate(vap.getDouble("ratio-rate"));
            vaps.get(i).setBeacon_int(vap.getInt("beacon_int"));
            vaps.get(i).setMax_num_sta(vap.getInt("max_num_sta"));
            
            vaps.get(i).setWmm_enabled(vap.getInt("wmm_enabled"));
            vaps.get(i).setIeee80211n(vap.getInt("ieee80211n"));
            vaps.get(i).setHt_capab(vap.getString("ht_capab"));
          
            if(vap.has("wmm_ac_bk_cwmin")){
                vaps.get(i).createQoSparameters();
                vaps.get(i).getQosParameters().setWmm_ac_bk_cwmin(vap.getDouble("wmm_ac_bk_cwmin"));
                vaps.get(i).getQosParameters().setWmm_ac_bk_cwmax(vap.getDouble("wmm_ac_bk_cwmax"));
                vaps.get(i).getQosParameters().setWmm_ac_bk_aifs(vap.getDouble("wmm_ac_bk_aifs"));
                vaps.get(i).getQosParameters().setWmm_ac_bk_txop_limit(vap.getDouble("wmm_ac_bk_txop_limit"));
                vaps.get(i).getQosParameters().setWmm_ac_bk_acm(vap.getDouble("wmm_ac_bk_acm"));

                vaps.get(i).getQosParameters().setWmm_ac_be_aifs(vap.getDouble("wmm_ac_be_aifs"));
                vaps.get(i).getQosParameters().setWmm_ac_be_cwmin(vap.getDouble("wmm_ac_be_cwmin"));
                vaps.get(i).getQosParameters().setWmm_ac_be_cwmax(vap.getDouble("wmm_ac_be_cwmax"));
                vaps.get(i).getQosParameters().setWmm_ac_be_txop_limit(vap.getDouble("wmm_ac_be_txop_limit"));
                vaps.get(i).getQosParameters().setWmm_ac_be_acm(vap.getDouble("wmm_ac_be_acm"));

                vaps.get(i).getQosParameters().setWmm_ac_vi_aifs(vap.getDouble("wmm_ac_vi_aifs"));
                vaps.get(i).getQosParameters().setWmm_ac_vi_cwmin(vap.getDouble("wmm_ac_vi_cwmin"));
                vaps.get(i).getQosParameters().setWmm_ac_vi_cwmax(vap.getDouble("wmm_ac_vi_cwmax"));
                vaps.get(i).getQosParameters().setWmm_ac_vi_txop_limit(vap.getDouble("wmm_ac_vi_txop_limit"));
                vaps.get(i).getQosParameters().setWmm_ac_vi_acm(vap.getDouble("wmm_ac_vi_acm"));

                vaps.get(i).getQosParameters().setWmm_ac_vo_aifs(vap.getDouble("wmm_ac_vo_aifs"));
                vaps.get(i).getQosParameters().setWmm_ac_vo_cwmin(vap.getDouble("wmm_ac_vo_cwmin"));
                vaps.get(i).getQosParameters().setWmm_ac_vo_cwmax(vap.getDouble("wmm_ac_vo_cwmax"));
                vaps.get(i).getQosParameters().setWmm_ac_vo_txop_limit(vap.getDouble("wmm_ac_vo_txop_limit"));
                vaps.get(i).getQosParameters().setWmm_ac_vo_acm(vap.getDouble("wmm_ac_vo_acm"));

                vaps.get(i).getQosParameters().setTx_queue_data3_aifs(vap.getDouble("tx_queue_data3_aifs"));
                vaps.get(i).getQosParameters().setTx_queue_data3_cwmin(vap.getDouble("tx_queue_data3_cwmin"));
                vaps.get(i).getQosParameters().setTx_queue_data3_cwmax(vap.getDouble("tx_queue_data3_cwmax"));
                vaps.get(i).getQosParameters().setTx_queue_data3_burst(vap.getDouble("tx_queue_data3_burst"));

                vaps.get(i).getQosParameters().setTx_queue_data2_aifs(vap.getDouble("tx_queue_data2_aifs"));
                vaps.get(i).getQosParameters().setTx_queue_data2_cwmin(vap.getDouble("tx_queue_data2_cwmin"));
                vaps.get(i).getQosParameters().setTx_queue_data2_cwmax(vap.getDouble("tx_queue_data2_cwmax"));
                vaps.get(i).getQosParameters().setTx_queue_data2_burst(vap.getDouble("tx_queue_data2_burst"));

                vaps.get(i).getQosParameters().setTx_queue_data1_aifs(vap.getDouble("tx_queue_data1_aifs"));
                vaps.get(i).getQosParameters().setTx_queue_data1_cwmin(vap.getDouble("tx_queue_data1_cwmin"));
                vaps.get(i).getQosParameters().setTx_queue_data1_cwmax(vap.getDouble("tx_queue_data1_cwmax"));
                vaps.get(i).getQosParameters().setTx_queue_data1_burst(vap.getDouble("tx_queue_data1_burst"));

                vaps.get(i).getQosParameters().setTx_queue_data0_aifs(vap.getDouble("tx_queue_data0_aifs"));
                vaps.get(i).getQosParameters().setTx_queue_data0_cwmin(vap.getDouble("tx_queue_data0_cwmin"));
                vaps.get(i).getQosParameters().setTx_queue_data0_cwmax(vap.getDouble("tx_queue_data0_cwmax"));
                vaps.get(i).getQosParameters().setTx_queue_data0_burst(vap.getDouble("tx_queue_data0_burst"));
            
            }
            
        }
        
         return vaps;
        
    }
    
    public static List<String> exportHostApdLines(String slice, String nodeID,JsonHostApd hostApd,List<JsonVap> vaps){
    
        List<String> hostApdLines=new ArrayList<String>();
    
        
        
        if(vaps.size()>1)
           hostApdLines.add("bssid="+getBSSID(slice,nodeID));
        
        hostApdLines.add("channel="+String.valueOf(hostApd.getChannel()));
        hostApdLines.add("hw_mode="+String.valueOf(hostApd.getHw_mode()));
        hostApdLines.add("driver="+String.valueOf(hostApd.getDriver()));

        
        return hostApdLines;
    }
    
    public static List<String> exportVapsLines(List<JsonVap> vaps){
    
       List<String> lines=new ArrayList<String>();
       
             // QoS Lines
            for (int i = 0; i < vaps.size(); ++i) {
                if(i==0){
                    lines.add("interface=wlan0"); //Used by default by VAP 1
                }
                else if(i>0){
                    lines.add("bss=wlan0_"+(i-1));
                }
                lines.add("bridge=br"+i);
                lines.add("ssid="+String.valueOf(vaps.get(i).getSsid()));
               // lines.add("password="+String.valueOf(vaps.get(i).getPassword()));
                lines.add("beacon_int="+String.valueOf(vaps.get(i).getBeacon_int()));
                lines.add("max_num_sta="+String.valueOf(vaps.get(i).getMax_num_sta()));
                lines.add("wmm_enabled="+String.valueOf(vaps.get(i).getWmm_enabled()));
                lines.add("ieee80211n="+String.valueOf(vaps.get(i).getIeee80211n()));
                lines.add("ht_capab="+String.valueOf(vaps.get(i).getHt_capab()));
                
                if(vaps.get(i).getQosParameters()!=null){
                    
                    lines.add("wmm_ac_bk_cwmin="+String.valueOf(vaps.get(i).getQosParameters().getWmm_ac_bk_cwmin()));
                    lines.add("wmm_ac_bk_cwmax="+String.valueOf(vaps.get(i).getQosParameters().getWmm_ac_bk_cwmax()));
                    lines.add("wmm_ac_bk_aifs="+String.valueOf(vaps.get(i).getQosParameters().getWmm_ac_bk_aifs()));
                    lines.add("wmm_ac_bk_txop_limit="+String.valueOf(vaps.get(i).getQosParameters().getWmm_ac_bk_txop_limit()));
                    lines.add("wmm_ac_bk_acm="+String.valueOf(vaps.get(i).getQosParameters().getWmm_ac_bk_acm()));

                    lines.add("wmm_ac_be_aifs="+String.valueOf(vaps.get(i).getQosParameters().getWmm_ac_be_aifs()));
                    lines.add("wmm_ac_be_cwmin="+String.valueOf(vaps.get(i).getQosParameters().getWmm_ac_be_cwmin()));
                    lines.add("wmm_ac_be_cwmax="+String.valueOf(vaps.get(i).getQosParameters().getWmm_ac_be_cwmax()));
                    lines.add("wmm_ac_be_txop_limit="+String.valueOf(vaps.get(i).getQosParameters().getWmm_ac_be_txop_limit()));
                    lines.add("wmm_ac_be_acm="+String.valueOf(vaps.get(i).getQosParameters().getWmm_ac_be_acm()));

                    lines.add("wmm_ac_vi_aifs="+String.valueOf(vaps.get(i).getQosParameters().getWmm_ac_vi_aifs()));
                    lines.add("wmm_ac_vi_cwmin="+String.valueOf(vaps.get(i).getQosParameters().getWmm_ac_vi_cwmin()));
                    lines.add("wmm_ac_vi_cwmax="+String.valueOf(vaps.get(i).getQosParameters().getWmm_ac_vi_cwmax()));
                    lines.add("wmm_ac_vi_txop_limit="+String.valueOf(vaps.get(i).getQosParameters().getWmm_ac_vi_txop_limit()));
                    lines.add("wmm_ac_vi_acm="+String.valueOf(vaps.get(i).getQosParameters().getWmm_ac_vi_acm()));

                    lines.add("wmm_ac_vo_aifs="+String.valueOf(vaps.get(i).getQosParameters().getWmm_ac_vo_aifs()));
                    lines.add("wmm_ac_vo_cwmin="+String.valueOf(vaps.get(i).getQosParameters().getWmm_ac_vo_cwmin()));
                    lines.add("wmm_ac_vo_cwmax="+String.valueOf(vaps.get(i).getQosParameters().getWmm_ac_vo_cwmax()));
                    lines.add("wmm_ac_vo_txop_limit="+String.valueOf(vaps.get(i).getQosParameters().getWmm_ac_vo_txop_limit()));
                    lines.add("wmm_ac_vo_acm="+String.valueOf(vaps.get(i).getQosParameters().getWmm_ac_vo_acm()));

                    lines.add("tx_queue_data3_aifs="+String.valueOf(vaps.get(i).getQosParameters().getTx_queue_data3_aifs()));
                    lines.add("tx_queue_data3_cwmin="+String.valueOf(vaps.get(i).getQosParameters().getTx_queue_data3_cwmin()));
                    lines.add("tx_queue_data3_cwmax="+String.valueOf(vaps.get(i).getQosParameters().getTx_queue_data3_cwmax()));
                    lines.add("tx_queue_data3_burst="+String.valueOf(vaps.get(i).getQosParameters().getTx_queue_data3_burst()));

                    lines.add("tx_queue_data2_aifs="+String.valueOf(vaps.get(i).getQosParameters().getTx_queue_data2_aifs()));
                    lines.add("tx_queue_data2_cwmin="+String.valueOf(vaps.get(i).getQosParameters().getTx_queue_data2_cwmin()));
                    lines.add("tx_queue_data2_cwmax="+String.valueOf(vaps.get(i).getQosParameters().getTx_queue_data2_cwmax()));
                    lines.add("tx_queue_data2_burst="+String.valueOf(vaps.get(i).getQosParameters().getTx_queue_data2_burst()));

                    lines.add("tx_queue_data1_aifs="+String.valueOf(vaps.get(i).getQosParameters().getTx_queue_data1_aifs()));
                    lines.add("tx_queue_data1_cwmin="+String.valueOf(vaps.get(i).getQosParameters().getTx_queue_data1_cwmin()));
                    lines.add("tx_queue_data1_cwmax="+String.valueOf(vaps.get(i).getQosParameters().getTx_queue_data1_cwmax()));
                    lines.add("tx_queue_data1_burst="+String.valueOf(vaps.get(i).getQosParameters().getTx_queue_data1_burst()));

                    lines.add("tx_queue_data0_aifs="+String.valueOf(vaps.get(i).getQosParameters().getTx_queue_data0_aifs()));
                    lines.add("tx_queue_data0_cwmin="+String.valueOf(vaps.get(i).getQosParameters().getTx_queue_data0_cwmin()));
                    lines.add("tx_queue_data0_cwmax="+String.valueOf(vaps.get(i).getQosParameters().getTx_queue_data0_cwmax()));
                    lines.add("tx_queue_data0_burst="+String.valueOf(vaps.get(i).getQosParameters().getTx_queue_data0_burst()));
                
                }
            }
            
            if(lines.isEmpty())
                return null;
        
        return lines;
        
    }

    static Hashtable mergeHashtables(Hashtable responseParameters, Hashtable libResponse, Hashtable netResponse, Hashtable hostapdServiceResponse) {
        
        Hashtable response=new Hashtable();
        
        response.putAll(responseParameters);
        response.putAll(libResponse);
        response.putAll(netResponse);
        response.putAll(hostapdServiceResponse);
        
        return response;
    }

    static String createJsonResponse(Hashtable response) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static String getBSSID(String _slice,String _nodeID) {
      
        String command="";
        String commandB="ifconfig wlan0 |grep -o \"HWaddr ..:..:..:..:..:..\"";
        String response="";
        String prefix="ssh -oStrictHostKeyChecking=no $h root@"+_nodeID+" ";
        
        command=prefix+"'"+commandB+"'";
        
        try {
            response=Utilities.remoteExecution(_slice,command);
        } catch (IOException ex) {
            Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return response;
    }
    
    static Hashtable loadInterfacesFile(String slice, String nodeID, List<JsonVap> vaps) {
               
        String command="";
        String string="";
        Hashtable response=new Hashtable(); 
        
           
        String prefix="ssh -oStrictHostKeyChecking=no $h root@"+nodeID+" ";
                
        try {
             if(vaps.size()>1){
            command=prefix+"rm /etc/network/interfaces";
            Utilities.remoteExecution(slice, command);
            
            command=prefix+"touch /etc/network/interfaces";
            Utilities.remoteExecution(slice, command);

            
            // Add Lines to the interfaces file
            // line 1
            string="auto lo wlan0";
                 for (int i = 0; i < vaps.size(); i++) {
                   string+=" eth"+i+" "+"br"+i;  
                     
                 }
            command="echo "+string+" |"+prefix+" 'cat>>/etc/network/interfaces'";
            Utilities.remoteExecution(slice, command);
            
            // line 2
            string="iface lo inet loopback";
            command="echo "+string+" |"+prefix+" 'cat>>/etc/network/interfaces'";
            Utilities.remoteExecution(slice, command);
            // line 3
            string="iface eth0 inet dhcp";
            command="echo "+string+" |"+prefix+" 'cat>>/etc/network/interfaces'";
            Utilities.remoteExecution(slice, command);
            
            List<String> lines;
            
            for (int i = 0; i < vaps.size(); i++) {
                  lines=new ArrayList<>();
            
                  lines.add("iface br"+String.valueOf(i)+"inet manual");
                 
                  if(i==0)
                     lines.add("\t\tbridge_ports wlan0 inet manual");
                  else 
                     lines.add("\t\tbridge_ports wlan0_"+String.valueOf(i-1)+"inet manual");
                  
                  lines.add("\t\tbridge_stp on");
                  lines.add("\t\tmaxwait 10");

                  
                for (Iterator<String> it = lines.iterator(); it.hasNext();) {
                    string = it.next();
                    command="echo "+string+" |"+prefix+" 'cat>>/etc/network/interfaces'";
                    Utilities.remoteExecution(slice, command);
                }
               
                string = "";
                command="echo "+string+" |"+prefix+" 'cat>>/etc/network/interfaces'";
                Utilities.remoteExecution(slice, command);
            }
           
           //STEP 3: Make Hostapd Service and run
          
           
            
            response.put("interfacesFileCreated", "yes");

           
             }
           
        } catch (Exception e) {
            
            System.out.println(e);
            LOGGER.log(Level.INFO,e.toString());
            
            
        }
       return response;
        
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Content;

import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * REST Web Service
 *
 * @author nitlab
 */
@Path("/node")
public class Node {

    /**
     * Creates a new instance of GenericResource
     */
    public Node() {
    }

    /**
     * Executes remote actions on the NITOS server
     * @return an instance of java.lang.String
     */
    @GET
    @Path("/node{nodeID}/{action}")
    @Produces("application/json")
    public String execOnServer(@PathParam("nodeID") String _nodeID,@PathParam("action") String _action) {

        String address="nitlab3.inf.uth.gr";
        String slice="kostas";
        String node=_nodeID;
        String action=_action;
        
        String jsonObj="";
        String command="";
        String response="";
        
        // Image load
        if("imageload".equals(action.toLowerCase())){
            // OMF 6 
            command ="omf6 load -t "+node+" -i baseline.ndz ";
            //OMF 5
            //command="omf load -i baseline.ndz -t omf.nitos."+node;
             
             response=Utilities.remoteExecution(address,slice,command);
             
             if(response.toLowerCase().contains("Load proccess completed".toLowerCase()))
                      response="success";
              else 
                  response="failure";
        }
       
        // on/off/reset
        if("on".equals(action.toLowerCase())||"off".equals(action.toLowerCase())||"reset".equals(action.toLowerCase())){
            //OMF 6 
            command="omf6 tell -a "+action+" -t "+node;
            
            //OMF 5
            // command="omf tell -a "+action+" -t omf.nitos."+node;
            
             response=Utilities.remoteExecution(address,slice,command);
            
            if(response.toLowerCase().contains("Proccess complete".toLowerCase()))
                     response="success";
               else 
                     response="failure";
        }
        
        if("status".equals(action.toLowerCase())){
             command="omf6 stat -t "+node;
            
             response=Utilities.remoteExecution(address,slice,command);
            
            if(response.toLowerCase().contains("status is: on".toLowerCase()))
                     response="on";
               else 
                     response="off";
        }
         
            jsonObj="{\"node\":\""+_nodeID+"\",\"action\":\""+_action+"\",\"status\":\""+response+"\"}";
            
            
         return jsonObj;
    
    }
    /**
     * POST method for updating or creating the network configuration of Node
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
   
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    @Path("{nodeID}/network")
    public String postNetworkConfig(@PathParam("nodeID") String _nodeID,final NetworkConfig body) {
        
        Hashtable<String,String> parameters=new Hashtable<>();
        
        parameters.put("slice","kostas");
        parameters.put("node",_nodeID);
        parameters.put("vlan",body.vlan);
        parameters.put("address",body.address);
        parameters.put("netmask",body.netmask);
        parameters.put("bridge",body.bridge);
        
        String address="nitlab3.inf.uth.gr";
        String slice="kostas";
        String response="";
        String nodeAddress="root@"+_nodeID;
        
        String command="ssh -oStrictHostKeyChecking=no $h "+nodeAddress+" echo 'kostas'";
        
        response=Utilities.remoteExecution(address,slice,command);
        
        String jsonObj="";
        
        jsonObj="{\"node\":\""+_nodeID+"\",\"action\":\""+"hostapdConfig"+"\",\"status\":\""+response+"\"}";
        
         return jsonObj;
    }
    
}

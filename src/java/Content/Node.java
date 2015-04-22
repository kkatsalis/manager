/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Content;

import java.io.IOException;
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
import javax.ws.rs.QueryParam;
import javax.xml.bind.annotation.XmlElement;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * REST Web Service
 *
 * @author nitlab
 */
@Path("")
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
    @Path("node")
    @Produces("application/json")
    public String initializeNodeActions(@QueryParam("name") String _nodeID,
                               @QueryParam("action") String _action,
                               @QueryParam("slice") String _slice) {
       
        String slice=_slice;
        String nodeID=_nodeID;
        String action=_action;
        String jsonResponse="";
     
        try {
            // Image load
            if("loadimage".equals(action.toLowerCase())){
                jsonResponse=Utilities.executeImageLoad(slice,nodeID);
            }
            // on/off/reset
            if("on".equals(action.toLowerCase())||"off".equals(action.toLowerCase())||"reset".equals(action.toLowerCase())){
                jsonResponse=Utilities.cmExecuteAction(slice, nodeID, action);
             }
            if("status".equals(action.toLowerCase())){
                jsonResponse=Utilities.cmStatus(slice,nodeID);
             }
         
        } catch (IOException ex) {
          Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
        }
         
         return jsonResponse;
    
    }
    /**
     * POST method for updating or creating the network configuration of Node
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
   
    
    @GET
    @Produces("text/plain")
    @Path("/node/console")
    public String getNodeConsole(@QueryParam("name") String _nodeID,
                                 @QueryParam("slice") String _slice,
                                 @QueryParam("command") String _command
                               ) {
        
        String command="";
        String response="";
        String prefix="ssh -oStrictHostKeyChecking=no $h root@"+_nodeID+" ";
        
        command=prefix+"'"+_command+"'";
               
        
        try {
            response=Utilities.remoteExecution(_slice,command);
        } catch (IOException ex) {
            Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
        }
        
         return response;
    }
    
    
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    @Path("/node/network")
    public String postNodeNetworkConfig(@QueryParam("name") String _nodeID,
                                    @QueryParam("slice") String _slice,
                               final NetworkParams body) {
        
        Hashtable<String,String> parameters=new Hashtable<>();
        

        parameters.put("vlan",body.vlan);
        parameters.put("address",body.address);
        parameters.put("netmask",body.netmask);
        parameters.put("bridge",body.bridge);
        parameters.put("driver",body.driver);
        
        Boolean status=false;
        String jsonObj="";
                
        status=Utilities.prepareNodeNetworkConfig(_slice, _nodeID);
       
        if(status)
           jsonObj=Utilities.createNodeNetworkConfig(_slice, _nodeID,parameters);
        
         return jsonObj;
    }
    
    
     @POST
     @Consumes("application/json")
     @Produces("application/json")
     @Path("/node/ap")
     public String postAccessPointConfig(@QueryParam("name") String _nodeID,
                                    @QueryParam("slice") String _slice,
                               final HostapdParams body) {
        
        Hashtable<String,String> parameters=new Hashtable<>();
        
        parameters.put("intrface",body.intrface);
        parameters.put("bridge",body.bridge);
        parameters.put("driver",body.driver);
        parameters.put("ssid",body.ssid);
        parameters.put("channel",body.channel);
        parameters.put("hw_mode",body.hw_mode);
        parameters.put("wmm_enabled",body.wmm_enabled);
        parameters.put("ieee80211n",body.ieee80211n);
        parameters.put("ht_capab",body.ht_capab);
        
        String jsonObj="";
       
         jsonObj=Utilities.createAccessPointConfig(_slice, _nodeID,parameters);
        
         return jsonObj;
    }
    
}

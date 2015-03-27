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
import javax.ws.rs.QueryParam;
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
    @Path("/node")
    @Produces("application/json")
    public String execOnServer(@QueryParam("name") String _nodeID,
                               @QueryParam("action") String _action,
                               @QueryParam("slice") String _slice) {

       
        String slice=_slice;
        String nodeID=_nodeID;
        String action=_action;
        
        String jsonResponse="";
        String response="";
        
        // Image load
        if("imageload".equals(action.toLowerCase())){
            response=Utilities.executeImageLoad(slice,nodeID);
        }
       
        // on/off/reset
        if("on".equals(action.toLowerCase())||"off".equals(action.toLowerCase())||"reset".equals(action.toLowerCase())){
            response=Utilities.executeAction(slice, nodeID, action);
         }
        
        if("status".equals(action.toLowerCase())){
            response=Utilities.findCMStatus(slice,nodeID);
         }
         
            jsonResponse="{\"node\":\""+_nodeID+"\",\"action\":\""+_action+"\",\"status\":\""+response+"\"}";
            
         return jsonResponse;
    
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
    public String postNetworkConfig(@QueryParam("name") String _nodeID,
                               @QueryParam("action") String _action,
                               @QueryParam("slice") String _slice,
                               final NetworkConfig body) {
        
        Hashtable<String,String> parameters=new Hashtable<>();
        
        parameters.put("slice","kostas");
        parameters.put("node",_nodeID);
        parameters.put("vlan",body.vlan);
        parameters.put("address",body.address);
        parameters.put("netmask",body.netmask);
        parameters.put("bridge",body.bridge);
        
       
        
        String response=Utilities.networkConfig(_slice, _nodeID,parameters);
        
        String jsonObj="";
        
        jsonObj="{\"node\":\""+_nodeID+"\",\"action\":\""+"hostapdConfig"+"\",\"status\":\""+response+"\"}";
        
         return jsonObj;
    }
    
}

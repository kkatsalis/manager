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
import org.json.JSONException;
import org.json.JSONObject;


/**
 * REST Web Service
 *
 * @author nitlab
 */
@Path("/")
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
    public String execNodeAction(@QueryParam("name") String _nodeID,
                               @QueryParam("action") String _action,
                               @QueryParam("slice") String _slice) {
       
        String slice=_slice;
        String nodeID=_nodeID;
        String action=_action;
        String jsonResponse="";
     
        try {
            // Image load
            if("imageload".equals(action.toLowerCase())){
                jsonResponse=Utilities.executeImageLoad(slice,nodeID);
            }
            // on/off/reset
            if("on".equals(action.toLowerCase())||"off".equals(action.toLowerCase())||"reset".equals(action.toLowerCase())){
                jsonResponse=Utilities.executeAction(slice, nodeID, action);
             }
            if("status".equals(action.toLowerCase())){
                jsonResponse=Utilities.findCMStatus(slice,nodeID);
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
   
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    @Path("/network/node")
    public String postNetworkConfig(@QueryParam("name") String _nodeID,
                                    @QueryParam("slice") String _slice,
                               final NetworkConfig body) {
        
        Hashtable<String,String> parameters=new Hashtable<>();
        

        parameters.put("vlan",body.vlan);
        parameters.put("address",body.address);
        parameters.put("netmask",body.netmask);
        parameters.put("bridge",body.bridge);
        parameters.put("driver",body.driver);
        
        String jsonObj=Utilities.createNetworkConfig(_slice, _nodeID,parameters);
        
        
         return jsonObj;
    }
    
}

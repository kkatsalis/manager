/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Content;

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
     * Retrieves representation of an instance of Content.Node
     * @return an instance of java.lang.String
     */
   

    @GET
    @Path("{nodeID}/{action}")
    @Produces("application/json")
    public String actionOnNode(@PathParam("nodeID") String _nodeID,@PathParam("action") String _action) {

        String address="nitlab3.inf.uth.gr";
        String slice="kostas";
        String node=_nodeID;
        String action=_action;
        
        String jsonObj="";
        String command="";
        String response="";
        
        // Image load
         //command="omf load -i baseline.ndz -t omf.nitos."+node;
        if("imageLoad".equals(action)){
             command ="omf6 load -t "+node+" -i baseline.ndz ";
             
             response=Utilities.remoteExecution(address,slice,command);
             
             if(response.toLowerCase().contains("Load proccess completed".toLowerCase()))
                      response="success";
              else 
                  response="failure";
        }
        // command="omf tell -a "+action+" -t omf.nitos."+node;
        // on/off/reset
        if("on".equals(action)||"off".equals(action)||"reset".equals(action)){
             command="omf6 tell -a "+action+" -t "+node;
            
             response=Utilities.remoteExecution(address,slice,command);
            
            if(response.toLowerCase().contains("Proccess complete".toLowerCase()))
                     response="success";
               else 
                     response="failure";
        }
        
        if("status".equals(action)){
             command="omf6 stat -t "+node;
            
             response=Utilities.remoteExecution(address,slice,command);
            
            if(response.toLowerCase().contains("status is: on".toLowerCase()))
                     response="on";
               else 
                     response="off";
        }
         
            jsonObj="{\"node\":\""+_nodeID+"\",\"action\":\""+_action+"\",\"status\":\""+response+"\"}";
            
            
         return jsonObj;
    //    return "<html lang=\"en\"><body><h1>"+response+"</body></h1></html>";
    }
    /**
     * PUT method for updating or creating an instance of Node
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    @Path("/hostapd")
    public String postHostapd(@PathParam("nodeID") String _nodeID,final Hostapd body) {
        
        String node=_nodeID;
        String address="10.1.0"+_nodeID.replace("node0", "");
        String jsonObj="";
        String command="";
        String response="";
        
         jsonObj="{\"node\":\""+node+"\",\"action\":\""+"hostapdConfig"+"\",\"status\":\""+response+"\"}";
        
         jsonObj=body.param1+" "+body.param2;
         
         return jsonObj;
    }
    
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    @Path("{nodeID}/network")
    public String postNodeNetwork(@PathParam("nodeID") String _nodeID,final Hostapd body) {
        
        String address="10.1.0"+_nodeID.replace("node0", "");
        String slice="kostas";
        String node=_nodeID;
        
        String jsonObj="";
        String command="";
        String response="";
        
         jsonObj="{\"node\":\""+_nodeID+"\",\"action\":\""+"hostapdConfig"+"\",\"status\":\""+response+"\"}";
        
         return jsonObj;
    }
    
}

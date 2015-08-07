/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Content;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
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
import javax.ws.rs.core.MediaType;
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
    @Path("node/cm")
    @Produces("application/json")
    public String nodeCmActions(@QueryParam("nodeID") String _nodeID,
                               @QueryParam("action") String _action,
                               @QueryParam("slice") String _slice,
                                @QueryParam("omf") String _omfVersion) {
       
        String slice=_slice;
        String nodeID=_nodeID;
        String action=_action;
        String omf=_omfVersion;
         
        String jsonResponse="";
     
        try {
            // on/off/reset
            if("on".equals(action.toLowerCase()))
               jsonResponse=Utilities.cmExecuteAction(slice, nodeID, action,omf);
            else if("off".equals(action.toLowerCase()))
               jsonResponse=Utilities.cmExecuteAction(slice, nodeID, action,omf);
            else if("reset".equals(action.toLowerCase()))
                jsonResponse=Utilities.cmExecuteAction(slice, nodeID, action,omf);
            else if("status".equals(action.toLowerCase())){
                jsonResponse=Utilities.cmStatus(slice,nodeID,omf);
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
    @Path("node/imageload")
    @Produces("application/json")
    public String imageLoad(@QueryParam("nodeID") String _nodeID,
                            @QueryParam("slice") String _slice,                   
                            @QueryParam("omf") String _omfVersion) {
       
        String slice=_slice;
        String nodeID=_nodeID;
        String omf=_omfVersion;
        String jsonResponse="";
     
        try {
            // Image load
                jsonResponse=Utilities.executeImageLoad(slice,nodeID,omf);
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
    public String nodeConsole(@QueryParam("nodeID") String _nodeID,
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
     //@Consumes("application/json")
     @Consumes(MediaType.APPLICATION_JSON)
     @Produces("application/json")
     @Path("/node/vap")
     public String vapConfig(@QueryParam("nodeID") String _nodeID,
                             @QueryParam("slice") String _slice, String _body) {
       
         String jsonObj="";
        
         try {
        
          JSONObject body=new JSONObject(_body);
          
          List<String> hostApdLines=Utilities.parseHostApdConfig(body);
          List<String> apQoSLines=Utilities.parseApQosConfig(body);
          
          List<String> vapLines=Utilities.parseVapsConfig(body);
               
          
        } catch (JSONException ex) {
            Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jsonObj;
    }
    
}

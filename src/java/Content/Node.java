/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Content;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
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
 * @author Kostas Katsalis
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
               jsonResponse=NodeUtilities.cmExecuteAction(slice, nodeID, action,omf);
            else if("off".equals(action.toLowerCase()))
               jsonResponse=NodeUtilities.cmExecuteAction(slice, nodeID, action,omf);
            else if("reset".equals(action.toLowerCase()))
                jsonResponse=NodeUtilities.cmExecuteAction(slice, nodeID, action,omf);
            else if("status".equals(action.toLowerCase())){
                jsonResponse=NodeUtilities.cmStatus(slice,nodeID,omf);
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
                            @QueryParam("omf") String _omfVersion,
                            @QueryParam("image") String _imageName) {
       
        String slice=_slice;
        String nodeID=_nodeID;
        String omf=_omfVersion;
        String imageName=_imageName;
        String jsonResponse="";
     
        try {
            // Image load
                jsonResponse=NodeUtilities.executeImageLoad(slice,nodeID,omf,imageName);
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
            response=NodeUtilities.remoteExecution(_slice,command);
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
       
         Hashtable responseParameters=new Hashtable();
         Hashtable libResponse=null;
         Hashtable netResponse=null;
         Hashtable hostapdServiceResponse=null;
         
         responseParameters.put("nodeID", _nodeID);
         responseParameters.put("slice", _slice);
         
         try {
        
          JSONObject body=new JSONObject(_body);
          
          // STEP 0: Install necessary libraries
          libResponse=NodeUtilities.prepareNodeLibraries(_slice,_nodeID);  
          
          if(!libResponse.isEmpty())
              responseParameters.put("librariesLoaded", "ok");
          else
              responseParameters.put("librariesLoaded", "error");
          
          // STEP 1: Parse hostapd 
          HostApd hostApd=NodeUtilities.parseHostApdConfig(body);
          List<Vap> vaps=NodeUtilities.parseVapsConfig(body);
          
          if(hostApd!=null&vaps!=null)
             responseParameters.put("parsingHostApd", "ok");
           else
             responseParameters.put("parsingHostApd", "error");
          
           // STEP 2: Load the Network Configuration 
          netResponse=NodeUtilities.createNodeNetworkConfig(_slice,_nodeID,hostApd,vaps);
         
          if(!netResponse.isEmpty())
             responseParameters.put("createNodeNetworkConfig", "ok");
          else
             responseParameters.put("createNodeNetworkConfig", "error");
       
          
          // STEP 3: Parse and create Configuration Lines for the HostApd File
          List<String> configLines=new ArrayList<String>();  
          String line;
           
          
          // 3.1 Prepare hostapd lines
          List<String> lines_hostApd=NodeUtilities.exportHostApdLines(_slice,_nodeID,hostApd,vaps);
          
             for (Iterator<String> it = lines_hostApd.iterator(); it.hasNext();) {
                line = it.next();
                configLines.add(line);
            }
         
          // 3.2 Add vaps lines
          
          List<String> lines_vaps=NodeUtilities.exportVapsLines(vaps);
          
          for (Iterator<String> it = lines_vaps.iterator(); it.hasNext();) {
                line = it.next();
                configLines.add(line);
            }
          
          if(configLines.isEmpty())
              responseParameters.put("hostApdLinesCreated", "no");
          else
              responseParameters.put("hostApdLinesCreated", "yes"); 
         
        // STEP 4: Load the HostApd file and start the Service
        hostapdServiceResponse=NodeUtilities.loadAndStartHostApdService(_slice,_nodeID, configLines);
          
        } catch (JSONException ex) {
            Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
        }
        
       Hashtable response=NodeUtilities.mergeHashtables(responseParameters,libResponse,netResponse,hostapdServiceResponse);  
       String jsonResponse=NodeUtilities.createJsonResponse(_nodeID,response);
         
         
         return jsonResponse;
    }
    
}

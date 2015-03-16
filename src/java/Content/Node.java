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
        String jsonObj="";
        String command="";
        String address="nitlab3.inf.uth.gr";
        String slice="kostas";
        String node=_nodeID;
        String action=_action;
        
        if("imageLoad".equals(action)){
             command ="omf6 load -t "+node+" -i baseline.ndz ";
           //command="omf load -i baseline.ndz -t omf.nitos."+node;
        }
       else if("on".equals(action)||"reboot".equals(action))
            command="omf tell -a "+action+" -t omf.nitos."+node;
      
        
        String response=Utilities.remoteExecution(address,slice,command);
         
         if("imageLoad".equals(action)){
              if(response.toLowerCase().contains("1 node successfully imaged".toLowerCase()))
                      response="success";
              else 
                  response="failure";
         }
         else if("on".equals(action)||"reboot".equals(action)){
               if(response.toLowerCase().contains("finished after".toLowerCase()))
                     response="success";
               else 
                     response="failure";
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
    @PUT
    @Consumes("application/json")
    public void putJson(String content) {
    }
}

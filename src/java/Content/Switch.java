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
 *
 * @author Kostas Katsalis
 */
@Path("")
public class Switch {
    
     @GET
    @Produces("text/plain")
    @Path("/switch/console")
    public String switchConsole(@QueryParam("command") String command) {
        
        String user="manager";
        String response="";
        
        try {
        
            response=SwitchUtilities.remoteExecution(user,command);
       
        } catch (IOException ex) {
            Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
        }
        
         return response;
    }
    
    
    
     @POST
     //@Consumes("application/json")
     @Consumes(MediaType.APPLICATION_JSON)
     @Produces("application/json")
     @Path("/switch/vswitch")
     public String switchConfig(String _body) {
       
         String jsonResponse="";
         Hashtable responseParameters=new Hashtable();
         String user="manager";
         
         
         try {
        
          JSONObject body=new JSONObject(_body);

          jsonResponse=NodeUtilities.createJsonResponse("",responseParameters);

        } catch (JSONException ex) {
            Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
        }
         
         return jsonResponse;
    }
}

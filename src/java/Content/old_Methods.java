/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Content;

import java.util.Hashtable;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 *
 * @author nitlab
 */
public class old_Methods {
    
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    @Path("/node/network")
    public String old_networkConfig(@QueryParam("nodeID") String _nodeID,
                                    @QueryParam("slice") String _slice,
                               final old_JsonVap body) {
        
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
     public String old_hostapdConfig(@QueryParam("nodeID") String _nodeID,
                                    @QueryParam("slice") String _slice,
                               final JsonHostApd body) {
        
        Hashtable<String,String> parameters=new Hashtable<>();
        
//        parameters.put("intrface",body.intrface);
//        parameters.put("bridge",body.bridge);
//        parameters.put("driver",body.driver);
//        parameters.put("ssid",body.ssid);
//        parameters.put("channel",body.channel);
//        parameters.put("hw_mode",body.hw_mode);
//        parameters.put("wmm_enabled",body.wmm_enabled);
//        parameters.put("ieee80211n",body.ieee80211n);
//        parameters.put("ht_capab",body.ht_capab);
        
        String jsonObj="";
       
         jsonObj=Utilities.createAccessPointConfig(_slice, _nodeID,parameters);
        
         return jsonObj;
    }
}

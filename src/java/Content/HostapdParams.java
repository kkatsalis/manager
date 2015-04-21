/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Content;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author nitlab
 */
public class HostapdParams {
    
    
    @XmlElement public String intrface;
    @XmlElement public String bridge;
    @XmlElement public String driver;
    @XmlElement public String ssid;
    @XmlElement public String channel;
    @XmlElement public String hw_mode;
    @XmlElement public String wmm_enabled;
    @XmlElement public String ieee80211n;
    @XmlElement public String ht_capab;

}

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

@XmlRootElement
public class NetworkConfig {
    
    @XmlElement public String vlan;
    @XmlElement public String driver;
    @XmlElement public String address;
    @XmlElement public String netmask;
    @XmlElement public String bridge;
    
    
    
}

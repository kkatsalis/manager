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
public class Hostapd {
    
    @XmlElement public String param1;
    @XmlElement public String param2;
    
}

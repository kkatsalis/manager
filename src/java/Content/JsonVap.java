/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Content;

/**
 *
 * @author nitlab
 */
public class JsonVap {
    
    public int id;
    public String ssid;
    public String password;
    public String network;
    public String netMask;
    public int vlan;
    public int max_customers;
    public double max_rate;
    public double min_rate;
    public double ratio_rate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getNetMask() {
        return netMask;
    }

    public void setNetMask(String netMask) {
        this.netMask = netMask;
    }

    public int getVlan() {
        return vlan;
    }

    public void setVlan(int vlan) {
        this.vlan = vlan;
    }

    public int getMax_customers() {
        return max_customers;
    }

    public void setMax_customers(int max_customers) {
        this.max_customers = max_customers;
    }

    public double getMax_rate() {
        return max_rate;
    }

    public void setMax_rate(double max_rate) {
        this.max_rate = max_rate;
    }

    public double getMin_rate() {
        return min_rate;
    }

    public void setMin_rate(double min_rate) {
        this.min_rate = min_rate;
    }

    public double getRatio_rate() {
        return ratio_rate;
    }

    public void setRatio_rate(double ratio_rate) {
        this.ratio_rate = ratio_rate;
    }
    
    
    
    
}

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
    public double max_rate;
    public double min_rate;
    public double ratio_rate;

    public int wmm_enabled;
    public int ieee80211n;
    public int beacon_int;
    public int max_num_sta;
    public String ht_capab;

    public JsonApQoSParams qosParameters;
    
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

    public int getWmm_enabled() {
        return wmm_enabled;
    }

    public void setWmm_enabled(int wmm_enabled) {
        this.wmm_enabled = wmm_enabled;
    }

    public int getIeee80211n() {
        return ieee80211n;
    }

    public void setIeee80211n(int ieee80211n) {
        this.ieee80211n = ieee80211n;
    }

    public int getBeacon_int() {
        return beacon_int;
    }

    public void setBeacon_int(int beacon_int) {
        this.beacon_int = beacon_int;
    }

    public int getMax_num_sta() {
        return max_num_sta;
    }

    public void setMax_num_sta(int max_num_sta) {
        this.max_num_sta = max_num_sta;
    }

    public String getHt_capab() {
        return ht_capab;
    }

    public void setHt_capab(String ht_capab) {
        this.ht_capab = ht_capab;
    }

    public JsonApQoSParams getQosParameters() {
        return qosParameters;
    }

    public void setQosParameters(JsonApQoSParams qosParameters) {
        this.qosParameters = qosParameters;
    }
    
    public void createQoSparameters(){
     this.qosParameters=new JsonApQoSParams();
    }
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Content;

/**
 *
 * @author Katsalis
 */
public class JsonHostApd {
    
public int channel;
public String hw_mode;
public String driver;
public int wmm_enabled;
public int ieee80211n;
public int beacon_int;
public int max_num_sta;
public String ht_capab;

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public String getHw_mode() {
        return hw_mode;
    }

    public void setHw_mode(String hw_mode) {
        this.hw_mode = hw_mode;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
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


  


}


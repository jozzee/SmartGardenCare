package com.sitthiphong.smartgardencare.datamodel;

import android.content.SharedPreferences;

/**
 * Created by Sitthiphong on 7/12/2016 AD.
 */
public class NetPieBean {
    private String appID;
    private String appKey;
    private String appSecret;

    public NetPieBean(SharedPreferences preferences) {
        this.appID = preferences.getString("appID","no directory");
        this.appKey = preferences.getString("appKey","no directory");
        this.appSecret = preferences.getString("appSecret","no directory");
    }

    public NetPieBean(String appID, String appKey, String appSecret) {
        this.appID = appID;
        this.appKey = appKey;
        this.appSecret = appSecret;
    }

    public String getAppID() {
        return appID;
    }

    public String getAppKey() {
        return appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }
}

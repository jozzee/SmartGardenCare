package com.sitthiphong.smartgardencare.datamodel;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Created by Sitthiphong on 10/17/2016 AD.
 */

public class RawDataBean {
    private long time;
    private MoistureBean moisture;
    private TempBean temp;
    private LightBean light;

    public RawDataBean(long time, MoistureBean moistureBean, TempBean tempBean, LightBean lightBean) {
        this.time = time;
        this.moisture = moistureBean;
        this.temp = tempBean;
        this.light = lightBean;
    }

    public RawDataBean(String jsonString) {
        Gson gson = new Gson();
        JsonObject rawDataObj = gson.fromJson(jsonString, JsonObject.class);
        if (rawDataObj.get("time") != null) {
            this.time = rawDataObj.get("time").getAsLong();
        }
        if (rawDataObj.get("moisture") != null) {
            this.moisture = new MoistureBean(gson.fromJson(rawDataObj.get("moisture"), JsonObject.class));
        }
        if (rawDataObj.get("temp") != null) {

            this.temp = new TempBean(gson.fromJson(rawDataObj.get("temp"), JsonObject.class));
        }
        if (rawDataObj.get("light") != null) {

            this.light = new LightBean(gson.fromJson(rawDataObj.get("light"), JsonObject.class));
        }
    }

    public long getTime() {
        return time;
    }

    public MoistureBean getMoistureBean() {
        return moisture;
    }

    public TempBean getTempBean() {
        return temp;
    }

    public LightBean getLightBean() {
        return light;
    }
}

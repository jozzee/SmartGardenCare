package com.sitthiphong.smartgardencare.datamodel;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Created by Sitthiphong on 10/13/2016 AD.
 */

public class LightBean {
    private float lightIn;
    private float lightOut;

    public LightBean(float lightIn, float lightOut) {
        this.lightIn = lightIn;
        this.lightOut = lightOut;
    }
    public LightBean(String jsonString) {
        Gson gson = new Gson();
        JsonObject lightObj = gson.fromJson(jsonString,JsonObject.class);
        if(lightObj.get("light_in") != null){
            this.lightIn = lightObj.get("light_in").getAsFloat();
        }
        if(lightObj.get("light_out") != null){
            this.lightOut = lightObj.get("light_out").getAsFloat();
        }
        if(lightObj.get("lightIn") != null){
            this.lightIn = lightObj.get("lightIn").getAsFloat();
        }
        if(lightObj.get("lightOut") != null){
            this.lightOut = lightObj.get("lightOut").getAsFloat();
        }
    }
    public LightBean( JsonObject lightObj){
        if(lightObj != null){
            if(lightObj.get("light_in") != null){
                this.lightIn = lightObj.get("light_in").getAsFloat();
            }
            if(lightObj.get("light_out") != null){
                this.lightOut = lightObj.get("light_out").getAsFloat();
            }
        }
    }

    public float getLightIn() {
        return lightIn;
    }

    public float getLightOut() {
        return lightOut;
    }
}

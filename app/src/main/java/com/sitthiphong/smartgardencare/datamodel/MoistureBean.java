package com.sitthiphong.smartgardencare.datamodel;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Created by Sitthiphong on 10/13/2016 AD.
 */

public class MoistureBean {
    private float average;
    private float point1;
    private float point2;


    public MoistureBean(float average, float point1, float point2) {
        this.average = average;
        this.point1 = point1;
        this.point2 = point2;

    }

    public MoistureBean(String jsonString) {
        Gson gson = new Gson();
        JsonObject moistureObj = gson.fromJson(jsonString, JsonObject.class);
        if (moistureObj.get("point1") != null) {
            this.point1 = moistureObj.get("point1").getAsFloat();
        }
        if (moistureObj.get("point2") != null) {
            this.point2 = moistureObj.get("point2").getAsFloat();
        }
        if (moistureObj.get("average") != null) {
            this.average = moistureObj.get("average").getAsFloat();
        }

    }

    public MoistureBean(JsonObject moistureObj) {
        if (moistureObj != null) {
            if (moistureObj.get("point1") != null) {
                this.point1 = moistureObj.get("point1").getAsFloat();
            }
            if (moistureObj.get("point2") != null) {
                this.point2 = moistureObj.get("point2").getAsFloat();
            }
            if (moistureObj.get("average") != null) {
                this.average = moistureObj.get("average").getAsFloat();
            }
        }
    }

    public float getAverage() {
        return average;
    }

    public float getPoint1() {
        return point1;
    }

    public float getPoint2() {
        return point2;
    }

}

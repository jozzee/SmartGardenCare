package com.sitthiphong.smartgardencare.datamodel;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Created by Sitthiphong on 10/13/2016 AD.
 */

public class TempBean {
    private float average;
    private float point1;
    private float point2;


    public TempBean(float average, float point1, float point2) {
        this.average = average;
        this.point1 = point1;
        this.point2 = point2;
    }
    public TempBean(String jsonString) {
        Gson gson = new Gson();
        JsonObject tempObj = gson.fromJson(jsonString,JsonObject.class);
        if(tempObj.get("point1") != null){
            this.point1 = tempObj.get("point1").getAsFloat();
        }
        if(tempObj.get("point2") != null){
            this.point2 = tempObj.get("point2").getAsFloat();
        }
        if(tempObj.get("average") != null){
            this.average = tempObj.get("average").getAsFloat();
        }
    }
    public TempBean(JsonObject tempObj){
        if(tempObj != null){
            if(tempObj.get("point1") != null){
                this.point1 = tempObj.get("point1").getAsFloat();
            }
            if(tempObj.get("point2") != null){
                this.point2 = tempObj.get("point2").getAsFloat();
            }
            if(tempObj.get("average") != null){
                this.average = tempObj.get("average").getAsFloat();
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

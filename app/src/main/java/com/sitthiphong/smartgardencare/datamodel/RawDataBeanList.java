package com.sitthiphong.smartgardencare.datamodel;

import com.google.gson.JsonObject;

/**
 * Created by Sitthiphong on 8/4/2016 AD.
 */
public class RawDataBeanList {
    private int id;
    private long time;
    private float moisture;
    private float temp;
    private float light;


    public RawDataBeanList(String payload) {
        if (payload != null){
            String[] payloadList = payload.split(",");
            if(payloadList.length == 4){
                time = Long.parseLong(payloadList[0]);
                moisture = Float.parseFloat(payloadList[1]);
                temp = Float.parseFloat(payloadList[2]);
                light = Float.parseFloat(payloadList[3]);
            }
        }
    }
    public RawDataBeanList(JsonObject obj) {
        if(obj.get("time")!= null){
            this.time = obj.get("time").getAsLong();
        }
        if(obj.get("moisture")!= null){
            this.moisture = obj.get("moisture").getAsFloat();
        }
        if(obj.get("temp")!= null){
            this.temp = obj.get("temp").getAsFloat();
        }
        if(obj.get("light")!= null){
            this.light = obj.get("light").getAsFloat();
        }
        if(obj.get("id")!= null){
            this.id = obj.get("id").getAsInt();
        }
    }

    public long getTime() {
        return time;
    }

    public float getMoisture() {
        return moisture;
    }

    public float getTemp() {
        return temp;
    }

    public float getLight() {
        return light;
    }

    public int getId() {
        return id;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setMoisture(float moisture) {
        this.moisture = moisture;
    }
    public void setTemp(float temp) {
        this.temp = temp;
    }

    public void setLight(float light) {
        this.light = light;
    }

    public void setId(int id) {
        this.id = id;
    }

}

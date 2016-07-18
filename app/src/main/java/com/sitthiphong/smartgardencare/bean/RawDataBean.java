package com.sitthiphong.smartgardencare.bean;

import com.google.gson.JsonObject;

/**
 * Created by jozze on 12/4/2559.
 */
public class RawDataBean {

    private long time;
    private float humidity;
    private float temp;
    private float light;
    private int id;



    public RawDataBean(String payload) {
        if (payload != null){
            String[] payloadList = payload.split(",");
            if(payloadList.length == 4){
                time = Long.parseLong(payloadList[0]);
                humidity = Float.parseFloat(payloadList[1]);
                temp = Float.parseFloat(payloadList[2]);
                light = Float.parseFloat(payloadList[3]);
            }
        }
    }
    public RawDataBean(JsonObject obj) {
        if(obj.get("time")!= null){
            this.time = obj.get("time").getAsLong();
        }
        if(obj.get("humidity")!= null){
            this.humidity = obj.get("humidity").getAsFloat();
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

    public float getHumidity() {
        return humidity;
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

    public void setHumidity(float humidity) {
        this.humidity = humidity;
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

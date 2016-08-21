package com.sitthiphong.smartgardencare.datamodel;

import com.google.gson.JsonObject;

/**
 * Created by jozze on 12/4/2559.
 */
public class RawDataBean {

    private long time;
    private float moisture1,moisture2,moistureAverage,temp1,temp2,tempAverage,lightOut,lightIn;

    public RawDataBean(String payload) {
        if (payload != null){

        }
    }
    public RawDataBean(JsonObject obj) {
        if(obj.get("time")!= null){
            this.time = obj.get("time").getAsLong();
        }
        if(obj.get("moisture1")!= null){
            this.moisture1 = obj.get("moisture1").getAsFloat();
        }
        if(obj.get("moisture2")!= null){
            this.moisture2 = obj.get("moisture2").getAsFloat();
        }
        if(obj.get("moistureAverage")!= null){
            this.moistureAverage = obj.get("moistureAverage").getAsFloat();
        }
        if(obj.get("m1")!= null){
            this.moisture1 = obj.get("m1").getAsFloat();
        }
        if(obj.get("m2")!= null){
            this.moisture2 = obj.get("m2").getAsFloat();
        }
        if(obj.get("mAverage")!= null){
            this.moistureAverage = obj.get("mAverage").getAsFloat();
        }

        if(obj.get("temp1")!= null){
            this.temp1 = obj.get("temp1").getAsFloat();
        }
        if(obj.get("temp2")!= null){
            this.temp2 = obj.get("temp2").getAsFloat();
        }
        if(obj.get("tempAverage")!= null){
            this.tempAverage = obj.get("tempAverage").getAsFloat();
        }
        if(obj.get("t1")!= null){
            this.temp1 = obj.get("t1").getAsFloat();
        }
        if(obj.get("t2")!= null){
            this.temp2 = obj.get("t2").getAsFloat();
        }
        if(obj.get("tAverage")!= null){
            this.tempAverage = obj.get("tAverage").getAsFloat();
        }
        if(obj.get("lightIn")!= null){
            this.lightIn = obj.get("lightIn").getAsFloat();
        }
        if(obj.get("lightOut")!= null){
            this.lightOut = obj.get("lightOut").getAsFloat();
        }
        if(obj.get("l1")!= null){
            this.lightOut = obj.get("l1").getAsFloat();
        }
        if(obj.get("l2")!= null){
            this.lightIn = obj.get("l2").getAsFloat();
        }
    }

    public long getTime() {
        return time;
    }

    public float getMoisture1() {
        return moisture1;
    }

    public float getMoisture2() {
        return moisture2;
    }

    public float getMoistureAverage() {
        return moistureAverage;
    }

    public float getTemp1() {
        return temp1;
    }

    public float getTemp2() {
        return temp2;
    }

    public float getTempAverage() {
        return tempAverage;
    }

    public float getLightOut() {
        return lightOut;
    }

    public float getLightIn() {
        return lightIn;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setMoisture1(float moisture1) {
        this.moisture1 = moisture1;
    }

    public void setMoisture2(float moisture2) {
        this.moisture2 = moisture2;
    }

    public void setMoistureAverage(float moistureAverage) {
        this.moistureAverage = moistureAverage;
    }

    public void setTemp1(float temp1) {
        this.temp1 = temp1;
    }

    public void setTemp2(float temp2) {
        this.temp2 = temp2;
    }

    public void setTempAverage(float tempAverage) {
        this.tempAverage = tempAverage;
    }

    public void setLightOut(float lightOut) {
        this.lightOut = lightOut;
    }

    public void setLightIn(float lightIn) {
        this.lightIn = lightIn;
    }
}

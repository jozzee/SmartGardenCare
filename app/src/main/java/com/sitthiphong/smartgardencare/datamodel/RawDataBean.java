package com.sitthiphong.smartgardencare.datamodel;

import com.google.gson.JsonObject;

/**
 * Created by jozze on 12/4/2559.
 */
public class RawDataBean {

    private long time;
    private float moisture1;
    private float moisture2;
    private float temp1;
    private float temp2;
    private float lightIn;
    private float lightOut;

    public RawDataBean(String payload) {
        if (payload != null){
            String[] payloadList = payload.split(",");
            if(payloadList.length == 7){
                time = Long.parseLong(payloadList[0]);
                moisture1 = Float.parseFloat(payloadList[1]);
                moisture2 = Float.parseFloat(payloadList[2]);
                temp1 = Float.parseFloat(payloadList[3]);
                temp2 = Float.parseFloat(payloadList[4]);
                lightIn = Float.parseFloat(payloadList[5]);
                lightOut = Float.parseFloat(payloadList[6]);
            }
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
        if(obj.get("temp1")!= null){
            this.temp1 = obj.get("temp1").getAsFloat();
        }
        if(obj.get("temp2")!= null){
            this.temp2 = obj.get("temp2").getAsFloat();
        }
        if(obj.get("lightIn")!= null){
            this.lightIn = obj.get("lightIn").getAsFloat();
        }
        if(obj.get("lightOut")!= null){
            this.lightOut = obj.get("lightOut").getAsFloat();
        }
    }

    public float getMoisture(){
        if(moisture1>0 && moisture2 >0){
            return ((moisture1+moisture2)/2);
        }
        else if(moisture1>0 && moisture2 <0){
            return moisture1;
        }
        else if(moisture1<0 && moisture2 >0){
            return moisture2;
        }
        else{
           return (float)-1;
        }
    }
    public float getTemp(){
        if(temp1>0 && temp2 >0){
            return ((temp1+temp2)/2);
        }
        else if(temp1>0 && temp2 <0){
            return temp1;
        }
        else if(temp1<0 && temp2 >0){
            return temp2;
        }
        else{
            return (float)-1;
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

    public float getTemp1() {
        return temp1;
    }

    public float getTemp2() {
        return temp2;
    }

    public float getLightIn() {
        return lightIn;
    }

    public float getLightOut() {
        return lightOut;
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

    public void setTemp1(float temp1) {
        this.temp1 = temp1;
    }

    public void setTemp2(float temp2) {
        this.temp2 = temp2;
    }

    public void setLightIn(float lightIn) {
        this.lightIn = lightIn;
    }

    public void setLightOut(float lightOut) {
        this.lightOut = lightOut;
    }
}

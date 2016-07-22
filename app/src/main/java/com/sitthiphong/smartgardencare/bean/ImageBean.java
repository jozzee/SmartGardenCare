package com.sitthiphong.smartgardencare.bean;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.google.gson.JsonObject;

/**
 * Created by Sitthiphong on 7/22/2016 AD.
 */
public class ImageBean {
    private long timeStamp;
    private String imAsBase64;

    public ImageBean() {
    }

    public ImageBean(long timeStamp, String imAsBase64) {
        this.timeStamp = timeStamp;
        this.imAsBase64 = imAsBase64;
    }
    public ImageBean(JsonObject obj) {
        //JsonObject obj = GsonProvider.getInstance().fromJson(jsonString,JsonObject.class);
        if(obj.get("dateTime") != null){
            this.timeStamp = obj.get("timeStamp").getAsLong();
        }
        if(obj.get("imAsBase64") != null){
            this.imAsBase64 = obj.get("imAsBase64").getAsString();
        }
    }
    public ImageBean(String payload){
        if(payload != null){
            String[] payloadList = payload.split(",");
            if(payloadList.length == 2){
                this.timeStamp = Long.parseLong(payloadList[0]);
                this.imAsBase64 = payloadList[1];
            }
        }
    }


    public Bitmap getBitmap(){
        Log.e("ImageBean","getBitmap");
        //Log.e("ImageBean","imAsBase64: "+imAsBase64);
        byte[] decodeIM = Base64.decode(imAsBase64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodeIM, 0, decodeIM.length);
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getImAsBase64() {
        return imAsBase64;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setImAsBase64(String imAsBase64) {
        this.imAsBase64 = imAsBase64;
    }
}

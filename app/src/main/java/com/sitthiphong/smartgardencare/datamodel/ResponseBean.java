package com.sitthiphong.smartgardencare.datamodel;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Created by Sitthiphong on 7/21/2016 AD.
 */
public class ResponseBean {
    private String topic;
    private boolean success;
    private String message;

    public ResponseBean(String topic, boolean success, String message) {
        this.topic = topic;
        this.success = success;
        this.message = message;
    }

    public ResponseBean(String payload) {
        JsonObject object = new Gson().fromJson(payload,JsonObject.class);
        if(object.get("topic")!= null){
            this.topic = object.get("topic").getAsString();
        }
        if(object.get("success")!= null){
            this.success = object.get("success").getAsBoolean();
        }
        if(object.get("message")!=null){
            this.message = object.get("message").getAsString();
        }
    }

    public String getTopic() {
        return topic;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

package com.sitthiphong.smartgardencare.bean;

/**
 * Created by Sitthiphong on 7/20/2016 AD.
 */
public class PublishBean {
    private String topic;
    private String payload;

    public PublishBean(String topic, String payload) {
        this.topic = topic;
        this.payload = payload;
    }

    public String getTopic() {
        return topic;
    }

    public String getPayload() {
        return payload;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}

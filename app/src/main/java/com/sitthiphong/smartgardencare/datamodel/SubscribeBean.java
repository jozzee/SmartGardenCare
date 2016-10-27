package com.sitthiphong.smartgardencare.datamodel;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Created by jozze on 12/4/2559.
 */
public class SubscribeBean {

    private final String TAG = "SubscribeBean";
    private String topic;
    private String payload;
    private boolean retain;
    private long lastUpdated;

    public SubscribeBean() {
    }
    public SubscribeBean(String jsonString) {
        Gson gson = new Gson();
        JsonArray jsonArray = gson.fromJson(jsonString, JsonArray.class);
        JsonObject jsonObject = gson.fromJson(jsonArray.get(0), JsonObject.class);
        if(jsonObject.get("topic")!= null){
            this.topic = jsonObject.get("topic").getAsString();
            String[] topicList = topic.split("/");
            topic = topicList[2];
            /*if(topicList.length == 3){
                if(topicList[2].equals("")){
                    topic = topicList[2];
                }else{
                    topic = topicList[1];
                }
            }*/
        }
        if(jsonObject.get("payload")!= null){
            this.payload = jsonObject.get("payload").getAsString();
        }
        if(jsonObject.get("retain")!= null){
            this.retain = jsonObject.get("retain").getAsBoolean();
        }
        if(jsonObject.get("lastUpdated") != null){
            this.lastUpdated = jsonObject.get("lastUpdated").getAsLong();
        }
    }

    public String getTopic() {
        return topic;
    }

    public String getPayload() {
        return payload;
    }

    public boolean isRetain() {
        return retain;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public void setRetain(boolean retain) {
        this.retain = retain;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    /*long unixSeconds = 1372339860;
    Date date = new Date(unixSeconds*1000L); // *1000 is to convert seconds to milliseconds
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z"); // the format of your date
    sdf.setTimeZone(TimeZone.getTimeZone("GMT-4")); // give a timezone reference for formating (see comment at the bottom
    String formattedDate = sdf.format(date);
    System.out.println(formattedDate);*/
}

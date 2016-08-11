package com.sitthiphong.smartgardencare.service;

import android.util.Log;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;

/**
 * Created by jozze on 11/4/2559.
 */
public class NetPieRestApi {
    private static final String TAG = NetPieRestApi.class.getSimpleName();
    OkHttpClient client;
    String url;
    String appID;
    String appKey;
    String appSecret;

    public NetPieRestApi(String appID, String appKey, String appSecret){
        this.appID = appID;
        this.appKey = appKey;
        this.appSecret = appSecret;
    }
    public String publish(String topic, String message, boolean retain){
        Log.i(TAG,"onPublish");
        Log.i(TAG,"topic: "+topic);
        Log.i(TAG,"message: "+message);
        Log.i(TAG,"retain: "+ String.valueOf(retain));

        url = "https://api.netpie.io/topic/" +appID  +"/"+topic;
        if(retain){
            url+="?retain";
        }
        Log.i(TAG,"url to publish: "+url);
        return OKHttpPUT(url,appKey,appSecret,message);
    }

    public String subscribe(String topic)  {
        Log.i(TAG,"onSubscribe");
        Log.i(TAG,"topic: "+topic);
        url = "https://api.netpie.io/topic/" +appID  +"/"+topic +"?auth=" +appKey+":"+appSecret;
        Log.i(TAG,"url to subscribe: "+url);
        String result = "connectionLost";
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            client = new OkHttpClient();
            Response response = client.newCall(request).execute();
            if(response.isSuccessful()){
                result = response.body().string();
                Log.i(TAG,"subscribe success");
            }
            else{

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    public String chat(String alias, String message){
        //parameter "alias" is alias of microGear to chat
        //message ids message to chat
        Log.i(TAG,"onChat");
        Log.i(TAG,"alias: "+alias);
        Log.i(TAG,"message: "+message);

        url = "https://api.netpie.io/microgear/" +appID  +"/"+alias;
        Log.i(TAG,"url to chat: "+url);

        return  OKHttpPUT(url,appKey,appSecret,message);
    }
    public String putPostbox(String postboxName, String message, String tag){
        Log.i(TAG,"onPutPostbox");
        Log.i(TAG,"postboxName: "+postboxName);
        Log.i(TAG,"message: "+message);
        Log.i(TAG,"tag: "+tag);// tag is name off this message

        url = "https://api.netpie.io/postbox/"+appID +"/" +postboxName +"?tag=" +tag;
        Log.i(TAG,"url to putPostbox: "+url);
        return OKHttpPUT(url,appKey,appSecret,message);
    }
    public String getPostbox(String postboxName, String tag){
        String result = "connectionLost";
        url = "https://api.netpie.io/postbox/" +appID  +"/"+postboxName +"?";
        if(!tag.equals("")){
            url+=("tag="+tag+"&");
        }
        url+=("auth=" +appKey+":"+appSecret);
        Log.i(TAG,"url to getPostbox: "+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            client = new OkHttpClient();
            Response response = client.newCall(request).execute();
            if(response.isSuccessful()){
                result = response.body().string();
                Log.i(TAG,"subscribe success");
            }
            else{

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String OKHttpPUT(String url, final String username, final String password, String message){
        String result = "connectionLost";
        client = new OkHttpClient.Builder()
                .authenticator(new Authenticator() {
                    @Override
                    public Request authenticate(Route route, Response response) throws IOException {
                        String credential = Credentials.basic(username, password); //appKey is Username and appSecret is Password
                        return response.request().newBuilder()
                                .header("Authorization", credential)
                                .build();
                    }
                })
                .build();
        Log.i(TAG,"Authorization: username: "+username +", password: " +password);
        MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");
        Request request = new Request.Builder()
                .url(url)
                .put(RequestBody.create(MEDIA_TYPE_MARKDOWN,message))
                .build();
        try {
            Log.i(TAG,"PUT...");
            Response response = client.newCall(request).execute();
            if(response.isSuccessful()){
                Log.i(TAG,"put success");
                result = response.message();
            }
            else{
                Log.e(TAG,"put false");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;

    }
    public String getAppID() {
        return appID;
    }

    public String getUrl() {
        return url;
    }

    public String getAppKey() {
        return appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }
}

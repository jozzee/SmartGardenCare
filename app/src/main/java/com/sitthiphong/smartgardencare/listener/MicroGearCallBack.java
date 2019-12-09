package com.sitthiphong.smartgardencare.listener;

import android.app.Activity;
import android.util.Log;

import com.sitthiphong.smartgardencare.datamodel.RawDataBean;

import io.netpie.microgear.MicrogearEventListener;


/**
 * Created by Sitthiphong on 10/17/2016 AD.
 */

public class MicroGearCallBack implements MicrogearEventListener {
    private final String TAG = "MicroGearCallBack";

    private Activity activity;
    private RawDataBean rawDataBean;

    public MicroGearCallBack(Activity activity) {
        this.activity = activity;
    }


    @Override
    public void onConnect() {
           /* Message msg = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("myKey", "Now I'm connected with netpie");
            msg.setData(bundle);
            handler.sendMessage(msg);
            Log.i("Connected", "Now I'm connected with netpie");*/

    }

    @Override
    public void onMessage(String topic, String message) {
            /*Message msg = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("myKey", topic + " : " + message);
            msg.setData(bundle);
            handler.sendMessage(msg);
            Log.i("Message", topic + " : " + message);*/

        String[] subTopic = topic.split("/");
        if (subTopic.length == 3) {
            topic = subTopic[2];
        }
        subTopic = null;


        Log.i(TAG, "MicroGearCallBack, onMessage");
        Log.i(TAG, "topic: " + topic);
        Log.i(TAG, "message: " + message);

        /*if (topic.equals(ConfigData.rawDataTopic)) {
            String test = "{\"time\":1476584815,\"moisture\":{\"point2\": -1.0, \"point1\": -1.0, \"average\": -1},\"temp\":{\"point2\": 27.6, \"point1\": -1, \"average\": 27.6},\"light\":{\"light_out\": -1, \"light_in\": 9.16}}";
            rawDataBean = new RawDataBean(test);
            setUpdateRawData();
        }*/

    }

    @Override
    public void onPresent(String token) {
            /*Message msg = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("myKey", "New friend Connect :" + token);
            msg.setData(bundle);
            handler.sendMessage(msg);
            Log.i("present", "New friend Connect :" + token);*/

    }

    @Override
    public void onAbsent(String token) {
            /*Message msg = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("myKey", "Friend lost :" + token);
            msg.setData(bundle);
            handler.sendMessage(msg);
            Log.i("absent", "Friend lost :" + token);*/

    }

    @Override
    public void onDisconnect() {
           /* Message msg = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("myKey", "Disconnected");
            msg.setData(bundle);
            handler.sendMessage(msg);
            Log.i("disconnect", "Disconnected");*/

    }

    @Override
    public void onError(String error) {
           /* Message msg = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("myKey", "Exception : " + error);
            msg.setData(bundle);
            handler.sendMessage(msg);
            Log.i("exception", "Exception : " + error);*/

    }

    @Override
    public void onInfo(String info) {

    }

    private MicroGearCallBack getThis() {
        return this;
    }

}

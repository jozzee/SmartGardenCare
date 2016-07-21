package com.sitthiphong.smartgardencare.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.sitthiphong.smartgardencare.listener.NetworkChangeListener;

/**
 * Created by Sitthiphong on 7/20/2016 AD.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {
    public NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkChangeListener.onNetworkChange != null){
            networkChangeListener.onNetworkChange.onNetworkChange((networkInfo != null && networkInfo.isConnected())?true:false);

        }
    }
}

package com.sitthiphong.smartgardencare.libs;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Sitthiphong on 10/17/2016 AD.
 */

public class Network {
    public static boolean isConnectingInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected()) ? true : false;
    }
}

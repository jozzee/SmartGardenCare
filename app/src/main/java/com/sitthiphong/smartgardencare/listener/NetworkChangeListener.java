package com.sitthiphong.smartgardencare.listener;

/**
 * Created by Sitthiphong on 7/20/2016 AD.
 */
public class NetworkChangeListener {
    public static OnNetworkChange onNetworkChange = null;
    public interface OnNetworkChange{
        public void onNetworkChange(boolean isConnect);
    }
    public void setNetworkChange (OnNetworkChange listener){
        onNetworkChange = listener;
    }
}

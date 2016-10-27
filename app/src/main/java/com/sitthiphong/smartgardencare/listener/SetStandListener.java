package com.sitthiphong.smartgardencare.listener;

import com.google.gson.JsonObject;

/**
 * Created by Sitthiphong on 10/23/2016 AD.
 */

public class SetStandListener {
    public interface OnSetStandardListener{
        public void onSetStandardListener(String sensor,int val);
    }
    public interface SetWaitDialog{
        public void setWaitDialog();
    }
    public interface Result{
        public void result(boolean result,String error);
    }
}

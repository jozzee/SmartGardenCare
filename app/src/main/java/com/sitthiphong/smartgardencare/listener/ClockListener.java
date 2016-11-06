package com.sitthiphong.smartgardencare.listener;

/**
 * Created by Sitthiphong on 11/5/2016 AD.
 */

public class ClockListener {
    public interface OnSaveClockListener{
        public void onSaveClockListener(String clockSets);
    }
    public interface OnFinishSaveClockListener{
        public void onFinishSaveClockListener(int status);
    }
}

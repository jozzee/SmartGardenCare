package com.sitthiphong.smartgardencare.datamodel;

/**
 * Created by Sitthiphong on 10/21/2016 AD.
 */

public class LogDataBean {
    private long time;
    private int working;
    private int type;
    private float val_berfore;
    private float val_after;

    public LogDataBean(long time, int working, int type, float val_berfore, float val_after) {
        this.time = time;
        this.working = working;
        this.type = type;
        this.val_berfore = val_berfore;
        this.val_after = val_after;
    }

    public long getTime() {
        return time;
    }

    public int getWorking() {
        return working;
    }

    public int getType() {
        return type;
    }

    public float getVal_berfore() {
        return val_berfore;
    }

    public float getVal_after() {
        return val_after;
    }
}

package com.sitthiphong.smartgardencare.datamodel;

/**
 * Created by Sitthiphong on 10/22/2016 AD.
 */

public class RawDataListBean {
    private long time;
    private float mos1,mos2,tmp1,tmp2,light_in,light_out;

    public RawDataListBean(long time, float mos1, float mos2, float tmp1, float tmp2, float light_in, float light_out) {
        this.time = time;
        this.mos1 = mos1;
        this.mos2 = mos2;
        this.tmp1 = tmp1;
        this.tmp2 = tmp2;
        this.light_in = light_in;
        this.light_out = light_out;
    }

    public long getTime() {
        return time;
    }

    public float getMos1() {
        return mos1;
    }

    public float getMos2() {
        return mos2;
    }

    public float getTmp1() {
        return tmp1;
    }

    public float getTmp2() {
        return tmp2;
    }

    public float getLight_in() {
        return light_in;
    }

    public float getLight_out() {
        return light_out;
    }
}

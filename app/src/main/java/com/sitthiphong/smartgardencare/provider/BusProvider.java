package com.sitthiphong.smartgardencare.provider;


import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by Jozzee on 8/10/2558.
 */
public final class BusProvider {
    private static final Bus BUS = new Bus(ThreadEnforcer.ANY);

    public static Bus getInstance() {
        return BUS;
    }
    private BusProvider() {
        // No instances.
    }



}

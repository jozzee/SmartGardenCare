package com.sitthiphong.smartgardencare.provider;

import java.text.SimpleDateFormat;

/**
 * Created by zaade on 31/5/2559.
 */
public final class SimpleDateProvider {
    private static final SimpleDateFormat dateTime = new SimpleDateFormat("HH:mm dd-MM-yyyy",java.util.Locale.US);
    private static final SimpleDateFormat minHour = new SimpleDateFormat("HH:mm",java.util.Locale.US);

    private SimpleDateProvider() {
    }
    public static SimpleDateFormat getInstance(){
        return dateTime;
    }
    public static SimpleDateFormat getMinHour(){
        return minHour;
    }

}

package com.sitthiphong.smartgardencare.provider;

import com.google.gson.Gson;

/**
 * Created by zaade on 26/5/2559.
 */
public final class GsonProvider {
    private static final Gson gson = new Gson();

    private GsonProvider() {
    }
    public static Gson getInstance(){
        return gson;
    }
}

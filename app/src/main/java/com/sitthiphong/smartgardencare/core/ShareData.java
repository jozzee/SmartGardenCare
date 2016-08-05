package com.sitthiphong.smartgardencare.core;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

/**
 * Created by Sitthiphong on 8/5/2016 AD.
 */
public class ShareData {
    private static Context context ;
    private static SharedPreferences sp;
    private static SharedPreferences.Editor spEditor;

    public ShareData(Context context) {
        this.context = context;
    }
    public static void createSharePreference (String preferenceName){ //FavoriteSick
        sp = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        spEditor = sp.edit();
    }
    public void putString(String key,String var){
        spEditor.putString(key,var);
        spEditor.commit();
    }
    public void putInt(String key,int var){
        spEditor.putInt(key,var);
        spEditor.commit();
    }


}


//    SharedPreferences.Editor putString(String var1, String var2);
//
//    SharedPreferences.Editor putStringSet(String var1, Set<String> var2);
//
//    SharedPreferences.Editor putInt(String var1, int var2);
//
//    SharedPreferences.Editor putLong(String var1, long var2);
//
//    SharedPreferences.Editor putFloat(String var1, float var2);
//
//    SharedPreferences.Editor putBoolean(String var1, boolean var2);
//
//    SharedPreferences.Editor remove(String var1);
//
//    SharedPreferences.Editor clear();

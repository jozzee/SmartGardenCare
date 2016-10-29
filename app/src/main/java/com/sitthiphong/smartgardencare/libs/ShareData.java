package com.sitthiphong.smartgardencare.libs;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sitthiphong.smartgardencare.datamodel.ConfigData;

import java.util.Locale;
import java.util.Set;

/**
 * Created by Sitthiphong on 8/5/2016 AD.
 */
public class ShareData {
    private Context context;
    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;

    public ShareData(Context context) {
        this.context = context;
    }

    public void createSharePreference() { //FavoriteSick
        sp = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
        spEditor = sp.edit();
    }

    public void createSharePreference(String preferencesName) { //FavoriteSick
        sp = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        spEditor = sp.edit();
    }

    public void putString(String key, String s) {
        spEditor.putString(key, s);
        spEditor.commit();
    }

    public void putStringSet(String key, Set<String> var) {
        spEditor.putStringSet(key, var);
        spEditor.commit();

    }

    public void putInt(String key, int i) {
        spEditor.putInt(key, i);
        spEditor.commit();
    }

    public void putLong(String key, long l) {
        spEditor.putLong(key, l);
        spEditor.commit();
    }

    public void putFloat(String key, float v) {
        spEditor.putFloat(key, v);
        spEditor.commit();
    }

    public void putBoolean(String key, boolean b) {
        spEditor.putBoolean(key, b);
        spEditor.commit();
    }

    public void remove(String key) {
        spEditor.remove(key);
        spEditor.commit();
    }

    public void clear() {
        spEditor.clear();
        spEditor.commit();

    }

    public String getString(String key, String defaultValue) {
        return sp.getString(key, defaultValue);
    }

    public int getInt(String key, int defaultValue) {
        return sp.getInt(key, defaultValue);
    }

    public Set<String> getStringSet(String key, Set<String> defaultValue) {
        return sp.getStringSet(key, defaultValue);
    }

    public long getLong(String key, long defaultValue) {
        return sp.getLong(key, defaultValue);
    }

    public float getFloat(String key, float defaultValue) {
        return sp.getFloat(key, defaultValue);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return sp.getBoolean(key, defaultValue);
    }

    public SharedPreferences getSharedPreferences() {
        return sp;
    }

    public void putToken(String token) {
        spEditor.putString(ConfigData.token, token);
        spEditor.commit();
    }

    public void putAppId(String appId) {
        spEditor.putString(ConfigData.appId, appId);
        spEditor.commit();
    }

    public void putKey(String key) {
        spEditor.putString(ConfigData.key, key);
        spEditor.commit();
    }

    public void putSecret(String secret) {
        spEditor.putString(ConfigData.secret, secret);
        spEditor.commit();
    }

    public void putFqPData(int fqPData) {
        spEditor.putInt(ConfigData.fqPData, fqPData);
        spEditor.commit();
    }

    public void putFqPImage(int fqPImage) {
        spEditor.putInt(ConfigData.fqPImage, fqPImage);
        spEditor.commit();
    }

    public void putFqIData(int fqIData) {
        spEditor.putInt(ConfigData.fqIData, fqIData);
        spEditor.commit();
    }

    public void putFqShower(int fqShower) {
        spEditor.putInt(ConfigData.fqShower, fqShower);
        spEditor.commit();
    }

    public void putAgeData(int ageData) {
        spEditor.putInt(ConfigData.ageData, ageData);
        spEditor.commit();
    }

    public void putAutoMode(boolean autoMode) {
        spEditor.putBoolean(ConfigData.autoMode, autoMode);
        spEditor.commit();
    }
    public int getMoistureStandard(){
        return sp.getInt(ConfigData.mosStd,20);
    }
    public void putMoistureStandard(int mosStd){
        spEditor.putInt(ConfigData.mosStd,mosStd);
        spEditor.commit();
    }
    public int getTempStandard(){
        return sp.getInt(ConfigData.tmpStd,40);
    }
    public void putTempStandard(int tmpStd){
        spEditor.putInt(ConfigData.tmpStd,tmpStd);
        spEditor.commit();
    }
    public int getLightStandard(){
        return sp.getInt(ConfigData.ligStd,5000);
    }
    public void putLightStandard(int ligStd){
        spEditor.putInt(ConfigData.ligStd,ligStd);
        spEditor.commit();
    }
    public void setLang(String lang){
        spEditor.putString("language",lang);
        spEditor.commit();
    }
    public String getLang(){
        return sp.getString("language","en");
    }

    public String getPreferencesAsObjString() {
        JsonObject detailsObj = new JsonObject();
        detailsObj.addProperty(ConfigData.fqPData, getFqPData());
        detailsObj.addProperty(ConfigData.fqPImage, getFqPImage());
        detailsObj.addProperty(ConfigData.fqIData, getFqIData());
        detailsObj.addProperty(ConfigData.fqShower, getFqShower());
        detailsObj.addProperty(ConfigData.ageData, getAgeData());
        detailsObj.addProperty(ConfigData.autoMode, isAutoMode());
        return new Gson().toJson(detailsObj);
    }
    public String getToken(){
        return sp.getString(ConfigData.token,"");

    }
    public void removeSendPrefer(){
        spEditor.remove("sendPrefer");
        spEditor.commit();
    }
    public boolean getSendPrefer(){
        return sp.getBoolean("sendPrefer", false);
    }
    public void putSendPrefer(){
        spEditor.putBoolean("sendPrefer",true);
        spEditor.commit();
    }

    public String getAppId() {
        return sp.getString(ConfigData.appId, "");
    }

    public String getAppKey() {
        return sp.getString(ConfigData.key, "");
    }

    public String getAppSecret() {
        return sp.getString(ConfigData.secret, "");
    }

    public int getFqPData() {
        return sp.getInt(ConfigData.fqPData, 1); //1,5,10,20,30
    }

    public int getFqPImage() {
        return sp.getInt(ConfigData.fqPImage, 1);//1,2,3,6,12
    }

    public int getFqIData() {
        return sp.getInt(ConfigData.fqIData, 1);//1,2,3,6,12
    }

    public int getFqShower() {
        return sp.getInt(ConfigData.fqShower, 0);//0,1,5,10,15,20
    }

    public int getAgeData() {
        return sp.getInt(ConfigData.ageData, 3);//1,2,3
    }

    public boolean isAutoMode() {
        return sp.getBoolean(ConfigData.autoMode, true);//1,2,3
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

package com.sitthiphong.smartgardencare.provider.event;

import com.google.gson.JsonObject;

/**
 * Created by Sitthiphong on 7/20/2016 AD.
 */
public class OnSaveSettingEvent {
    private boolean changeNETPIE;
    private boolean changeDetails;
    private JsonObject objNETPIE;
    private JsonObject objDetails;

    public OnSaveSettingEvent(boolean changeNETPIE, boolean changeDetails, JsonObject objNETPIE, JsonObject objDetails) {
        this.changeNETPIE = changeNETPIE;
        this.changeDetails = changeDetails;
        this.objNETPIE = objNETPIE;
        this.objDetails = objDetails;
    }

    public boolean isChangeNETPIE() {
        return changeNETPIE;
    }

    public JsonObject getObjNETPIE() {
        return objNETPIE;
    }

    public JsonObject getObjDetails() {
        return objDetails;
    }

    public boolean isChangeDetails() {
        return changeDetails;
    }

    public void setChangeNETPIE(boolean changeNETPIE) {
        this.changeNETPIE = changeNETPIE;
    }

    public void setChangeDetails(boolean changeDetails) {
        this.changeDetails = changeDetails;
    }

    public void setObjNETPIE(JsonObject objNETPIE) {
        this.objNETPIE = objNETPIE;
    }

    public void setObjDetails(JsonObject objDetails) {
        this.objDetails = objDetails;
    }
}

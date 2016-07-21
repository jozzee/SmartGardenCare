package com.sitthiphong.smartgardencare.bean;

import com.google.gson.JsonObject;

/**
 * Created by Sitthiphong on 7/21/2016 AD.
 */
public class SaveSettingBean {
    private boolean changeNETPIE;
    private boolean changeDetails;
    private JsonObject objNETPIE;
    private JsonObject objDrtails;

    public SaveSettingBean(boolean changeNETPIE, boolean changeDetails, JsonObject objNETPIE, JsonObject objDrtails) {
        this.changeNETPIE = changeNETPIE;
        this.changeDetails = changeDetails;
        this.objNETPIE = objNETPIE;
        this.objDrtails = objDrtails;
    }

    public boolean isChangeNETPIE() {
        return changeNETPIE;
    }

    public boolean isChangeDetails() {
        return changeDetails;
    }

    public JsonObject getObjNETPIE() {
        return objNETPIE;
    }

    public JsonObject getObjDrtails() {
        return objDrtails;
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

    public void setObjDrtails(JsonObject objDrtails) {
        this.objDrtails = objDrtails;
    }
}

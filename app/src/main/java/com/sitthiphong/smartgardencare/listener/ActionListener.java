package com.sitthiphong.smartgardencare.listener;

import com.google.gson.JsonObject;
import com.sitthiphong.smartgardencare.bean.ImageBean;
import com.sitthiphong.smartgardencare.bean.RawDataBean;
import com.sitthiphong.smartgardencare.bean.StatusBean;

import java.util.List;

/**
 * Created by Sitthiphong on 7/20/2016 AD.
 */
public class ActionListener {
    public static OnFinishSetupNETPIE onFinishSetupNETPIE = null;
    public static OnException onException = null;
    public static OnNoInternet onNoInternet = null;
    public static OnSaveSetting onSaveSetting = null;
    public static OnRequestUpdateImage onRequestUpdateImage = null;
    public static OnUpdateImage onUpdateImage =null;
    public static OnRequestRawData onRequestRawData = null;
    public static OnUpdateRawData onUpdateRawData = null;
    public static OnRequestLog onRequestLog = null;
    public static OnUpdateLog onUpdateLog = null;
    public static OnRequestRawBean onRequestRawBean = null;
    public static OnRequestRawList onRequestRawList = null;
    public static OnUpdateRawBean onUpdateRawBean = null;
    public static OnUpdateRawList onUpdateRawList = null;

    public interface OnFinishSetupNETPIE{
        public void onFinishSetupNETPIE(boolean event);
    }
    public void setOnFinishSetupNETPIE(OnFinishSetupNETPIE listener){
        onFinishSetupNETPIE = listener;
    }

    public interface OnSaveSetting{
        public void onSaveSetting(boolean changeNETPIE,boolean changeDetails, JsonObject objNETPIE,JsonObject objDetails);
    }
    public void setOnSaveSetting(OnSaveSetting listener){
        onSaveSetting = listener;
    }

    public interface OnException{
        public void onException(String error);
    }
    public void setOnException(OnException listener){
        onException = listener;
    }

    public interface OnNoInternet{
        public void onNoInternet(String error);
    }
    public void setOnNoInternet(OnNoInternet listener){
        onNoInternet = listener;
    }

    public interface OnRequestUpdateImage{
        public void onRequestUpdateImage();
    }
    public void setOnRequestUpdateImage(OnRequestUpdateImage listener){
        onRequestUpdateImage = listener;
    }
    public interface OnUpdateImage{
        public void onUpdateImage(StatusBean statusBean, ImageBean imageBean);
    }
    public void setOnUpdateImage(OnUpdateImage listener){
        onUpdateImage = listener;
    }
    public interface OnRequestRawData{
        public void OnRequestRawData();
    }
    public void setOnRequestRawData(OnRequestRawData listener){
        onRequestRawData = listener;
    }
    public interface OnUpdateRawData{
        public void OnUpdateRawDat(StatusBean statusBean, RawDataBean rawDataBean);
    }
    public void setOnUpdateRawData(OnUpdateRawData listener){
        onUpdateRawData = listener;
    }

    public interface OnRequestLog{
        public void onRequestLog();
    }
    public void setOnRequestLog(OnRequestLog listener){
        onRequestLog = listener;
    }
    public interface OnUpdateLog{
        public void onUpdateLog(StatusBean statusBean,String logListAsJsonString);
    }
    public void setOnUpdateLog(OnUpdateLog listener){
        onUpdateLog = listener;
    }
    public interface OnRequestRawBean{
        public void onRequestRawBean();
    }
    public void setOnRequestRawBean(OnRequestRawBean listener){
        onRequestRawBean = listener;
    }

    public interface OnRequestRawList{
        public void onRequestRawList();
    }
    public void setOnRequestRawList(OnRequestRawList listener){
        onRequestRawList = listener;
    }
    public interface OnUpdateRawBean{
        public void onUpdateRawBean(RawDataBean rawBean);
    }
    public void setOnUpdateRawBean(OnUpdateRawBean listener){
        onUpdateRawBean = listener;
    }
    public interface OnUpdateRawList{
        public void onUpdateRawList(String rawListAsJsonString);
    }
    public void setOnUpdateRawList(OnUpdateRawList listener){
        onUpdateRawList = listener;
    }






}

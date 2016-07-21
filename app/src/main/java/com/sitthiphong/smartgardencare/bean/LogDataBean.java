package com.sitthiphong.smartgardencare.bean;

/**
 * Created by zaade on 24/6/2559.
 */
public class LogDataBean {
    private int lid;
    private String action;
    private String typeAction;
    private long timeStamp;
    private String note;

    public LogDataBean(int lid, String action, String typeAction, long timeStamp, String note) {
        this.lid = lid;
        this.action = action;
        this.typeAction = typeAction;
        this.timeStamp = timeStamp;
        this.note = note;
    }

    public int getLid() {
        return lid;
    }

    public String getAction() {
        return action;
    }

    public String getTypeAction() {
        return typeAction;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setLid(int lid) {
        this.lid = lid;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setTypeAction(String typeAction) {
        this.typeAction = typeAction;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}

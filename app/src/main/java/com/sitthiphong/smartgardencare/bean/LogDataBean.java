package com.sitthiphong.smartgardencare.bean;

/**
 * Created by zaade on 24/6/2559.
 */
public class LogDataBean {
    private int id;
    private String action;
    private String actionType;
    private long time;
    private String note;

    public LogDataBean(int id, String action, String actionType, long time, String note) {
        this.id = id;
        this.action = action;
        this.actionType = actionType;
        this.time = time;
        this.note = note;
    }

    public int getId() {
        return id;
    }

    public String getAction() {
        return action;
    }

    public String getActionType() {
        return actionType;
    }

    public long getTime() {
        return time;
    }

    public String getNote() {
        return note;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setNote(String note) {
        this.note = note;
    }
}

package com.sitthiphong.smartgardencare.datamodel;

/**
 * Created by zaade on 24/6/2559.
 */
public class LogDataBean {
    private int id;
    private int action;
    private int type;
    private long time;
    private String note;

    public LogDataBean(int id, int action, int type, long time, String note) {
        this.id = id;
        this.action = action;
        this.type = type;
        this.time = time;
        this.note = note;
    }

    public int getId() {
        return id;
    }

    public int getAction() {
        return action;
    }

    public int getType() {
        return type;
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

    public void setAction(int action) {
        this.action = action;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setNote(String note) {
        this.note = note;
    }
}

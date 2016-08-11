package com.sitthiphong.smartgardencare.datamodel;

/**
 * Created by Sitthiphong on 7/20/2016 AD.
 */
public class StatusBean {
    private int status;
    private String exception;

    public StatusBean(int status, String exception) {
        this.status = status;
        this.exception = exception;
    }

    public int getStatus() {
        return status;
    }

    public String getException() {
        return exception;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setException(String exception) {
        this.exception = exception;
    }
}

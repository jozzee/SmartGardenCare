package com.sitthiphong.smartgardencare.datamodel;

import java.util.List;

/**
 * Created by Sitthiphong on 11/3/2016 AD.
 */

public class AlarmClockBean {
    private int hour, minute, working;
    private boolean isOpen,isRepeat;
    private List<String> daysSet;

    public AlarmClockBean(int hour, int minute, int working, boolean isOpen, boolean isRepeat,List<String> daysSet) {
        this.hour = hour;
        this.minute = minute;
        this.working = working;
        this.isOpen = isOpen;
        this.isRepeat = isRepeat;
        this.daysSet = daysSet;
    }


    public String getTimeText() {
        if ((hour >= 0) && (minute >= 0)) {
            if (hour < 10 && minute < 10) {
                return "0" + String.valueOf(hour) + ":0" + String.valueOf(minute);
            } else if (hour < 10) {
                return "0" + String.valueOf(hour) + ":" + String.valueOf(minute);
            } else if (minute < 10) {
                return String.valueOf(hour) + ":0" + String.valueOf(minute);
            } else {
                return String.valueOf(hour) + ":" + String.valueOf(minute);
            }
        } else {
            return "09:09";
        }

    }

    public boolean isRepeat() {
        return isRepeat;
    }

    public void setRepeat(boolean repeat) {
        isRepeat = repeat;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getWorking() {
        return working;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public List<String> getDaysSet() {
        return daysSet;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public void setWorking(int working) {
        this.working = working;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public void setDaysSet(List<String> daysSet) {
        this.daysSet = daysSet;
    }
}

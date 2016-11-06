package com.sitthiphong.smartgardencare.libs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import com.sitthiphong.smartgardencare.R;
import com.sitthiphong.smartgardencare.adapter.AlarmClockAdapter;

import java.util.Calendar;

/**
 * Created by Sitthiphong on 11/1/2016 AD.
 */

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

    private OnTimeSetListener onTimeSetListener = null;
    private int hour = -1, minute = -1;

    public TimePickerFragment() {
    }

    public TimePickerFragment(OnTimeSetListener onTimeSetListener) {
        this.onTimeSetListener = onTimeSetListener;
    }

    public TimePickerFragment(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public TimePickerFragment(OnTimeSetListener onTimeSetListener, int hour, int minute) {
        this.onTimeSetListener = onTimeSetListener;
        this.hour = hour;
        this.minute = minute;

    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if ((hour < 0) || (minute < 0) || (hour > 24) || (minute > 60)) {
            //Use the current time as the default values for the time picker
            final Calendar c = Calendar.getInstance();
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);
        }

        TimePickerDialog timePickerDialog;
        timePickerDialog = new TimePickerDialog(
                getActivity(),
                this,
                hour,
                minute,
                DateFormat.is24HourFormat(getActivity())
        );
        return timePickerDialog;


    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        if (onTimeSetListener != null) {
            onTimeSetListener.onTimeSet(timePicker, hourOfDay, minute, getTimeText(hourOfDay, minute));
        }
    }

    private String getTimeText(int h, int m) {
        if (h < 10 && m < 10) {
            return "0" + String.valueOf(h) + ":0" + String.valueOf(m);

        } else if (h < 10) {
            return "0" + String.valueOf(h) + ":" + String.valueOf(m);

        } else if (m < 10) {
            return String.valueOf(h) + ":0" + String.valueOf(m);
        } else {
            return String.valueOf(h) + ":" + String.valueOf(m);
        }
    }

    public void setOnTimeSetListener(OnTimeSetListener onTimeSetListener) {
        this.onTimeSetListener = onTimeSetListener;
    }


    public interface OnTimeSetListener {
        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute, String timeText);
    }


}

package com.sitthiphong.smartgardencare.core;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.sitthiphong.smartgardencare.R;
import com.sitthiphong.smartgardencare.listener.ActionListener;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

/**
 * Created by jozze on 18/7/2559.
 */
public class MagDiscreteSeekBar {
    private View view;
    private DiscreteSeekBar seekBar;
    private int seekBarId;
    private TextView tvValue;
    private String unit;
    private int colorId;
    private int maxValue;
    private int minValue;
    private int progress;
    private int val;
    private String sensor;


    public MagDiscreteSeekBar(View view, int seekBarId,
                              TextView tvValue, String unit, int colorId,
                              int maxValue, int minValue, int progress,String sensor){
        this.view = view;
        this.seekBarId = seekBarId;
        this.tvValue = tvValue;
        this.unit = unit;
        this.colorId = colorId;
        this.maxValue = maxValue;
        this.minValue = minValue;
        this.progress = progress;
        this.sensor = sensor;
        val = progress;

        seekBar = (DiscreteSeekBar)view.findViewById(seekBarId);

    }
    public void createSeekBar(){
        seekBar.setMax(maxValue);
        seekBar.setMin(minValue);
        seekBar.setProgress(progress);
        seekBar.setRippleColor(colorId);
        seekBar.setScrubberColor(colorId);
        seekBar.setThumbColor(colorId, colorId);
        seekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                val = value;
                tvValue.setText(String.valueOf(value)+unit);


            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                Log.e("TAG","จบแล้ววววว");
                JsonObject obj = new JsonObject();
                obj.addProperty("sensor",sensor);
                obj.addProperty("value",getValue());
                new ActionListener().onSaveStandard.onSaveStandard(obj);
            }
        });
    }
    public void setProgress(int progress){
        seekBar.setProgress(progress);
    }
    public int getValue(){
        return val;
    }

}

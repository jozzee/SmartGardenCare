package com.sitthiphong.smartgardencare.libs;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.sitthiphong.smartgardencare.R;
import com.sitthiphong.smartgardencare.activity.DetailsActivity;
import com.sitthiphong.smartgardencare.datamodel.ConfigData;
import com.sitthiphong.smartgardencare.listener.SetStandListener;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

/**
 * Created by jozze on 18/7/2559.
 */
public class MagDiscreteSeekBar extends AppCompatActivity {

    private static SetStandListener.OnSetStandardListener onSetStandardListener = null;
    private SetStandListener.SetWaitDialog setWaitDialog = null;

    private final String TAG = "MagDiscreteSeekBar";
    private View view;
    private DiscreteSeekBar seekBar;
    private int seekBarId;
    private TextView tvValue;
    private String unit;
    private int colorId;
    private int maxValue;
    private int minValue;
    private int oldVal;
    private int val;
    private String sensor;

    public static void setOnSetStandardListener(SetStandListener.OnSetStandardListener onSetStandardListener) {
        MagDiscreteSeekBar.onSetStandardListener = onSetStandardListener;
    }


   /* public MagDiscreteSeekBar(View view, int seekBarId,
                              TextView tvValue, String unit, int colorId,
                              int maxValue, int minValue, int progress, String sensor) {
        this.view = view;
        this.seekBarId = seekBarId;
        this.tvValue = tvValue;
        this.unit = unit;
        this.colorId = colorId;
        this.maxValue = maxValue;
        this.minValue = minValue;
        //this.progress = progress;
        this.sensor = sensor;
        val = progress;

        seekBar = (DiscreteSeekBar) view.findViewById(seekBarId);

    }*/
    public MagDiscreteSeekBar(View view,
                              TextView tvValue,
                              SetStandListener.SetWaitDialog setWaitDialog) {
        this.view = view;
        this.tvValue = tvValue;
        this.setWaitDialog = setWaitDialog;
        seekBar = (DiscreteSeekBar) view.findViewById(R.id.seekBarValue);

    }
    public void setOldValue(){
        seekBar.setProgress(oldVal);
        tvValue.setText(String.valueOf(oldVal) + unit);

    }
    public void setNewValue(){
        oldVal = val;
        seekBar.setProgress(val);
    }

    public void createSeekBar() {
        seekBar.setMax(maxValue);
        seekBar.setMin(minValue);
        seekBar.setProgress(val);
        seekBar.setRippleColor(colorId);
        seekBar.setScrubberColor(colorId);
        seekBar.setThumbColor(colorId, colorId);
        seekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                val = value;
                tvValue.setText(String.valueOf(value) + unit);

            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                Log.e(TAG, "จบแล้ววววว");
                Log.e(TAG,"value: "+val);
                if(onSetStandardListener != null){
                    onSetStandardListener.onSetStandardListener(sensor,val);
                }
                if(setWaitDialog != null){
                    setWaitDialog.setWaitDialog();
                }


                /*JsonObject obj = new JsonObject();
                SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("Details", MODE_PRIVATE);
                if (sensor.equals(ConfigData.mosStd)) {
                    obj.addProperty("moisture", getValue());
                    obj.addProperty("temp", sharedPreferences.getFloat("temp", (float) 40.00));
                    obj.addProperty("light", sharedPreferences.getFloat("light", (float) 5000.00));
                } else if (sensor.equals(ConfigData.tmpStd)) {
                    obj.addProperty("moisture", sharedPreferences.getFloat("moisture", (float) 20.00));
                    obj.addProperty("temp", getValue());
                    obj.addProperty("light", sharedPreferences.getFloat("light", (float) 5000.00));
                } else if (sensor.equals(ConfigData.ligStd)) {
                    obj.addProperty("moisture", sharedPreferences.getFloat("moisture", (float) 20.00));
                    obj.addProperty("temp", sharedPreferences.getFloat("temp", (float) 40.00));
                    obj.addProperty("light", getValue());
                }*/
                //new ActionListener().onSaveStandard.onSaveStandard(obj);
            }
        });
    }

    public void setProgressSeekBar(int progress) {
        seekBar.setProgress(progress);
    }

    public int getValue() {
        return val;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setColorId(int colorId) {
        this.colorId = colorId;
    }

    public void setVal(int val) {
        this.val = val;
        this.oldVal = val;
    }

    public void setSensor(String sensor) {
        this.sensor = sensor;
    }
}

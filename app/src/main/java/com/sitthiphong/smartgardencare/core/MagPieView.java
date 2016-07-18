package com.sitthiphong.smartgardencare.core;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import com.sitthiphong.smartgardencare.R;

import az.plainpie.PieView;

/**
 * Created by jozze on 18/7/2559.
 */
public class MagPieView {
    private Context context;
    private View view;
    private PieView pieView;
    private int pieViewId;
    private float value;
    private String unit;
    private int colorId;

    public MagPieView(Context context,View view, int pieViewId, float value, String unit, int colorId) {
        this.context = context;
        this.view = view;
        this.pieViewId = pieViewId;
        this.value = value;
        this.unit = unit;
        this.colorId = colorId;

        pieView = (PieView)view.findViewById(pieViewId);

    }
    public void createPieView(){
        pieView.setmPercentage(value);
        pieView.setInnerText(String.valueOf(value)+unit);
        pieView.setPercentageBackgroundColor(colorId);
        pieView.setPercentageTextSize(context.getResources().getInteger(R.integer.percentage_size_moisture));
    }
}

package com.sitthiphong.smartgardencare.core;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import com.sitthiphong.smartgardencare.R;

import java.math.BigDecimal;

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

    public MagPieView(Context context,View view, int pieViewId,String unit, int colorId){

    }
    public MagPieView(Context context,View view, int pieViewId, float value, String unit, int colorId) {
        this.context = context;
        this.view = view;
        this.pieViewId = pieViewId;
        this.value = value;
        this.unit = unit;
        this.colorId = colorId;
        pieView = (PieView)view.findViewById(pieViewId);
        pieView.setPercentageBackgroundColor(colorId);
        pieView.setPercentageTextSize(context.getResources()
                .getInteger(R.integer.percentage_size_moisture));

        if(value>0){
            pieView.setmPercentage(value);
            pieView.setInnerText(String.valueOf(value)+unit);
        }
        else {
            pieView.setmPercentage(0);
            pieView.setInnerText("0 %");

        }
    }

    public void setValue(float value) {
        this.value = value;
        if(value>0){
            if(colorId == ContextCompat.getColor(context,R.color.blue)){
                //BigDecimal v = new BigDecimal(value).setScale(2,BigDecimal.ROUND_HALF_UP);

                pieView.setmPercentage(value);
                pieView.setInnerText(String.valueOf(value)+unit);
            }
            else if(colorId == ContextCompat.getColor(context,R.color.deepOrange)){
                pieView.setmPercentage((value*2));
                pieView.setInnerText(String.valueOf(value)+unit);

            }
            else if(colorId == ContextCompat.getColor(context,R.color.amber)){
                pieView.setmPercentage((value/200));
                pieView.setInnerText(String.valueOf(value)+unit);
            }

        }
        else {
            pieView.setmPercentage(0);
            pieView.setInnerText("0 %");
        }
    }
    public void setVisibility(int visibility){
        if(pieView != null){
            pieView.setVisibility(visibility);
        }
    }
}

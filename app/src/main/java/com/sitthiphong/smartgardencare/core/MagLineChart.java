package com.sitthiphong.smartgardencare.core;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.sitthiphong.smartgardencare.R;
import com.sitthiphong.smartgardencare.activity.MyMarkerView;
import com.sitthiphong.smartgardencare.bean.RawDataBean;
import com.sitthiphong.smartgardencare.bean.SubscribeBean;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jozze on 17/7/2559.
 */
public class MagLineChart implements OnChartGestureListener, OnChartValueSelectedListener {
    private String TAG = "MagLineChart";
    private Context context;
    private View view;
    private int lineChartId;
    private LineChart mChart;
    private int type;
    private  List<RawDataBean> rawList;

    public MagLineChart(Context context,View view, int lineChartId,int type, List<RawDataBean> rawList) {
        this.context = context;
        this.view = view;
        this.lineChartId = lineChartId;
        this.type = type;
        this.rawList = rawList;
    }
    public void createLineChart(MagScreen magScreen){
        mChart =  (LineChart)view.findViewById(lineChartId);
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
                magScreen.getWidthLineChart(),magScreen.getHeightLineChart());
        mChart.setLayoutParams(param);
    }
    public void drawLineChart(){
        mChart.setOnChartGestureListener(this);
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawGridBackground(false);

        // no description text
        mChart.setDescription("");
        mChart.setNoDataTextDescription("You need to provide data for the chart.");

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        // mChart.setScaleXEnabled(true);
        // mChart.setScaleYEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        // mChart.setBackgroundColor(Color.GRAY);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        MyMarkerView mv = new MyMarkerView(context, R.layout.custom_marker_view);

        // set the marker to the chart
        mChart.setMarkerView(mv);


        // x-axis limit line
        LimitLine llXAxis = new LimitLine(10f, "Index 10");
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextSize(10f);

        XAxis xAxis = mChart.getXAxis();
        //xAxis.setValueFormatter(new MyCustomXAxisValueFormatter());
        //xAxis.addLimitLine(llXAxis); // add x-axis limit line

        //Typeface tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

        /*LimitLine ll1 = new LimitLine(130f, "Upper Limit");
        ll1.setLineWidth(4f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);
        //ll1.setTypeface(tf);
        LimitLine ll2 = new LimitLine(-30f, "Lower Limit");
        ll2.setLineWidth(4f);
        ll2.enableDashedLine(10f, 10f, 0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setTextSize(10f);
        //ll2.setTypeface(tf);*/

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        //leftAxis.addLimitLine(ll1);
        //leftAxis.addLimitLine(ll2);
        //leftAxis.setYOffset(20f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        mChart.getAxisRight().setEnabled(false);

        //mChart.getViewPortHandler().setMaximumScaleY(2f);
        //mChart.getViewPortHandler().setMaximumScaleX(2f);

        // add data
        if(type == 1){
            Log.e(TAG,"1---");
            setData(10, 100);
            leftAxis.setAxisMaxValue(100f);
            leftAxis.setAxisMinValue(0f);
        }
        else if(type == 2){
            Log.e(TAG,"2---");
            setData(10, 50);
            leftAxis.setAxisMaxValue(50f);
            leftAxis.setAxisMinValue(0f);
        }
        else if(type == 3){
            Log.e(TAG,"3---");
            setData(10, 20000);
            leftAxis.setAxisMaxValue(20000f);
            leftAxis.setAxisMinValue(0f);

        }

        //        mChart.setVisibleXRange(20);
//        mChart.setVisibleYRange(20f, AxisDependency.LEFT);
//        mChart.centerViewTo(20, 50, AxisDependency.LEFT);

        //mChart.animateX(2500, Easing.EasingOption.EaseInOutQuart);
        mChart.animateXY(3000, 3000);
//        mChart.invalidate();

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.LINE);

        // // dont forget to refresh the drawing
        // mChart.invalidate();


    }
    private void setData(int count, float range) {

        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<Entry> yVals = new ArrayList<Entry>();

        RawDataBean bean;

        int position = 0;
        int length = 0;
        while(length<10){
            bean = rawList.get(position);
            if(type == 1){
                if(bean.getHumidity()>0){
                    length++;
                }
            }
            else if(type == 2){
                if(bean.getTemp()>0){
                    length++;
                }
            }
            else if(type == 3){
                if(bean.getLight()>0){
                    length++;
                }
            }
            position++;
        }
        Log.e(TAG,"position: "+position);
        Log.e(TAG,"length: "+length);

        position--;
        int i =0;
        while (position>=0){
            Log.e(TAG,"position: "+position);
            bean = rawList.get(position);
            Log.e(TAG,"time: "+bean.getTime());
            if(type == 1){
                if(bean.getHumidity()>0){
                    xVals.add(new SimpleDateFormat("HH:mm",java.util.Locale.US)
                            .format(new Date(bean.getTime()*1000)));
                    yVals.add(new Entry(bean.getHumidity(),i));
                    i++;
                }
            }
            else if(type == 2){
                if(bean.getTemp()>0){
                    xVals.add(new SimpleDateFormat("HH:mm",java.util.Locale.US)
                            .format(new Date(bean.getTime()*1000)));
                    yVals.add(new Entry(bean.getTemp(),i));
                    i++;
                }
            }
            else if(type == 3){
                if(bean.getLight()>0){
                    xVals.add(new SimpleDateFormat("HH:mm",java.util.Locale.US)
                            .format(new Date(bean.getTime()*1000)));
                    yVals.add(new Entry(bean.getLight(),i));
                    i--;
                }
            }
            position--;
        }

        LineDataSet set1;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet)mChart.getData().getDataSetByIndex(0);
            set1.setYVals(yVals);
            mChart.getData().setXVals(xVals);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(yVals, "DataSet 1");

            // set1.setFillAlpha(110);
            // set1.setFillColor(Color.RED);

            // set the line to be drawn like this "- - - - - -"
            set1.enableDashedLine(10f, 5f, 0f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(true);

            if (Utils.getSDKInt() >= 18) {
                // fill drawable only supported on api level 18 and above
                Drawable drawable = ContextCompat.getDrawable(context, R.drawable.fade_red);
                set1.setFillDrawable(drawable);
            }
            else {
                set1.setFillColor(Color.BLACK);
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData data = new LineData(xVals, dataSets);

            // set data
            mChart.setData(data);
        }
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "START, x: " + me.getX() + ", y: " + me.getY());
    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "END, lastGesture: " + lastPerformedGesture);

        // un-highlight values after the gesture is finished and no single-tap
        if(lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP)
            mChart.highlightValues(null); // or highlightTouch(null) for callback to onNothingSelected(...)
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        Log.i("LongPress", "Chart longpressed.");
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        Log.i("DoubleTap", "Chart double-tapped.");
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        Log.i("SingleTap", "Chart single-tapped.");
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
        Log.i("Fling", "Chart flinged. VeloX: " + velocityX + ", VeloY: " + velocityY);
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        Log.i("Scale / Zoom", "ScaleX: " + scaleX + ", ScaleY: " + scaleY);
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        Log.i("Translate / Move", "dX: " + dX + ", dY: " + dY);
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        Log.i("Entry selected", e.toString());
        Log.i("LOWHIGH", "low: " + mChart.getLowestVisibleXIndex() + ", high: " + mChart.getHighestVisibleXIndex());
        Log.i("MIN MAX", "xmin: " + mChart.getXChartMin() + ", xmax: " + mChart.getXChartMax() + ", ymin: " + mChart.getYChartMin() + ", ymax: " + mChart.getYChartMax());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }
}

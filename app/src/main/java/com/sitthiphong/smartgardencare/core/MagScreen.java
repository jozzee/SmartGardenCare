package com.sitthiphong.smartgardencare.core;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by Sitthiphong on 7/8/2016 AD.
 */
public class MagScreen {
    private static final String TAG = MagScreen.class.getSimpleName();
    private static final int ORT_PORTRAIT = 0; // Portrait
    private static final int ORT_LANDSCAPE_RIGHT = 3; // Landscape right
    private static final int ORT_PORTRAIT_INVERT = 2;
    private static final int ORT_LANDSCAPE_LEFT = 1; // Landscape left
    private int widthPixels;
    private int heightPixels;
    private float density;
    private double widthDp;
    private double heightDp;
    private int screenOrientation;
    private Display display;
    private boolean isTablet;
    private int sizePieView;

    public MagScreen(Context context, DisplayMetrics metrics) {
        widthPixels = metrics.widthPixels;
        heightPixels = metrics.heightPixels;
        density = metrics.density;

        widthDp = ((double)widthPixels/(double)density);
        heightDp = ((double)heightPixels/(double)density);
        //Log.e(TAG,"widthDp: "+widthDp);
        //Log.e(TAG,"heightDp: "+heightDp);

        display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        screenOrientation = display.getRotation();

        if(widthDp<heightDp){
            if(widthDp >= 600 && heightDp >= 960){
                isTablet = true;
            }
            else{
                isTablet = false;
            }
        }
        else if(widthDp>heightDp){
            if(widthDp >= 960 && heightDp >= 600){
                isTablet = true;
            }
            else {
                isTablet = false;
            }
        }

        getSizePlainPi();

    }
    public int getWidthLineChart(){
        if(widthDp<heightDp){
            return (int)(widthDp*density);
        }
        else{
            return (int)(heightDp*density);
        }
    }
    public int getHeightLineChart(){
        if(widthDp<heightDp){
            return (int)((widthDp/(float)1.7777)*density);
        }
        else{
            return (int)(heightDp*density);
        }
    }

    public int getWidthGardenImage(){

            if(widthDp<heightDp){
                return (int)(widthDp*density);
            }
            else{
                return (isTablet)?((int)(((heightDp-292)*1.7777)*density)):((int)(((heightDp-136)*1.7777)*density));
            }
    }
    public int getHeightGardenImage(){
        if(widthDp<heightDp){
            return (int)((widthDp/(float)1.7777)*density);
        }
        else {
            return (isTablet)?((int)((heightDp-292)*density)):((int)((heightDp-136)*density));
        }
    }

    private void getSizePlainPi(){
        if(widthDp<heightDp){
            if(isTablet){
                Log.e(TAG,"isTablet");
                sizePieView = (int)(360*density);
            }
            else{
                if((widthDp-32)>200){
                    sizePieView = (int)(200*density);
                }
                else{
                    sizePieView = (int)((widthDp-32)*density);
                }
            }
        }
        else if(widthDp>heightDp){
            if(isTablet){
                Log.e(TAG,"isTablet");
                sizePieView = (int)(360*density);
            }
            else{
                if((heightDp-168)>200){
                    sizePieView = (int)(200*density);
                }
                else{
                   sizePieView = (int)((heightDp-168)*density);
                }
            }
        }

    }
//    public int getSizePlainPie(int margin){
//
//        return (widthDp<heightDp)?(int)((widthDp-(margin*2))*density):(int)((heightDp-(margin*2))*density);
//    }

    public int getWidthPixels() {
        return widthPixels;
    }

    public int getHeightPixels() {
        return heightPixels;
    }

    public float getDensity() {
        return density;
    }

    public double getWidthDp() {
        return widthDp;
    }

    public double getHeightDp() {
        return heightDp;
    }

    public int getScreenOrientation() {
        return screenOrientation;
    }

    public Display getDisplay() {
        return display;
    }

    public boolean isTablet() {
        return isTablet;
    }

    public int getSizePieView() {
        return sizePieView;
    }
}

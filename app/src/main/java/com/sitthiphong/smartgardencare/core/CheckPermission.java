package com.sitthiphong.smartgardencare.core;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.sitthiphong.smartgardencare.R;

/**
 * Created by Sitthiphong on 7/24/2016 AD.
 */
public class CheckPermission {
    private String TAG = "CheckPermission";
    private Context context;

    public CheckPermission(Context context) {
        this.context = context;
    }

    public boolean checkPermission(String permission) {
        Log.i(TAG, "check Permission");
        if (Build.VERSION.SDK_INT >= 23) {
            Log.i(TAG,"     Marshmallow++..");
            if(ContextCompat.checkSelfPermission(context,permission) != PackageManager.PERMISSION_GRANTED){
                Log.i(TAG,"     not has permission");
                return false;
            }
            else{
                Log.i(TAG,"     has permission");
                return true;
            }

        } else {
            // Pre-Marshmallow
            Log.e(TAG,"Pre-Marshmallow");
            return true;

        }
    }
    public void requestPermission(String permission){
        Log.i(TAG,"requestPermission");
        ActivityCompat.requestPermissions(
                (Activity) context,
                new String[] {permission},
                context.getResources().getInteger(R.integer.REQUEST_CODE_ASK_PERMISSIONSc));
    }
}

package com.sitthiphong.smartgardencare.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.DetectedActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jozziga.microgear.Microgear;
import com.jozziga.microgear.MicrogearEventListener;
import com.sitthiphong.smartgardencare.R;
import com.sitthiphong.smartgardencare.datamodel.ConfigData;
import com.sitthiphong.smartgardencare.datamodel.ImageBean;
import com.sitthiphong.smartgardencare.datamodel.PublishBean;
import com.sitthiphong.smartgardencare.datamodel.RawDataBean;
import com.sitthiphong.smartgardencare.datamodel.ResponseBean;
import com.sitthiphong.smartgardencare.datamodel.SubscribeBean;
import com.sitthiphong.smartgardencare.libs.MagDiscreteSeekBar;
import com.sitthiphong.smartgardencare.libs.MagScreen;
import com.sitthiphong.smartgardencare.libs.MyTextWatcher;
import com.sitthiphong.smartgardencare.libs.ShareData;
import com.sitthiphong.smartgardencare.listener.OnSaveSettingListener;
import com.sitthiphong.smartgardencare.listener.SetStandListener;
import com.sitthiphong.smartgardencare.listener.UpdateImageListener;
import com.sitthiphong.smartgardencare.listener.UpdateRawDataListener;
import com.sitthiphong.smartgardencare.service.GcmRegisterService;
import com.sitthiphong.smartgardencare.service.RestApiNetPie;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements
        UpdateRawDataListener,
        UpdateImageListener,
        OnSaveSettingListener,
        SetStandListener.OnSetStandardListener {
    private final String TAG = "MainActivity";
    private UpdateRawDataListener updateRawDataListener = null;
    //private SetStandListener.Result resultSetStandard = null;

    private boolean isReceiverRegistered;
    private CoordinatorLayout rootLayout;
    private Toolbar toolbar;
    private MicroGearCallBack callBack;
    private SwipeRefreshLayout refreshLayout;
    private NestedScrollView scrollView;
    private ImageView image;
    private RelativeLayout moistureLayout, templayout, lightLatout;
    private TextView moistureValue, tempValue, lightValue, lsatUpdateValue, slatStatus;
    private Button btnWater, btnFoggy, btnSlat, history;
    private ProgressDialog progressDialog;
    private RawDataBean rawDataBean;
    private ResponseBean responseBean;
    private TextView exception;
    private ProgressBar progressBar, progressBarImage;
    private ShareData shareData;


    private PublishBean publishBean;
    private Handler publishHandle;
    private Runnable publistask;
    private Handler checkConnectNetPieHandler;
    private Runnable checkConnectNetPieRunnable;
    private Microgear microgear = new Microgear(this);
    private String appId = "SmartGardenCare"; //APP_ID
    private String key = "L8LswNXzRY9nalS"; //KEY
    private String secret = "KcysynAOjLxZ3BkGzqSq4WvSA"; //SECRET
    private String alias = "android";
    private boolean isConnectNetPie;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String action = bundle.getString("action");
            if (action.equals("onConnect")) {
                isConnectNetPie = true;
                microgear.publish(ConfigData.token, shareData.getToken(), 1, true);
                if (shareData.getSendPrefer()) {
                    Log.i(TAG, "MicroGearCallBack, publish setting details (not save to netpie) ");
                    microgear.publish(ConfigData.preferencesTopic, shareData.getPreferencesAsObjString(), 1, true);
                    shareData.removeSendPrefer();
                }
                progressBar.setVisibility(View.GONE);
                exception.setVisibility(View.GONE);
                refreshLayout.setVisibility(View.VISIBLE);
                new SubscribeTask(getString(R.string.loadingImage)).execute(ConfigData.photoTopic);

            } else if (action.equals("onMessage")) {

                String topic = bundle.getString("topic");
                String message = bundle.getString("message");
                if (topic.equals(ConfigData.rawDataTopic)) {
                    rawDataBean = new RawDataBean(message);
                    updateRawData(rawDataBean);
                } else if (topic.equals(ConfigData.hasPhotoTopic)) {
                    new SubscribeTask(getString(R.string.loadingImage)).execute(ConfigData.photoTopic);

                } else if (topic.equals(ConfigData.slatStatusTopic)) {
                    updateSlatStatus(Integer.parseInt(message));

                } else if (topic.equals(ConfigData.responsesTopic)) {
                    responseBean = new ResponseBean(message);

                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    if (publishHandle != null) {
                        Log.e(TAG, "remove task");
                        publishHandle.removeCallbacks(publistask);
                    }

                    if ((publishBean != null) && (publishBean.getTopic() != null)) {
                        if (publishBean.getTopic().equals(responseBean.getTopic())) {
                            Log.e(TAG, "responses topic: " + responseBean.getTopic());
                            Log.e(TAG, "success: " + String.valueOf(responseBean.isSuccess()));
                            if (responseBean.isSuccess()) {
                                if (responseBean.getTopic().equals(ConfigData.refreshTopic)) {
                                    refreshLayout.setRefreshing(false);
                                } else if (responseBean.getTopic().equals(ConfigData.settingStandardTopic)) {
                                    if (DetailsActivity.resultSetStandard != null) {
                                        DetailsActivity.resultSetStandard.result(true, "");
                                    }
                                    JsonObject obj = new Gson().fromJson(responseBean.getMessage(), JsonObject.class);
                                    if (obj.get(ConfigData.mosStd) != null) {
                                        shareData.putInt(ConfigData.mosStd, obj.get(ConfigData.mosStd).getAsInt());
                                    }
                                    if (obj.get(ConfigData.tmpStd) != null) {
                                        shareData.putInt(ConfigData.tmpStd, obj.get(ConfigData.tmpStd).getAsInt());
                                    }
                                    if (obj.get(ConfigData.ligStd) != null) {
                                        shareData.putInt(ConfigData.ligStd, obj.get(ConfigData.ligStd).getAsInt());
                                    }

                                } else if (responseBean.getTopic().equals(ConfigData.setDetailsTopic)) {
                                    JsonObject obj = new Gson().fromJson(responseBean.getMessage(), JsonObject.class);
                                    saveDetails(obj);

                                    showDialog(getString(R.string.success), getString(R.string.saveSettingSuccess));

                                } else if (responseBean.getTopic().equals(ConfigData.ctrlDevicesTopic)) {
                                    JsonObject messageObj = new Gson().fromJson(responseBean.getMessage(), JsonObject.class);
                                    int working = 0;
                                    float valBefore = 0, valAfter = 0;
                                    if (messageObj.get("working") != null) {
                                        working = messageObj.get("working").getAsInt();
                                    }
                                    if (messageObj.get("valBefore") != null) {
                                        valBefore = messageObj.get("valBefore").getAsFloat();
                                    }
                                    if (messageObj.get("valAfter") != null) {
                                        valAfter = messageObj.get("valAfter").getAsFloat();
                                    }

                                    StringBuilder builder = new StringBuilder();

                                    if (working == 1) {
                                        builder.append(getString(R.string.before) + " ");
                                        builder.append(String.valueOf(valBefore) + " %\n");
                                        builder.append(getString(R.string.after) + " ");
                                        builder.append(String.valueOf(valAfter) + " %");
                                        alertDialog(getString(R.string.waterSuccess), builder.toString());
                                    } else if (working == 2) {
                                        builder.append(getString(R.string.before) + " ");
                                        builder.append(String.valueOf(valBefore) + " °C\n");
                                        builder.append(getString(R.string.after) + " ");
                                        builder.append(String.valueOf(valAfter) + " °C");
                                        alertDialog(getString(R.string.foggySuccess), builder.toString());
                                    } else if (working == 3) {
                                        builder.append(getString(R.string.before) + " ");
                                        builder.append(String.valueOf(valBefore) + " Lux\n");
                                        builder.append(getString(R.string.after) + " ");
                                        builder.append(String.valueOf(valAfter) + " Lux");
                                        alertDialog(getString(R.string.openSlatSuccess), builder.toString());
                                    } else if (working == 4) {
                                        builder.append(getString(R.string.before) + " ");
                                        builder.append(String.valueOf(valBefore) + " Lux\n");
                                        builder.append(getString(R.string.after) + " ");
                                        builder.append(String.valueOf(valAfter) + " Lux");
                                        alertDialog(getString(R.string.closeSlatSuccess), builder.toString());
                                    }
                                }

                            } else {
                                //for error control device
                                if (responseBean.getTopic().equals(ConfigData.ctrlDevicesTopic)) {
                                    JsonObject messageObj = new Gson().fromJson(responseBean.getMessage(), JsonObject.class);
                                    int working = 0, errorCode = 0;
                                    if (messageObj.get("working") != null) {
                                        working = messageObj.get("working").getAsInt();
                                    }
                                    if (messageObj.get("errorCode") != null) {
                                        errorCode = messageObj.get("errorCode").getAsInt();
                                    }

                                    if (working == 1) {
                                        if (errorCode == 0) {
                                            if ((messageObj.get("valBefore") != null) && (messageObj.get("valAfter") != null)) {
                                                StringBuilder builder = new StringBuilder();
                                                builder.append(getString(R.string.before) + " ");
                                                builder.append(String.valueOf(messageObj.get("valBefore").getAsFloat()) + " %\n");
                                                builder.append(getString(R.string.after) + " ");
                                                builder.append(String.valueOf(messageObj.get("valAfter").getAsFloat()) + " %\n");
                                                builder.append(getString(R.string.but) + " ");
                                                builder.append(getString(R.string.canNotSaveData));
                                                alertDialog(getString(R.string.waterSuccess), builder.toString());

                                            } else {
                                                alertDialog(getString(R.string.exception), getString(R.string.canNotSaveData));
                                            }
                                        } else if (errorCode == 1) {
                                            //showDialog(getString(R.string.exception), getString(R.string.waterFalse));
                                            alertDialog(getString(R.string.exception), getString(R.string.waterFalse));
                                        } else if (errorCode == 2) {
                                            StringBuilder builder = new StringBuilder();
                                            builder.append(getString(R.string.waterSuccess) + " " + getString(R.string.but) + "\n");
                                            builder.append(getString(R.string.moistureNotRising));
                                            alertDialog(getString(R.string.exception), builder.toString());
                                        }


                                    } else if (working == 2) {
                                        if (errorCode == 0) {
                                            if ((messageObj.get("valBefore") != null) && (messageObj.get("valAfter") != null)) {
                                                StringBuilder builder = new StringBuilder();
                                                builder.append(getString(R.string.before) + " ");
                                                builder.append(String.valueOf(messageObj.get("valBefore").getAsFloat()) + " °C\n");
                                                builder.append(getString(R.string.after) + " ");
                                                builder.append(String.valueOf(messageObj.get("valAfter").getAsFloat()) + " °C\n");
                                                builder.append(getString(R.string.but) + " ");
                                                builder.append(getString(R.string.canNotSaveData));
                                                alertDialog(getString(R.string.foggySuccess), builder.toString());

                                            } else {
                                                alertDialog(getString(R.string.exception), getString(R.string.canNotSaveData));
                                            }
                                        } else if (errorCode == 1) {
                                            alertDialog(getString(R.string.exception), getString(R.string.waterFalse));
                                        } else if (errorCode == 3) {
                                            StringBuilder builder = new StringBuilder();
                                            builder.append(getString(R.string.foggySuccess) + " " + getString(R.string.but) + "\n");
                                            builder.append(getString(R.string.tempNotDrop));
                                            alertDialog(getString(R.string.exception), builder.toString());
                                        } else if (errorCode == 4) {
                                            StringBuilder builder = new StringBuilder();
                                            builder.append(getString(R.string.canNotFoggy) + " " + getString(R.string.until) + "\n");
                                            builder.append(getDateTime(messageObj.get("msg").getAsLong()));
                                            alertDialog(getString(R.string.exception), builder.toString());
                                        }

                                    } else if (working == 3) {
                                        if (errorCode == 0) {
                                            if ((messageObj.get("valBefore") != null) && (messageObj.get("valAfter") != null)) {
                                                StringBuilder builder = new StringBuilder();
                                                builder.append(getString(R.string.before) + " ");
                                                builder.append(String.valueOf(messageObj.get("valBefore").getAsFloat()) + " Lux\n");
                                                builder.append(getString(R.string.after) + " ");
                                                builder.append(String.valueOf(messageObj.get("valAfter").getAsFloat()) + " Lux\n");
                                                builder.append(getString(R.string.but) + " ");
                                                builder.append(getString(R.string.canNotSaveData));
                                                alertDialog(getString(R.string.openSlatSuccess), builder.toString());

                                            } else {
                                                alertDialog(getString(R.string.exception), getString(R.string.canNotSaveData));
                                            }
                                        } else if (errorCode == 5) {
                                            alertDialog(getString(R.string.slatOpen), "");
                                        }


                                    } else if (working == 4) {
                                        if (errorCode == 0) {
                                            if ((messageObj.get("valBefore") != null) && (messageObj.get("valAfter") != null)) {
                                                StringBuilder builder = new StringBuilder();
                                                builder.append(getString(R.string.before) + " ");
                                                builder.append(String.valueOf(messageObj.get("valBefore").getAsFloat()) + " Lux\n");
                                                builder.append(getString(R.string.after) + " ");
                                                builder.append(String.valueOf(messageObj.get("valAfter").getAsFloat()) + " Lux\n");
                                                builder.append(getString(R.string.but) + " ");
                                                builder.append(getString(R.string.canNotSaveData));
                                                alertDialog(getString(R.string.closeSlatSuccess), builder.toString());

                                            } else {
                                                alertDialog(getString(R.string.exception), getString(R.string.canNotSaveData));
                                            }
                                        } else if (errorCode == 6) {
                                            alertDialog(getString(R.string.slatClose), "");
                                        } else if (errorCode == 7) {
                                            StringBuilder builder = new StringBuilder();
                                            builder.append(getString(R.string.closeSlatSuccess) + " " + getString(R.string.but) + "\n");
                                            builder.append(getString(R.string.lightNotDrops));
                                            alertDialog(getString(R.string.exception), builder.toString());
                                        }

                                    }

                                }
                            }
                        }

                    }

                }

            } else if (action.equals("onPresent")) {
                String token = bundle.getString("token");
                Log.e(TAG, "onPresent: token = " + token);

            } else if (action.equals("onAbsent")) {
                String token = bundle.getString("token");
                Log.e(TAG, "onAbsent: token = " + token);

            } else if (action.equals("onDisconnect")) {
                isConnectNetPie = false;
                notificationSnackBar(rootLayout, getString(R.string.notConnectNetPie));

            } else if (action.equals("onError")) {
                String error = bundle.getString("error");

                if (error.equals("connection Lost") || error.equals("No Internet connection")) {
                    isConnectNetPie = false;
                    notificationSnackBar(rootLayout, getString(R.string.notConnectNetPie));
                } else {
                    setContentView(R.layout.activity_main);
                    init();
                    setException(error);
                }

            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setSplashScreen();

        shareData = new ShareData(this);
        shareData.createSharePreference();

        registerReceiver();
        if (checkPlayServices()) {
            registerGcm();
        }

        if (!checkNetPie()) {
            Log.e(TAG, "is first open application");
            setLoginScreen();
        } else {
            setContentView(R.layout.activity_main);
            init();

            if (isConnectInternet(getContext())) {
                connectNetPie();
            } else {
                setException(getString(R.string.noInternet));
            }

        }

    }

    @Override
    protected void onStart() {
        Log.i(TAG, "onStart");
        super.onStart();

    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
        registerReceiver();
        if (microgear != null) {
            microgear.bindServiceResume();
        }


    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();
        unregisterReceiver();

    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop");
        super.onStop();

    }

    @Override
    protected void onRestart() {
        Log.i(TAG, "onRestart");
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
        if (microgear != null) {
            microgear.disconnect();
        }

    }

    @Override
    public void onBackPressed() {
        Log.i(TAG, "onBackPressed");
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.i(TAG, "onOptionsItemSelected: id " + id);

        if (id == 16908332) {
            onBackPressed();
            return true;
        } else if (id == R.id.actionSetting) {
            SettingActivity.onSaveSettingListener = getSettingListener();
            startActivity(new Intent(getContext(), SettingActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        //mBottomBar.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i(TAG, "onRestoreInstanceState");
    }

    /*private void setMainScreen() {
        Log.e(TAG, "setMainScreen");

        if (isConnectNetPie) {

        } else {

            setException(getString(R.string.noNetPieData));
        }

        if (checkNetPie()) {
            if (isConnectInternet(this)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        exception.setVisibility(View.GONE);
                        refreshLayout.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);
                    }
                });
                connectNetPie();
            } else {

                setException(getString(R.string.noInternet));
            }

        } else {

        }
    }*/

    private synchronized void setSplashScreen() {
        Log.e(TAG, "setSplashScreen");
        setContentView(R.layout.splash_screen);
    }

    private void init() {
        Log.i(TAG, "init");
        rootLayout = (CoordinatorLayout) findViewById(R.id.root_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name));

        DisplayMetrics metrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        MagScreen magScreen = new MagScreen(this, metrics);


        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        refreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorAccent));
        scrollView = (NestedScrollView) findViewById(R.id.scrollView);

        image = (ImageView) findViewById(R.id.image_garden);
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
                magScreen.getWidthGardenImage(), magScreen.getHeightGardenImage());
        image.setLayoutParams(param);

        moistureLayout = (RelativeLayout) findViewById(R.id.moisture_layout);
        templayout = (RelativeLayout) findViewById(R.id.temp_layout);
        lightLatout = (RelativeLayout) findViewById(R.id.light_layout);

        moistureValue = (TextView) findViewById(R.id.moisture_value);
        tempValue = (TextView) findViewById(R.id.temp_value);
        lightValue = (TextView) findViewById(R.id.light_value);
        lsatUpdateValue = (TextView) findViewById(R.id.time_value);
        history = (Button) findViewById(R.id.history);
        slatStatus = (TextView) findViewById(R.id.slat_status_value);

        btnWater = (Button) findViewById(R.id.btn_water);
        btnFoggy = (Button) findViewById(R.id.btn_foggy);
        btnSlat = (Button) findViewById(R.id.btn_slat);


        exception = (TextView) findViewById(R.id.exception);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        progressBarImage = (ProgressBar) findViewById(R.id.progress_image);

        exception.setVisibility(View.GONE);
        refreshLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);


        moistureLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rawDataBean != null) {
                    //Log.e(TAG,"66666: "+String.valueOf(rawDataBean.getMoistureBean().getAverage()));

                    startDetailsActivity("moisture", new Gson().toJson(rawDataBean.getMoistureBean()));
                }
            }
        });

        templayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rawDataBean != null) {
                    startDetailsActivity("temp", new Gson().toJson(rawDataBean.getTempBean()));
                }
            }
        });

        lightLatout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rawDataBean != null) {
                    startDetailsActivity("light", new Gson().toJson(rawDataBean.getLightBean()));
                }
            }
        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isConnectNetPie) {
                    //refreshLayout.setRefreshing(false);
                    publish(ConfigData.refreshTopic, "1", "null");
                } else {
                    notificationSnackBar(rootLayout, getString(R.string.notConnectNetPie));
                }

            }
        });
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), HistoryActivity.class));
            }
        });

        btnWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlsDevices("1", getString(R.string.onWater));
            }
        });
        btnFoggy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlsDevices("2", getString(R.string.onFoggy));
            }
        });
        btnSlat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnSlat.getText().toString().trim().equals(getString(R.string.openSlat))) {
                    controlsDevices("3", getString(R.string.onOpenSlat));

                } else if (btnSlat.getText().toString().trim().equals(getString(R.string.closeSlat))) {

                    controlsDevices("4", getString(R.string.onOpenSlat));
                }
            }
        });

    }

    private void controlsDevices(String payload, String messageDialog) {
        if (isConnectInternet(getContext())) {
            if (isConnectNetPie) {
                publish(ConfigData.ctrlDevicesTopic, payload, messageDialog);

            } else {
                notificationSnackBar(rootLayout, getString(R.string.notConnectNetPie));
            }

        } else {
            notificationSnackBar(rootLayout, getString(R.string.noInternet));
        }
    }

    private void connectNetPie() {
        Log.i(TAG, "connectNetPie");

        try {
            callBack = new MicroGearCallBack();
            microgear.setCallback(callBack);
            microgear.connect(
                    shareData.getAppId(),
                    shareData.getAppKey(),
                    shareData.getAppSecret(),
                    alias);
            //microgear.subscribe(ConfigData.preferencesTopic);
            microgear.subscribe(ConfigData.rawDataTopic);
            microgear.subscribe(ConfigData.hasPhotoTopic);
            microgear.subscribe(ConfigData.slatStatusTopic);
            microgear.subscribe(ConfigData.responsesTopic);
           /* checkConnectNetPieHandler = new Handler();
            checkConnectNetPieRunnable = new Runnable() {
                @Override
                public void run() {
                    setMainScreen();
                }
            };
            checkConnectNetPieHandler.postDelayed(checkConnectNetPieRunnable, 10000);*/

        } catch (NullPointerException e) {
            e.printStackTrace();
            callBack.onError("Error on connect NETPIE");
            Log.e(TAG, "Error on connect NETPIE 7777777777");

        }

    }

   /* private void setExceptionScreen(final String error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.except_screen);
                TextView except = (TextView) findViewById(R.id.exception);
                except.setText(error);
            }
        });


    }*/

    private void setException(String error) {
        refreshLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        exception.setText(error);
        exception.setVisibility(View.VISIBLE);
        if (error.equals(getString(R.string.noInternet))) {
            exception.setText(error + "\n" + getString(R.string.tryAgain));
            exception.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }

    private void startDetailsActivity(String sensor, String data) {

        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra("sensor", sensor);
        intent.putExtra("data", data);
        startActivity(intent);
        MagDiscreteSeekBar.setOnSetStandardListener(this);

    }


    private Context getContext() {
        return this;
    }

    @Override
    public void updateImageListener() {

    }

    @Override
    public void updateRawDataListener(RawDataBean rawDataBean) {

    }

    @Override
    public void onSaveSettingListener(JsonObject objNetPie, JsonObject objDetails) {
        Log.i(TAG, "onSaveSettingListener");
        if ((objNetPie.size() > 0) && (objDetails.size() > 0)) {
            objDetails.addProperty("lastUpdate", String.valueOf(System.currentTimeMillis() / 1000));
            saveNetPie(objNetPie);
            saveDetails(objDetails);
            shareData.putSendPrefer();
            reStartActivity();
        } else if (objNetPie.size() > 0) {
            saveNetPie(objNetPie);
            reStartActivity();
        } else if (objDetails.size() > 0) {
            if (objDetails.get(ConfigData.fqPData) == null) {
                objDetails.addProperty(ConfigData.fqPData, shareData.getFqPData());
            }
            if (objDetails.get(ConfigData.fqPImage) == null) {
                objDetails.addProperty(ConfigData.fqPImage, shareData.getFqPImage());
            }
            if (objDetails.get(ConfigData.fqIData) == null) {
                objDetails.addProperty(ConfigData.fqIData, shareData.getFqIData());
            }
            if (objDetails.get(ConfigData.fqShower) == null) {
                objDetails.addProperty(ConfigData.fqShower, shareData.getFqShower());
            }
            if (objDetails.get(ConfigData.ageData) == null) {
                objDetails.addProperty(ConfigData.ageData, shareData.getAgeData());
            }
            if (objDetails.get(ConfigData.autoMode) == null) {
                objDetails.addProperty(ConfigData.autoMode, shareData.isAutoMode());
            }
            objDetails.addProperty("lastUpdate", String.valueOf(System.currentTimeMillis() / 1000));

            publish(ConfigData.setDetailsTopic,
                    new Gson().toJson(objDetails),
                    getResources().getString(R.string.onSaveSetting));
        }
    }


    private void onSubscribeCallBack(SubscribeBean result) {
        if (result != null) {
            try {
                Log.e(TAG, "onSubscribeCallBack topic: " + result.getTopic());
                if (result.getTopic().equals(ConfigData.photoTopic)) {
                    ImageBean imageBean = new ImageBean(result.getPayload());
                    image.setImageBitmap(imageBean.getBitmap());
                    progressBarImage.setVisibility(View.GONE);
                }

            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateRawData(final RawDataBean bean) {
        Log.i(TAG, "updateRawData");
        rawDataBean = bean;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (rawDataBean != null) {
                    try {
                        if (rawDataBean.getMoistureBean().getAverage() > 0) {
                            moistureValue.setText(String.valueOf(rawDataBean.getMoistureBean().getAverage()) + " %");
                        } else {
                            moistureValue.setText(getString(R.string.sensorError));
                        }
                        if (rawDataBean.getTempBean().getAverage() > 0) {
                            tempValue.setText(String.valueOf(rawDataBean.getTempBean().getAverage()) + " °C");
                        } else {
                            tempValue.setText(getString(R.string.sensorError));
                        }
                        if (rawDataBean.getLightBean().getLightIn() > 0) {
                            lightValue.setText(String.valueOf(rawDataBean.getLightBean().getLightIn()) + " Lux");
                        } else {
                            lightValue.setText(getString(R.string.sensorError));
                        }
                        lsatUpdateValue.setText(getDateTime((rawDataBean.getTime() * 1000)));

                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


    }

    private String getDateTime(long time) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
            Date date = (new Date(time));
            return dateFormat.format(date);

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    private void updateSlatStatus(int status) {//0 is close , 1 is open
        Log.i(TAG, "updateSlatStatus: " + status);

        if (status == 0) {
            slatStatus.setText(getString(R.string.slatClose));
            btnSlat.setText(getString(R.string.openSlat));
        } else if (status == 1) {
            slatStatus.setText(getString(R.string.slatOpen));
            btnSlat.setText(getString(R.string.closeSlat));
        }


    }


    public void reStartActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void notificationSnackBar(View v, String message) {
        Snackbar.make(v, message, Snackbar.LENGTH_LONG).show();
    }

    private BroadcastReceiver mRegistrationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            boolean sentToken = sharedPreferences.getBoolean(GcmRegisterService.SENT_TOKEN_TO_SERVER, false);
            // TODO Do something here
        }
    };

    private void registerReceiver() {
        Log.e(TAG, "registerReceiver");
        if (!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(GcmRegisterService.REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }

    private void unregisterReceiver() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
    }

    private void registerGcm() {
        Intent intent = new Intent(this, GcmRegisterService.class);
        startService(intent);
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, ConfigData.PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            return false;
        }
        return true;
    }

    private OnSaveSettingListener getSettingListener() {
        return this;
    }

    private boolean checkNetPie() {
        Log.i(TAG, "checkNetPie");
        if (shareData.getAppId().equals("") ||
                shareData.getAppKey().equals("") ||
                shareData.getAppSecret().equals("")) {
            Log.e(TAG, "no Netpie data");
            return false;
        } else {
            Log.i(TAG, "application id: " + (shareData.getAppId()));
            Log.i(TAG, "key: " + (shareData.getAppKey()));
            Log.i(TAG, "secret: " + (shareData.getAppSecret()));
            return true;
        }
    }

    private void setLoginScreen() {
        Log.e(TAG, "setLoginScreen");
        setContentView(R.layout.layout_login);
        final Button cancel, login;
        final TextInputLayout appIdLayout, keyLayout, secretLayout;
        final EditText appId, key, secret;

        appIdLayout = (TextInputLayout) findViewById(R.id.TextInputLayoutAppID);
        keyLayout = (TextInputLayout) findViewById(R.id.TextInputLayoutAppKey);
        secretLayout = (TextInputLayout) findViewById(R.id.TextInputLayoutAppSecret);
        appId = (EditText) findViewById(R.id.editTextAppID);
        key = (EditText) findViewById(R.id.editTextAppKey);
        secret = (EditText) findViewById(R.id.editTextAppSecret);
        cancel = (Button) findViewById(R.id.btnCancel);
        login = (Button) findViewById(R.id.btnLogin);


        appId.addTextChangedListener(new MyTextWatcher(appIdLayout));
        key.addTextChangedListener(new MyTextWatcher(keyLayout));
        secret.addTextChangedListener(new MyTextWatcher(secretLayout));

        appId.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if (appId.getText().toString().trim().isEmpty()) {
                        appIdLayout.setError(getString(R.string.enterKey) + " " + getString(R.string.appId));
                    }
                }
                return false;
            }
        });

        key.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if (key.getText().toString().trim().isEmpty()) {
                        keyLayout.setError(getString(R.string.enterKey) + " " + getString(R.string.appKey));
                    }
                }
                return false;
            }
        });
        secret.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    login.callOnClick();
                }
                return false;
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean re = true;
                if (secret.getText().toString().trim().isEmpty()) {
                    secretLayout.setError(getString(R.string.enterKey) + " " + getString(R.string.appSecret));
                    re = false;
                }
                if (appId.getText().toString().trim().isEmpty()) {
                    appIdLayout.setError(getString(R.string.enterKey) + " " + getString(R.string.appId));
                    re = false;
                }
                if (key.getText().toString().trim().isEmpty()) {
                    keyLayout.setError(getString(R.string.enterKey) + " " + getString(R.string.appKey));
                    re = false;
                }
                if (re) {
                    shareData.putAppId(appId.getText().toString().trim());
                    shareData.putKey(key.getText().toString().trim());
                    shareData.putSecret(secret.getText().toString().trim());
                    setSplashScreen();
                    connectNetPie();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(getContext())
                        .title(getString(R.string.warning))
                        .content(getString(R.string.warningExitSetNetPie))
                        .positiveText(R.string.ok)
                        .negativeText(R.string.cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                setContentView(R.layout.activity_main);
                                init();
                                setException(getString(R.string.noNetPieData));
                                dialog.dismiss();
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .show();

            }
        });
    }

    private void saveNetPie(JsonObject objNetPie) {
        if (objNetPie.get(ConfigData.appId) != null) {
            shareData.putAppId(objNetPie.get(ConfigData.appId).getAsString());
        }
        if (objNetPie.get(ConfigData.key) != null) {
            shareData.putKey(objNetPie.get(ConfigData.key).getAsString());
        }
        if (objNetPie.get(ConfigData.secret) != null) {
            shareData.putSecret(objNetPie.get(ConfigData.secret).getAsString());
        }
    }

    private void saveDetails(JsonObject objDetails) {
        if (objDetails.get(ConfigData.fqPData) != null) {
            shareData.putFqPData(objDetails.get(ConfigData.fqPData).getAsInt());
        }
        if (objDetails.get(ConfigData.fqPImage) != null) {
            shareData.putFqPImage(objDetails.get(ConfigData.fqPImage).getAsInt());
        }
        if (objDetails.get(ConfigData.fqIData) != null) {
            shareData.putFqIData(objDetails.get(ConfigData.fqIData).getAsInt());
        }
        if (objDetails.get(ConfigData.fqShower) != null) {
            shareData.putFqShower(objDetails.get(ConfigData.fqShower).getAsInt());
        }
        if (objDetails.get(ConfigData.ageData) != null) {
            shareData.putAgeData(objDetails.get(ConfigData.ageData).getAsInt());
        }
        if (objDetails.get(ConfigData.autoMode) != null) {
            shareData.putAutoMode(objDetails.get(ConfigData.autoMode).getAsBoolean());

        }
    }


    public boolean isConnectInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected()) ? true : false;
    }

    @Override
    public void onSetStandardListener(String sensor, int val) {
        JsonObject object = new JsonObject();
        object.addProperty(sensor, val);
        Log.e(TAG, "payload: " + object.toString());
        publishStandard(object.toString());
    }


    public class MicroGearCallBack implements MicrogearEventListener {
        private final String TAG = "MicroGearCallBack";
        private RawDataBean rawDataBean;

        @Override
        public void onConnect() {
            Log.i(TAG, "MicroGearCallBack, onConnect");
            Message msg = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("action", "onConnect");
            msg.setData(bundle);
            handler.sendMessage(msg);
        }

        @Override
        public void onMessage(String topic, String message) {

            String[] subTopic = topic.split("/");
            if (subTopic.length == 3) {
                topic = subTopic[2];
            }
            subTopic = null;

            Log.i(TAG, "MicroGearCallBack, onMessage");
            Log.i(TAG, " topic: " + topic);
            Log.i(TAG, " message: " + message);


            Message msg = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("action", "onMessage");
            bundle.putString("topic", topic);
            bundle.putString("message", message);
            msg.setData(bundle);
            handler.sendMessage(msg);


        }

        @Override
        public void onPresent(String token) {
            Log.i(TAG, "MicroGearCallBack, onPresent");
            Message msg = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("action", "onPresent");
            bundle.putString("token", token);//onPresent,onAbsent
            msg.setData(bundle);
            handler.sendMessage(msg);

        }

        @Override
        public void onAbsent(String token) {
            Log.i(TAG, "MicroGearCallBack, onAbsent");
            Message msg = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("action", "onAbsent");
            bundle.putString("token", token);//onPresent,onAbsent
            msg.setData(bundle);
            handler.sendMessage(msg);

        }

        @Override
        public void onDisconnect() {
            Log.i(TAG, "MicroGearCallBack, onDisconnect");
            Message msg = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("action", "onDisconnect");
            msg.setData(bundle);
            handler.sendMessage(msg);
        }

        @Override
        public void onError(final String error) {
            Log.i(TAG, "MicroGearCallBack, onError: " + error);
            Message msg = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("action", "onError");
            bundle.putString("error", error);
            msg.setData(bundle);
            handler.sendMessage(msg);
        }

    }

    public void alertDialog(String title, String message) {
        new MaterialDialog.Builder(getContext())
                .title(title)
                .content(message)
                .positiveText(getResources().getString(R.string.ok))
                .show();
    }

    private void showProgressDialog(String messageDialog) {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(messageDialog);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public void showDialog(String title, String message) {
        new MaterialDialog.Builder(getContext())
                .title(title)
                .content(message)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public void publishStandard(String payload) {

        if (isConnectInternet(getContext())) {
            if (isConnectNetPie) {
                publishBean = new PublishBean(ConfigData.settingStandardTopic, payload);
                Log.e(TAG, "publishStandard");
                microgear.publish(ConfigData.settingStandardTopic, payload);

            } else {
                if (DetailsActivity.resultSetStandard != null) {
                    DetailsActivity.resultSetStandard.result(false, getString(R.string.notConnectNetPie));
                }
            }

        } else {
            if (DetailsActivity.resultSetStandard != null) {
                DetailsActivity.resultSetStandard.result(false, getString(R.string.noInternet));
            }

        }
    }

    public void publish(final String topic, String payload, String messageDialog) {
        Log.e(TAG, "publish");
        Log.e(TAG, "  topic: " + topic);
        Log.e(TAG, "  payload: " + payload);

        if (isConnectInternet(getContext())) {

            if (!messageDialog.equals("null")) {
                showProgressDialog(messageDialog);
            }

            publishBean = new PublishBean(topic, payload);


            if (topic.equals(ConfigData.ctrlDevicesTopic) || (topic.equals(ConfigData.refreshTopic))) {
                Log.e(TAG, "normal publish");
                if (isConnectNetPie) {
                    microgear.publish(topic, payload);
                } else {
                    notificationSnackBar(rootLayout, getString(R.string.notConnectNetPie));
                }
            } else {
                /*if (isConnectNetPie) {
                    Log.e(TAG, "rest api publish");
                    new PublishTask().execute(topic, payload);
                } else {
                    notificationSnackBar(rootLayout, getString(R.string.notConnectNetPie));
                }*/
                if (isConnectNetPie) {
                    microgear.publish(topic, payload, 1, true);
                } else {
                    notificationSnackBar(rootLayout, getString(R.string.notConnectNetPie));
                }
            }

            publishHandle = new Handler();
            publistask = new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "run task");
                    hideProgressDialog();
                    if (topic.equals(ConfigData.refreshTopic)) {
                        refreshLayout.setRefreshing(false);
                    }
                    showDialog(getString(R.string.exception), getString(R.string.piNotResponse));
                }
            };
            publishHandle.postDelayed(publistask, 25000); //20 second
        } else {
            notificationSnackBar(rootLayout, getString(R.string.noInternet));
        }
    }

    private void checkErrorConnectNetPie() {
        //Please Check your App id,Key,Secret

        checkConnectNetPieHandler = new Handler();
        checkConnectNetPieRunnable = new Runnable() {
            @Override
            public void run() {
                setException(getString(R.string.incorrectNetPie));
            }
        };
        checkConnectNetPieHandler.postDelayed(checkConnectNetPieRunnable, getResources().getInteger(R.integer.waitPublish));

    }


    public class PublishTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "publish topic: " + strings[0]);
            return new RestApiNetPie(
                    shareData.getAppId(),
                    shareData.getAppKey(),
                    shareData.getAppSecret())
                    .publish(strings[0], strings[1], true);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("ok")) {
                Log.d(TAG, "publish success");
            }
        }
    }

    private class SubscribeTask extends AsyncTask<String, Void, SubscribeBean> {
        private final String TAG = "SubscribeTask";
        private String message;


        public SubscribeTask(String message) {
            this.message = message;
        }

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
            getSupportActionBar().setTitle(message);
            if (message.equals(getString(R.string.loadingImage))) {
                progressBarImage.setVisibility(View.VISIBLE);
            }

        }

        @Override
        protected SubscribeBean doInBackground(String... params) {
            return new RestApiNetPie(
                    shareData.getAppId(),
                    shareData.getAppKey(),
                    shareData.getAppSecret())
                    .subscribe(params[0]);
        }

        @Override
        protected void onPostExecute(SubscribeBean result) {
            Log.i(TAG, "onPostExecute");
            //Log.e(TAG, "result: " + result);
            getSupportActionBar().setTitle(getString(R.string.app_name));
            if (message.equals(getString(R.string.loadingImage)) && progressBarImage != null) {
                progressBarImage.setVisibility(View.GONE);
            }
            onSubscribeCallBack(result);
        }
    }

}

package com.sitthiphong.smartgardencare.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sitthiphong.smartgardencare.BuildConfig;
import com.sitthiphong.smartgardencare.R;
import com.sitthiphong.smartgardencare.datamodel.ConfigData;
import com.sitthiphong.smartgardencare.datamodel.ImageBean;
import com.sitthiphong.smartgardencare.datamodel.PublishBean;
import com.sitthiphong.smartgardencare.datamodel.RawDataBean;
import com.sitthiphong.smartgardencare.datamodel.ResponseBean;
import com.sitthiphong.smartgardencare.datamodel.SubscribeBean;
import com.sitthiphong.smartgardencare.libs.BlurImage;
import com.sitthiphong.smartgardencare.libs.LocaleHelper;
import com.sitthiphong.smartgardencare.libs.MagDiscreteSeekBar;
import com.sitthiphong.smartgardencare.libs.MagScreen;
import com.sitthiphong.smartgardencare.libs.MyTextWatcher;
import com.sitthiphong.smartgardencare.libs.ShareData;
import com.sitthiphong.smartgardencare.listener.ClockListener;
import com.sitthiphong.smartgardencare.listener.OnSaveSettingListener;
import com.sitthiphong.smartgardencare.listener.SetStandListener;
import com.sitthiphong.smartgardencare.service.GcmRegisterService;
import com.sitthiphong.smartgardencare.service.RestApiNetPie;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.netpie.microgear.Microgear;
import io.netpie.microgear.MicrogearEventListener;


public class MainActivity extends AppCompatActivity implements
        OnSaveSettingListener,
        SetStandListener.OnSetStandardListener,
        ClockListener.OnSaveClockListener {

    private final String TAG = "MainActivity";
    private CoordinatorLayout rootLayout;
    private Toolbar toolbar;
    private SwipeRefreshLayout refreshLayout;
    private ImageView image, btnLoadIm;
    private RelativeLayout moistureLayout, tempLayout, lightLayout;
    private TextView moistureValue, tempValue, lightValue, lsatUpdateValue, slatStatus, exception, tvSlat;
    private ImageButton btnWater, btnFoggy, btnSlat, btnHistory, btnSetClock, btnSetting;

    private ProgressDialog progressDialog;
    private ProgressBar progressBar, progressBarImage;
    private Bitmap bitmap;
    private Menu menu;

    private ShareData shareData;
    private RawDataBean rawDataBean;
    private ResponseBean responseBean;
    private PublishBean publishBean;

    private Handler publishHandle;
    private Runnable publishTask;

    private MicroGearCallBack callBack;
    private Microgear microgear;//= new Microgear(this);

    private boolean isReceiverRegistered;
    private boolean isConnectNetPie;

    // private ClockListener.OnFinishSaveClockListener onFinishSaveClockListener = null;


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
                        publishHandle.removeCallbacks(publishTask);
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
                                } else if (responseBean.getTopic().equals(ConfigData.alarmTopic)) {
                                    shareData.setAlarmClockList(responseBean.getMessage());
                                    if (SetClockActivity.onFinishSaveClockListener != null) {
                                        SetClockActivity.onFinishSaveClockListener.onFinishSaveClockListener(1);
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
                                        } else if (errorCode == 8) {
                                            StringBuilder builder = new StringBuilder();
                                            builder.append(getString(R.string.but) + " " + getString(R.string.increasedHumidityNotSoMuch) + "\n");
                                            builder.append(getString(R.string.before) + " ");
                                            builder.append(String.valueOf(messageObj.get("valBefore").getAsFloat()) + " %\n");
                                            builder.append(getString(R.string.after) + " ");
                                            builder.append(String.valueOf(messageObj.get("valAfter").getAsFloat()) + " %\n");
                                            alertDialog(getString(R.string.waterSuccess), builder.toString());
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
                                            builder.append(getDateTime(messageObj.get("msg").getAsLong() * 1000));
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
                JsonObject tokenObj = new Gson().fromJson(bundle.getString("token"), JsonObject.class);
                String alias = tokenObj.get("alias").getAsString();
                if (alias.equals("Raaspbery Pi - Python")) {
                    alertDialog(getString(R.string.raspberryPi), getString(R.string.onLine));
                } else if (!alias.equals("android")) {
                    alertDialog(alias, getString(R.string.onLine));
                }

            } else if (action.equals("onAbsent")) {
                String token = bundle.getString("token");
                Log.e(TAG, "onAbsent: token = " + token);
                JsonObject tokenObj = new Gson().fromJson(bundle.getString("token"), JsonObject.class);
                String alias = tokenObj.get("alias").getAsString();
                if (alias.equals("Raaspbery Pi - Python")) {
                    alertDialog(getString(R.string.raspberryPi), getString(R.string.offLine));
                } else if (!alias.equals("android")) {
                    alertDialog(alias, getString(R.string.offLine));
                }

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

        shareData = new ShareData(this);
        shareData.createSharePreference();

        LocaleHelper.setLanguage(this, shareData.getLang());

        setSplashScreen();

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
        if (bitmap != null) {
            bitmap.recycle();
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
        this.menu = menu;
        MenuItem chLang = menu.findItem(R.id.actionChangeLang);
        if (shareData.getLang().equals(ConfigData.TH_LANG)) {
            chLang.setTitle(getString(R.string.engLang));
        } else {
            chLang.setTitle(getString(R.string.thaiLang));
        }
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
            SettingActivity.onSaveSettingListener = getSaveSettingListener();
            startActivity(new Intent(getContext(), SettingActivity.class));
        } else if (id == R.id.actionChangeLang) {
            if (shareData.getLang().equals(ConfigData.TH_LANG)) {
                shareData.setLang(ConfigData.EN_LANG);
                LocaleHelper.setLanguage(getContext(), shareData.getLang());
                reStartActivity();
                //recreate();
            } else {
                shareData.setLang(ConfigData.TH_LANG);
                LocaleHelper.setLanguage(getContext(), shareData.getLang());
                reStartActivity();
                //recreate();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i(TAG, "onRestoreInstanceState");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // refresh your views here
        super.onConfigurationChanged(newConfig);
    }

    private synchronized void setSplashScreen() {
        Log.e(TAG, "setSplashScreen");
        setContentView(R.layout.splash_screen);
    }

    private void init() {
        Log.i(TAG, "init");
        rootLayout = findViewById(R.id.root_layout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name));

        DisplayMetrics metrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        MagScreen magScreen = new MagScreen(this, metrics);


        refreshLayout = findViewById(R.id.refresh_layout);
        refreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorAccent));

        image = findViewById(R.id.image_garden);
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
                magScreen.getWidthGardenImage(), magScreen.getHeightGardenImage());
        image.setLayoutParams(param);
        btnLoadIm = findViewById(R.id.btn_load_im);
        btnLoadIm.setVisibility(View.GONE);

        moistureLayout = findViewById(R.id.moisture_layout);
        tempLayout = findViewById(R.id.temp_layout);
        lightLayout = findViewById(R.id.light_layout);

        moistureValue = findViewById(R.id.moisture_value);
        tempValue = findViewById(R.id.temp_value);
        lightValue = findViewById(R.id.light_value);
        lsatUpdateValue = findViewById(R.id.time_value);
        slatStatus = findViewById(R.id.slat_status_value);
        tvSlat = findViewById(R.id.tv_slat);

        btnWater = findViewById(R.id.btn_water);
        btnFoggy = findViewById(R.id.btn_foggy);
        btnSlat = findViewById(R.id.btn_slat);
        btnHistory = findViewById(R.id.history);
        btnSetClock = findViewById(R.id.btn_set_clock);
        btnSetting = findViewById(R.id.btn_setting);


        exception = findViewById(R.id.exception);
        progressBar = findViewById(R.id.progress);
        progressBarImage = findViewById(R.id.progress_image);

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

        tempLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rawDataBean != null) {
                    startDetailsActivity("temp", new Gson().toJson(rawDataBean.getTempBean()));
                }
            }
        });

        lightLayout.setOnClickListener(new View.OnClickListener() {
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
        btnHistory.setOnClickListener(new View.OnClickListener() {
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
                if (tvSlat.getText().toString().trim().equals(getString(R.string.openSlat))) {
                    controlsDevices("3", getString(R.string.onOpenSlat));

                } else if (tvSlat.getText().toString().trim().equals(getString(R.string.closeSlat))) {

                    controlsDevices("4", getString(R.string.onCloseSlat));
                }
            }
        });
        btnLoadIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (isPermissionWriteExternal()) {
                        new SaveIMTask().execute(bitmap);
                    } else {
                        requestPermissionWriteExternal();
                    }
                } else {
                    new SaveIMTask().execute(bitmap);
                }

            }
        });
        btnSetClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), SetClockActivity.class));
                SetClockActivity.onSaveClockListener = getSaveClockListener();
            }
        });
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingActivity.onSaveSettingListener = getSaveSettingListener();
                startActivity(new Intent(getContext(), SettingActivity.class));
            }
        });

    }

    private void startDetailsActivity(String sensor, String data) {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra("sensor", sensor);
        intent.putExtra("data", data);
        startActivity(intent);
        MagDiscreteSeekBar.setOnSetStandardListener(this);
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
                    "android");
            microgear.subscribe(ConfigData.rawDataTopic);
            microgear.subscribe(ConfigData.hasPhotoTopic);
            microgear.subscribe(ConfigData.slatStatusTopic);
            microgear.subscribe(ConfigData.responsesTopic);
        } catch (NullPointerException e) {
            e.printStackTrace();
            callBack.onError(getString(R.string.noNetPieData));
        }
    }

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
        //startActivity(new Intent(getContext(), SetClockActivity.class));
    }

    private Context getContext() {
        return this;
    }

    private void onSubscribeCallBack(SubscribeBean result) {
        if (result != null) {
            try {
                Log.e(TAG, "onSubscribeCallBack topic: " + result.getTopic());
                if (result.getTopic().equals(ConfigData.photoTopic)) {
                    ImageBean imageBean = new ImageBean(result.getPayload());
                    bitmap = imageBean.getBitmap();
                    image.setImageBitmap(bitmap);
                    progressBarImage.setVisibility(View.GONE);
                    Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
                    fadeIn.setDuration(750);
                    image.startAnimation(fadeIn);
                    btnLoadIm.setVisibility(View.VISIBLE);
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
                        if (rawDataBean.getMoistureBean().getAverage() >= 0) {
                            moistureValue.setText(String.valueOf(rawDataBean.getMoistureBean().getAverage()) + " %");
                            setAnimation(moistureValue);

                        } else {
                            moistureValue.setText(getString(R.string.sensorError));
                            setAnimation(moistureValue);
                        }
                        if (rawDataBean.getTempBean().getAverage() >= 0) {
                            tempValue.setText(String.valueOf(rawDataBean.getTempBean().getAverage()) + " °C");
                            setAnimation(tempValue);
                        } else {
                            tempValue.setText(getString(R.string.sensorError));
                            setAnimation(tempValue);
                        }
                        if (rawDataBean.getLightBean().getLightIn() >= 0) {
                            lightValue.setText(String.valueOf(rawDataBean.getLightBean().getLightIn()) + " Lux");
                            setAnimation(lightValue);
                        } else {
                            lightValue.setText(getString(R.string.sensorError));
                            setAnimation(lightValue);
                        }
                        lsatUpdateValue.setText(getDateTime((rawDataBean.getTime() * 1000)));
                        setAnimation(lsatUpdateValue);

                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void updateSlatStatus(int status) {//0 is close , 1 is open
        Log.i(TAG, "updateSlatStatus: " + status);

        if (status == 0) {
            slatStatus.setText(getString(R.string.slatClose));
            tvSlat.setText(getString(R.string.openSlat));
            btnSlat.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.selector_btn_open_slat));
        } else if (status == 1) {
            slatStatus.setText(getString(R.string.slatOpen));
            tvSlat.setText(getString(R.string.closeSlat));
            btnSlat.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.selector_btn_close_slat));
        }
    }

    private void setAnimation(TextView textView) {
        AnimationSet set = new AnimationSet(true);
        TranslateAnimation trAnimation = new TranslateAnimation(0, 0, textView.getHeight(), 0);
        trAnimation.setDuration(500);
        trAnimation.setFillAfter(true);
        set.addAnimation(trAnimation);
        Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(750);
        set.addAnimation(fadeIn);
        textView.startAnimation(set);
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

    @Override
    public void onSetStandardListener(String sensor, int val) {
        JsonObject object = new JsonObject();
        object.addProperty(sensor, val);
        Log.e(TAG, "payload: " + object.toString());
        publishStandard(object.toString());
    }

    public ClockListener.OnSaveClockListener getSaveClockListener() {
        return this;
    }

    @Override
    public void onSaveClockListener(String clockSets) {
        Log.e(TAG, "onSaveClockListener, clockSets: " + clockSets);
        publishClock(clockSets);
    }

    private String getDateTime(long time) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
            Date date = (new Date(time));
            return dateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
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

    private OnSaveSettingListener getSaveSettingListener() {
        return this;
    }

    private boolean checkNetPie() {
        Log.i(TAG, "check NetPie");
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

    public boolean isConnectInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected()) ? true : false;
    }


    /**
     * for GCM Google Cloud Message
     */
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

        appId.setText("SmartGardenCare");
        key.setText("L8LswNXzRY9nalS");
        secret.setText("KcysynAOjLxZ3BkGzqSq4WvSA");


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

        @Override
        public void onInfo(String info) {

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != ConfigData.REQUEST_CODE_PERMISSIONS_WRITE_EXTERNAL_STORAGE) {
            return;
        }
        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                if (shouldShowRequestPermissionRationale(permission)) {
                    Log.i(TAG, "Permission denied without 'NEVER ASK AGAIN': " + permission);
                    showRequestPermissionsSnackbar();
                } else {
                    Log.i(TAG, "Permission denied with 'NEVER ASK AGAIN': " + permission);
                    showLinkToSettingsSnackbar();
                }
            } else {
                Log.i(TAG, "Permission granted, building GoogleApiClient");
                new SaveIMTask().execute(bitmap);
            }
        }
    }

    private boolean isPermissionWriteExternal() {
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }

    private void requestPermissionWriteExternal() {
        ActivityCompat.requestPermissions(
                (Activity) getContext(),
                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                ConfigData.REQUEST_CODE_PERMISSIONS_WRITE_EXTERNAL_STORAGE);
    }

    /**
     * Displays {@link Snackbar} instructing user to visit Settings to grant permissions required by
     * this application.
     */
    private void showLinkToSettingsSnackbar() {
        Snackbar.make(rootLayout,
                R.string.permissionDenied,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.setting, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Build intent that displays the App settings screen.
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package",
                                BuildConfig.APPLICATION_ID, null);
                        intent.setData(uri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }).show();
    }

    /**
     * Displays {@link Snackbar} with button for the user to re-initiate the permission workflow.
     */
    private void showRequestPermissionsSnackbar() {
        Snackbar.make(rootLayout, R.string.permissionDenied,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Request permission.
                        requestPermissionWriteExternal();
                    }
                }).show();
    }

    private void showLoadImSuccessSnackbar(final Uri uri) {
        Snackbar.make(rootLayout, R.string.loadIMSuccess,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.open, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(uri, "image/*");
                        startActivity(intent);
                    }
                }).show();
    }

    public class SaveIMTask extends AsyncTask<Bitmap, Void, Uri> {

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
            //super.onPreExecute();
        }

        @Override
        protected Uri doInBackground(Bitmap... params) {
            return saveBitmapTpFile(params[0], getContext());
        }

        @Override
        protected void onPostExecute(Uri result) {
            Log.i(TAG, "onPostExecute");
            //notificationSnackBar(rootLayout,getString(R.string.loadIMSuccess));
            showLoadImSuccessSnackbar(result);

        }

        public Uri saveBitmapTpFile(Bitmap bitmap, Context context) {
            Log.i(TAG, "saveBitmapTpFile");
            String imageName = "SG_IMG" + String.valueOf(System.currentTimeMillis()) + ".jpg";
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), imageName);
            OutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

            }
            Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
            Uri contentUri = Uri.fromFile(file);
            mediaScanIntent.setData(contentUri);
            context.sendBroadcast(mediaScanIntent);
            file = null;
            return contentUri;
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

    public void publishClock(String payload) {
        if (isConnectInternet(getContext())) {
            if (isConnectNetPie) {
                publishBean = new PublishBean(ConfigData.alarmTopic, payload);
                Log.e(TAG, "publish alarm clock");
                microgear.publish(ConfigData.alarmTopic, payload, 1, true);

            } else {
                if (SetClockActivity.onFinishSaveClockListener != null) {
                    SetClockActivity.onFinishSaveClockListener.onFinishSaveClockListener(-1);
                }
            }

        } else {
            if (SetClockActivity.onFinishSaveClockListener != null) {
                SetClockActivity.onFinishSaveClockListener.onFinishSaveClockListener(0);
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
                if (isConnectNetPie) {
                    microgear.publish(topic, payload, 1, true);
                } else {
                    notificationSnackBar(rootLayout, getString(R.string.notConnectNetPie));
                }
            }

            publishHandle = new Handler();
            publishTask = new Runnable() {
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
            publishHandle.postDelayed(publishTask, 25000); //20 second
        } else {
            notificationSnackBar(rootLayout, getString(R.string.noInternet));
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
                if (bitmap != null) {
                    image.setImageBitmap(new BlurImage().make(getContext(), bitmap, 25));
                }
                if (progressBarImage != null) {
                    progressBarImage.setVisibility(View.VISIBLE);
                }
                if (btnLoadIm != null) {
                    btnLoadIm.setVisibility(View.GONE);
                }
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
            getSupportActionBar().setTitle(getString(R.string.app_name));
            if (message.equals(getString(R.string.loadingImage)) && progressBarImage != null) {
                progressBarImage.setVisibility(View.GONE);
            }
            onSubscribeCallBack(result);
        }
    }

    //SmartGardenCare
    //SmartGardenCare

    //L8LswNXzRY9nalS
    //L8LswNXzRY9nalS
    //KcysynAOjLxZ3BkGzqSq4WvSA
    //KcysynAOjLxZ3BkGzqSq4WvSA
}

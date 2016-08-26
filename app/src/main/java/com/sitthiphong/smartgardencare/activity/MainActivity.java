package com.sitthiphong.smartgardencare.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.JsonObject;
import com.itextpdf.text.DocumentException;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;
import com.sitthiphong.netpiegear.EventListener;
import com.sitthiphong.netpiegear.Microgear;
import com.sitthiphong.smartgardencare.R;
import com.sitthiphong.smartgardencare.activity.fragment.ImageFragment;
import com.sitthiphong.smartgardencare.activity.fragment.LightFragment;
import com.sitthiphong.smartgardencare.activity.fragment.LogFragment;
import com.sitthiphong.smartgardencare.activity.fragment.MoistureFragment;
import com.sitthiphong.smartgardencare.activity.fragment.TempFragment;
import com.sitthiphong.smartgardencare.core.MagPDF;
import com.sitthiphong.smartgardencare.datamodel.ImageBean;
import com.sitthiphong.smartgardencare.datamodel.PublishBean;
import com.sitthiphong.smartgardencare.datamodel.RawDataBean;
import com.sitthiphong.smartgardencare.datamodel.ResponseBean;
import com.sitthiphong.smartgardencare.datamodel.StatusBean;
import com.sitthiphong.smartgardencare.datamodel.SubscribeBean;
import com.sitthiphong.smartgardencare.listener.ActionListener;
import com.sitthiphong.smartgardencare.listener.NetworkChangeListener;
import com.sitthiphong.smartgardencare.service.NetPieRestApi;
import com.sitthiphong.smartgardencare.provider.BusProvider;
import com.sitthiphong.smartgardencare.provider.GsonProvider;
import com.sitthiphong.smartgardencare.service.GcmRegisterService;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    private final int IS_CONNECT_NETPIE = 200;
    private final int ERROR = 199;
    private final int WAIT = 201;
    private final int REQUEST_CODE_ASK_PERMISSIONSc = 123;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;


    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private RelativeLayout rootLayout;
    private BottomBar mBottomBar;
    private ActionListener actionListener = new ActionListener();
    private NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    private JsonObject objSetting;

    public Microgear microgear = new Microgear(this);
    public EventListener eventListener = new EventListener();
    private PublishBean publishBean;
    private Handler publishHandle;
    private Runnable publistask;
    private String appID = "ECPSmartGarden"; //APP_ID
    private String appKey = "23oRa3PnK8Czx91"; //KEY
    private String appSecret = "U1zuonSDIeEeokxAskiJJJTEW"; //SECRET
    private String[] topicList;

    private int menuItemId;
    private ImageFragment imFragment;
    private MoistureFragment moistureFragment;
    private TempFragment tempFragment;
    private LightFragment lightFragment;
    private Fragment fragment;
    private LogFragment logFragment;
    private android.support.v4.app.FragmentManager supportFragmentManager = getSupportFragmentManager();


    private StatusBean statusBean = new StatusBean(WAIT, "");
    private RawDataBean rawDataBean;
    private ImageBean imageBean = new ImageBean();
    private ProgressDialog progressDialog;
    private String logListAsJsonString;
    private String rawListAsJsonString;
    private int STSlat;
    private String methodUsePermission;

    private boolean isReceiverRegistered;


    private final String appIdTAG = "appId";
    private final String appKeyTAG = "appKey";
    private final String appSecretTAG = "appSecret";
    private final String dayStorageTAG = "dayStorage";
    private final String fqPubRawDataTAG = "fqPubRawData";
    private final String fqPubImageTAG = "fqPubImage";  //ไปตั้งค่าอยู่หน้า image fragment
    private final String fqInsertRawDataTAG = "fqInsertRawData";
    private final String autoModeTAG = "autoMode";


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String head = bundle.getString("head");
            if (head.equals("subscribe")) {
                Log.i(TAG, "NETPIE Event Listener: onSubscribe");
                String topic = bundle.getString("topic");
                String message = bundle.getString("message");
                Log.i(TAG, "NETPIE Event Listener:    topic: " + topic);

                if (topic.equals("response")) {
                    ResponseBean responseBean = new ResponseBean(message);
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    if (publishBean.getTopic().equals(responseBean.getTopic())) {
                        if (responseBean.isSuccess()) {
                            if (publishHandle != null) {
                                Log.e(TAG, "remove task");
                                publishHandle.removeCallbacks(publistask);
                            }
                            if (responseBean.getTopic().equals("settingDetails")) {
                                JsonObject objDetails = GsonProvider.getInstance().fromJson(responseBean.getMessage(), JsonObject.class);
                                if (objDetails.get(fqPubRawDataTAG) != null) {
                                    editor.putInt(fqPubRawDataTAG, objDetails.get(fqPubRawDataTAG).getAsInt());
                                }
                                if (objDetails.get(fqInsertRawDataTAG) != null) {
                                    editor.putInt(fqInsertRawDataTAG, objDetails.get(fqInsertRawDataTAG).getAsInt());
                                }
                                if (objDetails.get(dayStorageTAG) != null) {
                                    editor.putInt(dayStorageTAG, objDetails.get(dayStorageTAG).getAsInt());

                                }
                                if (objDetails.get(fqPubImageTAG) != null) {
                                    editor.putInt(fqPubImageTAG, objDetails.get(fqPubImageTAG).getAsInt());
                                }
                                if (objDetails.get(autoModeTAG) != null) {
                                    editor.putBoolean(autoModeTAG, objDetails.get(autoModeTAG).getAsBoolean());
                                }
                                if (objDetails.get("objNETPIE") != null) {
                                    JsonObject object = objDetails.get("objNETPIE").getAsJsonObject();
                                    if (object.get(appIdTAG) != null) {
                                        editor.putString(appIdTAG, object.get(appIdTAG).getAsString());
                                    }
                                    if (object.get(appKeyTAG) != null) {
                                        editor.putString(appKeyTAG, object.get(appKeyTAG).getAsString());
                                    }
                                    if (object.get(appSecretTAG) != null) {
                                        editor.putString(appSecretTAG, object.get(appSecretTAG).getAsString());
                                    }

                                    editor.commit();
                                    Log.e(TAG, "fqPubRawData: " + sharedPreferences.getInt(fqPubRawDataTAG, 0));
                                    Log.e(TAG, "fqPubImage: " + sharedPreferences.getInt(fqPubImageTAG, 0));
                                    Log.e(TAG, "fqInsertRawDataTAG: " + sharedPreferences.getInt(fqInsertRawDataTAG, 0));
                                    Log.e(TAG, "dayStorage: " + sharedPreferences.getInt(dayStorageTAG, 0));
                                    Log.e(TAG, "autoMode: " + sharedPreferences.getInt(autoModeTAG, 0));
                                    Log.e(TAG, "AppId: " + sharedPreferences.getString(appIdTAG, "null"));
                                    Log.e(TAG, "AppKey: " + sharedPreferences.getString(appKeyTAG, "null"));
                                    Log.e(TAG, "AppSecret: " + sharedPreferences.getString(appSecretTAG, "null"));
                                    reStartActivity();
                                }

                                editor.commit();
                                Log.e(TAG, "fqPubRawData: " + sharedPreferences.getInt(fqPubRawDataTAG, 0));
                                Log.e(TAG, "fqPubImage: " + sharedPreferences.getInt(fqPubImageTAG, 0));
                                Log.e(TAG, "fqInsertRawDataTAG: " + sharedPreferences.getInt(fqInsertRawDataTAG, 0));
                                Log.e(TAG, "dayStorage: " + sharedPreferences.getInt(dayStorageTAG, 0));
                                Log.e(TAG, "autoMode: " + sharedPreferences.getBoolean(autoModeTAG, false));
                                alertDialog(getResources().getString(R.string.saveSetting),
                                        getResources().getString(R.string.success));

                            } else if (responseBean.getTopic().equals("settingStandard")) {
                                JsonObject object = GsonProvider.getInstance().fromJson(responseBean.getMessage(), JsonObject.class);
                                int value = object.get("value").getAsInt();

                                if (object.get("sensor") != null) {
                                    String sensor = object.get("sensor").getAsString();
                                    if (sensor.equals("SoilMoisture")) {
                                        editor.putFloat("moisture", (float) value);
                                    } else if (sensor.equals("dht22")) {
                                        editor.putFloat("temp", (float) value);
                                    } else if (sensor.equals("bh1750")) {
                                        editor.putFloat("light", (float) value);
                                    }
                                    editor.commit();
                                    alertDialog(getResources().getString(R.string.saveSetting),
                                            getResources().getString(R.string.success));
                                }
                            } else if (responseBean.getTopic().equals("controlDevice")) {
                                JsonObject object = new JsonObject();
                                object = GsonProvider.getInstance().fromJson(publishBean.getPayload(), JsonObject.class);
                                if (object.get("1") != null) {
                                    alertDialog(getResources().getString(R.string.water),
                                            getResources().getString(R.string.success));
                                } else if (object.get("2") != null) {
                                    alertDialog(getResources().getString(R.string.shower),
                                            getResources().getString(R.string.success));
                                } else if (object.get("3") != null) {
                                    alertDialog(getResources().getString(R.string.acOpenSlat),//acOpenSlat
                                            getResources().getString(R.string.success));
                                } else if (object.get("4") != null) {
                                    alertDialog(getResources().getString(R.string.acCloseSlat),//acCloseSlat
                                            getResources().getString(R.string.acCloseSlat));
                                }

                            } else if (responseBean.getTopic().equals("refreshIM")) {

                            }
                        } else {
                            //for error control device
                            if (publishHandle != null) {
                                Log.e(TAG, "remove task");
                                publishHandle.removeCallbacks(publistask);
                            }
                            if (responseBean.getTopic().equals("controlDevice")) {
                                if (responseBean.getMessage().equals("water falsed!")) {
                                    alertDialog(getResources().getString(R.string.exception),
                                            getResources().getString(R.string.waterFalse));
                                } else {
                                    alertDialog(getResources().getString(R.string.warning), responseBean.getMessage());
                                }

                            }
                            Log.e(TAG, responseBean.getMessage());
                            notificationSnackBar(responseBean.getMessage());
                        }
                    } else {
                        // if topic not math
                    }
                } else if (topic.equals("rawData")) {
                    statusBean = new StatusBean(getResources().getInteger(R.integer.IS_CONNECT_NETPIE), "");
                    rawDataBean = new RawDataBean(GsonProvider.getInstance().fromJson(message, JsonObject.class));
                    try {
                        actionListener.onUpdateRawBean.onUpdateRawBean(rawDataBean);
                    } catch (IllegalStateException e) {

                    } catch (NullPointerException e) {

                    }

                } else if (topic.equals("STSlat")) {
                    STSlat = Integer.parseInt(message);
                    try {
                        actionListener.onUpdateSlatStatus.onUpdateSlatStatus(STSlat);
                    } catch (IllegalStateException e) {

                    } catch (NullPointerException e) {

                    }
                } else if (topic.equals("hasPhoto")) {
                    new SubscribeTask(getResources().getString(R.string.fetching))
                            .execute(appID = sharedPreferences.getString(appIdTAG, ""),
                                    appKey = sharedPreferences.getString(appKeyTAG, ""),
                                    sharedPreferences.getString(appSecretTAG, ""),
                                    "photo");
                } else if (topic.equals("hasRawList")) {
                    new SubscribeTask(getResources().getString(R.string.fetching))
                            .execute(appID = sharedPreferences.getString(appIdTAG, ""),
                                    appKey = sharedPreferences.getString(appKeyTAG, ""),
                                    sharedPreferences.getString(appSecretTAG, ""),
                                    "rawDataList");
                } else if (topic.equals("hasLogList")) {
                    new SubscribeTask(getResources().getString(R.string.fetching))
                            .execute(appID = sharedPreferences.getString(appIdTAG, ""),
                                    appKey = sharedPreferences.getString(appKeyTAG, ""),
                                    sharedPreferences.getString(appSecretTAG, ""),
                                    "logDataList");
                } else if (topic.equals("onPresent")) {
                    JsonObject object = GsonProvider.getInstance().fromJson(message, JsonObject.class);
                    try {
                        new MaterialDialog.Builder(getContextManual())
                                .title(getString(R.string.onPresent))
                                .content(object.get("alias").getAsString())
                                .positiveText(getResources().getString(R.string.ok))
                                .show();
                    } catch (NullPointerException e) {

                    }
                } else if (topic.equals("onAbsent")) {
                    JsonObject object = GsonProvider.getInstance().fromJson(message, JsonObject.class);
                    try {
                        new MaterialDialog.Builder(getContextManual())
                                .title(getString(R.string.onAbsent))
                                .content(object.get("alias").getAsString())
                                .positiveText(getResources().getString(R.string.ok))
                                .show();
                    } catch (NullPointerException e) {

                    }
                }
            } else if (head.equals("connect")) {
                boolean status = bundle.getBoolean("status");
                if (status) {
                    Log.i(TAG, "NETPIE Event Listener: onConnect: Connected to NETPIE!!");
                    notificationSnackBar(getApplicationContext().getString(R.string.connectedNETPIE));
                    statusBean = new StatusBean(getResources().getInteger(R.integer.IS_CONNECT_NETPIE), null);
                    microgear.publish("token", sharedPreferences.getString("token", ""), 0, true);
                    Log.e(TAG, "publush token 9999");
                    if (mBottomBar != null) {
                        Log.e(TAG, "create BottomBar: position " + mBottomBar.getCurrentTabPosition());
                        if (mBottomBar.getCurrentTabPosition() == 0) {
                            if (actionListener.onRequestUpdateImage != null) {
                                actionListener.onRequestUpdateImage.onRequestUpdateImage();
                            }
                        } else if (mBottomBar.getCurrentTabPosition() == 1) {
                            if (actionListener.onRequestRawData != null) {
                                actionListener.onRequestRawData.OnRequestRawData();
                            }
                        } else if (mBottomBar.getCurrentTabPosition() == 2) {
                            if (actionListener.onRequestRawData != null) {
                                actionListener.onRequestRawData.OnRequestRawData();
                            }
                        } else if (mBottomBar.getCurrentTabPosition() == 3) {
                            if (actionListener.onRequestRawData != null) {
                                actionListener.onRequestRawData.OnRequestRawData();
                            }
                        } else if (mBottomBar.getCurrentTabPosition() == 4) {
                            if (actionListener.onRequestLog != null) {
                                actionListener.onRequestLog.onRequestLog();
                            }
                        }
                    } else {
                        Log.e(TAG, "not create BottomBar");
                    }

                } else {
                    Log.i(TAG, "NETPIE Event Listener: onConnectFalse: Can't connect to NETPIE!!");
                    statusBean = new StatusBean(getResources().getInteger(R.integer.ERROR),
                            getApplicationContext().getString(R.string.notConnectedNETPIE));
                    statusBean = new StatusBean(getResources().getInteger(R.integer.ERROR),
                            getResources().getString(R.string.notConnectedNETPIE));
                    notificationSnackBar(statusBean.getException());
                    actionListener.onException.onException(statusBean.getException());
                }
            }
        }
    };
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
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            return false;
        }
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("Details", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        setContentView(R.layout.activity_main);

        rootLayout = (RelativeLayout) findViewById(R.id.rootLayoutMain);

        registerReceiver();


        if (checkPlayServices()) {
            registerGcm();
        }


        //Log.e(TAG,"token: "+sharedPreferences.getString("token",""));


        setActionListener();
        setNetPieEventListener();
        setNetworkChangeListener();
        if (checkFirstOpenApp()) {

        } else {
            connectNETPIE();
        }
        mBottomBar = BottomBar.attach(this, savedInstanceState);
//        mBottomBar = BottomBar.attachShy((CoordinatorLayout) findViewById(R.id.coordinatorLayout),
//                findViewById(R.id.nestedScrollView), savedInstanceState);
        mBottomBar.noTabletGoodness();
        mBottomBar.setItems(R.menu.bottom_bar_menu);
        //mBottomBar.setDefaultTabPosition(bottomPosition);
        //mBottomBar.setActiveTabColor(ContextCompat.getColor(this, R.color.grey));


        mBottomBar.setOnMenuTabClickListener(new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int menuItemId) {
                setFragment(menuItemId);
            }

            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {

            }
        });
        mBottomBar.mapColorForTab(0, ContextCompat.getColor(this, R.color.colorAccent));
        mBottomBar.mapColorForTab(1, ContextCompat.getColor(this, R.color.blue));
        mBottomBar.mapColorForTab(2, ContextCompat.getColor(this, R.color.deepOrange));
        mBottomBar.mapColorForTab(3, ContextCompat.getColor(this, R.color.amber));
        mBottomBar.mapColorForTab(4, ContextCompat.getColor(this, R.color.grey));


        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentContain,
                        new ImageFragment().newInstance(),
                        "im").commit();

        //startActivity(new Intent(this,SettingActivity.class));
    }

    @Override
    protected void onStart() {
        Log.i(TAG, "onStart");
        super.onStart();
        //BusProvider.getInstance().register(this);
        MagPDF magPDF = new MagPDF();
        try {
            magPDF.createPdf(MagPDF.PATH);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
        microgear.bindServiceResume();
        registerReceiver();
        editor.putString("activity", "onResume");
        editor.commit();
        Log.e(TAG, "COMMIT");


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
        BusProvider.getInstance().unregister(this);
        editor.putString("activity", "onStop");
        editor.commit();
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
            Log.w(TAG, "Disconnect Microgear!!");
        }
        editor.putString("activity", "onDestroy");
        editor.commit();

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
        Log.i(TAG, "onOptionsItemSelected");
        int id = item.getItemId();
        if (id == 16908332) {
            onBackPressed();
            return true;
        }
        if (id == R.id.actionSetting) {
            startActivity(new Intent(getContextManual(), SettingActivity.class));
            return true;
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

    public Handler getPublishHandle() {
        return publishHandle;
    }

    public void setActionListener() {
        Log.i(TAG, "setActionListener");
        actionListener.setOnFinishSetupNETPIE(new ActionListener.OnFinishSetupNETPIE() {
            @Override
            public void onFinishSetupNETPIE(boolean event) {
                if (event) {
                    connectNETPIE();
                } else {
                    statusBean = new StatusBean(getResources().getInteger(R.integer.ERROR),
                            getResources().getString(R.string.notSetupNETPIE));
                    actionListener.onException.onException(statusBean.getException());


                }
            }
        });
        actionListener.setOnException(new ActionListener.OnException() {
            @Override
            public void onException(String error) {

            }
        });
        actionListener.setOnSaveSetting(new ActionListener.OnSaveSetting() {

            @Override
            public void onSaveSetting(boolean changeNETPIE, boolean changeDetails, JsonObject objNETPIE, JsonObject objDetails) {
                if (changeNETPIE && changeDetails) {
                    objDetails.addProperty("objNETPIE", GsonProvider.getInstance().toJson(objNETPIE));
                    Log.e(TAG, "gson: " + GsonProvider.getInstance().toJson(objDetails));
                    publish("settingDetails",
                            GsonProvider.getInstance().toJson(objDetails),
                            getResources().getString(R.string.onSaveSetting));
                } else if (changeNETPIE) {
                    if (objNETPIE.get(appIdTAG) != null) {
                        editor.putString(appIdTAG, objNETPIE.get(appIdTAG).getAsString());
                    }
                    if (objNETPIE.get(appKeyTAG) != null) {
                        editor.putString(appKeyTAG, objNETPIE.get(appKeyTAG).getAsString());
                    }
                    if (objNETPIE.get(appSecretTAG) != null) {
                        editor.putString(appSecretTAG, objNETPIE.get(appSecretTAG).getAsString());
                    }
                    editor.commit();
                    reStartActivity();
                } else if (changeDetails) {
                    Log.e(TAG, "gson: " + GsonProvider.getInstance().toJson(objDetails));
                    publish("settingDetails",
                            GsonProvider.getInstance().toJson(objDetails),
                            getResources().getString(R.string.onSaveSetting));
                }
            }
        });
        actionListener.setOnSaveStandard(new ActionListener.OnSaveStandard() {
            @Override
            public void onSaveStandard(JsonObject obj) {
                publish("settingStandard",
                        GsonProvider.getInstance().toJson(obj),
                        getResources().getString(R.string.onSaveSetting));
            }
        });
        actionListener.setOnControlDevice(new ActionListener.OnControlDevice() {

            @Override
            public void onControlDevice(int device, boolean isOpen) {
                JsonObject object = new JsonObject();
                object.addProperty(String.valueOf(device), isOpen);
                String message = "";
                if (device == 1) {
                    message = getResources().getString(R.string.water___);
                } else if (device == 2) {
                    message = getResources().getString(R.string.shower___);
                } else if (device == 3) {
                    message = getResources().getString(R.string.openSlat___);//openSlat___
                } else if (device == 4) {
                    message = getResources().getString(R.string.closeSlat___);
                }
                publish("controlDevice",
                        GsonProvider.getInstance().toJson(object),
                        message);


            }
        });
        actionListener.setOnRequestUpdateImage(new ActionListener.OnRequestUpdateImage() {
            @Override
            public void onRequestUpdateImage() {
                if (statusBean.getStatus() == getResources().getInteger(R.integer.IS_CONNECT_NETPIE)) {
                    if (imageBean.getTimeStamp() > 0l) {
                        actionListener.onUpdateImage.onUpdateImage(statusBean, imageBean);
                    } else {
                        //load
                        new SubscribeTask(getResources().getString(R.string.fetching))
                                .execute(appID = sharedPreferences.getString(appIdTAG, ""),
                                        appKey = sharedPreferences.getString(appKeyTAG, ""),
                                        sharedPreferences.getString(appSecretTAG, ""),
                                        "photo");
                    }
                } else {
                    actionListener.onException.onException(statusBean.getException());
                }

            }
        });

        actionListener.setOnRequestRawBean(new ActionListener.OnRequestRawBean() {
            @Override
            public void onRequestRawBean() {
                if (statusBean.getStatus() == getResources().getInteger(R.integer.IS_CONNECT_NETPIE)) {
                    if (actionListener.onUpdateRawBean != null) {
                        actionListener.onUpdateRawBean.onUpdateRawBean(rawDataBean);
                    }
                } else if (statusBean.getStatus() == getResources().getInteger(R.integer.NO_INTERNET)) {
                    if (actionListener.onNoInternet != null) {
                        actionListener.onNoInternet.onNoInternet(statusBean.getException());
                    }
                } else if (statusBean.getStatus() == getResources().getInteger(R.integer.ERROR)) {
                    if (actionListener.onException != null) {
                        actionListener.onException.onException(statusBean.getException());
                    }

                }
            }
        });
        actionListener.setOnRequestRawList(new ActionListener.OnRequestRawList() {
            @Override
            public void onRequestRawList() {
                if (statusBean.getStatus() == getResources().getInteger(R.integer.IS_CONNECT_NETPIE)) {
                    if (rawListAsJsonString != null) {
                        if (actionListener.onUpdateRawList != null) {
                            actionListener.onUpdateRawList.onUpdateRawList(rawListAsJsonString);
                        }
                    } else {
                        //load
                        new SubscribeTask(getResources().getString(R.string.fetching))
                                .execute(appID = sharedPreferences.getString(appIdTAG, ""),
                                        appKey = sharedPreferences.getString(appKeyTAG, ""),
                                        sharedPreferences.getString(appSecretTAG, ""),
                                        "rawDataList");
                    }
                } else {
                    actionListener.onException.onException(statusBean.getException());
                }
            }
        });

        actionListener.setOnRequestLog(new ActionListener.OnRequestLog() {
            @Override
            public void onRequestLog() {
                if (statusBean.getStatus() == getResources().getInteger(R.integer.IS_CONNECT_NETPIE)) {
                    if (logListAsJsonString != null) {
                        actionListener.onUpdateLog.onUpdateLog(statusBean, logListAsJsonString);
                    } else {
                        //load
                        new SubscribeTask(getResources().getString(R.string.fetching))
                                .execute(appID = sharedPreferences.getString(appIdTAG, ""),
                                        appKey = sharedPreferences.getString(appKeyTAG, ""),
                                        sharedPreferences.getString(appSecretTAG, ""),
                                        "logDataList");
                    }
                } else {
                    actionListener.onException.onException(statusBean.getException());
                }

            }
        });
        actionListener.setOnRequestSlatStatus(new ActionListener.OnRequestSlatStatus() {
            @Override
            public void onRequestSlatStatus() {
                actionListener.onUpdateSlatStatus.onUpdateSlatStatus(STSlat);
            }
        });
        actionListener.setOnCheckPermission(new ActionListener.OnCheckPermission() {
            @Override
            public void onCheckPermission(String method, String permission) {
                methodUsePermission = method;
            }
        });
        actionListener.setOnRefreshImage(new ActionListener.OnRefreshImage() {
            @Override
            public void onRefreshImage() {
                publish("refreshIM", "1", getResources().getString(R.string.refreshIM));
            }
        });
        actionListener.setOnRegisterGCMFinish(new ActionListener.OnRegisterGCMFinish() {
            @Override
            public void onRegisterGCMFinish(String token) {
                //microgear.publish("token",token,0,true);
                microgear.publish("token", token, 1, true);
                //if(microgear != null){
                //    new NetPieRestApi(appID,appKey,appSecret).publish("token",sharedPreferences.getString("token",""),true);
                //    Log.e(TAG,"update Token");
                //}

            }
        });
        actionListener.setOnNoti(new ActionListener.OnNoti() {
            @Override
            public void onNoti(Bundle data) {
                new MaterialDialog.Builder(getContextManual())
                        .title(String.valueOf(data.get("title")))
                        .content(String.valueOf(data.get("body")))
                        .positiveText(getResources().getString(R.string.ok))
                        .show();
            }
        });


    }

    public void setNetPieEventListener() {
        Log.e(TAG, "setNetPieEventListener");
        eventListener.setConnectEventListener(new EventListener.OnServiceConnect() {
            @Override
            public void onConnect(Boolean status) {
                Message msg = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("head", "connect");
                bundle.putBoolean("status", status);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }

        });

        eventListener.setMessageEventListener(new EventListener.OnMessageReceived() {
            @Override
            public void onMessage(String topic, String message) {
                topic = topic.substring(1);
                topicList = topic.split("/");
                topic = topicList[1];
//                Log.e(TAG,"NETPIE Event Listener: onMessage");
//                Log.e(TAG,"NETPIE Event Listener:    topic: "+topic);
//                Log.e(TAG,"NETPIE Event Listener:    message: "+message);
                Message msg = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("head", "subscribe");
                bundle.putString("topic", topic);
                bundle.putString("message", message);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        });

        eventListener.setPresentEventListener(new EventListener.OnPresent() {
            @Override
            public void onPresent(String name) {
                Log.e(TAG, "NETPIE Event Listener: onPresent: " + name);
                Message msg = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("head", "subscribe");
                bundle.putString("topic", "onPresent");//onPresent,onAbsent
                bundle.putString("message", name);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        });

        eventListener.setAbsentEventListener(new EventListener.OnAbsent() {
            @Override
            public void onAbsent(String name) {
                Log.e(TAG, "NETPIE Event Listener: onAbsent: " + name);
                Message msg = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("head", "subscribe");
                bundle.putString("topic", "onAbsent");
                bundle.putString("message", name);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        });

        eventListener.setDisconnectEventListener(new EventListener.OnClose() {
            @Override
            public void onDisconnect(Boolean status) {
                Log.e(TAG, "NETPIE Event Listener: onDisconnect: " + String.valueOf(status));
            }
        });

        eventListener.setOnException(new EventListener.OnException() {
            @Override
            public void onException(String error) {
                Log.e(TAG, "NETPIE Event Listener: onException: " + error);
                statusBean = new StatusBean(getResources().getInteger(R.integer.ERROR), error);
                actionListener.onException.onException(error);

            }
        });

    }

    private void setFragment(int menuItemId) {
        Log.e(TAG, "setFragment");
        if (menuItemId == R.id.bottomBarImage) {
            imFragment = ImageFragment.newInstance();
            clearFragment();
            supportFragmentManager.beginTransaction().replace(
                    R.id.fragmentContain,
                    imFragment,
                    getApplicationContext().getString(R.string.image)
            ).commit();
            setTitle(R.string.image);
            this.menuItemId = R.id.bottomBarImage;
        } else if (menuItemId == R.id.bottomBarMoisture) {
            moistureFragment = MoistureFragment.newInstance().newInstance();
            clearFragment();
            supportFragmentManager.beginTransaction().replace(
                    R.id.fragmentContain,
                    moistureFragment,
                    getApplicationContext().getString(R.string.moisture)
            ).commit();
            setTitle(R.string.moisture);
            this.menuItemId = R.id.bottomBarMoisture;
        } else if (menuItemId == R.id.bottomBarTemp) {
            tempFragment = TempFragment.newInstance();
            clearFragment();
            supportFragmentManager.beginTransaction().replace(
                    R.id.fragmentContain,
                    tempFragment,
                    getApplicationContext().getString(R.string.temp)
            ).commit();
            setTitle(R.string.temp);
            this.menuItemId = R.id.bottomBarTemp;
        } else if (menuItemId == R.id.bottomBarLight) {
            lightFragment = LightFragment.newInstance();
            clearFragment();
            supportFragmentManager.beginTransaction().replace(
                    R.id.fragmentContain,
                    lightFragment,
                    getApplicationContext().getString(R.string.light)
            ).commit();
            setTitle(R.string.light);
            this.menuItemId = R.id.bottomBarLight;
        } else if (menuItemId == R.id.bottomBarLog) {
            logFragment = LogFragment.newInstance();
            clearFragment();
            supportFragmentManager.beginTransaction().replace(
                    R.id.fragmentContain,
                    logFragment,
                    getApplicationContext().getString(R.string.log)
            ).commit();
            setTitle(R.string.log);
            this.menuItemId = R.id.bottomBarLog;
        }

    }

    private void clearFragment() {
        fragment = supportFragmentManager.findFragmentByTag(getApplicationContext().getString(R.string.image));
        if (fragment != null) {
            supportFragmentManager.beginTransaction().remove(fragment).commit();
        }
        fragment = supportFragmentManager.findFragmentByTag(getApplicationContext().getString(R.string.moisture));
        if (fragment != null) {
            supportFragmentManager.beginTransaction().remove(fragment).commit();
        }
        fragment = supportFragmentManager.findFragmentByTag(getApplicationContext().getString(R.string.temp));
        if (fragment != null) {
            supportFragmentManager.beginTransaction().remove(fragment).commit();
        }
        fragment = supportFragmentManager.findFragmentByTag(getApplicationContext().getString(R.string.light));
        if (fragment != null) {
            supportFragmentManager.beginTransaction().remove(fragment).commit();
        }
        fragment = supportFragmentManager.findFragmentByTag(getApplicationContext().getString(R.string.log));
        if (fragment != null) {
            supportFragmentManager.beginTransaction().remove(fragment).commit();
        }

    }

    public void setNetworkChangeListener() {
        networkChangeListener.setNetworkChange(new NetworkChangeListener.OnNetworkChange() {
            @Override
            public void onNetworkChange(boolean isConnect) {
                if (isConnect) {
                    notificationSnackBar("is Connect Internet");

                } else {
                    //notificationSnackBar("no Connect Internet");
                }

            }
        });
    }


    public void publish(String topic, String payload, String messageDialog) {
        Log.e(TAG, "publish");
        Log.e(TAG, "  topic: " + topic);
        Log.e(TAG, "  payload: " + payload);

        //for get return object
//        objSetting = GsonProvider.getInstance().fromJson(payload,JsonObject.class);
//        if(objSetting.get("settingDetails")!= null){
//            Log.e(TAG,"settingDetails!= null");
//            JsonObject jsonObject = GsonProvider.getInstance().fromJson(objSetting.get("settingDetails").getAsString(),JsonObject.class);
//            if(jsonObject.get("objNETPIE")!=null){
//                Log.e(TAG,"has changeNETPIE");
//                JsonObject objNETPIE = GsonProvider.getInstance().fromJson(jsonObject.get("objNETPIE").getAsString(),JsonObject.class);
//                if(objNETPIE.get("appID")!=null){
//                    Log.e(TAG,"appID: "+objNETPIE.get("appID").getAsString());
//                }
//            }
//        }
        //------------------------------------
        if (isConnectingToInternet(getContextManual())) {
            progressDialog = new ProgressDialog(getContextManual());
            progressDialog.setMessage(messageDialog);
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
            publishBean = new PublishBean(topic, payload);
            microgear.publish(topic, payload);
            publishHandle = new Handler();
            publistask = new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "run task");
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    new MaterialDialog.Builder(getContextManual())
                            .title(getResources().getString(R.string.exception))
                            .content(getResources().getString(R.string.piNotResponse))
                            .positiveText(getResources().getString(R.string.ok))
                            .show();
//                    if(publishBean.getTopic().equals("settingStandard")){
//
//                        //actionListener.onSetStandardFalse.onSetStandardFalse();
//                    }
                }
            };
            publishHandle.postDelayed(publistask, getResources().getInteger(R.integer.waitPublish));
        } else {
            statusBean = new StatusBean(getResources().getInteger(R.integer.NO_INTERNET),
                    getResources().getString(R.string.noInternet));
            actionListener.onNoInternet.onNoInternet(statusBean.getException());
            notificationSnackBar(getResources().getString(R.string.noInternet));
        }
    }

    private boolean checkFirstOpenApp() {
        Log.i(TAG, "checkFirstOpenApp");
        if (sharedPreferences.getBoolean("first", true)) {
            editor.putBoolean("first", false); //open first app
            editor.putFloat("moisture", (float) 20.00); //persen
            editor.putFloat("temp", (float) 40.00); //°C
            editor.putFloat("light", (float) 5000.00);//Lux
            editor.putInt("dayStorage", 7); //unit day
            editor.putInt("fqPubRawData", 1); // unit minute
            editor.putInt("fqPubImage", 1); // unit hour
            editor.putInt("fqInsertRawData", 1); // unit hour
            editor.putBoolean("autoMode", true);
            editor.commit();
            Intent onBoarding = new Intent(getContextManual(), SetupNETPIEActivity.class);
            startActivity(onBoarding);
            Log.i(TAG, "Is First OpenApp : create onBoarding activity");
            return true;
        } else {
            Log.i(TAG, "Ever open this application");
            return false;

        }
    }

    private boolean checkAndGetKeyNetPie() {
        Log.e(TAG, "checkAndGetKeyNetPie");
        if (!sharedPreferences.getString(appIdTAG, "").equals("")) {
            if (!sharedPreferences.getString(appKeyTAG, "").equals("")) {
                if (!sharedPreferences.getString(appSecretTAG, "").equals("")) {
                    appID = sharedPreferences.getString(appIdTAG, "");
                    appKey = sharedPreferences.getString(appKeyTAG, "");
                    appSecret = sharedPreferences.getString(appSecretTAG, "");
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean isConnectingToInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected()) ? true : false;
    }

    public void connectNETPIE() {
        Log.i(TAG, "connectNETPIE");

        if (checkAndGetKeyNetPie()) {
            Log.e(TAG, "appID: " + appID);
            Log.e(TAG, "appKey: " + appKey);
            Log.e(TAG, "appSecret: " + appSecret);
            if (isConnectingToInternet(getContextManual())) {
                //setNetPieEventListener();
                microgear.setalias("Android");
                microgear.connect(appID, appKey, appSecret);
                microgear.subscribe("rawData");
                microgear.subscribe("response");
                microgear.subscribe("STSlat");
                microgear.subscribe("hasPhoto");
                microgear.subscribe("hasLogList");
                microgear.subscribe("hasRawList");
            } else {
                statusBean = new StatusBean(getResources().getInteger(R.integer.NO_INTERNET),
                        getResources().getString(R.string.noInternet));
                if (actionListener.onNoInternet != null) {
                    actionListener.onNoInternet.onNoInternet(statusBean.getException());
                }

            }
        } else {
            statusBean = new StatusBean(getResources().getInteger(R.integer.ERROR),
                    getResources().getString(R.string.notSetupNETPIE));
            if (actionListener.onException != null) {
                actionListener.onException.onException(statusBean.getException());
            }
        }


    }

    private class SubscribeTask extends AsyncTask<String, Void, String> {
        private final String TAG = "SubscribeTask";
        //private ProgressDialog progressDialog;
        private String dialogMessage;
        private String titleToolbar;

        public SubscribeTask(String dialogMessage) {
            this.dialogMessage = dialogMessage;
        }

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
            //super.onPreExecute();
//            progressDialog = new ProgressDialog(getContextManual());
//            progressDialog.setMessage(dialogMessage);
//            progressDialog.setIndeterminate(false);
//            progressDialog.setCancelable(false);
//            progressDialog.show();
            titleToolbar = getTitle().toString();
            setTitle(titleToolbar + " (" + dialogMessage + ")");

        }

        @Override
        protected String doInBackground(String... params) {
            return new NetPieRestApi(params[0], params[1], params[2]).subscribe(params[3]);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "onPostExecute");
            Log.e(TAG, "result: " + result);

            //super.onPostExecute(result);
//            if (progressDialog != null) {
//                progressDialog.dismiss();
//                progressDialog = null;
//            }
            setTitle(titleToolbar);

            if (!result.equals("connectionLost")
                    && !result.equals("Unauthorized.")
                    && !result.equals("[]")
                    && !result.equals("{\"code\":401,\"message\":\"Unauthorized\"}")) {
                Log.e(TAG, "has result");
                SubscribeBean bean = new SubscribeBean(result);
                Log.e(TAG, "topic:" + bean.getTopic());
                if (bean.getTopic().equals("logDataList")) {
                    logListAsJsonString = bean.getPayload();
                    //statusBean = new StatusBean(getResources().getInteger(R.integer.IS_CONNECT_NETPIE),"");
                    if (actionListener.onUpdateLog != null) {
                        actionListener.onUpdateLog.onUpdateLog(statusBean, logListAsJsonString);
                    }

                }
                if (bean.getTopic().equals("photo")) {
                    //statusBean = new StatusBean(getResources().getInteger(R.integer.IS_CONNECT_NETPIE),"");
                    imageBean = new ImageBean(bean.getPayload());
                    try {
                        actionListener.onUpdateImage.onUpdateImage(statusBean, imageBean);
                    } catch (NullPointerException e) {

                    }

                }
                if (bean.getTopic().equals("rawDataList")) {
                    rawListAsJsonString = bean.getPayload();
                    //statusBean = new StatusBean(getResources().getInteger(R.integer.IS_CONNECT_NETPIE),"");
                    try {
                        actionListener.onUpdateRawList.onUpdateRawList(rawListAsJsonString);
                    } catch (NullPointerException e) {

                    }

                }
            } else {
                Log.e(TAG, "error: " + result);
                if (result.equals("connectionLost")) {
                    statusBean = new StatusBean(getResources().getInteger(R.integer.NO_INTERNET),
                            getResources().getString(R.string.connectionLost));
                    try {
                        actionListener.onNoInternet.onNoInternet(statusBean.getException());
                    } catch (NullPointerException e) {

                    }


                } else {
                    statusBean = new StatusBean(getResources().getInteger(R.integer.ERROR), result);
                    try {
                        actionListener.onException.onException(result);
                    } catch (NullPointerException e) {

                    }
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.e(TAG, "onRequestPermissionsResult");
        Log.e(TAG, "requestCode: " + requestCode);
        Log.e(TAG, "permissions: " + permissions);
        switch (requestCode) {
            case 123:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "Permission Granted");

                    if (actionListener.onPermissionResult != null) {
                        actionListener.onPermissionResult.onPermissionResult(methodUsePermission, true);
                    }
                } else {
                    Log.e(TAG, "Permission Denied");
                    if (actionListener.onPermissionResult != null) {
                        actionListener.onPermissionResult.onPermissionResult(methodUsePermission, false);
                        alertDialog(getResources().getString(R.string.notLoadIm),
                                getResources().getString(R.string.PermissionWRITE_EXTERNALDenied));
                    }

                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void alertDialog(String title, String message) {
        new MaterialDialog.Builder(getContextManual())
                .title(title)
                .content(message)
                .positiveText(getResources().getString(R.string.ok))
                .show();
    }

    public Context getContextManual() {
        return this;
    }

    public void reStartActivity() {
        Intent intent = new Intent(getContextManual(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void notificationSnackBar(String message) {
        Snackbar.make(rootLayout, message, Snackbar.LENGTH_LONG).show();
    }
}

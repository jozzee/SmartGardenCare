package com.sitthiphong.smartgardencare.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.JsonObject;
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
import com.sitthiphong.smartgardencare.bean.ImageBean;
import com.sitthiphong.smartgardencare.bean.PublishBean;
import com.sitthiphong.smartgardencare.bean.RawDataBean;
import com.sitthiphong.smartgardencare.bean.ResponseBean;
import com.sitthiphong.smartgardencare.bean.StatusBean;
import com.sitthiphong.smartgardencare.bean.SubscribeBean;
import com.sitthiphong.smartgardencare.listener.ActionListener;
import com.sitthiphong.smartgardencare.listener.NetworkChangeListener;
import com.sitthiphong.smartgardencare.listener.SubscribeCallBackListener;
import com.sitthiphong.smartgardencare.core.NetPieRestApi;
import com.sitthiphong.smartgardencare.provider.BusProvider;
import com.sitthiphong.smartgardencare.provider.GsonProvider;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    private final int IS_CONNECT_NETPIE = 200;
    private final int ERROR = 199;
    private final int WAIT  = 201;
    private final int REQUEST_CODE_ASK_PERMISSIONSc = 123;


    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private RelativeLayout rootLayout;
    private Toolbar toolbar;
    private BottomBar mBottomBar;
    private ActionListener actionListener = new ActionListener();
    private NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    private StatusBean statusBean = new StatusBean(WAIT,"");
    private RawDataBean rawDataBean;
    private ImageBean imageBean = new ImageBean();
    private ProgressDialog progressDialog;

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
    private String logListAsJsonString;




    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String head = bundle.getString("head");
            if(head.equals("subscribe")){
                Log.i(TAG,"NETPIE Event Listener: onSubscribe");
                String topic = bundle.getString("topic");
                String message = bundle.getString("message");
                Log.i(TAG,"NETPIE Event Listener:    topic: "+topic);

                if(topic.equals("response")){
                    ResponseBean responseBean = new ResponseBean(message);
                    if(progressDialog != null){
                        progressDialog.dismiss();
                    }
                    if(publishBean.getTopic().equals(responseBean.getTopic())){
                        if(responseBean.isSuccess()){
                            if(publishHandle != null){
                                Log.e(TAG,"remove task");
                                publishHandle.removeCallbacks(publistask);
                            }
                            alertDialog("","");
                        }
                        else {
                            Log.e(TAG,responseBean.getMessage());
                            notificationSnackBar(responseBean.getMessage());
                        }
                    }
                    else{
                        // if topic not math
                    }
                }
            }
            else if(head.equals("connect")){
                boolean status = bundle.getBoolean("status");
                if(status == true){

                    Log.i(TAG,"NETPIE Event Listener: onConnect: Connected to NETPIE!!");
                    notificationSnackBar(getApplicationContext().getString(R.string.connectedNETPIE));
                    statusBean = new StatusBean(getResources().getInteger(R.integer.IS_CONNECT_NETPIE),null);
                    if(actionListener.onConnectedToNETPIE != null){
                        actionListener.onConnectedToNETPIE.onConnectedToNETPIE();
                    }
                }
                else{
                    Log.i(TAG,"NETPIE Event Listener: onConnectFalse: Can't connect to NETPIE!!");
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


    @Override
    protected void onCreate(Bundle savedInstanceState) { Log.i(TAG,"onCreate");
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("Details", MODE_PRIVATE);
        editor = sharedPreferences.edit();



        checkFirstOpenApp();
        setContentView(R.layout.activity_main);

        rootLayout = (RelativeLayout)findViewById(R.id.rootLayoutMain);


        setActionListener();
        setNetPieEventListener();
        setNetworkChangeListener();
        connectNETPIE();
        Log.e(TAG,"5555555555555555");

        mBottomBar = BottomBar.attach(this,savedInstanceState);
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

    }

    @Override
    protected void onResume(){
        Log.i(TAG, "onResume");
        super.onResume();
        microgear.bindServiceResume();

    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop");
        super.onStop();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    protected void onRestart(){
        Log.i(TAG, "onRestart");
        super.onRestart();
    }
    @Override
    protected void onDestroy(){
        Log.i(TAG, "onDestroy");
        super.onDestroy();
        if(microgear != null){
            microgear.disconnect();
            Log.w(TAG,"Disconnect Microgear!!");
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
        Log.i(TAG, "onOptionsItemSelected");
        int id = item.getItemId();
        if(id == 16908332){
            onBackPressed();
            return true;
        }
        if(id== R.id.actionSetting){
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
    public void setActionListener(){
        Log.i(TAG, "setActionListener");
        actionListener.setOnFinishSetupNETPIE(new ActionListener.OnFinishSetupNETPIE() {
            @Override
            public void onFinishSetupNETPIE(boolean event) {
                if(event){
                    connectNETPIE();
                }
                else{
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
                if(changeNETPIE && changeDetails){
                    objDetails.addProperty("objNETPIE", GsonProvider.getInstance().toJson(objNETPIE));
                    objSetting = new JsonObject();
                    objSetting.addProperty("settingDetails",GsonProvider.getInstance().toJson(objDetails));
                    publish("setting",GsonProvider.getInstance().toJson(objSetting));
                }
                else if(changeNETPIE){
                    if(objNETPIE.get("appID")!=null){
                        editor.putString("appID",objNETPIE.get("appID").getAsString());
                    }
                    if(objNETPIE.get("appKey")!=null){
                        editor.putString("appKey",objNETPIE.get("appKey").getAsString());
                    }
                    if(objNETPIE.get("appSecret")!=null){
                        editor.putString("appSecret",objNETPIE.get("appSecret").getAsString());
                    }
                    editor.commit();
                    reStartActivity();
                }
                else if(changeDetails){
                    objSetting = new JsonObject();
                    objSetting.addProperty("settingDetails",GsonProvider.getInstance().toJson(objDetails));
                    publish("setting",GsonProvider.getInstance().toJson(objSetting));
                }
            }
        });
        actionListener.setOnRequestUpdateImage(new ActionListener.OnRequestUpdateImage() {
            @Override
            public void onRequestUpdateImage() {
                if(statusBean.getStatus()==getResources().getInteger(R.integer.IS_CONNECT_NETPIE)){
                    if(imageBean.getTimeStamp()>0l){
                        actionListener.onUpdateImage.onUpdateImage(statusBean,imageBean);
                    }
                    else {
                        //load
                        new SubscribeTask(getResources().getString(R.string.fetching))
                                .execute(appID = sharedPreferences.getString("appID",""),
                                        appKey = sharedPreferences.getString("appKey",""),
                                        sharedPreferences.getString("appSecret",""),
                                        "photo");
                    }
                }
                else {
                    actionListener.onException.onException(statusBean.getException());
                }

            }
        });
        actionListener.setOnRequestRawData(new ActionListener.OnRequestRawData() {
            @Override
            public void OnRequestRawData() {
                actionListener.onUpdateRawData.OnUpdateRawDat(statusBean,rawDataBean);
            }
        });

        actionListener.setOnRequestLog(new ActionListener.OnRequestLog() {
            @Override
            public void onRequestLog() {
               if(statusBean.getStatus() == getResources().getInteger(R.integer.IS_CONNECT_NETPIE)){
                   if(logListAsJsonString != null){
                       actionListener.onUpdateLog.onUpdateLog(statusBean,logListAsJsonString);
                   }
                   else{
                       //load
                       new SubscribeTask(getResources().getString(R.string.fetching))
                               .execute(appID = sharedPreferences.getString("appID",""),
                                        appKey = sharedPreferences.getString("appKey",""),
                                        sharedPreferences.getString("appSecret",""),
                                        "logDataList");
                   }
               }
                else{
                   actionListener.onException.onException(statusBean.getException());
               }

            }
        });

    }
    public void setNetPieEventListener(){
        Log.e(TAG,"setNetPieEventListener");
        eventListener.setConnectEventListener(new EventListener.OnServiceConnect() {
            @Override
            public void onConnect(Boolean status) {
                Message msg = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("head", "connect");
                bundle.putBoolean("status",status);
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
                Log.e(TAG,"NETPIE Event Listener: onMessage");
                Log.e(TAG,"NETPIE Event Listener:    topic: "+topic);
                Log.e(TAG,"NETPIE Event Listener:    message: "+message);
                Message msg = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("head", "subscribe");
                bundle.putString("topic", topic);
                bundle.putString("message",message);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        });

        eventListener.setPresentEventListener(new EventListener.OnPresent() {
            @Override
            public void onPresent(String name) {
                Log.e(TAG,"NETPIE Event Listener: onPresent: "+name);
            }
        });

        eventListener.setAbsentEventListener(new EventListener.OnAbsent() {
            @Override
            public void onAbsent(String name) {
                Log.e(TAG,"NETPIE Event Listener: onAbsent: "+name);
            }
        });

        eventListener.setDisconnectEventListener(new EventListener.OnClose() {
            @Override
            public void onDisconnect(Boolean status) {
                Log.e(TAG,"NETPIE Event Listener: onDisconnect: "+String.valueOf(status));
            }
        });

        eventListener.setOnException(new EventListener.OnException() {
            @Override
            public void onException(String error) {
                Log.e(TAG,"NETPIE Event Listener: onException: "+error);
                statusBean = new StatusBean(getResources().getInteger(R.integer.ERROR),error);
                actionListener.onException.onException(error);

            }
        });

    }
    private void setFragment(int menuItemId){
        Log.e(TAG,"setFragment");
        if(menuItemId == R.id.bottomBarImage){
            imFragment = ImageFragment.newInstance();
            clearFragment();
            supportFragmentManager.beginTransaction().replace(
                    R.id.fragmentContain,
                    imFragment,
                    getApplicationContext().getString(R.string.image)
            ).commit();
            setTitle(R.string.image);
            this.menuItemId = R.id.bottomBarImage;
        }
        else if(menuItemId == R.id.bottomBarMoisture){
            moistureFragment = MoistureFragment.newInstance().newInstance();
            clearFragment();
            supportFragmentManager.beginTransaction().replace(
                    R.id.fragmentContain,
                    moistureFragment,
                    getApplicationContext().getString(R.string.moisture)
            ).commit();
            setTitle(R.string.moisture);
            this.menuItemId = R.id.bottomBarMoisture;
        }
        else if(menuItemId == R.id.bottomBarTemp){
            tempFragment = TempFragment.newInstance();
            clearFragment();
            supportFragmentManager.beginTransaction().replace(
                    R.id.fragmentContain,
                    tempFragment,
                    getApplicationContext().getString(R.string.temp)
            ).commit();
            setTitle(R.string.temp);
            this.menuItemId = R.id.bottomBarTemp;
        }
        else if(menuItemId == R.id.bottomBarLight){
            lightFragment = LightFragment.newInstance();
            clearFragment();
            supportFragmentManager.beginTransaction().replace(
                    R.id.fragmentContain,
                    lightFragment,
                    getApplicationContext().getString(R.string.light)
            ).commit();
            setTitle(R.string.light);
            this.menuItemId = R.id.bottomBarLight;
        }
        else if(menuItemId == R.id.bottomBarLog){
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
    private void clearFragment(){
        fragment = supportFragmentManager.findFragmentByTag(getApplicationContext().getString(R.string.image));
        if(fragment != null){
            supportFragmentManager.beginTransaction().remove(fragment).commit();
        }
        fragment = supportFragmentManager.findFragmentByTag(getApplicationContext().getString(R.string.moisture));
        if(fragment != null){
            supportFragmentManager.beginTransaction().remove(fragment).commit();
        }
        fragment = supportFragmentManager.findFragmentByTag(getApplicationContext().getString(R.string.temp));
        if(fragment != null){
            supportFragmentManager.beginTransaction().remove(fragment).commit();
        }
        fragment = supportFragmentManager.findFragmentByTag(getApplicationContext().getString(R.string.light));
        if(fragment != null){
            supportFragmentManager.beginTransaction().remove(fragment).commit();
        }
        fragment = supportFragmentManager.findFragmentByTag(getApplicationContext().getString(R.string.log));
        if(fragment != null){
            supportFragmentManager.beginTransaction().remove(fragment).commit();
        }

    }
    public void setNetworkChangeListener(){
        networkChangeListener.setNetworkChange(new NetworkChangeListener.OnNetworkChange() {
            @Override
            public void onNetworkChange(boolean isConnect) {
                if(isConnect){
                    notificationSnackBar("is Connect Internet");

                }
                else{
                    //notificationSnackBar("no Connect Internet");
                }

            }
        });
    }

    public void publish(String topic,String payload){
        Log.e(TAG,"publish");
        Log.e(TAG,"  topic: "+topic);
        Log.e(TAG,"  payload: "+payload);

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
        if(isConnectingToInternet(getContextManual())){
            progressDialog = new ProgressDialog(getContextManual());
            progressDialog.setMessage("Publish "+topic +" topic...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
            publishBean = new PublishBean(topic,payload);
            microgear.publish(topic,payload);
            publishHandle = new Handler();
            publistask = new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "run task");
                    if(progressDialog != null){
                        progressDialog.dismiss();
                    }
                    new MaterialDialog.Builder(getContextManual())
                            .title(getResources().getString(R.string.exception))
                            .content(getResources().getString(R.string.piNotResponse))
                            .positiveText(getResources().getString(R.string.ok))
                            .show();
                }
            };
            publishHandle.postDelayed(publistask, getResources().getInteger(R.integer.waitPublish));
        }else{
            statusBean = new StatusBean(getResources().getInteger(R.integer.NO_INTERNET),
                    getResources().getString(R.string.noInternet));
            actionListener.onNoInternet.onNoInternet();
            notificationSnackBar(getResources().getString(R.string.noInternet));
        }
    }
    private boolean checkFirstOpenApp(){Log.i(TAG,"checkFirstOpenApp");
        if(sharedPreferences.getBoolean("first",true)){
            editor.putBoolean("first",false); //open first app
            editor.putBoolean("autoFaucet",true);
            editor.putFloat("humidity", (float) 20.00); //persen
            editor.putBoolean("autoShower",true);
            editor.putFloat("temp", (float) 40.00); //°C
            editor.putBoolean("autoSlat",true);
            editor.putFloat("light", (float) 5000.00);//Lux
            editor.putInt("dayStore",7); //unit day
            editor.putInt("ftPubRD",1); // unit minute
            editor.putInt("ftPubIM",1); // unit hour
            editor.putInt("ftIRD",1); // unit hour
            editor.commit();
            Intent onBoarding = new Intent(getContextManual(), SetupNETPIEActivity.class);
            startActivity(onBoarding);
            Log.i(TAG,"Is First OpenApp : create onBoarding activity");
            return true;
        }
        else{
            Log.i(TAG,"Ever open this application");
            return false;

        }
    }
    private boolean checkAndGetKeyNetPie(){
        Log.e(TAG,"checkAndGetKeyNetPie");
        if(!sharedPreferences.getString("appID","").equals("")){
            if(!sharedPreferences.getString("appKey","").equals("")){
                if(!sharedPreferences.getString("appSecret","").equals("")){
                    appID = sharedPreferences.getString("appID","");
                    appKey = sharedPreferences.getString("appKey","");
                    appSecret = sharedPreferences.getString("appSecret","");
                    return true;
                }
                else{
                    return false;
                }
            }
            else{
                return false;
            }
        }
        else{
            return false;
        }
    }
    public boolean isConnectingToInternet(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected())?true:false;
    }
    public void connectNETPIE(){
        Log.i(TAG,"connectNETPIE");

        if(checkAndGetKeyNetPie()){
            Log.e(TAG,"appID: "+appID);
            Log.e(TAG,"appKey: "+appKey);
            Log.e(TAG,"appSecret: "+appSecret);
            if(isConnectingToInternet(getContextManual())){
                //setNetPieEventListener();
                microgear.setalias("Android");
                microgear.connect(appID,appKey,appSecret);
                microgear.subscribe("rawData");
                microgear.subscribe("response");
                microgear.subscribe("STSlat");
                microgear.subscribe("hasPhoto");
                microgear.subscribe("hasLog");
            }
            else{
                statusBean = new StatusBean(getResources().getInteger(R.integer.NO_INTERNET),
                                            getResources().getString(R.string.noInternet));
                actionListener.onNoInternet.onNoInternet();
            }
        }
        else{
            statusBean = new StatusBean(getResources().getInteger(R.integer.ERROR),
                                        getResources().getString(R.string.notSetupNETPIE));
            actionListener.onException.onException(statusBean.getException());
        }


    }


    private class GetRawListTask extends AsyncTask<String,Void,String> {
        private final String TAG = "GetRawListTask";
        private ProgressDialog progressDialog;
        String message;
        public GetRawListTask(String message) {
            this.message = message;
        }

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
            //super.onPreExecute();
            progressDialog = new ProgressDialog(getContextManual());
            progressDialog.setMessage(message);
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        @Override
        protected String doInBackground(String... params) {
            return new NetPieRestApi(params[0],params[1],params[2]).subscribe("rawDataList");
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "onPostExecute");
            //super.onPostExecute(result);
            if(progressDialog != null){
                progressDialog.dismiss();
                progressDialog = null;
            }
            new SubscribeCallBackListener()
                    .onSubscribeRawDataListCallBackListener
                    .onSubRawDataListCallBackListener(new SubscribeBean(result));
        }
    }
    private class SubscribeTask extends AsyncTask<String,Void,String> {
        private final String TAG = "SubscribeTask";
        private ProgressDialog progressDialog;
        private String dialogMessage;

        public SubscribeTask(String dialogMessage) {
            this.dialogMessage = dialogMessage;
        }

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
            //super.onPreExecute();
            progressDialog = new ProgressDialog(getContextManual());
            progressDialog.setMessage(dialogMessage);
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        @Override
        protected String doInBackground(String... params) {
            return new NetPieRestApi(params[0],params[1],params[2]).subscribe(params[3]);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "onPostExecute");
            Log.e(TAG,"result: "+result);

            //super.onPostExecute(result);
            if(progressDialog != null){
                progressDialog.dismiss();
                progressDialog = null;
            }

            if(!result.equals("connectionLost")
                    && !result.equals("Unauthorized.")
                    && !result.equals("[]")
                    && !result.equals("{\"code\":401,\"message\":\"Unauthorized\"}")){
                Log.e(TAG,"has result");
                SubscribeBean bean = new SubscribeBean(result);
                Log.e(TAG,"topic:"+bean.getTopic());
                if(bean.getTopic().equals("logDataList")){
                    logListAsJsonString = bean.getPayload();
                    //statusBean = new StatusBean(getResources().getInteger(R.integer.IS_CONNECT_NETPIE),"");
                    actionListener.onUpdateLog.onUpdateLog(statusBean,logListAsJsonString);
                }
                if(bean.getTopic().equals("photo")){
                    //statusBean = new StatusBean(getResources().getInteger(R.integer.IS_CONNECT_NETPIE),"");
                    imageBean = new ImageBean(bean.getPayload());
                    actionListener.onUpdateImage.onUpdateImage(statusBean,imageBean);
                }
            }
            else {
                Log.e(TAG,"error: "+result);
                statusBean = new StatusBean(getResources().getInteger(R.integer.ERROR),result);
                actionListener.onException.onException(result);
            }
        }
    }
    public void alertDialog(String title,String message){
        new MaterialDialog.Builder(getContextManual())
                .title(title)
                .content(message)
                .positiveText(getResources().getString(R.string.ok))
                .show();
    }
    public Context getContextManual(){
        return this;
    }
    public void reStartActivity(){
        Intent intent = new Intent(getContextManual(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    public void notificationSnackBar(String message){
        Snackbar.make(rootLayout, message, Snackbar.LENGTH_LONG).show();
    }
}

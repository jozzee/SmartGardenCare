package com.sitthiphong.smartgardencare.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.JsonObject;
import com.sitthiphong.smartgardencare.R;
import com.sitthiphong.smartgardencare.bean.NetPieBean;
import com.sitthiphong.smartgardencare.listener.ActionListener;
import com.sitthiphong.smartgardencare.provider.BusProvider;
import com.sitthiphong.smartgardencare.provider.event.OnSaveSettingEvent;

public class SettingActivity extends AppCompatActivity {
    private final String TAG = "SettingActivity";
    private CoordinatorLayout rootLayout;
    private Toolbar toolbar;
    private Menu menu;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private RelativeLayout layoutAppID;
    private RelativeLayout layoutAppKey;
    private RelativeLayout layoutAppSecret;
    private RelativeLayout layoutSendDataSensor;
    private RelativeLayout layoutInsertDatasensor;
    private RelativeLayout layoutStorageData;
    private TextView textViewAppID;
    private TextView textViewAppKey;
    private TextView textViewAppSecret;
    private TextView sendDataValue;
    private TextView insertDataValue;
    private TextView storageDataValue;
    private int ftPubRD;
    private int ftIRD;
    private int dayStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusProvider.getInstance().register(this);

        sharedPreferences = getSharedPreferences("Details", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        setContentView(R.layout.activity_setting);

        rootLayout = (CoordinatorLayout) findViewById(R.id.rootLayoutSetting);  //set RootLayout
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.setting);
//        toolbar = (Toolbar) findViewById(R.id.toolbarSetting); //Set Toolbar replace Actionbar
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setHomeButtonEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle(R.string.setting);

        ftPubRD = sharedPreferences.getInt("ftPubRD",1);
        ftIRD = sharedPreferences.getInt("ftIRD",1);
        dayStore = sharedPreferences.getInt("dayStore",7);

        layoutAppID = (RelativeLayout) findViewById(R.id.layoutSetAppId);
        layoutAppKey = (RelativeLayout)findViewById(R.id.layoutSetAppKey);
        layoutAppSecret = (RelativeLayout)findViewById(R.id.layoutSetAppSecret);
        layoutSendDataSensor = (RelativeLayout)findViewById(R.id.layoutSetSendData);
        layoutInsertDatasensor = (RelativeLayout)findViewById(R.id.layoutSetInsertData);
        layoutStorageData = (RelativeLayout)findViewById(R.id.layoutSetStorageData);


        textViewAppID = (TextView)findViewById(R.id.appIdValue);
        textViewAppKey =(TextView)findViewById(R.id.appKeyValue);
        textViewAppSecret = (TextView)findViewById(R.id.appSecretValue);
        sendDataValue = (TextView)findViewById(R.id.sendDataValue);
        insertDataValue = (TextView)findViewById(R.id.insertDataValue);
        storageDataValue = (TextView)findViewById(R.id.storageDataValue);


        textViewAppID.setText(sharedPreferences.getString("appID","no directory"));
        textViewAppKey.setText(sharedPreferences.getString("appKey","no directory"));
        textViewAppSecret.setText(sharedPreferences.getString("appSecret","no directory"));
        sendDataValue.setText(
                getResources().getString(R.string.every)+" "+
                ftPubRD+" "+
                getResources().getString(R.string.minute));
        insertDataValue.setText(
                getResources().getString(R.string.every)+" "+
                ftIRD+" "+
                getResources().getString(R.string.hour));
        storageDataValue.setText(
                dayStore+" "+
                getResources().getString(R.string.day));

        layoutAppID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(getContextManual())
                        .title(getResources().getString(R.string.editAppId))
                        //.content(R.string.input_content)
                        .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT)
                        .input(getResources().getString(R.string.textHintAppId),textViewAppID.getText().toString().trim(), new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                // Do something
                                Log.e(TAG,"Dialog Callback input App ID: "+input.toString());
                                if(!(input.toString().equals(sharedPreferences.getString("appID","no directory")))){
                                    textViewAppID.setText(input.toString());
                                    menu.findItem(R.id.actionSaveSetting).setVisible(true);
                                }else{
                                    if(textViewAppKey.getText().toString().trim()
                                            .equals(sharedPreferences.getString("appKey","no directory"))
                                            && textViewAppSecret.getText().toString().trim()
                                            .equals(sharedPreferences.getString("appSecret","no directory"))
                                            && ftPubRD == (sharedPreferences.getInt("ftPubRD",1))
                                            && ftIRD == (sharedPreferences.getInt("ftIRD",1))
                                            && dayStore == (sharedPreferences.getInt("dayStore",7))){
                                        menu.findItem(R.id.actionSaveSetting).setVisible(false);
                                        textViewAppID.setText(input);
                                    }
                                }
                            }
                        }).show();
            }
        });
        layoutAppKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(getContextManual())
                        .title(getResources().getString(R.string.editAppKey))
                        //.content(R.string.input_content)
                        .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT)
                        .input(getResources().getString(R.string.textHintAppKey),textViewAppKey.getText().toString().trim(), new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                // Do something
                                Log.e(TAG,"Dialog Callback input App key: "+input.toString());
                                if(!(input.toString().equals(sharedPreferences.getString("appKey","no directory")))){
                                    textViewAppKey.setText(input.toString());
                                    menu.findItem(R.id.actionSaveSetting).setVisible(true);
                                }else{
                                    if(textViewAppID.getText().toString().trim()
                                            .equals(sharedPreferences.getString("appID","no directory"))
                                            && textViewAppSecret.getText().toString().trim()
                                            .equals(sharedPreferences.getString("appSecret","no directory"))
                                            && ftPubRD == (sharedPreferences.getInt("ftPubRD",1))
                                            && ftIRD == (sharedPreferences.getInt("ftIRD",1))
                                            && dayStore == (sharedPreferences.getInt("dayStore",7))){
                                        menu.findItem(R.id.actionSaveSetting).setVisible(false);
                                        textViewAppKey.setText(input);
                                    }
                                }
                            }
                        }).show();
            }
        });
        layoutAppSecret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(getContextManual())
                        .title(getResources().getString(R.string.editAppSecret))
                        //.content(R.string.input_content)
                        .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT)
                        .input(getResources().getString(R.string.textHintAppSecret)
                                ,textViewAppSecret.getText().toString().trim(),
                                new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                // Do something
                                Log.e(TAG,"Dialog Callback input App Secret: "+input.toString());
                                if(!(input.toString().equals(sharedPreferences.getString("appSecret","no directory")))){
                                    textViewAppSecret.setText(input.toString());
                                    menu.findItem(R.id.actionSaveSetting).setVisible(true);
                                }else{
                                    if(textViewAppKey.getText().toString().trim()
                                            .equals(sharedPreferences.getString("appKey","no directory"))
                                            && textViewAppSecret.getText().toString().trim()
                                            .equals(sharedPreferences.getString("appSecret","no directory"))
                                            && ftPubRD == (sharedPreferences.getInt("ftPubRD",1))
                                            && ftIRD == (sharedPreferences.getInt("ftIRD",1))
                                            && dayStore == (sharedPreferences.getInt("dayStore",7))){
                                        menu.findItem(R.id.actionSaveSetting).setVisible(false);
                                        textViewAppSecret.setText(input);
                                    }
                                }
                            }
                        }).show();
            }
        });
        layoutSendDataSensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(getContextManual())
                        .title(getResources().getString(R.string.frequencySendData))
                        .items(R.array.ftPubRDArray)
                        .itemsCallbackSingleChoice(getWhichFromSendData(ftPubRD),
                                new MaterialDialog.ListCallbackSingleChoice() {
                                    @Override
                                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                        int value = getValueSendDataFromWhich(which);

                                        if(value != (sharedPreferences.getInt("ftPubRD",1))){
                                            ftPubRD = value;
                                            sendDataValue.setText(text);
                                            menu.findItem(R.id.actionSaveSetting).setVisible(true);
                                        }
                                        else{
                                            if(textViewAppID.getText().toString().trim()
                                                    .equals(sharedPreferences.getString("appID","no directory"))
                                                    && textViewAppKey.getText().toString().trim()
                                                    .equals(sharedPreferences.getString("appKey","no directory"))
                                                    && textViewAppSecret.getText().toString().trim()
                                                    .equals(sharedPreferences.getString("appSecret","no directory"))
                                                    //&& ftPubRD == (sharedPreferences.getInt("ftPubRD",1))
                                                    && ftIRD == (sharedPreferences.getInt("ftIRD",1))
                                                    && dayStore == (sharedPreferences.getInt("dayStore",7))){
                                                menu.findItem(R.id.actionSaveSetting).setVisible(false);
                                            }
                                            ftPubRD = sharedPreferences.getInt("ftPubRD",1);
                                            sendDataValue.setText(text);
                                        }
                                        return true;
                                    }
                                })
                        .positiveText(R.string.choose)
                        .negativeText(R.string.cancel)
                        .show();
            }
        });
        layoutInsertDatasensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(getContextManual())
                        .title(getResources().getString(R.string.frequencyInsertData))
                        .items(R.array.ftPubIM_IRDArray)
                        .itemsCallbackSingleChoice(getWhichFromInsertDataValue(ftIRD),
                                new MaterialDialog.ListCallbackSingleChoice() {
                                    @Override
                                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                        int value = getValueInsertDataValueFromWhich(which);

                                        if(value != (sharedPreferences.getInt("ftIRD",1))){
                                            ftIRD = value;
                                            insertDataValue.setText(text);
                                            menu.findItem(R.id.actionSaveSetting).setVisible(true);
                                        }
                                        else{
                                            if(textViewAppID.getText().toString().trim()
                                                    .equals(sharedPreferences.getString("appID","no directory"))
                                                    && textViewAppKey.getText().toString().trim()
                                                    .equals(sharedPreferences.getString("appKey","no directory"))
                                                    && textViewAppSecret.getText().toString().trim()
                                                    .equals(sharedPreferences.getString("appSecret","no directory"))
                                                    && ftPubRD == (sharedPreferences.getInt("ftPubRD",1))
                                                    //&& ftIRD == (sharedPreferences.getInt("ftIRD",1))
                                                    && dayStore == (sharedPreferences.getInt("dayStore",7))){
                                                menu.findItem(R.id.actionSaveSetting).setVisible(false);
                                            }
                                            ftIRD = sharedPreferences.getInt("ftIRD",1);
                                            sendDataValue.setText(text);
                                        }
                                        return true;
                                    }
                                })
                        .positiveText(R.string.choose)
                        .negativeText(R.string.cancel)
                        .show();
            }
        });
        layoutStorageData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(getContextManual())
                        .title(getResources().getString(R.string.dayStorageData))
                        .items(R.array.dayOfStorageArray)
                        .itemsCallbackSingleChoice(getWhichFromStorageDataValue(dayStore),
                                new MaterialDialog.ListCallbackSingleChoice() {
                                    @Override
                                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                        int value = getValueStorageDataValueFromWhich(which);

                                        if(value != (sharedPreferences.getInt("dayStore",1))){
                                            dayStore = value;
                                            storageDataValue.setText(text);
                                            menu.findItem(R.id.actionSaveSetting).setVisible(true);
                                        }
                                        else{
                                            if(textViewAppID.getText().toString().trim()
                                                    .equals(sharedPreferences.getString("appID","no directory"))
                                                    && textViewAppKey.getText().toString().trim()
                                                    .equals(sharedPreferences.getString("appKey","no directory"))
                                                    && textViewAppSecret.getText().toString().trim()
                                                    .equals(sharedPreferences.getString("appSecret","no directory"))
                                                    && ftPubRD == (sharedPreferences.getInt("ftPubRD",1))
                                                    && ftIRD == (sharedPreferences.getInt("ftIRD",1))){
                                                    //&& dayStore == (sharedPreferences.getInt("dayStore",7))){
                                                menu.findItem(R.id.actionSaveSetting).setVisible(false);
                                            }
                                            dayStore = sharedPreferences.getInt("dayStore",1);
                                            storageDataValue.setText(text);
                                        }
                                        return true;
                                    }
                                })
                        .positiveText(R.string.choose)
                        .negativeText(R.string.cancel)
                        .show();
            }
        });

    }
    @Override
    protected void onStart() {
        Log.i(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onResume(){
        Log.i(TAG, "onResume");
        super.onResume();

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
        BusProvider.getInstance().unregister(this);
    }
    @Override
    public void onBackPressed() {
        Log.i(TAG, "onBackPressed");
        super.onBackPressed();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "onCreateOptionsMenu");
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_done_setting, menu);
        menu.findItem(R.id.actionSaveSetting).setVisible(false);
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
        if(id== R.id.actionSaveSetting){
            Log.e(TAG,"onSaveSetting");
            validateChange();
            //new ActionListener().mOnReStartActivity.onReStartActivity();
            finish();
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
    public Context getContextManual(){
        return this;
    }
    public void validateChange(){
        Log.e(TAG,"validateChange");
        boolean changeNETPIE = false;
        boolean changeDetails = false;
        JsonObject objDetails = new JsonObject();
        JsonObject objNETPIE = new JsonObject();
        if(!textViewAppID.getText().toString().trim()
                .equals(sharedPreferences.getString("appID","no directory"))){
            //editor.putString("appID",textViewAppID.getText().toString().trim());
            objNETPIE.addProperty("appID",textViewAppID.getText().toString().trim());
            changeNETPIE = true;
        }
        if(!textViewAppKey.getText().toString().trim()
                .equals(sharedPreferences.getString("appKey","no directory"))){
            //editor.putString("appKey",textViewAppKey.getText().toString().trim());
            objNETPIE.addProperty("appKey",textViewAppKey.getText().toString().trim());
            changeNETPIE = true;
        }
        if(!textViewAppSecret.getText().toString().trim()
                .equals(sharedPreferences.getString("appSecret","no directory"))){
            //editor.putString("appSecret",textViewAppSecret.getText().toString().trim());
            objNETPIE.addProperty("appSecret",textViewAppSecret.getText().toString().trim());
            changeNETPIE = true;
        }
        if(ftPubRD != sharedPreferences.getInt("ftPubRD",1)){
            objDetails.addProperty("ftPubRD",ftPubRD);
            changeDetails = true;
        }
        if(ftIRD != sharedPreferences.getInt("ftIRD",1)){
            objDetails.addProperty("ftIRD",ftIRD);
            changeDetails = true;
        }
        if(dayStore != sharedPreferences.getInt("dayStore",1)){
            objDetails.addProperty("dayOfStorage",dayStore);
            changeDetails =true;
        }
        new ActionListener().onSaveSetting.onSaveSetting(
                changeNETPIE,
                changeDetails,
                objNETPIE,
                objDetails
        );
    }
    public int getValueSendDataFromWhich(int which){
        if(which == 0){
            return 1;
        }
        else if(which == 1){
            return 5;
        }
        else if(which == 2){
            return 10;
        }
        else if(which == 3){
            return 20;
        }
        else if(which == 4){
            return 30;
        }
        else if(which == 5){
            return 60;
        }else {
            return 1;
        }

    }
    public int getWhichFromSendData(int sendData){
        if(sendData == 1){
            return 0;
        }
        else if(sendData == 5){
            return 1;
        }
        else if(sendData == 10){
            return 2;
        }
        else if(sendData == 20){
            return 3;
        }
        else if(sendData == 30){
            return 4;
        }
        else if(sendData == 60){
            return 5;
        }else {
            return 0;
        }
    }
    public int getValueInsertDataValueFromWhich(int which){
        if(which == 0){
            return 1;
        }
        else if(which == 1){
            return 2;
        }
        else if(which == 2){
            return 3;
        }
        else if(which == 3){
            return 6;
        }
        else if(which == 4){
            return 12;
        }
        else if(which == 5){
            return 24;
        }else {
            return 1;
        }
    }
    public int getWhichFromInsertDataValue(int insertDataValue){
        if(insertDataValue == 1){
            return 0;
        }
        else if(insertDataValue == 2){
            return 1;
        }
        else if(insertDataValue == 3){
            return 2;
        }
        else if(insertDataValue == 6){
            return 3;
        }
        else if(insertDataValue == 12){
            return 4;
        }
        else if(insertDataValue == 24){
            return 5;
        }else {
            return 0;
        }
    }
    public int getValueStorageDataValueFromWhich(int which){
        if(which == 0){
            return 1;
        }
        else if(which == 1){
            return 2;
        }
        else if(which == 2){
            return 3;
        }
        else if(which == 3){
            return 4;
        }
        else if(which == 4){
            return 5;
        }
        else if(which == 5){
            return 6;
        }
        else if(which == 6){
            return 7;
        }
        else {
            return 1;
        }
    }
    public int getWhichFromStorageDataValue(int storageDataValue){
        if(storageDataValue == 1){
            return 0;
        }
        else if(storageDataValue == 2){
            return 1;
        }
        else if(storageDataValue == 3){
            return 2;
        }
        else if(storageDataValue == 4){
            return 3;
        }
        else if(storageDataValue == 5){
            return 4;
        }
        else if(storageDataValue == 6){
            return 5;
        }
        else if(storageDataValue == 7){
            return 6;
        }
        else {
            return 0;
        }
    }

}

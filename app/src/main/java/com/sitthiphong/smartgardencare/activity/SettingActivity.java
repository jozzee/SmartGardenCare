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
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
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
    private RelativeLayout layoutInsertDataSensor;
    private RelativeLayout layoutStorageData;
    private TextView textViewAppID;
    private TextView textViewAppKey;
    private TextView textViewAppSecret;
    private TextView sendDataValue;
    private TextView insertDataValue;
    private TextView storageDataValue;
    private TextView autoMote;
    private Switch swAutoMode;
    private int fqPubRawData;
    private int fqInsertRawData;
    private int dayStorage;

    private final String dayStorageTAG ="dayStorage";
    private final String fqPubRawDataTAG ="fqPubRawData";
    private final String fqPubImageTAG ="fqPubImage";  //ไปตั้งค่าอยู่หน้า image fragment
    private final String fqInsertRawDataTAG = "fqInsertRawData";
    private final String appIdTAG = "appId";
    private final String appKeyTAG = "appKey";
    private final String appSecretTAG ="appSecret";
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

        fqPubRawData = sharedPreferences.getInt(fqPubRawDataTAG,1);
        fqInsertRawData = sharedPreferences.getInt(fqInsertRawDataTAG,1);
        dayStorage = sharedPreferences.getInt(dayStorageTAG,7);

        layoutAppID = (RelativeLayout) findViewById(R.id.layoutSetAppId);
        layoutAppKey = (RelativeLayout)findViewById(R.id.layoutSetAppKey);
        layoutAppSecret = (RelativeLayout)findViewById(R.id.layoutSetAppSecret);
        layoutSendDataSensor = (RelativeLayout)findViewById(R.id.layoutSetSendData);
        layoutInsertDataSensor = (RelativeLayout)findViewById(R.id.layoutSetInsertData);
        layoutStorageData = (RelativeLayout)findViewById(R.id.layoutSetStorageData);


        textViewAppID = (TextView)findViewById(R.id.appIdValue);
        textViewAppKey =(TextView)findViewById(R.id.appKeyValue);
        textViewAppSecret = (TextView)findViewById(R.id.appSecretValue);
        sendDataValue = (TextView)findViewById(R.id.sendDataValue);
        insertDataValue = (TextView)findViewById(R.id.insertDataValue);
        storageDataValue = (TextView)findViewById(R.id.storageDataValue);

        autoMote = (TextView)findViewById(R.id.titleAutoMote);
        swAutoMode = (Switch)findViewById(R.id.swAutoMode);

        swAutoMode.setChecked(sharedPreferences.getBoolean("autoMode",false));
        if(swAutoMode.isChecked()){
            autoMote.setText(getResources().getString(R.string.autoMode)+" "+
                    getResources().getString(R.string.open));
        }
        else{
            autoMote.setText(getResources().getString(R.string.autoMode)+" "+
                    getResources().getString(R.string.close));
        }


        textViewAppID.setText(sharedPreferences.getString(appIdTAG,"no directory"));
        textViewAppKey.setText(sharedPreferences.getString(appKeyTAG,"no directory"));
        textViewAppSecret.setText(sharedPreferences.getString(appSecretTAG,"no directory"));
        sendDataValue.setText(
                getResources().getString(R.string.every)+" "+ fqPubRawData+" "+
                getResources().getString(R.string.minute));
        insertDataValue.setText(
                getResources().getString(R.string.every)+" "+
                        fqInsertRawData+" "+
                getResources().getString(R.string.hour));
        storageDataValue.setText(
                dayStorage+" "+
                getResources().getString(R.string.day));

        swAutoMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    autoMote.setText(getResources().getString(R.string.autoMode)+" "+
                            getResources().getString(R.string.open));
                }
                else {
                    autoMote.setText(getResources().getString(R.string.autoMode)+" "+
                            getResources().getString(R.string.close));
                }
                if(b!=sharedPreferences.getBoolean("autoMode",false)){
                    menu.findItem(R.id.actionSaveSetting).setVisible(true);
                }else{
                    if(textViewAppID.getText().toString().trim().equals(sharedPreferences.getString(appIdTAG,"no directory")) &&
                            textViewAppKey.getText().toString().trim()
                            .equals(sharedPreferences.getString(appKeyTAG,"no directory"))
                            && textViewAppSecret.getText().toString().trim()
                            .equals(sharedPreferences.getString(appSecretTAG,"no directory"))
                            && fqPubRawData == (sharedPreferences.getInt(fqPubRawDataTAG,1))
                            && fqInsertRawData == (sharedPreferences.getInt(fqInsertRawDataTAG,1))
                            && dayStorage == (sharedPreferences.getInt(dayStorageTAG,7))){
                        menu.findItem(R.id.actionSaveSetting).setVisible(false);
                        if(b){
                            autoMote.setText(getResources().getString(R.string.autoMode)+" "+
                                    getResources().getString(R.string.open));
                        }
                        else {
                            autoMote.setText(getResources().getString(R.string.autoMode)+" "+
                                    getResources().getString(R.string.close));
                        }
                    }
                }
            }
        });

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
                                if(!(input.toString().equals(sharedPreferences.getString(appIdTAG,"no directory")))){
                                    textViewAppID.setText(input.toString());
                                    menu.findItem(R.id.actionSaveSetting).setVisible(true);
                                }else{
                                    if(textViewAppKey.getText().toString().trim()
                                            .equals(sharedPreferences.getString(appKeyTAG,"no directory"))
                                            && textViewAppSecret.getText().toString().trim()
                                            .equals(sharedPreferences.getString(appSecretTAG,"no directory"))
                                            && fqPubRawData == (sharedPreferences.getInt(fqPubRawDataTAG,1))
                                            && fqInsertRawData == (sharedPreferences.getInt(fqInsertRawDataTAG,1))
                                            && dayStorage == (sharedPreferences.getInt(dayStorageTAG,7))){
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
                                if(!(input.toString().equals(sharedPreferences.getString(appKeyTAG,"no directory")))){
                                    textViewAppKey.setText(input.toString());
                                    menu.findItem(R.id.actionSaveSetting).setVisible(true);
                                }else{
                                    if(textViewAppID.getText().toString().trim()
                                            .equals(sharedPreferences.getString(appIdTAG,"no directory"))
                                            && textViewAppSecret.getText().toString().trim()
                                            .equals(sharedPreferences.getString(appSecretTAG,"no directory"))
                                            && fqPubRawData == (sharedPreferences.getInt(fqPubRawDataTAG,1))
                                            && fqInsertRawData == (sharedPreferences.getInt(fqInsertRawDataTAG,1))
                                            && dayStorage == (sharedPreferences.getInt(dayStorageTAG,7))){
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
                                if(!(input.toString().equals(sharedPreferences.getString(appSecretTAG,"no directory")))){
                                    textViewAppSecret.setText(input.toString());
                                    menu.findItem(R.id.actionSaveSetting).setVisible(true);
                                }else{
                                    if(textViewAppKey.getText().toString().trim()
                                            .equals(sharedPreferences.getString(appKeyTAG,"no directory"))
                                            && textViewAppSecret.getText().toString().trim()
                                            .equals(sharedPreferences.getString(appSecretTAG,"no directory"))
                                            && fqPubRawData == (sharedPreferences.getInt(fqPubRawDataTAG,1))
                                            && fqInsertRawData == (sharedPreferences.getInt(fqInsertRawDataTAG,1))
                                            && dayStorage == (sharedPreferences.getInt(dayStorageTAG,7))){
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
                        .itemsCallbackSingleChoice(getWhichFromSendData(fqPubRawData),
                                new MaterialDialog.ListCallbackSingleChoice() {
                                    @Override
                                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                        int value = getValueSendDataFromWhich(which);

                                        if(value != (sharedPreferences.getInt(fqInsertRawDataTAG,1))){
                                            fqPubRawData = value;
                                            sendDataValue.setText(text);
                                            menu.findItem(R.id.actionSaveSetting).setVisible(true);
                                        }
                                        else{
                                            if(textViewAppID.getText().toString().trim()
                                                    .equals(sharedPreferences.getString(appIdTAG,"no directory"))
                                                    && textViewAppKey.getText().toString().trim()
                                                    .equals(sharedPreferences.getString(appKeyTAG,"no directory"))
                                                    && textViewAppSecret.getText().toString().trim()
                                                    .equals(sharedPreferences.getString(appSecretTAG,"no directory"))
                                                    //&& ftPubRD == (sharedPreferences.getInt("ftPubRD",1))
                                                    && fqInsertRawData == (sharedPreferences.getInt(fqInsertRawDataTAG,1))
                                                    && dayStorage == (sharedPreferences.getInt(dayStorageTAG,7))){
                                                menu.findItem(R.id.actionSaveSetting).setVisible(false);
                                            }
                                            fqPubRawData = sharedPreferences.getInt(fqPubRawDataTAG,1);
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
        layoutInsertDataSensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(getContextManual())
                        .title(getResources().getString(R.string.frequencyInsertData))
                        .items(R.array.ftPubIM_IRDArray)
                        .itemsCallbackSingleChoice(getWhichFromInsertDataValue(fqInsertRawData),
                                new MaterialDialog.ListCallbackSingleChoice() {
                                    @Override
                                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                        int value = getValueInsertDataValueFromWhich(which);

                                        if(value != (sharedPreferences.getInt(fqInsertRawDataTAG,1))){
                                            fqInsertRawData = value;
                                            insertDataValue.setText(text);
                                            menu.findItem(R.id.actionSaveSetting).setVisible(true);
                                        }
                                        else{
                                            if(textViewAppID.getText().toString().trim()
                                                    .equals(sharedPreferences.getString(appIdTAG,"no directory"))
                                                    && textViewAppKey.getText().toString().trim()
                                                    .equals(sharedPreferences.getString(appKeyTAG,"no directory"))
                                                    && textViewAppSecret.getText().toString().trim()
                                                    .equals(sharedPreferences.getString(appSecretTAG,"no directory"))
                                                    && fqPubRawData == (sharedPreferences.getInt(fqPubRawDataTAG,1))
                                                    //&& ftIRD == (sharedPreferences.getInt("ftIRD",1))
                                                    && dayStorage == (sharedPreferences.getInt(dayStorageTAG,7))){
                                                menu.findItem(R.id.actionSaveSetting).setVisible(false);
                                            }
                                            fqInsertRawData = sharedPreferences.getInt(fqInsertRawDataTAG,1);
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
                        .itemsCallbackSingleChoice(getWhichFromStorageDataValue(dayStorage),
                                new MaterialDialog.ListCallbackSingleChoice() {
                                    @Override
                                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                        int value = getValueStorageDataValueFromWhich(which);

                                        if(value != (sharedPreferences.getInt(dayStorageTAG,7))){
                                            dayStorage = value;
                                            storageDataValue.setText(text);
                                            menu.findItem(R.id.actionSaveSetting).setVisible(true);
                                        }
                                        else{
                                            if(textViewAppID.getText().toString().trim()
                                                    .equals(sharedPreferences.getString(appIdTAG,"no directory"))
                                                    && textViewAppKey.getText().toString().trim()
                                                    .equals(sharedPreferences.getString(appKeyTAG,"no directory"))
                                                    && textViewAppSecret.getText().toString().trim()
                                                    .equals(sharedPreferences.getString(appSecretTAG,"no directory"))
                                                    && fqPubRawData == (sharedPreferences.getInt(fqPubRawDataTAG,1))
                                                    && fqInsertRawData == (sharedPreferences.getInt(fqInsertRawDataTAG,1))){
                                                    //&& dayStore == (sharedPreferences.getInt("dayStore",7))){
                                                menu.findItem(R.id.actionSaveSetting).setVisible(false);
                                            }
                                            dayStorage = sharedPreferences.getInt(dayStorageTAG,7);
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
                .equals(sharedPreferences.getString(appIdTAG,"no directory"))){
            //editor.putString("appID",textViewAppID.getText().toString().trim());
            objNETPIE.addProperty(appIdTAG,textViewAppID.getText().toString().trim());
            changeNETPIE = true;
        }
        if(!textViewAppKey.getText().toString().trim()
                .equals(sharedPreferences.getString(appKeyTAG,"no directory"))){
            //editor.putString("appKey",textViewAppKey.getText().toString().trim());
            objNETPIE.addProperty(appKeyTAG,textViewAppKey.getText().toString().trim());
            changeNETPIE = true;
        }
        if(!textViewAppSecret.getText().toString().trim()
                .equals(sharedPreferences.getString(appSecretTAG,"no directory"))){
            //editor.putString("appSecret",textViewAppSecret.getText().toString().trim());
            objNETPIE.addProperty(appSecretTAG,textViewAppSecret.getText().toString().trim());
            changeNETPIE = true;
        }
        if(fqPubRawData != sharedPreferences.getInt(fqPubRawDataTAG,1)){
            objDetails.addProperty(fqPubRawDataTAG,fqPubRawData);
            changeDetails = true;
        }
        if(fqInsertRawData != sharedPreferences.getInt(fqInsertRawDataTAG,1)){
            objDetails.addProperty(fqInsertRawDataTAG,fqInsertRawData);
            changeDetails = true;
        }
        if(dayStorage != sharedPreferences.getInt(dayStorageTAG,7)){
            objDetails.addProperty(dayStorageTAG,dayStorage);
            changeDetails =true;
        }
        if(swAutoMode.isChecked() != sharedPreferences.getBoolean("autoMode",false)){
            Log.e(TAG,"swAutoMode.isChecked(): "+String.valueOf(swAutoMode.isChecked()));
          
            objDetails.addProperty("autoMode",swAutoMode.isChecked());
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

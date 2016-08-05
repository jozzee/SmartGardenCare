package com.sitthiphong.smartgardencare.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.sitthiphong.smartgardencare.R;
import com.sitthiphong.smartgardencare.listener.ActionListener;

public class SetupNETPIEActivity extends AppCompatActivity {
    private final String TAG = "SetupNETPIEActivity";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private TextInputLayout tilAppID;
    private TextInputLayout tilAppKey;
    private TextInputLayout tilAppSecret;
    private EditText etAppID;
    private EditText etAppKey;
    private EditText etAppSecret;
    private ImageView logo;
    private Button cancel;
    private Button ok;
    public ActionListener actionListener = new ActionListener();

    private final String appIdTAG = "appId";
    private final String appKeyTAG = "appKey";
    private final String appSecretTAG ="appSecret";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_netpie);

        sharedPreferences = getSharedPreferences("Details", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        logo = (ImageView)findViewById(R.id.logoNETPIE);
        cancel = (Button)findViewById(R.id.btnCancelSetNETPIE) ;
        ok = (Button)findViewById(R.id.btnSubmitSetNETPIE);
        tilAppID = (TextInputLayout)findViewById(R.id.TextInputLayoutAppID);
        tilAppKey = (TextInputLayout)findViewById(R.id.TextInputLayoutAppKey);
        tilAppSecret = (TextInputLayout)findViewById(R.id.TextInputLayoutAppSecret);
        etAppID = (EditText)findViewById(R.id.editTextAppID);
        etAppKey =  (EditText)findViewById(R.id.editTextAppKey);
        etAppSecret =  (EditText)findViewById(R.id.editTextAppSecret);


        etAppID.addTextChangedListener(new MyTextWatcher(tilAppID));
        etAppKey.addTextChangedListener(new MyTextWatcher(tilAppKey));
        etAppSecret.addTextChangedListener(new MyTextWatcher(tilAppSecret));

        if(savedInstanceState != null){
            etAppID.setText(savedInstanceState.getString(appIdTAG));
            etAppKey.setText(savedInstanceState.getString(appKeyTAG));
            etAppSecret.setText(savedInstanceState.getString(appSecretTAG));
        }

        etAppID.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_NEXT){
                    validate(etAppID,tilAppID,"App ID");
                }
                return false;
            }
        });

        etAppKey.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_NEXT){
                    validate(etAppKey,tilAppKey,"App Key");
                }
                return false;
            }
        });
        etAppSecret.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    validate(etAppSecret,tilAppSecret,"App Secret");
                    onOK();

                }
                return false;
            }
        });
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://netpie.io"));
                startActivity(intent);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogWarningExit();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOK();
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
    }
    @Override
    public void onBackPressed() {
        Log.i(TAG, "onBackPressed");
        //super.onBackPressed();
        dialogWarningExit();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putString(appIdTAG,etAppID.getText().toString().trim());
        outState.putString(appKeyTAG,etAppKey.getText().toString().trim());
        outState.putString(appSecretTAG,etAppSecret.getText().toString().trim());

    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i(TAG, "onRestoreInstanceState");

    }
    private void dialogWarningExit(){
        new MaterialDialog.Builder(getContextManual())
                .title(R.string.warning)
                .content(R.string.warningExitSetupNetPie)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Log.w(TAG,"Exit setup netpie");
                        onCancel();
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
    private void onCancel(){
        //BusProvider.getInstance().post(new OnFinishSetupNetPieEvent("cancel"));
        actionListener.onFinishSetupNETPIE.onFinishSetupNETPIE(false);
        finish();
    }
    private void onOK(){
        if(validate(etAppID,tilAppID,"App ID")
                &&  validate(etAppKey,tilAppKey,"App Key")
                &&  validate(etAppSecret,tilAppSecret,"App Secret")){
            editor.putString(appIdTAG,etAppID.getText().toString().trim());
            editor.putString(appKeyTAG,etAppKey.getText().toString().trim());
            editor.putString(appSecretTAG,etAppSecret.getText().toString().trim());
            editor.commit();
            Log.w(TAG,"Setup netpie success");
            //BusProvider.getInstance().post(new OnFinishSetupNetPieEvent("ok"));
            actionListener.onFinishSetupNETPIE.onFinishSetupNETPIE(true);
            finish();
        }

    }
    private boolean validate(EditText editText, TextInputLayout textInputLayout,String netPieValue){
        if(editText.getText().toString().trim().isEmpty()){
            textInputLayout.setError("Enter your " +netPieValue);
            return false;
        }
        else{
            return true;
        }

    }

    private class MyTextWatcher implements TextWatcher {

        private TextInputLayout textInputLayout;
        public MyTextWatcher(TextInputLayout textInputLayout) {
            this.textInputLayout = textInputLayout;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            textInputLayout.setErrorEnabled(false);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
    public Context getContextManual(){
        return this;
    }
}

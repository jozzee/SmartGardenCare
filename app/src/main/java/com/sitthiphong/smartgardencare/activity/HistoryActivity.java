package com.sitthiphong.smartgardencare.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.sitthiphong.smartgardencare.R;
import com.sitthiphong.smartgardencare.adapter.LogDataAdapter;
import com.sitthiphong.smartgardencare.datamodel.ConfigData;
import com.sitthiphong.smartgardencare.datamodel.LogDataBean;
import com.sitthiphong.smartgardencare.datamodel.SubscribeBean;
import com.sitthiphong.smartgardencare.libs.PDFclass;
import com.sitthiphong.smartgardencare.libs.ShareData;
import com.sitthiphong.smartgardencare.service.RestApiNetPie;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private final String TAG = "HistoryActivity";
    private ShareData shareData;
    private CoordinatorLayout rootLayout;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private LogDataAdapter adapter;

    private ProgressBar progressBar;
    private TextView exception;
    private FloatingActionButton myFab;
    private int REQUEST_CODE_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 9999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        shareData = new ShareData(this);
        shareData.createSharePreference();

        rootLayout = (CoordinatorLayout) findViewById(R.id.root_layout);  //set RootLayout
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.history);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setVisibility(View.GONE);

        exception = (TextView) findViewById(R.id.exception);
        exception.setVisibility(View.GONE);

        progressBar = (ProgressBar) findViewById(R.id.progress);
        progressBar.setVisibility(View.VISIBLE);
        myFab = (FloatingActionButton) findViewById(R.id.btn_fab);
        myFab.setVisibility(View.GONE);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (checkPermissionsWriteExternalStorage()) {
                    //new SaveIMTask().execute(imBean.getBitmap());
                    //createPdf();
                    PDFclass pdFClass = new PDFclass(getContext(),adapter.getLogList());
                    pdFClass.createPDF();
                }
            }
        });

        new SubscribeTask().execute(ConfigData.logDataListTopic);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "onOptionsItemSelected");
        int id = item.getItemId();
        if (id == 16908332) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public List<LogDataBean> getLogList(String jsonString) {
        Gson gson = new Gson();
        JsonArray jsonArray = gson.fromJson(jsonString, JsonArray.class);
        Type listType = new TypeToken<ArrayList<LogDataBean>>() {
        }.getType();
        return gson.fromJson(jsonArray, listType);
    }

    private void onSubscribeCallBack(SubscribeBean result) {
        if(result != null){
            if (!result.getPayload().equals("")) {
                progressBar.setVisibility(View.GONE);
                exception.setVisibility(View.GONE);

                adapter = new LogDataAdapter(this, getLogList(result.getPayload()));
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.VISIBLE);
                myFab.setVisibility(View.VISIBLE);

            } else {
                progressBar.setVisibility(View.GONE);
                exception.setText(getString(R.string.exception));
                exception.setVisibility(View.VISIBLE);
            }
        }



    }

    /**
     * Checks if the app has permission to write to device storage
     * <p>
     * If the app does not has permission then the user will be prompted to grant permissions
     */
    public boolean checkPermissionsWriteExternalStorage() {
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_CODE_PERMISSIONS_WRITE_EXTERNAL_STORAGE);
            return false;
        } else {
            return true;
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.e(TAG, "onRequestPermissionsResult");
        Log.e(TAG, "requestCode: " + requestCode);

        if (requestCode == REQUEST_CODE_PERMISSIONS_WRITE_EXTERNAL_STORAGE) {
            // Received permission result for READ_PHONE_STATE permission.est.");
            // Check if the only required permission has been granted
            Log.e(TAG,"grantResults[0]: "+grantResults[0]);
            Log.e(TAG,"grantResults.length: "+grantResults.length);
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // READ_PHONE_STATE permission has been granted, proceed with displaying IMEI Number
                //alertAlert(getString(R.string.permision_available_read_phone_state));
                PDFclass pdFClass = new PDFclass(getContext(),adapter.getLogList());
                pdFClass.createPDF();
            } else {
                notificationSnackBar(rootLayout, getString(R.string.permissionDenied));
            }
        } else {


        }
    }

    public void notificationSnackBar(View v, String message) {
        Snackbar.make(v, message, Snackbar.LENGTH_LONG).show();
    }
    private Context getContext(){
        return this;
    }

    private class SubscribeTask extends AsyncTask<String, Void, SubscribeBean> {
        private final String TAG = "SubscribeTask";

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
            getSupportActionBar().setTitle(getString(R.string.loading));


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
            getSupportActionBar().setTitle(getString(R.string.history));
            onSubscribeCallBack(result);

        }
    }


}

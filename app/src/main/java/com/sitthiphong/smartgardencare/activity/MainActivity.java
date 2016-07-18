package com.sitthiphong.smartgardencare.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.sitthiphong.smartgardencare.R;
import com.sitthiphong.smartgardencare.bean.SubscribeBean;
import com.sitthiphong.smartgardencare.listener.SubscribeCallBackListener;
import com.sitthiphong.smartgardencare.core.NetPieRestApi;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentContain,
                        new MoistureFragment().newInstance(),
                        "im").commit();
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
    public Context getContextManual(){
        return this;
    }
}

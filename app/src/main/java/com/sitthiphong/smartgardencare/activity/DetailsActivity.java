package com.sitthiphong.smartgardencare.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.sitthiphong.smartgardencare.R;
import com.sitthiphong.smartgardencare.datamodel.ConfigData;
import com.sitthiphong.smartgardencare.datamodel.LightBean;
import com.sitthiphong.smartgardencare.datamodel.MoistureBean;
import com.sitthiphong.smartgardencare.datamodel.RawDataListBean;
import com.sitthiphong.smartgardencare.datamodel.SubscribeBean;
import com.sitthiphong.smartgardencare.datamodel.TempBean;
import com.sitthiphong.smartgardencare.libs.MagDiscreteSeekBar;
import com.sitthiphong.smartgardencare.libs.ShareData;
import com.sitthiphong.smartgardencare.listener.SetStandListener;
import com.sitthiphong.smartgardencare.service.RestApiNetPie;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class DetailsActivity extends AppCompatActivity implements
        SetStandListener.SetWaitDialog,
        SetStandListener.Result {
    private final String TAG = "DetailsActivity";
    public static SetStandListener.Result resultSetStandard;


    private CoordinatorLayout rootLayout;
    private RelativeLayout containLayout;
    private Toolbar toolbar;
    private WebView webView;
    JsonObject jsonObject;
    TextView valStd, except;

    private String sensor;
    private ShareData shareData;
    private MagDiscreteSeekBar seekBar;
    private String title;
    private ProgressBar progressBarWebView;
    private ProgressDialog progressDialog;
    private Handler setStandardHandle;
    private Runnable setStandardRunnable;

    private List<RawDataListBean> rawDataList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        shareData = new ShareData(this);
        shareData.createSharePreference();
        resultSetStandard = this;

        rootLayout = (CoordinatorLayout) findViewById(R.id.root_layout);  //set RootLayout
        containLayout = (RelativeLayout) findViewById(R.id.contain_layout);
        except = (TextView) findViewById(R.id.exception);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.details);

        progressBarWebView = (ProgressBar) findViewById(R.id.progressWebView);
        progressBarWebView.setVisibility(View.VISIBLE);


        TextView val1 = (TextView) findViewById(R.id.value1);
        TextView val2 = (TextView) findViewById(R.id.value2);
        TextView std = (TextView) findViewById(R.id.standard_title);
        valStd = (TextView) findViewById(R.id.standard_value);

        seekBar = new MagDiscreteSeekBar(rootLayout, valStd, this);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            sensor = bundle.getString("sensor");
            if (sensor.equals("moisture")) {
                title = getString(R.string.details) + " " + getString(R.string.moisture);
                getSupportActionBar().setTitle(title);
                MoistureBean bean = new MoistureBean(bundle.getString("data"));

                val1.setText((bean.getPoint1() > 0) ? (String.valueOf(bean.getPoint1()) + " %") : getString(R.string.sensorError));
                val2.setText((bean.getPoint2() > 0) ? (String.valueOf(bean.getPoint2()) + " %") : getString(R.string.sensorError));
                std.setText(getString(R.string.value) + " " + getString(R.string.moisture) + " " + getString(R.string.standard));
                valStd.setText(String.valueOf(shareData.getMoistureStandard()) + " %");

                seekBar.setUnit(" %");
                seekBar.setColorId(ContextCompat.getColor(this, R.color.Blue));
                seekBar.setMinValue(0);
                seekBar.setMaxValue(80);
                seekBar.setVal(shareData.getMoistureStandard());
                seekBar.setSensor(ConfigData.mosStd);
                seekBar.createSeekBar();



                /*seekBar = new MagDiscreteSeekBar(
                        rootLayout,
                        R.id.seekBarValue,
                        valStd,
                        " %",//unit
                        ContextCompat.getColor(this, R.color.Blue),//color
                        80,//max
                        10,//min
                        shareData.getMoistureStandard(),
                        ConfigData.mosStd);//progress
                seekBar.createSeekBar();*/

            } else if (sensor.equals("temp")) {
                title = getString(R.string.details) + " " + getString(R.string.temperature);
                getSupportActionBar().setTitle(title);
                TempBean bean = new TempBean(bundle.getString("data"));

                val1.setText((bean.getPoint1() > 0) ? (String.valueOf(bean.getPoint1()) + " °C") : getString(R.string.sensorError));
                val2.setText((bean.getPoint2() > 0) ? (String.valueOf(bean.getPoint2()) + " °C") : getString(R.string.sensorError));
                std.setText(getString(R.string.value) + " " + getString(R.string.temperature) + " " + getString(R.string.standard));
                valStd.setText(String.valueOf(shareData.getTempStandard()) + " °C");
                /*seekBar = new MagDiscreteSeekBar(
                        rootLayout,
                        R.id.seekBarValue,
                        valStd,
                        " °C",//unit
                        ContextCompat.getColor(this, R.color.DeepOrange),//color
                        50,//max
                        10,//min
                        shareData.getTempStandard(),
                        ConfigData.tmpStd);//progress
                seekBar.createSeekBar();*/

                seekBar.setUnit(" °C");
                seekBar.setColorId(ContextCompat.getColor(this, R.color.DeepOrange));
                seekBar.setMinValue(10);
                seekBar.setMaxValue(50);
                seekBar.setVal(shareData.getTempStandard());
                seekBar.setSensor(ConfigData.tmpStd);
                seekBar.createSeekBar();

            } else if (sensor.equals("light")) {
                title = getString(R.string.details) + " " + getString(R.string.lightIntensity);
                getSupportActionBar().setTitle(title);
                LightBean bean = new LightBean(bundle.getString("data"));

                TextView t1 = (TextView) findViewById(R.id.point_title1);
                TextView t2 = (TextView) findViewById(R.id.point_title2);
                t1.setText(getString(R.string.lightIn));
                t2.setText(getString(R.string.lightOut));
                val1.setText((bean.getLightIn() >= 0) ? (String.valueOf(bean.getLightIn()) + " Lux") : getString(R.string.sensorError));
                val2.setText((bean.getLightOut() >= 0) ? (String.valueOf(bean.getLightOut()) + " Lux") : getString(R.string.sensorError));
                std.setText(getString(R.string.value) + " " + getString(R.string.lightIntensity) + " " + getString(R.string.standard));
                valStd.setText(String.valueOf(shareData.getLightStandard()) + " Lux");

                /*seekBar = new MagDiscreteSeekBar(
                        rootLayout,
                        R.id.seekBarValue,
                        valStd,
                        " Lux",//unit
                        ContextCompat.getColor(this, R.color.DeepOrange),//color
                        20000,//max
                        1000,//min
                        shareData.getLightStandard(),
                        ConfigData.ligStd);//progress
                seekBar.createSeekBar();*/

                seekBar.setUnit(" Lux");
                seekBar.setColorId(ContextCompat.getColor(this, R.color.Amber));
                seekBar.setMinValue(1000);
                seekBar.setMaxValue(20000);
                seekBar.setVal(shareData.getLightStandard());
                seekBar.setSensor(ConfigData.ligStd);
                seekBar.createSeekBar();

            }
        }

        new SubscribeTask().execute(ConfigData.rawDataListTopic);






         /*var testDataObject = {"name":"sensorName",
                "categories":["1","2","3","4","5","6","7","8","9","10"],
        "colorSet":['#F44336','#2196F3','#4CAF50','#FF9800'],
        "sensorData":[{"name":"point1","data":[42.44,44.31,40.56,43.67,45.77,49.35,51.98,48.78,54.66,47.62]},
        {"name":"point2","data":[44.44,45.32,41.56,42.67,44.77,51.35,52.98,45.78,55.66,46.62]},
        {"name":"point3","data":[43.44,41.32,41.56,42.67,43.77,51.35,42.98,47.78,54.66,40.62]},
        {"name":"point4","data":[41.44,42.32,46.56,48.67,43.77,52.35,44.98,48.78,50.66,43.62]}]
        };*/
        /*jsonObject = new JsonObject();
        jsonObject.addProperty("name", "sensorName");
        JsonArray categories = new JsonArray();
        for (int i = 0; i < 67; i++) {
            categories.add(String.valueOf(i + 1));
        }
        jsonObject.add("categories", categories);
        JsonArray sensorData = new JsonArray();
        for (int i = 0; i < 2; i++) {
            JsonObject object = new JsonObject();
            object.addProperty("name", "point" + String.valueOf(i + 1));
            JsonArray data = new JsonArray();
            for (int j = 0; j < 67; j++) {
                Random rand = new Random();
                data.add(rand.nextInt(80 - 40));
            }
            object.add("data", data);
            sensorData.add(object);
        }
        jsonObject.add("sensorData", sensorData);

        JsonArray colorSet = new JsonArray();
        colorSet.add("#F44336");
        colorSet.add("#2196F3");
        colorSet.add("#4CAF50");
        colorSet.add("#FF9800");
        jsonObject.add("colorSet", colorSet);

        Log.d(TAG, jsonObject.toString());

        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/lineChart.html");
        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                Log.e(TAG, "onPageFinished");

                webView.loadUrl("javascript:createChart(" + jsonObject.toString() + ");");
                //webView.setVisibility(View.VISIBLE);

            }
        });*/
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

    private void onSubscribeCallBack(SubscribeBean result) {

        if (result != null) {
            rawDataList = getRawDataList(result.getPayload());

            JsonArray categories = new JsonArray();
            JsonArray data1 = new JsonArray();
            JsonArray data2 = new JsonArray();
            int max = 0, min = 0;
            String unit = "";
            String name = "";

            if (sensor.equals("moisture")) {
                min = 0;
                max = 100;
                unit = " %";
                name = getResources().getString(R.string.moisture);

            } else if (sensor.equals("temp")) {
                min = 0;
                max = 50;
                unit = " °C";
                name = getResources().getString(R.string.temperature);

            } else if (sensor.equals("light")) {
                min = 0;
                max = 20000;
                unit = " Lux";
                name = getResources().getString(R.string.lightIntensity);
            }

            for (int i = (rawDataList.size() - 1); i >= 0; i--) {
                RawDataListBean bean = rawDataList.get(i);

                if (sensor.equals("moisture")) {

                    if ((bean.getMos1() >= 0) && (bean.getMos2() >= 0)) {
                        categories.add(getDateTime(bean.getTime() * 1000));
                        data1.add(bean.getMos1());
                        data2.add(bean.getMos2());
                    }

                } else if (sensor.equals("temp")) {

                    if ((bean.getTmp1() >= 0) && (bean.getTmp2() >= 0)) {
                        categories.add(getDateTime(bean.getTime() * 1000));
                        data1.add(bean.getTmp1());
                        data2.add(bean.getTmp2());
                    }

                } else if (sensor.equals("light")) {
                    if ((bean.getLight_in() >= 0) && (bean.getLight_out() >= 0)) {
                        categories.add(getDateTime(bean.getTime() * 1000));
                        data1.add(bean.getLight_in());
                        data2.add(bean.getLight_out());
                    }
                }

            }

            JsonArray sensorData = new JsonArray();


            JsonObject object = new JsonObject();
            object.add("data", data1);
            if (sensor.equals("light")) {
                object.addProperty("name", getString(R.string.lightIn));
            } else {
                object.addProperty("name", getString(R.string.point1));
            }
            sensorData.add(object);
            object = new JsonObject();
            object.add("data", data2);
            if (sensor.equals("light")) {
                object.addProperty("name", getString(R.string.lightOut));
            } else {
                object.addProperty("name", getString(R.string.point2));
            }
            sensorData.add(object);

            jsonObject = new JsonObject();
            jsonObject.addProperty("name", name);
            jsonObject.add("categories", categories);
            jsonObject.add("sensorData", sensorData);

            JsonArray colorSet = new JsonArray();
            colorSet.add("#9C27B0");
            colorSet.add("#FFC107");
            colorSet.add("#4CAF50");
            colorSet.add("#FF9800");
            jsonObject.add("colorSet", colorSet);
            jsonObject.addProperty("max", max);
            jsonObject.addProperty("min", min);
            jsonObject.addProperty("unit", unit);


            Log.d(TAG, "data: " + jsonObject.toString());
            progressBarWebView.setVisibility(View.GONE);
            webView = (WebView) findViewById(R.id.webView);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadUrl("file:///android_asset/lineChart.html");
            webView.setWebViewClient(new WebViewClient() {
                public void onPageFinished(WebView view, String url) {
                    Log.e(TAG, "onPageFinished");

                    webView.loadUrl("javascript:createChart(" + jsonObject.toString() + ");");
                    //webView.setVisibility(View.VISIBLE);

                }
            });


        } else {
            setException(getString(R.string.noInternet));
        }

    }

    private void setException(String error) {
        containLayout.setVisibility(View.GONE);
        except.setText(error);
        except.setVisibility(View.VISIBLE);
    }

    private String getDateTime(long time) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("HH:mm dd MMM");
            Date date = (new Date(time));
            return dateFormat.format(date);

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    public void notificationSnackBar(View v, String message) {
        Snackbar.make(v, message, Snackbar.LENGTH_LONG).show();
    }

    private Context getContext() {
        return this;
    }

    public List<RawDataListBean> getRawDataList(String jsonString) {
        Gson gson = new Gson();
        JsonArray jsonArray = gson.fromJson(jsonString, JsonArray.class);
        Type listType = new TypeToken<ArrayList<RawDataListBean>>() {
        }.getType();
        return gson.fromJson(jsonArray, listType);
    }

    public SetStandListener.Result getResult() {
        return this;
    }

    @Override
    public void result(boolean result, String error) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        if (setStandardHandle != null) {
            Log.e(TAG, "remove task");
            setStandardHandle.removeCallbacks(setStandardRunnable);
        }

        if (result) {
            showDialog(getString(R.string.success),getString(R.string.saveSettingSuccess));
            seekBar.setNewValue();

        } else {
            seekBar.setOldValue();
            if (error.equals(getString(R.string.noInternet))) {
                notificationSnackBar(rootLayout, error);
            }else{
                showDialog(getString(R.string.exception),error);
            }
        }
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }


    @Override
    public void setWaitDialog() {

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getString(R.string.onSaveSetting));
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        setStandardHandle = new Handler();
        setStandardRunnable = new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "run task");
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                showDialog(getString(R.string.exception), getString(R.string.piNotResponse));
                seekBar.setOldValue();
            }
        };
        setStandardHandle.postDelayed(setStandardRunnable, 20000); //20 second

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
                .show();
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
            Log.e(TAG, "result: " + result);
            getSupportActionBar().setTitle(title);
            onSubscribeCallBack(result);

        }
    }

}

package com.sitthiphong.smartgardencare.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.sitthiphong.smartgardencare.R;
import com.sitthiphong.smartgardencare.activity.adapter.MoreAdapter;
import com.sitthiphong.smartgardencare.bean.RawDataBean;
import com.sitthiphong.smartgardencare.provider.BusProvider;
import com.sitthiphong.smartgardencare.provider.GsonProvider;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MoreActivity extends AppCompatActivity {
    private String TAG = "MoreActivity";

    private Bundle bundle;
    private List<RawDataBean> rawList;
    private int from;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private MoreAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        bundle = getIntent().getExtras();
        if(bundle != null) {
            Log.i(TAG, "get action from bundle");
            from = (Integer)bundle.get("from");
            rawList = getRawList((String)bundle.get("rawListBean"));
            Log.e(TAG,String.valueOf(from));

        }

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(from == 1){
            getSupportActionBar().setTitle(R.string.logMoisture);
        }
        else if(from == 2){
            getSupportActionBar().setTitle(R.string.logTemp);
        }
        else if(from == 3){
            getSupportActionBar().setTitle(R.string.logLight);
        }

        recyclerView = (RecyclerView)findViewById(R.id.recyclerViewMore);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new MoreAdapter(this,rawList,from);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

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
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "onOptionsItemSelected");
        int id = item.getItemId();
        if(id == 16908332){
            onBackPressed();
            return true;
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
    public List<RawDataBean> getRawList(String rawListAsJsonString){
        JsonArray jsonArray = GsonProvider.getInstance().fromJson(rawListAsJsonString, JsonArray.class);
        Type listType = new TypeToken<ArrayList<RawDataBean>>(){}.getType();
        return GsonProvider.getInstance().fromJson(jsonArray, listType);
    }
}

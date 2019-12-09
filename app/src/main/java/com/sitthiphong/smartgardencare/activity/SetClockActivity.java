package com.sitthiphong.smartgardencare.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.sitthiphong.smartgardencare.R;
import com.sitthiphong.smartgardencare.adapter.AlarmClockAdapter;
import com.sitthiphong.smartgardencare.datamodel.AlarmClockBean;
import com.sitthiphong.smartgardencare.libs.ShareData;
import com.sitthiphong.smartgardencare.libs.TimePickerFragment;
import com.sitthiphong.smartgardencare.listener.ClockListener;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SetClockActivity extends AppCompatActivity
        implements AlarmClockAdapter.OnRemoveClockListener,
        AlarmClockAdapter.HaveChangeListener,
        ClockListener.OnFinishSaveClockListener {

    public static ClockListener.OnSaveClockListener onSaveClockListener = null;
    public static ClockListener.OnFinishSaveClockListener onFinishSaveClockListener = null;
    private final String TAG = "SetClockActivity";
    private ShareData shareData;
    private CoordinatorLayout rootLayout;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private AlarmClockAdapter adapter;
    private FloatingActionButton myFab;
    private RelativeLayout layoutAddAlarm;
    private Menu menu;
    private ProgressDialog progressDialog;
    private Handler setClockHandle;
    private Runnable setClockRunnable;


    private List<AlarmClockBean> alarmClockList;
    private boolean isOpenSave = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_time);


        onFinishSaveClockListener = this;
        shareData = new ShareData(this);
        shareData.createSharePreference();

        rootLayout = (CoordinatorLayout) findViewById(R.id.activity_set_time);  //set RootLayout
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.setClock);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setVisibility(View.GONE);

        myFab = (FloatingActionButton) findViewById(R.id.btn_fab);
        myFab.setVisibility(View.GONE);

        layoutAddAlarm = (RelativeLayout) findViewById(R.id.layout_add_alarm);
        layoutAddAlarm.setVisibility(View.GONE);

        alarmClockList = getAlarmClockList();
        if (alarmClockList == null) {
            setNullClock();

        } else {
            if (alarmClockList.size() > 0) {
                setRecyclerView();
            } else {
                setNullClock();
            }

        }

        myFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTimePickerFragment();
            }
        });
    }

    private void setRecyclerView() {
        layoutAddAlarm.setVisibility(View.GONE);
        adapter = new AlarmClockAdapter(this, alarmClockList);
        adapter.setOnRemoveClock(this);
        adapter.setHaveChangeListener(this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        recyclerView.setVisibility(View.VISIBLE);
        myFab.setVisibility(View.VISIBLE);
    }

    private void setNullClock() {
        recyclerView.setVisibility(View.GONE);
        myFab.setVisibility(View.GONE);
        layoutAddAlarm.setVisibility(View.VISIBLE);
        layoutAddAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTimePickerFragment();
            }
        });
    }

    private void setTimePickerFragment() {
        TimePickerFragment timePicker = new TimePickerFragment();
        timePicker.setOnTimeSetListener(new TimePickerFragment.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute, String timeText) {
                Log.e(TAG, "onTimeSet: hour: " + hourOfDay + ", minute:" + minute);
                if (adapter != null) {
                    alarmClockList = adapter.getAlarmClockList();
                    alarmClockList.add(new AlarmClockBean(hourOfDay, minute, 1, true, false, new ArrayList<String>()));
                    adapter.notifyDataSetChanged();
                    menu.findItem(R.id.actionSaveClock).setVisible(true);
                    isOpenSave = true;

                } else if (alarmClockList == null) {
                    alarmClockList = new ArrayList<AlarmClockBean>();
                    alarmClockList.add(new AlarmClockBean(hourOfDay, minute, 1, true, false, new ArrayList<String>()));
                    setRecyclerView();
                    menu.findItem(R.id.actionSaveClock).setVisible(true);
                    isOpenSave = true;
                } else if (alarmClockList.size() == 0) {
                    alarmClockList = new ArrayList<AlarmClockBean>();
                    alarmClockList.add(new AlarmClockBean(hourOfDay, minute, 1, true, false, new ArrayList<String>()));
                    setRecyclerView();
                    menu.findItem(R.id.actionSaveClock).setVisible(true);
                    isOpenSave = true;
                }
            }
        });
        timePicker.show(getFragmentManager(), "TimePicker");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.menu_set_clock, menu);
        menu.findItem(R.id.actionSaveClock).setVisible(false);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        Log.i(TAG, "onOptionsItemSelected id: " + id);
        if (id == 16908332) {
            onBackPressed();
            return true;
        } else if (id == R.id.actionSaveClock) {
            if (adapter != null) {
                if (onSaveClockListener != null) {
                    onSaveClockListener.onSaveClockListener(new Gson().toJson(adapter.getAlarmClockList()));
                    showProgressDialog(getString(R.string.onSaveSetting));
                    setClockHandle = new Handler();
                    setClockRunnable = new Runnable() {
                        @Override
                        public void run() {
                            Log.i(TAG, "run task");
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }
                            showDialog(getString(R.string.exception), getString(R.string.piNotResponse), false);
                        }
                    };
                    setClockHandle.postDelayed(setClockRunnable, 20000); //20 second
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Log.e(TAG, "onBackPressed");
        if (isOpenSave) {
            showDialogExitSetClock();
        } else {
            SetClockActivity.super.onBackPressed();
        }
    }

    private void showProgressDialog(String messageDialog) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(messageDialog);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void showDialogExitSetClock() {
        new MaterialDialog.Builder(this)
                .title(getString(R.string.warning))
                .content(getString(R.string.warningExitClock))
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        SetClockActivity.super.onBackPressed();
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

    public void showDialog(String title, String message, final boolean inOnBackPressed) {
        new MaterialDialog.Builder(this)
                .title(title)
                .content(message)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        if (inOnBackPressed) {
                            SetClockActivity.super.onBackPressed();
                        }
                    }
                })
                .show();
    }

    private List<AlarmClockBean> getAlarmClockList() {

        List<AlarmClockBean> alarmClockList = null;//= new ArrayList<AlarmClockBean>()

        if (shareData.getAlarmClockList().equals("")) {
            //alarmClockList.add(new AlarmClockBean(9, 9, 0, false, new ArrayList<String>()));
        } else {
            Gson gson = new Gson();
            JsonArray jsonArray = gson.fromJson(shareData.getAlarmClockList(), JsonArray.class);
            Type listType = new TypeToken<ArrayList<AlarmClockBean>>() {
            }.getType();
            alarmClockList = gson.fromJson(jsonArray, listType);
        }
        return alarmClockList;
    }

    private AlarmClockAdapter.OnRemoveClockListener getRemoveClockListener() {
        return this;
    }

    public ClockListener.OnFinishSaveClockListener getFinishSaveClockListener() {
        return this;
    }


    @Override
    public void onRemoveClockListener() {
        if (adapter != null) {
            if (adapter.getAlarmClockList().size() == 0) {
                setNullClock();
            }
        }
        menu.findItem(R.id.actionSaveClock).setVisible(true);
        isOpenSave = true;
    }


    @Override
    public void haveChangeListener() {
        if (!isOpenSave) {
            menu.findItem(R.id.actionSaveClock).setVisible(true);
            isOpenSave = true;
        }
    }

    @Override
    public void onFinishSaveClockListener(int status) {
        if (status == -1) {
            notificationSnackBar(rootLayout, getString(R.string.notConnectNetPie));

        } else if (status == 0) {
            notificationSnackBar(rootLayout, getString(R.string.noInternet));

        } else if (status == 1) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            if (setClockHandle != null) {
                Log.e(TAG, "remove task");
                setClockHandle.removeCallbacks(setClockRunnable);
            }
            try {
                showDialog(getString(R.string.success), getString(R.string.saveSettingSuccess), true);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void notificationSnackBar(View v, String message) {
        Snackbar.make(v, message, Snackbar.LENGTH_LONG).show();
    }

}

package com.sitthiphong.smartgardencare.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.sitthiphong.smartgardencare.R;
import com.sitthiphong.smartgardencare.activity.MainActivity;
import com.sitthiphong.smartgardencare.datamodel.AlarmClockBean;
import com.sitthiphong.smartgardencare.libs.ShareData;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Akexorcist on 3/6/2016 AD.
 */
public class GcmDownstreamService extends GcmListenerService {
    private static final String TAG = "DcmDownstreamService";

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onMessageReceived(String from, Bundle data) {
        // TODO Do something here
        Log.e(TAG, "Message Incoming");
        String title = (String) data.get("title");

        if (title.equals("except")) {
            Log.e(TAG, "body: " + String.valueOf(data.get("body")));
            JsonObject bodyObj = new Gson().fromJson(String.valueOf(data.get("body")), JsonObject.class);

            StringBuilder builder = new StringBuilder();
            builder.append(getDateTime(bodyObj.get("time").getAsLong()));
            builder.append(" ");
            builder.append(getString(R.string.exceptionNoti));
            builder.append("\n");
            builder.append(getMessage(bodyObj.get("errorCode").getAsString()));

            if (isForeground("com.sitthiphong.smartgardencare")) {
                sendForegroundNotification(builder.toString());
            } else {
                sendNotification(builder.toString());
            }
        } else if (title.equals("alarm")) {
            Log.e(TAG, "body: " + String.valueOf(data.get("body")));
            JsonObject alarmObj = new Gson().fromJson(String.valueOf(data.get("body")), JsonObject.class);
            if (alarmObj.get("flag") != null) {
                JsonObject flag = new Gson().fromJson(alarmObj.get("flag"), JsonObject.class);
                if (flag.get("isOpen") != null) {
                    int index = flag.get("index").getAsInt();
                    List<AlarmClockBean> alarmClockList = getAlarmClockList();
                    AlarmClockBean bean = alarmClockList.get(index);
                    bean.setOpen(flag.get("isOpen").getAsBoolean());
                    alarmClockList.set(index, bean);

                    String tt = new Gson().toJson(alarmClockList);
                    Log.e(TAG, "tt: " + tt);
                    ShareData shareData = new ShareData(this);
                    shareData.createSharePreference();
                    shareData.setAlarmClockList(tt);
                }

            }
            boolean success = alarmObj.get("success").getAsBoolean();
            int working = alarmObj.get("working").getAsInt();
            StringBuilder builder = new StringBuilder();
            if (success) {
                JsonObject dataWorking = new Gson().fromJson(alarmObj.get("data"), JsonObject.class);
                builder.append(getWorking(working) + " ");
                builder.append(getString(R.string.byAlarmClock) +" "+ alarmObj.get("time").getAsString() + " \n");
                builder.append(getString(R.string.before) + " " + getValBefore(dataWorking, working) + ", ");
                builder.append(getString(R.string.after) + " " + getValAfter(dataWorking, working));

                if (isForeground("com.sitthiphong.smartgardencare")) {
                    sendForegroundNotification(builder.toString());
                } else {
                    sendNotification(builder.toString());
                }

            } else {
                builder = new StringBuilder();
                builder.append(getWorking(working) + " ");
                builder.append(getString(R.string.byAlarmClock) +" "+ alarmObj.get("time").getAsString() + " \n");
                JsonObject dataWorking = new Gson().fromJson(alarmObj.get("data"), JsonObject.class);
                int errorCode = dataWorking.get("errorCode").getAsInt();
                builder.append(getExcept(errorCode));

                if (isForeground("com.sitthiphong.smartgardencare")) {
                    sendForegroundNotification(builder.toString());
                } else {
                    sendNotification(builder.toString());
                }


            }

        }


    }

    private String getExcept(int errorCode) {
        if (errorCode == 0) {
            return getString(R.string.canNotSaveData);
        } else if (errorCode == 1) {
            return getString(R.string.waterFalse);
        } else if (errorCode == 2) {
            return getString(R.string.canNotSaveData);
        } else if (errorCode == 3) {
            return getString(R.string.tempNotDrop);
        } else if (errorCode == 4) {
            return getString(R.string.canNotFoggy);
        } else if (errorCode == 5) {
            return getString(R.string.slatOpen);
        } else if (errorCode == 6) {
            return getString(R.string.slatClose);
        } else if (errorCode == 7) {
            return getString(R.string.lightNotDrops);
        } else if (errorCode == 8) {
            return getString(R.string.increasedHumidityNotSoMuch);
        }else{
            return getString(R.string.exception);
        }
    }

    private String getValBefore(JsonObject dataWorking, int working) {
        String r = "";
        if (working == 1) {
            if (dataWorking.get("valBefore") != null) {
                r = dataWorking.get("valBefore").getAsString();
                r += " %";
            }
        } else if (working == 2) {
            if (dataWorking.get("valAfter") != null) {
                r = dataWorking.get("valAfter").getAsString();
                r += " °C";
            }

        } else if (working == 3 || working == 4) {
            if (dataWorking.get("light_in") != null) {
                r = dataWorking.get("light_in").getAsString();
                r += " Lcx";
            }
        }
        return r;
    }

    private String getValAfter(JsonObject dataWorking, int working) {
        String r = "";
        if (working == 1) {
            if (dataWorking.get("valAfter") != null) {
                r = dataWorking.get("valAfter").getAsString();
                r += " %";
            }
        } else if (working == 2) {
            if (dataWorking.get("valAfter") != null) {
                r = dataWorking.get("valAfter").getAsString();
                r += " °C";
            }

        } else if (working == 3 || working == 4) {
            if (dataWorking.get("light_in") != null) {
                r = dataWorking.get("light_in").getAsString();
                r += " Lcx";
            }
        }
        return r;
    }

    private List<AlarmClockBean> getAlarmClockList() {

        ShareData shareData = new ShareData(this);
        shareData.createSharePreference();

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

    private String getDateTime(long time) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("HH:mm: dd/MM/yyyy");
            Date date = (new Date((time * 1000)));
            return dateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    private String getWorking(int working) {
        if (working == 1) {
            return getString(R.string.water);
        } else if (working == 2) {
            return getString(R.string.foggy);
        } else if (working == 3) {
            return getString(R.string.openSlat);
        } else if (working == 4) {
            return getString(R.string.closeSlat);
        } else {
            return String.valueOf(working);
        }
    }

    /**
     * @param body 1: water not flows, 2: water not true area, 3: temp not decrease, 7: light in not decrease
     * @return
     */
    private String getMessage(String body) {

        if (body.equals("1")) {
            return getResources().getString(R.string.waterFalse);

        } else if (body.equals("2")) {
            return getResources().getString(R.string.noWateringArea);

        } else if (body.equals("3")) {
            return getResources().getString(R.string.tempNotDrop);

        } else if (body.equals("7")) {
            return getResources().getString(R.string.lightNotDrops);

        } else {
            return body;
        }
    }

    private String getTitle(String title) {
        if (title.equals("except")) {
            return getResources().getString(R.string.exception);
        } else {
            return title;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void sendForegroundNotification(String message) {
        Intent showTaskIntent = new Intent(this, MainActivity.class);
        showTaskIntent.setAction(Intent.ACTION_MAIN);
        showTaskIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        showTaskIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent = PendingIntent.getActivities(
                this,
                0,
                new Intent[]{showTaskIntent},
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        Notification notification = new Notification.Builder(this)
                .setContentTitle(getApplicationContext().getString(R.string.app_name))
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(contentIntent)
                .build();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notification);
    }

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getApplicationContext().getString(R.string.app_name))
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

    private void sendNotification2(String title, String messageBody) {


        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getApplicationContext().getString(R.string.app_name))
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

    /**
     * for check activity is running
     */
    public boolean isForeground(String myPackage) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);
        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
        return componentInfo.getPackageName().equals(myPackage);
    }

    public static boolean isBackgroundRunning(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                for (String activeProcess : processInfo.pkgList) {
                    if (activeProcess.equals(context.getPackageName())) {
                        return true;
                    }
                }
            }
        }


        return false;
    }

}
package com.sitthiphong.smartgardencare.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.sitthiphong.smartgardencare.R;
import com.sitthiphong.smartgardencare.activity.MainActivity;
import com.sitthiphong.smartgardencare.listener.ActionListener;

/**
 * Created by Akexorcist on 3/6/2016 AD.
 */
public class GcmDownstreamService extends GcmListenerService {
    private static final String TAG = "DcmDownstreamService";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        // TODO Do something here
        Log.e(TAG, "Message Incoming");
        SharedPreferences sp = getSharedPreferences("Details", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (sp.getString("activity", "").equals("onResume")) {
            Log.e(TAG, "onResume");
            sendNotification2(String.valueOf(data.get("title")), String.valueOf(data.get("body")));

        } else if (sp.getString("activity", "").equals("onStop")) {
            Log.e(TAG, "onResume");
            sendNotification2(String.valueOf(data.get("title")), String.valueOf(data.get("body")));

        } else if (sp.getString("activity", "").equals("onDestroy")) {
            Log.e(TAG, "onResume");
            sendNotification(String.valueOf(data.get("title")), String.valueOf(data.get("body")));
        }


    }

    private void sendNotification(String title, String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
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
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}
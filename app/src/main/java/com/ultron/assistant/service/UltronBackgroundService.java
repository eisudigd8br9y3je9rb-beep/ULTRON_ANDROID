package com.ultron.assistant.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

public class UltronBackgroundService extends Service {

    private static final String CHANNEL_ID =
            "ultron_background";

    private static final int NOTIFICATION_ID =
            7001;

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            Notification notification =
                    new Notification.Builder(
                            this,
                            CHANNEL_ID
                    )
                            .setContentTitle("ULTRON")
                            .setContentText(
                                    "ULTRON background service is active"
                            )
                            .setSmallIcon(
                                    android.R.drawable.ic_dialog_info
                            )
                            .setOngoing(true)
                            .build();

            startForeground(
                    NOTIFICATION_ID,
                    notification
            );
        }
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL_ID,
                            "ULTRON Background",
                            NotificationManager.IMPORTANCE_LOW
                    );

            NotificationManager manager =
                    getSystemService(
                            NotificationManager.class
                    );

            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public int onStartCommand(
            Intent intent,
            int flags,
            int startId
    ) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

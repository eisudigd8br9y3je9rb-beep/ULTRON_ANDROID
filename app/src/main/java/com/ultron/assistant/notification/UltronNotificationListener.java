package com.ultron.assistant.notification;

import android.app.Notification;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

public class UltronNotificationListener
        extends NotificationListenerService {

    private static volatile String latestSender = "";
    private static volatile String latestMessage = "";

    public static String getLatestSender() {
        return latestSender;
    }

    public static String getLatestMessage() {
        return latestMessage;
    }

    public static String getLatest() {
        if (latestMessage == null || latestMessage.trim().isEmpty()) {
            return "";
        }

        if (latestSender == null || latestSender.trim().isEmpty()) {
            return latestMessage;
        }

        return latestSender + ": " + latestMessage;
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (sbn == null) return;

        String pkg = sbn.getPackageName();

        if (!"com.whatsapp".equals(pkg)
                && !"com.whatsapp.w4b".equals(pkg)) {
            return;
        }

        Notification notification = sbn.getNotification();
        if (notification == null || notification.extras == null) return;

        CharSequence title =
                notification.extras.getCharSequence(Notification.EXTRA_TITLE);

        CharSequence text =
                notification.extras.getCharSequence(Notification.EXTRA_TEXT);

        if (text == null) {
            text = notification.extras.getCharSequence(
                    Notification.EXTRA_BIG_TEXT
            );
        }

        latestSender = title == null ? "" : title.toString().trim();
        latestMessage = text == null ? "" : text.toString().trim();
    }
}

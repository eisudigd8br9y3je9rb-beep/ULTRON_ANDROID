package com.ultron.assistant.communication;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

public class CommunicationManager {

    private final Context context;

    public CommunicationManager(Context context) {
        this.context = context;
    }

    public boolean call(String phoneNumber) {

        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }

        if (context.checkSelfPermission(
                android.Manifest.permission.CALL_PHONE
        ) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }

        try {
            Intent intent = new Intent(
                    Intent.ACTION_CALL,
                    Uri.parse("tel:" + Uri.encode(phoneNumber))
            );

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public void openDialer(String phoneNumber) {

        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return;
        }

        Intent intent = new Intent(
                Intent.ACTION_DIAL,
                Uri.parse("tel:" + Uri.encode(phoneNumber))
        );

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void composeSms(
            String phoneNumber,
            String message
    ) {

        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return;
        }

        Intent intent = new Intent(
                Intent.ACTION_SENDTO,
                Uri.parse("smsto:" + Uri.encode(phoneNumber))
        );

        if (message != null) {
            intent.putExtra("sms_body", message);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}

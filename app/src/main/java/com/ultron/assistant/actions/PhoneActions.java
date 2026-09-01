package com.ultron.assistant.actions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

public class PhoneActions {

    private final Context context;

    public PhoneActions(Context context) {
        this.context = context;
    }

    public void openSettings() {
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void goHome() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void searchGoogle(String query) {
        if (query == null || query.trim().isEmpty()) {
            return;
        }

        String url = "https://www.google.com/search?q="
                + Uri.encode(query);

        Intent intent = new Intent(
                Intent.ACTION_VIEW,
                Uri.parse(url)
        );

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}

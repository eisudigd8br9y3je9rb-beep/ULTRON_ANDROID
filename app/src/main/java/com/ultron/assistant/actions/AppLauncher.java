package com.ultron.assistant.actions;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

public class AppLauncher {

    private final Context context;

    public AppLauncher(Context context) {
        this.context = context;
    }

    public boolean openPackage(String packageName) {
        try {
            PackageManager packageManager = context.getPackageManager();

            Intent intent =
                    packageManager.getLaunchIntentForPackage(packageName);

            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean openYouTube() {
        return openPackage("com.google.android.youtube");
    }

    public boolean openInstagram() {
        return openPackage("com.instagram.android");
    }

    public boolean openPUBG() {
        if (openPackage("com.tencent.ig")) {
            return true;
        }

        if (openPackage("com.pubg.imobile")) {
            return true;
        }

        return false;
    }
}

package com.ultron.assistant.actions;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

public class AppLauncher {

    private final Context context;

    public AppLauncher(Context context) {
        this.context = context;
    }

    public boolean openPackage(String packageName) {

        try {

            PackageManager packageManager =
                    context.getPackageManager();

            Intent intent =
                    packageManager.getLaunchIntentForPackage(
                            packageName
                    );

            if (intent != null) {

                intent.addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK
                );

                context.startActivity(intent);

                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean openYouTube() {

        if (openPackage("com.google.android.youtube")) {
            return true;
        }

        try {

            Intent intent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com")
            );

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
            );

            context.startActivity(intent);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean openInstagram() {

        if (openPackage("com.instagram.android")) {
            return true;
        }

        return false;
    }

    public boolean openChrome() {

        if (openPackage("com.android.chrome")) {
            return true;
        }

        try {
            Intent intent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.google.com")
            );

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
            );

            context.startActivity(intent);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean openWhatsApp() {

        if (openPackage("com.whatsapp")) {
            return true;
        }

        if (openPackage("com.whatsapp.w4b")) {
            return true;
        }

        return false;
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

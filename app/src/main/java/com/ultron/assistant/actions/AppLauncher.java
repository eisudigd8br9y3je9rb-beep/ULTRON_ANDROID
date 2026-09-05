package com.ultron.assistant.actions;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import java.util.Locale;

public class AppLauncher {

    private final Context context;

    public AppLauncher(Context context) {
        this.context = context;
    }

    public boolean openPackage(String packageName) {

        if (packageName == null || packageName.trim().isEmpty()) {
            return false;
        }

        try {
            PackageManager packageManager =
                    context.getPackageManager();

            Intent intent =
                    packageManager.getLaunchIntentForPackage(
                            packageName.trim()
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

    public boolean openAppByName(String appName) {

        if (appName == null || appName.trim().isEmpty()) {
            return false;
        }

        String target =
                normalize(appName);

        PackageManager packageManager =
                context.getPackageManager();

        Intent launcherIntent =
                new Intent(Intent.ACTION_MAIN);

        launcherIntent.addCategory(
                Intent.CATEGORY_LAUNCHER
        );

        try {

            java.util.List<
                    android.content.pm.ResolveInfo
                    > apps =
                    packageManager.queryIntentActivities(
                            launcherIntent,
                            0
                    );

            // Exact app name match
            for (android.content.pm.ResolveInfo app : apps) {

                String label =
                        app.loadLabel(packageManager)
                                .toString();

                String normalizedLabel =
                        normalize(label);

                if (normalizedLabel.equals(target)) {

                    return launchResolveInfo(
                            app,
                            packageManager
                    );
                }
            }

            // App name contains target
            for (android.content.pm.ResolveInfo app : apps) {

                String label =
                        app.loadLabel(packageManager)
                                .toString();

                String normalizedLabel =
                        normalize(label);

                if (normalizedLabel.contains(target)
                        || target.contains(normalizedLabel)) {

                    return launchResolveInfo(
                            app,
                            packageManager
                    );
                }
            }

            // Package name match
            for (android.content.pm.ResolveInfo app : apps) {

                String packageName =
                        app.activityInfo.packageName;

                String normalizedPackage =
                        normalize(packageName);

                if (normalizedPackage.contains(target)) {

                    return launchResolveInfo(
                            app,
                            packageManager
                    );
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean launchResolveInfo(
            android.content.pm.ResolveInfo app,
            PackageManager packageManager
    ) {

        try {

            String packageName =
                    app.activityInfo.packageName;

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

    private String normalize(String text) {

        if (text == null) {
            return "";
        }

        return text
                .toLowerCase(Locale.ROOT)
                .replaceAll(
                        "[^a-z0-9\\u0900-\\u097F]",
                        ""
                )
                .trim();
    }

    public boolean openYouTube() {

        if (openAppByName("youtube")) {
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
            return false;
        }
    }

    public boolean openInstagram() {
        return openAppByName("instagram");
    }

    public boolean openChrome() {

        if (openAppByName("chrome")) {
            return true;
        }

        try {

            Intent intent =
                    new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://www.google.com")
                    );

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
            );

            context.startActivity(intent);

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public boolean openWhatsApp() {

        if (openAppByName("whatsapp")) {
            return true;
        }

        return openPackage("com.whatsapp")
                || openPackage("com.whatsapp.w4b");
    }

    public boolean openPUBG() {

        if (openAppByName("pubg")) {
            return true;
        }

        return openPackage("com.tencent.ig")
                || openPackage("com.pubg.imobile");
    }
}

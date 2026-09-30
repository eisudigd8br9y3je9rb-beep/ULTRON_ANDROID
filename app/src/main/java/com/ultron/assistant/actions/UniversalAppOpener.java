package com.ultron.assistant.actions;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Universal App Opener
 * Koi bhi installed app khol sakta hai — naam se fuzzy match karke
 * Hindi aliases bhi support karta hai
 */
public class UniversalAppOpener {

    private final Context context;
    private final PackageManager packageManager;

    // Hindi aliases — jo commonly bolte hain
    private static final Map<String, String> HINDI_ALIASES = new HashMap<>();
    static {
        HINDI_ALIASES.put("यूट्यूब", "youtube");
        HINDI_ALIASES.put("व्हाट्सएप", "whatsapp");
        HINDI_ALIASES.put("वॉट्सऐप", "whatsapp");
        HINDI_ALIASES.put("इंस्टाग्राम", "instagram");
        HINDI_ALIASES.put("फेसबुक", "facebook");
        HINDI_ALIASES.put("ट्विटर", "twitter");
        HINDI_ALIASES.put("क्रोम", "chrome");
        HINDI_ALIASES.put("सेटिंग", "settings");
        HINDI_ALIASES.put("कैमरा", "camera");
        HINDI_ALIASES.put("गैलरी", "gallery");
        HINDI_ALIASES.put("फोटो", "photos");
        HINDI_ALIASES.put("मैप", "maps");
        HINDI_ALIASES.put("जीमेल", "gmail");
        HINDI_ALIASES.put("म्यूजिक", "music");
        HINDI_ALIASES.put("प्ले स्टोर", "play store");
        HINDI_ALIASES.put("कैलकुलेटर", "calculator");
        HINDI_ALIASES.put("कैलेंडर", "calendar");
        HINDI_ALIASES.put("क्लॉक", "clock");
        HINDI_ALIASES.put("टेलीग्राम", "telegram");
        HINDI_ALIASES.put("इक्वल एआई", "equal ai");
        HINDI_ALIASES.put("इक्वल", "equal ai");
        HINDI_ALIASES.put("कॉल असिस्टेंट", "equal ai");
        HINDI_ALIASES.put("स्नैपचैट", "snapchat");
        HINDI_ALIASES.put("लिंक्डइन", "linkedin");
    }

    public UniversalAppOpener(Context context) {
        this.context = context.getApplicationContext();
        this.packageManager = context.getPackageManager();
    }

    /**
     * Command se app ka naam nikaalo aur kholo
     * Example: "WhatsApp kholo" → "whatsapp" → WhatsApp open
     */
    public boolean openAppByName(String command) {
        if (command == null || command.trim().isEmpty()) return false;

        String text = command.toLowerCase(Locale.getDefault()).trim();

        // Command me se "kholo", "open", "launch", "start" jaise words hata do
        text = text.replace("kholo", "")
                   .replace("khol do", "")
                   .replace("open karo", "")
                   .replace("open", "")
                   .replace("launch", "")
                   .replace("start", "")
                   .replace("chalu karo", "")
                   .replace("खोलो", "")
                   .replace("खोल दो", "")
                   .replace("ओपन करो", "")
                   .replace("चालू करो", "")
                   .trim();

        // Hindi alias check karo
        for (Map.Entry<String, String> entry : HINDI_ALIASES.entrySet()) {
            if (text.contains(entry.getKey())) {
                String englishName = entry.getValue();
                if (tryOpenApp(englishName)) return true;
            }
        }

        // Direct naam se try karo
        if (tryOpenApp(text)) return true;

        // Saare installed apps me fuzzy search
        return tryFuzzyMatch(text);
    }

    /**
     * App ka naam ya package name se kholne ki koshish
     */
    private boolean tryOpenApp(String appName) {
        if (appName == null || appName.isEmpty()) return false;

        // 1. Direct package name match
        Intent intent = packageManager.getLaunchIntentForPackage(appName);
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        }

        // 2. Common package patterns try karo
        String[] commonPrefixes = {"com.", "org.", "net."};
        for (String prefix : commonPrefixes) {
            String pkg = prefix + appName.toLowerCase().replace(" ", "");
            intent = packageManager.getLaunchIntentForPackage(pkg);
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return true;
            }
        }

        // 3. Common apps ke explicit package names
        Map<String, String> commonApps = new HashMap<>();
        commonApps.put("whatsapp", "com.whatsapp");
        commonApps.put("equal ai", "in.equal.ai.assistant");
        commonApps.put("equal", "in.equal.ai.assistant");
        commonApps.put("call assistant", "in.equal.ai.assistant");
        commonApps.put("equal identity", "in.equal.ai.assistant");
        commonApps.put("whatsapp business", "com.whatsapp.w4b");
        commonApps.put("wa business", "com.whatsapp.w4b");
        commonApps.put("youtube", "com.google.android.youtube");
        commonApps.put("instagram", "com.instagram.android");
        commonApps.put("facebook", "com.facebook.katana");
        commonApps.put("chrome", "com.android.chrome");
        commonApps.put("gmail", "com.google.android.gm");
        commonApps.put("maps", "com.google.android.apps.maps");
        commonApps.put("play store", "com.android.vending");
        commonApps.put("google play", "com.android.vending");
        commonApps.put("settings", "com.android.settings");
        commonApps.put("camera", "com.android.camera");
        commonApps.put("gallery", "com.android.gallery3d");
        commonApps.put("photos", "com.google.android.apps.photos");
        commonApps.put("telegram", "org.telegram.messenger");
        commonApps.put("snapchat", "com.snapchat.android");
        commonApps.put("twitter", "com.twitter.android");
        commonApps.put("linkedin", "com.linkedin.android");
        commonApps.put("gpay", "com.google.android.apps.nbu.paisa.user");
        commonApps.put("phonepe", "com.phonepe.app");
        commonApps.put("paytm", "net.one97.paytm");
        commonApps.put("amazon", "in.amazon.mShop.android.shopping");
        commonApps.put("flipkart", "com.flipkart.android");
        commonApps.put("hotstar", "in.startv.hotstar");
        commonApps.put("netflix", "com.netflix.mediaclient");
        commonApps.put("spotify", "com.spotify.music");
        commonApps.put("zomato", "com.application.zomato");
        commonApps.put("swiggy", "in.swiggy.android");

        String pkg = commonApps.get(appName);
        if (pkg != null) {
            intent = packageManager.getLaunchIntentForPackage(pkg);
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return true;
            }
        }

        return false;
    }

    /**
     * Saare installed apps me fuzzy search
     */
    private boolean tryFuzzyMatch(String query) {
        if (query == null || query.length() < 2) return false;

        try {
            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> apps = packageManager.queryIntentActivities(mainIntent, 0);

            String queryClean = query.replace(" ", "").toLowerCase();

            // Exact match
            for (ResolveInfo info : apps) {
                String label = info.loadLabel(packageManager).toString()
                                   .toLowerCase().replace(" ", "");
                String pkgName = info.activityInfo.packageName.toLowerCase();

                if (label.equals(queryClean) || pkgName.contains(queryClean)) {
                    return launchApp(info.activityInfo.packageName);
                }
            }

            // Partial match
            for (ResolveInfo info : apps) {
                String label = info.loadLabel(packageManager).toString()
                                   .toLowerCase().replace(" ", "");
                String pkgName = info.activityInfo.packageName.toLowerCase();

                if (label.contains(queryClean) || pkgName.contains(queryClean)
                        || queryClean.contains(label)) {
                    return launchApp(info.activityInfo.packageName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean launchApp(String packageName) {
        try {
            Intent intent = packageManager.getLaunchIntentForPackage(packageName);
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
}

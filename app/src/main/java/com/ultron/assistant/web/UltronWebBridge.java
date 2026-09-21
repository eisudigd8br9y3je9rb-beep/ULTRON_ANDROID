package com.ultron.assistant.web;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.BatteryManager;
import android.provider.Settings;
import android.webkit.JavascriptInterface;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UltronWebBridge {

    public interface CommandListener {
        void onCommand(String command);
        void onAsk(String text);
    }

    private final Context context;
    private final CommandListener listener;
    private static long prevIdle = -1;
    private static long prevTotal = -1;

    public UltronWebBridge(Context context, CommandListener listener) {
        this.context = context.getApplicationContext();
        this.listener = listener;
    }

    @JavascriptInterface
    public String getBattery() {
        try {
            IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent battery = context.registerReceiver(null, filter);

            if (battery == null) return "--%";

            int level = battery.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = battery.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            if (level < 0 || scale <= 0) return "--%";

            return Math.round((level * 100f) / scale) + "%";
        } catch (Exception e) {
            return "--%";
        }
    }

    @JavascriptInterface
    public String getBrightness() {
        try {
            int brightness = Settings.System.getInt(
                    context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS
            );

            int percent = Math.round((brightness / 255f) * 100f);
            return Math.max(0, Math.min(100, percent)) + "%";
        } catch (Exception e) {
            return "--%";
        }
    }

    @JavascriptInterface
    public String getNetwork() {
        try {
            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm == null) return "OFFLINE";
            Network network = cm.getActiveNetwork();
            if (network == null) return "OFFLINE";
            NetworkCapabilities caps = cm.getNetworkCapabilities(network);
            if (caps == null) return "OFFLINE";
            if (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) return "Wi-Fi";
            if (caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) return "Ethernet";
            if (caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) return "4G";
            return "ONLINE";
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }

    @JavascriptInterface
    public synchronized String getCpu() {
        try {
            String line = new java.io.BufferedReader(
                    new java.io.FileReader("/proc/stat")).readLine();
            if (line == null || !line.startsWith("cpu")) return "--%";
            String[] p = line.trim().split("\s+");
            long user=Long.parseLong(p[1]), nice=Long.parseLong(p[2]), sys=Long.parseLong(p[3]);
            long idle=Long.parseLong(p[4]), iowait=p.length>5?Long.parseLong(p[5]):0;
            long total=user+nice+sys+idle+iowait;
            if (prevTotal < 0) { prevTotal=total; prevIdle=idle+iowait; return "--%"; }
            long dt=total-prevTotal, di=(idle+iowait)-prevIdle;
            prevTotal=total; prevIdle=idle+iowait;
            if (dt<=0) return "0%";
            int usage=(int)Math.round((1.0-(double)di/dt)*100.0);
            return Math.max(0,Math.min(100,usage))+"%";
        } catch(Exception e) { return "--%"; }
    }

    @JavascriptInterface
    public String getMemory() {
        try {
            ActivityManager am=(ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
            if(am==null) return "--%";
            ActivityManager.MemoryInfo mi=new ActivityManager.MemoryInfo();
            am.getMemoryInfo(mi);
            long used=mi.totalMem-mi.availMem;
            return Math.max(0,Math.min(100,Math.round((used*100f)/mi.totalMem)))+"%";
        } catch(Exception e) { return "--%"; }
    }

    @JavascriptInterface
    public String getTime() {
        return new SimpleDateFormat(
                "hh:mm a",
                Locale.getDefault()
        ).format(new Date());
    }

    @JavascriptInterface
    public void command(String command) {
        if (listener != null && command != null) {
            listener.onCommand(command);
        }
    }

    @JavascriptInterface
    public void ask(String text) {
        if (listener != null && text != null && !text.trim().isEmpty()) {
            listener.onAsk(text.trim());
        }
    }
}

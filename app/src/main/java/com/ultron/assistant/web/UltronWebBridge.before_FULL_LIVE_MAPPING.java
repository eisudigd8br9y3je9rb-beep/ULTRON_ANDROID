package com.ultron.assistant.web;

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

            if (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return "Wi-Fi";
            }

            if (caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                if (caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)) {
                    return "Mobile";
                }
                return "Mobile";
            }

            if (caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                return "Ethernet";
            }

            return "ONLINE";
        } catch (Exception e) {
            return "UNKNOWN";
        }
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

package com.ultron.assistant.core;

import android.content.Context;
import android.content.SharedPreferences;

public class ActivationManager {

    private static final String PREFS = "ultron_activation";
    private static final String KEY_ACTIVE = "ultron_active";
    private static final long PENDING_TIMEOUT_MS = 2 * 60 * 1000;

    private final Context context;
    private long pendingSince = 0;

    public ActivationManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public boolean isActive() {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getBoolean(KEY_ACTIVE, false);
    }

    public void setActive(boolean active) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putBoolean(KEY_ACTIVE, active).apply();
    }

    public void triggerActivation() {
        pendingSince = System.currentTimeMillis();
    }

    public boolean confirmActivation() {
        if (isActive()) return true;
        if (pendingSince == 0) return false;
        long elapsed = System.currentTimeMillis() - pendingSince;
        if (elapsed > PENDING_TIMEOUT_MS) {
            pendingSince = 0;
            return false;
        }
        setActive(true);
        pendingSince = 0;
        return true;
    }

    public boolean isPending() {
        if (pendingSince == 0) return false;
        return (System.currentTimeMillis() - pendingSince) <= PENDING_TIMEOUT_MS;
    }

    public long getPendingSecondsLeft() {
        if (pendingSince == 0) return 0;
        long elapsed = System.currentTimeMillis() - pendingSince;
        long left = PENDING_TIMEOUT_MS - elapsed;
        return left > 0 ? left / 1000 : 0;
    }

    public void reset() {
        setActive(false);
        pendingSince = 0;
    }
}

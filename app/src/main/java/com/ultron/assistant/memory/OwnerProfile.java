package com.ultron.assistant.memory;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * ULTRON's persistent memory store.
 *
 * Currently holds only the owner's name, saved with SharedPreferences so it
 * survives the app being closed and reopened, and is editable at runtime via
 * a voice command instead of being hardcoded in MainActivity.
 *
 * Deliberately minimal: any class with a Context can read or update it, and
 * it can be extended later with more remembered facts without touching
 * MainActivity's UI code.
 */
public class OwnerProfile {

    private static final String PREFS_NAME = "ultron_memory";
    private static final String KEY_OWNER_NAME = "owner_name";
    private static final String DEFAULT_OWNER_NAME = "Imtiyaz";

    private final SharedPreferences prefs;

    public OwnerProfile(Context context) {
        this.prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public String getOwnerName() {

        String name = prefs.getString(KEY_OWNER_NAME, DEFAULT_OWNER_NAME);

        return (name == null || name.trim().isEmpty())
                ? DEFAULT_OWNER_NAME
                : name.trim();
    }

    public void setOwnerName(String name) {

        if (name == null || name.trim().isEmpty()) {
            return;
        }

        prefs.edit()
                .putString(KEY_OWNER_NAME, name.trim())
                .apply();
    }
}

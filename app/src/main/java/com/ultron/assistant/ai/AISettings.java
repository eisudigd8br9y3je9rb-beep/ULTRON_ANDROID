package com.ultron.assistant.ai;

import android.content.Context;
import android.content.SharedPreferences;

public final class AISettings {

    private static final String PREFS = "ultron_ai_settings";
    private static final String KEY_ENDPOINT = "ai_endpoint";
    private static final String KEY_API_KEY = "ai_api_key";

    private AISettings() {}

    public static void saveEndpoint(Context context, String endpoint) {
        if (context == null || endpoint == null) return;

        String value = endpoint.trim();
        if (value.isEmpty()) return;

        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_ENDPOINT, value)
                .apply();
    }

    public static void saveApiKey(Context context, String apiKey) {
        if (context == null || apiKey == null) return;

        String value = apiKey.trim();
        if (value.isEmpty()) return;

        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_API_KEY, value)
                .apply();
    }

    public static String getEndpoint(Context context) {
        if (context == null) {
            return AIConfig.DEFAULT_ENDPOINT;
        }

        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getString(KEY_ENDPOINT, AIConfig.DEFAULT_ENDPOINT);
    }

    public static String getApiKey(Context context) {
        if (context == null) return "";

        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getString(KEY_API_KEY, "");
    }

    public static boolean isConfigured(Context context) {
        return !getApiKey(context).isEmpty();
    }

    public static void clear(Context context) {
        if (context == null) return;

        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply();
    }
}

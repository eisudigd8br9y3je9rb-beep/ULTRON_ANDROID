package com.ultron.assistant.memory;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

public class MemoryManager {

    private static final String PREF_NAME = "ultron_memory";
    private static final String KEY_MEMORY = "conversation_memory";
    private static final int MAX_TURNS = 10;

    private final SharedPreferences preferences;

    public MemoryManager(Context context) {
        preferences = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void addConversation(String user, String assistant) {
        try {
            JSONArray history = new JSONArray(
                    preferences.getString(KEY_MEMORY, "[]")
            );

            JSONObject turn = new JSONObject();
            turn.put("user", user == null ? "" : user.trim());
            turn.put("assistant", assistant == null ? "" : assistant.trim());

            history.put(turn);

            while (history.length() > MAX_TURNS) {
                JSONArray newHistory = new JSONArray();

                for (int i = 1; i < history.length(); i++) {
                    newHistory.put(history.getJSONObject(i));
                }

                history = newHistory;
            }

            preferences.edit()
                    .putString(KEY_MEMORY, history.toString())
                    .apply();

        } catch (Exception ignored) {
        }
    }

    public String getLastUser() {
        try {
            JSONArray history = new JSONArray(
                    preferences.getString(KEY_MEMORY, "[]")
            );

            if (history.length() == 0) {
                return "";
            }

            JSONObject last = history.getJSONObject(history.length() - 1);
            return last.optString("user", "").trim();

        } catch (Exception e) {
            return "";
        }
    }

    public String getLastAssistant() {
        try {
            JSONArray history = new JSONArray(
                    preferences.getString(KEY_MEMORY, "[]")
            );

            if (history.length() == 0) {
                return "";
            }

            JSONObject last = history.getJSONObject(history.length() - 1);
            return last.optString("assistant", "").trim();

        } catch (Exception e) {
            return "";
        }
    }

    public String getRecentContext() {
        try {
            JSONArray history = new JSONArray(
                    preferences.getString(KEY_MEMORY, "[]")
            );

            StringBuilder context = new StringBuilder();

            for (int i = 0; i < history.length(); i++) {
                JSONObject turn = history.getJSONObject(i);

                context.append("User: ")
                        .append(turn.optString("user"))
                        .append("\n");

                context.append("ULTRON: ")
                        .append(turn.optString("assistant"))
                        .append("\n");
            }

            return context.toString().trim();

        } catch (Exception e) {
            return "";
        }
    }

    public void clearMemory() {
        preferences.edit()
                .remove(KEY_MEMORY)
                .apply();
    }
}

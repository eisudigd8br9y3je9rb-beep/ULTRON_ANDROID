package com.ultron.assistant.ai;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AIClient {

    public interface Callback {
        void onSuccess(String answer);
        void onError(String error);
    }

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private String endpoint = AIConfig.DEFAULT_ENDPOINT;
    private String apiKey = "";

    public AIClient() {}

    public AIClient(Context context) {
        configure(context);
    }

    public void configure(Context context) {
        if (context == null) return;
        setEndpoint(AISettings.getEndpoint(context));
        setApiKey(AISettings.getApiKey(context));
    }

    public void setEndpoint(String endpoint) {
        if (endpoint != null && !endpoint.trim().isEmpty()) {
            this.endpoint = endpoint.trim();
        }
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey == null ? "" : apiKey.trim();
    }

    public boolean isConfigured() {
        return !apiKey.isEmpty();
    }

    public void ask(String systemPrompt, String userMessage, String memoryContext, Callback callback) {
        executor.execute(() -> {
            if (!isConfigured()) {
                postError(callback, "AI brain is not configured yet. Add the API key securely.");
                return;
            }

            HttpURLConnection connection = null;
            try {
                URL url = new URL(endpoint);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setConnectTimeout(AIConfig.CONNECT_TIMEOUT_MS);
                connection.setReadTimeout(AIConfig.READ_TIMEOUT_MS);
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty(AIConfig.AUTH_HEADER, apiKey);

                // Native Gemini format
                StringBuilder fullPrompt = new StringBuilder();
                if (systemPrompt != null && !systemPrompt.trim().isEmpty()) {
                    fullPrompt.append(systemPrompt).append("\n\n");
                }
                if (memoryContext != null && !memoryContext.trim().isEmpty()) {
                    fullPrompt.append("Recent conversation:\n").append(memoryContext).append("\n\n");
                }
                fullPrompt.append("User: ").append(userMessage == null ? "" : userMessage.trim());

                JSONObject body = new JSONObject();
                JSONArray contents = new JSONArray();
                JSONObject contentObj = new JSONObject();
                JSONArray parts = new JSONArray();
                JSONObject part = new JSONObject();
                part.put("text", fullPrompt.toString());
                parts.put(part);
                contentObj.put("parts", parts);
                contents.put(contentObj);
                body.put("contents", contents);

                byte[] data = body.toString().getBytes(StandardCharsets.UTF_8);
                try (OutputStream output = connection.getOutputStream()) {
                    output.write(data);
                }

                int code = connection.getResponseCode();
                InputStream stream = (code >= 200 && code < 300) ? connection.getInputStream() : connection.getErrorStream();
                String response = readStream(stream);

                if (code < 200 || code >= 300) {
                    postError(callback, "AI server error: HTTP " + code + " - " + response);
                    return;
                }

                JSONObject json = new JSONObject(response);
                JSONArray candidates = json.optJSONArray("candidates");
                if (candidates == null || candidates.length() == 0) {
                    postError(callback, "AI returned no answer.");
                    return;
                }

                JSONObject candidate = candidates.getJSONObject(0);
                JSONObject content = candidate.optJSONObject("content");
                if (content == null) {
                    postError(callback, "AI returned empty content.");
                    return;
                }

                JSONArray responseParts = content.optJSONArray("parts");
                if (responseParts == null || responseParts.length() == 0) {
                    postError(callback, "AI returned empty parts.");
                    return;
                }

                StringBuilder answerBuilder = new StringBuilder();
                for (int i = 0; i < responseParts.length(); i++) {
                    JSONObject p = responseParts.getJSONObject(i);
                    String t = p.optString("text", "");
                    if (!t.isEmpty()) answerBuilder.append(t);
                }

                String answer = answerBuilder.toString().trim();
                if (answer.isEmpty()) {
                    postError(callback, "AI returned an empty answer.");
                    return;
                }

                postSuccess(callback, answer);

            } catch (Exception e) {
                postError(callback, "AI connection failed: " + e.getClass().getSimpleName());
            } finally {
                if (connection != null) connection.disconnect();
            }
        });
    }

    private String readStream(InputStream stream) throws Exception {
        if (stream == null) return "";
        StringBuilder result = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        }
        return result.toString();
    }

    private void postSuccess(Callback callback, String answer) {
        if (callback == null) return;
        mainHandler.post(() -> callback.onSuccess(answer));
    }

    private void postError(Callback callback, String error) {
        if (callback == null) return;
        mainHandler.post(() -> callback.onError(error));
    }

    public void shutdown() {
        executor.shutdownNow();
    }
}

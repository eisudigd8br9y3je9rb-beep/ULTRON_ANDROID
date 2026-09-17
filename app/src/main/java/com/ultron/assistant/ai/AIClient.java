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

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    private final Handler mainHandler =
            new Handler(Looper.getMainLooper());

    private String endpoint = AIConfig.DEFAULT_ENDPOINT;
    private String apiKey = "";

    public AIClient() {
    }

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

    public void ask(
            String systemPrompt,
            String userMessage,
            String memoryContext,
            Callback callback
    ) {
        executor.execute(() -> {

            if (!isConfigured()) {
                postError(callback,
                        "AI brain is not configured yet. Add the API key securely.");
                return;
            }

            HttpURLConnection connection = null;

            try {
                URL url = new URL(endpoint);
                connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setConnectTimeout(
                        AIConfig.CONNECT_TIMEOUT_MS
                );
                connection.setReadTimeout(
                        AIConfig.READ_TIMEOUT_MS
                );
                connection.setDoOutput(true);

                connection.setRequestProperty(
                        "Content-Type",
                        "application/json"
                );

                connection.setRequestProperty(
                        "Authorization",
                        "Bearer " + apiKey
                );

                JSONArray messages = new JSONArray();

                JSONObject system = new JSONObject();
                system.put(
                        "role",
                        "system"
                );
                system.put(
                        "content",
                        systemPrompt == null
                                ? "You are ULTRON, a helpful personal Android assistant."
                                : systemPrompt
                );
                messages.put(system);

                if (memoryContext != null
                        && !memoryContext.trim().isEmpty()) {

                    JSONObject memory = new JSONObject();

                    memory.put(
                            "role",
                            "system"
                    );

                    memory.put(
                            "content",
                            "Recent conversation context:\n"
                                    + memoryContext
                    );

                    messages.put(memory);
                }

                JSONObject user = new JSONObject();

                user.put(
                        "role",
                        "user"
                );

                user.put(
                        "content",
                        userMessage == null
                                ? ""
                                : userMessage.trim()
                );

                messages.put(user);

                JSONObject body = new JSONObject();

                body.put(
                        "model",
                        AIConfig.MODEL
                );

                body.put(
                        "messages",
                        messages
                );

                body.put(
                        "temperature",
                        0.4
                );

                byte[] data =
                        body.toString()
                                .getBytes(StandardCharsets.UTF_8);

                try (OutputStream output =
                             connection.getOutputStream()) {

                    output.write(data);
                }

                int code =
                        connection.getResponseCode();

                InputStream stream =
                        code >= 200 && code < 300
                                ? connection.getInputStream()
                                : connection.getErrorStream();

                String response =
                        readStream(stream);

                if (code < 200 || code >= 300) {
                    postError(
                            callback,
                            "AI server error: HTTP " + code
                    );
                    return;
                }

                JSONObject json =
                        new JSONObject(response);

                JSONArray choices =
                        json.optJSONArray("choices");

                if (choices == null
                        || choices.length() == 0) {

                    postError(
                            callback,
                            "AI returned no answer."
                    );
                    return;
                }

                JSONObject choice =
                        choices.getJSONObject(0);

                JSONObject message =
                        choice.optJSONObject("message");

                String answer =
                        message == null
                                ? ""
                                : message.optString(
                                        "content",
                                        ""
                                ).trim();

                if (answer.isEmpty()) {
                    postError(
                            callback,
                            "AI returned an empty answer."
                    );
                    return;
                }

                postSuccess(callback, answer);

            } catch (Exception e) {

                postError(
                        callback,
                        "AI connection failed: "
                                + e.getClass().getSimpleName()
                );

            } finally {

                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    private String readStream(InputStream stream)
            throws Exception {

        if (stream == null) {
            return "";
        }

        StringBuilder result =
                new StringBuilder();

        try (BufferedReader reader =
                     new BufferedReader(
                             new InputStreamReader(
                                     stream,
                                     StandardCharsets.UTF_8
                             )
                     )) {

            String line;

            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        }

        return result.toString();
    }

    private void postSuccess(
            Callback callback,
            String answer
    ) {
        if (callback == null) return;

        mainHandler.post(
                () -> callback.onSuccess(answer)
        );
    }

    private void postError(
            Callback callback,
            String error
    ) {
        if (callback == null) return;

        mainHandler.post(
                () -> callback.onError(error)
        );
    }

    public void shutdown() {
        executor.shutdownNow();
    }
}

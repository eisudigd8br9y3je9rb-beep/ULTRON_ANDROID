package com.ultron.assistant.ai;

public final class AIConfig {

    private AIConfig() {}

    public static final String DEFAULT_ENDPOINT =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.8-flash:generateContent";

    public static final String MODEL =
            "gemini-3.8-flash";

    public static final String AUTH_HEADER = "x-goog-api-key";

    public static final int CONNECT_TIMEOUT_MS = 15000;
    public static final int READ_TIMEOUT_MS = 30000;
}

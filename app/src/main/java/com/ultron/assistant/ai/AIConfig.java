package com.ultron.assistant.ai;

public final class AIConfig {

    private AIConfig() {}

    /*
     * OpenAI-compatible chat endpoint.
     * Keep the API key OUT of source code.
     *
     * Example endpoint:
     * https://generativelanguage.googleapis.com/v1beta/openai/chat/completions
     *
     * Configure these values later through a secure settings/backend layer.
     */
    public static final String DEFAULT_ENDPOINT =
            "https://generativelanguage.googleapis.com/v1beta/openai/chat/completions";

    public static final String MODEL =
            "gemini-2.0-flash";

    public static final int CONNECT_TIMEOUT_MS = 15000;
    public static final int READ_TIMEOUT_MS = 30000;
}

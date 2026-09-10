package com.ultron.assistant.voice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import java.util.ArrayList;
import java.util.Locale;

public class VoiceManager {

    public interface VoiceCallback {
        void onStatus(String status);
        void onResult(String text);
        void onError(int errorCode);
    }

    private final Context context;
    private SpeechRecognizer speechRecognizer;
    private final VoiceCallback callback;
    private boolean resultDelivered = false;

    // Continuous listening support
    private final Handler restartHandler =
            new Handler(Looper.getMainLooper());

    private boolean continuousListening = false;
    private boolean destroyed = false;

    public VoiceManager(Context context, VoiceCallback callback) {
        this.context = context;
        this.callback = callback;
    }

    public void startListening() {

        continuousListening = true;
        destroyed = false;
        restartHandler.removeCallbacksAndMessages(null);

        if (speechRecognizer != null) {
            try {
                speechRecognizer.cancel();
                speechRecognizer.destroy();
            } catch (Exception ignored) {
            }

            speechRecognizer = null;
        }

        resultDelivered = false;

        speechRecognizer =
                SpeechRecognizer.createSpeechRecognizer(context);

        Intent intent =
                new Intent(
                        RecognizerIntent.ACTION_RECOGNIZE_SPEECH
                );

        intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        );

        intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                "hi-IN"
        );

        intent.putExtra(
                RecognizerIntent.EXTRA_MAX_RESULTS,
                5
        );

        intent.putExtra(
                RecognizerIntent.EXTRA_PARTIAL_RESULTS,
                true
        );

        speechRecognizer.setRecognitionListener(
                new RecognitionListener() {

                    @Override
                    public void onReadyForSpeech(
                            Bundle params
                    ) {

                        if (callback != null) {
                            callback.onStatus(
                                    "Listening... Speak now"
                            );
                        }
                    }

                    @Override
                    public void onBeginningOfSpeech() {

                        if (callback != null) {
                            callback.onStatus(
                                    "I can hear you..."
                            );
                        }
                    }

                    @Override
                    public void onRmsChanged(float rmsdB) {
                    }

                    @Override
                    public void onBufferReceived(
                            byte[] buffer
                    ) {
                    }

                    @Override
                    public void onEndOfSpeech() {

                        if (callback != null) {
                            callback.onStatus(
                                    "Processing voice command..."
                            );
                        }
                    }

                    @Override
                    public void onError(int error) {

                        if (resultDelivered) {
                            return;
                        }

                        if (callback != null) {

                            String message;

                            switch (error) {

                                case SpeechRecognizer.ERROR_AUDIO:
                                    message =
                                            "Voice error: microphone problem";
                                    break;

                                case SpeechRecognizer.ERROR_CLIENT:
                                    message =
                                            "Voice error: client error";
                                    break;

                                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                                    message =
                                            "Voice error: microphone permission";
                                    break;

                                case SpeechRecognizer.ERROR_NETWORK:
                                    message =
                                            "Voice error: network problem";
                                    break;

                                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                                    message =
                                            "Voice error: network timeout";
                                    break;

                                case SpeechRecognizer.ERROR_NO_MATCH:
                                    message =
                                            "I could not understand. Try again.";
                                    break;

                                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                                    message =
                                            "Voice recognizer is busy. Try again.";
                                    break;

                                case SpeechRecognizer.ERROR_SERVER:
                                    message =
                                            "Voice recognition server error";
                                    break;

                                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                                    message =
                                            "No speech detected";
                                    break;

                                default:
                                    message =
                                            "Voice error: " + error;
                                    break;
                            }

                            callback.onStatus(message);
                            callback.onError(error);

                            // Restart listening after recoverable errors
                            if (continuousListening
                                    && !destroyed
                                    && error != SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS
                                    && error != SpeechRecognizer.ERROR_CLIENT) {

                                scheduleRestart();
                            }
                        }
                    }

                    @Override
                    public void onResults(Bundle results) {

                        if (results == null) {

                            if (callback != null) {
                                callback.onStatus(
                                        "No voice result received"
                                );
                            }

                            return;
                        }

                        ArrayList<String> matches =
                                results.getStringArrayList(
                                        SpeechRecognizer.RESULTS_RECOGNITION
                                );

                        if (matches != null
                                && !matches.isEmpty()
                                && matches.get(0) != null
                                && !matches.get(0).trim().isEmpty()) {

                            String command =
                                    matches.get(0).trim();

                            if (callback != null) {

                                callback.onStatus(
                                        "Recognized: " + command
                                );

                                resultDelivered = true;
                                callback.onResult(command);
                                // MainActivity restarts listening only after TTS finishes.
                                // Do NOT restart here, otherwise ULTRON hears its own voice.
                            }

                        } else {

                            if (callback != null) {

                                callback.onStatus(
                                        "No command recognized"
                                );

                                callback.onError(
                                        SpeechRecognizer.ERROR_NO_MATCH
                                );
                            }
                        }
                    }

                    @Override
                    public void onPartialResults(
                            Bundle partialResults
                    ) {

                        if (partialResults == null
                                || callback == null) {
                            return;
                        }

                        ArrayList<String> partial =
                                partialResults.getStringArrayList(
                                        SpeechRecognizer.RESULTS_RECOGNITION
                                );

                        if (partial != null
                                && !partial.isEmpty()
                                && partial.get(0) != null) {

                            callback.onStatus(
                                    "Hearing: "
                                            + partial.get(0)
                            );
                        }
                    }

                    @Override
                    public void onEvent(
                            int eventType,
                            Bundle params
                    ) {
                    }
                }
        );

        try {

            if (callback != null) {
                callback.onStatus(
                        "Starting voice recognition..."
                );
            }

            speechRecognizer.startListening(intent);

        } catch (Exception e) {

            if (callback != null) {

                callback.onStatus(
                        "Could not start voice recognition"
                );
            }
        }
    }

    private void scheduleRestart() {

        if (!continuousListening || destroyed) {
            return;
        }

        restartHandler.removeCallbacksAndMessages(null);

        restartHandler.postDelayed(
                () -> {
                    if (continuousListening && !destroyed) {
                        startListening();
                    }
                },
                700
        );
    }

    public void stopListening() {

        // Completely stop continuous listening
        continuousListening = false;
        restartHandler.removeCallbacksAndMessages(null);

        if (speechRecognizer != null) {

            try {
                speechRecognizer.stopListening();
            } catch (Exception ignored) {
            }
        }
    }

    public void destroy() {

        // Permanently stop continuous listening
        destroyed = true;
        continuousListening = false;
        restartHandler.removeCallbacksAndMessages(null);

        if (speechRecognizer != null) {

            try {
                speechRecognizer.cancel();
                speechRecognizer.destroy();
            } catch (Exception ignored) {
            }

            speechRecognizer = null;
        }
    }
}

package com.ultron.assistant.voice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import java.util.ArrayList;

public class VoiceManager {

    public interface VoiceCallback {
        void onStatus(String status);
        void onResult(String text);
        void onError(int errorCode);
    }

    private final Context context;
    private final VoiceCallback callback;

    private SpeechRecognizer speechRecognizer;
    private boolean listening = false;
    private boolean destroyed = false;
    private boolean resultDelivered = false;

    public VoiceManager(Context context, VoiceCallback callback) {
        this.context = context.getApplicationContext();
        this.callback = callback;
    }

    public void startListening() {

        if (destroyed) return;

        listening = true;
        resultDelivered = false;

        destroyRecognizerOnly();

        speechRecognizer =
                SpeechRecognizer.createSpeechRecognizer(context);

        Intent intent =
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        // Use Android's normal free-form speech recognition.
        // Do not force a specific language here.
        intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        );

        // Prefer Hindi recognition. Android may still recognize
        // English words/commands through the installed speech service.
        intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "hi-IN"
        );

        // Use the normal Android speech service. Do not force
        // offline recognition because the device may not have
        // an offline language pack.
        intent.putExtra(
                RecognizerIntent.EXTRA_PREFER_OFFLINE,
                false
        );

        intent.putExtra(
                RecognizerIntent.EXTRA_MAX_RESULTS,
                5
        );

        intent.putExtra(
                RecognizerIntent.EXTRA_PARTIAL_RESULTS,
                false
        );

        speechRecognizer.setRecognitionListener(
                new RecognitionListener() {

                    @Override
                    public void onReadyForSpeech(Bundle params) {
                        if (listening && callback != null) {
                            callback.onStatus("Listening... Speak now");
                        }
                    }

                    @Override
                    public void onBeginningOfSpeech() {
                        if (callback != null) {
                            callback.onStatus("I can hear you...");
                        }
                    }

                    @Override
                    public void onRmsChanged(float rmsdB) {
                    }

                    @Override
                    public void onBufferReceived(byte[] buffer) {
                    }

                    @Override
                    public void onEndOfSpeech() {
                        if (callback != null) {
                            callback.onStatus("Processing...");
                        }
                    }

                    @Override
                    public void onError(int error) {

                        if (resultDelivered || destroyed) return;

                        // IMPORTANT:
                        // Never automatically restart here.
                        // MainActivity starts a new recognition session
                        // only after ULTRON finishes speaking.
                        listening = false;

                        if (callback != null) {

                            String message;

                            switch (error) {

                                case SpeechRecognizer.ERROR_NO_MATCH:
                                    message =
                                            "I could not understand. Tap Start Voice Command and try again.";
                                    break;

                                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                                    message =
                                            "No speech detected. Tap Start Voice Command and try again.";
                                    break;

                                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                                    message =
                                            "Microphone permission is required.";
                                    break;

                                case SpeechRecognizer.ERROR_NETWORK:
                                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                                    message =
                                            "Speech service network error. Check Google speech recognition and internet, then tap Start Voice Command again.";
                                    break;

                                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                                    message =
                                            "Voice recognizer is busy. Try again.";
                                    break;

                                default:
                                    message =
                                            "Voice recognition error. Try again.";
                                    break;
                            }

                            callback.onStatus(message);
                            callback.onError(error);
                        }
                    }

                    @Override
                    public void onResults(Bundle results) {

                        if (!listening || destroyed || resultDelivered) {
                            return;
                        }

                        resultDelivered = true;
                        listening = false;

                        ArrayList<String> matches =
                                results == null
                                        ? null
                                        : results.getStringArrayList(
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
                                callback.onResult(command);
                            }

                        } else {

                            if (callback != null) {
                                callback.onStatus(
                                        "No command recognized. Tap Start Voice Command and try again."
                                );
                                callback.onError(
                                        SpeechRecognizer.ERROR_NO_MATCH
                                );
                            }
                        }
                    }

                    @Override
                    public void onPartialResults(Bundle partialResults) {
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
                callback.onStatus("Starting voice recognition...");
            }

            speechRecognizer.startListening(intent);

        } catch (Exception e) {

            listening = false;

            if (callback != null) {
                callback.onStatus(
                        "Could not start voice recognition."
                );
                callback.onError(
                        SpeechRecognizer.ERROR_CLIENT
                );
            }
        }
    }

    public void stopListening() {

        listening = false;

        if (speechRecognizer != null) {
            try {
                speechRecognizer.cancel();
            } catch (Exception ignored) {
            }
        }
    }

    private void destroyRecognizerOnly() {

        if (speechRecognizer != null) {
            try {
                speechRecognizer.cancel();
                speechRecognizer.destroy();
            } catch (Exception ignored) {
            }

            speechRecognizer = null;
        }
    }

    public void destroy() {

        destroyed = true;
        listening = false;
        destroyRecognizerOnly();
    }
}

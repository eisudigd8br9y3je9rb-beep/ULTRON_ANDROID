package com.ultron.assistant.voice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
    private VoiceCallback callback;

    public VoiceManager(Context context, VoiceCallback callback) {
        this.context = context;
        this.callback = callback;
    }

    public void startListening() {

        if (speechRecognizer != null) {
            speechRecognizer.destroy();
            speechRecognizer = null;
        }

        speechRecognizer =
                SpeechRecognizer.createSpeechRecognizer(context);

        Intent intent =
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        );

        intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault()
        );

        intent.putExtra(
                RecognizerIntent.EXTRA_PARTIAL_RESULTS,
                true
        );

        speechRecognizer.setRecognitionListener(
                new RecognitionListener() {

            @Override
            public void onReadyForSpeech(Bundle params) {
                if (callback != null) {
                    callback.onStatus("Listening...");
                }
            }

            @Override
            public void onBeginningOfSpeech() {
                if (callback != null) {
                    callback.onStatus("Speak now...");
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
                if (callback != null) {
                    callback.onError(error);
                }
            }

            @Override
            public void onResults(Bundle results) {

                ArrayList<String> matches =
                        results.getStringArrayList(
                                SpeechRecognizer.RESULTS_RECOGNITION
                        );

                if (matches != null && !matches.isEmpty()) {

                    if (callback != null) {
                        callback.onResult(matches.get(0));
                    }

                } else {

                    if (callback != null) {
                        callback.onError(
                                SpeechRecognizer.ERROR_NO_MATCH
                        );
                    }
                }
            }

            @Override
            public void onPartialResults(
                    Bundle partialResults) {
            }

            @Override
            public void onEvent(
                    int eventType,
                    Bundle params) {
            }
        });

        speechRecognizer.startListening(intent);
    }

    public void stopListening() {

        if (speechRecognizer != null) {
            speechRecognizer.stopListening();
        }
    }

    public void destroy() {

        if (speechRecognizer != null) {
            speechRecognizer.destroy();
            speechRecognizer = null;
        }
    }
}

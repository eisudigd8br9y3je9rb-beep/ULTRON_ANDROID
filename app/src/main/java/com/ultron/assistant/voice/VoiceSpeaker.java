package com.ultron.assistant.voice;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import java.util.Locale;

public class VoiceSpeaker {

    private TextToSpeech textToSpeech;
    private boolean ready = false;

    private final Locale hindiLocale =
            new Locale("hi", "IN");

    private final Locale englishLocale =
            Locale.US;

    public VoiceSpeaker(Context context) {

        textToSpeech = new TextToSpeech(
                context.getApplicationContext(),
                status -> {

                    if (status == TextToSpeech.SUCCESS) {

                        // Slightly slower and deeper voice style
                        textToSpeech.setSpeechRate(0.82f);
                        textToSpeech.setPitch(0.82f);

                        ready = true;

                    } else {

                        ready = false;
                    }
                }
        );
    }

    public void speak(String text) {
        speak(text, null);
    }

    public void speak(String text, Runnable onDone) {

        if (text == null || text.trim().isEmpty()) {
            return;
        }

        if (!ready || textToSpeech == null) {
            return;
        }

        String cleanText = text.trim();

        setBestLanguage(cleanText);

        // Slow, confident ULTRON-style delivery
        textToSpeech.setSpeechRate(0.82f);
        textToSpeech.setPitch(0.82f);

        textToSpeech.setOnUtteranceProgressListener(
                new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {
                    }

                    @Override
                    public void onDone(String utteranceId) {
                        if (onDone != null) {
                            onDone.run();
                        }
                    }

                    @Override
                    public void onError(String utteranceId) {
                        if (onDone != null) {
                            onDone.run();
                        }
                    }
                }
        );

        textToSpeech.speak(
                cleanText,
                TextToSpeech.QUEUE_FLUSH,
                null,
                "ULTRON_SPEECH"
        );
    }

    private void setBestLanguage(String text) {

        if (containsHindi(text)) {

            int result =
                    textToSpeech.setLanguage(
                            hindiLocale
                    );

            if (result ==
                    TextToSpeech.LANG_MISSING_DATA
                    || result ==
                    TextToSpeech.LANG_NOT_SUPPORTED) {

                textToSpeech.setLanguage(
                        englishLocale
                );
            }

        } else {

            textToSpeech.setLanguage(
                    englishLocale
            );
        }
    }

    private boolean containsHindi(String text) {

        for (int i = 0;
             i < text.length();
             i++) {

            char character =
                    text.charAt(i);

            if (character >= '\u0900'
                    && character <= '\u097F') {

                return true;
            }
        }

        return false;
    }

    public void stop() {

        if (textToSpeech != null) {
            textToSpeech.stop();
        }
    }

    public void destroy() {

        if (textToSpeech != null) {

            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
        }

        ready = false;
    }
}

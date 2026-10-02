package com.ultron.assistant.voice;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class VoiceSpeaker {

    private TextToSpeech textToSpeech;
    private boolean ready = false;

    private final Locale hindiLocale =
            new Locale("hi", "IN");

    private final Locale englishLocale =
            Locale.US;

    private long speechCounter = 0;

    public VoiceSpeaker(Context context) {

        textToSpeech = new TextToSpeech(
                context.getApplicationContext(),
                status -> {

                    if (status == TextToSpeech.SUCCESS) {

                        textToSpeech.setSpeechRate(1.0f);
                        textToSpeech.setPitch(1.0f);

                        ready = true;

                    } else {
                        ready = false;
                    }
                },
                "com.brahmadeo.supertonic.tts"
        );
    }

    public void speak(String text) {
        speak(text, null);
    }

    public void speak(String text, Runnable onDone) {

        if (text == null || text.trim().isEmpty()) {
            if (onDone != null) onDone.run();
            return;
        }

        if (!ready || textToSpeech == null) {
            if (onDone != null) onDone.run();
            return;
        }

        final String cleanText = text.trim();
        final String utteranceId =
                "ULTRON_" + (++speechCounter);

        final AtomicBoolean callbackCalled =
                new AtomicBoolean(false);

        setBestLanguage(cleanText);

        textToSpeech.setSpeechRate(1.0f);
        textToSpeech.setPitch(1.0f);

        textToSpeech.setOnUtteranceProgressListener(
                new UtteranceProgressListener() {

                    @Override
                    public void onStart(String id) {
                    }

                    @Override
                    public void onDone(String id) {

                        if (utteranceId.equals(id)
                                && onDone != null
                                && callbackCalled.compareAndSet(
                                        false, true)) {

                            onDone.run();
                        }
                    }

                    @Override
                    public void onError(String id) {

                        if (utteranceId.equals(id)
                                && onDone != null
                                && callbackCalled.compareAndSet(
                                        false, true)) {

                            onDone.run();
                        }
                    }
                }
        );

        textToSpeech.speak(
                cleanText,
                TextToSpeech.QUEUE_FLUSH,
                null,
                utteranceId
        );
    }

    private void setBestLanguage(String text) {
        // Force English locale — eSpeak NG English male voice
        // Hindi text bhi male voice me bolegi
        textToSpeech.setLanguage(englishLocale);
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

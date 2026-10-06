package com.ultron.assistant.voice;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class VoiceSpeaker {

    private TextToSpeech textToSpeech;
    private boolean ready = false;

    private final Locale englishLocale = Locale.US;
    private long speechCounter = 0;

    public VoiceSpeaker(Context context) {
        textToSpeech = new TextToSpeech(
                context.getApplicationContext(),
                status -> {
                    if (status == TextToSpeech.SUCCESS) {
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
            if (onDone != null) onDone.run();
            return;
        }
        if (!ready || textToSpeech == null) {
            if (onDone != null) onDone.run();
            return;
        }

        final String cleanText = text.trim();
        final String utteranceId = "ULTRON_" + (++speechCounter);
        final AtomicBoolean callbackCalled = new AtomicBoolean(false);

        textToSpeech.setSpeechRate(0.75f);
        textToSpeech.setPitch(0.1f);
        textToSpeech.setLanguage(englishLocale);

        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String id) {}

            @Override
            public void onDone(String id) {
                if (utteranceId.equals(id) && onDone != null
                        && callbackCalled.compareAndSet(false, true)) {
                    onDone.run();
                }
            }

            @Override
            public void onError(String id) {
                if (utteranceId.equals(id) && onDone != null
                        && callbackCalled.compareAndSet(false, true)) {
                    onDone.run();
                }
            }
        });

        textToSpeech.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }

    public void stop() {
        if (textToSpeech != null) textToSpeech.stop();
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

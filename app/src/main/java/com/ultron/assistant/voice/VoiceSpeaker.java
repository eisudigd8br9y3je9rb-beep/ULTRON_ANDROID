package com.ultron.assistant.voice;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class VoiceSpeaker {

    private TextToSpeech textToSpeech;
    private boolean ready = false;

    public VoiceSpeaker(Context context) {

        textToSpeech = new TextToSpeech(
                context.getApplicationContext(),
                status -> {

                    if (status == TextToSpeech.SUCCESS) {

                        int result =
                                textToSpeech.setLanguage(
                                        Locale.US
                                );

                        ready =
                                result != TextToSpeech.LANG_MISSING_DATA
                                && result != TextToSpeech.LANG_NOT_SUPPORTED;

                    } else {

                        ready = false;
                    }
                }
        );
    }

    public void speak(String text) {

        if (text == null || text.trim().isEmpty()) {
            return;
        }

        if (!ready || textToSpeech == null) {
            return;
        }

        textToSpeech.speak(
                text,
                TextToSpeech.QUEUE_FLUSH,
                null,
                "ULTRON_SPEECH"
        );
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

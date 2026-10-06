package com.ultron.assistant.voice;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.media.audiofx.EnvironmentalReverb;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import java.io.File;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class VoiceSpeaker {

    private final TextToSpeech textToSpeech;
    private final Context appContext;
    private boolean ready = false;
    private MediaPlayer currentPlayer;
    private EnvironmentalReverb currentReverb;
    private BassBoost currentBass;

    private final Locale englishLocale = Locale.US;
    private long speechCounter = 0;

    public VoiceSpeaker(Context context) {
        appContext = context.getApplicationContext();
        textToSpeech = new TextToSpeech(appContext, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setSpeechRate(0.85f);
                textToSpeech.setPitch(0.55f);
                textToSpeech.setLanguage(englishLocale);
                ready = true;
            } else {
                ready = false;
            }
        });
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

        textToSpeech.setSpeechRate(0.85f);
        textToSpeech.setPitch(0.55f);
        textToSpeech.setLanguage(englishLocale);

        File ttsDir = new File(appContext.getCacheDir(), "ultron_tts");
        if (!ttsDir.exists()) ttsDir.mkdirs();
        final File wavFile = new File(ttsDir, utteranceId + ".wav");

        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String id) {}

            @Override
            public void onDone(String id) {
                if (utteranceId.equals(id)) {
                    playWithEffects(wavFile, onDone, callbackCalled);
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

        textToSpeech.synthesizeToFile(cleanText, null, wavFile, utteranceId);
    }

    private void playWithEffects(File wavFile, Runnable onDone, AtomicBoolean callbackCalled) {
        try {
            stopCurrentPlayer();

            MediaPlayer player = new MediaPlayer();
            player.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANT)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build());
            player.setDataSource(wavFile.getAbsolutePath());
            player.prepare();
            player.setVolume(1.0f, 1.0f);

            int sessionId = player.getAudioSessionId();

            // Ultron-style reverb (room echo)
            EnvironmentalReverb reverb = new EnvironmentalReverb(0, sessionId);
            reverb.setDecayTime(2500);
            reverb.setDensity(1000);
            reverb.setDiffusion(1000);
            reverb.setReverbLevel(-500);
            reverb.setRoomLevel(-1500);
            reverb.setEnabled(true);

            // Bass boost for deep voice
            BassBoost bass = new BassBoost(0, sessionId);
            if (bass.getStrengthSupported()) {
                bass.setStrength((short) 800);
            }
            bass.setEnabled(true);

            currentPlayer = player;
            currentReverb = reverb;
            currentBass = bass;

            player.setOnCompletionListener(mp -> {
                cleanupEffects();
                try { mp.release(); } catch (Exception ignored) {}
                if (onDone != null && callbackCalled.compareAndSet(false, true)) {
                    onDone.run();
                }
            });

            player.setOnErrorListener((mp, what, extra) -> {
                cleanupEffects();
                try { mp.release(); } catch (Exception ignored) {}
                if (onDone != null && callbackCalled.compareAndSet(false, true)) {
                    onDone.run();
                }
                return true;
            });

            player.start();

        } catch (Exception e) {
            e.printStackTrace();
            if (onDone != null && callbackCalled.compareAndSet(false, true)) {
                onDone.run();
            }
        }
    }

    private void stopCurrentPlayer() {
        if (currentPlayer != null) {
            try {
                if (currentPlayer.isPlaying()) currentPlayer.stop();
                currentPlayer.release();
            } catch (Exception ignored) {}
            currentPlayer = null;
        }
        cleanupEffects();
    }

    private void cleanupEffects() {
        if (currentReverb != null) {
            try { currentReverb.setEnabled(false); currentReverb.release(); } catch (Exception ignored) {}
            currentReverb = null;
        }
        if (currentBass != null) {
            try { currentBass.setEnabled(false); currentBass.release(); } catch (Exception ignored) {}
            currentBass = null;
        }
    }

    public void stop() {
        if (textToSpeech != null) textToSpeech.stop();
        stopCurrentPlayer();
    }

    public void destroy() {
        stop();
        if (textToSpeech != null) {
            textToSpeech.shutdown();
        }
        ready = false;
    }
}

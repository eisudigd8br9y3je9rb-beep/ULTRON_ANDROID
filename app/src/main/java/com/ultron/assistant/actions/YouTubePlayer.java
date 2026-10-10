package com.ultron.assistant.actions;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;

import com.ultron.assistant.accessibility.UltronAccessibilityService;

public class YouTubePlayer {

    public static void searchAndPlay(Context ctx, String songName) {
        try {
            Intent intent = new Intent(Intent.ACTION_SEARCH);
            intent.setPackage("com.google.android.youtube");
            intent.putExtra("query", songName);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(intent);
        } catch (Exception e) {
            try {
                Intent fallback = new Intent(Intent.ACTION_VIEW);
                fallback.setData(Uri.parse("https://www.youtube.com/results?search_query=" + Uri.encode(songName)));
                fallback.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(fallback);
            } catch (Exception ignored) {}
        }

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    UltronAccessibilityService.clickFirstYouTubeResult();
                } catch (Exception ignored) {}
            }
        }, 3500);
    }

    public static void playPause(Context ctx) {
        sendMediaKey(ctx, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
    }

    public static void next(Context ctx) {
        sendMediaKey(ctx, KeyEvent.KEYCODE_MEDIA_NEXT);
    }

    public static void previous(Context ctx) {
        sendMediaKey(ctx, KeyEvent.KEYCODE_MEDIA_PREVIOUS);
    }

    private static void sendMediaKey(Context ctx, int keyCode) {
        AudioManager am = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
        if (am == null) return;
        long now = System.currentTimeMillis();
        am.dispatchMediaKeyEvent(new KeyEvent(now, now, KeyEvent.ACTION_DOWN, keyCode, 0));
        am.dispatchMediaKeyEvent(new KeyEvent(now, now, KeyEvent.ACTION_UP, keyCode, 0));
    }
}

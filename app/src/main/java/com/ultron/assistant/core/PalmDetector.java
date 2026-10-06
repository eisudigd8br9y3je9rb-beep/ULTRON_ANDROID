package com.ultron.assistant.core;

import android.graphics.Bitmap;
import android.graphics.Color;

public class PalmDetector {

    public interface PalmListener {
        void onDoublePalmDetected();
    }

    private static final long DOUBLE_PALM_WINDOW_MS = 1500;
    private static final float DARK_THRESHOLD = 0.35f;
    private static final float BRIGHT_THRESHOLD = 0.50f;
    private static final long MIN_COOLDOWN_MS = 300;

    private final PalmListener listener;
    private long firstPalmAt = 0;
    private boolean waitingForRelease = false;
    private long lastPalmAt = 0;

    public PalmDetector(PalmListener listener) {
        this.listener = listener;
    }

    public void analyzeFrame(Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled()) return;

        float brightness = averageBrightness(bitmap);
        long now = System.currentTimeMillis();

        if (now - lastPalmAt < MIN_COOLDOWN_MS) return;

        if (waitingForRelease) {
            if (brightness > BRIGHT_THRESHOLD) {
                waitingForRelease = false;
            }
            return;
        }

        if (brightness < DARK_THRESHOLD) {
            lastPalmAt = now;
            waitingForRelease = true;

            if (firstPalmAt == 0) {
                firstPalmAt = now;
            } else if (now - firstPalmAt <= DOUBLE_PALM_WINDOW_MS) {
                firstPalmAt = 0;
                if (listener != null) {
                    listener.onDoublePalmDetected();
                }
            } else {
                firstPalmAt = now;
            }
        }
    }

    private float averageBrightness(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int stepX = Math.max(1, w / 20);
        int stepY = Math.max(1, h / 20);
        long sum = 0;
        int count = 0;

        for (int y = 0; y < h; y += stepY) {
            for (int x = 0; x < w; x += stepX) {
                int pixel = bitmap.getPixel(x, y);
                int r = Color.red(pixel);
                int g = Color.green(pixel);
                int b = Color.blue(pixel);
                int lum = (r * 30 + g * 59 + b * 11) / 100;
                sum += lum;
                count++;
            }
        }

        if (count == 0) return 1.0f;
        return (sum / (float) count) / 255f;
    }

    public void reset() {
        firstPalmAt = 0;
        waitingForRelease = false;
        lastPalmAt = 0;
    }
}

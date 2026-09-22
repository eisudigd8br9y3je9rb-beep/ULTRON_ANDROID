package com.ultron.assistant.vision;

import android.graphics.Bitmap;

public class VisionManager {

    public interface VisionCallback {
        void onImageReady(Bitmap bitmap);
        void onError(String error);
    }

    private Bitmap lastFrame;
    private VisionCallback callback;

    public void setCallback(VisionCallback callback) {
        this.callback = callback;
    }

    public synchronized void setFrame(Bitmap bitmap) {

        if (bitmap == null || bitmap.isRecycled()) {
            if (callback != null) {
                callback.onError("Invalid camera frame.");
            }
            return;
        }

        Bitmap oldFrame = lastFrame;
        lastFrame = bitmap;

        if (oldFrame != null
                && !oldFrame.isRecycled()
                && oldFrame != bitmap) {
            oldFrame.recycle();
        }

        if (callback != null) {
            callback.onImageReady(bitmap);
        }
    }

    public synchronized Bitmap getLastFrame() {
        return lastFrame;
    }

    public synchronized boolean hasFrame() {
        return lastFrame != null
                && !lastFrame.isRecycled();
    }

    public synchronized void clear() {

        if (lastFrame != null
                && !lastFrame.isRecycled()) {
            lastFrame.recycle();
        }

        lastFrame = null;
    }
}

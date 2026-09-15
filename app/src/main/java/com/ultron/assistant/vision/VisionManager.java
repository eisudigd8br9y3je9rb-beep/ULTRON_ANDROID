package com.ultron.assistant.vision;

import android.graphics.Bitmap;

public class VisionManager {

    public interface VisionCallback {
        void onImageReady(Bitmap bitmap);
        void onError(String error);
    }

    private Bitmap lastFrame;

    public void setFrame(Bitmap bitmap) {

        if (lastFrame != null
                && !lastFrame.isRecycled()) {
            lastFrame.recycle();
        }

        lastFrame = bitmap;
    }

    public Bitmap getLastFrame() {
        return lastFrame;
    }

    public boolean hasFrame() {
        return lastFrame != null
                && !lastFrame.isRecycled();
    }

    public void clear() {

        if (lastFrame != null
                && !lastFrame.isRecycled()) {
            lastFrame.recycle();
        }

        lastFrame = null;
    }
}

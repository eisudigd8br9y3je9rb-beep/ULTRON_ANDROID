package com.ultron.assistant.vision;

import android.graphics.Bitmap;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.objects.DetectedObject;
import com.google.mlkit.vision.objects.ObjectDetection;
import com.google.mlkit.vision.objects.ObjectDetector;
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions;

import java.util.List;

public class VisionManager {

    public interface VisionCallback {
        void onImageReady(Bitmap bitmap);
        void onError(String error);
        default void onObjectsDetected(List<DetectedObject> objects) {}
    }

    private Bitmap lastFrame;
    private VisionCallback callback;

    private final ObjectDetector detector;

    public VisionManager() {
        ObjectDetectorOptions options =
                new ObjectDetectorOptions.Builder()
                        .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
                        .enableMultipleObjects()
                        .enableClassification()
                        .build();

        detector = ObjectDetection.getClient(options);
    }

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

        analyzeFrame(bitmap);
    }

    private void analyzeFrame(Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled()) {
            return;
        }

        InputImage image = InputImage.fromBitmap(bitmap, 0);

        detector.process(image)
                .addOnSuccessListener(objects -> {
                    if (callback != null) {
                        callback.onObjectsDetected(objects);
                    }
                })
                .addOnFailureListener(error -> {
                    if (callback != null) {
                        callback.onError(
                                "Vision detection error: "
                                        + error.getMessage());
                    }
                });
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

    public void close() {
        detector.close();
        clear();
    }
}
